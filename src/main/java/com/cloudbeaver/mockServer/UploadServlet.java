package com.cloudbeaver.mockServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet("/api/business/monitor/SyncStatus/upload/*")
public class UploadServlet extends HttpServlet {
	public static final String DEFAULT_CHARSET = "utf-8";
	private static Logger logger = LogManager.getLogger("logger");
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
    	BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
    	StringBuilder sb = new StringBuilder();
    	String tmp;
    	while ((tmp = br.readLine()) != null) {
			sb.append(tmp);
		}
    	byte[] bs = Base64.decodeBase64(sb.toString().getBytes(DEFAULT_CHARSET));
    	String content = new String(bs,DEFAULT_CHARSET);
    	System.out.println("get post data, data:" + content);
    }
}
