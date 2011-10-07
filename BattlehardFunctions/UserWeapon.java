package BattlehardFunctions;

/**
 * @deprecated class for the old unit weapons used with Unit Template Creation.
 * 
 * Kept around for posterity and amusement, mostly.
 * 
 * Contains all the information about a weapon, including it's firepower (which beats
 * armor), it's accuracy(which beats concealment), and ammunition(which beats
 * speed.)
 */
public class UserWeapon {
	
	double fp;
	double amm;
	double acc;
	String name;
	String desc;
	
	public UserWeapon(double fp, double amm, double acc, String name, int index) {
	
		this.acc = acc;
		this.amm = amm;
		this.fp = fp;
		this.name = name;
		desc = returnWeapDesc()[index];
	}
	public double getFp() {
		return fp;
	}
	public double getAmm() {
		return amm;
	}
	public double getAcc() {
		return acc;
	}
	public String getDesc() {
		return desc;
	}
	
	public static String[] returnWeapDesc() {
		String weapDesc[] = new String[21];
		
		weapDesc[0] = "The Pump-Action EMP Burster fires bursts of EMP pulses via a pump-action mechanism.";
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
	public String getName() {
		return name;
	}

}
