package BattlehardFunctions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;



public class UserPlayer {
	boolean isLeague=false;
	
	public int playedTicks=0;
	
	private String username, league, email;
	
	private int ID, knowledge, bp, scoutTech, bloodMetalPlating, scholTicksTotal, bodyArmor,
				revTimer, townTech, architecture, clockworkComputers, constructionResearch, ordinanceResearch = 0,
				capitaltid=-1, premiumTimer=0, ubTimer=0, mineTimer=0, timberTimer=0,
				manMatTimer=0, totalBPEarned=0, lord=0, foodTimer=0, feroTimer=0,
				teslaTech = 0, scholTicks = 0, totalScholars=0,totalPopulation=0,
				infrastructureTech = 18, structuralIntegrity = 1, firearmResearch = 0; // default value.
	
	private boolean airshipTech, clockworkAugments, attackAPI, advancedAttackAPI, tradingAPI,
					advancedTradingAPI, smAPI, researchAPI, buildingAPI, advancedBuildingAPI,
					messagingAPI, zeppelinAPI, completeAnalyticAPI, nukeAPI ,worldMapAPI,
					digAPI, personalShields, hydraulicAssistors, thrustVectoring,
					bloodMetalArmor, advancedFortifications=false;
	
	private UserAttackUnit[] au; // To keep a private player-held list of home aus.
	private Timestamp vassalFrom;
	private Hashtable[] vassalHash;
	// the playerside.
	// current player read goes metal, timber, mm, food, stealthTech, totalPop,alotTech,soldierTech,tank,jugger,weaps...I think.

