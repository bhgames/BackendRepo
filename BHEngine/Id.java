package BHEngine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

public class Id extends Player {
	public static double idInfluenceDistance=GodGenerator.mapTileWidthX; // The radius of which an Id town looks for data on what to be.
	public Id(int ID, GodGenerator God) {
		super(ID, God);
		// TODO Auto-generated constructor stub
	}
	
	public void iterate(int num) {
		double hourlyLeft = (getPlayedTicks())/(3600/GodGenerator.gameClockFactor);
		hourlyLeft-=Math.round(hourlyLeft);
		if(hourlyLeft==0) {
			deleteOldPlayers();
		}
		
		double dailyLeft = (getPlayedTicks())/(24*3600/GodGenerator.gameClockFactor);
		dailyLeft-=Math.round(dailyLeft);
		
		if(dailyLeft==0) {
			dailyRoutine();
		}
		double weeklyLeft = (getPlayedTicks())/(7*24*3600/GodGenerator.gameClockFactor);
		weeklyLeft-=Math.round(weeklyLeft);
		if(weeklyLeft==0) {
			weeklyRoutine();
		}
		
		incomingRoutine();
	}
	
	public void dailyRoutine() {
		Date today = new Date();
		int day=today.getDate(); // we can take abs difference of login day and this one, because we know login will be in the past!
		int month = today.getMonth();
		int year = today.getYear();
		double avgMineLvl=0;
		double avgTroopNumber=0;
		double totalTownsCheckedWithLeagues=0; // use this one for troops.
		double totalTownsCheckedWOLeagues=0; // for without leagues, we use this for mines, because leagues do not use mines.
		int i = 0;
		Town t;
		int j = 0;
		Town myT;
		Building b;UberStatement stmt=null;ResultSet rs;
		while(j<towns().size()) {
			myT = towns().get(j);
			i=0;
			while(i<God.getTowns().size()) {
				t = God.getTowns().get(i);
				double dist = Math.sqrt(Math.pow(t.getX()-myT.getX(),2) +Math.pow(t.getY()-myT.getY(),2));
				/*int num = 0;
				try {
					stmt = con.createStatement();
					rs = stmt.executeQuery("select count(*) from player where last_login > current_timestamp - 48*36000 and pid = " + t.getPlayer().ID + ";");
					if(rs.next()) num=rs.getInt(1);
					
					rs.close();
					stmt.close();
				}catch(Exception exc) {
					exc.printStackTrace();
				}*/
				
				if(dist<=idInfluenceDistance&&!t.getPlayer().isQuest()&&t.getPlayer().ID!=5&&t.getPlayer().last_login.getTime()>(today.getTime()-48*3600000)) {
				//	System.out.println(t.getTownName() + " is eligible.");
					// GRABBING MINE DATA
					int k = 0;
					if(!t.getPlayer().isLeague()) {
					while(k<t.bldg().size()) {
						if(t.bldg().get(k).getType().equals("Metal Mine")||
								t.bldg().get(k).getType().equals("Timber Field")||
								t.bldg().get(k).getType().equals("Manufactured Materials Plant")||
								t.bldg().get(k).getType().equals("Food Farm")) avgMineLvl+=t.bldg().get(k).getLvl();
						k++;
					}
					
					totalTownsCheckedWOLeagues++;
					}
					
					// GRABBING TROOP DATA
					
					k = 0;
					while(k<t.getAu().size()) {
						avgTroopNumber+=t.getAu().get(k).getSize()*t.getAu().get(k).getExpmod();
						k++;
					}
					
					k = 0;
					while(k<t.attackServer().size()) {
						int x = 0;
						while(x<t.attackServer().get(k).getAu().size()) {
							avgTroopNumber+=t.attackServer().get(k).getAu().get(x).getSize()*t.attackServer().get(k).getAu().get(x).getExpmod();
							x++;
						}
						k++;
					}
					
					
					totalTownsCheckedWithLeagues++;
				}
				i++;
			}
			avgMineLvl/=totalTownsCheckedWOLeagues*4;
			avgTroopNumber/=totalTownsCheckedWithLeagues;
			
			// SETTING NEW MINE DATA
			i = 0;
			int toSet = (int) Math.round(avgMineLvl-1);
			if(toSet<3) toSet=3;

			System.out.println("Setting " +myT.getTownName() + " to have mine of " + toSet + " and soldiers of " + ((int) Math.round(avgTroopNumber*.1)));
			while(i<myT.bldg().size()) {
				b = myT.bldg().get(i);
				if(b.getType().equals("Metal Mine")||
						b.getType().equals("Timber Field")||
						b.getType().equals("Manufactured Materials Plant")||
						b.getType().equals("Food Farm")) {
					b.setLvl(toSet);
				}
				
				i++;
			}
			
			// SETTING NEW SOLDIER DATA
			
			myT.setSize(0,(int) Math.round(avgTroopNumber*.1));
			
			
		j++;
		}
		
		
	}
	
