package com.cloudbeaver.checkAndAppend;

import java.util.List;

import javax.swing.Spring;

import org.junit.Assert;
import org.springframework.beans.factory.InitializingBean;

public class CheckAndAppendTestConf implements InitializingBean{
	private List<DatabaseBeanTestConf> databases;
	private boolean NewVersion;//true为新版本，false为老版本
	private String Path;
	private String cmd;
	public boolean isNewVersion() {
		return NewVersion;
	}
	public void setCmd(String cmd)
	{
		this.cmd = cmd;
	}
	public String getCmd()
	{
		return cmd;
	}
    public void setPath(String path){
    	Path = path;
    }
    public String getPath()
    {
    	return Path;
    }
	public void setNewVersion(boolean newVersion) {
		NewVersion = newVersion;
	}

	public List<DatabaseBeanTestConf> getDatabases() {
		return databases;
	}

	public void setDatabases(List<DatabaseBeanTestConf> databases) {
		this.databases = databases;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.assertNotNull("databases is null", databases);
		Assert.assertTrue("No database!", databases.size() > 0);
		Assert.assertNotNull("NewVersion is null", NewVersion);
		Assert.assertNotEquals("NewVersion name is empty", NewVersion, "");
	}
}
