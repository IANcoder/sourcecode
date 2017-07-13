package com.cloudbeaver.checkAndAppend;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.springframework.beans.factory.InitializingBean;

import com.cloudbeaver.client.dbbean.TransformOp;

public class TableBeanTestConf implements InitializingBean{
	private String tableName;//表名字
    private String versionColumn = "";
    private String minVersion = "0";
    private List<String> join;
    private List<String> join_subtable;
    private String key;
    private String tableUploadUrl;
    private List<TransformOp> replaceOp = new ArrayList<>();
    int initNum;//初始表数据
    int appendNum;//追加记录数

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getVersionColumn() {
		return versionColumn;
	}

	public void setVersionColumn(String versionColumn) {
		this.versionColumn = versionColumn;
	}

	public String getMinVersion() {
		return minVersion;
	}

	public void setMinVersion(String minVersion) {
		this.minVersion = minVersion;
	}

	public List<String> getJoin() {
		return join;
	}

	public void setJoin(List<String> join) {
		this.join = join;
	}

	public List<String> getJoin_subtable() {
		return join_subtable;
	}

	public void setJoin_subtable(List<String> join_subtable) {
		this.join_subtable = join_subtable;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTableUploadUrl() {
		return tableUploadUrl;
	}

	public void setTableUploadUrl(String tableUploadUrl) {
		this.tableUploadUrl = tableUploadUrl;
	}

	public List<TransformOp> getReplaceOp() {
		return replaceOp;
	}

	public void setReplaceOp(List<TransformOp> replaceOp) {
		this.replaceOp = replaceOp;
	}

	public int getInitNum() {
		return initNum;
	}

	public void setInitNum(int initNum) {
		this.initNum = initNum;
	}

	public int getAppendNum() {
		return appendNum;
	}

	public void setAppendNum(int appendNum) {
		this.appendNum = appendNum;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.assertNotNull("table name is null", tableName);
		Assert.assertNotEquals("table name is empty", tableName, "");

		Assert.assertNotNull("versionColumn is null", versionColumn);
		Assert.assertNotEquals("versionColumn is empty", versionColumn, "");

		Assert.assertNotNull("tableUploadUrl is null", tableUploadUrl);
		Assert.assertNotEquals("tableUploadUrl is empty", tableUploadUrl, "");

		Assert.assertNotNull("initNum is null", initNum);
		Assert.assertNotEquals("initNum is empty", initNum, "");

		Assert.assertNotNull("appendNum is null", appendNum);
		Assert.assertNotEquals("appendNum is empty", appendNum, "");
	}
}
