package BattlehardFunctions;

import java.util.ArrayList;



public class UserPlayer {
	private String username;
	private int ID;
	boolean isLeague=false;
	private int brkthrus = 0;
	private int capitaltid=-1;
	private int bp;
	private int commsCenterTech;
	public int playedTicks=0;
	private int premiumTimer=0;
	private int ubTimer=0;
	private int mineTimer=0;
	private int timberTimer=0;
	private int manMatTimer=0;
	private int scoutTech;
	private int totalBPEarned=0;
	private int knowledge;	
	private boolean zeppTech,missileSiloTech,recyclingTech,metalRefTech,timberRefTech,manMatRefTech,foodRefTech;
	private boolean attackAPI, advancedAttackAPI, tradingAPI,advancedTradingAPI,smAPI,researchAPI,buildingAPI,advancedBuildingAPI,messagingAPI,zeppelinAPI,completeAnalyticAPI,nukeAPI,worldMapAPI;

	private int foodTimer=0;
	private int feroTimer=0;
	private int brkups = 0; // not to be confused with lvlUps, this is increased every time
	//a  breakthrough occurs, and is decreased whenever you use one and make four researches.
	// You do not queue up breakthroughs like buildings, you are always using it. So uh...sorry
	// if it's confusing!
	private int scholTicks = 0;
	private int afTech;
	private int scholTicksTotal;
	private int stealthTech; private int buildingSlotTech;
	private int totalScholars=0,totalPopulation=0;
	private int lotTech = 18;	 private int aLotTech;
	private boolean soldierTech;
	private boolean tankTech;
	private boolean juggerTech;
	private boolean bomberTech;
	private boolean soldierPicTech[],tankPicTech[],juggerPicTech[],bomberPicTech[];
	private UserAttackUnit[] AUTemplates;
	private int tradeTech=1; // MAX IS TEN! Well, doesn't have to be...
	private int bunkerTech=1;
	private int stabilityTech = 1;
	private boolean weap[];
	private UserAttackUnit[] au; // To keep a private player-held list of home aus.
	private int supportTech;
	private int civWeapChoice = 0; // default value.
	private String league;
	private int revTimer;
	private int townTech;
	private int engTech,scholTech;
	private String email;
	// the playerside.
	// current player read goes metal, timber, mm, food, stealthTech, totalPop,alotTech,soldierTech,tank,jugger,weaps...I think.

