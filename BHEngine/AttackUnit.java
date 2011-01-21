/*
 * This unit holds all the data about a single attack unit type.
 */
package BHEngine;

import java.util.ArrayList;

public class AttackUnit {
private	double concealment, armor, cargo, speed;
	
	// Weapons are hardcoded into attack unit data as double arrays to save
	// processing time. There are a limited amount of weapons.

private 	 double fp[] = new double[21]; // Only one weapon right now.
private 	 double amm[] = new double[21];
private 	 double acc[] = new double[21];

private int graphicNum;
private	double firepower=0, ammo=0, accuracy=0;
private int support=0;private int originalSlot, originalTID; // for support aus...do not confuse support w/ raid's support,
//this let's us know this is a foreign unit.
// say which town to put this in as a slot for!
private boolean deleteMe=false; // these two booleans only used for template aus, which are more common than actual aus I bet.
private boolean addMe=false; // deleteMe removes au and db entry, addMe adds an entry for a new template,
private boolean editMe=false; // edit me makes it so the db entry isn't erased but the object is in place
// of a new modified version, which'll take up updating the entry afterwards.

private Player originalPlayer; // Reference to original player if in slot so that I can easily access it.
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
public static int soldierHP=100,tankHP=500,juggerHP=1000,bomberHP=4000,civilianHP=75,
soldierExpMod=1,tankExpMod=10,juggerExpMod=40,bomberExpMod=20,civilianExpMod=1, soldierPop=1,
tankPop=5,juggerPop=10,bomberPop=20,civilianPop=1,soldierPoints=400,tankPoints=800,juggerPoints=1600,bomberPoints=200,
tier1=100,tier2=200,tier3=400,tier4=100;

	public AttackUnit() {
		// default unit protocol
		
	
		
		setConcealment(setArmor(setCargo(setSpeed(3))));
		
		setSize(10);
		setName("Basic Trooper");
		
	//	setUpWeapons();
	}
	
	public static String[] returnWeapDesc() {
		String weapDesc[] = new String[21];

		weapDesc[0]="The Pump-Action EMP Burster fires bursts of EMP pulses via a pump-action mechanism.";
		weapDesc[1] = "The Pulverizer fires large bursts of gamma radiation at enemies.";
		weapDesc[2] = "The Rail Gun uses electromagnetic forces to propel titanium rods at high speeds.";
		weapDesc[3] = "The Plasma Rifle fires globs of plasma at enemies.";
		weapDesc[4] = "The Arc-Thrower allows the user to hurl arcs of electricity over a wide area.";
		weapDesc[5] = "The Laser Rifle is a sniper weapon that fires focused high energy laser beams at far-away targets.";
		weapDesc[6] = "The WTF Class Rocket Launcher fires heat-seeking rocket-propeled explosives at something unfriendly.";
		weapDesc[7] = "The Automatic EMP Burster is an advanced version of the original Pump-Action EMP Burster" +
				" with automatic firing capabilities.";
		weapDesc[8] = "The EMP Grenade Launcher. No further description required.";
		weapDesc[9] = "The Plasma Minigun is an advanced version of the original Plasma Rifle that fires larger, more focused" +
				" beams of plasma at an extremely high rate of automatic fire. Fires an entire clip of plasma in less than one second. Aim away from face.";
		weapDesc[10] ="The Gauss Cannon uses electromagnetic forces expel heavy tungsten projectiles at very unpleasant velocities. Like the Rail Gun, only less friendly and a lot larger.";
		weapDesc[11] = "The Fully Automated Laser Drone is attached as a backpack or add-on to a unit that detaches in battle" + 
		" and moves across the battlefield firing at enemies until it is destroyed.";
		weapDesc[12] = "The B.R.T.H.L.E.: Nobody has lived to tell what is inside the simple 3ft by 3ft square. Soldiers" +
		 " set it down and press a red button atop it and run as fast as they can in the other direction. No remains of " +
		 " soldiers are ever found, they are simply wiped from the face of the Universe, with the box.";
		weapDesc[13] = "The Singularity Whip projects a stream of singularities that are tethered by quantum foam allowing the user " +
		"to lash out at opponents with a force that can crush stars.";
		weapDesc[14] = "The Superstring Accelerator Rifle fires a fundamental piece of the early universe less than a planck length across" +
		 " with an accuracy verging on the uncertainty limit at a speed near that of light itself. ";
		weapDesc[15] = "The Quantum Anomaly Enabler Seeks out quantum fluctuations in the air near enemies and enlarges them with" +
		 " gravitational waves emitted at quantum resonance frequencies. Causes instant liquidation. ";
		weapDesc[16] = "The Gauss Minigun is an advanced, automatic version of the Gauss Cannon. Due to it's weight, it comes with an anti-gravity"
			+ " support unit to aid in moving and maintenance.";
		weapDesc[17] = "The EMP Wasp is a tiny cybernetic wasp that attaches to enemies, hacks into their neural nets, and turns "
			+ "them into allies until they have emptied their magazines on their former friends. Then the wasp and the hacked enemy" +
					" self-destruct in a violent explosion that does even more damage.";
		weapDesc[18] ="The H.I.V.E. is a shell containing a swarm of tiny nanobots that disassemble buildings, leaving most citizens intact. Best if" +
		 " dropped from a great height. This weapon can only be equipped on bombers." ;
		weapDesc[19] = "The Horizon Machine is a type of bomb that doesn't explode, but implodes into a tiny singularity"
		 +" with an event horizon about the size of a city block. It evaporates after a millisecond,"+
		 " taking everything within the horizon with it. This weapon can only be equipped on bombers and destroys" +
		 " both civilians and buildings, but only half as many civilians as the Focused Nova Bomb, and only half as many buildings" +
		 " as the H.I.V.E.";
		weapDesc[20] = "The Focused Nova Bomb: Within the machinations of a sphere one hundred feet wide, a star is born, and then it"+
		 " dies, brilliantly, violently. All of the gamma radiation from this fiery death is thrust out in one direction -"+
		 " down. Every citizen caught in it's rush is immediately disintegrated. This weapon can only be equipped on bombers.";
		return weapDesc;
		
	}
	public AttackUnit(int x) {
		// quick way of making a new template. Don't know what
		// I left the basic constructor above for, so I'm not
		// going to mess with it.
		setWeap(new int[0]);
		setSize(0);
		setName("New Template");
		setConcealment(setArmor(setCargo(setSpeed(0))));
		setHp(soldierHP);
		setExpmod(soldierExpMod); // make it a soldier by default.
		setPopSize(soldierPop);
		setGraphicNum(0);
		setUpWeapons();
	}
	
