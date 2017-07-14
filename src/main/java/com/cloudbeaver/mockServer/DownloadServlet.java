package com.cloudbeaver.mockServer;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cloudbeaver.client.dbbean.MultiDatabaseBean;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/business/monitor/SyncStatus/download/*")
public class DownloadServlet extends HttpServlet{
	//private static Logger logger = LogManager.getLogger(DownloadServlet.class);
	private static Logger logger = LogManager.getLogger("logger");
	private static String clientId = null;
	private static MultiDatabaseBean databaseBeans;
	private static String getTaskApi = "/api/business/monitor/SyncStatus/download/";

	public static String AllDBInitJsonForMockTest = GetTaskServlet.AllDBInitJsonForMockTest;

	public static MultiDatabaseBean getMultiDatabaseBean() throws JsonParseException, JsonMappingException, IOException{
		if(databaseBeans == null && clientId != null){
			ObjectMapper oMapper = new ObjectMapper();			
			databaseBeans = oMapper.readValue(AllDBInitJsonForMockTest, MultiDatabaseBean.class);
			GetTaskServlet.setMultiDatabaseBean(databaseBeans);
		}
		return databaseBeans;
	}

	public static void setMultiDatabaseBean(MultiDatabaseBean dbs) {
		databaseBeans = dbs;
	}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
    	String url = req.getRequestURI();
    	int tableIdIndex = url.lastIndexOf('/');
    	if (url.length() < getTaskApi.length() || tableIdIndex != (getTaskApi.length() - 1)) {
			throw new ServletException("invalid url, format: " + getTaskApi + "{tableId}");
		}

    	System.out.println("start get task succeed!");

    	clientId = url.substring(tableIdIndex + 1);
    	String json;
    	databaseBeans = getMultiDatabaseBean();    	
		ObjectMapper oMapper = new ObjectMapper();
		json = oMapper.writeValueAsString(databaseBeans);
		logger.info("task from server："+json);

    	resp.setCharacterEncoding("utf-8");
    	PrintWriter pw = resp.getWriter();
        pw.write(json);
        pw.flush();
        pw.close();
        System.out.println("get task succeed!");
    }
}
