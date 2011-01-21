package Gigabyte;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BHEngine.GodGenerator;
import BHEngine.UberConnection;

public class Gigawatch extends HttpServlet implements Runnable {
	Connection con;
	Thread Gigabyte;
	static String url = "jdbc:mysql://localhost:3306/bhdb";
	static String user = "root";
	static String pass = "battlehard";
	public void doGet(HttpServletRequest req, HttpServletResponse res) 
	throws IOException, ServletException {
	/*	res.setContentType("text/html");

		PrintWriter out = res.getWriter();
		out.print("here");
		System.out.println("I got a request2.");
		String reqtype = req.getParameter("reqtype");
		System.out.println(reqtype);
		if(req.getParameter("reqtype").equals("runTests")) {
			// okay so now we run the tests. 
			try {
				System.out.println("Running the test.");
		String UN = req.getParameter("UN");
		String Pass = req.getParameter("Pass");
		
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select pid,password from player where username = \"" + UN+"\"");
		if(rs.next()) {

		if(rs.getString(2).equals(Pass)) {
			String toExec =("java -cp " + Gigarun.bhengbindirectory + ":" + Gigarun.bhengbindirectory +"mysql-connector-java-5.1.11-bin.jar:" + Gigarun.apachedirectory+"lib/:"+ Gigarun.apachedirectory + "lib/commons-httpclient-3.1.jar:" + Gigarun.apachedirectory + "lib/servlet-api.jar:" + Gigarun.apachedirectory + "lib/commons-logging-1.1.1.jar:" + Gigarun.apachedirectory + "lib/commons-codec-1.4.jar Gigabyte.Gigarun "+rs.getInt(1)
					);
			System.out.println(toExec);
		Runtime.getRuntime().exec(toExec);*/
			//java -cp /usr/share/apache-tomcat-6.0.28/webapps/AIWars/WEB-INF/classes/:/usr/share/apache-tomcat-6.0.28/webapps/AIWars/WEB-INF/classes/mysql-connector-java-5.1.11-bin.jar:/usr/share/apache-tomcat-6.0.28/lib:/usr/share/apache-tomcat-6.0.28/lib/commons-httpclient-3.1.jar:/usr/share/apache-tomcat-6.0.28/lib/servlet-api.jar Gigabyte.Gigarun 1
			
			
		}
		public void init() {
			
			 try {
				Class.forName("com.mysql.jdbc.Driver");
				 con =
	                   DriverManager.getConnection(
	                               url,user, pass);	
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Gigabyte = new Thread(this);
			Gigabyte.start();
		      
	
	}
		public void run() {
		/*	Statement stmt=null;
			try {
				 stmt = con.createStatement();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for(;;) {
				try {

					Gigabyte.sleep((long) (GodGenerator.gameClockFactor*1000));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					
					stmt.execute("update God set gameClock=gameClock+1");
					
				} catch(SQLException exc) { exc.printStackTrace(); }
			}*/
		}
	
	/*public void init() {
		try {
			   Class.forName("com.mysql.jdbc.Driver");
			       con =
			                     DriverManager.getConnection(
			                                 Gigarun.url,Gigarun.user, Gigarun.pass);
	               Statement stmt = con.createStatement();
	               System.out.println("Gigabyte online.");
	               stmt.close();
	} catch(ClassNotFoundException exc) {exc.printStackTrace();} catch(SQLException exc) {exc.printStackTrace(); }
		
	}*/
}
