package com.cloudbeaver.checkAndAppend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseBeanTestConf implements Serializable, InitializingBean{
	List<TableBeanTestConf> tables = new ArrayList<TableBeanTestConf>();
    String databaseName;//数据库名
    String databaseType;//数据库类型
    JdbcTemplate jdbcTemplate;
    boolean containsLong;//是否包含整型数据表
    boolean containsDate;//是否包含日期数据表
    boolean containsLongText;//是否包含文本整型数据表
    boolean containsSubTable;//是否包含子表类型
	boolean containsReplaceOp;//是否包含ReplaceOp类型表
    boolean FullSync;//是否包含NoVersionColumn类型表
    boolean doesAppend;//是否追加数据
    int appendCount;//追加次数

    public boolean isContainsSubTable() {
		return containsSubTable;
	}

	public void setContainsSubTable(boolean containsSubTable) {
		this.containsSubTable = containsSubTable;
	}

	public boolean isContainsReplaceOp() {
		return containsReplaceOp;
	}

	public void setContainsReplaceOp(boolean containsReplaceOp) {
		this.containsReplaceOp = containsReplaceOp;
	}

	public List<TableBeanTestConf> getTables() {
		return tables;
	}

	public void setTables(List<TableBeanTestConf> tables) {
		this.tables = tables;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean isFullSync() {
		return FullSync;
	}

	public void setFullSync(boolean fullSync) {
		FullSync = fullSync;
	}

	public boolean isContainsLong() {
		return containsLong;
	}

	public void setContainsLong(boolean containsLong) {
		this.containsLong = containsLong;
	}

	public boolean isContainsDate() {
		return containsDate;
	}

	public void setContainsDate(boolean containsDate) {
		this.containsDate = containsDate;
	}

	public boolean isContainsLongText() {
		return containsLongText;
	}

	public void setContainsLongText(boolean containsLongText) {
		this.containsLongText = containsLongText;
	}

	public boolean isDoesAppend() {
		return doesAppend;
	}

	public void setDoesAppend(boolean doesAppend) {
		this.doesAppend = doesAppend;
	}

	public int getAppendCount() {
		return appendCount;
	}

	public void setAppendCount(int appendCount) {
		this.appendCount = appendCount;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.assertNotNull("database name is null", databaseName);
		Assert.assertNotEquals("database name is empty", databaseName, "");

		Assert.assertNotNull("tables is null", tables);
		Assert.assertTrue("tables is empty", tables.size() > 0);

		Assert.assertNotNull("dbType is null", databaseType);
		Assert.assertNotEquals("dbType name is empty", databaseType, "");

		Assert.assertNotNull("containsLong is null", containsLong);
		Assert.assertNotEquals("containsLong name is empty", containsLong, "");

		Assert.assertNotNull("containsDate is null", containsDate);
		Assert.assertNotEquals("containsDate name is empty", containsDate, "");

		Assert.assertNotNull("containsLongText is null", containsLongText);
		Assert.assertNotEquals("containsLongText name is empty", containsLongText, "");

		Assert.assertNotNull("containsSubTable is null", containsSubTable);
		Assert.assertNotEquals("containsSubTable name is empty", containsSubTable, "");

		Assert.assertNotNull("containsReplaceOp is null", containsReplaceOp);
		Assert.assertNotEquals("containsReplaceOp name is empty", containsReplaceOp, "");

		Assert.assertNotNull("FullSync is null", FullSync);
		Assert.assertNotEquals("FullSync name is empty", FullSync, "");

		Assert.assertNotNull("doesAppend is null", doesAppend);
		Assert.assertNotEquals("doesAppend name is empty", doesAppend, "");

		Assert.assertNotNull("appendCount is null", appendCount);
		Assert.assertNotEquals("appendCount name is empty", appendCount, "");
	}
}
