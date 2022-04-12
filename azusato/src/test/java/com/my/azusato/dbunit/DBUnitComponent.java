package com.my.azusato.dbunit;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DBUnitComponent {
	
	@Autowired
	IDatabaseTester databaseTester;

	public void initalizeTable(String dataSourceUrl) throws Exception {
		initalizeTable(Paths.get(dataSourceUrl));
	}
	
	public void initalizeTable(Path dataSourcePath) throws Exception {
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(dataSourcePath.toFile());
		databaseTester.setDataSet(dataSet);
		databaseTester.onSetup();
	}
	
	public void deleteTable() throws Exception {
		databaseTester.onTearDown();
	}
}
