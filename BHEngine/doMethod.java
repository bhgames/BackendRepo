package BHEngine;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Hashtable;

import BattlehardFunctions.UserRaid;

public class doMethod  extends Thread {

	Object p; String method; Object[] params; Class classes[]=null;
	public doMethod(String iteratorID, Object p, String method, Object... params) {
		super(iteratorID);
		this.method=method;
		this.params=params;
		if(params!=null) {
		classes = new Class[params.length];
		int i = 0;
		while(i<params.length) {
			classes[i]=params[i].getClass();
			i++;
		}
		}
		this.p=p;
	}
	public void run() {

		try {
			Date currTime = new Date();
			long before = currTime.getTime();		
			Method hourly = p.getClass().getMethod(method,classes);
			currTime = new Date();
			long afterTime = currTime.getTime();
			System.out.println("Finding the method" + method + " took " + (afterTime-before) + " ms.");
			 currTime = new Date();
			 before = currTime.getTime();		
			hourly.invoke(p,params);
			 currTime = new Date();

			 afterTime = currTime.getTime();
				System.out.println("Running the method" + method + " took " + (afterTime-before) + " ms.");

		} catch(Exception exc) { exc.printStackTrace(); } 
	}
}
