package BattlehardFunctions;

import java.sql.Timestamp;

import BHEngine.Town;



public class UserTown {
	 private long res[]; private UserAttackUnit[] au; private int townID; private String townName; String playerName;  private double resInc[] = new double[5]; private int x, y;
	 private UserBuilding[] bldg; private UserBuilding[] bldgserver;
	private int totalTraders=0;    int pid; // for use with bhviewer.
	private int totalEngineers=0; private UserRaid[] attackServer; 
	private UserTrade[] tradeServer;
	private double[] resEffects;
	private int destX,destY,fuelCells,ticksTillMove;
	private boolean zeppelin, resourceOutcropping;
	private int foodConsumption;
	private int lord=0;
	private double taxRate=0;
	private Timestamp vassalFrom;
	private int CSL,CS,influence;

	volatile long resCaps[] = new long[5];
	private UserTradeSchedule[] tradeSchedules;
	public UserTown(UserRaid[] attackServer, UserAttackUnit[] au,
			UserBuilding[] bldg, 
			int pid, String playerName, long[] res,
			long[] resCaps, double[] resInc, double[] resEffects, int totalEngineers,
			int totalTraders, int townID, String townName,
			UserTradeSchedule[] tradeSchedules, UserTrade[] tradeServer, int x,
			int y,int CSL, int CS, boolean zeppelin, int fuelCells, int destX,int destY, int ticksTillMove, int foodConsumption, double taxRate, int lord, Timestamp vassalFrom, int influence, boolean resourceOutcropping) {
		this.attackServer = attackServer;
		this.au = au;
		this.bldg = bldg;
		this.CSL=CSL; this.CS=CS;
		this.taxRate=taxRate;
		this.lord=lord;
		this.resourceOutcropping=resourceOutcropping;
		this.foodConsumption=foodConsumption;
		this.zeppelin=zeppelin; this.fuelCells=fuelCells; this.destX=destX; this.destY=destY; this.ticksTillMove=ticksTillMove;
		this.pid = pid;
		this.vassalFrom = new Timestamp(vassalFrom.getTime()); // must be copied.
		
		this.playerName = playerName;
		this.res = res;
		this.resCaps = resCaps;
		this.resEffects=resEffects;
		this.resInc = resInc;
		this.totalEngineers = totalEngineers;
		this.totalTraders = totalTraders;
		this.townID = townID;
		this.townName = townName;
		this.tradeSchedules = tradeSchedules;
		this.tradeServer = tradeServer;
		this.x = x;
		this.y = y;
	}
	public UserTown(UserAttackUnit[] supportAU,String playerName,String townName,int townID, int pid) {
		this.au=supportAU;
		this.playerName=playerName;
		this.townID=townID;
		this.townName=townName;
		this.pid=pid;
		// This means we want only the support town stuff.
	}
	/**
	 * Returns an array representing town resources.
	 * 0 - Metal
	 * 1 - Timber
	 * 2 - Manufactured Materials
	 * 3 - Food
	 * 4 - Town Civilian Population
	 * @return
	 */
	public long[] getRes() {
		return res;
	}
	public double[] getResEffects() {
		return resEffects;
	}
	public UserAttackUnit[] getAu() {
		return au;
	}
	public int getTownID() {
		return townID;
	}
	public String getTownName() {
		return townName;
	}
	public String getPlayerName() {
		return playerName;
	}
	public double[] getResInc() {
		return resInc;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getDestX() {
		return destX;
	}

	public UserBuilding[] getBldg() {
		return bldg;
	}
	public UserBuilding[] getBldgserver() {
		return bldgserver;
	}
	public int getTotalTraders() {
		return totalTraders;
	}
	public int getPid() {
		return pid;
	}
	
	public int getTotalEngineers() {
		return totalEngineers;
	}

	public int getFuelCells() {
		return fuelCells;
	}
	public int getTicksTillMove() {
		return ticksTillMove;
	}
	public boolean isZeppelin() {
		return zeppelin;
	}
	public UserRaid[] getAttackServer() {
		return attackServer;
	}
	
	public UserTrade[] getTradeServer() {
		return tradeServer;
	}
	public long[] getResCaps() {
		return resCaps;
	}
	public int getCSL() {
		return CSL;
	}
	public int getCS() {
		return CS;
	}
	public int getDestY() {
		return destY;
	}
	public void setDestY(int destY) {
		this.destY = destY;
	}
	public UserTradeSchedule[] getTradeSchedules() {
		return tradeSchedules;
	}

	public int getFoodConsumption() {
		return foodConsumption;
	}
	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}
	public double getTaxRate() {
		return taxRate;
	}
	public void setLord(int lord) {
		this.lord = lord;
	}
	/**
	 * If this town is under the influence of another player, this function will return that player's
	 * pid. If the player who owns this town is a vassal of a player(The lord), this town will only return the pid
	 * of the lord if the lord actually has influence over the town, and doesn't just have tax control over it
	 * because of player-level vassalage.(ie, if you own 10 towns, and are a vassal to a player because he has
	 * influence over 6 of them. The other 4 towns would have 0 as the pid of the lord, because they are not
	 * influenced, but are still taxed under the vassalage pact.) 
	 * 
	 * Returns 0 if the town has no lord.
	 * @return
	 */
	public int getLord() {
		return lord;
	}
	public void setVassalFrom(Timestamp vassalFrom) {
		this.vassalFrom = vassalFrom;
	}
	public Timestamp getVassalFrom() {
		return vassalFrom;
	}
	public void setInfluence(int influence) {
		this.influence = influence;
	}
	public int getInfluence() {
		return influence;
	}
	public void setResourceOutcropping(boolean resourceOutcropping) {
		this.resourceOutcropping = resourceOutcropping;
	}
	public boolean isResourceOutcropping() {
		return resourceOutcropping;
	}
	
}