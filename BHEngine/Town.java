package BHEngine;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import BattlehardFunctions.BattlehardFunctions;
import BattlehardFunctions.UserBuilding;

import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;


public class Town {
	
	 public int townID; 
	 private String holdingIteratorID = "-1";
	 private int internalClock=0;
	 private GodGenerator God; private UberConnection con;
	 private ArrayList<AttackUnit> au;
	 private ArrayList<Building> bldg;
	 private long res[], debris[];
	 private double resEffects[],resBuff[];
	 private Player p;
	 public int owedTicks;
	 private ArrayList<Trade> tradeServer;
	 private ArrayList<TradeSchedule> tradeSchedules;
	 private ArrayList<Raid> attackServer;
	 private String townName;
	 private int x,y;
	 private boolean zeppelin;
	 private int destX,destY;
	 private int fuelCells;
	 private int ticksTillMove;
	 public static int zeppelinTicksPerMove= (int) Math.round(((double) Math.sqrt(1)*10/(500*GodGenerator.speedadjust))/GodGenerator.gameClockFactor);
	 public static int ticksPerFuelPointBase =(int) Math.round((3600.0*24.0/((double) GodGenerator.gameClockFactor*10)));
	 public static int daysOfStoragePerAirshipPlatform = 4;
	 public static int maxFuelCells = 20; // the max fuel a zeppelin can hold.
	 public static int refillSpeed=4; // multiplier on ticksPerFuelPointBase to figure out how fast these plants refuel.
     // Given we want them to be able to grow ten fuel points every twenty four hours at level one.
	 //Then each additional level adds 2 fuel points. So they start at being able to move around a little bit. At level 5,
     // then can double their range.
	 public static int baseResourceGrowthRate=20; // the level 0 base resource rate for the game.
	 public static int maxBldgLvl = 30;

	/*
	 * 0 Metal
	 * 1 Timber
	 * 2 Manufactured Materials
	 * 3 Food
	 * 4 Population
	 * 
	 */
	public ArrayList<AttackUnit> getAu() {
			Player p = getPlayer();

		if(au==null) {
			//System.out.println(getTownName() + " is now fixing this.");
			ArrayList<AttackUnit> au = p.getAu();


		int i = 0;
		ArrayList<AttackUnit> aufortown = new ArrayList<AttackUnit>();
		 i = 0;
		AttackUnit ha;
		try {
		while(i<6) {
			 ha = au.get(i).returnCopy();
			ha.setSize(getMemInt("au" + (i+1)));
			
			aufortown.add(ha);
			i++;
		}
		} catch(Exception exc) {
			exc.printStackTrace(); System.out.println("Everything is fine. The au count though is " +  au.size() + " for " + p.ID);
		}
		try {
		UberStatement sau = con.createStatement();
			
		ResultSet saurs = sau.executeQuery("select * from supportAU where tid = " + townID + " order by slotnum asc");
	 UberStatement aus; ResultSet aurs;Player foreignP; String weapons;int weapc = 0; int weapforau[]; String holdPart; AttackUnit sAU;
	 aus = con.createStatement();

		while(saurs.next()) {
			int forTownSlot = saurs.getInt(3); // foreign town's slot.
			int thisTownSlot = saurs.getInt(4); // this town's slot.
			int originalTID = saurs.getInt(2); // the originating town ID of this supportAU.
			 aurs = aus.executeQuery("select pid from town where tid = " + saurs.getInt(2)); // should find six units.
			 // get foreign player's id.
			  aurs.next();
			  int fID = aurs.getInt(1);
			  
			   foreignP=God.getPlayer(fID);
			 aurs.close();
			 aurs = aus.executeQuery("select * from attackunit where pid = " + fID + " and slot = " + forTownSlot); // should find six units.
								
			
			aurs.next();
				int type = aurs.getInt(8);
				int popSize = 0;
				switch(type) {
				case 1: 
					popSize=1;
					break;
				case 2:
					popSize=5;
					break;
				case 3:
					popSize=10;
					break;
				case 4:
					popSize=20;
					break;
				}
				 weapons = aurs.getString(9); // weaps getting time...which weapons are equipped?

			        weapc = 0; 
			        holdPart = weapons;
			       while(!holdPart.equals("")) {
			    	   holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
			    	   weapc++;
			       }
			       
			        weapforau = new int[weapc]; 
			       
			       weapc=0;
					while(weapc<weapforau.length) {

						weapforau[weapc]=Integer.parseInt(weapons.substring(0,weapons.indexOf(",")));
						
						weapons = weapons.substring(weapons.indexOf(",")+1,weapons.length());

						weapc++;
					}
				
				 sAU = new AttackUnit(aurs.getString(1), aurs.getDouble(4), aurs.getDouble(5), 
						aurs.getDouble(6), aurs.getDouble(7), thisTownSlot, popSize, weapforau,aurs.getInt(10));
				sAU.setSize(saurs.getInt(5));
				if(saurs.getInt(6)==1) sAU.makeSupportUnit(forTownSlot,foreignP,originalTID);
				else if(saurs.getInt(6)==2) sAU.makeOffSupportUnit(forTownSlot,foreignP,originalTID);
			
				//System.out.println("I am loading " + sAU.name + " into "+ t.townName);
				aufortown.add(sAU);
				//System.out.println(t.townName + " has " + t.getAu().size() + " units.");
				
			aurs.close();
		
		}
		
		saurs.close();sau.close(); 	aus.close();

		} catch(SQLException exc) { exc.printStackTrace();}
		
		
		this.au= aufortown; 
		}
		
		
		return this.au;
	}
	
	public Town(int townID, GodGenerator God) {

		this.God=God; this.con=God.con;
		this.townID=townID;
		setInternalClock(God.gameClock);
		if(townID!=0){
		 getTownName();
		if(getPlayer()!=null) { // sometimes when we build towns for Quests, they don't have a player yet on the list of iteratorPlayers.
	
				getTradeServer(); getTradeSchedules();
				getRes(); getResBuff();  getResEffects();
				getAu();
				bldg();
		}

		zeppelin = getMemBoolean("zeppelin");
		debris = getMemDebris();
		owedTicks = getMemInt("owedTicks");

		if(isZeppelin()) {
		destX = getMemInt("destX");
		destY = getMemInt("destY");

		fuelCells = getMemInt("fuelcells");
		ticksTillMove = getMemInt("ticksTillMove");
		}
		x = getMemX();
		y = getMemY();}
	}
	synchronized public void synchronize() {
		// The only town related thing that is referenced by another thing is uh...
		// trade schedules. They may have a mate that references them.
		setInternalClock(God.gameClock);
		Player oldP = p;
		p=null; // set everything to null so it gets picked up again!
		getPlayer();
		if(oldP.ID!=p.ID) {
			p.towns().add(this);
			oldP.towns().remove(this);
		}
		res=null;resBuff=null;resEffects=null;
		bldg=null;au=null;
		townName=null;tradeServer=null;
		attackServer=null;
		attackServer();
		tradeServer(); getTownName();
		getRes(); getResBuff();  getResEffects();
		getAu();bldg();
		x = getMemX();
		y = getMemY();
		owedTicks = getMemInt("owedTicks");
		debris = getMemDebris();
		zeppelin = getMemBoolean("zeppelin");
		if(isZeppelin()) {
		destX = getMemInt("destX");
		destY = getMemInt("destY");
		fuelCells = getMemInt("fuelcells");
		ticksTillMove = getMemInt("ticksTillMove");
		}
		int i = 0;
		ArrayList<TradeSchedule> tses = tradeSchedules();
		while(i<tses.size()) {
			tses.get(i).synchronize();
			i++;
		}
	}

	
	 public boolean slotsFree() {
		 int lvl=0;
		 try {
		 lvl=getPlayer().getPs().b.getUserBuildings(townID,"Headquarters")[0].getLvl();
		 } catch(IndexOutOfBoundsException exc) {
			 return false;
		 }
	/*	try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select lvl from bldg where tid = " + townID + " and name = 'Headquarters'");
			if(rs.next()) lvl = rs.getInt(1);
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }*/
		
		int slots = 1+attackServer().size()+God.returnNumUniqueTowns(townID);
		// Get the number of unique towns for which there are supportAU from this one!
		// This is why originalTID is kept.
		// extra 1 is because level 1 from 0 based is like 2, need it to not include
		// zero in this count.
		if(slots>lvl) return false;
		else return true;
	}

	/**
	 * Adds a new level 0 building.
	 * @param type
	 * @param lotNum
	 * @return
	 */
	public Building addBuilding(String type,int lotNum) {
		return addBuilding(type,lotNum,0,1);
	}
	
