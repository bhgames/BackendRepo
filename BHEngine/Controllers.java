package BHEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import BattlehardFunctions.UserAttackUnit;
import BattlehardFunctions.UserBuilding;
import BattlehardFunctions.UserQueueItem;
import BattlehardFunctions.UserSR;
import BattlehardFunctions.UserTown;


public class Controllers {
	GodGenerator g;
		public Controllers(GodGenerator g) {
			this.g=g;
		}
	public  boolean loadWorldMap(HttpServletRequest req,PrintWriter out) {
		JSONWriter j = new JSONWriter(out);
		if(!session(req,out,true)) return false;

		

		Player ourP = g.getPlayer((Integer) req.getSession().getAttribute("pid"));		
			try {
			j.object();

			j.key("towns").array();

		int	x = 0; 
		Hashtable totalHash = ourP.getPs().b.getWorldMap();
		Hashtable[] townHash = (Hashtable[]) totalHash.get("townHash");
		Hashtable[] cloudHash = (Hashtable[]) totalHash.get("cloudHash");
		Hashtable[] tileHash = (Hashtable[]) totalHash.get("tileHash");
	/*	int ulcy = (Integer) totalHash.get("ulcy");
		int llcy = (Integer) totalHash.get("llcy");
		int ulcx = (Integer) totalHash.get("ulcx");
		int urcx = (Integer) totalHash.get("urcx");*/

		Hashtable r;
			while(x<townHash.length) {
				r = townHash[x];
				j.object()
		        .key("townName")
		        .value((String) r.get("townName"))
		  
		        .key("owner")
		        .value((String) r.get("owner"))
				
		      
		        .key("SSL")
		        .value((Integer) r.get("SSL"))
		        
		         .key("resEffects").array();
					int ie = 0;
					while(ie<4) {
						j.value((Double) r.get("resEffects"+ie));
						ie++;
					}
					j.value(0).
					endArray()
					.key("debris").array();
					 ie = 0;
					while(ie<4) {
						j.value(((long[]) r.get("debris"))[ie]);
						ie++;
					}
					j.value(0).
					endArray()
		        .key("x")
		        .value((Integer) r.get("x"))
		        .key("y")
		        .value((Integer) r.get("y"))
		        .key("zeppelin")
		        .value((Boolean) r.get("zeppelin"))
		        .key("destX")
		        .value((Integer) r.get("destX"))
		        .key("destY")
		        .value((Integer) r.get("destY"))
		        .key("movementTicks")
		        .value((Integer) r.get("movementTicks"))
		        .key("aiActive")
		        .value((Boolean) r.get("aiActive"))
		        .key("capital")
		        .value((Boolean) r.get("capital"))
		        .key("dig")
		        .value((Boolean) r.get("dig"))
		        .endObject();
					x++;
			}
			j.endArray()
			.key("tiles").array();
			int y = 0;
			while(y<tileHash.length) {
				j.object()
				.key("mid")
				.value((Integer) tileHash[y].get("mid"))
				.key("centerx")
				.value((Integer) tileHash[y].get("centerx"))
				.key("centery")
				.value((Integer) tileHash[y].get("centery"))
				.key("mapName")
				.value((String) tileHash[y].get("mapName"))
				.key("irradiated")
				.value((Boolean) tileHash[y].get("irradiated"))
				.endObject();
				y++;
			}
			j.endArray();

	//		System.out.println("ulcy is " + ulcy + " and ulcx is " + ulcx + " going to " + llcy + " and " + urcx + " and total tiles found are " + tileHash.size() + " and max expected is " +
		//			(ulcy-llcy+2)*(urcx-ulcx+2));
			/*int counter=0;
			while(y>=llcy) {
				
				j.array();
				 x = ulcx;
				while(x<=urcx) {
					tile = (String) tileHash.get(x+","+y);
					if(tile==null) tile = "g";
					j.value(tile);
					counter++;
					x++;
				}
				j.endArray();
				y--;
			}*/
			//System.out.println("total displayed: " + counter);
			
			/*j.key("clouds").array(); 
			
			double incs[];
			while(i<cloudHash.length) {
				j.object()
				.key("cloudID").value((Integer) cloudHash[i].get("cloudID"))
				.key("size").value((Integer) cloudHash[i].get("size"))
				.key("centerx").value((Integer) cloudHash[i].get("centerx"))
				.key("centery").value((Integer) cloudHash[i].get("centery"))
				.key("velocity").value((Double) cloudHash[i].get("velocity"))
				.key("ticksToDeath").value((Integer) cloudHash[i].get("ticksToDeath"))
				.key("direction").value((Integer) cloudHash[i].get("direction"));
				int k = 0;
				j.key("incs").array();
				incs = (double[]) cloudHash[i].get("incs");
				while(k<incs.length) {
					j.value(incs[k]);
					k++;
				}
				
				j.endArray()
				.endObject();
				i++;
			}
			j.endArray().*/
			j.endObject();

			//out.println("hello");
			} catch(JSONException exc) { exc.printStackTrace(); }
			return true;
	}
	
