package com.cloudbeaver.checkAndAppend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;

import com.cloudbeaver.client.common.BeaverFatalException;

import com.cloudbeaver.client.dbbean.DatabaseBean;
import com.cloudbeaver.client.dbbean.TableBean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CheckResults {
	protected Logger logger = Logger.getLogger(CheckResults.class.getName());

	public static final String DATABASE_FILE_PREFIX = "/tmp/db/";
	public static String VERSION_COLUMN_OFFSET = null;
	public static final String RECORD = "record";
	public static String SQLSERVER = "sqlserver";
	public static String ORACLE = "oracle";
	public static String MYSQL = "mysql";
	public static String POSTGRES = "postgres";
	public static final String FULL_SCAN_VERSION_COLUMN = "TOTAL_LINES_NUMBER";
	public static long EIGHT_HOURS = 8 * 3600;
	public static ArrayList<String> StringTable = new ArrayList<String>();
	{
		StringTable.add("consumer");
	}

	public void checkDBContentForTest(boolean isNewVersion, DatabaseBeanTestConf dbBean) throws SQLException, BeaverFatalException{
    	List<TableBeanTestConf> tables = dbBean.getTables();
    	for(int j=0;j<tables.size();j++)
    	{
    		TableBeanTestConf tableBean = tables.get(j);
    		if(tableBean.getTableName().equals("course")||tableBean.getTableName().equals("teacher")||tableBean.getTableName().equals("datetime_test")||tableBean.getTableName().equals("consumer"))
    			continue;
    		if(isNewVersion){
    			if(dbBean.getDatabaseType().equals(POSTGRES)){
    				VERSION_COLUMN_OFFSET = "version_column_offset";
    			} else {
    				VERSION_COLUMN_OFFSET = "VERSION_COLUMN_OFFSET";
    			}
    		} else {
    			VERSION_COLUMN_OFFSET = tableBean.getVersionColumn();
    		}

    		String versionColumn = tableBean.getVersionColumn();
    		JSONArray fileArray = readJsonFile(dbBean.getDatabaseType(), tableBean.getTableName(), DATABASE_FILE_PREFIX + dbBean.getDatabaseName() + "/" + dbBean.getDatabaseName() + "_" + tableBean.getTableName());
    		if(fileArray == null){
    			throw new BeaverFatalException("no content from db!");
    		}
    		//check repeated versionColumn
//		    		if(!tableBean.getVersionColumn().equals(SimpleSQLDBHandler.FULL_SCAN_VERSION_COLUMN) && !dbBean.getDatabaseType().equals(DbUploaderTest.WEBSERVICE)){
//			    		if(!checkRepeatedVersionColumn(fileArray, tableBean)){
//			    			try (RandomAccessFile file = new RandomAccessFile("/home/beaver/repeatedTable.txt", "rw")) {
//			    				String string = "Version Column is repeated! DatabaseName = " + dbBean.getDatabaseName() + ", TableName = " + tableBean.getTableName() + "\n";
//			    				file.seek(file.length());
//			    				file.write(string.getBytes());
//			    				file.close();
//			    			} catch (IOException e) {
//			    				e.printStackTrace();
//			    			}
//			    		}
//			    	}
    		//compare result with db file
    		int i = 0;
    		if(DBDataGeneration.TestFileMap.containsKey(dbBean.getDatabaseName())){
    			JSONArray dbArray = readDataFile(dbBean.getDatabaseType(), tableBean.getTableName(), DBDataGeneration.TestFileMap.get(dbBean.getDatabaseName()) + tableBean.getTableName());
    			if(dbArray.size() != fileArray.size()){
    				logger.info("Database: " + dbBean.getDatabaseName() + ", Table: " + tableBean.getTableName() + ". Record num in file is not equal to that in DB");
    				throw new BeaverFatalException("Record num in file is not equal to that in DB");
//    				continue;
    			}
    			long startTime = System.currentTimeMillis();
    			if(!versionColumn.equals(FULL_SCAN_VERSION_COLUMN)){
	    			for(i = 0; i < dbArray.size(); i++){
	    				if(tableBean.getTableName().toUpperCase().equals("CONSUMER")){
	    					String str = String.valueOf(dbArray.getJSONObject(i).get(VERSION_COLUMN_OFFSET));
	    					if(!str.substring(1, str.length()-1).equals(fileArray.getJSONObject(i).get(VERSION_COLUMN_OFFSET))){
		    					logger.info("Database: " + dbBean.getDatabaseName() + ", Table: " + tableBean.getTableName() + ". VersionColumn in file is not equal to that in DB");
		    					throw new BeaverFatalException("VersionColumn in file is not equal to that in DB");
//		    					break;
		    				}
	    				}else {
		    				if(!String.valueOf(dbArray.getJSONObject(i).get(VERSION_COLUMN_OFFSET)).equals(fileArray.getJSONObject(i).get(VERSION_COLUMN_OFFSET))){
		    					logger.info("Database: " + dbBean.getDatabaseName() + ", Table: " + tableBean.getTableName() + ". VersionColumn in file is not equal to that in DB");
		    					throw new BeaverFatalException("VersionColumn in file is not equal to that in DB");
//		    					break;
		    				}
	    				}
	    			}
	    			System.out.println("in check tabel name is "+tableBean.getTableName());
	    			System.out.println("i is "+i+" and dbarry size is"+ dbArray.size());
	    			if(i >= dbArray.size()){
	    				logger.info("-----------------------" + dbBean.getDatabaseName() + ":" + tableBean.getTableName() + " check finish------------------------");
	    			}
    			}else {
    				logger.info("-----------------------" + dbBean.getDatabaseName() + ":" + tableBean.getTableName() + " check finish------------------------");
    			}
    			long endTime = System.currentTimeMillis();
            	System.out.println("compare versionColumnValue：" + (endTime - startTime)/1000 + "s");
    		}
    	}
    }

	public void checkDBContent(boolean isNewVersion, DatabaseBean dbBean) throws SQLException, BeaverFatalException{
    	List<TableBean> tables = dbBean.getTables();
    	for (TableBean tableBean : tables){
    		if(isNewVersion){
    			if(dbBean.getDatabaseType().equals(POSTGRES)){
    				VERSION_COLUMN_OFFSET = "version_column_offset";
    			} else {
    				VERSION_COLUMN_OFFSET = "VERSION_COLUMN_OFFSET";
    			}
    		} else {
    			VERSION_COLUMN_OFFSET = tableBean.getVersionColumn();
    		}

    		String versionColumn = tableBean.getVersionColumn();
    		JSONArray fileArray = readJsonFile(dbBean.getDatabaseType(), tableBean.getTableName(), DATABASE_FILE_PREFIX + dbBean.getDatabaseName() + "/" + dbBean.getDatabaseName() + "_" + tableBean.getTableName());
    		System.out.println("database dir is"+DATABASE_FILE_PREFIX + dbBean.getDatabaseName());
    		if(fileArray == null){
    			throw new BeaverFatalException("no content from db!");
    		}
    		int i = 0;
    		if(DBDataGeneration.TestFileMap.containsKey(dbBean.getDatabaseName())){
    			JSONArray dbArray = readDataFile(dbBean.getDatabaseType(), tableBean.getTableName(), DBDataGeneration.TestFileMap.get(dbBean.getDatabaseName()) + tableBean.getTableName());
    			if(dbArray.size() != fileArray.size()){
    				logger.info("Database: " + dbBean.getDatabaseName() + ", Table: " + tableBean.getTableName() + ". Record num in file is not equal to that in DB");
    				throw new BeaverFatalException("Record num in file is not equal to that in DB");
//    				continue;
    			}
    			long startTime = System.currentTimeMillis();
    			if(!versionColumn.equals(FULL_SCAN_VERSION_COLUMN)){
	    			for(i = 0; i < dbArray.size(); i++){
	    				if(tableBean.getTableName().toUpperCase().equals("CONSUMER")){
	    					String str = String.valueOf(dbArray.getJSONObject(i).get(VERSION_COLUMN_OFFSET));
	    					if(!str.substring(1, str.length()-1).equals(fileArray.getJSONObject(i).get(VERSION_COLUMN_OFFSET))){
		    					logger.info("Database: " + dbBean.getDatabaseName() + ", Table: " + tableBean.getTableName() + ". VersionColumn in file is not equal to that in DB");
		    					throw new BeaverFatalException("VersionColumn in file is not equal to that in DB");
//		    					break;
		    				}
	    				}else {
		    				if(!String.valueOf(dbArray.getJSONObject(i).get(VERSION_COLUMN_OFFSET)).equals(fileArray.getJSONObject(i).get(VERSION_COLUMN_OFFSET))){
		    					logger.info("Database: " + dbBean.getDatabaseName() + ", Table: " + tableBean.getTableName() + ". VersionColumn in file is not equal to that in DB");
		    					throw new BeaverFatalException("VersionColumn in file is not equal to that in DB: " + String.valueOf(dbArray.getJSONObject(i).get(VERSION_COLUMN_OFFSET)) + "," + fileArray.getJSONObject(i).get(VERSION_COLUMN_OFFSET));
//		    					break;
		    				}
	    				}
	    			}
	    			if(i >= dbArray.size()){
	    				logger.info("-----------------------" + dbBean.getDatabaseName() + ":" + tableBean.getTableName() + " check finish------------------------");
	    			}
    			}else {
    				logger.info("-----------------------" + dbBean.getDatabaseName() + ":" + tableBean.getTableName() + " check finish------------------------");
    			}
    			long endTime = System.currentTimeMillis();
            	System.out.println("compare versionColumnValue：" + (endTime - startTime)/1000 + "s");
    		}
    	}
    }

	public static void checkIncreaseVersionColumn(JSONArray jArray) {
		long versionColumn = Long.parseLong(jArray.getJSONObject(0).getString(VERSION_COLUMN_OFFSET));
		for(int i = 1; i < jArray.size(); i++){
			Assert.assertTrue("Database: " + jArray.getJSONObject(i).getString("hdfs_db") + ", Table: " + jArray.getJSONObject(i).getString("hdfs_table") + ". VersionColumn is not increasing",
					Long.parseLong(jArray.getJSONObject(i).getString(VERSION_COLUMN_OFFSET)) >= versionColumn);
			versionColumn = Long.parseLong(jArray.getJSONObject(i).getString(VERSION_COLUMN_OFFSET));
		}
	}

	public static boolean checkRepeatedVersionColumn(JSONArray jArray, TableBean tableBean){
    	List<String> list = new ArrayList<String>();
    	for(int i = 0; i < jArray.size(); i++){
    		String versionColumn = jArray.getJSONObject(i).getString(tableBean.getVersionColumn());
    		if(list.contains(versionColumn)){
    			return false;
    		} else {
    			list.add(versionColumn);
    		}
    	}
    	return true;
    }

	public static JSONArray readJsonFile(String dbType, String tableName, String fileName){
		System.out.println("file name is "+fileName);
    	File file = new File(fileName);
    	System.out.println("file name is "+file.getName());
    	if(!file.exists()){
    		return null;
    	}
        BufferedReader reader = null;
        JSONArray jArray = new JSONArray();

        long startTime = System.currentTimeMillis();

        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            System.out.println("column offset is "+VERSION_COLUMN_OFFSET);
            while ((tempString = reader.readLine()) != null) {
            	JSONObject jsonObj = new JSONObject();
            	String subStr = tempString.substring(tempString.indexOf(VERSION_COLUMN_OFFSET));
            	String value = subStr.substring(VERSION_COLUMN_OFFSET.length() + 3, subStr.indexOf(",") - 1);
            	jsonObj.put(VERSION_COLUMN_OFFSET, value);
            	jsonObj.put(RECORD, tempString);
            	jArray.add(jsonObj);
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

        long endTime = System.currentTimeMillis();
    	System.out.println("read data get from db：" + (endTime - startTime)/1000 + "s");

        if(jArray.size() == 0){
        	return null;
        } else {
        	startTime = System.currentTimeMillis();
        	sortJSONArray(dbType, tableName, jArray);
        	endTime = System.currentTimeMillis();
        	System.out.println("sort jsonArray：" + (endTime - startTime)/1000 + "s");

        	return jArray;
        }   
    }

	public static JSONArray readDataFile(String dbType, String tableName, String fileName){
		JSONArray jArray = new JSONArray();
    	File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            String versionColumn = reader.readLine();
            int versionColumnNum = 0;
            tempString = reader.readLine();
            if(!versionColumn.equals(FULL_SCAN_VERSION_COLUMN)){
	            String columnNames[] = tempString.split(",");
	            for(int i = 0; i < columnNames.length; i++){
	            	if(columnNames[i].equals(versionColumn)){
	            		versionColumnNum = i;
	            		break;
	            	}
	            }
            }

            long startTime = System.currentTimeMillis();
            while ((tempString = reader.readLine()) != null) {
            	JSONObject jsonObj = new JSONObject();
            	if(!versionColumn.equals(FULL_SCAN_VERSION_COLUMN)){
            		String value = tempString.split(",")[versionColumnNum];
            		if(fileName.substring(fileName.lastIndexOf("/") + 1).equals("date_test")){
            			jsonObj.put(VERSION_COLUMN_OFFSET, changeDateToTimestamp(dbType, value.substring(1, value.length() - 1)));
            		} else if(fileName.substring(fileName.lastIndexOf("/") + 1).equals("datetime_test")){
            			jsonObj.put(VERSION_COLUMN_OFFSET, changeDatetimeToTimestamp(dbType, value.substring(1, value.length() - 1)));
            		} else if(fileName.substring(fileName.lastIndexOf("/") + 1).equals("timestamp_test")){
            			jsonObj.put(VERSION_COLUMN_OFFSET, changeTimestampToTimestamp(dbType, value.substring(1, value.length() - 1)));
            		} else{
            			jsonObj.put(VERSION_COLUMN_OFFSET, value);
            		}
            	}
            	jsonObj.put(RECORD, tempString);
            	jArray.add(jsonObj);
            }
            reader.close();
            long endTime = System.currentTimeMillis();
        	System.out.println("read origin db file：" + (endTime - startTime)/1000 + "s");

            if(!versionColumn.equals(FULL_SCAN_VERSION_COLUMN)){
            	startTime = System.currentTimeMillis();
            	sortJSONArray(dbType, tableName, jArray);
            	endTime = System.currentTimeMillis();
            	System.out.println("sort jsonArray：" + (endTime - startTime)/1000 + "s");
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return jArray;
    }

	public static void sortJSONArray(String dbType, String tableName, JSONArray jArray) {
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
	    for (int i = 0; i < jArray.size(); i++) {
	        jsonValues.add(jArray.getJSONObject(i));
	    }
	    Collections.sort( jsonValues, new Comparator<JSONObject>() {
	        @Override
	        public int compare(JSONObject a, JSONObject b) {
	        	if(dbType.equals(SQLSERVER) && tableName.equals("datetime_test")){
	        		float valA = 0, valB = 0;
		            valA = Float.parseFloat(a.getString(VERSION_COLUMN_OFFSET));
	                valB = Float.parseFloat(b.getString(VERSION_COLUMN_OFFSET));
	                if(valA > valB){
		            	return 1;
		            } else if(valA == valB){
		            	return 0;
		            } else {
		            	return -1;
		            }
	        	} else if(StringTable.contains(tableName)){
	        		String valA = "", valB = "";
		            valA = a.getString(VERSION_COLUMN_OFFSET);
	                valB = b.getString(VERSION_COLUMN_OFFSET);
	                return valA.compareTo(valB);
	        	} else {
		            long valA = 0, valB = 0;
		            valA = Long.parseLong(a.getString(VERSION_COLUMN_OFFSET));
	                valB = Long.parseLong(b.getString(VERSION_COLUMN_OFFSET));
		            if(valA > valB){
		            	return 1;
		            } else if(valA == valB){
		            	return 0;
		            } else {
		            	return -1;
		            }
	        	}
	        }
	    });
	    jArray.clear();
	    for (int i = 0; i < jsonValues.size(); i++) {
	        jArray.add(jsonValues.get(i));
	    }
	}

	public static String changeDateToTimestamp(String dbType, String d) throws ParseException{
		long timeStamp = 0;
		if(dbType.equals(MYSQL)){
			SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
		    Date date;
			date = simpleDateFormat.parse(d);
			timeStamp = date.getTime()/1000;
		} else if(dbType.equals(ORACLE)){
			String strs[] = d.split("-");
			d = "";
			if(strs[1].length() == 1){
				strs[1] = "0" + strs[1];
			}
			if(strs[2].length() == 1){
				strs[2] = "0" + strs[2];
			}
			timeStamp = Long.parseLong(strs[0] + strs[1] + strs[2] + "000000");
		}else if(dbType.equals(POSTGRES)){
			SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
		    Date date;
			date = simpleDateFormat.parse(d);
			timeStamp = date.getTime()/1000 + EIGHT_HOURS;
		}
		return String.valueOf(timeStamp);
	}

	public static String changeDatetimeToTimestamp(String dbType, String d) throws ParseException {
		SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date date;
		date = simpleDateFormat.parse(d);
		if(dbType.equals(MYSQL)){
			long timeStamp = date.getTime()/1000;
			return String.valueOf(timeStamp);
		} else if(dbType.equals(SQLSERVER)){
			String timeStamp = daysBetween("1900-01-01 00:00:00", d);
			return timeStamp;
		} else {
			return null;
		}
		
	}

	public static Object changeTimestampToTimestamp(String dbType, String d) throws ParseException {
		if(dbType.equals(MYSQL)){
			d = d.substring(0, 4) + "-" + d.substring(4, 6) + "-" + d.substring(6, 8) + " " + d.substring(8, 10) + ":" + d.substring(10, 12) + ":" + d.substring(12, 14);
			long timeStamp = 0;
			SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date date;
			date = simpleDateFormat.parse(d);
			timeStamp = date.getTime()/1000;
			return String.valueOf(timeStamp);
		} else if(dbType.equals(ORACLE)){
			return d.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		}else if(dbType.equals(POSTGRES)){
			long timeStamp = 0;
			SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date date;
			date = simpleDateFormat.parse(d);
			timeStamp = date.getTime()/1000 + EIGHT_HOURS;
			return String.valueOf(timeStamp);
		}else {
			return null;
		}
	}

	public static String daysBetween(String smdate,String bdate) throws ParseException{
		DecimalFormat decimalFormat=new DecimalFormat(".00");
		int scale = 2;
		int roundingMode = 4;
		
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
		date = sdf.parse(smdate);
        long time1 = date.getTime();
        date = sdf.parse(bdate);
        long time2 = date.getTime();
        long between_days=(time2-time1)/(1000*3600*24);
        float part = Float.parseFloat(bdate.substring(bdate.indexOf(" ") + 1, bdate.indexOf(":")))/24;
        BigDecimal bd = new BigDecimal((double)part);
        bd = bd.setScale(scale,roundingMode);  
		part = bd.floatValue();
        return decimalFormat.format(between_days + part);
    }

	public static void main(String[] args) {
		try {
			System.out.println(daysBetween("1900-01-01 00:00:00", "1970-08-01 03:00:00"));
			System.out.println(daysBetween("1900-01-01 00:00:00", "1970-08-01 08:00:00"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
