package BattlehardFunctions;

import java.util.UUID;

import BHEngine.Building;


public class UserBuilding {

	String type;
	String desc;
	  private int lvl; private long cap; private int lotNum; private long cost[] = new long[5];
	private int ticksToFinish, ticksToFinishTotal; private int peopleInside; // used to determine time to level up/build.
private UUID id;
private int refuelTicks; private int fortArray[];
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

	public UserBuilding(UserQueueItem[] queue, UUID id, int bunkerMode,
		long cap, long[] cost, boolean deconstruct, int lotNum,
		int lvl, int lvlUps, int numLeftToBuild,
		int peopleInside, int ticksLeft, int ticksPerPerson,
		int ticksToFinish, int ticksToFinishTotal, String type, int refuelTicks, int[] fortArray) {
	
	Queue = queue;
	this.id = id;
	this.bunkerMode = bunkerMode;
	this.cap = cap;
	this.cost = cost;
	this.deconstruct = deconstruct;
	this.lotNum = lotNum;
	this.fortArray=fortArray;
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

	public UUID getId() {
		return id;
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
		UserBuilding to[] = new UserBuilding[23];
		to[0] = new UserBuilding("Command Center");
		to[1] = new UserBuilding("Arms Factory");
		to[2] = new UserBuilding("Storage Yard");
		to[3] = new UserBuilding("Institute");
		to[4] = new UserBuilding("Resource Cache");
		to[5] = new UserBuilding("Trade Center");
		to[6] = new UserBuilding("Fortification");
		to[7] = new UserBuilding("Metal Warehouse");
		to[8] = new UserBuilding("Lumber Yard");
		to[9] = new UserBuilding("Crystal Repository");
		to[10] = new UserBuilding("Granary");
	
		to[11] = new UserBuilding("Missile Silo");
		to[12] = new UserBuilding("Recycling Center");
		to[13] = new UserBuilding("Airstrip");
		to[14] = new UserBuilding("Foundry");
		to[15] = new UserBuilding("Sawmill");
		to[16] = new UserBuilding("Crystal Refinery");
		to[17] = new UserBuilding("Hydroponics Bay");
		to[18] = new UserBuilding("Metal Mine");
		to[19] = new UserBuilding("Timber Field");
		to[20] = new UserBuilding("Crystal Mine");
		to[21] = new UserBuilding("Farm");
		to[22] = new UserBuilding("Manufacturing Plant");

		return to;
	}
	public void setDescription(String type) {
		if(type.equals("Command Center")) { 
			this.type=type;
			cost = Building.getCost(type);

			desc = "Requires: Nothing. <br /><br />This building is the center of any city. No building in a city can be more than two levels above your highest leveled command center." +
					" Leveling your command center allows you to send out more raids at once, and increases" +
					" the radius of the world map which you can see.";
		}
		if(type.equals("Arms Factory")) {//100 100 100 100
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];

			desc = "Requires: Level 1 Command Center.<br /><br />An Arms Factory allows you to build ground units. Increasing it's level increases the amount you can build at any one time.";
			
		}
		if(type.equals("Manufacturing Plant")) {//100 100 100 100
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];

			desc = "Requires: Level 10 Institute and Level 5 Arms Factory.<br /><br /> A Manufacturing Plant gives you the ability to build advanced ground units like the Juggernaught and Tank class. Leveling up this plant increases the amount of units you can build at one time and reduces the overall build times for combat units in your town by 1.5% per level.";
			
		}
		if(type.equals("Storage Yard")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
		//	peopleInside=0;
			
			
			desc = "Requires: Level 10 of each Warehouse type.<br /><br />The Storage Yard acts as a warehouse that stores all four types of resources. After level 5, it protects a certain net amount of resources from theft and cannot be leveled beyond the current lowest leveled warehouse.";
		}
		if(type.equals("Institute")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			
			desc = "Requires: Level 1 Command Center.<br /><br />An institute allows you to conduct research into different areas. They also house and build scholars, and these" +
					" scholars create knowledge points that you use to pursue the aforementioned research. Increasing building levels" +
					" increases the housing capacity for scholars.";
			
			

			
		}
		
