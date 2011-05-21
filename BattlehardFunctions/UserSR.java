package BattlehardFunctions;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.sql.Date;
import java.util.ArrayList;
import java.util.UUID;

import BHEngine.AttackUnit;
import BHEngine.Building;
import BHEngine.PlayerScript;
public class UserSR {

	/*
	 * This holds all you need to know about a SR.
	 */
	public boolean read = false; ArrayList<String> offList, defList,bldgList,lvlList; String combatHeader;
	String createdAt; String name;
	private boolean blastable;
	private int ax,ay,dx,dy;
	public int debm,debt,debmm,debf;
	private String zeppText;
	private boolean nukeSucc;
	private boolean nuke;
	private boolean debris,offdig,defdig; private String digMessage;
	String defNames, offNames, offst, offfi, defst, deffi, townOff,townDef; public UUID id; public boolean genocide=false,bomb=false; public boolean archived=false;
	public boolean defender = false;public int scout;public int resupplyID=-1;
public int bp; public boolean premium;
	public int ppllost=0; public boolean support = false;public int m,t,mm,f; public boolean invade=false;public boolean invsucc=false;
	public UserSR(UUID sid,String offst, String offfi,String defst, String deffi,String offNames,String defNames, String townOff, String townDef, boolean genocide, boolean read, boolean bomb, boolean defender,int m,int t,int mm, int f, int scout, boolean invade, boolean invsucc, int resupplyID,boolean archived,String combatHeader,String createdAt, String name, int bp, boolean premium
			,boolean blastable, int ax, int ay, int dx, int dy, String zeppText, int debm,int debt,int debmm,int debf, boolean debris,boolean nuke,boolean nukeSucc, boolean offdig, boolean defdig, String digMessage) {
		this.defNames = defNames; this.offNames=offNames; this.offst=offst; this.offfi = offfi;this.read=read;this.digMessage=digMessage;
		this.defst = defst; this.deffi = deffi; this.townOff = townOff; this.townDef = townDef;
		this.setOffdig(offdig); this.setDefdig(defdig);
		this.nuke=nuke;
		this.nukeSucc=nukeSucc;
		this.debris=debris;
		this.id = id; this.genocide=genocide; 
		this.name=name;
		this.blastable=blastable;
		this.debm=debm;this.debt=debt;this.debmm=debmm;this.debf=debf;
		this.createdAt=createdAt;
		this.bp=bp;this.premium=premium;
		this.combatHeader=combatHeader;
			this.defender=defender;
			this.scout=scout;
			this.archived=archived;
			this.invade=invade; this.invsucc=invsucc;
			// new instances are separated by +'s.
	
			this.m=m;this.t=t;this.mm=mm;this.f=f;
			this.resupplyID=resupplyID;
			this.ax=ax;this.ay=ay;this.dx=dx;this.dy=dy;
		int i = 0;
		this.bomb=bomb;
		String holdPart;
	/*	while(i<this.bombResultBldg.length) {
			if(!bombResultBldg[i].equals("null")&&!bombResultBldg[i].equals("nobldg")&&!bombResultBldg[i].equals("vic")) {
				 lotNum[i] = Integer.parseInt(bombResultBldg[i].substring(2,bombResultBldg[i].indexOf(".")));
				 holdPart = bombResultBldg[i].substring(bombResultBldg[i].indexOf(".")+1,bombResultBldg[i].length());
				 oldlvl[i] = Integer.parseInt(holdPart.substring(0,holdPart.indexOf(".")));
				ppllost += Integer.parseInt(holdPart.substring(holdPart.indexOf(".")+1,holdPart.lastIndexOf(".")));
	
		
			//System.out.println("Type is: " + b.type);
			//b = b.returnCopy();
			}
			i++;
		}*/
		i = 0;/*
		while(i<bombResultPpl.length) {
			if(!bombResultPpl[i].equals("null")&&!bombResultPpl[i].equals("noppl")&&!bombResultPpl[i].equals("vic")) { // only want to print vic once, right?
				// Might as well do arithmetic now and not over and over again later.
	
			String ppldead = bombResultPpl[i];
			int totalkilled = 0;
			String currslice;
			while(!ppldead.equals("")) {
			 currslice = ppldead.substring(0,ppldead.indexOf(";")); // take current piece off stack for analysis.
				int pplBef = Integer.parseInt(currslice.substring(currslice.indexOf(".")+1, currslice.lastIndexOf("."))); // get ppl before.
				int pplAft = Integer.parseInt(currslice.substring(currslice.lastIndexOf(".")+1, currslice.length())); // get ppl before.
				ppldead = ppldead.substring(ppldead.indexOf(";")+1,ppldead.length());
	
				totalkilled+=(pplBef-pplAft);
			}
			
			ppllost+=totalkilled;
			}
			i++;
		}*/
	}
	public String getCreatedDate() {
		return createdAt;
	}
	
	static String[] getStringArrayFromPluses(String bombResultBldg) {
		int i = 0;
		
		if(bombResultBldg.equals("null")) {
			String[] toRet = {"null"};
			return toRet;
		}
		String holdBomb = bombResultBldg; String holdPart;
		while(!holdBomb.equals("")) {
		//	System.out.println("Currently: " + holdBomb);
			holdBomb = holdBomb.substring(holdBomb.indexOf("+")+1,holdBomb.length());
			i++;
		}
		String bombResultBldgArray[] = new String[i];
		
		holdBomb = bombResultBldg;
		i=0;
		while(!holdBomb.equals("")) {
			bombResultBldgArray[i] = holdBomb.substring(0,holdBomb.indexOf("+"));
			holdBomb = holdBomb.substring(holdBomb.indexOf("+")+1,holdBomb.length());
			i++;
		}
		
		return bombResultBldgArray;
	}

