package com.my.azusato.dbunit;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.dbunit.IDatabaseTester;
import org.dbunit.assertion.DbUnitAssert;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DBUnitComponent  {

	@Autowired
	IDatabaseTester databaseTester;

	DbUnitAssert asserter = new DbUnitAssert();

	public void initalizeTable(String dataSourceUrl) throws Exception {
		initalizeTable(Paths.get(dataSourceUrl));
	}

	public void initalizeTable(Path dataSourcePath) throws Exception {
		FlatXmlDataSet dataSet = getFlatXmlDataSet(dataSourcePath);
		databaseTester.setDataSet(createReplacementDataSet(dataSet));
		databaseTester.onTearDown();
		databaseTester.onSetup();
	}

	public void compareTable(Path dataSourcePath, String tableName) throws Exception {
		FlatXmlDataSet dataSet = getFlatXmlDataSet(dataSourcePath);
		compare(tableName, createReplacementDataSet(dataSet));
	}

	public void compareTable(Path dataSourcePath, String tableName, String[] excludedColumns) throws Exception {
		FlatXmlDataSet dataSet = getFlatXmlDataSet(dataSourcePath);
		compare(tableName, createReplacementDataSet(dataSet), excludedColumns);
	}

	/**
	 * compare all tables in dataSourcePath.
	 * 
	 * @param dataSourcePath
	 * @throws Exception
	 */
	public void compareTable(Path dataSourcePath) throws Exception {
		FlatXmlDataSet dataSet = getFlatXmlDataSet(dataSourcePath);
		String[] tables = dataSet.getTableNames();
		for (String table : tables) {
			compare(table, createReplacementDataSet(dataSet));
		}
	}

	private void compare(String tableName, IDataSet dataSet) throws Exception {
		ITable expect = dataSet.getTable(tableName);
		ITable result = databaseTester.getConnection().createDataSet().getTable(tableName);
		asserter.assertEquals(expect, result);
	}

	private void compare(String tableName, IDataSet dataSet, String[] excludedColumns) throws Exception {
		ITable expectNotFiltered = dataSet.getTable(tableName);
		ITable expect = DefaultColumnFilter.excludedColumnsTable(expectNotFiltered, excludedColumns);
		ITable resultNotFiltered = databaseTester.getConnection().createDataSet().getTable(tableName);
		ITable result = DefaultColumnFilter.excludedColumnsTable(resultNotFiltered, excludedColumns);
		asserter.assertEquals(expect, result);
	}

	public void deleteTable() throws Exception {
		databaseTester.onTearDown();
	}
	
	private FlatXmlDataSet getFlatXmlDataSet(Path dataSourcePath) throws Exception {
		return new FlatXmlDataSetBuilder().setColumnSensing(true).build(dataSourcePath.toFile());
	}
	
	
	/**
	 * DBUnitでnullを使えるようにする。DBUnitではnullは省略しないと使えない。結果を比較する時、nullのフィールドは比較できないため、比較できるように"ReplacementDataSet"を使う。
	 * フィールドの値が[null］の場合nullにする。
	 * <a href="https://www.petrikainulainen.net/programming/spring-framework/spring-from-the-trenches-using-null-values-in-dbunit-datasets/">参考サイト</a>
	 * @param dataset xmlDataset
	 * @return dataset {@code ->} ReplacementDataSet
	 * 
	 */
	private ReplacementDataSet createReplacementDataSet(FlatXmlDataSet dataset) {
		ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataset);
		
		 //Configure the replacement dataset to replace '[null]' strings with null.
        replacementDataSet.addReplacementObject("[null]", null);
         
        return replacementDataSet;
	}
}