		if(type.equals("Resource Cache")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			desc = "Requires: Level 1 of each Warehouse type and Level 3 Institute. <br /><br />Each level of the Resource Cache allows you to hide more resources from attacking armies.";
			
			
		}
		

		
		if(type.equals("Trade Center")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			desc = "Requires: Level 3 Command Center and Level 1 Institute.<br /><br />Trade Centers are required to maintain any sort of trade relationship with your own territories or other players'. " +
					"Within the Trade Center you can both build and house Traders. Increasing building level increases the housing capacity of traders and the trading slots" +
					" available.";
			

			
		}
		if(type.equals("Fortification")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			desc = "Requires: Level 5 Command Center, Level 5 Institute, and Level 5 Arms Factory.<br /><br />Fortifications lend protective bonuses to your armies when you are attacked. Each level of a fortification increases the amount of combat units it can protect.";
			
			
			
		}if(type.equals("Airstrip")) {
			this.type=type;
			cost = Building.getCost(type);
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			desc = "Requires: Level 10 Command Center, Level 10 Arms Factory, Level 5 Manufacturing Plant, and Airship Blueprint Research. <br /><br /> The Airstrip is used for the creation and maintenance of an Airforce. Each level increase increases the amount of units you can build at one time. In addition, this building allows you to create Airships and constantly generates a supply of Airship fuel, the rate of which is determined " +
					"by this building's level. You can refuel Airships with this building by hovering the Airship over the town on the World Map.";
			
			
			
		}if(type.equals("Missile Silo")) {
			this.type=type;
			cost = Building.getCost(type);
			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			desc = "Requires: Level 15 Manufacturing Plant and Level 15 Institute.<br /><br /> The most powerful and destructive weapon in the game, a nuclear missile can be used either to create a SkyBurst EMP blast, which knocks out all A.I.s in an area " +
					" as well as leaving behind fall out, with no casualties or building destruction, or as a GroundBurst Nuke, which will level buildings, kill soldiers, and also leave fall out clouds but " +
					"won't cause an EMP Blast. When any player begins constructing a Level 1 Silo, a message is sent to all nearby players " +
					"notifying them of this gross transgression against the denizens of this Universe. " +
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
			desc = "Requires: Level 10 Command Center, Level 10 Institute, Level 10 Manufacturing Plant.<br /><br />This building automatically collects a percentage of debris from battles that happen in the town in which it's located, allows you to see what other towns have debris around them for manual collection by your army, and refunds you a percentage of the cost of your units and buildings when you destroy them yourself. Leveling this building increases the percentage collected as debris, and percentage cost refunded by self-destruction of units or buildings.";
			
			
		}
		if(type.equals("Foundry")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			desc = "Requires: Level 15 Metal Mine.<br /><br />Max Level: 20<br /><br />Foundries boost your metal production in the town by putting the ore you mine through a more efficient refinement process.";
			
			
		}if(type.equals("Sawmill")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			desc = "Requires: Level 15 Timber Field.<br /><br />Max Level: 20<br /><br />Sawmills boost your timber production by allowing you to get more product out of even the most unusable parts of the tree.";
			
			
		}if(type.equals("Crystal Refinery")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			desc = "Requires: Level 15 Crystal Mine.<br /><br />Max Level: 20<br /><br />Crystal Refineries boost your crystal production through a more efficient refinement process.";
			
			
		}if(type.equals("Hydroponics Bay")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			;
		//	peopleInside=0;
			desc = "Requires: Level 15 Farm.<br /><br />Max Level: 20<br /><br />Hydroponics Bays are used to boost your food output by using advanced technologies to enhance growth in your Farm.";
			
			
		}
		
		if(type.equals("Metal Warehouse")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			desc = "Requires: Level 1 Institute.<br /><br />Warehouses add to your town's storage capacity of a resource. Leveling up this building increases it's storage capacity," +
					"and therefore, your town's.";
			
		}
		if(type.equals("Lumber Yard")) {

			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			desc = "Requires: Level 1 Institute.<br /><br />Warehouses add to your town's storage capacity of a resource. Leveling up this building increases it's storage capacity," +
			"and therefore, your town's.";
			
		}
		if(type.equals("Crystal Repository")) {

			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			desc = "Requires: Level 1 Institute.<br /><br />Warehouses add to your town's storage capacity of a resource. Leveling up this building increases it's storage capacity," +
			"and therefore, your town's.";
		}
		
		if(type.equals("Granary")) {

			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			desc = "Requires: Level 1 Institute.<br /><br />Warehouses add to your town's storage capacity of a resource. Leveling up this building increases it's storage capacity," +
			"and therefore, your town's.";
			
		}
		if(type.equals("Metal Mine")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
		if(type.equals("Timber Field")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
		if(type.equals("Crystal Mine")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
		if(type.equals("Farm")) {
			this.type=type;
			cost = Building.getCost(type);

			//ticksToFinishTotal = cost[0] + cost[1] + cost[2] + cost[3];
			
			
			
		}
	}
	/**
	 * Returns an array representing how much of each unit type is protected by this fortification,
	 * if this building is indeed a fortification.
	 * 
	 * If it isn't, then it just returns an array of zeroes.
	 */
	public int[] getFortArray() {
		return fortArray;
	}
}