	/**
	 * Get the combat data in a string, tells you about certain environmentals that determined
	 * the outcome of the battle.
	 */
	public String getCombatHeader() {
		return combatHeader;
	}
	/**
	 * Get the beginning numbers of the offensive lineup, will return null if this is a scouting
	 * run. To view scouting results, you must use the getRaidString method.
	 * @return
	 */
	public int[] getOffBeginArray() {
		
		if(scout!=1) {
		return getIntArray(offst);
		
		} else return new int[1];
	}
	/**
	 * Get the ending numbers of the offensive lineup, will return null if this is a scouting
	 * run. To view scouting results, you must use the getRaidString method.
	 * @return
	 */
	public int[] getOffEndArray() {
		
		if(scout!=1) {

			return getIntArray(offfi);
			
			
			} else return new int[1];
		
	}
	/**
	 * Returns true if this SR is Facebook blastable.
	 * @return
	 */
	public boolean getBlasted() {
	return blastable;	
	}
	/**
	 * Get the beginning numbers of the defensive lineup, will return null if this is a scouting
	 * run or a resupply run. To view scouting results, you must use the getRaidString method.
	 * @return
	 */
	public int[] getDefBeginArray() {
		
		if(scout!=1) {
		
			if(resupplyID==-1&&defst!=null) {
				int[] toR =  getIntArray(defst);
				int realR[] = new int[defList.size()];
				int i = 0;
				while(i<realR.length) {
					realR[i]=toR[i];
					i++;
				}
				
				return realR; // based on premise that buildings always the last on the stack.
			}
		}
		return new int[1];
	}
	/**
	 * Get the ending numbers of the defensive lineup, will return null if this is a scouting
	 * run or a resupply run. To view scouting results, you must use the getRaidString method.
	 * @return
	 */
	public int[] getDefEndArray() {
		
		if(scout!=1) {
		
			if(resupplyID==-1&&deffi!=null) {
				int[] toR =  getIntArray(deffi);
				int realR[] = new int[defList.size()];
				int i = 0;
				while(i<realR.length) {
					realR[i]=toR[i];
					i++;
				}
				
				return realR; // based on premise that buildings always the last on the stack.
				
			}
		}
		
		return new int[1];

	}
	/**
	 * Returns the ending levels of all the buildings in the building name list, so if you had a Metal Mine bombed in your list at index 1, 
	 * this array at index 1 would have the level it ended at.
	 * @return
	 */
	public int[] getLvlArray() {
		
		if(scout!=1) {
		
			if(resupplyID==-1&&deffi!=null) {
				int[] toR =  getIntArray(deffi);
				int[] toR2 =  getIntArray(defst);

				int realR[] = new int[bldgList.size()];
				int i = 0;
				while(i<realR.length) {
					realR[i]=toR2[i+defList.size()]-toR[i+defList.size()]; // so if there is two unit, and the building is at index 2, then this is 0 + 2 = 2.
					i++;
				}
				
				return realR; // based on premise that buildings always the last on the stack.
				
			}
		}
		
		return new int[1];

	}
	/**
	 * Returns the names of the buildings that were bombed.
	 * @return
	 */
	public String[] getBldgNames() {
		String[] bldg;
		if(bldgList==null) getRaidString();
		if(bldgList!=null) {
		bldg=new String[bldgList.size()];
		int i =0;
		for(String x:bldgList) {
			bldg[i]=x;
			i++;
		}
		return bldg;
		} else {
			bldg = new String[0];
			return bldg;
		}
	}
	/**
	 * Returns the attack's name.
	 * @return
	 */
	public String getName() {
		return name;
	}
	public String[] getOffNames() {
		int i = 0;
		if(offList==null) 
			getRaidString(); // to populate the offList.
		String toRet[] = new String[offList.size()];
		while(i<toRet.length) {
			toRet[i] = removeDoubleColons(new String(offList.get(i)));
			i++;
		}
		return toRet;
	}
	public String[] getDefNames() {
		int i = 0;
		if(defList==null) 
			getRaidString(); // to populate the offList.

		String toRet[] = new String[defList.size()];
		while(i<toRet.length) {
			toRet[i] = removeDoubleColons(new String(defList.get(i)));
			i++;
		}
		
		return toRet;
	}
	
	public boolean isEqual(UUID sid) {
		if(this.id.equals(sid)) return true; else return false;
	}
	/**
	 * Returns the header of an SR, ex "Town1 bombs Town2 as part of a siege campaign."
	 * Perfect for the UI programmer looking to make a subject header for a status report.
	 */
	public String toString() {
		String toret="";
		String townOff = this.townOff;
		String townDef = this.townDef;
		if(offdig) townOff = " The dig team from " + townOff;
		if(defdig) townDef = " the dig team on " + townDef;

		if(genocide&&bomb) toret= townOff + " bombs " + townDef+" as part of a Glassing campaign.";
		else if(genocide) toret= townOff + " attacks " + townDef+" as part of a Siege campaign.";
		else if(!genocide&&bomb&&!debris) toret = townOff + " strafes " + townDef;
		else if(!genocide&&!debris) toret = townOff + " attacks " + townDef;
		else if(debris) toret = townOff + " collects debris from " +townDef;
		
		
		if(nuke&&nukeSucc) toret= townOff + " nukes the $#^@ out of " + townDef;
		if(nuke&&!nukeSucc) toret=townOff + "'s nuclear attack is thwarted by " + townDef;
		// invade + any other attack type boolean(genocide, etc)
		// will never occur, so if it's a normal attack and !genocide is then
		// the one, this invade block will change it if it's actually
		// not a normal attack but an invasion attempt. Furthermore,
		// an invasion will never be support or scout so we know the
		// following statements will not affect it.
		if(invade&&!invsucc) toret = townOff + " fails to invade " + townDef;
		else if(invade&&invsucc) toret = townOff + " successfully invades " + townDef;
		
		if(support) toret = townOff + " supplies " + townDef; // separate from other ifs,
		// support changes the entire thing, no matter the other boolean variables.
		
		if(resupplyID!=-1&&bomb) toret=townOff + " resupplies a Glassing campaign raid.";
		else if(resupplyID!=-1) toret=townOff+" resupplies a Siege campaign raid.";
			// so we do the least stuff last, so specifics get checked first.
		if(scout==1) toret=townOff + " spies on " + townDef;
		else if(scout==2) toret=townOff + "'s spies discovered by " + townDef;
		// scouting block, 0 yields nothing, 1 means a good scouting run and
		// we should format appropriately, 2 means it became a violent conflict.
		//if(!read) toret+=" (Unread)";
		/*if(premium)
		toret+=" (" + bp + " BP Gained)";
		else if(!premium)
			toret+=" (Would've gained " + bp + " BP with BHM)";*/

		return removeDoubleColons(toret);
	}
	public int getBp() {
		return bp;
	}
	
