package BHEngine;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.UUID;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import BattlehardFunctions.UserMessage;
import BattlehardFunctions.UserMessagePack;
import BattlehardFunctions.UserSR;

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
	private long[] secondaryResBuff;
	public int iterTicks = 0;
	public ArrayList<UserSR> currSRs;
	public ArrayList<UserMessagePack> currMessages;

	public int playedTicks=0;
	public long totalTimePlayed = 0;
	public int numLogins=0;
	private boolean voluntaryVassal;
	 private Hashtable eventListenerLists = new Hashtable();
		private String holdingLordIteratorID = "-1";

	 private Player lord;
	 private double taxRate;
	public Timestamp last_session;
	private int lordInternalClock=0;
	public int owedTicks =0;
	private String version;
	private ArrayList<Hashtable> achievements;
	private ArrayList<Hashtable> territories=new ArrayList<Hashtable>(); 
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
	private boolean airshipTech,clockworkAugments;
	private String email;
	private int mmTimer=0;
	private int fTimer=0;
	private int totalBPEarned=0;
	private int bp=0;
	UberConnection con;
	private boolean facsimile=false;
	private boolean synchronize = false;
	private League  league=null;
	
	public GodGenerator God;
	private int bodyArmor,teslaTech,ordinanceResearch,scholTicks,scholTicksTotal,capitaltid,infrastructureTech,townTech,firearmResearch,knowledge,architecture,scoutTech,clockworkComputers,constructionResearch,structuralIntegrity,bloodMetalPlating,totalScholars,totalPopulation;
	private boolean supportstaff,personalShields,hydraulicAssistors,thrustVectoring,advancedFortifications,bloodMetalArmor,isQuest=false;
	private ArrayList<QuestListener> activeQuests;
	private ArrayList<AttackUnit> au;
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
		   UberPreparedStatement stmt = God.con.createStatement("select * from player where pid = ?;");
		   
		   this.con=God.con;
		   if(ID==0) {
			   facsimile=true;
			   ID = 5; // this means a test player, and we'll use Id as our example player for both sides.
		   }
		   this.ID=ID;
		   this.God=God;
		   stmt.setInt(1,ID);
		   ResultSet rs = stmt.executeQuery();
	       internalClock = God.gameClock;
		   rs.next();
		   username = rs.getString(2);
	       password = rs.getString(26);
	       bodyArmor = rs.getInt(3);
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
	       //aLotTech = rs.getInt(8);
	       last_auto_blast = rs.getInt(59);
	       revTimer = rs.getInt(54);
	       supportstaff = rs.getBoolean(28);
	       airshipTech = rs.getBoolean(61);
	     //  missileSiloTech = rs.getBoolean(62);
	     //  recyclingTech = rs.getBoolean(63);
	       clockworkAugments = rs.getBoolean(64);
	       advancedFortifications = rs.getBoolean(65);
	       bloodMetalArmor = rs.getBoolean(66);
	    //   foodRefTech = rs.getBoolean(67);

	     //  soldierTech = rs.getBoolean(9);
	       personalShields=(rs.getBoolean(10));
	       hydraulicAssistors = rs.getBoolean(11);
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
	       constructionResearch = rs.getInt(13);
	       firearmResearch = rs.getInt(17);
	       thrustVectoring = rs.getBoolean(18);
	      // supportTech = rs.getInt(19);
	       townTech = rs.getInt(20);
	       //advancedFortifications=rs.getInt(21);
	       structuralIntegrity = rs.getInt(30);
	       bloodMetalPlating = rs.getInt(38);
	       scholTicks = rs.getInt(31);
	       ordinanceResearch=(rs.getInt(32));
	       teslaTech=(rs.getInt(33));
	       infrastructureTech = rs.getInt(34);
	       architecture = rs.getInt(35);
	       clockworkComputers = rs.getInt(36);
	   //    tradeTech = rs.getInt(27);
	   //   commsCenterTech = rs.getInt(43);
	       capitaltid = rs.getInt(39);
	       taxRate = rs.getDouble(88);
	       voluntaryVassal=rs.getBoolean(89);
	       pushLog = rs.getString(42);
	      // need to break apart weaptech into an array.
	  
			rs.close();
			if(!facsimile) { // you must set them yourself if you are.
				au = getAu(); 
				try {
				getAchievements();
				} catch(Exception exc) { exc.printStackTrace(); System.out.println("No idea why this error happened, but player load saved."); } 
			}
			
			ps = new PlayerScript(this);

		} catch(SQLException exc) { exc.printStackTrace(); }

	}

	
	public TradeSchedule findTradeSchedule(UUID trid) {
		int i = 0;
		int j = 0;
		while(j<towns().size()) {
			i = 0;
			
		while(i<towns().get(j).tradeSchedules().size()) {
			if(towns().get(j).tradeSchedules().get(i).id.equals(trid)) return towns().get(j).tradeSchedules().get(i);
			i++;
		}
		j++;
		}
		return null;
	}
	public Raid findRaid(UUID trid) {
		int i = 0;
		int j = 0;
		while(j<towns().size()) {
			i = 0;
			
		while(i<towns().get(j).attackServer().size()) {
			if(towns().get(j).attackServer().get(i).getId().equals(trid)) return towns().get(j).attackServer().get(i);
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
		double schol = (totalScholars*(1+cloudFactor+.05*(getClockworkComputers()))+1);
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
	 public boolean addEventListener(QuestListener q, String type) {
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
	 public boolean dropEventListener(QuestListener q, String type) {
			ArrayList<QuestListener> list =(ArrayList<QuestListener>) eventListenerLists.get(type);
		
		if(list!=null) {
			synchronized(list) {
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
		}
		else return false;
		
	}
	

	public void addMessage(UserMessage m) {
		
		// m = new UserMessage(UUID.fromString(rs.getString(14)),pid_to,rs.getInt(3),userArray,God.getUsername(rs.getInt(3)),rs.getString(4),rs.getString(5),rs.getInt(6), rs.getBoolean(7), rs.getInt(9), UUID.fromString(rs.getString(10)),rs.getString(11),UUID.fromString(rs.getString(13)),rs.getBoolean(8));
			
			if(m.getOriginalSubjectID()==null) {
				getMessages().add(new UserMessagePack());
				//	public UserMessage(int messageID,int pidTo, int pidFrom, String body, String subject, int msgType, boolean readed, int tsid, int originalMessageID, String creationDate) {
				/*
		
				 */
				
				
				getMessages().get(getMessages().size()-1).addMessage(m);
			} else {
				int i = 0; boolean found = false;
				UserMessagePack umpPiece;
				while(i<getMessages().size()) {
					umpPiece = getMessages().get(i);
					int j = 0;
					while(j<umpPiece.getMessages().size()) {
						if(umpPiece.getMessage(j).getSubjectID().equals(m.getOriginalSubjectID())) {
							// so we search all messages in a pack for an original reply identifier!
							
							umpPiece.addMessage(m); found = true; break;
						}
					
						j++;
					}
					if(found) break;
					i++;
				}
				
				if(!found) 	{
					getMessages().add(new UserMessagePack());
					getMessages().get(getMessages().size()-1).addMessage(m);

				}
				System.out.println(getUsername() + " has gone over message limit, deleting...");
				int counter=0;
				while(getMessages().size()>100) {
					getPs().b.markDeletedMessage(getMessages().get(0).getMessages().get(getMessages().get(0).getMessages().size()-1).getId());
					counter++;
				}
				System.out.println(getUsername() + " rid himself of " + counter + " messages.");
			}
			
	}
	 public ArrayList<UserMessagePack> getMessages() {
		 if(currMessages==null) {
		// return all messages.
				UserMessagePack umpPiece; UserMessage m;
				ArrayList<UserMessagePack> ump = new ArrayList<UserMessagePack>();

			try {
		/*		UberPreparedStatement stmt = g.con.createStatement("select count(*) from messages where pid = ?;");
				stmt.setInt(1,p.ID);
				
				ResultSet rs = stmt.executeQuery();
		      	int count=0;
		      	if(rs.next()) count = rs.getInt(1);
		      	rs.close();
		      	stmt.close();
		      	stmt = g.con.createStatement("select message_id from messages where pid = ? order by creation_date desc");
		      	if(count>GodGenerator.maxMessageLimit) {
		      		stmt.setInt(1,p.ID);
		      		rs = stmt.executeQuery();
		      		int counter=0;
		      		ArrayList<Integer> toDel = new ArrayList<Integer>();
		      		while(rs.next()) {
		      			if(counter<GodGenerator.maxMessageLimit)
		      			counter++;
		      			else {
		      				toDel.add(rs.getInt(1));
		      			}
		      		}
		      		rs.close();
		      		stmt.close();
		      		int i = 0;
		      		stmt = g.con.createStatement("delete from messages where message_id = ?;");
		      		while(i<toDel.size()) {
		      			stmt.setInt(1,toDel.get(i));
	    	      		stmt.executeUpdate();
	    	      		i++;
		      		}
		      	}
		      	stmt.close();*/
		      UberPreparedStatement	stmt = con.createStatement("select * from messages where pid = ? order by creation_date");
		      	stmt.setInt(1,ID);
				ResultSet rs = stmt.executeQuery();
				String userArray[];
				while(rs.next()) {
					int pid_to[] = PlayerScript.decodeStringIntoIntArray(rs.getString(2));
					userArray=new String[pid_to.length];
					int i = 0;
					while(i<pid_to.length) {
						userArray[i]=God.getUsername(pid_to[i]);
						i++;
					}
					String origIDStr=(rs.getString(10));
					UUID origID=null, subjID=null;
					if(origIDStr!=null&&!origIDStr.equals("none"))
					 origID = UUID.fromString(origIDStr);
					String subjIDStr = (rs.getString(13));
					if(subjIDStr!=null&&!subjIDStr.equals("none")) subjID = UUID.fromString(subjIDStr);
					
					m = new UserMessage(UUID.fromString(rs.getString(14)),pid_to,rs.getInt(3),userArray,God.getUsername(rs.getInt(3)),rs.getString(4),rs.getString(5),rs.getInt(6), rs.getBoolean(7), rs.getInt(9), origID,rs.getString(11),subjID,rs.getBoolean(8));
					
					if(origID==null) {
						ump.add(new UserMessagePack());
						//	public UserMessage(int messageID,int pidTo, int pidFrom, String body, String subject, int msgType, boolean readed, int tsid, int originalMessageID, String creationDate) {
						/*
				
						 */
						
						
						ump.get(ump.size()-1).addMessage(m);
					} else {
						 i = 0; boolean found = false;
						while(i<ump.size()) {
							umpPiece = ump.get(i);
							int j = 0;
							while(j<umpPiece.getMessages().size()) {
								if(umpPiece.getMessage(j).getSubjectID().equals(m.getOriginalSubjectID())) {
									// so we search all messages in a pack for an original reply identifier!
									
									umpPiece.addMessage(m); found = true; break;
								}
							
								j++;
							}
							if(found) break;
							i++;
						}
						
						if(!found) 	{
							ump.add(new UserMessagePack());
							ump.get(ump.size()-1).addMessage(m);

						}
					}
					
					
				}
				rs.close();
				stmt.close();
				int i = 0; 
				while(i<ump.size()) {
					umpPiece = ump.get(i);
					int j = 0;
					while(j<umpPiece.getMessages().size()) {
						if(umpPiece.getMessages().get(j).getDeleted()) {
							umpPiece.getMessages().remove(j); j--;
						}

						
						j++;
					}
					
					if(umpPiece.getMessages().size()==0) {
						ump.remove(i);
						i--;
					}
					i++;
				}
				
				currMessages=ump;
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		 }
		 
		 return currMessages;
	 }
	 public boolean hasVassaledTowns(int ID) {
		 for(Town t: towns()) {
			 if(t.getLord()!=null&&t.getLord().ID==ID) return true;
		 }
		 return false;
	 }
	
	 public void doVassalTaxes(int num) {
			/*if(resTimer==null) {

				calculateResIncs();
				resTimer = new Timer(checkResInc*1000);
			}
			else if(resTimer.isDone()) {
				calculateResIncs();
				resTimer = new Timer(checkResInc*1000);
			}*/
			Town holdTown; Player curr; Player p; Town t;

				/*
				 * Okay, granted, in the open space version, you just multiply by the fractions
				 * and add to each one. The problem is when you go over the limit.
				 * What you need to do is use the space available in each city
				 * as the sort of fraction. Then, what you do, is you load up
				 * res for each one proportional to the fraction of space AVAILABLE,
				 * and the fact is, if you've got 900 resource space available and 1000
				 * to fill, then doing 1/2 in one who has 450 and one half in the other 450
				 * you're gonna be left with losing resources. If you've got 2000 available
				 * then doing 1/3 * 1000 in each one is fine.
				 * 
				 * 
				 * 
				 */
			
			 int i = 0;
			 int totalOpenSpace[] = new int[5];
			 double newIncs[];
				double resEffects[]; long resCaps[]; double resInc[];
				long res[];
				ArrayList<Town> towns = towns();
				double resBuff[];
				
				while(i<towns.size()) {
				
				
				 int j = 0;
				 holdTown = towns.get(i);
				 resInc=holdTown.getResInc();
				 resCaps=holdTown.getResCaps();
				 res = holdTown.getRes();

						do {
							totalOpenSpace[j]+=(resCaps[j]+Building.baseResourceAmt-res[j]);
							j++;
						} while(j<res.length);
						
		
					i++;
						
					} 
			i=0;
			double tresEffects[];  double tresInc[];
		
			long[] secbuff = getSecondaryResBuff();
			 while(i<towns.size()) {
				
				
				 int j = 0;
				 holdTown = towns.get(i);
				 resInc=holdTown.getResInc();
				 resEffects=holdTown.getResEffects();
				 resCaps=holdTown.getResCaps();
				 resBuff = holdTown.getResBuff();
				 res = holdTown.getRes();
				 ArrayList<Town> ptowns;
				 
					// we add the secondary stuff. It'll get added completely, the fractions add to one.
				 synchronized(resBuff) {
					 double befBuff = resBuff[j];
				do {
				//	System.out.println(resBuff[j] + " before");
					double multiplier = 1;
					if(getMineTimer()>0&&j==0) multiplier*=1.25;
					if(getTimberTimer()>0&&j==1) multiplier*=1.25;
					if(getMmTimer()>0&&j==2) multiplier*=1.25;
					if(getFTimer()>0&&j==3) multiplier*=1.25;

					if(getPremiumTimer()>0) multiplier*=.5;
					
					if(totalOpenSpace[j]!=0)
					resBuff[j] +=multiplier*secbuff[j]*((double)(resCaps[j]+Building.baseResourceAmt-res[j])/(totalOpenSpace[j]));
					//System.out.println(resBuff[j] + " after");

					// add taxrates here
					
					int x = 0;
				
					while(x<God.getPlayers().size()) { //add taxes
						curr = God.getPlayers().get(x);
						if((curr.getLord()!=null&&curr.getLord().ID==ID)||curr.hasVassaledTowns(ID)) {
							int y = 0;
							p = curr;
						//	if(p.owedTicks<3*24*3600/GodGenerator.gameClockFactor) p.update(); // No need to update if a player is this old, just collect!
							// When we try to update players here, we often deadlock them.
								ptowns = p.towns();
								//checkForLeagueReference(p); Lordship is automatically retained the next day
								// if the reference is somehow lost. Leagues don't have this luxury.
							while(y<ptowns.size()) {
								
								t = ptowns.get(y);
								double taxRate=0;
								if(p.getLord()!=null&&p.getLord().ID==ID) {
									// If you are the lord here,
									if(t.getLord()==null||(t.getLord()!=null&&t.getLord().ID==ID)) //and the town has no town-level lord, or you are the 
										// town level lord also, give them the player taxrate.
										taxRate=p.getTaxRate();
									else if(t.getLord()!=null&&t.getLord().ID!=ID) taxRate=0; // if you are the lord here but not over this town, someone else is, you get shit.
								} else if((p.getLord()==null||(p.getLord()!=null&&p.getLord().ID!=ID))&&t.getLord()!=null&&t.getLord().ID==ID) {
									// if this player has no lord, or has one that isn't you, and you are lord of this town, then you
									// get slow growth.
									long diff = (new Timestamp((new Date()).getTime())).getTime()-t.getVassalFrom().getTime();
									double weeks = (int) Math.floor(((double) diff)/604800000);
									double toAdd = weeks*.15;
									if(toAdd>.75) toAdd=.75;
									taxRate=toAdd;
								}
								
								tresInc=t.getResInc();
								 tresEffects=t.getResEffects();
								 newIncs = God.Maelstrom.getResEffects(tresInc,t.getX(),t.getY());
								
	
								 if(totalOpenSpace[j]!=0){
							
						//		if(j==0&&getUsername().equals("EAGLE"))
							//	System.out.println("The internalclock is " + getLeagueInternalClock() + ". The tax rate is " + curr.taxRate + " for user " + curr.player.getUsername() + " from their town of " + t.getTownName() + ". The resInc is " + tresInc[j] + " and I am adding"  +
								//		multiplier*num*newIncs[j]*(tresEffects[j]+1)*curr.taxRate*((double)(resCaps[j]+Building.baseResourceAmt-res[j])/(totalOpenSpace[j]))  + " because the difference is " + 
									//	((double)(resCaps[j]+Building.baseResourceAmt-res[j])) + " and the open space is " + totalOpenSpace[j] + ", num: "+ num+ ", mult: "+  multiplier +", newIncs: " + newIncs[j]);
								
								 resBuff[j]+=multiplier*num*newIncs[j]*(tresEffects[j]+1)*taxRate*((double)(resCaps[j]+Building.baseResourceAmt-res[j])/(totalOpenSpace[j]));
								 }
								 y++;
							}
							
							
							}
						x++;
					}
			
					
				//	if(j==0&&getUsername().equals("EAGLE")) System.out.println("Total resbuff take is: " + (resBuff[j]-befBuff));
					j++;
				} while(j<res.length);
				 }
				j = 0;
				synchronized(res) {
					synchronized(resBuff) {
				while(j<res.length){
				if(resBuff[j]>=1) {
					int toAdd = (int) Math.floor(resBuff[j]);
					res[j]+=toAdd;
					resBuff[j]-=toAdd;
					
					if(res[j]>(resCaps[j]+Building.baseResourceAmt))
						res[j]=resCaps[j]+Building.baseResourceAmt;
					// works even if you lose the building and suddenly have a massive over the limit
					// amount of resources! HAHA :D
					
					}
				j++;
				}
				}
				}
				

				// Then I need to check building and attack servers. Each town has a building server, and an attack server.
			i++;
				
			}
			i = 0;
			
			while(i<secbuff.length) { //reset the resbuff of secondaryhood.
				secbuff[i]=0;
				i++;
			}
			
			setLordInternalClock(getLordInternalClock() + num); // we only iterate after FINISHING THE SAVE!

			
		}
	 public boolean isLord() {
		 for(Player p:God.getPlayers()) {
			 if(p.getLord()!=null&&p.getLord().ID==ID) return true;
			 else {
				 for(Town t: p.towns()) {
					 if(t.getLord()!=null&&t.getLord().ID==ID) return true;
				 }
			 }
		 }
		 return false;
	 }
	 public void territoryCalculator() {
			/*
			 * This guy calculates territories for the map, as a series of hashtables.
			 * Then it stores these hashes by player in God's table, so each player has a ArrayList of Hashtables,
			 * each Hashtable entry representing a territory they own. These player-ArrayLists are then
			 * stored in the territory cache for easy plucking by the world map function.
			 * 
			 * First, territories are calculated for each town as a separate shard. We don't worry about
			 * overlaps yet. To grab the blocks adjacent to blocks you already possess, the farthest of which is at Y-1 distance,
			 * you must have 
			 * 
			 * influence = 10(2.5y^2)
			 * 
			 * 
			 *  Then, at spots where overlap occurs, ownership is calculated by finding the person who has the most influence on that tile,
			 * the influence given as a Sum(townInfluence/town_r^2) for the person, where town_r is the distance
			 * from that point to the town. Players will no longer be placed in areas where their territorial
			 * strength would not beat that of a nearby player. This will have to be precalculated in player creation.
									  {
							
							owner : "SomeGuy"
							start : [6,2]
							sides : [-3, -3, 3, 3]
							}
				
				So corner 1 is 3,2
				 corner 2 is 3,-1
				  corner 3 is 6,-1
				  corner 4 is 6,2


				Finally, territorial overlaps between the same player will be combined.
				
			 
			 */
			ArrayList<ArrayList<Hashtable>> townPointLists = new ArrayList<ArrayList<Hashtable>>(); // holds the points each town possesses before
			// transformation into territory lists.

			for(Town t: towns()) {
				// so first we add influence.
				t.setInfluence(t.getInfluence()+t.getPlayer().getPs().b.getCSL(t.townID));
				
				// now we calculate to see what the max block this guy should have is.
				
				int maxR = (int) Math.round(Math.sqrt(t.getInfluence()/20.5));
				
				/*
				 * Now we need to figure out the "boundaries" of the parcel. What I suggest is starting with the point -maxR,maxR
				 * relative to the town, and scanning down the square, picking up everything that is within a distance maxR of the
				 * town. So we carve a circle out and store the points in like an arraylist.
				 */
				ArrayList<Hashtable> points = new ArrayList<Hashtable>();
				Hashtable pt;
				int startX = t.getX()-maxR;
				while(startX<=t.getX()+maxR) {
					int startY = t.getY()+maxR;
					while(startY>=t.getY()-maxR) {
						double dist = Math.sqrt(Math.pow((t.getX()-startX),2)+Math.pow((t.getY()-startY),2));
						if(dist<=maxR) {
							pt = new Hashtable();
							pt.put("x",startX);
							pt.put("y",startY);
							points.add(pt);
						}
						startY++;
					}
					startX++;
				}
				
				townPointLists.add(points);
				
				
				
			}
			
			// now we combine towns:
			
			 int k = 0;
			while(k<townPointLists.size()) {
				ArrayList<Hashtable> points = (ArrayList<Hashtable>) townPointLists.get(k);
				int i = 0;
				while(i<points.size()) {
					Hashtable point = points.get(i);
					int x = (Integer) point.get("x");
					int y = (Integer) point.get("y");
					int j = k+1; // for each point, we check for a copy of the point in the remaining territories, to see if connection is
					// necessary.
					while(j<townPointLists.size()) {
							ArrayList<Hashtable> otherPoints = (ArrayList<Hashtable>) townPointLists.get(j);
							int z = 0;
							while(z<otherPoints.size()) {
								Hashtable otherPoint = otherPoints.get(z);
								int otherX = (Integer) otherPoint.get("x");
								int otherY = (Integer) otherPoint.get("y");
								if(x==otherX&&y==otherY) {
									
									// WE HAVE A CONNECTION, CONNECT THE TERRITORIES!
									// now we must remember that k need not change, it's
									// at a lower entry than j, but entry j does need to be kicked out
									// of the pool, which means j will need to go j--.
									// it's possible j could be --'ed to k, but then it gets ++'d, so it's okay.
									// also, all x,y in otherPoints must be added that are not duplicates of x,y
									// in points.
									// Once this is done, then we break out of this z-loop,
									// and let the j-loop keep on going. Most likely, all town territories
									// will collapse into one giant one.
									
									int l = 0;
									while(l<otherPoints.size()) {
										int m = 0; boolean found = false;
										otherPoint = otherPoints.get(l);
										 otherX = (Integer) otherPoint.get("x");
										 otherY = (Integer) otherPoint.get("y");
										while(m<points.size()) {
											Hashtable checkPoint = points.get(m);
											int checkX = (Integer) checkPoint.get("x");
											int checkY = (Integer) checkPoint.get("y");
											if(checkX==otherX&&checkY==otherY) {
												found=true;
												break;
											}
											m++;
										}
										
										if(!found) {
											// add the point.
											points.add(otherPoint);
										}
										l++;
									}
									townPointLists.remove(j);
									j--;
									break;
									
								}
								z++;
							}
						
						j++;
					}
					i++;
				}
				k++;
			}
			ArrayList<ArrayList<Hashtable>> ourTerritories = townPointLists; //renaming for different use.

			// now we must check out conflicting territorial boundaries...
			// if one of their points is ALSO our points.
			// if it is, then we get the strength given by every town that has a point there
			// and compare. 
		
				
				Hashtable[] r = (Hashtable[]) getPs().b.getWorldMap().get("territoryArray");
				
				// Presumably, the world map will gather territories that matter and send them down!
				// If we just go straight by player, we will have to analyze each territory, ALL of them.
				// GetWorldMAP has the ability to sort through territories of relevance and return ONLY those.
				
				for(Hashtable h:r) {
					
					Hashtable[] theirPoints = (Hashtable[]) h.get("points"); // get the point format, but this is a copy of the actual storage.
					Hashtable theirTerritory = (Hashtable) h.get("corners");
					int pid = God.getPlayerId((String) theirTerritory.get("owner"));
					Player owner = God.getPlayer(pid);
					Hashtable actualTerritory = null; // so we have h, which is the carbon copy of the WM, and this, the actual,
					// to alter if points overlap.
					boolean actualChanged=false;  // if the actual territory changes, then we must recalculate corners.
					synchronized(owner.territories) { // we may be changing territories, so we need control of it.
					for(Hashtable terr: owner.territories) {
						if(((UUID) terr.get("id")).equals((UUID) h.get("id"))) { 
							actualTerritory=terr;
							break;
						}
					}
				
					for(Hashtable theirPoint: theirPoints) {
						int theirX = (Integer) theirPoint.get("x");
						int theirY = (Integer) theirPoint.get("y");
						 k = 0;
						while(k<ourTerritories.size()) {
							ArrayList<Hashtable> ourPoints = (ArrayList<Hashtable>) ourTerritories.get(k);
							int i = 0;
							
							while(i<ourPoints.size()) {
								Hashtable ourPoint = ourPoints.get(i);
								// so now we compare, nigga. We use a while with ourPoints because we'll be altering it,
								// their points, we're altering the real versions cached, not the copies given by
								// getWorldMaps, so we don't need to worry. The way the world map sends them,
								// each point has only one owner, so once we go past it, we no longer need to worry about it!
							
								int ourX = (Integer) ourPoint.get("x");
								int ourY = (Integer) ourPoint.get("y");
								if(ourX==theirX&&ourY==theirY) {
									// CHECK IT OUT!
									//Sum(townInfluence/town_r^2)
									double myInfluence=0;
									for(Town t: towns()) {
										if((Math.pow(t.getX()-ourX,2)+Math.pow(t.getY()-ourY,2))!=0)
											myInfluence +=t.getInfluence()/(Math.pow(t.getX()-ourX,2)+Math.pow(t.getY()-ourY,2));
										else myInfluence+=t.getInfluence();
									}
									
									
									
									double theirInfluence = 0;
									for(Town t: owner.towns()) {
										if((Math.pow(t.getX()-ourX,2)+Math.pow(t.getY()-ourY,2))!=0)
											theirInfluence +=t.getInfluence()/(Math.pow(t.getX()-ourX,2)+Math.pow(t.getY()-ourY,2));
										else theirInfluence+=t.getInfluence();
									}
									
									if(myInfluence>theirInfluence) {
										// they lose the point.
											// each territory has a "corners" version and a "points" version.
											ArrayList<Hashtable> points = (ArrayList<Hashtable>) actualTerritory.get("points");
											int l = 0;
											while(l<points.size()) {
												Hashtable checkPoint = points.get(l);
												int checkX = (Integer) checkPoint.get("x");
												int checkY = (Integer) checkPoint.get("y");
												if(checkX==ourX&&checkY==ourY) {
													// this shit needs to be removed.
													points.remove(l);
													break;
												}

												l++;
											}
											
											actualChanged=true;
										
									} else {
										// I lose the point.
										ourPoints.remove(i);
										i--;
									}
									
								}
								i++;
							}
							k++;
						}
					}
					if(actualChanged) {
						// what if the territory has been cut in half? How do we detect?
						ArrayList<ArrayList<Hashtable>> separatedPoints = owner.separatePoints((ArrayList<Hashtable>) actualTerritory.get("points"));
						
							owner.territories.remove(actualTerritory);
							for(ArrayList<Hashtable> points:separatedPoints) {
								owner.territories.add(owner.returnTerritory(points));
							}
						
						// so in one fell swoop, we redo the corners.
						
					}
					}
				}
				// now we need to make sure our territories weren't cut in half.
				ArrayList<ArrayList<Hashtable>> newTerritories = new ArrayList<ArrayList<Hashtable>>();
				for(ArrayList<Hashtable> points:ourTerritories) {
					ArrayList<ArrayList<Hashtable>> separatedPoints = separatePoints(points);
					for(ArrayList<Hashtable> newPoints:separatedPoints) {
						newTerritories.add(newPoints);
					}

				}
				
				// now with newTerritories, we can actually add them.
				synchronized(territories) {
					territories = new ArrayList<Hashtable>();
					if(getLord()!=null) {
						
					}
					for(ArrayList<Hashtable> points: newTerritories) {
						
						territories.add(returnTerritory(points));
					}
				}
				
				checkForNewlyVassaledTowns();
				checkLordQualifications();
			}
	 public void checkLordQualifications() {
		 // checks to see if you qualify to become somebody's bitch, or to be passed
		 // along to be somebody else's bitch.
		 //Requirements:Capital City tax rate of 75%
		// 50% of all towns are taxed 75% by the same player.
		 // first we see if you can break free of the current guy. 
		 
		 // then we check if there is anybody who qualifies to be the new guy.
		 
		 int yourPerc=0;// how many of your towns that YOU own.
		
		 ArrayList<Player> possibleLords = new ArrayList<Player>();
		 ArrayList<Integer> possibleLordTownsOwned = new ArrayList<Integer>();
		 for(Town t: towns()) {
				 if(t.getLord()!=null) {
					
					 long diff = (new Timestamp((new Date()).getTime())).getTime()-t.getVassalFrom().getTime();
						double weeks = (int) Math.floor(((double) diff)/604800000);
						taxRate+=weeks*.15;
						if(taxRate>=.75) {
							int i = 0; boolean found = false;
							while(i<possibleLords.size()) {
								if(possibleLords.get(i).ID==t.getLord().ID) {
									possibleLordTownsOwned.set(i,possibleLordTownsOwned.get(i)+1);
									found=true;
									break;
								}
								i++;
							}
							if(!found) {
								// need to add new lord.
								possibleLords.add(t.getLord());
								possibleLordTownsOwned.add(1);
							}
						}
						
				 } else {
					  yourPerc++;
				 }
			 
			 
		 }
		 
		 if((int) Math.round(yourPerc/towns().size())>=.5&&getLord()!=null&&!isVoluntaryVassal()) {
			 getLord().makeWallPost("","Vassal Lost!",getUsername()+"'s empire has freed itself from my grasp!",
						"http://www.steampunkwars.com","In Steampunk Wars, vassalage is just one of many ways to subjugate your neighbors. Join now to find out more!",
						"https://fbcdn-photos-a.akamaihd.net/photos-ak-snc1/v27562/23/164327976933647/app_1_164327976933647_5894.gif",
						"Play Now!","http://www.steampunkwars.com/");

				makeWallPost("","Freedom!","After a long period of vassalage under "+getLord().getUsername()+", my people are now free once more!",
						"http://www.steampunkwars.com","In Steampunk Wars, vassalage is just one of many ways to subjugate your neighbors. Join now to find out more!",
						"https://fbcdn-photos-a.akamaihd.net/photos-ak-snc1/v27562/23/164327976933647/app_1_164327976933647_5894.gif",
						"Play Now!","http://www.steampunkwars.com/");
				
				 makeVassalOf(null,false);

		 } else {
			 // lets see if the other lords have the ability to claim you, then. Whichever has the right, gets lordship.
			  int i = 0;
			 while(i<possibleLordTownsOwned.size()) {
				 if(((double) possibleLordTownsOwned.get(i))/(((double) towns().size()))>=.5) {
					 // this is the guy who gets it.
					 if(getLord()!=null&&getLord().ID!=possibleLords.get(i).ID) {
						 //  this is the case where one lord gives it to another
						 getLord().makeWallPost("","Vassal Lost!",getUsername()+"'s empire has freed itself from my grasp!",
									"http://www.steampunkwars.com","In Steampunk Wars, vassalage is just one of many ways to subjugate your neighbors. Join now to find out more!",
									"https://fbcdn-photos-a.akamaihd.net/photos-ak-snc1/v27562/23/164327976933647/app_1_164327976933647_5894.gif",
									"Play Now!","http://www.steampunkwars.com/");
						
							
							
						
					 } 
					 possibleLords.get(i).makeWallPost("","Vassal Acquired!",getUsername()+"'s entire empire has fallen under my sway and has sworn fealty to me and me alone!",
								"http://www.steampunkwars.com","In Steampunk Wars, vassalage is just one of many ways to subjugate your neighbors. Join now to find out more!",
								"https://fbcdn-photos-a.akamaihd.net/photos-ak-snc1/v27562/23/164327976933647/app_1_164327976933647_5894.gif",
								"Play Now!","http://www.steampunkwars.com/");
					 makeWallPost("","Subjugation!","My empire has fallen under the sway of "+possibleLords.get(i).getUsername()+".  I'm now forced to be their vassal.",
								"http://www.steampunkwars.com","In Steampunk Wars, vassalage is just one of many ways to subjugate your neighbors. Join now to find out more!",
								"https://fbcdn-photos-a.akamaihd.net/photos-ak-snc1/v27562/23/164327976933647/app_1_164327976933647_5894.gif",
								"Play Now!","http://www.steampunkwars.com/");
					 makeVassalOf(possibleLords.get(i),false);
					 
						
				 }
				 i++;
			 }
			 
		 }
		 
		 
		 
	 }
	public void makeVassalOf(Player lord, boolean voluntary) {
		setLord(lord);
		 setVoluntaryVassal(voluntary);
		 for(Hashtable terr:territories) {
			 if(lord==null)
				 ((Hashtable) terr.get("corners")).put("lord","none");
			 else
			 ((Hashtable) terr.get("corners")).put("lord",lord.getUsername());
		 }
		 save();
	}
	 public void checkForNewlyVassaledTowns() {
		 /*
		  * This function checks to see if any of your towns are not in your territories. If they are not in your territories
		  * and are not yet "vassaled" to someone else with a counter set, then they get that shit.
		  */
		 for(Town t: towns()) {
			 boolean found=false;
			 for(Hashtable terr:territories) {
				 for(Hashtable point:(ArrayList<Hashtable>) terr.get("points")) {
					 int x = (Integer) point.get("x");
					 int y = (Integer) point.get("y");
					 if(x==t.getX()&&y==t.getY()) {
						 found=true;
						 break;
					 }
				 }
			 }
			 if(!found) {
				 
				 // shit, this town was taken, but was it already taken?
				 // if it was already taken by someone, then this code doesn't care - it'll reset
				 // that shit.
					Hashtable[] r = (Hashtable[]) getPs().b.getWorldMap().get("territoryArray");
					
					// Presumably, the world map will gather territories that matter and send them down!
					// If we just go straight by player, we will have to analyze each territory, ALL of them.
					// GetWorldMAP has the ability to sort through territories of relevance and return ONLY those.
					
					for(Hashtable h:r) {
						
						Hashtable[] theirPoints = (Hashtable[]) h.get("points"); // get the point format, but this is a copy of the actual storage.
						Hashtable theirTerritory = (Hashtable) h.get("corners");
						int pid = God.getPlayerId((String) theirTerritory.get("owner"));
						Player owner = God.getPlayer(pid);
						
						for(Hashtable theirPoint:theirPoints) {
							
							int theirX = (Integer) theirPoint.get("x");
							int theirY = (Integer) theirPoint.get("y");
							
							if(theirX==t.getX()&&theirY==t.getY()) {
								
								// now we have a winner.
								
								

								owner.makeWallPost("","Territory Conquered!",getUsername()+"'s town has just fallen under my influence in Steampunk Wars!",
										"http://www.steampunkwars.com","Territory grows daily in Steampunk wars.  Join now and watch your empire grow!",
										"https://fbcdn-photos-a.akamaihd.net/photos-ak-snc1/v27562/23/164327976933647/app_1_164327976933647_5894.gif",
										"Play Now!","http://www.steampunkwars.com/");

								makeWallPost("","Territory Conquered!","My town " + t.getTownName() + " has just fallen under " + owner.getUsername() + "'s influence in Steampunk Wars!",
										"http://www.steampunkwars.com","Territory grows daily in Steampunk wars.  Join now and watch your empire grow!",
										"https://fbcdn-photos-a.akamaihd.net/photos-ak-snc1/v27562/23/164327976933647/app_1_164327976933647_5894.gif",
										"Play Now!","http://www.steampunkwars.com/");
								// presumably this is also called if they just got online, so it's possible
								// that they already have a timer set up.
								
									t.setLord(owner);
									t.setVassalFrom(new Timestamp((new Date()).getTime()));
							}

						}
						
						
						
					}
			 } else if(found&&t.getLord()!=null) {
				 // so now it's under it's own power again, but there is still a lord - RESET!
				
					 t.setLord(null);
			 }
		 }
	 }
	 public int makeWallPost(String message,String name, String caption, String link, String description, String picture, String bottomlinkname, String bottomlink) {
		 if(getFuid()!=0) {
			 /*
			  * 	
	$attachment = array('message' => urldecode($_POST['message']),
	'name' => urldecode($_POST['name']),
	'caption' => urldecode($_POST['caption']),
	'link' => urldecode($_POST['link']),
	'description' => urldecode($_POST['description']),
	'picture' => urldecode($_POST['picture']),
	'actions' => array(array('name' => urldecode($_POST['bottomlinkname']),
	'link' => urldecode($_POST['bottomlink'])))
	);
			  */
			 
			 HttpClient httpClient = new HttpClient();
				PostMethod method = new PostMethod("http://127.0.0.1/backendpost/wallpost.php");
				method.addParameter("message", message);
				method.addParameter("name", name);
				method.addParameter("caption", caption);
				method.addParameter("link", link);
				method.addParameter("description", description);
				method.addParameter("picture", picture);
				method.addParameter("bottomlinkname", bottomlinkname);
				method.addParameter("bottomlink", bottomlink);
				method.addParameter("fuid",""+getFuid());

				try {
					int statusCode = httpClient.executeMethod( method );
					return statusCode;
				//	System.out.println(statusCode);
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return -1;
			}
		 return -1;
			 
		 }
	 
	 public ArrayList<ArrayList<Hashtable>> separatePoints(ArrayList<Hashtable> points) {
		 // if a set of points has points a,b and c,d such that a,b cannot be connected
		 // directly to point c,d, then we separate the collections of points connected to a,b and c,d and return them
		 // as separate arraylists.
		 return null;
	 }
	 public Hashtable returnTerritory(ArrayList<Hashtable> points) {
		 
		 /*
		  * This method takes an assemblage of points and returns a polygon out of it.
		  * It does this:
		  * 
		  *  {
							
							owner : "SomeGuy"
							start : [6,2]
							sides : [-3, -3, 3, 3]
							}
				
				So corner 1 is 3,2
				 corner 2 is 3,-1
				  corner 3 is 6,-1
				  corner 4 is 6,2

			It does this by finding the farthest point in the +x,+y direction as a starting "corner", which is
			just any point that has the highest x and highest y.

		  */
		 
		 ArrayList<Hashtable> borders = giftWrapping(points);
		 
		 // so now i must take this code and turn it into Markus' corner code. F-THAT-SHIT
		 
		 int i = 0;
		 while(i<borders.size()) { // sort border points by distance.
			 Hashtable p1 = borders.get(i);
			 int x1 = (Integer) p1.get("x");
			 int y1 = (Integer) p1.get("y");

			 int j = i+1;
			 int minEntry=i+1;
			 double minDist=99999;
			 while(j<borders.size()) {
				 Hashtable p2 = borders.get(j);
				 int x2 = (Integer) p2.get("x");
				 int y2 = (Integer) p2.get("y");
				 double dist = Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
				 if(dist<minDist) {
					 minDist=dist;
					 minEntry=j;
				 }
				 j++;
			 }
			 
			 Hashtable swapPoint = borders.get(i+1);
			 Hashtable toPutInThere = borders.get(minEntry);
			 
			 borders.set(i+1,toPutInThere);
			 borders.set(minEntry,swapPoint); // should eventually swap them all correctly.
			 
			 
			 i++;
		 }
		 ArrayList<Integer> xdiffs = new ArrayList<Integer>();
		 ArrayList<Integer> ydiffs = new ArrayList<Integer>();
		  i = 1;
		 while(i<borders.size()) {
			 Hashtable p1 = borders.get(i-1);
			 Hashtable p2 = borders.get(i);
			 int x1 = (Integer) p1.get("x");
			 int y1 = (Integer) p1.get("y");
			 int x2 = (Integer) p2.get("x");
			 int y2 = (Integer) p2.get("y");
			 xdiffs.add(x2-x1);
			 ydiffs.add(y2-y1);
			 i++;
		 }
		 
		 i = 0;
		 while(i<xdiffs.size()) {
			 if(xdiffs.get(i)==0||ydiffs.get(i)==0) {
				 // if it equals zero, this means we're either going in the straight y dir, or the straight x dir,
				 // so we may as well compound here.
				if(i!=0) {
					// we need to take this entry and add it to the previous one, then move everything down.
					xdiffs.set(i-1,xdiffs.get(i-1)+xdiffs.get(i));
					xdiffs.remove(i);
					ydiffs.set(i-1,ydiffs.get(i-1)+ydiffs.get(i));
					ydiffs.remove(i);
					i--;
				} else {
					// in the event this is the FIRST entry, then we must just move everything down, and add the first entry
					// to the second.
					xdiffs.set(1,xdiffs.get(1)+xdiffs.get(0));
					xdiffs.remove(0);
					ydiffs.set(1,ydiffs.get(1)+ydiffs.get(0));
					ydiffs.remove(0);
					i--;
				}
			 }
			 i++;
		 }
		 
		 int sides[] = new int[xdiffs.size()*2];
		 i = 0; int k =0;
		 while(i<xdiffs.size()) {
			 sides[k]=xdiffs.get(i);
			 k++;
			 sides[k]=ydiffs.get(i);
			 k++;
			 i++;
		 }
		 UUID id = UUID.randomUUID();
		 
		 Hashtable newTerr = new Hashtable();
		 newTerr.put("id",id);
		 Hashtable corners = new Hashtable();
		 corners.put("owner",getUsername());
		 if(getLord()==null) {
			 corners.put("lord","none");
		 }
		 else {
			 corners.put("lord",getLord().getUsername());
		 }
		 int corner[] = new int[2];
		 corner[0] =(Integer)  borders.get(0).get("x");
		 corner[1] =(Integer)  borders.get(0).get("y");

		 corners.put("corner",corner);
		 corners.put("sides",sides);
		 newTerr.put("corners",corners);

		 newTerr.put("points",points);
		 /*
		 for(Hashtable p:borders) {
			 System.out.println("x: "+( (Integer) p.get("x")) + " y: "+( (Integer) p.get("y")));
		 }
		 */
		 return newTerr;
	 }
	 private ArrayList<Hashtable> giftWrapping(ArrayList<Hashtable> points)
	 	{
				// random
				 
				 int xPoints[] = new int[points.size()];
				 int yPoints[] = new int[points.size()];
				 ArrayList<Hashtable> toRet = new ArrayList<Hashtable>();
					Hashtable toAdd;
				 int c = 0;
				 for(Hashtable r: points) {
					 xPoints[c] = (Integer) r.get("x");
					 yPoints[c] = (Integer) r.get("y");
					 c++;
				 }
				
				// convex hull
				int min = 0;
				for ( int i = 1; i < points.size(); i++ ) {
				    if ( yPoints[i] == yPoints[min] ) {
					if ( xPoints[i] < xPoints[min] )
					    min = i;
				    }
				    else if ( yPoints[i] < yPoints[min] )
					min = i;
				}
				//System.out.println("min: " + min);
		
				int	num = 0;
				int smallest;
				int current = min;
				do {
				 //   xPoints2[num] = xPoints[current];
				  //  yPoints2[num] = yPoints[current];
				    toAdd = new Hashtable();
				    toAdd.put("x",xPoints[current]);
				    toAdd.put("y",yPoints[current]);
				    toRet.add(toAdd);
				    num++;
				    //System.out.println("num: " + num + ", current: " + current + "(" + xPoints[current] + ", " + yPoints[current] + ")");
				    smallest = 0;
				    if ( smallest == current )
					smallest = 1;
				    for ( int i = 0; i < points.size(); i++ ) {
					if ( ( current == i ) || ( smallest == i ) )
					    continue;
					if ( small(current, smallest, i,xPoints,yPoints))
					    smallest = i;
				    }
				    current = smallest;
				} while ( current != min );
				
				c = 0;
		
				return toRet;
				
		 }

	    private boolean small(int current, int smallest, int i, int[] xPoints, int[] yPoints)
	    {int xa, ya, xb, yb, val;
		xa = xPoints[smallest] - xPoints[current];
		xb = xPoints[i] - xPoints[current];
		ya = yPoints[smallest] - yPoints[current];
		yb = yPoints[i] - yPoints[current];
		
		val = xa * yb - xb * ya;
		if ( val > 0 )
		    return true;
		else if ( val < 0 )
		    return false;
		else {
		    if ( xa * xb + ya * yb < 0 )
			return false;
		    else {
			if ( xa * xa + ya * ya > xb * xb + yb * yb )
			    return true;
			else
			    return false;
		    }
		}
}
	public ArrayList<UserSR> getUserSR() {
	  	  if(currSRs==null) {
	      try{
	     
	    	  currSRs = new ArrayList<UserSR>();
	      	UberPreparedStatement stmt = con.createStatement("select * from statreports where pid = ? and deleted = false order by sid asc;");
	      	stmt.setInt(1,ID);
	    	ResultSet rs = stmt.executeQuery(); // normal statreports.
	    		// don't question the asc, you'd think it'd be desc but asc works! Desc doesn't!
	    		// probably because I insert elements at the bottom...
	    		while(rs.next())  {
	    			
	    		UUID currSID = UUID.fromString(rs.getString(1)); // search for foreign tid reports, then get their sids,
	    		// see if we have them, and so on...
	    		int defID = rs.getInt(3); int offID = rs.getInt(2);
	    
	    		Town t = God.findTown(defID); String townOff = "DATA CORRUPT-ID", townDef = "DATA CORRUPT-ID";
	    		if(t!=null&&t.townID!=0) townDef  = t.getTownName();
	    		t = God.findTown(offID);
	    		if(t!=null&&t.townID!=0) townOff  = t.getTownName();


		    	boolean defender = rs.getBoolean(17);
		    	// finding out if the supporting guy got defensive or offensive is problematic.
		    	// No guarantee it exists in a table when he reads it, so really it needs to be preserved in
		    	// an off/def boolean.
	    		String bombResultBldg = rs.getString(13);
	    		String bombArray[] = UserSR.getStringArrayFromPluses(bombResultBldg);
	    		int i = 0;
	    		String[] bname  = new String[bombArray.length]; 

	    		while(i<bombArray.length) {
	    		if(!bombArray[i].equals("null")&&!bombArray[i].equals("vic")&&!bombArray[i].equals("nobldg")) {

	    			bname[i] = bombArray[i].substring(bombArray[i].lastIndexOf(".")+1,bombArray[i].length());
	    			bombArray[i] = bombArray[i].substring(0,bombArray[i].lastIndexOf("."));
	    		
	    
	    		// so it finds the building name of the building in the slot destroyed/leveled by the bomber 
	    		}
	    			i++;
	    		}
	    		// find the name of the building.
	 //   		ResultSet bldgType = stmt3.executeQuery("select tid ")
//public StatusReport(int sid,String offst, String offfi,String defst, String deffi,String offNames,String defNames, String townOff, String townDef, boolean genocide, boolean read, String bombResultBldg, String bombResultPpl, String btype, boolean defender) {
	    		try {
	    		boolean support = rs.getBoolean(15);
	    		if(!support)
	    		currSRs.add(new UserSR(currSID,rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),
	    				rs.getString(9),rs.getString(38),rs.getString(39),rs.getBoolean(10),rs.getBoolean(11),rs.getBoolean(51),defender,rs.getInt(18),rs.getInt(19),
	    				rs.getInt(20),rs.getInt(21),rs.getInt(22),rs.getBoolean(23),rs.getBoolean(24),rs.getInt(25),rs.getBoolean(26),rs.getString(28),rs.getString(29),rs.getString(30),rs.getInt(31),rs.getBoolean(32),rs.getBoolean(33),rs.getInt(34),rs.getInt(35),rs.getInt(36),rs.getInt(37),rs.getString(40),
	    				rs.getInt(41),rs.getInt(42),rs.getInt(43),rs.getInt(44),rs.getBoolean(45),rs.getBoolean(46),rs.getBoolean(47),rs.getBoolean(48),rs.getBoolean(49),rs.getString(50)));
	    		else { 
	    			bname = new String[1]; bname[0]= "null";
	    			UserSR SR = new UserSR(currSID,rs.getString(4),rs.getString(5),"","",rs.getString(8),"",rs.getString(38),rs.getString(39),
		    				false,rs.getBoolean(11),rs.getBoolean(51),defender,rs.getInt(18),rs.getInt(19),
		    				rs.getInt(20),rs.getInt(21),rs.getInt(22),rs.getBoolean(23),rs.getBoolean(24),rs.getInt(25),rs.getBoolean(26),rs.getString(28),rs.getString(29),rs.getString(30),rs.getInt(31),rs.getBoolean(32),rs.getBoolean(33),rs.getInt(34),rs.getInt(35),rs.getInt(36),rs.getInt(37),rs.getString(40)
		    				,rs.getInt(41),rs.getInt(42),rs.getInt(43),rs.getInt(44),rs.getBoolean(45),rs.getBoolean(46),rs.getBoolean(47),rs.getBoolean(48),rs.getBoolean(49),rs.getString(50));
	    			currSRs.add(SR);
	    			SR.support=true;
	    			
	    		}
	    		} catch(Exception exc) { exc.printStackTrace(); }

	    	
	    	}
			rs.close();
			stmt.close();
	    	
	 
	  } catch(SQLException exc) { exc.printStackTrace(); }
	  
	  	  }
	  	  
	  	  return currSRs;

	
	}
	public void addUserSR(UserSR sr) {
		
		if(getUserSR().size()>100) {
			System.out.println(getUsername() + " has gone over SR limit, deleting...");
			int counter=0;
			while(getUserSR().size()>100) {
				getPs().b.deleteUserSR(getUserSR().get(0).id);
				counter++;
			}
			System.out.println(getUsername() + " rid himself of " + counter + " SRs.");
			
		}
	}
	public boolean deleteUserSR(UUID id) {
		try {
			UberPreparedStatement stmt = con.createStatement("update statreports set deleted=true where id = ?;");
			stmt.setString(1,id.toString());
			stmt.execute();
			stmt.close();
			
			int i = 0;
			while(i<getUserSR().size()) {
				if(getUserSR().get(i).id.equals(id)) {
					getUserSR().remove(i);
					return true;
				}
				i++;
			}
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
		
		return false;
	}
	public UserSR getUserSR(UUID id) {
		
		for(UserSR s:getUserSR()) {
			if(s.id.equals(id)) return s;
		}
		return null;
	}
	 public UserMessage getMessage(UUID id) {
		 
		 for(UserMessagePack ump:getMessages()) {
			 for(UserMessage m:ump.getMessages()) {
				 
				 if(m.getId().equals(id)) {
					 return m;
				 }
			 }
		 }
		 
		 return null;
	 }
	 
	public void saveAndIterate(int number) {
	
		UberStatement stmt=null;
		ResultSet checkChange;
		
		   
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
			UberPreparedStatement stmt = con.createStatement("select complete from qpc where qid = ? and pid = ?;");
			stmt.setInt(1,qid);
			stmt.setInt(2,ID);
			ResultSet rs = stmt.executeQuery();
		
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
			UberPreparedStatement stmt = con.createStatement("select qid from Quest where classname = ?;");
			stmt.setString(1,classname);
			ResultSet rs = stmt.executeQuery();
			ResultSet check; UberPreparedStatement stmt2 = con.createStatement("select complete from qpc where qid = ? and pid = ?;");
			rs.next();
			stmt2.setInt(1,rs.getInt(1));
			stmt2.setInt(2,ID);
			 check = stmt2.executeQuery();
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

		Town t=null;
		UberPreparedStatement stmt = con.createStatement("select * from player where pid = ?;");
		stmt.setInt(1,ID);
		ResultSet rs = stmt.executeQuery();
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
	       voluntaryVassal=rs.getBoolean(89);
	       secondaryResBuff=null; // let it get reset!
	       try {
		       last_session=rs.getTimestamp(83);
		      } catch(Exception exc) { last_session = new Timestamp((new Date()).getTime());}	       numLogins = rs.getInt(84);
	       totalTimePlayed = rs.getLong(85);
	       
	       setPassword(rs.getString(26));
	       bodyArmor=(rs.getInt(3));
	      // setKnowledge(rs.getInt(4));
	       setTotalScholars(rs.getInt(5));
		     setScoutTech(rs.getInt(40));
		       airshipTech = rs.getBoolean(61);
		      // missileSiloTech = rs.getBoolean(62);
		      // recyclingTech = rs.getBoolean(63);
		       clockworkAugments = rs.getBoolean(64);
		       advancedFortifications = rs.getBoolean(65);
		      bloodMetalArmor = rs.getBoolean(66);
		      // foodRefTech = rs.getBoolean(67);
		       attackAPI = rs.getBoolean(68);
		       advancedAttackAPI = rs.getBoolean(69);
		       tradingAPI = rs.getBoolean(70);
		       advancedTradingAPI = rs.getBoolean(71);
		       smAPI = rs.getBoolean(72);
		       taxRate = rs.getDouble(88);
		       researchAPI = rs.getBoolean(73);
		       buildingAPI = rs.getBoolean(74);
		       advancedBuildingAPI = rs.getBoolean(75);
		       messagingAPI = rs.getBoolean(76);
		       zeppelinAPI = rs.getBoolean(77);
		       completeAnalyticAPI = rs.getBoolean(78);
		       nukeAPI = rs.getBoolean(79);
		       worldMapAPI = rs.getBoolean(80);

		     setRevTimer(rs.getInt(54));
	       structuralIntegrity = (rs.getInt(30));
	      // setALotTech(rs.getInt(8));
	       setTotalBPEarned(rs.getInt(55));
	       knowledge = rs.getInt(4);

	       setSupportstaff(rs.getBoolean(28));
	   //    setSoldierTech(rs.getBoolean(9));
	       personalShields=(rs.getBoolean(10));
	       bloodMetalPlating=(rs.getInt(38));
	       hydraulicAssistors=(rs.getBoolean(11));
	       constructionResearch=(rs.getInt(13));
	       firearmResearch=(rs.getInt(17));
	       thrustVectoring=(rs.getBoolean(18));
	      // setSupportTech(rs.getInt(19));
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
	       //advancedFortifications=(rs.getInt(21));
		       setScholTicks(rs.getInt(31));
		      ordinanceResearch=(rs.getInt(32));
		       teslaTech=(rs.getInt(33));
		       infrastructureTech=(rs.getInt(34));
		       architecture=(rs.getInt(35));
		       clockworkComputers=(rs.getInt(36));
		       playedTicks=rs.getInt(45);
	 //      setTradeTech(rs.getInt(27));
	        setCapitaltid(rs.getInt(39));
	      // need to break apart weaptech into an array.
	    

			// now for each player we must retrieve au data...
			au = null;
			getAu();
			
			// time to get town stuff.
			UberPreparedStatement tus = con.createStatement("select * from town where pid = ?;");
			tus.setInt(1,ID);
			 ResultSet trs = tus.executeQuery();
			

			// need to establish all.
			
			while(trs.next()) {
				
			// There we go, we've got everything we need now.
			//	public Town(long res[], ArrayList<AttackUnit> au, int townID, String townName, int x, int y) {
		int	i=0;
			boolean found = false;
			while(i<towns().size()) {
				if(towns().get(i).townID==trs.getInt(1)) {
					t=	towns().get(i);
					t.synchronize();
					found=true;
					break;
				}
				i++;
			}
			if(!found) towns().add(God.findTown(trs.getInt(1)));
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
			totalpopulation+=God.getTotalSize(au.get(i),this)*au.get(i).getExpmod();
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
				if(b.getType().equals("Command Center")) totaleng+=b.getPeopleInside();
				else if(b.getType().equals("Trade Center")) totaltrad+=b.getPeopleInside();
				else if(b.getType().equals("Institute")) totalschol+=b.getPeopleInside();
				else if(b.getType().equals("Communications Center")) totalmess+=b.getPeopleInside();

				j++;
			}
			j=0;
			au = t.getAu();
			while(j<au.size()) {
				a = au.get(j);
				if(a.getName().equals("Engineer")) totaleng+=a.getSize();
				if(a.getName().equals("Trader")) totaltrad+=a.getSize();
				if(a.getName().equals("Scholar")) totalschol+=a.getSize();
				if(a.getName().equals("Messenger")) totalmess+=a.getSize();


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
		      UberPreparedStatement stmt= con.createStatement("select chg,last_login from player where pid = ?;");
		      stmt.setInt(1,ID);
		      ResultSet rs = stmt.executeQuery();
		      rs.next();
		      //	       last_login=rs.getTimestamp(41);

		      if(rs.getInt(1)==0&&(rs.getTimestamp(2).getTime()!=last_login.getTime()||owedTicks==0||(owedTicks>0&&(God.gameClock-owedTicks)<God.saveWhenTicks))) {
		    	//  System.out.println("Actually saving " + getUsername() + " and his owedTicks was " + owedTicks + " and gameClock diff is " + (God.gameClock-owedTicks) + " compared to " + God.saveWhenTicks + " and his last_login saved was " + rs.getTimestamp(2) + " and actual was " +last_login);
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
		    	  stmt.close();
			       stmt= con.createStatement("update player set bodyArmor = ?, personalShields = ?, hydraulicAssistors = ?, thrustVectoring = ?, bloodMetalPlating = ?, bloodMetalArmor = ?, advancedFortifications = ?, constructionResearch = ?, firearmResearch = ?"  +
			    		   ", townTech = ?, supportstaff = ?, infrastructureTech = ?, structuralIntegrity = ?" + 
			    		   ", scholTicks = ?, playedTicks = ?, bp = ?, premiumTimer = ?, ubTimer = ?, mineTimer = ?, feroTimer = ?" +
			    		   ", timberTimer = ?, mmTimer = ?, fTimer = ?, knowledge = ?, architecture = ?, clockworkComputers = ?, capitaltid = ?" 
			    		   +", revTimer = ?, totalBPEarned = ?, flicker = ?, fuid = ?, totalTimePlayed = ?, numLogins = ?, last_login = ?, last_session = ?" 
			    		   	+", last_auto_blast = ?,tPushes = ?, airshipTech = ?, clockworkAugments = ?" 
			    		   	+", attackAPI = ?, advancedAttackAPI = ?, digAPI = ?, tradingAPI = ?, advancedTradingAPI = ?, smAPI = ?, researchAPI = ?"+ 
			    		   	", buildingAPI = ?, advancedBuildingAPI = ?, messagingAPI = ?, zeppelinAPI = ?, completeAnalyticAPI = ?, version = ?, nukeAPI = ?, worldMapAPI = ?, owedTicks = ?, email =?, pushLog = ?, password = ?, lord = ?, taxRate = ?, voluntaryVassal=?, mbuff=?, tbuff = ?, mmbuff=?, fbuff=? where pid = ?;");
			       stmt.setInt(1,bodyArmor);
			       stmt.setBoolean(2,personalShields);
			       stmt.setBoolean(3,hydraulicAssistors);
			       stmt.setBoolean(4,thrustVectoring);
			       stmt.setInt(5,bloodMetalPlating);
			       stmt.setBoolean(6,bloodMetalArmor);
			       stmt.setBoolean(7,advancedFortifications);
			       stmt.setInt(8,constructionResearch);
			       stmt.setInt(9,firearmResearch);
			       stmt.setInt(10,townTech);
			       stmt.setBoolean(11,supportstaff);
			       stmt.setInt(12,infrastructureTech);
			       stmt.setInt(13,structuralIntegrity);
			       stmt.setInt(14,scholTicks);
			       stmt.setInt(15,playedTicks);
			       stmt.setInt(16,bp);
			       stmt.setInt(17,premiumTimer);
			       stmt.setInt(18,ubTimer);
			       stmt.setInt(19,mineTimer);
			       stmt.setInt(20,feroTimer);
			       stmt.setInt(21,timberTimer);
			       stmt.setInt(22,mmTimer);
			       stmt.setInt(23,fTimer);
			       stmt.setInt(24,knowledge);
			       stmt.setInt(25,architecture);
			       stmt.setInt(26,clockworkComputers);
			       stmt.setInt(27,capitaltid);
			       stmt.setInt(28,revTimer);
			       stmt.setInt(29,totalBPEarned);
			       stmt.setString(30,flicker);
			       stmt.setLong(31,fuid);
			       stmt.setLong(32,totalTimePlayed);
			       stmt.setInt(33,numLogins);
			       stmt.setString(34,last_login.toString());
			       stmt.setString(35,last_session.toString());
			       stmt.setInt(36,last_auto_blast);
			      stmt.setInt(37,tPushes);
			       stmt.setBoolean(38,airshipTech);
			       stmt.setBoolean(39,clockworkAugments);
			       stmt.setBoolean(40,attackAPI);
			       stmt.setBoolean(41,advancedAttackAPI);
			       stmt.setBoolean(42,digAPI);
			       stmt.setBoolean(43,tradingAPI);
			       stmt.setBoolean(44,advancedTradingAPI);
			       stmt.setBoolean(45,smAPI);
			       stmt.setBoolean(46,researchAPI);
			       stmt.setBoolean(47,buildingAPI);
			       stmt.setBoolean(48,advancedBuildingAPI);
			       stmt.setBoolean(49,messagingAPI);
			       stmt.setBoolean(50,zeppelinAPI);
			       stmt.setBoolean(51,completeAnalyticAPI);
			       stmt.setString(52,version);
			       stmt.setBoolean(53,nukeAPI);
			       stmt.setBoolean(54,worldMapAPI);
			       stmt.setInt(55,owedTicks);
			       stmt.setString(56,email);
			       stmt.setString(57,pushLog);
			       stmt.setString(58,password);
			       if(getLord()!=null)
			    	   stmt.setInt(59,lord.ID);
			       else
			    	   stmt.setInt(59,0);
			       stmt.setDouble(60,taxRate);
			       stmt.setBoolean(61,voluntaryVassal);
			       if(secondaryResBuff==null) {
			    	   stmt.setLong(62,0);
			    	   stmt.setLong(63,0);
			    	   stmt.setLong(64,0);
			    	   stmt.setLong(65,0);

			       } else {
			    	   stmt.setLong(62,secondaryResBuff[0]);
			    	   stmt.setLong(63,secondaryResBuff[1]);
			    	   stmt.setLong(64,secondaryResBuff[2]);
			    	   stmt.setLong(65,secondaryResBuff[3]);
			       }
			       stmt.setInt(66,ID);
	  /*   String  updatePlayer = "update player set bodyArmor = " + bodyArmor +", personalShields = " + personalShields + 
	    		  ", hydraulicAssistors = " + hydraulicAssistors +  ", thrustVectoring = " + thrustVectoring + ", bloodMetalPlating = " + bloodMetalPlating +", bloodMetalArmor = " + bloodMetalArmor+ ", advancedFortifications = " + advancedFortifications + ", constructionResearch = " + constructionResearch + ", firearmResearch = " + firearmResearch +
	    		   ", townTech = " + townTech 
	    		   +", supportstaff = " + supportstaff +", infrastructureTech = " + infrastructureTech + ", structuralIntegrity = " + structuralIntegrity + 
	    		   ", scholTicks = " + scholTicks + ", playedTicks = " + playedTicks +", bp = " + bp + ", premiumTimer = " + premiumTimer+ ", ubTimer = " + ubTimer + ", mineTimer = " + mineTimer + ", feroTimer = " + feroTimer +
	    		   ", timberTimer = "  + timberTimer + ", mmTimer = " + mmTimer + ", fTimer = " + fTimer + ", knowledge = " + knowledge +", architecture = " + architecture + ", clockworkComputers = " + clockworkComputers + ", capitaltid = " + capitaltid 
	    		   +", revTimer = " + revTimer +", totalBPEarned = " + totalBPEarned +", flicker = '" + flicker + "', fuid = " + fuid + ", totalTimePlayed = " + totalTimePlayed + ", numLogins = " + numLogins + ", last_login = '" + (last_login).toString() + "', last_session = '" + (last_session).toString() 
	    		   	+"', last_auto_blast =" + last_auto_blast + ",tPushes = "+tPushes+", airshipTech = " + airshipTech + ", clockworkAugments = " + clockworkAugments
	    		   	+", attackAPI = " + attackAPI +
	    		   	", advancedAttackAPI = " + advancedAttackAPI+", digAPI = " + digAPI + ", tradingAPI = " + tradingAPI + ", advancedTradingAPI = " + advancedTradingAPI + ", smAPI = " + smAPI + ", researchAPI = " + researchAPI + 
	    		   	", buildingAPI = " + buildingAPI + ", advancedBuildingAPI = " + advancedBuildingAPI + ", messagingAPI = " + messagingAPI + ", zeppelinAPI = " + zeppelinAPI + 
	    		   	", completeAnalyticAPI = " + completeAnalyticAPI +", version = '" + version + "', nukeAPI = " + nukeAPI +", worldMapAPI = " + worldMapAPI +", owedTicks = " + owedTicks+ ", email ='"+ email +"', pushLog = \"" + pushLog + "\", password = '" + password +  "' where pid = " + ID + ";";*/
	      stmt.executeUpdate();
	      
	      // First, let's get and write to au. I'm not sure if querying first is more labor intensive than just writing. Let's not
	      // worry just yet!
	      stmt.close();
	      int co = 0;
	      stmt = con.createStatement("update attackunit set name = ?, slot = ? where pid = ? and slot = ?;");
	      AttackUnit hau;
	      try {
	      while(co<getAu().size()) {
	    	   hau = getAu().get(co);
	    	  
	    	  stmt.setString(1,hau.getName());
	    	  stmt.setInt(2,hau.getSlot());
	    	  stmt.setInt(3,ID);
	    	  stmt.setInt(4,hau.getSlot());
	    	  stmt.executeUpdate();
	    	  co++;
	      }   } catch(Exception exc) { exc.printStackTrace(); System.out.println("Save saved for " + getUsername()); }
	      rs.close();
	      stmt.close(); // we close these in this if statement so that they don't wait while towns save.
	      stmt =  con.createStatement("update messages set readed = ?, deleted = ? where id = ?;");
	      int i = 0;
	      UserMessagePack ump; UserMessage m;
	      while(i<getMessages().size()) {
	    	  ump = getMessages().get(i);
	    	  int j = 0;
	    	  while(j<ump.getMessages().size()) {
	    		  m = ump.getMessages().get(j);
	    		  stmt.setBoolean(1,m.isReaded());
	    		  stmt.setBoolean(2,m.getDeleted());
	    		  stmt.setString(3,m.getId().toString());
	    		  stmt.execute();
	    		  
	    		  if(m.getDeleted()) {
	    			  
	    			  ump.removeMessage(m);
	    			  j--;
	    			  
	    		  }
	    		  j++;
	    	  }
	    	  if(ump.getMessages().size()==0)  {
				  
				  getMessages().remove(ump);
				  i--;
			  }
	    	  i++;
	      }
	      stmt.close();
	      stmt = con.createStatement("update statreports set readed=?,archived=? where id = ?;");
	      for(UserSR s:getUserSR()) {
	    	  stmt.setBoolean(1,s.read);
	    	  stmt.setBoolean(2,s.archived);
	    	  stmt.setString(3,s.id.toString());
	    	  stmt.execute();
	      }
	      stmt.close();
			 i = 0;
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
		    		 stmt = con.createStatement("update player set owedTicks =  ? where pid = ?;");
		    		 stmt.setInt(1,owedTicks);
		    		 stmt.setInt(2,ID);
		    		 stmt.execute(); // got to save owed ticks.
		    		 stmt.close();
		    		 stmt = con.createStatement("update town set owedTicks = ? where tid = ?;");

		    		 int i = 0;
		    		 while(i<towns().size()) {
		    			 stmt.setInt(1,towns().get(i).owedTicks);
		    			 stmt.setInt(2,towns().get(i).townID);
			    		 stmt.execute(); // got to save owed ticks.

		    			 i++;
		    		 }
		    	 }
			      rs.close();
			      stmt.close();
		      }
		    
		}catch(SQLException exc) { exc.printStackTrace(); }
		
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
		AttackUnit a; boolean set=false;
		while(i<au.size()) {
			a = au.get(i);
			if(a.getSlot()==hau.getSlot()){
				au.set(i,hau);
			}
			i++;
		}
	}
	
	synchronized public void addAu(AttackUnit au) {
		au.setSlot(getAu().size());
		try {
			UberPreparedStatement stmt = con.createStatement("insert into attackunit(name,pid,slot) values (?,?,?);");
			stmt.setString(1,au.getName());
			stmt.setInt(2,ID);
			stmt.setInt(3,au.getSlot());
			
			stmt.execute();
			stmt.close();
			getAu().add(au);
			
			for(Town t: towns()) {
				
				for(Building b: t.bldg()) {
					
					int oldFortArray[] = b.getFortArray();
					int newFort[] = new int[oldFortArray.length+1];
					
					int i = 0;
					while(i<oldFortArray.length) {
						newFort[i]=oldFortArray[i];
						i++;
					}
					b.setFortArray(newFort);
				}
				
				ArrayList<AttackUnit> newTAU = new ArrayList<AttackUnit>();
				int i = 0;
				while(i<=t.getAu().size()) {
					// t.getAu() has 3 aus, 0-2, then at i = 3, the new one will be added for a normal au array.
					// if there are support au present, then this will also be added at 3, but then 
					// the rest will get added normally, but their slot will be increased accordingly. 
					if(i==au.getSlot()) {
						newTAU.add(au.returnCopy());
					}	// still have to add the old au at i, which is probably a support unit, after we add this one to it's correct slot!
					if(i!=t.getAu().size())	{
						if(i>au.getSlot())
							t.getAu().get(i).setSlot(t.getAu().get(i).getSlot()+1); 
						newTAU.add(t.getAu().get(i));
					}

					i++;
				}
				t.setAu(newTAU);
				
			}
			
			save();
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
		
	}
	/*
	public void saveAu(AttackUnit hau) {
		
	try {
	
	    	  
	    	   String weapons="";
	    	  int j = 0;
	    	  while(j<hau.getWeap().length) { // this holds which weapon is what. And all that shizzit.
	    		  weapons+=hau.getWeap()[j]+",";
	    		  j++;
	    		  
	    	  }
	    	  
	    	  int type=hau.getType();
		  	 String updateAU = "update attackunit set name = \"" + hau.getName() + "\", slot = " + hau.getSlot() + ", conc = " + hau.getArmorType() + 
			  ", armor = " + hau.getArmor() + ", cargo = " + hau.getCargo() + ", speed = " + hau.getSpeed() + ", type = " +type + ", weapons = '" +
			  weapons + "' where pid = " + ID +" and slot = "+  hau.getSlot() + ";";
		  	 UberStatement stmt = con.createStatement();
			  stmt.executeUpdate(updateAU);
			  stmt.close();
		 } catch(SQLException exc) { exc.printStackTrace(); } 
	}*/
	public ArrayList<AttackUnit> getAu() {
		if(au==null&&!facsimile) {
			au = new ArrayList<AttackUnit>();

			try{ 
			UberPreparedStatement aus = con.createStatement("select * from attackunit where pid = ?;");
			aus.setInt(1,ID);
			 ResultSet aurs = aus.executeQuery(); // should find six units.



			while(aurs.next()) {
				
				
				au.add(new AttackUnit(aurs.getString(1), aurs.getInt(3),0));
			}
			
			aurs.close();aus.close(); 
		} catch(SQLException exc) { exc.printStackTrace(); }
		}
		return au;
	}
	
	
	public ArrayList<Town> towns() {
		if(towns==null&&!facsimile) {
			towns = new ArrayList<Town>();
		ArrayList<Town> totalTowns = God.getTowns();
		try {
			UberPreparedStatement stmt = con.createStatement("select tid from town where pid = ?;");
			stmt.setInt(1,ID);
			ResultSet rs = stmt.executeQuery();
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
/*
	public void setMemSupportstaff(boolean supportstaff) {
		setBoolean("supportstaff",supportstaff);
	}
	public boolean getMemSupportstaff() {
		return getBoolean("supportstaff");
	}*/
	public void setSupportstaff(boolean supportstaff) {
		this.supportstaff=supportstaff;
	}
	public boolean getSupportstaff() {
		return supportstaff;
	}
	
	public PlayerScript getPs() {
		return ps;
	}/*
	public void setMemCommsCenterTech(int commsCenterTech) {
		setInt("commsCenterTech",commsCenterTech);
	}
	public int getMemCommsCenterTech() {
		return getInt("commsCenterTech");
	}
	*/
	/*
	
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
	*/
	public League getLeague() {
		if(league==null) {
			int lpid=-1; int type=-1;
			try {
				UberPreparedStatement stmt = con.createStatement("select league_pid,type from tpr where pid = ?;");
				stmt.setInt(1,ID);
				ResultSet rs = stmt.executeQuery();
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
	}/*
	public void setMemUsername(String username) {
		setString("username",username);
	}
	public String getMemUsername() {
		return getString("username");
	}*/
	public void setUsername(String username) {
		this.username=username;
	}
	public String getUsername() {
		return username;
	}/*
	public void setMemCapitaltid(int capitaltid) {
		setInt("capitaltid",capitaltid);
	}
	public int getMemCapitaltid() {
		return getInt("capitaltid");
	}*/
	public void setCapitaltid(int capitaltid) {
		this.capitaltid=capitaltid;
	}
	public int getTPushes() {
		return tPushes;
	}


	public void setTPushes(int pushes) {
		tPushes = pushes;
	}


	public boolean isAirshipTech() {
		return airshipTech;
	}


	public void setAirshipTech(boolean airshipTech) {
		this.airshipTech = airshipTech;
	}



	

	public boolean isClockworkAugments() {
		return clockworkAugments;
	}


	public void setClockworkAugments(boolean clockworkAugments) {
		this.clockworkAugments = clockworkAugments;
	}






	public int getCapitaltid() {
		return capitaltid;
	}
	
	
	
	/*
	 * If 0, will add. If 1, will delete. If 2, will edit.
	 * @param hau
	 * @param addDelEdit
	 
	
	public void setAUTemplate(AttackUnit hau) {
		int i = 0;
		ArrayList<AttackUnit> AUTemplates = getAUTemplates();
		while(i<AUTemplates.size()) {
			if(AUTemplates.get(i).getName().equals(hau.getName())) AUTemplates.set(i,hau);
			i++;
		}
	}*//*
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

			//	AUTemplates.add(new AttackUnit(aurs.getString(1), aurs.getDouble(3), aurs.getDouble(4), 
				//		aurs.getDouble(5), aurs.getDouble(6), -1, popSize, weapforau,aurs.getInt(9))); // slot is -1 for template units, no slots, bitches.
			} // NOTICE THE DISCREPANCY, 9 V 10 ON LAST ARGUMENT, DUE TO AUTEMPLATES NOT HAVING A SLOT COLUMN! WATCHHHH!

			aurs.close(); aus.close();
			} catch(SQLException exc) { exc.printStackTrace(); }
			return AUTemplates;
		}
	*/
/*
	public void setMemTownTech(int townTech) {
		setInt("townTech",townTech);
	}
	public int getMemTownTech() {
		return getInt("townTech");
	}*/
	public void setTownTech(int townTech) {
		this.townTech=townTech;
	}
	public int getTownTech() {
		return townTech;
	}/*
	public void setMemFirearmResearch(int firearmResearch) {
		setInt("firearmResearch",firearmResearch);
	}
	public int getMemFirearmResearch() {
		return getInt("firearmResearch");
	}*/
	public void setFirearmResearch(int firearmResearch) {
		this.firearmResearch=firearmResearch;
	}
	public int getFirearmResearch() {
		return firearmResearch;
	}/*
	public void setMemOrdinanceResearch(int ordinanceResearch) {
		setInt("ordinanceResearch",ordinanceResearch);
	}
	public int getMemOrdinanceResearch() {
		return getInt("ordinanceResearch");
	}
	
	public void setMemScholTicks(int scholTicks) {
		setInt("scholTicks",scholTicks);
	}
	public int getMemScholTicks() {
		return getInt("scholTicks");
	}*/
	public void setScholTicks(int scholTicks) {
		this.scholTicks=scholTicks;
	}
	public int getScholTicks() {
		return scholTicks;
	}
	
	public int getScholTicksTotal() {
		return scholTicksTotal;
	}/*
	public void setMemInfrastructureTech(int infrastructureTech) {
		setInt("infrastructureTech",infrastructureTech);
	}
	public int getMemInfrastructureTech() {
		return getInt("infrastructureTech");
	}*/
	public void setInfrastructureTech(int infrastructureTech) {
		this.infrastructureTech=infrastructureTech;
	}
	public int getInfrastructureTech() {
		return infrastructureTech;
	}/*
	public void setMemBodyArmor(int bodyArmor) {
		setInt("bodyArmor",bodyArmor);
	}
	public int getMemBodyArmor() {
		return getInt("bodyArmor");
	}*/
	public void setBodyArmor(int bodyArmor) {
		this.bodyArmor=bodyArmor;
	}
	public int getBodyArmor() {
		return bodyArmor;
	}
	public void setScoutTech(int scoutTech) {
		this.scoutTech=scoutTech;
	}
	public int getScoutTech() {
		return scoutTech;
	}
/*	public void setMemScoutTech(int scoutTech) {
		setInt("scoutTech",scoutTech);
	}
	public int getMemScoutTech() {
		return getInt("scoutTech");
	}
	

	public void setMemPersonalShields(boolean personalShields) {
		setBoolean("personalShields",personalShields);
	}
	public boolean isMemPersonalShields() {
		return getBoolean("personalShields");
	}
	public void setMemHydraulicAssistors(boolean hydraulicAssistors) {
		setBoolean("hydraulicAssistors",hydraulicAssistors);
	}
	public boolean isMemHydraulicAssistors() {
		return getBoolean("hydraulicAssistors");
	}
	public void setMemThrustVectoring(boolean thrustVectoring) {
		setBoolean("thrustVectoring",thrustVectoring);
	}
	public boolean isMemThrustVectoring() {
		return getBoolean("thrustVectoring");
	}

	public void setMemArchitecture(int architecture) {
		setInt("architecture",architecture);
	}
	public int getMemArchitecture() {
		return getInt("architecture");
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
	}*/
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
	
	/*public ArrayList<QuestListener> getMemActiveQuests() {
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
	}*/
	public void setPersonalShields(boolean personalShields) {
		this.personalShields = personalShields;
	}
	public boolean isPersonalShields() {
		return personalShields;
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
	
	public int getArchitecture() {
		return architecture;
	}
	public void setArchitecture(int architecture) {
		this.architecture = architecture;
	}
	
	public int getClockworkComputers() {
		return clockworkComputers;
	}
	public void setClockworkComputers(int clockworkComputers) {
		this.clockworkComputers = clockworkComputers;
	}
	public int getConstructionResearch() {
		return constructionResearch;
	}
	public void setConstructionResearch(int constructionResearch) {
		this.constructionResearch = constructionResearch;
	}
	public int getStructuralIntegrity() {
		return structuralIntegrity;
	}
	public void setStructuralIntegrity(int structuralIntegrity) {
		this.structuralIntegrity = structuralIntegrity;
	}
	public boolean getAdvancedFortifications() {
		return advancedFortifications;
	}
	public void setAdvancedFortifications(boolean advancedFortifications) {
		this.advancedFortifications = advancedFortifications;
	}public boolean getBloodMetalArmor() {
		return bloodMetalArmor;
	}
	public void setBloodMetalArmor(boolean bloodMetalArmor) {
		this.bloodMetalArmor = bloodMetalArmor;
	}
	public int getBloodMetalPlating() {
		return bloodMetalPlating;
	}
	public void setBloodMetalPlating(int bloodMetalPlating) {
		this.bloodMetalPlating = bloodMetalPlating;
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
	public boolean isHydraulicAssistors() {
		return hydraulicAssistors;
	}
	public void setHydraulicAssistors(boolean hydraulicAssistors) {
		this.hydraulicAssistors = hydraulicAssistors;
	}
	public boolean isThrustVectoring() {
		return thrustVectoring;
	}
	public void setThrustVectoring(boolean thrustVectoring) {
		this.thrustVectoring = thrustVectoring;
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
		try {
			UberPreparedStatement stmt = God.con.createStatement("update users set password = md5(?) where username = ?;");
			stmt.setString(1,password);
			stmt.setString(2,getUsername());
			
			stmt.executeUpdate();
			
			stmt.close();
			
			stmt = God.con.createStatement("update player set password = md5(?) where username = ?;");
			stmt.setString(1,password);
			stmt.setString(2,getUsername());
			
			stmt.executeUpdate();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		setPassword(org.apache.commons.codec.digest.DigestUtils.md5Hex(password));
		try {
			Hashtable r = (Hashtable) God.accounts.get(getUsername());
			r.put("password",org.apache.commons.codec.digest.DigestUtils.md5Hex(password)); // gotta update accounts!
		} catch(NullPointerException exc) { exc.printStackTrace(); System.out.println("Whatever was setting was saved."); }
	
	}

	public void setNewFuid(long fuid) {
		try {
			UberPreparedStatement stmt = God.con.createStatement("update users set fuid = ? where username = ?;");
			stmt.setLong(1,fuid);
			stmt.setString(2,getUsername());
			
			stmt.executeUpdate();
			
			stmt.close();
			stmt = God.con.createStatement("update player set fuid = ? where username = ?;");
			stmt.setLong(1,fuid);
			stmt.setString(2,getUsername());
			
			stmt.executeUpdate();
			
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
	
	public void setAu(ArrayList<AttackUnit> au) {
		this.au = au;
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
			UberPreparedStatement stmt = con.createStatement("insert into ap (pid,aid) values (?,?);");
			stmt.setInt(1,ID);
			stmt.setInt(2,((Integer) r.get("aid")));
			
			stmt.execute();
			stmt.close();
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
	}


	public ArrayList<Hashtable> getAchievements() {
		if(achievements==null) {
			achievements = new ArrayList<Hashtable>();
			try {
				UberPreparedStatement stmt = con.createStatement("select aid from ap where pid = ?;");
				stmt.setInt(1,ID);
				ResultSet rs = stmt.executeQuery();
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


	public Building findBuilding(int bid) {
		// TODO Auto-generated method stub
		for(Town t: towns()) {
			
			for(Building b: t.bldg()) {
				
				if(b.bid==bid){
					return b;
				}
			}
		}
		return null;
	}
	public Town findTownWithBuilding(int bid) {
		// TODO Auto-generated method stub
		for(Town t: towns()) {
			
			for(Building b: t.bldg()) {
				
				if(b.bid==bid){
					return t;
				}
			}
		}
		return null;
	}


	public void setOrdinanceResearch(int ordinanceResearch) {
		this.ordinanceResearch = ordinanceResearch;
	}


	public int getOrdinanceResearch() {
		return ordinanceResearch;
	}


	

	public void setTeslaTech(int teslaTech) {
		this.teslaTech = teslaTech;
	}


	public int getTeslaTech() {
		return teslaTech;
	}


	public void setLord(Player lord) {
		this.lord = lord;
	}


	public Player getLord() {
		if(lord==null) {
			try {
				UberPreparedStatement stmt = con.createStatement("select lord from player where pid = ?;");
				stmt.setInt(1,ID);
				ResultSet rs = stmt.executeQuery();
				if(rs.next()) {
					
					int lpid = rs.getInt(1);
					if(lpid!=0) {
						lord = God.getPlayer(lpid);
					}
				}
			} catch(SQLException exc) {
				exc.printStackTrace();
			}
			
		}
		return lord;
	}


	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}


	public double getTaxRate() {
		return taxRate;
	}


	public void setVoluntaryVassal(boolean voluntaryVassal) {
		this.voluntaryVassal = voluntaryVassal;
	}


	public boolean isVoluntaryVassal() {
		return voluntaryVassal;
	}


	public void setLordInternalClock(int lordInternalClock) {
		this.lordInternalClock = lordInternalClock;
	}


	public int getLordInternalClock() {
		return lordInternalClock;
	}


	public void setSecondaryResBuff(long[] secondaryResBuff) {
		this.secondaryResBuff = secondaryResBuff;
	}


	public long[] getSecondaryResBuff() {
		if(secondaryResBuff==null) { // so we only load it if the player needs it!
			long resBuff[] = new long[5];
			try {
				UberPreparedStatement stmt = con.createStatement("select mbuff,tbuff,mmbuff,fbuff from player where pid = ?;");
				stmt.setInt(1,ID);
				ResultSet rs = stmt.executeQuery();
				if(rs.next()) {
		
					resBuff[0]=rs.getLong(1);
					resBuff[1]=rs.getLong(2);
					resBuff[2]=rs.getLong(3);
					resBuff[3]=rs.getLong(4);
					resBuff[4] = 0;
				}
				rs.close();
			stmt.close();
			secondaryResBuff=resBuff;
			} catch(SQLException exc) { exc.printStackTrace(); }
		}
		return secondaryResBuff;
	}


	public void setHoldingLordIteratorID(String holdingLordIteratorID) {
		this.holdingLordIteratorID = holdingLordIteratorID;
	}


	public String getHoldingLordIteratorID() {
		return holdingLordIteratorID;
	}
}


