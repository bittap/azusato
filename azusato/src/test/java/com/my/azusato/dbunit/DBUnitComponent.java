package com.my.azusato.dbunit;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.dbunit.IDatabaseTester;
import org.dbunit.assertion.DbUnitAssert;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.transaction.TestTransaction;

@Component
public class DBUnitComponent {

	@Autowired
	IDatabaseTester databaseTester;

	DbUnitAssert asserter = new DbUnitAssert();

	public void initalizeTable(String dataSourceUrl) throws Exception {
		initalizeTable(Paths.get(dataSourceUrl));
	}

	public void initalizeTable(Path dataSourcePath) throws Exception {
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(dataSourcePath.toFile());
		databaseTester.setDataSet(dataSet);
		databaseTester.onTearDown();
		databaseTester.onSetup();
	}

	public void compareTable(Path dataSourcePath, String tableName) throws Exception {
		TestTransaction.end();
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(dataSourcePath.toFile());
		compare(tableName, dataSet);
		TestTransaction.start();
	}

	public void compareTable(Path dataSourcePath, String tableName, String[] excludedColumns) throws Exception {
		TestTransaction.end();
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(dataSourcePath.toFile());
		compare(tableName, dataSet, excludedColumns);
		TestTransaction.start();
	}

	/**
	 * compare all tables in dataSourcePath.
	 * 
	 * @param dataSourcePath
	 * @throws Exception
	 */
	public void compareTable(Path dataSourcePath) throws Exception {
		//TestTransaction.end();
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(dataSourcePath.toFile());
		String[] tables = dataSet.getTableNames();
		for (String table : tables) {
			compare(table, dataSet);
		}
		//TestTransaction.start();
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
}
