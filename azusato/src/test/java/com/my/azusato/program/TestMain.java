package com.my.azusato.program;

import java.io.File;

import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.my.azusato.integration.AbstractIntegration;

import lombok.extern.slf4j.Slf4j;

/**
 * DBUnitテストのmainを持つクラス。
 * 
 * @author tarosa0001
 */
@Slf4j
public class TestMain extends AbstractIntegration {
	
	protected DataSourceDatabaseTester dataSourceDatabaseTester;

	@Autowired
	protected DataSource dataSource;
	
	@Test
	public void test() throws Exception {
		log.info("処理開始");

		// ---------------------------------
		// DBを更新する
		// ---------------------------------
		dataSourceDatabaseTester = new DataSourceDatabaseTester(dataSource);
		// --------------------------------------
		// テストデータ投入
		// --------------------------------------
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(new File("src\\test\\data\\entity\\data.xml"));
		dataSourceDatabaseTester.setDataSet(dataSet);
		// DELETE→INSERTで事前準備データを用意する
		dataSourceDatabaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
		dataSourceDatabaseTester.onSetup();

		log.info("前処理終了");
	}
}
