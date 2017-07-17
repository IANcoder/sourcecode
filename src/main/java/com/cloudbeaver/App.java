package com.cloudbeaver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import com.cloudbeaver.checkAndAppend.CheckAndAppendDataTest;

/**
 * Hello world!
 *
 */
public class App 
{
	static {
		try{
			PropertyConfigurator.configure("conf_test/log4j.properties");
	    	File file = new File("conf_test/log4j2.xml");
	        ConfigurationSource source = new ConfigurationSource(new FileInputStream(file));
	        Configurator.initialize(null, source);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

    public static void main( String[] args ) throws IOException
    {
        CheckAndAppendDataTest test = new CheckAndAppendDataTest();
        test.start();
    }
}