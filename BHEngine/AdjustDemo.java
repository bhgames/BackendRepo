package BHEngine;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class AdjustDemo {	
static String url = "jdbc:mysql://mysql.moverang.com:3306/moverang";
static String user = "jmp3qa";
static String pass = "Awesome11";

	public static void main(String args[]) {
		try {
			   Class.forName("com.mysql.jdbc.Driver");
			     
			      Connection con =
			                     DriverManager.getConnection(
			                                 url,user, pass);
			      Statement s = con.createStatement();
			      ResultSet rs = s.executeQuery("select id from user");
			      while(rs.next()) {
			    	  System.out.println(rs.getString(1));
			    	  System.err.println(rs.getString(1));

			      }
			      System.out.println("ehere");
			      rs.close(); s.close(); con.close();
		} catch(ClassNotFoundException exc) {exc.printStackTrace();} catch(SQLException exc) {exc.printStackTrace(); }
		
	}
}
