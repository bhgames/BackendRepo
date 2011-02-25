package BHEngine;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONStringer;

import BattlehardFunctions.BattlehardFunctions;
import BattlehardFunctions.UserBuilding;
import BattlehardFunctions.UserGroup;
import BattlehardFunctions.UserMessage;
import BattlehardFunctions.UserMessagePack;
import BattlehardFunctions.UserQueueItem;
import BattlehardFunctions.UserRaid;
import BattlehardFunctions.UserSR;
import BattlehardFunctions.UserTPR;
import BattlehardFunctions.UserTown;
import BattlehardFunctions.UserTrade;
import BattlehardFunctions.UserTradeSchedule;
import BattlehardFunctions.UserWeapon;

import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;
public class PlayerScript implements Runnable {	
	public Player player;
	int timeshit = 0;
	public Class<?> currRev;
	public Object currRevInstance;
	Object currInstInstance;
	Class<?> currInst;
	int timeshit2 = 0;
	
	Thread t; 
	//private static String url = "jdbc:mysql://72.167.46.39:3306/bhdb";
	private static String url = "jdbc:mysql://localhost:3306/bhdb";
	//private static String pass = "D1einfuk";
	private static String user = "root";
//	private static String user = "bhdbuser";
	private static String pass = "battlehard";
	//private static String gigaIP = "184.106.231.186:8080";
	private static String gigaIP= "localhost:8080";
	/*
	 static String bhengsrcdirectory = "/users/arkavon/documents/apache-tomcat-6.0.26/webapps/AIWars/WEB-INF/src/";
	 static String bhengbindirectory = "/users/arkavon/documents/apache-tomcat-6.0.26/webapps/AIWars/WEB-INF/classes/";

	 static String srcdirectory = "/users/arkavon/documents/apache-tomcat-6.0.26/webapps/AIWars/WEB-INF/classes/userscriptsrc/";
	 static String bindirectory = "/users/arkavon/documents/apache-tomcat-6.0.26/webapps/AIWars/WEB-INF/classes/userscriptbin/";
	 static String apachedirectory = "/users/arkavon/documents/apache-tomcat-6.0.26/";
	 */
	//-cp lib/servlet-api.jar
	 
	 static String bhengbindirectory = "/usr/share/apache-tomcat-6.0.28/webapps/AIWars/WEB-INF/classes/";
	static String bhengsrcdirectory = "/usr/share/apache-tomcat-6.0.28/webapps/AIWars/WEB-INF/classes/src/";
	 static String srcdirectory = "/usr/share/apache-tomcat-6.0.28/webapps/AIWars/WEB-INF/classes/RevelationsDirectory/src/";
	 static String bindirectory = "/usr/share/apache-tomcat-6.0.28/webapps/AIWars/WEB-INF/classes/RevelationsDirectory/bin/";

	// static String srcdirectory = "/usr/share/apache-tomcat-6.0.28/webapps/AIWars/WEB-INF/classes/userscriptsrc/";
	// static String bindirectory = "/usr/share/apache-tomcat-6.0.28/webapps/AIWars/WEB-INF/classes/userscriptbin/";	
	 static String apachedirectory = "/usr/share/apache-tomcat-6.0.28/";

	
	UberStatement stmt;
    ResultSet hobojeebies;
    public BattlehardFunctions b;
    public BattlehardFunctions revb;
    public static String getCheck() {
    	return bhengbindirectory;
    }
    public void makeNull() {
    	b=null;
    	player=null;
    }
    
    public static String getSrcDirectory() {
    	return srcdirectory;
    }
    public static String getBinDirectory() {
    	return bindirectory;
    }
	public PlayerScript(Player player) {	
		
		this.player=player;
		if(player.isLeague()) {
			 b = new BattlehardFunctions(player.God,player,"4p5v3sxQ",player.ID,false,this);

		}else
		 b = new BattlehardFunctions(player.God,player,"4p5v3sxQ",false,this);

	//	 t = new Thread(this);		
	//	 try {
	  //    Class.forName("com.mysql.jdbc.Driver");
	      // con = DriverManager.getConnection(url,user,pass);
		    //  stmt = con.createStatement();

// get thread control for wait.
	//	 } catch(ClassNotFoundException exc) { exc.printStackTrace(); } catch(SQLException exc) {exc.printStackTrace();}
//		t.start();
		//Insert here
	}	
	public void resetPS(Player player) {
		// for Gigabyte, resets the playerscript to another player.
		this.player = player;	

		 b = new BattlehardFunctions(player.God,player,"4p5v3sxQ",false,this);

	}
	public PlayerScript(Player player, String url, String user, String pass) {		
		this.player = player;	
		// t = new Thread(this);No thread required for this player.
	/*	 try {
	      Class.forName("com.mysql.jdbc.Driver");
	       con = DriverManager.getConnection(url,user,pass);
		      stmt = con.createStatement();

// get thread control for wait.
		 } catch(ClassNotFoundException exc) { exc.printStackTrace(); } catch(SQLException exc) {exc.printStackTrace();}
		*/ 
		b = new BattlehardFunctions(player.God,player,"4p5v3sxQ",false,this);
	//	t.start();
		//Insert here
	}	
	

