package BHEngine;

//import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;
import java.util.ArrayList;
//import java.sql.Connection;
import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;

public class League extends Player {
	private int internalLeagueClock=0;
	private String holdingLeagueIteratorID = "-1";
	private ArrayList<TaxPlayerRank> tpr;
	private String name,letters,description,website;
	private long[] secondaryResBuff; // duplicate of player, it'll get saved in league instead of player table,
	// but overall shouldn't be a problem if both league and player use a secondary res buff for lord and league taxes.
	public void createLeague(String leagueName,String leagueLetters, String description, String website, Player initial) {
		int tid[] = {towns().get(0).townID };
		TaxPlayerRank first = new TaxPlayerRank(0,initial.ID,"Admin",2,tid,this);
		first.player=initial;
		tpr = new ArrayList<TaxPlayerRank>();
		tpr.add(first);
		initial.setLeague(this);
		this.name=leagueName;this.letters=leagueLetters;this.description=description;this.website=website;
		UberPreparedStatement stmt;
		try {

	
	      stmt = con.createStatement("insert into league (pid,name,letters,website,description,mbuff,tbuff,mmbuff,fbuff) values (?,?,?,?,?,0,0,0,0);");
	      stmt.setInt(1,ID);
	      stmt.setString(2,leagueName);
	      stmt.setString(3,leagueLetters);
	      stmt.setString(4,website);
	      stmt.setString(5,description);
	      
	      // First things first. We update the player table.
	      boolean transacted=false;
	      while(!transacted) {
	    	  try {
	      
	      
	      // let's add this raid and therefore get the rid out of it.
	      
	      stmt.execute();

	      
	      //System.out.println("Transacting that shit.");
	      stmt.close(); transacted=true; }
	    	  catch(MySQLTransactionRollbackException exc) { }
	      }// need connection for attackunit adds!
		} catch(SQLException exc) { exc.printStackTrace(); }
		//calculateResIncs();

	}
	
	
	
	