	public void weeklyRoutine() {
		int i = 0;
		Town t;
		int j = 0;
		Town myT;
		int maxX=0;
		int maxY=0;
		Building b;

		 i = 0;
		while(i<God.getTowns().size()) {
			if(!God.getTowns().get(i).getPlayer().isQuest()&&Math.abs(God.getTowns().get(i).getX())>maxX) maxX = Math.abs(God.getTowns().get(i).getX());
			if(!God.getTowns().get(i).getPlayer().isQuest()&&Math.abs(God.getTowns().get(i).getY())>maxY) maxY = Math.abs(God.getTowns().get(i).getY());

			i++;
		}
		
			// now we got maxX, maxY.
			System.out.println("maxX is " + maxX + " and maxY is " + maxY);
		/*	int multX = (int) Math.floor(GodGenerator.mapTileWidthX/maxX); // Gets the amount of tiles out the maxX is.
			int multY = (int) Math.floor(GodGenerator.mapTileWidthY/maxY); // Gets the amount of tiles out the maxY is.
			maxX = multX*GodGenerator.mapTileWidthX+1;
			maxY = multY*GodGenerator.mapTileWidthY+1; // So if the max X is 14, we find out that it's 9 that's the farthest, but we want one more.
*/
			
		int x =-maxX;
		Town idT;
		ArrayList<Hashtable> cslHash,troopHash,mineHash;
		Hashtable r,v;
		Date today = new Date();
		int day=today.getDate(); // we can take abs difference of login day and this one, because we know login will be in the past!
		int month = today.getMonth();
		int year = today.getYear(); UberStatement stmt; ResultSet rs;
		while(x<=maxX){
			int y = -maxY;
			while(y<=maxY) {
				// new 5x5 block!
				System.out.println("Checking " +x + "," + y + " block...");
				cslHash = new ArrayList<Hashtable>();
				troopHash = new ArrayList<Hashtable>();
				mineHash = new ArrayList<Hashtable>();

				i=0;
				while(i<God.getTowns().size()) {
					t = God.getTowns().get(i);
						if(Math.abs(t.getX()-x)<=idInfluenceDistance&&Math.abs(t.getY()-y)<=idInfluenceDistance&&!t.getPlayer().isQuest()&&t.getPlayer().ID!=5
							&&t.getPlayer().last_login.getTime()>(today.getTime()-48*3600000)/*&&num>0*/) {
						//System.out.println(t.getTownName() + " is eligible.");
						// GRABBING MINE DATA
						int k = 0;
						double avgMineLvl=0,avgTroopNumber=0;
						if(!t.getPlayer().isLeague()) {
						while(k<t.bldg().size()) {
							if(t.bldg().get(k).getType().equals("Metal Mine")||
									t.bldg().get(k).getType().equals("Timber Field")||
									t.bldg().get(k).getType().equals("Manufactured Materials Plant")||
									t.bldg().get(k).getType().equals("Food Farm")) avgMineLvl+=t.bldg().get(k).getLvl();
							k++;
						}
						
						}
						
						// GRABBING TROOP DATA
						
						k = 0;
						while(k<t.getAu().size()) {
							avgTroopNumber+=t.getAu().get(k).getSize()*t.getAu().get(k).getExpmod();
							k++;
						}
						
						k = 0;
						while(k<t.attackServer().size()) {
							int xe = 0;
							while(xe<t.attackServer().get(k).getAu().size()) {
								avgTroopNumber+=t.attackServer().get(k).getAu().get(xe).getSize()*t.attackServer().get(k).getAu().get(xe).getExpmod();
								xe++;
							}
							k++;
						}
						
						avgMineLvl/=4.0;
						
						// now we have mine level and troop levels to put in the loop.
						
						double CSL = t.getPlayer().getPs().b.getCSL(t.townID);
						//STORING DATA
						r = new Hashtable();
						r.put("tid",t.townID);
						r.put("townName",t.getTownName());
						r.put("CSL",CSL);
						r.put("avgMineLvl",avgMineLvl);
						r.put("avgTroopNumber",avgTroopNumber);
						
						cslHash.add(r);
						mineHash.add(r);
						troopHash.add(r);

						
					} 
					
					i++;
				}
				
				// PROCESSING THE BITCH BLOCK.
				
				sort(cslHash,"CSL",true); sort(troopHash,"avgTroopNumber",true); sort(mineHash,"avgMineLvl",true);
				
				
				
				
				
				 i = 0; // DO NOT DELETE THESE LOOPS, THEY PRINT BUT ALSO PUT RANKS IN THAT SHIT.
				while(i<cslHash.size()) {
					cslHash.get(i).put("cslRank",i);
				//	System.out.println("CSL rank " + i + " is " + ((Double) cslHash.get(i).get("CSL")) + " with townName of "+  ((String) cslHash.get(i).get("townName")));
					i++;
				}
				 i = 0;
					while(i<troopHash.size()) {
						troopHash.get(i).put("troopRank",i);
					//	System.out.println("troop rank " + i + " is " + ((Double) troopHash.get(i).get("avgTroopNumber")) + " with townName of "+  ((String) troopHash.get(i).get("townName")));
						i++;
					}
					 i = 0;
					 // FINAL LOOP WE GET AVERAGE RANK AND THEN WE SORT BY IT.
						while(i<mineHash.size()) {
							r = mineHash.get(i);
							r.put("mineRank",i);
							double cslRank = (Integer) r.get("cslRank");
							double troopRank = (Integer) r.get("troopRank");
							double mineRank = (Integer) r.get("mineRank");
							double avg=0;
							if(God.findTown((Integer) r.get("tid")).getPlayer().isLeague()){
								avg=(cslRank+troopRank)/2.0; // LEAGUES DON'T USE MINES.
							} else {
								avg=(cslRank+troopRank+mineRank)/3.0;
							}
							r.put("totalRank",avg);
						//	System.out.println("mine rank " + i + " is " + ((Double) mineHash.get(i).get("avgMineLvl")) + " with townName of "+  ((String) mineHash.get(i).get("townName")));
							i++;
						}
				
						// SORT BY TOTAL RANK
						sort(cslHash, "totalRank",false); // we want the lowest rank, closest to first, since all the little shitters
						// that were high got lower ranks.
						
						 i = 0; 
							while(i<cslHash.size()) {
								System.out.println("total rank " + i + " is " + ((Double) cslHash.get(i).get("totalRank")) + " with townName of "+  ((String) cslHash.get(i).get("townName")));
							//	System.out.println("mine rank score is " + ((Integer) cslHash.get(i).get("mineRank")) + " with townName of "+  ((String) cslHash.get(i).get("townName")));
								//System.out.println("troop rank score is " + ((Integer) cslHash.get(i).get("troopRank")) + " with townName of "+  ((String) cslHash.get(i).get("townName")));
								//System.out.println("csl rank score is " + ((Integer) cslHash.get(i).get("cslRank")) + " with townName of "+  ((String) cslHash.get(i).get("townName")));

								i++;
							}
				
					i = 0;
					if(cslHash.size()>0)
					while(i<towns().size()) {
						idT = towns().get(i);
						if(Math.abs(idT.getX()-x)<=idInfluenceDistance&&Math.abs(idT.getY()-y)<=idInfluenceDistance) { // FIND ANY ID TOWN THAT CAN SEND THE ATTACK.
							
							// NOW WE HAVE OUR PLACE TO SEND THE RAID
							//	public Building addBuilding(String type, int lotNum, int lvl, int lvlUp) {
							 t = God.findTown((Integer) cslHash.get(0).get("tid"));

							 if(t.getPlayer().getPlayedTicks()>(7*24*3600/GodGenerator.gameClockFactor)) {
 
							b = idT.addBuilding("Headquarters",4,1,0);
							
							 idT.setSize(0,(int) Math.round(((Double) cslHash.get(0).get("avgTroopNumber"))/3.0));
							 //	public boolean attack(int yourTownID, int enemyx, int enemyy, int auAmts[], String attackType, int target,String name) {
								System.out.println("Avenging the players by killing " + t.getTownName());

							 int auAmts[] = {idT.getAu().get(0).getSize(),0,0,0,0,0};
							 System.out.println(
							 getPs().b.attack(idT.townID,t.getX(),t.getY(),auAmts,"siege",0,"GARBLEDTRANSMISSION") + ":" + getPs().b.getError());
							 
							 
							 idT.killBuilding(b.bid);
							 }
							 break;
							
						}
						
						i++;
					}
				
				y+=2*idInfluenceDistance;
			}
			x+=2*idInfluenceDistance;
		}
		
	}
	
