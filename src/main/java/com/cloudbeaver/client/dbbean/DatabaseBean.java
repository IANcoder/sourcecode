package com.cloudbeaver.client.dbbean;

import org.javatuples.Triplet;
import org.junit.Assert;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.cloudbeaver.client.common.BeaverFatalException;
import com.cloudbeaver.client.common.BeaverUtils;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * bean for one database
 */
public class DatabaseBean implements Serializable, InitializingBean {
    List<TableBean> tables = new ArrayList<TableBean>();
    String accessTime;
    String databaseName;

    @JsonIgnore
    String databaseType;

    @JsonIgnore
    JdbcTemplate jdbcTemplate;

    @JsonIgnore
    BeaverUtils.DBType enumDBType;

    @JsonIgnore
    Map<String, String> extraParams;

    public DatabaseBean() {
	}

    @JsonIgnore
	private ConcurrentHashMap<Triplet<String, String, String>, String> opTableCopy = new ConcurrentHashMap<>();

    @JsonIgnore
	public String getOpTableValue(String tableName, String rowKey, String columnName) {
		return opTableCopy.get(new Triplet<String, String, String>(tableName, rowKey, columnName));
	}

	public void putOpTableValue(String tableName, String rowKey, String columnName, String value) {
		Triplet<String, String, String> key = new Triplet<String, String, String>(tableName, rowKey, columnName);
		if (value != null && (!opTableCopy.contains(key) || !opTableCopy.get(key).equals(value))) {
			opTableCopy.put(key, value);
		}
	}

    @ConstructorProperties({"databaseType"})
    public DatabaseBean(String databaseType) {
		this.databaseType = databaseType;
	}

    @JsonIgnore
    public DatabaseBean fork(TableBean tBean){
    	DatabaseBean dBean = new DatabaseBean(databaseType);
    	dBean.setDatabaseName(databaseName);
    	dBean.setEnumDBType(enumDBType);
    	dBean.setExtraParams(extraParams);
    	dBean.setJdbcTemplate(jdbcTemplate);
    	List<TableBean> tableBeans = new ArrayList<>();
    	tableBeans.add(tBean);
    	dBean.setTables(tableBeans);

    	return dBean;
    }

	public Map<String, String> getExtraParams() {
		return extraParams;
	}

	public void setExtraParams(Map<String, String> extraParams) {
		this.extraParams = extraParams;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String dbName) {
		this.databaseName = dbName;
	}

	public BeaverUtils.DBType getEnumDBType() {
		return enumDBType;
	}

	public void setEnumDBType(BeaverUtils.DBType enumDBType) {
		this.enumDBType = enumDBType;
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String dbType) throws BeaverFatalException {
		this.databaseType = dbType;

		setEnumDBType(BeaverUtils.DBType.getDBTypeByTypeName(dbType));
	}

    public List<TableBean> getTables() {
        return tables;
    }

    public void setTables(List<TableBean> tables) {
        this.tables = tables;
    }

	public String getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(String accessTime) {
		this.accessTime = accessTime;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.assertNotNull("database name is null", databaseName);
		Assert.assertNotEquals("database name is empty", databaseName, "");

		Assert.assertNotNull("tables is null", tables);
//		Assert.assertTrue("tables is empty", tables.size() > 0);

		Assert.assertNotNull("dbType is null", databaseType);
	}

	@Transactional
	public void executeInsert(String ... insertSql) {
		for (String insert : insertSql) {
			getJdbcTemplate().execute(insert);
		}		
	}
}
