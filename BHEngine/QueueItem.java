package BHEngine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import BattlehardFunctions.UserBuilding;

public class QueueItem {
public int qid;
private Building b;
public int bid;
private int ticksPerUnit=0;
private int AUNumber; // This is how many of them
private int AUtoBuild;
private int currTicks;
private long cost[];
private static int baseBuildTime=(int) Math.round(60*10.0/GodGenerator.gameClockFactor);
private int townsAtTime,originalAUAmt,totalNumber;
private GodGenerator God;
private UberConnection con;
public static int days = 5;

// inherently, deleteMe is false and is used by player to detect that this queue item should
// be deleted on the db as opposed to updated. If it's loaded in, it's clearly not over yet!

	public void setQueueValues(int qid, int AUtoBuild, int AUNumber, int currTicks,int townsAtTime,int originalAUAmt,int totalNumber) {
		this.AUNumber=AUNumber;this.AUtoBuild=AUtoBuild;this.currTicks=currTicks;this.townsAtTime=townsAtTime;this.originalAUAmt=originalAUAmt;
		this.totalNumber=totalNumber;
	}
	public QueueItem(int qid, Building b,GodGenerator God) {
		this.qid=qid;this.bid=b.bid;this.b=b;
		this.God=God;this.con=God.con;
		try {
		UberPreparedStatement qus = con.createStatement("select * from queue where qid = ?;");
		qus.setInt(1,qid);
		 ResultSet qrs = qus.executeQuery();
		// getting queries.
		
		 
		 while(qrs.next()) {
			 //	public QueueItem(int qid, int bid, int AUtoBuild, int AUNumber, int currTicks,Town t) {
			
			setQueueValues(qrs.getInt(1),qrs.getInt(3),qrs.getInt(4),qrs.getInt(5),qrs.getInt(10),qrs.getInt(11),qrs.getInt(12));
			 // will go from earliest to latest in pushing queues onto the stack...
		 }
		  qrs.close();
		 qus.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		getCost();
		 /*
		AUNumber = getMemAUNumber();
		AUtoBuild = getMemAUtoBuild();
		currTicks = getMemCurrTicks();
		getCost();
		townsAtTime = getMemTownsAtTime();
		originalAUAmt = getMemOriginalAUAmt();
		totalNumber = getMemTotalNumber();*/
	}
	
	
	
	public void incrTicks() {
		setCurrTicks(getCurrTicks() + 1);
	}
	public void decrTicks() {
		setCurrTicks(getCurrTicks() - 1);
	}

	public void resetTicks() {
		setCurrTicks(0);
	}
	public int getTicksPerUnit() {
		return ticksPerUnit; // KEEP THIS, IT AUTOSETS IT WITH MODIFY TICKS BEFORE USE IF TICKSPERUNIT=0!
	}
	public int returnTotalTicksLeft() {
		// returns the amount of ticks till the queue item would be complete if it's on the top of the stack.
		// However this is only if it's currently working, it doesn't
		// account for other queue items in front of it.
		return getTicksPerUnit()*getAUNumber()-getCurrTicks(); // <--- THIS IS NOW INCORRECT AS SHIT.
		// AUNumber * ticksPerUnit gives the number left if something
		// had just started building, by subtracting current ticks, we get
		// current amount.
	}
	
	public boolean isDone() {
		if(getAUNumber()<=0) return true;
		else return false;
	}
	public int returnQID() {
		return qid;
	}
	public int returnBID() {
		return bid;
	}/*
	public int returnMemAUtoBuild() {
		return getInt("AUtoBuild");
	}
	*/
	public Building getB() {
		return b;
	}



	public void setB(Building b) {
		this.b = b;
	}



	public int getAUNumber() {
		return AUNumber;
	}



	public void setAUNumber(int number) {
		AUNumber = number;
	}



	public int getAUtoBuild() {
		return AUtoBuild;
	}



	public void setAUtoBuild(int utoBuild) {
		AUtoBuild = utoBuild;
	}



	public int getCurrTicks() {
		return currTicks;
	}



	public void setCurrTicks(int currTicks) {
		this.currTicks = currTicks;
	}



	public int getTownsAtTime() {
		return townsAtTime;
	}



	public void setTownsAtTime(int townsAtTime) {
		this.townsAtTime = townsAtTime;
	}



	public int getOriginalAUAmt() {
		return originalAUAmt;
	}



	public void setOriginalAUAmt(int originalAUAmt) {
		this.originalAUAmt = originalAUAmt;
	}



	public int getTotalNumber() {
		return totalNumber;
	}



	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}



