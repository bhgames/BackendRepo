package BHEngine;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;



public class Player  {
	public int ID;
	private boolean isLeague=false;
	private int buildingCheckMax=360;
	private int buildingCheckTimer=360;
	private int internalClock=0;
	private boolean beingDeleted=false;
	private String holdingIteratorID="-1";
	protected PlayerScript ps;
	private String pushLog="";
	public int iterTicks = 0;
	public int playedTicks=0;
	public long totalTimePlayed = 0;
	public int numLogins=0;
	 private Hashtable eventListenerLists = new Hashtable();

	public Timestamp last_session;
	public int owedTicks =0;
	private String version;
	private ArrayList<Hashtable> achievements;
	public int last_auto_blast;
	public Timestamp last_login;
	private long fuid;
	public String flicker="noflick";
	private boolean attackAPI, advancedAttackAPI, tradingAPI,advancedTradingAPI,smAPI,researchAPI,buildingAPI,advancedBuildingAPI,messagingAPI,zeppelinAPI,completeAnalyticAPI,nukeAPI,worldMapAPI,digAPI;
	private int premiumTimer=0;
	private int revTimer=0;
	private int tPushes=0;
	private int ubTimer=0;
	private int mineTimer=0;
	private int feroTimer=0;
	private int timberTimer=0;
	private boolean zeppTech,missileSiloTech,recyclingTech,metalRefTech,timberRefTech,manMatRefTech,foodRefTech;
	private String email;
	private int mmTimer=0;
	private int fTimer=0;
	private int totalBPEarned=0;
	private int bp=0;
	UberConnection con;
	private boolean synchronize = false;
	private League  league=null;
	
	public GodGenerator God;
	private int scholTicks,scholTicksTotal,aLotTech,capitaltid,commsCenterTech,lotTech,townTech,civWeapChoice,knowledge,scout,supportTech,engTech,scoutTech,tradeTech,scholTech,buildingSlotTech,stabilityTech,bunkerTech,afTech,totalScholars,totalPopulation;
	private boolean supportstaff,soldierTech,tankTech,juggerTech,bomberTech,isQuest=false;
	private boolean[] weapTech,soldierPicTech,tankPicTech,juggerPicTech,bomberPicTech;
	private ArrayList<QuestListener> activeQuests;
	private int stealth;
	private ArrayList<AttackUnit> au, AUTemplates;
	private ArrayList<Town> towns;
	private String username,password;
	/*
	 * Players have towns, towns have attack units, when a raid occurs, or an attack,
	 * the resulting changes are made to the raid which is then sent back with those unit changes. The raidserver
	 * detects them as a return and takes those units and puts them back in the town.
	 * 
	Players will soon be the base unit. Population is stored in res[4], for each town, and totalPopulation
	needs to be stored separately. totalScholars are scored separately, but each building has individual scholars.
	Total messengers stored separately, but each building stores them individually but they can be used anywhere, in any town, it's just
	if you have a large store of them, they can be glassed. Traders are stored in each building. Builders are stored in each building.
	
	Population in each town must be updated when a unit is build. totalPopulation then needs to be updated as well.
	 */
	//Thread t;
	public Player(int ID, GodGenerator God) {
		/*
		 * This guy CREATES a player in memory...
		 */
		try {
		   UberStatement stmt = God.con.createStatement();
		   this.con=God.con;
		   this.ID=ID;
		   this.God=God;
		   ResultSet rs = stmt.executeQuery("select * from player where pid = " + ID);
	       internalClock = God.gameClock;
		   rs.next();
		   username = rs.getString(2);
	       password = rs.getString(26);
	       stealth = rs.getInt(3);
	       playedTicks=rs.getInt(45);
	       owedTicks = rs.getInt(82);
	       version = rs.getString(81);
	       fuid = rs.getLong(57);
	       digAPI = rs.getBoolean(86);
	       knowledge = rs.getInt(4);
	       flicker = rs.getString(58);
	       last_login=rs.getTimestamp(41);
	       // EVENT LISTENER STUFF	
			eventListenerLists.put("onProgramLoad",new ArrayList<QuestListener>());
			eventListenerLists.put("onRaidSent",new ArrayList<QuestListener>());

	       try {
	       last_session=rs.getTimestamp(83);
	       } catch(Exception exc) { last_session = new Timestamp((new Date()).getTime());}
	       numLogins = rs.getInt(84);
	       totalTimePlayed = rs.getLong(85);
	       
	       attackAPI = rs.getBoolean(68);
	       advancedAttackAPI = rs.getBoolean(69);
	       tradingAPI = rs.getBoolean(70);
	       advancedTradingAPI = rs.getBoolean(71);
	       smAPI = rs.getBoolean(72);
	       researchAPI = rs.getBoolean(73);
	       buildingAPI = rs.getBoolean(74);
	       advancedBuildingAPI = rs.getBoolean(75);
	       messagingAPI = rs.getBoolean(76);
	       zeppelinAPI = rs.getBoolean(77);
	       completeAnalyticAPI = rs.getBoolean(78);
	       nukeAPI = rs.getBoolean(79);
	       worldMapAPI = rs.getBoolean(80);


	       tPushes = rs.getInt(60);
	       email = rs.getString(56);
	       totalScholars = rs.getInt(5);
	       totalPopulation = rs.getInt(7);
	       aLotTech = rs.getInt(8);
	       last_auto_blast = rs.getInt(59);
	       revTimer = rs.getInt(54);
	       supportstaff = rs.getBoolean(28);
	       zeppTech = rs.getBoolean(61);
	       missileSiloTech = rs.getBoolean(62);
	       recyclingTech = rs.getBoolean(63);
	       metalRefTech = rs.getBoolean(64);
	       timberRefTech = rs.getBoolean(65);
	       manMatRefTech = rs.getBoolean(66);
	       foodRefTech = rs.getBoolean(67);

	       soldierTech = rs.getBoolean(9);
	       setTankTech(rs.getBoolean(10));
	       juggerTech = rs.getBoolean(11);
	       scoutTech=rs.getInt(40);
	       premiumTimer = rs.getInt(47);
	       ubTimer = rs.getInt(48);
	       mineTimer = rs.getInt(49);
	       feroTimer = rs.getInt(50);
	       totalBPEarned=rs.getInt(55);
	       timberTimer = rs.getInt(51);
	       mmTimer = rs.getInt(52);
	       fTimer = rs.getInt(53);
	       bp = rs.getInt(46);
	       String holdThis = rs.getString(12); // weaps stuff.
	       buildingSlotTech = rs.getInt(13);
	       civWeapChoice = rs.getInt(17);
	       bomberTech = rs.getBoolean(18);
	       supportTech = rs.getInt(19);
	       townTech = rs.getInt(20);
	       bunkerTech=rs.getInt(21);
	       stabilityTech = rs.getInt(30);
	       afTech = rs.getInt(38);
	       scholTicks = rs.getInt(31);
	   //    brkthrus = rs.getInt(32);
	 //      brkups = rs.getInt(33);
	       lotTech = rs.getInt(34);
	       engTech = rs.getInt(35);
	       scholTech = rs.getInt(36);
	       tradeTech = rs.getInt(27);
	       commsCenterTech = rs.getInt(43);
	       capitaltid = rs.getInt(39);
	       
	       pushLog = rs.getString(42);
	      // need to break apart weaptech into an array.
	      int weapc = 0;  boolean[] weaps = new boolean[21];
	      soldierPicTech = new boolean[10];
	      tankPicTech = new boolean[10];
	      juggerPicTech = new boolean[10];
	      bomberPicTech = new boolean[5];

			while(weapc<21) {

				if(Integer.parseInt(holdThis.substring(0,holdThis.indexOf(",")))==1) weaps[weapc] = true; else weaps[weapc] = false;
				 holdThis = holdThis.substring(holdThis.indexOf(",")+1,holdThis.length());

				weapc++;
			}
			
			weapTech=weaps;
			weapc=0;
			
			holdThis = rs.getString(22);//soldier string
			String holdThis1=rs.getString(23);//tank
			String holdThis2=rs.getString(24);//juggernaught
			String holdThis3=rs.getString(25);//bomber

			while(weapc<10) {

				if(Integer.parseInt(holdThis.substring(0,holdThis.indexOf(",")))==1) soldierPicTech[weapc] = true; else soldierPicTech[weapc] = false;
				 holdThis = holdThis.substring(holdThis.indexOf(",")+1,holdThis.length());

				if(Integer.parseInt(holdThis1.substring(0,holdThis1.indexOf(",")))==1) tankPicTech[weapc] = true; else tankPicTech[weapc] = false;
					 holdThis1 = holdThis1.substring(holdThis1.indexOf(",")+1,holdThis1.length());
					 
				if(Integer.parseInt(holdThis2.substring(0,holdThis2.indexOf(",")))==1) juggerPicTech[weapc] = true; else juggerPicTech[weapc] = false;
						 holdThis2 = holdThis2.substring(holdThis2.indexOf(",")+1,holdThis2.length());

				if(weapc<5) { // special for bombers
				if(Integer.parseInt(holdThis3.substring(0,holdThis3.indexOf(",")))==1) bomberPicTech[weapc] = true; else bomberPicTech[weapc] = false;
							 holdThis3 = holdThis3.substring(holdThis3.indexOf(",")+1,holdThis3.length()); }
				weapc++;
			}
			rs.close();

			au = getMemAu(); AUTemplates = getMemAUTemplates();
			try {
			getAchievements();
			} catch(Exception exc) { exc.printStackTrace(); System.out.println("No idea why this error happened, but player load saved."); } 
			ps = new PlayerScript(this);
		} catch(SQLException exc) { exc.printStackTrace(); }

	}

	
	public TradeSchedule findTradeSchedule(int trid) {
		int i = 0;
		int j = 0;
		while(j<towns().size()) {
			i = 0;
			
		while(i<towns().get(j).tradeSchedules().size()) {
			if(towns().get(j).tradeSchedules().get(i).tradeScheduleID==trid) return towns().get(j).tradeSchedules().get(i);
			i++;
		}
		j++;
		}
		return null;
	}
	public Raid findRaid(int trid) {
		int i = 0;
		int j = 0;
		while(j<towns().size()) {
			i = 0;
			
		while(i<towns().get(j).attackServer().size()) {
			if(towns().get(j).attackServer().get(i).raidID==trid) return towns().get(j).attackServer().get(i);
			i++;
		}
		j++;
		}
		return null;
	}
	public void doResearch(int amt) {
		
		 scholTicksTotal=getResearchTicksForPoint(totalScholars,God.Maelstrom.getScholarEffect(this));
		 // if we just loaded, then ticksToFinishTotal isn't set and must be.
		if(getScholTicks()<getScholTicksTotal()&&getScholTicks()!=-1) {
			setScholTicks(getScholTicks() + amt); 
			} 
		 if(getScholTicks()>=getScholTicksTotal()) {
			 double howMany = ((double) getScholTicks())/((double) getScholTicksTotal());
			 int howManyRounded = (int) Math.floor(howMany);
			 setKnowledge(getKnowledge() + howManyRounded);  // Right, now we have the amount we gained.
			 int newScholTicks = (int) Math.floor((howMany-howManyRounded)*getScholTicksTotal());
		//	 if(getUsername().equals("JigglyYoWigly")||getUsername().equals("Trigger")) System.out.println("Number was " + amt + " Got new Schol Ticks of " + newScholTicks + " I have " + howManyRounded + " being added from  " + howMany + " from " + getScholTicks() + " scholTicks and total "+  getScholTicksTotal());
//Number was 2 Got new Schol Ticks of 68 I have 1 being added from  1.0 from 68 scholTicks and total 68

			 setScholTicks(newScholTicks); // start over, bitch. newScholTicks takes the fraction of the way
			 // you were to the next point, multiplies it by the total to know how many you truly deserve to keep.
		
		}
	}
	/*public void modifyResearchTicksLevel(double cloudFactor) {
	
		// setScholTicksTotal(getResearchTicksAtBreakthrough(getTotalScholars(),getBrkthrus()+1,cloudFactor));
		
	
	}*/
	/*public int getResearchTicks(double cloudFactor) {
		// why plus 1? This is the ticks you're gonna put in to move to the next level - 
		// and it's not on your lvlUps because you haven't added it yet, so we save
		// you the trouble and add one.
		
		//	x^y -> divide into two parts  1/5th + (5/6ths+.5(5/6ths)*towns.size)* Math.exp(-Math.log((totalScholars*(1+t.player.God.Maelstrom.getResearchEffect(t.x,t.y))+1))/(10))

		double nextLevelBase = (int) Math.round((Math.pow((getBrkthrus()+1),6.099))/GodGenerator.gameClockFactor);
		double base = (int) Math.round(nextLevelBase/6.0);
		double expFactor = base*5;
	
		return (int) Math.round(base+(expFactor+.5*expFactor*towns().size())*Math.exp(-Math.log((getTotalScholars()*(1+.05*(getScholTech()-1))*(1+cloudFactor)+1))/10));
		
	
	}*/
	public int getResearchTicksForPoint(int totalScholars, double cloudFactor) {
		//Lvl you want, so no +1. You do that for yourself, basically.
	//	double nextLevelBase = (int) Math.round((250*Math.pow(brkthrus,3))/GodGenerator.gameClockFactor);
		double nextLevelBase = 24*3600/GodGenerator.gameClockFactor;
		double base = (int) Math.round(nextLevelBase/6.0);
		double expFactor = base*5;
		double schol = (totalScholars*(1+cloudFactor+.05*(getScholTech()-1))+1);
		return (int) Math.round( ((double) nextLevelBase)/(schol)); // So if one scholar, 1pt/day. If 10, then 100/day.
	//	schol = Math.log(schol);
		//schol = Math.pow(schol,2.5);
	/*	double avgLevel = 0;
		
		
		ArrayList<Town> towns = towns();
		
		int i = 0;
		while(i<towns.size()) {
			avgLevel+=(int) Math.round(((double) God.getAverageLevel(towns.get(i)) )/ ((double) towns.size()));
			i++;
		}
		
		return (int) Math.round(base+expFactor*Math.exp(1-schol/(6*towns.size()*Building.getCap((int) avgLevel,false)))/Math.exp(1.0));*/
		
		
		
		
	}
	public GodGenerator getGod() {
		return God;
	}
	synchronized public boolean addEventListener(QuestListener q, String type) {
		ArrayList<QuestListener> list =(ArrayList<QuestListener>) eventListenerLists.get(type);
		if(list!=null)
		for(QuestListener p: list) {
			
			if(p.ID==q.ID) {
				
				return false;
			}
		}
		else return false;
		
		list.add(q);
		
		return true;
	}
	synchronized public boolean dropEventListener(QuestListener q, String type) {
			ArrayList<QuestListener> list =(ArrayList<QuestListener>) eventListenerLists.get(type);
		
		if(list!=null) {
			int counter=0;
			int i = 0;
			QuestListener p;
			while(i<list.size()) {
				
				p = list.get(i);
			
				if(p.ID==q.ID) {
					counter++;
					list.remove(p);
					i--;
				}
			i++;
			}
			if(counter>0)return true;
			else return false;
		}
		else return false;
		
	}
	
	
	public int returnUnitLots() {
		return getALotTech();
	}
	
