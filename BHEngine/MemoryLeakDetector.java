package BHEngine;

import java.util.Hashtable;

public class MemoryLeakDetector implements Runnable {
	Thread t;
	GodGenerator g;
		public MemoryLeakDetector(GodGenerator g) {
			t = new Thread(this, "MemoryLeakDetector");
			this.g=g;
			t.start();
		}
		
		public void run() {
			Hashtable r; boolean prog; Thread rev;
			for(;;) {
				for(int i = 0; i<g.programs.size(); i++) { 
					 r = g.programs.get(i);
					 int pid = (Integer) g.programs.get(i).get("pid");
					 if(r!=null&&((Object) r.get("Revelations")).getClass().getSuperclass().getName().equals("Revelations.RevelationsAI")) {
						  int pingCounter=0;
					//	  System.out.println("Pinging " + g.getPlayer(pid).getUsername());
						  int j = 0;
						  rev = (Thread) r.get("Revelations");

						  while(j<10) {
							  prog = (Boolean) r.get("sleep");
							  if(!prog&&rev.isAlive()) pingCounter++;
							  
							  try {
								Thread.currentThread().sleep(100); 
								// no program should run for more than 100 ms before sleeping, nor should it sleep less than 1s!
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							  j++;
						  }
						//  System.out.println("Ping counter was " + pingCounter);
						  // it only pings when the thing is not sleeping. if It has a ping count
						  // of more than three, that means the thing wasn't sleeping for 300ms!
						  // Case of memory leak itus?
						  if(pingCounter>7&&(Integer) r.get("pingFails")>2) {
						//	  System.out.println("Shutting down the bastardo " + g.getPlayer(pid).getUsername());
							  rev.stop();
							  g.getPlayer(pid).getPs().b.sendYourself("Your program has been shut down due to a suspected memory leak. No Revelations AI should be out of bf.wait mode for more than 700ms at a time. To minimize your chances of being shut down, try implementing a loop with a bf.wait(" +((int)  g.gameClockFactor) + ") command at the end of every iteration, " +
							  		" as this is a safer coding practice and the game doesn't go any faster than " + ((int) g.gameClockFactor) + "s clock ticks anyway. So iterating your loops every 1 picosecond as you would with no wait command is a bit like driving a super-hummer. Not that driving super-hummers is wrong or anything, because they are really nice vehicles.", "Memory Leak Detected! Revelations shut down by the Gigabyte A.I.!");
						  } else if(pingCounter>7&&(Integer) r.get("pingFails")<=2) {
							  int pingFails  = (Integer) r.get("pingFails");
							  r.put("pingFails",pingFails+1);
						  }
					 }
				}
				try {
					Thread.currentThread().sleep(360*1000); // we get every player in an hour.
				} catch(InterruptedException exc) { exc.printStackTrace(); }
				
			}
		}
}