	public void changeType(int popSize) {
		this.setPopSize(popSize);
		switch(popSize) {
		case 1:
			setHp(soldierHP); 
			setExpmod(soldierExpMod);
			break;
		case 5:
			setHp(tankHP); 
			setExpmod(tankExpMod);
			break;
		case 10:
			setHp(juggerHP);
			setExpmod(juggerExpMod);
			break;
		case 20:
			setHp(bomberHP); // no dmgmodifier, bombers do no damage to combat units.
			setExpmod(bomberExpMod);
			break;
		}
	}
	public int getSlot() {
		return slot;
	}
	public String getName() {
		return name;
	}
	public int getSize() {
		return size;
	}
	public void addWeapon(int weapnum) {
		int newWeap[] = new int[getWeap().length+1];
		int i = 0;
		while(i<getWeap().length) {
			newWeap[i]=getWeap()[i];
			i++;
		}
		newWeap[i]=weapnum;
		setWeap(newWeap);
	}
	public void removeWeapon(int index) {
		// removes weapon at index specified in array.
		int i = 0;
		int newWeap[] = new int[getWeap().length-1];
		int j = 0;
		while(j<newWeap.length) {
				
				newWeap[j] = getWeap()[i];
			/*
			 * 1 1
			 * 2 2
			 * 3 4
			 * so if index=3 then at the second that i gets to 3, it just needs
			 * to be incremented again.
			 */
			j++;
			i++;
			if(i==index) i++;

		}
		
		setWeap(newWeap);
		
	}
	public void makeSupportUnit(int originalSlot, Player p, int originalTID) {
		this.setOriginalTID(originalTID);
		this.setOriginalSlot(originalSlot);
		setSupport(1);
		setOriginalPlayer(p);
		
	}
	public void makeOffSupportUnit(int originalSlot, Player p, int originalTID) {
		this.setOriginalTID(originalTID);
		this.setOriginalSlot(originalSlot);
		setSupport(2);
		setOriginalPlayer(p);
		
	}
	public AttackUnit(String name, double conc, double armor, double cargo, double speed, int slot, int weap[]) { // why is this here?
		if(name.equals("locked")) { setConcealment(this.setArmor(this.setCargo(this.setSpeed(1)))); } else {
		
		setConcealment(conc); this.setArmor(armor);this.setCargo(cargo);this.setSpeed(speed);}
		this.setName(name);
		this.setSlot(slot); this.setWeap(weap);
		setHp(soldierHP);
		setUpWeapons();
	}
	
