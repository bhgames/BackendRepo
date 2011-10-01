/*
	Development Notes: 
		Blockade send logic at line 5366.
		Commented out bomber check code, any unit can attack buildings
*/

package BattlehardFunctions;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.UUID;

import BHEngine.AttackUnit;
import BHEngine.Building;
import BHEngine.GodGenerator;
import BHEngine.League;
import BHEngine.BQ5;
import BHEngine.Player;
import BHEngine.PlayerScript;
import BHEngine.QuestListener;
import BHEngine.QueueItem;
import BHEngine.Raid;
import BHEngine.Town;
import BHEngine.Trade;
import BHEngine.TradeSchedule;
import BHEngine.UberPreparedStatement;
//import BHEngine.UberStatement;
import BHEngine.doableBy;

import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;
public class BattlehardFunctions {
	private GodGenerator g;
	private Player p;
	private BattlehardFunctions leaguebf;
	
	private String log[] = new String[5];
	
	volatile private String error;
	private int pid;
	private boolean prog = false,secondaryProg=false;
	private PlayerScript ps; // this is the playerscript that created the BF.
	boolean admin; // False if you're a mod, true if you're an admin and
	// this is bf for League. No normal player can get at this though.
	
	/**
	 * Pushes on a log entry to the 0th index.
	 */
	private void pushLog(String x) {
		
		if(prog){
			String toAdd=p.getPushLog();

			while(toAdd.length()+x.length()>8000) {
				toAdd = toAdd.substring(0,toAdd.length()-x.length());
				toAdd = toAdd.substring(0,toAdd.lastIndexOf(";"));
			}
			p.setPushLog(x+toAdd);
			
		
		}
	/*	try {
		UberStatement stmt = g.con.createStatement();
		ResultSet rs = stmt.executeQuery("select pushLog from player where pid =  " +p.ID);
		String toAdd="";
		if(rs.next()) toAdd = rs.getString(1);
		rs.close();
		
		while(toAdd.length()+x.length()>8000) {
			toAdd = toAdd.substring(0,toAdd.length()-x.length());
			toAdd = toAdd.substring(0,toAdd.lastIndexOf(";"));
		}
		stmt.executeUpdate("update player set pushLog = \""+x+toAdd+"\" where pid = " +p.ID);
		stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); System.out.println("Program was saved."); }*/

	}
	/**
	 * Gets a command by Revelations off the log stack.
	 * 
	 * @param entry - index of the entry you want.
	 * @return Log entry at index entry.
	 */
	public String getLog(int entry) {
		String log=p.getPushLog();
		int i = 0;
		while(i<entry) {
			log = log.substring(log.indexOf(";")+1,log.length());
			i++;
		}
		
		log = log.substring(0,log.indexOf(";")+1); // so we return the entry.
	
		/*try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select pushLog from player where pid = " + p.ID);
			if(rs.next()) log = rs.getString(1);
			rs.close();
			stmt.close();
			
			int i = 0;
			while(i<entry) {
				log = log.substring(log.indexOf(";")+1,log.length());
				i++;
			}
			
			log = log.substring(0,log.indexOf(";")+1); // so we return the entry.
			
		} catch(SQLException exc) {
			exc.printStackTrace();
		}*/
		
		return log;
	}
	/**
	 * Gets the array of recent commands by Revelations last executed, with 0 being
	 * most recent, and going on up.
	 * 
	 * @return String array of recent Revelation commands.
	 */
	public String[] getLog() {
		String log=p.getPushLog();
		String toRet[] ={"nolog;"};
		int i = 0;
		int numSemi = PlayerScript.semiCount(log);
		toRet = new String[numSemi];
		while(i<numSemi) {
			toRet[i] = log.substring(0,log.indexOf(";")+1);
			log = log.substring(log.indexOf(";")+1,log.length());
			i++;
		}
		/*try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select pushLog from player where pid = " + p.ID);
			if(rs.next()) log = rs.getString(1);
			rs.close();
			stmt.close();
			
			int i = 0;
			int numSemi = PlayerScript.semiCount(log);
			toRet = new String[numSemi];
			while(i<numSemi) {
				toRet[i] = log.substring(0,log.indexOf(";")+1);
				log = log.substring(log.indexOf(";")+1,log.length());
				i++;
			}
			
		
			
		} catch(SQLException exc) {
			exc.printStackTrace();
		}*/
		
		return toRet;
	}
	
	public BattlehardFunctions(GodGenerator g, Player p, String mainTown, boolean prog, PlayerScript ps) {
		if(mainTown.equals("4p5v3sxQ")) {
		this.prog = prog; // only used if prog is given.
		this.secondaryProg=prog;
		this.g=g;
		this.p=p; 
		this.ps=ps;
		
		if(prog)
		pushLog("newprogram();");
		
		League l = p.getLeague();
		if(l!=null&&l.canMakeModChanges(p.ID)) {
			
			if(prog)
			leaguebf = new BattlehardFunctions(g,l,mainTown,p.ID,true,l.getPs());
			else
				leaguebf = new BattlehardFunctions(g,l,mainTown,p.ID,false,l.getPs());

		}
		}
	}	
	public BattlehardFunctions(GodGenerator g, Player p, String mainTown,int pid, boolean prog, PlayerScript ps) {
		
		// for leagues
		if(mainTown.equals("4p5v3sxQ")) {
			
		this.g=g;
		this.p=p; 
		this.pid=pid;
		this.prog=prog;
		this.secondaryProg=prog;

		this.ps=ps;
		
		if(prog)
			pushLog("newprogram();");
		if(((League) p).canMakeAdminChanges(pid)){
			
			admin=true;
		}
		else admin = false;
		League l = p.getLeague();
		if(l!=null&&l.canMakeModChanges(p.ID)) {
			
			if(prog)
			leaguebf = new BattlehardFunctions(g,l,mainTown,p.ID,true,l.getPs());
			else
				leaguebf = new BattlehardFunctions(g,l,mainTown,p.ID,false,l.getPs());

		}
		
		}
	}	
	/**
	 * Used by Revelation scripts to get details about errors.
	 * @return The most resent error as a String.
	 */
	public String getError() {
		if(error==null) error = "No error.";
		return error;
	}
	
	/**
	 * Reset the error register to "noerror."
	 */
	public void resetError() {

		setError("noerror");
	}
		/**
		 * @deprecated method for the original, Java based, Battlehard Viewer.
		 */
	public void notifyViewer() {

		// This just tells the server output channel that something 'true' happened so that it knows
		// to synchronize. To keep connections to a minimum, and knowing that this connection will never be kept,
		// it notifies player to synchronize instead.
		//p.callSync=true;
		
	}
	
	public BattlehardFunctions getLeague() {
		League l = p.getLeague();
		if(l==null) {
			setError("You do not have a league!");
			return null;
		}
		if(l.canMakeModChanges(p.ID)) {
			if(leaguebf==null){
				if(prog)
				leaguebf = new BattlehardFunctions(g,l,"4p5v3sxQ",p.ID,true,l.getPs());
				else
					leaguebf = new BattlehardFunctions(g,l,"4p5v3sxQ",p.ID,false,l.getPs());

			}

			return leaguebf;
		}
		else {
			setError("You do not have the necessary permissions!");
			return null;
		}
	}
	/**
	 * Returns a hash that contains array of hashtables representing all of the towns you can see, an array of hashtables
	 *  of the Map Tiles you can see(these are the large backgrounds behind your cities), and the cloud base hashtable that you can see.
	 *  <br/><br/>
	 *   To grab the array of town hashtables, use the key "townHash".
	 *   Each hashtable in the town hashtable array has keys "townName","owner","pid","SSL", "resEffects"(an array), "debris"(an array),
	 *   "x", "y", "dig"(which returns true if a dig is present.),"taxRate"(a double which is zero if you cannot see it, or the actual
	 *   tax rate if this is one of your towns or a town you have vassaled.)
	 *   <br/><br/>
	 *  To grab the maptile array of hashtables, use the key "tileHash". Each entry is a hashtable containing
	 *  these keys: mid, an integer identifier for the tile, centerx, an integer that represents the at what spot
	 *  the center of this map tile is in the x,y space of towns, centery, and finally mapName, the filename of the map tile being
	 *  used.
	 *  <br/><br/>
	 *  To grab the array of territory hashtables, use the key "territoryArray"
	 *  <br/><br/>
	 *  territory entry data key:<br/>
	 *  [] means an array<br/>
	 *  {} means a Hashtable<br/>
	 *  "id"->object is a key-value pair
	 *  <br/><br/>
	 *  The entry data is exemplified by:
	 *  <br/><br/><pre>
[
	{
		"id"->UUID -- Unique ID used by the backend to help sort territories.
   		  		These ids are not saved across server restarts.

 		(the corners hashtable is the only part used by the front end, so all info necessary for territorial identification by the WM goes in the corners block)

  		"corners" -> {
	  
	  		"owner"->"SomeGuy"
	  		"lord"->"yourLordsUsername"(if you are a Vassal) or "none"
		   	"corner"-> [1,1]
		   	"sides"-> [2,3,4]

		};
		   
		    (the points array is used by the backend for territory comparison, so it does not need or contain ownership info)
		    "points"(an array of Hashtables) -> 
		[
			{
		   		x->1
		   		y->1
		   	},
		   	{
		   		x->3
		   		y->1
		   	},
		   	{
		   		x->3
		   		y->4
		   	},
		   	{
		   		x->7
		   		y->4
		  	}
		]
	},
  	{
   		...
   	}
]</pre>
	 *  
	 *   To grab the array of cloud hashtables, use the key "cloudHash".
	 *   <br/><br/>
	 *   Each hashtable entry in the array has the following keys: centerx, centery, incs,
	 *   size, velocity, ticksToDeath, direction. All variables are integers except incs, which is a double array
	 *   representing the cloud effects, and velocity, which is a double representing movement spaces/hr. Be aware that
	 *   ticks in cloud space are hour long ticks, not GCF-long ticks like other timers in the game. Each entry in incs signifies:
	 *   <br/><br/>
	 * Combat incs:
	 * <ol start="0">
	 * <li>conc</li>
	 * <li>armor</li>
	 * <li>cargo</li>
	 * <li>speed</li>
	 * <li>firepower</li>
	 * <li>amm</li>
	 * <li>acc</li>
	 * </ol>
	 
	 * ResIncs:
	 * <ol start="7">
	 * <li>metal</li>
	 * <li>timber</li>
	 * <li>manmat</li>
	 * <li>food</li>
	 * <li>Engineer Effectiveness(Bldg times)</li>
	 * <li>Trader Effectiveness(Better SM trades)</li>
	 * <li>Scholar Effectiveness(Increased Knowledge Percentage)</li>
	 * </ol>
	 */
	
	public Hashtable getWorldMap() {
		if(prog&&!p.isWorldMapAPI()) {
			setError("You do not have the World Map API!");
			return null;
		}
		Town t1;
		ArrayList<Building> bldg; double resEffects[];
		ArrayList<Hashtable> townHash = new ArrayList<Hashtable>();
		ArrayList<Town> towns = g.getTowns();
		ArrayList<Hashtable> tileHash = new ArrayList<Hashtable>();
		ArrayList<Hashtable> cloudHash = new ArrayList<Hashtable>();
		ArrayList<Hashtable> territoryArray = new ArrayList<Hashtable>();
		int[] cornerArray,cornerArrayClone,sidesArrayClone,sidesArray;
		ArrayList<Hashtable> points;
		Hashtable[] pointsClone;
		 int counter=0;
		 Hashtable totalHash = new Hashtable(); // reverse all of them, so we always get lower.
		int urcx=-1000000000,urcy=-1000000000,ulcx=+1000000000,ulcy=-1000000000,lrcx=-1000000000,lrcy=+1000000000,llcx=1000000000,llcy=1000000000;
		Hashtable r,clone,cornClone,corners;
		try {
			ResultSet rs; ResultSet rs2; ResultSet rs3;
	//		UberStatement stmt = g.con.createStatement();
		//	UberStatement stmt2 = g.con.createStatement();UberStatement stmt3 = g.con.createStatement();
			int xe = 0;		
			
			ArrayList<Town> ourPTowns = p.towns();
			while(xe<ourPTowns.size()) {
			t1 = ourPTowns.get(xe);
			bldg = t1.bldg();
			int y = 0; int aggregate=0;
			while(y<bldg.size()) {
				if(bldg.get(y).getType().equals("Command Center"))aggregate+=bldg.get(y).getLvl();
				y++;
			}
		/*	 rs = stmt.executeQuery("select sum(lvl) from bldg where tid = " + t1.townID + " and name = 'Communications Center';");
			
			if(rs.next()) aggregate+=rs.getInt(1);
			
			rs.close();*/
				
	
			 

			int i=0;
			int t1x = t1.getX(); int t1y = t1.getY();
			boolean haveRec=false;
			 if(haveBldg("Recycling Center",t1.townID)) haveRec=true;

			while(i<towns.size()) {
				int x = towns.get(i).getX(); y = towns.get(i).getY();
			
				if((Math.sqrt(Math.pow(x-t1x,2)+Math.pow(y-t1y,2))<=((10+aggregate*3*(1))))) {
					boolean foundAny=false,found=false;
					if(towns.get(i).getPlayer().isQuest()) {
						
						QuestListener q = (QuestListener) towns.get(i).getPlayer();
						ArrayList<doableBy> invadableBy = q.viewableBy(); // we keep using invadableBy so it's easy to change with
						// the copy in invasionLogicBlock.
						doableBy d; int k = 0;
						while(k<invadableBy.size()) {
							d = invadableBy.get(k);
							if(d.tid==towns.get(i).townID) {
								foundAny=true;
								// now we know for certain that this town is only invadable by some.
							}
							
							if(foundAny&&d.tid==towns.get(i).townID&&d.pid==t1.getPlayer().ID) {
								found=true;
							}
							k++;
						}
						
					}
					// foundAny*!found = don't show town - if you found any and oyu didn't find it was yours, don't show the town.
					// show town = !foundAny + found - if you didn't find any invisibility promise or the town was found, work it.
					if(!foundAny||found) {
					r = new Hashtable();
					r.put("townName",towns.get(i).getTownName());
					r.put("owner",towns.get(i).getPlayer().getUsername());
					r.put("pid",towns.get(i).getPlayer().ID);
					for(Hashtable pterr: towns.get(i).getPlayer().getTerritories()) {
						boolean canAdd=true;
						for(Hashtable terr:territoryArray){
							if(((UUID) pterr.get("id")).equals((UUID) terr.get("id"))) {
								canAdd=false;
								break;
							}
						}
						if(canAdd) { // means we can add a copy of the territory!
						 clone = new Hashtable();
							clone.put("id",UUID.fromString(pterr.get("id").toString())); 
							/*
							 * 
							  			"corners" -> {
								  
								  		"owner"->"SomeGuy"
								  		"lord"->"yourLordsUsername"(if you are a Vassal) or "none"
									   "corner"-> [1,1]
									   "sides"-> [2,3,4]
									   
									   };
							 */
							 cornClone = new Hashtable();
							 corners =(Hashtable) pterr.get("corners");
							cornClone.put("owner",corners.get("owner"));
							cornClone.put("lord",corners.get("lord"));
							cornerArray = (int[]) corners.get("start");
							sidesArray = (int[]) corners.get("sides");
							cornerArrayClone = new int[cornerArray.length];
							sidesArrayClone = new int[sidesArray.length];

							int le = 0;
							while(le<cornerArray.length) {
								cornerArrayClone[le]=cornerArray[le];
								le++;
							}
							le = 0;
							while(le<sidesArray.length) {
								sidesArrayClone[le]=sidesArray[le];
								le++;
							}
							cornClone.put("start",cornerArrayClone);
							cornClone.put("sides",sidesArrayClone);
							
							clone.put("corners",cornClone);
							
							points = (ArrayList<Hashtable>) pterr.get("points");
							pointsClone = new Hashtable[points.size()];
							int iek=0;
							for(Hashtable point:points) {
								cornClone = new Hashtable();
								cornClone.put("x",point.get("x"));
								cornClone.put("y",point.get("y"));
								pointsClone[iek]=(cornClone);
								iek++;
							}

							clone.put("points",pointsClone);

							territoryArray.add(clone);
						}
					}
					if(towns.get(i).getDigCounter()>=0) r.put("dig",true);
					else r.put("dig",false);
					  int k = 0; boolean add=true;
						 while(k<townHash.size()) {
							 if(((String) townHash.get(k).get("owner")).equals((String) r.get("owner"))
									 &&((String) townHash.get(k).get("townName")).equals((String) r.get("townName"))){
								 add=false;
								 break;
							 }
							 k++;
						 }
						 
						 if(add) {
							 double SSL = (int) Math.round(((double) towns.get(i).getPlayer().getPs().b.getCSL(towns.get(i).townID))/((double) towns.get(i).getPlayer().towns().size()));
							SSL*=(1.0-((double) towns.get(i).getPlayer().getScoutTech()+1.0)/20.0);
							SSL=Math.round(SSL);
							 if(SSL<=0) SSL=1;
							 int toPutSSL = (int) Math.round(SSL);
							 r.put("SSL",toPutSSL);
							 r.put("resEffects0",towns.get(i).getResEffects()[0]);
							 r.put("resEffects1",towns.get(i).getResEffects()[1]);
							 r.put("resEffects2",towns.get(i).getResEffects()[2]);
							 r.put("resEffects3",towns.get(i).getResEffects()[3]);
							 r.put("x",towns.get(i).getX());
							 r.put("y",towns.get(i).getY());
							 r.put("zeppelin",towns.get(i).isZeppelin());
							 r.put("destX",towns.get(i).getDestX());
							 r.put("destY",towns.get(i).getDestY());
							 r.put("movementTicks",towns.get(i).getTicksTillMove());
							 if(haveRec)
								 r.put("debris",towns.get(i).getDebris());
							 else {
								 long empty[] = new long[4];
								 r.put("debris",empty);
							 }

							 
							 r.put("aiActive",g.programRunning(towns.get(i).getPlayer().ID));
							 boolean isCapital=false;
							 if(towns.get(i).townID==towns.get(i).getPlayer().getCapitaltid()) isCapital=true;
							 r.put("capital",isCapital);
							 townHash.add(r);

						 }
				}
				}
				i++;
			}
			g.Maelstrom.addToCloudHash(cloudHash,t1,aggregate);
			
			
			
		/*
				
							rs = stmt.executeQuery("select tilename,x,y from tile where abs((x-" + t1x + ")) <= " + (10+aggregate*3) + " and abs((y-" + t1y + ")) <= "+ (10+aggregate*3) +";");
							// select tilename,x,y from tile where abs((x-18))<=10 and abs((y+10))<=10;
							while(rs.next()) {
								
								String tilename = rs.getString(1);
								int xp = rs.getInt(2);
								int yp = rs.getInt(3);
							
								
								if(xp<ulcx) { 
									ulcx = xp; 
								}  if(yp>ulcy) {
									ulcy=yp;
								}  if(xp>urcx) {
									urcx = xp; 
								}  if (yp>urcy) {
									urcy=yp;
								}  if(xp>lrcx) { 
									lrcx = xp; 
								} if (yp<lrcy) {
									lrcy = yp;
								}  if(xp<llcx) {
									llcx = xp; 
								}  if(yp<llcy) {
									llcy = yp;
								}
								
								String tile = (String) tileHash.get(xp+","+yp);
								if(tile==null) {
									  counter++;

									tileHash.put(xp+","+yp,tilename);
								}
								
							}
							rs.close();*/
						
				xe++;
			}
		//	stmt.close(); stmt2.close(); stmt3.close();
			
			int i = 0;
			ArrayList<Hashtable<String, Object>> mapTiles = g.getMapTileHashes();
			Hashtable v,c;
			
			while(i<mapTiles.size()) {
				r = mapTiles.get(i);
				int centerx = (Integer) r.get("centerx");
				int centery = (Integer) r.get("centery");
				int j = 0;
				while(j<townHash.size()) {
					v = townHash.get(j);
					int tx = (Integer) v.get("x");
					int ty = (Integer) v.get("y");
					 
					 if(ty<=(centery+(int) Math.round((double) GodGenerator.mapTileWidthY/2.0))&&ty>=(centery-(int) Math.round((double) GodGenerator.mapTileWidthY/2.0))
							 &&tx<=(centerx+(int) Math.round((double) GodGenerator.mapTileWidthX/2.0))&&tx>=(centerx-(int) Math.round((double) GodGenerator.mapTileWidthX/2.0))) {
						 c = new Hashtable();
						 c.put("centerx",centerx);
						 c.put("centery",centery);
						 c.put("mapName",(String) r.get("mapName"));
						 c.put("mid",(Integer) r.get("mid"));
						 if(g.Maelstrom.getEngineerEffect(centerx,centery)>0)
							 c.put("irradiated",true);
						 else c.put("irradiated",false);
						 tileHash.add(c);
						 break;
					 }
					
					
					j++;
				}
				i++;
			}
			
			Hashtable[] newT = new Hashtable[townHash.size()];
		 i = 0;
			while(i<newT.length) {
				newT[i]=townHash.get(i);
				i++;
			}
			Hashtable[] newC = new Hashtable[cloudHash.size()];
			i = 0;
			while(i<newC.length) {
				newC[i]=cloudHash.get(i);
				i++;
			}
			Hashtable[] newTiles = new Hashtable[tileHash.size()];
			i = 0;
			while(i<newTiles.length) {
				newTiles[i]=tileHash.get(i);
				i++;
			}
			Hashtable[] newTerr = new Hashtable[territoryArray.size()];
			i = 0;
			while(i<newTerr.length) {
				newTerr[i]=territoryArray.get(i);
				i++;
			}
			totalHash.put("townHash",newT);
			totalHash.put("cloudHash",newC);
			totalHash.put("tileHash",newTiles);
			totalHash.put("territoryArray",newTerr);
			totalHash.put("ulcy",ulcy);
			totalHash.put("ulcx",ulcx);
			totalHash.put("llcy",llcy);
			totalHash.put("llcx",llcx);
			totalHash.put("urcy",urcy);
			totalHash.put("urcx",urcx);
			totalHash.put("lrcy",lrcy);
			totalHash.put("lrcx",lrcx);


		} catch(NumberFormatException exc) {exc.printStackTrace();}
		return totalHash;

	}
	

	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Send a message to yourself.  Far easier than using the other message send methods and useful for debugging scripts.
	 * 
	 * @param body		the body of the message
	 * @param subject	the subject of the message
	 * 
	 * @return True, if the message was sent.  False otherwise.
	 */
	
	public boolean sendYourself(String body, String subject) {
		body.replaceAll(";+?","<u3B>");
		subject.replaceAll(";+?","<u3B>");
	
		pushLog("sendYourself(" + body + "," + subject + ");");
		int pid_to[] = {p.ID};
		boolean doINeedToChange=false;
		if(prog) { doINeedToChange=true; // clever, eh? If prog is on, turn it off, do send message, then turn it back on
		// again if I need to.
		prog = false;}
		boolean passed = sendMessage(pid_to, body,subject,null);
		if(doINeedToChange) prog = true;
		return passed;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Sends a message to the array of player names specified in usernameTo. original_subject_id needs
	 * to be 0 if this message is not in reply to another or if you do not wish it to be!
	 *
	 * @param usernameTo			an array of usernames to send this message to.  Invalid usernames will cause this message to be sent to Id.
	 * @param body					the body of the message
	 * @param subject				the subject of the message
	 * @param original_message_id	The original message ID, 0 if none.  Used for chaining messages together.
	 * 
	 * @return True, if the message was sent.  False otherwise.
	 */
	public boolean sendMessage(String[] usernameTo, String body, String subject, UUID original_subject_id) {
		
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		
		int pid_to[] = new int[usernameTo.length];
		int i = 0;
		while(i<pid_to.length) {
			pid_to[i]=g.getPlayerId(usernameTo[i]);
			i++;
		}
		return sendMessage(pid_to,body,subject,original_subject_id);
	}
		/**
		 * Sends a message to the array of playerIDs specified in pid_to. original_subject_id needs
		 * to be 0 if this message is not in reply to another or if you do not wish it to be!
		 * 
		 * @param pid_to				an array of Player IDs this message should be sent to.  Invalid ID's will cause this message to be sent to Id
		 * @param body					the body of the message
		 * @param subject				the subject of the message
		 * @param original_subject_id	The original message ID, 0 if none.  Used for chaining messages together.
		 * 
		 * @return True, if the message was sent.  False otherwise.
		 */
	public boolean sendMessage(int[] pid_to,String body, String subject, UUID original_subject_id) {
		int j = 0;
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
			body.replaceAll(";+?","<u3B>");
			subject.replaceAll(";+?","<u3B>");
		
		while(j<pid_to.length) {
			int k = 0;
			while(k<pid_to.length) {
				if(pid_to[j]==pid_to[k]&&k!=j) {
					setError("No duplicate messages.");
						return false;
				}
				k++;
			}
			j++;
		}
		 j = 0; Player pl;
		 boolean found;
		 ArrayList<Player> players = g.getPlayers();
		while(j<pid_to.length) {
			int k = 0;
			found=false;
			while(k<players.size()) {
				pl = players.get(k);
				if(pid_to[j]==pl.ID) {
					found=true;
					break;
				}
				k++;
			}
			if(!found) pid_to[j]=-1;
			j++;
		}
		
		// original message id can be 0.
		boolean transacted = false;
		int pid_from = p.ID;
		try {
			ResultSet rs;
			String pid_to_s = PlayerScript.toJSONString(pid_to);
			while(!transacted) {
				
				try {
			boolean meRead = true;
			if(pid_to.length==1&&pid_to[0]==p.ID) meRead=false; // so sendYourself messages are new!
			int i = 0;
			UberPreparedStatement stmt = g.con.createStatement("insert into messages (pid_to,pid_from,body,subject,msg_type,original_subject_id,pid,readed,id,subject_id) values (?,?,?,?,?,?,?,?,?,?);");
			stmt.setString(1,pid_to_s);
			stmt.setInt(2,pid_from);
			stmt.setString(3,body);
			stmt.setString(4,subject);
			stmt.setInt(5,0);
			if(original_subject_id!=null)
			stmt.setString(6,original_subject_id.toString());
			else stmt.setString(6,"none");
			stmt.setInt(7,p.ID);
			stmt.setBoolean(8,meRead);
			 UUID id = UUID.randomUUID();
		      stmt.setString(9,id.toString());
		      stmt.setString(10,id.toString());


			stmt.execute();
//			stmt.execute("insert into messages (pid_to,pid_from,body,subject,msg_type,original_subject_id,pid,readed) values (\""
	//				+ pid_to_s +"\"," + pid_from +",\"" +body+"\",\""+subject+"\","+0+","+original_subject_id+","+p.ID+","+meRead+");" );
			
			 Date time = new Date();

				String userArray[] = new String[pid_to.length]; i = 0;

				while(i<pid_to.length) {
					userArray[i]=g.getUsername(pid_to[i]);
					i++;
				}
		     UserMessage myM = new UserMessage(id,pid_to,pid_from,userArray,p.getUsername(),body,subject,0, false, 0, original_subject_id,time.toString(),id,false);
			p.addMessage(myM);
			stmt.close();
			i=0;
			stmt = g.con.createStatement("insert into messages (pid_to,pid_from,body,subject,msg_type,original_subject_id,pid,subject_id,id) values (?,?,?,?,?,?,?,?,?);");
			if(pid_to.length==1&&pid_to[0]==p.ID) {
				// no second copy sent, but if you are not a program, your program should know.
				if(!secondaryProg)
				p.getPs().runMethod("onMessageReceivedCatch",p.getPs().b.getMessage(id));
			} else
			while(i<pid_to.length) {
				
				if(pid_to[i]!=-1) {
					
					//stmt.execute("insert into messages (pid_to,pid_from,body,subject,msg_type,original_subject_id,pid,subject_id) values (\""
						//	+ pid_to_s +"\"," + pid_from +",\"" +body+"\",\""+subject+"\","+0+","+original_subject_id+","+pid_to[i]+"," + msgid + ");" );
						
					stmt.setString(1,pid_to_s);
					stmt.setInt(2,pid_from);
					stmt.setString(3,body);
					stmt.setString(4,subject);
					stmt.setInt(5,0);
					if(original_subject_id!=null)
						stmt.setString(6,original_subject_id.toString());
						else stmt.setString(6,"none");
					
					stmt.setInt(7,pid_to[i]);
					stmt.setString(8,id.toString());
					UUID thisMsgID = UUID.randomUUID();
					stmt.setString(9,thisMsgID.toString());

						stmt.execute();
						myM = new UserMessage(thisMsgID,pid_to,pid_from,userArray,p.getUsername(),body,subject,0, false, 0, original_subject_id,time.toString(),id,false);

						Player otherP = g.getPlayer(pid_to[i]);
						otherP.addMessage(myM);
						otherP.getPs().runMethod("onMessageReceivedCatch",otherP.getPs().b.getMessage(thisMsgID));

				}
			i++;
			}
			
			stmt.close();
			transacted=true;
			} catch(MySQLTransactionRollbackException exc) {  }
		
			}
		}	catch (SQLException e) {
		
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Sends a vassal invitation message.
	 * msgtype will be 6 for vassal invitations.
	 * 
	 * @param usernameTo			an array of usernames this message should be sent to.  Invalid usernames will cause this message to be sent to Id.
	 * @param body					the body of the message
	 * @param subject				the subject of the message
	 * @param original_message_id	The original message ID, 0 if none.  Used for chaining messages together.
	 * 
	 * @return True, if the message was sent.  False otherwise.
	 */
	public boolean sendVassalInvitationMessage(String usernameTo[], String body, String subject, UUID original_subject_id) {
		
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		
		int pid_to[] = new int[usernameTo.length];
		int i = 0;
		while(i<pid_to.length) {
			pid_to[i]=g.getPlayerId(usernameTo[i]);
			i++;
		}
		return sendVassalInvitationMessage(pid_to,body,subject,original_subject_id);
	}
	
	/**
	 * Sends a vassal invitation message.
	 * msgtype will be 6 for vassal invitations.
	 * 
	 * @param pid_to				an array of Player IDs this message should be sent to.  Invalid IDs will cause this message to be sent to Id.
	 * @param body					the body of the message
	 * @param subject				the subject of the message
	 * @param original_subject_id	The original message ID, 0 if none.  Used for chaining messages together.
	 * 
	 * @return True, if the message was sent.  False otherwise.
	 */
	public boolean sendVassalInvitationMessage(int pid_to[],String body, String subject, UUID original_subject_id) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		int j = 0;
		// msg_type 5
		while(j<pid_to.length) {
			int k = 0;
			while(k<pid_to.length) {
				if(pid_to[j]==pid_to[k]&&k!=j) {
					setError("No duplicate messages.");
						return false;
				}
				k++;
			}
			j++;
		}
		 j = 0; Player pl;
		 boolean found;
		 ArrayList<Player> players = g.getPlayers();

		while(j<pid_to.length) {
			int k = 0;
			found=false;
			while(k<players.size()) {
				pl = players.get(k);
				if(pid_to[j]==pl.ID) {
					found=true;
					break;
				}
				k++;
			}
			if(!found) pid_to[j]=-1;
			j++;
		}
		
		// original message id can be 0.
		boolean transacted = false;
		int pid_from = p.ID;
		try {
			ResultSet rs;
			String pid_to_s = PlayerScript.toJSONString(pid_to);
			while(!transacted) {
				
				try {
			UberPreparedStatement stmt = g.con.createStatement("insert into messages (pid_to,pid_from,body,subject,msg_type,original_subject_id,pid,id,subject_id) values (?,?,?,?,?,?,?,?,?);");
			int i = 0;
			stmt.setString(1,pid_to_s);
			stmt.setInt(2,pid_from);
			stmt.setString(3,body);
			stmt.setString(4,subject);
			stmt.setInt(5,6);
			if(original_subject_id!=null)
			stmt.setString(6,original_subject_id.toString());
			else
				stmt.setString(7,"none");
			stmt.setInt(7,p.ID);
			 UUID id = UUID.randomUUID();
		      stmt.setString(8,id.toString());
		      stmt.setString(9,id.toString());
		      Date time = new Date();

				String userArray[] = new String[pid_to.length]; i = 0;

				while(i<pid_to.length) {
					userArray[i]=g.getUsername(pid_to[i]);
					i++;
				}
		     UserMessage myM = new UserMessage(id,pid_to,pid_from,userArray,p.getUsername(),body,subject,5, false, 0, original_subject_id,time.toString(),id,false);
			p.addMessage(myM);
			stmt.close();
			
			//stmt.executeUpdate("update messages set subject_id = " + msgid+  " where message_id = " + msgid);
			i=0;
			stmt = g.con.createStatement("insert into messages (pid_to,pid_from,body,subject,msg_type,original_subject_id,pid,subject_id,id) values (?,?,?,?,?,?,?,?,?);");
			while(i<pid_to.length) {
				
				if(pid_to[i]!=-1) {
					
					///	stmt.execute("insert into messages (pid_to,pid_from,body,subject,msg_type,original_subject_id,pid,subject_id) values (\""
				//	+ pid_to_s +"\"," + pid_from +",\"" +body+"\",\""+subject+"\","+5+","+original_subject_id+","+pid_to[i]+"," + msgid + ");" );
					stmt.setString(1,pid_to_s);
					stmt.setInt(2,pid_from);
					stmt.setString(3,body);
					stmt.setString(4,subject);
					stmt.setInt(5,6);
					if(original_subject_id!=null)
						stmt.setString(6,original_subject_id.toString());
						else
							stmt.setString(6,"none");
					stmt.setInt(7,pid_to[i]);
					stmt.setString(8,id.toString());
					 UUID thisMsgID = UUID.randomUUID();
				      stmt.setString(9,thisMsgID.toString());
					stmt.execute();
		//	rs = stmt.executeQuery("select message_id from messages where pid = " + pid_to[i] + " and pid_from =  "+ pid_from + " order by creation_date desc;");
				
			Player otherP = g.getPlayer(pid_to[i]);
		      UserMessage m = new UserMessage(thisMsgID,pid_to,pid_from,userArray,p.getUsername(),body,subject,5, false, 0, original_subject_id,time.toString(),id,false);
		      otherP.addMessage(m);
			otherP.getPs().runMethod("onMessageReceivedCatch",otherP.getPs().b.getMessage(thisMsgID));

				}
			i++;
			}

			stmt.close();
			transacted=true;
			} catch(MySQLTransactionRollbackException exc) {  }
		
			}
		}	catch (SQLException e) {
		
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		}
		return true;
	}
	
	/**
	 * Sends a system message.  System messages aren't sent down with other messages and 
	 * are only viewable through AI's
	 * 
	 * @param pid_to				an array of player IDs this message shoud be sent to,  Invalid IDs will cause this message to be sent to Id.
	 * @param body					the body of the message
	 * @param subject				the subject of the message
	 * @param original_subject_id	The original message ID, 0 if none.  Used for chaining messages together.
	 * @return
	 */
	public boolean sendSystemMessage(int pid_to[],String body, String subject, UUID original_subject_id) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		int j = 0;
		// msg_type 5
		while(j<pid_to.length) {
			int k = 0;
			while(k<pid_to.length) {
				if(pid_to[j]==pid_to[k]&&k!=j) {
					setError("No duplicate messages.");
						return false;
				}
				k++;
			}
			j++;
		}
		 j = 0; Player pl;
		 boolean found;
		 ArrayList<Player> players = g.getPlayers();

		while(j<pid_to.length) {
			int k = 0;
			found=false;
			while(k<players.size()) {
				pl = players.get(k);
				if(pid_to[j]==pl.ID) {
					found=true;
					break;
				}
				k++;
			}
			if(!found) pid_to[j]=-1;
			j++;
		}
		
		// original message id can be 0.
		boolean transacted = false;
		int pid_from = p.ID;
		try {
			ResultSet rs;
			String pid_to_s = PlayerScript.toJSONString(pid_to);
			while(!transacted) {
				
				try {
			UberPreparedStatement stmt = g.con.createStatement("insert into messages (pid_to,pid_from,body,subject,msg_type,original_subject_id,pid,id,subject_id) values (?,?,?,?,?,?,?,?,?);");
			int i = 0;
			stmt.setString(1,pid_to_s);
			stmt.setInt(2,pid_from);
			stmt.setString(3,body);
			stmt.setString(4,subject);
			stmt.setInt(5,5);
			if(original_subject_id!=null)
			stmt.setString(6,original_subject_id.toString());
			else
				stmt.setString(7,"none");
			stmt.setInt(7,p.ID);
			 UUID id = UUID.randomUUID();
		      stmt.setString(8,id.toString());
		      stmt.setString(9,id.toString());
		      Date time = new Date();

				String userArray[] = new String[pid_to.length]; i = 0;

				while(i<pid_to.length) {
					userArray[i]=g.getUsername(pid_to[i]);
					i++;
				}
		     UserMessage myM = new UserMessage(id,pid_to,pid_from,userArray,p.getUsername(),body,subject,5, false, 0, original_subject_id,time.toString(),id,false);
			p.addMessage(myM);
			stmt.close();
			
			//stmt.executeUpdate("update messages set subject_id = " + msgid+  " where message_id = " + msgid);
			i=0;
			stmt = g.con.createStatement("insert into messages (pid_to,pid_from,body,subject,msg_type,original_subject_id,pid,subject_id,id) values (?,?,?,?,?,?,?,?,?);");
			while(i<pid_to.length) {
				
				if(pid_to[i]!=-1) {
					
					///	stmt.execute("insert into messages (pid_to,pid_from,body,subject,msg_type,original_subject_id,pid,subject_id) values (\""
				//	+ pid_to_s +"\"," + pid_from +",\"" +body+"\",\""+subject+"\","+5+","+original_subject_id+","+pid_to[i]+"," + msgid + ");" );
					stmt.setString(1,pid_to_s);
					stmt.setInt(2,pid_from);
					stmt.setString(3,body);
					stmt.setString(4,subject);
					stmt.setInt(5,5);
					if(original_subject_id!=null)
						stmt.setString(6,original_subject_id.toString());
						else
							stmt.setString(6,"none");
					stmt.setInt(7,pid_to[i]);
					stmt.setString(8,id.toString());
					 UUID thisMsgID = UUID.randomUUID();
				      stmt.setString(9,thisMsgID.toString());
					stmt.execute();
		//	rs = stmt.executeQuery("select message_id from messages where pid = " + pid_to[i] + " and pid_from =  "+ pid_from + " order by creation_date desc;");
				
			Player otherP = g.getPlayer(pid_to[i]);
		      UserMessage m = new UserMessage(thisMsgID,pid_to,pid_from,userArray,p.getUsername(),body,subject,5, false, 0, original_subject_id,time.toString(),id,false);
		      otherP.addMessage(m);
			otherP.getPs().runMethod("onMessageReceivedCatch",otherP.getPs().b.getMessage(thisMsgID));

				}
			i++;
			}

			stmt.close();
			transacted=true;
			} catch(MySQLTransactionRollbackException exc) {  }
		
			}
		}	catch (SQLException e) {
		
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		}
		return true;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Sends a league message.  Used for league invitations only.
	 * 
	 * @param usernameTo			the names of the sending players (should be the league player)
	 * @param body					the body of the message
	 * @param subject				the subject of the message
	 * @param msg_type				needs to be either 3 or 4, 3 is invitation, 4 is an acceptance
	 * @param league_pid			the ID of the league player this message is for
	 * @param original_subject_id	The original message ID, 0 if none.  Used for chaining messages together.
	 * 
	 * @return True, if the message was sent.  False otherwise.
	 */
	public boolean sendLeagueMessage(String[] usernameTo,  String body, String subject, int msg_type, int league_pid, UUID original_subject_id) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		int pid_to[] = new int[usernameTo.length];
		int i = 0;
		while(i<pid_to.length) {
			pid_to[i]=g.getPlayerId(usernameTo[i]);
			i++;
		}
		return sendLeagueMessage(pid_to,body,subject,msg_type,league_pid,original_subject_id);
	}

	
	/**
	 * Sends a league message.  Used for league invitations only.
	 * 
	 * @param pid_to				the IDs of the sending players (should be the league player)
	 * @param body					the body of the message
	 * @param subject				the subject of the message
	 * @param msg_type 				needs to be either 3 or 4, 3 is invitation, 4 is an acceptance
	 * @param league_pid			the ID of the league player this league message is for
	 * @param original_subject_id	The original message ID, 0 if none.  Used for chaining messages together.
	 * 
	 * @return True, if the message was sent.  False otherwise.
	 */
	
	public boolean sendLeagueMessage(int[] pid_to, String body, String subject, int msg_type, int league_pid, UUID original_subject_id) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		
		if(body.contains(";")||subject.contains(";")){
			body.replace(";","[semicolon]");
			subject.replace(";","[semicolon]");
		}
		// original message id can be 0.
		
		//wrong message type checker
		
		if(msg_type!=3&&msg_type!=4) {
			setError("Improper msg_types for this message.");
			return false;
		}
	

		//DUPLICATE MESSAGES CHECKER
		int j = 0;
		while(j<pid_to.length) {
			int k = 0;
			while(k<pid_to.length) {
				if(pid_to[j]==pid_to[k]&&k!=j) {
					setError("No duplicate messages.");
						return false;
				} 
				if(pid_to[j]==5) {
					setError("No invites to Id.");
					return false;
				}
				k++;
			}
			j++;
		}
		
		Player pl;  j = 0;
		 boolean found;
		 ArrayList<Player> players = g.getPlayers();

		while(j<pid_to.length) {
			int k = 0;
			found=false;
			while(k<players.size()) {
				pl = players.get(k);
				if(pid_to[j]==pl.ID) {
					found=true;
					break;
				}
				k++;
			}
			if(!found) pid_to[j]=-1;
			j++;
		}

		
		int pid_from = p.ID;
		//INVITATION ACCEPTANCE CHECKER
		if(msg_type==4&&p.getLeague()!=null) {
			// so you can't get away with sending "okay I'm ready for a league" messages
			// without fitting the bill!
			setError("You are already in a league, the " + p.getLeague().getName() + "," + p.getUsername());
			return false;
	
		} else if(msg_type==3&&(!p.isLeague())&&p.getLeague()==null){
			setError("You are not in a league.");
			return false;
		}
		//MSG TYPE 4 ONLY ONE RECIPIENT CHECKER
		if(msg_type==4&&pid_to.length>1) {
			setError("You cannot join multiple leagues.");
			return false;
		}
		// INCORRECT LEAGUE ID CHECKER
		if(msg_type==3&&!p.isLeague()&&p.getLeague().ID!=league_pid){
			setError("Not your league!");
			return false;
			
		}
		
		//CHECK IF CAN CREATE TPR, ESSENTIALLY IF PLAYER EXISTS!

		if(msg_type==3) {
			
			/*
			 * Need to create a tpr.
			 */
			int i = 0; int tids[] = new int[0];
			while(i<pid_to.length) {
				
			if((!p.isLeague()&&!getLeague().createTPR(0,pid_to[i],"invitee",-1,tids))
					||(p.isLeague()&&!createTPR(0,pid_to[i],"invitee",-1,tids))) {
				pid_to[i]=-1; 
			}
				i++;
			}
		} else if(msg_type==4) {
			// LEAGUE EXISTS CHECKER, INVITATION EXISTS CHECKER
			int i = 0; found=false;

			while(i<players.size()) {
				if(players.get(i).ID==league_pid) {
					found = true;
					break;
				}
				i++;
			}
			if(!found) {
				setError("Incorrect league identifier.");
				return false;
			}
			
			League league = (League) players.get(i);
			if(!league.tprExists(p.ID)) {
				setError("No invitation for you exists.");
				return false;
			}
			// Add league in advance.
			 int tids[] = new int[0];
			league.createTPR(0,p.ID,"New Member",0,tids,league.ID);
			p.setLeague(league);
			// now let's create for you a user group and stuff with league.

		//	p.league.updateUserGroups(p.ID,false);
			
			
		//	p.ps.b.createUserGroup(p.league.name,p.league.returnPIDs(p.ID));

		}

		boolean transacted = false;
		try {
			String pid_to_s = PlayerScript.toJSONString(pid_to);
			while(!transacted) {
				
				try {
			//UberStatement stmt = g.con.createStatement();
			//stmt.execute("start transaction;");
			int i = 0;
			 UberPreparedStatement stmt = g.con.createStatement("insert into messages (pid_to,pid_from,body,subject,msg_type,original_subject_id,pid,tsid,readed,id,subject_id) values (?,?,?,?,?,?,?,?,?,?,?)");
				stmt.setString(1,pid_to_s);
				stmt.setInt(2,pid_from);
				stmt.setString(3,body);
				stmt.setString(4,subject);
				stmt.setInt(5,msg_type);
				if(original_subject_id!=null)
				stmt.setString(6,original_subject_id.toString());
				else stmt.setString(6,"none");
				stmt.setInt(7,p.ID);
				stmt.setInt(8,league_pid);
				stmt.setBoolean(9,true);
				 UUID id = UUID.randomUUID();
			      stmt.setString(10,id.toString());
			      stmt.setString(11,id.toString());
				stmt.close();
				Date time = new Date();

				String userArray[] = new String[pid_to.length]; i = 0;

				while(i<pid_to.length) {
					userArray[i]=g.getUsername(pid_to[i]);
					i++;
				}
				UserMessage myM = new UserMessage(id,pid_to,pid_from,userArray,p.getUsername(),body,subject,msg_type, false, league_pid, original_subject_id,time.toString(),id,false);
				p.addMessage(myM);

			stmt = g.con.createStatement("insert into messages (pid_to,pid_from,body,subject,msg_type,original_subject_id,pid,subject_id,tsid,id) values (?,?,?,?,?,?,?,?,?,?);");
			
			i=0;
			while(i<pid_to.length) {
				
				if(pid_to[i]!=-1) {
					
		//	stmt.execute("insert into messages (pid_to,pid_from,body,subject,msg_type,original_subject_id,pid,subject_id,tsid) values (\""
			//		+ pid_to_s +"\"," + pid_from +",\"" +body+"\",\""+subject+"\","+msg_type+","+original_subject_id+","+pid_to[i]+"," + msgid +","+league_pid+ ");" );
			
					stmt.setString(1,pid_to_s);
					stmt.setInt(2,pid_from);
					stmt.setString(3,body);
					stmt.setString(4,subject);
					stmt.setInt(5,msg_type);
					if(original_subject_id!=null)
						stmt.setString(6,original_subject_id.toString());
						else stmt.setString(6,"none");
					stmt.setInt(7,pid_to[i]);
					stmt.setString(8,id.toString());
					stmt.setInt(9,league_pid);
					 UUID thisMsgID = UUID.randomUUID();
				      stmt.setString(10,thisMsgID.toString());
			
			//rs = stmt.executeQuery("select message_id from messages where pid = " + pid_to[i] + " and pid_from =  "+ pid_from + " order by creation_date desc;");
				  //	public UserMessage(UUID messageID,int pidTo[], int pidFrom,String usernameTo[], String usernameFrom, String body, String subject, int msgType, boolean readed, int tsid, UUID originalSubjectID, String creationDate, UUID subjectID,boolean deleted) {

					
					UserMessage m = new UserMessage(thisMsgID,pid_to,pid_from,userArray,p.getUsername(),body,subject,msg_type, false, league_pid, original_subject_id,time.toString(),id,false);
			Player otherP = g.getPlayer(pid_to[i]);
			otherP.addMessage(m);

			otherP.getPs().runMethod("onMessageReceivedCatch",otherP.getPs().b.getMessage(thisMsgID));

				}
			i++;
			}

			stmt.close();

			transacted=true;
			} catch(MySQLTransactionRollbackException exc) {  }
		
			}
		}	catch (SQLException e) {
		
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		}
		return true;
		
	}
	

	
		/**
		 * @deprecated
		 * DEPRECATED: TRADE MESSAGES NO LONGER USED.
		 * 
		 * This is used to send trade invitation messages to players for two-way trades. 
		 * msg_type 0 is for normal messages, msg_type 1 means you're sending a request
		 * for the trade schedule identified by tsid to another guy for him to agree to,
		 * msg_type 2 is when you're replying that you agree to the terms of the trade
		 * schedule represented by tsid and should be sent to the guy who sent you
		 * the msg_type 1 message for that trade schedule.
		 * 
		 * Again, original_subject_id must be 0 if you do not wish to have this be
		 * read by the system as a reply to another message.
		 */

/*	public boolean sendTradeMessage(int pid_to, String body, String subject, int msg_type, UUID tsid, int original_subject_id) {
		if(msg_type!=1&&msg_type!=2) {
			setError("Improper msg_types for this message.");
			return false;
		}
		if(body.contains(";")||subject.contains(";")){
			body.replace(";","[semicolon]");
			subject.replace(";","[semicolon]");
		}
		// original message id can be 0.
		ArrayList<TradeSchedule> tses;
		
		if(msg_type==2) {
			// so you can't get away with sending "okay I'm ready for a ts" messages
			// without fitting the bill!
			boolean twoway=true;
			int i = 0;  boolean foundts=false; Town town;
			TradeSchedule ts=null;
			Player pl;
			 ArrayList<Player> players = g.getPlayers();
			 ArrayList<Town> towns;
			while(i<players.size()) {
				 int j = 0;
				pl = players.get(i);
				towns = pl.towns();
				while(j<towns.size()) {
					town = towns.get(j);
					int k = 0;
					tses = town.tradeSchedules();
					while(k<tses.size()) {
						ts = tses.get(k);
						if(ts.id.equals(tsid)) { foundts=true;  break; }
						k++;
					}
					if(foundts) break;
					j++;
				}
				if(foundts) break;
				i++;
			}
			
			if(!foundts) return false;
			
			// just checking numbers.
			
			i = 0;
			if(!checkMP(ts.getTown2().townID)) return false;
			int t2Slots=0; Building b;
			// do they both have trade centers?
			while(i<ts.getTown2().bldg().size()) {
				b=ts.getTown2().bldg().get(i);
				if(b.getType().equals("Trade Center")) t2Slots+=b.getLvl();
				i++;
			}
		//	System.out.println("t2before " + t2Slots + " and ts size " + ts.town2.tradeSchedules.size() + " and player 2 is " + ts.town2.player.username
			//		+ "player 1 " + ts.town1.player.username + " and ts size on other is " + ts.town1.tradeSchedules.size());
			t2Slots-=ts.getTown2().tradeSchedules().size();
			
		//	System.out.println(t2Slots);
			if(t2Slots<1) return false;
		}
		
		boolean transacted = false;
		int pid_from = p.ID;
		try {
			ResultSet rs;
			while(!transacted) {
				try {
			UberPreparedStatement stmt = g.con.createStatement("insert into messages (pid_to,pid_from,body,subject,msg_type,tsid,original_subject_id,pid) values (?,?,?,?,?,?,?,?);");
			//stmt.execute("insert into messages (pid_to,pid_from,body,subject,msg_type,tsid,original_subject_id,pid) values (\""
				//	+ "["+pid_to+"]" +"\"," + pid_from +",\"" +body+"\",\""+subject+"\","+msg_type+"," + tsid+","+original_subject_id  + "," + p.ID + ");" );
			stmt.setString(1,"["+pid_to+"]");
			stmt.setInt(2,pid_from);
			stmt.setString(3,body);
			stmt.setString(4,subject);
			stmt.setInt(5,msg_type);
			stmt.setInt(6,tsid);
			stmt.setInt(7,original_subject_id);
			stmt.setInt(8,p.ID);
			stmt.close();
			stmt = g.con.createStatement("select message_id from messages where pid = ? order by creation_date desc;");
			stmt.setInt(1,p.ID);
			rs = stmt.executeQuery();
			
			int msgid = 0;
			if(rs.next())
			 msgid = rs.getInt(1);
			
			rs.close();
			stmt.close();
			stmt = g.con.createStatement("update messages set subject_id = ? where message_id = ?");
			stmt.setInt(1,msgid);
			stmt.setInt(2,msgid);
			//stmt.executeUpdate("update messages set subject_id = " + msgid+  " where message_id = " + msgid);
			
			stmt.executeUpdate();
			stmt.close();
			stmt = g.con.createStatement("insert into messages (pid_to,pid_from,body,subject,msg_type,tsid,original_subject_id,pid,subject_id) values (?,?,?,?,?,?,?,?,?);");
		//	stmt.execute("insert into messages (pid_to,pid_from,body,subject,msg_type,tsid,original_subject_id,pid,subject_id) values (\""
			//		+ "["+pid_to+"]\"" +"," + pid_from +",\"" +body+"\",\""+subject+"\","+msg_type+"," + tsid+","+original_subject_id  + "," + pid_to +","+msgid+ ");" );
			stmt.setString(1,"["+pid_to+"]");
			stmt.setInt(2,pid_from);
			stmt.setString(3,body);
			stmt.setString(4,subject);
			stmt.setInt(5,msg_type);
			stmt.setInt(6,tsid);
			stmt.setInt(7,original_subject_id);
			stmt.setInt(8,pid_to);
			stmt.setInt(9,msgid);
			stmt.close();
			stmt = g.con.createStatement("select message_id from messages where pid = ? and pid_from =  ? order by creation_date desc;");
			stmt.setInt(1,pid_to);
			stmt.setInt(2,pid_from);
			rs = stmt.executeQuery();
			int thismsgid = 0;
			if(rs.next())
			 thismsgid = rs.getInt(1);
			
			rs.close();
			Player otherP = g.getPlayer(pid_to);
			otherP.getPs().runMethod("onMessageReceivedCatch",otherP.getPs().b.getMessage(thismsgid));

			stmt.close();
			transacted=true;
			} catch(MySQLTransactionRollbackException exc) {  }
		
			}
		}	catch (SQLException e) {
		
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		}
		return true;
	}*/

		/**
		 * UI Implemented.
		 * <br/><br/>
		 * Get a trade with the stock market AI, exchange rates will be calculated automatically
		 * just before the trade is sent!
		 * 
		 * @param tid1		the ID of the town you're sending from
		 * @param m			the amount of metal you're trading with
		 * @param t			the amount of timber you're trading with
		 * @param mm		the amount of crystal you're trading with
		 * @param f			the amoung of food you're trading with
		 * @param whichres	a number representing the type of resource you want in return. 
		 * 					0 -> metal, 1 -> timber, 2 -> crystal, 3 -> food
		 * 
		 * @return True, if the trade was set up.  False otherwise.
		 * 
		 * @see UserTradeSchedule
		 * @see UserTrade
		 */
	public boolean setUpStockMarketTrade(int tid1, int m, int t, int mm, int f, int whichresource) {
		if(prog&&!p.isSmAPI()) {
			setError("You do not have the Stock Market API!");
			return false;
		}
	// first find the cities.
		if(!checkMP(tid1)) return false;
		
		if(m==0&&t==0&&mm==0&&f==0) {
			setError("You need to send resources in a trade!");
			return false;
		}
		
		int i = 0; Town t1=g.findTown(tid1);
		
		if(t1.getPlayer().ID!=p.ID) {
			setError("This is not your town!");
			return false;
		}
		// okay now we have towns.
		
		//make sure we're not blockaded
		if(t1.getBlockades().size()>0) {
			setError("Town blockaded.");
			return false;
		}
		
		// just checking numbers.
		
		i = 0;
		int t1Slots=0;
		
	/*	try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select sum(lvl) from bldg where tid = " + tid1 + " and name = 'Trade Center';");
			if(rs.next()) t1Slots=rs.getInt(1);
			
			rs.close();
			rs = stmt.executeQuery("select count(*) from tradeschedule where tid1 = " + tid1 + " and finished = false;");
			if(rs.next()) t1Slots -= rs.getInt(1);
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); } */
		
		UserBuilding b[] = getUserBuildings(tid1,"Trade Center");
		
		while(i<b.length) {
			t1Slots+=b[i].getLvl();
			i++;
		}
		
		i = 0;
		ArrayList<TradeSchedule> tses = t1.tradeSchedules();
		while(i<tses.size()) {
			if(!tses.get(i).isFinished()||tses.get(i).isCaravan()) t1Slots-=1;
			i++;
		}
		
		if(t1Slots<1) return false;
		
		// We don't check city two because they need to agree to it first or if
		// it's a one way-er then it doesn't really matter so much!
		//	public boolean sendMessage(int pid_to, int pid_from, String body, String subject, int msg_type) {
		int toIndex = 0;
		if(m>0) toIndex=0;
		else if(t>1) toIndex=1;
		else if(mm>2) toIndex=2;
		else if(f>3) toIndex=3;
		
		long resource=0;
		if(m>0) resource=m;
		else if(t>0) resource=t;
		else if(mm>0) resource=mm;
		else if(f>0) resource=f;
		if(g.Trader==null||!g.Trader.setup) return false;
		long otherm=0,othert=0,othermm=0,otherf=0;
		switch(whichresource) {
		case 0:
			otherm=g.Trader.getExchangeResource(toIndex,whichresource,resource,1,t1.getX(),t1.getY());
			break;
		case 1:
			othert=g.Trader.getExchangeResource(toIndex,whichresource,resource,1,t1.getX(),t1.getY());
			
			break;
		case 2:
			othermm=g.Trader.getExchangeResource(toIndex,whichresource,resource,1,t1.getX(),t1.getY());

			break;
		case 3:
			otherf=g.Trader.getExchangeResource(toIndex,whichresource,resource,1,t1.getX(),t1.getY());

			break;
		default: return false;
		}
		TradeSchedule ts = new TradeSchedule(t1,  t1,  m,  t,  mm,  f, otherm,othert,othermm,otherf,  1, 1,  false,null,false);
		
		
		return true;
		
	}
	
	
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Sets up an open two way trade schedule. intervaltime is in seconds and
	 * is how long the system will wait before making the next trade, timesToDo is how many
	 * times you wish to have the trade executed, and tid1 is your town ID.
	 * 
	 * @param tid1			the ID of your town
	 * @param m				the amount of metal you're offering
	 * @param t				the amount of timber you're offering
	 * @param mm			the amount of crystal you're offering
	 * @param f				the amount of food you're offering
	 * @param otherm		the amount of metal you want in return
	 * @param othert		the amount of timber you want in return
	 * @param othermm		the amount of crystal you want in return
	 * @param otherf		the amount of food you want in return
	 * @param intervaltime	the amount of time, in seconds, between each trade
	 * @param timesToDo		the number of times this trade should be repeated
	 * 
	 * @return True, if the Trade Schedule was set up.  False otherwise.
	 */
	public boolean setUpTradeSchedule(int tid1, int m, int t, int mm, int f, long otherm,long othert, long othermm, long otherf, int intervaltime,int timesToDo) {
	
		if(prog&&!p.isTradingAPI()) {
			setError("You do not have the Trading API!");
			return false;
		}
		if(!checkMP(tid1)) return false;
		if((m==0&&t==0&&mm==0&&f==0)||(otherm==0&&othert==0&&othermm==0&&otherf==0)) {
			setError("You need to send resources in a trade!");
			return false;
		}
		// first find the cities.
		boolean twoway=true;
		int i = 0; /*Player pl;*/ Town t1=g.findTown(tid1); //Town t2=g.findTown(tid2);
		
		
		if(t1.getPlayer().ID!=p.ID) {
			setError("This is not your town!");
			return false;
		}
		
		//if(t2.getPlayer()==null) return false; // check against bad towns.
		// okay now we have towns.
		
		if(intervaltime<1||(timesToDo<1&&timesToDo!=-1)) {
			// so if timesToDo!=-1 and is less than one, is an illegal time.
			setError("Illegal interval time or timesToDo amount.");
			return false;
		}

		// just checking numbers.
		
		i = 0;
		int t1Slots=0;
		// do they both have trade centers?
	/*
		try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select sum(lvl) from bldg where tid = " + tid1 + " and name = 'Trade Center';");
			if(rs.next()) t1Slots=rs.getInt(1);
			
			rs.close();
			rs = stmt.executeQuery("select count(*) from tradeschedule where tid1 = " + tid1 + " and finished = false;");
			if(rs.next()) t1Slots -= rs.getInt(1);
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); } */
		boolean keep=false;
		if(prog) keep=true;
		prog=false;
		UserBuilding b[] = getUserBuildings(tid1,"Trade Center");
		if(keep) prog = true;
		while(i<b.length) {
			t1Slots+=b[i].getLvl();
			i++;
		}
		
		i = 0;
		ArrayList<TradeSchedule> tses = t1.tradeSchedules();
		while(i<tses.size()) {
			if(!tses.get(i).isFinished()||tses.get(i).isCaravan()) t1Slots-=1;
			i++;
		}
		
		if(t1Slots<1) return false;
		
		// We don't check city two because they need to agree to it first or if
		// it's a one way-er then it doesn't really matter so much!
		//	public boolean sendMessage(int pid_to, int pid_from, String body, String subject, int msg_type) {
		int toSend[] = new int[1];
		TradeSchedule ts = new TradeSchedule(t1,  null,  m,  t,  mm,  f,  otherm, othert,  othermm,  otherf,  intervaltime, timesToDo,  twoway,null,false);

	/*	 sendTradeMessage(t2.getPlayer().ID,"Do you accept this trading schedule request from " + t1.getPlayer().getUsername() + "" +
				" of " + m + " metal " + t + " timber " + mm + " man. mat. " + f + " food in exchange for "
				+ otherm + " metal " + othert + " timber " + othermm + " man. mat. " + otherf + " that will be completed " + 
				timesToDo + " times with an interval of " + GodGenerator.secondsToString(intervaltime) + "?", "Trading Schedule Request", 1,ts.tradeScheduleID,0);
		*/
		
		
		return true;
		
		
	}
	
	/**
	 * UI Implemented.
	 * <br/><br/>
	 *  Set up a one-way resource delivery trade schedule. intervaltime is in seconds and
	 * is how long the system will wait before making the next trad, timesToDo is how many
	 * times you wish to have the trade executed, and tid1 and tid2 is your town ID and the player
	 * you wish to trade with's town coordinates, respectively.
	 * 	
	 * @param tid1			the ID of your town
	 * @param x				the x coordinate of the target town
	 * @param y				the y coordinate of the target town
	 * @param m				the amount of metal to send
	 * @param t				the amount of timber to send
	 * @param mm			the amount of crystla to send
	 * @param f				the amount of food to send
	 * @param intervaltime	the amount of time, in seconds, between each trade
	 * @param timesToDo		the number of times this trade should be performed
	 * 
	 * @return True, if the Trade Schedule was set up.  False otherwise.
	 * 
	 * @see UserTradeSchedule
	 * @see USerTrade
	 */
	public boolean setUpTradeSchedule(int tid1, int x, int y, int m, int t, int mm, int f,int intervaltime,int timesToDo) {
		if(prog&&!p.isTradingAPI()) {
			setError("You do not have the Trading API!");
			return false;
		}
		Town town = g.findTown(x,y);
		
		if(town.townID==0) {
			setError("Invalid coordinates!");
			return false;
		}
		
		return setUpTradeSchedule(tid1,town.townID,m,t,mm,f,intervaltime,timesToDo);
	}
		
	/**
	 * Set up a one-way resource delivery trade schedule. intervaltime is in seconds and
	 * is how long the system will wait before making the next trad, timesToDo is how many
	 * times you wish to have the trade executed, and tid1 and tid2 is your town ID and the player
	 * you wish to trade with's town ID, respectively.
	 * 
	 * @param tid1			the ID of your town
	 * @param tid2			the ID of the target town
	 * @param m				the amount of metal to send
	 * @param t				the amount of timber to send
	 * @param mm			the amount of crystal to send
	 * @param f				the amount of food to send
	 * @param intervaltime	the amount of time between each trade, in seconds
	 * @param timesToDo		the number of times to perform this trade
	 * 
	 * @return True, if the Trade Schedule is set up.  False otherwise.
	 * 
	 * @see UserTradeSchedule
	 * @see UserTrade
	 */
	public boolean setUpTradeSchedule(int tid1, int tid2, int m, int t, int mm, int f,int intervaltime,int timesToDo) {
		if(prog&&!p.isTradingAPI()) {
			setError("You do not have the Trading API!");
			return false;
		}
		
		if(!checkMP(tid1)) return false;
		if(m==0&&t==0&&mm==0&&f==0) {
			setError("You need to send resources in a trade!");
			return false;
		}
		// first find the cities.
		boolean twoway=false;
		int i = 0; Town t1=g.findTown(tid1); Town t2=g.findTown(tid2); 
	
		if(t1.getPlayer().ID!=p.ID) {
			setError("This is not your town!");
			return false;
		}
		if(t2.getPlayer()==null) {
			setError("Invalid target.");
			return false;
		}
		
		// okay now we have towns.
		
		if(intervaltime<1||(timesToDo<1&&timesToDo!=-1)) {
			// so if timesToDo!=-1 and is less than one, is an illegal time.
			setError("Illegal interval time or timesToDo amount.");
			return false;
		}

		//make sure neather town is blockaded by a hostile force
		if(t1.getBlockades().size()>0) {
			for(Raid r : t1.getBlockades()) {
				//first we check if the target player is allied to the blockading player
				if(!r.getTown1().getPlayer().isAllied(t2.getPlayer())) {
					setError("Your town is blockaded by a hostile force.");
					return false;
				}
			}
		}
		if(t2.getBlockades().size()>0) {
			for(Raid r : t1.getBlockades()) {
				//then we check if the the target player is blockaded by an ally of the origin player
				if(!r.getTown1().getPlayer().isAllied(p)) {
					setError("Target town is blockaded by a hostile force.");
					return false;
				}
			}
		}
		
		// just checking numbers.
		
		i = 0;
		int t1Slots=0;
		// do they both have trade centers?
		boolean keep=!!prog;
		prog=false;
		UserBuilding b[] = getUserBuildings(tid1,"Trade Center");
		prog=!!keep;
		while(i<b.length) {
			t1Slots+=b[i].getLvl();
			i++;
		}
		
		i = 0;
		ArrayList<TradeSchedule> tses = t1.tradeSchedules();
		while(i<tses.size()) {
			if(!tses.get(i).isFinished()||tses.get(i).isCaravan()) t1Slots-=1;
			i++;
		}
		if(t1Slots<1) return false;
		
		// We don't check city two because they need to agree to it first or if
		// it's a one way-er then it doesn't really matter so much!
		//	public boolean sendMessage(int pid_to, int pid_from, String body, String subject, int msg_type) {


		@SuppressWarnings("unused")
		TradeSchedule ts = new TradeSchedule(t1,  t2,  m,  t,  mm,  f,  0, 0,  0,  0,  intervaltime, timesToDo,  twoway,null,false);
	
		return true;
		
		
	}
	/**
	 * UI Implemented
	 * <br/><br/>
	 * Returns how many traders are required to carry a specific resource from a specific town.
	 * 
	 * @param resource	the amount of resources to carry
	 * @param tid		the ID of the town you're sending from
	 * 
	 * @return the number of traders needed to carry resource resources.
	 */
	public int howManyTraders(long resource, int tid) {
		// CONNECTED TO GOD'S SENDTRADEIFPOSSIBLE!
		if(prog&&!p.isAdvancedTradingAPI()) {
			setError("You do not have the Advanced Trading API!");
			return -1;
		}
		 Town town1=g.findTown(tid); 
		
		if(town1.getPlayer().ID!=p.ID) {
	
			setError("Invalid tid!");
			return -1;
		}
		long f = (long)((double) (1  + p.God.Maelstrom.getTraderEffect(town1.getX(),town1.getY()))*GodGenerator.traderCarryAmount);
		long r = resource;
		int t1Required = (int) Math.ceil((-1+Math.sqrt(1+8*r/f))/2);
		if(t1Required==0) t1Required=1;
		
		return t1Required;
	
	}
		
	/*public boolean createUnitTemplate(String unitName, int tierNumber, int concealment,int armor, int cargo, int speed, int weaponsArray[], int graphicNum, boolean id) {
		if(p.ID==5||p.isQuest()) return createUnitTemplate(unitName,tierNumber,concealment,armor,cargo,speed,weaponsArray,graphicNum);
		else return false;
 	}
	private boolean createUnitTemplate(String unitName, int tierNumber, int concealment,int armor, int cargo, int speed, int weaponsArray[], int graphicNum) {

		
		
		// Right, so, making this shit work.

		// okay now make the unit template!
		if(canCreateUnitTemplate(unitName,tierNumber,concealment,armor,cargo,speed,weaponsArray,graphicNum)) {
		ArrayList<AttackUnit> au=p.getAUTemplates();
		boolean found=false;
		AttackUnit a=null;
		int i = 0; 
		while(i<au.size()) {
			a = au.get(i);
			if(a.getName().equals(unitName)) { found=true;break;}
			i++;
		}	

		
		// Same DB entry gets used either way...so player sees on display screen the same stuff,
		// only backend moves entry to the end of the queue by creating a new, slightly different copy
		// if there is an older one that existed before that they are trying to edit!
		// editMe does this by not deleting from db, just removing the object.
		// Also now there are no updates of templates unless there are changes...cool new technology, eh!?
		if(!found) { // so if it got editMe on then it didn't just fall out of the loop, we found an identical unit!
			
					switch(tierNumber) {
					case 1:
						p.setMemAUTemplate(new AttackUnit(unitName,concealment,armor,cargo,speed,-1,1,weaponsArray,graphicNum),0);
					break;
					case 2:
						p.setMemAUTemplate(new AttackUnit(unitName,concealment,armor,cargo,speed,-1,5,weaponsArray,graphicNum),0);

						break;
					case 3:
						p.setMemAUTemplate(new AttackUnit(unitName,concealment,armor,cargo,speed,-1,10,weaponsArray,graphicNum),0);

						break;
					case 4:
						p.setMemAUTemplate(new AttackUnit(unitName,concealment,armor,cargo,speed,-1,20,weaponsArray,graphicNum),0);
						
						break;
					
					}
					synchronized(p.getAUTemplates()) {
					p.setAUTemplates(p.getMemAUTemplates());
					}
		} else {
			switch(tierNumber) {
			case 1:
				p.setMemAUTemplate(new AttackUnit(unitName,concealment,armor,cargo,speed,-1,1,weaponsArray,graphicNum),2);
			break;
			case 2:
				p.setMemAUTemplate(new AttackUnit(unitName,concealment,armor,cargo,speed,-1,5,weaponsArray,graphicNum),2);
				
				break;
			case 3:
				p.setMemAUTemplate(new AttackUnit(unitName,concealment,armor,cargo,speed,-1,10,weaponsArray,graphicNum),2);
			
				break;
			case 4:
				p.setMemAUTemplate(new AttackUnit(unitName,concealment,armor,cargo,speed,-1,20,weaponsArray,graphicNum),2);
				
				break;
			
			}		
			synchronized(p.getAUTemplates()) {
				p.setAUTemplates(p.getMemAUTemplates());
				}	
		}
		
		}
				
		notifyViewer();
			return true;
		
		
	}
	
	public boolean canCreateUnitTemplate(String unitName, int tierNumber, int concealment,int armor, int cargo, int speed, int weaponsArray[], int graphicNum, boolean id) {
		if(p.ID==5||p.isQuest()) return canCreateUnitTemplate(unitName,tierNumber,concealment,armor,cargo,speed,weaponsArray,graphicNum);
		else return false;
 	}
	
	private boolean canCreateUnitTemplate(String unitName, int tierNumber, int concealment,int armor, int cargo, int speed, int weaponsArray[], int graphicNum) {


		if(unitName.length()==0) {
			setError("Must give this template a name!");
			return false;
		}
		if(unitName.contains("'")) {
			setError("Error. Nobody loves you. (Don't use apostrophes.)");
			return false;
		}
		if(concealment==0||armor==0||speed==0||cargo==0) {
			setError("Cannot have a zeroed out attribute.");
			return false;
		}
		if(tierNumber>4||tierNumber<1) {
			setError("Illegal tier number.");
			return false; // just in case they use the wrong tier.
		}
		// now we need to check towns and stuff everywhere in that unit slot.
		
		//ArrayList<Town> towns = p.towns();

		if(unitName.equals("Engineer")||unitName.equals("Scholar")||unitName.equals("Trader")||unitName.equals("Messenger")||unitName.equals("empty")||unitName.equals("locked")) {
			setError("Illegal unit name.");
			return false;
		}
		// no naming after civvies.
		// if it makes it through these horrible, outrageous tests...then it is ready for more.
		
		int totalPoints =0;
		int maxSlots = 0; int maxSlotsForError=0;
		int i = 0;
		boolean ret = false;
		switch(tierNumber) {
		case 1:
			if(graphicNum>9&&graphicNum<0) ret=true;
			 i = 0;
			while(i<weaponsArray.length)  {
				if(weaponsArray[i]>=18) {
					setError("Cannot equip bombs to Soldiers.");
					return false;
				}
				i++;
			}
			totalPoints = 400; maxSlots = 2;maxSlotsForError=2;
			break;
		case 2:
			if(graphicNum>9&&graphicNum<0)ret=true;

			totalPoints = 800; maxSlots = 4;maxSlotsForError=4;
			 i = 0;
			while(i<weaponsArray.length)  {
				if(weaponsArray[i]>=18) {
					setError("Cannot equip bombs to Tanks.");
					return false;
				}
				i++;
			}
			break;
		case 3:
			if(graphicNum>9&&graphicNum<0) ret=true;

			totalPoints = 1600; maxSlots = 8;maxSlotsForError=8;
			 i = 0;
			while(i<weaponsArray.length)  {
				if(weaponsArray[i]>=18) {
					setError("Cannot equip bombs to Juggernaughts.");
					return false;
				}
				i++;
			}
			break;
		case 4:
			if(graphicNum>4&&graphicNum<0) ret=true;
			 i = 0;
				while(i<weaponsArray.length)  {
					if(weaponsArray[i]<18) {
						setError("Can only equip bombs to Bombers.");
						return false;
					}
					i++;
				}
			totalPoints = 200; maxSlots = 1;maxSlotsForError=1;
			break;
			
		default:
			setError("Illegal tier choice. Please choose between 1 and 4 inclusive.");
			return false;
			
		}
		if(ret) {
			setError("Illegal graphic number.");
			return false;
		}
		
		int weaponsContribution = 0;
		
		 i = 0;
		// if it gets here now we need to check that the weapons choices are existing.
		while(i<weaponsArray.length) {
			if(weaponsArray[i] < 6 || (weaponsArray[i] >=18 &&weaponsArray[i]<=20)) { weaponsContribution+=100; maxSlots--; }
			if(weaponsArray[i] >= 6 && weaponsArray[i] < 12) { weaponsContribution+=200; maxSlots-=2; }
			if(weaponsArray[i] >= 12 && weaponsArray[i] < 18) { weaponsContribution+=400; maxSlots-=4; }
			if(weaponsArray[i]>20){
				setError("Illegal weapon choice.");
				return false;
			}
			i++;
		}
		
		
		if(concealment+armor+cargo+speed+weaponsContribution!=totalPoints) {
			setError("Invalid point distribution. Must be exactly " + totalPoints + ".");
			return false;
		}		
		// right so we know the weapons exist as they need to. We know the total points add up. We know the slot has no units, and unit type is researched.
		if(maxSlots<0){
			setError("Too many points spent on weapons for this unit type.");
			return false; // means they overused the maximum number of slots even though they had the points.
		}
		// Well, hell, let's just change'er up then, and at the same time check to make sure it has the right slot tech.
	
		return true;
		
	}*/

	private boolean checkLP() {
		// for leagues only really. We use this to check if you're an admin,
		// so moderators can't pull off admin actions.
		if(!p.isLeague()) return true; // If a normal player, always return true.
		if(!admin) {
			if(((League) p).canMakeAdminChanges(pid)) {
				admin = true;
				return true; // Clearly admin needs to be updated
				// for a new tpr!
			}

			setError("You do not have the correct permissions for this!");
			return false;
		}
		else return true;
	}
	private boolean checkLPWOE() {
		// for leagues only really. We use this to check if you're an admin,
		// so moderators can't pull off admin actions.
		if(!p.isLeague()) return true; // If a normal player, always return true.
		if(!admin) {
			if(((League) p).canMakeAdminChanges(pid)) {
				admin = true;
				return true; // Clearly admin needs to be updated
				// for a new tpr!
			}

			return false;
		}
		else return true;
	}
	private boolean checkMP(int tid) {
		// for leagues only really. We use this to check if you're have mod privvies of
		// the town you're about to control,
		// so moderators can't pull off admin actions.
		if(checkLP()){

			return true;
		}
		if(!p.isLeague()) {

			return true; // If a normal player, always return true.
		}
		if(!((League) p).canMakeModChangesToTown(pid,tid)) {
			setError("You do not have the correct permissions for this!");
			return false;
		}
		else {

			return true;
		}
	}
	private boolean checkMPWOE(int tid) {
		// Check to see if you hae MP privvies without setting an error if false.
		if(checkLPWOE()) return true;
		if(!p.isLeague()) return true; // If a normal player, always return true.
		if(!((League) p).canMakeModChangesToTown(pid,tid)) {
			return false;
		}
		else return true;
	}private boolean checkMP() {
		// for leagues only really. We use this to check if you're have mod privvies of
		// the town you're about to control,
		// so moderators can't pull off admin actions.
		if(checkLP()) {
			return true;
		}
		if(!((League) p).canMakeModChanges(pid)) {

			setError("You do not have the correct permissions for this!");
			return false;
		}
		else{

			return true;
		}

	}

		
		/*
	public boolean createCombatUnit(int slotNumber,String unitTemplateName) {
		if(prog&&!p.isAttackAPI()) {
			setError("You do not have the Attack API!");
			return false;
		}
		// SEE CANCREATECOMBATUNIT FOR LEAGUE PERMISSIONS CHECK!
	
		// Right, so, making this shit work.
		// Now you must have the template to make the attack unit.
		String unitName=unitTemplateName;
		if(!canCreateCombatUnit(slotNumber,unitTemplateName)) {
			
			return false;
		}
		
		AttackUnit a=null; int i=0; boolean found = false;
		while(i<p.getAUTemplates().size()) {
			a=p.getAUTemplates().get(i);
			if(a.getName().equals(unitName)) {found=true;break; }
			i++;
		}
		ArrayList<Town> towns = p.towns();

		
		
		if(!found&&!unitTemplateName.equals("empty")) return false; 
		
		if(unitTemplateName.equals("empty")) {
			int weap[] = new int[0];
			
			a = new AttackUnit("empty",0,0,0,0,slotNumber,0,weap,0);
		}
		// no null a's will get past point above.
		
		int graphicNum=a.getGraphicNum();
		int concealment=(int)a.getConcealment();
		int armor=(int)a.getArmor();
		int speed=(int)a.getSpeed();
		int cargo=(int)a.getCargo();
		int[] weaponsArray=a.getWeap();
	
		int tierNumber=5;
		switch(a.getPopSize()) {
		case 1:
			tierNumber=1;
			break;
		case 5:			
			tierNumber=2;

			break;
		case 10:
			tierNumber=3;

			break;
		case 20:
			tierNumber=4;
			break;
		}
		Town t;
		ArrayList<AttackUnit> au;
		AttackUnit holdA;
		i=0;
		au = p.getAu();
		AttackUnit ta;
		while(i<au.size()) {
			holdA=au.get(i);
		//	error+=holdA.getSlot()+" " + slotNumber + "," + holdA.getSize() +":";
			 if(holdA.getSlot()==slotNumber&&holdA.getSize()==0) {
				 // this means this is the right au.
					switch(tierNumber) {
					case 1:
						// error+=tierNumber + " is my tier, bitch.";
						p.setAu(new AttackUnit(unitName,concealment,armor,cargo,speed,slotNumber,1,weaponsArray,graphicNum));
						
					break;
					case 2:
						p.setAu(new AttackUnit(unitName,concealment,armor,cargo,speed,slotNumber,5,weaponsArray,graphicNum));
						break;
					case 3:
						p.setAu(new AttackUnit(unitName,concealment,armor,cargo,speed,slotNumber,10,weaponsArray,graphicNum));
						break;
					case 4:
						p.setAu(new AttackUnit(unitName,concealment,armor,cargo,speed,slotNumber,20,weaponsArray,graphicNum));
						break;
					case 5:
						if(unitTemplateName.equals("empty")) {
							p.setAu(new AttackUnit(unitName,concealment,armor,cargo,speed,slotNumber,0,weaponsArray,graphicNum));

						}
						break;
					}
					
					int k = 0;
					while(k<p.towns().size()) { // reset our units too
						ta = p.getAu().get(i).returnCopy();
						ta.setSize(p.towns().get(k).getAu().get(i).getSize());
						p.towns().get(k).getAu().set(i,ta);
						k++;
					}
					
					break; }
			 
			 

			i++;
		}
		/*i=0;
		while(i<towns.size()) {
				 t = towns.get(i);
				 au = t.getAu();
				int k = 0;
				while(k<au.size()) {
				 holdA = au.get(k);
				 if(holdA.getSlot()==slotNumber&&holdA.getSize()==0) { // this means this is the right au.
					// we need to change it into something else.
					 
					switch(tierNumber) {
					case 1:
						holdA.resetUnit(unitName,concealment,armor,cargo,speed,slotNumber,1,weaponsArray,graphicNum);
					break;
					case 2:
						holdA.resetUnit(unitName,concealment,armor,cargo,speed,slotNumber,5,weaponsArray,graphicNum);
						break;
					case 3:
						holdA.resetUnit(unitName,concealment,armor,cargo,speed,slotNumber,10,weaponsArray,graphicNum);
						break;
					case 4:
						holdA.resetUnit(unitName,concealment,armor,cargo,speed,slotNumber,20,weaponsArray,graphicNum);
						break;
					case 5:
						if(unitTemplateName.equals("empty")) {
							holdA.resetUnit(unitName,concealment,armor,cargo,speed,slotNumber,0,weaponsArray,graphicNum);

						}
					break;
					
					}
					break; }				
				k++;
			}
				i++;
		}

			return true;
				
	}
	
	
		public boolean canCreateCombatUnit( int slotNumber,String unitTemplateName) {
			if(!checkLP()) return false;
			if(prog&&!p.isAttackAPI()) {
				setError("You do not have the Attack API!");
				return false;
			}
			String unitName = unitTemplateName;
			int i = 0;AttackUnit a=null; boolean found=false;
			while(i<p.getAUTemplates().size()) {
				a=p.getAUTemplates().get(i);
				if(a.getName().equals(unitName)) {found=true;break; }
				i++;
			}
			
			
			if(!found&&!unitTemplateName.equals("empty")) {
				setError("No template by that name.");
				return false;
			}
			ArrayList<Town> towns = p.towns();
			i = 0;Town t; Building b; QueueItem q;
			ArrayList<Building> bldg;
			ArrayList<QueueItem> queue;
			while(i<towns.size()) {
				int j =0;
				t = towns.get(i);
				bldg = t.bldg();
				while(j<bldg.size()) {
					b = bldg.get(j);
					int k = 0;
					queue = b.Queue();
					while(k<queue.size()) {
						q = queue.get(k);
						if(q.getAUtoBuild()==slotNumber) {
							setError("You cannot clear a unit that has queues building!");
							return false;
						}
						k++;
					}
					j++;
				}
					i++;
			}
			if(unitTemplateName.equals("empty")) {
				 i = 0;
					while(i<p.getAu().size()) {
						if(p.getAu().get(i).getSlot()==slotNumber) {
							int size = g.getTotalSize(p.getAu().get(i),p);
							if(size>0) {
							setError("Cannot empty a slot that still has men existing!");
							// we replicate logic from down there up here since we don't want to do that other shit.
							return false;
							}
						}
						i++;
					}
					
					return true; // We stop processing here because the empty unit does not exist!
			}
			int concealment=(int)a.getConcealment();
			int armor=(int)a.getArmor();
			int speed=(int)a.getSpeed();
			int cargo=(int)a.getCargo();
			int[] weaponsArray=a.getWeap();
		
			int tierNumber=5;
			switch(a.getPopSize()) {
			case 1:
				tierNumber=1;
				break;
			case 5:			
				tierNumber=2;
		
				break;
			case 10:
				tierNumber=3;
		
				break;
			case 20:
				tierNumber=4;
				break;
			}
			if(tierNumber==1&&p.getUnitTech(1)==false) {
				setError("You do not possess this unit tech!");
				return false;
			}
			else if(tierNumber==2&&p.getUnitTech(2)==false){
				setError("You do not possess this unit tech!");
				return false;
			}
			else if(tierNumber==3&&p.getUnitTech(3)==false){
				setError("You do not possess this unit tech!");
				return false; // in case the tech isn't there.
			}
			else if(tierNumber==4&&p.getUnitTech(4)==false) {
				setError("You do not possess this unit tech!");

				return false;
			}
			else if(tierNumber>4) {
				setError("Bad tier number!");
				return false; // just in case they use the wrong tier.
			}
			// now we need to check towns and stuff everywhere in that unit slot.
			if(p.returnUnitLots()<slotNumber) {
				setError("You do not have enough attack unit lot tech to load this slot.");
				return false;
			}
			// Okay so we have the slot. Are all the units in that slot done yet?
			
			if(unitName.equals("Engineer")||unitName.equals("Scholar")||unitName.equals("Trader")||unitName.equals("Messenger")||unitName.equals("empty")||unitName.equals("locked")) {
				setError("You may not name your units after civilians or the special keyword locked.");
				return false;
			}
			 i = 0;
			while(i<p.getAu().size()) {
				if(p.getAu().get(i).getName().equals(unitName)){
					setError("You cannot have two units by the same name, as they are identified in this manner.");
					return false; // no double naming.
				}
				i++;
			}

			// no naming after civvies.
			if(slotNumber>5) return false; // six units is the limit anybody can mold, more than that,
			// you're bridging into support aus and the like!
			// I know it's unlikely they'll buy more lots than 6 but what if there
			// is a bug or some sort I don't know? Extra precautions!
		
			
			// if it makes it through these horrible, outrageous tests...then it is ready for more.
			int graphicNum=a.getGraphicNum();
		
			int totalPoints =0;
			int maxSlots = 0;
			boolean graphicTech[] = null;
			switch(tierNumber) {
			case 1:
				graphicTech=p.getSoldierPicTech();
				if(graphicNum>9||graphicNum<0) {
					setError("Illegal graphic number");
					return false;
				}
		
				totalPoints = 400; maxSlots = 2;
				break;
			case 2:
				graphicTech=p.getTankPicTech();
				if(graphicNum>9||graphicNum<0){
					setError("Illegal graphic number");

					return false;
				}
		
				totalPoints = 800; maxSlots = 4;
				break;
			case 3:
				graphicTech=p.getJuggerPicTech();
				if(graphicNum>9||graphicNum<0){
					setError("Illegal graphic number");
					return false;
				}
		
				totalPoints = 1600; maxSlots = 8;
				break;
			case 4:
				graphicTech=p.getBomberPicTech();
				if(graphicNum>4||graphicNum<0) {
					setError("Illegal graphic number");

					return false;
				}
		
				totalPoints = 200; maxSlots = 1;
				break;
				
			}
			if(!graphicTech[graphicNum]){
				setError("You do not possess this graphic technology.");
				return false; // can't use a bad graphic!
			}
			int weaponsContribution = 0;
			
			i = 0;

			boolean[] weap = p.getWeapTech();

			// if it gets here now we need to check that the weapons choices are existing.
			while(i<weaponsArray.length) {
				if(weaponsArray[i] < 6 || (weaponsArray[i] >=18 &&weaponsArray[i]<=20)) { weaponsContribution+=100; maxSlots--; }
				if(weaponsArray[i] >= 6 && weaponsArray[i] < 12) { weaponsContribution+=200; maxSlots-=2; }
				if(weaponsArray[i] >= 12 && weaponsArray[i] < 18) { weaponsContribution+=400; maxSlots-=4; }
				if(weaponsArray[i]>20){
					setError("Illegal weapon number.");
					return false;
				}
		
		
				if(!weap[weaponsArray[i]]) {
					setError("You do not possess this weapon technology.");

					return false; // So weaponsArray contains the number of the weapon from the tech list, so
				}
				// putting that into weap gives the true/false.
				i++;
			}
			

			
			if(concealment+armor+cargo+speed+weaponsContribution!=totalPoints) {
				setError("All of your attributes need to add up to " + totalPoints);
				return false;
			}
			// right so we know the weapons exist as they need to. We know the total points add up. We know the slot has no units, and unit type is researched.
			if(maxSlots<0) return false; // means they overused the maximum number of slots even though they had the points.
			// Well, hell, let's just change'er up then, and at the same time check to make sure it has the right slot tech.
			
			
			/*
			try {
				 UberStatement stmt = g.con.createStatement();
				 ResultSet rs = stmt.executeQuery("select sum(au" + (slotNumber+1) + ") from town where pid = " + p.ID + ";");
				 int size=0;
				 if(rs.next()) size = rs.getInt(1);
				 
				 rs.close();
				 stmt.close();
				 if(size>0) {
					 setError("You cannot replace this unit type while there are still men of that type!");
					 return false;		
				}
			 } catch(SQLException exc) { exc.printStackTrace(); }
			int k = 0; 
			ArrayList<AttackUnit> au;
			while(k<towns.size()) {
				t = towns.get(k);
				au = t.getAu();
				int j = 0;
				while(j<au.size()) {
					if(au.get(j).getSlot()==slotNumber&&au.get(j).getSize()>0) {
						setError("You cannot replace this unit type while there are still men of that type!");
						 return false;	
					}
					j++;
				}
				k++;
			}
			
			return true;
		}
		/**
		 * UI Implemented.
		 * Returns a string that tells you what all the AFs in a town
		 * are capable of doing, exactly what is displayed on the menu of the UI.
		 * If you want a more data-analysis friendly version, check out
		 * getAFEffect().
		 * @param townID
		 * @return
		
		public String getAFEffectToString(int townID) {
			if(prog&&!p.isAdvancedAttackAPI()) {
				setError("You do not have the Advanced Attack API!");
				return null;
			}
			int i = 0; int j = 0;  Town t=null; 
			double bunkerSize=0;
			int totalPoppedUnits=0;
			ArrayList<AttackUnit> pau = p.getAu();
			while(i<pau.size()) {
				if(!pau.get(i).equals("locked")&&!pau.get(i).equals("empty"))totalPoppedUnits++;
				i++;
			}
			i=0;
			t = g.findTown(townID);
			if(t.getPlayer().ID!=p.ID) return null;
			
					UserBuilding currBldg=null;
					j=0;
					UserBuilding[] bldg = getUserBuildings(townID,"CombatProduction");
					while(j<bldg.length) {
						currBldg = bldg[j];
						
							bunkerSize+=Math.round(.05*p.getAfTech()*GodGenerator.getPeople(currBldg.getLvl(),totalPoppedUnits,4,GodGenerator.totalUnitPrice/p.towns().size()));
						
						j++;
					}
				
				
		

		
				 i = 0;
				int currentExpAdvSizeDef=0;
				while(i<t.getAu().size()) {
				
					currentExpAdvSizeDef+=t.getAu().get(i).getSize()*t.getAu().get(i).getExpmod();
					i++;
				}
				double armysizefrac=0;
				if(currentExpAdvSizeDef!=0){
				 armysizefrac=bunkerSize/currentExpAdvSizeDef;
				}
				else
					return "No army in this town to protect.";
				if(armysizefrac>1) armysizefrac=1;

				double percentprotection = .05*p.getAfTech()*armysizefrac*100;
				
				if(percentprotection>100)  return "Enter army protected in offensive. "+((int) Math.round(percentprotection-100))
						+ "% can be protected.";
				else return  ((int) Math.round(percentprotection)) + "% of combat units protected in offensives.";

		
		}
		
		/**
		 * UI Implemented.
		 * Returns a string that tells you what the AF
		 * is capable of doing, exactly what is displayed on the menu of the UI.
		 * If you want a more data-analysis friendly version, check out
		 * getAFEffect().
		 * @param lotNum
		 * @param townID
		 * @return
		 
		public String getAFEffectToString(int lotNum, int townID) {
			if(prog&&!p.isAdvancedAttackAPI()) {
				setError("You do not have the Advanced Attack API!");
				return null;
			}
			int i = 0; int j = 0; int k =0;  Building currBldg=null; boolean found = false;
			int totalPoppedUnits=0;
			while(i<p.getAu().size()) {
				if(!p.getAu().get(i).equals("locked")&&!p.getAu().get(i).equals("empty"))totalPoppedUnits++;
				i++;
			}
			i=0;
			Town t = g.findTown(townID); if(t.getPlayer().ID!=p.ID) return null;
			int lvl = 0;
			/*
			try {
				UberStatement stmt = g.con.createStatement();
				ResultSet rs = stmt.executeQuery("select lvl from bldg where tid = " + townID + " and slot = "+ lotNum);
				if(rs.next()) lvl = rs.getInt(1);
				rs.close();
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }
			
			ArrayList<Building> bldg = t.bldg();
			while(i<bldg.size()) {
				if(bldg.get(i).getLotNum()==lotNum) {lvl = bldg.get(i).getLvl(); break; }
				i++;
			}
			double bunkerSize=Math.round(.05*p.getAfTech()*GodGenerator.getPeople(lvl,totalPoppedUnits,4,GodGenerator.totalUnitPrice/p.towns().size()));

		
				 i = 0;
				int currentExpAdvSizeDef=0;
				UserAttackUnit au[] = getUserAttackUnits(townID);
				while(i<au.length) {
				
					currentExpAdvSizeDef+=au[i].getSize()*au[i].getExpmod();
					i++;
				}
				double armysizefrac=0;
				if(currentExpAdvSizeDef!=0){
				 armysizefrac=bunkerSize/currentExpAdvSizeDef;
				}
				else
					return "No army in this town to protect.";
				if(armysizefrac>1) armysizefrac=1;

				double percentprotection = .05*p.getAfTech()*armysizefrac*100;
				
				if(percentprotection>100)   return "Enter army protected in offensive. "+((int) Math.round(percentprotection-100))
				+ "% can be protected.";
				else return  ((int) Math.round(percentprotection)) + "% of combat units protected in offensives.";

		
		}*/
		/**
		 * THIS METHOD IS NOT COMPLETE!
		 * 
		 * This is the combat sim. To use it, send in an array of names for the def and off. An example would be ["Pillager","Seeker"].
		 * Then, make an integer array of the same size and send in the sizes of each unit. You can do the same with buildings in each town,
		 * since buildings contribute to combat as well:
		 * ["Command Center","Fortification", "Resource Cache", "Resource Cache"], and then you can send in the levels of each one.
		 * <br/><br/>
		 * The combat sim will spit out an SR into your SR box.
		 * 
		 * @param offAUNames 			an array containing the names of all the units to use on offense.
		 * @param defAUNames 			an array containing the names of all the units to use on defense.
		 * @param offAUSizes 			an array containing the sizes of all the units to use on offense.  Should be the same size as offAUNames.
		 * @param defAUSizes 			an array containing the sizes of all the units to use on defense.  Should be the same size as defAUNames.
		 * @param offTownBuildings		an array containing the names of buildings to be added to the offender's town.
		 * @param defTownBuildings		an array containing the names of buildings to be added to the defender's town.
		 * @param offTownBuildingLevels	an array containing the levels of the buildings in the offender's town.  Should be the same size as offTownBuildings.  
		 * @param defTownBuildingLevels an array containing the levels of the buildings in the defender's town.  Should be the same size as defTownBuildings.
		 * @param attackType			the type of attack to send.  See {@link attack(int yourTownID, int enemyx, int enemyy, int[] auAmts, String attackType, String[] target,String name, boolean send) attack} for a list of valid attack types.
		 * @param target				the buildings to target if this is a bombing run.
		 * 
		 * @return False
		 */
		public boolean combatSimulator(String[] offAUNames, String[] defAUNames, int[] offAUSizes, 
				int defAUSizes[], String[] offTownBuildings, String[] defTownBuildings,
				int[] offTownBuildingLevels, int[] defTownBuildingLevels, String attackType,
				String[] target) {
			
			
			int counter=0;
			if(offTownBuildingLevels.length!=offTownBuildings.length) {
				setError("Invalid town building arrays!");
				return false;
			}
			if(defTownBuildingLevels.length!=defTownBuildings.length) {
				setError("Invalid town building arrays!");
				return false;
			}
			if(offAUNames.length!=offAUSizes.length) {
				setError("Invalid AU arrays!");
				return false;
			}
			if(defAUNames.length!=defAUSizes.length) {
				setError("Invalid AU arrays!");
				return false;
			}
			
			Player[] tests = p.generateFakePlayers(2, 1, 0, 0);
			Town[] towns = new Town[tests.length];
			for(int i=0;i<towns.length;i++) {
				towns[i] = tests[i].towns().get(0);
			}
			
			for(String bldg:offTownBuildings) {
				towns[0].addBuilding(bldg,counter+4,offTownBuildingLevels[counter],0);
				counter++;
			}
			counter=0;
			for(String bldg:defTownBuildings) {
				towns[1].addBuilding(bldg,counter+4,offTownBuildingLevels[counter],0);
				counter++;
			}
			
			p.deleteFakePlayers(tests);
			return false;
			
		}
		/**
		 * UI Implemented.
		 * 
		 * Returns in a string what you see on the resource cache menu about how many resources are protected.
		 */
		
		public String getCacheEffectToString(int townID) {
			if(prog&&!p.isAdvancedAttackAPI()) {
				setError("You do not have the Advanced Attack API!");
				return null;
			}
			boolean keep = false;
			if(prog) keep=true;
			prog=false;
			UserBuilding[] bldg = getUserBuildings(townID,"all");
			if(keep) prog = true;
			int j = 0; UserBuilding currBldg;
			long resSize = 0;
			while(j<bldg.length) {
				currBldg = bldg[j];
			//	if(currBldg.getLotNum()==lotNum) bunkerMode = currBldg.getBunkerMode();
			
				if(currBldg.getType().equals("Resource Cache"))resSize+=(long) Math.round(.33*.05*((double) Building.resourceAmt)*Math.pow(currBldg.getLvl()+2,2));
				if(currBldg.getType().equals("Storage Yard")&&currBldg.getLvl()>=5) resSize+=(long) Math.round(.33*.05*((double) Building.resourceAmt)*Math.pow(currBldg.getLvl()-4+2,2));
					
				
		
				j++;
			}
			
			String toRet= "Your resource caches protect " + resSize + " of each resource.";

			return toRet;
		}
	



/**
 * UI Implemented.
 * <br/><br/>
 * Returns the price in a long array for a level upgrade of the building in the lotNum
 * given in the town of the tid given.
 * 
 * @param lotNum	the lot number of the building you want the proces for
 * @param tid		the ID of the town the building is in
 * 
 * @returns an array of type long that contains the level up cost of this building 
 */

public long[] returnPrice(int lotNum, int tid) {
	if(prog&&!p.isAdvancedBuildingAPI()) {
		setError("You do not have the Advanced Building API!");
		return null;
	}
	Town t = g.findTown(tid);
	if(t.getPlayer().ID!=p.ID) return null;
	return returnPrice(t.getTownName(),lotNum);

}
/**
 * Returns a long array representing the net amount of resources to get to a certain level of building.
 * Great for guestimating.
 * 
 * @param lvl			the level you want an estimate for
 * @param buildingType	the type of building you want to check against.
 * 
 * @return an array containing the net amount of resources required to get from level 0 to lvl for a certain building.
 * <br/>
 * long[] {metal, timber, crystal, food}
 */
	public long[] returnPriceToGetToLevel(int lvl, String buildingType) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return null;
		}
		int i = 0;
		// LINKED TO ALL BUILDING PRICES
		//double additive=0;
		long[] cost=new long[4];
		//	public Building(String type, int lotNum, int totalEngineers, double cloudFactor, int engTech) {
		long[] basecost=Building.getCost(buildingType);
		if(basecost==null) {
			setError("Invalid building type!");
			return null;
		}
	//	Building b = new Building("Bunker",0,0,0,1); // just dummy
		
		i=0;
		while(i<lvl) {
			int j = 0;
			while(j<cost.length) {
				cost[j]+=basecost[j]*Math.pow(i+1,2+.03*(i+1));
				j++;
			}
			i++;
		}
		return cost;
	}
/**
 * Returns a number representing the net amount of resources(all lumped) to get to a certain level of building.
 * Great for guestimating.
 * 
 * @param lvl the level you want an estimate for
 * 
 * @return the net sum of all resources need to geta building from from level 0 to lvl.
 */
	public long returnPriceToGetToLevel(int lvl) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return 0;
		}
		int i = 0;
		// LINKED TO ALL BUILDING PRICES
		double additive=0;
		long cost=0;
		//	public Building(String type, int lotNum, int totalEngineers, double cloudFactor, int engTech) {
		int basecost=0;
	//	Building b = new Building("Bunker",0,0,0,1); // just dummy
		while(i<Building.getCost("Fortification").length) {
			basecost+=Building.getCost("Fortification")[i];
			i++;
		}
		i=0;
		while(i<lvl) {
			cost+=basecost*Math.pow(i+1,2+.03*(i+1));
			i++;
		}
		return cost;
	}
	
		/**
		 * UI Implemented.
		 * <br/><br/>
		 * Returns the price in a long array for a level upgrade of the building in the lotNum
		 * given in the town of the townName given.
		 * 
		 * @param townName	the name of the town the building is in
		 * @param lotNum	the lot number of the building you want the prices for
		 * 
		 * @deprecated Use {@link #returnPrice(int lotNum, int tid)} instead.
		 */
	public long[] returnPrice(String townName, int lotNum) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return null;
		}
		// LINKED TO LEVELUP, RETURNPRICETOGETTOLEVEL
		long cost[] = new long[5];
		// return price for buildings!
		 double additive;
		Town t = g.findTown(townName,p);
		if(t.getPlayer().ID!=p.ID) return null;
		UUID bid = null;
		/*try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select bid from bldg where tid = " + t.townID + " and slot = "+ lotNum);
			if(rs.next()) bid = rs.getInt(1);
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }*/
		
		int i = 0; ArrayList<Building> bldg = t.bldg();
		while(i<bldg.size()) {
			if(bldg.get(i).getLotNum()==lotNum) {bid = bldg.get(i).getId(); break; }
			i++;
		}
		if(bid==null) return null;
				UserBuilding b = getUserBuilding(bid);
		
						int k = 0;
						while(k<5) {
							if(!b.isDeconstruct()) {
								
						//Exp(lvl+1)*((lvl+1)/10)*100 - 100 is cost in this case, the average base cost of an upgrade
						
							 cost[k]=(long) Math.round((Building.getCost(b.getType())[k]*Math.pow(b.getLvl()+b.getLvlUps()+1,2+.03*(b.getLvl()+b.getLvlUps()+1))));
							} else {
								// demolsih initiates a level up but it's really not, just to keep the building
								// server looking at it, so we adjust by one for that by subtraction
								// for price displays!
									//Exp(lvl+1)*((lvl+1)/10)*100 - 100 is cost in this case, the average base cost of an upgrade
									
								 cost[k]=(long) Math.round((Building.getCost(b.getType())[k]*Math.pow(b.getLvl()+b.getLvlUps(),2+.03*(b.getLvl()+b.getLvlUps()))));
									
							}
						k++;
						
						}
						return cost;
				
		
	}
	
	/**
	 * 
	 * UI Implemented.
	 * <br/><br/>
	 * Returns the price in a long array for the number of unitType(may be civilian or military) 
	 * in the town of the townID given.
	 * <br/>
	 * Use of {@link #returnPrice(String unitType, int number, int tid, boolean minus)} is encouraged over this method.
	 * 
	 * @param unitType	the type of unit to be purchased
	 * @param number	the amount of that unit to be purchased
	 * @param tid		the Id of the town they're to be built in
	 * 
	 * @return	an array of type long that contains the prices to purchase number unitType's
	 * <br/>
	 * long[] {metal, timber, manmat, food, expMod}
	 */
	public long[] returnPrice(String unitType, int number, int tid) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return null;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return null;
		return returnPrice(unitType,number,tid,false);
		
	}
		/**
		 * UI Implemented.
		 * <br/><br/>
		 * Returns the price in a long array for the number of unitType(may be civilian or military) 
		 * in the town of the townName given.
		 * 
		 * @param townName	the name of the town the units will be built in
		 * @param unitType	the type of unit to be built
		 * @param number	the amount of that unit to be built.
		 * 
		 * @return an array of type long that contains the prices to purchase number unitType's
		 * <br/>
		 * long[] {metal, timber, manmat, food, expMod}
		 * 
		 * @deprecated Use {@link #returnPrice(String unitType, int number, int tid)} instead.
		 */
	public long[] returnPrice(String townName, String unitType, int number) {
		
		return returnPrice(unitType,number,g.findTown(townName,p).townID,false);
	}
	
	/**
	 * If minus is false, then it gets the price of the next number of soldiers.
	 * If minus is true, it gets the price of the most recent number made. Example:
	 * if minus is false, and you have 100 soldiers, and you ask for the price of 5,
	 * it gives you the price of the 101st,102nd,103rd,104th,105th soldier summed.
	 * If minus is true, and you have 100 soldiers, and you ask for the price of 5,
	 * it gives you the price of the 96th,97th,98th,99th,100th soldier prices summed.
	 * 
	 * @param unitType - String name of the unit
	 * @param number - Number of units to build
	 * @param tid - ID of the town they're being built in
	 * @param minus - if true, gives the price of the last number of units, instead of the next
	 * 
	 * @return long[] {metal, timber, manmat, food, expMod}
	 */
	public long[] returnPrice(String unitType, int number, int tid, boolean minus) {

		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return null;
		}
		// LINKED TO RETURN AMOUTN OF UNITS FOR LEVELING
		int i = 0; boolean found = false;
		
		Town holdT = g.findTown(tid);
			
		if(holdT.getPlayer().ID!=p.ID) return null;
		
			long cost[] = new long[5];
			if(unitType.equals("Engineer")) {
				  i = 0;
					 int currentlyBuilding = 0;
					 /*
				  try {
					  UberStatement stmt = g.con.createStatement();
					  ResultSet rs = stmt.executeQuery("select sum(pplbuild) from bldg where tid = " + holdT.townID + " and name = 'Construction Yard';");
					  if(rs.next()) currentlyBuilding = rs.getInt(1);
					  rs.close();
					  stmt.close();
					  
				  } catch(SQLException exc) { exc.printStackTrace(); }*/
					 UserBuilding b[] = getUserBuildings(holdT.townID,"Command Center");
					 while(i<b.length) {
						 currentlyBuilding+=b[i].getNumLeftToBuild();
						 i++;
					 }
				  
				  // n_new(n_new+1)/2 - n_old(n_old+1)/2 is the cost to build all those units.
					 // now, if we want it from number units ago...then allCalled is allCalled-number. So the most
					 // recent number.
				 int allCalled = holdT.getTotalEngineers()+currentlyBuilding;
				 if(minus) allCalled-=number;
					int totalnumber=number+allCalled;
					 double factor = totalnumber*(totalnumber+1)/2 - allCalled*(allCalled+1)/2;
					 
					 cost[0] = (long)Math.round(17*factor);
					 cost[1] = (long) Math.round(17*factor);
					 cost[2] = (long) Math.round(12*factor);
					 cost[3] = Town.getCivvieFoodConsumption(number);
					 cost[4] = -1;
					 return cost;

			} else if(unitType.equals("Trader")) {
				 int currentlyBuilding = 0;
				 /*
				  try {
					  UberStatement stmt = g.con.createStatement();
					  ResultSet rs = stmt.executeQuery("select sum(pplbuild) from bldg where tid = " + holdT.townID + " and name = 'Trade Center';");
					  if(rs.next()) currentlyBuilding = rs.getInt(1);
					  rs.close();
					  stmt.close();
					  
				  } catch(SQLException exc) { exc.printStackTrace(); }*/
				 UserBuilding b[] = getUserBuildings(holdT.townID,"Trade Center");
				 while(i<b.length) {
					 currentlyBuilding+=b[i].getNumLeftToBuild();
					 i++;
				 }
				  // n_new(n_new+1)/2 - n_old(n_old+1)/2 is the cost to build all those units.
				 int allCalled = holdT.getTotalTraders()+currentlyBuilding;
				 if(minus) allCalled-=number;

					int totalnumber=number+allCalled;
					
					 double factor = totalnumber*(totalnumber+1)/2 - allCalled*(allCalled+1)/2;
					 cost[0] = (long) Math.round(15*factor);
					 cost[1] = (long) Math.round(23*factor);
					 cost[2] = (long) Math.round(12*factor);
					 cost[3] = Town.getCivvieFoodConsumption(number);
					 cost[4] = -1; // These are citizens, add one to the population! This doesn't actually do anything the way I programmed it.
					 return cost;

				 
			} else if(unitType.equals("Scholar")) {
				
					  i = 0;
					 int currentlyBuilding = 0;
					 /*
					  try {
						  UberStatement stmt = g.con.createStatement();
						  ResultSet rs = stmt.executeQuery("select sum(pplbuild) from bldg where tid = " + holdT.townID + " and name = 'Institute';");
						  if(rs.next()) currentlyBuilding = rs.getInt(1);
						  rs.close();
						  stmt.close();
						  
					  } catch(SQLException exc) { exc.printStackTrace(); }*/
					 UserBuilding b[] = getUserBuildings(holdT.townID,"Institute");
					 while(i<b.length) {
						 currentlyBuilding+=b[i].getNumLeftToBuild();
						 i++;
					 }
					 
					 // n_new(n_new+1)/2 - n_old(n_old+1)/2 is the cost to build all those units.
					 int allCalled = currentlyBuilding;
					 if(minus) allCalled-=number;

						int totalnumber=number+allCalled;
						 double factor = totalnumber*(totalnumber+1)/2 - allCalled*(allCalled+1)/2;
				 cost[0] = (long) Math.round(13*factor);
				 cost[1] = (long) Math.round(20*factor);
				 cost[2] = (long) Math.round(20*factor);
				 cost[3] = Town.getCivvieFoodConsumption(number);
				 cost[4] = -1; // These are citizens, add one to the population!
				 return cost;
				 
			} else {
				// maybe an au?
				 i = 0;
				 AttackUnit AU;
				 ArrayList<AttackUnit> au = holdT.getAu();
				 ArrayList<Town> towns = p.towns();
				 int townSize = towns.size();
				 // so town 2 gets you 75% of it, then town 3 gets you 75% of THAT.
				 // so it's 1*.75*.75...
				 double modifier = Math.pow(.75,townSize);
				 while(i<au.size()) {
					 if(au.get(i).getName().equals(unitType)) {
						  AU = au.get(i);
						  int totalPop;
							 totalPop = GodGenerator.getTotalSize(AU,p);
						 
						 
						 double multiplier=0;
						 multiplier=AU.getExpmod()*modifier;
						 
						 //4 + .7
						 
						  i = 0;
						  UserBuilding b; UserQueueItem q;
						  UserQueueItem[] queue;
						 int currentlyBuilding = 0;
						 int k = 0;
						 UserBuilding[] bldg;
						 
						 Town t = null;
						 while(k<towns.size()) {
							 t = towns.get(k);
							 i=0;
							 bldg = getUserBuildings(t.townID,"CombatProduction");
								 while(i<bldg.length) {
									
										 int j = 0;
										  b = bldg[i]; queue = b.getQueue();
										 while(j<queue.length) {
											  q = queue[j];
											 if(q.returnAUtoBuild()==AU.getSlot()) currentlyBuilding+=q.returnNumLeft();
											 j++;
										 }
									 
									 i++;
								 } // n_new(n_new+1)/2 - n_old(n_old+1)/2 is the cost to build all those units.
								 k++;
						 }
						 int allCalled = totalPop+currentlyBuilding;
						 if(minus) allCalled-=number;
							int totalnumber=number+allCalled;
							 double factor = multiplier*totalnumber*(multiplier*totalnumber+1)/2 - multiplier*allCalled*(multiplier*allCalled+1)/2;
						 cost[0] = (long)Math.round(25*factor); // metal
						 cost[1] = (long) Math.round(10*factor); // timber
						 cost[2] = (long) Math.round(26*factor);//manmat
						 AttackUnit copy = AU.returnCopy(); copy.setSize(number);
						 cost[3] = Town.getFoodConsumption(copy);
						 cost[4] = -AU.getExpmod(); // // need a 10, 25, 15, 30
						 
						 return cost;
					 }
					 i++;
				 }
				 
			}
			 return null; // means that it found nothing.

	
		
		
	
	}
	/**
	 * Returns a short description of the quest.
	 * @param qid
	 * @return
	 */
	public String getQuestDescription(int qid) {
		int i = 0;
		QuestListener q;
		ArrayList<QuestListener> activeQuests = g.getAllActiveQuests();
		while(i<activeQuests.size()) {
			q = activeQuests.get(i);
			if(q.ID==qid) {
				return q.getQuestDescription(p.ID);
			}
			i++;
		}
		return null;
	}
	/**
	 * Returns true if you have the building type in the town named.
	 * 
	 * @param type	the type of building we're checking for.  See {@link UserBuilding} for a list of types.
	 * @param tid 	the ID of the town to check in
	 * 
	 * @return True, if a building of type exists in the town.  False otherwise.
	 */
	public boolean haveBldg(String type, int tid) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return haveBldg(t.getTownName(),type);
	}
		/**
		 * Returns true if you have the building type in the town townName.
		 * 
		 * @param townName	the name of the town to search in
		 * @param type		the type of building to search for.  See {@link UserBuilding} for a list of types.
		 * 
		 * @deprecated Use {@link #haveBldg(String type, int tid)} instead.
		 */
	public  boolean haveBldg(String townName, String type) {
	
		
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return false;
		}
		// type could be a string. Need to return it.
		Town t = g.findTown(townName,p);
		if(t.getPlayer().ID!=p.ID) return false;
		/*
		try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from bldg where tid = " + t.townID + " and name = '"+type+"';");
			int num=0;
			if(rs.next()) num=rs.getInt(1);
			rs.close();
			stmt.close();
			
			if(num>0) return true;
			
		} catch(SQLException exc) { exc.printStackTrace(); }*/
		
		ArrayList<Building> bldg = t.bldg();
		int i = 0;
		while(i<bldg.size()) {
			if(bldg.get(i).getType().equals(type)) { return true; }
			i++;
		}
		return false;
	}
	/**
	 * Returns true if there is a building of that type with that level or greater.
	 * 
	 * @param type		the type of building to look for.  See {@link UserBuilding} for a list of types.
	 * @param lvl		the minimum level the building must be
	 * @param townID	the Id of the town to look in
	 *
	 * @return True, if a building of type with a level of, at least, lvl is found.  False otherwise.
	 */
public  boolean haveBldg(String type, int lvl, int townID) {
	
		
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return false;
		}
		// type could be a string. Need to return it.
		Town t = g.findTown(townID);
		if(t.getPlayer().ID!=p.ID) return false;
		/*
		try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from bldg where tid = " + t.townID + " and name = '"+type+"';");
			int num=0;
			if(rs.next()) num=rs.getInt(1);
			rs.close();
			stmt.close();
			
			if(num>0) return true;
			
		} catch(SQLException exc) { exc.printStackTrace(); }*/
		
		ArrayList<Building> bldg = t.bldg();
		int i = 0;
		while(i<bldg.size()) {
			if(bldg.get(i).getType().equals(type)&&bldg.get(i).getLvl()>=lvl) { return true; }
			i++;
		}
		return false;
	}

	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Cancel queue item identified by it's qid in an military production facility identified
	 * by it's lotNum in a town specified by it's town id, tid.
	 * 
	 * @param qid the ID of the queue to cancel
	 * @param lotNum the lot number the building with the queue is on
	 * @param tid the ID of the town the building is in
	 * 
	 * @return True, if the queue was successfully canceled.  False otherwise.
	 */
	public boolean cancelQueueItem(UUID qid, int lotNum, int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return cancelQueueItem(qid,lotNum,t.getTownName());
	}
	
		/**
		 * UI Implemented.
		 * @deprecated Use {@link #cancelQueueItem(UUID qid, int lotNum, int tid)} instead.
		 */
	public boolean cancelQueueItem(UUID qid, int lotNum,String townName) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return false;
		}
		Town holdT;Building b = null;
	
		 holdT = g.findTown(townName,p);
		 if(holdT.getPlayer().ID!=p.ID) {
			 setError("Not your town!");
			 return false;
		 }
		 int i = 0;
			if(!checkMP(holdT.townID)) return false;

			UUID bid = null;
			/*
			try {
				UberStatement stmt = g.con.createStatement();
				ResultSet rs = stmt.executeQuery("select bid from bldg where tid = " + holdT.townID + " and slot = "+ lotNum);
				if(rs.next()) bid = rs.getInt(1);
				rs.close();
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }*/
			ArrayList<Building> bldg = holdT.bldg();
			while(i<bldg.size()) {
				if(bldg.get(i).getLotNum()==lotNum) {bid = bldg.get(i).getId(); break; }
				i++;
			}
			if(bid==null) {
				setError("Invalid building lot!");
				return false;
			}
			i = 0;
		boolean found=false;
		UserQueueItem q;
		 b = holdT.findBuilding(bid);
		UserQueueItem[] queue = getUserQueueItems(bid);
		long res[] = holdT.getRes();
		while(i<queue.length) {
			 q = queue[i];
			if(q.getId().equals(qid)&&i!=0) { 
				int j = 0;
				long cost[] = q.getCost();
				synchronized(res) {
				while(j<cost.length) {
					res[j]+=(long) Math.round(cost[j]);
					j++;
				}
				}
				QueueItem qu = b.findQueueItem(qid);
				qu.deleteMe(); found = true; break; }
			else if(q.getId().equals(qid)&&i==0) {

				// this means shit has been building and we need to figure out what's
				// left and then find the price for that given the starting
				// plus the new ones. And we must keep the original starting town,
				// or else risk that we give back less than they paid if they
				// invade and then hit x. But you know what, that is fine, we can
				// figure out.

				// maybe an au?
				int k = 0;
				 AttackUnit AU;
				 
				 ArrayList<AttackUnit> au = holdT.getAu();
				 while(k<au.size()) {
					 if(au.get(k).getSlot()==q.returnAUtoBuild()) {
						  AU = holdT.getAu().get(k);
						  int totalPop;
						  
							 totalPop = q.getOriginalAUAmt()+(q.getTotalNumber()-q.returnNumLeft()); // AUNumber is stuff left, so total - stuff left = stuff built!
						 
						//	 System.out.println("totalpop is " + totalPop + " originalAUAmt is" + q.getOriginalAUAmt() + " tN is " + q.getTotalNumber() +
							//		 " numleft is " + q.returnNumLeft());
						 double multiplier=0;
						 double modifier=Math.pow(.75,q.getTownsAtTime());
						multiplier=AU.getExpmod()*modifier;
						 
						 
						 //4 + .7
						
						// System.out.println("multiplier is " + multiplier);
						 int allCalled = totalPop; // we only want the totalpop. The one we started with
						 // plus the ones we got.
							int totalnumber=q.returnNumLeft()+allCalled; // allCalled is all you have, you're going to build AUNumber more...
							 double factor = multiplier*totalnumber*(multiplier*totalnumber+1)/2 - multiplier*allCalled*(multiplier*allCalled+1)/2;
						long cost[] = new long[5];
						cost[0] = (long)Math.round(25*factor); // metal
						 cost[1] = (long) Math.round(10*factor); // timber
						 cost[2] = (long) Math.round(26*factor);//manmat
						 cost[3] = (long) Math.round(9*factor); //food
						 cost[4] = -AU.getExpmod(); // // need a 10, 25, 15, 30
						 int j = 0;
							
						 synchronized(res) {
							while(j<q.getCost().length) {
						//		System.out.println("res before " +j+" " +res[j]);
								res[j]+=(long) Math.round(.9*cost[j]);
							//	System.out.println("res after " +j+" " +res[j]);
								j++;
							}
						 }
							QueueItem qu = b.findQueueItem(qid); // real queue item for deletion.
							qu.deleteMe(); found = true; break; 
						 
					 }
					 k++;
				 }
				 
			
				
			}
			i++;
		}
		if(found) {
		return true; } else return false;
	}
	
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Cancel a building level up by naming the slot of the building and the town id.
	 * 
	 * @param lotNum the lot number of the building whose level up we're canceling
	 * @param tid the ID of the town that building is in
	 * 
	 * @return True, if the cancelation was successful.  False otherwise.
	 */
	//Why is this method called "cancelQueueItem" anyways?
	public boolean cancelQueueItem(int lotNum, int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return cancelQueueItem(lotNum,t.getTownName());
	}

		/**
		 * UI Implemented.
		 * @deprecated Use {@link #cancelQueueItem(int lotNum, int tid)} instead.
		 */
	public boolean cancelQueueItem(int slotNum, String townName) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return false;
		}
		
		
		// so this cancels the most recent level up on a building and returns some resources.
		// if I just decrease lvlUps...if lvlUps is one, then the most recent tick thing has to be set to 0,
		// otherwise, is okay. If is lvl 0, then we have to remove from server entirely.
		Town holdT = g.findTown(townName,p);
		//boolean found=false;
		if(!checkMP(holdT.townID)) return false;

		if(holdT.getPlayer().ID!=p.ID) {
			setError("Not your town!");
			return false;
			
		}
		//int bid = 0;
/*
		try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select bid from bldg where tid = " + holdT.townID + " and slot = "+ slotNum);
			if(rs.next()) bid = rs.getInt(1);
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }*/
		int i = 0;
		ArrayList<Building> bldg = holdT.bldg();
		Building b=null;
		while(i<bldg.size()) {
			if(bldg.get(i).getLotNum()==slotNum) {b = bldg.get(i); break; }
			i++;
		}
		if(b==null) { setError("Bad building lot!"); return false; }
			int lvlUps = b.getLvlUps();
			
				if(lvlUps<=1) {
					b.setLvlUps(0);
					b.setTicksToFinish(-1);
					
					// if it's level 0, and this is the only level up on it, we have to destroy the fricking building.
					// Like gone, done, deaderoo, holy crap we just lost one. etc. etc.
					// Let's do the dirty work!
					
					if(b.getLvl()==0) holdT.killBuilding(b.getId());
					
				} else b.setLvlUps(lvlUps - 1);
				
				// lvlUps is increased after cost is calculated, so now that it has been decreased,
				// the below equation is correct. I think.
				if(!b.isDeconstruct()) {
				 int k = 0; 
				 long res[] = holdT.getRes();
				 lvlUps = b.getLvlUps();
				 int lvl = b.getLvl();
				 synchronized(res) {
				 do {
			
					 res[k]+=.9*Building.getCost(b.getType())[k]*Math.pow(lvl+lvlUps+1,2+.03*(lvl+lvlUps+1));// NNOOOOOOOOO!!!!
					 k++; 
				 } while(k<res.length);
				 }
				} else {
					b.setDeconstruct(false); // so if it's deconstructing, don't give resources back, cancel it!
				}
				notifyViewer();

				 return true;
			
	
	}
	/**
	 * UI Implemented.
	 * Get the Alamo Soft Limit of your city.
	 */
	public int getCSL(int tid) { 
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return -1;
		}
		Town t = g.findTown(tid);
		if(t==null) {
			setError("Invalid tid!");
			return -1;
		}
		if(t.getPlayer().ID!=p.ID) return 0;
		double avg = g.getAverageLevel(t);
		int k = 0;
		AttackUnit a; int popped = 0;
		ArrayList<AttackUnit> au = t.getPlayer().getAu();
		while(k<au.size()) {
			a = au.get(k);
			if(!a.getName().equals("locked")&&!a.getName().equals("empty"))popped++;
			k++;
		}
		
		return getCSLAtLevel(avg,popped,t.getPlayer().towns().size());
		
	}
	/**
	 *Get the Alamo Soft Limit of your city given this average level.
	 * @param avg - average level of the buildings in the city
	 * @param popped - number of combat unit slots occupied.
	 * @return
	 */
	public int getCSLAtLevel(double avg, int popped, int townSize) { // LINKED TO CONTROLLERS! LOADWORLDMAP! CSL IS THERE STREAMLINED!
		
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return -1;
		}
		
		return (int) Math.round(4*Math.sqrt(6)*(avg+1)*popped/* *townSize*/);
		
	}
	public int getPoppedUnits() {
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return -1;
		}
		int k = 0;
		AttackUnit a; int popped=0;
		ArrayList<AttackUnit> au = p.getAu();
		while(k<au.size()) {
			a = au.get(k);
			if(!a.getName().equals("locked")&&!a.getName().equals("empty"))popped++;
			k++;
		}
		
		return popped;
	}
	
	
	
	/** UI Implemented. Returns the current Cover Size of your army.
	 * 
	 */
	
	public int getCS(int tid) {
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return -1;
		}
		Town t = g.findTown(tid);
		if(t==null) {
			setError("Invalid tid!");
			return -1;
		}
		AttackUnit au;
		int k =0; int currentExpAdvSizeDefWithDivMods=0;
		ArrayList<AttackUnit> AU = t.getAu();
		while(k<AU.size()) {
			au = AU.get(k);
			
				 switch(au.getType()) {
					case 1:
						currentExpAdvSizeDefWithDivMods+=au.getExpmod()*au.getSize();
						break;
					case 2:
						currentExpAdvSizeDefWithDivMods+=au.getExpmod()*au.getSize();
						break;
					case 3:
						currentExpAdvSizeDefWithDivMods+=au.getExpmod()*au.getSize();
						break;
					case 4:
						currentExpAdvSizeDefWithDivMods+=(int) Math.round(au.getExpmod()*au.getSize());
						break;
			
					}
			k++;
		}
		return currentExpAdvSizeDefWithDivMods+t.getDigAmt();
	}
	/**
	 * Causes the current thread to stall and wait.
	 * 
	 * @param s the amount of time, in seconds, the thread should wait.
	 * 
	 * @return True, if the thread is waiting.  False otherwise.
	 * 
	 * @deprecated Only used with older versions of the Revelations interface.
	 * Please migrate to Revelations 2.0 if you have not already done so. 
	 */
	public boolean wait(int s) {
		pushLog("wait(" + s + ");");
		int i = 0; Hashtable r=null;
		while(i<g.programs.size()) {
			r = g.programs.get(i);
			if(((Integer) r.get("pid"))==p.ID) break;
			i++;
		}
		if(r==null) return false;
		r.put("sleep",true);
		try {
			Thread.currentThread().sleep(s*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			g.programs.get(p.ID).put("sleep",false);
			return false;

		}
		r.put("sleep",false);

		return true;
	}
	/**
	 * 
	 * UI Implemented.
	 * <br/><br/>
	 * Completely destructs the target building.
	 * 
	 * @param lotNum the lot number of the building to be demolished
	 * @param tid	 the ID of the town the building is in
	 * 
	 * @return True, if the building has begun destructing.  False otherwise.
	 */
	public boolean demolish(int lotNum, int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return demolish(lotNum,t.getTownName());
	}

		/**
		 * UI Implemented.
		 * @deprecated Use {@link #demolish(int lotNum, int tid)} instead.
		 */
	public boolean demolish(int lotNum, String townName) {

		
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		// format is levelUp(lotNum,town);
		Town holdT =g.findTown(townName,p); // exception town does not exist.
		if(holdT.isZeppelin()) {
			setError("You cannot demolish buildings on an Airship!");
			return false;
		}
	// Now does the building there exist?
		if(!checkMP(holdT.townID)) return false;

		if(holdT.getPlayer().ID!=p.ID) {
			setError("Not your town!"); return false;
			
		}
		int slotsSpent = 0;
		
		UUID bid = null;
	/*	try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select sum(lvlUp) from bldg where tid = " + holdT.townID + ";");
			if(rs.next()) slotsSpent = rs.getInt(1);
			rs.close();
			 rs = stmt.executeQuery("select bid from bldg where tid = " + holdT.townID + " and slot = " + lotNum + ";");
			if(rs.next()) bid = rs.getInt(1);
			rs.close();
			
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }*/
		int i = 0;
		ArrayList<Building> bldg = holdT.bldg();
		while(i<bldg.size()) {
			slotsSpent+=bldg.get(i).getLvlUps();
			if(bldg.get(i).getLotNum()==lotNum) {bid = bldg.get(i).getId(); }
			i++;
		}
	if(slotsSpent>=p.getConstructionResearch()) {
		setError("Need more construction research! Research it.");
		return false; // simple. effective.
	}
	
	if(bid==null) { 
		setError("Bad building lot!"); return false;
	}
	boolean keep=false;
	if(prog) keep=true;
	prog=false;
	UserBuilding holdBldg = getUserBuilding(bid);

	if(keep) prog = true;

			 if(holdBldg.getLvl()==0) {
				 setError("Cannot demolish a level 0 building. Cancel it's queue item in the Command Center instead to get rid of it!");
				 return false;
			 }
			 // So now we have the bldg(). If it has more than one lvlUp, it needs to cancelQueueItem on every single leveled
			 //thing up until it's levelUps are gone. Then, it needs to call levelUp in town, and that will be modified
			 // to not subtract resources if deconstruct is on, which it will be, of course. Then everything will run like clockwork.
			  if(holdBldg.getType().equals("Metal Mine")||holdBldg.getType().equals("Timber Field")
					  ||holdBldg.getType().equals("Crystal Mine")||
					  holdBldg.getType().equals("Farm")) {
				  setError("Cannot demolish mines.");
				  return false;
			  }
			Building b = holdT.findBuilding(bid);

			while(b.getLvlUps()>0) {
				
				cancelQueueItem(b.getLotNum(),holdT.getTownName());
			}

			b.setDeconstruct(true); // use the real thing for this.
			
			 if(levelUp(b.getLotNum(),holdT.getTownName()))  {
				 notifyViewer();
				 return true;			

			 } else return false;

		
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns true if you can buy the number of units(can be military or civilian) in the building
	 * labeled by slotNum(or lotNum, if you prefer) in the town designated by the town id.
	 * 
	 * @param unitType the name of the unit to be built.  IE "Engineer"
	 * @param number the amount of that unit to build
	 * @param lotNum the lot number of the building they're to be built in
	 * @param tid the ID of the town the building is in
	 * 
	 * @return True, if the units can be built.  False otherwise.
	 */
	public boolean canBuy(String unitType, int number, int lotNum, int tid) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return canBuy(t.getTownName(),unitType,number,lotNum);
	
	}
	
		/**s
		 * UI Implemented.
		 * @deprecated Use {@link #canBuy(String unitType, int number, int lotNum, int tid)} instead.
		 */
	public boolean canBuy(String townName, String unit, int number, int slotNum) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return false;
		}
	if(number<=0) return false;
		// right so I suppose we should check buildings first.
			Town holdT;
		
		 holdT = g.findTown(townName,p); // so now we have the town. Let's go through civvy units first.
			
			if(!checkMP(holdT.townID)) return false;
			if(holdT.getPlayer().ID!=p.ID) { setError("Not your town!"); return false; }
			
			long cost[] = new long[5]; Building b = null; int bid = 0;
		/*	try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select bid from bldg where slot = "+ slotNum + " and tid = " + holdT.townID);
			
			if(rs.next()) bid = rs.getInt(1);
			
			
			rs.close();
			stmt.close();
			if(bid==0) return false;
				
			} catch(SQLException exc)  { exc.printStackTrace(); }*/
			
			int i = 0;
			ArrayList<Building> bldg = holdT.bldg();
			while(i<bldg.size()) {
				if(bldg.get(i).getLotNum()==slotNum) {b = bldg.get(i); break; }
				i++;
			}
			if(b==null) { 
				setError("Bad lot number!");
				return false;
			}
			
		if(unit.equals("Engineer")) {
			// retrieving costs...
			// not F = CY*!F, !F = !CY + F
			 i = 0;
			
			if(!b.getType().equals("Command Center")) return false;
			if(b.getType().equals("Command Center")&&(b.getPeopleInside()+b.getNumLeftToBuild()+number)>b.getCap()) return false;
			// if this is a building and we're above cap, get out.
			
			cost = returnPrice(holdT.getTownName(),"Engineer",number);
			 
			 int j = 0; 
			 long res[] = holdT.getRes();
			 do {
				 if(res[j]<cost[j]) return false;
				 j++;
			 } while(j<4);
		}	else	if(unit.equals("Messenger")) {
			// retrieving costs...
			
					if(!b.getType().equals("Command Center")) return false;
					// if this is the building and it's not a cy, get out.
					if(b.getType().equals("Command Center")&&(b.getPeopleInside()+b.getNumLeftToBuild()+number)>b.getCap()) return false;
					// if this is a building and we're above cap, get out.		
				
			
			cost = returnPrice(holdT.getTownName(),"Messenger",number);

			 int j = 0; 
			 long res[] = holdT.getRes();
			 do {
				 if(res[j]<cost[j]) return false;
				 j++;
			 } while(j<4);
		} else	if(unit.equals("Trader")) {
			// retrieving costs...
			ArrayList<Trade> tres;
				
					if(!b.getType().equals("Trade Center")) {
						setError("This is not a trade center!");
						return false;
					}
					// if this is the building and it's not a cy, get out.
					if(b.getType().equals("Trade Center")) {
						int ie = 0; int currentlyOut=0;
						/*
						try {
							UberStatement stmt = g.con.createStatement();
							ResultSet rs = stmt.executeQuery("select sum(traders) from trade where tid1 = "+ holdT.townID + " and (tradeOver = false or ticksToHit >= 0)");
							
							if(rs.next()) currentlyOut = rs.getInt(1);
							
							
							rs.close();
							stmt.close();
							
								
							} catch(SQLException exc)  { exc.printStackTrace(); }*/
						
						ArrayList<Trade> tr = holdT.tradeServer();
						while(ie<tr.size()) {
							currentlyOut+=tr.get(ie).getTraders();
							ie++;
						}
						// System.out.println("Found " + currentlyOut + " traders out.");
						 // we need the tradeDearth.
						 int totalDearth=0; 
						 int k = 0; 
						 UserBuilding bldgs[] = getUserBuildings(holdT.townID,"Trade Center");
						 UserBuilding holdB;
							while(k<bldgs.length) {
								 holdB = bldgs[k];

									if(holdB.getType().equals("Trade Center")){
										totalDearth+=(holdB.getCap()-holdB.getPeopleInside()-holdB.getNumLeftToBuild());
										
									}
									
										

								k++;
							}
							//System.out.println("Total dearth is " + totalDearth);
						
						if((totalDearth-currentlyOut)<number) { 
						 // now we know if we get past this,
							 // we have the room in other TCs for traders, so we can build here if there
							 // is room with numLeftToBuild.
							 setError("Not enough room in Trade Center.");
							 return false;
						 }
					//	System.out.println("pplinside is " + b.peopleInside + " and num left to build is  " + b.numLeftToBuild);
						if ((b.getPeopleInside()+b.getNumLeftToBuild()+number)>b.getCap()) {
							 setError("Not enough room in Trade Center.");

							return false;
						}
						
					}
			cost = returnPrice(holdT.getTownName(),"Trader",number);

			 int j = 0; 
			 long res[] = holdT.getRes();
			 do {
				 if(res[j]<cost[j]) {
					 setError("You do not have the resources!");
					 return false;
				 }
				 j++;
			 } while(j<4);
			 
		} else if(unit.equals("Scholar")) {
			// retrieving costs...
			ArrayList<Trade> tres;
			
			if(!b.getType().equals("Institute")) {
				setError("This is not an Institute!");
				return false;
			}
			// if this is the building and it's not a cy, get out.
			if(b.getType().equals("Institute")) {
				int ie = 0; int currentlyOut=0;
				/*
				try {
					UberStatement stmt = g.con.createStatement();
					ResultSet rs = stmt.executeQuery("select sum(traders) from trade where tid1 = "+ holdT.townID + " and (tradeOver = false or ticksToHit >= 0)");
					
					if(rs.next()) currentlyOut = rs.getInt(1);
					
					
					rs.close();
					stmt.close();
					
						
					} catch(SQLException exc)  { exc.printStackTrace(); }*/
				
			
				currentlyOut+=g.returnScholarsAbroad(holdT.townID);
				int k = 0;
					
				while(k<holdT.attackServer().size()) {
					if(holdT.attackServer().get(k).getDigAmt()>0) currentlyOut+=holdT.attackServer().get(k).getDigAmt();
					k++;
				}
				// System.out.println("Found " + currentlyOut + " traders out.");
				 // we need the tradeDearth.
				 int totalDearth=0; 
				  k = 0; 
				 UserBuilding bldgs[] = getUserBuildings(holdT.townID,"Institute");
				 UserBuilding holdB;
					while(k<bldgs.length) {
						 holdB = bldgs[k];

							if(holdB.getType().equals("Institute")){
								totalDearth+=(holdB.getCap()-holdB.getPeopleInside()-holdB.getNumLeftToBuild());
								
							}
							
								

						k++;
					}
					//System.out.println("Total dearth is " + totalDearth);
				
				if((totalDearth-currentlyOut)<number) { 
				 // now we know if we get past this,
					 // we have the room in other TCs for traders, so we can build here if there
					 // is room with numLeftToBuild.
					 setError("Not enough room in Institute.");
					 return false;
				 }
			//	System.out.println("pplinside is " + b.peopleInside + " and num left to build is  " + b.numLeftToBuild);
				if ((b.getPeopleInside()+b.getNumLeftToBuild()+number)>b.getCap()) {
					 setError("Not enough room in Institute.");

					return false;
				}
			}
			
			cost = returnPrice(holdT.getTownName(),"Scholar",number);

			 int j = 0; 
			 long res[] = holdT.getRes();
			 do {
				 if(res[j]<cost[j]) {
					 setError("You do not have the resources!");
					 return false;
				 }
				 j++;
			 } while(j<4);
		} else {
			// we don't do buildings here, that is a separate thing...
			// should probably do combat units here.
				 i = 0;
				 AttackUnit a = null;
				while(i<p.getAu().size()) {
					if(p.getAu().get(i).getName().equals(unit)) {
						a = p.getAu().get(i);
						break;
					}
					i++;
				}
				if(a==null) {
					setError("Invalid unit!");
					return false;
				}
				if(a.getExpmod()==AttackUnit.soldierExpMod&&!b.getType().equals("Arms Factory")){
					setError("You have not chosen an Arms Factory!");
					return false;
				}else if((a.getExpmod()==AttackUnit.tankExpMod||a.getExpmod()==AttackUnit.juggerExpMod)&&!b.getType().equals("Manufacturing Plant")){
					setError("You have not chosen a Manufacturing Plant!");
					return false;
				}if(a.getExpmod()==AttackUnit.bomberExpMod&&!b.getType().equals("Airstrip")){
					setError("You have not chosen an Airstrip!");
					return false;
				}
				
		
			cost = returnPrice(holdT.getTownName(),unit,number);
			if(cost==null) return false; // in case incorrect unit.
				 int j = 0; 
				 long res[] = holdT.getRes();
				 do {
					 if(res[j]<cost[j]) return false;
					 j++;
				 } while(j<4);
				 
				}
		
		
			
		
		
		
		
		// if it gets past here it must have worked.
		return true;
		// If it hasn't been kicked by now it must be a building of some sort. Now if the building already exists...
		// Let's worry about this code later - as there are multiple buildings, specifying something like "Institute" could be misleading,
		// may need an override.
		
		// Can also put combat units in here. 
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns true if at some building in the town designated by the town id,
	 * you can build the number of units specified. Units can be military or civilian.
	 * <br/><br/>
	 * Use of {@link #canBuy(String unitType, int number, int lotNum, int tid)} is encouraged in place of this method.
	 * 
	 * @param unitType the name of the unit to be built.  IE "Engineer"
	 * @param number the amount of that unit to build
	 * @param tid the ID of the town to build them in
	 * 
	 * @return True, if they can be build.  False otherwise.
	 */
	public boolean canBuy(String unitType, int number, int tid) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return canBuy(t.getTownName(),unitType,number);
	
	}
	
		/**
		 * UI Implemented.
		 * 
		 * @deprecated Use {@link #canBuy(String unitType, int number, int tid)} instead.
		 */
	public boolean canBuy(String townName, String unit, int number) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return false;
		}
		// right so I suppose we should check buildings first.

		if(number<=0) return false;

		Town holdT;
		
		holdT = g.findTown(townName,p); // so now we have the town. Let's go through civvy units first.
		if(!checkMP(holdT.townID)) return false;
		if(holdT.getPlayer().ID!=p.ID) { setError("Not your town!"); return false; }
		long cost[] = new long[5];
	if(unit.equals("Engineer")) {
		// retrieving costs...
		cost = returnPrice(holdT.getTownName(),"Engineer",number);
		 
		 int j = 0; 
		 long res[] = holdT.getRes();
		 do {
			 if(res[j]<cost[j]) return false;
			 j++;
		 } while(j<4);
	}	else	if(unit.equals("Messenger")) {
		// retrieving costs...
		cost = returnPrice(holdT.getTownName(),"Engineer",number);

		 int j = 0; 
		 long res[] = holdT.getRes();

		 do {
			 if(res[j]<cost[j]) return false;
			 j++;
		 } while(j<4);
	} 	else if(unit.equals("Trader")) {
		// retrieving costs...
		cost = returnPrice(holdT.getTownName(),"Trader",number);
		 long res[] = holdT.getRes();

		 int j = 0; 
		 do {
			 if(res[j]<cost[j]) return false;
			 j++;
		 } while(j<4);
	} else if(unit.equals("Scholar")) {
		// retrieving costs...
		cost = returnPrice(holdT.getTownName(),"Scholar",number);
		 long res[] = holdT.getRes();

		 int j = 0; 
		 do {
			 if(res[j]<cost[j]) return false;
			 j++;
		 } while(j<4);
	} else {
		// we don't do buildings here, that is a separate thing...
		// should probably do combat units here.
		
		cost = returnPrice(holdT.getTownName(),unit,number);
		 long res[] = holdT.getRes();

		if(cost==null) return false; // in case incorrect unit.
			 int j = 0; 
			 do {
				 if(res[j]<cost[j]) return false;
				 j++;
			 } while(j<4);
			 
			}
	
	
		
	
	
	
	
	// if it gets past here it must have worked.
	return true;
	// If it hasn't been kicked by now it must be a building of some sort. Now if the building already exists...
	// Let's worry about this code later - as there are multiple buildings, specifying something like "Institute" could be misleading,
	// may need an override.
	
	// Can also put combat units in here. 
}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Builds the number of unit type specified(combat only) in the town designated by
	 * the town id and in the military production facility designated by it's lotNum.
	 * 
	 * @param type 		the name of the unit you want to build
	 * @param number 	the amount of units to build
	 * @param lotnum 	the lot number of the building that will construct these units
	 * @param tid 		the ID of the town you're building in
	 * 
	 * @return True, if construction has begun.  False otherwise.
	 */	
	public boolean buildCombatUnit(String type, int number, int lotNum, int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		pushLog("buildCombatUnit(" + type + "," + number + "," + lotNum + "," + tid + ");");
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return buildCombatUnit(lotNum,t.getTownName(),number,type);
	
	}
		/**
		 * UI Implemented.
		 * <br/><br/>
		 * Builds the number of unit type specified(combat only) in the town designated by
		 * townName and in the military production facility designated by it's lotNum.
		 * 
		 * @deprecated Use {@link #buildCombatUnit(String type, int number, int lotNum, int tid)} instead.
		 */
	public boolean buildCombatUnit(int lotNum, String townName, int number, String type) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
//System.out.println("I am in this bitch.");
		long cost[] = new long[5];

		 int j = 0;
		 Town holdT = g.findTown(townName,p); Building thisAF=null;
		 ArrayList<Town> towns = p.towns();
		 ArrayList<AttackUnit> au;
		
		 if(!checkMP(holdT.townID)) return false;
		 if(holdT.getPlayer().ID!=p.ID) { setError("Not your town!"); return false; }
			UUID bid = null;
			/*try {
				UberStatement stmt = g.con.createStatement();
				ResultSet rs = stmt.executeQuery("select bid from bldg where tid = " + holdT.townID + " and slot = "+ lotNum);
				if(rs.next()) bid = rs.getInt(1);
				rs.close();
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }*/
			int i = 0;
			ArrayList<Building> bldg = holdT.bldg();
			while(i<bldg.size()) {
				if(bldg.get(i).getLotNum()==lotNum) {bid = bldg.get(i).getId(); break; }
				i++;
			}
			
			if(bid==null) { setError("Bad building lot!"); return false; }
				ArrayList<AttackUnit> AU = p.getAu();
				boolean keep = false;
				if(prog) keep=true;
				prog=false;

				UserQueueItem[] queue = getUserQueueItems(bid);
				if(keep) prog = true;
				thisAF = holdT.findBuilding(bid);
					 int k = 0; int totalQueued=0;	
					 while(k<queue.length) {
						 totalQueued+=AU.get(queue[k].returnAUtoBuild()).getExpmod()*queue[k].returnNumLeft();
						 k++;
					 }
					 
					 k=0;int popped=0;AttackUnit a; int saved = 0;
					 while(k<AU.size()) {
						 a = AU.get(k);
						 if(a.getName().equals(type)) saved = k;
						 if(!a.getName().equals("locked")&&!a.getName().equals("empty")) popped++;
						 
						 k++;
					 }
					 a = AU.get(saved);
					 Building myaf = holdT.findBuilding(bid);
					 if(a.getType()==1&&!myaf.getType().equals("Arms Factory")) {
						 setError("This is not an Arms Factory!");
						 return false;
						 
					 }else if((a.getType()==2||a.getType()==3)&&!myaf.getType().equals("Manufacturing Plant")) {
						 setError("This is not an Manufacturing Plant!");
						 return false;

						 
					 }else if((a.getType()==4)&&!myaf.getType().equals("Airstrip")) {
						 setError("This is not an Airstrip!");
						 return false;

						 
					 }
					 if(totalQueued+number*a.getExpmod()>thisAF.getQueueCap()) {
						 setError("Too many units queued already!");
						 return false;
					 }
					if(queue.length>thisAF.getSlotCap()) {
						setError("Too many slots used!");
						return false;
					}
					 
					 
				 // need to formulate combat unit costs. Let's get the unit.
				 
				  i = 0;
				  boolean foundAU = false;
				 au = holdT.getAu();
				 while(i<au.size()) {
					 a = au.get(i);
					 if(a.getName().equals(type)) {foundAU=true; break;}
					 i++;
				 }
				 if(!foundAU) {
					 setError("Not one of your attack units.");
					 return false;
				 }
				 
				 // now we have the AU. Get unit type and make bases off this.
				 // we need the total population of this AU in all towns.

				  int totalPop;
					 totalPop = GodGenerator.getTotalSize(a,p);
				 
				 i = 0;
			
				 
				 double multiplier=0;
				 double modifier = Math.pow(.75,p.towns().size());
				 multiplier=a.getExpmod()*modifier;
				 
				 //4 + .7
				 
				  i = 0;  k=0;
				 int currentlyBuilding = 0; UserBuilding b; UserQueueItem q;
					 keep = false;
					if(prog) keep=true;
					prog=false;

				UserBuilding[] bldgs = getUserBuildings(holdT.townID,"CombatProduction");
				if(keep) prog = true;
				 while(i<bldgs.length) {
					
						  j = 0;
						  b = bldgs[i]; queue = b.getQueue();
						 while(j<queue.length) {
							  q = queue[j];
							 if(q.returnAUtoBuild()==a.getSlot()) currentlyBuilding+=q.returnNumLeft();
							 j++;
						 }
					 
					 i++;
				 } // n_new(n_new+1)/2 - n_old(n_old+1)/2 is the cost to build all those units.
				 int allCalled = totalPop+currentlyBuilding;
				int totalnumber=number+allCalled;
				 double factor = multiplier*totalnumber*(multiplier*totalnumber+1)/2 - multiplier*allCalled*(multiplier*allCalled+1)/2;
				 cost[0] = (long)Math.round(25*factor); // metal
				 cost[1] = (long) Math.round(10*factor); // timber
				 cost[2] = (long) Math.round(26*factor);//manmat
				 cost[3] = (long) Math.round(9*factor); //food
				 cost[4] = -a.getExpmod(); // // need a 10, 25, 15, 30
				 
				 
				 boolean canBuild = true;
					 k = 0;
					
					
					if(thisAF.getLvl()==0) { 
						setError("Cannot build combat units in zero-leveled Arms Factories!");
						return false;
					}
				//	if(number>(b.peopleCap-(b.peopleInside+b.numLeftToBuild))) number = (b.peopleCap-(b.peopleInside+b.numLeftToBuild));
					// no population limit with these guys..
					 long res[] = holdT.getRes();

					do {
						if(res[k]<cost[k]) canBuild = false; // so if resources aren't enough...
						k++;
					} while(k<cost.length-1);
					
					
					if(canBuild) {
						
						 k = 0;
						do {
							res[k]-=cost[k];
						//	System.out.println("After " + holdT.res[k] + " with a cost of " + cost[k]);

							k++;
						} while(k<cost.length-1);
						holdT.setRes(res);
					//	holdT.getTotalEngineers()-=number*cost[4];
					//	player.totalPopulation-=number*cost[4]; // Remember cost is -1.
						// Don't add population here. It gets added by buildserver when they are actually
						// built.
						thisAF.addCombatUnit(a.getSlot(),number,holdT,cost);
				//		thisAF.modifyUnitTicksForQueue(holdT.getAu(),holdT.getTotalEngineers());

						notifyViewer();

						return true;
					} else {
						setError("Not enough resources.");
						return false;
					}
			
		 
		

	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns an array of UserTowns representing the towns where you've sent support.
	 * These UserTownser have minimal info other than name, place
	 * and support units attached.
	 * 
	 * @param tid	the ID of the town where the support originated.
	 * 
	 * @return an array of {@link UserTown UserTowns} with the info about the support units you have there.
	 */
	public UserTown[] getUserTownsWithSupportAbroad(int tid) {
		//[{tid : int, townName : string, AU : int, size : int}]
		if(prog&&!p.isCompleteAnalyticAPI()) {
			setError("You do not have the Complete Analytics API!");
			return null;
		}
		Town t; AttackUnit a;  
		ArrayList<UserTown>toRet=null;
		 toRet = new ArrayList<UserTown>();
		 ArrayList<UserAttackUnit> sau=null;
		 ArrayList<AttackUnit> au;
	 	ArrayList<Town> towns = g.getTowns();
	 	int i = 0;
	 	ArrayList<Integer> townIDs = new ArrayList<Integer>();
	 	
	 	while(i<towns.size()) {
	 		t = towns.get(i);
	 		au = t.getAu();
	 		int j = 0;
	 		if(t.getDigTownID()==tid) townIDs.add(t.townID);
	 		else
	 		while(j<au.size()) {
	 			if(au.get(j).getSupport()>0&&au.get(j).getOriginalPlayer().ID==p.ID&&au.get(j).getOriginalTID()==tid) {
	 				townIDs.add(t.townID);
	 				break;
	 			}
	 			j++;
	 		}
	 		i++;
	 	}
	 	i=0;
	 	while(i<townIDs.size()) {
	 		t = g.findTown(townIDs.get(i));
			int k = 0;
			au = t.getAu();
			sau = new ArrayList<UserAttackUnit>();
			while(k<au.size()) {
				a = au.get(k);
				
				if(a.getSupport()>0&&a.getOriginalTID()==tid) {
					
						//public UserAttackUnit(String name, int slot, int originalPlayerID, int originalSlot,
					//	int originalTID,
					//	int support, String originalPlayer) {
						sau.add(new UserAttackUnit(a.getName(),a.getSlot(),a.getOriginalPlayer().ID,a.getOriginalSlot(),
								a.getOriginalTID(),a.getSupport(),a.getOriginalPlayer().getUsername(),0,a.getSize()));
					
				}
				k++;
			}
			if(t.getDigTownID()==tid) { // if it's a dig, we add civilains.
				
				sau.add(new UserAttackUnit("Archaeologist",0,t.getPlayer().ID,0,t.getDigTownID(),1,t.getPlayer().getUsername(),0,t.getDigAmt()));
			}
			
			
				// now we make the array.
				int z = 0;
				UserAttackUnit arr[] = new UserAttackUnit[sau.size()];
				while(z<arr.length) {
					arr[z]=sau.get(z);
					z++;
				}
				toRet.add(new UserTown(arr,t.getPlayer().getUsername(),t.getTownName(),t.townID,t.getPlayer().ID));
				
			i++;
	 	}
	 	/*
		try {
		UberStatement stmt = g.con.createStatement();
		ResultSet rs;
		
		
			rs = stmt.executeQuery("select tid from supportAU where ftid = " + tid);
			
			while(rs.next()) {
				t = new Town(rs.getInt(1),g);
				int k = 0;
				au = t.getAu();
				sau = new ArrayList<UserAttackUnit>();
				while(k<au.size()) {
					a = au.get(k);
					
					if(a.getSupport()>0&&a.getOriginalTID()==tid) {
						
							
							int z = 0;
							int weap[] = new int[a.getWeap().length];
							while(z<a.getWeap().length) {
								weap[z]=a.getWeap()[z];
								z++;
							}
							sau.add(new UserAttackUnit(a.getAccuracy(),a.getAmmo(),a.getArmor(),a.getCargo(),a.getCivType(),
									a.getConcealment(),a.getExpmod(),a.getFirepower(),a.getGraphicNum(),a.getHp(),a.getLotNum(),
									a.getName(),a.getOriginalPlayer().ID,a.getOriginalSlot(),a.getOriginalTID(),a.getPopSize(),a.getSize(),
									a.getSlot(),a.getSpeed(),a.getSupport(),weap,p.getUsername()));
						
					}
					k++;
				}
				
				
					// now we make the array.
					int z = 0;
					UserAttackUnit arr[] = new UserAttackUnit[sau.size()];
					while(z<arr.length) {
						arr[z]=sau.get(z);
						z++;
					}
					toRet.add(new UserTown(arr,t.getPlayer().getUsername(),t.getTownName(),t.townID,t.getPlayer().ID));
					
				
			}
			rs.close();stmt.close(); 
		} catch(SQLException exc) { exc.printStackTrace(); } */
		
		UserTown[] r = new UserTown[toRet.size()];
		 i = 0;
		while(i<toRet.size()) {
			r[i]=toRet.get(i);
			i++;
		}
		return r;
	}
	/**
	 * UI Implemented. 
	 * <br/><br/>
	 * Kills the number of units of the type you specify
	 * in the townID you specify. Recommended that you see a psychiatrist first
	 * before pursuing such drastic actions.
	 * 
	 * You do get some resources back from killed units based on the levels of your current
	 * Recycling Yards.
	 * 
	 * @param type		the type of unit to kill
	 * @param number	the amount of units to be killed
	 * @param tid		the ID of the town the units are in
	 * 
	 * @return True, if the units were killed.  False otherwise.
	 */
	public boolean killMyself(String type, int number, int tid) {
		if(prog&&!p.isAttackAPI()) {
			setError("You do not have the Attack API!");
			return false;
		}
		int i = 0; boolean found = false;
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) {
			setError("Not your town!");
			return false;
		}
		 i = 0; AttackUnit a;
		 ArrayList<AttackUnit> au = t.getAu();
		while(i<au.size()) {
			a = au.get(i);
			if(a.getName().equals(type)) {
				if(a.getSupport()>0) {
					setError("Cannot cause another player's units to kill themselves. Sorry.");
					return false;
				}
				
				if(number>a.getSize()) number=a.getSize();
				if(number<=0) {
					setError("Cannot kill negative or zero units. Learn math.");
					return false;
				}
				int oldSize = a.getSize();
				t.setSize(i,a.getSize() - number);
				//	public long[] returnPrice(String townName, String unitType, int number) {

				long cost[] = returnPrice(a.getName(),number,t.townID,true);
				if(a.getSize()<0) { t.setSize(i,0);
					cost = returnPrice(a.getName(),oldSize,t.townID,true);
				}
				
				if(a.getSize()>oldSize){ t.setSize(i,oldSize); 
					int j=0;
					while(j<cost.length) { cost[j]=0; j++; }
				}
				
				UserBuilding[] rec = getUserBuildings(t.townID,"Recycling Center");
				int totallevel = 0;
				for(UserBuilding b: rec) {
					totallevel+=b.getLvl();
				}
				double totalperc = .0166*totallevel;
				if(totalperc<.0) totalperc = 0;
				if(totalperc>.5) totalperc=.5;
				int j = 0;
				synchronized(t.getResBuff()) {
					while(j<cost.length) { 
						t.getResBuff()[j]+=cost[j]*totalperc;

						j++;
					}
				}
				// Just some reasonable checks. We duplicate our math to avoid having to recall new town AU objects that are updated
				// from the database.
				return true;
				
			}
			i++;
		}
		setError("This unit type doesn't exist. Learn spelling.");
		return false;
	}
	/**
	 * Automatically finds the largest military production facility in the town capable of building your unit and builds
	 * the number of the unit type(military only) described in the town designed
	 * by town id if possible.
	 */
	private boolean buildCombatUnit(String type, int number,int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		pushLog("buildCombatUnit("+type+","+number+","+tid+");");
		if(t.getPlayer().ID!=p.ID) return false;
		return buildCombatUnit(t.getTownName(),number,type);
		
	}
		/**
		 * @deprecated
		 * Automatically finds the largest building for your unit type in the town and builds
		 * the number of the unit type(military only) described in the town designed
		 * by townName if possible.
		 * 
		 * NOTE THAT THIS METHOD IS EXTREMELY LAGGY.
		 */
	private boolean buildCombatUnit(String townName, int number, String type) {
		
		if(prog&&!p.isBuildingAPI()&&!QuestListener.partOfQuest(p,"RQ2")) {
			setError("You do not have the Building API!");
			return false;
		}
		
		pushLog("buildCombatUnit("+townName+","+number+","+type+");");

		long cost[] = new long[5];

		 int j = 0;
		 ArrayList<AttackUnit> au;
		 Town holdT = g.findTown(townName,p);

		 int i = 0;
		 AttackUnit AU = null;
		 au = holdT.getAu();
		 while(i<au.size()) {
			 AU = au.get(i);
			 if(AU.getName().equals(type)) break;
			 i++;
		 }
		 boolean keep = false;
		 if(prog) keep = true;
		 prog=false;
		 boolean haveIt = false;
		 if(AU.getType()==1) {
			 haveIt = haveBldg(townName,"Arms Factory");
		 }
		 else if(AU.getType()==2||AU.getType()==3) {
			 haveIt = haveBldg(townName,"Manufacturing Plant");

		 } else if(AU.getType()==4) {
			 haveIt = haveBldg(townName, "Airstrip");
		 }
		  
		 if(keep) prog=true;
			 if(haveIt) {
					if(!checkMP(holdT.townID)) return false;

				 // need to formulate combat unit costs. Let's get the unit.
				 
				 
				 // now we have the AU. Get unit type and make bases off this.
				 // we need the total population of this AU in all towns.

				  int totalPop;
					 totalPop = GodGenerator.getTotalSize(AU,p);
				
				 				 i = 0;
			
				 
				 double multiplier=0;
				 double modifier = Math.pow(.75,p.towns().size());
				 multiplier=AU.getExpmod()*modifier;
				 
				 //4 + .7
				 
				  i = 0;
				 int currentlyBuilding = 0; Building b; QueueItem q;
				 int x = 0; Town t=null;
				 ArrayList<Town> towns = p.towns();
				 ArrayList<Building> bldg;
				 ArrayList<QueueItem> queue;
				 while(x<towns.size()) {
					 t = towns.get(x);
					  i = 0;
					  bldg = t.bldg();
				 while(i<bldg.size()) {
					 if((bldg.get(i).getType().equals("Arms Factory")||bldg.get(i).getType().equals("Manufacturing Plant")
								||bldg.get(i).getType().equals("Airstrip"))) {
						 
						 int k = 0;
						  b = bldg.get(i);
						  queue=b.Queue();
						 while(k<queue.size()) {
							  q = queue.get(k);
							 if(q.getAUtoBuild()==AU.getSlot()) currentlyBuilding+=q.getAUNumber();
							 k++;
						 }				
						 }
					 i++;
				 } // n_new(n_new+1)/2 - n_old(n_old+1)/2 is the cost to build all those units.
				 x++;
			 }
				 int allCalled = totalPop+currentlyBuilding;
					int totalnumber=number+allCalled;
					 double factor = multiplier*totalnumber*(multiplier*totalnumber+1)/2 - multiplier*allCalled*(multiplier*allCalled+1)/2;
				 cost[0] = (long)Math.round(25*factor); // metal
				 cost[1] = (long) Math.round(10*factor); // timber
				 cost[2] = (long) Math.round(26*factor);//manmat
				 cost[3] = (long) Math.round(9*factor); //food
				 cost[4] = -AU.getExpmod(); // // need a 10, 25, 15, 30
				 
				 
				 boolean canBuild = true;
					int k = 0;
					
					 b = null;
					Building holdB;
					bldg = holdT.bldg();
					while(k<holdT.bldg().size()) {
						 holdB =bldg.get(k);
						if(((holdB.getType().equals("Arms Factory")||holdB.getType().equals("Manufacturing Plant")
								||holdB.getType().equals("Airstrip"))&&b!=null&&holdB.getLvl()>b.getLvl())
								||((holdB.getType().equals("Arms Factory")||holdB.getType().equals("Manufacturing Plant")
										||holdB.getType().equals("Airstrip"))&&b==null))
							
							b = holdB;
				
			
					k++;	
					}  // Find largest one.
					
					if(b.getLvl()==0) return false; 
				//	if(number>(b.peopleCap-(b.peopleInside+b.numLeftToBuild))) number = (b.peopleCap-(b.peopleInside+b.numLeftToBuild));
					// no population limit with these guys..
					k=0;		
					long res[] = holdT.getRes();

					do {
						if(res[k]<cost[k]) canBuild = false; // so if resources aren't enough...
						k++;
					} while(k<cost.length-1);
					
					
					if(canBuild) {
						if(prog) keep=true;
						prog=false;
						ArrayList<AttackUnit> theAU = p.getAu();

						UserQueueItem[] que = getUserQueueItems(b.getId());
						if(keep) prog = true;
						  k = 0; int totalQueued=0;	
						 while(k<que.length) {
							 totalQueued+=theAU.get(que[k].returnAUtoBuild()).getExpmod()*que[k].returnNumLeft();
							 k++;
						 }
						 
						 k=0;int popped=0;AttackUnit a; int saved = 0;
						 while(k<theAU.size()) {
							 a = theAU.get(k);
							 if(a.getName().equals(type)) saved = k;
							 if(!a.getName().equals("locked")&&!a.getName().equals("empty")) popped++;
							 
							 k++;
						 }
						 a = theAU.get(saved);
						 if(totalQueued+number*a.getExpmod()> b.getCap()) {
							 setError("Too many units queued already!");
							 return false;
						 }
						 k = 0;
						do {
							res[k]-=cost[k];
							k++;
						} while(k<cost.length-1);
						holdT.setRes(res);
					//	holdT.getTotalEngineers()-=number*cost[4];
					//	player.totalPopulation-=number*cost[4]; // Remember cost is -1.
						// Don't add population here. It gets added by buildserver when they are actually
						// built.
						
						b.addCombatUnit(AU.getSlot(),number,holdT,cost);
					//	b.modifyUnitTicksForQueue(holdT.getAu(),holdT.getTotalEngineers());

						notifyViewer();

						return true;
					} else return false;
			 }
			 setError("You are not using or do not have the proper building!");
		
		 return false;

	}
	
	/**
	 * 
	 * Returns in seconds, the tick rate of God. This is the unit that all timers in the game
	 * are in when you use programming. For instance, the eta() method of UserRaid returns
	 * a number of ticks till you hit the guy - these ticks are in units of GCF, which is some
	 * number of seconds. So if the GCF is 10, and the ETA given by a UserRaid is 9, then the
	 * actual time till the raid hits is 10*9=90 secs.
	 * @return
	 */
	public double getGameClockFactor() {
		return GodGenerator.gameClockFactor;
	}
	
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Sends a mission, or checks if a mission could be sent with the auAmts specified, attackType chosen, and bomb target designated(ignored if not a bombing run).
	 * <br/><br/>
	 * Note: If you attack a city with an Airship above it and that city is an ID city, you will hit the Airship.
	 * If you hit a player-owned city with an Airship above it, you will hit the city but can kill the Airship above it
	 * if you wipe all of it's troops out.
	 * <br/><br/>
	 * If you aim at an Airship directly, you may not commit Sieges or Glassings on it.
	 * <br/><br/>
	 * valid attackTypes:
	 * <ul>
	 * <li>attack</li>
	 * <li>siege</li>
	 * <li>glass</li>
	 * <li>strafe</li>
	 * <li>invasion</li>
	 * <li>scout</li>
	 * <li>offsupport</li>
	 * <li>support</li>
	 * <li>debris</li>
	 * <li>dig</li>
	 * <li>excavation</li>
	 * <li>blockade</li>
	 * </ul>
	 *
	 * @param yourTownID  	ID of the town you're sending from.
	 * @param enemyx  		The x coordinate of the target town.
	 * @param enemyy 	 	The y coordinate of the target town.
	 * @param auAmts  		The amount of units to send.  Should be of equal in length to your number of AU + any support units you wish to send.
	 * @param attackType	The type of mission to send.  See above for valid types.
	 * @param target  		An array of building names to target, if bombing.  Ex. {"Command Center","Trade Center"}
	 * @param name  		The name of this mission.  This is purely cosmetic.
	 * @param send  		If true, attack() tries to send the mission.  If false, attack() checks if this mission could be sent.
	 * 
	 * @return True, if the mission was, or could be sent.  False otherwise.
	 */
	public boolean attack(int yourTownID, int enemyx, int enemyy, int[] auAmts, String attackType, String[] target,String name, boolean send) {
		if(prog&&!p.isAttackAPI()&&!QuestListener.partOfQuest(p,"RQ3")&&!QuestListener.partOfQuest(p,"RQ4")&&!QuestListener.partOfQuest(p,"RQ5")) {
			setError("You do not have the Attack API!");
			return false;
		}
		
		if(target==null) target = new String[0];
		pushLog("attack(" +yourTownID+","+enemyx+","+  enemyy +","+  PlayerScript.toJSONString(auAmts) +","+  attackType+","+  PlayerScript.toJSONString(target)+","+ name+","+send+");" );
		
		if(name.contains("'")) {
			setError("God hates apostrophes.");
			return false;
		}
		
		int holdNumbers[] = auAmts;
		
		// Gotta get the town first.
		Town t1 = g.findTown(yourTownID);
		Player t1p = t1.getPlayer();
		PrintWriter out = t1p.God.out;

		if(attackType.equals("invasion")&&t1.isZeppelin()) {
			setError("You cannot invade with an Airship!");
			return false;
		}
		
		if(!checkMP(t1.townID)) {
			setError("Insufficient permissions.");
			return false;
		}

		if(t1p.ID!=p.ID) {
			setError("Not your town!");
			return false;
		}
		if(!t1.slotsFree()) {
			setError("No slots. Upgrade Command Center!");
			return false; // no slots, no attacks, bitch.
		}
		
		int x = enemyx;
		
		int y = enemyy;
		int t1x = t1.getX();
		int t1y = t1.getY();

		int k = 0; // to make sure can only go up to six.
				
		AttackUnit hau; int totalsize=0, limit = holdNumbers.length;
		boolean zeroes = true, negatives = false;
		ArrayList<AttackUnit> t1au = t1.getAu();
		if(attackType.equals("excavation")) limit--;
		while(k<limit) {
			
			// Next we need to check if these numbers go over the max units in
			// the town. If they do, send the max units instead.
			if(k<t1au.size()) {
				  hau =t1au.get(k);
				  //System.out.println("Checking au support = "+hau.getSupport());
				  if((attackType.equals("dig")||attackType.contains("support")||attackType.equals("excavation")||attackType.equals("blockade"))
						  &&hau.getSupport()>0&&holdNumbers[k]>0) {
					  setError("You cannot send other players' support units on supporting, dig, excavation, or blockade missions!");
					  return false;
				  }
				if(hau.getSize()<holdNumbers[k]) holdNumbers[k]=hau.getSize();
				if(holdNumbers[k]>0) zeroes=false; // needs to be after the size-mod if overflowing.
				if(holdNumbers[k]<0) holdNumbers[k]=0; // No less than zero crap..
				 // Simple as cake.
			//	holdLowSpeed+=(holdNumbers[k]*hau.getExpmod()*hau.getTrueSpeed(t1.getPlayer()));
				totalsize+=holdNumbers[k]*hau.getExpmod();
			}
			
			k++;
		}
		
		if(zeroes&&(!(attackType.equals("dig")||attackType.equals("excavation"))||holdNumbers[holdNumbers.length-1]==0)) {
			setError("Can't send an empty raid.");
			return false; // not sending a raid of nada.
		}
		/*if(negatives) {
			setError("No such thing as negative units.");
			return false;
		}*/
		Town Town2 = g.findTown(x,y); // findTown auto detects the town at the x,y, not the Zeppelin, if there is one.

		if(Town2 == null) {
			setError("Could not find the town!");
			return false;
		}
		
		boolean Genocide = false, Bomb = false, invade = false, debris = false, dig=false;
		int support = 0, scout = 0, digAmt=0;
		if(attackType.equals("attack")) { } // alter if UberStatement for offsupporters down below to include
		// invasion and scouting also!
		else if(attackType.equals("genocide")||attackType.equals("siege")) Genocide = true; 
		else if(attackType.equals("debris")) debris = true;
		else if(attackType.equals("strafe")) {	
			Bomb = true;
		}
		else if(attackType.equals("glass")) {
			Bomb = true;
			Genocide = true;
		}
		else if(attackType.equals("support")) {support = 1;}
		else if(attackType.equals("offsupport")) {support=2;}
		else if(attackType.equals("blockade")) {support = 3;}
		else if(attackType.equals("dig")||attackType.equals("excavation")) {
			if(prog&&!p.isdigAPI()) {
				setError("You need the Dig API in order to use this!");
				return false;
			}
			if(t1.getBlockades().size()>0) {
				boolean hasOff = false;
				for(int i = 0;i<holdNumbers.length-1;i++) {
					if(holdNumbers[i]>0) {
						hasOff = true;
						break;
					}
				}
				if(!hasOff) {
					setError("Town blockaded.");
					return false;
				}
			}
			
			support = 1; dig=true;
			int z=0;UserBuilding b[];
			if(attackType.equals("excavation"))
				b = getUserBuildings(t1.townID, "Command Center");
			else	
				b = getUserBuildings(t1.townID, "Institute");
			int totalScholars=0;
			while(z<b.length) {
				totalScholars+=b[z].getPeopleInside();
				z++;
			}
			int req = GodGenerator.digScholarRequirement;
			if(attackType.equals("excavation")) req = holdNumbers[holdNumbers.length-1];
			if(totalScholars<req&&!(QuestListener.partOfQuest(p,"NQ4")&&p.getVersion().equals("civilian"))) {
				if(attackType.equals("excavation"))
					setError("You do not have enough Engineers!");
				else
					setError("You do not have enough Scholars!");
				return false;
			}
			z=0; Building actb; UserBuilding bl;
			int digToTake = req;
			if(QuestListener.partOfQuest(p,"NQ4")&&p.getVersion().equals("civilian")) {
				digToTake=1;
			}
			while(z<b.length) {
				bl = b[z];
				 actb = t1.findBuilding(bl.getId());
				if(actb.getPeopleInside()>digToTake-digAmt) {
					actb.setPeopleInside(actb.getPeopleInside()-(digToTake-digAmt));
					digAmt+=(digToTake-digAmt);
				} else {
					digAmt+=actb.getPeopleInside();

					actb.setPeopleInside(0);
				}
				 if(digAmt>=digToTake) break;
				z++;
			}
			
		}
		else if(attackType.equals("scout")) { scout = 1; }
		else if(attackType.equals("invasion")) {
		
			if(p.getTownTech()<=p.towns().size()) { // don't want players invading when townTech <= town size!
				setError("Too many towns.  Level your Town Tech");
				return false;
			}
			
			if(Town2.isResourceOutcropping()) {
				setError("You cannot invade a Resource Outcropping!");
				return false;
			}
			
			int aggregate=0; int z = 0;
			/*
				try {
					UberStatement stmt = g.con.createStatement();
					ResultSet rs = stmt.executeQuery("select sum(lvl) from bldg where tid = " + t1.townID + " and name = 'Command Center';");
					if(rs.next()) aggregate = rs.getInt(1);
					rs.close();
					stmt.close();
				} catch(SQLException exc) { exc.printStackTrace(); }
			*/
			 ArrayList<Building> b = t1.bldg();
			 while(z<b.size()) {
				 Building hb = b.get(z);
				 if(hb.getType().equals("Command Center")) {
					aggregate=(hb.getLvl()+2)*3;
					break;
				 }
				 z++;
			 }
				
			 double distance = Math.sqrt((x-t1x)*(x-t1x) + (y-t1y)*(y-t1y));
			 if(distance>aggregate) {
				 setError(" You can only invade " + (aggregate) + " spaces out. Level up your Command Center.");

				 return false;
			 }
			 
			invade = true;
		
		}
		else {
			setError("Invalid attack type.");
			return false; // if they don't get the code right, screw 'em.
		}
		
		
		boolean keep=!!prog;//duplicate prog into keep
		prog=false;			//set prog to false so we can call getAttackETA
		int ticksToHit = getAttackETA(t1.townID, enemyx,enemyy,auAmts);
		prog = !!keep;		//set prog back
		// The only two cases that matter: You find the town at the x,y, or you'll find a Zeppelin.
		// If it's a zeppelin, it's easy to deal with, if it's a town, it's easy to deal with.
		if(Town2.townID==0) {
			setError("Town doesn't exist!");
			return false;
		}
		
		Player Town2p = Town2.getPlayer();
		
		if(dig&&Town2p.ID!=5) {
			setError("You can only dig in an Id town!");
			return false;
		}
		if(support==3&&Town2p.ID==5){
			setError("You cannot blockade Id towns!");
			return false;
		}
		if(support==3&&Town2p.isAllied(t1p)){
			setError("You cannot blockade your allies!");
			return false;
		}
		if(attackType.equals("excavation")&&!Town2.isResourceOutcropping()) {
			
			setError("You can only excavate in a Resource Outcropping!");
			return false;
		}
		

		if(Town2.getX()==t1.getX()&&Town2.getY()==t1.getY()&&attackType.contains("support")) {
			// This means zeppelin is directly overhead. 
			Town possZepp = g.findZeppelin(x,y);
			if(possZepp.townID!=0) {
				// this means your zeppelin is just overhead.
				Town2=possZepp; // You probably mean to support it!
				Town2p = Town2.getPlayer();
			}
		}
		if(Town2.isZeppelin()&&!Genocide) {
			setError("You cannot commit multi round attacks on Airships!");
			return false;
			
		}
		if(!t1p.isQuest()&&!t1p.isNoob()&&Town2p.isNoob()&&Town2p.ID!=5&&!Town2p.isQuest()&&Town2p.ID!=t1p.ID&&!debris) {
			// quests can attack any time...
			setError("NOOB PROTECTION!");
			return false;
			
		} else if(t1p.isNoob()&&!Town2p.isQuest()&&Town2p.ID!=5&&!debris&&!Town2p.isNoob()&&send) {
			
			// If you are under noob protection and you are attacking a player that is not a quest and not Id and not under noob protection
			// then you lose your noob protection.
			
			t1p.playedTicks=Player.noobDuration;
		}
		
		k = 0;

		ArrayList<AttackUnit> au = new ArrayList<AttackUnit>();
		//System.out.println("Where do I die?");
		k = 0; AttackUnit addThis;
		while(k<limit) { // making sure it satisfies  reqs here is all.
			// if we combined this loop then units would be lost if later in the loop there was an
			// error found. If the user only sends his own aus, he won't address supp aus in his array.
			// so this loop is set up to either goto the holdnumbers max. If
			// not, then index out of bounds on holdnumbers occurs.
			 addThis = t1au.get(k).returnCopy();
			
			addThis.setSize(holdNumbers[k]);

			if(addThis.getSupport()>0&&addThis.getOriginalPlayer().ID!=p.ID&&(support>0||dig)&&addThis.getSize()>0){
				setError("You cannot send another player's unit to another location as support or in a blockade.");
				return false; 
			}
			// You cannot send another player's unit to another location as
			// support. See, since support is >0 this is a supporting run, and you can't move other player's supporting
			// units(foreign aus identified by au.support>0) from their original destination protection place.
			if(addThis.getSupport()==1&&addThis.getSize()>0){
				setError("A unit you are sending is not an offensive support unit.");
				return false;
			}
			// If this is a support unit, but it's not an offensive one(i.e. support=2), then this
			// user cannot send it anywhere.
			if(scout==1&&addThis.getSize()>0&&addThis.getType()!=1) {
				setError("You must send soldiers on a scouting mission!");
				return false; 
			}
			// if this happens to be a scouting mission, and you are sending some of this unit type,
			// and it is NOT a soldier unit, then please, go away!
			k++;
		}
		//System.out.println("Got here1.");

		if(send) {

		k=0;
		while(k<t1au.size()) {
			// by using all au, not just the array of holdNumbers, we insure
			// that the attack lineup remains intact. AttackUnits that aren't
			// mentioned will still send zeroes.

			addThis = t1au.get(k).returnCopy();

			if(Town2p.ID!=t1p.ID&&support==1) addThis.makeSupportUnit(addThis.getSlot(),t1p,t1.townID);
			else if(Town2p.ID!=t1p.ID&&support==2) addThis.makeOffSupportUnit(addThis.getSlot(),t1p,t1.townID);
			// so if this is a supporting run, and you aren't sending to your own town, these units get
			// marked as "foreign."
			au.add(addThis);
			if(k<limit) {
				addThis.setSize(holdNumbers[k]);
	
				t1.setSize(k,t1au.get(k).getSize() - holdNumbers[k]);
				
			}
			
			else addThis.setSize(0);

			k++;
		}

			k=0;
			Raid holdAttack = new Raid(Math.sqrt((t1x-x)*(t1x-x) + (t1y-y)*(t1y-y)), ticksToHit, t1, Town2, Genocide, Bomb,support,invade,name,debris,au,digAmt); // digAmt may not be the requirement,
			// but it'll always be zero if dig isn't on!
			
			if(scout==1) holdAttack.makeScoutRun(); 
			else {									// never going to be a bomb+scout run.
				if(Bomb) holdAttack.setBombTarget(target);
				else holdAttack.setBombTarget(new String[0]);
			}
			
			ArrayList<Raid> blockades = t1.getBlockades();
			if(blockades.size()>0) {
				//if we have blockades, it means we have bad guys here
				//we have to set up a fake town to hold these blockades so that they can be attacked
				ArrayList<AttackUnit> AU = blockades.get(0).getAu(),
									  AUafter;
				for(int i = 1;i<blockades.size();i++) {
					for(AttackUnit a : blockades.get(i).getAu()) {	//first, we have to tally up and
						boolean found = false;						//colleate all the au so we have
						for(AttackUnit bu : AU) {					//no copies
							if(a.getName().equals(bu.getName())) {
								found = true; 
								bu.setSize(bu.getSize()+a.getSize());
							}
						}
						if(!found) {
							AU.add(a);
						}
					}
				}
				
				Player[] fakes = t1p.generateFakePlayers(1, 1, 0, 0); fakes[0].setAu(AU);
				Town town = fakes[0].towns().get(0); town.setTownName("Blockade surrounding your town");
				town.setAu(AU);
				fakes[0].setAu(AU);
				boolean unitsLeft;
				do {
					Raid raid = new Raid(0,0,t1,town,false,false,0,false,"",false,holdAttack.getAu(),holdAttack.getDigAmt());
					GodGenerator.combatLogicBlock(raid,"Your town is blockaded by a hostile force.");
					holdAttack.setAu(raid.getAu());
					holdAttack.setDigAmt(raid.getDigAmt());
					raid.deleteMe();
					unitsLeft = holdAttack.getDigAmt()>0;
					if(!unitsLeft) {
						for(AttackUnit a : holdAttack.getAu()) {
							if(a.getSize()>0) {
								unitsLeft = true;
								break;
							}
						}
					}
					boolean breakOut = false;
					if(unitsLeft) {
						breakOut = true;
						for(AttackUnit a : town.getAu()) {
							if(a.getSize()>0) {
								breakOut=false;
								break;
							}
						}
					}
					if(breakOut) break;
				} while(unitsLeft);
				AUafter = town.getAu();
				for(int i = 0; i<AUafter.size();i++) {
					AttackUnit holdUnit = AUafter.get(i);
					int holdOld = AU.get(i).getSize();
					for(int j = 0; i<blockades.size();i++) {
						Raid r = blockades.get(j);
						for(int l = 0;l<r.getAu().size();l++) {
							AttackUnit a = r.getAu().get(l);
							if(a.getName().equals(holdUnit.getName())) {
								//if holdOld is 0, then there aren't any in the blockade, so we shouldn't mess with them
								//especially since dividing by 0 is awesome!
								if(holdOld>0) a.setSize((int)Math.round(a.getSize()*(holdUnit.getSize()/holdOld)));
							}
						}
					}
				}
				t1p.deleteFakePlayers(fakes);
				t1.checkForDeadBlockades();
				if(!unitsLeft) {
					return true;
				}
			}
			
			//	public boolean runMethod(String methodName, Object... params) {
			UserRaid theRaid =getUserRaid(holdAttack.getId());
			holdAttack.getTown2().getPlayer().getPs().runMethod("onIncomingRaidDetectedCatch",theRaid);
			ArrayList<QuestListener> list =  p.getEventListenerList("onRaidSent");
			if(list!=null) {
				for(QuestListener q:list) {
					q.onRaidSent(holdAttack,prog);
				}
			}
		}
		
		return true;
		
	}
		/**
		 * @deprecated Use {@link #attack(int yourTownID, int enemyx, int enemyy, int[] auAmts, String attackType, String[] target,String name, boolean send)} instead
		 */
	public boolean attack(String yourTownName, int enemyx, int enemyy, int[] auAmts, String attackType, String[] target,String name) {
		// So if you are part of RQ3-5 or BQ8, you should get through no matter what.
		// Get through = RQ3 + RQ4 + RQ5 + BQ8. Then not getting through is ! that,
		// 
		
		if(target==null) target = new String[0];
		if(prog&&!p.isAttackAPI()&&!QuestListener.partOfQuest(p,"RQ3")&&!QuestListener.partOfQuest(p,"RQ4")&&!QuestListener.partOfQuest(p,"RQ5")&&!QuestListener.partOfQuest(p,"BQ8")) {
			setError("You do not have the Attack API!");
			return false;
		}
		
		// Gotta get the town first.
		Town t1 = g.findTown(yourTownName,p);
		
		return attack(t1.getTownID(), enemyx, enemyy, auAmts, attackType, target, name, true);
	}
	
	
	
	
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Renames the town with ID tid, if it's yours.
	 * 
	 * @param tid	the ID of the town to be renamed
	 * @param name	the new name to give the town
	 * 
	 * @return True, if the town was renamed.  False otherwise.
	 */
	
	public boolean renameTown(int tid, String name) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		Town t= g.findTown(tid);
		if(t.townID==0) {
			setError("Invalid tid!");
			return false;
		}
		if(t.getPlayer().ID!=p.ID) {
			setError("Someone else's tid!");
			return false;
		}
		if(name.contains("'")){
			setError("Error. Nobody loves you. (Don't use apostrophes!)");
			return false;
		}
		if(name.contains("Outcropping")&&p.ID!=5&&!p.isQuest()) {
			setError("Outcropping is a special name reserved for resource outcroppings.");
			return false;
		}
		if(name.contains("DATA CORRUPT-ID")){
			setError("Error. You really tried to screw us? Kill yourself.");
			return false;
		}
		
		t.setTownName(name);
		return true;
		
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Sets the Capital TID of your player. Can only be set when you have lost your capital city.
	 * 
	 * @param tid the ID of the new capital city
	 * 
	 * @return True, if the new capital was set.  False otherwise.
	 */
	
	public boolean setCapitalCity(int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
			if(p.getCapitaltid()==-1) {
			
				
				p.setCapitaltid(tid);
				return true;
				
				
			} else {
			Town t  = g.findTown(p.getCapitaltid());
			if(t.getPlayer().ID!=p.ID) {
				
				Town newT = g.findTown(tid);
				if(newT.getPlayer().ID==p.ID) {
				p.setCapitaltid(tid);
				return true;
				}
				
				
			}
			}
			
			
		
		
			
		
	
		return false;
	}
	/**
	 * UI Implemented.
	 * For assholes who can't be bothered to use tids.
	 */
	
	public int getTradeETA(int x1,int y1, int x2, int y2) {
		if(prog&&!p.isAdvancedTradingAPI()) {
			setError("You do not have the Advanced Trading API!");
			return -1;
		}
		Town t1 = g.findTown(x1,y1);
		Town t2 = g.findTown(x2,y2);
		if(t1.townID==0||t2.townID==0) {
			setError("Invalid coordinates!");
			return -1;
		} else return getTradeETA(t1.townID,t2.townID);
		
		
	}
	
	/**
	 * UI Implemented.
	 * 
	 * Returns in ticks the ETA of a trade. if otherTownID is your own, it's assumed
	 * to be a stock market trade!
	 */
	public int getTradeETA(int yourTownID, int otherTownID) {
		if(prog&&!p.isAdvancedTradingAPI()) {
			setError("You do not have the Advanced Trading API!");
			return -1;
		}
		int i = 0; Town town1=null; boolean found = false;
		town1 = g.findTown(yourTownID);
		if(town1.getPlayer().ID!=p.ID) { setError("Not your town!"); return -1; }
		
		Town town2 = g.findTown(otherTownID); // No use to cycle through players with town1.
		Player t1p = town1.getPlayer();
		int t1x = town1.getX(); int t1y = town1.getY();
		int t2x = town2.getX(); int t2y = town2.getY();

		double factor = 1-(t1p.God.Maelstrom.getTraderEffect(t1x,t1y));
		//System.out.println("Time before: " +(int) Math.round(((double) GodGenerator.stockMarketTime*(1-.1*(town1.player.tradeTech-1)))) + " factor after: " + (int) Math.round(((double) GodGenerator.stockMarketTime*(factor))) );
		if(factor<.01) factor = .01;
		int ticksToHit=0;
		if(yourTownID==otherTownID) {
			ticksToHit=(int) Math.round((((double) GodGenerator.stockMarketTime*(factor))/GodGenerator.gameClockFactor));
		} else {
		 ticksToHit = (int) Math.round((((double) Math.sqrt((t1x-t2x)*(t1x-t2x) + (t1y-t2y)*(t1y-t2y))*10*(factor)/(GodGenerator.traderSpeed*GodGenerator.speedadjust)))/GodGenerator.gameClockFactor);

		}
		return ticksToHit;
	
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns in ticks the ETA of an attack with the given parameters.
	 * 
	 * @param yourTownID	the ID of the town you're sending from
	 * @param enemyx		the x coordinate of the target town
	 * @param enemyy		the y coordinate of the target town
	 * @param holdNumbers	an array containing the number of each unit being sent
	 * 
	 * @return the number of ticks it will take for the attack to land.
	 */
	public int getAttackETA(int yourTownID, int enemyx, int enemyy, int[] holdNumbers) {
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return -1;
		}
		int i = 0;  boolean found = false;
		Town t = g.findTown(yourTownID);
		if(t.getPlayer().ID!=p.ID) { setError("Not your town!"); return -1; }
		
		
		double holdLowSpeed=0; AttackUnit hau;
		double totalsize=0;
		int k = 0;
		ArrayList<AttackUnit> au = t.getAu();
		while(k<au.size()) {
			hau = au.get(k);
			 holdLowSpeed+=(holdNumbers[k]*hau.getExpmod()*hau.getTrueSpeed(t.getPlayer()));
			 totalsize+=(holdNumbers[k]*hau.getExpmod());
			k++;
		}
		if(totalsize==0) totalsize=1;
		if(holdLowSpeed==0) holdLowSpeed=GodGenerator.scholarSpeed;
		holdLowSpeed/=totalsize;
		int x = t.getX(); int y = t.getY();
		int ticksToHit = (int) Math.round(((double) Math.sqrt((x-enemyx)*(x-enemyx) + (y-enemyy)*(y-enemyy))*10/(holdLowSpeed*g.speedadjust))/GodGenerator.gameClockFactor);
		if(ticksToHit==0) ticksToHit=(int) Math.round(((double) 10/(holdLowSpeed*g.speedadjust))/GodGenerator.gameClockFactor);
		//System.out.println(ticksToHit);
		return ticksToHit;
	}
	
		/**
		 * UI Implemented.
		 * <br/><br/>
		 * Allows you to change the bombing target of a Strafing or Glassing mission.
		 * raidID is the identifier for your raid, and yourTownID the town of the raid's
		 * origination, and is given to speed up the search algorithm. newTarget is the new
		 * target designation.
		 * <br/>
		 * See {@link UserBuilding} for a list of valid building types.
		 * 
		 * @param raidID 		the ID of the raid to be modified
		 * @param newTarget		a String array of the building types to be targeted
		 * @param yourTownID	the ID of the town you sent the raid from
		 * 
		 * @return True, if the bombing target was successfully changed.  False otherwise.
		 */
	public boolean changeBombTarget(UUID raidID, String newTarget[], int yourTownID) {
		if(prog&&!p.isAttackAPI()) {
			setError("You do not have the Attack API!");
			return false;
		}
	
		int i = 0; boolean found = false;
		
		
		Town t1 = g.findTown(yourTownID);
		if(t1.getPlayer().ID!=p.ID) { setError("Not your town!"); return false; }
		
		if(!checkMP(t1.townID)) return false;

		 i = 0;
		  found = false;
		
		UserRaid r = getUserRaid(raidID);
		if(r.getTID1()!=t1.townID) { setError("Not your raid!"); return false; }
		
		Raid actr = new Raid(raidID,g);
		if(found&&r!=null) {//double protection with r!=null part.
			
			if(r.raidOver()) return false; // no need for a raid that's over!
			if(!r.bomb()) return false; // non-bombing missions not allowed!
			actr.setBombTarget(newTarget);
			
			
			notifyViewer();
		
			return true;
		
		}
		else return false;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Sends a resupply raid to a Siege or Glassing campaign designated
	 * by the raidID and originating town of the raid, yourTownID. auAmts
	 * is the amount of each unit you want to send, size is equal to your number of
	 * unlocked units + however many support units you have.
	 * 
	 * @param raidID		the ID of the raid to be resupplied
	 * @param auAmts		the amounts of units to resupply this raid with.
	 * @param yourTownID	the ID of the town the raid originated from.
	 * 
	 * @return True, if the resupply mission was sent.  False otherwise.
	 */
	public boolean resupply(UUID raidID, int[] auAmts, int yourTownID) {
		if(prog&&!p.isAttackAPI()) {
			setError("You do not have the Attack API!");
			return false;
		}
		Town t = g.findTown(yourTownID);
		if(t.getPlayer().ID!=p.ID) return false;
		return resupply(raidID,t.getTownName(),  auAmts);
	}
		/**
		 * UI Implemented.
		 * <br/><br/>
		 * Sends a resupply raid to a Genocide or Glassing campaign designated
		 * by the raidID and originating town of the raid, yourTownName. auAmts
		 * is the amount of each unit you want to send, size is 6 + however many
		 * support units you have.
		 * 
		 * @param raidID		the ID of the raid to resupply
		 * @param yourTownName	the name of the town the raid originated from
		 * @param auAmts		an amounts of units to resupply this raid with
		 * 
		 * @return True, if the resupply mission was sent.  False otherwise.
		 * 
		 * @deprecated Use {@link #resupply(UUID raidID, int[] auAmts, int yourTownID)} instead.
		 */
	public boolean resupply(UUID raidID,String yourTownName, int[] auAmts) {
		if(prog&&!p.isAttackAPI()) {
			setError("You do not have the Attack API!");
			return false;
		}

		int holdNumbers[] = auAmts; // Should only have six types of unit, therefore
		// attack method can be easily overloaded by making it so you can do
		// attack(x,y,unit1,unit2) or add unit 3 on there...
		
		// Gotta get the town first.
		
		Town t1 = g.findTown(yourTownName,p);
		if(t1.townID==0) return false;
		if(t1.getPlayer().ID!=p.ID) { setError("Not your town!"); return false; }
		if(!checkMP(t1.townID)) return false;

		if(!t1.slotsFree()) return false; // no slots, no resupplies, bitch.


		int k = 0; // to make sure can only go up to six.
				
		boolean zeroes = true; boolean negatives = false;
		AttackUnit hau;
		ArrayList<AttackUnit> t1au = t1.getAu();
		while(k<holdNumbers.length) {
			

			// Next we need to check if these numbers go over the max units in
			// the town. If they do, send the max units instead.
			  hau = t1au.get(k);
			if(hau.getSize()<holdNumbers[k]) holdNumbers[k]=hau.getSize();
			 if(holdNumbers[k]>0) zeroes=false; // needs to be after the size-mod if overflowing.
			if(holdNumbers[k]<0) negatives = true; // No less than zero crap..
			 // Simple as cake.
			
		//	if(hau.speed < holdLowSpeed && holdNumbers[k]>0)   holdLowSpeed = hau.speed;
	
			k++;
		}
		if(zeroes) return false; // not sending a raid of nada.
		if(negatives) return false;
		boolean Genocide = false; boolean Bomb = false; int support = 0; 
		boolean invade = false;
		
		
		int i = 0; boolean found = false; Raid r=new Raid(raidID,g);
		if(r.getTown1().getPlayer().ID!=p.ID) { setError("Not your raid!"); return false; }
		
		if(!r.isGenocide()) return false;
		// so if the raid doesn't exist OR it does exist AND it's not a genocide(or glass, glass
		// also uses the genocide boolean) run, return false!

	
		int	x = r.getTown2().getX();
		int	y=r.getTown2().getY();
		int t1x = t1.getX(); int t1y = t1.getY();
		boolean keep=false;
		if(prog) keep=true;
		prog=false;
		int ticksToHit = getAttackETA(t1.townID, x,y,auAmts);
		if(keep) prog =true;

		Town Town2 = g.findTown(x,y);
		if(Town2 == null) return false;
		
		
		k = 0;

		ArrayList<AttackUnit> au = new ArrayList<AttackUnit>();
		
		k = 0;
		AttackUnit addThis;
		while(k<holdNumbers.length) { // making sure it satisfies  reqs here is all.
			// if we combined this loop then units would be lost if later in the loop there was an
			// error found. If the user only sends his own aus, he won't address supp aus in his array.
			// so this loop is set up to either goto the holdnumbers max. If
			// not, then index out of bounds on holdnumbers occurs.
			addThis = t1au.get(k).returnCopy();
			
			addThis.setSize(holdNumbers[k]);
			// Notice a missing if UberStatement in here, it is usually here for attack but
			// only pertains to support=1 or support=2 missions, which doesn't happen with
			// resupply.
			if(addThis.getSupport()==1&&addThis.getSize()>0) return false;
			// If this is a support unit, but it's not an offensive one(i.e. support=2), then this
			// user cannot send it anywhere.

			// scouting if UberStatement removed, impossible for resupply.
			
			k++;
		}
		
		
		k=0; 
		while(k<t1au.size()) {
			// by using all au, not just the array of holdNumbers, we insure
			// that the attack lineup remains intact. AttackUnits that aren't
			// mentioned will still send zeroes.
		
			 addThis = t1au.get(k).returnCopy();
			
		// support if UberStatements removed here, no such thing as offsupport or normal support runs
			// for resupply, which is supp==3.
			
			au.add(addThis);
			if(k<holdNumbers.length) {
			addThis.setSize(holdNumbers[k]);

			t1.setSize(k,t1au.get(k).getSize() - holdNumbers[k]);}
			
			else addThis.setSize(0);

			k++;
		}

		k=0;
		Raid holdAttack = new Raid(Math.sqrt((t1x-x)*(t1x-x) + (t1y-y)*(t1y-y)), ticksToHit, t1, g.findTown(x,y), Genocide, Bomb,support,invade,"noname",false,au,0);
	
		holdAttack.setResupplyID(holdAttack.getId());
	
		//holdAttack.closeCon();
		notifyViewer();

		return true;
		
	}
		
		/**
		 * UI Implemented.
		 * <br/><br/>
		 * Cancels the trade schedule designated by the tsid.
		 * 
		 * @param tsid the ID of the Trade Schedule to be canceled
		 * 
		 * @return True, if the Schedule was successfully canceled.  False otherwise.
		 */
	public boolean cancelTradeSchedule(UUID tsid) {
		if(prog&&!p.isTradingAPI()) {
			setError("You do not have the Trading API!");
			return false;
		}
			TradeSchedule ts = p.findTradeSchedule(tsid);
			if(ts==null) {
				setError("This is not a valid tradeschedule!");
				return false;
			}
			Town t = ts.getTown1();
			if(t.getPlayer().ID!=p.ID) { setError("Not your trade schedule!"); return false;} 
					if(!checkMP(ts.getTown1().townID)) return false;
					
					ts.deleteMeInterrupt(); return true;
	}
		/**
		 * UI Implemented.
		 * <br/><br/>
		 * Recalls the raid designated by this raidID.  This only works on raids that have not landed.
		 * 
		 * @param raidID the ID of the raid to recall
		 * 
		 * @return True, if the raid has been recalled.  False otherwise.
		 */
	public boolean recall(UUID raidID) {
		if(prog&&!p.isAttackAPI()) {
			setError("You do not have the Attack API!");
			return false;
		}
		int i = 0; boolean found = false;
		Raid rtokill=null;
		while(i<p.towns().size()) {
			int j = 0;
			while(j<p.towns().get(i).attackServer().size()) {
				if(p.towns().get(i).attackServer().get(j).getId().equals(raidID)) {
					rtokill = p.towns().get(i).attackServer().get(j);
					break;
				}
				j++;
			}
			i++;
		}
		
		if(rtokill==null) {
			setError("Invalid Raid ID!");
			return false;
		}
		Town t= rtokill.getTown1();
		
		if(t.getPlayer().ID!=p.ID) { 
			setError("Not your raid!"); return false;
		}
		if(!checkMP(t.townID)) return false;
		if(rtokill.getDigAmt()>0) {
			rtokill.getTown2().returnDigOrRO(false,false);
			return true;
		} else {
				
			//r to kill will not be null if found is true.
			int c = 0;
			double lowSpeed = 0;
			AttackUnit g;
			// we want it weighted...so we must divide by total size*expmod...
			// because that gives us the total amount of soldier equivalents.
			double totalsize=0;
			ArrayList<AttackUnit> au = rtokill.getAu();
			do {
				 g = au.get(c);
			//	if(g.size>0&&g.speed<lowSpeed) lowSpeed=g.speed;
				lowSpeed+=(g.getSize()*g.getExpmod()*g.getTrueSpeed(t.getPlayer()));
				totalsize+=(g.getSize()*g.getExpmod());
				 c++;
			} while(c<au.size());
			lowSpeed/=totalsize;
			Town t2 = rtokill.getTown2();
			int t1x = t.getX(); int t1y = t.getY(); int t2x = t2.getX(); int t2y = t2.getY();
			int testhold = (int) Math.round((Math.sqrt((t2x-t1x)*(t2x-t1x)+(t2y-t1y)*(t2y-t1y))*10/(lowSpeed*GodGenerator.speedadjust))/GodGenerator.gameClockFactor);
			
			testhold-=rtokill.getTicksToHit(); // so if there are 10 tick distances,
			// and the unit is 3 ticks from hitting it, then 10-3 = 7 is
			// the distance is must travel back.
			rtokill.setTicksToHit(testhold);
			rtokill.setRaidOver(true);
		
			/*
			try { // turn back all resupplies.
				UberStatement stmt = this.g.con.createStatement();
				ResultSet rs = stmt.executeQuery("select rid from raid where resupplyID=" + rtokill.raidID);
				
				while(rs.next()) {
					recall(rs.getInt(1));
				}
				
				rs.close();
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }*/
			ArrayList<Town> towns = p.towns();
			int k = 0;
			ArrayList<Raid> as;
			while(k<towns.size()) {
				as = towns.get(k).attackServer();
				int j = 0;
				while(j<as.size()) {
					if(as.get(j).getResupplyID().equals(rtokill.getId())) {
						recall(as.get(j).getId());
						break;
					}
					j++;
				}
				k++;
			}
			return true;
		}
		
	}
	
		/**
		 * UI Implemented.
		 * <br/><br/>
		 * Gets an array of all the raids a player has out, in order by town.
		 * 
		 * @return an array of {@link UserRaid UserRaids} or null, if an error occurred.
		 */
	public UserRaid[] getUserRaids() {
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return null;
		}
		/*
		 * This method returns a list of all raids a user has in UserRaid objects.
		 * 
		 * 	public UserRaid(int raidID, double distance, boolean raidOver, int ticksToHit, String town1, int x1, int y1, String town2, int x2, int y2, int auAmts[], String auNames[], String raidType,int m, int t, int mm, int f) {
		LINKED TO GETUSERRAIDS(TID) IF YOU CHANGE!
		 */
		
		int i = 0;
		ArrayList<UserRaid> temp = new ArrayList<UserRaid>(); Town t; Raid r;
		
		ArrayList<Town> towns = p.towns();
		UserRaid[] raids;
		while(i<towns.size()) {
			int j = 0;
			 t = towns.get(i);

			if(checkMP(t.townID)) {
			raids = getUserRaids(t.townID);
				while(j<raids.length) {
					int k = 0; boolean add=true;
					while(k<temp.size()) {
						if(temp.get(k).id().equals(raids[j].id())) {
							add=false;
							break;
						}
						k++;
					}
					if(add)
						temp.add(raids[j]);
					
					j++;
				}
			}
			i++;
		}

	
		i = 0;
		UserRaid array[] = new UserRaid[temp.size()];
		while(i<temp.size()) {
			array[i]=temp.get(i);
			i++;
		}
		setError("noerror");

		return array;
	}
		

	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Sends some support home from a particular player given by the player ID in your town given
	 * by the town id. The town is your town, the pid is their pid. So you use your town to search
	 * for units and their PID to find them.
	 * 
	 * @param auAmts	the amounts of units to send home
	 * @param tid		the ID of your town with foreign support
	 * @param pid		the ID of the supporting Player
	 * 
	 * @return True, if the number of troops specified in auAmts is now returning home.  False otherwise.
	 */
	
	public boolean sendHome(int[] auAmts,int tid, int pid) {
		if(prog&&!p.isAttackAPI()) {
			setError("You do not have the Attack API!");
			return false;
		}
		int i = 0; Player pl=null;
		boolean found = false;
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) { setError("Not your town!"); return false; }
		 i = 0;  AttackUnit a; found = false; int returnTID=0;
	
				 
			 int j = 6;
			 found = true;
			 ArrayList<AttackUnit> au = t.getAu();
			 while(j<au.size()) {
				 a = au.get(j);
				 if(a.getOriginalPlayer().ID==pid) returnTID = a.getOriginalTID();
				 j++;
			 }
			
			
		 if(returnTID==0) {
			 setError("This player is not supporting you!");
			 return false;
		 }
		return g.getPlayer(pid).getPs().b.recall(auAmts,tid,p.ID,returnTID);
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Sends support home from a particular player given by the player ID in the town given
	 * by the town id. The town is your town, the pid is their player ID.
	 * 
	 * @param tid	the ID of your town with foreign support
	 * @param pid	the ID of the supporting player
	 * 
	 * @return True, if the supporting troops are now returning home.  False otherwise.
	 */
	
	public boolean sendHome(int tid, int pid) {
		if(prog&&!p.isAttackAPI()) {
			setError("You do not have the Attack API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) { setError("Not your town!"); return false; }
		
		int  i = 0; AttackUnit a;  int returnTID=0;
	
		 int j = 0;
		 ArrayList<AttackUnit> au = t.getAu();
			 while(j<au.size()) {
				 a = au.get(j);
				 if(a.getOriginalPlayer().ID==pid) returnTID = a.getOriginalTID();
				 j++;
			 }
		
		 
		return g.getPlayer(pid).getPs().b.recall(tid,p.ID,returnTID);
	}

	
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * This recalls all support units from the town designated by town id, of the player
	 * designated by pidOfRecallTown, to your town by the town id of destinationTown.
	 * <br/>
	 * This is a shortcut of {@link #recall(int[] auAmts, int townToRecallFromID, int pidOfRecallTown, int yourTownID)}
	 * for when you want to recall all units.
	 * 
	 * @param townToRecallFromID	the ID of the town you're recalling from
	 * @param pidOfRecallTown		the ID of the player that owns the town you're recalling from
	 * @param yourTownID			the Id of the town you sent the support from
	 * 
	 * @return True, if the units are returning home.  False otherwise.
	 */
	public boolean recall(int townToRecallFromID, int pidOfRecallTown, int yourTownID) {
		
		int auAmts[] = new int[0];
		return recall(auAmts,townToRecallFromID,pidOfRecallTown,  yourTownID);

	}

	/**
	 * UI Implemented.
	 * <br/><br/>
	 * This recalls some support units from the town designated by town id, of the player
	 * designated by pidOfRecallTown, to your town by the town id of destinationTown. 
	 * 
	 * @param auAmts				an array containing the number of units to recall
	 * @param townToRecallFromID	the ID of the town you're recalling from
	 * @param pidOfRecallTown		the ID of the player that owns the town you're recalling from
	 * @param yourTownID			the ID of the town you sent the units from
	 * 
	 * @return True, if the units are now returning.  False otherwise.
	 */
	public boolean recall(int[] auAmts, int townToRecallFromID, int pidOfRecallTown, int yourTownID) {
		if(prog&&!p.isAttackAPI()) {
			setError("You do not have the Attack API!");
			return false;
		}
		p.update(); // because if recall is begin called and there
		// is stuff to update, we better update it now,
		//as recalled can be called through sendHome via other players
		// and by digLogicBlock..we'd hate to have that
		// screw over a perfectly good town!
		
		// needs player and town name to make certain.
		// If players make towns with the same name this could yield problems.
		/*
		 * Recall works by finding the town object associated with the player
		 * and then removing appropriate aus and returning them in a return raid.
		 *  While using the sql db would be nice, the actual data structures are needed.
		 * 
		 * It also removes the support aus from any outgoing raids and adds them.
		 * 
		 * Must include destination town because you may have lost that town that they originally
		 * came from! :OOOO
		 */
		
		Town myTown = g.findTown(yourTownID);
		Town t = g.findTown(townToRecallFromID); 
		if(myTown.getPlayer().ID!=p.ID) { setError("Not your town!"); return false; }
		int g = 0;
		if(!checkMP(myTown.townID)) return false;

		Player otherP; ArrayList<AttackUnit> au;
		if(t.getPlayer().ID!=pidOfRecallTown) {
			setError("Not their town!"); 
			return false; 
		}
		ArrayList<Raid> blockades = t.getBlockades();
		Raid raid = null;
		for(Raid r : blockades) {
			if(r.getTown1().getTownID()==yourTownID) {
				raid = r;
				break;
			}
		}
		
		if(t.getDigAmt()>0) {
			t.returnDigOrRO(false,false);
		} else if(raid!=null) {
			//if we're recalling a blockade, this is much easier since the units aren't
			//added to the base town
			boolean totalRecall = true;
			au = raid.getAu();
			if(auAmts.length>0) {
				for(int i=0;i<auAmts.length;i++) {
					AttackUnit a = au.get(i);
					a.setSize(Math.max(a.getSize()-auAmts[i], 0));
					if(a.getSize()>0) 
						totalRecall = false;
				}
			} else {
				totalRecall = true;
			}
			if(totalRecall) {
				t.attackServer().remove(raid);
				raid.setTicksToHit(raid.getTotalTicks());
				raid.setRaidOver(true);
			} else {
				ArrayList<AttackUnit> au2 = new ArrayList<AttackUnit>();
				for(int i=0;i<au.size();i++) {
					AttackUnit a = au.get(i).returnCopy();
					au2.add(i, a);
					au2.get(i).setSize(auAmts[i]);
				}
				Raid holdRaid = new Raid(raid.getDistance(), raid.getTotalTicks(), raid.getTown1(), 
											raid.getTown2(), false, false, 0, false, raid.getName(), false,
											au2, 0);
				holdRaid.setRaidOver(true);
				//and the rest is handled by the iterators.  :D
			}
		} else {
			AttackUnit hau; Raid holdAttack;
			AttackUnit a;
			ArrayList<Raid> t2as;
			ArrayList<AttackUnit> asau;
			int i = 0; boolean found = false;
			//better than missing one.
			g = 0;  au = new ArrayList<AttackUnit>();
			
			while(g<p.getAu().size()) { // get an array ready!
				au.add(p.getAu().get(g).returnCopy());
				g++;
			}
			
			int k = 0;
			ArrayList<AttackUnit> t1au = t.getAu();
			while(k<t1au.size()) {
		
				a = t1au.get(k);
		
				if(a.getSupport()>0&&a.getOriginalPlayer().ID==p.ID) {
				
				 /*
				  * Here I remove the units one at a time from the other player's
				  * data structure, turn them into normal "AUs" and add them to
				  * an au list to return...but we've got to use their original
				  * lots and create a completely authentic raid...
				  * 
				  * And we remove them from the supportAU table.
				  * 
				  * Don't forget to set raidOver = true. This'll let the
				  * attackServer know what to do.
				  */
					
					
				 /*
				  * WHY FOUND IS TRUE BELOW:
				  * if it gets here, most likely, the unit has
				  * some size to it(otherwise it was called one cycle before
				  * a player could erase it) and that size must be on raids
				  * somewhere then and
				  * so we can set found=true because this means we found the support units
				  * to return!
				  */
					
					found=true;
					// it's original slot num corresponds to the index of it's storage in the au array.
					if(auAmts.length>0) {
						if(auAmts[a.getOriginalSlot()]>a.getSize()) auAmts[a.getOriginalSlot()] = a.getSize();
					
						else if(auAmts[a.getOriginalSlot()]<0) auAmts[a.getOriginalSlot()]=0;
					
						au.get(a.getOriginalSlot()).setSize(auAmts[a.getOriginalSlot()]);
						t.setSize(k,a.getSize() - auAmts[a.getOriginalSlot()]);
					} else
						au.get(a.getOriginalSlot()).setSize(a.getSize());

					int y = 0;
					// go through each raid, find the au, return those units.
					// I know some may be far away but this is easiest for
					// the computer and for me.
					if(auAmts.length==0){ // >0 indicates only a partial recall. No need to do that for that.
					 /*	try {
						UberStatement stmt = this.g.con.createStatement();
						ResultSet rs = stmt.executeQuery("select size from raidSupportAU where tidslot = " + a.getSlot() + " and tid = " + t.townID +";");
						while(rs.next()) {
							au.get(a.getOriginalSlot()).setSize(au.get(a.getOriginalSlot()).getSize()+rs.getInt(1));
							
						}
						rs.close();
						stmt.executeUpdate("delete from raidSupportAU where tidslot = " + a.getSlot() + " and tid = " + t.townID +";");
						stmt.close();
					} catch(SQLException exc) { exc.printStackTrace(); }*/
					 //size in this case being the size of all the support units out on raids.
						t2as = t.attackServer();
						while(y<t2as.size()) {
							asau = t2as.get(y).getAu();
							int j = 0;
							while(j<asau.size()) {
								if(asau.get(j).getSupport()>0&&asau.get(j).getOriginalPlayer().ID==p.ID&&asau.get(j).getOriginalSlot()==a.getOriginalSlot()) {
									au.get(a.getOriginalSlot()).setSize(au.get(a.getOriginalSlot()).getSize()+asau.get(j).getSize());
									asau.get(j).setSize(0);
								 
								}
								j++;
							}
							y++;
						}
					 
					}
					
					// now to remove it from the supportAU and raidSupportAU table. Player
					// has the ability to do this - it happens when the support AU
					// has zero size. So just set it that and save memory.
					// This will also remove the au from the attackunit array
					// of the town naturally via player.
					if(auAmts.length==0)
						t.setSize(k,0);

				}
				k++;
			}
			// Now I create a "false" return raid for the town by making it and setting raidOver=true instantaneously so
			// that attackServer thinks it's a return raid. Support=0 here, just to mention that - it's
			// as if it were a normal attack.
			g = 0; double holdLowSpeed=0;
			int totalsize=0;
			while(g<au.size()) {
				hau = au.get(g);
				//if(hau.speed < holdLowSpeed && hau.size>0)   holdLowSpeed = hau.speed;
				holdLowSpeed+=(hau.getSize()*hau.getExpmod()*hau.getTrueSpeed(t.getPlayer()));
				totalsize+=hau.getSize()*hau.getExpmod();
				g++; 
			}
			if(totalsize==0&&t.getDigCounter()>0) { // so digs with zero guys will send back at scholar speed.
				holdLowSpeed=GodGenerator.scholarSpeed;
				totalsize=1; 
			}
			holdLowSpeed/=totalsize;
		
			if(holdLowSpeed>0) { 
				// if we have a good holdLowSpeed that means we can send a returnRaid.
				// if not that means found was never set to true and no support units were ever found
				// or they were found but with zero size and not removed yet.
				
				int t1x = t.getX(), t1y = t.getY(), t2x = myTown.getX(), t2y = myTown.getY();
			 
				int ticksToHit = (int) Math.round(Math.sqrt(Math.pow((t1x-t2x),2) + Math.pow((t1y-t2y),2))*10/(holdLowSpeed*this.g.speedadjust)/GodGenerator.gameClockFactor);
				if(ticksToHit==0) ticksToHit=(int) Math.round(((double) 10/(holdLowSpeed*this.g.speedadjust))/GodGenerator.gameClockFactor);
				/* 	int digAmt=0; No longer needed.
				if(pidOfRecallTown==5&&t.getDigCounter()>0&&auAmts.length==1) {
				digAmt=t.getDigAmt();
				//System.out.println("I am setting everything in " + t.getTownName());

				t.resetDig(0,0,false,null);	// because the second you set dig counter
											// to -1, the town becomes inactive!
				// System.out.println("t's owed ticks are " + t.owedTicks + " and t is " +t.getTownName());
			}*/
				int digAmt=0;
				holdAttack=null;
				try {
					holdAttack = new Raid(Math.sqrt(Math.pow((t1x-t2x),2) + Math.pow((t1y-t2y),2)), ticksToHit, myTown,t, false, false,0,false,"noname",false,au,digAmt);
				} catch(Exception exc) { exc.printStackTrace(); System.out.println("Error happened with recall but we caught it."); }
				// myTown needs to be town1 because this is going to be the ghost destinator town - where the raid
				// will "believe" this return raid came from.
				g=0;
				
				holdAttack.endRaid(); // uses endRaid method to set raidOver=true;
			
			}
		}
		return true;
	}
	
	
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Builds a building of the type specified at the lotNum given in the town
	 * specified by the town id if possible.
	 * 
	 * @param type the type of building to be constructed.  See {@link UserBuilding} for valid types.
	 * @param lotNum the lot to build this building on
	 * @param tid ID of the town to build this building in
	 * 
	 * @return True, if construction has begun.  False otherwise.
	 */
	public boolean build(String type, int lotNum, int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return build(type,lotNum,t.getTownName());
	}
		/**
		 * @deprecated Use {@link #build(String type, int lotNum, int tid)} instead
		 * UI Implemented.
		 */
	public boolean build(String type, int lotNum, String town) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
			try {
			if(canBuild(type,lotNum,town)) {
				int i = 0;Town holdT=g.findTown(town,p);
				// Building b = new Building(type,lotNum,holdT.getTotalEngineers(),g.Maelstrom.getEngineerEffect(holdT.x,holdT.y),p.engTech);

				Building b = holdT.addBuilding(type,lotNum);
				if(type.equals("Missile Silo")) {
					g.sendNukeMessage("none",holdT,true);
				}
				 int j = 0;
				 long res[] = holdT.getRes();

				do {
					res[j]-=Building.getCost(b.getType())[j];
					j++;
				} while(j<res.length-1);
				holdT.setRes(res);

				return true;
			} else
			
		// no need to set error, canBuild already did!
		return false; // if it gets here, it's failed.
			} catch(NullPointerException exc) {
				exc.printStackTrace();
				setError("internalservererror");
				return false;
			}
	}
	
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns true if you can build a building in the desired lotNum.(ie, if you have
	 * the resources, open lot spot, and a few other requirements are met.)
	 * 
	 * @param type 		type of building to construct.  See {@link UserBuilding} for valid building types.
	 * @param lotNum 	lot number for the building to be placed on.
	 * @param tid 		Id of the town we're to build it in
	 * @return
	 */
	public boolean canBuild(String type, int lotNum, int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return canBuild(type,lotNum,t.getTownName());
	
	}
	/**
	 * UI Implemented.
	 * 
	 * @deprecated Use {@link #canBuild(String type, int lotNum, int tid)} instead.
	 */
	public boolean canBuild(String type,int lotNum, String town) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		// righto. Build function. Sees if they got the sources, if the lot number is free, then builds
		// the building there. Need to incorporate building times...
		// build(type, lotNum, town)
		 // Exception here what if type is a number instead of string.
		// Also need to get rid of quotes on it.
		
		
		  if(type.equals("Metal Mine")||type.equals("Timber Field")
				  ||type.equals("Crystal Mine")||
				  type.equals("Farm")) {
			  setError("Cannot build mines.");
			  return false;
		  }		
		  
		  if(!type.equals("Arms Factory")&&!type.equals("Manufacturing Plant")&&!type.equals("Command Center")&&
				  !type.equals("Institute")&&!type.equals("Trade Center")&&
				  !type.equals("Fortification")&&!type.equals("Metal Warehouse")&&!type.equals("Lumber Yard")&&
				  !type.equals("Crystal Repository")&&!type.equals("Granary")
				  &&!type.equals("Airstrip")&&!type.equals("Missile Silo")&&!type.equals("Recycling Center")
				  &&!type.equals("Foundry")&&!type.equals("Sawmill")&&!type.equals("Crystal Refinery")
				  &&!type.equals("Hydroponics Bay") &&!type.equals("Storage Yard")&&!type.equals("Resource Cache")) {
			  setError("Incorrect building type.");
			  return false;
		  }

		  int k = 0;
		boolean found = false;
		Town holdT; Building b;
		holdT = g.findTown(town,p);
			boolean reqsMet=false;
		  if((type.equals("Airstrip")&&!p.isAirshipTech())&&haveBldg("Command Center",10,holdT.townID)
				  &&haveBldg("Arms Factory",10,holdT.townID)&&haveBldg("Manufacturing Plant",5,holdT.townID)
				  ) {
			  reqsMet=true;
		  } else if(type.equals("Command Center")&&!haveBldg("Command Center",1,holdT.townID)) {
			  reqsMet=true; // if you don't have one, you meet reqs.
		  } else if((type.equals("Missile Silo"))&&haveBldg("Manufacturing Plant",15,holdT.townID)
				  &&haveBldg("Institute",15,holdT.townID)) {
			  reqsMet=true;

		  }else if((type.equals("Recycling Center"))&&haveBldg("Command Center",10,holdT.townID)
				  &&haveBldg("Institute",10,holdT.townID)&&haveBldg("Manufacturing Plant",10,holdT.townID)) {
			  reqsMet=true;

		  }else if((type.equals("Foundry")&&haveBldg("Metal Mine",15,holdT.townID))) {
			  reqsMet=true;

		  }else if((type.equals("Sawmill")&&haveBldg("Timber Field",15,holdT.townID))) {
			  reqsMet=true;

		  }else if((type.equals("Crystal Refinery")&&haveBldg("Crystal Mine",15,holdT.townID))) {
			  reqsMet=true;
		  }else if((type.equals("Hydroponics Bay")&&haveBldg("Farm",15,holdT.townID))) {
			  reqsMet=true;

		  }else if((type.equals("Fortification")&&haveBldg("Command Center",5,holdT.townID)
				  &&haveBldg("Institute",5,holdT.townID)&&haveBldg("Arms Factory",5,holdT.townID))) {
			  reqsMet=true;

		  }else if((type.equals("Institute")&&haveBldg("Command Center",1,holdT.townID))) {
			  reqsMet=true;

		  }else if((type.equals("Manufacturing Plant")
				  &&haveBldg("Institute",10,holdT.townID)&&haveBldg("Arms Factory",5,holdT.townID))) {
			  reqsMet=true;

		  }else if((type.equals("Resource Cache")&&haveBldg("Metal Warehouse",1,holdT.townID)
				  &&haveBldg("Institute",3,holdT.townID)&&haveBldg("Lumber Yard",1,holdT.townID)
				  &&haveBldg("Crystal Repository",1,holdT.townID)&&haveBldg("Granary",1,holdT.townID))) {
			  reqsMet=true;

		  }else if((type.equals("Storage Yard")&&haveBldg("Metal Warehouse",10,holdT.townID)
				 &&haveBldg("Lumber Yard",10,holdT.townID)
				  &&haveBldg("Crystal Repository",10,holdT.townID)&&haveBldg("Granary",10,holdT.townID))) {
			  reqsMet=true;

		  }else if((type.equals("Trade Center")&&haveBldg("Command Center",3,holdT.townID)
				  &&haveBldg("Institute",1,holdT.townID))) {
			  reqsMet=true;

		  }else if((type.equals("Arms Factory")&&haveBldg("Command Center",1,holdT.townID)
				  )) {
			  reqsMet=true;

		  }else if(  (type.equals("Metal Warehouse")||type.equals("Lumber Yard")
				  ||type.equals("Crystal Repository")||type.equals("Granary"))
				  &&haveBldg("Institute",1,holdT.townID)) {
			  reqsMet=true;

		  }
		  
		  if(!reqsMet) {
			  setError("You do not satisfy the requirements for this building yet!");
			  return false;
		  }
		  
		if(holdT.isZeppelin()) {
			setError("You cannot build on an Airship!");
			return false;
		}
	
			if(!checkMP(holdT.townID)) return false;
			//System.out.println("Asked to build in " + town);

			// Now does the building there exist?
			int i = 0;
			int slotsSpent = 0;
			
			/*
			try {
				UberStatement stmt = g.con.createStatement();
				ResultSet rs = stmt.executeQuery("select sum(lvlUp) from bldg where tid = " + holdT.townID);
				if(rs.next()) slotsSpent = rs.getInt(1);
				rs.close();
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }*/
			
			ArrayList<Building> bldg = holdT.bldg();
			while(i<bldg.size()) {
				slotsSpent+=bldg.get(i).getLvlUps();
				i++;
			}
			
			int actualLotTech=p.getInfrastructureTech();
			if(p.getCapitaltid()==holdT.townID) actualLotTech+=4;
			if(actualLotTech>GodGenerator.infrastructureTechLimit) actualLotTech=GodGenerator.infrastructureTechLimit;
			if(lotNum>actualLotTech) {
				setError("Outside your lot tech!");
				return false;
			}
			if(slotsSpent>=p.getConstructionResearch()) return false; // simple. effective.
			
			
			int hqs=0;
			int howmany =0;
			/*
			try {
				UberStatement stmt = g.con.createStatement();
				ResultSet rs = stmt.executeQuery("select count(*) from bldg where tid = " + holdT.townID + " and name = 'Headquarters'");
				if(rs.next()) hqs = rs.getInt(1);
				rs.close();
				 rs = stmt.executeQuery("select count(*) from bldg where tid = " + holdT.townID + " and slot = " + lotNum);
				 if(rs.next()) howmany = rs.getInt(1);
					rs.close();
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }*/
			boolean keep=false;
			if(prog) keep=true;
			prog=false;
			hqs = getUserBuildings(holdT.townID,"Command Center").length;
			if(keep) prog=true;
			int j = 0;
					ArrayList<Building> bldgs = holdT.bldg();
					while(j<bldgs.size()) {
						if(bldgs.get(j).getLotNum()==lotNum) {howmany=1; break; }
						j++;
					}
			
			
			if(howmany>0) { setError("Cannot build on this lot, already a building present!"); return false; }
			
		
			
			// Means no building exists on that lot here. 
			// Now to check if resources are there, if not, do not build.
			// exception what if not a type or wrong type
	//		System.out.println("I am not finding a building here." + holdT.bldg().size());
			
		//	System.out.println("K.");
			
			
			// b = new Building(type,lotNum,holdT.getTotalEngineers(),g.Maelstrom.getEngineerEffect(holdT.x,holdT.y),p.engTech);
			boolean canBuild = true;
			long cost[] = Building.getCost(type);

			 j = 0;
			 long res[] = holdT.getRes();

			do {
				if(res[j]<cost[j]) canBuild = false; // so if resources aren't enough...
				j++;
			} while(j<cost.length-1);
			
			if(canBuild) {
				

				return true;
			}
		
		
		return false; // if it gets here, it's failed.
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns a list of buildings buildable in the town given by this tid.
	 * 
	 * @param tid ID of the town you want to check
	 *  
	 * @return a String array of building types that can be built.
	 */
	public String[] buildableBuildings(int tid) {
		for(Town t: p.towns()) {
			
			if(t.townID==tid) {
				
				ArrayList<String> to = new ArrayList<String>();
			
				for(UserBuilding possible: getBuildings()) {
					if(canBuild(possible.getType(),-1,tid)) {
						to.add(possible.getType());
					}
				}
				String[] toRet = new String[to.size()];
				int i =0;
				while(i<toRet.length) {
					toRet[i]=to.get(i);
					i++;
				}
				resetError();
				return toRet;
			}
		}
		
		setError("Invalid tid!");
		return null;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Levels up the building in the lot designated by lotNum in the town
	 * designated by the town id.
	 * 
	 * @param lotNum the lot of the building you want to level up
	 * @param tid ID of the town the building is in
	 * 
	 * @return True, if the level up is sucessful.  False otherwise.
	 */
	public boolean levelUp(int lotNum, int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return levelUp(lotNum,t.getTownName());
	}
	
		/**
		 * UI Implemented.
		 * <br/><br/>
		 * Levels up the building in the lot designated by lotNum in the town
		 * designated by the String town.
		 * 
		 * @param lotNum	the lot number of the building to be upgraded
		 * @param town		the name of the town the building is in
		 * 
		 * @deprecated Use {@link #levelUp(int lotNum, int tid)} instead.
		 */
	public boolean levelUp(int lotNum, String town) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}

		//LINKED TO RETURN PRICE FOR BUILDING, CANUPGRADE
		/*
		 * Why not just use return price? To knock down on processor time.
		 */
		// format is levelUp(lotNum,town);


			Town holdT =g.findTown(town,p); // exception town does not exist.
		// Now does the building there exist?
			int i = 0;
			if(holdT.isZeppelin()&&lotNum<=3) {
				setError("You don't have mines on an Airship!");
				return false;
			}
			if(!checkMP(holdT.townID)) return false;

			int slotsSpent = 0;
			UUID bid = null;
			int lvl = 0; int lvlUp =0;
			boolean deconstruct=false;
			String name = null;
		/*	try {
				UberStatement stmt = g.con.createStatement();
				ResultSet rs = stmt.executeQuery("select sum(lvlUp) from bldg where tid = " + holdT.townID);
				if(rs.next()) slotsSpent = rs.getInt(1);
				rs.close();
				 rs = stmt.executeQuery("select bid,deconstruct,lvl,lvlUp,name from bldg where tid = " + holdT.townID + " and slot = " + lotNum);
					if(rs.next()) {bid = rs.getInt(1); deconstruct=rs.getBoolean(2); lvl = rs.getInt(3); lvlUp = rs.getInt(4); name = rs.getString(5); }
					rs.close();
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }*/
			 i = 0;
			 int highLvl=0;
			ArrayList<Building> bldg = holdT.bldg();
			Building b=null;
			while(i<bldg.size()) {
				slotsSpent+=bldg.get(i).getLvlUps();
				if(bldg.get(i).getLotNum()==lotNum) {b = bldg.get(i); }
				i++;
			}
			
			i = 0;
			while(i<bldg.size()) {
				if(bldg.get(i).getType().equals("Command Center")&&bldg.get(i).getLvl()>highLvl) highLvl=bldg.get(i).getLvl();
				i++;
			}
			
			if(b==null) { setError("Invalid lotNum!"); return false; }
			
			bid = b.getId(); deconstruct = b.isDeconstruct(); lvl = b.getLvl(); lvlUp = b.getLvlUps(); name = b.getType();
			if(lvl+lvlUp+1>highLvl+2&&!b.getType().equals("Command Center")&&!b.isDeconstruct()) {
				setError("You cannot level a building greater than two levels above your highest command center level!");
				return false;
			}
			if(b.getType().equals("Storage Yard")) {
				i = 0; int otherHighLvl=0;
				while(i<bldg.size()) {
					if(bldg.get(i).getType().equals("Metal Warehouse")
							||bldg.get(i).getType().equals("Lumber Yard")||
							bldg.get(i).getType().equals("Crystal Repository")||
							bldg.get(i).getType().equals("Granary")&&bldg.get(i).getLvl()>otherHighLvl) otherHighLvl=bldg.get(i).getLvl();
					i++;
				}
				if(lvl+lvlUp+1>otherHighLvl&&!b.isDeconstruct()) {
					setError("You cannot level a Storage Yard greater than your lowest level resource storage building level!");
					return false;
				}
			}		
			if((b.getType().equals("Foundry")||b.getType().equals("Sawmill")||b.getType().equals("Crystal Refinery")
					||b.getType().equals("Hydroponics Bay"))&&(lvl+lvlUp+1)>20&&!b.isDeconstruct()) {
				setError("You cannot level this building greater than level 20!");
				return false;
			}
			
			
			
			
		if(slotsSpent>=p.getConstructionResearch()) return false; // simple. effective.
		
				double additive;
				 // So now we have the bldg(). Now, do we have the resources?
				boolean keepMe=false;
				if(prog) keepMe=true;
				prog=false;
					if(!deconstruct&&!haveBldg(town, "Command Center")) {
						setError("You need a Command Center to do this!");
						return false;
					}
				if(keepMe) prog = true;
				 boolean canBuild = true;
				 int k = 0;
				 if(!deconstruct) {
					 long res[] = holdT.getRes();
					 long cost[] = Building.getCost(name);
				 do {

					 //Exp(lvl+1)*((lvl+1)/10)*100 - 100 is cost in this case, the average base cost of an upgrade
					 
					 if(res[k]<cost[k]*Math.pow(lvl+lvlUp+1,2+.03*(lvl+lvlUp+1))) canBuild = false; // NNOOOOOOOOO!!!!
					 k++;
					
				 } while(k<res.length);
				 } 
				
					
				 if(canBuild) {

					 // so the cost is set so that lvlUps keeps track of how many level costs you have bought. It's 0
					 // the first time so nothing. Also, it's counted in slots, and bldgserver levels up
					 // auto and lowers lvlCosts f it sees it still has some lvlUps left.
					 if(holdT.levelUpBuilding(bid)) {
						 notifyViewer();return true; } else return false; // boo yah.
					
				 } else {
					 setError("Not enough resources.");
				 }
				 
				 
			 
		
		return false;
		
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns true if you can upgrade the building located
	 * in the lot designated by lotNum in the town designated by
	 * tid.
	 * 
	 * @param lotNum	the lot number of the building to be upgraded
	 * @param tid		the ID of the town the building is in
	 * 
	 * @return True, if the building can be upgraded.  False otherwise.
	 */
	public boolean canUpgrade(int lotNum, int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return canUpgrade(lotNum,t.getTownName());
		
	}
	
		/**
		 * UI Implemented.
		 * @deprecated Use {@link #canUpgrade(int lotNum, int tid)} instead.
		 */
	public boolean canUpgrade(int lotNum, String town) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
// LINKED TO LEVEL UP, RETURN PRICE FOR BUILDING.
		// format is levelUp(lotNum,town);
		Town holdT = g.findTown(town,p); // exception town does not exist.
	// Now does the building there exist?
		if(holdT.isZeppelin()&&lotNum<=3) {
			setError("You don't have mines on an Airship!");
			return false;
		}
		if(!checkMP(holdT.townID)) return false;
		
		int i = 0;
		int slotsSpent = 0;
		UUID bid = null;
		int lvl = 0; int lvlUp =0;
		boolean deconstruct=false;
		String name = null;
		/*try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select sum(lvlUp) from bldg where tid = " + holdT.townID);
			if(rs.next()) slotsSpent = rs.getInt(1);
			rs.close();
			 rs = stmt.executeQuery("select bid,deconstruct,lvl,lvlUp,name from bldg where tid = " + holdT.townID + " and slot = " + lotNum);
				if(rs.next()) {bid = rs.getInt(1); deconstruct=rs.getBoolean(2); lvl = rs.getInt(3); lvlUp = rs.getInt(4); name = rs.getString(5); }
				rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }*/
		
		 i = 0;
			ArrayList<Building> bldg = holdT.bldg();
			Building b=null;
			int highLvl = 0;
			while(i<bldg.size()) {
				slotsSpent+=bldg.get(i).getLvlUps();
				if(bldg.get(i).getLotNum()==lotNum) {b = bldg.get(i); }
				i++;
			}
			i = 0;
			while(i<bldg.size()) {
				if(bldg.get(i).getType().equals("Command Center")&&bldg.get(i).getLvl()>highLvl) highLvl=bldg.get(i).getLvl();
				i++;
			}
		
			
			if(b==null) { setError("Invalid lotNum!"); return false; }
			
			bid = b.getId(); deconstruct = b.isDeconstruct(); lvl = b.getLvl(); lvlUp = b.getLvlUps(); name = b.getType();
			if(lvl+lvlUp+1>highLvl+2&&!b.getType().equals("Command Center")) {
				setError("You cannot level a building greater than two levels above your highest command center level!");
				return false;
			}
			if(b.getType().equals("Storage Yard")) {
				i = 0; int otherHighLvl=0;
				while(i<bldg.size()) {
					if(bldg.get(i).getType().equals("Metal Warehouse")
							||bldg.get(i).getType().equals("Lumber Yard")||
							bldg.get(i).getType().equals("Crystal Repository")||
							bldg.get(i).getType().equals("Granary")&&bldg.get(i).getLvl()>otherHighLvl) otherHighLvl=bldg.get(i).getLvl();
					i++;
				}
				if(lvl+lvlUp+1>otherHighLvl) {
					setError("You cannot level a Storage Yard greater than your lowest level resource storage building level!");
					return false;
				}
			}
			if((b.getType().equals("Foundry")||b.getType().equals("Sawmill")||b.getType().equals("Crystal Refinery")
					||b.getType().equals("Hydroponics Bay"))&&(lvl+lvlUp+1)>20) {
				setError("You cannot level this building greater than level 20!");
				return false;
			}
			
		
	if(slotsSpent>=p.getConstructionResearch()) {
		error = "Not enough building slots!";
		return false; // simple. effective.
	}
	int j = 0;double additive;
	
			 // So now we have the bldg(). Now, do we have the resources?
			 
			 boolean canBuild = true;
			 int k = 0;
			 long res[] = holdT.getRes();
			 long cost[] = Building.getCost(name);
			 do {

				 if(res[k]<cost[k]*Math.pow(lvl+lvlUp+1,2+.03*(lvl+lvlUp+1))) canBuild = false; // NNOOOOOOOOO!!!!
				 k++;
			 } while(k<res.length);
			 if(canBuild) {
				return true;
				
			 } else{
				 error=("You do not have enough resources!");
				 return false;
			 }
		
	
	}
		/**
		 * UI Implemented.
		 * Returns the total seconds required to level the building to the level given by lvl at the lot
		 * designated by lotNum in the town designated by it's townID. 
		 */
	public int getTicksForLevelingAtLevel(int lotNum, int lvl, int townID) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return -1;
		}

		Town t = g.findTown(townID);
		int i = 0; Building b=null;
		while(i<t.bldg().size()) {
			b = t.bldg().get(i);
			if(b.getLotNum()==lotNum) break;
			i++;
		}
		if(b==null) return 0;
		return Building.getTicksForLevelingAtLevel(t.getTotalEngineers(), lvl,g.Maelstrom.getEngineerEffect(t.getX(),t.getY()),p.getArchitecture(),b.getType());
			
	}
		/**
		 * UI Implemented.
		 * Returns the total seconds required to level the building to the next level at the lot
		 * designated by lotNum in the town designated by it's townID.
		 */
	public int getTicksForLeveling(int lotNum, int townID) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return -1;
		}
			Town t = g.findTown(townID);
			if(t.getPlayer().ID!=p.ID) { setError("Not your town!"); return -1; }
			UUID bid = null;
			/*
			try {
				UberStatement stmt = g.con.createStatement();
				ResultSet rs = stmt.executeQuery("select bid from bldg where tid = " + townID + " and slot = "+ lotNum);
				if(rs.next()) bid = rs.getInt(1);
				rs.close();
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace(); }*/
			int i = 0;
			ArrayList<Building> bldg = t.bldg();
			while(i<bldg.size()) {
				if(bldg.get(i).getLotNum()==lotNum) {bid = bldg.get(i).getId(); }
				i++;
			}
			
			if(bid==null) { setError("No building on this lot!"); return -1; }
			Building b= t.findBuilding(bid);
			return b.getTicksForLeveling(t.getTotalEngineers(),g.Maelstrom.getEngineerEffect(t.getX(),t.getY()),p.getArchitecture());
			
	}
		/**
		 * UI Implemented.
		 * Get the total seconds required to build a civilian unit in the building
		 * on the lot designated by lotNum in the town designated by it's townID.
		 */
	public int getTicksPerPerson(int lotNum, int townID) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return -1;
		}
		Town t = g.findTown(townID);
		if(t.getPlayer().ID!=p.ID) { setError("Not your town!"); return -1; }
		UUID bid = null;
		int ppl=0,lvl=0;
		/*
		try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select bid,ppl,lvl from bldg where tid = " + townID + " and slot = "+ lotNum);
			if(rs.next()) {
				bid = rs.getInt(1);
				ppl = rs.getInt(2);
				lvl = rs.getInt(3);
			}
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }*/
		
		int i = 0;
		ArrayList<Building> bldg = t.bldg();
		Building b=null;
		while(i<bldg.size()) {
			if(bldg.get(i).getLotNum()==lotNum) {
				b = bldg.get(i);
				bid = bldg.get(i).getId();
				ppl = bldg.get(i).getPeopleInside();
				lvl = bldg.get(i).getLvl();
				
			break; }
			i++;
		}
		
		if(b==null) { setError("No building on this lot!"); return -1; }
		
		return b.getTicksPerPerson(t.getTotalEngineers(),g.Maelstrom.getEngineerEffect(t.getX(),t.getY()),p.getArchitecture(),ppl,lvl,b.getType());
			
	}
	/**
	 * UI Implemented.
	 * Returns the number of ticks to build a unit in the town specified.
	 * @param tierType
	 * @param townID
	 * @return
	 */
	public int getTicksPerAttackUnit(int tierType, int townID) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return -1;
		}
		Town t = g.findTown(townID);
		if(t.getPlayer().ID!=p.ID) { setError("Not your town!"); return -1; }
		return QueueItem.getUnitTicks(tierType,t.getTotalEngineers(),t);

			
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Build a number of engineers in the building who's lot is designated by slot,
	 * in the town designated by the town id.
	 * 
	 * @param lotnum the lot your Command Center is on.  Should be 5.
	 * @param number the amount of Engineers to build.
	 * @param tid ID of the town we're to build them in
	 * 
	 * @return True, if contstruction has begun.  False otherwise.
	 */
	public boolean buildEng(int lotNum, int number, int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return buildEng(lotNum,t.getTownName(),number);
	}
	
	/**
	 * UI Implemented. 
	 * <br/><br/>
	 * Returns a string array that can be used to characterize
	 * the effect of a single Command Center on build times in a city.
	 * 
	 * @param lotNum the lot your Command Center is on.  Should be 5.
	 * @param tid ID of the town we're checking.
	 * 
	 * @return a String array characterizing the effects of the Engineers in that building
	 * 
	 * @deprecated THIS METHOD IS NO LONGER USED.
	 */
	public String[] getEngineerReductionsAsStringArray(int lotNum, int tid) {
	/*	String red[] = new String[34];
		
		
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return null;
		
		int totalEngineers = t.getTotalEngineers();
		int x = t.getX(); int y = t.getY();
		int engTech = p.engTech;
		
		double engEffect = g.Maelstrom.getEngineerEffect(x,y);
		//	public static int getUnitTicks(int pop, int totalEngineers, Town t) {
		double without = QueueItem.getUnitTicks(1,0,t);
		float perc = (float) ((without-QueueItem.getUnitTicks(1,totalEngineers,t))/without);
		perc*=100;
		red[0] = "Soldiers are experiencing a " + perc + "% reduction in build time due to this Command Center.";
		without = QueueItem.getUnitTicks(5,0,t);
		perc = (float) ((without-QueueItem.getUnitTicks(5,totalEngineers,t))/without);
		perc*=100;
		red[1] = "Tanks are experiencing a " + perc + "% reduction in build time due to this Command Center.";
		without = QueueItem.getUnitTicks(10,0,t);
		perc = (float) ((without-QueueItem.getUnitTicks(10,totalEngineers,t))/without);
		perc*=100;
		red[2] = "Juggernaughts are experiencing a " + perc + "% reduction in build time due to this Command Center.";
		without = QueueItem.getUnitTicks(20,0,t);
		perc = (float) ((without-QueueItem.getUnitTicks(20,totalEngineers,t))/without);
		perc*=100;
		red[3] = "Bombers are experiencing a " + perc+ "% reduction in build time due to this Command Center.";
		//	public int getTicksPerPerson(int totalEngineers, double cloudFactor, int engTech) {
		without = Building.getTicksPerPerson(0,engEffect,engTech);
		perc = (float) ((without -Building.getTicksPerPerson(totalEngineers,engEffect,engTech))/without);
		perc*=100;

		red[4] = "Civilians are experiencing a " + perc + "% reduction in build time due to this Command Center.";
		int i = 1;
		while(i<=30) {
			//	public static int getTicksForLevelingAtLevel(int totalEngineers,int lvlYouWant, double cloudFactor, int engTech) {

			without = Building.getTicksForLevelingAtLevel(0,i,engEffect,engTech);
			perc = (float) ((without -Building.getTicksForLevelingAtLevel(totalEngineers,i,engEffect,engTech))/without);
			perc*=100;
			red[4+(i-1)] = "Buildings upgrading to level " + i + " are experiencing a " +  perc + "% reduction in build time due to this Command Center.";
			i++;
		}
		return red;*/
		return new String[] {"This method needs updating."};
		
		
	}
		/**
		 * UI Implemented.
		 * <br/><br/>
		 * Build a number of engineers in the building who's lot is designated by slot,
		 * in the town designated by townName.
		 * 
		 * @param slot the lot your Command Center is on.  Should be 5.
		 * @param townName the name of the town we're to build them in
		 * @param number the amount of Engineers to build.
		 * 
		 * @return True, if construction has begun.  False otherwise.
		 * 
		 * @deprecated Use {@link #buildEng(int lotNum, int number, int tid)} instead.
		 */
	public boolean buildEng(int slot, String townName,int number) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		// Engineers' price rise based on total population of engineers in a town. Linearly. Players' resources
		// and everything else grows exponentially, so in order to meet that, these guys must grow
		// linearly - if they do grow proportional to population - and all population grows somewhat
		// exponentially, then we'll probably be okay.
		
		// form buildeng(number,townName)
		
		// This will find the most leveled up comm center and use it. Need to add a second version for specifics.
		
		// Will not build if space is all filled.
		
		// Make sure for lvl 0 buildings, nothing happens.
		
	
		
		//-----IF YOU CHANGE PRICES HERE YOU MUST MODIFY RETURNPRICE----
	
		

		 int j = 0;
		 Town holdT = g.findTown(townName,p);
		
					if(!checkMP(holdT.townID)) return false;

				 long cost[] = returnPrice("Engineer",number,holdT.townID);
				 
				 boolean canBuild = true;
					int k = 0;
					boolean found=false; 
					int lvl = 0;
					UUID bid = null;
					/*
					try {
						UberStatement stmt = g.con.createStatement();
						ResultSet rs = stmt.executeQuery("select lvl,bid from bldg where tid = " + holdT.townID + " and slot = "+ slot + " and name = 'Command Center'");
						if(rs.next()){ lvl = rs.getInt(1); bid = rs.getInt(2); }
						rs.close();
						stmt.close();
					} catch(SQLException exc) { exc.printStackTrace(); }*/
					
					ArrayList<Building> bldg = holdT.bldg();
					int i = 0;
					int totalCap=0;
					int currentlyBuilding=0;
					while(i<bldg.size()) {
						if(bldg.get(i).getType().equals("Command Center")){
							totalCap+=bldg.get(i).getCap();
							currentlyBuilding+=bldg.get(i).getNumLeftToBuild();
						}
						if(bldg.get(i).getLotNum()==slot&&bldg.get(i).getType().equals("Command Center")) {bid = bldg.get(i).getId(); lvl = bldg.get(i).getLvl(); }
						i++;
					}
					int maxNumber = totalCap-holdT.getTotalEngineers()-currentlyBuilding; // this is the max amount that can be built

					
					if(lvl==0) { 
						setError("No fully constructed Command Center on this lot!");
						return false;
					}
						
					boolean keep=false;
					if(prog) keep=true;
					prog=false;
					UserBuilding b = getUserBuilding(bid);
					if(keep) prog=true;
					if(number>=(b.getCap()-(b.getPeopleInside()+b.getNumLeftToBuild()))) number = (int) (b.getCap()-(b.getPeopleInside()+b.getNumLeftToBuild()));
					if(number>maxNumber) number = maxNumber;
					if(number==0) {
						setError("You cannot build zero engineers!");
						return false;
					}
					k=0;
					 long res[] = holdT.getRes();

					do {
						if(res[k]<cost[k]) {
							setError("Not enough resources!");
							canBuild = false; // so if resources aren't enough...
						}
						k++;
					} while(k<cost.length-1);
					
					if(canBuild) {
						 k = 0;
						do {
							res[k]-=cost[k];
							k++;
						} while(k<cost.length-1);
						holdT.setRes(res);
					//	holdT.getTotalEngineers()-=number*cost[4];
					//	p.totalPopulation-=number*cost[4]; // Remember cost is -1.
						// Don't add population here. It gets added by buildserver when they are actually
						// built.
						Building actb = holdT.findBuilding(bid);
						actb.addUnit(number);
						actb.modifyPeopleTicks(holdT.getTotalEngineers(),holdT.getPlayer().God.Maelstrom.getEngineerEffect(holdT.getX(),holdT.getY()),p.getArchitecture());
						
						return true;
					} else return false;
				 
		

	}
	/**
	 * Build a number of engineers in the town designated by the town id. Will automatically find the
	 * best building in which to build them, if possible.
	 * 
	 * @deprecated This method has been commented out, it was not updated with the infrastructure switch.  Use will result in return false.
	 */	
	public boolean buildEng(int number, int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return buildEng(t.getTownName(),number);
	}
		/**
		 * Build a number of engineers in the town designated by townName. Will automatically find the
		 * best building in which to build them, if possible.
		 * 
		 * @deprecated This method has been commented out, it was not updated with the infrastructure switch.  Use will result in return false.
		 */
	public boolean buildEng(String townName, int number) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		return false;

		// Engineers' price rise based on total population of engineers in a town. Linearly. Players' resources
		// and everything else grows exponentially, so in order to meet that, these guys must grow
		// linearly - if they do grow proportional to population - and all population grows somewhat
		// exponentially, then we'll probably be okay.
		
		// form buildeng(number,townName)
		
		// This will find the most leveled up comm center and use it. Need to add a second version for specifics.
		
		// Will not build if space is all filled.
		
		// Make sure for lvl 0 buildings, nothing happens.
		
	
		
		//-----IF YOU CHANGE PRICES HERE YOU MUST MODIFY RETURNPRICE----
	
/*
		 int j = 0;
		 Town holdT = g.findTown(townName,p); UserBuilding b; 
		
					if(!checkMP(holdT.townID)) return false;

				long cost[] = returnPrice("Engineer",number,holdT.townID);
				 
				 
				 boolean canBuild = true;
					int k = 0;
					
					 b = null;
					
					UserBuilding cys[] = getUserBuildings(holdTown.townID,"Command Center");
					
					if(b.getLvl()==0) break; 
					if(number>=(b.getCap()-(b.getPeopleInside()+b.getNumLeftToBuild()))) number =(int) (b.getCap()-(b.getPeopleInside()+b.getNumLeftToBuild()));
					k=0;
					 long res[] = holdT.getRes();

					do {
						if(res[k]<cost[k]) canBuild = false; // so if resources aren't enough...
						k++;
					} while(k<cost.length-1);
					
					
					if(canBuild) {
						
						 k = 0;
						do {
							res[k]-=cost[k];
							k++;
						} while(k<cost.length-1);
						holdT.setRes(res);
					//	holdT.getTotalEngineers()-=number*cost[4];
					//	p.totalPopulation-=number*cost[4]; // Remember cost is -1.
						// Don't add population here. It gets added by buildserver when they are actually
						// built.
						
						b.addUnit(number);
						b.modifyPeopleTicks(holdT.getTotalEngineers(),holdT.getPlayer().God.Maelstrom.getEngineerEffect(holdT.getX(),holdT.getY()),holdT.getPlayer().engTech);

						 notifyViewer();
						return true;
					} else return false;
			
*/
	}
	/**
	 * Build a number of traders in the town designated by the town id. Will automatically find the
	 * best building in which to build them, if possible.
	 * 
	 * @deprecated Use {@link #buildTrader(int lotNum, int number, int tid)} instead.
	 */	
	public boolean buildTrader(int number, int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return buildTrader(t.getTownName(),number);
		
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Build a number of traders on the lotNum in the town of the town id tid.
	 * 
	 * @param lotNum the lot our Trade Center is on
	 * @param number the amount of Traders to build
	 * @param tid the ID of the town we're building them in.
	 * 
	 * @return True, if construction has begun.  False otherwise.
	 */
	public boolean buildTrader(int lotNum, int number, int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}


		// Traders' price rise based on total population of traders in a town. Linearly. Players' resources
		// and everything else grows exponentially, so in order to meet that, these guys must grow
		// linearly - if they do grow proportional to population - and all population grows somewhat
		// exponentially, then we'll probably be okay.
		
		// form buildtrader(number,townName)
		
		// This will find the most leveled up comm center and use it. Need to add a second version for specifics.
		
		// Will not build if space is all filled.
		
		// Make sure for lvl 0 buildings, nothing happens.
		
	
		 int j = 0;
		 Town holdT = g.findTown(tid); Building b=null; Building holdB; Trade t;
		if(holdT.getPlayer().ID!=p.ID) { setError("Not your town!"); return false; }
					if(!checkMP(holdT.townID)) return false;
					long[] cost = returnPrice("Trader",number,holdT.townID);
				 

					
					if(canBuy("Trader",number,lotNum,holdT.townID)) {

						
					//	holdT.getTotalEngineers()-=number*cost[4];
					//	p.totalPopulation-=number*cost[4]; // Remember cost is -1.
						// Don't add population here. It gets added by buildserver when they are actually
						// built.
						int lvl = 0;
						UUID bid = null;
						/*
						try {
							UberStatement stmt = g.con.createStatement();
							ResultSet rs = stmt.executeQuery("select bid,lvl from bldg where tid = " + holdT.townID + " and slot = "+ lotNum + " and name = 'Trade Center'");
							if(rs.next()){ bid = rs.getInt(1); lvl = rs.getInt(2); }
							rs.close();
							stmt.close();
						} catch(SQLException exc) { exc.printStackTrace(); }*/
						ArrayList<Building> bldg = holdT.bldg();
						int i = 0;
						int totalCap=0;
						int currentlyBuilding=0;
						while(i<bldg.size()) {
							if(bldg.get(i).getType().equals("Trade Center")){
								totalCap+=bldg.get(i).getCap();
								currentlyBuilding+=bldg.get(i).getNumLeftToBuild();
							}
							if(bldg.get(i).getLotNum()==lotNum&&bldg.get(i).getType().equals("Trade Center")) {bid = bldg.get(i).getId(); lvl = bldg.get(i).getLvl(); }
							i++;
						}
						int maxNumber = totalCap-holdT.getTotalTraders()-currentlyBuilding; // this is the max amount that can be built

						if(lvl==0) { 
							setError("No fully constructed trade center on this lot!");
							return false;
						}
						 b = holdT.findBuilding(bid);
							if(number>=(b.getCap()-(b.getPeopleInside()+b.getNumLeftToBuild()))) number =(int) (b.getCap()-(b.getPeopleInside()+b.getNumLeftToBuild()));
							if(number>maxNumber) number = maxNumber;
							if(number==0) {
								setError("You cannot build zero traders!");
								return false;
							}
						int k = 0;
						 long res[] = holdT.getRes();
						 synchronized(res) { 
						do {
							res[k]-=cost[k];
							k++;
						} while(k<cost.length-1);
						 }
						b.addUnit(number);
						//b.modifyPeopleTicks(holdT.getTotalEngineers(),holdT.getPlayer().God.Maelstrom.getEngineerEffect(holdT.getX(),holdT.getY()),holdT.getPlayer().engTech);

						return true;
					} else return false;
			
	
	}
		/**
		 * Build a number of traders in the town designated by townName. Will automatically find the
		 * best building in which to build them, if possible.
		 * 
		 * This method, while not commented out, is extremely LAGGY as it was never streamlined! Use at your
		 * own risk!
		 * 
		 * @deprecated Use {@link #buildTrader(int lotNum, int number, int tid)} instead.
		 */
	public boolean buildTrader(String townName, int number) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}

		// Traders' price rise based on total population of traders in a town. Linearly. Players' resources
		// and everything else grows exponentially, so in order to meet that, these guys must grow
		// linearly - if they do grow proportional to population - and all population grows somewhat
		// exponentially, then we'll probably be okay.
		
		// form buildtrader(number,townName)
		
		// This will find the most leveled up comm center and use it. Need to add a second version for specifics.
		
		// Will not build if space is all filled.
		
		// Make sure for lvl 0 buildings, nothing happens.
		
		ArrayList<Trade> tres;
	
		long cost[] = new long[5];

		 int j = 0;
		 Town holdT; Building b; Building holdB; Trade t;
		 do {
			  holdT = p.towns().get(j);
			 int i = 0;
			 boolean haveIt=false;
			 boolean keep=false;
				if(prog) keep=true;
				prog=false;
				haveIt = haveBldg(townName,"Trade Center");
				if(keep) prog = true;
			 if(holdT.getTown().equals(townName)&&haveIt) {
					if(!checkMP(holdT.townID)) return false;

				 int currentlyBuilding = 0;
				 while(i<holdT.bldg().size()) {
					 if(holdT.bldg().get(i).getType().equals("Trade Center")) currentlyBuilding+=holdT.bldg().get(i).getNumLeftToBuild();
					 i++;
				 } // n_new(n_new+1)/2 - n_old(n_old+1)/2 is the cost to build all those units.
				 
				 int allCalled = holdT.getTotalTraders()+currentlyBuilding;
				 int all = number+allCalled;

				 double factor = all*(all+1)/2 - allCalled*(allCalled+1)/2;
				 //	public long[] returnPrice(String unitType, int number, int tid) {

				 cost = returnPrice("Trader",number,holdT.townID);
				 
				 
				 boolean canBuild = true;
					int k = 0;
					
			
					// So what we do down here is we figure out if when traders return,
					// they'd return to find no slot available due to these newly built units.
					int totalDearth=0;
					while(k<holdT.bldg().size()) {
						 holdB = holdT.bldg().get(k);

							if(holdB.getType().equals("Trade Center"))
							totalDearth+=(holdB.getCap()-holdB.getPeopleInside());
						k++;
					}
					int totalTradersOut=0;
					k=0;
					tres = holdT.tradeServer();
					while(k<tres.size()) {
						 t = tres.get(k);

							totalTradersOut+=t.getTraders();
						k++;
					}
					if((totalDearth-totalTradersOut)<number){
						setError("You do not have enough space to build this amount of traders!");
						return false;
					}
					
					k=0;
					 b= null;
					do {
						 holdB = holdT.bldg().get(k);
						if((holdB.getType().equals("Trade Center")&&b!=null&&holdB.getLvl()>b.getLvl())
								||(b==null&&holdB.getType().equals("Trade Center")))
							
							b = holdB;
						
						
			
					k++;	
					} while(k<holdT.bldg().size()); // Find largest one.
					
					if(b.getLvl()==0) break; 
					if(number>=(b.getCap()-(b.getPeopleInside()+b.getNumLeftToBuild()))) number =(int) (b.getCap()-(b.getPeopleInside()+b.getNumLeftToBuild()));
					k=0;
					 long res[] = holdT.getRes();

					do {
						if(res[k]<cost[k]) canBuild = false; // so if resources aren't enough...
						k++;
					} while(k<cost.length-1);
					
					
					if(canBuild) {
						
						 k = 0;
						do {
							res[k]-=cost[k];
							k++;
						} while(k<cost.length-1);
						holdT.setRes(res);
					//	holdT.getTotalEngineers()-=number*cost[4];
					//	p.totalPopulation-=number*cost[4]; // Remember cost is -1.
						// Don't add population here. It gets added by buildserver when they are actually
						// built.
						
						b.addUnit(number);
						b.modifyPeopleTicks(holdT.getTotalEngineers(),holdT.getPlayer().God.Maelstrom.getEngineerEffect(holdT.getX(),holdT.getY()),holdT.getPlayer().getArchitecture());

						 notifyViewer();
						return true;
					}
				 break; // because it's done after.
			 }
			 j++;
		 } while(j<p.towns().size());

		 return false;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Build a number of scholars in the town designated by the town id in the institute 
	 * designated by lotNum.
	 * 
	 * @param lotNum the lot our Institute is on
	 * @param number the amount of Scholars to build
	 * @param tid the ID of the town we're building them in
	 * 
	 * @return True, if construction has begun.  False otherwise.
	 */	
	public boolean buildSchol(int lotNum, int number, int tid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return buildSchol(t.getTownName(),number,lotNum);
	}
		/**
		 * @deprecated Use {@link #buildSchol(int lotNum, int number, int tid)} instead.
		 */
	public boolean buildSchol(String townName, int number, int lotNum) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}

		// Scholars' price rise based on total population of scholars. Linearly. Players' resources
		// and everything else grows exponentially, so in order to meet that, these guys must grow
		// linearly - if they do grow proportional to population - and all population grows somewhat
		// exponentially, then we'll probably be okay.
		
		// form buildscholar(number,townName)
		
		// This will find the most leveled up comm center and use it. Need to add a second version for specifics.
		
		// Will not build if space is all filled.
		
		// Make sure for lvl 0 buildings, nothing happens.
	
	
		long cost[] = new long[5];

		 int j = 0;
		 Town holdT = g.findTown(townName,p); UserBuilding b,holdB;
		
					if(!checkMP(holdT.townID)) return false;
//	public long[] returnPrice(String unitType, int number, int tid) {

				  cost = returnPrice("Scholar",number,holdT.townID);
				  int lvl = 0;
					UUID bid = null;
					/*try {
						UberStatement stmt = g.con.createStatement();
						ResultSet rs = stmt.executeQuery("select bid,lvl from bldg where tid = " + holdT.townID + " and slot = "+ lotNum + " and name = 'Institute'");
						if(rs.next()){ bid = rs.getInt(1); lvl = rs.getInt(2); }
						rs.close();
						stmt.close();
					} catch(SQLException exc) { exc.printStackTrace(); }*/
					
					int i = 0;
					ArrayList<Building> bldg = holdT.bldg();
					int totalCap=0;
					int currentlyBuilding=0;
					while(i<bldg.size()) {
						if(bldg.get(i).getType().equals("Institute")){
							totalCap+=bldg.get(i).getCap();
							currentlyBuilding+=bldg.get(i).getNumLeftToBuild();
						}
						if(bldg.get(i).getLotNum()==lotNum&&bldg.get(i).getType().equals("Institute")) {bid = bldg.get(i).getId(); lvl = bldg.get(i).getLvl(); }
						i++;
					}
					int maxNumber = totalCap-holdT.getTotalScholars()-currentlyBuilding; // this is the max amount that can be built
					// to still give room for scholars coming back from digs.
					
					
				 boolean canBuild = true;
					int k = 0;
					
				
				// Find largest one.
					// so first one is n+size(n+1)/2
					if(lvl==0){  setError("Cannot build scholars in a level 0 Institute!"); return false; } 
					b = getUserBuilding(bid);
					if(number>=(b.getCap()-(b.getPeopleInside()+b.getNumLeftToBuild()))) number =(int) (b.getCap()-(b.getPeopleInside()+b.getNumLeftToBuild()));
					if(number>maxNumber) number=maxNumber; // so a second check ensures you can only build up to your max.
					if(number==0) {
						setError("You cannot build zero scholars!");
						return false;
					}
					k=0;
					 long res[] = holdT.getRes();

					do {
						if(res[k]<cost[k]) canBuild = false; // so if resources aren't enough...
						k++;
					} while(k<cost.length-1);
					
					
					if(canBuild) {
						if(b.getLvl()<=0) {
							setError("Cannot build scholars in a level 0 Institute!");
							return false;
						}

						 k = 0;
						do {
							res[k]-=cost[k];
							k++;
						} while(k<cost.length-1);
						holdT.setRes(res);
					//	holdT.getTotalEngineers()-=number*cost[4];
					//	p.totalPopulation-=number*cost[4]; // Remember cost is -1.
						// Don't add population here. It gets added by buildserver when they are actually
						// built.
						Building actb = holdT.findBuilding(bid);
						actb.addUnit(number);
						//b.modifyPeopleTicks(holdT.getTotalEngineers(),holdT.getPlayer().God.Maelstrom.getEngineerEffect(holdT.getX(),holdT.getY()),holdT.getPlayer().engTech);

						 notifyViewer();
						return true;
					} else return false;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Determines if a UserGroup can be created called name with the Player names in usernames
	 * can be created.
	 * 
	 * @param name the name to give this UserGroup
	 * @param usernames a list of names that will be in this Group
	 * 
	 * @return True, if the UserGroup can be created.  False otherwise.
	 */
	public boolean canCreateUserGroup(String name, String[] usernames) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		int pid[] = new int[usernames.length];
		int i = 0;
		while(i<pid.length) {
			pid[i] = g.getPlayerId(usernames[i]);
			i++;
		}
		return canCreateUserGroup(name,pid);
	}
	/**
	 * Determines if a UserGroup called name containing the player IDs in pid can be created. 
	 * 
	 * @param name the name to give this UserGroup
	 * @param pid the IDs of all the players that will be in this Group
	 * 
	 * @return True, if the UserGroup can be created.  False otherwise.
	 */
	public boolean canCreateUserGroup(String name, int[] pid) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		try {
			UberPreparedStatement stmt = g.con.createStatement("select username from player where pid = ?");
			ResultSet rs;
			int i = 0;
			while(i<pid.length) {
				int j = 0;
				stmt.setInt(1,pid[i]);
				rs = stmt.executeQuery();
				if(!rs.next()){
					rs.close(); 
					stmt.close();

					return false;
					
				}
				while(j<pid.length) {
					
					if(pid[i]==pid[j]&&i!=j) {
						rs.close(); 
						stmt.close();
						return false;
					}
					j++;
				}
				i++;
			}
			stmt.close();
			stmt = g.con.createStatement("select name, usergroupid  from usergroups where pid = ?");
			stmt.setInt(1,p.ID);
			 rs = stmt.executeQuery();
			while(rs.next()) {
				if(rs.getString(1).equals(name)) {
					rs.close(); 
					stmt.close();
					return false;
				}
			}
			rs.close();
			stmt.close();
			return true;
		} catch(SQLException exc) { exc.printStackTrace(); }
		return false;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Creates a new user group for messaging of the name designated
	 * by the String name with the player IDs identified in the pid array.
	 * 
	 * @param name the name to give the new UserGroup
	 * @param usernames an array of Player names this Group
	 * 
	 * @return True, if the UserGroup was created.  False otherwise.
	 */
	public boolean createUserGroup(String name, String[] usernames) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		int pid[] = new int[usernames.length];
		int i = 0;
		while(i<pid.length) {
			pid[i] = g.getPlayerId(usernames[i]);
			i++;
		}
		return createUserGroup(name,pid);
	}
		/**
		 * Creates a new user group for messaging of the name designated
		 * by the String name with the player IDs identified in the pid array.
		 * 
		 * @param name the name to give this new UserGroup
		 * @param pid an array of Player ID that this Group references
		 * 
		 * @return True, if the UserGroup was created.  False otherwise.
		 */
	
	public boolean createUserGroup(String name, int[] pid) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
			try {	
				if(!canCreateUserGroup(name,pid))  return false;
				int i = 0; 
				
				UberPreparedStatement stmt = g.con.createStatement("insert into usergroups (name,pid) values (?,?);");
				ResultSet rs;
				stmt.setString(1,name);
				stmt.setInt(2,p.ID);
				stmt.execute();
				stmt.close();
			//stmt.execute("insert into usergroups (name,pid) values (\"" + name + "\"," + p.ID + ");");
			
			 i = 0;
			 stmt = g.con.createStatement("select usergroupid from usergroups where name = ? and pid = ?;");
			 stmt.setString(1,name);
			 stmt.setInt(2,p.ID);
			rs = stmt.executeQuery();
			rs.next();
			int ugid = rs.getInt(1);
			rs.close();
			stmt.close();
			stmt = g.con.createStatement("insert into usergroupmember (usergroupid,pid) values (?,?);");
			while(i<pid.length) {
			//	stmt.execute("insert into usergroupmember (usergroupid,pid) values (" + ugid + ","+ pid[i] + ");");
				stmt.setInt(1,ugid);
				stmt.setInt(2,pid[i]);
				stmt.execute();
				i++;
			}
			
			stmt.close();
			return true;
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
		return false;
	}
		/**
		 * UI Implemented.
		 * <br/><br/>
		 * Returns an array of UserGroup objects representing the player's UserGroups.
		 * 
		 * @return an array of {@link UserGroup UserGroups}.
		 */
	public UserGroup[] getUserGroups() {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return null;
		}
		try {
			UberPreparedStatement stmt = g.con.createStatement("select * from usergroups where pid = ?;");
			stmt.setInt(1,p.ID);
			ResultSet rs = stmt.executeQuery();
		//	ResultSet rs = stmt.executeQuery("select * from usergroups where pid = " + p.ID);
			ArrayList<UserGroup> ug = new ArrayList<UserGroup>();
			ResultSet rs2 = null; UberPreparedStatement stmt2=g.con.createStatement("select * from usergroupmember where usergroupid = ?;");
			ResultSet rs3=null; UberPreparedStatement stmt3=g.con.createStatement("select username,pid from player where pid = ?;");

			ArrayList<String> users;
			ArrayList<Integer> pids;
			UserGroup u;

			while(rs.next()) {
				//rs2=stmt2.executeQuery("select * from usergroupmember where usergroupid =" + rs.getInt(1));
				stmt2.setInt(1,rs.getInt(1));
				rs = stmt2.executeQuery();
				users = new ArrayList<String>();
				pids = new ArrayList<Integer>();

				while(rs2.next()) {
				//	public UserGroup(String name, int userGroupID, ArrayList<String> users) {
				//rs3 = stmt3.executeQuery("select username,pid from player where pid = " + rs2.getInt(3));
					stmt3.setInt(1,rs2.getInt(3));
					rs3 = stmt3.executeQuery();
				rs3.next();
				
				users.add(rs3.getString(1));
				pids.add(rs3.getInt(2));
				rs3.close();
				}
				rs2.close(); 
				
				
				
				u = new UserGroup(rs.getString(2),rs.getInt(1),users,pids);
				ug.add(u);
				
			}
			
			
			rs.close();stmt.close();  stmt2.close(); stmt3.close();
			
			if(p.getSupportstaff()) {
				users = new ArrayList<String>();
				pids = new ArrayList<Integer>();
				
				ArrayList<Player> ps= g.getPlayers();
				int i = 0;
				while(i<ps.size()) {
					users.add(ps.get(i).getUsername());
					pids.add(ps.get(i).ID);
					i++;
				}
				u = new UserGroup("all",0,users,pids);
				ug.add(u);
			}
			
			if(p.getLeague()!=null||p.isLeague()) {
				users = new ArrayList<String>();
				pids = new ArrayList<Integer>();
				int[] uspids;
				if(p.isLeague()) uspids = ((League) p).returnPIDs(p.ID);
				else uspids = p.getLeague().returnPIDs(p.ID);
				
				int i = 0;
				while(i<uspids.length) {
					if((p.getLeague()!=null&&p.getLeague().getType(uspids[i])>=0)||
							(p.isLeague()&&((League) p).getType(uspids[i])>=0)) {
					users.add(g.getPlayer(uspids[i]).getUsername());
					pids.add(uspids[i]);
					}
					i++;
				}
				u = new UserGroup("league",0,users,pids);
				ug.add(u);
				
			}
			int i = 0;
			UserGroup toRet[] = new UserGroup[ug.size()];
			while(i<ug.size()) {
				toRet[i] = ug.get(i);
				i++;
			}
			return toRet;
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
		return null;
		
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Determines if the UserGroup designated by name can be updated to contain the Player names in usernames
	 * 
	 * @param name 		the name of the UserGroup to be updated
	 * @param usernames	the names of the Players to put in this UserGroup.  Overwrites the existing names.
	 * @return
	 */
	public boolean canUpdateUserGroup(String name, String[] usernames) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		int pid[] = new int[usernames.length];
		int i = 0;
		while(i<pid.length) {
			pid[i] = g.getPlayerId(usernames[i]);
			i++;
		}
		return canUpdateUserGroup(name,pid);
	}
	
	/**
	 * Determines if the UserGroup designated by name can be updated to contain the IDs in pid
	 * 
	 * @param name	the name of the UserGroup to update
	 * @param pid	the IDs of the Players to put in the UserGroup.  Overwrites the existing IDs.
	 * 
	 * @return True, if the UserGroup can be updated.  False otherwise.
	 */
	public boolean canUpdateUserGroup(String name, int pid[]) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		if(name.equals("all")) return false;

			try {
				UberPreparedStatement stmt = g.con.createStatement("select username from player where pid = ?;");

				ResultSet rs; 
				int i = 0;
				while(i<pid.length) {
					int j = 0;
					//rs = stmt.executeQuery("select username from player where pid = " + pid[i]);
					stmt.setInt(1,pid[i]);
					rs = stmt.executeQuery();
					if(!rs.next()) {
						rs.close(); stmt.close();

						return false;
					}
					while(j<pid.length) {
						if(pid[i]==pid[j]&&i!=j) {
							rs.close(); stmt.close();

							return false;
						}
						j++;
					}
					i++;
				}
				stmt.close();
				stmt = g.con.createStatement("select name, usergroupid  from usergroups where pid = ?;");
				stmt.setInt(1,p.ID);
				 rs = stmt.executeQuery();
				while(rs.next()) {
					if(rs.getString(1).equals(name)){
						rs.close(); 
						stmt.close();
						return true; // reverse of create user group, must exist first.
					}
				}
				rs.close();
				stmt.close();
				return false;
			} catch(SQLException exc) { exc.printStackTrace(); }
			return false;
		}
	/**
	 * Updates a user group to add/remove new names. This completely erases the old user group, though,
	 * so please be careful to include all the original names as well!
	 * 
	 * @param name	the name of the UserGroup to update
	 * @param toAdd	an array of Player names to add to the UserGroup
	 * @param del 	If true, toAdd becomes "toRemove" and these names are removed, if possible.
	 * 
	 * @return True, if the UserGroup was updated.  False otherwise.
	 * 
	 * @see UserGroup
	 */
	public boolean updateUserGroup(String name, String[] toAdd, boolean del) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		int pid[] = new int[toAdd.length];
		int i = 0;
		while(i<pid.length) {
			pid[i] = g.getPlayerId(toAdd[i]);
			i++;
		}
		return updateUserGroup(name,pid,del);
	}
	/**
	 * Updates a user group to add/remove new names. This completely erases the old user group, though,
	 * so please be careful to include all the original names as well!
	 * 
	 * @param name	name of the UserGroup to update
	 * @param toAdd	an array of Player IDs to add to the UserGroup
	 * @param del 	If true, toAdd becomes "toRemove" and these pids are removed, if possible.
	 * 
	 * @return True, if the UserGroup was update.  False otherwise.
	 * 
	 * @see UserGroup
	 */
	public boolean updateUserGroup(String name, int[] toAdd, boolean del) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		if(name.equals("all")) return false;
		if(!canUpdateUserGroup(name,toAdd)) return false;
		UserGroup[] g = getUserGroups();
		int i = 0;
		UserGroup hold;
		
		while(i<g.length) {
			hold = g[i];
			if(hold.getName().equals(name)) {
				int j = 0;
				if(!del) {
				while(j<toAdd.length) {
				
					hold.getPIDs().add(new Integer(toAdd[j]));
					
					j++;
				} }
				else {
					
					while(j<toAdd.length) {
						int k = 0;
					while(k<hold.getPIDs().size()) {
						
						if(hold.getPIDs().get(k)==toAdd[j]) {
							hold.getPIDs().remove(k);
							k--;
						}
						
						k++;
					}
					j++;
					}
				}

				j = 0;
				int toMake[] = new int[hold.getPIDs().size()];
				while(j<hold.getPIDs().size()) {
					toMake[j]=hold.getPIDs().get(j);
					
					j++;
				}
				
				if(canUpdateUserGroup(name,toMake)) {
					deleteUserGroup(name);
					createUserGroup(name,toMake);
					return true;
				} else return false;
				
				
			}
			i++;
		}
		return false;
	}
		/**
		 * UI Implemented.
		 * <br/><br/>
		 * Deletes the UserGroup with the name designated by the String name.
		 * 
		 * @param name the name of the UserGroup to be deleted
		 * 
		 * @return True, if the UserGroup was deleted.  False otherwise.
		 */
	public boolean deleteUserGroup(String name) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		if(!checkLP()) return false;
		if(name.equals("all")) return false;
		try {
			UberPreparedStatement stmt = g.con.createStatement("select name,usergroupid from usergroups where pid = ? and name = ?;");
			//stmt.execute("start transaction;");
		//	ResultSet rs = stmt.executeQuery("select name,usergroupid from usergroups where pid = " + p.ID + " and name = \""+name + "\"");
			stmt.setInt(1,p.ID);
			stmt.setString(2,name);
			ResultSet rs = stmt.executeQuery();
			if(!rs.next()) {
				rs.close(); stmt.close();
				return false;
			}
				if(!rs.getString(1).equals(name)) {
					rs.close(); stmt.close();

					return false;
				}
			
			int i = 0;
			int usergroupid = rs.getInt(2);
			rs.close();
			stmt.close();
			stmt = g.con.createStatement("delete from usergroupmember where usergroupid = ?;");
			stmt.setInt(1,usergroupid);
			stmt.execute();
			stmt.close();
		//		stmt.execute("delete from usergroupmember where usergroupid = " + usergroupid + ";");
			stmt = g.con.createStatement("delete from usergroups where usergroupid = ?;");
			stmt.setInt(1,usergroupid);
			stmt.execute();
			stmt.close();
		//	stmt.execute("delete from usergroups where usergroupid = " + usergroupid + ";");
			stmt.close();
 
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
		return true;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns true if this user group exists.
	 * 
	 * @param name the name of the UserGroup to check
	 * 
	 * @return True, if the UserGroup exists.  False otherwise.
	 * 
	 * @see UserGroup
	 */
	public boolean userGroupExists(String name) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		if(name.equals("all")&&p.getSupportstaff()) return true;
		else if(name.equals("all")&&!p.getSupportstaff()) return false;
		try {
			UberPreparedStatement stmt = g.con.createStatement("select * from usergroups where name = ? and pid = ?;");
			stmt.setString(1,name);
			stmt.setInt(1,p.ID);
			ResultSet rs = stmt.executeQuery();
		//	ResultSet rs = stmt.executeQuery("select * from usergroups where name = \"" + name + "\" and pid = " + p.ID + ";");
			if(!rs.next())  {
				rs.close(); stmt.close();
				return false;
			}
			rs.close();stmt.close();
			return true;
		} catch(SQLException exc) { exc.printStackTrace(); }
		return false;
	}
	public UserMessage getMessage(UUID msgID) {
		return p.getMessage(msgID);
	}
		/**
		 * UI Implemented.
		 * Gets an array of UserMessagePacks that represents the state of the player's
		 * inbox.
		 */
	public UserMessagePack[] getMessages() {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return null;
		}

		
		
		int i=0;
		UserMessagePack toRet[] = new UserMessagePack[p.getMessages().size()];
		while(i<p.getMessages().size()) {
			toRet[i]=p.getMessages().get(i);
			i++;
		}
		return toRet;
		
	}
	/**
	 * UI implemented.
	 * <br/><br/>
	 * Marks the message with ID msgid as read.
	 * 
	 * @param msgid the ID of the message you'd like to mark read.
	 * 
	 * @return True, if the message was marked read.  False otherwise.
	 */
	public boolean markReadMessage(UUID msgid) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		UserMessage m = p.getMessage(msgid);
		if(m!=null) {
			m.setReaded(true);
			return true;
		} else {
			setError("Unable to find this message.");
			return false;
		}
	}
	/**
	 * UI implemented.
	 * <br/><br/>
	 * Marks the message with Id msgid not read.
	 * 
	 * @param msgid the IS of the message you'd like to mark not read
	 * 
	 * @return True, if the message is now marked unread.  False otherwise.
	 */
	public boolean markUnReadMessage(UUID msgid) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		UserMessage m = p.getMessage(msgid);
		if(m!=null) {
			m.setReaded(false);
			return true;
		} else {
			setError("Unable to find this message.");
			return false;
		}
	}
	/** 
	 * UI Implemented.
	 * <br/><br/>
	 * Deletes the message with the ID id.
	 * 
	 * @param id the ID of the message to be deleted
	 * 
	 * @return True, if the message was deleted.  False otherwise.
	 */
	public boolean markDeletedMessage(UUID id) {
		if(prog&&!p.isMessagingAPI()) {
			setError("You do not have the Messaging API!");
			return false;
		}
		
		UserMessage m = p.getMessage(id);
		if(m!=null) {
			m.setDeleted(true);
			return true;
		} else {
			setError("Unable to find this message.");
			return false;
		}
	}
	/**
	 * UI Implemented. 
	 * <br/><br/>
	 * For when you're done socializing.
	 * 
	 * @return True, if you've left your league.  False otherwise.
	 */
	public boolean leaveLeague() {
		if(p.getLeague()==null) {
			setError("Nobody loves you because you're not in a league!");
			return false;
		} else {

			if(p.getLeague().deleteTPR(p.ID)) {p.setLeague(null); return true; }
			else return false;
		}
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Create a new TPR. Must be done from a League player. taxRate is to be between 0-1, and
	 * tids is an array of townIDs that you want this player to be able to administrate for you.
	 * 
	 * @param taxRate 	the percentage of resources to deduct from the player with this rank (as a decimal).  IE .1 for 10%
	 * @param pid		the ID of the player this rank will apply to
	 * @param rank		the name to give this rank
	 * @param type		the numerical level of access this player has.  0 for members, 1 for officers, 2 for administrators
	 * @param tids		the IDs of all the league towns this player has access to.  Only applicable if the player is of type 1 or higher.
	 * 
	 * @return True, if the TPR was successfully created.  False otherwise.
	 */
	
	public boolean createTPR(double taxRate, int pid, String rank, int type, int[] tids) {
		if(!checkLP()) {
			return false;
		}

			if(!p.isLeague()){
				setError("Do this from your league player!");
				return false;
			}
			if(taxRate!=0) {
				setError("You cannot set taxrates on members currently. This is an upcoming feature.");
				return false;
			}
			return ((League) p).createTPR( taxRate,  pid,  rank,  type, tids,p.ID);
		
		
			
	}
	
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Deletes the TPR for the player with ID pid.
	 * 
	 * @param pid the ID of the player whose TPR we're deleting.
	 * 
	 * @return True, if the TPR was deleted.  False otherwise.
	 */
	public boolean deleteTPR(int pid) {

		if(!checkLP()) {
			return false;
		}

			if(!p.isLeague()){
				setError("Do this from your league player!");
				return false;
			}
			
			return ((League) p).deleteTPR(pid);
		
		
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns your TPR.
	 * 
	 * @return the {@link UserTPR} attached to your player or null, if there is none.
	 */
	public UserTPR getUserTPR() {
		if(p.getLeague()==null) {
			setError("Not in a league!");
			return null;
		} else {
			League l = p.getLeague();
			//	public UserTPR(String league, int pid, String player, String rank,
			//double taxRate, int[] tids, int tprID, int type) {
		UserTPR t = new UserTPR(l.getUsername(),p.ID,p.getUsername(),l.getRank(p.ID),l.getTaxRate(p.ID),l.getTIDs(p.ID),l.getTPRID(p.ID),l.getType(p.ID));
		return t;
		}
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns all the user tax-player-ranks you're allowed to see.
	 * 
	 * @return an array of all the {@link UserTPR UserTPRs} you have permission to see.
	 */
	public UserTPR[] getUserTPRs() {
		/*
		 * 	public UserTPR(String league, int pid, String player, String rank,
			double taxRate, int[] tids, int tprID, int type) {
		 */
		if(p.isLeague()) {
		UserTPR[] toRet;
		if(checkLP()) {
			
			int i = 0;int pids[];
			
			pids =( (League) p).returnPIDs(p.ID);
		
			toRet = new UserTPR[pids.length];
			while(i<pids.length) {
				
				toRet[i] =  new UserTPR(((League) p).getName(),pids[i],((League) p).getUsername(pids[i]),((League) p).getRank(pids[i]),
						((League) p).getTaxRate(pids[i]),((League) p).getTIDs(pids[i]),((League) p).getTPRID(pids[i]),((League) p).getType(pids[i]));
				i++;
			}
			
			return toRet;
		} else {
			setError("You do not have the permissions for this!");
			return null;
		}
		} else{
			setError("Call this from a league player!");
			return null;
		}
		
	}
	/**
	 * UI Implemented.
	 * Returns null if you're not in a league, returns a string array with information on it if you are.
	 * 0 is League Name
	 * 1 is League Letters
	 * 2 is League Website
	 * 3 is League Description.
	 * 
	 * @return
	 */
	public String[] getLeagueInfo() {
		if(p.getLeague()==null) {
			setError("You're not in a league!");
			return null;
		} else {
			String toRet[] = new String[4];
			toRet[0] = p.getLeague().getUsername();
			toRet[1]=p.getLeague().getLetters();
			toRet[2] = p.getLeague().getWebsite();
			toRet[3] = p.getLeague().getDescription();
			return toRet;
		}
		
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Create a new league, if you are not in one.
	 * 
	 * @param tid  			the ID of the town you're giving up to become the first League-owned town. Unless you specify otherwise, you will always be in control of all league towns.
	 * @param name  		the name to give the new League
	 * @param description 	the description to give the league
	 * @param website		the URL of your League's website (good place for a forum)
	 * @param letters  		the initials to give the League
	 * 
	 * @return True, if the league was successfully created.  False otherwise.
	 */
	 public boolean createLeague(String name, String description, String website, String letters) {
		if(p.getLeague()!=null) {
			setError("Already in a league!");
			return false;
		}
		if(p.towns().size()==1) {
			setError("Need more than one city.");
			return false;
		}
		int i = 0;
		 ArrayList<Player> players = g.getPlayers();

		while(i<players.size()) {
			if(players.get(i).equals(letters)) {
				setError("This username is already taken.");
				return false;
			}
			i++;
		}/*
		if(tid==p.getCapitaltid()) {
			error = "Your capital cannot be made into a League city!";
			return false;
		}
		Town t = g.findTown(tid);
	

		if(t.getPlayer().ID!=p.ID) {
			error = "Can't take a town that isnt yours!";
		}
		
		*/
		League newLeague = (League) g.createNewPlayer(letters,"4p5v3sxQ",1, -1,"0000","nolinkedemail",true,0,0,false,0);
		//public void createLeague(String leagueName,String leagueLetters, Player initial) {
		if(newLeague==null){
			error = "Player create failed for some reason.";
			return false; // For some reason it didn't work!
		}
		
		newLeague.createLeague(name,letters,description,website,p);

		return true;
	}
	
	
	/**
 	 * Returns true if you have the amount of metal specified by number in town
 	 * specified by town id.
 	 * 
 	 * @param number	the amount of resources to check for
 	 * @param tid		the ID of the town to check in
 	 * 
	 * @return True, if the town has, at least, number Metal.  False otherwise.
	 */
	public boolean haveMetal(int number, int tid) {
		pushLog("haveMetal("+number+","+tid+");");
		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) {setError("Not your town!"); return false;};
		if(!checkMP(t.townID)) return false;
		return t.getRes()[0]>=number;
	}
	/**
 	 * Returns true if you have the amount of metal identified by condResNum, in the town
	 * with the name specified by condName.
	 * 
	 * @param condResNum	the amount of resources to check for
	 * @param condName		the name of the town to check in
	 * 
	 * @return True, if the town has, at least, number Metal.  False otherwise.
	 * 
	 * @deprecated Use {@link #haveMetal(int number, int tid)} instead.
	 */
	public boolean haveMetal(int condResNum, String condName) {
		pushLog("haveMetal("+condResNum+","+condName+");");

		// Problems: Cond could have multiple holdmetals. This is just one problem with this implementation.
		// Also, if town doesn't exist, should throw some sort of error. Need to make new exceptions
		// and present them to the user during compilation of his/her script at some point.
		
		Town t = g.findTown(condName, p);
		if(t.getPlayer().ID!=p.ID) { setError("Not your town!"); return false;}
		if(!checkMP(t.townID)) return false;

		//System.out.println("As I check, " + player.towns().get(holdI).res[0] + " and " + condRes);
		return t.getRes()[0]>=condResNum;
		
	}
	/**
 	 * Returns true if you have the amount of timber specified by number in town
 	 * specified by town id.
 	 * 
 	 * @param number	the amount of resources to check for
 	 * @param tid		the ID of the town to check in
 	 * 
	 * @return True, if the town has, at least, number Timber.  False otherwise.
	 */
	public boolean haveTimber(int number, int tid) {
		pushLog("haveTimber("+number+","+tid+");");

		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return t.getRes()[1]>=number;
	}
		/**
		 * Returns true if you have the amount of timber identified by condResNum, in the town
		 * with the name specified by condName.
		 * 
		 * @param condResNum	the amount of resources to check for
		 * @param condName		the name of the town to check in
		 * 
		 * @return True, if the town has, at least, condResNum Timber.  False otherwise.
		 * 
		 * @deprecated Use {@link #haveTimber(int number, int tid)} instead.
		 */
	public boolean haveTimber(int condResNum, String condName) {

		pushLog("haveTimber("+condResNum+","+condName+");");

		Town t = g.findTown(condName, p);
		if(t.getPlayer().ID!=p.ID) { setError("Not your town!"); return false;}
		if(!checkMP(t.townID)) return false;

		//System.out.println("As I check, " + player.towns().get(holdI).res[0] + " and " + condRes);
		return t.getRes()[1]>=condResNum;
		//	System.out.println("As I check 2ndly, " + player.towns().get(holdI).res[0] + " and " + condRes);

	}
	
	/**
	 * 
	 * @param number
	 * @param tid
	 * @return
	 */
	public boolean haveCrystal(int number, int tid) {
		pushLog("haveCrystal("+number+","+tid+");");

		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return t.getRes()[2]>=number;
	}
	/**
 	 * Returns true if you have the amount of crystal specified by number in town
 	 * specified by tid.
 	 * 
 	 * @param number	the amount of Crystal to check for
 	 * @param tid		the ID of the town to check in
 	 * 
	 * @return True, if the town has, at least, number Crystal.  False otherwise.
	 * 
	 * @deprecated Use {@link #haveCrystal(int number, int tid)} instead.
	 */
	public boolean haveManMat(int number, int tid) {
		pushLog("haveManMat("+number+","+tid+");");

		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return t.getRes()[2]>=number;
	}
		/**
		 * Returns true if you have the amount of crystal identified by condResNum, in the town
		 * with the name specified by condName.
		 * 
		 * @param condResNum	the amount of Crystal to check for
		 * @param condName		the name of the town to check in
		 * 
		 * @return True, if the town has, at least, condResNum crystal.  False otherwise.
		 * 
		 * @deprecated Use {@link #haveCrystal(int number, int tid)} instead.
		 */
	public boolean haveManMat(int condResNum, String condName) {
		pushLog("haveManMat("+condResNum+","+condName+");");

		
		Town t = g.findTown(condName, p);
		if(t.getPlayer().ID!=p.ID) { setError("Not your town!"); return false;}
		if(!checkMP(t.townID)) return false;

		//System.out.println("As I check, " + player.towns().get(holdI).res[0] + " and " + condRes);
		return t.getRes()[2]>=condResNum;
		//	System.out.println("As I check 2ndly, " + player.towns().get(holdI).res[0] + " and " + condRes);
	}
	/**
 	 *Returns true if you have the amount of food specified by number in town
 	 *specified by town id.
 	 *
 	 * @param number	the amount of food to check for
 	 * @param tid		the ID of the town to check in
 	 *
	 * @return True, if the town has at least number food.  False, otherwise.
	 */
	public boolean haveFood(int number, int tid) {
		pushLog("haveFood("+number+","+tid+");");

		Town t = g.findTown(tid);
		if(t.getPlayer().ID!=p.ID) return false;
		return haveFood(number,t.getTownName());
	}
		/**
		 * Returns true if you have the amount of food identified by condResNum, in the town
		 * with the name specified by condName.
		 * 
		 * @param condResNum	the amount of food to check for
		 * @param condName		the name of the town to search in
		 * 
		 * @deprecated Use {@link #haveFood(int number, int tid)} instead.
		 */
	public boolean haveFood(int condResNum, String condName) {

		pushLog("haveFood("+condResNum+","+condName+");");

		Town t = g.findTown(condName, p);
		if(t.getPlayer().ID!=p.ID) { setError("Not your town!"); return false;}
		if(!checkMP(t.townID)) return false;

		//System.out.println("As I check, " + player.towns().get(holdI).res[0] + " and " + condRes);
		if(t.getRes()[3]>=condResNum) {
		//	System.out.println("As I check 2ndly, " + player.towns().get(holdI).res[0] + " and " + condRes);

			return true;
		} else{
			return false;
		}
	}
	
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns true if you can research the list in the array, false with error if not. Used in completeResearches
	 * to figure out of you can access those researches.
	 * 
	 * @param array a String array of the names of the researches you would like to purchase
	 * 
	 * @return True, if you can purchase ALL the researches specified in array.  False otherwise.
	 */
	public boolean canCompleteResearches(String[] array) {
		if(prog&&!p.isResearchAPI()) {
			setError("You do not have the Research API!");
			return false;
		}
		return canCompleteResearches(array,false);
	}
	
	private boolean canCompleteResearches(String array[], boolean free) {
	int i = 0; int hypoTotal=p.getKnowledge();
	 i = 0;
	
		while(i<array.length) {
		
		
		//20*2(level-8)/2 KP
			if(array[i].equals("infrastructureTech")) {
				if(!free&&hypoTotal<GodGenerator.infrastructureTechPrice*Math.pow(2,(p.getInfrastructureTech()-8)/2)) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.infrastructureTechPrice*Math.pow(2,(p.getInfrastructureTech()-8)/2);
				if(p.getInfrastructureTech()>=GodGenerator.infrastructureTechLimit) {
					setError("You cannot research any further in this field.");
					return false;

				} 
			} else if(array[i].equals("scoutTech")) {
				if(!free&&hypoTotal<GodGenerator.scoutTechPrice*Math.pow(2,(p.getScoutTech()/2))) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.scoutTechPrice*Math.pow(2,(p.getScoutTech()/2));
				if(p.getScoutTech()>=20) {
					setError("You cannot research any further in this field.");
					return false;

				}//50*2unlocked_soldiers KP
			} else if(array[i].equals("Panzerfaust")||array[i].equals("Pillager")||array[i].equals("Vanguard")) {
				int k = 0;
				for(AttackUnit a:p.getAu()) {
					if(a.getType()==1) k++;
					if(a.getName().equals(array[i])){
						setError("You already have this unit!");
						return false;
					}
				}
				if(!free&&hypoTotal<GodGenerator.soldierPrice*Math.pow(2,k)) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.soldierPrice*Math.pow(2,k);
			}else if(array[i].equals("Wolverine")||array[i].equals("Seeker")||array[i].equals("Damascus")) {
				int k = 0;
				for(AttackUnit a:p.getAu()) {
					if(a.getType()==2) k++;
					if(a.getName().equals(array[i])){
						setError("You already have this unit!");
						return false;
					}
				}
				if(p.towns().size()<2) {
					setError("You need at least 2 towns to research tanks!");
					return false;
				}

			
				if(!free&&hypoTotal<GodGenerator.tankPrice*Math.pow(2,k)) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.tankPrice*Math.pow(2,k);

			}else if(array[i].equals("Punisher")||array[i].equals("Dreadnaught")||array[i].equals("Collossus")) {
				int k = 0;
				for(AttackUnit a:p.getAu()) {
					if(a.getType()==3) k++;
					if(a.getName().equals(array[i])){
						setError("You already have this unit!");
						return false;
					}
				}				if(p.towns().size()<3) {
					setError("You need at least 3 towns to research golems!");
					return false;

				}
				
				if(!free&&hypoTotal<GodGenerator.golemPrice*Math.pow(2,k)) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.golemPrice*Math.pow(2,k);

			}else if(array[i].equals("Gunship")||array[i].equals("Thunderbolt")
					||array[i].equals("Blastmaster")){
				int k = 0;
				for(AttackUnit a:p.getAu()) {
					if(a.getType()==4&&
							(a.getName().contains("Gunship")||a.getName().contains("Blastmaster")||a.getName().contains("Thunderbolt"))) k++;
					if(a.getName().contains(array[i])){
						setError("You already have this unit!");
						return false;
					}
				}			
				if(p.towns().size()<4) {
					setError("You need at least 4 towns to research air units!");
					return false;

				}
				
				if(!free&&hypoTotal<GodGenerator.airPrice*Math.pow(2,k)) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.airPrice*Math.pow(2,k);

			}else if(array[i].equals("Monolith")
					||array[i].equals("Halcyon")||array[i].equals("Hades")){
				int k = 0;
				for(AttackUnit a:p.getAu()) {
					if(a.getType()==4&&
							(a.getName().contains("Monolith")||a.getName().contains("Halcyon")||a.getName().contains("Hades"))) k++;	
					if(a.getName().contains(array[i])){
						setError("You already have this unit!");
						return false;
					}
				}			
				if(p.towns().size()<4) {
					setError("You need at least 4 towns to research air units!");
					return false;

				}
				
				if(!free&&hypoTotal<GodGenerator.airPrice*Math.pow(2,k)) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.airPrice*Math.pow(2,k);

			}else if(array[i].equals("zeppelinTech")) {
				
					if(p.isAirshipTech())  {
						setError("You already have this technology!");
						return false;
					}
				
				if(!free&&hypoTotal<GodGenerator.airshipTechPrice) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.airshipTechPrice;
			}else if(array[i].equals("attackAPI")) {
			
			if(p.isAttackAPI())  {
				setError("You already have this technology!");
				return false;
			}
		
		if(!free&&hypoTotal<GodGenerator.attackAPITechPrice) {
			setError("You do not have enough KP for this research.");
			return false;
		} else hypoTotal-=GodGenerator.attackAPITechPrice;
		}else if(array[i].equals("advancedAttackAPI")) {
			
			if(p.isAdvancedAttackAPI())  {
				setError("You already have this technology!");
				return false;
			}
		
		if(!free&&hypoTotal<GodGenerator.advancedAttackAPITechPrice) {
			setError("You do not have enough KP for this research.");
			return false;
		} else hypoTotal-=GodGenerator.advancedAttackAPITechPrice;
		}else if(array[i].equals("digAPI")) {
			
			if(p.isdigAPI())  {
				setError("You already have this technology!");
				return false;
			}
		
		if(!free&&hypoTotal<GodGenerator.digAPITechPrice) {
			setError("You do not have enough KP for this research.");
			return false;
		} else hypoTotal-=GodGenerator.digAPITechPrice;
		}else if(array[i].equals("tradingAPI")) {
			
			if(p.isTradingAPI())  {
				setError("You already have this technology!");
				return false;
			}
		
		if(!free&&hypoTotal<GodGenerator.tradingAPITechPrice) {
			setError("You do not have enough KP for this research.");
			return false;
		} else hypoTotal-=GodGenerator.tradingAPITechPrice;
		}else if(array[i].equals("advancedTradingAPI")) {
			
			if(p.isAdvancedTradingAPI())  {
				setError("You already have this technology!");
				return false;
			}
		
		if(!free&&hypoTotal<GodGenerator.advancedTradingAPITechPrice) {
			setError("You do not have enough KP for this research.");
			return false;
		} else hypoTotal-=GodGenerator.advancedTradingAPITechPrice;
		}else if(array[i].equals("smAPI")) {
			
			if(p.isSmAPI())  {
				setError("You already have this technology!");
				return false;
			}
		
		if(!free&&hypoTotal<GodGenerator.smAPITechPrice) {
			setError("You do not have enough KP for this research.");
			return false;
		} else hypoTotal-=GodGenerator.smAPITechPrice;
		}else if(array[i].equals("researchAPI")) {
			
			if(p.isResearchAPI())  {
				setError("You already have this technology!");
				return false;
			}
		
		if(!free&&hypoTotal<GodGenerator.researchAPITechPrice) {
			setError("You do not have enough KP for this research.");
			return false;
		} else hypoTotal-=GodGenerator.researchAPITechPrice;
		}else if(array[i].equals("buildingAPI")) {
			
			if(p.isBuildingAPI())  {
				setError("You already have this technology!");
				return false;
			}
		
		if(!free&&hypoTotal<GodGenerator.buildingAPITechPrice) {
			setError("You do not have enough KP for this research.");
			return false;
		} else hypoTotal-=GodGenerator.buildingAPITechPrice;
		}else if(array[i].equals("advancedBuildingAPI")) {
			
			if(p.isAdvancedBuildingAPI())  {
				setError("You already have this technology!");
				return false;
			}
		
		if(!free&&hypoTotal<GodGenerator.advancedBuildingAPITechPrice) {
			setError("You do not have enough KP for this research.");
			return false;
		} else hypoTotal-=GodGenerator.advancedBuildingAPITechPrice;
		}else if(array[i].equals("messagingAPI")) {
			
			if(p.isMessagingAPI())  {
				setError("You already have this technology!");
				return false;
			}
		
		if(!free&&hypoTotal<GodGenerator.messagingAPITechPrice) {
			setError("You do not have enough KP for this research.");
			return false;
		} else hypoTotal-=GodGenerator.messagingAPITechPrice;
		}else if(array[i].equals("zeppelinAPI")) {
			
			if(p.isZeppelinAPI())  {
				setError("You already have this technology!");
				return false;
			}
		
		if(!free&&hypoTotal<GodGenerator.zeppelinAPITechPrice) {
			setError("You do not have enough KP for this research.");
			return false;
		} else hypoTotal-=GodGenerator.zeppelinAPITechPrice;
		}else if(array[i].equals("completeAnalyticAPI")) {
			
			if(p.isCompleteAnalyticAPI())  {
				setError("You already have this technology!");
				return false;
			}
			
			if(!p.isAdvancedAttackAPI()||!p.isAdvancedTradingAPI()||!p.isAdvancedBuildingAPI()) {
				setError("You do not have all of the advanced APIs yet!");
				return false;
			}
		
		if(!free&&hypoTotal<GodGenerator.completeAnalyticAPITechPrice) {
			
			setError("You do not have enough KP for this research.");
			return false;
		} else hypoTotal-=GodGenerator.completeAnalyticAPITechPrice;
		}else if(array[i].equals("nukeAPI")) {
			
			if(p.isNukeAPI())  {
				setError("You already have this technology!");
				return false;
			}
		
		if(!free&&hypoTotal<GodGenerator.nukeAPITechPrice) {
			setError("You do not have enough KP for this research.");
			return false;
		} else hypoTotal-=GodGenerator.nukeAPITechPrice;
		}else if(array[i].equals("worldMapAPI")) {
			
			if(p.isWorldMapAPI())  {
				setError("You already have this technology!");
				return false;
			}
		
		if(!free&&hypoTotal<GodGenerator.worldMapAPITechPrice) {
			setError("You do not have enough KP for this research.");
			return false;
		} else hypoTotal-=GodGenerator.worldMapAPITechPrice;
		}	else if(array[i].equals("townTech")) {
	
				// we know that we want xn+1 = 2*xn = 2*(2xn-1) = 2*2*2xn-2 = 2^n*x0.
				// can we calculate 2*xn? well...we know x0 is 200. 
				// so we can get what we need.
				// towntechprice = 200*2^n, n is towns-1,which gives me 200, then 2 gives me 400, 3 gives me 800. 
				if(!free&&hypoTotal<GodGenerator.townTechPrice*Math.pow(2,(p.getTownTech()-1))) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.townTechPrice*Math.pow(2,(p.getTownTech()-1));
			}else if(array[i].equals("architecture")) {
				if(!free&&hypoTotal<GodGenerator.civEfficiencyPrice*Math.pow(2,(p.getArchitecture()/2))) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.civEfficiencyPrice*Math.pow(2,(p.getArchitecture()/2));
				if(p.getArchitecture()>=20) {
					setError("You can not research any further in this field.");
					return false;
				}

			}else if(array[i].equals("clockworkComputers")) {
				if(!free&&hypoTotal<GodGenerator.civEfficiencyPrice*Math.pow(2,(p.getClockworkComputers()/2))) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.civEfficiencyPrice*Math.pow(2,(p.getClockworkComputers()/2));
				if(p.getClockworkComputers()>=20) {
					setError("You can not research any further in this field.");
					return false;
				} 

			}else if(array[i].equals("constructionResearch")) {
				if(!free&&hypoTotal<GodGenerator.constructionResearchPrice*Math.pow(2,(p.getConstructionResearch()/2))) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.constructionResearchPrice*Math.pow(2,(p.getConstructionResearch()/2));
				if(p.getConstructionResearch()>=10) {
					setError("You can not research any further in this field.");
					return false;
				} 

			}else if(array[i].equals("structuralIntegrity")) {
				if(!free&&hypoTotal<GodGenerator.structuralIntegrityPrice*Math.pow(2,(p.getStructuralIntegrity()/2))) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.structuralIntegrityPrice*Math.pow(2,(p.getStructuralIntegrity()/2));
				if(p.getStructuralIntegrity()>=10) {
					setError("You can not research any further in this field.");
					return false;
				}  			

			}else if(array[i].equals("bloodMetalPlating")) {
				if(!free&&hypoTotal<GodGenerator.bloodMetalPlatingPrice*Math.pow(2,(p.getBloodMetalPlating()/2))) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.bloodMetalPlatingPrice*Math.pow(2,(p.getBloodMetalPlating()/2));
				if(p.getBloodMetalPlating()>=20) {
					setError("You can not research any further in this field.");
					return false;
				} 

			} else if(array[i].equals("firearmResearch")) {
				if(!free&&hypoTotal<GodGenerator.firearmResearchPrice*Math.pow(2,(p.getFirearmResearch()/2))) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.firearmResearchPrice*Math.pow(2,(p.getFirearmResearch()/2));
				if(p.getFirearmResearch()>=20) {
					setError("You can not research any further in this field.");
					return false;
				} 

			}else if(array[i].equals("ordinanceResearch")) {
				if(!free&&hypoTotal<GodGenerator.ordinanceResearchPrice*Math.pow(2,(p.getOrdinanceResearch()/2))) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.ordinanceResearchPrice*Math.pow(2,(p.getOrdinanceResearch()/2));
				if(p.getOrdinanceResearch()>=20) {
					setError("You can not research any further in this field.");
					return false;
				} 

			}else if(array[i].equals("teslaTech")) {
				if(!free&&hypoTotal<GodGenerator.teslaTechPrice*Math.pow(2,(p.getTeslaTech()/2))) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.teslaTechPrice*Math.pow(2,(p.getTeslaTech()/2));
				if(p.getTeslaTech()>=20) {
					setError("You can not research any further in this field.");
					return false;
				} 

			}else if(array[i].equals("bodyArmor")) {
				if(!free&&hypoTotal<GodGenerator.bodyArmorPrice*Math.pow(2,(p.getBodyArmor()/2))) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.bodyArmorPrice*Math.pow(2,(p.getBodyArmor()/2));
				if(p.getBodyArmor()>=20) {
					setError("You can not research any further in this field.");
					return false;
				} 

			}else if(array[i].equals("bloodMetalArmor")) {
				if(!free&&hypoTotal<GodGenerator.bloodMetalArmorPrice) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.bloodMetalArmorPrice;
				if(p.getBloodMetalArmor()) {
					setError("You can not research any further in this field.");
					return false;
				} 

			}else if(array[i].equals("personalShields")) {
				if(!free&&hypoTotal<GodGenerator.personalShieldsPrice) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.personalShieldsPrice;
				if(p.isPersonalShields()) {
					setError("You can not research any further in this field.");
					return false;
				} 

			}else if(array[i].equals("thrustVectoring")) {
				if(!free&&hypoTotal<GodGenerator.thrustVectoringPrice) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.thrustVectoringPrice;
				if(p.isThrustVectoring()) {
					setError("You can not research any further in this field.");
					return false;
				} 

			}else if(array[i].equals("hydraulicAssistors")) {
				if(!free&&hypoTotal<GodGenerator.hydraulicAssistorsPrice) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.hydraulicAssistorsPrice;
				if(p.isHydraulicAssistors()) {
					setError("You can not research any further in this field.");
					return false;
				} 

			}else if(array[i].equals("clockworkAugments")) {
				if(!free&&hypoTotal<GodGenerator.clockworkAugmentsPrice) {
					setError("You do not have enough KP for this research.");
					return false;
				} else hypoTotal-=GodGenerator.clockworkAugmentsPrice;
				if(p.isClockworkAugments()) {
					setError("You can not research any further in this field.");
					return false;
				} 

			}else {
				setError("Invalid research!");
				return false;
			}
			
			
			
		
			
			
			i++;
		}
		return true;
	}
	
	
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns true if you can purchase the researches listed in array.
	 * 
	 * @param array a list of researches you wish to purchase
	 * 
	 * @return True, if the reseaches were purchased.  False otherwise.
	 */
	public boolean completeResearches(String[] array) {
		if(prog&&!p.isResearchAPI()) {
			setError("You do not have the Research API!");
			return false;
		}
		return completeResearches(array,false);
	}
	/**
	 * Allows you to get free researches from private calls!
	 */
	private boolean completeResearches(String array[], boolean free) {
		int i = 0;
		if(QuestListener.partOfQuest(p,"BQ5")) {
			try {
				((BQ5) g.getPlayer(g.getPlayerId("BQ5"))).callMeIfResearched(p.ID);
			} catch(Exception exc) { 
				exc.printStackTrace();
				System.out.println("System saved. Couldn't find BQ5 player for research.");
				
			}
		}
	
		Town t; Building b;
		if(canCompleteResearches(array,free)) {// if it's free... then this'll return true depending.
			
			if(QuestListener.partOfQuest(p,"BQ5")) {
				try {
					((BQ5) g.getPlayer(g.getPlayerId("BQ5"))).callMeIfResearched(p.ID);
				} catch(Exception exc) { 
					exc.printStackTrace();
					System.out.println("System saved. Couldn't find BQ5 player for research.");
					
				}
			}
			
			while(i<array.length) {
				
				if(array[i].equals("infrastructureTech")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()- GodGenerator.infrastructureTechPrice*(Math.pow(2,(p.getInfrastructureTech()-8)/2))));
					 p.setInfrastructureTech(p.getInfrastructureTech() + 1);
				} else if(array[i].equals("scoutTech")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.scoutTechPrice*(Math.pow(2,p.getScoutTech()/2))));
					 p.setScoutTech(p.getScoutTech() + 1);
				}else if(array[i].equals("zeppelinTech")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.airshipTechPrice);
					p.setAirshipTech(true);
				}else if(array[i].equals("attackAPI")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.attackAPITechPrice);
					p.setAttackAPI(true);
				}else if(array[i].equals("digAPI")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.digAPITechPrice);
					p.setdigAPI(true);
				}else if(array[i].equals("advancedAttackAPI")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.advancedAttackAPITechPrice);
					p.setAdvancedAttackAPI(true);
				}else if(array[i].equals("tradingAPI")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.tradingAPITechPrice);
					p.setTradingAPI(true);
				}else if(array[i].equals("advancedTradingAPI")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.advancedTradingAPITechPrice);
					p.setAdvancedTradingAPI(true);
				}else if(array[i].equals("smAPI")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.smAPITechPrice);
					p.setSmAPI(true);
				}else if(array[i].equals("researchAPI")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.researchAPITechPrice);
					p.setResearchAPI(true);
				}else if(array[i].equals("buildingAPI")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.buildingAPITechPrice);
					p.setBuildingAPI(true);
				}else if(array[i].equals("advancedBuildingAPI")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.advancedBuildingAPITechPrice);
					p.setAdvancedBuildingAPI(true);
				}else if(array[i].equals("messagingAPI")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.messagingAPITechPrice);
					p.setMessagingAPI(true);
				}else if(array[i].equals("zeppelinAPI")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.zeppelinAPITechPrice);
					p.setZeppelinAPI(true);
				}else if(array[i].equals("completeAnalyticAPI")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.completeAnalyticAPITechPrice);
					p.setCompleteAnalyticAPI(true);
				}else if(array[i].equals("nukeAPI")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.nukeAPITechPrice);
					p.setNukeAPI(true);
				}else if(array[i].equals("worldMapAPI")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.worldMapAPITechPrice);
					p.setWorldMapAPI(true);
				}else if(array[i].equals("clockworkAugments")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.clockworkAugmentsPrice);
					p.setClockworkAugments(true);
				}else if(array[i].equals("advancedFortifications")) { 
					if(!free) p.setKnowledge(p.getKnowledge()-GodGenerator.advancedFortificationsPrice);
					p.setAdvancedFortifications(true);
				}else if(array[i].equals("townTech")) {
					if(!free) p.setKnowledge(p.getKnowledge()-(int) Math.round(GodGenerator.townTechPrice*Math.pow(2,(p.getTownTech()-1))));
	
					p.setTownTech(p.getTownTech() + 1);
				}else if(array[i].equals("architecture")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.civEfficiencyPrice*Math.pow(2,(p.getArchitecture()/2))));
	
					 p.setArchitecture(p.getArchitecture() + 1);
				}else if(array[i].equals("clockworkComputers")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.civEfficiencyPrice*Math.pow(2,(p.getClockworkComputers()/2))));
	
					p.setClockworkComputers(p.getClockworkComputers() + 1);
				}else if(array[i].equals("constructionResearch")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.constructionResearchPrice*Math.pow(2,(p.getConstructionResearch()/2))));
	
					p.setConstructionResearch(p.getConstructionResearch() + 1);
				}else if(array[i].equals("structuralIntegrity")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.structuralIntegrityPrice*Math.pow(2,(p.getStructuralIntegrity()/2))));
	
					p.setStructuralIntegrity(p.getStructuralIntegrity() + 1);
				}else if(array[i].equals("firearmResearch")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.firearmResearchPrice*Math.pow(2,(p.getFirearmResearch()/2))));
	
					p.setFirearmResearch(p.getFirearmResearch() + 1);
				}else if(array[i].equals("ordinanceResearch")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.ordinanceResearchPrice*Math.pow(2,(p.getOrdinanceResearch()/2))));
	
					p.setOrdinanceResearch(p.getOrdinanceResearch() + 1);
				}else if(array[i].equals("teslaTech")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.teslaTechPrice*Math.pow(2,(p.getTeslaTech()/2))));
	
					p.setTeslaTech(p.getTeslaTech() + 1);
				}else if(array[i].equals("bloodMetalPlating")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.bloodMetalPlatingPrice*Math.pow(2,(p.getBloodMetalPlating()/2))));
	
					p.setBloodMetalPlating(p.getBloodMetalPlating() + 1);
				}else if(array[i].equals("bodyArmor")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.bodyArmorPrice*Math.pow(2,(p.getBodyArmor()/2))));
	
					p.setBodyArmor(p.getBodyArmor() + 1);
				}else if(array[i].equals("bloodMetalArmor")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.bloodMetalArmorPrice));
	
					p.setBloodMetalArmor(true);
				}else if(array[i].equals("personalShields")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.personalShieldsPrice));
	
					p.setPersonalShields(true);
				}else if(array[i].equals("thrustVectoring")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.thrustVectoringPrice));
	
					p.setThrustVectoring(true);
				}else if(array[i].equals("hydraulicAssistors")) {
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.hydraulicAssistorsPrice));
	
					p.setHydraulicAssistors(true);
				}else if(array[i].equals("Panzerfaust")||array[i].equals("Pillager")||array[i].equals("Vanguard")) {
					int k = 0;
					for(AttackUnit a: p.getAu()) {
						
						if(a.getType()==1) k++;
					}
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.soldierPrice*Math.pow(2,k)));
	
					p.addAu(new AttackUnit(array[i],0,0));
				}else if(array[i].equals("Seeker")||array[i].equals("Damascus")||array[i].equals("Wolverine")) {
					int k = 0;
					for(AttackUnit a: p.getAu()) {
						
						if(a.getType()==2) k++;
					}
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.tankPrice*Math.pow(2,k)));
	
					p.addAu(new AttackUnit(array[i],0,0));
				}else if(array[i].equals("Punisher")||array[i].equals("Dreadnaught")||array[i].equals("Collossus")) {
					int k = 0;
					for(AttackUnit a: p.getAu()) {
						
						if(a.getType()==3) k++;
					}
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.golemPrice*Math.pow(2,k)));
	
					p.addAu(new AttackUnit(array[i],0,0));
				}else if(array[i].equals("Gunship")||array[i].equals("Thunderbolt")||array[i].equals("Blastmaster")
						) {
					int k = 0;
					for(AttackUnit a: p.getAu()) {
						
						if(a.getType()==4&&
								(a.getName().equals("Gunship")||a.getName().equals("Blastmaster")||a.getName().equals("Thunderbolt"))) k++;
						
					}
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.airPrice*Math.pow(2,k)));
	
					p.addAu(new AttackUnit(array[i],0,0));
				}else if(array[i].equals("Monolith")||array[i].equals("Halcyon")||array[i].equals("Hades")) {
					int k = 0;
					for(AttackUnit a: p.getAu()) {
						
						if(a.getType()==4&&
								(a.getName().equals("Monolith")||a.getName().equals("Halcyon")||a.getName().equals("Hades"))) k++;
						
					}
					if(!free) p.setKnowledge((int) (p.getKnowledge()-GodGenerator.airPrice*Math.pow(2,k)));
	
					p.addAu(new AttackUnit(array[i],0,0));
				}else {
					setError("Invalid research!");
					return false;
				}
				
				i++;
			}
			return true;
		}
		else return false;
		
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * This method marks unread a userSR of the ID given if it belongs to the player.
	 * 
	 * @param sid the ID of the Status Report you'd like to mark not read
	 * 
	 * @return True, if the Status Report is now marked not read.  False otherwise.
	 */
	public boolean markUnReadUserSR(UUID sid) {
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return false;
		}
		UserSR m = p.getUserSR(sid);
		if(m!=null) {
			m.setRead(false);
			return true;
		} else {
			setError("Invalid sid!");
			return false;
		}
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * This method marks read a userSR of the ID given if it belongs to the player.
	 * 
	 * @param sid the ID of the Status Report you'd like to mark read.
	 * 
	 * @return True, if the Status Report was marked read.  False otherwise.
	 */
	public boolean markReadUserSR(UUID sid) {
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return false;
		}
		UserSR m = p.getUserSR(sid);
		if(m!=null) {
			m.setRead(true);
			return true;
		} else {
			setError("Invalid sid!");
			return false;
		}
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * This method deletes a userSR of the ID given if it belongs to the player.
	 * 
	 * @param sid the Id of the Status Report to be deleted
	 * 
	 * @return True, if the Status Report was deleted.  False otherwise.
	 */
	public boolean deleteUserSR(UUID sid) {
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return false;
		}
		if(p.deleteUserSR(sid))
		return true;
		else {
			setError("Invalid sid!");
			return false;
		}
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * This method archives a userSR of the ID given if it belongs to the player.
	 * 
	 *  @param sid ID of the Status Report to be archived
	 *  
	 *  @return True, if the Status Report is archived.  False otherwise.
	 */
	public boolean archiveUserSR(UUID sid) {
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return false;
		}
		UserSR m = p.getUserSR(sid);
		if(m!=null) {
			m.setArchived(true);
			return true;
		} else {
			setError("Invalid sid!");
			return false;
		}
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * This method unarchives a userSR of the ID given if it belongs to the player.
	 * 
	 * @param sid the ID of the Status Report to be unarchived
	 * 
	 * @return True, if the Status Report was unarchived.  False otherwise.
	 */
	public boolean unarchiveUserSR(UUID sid) {
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return false;
		}
		UserSR m = p.getUserSR(sid);
		if(m!=null) {
			m.setArchived(true);
			return true;
		} else {
			setError("Invalid sid!");
			return false;
		}

	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * This method returns an array of all UserSR objects for a player.
	 * 
	 * @return an array of {@link UserSR UserSRs} or null, if an error occurred.
	 */
	public UserSR[] getUserSR() {
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return null;
		}
		UserSR[] toRet;
		ArrayList<UserSR> currSR=p.getUserSR();
		if(currSR!=null) {
		    	int i = 0;
		    	toRet = new UserSR[currSR.size()];
		    	while(i<currSR.size()) {
		    		toRet[i]=currSR.get(i);
		    		i++;
		    	}
		    	

		    	return toRet;
		} else return null;
		 
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Lets you set up the amount of soldiers/tanks you want fortified by a fortification.
	 * <br/>
	 * Tanks can only be fortified after Advanced Fortifications has been researched. 
	 * 
	 * @param auNumbers an array of integers representing the desired number of each of 
	 * your unit types you wish to be protected by this fortification
	 * @param bid - the unique building id of the Fortification.
	 * 
	 * @return True, if the units have been fortified.  False otherwise.
	 */
	public boolean setFortification(int[] auNumbers, UUID bid) {
		if(prog&&!p.isBuildingAPI()) {
			setError("You do not have the Building API!");
			return false;
		}
		Building b = p.findBuilding(bid);
		Town t = p.findTownWithBuilding(bid);
		if(!b.getType().equals("Fortification")) {
			setError("This is not a Fortification!");
			return false;
		}
		if(auNumbers.length!=p.getAu().size()) {
			setError("This array is the correct length! There must be one entry for every Attack Unit you can build!");
			return false;
		}
		int i = 0;
		int max = b.getLvl()*2;
		if(p.getAdvancedFortifications()) max+=b.getLvl()*1;
		
		int desired=0;
		while(i<auNumbers.length) {
			desired+=auNumbers[i]*t.getAu().get(i).getExpmod();
			// so if the type is not 1 or it is 2 and not advanced fortifications, break.
			// break = !1 + 2*!adv
			// which means not break = 1*!(2*!adv)=1*(!2+adv)
			// or to say, if it's 1 and not 2 or if it's 1
			if(t.getAu().get(i).getType()!=1&&auNumbers[i]>0) {
				if((t.getAu().get(i).getType()==2&&p.getAdvancedFortifications())) {
					// this guy is okay.
				} else {
					// break out! BREAK OUT!
					setError("You cannot host this unit type in a fortification!");
					return false;
				}
						
			}
			i++;
		}
		
		if(desired<=max) {
			b.setFortArray(auNumbers);
			return true;
		} else {
			setError("You do not have enough room in this Fortification!");
			return false;

		}
		
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns an array of all the different building types in the game
	 * with accompanying descriptions and base cost arrays.
	 * 
	 * @return an array of UserBuildings with one entry for each type of building
	 */
	public UserBuilding[] getBuildings() {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return null;
		}
		return UserBuilding.getBuildings();
	}
	/**
	 * Get's the UserBuilding object for the building specified by bid.
	 * 
	 * @param bid the ID of the building you want the UserBuilding to represent
	 *  
	 * @return a {@link UserBuilding} object representing the building with the given ID
	 */
	public UserBuilding getUserBuilding(UUID bid) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return null;
		}
		UserBuilding b=null;

		int i = 0;
		int k = 0;
		ArrayList<Town> towns = p.towns(); Town t=null; Building actb=null;
		while(k<towns.size()) {
		
		ArrayList<Building> bldg = towns.get(k).bldg();
		i=0;
			while(i<bldg.size()) {
				if(bldg.get(i).getId().equals(bid)) {t = towns.get(k);
				if(!checkMP(t.townID)) return null;
				actb = bldg.get(i); }
				i++;
			}
		k++;
		}
		if(actb==null) {
			setError("Not a real building!");
			return null;
		}
		int totalEngineers = t.getTotalEngineers(); int x = t.getX(); int y = t.getY();
		double engEffect = g.Maelstrom.getEngineerEffect(x,y);
		boolean mineBldg=false;
		String name = actb.getType();
		if(name.contains("Warehouse"))mineBldg=true;
		int lvl = actb.getLvl();
		int ppl = actb.getPeopleInside();
		 bid = actb.getId();

		 b = new UserBuilding(getUserQueueItems(bid),bid,actb.getBunkerMode(),Building.getCap(lvl,mineBldg),
			Building.getCost(name),actb.isDeconstruct(),actb.getLotNum(),lvl,
			actb.getLvlUps(),actb.getNumLeftToBuild(),ppl,actb.getTicksLeft(),
			Building.getTicksPerPerson(totalEngineers,engEffect,p.getArchitecture(),ppl,lvl,actb.getType()),
			actb.getTicksToFinish(),Building.getTicksForLevelingAtLevel(totalEngineers,lvl,engEffect,p.getArchitecture(),actb.getType()),name,actb.getRefuelTicks(),actb.getFortArray());
		

		/*
		try {
			
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select bid,bunkerMode,name,deconstruct,slot,lvl,lvlUp,pplbuild,ppl,pplticks,lvling,tid from " +
					" bldg where bid = " + bid);
			
			if(rs.next()) { 
				Town t = new Town(rs.getInt(12),g);
				if(t.getPlayer().ID!=p.ID) {
					rs.close();
					stmt.close();
					return null;
				}
				
			int totalEngineers = t.getTotalEngineers(); int x = t.getX(); int y = t.getY();
			double engEffect = g.Maelstrom.getEngineerEffect(x,y);
			boolean mineBldg=false;
			String name = rs.getString(3);
			if(name.contains("Warehouse"))mineBldg=true;
			int lvl = rs.getInt(6);
			int ppl = rs.getInt(9);
			 bid = rs.getInt(1);

			 b = new UserBuilding(getUserQueueItems(bid),bid,rs.getInt(2),Building.getCap(lvl,mineBldg),
				Building.getCost(name),rs.getBoolean(4),rs.getInt(5),lvl,
				rs.getInt(7),rs.getInt(8),ppl,rs.getInt(10),
				Building.getTicksPerPerson(totalEngineers,engEffect,p.getArchitecture(),ppl,lvl),
				rs.getInt(11),Building.getTicksForLevelingAtLevel(totalEngineers,lvl,engEffect,p.getArchitecture()),name);
			}

		rs.close(); stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); } */
			setError("noerror");

		return b;

	}
	
	private UserBuilding[] getUserBuildings(int tid, String type, boolean free) {
		boolean keep=!!prog;
		prog=false;
		UserBuilding[] b = getUserBuildings(tid,type);
		prog=!!keep;
		
		return b;
	}
	/**
	 * Returns an array of UserBuilding objects from the town id. This is different
	 * from getBuildings which just returns the types!
	 * <br/><br/>
	 * Put in the type you want. Special types: "all" returns all buildings, and "CombatProduction" returns all 
	 * military unit production buildings.
	 * 
	 * @param tid	the ID of the town to get the buildings from
	 * @param type	the type of building to get
	 * 
	 * @return an array of {@link UserBuilding UserBuildings} or null, if an error occurred.
	 */
	
	public UserBuilding[] getUserBuildings(int tid, String type) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return null;
		}
	
		int i = 0; Town t = g.findTown(tid);
	//	System.out.println("The townID belongs to " + t.getPlayer().ID);
		if(t==null||(t!=null&&t.townID==0)) {
			setError("Invalid town!");
			return null;
		}
		if(t.getPlayer().ID!=p.ID) {
			setError("Not your town!");
			return null;
		}
		Building actb;
		ArrayList<UserBuilding> tses = new ArrayList<UserBuilding>();
		int totalEngineers = t.getTotalEngineers(); int x = t.getX(); int y = t.getY();
		double engEffect = g.Maelstrom.getEngineerEffect(x,y);
		ArrayList<Building> bldg = t.bldg();
		if(!checkMP(t.townID)) {
			setError("You don't have permission for that.");
			return null;
		}

		while(i<bldg.size()) {
			actb = bldg.get(i);
			if(type.equals("all")||actb.getType().equals(type)||(type.equals("CombatProduction")
					&&(actb.getType().equals("Arms Factory")||actb.getType().equals("Manufacturing Plant")
							||actb.getType().equals("Airstrip")))) {
				
		String name = actb.getType();
		boolean mineBldg = name.contains("Warehouse");
		
		int lvl = actb.getLvl();
		int ppl = actb.getPeopleInside();
		UUID  bid = actb.getId();

		 tses.add(new UserBuilding(getUserQueueItems(bid),bid,actb.getBunkerMode(),Building.getCap(lvl,mineBldg),
				 Building.getCost(name),actb.isDeconstruct(),actb.getLotNum(),lvl,actb.getLvlUps(),
				 actb.getNumLeftToBuild(),ppl,actb.getTicksLeft(),
				 Building.getTicksPerPerson(totalEngineers,engEffect,p.getArchitecture(),ppl,lvl,actb.getType()),
				 actb.getTicksToFinish(),
				 Building.getTicksForLevelingAtLevel(totalEngineers,lvl,engEffect,p.getArchitecture(),actb.getType()),
				 name,actb.getRefuelTicks(),actb.getFortArray()));
			}
		 i++;
		}
		/*
		try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs;
			if(type.equals("all"))
			 rs = stmt.executeQuery("select bid,bunkerMode,name,deconstruct,slot,lvl,lvlUp,pplbuild,ppl,pplticks,lvling from " +
					" bldg where tid = " + tid);
			else 
				 rs = stmt.executeQuery("select bid,bunkerMode,name,deconstruct,slot,lvl,lvlUp,pplbuild,ppl,pplticks,lvling from " +
							" bldg where tid = " + tid + " and name = '" + type + "'");
			while(rs.next()) {
				boolean mineBldg=false;
				String name = rs.getString(3);
				if(name.contains("Warehouse"))mineBldg=true;
				int lvl = rs.getInt(6);
				int bid = rs.getInt(1);
				int ppl = rs.getInt(9);
			tses.add(new UserBuilding(getUserQueueItems(bid),bid,rs.getInt(2),Building.getCap(lvl,mineBldg),
					Building.getCost(name),rs.getBoolean(4),rs.getInt(5),lvl,
					rs.getInt(7),rs.getInt(8),rs.getInt(9),rs.getInt(10),
					Building.getTicksPerPerson(totalEngineers,engEffect,p.getArchitecture(),ppl,lvl),
					rs.getInt(11),Building.getTicksForLevelingAtLevel(totalEngineers,lvl,engEffect,p.getArchitecture()),name));
		
			}
			
		
					rs.close(); stmt.close();	
				
		} catch(SQLException exc) { exc.printStackTrace(); }
			*/
		int j = 0;
		UserBuilding[] toRet = new UserBuilding[tses.size()];
		while(j<tses.size()) {
			toRet[j]=tses.get(j);
			j++;
		}
		setError("noerror");

		return toRet;
	}
	/**
	 * Get only buildings that are on the building server. Buildings on the building server are currently being constructed, or destructed.
	 * <br/>
	 * "all" returns all buildings, "CombatProduction" returns all combat unit production buildings.
	 * 
	 * @param tid	the ID of the town to get the buildings from
	 * @param type	the type of buildings to get
	 * 
	 * @return an array of {@link UserBuilding USerBuildings} or null, if type matched no buildings or an error occurred.
	 */
	public UserBuilding[] getUserBuildingServer(int tid, String type) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return null;
		}
		int i = 0; Town t = g.findTown(tid); if(t.getPlayer().ID!=p.ID) return null; Building actb;
		ArrayList<UserBuilding> tses = new ArrayList<UserBuilding>();
		int totalEngineers = t.getTotalEngineers(); int x = t.getX(); int y = t.getY();
		double engEffect = g.Maelstrom.getEngineerEffect(x,y);
		ArrayList<Building> bldg = t.bldgserver();
		if(!checkMP(t.townID)) return null;

		while(i<bldg.size()) {
			actb = bldg.get(i);
			if(type.equals("all")||actb.getType().equals(type)||(type.equals("CombatProduction")
					&&(type.equals("Arms Factory")||type.equals("Manufacturing Plant")||type.equals("Airstrip"))
					)) {
				boolean mineBldg=false;
		String name = actb.getType();
		if(name.contains("Warehouse"))mineBldg=true;
		int lvl = actb.getLvl();
		int ppl = actb.getPeopleInside();
		UUID  bid = actb.getId();

		 tses.add(new UserBuilding(getUserQueueItems(bid),bid,actb.getBunkerMode(),Building.getCap(lvl,mineBldg),
			Building.getCost(name),actb.isDeconstruct(),actb.getLotNum(),lvl,
			actb.getLvlUps(),actb.getNumLeftToBuild(),ppl,actb.getTicksLeft(),
			Building.getTicksPerPerson(totalEngineers,engEffect,p.getArchitecture(),ppl,lvl,actb.getType()),
			actb.getTicksToFinish(),Building.getTicksForLevelingAtLevel(totalEngineers,lvl,engEffect,p.getArchitecture(),actb.getType()),name,actb.getRefuelTicks(),actb.getFortArray()));
			}
		 i++;
		}
		
		/*int i = 0; Town t = g.findTown(tid); if(t.getPlayer().ID!=p.ID) return null; Building b;
		ArrayList<UserBuilding> tses = new ArrayList<UserBuilding>();
		int totalEngineers = t.getTotalEngineers(); int x = t.getX(); int y = t.getY();
		double engEffect = g.Maelstrom.getEngineerEffect(x,y);
		try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs;
			if(type.equals("all"))
			 rs = stmt.executeQuery("select bid,bunkerMode,name,deconstruct,slot,lvl,lvlUp,pplbuild,ppl,pplticks,lvling from " +
					" bldg where tid = " + tid + " and (pplbuild>0 or lvlUp>0)");
			else 
				 rs = stmt.executeQuery("select bid,bunkerMode,name,deconstruct,slot,lvl,lvlUp,pplbuild,ppl,pplticks,lvling from " +
							" bldg where tid = " + tid + " and name = '" + type + "' and (pplbuild>0 or lvlUp>0)");
			while(rs.next()) {
				boolean mineBldg=false;
				String name = rs.getString(3);
				if(name.contains("Warehouse"))mineBldg=true;
				int lvl = rs.getInt(6);
				int bid = rs.getInt(1);
				int ppl = rs.getInt(9);
			tses.add(new UserBuilding(getUserQueueItems(bid),bid,rs.getInt(2),Building.getCap(lvl,mineBldg),
					Building.getCost(name),rs.getBoolean(4),rs.getInt(5),lvl,
					rs.getInt(7),rs.getInt(8),rs.getInt(9),rs.getInt(10),
					Building.getTicksPerPerson(totalEngineers,engEffect,p.getArchitecture(),ppl,lvl),
					rs.getInt(11),Building.getTicksForLevelingAtLevel(totalEngineers,lvl,engEffect,p.getArchitecture()),name));
		
			}
			
			
		
					rs.close(); stmt.close();	
				
		} catch(SQLException exc) { exc.printStackTrace(); }*/
			
		int j = 0;
		UserBuilding[] toRet = new UserBuilding[tses.size()];
		while(j<tses.size()) {
			toRet[j]=tses.get(j);
			j++;
		}
		setError("noerror");

		return toRet;
	}
	
	/**
	 * Returns an array of UserQueueItem objects from the building id.
	 * 
	 * @param bid the building whose queue items we want
	 * 
	 * @return an array of {@link UserQueueItem UserQueueItems} or null, if an error occurred.
	 */
	
	public UserQueueItem[] getUserQueueItems(UUID bid) {
		if(prog&&!p.isAdvancedBuildingAPI()) {
			setError("You do not have the Advanced Building API!");
			return null;
		}
		 Town t=null; 
	
		ArrayList<UserQueueItem> tses = new ArrayList<UserQueueItem>();
		int i = 0;
		int k = 0;
		ArrayList<Town> towns = p.towns(); Building actb=null;
		while(k<towns.size()) {
		
		ArrayList<Building> bldg = towns.get(k).bldg();
		i=0;
			while(i<bldg.size()) {
				if(bldg.get(i).getId().equals(bid)) {t = towns.get(k);
				if(!checkMP(t.townID)) return null;
				actb = bldg.get(i); }
				i++;
			}
		k++;
		}
		if(actb==null) {
			setError("Not a real building!");
			return null;
		}
		int totalEngineers = t.getTotalEngineers(); int x = t.getX(); int y = t.getY();
		double engEffect = g.Maelstrom.getEngineerEffect(x,y);
		ArrayList<QueueItem> queue = actb.Queue();
		i = 0;
		QueueItem q;
		while(i<queue.size()) {
			q = queue.get(i);
			long cost[] = new long[5];
			 k = 0;
			while(k<cost.length) {
				cost[k]=q.getCost()[k];
				k++;
			}
			//	public UserQueueItem(int qid, int bid, int AUtoBuild, int AUNumber, int currTicks, int ticksPerUnit, long cost[],int townsAtTime, int originalAUAmt, int totalNumber) {
			tses.add(new UserQueueItem(q.getId(),actb.getId(),q.getAUtoBuild(),q.getAUNumber(), q.getCurrTicks(), q.getTicksPerUnit(), cost,q.getTownsAtTime(),q.getOriginalAUAmt(),q.getTotalNumber()));
			i++;
		}
		
		/*
		try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select tid from bldg where bid = " + bid);
			UberStatement stmt2 = g.con.createStatement();
			ResultSet rs2;
			if(rs.next()) 
			{
				t = new Town(rs.getInt(1),g);
				if(t.getPlayer().ID!=p.ID) return null; // SECURITY PROTOCOL.
			} else return null;
			rs.close();
			
			int totalEngineers = t.getTotalEngineers(); int x = t.getX(); int y = t.getY();
			double engEffect = g.Maelstrom.getEngineerEffect(x,y);
			
			 rs = stmt.executeQuery("select qid,AUtoBuild,AUNumber,currTicks,m,t,mm,f,townsAtTime,originalAUAmt,totalNumber from " +
					"queue where bid = " + bid);
			while(rs.next()) {
				
				long cost[] = new long[5];
				cost[0] = rs.getLong(5);
				cost[1] = rs.getLong(6);
				cost[2] = rs.getLong(7);
				cost[3] = rs.getLong(8);
				int AUtoBuild = rs.getInt(2);
				rs2 = stmt2.executeQuery("select type from attackunit where pid = " + p.ID + " and slot = " +AUtoBuild );
				
				rs2.next();
				int type = rs2.getInt(1);
				rs2.close();
				int pop=1;
				switch(type) {
				case 1:
					pop = 1;
					break;
				case 2:
					pop = 5;
					break;
				case 3:
					pop = 10;
					break;
				case 4:
					pop=20;
					break;
				case 5:
					pop=0;
					break;
				}
				int ticksPerUnit = QueueItem.getUnitTicks(pop,totalEngineers,t);
				
			tses.add(new UserQueueItem(rs.getInt(1),bid,AUtoBuild,rs.getInt(3),rs.getInt(4),ticksPerUnit,cost,rs.getInt(9),
					rs.getInt(10),rs.getInt(11)));
		
			}
			
			//toRet[k]=new UserQueueItem(q.qid,b.bid,q.returnAUtoBuild(),q.getAUNumber(),q.getCurrTicks(),q.getTicksPerUnit(),cost,q.getTownsAtTime(),q.getOriginalAUAmt(),q.getTotalNumber());

			
					rs.close(); stmt.close();stmt2.close();	
				
			
		} catch(SQLException exc) { exc.printStackTrace(); }*/
		
		int j = 0;
		UserQueueItem[] toRet = new UserQueueItem[tses.size()];
		while(j<tses.size()) {
			toRet[j]=tses.get(j);
			j++;
		}
		setError("noerror");

		return toRet;
		
						
	}
	/**
	 * Return a UserRaid reprsenting the Raid with an ID of id.
	 * 
	 * @param id the ID of the raid we want to represent
	 * 
	 * @return a {@link UserRaid} object or null, if an error occurred.
	 */
	public UserRaid getUserRaid(UUID id) {
		
		// LINKED TO GETUSERRAIDS().
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return null;
		}
		
		int i = 0;
		AttackUnit a; String raidType="attack";
		String auNames[]; int auAmts[];
		ArrayList<AttackUnit> auset;
		ArrayList<Raid> attackServer;
		Raid r;
		ArrayList<Town> towns = p.towns();
		while(i<towns.size()) {
			attackServer = towns.get(i).attackServer();
			int j = 0;
			while(j<attackServer.size()) {
				r = attackServer.get(j);
				if(r.getId().equals(id)) {
					
					if(!checkMP(towns.get(i).townID)) {
						return null;
					}
					setError("noerror"); // because checkLP probably returned false.

					boolean genocide = r.isGenocide();
					boolean bomb = r.isBomb();
					int support = r.getSupport();
					int scout = r.getScout();
					boolean invade = r.isInvade();
					UUID resupplyID = r.getResupplyID();
					boolean debris = r.isDebris();
					raidType="attack";
					if(genocide) raidType="siege";
					if(!genocide&&bomb) raidType = "strafe";
					else if(genocide&&bomb) raidType="glass";
					if(support==1) raidType = "support";
					if(support==2) raidType="offsupport";
					if(support==3) raidType="blockade";
					if(scout==1) raidType="scout";
					if(invade) raidType="invasion";
					if(resupplyID!=null) raidType="resupply";
					if(r.isDebris()) raidType="debris";
					if(r.getDigAmt()>0&&!r.getTown2().isResourceOutcropping()) raidType = "dig";
					if(r.getDigAmt()>0&&r.getTown2().isResourceOutcropping()) raidType = "excavation";


					
					int ie = 0;int totalCheckedSize=0;
					while(ie<r.getAu().size()) {
						totalCheckedSize+=r.getAu().get(ie).getSize();
						// SuggestTitleVideoId
						ie++;
					}
					totalCheckedSize+=r.getDigAmt();
					if(totalCheckedSize==0) {
						// this means we called getAu() for the first time before the au statements got to update and put
						// the units into the raid!
						r.setAu(null);
						r.getAu(); // reset.
					}
					 auAmts = new int[r.getAu().size()];
					 auNames = new String[r.getAu().size()];

					 
					 
					int k = 0;
					while(k<auAmts.length) {
						auAmts[k] = r.getAu().get(k).getSize();
						auNames[k] = r.getAu().get(k).getName();

						k++;
					}
					
					Timestamp dfin;
					if(r.getDockingFinished()==null) dfin=null;
					else dfin= new Timestamp(r.getDockingFinished().getTime());
					//public UserRaid(int raidID, double distance, boolean raidOver, double ticksToHit, String town1, int x1, int y1, String town2, int x2, int y2, int auAmts[], String auNames[], String raidType,long  m, long  t, long mm, long f,boolean allClear, int bombTarget,
				//	int tid1,int tid2,String name, int genoRounds, boolean bomb) {
					return new UserRaid(r.getId(),r.getDistance(),r.isRaidOver(),r.getTicksToHit(),r.getTown1().getTownName(),
							r.getTown1().getX(),r.getTown1().getY(),r.getTown2().getTownName(),r.getTown2().getX(),
							r.getTown2().getY(),auAmts,auNames,raidType,r.getMetal(),r.getTimber(),r.getManmat(),
							r.getFood(),r.isAllClear(),r.getBombTarget(),r.getTown1().townID,r.getTown2().townID,
							r.getName(),r.getGenoRounds(),r.isBomb(),r.isDebris(),r.getDigAmt(),dfin,r.getReward());
				}
				j++;
			}
			i++;
		}
		/*
		try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs;
			UberStatement stmt2 = g.con.createStatement();
			ResultSet rs2;
		
				
			Raid r = new Raid(rid,g);
			if(r.getTown1().getPlayer().ID!=p.ID) { setError("Not your town!"); return null; }
				 auset = r.getAu();
				 
				 auAmts = new int[auset.size()];
				 auNames = new String[auset.size()];
				// reason for size is au arraylist size is variable, could be support aus on
				// board!
				int k = 0;
				while(k<auset.size()) {
					 a = auset.get(k);
					auAmts[k]=a.getSize();
					auNames[k]=a.getName();
					k++;
				}
				 raidType="attack";
				rs = stmt.executeQuery("select genocide,bomb,support,scout,invade,resupplyID,distance,raidOver,ticksToHit,m,t,mm,f,allClear,bombTarget,tid1,tid2,name,genorounds,bomb from raid where rid = " + r.raidID);
				rs.next();
				boolean genocide = rs.getBoolean(1);
				boolean bomb = rs.getBoolean(2);
				int support = rs.getInt(3);
				int scout = rs.getInt(4);
				boolean invade = rs.getBoolean(5);
				int resupplyID = rs.getInt(6);
				
				if(genocide) raidType="genocide";
				if(!genocide&&bomb) raidType = "strafe";
				else if(genocide&&bomb) raidType="glass";
				if(support==1) raidType = "support";
				if(support==2) raidType="offsupport";
				if(scout==1) raidType="scout";
				if(invade) raidType="invasion";
				if(resupplyID>-1) raidType="resupply";
				
				rs2 = stmt2.executeQuery("select townName,x,y from town where tid = " + rs.getInt(16));
				String tid1name = "DATA CORRUPT-ID";
				int t1x = 0; int t1y = 0;

				if(rs2.next()) {
					tid1name = rs2.getString(1);
					 t1x = rs2.getInt(2);  t1y = rs2.getInt(3);

				}

				rs2.close();
				rs2 = stmt2.executeQuery("select townName,x,y from town where tid = " + rs.getInt(17));
				String tid2name = "DATA CORRUPT-ID";
				int t2x = 0; int t2y = 0;

				if(rs2.next()) {
					tid2name = rs2.getString(1);
					 t2x = rs2.getInt(2);  t2y = rs2.getInt(3);

				}
				rs2.close();
				
			
				temp = new UserRaid(r.raidID,rs.getDouble(7),rs.getBoolean(8),rs.getInt(9),tid1name,t1x,t1y,
						tid2name,t2x,t2y,auAmts,auNames,raidType,rs.getLong(10),rs.getLong(11),rs.getLong(12),rs.getLong(13),
						rs.getBoolean(14),rs.getInt(15),rs.getInt(16),rs.getInt(17),rs.getString(18),rs.getInt(19),rs.getBoolean(20));
				rs.close();
			
			
		
		
				
		stmt.close();stmt2.close();
		} catch(SQLException exc) { exc.printStackTrace(); }*/
		i = 0;
		setError("noerror");

		return null;
	}
	/**
	 * Returns an array of UserRaid objects from the town id.
	 * 
	 * @param tid the ID of the town whose raids we want
	 * 
	 * @return an array of {@link UserRaid UserRaids} or null, if an error occurred.
	 */
	
	public UserRaid[] getUserRaids(int tid) {
		
		// LINKED TO GETUSERRAID().
		if(prog&&!p.isAdvancedAttackAPI()&&!QuestListener.partOfQuest(p,"BQ8")) {
			setError("You do not have the Advanced Attack API!");
			return null;
		}
		if(!checkMP(tid)) {
			return null;
		}
		
		setError("noerror"); // because checkLP probably returned false.

		int i = 0;
		AttackUnit a; String raidType="attack";
		String auNames[]; int auAmts[];
		ArrayList<AttackUnit> auset;
		ArrayList<Raid> attackServer;
		Raid r;
		ArrayList<UserRaid> temp = new ArrayList<UserRaid>();
		
		ArrayList<Town> towns = p.towns();
		Town t = g.findTown(tid);
		if(t.getPlayer()==null) {
			return new UserRaid[0];
		}
		if(t.getPlayer().ID!=p.ID) return null;
		
		attackServer = t.attackServer();
			int j = 0;
			while(j<attackServer.size()) {
				r = attackServer.get(j);
				try {
					boolean genocide = r.isGenocide();
					boolean bomb = r.isBomb();
					int support = r.getSupport();
					int scout = r.getScout();
					boolean invade = r.isInvade();
					UUID resupplyID = r.getResupplyID();
					raidType="attack";
					if(genocide) raidType="siege";
					if(!genocide&&bomb) raidType = "strafe";
					else if(genocide&&bomb) raidType="glass";
					if(support==1) raidType = "support";
					if(support==2) raidType="offsupport";
					if(support==3) raidType="blockade";
					if(scout==1) raidType="scout";
					if(invade) raidType="invasion";
					if(resupplyID!=null) raidType="resupply";
					if(r.isDebris()) raidType="debris";
					if(r.getDigAmt()>0&&!r.getTown2().isResourceOutcropping()) raidType = "dig";
					if(r.getDigAmt()>0&&r.getTown2().isResourceOutcropping()) raidType = "excavation";
					int ie = 0;int totalCheckedSize=0;
					while(ie<r.getAu().size()) { // This is to check to see if the raids accidentally loaded AU
						// before the DB got it.
						totalCheckedSize+=r.getAu().get(ie).getSize();
						// SuggestTitleVideoId
						ie++;
					}
					totalCheckedSize+=r.getDigAmt();
					if(totalCheckedSize==0) {
						// this means we called getAu() for the first time before the au statements got to update and put
						// the units into the raid!
						r.setAu(null);
						r.getAu(); // reset.
					}
					 auAmts = new int[r.getAu().size()];
					 auNames = new String[r.getAu().size()];
					int k = 0;
					while(k<auAmts.length) {
						auAmts[k] = r.getAu().get(k).getSize();
						auNames[k] = r.getAu().get(k).getName();

						k++;
					}
					Timestamp dfin;
					if(r.getDockingFinished()==null) dfin=null;
					else dfin= new Timestamp(r.getDockingFinished().getTime());
					//public UserRaid(int raidID, double distance, boolean raidOver, double ticksToHit, String town1, int x1, int y1, String town2, int x2, int y2, int auAmts[], String auNames[], String raidType,long  m, long  t, long mm, long f,boolean allClear, int bombTarget,
				//	int tid1,int tid2,String name, int genoRounds, boolean bomb) {
					temp.add(new UserRaid(r.getId(),r.getDistance(),r.isRaidOver(),r.getTicksToHit(),r.getTown1().getTownName(),r.getTown1().getX(),r.getTown1().getY(),r.getTown2().getTownName(),r.getTown2().getX(),r.getTown2().getY(),auAmts,auNames,raidType,r.getMetal(),r.getTimber(),r.getManmat(),r.getFood(),r.isAllClear(),r.getBombTarget()
							,r.getTown1().townID,r.getTown2().townID,r.getName(),r.getGenoRounds(),r.isBomb(),r.isDebris(),r.getDigAmt(),dfin,r.getReward()));
				} catch(Exception exc) { exc.printStackTrace(); System.out.println("getUserRaids saved. The raid in question: " + r.getId().toString()); }
				
				j++;
			}
			
			i = 0;
			towns = g.getTowns(); Town t2;
			int y = 0; int aggregate=0;
			boolean keep = false;
			if(prog) keep=true;
			prog=false;

		
			UserBuilding b[] = getUserBuildings(t.townID,"Command Center");
			if(keep) prog = true;
			while(i<b.length) {
				aggregate+=b[i].getLvl();
				i++;
			}
			
			
			int percmult = (int) Math.round(aggregate);
			if(percmult<3) percmult=3;
		// new formula has got to take the percmult and do it instead by tiles instead. So each level up should increase us past a 3x3 block.
		
			
			// we could either go by towns who send out the raids, or we could go by when the raid should be in the area.
			// Or we can do an easy hybrid: You can detect raids coming from outside the tileset only if they're coming at you.
			// This is fairly easily done by calculating radius. The second case is when somebody from outside sends a raid
			// at another city inside. Well you can figure this out by finding finding the difference in radius between
			// that town and the max, called it dR, and then when the raid is within that distance of the town, list it.
			// it's not perfect but it allows a lot of leeway.
			
			// so the protocol:
			// The distance of the raid, given by d = (currTicks)*totalDistance/totalTicks, can be translated to an actual x
			// by the fact that we know the change-in-x and change-in-y are like part of a right triangle, and totalDistance is the hypotenuse. The angle is
			// going to be the same as when the triangle is current difference-in-X, difference-in-Y, and d, where the difference is from the target point.
			// We can then derive the angles by using SOHCAHTOA
			// 1. The small corner angle is lowerTheta = tan-1(change-in-y/change-in-x) (Opposite over Adjacent)
			// 2. Once we have these angles, calculate d, the current distance, by doing d = currTicks*totalDistance/totalTicks.
			// 3. Get the current difference-in-x and difference-in-y by doing this: sin(lowerTheta)*d = difference-in-y (Sin*Hyp=Opp, or SOH) and cos(lowerTheta)*d = difference-in-x(Cos*Hyp = Adj, or CAH).
			// 4. Check against the borders of the box by turning the differences into actual values. 
			
			Town myTown = t;
			int rightBorder = t.getX()+percmult; // my box.
			int leftBorder = t.getX()-percmult;
			int topBorder = t.getY()+percmult;
			int bottomBorder = t.getY()-percmult;
			
			while(i<towns.size()) {
				
			t = towns.get(i);
			if(t.townID!=myTown.townID) {
			attackServer = t.attackServer();
			 j = 0;
			while(j<attackServer.size()) {
				
				r = attackServer.get(j);
				try {
				int totalChangeInX = 0;
				int totalChangeInY=0;
				
				 totalChangeInX = (r.getTown1().getX()-r.getTown2().getX()); // So if x1 = 5 and x2 = 10, the difference is -5, and if I add
				// this variable to x2, I get x1. So this is something you add to x2 to transform it.
				 totalChangeInY = (r.getTown1().getY()-r.getTown2().getY()); 
				double lowerTheta = Math.asin(((double) totalChangeInY)/((double) r.getDistance()));
				int d = (int) Math.round((((double ) r.getTicksToHit())/((double) r.getTotalTicks()))*r.getDistance());
				int diffInY = (int) Math.round(Math.sin(lowerTheta)*((double) d));
				int diffInX = (int) Math.round(Math.cos(lowerTheta)*((double) d));
				// if the starting difference is negative, it can only goto zero. That's the law.
				if(totalChangeInX<0) diffInX = -Math.abs(diffInX);
				if(totalChangeInY<0) diffInY = -Math.abs(diffInY);

				int currX = r.getTown2().getX()+diffInX;
				int currY = r.getTown2().getY()+diffInY;
			/*	System.out.println("For this raid between " + r.getTown1().getX() + "," + r.getTown1().getY() + " and " + r.getTown2().getX() + ","+ r.getTown2().getY()
						+ " I see a totalChangeInX of " + totalChangeInX + " and totalChangeInY of " + totalChangeInY +". The current distance, due to a ticksToHit of " + 
						r.getTicksToHit() + " and a total tick count of " + r.getTotalTicks() + " and original distance of " + r.getDistance()  + " is " + d + ". This led to a lowerTheta of " + lowerTheta 
						+ "  and then diffInX and diffInY are " + diffInX +"," + diffInY + ". We add these to town2's x and ys to get a current x and y of "+ 
						currX + "," + currY + ". Given the borders are right " + rightBorder + " left " + leftBorder + " top " + topBorder + " bottom " + bottomBorder + " " +
								" we are now judging to see if this raid gets added."
						);*/

					//	if(((r.getTicksToHit()<(.033*percmult*r.getTotalTicks()))||(r.getSupport()>0))
						//	&&r.getScout()==0&&&&
							//(!r.isRaidOver()||r.getTicksToHit()>=0)) {	
				if(((currX>=leftBorder&&currX<=rightBorder&&currY>=bottomBorder&&currY<=topBorder)||(r.getSupport()>0&&r.getTown2().townID==myTown.townID))
						&&r.getScout()==0&&
						(!r.isRaidOver()||r.getTicksToHit()>=0)) {
					
					if(((currX==leftBorder&&currX<=rightBorder&&currY>=bottomBorder&&currY<=topBorder))) {
						
					}
			//		System.out.println("We've decided to add this raid of " +r.getTown1().getX() + "," + r.getTown1().getY() + " and " + r.getTown2().getX() + ","+ r.getTown2().getY());
					boolean genocide = r.isGenocide();
					boolean bomb = r.isBomb();
					int support = r.getSupport();
					int scout = r.getScout();
					boolean invade = r.isInvade();
					UUID resupplyID = r.getResupplyID();
					
					if(genocide) raidType="siege";
					if(!genocide&&bomb) raidType = "strafe";
					else if(genocide&&bomb) raidType="glass";
					if(support==1) raidType = "support";
					if(support==2) raidType="offsupport";
					if(scout==1) raidType="scout";
					if(invade) raidType="invasion";
					if(resupplyID!=null) raidType="resupply";
					
					 auAmts = new int[r.getAu().size()];
					 auNames = new String[r.getAu().size()];

					int k = 0;
					while(k<auAmts.length) {
						auAmts[k] = r.getAu().get(k).getSize();
						auNames[k] = r.getAu().get(k).getName();

						k++;
					}
					Timestamp ts=null;
					if(r.getDockingFinished()!=null) ts = new Timestamp(r.getDockingFinished().getTime());
					
					//public UserRaid(int raidID, double distance, boolean raidOver, double ticksToHit, String town1, int x1, int y1, String town2, int x2, int y2, int auAmts[], String auNames[], String raidType,long  m, long  t, long mm, long f,boolean allClear, int bombTarget,
				//	int tid1,int tid2,String name, int genoRounds, boolean bomb) {
					temp.add(new UserRaid(r.getId(),r.getDistance(),r.isRaidOver(),r.getTicksToHit(),r.getTown1().getTownName(),r.getTown1().getX(),r.getTown1().getY(),r.getTown2().getTownName(),r.getTown2().getX(),r.getTown2().getY(),auAmts,auNames,raidType,r.getMetal(),r.getTimber(),r.getManmat(),r.getFood(),r.isAllClear(),r.getBombTarget()
							,r.getTown1().townID,r.getTown2().townID,r.getName(),r.getGenoRounds(),r.isBomb(),r.isDebris(),r.getDigAmt(),ts,r.getReward()));
					}
			} catch(Exception exc) { exc.printStackTrace(); System.out.println("Raids saved from " + r.getId().toString());}
				
				j++;
			}
			}
			i++;
			}
		
		/*
		int i = 0;
		ArrayList<UserRaid> temp = new ArrayList<UserRaid>(); Raid r;
		AttackUnit a; String raidType;
		String auNames[]; int auAmts[];
		ArrayList<AttackUnit> auset;
		ArrayList<Raid> attackServer;
		try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs;
			UberStatement stmt2 = g.con.createStatement();
			ResultSet rs2;
		
				 attackServer = t.attackServer();
				 int j = 0;
			while(j<attackServer.size()) {
				 r = attackServer.get(j);
				 auset = r.getAu();
				 
				 auAmts = new int[auset.size()];
				 auNames = new String[auset.size()];
				// reason for size is au arraylist size is variable, could be support aus on
				// board!
				int k = 0;
				while(k<auset.size()) {
					 a = auset.get(k);
					auAmts[k]=a.getSize();
					auNames[k]=a.getName();
					k++;
				}
				 raidType="attack";
				rs = stmt.executeQuery("select genocide,bomb,support,scout,invade,resupplyID,distance,raidOver,ticksToHit,m,t,mm,f,allClear,bombTarget,tid1,tid2,name,genorounds,bomb from raid where rid = " + r.raidID);
				rs.next();
				boolean genocide = rs.getBoolean(1);
				boolean bomb = rs.getBoolean(2);
				int support = rs.getInt(3);
				int scout = rs.getInt(4);
				boolean invade = rs.getBoolean(5);
				int resupplyID = rs.getInt(6);
				
				if(genocide) raidType="genocide";
				if(!genocide&&bomb) raidType = "strafe";
				else if(genocide&&bomb) raidType="glass";
				if(support==1) raidType = "support";
				if(support==2) raidType="offsupport";
				if(scout==1) raidType="scout";
				if(invade) raidType="invasion";
				if(resupplyID>-1) raidType="resupply";
				
				rs2 = stmt2.executeQuery("select townName,x,y from town where tid = " + rs.getInt(16));
				String tid1name = "DATA CORRUPT-ID";
				int t1x = 0; int t1y = 0;

				if(rs2.next()) {
					tid1name = rs2.getString(1);
					 t1x = rs2.getInt(2);  t1y = rs2.getInt(3);

				}

				rs2.close();
				rs2 = stmt2.executeQuery("select townName,x,y from town where tid = " + rs.getInt(17));
				String tid2name = "DATA CORRUPT-ID";
				int t2x = 0; int t2y = 0;

				if(rs2.next()) {
					tid2name = rs2.getString(1);
					 t2x = rs2.getInt(2);  t2y = rs2.getInt(3);

				}
				rs2.close();
				
			
				temp.add(new UserRaid(r.raidID,rs.getDouble(7),rs.getBoolean(8),rs.getInt(9),tid1name,t1x,t1y,
						tid2name,t2x,t2y,auAmts,auNames,raidType,rs.getLong(10),rs.getLong(11),rs.getLong(12),rs.getLong(13),
						rs.getBoolean(14),rs.getInt(15),rs.getInt(16),rs.getInt(17),rs.getString(18),rs.getInt(19),rs.getBoolean(20)));
				rs.close();
				j++;
			}
			
		
		
					
						int y = 0; int aggregate=0;
						
						rs = stmt.executeQuery("select sum(lvl) from bldg where tid = " + tid + " and name = 'Communications Center';");
						
						rs.next();
						aggregate=rs.getInt(1);
						rs.close();
						double percmult = aggregate*(1+.05*(p.getCommsCenterTech()-1));
						if(percmult<5) percmult=5;
						
						rs = stmt.executeQuery("select genocide,bomb,support,scout,invade,resupplyID,distance,raidOver,ticksToHit,m,t,mm,f,allClear,bombTarget,tid1,tid2,name,genorounds,rid,bomb from raid" +
								" where (ticksToHit<(.033*" + (percmult) + "*totalTicks) or support>0) and scout=0 and tid2 = " + tid + " and (raidOver=false or ticksToHit>=0);");
						while(rs.next()) { // USE AT LEAST + 1 ABOVE SO AGGREGATE 0 YOU STILL GET TO SEE SOME ATTACKS!
							r = new Raid(rs.getInt(20),g);

							//		 if((r.getTicksToHit()<(.033*(aggregate+1)*r.getTotalTicks())||r.getSupport()>0)&&r.getScout()==0) {
									 auset = r.getAu();
								 auAmts = new int[auset.size()];
								 auNames = new String[auset.size()];
								// reason for size is au arraylist size is variable, could be support aus on
								// board!
								int k = 0;
								while(k<auset.size()) {
									 a = auset.get(k);
									auAmts[k]=a.getSize();
									auNames[k]=a.getName();
									k++;
								}
								
							raidType="attack";
						boolean genocide = rs.getBoolean(1);
						boolean bomb = rs.getBoolean(2);
						int support = rs.getInt(3);
						int scout = rs.getInt(4);
						boolean invade = rs.getBoolean(5);
						int resupplyID = rs.getInt(6);
						
						if(genocide) raidType="genocide";
						if(!genocide&&bomb) raidType = "strafe";
						else if(genocide&&bomb) raidType="glass";
						if(support==1) raidType = "support";
						if(support==2) raidType="offsupport";
						if(scout==1) raidType="scout";
						if(invade) raidType="invasion";
						if(resupplyID>-1) raidType="resupply";
						
						rs2 = stmt2.executeQuery("select townName,x,y from town where tid = " + rs.getInt(16));
						rs2.next();
						String tid1name = rs2.getString(1);
						int t1x = rs2.getInt(2); int t1y = rs2.getInt(3);
						rs2.close();
						rs2 = stmt2.executeQuery("select townName,x,y from town where tid = " + rs.getInt(17));
						rs2.next();
						String tid2name = rs2.getString(1);
						int t2x = rs2.getInt(2); int t2y = rs2.getInt(3);
						rs2.close();
						
					
						temp.add(new UserRaid(r.raidID,rs.getDouble(7),rs.getBoolean(8),rs.getInt(9),tid1name,t1x,t1y,
								tid2name,t2x,t2y,auAmts,auNames,raidType,rs.getLong(10),rs.getLong(11),rs.getLong(12),rs.getLong(13),
								rs.getBoolean(14),rs.getInt(15),rs.getInt(16),rs.getInt(17),rs.getString(18),rs.getInt(19),rs.getBoolean(21)));
						}
						rs.close();
						 
				
		stmt.close();stmt2.close();
		} catch(SQLException exc) { exc.printStackTrace(); }*/
		i = 0;
		UserRaid array[] = new UserRaid[temp.size()];
		while(i<temp.size()) {
			array[i]=temp.get(i);
			i++;
		}
		setError("noerror");

		return array;
	}

	/**
	 * Returns one UserTrade given by trid.
	 * 
	 * @param trid the ID of the trade you want.
	 * 
	 * @return a {@link UserTrade} object representing the Trade with ID trid or null, if an error occurred.
	 */
	public UserTrade getUserTrade(UUID trid) {
		if(prog&&!p.isAdvancedTradingAPI()) {
			setError("You do not have the Advanced Trading API!");
			return null;
		}
		int i = 0;
		ArrayList<Trade> ts;
		ArrayList<Town> towns = p.towns();
		Trade t;
		while(i<towns.size()) {
			ts = towns.get(i).tradeServer();
			int j = 0;
			while(j<ts.size()) {
				t = ts.get(j);
				if(t.id.equals(trid)) {
					if(!checkMP(towns.get(i).townID)) {
						return null;
					}
					
					return new UserTrade(t.getDistance(),t.getFood(),t.getManmat(),t.getMetal(),t.getTicksToHit(),t.getTimber(),
							t.getTotalTicks(),t.getTown1().townID,t.getTown2().townID,t.id,t.isTradeOver(),t.getTraders(),
							t.getTs().id,t.getTown1().getTownName(),t.getTown1().getPlayer().getUsername(),t.getTown2().getTownName(),
							t.getTown2().getPlayer().getUsername());
					
				}
				j++;
			}
			i++;
		}
		/*
		Trade tr = new Trade(trid,g);
		Town t= tr.getTown1();
		Town t2 = tr.getTown2();
		if(t.getPlayer().ID!=p.ID) return null; // security.
		UserTrade toRet=null;
	
		try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select distance,f,mm,m,ticksToHit,t,totalTicks,tid1,tid2,trid,tradeOver,traders,tsid from " +
					" trade where trid = " + trid);
			while(rs.next()) {
				String originatingPlayer = p.getUsername();
				String originatingTown = t.getTownName();
				String destTown =t2.getTownName();
				String destPlayer = t2.getPlayer().getUsername();
			toRet=(new UserTrade(rs.getDouble(1),rs.getLong(2),rs.getLong(3),
					rs.getLong(4),rs.getInt(5),rs.getLong(6),rs.getInt(7),
					rs.getInt(8),rs.getInt(9),rs.getInt(10),rs.getBoolean(11),rs.getInt(12),rs.getInt(13),originatingTown,originatingPlayer,destTown,destPlayer));
		
			}
			
			rs.close(); stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }*/
			
				return null;
		
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns an array of UserTrade objects from the town id.
	 * 
	 * @param tid the ID of the town we want to check for trades
	 * 
	 * @return an array of {@link UserTrade UserTrades} that reference this town or null, if an error occurred.
	 */
	
	public UserTrade[] getUserTrades(int tid) {
		if(prog&&!p.isAdvancedTradingAPI()) {
			setError("You do not have the Advanced Trading API!");
			return null;
		}
		if(!checkMP(tid)) {
			return null;
		}
		Town t1= g.findTown(tid);
		if(t1.getPlayer().ID!=p.ID){
			return null; // security.
		}
		ArrayList<UserTrade> tses = new ArrayList<UserTrade>();
		
		ArrayList<Trade> ts = new ArrayList<Trade>();
		{	Trade t;
		ts = t1.tradeServer();
		int j = 0;
		while(j<ts.size()) {
			t = ts.get(j);
			try {
				tses.add( new UserTrade(t.getDistance(),t.getFood(),t.getManmat(),t.getMetal(),t.getTicksToHit(),t.getTimber(),
						t.getTotalTicks(),t.getTown1().townID,t.getTown2().townID,t.id,t.isTradeOver(),t.getTraders(),
						t.getTs().id,t.getTown1().getTownName(),t.getTown1().getPlayer().getUsername(),t.getTown2().getTownName(),
						t.getTown2().getPlayer().getUsername()));
			} catch(Exception exc) {System.out.println("usertrade exc caught with "+ t.id.toString()); }
				
			
			j++;
		}}
		/*
		
	
		ArrayList<UserTrade> tses = new ArrayList<UserTrade>();
		try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select distance,f,mm,m,ticksToHit,t,totalTicks,tid1,tid2,trid,tradeOver,traders,tsid from " +
					" trade where (tid1 = " + tid + " or tid2 = " + tid + ") and (tradeOver = false or ticksToHit >= 0)");
			while(rs.next()) {
			 t1 = new Town(rs.getInt(8),g);
			 t2 = new Town(rs.getInt(9),g);

				String originatingPlayer = t1.getPlayer().getUsername();
				String originatingTown = t1.getTownName();
				String destTown =t2.getTownName();
				String destPlayer = t2.getPlayer().getUsername();
			tses.add(new UserTrade(rs.getDouble(1),rs.getLong(2),rs.getLong(3),
					rs.getLong(4),rs.getInt(5),rs.getLong(6),rs.getInt(7),
					rs.getInt(8),rs.getInt(9),rs.getInt(10),rs.getBoolean(11),rs.getInt(12),rs.getInt(13),originatingTown,
					originatingPlayer,destTown,destPlayer));
		
			}
			
			rs.close(); stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }*/
		
		int i = 0;
		ArrayList<Town> towns = g.getTowns(); Town t2;
		int y = 0; int aggregate=0;
		Town t = t1; Trade r;

		UserBuilding b[] = getUserBuildings(t.townID,"Command Center");
		while(i<b.length) {
			aggregate+=b[i].getLvl();
			i++;
		}
		
		
		int percmult = (int) Math.round(aggregate*(1));
		if(percmult<3) percmult=3;
		Town myTown = t1;
		int rightBorder = t.getX()+percmult; // my box.
		int leftBorder = t.getX()-percmult;
		int topBorder = t.getY()+percmult;
		int bottomBorder = t.getY()-percmult;
		while(i<towns.size()) {
			
		 t = towns.get(i);
		if(t.townID!=myTown.townID) {
		ArrayList<Trade> attackServer = t.tradeServer();
		int j = 0;
		while(j<attackServer.size()) {
			r = attackServer.get(j);
			int totalChangeInX = (r.getTown1().getX()-r.getTown2().getX()); // So if x1 = 5 and x2 = 10, the difference is -5, and if I add
			// this variable to x2, I get x1. So this is something you add to x2 to transform it.
			int totalChangeInY = (r.getTown1().getY()-r.getTown2().getY()); 
			double lowerTheta = Math.asin(((double) totalChangeInY)/((double) r.getDistance()));
			int d = (int) Math.round((((double ) r.getTicksToHit())/((double) r.getTotalTicks()))*r.getDistance());
			int diffInY = (int) Math.round(Math.sin(lowerTheta)*((double) d));
			int diffInX = (int) Math.round(Math.cos(lowerTheta)*((double) d));
			// if the starting difference is negative, it can only goto zero. That's the law.
			if(totalChangeInX<0) diffInX = -Math.abs(diffInX);
			if(totalChangeInY<0) diffInY = -Math.abs(diffInY);

			int currX = r.getTown2().getX()+diffInX;
			int currY = r.getTown2().getY()+diffInY;
			
/*
			System.out.println("For this raid between " + r.getTown1().getX() + "," + r.getTown1().getY() + " and " + r.getTown2().getX() + ","+ r.getTown2().getY()
					+ " I see a totalChangeInX of " + totalChangeInX + " and totalChangeInY of " + totalChangeInY +". The current distance, due to a ticksToHit of " + 
					r.getTicksToHit() + " and a total tick count of " + r.getTotalTicks() + " and original distance of " + r.getDistance()  + " is " + d + ". This led to a lowerTheta of " + lowerTheta 
					+ "  and then diffInX and diffInY are " + diffInX +"," + diffInY + ". We add these to town2's x and ys to get a current x and y of "+ 
					currX + "," + currY + ". Given the borders are right " + rightBorder + " left " + leftBorder + " top " + topBorder + " bottom " + bottomBorder + " " +
							" we are now judging to see if this raid gets added."
					);*/

				//	if(((r.getTicksToHit()<(.033*percmult*r.getTotalTicks()))||(r.getSupport()>0))
					//	&&r.getScout()==0&&&&
						//(!r.isRaidOver()||r.getTicksToHit()>=0)) {	
				if(((currX>=leftBorder&&currX<=rightBorder&&currY>=bottomBorder&&currY<=topBorder))
						&&(!r.isTradeOver()||r.getTicksToHit()>=0)) {
				//	System.out.println("Adding.");
							
								try {
									tses.add( new UserTrade(r.getDistance(),r.getFood(),r.getManmat(),r.getMetal(),r.getTicksToHit(),r.getTimber(),
											r.getTotalTicks(),r.getTown1().townID,r.getTown2().townID,r.id,r.isTradeOver(),r.getTraders(),
											r.getTs().id,r.getTown1().getTownName(),r.getTown1().getPlayer().getUsername(),r.getTown2().getTownName(),
											r.getTown2().getPlayer().getUsername()));
								} catch(Exception exc) {System.out.println("usertrade exc caught with "+ r.id.toString()); }
									
							
							
						}
				
				j++;
			}
		
			}
			i++;
		}
			
				int j = 0;
				UserTrade toReturn[] = new UserTrade[tses.size()];
				while(j<tses.size()) {
					toReturn[j]=tses.get(j);
					j++;
				}
				setError("noerror");

				return toReturn;
		
	}
	/**
	 * Returns a single UserTradeSchedule given by the tsid.
	 * 
	 * @param tsid the ID of the Trade Schedule we want represented
	 * 
	 * @return a {@link UserTradeSchedule} object that represents the Trade Schedule with the ID tsid or null, if an error occurred.
	 */
	public UserTradeSchedule getUserTradeSchedule(UUID tsid) {
		if(prog&&!p.isAdvancedTradingAPI()) {
			setError("You do not have the Advanced Trading API!");
			return null;
		}
		int i = 0;
		ArrayList<TradeSchedule> ts;
		ArrayList<Town> towns = p.towns();
		TradeSchedule t;
		while(i<towns.size()) {
			ts = towns.get(i).tradeSchedules();
			int j = 0;
			while(j<ts.size()) {
				t = ts.get(j);
				if(t.id.equals(tsid)) {
					
					if(!checkMP(towns.get(i).townID)) {
						return null;
					}
					String town2Name = "";
					String town2PlayerName="";
					int tid2 = 0; if(t.getTown2()!=null){
						tid2=t.getTown2().townID;
						town2Name=t.getTown2().getTownName();
						town2PlayerName=t.getTown2().getPlayer().getUsername();
					}
					return new UserTradeSchedule(t.isAgreed(),t.getCurrTicks(),t.isFinished(),t.getFood(),
							t.getIntervaltime(),t.getManmat(),t.getMateID(),t.getMetal(),t.getOtherfood(),
							t.getOthermanmat(),t.getOthermetal(),t.getOthertimber(),t.getTimber(),
							t.getTimesDone(),t.getTimesToDo(),t.getTown1().townID,tid2,t.id,t.isTwoway(),t.getTown1().getTownName(),
							t.getTown1().getPlayer().getUsername(),town2Name,
							town2PlayerName,t.isCaravan());
					
				}
				j++;
			}
			i++;
		}
		
		/*
		TradeSchedule ts = new TradeSchedule(tsid,g);
		Town t = ts.getTown1();
		Town t2 = ts.getTown2();
	
	if(t.getPlayer().ID!=p.ID) {
			return null; // security.
	}
		UserTradeSchedule toRet=null;
		try {
			UberStatement stmt = g.con.createStatement();
			ResultSet rs = stmt.executeQuery("select agreed,currticks,finished,f,intervaltime,mm,mate_tsid,m,otherf,othermm,otherm,othert,t,timesdone,times,tid2,tsid,twoway from" +
					" tradeschedule where tsid = " + tsid + ";");
			while(rs.next()) {

					String originatingPlayer =p.getUsername();
					String originatingTown = t.getTownName();
					String destTown =t2.getTownName();
					String destPlayer = t2.getPlayer().getUsername();
			toRet=(new UserTradeSchedule(rs.getBoolean(1),rs.getInt(2),rs.getBoolean(3),
					rs.getLong(4),rs.getInt(5),rs.getLong(6),rs.getInt(7),
					rs.getLong(8),rs.getLong(9),rs.getLong(10),rs.getLong(11),rs.getLong(12),
					rs.getLong(13),rs.getInt(14),rs.getInt(15),t.townID,
					rs.getInt(16),rs.getInt(17),rs.getBoolean(18),originatingTown,originatingPlayer,destTown,destPlayer));
		
			}
			
			rs.close(); stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
			
*/
		
		return null;

}

	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns an array of UserTradeSchedule objects from the town id.
	 * 
	 * @param tid the ID of the town we want to check for trade schedules
	 * 
	 * @return an array of {@link UserTradeSchedule UserTradeSchedules} that reference this town or null, if an error occurred.
	 */
	
	public UserTradeSchedule[] getUserTradeSchedules(int tid) {
		if(prog&&!p.isAdvancedTradingAPI()) {
			setError("You do not have the Advanced Trading API!");
			return null;
		}
				Town t1= g.findTown(tid);
				if(!checkMP(tid)) {
					return null;
				}
			if(t1.getPlayer().ID!=p.ID) return null; // security.
			int i = 0;
			ArrayList<TradeSchedule> ts;
			ArrayList<UserTradeSchedule> tses = new ArrayList<UserTradeSchedule>();
			TradeSchedule t;
		
				ts = t1.tradeSchedules();
				int j = 0;
				while(j<ts.size()) {
					t = ts.get(j);
					String town2Name = "";
					String town2PlayerName="";
					int tid2 = 0; if(t.getTown2()!=null){
						tid2=t.getTown2().townID;
						town2Name=t.getTown2().getTownName();
						town2PlayerName=t.getTown2().getPlayer().getUsername();
					}

						tses.add(new UserTradeSchedule(t.isAgreed(),t.getCurrTicks(),t.isFinished(),t.getFood(),
								t.getIntervaltime(),t.getManmat(),t.getMateID(),t.getMetal(),t.getOtherfood(),
								t.getOthermanmat(),t.getOthermetal(),t.getOthertimber(),t.getTimber(),
								t.getTimesDone(),t.getTimesToDo(),t.getTown1().townID,tid2,t.id,t.isTwoway(),t.getTown1().getTownName(),
								t.getTown1().getPlayer().getUsername(),town2Name,
								town2PlayerName,t.isCaravan()));
						
					
					j++;
				}
			
			/*
			Town t2;
				ArrayList<UserTradeSchedule> tses = new ArrayList<UserTradeSchedule>();
				try {
					UberStatement stmt = g.con.createStatement();
					ResultSet rs = stmt.executeQuery("select agreed,currticks,finished,f,intervaltime,mm,mate_tsid,m,otherf,othermm,otherm,othert,t,timesdone,times,tid2,tsid,twoway from " +
							" tradeschedule where tid1 = " + tid + " and finished = false;");
					while(rs.next()) {
						
						 t2 = new Town(rs.getInt(16),g);

							String originatingPlayer = p.getUsername();
							String originatingTown = t.getTownName();
							String destTown =t2.getTownName();
							String destPlayer = t2.getPlayer().getUsername();
							
					tses.add(new UserTradeSchedule(rs.getBoolean(1),rs.getInt(2),rs.getBoolean(3),
							rs.getLong(4),rs.getInt(5),rs.getLong(6),rs.getInt(7),
							rs.getLong(8),rs.getLong(9),rs.getLong(10),rs.getLong(11),rs.getLong(12),
							rs.getLong(13),rs.getInt(14),rs.getInt(15),tid,
							rs.getInt(16),rs.getInt(17),rs.getBoolean(18),originatingTown,originatingPlayer,destTown,destPlayer));
				
					}
					
					rs.close(); stmt.close();
				} catch(SQLException exc) { exc.printStackTrace(); }*/
					UserTradeSchedule toRet[] = new UserTradeSchedule[tses.size()];
					 i = 0;
					while(i<tses.size()) {
						
						toRet[i]=tses.get(i);
						i++;
					}
					
					
					setError("noerror");

				
				return toRet;
		
	}
	/**
	 * Returns an array of UserAttackUnit objects from the town id.
	 */
	
	public UserAttackUnit[] getUserAttackUnits(int tid) {
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return null;
		}
		int i = 0; Town t =  g.findTown(tid); AttackUnit b;
			if(!checkMP(tid)) return null;

				int j = 0;
				ArrayList<AttackUnit> au = t.getAu();
				UserAttackUnit toRet[] = new UserAttackUnit[t.getAu().size()];
			
				while(j<au.size()) {
					b = au.get(j);
					
					int k = 0;
				
					if(b.getSupport()==0) 
						/*
						 * public UserAttackUnit(String name, int slot, int originalPlayerID, int originalSlot,
		int originalTID,
		int support, String originalPlayer, int lvl,int size)
						 */
					toRet[j] = new UserAttackUnit(b.getName(),b.getSlot(),p.ID,b.getSlot(),tid,0,p.getUsername(),0,b.getSize());
					else
						toRet[j] = new UserAttackUnit(b.getName(),b.getSlot(),b.getOriginalPlayer().ID,b.getOriginalSlot(),b.getOriginalTID(),b.getSupport(),
								b.getOriginalPlayer().getUsername(),0,b.getSize());
					j++;
				}
				setError("noerror");

				return toRet;
		
	}
	
	/*
	/**
	 * Returns an array of the user's attack unit templates.
	 
	
	public UserAttackUnit[] getUserAttackUnitTemplates() {
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return null;
		}
				AttackUnit b;
				if(!checkMP()) return null;

				int j = 0;
				UserAttackUnit toRet[] = new UserAttackUnit[p.getAUTemplates().size()];
				while(j<p.getAUTemplates().size()) {
					b = p.getAUTemplates().get(j);
					
					int k = 0;
					int weap[] = new int[b.getWeap().length];
					while(k<b.getWeap().length) {
						weap[k]=b.getWeap()[k]; // protect the values!
						k++;
					}
					Player original = b.getOriginalPlayer();
					if(original==null) original=p;
			
					toRet[j] = new UserAttackUnit(b.getAccuracy(),b.getAmmo(),b.getArmor(),b.getCargo(),b.getCivType(),
							b.getConcealment(),b.getExpmod(),b.getFirepower(),b.getGraphicNum(),b.getHp(),b.getLotNum(),
							b.getName(),original.ID,b.getOriginalSlot(),b.getOriginalTID(),b.getPopSize(),b.getSize(),
							b.getSlot(),b.getSpeed(),b.getSupport(),weap,p.getUsername());
					j++;
				}
				setError("noerror");

				return toRet;
			
	}*/
	/**
	 * Returns an array of the user's attack units. They are of size zero as they are the models
	 * for the ones in the UserTowns. They are stored at the UserPlayer level, not the UserTown level.
	 */
	
	public UserAttackUnit[] getUserAttackUnits() {
		if(prog&&!p.isAdvancedAttackAPI()) {
			setError("You do not have the Advanced Attack API!");
			return null;
		}
		AttackUnit b;
		if(!checkMP()) return null;
		int j = 0;
		UserAttackUnit toRet[] = new UserAttackUnit[p.getAu().size()];
		ArrayList<AttackUnit> au = p.getAu();
		while(j<au.size()) {
			b =au.get(j);
			
			int k = 0;
			
			Player original = b.getOriginalPlayer();
			if(original==null) original=p;
		
			toRet[j] = new UserAttackUnit(b.getName(),b.getSlot(),p.ID,b.getSlot(),0,0,p.getUsername(),0,b.getSize());
	
			j++;
		}
		setError("noerror");

		return toRet;
	}
	
	/**
	 * Returns an array of the player's towns. They are the current state of the towns at the call.
	 * 
	 * @return an array of {@link UserTown UserTowns} representing the players towns or null, if an error occurred.
	 */
	
	 public UserTown[] getUserTowns() {
		 if(prog&&!p.isCompleteAnalyticAPI()) {
				setError("You do not have the Complete Analytics API!");
				return null;
			}
		int i = 0;
		UserTown toRet[] = new UserTown[p.towns().size()]; Town t;
		UserRaid r[];
		UserTrade tr[];
		UserTradeSchedule ts[];
		UserAttackUnit au[];
		UserBuilding b[];
		Town idT;
		Player pl = g.getPlayer(p.ID);
		ArrayList<Town> towns = p.towns();
		while(i<towns.size()) {
			t = towns.get(i);
		/*	if(t.getPlayer().ID!=p.ID) {
				System.out.println("Shizzit has changed.");
				break;
			}*/
			if(checkMP(t.townID)) {
		//	if(t.townID==3673) System.out.println("Grabbing capital");
		//	if(t.townID==3785) System.out.println("Grabbing secondary and admin is "+ admin + " and pid is " + pid );
			r = getUserRaids(t.townID);
			tr = getUserTrades(t.townID);
			ts = getUserTradeSchedules(t.townID);
			au = getUserAttackUnits(t.townID);
			b = getUserBuildings(t.townID,"all");
			long res[] = new long[5];
			long resCaps[]=t.getResCaps();
			double resInc[] = t.getResInc();
			double resEffects[] = new double[5];
			 
			if(t.isZeppelin()) {
				idT = g.findTown(t.getX(),t.getY());
				if(idT.townID!=0&&idT.townID!=t.townID&&idT.getPlayer().ID==5) {
					resInc = idT.getResInc();
				}
			}
					res[0] = t.getRes()[0];
					 res[1] = t.getRes()[1];
					 res[2] =t.getRes()[2];
					 res[3] = t.getRes()[3];
				
				 resEffects[0] = t.getResEffects()[0];
				 resEffects[1] = t.getResEffects()[1];
				 resEffects[2] = t.getResEffects()[2];
				 resEffects[3] = t.getResEffects()[3];
				 int lord = 0;
				 if(t.getLord()!=null) lord = t.getLord().ID;
				 Timestamp vassalFrom = new Timestamp((new Date()).getTime());
				 if(t.getVassalFrom()!=null) vassalFrom = t.getVassalFrom();

			
			toRet[i] = new UserTown(r,au,b,p.ID,p.getUsername(),res,resCaps,resInc,resEffects,t.getTotalEngineers(),
					t.getTotalTraders(),t.townID,t.getTownName(),ts,tr,t.getX(),t.getY(),getCSL(t.townID),getCS(t.townID),t.isZeppelin()
					,t.getFuelCells(),t.getDestX(),t.getDestY(),t.getTicksTillMove(),t.getFoodConsumption(),t.getVassalRate(),lord,vassalFrom,t.getInfluence(),t.isResourceOutcropping(),t.getRumor());
			}
			i++;

		}
		
		i = 0; int numNull=0;
		while(i<toRet.length) {
			if(toRet[i]==null) numNull++;
			i++;
		}
		
		UserTown[] toRet2 = new UserTown[toRet.length-numNull];
		
		i = 0; int j = 0;
		while(i<toRet.length) {
			
			if(toRet[i]!=null) {
				toRet2[j] = toRet[i];
				j++;
			}
			i++;
		}
		setError("noerror");

		return toRet2;
	}
	/**
	 * Returns UserTowns just as getUserTowns does. This used to be a slimmer method for quicker load times, but with the switch
	 * back to Memory from ORM, is no longer strictly necessary.
	 * @deprecated Use {@link #getUserTowns()} instead.
	 */
	public UserTown[] getUserTownsSlim() {
		if(prog&&!p.isCompleteAnalyticAPI()) {
			setError("You do not have the Complete Analytics API!");
			return null;
		}
	/*	int i = 0;
		UserTown toRet[] = new UserTown[p.towns().size()]; Town t;
		UserRaid r[];
		UserTrade tr[];
		UserTradeSchedule ts[];
		UserAttackUnit au[];
		UserBuilding b[];
		try{
		UberStatement stmt = g.con.createStatement();
		ResultSet rs;
		ArrayList<Town> towns = p.towns();
		while(i<towns.size()) {
			t = towns.get(i);
			r = new UserRaid[0];
			tr = new UserTrade[0];
			ts = new UserTradeSchedule[0];
		//	r = getUserRaids(t.townID);
		//	tr = getUserTrades(t.townID);
		//	ts = getUserTradeSchedules(t.townID);
			au = getUserAttackUnits(t.townID);
			b = getUserBuildings(t.townID,"all");
			long res[] = new long[5];
			long resCaps[]=t.getResCaps();
			double resInc[] = t.getResInc();
			double resEffects[] = new double[5];
				 rs = stmt.executeQuery("select townName,x,y,m,t,mm,f,minc,tinc,mminc,finc from town where tid = " + t.townID );
				 rs.next();
				 res[0] = rs.getLong(4);
				 res[1] = rs.getLong(5);
				 res[2] = rs.getLong(6);
				 res[3] = rs.getLong(7);
				 resEffects[0] = rs.getDouble(8);
				 resEffects[1] = rs.getDouble(9);
				 resEffects[2] = rs.getDouble(10);
				 resEffects[3] = rs.getDouble(11);
		
			
			toRet[i] = new UserTown(r,au,b,p.ID,p.getUsername(),res,resCaps,resInc,resEffects,t.getTotalEngineers(),
					t.getTotalTraders(),t.townID,rs.getString(1),ts,tr,rs.getInt(2),rs.getInt(3),getCSL(t.townID),getCS(t.townID));
			rs.close();
			i++;
		}
		
		stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
*/
		return getUserTowns();
	}
	/**
	 * Returns a UserPlayer object that represents the state of the player at the method call.
	 * 
	 * @return a {@link UserPlayer} object or null, if an error occurred.
	 */
	
	public UserPlayer getUserPlayer() {
		if(prog&&!p.isCompleteAnalyticAPI()) {
			setError("You do not have the Complete Analytics API!");
			return null;
		}
		UserTown[] t = getUserTowns();
		UserAttackUnit au[] = getUserAttackUnits();

		
		String leagueName = null;
		int lord = 0; Timestamp vassalFrom=new Timestamp((new Date()).getTime());
		if(p.getLord()!=null) lord = p.getLord().ID;
		if(p.getVassalFrom()!=null) vassalFrom = p.getVassalFrom();
		if(p.getLeague()!=null) leagueName=p.getLeague().getUsername();
		return new UserPlayer(p.ID,p.getInfrastructureTech(),p.getBloodMetalPlating(),au,
				p.isThrustVectoring(),p.getKnowledge(),p.getConstructionResearch(),
				p.getAdvancedFortifications(),p.getFirearmResearch(),p.getArchitecture(),p.isLeague(),
				p.isHydraulicAssistors(),leagueName,p.getClockworkComputers(),p.getScholTicks(),p.getScholTicksTotal(),
				p.getStructuralIntegrity(),p.getBodyArmor(),
				p.isPersonalShields(),p.getTotalPopulation(),p.getTotalScholars(),p.getTownTech(),
				t,p.getUsername(),p.getCapitaltid(),p.getBp(),p.getPlayedTicks(),p.getPremiumTimer(),
				p.getUbTimer(),p.getMineTimer(),p.getFeroTimer(),p.getTimberTimer(),p.getMmTimer(),p.getFTimer(),p.getRevTimer(),p.getTotalBPEarned(),p.getEmail(),p.isAirshipTech(),
				p.isClockworkAugments(),
				p.isAttackAPI(),p.isAdvancedAttackAPI(),p.isTradingAPI(),p.isAdvancedTradingAPI(),p.isSmAPI(),p.isResearchAPI(),
				p.isBuildingAPI(),p.isAdvancedBuildingAPI(),p.isMessagingAPI(),p.isZeppelinAPI(),p.isCompleteAnalyticAPI(),
				p.isNukeAPI(),p.isWorldMapAPI(),p.isdigAPI(),p.getScoutTech(),p.getBloodMetalArmor(),lord,vassalFrom,p.getVassalHash());

		
	}
	
	
	/**
	 * UI Implemented.
	 * Returns a matrix of stock market rates. So rate[1][0] is how much
	 * metal(index 0) you  get for one timber(index 1). This
	 * matrix should be symmetric. This matrix takes into account
	 * local weather conditions and the trade technology level of the player,
	 * which alters the exchange rates significantly.
	 */
	
	public float[][] getStockMarketRates(int tid) {
		if(prog&&!p.isSmAPI()) {
			setError("You do not have the Stock Market API!");
			return null;
		}
		int i = 0; boolean found = false; Town t=null;
		ArrayList<Town> towns = p.towns();
		while(i<towns.size()) {
			t = towns.get(i);
			if(t.townID==tid) {
				found=true; break;
			}
			i++;
		}
		if(!found) {
			setError("Invalid tid!");
			return null;
		}
		i=0;
		float toRet[][] = new float[4][4];
		while(i<4){
			int j = 0;
			while(j<4) {
				//public long getExchangeResource(int toIndex, int fromIndex, long toResource, int tradeTech,int x, int y) {

				toRet[i][j]=((float) g.Trader.getExchangeResource(i,j,1000,1,t.getX(),t.getY()))/1000;
				// so we auto divide by 1000, so we send in and may get anywhere from 100 to 10000 and we divide
				// by 1000 to get .1 or 10, respectively, and we know that we get 10 of j resource for
				// one of i resource if we get back 10000.
				j++;
			}
			i++;
		}
		
		return toRet;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Joins a quest identified by this qid.
	 * 
	 * @param qid the ID of the quest to be joined
	 * 
	 * @return True, if the quest was joined.  False otherwise.
	 */
	
	public boolean joinQuest(int qid) {
		return	((QuestListener) g.getPlayer(qid)).addPlayer(p);

	}
	/**
	 * Joins the quest identified by the name questname.
	 * <br/>
	 * Use of {@link #joinQuest(int qid)} is encouraged over this method.
	 * 
	 * @param questname the name of the quest to join.
	 * 
	 * @return True, if the quest was joined.  False otherwise.
	 */
	public boolean joinQuest(String questname) {
		return	((QuestListener) g.getPlayer(g.getPlayerId(questname))).addPlayer(p);

	}
	
	
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Leaves a quest identified by this qid.
	 * 
	 * @param qid the ID of the quest you'd like to leave
	 * 
	 * @return True, if the quest was successfully left.  False otherwise.
	 */
	
	public boolean leaveQuest(int qid) {
		
			return ((QuestListener) g.getPlayer(qid)).destroyWithoutCompletion(p);
		
	}
	
	/**
	 * UI Implemented. Returns a list of quests.
	 */
	
	public String[] getQuests() {
		ArrayList<QuestListener> activeQuests = g.getAllActiveQuests();
		int k = 0;
		String toRet[] = new String[activeQuests.size()];
		int i = 0;
		ArrayList<QuestListener> pactiveQuests = p.getActiveQuests();
		while(i<toRet.length) {
			int status = 0;
			int j = 0;
			
			while(j<pactiveQuests.size()) {
				if(pactiveQuests.get(j).ID==activeQuests.get(i).ID) status=1;
				j++;
			}
			if(activeQuests.get(i).completedQuest(p,activeQuests.get(i).ID)) status = 2;
			toRet[i]=activeQuests.get(i).ID+","+activeQuests.get(i).getUsername()+","+status+","+activeQuests.get(i).getQuestDescription(p.ID);
			
			i++;
		}
		
		return toRet;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Name your poison to use your BP on.
	 * 
	 * @param type what you want to spend your BP on
	 * 
	 * @return True, if the transaction was successful.  False otherwise.
	 *  
	 */
	
	public boolean useBP(String type) {
		if(type.startsWith("ub")) {
			if(p.getBp()<100) {
				setError("Not enough BP!");
				return false;

			}
			p.setUbTimer(p.getUbTimer()+(int) Math.round(7.0*24.0*3600.0/((double) GodGenerator.gameClockFactor)));
			p.setBp(p.getBp()-100);
		} else if(type.startsWith("troopPush")) {
			if(p.getBp()<1000) {
				setError("Not enough BP!");
				return false;

			}
			int j = 0;  // for the six sizes...
			int x = 0; int divider=0;
			ArrayList<AttackUnit> pau = p.getAu();
			while(x<pau.size()) {
				if(!pau.get(x).getName().equals("locked")&&!pau.get(x).getName().equals("empty")) divider++;
				x++; // so if you have more slots you don't get more units.
			}
			ArrayList<Town> towns = p.towns();
			double avgLevel=0;
			 int k = 0;
			 int highLvl=0;
			while(k<towns.size()) {
				avgLevel+=(int) Math.round(((double) p.God.getAverageLevel(towns.get(k)) )/ ((double) towns.size()));
				 x = 0;
				while(x<towns.get(k).bldg().size()) {
					if(towns.get(k).bldg().get(x).getLvl()>highLvl) highLvl=towns.get(k).bldg().get(x).getLvl();
					x++;
				}
				k++;
			}
			double percdifflvl = ((double) (highLvl-avgLevel))/100;
			
			double engAvgLevel = (int) Math.round(((double) (1.0-percdifflvl)*((double) avgLevel) + percdifflvl*((double) highLvl)));
			Town t = g.findTown(p.getCapitaltid());
			double days=(int) Math.round(((double) QueueItem.days)*((double) engAvgLevel-2)/(((double) Town.maxBldgLvl)/6.0));
			if(days>QueueItem.days) days =QueueItem.days;	
			if(days<=0) days=(int) Math.round(((double) QueueItem.days)*((double) 1)/(((double) Town.maxBldgLvl)/6.0));
			double ticks = days*24*3600/GodGenerator.gameClockFactor;
			int i = 0;
			for(;;) {
				if(ticks-QueueItem.getUnitTicksForMany(i,t.getTotalEngineers(),t)<=0) {
					break;
				}
				i++;
			}
			ArrayList<AttackUnit> au;
			double amt = i;
				while(j<p.towns().size()) {
					t = p.towns().get(j);
				if(t.townID==p.getCapitaltid()) {

					
						double num[] = new double[6];
						
						x=0;
							while(x<pau.size()) {
							if(!pau.get(x).getName().equals("locked")&&!pau.get(x).getName().equals("empty")) {
								if(pau.get(x).getType()>0)
							num[x]+= ((amt))/((double) divider*pau.get(x).getExpmod());   // ((double) QueueItem.days*(24*3600-1800*(p.brkthrus-p.brkups+1)))/((double) GodGenerator.gameClockFactor*(QueueItem.getUnitTicks(pau.get(x).getPopSize(),t.getTotalEngineers(),t)*divider));
							
						//	System.out.println(b.bid + " contributes  " + [x]+ " of au " + p.getAu().get(x).name + " in town " + t.townName);
							}
							x++;	
							}
						
						
						// now we add them to the town!
							x = 0;
							au = t.getAu();
							synchronized(au) {
							while(x<au.size()) {
								t.setSize(x,
										au.get(x).getSize() + ((int) Math.round(num[x])));
								x++;
							}
							}
				}
					j++;
				}
				
				p.setBp(p.getBp()-1000);

		} else if(type.startsWith("metal")) {
			if(p.getBp()<50) {
				setError("Not enough BP!");
				return false;

			}
			p.setMineTimer(p.getMineTimer()+(int) Math.round(7.0*24.0*3600.0/((double) GodGenerator.gameClockFactor)));
			p.setBp(p.getBp()-50);
		} else if(type.startsWith("timber")) {
			if(p.getBp()<50) {
				setError("Not enough BP!");
				return false;

			}
			p.setTimberTimer(p.getTimberTimer()+(int) Math.round(7.0*24.0*3600.0/((double) GodGenerator.gameClockFactor)));
			p.setBp(p.getBp()-50);
		} else if(type.startsWith("manmat")) {
			if(p.getBp()<50) {
				setError("Not enough BP!");
				return false;

			}
			p.setMmTimer(p.getMmTimer()+(int) Math.round(7.0*24.0*3600.0/((double) GodGenerator.gameClockFactor)));
			p.setBp(p.getBp()-50);
		} else if(type.startsWith("food")) {
			if(p.getBp()<50) {
				setError("Not enough BP!");
				return false;

			}
			p.setFTimer(p.getFTimer()+(int) Math.round(7.0*24.0*3600.0/((double) GodGenerator.gameClockFactor)));
			p.setBp(p.getBp()-50);
		} else if(type.startsWith("buildingFinish")) {
			if(p.getBp()<100) {
				setError("Not enough BP!");
				return false;

			}
			int i = 0;
			ArrayList<Town> towns = p.towns();
			ArrayList<Building> bldgserver;
			Building b;
			while(i<towns.size()) {
				bldgserver = towns.get(i).bldgserver();
				int j = 0;
				while(j<bldgserver.size()) {
					b = bldgserver.get(j);
					synchronized(b) {
					if(b.getLvlUps()>=1)  {
						b.setTicksToFinish(b.getTicksToFinishTotal()-1);
					}
					}
					j++;
				}
				i++;
			}
			p.setBp(p.getBp()-100);

		} /*else if(type.startsWith("soldierPic")
				||type.startsWith("tankPic")
				||type.startsWith("juggerPic")
				||type.startsWith("bomberPic")) {
			if(p.getBp()<100) {
				setError("Not enough BP!");
				return false;

			}
			
			int skinNumber = Integer.parseInt(type.substring(type.indexOf("c")+1,type.length()));
			if(type.startsWith("soldier")&&skinNumber<10&&!p.getSoldierPicTech()[skinNumber]) {
				p.getSoldierPicTech()[skinNumber]=true;
			} else if(type.startsWith("tank")&&skinNumber<10&&!p.getTankPicTech()[skinNumber]) {
				p.getTankPicTech()[skinNumber]=true;
			}else if(type.startsWith("jugger")&&skinNumber<10&&!p.getJuggerPicTech()[skinNumber]) {
				p.getJuggerPicTech()[skinNumber]=true;
			}else if(type.startsWith("bomber")&&skinNumber<5&&!p.getBomberPicTech()[skinNumber]) {
				p.getBomberPicTech()[skinNumber]=true;
			} else{
				setError("Either you used an invalid skinNumber or you already possess this skin!");
				return false;
			}
			
			p.setBp(p.getBp()-100);

		}*/ else if(type.startsWith("ferocity")) {
			if(p.getBp()<200) {
				setError("Not enough BP!");
				return false;
			}
			p.setFeroTimer(p.getFeroTimer()+(int) Math.round(7.0*24.0*3600.0/((double) GodGenerator.gameClockFactor)));
			p.setBp(p.getBp()-200);
		} else if(type.startsWith("instantSM")) {
			if(p.getBp()<10) {
				setError("Not enough BP!");
				return false;

			}
			 // "research_researchName" Gets you a free research. 1000BP. The researchNames you can use are:
			String hold = type.substring(type.indexOf("_")+1,type.length());
			int tid = Integer.parseInt(hold.substring(0,hold.indexOf("_")));
			
			hold = hold.substring(hold.indexOf("_")+1,hold.length());
			
			int res = Integer.parseInt(hold.substring(0,hold.indexOf("_")));

			hold = hold.substring(hold.indexOf("_")+1,hold.length());
			
			int whichres = Integer.parseInt(hold.substring(0,hold.indexOf("_")));

			hold = hold.substring(hold.indexOf("_")+1,hold.length());
			
			int whichexcres = Integer.parseInt(hold.substring(0,hold.length()));
			
			if(whichres>3||whichres<0||whichexcres>3||whichexcres<0) {
				setError("Invalid resource type!");
				return false;
			}
			
			Town t = g.findTown(tid);
			if(t.getPlayer().ID!=p.ID) {
				setError("Not your town!");
				return false;
			}
			
			int other=(int) g.Trader.getExchangeResource(whichres,whichexcres,res,1,t.getX(),t.getY());
			
			synchronized(t.getRes()) {
				t.getRes()[whichres]-=res;
				t.getRes()[whichexcres]+=other;
			}
			
			
			p.setBp(p.getBp()-10);

			
		} else if(type.startsWith("research")) {
			if(p.getBp()<1000) {
				setError("Not enough BP!");
				return false;
			}
			String hold = type.substring(type.indexOf("_")+1,type.length());
		
			String which[] = {hold.substring(0,hold.length())};

			completeResearches(which,true); // yay for free research!
			
			p.setBp(p.getBp()-1000);

			
		} else {
			setError("Invalid BP Expense Choice!");
			return false;
		}
		return true;
	}
	/**
	 * UI Implemented.
	 * Returns a Hashtable ranking of players based on their average CSL.
	 * The fields in each Hash: username, averageCSL.
	 * @return
	 */
	public Hashtable[] getPlayerRanking() {
		ArrayList<Hashtable> hashie = new ArrayList<Hashtable>();
		Hashtable hashor;
		int i = 0;
		Player p;
		while(i<g.getPlayers().size()) {
			p = g.getPlayers().get(i);
			if(!p.isQuest()&&!p.isLeague()&&p.ID!=5) {
				
				ArrayList<Town> towns = p.towns();
				int x = 0; int avgLevel=0;
				while(x<towns.size()) {
					avgLevel+=(int) Math.round(((double) p.God.getAverageLevel(towns.get(x)) )/ ((double) towns.size()));
					x++;
				}
				
				int k = 0;
				AttackUnit a; int popped = 0;
				ArrayList<AttackUnit> au = p.getAu();
				while(k<au.size()) {
					a = au.get(k);
					if(!a.getName().equals("locked")&&!a.getName().equals("empty"))popped++;
					k++;
				}
				k=0;
				int avgCSL = getCSLAtLevel(avgLevel,popped,p.towns().size());
				int totalCSL=0;
				 k = 0;
				 
				while(k<p.towns().size()) {
					totalCSL+=p.getPs().b.getCSL(p.towns().get(k).townID);
					k++;
				}
				hashor = new Hashtable();
				hashor.put("averageCSL",totalCSL);
				hashor.put("username",p.getUsername());
				if(p.getPremiumTimer()>0) hashor.put("battlehardMode",true);
				else hashor.put("battlehardMode",false);
				hashor.put("achievements",p.getAchievements());
				hashie.add(hashor);
			}
			i++;
		}
		
		i = 0; Hashtable[] toRet = new Hashtable[hashie.size()];
		while(i<toRet.length) {
			toRet[i]=hashie.get(i);
			i++;
		}
		return toRet;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns a Hashtable ranking of BattlehardMode players based on their current BP count.
	 * The fields in each Hash: username, BP.
	 * <br/>
	 * Note: The entries are in no particular order and must be sorted manually.
	 * 
	 * @return A hashtable containing the current BattlehardMode rankings.
	 */
	public Hashtable[] getBattlehardRanking() {
		ArrayList<Hashtable> hashie = new ArrayList<Hashtable>();
		Hashtable hashor;
		int i = 0;
		Player p;
		while(i<g.getPlayers().size()) {
			p = g.getPlayers().get(i);
			if(!p.isQuest()&&p.getPremiumTimer()>0) {
				
				hashor = new Hashtable();
				hashor.put("BP",p.getTotalBPEarned());
				hashor.put("username",p.getUsername());
				
				hashie.add(hashor);
			}
			i++;
		}
		
		i = 0; Hashtable[] toRet = new Hashtable[hashie.size()];
		while(i<toRet.length) {
			toRet[i]=hashie.get(i);
			i++;
		}
		return toRet;
	}
	/**
	 * UI Implemented.
	 * Returns a Hashtable ranking of leagues based on their the sum of the average CSLs of their players.
	 * @return
	 */
	public Hashtable[] getLeagueRanking() {
		ArrayList<Hashtable> hashie = new ArrayList<Hashtable>();
		Hashtable hashor;
		int i = 0;
		Player p;
		while(i<g.getPlayers().size()) {
			p = g.getPlayers().get(i);
			if(p.isLeague()) {
				
				ArrayList<Town> towns = p.towns();
				int x = 0; int avgLevel=0;
				while(x<towns.size()) {
					avgLevel+=(int) Math.round(((double) p.God.getAverageLevel(towns.get(x)) )/ ((double) towns.size()));
					x++;
				}
				
				int k = 0;
				AttackUnit a; int popped = 0;
				ArrayList<AttackUnit> au = p.getAu();
				while(k<au.size()) {
					a = au.get(k);
					if(!a.getName().equals("locked")&&!a.getName().equals("empty"))popped++;
					k++;
				}
				k=0;
				int avgCSL = getCSLAtLevel(avgLevel,popped,p.towns().size());
				
				k = 0;
				int pids[] = ((League) p).returnPIDs(p.ID);
				Player otherP;
				while(k<pids.length) {
					if(((League) p).getType(pids[k])>=0) {
						otherP = g.getPlayer(pids[k]);
						towns = otherP.towns();
						 x = 0;  avgLevel=0;
						while(x<towns.size()) {
							avgLevel+=(int) Math.round(((double) otherP.God.getAverageLevel(towns.get(x)) )/ ((double) towns.size()));
							x++;
						}
						
						int y = 0;
						popped = 0;
						au = otherP.getAu();
						while(y<au.size()) {
							a = au.get(y);
							if(!a.getName().equals("locked")&&!a.getName().equals("empty"))popped++;
							y++;
						}
						y=0;
						 avgCSL += getCSLAtLevel(avgLevel,popped,otherP.towns().size());
					}
					
					k++;
				}
				//{ leagueName, leagueDescription, leagueLetters, leagueWebsite, averageCSL}
				hashor = new Hashtable();
				hashor.put("averageCSL",avgCSL);
				hashor.put("leagueName",((League) p).getName());
				hashor.put("leagueLetters",((League) p).getLetters());
				hashor.put("leagueWebsite",((League) p).getWebsite());
				hashor.put("leagueDescription",((League) p).getDescription());

	
				if(p.getPremiumTimer()>0) hashor.put("battlehardMode",true);
				else hashor.put("battlehardMode",false);
				hashie.add(hashor);
			}
			i++;
		}
		
		i = 0; Hashtable[] toRet = new Hashtable[hashie.size()];
		while(i<toRet.length) {
			toRet[i]=hashie.get(i);
			i++;
		}
		return toRet;
	}
	/**
	 * UI Implemented. 
	 * <br/><br/>
	 * Runs the most recently saved version of your AI script.
	 * 
	 * @return True, if the script is now running.  False otherwise.
	 * 
	 * @see #editProgram()
	 * @see #saveProgram(String)
	 * @see #stopProgram()
	 */
	public boolean runProgram() {
		int i = 0;
		if(!checkLP()) return false;
		ResultSet holdRevStuff=null; UberPreparedStatement stmt=null;
		try {
		 stmt = g.con.createStatement("select revAI from revelations where pid = ?;");
		 stmt.setInt(1,p.ID);
		  holdRevStuff = stmt.executeQuery();
			holdRevStuff.next();
			String oldRev[] = new String[1];
			 oldRev[0] = holdRevStuff.getString(1);
			String prog[] = GodGenerator.semicolonSeparate(oldRev);
			//LOAD STUFF HERE AND CHECK IT.
			
			holdRevStuff.close();
			stmt.close();
			stmt = g.con.createStatement("select count(*) From checkTable");
			holdRevStuff = stmt.executeQuery();
			holdRevStuff.next();
			String toCheckArray[] = new String[holdRevStuff.getInt(1)];
			
			holdRevStuff.close();
			stmt.close();
			stmt = g.con.createStatement("select badstring From checkTable");
			
			holdRevStuff = stmt.executeQuery();
			i=0;
			while(holdRevStuff.next()) {
				toCheckArray[i]=holdRevStuff.getString(1);
				i++;
			}
			holdRevStuff.close();
			stmt.close();
			i=0;
		while(i<prog.length) {
			
			int j = 0;
			while(j<toCheckArray.length) {
				if(prog[i].contains(toCheckArray[j])) {
					setError("You are not allowed to use the words " + toCheckArray[j] + " in your program " +
					" due to the security risks this UberStatement poses. Sorry for the inconvenience.");
					return false;
				}
				j++;
			}
			i++;
		}
		
		// if we get through here, we must be ready to go!
		
	//	p.ps.makeCompileReq();
		if(p.isLeague())
			return ps.loadAndRunProgram(this);
		else
		return ps.loadAndRunProgram();

		//return false;
		} catch(SQLException exc) {if(holdRevStuff!=null)
			try {
				holdRevStuff.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		if(stmt!=null)
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		exc.printStackTrace(); }
		setError("internalservererror");
		return false;	}
	/**
	 * If this is a league bf, returns the pid of the player making
	 * calls with it. Otherwise, returns 0.
	 * @return
	 */
	public int getPID() {
		return pid;
	}
	/**
	 * Returns the username of a player given by the pid. Returns Id if it
	 * can't find one.
	 * 
	 * @param pid the ID of the player who's name we want.
	 * 
	 * @return the name of the Player with the given ID or "Id", if no player matched the ID.
	 */
	public String getUsername(int pid) {
		return g.getPlayer(pid).getUsername();
	}
	/**
	 * Returns the pid of a player given by the username. Returns 5(Id's pid) if it
	 * can't find one.
	 * 
	 * @param username the username of the Player whose ID you want
	 * 
	 * @return the ID of the Player with name username or 5, if no such name was found.
	 */
	public int getPID(String username) {
		return g.getPlayerId(username);
	}
	/**
	 * Forces the quest with questname to update.
	 * 
	 * @param questname the name of the quest you'd like to update
	 * 
	 * @return True, if the quest was found and updated.  False otherwise.
	 */
	public boolean pingQuest(String questname) {
	 int i =0;
	 while(i<p.getActiveQuests().size()) {
		 if(p.getActiveQuests().get(i).getUsername().equals(questname))  {
			 p.getActiveQuests().get(i).iterateQuest(1,p.ID); return true; }
		 i++;
	 }
	 setError("Invalid quest name!");
	 return false;
	}
	/**
	 * Returns the quest log given by this qid.
	 * @param qid
	 * @return
	 */
	public String[] getQuestLog(int qid) {
		String log[]= null;

		QuestListener q = (QuestListener) g.getPlayer(qid);
		if(q.completedOrPartOfQuest(p,qid)||p.getSupportstaff())
		log =  q.getCurrentQuestText(p.ID); 
	
		if(log==null)
		error = "This Quest has not been unlocked yet!";
		return log;
	}
	/**
	 * UI Implemented. 
	 * <br/><br/>
	 * Returns a String representation of your last saved AI script which can be edited.
	 * 
	 * @return a formatted String containing your last saved AI script 
	 * 
	 * @see #runProgram()
	 * @see #saveProgram(String)
	 * @see #stopProgram()
	 */
	
	public String editProgram() {
		 String oldRev = "";

		try {
			UberPreparedStatement stmt = g.con.createStatement("select revAI from revelations where pid = ?;");
			stmt.setInt(1,p.ID);
		 ResultSet holdRevStuff = stmt.executeQuery();
			 if(holdRevStuff.next())
			 oldRev = holdRevStuff.getString(1);
			 holdRevStuff.close();
			 stmt.close();
			 
		} catch(SQLException exc) { exc.printStackTrace(); }
		 return oldRev;

	}
	/**
	 * 
	 * UI Implemented.
	 * <br/><br/>
	 * Saves the program string given in toSave.
	 * 
	 * @param toSave the string representation of your AI script.
	 * 
	 * @return True, if the script has been saved.  False otherwise.
	 * 
	 * @see #editProgram()
	 * @see #runProgram()
	 * @see #stopProgram()
	 */
	
	public boolean saveProgram(String toSave) {

		try {
		//	System.out.println("Saving ... " + toSave);
			UberPreparedStatement stmt = g.con.createStatement("update revelations set revAI = ? where pid = ?;");
			stmt.setString(1,toSave);
			stmt.setInt(2,p.ID);
			stmt.executeUpdate();
			stmt.close();
			 return true;
		} catch(SQLException exc) { exc.printStackTrace(); }
		return false;
	}
	/**
	 * UI Implemented. 
	 * <br/><br/>
	 * Stops your currently running SI script.
	 * 
	 * @return True, if the script was stopped.  False otherwise.
	 * 
	 * @see #editProgram()
	 * @see #saveProgram(String)
	 * @see #runProgram()
	 */
	
	public boolean stopProgram() {
		return p.getPs().stopProgram();
	}
	/**
	 * UI Implemented.
	 * Starts the compilation process for a Revelations A.I.
	 * @return
	 */
	
	private boolean compileProgram() {
		int i = 0;
		ResultSet holdRevStuff=null; UberPreparedStatement stmt=null;
		try {
		 stmt = g.con.createStatement("select revAI from revelations where pid = ?;");
		 stmt.setInt(1,p.ID);
		  holdRevStuff = stmt.executeQuery();
			holdRevStuff.next();
			String oldRev[] = new String[1];
			 oldRev[0] = holdRevStuff.getString(1);
			String prog[] = GodGenerator.semicolonSeparate(oldRev);
			//LOAD STUFF HERE AND CHECK IT.
			
			holdRevStuff.close();
			stmt.close();
			stmt = g.con.createStatement("select count(*) From checkTable");
			holdRevStuff = stmt.executeQuery();
			holdRevStuff.next();
			String toCheckArray[] = new String[holdRevStuff.getInt(1)];
			
			holdRevStuff.close();
			stmt.close();
			stmt = g.con.createStatement("select badstring From checkTable");

			holdRevStuff = stmt.executeQuery();
			i=0;
			while(holdRevStuff.next()) {
				toCheckArray[i]=holdRevStuff.getString(1);
				i++;
			}
			holdRevStuff.close();
			stmt.close();
			i=0;
		while(i<prog.length) {
			
			int j = 0;
			while(j<toCheckArray.length) {
				if(prog[i].contains(toCheckArray[j])) {
					setError("You are not allowed to use the words " + toCheckArray[j] + " in your program " +
					" due to the security risks this UberStatement poses. Sorry for the inconvenience.");
					return false;
				}
				j++;
			}
			i++;
		}
		
		// if we get through here, we must be ready to go!
		stmt.close();
		
	//	p.ps.makeCompileReq();
		p.getPs().loadProgram();

		return false;
		} catch(SQLException exc) {if(holdRevStuff!=null)
			try {
				holdRevStuff.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		if(stmt!=null)
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		exc.printStackTrace(); }
		setError("internalservererror");
		return false;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns a list of all achievements, including ones you haven't gotten yet.
	 * 
	 * @return an ArrayList containing all the achievements in the game
	 */
	public ArrayList<Hashtable> getAchievements() {
		
		ArrayList<Hashtable> toRet = new ArrayList<Hashtable>();
		int i = 0;Hashtable r;
		while(i<g.getAchievements().length) {
			r = new Hashtable();
			r = (Hashtable) g.getAchievements()[i].clone();
			r.put("achieved",false);
			int j = 0;
			while(j<p.getAchievements().size()) {
				if(((Integer) p.getAchievements().get(j).get("aid"))==((Integer) r.get("aid"))) {
					r.put("achieved",true);
				}
				j++;
			}
			toRet.add(r);
			i++;
		}
		return toRet;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Sets whether you want your AI yot to start automatically on server load.
	 * 
	 * @param on if true, your Revelations A.I. will restart when the server does.
	 * 
	 * @return True
	 */
	public boolean setAutoRun(boolean on) {
		try {
			UberPreparedStatement stmt = g.con.createStatement("update player set autorun = ? where pid = ?;");
			stmt.setBoolean(1,on);
			stmt.setInt(2,p.ID);
			stmt.execute();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return true;
		
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns whether your AI is set to automatically start on server load.
	 * This is generally a good idea unless your AI requires some upkeep
	 * 
	 * @return True, if your AI is set to auto run.  False otherwise.
	 */
	public boolean getAutoRun() {
		boolean auto = false;

		try {
			UberPreparedStatement stmt = g.con.createStatement("select autorun from player where pid = ?;");
			stmt.setInt(1,pid);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) auto = rs.getBoolean(1);
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return auto;
		
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Accept a two way trade schedule offer from somebody else.
	 * 
	 * @param tsid 		the ID of the trade to be accepted
	 * @param yourTID 	the ID of the town you're accepting from
	 * 
	 * @return True if the trade is accepted.  False otherwise.
	 */
	
	public boolean acceptTradeSchedule(UUID tsid, int yourTID) {
		if(prog&&!p.isTradingAPI()) {
			setError("You do not have the Trading API!");
			return false;
		}
		int i = 0; TradeSchedule ts=null; Town t = g.findTown(yourTID), t2 = null;
		ArrayList<Town> towns = g.getTowns();
		if(!checkMP(yourTID)) return false;
		if(t.getPlayer().ID!=p.ID) {
			setError("Not your town!");
			return false;
		}
		while(i<towns.size()) {
			Town town = towns.get(i);
			ts = town.findTradeSchedule(tsid);
			if(ts!=null) {
				t2 = town;
				break;
			}
			i++;
		}
		if(ts==null) {
			setError("No such Trade Schedule exists!");
			return false;
		}
		ArrayList<Raid> blockadeT = t.getBlockades(), blockadeT2 = t2.getBlockades();
		boolean canTrade = true;
		//here we check if the blockading players are allied to the current player
		if(blockadeT2.size()>0) {
			canTrade = false;
			for(Raid r : blockadeT2) {
				if(r.getTown1().getPlayer().isAllied(p)) {
					canTrade = true;
					break;
				}
			}
		}
		//here we check if the blockading players are allied to the target player
		//ie the one with the open TS
		if(blockadeT.size()>0&&canTrade) {
			canTrade = false;
			for(Raid r : blockadeT) {
				if(r.getTown1().getPlayer().isAllied(t2.getPlayer())) {
					canTrade = true;
					break;
				}
			}
		}
		if(canTrade) {
			synchronized(ts) {
				
				if(ts.getTown2()==null) {
					ts.completeTradeSetUp(t);
					return true;
				}
			}
		}
		return false;
		
	}
	/**
	 * UI Implemented.
	 * Get a list of possible two-way trades you can choose from. Give the TID you called from so that distances can be set.
	 */
	
	public UserTradeSchedule[] getOpenTwoWays(int tidYouCalledFrom) {
		if(prog&&!p.isAdvancedTradingAPI()) {
			setError("You do not have the Advanced Trading API!");
			return null;
		}
		int i = 0;ArrayList<UserTradeSchedule> tses=new ArrayList<UserTradeSchedule>();
		Town yourT = g.getTown(tidYouCalledFrom);
		if(yourT.getPlayer().ID!=p.ID) {
			setError("This is not your town!");
			return null;
		}
		
		UserTradeSchedule[] townTses;
		UserTradeSchedule ts;
		TradeSchedule actts;
		Town t;
		ArrayList<Raid> yourTBlock = yourT.getBlockades();
		while(i<g.getTowns().size()) {
			t = g.getTowns().get(i);
			townTses = t.getPlayer().getPs().b.getUserTradeSchedules(t.townID);
			//no need to do all this checking if the town has no trade schedules anyways
			if(townTses.length>0) {
				ArrayList<Raid> tBlock = t.getBlockades();
				boolean canTrade = true;
				if(yourTBlock.size()>0) { 	//if your town is blockaded, we have to check if the
					canTrade = false;		//player you're trading to, or his ally, is blockading you
					yourT.getPlayer().God.out.println("Checking blockades for your town.");
					for(Raid r : yourTBlock) {
						if(r.getTown1().getPlayer().isAllied(t.getPlayer())) {
							canTrade = true;
							break;
						}
					}
				}
				//but what if they're blockaded too!
				if(tBlock.size()>0&&canTrade) {
					yourT.getPlayer().God.out.println("Checking blockades for the target town.");
					canTrade = false;
					for(Raid r : tBlock) {
						if(r.getTown1().getPlayer().isAllied(p)) {
							canTrade = true;
							break;
						}
					}
				}
				if(canTrade) {
					int j = 0;
					while(j<townTses.length) {
						ts = townTses[j];
						if(ts.getTID2()==0&&ts.getTID1()!=tidYouCalledFrom) {
							ts.setDistance(Math.sqrt(Math.pow(t.getX()-yourT.getX(),2) + Math.pow(t.getY()-yourT.getY(),2)));
							// THIS MEANS IT'S A TRADE THAT HAS NO PARDNER YET!
							// now we need to see if it's TS is actually there. Just a check, really.
							yourT.getPlayer().God.out.println("Adding schedule to the list.");
							tses.add(ts);
						}
						j++;
					}
				}
			}
			i++;
		}
		
		i = 0;
		
		return tses.toArray(new UserTradeSchedule[tses.size()]);
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Creates an Airship using your Town Tech over the city of your choosing.
	 * 
	 * @param airshipName 	the name to give the new Airship
	 * @param townID		the ID of the town to build it at (Must contain an Airstrip)
	 * 
	 * @return True, if the airship was created.  False otherwise.
	 */
	public boolean createAirship(String airshipName, int townID) {
		if(prog&&!p.isZeppelinAPI()) {
			setError("You do not have the Zeppelin API!");
			return false;
		}
		
		Town t = g.findTown(townID);
		if(t.getPlayer().ID!=p.ID) {
			setError("Not your town!");
			return false;
		}
		if(!checkMP(townID)) return false;
		double resEffects[] = {0,0,0,0,0};
		boolean keep = false;
		if(prog) keep = true;
		prog = false;
		if(p.towns().size()<p.getTownTech()&&haveBldg("Airstrip",townID)) {
			addZeppelin(t.getX(),t.getY(),resEffects,airshipName);
		} else {
			setError("You are either missing a town tech or need an Airstrip!");
		}
		
		if(keep) prog = true;
		
		return true;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * For those idiots who just can't figure out how to fly an airship on empty.
	 * Returns your airship to your capital after losing all of it's resources.
	 * If another airship is at your capital, you must move it first.
	 * 
	 * @param townID ID of the Airship to be moved.
	 * 
	 * @return True if the Airship is moved.  False, otherwise.
	 */
	public boolean abortAirship(int townID) {
		if(prog&&!p.isZeppelinAPI()) {
			setError("You do not have the Zeppelin API!");
			return false;
		}
		Town t = g.getTown(townID);
		
		Town capital = g.getTown(p.getCapitaltid());
		
		if(capital==null) {
			setError("You need a capital!");
			return false;
		}
		if(t==null) {
			setError("This Airship does not exist!");
			return false;
		}
		
		if(t.getPlayer().ID!=p.ID) {
			setError("This is not your Airship!");
			return false;
		}
		
		synchronized(t.getRes()) {
			
			int i = 0;
			while(i<t.getRes().length-1) {
				t.getRes()[i]=0;
				i++;
			}
			
		}
		t.setX(capital.getX());
		t.setY(capital.getY());
		t.setDestX(capital.getX());
		t.setDestY(capital.getY());
		return true;
		
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Moves your Airship given by it's townID to the position given by the x and y.
	 * Remember that Airships are basically Towns as far as SP Wars is concerned, hence the TownID.
	 * 
	 * @param x
	 * @param y
	 * 
	 * @return True, if the Airship has begun moving.  False otherwise.
	 */
	public boolean moveAirship(int x, int y, int townID) {
		if(prog&&!p.isZeppelinAPI()) {
			setError("You do not have the Zeppelin API!");
			return false;
		}
		
		
		Town t = g.findTown(townID);
		if(t.getPlayer().ID!=p.ID) {
			setError("Not your Airship!");
			return false;
		}
		if(!t.isZeppelin()) {
			setError("Not an Airship!");
		}
		double distance = Math.sqrt(Math.pow(x-t.getX(),2)+Math.pow(y-t.getY(),2));
		if(distance>t.getFuelCells()) {
			setError("You do not have enough fuel for this adventure!");
			return false;
		}
		
		Town otherTown = g.findTown(x,y);
		if(otherTown.townID!=0&&otherTown.getPlayer().ID!=p.ID&&otherTown.getPlayer().ID!=5&&!otherTown.isZeppelin()) {
			// you're allowed to overlap with other zeppelins to fight them.
			setError("You can only hover your Airship over your town or Id's towns!");
			return false;
		}
		
		if(otherTown.townID!=0&&otherTown.isZeppelin()&&otherTown.getPlayer().ID==p.ID) {
			
			setError("Cannot hover two of your Airships in the same location!");
			return false;
		}
		int i = 0;
		while(i<p.towns().size()) {
			if(p.towns().get(i).isZeppelin()&&p.towns().get(i).getDestX()==x&&p.towns().get(i).getDestY()==y) {
				setError("Another one of your Airships is already moving to that location. You would be in danger of a collision!");
				return false;
			}
			i++;
		}
		if(t.getX()!=t.getDestX()||t.getY()!=t.getDestY()) {
			setError("You cannot alter your path while moving!");
			return false;
		}
		i=0;
		while(i<t.attackServer().size()) {
			if(t.attackServer().get(i).getTown1().townID==t.townID) {
				
				setError("You cannot move your Airship while you have raids out!");
				return false;
			}
			i++;
		}
		
		t.setDestX(x); t.setDestY(y); t.setFuelCells(t.getFuelCells()-(int) Math.floor(distance));
		
		return true;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Add a week of Battlehard Mode onto your player and experience the burn.
	 * 
	 * @return True, if the time was successfully added.  False otherwise.
	 */
	public boolean goBHM() {
		if(p.getPremiumTimer()<52*7*24*3600/GodGenerator.gameClockFactor) {
			p.setPremiumTimer((p.getPremiumTimer()+(int) Math.round(7*24*3600/GodGenerator.gameClockFactor)));
			return true;
		} else return false;
		
		
	}
	
	/**
	 * UI Implemented
	 * <br/><br/>
	 * Responds to a message sent by one of your Dig teams.
	 * 
	 * @param yes		True, if you want to accept the reward presented.
	 * @param townID	the ID of the Id town your team is digging.
	 * 
	 * @return True, if your response was heard.  False otherwise.
	 */
	public boolean respondToDigMessage(boolean yes, int townID) {
		if(prog&&!p.isdigAPI()) {
			setError("You do not have the Dig API!");
			return false;
		}
		
		Town idTown = g.findTown(townID);
		if(idTown.townID==0){
			setError("This town doesn't exist!");
			return false;
		}
		
		Town yourTown = g.findTown(idTown.getDigTownID());
		if(yourTown.townID==0) {
			setError("This Id town doesn't have a dig!");
			return false;
		}
		if(yourTown.getPlayer().ID!=p.ID) {
			setError("This is not your town!");
			return false;
		}
		
		if(idTown.getMsgSent()) {
			// so this is a reply.
			
			if(yes) {
				//	public String returnPrizeName(int probTick, int x, int y, boolean test, PrintWriter out, double presetRand, String presetTile) {
				 Town otherT = idTown.getPlayer().God.getTown(idTown.getDigTownID());
				 Raid r= null;
				 for(Raid r2:otherT.attackServer()){
					 if(r2.getTown2().townID==townID&&r2.getDigAmt()>0) {
						 r=r2;break;
					 }
				 }
				
				String reward = r.getReward();
				idTown.setRumor(idTown.getRandomRumor()); // reset the rumor.
				//reward = "zeppelin";
				/*

				 * Prize codes:
				 * nothing
				 * daily10
				 * daily20
				 * daily30
				 * daily50
				 * lowkp
				 * medkp
				 * highkp
				 * api
				 * soldier
				 * civvietech
				 * militech
				 * tank
				 * metaltech
				 * timbertech
				 * manmattech
				 * foodtech
				 * juggernaught
				 * silo
				 * zeppelin
				 */
				
				if(reward.equals("nothing")) {
					sendYourself(idTown.getDigSmackTalk(),"Dig Find From "+ idTown.getTownName());
				} else if(reward.equals("daily10")) {
				//	sendYourself("Sir,\n We found a small resource cache! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					yourTown.doMyResources((int) Math.round(.1*24*3600/GodGenerator.gameClockFactor));

				}else if(reward.equals("daily20")) {
				//	sendYourself("Sir,\n We found a medium resource and knowledge cache! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					yourTown.doMyResources((int) Math.round(.2*24*3600/GodGenerator.gameClockFactor));
					p.setKnowledge(p.getKnowledge()+(int) Math.round(p.knowledgePerDay()*.05));

				}else if(reward.equals("daily30")) {
				//	sendYourself("Sir,\n We found a large resource and knowledge cache! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					yourTown.doMyResources((int) Math.round(.3*24*3600/GodGenerator.gameClockFactor));
					p.setKnowledge(p.getKnowledge()+(int) Math.round(p.knowledgePerDay()*.1));

				}else if(reward.equals("daily50")) {
					//sendYourself("Sir,\n We found a very large resource and knowledge cache! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					yourTown.doMyResources((int) Math.round(.5*24*3600/GodGenerator.gameClockFactor));
					p.setKnowledge(p.getKnowledge()+(int) Math.round(p.knowledgePerDay()*.2));

				}else if(reward.equals("randomTech")) { 
					String tech[] ={ getRandomTech()};
					completeResearches(tech,true);
				//	sendYourself("Sir,\n We found a piece of ancient [" + tech[0] + "]! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					
				}/*else if(reward.equals("lowKP")) {
					sendYourself("Sir,\n We found a small knowledge cache! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					p.setKnowledge(p.getKnowledge()+(int) Math.round(p.knowledgePerDay()*.05));
					
				}else if(reward.equals("medKP")) {
					sendYourself("Sir,\n We found a medium knowledge cache! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					p.setKnowledge(p.getKnowledge()+65);
					
				}else if(reward.equals("highKP")) {
					sendYourself("Sir,\n We found a large knowledge cache! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					p.setKnowledge(p.getKnowledge()+80);
					
				}else if(reward.equals("api")) {
					String api = getRandomAPI();
					sendYourself("Sir,\n We found the " + api + " API! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					String toSend[] = {api};
					completeResearches(toSend,true);
					
				}else if(reward.equals("soldier")) {
					sendYourself("Sir,\n We found a blueprint for an ancient soldier unit! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					generateRandomAUTemplate(1,true,null);
					
				}else if(reward.equals("tank")) {
					sendYourself("Sir,\n We found a blueprint for an ancient tank unit! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					generateRandomAUTemplate(2,true,null);
					
				}else if(reward.equals("juggernaught")) {
					sendYourself("Sir,\n We found a blueprint for an ancient juggernaught unit! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					generateRandomAUTemplate(3,true,null);
					
				}
				else if(reward.equals("civvietech")) {
					String tech[] ={ getRandomCivvieTech()};
					completeResearches(tech,true);
					sendYourself("Sir,\n We found a piece of ancient [" + tech[0] + "]! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					
				}else if(reward.equals("militech")) {
					String tech[] ={ getRandomMiliTech()};
					completeResearches(tech,true);
					sendYourself("Sir,\n We found a piece of ancient [" + tech[0] + "]! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					
				}else if(reward.equals("metaltech")) {
					String tech[] ={ "metalRefTech"};
					completeResearches(tech,true);
					sendYourself("Sir,\n We found the diagrams for a [" + tech[0] + "]! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					
				}else if(reward.equals("timbertech")) {
					String tech[] ={ "timberRefTech"};
					completeResearches(tech,true);
					sendYourself("Sir,\n We found the diagrams for a [" + tech[0] + "]! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					
				}else if(reward.equals("manmattech")) {
					String tech[] ={ "manMatRefTech"};
					completeResearches(tech,true);
					sendYourself("Sir,\n We found the diagrams for a [" + tech[0] + "]! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					
				}else if(reward.equals("foodtech")) {
					String tech[] ={ "foodRefTech"};
					completeResearches(tech,true);
					sendYourself("Sir,\n We found the diagrams for a [" + tech[0] + "]! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
					
				} else if(reward.equals("silo")) {
					String[] tech = {"lotTech"};
					if(canCompleteResearches(tech,true)) completeResearches(tech,true);
						int i=0; boolean foundBuildSite=false; Town t=null; int j = 0;
						while(i<p.towns().size()) {
							t = p.towns().get(i);
							 j =0;
							while(j<GodGenerator.infrastructureTechLimit) {
								//	public boolean canBuild(String type, int lotNum, int tid) {

								int k = 0; boolean lotTaken=false;
								while(k<t.bldg().size()) {
									if(t.bldg().get(k).getLotNum()==j) {
										lotTaken=true;break;
										
									}
									k++;
								}
								if(!lotTaken) {
									foundBuildSite=true;
									break;
								}
								j++;
							}
							if(foundBuildSite) break;
							i++;
						}
						
						if(foundBuildSite) {
							//	public Building addBuilding(String type, int lotNum, int lvl, int lvlUp) {

							t.addBuilding("Missile Silo",j,1,0);
							sendYourself("Sir,\n We found a tactical nuke hidden in the sands of time. We were able to salvage it and bring it back to " + t.getTownName() + "! We hope you use it wisely. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());

						} else {
				
							tech[0]="missileSiloTech";
							completeResearches(tech,true);
							sendYourself("Sir,\n We found a tactical nuke hidden in the sands of time... Unfortunately, we were unable to find a place for it in your Empire. However, we WERE able to uncover the diagrams to make one. We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());

						}
					
				}*/
				else if(reward.equals("zeppelin")) {
					double rand = Math.random();
					if(rand<.1||p.isAirshipTech()) {
						sendYourself("Sir,\n We found an ancient Airship from long ago. We weren't able to salvage it, but we found a huge knowledge cache!\n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());
						p.setKnowledge(p.getKnowledge()+(int) Math.round(p.knowledgePerDay()*.4));

					}
					String tech[] ={ "zeppelinTech","townTech"};
					completeResearches(tech,true);
					sendYourself("Sir,\n We found an ancient Airship from long ago. We weren't able to salvage it, but we were able to salvage an extra town slot and the plans to build an Airstrip. From an Airstrip, you could build your own Airship! We just shipped it to you. It should be arriving now. \n-The Dig Team from " + idTown.getTownName(),"Dig Find From "+ idTown.getTownName());

					
				}
				
				idTown.returnDigOrRO(false,true);
			} else {
				//	 public void resetDig(int newTownID, int digAmt, boolean findTime, Raid r) {
				boolean done = false;
				for(Town t: p.towns()) {
					for(Raid theR :t.attackServer()) {
						if(theR.getDigAmt()>0&&theR.getTicksToHit()==0&&!theR.isRaidOver()&&theR.getDockingFinished()!=null&&theR.getTown2().townID==idTown.townID) {
							idTown.resetDig(idTown.getDigTownID(),idTown.getDigAmt(),true,theR);
							done=true;
							break;
						}
					}
					if(done) break;
				}
			}

			//	public boolean recall(int townToRecallFromID, int pidOfRecallTown, int yourTownID) {

			
		} else {
			setError("This message was never sent!");
			return false;
		}
		
		return true;
	}
	/**
	 * @deprecated
	 * AI Wars function
	 * @return
	 */
	private String getRandomCivvieTech() {
		String random[] ={
				"buildingSlotTech",
				"lotTech",
				"buildingStabilityTech",
				"townTech",
				"engineerTech",
				"traderTech",
				"scholarTech"
				
		};
		int counter=0;
		String toSend[] = {"null"};
		do {
			int rand = (int) Math.round(Math.random()*(random.length-1));
			if(rand<0) rand = 0;
			toSend[0]=random[rand];
			counter++;
			
		}while(counter<10&&canCompleteResearches(toSend,true));
		return toSend[0];
	}
	private String getRandomTech() {
		/*
		 * 1.9.1.Firearm Research(double check)
1.9.2.Ordinance Research(double check)
1.9.3.Tesla Tech(double check)
1.9.4.BloodMetal Plating(double check)
1.9.5.Body Armor(double check)
1.9.6.BloodMetal Armor(double check)
1.9.7.Personal Shields(double check)
1.9.8.Hydraulic Assistors(double check)
1.9.9.Thrust Vectoring(double check)
1.9.10.Clockwork Augments(double check)
1.9.11.Clockwork Computers(double check)
1.9.12.Architecture(double check)
1.9.13.Airship Blueprint(double check)
1.9.14.Advanced Fortifications(double check)
1.9.15.Structural Integrity(NEEDS FURTHER IMPLEMENTATION)
1.9.16.Infrastructure Tech(double check)
1.9.17.Town Tech(double check)
1.9.18.Construction Research(double check)
1.9.19.Scout Tech(double check)
1.9.20.Soldier Blueprints(double check)
1.9.21.Tank Blueprints(double check)
1.9.22.Golem Blueprints(double check)
1.9.23.Light Airship Blueprints(double check)
1.9.24.Heavy Airship Blueprints(double check)
		 */
		String random[] = {
				"firearmResearch",
				"ordinanceResearch",
				"teslaTech",
				"bloodMetalPlating",
				"bodyArmor",
				"bloodMetalArmor",
				"personalShields",
				"hydraulicAssistors",
				"thrustVectoring",
				"clockworkAugments",
				"clockworkComputers",
				"architecture",
				"advancedFortifications",
				"structuralIntegrity",
				"infrastructureTech",
				"townTech",
				"constructionResearch",
				"scoutTech",
				"Panzerfaust",
				"Pillager",
				"Vanguard",
				"Seeker",
				"Damascus",
				"Wolverine",
				"Punisher",
				"Dreadnaught",
				"Collossus",
				"Gunship",
				"Thunderbolt",
				"Blastmaster",
				"Halcyon",
				"Hades",
				"Monolith"
				
				
				
		};
		int counter=0;
		String toSend[] = {"null"};
		do{
			int rand = (int) Math.round(Math.random()*(random.length-1));
			if(rand<0) rand = 0;
			toSend[0]=random[rand];
			counter++;
			
		}while(counter<10&&canCompleteResearches(toSend,true)) ;
		return toSend[0];
	}

	/**
	 * @deprecated
	 * AI Wars function
	 * @return
	 */
	private String getRandomMiliTech() {
		String random[] ={
				"afTech",
				"bunkerTech",
				"unitLotTech",
				"commsCenterTech",
				"stealthTech",
				"scoutTech",
				"supportTech"
				
		};
		int counter=0;
		String toSend[] = {"null"};
		do{
			int rand = (int) Math.round(Math.random()*(random.length-1));
			if(rand<0) rand = 0;
			toSend[0]=random[rand];
			counter++;
			
		}while(counter<10&&canCompleteResearches(toSend,true)) ;
		return toSend[0];
	}
	/**
	 * @deprecated
	 * AI Wars function
	 * @return
	 */
	private String getRandomAPI() {
		String[] random ={
				"digAPI",
				"attackAPI",
				"advancedAttackAPI",
				"tradingAPI",
				"advancedTradingAPI",
				"smAPI",
				"researchAPI",
				"buildingAPI",
				"advancedBuildingAPI",
				"messagingAPI",
				"zeppelinAPI",
				"completeAnalyticAPI",
				"nukeAPI",
				"worldMapAPI"
				
		};
		int counter=0;
		String[] toSend = {"null"};
	do 	 {
			int rand = (int) Math.round(Math.random()*(random.length-1));
			if(rand<0) rand = 0;
			toSend[0]=random[rand];
			//System.out.println("random is " +toSend[0]);
			counter++;
			
		} while(counter<10&&canCompleteResearches(toSend,true));
		return toSend[0];
	}
	
	private boolean generateRandomAUTemplate(int type, boolean test, PrintWriter out) {
		
		//public boolean createUnitTemplate(String unitName, int tierNumber, int concealment,int armor, int cargo, int speed, int weaponsArray[], int graphicNum, boolean id) {
		// for tanks, is *2, for juggernaughts, *3
		// how would I go about doing this? 
		// Think....
		/*
		 * Random unit creation is fairly simple. Just uh...remember you need to spend 200*tierNumber points on the four different types, and you need to split it up.
		 *You need to generate four random numbers that add up to 200. You could do a while loop where you wait for a random summation to reach 200. But that just
		 *seems kind of gay. 
		 *
		 *Randomly generate the four numbers, and then, add them up and find a normalization factor to get them to 200. Make sure none of them is less than 30, or
		 *repeat. 
		 *
		 *Then for the weapons, just choose between 0 and 5 for soldier, 6 and 12 for tank, 13 and 18 for juggernaught. Choose random graphicNum between 0 and 10.
		 *
		 */
	/*	int mult=1;
		switch(type) {
		case 2:
			mult=2;
			break;
		case 3:
			mult = 4;
			break;
		}
		double conc = 0; double armor = 0; double cargo = 0; double speed = 0; int i = 0;
		while((conc>0||armor>0||speed>0||cargo>0||conc<30*mult||armor<30*mult||speed<30*mult)&&i<1000) {
			
			conc = Math.random()*100;
			armor=Math.random()*100;
			cargo = Math.random()*100;
			speed=Math.random()*100;
			double sum = conc+cargo+armor+speed;
			double N = 200*mult/sum;//N*Sum = 200, becomes our scaling factor.
			conc=(int) Math.round(((double) conc*N));
			armor=(int) Math.round(((double) armor*N));
			cargo=(int) Math.round(((double) cargo*N));
			speed=(int) Math.round(((double) speed*N));
			i++;
			
		//	 System.out.print("conc: " + conc + " armor: " + armor + " speed: " + speed + " cargo: " + cargo + " sum: "+ (conc+armor+cargo+speed) );
		}
		
		if(conc+armor+cargo+speed<200*mult-1||conc+armor+cargo+speed>200*mult+1) {

			return false; // Clearly screwed up.
		} else {

			if(conc+armor+cargo+speed==200*mult-1) speed++;
			else if(conc+armor+speed+cargo==200*mult+1) speed--;
		}

		int weap1 = (int) Math.round(6*Math.random())-1+(type-1)*6;
		if(weap1<(type-1)*6) weap1=(type-1)*6;
		int weap2 =  (int) Math.round(6*Math.random())-1+(type-1)*6;
		if(weap2<(type-1)*6) weap2=(type-1)*6;
		int weap[] = {weap1,weap2};
		int graphicNum =  (int) Math.round(9*Math.random());
		
		String unitPrefixCombos[] = {
				"Dirty",
				"Fallen",
				"Nah",
				"Suck",
				"Big",
				"Small",
				"Brat",
				"Risen",
				"Angry",
				"Tired",
				"Dumb",
				"Supa",
				"Mr",
				"Sir",
				"Butt",
				"Ilikea",
				"Father",
				"Mother"
				
		};
		String unitPostfixCombos[] = {
				"Panda",
				"Felix",
				"Hole",
				"Advil",
				"Meerkat",
				"Challenger",
				"Ram",
				"Testosterone",
				"Estrogen",
				"Killer",
				"Booter",
				"Pirate",
				"Killpeople",
				"Messiah",
				"Bootscooter"
				
		};
		//public boolean createUnitTemplate(String unitName, int tierNumber, int concealment,int armor, int cargo, int speed, int weaponsArray[], int graphicNum, boolean id) {

		int prefix = (int) Math.round(Math.random()*unitPrefixCombos.length-1);
		if(prefix<0) prefix=0;
		int postfix = (int) Math.round(Math.random()*unitPostfixCombos.length-1);
		if(postfix<0) postfix=0;
		String unitName = unitPrefixCombos[prefix]+" " + unitPostfixCombos[postfix];
		//					createUnitTemplate("Shock Trooper",1,75,25,50,50,weap,0);
		boolean can = createUnitTemplate(unitName,type,(int) conc,(int) armor,(int) cargo,(int) speed,weap,graphicNum);
		String error = "none";
		if(!can) error = getError();
		if(test&&out!=null) out.print("conc: " + conc + " armor: " + armor + " speed: " + speed + " cargo: " + cargo + " weap1: " + weap1  + " weap2: " + weap2 + " type: " + type + " graphicNum: " + graphicNum + " unitName: " + unitName + " can: " +can + " error: " + getError());
		if(test&&out==null) System.out.print("conc: " + conc + " armor: " + armor + " speed: " + speed + " cargo: " + cargo + " weap1: " + weap1  + " weap2: " + weap2 + " type: " + type + " graphicNum: " + graphicNum + " unitName: " + unitName + " can: " + can + " error: " + getError());
		*/
		return true;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns if it's possible to launch the nuke in the building ID given at the coordinates given.
	 * <br/>
	 * False nukeMode is a ground nuke, true is a sky nuke.
	 * 
	 * @param bid 		the ID of the Missile Silo whose nuke you want to launch
	 * @param x 		the x coordinate of the target town
	 * @param y 		the y coordinate of the target town
	 * @param nukeMode	If true, this nuke will detonate as an EMP, shutting down play AIs.  Otherwise, it will detonate on the ground and do damage.
	 * 
	 * @return True, if the nuke can be launched.  False otherwise.
	 */
	public boolean canLaunchNuke(int bid, int x, int y, boolean nukeMode) {
		if(prog&&!p.isNukeAPI()) {
			setError("You do not have the Nuke API!");
			return false;
		}
		int i = 0; Building b;
		while(i<p.towns().size()) {
			int j = 0;
			while(j<p.towns().get(i).bldg().size()) {
				b = p.towns().get(i).bldg().get(j);
				if(b.getId().equals(bid)) {
					if(!b.getType().equals("Missile Silo")||(b.getType().equals("Missile Silo")&&b.getLvl()==0)) {
						setError("This building must be a Missile Silo of at least level 1 to fire a Missile out from it!");
						return false;
					}
					
					// now that we know it is a missile silo that can be fired, we test to see if we can reach that town.
					Town t = g.findTown(x,y);
					if(t==null||(t!=null&&t.isZeppelin())||t.townID==0) {
						setError("You cannot fire at Airships or non-existant towns!");
						return false;
					}
					
					// NOW, IS IT IN RANGE?
					
					double distance = Math.sqrt(Math.pow(t.getX()-p.towns().get(i).getX(),2) + Math.pow(t.getY()-p.towns().get(i).getY(),2));
					if(distance>b.getLvl()*5) {
						setError("This target is beyond your range!");
						return false;
					}
					
					// has it already been fired?
					if(b.getTicksLeft()>0) {
						setError("This missile has already been fired!");
						
					}
					// true true?
					
					return true;
				}
				j++;
			}
			i++;
		}
		setError("Invalid building!");
		return false;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * The holy of holies, this launches the big one. If nukeMode is false, then this is a groundNuke and will do building damage.
	 * Otherwise, it's an EMP and will shut down AIs. 
	 * 
	 * @param bid		the Id of the Missile Silo you're launching from
	 * @param x			the x coordinate of the target town
	 * @param y			the y coordinate of the target town
	 * @param nukeMode	if true, this nuke detonates as an EMP.  Otherwise, this nuke detonates on the gound.
	 * 
	 * @return True, if launch was successful.  False, otherwise.
	 */
	public boolean launchNuke(int bid, int x, int y, boolean nukeMode) {
		if(prog&&!p.isNukeAPI()) {
			setError("You do not have the Nuke API!");
			return false;
		}
		if(canLaunchNuke(bid,x,y,nukeMode)) {
			int i = 0; Building b;
			while(i<p.towns().size()) {
				int j = 0;
				while(j<p.towns().get(i).bldg().size()) {
					b = p.towns().get(i).bldg().get(j);
					if(b.getId().equals(bid)) {
						Town t = g.findTown(x,y);

						double distance = Math.sqrt(Math.pow(t.getX()-p.towns().get(i).getX(),2) + Math.pow(t.getY()-p.towns().get(i).getY(),2));
						
						b.setTicksLeft((int) Math.round(Town.zeppelinTicksPerMove*distance)); // make it go as fast as an Airship!
						b.setBunkerMode(t.getX()); b.setRefuelTicks(t.getY());
						b.setNukeMode(nukeMode);
						
						return true;
					}
					
					j++;
				}
				i++;
			}
			
		} 
		return false;
		
	}
	/**
	 * This will add Airships at will to your account at the x,y specified. 
	 * 
	 * @param x 			the x coordinate to place the Airship
	 * @param y 			the y coordinate to place the Airship
	 * @param resEffects	the modifiers to res income for this Airship
	 * @param townName		the name to give this Airship
	 * 
	 * @return The town ID of the Airship
	 */
	private int addZeppelin(int x, int y, double[] resEffects, String townName) {

		int tid=-1;

		try {
			ResultSet rs;
			boolean transacted=false;
			while(!transacted) {
				try {
					UberPreparedStatement stmt = g.con.createStatement("insert into town (pid,townName,x,y,destX,destY,m,t,mm,f,pop,minc,tinc,mminc,finc,kinc,au1,au2,au3,au4,au5,au6,zeppelin) values (?,?,?,?,?,0,0,0,0,1,?,?,?,?,?,0,0,0,0,0,0,true)");

		//	stmt.execute("update player set chg = 1 where pid = " + ID);
		
			  //stmt.execute("insert into town (pid,townName,x,y,destX,destY,m,t,mm,f,pop,minc,tinc,mminc,finc,kinc,au1,au2,au3,au4,au5,au6,zeppelin) values (" + p.ID  +",\"" + townName+ "\","
    			//	  +x+","+(y)+","+x+","+y+",0,0,0,0,1," + resEffects[0] + "," + resEffects[1] + "," + resEffects[2] + "," + resEffects[3] + "," + resEffects[4] + ",0,0,0,0,0,0,true)");
			  stmt.setInt(1,p.ID);
			  stmt.setString(2,townName);
			  stmt.setInt(3,x);
			  stmt.setInt(4,y);
			  stmt.setInt(5,x);
			  stmt.setInt(6,y);
			  stmt.setDouble(7,resEffects[0]);
			  stmt.setDouble(8,resEffects[1]);
			  stmt.setDouble(9,resEffects[2]);
			  stmt.setDouble(10,resEffects[3]);
			  stmt.setDouble(11,resEffects[4]);

			  stmt.execute();
			  stmt.close();
			  stmt = g.con.createStatement("select tid from town where x = ? and y = ? and zeppelin=true;");
			  stmt.setInt(1,x);
			  stmt.setInt(2,y);
    		  rs = stmt.executeQuery();
    		  rs.next();
    		   tid = rs.getInt(1);
    		  rs.close();
    		stmt.close();
    		stmt = g.con.createStatement("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp,deconstruct,pploutside,bunkerMode) values (" +
    		  		"?,?,3,-1,0,0,0,?,0,0,-1,0);");
    		stmt.setString(1,"Metal Mine");
    		stmt.setInt(2,0);
    		stmt.setInt(3,tid);
    		stmt.execute();
    		stmt.setString(1,"Timber Field");
    		stmt.setInt(2,1);
    		stmt.setInt(3,tid);
    		stmt.execute();

    		stmt.setString(1,"Crystal Mine");
    		stmt.setInt(2,2);
    		stmt.setInt(3,tid);
    		stmt.execute();

    		stmt.setString(1,"Farm");
    		stmt.setInt(2,3);
    		stmt.setInt(3,tid);
    		stmt.execute();

    		stmt.setString(1,"Command Center");
    		stmt.setInt(2,4);
    		stmt.setInt(3,tid);
    		stmt.execute();

    		stmt.setString(1,"Metal Warehouse");
    		stmt.setInt(2,5);
    		stmt.setInt(3,tid);
    		stmt.execute();

    		stmt.setString(1,"Lumber Yard");
    		stmt.setInt(2,6);
    		stmt.setInt(3,tid);
    		stmt.execute();
    		
    		stmt.setString(1,"Crystal Repository");
    		stmt.setInt(2,7);
    		stmt.setInt(3,tid);
    		stmt.execute();
    		
    		stmt.setString(1,"Granary");
    		stmt.setInt(2,8);
    		stmt.setInt(3,tid);
    		stmt.execute();

    		  
    		  Town t = new Town(tid,g);
    		  rs.close();
    		  g.getIteratorTowns().add(t);
    		  p.towns().add(t);
    		 // System.out.println("This town: " + t + " on end of player: "+ towns().get(towns().size()-1));

    //		  stmt.execute("update player set chg = 2 where pid = "+ ID);
    		  stmt.close();
    		  transacted=true;
				} catch(MySQLTransactionRollbackException exc) { } 
			}
			return tid;
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return tid;
		
	
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * The Airship with the townID given will offload resources to the town directly beneath
	 * it, up to the caps obviously. It will keep the rest.
	 * 
	 * @param townID the ID of the Airship to unload
	 * 
	 * @return True, if offloading was successful.  False otherwise.
	 */
	public boolean offloadResources(int townID) {
		if(prog&&!p.isZeppelinAPI()) {
			setError("You do not have the Zeppelin API!");
			return false;
		}
		
		
		Town zepp = g.findTown(townID);
		if(zepp.getPlayer().ID!=p.ID) {
			setError("This is not your Airship!");
			return false;
		}
		
		Town t = g.findTown(zepp.getX(),zepp.getY());
		
		if(t.townID==zepp.townID||t.townID==0) {
			setError("No town there to offload resources to!");
			return false;
		}
		
		int i = 0;
		long toAdd[] = new long[zepp.getRes().length];
		long resCaps[] = t.getResCaps();
		while(i<zepp.getRes().length-1) {
			toAdd[i]+=(resCaps[i]-t.getRes()[i]);
			if(toAdd[i]>zepp.getRes()[i]) toAdd[i] = zepp.getRes()[i];
			i++;
		}
		
		i = 0;
		synchronized(t.getRes()) {
			synchronized(zepp.getRes()) {
				while(i<t.getRes().length-1) {
					t.getRes()[i]+=toAdd[i];
					zepp.getRes()[i]-=toAdd[i];
					
					i++;
				}
				
			}
			
		}
		return true;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns true if your program is currently running.
	 * 
	 * @return True, if your program is running.  False otherwise.
	 */
	public boolean isAlive() {
		Object currRevInstance= null; Hashtable r=null;
		synchronized(g.programs) {
			int i = 0;
				while(i<g.programs.size()) {
					if(((Integer) g.programs.get(i).get("pid")) == p.ID) {
						currRevInstance = (Object) g.programs.get(i).get("Revelations");
						r = g.programs.get(i);
						break;
					}
					i++;
				}
				}
		if(currRevInstance!=null&&
				(   (currRevInstance.getClass().getSuperclass().getName().equals("Revelations.RevelationsAI")&&
						((Thread) currRevInstance).isAlive())  
				|| currRevInstance.getClass().getSuperclass().getName().equals("Revelations.RevelationsAI2")))
				  return true;
		else return false;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Returns the ticks in gameClockFactor since the last server restart.
	 * 
	 * @return The time, in ticks, since the last server restart.
	 */
	public int getServerTicks() {
		return g.getGameClock();
	}

	/**
	 * Sets the most recent error to the value passed.  This is usually used by BF methods
	 * to report more detail about why a function failed to properly execute.  It is not
	 * advisted to use this method yourself if you don't understand the consequences.
	 * 
	 * @param error the string to set the error to
	 */
	public void setError(String error) {
		this.error = error;
	}
	/**
	 * @return a string representing the current UI version.  Should always return "original". 
	 * 
	 * @deprecated Was to be used with the UI switcher that never got implemented. 
	 */
	public String getVersion() {
		
		return p.getVersion();
	}
	/**
	 * @param version the version of UI to use
	 * 
	 * @return False
	 * 
	 * @deprecated Was to be used with the UI switcher that never got implemented.
	 */
	public boolean setVersion(String version) {
		
		if(!version.equals("original")&&!version.equals("dark")&&!version.equals("light")) {
			setError("Invalid UI code!");
			return false;
		}
		
		p.setVersion(version);
		return false;
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * If you are already a voluntary vassal of one Lord and wish to switch, then you can do that with the msgid of the
	 * invite message.<br/>
	 * If you are not a vassal, and you have no sack, then you can swear fealty here too.
	 * 
	 * @param msgID ID of the message sent to request vassalage.
	 * 
	 * @return True, if the vassalage request was accepted.  False otherwise.
	 */
	//Note: This description needs more clarification.  What is a sack?
	public boolean acceptVassalage(UUID msgID) {
		
		
		UserMessage msg = getMessage(msgID);
		Player lord = g.getPlayer(msg.getPidFrom());
		if(msg==null){
			setError("Not a valid message!");
			return false;
		} 

		if(msg.getMsgType()!=6) {
			setError("Not a vassal invite!");
			return false;
		}
		if(p.getLord()==null||
				(p.getLord()!=null&&p.getLord().ID!=lord.ID&&p.isVoluntaryVassal())
				||(p.getLord()!=null&&p.getLord().ID==lord.ID&&!p.isVoluntaryVassal())) {
			if(p.getLord()!=null) {
				 p.getLord().makeWallPost("","Vassal Lost!",p.getUsername()+"'s empire has freed itself from my grasp!",
							"http://www.steampunkwars.com","In Steampunk Wars, vassalage is just one of many ways to subjugate your neighbors. Join now to find out more!",
							"https://fbcdn-photos-a.akamaihd.net/photos-ak-snc1/v27562/23/164327976933647/app_1_164327976933647_5894.gif",
							"Play Now!","http://www.steampunkwars.com/");
			}
			lord.makeWallPost("","Vassal Acquired!",p.getUsername()+"'s entire empire has fallen under my sway and has sworn fealty to me and me alone!",
					"http://www.steampunkwars.com","In Steampunk Wars, vassalage is just one of many ways to subjugate your neighbors. Join now to find out more!",
					"https://fbcdn-photos-a.akamaihd.net/photos-ak-snc1/v27562/23/164327976933647/app_1_164327976933647_5894.gif",
					"Play Now!","http://www.steampunkwars.com/");
			p.makeWallPost("","Subjugation!","My empire has fallen under the sway of "+lord.getUsername()+".  We shall be their vassal forevermore.",
					"http://www.steampunkwars.com","In Steampunk Wars, vassalage is just one of many ways to subjugate your neighbors. Join now to find out more!",
					"https://fbcdn-photos-a.akamaihd.net/photos-ak-snc1/v27562/23/164327976933647/app_1_164327976933647_5894.gif",
					"Play Now!","http://www.steampunkwars.com/");
			
			 p.makeVassalOf(lord,true);
			 return true;
		} else {
			setError("You are either a forced vassal of a different lord, or are already a voluntary vassal of this one!");
			return false;
		}
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * If you are a lord and no longer want the vassal denoted by the Username, you can cancel it here. This will
	 * also de-vassal any towns of theirs that you had at the town-level before player-vassalification.
	 * <br/><br/>
	 * 
	 * 
	 * @param username the name of the player whose vassalage you no longer desire
	 * 
	 * @return True, if vassalage was canceled.  False otherwise.
	 */
	public boolean cancelVassalage(String username) {
		return cancelVassalage(g.getPlayerId(username));
	}
	/**
	 * If you are a lord and no longer want the vassal denoted by the player ID, you can cancel it here.
	 * 
	 * @param pid the ID of the player whose vassalage you no longer desire
	 * 
	 * @return True, if the vassalage was canceled.  False otherwise.
	 */
	public boolean cancelVassalage(int pid) {
		
		
		Player vassal = g.getPlayer(pid);
		if(vassal==null||vassal.ID==5||(vassal!=null&&vassal.getLord()==null)||(vassal!=null&&vassal.getLord()!=null&&vassal.getLord().ID!=p.ID)) {
			setError("Invalid player!");
			return false;
		}
			if(!vassal.isVoluntaryVassal()) {
				setError("You cannot devassal an involuntary vassal!");
				return false;
			}
			
			 vassal.makeVassalOf(null,false);
			 return true;
		
	}
	/**
	 * Returns a list of pids of your player-level vassals. Note the difference here:
	 * You can have influence over some of a player's towns, but if you have influence over enough of them,
	 * you are lord over all of them(ie, lord over the player.) This is a list of player-level
	 * vassals.
	 */
	
	public int[] getPlayerLevelVassalPids() {
		ArrayList<Integer> players = new ArrayList<Integer>();
		for(Player pl: g.getPlayers()) {
			
			if(pl.getLord()!=null&&pl.getLord().ID==p.ID) {
				players.add(pl.ID);
			}
		}
		
		int pids[] = new int[players.size()];
		int i = 0;
		while(i<pids.length) {
			pids[i]=players.get(i);
			i++;
		}
		
		return pids;
	}
	/**
	 * Returns a list of usernames of your player-level vassals. Note the difference here:
	 * You can have influence over some of a player's towns, but if you have influence over enough of them,
	 * you are lord over all of them(ie, lord over the player.) This is a list of player-level
	 * vassals.
	 */
	
	public String[] getPlayerLevelVassalUsernames() {
		ArrayList<String> players = new ArrayList<String>();
		for(Player pl: g.getPlayers()) {
			
			if(pl.getLord()!=null&&pl.getLord().ID==p.ID) {
				players.add(pl.getUsername());
			}
		}
		
		String pids[] = new String[players.size()];
		int i = 0;
		while(i<pids.length) {
			pids[i]=players.get(i);
			i++;
		}
		
		return pids;
	}
	
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * Reset the tax rates on voluntary player-level vassals given by their usernames.
	 * 
	 * @param username	the name of the vassal whose tax rate is to be adjusted
	 * @param rate		the tax rate percentage for this vassal as a decimal (IE .1 for 10%).  Cannot be more than 0.5.
	 * 
	 * @return True, if the vassal's tax rate was adjusted.  False otherwise.
	 */
	public boolean resetVassalTax(String username, double rate) {
		
		
		return resetVassalTax(g.getPlayerId(username),rate);
		
	}
	/**
	 * Reset the tax rates on voluntary player-level vassals given by their PIDs.
	 * 
	 * @param pid	the ID of the vassal whose tax rate is to be adjusted
	 * @param rate	the tax rate percent for this vassal as a decimal(IE .1 for 10%).  Can not exceed 50%.
	 * 
	 * @return True, if the tax rate was set to the new value.  False otherwise.
	 */
	public boolean resetVassalTax(int pid, double rate) {
		
		
		Player vassal = g.getPlayer(pid);
		if(vassal==null||vassal.ID==5||(vassal!=null&&vassal.getLord()==null)||(vassal!=null&&vassal.getLord()!=null&&vassal.getLord().ID!=p.ID)) {
			setError("Invalid player!");
			return false;
		}
		
			if(rate>.5&&vassal.isVoluntaryVassal()) {
				setError("You may not set greater than 50% tax on voluntary vassals!");
				return false;
			} else if(!vassal.isVoluntaryVassal()) {
				setError("The tax rate on forced vassals is constant!");
				return false;
			}
			 vassal.setTaxRate(rate);
			 return true;
		
	}
	/**
	 * UI Implemented.
	 * <br/><br/>
	 * If you are a voluntary player-level vassal, you can cancel player-level vassalage here. This also releases
	 * all towns that you have that were previously vassaled to your player-level lord.
	 * 
	 * @return True, if vassalage was canceled.  False otherwise.
	 */
	public boolean cancelVassalage() {
		
		if(p.getLord()!=null&&p.isVoluntaryVassal()) {
			 p.makeVassalOf(null,false);
			 return true;
		} else {
			if(p.getLord()!=null) {
				setError("You cannot cancel forced vassalage.");
			} else {
				setError("You are not a vassal.");
			}
			return false;
		}
	}
	/**
	 * UI Implemented.
	 * Cancels a trade caravan by the given tsid.
	 * @param tsid
	 * @return
	 */
	public boolean cancelTradeCaravan(UUID tsid) {
		TradeSchedule ts = p.findTradeSchedule(tsid);
		if(ts==null) {
			setError("Invalid tsid!");
			return false;
		}
		ts.deleteMeInterrupt();
		return false;
	}
	/**
	 * UI Implemented.
	 * Offers a trade caravan to the town of choice from the town of choice. Is a type of two way trade,
	 * so you can accept the trade caravan the same way you would a two-way.
	 * @param yourTownID
	 * @param theirTownID
	 * @return
	 */
	public boolean makeTradeCaravan(int yourTownID, int x, int y) {
		Town t = g.findTown(x,y);
		if(t==null) {
			setError("No town on this x,y!");
			return false;
		}
		
		Town myT = g.findTown(yourTownID);
		if(myT==null) {
			setError("Invalid tid!");
			return false;
		}
		if(t.isZeppelin()||myT.isZeppelin()) {
			setError("Both towns must not be Airships for a trade caravan to work!");
			return false;
		}
		int ticksToHit=(int) ((2*getTradeETA(myT.townID,t.townID)+2)*GodGenerator.gameClockFactor);
		//intervaltime is always in s!
		TradeSchedule ts = new TradeSchedule(myT,  t,(long)  0,(long)0,(long)0,(long)0, (long) 0,(long) 0, (long) 0,(long)  0,  ticksToHit+2, -1,  false,null,true);
		return true;
	}
	}

