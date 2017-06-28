package com.cloudbeaver.mockServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.cloudbeaver.checkAndAppend.CheckAndAppendDataTest;
import com.cloudbeaver.checkAndAppend.CheckAndAppendTestConf;
import com.cloudbeaver.checkAndAppend.DatabaseBeanTestConf;
import com.cloudbeaver.checkAndAppend.TableBeanTestConf;
import com.cloudbeaver.client.common.BeaverUtils;
import com.cloudbeaver.client.dbbean.DatabaseBean;
import com.cloudbeaver.client.dbbean.MultiDatabaseBean;
import com.cloudbeaver.client.dbbean.TableBean;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.apache.commons.codec.binary.Base64;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebServlet("/")
public class PostDataServlet extends HttpServlet{
	private static Logger logger = Logger.getLogger(PostDataServlet.class);

	private static final String FLUME_HTTP_REQ_PREFIX = "[{ \"headers\" : {}, \"body\" : \"";
	public static final String DEFAULT_CHARSET = "utf-8";
	public static final String FILE_SAVE_DIR = "/home/beaver/Documents/test/result/";
	public static final boolean NEED_SAVE_FILE = false;
	public static final String DATABASE_FILE_PREFIX = CheckAndAppendDataTest.DATABASE_FILE_PREFIX;
	public static final int WRITE_BUFFER_SIZE = 1024;
	public static final String VERSION_COLUMN_OFFSET = "VERSION_COLUMN_OFFSET";
	public static final String DATABASE_NAME = "hdfs_db";
	public static final String TABLE_NAME = "hdfs_table";
	private static int picNum = 0;
	public static String CONF_FILENAME = "conf_test/CheckAndAppendConf/CheckAndAppendTestConf.xml";
	public static ApplicationContext appContext = new FileSystemXmlApplicationContext(CONF_FILENAME);
	public static CheckAndAppendTestConf checkAndAppendBean = appContext.getBean("checkAndAppendTestConf", CheckAndAppendTestConf.class);
	public static Map<String, String> TableProperty = new HashMap<String, String>();

	public static Map<String, String> DBName2DBType = new HashMap<String, String>();
	{
		DBName2DBType.put("DocumentDB", "sqlserver");
		DBName2DBType.put("MeetingDB", "webservice");
		DBName2DBType.put("TalkDB", "webservice");
		DBName2DBType.put("PrasDB", "webservice");
		DBName2DBType.put("JfkhDB", "oracle");
		DBName2DBType.put("DocumentDBForSqlite", "sqlite");
		DBName2DBType.put("DocumentFiles", "file");
		DBName2DBType.put("VideoMeetingDB", "sqlserver");
		DBName2DBType.put("HelpDB", "sqlserver");
		TableProperty.put("OracleTest_course", "COURSENAME");
		TableProperty.put("OracleTest_teacher", "NAME");
		TableProperty.put("SqlServerTest_course", "courseName");
		TableProperty.put("SqlServerTest_teacher", "name");
		TableProperty.put("MysqlTest_course", "courseName");
		TableProperty.put("MysqlTest_teacher", "name");
		TableProperty.put("PostgresqlTest_course", "coursename");
		TableProperty.put("PostgresqlTest_teacher", "name");
		
	}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
    	BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
    	StringBuilder sb = new StringBuilder();
    	String tmp;
    	while ((tmp = br.readLine()) != null) {
			sb.append(tmp);
		}