	public AttackUnit(String name, double conc, double armor, double cargo, double speed, int slot, int popSize, int weap[], int graphicNum) {
		setLotNum(-1);
		if(name.equals("locked")||name.equals("empty")) { setConcealment(this.setArmor(this.setCargo(this.setSpeed(this.setPopSize(0))))); } else {
			
			setConcealment(conc); this.setArmor(armor);this.setCargo(cargo);this.setSpeed(speed);}		this.setName(name);
		this.setPopSize(popSize);
		
		this.setGraphicNum(graphicNum);
		switch(popSize) {
		case 1:
			setHp(soldierHP); 
			setExpmod(soldierExpMod);
			break;
		case 5:
			setHp(tankHP); 
			setExpmod(tankExpMod);
			break;
		case 10:
			setHp(juggerHP);
			setExpmod(juggerExpMod);
			break;
		case 20:
			setHp(bomberHP); // no dmgmodifier, bombers do no damage to combat units.
			setExpmod(bomberExpMod);
			break;
		}
		
		this.setWeap(weap);
		this.setSlot(slot);
		setUpWeapons();
	}
	
public void resetUnit(String name, double conc, double armor, double cargo, double speed, int slot, int popSize, int weap[], int graphicNum) { 
	
	setLotNum(-1);
	if(name.equals("locked")||name.equals("empty")) { setConcealment(this.setArmor(this.setCargo(this.setSpeed(this.setPopSize(0))))); } else {

		setConcealment(conc); this.setArmor(armor);this.setCargo(cargo);this.setSpeed(speed);}		this.setName(name);
	this.setPopSize(popSize);
	this.setGraphicNum(graphicNum);
	switch(popSize) {
	case 1:
		setHp(soldierHP); 
		setExpmod(soldierExpMod);
		break;
	case 5:
		setHp(tankHP); 
		setExpmod(tankExpMod);
		break;
	case 10:
		setHp(juggerHP);
		setExpmod(juggerExpMod);
		break;
	case 20:
		setHp(bomberHP); // no dmgmodifier, bombers do no damage to combat units.
		setExpmod(bomberExpMod);
		break;
	}
	
	this.setWeap(weap);
	this.setSlot(slot);
	setUpWeapons();
}
	public AttackUnit(String name,  int lotNum, int weap[], String civType) {
		setConcealment(setArmor(setSpeed(33))); setCargo(0); // setting up civvy values.
		setPopSize(civilianPop);
		setExpmod(civilianExpMod);//expmod needs to be the same as soldiers.
		// if it's zero, no damage doled out.
		setHp(civilianHP);
		this.setName(name);
		this.setWeap(weap);
		setUpWeapons();
		this.setLotNum(lotNum); // For civilians.
		this.setCivType(civType);
	}
	
