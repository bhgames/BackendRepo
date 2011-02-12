

package BHEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;

public class Raid {
/*
 * 
 */
	private boolean Genocide;
		private boolean allClear = false; private long metal, timber, manmat, food;
	private ArrayList<AttackUnit> au;
	private double distance; private int resupplyID=-1; // for resupply runs.
	private int ticksToHit; private Town town2; private Town town1; private boolean raidOver;
	private boolean debris; private int digAmt;
	UberConnection con; public int raidID;
	public void makeScoutRun() {
		setScout(1);
	}
	GodGenerator God;
	
	private boolean Bomb; private int bombTarget=0; private int scout = 0;
	private int support = 0; // do not confuse with au's support, this lets us know this raid is actually a support run.
	private int totalTicks=0;private String name;
	private int genoRounds=0;
	// Geno+Bomb = Glass
	// !Geno + Bomb = Strafe
	
	private boolean invade = false;
	
	public void makeDiscoveredScoutRun() {
		setScout(2); // nondetectable by attack server except as to note this was once a scouting run.
	}
	public void makeSupportRun() {
		setSupport(1);
		if(getTown2().getPlayer().ID!=getTown1().getPlayer().ID&&!isRaidOver()) {
			// this means all of these units need to be labeled as support units since they are foreign.
			int i = 0;
			AttackUnit a;
			while(i<getAu().size()) {
				 a = getAu().get(i);
				a.makeSupportUnit(a.getSlot(),getTown1().getPlayer(),getTown1().townID);
				
				i++;
			}
		}
	}
	public void setMemResupplyID(int ID) {
		setInt("resupplyID",ID);
	}
	public void setResupplyID(int ID) {
		resupplyID=ID;
	}
	public void makeOffSupportRun() {
		// THIS MUST BE USED AFTER AUS HAVE BEEN ADDED!
		
		setSupport(2);
		if(getTown2().getPlayer().ID!=getTown1().getPlayer().ID&&!isRaidOver()) {
			// this means all of these units need to be labeled as support units since they are foreign.
			int i = 0;
			AttackUnit a;
			while(i<getAu().size()) {
				 a = getAu().get(i);
				a.makeOffSupportUnit(a.getSlot(),getTown1().getPlayer(),getTown1().townID);
				i++;
			}
		}
	}
	public void endRaid() {
		setRaidOver(true);
	}
	
