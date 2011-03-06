package BHEngine;

import java.lang.reflect.Method;
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
			
		
			Method hourly = p.getClass().getMethod(method,classes);
			hourly.invoke(p,params);
		
		} catch(Exception exc) { exc.printStackTrace(); } 
	}
}
