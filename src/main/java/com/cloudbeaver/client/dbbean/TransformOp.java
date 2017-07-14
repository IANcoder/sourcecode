package com.cloudbeaver.client.dbbean;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;

import com.fasterxml.jackson.annotation.JsonIgnore;

/*
 * for now, only replace operator
 */
public class TransformOp {
	//private static Logger logger = Logger.getLogger(TransformOp.class);
	private static org.apache.logging.log4j.Logger logger = LogManager.getLogger("logger");
	private static String spliter = "___";

	private String toColumn;
	private String fromTable;
	private String fromKey;
	private String fromColumns;

	@JsonIgnore
	private String[] fromColumsArry = new String[]{};
	
	@JsonIgnore
	public String getOpSqlForLoading(List<String> columns) {
		String keys = getOpKeys().stream().collect(Collectors.joining(","));
		return String.format("select %s,%s from %s", keys, columns.stream().collect(Collectors.joining(",")), fromTable);
	}

	@JsonIgnore
	public static String getSpliter() {
		return spliter;
	}

	@JsonIgnore
	public static void setSpliter(String spliter2){
		spliter = spliter2;
	}

	@JsonIgnore
	public List<String> getOpKeys(){
		return Arrays.asList(fromKey.split(spliter)).stream().map(part -> part.split("=")[0]).collect(Collectors.toList());
	}

	@JsonIgnore
	public List<String> getOpValues(){
		return Arrays.asList(fromKey.split(spliter)).stream().filter(part -> part.indexOf('=') != -1).map(part -> part.split("=")[1].trim()).collect(Collectors.toList());
	}

	public String getToColumn() {
		return toColumn;
	}
	public void setToColumn(String toColumn) {
		this.toColumn = toColumn;
	}
	public String getFromTable() {
		return fromTable;
	}
	public void setFromTable(String fromTable) {
		this.fromTable = fromTable;
	}
	public String getFromKey() {
		return fromKey;
	}
	public void setFromKey(String fromKey) {
		this.fromKey = fromKey;
	}
	public String getFromColumns() {
		return fromColumns;
	}
	public void setFromColumns(String fromColumns) {
		this.fromColumns = fromColumns;
		this.fromColumsArry = fromColumns.trim().split("\\s");
	}

	public String[] getFromColumsArry() {
		return fromColumsArry;
	}

	@Override
	public String toString() {
		return "TransformOp [toColumn=" + toColumn + ", fromTable=" + fromTable + ", fromKey=" + fromKey + ", fromColumns=" + fromColumns + "]";
	}
}
