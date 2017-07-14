package com.cloudbeaver.client.dbbean;

import org.apache.logging.log4j.LogManager;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.junit.Assert;
import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * root entry for beans, contains many db-beans
 */
public class MultiDatabaseBean implements InitializingBean {
	@JsonIgnore
   // private Logger logger = Logger.getLogger("logger");
	private static org.apache.logging.log4j.Logger logger = LogManager.getLogger("logger");

	private List<DatabaseBean> databases;

    public List<DatabaseBean> getDatabases() {
        return databases;
    }

    public void setDatabases(List<DatabaseBean> databases) {
        this.databases = databases;
    }

    public Map<Triplet<String, String, String>, TableBean> toMap(){
    	return databases.stream().flatMap(db -> db.getTables().stream().map(table -> new Pair<>(db.getDatabaseName(), table)))
    		.collect(Collectors.toMap(p -> new Triplet<>(p.getValue0(), p.getValue1().getTableName(), p.getValue1().getVersionColumn()), data -> data.getValue1()));
    }

    public void syncWithOthers(MultiDatabaseBean other){
        Map<Triplet<String, String, String>, TableBean> syncVersionMap = other.toMap();
        Map<Triplet<String, String, String>, TableBean> localVersionMap = toMap();
        localVersionMap.forEach((key, value) -> {if(syncVersionMap.containsKey(key)){
        	value.setMinVersion(syncVersionMap.get(key).getMinVersion());
        	value.setMaxVersion(syncVersionMap.get(key).getMaxVersion());}});
    }

    @Override
    public boolean equals(Object obj) {
    	return toMap().equals(((MultiDatabaseBean)obj).toMap());
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.assertNotNull("databases is null", databases);
		Assert.assertTrue("No database!", databases.size() > 0);

		logger.info("MultiDatabaseBean conf is correct!");
	}
}