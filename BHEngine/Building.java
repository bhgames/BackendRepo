package BHEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;

public class Building {
	// Just a note up here that we generally use totalEngineers+1 instead of totalEngineers
	// because taking the log of zero is not fun for anybody.
	
	public int bid;
	private int ticksToFinishTotal=0;
	private ArrayList<QueueItem> Queue;
	private long cap;
	private boolean mineBldg;
	private int refuelTicks;
	private boolean nukeMode;
	private int fortArray[];
	private String type;
	private int lvl;
	private boolean deconstruct;
	private int ticksLeft,ticksToFinish,numLeftToBuild,bunkerMode,lvlUps;
	private int peopleInside,lotNum;
private GodGenerator God; private UberConnection con;
//public	int ticksPerUnit[] = new int[6]; // for combat units ONLY.
//public	int ticksLeftPerUnit[] = new int[6]; // for combat units ONLY.

private int ticksPerPerson=0;
public static int resourceAmt = 600; // increase this to get an increase in
// warehouse size per level, currently the amt of the building
// but this roughly equals six hours of production at the mine level = warehouse lvl.
// then level ups cannot happen anymore.
public static int baseResourceAmt = 2000;
// This is the amount of resources that you can hold without any warehouses at all,
// it's a systemwide attribute!
	
	
	public boolean resetBunkerMode(int newMode) {
		if(getType().equals("Bunker")&&newMode<3&&newMode>=0) setBunkerMode(newMode);
		else return false;
		return true;
	}
	

	
	
	public Building returnCopy() {
		return new Building(bid,God);
	}
	
	public void setBuildingValues(String type, int lotNum, int bldglvl, int ticksToFinish, int people, int pplbldging, int ticksLeft, int lvlUps, boolean deconstruct, int bid, int refuelTicks,boolean nukeMode,int bunkerMode, String fortArrayStr) {
		setLvl(bldglvl); this.ticksToFinish=ticksToFinish; peopleInside = people; numLeftToBuild = pplbldging;
		this.nukeMode=nukeMode;
		this.bid=bid;
		this.ticksLeft = ticksLeft;
		this.lvlUps=lvlUps;this.deconstruct=deconstruct;
		this.lotNum = lotNum;
		this.refuelTicks=refuelTicks;
		this.bunkerMode=bunkerMode;
		this.type=type;
		setFortArray(PlayerScript.decodeStringIntoIntArray(fortArrayStr));
	}
	public Building(int bid,GodGenerator God) {
		
		this.bid=bid;
	
		this.God=God; this.con = God.con;
		
			try {
				UberStatement bus = con.createStatement();

				ResultSet brs = bus.executeQuery("select * from bldg where bid = " + bid); // Let's kill your mom, ho.
				
			
				while(brs.next()) {
		
					String fortArrayStr = brs.getString(16);
					
					  setBuildingValues(brs.getString(1), brs.getInt(2), brs.getInt(3), brs.getInt(4), brs.getInt(5), 
							brs.getInt(6),brs.getInt(7),brs.getInt(9),brs.getBoolean(10),bid,brs.getInt(14),brs.getBoolean(15),brs.getInt(12),fortArrayStr);
					
					 
					 
				}
			} catch(SQLException exc) { exc.printStackTrace(); }
			
			
			
/*
 
		ticksPerPerson = (int) Math.round(300+1500*Math.exp(-totalEngineers/1800.0));
		double nextLevelBase = (int) Math.round(300*Math.exp(getLvl()));
		double base = (int) Math.round(nextLevelBase/6.0);
		double expFactor = base*5;
		
		ticksToFinishTotal  = (int) Math.round(base+expFactor*Math.exp(-totalEngineers/nextLevelBase));*/
		// there are HI MED MEDLO LO, 4x3x2x1 = 22 possible combos.
		
		
		// ALL THIS SHIT LINKED WITH USERBUILDING.
		

		 // this at the end
		//because we don't know if a mine bldg unless goes through above if stmts
		
	}

