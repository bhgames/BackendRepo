package BHEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class UberConnection  {

	private Connection con;
	private GodGenerator God;
	private ArrayList<UberStatement> stmtPool = new ArrayList<UberStatement>();
	private ArrayList<UberPreparedStatement> prepStmtPool = new ArrayList<UberPreparedStatement>();

	public UberConnection(String url, String user, String pass, GodGenerator God) {
		// TODO Auto-generated constructor stub
		this.God=God;
		try {
		   Class.forName("com.mysql.jdbc.Driver");
	       con =
	                     DriverManager.getConnection(
	                                 url,user, pass);
		} catch(ClassNotFoundException exc) { exc.printStackTrace(); } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void memoryLeakDetector() {
		// This bastardo KILLS any statement that has been fucking open for more than 10 server ticks.
		// These are MEMORY LEAKS and KILL GOD. AND if you close the statement to a resultset,
		// THEN THE RESULTSET AUTOCLOSES AS WELL! AS WE'VE WITNESSED BY ERROR - CANNOT DO THIS 
		// WHILE RESULTSET IS CLOSED.
		// And so we do.
		try {
		int i = 0;

		UberStatement s;
		int gameClock = God.gameClock;
		while(i<stmtPool.size()) {
			s = stmtPool.get(i);
			
				if(s.isTaken()&&s.getLastOpenedTick()<(gameClock-10)) {
					// fuck this shit.
			//		System.out.println("releasing "+ stmtPool.get(i) + " due to a memory leak. It was opened at " + stmtPool.get(i).getLastOpenedTick() + " and gameClock is now " + God.gameClock);
					stmtPool.get(i).close(); // by doing this we don't kill it immediately, it has more time to be taken, maybe?
				//	stmtPool.remove(i);
					//i--;
					
				}
			
			i++;
		}
		 i = 0;

		UberPreparedStatement ps;
		 gameClock = God.gameClock;
		while(i<prepStmtPool.size()) {
			ps = prepStmtPool.get(i);
			
				if(ps.isTaken()&&ps.getLastOpenedTick()<(gameClock-10)) {
					// fuck this shit.
			//		System.out.println("releasing "+ stmtPool.get(i) + " due to a memory leak. It was opened at " + stmtPool.get(i).getLastOpenedTick() + " and gameClock is now " + God.gameClock);
					prepStmtPool.get(i).close(); // by doing this we don't kill it immediately, it has more time to be taken, maybe?
				//	stmtPool.remove(i);
					//i--;
					
				}
			
			i++;
		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized UberStatement createStatement() throws SQLException {
		int i = 0; boolean found=false; int extraCounter=0; int index = 0;
		
			
		while(i<stmtPool.size()) {
			if(found) extraCounter++;
			if(!stmtPool.get(i).isTaken()&&!found) {
				index = i;
				found = true;
			}
		
			i++;
		}
		
		if(found) {
			
			UberStatement stmt=	stmtPool.get(index);
			stmt.open();
			
			double frac = ((double) extraCounter)/((double) stmtPool.size());
		//	System.out.println("frac is " + frac);
			if(frac>.2) {
				// need to close some connections;
				int numToClose = (int) Math.round((frac/2.0)*((double) stmtPool.size()));
				extraCounter=0;
		//		System.out.println("closing " + numToClose);
				
				while(extraCounter<numToClose) {
			//		System.out.println("Trying");
					i = 0;
					boolean noneClosed=true;
					while(i<stmtPool.size()) {
					
						if(!stmtPool.get(i).isTaken()) {
				//			System.out.println("destroying "+ stmtPool.get(i));
							stmtPool.get(i).destroy();
							stmtPool.remove(i);
							i--;
							noneClosed=false;
							extraCounter++;
						}
						i++;
					}
					
					if(noneClosed) break;
					
				
				}
			}
			
			return stmt;
			
		} else {
		//	System.out.println("Adding a statement. stmtPool is " + stmtPool.size());
			stmtPool.add(new UberStatement(con.createStatement(),God));
			stmtPool.get(stmtPool.size()-1).open();
			return stmtPool.get(stmtPool.size()-1);
		}
		
		
	}
	public synchronized UberPreparedStatement createStatement(String theStmt) throws SQLException {
		int i = 0; boolean found=false; int extraCounter=0; int index = 0;
		
			int totalStmtsOfThisType=0;
		while(i<prepStmtPool.size()) {
			if(found&&prepStmtPool.get(i).isStatement(theStmt)) {
				extraCounter++;
				totalStmtsOfThisType++;

			}
			if(!found&&!prepStmtPool.get(i).isTaken()&&prepStmtPool.get(i).isStatement(theStmt)) {
				index = i;
				found = true;
				totalStmtsOfThisType++;
			}
		
			i++;
		}
		
		if(found) {
			
			UberPreparedStatement stmt=	prepStmtPool.get(index);
		//	System.out.println("Opening the stmt " + stmt.myString + " but was looking for " + theStmt);
			stmt.open();
			
			double frac = ((double) extraCounter)/((double) totalStmtsOfThisType);
		//	System.out.println("frac is " + frac);
			if(frac>.2) {
				// need to close some connections;
				int numToClose = (int) Math.round((frac/2.0)*((double) totalStmtsOfThisType));
				extraCounter=0;
		//		System.out.println("closing " + numToClose);
				
				while(extraCounter<numToClose) {
			//		System.out.println("Trying");
					i = 0;
					boolean noneClosed=true;
					while(i<prepStmtPool.size()) {
					
						if(!prepStmtPool.get(i).isTaken()&&prepStmtPool.get(i).isStatement(theStmt)) {
				//			System.out.println("destroying "+ stmtPool.get(i));
							prepStmtPool.get(i).destroy();
							prepStmtPool.remove(i);
							i--;
							noneClosed=false;
							extraCounter++;
						}
						i++;
					}
					
					if(noneClosed) break;
					
				
				}
			}
			
			return stmt;
			
		} else {
		//	System.out.println("Adding a statement. stmtPool is " + stmtPool.size());
		//	System.out.println("Adding a new statement for " + theStmt);
			prepStmtPool.add(new UberPreparedStatement(con.prepareStatement(theStmt),God,theStmt));
			prepStmtPool.get(prepStmtPool.size()-1).open();
			return prepStmtPool.get(prepStmtPool.size()-1);
		}
		
		
	}
	public void close() throws SQLException {
		int i = 0;
		
		while(i<stmtPool.size()) {
			stmtPool.get(i).destroy();
			i++;
		}
		for(UberPreparedStatement p: prepStmtPool) {
			p.destroy();
		}
		con.close();
	}
	public String getPoolData() throws SQLException {
		String toRet = " Pool size is: " + stmtPool.size();
		int i = 0; int trueCtr=0;
		while(i<stmtPool.size()) {
			if(stmtPool.get(i).isTaken()) trueCtr++;
			i++;
		}
		toRet+=". And taken is: " +trueCtr;
		return toRet;
	}
}
