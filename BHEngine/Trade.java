package BHEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;

public class Trade {

	public int tradeID;
	private UberConnection con; 
	private GodGenerator God;
	
	private long metal, timber, manmat, food;
	private double distance; 
	private int ticksToHit; private Town town2; private Town town1; private boolean tradeOver;
	private TradeSchedule ts;
	private int totalTicks=0; private int traders;
	
	public void setTradeValues(int tradeID, double distance, int ticksToHit, Town town1, Town town2,
			long metal, long timber, long manmat, long food,boolean tradeOver,int traders, int totalTicks) {
		// This constructor does not use the support integer because it is rarely used compared to other things
		// and so to save space that is exported as an extra usable method. It is only necessary
		// when creating raids.
		// this constructor is for when a raid is being loaded into memory.
		this.totalTicks=totalTicks;
		this.distance=distance; this.ticksToHit=ticksToHit;
		 this.town2=town2;this.town1 = town1;
		 this.tradeID = tradeID; this.metal=metal;this.timber=timber;
		 this.manmat=manmat;this.food=food;this.tradeOver=tradeOver;
		 this.traders=traders;
		

	
}

	public Trade(int tradeID,GodGenerator God) {
			// This constructor does not use the support integer because it is rarely used compared to other things
			// and so to save space that is exported as an extra usable method. It is only necessary
			// when creating raids.
			// this constructor is for when a raid is being loaded into memory.
		con = God.con;
		this.God = God;
		
		this.tradeID=tradeID;
		try {
			UberStatement rus = con.createStatement();
			
		ResultSet rrs = rus.executeQuery("select * from trade where trid = " +tradeID);
			// so we don't want it to load if raidOver is true and ticksToHit is 0. Assume 0 is not, 1 is on, for ttH. !F = R!T
			// Then F = !(R!T) = !R + T;
			while(rrs.next()) {
		
				// before anything, need to find trade schedule.
	
			//	System.out.println(holdTradeSchedule);
				
				// First, to seek out town 2, use a query!
				Town town1 = God.findTown(rrs.getInt(1));
				Town town2Obj = God.findTown(rrs.getInt(2));

			
				 setTradeValues(rrs.getInt(3),rrs.getDouble(4),rrs.getInt(5),town1,town2Obj,rrs.getLong(7),
						 rrs.getLong(8),rrs.getLong(9),rrs.getLong(10),rrs.getBoolean(6),rrs.getInt(11),rrs.getInt(12)); // this one has no sql addition!
		
			}
					rrs.close();rus.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		
	}
	
	public Trade(Town town1, Town town2, long m, long t, long mm, long f, TradeSchedule ts, int traders) {
		// Can't do an infinite number of arguments here so need to add manually.
		// holds distance and ticksToHit in this object.
		Town t1 = town1;
		Town t2 = town2; // for ease of reading.
		con = town1.getPlayer().God.con;
		God = town1.getPlayer().God;
		
		
		// LINKED TO GET TRADEETA
		double distance=0;
		if(ts.isStockMarketTrade()) {
			distance=GodGenerator.tradeDistance;
		} else {
		 distance = (Math.sqrt((t1.getX()-t2.getX())*(t1.getX()-t2.getX()) + (t1.getY()-t2.getY())*(t1.getY()-t2.getY())));

		}
		int ticksToHit=(town1.getPlayer().getPs().b.getTradeETA(t1.townID,t2.townID));
		 
		 
		this.ts=ts;
	
		 this.town2=town2;this.town1 = town1; tradeOver=false;
		this.totalTicks=ticksToHit;
		metal=m;timber=t;manmat=mm;food=f;
		this.traders=traders;
		this.distance=distance;
		this.ticksToHit=ticksToHit;
		
		this.totalTicks=ticksToHit;
		tradeOver=false;
			UberStatement stmt;
			
			try {

		      
		      stmt = t1.getPlayer().God.con.createStatement();
		      
		      // First things first. We update the player table.
		      boolean transacted=false;
		      while(!transacted) {
		    	  try {
		      
		      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
		      System.out.println("Trade schedule id is " + ts.tradeScheduleID);
		      // let's add this raid and therefore get the rid out of it.
		      if(town2!=null)
		      stmt.executeUpdate("insert into trade (tid1, tid2, distance, ticksToHit,m,t,mm,f,totalTicks,tsid,traders) values (" +
		    		  town1.townID + "," + town2.townID + "," + distance + "," + ticksToHit
		    		  + "," + m + "," + t + "," +mm + "," + f + "," + ticksToHit+ ","+ts.tradeScheduleID + "," + traders + ");");
		      stmt.execute("commit;");
		      Thread.currentThread().sleep(10);
		      
		      ResultSet ridstuff = stmt.executeQuery("select trid from trade where tid1 = " + town1.townID + " and tradeOver = false");
		      /*
		       *Okay, search out all raids on the db for this town, and compare them to the raid server that the town has. There should be an rid
		       *corresponding to each raid on there, and one that isn't. This one is our rid.
		       *Now, if the user is running concurrent threads and they both call to this to add the raid at the same time, then there will be two
		       *separate raidIDs on there. Still something of a problem, but we have to acknowledge the quickness of this maneuver - this thing will
		       *literally be lightning quick. It'll snatch up that rid and add this one to the server without a hesitation.
		       *if there is a problem, we can always assign a huge random number tempid to it, and use that as an extra comparison. If the user
		       *does do two things at once, two raids, then I have to ask - what is the problem with switching the rids up? You see, if he does manage
		       *to do it on the same town at the same time within a time frame that would allow both to be unaccounted for at the same time, then as soon
		       *as the rids are switched, the player object would update them both with correct values from us. There we go. It happens so quickly.
		       */
		      
		      while(ridstuff.next()) {
		    	  int j = 0;
		    	  
		    	  while(j<town1.tradeServer().size()) {
		    		  if(town1.tradeServer().get(j).tradeID==ridstuff.getInt(1)) break;
		    		  j++;
		    	  }
		    	  
		    	  if(j==town1.tradeServer().size()) break; // means we found no raid accompanying this raidID.
		      }
		      
		      	tradeID = ridstuff.getInt(1);
		      	town1.tradeServer().add(this);
		      	ridstuff.close();
		     /* int timesTried = 0;
		      ArrayList<Trade> a  = town1.tradeServer();
		      while(a.size()<=0&&timesTried<10) {
			  Thread.currentThread().sleep(10);
		      a= town1.tradeServer();
		      timesTried++;
		      
		      }
		      tradeID=a.get(a.size()-1).tradeID;*/

		      
		      stmt.close(); transacted=true;
		      }
		    	  catch(MySQLTransactionRollbackException exc) { } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      }// need connection for attackunit adds! Not for trades though.
			 } catch(SQLException exc) { exc.printStackTrace(); }

		
	}
	
	public Town getTown2() {
		if(town2==null)
	town2= God.getTown(getInt("tid2"));
		
		return town2;

	}
	public Town getTown1() {
		
		if(town1==null)
			town1= God.getTown(getInt("tid1"));
				
				return town1;	}

	public void deleteMe() {

		
		  // Although raids are not removed from the sql server, their supporting au should be. They are merely placeholders
		  // and will either survive or goto zero, besides, the statreports exist to tell the true story.
		  // By placeholders I mean they are like extra au fields for the raid, the actual
		  // data is held on the supportAU table.
		try {
		   
		   UberStatement stmt = con.createStatement();
		   //stmt.executeUpdate("update trade set ticksToHit=-1 where trid = " + tradeID);
		   setTicksToHit(-1);
		   save();
		   getTown1().tradeServer().remove(this);
		   stmt.close();
		  
		} catch(SQLException exc) { exc.printStackTrace(); }
		}
	synchronized public void save() {
		try {
		  String update="";
		  UberStatement stmt = con.createStatement();
		  try {
		   update = "update trade set distance = " + distance + ", ticksToHit = " + ticksToHit + ", tradeOver = " + tradeOver + 
		   ", m = "+  metal + ", t = " + 
		  timber + ", mm = " + manmat + ", f = " + food +  ", totalTicks = " + totalTicks +", tsid = " + getTs().tradeScheduleID + " where trid = " + tradeID +";";
		  } catch(IndexOutOfBoundsException exc) {
			  exc.printStackTrace();
		  }
		  stmt.executeUpdate(update);
		  stmt.close();
	
		
	} catch(SQLException exc) { exc.printStackTrace(); }
	}
	
	public long getMetal() {
		return metal;
	}

	public void setMetal(long metal) {
		this.metal = metal;
	}

	public long getTimber() {
		return timber;
	}

	public void setTimber(long timber) {
		this.timber = timber;
	}

	public long getManmat() {
		return manmat;
	}

	public void setManmat(long manmat) {
		this.manmat = manmat;
	}

	public long getFood() {
		return food;
	}

	public void setFood(long food) {
		this.food = food;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getTicksToHit() {
		return ticksToHit;
	}

	public void setTicksToHit(int ticksToHit) {
		this.ticksToHit = ticksToHit;
	}

	public boolean isTradeOver() {
		return tradeOver;
	}

	public void setTradeOver(boolean tradeOver) {
		this.tradeOver = tradeOver;
	}

	public int getTotalTicks() {
		return totalTicks;
	}

	public void setTotalTicks(int totalTicks) {
		this.totalTicks = totalTicks;
	}

	public int getTraders() {
		return traders;
	}

	public void setTraders(int traders) {
		this.traders = traders;
	}

	public void setTown2(Town town2) {
		this.town2 = town2;
	}

	public void setTown1(Town town1) {
		this.town1 = town1;
	}

	public void setMemTicksToHit(int ticksToHit) {
		setInt("ticksToHit",ticksToHit);
	}

	public int getMemTicksToHit() {
		return getInt("ticksToHit");
	}

	public void setMemTotalTicks(int totalTicks) {
		setInt("totalTicks",totalTicks);
	}

	public int getMemTotalTicks() {
		return getInt("totalTicks");
	}

	public void setMemDistance(double distance) {
		setDouble("distance",distance);
	}

	public double getMemDistance() {
		return getDouble("distance");
	}

	public void setMemTradeOver(boolean tradeOver) {
		setBoolean("tradeOver",tradeOver);
	}

	public boolean isMemTradeOver() {
		return getBoolean("tradeOver");
	}

	public void setTs(TradeSchedule ts) {
		this.ts=ts;
	}

	public TradeSchedule getTs() {
		if(ts==null) {
			int i = 0;
			ArrayList<TradeSchedule> tses = getTown1().tradeSchedules();
			int tradeScheduleID = getInt("tsid");
	//		if(tradeID==16197)
			System.out.println("Found a tsid of " + tradeScheduleID);
			while(i<tses.size()) {
				System.out.println("Checking ts " + tses.get(i).tradeScheduleID);
				if(tses.get(i).tradeScheduleID==tradeScheduleID) {
					this.ts=tses.get(i);
					break;
				}
				i++;
			}
			
		
		}
		
		return ts; 
	}

	public void setMemTraders(int traders) {
		setInt("traders",traders);
	}

	public int getMemTraders() {
		return getInt("traders");
	}

	public void setMemFood(long food) {
		setLong("f",food);
	}

	public long getMemFood() {
		return getLong("f");
	}

	public void setMemManmat(long manmat) {
		setLong("mm",manmat);

	}

	public long getMemManmat() {
		return getLong("mm");
	}

	public void setMemMetal(long metal) {
		setLong("m",metal);
	}

	public long getMemMetal() {
		return getLong("m");
	}

	public void setMemTimber(long timber) {
		setLong("t",timber);
	}

	public long getMemTimber() {
		return getLong("t");
	}
	public void setInt(String fieldName, int toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update trade set " + fieldName + " = " + toSet + " where trid = " + tradeID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setDouble(String fieldName, double toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update trade set " + fieldName + " = " + toSet + " where trid = " + tradeID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setLong(String fieldName, long toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update trade set " + fieldName + " = " + toSet + " where trid = " + tradeID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setBoolean(String fieldName, boolean toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update trade set " + fieldName + " = " + toSet + " where trid = " + tradeID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setString(String fieldName, String toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update trade set " + fieldName + " = \"" + toSet + "\" where trid = " + tradeID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public int getInt(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from trade where trid = " + tradeID);
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
	
	
	public double getDouble(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from trade where trid = " + tradeID);
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
	
	public long getLong(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from trade where trid = " + tradeID);
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
	
	public boolean getBoolean(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from trade where trid = " + tradeID);
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
	public String getString(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from trade where trid = " + tradeID);
			rs.next();
			String toRet=rs.getString(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return null;
	}
}
