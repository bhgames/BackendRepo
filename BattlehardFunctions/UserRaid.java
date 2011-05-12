package BattlehardFunctions;

import java.util.UUID;


public class UserRaid {
	private String town1; private int x1; private int y1; 
	private String town2; private int x2; private int y2; private int auAmts[]; private String auNames[];
	private String raidType;
	private boolean raidOver; private double ticksToHit;
	private double distance; private long res[];
	private UUID id;
	private int tid1,tid2;
	private String[] bombTargets;
	private boolean debris;
	private int genoRounds=0;
	private int digAmt = 0;
	private String name;
	private boolean bomb;
	private boolean allClear = false;
	private int totalTicks=0;
	public UserRaid(UUID id, double distance, boolean raidOver, double ticksToHit, String town1, int x1, int y1, String town2, int x2, int y2, int auAmts[], String auNames[], String raidType,long  m, long  t, long mm, long f,boolean allClear, String[] bombTarget,
			int tid1,int tid2,String name, int genoRounds, boolean bomb, boolean debris, int digAmt) {
		this.town1=town1;this.town2=town2; this.x1=x1;this.y1=y1;
		this.genoRounds=genoRounds;
		this.digAmt=digAmt;
		this.bomb=bomb;
		this.x2=x2;this.y2=y2;this.auAmts=auAmts;this.auNames=auNames;
		this.raidType=raidType;
		this.tid1=tid1;this.tid2=tid2;
		this.name=name;
		this.raidOver=raidOver; this.ticksToHit=ticksToHit;
		this.distance=distance;
		this.debris=debris;
		this.bombTargets=bombTarget;
		 res = new long[4];
		res[0]=m;
		res[1]=t;
		res[2]=mm;
		res[3]=f;
		
		this.id=id;
		this.allClear=allClear;
		 
	}
	/**
	 * Returns the resources in a long array carried by this raid at the moment.
	 * These will be deposited in the originating town upon safe return of the raid!
	 * @return
	 */
	public long[] resources() {

		return res;

	}
	public boolean bomb() {
		return bomb;
	}
	/**
	 * Returns the number of rounds this Genocide has completed.
	 * @return
	 */
	public int getGenoRounds() {
		return genoRounds;
	}
	public String attackingTown() {
		return town1;
		
	}
	
	public String defendingTown() {
		return town2;
	}
	
	public int attackerX() {
		return x1;
	}
	public int attackerY() {
		return y1;
	}
	public int defenderX() {
		return x2;
	}
	public int defenderY() {
		return y2;
	}
	
	public int[] auAmounts() {
		return auAmts;
	}
	public String[] auNames() {
		return auNames;
	}
	
	public int getTID1() {
		return tid1;
	}
	public int getTID2() {
		return tid2;
	}
	public String raidType() {
		return raidType;
	}
	/**
	 * Returns true if the raid is returning home.
	 * @return
	 */
	public boolean raidOver() {
		return raidOver;
	}
	public String name() {
		return name;
	}
	public double eta() {
		return ticksToHit;
	}
	public double distance() {
		return distance;
	}
	public UUID id() {
		return id;
	}
	public int totalTicks() {
		return totalTicks;
	}
	
	public String[] bombTargets() {
		return bombTargets;
	}
	/**
	 * In a genocide/glassing run, this variable being true or "All Clear" means that
	 * all military units have been wiped out in the defending town and that in the next
	 * battle, the civilians will fight the intruders!
	 * @return
	 */
	
	public boolean allClear() {
		//if(!raidType.equals("glass")&&!raidType.equals("genocide")) return "N/A";
		if(allClear) return true;
		else return false;
	}

	public boolean isDebris() {
		return debris;
	}
	public String toString() {
		String returnattack = "arrive";
		if(raidOver) returnattack = "return";
		if(raidType.equals("debris")) {
			return "This is a " + " debris collection run " + " from " + town1 + " on " +town2 + " that will " + returnattack+ " in " + ticksToHit;

		} else if(getDigAmt()>0) { 
			return "This is a dig team from " + town1 + " to " + town2 + " that will " + returnattack + " in " +ticksToHit;
		}else
		return "This is a " + raidType + " from " + town1 + " on " +town2 + " that will " + returnattack+ " in " + 
		ticksToHit;
	}
	public void setDigAmt(int digAmt) {
		this.digAmt = digAmt;
	}
	public int getDigAmt() {
		return digAmt;
	}
	public void setBombTargets(String[] bombTargets) {
		this.bombTargets = bombTargets;
	}
	public String[] getBombTargets() {
		return bombTargets;
	}
}
