package com.cloudbeaver.checkAndAppend;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import com.cloudbeaver.client.common.BeaverFatalException;
import com.cloudbeaver.client.common.BeaverUtils;
import com.cloudbeaver.mockServer.MockWebServer;
public class CheckAndAppendDataTest{
	private Logger logger = LogManager.getLogger(CheckAndAppendDataTest.class.getName());
	public static String DATABASE_FILE_PREFIX = "/tmp/db/";
	public Map<String, String> DB2TypeMap = new HashMap<String, String>();
	public Map<String, Long> DBResultFileMap = new HashMap<String, Long>();
	public Map<String, String> DBFileMap = new HashMap<String, String>();
	public Map<String, Integer> AppendCountMap = new HashMap<String, Integer>();

	public static String CONF_FILENAME = "conf_test/CheckAndAppendConf/CheckAndAppendTestConf.xml";
	public ApplicationContext appContext = new FileSystemXmlApplicationContext(CONF_FILENAME);
	public CheckAndAppendTestConf checkAndAppendBean = appContext.getBean("checkAndAppendTestConf", CheckAndAppendTestConf.class);

	private MockWebServer mockServer = new MockWebServer();
	private DBDataGeneration dbDataGeneration = new DBDataGeneration();
	private CheckResults checkResults = new CheckResults();
	private Process p;
	private static int MAX_LOOP_NUM = 0;
	private String path;
	private String []cmd;
	@BeforeClass
//	@Before
//	@Ignore
	public void setUpServers() throws ParseException{
		logger.info("i'm here");
		System.out.println("clear old data");
		dbDataGeneration.DBClear(checkAndAppendBean);
		dbDataGeneration.deleteLocalFile(checkAndAppendBean);
		dbDataGeneration.deleteDBFile(checkAndAppendBean);
		System.out.println("clear success!");

		mockServer.start(false);
		initMap();
		for(DatabaseBeanTestConf dbBean : checkAndAppendBean.getDatabases()){
			if(dbBean.isDoesAppend()){
				AppendCountMap.put(dbBean.getDatabaseName(), dbBean.getAppendCount());
				MAX_LOOP_NUM = MAX_LOOP_NUM > dbBean.getAppendCount() ? MAX_LOOP_NUM : dbBean.getAppendCount();
			} else {
				AppendCountMap.put(dbBean.getDatabaseName(), 0);
			}
		}
		System.out.println("into dbinit");
		dbDataGeneration.DBInit(checkAndAppendBean);
		System.out.println("into external process");
		cmd=checkAndAppendBean.getCmd().split(" ");
		ProcessBuilder pb=new ProcessBuilder(cmd);
		path=checkAndAppendBean.getPath();
		pb.directory(new File(path));
		File log = new File(path+"/client.log");
		pb.redirectErrorStream(true);
		pb.redirectOutput(Redirect.appendTo(log));
		try {
		    p = pb.start();
			assert pb.redirectInput() == Redirect.PIPE;
			assert pb.redirectOutput().file() == log;
			assert p.getInputStream().read() == -1;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@AfterClass
	public void tearDownServers(){
		mockServer.stop();
		dbDataGeneration.DBClear(checkAndAppendBean);
		dbDataGeneration.deleteLocalFile(checkAndAppendBean);
		dbDataGeneration.deleteDBFile(checkAndAppendBean);
	}

	public void initMap(){
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

	public boolean traverseFolder(String folderPath){
		logger.info("-------------------------start traverse folder-------------------------------");
		boolean b = true;
		File file = new File(folderPath);
        if (file.exists()) {
        	File[] files = file.listFiles();
        	for (File file2 : files) {
        		if(!(DBResultFileMap.containsKey(file2.getAbsolutePath())&&DBResultFileMap.get(file2.getAbsolutePath())== file2.lastModified()))
        		{
        			b = false;
        			DBResultFileMap.put(file2.getAbsolutePath(), file2.lastModified());
        		}
        	}
        }else b=false;
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
			for (int j = 0; j < checkAndAppendBean.getDatabases().size(); j++) {
				if(checkAndAppendBean.getDatabases().get(j).isDoesAppend() && checkAndAppendBean.getDatabases().get(j).getAppendCount() > i){
					logger.info("-------------------------start append " + checkAndAppendBean.getDatabases().get(j).getDatabaseName() + " " + (i+1) + "-------------------------------");
					dbDataGeneration.appendDataToDB(checkAndAppendBean.getDatabases().get(j), j, checkAndAppendBean);
					logger.info("-------------------------start append " + checkAndAppendBean.getDatabases().get(j).getDatabaseName() + " " + (i+1) + "-------------------------------");
				}
			}
			BeaverUtils.sleep(30*1000);
		}
	}

	public static void main(String[] args) {
		String path="/home/fanyan/dbsync-test/conf_test/DBSetup/SqlServerTest";
	//	CheckAndAppendDataTest cTest = new CheckAndAppendDataTest();
	//	cTest.start();
	}

	public void start() {
	//	tearDownServers();
		try {
			setUpServers();//启动server和初始化数据库
			logger.info("---------------------------DB Init Complete!-----------------------------");
			BeaverUtils.sleep(60*1000);
			testGetMsg();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			p.destroyForcibly();
			System.out.println("external process shut down");
			tearDownServers();
		}		
	}
}