    	if(checkAndAppendBean.isNewVersion()){
    		if(sb.toString().contains(BeaverUtils.IP_ADDRESS)){
        		System.out.println("heart beat:" + sb.toString());
        		return;
        	}
        	byte[] bs = Base64.decodeBase64(sb.toString().getBytes(DEFAULT_CHARSET));
        	String content = new String(bs,DEFAULT_CHARSET);
        	System.out.println("get post data, data:" + content);
        	JSONArray jArray = JSONArray.fromObject(content);
        	String databaseName = jArray.getJSONObject(0).getString(DATABASE_NAME);
        	String tableName = jArray.getJSONObject(0).getString(TABLE_NAME);
        	String versionColumn = "";
        	for(DatabaseBeanTestConf dbBean : checkAndAppendBean.getDatabases()){
        		if(dbBean.getDatabaseName().equals(databaseName)){
        			for(TableBeanTestConf tBean : dbBean.getTables()){
        				if(tBean.getTableName().equals(tableName)){
        					versionColumn = tBean.getVersionColumn();
        				}
        			}
        		}
        	}

        	//write data to local
        	String fileName = DATABASE_FILE_PREFIX  + databaseName + "/" + databaseName + "_" + tableName;
        	RandomAccessFile file = new RandomAccessFile(fileName, "rw");
    		if(file.length() > 0 && versionColumn.equals("TOTAL_LINES_NUMBER")){
    			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
    			String tempString = reader.readLine();
	            reader.close();
	            JSONArray jArray2 = JSONArray.fromObject("[" + tempString + "]");
	            if(jArray2.getJSONObject(0).getString(TableProperty.get(databaseName + "_" + tableName)).equals(jArray.getJSONObject(0).getString(TableProperty.get(databaseName + "_" + tableName)))){
	            	File f = new File(fileName);
	            	f.delete();
	            	file = new RandomAccessFile(fileName, "rw");
	            	file.seek(0);
	            } else {
	            	file.seek(file.length());
	            }
    		}else {
    			file.seek(file.length());
    		}
    		for(int i = 0; i < jArray.size(); i++){
    			file.write((jArray.get(i) + "\n").getBytes());
    		}
    		file.close();

//        	checkVersionColumnOffset(databaseName, tableName, jArray);
    	} else {
    		String content = null;
        	if(sb.indexOf("headers") != -1 && sb.indexOf("body") != -1){
        		String base64code = sb.substring(sb.indexOf(FLUME_HTTP_REQ_PREFIX)+FLUME_HTTP_REQ_PREFIX.length(), sb.indexOf("\" }]"));
        		byte []bs = BeaverUtils.decompress(base64code.getBytes(DEFAULT_CHARSET));
        		content = new String(bs,DEFAULT_CHARSET);
        	} else {
        		content = sb.toString();
        	}
    		System.out.println("content = " + content);
    		
    		String dbName = null;
    		String tName = null;
    		JSONArray newjArray = JSONArray.fromObject(content);

    		Map<String, String> DBName2DBType = GetTaskServlet.map;
    		if(!content.contains("HeartBeat")){
    			if(newjArray.size()>0){
    				JSONObject record = newjArray.getJSONObject(0);
    				dbName = record.getString(DATABASE_NAME);
    				tName = record.getString(TABLE_NAME);
    			}
    			System.out.println("dbName = " + dbName);
    			Assert.assertTrue("this database or file doesn't exists", DBName2DBType.containsKey(dbName));

    			//write data to local
    	    	String fileName = DATABASE_FILE_PREFIX  + dbName + "/" + dbName + "_" + tName;
    	    	RandomAccessFile file = new RandomAccessFile(fileName, "rw");
    			file.seek(file.length());
    			for(int i = 0; i < newjArray.size(); i++){
        			file.write((newjArray.get(i) + "\n").getBytes());
        		}
    			file.close();
    		}

//    		if(!content.contains("HeartBeat") && DBName2DBType.containsKey(dbName)){
//    			try {
//    				updateTask(content, DBName2DBType.get(dbName));
//    			} catch (ParseException e) {
//    				e.printStackTrace();
//    			}
//    		}

    		if (!content.contains("HeartBeat") && DBName2DBType.containsKey(dbName) && DBName2DBType.get(dbName).equals("file")) {
    			System.out.println("got one pic, Num:" + picNum++);
    			if (NEED_SAVE_FILE) {
    				saveFile(content, dbName);
    			}
    		}
    	}
    }

    public static void checkVersionColumnOffset(String databaseName, String tableName, JSONArray jArray) throws JsonParseException, JsonMappingException, IOException {
		MultiDatabaseBean mBean = GetTaskServlet.getMultiDatabaseBean();
		for(int i = 0; i < mBean.getDatabases().size(); i++){
			if(mBean.getDatabases().get(i).getDatabaseName().equals(databaseName)){
				DatabaseBean dbBean = mBean.getDatabases().get(i);
				for (TableBean tBean : dbBean.getTables()) {
					if(tBean.getTableName().equals(tableName)){
						String oldVersionColumnOffset = tBean.getMinVersion();
						for(int k = 0; k < jArray.size(); k++){
							String newVersionColumnOffset = jArray.getJSONObject(k).getString(VERSION_COLUMN_OFFSET);
							Assert.assertTrue("Database : " + databaseName + "; TableName : " + tableName + ". VersionColumnOffset of new record is less than that from web", BeaverUtils.StringCompareWithLength(newVersionColumnOffset, oldVersionColumnOffset) >= 0);
							if(BeaverUtils.StringCompareWithLength(newVersionColumnOffset, tBean.getMinVersion()) > 0){
								tBean.setMinVersion(newVersionColumnOffset);
							}
						}
					}
				}
			}
		}
		GetTaskServlet.setMultiDatabaseBean(mBean);
	}

    public static void saveFile(String content, String serverType) throws IOException{
    	JSONArray newjArray = JSONArray.fromObject(content);
		if(newjArray.size()>0){
			for(int i=0;i<newjArray.size();i++){
				JSONObject iob = newjArray.getJSONObject(i);
				String fileName = iob.getString("file_name");
//				String dirName = iob.getString("hdfs_table");
				String fileData = iob.getString("file_data");
				Object database = iob.get("hdfs_db");
				if(!DBName2DBType.get(database).equals("file")){
					continue;
				}
				BufferedWriter out = new BufferedWriter(new FileWriter(FILE_SAVE_DIR+fileName));
				out.write(fileData);
				out.flush();
				out.close();
			}
		}
    }

    public static void main(String []args) throws ParseException, IOException {
    }
}
