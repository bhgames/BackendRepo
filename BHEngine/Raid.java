

package BHEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;

public class Raid {
/*
 * 
 */
	// Geno+Bomb = Glass
	// !Geno + Bomb = Strafe
	private boolean Genocide, Bomb, raidOver, debris, allClear = false, invade = false; 
	private long metal, timber, manmat, food;
	private ArrayList<AttackUnit> au;
	private UUID id, resupplyID;// for resupply runs.
	private Timestamp dockingFinished=null;
	private double distance; 
	private int ticksToHit, digAmt, scout = 0, totalTicks = 0, genoRounds = 0,
			support = 0; // do not confuse with au's support, this lets us know this raid is actually a support run.
	private Town town2, town1;
	private String name;
	private String[] bombTarget;
	UberConnection con; 
	GodGenerator God;
	
	
	public void makeScoutRun() {
		setScout(1);
	}
	
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
	public void setMemResupplyID(int ID) {
		setInt("resupplyID",ID);
	}
	public void setResupplyID(UUID ID) {
		resupplyID=ID;
	}
	public void endRaid() {
		setRaidOver(true);
	}
	
	public void setRaidValues(int raidID, double distance, int ticksToHit, Town town1, Town town2, 
								boolean Genocide,boolean allClear, int metal, int timber, int manmat, 
								int food,boolean raidOver, boolean Bomb, boolean invade, int totalTicks,
								String name,int genoRounds, boolean debris, int digAmt, UUID id, 
								Timestamp dockingFinished) {

					this.distance=distance; this.ticksToHit=ticksToHit; this.town1=town1;
					this.town2=town2; this.Genocide=Genocide; this.allClear=allClear;
					this.metal=metal;this.timber=timber;this.manmat=manmat;this.food=food;
					this.dockingFinished=dockingFinished; this.setDebris(debris);
					this.raidOver=raidOver;this.Bomb=Bomb;this.invade=invade;
					this.totalTicks=totalTicks;this.name=name;this.genoRounds=genoRounds; 
					this.digAmt=digAmt; this.id=id;
		}
	
	public Raid(UUID id, GodGenerator God) {
		this.God=God;
		this.con=God.con;try {
		UberPreparedStatement rus = con.createStatement("select * from raid where id = ?;");
		rus.setString(1,id.toString());
		ResultSet rrs = rus.executeQuery();
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
					rrs.getBoolean(8), rrs.getInt(9),rrs.getInt(10),rrs.getInt(11),rrs.getInt(12),rrs.getBoolean(7), rrs.getBoolean(19),rrs.getBoolean(23),rrs.getInt(25),rrs.getString(26),rrs.getInt(27),rrs.getBoolean(28),rrs.getInt(29),UUID.fromString(rrs.getString(31)),new Timestamp((new Date(rrs.getString(32))).getTime())); // this one has no sql addition!
			getAu();
					
			if(rrs.getBoolean(19)) bombTarget=PlayerScript.decodeStringIntoStringArray(rrs.getString(20));
			else bombTarget = new String[0];
			if(rrs.getInt(21)==1&&!rrs.getBoolean(7)) makeSupportRun();
			else if(rrs.getInt(21)==2&&!rrs.getBoolean(7)) makeOffSupportRun();

			else if(rrs.getInt(22)==1&&!rrs.getBoolean(7)) makeScoutRun();
			else if(rrs.getInt(22)==2&&!rrs.getBoolean(7)) makeDiscoveredScoutRun();
			
			if(rrs.getString(24)!=null&&!rrs.getBoolean(7)) setResupplyID(UUID.fromString(rrs.getString(24)));
	
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
		if(Genocide) setAllClear(false);// Note you need to watch in loaded raids, that raidOver and allClear need to be
		 								// set manually.
		// player1 hits player2's town2, only need town2 to access units.
	    id = UUID.randomUUID();

	    town1.attackServer().add(this); // even if this error happens, raid still works...

	    UberPreparedStatement stmt;
		try {

		   
			con =town1.getPlayer().God.con;

		    // First things first. We update the player table.
		    boolean transacted=false;
		    while(!transacted) {
		    	try {
		      
		      
		    		// let's add this raid and therefore get the rid out of it.
		    		stmt = con.createStatement("insert into raid (tid1, tid2, distance, ticksToHit, genocide, raidOver,allClear,m,t,mm,f,totalTicks,Bomb,invade,name,genoRounds,digAmt,auSizes,support,debris,id) values (?,?,?,?,?,false,false,0,0,0,0,?,?,?,?,?,?,?,?,?,?);");
				    stmt.setInt(1,town1.townID);
				    stmt.setInt(2,town2.townID);
				    stmt.setDouble(3,distance);
				    stmt.setInt(4,ticksToHit);
				    stmt.setBoolean(5,Genocide);
				    stmt.setInt(6,ticksToHit);
				    stmt.setBoolean(7,Bomb);
				    stmt.setBoolean(8,invade);
				    stmt.setString(9,name);
				    stmt.setInt(10,genoRounds);
				    stmt.setInt(11,digAmt);
				    stmt.setString(12,PlayerScript.toJSONString(au));
				    stmt.setInt(13,support);
				    stmt.setBoolean(14,debris);
				    stmt.setString(15,id.toString());
		      
				    // let's add this raid and therefore get the rid out of it.
				    
				    stmt.executeUpdate();
		      
				    stmt = con.createStatement("insert into raidSupportAU (rid,tid,tidslot,size) values (?,?,?,?);");
				    stmt.setString(1,id.toString());
				    stmt.setInt(2,getTown1().townID);
			      
			      	for(AttackUnit j:au) {
			      		stmt.setInt(3,j.getSlot());
					    stmt.setInt(4,j.getSize());
						stmt.executeUpdate(); // don't need original slot, need
						// current town slot for remembering!
			      	}
			      	//town1.attackServer.add(this); // <---- THIS NEEDS TO BE RETURNED TO NORMAL IF YOU GO BACK TO MEMORYLOADING!
		//	      System.out.println("I put on " +raidID);

			      	
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
			      
			      	transacted=true; 
		    	}
			    catch(MySQLTransactionRollbackException exc) { } 
		    }// need connection for attackunit adds!
		} catch(SQLException exc) { exc.printStackTrace();}	
	}
	