	public void setCost(long[] cost) {
		this.cost = cost;
	}
	/**
	 * The new unit tick getter.
	 * @param howMany - how many troops you are building(tanks are worth 10, juggers are worth 40, and so on. It's their expmod.
	 * @param totalEngineers
	 * @param t
	 * @return
	 */
	public static int getUnitTicks(int howMany, Town t) {
		Player p = t.getPlayer();
		double engEffect = p.God.Maelstrom.getEngineerEffect(t.getX(),t.getY());
		int engTech = p.getArchitecture();
		int totalEngineers = t.getTotalEngineers();
		totalEngineers=(int) Math.round(((double) totalEngineers) *(1+engEffect+.05*(engTech))+1);
		UserBuilding b[] = p.getPs().b.getUserBuildings(t.townID,"Manufacturing Plant");
		int totalNumLev = 0;
		for(UserBuilding bldg:b) {
			
			totalNumLev+=bldg.getLvl();
		}
		double bldgEffect = 1-(((double) totalNumLev)*.025);
		if(bldgEffect<.1) bldgEffect = .1;
		double totalEngEffect = 1-(((double) totalEngineers))*.005625;
		if(totalEngEffect<.1) totalEngEffect=.1;

		int totalTicks = (int) Math.round(howMany*baseBuildTime*totalEngEffect*bldgEffect);
		if(totalTicks<1) totalTicks=1;
		return totalTicks;
		
		
		
	}