	public String toString() {
		return getName();
	}
	public AttackUnit returnCopy() {
	
		AttackUnit copy = new AttackUnit(getName(), getConcealment(), getArmor(), getCargo(), getSpeed(),getSlot(),getPopSize(), getWeap(),getGraphicNum());
		if(getSupport()==1) copy.makeSupportUnit(getOriginalSlot(),getOriginalPlayer(),getOriginalTID());
		else if(getSupport()==2) copy.makeOffSupportUnit(getOriginalSlot(),getOriginalPlayer(),getOriginalTID());
		// so it'll copy support units correctly!
		return copy;
		
		
	}
	public int getPop() {
		return getPopSize();
	}
	public void setUpWeapons() {
		/* (firepower, ammo, accuracy) - remember two tier 1's should be able to kill another soldier.
		 * Tier 1: (Costs only one weapons slot)
		 * Pump-Action EMP Burster - Fires short range, wide burst EMP pulses to deactivate other soldiers.
		 * Pulverizer - Fires medium range heavy radiation beams.
		 * Plasma Rifle - Fires automatic directed globs of liquid plasma, great for all situations.
		 * Rail Gun - Fires heavy steel projectiles, extremely accurate! 
		 * Laser Rifle - Extremely accurate, semi-auto laser rifle
		 * Arc-Thrower - Looks like a bow, but made of fine steel and complicated circuitry..
		 * Instead of a bowstring, there is an arc of electricity.
		 * The user can swing the bow in a direction and the electromagnetic energy in the "bowstring" will fling off
		 * towards a target and fry their circuits.
		 * 
		 * 
		 * Tier 2: (Costs two weapons slots)
		 * WTF Class Rocket Launcher - Fires a giant rocket at something unfriendly. (70,1,25)
		 * Automatic EMP Burster - Fires short range, wide burst EMP pulses without all the pumping. (12,5,45)
		 * Plasma Minigun - Fires an entire clip of plasma in less than one second. Aim away from face. (2,25,65)
		 * Gauss Cannon - Like a rail gun, only less friendly and a lot larger. (15,3,75)
		 * Fully Automated Laser Drone - Can be attached to the back of the soldier and will fire automatically with nearly perfect
		 * accuracy. (40,1,100) 
		 * Moles - Thrown as a single grenade, five tiny robots launch out of the central stalk of the grenade using
		 * it's explosive force and burrow into the nearest
		 * enemy, going straight for electronic components.
		 * EMP Grenade Launcher - No description required.
		 * Auto-targeting Laser Rifle - as accurate as a laser rifle, but fully automatic, with an auto-targeting feature for
		 * high FPS.
		 * Missile Launcher - Packs less punch than a single-fire Rocket Launcher, since it has many miniature
		 * rockets packed into a single square grid located on the front of the device. Better chance of hitting your target, though.
		 * 
		 * 
		 * Tier 3: (Costs four weapons slots)
		 * BRTHLE - Nobody has lived to tell what is inside the simple 3ft by 3ft square. Soldiers
		 * set it down and press a red button atop it and run as fast as they can in the other direction. No remains of
		 * soldiers are ever found, they are simply wiped from the face of the Universe, with the box.(160,1,60)
		 * 
		 * Singularity Whip - A whip that flings miniature black holes at enemies. (70,2,75)
		 * 
		 * Gauss Minigun with Antigravity - Fully automatic Gauss minigun with Antigravity support for easy transport. (1,120,85)
		 * 
		 * Quantum Anomaly Enabler (QAE) - Seeks out quantum fluctuations in the air near enemies and enlarges them with
		 * gravitational waves emitted at quantum resonance frequencies. Causes instant liquidation. (50,2,95)
		 * 
		 * Superstring Accelerator Rifle - Fires a fundamental piece of the early universe less than a planck length across
		 * with an accuracy verging on the uncertainty limit at a speed near that of light itself. (90,1,120)
		 * 
		 * Mass-To-Energy Converter (MEC) - Fires a piece of antimatter that turns any matter it touches into pure energy.
		 * 
		 * EMP Wasp - A small flying robot that lands on an enemy soldier, takes over his subsystems, and causes him to turn
		 * towards his nearest ally and emit an EMP burst in that direction, as well as emptying whatever gun he is carrying.
		 * 
		 * The Kingmaker - Fires a miniature neutron bomb that explodes with a radius of about one hundred meters, annihilating
		 * all organic material within.
		 * 
		 * Wrath - A cyber-warfare program integrated into the unit that disables other units by inducing suicidal thoughts via
		 * electromagnetic interference. As this has no actual picture, we should instead draw a picture of a man with his hands
		 * on his head in great pain.
		 * 
		 * Tier four weapons(1 weapons slot, can only be equipped with bombers):
		 * The Horizon Machine and the Focused Nova Bomb kill people - the Nova Bomb should be as powerful as a tier 3 weapon, the
		 * HM as powerful as a tier 2.
		 * 
		 *The Horizon Machine - A type of bomb that doesn't explode, but implodes into a tiny singularity
		 * with an event horizon about the size of a city block. It evaporates after a millisecond, 
		 * taking everything within the horizon with it.
		 * 
		 *Focused Nova Bomb - Within the machinations of a sphere one hundred feet wide, a star is born, and then it
		 *dies, brilliantly, violently. All of the gamma radiation from this fiery death is thrust out in one direction -
		 *down. Every citizen caught in it's rush is immediately deactivated. Kills people 
		 *
		 *H.I.V.E. - A shell containing a swarm of tiny nanobots that disassemble buildings, leaving most citizens intact. Best
		 *dropped from a great height.*/
		// so this is where we take weap and use it to set up firepower, ammo, and accuracy.
		
		// High is 50, Med is 30, Low is 10 for Tier 1
		getFp()[0] = 50; // HLM - Pump Action Emp 
		getAmm()[0] = 10;
		getAcc()[0] = 30;
		getFp()[1] = 50; // HML - Pulverizer
		getAmm()[1] = 30;
		getAcc()[1] = 10;
		getFp()[2] = 30; // MLH - Rail Gun
		getAmm()[2] = 10;
		getAcc()[2] = 50;
		getFp()[3] = 30; // MHL - Plasma Rifle
		getAmm()[3] = 50;
		getAcc()[3] = 10;
		getFp()[4] = 10; // LHM - Arc-Thrower
		getAmm()[4] = 50;
		getAcc()[4] = 30;
		getFp()[5] = 10; // LMH - Laser Rifle
		getAmm()[5] = 30;
		getAcc()[5] = 50;
		
		// High is 110, Medium is 66, Low is 22 for Tier 2 (10% more than two tier 1 weapons...)
		// Why do we do this? Don't want tanks with four tier 1s and no tier 2s, want people using them on soldiers
		// because they allow a diversity of preparedness there, whereas for tanks and up they can provide preparedness
		// since they can hold 2+.
		
		getFp()[6] = 110; // HLM - WTF Class Rocket Launcher .
		getAmm()[6] = 22;
		getAcc()[6] = 66;
		getFp()[7] = 110; // HML - Automatic EMP Burster 
		getAmm()[7] = 66;
		getAcc()[7] = 22;
		getFp()[8] = 66;  // MLH - EMP Grenade Launcher
		getAmm()[8] = 22;
		getAcc()[8] = 110;
		getFp()[9] = 66;  // MHL - Plasma Minigun
		getAmm()[9] = 110;
		getAcc()[9] = 22;
		getFp()[10] = 22; // LHM - Gauss Cannon
		getAmm()[10] = 110;
		getAcc()[10] = 66;
		getFp()[11] = 22; // LMH - Fully Automated Laser Drone
		getAmm()[11] = 66;
		getAcc()[11] = 110;
		
		// High is 242, Medium is 145, Low is 50... so 220+10% is 2242.
		getFp()[12] = 242; // HLM - BRTHLE
		getAmm()[12] = 50;
		getAcc()[12] = 145;
		getFp()[13] = 242; // HML - Singularity Whip
		getAmm()[13] = 145;
		getAcc()[13] = 50;
		getFp()[14] = 145; // MLH Superstring Accelerator Cannon
		getAmm()[14] = 50;
		getAcc()[14] = 242;
		getFp()[15] = 145; // MHL - QAE
		getAmm()[15] = 242;
		getAcc()[15] = 50;		
		getFp()[16] = 50; // LHM - Gauss Minigun with Antigravity
		getAmm()[16] = 242;
		getAcc()[16] = 145;
		getFp()[17] = 50; // LMH - EMP Wasp
		getAmm()[17] = 145;
		getAcc()[17] = 242;
		
		// these are bombs, so 242+145+50=437/3 = 146 for tier 3, 66 for tier 2, 27 for tier 1.
		getFp()[18] = 27; // H.I.V.E.
		getAmm()[18] = 27;
		getAcc()[18] = 27;
		getFp()[19] = 66; // The Horizon Machine
		getAmm()[19] = 66;
		getAcc()[19] = 66;		
		getFp()[20] = 146; // Focused Nova Bomb
		getAmm()[20] = 146;
		getAcc()[20] = 146;
		
		// okay now we have set up array values.
	
		
		
		int i = 0;
		setFirepower(setAccuracy(setAmmo(0)));
		while(i<getWeap().length) {
			setFirepower(getFirepower() + getFp()[getWeap()[i]]);
			setAccuracy(getAccuracy() + getAcc()[getWeap()[i]]);
			setAmmo(getAmmo() + getAmm()[getWeap()[i]]);


			i++;
		}
		// well isn't this nice, weapons all set up.
	
	}
	