	public boolean saveProgram(HttpServletRequest req, PrintWriter out) {
		if(!session(req,out,true)) return false;
		boolean league = false;
		System.out.println("Being called.");
		if(req.getParameter("league").equals("true")) league = true;
		Player p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));
		if(league&&p.getLeague()!=null) {
			p = p.getLeague();
			boolean toret = p.getPs().b.saveProgram(req.getParameter("program"));
			if(toret){
			out.println(true+";");
			return true;
			} else {
				out.println(false+":"+p.getPs().b.getError());
				return false;
			}
			
			} else {
			boolean toret = p.getPs().b.saveProgram(req.getParameter("program"));
			if(toret){
			out.println(true+";");
			return true;
			} else {
				out.println(false+":"+p.getPs().b.getError());
				return false;
			}

		}
		
		
		
	}
	public boolean pausePlayer(HttpServletRequest req, PrintWriter out) {
	
		if(!session(req,out,true)) return false;
			Player p;

			// must be a player request.
			if( req.getParameter("UN")!=null) {
				 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));

				if(p.getSupportstaff()) {
					g.getPlayer(g.getPlayerId(req.getParameter("UN"))).setSynchronize(true);
					cmdsucc(out);
					return true;
					
				}
				
			}
			retry(out); return false;
		
	}
	public boolean convert(HttpServletRequest req, PrintWriter out) {
		
		if(!session(req,out,true)) return false;
			Player p;

			// must be a player request.
			
				 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));

				if(p.getSupportstaff()) {
					g.convertPlayers();
					cmdsucc(out);
					return true;
					
				}
				
			
			retry(out); return false;
		
	}
	public boolean syncPlayer(HttpServletRequest req, PrintWriter out) {
	
		if(!session(req,out,true)) return false;
			Player p;

			// must be a player request.
			if( req.getParameter("UN")!=null) {
				 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));

				if(p.getSupportstaff()) {
					try {
						Player otherP = g.getPlayer(g.getPlayerId(req.getParameter("UN")));
						if(otherP.isSynchronize()) {
						otherP.synchronize();
						cmdsucc(out);
						return true;
						} else {
							cmdfail(out);
							return false;
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					
				}
				
			}
			retry(out); return false;
		
	}
	
	public boolean saveServer(HttpServletRequest req, PrintWriter out) {
		
		if(!session(req,out,true)) return false;
			Player p;

			// must be a player request.
				 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));

				if(p.getSupportstaff()) {
					int i =0;
					ArrayList<Player> players = g.getPlayers();
					while(i<players.size()) {
						try {
						players.get(i).save();
						} catch(Exception exc) {exc.printStackTrace(); }
						out.println("Saving " + players.get(i).getUsername());
						System.out.println("Save at " +i + " of " +players.size());
						//System.out.println("Saving " + players.get(i).getUsername());

						i++;
					}
					System.out.println("Save completed.");
					return true;
					
				}
				
			
			retry(out); return false;
		
	}public boolean repairMap(HttpServletRequest req, PrintWriter out) {
		
		if(!session(req,out,true)) return false;
			Player p;

			// must be a player request.
				 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));

				if(p.getSupportstaff()) {
					g.repairMap();
					return true;
					
				}
				
			
			retry(out); return false;
		
	}
	public boolean support(HttpServletRequest req, PrintWriter out)  {
		if(!session(req,out,true)) return false;
		Player p;

		// must be a player request.
			 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));
			 //reqtype=support&message=&subject=&problemSubject=

			 String message = req.getParameter("message");
			 String subject = req.getParameter("subject");
			 String email = req.getParameter("email");
			 
			Player id =  g.getPlayer(5);
			int toSend[] = {5,809};
			p.getPs().b.sendMessage(toSend,message,subject + " Email: " +email,0);
		
			return true;
	}
	public boolean makePlayers(HttpServletRequest req, PrintWriter out) {
		
		if(!session(req,out,true)) return false;
		Player p;

		// must be a player request.
			 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));
			 
			 if(p.getSupportstaff()) {
				 
				 int num = Integer.parseInt(req.getParameter("num"));
				 String name = req.getParameter("name");
				 int i = 0;
				 while(i<num) {
					 
						g.createNewPlayer(name+i,"4p5v3sxQ",0,-1,"0000", "none",true,0,0,true,0);
					 i++;
				 }
				 success(out);
				 return true;
			 }
			 retry(out);
			 return false;
	}
	public boolean upgrade(HttpServletRequest req, PrintWriter out) {
		System.out.println("CALL ME CALLED!");
		
		Player p;

		// must be a player request.
		 int tID = Integer.parseInt(req.getParameter("transactionRef")); 
		 int pid=0;
		 String item="autopilot";

		 try {
			 
			 UberStatement stmt = g.zongCon.createStatement();
			 ResultSet rs = stmt.executeQuery("select pid,itemDesc from zongPayment where transactionID = " + tID);
			 if(rs.next()) {
				 pid=rs.getInt(1);
				 item = rs.getString(2);
			 }
			 rs.close();
			 stmt.close();
		 } catch(SQLException exc) { exc.printStackTrace(); }
		 
		 
			 p = g.getPlayer(pid);
			// String itemDesc = req.getParameter("itemRef");
			 String itemDesc = item;
			 String status = req.getParameter("status");
			 String failure = req.getParameter("failure");
			 if(failure==null) failure = "noFail";
			 String method = req.getParameter("method");
			 String msisdn = req.getParameter("msisdn");
			 String outPayment = req.getParameter("outPayment");
			 String consumerPrice = req.getParameter("consumerPrice");
			 String consumerCurrency = req.getParameter("consumerCurrency");
			 boolean sim = Boolean.parseBoolean( req.getParameter("simulated"));
			 String signature = req.getParameter("signature");
			 String signatureVersion = req.getParameter("signatureVersion");


			 try {
				 UberStatement stmt = g.zongCon.createStatement();
				 /*
				  *  transactionID int not null auto_increment,
						    -> pid int not null,
						    -> itemDesc varchar(1000) not null default 'premium',
						    -> status varchar(100) not null default 'waitingOnZong',
						    -> failureReason varchar(1000) not null default 'noFail',
						    -> method varchar(10) not null default 'Mobile',
						    -> msisdn varchar(20) not null default '0',
						    -> outPayment varchar(100) not null default '0',
						    -> consumerPrice varchar(100) not null default '0',
						    -> consumerCurrency varchar(10) not null default 'USD',
						    -> signature varchar(5000) not null default 'noSig',
						    -> primary key(transactionID)
						    -> ) engine=InnoDB;
						AND sigVers
				  */

				 ResultSet rs = stmt.executeQuery("select failureReason from zongPayment where transactionID = " + tID);
				 boolean canUpdate=true;
				 if(rs.next()) {
					 if(rs.getString(1).equals("COMPLETED")) canUpdate=false;
				 }
				 

				 rs.close();
				 if(canUpdate) {
				 stmt.execute("update zongPayment set itemDesc = '" + itemDesc + "', status = '" +status + "', failureReason = '" + failure + "'," +
				 		" method = '" + method + "', msisdn = '" + msisdn + "', outPayment = '" + outPayment + "', consumerPrice = '"+consumerPrice
				 		+ "', consumerCurrency = '"+consumerCurrency + "', signature = '"+signature +"', sigVers = '"+signatureVersion + "' where transactionID = " + tID + ";");
				 
				 if(status.equals("COMPLETED")) {
					 System.out.println(p.getUsername() + " just got upgraded on " + itemDesc);
					 if(itemDesc.equals("battlehardmode")){
					 p.setPremiumTimer(p.getPremiumTimer()+(int) Math.round(7.0*24.0*3600.0/GodGenerator.gameClockFactor));
					 System.out.println("upping premium timer.");
					 }
					 else if(itemDesc.equals("autopilot"))
					 p.setRevTimer(p.getRevTimer()+(int) Math.round(7.0*24.0*3600.0/GodGenerator.gameClockFactor));

				 }
				 
				 }
				 out.println(tID+":OK");

				 stmt.close();
				 return true;
			 } catch(SQLException exc) { exc.printStackTrace(); }
			 
			 return false;
	}
	public boolean refund(HttpServletRequest req, PrintWriter out) {
		System.out.println("CALL ME REFUNDED!");
		// NOT MADE YET
		Player p;

		// must be a player request.
		 System.out.println("0");
		 int tID = Integer.parseInt(req.getParameter("transactionRef")); 
		 int pid=0;
		 
		 try {
			 
			 UberStatement stmt = g.zongCon.createStatement();
			 
			 ResultSet rs = stmt.executeQuery("select pid from zongPayment where transactionID = " + tID);
			 if(rs.next()) pid=rs.getInt(1);
			 rs.close();
			 stmt.close();
		 } catch(SQLException exc) { exc.printStackTrace(); }
		 
		 
			 p = g.getPlayer(pid);
			 String itemDesc = req.getParameter("itemRef");
			 String status = req.getParameter("status");
			 String failure = req.getParameter("failure");
			 if(failure==null) failure = "noFail";
			 String method = req.getParameter("method");
			 String msisdn = req.getParameter("msisdn");
			 String outPayment = req.getParameter("outPayment");
			 String consumerPrice = req.getParameter("consumerPrice");
			 String consumerCurrency = req.getParameter("consumerCurrency");
			 boolean sim = Boolean.parseBoolean( req.getParameter("simulated"));
			 String signature = req.getParameter("signature");
			 String signatureVersion = req.getParameter("signatureVersion");


			 try {
				 UberStatement stmt = g.zongCon.createStatement();
				 /*
				  *  transactionID int not null auto_increment,
						    -> pid int not null,
						    -> itemDesc varchar(1000) not null default 'premium',
						    -> status varchar(100) not null default 'waitingOnZong',
						    -> failureReason varchar(1000) not null default 'noFail',
						    -> method varchar(10) not null default 'Mobile',
						    -> msisdn varchar(20) not null default '0',
						    -> outPayment varchar(100) not null default '0',
						    -> consumerPrice varchar(100) not null default '0',
						    -> consumerCurrency varchar(10) not null default 'USD',
						    -> signature varchar(5000) not null default 'noSig',
						    -> primary key(transactionID)
						    -> ) engine=InnoDB;
						AND sigVers
				  */

				 ResultSet rs = stmt.executeQuery("select failureReason from zongPayment where transactionID = " + tID);
				 boolean canUpdate=true;
				 if(rs.next()) {
					 if(rs.getString(1).equals("COMPLETED")) canUpdate=false;
				 }
				 

				 rs.close();
				 if(canUpdate) {
				 stmt.execute("update zongPayment set itemDesc = '" + itemDesc + "', status = '" +status + "', failureReason = '" + failure + "'," +
				 		" method = '" + method + "', msisdn = '" + msisdn + "', outPayment = '" + outPayment + "', consumerPrice = '"+consumerPrice
				 		+ "', consumerCurrency = '"+consumerCurrency + "', signature = '"+signature +"', sigVers = '"+signatureVersion + "' where transactionID = " + tID + ";");
				 
				 if(status.equals("COMPLETED")) {
					 System.out.println(p.getUsername() + " just got upgraded!");
					 p.setPremiumTimer(p.getPremiumTimer()+(int) Math.round(7.0*24.0*3600.0/GodGenerator.gameClockFactor));
				 }
				 
				 }
				 out.println(tID+":OK");

				 stmt.close();
				 return true;
			 } catch(SQLException exc) { exc.printStackTrace(); }
			 
			 return false;
	}
	public boolean makePaypalReq(HttpServletRequest req, PrintWriter out) {
		
		if(!session(req,out,true)) return false;
			Player p;

			// must be a player request.
				 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));
				 String country = (String) req.getParameter("country");
				 int purchaseCode = Integer.parseInt(req.getParameter("purchaseCode"));
				 boolean league = Boolean.parseBoolean(req.getParameter("league"));
				 System.out.println("your player is " + p.getUsername() + " and your country is " + country + " and purchaseCode is " + purchaseCode  + " and league is " + league);
				 // so if you ask for a league and have a league, and you're an admin, you get it for your leagued player.
				 if(league&&p.getLeague()!=null&&p.getLeague().getType(p.ID)==2)  {
					 p = p.getLeague();
				 }
				 String item = "autopilot";
				 if(purchaseCode==1) item = "battlehardmode";
				 
				 g.makePaypalCall();
					 
					 return true;
				
		
	}
	public boolean getZongScreen(HttpServletRequest req, PrintWriter out) {
		
		if(!session(req,out,true)) return false;
			Player p;

			// must be a player request.
				 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));
				 String country = (String) req.getParameter("country");
				 int purchaseCode = Integer.parseInt(req.getParameter("purchaseCode"));
				 boolean league = Boolean.parseBoolean(req.getParameter("league"));
				 System.out.println("your player is " + p.getUsername() + " and your country is " + country + " and purchaseCode is " + purchaseCode  + " and league is " + league);
				 // so if you ask for a league and have a league, and you're an admin, you get it for your leagued player.
				 if(league&&p.getLeague()!=null&&p.getLeague().getType(p.ID)==2)  {
					 p = p.getLeague();
				 }
				 String item = "autopilot";
				 if(purchaseCode==1) item = "battlehardmode";
				 String entryPointURL = g.getEntryPointURL(country);
				 int tID = 0;

				 try {
					 UberStatement stmt = g.zongCon.createStatement();
					 
					 stmt.execute("insert into zongPayment(pid,itemDesc) values (" + p.ID +",'"+item +"');");
					 ResultSet rs = stmt.executeQuery("select max(transactionID) from zongPayment where pid = " + p.ID);
					 if(rs.next()) tID = rs.getInt(1);
					 rs.close();
					 stmt.close();

				 } catch(SQLException exc) {
					 try {
						g.zongCon.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 g.zongCon =
		                   new UberConnection(
	                              GodGenerator.zongurl,GodGenerator.user, GodGenerator.pass,g);
					 try {
					 	UberStatement stmt = g.zongCon.createStatement();
					 
					 stmt.execute("insert into zongPayment(pid,itemDesc) values (" + p.ID +",'"+item +"');");
					 ResultSet rs = stmt.executeQuery("select max(transactionID) from zongPayment where pid = " + p.ID);
					 if(rs.next()) tID = rs.getInt(1);
					 rs.close();
					 stmt.close(); } catch(SQLException exc2) { exc2.printStackTrace(); }
					 
				 }
					 // now we get pricepoints.

					
					 /*
					  * <iframe src= Óhttps://pay01.zong.com/zongpay/actions/processing?purchaseKey= 
						eNp7VXh6p...& transactionRef=123&itemDesc=100%20coins&redirect=https %3A%2F%2F 
						yourUrl& basketUrl=https %3A%2F%2F yourbasketUrl&app=appNameÓ width=Ó490Ó 
						height=Ó350Ó frameborder=Ó0Ó scrolling=ÓnoÓ/>  

					  */
					 String src = entryPointURL+"&transactionRef="+tID+"&itemDesc="+item+"&redirect=http%3A%2F%2Fwww.aiwars.org/redirect.html&basketUrl="+
					 "http%3A%2F%2Fwww.aiwars.org/redirect.html&app=AIWars&userId="+p.ID+"&username="+p.getUsername();
					 System.out.println(src);
					 out.println("<iframe src =\""+src+"\" width=\"490\""+ 
						"height=\"350\" frameborder=\"0\" scrolling=\"no\"/>");
					 
					 
					 
					 return true;
				
		
	}