	public static int getUnitTicksForMany(int theMany, int totalEngineers, Town t) {
		return getUnitTicks(theMany,t);
	/*	double addon;
		Player p = t.getPlayer();
		double engEffect = p.God.Maelstrom.getEngineerEffect(t.getX(),t.getY());
		int engTech = p.getEngTech();
		totalEngineers=(int) Math.round(((double) totalEngineers) *(1+engEffect+.05*(engTech-1))+1);

		
		
		ArrayList<Town> towns = p.towns();
		int i = 0;
		double avgLevel=0;
		 int k = 0;
		 int highLvl=0;

			avgLevel+=(int) Math.round(((double) p.God.getAverageLevel(t) ));
			int x = 0;
			while(x<t.bldg().size()) {
				if(t.bldg().get(x).getLvl()>highLvl) highLvl=t.bldg().get(x).getLvl();
				x++;
			}
		double percdifflvl = ((double) (highLvl-avgLevel))/100;
		
		double engAvgLevel = (int) Math.round(((double) (1.0-percdifflvl)*((double) avgLevel) + percdifflvl*((double) highLvl)));
		 k = 0;
		AttackUnit a; int popped = 0;
		ArrayList<AttackUnit> au = p.getAu();
		while(k<au.size()) {
			a = au.get(k);
			if(!a.getName().equals("locked")&&!a.getName().equals("empty"))popped++;
			k++;
		}
		k=0;
		int pop = p.God.getCombatPopWithExpMod(p);
		double cap = Building.getCap((int) engAvgLevel,false);
		double CSL = p.getPs().b.getCSLAtLevel(avgLevel,popped,p.towns().size());
		double ticks = 2*3600*24*Math.exp(1-((double) totalEngineers)/cap)/(Math.exp(1)*Math.pow(CSL,2));
		double chgdays=(int) Math.round(((double) QueueItem.days)*((double) engAvgLevel-2)/(((double) Town.maxBldgLvl)/6.0));
		if(chgdays>QueueItem.days) chgdays =QueueItem.days;	
		if(chgdays<=0) chgdays=(int) Math.round(((double) QueueItem.days)*((double) 1)/(((double) Town.maxBldgLvl)/6.0));

		double newTicks=0;
		// System.out.println("theMany is " + theMany + " and ticks is  " + ticks);
		 i = 0;
		 while(i<theMany) {
			 if(pop+i>CSL) 
				 newTicks+=ticks*Math.pow((pop+i),1+.5*((double) (pop+i)-CSL)/((double) CSL));
			 else
				 newTicks+=ticks*(pop+i);
			 i++;
		 }
		 
		 ticks=newTicks;
		// System.out.println("ticks is " + ticks);

		 ticks*=chgdays/GodGenerator.gameClockFactor;
		 if(p.getUbTimer()>0) ticks/=2.0;
		 if(ticks>52*7*24*3600/GodGenerator.gameClockFactor) {
			 ticks = 52*7*24*3600/GodGenerator.gameClockFactor;
		 }
		 return ((int) Math.round(ticks)+1);*/
	}
	
	
	public static int getUnitTicks(int hAUpop, int totalEngineers, Town t) {
		int theMany=1; 
		switch(hAUpop) {
		 case 1:
			 //t = 345600*(number*expMod)*Exp(1-townEngineers/capForLevelAtCSL)/(CSL^2)
			 theMany = AttackUnit.soldierExpMod;

			 break;
		 case 2:
			 //566,3400 is the old one
			 theMany=AttackUnit.tankExpMod;
		//	 days+=1;
			 	 break;
		 case 3:
			 //1066 6400
			// days+=2;
			 theMany=AttackUnit.juggerExpMod;

		 case 4:
		//	 days+=3;
			 theMany=AttackUnit.bomberExpMod;

		 }
		return getUnitTicks(theMany,t);
	/*	double addon;
		Player p = t.getPlayer();
		double engEffect = p.God.Maelstrom.getEngineerEffect(t.getX(),t.getY());
		int engTech = p.getEngTech();
		
		totalEngineers=(int) Math.round(((double) totalEngineers) *(1+engEffect+.05*(engTech-1))+1);
		ArrayList<Town> towns = p.towns();
		double avgLevel=0;
		 int k = 0;
		 int highLvl=0;

			avgLevel+=(int) Math.round(((double) p.God.getAverageLevel(t) ));
			int x = 0;
			while(x<t.bldg().size()) {
				if(t.bldg().get(x).getLvl()>highLvl) highLvl=t.bldg().get(x).getLvl();
				x++;
			}
		double percdifflvl = ((double) (highLvl-avgLevel))/100;
		
		double engAvgLevel = (int) Math.round(((double) (1.0-percdifflvl)*((double) avgLevel) + percdifflvl*((double) highLvl)));
		 k = 0;
		AttackUnit a; int popped = 0;
		ArrayList<AttackUnit> au = p.getAu();
		while(k<au.size()) {
			a = au.get(k);
			if(!a.getName().equals("locked")&&!a.getName().equals("empty"))popped++;
			k++;
		}
		k=0;
		int pop = p.God.getCombatPopWithExpMod(p);
		double cap = Building.getCap((int) engAvgLevel,false);
		int i = 0;
		double CSL = p.getPs().b.getCSLAtLevel(avgLevel,popped,p.towns().size());
		double ticks =2*3600*24*Math.exp(1-((double) totalEngineers)/cap)/(Math.exp(1)*Math.pow(CSL,2));
	//	System.out.println("ticks before: " + ticks); The two is there because of math badness.
		double chgdays=(int) Math.round(((double) QueueItem.days)*((double) engAvgLevel-2)/(((double) Town.maxBldgLvl)/6.0));
		if(chgdays>QueueItem.days) chgdays =QueueItem.days;	
		if(chgdays<=0) chgdays=(int) Math.round(((double) QueueItem.days)*((double) 1)/(((double) Town.maxBldgLvl)/6.0));


		double newTicks=0;
		 int theMany=1;
		 switch(hAUpop) {
		 case 1:
			 //t = 345600*(number*expMod)*Exp(1-townEngineers/capForLevelAtCSL)/(CSL^2)
			 theMany = AttackUnit.soldierExpMod;

			 break;
		 case 5:
			 //566,3400 is the old one
			 theMany=AttackUnit.tankExpMod;
		//	 days+=1;
			 	 break;
		 case 10:
			 //1066 6400
			// days+=2;
			 theMany=AttackUnit.juggerExpMod;

		 case 20:
		//	 days+=3;
			 theMany=AttackUnit.bomberExpMod;

		 }
		 i=0;
		 while(i<theMany) {
			 if(pop+i>CSL) 
				 newTicks+=ticks*Math.pow((pop+i),1+.5*((double) (pop+i)-CSL)/((double) CSL));
			 else
				 newTicks+=ticks*(pop+i);
			 i++;
		 }
		 
		 ticks=newTicks;
	//	 System.out.println("new ticks after: "+ ticks);
		 ticks*=chgdays/GodGenerator.gameClockFactor;
		 if(p.getUbTimer()>0) ticks/=2.0;
		 if(ticks>52*7*24*3600/GodGenerator.gameClockFactor) {
			 ticks = 52*7*24*3600/GodGenerator.gameClockFactor;
		 }
		 int toRet =  ((int) Math.round(ticks))+1;
	
		 

		 return toRet;*/
	}
	