	/*public Building(String type, int lotNum, int totalEngineers, double cloudFactor, int engTech, GodGenerator God) {
		this.God=God; this.con = God.con;

		
		this.lotNum = lotNum; 
		
		
		 * Build times and engineers: The idea being that engineers should decrease the build time in exponentially smaller increments. How to do this?
		 * Well if you subtract an exponential(engineernumber) it could eventually get larger than 300.
		 * But if it were exponential(engineernumber/ticksNormal) then the thing would be ridiculously small.
		 * 
		 * How about 300exp(-engineerNumber/300)? Then it'd take roughly 300 engineers to do 1/2.7 something...but it'd never completely approach zero.
		 * The way you're doing it, 300 ticks being the first level, originally they will have maybe 20 engineers, by the time they get to 3000 ticks,
		 * engineers will be around 300...yeah. Works.
		
		cap = 0; // assume a new building. Then it should be 0.
		lvlUps=0; deconstruct = false;
		ticksToFinishTotal = getTicksForLeveling(totalEngineers,cloudFactor,engTech); // So the max you can take off is 5/6ths of ticker time.
		setLvl(0);		ticksPerPerson = getTicksPerPerson(totalEngineers,cloudFactor,engTech);

		if(type.equals("empty") || type.equals("locked")) {
			this.type=type;
			cost[0] = 0;
			cost[1] = 0;
			cost[2] = 0;
			cost[3] = 0;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			ticksLeft = 0;
			
		}
		
		if(type.equals("Headquarters")) { 
			this.type=type;
			cost[0] = 70;
			cost[1] = 40;
			cost[2] = 150;
			cost[3] = 140;
			cost[4] = 0;
		}
		if(type.equals("Arms Factory")) {//100 100 100 100
			this.type=type;
			cost[0] = 150;
			cost[1] = 70;
			cost[2] = 140;
			cost[3] = 40;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			ticksLeft = 0;
			
			
		}
		if(type.equals("Command Center")) {
			this.type=type;
			cost[0] = 70;
			cost[1] = 150;
			cost[2] = 140;
			cost[3] = 40;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
		//	peopleInside=0;
			
			
			
		}
		if(type.equals("Institute")) {
			this.type=type;
			cost[0] = 40;
			cost[1] = 140;
			cost[2] = 70;
			cost[3] = 150;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			
			

			
		}
		
		if(type.equals("Communications Center")) {
			this.type=type;
			cost[0] = 140;
			cost[1] = 40;
			cost[2] = 150;
			cost[3] = 70;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;

			
			
		}
		if(type.equals("Trade Center")) {
			this.type=type;
			cost[0] = 100;
			cost[1] = 100;
			cost[2] = 100;
			cost[3] = 100;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			

			
		}
		if(type.equals("Bunker")) {
			this.type=type;
			cost[0] = 140;
			cost[1] = 150;
			cost[2] = 40;
			cost[3] = 70;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
			resourceCap = 100;
			
			
			
		}
		
		if(type.equals("Metal Warehouse")) {
			mineBldg=true;
			this.type=type;
			cost[0] = 40;
			cost[1] = 150;
			cost[2] = 140;
			cost[3] = 70;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			resourceCap=100;
			
			
			
		}
		if(type.equals("Lumber Yard")) {
			mineBldg=true;

			this.type=type;
			cost[0] = 70;
			cost[1] = 40;
			cost[2] = 150;
			cost[3] = 140;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			resourceCap=100;
			
			
			
		}
		if(type.equals("Crystal Repository")) {
			mineBldg=true;

			this.type=type;
			cost[0] = 140;
			cost[1] = 70;
			cost[2] = 40;
			cost[3] = 150;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			resourceCap=100;
			
			
			
		}
		
		if(type.equals("Granary")) {
			mineBldg=true;

			this.type=type;
			cost[0] = 150;
			cost[1] = 140;
			cost[2] = 70;
			cost[3] = 40;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			resourceCap=100;
			
			
			
		}
		
		if(type.equals("Metal Mine")) {
			this.type=type;
			cost[0] = 100;
			cost[1] = 130;
			cost[2] = 60;
			cost[3] = 110;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			resourceCap=100;
			
			
			
		}
		if(type.equals("Timber Field")) {
			this.type=type;
			cost[0] = 95;
			cost[1] = 95;
			cost[2] = 60;
			cost[3] = 150;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			resourceCap=100;
			
			
			
		}
		if(type.equals("Manufactured Materials Plant")) {
			this.type=type;
			cost[0] = 100;
			cost[1] = 90;
			cost[2] = 90;
			cost[3] = 120;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			resourceCap=100;
			
			
			
		}
		if(type.equals("Food Farm")) {
			this.type=type;
			cost[0] = 95;
			cost[1] = 95;
			cost[2] = 100;
			cost[3] = 110;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			resourceCap=100;
			
			
			
		}
		

		if(mineBldg) cap=(int) Math.ceil(resourceAmt*Math.exp(lvl+1));
		else cap = (int) Math.ceil(Math.sqrt(2*Math.exp(lvl)));
	}*/
	
