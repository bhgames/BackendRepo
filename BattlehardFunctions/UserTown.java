package BattlehardFunctions;

import BHEngine.Town;



public class UserTown {
	 private long res[]; private UserAttackUnit[] au; private int townID; private String townName; String playerName;  private double resInc[] = new double[5]; private int x, y;
	 private UserBuilding[] bldg; private UserBuilding[] bldgserver;
	private int totalTraders=0;    int pid; // for use with bhviewer.
	private int totalEngineers=0; private UserRaid[] attackServer; 
	private UserTrade[] tradeServer;
	private double[] resEffects;
	private int destX,destY,fuelCells,ticksTillMove;
	private boolean zeppelin;
	private int foodConsumption;
	private int CSL,CS;

	volatile long resCaps[] = new long[5];
	private UserTradeSchedule[] tradeSchedules;
	public UserTown(UserRaid[] attackServer, UserAttackUnit[] au,
			UserBuilding[] bldg, 
			int pid, String playerName, long[] res,
			long[] resCaps, double[] resInc, double[] resEffects, int totalEngineers,
			int totalTraders, int townID, String townName,
			UserTradeSchedule[] tradeSchedules, UserTrade[] tradeServer, int x,
			int y,int CSL, int CS, boolean zeppelin, int fuelCells, int destX,int destY, int ticksTillMove, int foodConsumption) {
		this.attackServer = attackServer;
		this.au = au;
		this.bldg = bldg;
		this.CSL=CSL; this.CS=CS;
		this.foodConsumption=foodConsumption;
		this.zeppelin=zeppelin; this.fuelCells=fuelCells; this.destX=destX; this.destY=destY; this.ticksTillMove=ticksTillMove;
		this.pid = pid;
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
	
}