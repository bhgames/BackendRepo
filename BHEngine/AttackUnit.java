/*
 * This unit holds all the data about a single attack unit type.
 */
package BHEngine;

import java.util.ArrayList;

import BattlehardFunctions.UserAttackUnit;

public class AttackUnit {
private	double armorType, armor, cargo, speed;
	
	// Weapons are hardcoded into attack unit data as double arrays to save
	// processing time. There are a limited amount of weapons.



private int attackType;
private	double attackDamage=0;
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
private int type;
private	int weap[];
volatile private	int size; // Optional, can be set if you want AttackUnit to store the number
	// of this type in a raid/attack. This class can also be used to simply
	// describe a general unit type also.
private	String name; 
private	int slot; // Which slot this unit is stored in.
private	int lotNum=-1; // For the civilians.
private	String civType = "None";
private int lvl;
public static int soldierHP=100,tankHP=500,juggerHP=1000,bomberHP=4000,civilianHP=75,
soldierExpMod=1,tankExpMod=10,juggerExpMod=40,bomberExpMod=20,civilianExpMod=1, soldierPop=1,
tankPop=5,juggerPop=10,bomberPop=20,civilianPop=1,soldierPoints=400,tankPoints=800,juggerPoints=1600,bomberPoints=200,
tier1=100,tier2=200,tier3=400,tier4=100;


public int getLvl() {
	return lvl;
}public void setLvl(int lvl) {
	this.lvl=lvl;
}
/**
 * Returns the modifier to the damage dealt based on this unit's armor type and the other unit's damage type.
 * Returns 1.25, for instance, if you want 25% more damage.
 * @param a
 * @return
 */
	public double getArmorModifier(AttackUnit a, Player asP, Player myP) {
		
		/*
		 * Attack Type(1 Physical 2 Explosive 3 Electric)

			Armor Type(1 Light 2 Heavy 3 Building 4 Civilian)
 

			Armor types:
			
			Civilian
			Standard for Civilians
			No special damage mitigation
			2x explosive
			
			Light
			Standard for infantry, air, or scout/recon type units
			1/2 electrical damage
			2x explosive
			
			Heavy
			Standard for Golems and Tanks
			2x electrical
			1/2 explosive damage
			
			Building
			Standard for buildings
			1/4 physical damage and 1/4 electrical damage


		 */
		int damType = a.getAttackType();
		double mod = 1;
		int armorType = (int) getArmorType();
		switch(armorType) {
			case 1:
				// light

				switch(damType) {
					case 1:
						//physical
						
						mod *=(1+.025*asP.getFirearmResearch());
						break;
					case 2:
						//explosive
						if(myP.getBloodMetalArmor()) mod*=1.5;

						mod *=(1+.025*asP.getOrdinanceResearch());
						mod*=2;
						break;
					case 3:
						//electric
						if(myP.getBloodMetalArmor()) mod*=.75;

						mod *=(1+.025*asP.getTeslaTech());

						mod*=.5;
						break;
				}
				break;
			case 2:
				// heavy
				switch(damType) {
				case 1:
					//physical
					mod *=(1+.025*asP.getFirearmResearch());

					break;
				case 2:
					//explosive
					if(myP.isPersonalShields()) mod*=.75;

					mod *=(1+.025*asP.getOrdinanceResearch());

					mod*=.5;
					break;
				case 3:
					//electric
					if(myP.isPersonalShields()) mod*=1.5;

					mod *=(1+.025*asP.getTeslaTech());
					mod*=2;
					break;
			}
				break;
			case 3:
				// building
				switch(damType) {
				case 1:
					//physical
					mod *=(1+.025*asP.getFirearmResearch());

					mod*=.25;
					break;
				case 2:
					//explosive
					mod *=(1+.025*asP.getOrdinanceResearch());

					break;
				case 3:
					//electric
					mod *=(1+.025*asP.getTeslaTech());

					mod*=.25;
					break;
			}
				break;
			case 4:
				// civilian
				switch(damType) {
				case 1:
					//physical
					mod *=(1+.025*asP.getFirearmResearch());

					break;
				case 2:
					//explosive
					mod *=(1+.025*asP.getOrdinanceResearch());

					mod*=2;
					break;
				case 3:
					mod *=(1+.025*asP.getTeslaTech());

					//electric
					break;
			}
				break;
		
		}
		return mod;
		
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
	
	
	public double getArmorType() {
		return armorType;
	}
	public static void setValues(UserAttackUnit a) {
		String name = a.getName();
		int lvl = a.getLvl();
		int hp=0,attackDamage=0,attackType=1;
		double armor=0,armorType=0,speed=0,cargo=0,expmod=1,type=0;
		if(name.equals("Archaeologist")||
				name.equals("Civilian")){
			hp = 30;
			attackDamage=15;
			attackType=1; //physical
			armor=0;
			armorType=4; //civilian
			speed=50;
			cargo=0;
			expmod=1;
			type=1;
		} else if(name.equals("Pillager")||name.equals("Iddite")) {
			/*
			 * HP: 50
				Attack Damage: 25
				Attack Type: Physical
				Armor Level: 15
				Armor Type: Light
				Speed: 60
				Cargo: 200
			 */
			hp = 50;
			attackDamage=25;
			attackType=1; //physical
			armor=15;
			armorType=1; //light
			speed=60;
			cargo=200;
			expmod=1;
			type=1;

		} else if(name.equals("Panzerfaust")) {
			/*
			 * HP: 75
				Attack Damage: 20
				Attack Type: Electrical
				Armor Level: 10
				Armor Type: Light
				Speed: 50
				Cargo: 20
			 */
			hp = 75;
			attackDamage=20;
			attackType=3; //electric
			armor=10;
			armorType=1; //light
			speed=50;
			cargo=20;
			expmod=1;
			type=1;


		}else if(name.equals("Vanguard")) {
			/*
			 *HP: 50
				Attack Damage: 20
				Attack Type: Explosive
				Armor Level: 10
				Armor Type: Light
				Speed: 20
				Cargo: 50
			 */
			hp = 50;
			attackDamage=20;
			attackType=2; //explosive
			armor=10;
			armorType=1; //light
			speed=20;
			cargo=50;
			expmod=1;
			type=1;


		}else if(name.equals("Seeker")) {
			/*
			 *HP: 150
				Attack Damage: 390
				Attack Type: Physical
				Armor Level: 100
				Armor Type: Heavy
				Speed: 200
				Cargo: 400 
			 */
			hp = 150;
			attackDamage=390;
			attackType=1; //physical
			armor=100;
			armorType=2; //heavy
			speed=200;
			cargo=400;
			expmod=14;
			type=2;

		}else if(name.equals("Damascus")) {
			/*
			HP: 150
			Attack Damage: 390
			Attack Type: Electrical
			Armor Level: 100
			Armor Type: Heavy
			Speed: 150
			Cargo: 250
			 */
			hp = 150;
			attackDamage=390;
			attackType=3; //electric
			armor=100;
			armorType=2; //heavy
			speed=150;
			cargo=250;
			expmod=14;
			type=2;

		}else if(name.equals("Wolverine")) {
			/*
			HP: 200
			Attack Damage: 390
			Attack Type: Explosive
			Armor Level: 100
			Armor Type: Heavy
			Speed: 100
			Cargo: 100
			 */
			hp = 200;
			attackDamage=390;
			attackType=2; //explosive
			armor=100;
			armorType=2; //heavy
			speed=100;
			cargo=100;
			expmod=17;
			type=2;

		}else if(name.equals("Punisher")) {
			/*
			HP: 700
			Attack Damage: 1430
			Attack Type: Physical
			Armor Level: 300
			Armor Type: Heavy
			Speed: 500
			Cargo: 1000
			 */
			hp = 700;
			attackDamage=1430;
			attackType=1; //physical
			armor=300;
			armorType=2; //heavy
			speed=500;
			cargo=1000;
			expmod=55;
			type=3;

		}else if(name.equals("Dreadnaught")) {
			/*
			HP: 700
			Attack Damage: 1430
			Attack Type: Electrical
			Armor Level: 300
			Armor Type: Heavy
			Speed: 300
			Cargo: 600
			 */
			hp = 700;
			attackDamage=1430;
			attackType=3; //electrical
			armor=300;
			armorType=2; //heavy
			speed=300;
			cargo=600;
			expmod=55;
			type=3;

		}
		else if(name.equals("Collossus")) {
			/*
			HP: 900
			Attack Damage: 1430
			Attack Type: Explosive
			Armor Level: 200
			Armor Type: Heavy
			Speed: 250
			Cargo: 400

			 */
			hp = 900;
			attackDamage=1430;
			attackType=2; //explosive
			armor=200;
			armorType=2; //heavy
			speed=250;
			cargo=400;
			expmod=61;
			type=3;

		}else if(name.contains("Gunship")) {
			/*
			HP: 50
			Attack Damage: 75
			Attack Type: Physical
			Armor Level: 10
			Armor Type: Light
			Speed: 500
			Cargo: 0

			 */
			name = "LA-513 Gunship";
			hp = 50;
			attackDamage=75;
			attackType=1; //physical
			armor=10;
			armorType=1; //light
			speed=500;
			cargo=0;
			expmod=1;
			type=4;

		}else if(name.contains("Thunderbolt")) {
			/*
			HP: 50
			Attack Damage: 75
			Attack Type: Electrical
			Armor Level: 10
			Armor Type: Light
			Speed: 500
			Cargo: 0

			 */
			name = "LA-616 Thunderbolt";
			hp = 50;
			attackDamage=75;
			attackType=3; //electrical
			armor=10;
			armorType=1; //light
			speed=500;
			cargo=0;
			expmod=1;
			type=4;

		}else if(name.contains("Blastmaster")) {
			/*
			HP: 50
			Attack Damage: 75
			Attack Type: Explosive
			Armor Level: 10
			Armor Type: Light
			Speed: 500
			Cargo: 0

			 */
			name = "LA-293 Blastmaster";
			hp = 50;
			attackDamage=75;
			attackType=2; //explosive
			armor=10;
			armorType=1; //light
			speed=500;
			cargo=0;
			expmod=1;
			type=4;

		}else if(name.contains("Monolith")) {
			/*
			HP: 75
			Attack Damage: 50
			Attack Type: Physical
			Armor Level: 25
			Armor Type: Heavy
			Speed: 300
			Cargo: 0

			 */
			name = "HA-44 Monolith";
			hp = 75;
			attackDamage=50;
			attackType=1; //physical
			armor=25;
			armorType=2; //heavy
			speed=300;
			cargo=0;
			expmod=1;
			type=4;

		}else if(name.contains("Halcyon")) {
			/*
			HP: 75
			Attack Damage: 50
			Attack Type: Electrical
			Armor Level: 25
			Armor Type: Heavy
			Speed: 300
			Cargo: 0

			 */
			name="HA-18 Halcyon";
			hp = 75;
			attackDamage=50;
			attackType=3; //electrical
			armor=25;
			armorType=2; //heavy
			speed=300;
			cargo=0;
			expmod=1;
			type=4;

		}else if(name.contains("Hades")) {
			/*
			HP: 75
			Attack Damage: 50
			Attack Type: Explosive
			Armor Level: 25
			Armor Type: Heavy
			Speed: 300
			Cargo: 0

			 */
			name = "HA-69 Hades";
			hp = 75;
			attackDamage=50;
			attackType=2; //explosive
			armor=25;
			armorType=2; //heavy
			speed=300;
			cargo=0;
			expmod=1;
			type=4;

		}else if(name.equals("Command Center")||
				name.equals("Fortification")||
				name.equals("Metal Mine")||
				name.equals("Timber Field")||
				name.equals("Crystal Mine")||
				name.equals("Farm")||
				name.equals("Resource Cache")) {
			
			hp = 1500*lvl;
			attackDamage=0;
			attackType=0; //none
			//Armor : (1500*lvl*hardMod)+(1500*lvl*stabMod)
			armor=1500*lvl*1;
			armorType=3; //building
			speed=0;
			cargo=0;
			expmod=1; // in comparison to other buildings, all the same size.
			type=5; // type 5 is buildings.

		}else {
			
			hp = 1500*lvl;
			attackDamage=0;
			attackType=0; //none
			armor=0;
			armorType=3; //building
			speed=0;
			cargo=0;
			expmod=1; // in comparison to other buildings, all the same size.
			type=5; // type 5 is buildings.

		}
		a.setHp(hp);
		a.setAttackDamage(attackDamage);
		a.setAttackType(attackType);
		a.setArmor(armor);
		a.setArmorType(armorType);
		a.setSpeed(speed);
		a.setCargo(cargo);
		a.setExpmod(expmod);
		a.setType(type);

		
	}
	
	public void setValues() {
		if(name.equals("Archaeologist")
				||name.equals("Civilian")){
			hp = 30;
			attackDamage=15;
			attackType=1; //physical
			armor=0;
			armorType=4; //civilian
			speed=50;
			cargo=0;
			expmod=1;
			type=1;
		} else if(name.equals("Pillager")||name.equals("Iddite")) {
			/*
			 * HP: 50
				Attack Damage: 25
				Attack Type: Physical
				Armor Level: 15
				Armor Type: Light
				Speed: 60
				Cargo: 200
			 */
			hp = 50;
			attackDamage=25;
			attackType=1; //physical
			armor=15;
			armorType=1; //light
			speed=60;
			cargo=200;
			expmod=1;
			type=1;

		} else if(name.equals("Panzerfaust")) {
			/*
			 * HP: 75
				Attack Damage: 20
				Attack Type: Electrical
				Armor Level: 10
				Armor Type: Light
				Speed: 50
				Cargo: 20
			 */
			hp = 75;
			attackDamage=20;
			attackType=3; //electric
			armor=10;
			armorType=1; //light
			speed=50;
			cargo=20;
			expmod=1;
			type=1;


		}else if(name.equals("Vanguard")) {
			/*
			 *HP: 50
				Attack Damage: 20
				Attack Type: Explosive
				Armor Level: 10
				Armor Type: Light
				Speed: 20
				Cargo: 50
			 */
			hp = 50;
			attackDamage=20;
			attackType=2; //explosive
			armor=10;
			armorType=1; //light
			speed=20;
			cargo=50;
			expmod=1;
			type=1;


		}else if(name.equals("Seeker")) {
			/*
			 *HP: 150
				Attack Damage: 390
				Attack Type: Physical
				Armor Level: 100
				Armor Type: Heavy
				Speed: 200
				Cargo: 400 
			 */
			hp = 150;
			attackDamage=390;
			attackType=1; //physical
			armor=100;
			armorType=2; //heavy
			speed=200;
			cargo=400;
			expmod=14;
			type=2;

		}else if(name.equals("Damascus")) {
			/*
			HP: 150
			Attack Damage: 390
			Attack Type: Electrical
			Armor Level: 100
			Armor Type: Heavy
			Speed: 150
			Cargo: 250
			 */
			hp = 150;
			attackDamage=390;
			attackType=3; //electric
			armor=100;
			armorType=2; //heavy
			speed=150;
			cargo=250;
			expmod=14;
			type=2;

		}else if(name.equals("Wolverine")) {
			/*
			HP: 200
			Attack Damage: 390
			Attack Type: Explosive
			Armor Level: 100
			Armor Type: Heavy
			Speed: 100
			Cargo: 100
			 */
			hp = 200;
			attackDamage=390;
			attackType=2; //explosive
			armor=100;
			armorType=2; //heavy
			speed=100;
			cargo=100;
			expmod=17;
			type=2;

		}else if(name.equals("Punisher")) {
			/*
			HP: 700
			Attack Damage: 1430
			Attack Type: Physical
			Armor Level: 300
			Armor Type: Heavy
			Speed: 500
			Cargo: 1000
			 */
			hp = 700;
			attackDamage=1430;
			attackType=1; //physical
			armor=300;
			armorType=2; //heavy
			speed=500;
			cargo=1000;
			expmod=55;
			type=3;

		}else if(name.equals("Dreadnaught")) {
			/*
			HP: 700
			Attack Damage: 1430
			Attack Type: Electrical
			Armor Level: 300
			Armor Type: Heavy
			Speed: 300
			Cargo: 600
			 */
			hp = 700;
			attackDamage=1430;
			attackType=3; //electrical
			armor=300;
			armorType=2; //heavy
			speed=300;
			cargo=600;
			expmod=55;
			type=3;

		}
		else if(name.equals("Collossus")) {
			/*
			HP: 900
			Attack Damage: 1430
			Attack Type: Explosive
			Armor Level: 200
			Armor Type: Heavy
			Speed: 250
			Cargo: 400

			 */
			hp = 900;
			attackDamage=1430;
			attackType=2; //explosive
			armor=200;
			armorType=2; //heavy
			speed=250;
			cargo=400;
			expmod=61;
			type=3;

		}else if(name.contains("Gunship")) {
			/*
			HP: 50
			Attack Damage: 75
			Attack Type: Physical
			Armor Level: 10
			Armor Type: Light
			Speed: 500
			Cargo: 0

			 */
			name = "LA-513 Gunship";
			hp = 50;
			attackDamage=75;
			attackType=1; //physical
			armor=10;
			armorType=1; //light
			speed=500;
			cargo=0;
			expmod=1;
			type=4;

		}else if(name.contains("Thunderbolt")) {
			/*
			HP: 50
			Attack Damage: 75
			Attack Type: Electrical
			Armor Level: 10
			Armor Type: Light
			Speed: 500
			Cargo: 0

			 */
			name = "LA-616 Thunderbolt";
			hp = 50;
			attackDamage=75;
			attackType=3; //electrical
			armor=10;
			armorType=1; //light
			speed=500;
			cargo=0;
			expmod=1;
			type=4;

		}else if(name.contains("Blastmaster")) {
			/*
			HP: 50
			Attack Damage: 75
			Attack Type: Explosive
			Armor Level: 10
			Armor Type: Light
			Speed: 500
			Cargo: 0

			 */
			name = "LA-293 Blastmaster";
			hp = 50;
			attackDamage=75;
			attackType=2; //explosive
			armor=10;
			armorType=1; //light
			speed=500;
			cargo=0;
			expmod=1;
			type=4;

		}else if(name.contains("Monolith")) {
			/*
			HP: 75
			Attack Damage: 50
			Attack Type: Physical
			Armor Level: 25
			Armor Type: Heavy
			Speed: 300
			Cargo: 0

			 */
			name = "HA-44 Monolith";
			hp = 75;
			attackDamage=50;
			attackType=1; //physical
			armor=25;
			armorType=2; //heavy
			speed=300;
			cargo=0;
			expmod=1;
			type=4;

		}else if(name.contains("Halcyon")) {
			/*
			HP: 75
			Attack Damage: 50
			Attack Type: Electrical
			Armor Level: 25
			Armor Type: Heavy
			Speed: 300
			Cargo: 0

			 */
			name="HA-18 Halcyon";
			hp = 75;
			attackDamage=50;
			attackType=3; //electrical
			armor=25;
			armorType=2; //heavy
			speed=300;
			cargo=0;
			expmod=1;
			type=4;

		}else if(name.contains("Hades")) {
			/*
			HP: 75
			Attack Damage: 50
			Attack Type: Explosive
			Armor Level: 25
			Armor Type: Heavy
			Speed: 300
			Cargo: 0

			 */
			name = "HA-69 Hades";
			hp = 75;
			attackDamage=50;
			attackType=2; //explosive
			armor=25;
			armorType=2; //heavy
			speed=300;
			cargo=0;
			expmod=1;
			type=4;

		}else if(name.equals("Command Center")||
				name.equals("Fortification")||
				name.equals("Metal Mine")||
				name.equals("Timber Field")||
				name.equals("Crystal Mine")||
				name.equals("Farm")||
				name.equals("Resource Cache")) {
			
			hp = 1500*lvl;
			attackDamage=0;
			attackType=0; //none
			//Armor : (1500*lvl*hardMod)+(1500*lvl*stabMod)
			armor=1500*lvl*1;
			armorType=3; //building
			speed=0;
			cargo=0;
			expmod=1; // in comparison to other buildings, all the same size.
			type=5; // type 5 is buildings.

		}else {
			
			hp = 1500*lvl;
			attackDamage=0;
			attackType=0; //none
			armor=0;
			armorType=3; //building
			speed=0;
			cargo=0;
			expmod=1; // in comparison to other buildings, all the same size.
			type=5; // type 5 is buildings.

		}
		
	}

	public void setArmorType(double armorType) {
		this.armorType = armorType;
	}


	public int getAttackType() {
		return attackType;
	}


	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}


