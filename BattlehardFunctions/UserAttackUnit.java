package BattlehardFunctions;

import BHEngine.AttackUnit;


public class UserAttackUnit {
private	double concealment, armor, cargo, speed;
	
	// Weapons are hardcoded into attack unit data as double arrays to save
	// processing time. There are a limited amount of weapons.


private int graphicNum;
private	double firepower=0, ammo=0, accuracy=0;
private int support=0; private int originalSlot, originalTID; // for support aus...do not confuse support w/ raid's support,
//this let's us know this is a foreign unit.
// say which town to put this in as a slot for!

private int originalPlayerID; // Reference to original player if in slot so that I can easily access it.
private int expmod = 0;
private double hp = 0;
private	int weap[];
volatile private	int size; // Optional, can be set if you want AttackUnit to store the number
	// of this type in a raid/attack. This class can also be used to simply
	// describe a general unit type also.
private	String name; private int popSize; 
private	int slot; // Which slot this unit is stored in.
private	int lotNum; // For the civilians.
private	String civType = "None";
private static int soldierHP=100,tankHP=500,juggerHP=1000,bomberHP=4000,civilianHP=75,
soldierExpMod=1,tankExpMod=10,juggerExpMod=40,bomberExpMod=20,civilianExpMod=1, soldierPop=1,
tankPop=5,juggerPop=10,bomberPop=20,civilianPop=1,soldierPoints=400,tankPoints=800,juggerPoints=1600,bomberPoints=200,
tier1=100,tier2=200,tier3=400,tier4=100;
private String originalPlayer;
public UserAttackUnit(String name, int slot, int originalPlayerID, int originalSlot,
		int originalTID,
		int support, String originalPlayer) {
	this.originalPlayer=originalPlayer;
	
	this.originalPlayerID = originalPlayerID;
	this.originalSlot = originalSlot;
	this.originalTID = originalTID;

	this.slot = slot;
	this.support = support;
	this.name=name;
	AttackUnit.setValues(this);
}
public String getOriginalPlayer() {
	return originalPlayer;
}
public double getConcealment() {
	return concealment;
}
public double getArmor() {
	return armor;
}
public double getCargo() {
	return cargo;
}
public double getSpeed() {
	return speed;
}

public int getGraphicNum() {
	return graphicNum;
}
public double getFirepower() {
	return firepower;
}
public double getAmmo() {
	return ammo;
}
public double getAccuracy() {
	return accuracy;
}
public int getSupport() {
	return support;
}
public int getOriginalSlot() {
	return originalSlot;
}
public int getOriginalTID() {
	return originalTID;
}

public int getOriginalPlayerID() {
	return originalPlayerID;
}
public int getExpmod() {
	return expmod;
}
public double getHp() {
	return hp;
}
public int[] getWeap() {
	return weap;
}
public int getSize() {
	return size;
}
public String getName() {
	return name;
}
public int getPopSize() {
	return popSize;
}
public int getSlot() {
	return slot;
}
public int getLotNum() {
	return lotNum;
}
public String getCivType() {
	return civType;
}
public static int getSoldierHP() {
	return soldierHP;
}
public static int getTankHP() {
	return tankHP;
}
public static int getJuggerHP() {
	return juggerHP;
}
public static int getBomberHP() {
	return bomberHP;
}
public static int getCivilianHP() {
	return civilianHP;
}
public static int getSoldierExpMod() {
	return soldierExpMod;
}
public static int getTankExpMod() {
	return tankExpMod;
}
public static int getJuggerExpMod() {
	return juggerExpMod;
}
public static int getBomberExpMod() {
	return bomberExpMod;
}
public static int getCivilianExpMod() {
	return civilianExpMod;
}
public static int getSoldierPop() {
	return soldierPop;
}
public static int getTankPop() {
	return tankPop;
}
public static int getJuggerPop() {
	return juggerPop;
}
public static int getBomberPop() {
	return bomberPop;
}
public static int getCivilianPop() {
	return civilianPop;
}
public static int getSoldierPoints() {
	return soldierPoints;
}
public static int getTankPoints() {
	return tankPoints;
}
public static int getJuggerPoints() {
	return juggerPoints;
}
public static int getBomberPoints() {
	return bomberPoints;
}
public static int getTier1() {
	return tier1;
}
public static int getTier2() {
	return tier2;
}
public static int getTier3() {
	return tier3;
}
public static int getTier4() {
	return tier4;
}

}
