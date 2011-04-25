package BHEngine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class UberPreparedStatement  {
	private PreparedStatement stmt;
	private boolean taken=false;
	public String myString;
	private int lastOpenedTick=0;
	private GodGenerator God;
	public UberPreparedStatement(PreparedStatement stmt, GodGenerator God, String myString) {
		// TODO Auto-generated constructor stub
		this.stmt=stmt;
		this.myString=myString;
		this.God=God;
	}
	
	public void close() throws SQLException {
	//	System.out.println(this + " was closed from when it was opened at " + lastOpenedTick + " and closes at " + God.gameClock);
		stmt.clearParameters();
		taken=false;
	}
	
	public void open() throws SQLException{
		taken = true;
		lastOpenedTick = God.gameClock;
	}
	
	public boolean isTaken() throws SQLException{
		return taken;
	}
	public void destroy() throws SQLException{
			stmt.close();
		
	}
	
	public int getLastOpenedTick() {
		return lastOpenedTick;
	}
	
	public ResultSet executeQuery() throws SQLException {
		return stmt.executeQuery();
	}
	
	public void execute() throws SQLException {
		 stmt.execute();
	}
	public void executeUpdate() throws SQLException {
		 stmt.executeUpdate();
	}
	public boolean isStatement(String stmt) {
		if(stmt.equals(myString)) return true;
		else return false;
		
		
	}
	
	public void setString(int place, String str) {
		try {
			stmt.setString(place,str);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setInt(int place, int str) {
		try {
			stmt.setInt(place,str);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}public void setLong(int place, long str) {
		try {
			stmt.setLong(place,str);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setDouble(int place, double str) {
		try {
			stmt.setDouble(place,str);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setBoolean(int place,  boolean str) {
		try {
			stmt.setBoolean(place,str);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