	public static String returnWeaponsString(String weapNumbers) {
		/*
		 * This method expects the first entry to be like entry,entry,entry,
		 * not ,entry,entry,entry, or ,entry,entry,entry.
		 * 
		 * It returns similarly.
		 * 
		 * This method returns in comma form the names of the weapons coded in
		 * the CSV form of weapNumbers. It's hard coded here in this object because this is where
		 * weapon storage information is kept.
		 * 
		 * It returns entries in entry,entry,entry, format, which is what
		 * we normally use here and so we keep it for convention.
		 */
		String weapReturn = "";
		
		while(!weapNumbers.equals("")) {
			int currWeap = Integer.parseInt(weapNumbers.substring(0,weapNumbers.indexOf(",")));
			weapNumbers=weapNumbers.substring(weapNumbers.indexOf(",")+1,weapNumbers.length());
			switch(currWeap) {
			case 0:
				weapReturn+="Pump Action EMP,";
				break;
			case 1:
				weapReturn+="Pulverizer,";
				break;
			case 2:
				weapReturn+="Rail Gun,";
				break;
			case 3:
				weapReturn+="Plasma Rifle,";
				break;
			case 4:
				weapReturn+="Arc Thrower,";
				break;
			case 5:
				weapReturn+="Laser Rifle,";
				break;
			case 6:
				weapReturn+="WTF Class Rocket Launcher,";
				break;
			case 7:
				weapReturn+="Automatic EMP Burster,";
				break;
			case 8:
				weapReturn+="EMP Grenade Launcher,";
				break;
			case 9:
				weapReturn+="Plasma Minigun,";
				break;
			case 10:
				weapReturn+="Gauss Cannon,";
				break;
			case 11:
				weapReturn+="Fully Automated Laser Drone,";
				break;
			case 12:
				weapReturn+="BRTHLE,";
				break;
			case 13:
				weapReturn+="Singularity Whip,";
				break;
			case 14:
				weapReturn+="Superstring Accelerator Cannon,";
				break;
			case 15:
				weapReturn+="Quantum Anomaly Enabler(QAE),";
				break;
			case 16:
				weapReturn+="Gauss Minigun with Antigravity Support,";
				break;
			case 17:
				weapReturn+="EMP Wasp,";
				break;
			case 18:
				weapReturn+="H.I.V.E.,";
				break;
			case 19:
				weapReturn+="The Horizon Machine,";
				break;
			case 20:
				weapReturn+="Focused Nova Bomb,";
				break;
			default: weapReturn+="Unkown Weapon";
			}
			
			
			
			
		}
		
		return weapReturn;
		
	}
	public static void addSkinEffects(ArrayList<AttackUnit> au) {
		/*
		 * This method adds any skin effects that the units sent may have.
		 * 0: Nada, nobody loves you. ----- Standard Infantry/Tank/Juggernaught symbology
1: 5% FP, AMM, ACC increase ----- Advanced Weaponry Upgrade
2: 5% CONC, ARMOR, CARGO, SPEED increase ----- Advanced Armor Upgrade
3: 5% across the board ----- Nanotech Integration Upgrade 
4: 10% across the board and 5% more BP gained based on percentage of army make up with BP skins. ----- Morale Upgrade
5: 25% more BP gained. ----- Premium Upgrade
6: 25% Cover Size Limit deflection -----  Alamo Upgrade
7: 25% BP 25% CSL deflection 5% across the board - Superiority Upgrade
8: 50% weather resistance and 10% across the board - Impervious Upgrade
9: 25% weather resistance 25% CSL deflection and 10% across the board. - Conqueror Upgrade

Bomber Upgrades:
0: Nada, nobody loves you. ----- Standard
1: 25% increase in People Bombing ----- Genocide Upgrade 
2: 25% increase in Building Bombing ----- Devastation Upgrade
3: 15% people, 15% building bombing ----- Siege Upgrade
4: 25% building 25% people bombing ----- Armageddon Upgrade
 
		 */
		
		int i = 0; AttackUnit a;
		while(i<au.size()) {
			a = au.get(i);
			if(a.getExpmod()!=20&&a.getExpmod()!=0)
			switch(a.getGraphicNum()) {
			case 1:
				a.setFirepower((int) Math.round(1.05*((double)  a.getFirepower())));
				a.setAmmo((int) Math.round(1.05*((double)  a.getAmmo())));
				a.setAccuracy((int) Math.round(1.05*((double)  a.getAccuracy())));
				break;
			case 2:
				a.setArmor((int) Math.round(1.05*((double)  a.getArmor())));
				a.setConcealment((int) Math.round(1.05*((double)  a.getConcealment())));
				a.setSpeed((int) Math.round(1.05*((double)  a.getSpeed())));
				a.setCargo((int) Math.round(1.05*((double)  a.getCargo())));
				break;
			case 3:
				a.setFirepower((int) Math.round(1.05*((double)  a.getFirepower())));
				a.setAmmo((int) Math.round(1.05*((double)  a.getAmmo())));
				a.setAccuracy((int) Math.round(1.05*((double)  a.getAccuracy())));
				a.setArmor((int) Math.round(1.05*((double)  a.getArmor())));
				a.setConcealment((int) Math.round(1.05*((double)  a.getConcealment())));
				a.setSpeed((int) Math.round(1.05*((double)  a.getSpeed())));
				a.setCargo((int) Math.round(1.05*((double)  a.getCargo())));
				break;
			case 4:
				a.setFirepower((int) Math.round(1.1*((double)  a.getFirepower())));
				a.setAmmo((int) Math.round(1.1*((double)  a.getAmmo())));
				a.setAccuracy((int) Math.round(1.1*((double)  a.getAccuracy())));
				a.setArmor((int) Math.round(1.1*((double)  a.getArmor())));
				a.setConcealment((int) Math.round(1.1*((double)  a.getConcealment())));
				a.setSpeed((int) Math.round(1.1*((double)  a.getSpeed())));
				a.setCargo((int) Math.round(1.1*((double)  a.getCargo())));
				break;
			case 7:
				a.setFirepower((int) Math.round(1.05*((double)  a.getFirepower())));
				a.setAmmo((int) Math.round(1.05*((double)  a.getAmmo())));
				a.setAccuracy((int) Math.round(1.05*((double)  a.getAccuracy())));
				a.setArmor((int) Math.round(1.05*((double)  a.getArmor())));
				a.setConcealment((int) Math.round(1.05*((double)  a.getConcealment())));
				a.setSpeed((int) Math.round(1.05*((double)  a.getSpeed())));
				a.setCargo((int) Math.round(1.05*((double)  a.getCargo())));
				break;
			case 8:
				a.setFirepower((int) Math.round(1.1*((double)  a.getFirepower())));
				a.setAmmo((int) Math.round(1.1*((double)  a.getAmmo())));
				a.setAccuracy((int) Math.round(1.1*((double)  a.getAccuracy())));
				a.setArmor((int) Math.round(1.1*((double)  a.getArmor())));
				a.setConcealment((int) Math.round(1.1*((double)  a.getConcealment())));
				a.setSpeed((int) Math.round(1.1*((double)  a.getSpeed())));
				a.setCargo((int) Math.round(1.1*((double)  a.getCargo())));
				break;
			case 9:
				a.setFirepower((int) Math.round(1.1*((double)  a.getFirepower())));
				a.setAmmo((int) Math.round(1.1*((double)  a.getAmmo())));
				a.setAccuracy((int) Math.round(1.1*((double)  a.getAccuracy())));
				a.setArmor((int) Math.round(1.1*((double)  a.getArmor())));
				a.setConcealment((int) Math.round(1.1*((double)  a.getConcealment())));
				a.setSpeed((int) Math.round(1.1*((double)  a.getSpeed())));
				a.setCargo((int) Math.round(1.1*((double)  a.getCargo())));
				break;
			}
			
			i++;
		}
	}
	