	public void incomingRoutine() {
		int i = 0;
		ArrayList<Raid> as; Town t; Town idT; Building b;
		while(i<God.getTowns().size()) {
			
			t = God.getTowns().get(i);
			if(t.getPlayer().getPlayedTicks()>(7*24*3600/GodGenerator.gameClockFactor)) { // no hunting those under a week old!
			as = t.attackServer();
			int j = 0;
			try {
			while(j<as.size()) {
				
				if(as.get(j).getTown2().getPlayer().ID==5&&as.get(j).getTicksToHit()==1) {
					// we go on ticksToHit is 1.
					double rand=Math.random();
					if(rand>0&&rand<.09) {
						idT = as.get(j).getTown2();
						if(idT.getAu().get(0).getSize()>0) {
							
							b = idT.addBuilding("Headquarters",4,1,0);
								 //	public boolean attack(int yourTownID, int enemyx, int enemyy, int auAmts[], String attackType, int target,String name) {
								System.out.println("Defending myself by hitting " + t.getTownName());

							 int auAmts[] = {idT.getAu().get(0).getSize(),0,0,0,0,0};
							 System.out.println(
							 getPs().b.attack(idT.townID,t.getX(),t.getY(),auAmts,"attack",0,"GARBLEDTRANSMISSION") + ":" + getPs().b.getError());
							 
							 
							 idT.killBuilding(b.bid);
						}
					} else if(rand>=.09&&rand<=.1) {
						idT = as.get(j).getTown2();
						int k = 0; double totalNum=0;
						while(k<as.get(j).getAu().size()) {
							totalNum+=as.get(j).getAu().get(k).getSize()*as.get(j).getAu().get(k).getExpmod();
							k++;
						}
						idT.setSize(0,(int) Math.round(totalNum*.3));
						
							b = idT.addBuilding("Headquarters",4,1,0);
								 //	public boolean attack(int yourTownID, int enemyx, int enemyy, int auAmts[], String attackType, int target,String name) {
								System.out.println("Defending myself by sieging " + t.getTownName());
							
							 int auAmts[] = {idT.getAu().get(0).getSize(),0,0,0,0,0};
							 System.out.println(
							 getPs().b.attack(idT.townID,t.getX(),t.getY(),auAmts,"siege",0,"GARBLEDTRANSMISSION") + ":" + getPs().b.getError());
							 
							 
							 idT.killBuilding(b.bid);
						
					}
					
				}
				j++;
			}
			
			} catch(Exception exc) { exc.printStackTrace(); if(as.get(j)!=null) { System.out.println("RaidID is " + as.get(j).raidID); } System.out.println("Id saved.");} 
			}
			i++;
			
		}
	}
	synchronized public void save() {
		try {
		UberStatement stmt = con.createStatement();
		stmt.execute("update God set gameClock = " + God.gameClock);
		
		} catch(SQLException exc) { exc.printStackTrace(); }
		super.save();
	}
	public void deleteOldPlayers() {
		System.out.println("Checking players.");
		int i = 0;
		ArrayList<Player> players = God.getPlayers();
		Player p;
		Date today = new Date(); Town theCapital; Town t;
		while(i<players.size()) {
			p = players.get(i);
			if(p.ID!=5&&!p.isQuest())// NOTICE LEAGUES CAN'T BE DELETED THOUGH...
			try {
			if(!p.completedQuest("BQ1")&&p.getPs().b.getCSL(p.getCapitaltid())<100&&p.last_login.getTime()<(today.getTime()-2*24*3600000)&&p.getPlayedTicks()<(7*24*3600/GodGenerator.gameClockFactor)) {
				// kill the bastard.
				System.out.println(p.getUsername() + " is eligible for early player deletion as their difference is " + (p.last_login.getTime()-(today.getTime()-48*3600000))+"s and last_login is " + p.last_login + " and they have not completed BQ1.");
				
				//check surrounding players.
				int j = 0;
				theCapital = God.getTown(p.getCapitaltid());
				double avgPlayTicks=0; double townsHit=0;
				while(j<God.getTowns().size()) {
					t = God.getTowns().get(j);
					if(t.getPlayer().ID!=5&&!t.getPlayer().isQuest()) { // NOTICE WE COUNT LEAGUES HERE IN TIME PLAYED
				
					double dist = Math.sqrt(Math.pow(t.getX()-theCapital.getX(),2) + Math.pow(t.getY()-theCapital.getY(),2));
				
					if(dist<idInfluenceDistance) {
						townsHit++;
						avgPlayTicks+=p.getPlayedTicks();
						
					}
				
					}
					j++;
				}
				avgPlayTicks/=townsHit;
				if(avgPlayTicks>(14*24*3600/GodGenerator.gameClockFactor)) {
					System.out.println("Avgplayticks around " + p.getUsername() + " is " + avgPlayTicks + " and so this is not a playable town.");

				God.deletePlayer(p,false);
				}
				else {
					System.out.println("Avgplayticks around " + p.getUsername() + " is " + avgPlayTicks + " and so this is a playable town.");

				God.deletePlayer(p,true);
				}
				
				i--; // the player was removed from the list.
			} else if(!p.completedQuest("BQ1")&&p.getPs().b.getCSL(p.getCapitaltid())<100&&p.last_login.getTime()<(today.getTime()-24*3600000)&&p.getPlayedTicks()<(7*24*3600/GodGenerator.gameClockFactor))
			{// send warning email if less than week and > 24 hrs
				if(p.last_login.getTime()>=today.getTime()-25*3600000&&!p.getEmail().equals("nolinkedemail")&&!p.getEmail().equals(null))
				God.sendMail(p.getEmail(),p.getUsername(),"Account Deletion Notice","Account Deletion Notice","");
			} else if(p.last_login.getTime()<(today.getTime()-13*24*3600000)&&p.getPlayedTicks()>=(7*24*3600/GodGenerator.gameClockFactor))
			{ // send email if > week and > 13 days
				if(p.last_login.getTime()>=(today.getTime()-(13*24+1)*3600000)&&!p.getEmail().equals("nolinkedemail")&&!p.getEmail().equals(null))
				God.sendMail(p.getEmail(),p.getUsername(),"Account Deletion Notice","Account Deletion Notice", "");
			}else if(p.last_login.getTime()<(today.getTime()-14*24*3600000)&&p.getPlayedTicks()>=(7*24*3600/GodGenerator.gameClockFactor)) {
				System.out.println(p.getUsername() + " is eligible for late deletion.");
				God.deletePlayer(p,false);
				i--;
			} else {
				System.out.println(p.getUsername() + " is NOT eligible for early player deletion as their difference is " + (p.last_login.getTime()-(today.getTime()-48*3600000))+"s and last_login is " + p.last_login);

			}
			} catch(Exception exc) { exc.printStackTrace(); getPs().b.sendYourself("I tried to delete " + p.ID + " but failed.","Error report."); System.out.println("Id was saved."); }
			
			
			i++;
		}
	}
	public void sort(ArrayList<Hashtable> cslHash, String toSort, boolean more) {
		//if more is true, then the largest will be on top.
		
		int i = 0; Hashtable r,v;
		int numSorts=0;
		do {
			numSorts=0; // we reset the number of sorts here.
			i=0;
			while(i<cslHash.size()-1) {
				if((more&&(Double) cslHash.get(i).get(toSort)< (Double) cslHash.get(i+1).get(toSort))
			||(!more&&(Double) cslHash.get(i).get(toSort)> (Double) cslHash.get(i+1).get(toSort))){
					r = cslHash.get(i);
					v = cslHash.get(i+1);
				//	System.out.println(toSort + " is  " +r.get(toSort) + " for " +i + " and " +v.get(toSort) + " for " + (i+1));

					cslHash.set(i,v);
					cslHash.set(i+1,r); // switch em.
					numSorts++;
				}
				
				i++;
			}
			//System.out.println("numSorts is " + numSorts + " for  " + toSort);
		} while(numSorts>0);
	}
}
