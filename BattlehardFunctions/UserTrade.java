package BattlehardFunctions;

import java.util.UUID;


public class UserTrade {
	private long metal, timber, manmat, food;
	private double distance; 
	private int ticksToHit; private int tid2 ; private int tid1; private boolean tradeOver; private UUID id; 
	 UUID tradeScheduleID; private String originatingPlayer,originatingTown,destPlayer,destTown;
	private int totalTicks=0; private int traders;
	public UserTrade(double distance, long food, long manmat, long metal,
			int ticksToHit, long timber, int totalTicks, int tid1,
			int tid2, UUID tradeID, boolean tradeOver, int traders,
			UUID tradeScheduleID, String originatingTown, String originatingPlayer, String destTown,String destPlayer) {
	
		this.distance = distance;
		this.food = food;
		this.manmat = manmat;
		this.metal = metal;
		this.ticksToHit = ticksToHit;
		this.timber = timber;
		this.totalTicks = totalTicks;
		this.tid1 = tid1;
		this.tid2 = tid2;
		this.id = tradeID;
		this.tradeOver = tradeOver;
		this.traders = traders;
		this.tradeScheduleID = tradeScheduleID;
		this.setOriginatingPlayer(originatingPlayer); this.setOriginatingTown(originatingTown);
		this.setDestPlayer(destPlayer); this.setDestTown(destTown);

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
	public double getDistance() {
		return distance;
	}
	public boolean isStockMarketTrade() {
		if(getTID1()==getTID2()) return true;
		else return false;
	}
	public int getTicksToHit() {
		return ticksToHit;
	}
	public int getTID2() {
		return tid2;
	}
	public int getTID1() {
		return tid1;
	}
	public boolean isTradeOver() {
		return tradeOver;
	}
	public UUID getId() {
		return id;
	}
	public UUID getTradeScheduleID() {
		return tradeScheduleID;
	}
	public int getTotalTicks() {
		return totalTicks;
	}
	public int getTraders() {
		return traders;
	}


	public void setOriginatingTown(String originatingTown) {
		this.originatingTown = originatingTown;
	}


	public String getOriginatingTown() {
		return originatingTown;
	}


	public void setOriginatingPlayer(String originatingPlayer) {
		this.originatingPlayer = originatingPlayer;
	}


	public String getOriginatingPlayer() {
		return originatingPlayer;
	}


	public void setDestTown(String destTown) {
		this.destTown = destTown;
	}


	public String getDestTown() {
		return destTown;
	}


	public void setDestPlayer(String destPlayer) {
		this.destPlayer = destPlayer;
	}


	public String getDestPlayer() {
		return destPlayer;
	}
}
