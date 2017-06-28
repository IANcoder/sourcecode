package com.cloudbeaver.mockServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@WebServlet("/api/auth/login")
public class LoginServlet  extends HttpServlet{
	private static Logger logger = Logger.getLogger(LoginServlet.class);
	private static final String TOKEN = "token";
	private static final String ID = "id";

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
    	BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
    	StringBuilder sb = new StringBuilder();
    	String tmp;
    	while ((tmp = br.readLine()) != null) {
			sb.append(tmp);
		}
    	System.out.println(sb.toString());

    	String token = "35dc02a0-1f4e-11e6-9597-973a58e6df04";
    	long id = 0;
    	resp.setCharacterEncoding("utf-8");
    	PrintWriter pw = resp.getWriter();
    	String json = String.format("{\"%s\":\"%s\",\"%s\":%s}", TOKEN, token, ID, id);
        pw.write(json);
        pw.flush();
        pw.close();
        System.out.println("login succeed!");
    }
}