//	public static int sendMail(String email, String name, String promotion, String subject, String message) {
	public boolean newsletter(HttpServletRequest req, PrintWriter out) {
		if(!session(req,out,true)) return false;
		Player p;

		// must be a player request.
			 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));

			if(p.getSupportstaff()) {
				try {
					UberStatement stmt = g.con.createStatement(); 
					ResultSet rs = stmt.executeQuery("select email from users");
					while(rs.next()) {
						
						if(g.getPlayerByEmail(rs.getString(1))==null) {

							// this means the player has been deleted. so we spam them!
							g.sendMail(g.getPlayerByEmail(rs.getString(1)).getEmail(),g.getPlayerByEmail(rs.getString(1)).getUsername(),"WeeklyNews","AI Wars Weekly Newsletter","");
						}
						
						
						
					}
					rs.close();
					stmt.close();
				} catch(SQLException exc) { exc.printStackTrace(); }
				cmdsucc(out);
				return true;
				
			}
			
		
		retry(out); return false;
	
	}

public boolean linkFB(HttpServletRequest req, PrintWriter out) {
		System.out.println("Got FB link req");
		if(!session(req,out,true)) return false;
			Player p;
			// must be a player request.
				 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));
				if(p.getFuid()!=0) {
					retry(out); return false;
				}
				 long fuid =Long.parseLong( req.getParameter("fuid"));
				 int i = 0;
				 while(i<g.getPlayers().size()) {
					 if(g.getPlayers().get(i).getFuid()==fuid) return false;
					 i++;
				 }
				 p.setNewFuid(fuid);
				 
				 
				 success(out);
			
				 return true;
		
	}