	public void setRaidValues(int raidID, double distance, int ticksToHit, Town town1, Town town2, boolean Genocide,boolean allClear,
			int metal, int timber, int manmat, int food,boolean raidOver, boolean Bomb, boolean invade, int totalTicks,String name,int genoRounds, boolean debris, int digAmt) {

					this.distance=distance; this.ticksToHit=ticksToHit; this.town1=town1;
					this.town2=town2; this.Genocide=Genocide; this.allClear=allClear;
					this.metal=metal;this.timber=timber;this.manmat=manmat;this.food=food;
					this.setDebris(debris);
					this.raidOver=raidOver;this.Bomb=Bomb;this.invade=invade;
					this.totalTicks=totalTicks;this.name=name;this.genoRounds=genoRounds; this.digAmt=digAmt;
		}
	public Raid(int raidID, double distance, int ticksToHit, Town town1, Town town2, boolean Genocide,boolean allClear,
			int metal, int timber, int manmat, int food,boolean raidOver,ArrayList<AttackUnit> au, boolean Bomb, boolean invade, int totalTicks,String name,int genoRounds,boolean debris, int digAmt) {
		// This constructor does not use the support integer because it is rarely used compared to other things
		// and so to save space that is exported as an extra usable method. It is only necessary
		// when creating raids.
		// this constructor is for when a raid is being loaded into memory.
	
		if(distance==0) distance=1;
		this.digAmt=digAmt;
		this.setDebris(debris);
		this.distance=distance; this.ticksToHit=ticksToHit; this.town1=town1;
		this.town2=town2; this.Genocide=Genocide; this.allClear=allClear;
		this.metal=metal;this.timber=timber;this.manmat=manmat;this.food=food;
		this.raidOver=raidOver;this.au=au;this.Bomb=Bomb;this.invade=invade;
		this.totalTicks=totalTicks;this.name=name;this.genoRounds=genoRounds;

			UberStatement stmt;
			try {

		   
		       con =town1.getPlayer().God.con;
		       God = town1.getPlayer().God;
		      stmt = con.createStatement();
		      
		      // First things first. We update the player table.
		      boolean transacted=false;
		      while(!transacted) {
		    	  try {
		      
		      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
		      
		      // let's add this raid and therefore get the rid out of it.
		   
		      
		      
		      stmt.executeUpdate("insert into raid (tid1, tid2, distance, ticksToHit, genocide, raidOver,allClear,m,t,mm,f,totalTicks,Bomb,invade,name,genoRounds,digAmt) values (" +
		    		  town1.townID + "," + town2.townID + "," + distance + "," + ticksToHit + "," + Genocide + "," + false + "," + 
		    		  false + "," + 0+ "," +0 + "," + 0 + "," +0 + "," + ticksToHit+"," + Bomb + "," + invade + ",\"" + name +  "\"," + genoRounds +","+digAmt+ ");");
		      stmt.execute("commit;");
		      
			     Thread.currentThread().sleep(10);

		      ResultSet ridstuff = stmt.executeQuery("select rid from raid where tid1 = " + town1.townID + " and raidOver = false");

		      while(ridstuff.next()) {
		    	  int j = 0;
		    	  ArrayList<Raid> attackServer = town1.attackServer();
		    	  while(j<attackServer.size()) {
		    		  if(attackServer.get(j).raidID==ridstuff.getInt(1)) break;
		    		  j++;
		    	  }
		    	  
		    	  if(j==attackServer.size()) break; // means we found no raid accompanying this raidID.
		      }
		      
		      	raidID=(ridstuff.getInt(1));
				//town1.attackServer.add(this); // <---- THIS NEEDS TO BE RETURNED TO NORMAL IF YOU GO BACK TO MEMORYLOADING!
			//      System.out.println("I put on " +raidID);

		      ridstuff.close();
			 /* Thread.currentThread().sleep(100);

		      
		      int timesTried = 0;
		      ArrayList<Raid> a  = town1.attackServer();
		      while(a.size()<=0&&timesTried<1000) { // we wait longer for raids...fucking a raid up is really bad...REALLY.
		    	  // because we add shit to it after it is made! Of course, could fuck up oppositely, and take a raid already
		    	  // on there and add shit to it, either way, bad for biz. So we wait 100ms before we even try.
			  Thread.currentThread().sleep(10);
		      a= town1.attackServer();
		      timesTried++;
		      
		      }		    
		      raidID=a.get(a.size()-1).raidID;*/
		      int i = 0;
		      while(i<au.size()) {
		    	  add(au.get(i));
		    	  i++;
		      }

		      stmt.close(); 
		      
		      transacted=true; }
		    	  catch(MySQLTransactionRollbackException exc) { } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      }// need connection for attackunit adds!
			} catch(SQLException exc) { exc.printStackTrace();}

		
	

	}
	