	private void add(AttackUnit j) {
		// THIS IS ONLY FOR DB USAGE...
		//getAu().add(j); 
		UberPreparedStatement stmt;
		try {
	
			stmt = con.createStatement("insert into raidSupportAU (rid,tid,tidslot,size) values (?,?,?,?);");
			stmt.setString(1,id.toString());
			stmt.setInt(2,getTown1().townID);
	      
			// First things first. We update the player table.
			boolean transacted=false;
			while(!transacted) {
	    	 
				try {
	      
					// let's add this raid and therefore get the rid out of it.
					if(j.getSlot()<6) {
						//stmt.executeUpdate("update raid set au" + (j.getSlot()+1) + " = " + j.getSize() + " where rid = " + raidID + ";");
					} else {
						stmt.setInt(3,j.getSlot());
						stmt.setInt(4,j.getSize());
						stmt.executeUpdate(); // don't need original slot, need
						// current town slot for remembering!
					}
			
					stmt.close(); transacted=true; 
				}
				catch(MySQLTransactionRollbackException exc) { System.out.println(id + " is having trouble adding units."); } 
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
	public String[] getBombTarget() {
		return bombTarget;
	}
	public void setBombTarget(String[] bombTarget) {
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
	public UUID getResupplyID() {
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
			UberPreparedStatement stmt = con.createStatement("update raid set " + fieldName + " = ? where id = ?;");
			stmt.setInt(1,toSet);
			stmt.setString(2,id.toString());
			
			stmt.execute();
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setDouble(String fieldName, double toSet) {
		try {
			UberPreparedStatement stmt = con.createStatement("update raid set " + fieldName + " = ? where id = ?;");
			stmt.setDouble(1,toSet);
			stmt.setString(2,id.toString());
			
			stmt.execute();
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setLong(String fieldName, long toSet) {
		try {
			UberPreparedStatement stmt = con.createStatement("update raid set " + fieldName + " = ? where id = ?;");
			stmt.setLong(1,toSet);
			stmt.setString(2,id.toString());
			
			stmt.execute();
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setBoolean(String fieldName, boolean toSet) {
		try {
			UberPreparedStatement stmt = con.createStatement("update raid set " + fieldName + " = ? where id = ?;");
			stmt.setBoolean(1,toSet);
			stmt.setString(2,id.toString());
			
			stmt.execute();
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setString(String fieldName, String toSet) {
		try {
			UberPreparedStatement stmt = con.createStatement("update raid set " + fieldName + " = ? where id = ?;");
			stmt.setString(1,toSet);
			stmt.setString(2,id.toString());
			
			stmt.execute();
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public int getInt(String toGet) {
		try {
			UberPreparedStatement stmt = con.createStatement("select "+toGet+" from raid where id = ?;");
			stmt.setString(1,id.toString());
			
			ResultSet rs = stmt.executeQuery();
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
			UberPreparedStatement stmt = con.createStatement("select "+toGet+" from raid where id = ?;");
			stmt.setString(1,id.toString());
			
			ResultSet rs = stmt.executeQuery();
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
			UberPreparedStatement stmt = con.createStatement("select "+toGet+" from raid where id = ?;");
			stmt.setString(1,id.toString());
			
			ResultSet rs = stmt.executeQuery();
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
			UberPreparedStatement stmt = con.createStatement("select "+toGet+" from raid where id = ?;");
			stmt.setString(1,id.toString());
			
			ResultSet rs = stmt.executeQuery();
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
			UberPreparedStatement stmt = con.createStatement("select "+toGet+" from raid where id = ?;");
			stmt.setString(1,id.toString());
			
			ResultSet rs = stmt.executeQuery();
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
			String auSizesStr = getString("auSizes");
			int auSizes[] = PlayerScript.decodeStringIntoIntArray(auSizesStr);
			ArrayList<AttackUnit> tau = getTown1().getAu();
			while(y<auSizes.length) {
				auHold = tau.get(y).returnCopy();
				if(getSupport()==1&&getTown1().getPlayer().ID!=getTown2().getPlayer().ID){
					 auHold.makeSupportUnit(auHold.getSlot(),getTown1().getPlayer(),getTown1().townID);
				} else if(getSupport()==2&&getTown1().getPlayer().ID!=getTown2().getPlayer().ID) auHold.makeOffSupportUnit(auHold.getSlot(),getTown1().getPlayer(),getTown1().townID);
	
				auHold.setSize(auSizes[y]);
				raidAU.add(auHold);
				
				y++;
			}
			/* OLDER SUPPORT CODE.
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
			rustown2.close();*/
			
			au= raidAU;
			//} catch(SQLException exc) { exc.printStackTrace(); }
			
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
	public void collapseDigOrRO(Raid r) {
		// only to be used for digs and ROs, so no support is ever present.
		int i = 0;
		while(i<r.getAu().size()) {
		//	System.out.println("Raid before:"+getAu().get(i) + ":" + getAu().get(i).getSize());

			getAu().get(i).setSize(getAu().get(i).getSize()+r.getAu().get(i).getSize());
		//	System.out.println("Raid after:"+getAu().get(i) + ":" + getAu().get(i).getSize());

			i++;
		}
		 double oldEngRate = getTown2().getDigAmt()*GodGenerator.engineerRORate;
		setDigAmt(getDigAmt()+r.getDigAmt());


			// means we need to reflect it here, too.
			for(AttackUnit rau:r.getAu()) {
				for(AttackUnit a:getTown2().getAu()) {
					if(a.getName().equals(rau.getName())&&a.getSupport()>0&&a.getOriginalPlayer().ID==getTown1().getPlayer().ID) {
						a.setSize(a.getSize()+rau.getSize());
					}
				}
				
			}
			getTown2().setDigAmt(getTown2().getDigAmt()+r.getDigAmt());
			 double newEngRate = getTown2().getDigAmt()*GodGenerator.engineerRORate;
		//	 System.out.println("old date was " + getDockingFinished().getTime() + " new engrate is " + newEngRate + " old is " + oldEngRate + " and dividing is " + oldEngRate/newEngRate
			//		 + " and multiplying is " + ((double)getDockingFinished().getTime())*oldEngRate/newEngRate + " and rounding is " + 
				// Math.round(((double)getDockingFinished().getTime())*oldEngRate/newEngRate));
			 if(getDockingFinished()!=null) // so if you send 10 guys to a 10 man army, then it's 10/20, you've knocked it down 50%.
			 setDockingFinished(new Timestamp((long) Math.round(((double)getDockingFinished().getTime())*oldEngRate/newEngRate)));
			 r.setRaidOver(true);
			 r.deleteMe();
			 getTown2().save(); // this is save-worthy.
			 save();

	}
	public void setMemSize(int index, int size) {
		if(index<6) setInt("au"+(index+1),size);
		else {
			try {
				UberPreparedStatement stmt = con.createStatement("update raidSupportAU set size = ? where tidslot = ? and rid = ? and tid = ?;");
				stmt.setString(3,id.toString());
				stmt.setInt(4,getTown1().townID);
				
				AttackUnit a = getAu().get(index);
				stmt.setInt(1,size);
				stmt.setInt(2,a.getSlot());
				stmt.executeUpdate();
				
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
		   int k = 0;
		   UberPreparedStatement stmt = con.createStatement("delete from raidSupportAU where tid = ? and rid = ? and tidslot = ?;");
		   stmt.setInt(1,getTown1().townID);
		   stmt.setString(2,id.toString());
		   ArrayList<AttackUnit> au = getAu();
		//   stmt.executeUpdate("update raid set ticksToHit=-1 where rid = " + raidID);
		   setTicksToHit(-1);
		   save(); // save the current state right before deletion.
		  while(k<au.size()) {
			  if(au.get(k).getSupport()>0) {
				
			  
				   stmt.setInt(3,au.get(k).getSlot());
				  stmt.executeUpdate();	
			  }
			  k++;
		  }
		  stmt.close();
		  getTown1().attackServer().remove(this); // and we remove it from memory....
		} catch(SQLException exc) { exc.printStackTrace(); }
	}
	
	synchronized public void save() {
   		  try {
   			  UberPreparedStatement stmt = con.createStatement("update raid set distance = ?, ticksToHit = ?, genocide = ?, raidOver = ?, allClear = ?, m = ?, t = ?, mm = ?, f = ?, auSizes=?, bomb = ?, bombtarget = ?, support = ?, scout = ?, invade = ?, resupplyID = ?, totalTicks = ?, dockingFinished = ? where id = ?;");
   	   		  ArrayList<AttackUnit> au = getAu();
   	   		
   	   		  stmt.setDouble(1,distance);
   	   		  stmt.setInt(2,ticksToHit);
   	   		  stmt.setBoolean(3,Genocide);
   	   		  stmt.setBoolean(4,raidOver);
   	   		  stmt.setBoolean(5,allClear);
   	   		  stmt.setLong(6,metal);
   	   		  stmt.setLong(7,timber);
   	   		  stmt.setLong(8,manmat);
   	   		  stmt.setLong(9,food);
   	   		  stmt.setString(10,PlayerScript.toJSONString(getAu()));
   	   		  stmt.setBoolean(11,Bomb);
   	   		  stmt.setString(12,PlayerScript.toJSONString(bombTarget));
   	   		  stmt.setInt(13,support);
   	   		  stmt.setInt(14,scout);
   	   		  stmt.setBoolean(15,invade);
   	   		  if(resupplyID!=null)
   	   		  stmt.setString(16,resupplyID.toString());
   	   		  else stmt.setString(16,"none");
   	   		  stmt.setInt(17,totalTicks);
   	   		  if(getDockingFinished()!=null)
			 stmt.setString(18,getDockingFinished().toString());
			 else stmt.setString(18,"2011-01-01 00:00:01");
   	   		  stmt.setString(19,id.toString());
   		  stmt.executeUpdate();
   		  
   		/*  int k = 6;
   		  while(k<au.size()) {
   				stmt.executeUpdate("update raidSupportAU set size = " + au.get(k).getSize() + " where tid = " + town1.townID + " and rid = " + raidID + " and tidslot = " + au.get(k).getSlot() + ";");		    			  
   			  k++;
   		  }*/
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
	public void setId(UUID id) {
		this.id = id;
	}
	public UUID getId() {
		return id;
	}
	public void setDockingFinished(Timestamp dockingFinished) {
		this.dockingFinished = dockingFinished;
	}
	public Timestamp getDockingFinished() {
		return dockingFinished;
	}
	
	
}
