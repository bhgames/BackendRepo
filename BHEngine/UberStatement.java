package BHEngine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UberStatement  {
	private Statement stmt;
	private boolean taken=false;
	private int lastOpenedTick=0;
	private GodGenerator God;
	public UberStatement(Statement stmt, GodGenerator God) {
		// TODO Auto-generated constructor stub
		this.stmt=stmt;
		this.God=God;
	}
	
	public void close() throws SQLException {
	//	System.out.println(this + " was closed from when it was opened at " + lastOpenedTick + " and closes at " + God.gameClock);
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
	
	public ResultSet executeQuery(String toExec) throws SQLException {
		return stmt.executeQuery(toExec);
	}
	
	public void execute(String toExec) throws SQLException {
		 stmt.execute(toExec);
	}
	public void executeUpdate(String toExec) throws SQLException {
		 stmt.executeUpdate(toExec);
	}
	
}