	public double getAttackDamage() {
		return attackDamage;
	}


	public void setAttackDamage(double attackDamage) {
		this.attackDamage = attackDamage;
	}


	public void changeType(int type) {
		switch(type) {
		case 1:
			setHp(soldierHP); 
			setExpmod(soldierExpMod);
			break;
		case 2:
			setHp(tankHP); 
			setExpmod(tankExpMod);
			break;
		case 3:
			setHp(juggerHP);
			setExpmod(juggerExpMod);
			break;
		case 4:
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
	public AttackUnit(String name, int slot, int lvl) { // why is this here?
	//	if(name.equals("locked")) { setConcealment(this.setArmor(this.setCargo(this.setSpeed(1)))); } else {
		
	
		this.setName(name);
		this.lvl=lvl;
		setValues();
		this.setSlot(slot); 
	}
	

	

	public AttackUnit(String name,  int lotNum, String civType) {
		this.setName(name);
		this.lotNum=lotNum;
		this.civType=civType;
		setValues();
	}
	
	

	public String toString() {
		return getName();
	}
	public AttackUnit returnCopy() {
	
		AttackUnit copy = new AttackUnit(getName(), getSlot(),lvl);
		if(getSupport()==1) copy.makeSupportUnit(getOriginalSlot(),getOriginalPlayer(),getOriginalTID());
		else if(getSupport()==2) copy.makeOffSupportUnit(getOriginalSlot(),getOriginalPlayer(),getOriginalTID());
		// so it'll copy support units correctly!
		return copy;
		
		
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
	
	
	public static int getBP(ArrayList<AttackUnit> au, int originalBP) {
		/*	4: 10% across the board and 5% more BP gained based on percentage of army make up with BP skins. ----- Morale Upgrade
		5: 25% more BP gained. ----- Premium Upgrade
		6: 25% Cover Size Limit deflection -----  Alamo Upgrade
		7: 25% BP 25% CSL deflection 5% across the board - Superiority Upgrade
		8: 50% weather resistance and 10% across the board - Impervious Upgrade
		9: 25% weather resistance 25% CSL deflection and 10% across the board. - Conqueror Upgrade*/
		int i = 0; AttackUnit a; double bpmodifier=0; double size=0;
		
		
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
		
		
		return expTerm+ (1.0-expTerm)*(1.0-cslmodifier); // so we take the decrease, and we reduce it by the specified amount.
		// then we add it to expTerm, so if expTerm was 70, yielding a 30 % decrease, if we cut it by 50%, we get 15% back, which we then
		// add to the 70 to get 85.
		
	}
	public static double getBombBldgEffect(ArrayList<AttackUnit> au, double pplbombers) {
		int i = 0; AttackUnit a; double bombmodifier=0; double size=0;

		return (int) Math.round( ((double) pplbombers)*(1.0+bombmodifier));

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
		
			
			return (int) Math.round( ((double) pplbombers)*(1.0+bombmodifier));
			
		}
	/*public static double getBombBldgEffect(ArrayList<AttackUnit> au, double bldgbombers) {
	
		
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
			
		}*/
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
	public double getTrueSpeed(Player myP) { // only used now for eta calculations...
		if(support>0) myP = getOriginalPlayer();
		if((getType()==3&&myP.isHydraulicAssistors())
				||(getType()==4&&myP.isThrustVectoring()))
			return speed*1.5;
		else if(getType()==1&&myP.isClockworkAugments()) return speed*1.25;
		else
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

	

	

	public void setHp(double hp) {
		this.hp = hp;
	}

	public double getHp() {
		return hp;
	}
	public double getTrueHp(Player p) {
		if(support!=0) p=getOriginalPlayer();
		if(getType()==1&&p.isClockworkAugments()) return hp*1.25;
		else
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

	

	public double setArmor(double armor) {
		this.armor = armor;
		return armor;
	}

	public double getArmor() {
		return armor;
	}

	public double getTrueArmor(Player p) {
		double mod  =1;
		switch((int) getArmorType()) {
		case 1:
			//light
			mod*=(1+.025*p.getBodyArmor());

			break;
		case 2:
			//heavy
			mod*=(1+.025*p.getBloodMetalPlating());
			break;
		case 3:
			mod*=(1+.025*p.getStructuralIntegrity());
			//building
			break;
		case 4:
			//civilian
			break;
		}
		return armor;
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

	

	public void setDeleteMe(boolean deleteMe) {
		this.deleteMe = deleteMe;
	}

	public boolean isDeleteMe() {
		return deleteMe;
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


	public int getType() {
		// TODO Auto-generated method stub
		return type;
	}
}
