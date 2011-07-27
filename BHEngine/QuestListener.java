//<div class='goalBox'>Goals:<ul></ul></div><div class='descBox'></div><div class='rewardBox'>
package BHEngine;

import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;

import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;
/**
 * The QuestListener is an abstract class that can be implemented so that you can write
 * your own Quest AI's. On server start up, every QuestListener is loaded up into memory only
 * once, your Quest exists as a singular A.I. on the server, and any player that has it as a Listener
 * calls it's iterate method once every processing cycle. Therefore, you can have multiple players
 * on your quest and you can even use them to interact with one another, or group them based on
 * your own additions.
 * <br/><br/>
 * Please be responsible in your questwriting, all quests will be reviewed before being added
 * to the main server.
 * 
 * @author Jordan Prince
 *
 */
public abstract class QuestListener extends Player {
	private ArrayList<Player> players;
	private ArrayList<QuestPlayerComplete> qpc;
	ArrayList<doableBy> invadableBy;
	ArrayList<doableBy> viewableBy;
	protected QuestListener(int ID, GodGenerator God){
		super(ID, God);
		setQuest(true);
        ps = new PlayerScript(this);

	}
	public ArrayList<doableBy> invadableBy() {
		if(invadableBy==null) {
		try {
			invadableBy=new ArrayList<doableBy>();
			UberPreparedStatement stmt = con.createStatement("select * from invadable where tid =  ? and type = 0;");
			int i = 0;
			ArrayList<Town> towns = towns();
			ResultSet rs;
			while(i<towns.size()) {
				stmt.setInt(1,towns.get(i).townID);
				rs = stmt.executeQuery();
				while(rs.next()) {
					invadableBy.add(new doableBy(rs.getInt(1),rs.getInt(3),rs.getInt(2),0));
				}
				rs.close();
				i++;
			}
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
	
		}
		return invadableBy;
	}
	public ArrayList<doableBy> viewableBy() {
		if(viewableBy==null) {
			try {
				viewableBy=new ArrayList<doableBy>();
				UberPreparedStatement stmt = con.createStatement("select * from invadable where tid =  ? and type = 1;");
				int i = 0;
				ArrayList<Town> towns = towns();
				ResultSet rs;
				while(i<towns.size()) {
					stmt.setInt(1,towns.get(i).townID);
					rs = stmt.executeQuery();
					while(rs.next()) {
						viewableBy.add(new doableBy(rs.getInt(1),rs.getInt(3),rs.getInt(2),1));
					}
					rs.close();
					i++;
				}
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }
		
			}
			return viewableBy;
	}
	public ArrayList<QuestPlayerComplete> qpc() {
		if(qpc==null) {
		try {
			qpc=new ArrayList<QuestPlayerComplete>();
			players=new ArrayList<Player>();
	        // so it gets it's a playerscript.
			UberPreparedStatement stmt = con.createStatement("select * from qpc where qid = ?;");
			stmt.setInt(1,ID);
			ResultSet rs = stmt.executeQuery();
			Player p;
			while(rs.next()) {
				
				p = God.getPlayer(rs.getInt(3));
				if(rs.getInt(4)==0) players.add(p);
		
				qpc.add(new QuestPlayerComplete(rs.getInt(4),rs.getString(5),p,ID,rs.getInt(1)));
				
			}
			rs.close();
			stmt.close();
			// TODO Auto-generated constructor stub
			} catch(SQLException exc) { exc.printStackTrace(); }
		}
		
		return qpc;
			
	}
	/**
	 * This method will normally be called my a Player object in iteration
	 * and it's argument will normally be 1. This means make whatever checks
	 * you'd need to make in a normal period of one game cycle(whatever the gameClockFactor is), but be prepared,
	 * if for some reason player needs to iterate multiple times quickly,
	 * or needs the QuestListener object to do so, to do whatever is required
	 * for any number of times.
	 * @param times
	 * @param pid - The Player ID of the player calling iterate.
	 */
	public abstract void iterateQuest(int times, int pid);
	
	/**
	 * Call this method to reward the player denoted by pid.
	 * @param pid
	 */
	public abstract void reward(int pid);

	public void save() {
		super.save();
		int i = 0;
		try {
			UberPreparedStatement stmt = con.createStatement("update qpc set complete = ?, log=? where qpcid = ?;");
			QuestPlayerComplete q;
			while(i<qpc().size()) {
				q = qpc().get(i);
				stmt.setInt(1,q.getCompleted());
				stmt.setString(2,q.getMemory());
				stmt.setInt(3,q.qpcid);
				stmt.executeUpdate();
				i++;
			}
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
	}
	public void rewardOneHour(int pid) {
		// default reward, already pre-coded - delivers an hour of resources to every city.
		
		Player p = findPlayer(pid);
		ArrayList<Town> towns = p.towns();
		int i = 0; Town t; long[] res; double[] resInc;
		while(i<towns.size()) {
			t = towns.get(i);
			res = t.getRes();
			resInc = t.getResInc();
			int j = 0;
			synchronized(res) {
				while(j<res.length-1) {
					res[j]+=(long) Math.round((resInc[j]*((double) 3600)/((double) GodGenerator.gameClockFactor)));
					j++;
				}
			}
			i++;
		}
	}
	public long[] getRewardOneHour(int pid) {
		// default reward, already pre-coded - delivers an hour of resources to every city.
		
		Player p = God.getPlayer(pid);
		ArrayList<Town> towns = p.towns();
		int i = 0; Town t; long[] res= new long[5]; double resInc[];
		
		while(i<towns.size()) {
			t = towns.get(i);
			resInc = t.getResInc();
			int j = 0;
			while(j<res.length-1) {
				res[j]+=(long) Math.round((resInc[j]*((double) 3600)/((double) GodGenerator.gameClockFactor)));
				j++;
			}
			i++;
		}
		
		return res;
	}
	/**
	 * Destroys the quest without completion.
	 * @param p
	 * @return
	 */
	public boolean destroyWithoutCompletion(Player p) {
		
		try {

			Player pl = findPlayer(p.ID);
			
			if(pl==null) return false;
			deleteAllInvadableViewable(pl.ID);

			int i = 0;
			synchronized(qpc()) {
			while(i<qpc().size()) {
				if(qpc().get(i).getPlayer().ID==p.ID) {
					qpc().remove(i);
					break;
				}
				i++;
			}
			synchronized(getPlayers()) {
			getPlayers().remove(pl);
			}
			synchronized(p) {
			p.getActiveQuests().remove(this);
			}
			
			}
			UberPreparedStatement stmt = p.con.createStatement("delete from qpc where qid = ? and pid = ?;");
			stmt.setInt(1,ID);
			stmt.setInt(2,p.ID);
			boolean transacted=false;
			while(!transacted) {
			try {
			stmt.execute();

			transacted=true;
			stmt.close();
			} catch(MySQLTransactionRollbackException exc) {
				
			}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}
	
	public final void digFinishCatch(Town t, Player p) {
		try {
			if(partOfQuest(p,getUsername()))

			 digFinish(t,p);
			
		}catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void digFinish(Town t, Player p) {
		
	}	
	public final void onRaidSentCatch(Raid r,  boolean prog) {
		try {
			if(partOfQuest(r.getTown1().getPlayer(),getUsername()))

			onRaidSent(r,prog);
			
		}catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void onRaidSent(Raid r, boolean prog) {
		
	}
	
	public final void onRaidLandingCatch(Raid r) {
		try {
			if(partOfQuest(r.getTown1().getPlayer(),getUsername()))

			onRaidLanding(r);
			
		}catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void onRaidLanding(Raid r) {
		
	}
	
	public final void onProgramLoadCatch(Player p) {
		try {
			if(partOfQuest(p,getUsername()))
			 onProgramLoad(p);
			
		}catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void onProgramLoad(Player p) {
		
	}
	public final void onServerLoadCatch() {
		try {
			 onServerLoad();
			
		}catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void onServerLoad() {
		
	}
	
	/**
	 * Destroys the quest for a player and marks it as complete.
	 */
	public boolean destroy(Player p) {
	//	System.out.println("Destroying I am " + this);
	//	try {
			Player pl = findPlayer(p.ID);
			if(pl==null) return false;
			deleteAllInvadableViewable(pl.ID);
			int i = 0;
			synchronized(qpc()) {
			while(i<qpc().size()) {
				if(qpc().get(i).getPlayer().ID==p.ID) {
					qpc().get(i).setCompleted(1);
					break;
				}
				i++;
			}
			}
			synchronized(getPlayers()) {
				getPlayers().remove(p);
			}
			synchronized(p.getActiveQuests()) {
				p.getActiveQuests().remove(this);
				//p.flicker=getUsername();
				}
			/*UberStatement stmt = p.con.createStatement();
		
			
			boolean transacted=false;
			while(!transacted) {
			try {
				stmt.execute("start transaction;");
			stmt.execute("update qpc() set complete=1 where qid = " +ID + " and pid = " + p.ID + ";");
			stmt.execute("commit;");

			transacted=true;
			stmt.close();
			} catch(MySQLTransactionRollbackException exc) {
				
			}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return true;

	}
	public boolean deleteAllInvadableViewable(int pid) {
		int i = 0;
		while(i<invadableBy().size()) {
			if(invadableBy().get(i).pid==pid) {
				deleteInvadableBy(invadableBy().get(i).tid,invadableBy().get(i).pid);
				i--;
			}
			i++;
		}
		 i = 0;
		while(i<viewableBy().size()) {
			if(viewableBy().get(i).pid==pid) {
				deleteViewableBy(viewableBy().get(i).tid,viewableBy().get(i).pid);
				i--;
			}
			i++;
		}
		return true;
	}
	/**
	 * Returns true if a player has completed a quest by this questname.
	 * 
	 */
	public boolean completedQuest(Player p, String questname) {
		int i =0;
		if(God.getPlayerId(questname)==5) return false;
		ArrayList<QuestPlayerComplete> qpc = ((QuestListener) God.getPlayer(God.getPlayerId(questname))).qpc();
		while(i<qpc.size()) {

			if(qpc.get(i).getPlayer().ID==p.ID) {
				if(qpc.get(i).getCompleted()==1) {
					return true;
				}
				else break;
			}
			i++;
		}
		return false;
		//UberStatement stmt=null; ResultSet rs=null;
		/*
		try {
			 stmt = con.createStatement();
			 rs = stmt.executeQuery("select qid from Quest where classname = \""  + questname + "\";");
			 
			if(rs.next()) {
				boolean toret = completedQuest(p,rs.getInt(1));
				rs.close();
				stmt.close();
				return toret;
			} else {
				stmt.close();
				rs.close();
				return false;
			}
		} catch(SQLException exc) { try{ 
			stmt.close(); rs.close();
		} catch(Exception exc2) { exc2.printStackTrace(); }
		exc.printStackTrace(); }
				return false;
		 */
	}
	/**
	 * Returns true if you're part of the Quest given by the Quest name.
	 * @param p
	 * @param questname
	 * @return
	 */
	public static boolean partOfQuest(Player p, String questname) {
		int i = 0;
		if(p.God.getPlayerId(questname)==5) return false;

		ArrayList<QuestPlayerComplete> qpc = ((QuestListener) p.God.getPlayer(p.God.getPlayerId(questname))).qpc();
		while(i<qpc.size()) {
		//	System.out.println(getUsername() + "'s quests when doing completed: " + qpc().get(i).qpcid);
			if(qpc.get(i).getPlayer().ID==p.ID&&qpc.get(i).getCompleted()==0) {
				return true;
			}
			i++;
		}
		return false;
	}
	/**
	 * Returns true if a player has completed or is currently part of the quest given by questname.
	 * @param p
	 * @param questname
	 * @return
	 */
	public boolean completedOrPartOfQuest(Player p, String questname) {
		int i = 0;
		if(God.getPlayerId(questname)==5) return false;

		ArrayList<QuestPlayerComplete> qpc = ((QuestListener) God.getPlayer(God.getPlayerId(questname))).qpc();
		while(i<qpc.size()) {
		//	System.out.println(getUsername() + "'s quests when doing completed: " + qpc().get(i).qpcid);
			if(qpc.get(i).getPlayer().ID==p.ID) {
				return true;
			}
			i++;
		}
		return false;
		/*UberStatement stmt=null; ResultSet rs=null;

		try {
			 stmt = con.createStatement();
			 rs = stmt.executeQuery("select qid from Quest where classname = \""  + questname + "\";");
			 
			if(rs.next()) {
				
				rs.close();
				stmt.close();
				return true;
			} else {
				stmt.close();
				rs.close();
				return false;
			}
		} catch(SQLException exc) { try{ 
			stmt.close(); rs.close();
		} catch(Exception exc2) { exc2.printStackTrace(); }
		exc.printStackTrace(); }
		
		return false;*/
	}
	/**
	 * Will check to see if the commands identified by sequence have been executed. If strength is set to 2,
	 * then seq needs to contain the exact arguments you are searching for. If it is sent to 1, then it will only
	 * do argument comparisons on commands with arguments. If it is set to 0, then you can just send
	 * a String array of "empty" commands like sendYourself();attack(); and it will just look for general calls. If
	 * the strength is set to -1, you can just provide statements along the way. If they called attack ten times,
	 * and then haveMetal, you can search attack();haveMetal(); and it'll work - it'll just sort of peruse through
	 * not to see if the exact sequence was followed, but if those UberStatement were called and called in that order.
	 * @param seq
	 * @param strength
	 * @param p
	 * @return
	 */
	public boolean logContains(String[] seq, int strength, Player p) {
		if(p.getPs().revb==null) return false;
		String[] log = p.getPs().revb.getLog();
		int i = log.length-1;
		while(i>=0) {
			if(log[i].equals(seq[0])) {
				int j = 0; boolean correctsequence=true;
				boolean foundAtSomePoint[] = new boolean[seq.length];
				while(j<seq.length&&(i-j)>=0) {
					switch(strength) {
					case -1:
						int k=i-j;
						while(k>=0) {
							if(log[k].startsWith(seq[j].substring(0,seq[j].indexOf("();")))) {
								foundAtSomePoint[j]=true; break;

							}
							k--;
						}

					case 0:
						if(!log[i-j].startsWith(seq[j].substring(0,seq[j].indexOf("();")))) correctsequence=false; 
						break;
					case 1:

						if((seq[j].endsWith("();")&&!log[i-j].startsWith(seq[j].substring(0,seq[j].indexOf("();"))))||
								(!seq[j].endsWith("();")&&!log[i-j].equals(seq[j]))
						) correctsequence=false;
					//	if(!correctsequence) {
					//		System.out.println(log[i-j] + " broke sequence by not being " + seq[j]);
						//}
						break;
					case 2:
						 if(!log[i-j].equals(seq[j])) correctsequence=false;
						 break;
					}
					
					if(!correctsequence) break;
					j++;
				}
				if(strength==-1) {
					int k =0;boolean allTrue = true;
					while(k<foundAtSomePoint.length) {
						if(!foundAtSomePoint[k]) {
							allTrue=false; break;
						}
						k++;
					}
					
					if(allTrue) return true;
				}
				if(correctsequence&&j==seq.length) return true;
			}
			
			i--;
		}
		return false;
	}
	/**
	 * Returns a short description of the quest.
	 */
	public abstract String getQuestDescription(int pid);
	/**
	 * Returns true if a player has completed a quest by this qid.
	 */
	
	public boolean completedQuest(Player p, int qid) {
		int i = 0;
		if(God.getPlayer(qid).ID==5) return false;

		ArrayList<QuestPlayerComplete> qpc = ((QuestListener) God.getPlayer(qid)).qpc();
		while(i<qpc.size()) {
		//	System.out.println(getUsername() + "'s quests when doing completed: " + qpc().get(i).qpcid);
			if(qpc.get(i).getPlayer().ID==p.ID) {
				if(qpc.get(i).getCompleted()==1) return true;
				else break;
			}
			i++;
		}
		return false;
		/*
		UberStatement stmt=null; ResultSet rs=null;
		try {
			 stmt = con.createStatement();
			 rs = stmt.executeQuery("select complete from qpc() where qid = "  + qid + " and pid = " + p.ID);
			if(rs.next()) {
				if(rs.getInt(1)==1) return true;
				else {
					rs.close();
					stmt.close();
					return false;
				}
			} else {
				stmt.close();
				rs.close();
				return false;
			}
		} catch(SQLException exc) { try{ 
			stmt.close(); rs.close();
		} catch(Exception exc2) { exc2.printStackTrace(); }
		exc.printStackTrace(); }
		
		return false;*/
	}
/**
 * Means you can be currently pursuing or have completed a quest to get a true, so you can do
 * quest branching - they have to join and read the quest prompt of one before opening another!
 * @param p
 * @param qid
 * @return
 */
	public boolean completedOrPartOfQuest(Player p, int qid) {
		int i = 0;
		if(God.getPlayer(qid).ID==5) return false;

		ArrayList<QuestPlayerComplete> qpc = ((QuestListener) God.getPlayer(qid)).qpc();
		while(i<qpc.size()) {
		//	System.out.println(getUsername() + "'s quests when doing completed: " + qpc().get(i).qpcid);
			if(qpc.get(i).getPlayer().ID==p.ID) {
				return true;
			}
			i++;
		}
		return false;
		/*
		UberStatement stmt=null; ResultSet rs=null;
		try {
			 stmt = con.createStatement();
			 rs = stmt.executeQuery("select complete from qpc() where qid = "  + qid + " and pid = " + p.ID);
			if(rs.next()) {
				
					rs.close();
					stmt.close();
					return true;
				
			} else {
				stmt.close();
				rs.close();
				return false;
			}
		} catch(SQLException exc) { try{ 
			stmt.close(); rs.close();
		} catch(Exception exc2) { exc2.printStackTrace(); }
		exc.printStackTrace(); }
		
		return false;*/
	}
	/**
	 * Adds a player to the Quest. You can have any sort of checks and balances
	 * on the checkPlayer method that you need to reject or accept this player into the quest,
	 * as this method calls that method before it inputs the system info.
	 * 
	 * @param p
	 */
	public boolean addPlayer(Player p) {
		if(!checkPlayer(p)) {
			return false;
		}
		
	try {
			boolean transacted=false;
			Player pl = findPlayer(p.ID);
			if(pl!=null)
				return false; // player already added.
			else {
				
			UberPreparedStatement stmt = p.con.createStatement("select * from qpc where pid = ? and qid = ?;");
			stmt.setInt(1,p.ID);
			stmt.setInt(2,ID);
			while(!transacted) {
			try {
				ResultSet rs = stmt.executeQuery();
				if(!rs.next())  {
					// so if the entry does not exist, we make it.
				rs.close();
				stmt.close();
				stmt = p.con.createStatement("insert into qpc (qid,pid) values (?,?);");
				stmt.setInt(1,ID);
				stmt.setInt(2,p.ID);
				stmt.execute();
				stmt.close();
				stmt = p.con.createStatement("select qpcid from qpc where qid = ? and pid = ?;");
				stmt.setInt(1,ID);
				stmt.setInt(2,p.ID);
				rs = stmt.executeQuery();
				rs.next();
				int qpcid = rs.getInt(1);
				
				qpc().add(new QuestPlayerComplete(0,"",p,ID,qpcid));
				
				} 
				
				// a next, and if that next is a completed quest, we do not add it, 
				// if it isn't, we go through and add it.
			transacted=true;
			rs.close();// closed no matter what!
			stmt.close();

			} catch(MySQLTransactionRollbackException exc) {
				
			} 

			}
			p.getActiveQuests().add(this);
			p.flicker=getUsername();
			getPlayers().add(p);
			playerConstructor(p);

			}
	} catch(SQLException exc) { exc.printStackTrace(); }
		return true;
	}
	/**
	 * Called on server start up as each player is attached to the quest.
	 */
	public abstract void playerConstructor(Player p);
	/**
	 * This returns the quest log for your quest. Make sure it returns
	 * something legible.
	 * @return
	 */
	public abstract String[] getCurrentQuestText(int pid);
	
	/**
	 * Stores data in your quest's preset long text column in the database.
	 * Use it for loading and saving information. You must come up with your
	 * own data structures. Returns false if you provide an invalid pid.
	 * @param toWrite
	 * @return
	 */
	public boolean writeToMemory(String toWrite, int pid) {
		Player p = findPlayer(pid);
		if(p==null) return false;
		
		int i = 0;
		while(i<qpc().size()) {
			if(qpc().get(i).getPlayer().ID==p.ID){
				synchronized(qpc().get(i)){qpc().get(i).setMemory(toWrite);}
				return true;
			}
			i++;
		}
		return false;
		
		/*
		try {
			UberStatement stmt = con.createStatement();
			boolean transacted=false;
			while(!transacted) {
				try {
				stmt.execute("start transaction;");
				stmt.execute("update qpc() set log = \"" + toWrite + "\" where pid = " + pid + " and qid = " + ID);
				stmt.execute("commit;");
				transacted=true;
				} catch(MySQLTransactionRollbackException exc) { }
			}
			stmt.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;*/
	}
	/**
	 * Returns in string form the players' memory longtext column from the database.
	 * Returns null if you provide an invalid pid.
	 * @return
	 */
	public String readFromMemory(int pid) {
		Player p = findPlayer(pid);
		
		if(p==null) {
			return null;
		}
		int i = 0;
		while(i<qpc().size()) {
			if(qpc().get(i).getPlayer().ID==p.ID){
				return qpc().get(i).getMemory();
			}
			i++;
		}
		return null;
		/*
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs =stmt.executeQuery("select log from qpc() where pid = " + pid + " and qid = " + ID);
			rs.next();
			String toRet = rs.getString(1);
			rs.close();
			stmt.close();
			return toRet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
		 */
		
	}
	
	/**
	 * Adds a town for this quest at the location specified, if possible. If pidsInvadableBy is 0, this town
	 * is invadable by anybody.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int addTown(int x, int y, String townName, double resEffects[], int[] pidsInvadableBy, int[] pidsViewableBy) {
		int tid=-1;
		try {
			UberPreparedStatement stmt = con.createStatement("select count(*) from town where x = ? and y = ?;");
			stmt.setInt(1,x);
			stmt.setInt(2,y);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				int size = rs.getInt(1);
				if(size>0) {
				rs.close();
				stmt.close();
				return tid;
				}
			}
			rs.close();
			stmt.close();
			
			boolean transacted=false;
			while(!transacted) {
				try {
		//	stmt.execute("update player set chg = 1 where pid = " + ID);
					stmt = con.createStatement("insert into town (pid,townName,x,y,m,t,mm,f,pop,minc,tinc,mminc,finc,kinc,auSizes) values (?,?,?,?,0,0,0,0,1,?,?,?,?,?,?);");
					stmt.setInt(1,ID);
					stmt.setString(2,townName);
					stmt.setInt(3,x);
					stmt.setInt(4,y);
					stmt.setDouble(5,resEffects[0]);
					stmt.setDouble(6,resEffects[1]);
					stmt.setDouble(7,resEffects[2]);
					stmt.setDouble(8,resEffects[3]);
					stmt.setDouble(9,resEffects[4]);
					int newSizes[] = new int[0];
					stmt.setString(10,PlayerScript.toJSONString(newSizes));
					stmt.execute();
					stmt = con.createStatement("select tid from town where x = ? and y = ?;");
					stmt.setInt(1,x);
					stmt.setInt(2,y);
    		  rs = stmt.executeQuery();
    		  rs.next();
    		   tid = rs.getInt(1);
    		  rs.close();
    		stmt.close();
    		UUID id = UUID.randomUUID();
    		stmt = con.createStatement("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp,deconstruct,pploutside,bunkerMode,id) values (?,?,3,-1,0,0,0,?,0,0,-1,0,?);");
    		stmt.setString(4,id.toString());
    		stmt.setInt(3,tid);
    		stmt.setString(1,"Metal Mine");
    		stmt.setInt(2,0);
    		stmt.execute();
    		id = UUID.randomUUID();
    		stmt.setString(4,id.toString());
    		stmt.setString(1,"Timber Field");
    		stmt.setInt(2,1);
    		stmt.execute();
    		id = UUID.randomUUID();
    		stmt.setString(4,id.toString());
    		stmt.setString(1,"Crystal Mine");
    		stmt.setInt(2,2);
    		stmt.execute();
    		id = UUID.randomUUID();
    		stmt.setString(4,id.toString());
    		stmt.setString(1,"Farm");
    		stmt.setInt(2,3);
    		stmt.execute();
    		
    		  
    		  Town t = new Town(tid,God);
    		  rs.close();
    		  God.getIteratorTowns().add(t);
    		  towns().add(t);
    		 // System.out.println("This town: " + t + " on end of player: "+ towns().get(towns().size()-1));
    		  UberPreparedStatement stmt2 = con.createStatement("select iid from invadable where tid = ? and pid = ? and type = ?");
    		  stmt2.setInt(1,tid);
    		  stmt2.setInt(3,0);
    		  stmt = con.createStatement("insert into invadable(tid,pid,type) values (?,?,?);");
    		  stmt.setInt(1,tid);
    		  stmt.setInt(3,0);
    		  if(pidsInvadableBy!=null) {
    			  int i = 0;
    			  while(i<pidsInvadableBy.length) {
    				  stmt.setInt(2,pidsInvadableBy[i]);
    				  stmt.execute();
    				  stmt2.setInt(2,pidsInvadableBy[i]);
        			   rs = stmt2.executeQuery();
            		
        			   if(rs.next()) invadableBy().add(new doableBy(rs.getInt(1),pidsInvadableBy[i],tid,0));
        			   rs.close();
        			   i++;
    			  }

    		  }
    		  stmt.setInt(3,1);
    		  stmt2.setInt(3,1);
    		  if(pidsViewableBy!=null) {
    			  int i = 0;
    			  while(i<pidsViewableBy.length) {
    				  stmt.setInt(2,pidsViewableBy[i]);

    				  stmt.execute();
    				  
    				  stmt2.setInt(2,pidsViewableBy[i]);

    				  rs = stmt2.executeQuery();
              		
	       			   if(rs.next())  viewableBy().add(new doableBy(rs.getInt(1),pidsViewableBy[i],tid,1));
	       			   rs.close();
    				  i++;
    			  }
    		  }
    //		  stmt.execute("update player set chg = 2 where pid = "+ ID);
    		  rs.close();
    		  stmt.close();
    		  stmt2.close();
    		  transacted=true;
				} catch(MySQLTransactionRollbackException exc) { } 
			}
			return tid;
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return tid;
		
	}
	/**
	 * Removes a town for this quest by the given tid.
	 * 
	 * @param tid the tid of the quest town to remove
	 * 
	 * @return True, if no errors occurred.
	 */
	public boolean killTown(int tid) {
		/*
		 * First we find the town, then we give the town to the quest again!
		 * Then we can delete with impunity.
		 */
	
		Town t= God.findTown(tid);
		if(t.getPlayer().ID==ID) {
			System.out.println("1.");

			int i = 0;
			while(i<invadableBy().size()) {

				if(invadableBy().get(i).tid==t.townID) {
					System.out.println("size bef: " + invadableBy().size());

					deleteInvadableBy(invadableBy().get(i).tid,invadableBy().get(i).pid);
					System.out.println("size aft: " + invadableBy().size());
					i--;
				}
				i++;
			}
			System.out.println("2.");

			i = 0;
			while(i<viewableBy().size()) {
				if(viewableBy().get(i).tid==t.townID) {
					deleteViewableBy(viewableBy().get(i).tid,viewableBy().get(i).pid);
					i--;
				}
				i++;
			}
			System.out.println("3.");

			t.deleteTown();


		} else 
			return false;
		
	
		return true;
		
		
	}
	
	
	/**
	 * Your checker function on whether or not a player should be able to be added
	 * to this quest.
	 * @param p
	 * @return
	 */
	public abstract boolean checkPlayer(Player p);
	
	public Player findPlayer(int pid) {
		int i = 0; //Player p=null;
		/*
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from qpc() where pid = " + pid + " and qid = " + ID);
			if(rs.next()) p = new Player(pid,God);
			rs.close();
			stmt.close();
		} catch(SQLException exc) {
			exc.printStackTrace();
		}*/
		while(i<getPlayers().size()) {
			if(getPlayers().get(i).ID==pid) {
				return getPlayers().get(i);
			}
			i++;
		}
		return null;
	}
public ArrayList<Player> getPlayers() {
		if(players==null) {
			qpc();
		}
		return players;
	}

public void deleteInvadableBy(int tid, int pid) {
	try {
		UberPreparedStatement stmt = con.createStatement("delete from invadable where tid = ? and pid = ? and type = 0;");
		
		//Player p = findPlayer(pid); Town t = God.findTown(tid);
		int i = 0;
		while(i<invadableBy().size()) {
			if(invadableBy().get(i).tid==tid&&invadableBy().get(i).pid==pid){ 
				stmt.setInt(1,tid);
				stmt.setInt(2,pid);
				stmt.execute();
				invadableBy().remove(i); break; }
			i++;
		}
		
		
		stmt.close();
	} catch(SQLException exc) { exc.printStackTrace(); }
}
public void deleteViewableBy(int tid, int pid) {
	try {
		UberPreparedStatement stmt = con.createStatement("delete from invadable where tid = ? and pid = ? and type = 1;");
		//Player p = findPlayer(pid); Town t = God.findTown(tid);
		stmt.setInt(1,tid);
		stmt.setInt(2,pid);
		stmt.execute();
		int i = 0;
		while(i<viewableBy().size()) {
			if(viewableBy().get(i).tid==tid&&viewableBy().get(i).pid==pid){ viewableBy().remove(i); break; }
			i++;
		}
		
		
		stmt.close();
	} catch(SQLException exc) { exc.printStackTrace(); }
}


public void addAchievement(String achievementName, int pid) {
	Hashtable[] a = God.getAchievements();
	Player p = findPlayer(pid);
	int i = 0;
	
	while(i<a.length) {
		if(((String) a[i].get("aname")).equals(achievementName)) {
			//int j = 0;
			p.addAchievement(a[i]);
			break;
/* ONLY USE THIS IF YOU WANT TO ACTIVATE PERMISSIONS!
			int[] permissions = (int[]) a[i].get("permissions");
			while(j<permissions.length) {
				if(permissions[j]==ID) {
					p.addAchievement(a[i]);
					break;
				}
				j++;
			}*/
		}
		i++;
	}
}

public String getRewardBlock(int hourlyAmt, int pid, String additional[]) {
	long reward[] = getRewardOneHour(pid);
	String res = "<div style='font-family:BankGothic;'>Reward:&nbsp<br />" +
			"<div style='display:inline-block;width:80px;'><img src='AIFrames/icons/MetalIcon.png' alt='' />"+ hourlyAmt*reward[0]+ "</div>"+
		"<div style='display:inline-block;width:90px;'><img src='AIFrames/icons/TimberIcon.png' alt='' />"+ hourlyAmt*reward[1]+ "</div>"+
		"<div style='display:inline-block;width:85px;'><img src='AIFrames/icons/PlasticIcon.png' alt='' />"+ hourlyAmt*reward[2]+ "</div>"+
		"<div style='display:inline-block;width:80px;'><img src='AIFrames/icons/FoodIcon.png' alt='' />"+ hourlyAmt*reward[3]+ "</div>";
	
		int i = 0;
		if(additional!=null)
			while(i<additional.length){
				res+="<div style='display:inline-block;width:170px;'>"+additional[i]+"</div>";
				i++;
			}
		res+="</div>";
		return res;
}
}

class QuestPlayerComplete {
	private String memory;

	private int completed;
	private Player player;
	public int qpcid;
	private int qid;
	public QuestPlayerComplete(int completed, String memory, Player p, int qid,
			int qpcid) {
		this.completed = completed;
		this.memory = memory;
		this.player = p;
		this.qid = qid;
		this.qpcid = qpcid;
	}
	public String getMemory() {
		return memory;
	}
	public void setMemory(String memory) {
		this.memory = memory;
	}
	public int getCompleted() {
		return completed;
	}
	public void setCompleted(int completed) {
		this.completed = completed;
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public int getQid() {
		return qid;
	}
	public void setQid(int qid) {
		this.qid = qid;
	}
	
}

