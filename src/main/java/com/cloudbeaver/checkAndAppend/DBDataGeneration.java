package com.cloudbeaver.checkAndAppend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.core.appender.rolling.action.IfAccumulatedFileCount;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;

import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.Map;
import java.util.Random;


public class DBDataGeneration {
	public static final String SETUP_SQL_FILE = "setup.sql";
	public static final String TEARDOWN_SQL_FILE = "tearDown.sql";
	public static final String USERS = "users";
	public static final String STUDENT = "student";
	public static final String DEPARTMENT = "department";
	public static final String COURSE = "course";
	public static final String TEACHER = "teacher";
	public static final String DATE = "date_test";
	public static final String DATETIME = "datetime_test";
	public static final String TIMESTAMP = "timestamp_test";
	public static final String CONSUMER = "consumer";
	public static final String CLASS = "class";
	public static final String MONITOR = "monitor";
	public static Map<String, String> TestFileMap = new HashMap<String, String>();
	public static String SQLSERVER = "sqlserver";
	public static String ORACLE = "oracle";
	public static String MYSQL = "mysql";
	public static String POSTGRES = "postgres";
	public static final String CreateTableSQL[] = {
			"create table department(ID integer, name varchar(50));\n",
			"create table class(ID integer, classname varchar(20), depId integer);\n"
			};
	public static final String DropTableSQL[] = {
			"drop table if exists department;\n",
			"drop table if exists class ;\n"};
	public static final String DBDataFile[] = {"setup.sql", "tearDown.sql", "users", "student", "department", "course", "teacher", "date_test", "datetime_test", "timestamp_test", 
			"consumer", "class", "monitor","TestTimeStamp"};
	public static ArrayList<Integer> WRITE_RECORD_NUMS_COURSE = new ArrayList<Integer>(); //writing record number of table course every time
	public static ArrayList<Integer> WRITE_RECORD_NUMS_TEACHER = new ArrayList<Integer>(); //writing record number of table teacher every time
	public static ArrayList<Integer> RECORD_NUMS = new ArrayList<Integer>(); //record number every time
	public static boolean WRITE_MODE = false;
	public static int DIFF = 0;
	public static int MIN_ID = 1;
	public static int MAX_ID = 0;
	public static int START_YEAR = 1970;
	public static String START_DATE = "2011-01-01";
	public static String CURRENT_DATE = START_DATE;
	public static String START_DATETIME = "1970-01-02 00:00:00";
	public static String CURRENT_DATETIME = START_DATETIME;
	public static String START_STRING = "1000000000000000000000000";
	public static String CURRENT_STRING = START_STRING;
	public static String STRING_DIFF = "100";
	private static int DEFAULT_NUM = 12000;

