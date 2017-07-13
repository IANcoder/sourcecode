package com.cloudbeaver;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.Loader;

import com.cloudbeaver.checkAndAppend.CheckAndAppendDataTest;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	PropertyConfigurator.configure("conf_test/log4j.properties");
    	//System.out.println("this is test");
        CheckAndAppendDataTest test = new CheckAndAppendDataTest();
        test.start();
    }
}