	public boolean[] getWeapTech() {
		return getWeap();
	}
	
	
	
	
	public boolean getUnitTech(int num) {
		if(num==1) return isSoldierTech();
		if(num==2) return isTankTech();
		if(num==3) return isJuggerTech();
		if(num==4) return isBomberTech();
		return false;
	}
	public boolean getWeapTech(int num) {
		return getWeap()[num];
	}

	public void synchronizeWithPlayer(Player p) {
		/*
		 * public String username;---
	public int ID;---
	public String password;----
	boolean isLeague=false;----
	public PlayerScript ps;---nnot needed----
	public int stealthTech;----- public int buildingSlotTech;----
	int knowledge;----
	public int totalScholars=0,---- totalMessengers=0,---- totalPopulation=0;-----
	int lotTech = 18;----	boolean godHere = true;---- int aLotTech;---
	boolean soldierTech,----tankTech,----juggerTech,---bomberTech;----
	public boolean soldierPicTech[],---tankPicTech[],---juggerPicTech[],---bomberPicTech[];----
	public ArrayList<AttackUnit> AUTemplates;----
	public int tradeTech=1; ----// MAX IS TEN! Well, doesn't have to be...
	public int bunkerTech=1;-----
	boolean weap[];----
	Connection con;---not needed---
	boolean synchronize = false;---
	boolean killmyself = false;----
	public ArrayList<AttackUnit> au; ----// To keep a private player-held list of home aus.
	int supportTech;-----
	public int civWeapChoice = 0;---- // default value.
	public League league;----
	public int townTech;-----
	boolean supportstaff;-----
	public boolean callSync = false;
		 */
		
		this.setCivWeapChoice(p.getCivWeapChoice());
		this.setTotalScholars(p.getTotalScholars());
		this.setBuildingSlotTech(p.getBuildingSlotTech());
		this.setTotalPopulation(p.getTotalPopulation());
		this.setStealthTech(p.getStealthTech());
		this.setLotTech(p.getLotTech());
		this.setTownTech(p.getTownTech());
		this.setSupportstaff(p.getSupportstaff());
		 this.ID=p.ID; this.setUsername(p.getUsername());
	//	 this.setKnowledge(p.getKnowledge());
		 this.setALotTech(p.getALotTech());
		 this.setSoldierTech(p.isSoldierTech()); this.setTankTech(p.isTankTech()); this.setJuggerTech(p.isJuggerTech()); this.setWeap(p.getWeap());
		 //this.au=p.getAu();
		// this.setAUTemplates(p.getAUTemplates());
		 this.setTradeTech(p.getTradeTech());
		 this.setPassword(p.getPassword());
		 this.setBomberTech(p.isBomberTech());
		 this.setSoldierPicTech(p.getSoldierPicTech());
		 this.setTankPicTech(p.getTankPicTech());
		 this.setJuggerPicTech(p.getJuggerPicTech());
		 this.setBomberPicTech(p.getBomberPicTech());
		 this.setBunkerTech(p.getBunkerTech());
		 this.setSupportTech(p.getSupportTech());
		int i = 0;
	/*	do {
			towns.get(i).addPlayer(this);
			i++;
		} while(i<towns.size());*/
		synchronize=false; // because we're done synchronizing!
	} 
	public void saveAndIterate(int number) {
	
		UberStatement stmt=null;
		ResultSet checkChange;
		
		  try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // may be a problem if we lose the UberStatement,
		  // the player needs complete reloading. But this conserves memory.
		  String updateAU[]; String weapStr,soldierPicStr,tankPicStr,juggerPicStr,bomberPicStr; String updatePlayer;
		  AttackUnit hau; String weapons;
		     boolean transacted=false;
		//    lagTimer = new Timer();
	//	for(;;) {
			try {
		//if(!God.godHere) break;
			if(!synchronize) { // no incrementation when in synchronization!!!!
				// not important for serverside, is always false.
		
				//if(ID==73) System.out.println("1");
				doResearch(number);
				//if(ID==73) System.out.println("2");
				
				populationCheck(); // only need to do this once!
				//if(ID==73) System.out.println("3");
				
				iterate(number); // doesn't do anything unless you're Id.
				
				
				ArrayList<Town> towns = towns();
				
				if(buildingCheckTimer>=buildingCheckMax) {
					int i = 0;
					while(i<towns.size()) {
						towns.get(i).checkBuildingDupes();

						i++;
					}
				buildingCheckTimer=0;
				} else buildingCheckTimer++;
				
				if(towns().size()==0&&!isBeingDeleted()&&God.serverLoaded&&!isQuest()&&ID!=5) {
					System.out.println("Checking if " + getUsername() + " is townless, adding 1.");
					towns = null; // set it so it's null.
					towns(); // REFRESH.
					if(towns().size()==0) {
						System.out.println(getUsername() + " is ACTUALLY townless, adding 1.");

					God.giveNewTown(this,-1,0,true,0,0);
					}
				}
				
				if(towns().size()>getTownTech()&&!isQuest()&&God.serverLoaded&&ID!=5&&getPlayedTicks()<3600/GodGenerator.gameClockFactor) {
					int tryCounter=0;
					while(towns().size()>getTownTech()&&towns().size()>1&&tryCounter<10) {
					System.out.println(getUsername() + " has too many towns at "+towns().size() + " towns.");
					if(towns().get(towns().size()-1).townID!=getCapitaltid())
					towns().get(towns().size()-1).giveTown(null,God.getPlayer(5));
					else towns().get(0).giveTown(null,God.getPlayer(5)); 
					tryCounter++;
					}
				}
				last_auto_blast+=number;
				if(premiumTimer>0) premiumTimer-=number;
				if(feroTimer>0) feroTimer-=number;
				if(ubTimer>0) ubTimer-=number;
				if(mineTimer>0) mineTimer-=number;
				if(timberTimer>0) timberTimer-=number;
				if(mmTimer>0) mmTimer-=number;
				if(fTimer>0) fTimer-=number;
				if(revTimer>0) revTimer-=number;
				try {
					if(revTimer<=1) {
						int i = 0;
						while(i<God.programs.size()) {
							if(((Integer) God.programs.get(i).get("pid"))==ID&&((Thread) God.programs.get(i).get("Revelations")).isAlive()) {
								((Thread) God.programs.get(i).get("Revelations")).stop();
								getPs().b.sendYourself("Your Revelations A.I. has been shut off due to your Autopilot membership expiring. Please purchase another Autopilot membership to use Revelations again.", "Revelations A.I. shut off by the Gigabyte A.I. due to expired Autopilot membership!");
								}
							i++;
						}
					}
					} catch(Exception exc) { exc.printStackTrace(); System.out.println("Exception occured with autoshutoff but player saved.");}
				if(God.Maelstrom.EMPed(this)&&getPlayedTicks()>7*24*3600/GodGenerator.gameClockFactor) {
					int i = 0;
					while(i<God.programs.size()) {
						if(((Integer) God.programs.get(i).get("pid"))==ID&&((Thread) God.programs.get(i).get("Revelations")).isAlive()) {
							((Thread) God.programs.get(i).get("Revelations")).stop();
							getPs().b.sendYourself("Your Revelations A.I. was shut off due to an EMP caused by a nuclear burst over your map tile!", "EMP Burst has shut off your A.I.!");
							}
						i++;
					}
				}
				//if(ID==73) System.out.println("4");
				int i = 0; 
				ArrayList<QuestListener> activeQuests = getActiveQuests();
			
				try {
					while(i<activeQuests.size()) {
				//	System.out.println(username + " is iterating quest " + activeQuests.get(i).ID);

					activeQuests.get(i).iterateQuest(number,ID);
					i++;
				}
				} catch(Exception exc) { 
					exc.printStackTrace(); System.out.println("Quest exception caught, player " + getUsername() + " moved forward.");
					
					activeQuests.get(i).destroy(this); 
				}
				//if(ID==73) System.out.println("5");

				//System.out.println(username + " got through basic.");
			}
			
			
			
		} 
		  catch(OutOfMemoryError exc) {
			 God.killGod=true;
			  God.holdE=exc;
			  exc.printStackTrace();
		}
	//	}
			//if(ID==73) System.out.println("7");
		//  System.out.println(getUsername() + " has been iterated at " + getInternalClock() + "!");
		  	
			setInternalClock(getInternalClock() + number); // we only iterate after FINISHING THE SAVE!
			if(getInternalClock()>God.gameClock) setInternalClock(God.gameClock); // means owedTicks stretches past the last server restart,
			//so we just hold the internalClock steady while we update.
			playedTicks+=number;
			
	}
	public int getPlayedTicks() {
		
		if(owedTicks==0) return playedTicks;
		else return playedTicks+(God.gameClock-owedTicks);
	}
	synchronized public void update() {
		// This method brings the player up to standard time.
		boolean saveTripped=false;
		if(owedTicks>0) {
			saveAndIterate(God.gameClock-owedTicks);
			owedTicks=0; 
			saveTripped=true;
		}
			int i = 0;
		while(i<towns().size()) {
			if(towns().get(i).owedTicks>0) {
				towns().get(i).iterate(God.gameClock-towns().get(i).owedTicks);
				towns().get(i).owedTicks=0; // player towns and players normally will have around the same owedTicks...
				// we only keep owedTicks on towns for Id's sake.
				saveTripped=true;
			}
			i++;
		}
		if(saveTripped)
		save(); // Got to save whenever update is called so we don't waste resources on a server restart
		// reiterating them!
		
		
	}
	public boolean stuffOut() {
		int i = 0;
		while(i<towns().size()) {
			if(towns().get(i).stuffOut()) {
				return true;
			}
			i++;
		}
		if(getPs().b.isAlive()) return true; // program running means the player keeps cycling.
		return false;
	}
	public boolean completedQuest(int qid) {
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select complete from qpc where qid = " + qid + " and pid = " + ID);
		
			if(rs.next()) {
				if(rs.getInt(1)==1) {
					rs.close();
					stmt.close();
					return true;
				}
				else{
					rs.close();
					stmt.close();
					return false;
				}
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * Not the best version to use as there may be multiple classnames that are the same.
	 * If you do choose to use this, make sure your classnames are strongly unique!
	 * 
	 * @param classname
	 * @return
	 */
	public boolean completedQuest(String classname) {
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select qid from Quest where classname = '" + classname + "'");
			ResultSet check; UberStatement stmt2 = con.createStatement();
			rs.next();
			 check = stmt.executeQuery("select complete from qpc where qid = " + rs.getInt(1) + " and pid = " + ID);
			if(check.next()) {
				if(check.getInt(1)==1) {
					check.close();
					stmt2.close();
					rs.close();
					stmt.close();
					return true;
				}
				else {
					check.close();
					stmt2.close();
					rs.close();
					stmt.close();
					return false;
				}
			}
			check.close();
			stmt2.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	synchronized public void synchronize() throws SQLException {
		System.out.println("I am beginning to synchronize.");
		Trade holdTrade; TradeSchedule holdTradeSchedule;
		UberStatement aus,tus,bus,qus,sau,rus,rustown2,rustown22,lchk,mus;
		ResultSet aurs,trs,brs,qrs,saurs,rrs,rrs2,rrs22,lchkRS,mrs;
		String holdPart,weapons;
		League league;
		ArrayList<AttackUnit> aufortown,raidAU;
		 
		long cost[];
		long res[],resInc[];
		boolean weaps[];//soldierPicTech[],tankPicTech[],juggerPicTech[],bomberPicTech[];
		int weapforau[],weap[];
		double resEffects[];
		AttackUnit ha,Civ,sAU,auHold;
		Town t=null,Town1,town2Obj;
		Building b; String holdThis,holdThis1,holdThis2,holdThis3;
		Player foreignP;
		Raid holdRaid;
		UberStatement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from player where pid = " + ID);
		rs.next();
	        ID = rs.getInt(1);
	        

	       setUsername(rs.getString(2));
	       email = rs.getString(56);
	       last_auto_blast = rs.getInt(59);
	       fuid = rs.getLong(57);
	       flicker = rs.getString(58);
	       digAPI = rs.getBoolean(86);

	       tPushes = rs.getInt(60);
	       version = rs.getString(81);
	       owedTicks = rs.getInt(82);

	       try {
		       last_session=rs.getTimestamp(83);
		      } catch(Exception exc) { last_session = new Timestamp((new Date()).getTime());}	       numLogins = rs.getInt(84);
	       totalTimePlayed = rs.getLong(85);
	       
	       setPassword(rs.getString(26));
	       setStealthTech(rs.getInt(3));
	      // setKnowledge(rs.getInt(4));
	       setTotalScholars(rs.getInt(5));
		     setScoutTech(rs.getInt(40));
		       zeppTech = rs.getBoolean(61);
		       missileSiloTech = rs.getBoolean(62);
		       recyclingTech = rs.getBoolean(63);
		       metalRefTech = rs.getBoolean(64);
		       timberRefTech = rs.getBoolean(65);
		       manMatRefTech = rs.getBoolean(66);
		       foodRefTech = rs.getBoolean(67);
		       attackAPI = rs.getBoolean(68);
		       advancedAttackAPI = rs.getBoolean(69);
		       tradingAPI = rs.getBoolean(70);
		       advancedTradingAPI = rs.getBoolean(71);
		       smAPI = rs.getBoolean(72);
		       researchAPI = rs.getBoolean(73);
		       buildingAPI = rs.getBoolean(74);
		       advancedBuildingAPI = rs.getBoolean(75);
		       messagingAPI = rs.getBoolean(76);
		       zeppelinAPI = rs.getBoolean(77);
		       completeAnalyticAPI = rs.getBoolean(78);
		       nukeAPI = rs.getBoolean(79);
		       worldMapAPI = rs.getBoolean(80);

		     setRevTimer(rs.getInt(54));
	       setStabilityTech(rs.getInt(30));
	       setTotalPopulation(rs.getInt(7));
	       setALotTech(rs.getInt(8));
	       setTotalBPEarned(rs.getInt(55));
	       knowledge = rs.getInt(4);

	       setSupportstaff(rs.getBoolean(28));
	       setSoldierTech(rs.getBoolean(9));
	       setTankTech(rs.getBoolean(10));
	       setAfTech(rs.getInt(38));
	       setJuggerTech(rs.getBoolean(11));
	       holdThis = rs.getString(12); // weaps stuff.
	       setBuildingSlotTech(rs.getInt(13));
	       setCivWeapChoice(rs.getInt(17));
	       setBomberTech(rs.getBoolean(18));
	       setSupportTech(rs.getInt(19));
	       premiumTimer = rs.getInt(47);
	       bp = rs.getInt(46);
	       ubTimer = rs.getInt(48);
	       mineTimer = rs.getInt(49);
	       feroTimer = rs.getInt(50);
	       timberTimer = rs.getInt(51);
	       mmTimer = rs.getInt(52);
	       fTimer = rs.getInt(53);
	       last_login=rs.getTimestamp(41);
	       setTownTech(rs.getInt(20));
	       setBunkerTech(rs.getInt(21));
		       setStabilityTech(rs.getInt(30));
		       setScholTicks(rs.getInt(31));
		    //   setBrkthrus(rs.getInt(32));
		     //  setBrkups(rs.getInt(33));
		       setLotTech(rs.getInt(34));
		       setEngTech(rs.getInt(35));
		       setScholTech(rs.getInt(36));
		       playedTicks=rs.getInt(45);
	       setTradeTech(rs.getInt(27));
	        setCapitaltid(rs.getInt(39));
	      // need to break apart weaptech into an array.
	      int weapc = 0;   weaps = new boolean[21];
	      setSoldierPicTech(new boolean[10]);
	      setTankPicTech(new boolean[10]);
	      setJuggerPicTech(new boolean[10]);
	      setBomberPicTech(new boolean[5]);

			while(weapc<21) {

				if(Integer.parseInt(holdThis.substring(0,holdThis.indexOf(",")))==1) weaps[weapc] = true; else weaps[weapc] = false;
				 holdThis = holdThis.substring(holdThis.indexOf(",")+1,holdThis.length());

				weapc++;
			}
			
			this.setWeap(weaps); //this.weap is a boolean array of weap techs,
			// we use a local form here for AUs, I think.
			weapc=0;
			
			holdThis = rs.getString(22);//soldier string
			 holdThis1=rs.getString(23);//tank
			 holdThis2=rs.getString(24);//juggernaught
			 holdThis3=rs.getString(25);//bomber

			while(weapc<10) {

				if(Integer.parseInt(holdThis.substring(0,holdThis.indexOf(",")))==1) getSoldierPicTech()[weapc] = true; else getSoldierPicTech()[weapc] = false;
				 holdThis = holdThis.substring(holdThis.indexOf(",")+1,holdThis.length());

				if(Integer.parseInt(holdThis1.substring(0,holdThis1.indexOf(",")))==1) getTankPicTech()[weapc] = true; else getTankPicTech()[weapc] = false;
					 holdThis1 = holdThis1.substring(holdThis1.indexOf(",")+1,holdThis1.length());
					 
				if(Integer.parseInt(holdThis2.substring(0,holdThis2.indexOf(",")))==1) getJuggerPicTech()[weapc] = true; else getJuggerPicTech()[weapc] = false;
						 holdThis2 = holdThis2.substring(holdThis2.indexOf(",")+1,holdThis2.length());

				if(weapc<5) { // special for bombers
				if(Integer.parseInt(holdThis3.substring(0,holdThis3.indexOf(",")))==1) getBomberPicTech()[weapc] = true; else getBomberPicTech()[weapc] = false;
							 holdThis3 = holdThis3.substring(holdThis3.indexOf(",")+1,holdThis3.length()); }
				weapc++;
			}


			// now for each player we must retrieve au data...
			
			 aus = con.createStatement();
			 aurs = aus.executeQuery("select * from attackunit where pid = " + ID); // should find six units.


			 au = new ArrayList<AttackUnit>();

			
			while(aurs.next()) {
				int type = aurs.getInt(8);
				int popSize = 0;
				switch(type) {
				case 1: 
					popSize=1;
					break;
				case 2:
					popSize=5;
					break;
				case 3:
					popSize=10;
					break;
				case 4:
					popSize=20;
					break;
				}
				  weapons = aurs.getString(9); // weaps getting time...which weapons are equipped?
				if(weapons==null) weapons="";
			       weapc = 0; 
			        holdPart = weapons;
			       while(!holdPart.equals("")) {
			    	   holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
			    	   weapc++;
			       }
			       
			     weapforau = new int[weapc]; 
			       
			       weapc=0;
					while(weapc<weapforau.length) {

						weapforau[weapc]=Integer.parseInt(weapons.substring(0,weapons.indexOf(",")));
						
						weapons = weapons.substring(weapons.indexOf(",")+1,weapons.length());

						weapc++;
					}
				
				
				au.add(new AttackUnit(aurs.getString(1), aurs.getDouble(4), aurs.getDouble(5), 
						aurs.getDouble(6), aurs.getDouble(7), aurs.getInt(3), popSize, weapforau,aurs.getInt(10)));
			}
			
			aurs.close();aus.close(); 
			 aus = con.createStatement();
			 aurs = aus.executeQuery("select * from autemplate where pid = " + ID); // should find six units.

			// setAUTemplates(new ArrayList<AttackUnit>());
				AUTemplates = new ArrayList<AttackUnit>();

				while(aurs.next()) {
					int type = aurs.getInt(7);
					int popSize = 0;
					switch(type) {
					case 1: 
						popSize=1;
						break;
					case 2:
						popSize=5;
						break;
					case 3:
						popSize=10;
						break;
					case 4:
						popSize=20;
						break;
					}
					 weapons = aurs.getString(8); // weaps getting time...which weapons are equipped?
					if(weapons==null) weapons="";
				       weapc = 0; 
				        holdPart = weapons;
				       while(!holdPart.equals("")) {
				    	   holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
				    	   weapc++;
				       }
				       
				        weapforau = new int[weapc]; 
				       
				       weapc=0;
						while(weapc<weapforau.length) {

							weapforau[weapc]=Integer.parseInt(weapons.substring(0,weapons.indexOf(",")));
							
							weapons = weapons.substring(weapons.indexOf(",")+1,weapons.length());

							weapc++;
						}
					
					//	public AttackUnit(String name, double conc, double armor, double cargo, double speed, int slot, int popSize, int weap[], int graphicNum) {

					getAUTemplates().add(new AttackUnit(aurs.getString(1), aurs.getDouble(3), aurs.getDouble(4), 
							aurs.getDouble(5), aurs.getDouble(6), -1, popSize, weapforau,aurs.getInt(9))); // slot is -1 for template units, no slots, bitches.
				} // NOTICE THE DISCREPANCY, 9 V 10 ON LAST ARGUMENT, DUE TO AUTEMPLATES NOT HAVING A SLOT COLUMN! WATCHHHH!

			 aurs.close(); aus.close();
			 
			// time to get town stuff.
			 tus = con.createStatement();

			 trs = tus.executeQuery("select * from town where pid = " + ID);
			

			// need to establish all.
			
			while(trs.next()) {
				
			// There we go, we've got everything we need now.
			//	public Town(long res[], ArrayList<AttackUnit> au, int townID, String townName, int x, int y) {
		int	i=0;
			boolean found = false;
			while(i<towns.size()) {
				if(towns.get(i).townID==trs.getInt(1)) {
					t=	towns.get(i);
					t.synchronize();
					found=true;
					break;
				}
				i++;
			}
			if(!found) towns.add(God.findTown(trs.getInt(1)));
			// means somebody added a new town. Town will take care of player switch.
			}
			trs.close(); tus.close();

		
			// crapola, need to laod the player now.
			synchronize=false;
			
}

	
	public void populationCheck() {
		/*
		 * This method checks all the population numbers so that it's all saved correctly.
		 * 
		 * 
		 */
		try {
		int totalpopulation=0;
		
		//	public static int getTotalSize(AttackUnit au, Player p) {
		int i = 0;
		ArrayList<AttackUnit> au = getAu();
		ArrayList<Building> bldg;
		while(i<au.size()) {
			totalpopulation+=God.getTotalSize(au.get(i),this)*au.get(i).getPopSize();
			i++;
		}

		// now we need to find all the fucking various civvies.
		int totalplayerscholars = 0; int totalplayermessengers=0;
		i = 0; Town t; Building b; AttackUnit a;
		ArrayList<Town> towns = towns();
		while(i<towns.size()) {
			int totaleng=0,totaltrad=0,totalschol=0,totalmess=0;
			t = towns.get(i);
			int j = 0;
			bldg = t.bldg();
			while(j<bldg.size()) {
				b = bldg.get(j);
				if(b.getType().equals("Construction Yard")) totaleng+=b.getPeopleInside();
				else if(b.getType().equals("Trade Center")) totaltrad+=b.getPeopleInside();
				else if(b.getType().equals("Institute")) totalschol+=b.getPeopleInside();
				else if(b.getType().equals("Communications Center")) totalmess+=b.getPeopleInside();

				j++;
			}
			j=0;
			au = t.getAu();
			while(j<au.size()) {
				a = au.get(j);
				if(a.getName().equals("Engineer")) totaleng+=a.getSize()*a.getPopSize();
				if(a.getName().equals("Trader")) totaltrad+=a.getSize()*a.getPopSize();
				if(a.getName().equals("Scholar")) totalschol+=a.getSize()*a.getPopSize();
				if(a.getName().equals("Messenger")) totalmess+=a.getSize()*a.getPopSize();


				j++;
			}
			
			// now we update.
			
			long pop = t.getPop();
			pop=totaleng+totaltrad+totalschol+totalmess;
			if(pop==0) pop=1; // to make sure it still works!
			totalpopulation+=pop; // okay now we know the town pop correctly we can add it.
			
			t.getRes()[4]=(pop);
			totalplayerscholars+=totalschol;
			totalplayermessengers+=totalmess;
			i++;
		}

		setTotalScholars(totalplayerscholars);
		setTotalPopulation(totalpopulation);
		
		} catch(NullPointerException exc) { }
	}
	public int getPremiumTimer() {
		return premiumTimer;
	}


	public void setPremiumTimer(int premiumTimer) {
		this.premiumTimer = premiumTimer;
	}


	public int getBp() {
		return bp;
	}


	public void setBp(int bp) {
		this.bp = bp;
	}


	public void iterate(int num) {
		// this method is now only for Id.
		
		
	}
	synchronized public void save() {
		// saves everything but AUTemplates, which alter themselves on change.
		try {
		      UberStatement stmt= con.createStatement();
		      ResultSet rs = stmt.executeQuery("select chg,last_login from player where pid = " + ID);
		      rs.next();
		      //	       last_login=rs.getTimestamp(41);

		      if(rs.getInt(1)==0&&(rs.getTimestamp(2).getTime()!=last_login.getTime()||owedTicks==0||(owedTicks>0&&(God.gameClock-owedTicks)<God.saveWhenTicks))) {
		    	  System.out.println("Actually saving " + getUsername() + " and his owedTicks was " + owedTicks + " and gameClock diff is " + (God.gameClock-owedTicks) + " compared to " + God.saveWhenTicks + " and his last_login saved was " + rs.getTimestamp(2) + " and actual was " +last_login);
		    	  // when do we save with owedTicks?
		    	  // we know if owedTicks is less than saveWhenTicks, then
		    	  // the player has been changed since last save. 
		    	  // because if the last save was 10 ticks ago, and owedTicks is 5,
		    	  // that means the player stopped cycling at 5 ticks ago, needs a save.
		    	  // If owed ticks is 15, player stopped cycling 15 ticks ago, and 
		    	  // that player was saved.
		    	  // Bad situation: Player's owed ticks are incremented just beyond
		    	  // the save limit and he logs in, putting him in line for a save,
		    	  // but the server shuts down without him. Then he loses stuff, loses
		    	  // that he should even be saved for a bit. But this is always true.
		   int co = 0;
	       String weapStr = "";
	       boolean weap[] = getWeapTech();
	      while(co<weap.length) {
	    	  if(weap[co]) weapStr+="1,";
	    	  else weapStr+="0,";
	    	  
	    	  co++;
	      }
	      co=0;
	    String  soldierPicStr="";
	      while(co<soldierPicTech.length) {
	    	  if(soldierPicTech[co]) soldierPicStr+="1,";
	    	  else soldierPicStr+="0,";
	    	  
	    	  co++;
	      }
	      co=0;
	     String tankPicStr="";
	      while(co<tankPicTech.length) {
	    	  if(tankPicTech[co]) tankPicStr+="1,";
	    	  else tankPicStr+="0,";
	    	  
	    	  co++;
	      }
	      co=0;
	     String juggerPicStr="";
	      while(co<juggerPicTech.length) {
	    	  if(juggerPicTech[co]) juggerPicStr+="1,";
	    	  else juggerPicStr+="0,";
	    	  
	    	  co++;
	      }
	      co=0;
	     String bomberPicStr="";
	      while(co<bomberPicTech.length) {
	    	  if(bomberPicTech[co]) bomberPicStr+="1,";
	    	  else bomberPicStr+="0,";
	    	  
	    	  co++;
	      }
	     String  updatePlayer = "update player set stealth = " + stealth + ", totalscho = " + totalScholars + ", totalpop = " + totalPopulation + ", alotTech = " + aLotTech + ", soldTech = " + soldierTech + ", tankTech = " + tankTech + 
	    		  ", juggerTech = " + juggerTech +  ", bomberTech = " + bomberTech + ", afTech = " + afTech + ", bunkerTech = " + bunkerTech + ", weapontech = '" + weapStr+"', buildingSlotTech = " + buildingSlotTech + ", civWeap = " + civWeapChoice +
	    		   ", townTech = " + townTech +  ", tradeTech = " + tradeTech +", suppTech = " + supportTech +   ", soldierPicTech = '" + soldierPicStr + "', tankPicTech = '" + tankPicStr
	    		   + "', juggerPicTech = '" + juggerPicStr + "', bomberPicTech = '" + bomberPicStr + "', supportstaff = " + supportstaff + ", tradeTech = " + tradeTech +", lotTech = " + lotTech + ", stabilityTech = " + stabilityTech + 
	    		   ", scholTicks = " + scholTicks + ", playedTicks = " + playedTicks +", bp = " + bp + ", premiumTimer = " + premiumTimer+ ", ubTimer = " + ubTimer + ", mineTimer = " + mineTimer + ", feroTimer = " + feroTimer +
	    		   ", timberTimer = "  + timberTimer + ", mmTimer = " + mmTimer + ", fTimer = " + fTimer + ", knowledge = " + knowledge +/* KNOWLEDGE WAS BRKTHRUS ", brkups = " + brkups +*/", engTech = " + engTech + ", scholTech = " + scholTech  +", commsCenterTech = " + commsCenterTech + ", capitaltid = " + capitaltid 
	    		   +", revTimer = " + revTimer +", totalBPEarned = " + totalBPEarned +", flicker = '" + flicker + "', fuid = " + fuid + ", totalTimePlayed = " + totalTimePlayed + ", numLogins = " + numLogins + ", last_login = '" + (last_login).toString() + "', last_session = '" + (last_session).toString() 
	    		   	+"', last_auto_blast =" + last_auto_blast + ",tPushes = "+tPushes+", zeppTech = " + zeppTech + ", recycTech = " + recyclingTech + ", nukeTech = " + missileSiloTech + ", metalRefTech = " + metalRefTech
	    		   	+ ", timberRefTech = " + timberRefTech + ", manMatRefTech = " + manMatRefTech + ", foodRefTech = " + foodRefTech +", attackAPI = " + attackAPI +
	    		   	", advancedAttackAPI = " + advancedAttackAPI+", digAPI = " + digAPI + ", tradingAPI = " + tradingAPI + ", advancedTradingAPI = " + advancedTradingAPI + ", smAPI = " + smAPI + ", researchAPI = " + researchAPI + 
	    		   	", buildingAPI = " + buildingAPI + ", advancedBuildingAPI = " + advancedBuildingAPI + ", messagingAPI = " + messagingAPI + ", zeppelinAPI = " + zeppelinAPI + 
	    		   	", completeAnalyticAPI = " + completeAnalyticAPI +", version = '" + version + "', nukeAPI = " + nukeAPI +", worldMapAPI = " + worldMapAPI +", owedTicks = " + owedTicks+ ", email ='"+ email +"', pushLog = \"" + pushLog + "\", password = '" + password +  "' where pid = " + ID + ";";
	      stmt.executeUpdate(updatePlayer);
	      
	      // First, let's get and write to au. I'm not sure if querying first is more labor intensive than just writing. Let's not
	      // worry just yet!
	      
	      co = 0;

	      String[] updateAU = new String[6];
	      AttackUnit hau;
	      String weapons;
	      try {
	      while(co<6) {
	    	   hau = getAu().get(co);
	    	   weapons="";
	    	  int j = 0;
	    	  while(j<hau.getWeap().length) { // this holds which weapon is what. And all that shizzit.
	    		  weapons+=hau.getWeap()[j]+",";
	    		  j++;
	    		  
	    	  }
	    	  int type;
	    	  if(hau.getPopSize()==1) type = 1;
	    	  else if(hau.getPopSize()==5) type = 2;
	    	  else if(hau.getPopSize()==10) type = 3;
	    	  else if(hau.getPopSize()==20) type=4;
	    	  else if(hau.getPopSize()==0) type=5;
	    	  else type = 6; // means somebody went-a-hackin'.
	    	  updateAU[co] = "update attackunit set name = \"" + hau.getName() + "\", slot = " + hau.getSlot() + ", conc = " + hau.getConcealment() + 
	    	  ", armor = " + hau.getArmor() + ", cargo = " + hau.getCargo() + ", speed = " + hau.getSpeed() + ", type = " +type + ", weapons = '" +
	    	  weapons + "' where pid = " + ID +" and slot = "+  hau.getSlot() + ";";

	    	  stmt.executeUpdate(updateAU[co]);
	    	  co++;
	      }   } catch(Exception exc) { exc.printStackTrace(); System.out.println("Save saved for " + getUsername()); }
	      rs.close();
	      stmt.close(); // we close these in this if statement so that they don't wait while towns save.
			int i = 0;
			ArrayList<Town> towns = towns();
			while(i<towns.size()) {
				try {
				if(towns.get(i).getPlayer()!=null) // if it's a quest just being made, then p is null even though there is a player with
					// the town attached...because we discard THAT player and reload it as a QuestListener.
					towns.get(i).save();
				} catch(Exception exc) { exc.printStackTrace(); System.out.println("Save for town " + towns.get(i).townID + " is saved."); }
				i++;
			}
		      } else { // we use else statement to close this stuff if we can't iterate, since we close it up there
		    	  // in the if statement to prevent that statement getting plucked away by UberConnection!
		    	 if(rs.getInt(1)==0){
		    		 stmt.execute("update player set owedTicks =  "+owedTicks + " where pid = " + ID+";"); // got to save owed ticks.
		    		 int i = 0;
		    		 while(i<towns().size()) {
			    		 stmt.execute("update town set owedTicks =  "+towns().get(i).owedTicks + " where tid = " + towns().get(i).townID + ";"); // got to save owed ticks.

		    			 i++;
		    		 }
		    	 }
			      rs.close();
			      stmt.close();
		      }
		    
		}catch(SQLException exc) { exc.printStackTrace(); }
		
	}
	      
		
	
	public int getStealth() {
		return getStealthTech();
	}
	/*public boolean isLoggedIn() {
		if()
	}*/
	public void setLeagueBool(boolean league) {
		this.isLeague=league;
	}
	public boolean isLeague() {
		return isLeague;
	/*	boolean l=false;
		try {
			UberStatement stmt = con.createStatement();
		ResultSet rs= stmt.executeQuery("select pid from league where pid = "+ ID);
		int league=-1;
		if(rs.next()) league=rs.getInt(1);
		rs.close();
		stmt.close();
		if(league!=-1) l=true;
		} catch(SQLException exc) { exc.printStackTrace(); }	
		return l;*/
		
	}

	public boolean isAttackAPI() {
		return attackAPI;
	}


	public void setAttackAPI(boolean attackAPI) {
		this.attackAPI = attackAPI;
	}


	public boolean isAdvancedAttackAPI() {
		return advancedAttackAPI;
	}


	public void setAdvancedAttackAPI(boolean advancedAttackAPI) {
		this.advancedAttackAPI = advancedAttackAPI;
	}


	public boolean isTradingAPI() {
		return tradingAPI;
	}


	public void setTradingAPI(boolean tradingAPI) {
		this.tradingAPI = tradingAPI;
	}


	public boolean isAdvancedTradingAPI() {
		return advancedTradingAPI;
	}


	public void setAdvancedTradingAPI(boolean advancedTradingAPI) {
		this.advancedTradingAPI = advancedTradingAPI;
	}


	public boolean isSmAPI() {
		return smAPI;
	}


	public void setSmAPI(boolean smAPI) {
		this.smAPI = smAPI;
	}


	public boolean isResearchAPI() {
		return researchAPI;
	}


	public void setResearchAPI(boolean researchAPI) {
		this.researchAPI = researchAPI;
	}


	public boolean isBuildingAPI() {
		return buildingAPI;
	}


	public void setBuildingAPI(boolean buildingAPI) {
		this.buildingAPI = buildingAPI;
	}


	public boolean isAdvancedBuildingAPI() {
		return advancedBuildingAPI;
	}


	public void setAdvancedBuildingAPI(boolean advancedBuildingAPI) {
		this.advancedBuildingAPI = advancedBuildingAPI;
	}


	public boolean isMessagingAPI() {
		return messagingAPI;
	}


	public void setMessagingAPI(boolean messagingAPI) {
		this.messagingAPI = messagingAPI;
	}


	public boolean isZeppelinAPI() {
		return zeppelinAPI;
	}


	public void setZeppelinAPI(boolean zeppelinAPI) {
		this.zeppelinAPI = zeppelinAPI;
	}


	public boolean isCompleteAnalyticAPI() {
		return completeAnalyticAPI;
	}


	public void setCompleteAnalyticAPI(boolean completeAnalyticAPI) {
		this.completeAnalyticAPI = completeAnalyticAPI;
	}


	public boolean isNukeAPI() {
		return nukeAPI;
	}


	public void setNukeAPI(boolean nukeAPI) {
		this.nukeAPI = nukeAPI;
	}


	public void setAu(AttackUnit hau) {
		int i = 0;
		ArrayList<AttackUnit> au = getAu();
		AttackUnit a;
		while(i<au.size()) {
			a = au.get(i);
			if(a.getSlot()==hau.getSlot()) au.set(i,hau);
			i++;
		}
	}
	public void saveAu(AttackUnit hau) {
		
	try {
	
	    	  
	    	   String weapons="";
	    	  int j = 0;
	    	  while(j<hau.getWeap().length) { // this holds which weapon is what. And all that shizzit.
	    		  weapons+=hau.getWeap()[j]+",";
	    		  j++;
	    		  
	    	  }
	    	  
	    	  int type;
	    	  if(hau.getPopSize()==1) type = 1;
	    	  else if(hau.getPopSize()==5) type = 2;
	    	  else if(hau.getPopSize()==10) type = 3;
	    	  else if(hau.getPopSize()==20) type=4;
	    	  else if(hau.getPopSize()==0) type=5;
	    	  else type = 6; // means somebody went-a-hackin'.
		
		  	 String updateAU = "update attackunit set name = \"" + hau.getName() + "\", slot = " + hau.getSlot() + ", conc = " + hau.getConcealment() + 
			  ", armor = " + hau.getArmor() + ", cargo = " + hau.getCargo() + ", speed = " + hau.getSpeed() + ", type = " +type + ", weapons = '" +
			  weapons + "' where pid = " + ID +" and slot = "+  hau.getSlot() + ";";
		  	 UberStatement stmt = con.createStatement();
			  stmt.executeUpdate(updateAU);
			  stmt.close();
		 } catch(SQLException exc) { exc.printStackTrace(); } 
	}
	public ArrayList<AttackUnit> getAu() {
		return au;
	}
	public ArrayList<AttackUnit> getMemAu() {
		ArrayList<AttackUnit> au = new ArrayList<AttackUnit>();

		try{ 
		UberStatement aus = con.createStatement();
		 ResultSet aurs = aus.executeQuery("select * from attackunit where pid = " + ID); // should find six units.



		String weapons; int weapc = 0; String holdPart; int weapforau[];
		while(aurs.next()) {
			int type = aurs.getInt(8);
			int popSize = 0;
			switch(type) {
			case 1: 
				popSize=1;
				break;
			case 2:
				popSize=5;
				break;
			case 3:
				popSize=10;
				break;
			case 4:
				popSize=20;
				break;
			}
			 weapons = aurs.getString(9); // weaps getting time...which weapons are equipped?
			if(weapons==null) weapons="";
		       weapc = 0; 
		        holdPart = weapons;
		       while(!holdPart.equals("")) {
		    	   holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
		    	   weapc++;
		       }
		       
		        weapforau = new int[weapc]; 
		       
		       weapc=0;
				while(weapc<weapforau.length) {

					weapforau[weapc]=Integer.parseInt(weapons.substring(0,weapons.indexOf(",")));
					
					weapons = weapons.substring(weapons.indexOf(",")+1,weapons.length());

					weapc++;
				}
			
			
			au.add(new AttackUnit(aurs.getString(1), aurs.getDouble(4), aurs.getDouble(5), 
					aurs.getDouble(6), aurs.getDouble(7), aurs.getInt(3), popSize, weapforau,aurs.getInt(10)));
		}
		
		aurs.close();aus.close(); 
	} catch(SQLException exc) { exc.printStackTrace(); }
	return au;

	
	}
	
	public ArrayList<Town> towns() {
		if(towns==null) {
			towns = new ArrayList<Town>();
		ArrayList<Town> totalTowns = God.getTowns();
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select tid from town where pid = " + ID);
			while(rs.next()) {
		
				int i = 0;
				while(i<totalTowns.size()) {
					if(totalTowns.get(i).townID==rs.getInt(1)) {
						towns.add(totalTowns.get(i));
						//totalTowns.get(i).setPlayer(this);
					}
					i++;
				}
			}

			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		
		}
		return towns;
	}
	public void removeTown(Town t) {
		towns();
		towns.remove(t);
	}
	public void addTown(Town t) {
		towns();
		towns.add(t);
	}
	public String getPushLog() {
		return pushLog;
	}
	public void setPushLog(String pushLog) {
		this.pushLog=pushLog;
	}
	public ArrayList<Town> getMemTowns() {
		ArrayList<Town> towns= new ArrayList<Town>();
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select tid from town where pid = " + ID);
			while(rs.next()) {
				towns.add(new Town(rs.getInt(1),God));
			}

			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		
		return towns;
	}
	public void setMemSupportstaff(boolean supportstaff) {
		setBoolean("supportstaff",supportstaff);
	}
	public boolean getMemSupportstaff() {
		return getBoolean("supportstaff");
	}
	public void setSupportstaff(boolean supportstaff) {
		this.supportstaff=supportstaff;
	}
	public boolean getSupportstaff() {
		return supportstaff;
	}
	
	public PlayerScript getPs() {
		return ps;
	}
	public void setMemCommsCenterTech(int commsCenterTech) {
		setInt("commsCenterTech",commsCenterTech);
	}
	public int getMemCommsCenterTech() {
		return getInt("commsCenterTech");
	}
	public void setCommsCenterTech(int commsCenterTech) {
		this.commsCenterTech=commsCenterTech;
	}
	public int getCommsCenterTech() {
		return commsCenterTech;
	}
	
	
	
	public League getMemLeague() {
		League l=null;
		try {
			UberStatement stmt = con.createStatement();
		ResultSet rs= stmt.executeQuery("select league_pid from tpr where pid = "+ ID);
		int league=-1;
		if(rs.next()) league=rs.getInt(1);
		rs.close();
		stmt.close();
		if(league==-1) return null;
		else l= new League(league,God);
		} catch(SQLException exc) { exc.printStackTrace(); }	
		return l;
	}
	
	public League getLeague() {
		if(league==null) {
			int lpid=-1; int type=-1;
			try {
				UberStatement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("select league_pid,type from tpr where pid = " +ID);
				if(rs.next()) { lpid = rs.getInt(1); type=rs.getInt(2);}
				rs.close();stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }
			if(lpid==-1||type==-1) return null;
			int i = 0;
			ArrayList<Player> players = God.getPlayers();
			if(players!=null)
			while(i<players.size()) {
				if(players.get(i).ID==lpid){ league = ((League) players.get(i)); break;}
				i++;
			}
		}
		return league;
	}
	public int getKnowledge() {
		return knowledge;
	}
	public void setKnowledge(int knowledge) {
		this.knowledge=knowledge;
	}
	public void setMemUsername(String username) {
		setString("username",username);
	}
	public String getMemUsername() {
		return getString("username");
	}
	public void setUsername(String username) {
		this.username=username;
	}
	public String getUsername() {
		return username;
	}
	public void setMemCapitaltid(int capitaltid) {
		setInt("capitaltid",capitaltid);
	}
	public int getMemCapitaltid() {
		return getInt("capitaltid");
	}
	public void setCapitaltid(int capitaltid) {
		this.capitaltid=capitaltid;
	}
	public int getTPushes() {
		return tPushes;
	}


	public void setTPushes(int pushes) {
		tPushes = pushes;
	}


	public boolean isZeppTech() {
		return zeppTech;
	}


	public void setZeppTech(boolean zeppTech) {
		this.zeppTech = zeppTech;
	}


	public boolean isMissileSiloTech() {
		return missileSiloTech;
	}


	public void setMissileSiloTech(boolean missileSiloTech) {
		this.missileSiloTech = missileSiloTech;
	}


	public boolean isRecyclingTech() {
		return recyclingTech;
	}


	public void setRecyclingTech(boolean recyclingTech) {
		this.recyclingTech = recyclingTech;
	}


	public boolean isMetalRefTech() {
		return metalRefTech;
	}


	public void setMetalRefTech(boolean metalRefTech) {
		this.metalRefTech = metalRefTech;
	}


	public boolean isTimberRefTech() {
		return timberRefTech;
	}


	public void setTimberRefTech(boolean timberRefTech) {
		this.timberRefTech = timberRefTech;
	}


	public boolean isManMatRefTech() {
		return manMatRefTech;
	}


	public void setManMatRefTech(boolean manMatRefTech) {
		this.manMatRefTech = manMatRefTech;
	}


	public boolean isFoodRefTech() {
		return foodRefTech;
	}


	public void setFoodRefTech(boolean foodRefTech) {
		this.foodRefTech = foodRefTech;
	}


	public int getCapitaltid() {
		return capitaltid;
	}
	public void setMemWeap(boolean weap[]) {
		   
	      int co = 0;
	      String  weapStr = "";
	  
	      while(co<weap.length) {
	    	  if(weap[co]) weapStr+="1,";
	    	  else weapStr+="0,";
	    	  
	    	  co++;
	      }	
	      
	      setString("weaponTech",weapStr);
	      
	}
	public void setWeap(boolean[] weap) {
		this.weapTech=weap;
	}
	public boolean[] getMemWeap() {
	      int weapc = 0; boolean[] weaps = new boolean[21];
	      String holdThis = getString("weaponTech");
			while(weapc<21) {

				if(Integer.parseInt(holdThis.substring(0,holdThis.indexOf(",")))==1) weaps[weapc] = true; else weaps[weapc] = false;
				 holdThis = holdThis.substring(holdThis.indexOf(",")+1,holdThis.length());

				weapc++;
			}
			
			return weaps;
			
	}
	public boolean[] getWeap() {
		return weapTech;
	}
	public void setMemALotTech(int aLotTech) {
		setInt("alotTech",aLotTech);
	}
	public int getMemALotTech() {
		return getInt("alotTech");
	}
	public void setALotTech(int aLotTech) {
		this.aLotTech=aLotTech;
	}
	public int getALotTech() {
		return aLotTech;
	}
	
	/**
	 * If 0, will add. If 1, will delete. If 2, will edit.
	 * @param hau
	 * @param addDelEdit
	 */
	public void setMemAUTemplate(AttackUnit hau, int addDelEdit) {
		try {
			  UberStatement stmt = con.createStatement();
			  
	    	   String weapons="";
	    	   
	    	  int j = 0;
	    	  while(j<hau.getWeap().length) { // this holds which weapon is what. And all that shizzit.
	    		  weapons+=hau.getWeap()[j]+",";
	    		  j++;
	    		  
	    	  }
	    	  
	    	  int type;
	    	  if(hau.getPopSize()==1) type = 1;
	    	  else if(hau.getPopSize()==5) type = 2;
	    	  else if(hau.getPopSize()==10) type = 3;
	    	  else if(hau.getPopSize()==20) type=4;
	    	  else if(hau.getPopSize()==0) type=5;
	    	  else type = 6; // means somebody went-a-hackin'.
	    	  switch(addDelEdit) {
	    	  case 0:
	    		  stmt.executeUpdate("insert into autemplate(name,pid,conc,armor,cargo,speed,type,weapons,graphic) values ('" +
	    				  hau.getName() + "'," + ID + "," + hau.getConcealment() + "," + hau.getArmor() + "," + hau.getCargo() + "," + hau.getSpeed() +
	    				  "," + type + ",'" + weapons + "'," + hau.getGraphicNum() + ");");
	    	  break;
	    	  case 1:
	    		  stmt.executeUpdate("delete from autemplate where pid = " + ID + " and name = '" + hau.getName() + "';");
	    		  break;
	    	  case 2:
	    		  stmt.executeUpdate("update autemplate set name = \"" + hau.getName() + "\", conc = " + hau.getConcealment() + 
		    	  ", armor = " + hau.getArmor() + ", cargo = " + hau.getCargo() + ", speed = " + hau.getSpeed() + ", type = " +type + ", weapons = '" +
		    	  weapons + "', graphic = " + hau.getGraphicNum() + " where pid = " + ID +" and name = '"+  hau.getName() + "';");
	    		  break;
	    	  }
	    	 
	    	  stmt.close();
			
		} catch(SQLException exc) { exc.printStackTrace(); }
	
	
	}
	public void setAUTemplate(AttackUnit hau) {
		int i = 0;
		ArrayList<AttackUnit> AUTemplates = getAUTemplates();
		while(i<AUTemplates.size()) {
			if(AUTemplates.get(i).getName().equals(hau.getName())) AUTemplates.set(i,hau);
			i++;
		}
	}
	public ArrayList<AttackUnit> getMemAUTemplates() {
		 ArrayList<AttackUnit> AUTemplates = new ArrayList<AttackUnit>();


			try{ 
			UberStatement aus = con.createStatement();
			 ResultSet aurs = aus.executeQuery("select * from autemplate where pid = " + ID); // should find six units.



			String weapons; int weapc = 0; String holdPart; int weapforau[];
		
			

			while(aurs.next()) {
				int type = aurs.getInt(7);
				int popSize = 0;
				switch(type) {
				case 1: 
					popSize=1;
					break;
				case 2:
					popSize=5;
					break;
				case 3:
					popSize=10;
					break;
				case 4:
					popSize=20;
					break;
				}
				 weapons = aurs.getString(8); // weaps getting time...which weapons are equipped?
				if(weapons==null) weapons="";
			       weapc = 0; 
			        holdPart = weapons;
			       while(!holdPart.equals("")) {
			    	   holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
			    	   weapc++;
			       }
			       
			        weapforau = new int[weapc]; 
			       
			       weapc=0;
					while(weapc<weapforau.length) {

						weapforau[weapc]=Integer.parseInt(weapons.substring(0,weapons.indexOf(",")));
						
						weapons = weapons.substring(weapons.indexOf(",")+1,weapons.length());

						weapc++;
					}
				
				//	public AttackUnit(String name, double conc, double armor, double cargo, double speed, int slot, int popSize, int weap[], int graphicNum) {

				AUTemplates.add(new AttackUnit(aurs.getString(1), aurs.getDouble(3), aurs.getDouble(4), 
						aurs.getDouble(5), aurs.getDouble(6), -1, popSize, weapforau,aurs.getInt(9))); // slot is -1 for template units, no slots, bitches.
			} // NOTICE THE DISCREPANCY, 9 V 10 ON LAST ARGUMENT, DUE TO AUTEMPLATES NOT HAVING A SLOT COLUMN! WATCHHHH!

		 aurs.close(); aus.close();
			} catch(SQLException exc) { exc.printStackTrace(); }
			return AUTemplates;
		}
	
	public ArrayList<AttackUnit> getAUTemplates() {
		return AUTemplates;
	}
	public void setMemTownTech(int townTech) {
		setInt("townTech",townTech);
	}
	public int getMemTownTech() {
		return getInt("townTech");
	}
	public void setTownTech(int townTech) {
		this.townTech=townTech;
	}
	public int getTownTech() {
		return townTech;
	}
	public void setMemCivWeapChoice(int civWeapChoice) {
		setInt("civWeap",civWeapChoice);
	}
	public int getMemCivWeapChoice() {
		return getInt("civWeap");
	}
	public void setCivWeapChoice(int civWeapChoice) {
		this.civWeapChoice=civWeapChoice;
	}
	public int getCivWeapChoice() {
		return civWeapChoice;
	}
	public void setMemBrkthrus(int brkthrus) {
		setInt("brkthru",brkthrus);
	}
	public int getMemBrkthrus() {
		return getInt("brkthru");
	}
	/*public void setBrkthrus(int brkthrus) {
		this.brkthrus=brkthrus;
	}
	public int getBrkthrus() {
		return brkthrus;
	}
	public void setMemBrkups(int brkups) {
		setInt("brkups",brkups);
	}
	public int getMemBrkups() {
		return getInt("brkups");
	}
	public void setBrkups(int brkups) {
		this.brkups=brkups;
	}
	public int getBrkups() {
		return brkups;
	}*/
	public void setMemScholTicks(int scholTicks) {
		setInt("scholTicks",scholTicks);
	}
	public int getMemScholTicks() {
		return getInt("scholTicks");
	}
	public void setScholTicks(int scholTicks) {
		this.scholTicks=scholTicks;
	}
	public int getScholTicks() {
		return scholTicks;
	}
	
	public int getScholTicksTotal() {
		return scholTicksTotal;
	}
	public void setMemLotTech(int lotTech) {
		setInt("lotTech",lotTech);
	}
	public int getMemLotTech() {
		return getInt("lotTech");
	}
	public void setLotTech(int lotTech) {
		this.lotTech=lotTech;
	}
	public int getLotTech() {
		return lotTech;
	}
	public void setMemStealthTech(int stealthTech) {
		setInt("stealth",stealthTech);
	}
	public int getMemStealthTech() {
		return getInt("stealth");
	}
	public void setStealthTech(int stealthTech) {
		this.stealth=stealthTech;
	}
	public int getStealthTech() {
		return stealth;
	}
	public void setMemScoutTech(int scoutTech) {
		setInt("scoutTech",scoutTech);
	}
	public int getMemScoutTech() {
		return getInt("scoutTech");
	}
	public void setScoutTech(int scoutTech) {
		this.scoutTech=scoutTech;
	}
	public int getScoutTech() {
		return scoutTech;
	}
	public void setMemSoldierTech(boolean soldierTech) {
		setBoolean("soldTech",soldierTech);
	}
	public boolean isMemSoldierTech() {
		return getBoolean("soldTech");
	}
	public void setSoldierTech(boolean soldierTech) {
		this.soldierTech=soldierTech;
	}
	public boolean isSoldierTech() {
		return soldierTech;
	}
	public void setMemTankTech(boolean tankTech) {
		setBoolean("tankTech",tankTech);
	}
	public boolean isMemTankTech() {
		return getBoolean("tankTech");
	}
	public void setMemJuggerTech(boolean juggerTech) {
		setBoolean("juggerTech",juggerTech);
	}
	public boolean isMemJuggerTech() {
		return getBoolean("juggerTech");
	}
	public void setMemBomberTech(boolean bomberTech) {
		setBoolean("bomberTech",bomberTech);
	}
	public boolean isMemBomberTech() {
		return getBoolean("bomberTech");
	}
	public void setMemSupportTech(int supportTech) {
		setInt("suppTech",supportTech);
	}
	public int getMemSupportTech() {
		return getInt("suppTech");
	}
	public void setMemEngTech(int engTech) {
		setInt("engTech",engTech);
	}
	public int getMemEngTech() {
		return getInt("engTech");
	}
	public void setMemTradeTech(int tradeTech) {
		setInt("tradeTech",tradeTech);
	}
	public int getMemTradeTech() {
		return getInt("tradeTech");
	}
	public void setMemScholTech(int scholTech) {
		setInt("scholTech",scholTech);
	}
	public int getMemScholTech() {
		return getInt("scholTech");
	}
	public void setMemBuildingSlotTech(int buildingSlotTech) {
		setInt("buildingSlotTech",buildingSlotTech);
	}
	public int getMemBuildingSlotTech() {
		return getInt("buildingSlotTech");
	}
	public void setMemStabilityTech(int stabilityTech) {
		setInt("stabilityTech",stabilityTech);
	}
	public int getMemStabilityTech() {
		return getInt("stabilityTech");
	}
	public void setMemBunkerTech(int bunkerTech) {
		setInt("bunkerTech",bunkerTech);
	}
	public int getMemBunkerTech() {
		return getInt("bunkerTech");
	}
	public void setMemAfTech(int afTech) {
		setInt("afTech",afTech);
	}
	public int getMemAfTech() {
		return getInt("afTech");
	}
	public void setMemSoldierPicTech(boolean soldierPicTech[]) {
		   
	      int co = 0;
	      String  weapStr = "";
	  
	      while(co<soldierPicTech.length) {
	    	  if(soldierPicTech[co]) weapStr+="1,";
	    	  else weapStr+="0,";
	    	  
	    	  co++;
	      }	
	      
	      setString("soldierPicTech",weapStr);
	}
	public boolean[] getMemSoldierPicTech() {
	      int weapc = 0; boolean[] weaps = new boolean[10];
	      String holdThis = getString("soldierPicTech");
			while(weapc<10) {

				if(Integer.parseInt(holdThis.substring(0,holdThis.indexOf(",")))==1) weaps[weapc] = true; else weaps[weapc] = false;
				 holdThis = holdThis.substring(holdThis.indexOf(",")+1,holdThis.length());

				weapc++;
			}
		
			return weaps;	
			}
	public void setMemTankPicTech(boolean tankPicTech[]) {
	     int co = 0;
	      String  weapStr = "";
	  
	      while(co<tankPicTech.length) {
	    	  if(tankPicTech[co]) weapStr+="1,";
	    	  else weapStr+="0,";
	    	  
	    	  co++;
	      }	
	      
	      setString("tankPicTech",weapStr);	
	      }
	public boolean[] getMemTankPicTech() {
	      int weapc = 0; boolean[] weaps = new boolean[10];
	      String holdThis = getString("tankPicTech");
			while(weapc<10) {

				if(Integer.parseInt(holdThis.substring(0,holdThis.indexOf(",")))==1) weaps[weapc] = true; else weaps[weapc] = false;
				 holdThis = holdThis.substring(holdThis.indexOf(",")+1,holdThis.length());

				weapc++;
			}
			
			return weaps;		
			}
	public void setMemJuggerPicTech(boolean juggerPicTech[]) {
	     int co = 0;
	      String  weapStr = "";
	  
	      while(co<juggerPicTech.length) {
	    	  if(juggerPicTech[co]) weapStr+="1,";
	    	  else weapStr+="0,";
	    	  
	    	  co++;
	      }	
	      
	      setString("juggerPicTech",weapStr);
	      }
	public boolean[] getMemJuggerPicTech() {
	      int weapc = 0; boolean[] weaps = new boolean[10];
	      String holdThis = getString("juggerPicTech");
			while(weapc<10) {

				if(Integer.parseInt(holdThis.substring(0,holdThis.indexOf(",")))==1) weaps[weapc] = true; else weaps[weapc] = false;
				 holdThis = holdThis.substring(holdThis.indexOf(",")+1,holdThis.length());

				weapc++;
			}
			
			return weaps;		}
	public void setMemBomberPicTech(boolean bomberPicTech[]) {
	     int co = 0;
	      String  weapStr = "";
	  
	      while(co<bomberPicTech.length) {
	    	  if(bomberPicTech[co]) weapStr+="1,";
	    	  else weapStr+="0,";
	    	  
	    	  co++;
	      }	
	      
	      setString("bomberPicTech",weapStr);	
	      }
	public boolean[] getMemBomberPicTech() {
	      int weapc = 0; boolean[] weaps = new boolean[5];
	      String holdThis = getString("bomberPicTech");
			while(weapc<5) {

				if(Integer.parseInt(holdThis.substring(0,holdThis.indexOf(",")))==1) weaps[weapc] = true; else weaps[weapc] = false;
				 holdThis = holdThis.substring(holdThis.indexOf(",")+1,holdThis.length());

				weapc++;
			}
			
			return weaps;		}
	public void setMemPassword(String password) {
		setString("password",password);
	}
	public String getMemPassword() {
		return getString("password");
	}
	public void setMemTotalScholars(int totalScholars) {
		setInt("totalscho",totalScholars);
	}
	public int getMemTotalScholars() {
		return getInt("totalscho");
	}

	public void setMemTotalPopulation(int totalPopulation) {
		setInt("totalpop",totalPopulation);
	}
	public int getMemTotalPopulation() {
		return getInt("totalpop");
	}
	public void setInternalClock(int internalClock) {
		this.internalClock=internalClock;
	}
	public int getInternalClock() {
		return internalClock;
	}

	
	

	public void setHoldingIteratorID(String toSet) {
		holdingIteratorID=toSet;
	}
	public String getHoldingIteratorID() {
		return holdingIteratorID;
	}
	
	public ArrayList<QuestListener> getMemActiveQuests() {
		ArrayList<QuestListener> aq = new ArrayList<QuestListener>();
		try {
			UberStatement stmt = con.createStatement();
			//				qs = qstmt.executeQuery("select qid,questcode,classname from Quest where activated = true and qid = " + ID);

			ResultSet rs = stmt.executeQuery("select qid from qpc where pid = " + ID + " and complete=0;");
			UberStatement stmt2 = con.createStatement(); ResultSet rs2;
			
			while(rs.next()) {
				int qid = rs.getInt(1);
				rs2 = stmt2.executeQuery("select questcode,classname from Quest where qid = " + qid);
				if(rs2.next())
				aq.add(God.loadQuest(qid,rs2.getString(1),rs2.getString(2)));
				rs2.close();
			}
			stmt2.close();
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return aq;
	}
	public void setInt(String fieldName, int toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update player set " + fieldName + " = " + toSet + " where pid = " + ID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setDouble(String fieldName, double toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update player set " + fieldName + " = " + toSet + " where pid = " + ID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setLong(String fieldName, long toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update player set " + fieldName + " = " + toSet + " where pid = " + ID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setBoolean(String fieldName, boolean toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update player set " + fieldName + " = " + toSet + " where pid = " + ID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setString(String fieldName, String toSet) {
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update player set " + fieldName + " = \"" + toSet + "\" where pid = " + ID);
			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public int getInt(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from player where pid = " + ID);
			rs.next();
			int toRet=rs.getInt(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return -1;
	}
	
	
	public double getDouble(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from player where pid = " + ID);
			rs.next();
			double toRet=rs.getDouble(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return -1;
	}
	
	public long getLong(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from player where pid = " + ID);
			rs.next();
			long toRet=rs.getLong(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return -1;
	}
	
	public boolean getBoolean(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from player where pid = " + ID);
			rs.next();
			boolean toRet=rs.getBoolean(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return false;
	}
	public String getString(String toGet) {
		try {
			UberStatement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select " + toGet + " from player where pid = " + ID);
			rs.next();
			String toRet=rs.getString(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return null;
	}

	public boolean exists() {
		boolean toRet=false;
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select pid from player where pid = " + ID);
			if(rs.next()) toRet=true;
			rs.close(); stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return toRet;
	}
	public void setTankTech(boolean tankTech) {
		this.tankTech = tankTech;
	}
	public boolean isTankTech() {
		return tankTech;
	}
	public int getID() {
		return ID;
	}
	public void setID(int id) {
		ID = id;
	}
	public int getBuildingCheckMax() {
		return buildingCheckMax;
	}
	public void setBuildingCheckMax(int buildingCheckMax) {
		this.buildingCheckMax = buildingCheckMax;
	}
	public int getBuildingCheckTimer() {
		return buildingCheckTimer;
	}
	public void setBuildingCheckTimer(int buildingCheckTimer) {
		this.buildingCheckTimer = buildingCheckTimer;
	}
	public int getIterTicks() {
		return iterTicks;
	}
	public void setIterTicks(int iterTicks) {
		this.iterTicks = iterTicks;
	}
	public UberConnection getCon() {
		return con;
	}
	public void setCon(UberConnection con) {
		this.con = con;
	}
	public boolean isSynchronize() {
		return synchronize;
	}
	public void setSynchronize(boolean synchronize) {
		this.synchronize = synchronize;
	}
	public int getScout() {
		return scout;
	}
	public void setScout(int scout) {
		this.scout = scout;
	}
	public int getSupportTech() {
		return supportTech;
	}
	public void setSupportTech(int supportTech) {
		this.supportTech = supportTech;
	}
	public int getEngTech() {
		return engTech;
	}
	public void setEngTech(int engTech) {
		this.engTech = engTech;
	}
	public int getTradeTech() {
		return tradeTech;
	}
	public void setTradeTech(int tradeTech) {
		this.tradeTech = tradeTech;
	}
	public int getScholTech() {
		return scholTech;
	}
	public void setScholTech(int scholTech) {
		this.scholTech = scholTech;
	}
	public int getBuildingSlotTech() {
		return buildingSlotTech;
	}
	public void setBuildingSlotTech(int buildingSlotTech) {
		this.buildingSlotTech = buildingSlotTech;
	}
	public int getStabilityTech() {
		return stabilityTech;
	}
	public void setStabilityTech(int stabilityTech) {
		this.stabilityTech = stabilityTech;
	}
	public int getBunkerTech() {
		return bunkerTech;
	}
	public void setBunkerTech(int bunkerTech) {
		this.bunkerTech = bunkerTech;
	}
	public int getAfTech() {
		return afTech;
	}
	public void setAfTech(int afTech) {
		this.afTech = afTech;
	}
	public int getTotalScholars() {
		return totalScholars;
	}
	public void setTotalScholars(int totalScholars) {
		this.totalScholars = totalScholars;
	}
	public int getTotalPopulation() {
		return totalPopulation;
	}
	public void setTotalPopulation(int totalPopulation) {
		this.totalPopulation = totalPopulation;
	}
	public boolean isJuggerTech() {
		return juggerTech;
	}
	public void setJuggerTech(boolean juggerTech) {
		this.juggerTech = juggerTech;
	}
	public boolean isBomberTech() {
		return bomberTech;
	}
	public void setBomberTech(boolean bomberTech) {
		this.bomberTech = bomberTech;
	}
	public boolean[] getSoldierPicTech() {
		return soldierPicTech;
	}
	public void setSoldierPicTech(boolean[] soldierPicTech) {
		this.soldierPicTech = soldierPicTech;
	}
	public boolean[] getTankPicTech() {
		return tankPicTech;
	}
	public void setTankPicTech(boolean[] tankPicTech) {
		this.tankPicTech = tankPicTech;
	}
	public boolean[] getJuggerPicTech() {
		return juggerPicTech;
	}
	public void setJuggerPicTech(boolean[] juggerPicTech) {
		this.juggerPicTech = juggerPicTech;
	}
	public boolean[] getBomberPicTech() {
		return bomberPicTech;
	}
	public void setBomberPicTech(boolean[] bomberPicTech) {
		this.bomberPicTech = bomberPicTech;
	}
	public ArrayList<QuestListener> getActiveQuests() {
		if(activeQuests==null) {
			ArrayList<QuestListener> aq = God.getAllActiveQuests();
			activeQuests=new ArrayList<QuestListener>();
			int i = 0;
			Player p;
			while(i<aq.size()) {
				 p = aq.get(i).findPlayer(ID);
				 if(p!=null) activeQuests.add(aq.get(i));
				i++;
			}
		}
		return activeQuests;
	}
	public void setActiveQuests(ArrayList<QuestListener> activeQuests) {
		this.activeQuests = activeQuests;
	}
	public ArrayList<Town> getTowns() {
		return towns;
	}
	public void setTowns(ArrayList<Town> towns) {
		this.towns = towns;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setNewPassword(String password) {
		setMemPassword(org.apache.commons.codec.digest.DigestUtils.md5Hex(password));
		try {
			UberStatement stmt = God.con.createStatement();
			stmt.executeUpdate("update users set password = md5("+password+") where username = '"+getUsername()+"';");
			
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		setPassword(org.apache.commons.codec.digest.DigestUtils.md5Hex(password));
		try {
			Hashtable r = (Hashtable) God.accounts.get(getUsername());
			r.put("password",org.apache.commons.codec.digest.DigestUtils.md5Hex(password)); // gotta update accounts!
		} catch(NullPointerException exc) { exc.printStackTrace(); System.out.println("Whatever was setting was saved."); }
	
	}

	public void setNewFuid(long fuid) {
		setLong("fuid",fuid);
		try {
			UberStatement stmt = God.con.createStatement();
			stmt.executeUpdate("update users set fuid = "+fuid+" where username = '"+getUsername()+"';");
			
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		setFuid(fuid);

		try {
			Hashtable r = (Hashtable) God.accounts.get(getUsername());
			r.put("fuid",fuid);
			God.accounts.put(fuid,r); // gotta update accounts.
		} catch(NullPointerException exc) { exc.printStackTrace(); System.out.println("Whatever was setting was saved."); }
		}
	public void setLeague(boolean isLeague) {
		this.isLeague = isLeague;
	}
	public void setLeague(League league) {
		this.league = league;
	}
	public void setGod(GodGenerator god) {
		God = god;
	}
	public void setScholTicksTotal(int scholTicksTotal) {
		this.scholTicksTotal = scholTicksTotal;
	}
	public void setWeapTech(boolean[] weapTech) {
		this.weapTech = weapTech;
	}
	public void setStealth(int stealth) {
		this.stealth = stealth;
	}
	public void setAu(ArrayList<AttackUnit> au) {
		this.au = au;
	}
	public void setAUTemplates(ArrayList<AttackUnit> templates) {
		AUTemplates = templates;
	}



	public void setQuest(boolean isQuest) {
		this.isQuest = isQuest;
	}



	public boolean isQuest() {
		return isQuest;
	}


	public void setUbTimer(int ubTimer) {
		this.ubTimer = ubTimer;
	}


	public int getUbTimer() {
		return ubTimer;
	}


	public void setMineTimer(int mineTimer) {
		this.mineTimer = mineTimer;
	}


	public int getMineTimer() {
		return mineTimer;
	}


	public void setFeroTimer(int feroTimer) {
		this.feroTimer = feroTimer;
	}


	public int getFeroTimer() {
		return feroTimer;
	}


	public void setTimberTimer(int timberTimer) {
		this.timberTimer = timberTimer;
	}


	public int getTimberTimer() {
		return timberTimer;
	}


	public void setMmTimer(int mmTimer) {
		this.mmTimer = mmTimer;
	}


	public int getMmTimer() {
		return mmTimer;
	}


	public void setFTimer(int fTimer) {
		this.fTimer = fTimer;
	}


	public int getFTimer() {
		return fTimer;
	}


	public void setRevTimer(int revTimer) {
		this.revTimer = revTimer;
	}


	public int getRevTimer() {
		return revTimer;
	}


	public void setTotalBPEarned(int totalBPEarned) {
		this.totalBPEarned = totalBPEarned;
	}


	public int getTotalBPEarned() {
		return totalBPEarned;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getEmail() {
		return email;
	}


	public void setFuid(long fuid) {
		this.fuid = fuid;
	}


	public long getFuid() {
		return fuid;
	}


	public void addAchievement(Hashtable r) {
		getAchievements().add(r);
		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("insert into ap (pid,aid) values (" + ID+","+((Integer) r.get("aid"))+");");
			stmt.close();
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
	}


	public ArrayList<Hashtable> getAchievements() {
		if(achievements==null) {
			achievements = new ArrayList<Hashtable>();
			try {
				UberStatement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("select aid from ap where pid = " + ID);
				while(rs.next()) {
					int i = 0;
					while(i<God.getAchievements().length) {
						if(((Integer) God.getAchievements()[i].get("aid"))==rs.getInt(1)){
							achievements.add(God.getAchievements()[i]);
							break;}
							
						i++;
					}
					
				}
				rs.close();
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }
			
		}
		return achievements;
	}


	public void setWorldMapAPI(boolean worldMapAPI) {
		this.worldMapAPI = worldMapAPI;
	}


	public boolean isWorldMapAPI() {
		return worldMapAPI;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public String getVersion() {
		return version;
	}


	public void setBeingDeleted(boolean beingDeleted) {
		this.beingDeleted = beingDeleted;
	}


	public boolean isBeingDeleted() {
		return beingDeleted;
	}


	public void setdigAPI(boolean digAPI) {
		this.digAPI = digAPI;
	}


	public boolean isdigAPI() {
		return digAPI;
	}


	


	public ArrayList<QuestListener> getEventListenerList(String name) {
		ArrayList<QuestListener> l = (ArrayList<QuestListener>) eventListenerLists.get(name);
		if(l==null) return null;
		ArrayList<QuestListener> n = new ArrayList<QuestListener>();
		for(QuestListener q:l) {
			n.add(q);
		}
		return n;
		
	}
}


