package BattlehardFunctions;

import BHEngine.Building;


public class UserBuilding {

	String type;
	String desc;
	  private int lvl; private long cap; private int lotNum; private long cost[] = new long[5];
	private int ticksToFinish, ticksToFinishTotal; private int peopleInside; // used to determine time to level up/build.
private int bid;
private int refuelTicks;
//private	int ticksPerUnit[] = new int[6]; // for combat units ONLY.
//private	int ticksLeftPerUnit[] = new int[6]; // for combat units ONLY.
private UserQueueItem[] Queue;

private	int numLeftToBuild, ticksPerPerson, ticksLeft; // for people
//private	int numUnitsLeftToBuild[] = new int[6];
private int lvlUps; // so if they call for one already on the server, it just ups this instead.
private boolean deconstruct; // needs to be false if nothing is happening, if it becomes true and is on bldgserver,
private int bunkerMode=0;
private boolean mineBldg=false;
private static int resourceAmt = 600; // increase this to get an increase in
// warehouse size per level, currently the amt of the building
// but this roughly equals six hours of production at the mine level = warehouse lvl.
// then level ups cannot happen anymore.
private static int baseResourceAmt = 1000;

	public UserBuilding(UserQueueItem[] queue, int bid, int bunkerMode,
		long cap, long[] cost, boolean deconstruct, int lotNum,
		int lvl, int lvlUps, int numLeftToBuild,
		int peopleInside, int ticksLeft, int ticksPerPerson,
		int ticksToFinish, int ticksToFinishTotal, String type, int refuelTicks) {
	
	Queue = queue;
	this.bid = bid;
	this.bunkerMode = bunkerMode;
	this.cap = cap;
	this.cost = cost;
	this.deconstruct = deconstruct;
	this.lotNum = lotNum;
	this.lvl = lvl;
	this.lvlUps = lvlUps;
	this.refuelTicks=refuelTicks;
	this.numLeftToBuild = numLeftToBuild;
	this.peopleInside = peopleInside;
	this.ticksLeft = ticksLeft;
	this.ticksPerPerson = ticksPerPerson;
	this.ticksToFinish = ticksToFinish;
	this.ticksToFinishTotal = ticksToFinishTotal;
	this.type = type;
	if(type.contains("Warehouse")) mineBldg=true;
}
	public int getLvl() {
		return lvl;
	}

	public long getCap() {
		return cap;
	}

	public int getLotNum() {
		return lotNum;
	}

	public int getTicksToFinish() {
		return ticksToFinish;
	}

	public int getTicksToFinishTotal() {
		return ticksToFinishTotal;
	}

	public int getPeopleInside() {
		return peopleInside;
	}

	public int getBid() {
		return bid;
	}

	public UserQueueItem[] getQueue() {
		return Queue;
	}

	public int getNumLeftToBuild() {
		return numLeftToBuild;
	}

	public int getTicksPerPerson() {
		return ticksPerPerson;
	}

	public int getTicksLeft() {
		return ticksLeft;
	}

	public int getRefuelTicks() {
		return refuelTicks;
	}
	public int getLvlUps() {
		return lvlUps;
	}

	public boolean isDeconstruct() {
		return deconstruct;
	}

	public int getBunkerMode() {
		return bunkerMode;
	}

	public boolean isMineBldg() {
		return mineBldg;
	}

	public static int getResourceAmt() {
		return resourceAmt;
	}

	public static int getBaseResourceAmt() {
		return baseResourceAmt;
	}
	public String getType() {
		return type;
	}

	public String getDesc() {
		return desc;
	}

	public long[] getCost() {
		return cost;
	}
	public UserBuilding(String type) {
		setDescription(type);
	}
	