	private UserTown[] towns;
	
	
	public UserPlayer(UserAttackUnit[] templates, int id, int lotTech,
			int afTech, UserAttackUnit[] au, boolean[] bomberPicTech,
			boolean bomberTech, int knowledge, int buildingSlotTech,
			int bunkerTech, int civWeapChoice, int engTech, boolean isLeague,
			boolean[] juggerPicTech, boolean juggerTech, String league,
			 int scholTech, int scholTicks, int scholTicksTotal,
			boolean[] soldierPicTech, boolean soldierTech, int stabilityTech,
			int stealthTech, int supportTech,
			boolean[] tankPicTech, boolean tankTech, 
			int totalPopulation, int totalScholars, int townTech,
			UserTown[] towns, int tradeTech, String username,
			boolean[] weap,int capitaltid, int bp, int commsCenterTech, int playedTicks, int premiumTimer,
			int ubTimer, int mineTimer, int feroTimer, int timberTimer, int manMatTimer, int foodTimer, int revTimer, int totalBPEarned, String email,
			boolean zeppTech, boolean missileSiloTech, boolean recyclingTech, boolean metalRefTech, boolean timberRefTech, boolean manMatRefTech,
			boolean foodRefTech, boolean attackAPI, boolean advancedAttackAPI, boolean tradingAPI, boolean advancedTradingAPI,
			boolean smAPI, boolean researchAPI, boolean buildingAPI, boolean advancedBuildingAPI, boolean messagingAPI, 
			boolean zeppelinAPI, boolean completeAnalyticAPI, boolean nukeAPI, boolean worldMapAPI, int scoutTech, int alotTech) {
		this.bp=bp;
		this.scoutTech=scoutTech;
		this.commsCenterTech=commsCenterTech;
		this.timberTimer=timberTimer; this.manMatTimer=manMatTimer; this.foodTimer=foodTimer;
		this.playedTicks=playedTicks;
		this.premiumTimer=premiumTimer;
		this.attackAPI=attackAPI;this.advancedAttackAPI=advancedAttackAPI;
		this.tradingAPI=tradingAPI;this.advancedTradingAPI=advancedTradingAPI;
		this.smAPI=smAPI;this.researchAPI=researchAPI;this.buildingAPI=buildingAPI;
		this.advancedBuildingAPI=advancedBuildingAPI;this.messagingAPI=messagingAPI;
		this.zeppelinAPI=zeppelinAPI;this.completeAnalyticAPI=completeAnalyticAPI;
		this.nukeAPI=nukeAPI;
		this.worldMapAPI=worldMapAPI;
		this.ubTimer=ubTimer; this.mineTimer=mineTimer; this.feroTimer=feroTimer;
		this.revTimer=revTimer;
		this.zeppTech=zeppTech; this.missileSiloTech=missileSiloTech;
		this.recyclingTech=recyclingTech;this.metalRefTech=metalRefTech;
		this.timberRefTech=timberRefTech;this.manMatRefTech=manMatRefTech;this.foodRefTech=foodRefTech;
		AUTemplates = templates;
		this.capitaltid=capitaltid;
		this.totalBPEarned=totalBPEarned;
		this.email=email;
		ID = id;
		this.aLotTech = aLotTech;
		this.afTech = afTech;
		this.au = au;
		this.bomberPicTech = bomberPicTech;
		this.bomberTech = bomberTech;
		this.knowledge=knowledge;
		this.buildingSlotTech = buildingSlotTech;
		this.bunkerTech = bunkerTech;
		this.civWeapChoice = civWeapChoice;
		this.engTech = engTech;
		this.isLeague = isLeague;
		this.juggerPicTech = juggerPicTech;
		this.juggerTech = juggerTech;
		this.league = league;
		this.lotTech=lotTech;
		this.scholTech = scholTech;
		this.scholTicks = scholTicks;
		this.scholTicksTotal = scholTicksTotal;
		this.soldierPicTech = soldierPicTech;
		this.soldierTech = soldierTech;
		this.stabilityTech = stabilityTech;
		this.stealthTech = stealthTech;
		this.supportTech = supportTech;
		this.tankPicTech = tankPicTech;
		this.tankTech = tankTech;
		this.totalPopulation = totalPopulation;
		this.totalScholars = totalScholars;
		this.townTech = townTech;
		this.towns = towns;
		this.tradeTech = tradeTech;
		this.username = username;
		this.weap = weap;
	}
	public String getUsername() {
		return username;
	}
	public int getID() {
		return ID;
	}
	public String getEmail() {
		return email;
	}
	public int getKnowledge(){
		return knowledge;
	}
	public boolean isLeague() {
		return isLeague;
	}
	public int getBrkthrus() {
		return brkthrus;
	}
	public int getBrkups() {
		return brkups;
	}
	public int getAutopilotTimer() {
		return revTimer;
	}
	public int getScholTicks() {
		return scholTicks;
	}
	public int getTimberTimer() {
		return timberTimer;
	}
	public int getManMatTimer() {
		return manMatTimer;
	}
	/**
	 * This and other timers returns in server ticks how long you have till the benefit you bought with your BP wears off.
	 * @return
	 */
	public int getFoodTimer() {
		return foodTimer;
	}
	public int getBP() {
		return bp;
	}
	public int getTotalBPEarned() {
		return totalBPEarned;
	}
	public int getAfTech() {
		return afTech;
	}
	public int getScholTicksTotal() {
		return scholTicksTotal;
	}
	public int getStealthTech() {
		return stealthTech;
	}
	public int getBuildingSlotTech() {
		return buildingSlotTech;
	}
	public int getTotalScholars() {
		return totalScholars;
	}
	public int getCommsCenterTech() {
		return commsCenterTech;
	}
	public int getPlayedTicks() {
		return playedTicks;
	}
	public int getBattlehardModeTimer() {
		return premiumTimer;
	}
	public int getUbTimer() {
		return ubTimer;
	}
	public int getMineTimer() {
		return mineTimer;
	}
	public boolean isWorldMapAPI() {
		return worldMapAPI;
	}
	public int getFeroTimer() {
		return feroTimer;
	}
	public int getTotalPopulation() {
		return totalPopulation;
	}
	public int getLotTech() {
		return lotTech;
	}
	public int getALotTech() {
		return aLotTech;
	}
	public int getCapitaltid() {
		return capitaltid;
	}
	public int getBp() {
		return bp;
	}
	public int getPremiumTimer() {
		return premiumTimer;
	}
	public boolean isZeppTech() {
		return zeppTech;
	}
	public boolean isMissileSiloTech() {
		return missileSiloTech;
	}
	public boolean isRecyclingTech() {
		return recyclingTech;
	}
	public boolean isMetalRefTech() {
		return metalRefTech;
	}
	public boolean isTimberRefTech() {
		return timberRefTech;
	}
	public boolean isManMatRefTech() {
		return manMatRefTech;
	}
	public boolean isFoodRefTech() {
		return foodRefTech;
	}
	public int getRevTimer() {
		return revTimer;
	}
	public boolean isSoldierTech() {
		return soldierTech;
	}
	public boolean isTankTech() {
		return tankTech;
	}
	public int getCapitalTID() {
		return capitaltid;
	}
	public boolean isJuggerTech() {
		return juggerTech;
	}
	public boolean isBomberTech() {
		return bomberTech;
	}
	public boolean[] getSoldierPicTech() {
		return soldierPicTech;
	}
	public boolean[] getTankPicTech() {
		return tankPicTech;
	}
	public boolean[] getJuggerPicTech() {
		return juggerPicTech;
	}
	public boolean[] getBomberPicTech() {
		return bomberPicTech;
	}
	public UserAttackUnit[] getAUTemplates() {
		return AUTemplates;
	}
	public int getTradeTech() {
		return tradeTech;
	}
	public int getBunkerTech() {
		return bunkerTech;
	}
	public int getStabilityTech() {
		return stabilityTech;
	}
	public boolean[] getWeap() {
		return weap;
	}
	public UserAttackUnit[] getAu() {
		return au;
	}
	public boolean isAttackAPI() {
		return attackAPI;
	}
	public boolean isAdvancedAttackAPI() {
		return advancedAttackAPI;
	}
	public boolean isTradingAPI() {
		return tradingAPI;
	}
	public boolean isAdvancedTradingAPI() {
		return advancedTradingAPI;
	}
	public boolean isSmAPI() {
		return smAPI;
	}
	public boolean isResearchAPI() {
		return researchAPI;
	}
	public boolean isBuildingAPI() {
		return buildingAPI;
	}
	public boolean isAdvancedBuildingAPI() {
		return advancedBuildingAPI;
	}
	public boolean isMessagingAPI() {
		return messagingAPI;
	}
	public boolean isZeppelinAPI() {
		return zeppelinAPI;
	}
	public boolean isCompleteAnalyticAPI() {
		return completeAnalyticAPI;
	}
	public boolean isNukeAPI() {
		return nukeAPI;
	}
	public int getSupportTech() {
		return supportTech;
	}
	public int getCivWeapChoice() {
		return civWeapChoice;
	}
	public String getLeague() {
		return league;
	}
	public int getTownTech() {
		return townTech;
	}
	public int getEngTech() {
		return engTech;
	}
	public int getScholTech() {
		return scholTech;
	}
	public UserTown[] getTowns() {
		return towns;
	}
	public void setScoutTech(int scoutTech) {
		this.scoutTech = scoutTech;
	}
	public int getScoutTech() {
		return scoutTech;
	}
}