	public int getAx() {
		return ax;
	}
	public void setAx(int ax) {
		this.ax = ax;
	}
	public int getAy() {
		return ay;
	}
	public void setAy(int ay) {
		this.ay = ay;
	}
	public int getDx() {
		return dx;
	}
	public void setDx(int dx) {
		this.dx = dx;
	}
	public int getDy() {
		return dy;
	}
	public void setDy(int dy) {
		this.dy = dy;
	}
	public String getZeppText() {
		return zeppText;
	}
	public void setZeppText(String zeppText) {
		this.zeppText = zeppText;
	}
	/**
	 * Gets the various results of the mission, like, Your Bombing mission was successful: No targets remain.
	 * Also tells you what people you killed, resources taken/scouted, etc.
	 * @return
	 */
	public String getHeaders() {
		String toRet="";
		int i = 0;
		/*while(i<bombResultBldg.length) {
			if(!bombResultBldg[i].equals("null")&&!bombResultBldg[i].equals("nobldg")) {
				if(bombResultBldg[i].equals("vic")) toRet+=("Your bombing mission was successful. No targets remain.;");
				else {
				// need ot parcel these up!
				
				if(bombResultBldg[i].startsWith("l"))
					toRet+="A " + btype[i] + " was bombed from level " + oldlvl[i] +
							" to level " + (oldlvl[i]-1)+";";
					else if(bombResultBldg[i].startsWith("d"))
						toRet+="A " + btype[i] + " was destroyed.;";
					}
				
				
			}
			i++;
		}*/
	/*	if(ppllost>0) {
			//how are these printed? One line for every building? Or just a total number killed? Probably best to do a total number killed...
			// do this by sucking up the string...

			toRet+=(ppllost+ " citizens were killed in this bombing run.;");

		}*/
		if(!support&&resupplyID==-1&&scout==0){
			// if it is not a support run, or a resupply run, or a scouting run, then print resources taken...
		toRet+= m + " Metal, "+ t + " Timber, " + mm + " Crystal, and " + f + " Food were taken in the attack.;";
	//	if(premium) {
		//	toRet+="You received " + bp + " BP from this attack. ";
		//} else toRet+=+ bp + " BP received with premium membership. ";
		/*	double rand = Math.random();
			if(bp<20){
			//	if(rand<.5)
			//	toRet+="Your Suck is only eclipsed by how disgustingly average you are.;";
				toRet+="Try attacking players more your own size to raise your BP scores!;";
			//	else
			//	toRet+="The suicide button in this game does not apply to you. Please stop pressing it.;";

			}
			else if(bp>20&&bp<40) {
				toRet+="You've got an okay battle but you were a clear victor going in. To raise your score even higher, hit someone larger!;";
		//		if(rand<.5)
			//		toRet+="You fight like a commie.;";
			//	else
				//	toRet+="Your citizens must take a lot of antidepressants.;";
			}
			else if(bp>40&&bp<60)
				toRet+="This is a good, solid battle that you've fought here and you have earned your BP.";
			//	if(rand<.5) 
				//	toRet+="Somebody's parents might possibly have loved them at some undisclosed point in the far past, but they stopped when you started acting like a namby-pamby.;";
				//else
					//toRet+="You may get yourself a man someday at this rate!;";
			else if(bp>60&&bp<80)
				toRet+="Great battle, evenly fought.";
			//	if(rand<.5)
			//toRet+="Respectability wafts about you briefly like a stranger's fart in an elevator.;";
			//	else
			//toRet+="This seems good now, but let me put it this way: If you were at this percentile in reading, you wouldn't be able to read this sentence.;";

			else if(bp>80&&bp<100)
				if(rand<.5)
				toRet+="The good news is, you kicked ass. The bad news is, women still won't say hello to you.;";
				else
				toRet+="Congratulations, your army of retarded cripples overwhelmed an opponent by sheer numbers. Make sure to give them cookies.;";

			else if(bp>80&&bp<100)
				if(rand<.5)
			toRet+="Okay, maybe your parents do remember your name sometimes...;";
				else
			toRet+="Wow, you've just hit the high point of your entire life. Savor it. That's right. Enjoy it. Now go away.;";

			else if(bp>100)
			toRet+="Perfection just means everybody secretly hates you.;";*/
			
		
		} else if(scout==1) {
			// support WILL be false if scout is 1, so we say scout must be 0 and !support for
			// showing resources like above so that it'll hopscotch to the scout==1 if it is indeed
			// a successful scouting run.
			
			
			if(m!=-1) 
			toRet+=(m + " Metal, "+ t + " Timber, " + mm + " Crystal, and " + f + " Food were sited at this location.;");
			else toRet+=("Resources were not reconnoitered on this scouting run.;");
		
		}
		return removeDoubleColons(toRet);
	}
	
	/**
	 * Returns the side labels like units gained:; units returned:;
	 * @return
	 */
	public String getLabels() {
		String toRet="";
		 if(support) {

				toRet+=(townDef + " units gained:;");
				toRet+=(townDef + " units returned:;");
			} else if(scout==1) {
				// scout == 0 will ensure that !support, which happens with scout=1, will not allow
				// falling in up there. When support is true, scout = 0 anyway, and so
				// we just add scout = 1.
				
				toRet+=(townDef + " units: ;");
				toRet+=(townDef + " unit stats:;");
			} else if(resupplyID!=-1) {

				toRet+=("Raid before:;");
				toRet+=("Raid after:;");
			}
			else {
			
				toRet+=(townOff + " before:;");
				toRet+=(townOff + " after:;");
			}
		 return removeDoubleColons(toRet);
	}
	
	private int[] getIntArray(String holdThis) { 
		int size = PlayerScript.commaCount(holdThis);
		int array[] = new int[size];
		int i = 0;
		holdThis+=",";
		while(i<size) {

			 holdThis = holdThis.substring(holdThis.indexOf(",")+1,holdThis.length());

			array[i]=Integer.parseInt(holdThis.substring(0,holdThis.indexOf(",")));
			i++;
		}
		
		return array;
		
	}
	
