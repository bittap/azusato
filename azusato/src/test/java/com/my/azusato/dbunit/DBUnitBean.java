package com.my.azusato.dbunit;

import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class DBUnitBean {
	
	@Autowired
	protected DataSource dataSource;

	@Bean
	public IDatabaseTester getIdateBaseTester() throws Exception {
		DataSourceDatabaseTester mysqlTester = new DataSourceDatabaseTester(dataSource);
		mysqlTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
		mysqlTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
		return mysqlTester;
	}
}