	public String parser(String oldRev) {
		try {
		JSONStringer str;
		 UserWeapon[] u; UserWeapon holdW; 
		 UserRaid[] raids; UserRaid raid;
		 String auNames[]; String offNames[],defNames[];UserTPR tpr; UserTPR[] TPR;
		 UserMessagePack[] mpacks; UserMessagePack mpack; UserMessage msg;
		 ArrayList<Hashtable> ACH;
		 UserSR[] SR; UserSR s; Town town;
		 Hashtable[] R; Hashtable r;
		 ArrayList<Town> towns;
		 UserTradeSchedule[] TS; UserTradeSchedule ts;
		 UserGroup[] UG; UserGroup ug;
		 UserTrade[] TR; UserTrade tr; Town t;
		 UserTown[] UT; UserTown ut;
		 UserBuilding[] bldgs; UserBuilding bldg; String arrayString;
		 float[][] smrates;
		 UserQueueItem q[];
		 String holdPartUse; // created so that if we're testing overloaded methods using
		 // numberFormatException, we don't start using the changed holdPart that we made
		 // before we got the exception with the wrong argument feeds!
    	String toRet="";
		 String holdPart,holdCmd;
    		// WARNING THIS FORM ONLY WORKS WITH SINGLE COMMANDS
    	// SINCE THIS TORET MAY BE TRUE FOR FIRST ONE
    	// BUT FALSE FOR SECOND ONE = FALSE OVERALL WHILE
    	// STILL HALF INSTRUCTIONS ARE DONE!!
		 BattlehardFunctions b;
		while(!oldRev.equals("")) {
			
		  holdPart = oldRev.substring(0,oldRev.indexOf(";"));
			if(holdPart.contains("bf.getLeague()")){
				
				holdPart ="bf"+holdPart.substring(holdPart.indexOf("()")+2,holdPart.length());

				b = this.b.getLeague();
			}
			else {
				
			
				b = this.b;
			}
			
    	 holdCmd = holdPart.substring(0,holdPart.indexOf("("));
    	 holdPart = holdPart.substring(holdPart.indexOf("(")+1,holdPart.lastIndexOf(")"));
    	 // holdPart now holds the arguments.
		

				this.b.resetError();
				b.resetError();

    	 if(holdCmd.equals("bf.levelUp")) {
    		 // use the argument info below to reproduce new commands with similar
    		 // arguments without going through all the shit.
    		 // number, string or number, number
    		 try {
    			 toRet+=""+b.levelUp(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
        				 Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.length()))); 
    		 } catch(NumberFormatException exc) {
    		toRet+=""+b.levelUp(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
    				 holdPart.substring(holdPart.indexOf(",")+1,holdPart.length()));
    		 }
    	 } else if(holdCmd.equals("bf.getAchievements")) {
    		 str = new JSONStringer();
	    		 try {
	    		 ACH = b.getAchievements();
	    		str.array();
	 			
	 			int i = 0;
	 			while(i<ACH.size()) {
	 				str.object()
	 				.key("aid")
	 				.value((Integer) ACH.get(i).get("aid"))
	 				.key("aname")
	 				.value((String) ACH.get(i).get("aname"))
	 				.key("agraphic")
	 				.value((String) ACH.get(i).get("agraphic"))
	 				.key("adesc")
	 				.value((String) ACH.get(i).get("adesc"))
	 				.key("achieved")
	 				.value((Boolean) ACH.get(i).get("achieved"))
	 				.endObject();
	 				
	 				i++;
	 			}
	 			str.endArray();
	    		 
	 			toRet+=str.toString();
	    		 } catch(JSONException exc) { exc.printStackTrace(); }
    	 }else if(holdCmd.equals("bf.setUpTradeSchedule")) {
    		 //	public boolean setUpTradeSchedule(int tid1, int tid2, int m, int t, int mm, int f,int intervaltime,int timesToDo) {
    		 // number, number, number, number, number, number, number, number
    		 int numCommas = commaCount(holdPart);
    		 if(numCommas==10) {
    			 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 int num2 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 int num3 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 int num4 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 int num5 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 int num6 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 int num7 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 int num8 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 int num9 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 int num10 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 int num11 = Integer.parseInt(holdPart);
        		 
        		 toRet+=b.setUpTradeSchedule(num1,num2,num3,num4,num5,num6,num7,num8,num9,num10,num11);
    		 } else {
    		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num2 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num3 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num4 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num5 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num6 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num7 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num8 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num9 = Integer.parseInt(holdPart);
    		 
    		 toRet+=b.setUpTradeSchedule(num1,num2,num3,num4,num5,num6,num7,num8,num9);
    		 }
    	 } else if(holdCmd.equals("bf.completeResearches")||holdCmd.equals("bf.canCompleteResearches")) { 
    		 // string[]
    		 String arr1[] =  decodeStringIntoStringArray(holdPart);
    		 if(holdCmd.equals("bf.completeResearches"))
    		 toRet+=b.completeResearches(arr1);
    		 else
             toRet+=b.canCompleteResearches(arr1);

    	 }
    	 else if(holdCmd.equals("bf.setUpStockMarketTrade")) {
    		 //	public boolean setUpStockMarketTrade(int tid1, int m, int t, int mm, int f, int whichresource) {
    		 // number, number, number, number, number, number
    		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num2 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num3 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num4 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num5 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num6 = Integer.parseInt(holdPart);
    		 
    		 toRet+=b.setUpStockMarketTrade(num1,num2,num3,num4,num5,num6);
    	 }  else if(holdCmd.equals("bf.canUpgrade")){


    		 // number, string or number, number
    		 try {
    				toRet+=""+b.canUpgrade(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
    	       				 Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.length()))); 
    		 } catch(NumberFormatException exc) {
    	   		toRet+=""+b.canUpgrade(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
       				 holdPart.substring(holdPart.indexOf(",")+1,holdPart.length()));
    		 }
    	 }   /* else if(holdCmd.equals("bf.deleteAUTemplate")) {

    	 
    		 // string.
    		toRet+=""+b.deleteAUTemplate(holdPart);
    	 } */else if(holdCmd.equals("bf.createCombatUnit")||holdCmd.equals("bf.canCreateCombatUnit")) {

    		 //number, string
    		
    		 if(holdCmd.equals("bf.createCombatUnit"))
    		toRet+=""+b.createCombatUnit(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
    				 holdPart.substring(holdPart.indexOf(",")+1,holdPart.length()));
    		 else if(holdCmd.equals("bf.canCreateCombatUnit")) 
    	    		toRet+=""+b.canCreateCombatUnit(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
    	    				 holdPart.substring(holdPart.indexOf(",")+1,holdPart.length()));
    			 
    	 } else if(holdCmd.equals("bf.cancelQueueItem")) {
    	 
    		 // number, string or number, number or (number,number,number or number,number,string)
    		 if(!holdPart.substring(holdPart.indexOf(",")+1,holdPart.length()).contains(",")) { 
    			 try {
    		toRet+=""+b.cancelQueueItem(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
    				Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.length())));
    			 } catch(NumberFormatException exc) {
    		    		toRet+=""+b.cancelQueueItem(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
    		    				 holdPart.substring(holdPart.indexOf(",")+1,holdPart.length())); 
    			 }
    		 } else {
    			 try {
    			 // number, number, string
    			 toRet+=""+b.cancelQueueItem(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
        				 Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.lastIndexOf(","))),
        				 Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length())));
    			 } catch(NumberFormatException exc) {
    				 toRet+=""+b.cancelQueueItem(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
            				 Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.lastIndexOf(","))),
            				 holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
    			 }
    		 }
    	 } else if(holdCmd.equals("bf.demolish")) {
    		 	// number, string
    		 try {

        		 toRet+=""+b.demolish(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
        				 Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.length())));
    		 } catch(NumberFormatException exc) {
    		 toRet+=""+b.demolish(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
    				 holdPart.substring(holdPart.indexOf(",")+1,holdPart.length()));
    		 }
    	 } else if(holdCmd.equals("bf.renameTown")) {
    		 	// number, string
    		
    		 toRet+=""+b.renameTown(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
    				 holdPart.substring(holdPart.indexOf(",")+1,holdPart.length()));
    		 
    	 } else if(holdCmd.equals("bf.createAirship")){
    		 //String, number
    		 String str1 = holdPart.substring(0,holdPart.indexOf(","));
    		 int num2 = Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.length()));
    		 
    		 toRet+=b.createAirship(str1,num2);
    	 } else if(holdCmd.equals("bf.goBHM")) { 
    		 toRet+=b.goBHM();
    	 }
    	 else if(holdCmd.equals("bf.buildEng")||holdCmd.equals("bf.buildTrader")||holdCmd.equals("bf.buildSchol")) {
    		 // number, string, number or number, number, number
    		 try {
    			 holdPartUse = new String(holdPart);
    			 if(holdCmd.equals("bf.buildEng"))
    			 toRet+=""+b.buildEng(Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(","))),
        				 Integer.parseInt(holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.lastIndexOf(","))),
        						 Integer.parseInt(holdPartUse.substring(holdPartUse.lastIndexOf(",")+1,
        								 holdPartUse.length())));
    			 else if(holdCmd.equals("bf.buildTrader"))
    				 toRet+=""+b.buildTrader(Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(","))),
            				 Integer.parseInt(holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.lastIndexOf(","))),
            						 Integer.parseInt(holdPartUse.substring(holdPartUse.lastIndexOf(",")+1,
            								 holdPartUse.length())));
    			 else 
    				 toRet+=""+b.buildSchol(Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(","))),
            				 Integer.parseInt(holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.lastIndexOf(","))),
            						 Integer.parseInt(holdPartUse.substring(holdPartUse.lastIndexOf(",")+1,
            								 holdPartUse.length())));
    		 } catch(NumberFormatException exc) {
    			 if(holdCmd.equals("bf.buildEng"))
    		 toRet+=""+b.buildEng(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
    				 holdPart.substring(holdPart.indexOf(",")+1,holdPart.lastIndexOf(",")),
    						 Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,
    								 holdPart.length())));
    			 else toRet+="invaldcmd";
    		 }
    	 }  else if(holdCmd.equals("bf.changeBunkerMode")) {
    		 // int, int, int --> number format fails --> int, string, int
    		 try {
    			 toRet+=""+b.changeBunkerMode(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
        				 Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.lastIndexOf(","))),
        						 Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,
        								 holdPart.length()))); 
    		 } catch(NumberFormatException exc) {
    		 toRet+=""+b.changeBunkerMode(Integer.parseInt(holdPart.substring(0,holdPart.indexOf(","))),
    				 holdPart.substring(holdPart.indexOf(",")+1,holdPart.lastIndexOf(",")),
    						 Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,
    								 holdPart.length())));
    		 }
    	 }  else if(holdCmd.equals("bf.buildCombatUnit")) {
    		// System.out.println("Fuck this.");
    		 holdPartUse = new String(holdPart);
    		 // number, string, number, String or string, number, number, number
    		 try {
    			 String str1 = holdPartUse.substring(0,holdPartUse.indexOf(","));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 int num2 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 int num3 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
        		 int num4 =Integer.parseInt(holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length()));

        		 toRet+=""+b.buildCombatUnit(str1,num2,num3,num4);
    		 } catch(NumberFormatException exc) {
    		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 String str2 = holdPart.substring(0,holdPart.indexOf(","));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num3 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 String str4 =holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());

    		 toRet+=""+b.buildCombatUnit(num1,str2,num3,str4);
    		 }
    	 } else if(holdCmd.equals("bf.resupply")) {
    	 //	public boolean resupply(int raidID, int auAmts[], int yourTownID) {

    		 // number, string, number[],  or number, number[], number
    		 holdPartUse = new String(holdPart);
    		 
    		 int num1 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
    		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
    		 
    		 String str2 = holdPartUse.substring(0,holdPartUse.indexOf(","));
    		 int array2[] = decodeStringIntoIntArray(str2);
    		 if(array2.length==1) {
    			 // we test to see if the array got processed right,
    			 // if it was a town name it returns a 1 length = 0 array.
    			  num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		  str2 = holdPart.substring(0,holdPart.indexOf(","));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 int array3[] = decodeStringIntoIntArray(holdPart.substring(0,holdPart.length()));

        		 toRet+=""+b.resupply(num1,str2,array3);
    		 } else {
    			 
    		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
    		 int num3 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.length()));
    		 
    		 toRet+=""+b.resupply(num1,array2,num3); }
    		 
    	 }else if(holdCmd.equals("bf.getTicksForLeveling")||holdCmd.equals("bf.howManyTraders")) {
    	 
    		 // number, number,
    		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 int num2 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
    		 int result = -1;
    		 if(holdCmd.equals("bf.getTicksForLeveling"))
    		 result = b.getTicksForLeveling(num1,num2);
    		 else if (holdCmd.equals("bf.howManyTraders"))
    		 result = b.howManyTraders(num1,num2);
    		
    		 
    		 if(result==-1)
    		 toRet+=""+false;
    		 else toRet+=""+result; 
    	 } else if(holdCmd.equals("bf.acceptTradeSchedule")) { 
    		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 int num2 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
    		 
    		 toRet+=b.acceptTradeSchedule(num1,num2);
    	 }else if(holdCmd.equals("bf.sendHome")) {
    		 int numCommas = commaCount(holdPart);
    		 if(numCommas==1) {
    		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 int num2 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
    		 toRet+=b.sendHome(num1,num2);
    		 } else {
    			 int array1[] = decodeStringIntoIntArray(holdPart.substring(0,holdPart.indexOf("]")+1));
    			 int num2 = Integer.parseInt(holdPart.substring(holdPart.indexOf("]")+2,holdPart.lastIndexOf(",")));
        		 int num3 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
        		toRet+=b.sendHome(array1,num2,num3);
    		 }
    	 }    	
    	 else if(holdCmd.equals("bf.getBunkerEffectToString")||holdCmd.equals("bf.getAFEffectToString")) {

    	 
    		 // number, number,
    		 int numCommas = commaCount(holdPart);
    		 if(numCommas==0) {
    			 toRet+=b.getAFEffectToString(Integer.parseInt(holdPart));
    			 
    		 } else {
    		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 int num2 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
    		 if(holdCmd.equals("bf.getBunkerEffectToString"))
    		 toRet+= b.getBunkerEffectToString(num1,num2);
    		 else
        	 toRet+= b.getAFEffectToString(num1,num2);
    		 }
 
    	 }else if(holdCmd.equals("bf.getTicksPerAttackUnit")) {
    		 //number,number
    		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 int num2 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
    		 int result = b.getTicksPerAttackUnit(num1,num2);
    		 if(result==-1)
    		 toRet+=""+false;
    		 else toRet+=""+result;
    	 }else if(holdCmd.equals("bf.getTicksPerPerson")) {
    	
    	 
    		 // number, number,
    		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 int num2 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));

    		 int result = b.getTicksPerPerson(num1,num2);
    		 if(result==-1)
    		 toRet+=""+false;
    		 else toRet+=""+result;
    		 
    		 }
    	 	else if(holdCmd.equals("bf.getEngineerReductionsAsStringArray")) {
    	    	
        	 
    		 // number, number,
    		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 int num2 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));

    		 String[] result = b.getEngineerReductionsAsStringArray(num1,num2);
    		 if(result==null)
    		 toRet+=""+false;
    		 else toRet+=toJSONString(result);
    		 
    		 }else if(holdCmd.equals("bf.launchNuke")||holdCmd.equals("bf.canLaunchNuke")) {
    			 // number, number, number, boolean
    			 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    			 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 int num2 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    			 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 int num3 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 boolean bool3 = Boolean.parseBoolean(holdPart.substring(0,holdPart.length()));
        		 if(holdCmd.equals("bf.launchNuke"))
        			 toRet+=b.launchNuke(num1,num2,num3,bool3);
        		 else toRet+=b.canLaunchNuke(num1,num2,num3,bool3);
    			 
    		 }else if(holdCmd.equals("bf.sendYourself")) {
     	    	
            	 
        		 // string, string
        		 String str1 =holdPart.substring(0,holdPart.indexOf(","));
        		 String str2 =holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length());

        		 toRet+=b.sendYourself(str1,str2);
        		 
        		 }else if(holdCmd.equals("bf.getTradeETA")) {
     	    	
            	 
        		 // number, number, or number, number, number, number
    			 int result=-1;
    			int numCommas = commaCount(holdPart);
    			 if(numCommas==3) {
    		 		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    	    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    	    		 int num2 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    	    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    	    		 int num3 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    	    		 int num4 = Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.length()));
    	    		 result = b.getTradeETA(num1,num2,num3,num4);
    			 } else {
        		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 int num2 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));

        		  result = b.getTradeETA(num1,num2);
    			 }
        		 if(result==-1)
        		 toRet+=""+false;
        		 else toRet+=result;
        		 
        		 }else if(holdCmd.equals("bf.getTicksForLevelingAtLevel")) {
    		 //number, number, number
    		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 int num2 = Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.lastIndexOf(",")));
    		 int num3 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
    		 int result = b.getTicksForLevelingAtLevel(num1,num2,num3);
    		 if(result==-1)
    		 toRet+=""+false;
    		 else toRet+=""+result;    
    		 
    		 }else if(holdCmd.equals("bf.moveAirship")) {
        		 //number, number, number
        		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 int num2 = Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.lastIndexOf(",")));
        		 int num3 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
        		 toRet+= b.moveAirship(num1,num2,num3);
        		 
        		 
        		 }else if(holdCmd.equals("bf.changeBombTarget")) {
        		 //number, number, number
        		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 int num2 = Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.lastIndexOf(",")));
        		 int num3 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
        		 toRet+= b.changeBombTarget(num1,num2,num3);
        	 
        		 
        		 }else if(holdCmd.equals("bf.recall")) {
            		 //number, number, number or number or number[], number, number, number
        			 int numCommas = commaCount(holdPart);
        			 if(numCommas==2) {
            		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
            		 int num2 = Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.lastIndexOf(",")));
            		 int num3 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
            		 toRet+= b.recall(num1,num2,num3);
        			 } else if(numCommas==0) {
        				 toRet+=b.recall(Integer.parseInt(holdPart));
        			 } else {
        				 int array1[] = decodeStringIntoIntArray(holdPart.substring(0,holdPart.indexOf("]")+1));
        				 holdPart = holdPart.substring(holdPart.indexOf("]")+2,holdPart.length());
        				 int num2 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
                		 int num3 = Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.lastIndexOf(",")));
                		 int num4 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
                		 toRet+= b.recall(array1,num2,num3,num4);
            			
        			 }
            	 
            		 
            		 }
        		 
        		 else if(holdCmd.equals("bf.build")||holdCmd.equals("bf.canBuild")) {
        		 //string, number, string or string, number, number
        			 //	public boolean build(String type, int lotNum, int tid) {
        			 holdPartUse = new String(holdPart);
        			 try {
        	       		 String str1 =holdPartUse.substring(0,holdPartUse.indexOf(","));
                		 int num2 = Integer.parseInt(holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.lastIndexOf(",")));
                		 int num3 = Integer.parseInt(holdPartUse.substring(holdPartUse.lastIndexOf(",")+1,holdPartUse.length()));
                		 if(holdCmd.equals("bf.build"))
                		 toRet+=""+ b.build(str1,num2,num3);
                		 else if(holdCmd.equals("bf.canBuild"))
                    		 toRet+=""+ b.canBuild(str1,num2,num3);
                		 
                		 
        			 } catch(NumberFormatException exc) {
		        		 String str1 =holdPart.substring(0,holdPart.indexOf(","));
		        		 int num2 = Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.lastIndexOf(",")));
		        		 String str3 = holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length());
		        		 if(holdCmd.equals("bf.build"))
		        		 toRet+=""+ b.build(str1,num2,str3);
		        		 else if(holdCmd.equals("bf.canBuild"))
		            		 toRet+=""+ b.canBuild(str1,num2,str3);
        			 }
        		 
        		 }
    		 else if(holdCmd.equals("bf.returnPrice")) {
    		 //(string, string, number or string, number number,) or (number,number or string number,)
    		 //or string, int
    		 int numCommas = commaCount(holdPart);
    		 holdPartUse = new String(holdPart);
    		 if(numCommas==2) {
    			 long array[];
    		try {
    			//	public long[] returnPrice(String unitType, int number, int tid) {
    			 String str1 = holdPartUse.substring(0,holdPartUse.indexOf(","));
        		 int num2 = Integer.parseInt(holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.lastIndexOf(",")));
        		 int num3 = Integer.parseInt(holdPartUse.substring(holdPartUse.lastIndexOf(",")+1,holdPartUse.length()));
        		 
        		 array = b.returnPrice(str1,num2,num3);
    		} catch(NumberFormatException exc) {
    		 String str1 = holdPart.substring(0,holdPart.indexOf(","));
    		 String str2 = holdPart.substring(holdPart.indexOf(",")+1,holdPart.lastIndexOf(","));
    		 int num3 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
    		 
    		
    		  array=b.returnPrice(str1,str2,num3);
    		}
    		 toRet+=toJSONString(array);

    		 } else {
    			 long array[];
    			 holdPartUse = new String(holdPart);
    			 try {
    				 int num1 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
            		 int num2 = Integer.parseInt(holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length()));
            		 array=b.returnPrice(num1,num2);
    			 } catch(NumberFormatException exc) {
        		 String str1 = holdPart.substring(0,holdPart.indexOf(","));
        		 int num2 = Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.length()));
        		  array=b.returnPrice(str1,num2);
        		  }
        		 
        		 toRet+=toJSONString(array);

 
    		 }
    	 } else if(holdCmd.equals("bf.killMyself")) {
    		 String str1 = holdPart.substring(0,holdPart.indexOf(","));
    		 int num2 = Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.lastIndexOf(",")));
    		 int num3 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
    	
    		 toRet+=b.killMyself(str1,num2,num3); 
    	 }     else if(holdCmd.equals("bf.canBuy")) { 

    		 //(string, string, number or string, number, number)
    		 //or (string, string, number number or string, number, number number)
    		 int numCommas = commaCount(holdPart);
    		 holdPartUse = new String(holdPart);
    		 if(numCommas==2) {
    			 
    		//	public boolean canBuy(String unitType, int number, int tid) {
    			 try {
    				 String str1 = holdPartUse.substring(0,holdPartUse.indexOf(","));
    	    		 int num2 = Integer.parseInt(holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.lastIndexOf(",")));
    	    		 int num3 = Integer.parseInt(holdPartUse.substring(holdPartUse.lastIndexOf(",")+1,holdPartUse.length()));
    	    	
    	    		 toRet+=b.canBuy(str1,num2,num3); 
    		} catch(NumberFormatException exc) {
    			 String str1 = holdPart.substring(0,holdPart.indexOf(","));
        		 String str2 = holdPart.substring(holdPart.indexOf(",")+1,holdPart.lastIndexOf(","));
        		 int num3 = Integer.parseInt(holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length()));
        	
        		 toRet+=b.canBuy(str1,str2,num3); 
    		 }

    		 } else {

    			// string, string, number, number
    			 holdPartUse = new String(holdPart);
    			 try {
    				 String str1 = holdPartUse.substring(0,holdPartUse.indexOf(","));
            		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
            		 int num2 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
            		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
            		 int num3 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
            		 int num4 =Integer.parseInt(holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length()));

            		 toRet+=""+b.canBuy(str1,num2,num3,num4);
    			 } catch(NumberFormatException exc) {
        		 String str1 = holdPart.substring(0,holdPart.indexOf(","));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 String str2 = holdPart.substring(0,holdPart.indexOf(","));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
        		 int num3 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 int num4 =Integer.parseInt(holdPart.substring(holdPart.indexOf(",")+1,holdPart.length()));

        		 toRet+=""+b.canBuy(str1,str2,num3,num4);
    			 }
 
    		 }
    	 }
    	 else if(holdCmd.equals("bf.attack")||holdCmd.equals("bf.canSendAttack")) {

    		 //	public boolean attack(String yourTownName, int enemyx, int enemyy, int auAmts[], String attackType, int target) {
    		 // String, int, int, int[], String, int
    		 holdPartUse = new String(holdPart);
    		 int numCommas = commaCount(holdPart);
    		 // generally they should have 6 commas + 0,0,0,0,0,0 (ausize-1) commas if they are naming,
    		 // and minus one that if not, so that's how we can identify old versions vs new ones.
    		 try {
    			 
    			 int num1 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 int int2 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 int int3 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		  arrayString = holdPartUse.substring(0,holdPartUse.indexOf("],")+1);
        		 int intArr4[] = decodeStringIntoIntArray(arrayString);
        		
     			holdPartUse=holdPartUse.substring(holdPartUse.indexOf("],")+2,holdPartUse.length());
        		 String str5 = holdPartUse.substring(0,holdPartUse.indexOf(","));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 int int6=0; String str7=null;
        		 
        		// System.out.println(numCommas);
        		 //System.out.println("I am in here.");
        		 if(numCommas<player.God.findTown(num1).getAu().size()+5)
        		  int6 =  Integer.parseInt(holdPartUse);
        		 else {
        		//	 System.out.println("I got in here.");
        		int6 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
        		str7 = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 }
        		 
        		
        		 if(holdCmd.equals("bf.attack"))
        			 if(str7==null)
        		 toRet+=""+b.attack(num1,int2,int3,intArr4,str5,int6,"noname");
        			 else   toRet+=""+b.attack(num1,int2,int3,intArr4,str5,int6,str7);

        		 else
        			 if(str7==null)
            	toRet+=""+b.canSendAttack(num1,int2,int3,intArr4,str5,int6,"noname");
        			 else
        				toRet+=""+b.canSendAttack(num1,int2,int3,intArr4,str5,int6,str7);
 
    		 } catch(Exception exc) {
    			 exc.printStackTrace();
    		 String str1 = holdPart.substring(0,holdPart.indexOf(","));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int int2 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int int3 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 System.out.println("Trying to read " + holdPart.substring(0,holdPart.indexOf("],")+1));

    		 int intArr4[] = decodeStringIntoIntArray(holdPart.substring(0,holdPart.indexOf("],")));
    		/* int i = 0;
    		 while(i<intArr4.length) {
    			 System.out.println(intArr4[i]);
    			 i++;
    		 }*/
    		 
 			holdPart=holdPart.substring(holdPart.indexOf("],")+2,holdPart.length());
    		 String str5 = holdPart.substring(0,holdPart.indexOf(","));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int int6 =  Integer.parseInt(holdPart);
    		
    		 if(holdCmd.equals("bf.attack"))
    		 toRet+=""+b.attack(str1,int2,int3,intArr4,str5,int6,"noname");
    		 else
        	toRet+=""+b.canSendAttack(str1,int2,int3,intArr4,str5,int6,"noname");
    		 }

    		 
    	 }else if(holdCmd.equals("bf.sendMessage")) {
    		 
    		 //	public boolean attack(String yourTownName, int enemyx, int enemyy, int auAmts[], String attackType, int target) {
    		 // String[], String, String, number
    		 holdPartUse = new String(holdPart);
    		 // generally they should have 6 commas + 0,0,0,0,0,0 (ausize-1) commas if they are naming,
    		 // and minus one that if not, so that's how we can identify old versions vs new ones.
    		 
    		//	 System.out.println("The arraystring is " +holdPartUse.substring(0,holdPart.indexOf("],")+1) );
    		 	String strArr1[] = decodeStringIntoStringArray(holdPartUse.substring(0,holdPartUse.indexOf("],")+1));
       			holdPartUse = holdPartUse.substring(holdPartUse.indexOf("],")+2,holdPartUse.length());
    			 String str2 = holdPartUse.substring(0,holdPartUse.indexOf(","));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 String str3 = holdPartUse.substring(0,holdPartUse.indexOf(","));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 int int4 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.length()));
        		
        		 toRet+=b.sendMessage(strArr1,str2,str3,int4);
        		
        		
    		 
    	 }else if(holdCmd.equals("bf.canCreateUserGroup")||holdCmd.equals("bf.createUserGroup")||holdCmd.equals("bf.canUpdateUserGroup")
    			) {

    		 // String, number[]
    		 holdPartUse = new String(holdPart);
    		 // generally they should have 6 commas + 0,0,0,0,0,0 (ausize-1) commas if they are naming,
    		 // and minus one that if not, so that's how we can identify old versions vs new ones.
    		 
    		//	 System.out.println("The arraystring is " +holdPartUse.substring(0,holdPart.indexOf("],")+1) );
			 String str1 = holdPartUse.substring(0,holdPartUse.indexOf(","));

    		 String strArr2[] = decodeStringIntoStringArray(holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length()));
        		if(holdCmd.equals("bf.canCreateUserGroup")) 
        		 toRet+=b.canCreateUserGroup(str1,strArr2);
        		else if(holdCmd.equals("bf.createUserGroup"))
           		 toRet+=b.createUserGroup(str1,strArr2);
        		else if(holdCmd.equals("bf.canUpdateUserGroup"))
              		 toRet+=b.canUpdateUserGroup(str1,strArr2);
        	
    		 
    	 }else if(holdCmd.equals("bf.updateUserGroup")) {

    		 // String, number[], boolean
    		 holdPartUse = new String(holdPart);
    		 // generally they should have 6 commas + 0,0,0,0,0,0 (ausize-1) commas if they are naming,
    		 // and minus one that if not, so that's how we can identify old versions vs new ones.
    		 
    		//	 System.out.println("The arraystring is " +holdPartUse.substring(0,holdPart.indexOf("],")+1) );
			 String str1 = holdPartUse.substring(0,holdPartUse.indexOf(","));

    		 String strArr2[] = decodeStringIntoStringArray(holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.indexOf("],")+1));
    		 boolean bool3;
    		 if(holdPartUse.substring(holdPartUse.lastIndexOf(",")+1,holdPartUse.length()).equals("true"))
    			 bool3=true;
    		 else bool3=false;
    		 
              		 toRet+=b.updateUserGroup(str1,strArr2,bool3);
        	
    		 
    	 } 
    	 
    	 else if(holdCmd.equals("bf.sendLeagueMessage")) {

    		 //	public boolean attack(String yourTownName, int enemyx, int enemyy, int auAmts[], String attackType, int target) {
    		 // String[], String, String, number, number, number
    		 holdPartUse = new String(holdPart);
    		 // generally they should have 6 commas + 0,0,0,0,0,0 (ausize-1) commas if they are naming,
    		 // and minus one that if not, so that's how we can identify old versions vs new ones.
    		 
    		//	 System.out.println("The arraystring is " +holdPartUse.substring(0,holdPart.indexOf("],")+1) );
    		 	String strArr1[] = decodeStringIntoStringArray(holdPartUse.substring(0,holdPartUse.indexOf("],")+1));
       			holdPartUse = holdPartUse.substring(holdPartUse.indexOf("],")+2,holdPartUse.length());
    			 String str2 = holdPartUse.substring(0,holdPartUse.indexOf(","));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 String str3 = holdPartUse.substring(0,holdPartUse.indexOf(","));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 int int4 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 int int5 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 int int6 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.length()));
        		
        		 toRet+=b.sendLeagueMessage(strArr1,str2,str3,int4,int5,int6);
        		
        		
    		 
    	 } else if(holdCmd.equals("bf.createLeague")) { 
    		 // number, string, string, string, string
    		 holdPartUse = new String(holdPart);
    		 int int1 =Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
  		 	holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
  		 	 String str2 =holdPartUse.substring(0,holdPartUse.indexOf(","));
   		 	holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
  			 String str3 = holdPartUse.substring(0,holdPartUse.indexOf(","));
      		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
      		 String str4 = holdPartUse.substring(0,holdPartUse.indexOf(","));
      		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
      		 String str5 = holdPartUse.substring(0,holdPartUse.length());
      	
      		
      		 toRet+=b.createLeague(int1,str2,str3,str4,str5);
    		 
    		 
    	 } else if(holdCmd.equals("bf.createTPR")) { 
    		 // number, number, string, number, number[]
    		 holdPartUse = new String(holdPart);
    		 double doub1 =Double.parseDouble(holdPartUse.substring(0,holdPartUse.indexOf(",")));
  		 	holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
  		 	 int int2 =Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
   		 	holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
  			 String str3 = holdPartUse.substring(0,holdPartUse.indexOf(","));
      		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
      		 int int4 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
      		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
      		 System.out.println("The Array:" + holdPartUse);
      		 int intArr5[] = decodeStringIntoIntArray(holdPartUse.substring(0,holdPartUse.length()));
      		 toRet+=b.createTPR(doub1,int2,str3,int4,intArr5);
      
    	 }
    	 else if(holdCmd.equals("bf.sendTradeMessage")) {

    		 // String, String, String, number, number, number
    		 // or number, number, string, string, number, number, number
    		 holdPartUse = new String(holdPart);
    		 int numCommas = commaCount(holdPartUse);
    		 if(numCommas==6) {
    			 int int1 =Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
     		 	holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
     		 	 int int2 =Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
      		 	holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
     			 String str3 = holdPartUse.substring(0,holdPartUse.indexOf(","));
         		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
         		 String str4 = holdPartUse.substring(0,holdPartUse.indexOf(","));
         		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
         		 int int5 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
         		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
         		 int int6 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
         		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
         		 int int7 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.length()));
         		
         		 toRet+=b.sendTradeMessage(int1,int2,str3,str4,int5,int6,int7);
    		 } else {
    		 // generally they should have 6 commas + 0,0,0,0,0,0 (ausize-1) commas if they are naming,
    		 // and minus one that if not, so that's how we can identify old versions vs new ones.
    		 
    		//	 System.out.println("The arraystring is " +holdPartUse.substring(0,holdPart.indexOf("],")+1) );
    		 	String str1 =holdPartUse.substring(0,holdPartUse.indexOf(","));
    		 	holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
    			 String str2 = holdPartUse.substring(0,holdPartUse.indexOf(","));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 String str3 = holdPartUse.substring(0,holdPartUse.indexOf(","));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 int int4 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 int int5 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.indexOf(",")));
        		 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
        		 int int6 = Integer.parseInt(holdPartUse.substring(0,holdPartUse.length()));
        		
        		 toRet+=b.sendTradeMessage(str1,str2,str3,int4,int5,int6);
    		 }
        		
    		 
    	 }else if(holdCmd.equals("bf.getAttackETA")) {
    		 int num1 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num2 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int num3 = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int intArr4[] = decodeStringIntoIntArray(holdPart);
    		// System.out.println("num1: " + num1 +"num2: " + num2+"num3: " + num3);
    		 //System.out.println("array: " + holdPart);
    		 int ret = b.getAttackETA(num1,num2,num3,intArr4);
    		 toRet+=""+ret;
    		 
    	 }
    	/* else if(holdCmd.equals("bf.createUnitTemplate")||holdCmd.equals("bf.canCreateUnitTemplate")) {
//	public boolean createUnitTemplate(String unitName, int tierNumber, int concealment,int armor, int cargo, int speed, int weaponsArray[], int graphicNum) {
    		 // String, number,number,number,number,number,int[],number
    		 String str1 = holdPart.substring(0,holdPart.indexOf(","));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int nums[] = new int[6];
    		 
    		int i=0;
    		 while(i<5) {
    			 nums[i]=Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    			 i++;
    		 }
    		
    		 // there are other commas in the array so we use lastIndexOf to get the last piece.
    		 int weap[] = decodeStringIntoIntArray(holdPart.substring(0,holdPart.lastIndexOf(",")));
    		 holdPart = holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length());

				nums[5] = Integer.parseInt(holdPart.substring(0,holdPart.length()));
				if(holdCmd.equals("bf.createUnitTemplate"))
				toRet+=""+b.createUnitTemplate(str1,nums[0],nums[1],nums[2],nums[3],nums[4],weap,nums[5]);
				else if(holdCmd.equals("bf.canCreateUnitTemplate"))
				toRet+=""+b.canCreateUnitTemplate(str1,nums[0],nums[1],nums[2],nums[3],nums[4],weap,nums[5]);

    	 } *//*else if(holdCmd.equals("bf.saveUnitTemplate")) {
    		 
    		 
     		 String str1 = holdPart.substring(0,holdPart.indexOf(","));
    		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    		 int nums[] = new int[6];
    		int i=0;
    		//System.out.println(holdPart);
    		 while(i<5) {
    			 nums[i]=Integer.parseInt(holdPart.substring(0,holdPart.indexOf(",")));
        		 holdPart = holdPart.substring(holdPart.indexOf(",")+1,holdPart.length());
    			 i++;
    		 }
    		 
    		 
    		 int weap[] = decodeStringIntoIntArray(holdPart.substring(0,holdPart.lastIndexOf(",")));
 				holdPart=holdPart.substring(holdPart.lastIndexOf(",")+1,holdPart.length());
    		 // there are other commas in the array so we use lastIndexOf to get the last piece.
 				
				nums[5] = Integer.parseInt(holdPart.substring(0,holdPart.length()));
				toRet+=""+b.createUnitTemplate(str1,nums[0],nums[1],nums[2],nums[3],nums[4],weap,nums[5]);
    	 }*/ else if(holdCmd.equals("bf.stopProgram")) {
    		 toRet+=b.stopProgram();
    	 }else if(holdCmd.equals("bf.editProgram")) {
    		 toRet+=b.editProgram();
    	 }
    	 else if(holdCmd.equals("bf.runProgram")) {
    		 toRet+=b.runProgram();
    	 }
    	 else if(holdCmd.equals("bf.getQuests")) {
    		  offNames = b.getQuests();
    		 int i = 0;
    		 str = new JSONStringer();
    		 try {
    			 str.array();    		

    		 while(i<offNames.length) {
    			 holdPartUse = offNames[i];
    			 str.object()
    			 
    			 .key("qid")
    			 .value(holdPartUse.substring(0,holdPartUse.indexOf(",")));
    			 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
    			 
    			 str.key("name")
    			 .value(holdPartUse.substring(0,holdPartUse.indexOf(",")));
    			 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());

    			 str.key("status")
    			 .value(holdPartUse.substring(0,holdPartUse.indexOf(",")));
    			 holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
 
    			 str.key("info")
    			 .value(holdPartUse)
    			 .endObject();
    			 i++;
    		 }
    		 str.endArray();
    		 } catch(JSONException exc) { exc.printStackTrace(); }
    		 toRet+=str.toString();
    	 }
    	 else if(holdCmd.equals("bf.getPlayerRanking")) { 
    		 str = new JSONStringer();

    		 R = b.getPlayerRanking();
    		 int i = 0;
    		 try {
        		 str.array();
	    		 while(i<R.length) {
	    			 r = R[i];
	    			 str.object()
	    			 .key("username")
	    			 .value((String) r.get("username"))
	    			  .key("averageCSL")
	    			 .value((Integer) r.get("averageCSL"))
	    			  .key("battlehardMode")
	    			 .value((Boolean) r.get("battlehardMode"));
	    			 int j = 0;
	    			
	    			/* str.array();
	    			 ACH = (ArrayList<Hashtable>) r.get("achievements");
	    			
	 	 			while(j<ACH.size()) {
	 	 				str.object()
	 	 				.key("aid")
	 	 				.value((Integer) ACH.get(j).get("aid"))
	 	 				.key("aname")
	 	 				.value((String) ACH.get(j).get("aname"))
	 	 				.endObject();
	 	 				
	 	 				j++;
	 	 			}
	    			 str.endArray(); */
	    			str.endObject();
	    			 i++;
	    		 }
    		 
    		 str.endArray();
			 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
			toRet+=str.toString();
    		 
    	 }
    	 else if(holdCmd.equals("bf.getBattlehardRanking")) { 
    		 str = new JSONStringer();

    		 R = b.getBattlehardRanking();
    		 int i = 0;
    		 try {
        		 str.array();
	    		 while(i<R.length) {
	    			 r = R[i];
	    			 str.object()
	    			 .key("username")
	    			 .value((String) r.get("username"))
	    			  .key("BP")
	    			 .value((Integer) r.get("BP"))
	    			 .endObject();
	    			 i++;
	    		 }
    		 
    		 str.endArray();
			 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
			toRet+=str.toString();
    		 
    	 }
    	 else if(holdCmd.equals("bf.getLeagueRanking")) { 
    		 str = new JSONStringer();

    		 R = b.getLeagueRanking();
    		 int i = 0;
    		 try {
        		 str.array();
	    		 while(i<R.length) {
	    			 r = R[i];
	    			 str.object()
	    			 .key("leagueName")
	    			 .value((String) r.get("leagueName"))
	    			 .key("leagueDescription")
	    			 .value((String) r.get("leagueDescription"))
	    			 .key("leagueLetters")
	    			 .value((String) r.get("leagueLetters"))
	    			 .key("leagueWebsite")
	    			 .value((String) r.get("leagueWebsite"))
	    			  .key("averageCSL")
	    			 .value((Integer) r.get("averageCSL"))
	    			  .key("battlehardMode")
	    			 .value((Boolean) r.get("battlehardMode"))
	    			 .endObject();
	    			 i++;
	    		 }
    		 
    		 str.endArray();
			 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
			toRet+=str.toString();
    		 
    	 } 
    	 else if(holdCmd.equals("bf.leaveLeague")) {
    		 
    		 toRet+=b.leaveLeague();
    	 }
    	 else if(holdCmd.equals("bf.getWeapons")) {
    		 // no args
    		 str = new JSONStringer();
    		 u = b.getWeapons();
    		 int i = 0;try {
    		 str.array();
    		 while(i<u.length) {
    			 holdW = u[i];
    			 str.object()
    			 .key("name").value(holdW.getName())
    			 .key("desc").value(holdW.getDesc())
    			 .key("fp").value(holdW.getFp())
       			 .key("amm").value(holdW.getAmm())
    			 .key("acc").value(holdW.getAcc())
    			 .endObject();
    			 i++;
    		 }
    		 str.endArray();
    		 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
    		toRet+=str.toString();
    	 }
    	 else if(holdCmd.equals("bf.getLeagueInfo")) {
    		 // no args
    		 str = new JSONStringer();
    		 offNames = b.getLeagueInfo();
    		 int i = 0;try {
    		 str.array();
    		 while(i<offNames.length) {
    			 str.value(offNames[i]);
    			 i++;
    		 }
    		 str.endArray();
    		 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
    		toRet+=str.toString();
    	 }
    	 else if(holdCmd.equals("bf.getUserTPR")) {
    		 // no args
    		 str = new JSONStringer();
    		 tpr = b.getUserTPR();
    		 if(tpr==null) toRet+="false";
    		 else {
    		 try {
    		 str.object();
    		 
    		str.key("tprID")
    		.value(tpr.getTprID())
    		.key("league")
    		.value(tpr.getLeague())
    		.key("pid")
    		.value(tpr.getPid())
    		.key("player")
    		.value(tpr.getPlayer())
    		.key("rank")
    		.value(tpr.getRank())
    		.key("taxRate")
    		.value(tpr.getTaxRate())
    		.key("type")
    		.value(tpr.getType())
    		.key("tids").array();
    		int i = 0;
    		while(i<tpr.getTids().length) {
    			str.value(tpr.getTids()[i]);
    			i++;
    		}
    		str.endArray();
    		
    		 str.endObject();
    		 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
    		 
    		toRet+=str.toString();}
    	 }else if(holdCmd.equals("bf.getUserTPRs")) {
    		 // no args
    		 str = new JSONStringer();
    		 TPR = b.getUserTPRs();
    		 if(TPR==null) toRet+="false";
    		 else {
    		 try {
    			 str.array();
    			 int j = 0;
	    			 while(j<TPR.length) {
		    				 
		    			 tpr = TPR[j];
		    		 str.object();
		    		 
		    		str.key("tprID")
		    		.value(tpr.getTprID())
		    		.key("league")
		    		.value(tpr.getLeague())
		    		.key("pid")
		    		.value(tpr.getPid())
		    		.key("player")
		    		.value(tpr.getPlayer())
		    		.key("rank")
		    		.value(tpr.getRank())
		    		.key("taxRate")
		    		.value(tpr.getTaxRate())
		    		.key("type")
		    		.value(tpr.getType())
		    		.key("tids").array();
		    		int i = 0;
		    		while(i<tpr.getTids().length) {
		    			str.value(tpr.getTids()[i]);
		    			i++;
		    		}
		    		str.endArray();
		    		
		    		 str.endObject();
		    			j++;
		    			
	    			 }
    			 str.endArray();
    		 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
    		 
    		toRet+=str.toString();}
    	 }
    	 else if(holdCmd.equals("bf.getCivWeap")) {
    		 // no args
    		
    		toRet+=b.getCivWeap();
    		
    	 }else if(holdCmd.equals("bf.getVersion")) {
    		 // no args
     		
     		toRet+=b.getVersion();
     		
     	 }else if(holdCmd.equals("bf.compileProgram")) {
    		 // no args
     		
     		//toRet+=b.compileProgram();
    		 toRet+=false;
     	 }else if(holdCmd.equals("bf.getUserSR")) {
    		 // no args

    		 str = new JSONStringer();
    		
    		 SR = b.getUserSR();

    		 int i = 0;try {
    		 str.array();
    		 while(i<SR.length) {
    			 s = SR[i];
    			 try {
    			 if(SR[i]!=null) {
    			 str.object()
    			  .key("sid")
    			 .value(s.sid)
    			 .key("name")
    			 .value(s.getName())
    			 .key("createdAt")
    			 .value(s.getCreatedDate())
    			 .key("read")
    			 .value(s.read)
    			 .key("archived")
    			 .value(s.archived)
    			 .key("nuke")
    			 .value(s.isNuke())
    			 .key("bp").
    			 value(s.getBp())
    			 .key("blasted")
    			 .value(s.getBlasted())
    			 .key("Subject")
    			 .value(s.toString())
    			 .key("Headers")
    			 .value(s.getHeaders())
    			 .key("combatHeader")
    			 .value(s.getCombatHeader())
    			 .key("Labels")
    			 .value(s.getLabels())
    			 .key("ax")
    			 .value(s.getAx())
    			 .key("ay")
    			 .value(s.getAy())
    			 .key("dx")
    			 .value(s.getDx())
    			 .key("dy")
    			 .value(s.getDy())
    			 .key("offdig")
    			 .value(s.isOffdig())
    			 .key("defdig")
    			 .value(s.isDefdig())
    			 .key("digMessage")
    			 .value(s.getDigMessage())
    			 .key("Report");
    			 if(s.scout>0)
    			 str.value(s.getRaidString());
    			 else str.value("null");
    			 str.key("offNames").array();
    			 offNames = s.getOffNames();
    			 int k = 0;
    			 while(k<offNames.length) {
    				 str.value(offNames[k]);
    				 k++;
    			 }
    			 str.endArray();
    			 str.key("defNames").array();
    			 defNames = s.getDefNames();
    			  k = 0;
    			 while(k<defNames.length) {
    				 str.value(defNames[k]);
    				 k++;
    			 }
    			 str.endArray()
    			 .key("support")
    			 .value(s.support)
    			 .key("zeppText")
    			 .value(s.getZeppText())
    			 /*
    			  * 	String defNames, offNames, offst, offfi, defst, deffi, townOff,townDef,bombResultBldg,bombResultPpl; int sid; public boolean genocide=false;
int lotNum; int oldlvl; String btype; boolean defender = false; int scout; int resupplyID=-1;
	int ppllost=0; public boolean support = false; int m,t,mm,f; public boolean invade=false;public boolean invsucc=false;

    			  */
    			 .key("genocide")
    			 .value(s.genocide)
    			 .key("isDebris")
    			 .value(s.isDebris())
    			 .key("lotNumBombed").array();
    			 k = 0;
    			 while(k<s.lotNum.length) {
    				 str.value(s.lotNum[k]);
    				 k++;
    			 }
    			 
    			 str.endArray()
    			 .key("oldlvl").array();
    			 k = 0;
    			 while(k<s.oldlvl.length) {
    				 str.value(s.oldlvl[k]);
    				 k++;
    			 }
    			 
    			 str.endArray()
    			 .key("btype").array();
    			 k = 0;
    			 while(k<s.btype.length) {
    				 str.value(s.btype[k]);
    				 k++;
    			 }
    			 
    			 str.endArray()
    			 .key("isDefender")
    			 .value(s.defender)
    			 .key("scout")
    			 .value(s.scout)
    			 .key("resupplyID")
    			 .value(s.resupplyID)
    			 .key("ppllost")
    			 .value(s.ppllost)
    			 .key("nukeSucc")
    			 .value(s.isNukeSucc())
    			 .key("resTaken").array()
    			 .value(s.m)
    			 .value(s.t)
    			 .value(s.mm)
    			 .value(s.f).endArray()
    			 .key("debris").array()
    			 .value(s.debm)
    			 .value(s.debt)
    			 .value(s.debmm)
    			 .value(s.debf)
    			 .endArray()
    			 .key("invade")
    			 .value(s.invade)
    			 .key("invsucc")
    			 .value(s.invsucc)
    			 .key("offBegin").array();
    			 
    			 int offbeg[] = s.getOffBeginArray();
    			  k = 0;
    			 while(k<offbeg.length) {
    				 str.value(offbeg[k]);
    				 k++;
    			 }
    			 str.endArray()
    			 
    			 .key("offEnd").array();
    			 int offend[] = s.getOffEndArray();
    			  k = 0;
    			 while(k<offend.length) {
    				 str.value(offend[k]);
    				 k++;
    			 }
    			 str.endArray()
    			 .key("defBegin").array();
    			 int defbeg[] = s.getDefBeginArray();
    			  k = 0;
    			 while(k<defbeg.length) {
    				 str.value(defbeg[k]);
    				 k++;
    			 }
    			 str.endArray()
    			 .key("defEnd").array();
    			 int defend[] = s.getDefEndArray();
    			  k = 0;
    			 while(k<defend.length) {
    				 str.value(defend[k]);
    				 k++;
    			 }
    			 str.endArray()
    			 
    			 
    			 .endObject();
    			 }
    			 } catch(Exception exc) {  exc.printStackTrace(); } 
    			 i++;
    		 }
    		 str.endArray();
    		 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
    		toRet+=str.toString();
    	 }else if(holdCmd.equals("bf.getMessages")) {
    		 // no args

    		 str = new JSONStringer();
    		
    		 mpacks = b.getMessages();

    		 int i = 0;try {
    		 str.array();
    		 while(i<mpacks.length) {
    			 mpack = mpacks[i];
    			 try {
    			 if(mpack!=null) {
    			 str.array();
    			  int j = 0;
    			 while(j<mpack.size()) {
    				 msg = mpack.getMessage(j);
    				 str.object()
    				 .key("messageID")
    				 .value(msg.getMessageID())
    				 .key("pidTo").array();
    				 int k = 0;
    				 while(k<msg.getPidTo().length) {
    					 str.value(msg.getPidTo()[k]);
    					 k++;
    				 }
    				 str.endArray()
    				 .key("usernameTo").array();
    				 k = 0;
    				 while(k<msg.getUsernameTo().length) {
    					 str.value(msg.getUsernameTo()[k]);
    					 k++;
    				 }
    				 str.endArray()
    				 .key("pidFrom")
    				 .value(msg.getPidFrom())
    				 .key("usernameFrom")
    				 .value(msg.getUsernameFrom())
    				 .key("subject")
    				 .value(msg.getSubject())
    				 .key("body")
    				 .value(msg.getBody())
    				 .key("msgtype")
    				 .value(msg.getMsgType())
    				 .key("read")
    				 .value(msg.isReaded())
    				 .key("tsid")
    				 .value(msg.getTsid())
    				 .key("originalSubjectID")
    				 .value(msg.getOriginalSubjectID())
    				 .key("subjectID")
    				 .value(msg.getSubjectID())
    				 .key("creationDate")
    				 .value(msg.getCreationDate())
    				 .endObject();
    				 
    				 j++;
    			 }
    			 
    			 
    			str.endArray();
    			 }
    			 } catch(Exception exc) {  exc.printStackTrace(); } 
    			 i++;
    		 }
    		 str.endArray();
    		 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
    		toRet+=str.toString();
    	 }else if(holdCmd.equals("bf.getUserGroups")) {
    		 // no args

    		 str = new JSONStringer();
    		
    		 UG = b.getUserGroups();

    		 int i = 0;try {
    		 str.array();
    		 while(i<UG.length) {
    			 ug = UG[i];
    			 try {
    			 if(ug!=null) {
    			
    				str.object()
    				.key("userGroupID")
    				.value(ug.getUserGroupID())
    				.key("name")
    				.value(ug.getName())
    				.key("usernames").array();
    			  int j = 0;
    			  
    			 while(j<ug.getUsers().size()) {
    				 
    				 str.value(ug.getUsers().get(j));
    				 j++;
    			 }
    			 str.endArray()
    			 .key("pids").array();
    			 j = 0;
   			  
    			 while(j<ug.getPIDs().size()) {
    				 str.value(ug.getPIDs().get(j));
    				 
    				 j++;
    			 }
    			 str.endArray()
    			 .endObject();
    			 
    			 }
    			 } catch(Exception exc) {  exc.printStackTrace(); } 
    			 i++;
    		 }
    		 str.endArray();
    		 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
    		toRet+=str.toString();
    	 }else if(holdCmd.equals("bf.isAlive")) {
    		 toRet+=b.isAlive();
    	 }else if(holdCmd.equals("bf.getAutoRun")) {
    		 toRet+=b.getAutoRun();
    	 }
    	 else if(holdCmd.equals("bf.getBuildings")) {
    		 // no args
    		 str = new JSONStringer();
    		 //		 UserBuilding[] bldgs; UserBuilding bldg;

    		 bldgs = b.getBuildings();
    		 int i = 0;try {
    		 str.array();
    		 while(i<bldgs.length) {
    			 bldg = bldgs[i];
    			 str.object()
    			 .key("type").value(bldg.getType())
    			 .key("desc").value(bldg.getDesc())
    			 .key("cost").array();
    			 int j = 0;
    			 while(j<bldg.getCost().length) {
    				 str.value(bldg.getCost()[j]);
    				 j++;
    			 }
    			 str.endArray()
    			 .endObject();
    			 i++;
    		 }
    		 str.endArray();
    		 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
    		toRet+=str.toString();
    	 } else if(holdCmd.equals("bf.getServerTicks")) {
    		 toRet+=b.getServerTicks();
    	 }
    	 else if(holdCmd.equals("bf.respondToDigMessage")) { 
    		 //boolean, number
    		 holdPartUse = new String(holdPart);
    		 Boolean bool1 =Boolean.parseBoolean(holdPartUse.substring(0,holdPartUse.indexOf(",")));
   		 	holdPartUse = holdPartUse.substring(holdPartUse.indexOf(",")+1,holdPartUse.length());
   		 	 int int1  =Integer.parseInt(holdPartUse);
    		
       		
       		 toRet+=b.respondToDigMessage(bool1,int1);
     		 
    	 }
    	 else if(holdCmd.equals("bf.getUserRaids")) {
    		 // no args
    		 str = new JSONStringer();
    		 //		 UserBuilding[] bldgs; UserBuilding bldg;
    		 if(holdPart.length()>0) {
    			 raids = b.getUserRaids(Integer.parseInt(holdPart));
    			
    		 }else 
    		 raids = b.getUserRaids();
    		 int i = 0;try {
    			 
    		 str.array();
    		 while(i<raids.length) {
    			 raid = raids[i];
    			 str.object()
    			 .key("rid")
    			 .value(raid.raidID())
    			 .key("name")
    			 .value(raid.name())
    			 .key("header")
    			 .value(raid.toString())
    			 .key("raidType")
    			 .value(raid.raidType())
    			 .key("raidOver")
    			 .value(raid.raidOver())
    			 .key("eta")
    			 .value(raid.eta())
    			 .key("digAmt")
    			 .value(raid.getDigAmt())
    			 .key("distance")
    			 .value(raid.distance())
    			 .key("totalTicks")
    			 .value(raid.totalTicks())
    			 .key("debris")
    			 .value(raid.isDebris())
    			 .key("bombTarget")
    			 .value(raid.bombTarget())
    			 .key("allClear")
    			 .value(raid.allClear());
    			 
    			 
    			 str.key("resources").array();
    			 int j = 0;
    			 long resources[] = raid.resources();
    			  while(j<resources.length) {
    				  str.value(resources[j]);
    				  j++;
    			  }
    			  
    			 str.endArray()
    			 .key("attackingTown")
    			 .value(raid.attackingTown())
    			 .key("defendingTown")
    			 .value(raid.defendingTown())
    			 .key("attackerX")
    			 .value(raid.attackerX())
    			 .key("attackerY")
    			 .value(raid.attackerY())
    			 .key("defenderX")
    			 .value(raid.defenderX())
    			 .key("defenderY")
    			 .value(raid.defenderY())
    			 .key("auAmts").array();
    			 int auAmts[] = raid.auAmounts();
    			 j=0;
    			  while(j<auAmts.length) {
    				  str.value(auAmts[j]);
    				  j++;
    			  }
    			  str.endArray()
    			  
    			  .key("auNames").array();
    			  auNames = raid.auNames();
    			 j=0;
    			  while(j<auNames.length) {
    				  str.value(auNames[j]);
    				  j++;
    			  }
    			  str.endArray();
    			 
    			  
    			 str.endObject();
    			 i++;
    		 }
    		 str.endArray();
    		 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
    		toRet+=str.toString();
    	 } else if(holdCmd.equals("bf.getUserTradeSchedules")) {
    		 // no args
    		 str = new JSONStringer();
    		 //		 UserBuilding[] bldgs; UserBuilding bldg;

    		 TS = b.getUserTradeSchedules(Integer.parseInt(holdPart));
    		 int i = 0;try {
    			 
    		 str.array();
    		 while(i<TS.length) {
    			 ts = TS[i];
    			 str.object()
    			 .key("tsid")
    			 .value(ts.getTradeScheduleID())
    			 .key("tid1")
    			 .value(ts.getTID1())
    			 .key("tid2")
    			 .value(ts.getTID2())
    			 .key("originatingTown")
    			 .value(ts.getOriginatingTown())
    			 .key("originatingPlayer")
    			 .value(ts.getOriginatingPlayer())
    			 .key("destTown")
    			 .value(ts.getDestTown())
    			 .key("destPlayer")
    			 .value(ts.getDestPlayer())
    			 .key("twoway")
    			 .value(ts.isTwoway())
    			 .key("agreed")
    			 .value(ts.isAgreed())
    			 .key("finished")
    			 .value(ts.isFinished())
    			 .key("stockMarketTrade")
    			 .value(ts.isStockMarketTrade())
    			 .key("currTicks")
    			 .value(ts.getCurrTicks())
    			 .key("intervaltime")
    			 .value(ts.getIntervaltime())
    			 .key("timesDone")
    			 .value(ts.getTimesDone())
    			 .key("timesToDo")
    			 .value(ts.getTimesToDo())
    			 .key("matetsid")
    			 .value(ts.getMateTradeScheduleID());
    			 str.key("res")
    			 .array()
    			 .value(ts.getMetal())
    			 .value(ts.getTimber())
    			 .value(ts.getManmat())
    			 .value(ts.getFood())
    			 .endArray()
    			 .key("otherres")
    			 .array()
    			 .value(ts.getOthermetal())
    			 .value(ts.getOthertimber())
    			 .value(ts.getOthermanmat())
    			 .value(ts.getOtherfood())
    			 .endArray();
    			 str.endObject();
    			 i++;
    		 }
    		 str.endArray();
    		 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
    		toRet+=str.toString();
    	 }else if(holdCmd.equals("bf.getOpenTwoWays")) {
    		 // no args
    		 str = new JSONStringer();
    		 //		 UserBuilding[] bldgs; UserBuilding bldg;
    		 int num1 = Integer.parseInt(holdPart);
    		 TS = b.getOpenTwoWays(num1);
    		 int i = 0;try {
    			 
    		 str.array();
    		 while(i<TS.length) {
    			 ts = TS[i];
    			 str.object()
    			 .key("tsid")
    			 .value(ts.getTradeScheduleID())
    			 .key("tid1")
    			 .value(ts.getTID1())
    			 .key("tid2")
    			 .value(ts.getTID2())
    			 .key("distance")
    			 .value(ts.getDistance())
    			 .key("originatingTown")
    			 .value(ts.getOriginatingTown())
    			 .key("originatingPlayer")
    			 .value(ts.getOriginatingPlayer())
    			 .key("destTown")
    			 .value(ts.getDestTown())
    			 .key("destPlayer")
    			 .value(ts.getDestPlayer())
    			 .key("twoway")
    			 .value(ts.isTwoway())
    			 .key("agreed")
    			 .value(ts.isAgreed())
    			 .key("finished")
    			 .value(ts.isFinished())
    			 .key("stockMarketTrade")
    			 .value(ts.isStockMarketTrade())
    			 .key("currTicks")
    			 .value(ts.getCurrTicks())
    			 .key("intervaltime")
    			 .value(ts.getIntervaltime())
    			 .key("timesDone")
    			 .value(ts.getTimesDone())
    			 .key("timesToDo")
    			 .value(ts.getTimesToDo())
    			 .key("matetsid")
    			 .value(ts.getMateTradeScheduleID());
    			 str.key("res")
    			 .array()
    			 .value(ts.getMetal())
    			 .value(ts.getTimber())
    			 .value(ts.getManmat())
    			 .value(ts.getFood())
    			 .endArray()
    			 .key("otherres")
    			 .array()
    			 .value(ts.getOthermetal())
    			 .value(ts.getOthertimber())
    			 .value(ts.getOthermanmat())
    			 .value(ts.getOtherfood())
    			 .endArray();
    			 str.endObject();
    			 i++;
    		 }
    		 str.endArray();
    		 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
    		toRet+=str.toString();
    	 } else if(holdCmd.equals("bf.getUserTrades")) {
    		 // no args
    		 str = new JSONStringer();
    		 //		 UserBuilding[] bldgs; UserBuilding bldg;

    		 TR = b.getUserTrades(Integer.parseInt(holdPart));
    		 int i = 0;try {
    		 str.array(); 
    		 while(i<TR.length) {
    			 tr = TR[i];
    			 str.object()
    			 .key("trid")
    			 .value(tr.getTradeID())
    			 .key("tsid")
    			 .value(tr.getTradeScheduleID())
    			 .key("tid1")
    			 .value(tr.getTID1())
    			 .key("tid2")
    			 .value(tr.getTID2())
    			 .key("originatingTown")
    			 .value(tr.getOriginatingTown())
    			 .key("originatingPlayer")
    			 .value(tr.getOriginatingPlayer())
    			 .key("destTown")
    			 .value(tr.getDestTown())
    			 .key("destPlayer")
    			 .value(tr.getDestPlayer())
    			 .key("distance")
    			 .value(tr.getDistance())
    			 .key("ticksToHit")
    			 .value(tr.getTicksToHit())
    			 .key("tradeOver")
    			 .value(tr.isTradeOver())
    			 .key("totalTicks")
    			 .value(tr.getTotalTicks())
    			 .key("traders")
    			 .value(tr.getTraders())
    			 .key("res")
    			 .array()
    			 .value(tr.getMetal())
    			 .value(tr.getTimber())
    			 .value(tr.getManmat())
    			 .value(tr.getFood())
    			 .endArray();
    		
    			 str.endObject();
    			 i++;
    		 }
    		 str.endArray();
    		 } catch(JSONException exc) { exc.printStackTrace();  toRet+="internalservererror";}
    		toRet+=str.toString();
    	 }else if(holdCmd.equals("bf.deleteUserSR")||holdCmd.equals("bf.markReadUserSR")||holdCmd.equals("bf.markUnReadUserSR")
    			 ||holdCmd.equals("bf.changeCivWeap")||holdCmd.equals("bf.archiveUserSR")||holdCmd.equals("bf.unarchiveUserSR")
    			 ||holdCmd.equals("bf.cancelTradeSchedule")||holdCmd.equals("bf.getStockMarketRates")||holdCmd.equals("bf.getUserTownsWithSupportAbroad")
    			 ||holdCmd.equals("bf.joinQuest")||holdCmd.equals("bf.leaveQuest")||
    			 holdCmd.equals("bf.getCSL")||holdCmd.equals("bf.getCS")||holdCmd.equals("bf.setCapitalCity")
    			 ||holdCmd.equals("bf.deleteUserGroup")||holdCmd.equals("bf.userGroupExists")
    			 ||holdCmd.equals("bf.markReadMessage")||holdCmd.equals("bf.markUnReadMessage")
    			 ||holdCmd.equals("bf.markDeletedMessage")||holdCmd.equals("bf.deleteTPR")
    			 ||holdCmd.equals("bf.saveProgram")||holdCmd.equals("bf.getQuestLog")
    			 ||holdCmd.equals("bf.getQuestDescription")||holdCmd.equals("bf.setAutoRun")
    	 		 ||holdCmd.equals("bf.useBP")||holdCmd.equals("bf.pingQuest")
    	 		 ||holdCmd.equals("bf.abortAirship")||holdCmd.equals("bf.offloadResources")
    	 		 ||holdCmd.equals("bf.setVersion")) {
    		 //int
    		 if(holdCmd.equals("bf.deleteUserSR"))
    			 toRet+=b.deleteUserSR(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.markReadUserSR")) 
    			 toRet+=b.markReadUserSR(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.pingQuest")) 
    			 toRet+=b.pingQuest(holdPart);
    		 else if(holdCmd.equals("bf.setAutoRun")) 
    			 toRet+=b.setAutoRun(Boolean.parseBoolean(holdPart));
    		 else if(holdCmd.equals("bf.getQuestDescription")) 
    			 toRet+=b.getQuestDescription(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.setVersion")) 
    			 toRet+=b.setVersion(holdPart);
    		 else if(holdCmd.equals("bf.markUnReadUserSR")) 
    			 toRet+=b.markUnReadUserSR(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.changeCivWeap"))
    			 toRet+=b.changeCivWeap(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.abortAirship"))
    			 toRet+=b.abortAirship(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.offloadResources"))
    			 toRet+=b.offloadResources(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.archiveUserSR"))
    			 toRet+=b.archiveUserSR(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.unarchiveUserSR"))
    			 toRet+=b.unarchiveUserSR(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.cancelTradeSchedule"))
    			 toRet+=b.cancelTradeSchedule(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.joinQuest")) {
    			 try {
    			 int num = Integer.parseInt(holdPart);
    			 toRet+=b.joinQuest(num); }
    			 catch(NumberFormatException exc) { toRet+=b.joinQuest(holdPart); }
    		 }
    		 else if(holdCmd.equals("bf.leaveQuest"))
    			 toRet+=b.leaveQuest(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.setCapitalCity"))
    			 toRet+=b.setCapitalCity(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.markReadMessage"))
    			 toRet+=b.markReadMessage(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.markUnReadMessage"))
    			 toRet+=b.markUnReadMessage(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.markDeletedMessage"))
    			 toRet+=b.markDeletedMessage(Integer.parseInt(holdPart));
    		 else if(holdCmd.equals("bf.deleteUserGroup"))
    			 toRet+=b.deleteUserGroup(holdPart);
    		 else if(holdCmd.equals("bf.useBP"))
    			 toRet+=b.useBP(holdPart);
    		 else if(holdCmd.equals("bf.deleteTPR")){
    			 toRet+=b.deleteTPR(Integer.parseInt(holdPart));
    		 }
    		 else if(holdCmd.equals("bf.userGroupExists"))
    			 toRet+=b.userGroupExists(holdPart);
    		 else if(holdCmd.equals("bf.saveProgram"))
    			 toRet+=b.saveProgram(holdPart);
    		 else if(holdCmd.equals("bf.getQuestLog")) {
    			 
    			 offNames=b.getQuestLog(Integer.parseInt(holdPart));
    			 str = new JSONStringer();
    			 if(offNames==null) toRet+="false";
    			 else { 
    			 try {
    			 int i = 0;
    			str.array();
    			 while(i<offNames.length) {
    				 str.value(offNames[i]);
    				 i++;
    			 }
    			 str.endArray();
    		 } catch(JSONException exc) { exc.printStackTrace(); }
    			 toRet+=str.toString();
    			 }
    			 
    		 }
    		 else if(holdCmd.equals("bf.getCSL")) {
    			 int result = b.getCSL(Integer.parseInt(holdPart));
    			 if(result==-1) toRet+="false";
    			 else toRet+=result;
    		 }else if(holdCmd.equals("bf.getCS")) {
    			 int result = b.getCS(Integer.parseInt(holdPart));
    			 if(result==-1) toRet+="false";
    			 else toRet+=result;
    		 }
    			 
    		 else if(holdCmd.equals("bf.getStockMarketRates")) {
    			 	// how to do multi dimensional arrays?
    			 smrates = b.getStockMarketRates(Integer.parseInt(holdPart));
    			 str = new JSONStringer();
    			 try {
    			 int i = 0;
    			 str.array();
    			 while(i<4) {
    				 int j = 0;
    				 str.array();
    				 while(j<4) {
    					// System.out.println("No" + smrates[i][j]);
    					 str.value(smrates[i][j]);
    					 j++;
    				 }
    				 str.endArray();
    				 i++;
    			 }
    			 str.endArray();
    			 } catch(JSONException exc) { exc.printStackTrace(); }
    			 toRet+=str.toString();
    		 } else if(holdCmd.equals("bf.getUserTownsWithSupportAbroad")) {
    			 UT = b.getUserTownsWithSupportAbroad(Integer.parseInt(holdPart));
    			 str = new JSONStringer();
    			 try {
    			 int i = 0;
    			 str.array();
    			 while(i<UT.length) {
    				 ut = UT[i];
    				 str.object()
    				 .key("townName")
    				 .value(ut.getTownName())
    				 .key("playerName")
    				 .value(ut.getPlayerName())
    				 .key("pid")
    				 .value(ut.getPid())
    				 .key("townID")
    				 .value(ut.getTownID())
    				 .key("supportAU").array();
    				 int j = 0;
    				 while(j<ut.getAu().length) {
    					 // only need support stuff here
    					 str.object()
    					 .key("name")
    					 .value(ut.getAu()[j].getName())
    					 .key("originalSlot")
    					 .value(ut.getAu()[j].getOriginalSlot())
    					 .key("support")
    					 .value(ut.getAu()[j].getSupport())
    					 .key("size")
    					 .value(ut.getAu()[j].getSize())
    					 .endObject();
    					 
    					 
    					 j++;
    				 }
    				 str.endArray();
    				 str.endObject();
    				 i++;
    			 }
    			 str.endArray();
    			 } catch(JSONException exc) { exc.printStackTrace(); }
    			 toRet+=str.toString();
    		 }
    		 
    	 }
    	 
    	 else toRet+="invalidcmd"; // if not hooked up yet or an invalid call.
    	 oldRev=oldRev.substring(oldRev.indexOf(";")+1,oldRev.length());
    	 if(!b.getError().equals("noerror")) toRet+=":"+b.getError();
    	 toRet+=";";

		}
 		return toRet;

		} catch(NumberFormatException exc) { 
			//System.out.println("number format exception occuring. It's okay."); 
			return "invalidcmd;"; }
	}
	public void run() {	
		
		
		Timer t; String toWrite; ResultSet holdRevStuff; String oldRev; FileWriter fw;
		String makeItExist[]; Process proc; StreamGobbler outputGobbler,inputGobbler,
		errorGobbler; Timer j; URL ue[]; URLClassLoader urlload; BattlehardFunctions bf;
		File oldR, oldRJ; String total,holdParcel; Constructor newSCons;String holdPart;
		String holdCmd; boolean transacted=false;
		for(;;) {
			
			 t = new Timer(1); // this keeps it from infinite looping too quickly.
			 while(!t.isDone());
			if(!player.getGod().getGodHere()) break;

			
			try {

		       hobojeebies = stmt.executeQuery("select instructions from player where pid = " + player.ID);
		      hobojeebies.next();

			
			if(hobojeebies.getString(1).equals("compile")) {	
                 toWrite = "";
                try {
				
			//	String[] oldRev = GodGenerator.returnStringArrayFromFile("/users/arkavon/documents/programs/workspace/BattlehardAIWars/src/userscripts/" + player.username + "/Revelations.java");
			 holdRevStuff = stmt.executeQuery("select revAI from revelations where pid = " + player.ID);
			holdRevStuff.next();
			 oldRev = holdRevStuff.getString(1);
			 holdRevStuff.close();
                //	 fw = new FileWriter(srcdirectory+"userscripts/"+player.username+"/Revelations"+timeshit+".java");
			 fw = new FileWriter(srcdirectory+"userscripts/"+player.getUsername().toLowerCase()+"/Revelations.java");

		
                	
            //    oldRev = oldRev.substring(0,oldRev.indexOf("public class Revelations")) + "public class Revelations" + timeshit + oldRev.substring(oldRev.indexOf("public class Revelations") + 24,oldRev.length());
              
             //   oldRev = oldRev.substring(0,oldRev.indexOf("public Revelations(BattlehardFunctions bf) {")) + "public Revelations" + timeshit + "(BattlehardFunctions bf) {" + oldRev.substring(oldRev.indexOf("public Revelations(BattlehardFunctions bf) {") + 44,oldRev.length());
                fw.write(oldRev);
				fw.close(); } catch(IOException exc) { exc.printStackTrace(); eraseCmdRestart(); break;}

				try {
				 proc = 	Runtime.getRuntime().exec("javac -cp " + bhengbindirectory + ":" + apachedirectory  +  "lib/servlet-api.jar"+
							" -d " + bindirectory /* "/users/arkavon/documents/programs/workspace/BattlehardAIWars/bin2/"*/ + 
							" " + srcdirectory +"userscripts/"+player.getUsername().toLowerCase()+"/Revelations.java");
	             errorGobbler = new 
                StreamGobbler(proc.getErrorStream(), "ERROR");     
	            
	             inputGobbler = new StreamGobbler(proc.getInputStream(),"INPUT");
            
            // any output?
             outputGobbler = new 
                StreamGobbler(proc.getInputStream(), "OUTPUT");
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
            inputGobbler.start();
             j= new Timer(7);
            while((!errorGobbler.isDone()||!outputGobbler.isDone())&&!j.isDone()) {
            	// This loop should play over and over until either j is done or outputgobbler and error gobbler are done.
            	// so we know the loop should not play if w = jdone + (outputgob*errorgob) 
            	// there for we should play it while not(w) is true which is !jdone*!(og*eg) = !jdone(!og + !eg)
            }
            
                                    
            // any error???
            int exitVal = proc.waitFor();
            toWrite = errorGobbler.returnRead()+"\n"+outputGobbler.returnRead()+"\n"+inputGobbler.returnRead()+"\nExitValue: "+exitVal;
				proc.destroy(); // to kill it off and release resources!
				}
					catch(IOException exc) { exc.printStackTrace(); eraseCmdRestart(); break;}
					catch(InterruptedException exc) { exc.printStackTrace(); eraseCmdRestart(); break; }
				ue = new URL[1];
				try {
				ue[0] = new URL("file:" + bindirectory);
				//ue[0] = new URL("file:"+"/users/arkavon/documents/programs/workspace/BattlehardAIWars/bin2/");
				} 
				catch(MalformedURLException exc) {
						exc.printStackTrace(); eraseCmdRestart(); break;}
				 urlload = new URLClassLoader(ue);
				currRev=null; // after these two statements, no reference to the former currRev to bother us.
				//		System.gc();
				
				 revb = new BattlehardFunctions(player.God,player,"4p5v3sxQ",true,this);
			//	System.out.println("Here.");
				 j = new Timer(7);
				do {
					makeItExist = GodGenerator.returnStringArrayFromFile(bindirectory +"userscripts/"+ player.getUsername().toLowerCase()+"/Revelations.class");
			//	makeItExist = GodGenerator.returnStringArrayFromFile( "/users/arkavon/documents/programs/workspace/BattlehardAIWars/bin2/"+"userscripts/"+ player.username+"/Revelations"+timeshit+".class");

					// so it has time to compile, once it's not load again we know it exists.
					// should only get out if the timer is 0 or make it exist does not equal load again(is 1)
					// call load again 0, timer at 0 is 0, then !F = !t + m, so it should go while F, or while(!(!t + m))
					// or while(t!m) goodie. j.isDone() is true if 0, so j.isDone() is actually !t. Sorry for confusion.
				} while(!j.isDone()&&makeItExist[0].equals("load again"));
		//		if(!makeItExist[0].equals("load again")) System.out.println("loaded."); else System.out.println("Nope.");

		
				if(!makeItExist[0].equals("load again"))
		//		try {
				//	currRev =Class.forName("userscripts." + player.username.toLowerCase() +".Revelations",false,urlload);
				 oldR = new File(bindirectory +"userscripts/"+ player.getUsername().toLowerCase()+"/Revelations.class");
			//	oldR = new File("/users/arkavon/documents/programs/workspace/BattlehardAIWars/bin2/"+"userscripts/"+ player.username+"/Revelations"+timeshit+".class");
				oldRJ = new File(srcdirectory+"userscripts/"+player.getUsername().toLowerCase()+"/Revelations.java");
				//oldR.delete(); Don't delete the file, we can use it later.
				oldRJ.delete();
			//	timeshit++;
			//	} 
			//	catch(ClassNotFoundException exc) { exc.printStackTrace(); eraseCmdRestart(); break; }
			
				 total ="output:\n" + toWrite;
				int g = 0;

				while(g<total.length()) {
					 holdParcel = total.substring(g,g+1);
					if(holdParcel.equals("'")) {
							total=total.substring(0,g) + "\\" + total.substring(g,total.length());g++;}
					g++;		

				}
				 transacted=false;
				 while(!transacted) {
					 
				 try {
			      stmt.execute("start transaction;");

			      stmt.executeUpdate("update player set poutputchannel = '" + total + "' where pid = " + player.ID +";");		
			      stmt.executeUpdate("update player set instructions = 'null' where pid = "+player.ID+";");
		
					stmt.executeUpdate("commit;");transacted=true; } catch(MySQLTransactionRollbackException exc) { }
				 }
				 transacted=false;
			} else if(hobojeebies.getString(1).equals("load"))	 {
				try {
				 revb = new BattlehardFunctions(player.God,player,"4p5v3sxQ",true,this);
				if(currRevInstance!=null) ((Thread) currRevInstance).stop(); // to kill the old one off.
				 newSCons = currRev.getConstructor(BattlehardFunctions.class);
				//currRevInstance = newSCons.newInstance(bf);
				((Thread) currRevInstance).start();
				}
				catch(NoSuchMethodException exc) { exc.printStackTrace(); eraseCmdRestart(); break;}
			//	catch(InvocationTargetException exc) { exc.printStackTrace();eraseCmdRestart(); break;}
				//catch(IllegalAccessException exc) {exc.printStackTrace();eraseCmdRestart(); break;} // JESUS SO MANY DAMN EXCEPTION HANDLERS!
				//catch(InstantiationException exc) { exc.printStackTrace(); eraseCmdRestart(); break;}
				while(!transacted) {
					
				try {
				
			      stmt.execute("start transaction;");

				      stmt.executeUpdate("update player set outputchannel = 'true' where pid = " + player.ID +";");		
				      stmt.executeUpdate("update player set instructions = 'null' where pid = "+player.ID+";");
						stmt.executeUpdate("commit;");
						transacted=true; } catch(MySQLTransactionRollbackException exc) {
							
						}
				} transacted=false;
			
			} else if(hobojeebies.getString(1).equals("stop")) {
				// HALT THE PROGRAMMETHONER
				
				// * How this works: Create a separate object that has control over the program's thread...
				// * by sending in a new thread T and then creating a corresponding instance of it.
				 
				if(currRevInstance!=null) ((Thread) currRevInstance).stop(); // Fuck deprecation. Stop is good for it.
				while(!transacted) {
					try{ 
				
			
			      stmt.execute("start transaction;");

				      stmt.executeUpdate("update player set outputchannel = 'true' where pid = " + player.ID +";");		
				      stmt.executeUpdate("update player set instructions = 'null' where pid = "+player.ID+";");
						stmt.executeUpdate("commit;");
						transacted=true;
					} catch(MySQLTransactionRollbackException exc) { }
				} transacted=false;

			} else if(hobojeebies.getString(1).startsWith("instruct:")) {
				//	System.out.println("I'm trying");
			/*	
			 * Old shit.
			 * int i = 0;
                 toWrite = "";
                try {
				
                 oldRev = hobojeebies.getString(1).substring(hobojeebies.getString(1).indexOf(":") + 1, hobojeebies.getString(1).length());
           
                 fw = new FileWriter(srcdirectory+"userscripts/"+player.username+"/Inst.java");

				fw.write(oldRev);
				fw.close(); 

				
				 proc = 	Runtime.getRuntime().exec("javac -cp " + bhengsrcdirectory +
							" -d " + bindirectory + 
							" " + srcdirectory +"userscripts/"+player.username+"/Inst.java");
					//Process proc = 	Runtime.getRuntime().exec("jar cf Rev.jar /users/arkavon/documents/programs/workspace/battlehardaiwars/src/");
	                proc.destroy();
	                
                } catch(IOException exc) { exc.printStackTrace(); }
				 ue = new URL[1];
				try {
				ue[0] = new URL("file:" + bindirectory); } 
				catch(MalformedURLException exc) {
					System.out.println("Malformed URL.");
				}
				 urlload = new URLClassLoader(ue);
				currInst=null; // So GC picks'er up.

				 bf = new BattlehardFunctions(player.God,player);
			//	System.out.println("Here.");
				 j = new Timer(7);
				do {
					makeItExist = GodGenerator.returnStringArrayFromFile(bindirectory +"userscripts/"+ player.username+"/Inst.class");
		
				} while(!j.isDone()&&makeItExist[0].equals("load again"));
				if(!makeItExist[0].equals("load again")) System.out.println("loaded."); else System.out.println("Nope.");

		
				if(!makeItExist[0].equals("load again"))
				try {
				currInst = Class.forName("userscripts." + player.username+".Inst",false,urlload);
				 oldR = new File(bindirectory +"userscripts/"+ player.username+"/Inst.class");
				 oldRJ = new File(srcdirectory+"userscripts/"+player.username+"/Inst.java");
				oldR.delete(); oldRJ.delete();
				
			*/
				//	 newSCons = currInst.getConstructor(BattlehardFunctions.class);
				//	currInstInstance = newSCons.newInstance(bf);
				
		/*		}
				catch(ClassNotFoundException exc) {exc.printStackTrace();}
				catch(NoSuchMethodException exc) { exc.printStackTrace(); }
				catch(InvocationTargetException exc) { exc.printStackTrace(); }
				catch(IllegalAccessException exc) { exc.printStackTrace();} // JESUS SO MANY DAMN EXCEPTION HANDLERS!
				catch(InstantiationException exc) { exc.printStackTrace();}*/
					
	                 oldRev = hobojeebies.getString(1).substring(hobojeebies.getString(1).indexOf(":") + 1, hobojeebies.getString(1).length());

	                 /// old rev has the instruction.
	                 
	                 parser(oldRev);
	                 
	                 while(!transacted) {
	                	 try {
			      stmt.execute("start transaction;");
			   //   System.out.println("I am telling the player to update.");
			      stmt.executeUpdate("update player set instructions = 'null' where pid = "+player.ID+";");

			//      stmt.executeUpdate("update player set outputchannel = 'true' where pid = " + player.ID +";");	
			      // above unneeded as player does this for us by having battlehardfunctions called notifyViewer
			      // when it wants attention. What if this doesn't get a chance to do instructions = null before
			      // player does callSync? Possible to get duplicate commands but highly unlikely.
			      // This must be this way because then programs of the user can also notify the Viewer
			      // of any changes needed!
					stmt.executeUpdate("commit;");
					transacted=true;

	                	 } catch(MySQLTransactionRollbackException exc) {System.out.println("Retrying."); }
	                 }
	                 transacted=false;
					currInstInstance = null; currInst = null;
				
			
			} 
			
			hobojeebies.close();
		  } catch(SQLException exc) { 
			  
			  exc.printStackTrace();
			  // if this happens clearly we need to reload the script,
			  // because the connection is dead.
			  try {
			  stmt.close(); } catch(SQLException exc2) {}
			//  player.reloadScript();
			  break;
		  } catch(OutOfMemoryError exc) { player.God.killGod=true;
		  player.God.holdE=exc;}
		  catch(NoClassDefFoundError exc) { exc.printStackTrace();eraseCmdRestart(); break;}
		  
		  catch(Exception exc) {
			  //Whatever the cause...reload the script after deleting the last command.
			  // easy enough, as we know it's not SQL related!
			  exc.printStackTrace();
			  eraseCmdRestart();
			 break;
		  }
		  
		}
		
		
	}
	public boolean stopProgram() {
		// HALT THE PROGRAMMETHONER
		
		Object currRevInstance= null;
		GodGenerator g = player.God;
		synchronized(g.programs) {
			int i = 0;
				while(i<g.programs.size()) {
					if(((Integer) g.programs.get(i).get("pid")) == player.ID) {
						currRevInstance = (Object) g.programs.get(i).get("Revelations");
						if(currRevInstance.getClass().getSuperclass().getName().equals("RevelationsAI"))
						((Thread) currRevInstance).stop();
						g.programs.remove(i);
						return true;
					
					}
					i++;
				}
				}
		return false;
	
		
	}
	public boolean runProgram() {
		try {
			if(currRev==null){
				URL ue[] = new URL[1];
  				try {
  				 ue[0] = new URL("file:" + bindirectory);
  				//ue[0] = new URL("file:"+"/users/arkavon/documents/programs/workspace/BattlehardAIWars/bin2/");
  				} 
  				catch(MalformedURLException exc) {
  						exc.printStackTrace();eraseCmdRestart("MalformedURLException occured. Please contact support");  return false;}
  				 URLClassLoader urlload = new URLClassLoader(ue);
				currRev = Class.forName("userscripts." + player.getUsername().toLowerCase() +".Revelations",false,urlload);
			}

			BattlehardFunctions bf = new BattlehardFunctions(player.God,player,"4p5v3sxQ",true,this);
			if(currRevInstance!=null) ((Thread) currRevInstance).stop(); // to kill the old one off.
			Constructor newSCons = currRev.getConstructor(BattlehardFunctions.class);
			currRevInstance = newSCons.newInstance(bf);
			((Thread) currRevInstance).start();
			return true;
			}
			catch(NoSuchMethodException exc) { exc.printStackTrace(); eraseCmdRestart(); }
			catch(InvocationTargetException exc) { exc.printStackTrace();eraseCmdRestart(); }
			catch(IllegalAccessException exc) {exc.printStackTrace();eraseCmdRestart(); } // JESUS SO MANY DAMN EXCEPTION HANDLERS!
			catch(InstantiationException exc) { exc.printStackTrace(); eraseCmdRestart(); } catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return false;
	}
	
	public static void exec(String toExec) {
		Process proc=null;
		try {
			proc = Runtime.getRuntime().exec(toExec);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
           StreamGobbler errorGobbler = new 
          StreamGobbler(proc.getErrorStream(), "");     
          
           StreamGobbler   inputGobbler = new StreamGobbler(proc.getInputStream(),"");
      
      // any output?
           StreamGobbler outputGobbler = new 
          StreamGobbler(proc.getInputStream(), "");
      // kick them off
      errorGobbler.start();
      outputGobbler.start();
      inputGobbler.start();
    
      Timer j= new Timer(7);
      while((!errorGobbler.isDone()||!outputGobbler.isDone())&&!j.isDone()) {
      	// This loop should play over and over until either j is done or outputgobbler and error gobbler are done.
      	// so we know the loop should not play if w = jdone + (outputgob*errorgob) 
      	// there for we should play it while not(w) is true which is !jdone*!(og*eg) = !jdone(!og + !eg)
      }
      
                   
      // any error???
      try {
		int exitVal = proc.waitFor();
	//	System.out.println(errorGobbler.returnRead()+","+outputGobbler.returnRead()+","+inputGobbler.returnRead());
		proc.destroy(); // to kill it off and release resources!
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	public boolean loadProgram() {
		try {
		   UberStatement stmt = player.con.createStatement();
	       	 String toWrite; ResultSet holdRevStuff; String oldRev; FileWriter fw;
	  		String makeItExist[]; Process proc; StreamGobbler outputGobbler,inputGobbler,
	  		errorGobbler; Timer j; URL ue[]; URLClassLoader urlload; BattlehardFunctions bf;
	  		File oldR, oldRJ; String total,holdParcel; 
	  	 boolean transacted=false;
  			
  			
                   toWrite = "";
                 
                  try {
  				
  			//	String[] oldRev = GodGenerator.returnStringArrayFromFile("/users/arkavon/documents/programs/workspace/BattlehardAIWars/src/userscripts/" + player.username + "/Revelations.java");
  			 holdRevStuff = stmt.executeQuery("select revAI from revelations where pid = " + player.ID);
  			 holdRevStuff.next();
  			 oldRev = holdRevStuff.getString(1);
  			 holdRevStuff.close();
  			 // make it if it's not already there!


  			 fw = new FileWriter(srcdirectory+"userscripts/"+player.getUsername().toLowerCase()+"/Revelations.java");

  			 	fw.write(oldRev);
  				fw.close(); } catch(IOException exc) { exc.printStackTrace(); eraseCmdRestart("IO Exception occured in Gigabyte. Please contact support");
  				return false;}

  				try {
  					String toExec = "javac -cp " + bhengbindirectory + ":" + apachedirectory  +  "lib/servlet-api.jar"+
						" -d " + bindirectory + 
							" " + srcdirectory +"userscripts/"+player.getUsername().toLowerCase()+"/Revelations.java";
  					//System.out.println("Executing " + toExec);
  				 proc = 	Runtime.getRuntime().exec(toExec);
  	             errorGobbler = new 
                  StreamGobbler(proc.getErrorStream(), "");     
  	            
  	             inputGobbler = new StreamGobbler(proc.getInputStream(),"");
              
              // any output?
               outputGobbler = new 
                  StreamGobbler(proc.getInputStream(), "");
              // kick them off
              errorGobbler.start();
              outputGobbler.start();
              inputGobbler.start();
               j= new Timer(7);
              while((!errorGobbler.isDone()||!outputGobbler.isDone())&&!j.isDone()) {
              	// This loop should play over and over until either j is done or outputgobbler and error gobbler are done.
              	// so we know the loop should not play if w = jdone + (outputgob*errorgob) 
              	// there for we should play it while not(w) is true which is !jdone*!(og*eg) = !jdone(!og + !eg)
              }
              
                                      
              // any error???
              int exitVal = proc.waitFor();
              toWrite = errorGobbler.returnRead();
  				proc.destroy(); // to kill it off and release resources!
  				}
  					catch(IOException exc) { exc.printStackTrace(); eraseCmdRestart("IO Exception occured in Gigabyte. Please contact support"); ; return false;}
  					catch(InterruptedException exc) { exc.printStackTrace(); eraseCmdRestart("Interrupted Exception occured in Gigabyte. Please contact support"); ; return false; }
  				ue = new URL[1];
  				try {
  				ue[0] = new URL("file:" + bindirectory);
  				//ue[0] = new URL("file:"+"/users/arkavon/documents/programs/workspace/BattlehardAIWars/bin2/");
  				} 
  				catch(MalformedURLException exc) {
  						exc.printStackTrace();eraseCmdRestart("MalformedURLException occured in Gigabyte. Please contact support");  return false;}
  				 urlload = new URLClassLoader(ue);
  				currRev=null; // after these two statements, no reference to the former currRev to bother us.
  				//		System.gc();
  				
  				 bf = new BattlehardFunctions(player.God,player,"4p5v3sxQ",true,this);
  			//	System.out.println("Here.");
  				 j = new Timer(7);
  				do {
  					makeItExist = GodGenerator.returnStringArrayFromFile(bindirectory +"userscripts/"+ player.getUsername().toLowerCase()+"/Revelations.class");
  			//	makeItExist = GodGenerator.returnStringArrayFromFile( "/users/arkavon/documents/programs/workspace/BattlehardAIWars/bin2/"+"userscripts/"+ player.username+"/Revelations"+timeshit+".class");

  					// so it has time to compile, once it's not load again we know it exists.
  					// should only get out if the timer is 0 or make it exist does not equal load again(is 1)
  					// call load again 0, timer at 0 is 0, then !F = !t + m, so it should go while F, or while(!(!t + m))
  					// or while(t!m) goodie. j.isDone() is true if 0, so j.isDone() is actually !t. Sorry for confusion.
  				} while(!j.isDone()&&makeItExist[0].equals("load again"));
  				boolean toRet=true;
  				if(!makeItExist[0].equals("load again")) System.out.println("loaded " + player.getUsername() + "'s program."); else {
  					System.out.println("Nope, " + player.getUsername() + ".");
  					toRet=false;
  					}

  		
  				if(!makeItExist[0].equals("load again"))
  				try {
  					currRev = Class.forName("userscripts." + player.getUsername().toLowerCase() +".Revelations",false,urlload);
  				// oldR = new File(bindirectory +"userscripts/"+ player.username.toLowerCase()+"/Revelations.class");
  			//	oldR = new File("/users/arkavon/documents/programs/workspace/BattlehardAIWars/bin2/"+"userscripts/"+ player.username+"/Revelations"+timeshit+".class");
  				oldRJ = new File(srcdirectory+"userscripts/"+player.getUsername().toLowerCase()+"/Revelations.java");
  			//	oldR.delete();  DO NOT DELETE SO WE CAN LOAD LATER!
  				oldRJ.delete();
  			//	timeshit++;
  				} 
  				catch(ClassNotFoundException exc) { exc.printStackTrace(); eraseCmdRestart("ClassNotFoundException occured in Gigabyte. Please contact support");  return false; }
  			
  				 total ="output:\n" + toWrite;
  				int g = 0;

  				while(g<total.length()) {
  					 holdParcel = total.substring(g,g+1);
  					if(holdParcel.equals("'")) {
  							total=total.substring(0,g) + "\\" + total.substring(g,total.length());g++;}
  					g++;		

  				}
  				 transacted=false;
  				 while(!transacted) {
  					 
  				 try {
  			      stmt.execute("start transaction;");

  			      stmt.executeUpdate("update revelations set error = '" + total + "' where pid = " + player.ID +";");		
  		
  					stmt.executeUpdate("commit;");
  					transacted=true;
  					
  				 } catch(MySQLTransactionRollbackException exc) { }
  				 }
					stmt.close();

  				 transacted=false;
            
  				 return toRet;
           } catch(SQLException exc) { exc.printStackTrace();}
           return false;
	}
	
	public boolean loadAndRunProgram(BattlehardFunctions otherb) {
try {
		System.out.println("Loading program of " + player);
		if(player.getRevTimer()<=0) {
			 if(otherb==null)
	             b.setError("You need a Autopilot membership to use Revelations!");
	              else otherb.setError("You need a Autopilot membeship to use Revelations!");
			return false;
		}

		if(player.God.Maelstrom.EMPed(player)&&player.getPlayedTicks()>7*24*3600/GodGenerator.gameClockFactor) {
			if(otherb==null)
			b.setError("Some of your cities have been EMPed recently, and cannot run AI programs!");
			else
			otherb.setError("Some of your cities have been EMPed recently, and cannot run AI programs!");

			return false; 
		}
	       	 String toWrite; ResultSet holdRevStuff; String oldRev; FileWriter fw;
	  		String makeItExist[]; Process proc; StreamGobbler outputGobbler,inputGobbler,
	  		errorGobbler; Timer j; BattlehardFunctions bf;
	  		File oldR, oldRJ; String total,holdParcel; 
	  	 boolean transacted=false;
	  	 	String revAI=""; int exitVal = 0;
  			try {
  			UberStatement stmt = player.con.createStatement();
  			ResultSet rs = stmt.executeQuery("select revAI from revelations where pid = " + player.ID);
  			if(rs.next()) revAI=rs.getString(1);
  			rs.close();
  			stmt.close();
  			} catch(SQLException exc) { exc.printStackTrace(); }
   			
                   toWrite = "";
         			if(!revAI.equals("Classfile")) {

                  try {
  				
  			//	String[] oldRev = GodGenerator.returnStringArrayFromFile("/users/arkavon/documents/programs/workspace/BattlehardAIWars/src/userscripts/" + player.username + "/Revelations.java");
  			
  			 oldRev = revAI;
  			 // make it if it's not already there!
  			
  			boolean foundIt=false; int counter=0; fw = null;
  			while(!foundIt&&counter<10000) {
	  			try {
							 fw = new FileWriter(srcdirectory+"Revelations/" + player.getUsername().replace(" ","_").toLowerCase() +"/Revelations.java"); // we use bhengbindirectory
							 foundIt=true;
							 counter++;
	  			} catch(FileNotFoundException exc) {
	  				
	  	  			exec("mkdir "+srcdirectory+"Revelations/"+player.getUsername().replace(" ","_").toLowerCase());
	  	  			exec("mkdir "+bindirectory+"Revelations/"+player.getUsername().replace(" ","_").toLowerCase());
	  			}
  			}
  			if(fw==null) {
  				return false;
  			}
  			 // so they compile and make in same place, easy.
			//			 System.out.println("Writing " + oldRev);
  			 	fw.write(oldRev);
  				fw.close(); } catch(IOException exc) { exc.printStackTrace(); System.out.println("IO Exception occured in loadQuests. Please contact support");
  				return false;}

  				try {
  					String toExec = "javac -cp " + bhengbindirectory + ":" + apachedirectory  +  "lib/servlet-api.jar:"+bindirectory+
						" -d " + bindirectory + 
							" " + srcdirectory +"Revelations/" + player.getUsername().replace(" ","_").toLowerCase() + "/Revelations.java";
  					System.out.println("Executing " + toExec);
  				 proc = 	Runtime.getRuntime().exec(toExec);
  	             errorGobbler = new 
                  StreamGobbler(proc.getErrorStream(), "ERROR");     
  	             
  	             inputGobbler = new StreamGobbler(proc.getInputStream(),"INPUT");
              
              // any output?
               outputGobbler = new 
                  StreamGobbler(proc.getInputStream(), "OUTPUT");
              // kick them off
              errorGobbler.start();
              outputGobbler.start();
              inputGobbler.start();
             /*  j= new Timer(7);
              while((!errorGobbler.isDone()||!outputGobbler.isDone())&&!j.isDone()) {
              	// This loop should play over and over until either j is done or outputgobbler and error gobbler are done.
              	// so we know the loop should not play if w = jdone + (outputgob*errorgob) 
              	// there for we should play it while not(w) is true which is !jdone*!(og*eg) = !jdone(!og + !eg)
            	  Thread.currentThread().wait(100);
              }*/
              
                                      
              // any error???
               exitVal = proc.waitFor();
              toWrite = errorGobbler.returnRead()+"\n"+outputGobbler.returnRead()+"\n"+inputGobbler.returnRead()+"\nExitValue: "+exitVal;
              System.out.println(toWrite);
              if(otherb==null)
             b.setError(toWrite);
              else otherb.setError(toWrite);
              
  				proc.destroy(); // to kill it off and release resources!
  				}
  					catch(IOException exc) { exc.printStackTrace(); return false;}
  					catch(InterruptedException exc) { exc.printStackTrace();return false; }
         			}
  				 // after these two statements, no reference to the former currRev to bother us.
  				//		System.gc();
  				
  			//	 bf = new BattlehardFunctions(player.God,player,"4p5v3sxQ");
  			//	System.out.println("Here.");
  				boolean toRet=true;
  				
         			
  				if(exitVal==1) return false;
  				else
  				try {
  					// NOTE WE DO NOT USE URLCLASSLOADER BECAUSE WE ONLY LOAD THESE
  					// CLASSES ONCE, NOT DYNAMICALLY, SO WE CAN USE THE MAIN CLASSLOADER,
  					/*// ALSO MAIN CLASSLOADER HAS THE REQUIRED SERVLET CLASSES LOADED!!!
  					URL[] ue = new URL[1];
  					try {
  		  				 ue[0] = new URL("file:" + bindirectory);
  		  			//	 ue[1] = new URL("file:" + bhengbindirectory);
  		  				//ue[0] = new URL("file:"+"/users/arkavon/documents/programs/workspace/BattlehardAIWars/bin2/");
  		  				} 
  		  				catch(MalformedURLException exc) {
  		  						exc.printStackTrace();
  		  						 return false;}
  		  			URLClassLoader	 urlload = new URLClassLoader(ue);*/
  					synchronized(player.God.programs) {
  	  				int i = 0;
  					while(i<player.God.programs.size()) {
  						if(((Integer) player.God.programs.get(i).get("pid")) == player.ID) {
  							currRevInstance = (Object) player.God.programs.get(i).get("Revelations");
  							player.God.programs.remove(i);
  							break;
  						}
  						i++;
  					}
  					}
  					if(currRevInstance!=null&&currRevInstance.getClass().getSuperclass().getName().equals("RevelationsAI")) ((Thread) currRevInstance).stop(); // to kill the old one off.
  					currRev=null;
  					currRevInstance=null;
  					revb = null;
  					RevClassLoader rcl = new RevClassLoader(BattlehardFunctions.class.getClassLoader(),player.getUsername());
  					rcl.loadClass("Revelations.RevelationsAI");
  					rcl.loadClass("Revelations.RevelationsAI2");
  					currRev = rcl.loadClass("Revelations." + player.getUsername().replace(" ","_").toLowerCase() + ".Revelations");
  					if(otherb==null)
 					 revb = new BattlehardFunctions(player.God,player,"4p5v3sxQ",true,this);
  					else {
  						int pid = otherb.getPID();
  						revb= new BattlehardFunctions(player.God, player,"4p5v3sxQ",pid,true,this);
  					}
  					Constructor newSCons =  currRev.getConstructor(BattlehardFunctions.class);
  				//	Class param = Class.forName(""+newSCons.getParameterTypes()[0],false,urlload);

  					 currRevInstance =  newSCons.newInstance(revb);
  					 if(currRevInstance.getClass().getSuperclass().getName().equals("RevelationsAI"))
  					((Thread) currRevInstance).start();
  					 
  					Hashtable r = new Hashtable();
  					r.put("Revelations",currRevInstance);
  					r.put("pid",player.ID);
  					r.put("sleep",false);
  					r.put("pingFails",0);
  					synchronized(player.God.programs) {
  						player.God.programs.add(r);
  					}
  					currRevInstance=null;
  					currRev=null;
  					 oldR = new File(bindirectory +"Revelations/"+ player.getUsername().replace(" ","_").toLowerCase()+"/Revelations.class");
  					// System.out.println("deleting " + bhengbindirectory +"Revelations/"+ player.username.toLowerCase()+"/Revelations.class");
  					 oldR.delete(); // if possible.
  					 if(!revAI.equals("Classfile")) {
  	  					 oldR = new File(srcdirectory +"Revelations/"+ player.getUsername().replace(" ","_").toLowerCase()+"/Revelations.class");
  	  					 oldR.delete();
  					 }
  	  	
  				// oldR = new File(bindirectory +"userscripts/"+ player.username.toLowerCase()+"/Revelations.class");
  	  				//oldRJ = new File(PlayerScript.bhengbindirectory+"BHEngine/"+questcode+".class");
  				//oldR = new File(PlayerScript.bhengbindirectory+"BHEngine/"+questcode+".java");
  			//	oldR.delete(); 
  			//	oldRJ.delete();
 				 return true;

  			//	timeshit++;
  				} 
  				catch(ClassNotFoundException exc) { exc.printStackTrace();   return false; } catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} /*catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/ catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
  			
} catch(Exception exc) { exc.printStackTrace(); System.out.println("God was saved, though.");} 
            
           return true;
	
	
	}
	
	public boolean loadAndRunProgram() {
		return loadAndRunProgram(null);
	}
	
	/*
	public boolean loadProgram() {
		try {
		   UberStatement stmt = player.con.createStatement();
	       	 String toWrite; ResultSet holdRevStuff; String oldRev; FileWriter fw;
	  		String makeItExist[]; Process proc; StreamGobbler outputGobbler,inputGobbler,
	  		errorGobbler; Timer j; URL ue[]; URLClassLoader urlload; BattlehardFunctions bf;
	  		File oldR, oldRJ; String total,holdParcel; 
	  	 boolean transacted=false;
  			
  			
                   toWrite = "";
                 
                  try {
  				
  			//	String[] oldRev = GodGenerator.returnStringArrayFromFile("/users/arkavon/documents/programs/workspace/BattlehardAIWars/src/userscripts/" + player.username + "/Revelations.java");
  			 holdRevStuff = stmt.executeQuery("select revAI from revelations where pid = " + player.ID);
  			 holdRevStuff.next();
  			 oldRev = holdRevStuff.getString(1);
  			 holdRevStuff.close();
  			 // make it if it's not already there!
  			 Runtime.getRuntime().exec("mkdir "+srcdirectory+"userscripts/"+player.username.toLowerCase());
  			 Runtime.getRuntime().exec("mkdir "+bindirectory+"userscripts/"+player.username.toLowerCase());

  			 fw = new FileWriter(srcdirectory+"userscripts/"+player.username.toLowerCase()+"/Revelations.java");

  			 	fw.write(oldRev);
  				fw.close(); } catch(IOException exc) { exc.printStackTrace(); eraseCmdRestart("IO Exception occured in Gigabyte. Please contact support");
  				return false;}

  				try {
  					String toExec = "javac -cp " + bhengbindirectory + ":" + apachedirectory  +  "lib/servlet-api.jar"+
						" -d " + bindirectory + 
							" " + srcdirectory +"userscripts/"+player.username.toLowerCase()+"/Revelations.java";
  					System.out.println("Executing " + toExec);
  				 proc = 	Runtime.getRuntime().exec(toExec);
  	             errorGobbler = new 
                  StreamGobbler(proc.getErrorStream(), "ERROR");     
  	            
  	             inputGobbler = new StreamGobbler(proc.getInputStream(),"INPUT");
              
              // any output?
               outputGobbler = new 
                  StreamGobbler(proc.getInputStream(), "OUTPUT");
              // kick them off
              errorGobbler.start();
              outputGobbler.start();
              inputGobbler.start();
               j= new Timer(7);
              while((!errorGobbler.isDone()||!outputGobbler.isDone())&&!j.isDone()) {
              	// This loop should play over and over until either j is done or outputgobbler and error gobbler are done.
              	// so we know the loop should not play if w = jdone + (outputgob*errorgob) 
              	// there for we should play it while not(w) is true which is !jdone*!(og*eg) = !jdone(!og + !eg)
              }
              
                                      
              // any error???
              int exitVal = proc.waitFor();
              toWrite = errorGobbler.returnRead()+"\n"+outputGobbler.returnRead()+"\n"+inputGobbler.returnRead()+"\nExitValue: "+exitVal;
              b.setError(toWrite);
              System.out.println(toWrite);
  				proc.destroy(); // to kill it off and release resources!
  				}
  					catch(IOException exc) { exc.printStackTrace(); eraseCmdRestart("IO Exception occured in Gigabyte. Please contact support"); ; return false;}
  					catch(InterruptedException exc) { exc.printStackTrace(); eraseCmdRestart("Interrupted Exception occured in Gigabyte. Please contact support"); ; return false; }
  				ue = new URL[1];
  				try {
  				ue[0] = new URL("file:" + bindirectory);
  				//ue[0] = new URL("file:"+"/users/arkavon/documents/programs/workspace/BattlehardAIWars/bin2/");
  				} 
  				catch(MalformedURLException exc) {
  						exc.printStackTrace();eraseCmdRestart("MalformedURLException occured in Gigabyte. Please contact support");  return false;}
  				 urlload = new URLClassLoader(ue);
  				currRev=null; // after these two statements, no reference to the former currRev to bother us.
  				//		System.gc();
  				
  				 bf = new BattlehardFunctions(player.God,player,"4p5v3sxQ");
  			//	System.out.println("Here.");
  				 j = new Timer(7);
  				do {
  					makeItExist = GodGenerator.returnStringArrayFromFile(bindirectory +"userscripts/"+ player.username.toLowerCase()+"/Revelations.class");
  			//	makeItExist = GodGenerator.returnStringArrayFromFile( "/users/arkavon/documents/programs/workspace/BattlehardAIWars/bin2/"+"userscripts/"+ player.username+"/Revelations"+timeshit+".class");

  					// so it has time to compile, once it's not load again we know it exists.
  					// should only get out if the timer is 0 or make it exist does not equal load again(is 1)
  					// call load again 0, timer at 0 is 0, then !F = !t + m, so it should go while F, or while(!(!t + m))
  					// or while(t!m) goodie. j.isDone() is true if 0, so j.isDone() is actually !t. Sorry for confusion.
  				} while(!j.isDone()&&makeItExist[0].equals("load again"));
  				boolean toRet=true;
  				if(!makeItExist[0].equals("load again")) System.out.println("loaded " + player.username + "'s program."); else {
  					System.out.println("Nope, " + player.username + ".");
  					toRet=false;
  					}

  		
  				if(!makeItExist[0].equals("load again"))
  			//	try {
  				//	currRev = Class.forName("userscripts." + player.username.toLowerCase() +".Revelations",false,urlload);
  				// oldR = new File(bindirectory +"userscripts/"+ player.username.toLowerCase()+"/Revelations.class");
  			//	oldR = new File("/users/arkavon/documents/programs/workspace/BattlehardAIWars/bin2/"+"userscripts/"+ player.username+"/Revelations"+timeshit+".class");
  				//oldRJ = new File(srcdirectory+"userscripts/"+player.username.toLowerCase()+"/Revelations.java");
  			//	oldR.delete();  DO NOT DELETE SO WE CAN LOAD LATER!
  			//	oldRJ.delete();
  			//	timeshit++;
  			//	} 
  				//catch(ClassNotFoundException exc) { exc.printStackTrace(); eraseCmdRestart("ClassNotFoundException occured in Gigabyte. Please contact support");  return false; }
  			
  				 total ="output:\n" + toWrite;
  				int g = 0;

  				while(g<total.length()) {
  					 holdParcel = total.substring(g,g+1);
  					if(holdParcel.equals("'")) {
  							total=total.substring(0,g) + "\\" + total.substring(g,total.length());g++;}
  					g++;		

  				}
  				 transacted=false;
  				 while(!transacted) {
  					 
  				 try {
  			      stmt.execute("start transaction;");

  			      //stmt.executeUpdate("update revelations set error = '" + total + "' where pid = " + player.ID +";");		
  		
  					stmt.executeUpdate("commit;");
  					transacted=true;
  					
  				 } catch(MySQLTransactionRollbackException exc) { }
  				 }
					stmt.close();

  				 transacted=false;
            
  				 return toRet;
           } catch(SQLException exc) { exc.printStackTrace();}
           return false;
	}*/
	
	public int makeCompileReq() {
		
		HttpClient httpClient = new HttpClient(); // may need to be s, or no s, or no 8080
		GetMethod method = new GetMethod("http://" + gigaIP + "/AIWars/Gigabyte?reqtype=runTests&UN=" + player.getUsername()+"&Pass="+player.getPassword());
		/*method.addRequestHeader(new Header("reqtype","runTests"));
		method.addRequestHeader(new Header("UN", player.username));
		method.addRequestHeader(new Header("Pass", player.password));*/

		try {
			int statusCode = httpClient.executeMethod( method );
				//System.out.println(statusCode);

			return statusCode;
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	
	
	public static int commaCount(String holdCmd) {
		 String commaCount = new String(holdCmd);
		 int i = 0;
		 while(commaCount.contains(",")) {
			 
			 commaCount = commaCount.substring(commaCount.indexOf(",")+1,commaCount.length());
			 i++;
		 }
		 
		 return i;
	}
	public static int dotCount(String holdCmd) {
		 String commaCount = new String(holdCmd);
		 int i = 0;
		 while(commaCount.contains(".")) {
			 
			 commaCount = commaCount.substring(commaCount.indexOf(".")+1,commaCount.length());
			 i++;
		 }
		 
		 return i;
	}
	public static int semiCount(String holdCmd) {
		 String commaCount = new String(holdCmd);
		 int i = 0;
		 while(commaCount.contains(";")) {
			 
			 commaCount = commaCount.substring(commaCount.indexOf(";")+1,commaCount.length());
			 i++;
		 }
		 
		 return i;
	}
	public String toJSONString(long array[]) {
		JSONStringer arrayWriter=new JSONStringer();
		if(array==null) return "false";
		 try {
			arrayWriter.array();
	
		 int k = 0;
		 while(k<array.length) {
			 arrayWriter.value(array[k]);

			 k++;
		 }
		 arrayWriter.endArray();
		 return arrayWriter.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return "internal_parser_error";
	}
	public static String toJSONString(int array[]) {
		JSONStringer arrayWriter=new JSONStringer();
		if(array==null) return "false";
		 try {
			arrayWriter.array();
	
		 int k = 0;
		 while(k<array.length) {
			 arrayWriter.value(array[k]);

			 k++;
		 }
		 arrayWriter.endArray();
		 return arrayWriter.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return "internal_parser_error";
	}
	public String toJSONString(String array[]) {
		JSONStringer arrayWriter=new JSONStringer();
		if(array==null) return "false";
		 try {
			arrayWriter.array();
	
		 int k = 0;
		 while(k<array.length) {
			 arrayWriter.value(array[k]);

			 k++;
		 }
		 arrayWriter.endArray();
		 return arrayWriter.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return "internal_parser_error";
	}
	public void eraseCmdRestart() {
		// System.out.println("Hey~");
		  try {
			  
	      stmt.execute("start transaction;");
		   //   System.out.println("I am telling the player to update.");
		      stmt.executeUpdate("update player set instructions = 'null' where pid = "+player.ID+";");

		//      stmt.executeUpdate("update player set outputchannel = 'true' where pid = " + player.ID +";");	
		      // above unneeded as player does this for us by having battlehardfunctions called notifyViewer
		      // when it wants attention. What if this doesn't get a chance to do instructions = null before
		      // player does callSync? Possible to get duplicate commands but highly unlikely.
		      // This must be this way because then programs of the user can also notify the Viewer
		      // of any changes needed!
				stmt.executeUpdate("commit;"); 				
				stmt.close(); //player.con.close();
		  } catch(SQLException exc2) { }
				
				  //player.reloadScript();
				 
	}
	
	public  void eraseCmdRestart( String error) {
		  try {
			  UberStatement stmt = player.con.createStatement();
	      stmt.execute("start transaction;");
		   //   System.out.println("I am telling the player to update.");
		      stmt.executeUpdate("update revelations set error = '" + error + "' where pid = "+player.ID+";");

		//      stmt.executeUpdate("update player set outputchannel = 'true' where pid = " + player.ID +";");	
		      // above unneeded as player does this for us by having battlehardfunctions called notifyViewer
		      // when it wants attention. What if this doesn't get a chance to do instructions = null before
		      // player does callSync? Possible to get duplicate commands but highly unlikely.
		      // This must be this way because then programs of the user can also notify the Viewer
		      // of any changes needed!
				stmt.executeUpdate("commit;"); 				
				stmt.close(); //player.con.close();
		  } catch(SQLException exc2) { }
				
				 
	}
	public static String[] decodeStringIntoStringArray(String holdPart) {
		 
		try {
			JSONArray j = new JSONArray(holdPart);
			int size = j.length();
			String array[] = new String[size];
			int i =0;
			while(i<size) {
				array[i]=j.getString(i);
				i++;
			}
			return array;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String[1];
		
	}
	public static int[] decodeStringIntoIntArray(String holdPart) {
		 
		try {
			JSONArray j = new JSONArray(holdPart);
			int size = j.length();
			int array[] = new int[size];
			int i =0;
			while(i<size) {
				array[i]=j.getInt(i);
				i++;
			}
			return array;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new int[1];
		
	}
	public static boolean[] decodeStringIntoBooleanArray(String holdPart) {
		 
		try {
			JSONArray j = new JSONArray(holdPart);
			int size = j.length();
			boolean array[] = new boolean[size];
			int i =0;
			while(i<size) {
				if(j.getInt(i)==1)
					array[i]=true;
				else 
					array[i]=false;
				i++;
			}
			return array;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new boolean[1];
		
	}
	public static double[] decodeStringIntoDoubleArray(String holdPart) {
		 
		try {
			JSONArray j = new JSONArray(holdPart);
			int size = j.length();
			double array[] = new double[size];
			int i =0;
			while(i<size) {
				array[i]=j.getDouble(i);
				i++;
			}
			return array;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new double[1];
		
	}
	public static long[] decodeStringIntoLongArray(String holdPart) {
		 
		try {
			JSONArray j = new JSONArray(holdPart);
			int size = j.length();
			long array[] = new long[size];
			int i =0;
			while(i<size) {
				array[i]=j.getLong(i);
				i++;
			}
			return array;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new long[1];
		
	}

	}
	
