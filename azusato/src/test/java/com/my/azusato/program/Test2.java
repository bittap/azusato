package com.my.azusato.program;

import java.io.File;

import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.my.azusato.common.TestConstant;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@ActiveProfiles(TestConstant.PROFILES)
@Slf4j
public class Test2  {

	@Autowired
	IDatabaseTester databaseTester;
	
	@Test
	public void test() throws Exception {
		 log.info("処理開始");
		 // --------------------------------------
	    // テストデータ投入
	    // --------------------------------------
	    IDataSet dataSet = new FlatXmlDataSetBuilder().build(new File("src\\test\\data\\entity\\data.xml"));
	    databaseTester.setDataSet(dataSet);
	    // DELETE→INSERTで事前準備データを用意する
	    databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
	        databaseTester.onSetup();

	    log.info("前処理終了");
	}

}