	private UserTown[] towns;
	
	
	public UserPlayer(int id, int infrastructureTech,
			int bloodMetalPlating, UserAttackUnit[] au, 
			boolean thrustVectoring, int knowledge, int constructionResearch,
			boolean advancedFortifications, int firearmResearch, int architecture, boolean isLeague,
			boolean hydraulicAssistors, String league,
			int clockworkComputers, int scholTicks, int scholTicksTotal,
			int structuralIntegrity,
			int bodyArmor,
			boolean personalShields, 
			int totalPopulation, int totalScholars, int townTech,
			UserTown[] towns, String username,
			int capitaltid, int bp, int playedTicks, int premiumTimer,
			int ubTimer, int mineTimer, int feroTimer, int timberTimer, int manMatTimer, int foodTimer, int revTimer, int totalBPEarned, String email,
			boolean airshipTech, boolean clockworkAugments, boolean attackAPI, boolean advancedAttackAPI, boolean tradingAPI, boolean advancedTradingAPI,
			boolean smAPI, boolean researchAPI, boolean buildingAPI, boolean advancedBuildingAPI, boolean messagingAPI,
			boolean zeppelinAPI, boolean completeAnalyticAPI, boolean nukeAPI, boolean worldMapAPI, boolean digAPI, int scoutTech, boolean bloodMetalArmor, int lord, Timestamp vassalFrom, Hashtable[] vassalHash) {

		this.bp=bp;
		this.scoutTech=scoutTech;
		this.bloodMetalArmor=(bloodMetalArmor);
		this.timberTimer=timberTimer; this.manMatTimer=manMatTimer; this.foodTimer=foodTimer;
		this.playedTicks=playedTicks;
		this.premiumTimer=premiumTimer;
		this.attackAPI=attackAPI;this.advancedAttackAPI=advancedAttackAPI;
		this.vassalHash=vassalHash;
		this.lord=lord;
		this.vassalFrom = new Timestamp(vassalFrom.getTime()); // must be copied.
		this.tradingAPI=tradingAPI;this.advancedTradingAPI=advancedTradingAPI;
		this.smAPI=smAPI;this.researchAPI=researchAPI;this.buildingAPI=buildingAPI;
		this.advancedBuildingAPI=advancedBuildingAPI;this.messagingAPI=messagingAPI;
		this.zeppelinAPI=zeppelinAPI;this.completeAnalyticAPI=completeAnalyticAPI;
		this.nukeAPI=nukeAPI;
		this.worldMapAPI=worldMapAPI;
		this.ubTimer=ubTimer; this.mineTimer=mineTimer; this.feroTimer=feroTimer;
		this.revTimer=revTimer;
		this.airshipTech=airshipTech; 
		this.clockworkAugments=clockworkAugments;
		this.capitaltid=capitaltid;
		this.totalBPEarned=totalBPEarned;
		this.email=email;
		ID = id;
		this.bloodMetalPlating = bloodMetalPlating;
		this.au = au;
		this.thrustVectoring = thrustVectoring;
		this.knowledge=knowledge;
		this.constructionResearch = constructionResearch;
		this.advancedFortifications = advancedFortifications;
		this.firearmResearch = firearmResearch;
		this.architecture = architecture;
		this.isLeague = isLeague;
		this.hydraulicAssistors = hydraulicAssistors;
		this.league = league;
		this.infrastructureTech = (infrastructureTech);
		this.clockworkComputers = clockworkComputers;
		this.scholTicks = scholTicks;
		this.scholTicksTotal = scholTicksTotal;
		this.structuralIntegrity = structuralIntegrity;
		this.bodyArmor = bodyArmor;
		this.personalShields = personalShields;
		this.totalPopulation = totalPopulation;
		this.totalScholars = totalScholars;
		this.townTech = townTech;
		this.towns = towns;
		this.username = username;
	}
	public int getOrdinanceResearch() {
		return ordinanceResearch;
	}
	public boolean isAirshipTech() {
		return airshipTech;
	}
	public boolean isClockworkAugments() {
		return clockworkAugments;
	}
	public int getTeslaTech() {
		return teslaTech;
	}
	public int getBloodMetalPlating() {
		return bloodMetalPlating;
	}
	public int getBodyArmor() {
		return bodyArmor;
	}
	public int getConstructionResearch() {
		return constructionResearch;
	}
	public boolean isPersonalShields() {
		return personalShields;
	}
	public boolean isHydraulicAssistors() {
		return hydraulicAssistors;
	}
	public boolean isThrustVectoring() {
		return thrustVectoring;
	}
	public boolean getAdvancedFortifications() {
		return advancedFortifications;
	}
	public int getStructuralIntegrity() {
		return structuralIntegrity;
	}
	public int getFirearmResearch() {
		return firearmResearch;
	}
	public int getArchitecture() {
		return architecture;
	}
	public int getClockworkComputers() {
		return clockworkComputers;
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
	
	public int getScholTicksTotal() {
		return scholTicksTotal;
	}
	
	public int getTotalScholars() {
		return totalScholars;
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
	
	public int getCapitaltid() {
		return capitaltid;
	}
	public int getBp() {
		return bp;
	}
	public int getPremiumTimer() {
		return premiumTimer;
	}
	
	public int getRevTimer() {
		return revTimer;
	}
	
	public int getCapitalTID() {
		return capitaltid;
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
	
	public String getLeague() {
		return league;
	}
	public int getTownTech() {
		return townTech;
	}
	
	public UserTown[] getTowns() {
		return towns;
	}

	public int getScoutTech() {
		return scoutTech;
	}
	
	public boolean isDigAPI() {
		return digAPI;
	}
	
	public int getInfrastructureTech() {
		return infrastructureTech;
	}

	public boolean isBloodMetalArmor() {
		return bloodMetalArmor;
	}
	public void setLord(int lord) {
		this.lord = lord;
	}
	/**
	 * Returns the pid of your lord if you have one, otherwise is 0.
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
	public void setVassalHash(Hashtable[] vassalHash) {
		this.vassalHash = vassalHash;
	}
	/**
	 * Returns an array of hashtables, each representing a player who has towns you control or who is your vassal.
	 * ex:
	 *  { {owner: someone, vassal: true/false, towns: {townName, taxRate,x,y} }, { owner... 
	 * @return
	 */
	public Hashtable[] getVassalHash() {
		return vassalHash;
	}
}
