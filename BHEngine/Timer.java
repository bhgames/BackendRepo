package BHEngine;

public class Timer implements Runnable {
	private int t;
	Thread st; // Quick timing device, you name your seconds.
	 boolean done = false;
	boolean upCount;static int synchTime = 5;
	public int waitTime=1000; // default value.
	public Timer(int t) {
		this.t=t;
		upCount=false;
		 st = new Thread(this,"Timer"+Math.round(100*Math.random()));
		st.start();
	}
	public Timer() {
		
		t=0;upCount=true; // This is an up counting timer.
		 st = new Thread(this,"Timer"+Math.round(100*Math.random()));
			st.start();
	}
	public Timer(int t, int waitTime) {
		this.t=t;
		this.waitTime=waitTime;
		upCount=false;
		 st = new Thread(this,"Timer"+Math.round(100*Math.random()));
		st.start();
			
	}
	
	public void run() {
		while(getT()>=0) {
			try {
			st.sleep(waitTime); } catch(InterruptedException exc) { break; }
			if(done) break; // in case I set done to true to cut out the timer.
			// Most of the time, we're in sleep, so timer won't go up if you set done on, it'll round down, so to speak
			// except in rare occurences when it's at t++ when we set done to true, but even then, it still won't add.
			if(!upCount) t--; else t++;
			
			
			
		}
		done=true;
	}
	
	public boolean isDone() {
		return done;
	}
	public void stopTimer() {
		done=true;
	}
	/*
	public void resetTimer() {
		done=false;
		t=0;
		upCount=true;
		st.start();
		
	}*/
	
	public int getT() {
		
		return t;
	}
}