	public static UserBuilding[] getBuildings() {
		UserBuilding to[] = new UserBuilding[22];
		to[0] = new UserBuilding("Headquarters");
		to[1] = new UserBuilding("Arms Factory");
		to[2] = new UserBuilding("Construction Yard");
		to[3] = new UserBuilding("Institute");
		to[4] = new UserBuilding("Communications Center");
		to[5] = new UserBuilding("Trade Center");
		to[6] = new UserBuilding("Bunker");
		to[7] = new UserBuilding("Metal Warehouse");
		to[8] = new UserBuilding("Timber Warehouse");
		to[9] = new UserBuilding("Manufactured Materials Warehouse");
		to[10] = new UserBuilding("Food Warehouse");
	
		to[11] = new UserBuilding("Missile Silo");
		to[12] = new UserBuilding("Recycling Center");
		to[13] = new UserBuilding("Airship Platform");
		to[14] = new UserBuilding("Metal Refinery");
		to[15] = new UserBuilding("Timber Processing Plant");
		to[16] = new UserBuilding("Materials Research Center");
		to[17] = new UserBuilding("Hydroponics Lab");
		to[18] = new UserBuilding("Metal Mine");
		to[19] = new UserBuilding("Timber Field");
		to[20] = new UserBuilding("Manufactured Materials Plant");
		to[21] = new UserBuilding("Food Farm");
		return to;
	}
	public void setDescription(String type) {
		if(type.equals("Headquarters")) { 
			this.type=type;
			cost[0] = 70;
			cost[1] = 40;
			cost[2] = 150;
			cost[3] = 140;
			cost[4] = 0;
			desc = "This building controls all of your ingoing and outgoing raids. Each level you add to" +
					" this building adds an extra slot for a raid, and you can only have one per town." +
					" This building and your bunkers must be demolished before an enemy can have a chance of" +
					" invading your town.";
		}
		if(type.equals("Arms Factory")) {//100 100 100 100
			this.type=type;
			cost[0] = 150;
			cost[1] = 70;
			cost[2] = 140;
			cost[3] = 40;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];

			desc = "An Arms Factory allows you to build the combat units you have in your unit slots. You can also fill" +
					" or empty your slots with unit templates from the UTCC in this building. Combat slots are universal across" +
					" all towns - you cannot fill each town with different unit types. Leveling up this building " +
					" gives the raids you send from the town it's located in extra defensive protection on those foreign battlefields" +
					" in the same way that bunkers do at home on defensive mode.";
			
		}
		if(type.equals("Construction Yard")) {
			this.type=type;
			cost[0] = 70;
			cost[1] = 150;
			cost[2] = 140;
			cost[3] = 40;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
		//	peopleInside=0;
			
			
			desc = "A construction yard allows you to demolish and upgrade buildings.(You can still build buildings without a" +
					" construction yard.) They also build and house engineers, which lower" +
					" the build times on pretty much everything that your town produces. Engineers in one town do not effect the building times" +
					" in another town you own. Each level increases the engineer housing capacity of the building.";
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
			
			desc = "An institute allows you to conduct research into different areas. They also house and build scholars, and these" +
					" scholars create knowledge points that you use to pursue the aforementioned research. Increasing building levels" +
					" increases the housing capacity for scholars.";
			
			

			
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
			desc = "A communications center is an absolute requirement to join, create, or maintain a league.  Wondering why you would ever want to level up your communications center?" +
					" To account for the extra tax income pouring into each League-owned town from you and other members, the League has higher" +
					" resource caps proportional to the aggregate level of all communications centers owned by league members. Also, upgrading" +
					" these buildings allows you to detect all raids trades to and from any town nearby to spy on your neighbor, and increases the distance from the town at which" +
					" you can make invasions.";
			
			
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
			desc = "Trade Centers are required to maintain any sort of trade relationship with your own territories or other players'. " +
					"Within the Trade Center you can both build and house Traders. Increasing building level increases the housing capacity of traders and the trading slots" +
					" available.";
			

			
		}
		if(type.equals("Bunker")) {
			this.type=type;
			cost[0] = 140;
			cost[1] = 150;
			cost[2] = 40;
			cost[3] = 70;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			desc = "Bunkers provide different advantages depending on their mode. In Defense Mode, Bunkers provide extra protection " +
					"for your soldiers when you are attacked. In VIP Mode, they protect a certain percentage of your citizenry from being" +
					" bombed in a Glassing/Strafing run or being slaughtered in a Genocide/Glassing campaign. In Resource Cache mode," +
					" they protect a certain percentage of your resources from being thefted during an enemy raid. Increasing level" +
					" increases the percentage of effectiveness offered by each option.";
			
			
			
		}if(type.equals("Airship Platform")) {
			this.type=type;
			cost = Building.getCost(type);
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			desc = "This building allows you to create Airships and constantly generates a supply of Airship fuel, the rate of which is determined " +
					"by this building's level. You can refuel Airships with this building by hovering the Airship over the town on the World Map.";
			
			
			
		}if(type.equals("Missile Silo")) {
			this.type=type;
			cost = Building.getCost(type);
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			desc = "The most powerful and destructive weapon in the game, a nuclear missile can be used either to create a SkyBurst EMP blast, which knocks out all A.I.s in an area " +
					" as well as leaving behind fall out, with no casualties or building destruction, or as a GroundBurst Nuke, which will level buildings, kill soldiers, and also leave fall out clouds but " +
					"won't cause an EMP Blast. When any player begins constructing a Level 1 Silo, a message is sent to all nearby players " +
					"notifying them of this gross transgression against the denizens of the A.I. Wars Universe. " +
					"The time it takes to build the Silo to level 1 is one week, returning to normal upgrade times for later levels, and this building can be destroyed " +
					"by any successful attack on the town it resides in while in this initial building phase. A successful attack means that your army is completely destroyed at the end of the assault. " +
					"Once built, every level you purchase on this building increases the severity of the destructive effects of the nuke. All" +
					" nukes generate large fall-out clouds that effect everybody nearby for a long time, and a copy of the status report generated by the impact is sent to every player in the region." +
					" The larger the nuke, the more powerful and long-lasting the Fall Out. Nukes also protect you from other nukes: When " +
					"somebody nukes you, your missiles intercept theirs, weakening them, but depleting yours by the difference in Silo levels. When a Silo is used once, it is completely destroyed after use, unless it is used in defense of another nuke, in which case it can be lowered at maximum to level 1.";
			
			
			
		}if(type.equals("Recycling Center")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			desc = "When someone attacks your town, and units die on either side, debris is created about the town and must be collected with debris missions from either you or someone else. However, if you" +
					" have a Recycling Center on site, immediately after the attack is completed, you receive some of the debris directly back into your resource store, with no mission sending required.";
			
			
		}
		if(type.equals("Metal Refinery")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			desc = "Metal Refineries boost your metal production in the town by putting the ore you mine through a more efficient refinement process.";
			
			
		}if(type.equals("Timber Processing Plant")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			desc = "Timber Processing Plants boost your timber production by allowing you to get more product out of even the most unusable parts of the tree.";
			
			
		}if(type.equals("Materials Research Center")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			desc = "This additional building allows your civilization to boost manufactured materials production through additional quality assurance techniques and new fabrication technologies.";
			
			
		}if(type.equals("Hydroponics Lab")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			desc = "Hydroponics Labs are used to boost your food output by using advanced technologies to enhance growth in your Food Farm.";
			
			
		}
		