	public void modifyUnitTicksForItem(int hAUpop, Town t) {
		// because these ticks represent not the attack unit order but their slot order.
	/*	double addon;
		int totalEngineers = t.getTotalEngineers();Player p = t.getPlayer();
		double engEffect = p.God.Maelstrom.getEngineerEffect(t.getX(),t.getY());
		int engTech = p.getEngTech();
		totalEngineers=(int) Math.round(((double) totalEngineers) *(1+engEffect+.05*(engTech-1))+1);

		
		ArrayList<Town> towns = p.towns();
		double avgLevel=0;
		 int k = 0;
		 int highLvl=0;
	
			avgLevel+=(int) Math.round(((double) p.God.getAverageLevel(t) ));
			int x = 0;
			while(x<t.bldg().size()) {
				if(t.bldg().get(x).getLvl()>highLvl) highLvl=t.bldg().get(x).getLvl();
				x++;
			}
		double percdifflvl = ((double) (highLvl-avgLevel))/100;
		
		double engAvgLevel = (int) Math.round(((double) (1.0-percdifflvl)*((double) avgLevel) + percdifflvl*((double) highLvl)));
		 k = 0;
		AttackUnit a; int popped = 0;
		ArrayList<AttackUnit> au = p.getAu();
		while(k<au.size()) {
			a = au.get(k);
			if(!a.getName().equals("locked")&&!a.getName().equals("empty"))popped++;
			k++;
		}
		k=0;
		int pop = p.God.getCombatPopWithExpMod(p);
		double cap = Building.getCap((int) engAvgLevel,false);
		
	int	i = 0;
	double CSL = p.getPs().b.getCSLAtLevel(avgLevel,popped,p.towns().size());
	//	while(i<hAU.getExpmod()) {
		double ticks =2*3600*24*Math.exp(1-((double) totalEngineers)/cap)/(Math.exp(1)*Math.pow(CSL,2));
		//}
	//	System.out.println("the exponential component is " + Math.exp(1-((double) totalEngineers)/cap) + " and the CSL divider is " + (Math.exp(1)*Math.pow(p.getPs().b.getCSLAtLevel(avgLevel,popped,p.towns().size()),2)) + " because the popped number is " + popped 
		//		+ " and avgLevel is " + avgLevel + " and ticks is before " + ticks + " and building cap is " + cap + " and total engineers is " + totalEngineers + " and pop is " + pop + " and CSL is " + p.getPs().b.getCSLAtLevel(avgLevel,popped,p.towns().size()));
		
		double days=(int) Math.round(((double) QueueItem.days)*((double) engAvgLevel-2)/(((double) Town.maxBldgLvl)/6.0));
		if(days>QueueItem.days) days =QueueItem.days;	
		if(days<=0) days=(int) Math.round(((double) QueueItem.days)*((double) 1)/(((double) Town.maxBldgLvl)/6.0));
		 double newTicks=0;

		 int theMany=1;
		 switch(hAUpop) {
		 case 1:
			 //t = 345600*(number*expMod)*Exp(1-townEngineers/capForLevelAtCSL)/(CSL^2)
			 theMany = AttackUnit.soldierExpMod;
			 break;
		 case 5:
			 //566,3400 is the old one
			 theMany=AttackUnit.tankExpMod;
		//	 days+=1;
			 	 break;
		 case 10:
			 //1066 6400
			// days+=2;
			 theMany=AttackUnit.juggerExpMod;

		 case 20:
		//	 days+=3;
			 theMany=AttackUnit.bomberExpMod;

		 }
		 i=0;
		 while(i<theMany) {
			 if(pop+i>CSL) 
				 newTicks+=ticks*Math.pow((pop+i),1+.5*((double) (pop+i)-CSL)/((double) CSL));
			 else
				 newTicks+=ticks*(pop+i);
			 i++;
		 }
		 ticks=newTicks;
		 
	//	 System.out.println("After multiplying by pop, we get " + ticks);
		 ticks*=days/GodGenerator.gameClockFactor;
	//	 System.out.println("After dividing by days and GCF, we get " + ticks);

		 if(p.getUbTimer()>0) ticks/=2.0;
		 ticks+=1;
		 if(ticks>52*7*24*3600/GodGenerator.gameClockFactor) {
			 ticks = 52*7*24*3600/GodGenerator.gameClockFactor;
		 }*/
		 int theMany=1;
		 switch(hAUpop) {
		 case 1:
			 //t = 345600*(number*expMod)*Exp(1-townEngineers/capForLevelAtCSL)/(CSL^2)
			 theMany = AttackUnit.soldierExpMod;
			 break;
		 case 2:
			 //566,3400 is the old one
			 theMany=AttackUnit.tankExpMod;
		//	 days+=1;
			 	 break;
		 case 3:
			 //1066 6400
			// days+=2;
			 theMany=AttackUnit.juggerExpMod;

		 case 4:
		//	 days+=3;
			 theMany=AttackUnit.bomberExpMod;

		 }
		 setTicksPerUnit(getUnitTicks(theMany,t));
	}

/*
	public void setMemAUtoBuild(int AUtoBuild) {
		setInt("AUtoBuild",AUtoBuild);
	}
	public int getMemAUtoBuild() {
		return getInt("AUtoBuild");
	}


	public void setMemCost(long cost[]) {
		setLong("m",cost[0]);
		setLong("t",cost[1]);
		setLong("mm",cost[2]);
		setLong("f",cost[3]);

	}





	public void setMemCurrTicks(int currTicks) {
		setInt("currTicks",currTicks);
	}


	public int getMemCurrTicks() {
		return getInt("currTicks");
	}
*/
	public long[] getCost() {
		if(cost==null) {
			try {
				long[] cost = new long[5];
				UberPreparedStatement stmt = con.createStatement("select m,t,mm,f from queue where qid = ?;");
				stmt.setInt(1,qid);
				ResultSet rs = stmt.executeQuery();
				rs.next();
				cost[0]=rs.getLong(1);
				cost[1]=rs.getLong(2);
				cost[2]=rs.getLong(3);
				cost[3]=rs.getLong(4);
			//	System.out.println("cost is " + cost[3]);
				this.cost= cost;
				rs.close();
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }
		}
		return cost;
	}
	public void setTicksPerUnit(int ticksPerUnit) {
		this.ticksPerUnit = ticksPerUnit;
	}

/*
	public void setMemTownsAtTime(int townsAtTime) {
		setInt("townsAtTime",townsAtTime);
	}


	public int getMemTownsAtTime() {
		return getInt("townsAtTime");
	}


	public void setMemOriginalAUAmt(int originalAUAmt) {
		setInt("originalAUAmt",originalAUAmt);
	}


	public int getMemOriginalAUAmt() {
		return getInt("originalAUAmt");
	}


	public void setMemTotalNumber(int totalNumber) {
		setInt("totalNumber",totalNumber);
	}


	public int getMemTotalNumber() {
		return getInt("totalNumber");
	}
*/