	public static void writeCreateTableSQL(DatabaseBeanTestConf dbBean){
		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + SETUP_SQL_FILE;
    	RandomAccessFile file;
    	System.out.println("Note: db name is "+dbBean.getDatabaseName());
		try {
			System.out.println("true base is "+fileName);
			file = new RandomAccessFile(fileName, "rw");
			file.seek(file.length());
			for(int i = 0 ; i < CreateTableSQL.length; i++){
				file.write(CreateTableSQL[i].getBytes());
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeDropTableSQL(DatabaseBeanTestConf dbBean){
		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + TEARDOWN_SQL_FILE;
    	RandomAccessFile file;
		try {
			file = new RandomAccessFile(fileName, "rw");
			file.seek(file.length());
			for(int i = 0 ; i < DropTableSQL.length; i++){
				file.write(DropTableSQL[i].getBytes());
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getDepartmentData(DatabaseBeanTestConf dbBean, int recordNum) {
		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + DEPARTMENT;
    	RandomAccessFile file;
    	File f = new File(fileName);
    	BufferedWriter writer = null;
		try {
			if(!f.exists()){
				writer = new BufferedWriter(new FileWriter(f));
	            writer.write("ID\n");
	            writer.write("ID,name\n");
	            writer.close();
			}
			file = new RandomAccessFile(fileName, "rw");
			file.seek(file.length());
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0; i < recordNum; i++){
				list.add((i + MIN_ID) + "");
			}
			if(RECORD_NUMS.size() == 1){
				shufflecard(list);
			}
			for(int i = 0; i < recordNum; i++){
				String department = RandomStringUtils.randomAlphanumeric(20);
				String record = list.get(i) + "," + "\'" + department + "\'";
				file.write((record + "\n").getBytes());
				String tempString = "insert into department values(" + record + ")";
            	dbBean.getJdbcTemplate().execute(tempString);
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getClassData(DatabaseBeanTestConf dbBean, int recordNum) {
		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + CLASS;
    	RandomAccessFile file;
    	File f = new File(fileName);
    	BufferedWriter writer = null;
		try {
			if(!f.exists()){
				writer = new BufferedWriter(new FileWriter(f));
	            writer.write("ID\n");
	            writer.write("ID,classname,depId\n");
	            writer.close();
			}
			file = new RandomAccessFile(fileName, "rw");
			file.seek(file.length());
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0; i < recordNum; i++){
				list.add((i + MIN_ID) + "");
			}
			if(RECORD_NUMS.size() == 1){
				shufflecard(list);
			}
			for(int i = 0; i < recordNum; i++){
				String classname = RandomStringUtils.randomAlphanumeric(20);
				int depId = (int) (Math.random() * recordNum +  MIN_ID);
				String record = list.get(i) + "," + "\'" + classname + "\'" + "," + depId;
				file.write((record + "\n").getBytes());
				String tempString = "insert into class values(" + record + ")";
            	dbBean.getJdbcTemplate().execute(tempString);
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getUsersData(DatabaseBeanTestConf dbBean, int recordNum) {
		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + USERS;
    	RandomAccessFile file;
    	File f = new File(fileName);
    	BufferedWriter writer = null;
		try {
			if(!f.exists()){
				writer = new BufferedWriter(new FileWriter(f));
	            writer.write("ID\n");
	            writer.write("ID,name\n");
	            writer.close();
			}
			file = new RandomAccessFile(fileName, "rw");
			file.seek(file.length());
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0; i < recordNum; i++){
				list.add((i + MIN_ID) + "");
			}
			if(RECORD_NUMS.size() == 1){
				shufflecard(list);
			}
			for(int i = 0; i < recordNum; i++){
				String user = RandomStringUtils.randomAlphanumeric(20);
				String record = list.get(i) + "," + "\'" + user + "\'";
				file.write((record + "\n").getBytes());
				String tempString = "insert into users values(" + record + ")";
            	dbBean.getJdbcTemplate().execute(tempString);
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getStudentData(DatabaseBeanTestConf dbBean, int recordNum) {
		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + STUDENT;
    	RandomAccessFile file;
    	File f = new File(fileName);
    	BufferedWriter writer = null;
		try {
			if(!f.exists()){
				writer = new BufferedWriter(new FileWriter(f));
	            writer.write("ID\n");
	            writer.write("name,ID,depId\n");
	            writer.close();
			}
			file = new RandomAccessFile(fileName, "rw");
			file.seek(file.length());
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0; i < recordNum; i++){
				list.add((i + MIN_ID) + "");
			}
			if(RECORD_NUMS.size() == 1){
				shufflecard(list);
			}
			for(int i = 0; i < recordNum; i++){
				String student = RandomStringUtils.randomAlphanumeric(20);
				int depId = (int) (Math.random() * recordNum +  MIN_ID);
				String record = "\'" + student + "\'," + list.get(i) + "," + depId;
				file.write((record + "\n").getBytes());
				String tempString = "insert into student values(" + record + ")";
            	dbBean.getJdbcTemplate().execute(tempString);
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getMonitorData(DatabaseBeanTestConf dbBean, int recordNum) {
		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + MONITOR;
    	RandomAccessFile file;
    	File f = new File(fileName);
    	BufferedWriter writer = null;
		try {
			if(!f.exists()){
				writer = new BufferedWriter(new FileWriter(f));
	            writer.write("ID\n");
	            writer.write("ID,classId\n");
	            writer.close();
			}
			file = new RandomAccessFile(fileName, "rw");
			file.seek(file.length());
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0; i < recordNum; i++){
				list.add((i + MIN_ID) + "");
			}
			if(RECORD_NUMS.size() == 1){
				shufflecard(list);
			}
			for(int i = 0; i < recordNum; i++){
				int classId = (int) (Math.random() * recordNum +  MIN_ID);
				String record = list.get(i) + "," + classId;
				file.write((record + "\n").getBytes());
				String tempString = "insert into monitor values(" + record + ")";
            	dbBean.getJdbcTemplate().execute(tempString);
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getConsumerData(DatabaseBeanTestConf dbBean, int recordNum) {
		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + CONSUMER;
    	RandomAccessFile file;
    	File f = new File(fileName);
    	BufferedWriter writer = null;
		try {
			if(!f.exists()){
				writer = new BufferedWriter(new FileWriter(f));
				writer.write("ID\n");
	            writer.write("ID,depId\n");
	            writer.close();
			}
			file = new RandomAccessFile(fileName, "rw");
			file.seek(file.length());
			ArrayList<String> list = getRandomBigInteger(recordNum);
			if(RECORD_NUMS.size() > 1){
				Collections.sort(list);
			}
			for(int i = 0; i < recordNum; i++){
				int depId = (int) (Math.random() * recordNum + MIN_ID);
				String record = "\'" + list.get(i) + "\'," + depId;
				file.write((record + "\n").getBytes());
				String tempString = "insert into consumer values(" + record + ")";
            	dbBean.getJdbcTemplate().execute(tempString);
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public static void getCourseData(DatabaseBeanTestConf dbBean, int recordNum) {
		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + COURSE;
    	RandomAccessFile file;
    	File f = new File(fileName);
    	BufferedWriter writer = null;
		try {
			if(!f.exists()){
				writer = new BufferedWriter(new FileWriter(f));
	            writer.write("TOTAL_LINES_NUMBER\n");
	            writer.write("courseName\n");
	            writer.close();
			}
			file = new RandomAccessFile(fileName, "rw");
			file.seek(file.length());
			for(int i = 0; i < recordNum; i++){
				String course = RandomStringUtils.randomAlphanumeric(20);
				String record = "\'" + course + "\'";
				file.write((record + "\n").getBytes());
				String tempString = "insert into course values(" + record + ")";
            	dbBean.getJdbcTemplate().execute(tempString);
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void getTestTimeStampData(DatabaseBeanTestConf dbBean,int recordNum){
		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + "TestTimeStamp";
    	RandomAccessFile file;
    	File f = new File(fileName);
    	BufferedWriter writer = null;
		try {
			if(!f.exists()){
				writer = new BufferedWriter(new FileWriter(f));
	            writer.write("ID\n");
	            writer.write("ID,depId,xgsj\n");
	            writer.close();
			}
			file = new RandomAccessFile(fileName, "rw");
			file.seek(file.length());
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0; i < recordNum; i++){
				list.add((i + MIN_ID) + "");
			}
			if(RECORD_NUMS.size() == 1){
				shufflecard(list);
			}
			long start=0;
			long time_stamp_insert_time=0;
			for(int i = 0; i < recordNum; i++){
				int depId = (int) (Math.random() * recordNum +  MIN_ID);
				String record =list.get(i) + "," + depId+",DEFAULT";
				file.write((record + "\n").getBytes());
				String tempString = "insert into TestTimeStamp values(" + record + ")";
				start=System.nanoTime();
            	dbBean.getJdbcTemplate().execute(tempString);
            	time_stamp_insert_time+=System.nanoTime()-start;
			}
			System.out.println("time stamp insert is "+(time_stamp_insert_time/1000000)+"ms");
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void getTeacherData(DatabaseBeanTestConf dbBean, int recordNum) {
		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + TEACHER;
    	RandomAccessFile file;
    	File f = new File(fileName);
    	BufferedWriter writer = null;
		try {
			if(!f.exists()){
				writer = new BufferedWriter(new FileWriter(f));
	            writer.write("TOTAL_LINES_NUMBER\n");
	            writer.write("name,depId\n");
	            writer.close();
			}
			file = new RandomAccessFile(fileName, "rw");
			file.seek(file.length());
			for(int i = 0; i < recordNum; i++){
				String teacher = RandomStringUtils.randomAlphanumeric(20);
				int depId = (int) (Math.random() * recordNum + MIN_ID);
				String record = "\'" + teacher + "\'," + depId;
				file.write((record + "\n").getBytes());
				String tempString = "insert into teacher values(" + record + ")";
            	dbBean.getJdbcTemplate().execute(tempString);
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	public static void getCourseData(DatabaseBeanTestConf dbBean, int recordNum) {
//		int writeRecordNum = 0;
//		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + COURSE;
//    	RandomAccessFile file;
//    	StringBuilder sb = new StringBuilder("");
//    	File f = new File(fileName);
//    	BufferedReader reader = null;
//		try {
//			if(f.exists()){
//				reader = new BufferedReader(new FileReader(f));
//	            String tempString = null;
//	            reader.readLine();
//	            reader.readLine();
//	            int skipNum = getSkipNums(WRITE_RECORD_NUMS_COURSE);
//	            for(int i = 0; i < skipNum; i++){
//	            	reader.readLine();
//	            }
//	            while ((tempString = reader.readLine()) != null) {
//	            	writeRecordNum++;
//	                sb.append(tempString).append("\n");
//	            }
//	            reader.close();
//			} else {
//				file = new RandomAccessFile(fileName, "rw");
//				file.seek(file.length());
//				file.write("TOTAL_LINES_NUMBER\n".getBytes());
//				file.write("courseName\n".getBytes());
//			}
//			file = new RandomAccessFile(fileName, "rw");
//			file.seek(file.length());
//			if(!sb.toString().equals("")){
//				file.write(sb.toString().getBytes());
//			}
//			for(int i = 0; i < recordNum; i++){
//				String course = RandomStringUtils.randomAlphanumeric(20);
//				String record = "\'" + course + "\'";
//				file.write((record + "\n").getBytes());
//				String tempString = "insert into course values(" + record + ")";
//            	dbBean.getJdbcTemplate().execute(tempString);
//			}
//			writeRecordNum += recordNum;
//			if(WRITE_MODE && WRITE_RECORD_NUMS_COURSE.size() < RECORD_NUMS.size()){
//				WRITE_RECORD_NUMS_COURSE.add(writeRecordNum);
//			}
//			file.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

//	public static void getTeacherData(DatabaseBeanTestConf dbBean, int recordNum) {
//		int writeRecordNum = 0;
//		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + TEACHER;
//    	RandomAccessFile file;
//    	StringBuilder sb = new StringBuilder("");
//    	File f = new File(fileName);
//    	BufferedReader reader = null;
//		try {
//			if(f.exists()){
//				reader = new BufferedReader(new FileReader(f));
//	            String tempString = null;
//	            reader.readLine();
//	            reader.readLine();
//	            int skipNum = getSkipNums(WRITE_RECORD_NUMS_TEACHER);
//	            for(int i = 0; i < skipNum; i++){
//	            	reader.readLine();
//	            }
//	            while ((tempString = reader.readLine()) != null) {
//	            	writeRecordNum++;
//	                sb.append(tempString).append("\n");
//	            }
//	            reader.close();
//			} else {
//				file = new RandomAccessFile(fileName, "rw");
//				file.seek(file.length());
//				file.write("TOTAL_LINES_NUMBER\n".getBytes());
//				file.write("name,depId\n".getBytes());
//			}
//			file = new RandomAccessFile(fileName, "rw");
//			file.seek(file.length());
//			if(!sb.toString().equals("")){
//				file.write(sb.toString().getBytes());
//			}
//			for(int i = 0; i < recordNum; i++){
//				String teacher = RandomStringUtils.randomAlphanumeric(20);
//				int depId = (int) (Math.random() * recordNum + MIN_ID);
//				String record = "\'" + teacher + "\'," + depId;
//				file.write((record + "\n").getBytes());
//				String tempString = "insert into teacher values(" + record + ")";
//            	dbBean.getJdbcTemplate().execute(tempString);
//			}
//			writeRecordNum += recordNum;
//			if(WRITE_MODE && WRITE_RECORD_NUMS_TEACHER.size() < RECORD_NUMS.size()){
//				WRITE_RECORD_NUMS_TEACHER.add(writeRecordNum);
//			}
//			file.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public static void getDateData(DatabaseBeanTestConf dbBean, int recordNum) throws ParseException {
		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + DATE;
    	RandomAccessFile file;
    	File f = new File(fileName);
    	BufferedWriter writer = null;
		try {
			if(!f.exists()){
				writer = new BufferedWriter(new FileWriter(f));
	            writer.write("ID\n");
	            writer.write("ID,depId\n");
	            writer.close();
			}
			file = new RandomAccessFile(fileName, "rw");
			file.seek(file.length());
			ArrayList<String> time = getRandomDate(recordNum);
			if(RECORD_NUMS.size() == 1){
				shufflecard(time);
			}
			for(int i = 0; i < recordNum; i++){				
				int depId = (int) (Math.random() * recordNum +  MIN_ID);
				String record = "";
				record = "\'" + time.get(i) + "\'," + depId;
				file.write((record + "\n").getBytes());
				if(dbBean.getDatabaseType().equals(ORACLE)){
					record = "to_date(\'" + time.get(i) + "\','YYYY-MM-DD')," + depId;
				}				
				String tempString = "insert into date_test values(" + record + ")";
            	dbBean.getJdbcTemplate().execute(tempString);
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getDatetimeData(DatabaseBeanTestConf dbBean, int recordNum) throws ParseException {
		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + DATETIME;
    	RandomAccessFile file;
    	File f = new File(fileName);
    	BufferedWriter writer = null;
		try {
			if(!f.exists()){
				writer = new BufferedWriter(new FileWriter(f));
	            writer.write("ID\n");
	            writer.write("ID,depId\n");
	            writer.close();
			}
			file = new RandomAccessFile(fileName, "rw");
			file.seek(file.length());
			ArrayList<String> time = getRandomDateTime(recordNum);
			if(RECORD_NUMS.size() == 1){
				shufflecard(time);
			}
			for(int i = 0; i < recordNum; i++){				
				int depId = (int) (Math.random() * recordNum +  MIN_ID);
				String record = "\'" + time.get(i) + "\'," + depId;
				file.write((record + "\n").getBytes());
				String tempString = "insert into datetime_test values(" + record + ")";
            	dbBean.getJdbcTemplate().execute(tempString);
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getTimestampData(DatabaseBeanTestConf dbBean, int recordNum) {
		String fileName = TestFileMap.get(dbBean.getDatabaseName()) + TIMESTAMP;
    	RandomAccessFile file;
    	File f = new File(fileName);
    	BufferedWriter writer = null;
		try {
			if(!f.exists()){
				writer = new BufferedWriter(new FileWriter(f));
	            writer.write("ID\n");
	            writer.write("ID,depId\n");
	            writer.close();
			}
			file = new RandomAccessFile(fileName, "rw");
			file.seek(file.length());
			ArrayList<String> time = null;
			if(dbBean.getDatabaseType().equals(MYSQL)){
				time = getRandomTimeStamp(recordNum);
			} else if(dbBean.getDatabaseType().equals(ORACLE) || dbBean.getDatabaseType().equals(POSTGRES)){
				time = getRandomTimeStampForOther(recordNum);
			}
			if(RECORD_NUMS.size() > 1){
				Collections.sort(time);
			}
			for(int i = 0; i < recordNum; i++){				
				int depId = (int) (Math.random() * recordNum +  MIN_ID);
				String record = "\'" + time.get(i) + "\'," + depId;
				file.write((record + "\n").getBytes());
				String tempString = null;
				if(dbBean.getDatabaseType().equals(MYSQL)){
					tempString = "insert into timestamp_test values(" + record + ")";
				} else if(dbBean.getDatabaseType().equals(ORACLE) || dbBean.getDatabaseType().equals(POSTGRES)){
					tempString = "insert into timestamp_test values(to_timestamp(\'" + time.get(i) + "\','yyyy-mm-dd hh24:mi:ss')," + depId + ")";
				}
            	dbBean.getJdbcTemplate().execute(tempString);
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> getRandomTimeStamp(int num) {
		ArrayList<String> list = new ArrayList<String>();
		int a = 0;
		while(list.size() < num){
			StringBuilder sb =  new StringBuilder();
			a = START_YEAR + RECORD_NUMS.size() - 1;
			sb.append(a);
			a = (int) (Math.random() * 10) + 1;
			if(a < 10){
				sb.append("0").append(a);
			} else {
				sb.append(a);
			}
			a = (int) (Math.random() * 10) + 15;
			sb.append(a);
			a = (int) (Math.random() * 10) + 10;
			sb.append(a);
			a = (int) (Math.random() * 50) + 10;
			sb.append(a);
			a = (int) (Math.random() * 50) + 10;
			sb.append(a);
			if(list.isEmpty() || !list.contains(sb.toString())){
				list.add(sb.toString());
			}
		}
		return list;
	}

	public static ArrayList<String> getRandomTimeStampForOther(int num) {
		ArrayList<String> list = new ArrayList<String>();
		int a = 0;
		while(list.size() < num){
			StringBuilder sb =  new StringBuilder();
			a = START_YEAR + RECORD_NUMS.size() - 1;
			sb.append(a).append("-");
			a = (int) (Math.random() * 10) + 1;
			if(a < 10){
				sb.append("0").append(a).append("-");
			} else {
				sb.append(a).append("-");
			}
			a = (int) (Math.random() * 10) + 15;
			sb.append(a).append(" ");
			a = (int) (Math.random() * 10) + 10;
			sb.append(a).append(":");
			a = (int) (Math.random() * 50) + 10;
			sb.append(a).append(":");
			a = (int) (Math.random() * 50) + 10;
			sb.append(a);
			if(list.isEmpty() || !list.contains(sb.toString())){
				list.add(sb.toString());
			}
		}
		return list;
	}

	public static ArrayList<String> getRandomDateTime(int num) throws ParseException {
		ArrayList<String> list = new ArrayList<String>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
	    Date date = simpleDateFormat.parse(START_DATETIME);
	    Calendar calendar = new GregorianCalendar(); 
		while(list.size() < num){
			if(list.isEmpty() || !list.contains(simpleDateFormat.format(date))){
				list.add(simpleDateFormat.format(date));
			}
		    calendar.setTime(date); 
		    calendar.add(calendar.HOUR,1);
		    date = calendar.getTime();
		}
		if(CURRENT_DATETIME.compareTo(simpleDateFormat.format(date)) < 0){
			CURRENT_DATETIME = simpleDateFormat.format(date);
		}
		return list;
	}

	public static ArrayList<String> getRandomDate(int num) throws ParseException {
		ArrayList<String> list = new ArrayList<String>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Date date = simpleDateFormat.parse(START_DATE);
	    Calendar calendar = new GregorianCalendar(); 
		while(list.size() < num){
			if(list.isEmpty() || !list.contains(simpleDateFormat.format(date))){
				list.add(simpleDateFormat.format(date));
			}
		    calendar.setTime(date); 
		    calendar.add(calendar.DATE,1);
		    date = calendar.getTime();
		}
		if(CURRENT_DATE.compareTo(simpleDateFormat.format(date)) < 0){
			CURRENT_DATE = simpleDateFormat.format(date);
		}
		return list;
	}

	public static int getSkipNums(ArrayList<Integer> list){
		int sum = 0;
		for(int i = 0; i < list.size() - 1; i++){
			sum += list.get(i);
		}
		return sum;
	}

	public static ArrayList<String> getRandomBigInteger(int num){
		BigInteger bInteger = new BigInteger(START_STRING);
		ArrayList<String> list = new ArrayList<String>();
		while(list.size() < num){
			String a = bInteger.toString();
			if(list.isEmpty() || !list.contains(a)){
				list.add(a);
			}
			bInteger = bInteger.add(new BigInteger(STRING_DIFF));
		}
		if(CURRENT_STRING.compareTo(bInteger.toString()) < 0){
			CURRENT_STRING = bInteger.toString();
		}
		return list;
	}

	public static ArrayList<String> getRandomBigIntegerNew(int num){
		String prefix = "100000000000000";
		ArrayList<String> list = new ArrayList<String>();
		while(list.size() < num){
			String a = prefix + RandomStringUtils.randomNumeric(10);
			if(list.isEmpty() || !list.contains(a)){
				list.add(a);
			}
		}
		return list;
	}

	public static void shufflecard(ArrayList<String> list){
        Random rd = new Random();
        for(int i = 0; i < list.size(); i++){
            int j = rd.nextInt(list.size());
            String temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }
	
	public static String[] readFile(String fileName){
    	StringBuilder sb = new StringBuilder();
    	File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
               sb.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return sb.toString().split(";");
    }

	public void DBInit(CheckAndAppendTestConf dbBeans) throws ParseException{
		START_DATE = CURRENT_DATE;
		START_DATETIME = CURRENT_DATETIME;
		START_STRING = CURRENT_STRING;
		RECORD_NUMS.add(1);

		DatabaseBeanTestConf dbBean = null;
		long startTime = 0;
		long endTime = 0;
		int maxId = 0;
		System.out.println("into dbinit,and tested db size is "+dbBeans.getDatabases().size());
		for (int i = 0; i < dbBeans.getDatabases().size(); i++) {
			dbBean = dbBeans.getDatabases().get(i);
			int maxRecordNum = 0;
			Map<String, Integer> recordNum = new HashMap<String, Integer>();
			recordNum.put(DEPARTMENT, DEFAULT_NUM);
			recordNum.put(CLASS, DEFAULT_NUM);
			for(TableBeanTestConf tConf : dbBean.getTables()){
				recordNum.put(tConf.getTableName().toLowerCase(), tConf.getInitNum());
				maxRecordNum = maxRecordNum > tConf.getInitNum() ? maxRecordNum : tConf.getInitNum();
			}
			DIFF = maxRecordNum + 10000;
			MAX_ID = MIN_ID + DIFF;
			maxId = maxId > MAX_ID ? maxId : MAX_ID;
			if(i == dbBeans.getDatabases().size() - 1){
				WRITE_MODE = true;
			}
			System.out.println("init db : " + dbBean.getDatabaseName());
            if (TestFileMap.containsKey(dbBean.getDatabaseName())) {
        		writeCreateTableSQL(dbBean);
            	String sql[] = readFile(TestFileMap.get(dbBean.getDatabaseName()) + SETUP_SQL_FILE);
            	for(int j = 0; j < sql.length; j++){
	            	dbBean.getJdbcTemplate().execute(sql[j]);
            	}
            	startTime = System.currentTimeMillis();
            	getDepartmentData(dbBean, recordNum.get(DEPARTMENT));
            	endTime = System.currentTimeMillis();
            	System.out.println("表department运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get(DEPARTMENT));

            	startTime = System.currentTimeMillis();
            	getClassData(dbBean, recordNum.get(CLASS));
            	endTime = System.currentTimeMillis();
            	System.out.println("表class运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get(CLASS));

            	if(dbBean.isContainsLong()){
            		dbBean.getJdbcTemplate().execute("create table users(ID integer, name varchar(20))");
            		dbBean.getJdbcTemplate().execute("create table student(name varchar(20), ID integer, depId integer)");

	            	startTime = System.currentTimeMillis();
	            	getUsersData(dbBean, recordNum.get(USERS));
	            	endTime = System.currentTimeMillis();
	            	System.out.println("表users运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get(USERS));
	
	            	startTime = System.currentTimeMillis();
	            	getStudentData(dbBean, recordNum.get(STUDENT));
	            	endTime = System.currentTimeMillis();
	            	System.out.println("表student运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get(STUDENT));
            	}
            	if(dbBean.isContainsLongText()){
            		dbBean.getJdbcTemplate().execute("create table consumer(ID varchar(25), depId integer)");

	            	startTime = System.currentTimeMillis();
	            	getConsumerData(dbBean, recordNum.get(CONSUMER));
	            	endTime = System.currentTimeMillis();
	            	System.out.println("表consumer运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get(CONSUMER));
            	}
            	if(dbBean.isContainsReplaceOp()){
            		dbBean.getJdbcTemplate().execute("create table monitor(ID integer, classId integer)");

	            	startTime = System.currentTimeMillis();
	            	getMonitorData(dbBean, recordNum.get(MONITOR));
	            	endTime = System.currentTimeMillis();
	            	System.out.println("表monitor运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get(MONITOR));
            	}
            	if(dbBean.isFullSync()){
            		dbBean.getJdbcTemplate().execute("create table course(courseName varchar(20))");
            		dbBean.getJdbcTemplate().execute("create table teacher(name varchar(20), depId integer)");

            		startTime = System.currentTimeMillis();
            		getCourseData(dbBean, recordNum.get(COURSE));
            		endTime = System.currentTimeMillis();
                	System.out.println("表course运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get(COURSE));

                	startTime = System.currentTimeMillis();
                	getTeacherData(dbBean, recordNum.get(TEACHER));
                	endTime = System.currentTimeMillis();
                	System.out.println("表teacher运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get(TEACHER));
            	}
            	if(dbBean.iscontainsTestTimeStamp()){
            		dbBean.getJdbcTemplate().execute("create table TestTimeStamp(ID integer, depId integer,xgsj timestamp)");
	            	startTime = System.currentTimeMillis();
	            	getTestTimeStampData(dbBean, recordNum.get("testtimestamp"));
	            	endTime = System.currentTimeMillis();
	            	System.out.println("表TestTimeStamp运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get("testtimestamp"));
            	}
            	if(dbBean.isContainsDate()){
            		if(dbBean.getDatabaseType().equals(MYSQL)){
                		dbBean.getJdbcTemplate().execute("create table date_test(ID date, depId integer);");
                		dbBean.getJdbcTemplate().execute("create table datetime_test(ID datetime, depId integer);");
                		dbBean.getJdbcTemplate().execute("create table timestamp_test(ID timestamp, depId integer);");

                		startTime = System.currentTimeMillis();
                		getDateData(dbBean, recordNum.get(DATE));
                		endTime = System.currentTimeMillis();
                    	System.out.println("表date运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get(DATE));

                    	startTime = System.currentTimeMillis();
                		getDatetimeData(dbBean, recordNum.get(DATETIME));
                		endTime = System.currentTimeMillis();
                    	System.out.println("表datetime运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get(DATETIME));

                    	startTime = System.currentTimeMillis();
                		getTimestampData(dbBean, recordNum.get(TIMESTAMP));
                		endTime = System.currentTimeMillis();
                    	System.out.println("表timestamp运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get(TIMESTAMP));
                	} else if(dbBean.getDatabaseType().equals(SQLSERVER)){
                		dbBean.getJdbcTemplate().execute("create table datetime_test(ID datetime, depId integer);");

                		startTime = System.currentTimeMillis();
                		getDatetimeData(dbBean, recordNum.get(DATETIME));
                		endTime = System.currentTimeMillis();
                    	System.out.println("表datetime运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get(DATETIME));
                	} else if(dbBean.getDatabaseType().equals(ORACLE) || dbBean.getDatabaseType().equals(POSTGRES)){
                		dbBean.getJdbcTemplate().execute("create table date_test(ID date, depId integer)");
                		dbBean.getJdbcTemplate().execute("create table timestamp_test(ID timestamp, depId integer)");

                		startTime = System.currentTimeMillis();
                		getDateData(dbBean, recordNum.get(DATE));
                		endTime = System.currentTimeMillis();
                    	System.out.println("表date运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get(DATE));

                    	startTime = System.currentTimeMillis();
                		getTimestampData(dbBean, recordNum.get(TIMESTAMP));
                		endTime = System.currentTimeMillis();
                    	System.out.println("表timestamp运行时间：" + (endTime - startTime)/1000 + "s, " + "条数： " + recordNum.get(TIMESTAMP));
                	}
            	}
            }
        }
		WRITE_MODE = false;
		MAX_ID = maxId;
	}

	public void DBClear(CheckAndAppendTestConf dbBeans){
		for (DatabaseBeanTestConf dbBean : dbBeans.getDatabases()) {
	        if (TestFileMap.containsKey(dbBean.getDatabaseName())) {
	        	writeDropTableSQL(dbBean);
	        	String sql[] = readFile(TestFileMap.get(dbBean.getDatabaseName()) + TEARDOWN_SQL_FILE);
	        	for(int i = 0; i < sql.length; i++){
	            	dbBean.getJdbcTemplate().execute(sql[i]);
	        	}
	        	if(dbBean.isContainsLong()){
	        		dbBean.getJdbcTemplate().execute("drop table if exists users");
	        		dbBean.getJdbcTemplate().execute("drop table if exists student");
	        	}
	        	if(dbBean.isContainsReplaceOp()){
	        		dbBean.getJdbcTemplate().execute("drop table if exists monitor");
	        	}
	        	if(dbBean.isFullSync()){
	        		dbBean.getJdbcTemplate().execute("drop table if exists course");
	        		dbBean.getJdbcTemplate().execute("drop table if exists teacher");
	        	}
	        	if(dbBean.iscontainsTestTimeStamp()){
	        		dbBean.getJdbcTemplate().execute("drop table if exists TestTimeStamp");
	        	}
	        	if(dbBean.isContainsDate()){
	        		if(dbBean.getDatabaseType().equals(MYSQL)){
	            		dbBean.getJdbcTemplate().execute("drop table if exists date_test;");
	            		dbBean.getJdbcTemplate().execute("drop table if exists datetime_test;");
	            		dbBean.getJdbcTemplate().execute("drop table if exists timestamp_test;");
	            	} else if(dbBean.getDatabaseType().equals(SQLSERVER)){
	            		dbBean.getJdbcTemplate().execute("drop table if exists datetime_test;");
	            	} else if(dbBean.getDatabaseType().equals(ORACLE) || dbBean.getDatabaseType().equals(POSTGRES)){
	            		dbBean.getJdbcTemplate().execute("drop table if exists date_test");
	            		dbBean.getJdbcTemplate().execute("drop table if exists timestamp_test");
	            	}
	        	}
	        	if(dbBean.isContainsLongText()){
	        		dbBean.getJdbcTemplate().execute("drop table if exists consumer");
	        	}
	        }
	    }
		RECORD_NUMS.clear();
		WRITE_RECORD_NUMS_COURSE.clear();
		WRITE_RECORD_NUMS_TEACHER.clear();
		WRITE_MODE = false;
		MIN_ID = 1;
		MAX_ID = 0;
		DIFF = 0;
		START_YEAR = 1970;
		START_DATE = "2011-01-01";
		CURRENT_DATE = START_DATE;
		START_DATETIME = "1970-01-02 00:00:00";
		CURRENT_DATETIME = START_DATETIME;
		START_STRING = "1000000000000000000000000";
		CURRENT_STRING = START_STRING;
		STRING_DIFF = "100";
	}

	public void appendDataToDB(DatabaseBeanTestConf dbBean, int i, CheckAndAppendTestConf dbBeans) throws ParseException{
		START_DATE = CURRENT_DATE;
		START_DATETIME = CURRENT_DATETIME;
		START_STRING = CURRENT_STRING;
		RECORD_NUMS.add(1);
		int maxId = 0;
		int maxRecordNum = 0;
		Map<String, Integer> recordNum = new HashMap<String, Integer>();
		recordNum.put(DEPARTMENT, DEFAULT_NUM);
		recordNum.put(CLASS, DEFAULT_NUM);
		for(TableBeanTestConf tConf : dbBean.getTables()){
			recordNum.put(tConf.getTableName(), tConf.getInitNum());
			maxRecordNum = maxRecordNum > tConf.getInitNum() ? maxRecordNum : tConf.getInitNum();
		}
		DIFF = maxRecordNum + 10000;
		MIN_ID = MAX_ID + 1;
		MAX_ID = MIN_ID + DIFF;
		maxId = maxId > MAX_ID ? maxId : MAX_ID;
		if(i == dbBeans.getDatabases().size() - 1){
			WRITE_MODE = true;
		}
        if (TestFileMap.containsKey(dbBean.getDatabaseName())) {
        	getDepartmentData(dbBean, recordNum.get(DEPARTMENT));
        	getClassData(dbBean, recordNum.get(CLASS));
        	if(dbBean.isContainsLong()){
            	getUsersData(dbBean, recordNum.get(USERS));
            	getStudentData(dbBean, recordNum.get(STUDENT));
        	}
        	if(dbBean.isContainsLongText()){
        		getConsumerData(dbBean, recordNum.get(CONSUMER));
        	}
        	if (dbBean.isContainsReplaceOp()) {
				getMonitorData(dbBean, recordNum.get(MONITOR));
			}
        	if(dbBean.isFullSync()){
        		getCourseData(dbBean, recordNum.get(COURSE));
            	getTeacherData(dbBean, recordNum.get(TEACHER));
        	}
        	if(dbBean.iscontainsTestTimeStamp()){
        		getTestTimeStampData(dbBean, recordNum.get("testtimestamp"));
        	}
        	if(dbBean.isContainsDate()){
        		if(dbBean.getDatabaseType().equals(MYSQL)){
            		getDateData(dbBean, recordNum.get(DATE));
            		getDatetimeData(dbBean, recordNum.get(DATETIME));
            		getTimestampData(dbBean, recordNum.get(TIMESTAMP));
            	} else if(dbBean.getDatabaseType().equals(SQLSERVER)){
            		getDatetimeData(dbBean, recordNum.get(DATETIME));
            	} else if(dbBean.getDatabaseType().equals(ORACLE) || dbBean.getDatabaseType().equals(POSTGRES)){
            		getDateData(dbBean, recordNum.get(DATE));
            		getTimestampData(dbBean, recordNum.get(TIMESTAMP));
            	}
        	}
        }
		WRITE_MODE = false;
		if(i == dbBeans.getDatabases().size() - 1){
			MAX_ID = maxId;
		}
	}

	public void deleteLocalFile(CheckAndAppendTestConf dbBeans){
		for (DatabaseBeanTestConf dbBean : dbBeans.getDatabases()) {
            for(TableBeanTestConf tBean : dbBean.getTables()){
            	File file = new File(CheckAndAppendDataTest.DATABASE_FILE_PREFIX + dbBean.getDatabaseName() + "/" + dbBean.getDatabaseName() + "_" + tBean.getTableName());
            	if(file.exists()){
            		file.delete();
            	}
            }
		}
	}
	public void deleteDBFile(CheckAndAppendTestConf dbBeans){
		File file = null;
		for (DatabaseBeanTestConf dbBean : dbBeans.getDatabases()) {
			for(int i = 0; i < DBDataFile.length; i++){
				file = new File(TestFileMap.get(dbBean.getDatabaseName()) + DBDataFile[i]);
				if(file.exists()){
					file.delete();
				}
			}
		}
	}
	public static void main(String []args) throws ParseException {
	}
}
