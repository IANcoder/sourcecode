package com.cloudbeaver.client.dbbean;

import org.junit.Assert;
import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * bean for one table
 */
public class TableBean implements Serializable, InitializingBean{
    private String tableName;
    private String versionColumn = "TOTAL_LINES_NUMBER";
    private String accessTime;

    @JsonIgnore
    private static final BigDecimal versionStep = new BigDecimal(1000);

    @JsonIgnore
    private String maxVersion = "";
    @JsonIgnore
    private String minVersion = "";

    @JsonIgnore
    private List<String> join;
    @JsonIgnore
    private List<String> join_subtable;
    @JsonIgnore
    private String key;

	@JsonIgnore
    private String tableUploadUrl;

	@JsonIgnore
    private Map<String, String> extraParams;

    @JsonIgnore
    private Map<String, Integer> columnMap = new HashMap<>();

    public Map<String, Integer> getColumMap() {
		return columnMap;
	}

	public void addColumnMetaData(String columnName, int sqlType){
		columnMap.put(columnName.toLowerCase(), sqlType);
	}

	private List<TransformOp> replaceOp = new ArrayList<>();

    public List<TransformOp> getReplaceOp() {
		return replaceOp;
	}

	public void setReplaceOp(List<TransformOp> replaceOp) {
		this.replaceOp = replaceOp;
	}

	public String getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(String accessTime) {
		this.accessTime = accessTime;
	}

	public String getTableUploadUrl() {
		return tableUploadUrl;
	}

	public void setTableUploadUrl(String tableUploadUrl) {
		this.tableUploadUrl = tableUploadUrl;
	}

	public Map<String, String> getExtraParams() {
		return extraParams;
	}

	public void setExtraParams(Map<String, String> extraParams) {
		this.extraParams = extraParams;
	}

	public String getVersionColumn() {
		return versionColumn;
	}

	public void setVersionColumn(String versionColumn) {
		this.versionColumn = versionColumn;
	}

	public List<String> getJoin_subtable() {
		return join_subtable;
	}

	public void setJoin_subtable(ArrayList<String> join_subtable) {
		this.join_subtable = join_subtable;
	}

	public String getTableName() {
        return tableName;
    }

    public void setTableName(String table) {
        this.tableName = table;
    }

    public List<String> getJoin() {
        return join;
    }

    public void setJoin(ArrayList<String> join) {
        this.join = join;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMinVersion() {
		return minVersion;
	}

	public void setMinVersion(String minVersion) {
		this.minVersion = minVersion;
	}

    public String getMaxVersion() {
		return maxVersion;
	}

	public void setMaxVersion(String maxVersion) {
		this.maxVersion = maxVersion;
	}

	@JsonIgnore
    public boolean needFullScan(){
    	return versionColumn.equals("TOTAL_LINES_NUMBER");
    }

	@JsonIgnore
	public String getNextStep() {
		BigDecimal bd = new BigDecimal(minVersion);
		return bd.add(versionStep).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TableBean) {
			TableBean other = (TableBean)obj;
			return other.getTableName().equals(getTableName()) && other.getVersionColumn().equals(getVersionColumn()) 
					&& other.getMaxVersion().equals(getMaxVersion()) && other.getMinVersion().equals(getMinVersion());
		}else{
			return false;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.assertNotNull("table name is null", tableName);
		Assert.assertNotEquals("table name is empty", tableName, "");
		Assert.assertNotNull("table name is null", versionColumn);
		Assert.assertNotEquals("table name is empty", versionColumn, "");
	}

	@JsonIgnore
	public String getNextVersionOffsetWithinMax() {
		BigDecimal bd = new BigDecimal(minVersion);
		bd = bd.add(versionStep);
		if(bd.compareTo(new BigDecimal(maxVersion)) > 0){
			return maxVersion;
		}

		return bd.toString();
	}

	@JsonIgnore
	public String getSubJoinColumnName() {
		if (join_subtable != null) {
			int index = key.indexOf(tableName) + tableName.length() + 1;
			return key.substring(index, key.indexOf('=', index)).trim();
		}else{
			return null;
		}
	}

	public boolean versionColumnIsDate(){
		int type = columnMap.get(versionColumn.toLowerCase());
		return type == Types.TIMESTAMP || type == Types.DATE;
	}

	public boolean columnIsNumber(String columnName) {
		if (columnMap.containsKey(columnName)) {
			switch (columnMap.get(columnName)) {
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.SMALLINT:
			case Types.TINYINT:
			case Types.FLOAT:
			case Types.DOUBLE:
			case Types.REAL:
			case Types.DECIMAL:
			case Types.NUMERIC:
				return true;
			default:
				return false;
			}
		}else{
			return false;
		}
	}

	public boolean versionColumnIsVarchar(String columnName) {
		if (columnMap.containsKey(columnName)) {
			switch (columnMap.get(columnName)) {
			case Types.VARCHAR:
				return true;
			default:
				return false;
			}
		}else{
			return false;
		}
	}
}

