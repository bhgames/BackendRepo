package BattlehardFunctions;


public class UserTPR {
	double taxRate;
	int pid;
	String rank;
	int tprID;
	int tids[];
	String player;
	String league;
	int type;
	public UserTPR(String league, int pid, String player, String rank,
			double taxRate, int[] tids, int tprID, int type) {
		super();
		this.league = league;
		this.pid = pid;
		this.player = player;
		this.rank = rank;
		this.taxRate = taxRate;
		this.tids = tids;
		this.tprID = tprID;
		this.type = type;
	}
	public double getTaxRate() {
		return taxRate;
	}
	public int getPid() {
		return pid;
	}
	public String getRank() {
		return rank;
	}
	public int getTprID() {
		return tprID;
	}
	public int[] getTids() {
		return tids;
	}
	public String getPlayer() {
		return player;
	}
	public String getLeague() {
		return league;
	}
	public int getType() {
		return type;
	}
}