	/**
	 * This big bad boy contains the meat of a raid. It contains everything except
	 * the toString() subject head and the getHeaders() strings that tell you about resources
	 * and what not. It shoots out a bunch of strings separated by semicolons that represent
	 * the state of the raid.
	 * @return
	 */
	public String getRaidString() {
		String offfitemp=offfi;

	String offsttemp = offst+",";
	String offNamestemp = offNames+",";
	String defsttemp = defst+",";
	String defNamestemp = defNames+",";
	int superIndentAmt=0;
	String toRet="";
	if(scout!=1) {
		offfitemp +=",";
	
	// when scout = 1, the extra comma on the end of attackunit stats
	//screws up it's processing.
	} else {
		// when scout = 1, we need to find the super indent maximum if weapons
		// and stats are given.
		String numberlost;
		while(!offfitemp.equals("")) {
			offfitemp = offfitemp.substring(offfitemp.indexOf(";")+1,offfitemp.length());
			if(offfitemp.length()>0) {
			numberlost = offfitemp.substring(offfitemp.indexOf(",")+1,offfitemp.indexOf(";"));
			numberlost+=",";
			// numberlost doesn't need first comma attached to the string,
			// though it's there out of using the same concepts we've used
			// in other data fields! However, it does attach the , manually like other
			// times.
			if(!numberlost.equals("?,")) {
				numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
				numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
				numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
				numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
				numberlost=AttackUnit.returnWeaponsString(numberlost);
				int superHolder = 0;
				while(!numberlost.equals("")) {
					numberlost=numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
					superHolder++;
				}
				if(superHolder>superIndentAmt) superIndentAmt=superHolder;
			}
			
		}
		

	}
		
		offfitemp=offfi;
	}
	String deffitemp = deffi+",";
	
	
	
	int offCount = 0, defCount = 0;
	while(!offsttemp.equals("")) {
		offsttemp = offsttemp.substring(offsttemp.indexOf(",")+1,offsttemp.length());
		offCount++;
	}
	//System.out.println("Before: " +defsttemp);
	while(!defsttemp.equals("")) {
		//System.out.println(defsttemp + " - " + defCount);
		defsttemp = defsttemp.substring(defsttemp.indexOf(",")+1,defsttemp.length());
		defCount++;
	}
	 defsttemp = defst+",";
	 offsttemp = offst+",";

	boolean breakout = false;
	int i = 0;
	int currX;
	 if(support) {

		toRet+=(townDef + " units gained:;");
		toRet+=(townDef + " units returned:;");
	} else if(scout==1) {
		// scout == 0 will ensure that !support, which happens with scout=1, will not allow
		// falling in up there. When support is true, scout = 0 anyway, and so
		// we just add scout = 1.
		
		toRet+=(townDef + " units: ;");
		toRet+=(townDef + " unit stats:;");
	} else if(resupplyID!=-1) {

		toRet+=("Raid before:;");
		toRet+=("Raid after:;");
	}
	else {
	
		toRet+=(townOff + " before:;");
		toRet+=(townOff + " after:;");
	}
	boolean superIndent=false; // only enacted if scout is on and we get attack unit info.
	offList = new ArrayList<String>();
	defList = new ArrayList<String>();
	bldgList = new ArrayList<String>();

	String number, offName, numberlost,unitStats[],nametest;
	int lengths[];
	while(i<offCount-1) {
		offsttemp = offsttemp.substring(offsttemp.indexOf(",")+1,offsttemp.length());
		 number = offsttemp.substring(0,offsttemp.indexOf(",")); 
		 
		 	 unitStats = new String[5];
			
			
			if(scout==1) { // dealing with attack unit data codes in here!

				offfitemp = offfitemp.substring(offfitemp.indexOf(";")+1,offfitemp.length());
				if(offfitemp.length()>0) {
				numberlost = offfitemp.substring(offfitemp.indexOf(",")+1,offfitemp.indexOf(";"));
				numberlost+=",";
				// numberlost doesn't need first comma attached to the string,
				// though it's there out of using the same concepts we've used
				// in other data fields! However, it does attach the , manually like other
				// times.
				if(!numberlost.equals("?,")) {
					unitStats[0]="Cnclmt: " + numberlost.substring(0,numberlost.indexOf(","))+";";
					numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
					unitStats[1]="Armor: " + numberlost.substring(0,numberlost.indexOf(","))+";";
					numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
					unitStats[2]="Cargo: " + numberlost.substring(0,numberlost.indexOf(","))+";";
					numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
					unitStats[3]="Speed: " + numberlost.substring(0,numberlost.indexOf(","))+";";
					numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
					unitStats[4]= "Wpns: " + AttackUnit.returnWeaponsString(numberlost)+";";
				}
				superIndent=true;
				} else numberlost = "";
			} else {
				offfitemp = offfitemp.substring(offfitemp.indexOf(",")+1,offfitemp.length());
				numberlost = offfitemp.substring(0,offfitemp.indexOf(",")); 

			}
			 
		offNamestemp = offNamestemp.substring(offNamestemp.indexOf(",")+1,offNamestemp.length());
		 nametest = offNamestemp.substring(0,offNamestemp.indexOf(","));
		if((defender||(scout==1&&
				(!nametest.equals("Engineer")&&!nametest.equals("Trader")&&!nametest.equals("Scholar")))
				)&&number.equals("0")) offName = "???";
		// scout == 1 makes sure program recognizes offensive actually has town2's stuff
		// and so should not show zero unit things since they aren't present.
		// except for civilian units, so if it is scout 1 and not an engineer
		// and not a trader and not a scholar then we can ??? it.
		else offName = offNamestemp.substring(0,offNamestemp.indexOf(","));
		// by definition, with numberlost, it should ALWAYS be less than the number started with.
		
		
		// need to format correctly - whichever is larger controls the pie, so to speak!
	
		
		if(unitStats[0]==null) {
			// This thing can draw normally if unit stats are not included,
			// and null means that they weren't.
		if(offName.length()>number.length()) {
		offList.add(new String(offName));
		toRet+=(offName+";");

		toRet+=(number+";");
		if(scout==1&&numberlost.equals("?,")) {// do nothing, no print for ?'s.	
		}
		else
		toRet+=(numberlost+";");
		// so the thing needs to be incremented by cx + start of word + word length + tab
		} else {
			offList.add(new String(offName));
			toRet+=(offName+";");

			toRet+=(number+";");
			toRet+=numberlost+";";

			// so the thing needs to be incremented by cx + start of word + word length + tab
		}
		} else {
			
			// scout printing block for attack unit stats and all!!
			// now unitStats must be compared with numbers and with offName,
			// each could be the largest.
			
	
		
		
		int whichwin = 0; // if whichwin is 1, then offName won, 2, number won, 3,
		// unitStats won. We'll use a switch to print the correct formatting!
		
	
		
			offList.add(new String(offName));
			toRet+=offName+";";

			toRet+=number+=";";
				// drawing unit stats only if we have received them!
				int j = 0;
				while(j<unitStats.length) {
				toRet+=(unitStats[j]);
				// notice *j on the end to increment the y each time!
				// I hope that my spacing is right...
				j++;
				}
				
		
		}
		
		
		
		i++;
		
	}
	
	breakout = false;
	 i = 0;
	
			
		
	 if(!support&&resupplyID==-1) { // support and resupply runs do not need tread here, so -1 ensures
		 // that on the resupply front, !support on the support front!
		 if(scout==1) {
				toRet+=townDef + "'s Bldg Amts:"+";";
			//	 toRet+=townDef+"'s Bldg Amts:;";

		 } else {
		toRet+=townDef + " before:;";
		toRet+=townDef + " after:;";
	 
		 }
		String defName;

	while(i<defCount-1) {
		defsttemp = defsttemp.substring(defsttemp.indexOf(",")+1,defsttemp.length());
		 number = defsttemp.substring(0,defsttemp.indexOf(",")); 
		
		 

			deffitemp = deffitemp.substring(deffitemp.indexOf(",")+1,deffitemp.length());
		//	System.out.println("Problem child: " + deffitemp);
			if(scout!=1)
			numberlost = deffitemp.substring(0,deffitemp.indexOf(",")); 
			else numberlost="";
		defNamestemp = defNamestemp.substring(defNamestemp.indexOf(",")+1,defNamestemp.length());

		if(!defender&&scout==0&&number.equals("0")) defName = "???";
		// no use in ??? out buildings, which happens with scout==1.
		else defName = defNamestemp.substring(0,defNamestemp.indexOf(","));
		// no need for ??? for defensive purposes!!!
		if(Building.getCost(defName)[0]==0)
		defList.add(new String(defName));
		else bldgList.add(new String(defName));

	
		
		// need to format correctly - whichever is larger controls the pie, so to speak!
		if(defName.length()>number.length()) {
			if(defName.endsWith(";")) {
				toRet+=defName;
			}
			else {
				toRet+=(defName+";");

			}
			if(number.endsWith(";"))
				toRet+=number;
			else
			toRet+=(number+";");
			if(numberlost.endsWith(";"))
				toRet+=numberlost;
			else
			toRet+=(numberlost+";");
		// so the thing needs to be incremented by cx + start of word + word length + tab
		} else {
			if(defName.endsWith(";")) {
				defList.add(new String(defName));
				toRet+=(defName+";");

			}
			else {
				defList.add(new String(defName));

			toRet+=(defName+";");
			}
			if(number.endsWith(";"))
				toRet+=number;
			else
			toRet+=(number+";");
			if(numberlost.endsWith(";"))
				toRet+=numberlost;
			else
			toRet+=(numberlost+";");

			// so the thing needs to be incremented by cx + start of word + word length + tab
		}
		i++;
		
	}
	 }
	 
	 
	 
	 return removeDoubleColons(toRet);
	 }
	private String removeDoubleColons(String toRet) {
	int i = 0;
		while(toRet.contains(";;")) {
			i = toRet.indexOf(";;");
			toRet = toRet.substring(0,i) + toRet.substring(i+1,toRet.length());
			
			
		}
		
		/*
		String p;
		while(i<toRet.length()-1) {
			p = toRet.substring(i,i+1);
			if(p.equals("'")) {
			if(i!=0)
			toRet = toRet.substring(0,i-1) +"\\"+ toRet.substring(i,toRet.length());
			else toRet="\\"+toRet;
			i+=2; // length has been increased by one, so we decrease. Now before it
			// was on the comma, now it should be on the slash. So we increment
			// by one to get it back on the ', and then by one again to get it past.
			}
			i++;
		}*/
		
		return toRet;
	}
	/*
	private void print(Graphics buffg, int offsety) {
	
	
		// how do we want this to print? We want to do the spacing of the names according to the offst or defst
		// units. 

		buffg.setColor(Color.white);
		buffg.setFont(new Font("Visitor TT1 (BRK)",Font.PLAIN, 18)); // beware the entire program uses the fm for this font.
		int cx = 45; int cy = 178+offsety;
		// how do we make this preferable for scrolling?
		// Answer: really unnecessary, we just make the thing scrollable with offset.
		FontMetrics fm = buffg.getFontMetrics();

		buffg.drawString(toString(),425-fm.stringWidth(toString())/2,cy); // center is at 425, we want center of words there..
		
		cy+=fm.getMaxAscent()*3;
		
		buffg.setFont(new Font("Visitor TT1 (BRK)",Font.PLAIN, 14)); // beware the entire program uses the fm for this font.
		fm = buffg.getFontMetrics();
	
		
		// bombResultBldg looks like d lotnum or l lotnum.oldlvl.pplkilledinblast
		// bomb result people looks like ;lotNum.peopleBef.peopleAfter
		// so bombResultPpl does ;data;data;data <--- no ending ;.
		// using different separators for different parts.
		
	
		if(!bombResultBldg.equals("null")&&!bombResultBldg.equals("nobldg")) {
			cy+=fm.getMaxAscent();
			if(bombResultBldg.equals("vic")) buffg.drawString("Your bombing mission was successful. No targets remain.",cx,cy);
			else {

			if(bombResultBldg.startsWith("l"))
			buffg.drawString("A " + btype + " was bombed from level " + oldlvl +
					" to level " + (oldlvl-1),cx,cy);
			else if(bombResultBldg.startsWith("d"))
				buffg.drawString("A " + btype + " was destroyed.",cx,cy);
			}
			cy+=fm.getMaxAscent()*2;
			//else buffg.drawString("")
			
		}
		if(ppllost>0) {
			cy+=fm.getMaxAscent();
			//how are these printed? One line for every building? Or just a total number killed? Probably best to do a total number killed...
			// do this by sucking up the string...

			buffg.drawString(ppllost+ " citizens were killed in this bombing run.",cx,cy);
			cy+=fm.getMaxAscent()*2;

		}
		if(!support&&resupplyID==-1&&scout==0){
			// if it is not a support run, or a resupply run, or a scouting run, then print resources taken...
			buffg.setColor(Color.yellow);
		buffg.drawString(m + " Metal, "+ t + " Timber, " + mm + " Manufactured Materials, and " + f + " Food were taken in the attack.",cx,cy);
		cy+=fm.getMaxAscent();
		buffg.setColor(Color.white);
		} else if(scout==1) {
			// support WILL be false if scout is 1, so we say scout must be 0 and !support for
			// showing resources like above so that it'll hopscotch to the scout==1 if it is indeed
			// a successful scouting run.
			
			
			buffg.setColor(Color.yellow);
			if(m!=-1) 
			buffg.drawString(m + " Metal, "+ t + " Timber, " + mm + " Manufactured Materials, and " + f + " Food were sited at this location.",cx,cy);
			else buffg.drawString("Resources were not reconnoitered on this scouting run.",cx,cy);
			cy+=fm.getMaxAscent();
			buffg.setColor(Color.white);
		}
		String offfitemp=offfi;
		int namecy = cy+fm.getMaxAscent();
		int numcy= cy+fm.getMaxAscent()*3;
		String offsttemp = offst+",";
		String offNamestemp = offNames+",";
		String defsttemp = defst+",";
		String defNamestemp = defNames+",";
		int superIndentAmt=0;
		if(scout!=1) {
			offfitemp +=",";
		
		// when scout = 1, the extra comma on the end of attackunit stats
		//screws up it's processing.
		} else {
			// when scout = 1, we need to find the super indent maximum if weapons
			// and stats are given.
			String numberlost;
			while(!offfitemp.equals("")) {
				offfitemp = offfitemp.substring(offfitemp.indexOf(";")+1,offfitemp.length());
				if(offfitemp.length()>0) {
				numberlost = offfitemp.substring(offfitemp.indexOf(",")+1,offfitemp.indexOf(";"));
				numberlost+=",";
				// numberlost doesn't need first comma attached to the string,
				// though it's there out of using the same concepts we've used
				// in other data fields! However, it does attach the , manually like other
				// times.
				if(!numberlost.equals("?,")) {
					numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
					numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
					numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
					numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
					numberlost=AttackUnit.returnWeaponsString(numberlost);
					int superHolder = 0;
					while(!numberlost.equals("")) {
						numberlost=numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
						superHolder++;
					}
					if(superHolder>superIndentAmt) superIndentAmt=superHolder;
				}
				
			}
			

		}
			
			offfitemp=offfi;
		}
		String deffitemp = deffi+",";
		
		
		
		int offCount = 0, defCount = 0;
		while(!offsttemp.equals("")) {
			offsttemp = offsttemp.substring(offsttemp.indexOf(",")+1,offsttemp.length());
			offCount++;
		}
		while(!defsttemp.equals("")) {
			defsttemp = defsttemp.substring(defsttemp.indexOf(",")+1,defsttemp.length());
			defCount++;
		}
		 defsttemp = defst+",";
		 offsttemp = offst+",";

		boolean breakout = false;
		int i = 0;
		int currX;
		 if(support) {

			buffg.drawString(townDef + " units gained:",cx,numcy);
			buffg.drawString(townDef + " units returned:",cx,numcy+fm.getMaxAscent()*2);
			 currX = cx+fm.stringWidth(townDef+" units returned:\t");
		} else if(scout==1) {
			// scout == 0 will ensure that !support, which happens with scout=1, will not allow
			// falling in up there. When support is true, scout = 0 anyway, and so
			// we just add scout = 1.
			
			buffg.drawString(townDef + " units: ",cx,numcy);
			buffg.drawString(townDef + " unit stats:",cx,numcy+fm.getMaxAscent()*2);
			 currX = cx+fm.stringWidth(townDef+" unit stats:\t");
		} else if(resupplyID!=-1) {

			buffg.drawString("Raid before:",cx,numcy);
			buffg.drawString("Raid after:",cx,numcy+fm.getMaxAscent()*2);
			 currX = cx+fm.stringWidth("Raid before:\t");
		}
		else {
		
			buffg.drawString(townOff + " before:",cx,numcy);
			buffg.drawString(townOff + " after:",cx,numcy+fm.getMaxAscent()*2);
			 currX = cx+fm.stringWidth(townOff+" before:\t");
		}
		buffg.setColor(Color.red);
		boolean superIndent=false; // only enacted if scout is on and we get attack unit info.
		
		String number, offName, numberlost,unitStats[],nametest;
		int lengths[];
		while(i<offCount-1) {
			offsttemp = offsttemp.substring(offsttemp.indexOf(",")+1,offsttemp.length());
			 number = offsttemp.substring(0,offsttemp.indexOf(",")); 
			 
			 	 unitStats = new String[5];
				
				
				if(scout==1) { // dealing with attack unit data codes in here!

					offfitemp = offfitemp.substring(offfitemp.indexOf(";")+1,offfitemp.length());
					if(offfitemp.length()>0) {
					numberlost = offfitemp.substring(offfitemp.indexOf(",")+1,offfitemp.indexOf(";"));
					numberlost+=",";
					// numberlost doesn't need first comma attached to the string,
					// though it's there out of using the same concepts we've used
					// in other data fields! However, it does attach the , manually like other
					// times.
					if(!numberlost.equals("?,")) {
						unitStats[0]="Cnclmt: " + numberlost.substring(0,numberlost.indexOf(","));
						numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
						unitStats[1]="Armor: " + numberlost.substring(0,numberlost.indexOf(","));
						numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
						unitStats[2]="Cargo: " + numberlost.substring(0,numberlost.indexOf(","));
						numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
						unitStats[3]="Speed: " + numberlost.substring(0,numberlost.indexOf(","));
						numberlost = numberlost.substring(numberlost.indexOf(",")+1,numberlost.length());
						unitStats[4]= "Wpns: " + AttackUnit.returnWeaponsString(numberlost);
					}
					superIndent=true;
					} else numberlost = "";
				} else {
					offfitemp = offfitemp.substring(offfitemp.indexOf(",")+1,offfitemp.length());
					numberlost = offfitemp.substring(0,offfitemp.indexOf(",")); 

				}
				 
			offNamestemp = offNamestemp.substring(offNamestemp.indexOf(",")+1,offNamestemp.length());
			 nametest = offNamestemp.substring(0,offNamestemp.indexOf(","));
			if((defender||(scout==1&&
					(!nametest.equals("Engineer")&&!nametest.equals("Trader")&&!nametest.equals("Scholar")))
					)&&number.equals("0")) offName = "???";
			// scout == 1 makes sure program recognizes offensive actually has town2's stuff
			// and so should not show zero unit things since they aren't present.
			// except for civilian units, so if it is scout 1 and not an engineer
			// and not a trader and not a scholar then we can ??? it.
			else offName = offNamestemp.substring(0,offNamestemp.indexOf(","));
			// by definition, with numberlost, it should ALWAYS be less than the number started with.
			
			
			// need to format correctly - whichever is larger controls the pie, so to speak!
			if(i>0&&i%6==0) {
				// so if i is a multiple of six, we want to begin a new line, for support units.
				// for i==0 yields a mod of zero
				// which causes an unnecessary increase. So we sort of take care of this.
				// support runs never send more than six.
				if(!superIndent) {
				 namecy += fm.getMaxAscent()*8;
				 numcy+=fm.getMaxAscent()*8; 
				} else {
					 namecy +=(16+superIndentAmt)*fm.getMaxAscent(); // need an extra line down to print the five lines of stat code.
					 numcy+=(16+superIndentAmt)*fm.getMaxAscent(); 
				}
				
				if(scout==1)  currX = cx+fm.stringWidth(townDef+" unit stats:\t");
				else 	currX = cx+fm.stringWidth(townOff+" before:\t");

				
				
				 }
			
			if(unitStats[0]==null) {
				// This thing can draw normally if unit stats are not included,
				// and null means that they weren't.
			if(offName.length()>number.length()) {
			buffg.drawString(offName,currX,namecy);

			buffg.drawString(number,currX+fm.stringWidth(offName)/2-fm.stringWidth(number)/2,numcy);
			if(scout==1&&numberlost.equals("?,")) {// do nothing, no print for ?'s.	
			}
			else
			buffg.drawString(numberlost,currX+fm.stringWidth(offName)/2-fm.stringWidth(numberlost)/2,numcy+fm.getMaxAscent()*2);
			// so the thing needs to be incremented by cx + start of word + word length + tab
			currX+=fm.stringWidth(offName)+fm.stringWidth("\t"); 
			} else {
				buffg.drawString(offName,currX+fm.stringWidth(number)/2-fm.stringWidth(offName)/2,namecy);

				buffg.drawString(number,currX,numcy);
				buffg.drawString(numberlost,currX+fm.stringWidth(number)/2-fm.stringWidth(numberlost)/2,numcy+fm.getMaxAscent()*2);

				// so the thing needs to be incremented by cx + start of word + word length + tab
				currX+=fm.stringWidth(number)+fm.stringWidth("\t"); 
			}
			} else {
				
				// scout printing block for attack unit stats and all!!
				// now unitStats must be compared with numbers and with offName,
				// each could be the largest.
				
				int greatestLength=0; int j = 0;
			while(j<unitStats.length) {
			//	System.out.println("The width of " + unitStats[j] + " is " + fm.stringWidth(unitStats[j]));
				int length;
				if(j==4) 
					length = fm.stringWidth(unitStats[j].substring(0,unitStats[j].indexOf(",")));
				else
					 length = fm.stringWidth(unitStats[j]);
				
				
				// because we're going to be separating the weapons string.
				if(length>greatestLength) { 
					greatestLength=length;
				}
				j++;
			}
			
			
			int whichwin = 0; // if whichwin is 1, then offName won, 2, number won, 3,
			// unitStats won. We'll use a switch to print the correct formatting!
			
			 lengths = new int[3];
			lengths[0]=fm.stringWidth(offName);
			lengths[1]=fm.stringWidth(number);
			lengths[2]=greatestLength;
			
			j = 0; greatestLength=0;
			while(j<lengths.length) {
				if(lengths[j]>greatestLength) { // So see how which win
					// is autodecided by this sorting loop based
					// on the order I put the data in!
					greatestLength=lengths[j]; 
					whichwin=j; 
					}
				j++;
			}

			
			switch(whichwin) {
			case 0:
				buffg.drawString(offName,currX,namecy);

				buffg.drawString(number,currX+fm.stringWidth(offName)/2-fm.stringWidth(number)/2,numcy);
					// drawing unit stats only if we have received them!
					 j = 0;
					while(j<unitStats.length) {
					buffg.drawString(unitStats[j],currX+fm.stringWidth(offName)/2-fm.stringWidth(unitStats[j])/2,numcy+fm.getMaxAscent()*2*(j+1));
					// notice *j on the end to increment the y each time!
					// I hope that my spacing is right...
					j++;
					}
					

					// so the thing needs to be incremented by cx + start of word + word length + tab
					currX+=fm.stringWidth(offName)+fm.stringWidth("\t"); 
				break;
			case 1:
				
				buffg.drawString(offName,currX+fm.stringWidth(number)/2-fm.stringWidth(offName)/2,namecy);

				buffg.drawString(number,currX,numcy);
				 j = 0;
					while(j<unitStats.length) {
					buffg.drawString(unitStats[j],currX+fm.stringWidth(number)/2-fm.stringWidth(unitStats[j])/2,numcy+fm.getMaxAscent()*2*(j+1));
					// notice *j on the end to increment the y each time!
					// I hope that my spacing is right...
					j++;
					}
				// so the thing needs to be incremented by cx + start of word + word length + tab
				currX+=fm.stringWidth(number)+fm.stringWidth("\t"); 
				break;
			case 2:

				buffg.drawString(offName,currX+greatestLength/2-fm.stringWidth(offName)/2,namecy);

				buffg.drawString(number,currX+greatestLength/2-fm.stringWidth(number)/2,numcy);
				 j = 0;
				 	buffg.setColor(Color.green);
					while(j<unitStats.length-1) {
					buffg.drawString(unitStats[j],currX,numcy+fm.getMaxAscent()*2*(j+1));
					// notice *j on the end to increment the y each time!
					// I hope that my spacing is right...
					j++;
					}
					
					String weaps = unitStats[j];
					
					buffg.drawString(weaps.substring(0,weaps.indexOf(",")), currX,numcy+fm.getMaxAscent()*2*(j+1));
					
					weaps = weaps.substring(weaps.indexOf(",")+1,weaps.length());
					j++; // for the last line just entered since it had "wpns" on it
					// and couldn't be part of this below loop.
					while(!weaps.equals("")) {
						buffg.drawString(weaps.substring(0,weaps.indexOf(",")), currX+fm.stringWidth("Wpns: "),numcy+fm.getMaxAscent()*2*(j+1));
						weaps = weaps.substring(weaps.indexOf(",")+1,weaps.length());
						j++;
					}
				buffg.setColor(Color.red);
				currX+=greatestLength+fm.stringWidth("\t"); 

				break;
			}
			
			}
			
			
			
			i++;
			
		}
		
		breakout = false;
		 i = 0;
			if(!superIndent) {
				 namecy += fm.getMaxAscent()*8;
				 numcy+=fm.getMaxAscent()*8; 
				} else {
					 namecy += (16+superIndentAmt)*fm.getMaxAscent(); // need an extra line down to print the five lines of stat code.
					 numcy+=(16+superIndentAmt)*fm.getMaxAscent(); 
				}
				
			
		 buffg.setColor(Color.white);
		 if(!support&&resupplyID==-1) { // support and resupply runs do not need tread here, so -1 ensures
			 // that on the resupply front, !support on the support front!
			 if(scout==1) {
					buffg.drawString(townDef + "'s Bldg Amts:",cx,numcy);
					 currX = cx+fm.stringWidth(townDef+"'s Bldg Amts:\t");

			 } else {
			buffg.drawString(townDef + " before:",cx,numcy);
			buffg.drawString(townDef + " after:",cx,numcy+fm.getMaxAscent()*2);
			 currX = cx+fm.stringWidth(townDef+" before:\t");
		 
			 }
		 buffg.setColor(Color.blue);
			String defName;

		while(i<defCount-1) {
			defsttemp = defsttemp.substring(defsttemp.indexOf(",")+1,defsttemp.length());
			 number = defsttemp.substring(0,defsttemp.indexOf(",")); 
			
			 

				deffitemp = deffitemp.substring(deffitemp.indexOf(",")+1,deffitemp.length());
				if(scout!=1)
				numberlost = deffitemp.substring(0,deffitemp.indexOf(",")); 
				else numberlost="";
			defNamestemp = defNamestemp.substring(defNamestemp.indexOf(",")+1,defNamestemp.length());
			if(!defender&&scout==0&&number.equals("0")) defName = "???";
			// no use in ??? out buildings, which happens with scout==1.
			else defName = defNamestemp.substring(0,defNamestemp.indexOf(","));
			// no need for ??? for defensive purposes!!!
			
			if(i>0&&i%6==0) {
				// so if i is a multiple of six, we want to begin a new line, for support units.

				 namecy += fm.getMaxAscent()*8;
				 numcy+=fm.getMaxAscent()*8; 
				if(scout==1) currX = cx+fm.stringWidth(townDef+"'s Bldg Amt:\t");
				else currX = cx+fm.stringWidth(townOff+" before:\t");
				
				 }
			
			// need to format correctly - whichever is larger controls the pie, so to speak!
			if(defName.length()>number.length()) {
			buffg.drawString(defName,currX,namecy);

			buffg.drawString(number,currX+fm.stringWidth(defName)/2-fm.stringWidth(number)/2,numcy);
			buffg.drawString(numberlost,currX+fm.stringWidth(defName)/2-fm.stringWidth(numberlost)/2,numcy+fm.getMaxAscent()*2);

			// so the thing needs to be incremented by cx + start of word + word length + tab
			currX+=fm.stringWidth(defName)+fm.stringWidth("\t"); 
			} else {
				buffg.drawString(defName,currX+fm.stringWidth(number)/2-fm.stringWidth(defName)/2,namecy);

				buffg.drawString(number,currX,numcy);
				buffg.drawString(numberlost,currX+fm.stringWidth(number)/2-fm.stringWidth(numberlost)/2,numcy+fm.getMaxAscent()*2);

				// so the thing needs to be incremented by cx + start of word + word length + tab
				currX+=fm.stringWidth(number)+fm.stringWidth("\t"); 
			}
			i++;
			
		}
		 }
		
	}*/
	public void setDebris(boolean debris) {
		this.debris = debris;
	}
	public boolean isDebris() {
		return debris;
	}
	public void setNuke(boolean nuke) {
		this.nuke = nuke;
	}
	public boolean isNuke() {
		return nuke;
	}
	public void setNukeSucc(boolean nukeSucc) {
		this.nukeSucc = nukeSucc;
	}
	public boolean isNukeSucc() {
		return nukeSucc;
	}
	public void setOffdig(boolean offdig) {
		this.offdig = offdig;
	}
	public boolean isOffdig() {
		return offdig;
	}
	public void setDefdig(boolean defdig) {
		this.defdig = defdig;
	}
	public boolean isDefdig() {
		return defdig;
	}
	public void setDigMessage(String digMessage) {
		this.digMessage = digMessage;
	}
	public String getDigMessage() {
		return digMessage;
	}

}