	public void checkForBadRaids() {
		
		int i = 0; Raid r;
		while(i<attackServer().size()) {
			r = attackServer().get(i);
			if(r.getTown2()==null) {
				System.out.println(getTownName() + " is saving us from " + r.raidID);
				try {
				int j = 0;
				while(j<6) {
					setSize(j,r.getAu().get(j).getSize());
					j++;
				}
				} catch(Exception exc) { exc.printStackTrace(); System.out.println("Town saved."); }
				
				long[] res = getRes();
				synchronized(res) {
					res[0]+=r.getMetal();
					res[1]+=r.getTimber();
					res[2]+=r.getManmat();
					res[3]+=r.getFood();

					
				}
				
				r.deleteMe();
			}
			i++;
		}
	}
	/**
	 * Adds a building of any level.
	 * @param type
	 * @param lotNum
	 * @param lvl
	 * @return
	 */
	public Building addBuilding(String type, int lotNum, int lvl, int lvlUp) {
		// Adds a new building. Which means shit can go down.
			
		
			
			UberStatement stmt;
			try {

		     
		      stmt = con.createStatement();
		      
		      // First things first. We update the player table.
		      boolean transacted=false;
		      while(!transacted) {
		    	  try {
		      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.

			stmt.executeUpdate("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp,deconstruct) values ('" + type + "'," + lotNum + "," + lvl +"," +
					"0" + ",0,0,0," + townID + ","+lvlUp+",false);");
			
			int bid = 0;
			int timesTried=0;
			ResultSet id; 
			// We want it to break out if bid is not zero or timesTried > 1000. breakout = (!0 + !<1000)
			// so stayin = !(!0 + !<1000) = 0&&<1000.
			while(bid==0&&timesTried<1000) {
				Thread.currentThread().sleep(10);

				id = stmt.executeQuery("select bid from bldg where tid = " + townID + " and slot = " + lotNum);
				if(id.next()) bid = id.getInt(1);
				
				id.close();
				timesTried++;
			}
		
			Building b = new Building(bid,getPlayer().God);
			
			bldg().add(b);
			
			stmt.execute("commit;"); stmt.close(); transacted=true; 
			return b; } catch(MySQLTransactionRollbackException exc) { } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			} catch(SQLException exc) { exc.printStackTrace(); }
			return null;
		
	}
	
	public void loadBuilding(Building b) {
		
		// So in both programs, player has not been set when load is called...we need to keep track of people "lost" in that period of time...
		// these will be kept, safe, in two variables. When a player is finally added, all will be put to rights.
		bldg().add(b);
		
		if(b.getTicksToFinish()>=0||b.Queue().size()>0||b.getNumLeftToBuild()>0) {
			// otherwise will not be added if just people are building!!!
			bldgserver().add(b); }
		
		 if(b.getType().equals("Construction Yard")) {
	//	res[4]+=b.peopleInside; 
	//	if(player!=null)
	//	player.totalPopulation+=b.peopleInside;
		}
		else if(b.getType().equals("Trade Center")) { 
		//res[4]+=b.peopleInside;
		//if(player!=null)
			//player.totalPopulation+=b.peopleInside;
		}
		else if(b.getType().equals("Metal Warehouse")) {
			getResCaps()[0]+=b.getCap();
		}
		else if(b.getType().equals("Timber Warehouse")) {
			getResCaps()[1]+=b.getCap();
		}
		else if(b.getType().equals("Manufactured Materials Warehouse")) {
			getResCaps()[2]+=b.getCap();;
		}
		else if(b.getType().equals("Food Warehouse")) {
			getResCaps()[3]+=b.getCap();
		} else if(b.getType().equals("Metal Mine")) {
			getResInc()[0]=GodGenerator.gameClockFactor*baseResourceGrowthRate*Math.pow(b.getLvl()+1,2)/3600;
		} else if(b.getType().equals("Timber Field")) {
			getResInc()[1]=GodGenerator.gameClockFactor*baseResourceGrowthRate*Math.pow(b.getLvl()+1,2)/3600;
		} else if(b.getType().equals("Manufactured Materials Plant")) {
			getResInc()[2]=GodGenerator.gameClockFactor*baseResourceGrowthRate*Math.pow(b.getLvl()+1,2)/3600;
		} else if(b.getType().equals("Food Farm")) {
			getResInc()[3]=GodGenerator.gameClockFactor*baseResourceGrowthRate*Math.pow(b.getLvl()+1,2)/3600;
		}
		
		int i = 0;
		// need to make sure that buildings on building server have correct ticks.
	
	//	if(b.type.equals("Communications Center")){  
	//		if(player!=null)
		//		player.totalMessengers+=b.peopleInside;}
	//	if(b.type.equals("Institute")) {	
			//if(player!=null)
			
		//	player.totalScholars+=b.peopleInside;res[4]+=b.peopleInside;
	//	if(player!=null)
	//		player.totalPopulation+=b.peopleInside;}
	


	}
	
	/*public void addPlayer(Player player) {
	this.setPlayer(player);
		player.totalMessengers+=messLost;
		player.totalScholars+=scholLost; // the lost ones return...
		setPlayerName(player.username);
		
		// uh, I'm not sure if this is done elsewhere, but the buildings do not know the correct engineer amount, because the first
		// thing they do is test to see if the ticks level is less than the current tick amount, and then they modify ticks to include
		// all new engineers. So here, I set all the buildings' tick levels at once, with add player, which happens only
		// after player is made and all buildings are strapped up.
		
		int i = 0;
		while(i<bldg().size()) {
			bldg().get(i).modifyTicksLevel(totalEngineers,player.God.Maelstrom.getEngineerEffect(getX(),getY()),player.engTech); 
			bldg().get(i).modifyPeopleTicks(totalEngineers,player.God.Maelstrom.getEngineerEffect(getX(),getY()),player.engTech); 
		//	bldg.get(i).modifyUnitTicksForQueue(au,totalEngineers);
			i++;
		} // boo yah.
		
	}*/
	public Building findBuilding(int bid) {
		int i = 0;
		ArrayList<Building> bldg = bldg();
		while(i<bldg.size()) {
			if(bldg.get(i).bid==bid) return bldg.get(i);
			i++;
		}
		return null;
	}
	 public boolean levelUpBuilding(int bid) {
		// This thing finds the building and levels it up. Currently one must wait before leveling up again - if you add
		// the same building object twice to the arraylist, I'm not sure what it's going to do. This may need some redesigning later.
		
		int i = 0;
		UserBuilding holdBldg = getPlayer().getPs().b.getUserBuilding(bid);
		
		i = 0; boolean canBuild = true;
		// building limit...
		
		
		if(canBuild) {// make it so it returns false if unable to build, and checks
			// bldg server size.
			if(!holdBldg.isDeconstruct()) {
				if(holdBldg.getLvl()>=maxBldgLvl) return false; // no buildings beyond thirty!
		int k = 0;
		double additive;
		long res[] = getRes();
		synchronized(res) {
		 do {
			//cost[u]*Math.pow((blvl+blvlups+1),(2+.03*(blvl+blvlups+1)))
			 res[k]-=holdBldg.getCost()[k]*Math.pow(holdBldg.getLvl()+holdBldg.getLvlUps()+1,(2+.03*(holdBldg.getLvl()+holdBldg.getLvlUps()+1)));
			
			 k++;
		 } while(k<res.length); // now to add the building to the server.
		}
			}
			 Building actb = findBuilding(bid);

		 if(holdBldg.getLvlUps()==0) {
		actb.levelUp(getTotalEngineers(),getPlayer().God.Maelstrom.getEngineerEffect(getX(),getY()),getPlayer().getEngTech());
		actb.setLvlUps(holdBldg.getLvlUps() + 1);
			
		}
		 else {
			 // okay so lvlUps>0. We know this. We just want to increase it then to let the server know to fix it.
			 actb.setLvlUps(holdBldg.getLvlUps() + 1);
			
		 }
		return true;
		
		} else return false; // So now no building if it's already on the queue, simply solution for now.

	}
	/*public Town() {
		setRes(new long[5]);
		getRes()[0]=0;
		au = new ArrayList<AttackUnit>();
		
		townID = 0;
		setTownName(("Testtown"));
		
		setPlayerName("TestPlayer");
		
		setResInc(new double[5]);
		
		getResInc()[0] = 0;
	}*/
	
/*	public void addUnitType(AttackUnit aunit) {
		au.add(aunit);
	}*/
	 
	
	public void makeZeppelinFuel(int num) {
		/*
		 * This method checks for buildings that are Airship Platforms and loads them with teh fuels.
		 */
		int i = 0; Building b;
		double cloudFactor = getPlayer().God.Maelstrom.getEngineerEffect(getX(),getY());
		while(i<bldg().size()) {
			b = bldg().get(i);
			if(b.getType().equals("Airship Platform")) {
				/*
				 * 	 public static int ticksPerFuelPointBase =(int) Math.round((3600.0*24.0/((double) GodGenerator.gameClockFactor*10)));
	 					public static int daysOfStoragePerAirshipPlatform = 4;
				 */
					 // if we just loaded, then ticksPerPerson isn't set and must be. If we don't cap may not work for Airship Platforms!
					if(b.getTicksPerPerson()==0) b.modifyPeopleTicks(getTotalEngineers(),cloudFactor,getPlayer().getEngTech());

					if(b.getTicksLeft()>=b.getTicksPerPerson()) {
						
						// so we find out how many we can put in one platform.
					//	System.out.println("Adding fuel cell...");
						 double howMany = ((double) b.getTicksLeft())/((double) b.getTicksPerPerson());
						 int howManyRounded = (int) Math.floor(howMany);
						 int newTicksLeft =  (int) Math.floor((howMany-howManyRounded)*b.getTicksPerPerson());
						if(b.getPeopleInside()<b.getCap())
						b.setPeopleInside(b.getPeopleInside()+howManyRounded); // PEOPLE'RE FUEL NOW!
						
						b.setTicksLeft(newTicksLeft);
				
					

					} else {
					//	System.out.println("How many ticks left?" +b.getTicksLeft() + " of " +  b.getTicksPerPerson());
						b.setTicksLeft(b.getTicksLeft()+num); }
					
				
			}
			
			i++;
		}
	}
	
	public void giveFuelToZeppelin(int num) {
		// Gives the fuel to the Zeppelin if the Zeppelin is over a town.
		Town t = getPlayer().God.findZeppelin(getX(),getY()); 
	//	if(townID==3844)
	//	System.out.println("I found a zeppelin and it's townID is  "+ t.townID  + " and  it's player id is " +t.getPlayer().ID + " as compared to mine, " + getPlayer().ID + " ");
		
		if(t.townID!=0&&t.getPlayer().ID==getPlayer().ID&&t.getDestX()==t.getX()&&t.getDestY()==t.getY()) {
			int maxZeppFuel = (int) Math.round(God.getAverageLevel(t)*maxFuelCells);

			// REFUELIN TIME
			int i = 0; Building b;
			double cloudFactor = getPlayer().God.Maelstrom.getEngineerEffect(getX(),getY());
			while(i<bldg().size()) {
				b = bldg().get(i);
				if(b.getType().equals("Airship Platform")&&b.getPeopleInside()>0) {
					/*
					 * 	 public static int ticksPerFuelPointBase =(int) Math.round((3600.0*24.0/((double) GodGenerator.gameClockFactor*10)));
		 					public static int daysOfStoragePerAirshipPlatform = 4;
					 */
						int realTicks =b.getAirshipTicks(getTotalEngineers(),cloudFactor,getPlayer().getEngTech());
					//	System.out.println("Ticking through refuel tick " + b.getRefuelTicks() + " of " + realTicks);

						realTicks/=refillSpeed; // You can refill four times as fast as you gain fuel.
						
						 // if we just loaded, then ticksPerPerson isn't set and must be.
						if(b.getRefuelTicks()>=realTicks) {
							 double howMany = ((double) b.getRefuelTicks())/((double) realTicks);
							 int howManyRounded = (int) Math.floor(howMany);
							 int newFuelTicks =  (int) Math.floor((howMany-howManyRounded)*realTicks);
							// SHIT NEED ANOTHER TICKER...
					//		System.out.println("Giving up a fuel cell!");
							if(t.getFuelCells()<maxZeppFuel) {
							t.setFuelCells(t.getFuelCells()+howManyRounded);
							b.setPeopleInside(b.getPeopleInside()-howManyRounded);
							b.setRefuelTicks(newFuelTicks);
							}

						} else {
							b.setRefuelTicks(b.getRefuelTicks()+num); }
						
					
				}
				
				i++;
			}
			
		}
	}
	
	public void checkZeppelinMovement() {
		
		// This code is enacted by the Zeppelin.
		
		if(isZeppelin()&&(getX()!=getDestX()||getY()!=getDestY())) {
			if(getTicksTillMove()>=zeppelinTicksPerMove) {
				// SHIT NEED ANOTHER TICKER...
				int totalMoved=0; // Need to move AT LEAST 2 PLACES EACH TIME.
				System.out.println("Now moving.");
				while(totalMoved<2&&(getY()!=getDestY()||getX()!=getDestX())) {
					if(getY()!=getDestY()) {
						if(getY()>getDestY())
						setY(getY()-1);
						else if(getY()<getDestY()) // just in case so the else doesn't fall into the = case.
						setY(getY()+1);
						System.out.println("Changing y to " + getY());
						totalMoved++;
					}
					if(getX()!=getDestX()) {
						if(getX()>getDestX())
						setX(getX()-1);
						else if(getX()<getDestX()) // just in case so the else doesn't fall into the = case.
						setX(getX()+1);
						System.out.println("Changing x to " + getX());

						totalMoved++;
					}
					System.out.println("totalMoved is " + totalMoved);
				
				}
				System.out.println("Outside this loop now.");
				if(getY()==getDestY()&&getX()==getDestX()) {
				
					System.out.println("Done with moving.");
					ArrayList<Town> zeppelins = getPlayer().God.findZeppelins(getX(),getY());
					while(zeppelins.size()>1) { // SO WE KEEP STRIKING UNTIL ZEPPELINS DIES.
						System.out.println("Found another zepp here.");
						//	public boolean attack(int yourTownID, int enemyx, int enemyy, int auAmts[], String attackType, int target,String name) {

						// TIME FOR WAR! NOBODY LOVES YOU!
						int auAmts[] = new int[getAu().size()];
						int i = 0;
						while(i<auAmts.length) {
							auAmts[i]=getAu().get(i).getSize();
							i++;
						}
						synchronized(attackServer()) {
						getPlayer().getPs().b.attack(townID,getX(),getY(),auAmts,"attack",0,"noname");
						getPlayer().God.combatLogicBlock(attackServer().get(attackServer().size()-1),""); // EARLY CALL
						// FOR THE LATEST RAID JUST ADDED!
						}
						zeppelins = getPlayer().God.findZeppelins(getX(),getY()); // refresh after the attack.
						// now this loop is gonna keep running...though this Blimpie will die if it loses.
						// The town list will not return the zeppelin again, though the object will still exist
						// till this purpose is done. Then once it's done, it's thrown to the ashes.
						
					}
				}

			} else {
				System.out.println("Ticks till move: " + getTicksTillMove() + " of " + zeppelinTicksPerMove);
				setTicksTillMove(getTicksTillMove()+1); }
			
		}
	}
	public boolean nukeCheck() {
		try {
		int i = 0; Building b,tb;
		while(i<bldg().size()) {
			b = bldg().get(i);
			boolean breakOut=false;
			if(b.getType().equals("Missile Silo")&&b.getTicksLeft()>0) {
				
				// MEANS A NUKE IS FLYIN'. 
				// BUT WHERE IS IT GOING?
				// Easy - hide the X and Y in the bunkerMode and refuelTicks fields!
				//(LIKE A PRO)
				
				int x = b.getBunkerMode();
				int y = b.getRefuelTicks();
				boolean skyNuke =b.isNukeMode(); // if nukeMode is true, is skyNuke.
				Town t = getPlayer().God.findTown(x,y);
				
				// HERE WE GO MOTHERFUCKER. TIME TO BURN YOUR FUCKING SHIT UP!
				if(b.getTicksLeft()==1) {
					
					// BLOW THE SHIT UP.
					int j = 0;
					
					int aggregateLvl = b.getLvl();
					int oldAggLevel = b.getLvl();
					while(j<t.bldg().size()) {
						tb = t.bldg().get(j);
						if(tb.getType().equals("Missile Silo")&&tb.getLvl()>1) { // For each Missile Silo, let it contribute!
							System.out.println("Found a missile silo to block.");
							if(aggregateLvl<=tb.getLvl()) {
								System.out.println("Deflecting missile entirely.");
								int toLvlDown = aggregateLvl;
								int k = 0;
								System.out.println("Missile silo defense before: " + tb.getLvl() + " and toLvlDown is "  + toLvlDown);

								while(k<toLvlDown-1) {
									t.levelDown(tb.bid);
									k++;
								}
								System.out.println("Missile Silo after:" + tb.getLvl());

								// need to make a missile failed report.
								String unitStart=""; String unitNames="";String unitEnd="";
								 k = 0;
								while(k<t.getAu().size()) {
									unitStart+=","+t.getAu().get(k).getSize();
									unitNames+=","+t.getAu().get(k).getName();
									unitEnd+=",0";

									k++;
								}
								makeNukeReport(t,unitStart,unitEnd,null,null,unitNames,toLvlDown,false);
								
								breakOut=true;
								break;
							} else {
								// this means we must simply subtract from the aggregate level on our way through!
								int toLvlDown = (tb.getLvl());  // level it down completely.
								System.out.println("aggregateLvl was " +aggregateLvl);

								aggregateLvl-=(tb.getLvl());
								System.out.println("aggregateLvl is now " +aggregateLvl);
								int k = 0;
								System.out.println("Missile silo defense before: " + tb.getLvl() + " and toLvlDown is "  + toLvlDown);
								while(k<toLvlDown-1) { // we level it down accordingly! But if we do 9, and that means we go 9 times, it'll be 0. So we always do -1.
									t.levelDown(tb.bid);
									k++;
								}
								System.out.println("Missile Silo after:" + tb.getLvl());

								
							}
							
							
							
							
						}
						j++;
					}
					
					// now that we're out...
					
					killBuilding(b.bid); // KILL THE SILO. THEN DO THE DAMAGE.

					if(!breakOut) {
					
						// If the nuke was defeated, it shouldn't do any of this after the aggregateLvl has been adjusted.
						// First off, we need to basically call the fucking building leveler.
						int k = 0;
						String bombReturn,bombResultBldg,bombResultPpl;
						String bombResultBldgAgg="",bombResultPplAgg="";
						if(!skyNuke)
						while(k<aggregateLvl) { // BOMB THE SHIT OUT OF THEM.
							bombReturn = GodGenerator.bombLogicBlock(null,this,t);
							// other wise needs to be used after the attack is complete.
						//	System.out.println("Return value for bombLogic is " + bombReturn);
							int returnNum = Integer.parseInt(bombReturn.substring(0,bombReturn.indexOf(",")));
							//BHAttackViewer newWindow = new BHAttackViewer(holdAttack,defNames,offNames,defUnitsBefore,offUnitsBefore,defUnitsAfter,offUnitsAfter);
						
							 bombResultBldg = bombReturn.substring(bombReturn.indexOf(",")+1,bombReturn.lastIndexOf(","));
							 bombResultPpl = bombReturn.substring(bombReturn.lastIndexOf(",")+1,bombReturn.length());
							bombResultBldgAgg+=bombResultBldg;
							bombResultPplAgg+=bombResultPpl;
							k++;
						}
						else{ bombResultBldgAgg="null+"; bombResultPplAgg+="null+";}
						
						// NOW KILL ALL THEIR CHILDREN.
						String unitStart=""; String unitEnd = "";String unitNames="";

						double percToRem = aggregateLvl*.05;
						if(percToRem>1) percToRem=1;
						k = 0; AttackUnit a; int totalExpMod =0;
						while(k<t.getAu().size()) {
							a = t.getAu().get(k);
							totalExpMod+=a.getSize()*a.getExpmod();
							k++;
						}
						
						
						k=0;
						synchronized(t.getAu()) {
						while(k<t.getAu().size()) {
							a = t.getAu().get(k);
							unitStart+=","+a.getSize();
							int holdOld = a.getSize();
							unitNames+=","+a.getName();
							double toRemPerc=0;
							if(!skyNuke)
							 toRemPerc =percToRem;
							System.out.println("toRemPerc is " + toRemPerc+ " from total percToRem " + percToRem);
							// Let's go with soldiers...clearly perc to remove is in terms of soldiers. So for tanks, you
							// need to remove less...no...more...if you multiply the 5% across the way it should work.
							System.out.println("size " + k + " before: " + a.getSize());
							a.setSize(a.getSize()-(int) Math.round(((double) a.getSize())*toRemPerc));
							System.out.println("size " + k + " after: " + a.getSize());
							if(a.getSize()<0) a.setSize(0); // Can never be too certain!
							unitEnd+=","+(holdOld-a.getSize());
							k++;
						}
						
						}
						
						// FINALLY, THE FALLOUT...
						
						// use PercToRem to make the proper cloud...
						
						ArrayList<Hashtable> mapTiles = getPlayer().God.getMapTileHashes();

						k = 0; double minDist = 9999999;
								int specIndex = -1;
						while(k<mapTiles.size()) {
							
							int centerx = (Integer) mapTiles.get(k).get("centerx");
							int centery = (Integer) mapTiles.get(k).get("centery");
							double dist = Math.sqrt(Math.pow(t.getX()-centerx,2) + Math.pow(t.getY()-centery,2));
							if(dist<minDist) {
								minDist=dist;
								specIndex=k;
							}
							k++;
						}
						
						int cloudX = (Integer) mapTiles.get(specIndex).get("centerx");
						int cloudY = (Integer) mapTiles.get(specIndex).get("centery");
						System.out.println("Placing the cloud at " + cloudX +"," + cloudY + " center tile.");
						if(skyNuke)
						getPlayer().God.Maelstrom.addCloud(-percToRem,cloudX,cloudY,true);
						if(!skyNuke)
						getPlayer().God.Maelstrom.addCloud(-percToRem,cloudX,cloudY,false);
						
						
						makeNukeReport(t,unitStart,unitEnd,bombResultBldgAgg,bombResultPplAgg,unitNames,(oldAggLevel-aggregateLvl),true); // AND NOW WE'RE DONE!
					}
					i--; // building is gone and we must subtract then from i because
					// it's no longer part of the system.
					
				} else{
					System.out.println("Nuke ticks: " + b.getTicksLeft());
					b.setTicksLeft(b.getTicksLeft()-1);
				}
				
				
				
			}
			
			i++;
		}
		} catch(Exception exc) { exc.printStackTrace(); }
		return true;
	}
	
	public void makeNukeReport(Town t2, String t2UnitsBefore, String t2UnitsAfter, String bombResultBldg, String bombResultPpl, String t2UnitNames, int aggregateLevelLoss,boolean nukeSucc) {
		try {
		UberStatement stmt = con.createStatement();

		int o = 0;
		ArrayList<Player> holdForP = new ArrayList<Player>();
		
		Hashtable wm = t2.getPlayer().getPs().b.getWorldMap();
		
		ArrayList<Integer> playerIDs = new ArrayList<Integer>();
		Hashtable[] towns = (Hashtable[]) wm.get("townHash");
		while(o<towns.length) {
			int k = 0; boolean found=false;
			while(k<playerIDs.size()) {
				if(playerIDs.get(k).toString().equals(((Integer) towns[o].get("pid")).toString())) {
					found = true; break;
				}
				k++;
			}
			
			if(!found) playerIDs.add((Integer) towns[o].get("pid"));
			o++;
		}
		
		o = 0;
		while(o<playerIDs.size()) {
			holdForP.add(getPlayer().God.getPlayer(playerIDs.get(o)));
			o++;
		}
		
		o = 0; boolean found = false;
		while(o<holdForP.size()) {
			if(holdForP.get(o).ID==getPlayer().ID) {
				found=true; break;
			}
			o++;
		}
		if(!found) holdForP.add(getPlayer());
		
		o = 0;
		if(bombResultBldg==null) {
			//-2+",null,null+"
			bombResultBldg="null+";
		}if(bombResultPpl==null) {
			//-2+",null,null+"
			bombResultPpl="null+";
		}
		String toPut = "Your nuclear facilities fired defensive missiles and caused a " + aggregateLevelLoss + " Power Level reduction on this nuclear missile. Correspondingly, they went down a total " + aggregateLevelLoss + " levels.";
  	  if(aggregateLevelLoss==0) toPut = "This town had no nuclear facilities to defend against this missile. Try building a Missile Silo next time, as it can defend from Nukes as well as create them!";
  	  if(!nukeSucc){
  		  toPut += " As a result, this nuclear missile failed to penetrate the defenses of the town.";
  	  }
  	  String t2UnitNamesPut,t2UnitsBeforePut,t2UnitsAfterPut;
  	  String toPutTemp;
	      while(o<holdForP.size()) {
	    	  if(holdForP.get(o).ID==t2.getPlayer().ID||holdForP.get(o).ID==getPlayer().ID) {
	    		  toPutTemp=toPut;
	    		  t2UnitNamesPut=t2UnitNames;
	    		  t2UnitsBeforePut=t2UnitsBefore;
	    		  t2UnitsAfterPut=t2UnitsAfter;
	    	  }
	    	  else{
	    		  toPutTemp = ""; 
	    		  t2UnitNamesPut = ",???,???,???,???,???,???";
	    		  t2UnitsBeforePut=",0,0,0,0,0,0";
	    		  t2UnitsAfterPut=",0,0,0,0,0,0";

	    	  }
	    	stmt.execute("insert into statreports (defender,invade,invsucc,scout,m,t,mm,f,pid,tid1,tid2,auoffst,auofffi,auoffnames,genocide,bombbldgdata,bombppldata,combatheader,ax,ay,dx,dy,offTownName,defTownName,nuke,nukeSucc) values" +
			      		"(false,"+ "false" + "," + "false" + "," +"false" + ","+0 + "," +0+ "," +0 + "," +0 +"," + holdForP.get(o).ID + ","+ townID + "," + t2.townID + ",\"" + t2UnitsBeforePut + "\",\"" + t2UnitsAfterPut + "\",\"" 
			      		+ t2UnitNamesPut + "\"," + "true" + ",\""+bombResultBldg+"\",\""
			      		+ bombResultPpl+ "\",'"+toPutTemp+"' ,"+ getX()+","+getY()+","+t2.getX()+","+t2.getY()+",'"+getTownName()+"','"+t2.getTownName()+"',true,"+nukeSucc+");"); // use genocide to trick Markus into making a report for it with bombings.
	    	 
		      o++;
	      }
		} catch(SQLException exc) { exc.printStackTrace();} 
	}
	public long getPop() {
		if(res==null) res = getMemRes();
		return res[4];
	}

	
	
	public int getStealth() {
		return getPlayer().getStealth();
	}
	/* public void changeUnitAmount(AttackUnit aunit, int amount) {
		// If you want to subtract this amount from the AttackUnit sitting at base.
		// Say you have 5 soldier types sitting at base and three come back. Then this adds
		// 3 to your 5. Can subtract, also.
		
		int i = 0;
		AttackUnit holdAU;
		ArrayList<AttackUnit> au= getAU();
		synchronized(au) {
		do {
			 holdAU = au.get(i);
			if(aunit.getName().equals(holdAU.getName())) { holdAU.setSize(holdAU.getSize() + amount); break; }
			i++;
		} while(i<au.size());
		}
		
		
	}*/
	/*public void setTown(String townName) {
		this.townName = townName;
	}*/
	public String getTown() {
		return getTownName();
	}
/*	public void setTotalEngineers(int totalEngineers) {
		this.totalEngineers = totalEngineers;
	}*/
	public void lowerCapsAndRes(Building b) {
		//System.out.println("Lower caps for " + b.lotNum + " which is a " + b.type);
		if(b.isMineBldg()) {
			int newcap = (int) Building.getCap(b.getLvl(),true);
			synchronized(getResCaps()) {
			if(b.getType().equals("Metal Warehouse")) {
				getResCaps()[0]-=(b.getCap()-newcap);
			}
			else if(b.getType().equals("Timber Warehouse")) {
				getResCaps()[1]-=(b.getCap()-newcap);
			}
			else if(b.getType().equals("Manufactured Materials Warehouse")) {
				getResCaps()[2]-=(b.getCap()-newcap);
			}
			else if(b.getType().equals("Food Warehouse")) {
				getResCaps()[3]-=(b.getCap()-newcap);
			} }
			b.setCap(newcap); // So we use the old one to get the difference between the new and old
			// and subtract that amount from the town's resCaps!
		}else 
		b.setCap(Building.getCap(b.getLvl(),false));
		 // level has already been lowered.
		synchronized(getResInc()) {
		if(b.getType().equals("Metal Mine")) {
			getResInc()[0]=GodGenerator.gameClockFactor*Town.baseResourceGrowthRate*Math.pow(b.getLvl()+1,2)/3600;

		} else if(b.getType().equals("Timber Field")) {
			getResInc()[1]=GodGenerator.gameClockFactor*Town.baseResourceGrowthRate*Math.pow(b.getLvl()+1,2)/3600;

		}else if(b.getType().equals("Manufactured Materials Plant")) {
			getResInc()[2]=GodGenerator.gameClockFactor*Town.baseResourceGrowthRate*Math.pow(b.getLvl()+1,2)/3600;

		}else if(b.getType().equals("Food Farm")) {
			getResInc()[3]=GodGenerator.gameClockFactor*Town.baseResourceGrowthRate*Math.pow(b.getLvl()+1,2)/3600;

		}}
	}
	 public String levelDown(int bid) {
		// Crap, this is actually...fairly complex. I guess the lvlups makes it so the level will
		// increase if it's on the server. So we don't need to worry about that. But what about people ticks?
		// We need to maybe even take it off the bldg server if it's got people in there that it can't support...
		// UserBuilding bldg[] = getPlayer().ps.b.getUserBuildings(townID,"all");
		 Player p = getPlayer();
		 UserBuilding b = p.getPs().b.getUserBuilding(bid);
		 Building actb = findBuilding(bid);
				//System.out.println("Leveling down " + b.lotNum + " which is a " + b.type + " at " + b.lvl + " going down one.");
		if(b.getLvl()>0) // so if this is a mine, then it doesn't go below zero if it is bombed from zero. Just remains the same.
		actb.setLvl(b.getLvl() - 1);
		
		if(b.getLvl()<=0&&!b.getType().equals("Metal Mine")&&!b.getType().equals("Timber Field")
				  &&!b.getType().equals("Manufactured Materials Plant")&&
				  !b.getType().equals("Food Farm")) {
			
			  
			// destroy building code.
		/*	int i = 0;
			while(i<bldgserver().size()) {
				if(bldgserver().get(i).getLotNum()==b.getLotNum()) {bldgserver().remove(i); break;}
				i++;
			}*/
			// if this is a minebldg it autolowers caps inside killBldg, mines never die
			// so don't do lower caps and res here.
			
			killBuilding(bid); // doesn't get bldgserver for some reason.
			
			return "d " + b.getLotNum() + "."+(b.getLvl())+"."+b.getPeopleInside() + "." + b.getType();
		}
	//	lowerCapsAndRes(actb);
		String toRet = "l " + b.getLotNum()+"." + (b.getLvl());
		// now we know we're keeping the building..
		// we need to reset the cap and if the number of people are above the cap, we need to remove
		// them and all of the people queued up to be made.
		// Also, if lvlUps = 0 then this thing is on the building server ONLY because of people being made
		// and so should be removed.
		// Basically, if(made+queue>=newcap) then queue+=(newcap-made-queue),if(queue<0) { made+=queue,  queue=0.   if(made<0) made = 0.}
		
		// So if made = 5, queue = 6, and new cap = 8, then queue = -3, which means 3 are owed from made
		// after queue gave it's piece, so we do made+=-3 = made-3, and then we set queue = 0 to reset it
		// and if made<0 after this then as a precaution we do if(made<0) made = 0.
		
		// finally, if queue==0 AND lvlUps = 0, then we know to remove the building from the bldgserver().
		//lowerCapsAndRes(b);
		
		int amttosubtract = b.getPeopleInside();
		if(b.getNumLeftToBuild()+b.getPeopleInside()>b.getCap()) {
			actb.setNumLeftToBuild((int) (b.getNumLeftToBuild() + (b.getCap()-b.getPeopleInside()-b.getNumLeftToBuild())));
			
			if((b.getNumLeftToBuild() + (b.getCap()-b.getPeopleInside()-b.getNumLeftToBuild()))<0) {
				actb.setPeopleInside(b.getPeopleInside()
						+ b.getNumLeftToBuild()); 
				actb.setNumLeftToBuild(0); 
			if((b.getPeopleInside()
					+ b.getNumLeftToBuild())<0) actb.setPeopleInside(0);
			}
			b = p.getPs().b.getUserBuilding(bid);
			amttosubtract-=b.getPeopleInside(); // now we know how many people were lost...
			toRet+="."+amttosubtract;
			removePeople(actb,amttosubtract);
			// no use putting below block in above block, it doesn't cover for == 0, only <0! It could do <=0 but
			// this means extra cycles when numLeftToBuild is already 0, which will be often...
			/*if(b.getNumLeftToBuild()==0&&b.getLvlUps()==0) { // means no lvling so it's just people that was on it.
				int i = 0;
				while(i<bldgserver().size()) {
					if(bldgserver().get(i).getLotNum()==b.getLotNum()) {bldgserver().remove(i); break;}
					i++;
				}
			}*/
		} else toRet+=".0";
		
		toRet+="."+b.getType();
		return toRet;
		
	}
	public int getTotalEngineers() {
		
		ArrayList<Building> bldg = bldg();
		int i = 0; int totalEngineers=0;
		while(i<bldg.size()) {
			if(bldg.get(i).getType().equals("Construction Yard"))
			totalEngineers+=bldg.get(i).getPeopleInside();
			i++;
		}
		return totalEngineers;
		/*
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select sum(ppl) from bldg where tid = " + townID + " and name = 'Construction Yard';");
			int totalEngineers=0;
			if(rs.next())
			 totalEngineers = rs.getInt(1);
			rs.close(); stmt.close();
			return totalEngineers;
		} catch(SQLException exc) { exc.printStackTrace(); } 
		
		
		return 0;*/
	}
	public int getTotalScholars() {
		

		ArrayList<Building> bldg = bldg();
		int i = 0; int totalEngineers=0;
		while(i<bldg.size()) {
			if(bldg.get(i).getType().equals("Insitute"))
			totalEngineers+=bldg.get(i).getPeopleInside();
			i++;
		}
		return totalEngineers;
		/*
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select sum(ppl) from bldg where tid = " + townID + " and name = 'Institute';");
			int totalScholars=0;
			if(rs.next())
				totalScholars = rs.getInt(1);
			rs.close(); stmt.close();
			return totalScholars;
		} catch(SQLException exc) { exc.printStackTrace(); } 
		return 0;*/
	}

	public  void setSize(int index, int size) {
		synchronized(getAu()) {
	getAu().get(index).setSize(size);	}
	}
	 public void removePeople(Building b, int peopleToRem) {
		// makes sure populations all stay as they should!
		// if(b.getType().equals("Communications Center")) getPlayer().totalMessengers-=peopleToRem;
	//	else if(b.getType().equals("Institute")) getPlayer().totalScholars-=peopleToRem;
	
			//long res[] = getRes();
		//res[4]-=peopleToRem; 
		//setRes(res);
	//	getPlayer().totalPopulation-=peopleToRem;
	}
	
	 public void removeAU(int slot, int peopleToRem) {
		
		// so when attack units are lost, the total population needs to be decreased, but nothing else,
		// and this method, when called by the attackServer, does that bookkeeping.
		/* try {
		int i = 0; int popSize=0;
		ArrayList<AttackUnit> au = getAu();
		if(slot<6) {
			setMemInt("au" + (slot+1), getMemInt("au"+(slot+1))-peopleToRem);
			 i = 0;
			while(i<au.size()) {
				if(au.get(i).getSlot()==slot) break;
				i++;
			}
			
			getPlayer().setTotalPopulation(
					getPlayer().getTotalPopulation() - (au.get(i).getPopSize()*peopleToRem));

		} else {
			try {
				 i = 0;
					while(i<au.size()) {
						if(au.get(i).getSlot()==slot) break;
						i++;
					}
					if(i!=au.size()) { // so no bad exceptions leaves the stmt open.
						UberStatement stmt = con.createStatement();
						
						stmt.execute("update supportAU set size = (size - " + peopleToRem + ") where slot = "+ au.get(i).getSlot() + " and tid = " + townID);
						stmt.close();
						
						au.get(i).getOriginalPlayer().setTotalPopulation(
								au.get(i).getOriginalPlayer().getTotalPopulation()
										- (popSize*peopleToRem));
				
					}
			} catch(SQLException exc) {
				exc.printStackTrace();
			}
		}
			} catch(Exception exc) { exc.printStackTrace(); System.out.println("Combat was saved.");}*/

		synchronized(au) {
			int popSize=0; int i = 0;
		while(i<au.size()) {
			if(au.get(i).getSlot()==slot){ popSize=au.get(i).getPopSize(); break; }
			i++;
		}

		AttackUnit a = au.get(i);
		if(a.getSupport()==0)
		getPlayer().setTotalPopulation(getPlayer().getTotalPopulation()-popSize*peopleToRem);
		else {
			// this means this is a support unit. We need to contact and remove population from the OTHER player
			// associated with this unit.
			
		a.getOriginalPlayer().setTotalPopulation(getPlayer().getTotalPopulation()-popSize*peopleToRem);
		
		// to avoid having to make multiple connections for each unit that hits zero, the player will instead check each town
		// and if a town has support units present that are at 0, they will be removed from db and from memory.
		
		}
		}
	}
	synchronized public void update() {
			if(owedTicks>0) {

		 if(getPlayer().ID==5||getPlayer().isQuest()) {
			 iterate(God.gameClock-owedTicks);
			 owedTicks=0;
			 save();
		 }
		 else getPlayer().update();
			}
	 }
	 public boolean stuffOut() {
			int i = 0;
			while(i<attackServer().size()) {
				if(attackServer().get(i).getTown1().townID==townID) {
					return true;
				}
				i++;
			}
			 i = 0;
			while(i<tradeServer().size()) {
				if(tradeServer().get(i).getTown1().townID==townID) {
					return true;
				}
				i++;
			}
			 if(bldgserver().size()>0) return true;
			 i = 0; Building b;
			 while(i<bldg().size()) {
				  b = bldg().get(i);
					if(b.getType().equals("Missile Silo")&&b.getTicksLeft()>0) {
						return true; //NUKE ALERTS.
					}
					i++;
			 }
			 if(isZeppelin()&&(getX()!=getDestX()||getY()!=getDestY())) return true;
			 // if this zeppelin is moving, then stuff is "out".
			if(getPlayer().getPs().b.isAlive()) return true; // program running means the player keeps cycling.

			return false;
		}
	 public void iterate(int num) {
	
	//	 if(townID==2958)
			//System.out.println("Town's player is now " + getPlayer());
		 double[] resInc=getResInc();
		 double[] resEffects=getResEffects();
		 long[] resCaps=getResCaps();
		double[] resBuff = getResBuff();
		long[] res = getRes();
		double[] newIncs = God.Maelstrom.getResEffects(resInc,getX(),getY());
		Player p = getPlayer();
		League l =p.getLeague();
		int j = 0;
		double taxRate=0;
		if(l!=null)
		 taxRate = l.getTaxRate(p.ID);
		Town zepp = getPlayer().God.findZeppelin(getX(),getY());
		if(getPlayer().ID==5&&zepp.townID!=0){
			resBuff = zepp.getResBuff(); // SUCKLEPOWA
			res = zepp.getRes();
			resCaps = zepp.getResCaps(); // HOLY SHIT YOU JUST HIJACKED THAT SHIT!
		}
		synchronized(resBuff) {
		do {
			
			resBuff[j]+=num*(newIncs[j]*(1-taxRate)*(resEffects[j]+1));
			
			
			j++;
		} while(j<res.length);
		}
		
		j =0;
		synchronized(res) {
		while(j<res.length) {
			if(resBuff[j]>=1) {
				int toAdd = (int) Math.floor(resBuff[j]);
				res[j]+=toAdd;
				resBuff[j]-=toAdd;
				if(res[j]>(resCaps[j]+Building.baseResourceAmt))
					res[j]=resCaps[j]+Building.baseResourceAmt; // SAME OBJECT! BUT NOT REALLY...
				// works even if you lose the building and suddenly have a massive over the limit
				// amount of resources! HAHA :D
				
			}
			j++;
		}
		}
		
		auCheck();
		checkForBadRaids();
		GodGenerator.attackServerCheck(this,p); // NONE of these things would be iterating ANYTHING if this player had
		// a num of iterations>1. Because that'd imply an update. And a player is never FROZEN if:
		// 1. It's AI is on
		// 2. It's got Raid, Trade, or Building building.
		GodGenerator.tradeServerCheck(this,p); // Not this thing.
		GodGenerator.buildingServerCheck(this); // Not this thing
		nukeCheck(); // Not this thing, either.
		try {
		makeZeppelinFuel(num);
		giveFuelToZeppelin(num);
		checkZeppelinMovement(); // Not this thing...
		} catch(Exception exc) { exc.printStackTrace(); System.out.println("Zeppelins saved."); }
		
		setInternalClock(getInternalClock() + num); // we only iterate after FINISHING THE SAVE!
		if(getInternalClock()>God.gameClock) setInternalClock(God.gameClock); // means owedTicks stretches past the last server restart,
	 }
	 
	 synchronized public void save() {
		 // for towns we save AU and then it's stats, then we call the save method of attached objects.
		 // raidSupportAU are deleted when a raid returns.
		 // supportAU are deleted by the AU Check method when there are no further raid support AU out nor supportAU at home.
	 try {
		 UberStatement stmt = con.createStatement();
	 	  int j = 6; String updateAU[] = null;
    	  if(getAu().size()>6) 
    	  updateAU = new String[getAu().size()-6];
    	  AttackUnit hau;
    	  while(j<getAu().size()) {
    		   hau = getAu().get(j); // get support/civvy units units.
    		//  System.out.println("Saving " + hau.name);
    
    	   if(hau.getSupport()>0) {
    		  // means it is a support au of some sort. This needs to be updated in a different place.
    		   
    		System.out.println("updating " + hau.getName() + " with size " + hau.getSize());
    			  updateAU[j-(6)] = "update supportAU set size = " + hau.getSize() + " where tid = " + townID + " and slotnum = "+ hau.getSlot() + ";";

    		
    			  }
    			
    		
	    	  stmt.executeUpdate(updateAU[j-(6)]);
    	  
	    	  j++;
	    	  
    	  }
    	  ArrayList<AttackUnit> au = getAu();
    	   
    	  String update = "update town set townName = '" + townName + "', x = " + x + ", y = " + y + 
	    	  ", m = " + getRes()[0] + ", t = " + getRes()[1] + ", mm = " + getRes()[2] + ", f = " + getRes()[3] + ", pop = " + getRes()[4] +
	    	   ", au1 = " +
	    	  au.get(0).getSize() + ", au2 = " +  au.get(1).getSize() + ", au3 = " +  au.get(2).getSize() + ", au4 = " +  au.get(3).getSize() +
	    	  ", au5 = " + au.get(4).getSize() + ", au6 =" +  au.get(5).getSize() +", owedTicks = " + owedTicks+", zeppelin = " + zeppelin + ",  fuelcells = " + fuelCells + 
	    	  ", ticksTillMove = " + ticksTillMove + ", destX = " + destX +", destY = " + destY + ", debm = " + getDebris()[0] + ", debt = " + getDebris()[1] + ", debmm = " + getDebris()[2] + ", debf = " + getDebris()[3] +" where tid = " + townID + ";";
	    	  stmt.executeUpdate(update);
	    	  
	    	  stmt.close();
    	   } catch(SQLException exc) { 
    		  exc.printStackTrace();
    		   }
    	   int i = 0;
    	   ArrayList<Building> bldg = bldg();
    	   while(i<bldg.size()) {
    		   bldg.get(i).save();
    		   i++;
    	   }
    	   i = 0;
    	   
    	
    	   
    	   ArrayList<Raid> as = attackServer();
    	   while(i<as.size()) {
    		      as.get(i).save();
    		   i++;
    	   }
    	   i = 0;
    	   ArrayList<TradeSchedule> tses = tradeSchedules();
    	   while(i<tses.size()) {
    		   tses.get(i).save();
    		   i++;
    	   }
    	   i = 0;
    	   ArrayList<Trade> tres = tradeServer();
    	   while(i<tres.size()) {
    		   tres.get(i).save();
    		   i++;
    	   }
	 
    	   
	 }
	 synchronized public boolean killBuilding(int bid) {
		// this removes a bldg both from the server and from the local object. A sad thing, but can be used
		// with deconstruct. Bldgserver must do the removing from the bldgserver object itself in however manner it chooses.
		// but it must do so before this method is called. And you're a bitchbastard.
		
		// i is the slot of bldg to remove.
		
		 Player p = getPlayer();Building b=null;
		try {
		 b = findBuilding(bid);
		//System.out.println("I found the building to kill.");

		System.out.println("Destroying " + b.getLotNum() + " which is a " + b.getType() +
" and bid of " + b.bid + " and of town " + townID + " b deconstruct is " + b.isDeconstruct());
		/*if(b.isMineBldg()) {
			if(b.getType().equals("Metal Warehouse")) {
				getResCaps()[0]-=b.getCap();
			}
			else if(b.getType().equals("Timber Warehouse")) {
				getResCaps()[1]-=b.getCap();
			}
			else if(b.getType().equals("Manufactured Materials Warehouse")) {
				getResCaps()[2]-=b.getCap();
			}
			else if(b.getType().equals("Food Warehouse")) {
				getResCaps()[3]-=b.getCap();
			}
			// in the event of a lost warehouse, you lose whatever is left of it's
			// holding capacity!
		}*/
		// now we have b. As this is an internal method, the user never sees it, and so it's okay.
		// we need to worry about the population here? it changes...
		
		UberStatement stmt;

	     
	      stmt = p.God.con.createStatement();
	      
	      // First things first. We update the player table.
	      boolean transacted=false;
	      while(!transacted) {
	    	  
	    	  try {
	      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
			stmt.executeUpdate("delete from queue where bid = " + bid + ";");

		stmt.executeUpdate("delete from bldg where bid = " + bid + ";");
		
		stmt.execute("commit;");stmt.close(); transacted=true; } catch(MySQLTransactionRollbackException exc) { }
	      
			
	      }
			bldg().remove(b);

		 } catch(SQLException exc) { exc.printStackTrace(); }
		 catch(NullPointerException exc) { exc.printStackTrace(); System.out.println("Building was not on the stack...was illegal."); }


	return true;
	}
	 public void checkBuildingDupes() {
			try {
			UberStatement stmt = God.con.createStatement();
			int i = 0;
			ResultSet rs; 
			Player p =getPlayer();
			int lotTech = p.getLotTech();
			while(i<lotTech) {
				rs =stmt.executeQuery("select count(*) from bldg where tid = " + townID + " and slot = " + i);
				int counter = 0;
				if(rs.next()) {
					counter=rs.getInt(1);
				}
				
				rs.close();
				if(counter>1) {
					int lowest =31;
					int lowestbid=0;
					
					rs = stmt.executeQuery("select bid,lvl from bldg where tid = " + townID + " and slot = " + i);
					while(rs.next()) {
						if(rs.getInt(2)<lowest) { lowest = rs.getInt(2); lowestbid = rs.getInt(1); }
					}
					rs.close();
					
					if(lowestbid!=0) {
						killBuilding(lowestbid);
					}

				}

				i++;
			}
			i=0;
			while(i<lotTech) {
				rs =stmt.executeQuery("select count(*) from bldg where tid = " + townID + " and name = 'Metal Mine';");
				int counter = 0;
				if(rs.next()) {
					counter=rs.getInt(1);
				}
				
				rs.close();
				if(counter==0) {
					//	public Building addBuilding(String type, int lotNum, int lvl, int lvlUp) {
					System.out.println(townID + " is replacing a metal mine.");
					addBuilding("Metal Mine",0,3,0);
				}
				rs =stmt.executeQuery("select count(*) from bldg where tid = " + townID + " and name = 'Timber Field';");
				 counter = 0;
				if(rs.next()) {
					counter=rs.getInt(1);
				}
				
				rs.close();
				if(counter==0) {
					//	public Building addBuilding(String type, int lotNum, int lvl, int lvlUp) {
					System.out.println(townID + " is replacing a timber field.");

					addBuilding("Timber Field",1,3,0);
				}
				rs =stmt.executeQuery("select count(*) from bldg where tid = " + townID + " and name = 'Manufactured Materials Plant';");
				 counter = 0;
				if(rs.next()) {
					counter=rs.getInt(1);
				}
				
				rs.close();
				if(counter==0) {
					//	public Building addBuilding(String type, int lotNum, int lvl, int lvlUp) {
					System.out.println(townID + " is replacing a Man Mat Plant.");

					addBuilding("Manufactured Materials Plant",2,3,0);
				}
				rs =stmt.executeQuery("select count(*) from bldg where tid = " + townID + " and name = 'Food Farm';");
				 counter = 0;
				if(rs.next()) {
					counter=rs.getInt(1);
				}
				
				rs.close();
				if(counter==0) {
					//	public Building addBuilding(String type, int lotNum, int lvl, int lvlUp) {
					System.out.println(townID + " is replacing a Food Farm.");

					addBuilding("Food Farm",3,3,0);
				}
				i++;
			}
			
			
			stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }
			
			int i =0;
			while(i<bldg().size()) {
				if(bldg.get(i).getLvlUps()<0) bldg.get(i).setLvlUps(0); // check level up doesn't goto -infinity.
				i++;
			}
		}
	/*synchronized public void giveTown(Raid r, Player incomingPlayer) {
		// player in town1 of r will receive this town, after it is properly converted.
		System.out.println("beginning the giving process.");
		/*
		 *  4. Create the giveTown method
 	To switch a town:
 	BLDGBLOCK
 	 -A town has buildings whose citizens-in-building(including AUs) need to be deleted.
	-Resources do not need to be refunded to the old user for lost building stuff..---CHECK---
	-Bldgserver emptied.---CHECK---
	
 	AU BLOCK(Not necessarily in this order, see block for more specifics.)
 	-A town has AU which need to be emptied and replaced with new AU copies, holding
 	the remains of the invasion raid.---CHECK---
 	 -The incoming attackServer can be kept, I think it's only for facsimiles anyhow.---CHECK---
	-the raids going out may possess support units in the raidSupportAU table.
 	these supportUnits need to be separated from those raids, and put on return raids,
 	as per the recall function protocols.---CHECK---
 	-A town has an attackServer that needs to be emptied, all raids on it need
 	to have their town1's changed to the player's closest town by default and
 	then placed on that attackServer.---CHECK---
 		//6. Return all support raids from this same player enroute to this town.
		// How do we do this? Well, we could just set the raidOver boolean to true!
		// HAHAHAHAH. That's hilarious.---CHECK---

 	PAPERWORK BLOCK
 	-A town has a player pointer.---CHECK---
 	-A town has a playerName object.---CHECK---
 	-Server changes need to be made - the pid needs to be changed on the sql server.---CHECK---
 	-The town object is added to new player's towns and taken from the old.---CHECK---

		 
		//setBeingInvaded(true); // This keeps the player who is giving up
		// the town from incrementing it's resources,
		// checking it's attackserver or building server, and keeps it from having
		// data written about it to the SQL server until this method
		// has finished it's processing!

		UberStatement stmt;
		try {

	      Player p = getPlayer();
	      stmt = p.God.con.createStatement();
	      boolean transacted=false;
	      while(!transacted) {
	    	  try {
	      
	      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.

	      //BUILDING DEALING BLOCK
		int i = 0; // delete all citizens being built and reset all variables.
		Building b; QueueItem q;
		ArrayList<Building> bldg = bldg();
		ArrayList<QueueItem> queue;
		while(i<bldg.size()) {
			 b = bldg.get(i);
			b.setNumLeftToBuild(0);
			b.setTicksToFinish(-1);
			b.setNumLeftToBuild(0);
			b.setTicksLeft(0);
			b.setLvlUps(0);
			b.setDeconstruct(false); 
			getPlayer().setTotalPopulation(
					getPlayer().getTotalPopulation() - b.getPeopleInside()); // because they're losing them.
			// insert resource cap info here when ready.
			if(b.getType().equals("Arms Factory")) {
			int j = 0;
			queue = b.Queue();
			while(j<queue.size()) {
				 q = queue.get(j);
				
				q.deleteMe();
				
				
				
			}
			}
			i++;
		}
		
		// END BUILDING DEALING BLOCK
		
		
		System.out.println("Got past building.");
		// AU DEALING BLOCK
		// 1.all of their actual units on attacks should
		// be sent to their closest town as support. The trick
		// is to craft them as a return raid. All at once, too. Could
		// resend as support but I'd rather it be a "Returning from..."
		//2. Remove their in-town AU and return foreign ones, including raid foreign au.
		// 3. Empty attackServer data structure and db entries now that
		// all in-town and out on raid units have been accounted for...(in town aus
		// die, in town support goes home, out of town support goes home, out of town
		// au reroute to nearest town.)
		//4. Add your AU copies to town's now emptied home and support au,
		// along with unit sizes.
		// 5. Return all supportAU units from your side from the invasion.
		// (You here is the invader, not invadee.)
		//6. Return all support raids from this same player enroute to this town.
		// How do we do this? Well, we could just set the raidOver boolean to true!
		// HAHAHAHAH. That's hilarious.

		
		
	
		
		i = 0; // Emptying all of the attackServer home AUs into
		// a single raid to send to the closest town.
		// support AUs will be dealt with next.
		
		double distance = 999999;// find closestTown to return to.
		Town closestTown=getPlayer().towns().get(0); //main town by default.
		int j = 0; Town t; Raid hr;ArrayList<AttackUnit> retAU; Raid currR; 
		AttackUnit g; Raid retAURaid;ArrayList<Raid> attackServer;
		UberStatement stmt2 = p.God.con.createStatement();
		ArrayList<Town> towns = getPlayer().towns();
		
		ResultSet rs2 = stmt2.executeQuery("select rid from raid where tid2 = " + townID + " and support > 0 and (raidOver = false or ticksToHit >= 0);");
		while(rs2.next()) {
			hr = new Raid(rs2.getInt(1), p.God);
			hr.setRaidOver(true); hr.setTicksToHit(hr.getTicksToHit() + 1);
		}
		rs2.close();
		stmt2.close();
		int x = getX(); int y = getY();
		
		while(j<towns.size()) { // get closest town, and 
			// tell other town's support runs to back off.
			 t = towns.get(j);
			i = 0;
			
			
			if((Math.sqrt(Math.pow((t.getX()-x),2) + Math.pow((t.getY()-y),2)))<distance&&t.townID!=townID) {
				distance = (Math.sqrt(Math.pow((t.getX()-x),2) + Math.pow((t.getY()-y),2)));
				closestTown=t;
			}
			j++;
		}
		i=0;
		retAU = new ArrayList<AttackUnit>();
		ArrayList<AttackUnit> t1au = getAu();
		while(i<6) { // only want the player's own AUs,
			retAU.add(t1au.get(i).returnCopy());
			i++;
		}
		
		i=0;
		attackServer = attackServer();
		ArrayList<AttackUnit> currRAU;
		while(i<attackServer.size()) { // get all home au.
			 currR = attackServer.get(i);
			 j = 0;
			 currRAU = currR.getAu();
			while(j<6) {
				
				retAU.get(j).setSize(retAU.get(j).getSize() + currRAU.get(j).getSize());
				currR.setSize(j,0);
				j++;
			}
			
			i++;
		}
		
	
		//	public Raid(double distance, int ticksToHit, Town town1, Town town2, boolean Genocide, boolean Bomb, int support,boolean invade) {

		int c = 0;
		double lowSpeed = 0;
		double totalsize=0;
		do {
			 g = retAU.get(c);
			//if(g.size>0&&g.speed<lowSpeed) lowSpeed=g.speed;
			lowSpeed+=(g.getSize()*g.getExpmod()*g.getSpeed());
			totalsize+=g.getSize()*g.getExpmod();
			 c++;
		} while(c<retAU.size());
	
		
		if(totalsize>0) {
			lowSpeed/=totalsize;
		 int ticksToHit = (int) Math.round(Math.sqrt(Math.pow((closestTown.getX()-x),2) + Math.pow((closestTown.getY()-y),2))*10/(lowSpeed*GodGenerator.speedadjust));
		// System.out.println(closestTown.townID);
			 retAURaid = new Raid(distance,ticksToHit,closestTown,this,false,false,0,false,"noname");
			retAURaid.setRaidOver(true); // so now it's a "return raid" on the server with full
			// ticks and everything, I "fooled" my own system!.
			i = 0;
			while(i<6) {
				retAURaid.add(retAU.get(i)); // adding all of the units.
				i++; 
			}
		}
	//		System.out.println("RID for sending home units to other cities: "+ retAURaid.raidID);
		
		// remove support AU and then empty the array, including on raids.
		i = 6;
		AttackUnit a; BattlehardFunctions bhf;
		Raid ha;
		ArrayList<AttackUnit> au = getAu();
		while(i<au.size()) {
			 a = au.get(i);
			
				// means a support unit, needs to be sent
				// back to owner.
				
				// we also need to gather all remaining units
				// in forward raids. Let recall do that.
				
				// We'll do the paperwork.
				Player op = a.getOriginalPlayer();
				 bhf = new BattlehardFunctions(op.God,op,"4p5v3sxQ",false,op.getPs());
				//set up a "fake" bfunctions to do recalling for us.
				 distance = 999999;// find closestTown to return to.
				 towns = a.getOriginalPlayer().towns();
				 closestTown=towns.get(0); //main town by default.
				 j = 0;
				while(j<towns.size()) {
					 t = towns.get(j);
					if((Math.sqrt(Math.pow((t.getX()-x),2) + Math.pow((t.getY()-y),2)))<distance) {
						distance = (Math.sqrt(Math.pow((t.getX()-x),2) + Math.pow((t.getY()-y),2)));
						closestTown=t;
					}
					j++;
				}
				bhf.recall(new int[1],townID,getPlayer().ID,closestTown.townID);
				// so have it recall to his closest town by default.
				// need to remove from the supportAU table for the player
				// since soon this player will no longer be in charge and
				// the recall method relies on the player object to
				// do deleting from tables.
				
				// Also relies on player to do deleting from objects, but we'll
				// just not worry about those, we'll empty them from
				// the player when we empty normal ones since their size will
				// be zero and thus harmless!
				
				
				// no need for this when recall does it all!
				/*stmt.executeUpdate("delete from supportAU where tid = "+  townID + " and slotnum = "+ a.getSlot() + ";");
				int k = 0;
				while(k<attackServer.size()) { // so for every raid, there is an entry
					// on raidSupportAU for this attackunit that is a supporter
					// called a and we go through and delete each inference.
					// the recall functions does this to the data structures.
					
		    		 ha = attackServer.get(k);
		    				stmt.executeUpdate("delete from raidSupportAU where tid = " + townID + " and rid = " + ha.raidID + 
		    						" and tidslot = " + a.getSlot() + ";");				  	
		    		  k++;
				}
			i++;
		}
		
		stmt.executeUpdate("delete from supportAU where tid = "+  townID + ";");

		stmt.executeUpdate("delete from raidSupportAU where tid = " + townID + ";"); // CHECKS!
		//stmt.executeUpdate("update town set invaded_at = CURRENT_TIMESTAMP where tid = " + townID + ";");
		// delete all traces of support au for this town...

		i = 0;
		// empty au table now that support has been taken care of, can't
		// do it at the same time because recall needs a full au arraylist to
		// do it's job! Instead empty them then empty here. Though theoretically
		// all support aus are gone but their shells still exist. However,
		// those shells have zero size. 
	/*	while(i<6) {
			 a = au.get(i);
			 removeAU(a.getSlot(),a.getSize());

		}
		
	//System.out.println("Emptied au.");
		// At this point, all home aus in the town have been killed straight off.
		// All foreign aus out on raids or in the town have been sent home.
		// All home aus out on raids have returned to their nearest town
		// on a false raid.
		// The AU arraylist on the town has been emptied of units for replacement.
		// The attackServer can now be emptied as well, though incomingAttackServer
		// does not need to be. 
		// this also means deleting from the database.
		

		// delete from the db.
	//	stmt.executeUpdate("delete from raid where tid1 = " + townID + " and raidOver=false and ticksToHit=>0;");
		
		i = 0;
		while(i<attackServer.size()) {  // delete from memory.
			attackServer.get(i).setRaidOver(true);
			attackServer.get(i).deleteMe();
		i++;
		}
		System.out.println("Killed attack server.");
		
		if(r!=null) {
			i=0;
			
		while(i<6) {
			setMemInt("au"+(i+1),(t1au.get(i).getSize()));
			r.setSize(i,0);
			t1au.get(i).setSize(0);
			
			//System.out.println("New attack unit is " + au.get(i).name);
			i++;
		}
		
		

		// now that your aus have been moved over, take care of raid...
		// send it back if there are support aus on board!!!

		 c = 0;
		 lowSpeed = 0;
		totalsize=0;
		do {
			 g = t1au.get(c);
		
				//if(g.size>0&&g.speed<lowSpeed) lowSpeed=g.speed;
				lowSpeed+=(g.getSize()*g.getExpmod()*g.getSpeed());
				totalsize+=g.getSize()*g.getExpmod();
			c++;
		} while(c<t1au.size());
		if(totalsize<=0) {
			r.setRaidOver(true); // no need to reset ticksToHit, why return a ghostparty?
			r.deleteMe(); // no longer needed in memory, release it.
			
		} else {
			lowSpeed/=totalsize;
			int testhold = (int) Math.round(Math.sqrt(Math.pow((x-r.getTown1().getX()),2)+Math.pow((y-r.getTown1().getY()),2))*10/(lowSpeed*GodGenerator.speedadjust));
			r.setRaidOver(true);
			r.setTicksToHit(testhold); // sending back supportAUs!
		}
		
		} else {
			/*i=0;
			while(i<6) {
				 a = incomingPlayer.getAu().get(i).returnCopy();
				a.setSize(0);
				
				
				au.add(a); // making a new attack unit set!
				//System.out.println("New attack unit is " + au.get(i).name);
				i++;
			}
		}

		// END AU DEALING BLOCK
		System.out.println("And AU...");
		// PAPERWORK DEALING BLOCK
		/* i = 0;// remove town from current player's towns list.
			while(i<getPlayer().towns.size()) {
				if(getPlayer().towns.get(i).townID==townID) {getPlayer().towns.remove(i); break; }
				i++;
			}
			//player.callSync=true; // telling the playerside player to synchronize...
		
			setMemInt("pid",incomingPlayer.ID);
	//	addPlayer(incomingPlayer); // town1 is taking town2. Reset player.
		// this takes care of both playerName and the player object being loaded!
		
	//	getPlayer().towns.add(this); // add to new player's town array.
		
	//	stmt.executeUpdate("update town set pid = " + getPlayer().ID + " where tid = " + townID + ";");
		// change recorded pid to new player's.
		
		// END PAPERWORK DEALING BLOCK
	
		//...and now we're done giving the town over.

		
			stmt.execute("commit;"); transacted=true; } catch(MySQLTransactionRollbackException exc) { }
	      }
			
			System.out.println("Committing to memory...");
		//	r.getTown1().player.callSync=true; // now telling the happy new owner of the town
			// to synchronize! Using different call signal other than player again
			// to avoid confusion.
			
		//	setBeingInvaded(false);
			stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }

		
		
		
	}
	*/
		synchronized public void giveTown(Raid r, Player incomingPlayer) {
			// player in town1 of r will receive this town, after it is properly converted.
			/*
			 *  4. Create the giveTown method
	 	To switch a town:
	 	BLDGBLOCK
	 	 -A town has buildings whose citizens-in-building(including AUs) need to be deleted.
		-Resources do not need to be refunded to the old user for lost building stuff..---CHECK---
		-Bldgserver emptied.---CHECK---
		
	 	AU BLOCK(Not necessarily in this order, see block for more specifics.)
	 	-A town has AU which need to be emptied and replaced with new AU copies, holding
	 	the remains of the invasion raid.---CHECK---
	 	 -The incoming attackServer can be kept, I think it's only for facsimiles anyhow.---CHECK---
		-the raids going out may possess support units in the raidSupportAU table.
	 	these supportUnits need to be separated from those raids, and put on return raids,
	 	as per the recall function protocols.---CHECK---
	 	-A town has an attackServer that needs to be emptied, all raids on it need
	 	to have their town1's changed to the player's closest town by default and
	 	then placed on that attackServer.---CHECK---
	 		//6. Return all support raids from this same player enroute to this town.
			// How do we do this? Well, we could just set the raidOver boolean to true!
			// HAHAHAHAH. That's hilarious.---CHECK---

	 	PAPERWORK BLOCK
	 	-A town has a player pointer.---CHECK---
	 	-A town has a playerName object.---CHECK---
	 	-Server changes need to be made - the pid needs to be changed on the sql server.---CHECK---
	 	-The town object is added to new player's towns and taken from the old.---CHECK---

			 */

			
			try {

		
		     UberStatement stmt = con.createStatement();
		      boolean transacted=false;
		      while(!transacted) {
		    	  try {
		      
		      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
		      Player player = getPlayer();

		      //BUILDING DEALING BLOCK
			int i = 0; // delete all citizens being built and reset all variables.
			Building b; QueueItem q;
			ArrayList<Building> bldg = bldg();
			while(i<bldg.size()) {
				 b = bldg.get(i);
				b.setNumLeftToBuild(0);
				b.setTicksToFinish(-1);
				b.setTicksLeft(0);
				b.setLvlUps(0);
				b.setDeconstruct(false); 
				player.setTotalPopulation(player.getTotalPopulation()-b.getPeopleInside()); // because they're losing them.
				// insert resource cap info here when ready.
				if(b.getType().equals("Arms Factory")) {
				int j = 0;
				while(j<b.Queue().size()) {
					 q = b.Queue().get(j);
					q.deleteMe();
					stmt.executeUpdate("delete from queue where qid = " + q.qid);
					
					
				}
				}
				i++;
			}
			
			// END BUILDING DEALING BLOCK
			
			
		//	System.out.println("Got past building.");
			// AU DEALING BLOCK
			// 1.all of their actual units on attacks should
			// be sent to their closest town as support. The trick
			// is to craft them as a return raid. All at once, too. Could
			// resend as support but I'd rather it be a "Returning from..."
			//2. Remove their in-town AU and return foreign ones, including raid foreign au.
			// 3. Empty attackServer data structure and db entries now that
			// all in-town and out on raid units have been accounted for...(in town aus
			// die, in town support goes home, out of town support goes home, out of town
			// au reroute to nearest town.)
			//4. Add your AU copies to town's now emptied home and support au,
			// along with unit sizes.
			// 5. Return all supportAU units from your side from the invasion.
			// (You here is the invader, not invadee.)
			//6. Return all support raids from this same player enroute to this town.
			// How do we do this? Well, we could just set the raidOver boolean to true!
			// HAHAHAHAH. That's hilarious.

			
			
		
			
			i = 0; // Emptying all of the attackServer home AUs into
			// a single raid to send to the closest town.
			// support AUs will be dealt with next.
			
			double distance = 999999;// find closestTown to return to.
			Town closestTown=player.towns().get(0); //main town by default.
			int j = 0; Town t; Raid hr;ArrayList<AttackUnit> retAU; Raid currR; 
			AttackUnit g; Raid retAURaid;
			while(j<player.towns().size()) { // get closest town, and 
				// tell other town's support runs to back off.
				 t = player.towns().get(j);
				i = 0;
				if(t.townID!=townID)
				while(i<t.attackServer().size()) {
					 hr = t.attackServer().get(i);
					if(hr.getSupport()>0&&hr.getTown2().townID==townID) { hr.setRaidOver(true); hr.setTicksToHit(hr.getTicksToHit()+1); }
						// support units have support>0, and support raids
						// do also, but home units on support raids to other home cities
						// do not. This would be problematic when the supportLogicBlock
						// sees them as home units when post invasion they are really not!
						// So we tell these raids to go home with raidOver=true.
					// Also just in case they are 1 second away, and are on the attackServer
					// waiting to be...processed, we increase their ticksToHit by one hopefully getting
					// them out. Also we put support logic protections on the support logic block
					// too. Go there to check them out. Those protections are if they are being
					// processed at the same instant that these invasion protocols are.
					i++;
				}
				
				
				if((Math.sqrt((t.x-x)*(t.x-x) + (t.y-y)*(t.y-y)))<distance&&t.townID!=townID) {
					distance = Math.sqrt(((t.x-x)*(t.x-x) + (t.y-y)*(t.y-y)));
					closestTown=t;
				}
				j++;
			}
			i=0;
			retAU = new ArrayList<AttackUnit>();
			ArrayList<AttackUnit> au = getAu();
			while(i<6) { // only want the player's own AUs,
				retAU.add(au.get(i).returnCopy());
				i++;
			}
			
			i=0;
			while(i<attackServer().size()) { // get all home au.
				 currR = attackServer().get(i);
				 j = 0;
				while(j<6) {
					
					retAU.get(j).setSize(retAU.get(j).getSize()+currR.getAu().get(j).getSize());
					currR.getAu().get(j).setSize(0);
					
					j++;
				}
				currR.deleteMe();
				i++;
			}
			

			//	public Raid(double distance, int ticksToHit, Town town1, Town town2, boolean Genocide, boolean Bomb, int support,boolean invade) {
			 double lowSpeed = 0;
				double totalsize=0;
				int c=0;
				do {
					 g = retAU.get(c);
				
						//if(g.size>0&&g.speed<lowSpeed) lowSpeed=g.speed;
						lowSpeed+=(g.getSize()*g.getExpmod()*g.getSpeed());
						totalsize+=g.getSize()*g.getExpmod();
					c++;
				} while(c<retAU.size());
				if(totalsize>0) {
				
					//	public Raid(double distance, int ticksToHit, Town town1, Town town2, boolean Genocide, boolean Bomb, int support,boolean invade, String name) {

					lowSpeed/=totalsize;
					int testhold = (int) Math.round(Math.sqrt(Math.pow((x-closestTown.getX()),2)+Math.pow((y-closestTown.getY()),2))*10/(lowSpeed*GodGenerator.speedadjust));
					retAURaid = new Raid(distance,testhold,closestTown,this,false,false,0,false,"Run Away!",false);
					retAURaid.setRaidOver(true); // so now it's a "return raid" on the server with full
					i = 0;
					while(i<6) {
						retAURaid.add(retAU.get(i)); // adding all of the units.
						i++; 
					}
				}
			
			
			 
				// ticks and everything, I "fooled" my own system!.
			
				
			
			// remove support AU and then empty the array, including on raids.
			i = 6;
			AttackUnit a; BattlehardFunctions bhf;
			Raid ha;

			while(i<au.size()) {
				 a = au.get(i);
				
					// means a support unit, needs to be sent
					// back to owner.
					
					// we also need to gather all remaining units
					// in forward raids. Let recall do that.
					
					// We'll do the paperwork.
					
					 bhf = a.getOriginalPlayer().getPs().b;
					//set up a "fake" bfunctions to do recalling for us.
					 distance = 999999;// find closestTown to return to.
					 closestTown=a.getOriginalPlayer().towns().get(0); //main town by default.
					 j = 0;
					while(j<a.getOriginalPlayer().towns().size()) {
						 t = a.getOriginalPlayer().towns().get(j);
						if((Math.sqrt((t.x-x)*(t.x-x) + (t.y-y)*(t.y-y)))<distance) {
							distance = Math.sqrt(((t.x-x)*(t.x-x) + (t.y-y)*(t.y-y)));
							closestTown=t;
						}
						j++;
					}
					bhf.recall(townID,player.ID,closestTown.townID);
					// so have it recall to his closest town by default.
					// need to remove from the supportAU table for the player
					// since soon this player will no longer be in charge and
					// the recall method relies on the player object to
					// do deleting from tables.
					
					// Also relies on player to do deleting from objects, but we'll
					// just not worry about those, we'll empty them from
					// the player when we empty normal ones since their size will
					// be zero and thus harmless!
					
					
					
					stmt.executeUpdate("delete from supportAU where tid = "+  townID + " and slotnum = "+ a.getSlot() + ";");
					int k = 0;
					while(k<attackServer().size()) { // so for every raid, there is an entry
						// on raidSupportAU for this attackunit that is a supporter
						// called a and we go through and delete each inference.
						// the recall functions does this to the data structures.
						
			    		 ha = attackServer().get(k);
			    				stmt.executeUpdate("delete from raidSupportAU where tid = " + townID + " and rid = " + ha.raidID + 
			    						" and tidslot = " + a.getSlot() + ";");				  	
			    		  k++;
					}
				i++;
			}
			stmt.executeUpdate("delete from supportAU where tid = "+  townID + ";");

			stmt.executeUpdate("delete from raidSupportAU where tid = " + townID + ";");
			//stmt.executeUpdate("update town set invaded_at = CURRENT_TIMESTAMP where tid = " + townID + ";");
			// delete all traces of support au for this town...

			i = 0;
			// empty au table now that support has been taken care of, can't
			// do it at the same time because recall needs a full au arraylist to
			// do it's job! Instead empty them then empty here. Though theoretically
			// all support aus are gone but their shells still exist. However,
			// those shells have zero size. 
			while(i<au.size()) {
				 a = au.get(i);
					au.remove(i);
					player.setTotalPopulation(player.getTotalPopulation()-a.getSize()*a.getPopSize());

			}
			
			//System.out.println("Emptied au.");
			// At this point, all home aus in the town have been killed straight off.
			// All foreign aus out on raids or in the town have been sent home.
			// All home aus out on raids have returned to their nearest town
			// on a false raid.
			// The AU arraylist on the town has been emptied of units for replacement.
			// The attackServer can now be emptied as well, though incomingAttackServer
			// does not need to be. 
			// this also means deleting from the database.
			

			// delete from the db.
			stmt.executeUpdate("delete from raid where tid1 = " + townID + " and raidOver=false and ticksToHit>0;");
			
			i = 0;
			while(i<attackServer().size()) {  // delete from memory.
				attackServer().remove(i);
			
			}
		//	System.out.println("Killed attack server.");
			ArrayList<AttackUnit> newAU = incomingPlayer.getAu();
			i=0;
			while(i<6) {
				 a =newAU.get(i).returnCopy();
				 if(r!=null) {
				a.setSize(r.getAu().get(i).getSize());
				 
				r.getAu().get(i).setSize(0);
				 }
				
				au.add(a); // making a new attack unit set!
				//System.out.println("New attack unit is " + au.get(i).getName());
				i++;
			}
			
			if(r!=null) {
			
			
			

			// now that your aus have been moved over, take care of raid...
			// send it back if there are support aus on board!!!
			
			  lowSpeed = 0;
				 totalsize=0;
				 c=0;
				do {
					 g = r.getAu().get(c);
				
						//if(g.size>0&&g.speed<lowSpeed) lowSpeed=g.speed;
						lowSpeed+=(g.getSize()*g.getExpmod()*g.getSpeed());
						totalsize+=g.getSize()*g.getExpmod();
					c++;
				} while(c<r.getAu().size());
				
				
			if(lowSpeed==0) {
				r.setRaidOver( true); // no need to reset ticksToHit, why return a ghostparty?
				r.deleteMe(); // no longer needed in memory, release it.
				
			} else {
				int testhold = (int) Math.round(Math.sqrt(Math.pow((r.getTown2().getX()-r.getTown1().getX()),2)+Math.pow((r.getTown2().getY()-r.getTown1().getY()),2))*10/(lowSpeed*GodGenerator.speedadjust));
				r.setRaidOver(true);
				r.setTicksToHit(testhold); // sending back supportAUs!
			}
			
			
			}
			// END AU DEALING BLOCK
			//System.out.println("And AU...");
			// PAPERWORK DEALING BLOCK
			 i = 0;// remove town from current player's towns list.
			
			p.removeTown(this);
	
			//System.out.println("Town removed from  "+ p.getUsername() + " who's towns now have size " + p + " with user " + p.getUsername());
		/*		while(i<p.towns().size()) {
					if(p.towns().get(i).townID==townID) {System.out.println("Found it in Id's list."); break; }
					i++;
				}
			*/
			this.p = (incomingPlayer); // town1 is taking town2. Reset player.

			// this takes care of both playerName and the player object being loaded!
			
			incomingPlayer.addTown(this); // add to new player's town array.
			//System.out.println("Town added to "+ p.getUsername() + " who now has town size of " + p.towns().size());

			stmt.executeUpdate("update town set pid = " + p.ID + " where tid = " + townID + ";");
			// change recorded pid to new player's.
			
			// END PAPERWORK DEALING BLOCK
		
			//...and now we're done giving the town over.

			
				stmt.execute("commit;"); transacted=true; } catch(MySQLTransactionRollbackException exc) { }
		      }
				
				System.out.println("Committing to memory...");
				// to synchronize! Using different call signal other than player again
				// to avoid confusion.
				
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }
			
		}

	public void deleteTown() {
		getPlayer().God.giveNewTown(getPlayer(),townID,0,true,0,0);

		// so the town has been washed of support units..
		
		// now we can delete this town.
		/*
		 * Trade Schedules, Trades, and incoming Raids must all actually
		 * be erased in the giveTown method for this town to eventually
		 * disappear from memory. Well, you can leave Trades and Raids,
		 * but not Trade Schedules. Those gotta go for this town to disappear
		 * like a bad memory. Just add this into the giveTown method.
		 */
		int i = 0;try {
		ArrayList<Building> bldg = bldg();
		while(i<bldg.size()) {
			killBuilding(bldg.get(i).bid); // first we knock the buildings.
		}
		} catch(Exception exc) { } 
		 i = 0;
			ArrayList<Town> towns = God.getTowns();
			ArrayList<TradeSchedule> ts;
			ArrayList<Raid> as;
			while(i<towns.size()) {
				ts = towns.get(i).tradeSchedules();
				as = towns.get(i).attackServer();
				int j = 0;
				try {
				while(j<ts.size()) {
					if(ts.get(j).getTown2().townID==townID) {
						ts.get(j).deleteMeInterrupt();
						
					}
					j++;
				}
				} catch(Exception exc) { }
				
				 j = 0;
				 try {
				while(j<as.size()) {
					if(as.get(j).getTown2().townID==townID) {
						as.get(j).setRaidOver(true);
						as.get(j).setTicksToHit(as.get(j).getTotalTicks()-as.get(j).getTicksToHit());
						
					}
					j++;
				}
				 } catch(Exception exc)  { }
				i++;
			}

		try {
			UberStatement stmt = getPlayer().con.createStatement();
			boolean transacted=false;
			while(!transacted) {
			
			try {
				synchronized(this) {
					God.getTowns().remove(this);
					getPlayer().towns().remove(this);
					

			stmt.execute("start transaction");
			stmt.execute("delete from trade where tid1 = " + townID);
			stmt.execute("delete from tradeschedule where tid1 = " + townID);
			stmt.execute("delete from raid where tid1 = " + townID);
			stmt.execute("update raid set raidOver=true,ticksToHit=totalTicks-ticksToHit where tid2 = " + townID);

			stmt.execute("delete from bldg where tid = " + townID); // delete from buildings.
			stmt.execute("delete from invadable where tid = " +townID);

			stmt.execute("delete from town where tid = " + townID); // delete from towns.
			stmt.execute("commit;");

			
			transacted=true;
			stmt.close();
				}
			} catch(MySQLTransactionRollbackException exc) { } 
			
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Raid> attackServer() {
		if(attackServer==null) {
		UberStatement rus; ArrayList<Raid> r = new ArrayList<Raid>();
		UberConnection con = getPlayer().con;
	
		ResultSet rrs;
		
			
			try {
	
			
			
		
		 rus = con.createStatement();
		
			 rrs = rus.executeQuery("select rid from raid where tid1 = " + townID + " and (raidOver = false or ticksToHit >= 0)");

	
		while(rrs.next()) {
			r.add(new Raid(rrs.getInt(1),getPlayer().God));
			
		}
		
		rrs.close();
		rus.close();
			
			
			attackServer=r;
			
	} catch(SQLException exc) { exc.printStackTrace(); }
		}
	return attackServer;
		
	
	

	}
	
	
	public ArrayList<TradeSchedule> tradeSchedules() {
		if(tradeSchedules==null) {
		ArrayList<TradeSchedule> tses = new ArrayList<TradeSchedule>();
		try {
		UberStatement rus = getPlayer().con.createStatement();
			
		ResultSet rrs = rus.executeQuery("select tsid from tradeschedule where tid1 = " + townID + " and finished = false");
		// so we don't want it to load if raidOver is true and ticksToHit is 0. Assume 0 is not, 1 is on, for ttH. !F = R!T
		// Then F = !(R!T) = !R + T;
		while(rrs.next()) {
	
			tses.add(new TradeSchedule(rrs.getInt(1),getPlayer().God));
	
		}
				rrs.close();rus.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		tradeSchedules=tses;
		}
		return tradeSchedules;
		
		
	}
	public ArrayList<Trade> tradeServer() {
		if(tradeServer==null) {
		ArrayList<Trade> tres = new ArrayList<Trade>();
		try {
		UberStatement rus = getPlayer().con.createStatement();
			
		ResultSet rrs = rus.executeQuery("select trid from trade where tid1 = " + townID + " and (tradeOver = false or ticksToHit>=0)");
		// so we don't want it to load if raidOver is true and ticksToHit is 0. Assume 0 is not, 1 is on, for ttH. !F = R!T
		// Then F = !(R!T) = !R + T;
		while(rrs.next()) {
	
			tres.add(new Trade(rrs.getInt(1),getPlayer().God));
	
		}
				rrs.close();rus.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		tradeServer=tres;
		}
		return tradeServer;
		
		
	}
	public ArrayList<Building> bldg() {
		if(bldg==null) {
		ArrayList<Building> tres = new ArrayList<Building>();
		try {
		UberStatement rus = getPlayer().con.createStatement();
			
		ResultSet rrs = rus.executeQuery("select bid from bldg where tid = " + townID + ";");
		// so we don't want it to load if raidOver is true and ticksToHit is 0. Assume 0 is not, 1 is on, for ttH. !F = R!T
		// Then F = !(R!T) = !R + T;
		while(rrs.next()) {
	
			tres.add(new Building(rrs.getInt(1),getPlayer().God));
	
		}
				rrs.close();rus.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		
		bldg= tres;
		}
		return bldg;
	}
	
	public ArrayList<Building> bldgserver() {
		
		ArrayList<Building> tres = new ArrayList<Building>();
		ArrayList<Building> bldg = bldg();

		int i = 0;
		while(i<bldg.size()) {
			if(getPlayer().getUsername().contains("testman")) System.out.println("I want to be Putting a " + bldg.get(i).getType() + " on this bitch but it's lvlUps is " +bldg().get(i).getLvlUps());
			if(bldg.get(i).getNumLeftToBuild()>0||bldg.get(i).getLvlUps()>0||bldg.get(i).Queue().size()>0) {tres.add(bldg.get(i));
			if(getPlayer().getUsername().contains("testman")) System.out.println("Putting a " + bldg.get(i).getType() + " on this bitch.");}
			i++;
		}
		return tres;
		/*
		ArrayList<Building> tres = new ArrayList<Building>();
		try {
		UberStatement rus = getPlayer().con.createStatement();
			
		ResultSet rrs = rus.executeQuery("select bid from bldg where tid = " + townID + " and (pplbuild>0 or lvlUp>0);");
		// so we don't want it to load if raidOver is true and ticksToHit is 0. Assume 0 is not, 1 is on, for ttH. !F = R!T
		// Then F = !(R!T) = !R + T;
		while(rrs.next()) {
	
			tres.add(new Building(rrs.getInt(1),getPlayer().God));
	
		}
				rrs.close();rus.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		
		return tres;*/
	}
	public Raid findRaid(int rid) {
		int i =0;
		
		while(i<attackServer().size()) {
			if(attackServer().get(i).raidID==rid) return attackServer().get(i);
			i++;
		}
		return null;
	}
	public void auCheck() {
		int i = 0;
		int ie = 0;int totalCheckedSize=0;
		Raid r;

		while(i<attackServer().size()) {
			r = attackServer().get(i);
			 ie = 0;
			 totalCheckedSize=0;
				while(ie<r.getAu().size()) {
					totalCheckedSize+=r.getAu().get(ie).getSize();
					// SuggestTitleVideoId
					ie++;
				}
				if(totalCheckedSize==0&&!r.isRaidOver()) { // if raid is over, AU has been loaded 
					// and checked in combat logic.
					// this means we called getAu() for the first time before the au statements got to update and put
					// the units into the raid!
					r.setAu(null);
					r.getAu(); // reset.
				}
			i++;
		}
		 i = 0;
		Town t;
		ArrayList<AttackUnit> au;
		ArrayList<Raid> as;
		AttackUnit a;
		AttackUnit ourA;
		int x = 6;
		ArrayList<AttackUnit> ourAU = getAu();
		while(x<ourAU.size()) {
			ourA = ourAU.get(x);
				if(ourA.getSize()==0) {
					
					as = attackServer();
					int j = 0;
					boolean foundAU = false;
					while(j<as.size()) {
						r = as.get(j);
						au = r.getAu();
						int k = 6;
						while(k<au.size()) {
							a = au.get(k);
							if(a.getSupport()>0&&a.getSlot()==ourA.getSlot()&&a.getSize()>0) {
								foundAU = true;
								break;
							}
							k++;
						}
						j++;
					}
					
					if(!foundAU) {
						try {
							UberStatement stmt2 = con.createStatement();
							stmt2.executeUpdate("delete from supportAU where slotnum = " + ourA.getSlot() + " and tid = " + townID);
							stmt2.close();
							// who cares if raidsupportau has zero units.
						} catch(SQLException exc) { exc.printStackTrace(); }
						ourAU.remove(x);
						x--;
					}
				}
			x++;
		}
		
		/*
		try {
			UberStatement stmt = con.createStatement();
			UberStatement stmt2 = con.createStatement(); ResultSet rs2;
			ResultSet rs = 	stmt.executeQuery("select size,slotnum from supportAU where tid = " + townID);
			while(rs.next()) {
				if(rs.getInt(1)==0) {
				int rausize=0;
				 rs2 = stmt2.executeQuery("select sum(size) from raidSupportAU where tid = " + townID + " and tidslot = " + rs.getInt(2));
				if(rs2.next()) rausize=rs2.getInt(1);
				rs2.close();
				if(rausize==0){
					System.out.println("Deleting something...");
					stmt2.executeUpdate("delete from supportAU where slotnum = " + rs.getInt(2) + " and tid = " + townID);
				}
				}
			}
			rs.close();
			stmt2.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); System.out.println("Your shit is a-okay."); } */
	
	}
	public Trade findTrade(int trid) {
		int i = 0;
		while(i<tradeServer().size()) {
			if(tradeServer().get(i).tradeID==trid) return tradeServer().get(i);
			i++;
		}
		return null;
	}
	public TradeSchedule findTradeSchedule(int trid) {
		int i = 0;
		while(i<tradeSchedules().size()) {
			if(tradeSchedules().get(i).tradeScheduleID==trid) return tradeSchedules().get(i);
			i++;
		}
		return null;
	}
	public int getTownID() {
		return townID;
	}

	public void setTownID(int townID) {
		this.townID = townID;
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

	

	public long[] getRes() {
		if(res==null) res = getMemRes();
		return res;
	}

	public void setRes(long[] res) {
		this.res = res;
	}

	public long[] getResCaps() {
		
		long resCaps[] = new long[5];
		int i = 0;
		ArrayList<Building> bldg = bldg();
		Building b;
		if(getPlayer().ID ==5 ) { // Id's go based on mines.
			while(i<bldg.size()) {
				b = bldg.get(i);
				if(b.getType().equals("Metal Mine")) {
					resCaps[0]+=(long) Building.getCap(b.getLvl()-1,true);
				} else if(b.getType().equals("Timber Field")) {
					resCaps[1]+=(long) Building.getCap(b.getLvl()-1,true);

				}else if(b.getType().equals("Manufactured Materials Plant")) {
					resCaps[2]+=(long) Building.getCap(b.getLvl()-1,true);

				}else if(b.getType().equals("Food Farm")) {
					resCaps[3]+=(long) Building.getCap(b.getLvl()-1,true);

				}
				i++;
			}
		} else {
		while(i<bldg.size()) {
			b = bldg.get(i);
			if(b.getType().equals("Metal Warehouse")) {
				resCaps[0]+=(long) Building.getCap(b.getLvl(),true);
			} else if(b.getType().equals("Timber Warehouse")) {
				resCaps[1]+=(long) Building.getCap(b.getLvl(),true);

			}else if(b.getType().equals("Manufactured Materials Warehouse")) {
				resCaps[2]+=(long) Building.getCap(b.getLvl(),true);

			}else if(b.getType().equals("Food Warehouse")) {
				resCaps[3]+=(long) Building.getCap(b.getLvl(),true);

			}
			i++;
		}
		}
		
		if(getPlayer().isLeague()) {
		 i = 0;
		ArrayList<TaxPlayerRank> tpr = ((League) p).tpr();
		TaxPlayerRank curr;
		ArrayList<Town> towns;
		while(i<tpr.size()) {
			curr = tpr.get(i);
			if(curr.type>=0) {
			Player pl = curr.player;
			towns = pl.towns();
			int j = 0;
			while(j<towns.size()) {
				
				bldg = towns.get(j).bldg();
				int k = 0;
				
				while(k<bldg.size()) {
					b = bldg.get(k);
					if(b.getType().equals("Communications Center")) {
						int x = 0;
						while(x<resCaps.length-1) {
							resCaps[x]+=(long) Math.round(curr.taxRate*((double) Building.getCap(b.getLvl(),true)*(1+.05*(pl.getCommsCenterTech()-1))));
							x++;
						}
					}
					k++;
				}
				j++;
			
				
			}
			}
		i++;
		}
		
		}
		
		return resCaps;
	}
	
/*
 * 	long resCaps[] = new long[5];
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select sum(pow(lvl+2,2)) from bldg where tid = " + townID + " and name = 'Metal Warehouse';");
			if(rs.next()) resCaps[0]=(long) Math.ceil(Building.resourceAmt*rs.getLong(1));
			rs.close();
			
			rs = stmt.executeQuery("select sum(pow(lvl+2,2)) from bldg where tid = " + townID + " and name = 'Timber Warehouse';");
			if(rs.next()) resCaps[1]=(long) Math.ceil(Building.resourceAmt*rs.getLong(1));
			rs.close();
			
			rs = stmt.executeQuery("select sum(pow(lvl+2,2))from bldg where tid = " + townID + " and name = 'Manufactured Materials Warehouse';");
			if(rs.next()) resCaps[2]=(long) Math.ceil(Building.resourceAmt*rs.getLong(1));
			rs.close();
			
			rs = stmt.executeQuery("select sum(pow(lvl+2,2)) from bldg where tid = " + townID + " and name = 'Food Warehouse';");
			if(rs.next()) resCaps[3]=(long) Math.ceil(Building.resourceAmt*rs.getLong(1));
			rs.close();
			
			stmt.close();
			Player pl = getMemPlayer();
			if(pl.isLeague()) {
				League p = (League) getMemPlayer();
				UberStatement stmt2 = God.con.createStatement();
				ResultSet getMemT; TaxPlayerRank curr;
				ResultSet getMemB; UberStatement bstmt = God.con.createStatement();
				ResultSet getMemC; UberStatement cstmt = God.con.createStatement();

				Town t;
				ArrayList<Town> towns = p.towns();

				int i = 0;
				ArrayList<TaxPlayerRank> tpr = ((League) p).tpr();
				while(i<tpr.size()) {
					curr = tpr.get(i);
				getMemT = stmt2.executeQuery("select tid from town where pid = " + curr.pid);
				getMemC = cstmt.executeQuery("select commsCenterTech from player where pid = " +curr.pid);
				int commsCenterTech=1;
				if(getMemC.next())
				 commsCenterTech = getMemC.getInt(1);
				getMemC.close();
				while(getMemT.next()) {
					
					getMemB = bstmt.executeQuery("select lvl from bldg where tid = " + getMemT.getInt(1) + " and name = 'Communications Center';");
					int amt=0;
					while(getMemB.next()) {
						amt+=(long) Math.ceil(Building.resourceAmt*Math.pow(getMemB.getInt(1)+2,2)*(1+.05*(commsCenterTech-1)));
					}
					getMemB.close();
					int k = 0;
						while(k<resCaps.length-1) {
							resCaps[k]+=amt;
							k++;
						}
	
				}
				getMemT.close();
				i++;
				}
				stmt2.close(); bstmt.close();
			}
			
			
			return resCaps;
		} catch(SQLException exc) { exc.printStackTrace(); }
		return resCaps;	
 */
	
	

	public double[] getResInc() {
		double[] resIncs= new double[5];
		int i = 0;
		ArrayList<Building> bldg = bldg();
		Building b;
		
		double[] additions = new double[5];
		while(i<bldg.size()) {
			b = bldg.get(i);
			if(b.getType().equals("Metal Refinery")) {
				additions[0]+=b.getLvl()*.05;
			}else if(b.getType().equals("Timber Processing Plant")) {
				additions[1]+=b.getLvl()*.05;
			}else if(b.getType().equals("Materials Research Center")) {
				additions[2]+=b.getLvl()*.05;
			}else if(b.getType().equals("Hydroponics Lab")) {
				additions[3]+=b.getLvl()*.05;
			}
			i++;
		}
		i = 0;
		
		
		if(!getPlayer().isLeague()&&!isZeppelin())
		while(i<bldg.size()) {
			b = bldg.get(i);
			if(b.getType().equals("Metal Mine")) {
				resIncs[0]=((double) GodGenerator.gameClockFactor)*((double) Town.baseResourceGrowthRate)*Math.pow(b.getLvl()+1,2)/3600;
				resIncs[0]*=(1+additions[0]);
				if(getPlayer().getMineTimer()>0) resIncs[0]*=1.25;
			} else if(b.getType().equals("Timber Field")) {
				resIncs[1]=((double) GodGenerator.gameClockFactor)*((double) Town.baseResourceGrowthRate)*Math.pow(b.getLvl()+1,2)/3600;
				resIncs[1]*=(1+additions[1]);

				if(getPlayer().getTimberTimer()>0) resIncs[1]*=1.25;

			}else if(b.getType().equals("Manufactured Materials Plant")) {
				resIncs[2]=((double) GodGenerator.gameClockFactor)*((double) Town.baseResourceGrowthRate)*Math.pow(b.getLvl()+1,2)/3600;
				resIncs[2]*=(1+additions[2]);

				if(getPlayer().getMmTimer()>0) resIncs[2]*=1.25;

			}else if(b.getType().equals("Food Farm")) {
				resIncs[3]=((double) GodGenerator.gameClockFactor)*((double) Town.baseResourceGrowthRate)*Math.pow(b.getLvl()+1,2)/3600;
				resIncs[3]*=(1+additions[3]);

				if(getPlayer().getFTimer()>0) resIncs[3]*=1.25;

			}
			i++;
		}
		if(getPlayer().getPremiumTimer()>0) {
		i=0;
		while(i<resIncs.length) {
			resIncs[i]*=.75;
			i++;
		}
		}
		return resIncs;
			}

	
	public double[] getResEffects() {
		if(resEffects==null) resEffects=getMemResEffects();
		return resEffects;
	}

	public void setResEffects(double[] resEffects) {
		this.resEffects = resEffects;
	}

	public double[] getResBuff() {
		if(resBuff==null) resBuff = getMemResBuff();
		return resBuff;
	}

	public void setResBuff(double[] resBuff) {
		this.resBuff = resBuff;
	}

	public Player getPlayer() {
		if(p==null) {
			p = God.getPlayer(getMemInt("pid"));
		}
		return p;
	}

	public void setPlayer(Player p) {
		this.p = p;
	
	}

	public ArrayList<Trade> getTradeServer() {
		return tradeServer;
	}

	public void setTradeServer(ArrayList<Trade> tradeServer) {
		this.tradeServer = tradeServer;
	}

	public ArrayList<TradeSchedule> getTradeSchedules() {
		return tradeSchedules;
	}

	public void setTradeSchedules(ArrayList<TradeSchedule> tradeSchedules) {
		this.tradeSchedules = tradeSchedules;
	}

	public ArrayList<Raid> getAttackServer() {
		return attackServer;
	}

	public void setAttackServer(ArrayList<Raid> attackServer) {
		this.attackServer = attackServer;
	}

	public String getTownName() {
		if(townName==null) townName = getMemTownName();
		return townName;
	}

	public void setTownName(String townName) {
		this.townName = townName;
	}

	public int getTotalTraders() {
		

		ArrayList<Building> bldg = bldg();
		int i = 0; int totalEngineers=0;
		while(i<bldg.size()) {
			if(bldg.get(i).getType().equals("Trade Center"))
			totalEngineers+=bldg.get(i).getPeopleInside();
			i++;
		}
		return totalEngineers;
	}

	

	public int getFuelCells() {
		return fuelCells;
	}

	public void setFuelCells(int fuelCells) {
		this.fuelCells = fuelCells;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getHoldingIteratorID() {
		return holdingIteratorID;
	}

	public int getInternalClock() {
		return internalClock;
	}

	public void setAu(ArrayList<AttackUnit> au) {
		this.au = au;
	}

	public void setMemSize(int index, int size) {
		if(index<6) setMemInt("au"+(index+1),size);
		else {
			try {
				UberStatement stmt = con.createStatement();
				AttackUnit a = getAu().get(index);
			
				stmt.executeUpdate("update supportAU setMem size = " +size + " where slotnum = " +a.getSlot() +  " and tid = " + townID);
				
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); System.out.println("Your shit is a-okay."); } 
		}
	}
	
	public void setMemPlayer(Player player) {
		setMemInt("pid",player.ID);
	}
	public Player getMemPlayer() {
		return God.getPlayer(getMemInt("pid"));
	}
	public void setMemResBuff(double a[]) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update town setMem mbuff = " + a[0] + ", tbuff = " + a[1] + ", mmbuff = " + a[2] + ", fbuff = " + a[3] + " where tid = " + townID);
			stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }
	}
	public double[] getMemResBuff() {
		double resBuff[] = new double[5];
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select mbuff,tbuff,mmbuff,fbuff from town where tid = "+ townID);
			if(rs.next()) {
	
				resBuff[0]=rs.getDouble(1);
				resBuff[1]=rs.getDouble(2);
				resBuff[2]=rs.getDouble(3);
				resBuff[3]=rs.getDouble(4);
				resBuff[4] = 0;
			}
			rs.close();
		stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return resBuff;

	}
	public void setMemRes(long a[]) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update town setMem m = " + a[0] + ", t = " + a[1] + ", mm = " + a[2] + ", f = " + a[3] +  ", pop = " + a[4] + " where tid = " + townID);
			stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }
		}
	public long[] getMemRes() {
		long res[] = new long[5];
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select m,t,mm,f,pop from town where tid = "+ townID);
			if(rs.next()) {
	
		res[0]=rs.getLong(1);
		res[1]=rs.getLong(2);
		res[2]=rs.getLong(3);
		res[3]=rs.getLong(4);
		res[4] = rs.getLong(5);
			}
			rs.close();
		stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return res;
		
	}public long[] getMemDebris() {
		long res[] = new long[5];
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select debm,debt,debmm,debf from town where tid = "+ townID);
			if(rs.next()) {
	
		res[0]=rs.getInt(1);
		res[1]=rs.getInt(2);
		res[2]=rs.getInt(3);
		res[3]=rs.getInt(4);
		res[4] = 0;
			}
			rs.close();
		stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return res;
		
	}
	
	public long[] getMemResCaps() {
		//long newcap =(long) Math.ceil(Building.resourceAmt*Math.exp(holdBldg.getMemLvl()+1));
		long resCaps[] = new long[5];
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select sum(pow(lvl+2,2)) from bldg where tid = " + townID + " and name = 'Metal Warehouse';");
			if(rs.next()) resCaps[0]=(long) Math.ceil(Building.resourceAmt*rs.getLong(1));
			rs.close();
			
			rs = stmt.executeQuery("select sum(pow(lvl+2,2)) from bldg where tid = " + townID + " and name = 'Timber Warehouse';");
			if(rs.next()) resCaps[1]=(long) Math.ceil(Building.resourceAmt*rs.getLong(1));
			rs.close();
			
			rs = stmt.executeQuery("select sum(pow(lvl+2,2))from bldg where tid = " + townID + " and name = 'Manufactured Materials Warehouse';");
			if(rs.next()) resCaps[2]=(long) Math.ceil(Building.resourceAmt*rs.getLong(1));
			rs.close();
			
			rs = stmt.executeQuery("select sum(pow(lvl+2,2)) from bldg where tid = " + townID + " and name = 'Food Warehouse';");
			if(rs.next()) resCaps[3]=(long) Math.ceil(Building.resourceAmt*rs.getLong(1));
			rs.close();
			
			stmt.close();
			Player pl = getMemPlayer();
			if(pl.isLeague()) {
				League p = (League) getMemPlayer();
				UberStatement stmt2 = God.con.createStatement();
				ResultSet getMemT; TaxPlayerRank curr;
				ResultSet getMemB; UberStatement bstmt = God.con.createStatement();
				ResultSet getMemC; UberStatement cstmt = God.con.createStatement();

				Town t;
				ArrayList<Town> towns = p.towns();

				int i = 0;
				ArrayList<TaxPlayerRank> tpr = ((League) p).tpr();
				while(i<tpr.size()) {
					curr = tpr.get(i);
				getMemT = stmt2.executeQuery("select tid from town where pid = " + curr.pid);
				getMemC = cstmt.executeQuery("select commsCenterTech from player where pid = " +curr.pid);
				int commsCenterTech=1;
				if(getMemC.next())
				 commsCenterTech = getMemC.getInt(1);
				getMemC.close();
				while(getMemT.next()) {
					
					getMemB = bstmt.executeQuery("select lvl from bldg where tid = " + getMemT.getInt(1) + " and name = 'Communications Center';");
					int amt=0;
					while(getMemB.next()) {
						amt+=(long) Math.ceil(Building.resourceAmt*Math.pow(getMemB.getInt(1)+2,2)*(1+.05*(commsCenterTech-1)));
					}
					getMemB.close();
					int k = 0;
						while(k<resCaps.length-1) {
							resCaps[k]+=amt;
							k++;
						}
	
				}
				getMemT.close();
				i++;
				}
				stmt2.close(); bstmt.close();
			}
			
			
			return resCaps;
		} catch(SQLException exc) { exc.printStackTrace(); }
		return resCaps;
	}
	public void setMemResEffects(double a[]) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update town set minc = " + a[0] + ", tinc = " + a[1] + ", mminc = " + a[2] + ", finc = " + a[3] + " where tid = " + townID);
			stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }
	}
	public double[] getMemResEffects() {
		double resEffects[] = new double[5];
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select minc,tinc,mminc,finc from town where tid = "+ townID);
			if(rs.next()) {
	
				resEffects[0]=rs.getDouble(1);
				resEffects[1]=rs.getDouble(2);
				resEffects[2]=rs.getDouble(3);
				resEffects[3]=rs.getDouble(4);
				resEffects[4] = 0;
			}
			rs.close();
		stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return resEffects;	
		}
	
	public double[] getMemResInc() {
	
		//						holdTown.getMemResInc()[1]=GodGenerator.gameClockFactor*Town.baseResourceGrowthRate*Math.exp(holdBldg.getMemLvl())/3600;
		double resIncs[] = new double[5];
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select sum(pow(lvl+1,2)) from bldg where tid = " + townID + " and name = 'Metal Mine';");
			if(rs.next()) resIncs[0]=((double) GodGenerator.gameClockFactor)*((double) Town.baseResourceGrowthRate)*rs.getDouble(1)/3600;
			rs.close();
			
			rs = stmt.executeQuery("select sum(pow(lvl+1,2)) from bldg where tid = " + townID + " and name = 'Timber Field';");
			if(rs.next()) resIncs[1]=((double) GodGenerator.gameClockFactor)*((double) Town.baseResourceGrowthRate)*rs.getDouble(1)/3600;
			rs.close();
			
			rs = stmt.executeQuery("select sum(pow(lvl+1,2)) from bldg where tid = " + townID + " and name = 'Manufactured Materials Plant';");
			if(rs.next()) resIncs[2]=((double) GodGenerator.gameClockFactor)*((double) Town.baseResourceGrowthRate)*rs.getDouble(1)/3600;
			rs.close();
			
			rs = stmt.executeQuery("select sum(pow(lvl+1,2)) from bldg where tid = " + townID + " and name = 'Food Farm';");
			if(rs.next()) resIncs[3]=((double) GodGenerator.gameClockFactor)*((double) Town.baseResourceGrowthRate)*rs.getDouble(1)/3600;
			rs.close();
			
			stmt.close();
			
			return resIncs;
		} catch(SQLException exc) { exc.printStackTrace(); }
		return resIncs;
		}
	