	 ArrayList<TaxPlayerRank> tpr() {
		 int i = 0; boolean foundnullers=false;
		 if(tpr!=null)
		 while(i<tpr.size()) {
			 if(tpr.get(i).player==null) {
				 foundnullers=true;
				 break;
			 }
			 i++;
		 }
		 if(tpr==null||foundnullers) {
		ArrayList<TaxPlayerRank> tpr = new ArrayList<TaxPlayerRank>();

		try {
		UberPreparedStatement stmt = con.createStatement("select * from tpr where league_pid = ?;");
		stmt.setInt(1,ID);
		ResultSet getLInfo = stmt.executeQuery();
		UberPreparedStatement stmt2 = God.con.createStatement("select * from permissions where tprID = ?;");
		ResultSet getT; TaxPlayerRank curr;

		ArrayList<Integer> Permissions;
		while(getLInfo.next()) {
			Permissions = new ArrayList<Integer>();
			stmt2.setInt(1,getLInfo.getInt(1));
			getT = stmt2.executeQuery();
			while(getT.next()) {
				Permissions.add(new Integer(getT.getInt(3)));
			}
			getT.close();
			 i = 0;
			int tid[] = new int[Permissions.size()]; // so now we have their permissions loaded.
			while(i<Permissions.size()) {
				tid[i]=Permissions.get(i);
				i++;
			}
			curr = new TaxPlayerRank(getLInfo.getDouble(3),getLInfo.getInt(4),getLInfo.getString(5),getLInfo.getInt(6),tid,getLInfo.getInt(1),this);
		
			i = 0;
			 Player p=God.getPlayer(curr.pid);
			
			
			curr.player=p;
		//	if(curr.type!=-1)
			//p.setLeague(this);
			tpr.add(curr);
			
			// alright, let's try and get some info on lvls for resInc for each town league has.
			
			
			
		}
		//loadTownReferencesForPlayers();

		//System.out.println("I am alive and somewhat well, at least, and my tpr count is  " + tpr.size() + " with it's" +
			//	" shit being " + tpr.get(0).type + " and stuff.");
		getLInfo.close();stmt.close(); stmt2.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		this.tpr=tpr;
		 }
		return tpr;

	}
	public League(int ID, GodGenerator God) throws SQLException {
		super(ID, God);
		setLeagueBool(true);
		setLeagueInternalClock(God.gameClock);
        ps = new PlayerScript(this); // if you call this after you set league bool, you get a different type
        // of battlehardfunctions.

        secondaryResBuff = getMemSecondaryResBuff();
        letters = getMemLetters();
        website = getMemWebsite();
        description = getMemDescription();
        name = getMemName();
		// TODO Auto-generated constructor stub
	}

	
	public boolean isAllied(League l) {
		//will return the actual alliance of the two leagues once diplo is in place
		return false;
	}
	
	public String JSONMap() {
		// input the mapper output code here.
		return "blank";
	}
	public void checkForLeagueReference(Player p) {
		
		// this method exists to check if a player is missing his league reference, and gives it back to him.
		if(p.getLeague()==null) p.setLeague(this);
		
	}
	public void doTaxes(int num) {
		/*if(resTimer==null) {

			calculateResIncs();
			resTimer = new Timer(checkResInc*1000);
		}
		else if(resTimer.isDone()) {
			calculateResIncs();
			resTimer = new Timer(checkResInc*1000);
		}*/
		Town holdTown; TaxPlayerRank curr; Player p; Town t;

			/*
			 * Okay, granted, in the open space version, you just multiply by the fractions
			 * and add to each one. The problem is when you go over the limit.
			 * What you need to do is use the space available in each city
			 * as the sort of fraction. Then, what you do, is you load up
			 * res for each one proportional to the fraction of space AVAILABLE,
			 * and the fact is, if you've got 900 resource space available and 1000
			 * to fill, then doing 1/2 in one who has 450 and one half in the other 450
			 * you're gonna be left with losing resources. If you've got 2000 available
			 * then doing 1/3 * 1000 in each one is fine.
			 * 
			 * 
			 * 
			 */
		
		 int i = 0;
		 int totalOpenSpace[] = new int[5];
		 double newIncs[];
			double resEffects[]; long resCaps[]; double resInc[];
			long res[];
			ArrayList<Town> towns = towns();
			double resBuff[];
			
			while(i<towns.size()) {
			
			
			 int j = 0;
			 holdTown = towns.get(i);
			 resInc=holdTown.getResInc();
			 resCaps=holdTown.getResCaps();
			 res = holdTown.getRes();

					do {
						totalOpenSpace[j]+=(resCaps[j]+Building.baseResourceAmt-res[j]);
						j++;
					} while(j<res.length);
					
	
				i++;
					
				} 
		i=0;
		double tresEffects[];  double tresInc[];
	
		long[] secbuff = getSecondaryResBuff();
		 while(i<towns.size()) {
			
			
			 int j = 0;
			 holdTown = towns.get(i);
			 resInc=holdTown.getResInc();
			 resEffects=holdTown.getResEffects();
			 resCaps=holdTown.getResCaps();
			 resBuff = holdTown.getResBuff();
			 res = holdTown.getRes();
			 ArrayList<Town> ptowns;
			 
				// we add the secondary stuff. It'll get added completely, the fractions add to one.
			 synchronized(resBuff) {
				 double befBuff = resBuff[j];
			do {
			//	System.out.println(resBuff[j] + " before");
				double multiplier = 1;
				if(getMineTimer()>0&&j==0) multiplier*=1.25;
				if(getTimberTimer()>0&&j==1) multiplier*=1.25;
				if(getMmTimer()>0&&j==2) multiplier*=1.25;
				if(getFTimer()>0&&j==3) multiplier*=1.25;

				if(getPremiumTimer()>0) multiplier*=.5;
				
				if(totalOpenSpace[j]!=0)
				resBuff[j] +=multiplier*secbuff[j]*((double)(resCaps[j]+Building.baseResourceAmt-res[j])/(totalOpenSpace[j]));
				//System.out.println(resBuff[j] + " after");

				// add taxrates here
				
				int x = 0;
			
				while(x<tpr().size()) { //add taxes
					curr = tpr().get(x);
					if(curr.type>=0) {
					int y = 0;
					p = curr.player;
				//	if(p.owedTicks<3*24*3600/GodGenerator.gameClockFactor) p.update(); // No need to update if a player is this old, just collect!
					// When we try to update players here, we often deadlock them.
					if(p!=null) {
						ptowns = p.towns();
						checkForLeagueReference(p);
						
						
					while(y<ptowns.size()) {
						
						t = ptowns.get(y);
						
						// so that we make sure we have exact rates.
						tresInc=t.getResInc();
						// that no resources are taken from the outcropping by this league, but that
						// his resources are taken properly.
						 tresEffects=t.getResEffects();
						 newIncs = God.Maelstrom.getResEffects(tresInc,t.getX(),t.getY());
						

						 if(totalOpenSpace[j]!=0){
					
				//		if(j==0&&getUsername().equals("EAGLE"))
					//	System.out.println("The internalclock is " + getLeagueInternalClock() + ". The tax rate is " + curr.taxRate + " for user " + curr.player.getUsername() + " from their town of " + t.getTownName() + ". The resInc is " + tresInc[j] + " and I am adding"  +
						//		multiplier*num*newIncs[j]*(tresEffects[j]+1)*curr.taxRate*((double)(resCaps[j]+Building.baseResourceAmt-res[j])/(totalOpenSpace[j]))  + " because the difference is " + 
							//	((double)(resCaps[j]+Building.baseResourceAmt-res[j])) + " and the open space is " + totalOpenSpace[j] + ", num: "+ num+ ", mult: "+  multiplier +", newIncs: " + newIncs[j]);
							
							
						 resBuff[j]+=multiplier*num*newIncs[j]*(tresEffects[j]+1)*curr.taxRate*((double)(resCaps[j]+Building.baseResourceAmt-res[j])/(totalOpenSpace[j]));
						 }
						 y++;
					}
					
					}
					}
					x++;
				}
		
				
			//	if(j==0&&getUsername().equals("EAGLE")) System.out.println("Total resbuff take is: " + (resBuff[j]-befBuff));
				j++;
			} while(j<res.length);
			 }
			j = 0;
			synchronized(res) {
				synchronized(resBuff) {
			while(j<res.length){
			if(resBuff[j]>=1) {
				int toAdd = (int) Math.floor(resBuff[j]);
				res[j]+=toAdd;
				resBuff[j]-=toAdd;
				
				if(res[j]>(resCaps[j]+Building.baseResourceAmt))
					res[j]=resCaps[j]+Building.baseResourceAmt;
				// works even if you lose the building and suddenly have a massive over the limit
				// amount of resources! HAHA :D
				
				}
			j++;
			}
			}
			}
			

			// Then I need to check building and attack servers. Each town has a building server, and an attack server.
		i++;
			
		}
		i = 0;
		
		while(i<secbuff.length) { //reset the resbuff of secondaryhood.
			secbuff[i]=0;
			i++;
		}
		
		setLeagueInternalClock(getLeagueInternalClock() + num); // we only iterate after FINISHING THE SAVE!

		
	}
	
	public double getTaxRate(int pid) {
		int i = 0;
		while(i<tpr().size()) {
			if(tpr().get(i).pid==pid) return tpr().get(i).taxRate;
			i++;
		}
		return 0;
		/*
		double tax = 0;
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select tax from tpr where pid = " + pid);
			if(rs.next()) tax=rs.getDouble(1);
			rs.close();
			stmt.close();
			
		} catch(SQLException exc) { exc.printStackTrace(); }
		return tax;*/
	}
	public String getRank(int pid) {
		int i = 0;
		while(i<tpr().size()) {
			if(tpr().get(i).pid==pid) return tpr().get(i).rank;
			i++;
		}
		return null;
		/*
		String tax = null;
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select rank from tpr where pid = " + pid);
			if(rs.next()) tax = rs.getString(1);
			rs.close();
			stmt.close();
			
		} catch(SQLException exc) { exc.printStackTrace(); }
		return tax;*/
	}
	public int getTPRID(int pid) {
		int i = 0;
		while(i<tpr().size()) {
			if(tpr().get(i).pid==pid) return tpr().get(i).tprID;
			i++;
		}
		return 0;
		/*
		int tax = 0;
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select tprid from tpr where pid = " + pid);
			if(rs.next()) tax = rs.getInt(1);
			rs.close();
			stmt.close();
			
		} catch(SQLException exc) { exc.printStackTrace(); }
		return tax;*/
		
	}

	public int[] getTIDs(int pid) {
		int i = 0;
		while(i<tpr().size()) {
			if(tpr().get(i).pid==pid){
				int newIds[] = new int[tpr().get(i).tids.length];
				int j = 0;
				while(j<newIds.length) {
					newIds[j]=tpr().get(i).tids[j];
					j++;
				}
				return newIds;
			}
			i++;
		}
		return new int[0];
		/*int toRet[]=null;
		try {
			int tprid=0;
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select tprid from tpr where pid = " + pid);
			if(rs.next())
				tprid = rs.getInt(1);
			rs.close();
			
			ArrayList<Integer> tids = new ArrayList<Integer>();
			
			rs = stmt.executeQuery("select tid from permissions where tprid = " + tprid);
			while(rs.next()) {
				
				tids.add(rs.getInt(1));
			}
			
			rs.close();
			stmt.close();
			
			int i = 0;
			toRet = new int[tids.size()];
			while(i<toRet.length) {
				toRet[i]=tids.get(i);
				i++;
			}
			
		} catch(SQLException exc) { exc.printStackTrace(); }
		return toRet;*/
	
		
	}

public int getType(int pid) {
	int i = 0;
	while(i<tpr().size()) {
		if(tpr().get(i).pid==pid) return tpr().get(i).type;
		i++;
	}
	return 0;
}
	
public String getUsername(int pid) {
	int i = 0;
	while(i<tpr().size()) {
		if(tpr().get(i).pid==pid) return tpr().get(i).player.getUsername();
		i++;
	}
	return null;
}
	public boolean tprExists(int pid) {
		int i = 0;
		while(i<tpr().size()) {
			if(tpr().get(i).pid==pid) return true;
			i++;
		}
		return false;
		/*boolean found = false;

		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select type from tpr where pid = " + pid);
			if(rs.next()) found = true;
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		
		return found; // if you can't find it for some odd reason.*/
	}

	

	
	public boolean canMakeAdminChanges(int pid) {
	//	System.out.println(pid + " is the player, type of " + getType(pid));
		if(getType(pid)==2||pid==ID) {
			return true;
		}
		else return false;
	}
	public boolean canMakeModChanges(int pid) {
		int type = getType(pid);
		if(type==1||type==2) return true;
		else return false;
	}
	public boolean canMakeModChangesToTown(int pid,int tid) {
		if(canMakeAdminChanges(pid)) return true;
		// No use searching if you're already an admin!
		if(!canMakeModChanges(pid)) return false;
		// no use searching if you can't make mod changes period!
		int i=0;boolean found=false;
		while(i<tpr().size()) {
			if(tpr().get(i).pid == pid) {
				int j =0;
				
				while(j<tpr().get(i).tids.length) {

					if(tpr().get(i).tids[j]==tid){
						return true;
					}
					j++;
				}
			 return false; // If I reach this point, clearly, not working.
			}
			
			i++;
		}
		return false;
	}
	public int[] returnPIDs(int yourPID) {
		/*ArrayList<Integer> pids = new ArrayList<Integer>();
		
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select pid,type from tpr where league_pid = " + ID);
			while (rs.next()) {
				int pid = rs.getInt(1);
				if(rs.getInt(2)!=-1&&pid!=yourPID) 
					pids.add(pid);
			}
			rs.close();
			stmt.close();
			
		} catch(SQLException exc) { exc.printStackTrace(); }
			*/
		
		int toRet[] = new int[tpr().size()];
		int i = 0;
		while(i<toRet.length) {
			toRet[i]=tpr().get(i).pid;
			i++;
		}
		
		return toRet;
	}
	 public boolean deleteTPR(int pid) {
		// removes a tpr and, essentially, a player, from the league.
		int i= 0;TaxPlayerRank curr;
		while(i<tpr().size()) {
			curr = tpr().get(i);
			if(curr.pid==pid) {			
				curr.delete();
				tpr().remove(curr);
				return true;
			}
			i++;
		} 
		return false;
	}
	 public boolean createTPR(double taxRate, int pid, String rank, int type, int[] tids, int yourPID) {
		/*
		 * tpr codes
		 * -2 - tpr does not exist.
		 * -1 - Invited member
		 * 0 - Normal member
		 * 1 - Moderator(Can control towns but no grant privileges)
		 * 2 - Admin(Can control towns, with grant privileges) 
		 */
		if(taxRate>1||taxRate<0) return false;

		if(yourPID!=ID) return false; // Dunno how you're makin this request, bubs,
		// if you're not the league!
		int i = 0; boolean found = false;
		Player p=null;
		ArrayList<Player> players = God.getPlayers();
		while(i<players.size()) {
			p = players.get(i);
			if(p.ID==pid) {
				found=true;
				break;
			}
			i++;
		}
		
		if(p.isLeague()) return false; // Can't allow leagues to join leagues, now can we!?
		if(!found) return false;


		i=0;found=false;
	
		 i = 0;  found = false;
			while(i<tpr().size()) {
				if(tpr().get(i).pid == pid) {
					found = true; break;
				}
				i++;
			}
			
		if(found&&tpr().get(i).type==-1&&pid!=tpr().get(i).pid) return false; // you can only accept your own
		// invitations.

		if(found&&tpr().get(i).type==-1&&(type!=0||tids.length>0)) return false;

		// can't give yourself any privileges either.
	

		TaxPlayerRank curr;
		if(found) {
			curr = tpr().get(i);
			if(type==-1) return false; // current members cannot be downgraded to invite.

			return curr.reset(taxRate,pid,rank,type,tids); 
		} else {

			curr = new TaxPlayerRank(taxRate,pid,rank,type,tids,this);
			tpr.add(curr);
			return true;
		}
		
	}

	public int getInternalLeagueClock() {
		return internalLeagueClock;
	}







	public void setInternalLeagueClock(int internalLeagueClock) {
		this.internalLeagueClock = internalLeagueClock;
	}







	public String getHoldingLeagueIteratorID() {
		return holdingLeagueIteratorID;
	}







	public void setHoldingLeagueIteratorID(String holdingLeagueIteratorID) {
		this.holdingLeagueIteratorID = holdingLeagueIteratorID;
	}







	public ArrayList<TaxPlayerRank> getTpr() {
		return tpr();
	}














	public String getName() {
		return name;
	}







	public void setName(String name) {
		this.name = name;
	}







	public String getLetters() {
		return letters;
	}







	public void setLetters(String letters) {
		this.letters = letters;
	}







	public String getDescription() {
		return description;
	}







	public void setDescription(String description) {
		this.description = description;
	}







	public String getWebsite() {
		return website;
	}







	public void setWebsite(String website) {
		this.website = website;
	}







	public long[] getSecondaryResBuff() {
		return secondaryResBuff;
	}







	public void setSecondaryResBuff(long[] secondaryResBuff) {
		this.secondaryResBuff = secondaryResBuff;
	}







	public void setMemLetters(String letters) {
		try {
			UberPreparedStatement stmt = con.createStatement("update league set letters = ? where pid = ?;");	
			stmt.setString(1,letters);
			stmt.setInt(2,ID);
			stmt.execute();
			stmt.close();
			
		} catch(SQLException exc) { exc.printStackTrace(); }	
		}

	public String getMemLetters() {
		String l="";
		try {
			UberPreparedStatement stmt = con.createStatement("select letters from league where pid = ?;");
			stmt.setInt(1,ID);
		ResultSet rs= stmt.executeQuery();
		if(rs.next()) l=rs.getString(1);
		rs.close();
		stmt.close();
	
		} catch(SQLException exc) { exc.printStackTrace(); }	
		return l;
		}

	public void setMemWebsite(String website) {
		try {
			UberPreparedStatement stmt = con.createStatement("update league set website = ? where pid = ?;");
			stmt.setString(1,website);
			stmt.setInt(2,ID);
			stmt.execute();
			stmt.close();
			
		} catch(SQLException exc) { exc.printStackTrace(); }		}

	public String getMemWebsite() {
		String l="";
		try {
			UberPreparedStatement stmt = con.createStatement("select website from league where pid = ?;");
			stmt.setInt(1,ID);
		ResultSet rs= stmt.executeQuery();
		if(rs.next()) l=rs.getString(1);
		rs.close();
		stmt.close();
	
		} catch(SQLException exc) { exc.printStackTrace(); }	
		return l;
		}
	public void setMemName(String leagueName) {
		// TODO Auto-generated method stub
		try {
			UberPreparedStatement stmt = con.createStatement("update league set name = ? where pid = ?;");	
			stmt.setString(1,leagueName);
			stmt.setInt(2,ID);
			stmt.execute();
			stmt.close();
			
		} catch(SQLException exc) { exc.printStackTrace(); }		}
	public String getMemName() {
		String l="";
		try {
			UberPreparedStatement stmt = con.createStatement("select name from league where pid = ?;");
			stmt.setInt(1,ID);
		ResultSet rs= stmt.executeQuery();
		if(rs.next()) l=rs.getString(1);
		rs.close();
		stmt.close();
	
		} catch(SQLException exc) { exc.printStackTrace(); }	
		return l;	}

	public void setMemDescription(String description) {
		try {
			UberPreparedStatement stmt = con.createStatement("update league set description = ? where pid = ?;");
			stmt.setString(1,description);
			stmt.setInt(2,ID);
			stmt.execute();
			stmt.close();
			
		} catch(SQLException exc) { exc.printStackTrace(); }	
		}

	public String getMemDescription() {
		String l="";
		try {
			UberPreparedStatement stmt = con.createStatement("select description from league where pid = ?;");
			stmt.setInt(1,ID);
		ResultSet rs= stmt.executeQuery();
		if(rs.next()) l=rs.getString(1);
		rs.close();
		stmt.close();
	
		} catch(SQLException exc) { exc.printStackTrace(); }	
		return l;
		}

	public void setMemSecondaryResBuff(long a[]) {
		try {
			UberPreparedStatement stmt = con.createStatement("update league set mbuff = ?, tbuff = ?, mmbuff = ?, fbuff = ? where pid = ?;");
			stmt.setDouble(1,a[0]);
			stmt.setDouble(2,a[1]);
			stmt.setDouble(3,a[2]);
			stmt.setDouble(4,a[3]);
			stmt.setInt(1,ID);

			stmt.execute();
			stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }	}

	public long[] getMemSecondaryResBuff() {
		long resBuff[] = new long[5];
		try {
			UberPreparedStatement stmt = con.createStatement("select mbuff,tbuff,mmbuff,fbuff from league where pid = ?;");
			stmt.setInt(1,ID);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
	
				resBuff[0]=rs.getLong(1);
				resBuff[1]=rs.getLong(2);
				resBuff[2]=rs.getLong(3);
				resBuff[3]=rs.getLong(4);
				resBuff[4] = 0;
			}
			rs.close();
		stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return resBuff;
	}
	
	public void setLeagueInternalClock(int internalClock) {
		this.internalLeagueClock=internalClock;
	}
	public int getLeagueInternalClock() {
		return internalLeagueClock;
	}

	
	
	

	public void setLeagueHoldingIteratorID(String toSet) {
		holdingLeagueIteratorID=toSet;
	}
	public String getLeagueHoldingIteratorID() {
		return holdingLeagueIteratorID;
	}
	
	/*public void setLeagueInt(String fieldName, int toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update league set " + fieldName + " = " + toSet + " where pid = " + ID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setLeagueDouble(String fieldName, double toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update league set " + fieldName + " = " + toSet + " where pid = " + ID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setLeagueLong(String fieldName, long toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update league set " + fieldName + " = " + toSet + " where pid = " + ID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setLeagueBoolean(String fieldName, boolean toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update league set " + fieldName + " = " + toSet + " where pid = " + ID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setLeagueString(String fieldName, String toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update league set " + fieldName + " = \"" + toSet + "\" where pid = " + ID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public int getLeagueInt(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from league where pid = " + ID);
			rs.next();
			int toRet=rs.getInt(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return -1;
	}
	
	
	public double getLeagueDouble(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from league where pid = " + ID);
			rs.next();
			double toRet=rs.getDouble(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return -1;
	}
	
	public long getLeagueLong(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from league where pid = " + ID);
			rs.next();
			long toRet=rs.getLong(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return -1;
	}
	
	public boolean getLeagueBoolean(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from league where pid = " + ID);
			rs.next();
			boolean toRet=rs.getBoolean(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return false;
	}
	public String getLeagueString(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from league where pid = " + ID);
			rs.next();
			String toRet=rs.getString(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return null;
	}*/
}


class TaxPlayerRank {
	double taxRate;
	int pid;
	String rank;
	int tprID;
	int tids[];
	Player player;
	League league;
	int type;
	public void delete() {
		UberPreparedStatement stmt;
		try {

	      

	      // First things first. We update the player table.
	      boolean transacted=false;
	      while(!transacted) {
	    	  try {
	    		
			      stmt = league.God.con.createStatement("delete from permissions where tprID = ?;");
			      stmt.setInt(1,tprID);
			      stmt.execute();
			      stmt.close();
	      // keep track of all the permids and make the changes individually!!!
	      stmt = league.God.con.createStatement("delete from tpr where tprID = ?;");
	      stmt.setInt(1,tprID);
	      stmt.execute();
	      stmt.close();
	      transacted=true; }
	    	  catch(MySQLTransactionRollbackException exc) {  }
	      }// need connection for attackunit adds!
		}catch(SQLException exc) { exc.printStackTrace();  }
	}
	public boolean reset(double taxRate,int pid, String rank, int type, int tids[]) {
		this.pid = pid;
		this.rank = rank;
		this.taxRate = taxRate;
		this.tids=tids;
		this.type=type;		
		UberPreparedStatement stmt;
		try {

	      
	   
	      // First things first. We update the player table.
	      boolean transacted=false;
	      while(!transacted) {
	    	  try {
	    		
	    		   stmt = league.God.con.createStatement("update tpr set league_pid = ?, tax = ?, pid = ?, rank = ?, type = ? where tprID = ?;");
	    		      stmt.setInt(1,league.ID);
	    		      stmt.setDouble(2,taxRate);
	    		      stmt.setInt(3,pid);
	    		      stmt.setString(4,rank);
	    		      stmt.setInt(5,type);
	    		      stmt.setInt(6,tprID);
	      
	      // let's add this raid and therefore get the rid out of it.
	    		      stmt.executeUpdate();
	    		      stmt.close();
	    		      
	    		      
	    		      stmt = league.God.con.createStatement("delete from permissions where tprID = ?;");

	    		      stmt.setInt(1,tprID);

	      int i = 0;
	      stmt.execute(); // just easier to reset them than
	      // keep track of all the permids and make the changes individually!!!
	      stmt.close();
	      stmt = league.God.con.createStatement("insert into permissions (tprID,tid) values (?,?);");
	      stmt.setInt(1,tprID);
	      while(i<tids.length) {
	    	  stmt.setInt(2,tids[i]);
	    	  stmt.execute();
	    	  i++;
	      }
	      

	      stmt.close(); transacted=true; }
	    	  catch(MySQLTransactionRollbackException exc) {  }
	      }// need connection for attackunit adds!
		}catch(SQLException exc) { exc.printStackTrace(); return false; }
		
		return true;
	}
	public TaxPlayerRank( double taxRate,int pid, String rank, int type, int tids[],League league) {
		this.pid = pid;
		this.rank = rank;
		this.taxRate = taxRate;
		this.tids=tids;
		this.league=league;
		this.type=type;

		
		// INSERT SQL STUFF HERE
		
		UberPreparedStatement stmt;
		try {

	      
	      
	      // First things first. We update the player table.
	      boolean transacted=false;
	      while(!transacted) {
	    	  try {
	    		

	    	      stmt = league.God.con.createStatement("insert into tpr (league_pid,tax,pid,rank,type) values (?,?,?,?,?);");
	    	      stmt.setInt(1,league.ID);
	    	      stmt.setDouble(2,taxRate);
	    	      stmt.setInt(3,pid);
	    	      stmt.setString(4,rank);
	    	      stmt.setInt(5,type);
	      // let's add this raid and therefore get the rid out of it.
			      stmt.execute();
			      stmt.close();
			      stmt = league.God.con.createStatement("select tprID from tpr where pid = ? and league_pid = ?;");
			      stmt.setInt(1,pid);
			      stmt.setInt(2,league.ID);
	      
	      ResultSet ridstuff = stmt.executeQuery();
	     
	      ArrayList<TaxPlayerRank> tpr = league.tpr();
	      while(ridstuff.next()) {
	    	  int j = 0;
	    	  
	    	  while(j<tpr.size()) {
	    		  if(tpr.get(j).tprID==ridstuff.getInt(1)) break;
	    		  j++;
	    	  }
	    	  
	    	  if(j==tpr.size()) break; // means we found no raid accompanying this raidID.
	      }
	      
	      	tprID = ridstuff.getInt(1);

	      ridstuff.close();
	      stmt.close();
	      // now that we possess an id, we need to make entries for each of the towns!
	      /*
	      +--------+------------------+------+-----+---------+----------------+
	      | Field  | Type             | Null | Key | Default | Extra          |
	      +--------+------------------+------+-----+---------+----------------+
	      | permid | int(10) unsigned | NO   | PRI | NULL    | auto_increment |
	      | tprID  | int(10) unsigned | NO   | MUL | NULL    |                |
	      | tid    | int(10) unsigned | NO   | MUL | NULL    |                |
	      +--------+------------------+------+-----+---------+----------------+*/

	      int i = 0;
	      stmt = league.God.con.createStatement("insert into permissions (tprID,tid) values (?,?);");
	      stmt.setInt(1,tprID);
	      while(i<tids.length) {
	    	  stmt.setInt(2,tids[i]);
	    	  stmt.execute();
	    	  i++;
	      }

	      stmt.close(); transacted=true; }
	    	  catch(MySQLTransactionRollbackException exc) { }
	      }// need connection for attackunit adds!
		}catch(SQLException exc) { exc.printStackTrace(); }
		
	}
	public TaxPlayerRank( double taxRate, int pid, String rank,int type, int tids[], int tprID,League league) {
		this.pid = pid;
		this.rank = rank;
		this.taxRate = taxRate;
		this.tids=tids;
		this.tprID=tprID;
		this.league=league;
		this.type=type;
		
	}
	
}