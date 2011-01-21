package BHEngine;

public class RecordKeeper implements Runnable {

	Thread t; 

	GodGenerator g;
	public RecordKeeper(GodGenerator g) {
		this.g=g; // so now we have access to players' databanks.
		t = new Thread(this, "Record Keeper AI");
		t.start();
	}
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