		if(type.equals("Metal Warehouse")) {
			this.type=type;
			cost[0] = 40;
			cost[1] = 150;
			cost[2] = 140;
			cost[3] = 70;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			desc = "Warehouses add to your town's storage capacity of a resource. Leveling up this building increases it's storage capacity," +
					"and therefore, your town's.";
			
		}
		if(type.equals("Timber Warehouse")) {

			this.type=type;
			cost[0] = 70;
			cost[1] = 40;
			cost[2] = 150;
			cost[3] = 140;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			desc = "Warehouses add to your town's storage capacity of a resource. Leveling up this building increases it's storage capacity," +
			"and therefore, your town's.";
			
		}
		if(type.equals("Manufactured Materials Warehouse")) {

			this.type=type;
			cost[0] = 140;
			cost[1] = 70;
			cost[2] = 40;
			cost[3] = 150;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			desc = "Warehouses add to your town's storage capacity of a resource. Leveling up this building increases it's storage capacity," +
			"and therefore, your town's.";
		}
		
		if(type.equals("Food Warehouse")) {

			this.type=type;
			cost[0] = 150;
			cost[1] = 140;
			cost[2] = 70;
			cost[3] = 40;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			desc = "Warehouses add to your town's storage capacity of a resource. Leveling up this building increases it's storage capacity," +
			"and therefore, your town's.";
			
		}
		if(type.equals("Metal Mine")) {
			this.type=type;
			cost[0] = 100;
			cost[1] = 130;
			cost[2] = 60;
			cost[3] = 110;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
		if(type.equals("Timber Field")) {
			this.type=type;
			cost[0] = 95;
			cost[1] = 95;
			cost[2] = 60;
			cost[3] = 150;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
		if(type.equals("Manufactured Materials Plant")) {
			this.type=type;
			cost[0] = 100;
			cost[1] = 90;
			cost[2] = 90;
			cost[3] = 120;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
		if(type.equals("Food Farm")) {
			this.type=type;
			cost[0] = 95;
			cost[1] = 95;
			cost[2] = 100;
			cost[3] = 110;
			cost[4] = 0;
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
	}
}
