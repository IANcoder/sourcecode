package com.cloudbeaver.checkAndAppend;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.cloudbeaver.client.common.BeaverFatalException;
import com.cloudbeaver.client.common.BeaverUtils;
import com.cloudbeaver.mockServer.MockWebServer;

public class CheckAndAppendDataTest{
	private static Logger logger = Logger.getLogger(CheckAndAppendDataTest.class);
	public static final String DATABASE_FILE_PREFIX = "/tmp/db/";
	public static Map<String, String> DB2TypeMap = new HashMap<String, String>();
	public static Map<String, Long> DBResultFileMap = new HashMap<String, Long>();
	public static Map<String, String> DBFileMap = new HashMap<String, String>();
	public static Map<String, Integer> AppendCountMap = new HashMap<String, Integer>();

	public static String CONF_FILENAME = "conf_test/CheckAndAppendConf/CheckAndAppendTestConf.xml";
	public static ApplicationContext appContext = new FileSystemXmlApplicationContext(CONF_FILENAME);
	public static CheckAndAppendTestConf checkAndAppendBean = appContext.getBean("checkAndAppendTestConf", CheckAndAppendTestConf.class);

	private static MockWebServer mockServer = new MockWebServer();
	private static DBDataGeneration dbDataGeneration = new DBDataGeneration();
	private static CheckResults checkResults = new CheckResults();
	
	private static int MAX_LOOP_NUM = 0;

	@BeforeClass
//	@Before
//	@Ignore
	public static void setUpServers() throws ParseException{
		mockServer.start(false);
		initMap();
//		for(DatabaseBeanTestConf dbBean : checkAndAppendBean.getDatabases()){
//			if(dbBean.isDoesAppend()){
//				AppendCountMap.put(dbBean.getDatabaseName(), dbBean.getAppendCount());
//				MAX_LOOP_NUM = MAX_LOOP_NUM > dbBean.getAppendCount() ? MAX_LOOP_NUM : dbBean.getAppendCount();
//			} else {
//				AppendCountMap.put(dbBean.getDatabaseName(), 0);
//			}
//		}
//		dbDataGeneration.DBInit(checkAndAppendBean);
	}

	@AfterClass
	public static void tearDownServers(){
		mockServer.stop();
//		dbDataGeneration.DBClear(checkAndAppendBean);
//		dbDataGeneration.deleteLocalFile(checkAndAppendBean);
//		dbDataGeneration.deleteDBFile(checkAndAppendBean);
	}

	public static void initMap(){
		DB2TypeMap.put("DocumentDB", "sqlserver");
		DB2TypeMap.put("MeetingDB", "webservice");
		DB2TypeMap.put("TalkDB", "webservice");
		DB2TypeMap.put("PrasDB", "webservice");
		DB2TypeMap.put("JfkhDB", "oracle");
		DB2TypeMap.put("DocumentDBForSqlite", "sqlite");
		DB2TypeMap.put("DocumentFiles", "smallFiles");
		DB2TypeMap.put("VideoMeetingDB", "sqlserver");
		DB2TypeMap.put("HelpDB", "sqlserver");
		DB2TypeMap.put("MysqlTest", "mysql");
		DB2TypeMap.put("XfzxDB", "oracle");
		DBDataGeneration.TestFileMap.put("MysqlTest", "conf_test/DBSetup/MysqlTest/");
		DBDataGeneration.TestFileMap.put("SqlServerTest", "conf_test/DBSetup/SqlServerTest/");		
		DBDataGeneration.TestFileMap.put("OracleTest", "conf_test/DBSetup/OracleTest/");
		DBDataGeneration.TestFileMap.put("PostgresqlTest", "conf_test/DBSetup/PostgresqlTest/");
		DBFileMap.put("MysqlTest", DATABASE_FILE_PREFIX + "MysqlTest/");
		DBFileMap.put("SqlServerTest", DATABASE_FILE_PREFIX + "SqlServerTest/");
		DBFileMap.put("OracleTest", DATABASE_FILE_PREFIX + "OracleTest/");
		DBFileMap.put("PostgresqlTest", DATABASE_FILE_PREFIX + "PostgresqlTest/");
	}

	public static boolean traverseFolder(String folderPath){
		logger.info("-------------------------start traverse folder-------------------------------");
		boolean b = true;
		File file = new File(folderPath);
        if (file.exists()) {
        	File[] files = file.listFiles();
        	for (File file2 : files) {
        		if(DBResultFileMap.isEmpty()){
        			b = false;
        			DBResultFileMap.put(file2.getAbsolutePath(), file2.lastModified());
        		} else {
        			if(DBResultFileMap.containsKey(file2.getAbsolutePath())){
        				if(DBResultFileMap.get(file2.getAbsolutePath()) != file2.lastModified()){
        					b = false;
        					DBResultFileMap.put(file2.getAbsolutePath(), file2.lastModified());
        				}
        			} else {
        				b = false;
        				DBResultFileMap.put(file2.getAbsolutePath(), file2.lastModified());
        			}
        		}
        	}
        }
        return b;
	}

	@Test
	public void testGetMsg() throws SQLException, BeaverFatalException, ParseException{
		for(int i = 0; i <= MAX_LOOP_NUM; i++){
			Map<String, Boolean> doesCheckMap = new HashMap<String, Boolean>();
			for(DatabaseBeanTestConf dBean : checkAndAppendBean.getDatabases()){
				doesCheckMap.put(dBean.getDatabaseName(), false);
			}
			while(true){
				for(DatabaseBeanTestConf dBean : checkAndAppendBean.getDatabases()){
					if(traverseFolder(DBFileMap.get(dBean.getDatabaseName())) && !doesCheckMap.get(dBean.getDatabaseName())){
						logger.info("-------------------------start check " + dBean.getDatabaseName() + " " + (i+1) + "-------------------------------");
						checkResults.checkDBContentForTest(checkAndAppendBean.isNewVersion(), dBean);//数据检验
						doesCheckMap.put(dBean.getDatabaseName(), true);
					}
				}
				boolean b = true;
				for (Map.Entry<String, Boolean> entry : doesCheckMap.entrySet()){
					if(!entry.getValue()){
						b = false;
						BeaverUtils.sleep(60*1000);//需根据实际情况修改检查目录的间隔时间
						break;
					}
				}
				if(b){
					break;
				}
			}
//			for (int j = 0; j < checkAndAppendBean.getDatabases().size(); j++) {
//				if(checkAndAppendBean.getDatabases().get(j).isDoesAppend() && checkAndAppendBean.getDatabases().get(j).getAppendCount() > i){
//					logger.info("-------------------------start append " + checkAndAppendBean.getDatabases().get(j).getDatabaseName() + " " + (i+1) + "-------------------------------");
//					dbDataGeneration.appendDataToDB(checkAndAppendBean.getDatabases().get(j), j, checkAndAppendBean);
//					logger.info("-------------------------start append " + checkAndAppendBean.getDatabases().get(j).getDatabaseName() + " " + (i+1) + "-------------------------------");
//				}
//			}
//			BeaverUtils.sleep(30*1000);
		}
	}

	public static void main(String[] args) {
		CheckAndAppendDataTest cTest = new CheckAndAppendDataTest();
		cTest.start();
	}

	public void start() {
		try {
			setUpServers();//启动server和初始化数据库
			logger.info("---------------------------DB Init Complete!-----------------------------");
			BeaverUtils.sleep(60*1000);
			testGetMsg();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tearDownServers();
		}		
	}
}
