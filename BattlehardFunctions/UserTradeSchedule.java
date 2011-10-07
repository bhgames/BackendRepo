package BattlehardFunctions;

import java.util.UUID;



public class UserTradeSchedule {
	private long metal, 
				 timber, 
				 manmat, 
				 food,
				 othermetal, 
				 othertimber, 
				 othermanmat, 
				 otherfood;
	private int tid2,
				tid1;
	private UUID id;
	private String originatingPlayer,
			originatingTown,
			destPlayer,
			destTown;
	private double distance;
	int currTicks = 0,
		timesDone=0,
		timesToDo=1,
		intervaltime=3600; 
	UUID mateID;
	boolean agreed=false,
			caravan=false,
			finished = false,
			stockMarketTrade=false,
			twoway=false; 
	
	public UserTradeSchedule(boolean agreed, int currTicks, boolean finished, long food, 
			int intervaltime, long manmat, UUID mateID, long metal, long otherfood,
			long othermanmat, long othermetal, long othertimber, long timber,
			int timesDone, int timesToDo, int tid1, int tid2, UUID id, boolean twoway,
			String originatingTown, String originatingPlayer, String destTown,
			String destPlayer, boolean caravan) {
		
		this.agreed = agreed;
		this.currTicks = currTicks;
		this.finished = finished;
		this.food = food;
		this.tid1=tid1;
		this.tid2=tid2;
		this.intervaltime = intervaltime;
		this.manmat = manmat;
		this.mateID = mateID;
		this.metal = metal;
		this.otherfood = otherfood;
		this.othermanmat = othermanmat;
		this.othermetal = othermetal;
		this.othertimber = othertimber;
		this.timber = timber;
		this.timesDone = timesDone;
		this.caravan=caravan;
		this.timesToDo = timesToDo;
		this.setOriginatingPlayer(originatingPlayer); 
		this.setOriginatingTown(originatingTown);
		this.setDestPlayer(destPlayer); 
		this.setDestTown(destTown);
	
		if(tid1==tid2) stockMarketTrade=true;
		this.id = id;
		this.twoway = twoway;
	}
	public boolean isCaravan() {
		return caravan;
	}
	public long getMetal() {
		return metal;
	}
	public long getTimber() {
		return timber;
	}
	public long getManmat() {
		return manmat;
	}
	public long getFood() {
		return food;
	}
	public long getOthermetal() {
		return othermetal;
	}
	public long getOthertimber() {
		return othertimber;
	}
	public long getOthermanmat() {
		return othermanmat;
	}
	public long getOtherfood() {
		return otherfood;
	}
	public int getTID2() {
		return tid2;
	}
	public int getTID1() {
		return tid1;
	}
	public UUID getId() {
		return id;
	}
	public boolean isTwoway() {
		return twoway;
	}
	public int getCurrTicks() {
		return currTicks;
	}
	public int getTimesDone() {
		return timesDone;
	}
	public int getTimesToDo() {
		return timesToDo;
	}
	public UUID getMateID() {
		return mateID;
	}
	
	public int getIntervaltime() {
		return intervaltime;
	}
	public boolean isAgreed() {
		return agreed;
	}
	public boolean isFinished() {
		return finished;
	}
	public boolean isStockMarketTrade() {
		return stockMarketTrade;
	}
	public void setOriginatingPlayer(String originatingPlayer) {
		this.originatingPlayer = originatingPlayer;
	}
	public String getOriginatingPlayer() {
		return originatingPlayer;
	}
	public void setOriginatingTown(String originatingTown) {
		this.originatingTown = originatingTown;
	}
	public String getOriginatingTown() {
		return originatingTown;
	}
	public void setDestPlayer(String destPlayer) {
		this.destPlayer = destPlayer;
	}
	public String getDestPlayer() {
		return destPlayer;
	}
	public void setDestTown(String destTown) {
		this.destTown = destTown;
	}
	public String getDestTown() {
		return destTown;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public double getDistance() {
		return distance;
	}
}