	public Raid(int raidID, GodGenerator God) {
		this.raidID=raidID;
		this.God=God;
		this.con=God.con;try {
		UberStatement rus = con.createStatement();

		ResultSet rrs = rus.executeQuery("select * from raid where rid = " + raidID);
		// so we don't want it to load if raidOver is true and ticksToHit is 0. Assume 0 is not, 1 is on, for ttH. !F = R!T
		// Then F = !(R!T) = !R + T;
		while(rrs.next()) {
	

			
			// First, to seek out town 2, use a query!
			
			Town town1 = God.findTown(rrs.getInt(1));
			Town town2Obj = God.findTown(rrs.getInt(2));
			
	
			//double distance = Math.sqrt((town2Obj.x-Town1.x)*(town2Obj.x-Town1.x) + (town2Obj.y-Town1.y)*(town2Obj.y-Town1.y));

			//
			//	public Raid(int raidID, double distance, int ticksToHit, Town town1, Town town2, boolean Genocide,boolean allClear,
			//int metal, int timber, int manmat, int food,boolean raidOver,ArrayList<AttackUnit> au, boolean Bomb) {
			int y = 0;
		
			 setRaidValues(rrs.getInt(3),rrs.getDouble(4),rrs.getInt(5),town1,town2Obj,rrs.getBoolean(6),
					rrs.getBoolean(8), rrs.getInt(9),rrs.getInt(10),rrs.getInt(11),rrs.getInt(12),rrs.getBoolean(7), rrs.getBoolean(19),rrs.getBoolean(23),rrs.getInt(25),rrs.getString(26),rrs.getInt(27),rrs.getBoolean(28),rrs.getInt(29)); // this one has no sql addition!
			getAu();
					
			if(rrs.getBoolean(19)) bombTarget=rrs.getInt(20);
			if(rrs.getInt(21)==1&&!rrs.getBoolean(7)) makeSupportRun();
			else if(rrs.getInt(21)==2&&!rrs.getBoolean(7)) makeOffSupportRun();

			else if(rrs.getInt(22)==1&&!rrs.getBoolean(7)) makeScoutRun();
			else if(rrs.getInt(22)==2&&!rrs.getBoolean(7)) makeDiscoveredScoutRun();
			
			if(rrs.getInt(24)!=-1&&!rrs.getBoolean(7)) setResupplyID(rrs.getInt(24));
	
		}
		
				rrs.close();rus.close(); } catch(SQLException exc) { exc.printStackTrace(); }
	/*	this.distance=getMemDistance(); this.ticksToHit=getMemTicksToHit(); getTown1();
		getTown1(); this.Genocide=isMemGenocide(); this.allClear=isMemAllClear();
		this.metal=getMemMetal();this.timber=getMemTimber();this.manmat=getMemManmat();this.food=getMemFood();
		this.raidOver=isMemRaidOver();getAu();this.Bomb=isMemBomb();this.invade=isMemInvade();
		this.totalTicks=getMemTotalTicks();this.name=getMemName();this.genoRounds=getMemGenoRounds();*/
		// we set nothing else!
	}
	public Raid(double distance, int ticksToHit, Town town1, Town town2, boolean Genocide, boolean Bomb, int support,boolean invade, String name, boolean debris,ArrayList<AttackUnit> au,int digAmt) {
		// Can't do an infinite number of arguments here so need to add manually.
		// holds distance and ticksToHit in this object.
		this.setInvade(invade);
		this.setSupport(support);
		this.setDebris(debris);
		this.digAmt=digAmt;
		this.setName(name);
		this.au = au;
		this.setDistance(distance); this.setTicksToHit(ticksToHit);
		if(distance==0) distance=1;
		God = town1.getPlayer().God;
	
		
		this.setBomb(Bomb);
		 this.setTown2(town2);this.setTown1(town1);
		 
		 setRaidOver(false);
		this.setTotalTicks(ticksToHit);
		 this.setGenocide(Genocide);
		 if(Genocide) setAllClear(false); // Note you need to watch in loaded raids, that raidOver and allClear need to be
		 // set manually.
		// player1 hits player2's town2, only need town2 to access units.
			UberStatement stmt;
			try {

		   
		       con =town1.getPlayer().God.con;
		      stmt = con.createStatement();
		      
		      // First things first. We update the player table.
		      boolean transacted=false;
		      while(!transacted) {
		    	  try {
		      
		      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
		      
		      // let's add this raid and therefore get the rid out of it.
		      
		      stmt.executeUpdate("insert into raid (tid1, tid2, distance, ticksToHit, genocide, raidOver,allClear,m,t,mm,f,totalTicks,Bomb,support,invade,name,debris,digAmt) values (" +
		    		  town1.townID + "," + town2.townID + "," + distance + "," + ticksToHit + "," + Genocide + "," + false + "," + 
		    		  false + "," + 0+ "," +0 + "," + 0 + "," +0 + "," + ticksToHit+"," + Bomb + "," + support + "," + invade + ",\"" + name +  "\","+debris+","+digAmt+");");
		      stmt.execute("commit;");
		      
			     Thread.currentThread().sleep(10);

		      ResultSet ridstuff = stmt.executeQuery("select rid from raid where tid1 = " + town1.townID + " and raidOver = false");
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
		    	  ArrayList<Raid> attackServer = town1.attackServer();
		    	  while(j<attackServer.size()) {
		    		  if(attackServer.get(j).raidID==ridstuff.getInt(1)) break;
		    		  j++;
		    	  }
		    	  
		    	  if(j==attackServer.size()) break; // means we found no raid accompanying this raidID.
		      }
		      
		      	raidID=(ridstuff.getInt(1));
		     
				//town1.attackServer.add(this); // <---- THIS NEEDS TO BE RETURNED TO NORMAL IF YOU GO BACK TO MEMORYLOADING!
		//	      System.out.println("I put on " +raidID);
			      	town1.attackServer().add(this); // even if this error happens, raid still works...

		      ridstuff.close();
		      
		     stmt.execute("commit;");
		      /*
		      int timesTried = 0;
		      ArrayList<Raid> a  = town1.attackServer();
		      while(a.size()<=0&&timesTried<10) {
			  Thread.currentThread().sleep(10);
		      a= town1.attackServer();
		      timesTried++;
		      
		      }		    
		      raidID=a.get(a.size()-1).raidID;*/

		      stmt.close(); 
		      
		      transacted=true; }
		    	  catch(MySQLTransactionRollbackException exc) { } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		      }// need connection for attackunit adds!
			} catch(SQLException exc) { exc.printStackTrace();}

		
	}
	
	public void add(AttackUnit j) {
		// THIS IS ONLY FOR DB USAGE...
	//getAu().add(j); 
	UberStatement stmt;
	try {

    
      stmt = con.createStatement();
      
      // First things first. We update the player table.
      boolean transacted=false;
      while(!transacted) {
    	 
      try {
      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
      
      // let's add this raid and therefore get the rid out of it.
		if(j.getSlot()<6) {
      stmt.executeUpdate("update raid set au" + (j.getSlot()+1) + " = " + j.getSize() + " where rid = " + raidID + ";");
		} else {
			
			stmt.executeUpdate("insert into raidSupportAU (rid,tid,tidslot,size) values (" + raidID + "," +
					getTown1().townID + "," + j.getSlot() + "," + j.getSize() + ");"); // don't need original slot, need
			// current town slot for remembering!
			
		}
		
      stmt.execute("commit;");stmt.close(); transacted=true; }
      catch(MySQLTransactionRollbackException exc) { System.out.println(raidID + " is having trouble adding units."); } 
      } 
		} catch(SQLException exc) { exc.printStackTrace(); }

	}
	
	/*public void add(AttackUnit j) {
		// THIS IS ONLY FOR DB USAGE...
	getAu().add(j); 
	UberStatement stmt;
	try {

    
      stmt = con.createStatement();
      
      // First things first. We update the player table.
      boolean transacted=false;
      while(!transacted) {
    	  
      try {
      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
      
      // let's add this raid and therefore get the rid out of it.
		if(getAu().size()<=6) {

      stmt.executeUpdate("update raid set au" + getAu().size() + " = " + j.size + " where rid = " + raidID + ";");
		} else {
			stmt.executeUpdate("insert into raidSupportAU (rid,tid,tidslot,size) values (" + raidID + "," +
					getTown1().townID + "," + j.slot + "," + j.size + ");"); // don't need original slot, need
			// current town slot for remembering!
			
		}
		
      stmt.execute("commit;");stmt.close(); transacted=true; }
      catch(MySQLTransactionRollbackException exc) { } 
      } 
		} catch(SQLException exc) { exc.printStackTrace(); }

	}*/
	
	public boolean isGenocide() {
		return Genocide;
	}
	public void setGenocide(boolean genocide) {
		Genocide = genocide;
	}
	public boolean isAllClear() {
		return allClear;
	}
	public void setAllClear(boolean allClear) {
		this.allClear = allClear;
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
	public boolean isRaidOver() {
		return raidOver;
	}
	public void setRaidOver(boolean raidOver) {
		this.raidOver = raidOver;
	}
	public boolean isBomb() {
		return Bomb;
	}
	public void setBomb(boolean bomb) {
		Bomb = bomb;
	}
	public int getBombTarget() {
		return bombTarget;
	}
	public void setBombTarget(int bombTarget) {
		this.bombTarget = bombTarget;
	}
	public int getScout() {
		return scout;
	}
	public void setScout(int scout) {
		this.scout = scout;
	}
	public int getSupport() {
		return support;
	}
	public void setSupport(int support) {
		this.support = support;
	}
	public int getTotalTicks() {
		return totalTicks;
	}
	public void setTotalTicks(int totalTicks) {
		this.totalTicks = totalTicks;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getGenoRounds() {
		return genoRounds;
	}
	public void setGenoRounds(int genoRounds) {
		this.genoRounds = genoRounds;
	}
	public boolean isInvade() {
		return invade;
	}
	public void setInvade(boolean invade) {
		this.invade = invade;
	}
	public int getResupplyID() {
		return resupplyID;
	}
	public void setAu(ArrayList<AttackUnit> au) {
		this.au = au;
	}
	public void setTown2(Town town2) {
		this.town2 = town2;
	}
	public void setTown1(Town town1) {
		this.town1 = town1;
	}
	/*public void closeCon() {
		
		try {con.close();		} catch(SQLException exc) { exc.printStackTrace(); }

	}*/
	public void setInt(String fieldName, int toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update raid set " + fieldName + " = " + toSet + " where rid = " + raidID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setDouble(String fieldName, double toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update raid set " + fieldName + " = " + toSet + " where rid = " + raidID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setLong(String fieldName, long toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update raid set " + fieldName + " = " + toSet + " where rid = " + raidID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setBoolean(String fieldName, boolean toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update raid set " + fieldName + " = " + toSet + " where rid = " + raidID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setString(String fieldName, String toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update raid set " + fieldName + " = \"" + toSet + "\" where rid = " + raidID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public int getInt(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from raid where rid = " + raidID);
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
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from raid where rid = " + raidID);
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
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from raid where rid = " + raidID);
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
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from raid where rid = " + raidID);
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
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from raid where rid = " + raidID);
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
	
	public ArrayList<AttackUnit> getAu() {
		if(au==null) {
		int y = 0;

		
		ArrayList<AttackUnit> raidAU = new ArrayList<AttackUnit>();
		AttackUnit auHold;
		ArrayList<AttackUnit> tau = getTown1().getAu();
		while(y<6) {
			 auHold = tau.get(y).returnCopy();
			if(getSupport()==1&&getTown1().getPlayer().ID!=getTown2().getPlayer().ID){
				 auHold.makeSupportUnit(auHold.getSlot(),getTown1().getPlayer(),getTown1().townID);
			} else if(getSupport()==2&&getTown1().getPlayer().ID!=getTown2().getPlayer().ID) auHold.makeOffSupportUnit(auHold.getSlot(),getTown1().getPlayer(),getTown1().townID);

			auHold.setSize(getInt("au" + (y+1)));
			raidAU.add(auHold);
			
			y++;
		}
		
		try {
		
			UberStatement rustown2 = con.createStatement();
		 ResultSet rrs2 = rustown2.executeQuery("select * from raidSupportAU where rid = " + raidID + " and tid = " + getTown1().townID + " order by tidslot asc");
		 AttackUnit sAU;
		while(rrs2.next()) {
			int size = rrs2.getInt(4);
		 // right we have what we need, we know
			// we already have a reference to town1...
			int j =6; boolean found = false;
			while(j<tau.size()) {
				if(tau.get(j).getSlot()==rrs2.getInt(3)){
					 sAU = tau.get(j).returnCopy();
						// attackunits are already on town 1, so they were ported
						// here, just need sizes!
						sAU.setSize(size);
						raidAU.add(sAU); 
				}
				j++;
			}
			
		
		}
		
		rrs2.close();
		rustown2.close();
		
		au= raidAU;
		} catch(SQLException exc) { exc.printStackTrace(); }
		
		}
		return au;
		
		
	}
	
	public Town getTown2() {
		if(town2==null) {
		town2= God.findTown(getInt("tid2"));
		
		}
		return town2;
		
	}
	public void setSize(int index, int size) {
		getAu().get(index).setSize(size);
	}
	public void setMemSize(int index, int size) {
		if(index<6) setInt("au"+(index+1),size);
		else {
			try {
				UberStatement stmt = con.createStatement();
				AttackUnit a = getAu().get(index);
				stmt.executeUpdate("update raidSupportAU set size = " + size + " where tidslot = " +a.getSlot() + " and rid = " + raidID + " and tid = " + getTown1().townID);
				
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); System.out.println("Your shit is a-okay."); } 
		}
	}
	
	public Town getTown1() {
		if(town1==null) {
			town1= God.findTown(getInt("tid1"));
			
			}
			return town1;	
			}
	public void setMemScout(int scout) {
		setInt("scout",scout);
	}
	public int getMemScout() {
		return getInt("scout");
	}
	public void setMemRaidOver(boolean raidOver) {
		setBoolean("raidOver",raidOver);
	}
	public boolean isMemRaidOver() {
		return getBoolean("raidOver");
	}
	public void setMemTicksToHit(int ticksToHit) {
		setInt("ticksToHit",ticksToHit);
	}
	public int getMemTicksToHit() {
		return getInt("ticksToHit");
	}
	public void deleteMe() {
		
		  // Although raids are not removed from the sql server, their supporting au should be. They are merely placeholders
		  // and will either survive or goto zero, besides, the statreports exist to tell the true story.
		  // By placeholders I mean they are like extra au fields for the raid, the actual
		  // data is held on the supportAU table.
		try {
		   int k = 6;
		   UberStatement stmt = con.createStatement();
		   ArrayList<AttackUnit> au = getAu();
		//   stmt.executeUpdate("update raid set ticksToHit=-1 where rid = " + raidID);
		   setTicksToHit(-1);
		   save(); // save the current state right before deletion.
		  while(k<au.size()) {
				stmt.executeUpdate("delete from raidSupportAU where tid = " + getTown1().townID + " and rid = " + raidID + 
						" and tidslot = " + au.get(k).getSlot() + ";");		    			  
			  k++;
		  }
		  stmt.close();
		  getTown1().attackServer().remove(this); // and we remove it from memory....
		} catch(SQLException exc) { exc.printStackTrace(); }
	}
	
	synchronized public void save() {
   		  try {
   			  UberStatement stmt = con.createStatement();
   	   		  ArrayList<AttackUnit> au = getAu();
   	   		  if(getTown1().townID==3569)
   	   		  System.out.println("I am raid " + raidID);
   		String   update="";
   		  try {
   			
   		   update = "update raid set distance = " + distance + ", ticksToHit = " + ticksToHit + ", genocide = " +
   		  Genocide + ", raidOver = " + raidOver + ", allClear = " + allClear + ", m = "+  metal + ", t = " + 
   		  timber + ", mm = " + manmat + ", f = " + food +  ", au1 = " +
	    	  au.get(0).getSize() + ", au2 = " + au.get(1).getSize() + ", au3 = " + au.get(2).getSize() + ", au4 = " + au.get(3).getSize() +
	    	  ", au5 = " + au.get(4).getSize() + ", au6 = " + au.get(5).getSize() + ", bomb = " + Bomb
	    	  + ", bombtarget = " + bombTarget + ", support = " + support + ", scout = " + scout+ ", invade = " + invade + ", resupplyID = " +
	    	  resupplyID + ", totalTicks = " + totalTicks + " where rid = " + raidID +";";
   		  } catch(IndexOutOfBoundsException exc) {
   			  exc.printStackTrace();
   			  System.out.println("Found");
   		  }
   		  stmt.executeUpdate(update);
   		  
   		  int k = 6;
   		  while(k<au.size()) {
   				stmt.executeUpdate("update raidSupportAU set size = " + au.get(k).getSize() + " where tid = " + town1.townID + " and rid = " + raidID + " and tidslot = " + au.get(k).getSlot() + ";");		    			  
   			  k++;
   		  }
   		  stmt.close();
   		  
   		  // either being added back to town or was completely wiped out, either way, should be removed from memory.
	} catch(SQLException exc) { exc.printStackTrace(); }
	}
	
	public void setMemInvade(boolean invade) {
		setBoolean("invade",invade);

	}
	public boolean isMemInvade() {
		return getBoolean("invade");
	}
	public void setMemGenocide(boolean genocide) {
		setBoolean("genocide",genocide);

	}
	public boolean isMemGenocide() {
		return getBoolean("genocide");
	}
	public void setMemAllClear(boolean allClear) {
		setBoolean("allClear",allClear);
	}
	public boolean isMemAllClear() {
		return getBoolean("allClear");
	}
	public void setMemGenoRounds(int genoRounds) {
		setInt("genorounds",genoRounds);
	}
	public int getMemGenoRounds() {
		return getInt("genorounds");
	}
	public void setMemTotalTicks(int totalTicks) {
		setInt("totalTicks",totalTicks);
	}
	public int getMemTotalTicks() {
		return getInt("totalTicks");
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
	public void setMemManmat(long manmat) {
		setLong("mm",manmat);
	}
	public long getMemManmat() {
		return getLong("mm");
	}
	public void setMemFood(long food) {
		setLong("f",food);
	}
	public long getMemFood() {
		return getLong("f");
	}
	public int getMemResupplyID() {
		return getInt("resupplyID");
	}
	
	public void setMemSupport(int support) {
		setInt("support",support);
	}
	public int getMemSupport() {
		return getInt("support");
	}
	public void setMemBomb(boolean bomb) {
		setBoolean("bomb",bomb);
	}
	public boolean isMemBomb() {
		return getBoolean("bomb");
	}
	public void setMemBombTarget(int bombTarget) {
		setInt("bombtarget",bombTarget);
	}
	public int getMemBombTarget() {
		return getInt("bombtarget");
	}
	public void setMemDistance(double distance) {
		setDouble("distance",distance);
	}
	public double getMemDistance() {
		return getDouble("distance");
	}
	public void setMemName(String name) {
		setString("name",name);
	}
	public String getMemName() {
		return getString("name");
	}
	public void setDebris(boolean debris) {
		this.debris = debris;
	}
	public boolean isDebris() {
		return debris;
	}
	public void setDigAmt(int digAmt) {
		this.digAmt = digAmt;
	}
	public int getDigAmt() {
		return digAmt;
	}
	
	
}