	public static void removeEffects(ArrayList<AttackUnit> au, Player p) {
		/*
		 * This method adds any skin or weather effects that the units sent may have.
		 */
		
		int i = 0; AttackUnit a; AttackUnit pa=null;
		while(i<au.size()) {
			a = au.get(i);
			
			if(a.getSupport()==0) {
				int j = 0;
				while(j<p.getAu().size()) {
					pa = p.getAu().get(j);
					if(pa.getSlot()==a.getSlot()) break;
					j++;
				}
				
				

				
			} else {
				int j = 0;
				if(a.getOriginalPlayer()!=null)
				while(j<a.getOriginalPlayer().getAu().size()) {
					pa = a.getOriginalPlayer().getAu().get(j);
					if(pa.getOriginalSlot()==a.getOriginalSlot()) break;
					j++;
				}
			}
			try {
			a.setFirepower(pa.getFirepower());
			a.setAmmo(pa.getAmmo());
			a.setAccuracy(pa.getAccuracy());
			a.setArmor(pa.getArmor());
			a.setSpeed(pa.getSpeed());
			a.setCargo(pa.getCargo());
			a.setConcealment(pa.getConcealment());

			} catch(Exception exc) { exc.printStackTrace(); System.out.println("AU nullpointer caught when attempting to remove effects! Combat was saved! Player id was " + p.ID); }	
			i++;
		}
	}
	