	public void setMemTownName(String townName) {
		setMemString("townName",townName);
	}
	public String getMemTownName() {
		return getMemString("townName");
	}

	public int getMemTotalTraders() {
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select sum(ppl) from bldg where tid = " + townID + " and name = 'Trade Center';");
			int totalTraders=0;
			if(rs.next())
				totalTraders = rs.getInt(1);
			rs.close(); stmt.close();
			return totalTraders;
		} catch(SQLException exc) { exc.printStackTrace(); } 
		return 0;	}
	public void setMemX(int x) {
		setMemInt("x",x);
	}
	public int getMemX() {
		return getMemInt("x");
	}
	public void setMemY(int y) {
		setMemInt("y",y);
	}
	public int getMemY() {
		return getMemInt("y");
	}
	public void setMemPop(long pop) {
		setMemLong("pop",pop);
	}
	
	public String getMemPlayerName() {
		return getMemPlayer().getMemUsername();
	}
	public void setInternalClock(int internalClock) {
		this.internalClock=internalClock;
	}
	public int getMemInternalClock() {
		return internalClock;
	}

	
	
	public boolean exists() {
		boolean toRet=false;
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select tid from town where tid = " + townID);
			if(rs.next()) toRet=true;
			rs.close(); stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return toRet;
	}

	public void setHoldingIteratorID(String tosetMem) {
		holdingIteratorID=tosetMem;
	}
	public String getMemHoldingIteratorID() {
		return holdingIteratorID;
	}
	public void setMemInt(String fieldName, int tosetMem) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update town set " + fieldName + " = " + tosetMem + " where tid = " + townID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setMemDouble(String fieldName, double tosetMem) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update town set " + fieldName + " = " + tosetMem + " where tid = " + townID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setMemLong(String fieldName, long tosetMem) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update town set " + fieldName + " = " + tosetMem + " where tid = " + townID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setMemBoolean(String fieldName, boolean tosetMem) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update town set " + fieldName + " = " + tosetMem + " where tid = " + townID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setMemString(String fieldName, String tosetMem) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update town set " + fieldName + " = \"" + tosetMem + "\" where tid = " + townID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public int getMemInt(String togetMem) {
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select " + togetMem + " from town where tid = " + townID);
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
	
	
	public double getMemDouble(String togetMem) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + togetMem + " from town where tid = " + townID);
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
	
	public long getMemLong(String togetMem) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + togetMem + " from town where tid = " + townID);
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
	
	public boolean getMemBoolean(String togetMem) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + togetMem + " from town where tid = " + townID);
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
	public String getMemString(String togetMem) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + togetMem + " from town where tid = " + townID);
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

	public void setZeppelin(boolean zeppelin) {
		this.zeppelin = zeppelin;
	}

	public boolean isZeppelin() {
		return zeppelin;
	}

	
	public int getDestX() {
		return destX;
	}

	public void setDestX(int destX) {
		this.destX = destX;
	}

	public int getDestY() {
		return destY;
	}

	public void setDestY(int destY) {
		this.destY = destY;
	}

	public void setTicksTillMove(int ticksTillMove) {
		this.ticksTillMove = ticksTillMove;
	}

	public int getTicksTillMove() {
		return ticksTillMove;
	}

	public void setDebris(long debris[]) {
		this.debris = debris;
	}

	public long[] getDebris() {
		return debris;
	}
}