	public void deleteMe() {
		try {
			UberPreparedStatement stmt = con.createStatement("delete from queue where qid = ?;");
			stmt.setInt(1,qid);
			stmt.executeUpdate();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
	
		b.Queue().remove(this);
	}

	synchronized public void save() {
		try {
			String update = "update queue set AUNumber = " + getAUNumber() + ", currTicks = " + getCurrTicks() + 
			  " where qid = " + qid;
			UberPreparedStatement stmt = con.createStatement("update queue set AUNumber = ?, currTicks = ? where qid = ?;");
			stmt.setInt(1,getAUNumber());
			stmt.setInt(2,getCurrTicks());
			stmt.setInt(3,qid);
			stmt.execute();
			stmt.close();
			
		}	catch(SQLException exc) { exc.printStackTrace(); }
		
	}

	/*

	public void setMemAUNumber(int AUNumber) {
		setInt("AUNumber",AUNumber);
	}


	public int getMemAUNumber() {
		return getInt("AUNumber");
	}
	public void setInt(String fieldName, int toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update queue set " + fieldName + " = " + toSet + " where qid = " + qid);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setDouble(String fieldName, double toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update queue set " + fieldName + " = " + toSet + " where qid = " + qid);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setLong(String fieldName, long toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update queue set " + fieldName + " = " + toSet + " where qid = " + qid);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setBoolean(String fieldName, boolean toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update queue set " + fieldName + " = " + toSet + " where qid = " + qid);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setString(String fieldName, String toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update queue set " + fieldName + " = \"" + toSet + "\" where qid = " + qid);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public int getInt(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from queue where qid = " + qid);
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
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from queue where qid = " + qid);
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
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from queue where qid = " + qid);
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
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from queue where qid = " + qid);
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
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from queue where qid = " + qid);
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