	public void addUnit(int number) {
		/*
		 * Right so you have numLeftToBuild, this is how many left to build,
		 * it will be decreased by building server as ticksLeft decreases to 0.
		 */
		setNumLeftToBuild(getNumLeftToBuild() + number);

	} 
	
	public void modifyTicksLevel(int totalEngineers,double cloudFactor,int engTech) {
		/*
		 * Right so if ticksToFinish was previously at 50/60, I now want to adjust the totalTicks amt...
		 * totalTicks = let's say 30 + 40Exp(-prevTotalEng/70), and prevTotal was such that totalTicks = 60, now I want to adjust
		 * the total ticks such that totalTicks is now 58 with the new total. Well, clearly this total is 2 seconds less - so two seconds should be
		 * taken off the ticksToFinish.
		 * 
		 * then ticksToFinish = ticksToFinish - changeInTotal, changeInTotal = 40Exp(-prevTotalEng/70) - 40Exp(-newTotalEng/70); you see-izzle?
		 * 
		 * but how do we know what the ticks previous total was? I mean - there is an exponential factor there. I guess the difference would cancel out...
		 * guess I need to be able to level up, then I'll have the formula for it...
		 */
	
		 setTicksToFinishTotal(getTicksForLevelingAtLevel(totalEngineers,getLvl()+1,cloudFactor,engTech,getType()));
		 // we use at the lvl+1 we're at because that's the thing we're currently watching,
		 // not +lvlUps or anything else - we're at one level down from the level we're going to,
		 // and we want the change for that level.

	
	}
	public void modifyPeopleTicks(int totalEngineers, double cloudFactor, int engTech) {
		//CONNECTED TO GET TICKS PER PERSON
		// I honestly don't care about the current person left. ticksPerPerson can just be modified and then the next guy up will work do that shit.
		int ppl=0,lvl=0;
/*
		try {
			UberStatement stmt = God.con.createStatement();
			ResultSet rs = stmt.executeQuery("select ppl,lvl from bldg where bid = " + bid);
			if(rs.next()) {
				ppl=rs.getInt(1);
				lvl=rs.getInt(2);
				
			}
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }*/
		setTicksPerPerson(getTicksPerPerson(totalEngineers,cloudFactor,engTech,peopleInside,getLvl(),getType()));
	}
	public static int getTicksPerPerson(int totalEngineers, double cloudFactor, int engTech, int peopleInside, int blvl, String type) {
		//CONNECTED TO MODIFY PEOPLE TICKS
		// totalEngineers+1 because log(0) is bad for your health.
		// So we want engineers to slowly become easier to build over time.
		// 
	//	t = 345600*(number)*Exp(1-townEngineers/capForLevelAtBuildingLevel)/(BuildingCap^2)
		if(type.equals("Airstrip"))  return getAirshipTicks(totalEngineers,cloudFactor,engTech,blvl);
		totalEngineers=(int) Math.round(((double) totalEngineers) *(1+cloudFactor+.05*(engTech))+1);
		double totalTicks = getTicksForLevelingAtLevel(totalEngineers,blvl,cloudFactor,engTech,type); // Already is in GameClockFactors.
		
		double chgdays=(int) Math.round(((double) QueueItem.days)*((double) blvl-2)/(((double) Town.maxBldgLvl)/6.0));
		if(chgdays>QueueItem.days) chgdays =QueueItem.days;	
		if(chgdays<=0) chgdays=(int) Math.round(((double) QueueItem.days)*((double) 1)/(((double) Town.maxBldgLvl)/6.0));

		return (int)(1+ Math.round(/*chgdays*2.0*3600.0*24.0*/((double ) totalTicks)*((double) peopleInside)*Math.exp(1-totalEngineers/getCap(blvl,false))/(Math.exp(1)*Math.pow(getCap(blvl,false),2))));
		//return (int) Math.round((300+1500*Math.exp(-Math.log(totalEngineers*(1+cloudFactor+.05*(engTech-1))+1)/10))/GodGenerator.gameClockFactor);
	//	return totalEngineers;
	}
	