	public static int getBP(ArrayList<AttackUnit> au, int originalBP) {
		/*	4: 10% across the board and 5% more BP gained based on percentage of army make up with BP skins. ----- Morale Upgrade
		5: 25% more BP gained. ----- Premium Upgrade
		6: 25% Cover Size Limit deflection -----  Alamo Upgrade
		7: 25% BP 25% CSL deflection 5% across the board - Superiority Upgrade
		8: 50% weather resistance and 10% across the board - Impervious Upgrade
		9: 25% weather resistance 25% CSL deflection and 10% across the board. - Conqueror Upgrade*/
		int i = 0; AttackUnit a; double bpmodifier=0; double size=0;
		while(i<au.size()) {
			a = au.get(i);
			if(a.getExpmod()!=20&&a.getExpmod()!=0)
				switch(a.getGraphicNum()) {
				case 4:
					bpmodifier+=(.05*a.getSize()*a.getExpmod());
					break;
				case 5:
					bpmodifier+=(.25*a.getSize()*a.getExpmod());
					break;
				case 7:
					bpmodifier+=(.25*a.getSize()*a.getExpmod());
					break;

				}
			size+=a.getSize()*a.getExpmod();
			i++;
		}
		bpmodifier/=size; // so we get it diluted by percentage makeup.
		
		return (int) Math.round(((double) originalBP)*(1.0+bpmodifier));
		
		
	}
	
	public static double getNewCSL(ArrayList<AttackUnit> au, double expTerm) {
		/*	4: 10% across the board and 5% more BP gained based on percentage of army make up with BP skins. ----- Morale Upgrade
		5: 25% more BP gained. ----- Premium Upgrade
		6: 25% Cover Size Limit deflection -----  Alamo Upgrade
		7: 25% BP 25% CSL deflection 5% across the board - Superiority Upgrade
		8: 50% weather resistance and 10% across the board - Impervious Upgrade
		9: 25% weather resistance 25% CSL deflection and 10% across the board. - Conqueror Upgrade*/
		int i = 0; AttackUnit a; double cslmodifier=0; double size=0;
		while(i<au.size()) {
			a = au.get(i);
			if(a.getExpmod()!=20&&a.getExpmod()!=0)
				switch(a.getGraphicNum()) {
				case 6:
					cslmodifier+=(.25*a.getSize()*a.getExpmod());
					break;
				case 7:
					cslmodifier+=(.25*a.getSize()*a.getExpmod());
					break;
				case 9:
					cslmodifier+=(.25*a.getSize()*a.getExpmod());
					break;
					
				}
			size+=a.getSize()*a.getExpmod();

			
			i++;
		}
		cslmodifier/=size; // so we get it diluted by percentage makeup.
		
		return expTerm+ (1.0-expTerm)*(1.0-cslmodifier); // so we take the decrease, and we reduce it by the specified amount.
		// then we add it to expTerm, so if expTerm was 70, yielding a 30 % decrease, if we cut it by 50%, we get 15% back, which we then
		// add to the 70 to get 85.
		
	}
	public static double getBombPplEffect(ArrayList<AttackUnit> au, double pplbombers) {
		/*
		 * 0: Nada, nobody loves you. ----- Standard
		1: 25% increase in People Bombing ----- Genocide Upgrade 
			2: 25% increase in Building Bombing ----- Devastation Upgrade
			3: 15% people, 15% building bombing ----- Siege Upgrade
			4: 25% building 25% people bombing ----- Armageddon Upgrade
		 */
		
		int i = 0; AttackUnit a; double bombmodifier=0; double size=0;
		while(i<au.size()) {
			a = au.get(i);
			if(a.getExpmod()==20) {
				
				switch(a.getGraphicNum()) {
					case 1:
						bombmodifier+=(.25*a.getSize());
						break;
					case 3:
						bombmodifier+=(.15*a.getSize());
						break;
					case 4:
						bombmodifier+=(.25*a.getSize());
						break;
						
				
				}
				
			}
			size+=a.getSize();

			i++;
			
		}
			bombmodifier/=size;
			
			return (int) Math.round( ((double) pplbombers)*(1.0+bombmodifier));
			
		}
	public static double getBombBldgEffect(ArrayList<AttackUnit> au, double bldgbombers) {
		/*
		 * 0: Nada, nobody loves you. ----- Standard
		1: 25% increase in People Bombing ----- Genocide Upgrade 
			2: 25% increase in Building Bombing ----- Devastation Upgrade
			3: 15% people, 15% building bombing ----- Siege Upgrade
			4: 25% building 25% people bombing ----- Armageddon Upgrade
		 */
		
		int i = 0; AttackUnit a; double bombmodifier=0; double size=0;
		while(i<au.size()) {
			a = au.get(i);
			if(a.getExpmod()==20) {
				
				switch(a.getGraphicNum()) {
					case 2:
						bombmodifier+=(.25*a.getSize());
						break;
					case 3:
						bombmodifier+=(.15*a.getSize());
						break;
					case 4:
						bombmodifier+=(.25*a.getSize());
						break;
						
				
				}
				
			}
			size+=a.getSize();

			i++;
			
		}
			bombmodifier/=size;
			
			i = 0;
			return (int) Math.round( ((double) bldgbombers)*(1.0+bombmodifier));
			
		}
	public void setExpmod(int expmod) {
		this.expmod = expmod;
	}

