package BHEngine;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Hashtable;

import BattlehardFunctions.BattlehardFunctions;

public class createRevelations  extends Thread {

	Constructor newSCons; BattlehardFunctions revb; public Object currRevInstance=null;
	public createRevelations(String iteratorID, Constructor newSCons, BattlehardFunctions revb) {
		super(iteratorID);
		this.newSCons=newSCons;
		this.revb=revb;
	}
	public void run() {

		try {
			Object thisRev=null;
		
				 thisRev =  newSCons.newInstance(revb); // so you can't get a reference to this object until it's done constructing!
				 currRevInstance=thisRev;
		
		} catch(Exception exc) { exc.printStackTrace(); 
			StackTraceElement[] s = exc.getStackTrace();
			int i = 0;String toSend = "";
			while(i<s.length) {
				toSend+=s[i].toString()+"\n";
				i++;
				
			}
			revb.sendYourself(toSend,"Runtime Error from Revelations 2.0");
		} 
	}
}