	/*public void modifyUnitTicksForQueue(ArrayList<AttackUnit> AU, int totalEngineers) {

		// Modifies the ticks requirement for the current queue.
		
		QueueItem q;
		int i = 0;
		while(i<Queue().size()) {
			 q = Queue().get(i);
			
			 // so if two soldiers take 10s, then we want 1 tank = 2 soldiers to take 9 s,
			 // and a juggernaught which equals two tanks to take 16 sec.
			 // in this case the base is 1800. 
			 // so soldiers take 1800 per.
			 // two soldiers take 3600 so tanks take 3400 per.
			 // two tanks take 6800 so juggernaughts take 6400 per.
			 // two juggernaughts equals a bomber so 12800 becomes 12000 per.
			 // base time can be 1/6th the original time.
			q.modifyUnitTicksForItem(AU.get(q.returnAUtoBuild()).getPop(),totalEngineers);
			 
			 
			i++;
		}
	}*/
	
	public void levelUp(int totalEngineers, double cloudFactor, int engTech) {
		// The script server has to worry about decreasing resources, this guy just has to set up timers.
		
		/*
		 * So we want the cost to rise exponentially, and we also want the ticks required to rise exponentially. 
		 * ticksToFinishTotal = (int) Math.round(50+250*Math.exp(-numEngineers/300));  is the original level 0 exposure.
		 * 
		 * Now the question is, how do we go about doing it for higher levels? We clearly need to set up a base higher level.
		 * 
		 * Once we have that we can use the five sixth's rule to separate out the two pieces, then we can add them up as required.
		 * 
		 * So uh...this shit is incorrect. And here's why - we need this thing to scale like engineers in the building do.
		 * 
		 * SO having a nextLevelbase of 300*sqrt(2*exp(lvl)) would be nice
		 * except then the engineers would do next to nothing to the ticks - given that this
		 * gets divided over, there'd always be nearly a 1/300 correspondence. Not good enough.
		 * So we've got to put in a factor of say...300 on the top, to allow for ticks to start at 5 minutes. THen if they
		 * filled out the Command Center with engineers to it's capacity, then the next level should only be
		 * 1/5th of it's previous one. Similarly, if this is another building, it's decreased a shitload also.
		 * But every time you level it up it goes away!
		 */
		//sqrt(2*exp(lvl))
	
		setTicksToFinish(0);
		setTicksToFinishTotal(getTicksForLeveling(totalEngineers,cloudFactor,engTech));

		// need to set up new costs for the next level...
		// cost needs to rise like...exp(lvl)...
		

		// Very good.
	}
	
	public int getTicksForLeveling(int totalEngineers,double cloudFactor, int engTech) {
		// why plus 1? This is the ticks you're gonna put in to move to the next level - 
		// and it's not on your lvlUps because you haven't added it yet, so we save
		// you the trouble and add one.
		if(type.equals("Missile Silo")&&(getLvl()==0)) return (int) (7*24*3600/GodGenerator.gameClockFactor); // it takes a week if if level is 0.

		int lvl = getLvl(); int lvlUp = getLvlUps();
		double nextLevelBase = (int) Math.round((50*(lvl+lvlUp+1)*(lvl+lvlUp+1)*Math.sqrt((lvl+lvlUp+1)))/GodGenerator.gameClockFactor);
		double base = (int) Math.round(nextLevelBase/6.0);
		double expFactor = base*5;
	//	double eng = Math.pow((totalEngineers*(1+cloudFactor+.05*(engTech-1))+1),2);
	//	eng = Math.log(eng);
	//	eng = Math.pow(eng,2.5);
		totalEngineers=(int) Math.round(((double) totalEngineers) *(1+cloudFactor+.05*(engTech))+1);
		double totalEngEffect = 1-(((double) totalEngineers))*.01125;
		if(totalEngEffect<.1) totalEngEffect=.1;
		
		if(lvl+lvlUp+1==1) return 1;
		else
		return (int) (1+Math.round((base+expFactor)*totalEngEffect/*Math.exp(1-eng/getCap(lvl,false))/Math.exp(1))*/));
		
	
	}
	