	public int getExpmod() {
		return expmod;
	}

	public double setSpeed(double speed) {
		this.speed = speed;
		return speed;
	}

	public double getSpeed() {
		return speed;
	}

	public void setLotNum(int lotNum) {
		this.lotNum = lotNum;
	}

	public int getLotNum() {
		return lotNum;
	}

	public void setCivType(String civType) {
		this.civType = civType;
	}

	public String getCivType() {
		return civType;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setWeap(int weap[]) {
		this.weap = weap;
	}

	public int[] getWeap() {
		return weap;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public int setPopSize(int popSize) {
		this.popSize = popSize;
		return popSize;
	}

	public int getPopSize() {
		return popSize;
	}

	public void setConcealment(double concealment) {
		this.concealment = concealment;
	}

	public double getConcealment() {
		return concealment;
	}

	public void setHp(double hp) {
		this.hp = hp;
	}

	public double getHp() {
		return hp;
	}

	public void setSupport(int support) {
		this.support = support;
	}

	public int getSupport() {
		return support;
	}

	public void setOriginalPlayer(Player originalPlayer) {
		this.originalPlayer = originalPlayer;
	}

	public Player getOriginalPlayer() {
		return originalPlayer;
	}

	public void setFirepower(double firepower) {
		this.firepower = firepower;
	}

	public double getFirepower() {
		return firepower;
	}

	public double setArmor(double armor) {
		this.armor = armor;
		return armor;
	}

	public double getArmor() {
		return armor;
	}

	public double setAccuracy(double accuracy) {
		this.accuracy = accuracy;
		return accuracy;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public double setAmmo(double ammo) {
		this.ammo = ammo;
		return ammo;
	}

	public double getAmmo() {
		return ammo;
	}

	public void setOriginalSlot(int originalSlot) {
		this.originalSlot = originalSlot;
	}

	public int getOriginalSlot() {
		return originalSlot;
	}

	public double setCargo(double cargo) {
		this.cargo = cargo;
		return cargo;
	}

	public double getCargo() {
		return cargo;
	}

	public void setOriginalTID(int originalTID) {
		this.originalTID = originalTID;
	}

	public int getOriginalTID() {
		return originalTID;
	}

	public void setGraphicNum(int graphicNum) {
		this.graphicNum = graphicNum;
	}

	public int getGraphicNum() {
		return graphicNum;
	}

	public void setDeleteMe(boolean deleteMe) {
		this.deleteMe = deleteMe;
	}

	public boolean isDeleteMe() {
		return deleteMe;
	}

	public void setFp(double fp[]) {
		this.fp = fp;
	}

	public double[] getFp() {
		return fp;
	}

	public void setAmm(double amm[]) {
		this.amm = amm;
	}

	public double[] getAmm() {
		return amm;
	}

	public void setAcc(double acc[]) {
		this.acc = acc;
	}

	public double[] getAcc() {
		return acc;
	}

	public void setEditMe(boolean editMe) {
		this.editMe = editMe;
	}

	public boolean isEditMe() {
		return editMe;
	}

	public void setAddMe(boolean addMe) {
		this.addMe = addMe;
	}

	public boolean isAddMe() {
		return addMe;
	}
}