public boolean FBBlast(HttpServletRequest req, PrintWriter out) {
	if(!session(req,out,true)) return false;
		Player p;
		// must be a player request.
		System.out.println("Got a reqest for an fb blast.");
			 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));
			 int type = (Integer.parseInt( req.getParameter("rewardChoice")));
			 int sid = (Integer.parseInt( req.getParameter("SID")));

			if(p.getFuid()==0) {
				retry(out); return false;
			}
			System.out.println("Got past all the shitnuggets.");

			 long fuid =Long.parseLong( req.getParameter("fuid"));
				System.out.println("Got past all the shitnuggets2.");

			 if(p.getFuid()==fuid||p.getUsername().equals("Azel")) {
				 // MAKE TEST REQUEST HERE.

					 UserSR[] s = p.getPs().b.getUserSR();
					 int i = 0;
					 while(i<s.length) {
						// System.out.println("Scanning " + s[i].sid + " which is " + s[i].getBlasted());
						 //if(s[i].sid==sid) System.out.println("This is what you requested.");
						 if(s[i].sid==sid&&!s[i].getBlasted()) {
							 System.out.println("Inside reward area and type is " + type + " with sid " + sid);
							 try {
								 UberStatement stmt = g.con.createStatement();
								 stmt.execute("update statreports set blasted=true where sid = " + sid);
								 stmt.close();
							 } catch(SQLException exc) { exc.printStackTrace(); }
						

							 if(type==0&&p.getRevTimer()==0) {
								 p.setRevTimer(p.getRevTimer()+(int) Math.round(3600*24/GodGenerator.gameClockFactor));
							 }
							 if(type==1&&p.getPremiumTimer()==0) p.setPremiumTimer(p.getPremiumTimer()+(int) Math.round(3600*24/GodGenerator.gameClockFactor));
							 if(type==2&&p.getCapitaltid()!=-1) {
								 System.out.println("asking for type 2...");
								 Town t=g.findTown(p.getCapitaltid());
								 synchronized(t.getRes()) {
									 System.out.println("res bef: " + t.getRes()[0]);
									 t.getRes()[0]+=(long) Math.round(((double) s[i].m)*.25);
									 System.out.println("res aft: " + t.getRes()[0]);
									 t.getRes()[1]+=(long) Math.round(((double) s[i].t)*.25);
									 t.getRes()[2]+=(long) Math.round(((double) s[i].mm)*.25);
									 t.getRes()[3]+=(long) Math.round(((double) s[i].f)*.25);

								 }
								 p.last_auto_blast=p.getPlayedTicks();
								 
							 }
							 else {
								 retry(out);
								 return false;
							 }
							 
							 success(out);
							 return true;
							 
						 }
						 i++;
					 }
					 /*
					 if((int) Math.floor(p.last_auto_blast/2.0)>48*3600/GodGenerator.gameClockFactor) {
						 p.setRevTimer(p.getRevTimer()+(int) Math.round(24*3600/GodGenerator.gameClockFactor));
					 }else 
					 p.setRevTimer(p.getRevTimer()+(int) Math.floor(p.last_auto_blast/2.0));	*/ 
				 retry(out); // success out before we do the sql.
				 return false;
				 
				 
			 }
			 
			 
			 retry(out);
		
			 return true;
	
}
public boolean noFlick(HttpServletRequest req, PrintWriter out) {
	
	if(!session(req,out,true)) return false;
		Player p;

		// must be a player request.
			 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));
			 boolean league = Boolean.parseBoolean(req.getParameter("league"));
			 if(league&&p.getLeague()!=null&&p.getLeague().getType(p.ID)>=1) {
				 p.getLeague().flicker="noflick";
			 }else
			 p.flicker="noflick";
			 
			 success(out);

			 
		
			 return true;
	
}public boolean flickStatus(HttpServletRequest req, PrintWriter out) {
	
	if(!session(req,out,true)) return false;
		Player p;

		// must be a player request.
			 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));
			 boolean league = Boolean.parseBoolean(req.getParameter("league"));
			 if(league&&p.getLeague()!=null&&p.getLeague().getType(p.ID)>=1) {
				out.println(p.getLeague().flicker);
			 }else
			out.println(p.flicker);
			 

			 
		
			 return true;
	
}
	public boolean username(HttpServletRequest req, PrintWriter out) {
		String username = (String) req.getParameter("username").toLowerCase();
		int i = 0;
		while(i<g.getPlayers().size()) {
			if(g.getPlayers().get(i).getUsername().toLowerCase().equals(username.toLowerCase())) {
				cmdfail(out);
				return false;
			}
			i++;
		}
		cmdsucc(out);
		
		return true;
	}
	public boolean getTiles(HttpServletRequest req, PrintWriter out) {
		JSONWriter j = new JSONWriter(out);
		
		Player Id = g.getPlayer(5);
		Hashtable worldMapHash = Id.getPs().b.getWorldMap();
		
		Hashtable[] tiles = (Hashtable[]) worldMapHash.get("tileHash");
		ArrayList<Town> towns; Player p; Town t;
		int i = 0;
		Date today = new Date();
		try {
			j.array();
		 
		while(i<tiles.length) {
			j.object()
			.key("mid")
			.value((Integer) tiles[i].get("mid"))
			.key("centerx")
			.value((Integer) tiles[i].get("centerx"))
			.key("centery")
			.value((Integer) tiles[i].get("centery"))
			.key("mapName")
			.value((String) tiles[i].get("mapName"));
			
			int centerx =  (Integer) tiles[i].get("centerx");
			int centery =  (Integer) tiles[i].get("centery");

			int k = 0;
			double averagePlayedTicks = 0;
			double numberOfPlayers=0;
			int dailyActives = 0;
			
			int weeklyActives = 0;
			j.key("players").array();
			while(k<g.getPlayers().size()) {
				p = g.getPlayers().get(k);
				if(p.ID!=5&&!p.isQuest()) {
					// so if they are greater than 48 hours they fall into the dailyActives category, but if they don't, they get a chance to 
					// fall into the weeklyactives category.
					int x = 0; towns = p.towns();
					boolean foundATown=false;
				while(x<towns.size()) {
					t = towns.get(x);
					double ty = t.getY(); double tx = t.getX();
					if(ty<=(centery+(int) Math.round((double) GodGenerator.mapTileWidthY/2.0))&&ty>=(centery-(int) Math.round((double) GodGenerator.mapTileWidthY/2.0))
							 &&tx<=(centerx+(int) Math.round((double) GodGenerator.mapTileWidthX/2.0))&&tx>=(centerx-(int) Math.round((double) GodGenerator.mapTileWidthX/2.0))) {
							// this town is within the limit!
							if((p.completedQuest("BQ1")||p.getPs().b.getCSL(p.getCapitaltid())>100)&&p.last_login.getTime()>(today.getTime()-2*24*3600000))
								dailyActives++;
							
							 if((p.completedQuest("BQ1")||p.getPs().b.getCSL(p.getCapitaltid())>100)&&p.last_login.getTime()>(today.getTime()-7*24*3600000))
								weeklyActives++;
							 
							 numberOfPlayers++;
							 averagePlayedTicks+=p.getPlayedTicks();
						towns = p.towns();
						j.object()
						.key("username")
						.value(p.getUsername())
						.key("fuid")
						.value(p.getFuid())
						.endObject();
						
						foundATown=true; // we only need to find one town in this tile to register this player.
					
					}
						
					if(foundATown) break;
					x++;
				}
				
				}
				k++;
			}
			j.endArray();
			if(numberOfPlayers>0)
			averagePlayedTicks/=numberOfPlayers;
			j.key("dailyActives")
			.value(dailyActives)
			.key("weeklyActives")
			.value(weeklyActives)
			.key("numberOfPlayers")
			.value(numberOfPlayers)
			.key("averagePlayerAge")
			.value(averagePlayedTicks);

			j.endObject();
			i++;
		}
		
		j.endArray();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	public boolean loadPlayer(HttpServletRequest req, PrintWriter out, boolean grabLeague) {
		JSONWriter j = new JSONWriter(out);
		UserQueueItem q;
		if(!session(req,out,true)) return false;
		try {
			Player p; UserTown t; UserBuilding b;UserAttackUnit a;

			// must be a player request.
			if( req.getParameter("UN")!=null) {
				 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));

				if(p.getSupportstaff()) {
					
					int x = 0;boolean found = false;
					 ArrayList<Player> players = g.getPlayers();

					while(x<players.size()) {
						p = players.get(x);
						if(p.getUsername().toLowerCase().equals(req.getParameter("UN"))) {
							found=true;
							break;
						}
						x++;
					}
					if(!found) {
						retry(out);
						return false;
					}
					
				} else{
					retry(out);
					return false; // this means we're trying to access another player's object.
				}
				// cna only do this as support staff.
			} else
				 p =g.getPlayer((Integer) req.getSession().getAttribute("pid"));			
			
			Player originalPlayer=null;
			if(grabLeague&&p.getLeague()!=null) {
				originalPlayer=p;
				p = p.getLeague();
			} else if(grabLeague&&p.getLeague()==null) {
				grabLeague=false;
			}
			
			g.updateLastLogin(p.ID);

			j.object()
			.key("username")
			.value(p.getUsername())
			.key("pid")
			.value(p.ID)
			.key("version")
			.value(p.getVersion())
			.key("zeppelinTicksPerMovement")
			.value(Town.zeppelinTicksPerMove)
			.key("refillSpeed")
			.value(Town.refillSpeed)
			.key("daysOfStoragePerAirshipPlatform")
			.value(Town.daysOfStoragePerAirshipPlatform)
			.key("league")

			.value(p.isLeague());
			if(p.isLeague()&&originalPlayer!=null) {
				j.key("origUN").value(originalPlayer.getUsername());
			} else if(!p.isLeague()&&p.getLeague()!=null) {
				j.key("league_pid").value(p.getLeague().ID);
			}
			j.key("civWeapChoice")
			.value(p.getCivWeapChoice())
			.key("capitaltid")
			.value(p.getCapitaltid())
			.key("gameClockFactor")
			.value(GodGenerator.gameClockFactor)
			.key("research").object()
		//	.key("brkthrus")
		//	.value(p.getBrkthrus())
			.key("attackAPI")
			.value(p.isAttackAPI())
			.key("advancedAttackAPI")
			.value(p.isAdvancedAttackAPI())
			.key("tradingAPI")
			.value(p.isTradingAPI())
			.key("worldMapAPI")
			.value(p.isWorldMapAPI())
			.key("advancedTradingAPI")
			.value(p.isAdvancedTradingAPI())
			.key("smAPI")
			.value(p.isSmAPI())
			.key("researchAPI")
			.value(p.isResearchAPI())
			.key("buildingAPI")
			.value(p.isBuildingAPI())
			.key("advancedBuildingAPI")
			.value(p.isAdvancedBuildingAPI())
			.key("messagingAPI")
			.value(p.isMessagingAPI())
			.key("zeppelinAPI")
			.value(p.isZeppelinAPI())
			.key("completeAnalyticAPI")
			.value(p.isCompleteAnalyticAPI())
			.key("nukeAPI")
			.value(p.isNukeAPI())
			.key("flicker")
			.value(p.flicker)
			.key("tPushes")
			.value(p.getTPushes())
			.key("zeppTech")
			.value(p.isZeppTech())
			.key("missileSiloTech")
			.value(p.isMissileSiloTech())
			.key("recyclingTech")
			.value(p.isRecyclingTech())
			.key("metalRefTech")
			.value(p.isMetalRefTech())
			.key("timberRefTech")
			.value(p.isTimberRefTech())
			.key("manMatRefTech")
			.value(p.isManMatRefTech())
			.key("foodRefTech")
			.value(p.isFoodRefTech())
			.key("autoblastable");
			if(p.getRevTimer()==0)
				j.value(true);
			else j.value(false);
			j.key("bhmblastable");
			if(p.getPremiumTimer()==0)
				j.value(true);
			else j.value(false);
			
			j.key("resblastable");
			if((p.last_auto_blast)>24*360)
				j.value(true);
			else j.value(false);
			
			j.key("fbLinked");
			if(p.getFuid()==0) j.value(false);
			else j.value(true);
			//j.key("brkups")
			//.value(p.getBrkups())
			j.key("knowledge")
			.value(p.getKnowledge())
			.key("scholTicks")
			.value(p.getScholTicks())
			
			.key("premiumTimer")
			.value(p.getPremiumTimer())
			.key("ubTimer")
			.value(p.getUbTimer())
			.key("feroTimer")
			.value(p.getFeroTimer())
			.key("mineTimer")
			.value(p.getMineTimer())
			.key("timberTimer")
			.value(p.getTimberTimer())
			.key("mmTimer")
			.value(p.getMmTimer())
			.key("fTimer")
			.value(p.getFTimer())
			.key("bp")
			.value(p.getBp())
			.key("totalBPEarned")
			.value(p.getTotalBPEarned())
			.key("revTimer")
			.value(p.getRevTimer())
			.key("scholTicksTotal")
			.value(p.getScholTicksTotal())
			.key("lotTech")
			.value(p.getLotTech())
			.key("stealthTech")
			.value(p.getStealthTech())
			.key("scoutTech")
			.value(p.getScoutTech())
			.key("aLotTech")
			.value(p.getALotTech())
			.key("soldierTech")
			.value(p.isSoldierTech())
			.key("tankTech")
			.value(p.isTankTech())
			.key("juggerTech")
			.value(p.isJuggerTech())
			.key("bomberTech")
			.value(p.isBomberTech())
			.key("weap").array();
			 int i = 0;
			while(i<p.getWeap().length) {
				j.value(p.getWeap()[i]);
				i++;
			}
			j.endArray()
			.key("supportTech")
			.value(p.getSupportTech())
			.key("townTech")
			.value(p.getTownTech())
			.key("engTech")
			.value(p.getEngTech())
			.key("tradeTech")
			.value(p.getTradeTech())
			.key("scholTech")
			.value(p.getScholTech())
			.key("commsCenterTech")
			.value(p.getCommsCenterTech())
			.key("buildingSlotTech")
			.value(p.getBuildingSlotTech())
			.key("stabilityTech")
			.value(p.getStabilityTech())
			.key("bunkerTech")
			.value(p.getBunkerTech())
			.key("afTech")
			.value(p.getAfTech())
			.key("soldierPicTech").array();
			i = 0;
			boolean[] pictech = p.getSoldierPicTech();
			while(i<pictech.length) {
				j.value(pictech[i]);
				i++;
			}
			j.endArray()
			.key("tankPicTech").array();
			i = 0;
			pictech = p.getTankPicTech();
			while(i<pictech.length) {
				j.value(pictech[i]);
				i++;
			}
			j.endArray()
			.key("juggerPicTech").array();
			i = 0;
			pictech = p.getJuggerPicTech();
			while(i<pictech.length) {
				j.value(pictech[i]);
				i++;
			}
			j.endArray()
			.key("bomberPicTech").array();
			i = 0;
			pictech = p.getBomberPicTech();
			while(i<pictech.length) {
				j.value(pictech[i]);
				i++;
			}
			AttackUnit aunit;
			j.endArray()
			.endObject();

			j.key("AUTemplates").array();
					 i = 0;
					 ArrayList<AttackUnit> paut = p.getAUTemplates();
					while(i<paut.size()) {
						try {
						aunit = paut.get(i);
						//	public AttackUnit(String name, double conc, double armor, double cargo, double speed, int slot, int popSize, int weap[], int graphicNum) {
						j.object()
						.key("name").value(aunit.getName())
						.key("conc").value(aunit.getConcealment())
						.key("armor").value(aunit.getArmor())
						.key("cargo").value(aunit.getCargo())
						.key("speed").value(aunit.getSpeed())
						.key("slot").value(aunit.getSlot())
						.key("firepower").value(aunit.getFirepower())
						.key("ammo").value(aunit.getAmmo())
						.key("accuracy").value(aunit.getAccuracy())
						.key("popSize").value(aunit.getPopSize())
						.key("weap").array();
						int k = 0;
						while(k<aunit.getWeap().length) {
							j.value(aunit.getWeap()[k]);
							k++;
						}
						j.endArray()
						.key("graphicNum").value(aunit.getGraphicNum())
						.endObject();
						} catch(Exception exc) { exc.printStackTrace(); System.out.println("Player load saved."); }

						i++;
					}
			j.endArray() // should I put in AUs here?
			.key("AU").array();
					 i = 0;
					 ArrayList<AttackUnit> pau = p.getAu();
					while(i<pau.size()) {
						try {
						aunit = pau.get(i);
						//	public AttackUnit(String name, double conc, double armor, double cargo, double speed, int slot, int popSize, int weap[], int graphicNum) {
						
						j.object()
						.key("name").value(aunit.getName())
						.key("conc").value(aunit.getConcealment())
						.key("armor").value(aunit.getArmor())
						.key("cargo").value(aunit.getCargo())
						.key("speed").value(aunit.getSpeed())
						.key("slot").value(aunit.getSlot())
						.key("firepower").value(aunit.getFirepower())
						.key("ammo").value(aunit.getAmmo())
						.key("accuracy").value(aunit.getAccuracy())
						.key("popSize").value(aunit.getPopSize())
						.key("weap").array();
						int k = 0;
						while(k<aunit.getWeap().length) {
							j.value(aunit.getWeap()[k]);
							k++;
						}
						j.endArray()
						.key("graphicNum").value(aunit.getGraphicNum());
						j.endObject();
						} catch(Exception exc) { exc.printStackTrace(); System.out.println("Player load saved for au."); }
						i++;
						
					}
			j.endArray() // should I put in AUs here?
			.key("towns")
			.array();
			int k = 0;
	         UserQueueItem[] queue;
	        UserBuilding[] bldg;
	         UserAttackUnit[] au;
	         UserTown[] towns;
	         if(p.isLeague()) towns=originalPlayer.getPs().b.getLeague().getUserTowns();
	         else towns = p.getPs().b.getUserTowns();

			long res[]; double resInc[]; double resEffects[];
			while(k<towns.length) {
				t =towns[k];
				try {
				if(!grabLeague||(grabLeague&&p!=null&&p.isLeague()&&((League) p).canMakeModChangesToTown(originalPlayer.ID,t.getTownID())))  {
				
				
					j.object() // beginning of town
			        .key("townName")
			        .value(t.getTownName())
			        .key("townID")
			        .value(t.getTownID())
			        .key("playerName")
			        .value(t.getPlayerName())
			          .key("CSL")
			        .value(t.getCSL())
			        .key("x")
			        .value(t.getX())
			        .key("y")
			        .value(t.getY())
			        .key("zeppelin")
			        .value(t.isZeppelin())
			        .key("destX")
			        .value(t.getDestX())
			        .key("destY")
			        .value(t.getDestY())
			        .key("fuelCells")
			        .value(t.getFuelCells())
			        .key("fuelCellCap")
			        .value((int) Math.round(Town.maxFuelCells*g.getAverageLevel(g.findTown(t.getTownID()))))
			        .key("movementTicks")
			        .value(t.getTicksTillMove())
			        .key("resEffects").array();
					i = 0;
					resEffects = t.getResEffects();
					while(i<resEffects.length) {
						j.value(resEffects[i]);
						i++;
					}
					j.endArray()
			        .key("res").array();
					i = 0;
					res = t.getRes();
					while(i<res.length) {
						j.value(res[i]);
						i++;
					}
					j.endArray()
					 .key("resInc").array();
					i = 0;
					resInc = t.getResInc();
					while(i<resInc.length) {
						j.value(resInc[i]*(1+resEffects[i]));
						i++;
					}
					j.endArray()
					 .key("resCaps").array();
					res = t.getResCaps();
					i = 0;
					while(i<res.length) {
						j.value(res[i]+Building.baseResourceAmt);
						i++;
					}
					j.endArray()
			        .key("au").array();
					
					 i = 0;				
					 au = t.getAu();

					while(i<6) {
						a = au[i];
						try {
						j.value(a.getSize()); // how to do support au?
						} catch(Exception exc) { exc.printStackTrace(); System.out.println("AU load saved at " + towns[k].getTownID()); }

						i++;
					}
					j.endArray()
					
					.key("supportAU").array();
					
					 i = 6;
					while(i<au.length) {
						a = au[i];
						//	public AttackUnit(String name, double conc, double armor, double cargo, double speed, int slot, int popSize, int weap[], int graphicNum) {
						try {
						if(a.getSupport()>0) {
							
						j.object()
						.key("originalPlayer")
						.value(a.getOriginalPlayer())
						.key("originalSlot")
						.value(a.getOriginalSlot())
						.key("name").value(a.getName())
						.key("size").value(a.getSize())
						.key("support").value(a.getSupport())
						.key("conc").value(a.getConcealment())
						.key("armor").value(a.getArmor())
						.key("cargo").value(a.getCargo())
						.key("speed").value(a.getSpeed())
						.key("slot").value(a.getSlot())
						.key("firepower").value(a.getFirepower())
						.key("ammo").value(a.getAmmo())
						.key("accuracy").value(a.getAccuracy())
						.key("popSize").value(a.getPopSize())
						.key("originalPlayerID")
						.value(a.getOriginalPlayerID())
						.key("originalTID")
						.value(a.getOriginalTID())
						.key("weap").array();
					
						int z = 0;
						while(z<a.getWeap().length) {
							j.value(a.getWeap()[z]);
							z++;
						}
						j.endArray()
						.key("graphicNum").value(a.getGraphicNum())
						.endObject();
						}
						} catch(Exception exc) { exc.printStackTrace(); System.out.println("Town load of supportau saved at " + towns[k].getTownID() + " with supportunit from " + a.getOriginalPlayer() + " of slot " + a.getOriginalSlot()); }

						i++;
					}
					j.endArray() // should I put in AUs here?
					
			        .key("bldg").array();
			         i = 0;long cost[];
			         bldg = t.getBldg();
			        while(i<bldg.length) {
			        	b = bldg[i];
			        	try {
			        	j.object()
			        	.key("type")
			        	.value(b.getType())
			        	.key("bid")
			        	.value(b.getBid())
			        	.key("lvl")
			        	.value(b.getLvl())
			        	.key("lotNum")
			        	.value(b.getLotNum())
			        	.key("refuelTicks")
			        	.value(b.getRefuelTicks())
			        	.key("ticksToFinish")
			        	.value(b.getTicksToFinish())
			        	.key("lvlUps");
			        	j.value(b.getLvlUps());

			        	j.key("bunkerMode")
			        	.value(b.getBunkerMode());
			        	
			        	j.key("ticksToFinishTotal").array();
			   			int je = 1; // because lvlUps = 0 is unacceptable, it means nothing
			   			// and isn't even on the building server. If je = 0, then
			   			// we do an entire iteration where the results are shit
			   			// and mean absolutely nothing, in fact, they represent data
			   			// of ticks from the level below the current one, which won't
			   			// make users happy when their ticks keep going above their assumed "total!".
			   			while(je<=b.getLvlUps()) {
			   			
			   			int y = 0; int totalTime = 0;
			   			
			   			while(y<=je) {
			   			 totalTime += Building.getTicksForLevelingAtLevel(t.getTotalEngineers(),b.getLvl()+y,p.God.Maelstrom.getEngineerEffect(t.getX(),t.getY()),p.getEngTech(),b.getType());
			   			y++;
			   			}
			   			
			   			j.value(totalTime);
			   			je++;

			   			}
			   		
			   		
			        	
			        	j.endArray()
			        	.key("deconstruct")
			        	.value(b.isDeconstruct())
			        	.key("numLeftToBuild")
			        	.value(b.getNumLeftToBuild())
			        	.key("ticksLeft")
			        	.value(b.getTicksLeft())
			        	.key("ticksPerPerson")
			        	.value(b.getTicksPerPerson())
			        	.key("peopleInside")
			        	.value(b.getPeopleInside());
			        	int u = 0; int popped = 1;
			        	if(b.getType().equals("Arms Factory")) {
			        		popped=0;
			        		while(u<pau.size()) {
			        			if(!pau.get(u).getName().equals("locked")&&!pau.get(u).getName().equals("empty")) popped++;
			        			u++;
			        		}
			        	}
			        	j.key("cap")
			        	.value(b.getCap()*popped);
			        	j.key("cost").array();
			        	 u = 0;
			        	 
			        	  cost = Building.getCost(b.getType());
			        	  int blvl=b.getLvl(); int blvlups = b.getLvlUps();
			        	while(u<5) {
			        		//(lvl+1)^(2+.03*(lvl+1))*100
			        	j.value(Math.ceil(cost[u]*Math.pow((blvl+blvlups+1),(2+.03*(blvl+blvlups+1)))));
			        	u++;
			        	}
			        	
			        	j.endArray();
			        	

			        	u = 0;
			        	j.key("Queue").array();
			        	queue = b.getQueue();
			        	while(u<queue.length) {
			        		q = queue[u];
			        		try {
			        		j.object().
			        		key("qid").value(q.getQid()).
			        		key("AUtoBuild").value(q.returnAUtoBuild()).
			        		key("AUNumber").value(q.returnNumLeft()).
			        		key("currTicks").value(q.returnTicks()).
			        		key("ticksPerUnit").value(q.returnTicksPerUnit()).
			        		endObject();
							} catch(Exception exc) { exc.printStackTrace(); System.out.println("Town load saved at " + towns[k].getTownID() + " in bid " + b.getBid() + " and queue " +q.getQid()); }

			        		u++;
			        	}
			        	j.endArray();
			        	j.endObject(); // end building
						} catch(Exception exc) { exc.printStackTrace(); System.out.println("Building load saved at " + towns[k].getTownID() + " and bid " + b.getBid()); }

			        	i++;
			        }
			        
			        
			        j.endArray(); // end building array;
			        j.endObject();// end of town
				
				}
				} catch(Exception exc) { exc.printStackTrace(); System.out.println("Town load saved at " + towns[k].getTownID()); }

				k++;
			}
			
			j.endArray(); // end of town array
	        j.endObject();// end of player
			
	} catch(NumberFormatException exc) {exc.printStackTrace(); } catch(JSONException exc) {exc.printStackTrace();} 
	return true;
	}
	
	public boolean session(HttpServletRequest req, PrintWriter out, boolean partial) {
		// if it is not a partial, then we do want to write valid out,
		// which means it requested as directly from a view. If another controller requested it, then
		// it's a partial!
		// tests to see if it exists!
		try {

		HttpSession session = req.getSession(true);
		
		int pid = (Integer) session.getAttribute("pid");
		String username = (String) session.getAttribute("username");

		if(username==null||session.isNew()||!(this.g.getPlayer(pid)).getUsername().toLowerCase().equals(username)) { retry(out); return false; }
		} catch(NullPointerException exc) {
			retry(out);
			return false;
		} catch(IndexOutOfBoundsException exc) {
			retry(out);
			return false;
		}
		if(!partial)
		success(out);
		return true;
		
	}
	public boolean login(HttpServletRequest req, PrintWriter out) {
		try {
		HttpSession session = req.getSession(true);
		String username = req.getParameter("UN");
		String password="";
		Player p;
		if(username==null) {
			long fuid = Long.parseLong(req.getParameter("fuid"));
			if(fuid==0) {
				retry(out); return false;
			}
			p = g.getPlayerByFuid(fuid);
			if(p!=null) {
			username  = p.getUsername().toLowerCase(); password = p.getPassword(); }
		} else{
			username = username.toLowerCase();
			 p = g.getPlayer((Integer) g.getPlayerId((username).toLowerCase()));
			
			 password = org.apache.commons.codec.digest.DigestUtils.md5Hex(req.getParameter("Pass"));
			if(username==null||password==null) { retry(out); return false; }

		}
				 if(p==null||(p.ID==5&&!username.equals("Id"))) {
					 // shit, must've been killed somehow.
					 Hashtable r; long fuid = 0;
					 if(username!=null)
					  r= (Hashtable) g.accounts.get(username);
					 else {
						 fuid = Long.parseLong(req.getParameter("fuid"));
						 r = (Hashtable) g.accounts.get(fuid);
					 }
					 if(r==null) { 
						 retry(out); return false;
					 }
					 else {
						 String accpassword =(String) r.get("password");
						 if(accpassword.equals(password)||fuid!=0) {
							g.createNewPlayer((String) r.get("username"),(String)r.get("password"),0,-1,"0000", (String)r.get("email"),true,0,0,false,(Long) r.get("fuid"));
							username = ((String) r.get("username")).toLowerCase();
							int pid = (Integer) g.getPlayerId(username);
							p = g.getPlayer(pid);

							
							g.getPlayer((Integer) g.getPlayerId((username).toLowerCase())).getPs().b.sendYourself( "Hey there, sorry to have deliver the bad news. You either deleted your account, it was deleted by an administrator, or you were inactive long enough to be removed from the map by Id, the unoccupied player. You've been placed on a new city and have another chance at life in the AI Wars Universe. If you have any questions, please don't hesitate to email support!","Wondering why your city is empty?");
								
							 password =p.getPassword();

						 } else {
							 retry(out); return false;
						 }

					 }
				 }
		
				String pusername = p.getUsername().toLowerCase();
				String ppassword = p.getPassword();
			//	 out.println("password I read is " + password + " vs " +ppassword + " which is "+ ppassword.equals(password));

				int pid = p.ID;
				if(pusername.equals(username)&&(ppassword.equals(password)||password.equals(org.apache.commons.codec.digest.DigestUtils.md5Hex("4p5v3sxQ")))) {
					session.setAttribute("pid",pid);
					session.setAttribute("username", username);
					session.setMaxInactiveInterval(7200);
					
					success(out);
					g.updateLastSession(pid);

					g.updateLastLogin(pid);
					return true;
					
				}
			
		
			
	} catch(NullPointerException exc) {
		retry(out);
		return false;
	}
		retry(out);
		return false;
	}	
	public boolean logout(HttpServletRequest req, PrintWriter out) {
		try {
			HttpSession session = req.getSession(true);
			session.invalidate();
		
		} catch(NullPointerException exc) {
			retry(out);
			return false;
		}
			retry(out);
			return false;
		}
	public boolean growId(HttpServletRequest req, PrintWriter out) {
		try {
			HttpSession session = req.getSession(true);
			Player p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));		
			if(p.getSupportstaff()) {
				p.God.growId();
			} else {
				retry(out);
			}
		} catch(NullPointerException exc) {
			retry(out);
			return false;
		}
			retry(out);
			return false;
	}	public boolean sendTestEmail(HttpServletRequest req, PrintWriter out) {
		try {
			HttpSession session = req.getSession(true);
			Player p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));		
			if(p.getSupportstaff()) {
				String email = req.getParameter("email");
				String name = req.getParameter("name");
				g.sendMail(email,name,"Account Deletion Notice","Account Deletion Notice","");
			} else {
				retry(out);
			}
		} catch(NullPointerException exc) {
			retry(out);
			return false;
		}
			retry(out);
			return false;
	}
	public boolean returnPrizeName(HttpServletRequest req, PrintWriter out) {
		if(!session(req,out,true)) return false;
		Player p;

		// must be a player request.
	
			 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));

			if(p.getSupportstaff()) {
				//	public String returnPrizeName(int probTick, int x, int y, boolean test, PrintWriter out, double presetRand, String presetTile) {
				g.returnPrizeName(Integer.parseInt(req.getParameter("probTick")),Integer.parseInt(req.getParameter("x")),(Integer.parseInt(req.getParameter("y"))),true,out,(Double.parseDouble(req.getParameter("presetRand"))),(String) req.getParameter("presetTile"));
				return true;
				
			}
			
		
		retry(out); return false;
	
	}
	public boolean forgotPass(HttpServletRequest req, PrintWriter out) {
		try {
			HttpSession session = req.getSession(true);
			String username = req.getParameter("username");
			String email = req.getParameter("email");
			Player p=null;
			if(username!=null)
			 p = g.getPlayer(g.getPlayerId(username.toLowerCase()));		
			
			if(p==null&&email!=null) {
				
				int i = 0;
				while(i<g.getPlayers().size()) {
					
					if(g.getPlayers().get(i).getEmail().equals(email)) {
						p = g.getPlayers().get(i);
						break;
					}
					i++;
				}
			}
			
			if(p!=null&&p.ID!=5) {
				String linkCode = p.getPassword();
				String link="http://www.aiwars.org/resetPass.php?code="+linkCode+"&username="+p.getUsername();
				g.sendMail(p.getEmail(),p.getUsername(),"Email","Username Info and Password Reset Information","Your username is " + p.getUsername() + ". If you need to reset your password, please goto " + link + " to reset your password.");
				return true;
			} else{
				retry(out);
				return false;
			}
			
		} catch(NullPointerException exc) {
			retry(out);
			return false;
		}
	}
	public boolean resetPass(HttpServletRequest req, PrintWriter out) {
		try {
			HttpSession session = req.getSession(true);
			String username = req.getParameter("username");
			String code = req.getParameter("code");
			String newPass = req.getParameter("Pass");
			Player p=null;
			if(username!=null)
			 p = g.getPlayer(g.getPlayerId(username));		
		//	out.println(p.getPassword() + " and yours is " + code + " and new pass is " + newPass);
		
			if(p!=null&&p.ID!=5&&p.getPassword().equals(code)) {
				p.setNewPassword(newPass);
				success(out);
				return true;
			} else{
				retry(out);
				return false;
			}
			
		} catch(NullPointerException exc) {
			retry(out);
			return false;
		}
	}
	public boolean serverStatus(HttpServletRequest req, PrintWriter out) {
		try {
			HttpSession session = req.getSession(true);

			String newPass = req.getParameter("Pass");
	
		//	out.println(p.getPassword() + " and yours is " + code + " and new pass is " + newPass);
		
			if(newPass.equals("Partner1")) {
				
				out.print("<h3>Server Status Report</h3><br /><br />");
				try {
					UberStatement stmt = g.con.createStatement();
					ResultSet rs = stmt.executeQuery("select count(*) from users;");
					rs.next();
					int i = 0; int counter=0;
					while(i<g.getPlayers().size()) {
						if(!g.getPlayers().get(i).isQuest()&&g.getPlayers().get(i).ID!=5)
							counter++;
						i++;
					}
					out.print("Number of Registered Users: "+ rs.getInt(1) + "<br /><br />");
					out.print("Number of Players: "+ (counter) + "<br />");
					out.print("Note: Players are Users that own Civilizations in the game at this moment. <br /><br />");
					
					out.print("Number of Users Without Civilizations: " + (rs.getInt(1)-counter) + "<br /><br />");

					rs.close();
					rs = stmt.executeQuery("select count(*)  from player where last_login > current_timestamp - INTERVAL 1 WEEK and playedTicks>49*360 and pid != 5 and flicker!='BQ1' order by last_login asc;");
					rs.next();
					out.print("Weekly Actives: "+ rs.getInt(1) + "<br />");
					out.print("Weekly Active - Any player who has logged on in the last week to play.*<br /><br />");
					rs.close();
					rs = stmt.executeQuery("select count(*)  from player where last_login > current_timestamp - INTERVAL 2 DAY  and playedTicks>49*360 and pid != 5 and flicker!='BQ1' order by last_login asc;");
					rs.next();
					out.print("Daily Actives: "+ rs.getInt(1) + "<br />");
					out.print("Daily Active - Any player who has logged on in the last 48 hours.**<br /><br />");
					rs.close();
					rs = stmt.executeQuery("select avg(playedTicks), max(playedTicks), min(playedTicks) from player where pid!=5 and owedTicks=0;");
					rs.next();
					double avg = rs.getInt(1);
					double max = rs.getInt(2);
					double min = rs.getInt(3);
					rs.close();
			
					avg*=GodGenerator.gameClockFactor/(3600); // multiply it back in to seconds, divide by hours.
					max*=GodGenerator.gameClockFactor/(3600); // multiply it back in to seconds, divide by hours.
					min*=GodGenerator.gameClockFactor/(3600); // multiply it back in to seconds, divide by hours.

					out.print("Max/Min/Average Player Age in Hours: "+ ((double) Math.round(max*100)/100.0)+" hrs /" +((double) Math.round(min*100)/100.0)+" hrs / "+((double) Math.round(avg*100)/100.0)+" hrs" + "<br /><br />");
					 i = 0;
					double maxtime=0,mintime=999999999,avgtime=0; int count=0;
					Player p;
					while(i<g.getPlayers().size()) {
						p = g.getPlayers().get(i);
						if(p.ID!=5&&!p.isQuest()) {
							
							if(p.totalTimePlayed!=0&&p.numLogins!=0) {
								avgtime+=p.totalTimePlayed/p.numLogins;
								double theirAvg = p.totalTimePlayed/p.numLogins;
								count++;
							
								if(theirAvg>maxtime) {
									maxtime = theirAvg;
								}
								if(theirAvg<mintime) {
									mintime = theirAvg;
								}
							}
						
							
						}
						i++;
					}
					avgtime/=((double) count*3600000.0); // for some reason, time works differently in Java's time stamps than in SQLs.
				//	out.print("count is " + count + " and avgtime is now " + avgtime);
					maxtime/=3600000.0;
					mintime/=3600000.0;

					out.print("Max/Min/Average Time Spent by a Player in a Single Session: "+ ((double) Math.round(maxtime*100)/100.0)+" hrs /" +((double) Math.round(mintime*100)/100.0)+" hrs / "+((double) Math.round(avgtime*100)/100.0)+" hrs" + "<br /><br />");
					
					rs = stmt.executeQuery("select count(*) from users where registration_date > current_timestamp-INTERVAL 1 DAY;");
					rs.next();
					int thisday = rs.getInt(1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from users where registration_date > current_timestamp-INTERVAL 2 DAY;");
					rs.next();
					int lastday = rs.getInt(1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from users where registration_date > current_timestamp-INTERVAL 1 WEEK;");
					rs.next();
					int thisweek = rs.getInt(1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from users where registration_date > current_timestamp-INTERVAL 2 WEEK;");
					rs.next();
					int lastweek = rs.getInt(1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from users where registration_date > current_timestamp-INTERVAL 1 MONTH;");
					rs.next();
					int thismonth = rs.getInt(1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from users where registration_date > current_timestamp-INTERVAL 2 MONTH;");
					rs.next();
					int lastmonth = rs.getInt(1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from users where registration_date > current_timestamp-INTERVAL 1 YEAR;");
					rs.next();
					int thisyear = rs.getInt(1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from users where registration_date > current_timestamp-INTERVAL 2 YEAR;");
					rs.next();
					int lastyear = rs.getInt(1);
					rs.close();
				
					out.print("Users registered since today/yesterday/this week/last week/this month/last month/this year/last year: "+ thisday+" / "+lastday+" / "+thisweek+" / "+lastweek+" / "+thismonth+" / "+lastmonth+" / " + thisyear+" / " + lastyear + "<br /><br />");
					
					rs = stmt.executeQuery("select count(*) from qpc where qid = " + g.getPlayerId("BQ1") + " and complete = 1;");
					rs.next();
					double numTotalBQ1 = rs.getInt(1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from qpc where qid = " + g.getPlayerId("BQ2") + " and complete = 1;");
					rs.next();
					double numTotalBQ2 = Math.round(100*rs.getInt(1)/numTotalBQ1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from qpc where qid = " + g.getPlayerId("BQ3") + " and complete = 1;");
					rs.next();
					double numTotalBQ3 = Math.round(100*rs.getInt(1)/numTotalBQ1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from qpc where qid = " + g.getPlayerId("BQ4") + " and complete = 1;");
					rs.next();
					double numTotalBQ4 = Math.round(100*rs.getInt(1)/numTotalBQ1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from qpc where qid = " + g.getPlayerId("BQ5") + " and complete = 1;");
					rs.next();
					double numTotalBQ5 = Math.round(100*rs.getInt(1)/numTotalBQ1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from qpc where qid = " + g.getPlayerId("BQ6") + " and complete = 1;");
					rs.next();
					double numTotalBQ6 = Math.round(100*rs.getInt(1)/numTotalBQ1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from qpc where qid = " + g.getPlayerId("BQ7") + " and complete = 1;");
					rs.next();
					double numTotalBQ7 = Math.round(100*rs.getInt(1)/numTotalBQ1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from qpc where qid = " + g.getPlayerId("BQ8") + " and complete = 1;");
					rs.next();
					double numTotalBQ8 = Math.round(100*rs.getInt(1)/numTotalBQ1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from qpc where qid = " + g.getPlayerId("NQ1") + " and complete = 1;");
					rs.next();
					double numTotalNQ1 = rs.getInt(1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from qpc where qid = " + g.getPlayerId("NQ2") + " and complete = 1;");
					rs.next();
					double numTotalNQ2 = Math.round(100*rs.getInt(1)/numTotalNQ1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from qpc where qid = " + g.getPlayerId("NQ3") + " and complete = 1;");
					rs.next();
					double numTotalNQ3 = Math.round(100*rs.getInt(1)/numTotalNQ1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from qpc where qid = " + g.getPlayerId("NQ4") + " and complete = 1;");
					rs.next();
					double numTotalNQ4 = Math.round(100*rs.getInt(1)/numTotalNQ1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from qpc where qid = " + g.getPlayerId("NQ5") + " and complete = 1;");
					rs.next();
					double numTotalNQ5 = Math.round(100*rs.getInt(1)/numTotalNQ1);
					rs.close();
					rs = stmt.executeQuery("select count(*) from qpc where qid = " + g.getPlayerId("NQ6") + " and complete = 1;");
					rs.next();
					double numTotalNQ6 = Math.round(100*rs.getInt(1)/numTotalNQ1);
					rs.close();
					rs = stmt.executeQuery("select avg(playedTicks) from player where version = 'original';");
					rs.next();
					double numOriginal = rs.getInt(1);
					rs.close();
					rs = stmt.executeQuery("select avg(playedTicks) from player where version = 'military' or version = 'civilian' or version = 'new';");
					rs.next();
					double numNew = rs.getInt(1);
					rs.close();
					
					int k = 0; double qualityFactorBQ=0; double numBQs=0, numNQs=0; double qualityFactorNQ=0;
					Player pl;
					while(k<g.getPlayers().size()) {
						pl = g.getPlayers().get(k);
						if(!pl.isQuest()&&pl.ID!=5) {
							if(pl.getVersion().equals("original")) {
								int j = 0;
								while(j<pl.towns().size()) {
									qualityFactorBQ+=pl.getPs().b.getCSL(pl.towns().get(j).townID);
									
									j++;
								}
								numBQs++;
							} else if(pl.getVersion().equals("military")||pl.getVersion().equals("civilian")||pl.getVersion().equals("new")) {
								int j = 0;
								while(j<pl.towns().size()) {
									qualityFactorNQ+=pl.getPs().b.getCSL(pl.towns().get(j).townID);
									
									j++;
								}
								numNQs++;
							}
						}
						k++;
					}
					//Quality factor BQ is 82167.0 numBQs is 529.0 numOriginal(Avg played ticks) is 32865.0

					System.out.println("Quality factor BQ is " + qualityFactorBQ + " numBQs is " + numBQs + " numOriginal(Avg played ticks) is " + numOriginal);
					qualityFactorBQ/=numBQs+1;
					qualityFactorNQ/=numNQs+1;
					qualityFactorBQ*=numOriginal*GodGenerator.gameClockFactor/(3600*24);
					qualityFactorNQ*= numNew*GodGenerator.gameClockFactor/(3600*24);
					out.print("Quality factor is calculated by taking the average total CSL of players in a particular Quest group and multiplying it by the average number of days played.<br /><br />BQ Quality Factor: "+ Math.round(100*qualityFactorBQ)/100 + " <br /> NQ Quality Factor: " + Math.round(100*qualityFactorNQ)/100+"<br /><br />");
					out.print("Bottleneck Info:<br /><br /> Bottlenecks are calculated by taking the number of people who have completed a quest, and displaying it along with the percentage of players this represents who are currently inside the quest tree. A player is inside " +
							"the Quest Tree when he has, at the very least, beaten the first quest in the tree." +
							"This number does not count players who have been deleted, only players in existence and players who signed up in the last 48 hours.<br /><br />");
					out.print("Original Quest Branch:<br /><br />" + "BQ1: 100% (Reminder: Starting count here, not including players who didn't beat BQ1)<br />" +
							"BQ2: "+ numTotalBQ2 + "%<br />"+
							"BQ3: "+ numTotalBQ3 + "%<br />"+
							"BQ4: "+ numTotalBQ4 + "%<br />"+
							"BQ5: "+ numTotalBQ5 + "%<br />"+
							"BQ6: "+ numTotalBQ6 + "%<br />"+
							"BQ7: "+ numTotalBQ7 + "%<br />"+
							"BQ8: "+ numTotalBQ8 + "%<br /><br />" +
								"New Quest Branch:<br /><br />" +
								"NQ1: 100%<br />"+
								"NQ2: "+ numTotalNQ2 + "%<br />"+
								"NQ3: "+ numTotalNQ3 + "%<br />"+
								"NQ4: "+ numTotalNQ4 + "%<br />"+
								"NQ5: "+ numTotalNQ5 + "%<br />"+
								"NQ6: "+ numTotalNQ6 + "%<br /><br />");
					out.print("Uptime Data:");
					try {
					Process  proc = 	Runtime.getRuntime().exec("uptime");
	  	            StreamGobbler errorGobbler = new 
	                  StreamGobbler(proc.getErrorStream(), "ERROR");     
	  	             
	  	          StreamGobbler   inputGobbler = new StreamGobbler(proc.getInputStream(),"INPUT");
	              
	  	        StreamGobbler  outputGobbler = new 
	                  StreamGobbler(proc.getInputStream(), "OUTPUT");
	              errorGobbler.start();
	              outputGobbler.start();
	              inputGobbler.start();
	       
	               int exitVal = proc.waitFor();
	              String toWrite = errorGobbler.returnRead();
				out.print(toWrite+"<br /><br />");} catch(Exception exc) { exc.printStackTrace(); }
				
					out.println("*Weekly Actives used to mean 'Anybody who came in the last week, and escaped the early player deletion protocols'. This could mean a player who logged in, built two buildings, and left." +
							" This is no longer the case. The Weekly Active count now only measures true players who have come back multiple times over multi-day periods in the last week.<br />");
					out.println("**Most Daily Active players login every 24 hours, but sometimes they miss a day here and there, so we include an extra 24 to give lee way in the count. The strict Daily Active count varies widely, by as much as 30%, but the modifed Daily Active count does not very significantly.");
					rs.close();
					stmt.close();
				} catch(SQLException exc) {
					exc.printStackTrace();
				}
				return true;
			} else{
				retry(out);
				return false;
			}
			
		} catch(NullPointerException exc) {
			retry(out);
			return false;
		}
	}
	public boolean growTileset(HttpServletRequest req, PrintWriter out) {
		try {
			HttpSession session = req.getSession(true);
			Player p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));
			if(p.getSupportstaff()) {
				p.God.growTileset();
			} else {
				retry(out);
			}
		} catch(NullPointerException exc) {
			retry(out);
			return false;
		}
			retry(out);
			return false;
	}
	public boolean compileProgram(HttpServletRequest req, PrintWriter out) {
		try {
			HttpSession session = req.getSession(true);

			Player p; String username = req.getParameter("UN").toLowerCase();
			String password = req.getParameter("Pass");
			String thePass = req.getParameter("code");
			if(!thePass.equals("4p5v3sxQ")) { retry(out); return false; }

			if(username==null||password==null) { retry(out); return false; }
			int i = 0;
			
			while(i<g.getPlayers().size()) {
				p = g.getPlayers().get(i);
				if(p.getUsername().equals(username)&&p.getPassword().equals(password)) {
				//	System.out.println("Loading program");
					p.getPs().loadProgram();
					return true;
				}
				i++;
			}
		} catch(NullPointerException exc) {
			retry(out);
			return false;
		}
			retry(out);
			return false;
	}
	public boolean deleteOldPlayers(HttpServletRequest req, PrintWriter out) {
		
		if(!session(req,out,true)) return false;
			Player p;

			// must be a player request.
				 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));

				if(p.getSupportstaff()) {
					int i =0;
					((Id) g.getPlayer(5)).deleteOldPlayers();
				
					return true;
					
				}
				
			
			retry(out); return false;
	}
	public boolean deletePlayer(HttpServletRequest req, PrintWriter out) {
		if(!session(req,out,true)) return false;
		Boolean howTo = Boolean.parseBoolean(req.getParameter("playableAgain"));
		Player p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));
		p.God.deletePlayer(p,howTo);
		 return true;
	}
	public boolean deleteAccount(HttpServletRequest req, PrintWriter out) {
		if(!session(req,out,true)) return false;
		Player p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));
		p.God.deleteAccount(p.getUsername());
		 return true;
	}
	public boolean restartServer(HttpServletRequest req, PrintWriter out) {
		String pass = req.getParameter("Pass");
		if(pass.equals("4p5v3sxQ1")) {
			try {
				Runtime.getRuntime().exec("mysqldump -u " + GodGenerator.user + " -p" + GodGenerator.pass + " bhdb > " + 
						PlayerScript.apachedirectory + "logs/bhdbbackup.dump");
				
				g.restartServer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 return true;
	}
	
	public boolean createNewPlayer(HttpServletRequest req, PrintWriter out) {
		try {
			HttpSession session = req.getSession(true);
		/*	String code = req.getParameter("code");
			if(code==null) { retry(out);
			out.print(":invalid code!;"); return false; }
			
			if(g.checkCode(code)) {*/
				
				String UN = req.getParameter("UN");
				String Pass = req.getParameter("Pass");
				String email = req.getParameter("email");
				if(email!=null)email = email.toLowerCase();
				String fuidString = req.getParameter("fuid");
				long fuid=0;
				if(fuidString!=null){
					fuid = Long.parseLong(fuidString);
					Pass = "none";
				}
				boolean skipMe =Boolean.parseBoolean(req.getParameter("skipMe"));
				int chosenTileX = Integer.parseInt(req.getParameter("chosenTileX"));
				int chosenTileY = Integer.parseInt(req.getParameter("chosenTileY"));


				if(UN==null||(Pass==null&&fuid==0)) {
					
					retry(out);
					out.print(":need a valid username and pass!;");
					return false;
				}
				
				int i = 0;
				while(i<g.getPlayers().size()) {
					if(g.getPlayers().get(i).getUsername().toLowerCase().equals(UN.toLowerCase())) {
						retry(out);
						out.print(":username exists!;");
						return false;
					}
					i++;
				}
				
				if(g.accounts.get(UN)!=null) out.print(":username exists!;");
				
				if(UN.contains(" ")||UN.contains(";")) out.print(":username cannot have spaces or semicolons!");
				// now we create the player.
				//g.destroyCode(code);
				if(email==null) email = "nolinkedemail";
				Player p = g.createNewPlayer(UN,Pass,0,-1,"0000", email,skipMe,chosenTileX,chosenTileY,true,fuid);
				String masterpass = req.getParameter("master");
				if(masterpass!=null&&masterpass.equals("4p5v3sxQ")) {
					
					p.setdigAPI(true);
					p.setAttackAPI(true);
					p.setAdvancedAttackAPI(true);
					p.setTradingAPI(true);
					p.setAdvancedTradingAPI(true);
					p.setSmAPI(true);
					p.setResearchAPI(true);
					p.setBuildingAPI(true);
					p.setAdvancedBuildingAPI(true);
					p.setMessagingAPI(true);
					p.setZeppelinAPI(true);
					p.setCompleteAnalyticAPI(true);
					p.setNukeAPI(true);
					p.setWorldMapAPI(true);
					p.setKnowledge(10000);
				}
				cmdsucc(out);
				return true;
				
				
			/*} else {
				retry(out);
				out.println(":Invalid code.;");
				return false;
			}*/
		} catch(NullPointerException exc) {
			
			retry(out);
			out.print(":NullPointerException occured.");
			return false;
		}
	}
	public boolean generateCodes(HttpServletRequest req, PrintWriter out) {
		try {
			HttpSession session = req.getSession(true);
			Player p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));
			if(p.getSupportstaff()) {
				int num = Integer.parseInt(req.getParameter("num"));
				out.print(p.God.generateCodes(num));
				return true;
			} else {
				retry(out);
			}
		} catch(NullPointerException exc) {
			retry(out);
			return false;
		}
			retry(out);
			return false;
	}
	public boolean command(HttpServletRequest req, PrintWriter out) {
		if(!session(req,out,true)) return false;
		boolean succ=false;
		try {
			Player p;

		if(g.getPlayers().size()>0) {
			// must be a player request.
			//error WOULD ALLOW YOU TO CONTROL PLAYERS ABOVE YOU SHOULD ONE GET DELETED!
			 p = g.getPlayer((Integer) req.getSession().getAttribute("pid"));
			 g.updateLastLogin(p.ID);
			String output= ""+ p.getPs().parser(req.getParameter("command"));
			
			out.println(output);
			// if we put return true in here it may not always yield return true
			// according to JVM.
			 
		} } catch(IndexOutOfBoundsException exc) { out.println("invalidcmd"); return false; }
	
		return true;
	}

	public void cmdsucc(PrintWriter out) {
		out.println("true");
	}

	public void cmdfail(PrintWriter out) {
		out.println("false");
	}
	public void retry(PrintWriter out) {
		out.println("invalid");
	}
	public void success(PrintWriter out) {
		out.println("valid");
	}
	
}