	public static int getTicksForLevelingAtLevel(int totalEngineers,int lvlYouWant, double cloudFactor, int engTech, String type) {
		//Lvl you want, so no +1. You do that for yourself, basically.
		if(type.equals("Missile Silo")&&lvlYouWant==1) return (int) (7*24*3600/GodGenerator.gameClockFactor);

		double nextLevelBase = (int) Math.round((50*(lvlYouWant*lvlYouWant*Math.sqrt(lvlYouWant)))/GodGenerator.gameClockFactor);
		double base = (int) Math.round(nextLevelBase/6.0);
		double expFactor = base*5;
	//	double eng = Math.pow((totalEngineers*(1+cloudFactor+.05*(engTech-1))+1),2);
		totalEngineers=(int) Math.round(((double) totalEngineers) *(1+cloudFactor+.05*(engTech))+1);
		double totalEngEffect = 1-(((double) totalEngineers))*.01125;
		if(totalEngEffect<.1) totalEngEffect=.1;
		//eng = Math.log(eng);
		//eng = Math.pow(eng,2.5);
		
		if(lvlYouWant==1) return 1;
		else
		return (int)(1+ Math.round((base+expFactor)*totalEngEffect)/*Math.exp(1-eng/getCap(lvlYouWant,false))/Math.exp(1))*/);
	}
	
	
	public void addCombatUnit(int index, int number, Town t, long cost[]) {
		
		
		try {
			int townsAtTime = t.getPlayer().towns().size();
			   int i = 0;AttackUnit a=null;
			   ArrayList<AttackUnit> au = t.getPlayer().getAu();
			      while(i<au.size()) {
			    	  a = au.get(i);
			    	  if(a.getSlot()==index) break;
			    	  i++;
			      }
			int originalAUAmt = t.getPlayer().God.getTotalSize(a,t.getPlayer());
			int totalNumber=number;
	
		     UberStatement stmt = t.getPlayer().God.con.createStatement();
		     UberStatement stmt2 = t.getPlayer().God.con.createStatement();

		      // First things first. We update the player table.
		      boolean transacted=false;
		      ResultSet rs;
		      while(!transacted) {
		    	  
		      try {
			       rs = stmt2.executeQuery("select slot from bldg where bid = " + bid);
			      if(rs.next()) {
			      
		      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
		      
		      // let's add this raid and therefore get the rid out of it.
		      stmt.executeUpdate("insert into queue (bid,AUtoBuild,AUNumber,m,t,mm,f,townsAtTime,originalAUAmt,totalNumber) values (" +bid + "," + index + 
		    		  "," + number + "," + cost[0] + "," + cost[1] + "," + cost[2] + "," + cost[3] + "," + townsAtTime + "," + originalAUAmt + "," + totalNumber + ");");
		      
		      ResultSet qrs = stmt.executeQuery("select qid from queue where bid = " + bid + " and AUtoBuild = " + index + 
		    		  " and AUNumber = " + number + " order by qid desc");
		      qrs.next();
	
		      QueueItem q = new QueueItem(qrs.getInt(1),this,God);
				
		      q.modifyUnitTicksForItem(a.getType(),t); 
			
				
		      Queue().add(q);
		      
		      stmt.execute("commit;"); qrs.close(); stmt.close(); } rs.close(); stmt2.close(); transacted=true;
		      } catch(MySQLTransactionRollbackException exc) { 		   } 
		      }
			 } catch(SQLException exc) { exc.printStackTrace(); }

	}
	public QueueItem findQueueItem(int qid) {
		int i= 0;
		ArrayList<QueueItem> Queue = Queue();
		while(i<Queue.size()) {
			if(Queue.get(i).qid==qid) return Queue.get(i);
			i++;
		}
		return null;
	}
	public ArrayList<QueueItem> Queue() {
		if(Queue==null) {
		ArrayList<QueueItem> queue= new ArrayList<QueueItem>();
		long cost[];
		try {
		UberStatement qus = con.createStatement();
		 ResultSet qrs = qus.executeQuery("select * from queue where bid = " + bid + " order by qid asc");
		 while(qrs.next()) {
			 //	public QueueItem(int qid, int bid, int AUtoBuild, int AUNumber, int currTicks,Town t) {
			 int k = 0;
			 cost = new long[4];
			 while(k<4) {
				 cost[k]=qrs.getLong(6+k);
				 k++;
			 }
			 
			 queue.add(new QueueItem(qrs.getInt(1),this,God));
			 
		 }
		 qrs.close(); qus.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		
		Queue= queue;
		}
		return Queue;
	}
	
	synchronized public void save() {
		try {
		 String  update ="";
		 UberStatement stmt = con.createStatement();
 			   update = "update bldg set name = '" + type + "', lvl = " + getLvl() + ", lvling = " +
 		  ticksToFinish + ", ppl = " + peopleInside + ", pplbuild = " + numLeftToBuild + ", pplticks = " +
 		  ticksLeft + ", fortArray = '"+PlayerScript.toJSONString(fortArray) + "', lvlUp = " + lvlUps + ", deconstruct = " + deconstruct + ", bunkerMode = " + bunkerMode +", refuelTicks = " + refuelTicks+", nukeMode = " + nukeMode 
 		  + " where bid = " + bid + ";";
 	
 			  
 		  
		
 		  // crap, this is ugly.
 		  
 		  stmt.executeUpdate(update);
 		  stmt.close();
 		  int i = 0;
 		  ArrayList<QueueItem> Queue = Queue();
 		  while(i<Queue.size()) {
 			  Queue.get(i).save();
 			  i++;
 		  }
		} catch(SQLException exc) { exc.printStackTrace(); }
	}
	public long getCap() {
		long cap=0;
		if(type.equals("Airstrip")) return getAirshipCap(getTicksPerPerson());
		if(isMineBldg()) cap=(long) Math.ceil(resourceAmt*Math.pow(getLvl()+2,2));
		else cap = (long) Math.ceil(Math.sqrt(6)*(getLvl()+1));
		return cap;
	}
	public long getQueueCap() {
		// only for military production facilities.
		int cap = getLvl()*2;
		if(cap>60) cap=60;
		return getLvl()*2;
		
	}
	public int getSlotCap() {
		// only for mil. prod facilities.
		return (int) Math.ceil(((double) getLvl())/3.0);
	}
	public long getAirshipCap(int realTicks) {
		long cap = (int) Math.round( ((double) Town.daysOfStoragePerAirshipPlatform*24.0*3600.0)/((double) realTicks));
		return cap;
	}
	public int getAirshipTicks(int totalEngineers, double cloudFactor, int engTech) {
		return getAirshipTicks(totalEngineers,cloudFactor,engTech,getLvl());
	}

	public static int getAirshipTicks(int totalEngineers, double cloudFactor, int engTech, int blvl) {
		double eng=(int) Math.round(((double) totalEngineers) *(1+cloudFactor+.05*(engTech))+1);
		double exp =Math.exp(1-eng/getCap(blvl,false))/Math.exp(1);
	//	System.out.println("eng is " + eng + " exp is " + exp + " total engineers is " +totalEngineers + " engTech is "+ engTech + " cloudFactor is " + cloudFactor);

		return  (int) Math.round(((double) Town.ticksPerFuelPointBase+blvl*2)*exp);
		
	}
	public static long getCap(int lvl, boolean mineBldg) {
		long cap=0;
		if(mineBldg) cap=(long) Math.ceil(resourceAmt*Math.pow(lvl+2,2));
		else cap = (long) Math.ceil(Math.sqrt(6)*(lvl+1));
		return cap;
	}
	
	public boolean isMineBldg() {
		String type = getType();
		if(type.equals("Metal Warehouse")||type.equals("Lumber Yard")
				||type.equals("Manufactured Materials Warehouse")||type.equals("Granary")) return true;
		else return false;
	}
	public static long[] getCost(String type) {
		
		long cost[] = new long[5];
		if(type.equals("empty") || type.equals("locked")) {
			cost[0] = 0;
			cost[1] = 0;
			cost[2] = 0;
			cost[3] = 0;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
		}
		if(type.equals("Airstrip")) { 
			cost[0] = 150;
			cost[1] = 70;
			cost[2] = 110;
			cost[3] = 70;
			cost[4] = 0;
		}
		if(type.equals("Missile Silo")) { 
			cost[0] = 1000;
			cost[1] = 1000;
			cost[2] = 1000;
			cost[3] = 1000;
			cost[4] = 0;
		}if(type.equals("Recycling Center")) { 
			cost[0] = 50;
			cost[1] = 130;
			cost[2] = 90;
			cost[3] = 130;
			cost[4] = 0;
		}
		if(type.equals("Foundry")) { 
			cost[0] = 40;
			cost[1] = 150;
			cost[2] = 140;
			cost[3] = 70;
			cost[4] = 0;
		}if(type.equals("Sawmill")) { 
			cost[0] = 70;
			cost[1] = 40;
			cost[2] = 150;
			cost[3] = 140;
			cost[4] = 0;
		}if(type.equals("Crystal Refinery")) { 
			cost[0] = 140;
			cost[1] = 70;
			cost[2] = 40;
			cost[3] = 150;
			cost[4] = 0;
		}if(type.equals("Hydroponics Bay")) { 
			cost[0] = 150;
			cost[1] = 140;
			cost[2] = 70;
			cost[3] = 40;
			cost[4] = 0;
		}
		if(type.equals("Command Center")) { 
			cost[0] = 70;
			cost[1] = 40;
			cost[2] = 150;
			cost[3] = 140;
			cost[4] = 0;
		}
		if(type.equals("Arms Factory")) {//100 100 100 100
			cost[0] = 150;
			cost[1] = 70;
			cost[2] = 140;
			cost[3] = 40;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
		}	
		if(type.equals("Manufacturing Plant")) {//100 100 100 100
			cost[0] = 150;
			cost[1] = 70;
			cost[2] = 140;
			cost[3] = 40;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
		}
		if(type.equals("Storage Yard")) {
			cost[0] = 70;
			cost[1] = 150;
			cost[2] = 140;
			cost[3] = 40;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
		//	peopleInside=0;
			
			
			
		}
		if(type.equals("Institute")) {
			cost[0] = 40;
			cost[1] = 140;
			cost[2] = 70;
			cost[3] = 150;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			
			

			
		}
		
		if(type.equals("Resource Cache")) {
			cost[0] = 140;
			cost[1] = 40;
			cost[2] = 150;
			cost[3] = 70;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;

			
			
		}
		if(type.equals("Trade Center")) {
			cost[0] = 100;
			cost[1] = 100;
			cost[2] = 100;
			cost[3] = 100;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			

			
		}
		if(type.equals("Fortification")) {
			cost[0] = 140;
			cost[1] = 150;
			cost[2] = 40;
			cost[3] = 70;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
			
		}
		
		if(type.equals("Metal Warehouse")) {

			cost[0] = 40;
			cost[1] = 150;
			cost[2] = 140;
			cost[3] = 70;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
		if(type.equals("Lumber Yard")) {
	
			cost[0] = 70;
			cost[1] = 40;
			cost[2] = 150;
			cost[3] = 140;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
		if(type.equals("Crystal Repository")) {

			cost[0] = 140;
			cost[1] = 70;
			cost[2] = 40;
			cost[3] = 150;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
		
		if(type.equals("Granary")) {
	
			cost[0] = 150;
			cost[1] = 140;
			cost[2] = 70;
			cost[3] = 40;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
		if(type.equals("Metal Mine")) {
			cost[0] = 100;
			cost[1] = 130;
			cost[2] = 60;
			cost[3] = 110;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
		
			
			
			
		}
		if(type.equals("Timber Field")) {
			cost[0] = 95;
			cost[1] = 95;
			cost[2] = 60;
			cost[3] = 150;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
		if(type.equals("Crystal Mine")) {
			cost[0] = 100;
			cost[1] = 90;
			cost[2] = 90;
			cost[3] = 120;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
		if(type.equals("Farm")) {
			cost[0] = 95;
			cost[1] = 95;
			cost[2] = 100;
			cost[3] = 110;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
		return cost;
	}

	public int getTicksToFinishTotal() {
		return	ticksToFinishTotal;

	}




	public void setTicksToFinishTotal(int ticksToFinishTotal) {
		this.ticksToFinishTotal = ticksToFinishTotal;
	}





	public String getType() {
		return type;
	}




	public void setType(String type) {
		this.type = type;
	}




	public int getLvl() {
		return lvl;
	}




	public void setLvl(int lvl) {
		this.lvl = lvl;
	}




	public boolean isDeconstruct() {
		return deconstruct;
	}




	public void setDeconstruct(boolean deconstruct) {
		this.deconstruct = deconstruct;
	}




	public int getTicksLeft() {
		return ticksLeft;
	}




	public void setTicksLeft(int ticksLeft) {
		this.ticksLeft = ticksLeft;
	}




	public int getTicksToFinish() {
		return ticksToFinish;
	}




	public void setTicksToFinish(int ticksToFinish) {
		this.ticksToFinish = ticksToFinish;
	}




	public int getNumLeftToBuild() {
		return numLeftToBuild;
	}




	public void setNumLeftToBuild(int numLeftToBuild) {
		this.numLeftToBuild = numLeftToBuild;
	}




	public int getBunkerMode() {
		return bunkerMode;
	}




	public void setBunkerMode(int bunkerMode) {
		this.bunkerMode = bunkerMode;
	}




	public int getLvlUps() {
		return lvlUps;
	}




	public void setLvlUps(int lvlUps) {
		this.lvlUps = lvlUps;
	}




	public int getPeopleInside() {
		return peopleInside;
	}




	public void setPeopleInside(int peopleInside) {
		this.peopleInside = peopleInside;
	}




	public int getLotNum() {
		return lotNum;
	}




	public void setLotNum(int lotNum) {
		this.lotNum = lotNum;
	}




	public GodGenerator getGod() {
		return God;
	}




	public void setGod(GodGenerator god) {
		God = god;
	}




	public UberConnection getCon() {
		return con;
	}




	public void setCon(UberConnection con) {
		this.con = con;
	}




	public int getTicksPerPerson() {
		return ticksPerPerson;
	}




	public void setTicksPerPerson(int ticksPerPerson) {
		this.ticksPerPerson = ticksPerPerson;
	}




	public void setCap(long cap) {
		this.cap = cap;
	}




	public void setMineBldg(boolean mineBldg) {
		this.mineBldg = mineBldg;
	}




	public void setMemType(String type) {
		setString("name",type);
	}

	public String getMemType() {
		return getString("name");
	}

	public void setMemPeopleInside(int peopleInside) {
		setInt("ppl",peopleInside);
	}

	public int getMemPeopleInside() {
		return getInt("ppl");
	}

	public void setMemLvl(int lvl) {
		setInt("lvl",lvl);
	}

	public int getMemLvl() {
		return getInt("lvl");
	}

	public void setMemLotNum(int lotNum) {
		setInt("slot",lotNum);
	}

	public int getMemLotNum() {
		return getInt("slot");
	}

	public void setMemTicksToFinishTotal(int ticksToFinishTotal) {
		this.ticksToFinishTotal = ticksToFinishTotal;
	}

	public int getMemTicksToFinishTotal() {
		return ticksToFinishTotal;
	}

	public void setMemTicksToFinish(int ticksToFinish) {
		setInt("lvling",ticksToFinish);
	}

	public int getMemTicksToFinish() {
		return getInt("lvling");
	}

	public void setMemNumLeftToBuild(int numLeftToBuild) {
		setInt("pplbuild",numLeftToBuild);
	}

	public int getMemNumLeftToBuild() {
		return getInt("pplbuild");
	}

	public void setMemBunkerMode(int bunkerMode) {
		setInt("bunkerMode",bunkerMode);
	}

	public int getMemBunkerMode() {
		return getInt("bunkerMode");
	}

	public void setMemLvlUps(int lvlUps) {
		setInt("lvlUp",lvlUps);
	}

	public int getMemLvlUps() {
		return getInt("lvlUp");
	}

	

	public void setMemDeconstruct(boolean deconstruct) {
		setBoolean("deconstruct",deconstruct);
	}

	public boolean isMemDeconstruct() {
		return getBoolean("deconstruct");
	}

	public void setMemTicksLeft(int ticksLeft) {
		setInt("pplticks",ticksLeft);
	}

	public int getMemTicksLeft() {
		return getInt("pplticks");
	}

	public void setMemTicksPerPerson(int ticksPerPerson) {
		this.ticksPerPerson = ticksPerPerson;
	}

	public int getMemTicksPerPerson() {
		return ticksPerPerson;
	}
	public void setInt(String fieldName, int toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update bldg set " + fieldName + " = " + toSet + " where bid = " + bid);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setDouble(String fieldName, double toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update bldg set " + fieldName + " = " + toSet + " where bid = " + bid);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setLong(String fieldName, long toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update bldg set " + fieldName + " = " + toSet + " where bid = " + bid);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setBoolean(String fieldName, boolean toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update bldg set " + fieldName + " = " + toSet + " where bid = " + bid);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setString(String fieldName, String toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update bldg set " + fieldName + " = \"" + toSet + "\" where bid = " + bid);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public int getInt(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from bldg where bid = " + bid);
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
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from bldg where bid = " + bid);
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
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from bldg where bid = " + bid);
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
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from bldg where bid = " + bid);
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
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from bldg where bid = " + bid);
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




	public void setRefuelTicks(int refuelTicks) {
		this.refuelTicks = refuelTicks;
	}




	public int getRefuelTicks() {
		return refuelTicks;
	}




	public void setNukeMode(boolean nukeMode) {
		this.nukeMode = nukeMode;
	}




	public boolean isNukeMode() {
		return nukeMode;
	}




	public void setFortArray(int fortArray[]) {
		this.fortArray = fortArray;
	}




	public int[] getFortArray() {
		return fortArray;
	}
}