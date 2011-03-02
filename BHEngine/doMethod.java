package BHEngine;

import java.lang.reflect.Method;
import java.util.Hashtable;

public class doMethod  extends Thread {

	Object p; String method; Object[] params;
	public doMethod(String iteratorID, Object p, String method, Object... params) {
		super(iteratorID);
		this.method=method;
		this.params=params;
		this.p=p;
	}
	public void run() {

		try {
			
		
			Method hourly = p.getClass().getMethod(method);
			hourly.invoke(p,params);
		
		} catch(Exception exc) { exc.printStackTrace(); } 
	}
}
