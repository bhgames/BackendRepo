package Gigabyte;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import BHEngine.GodGenerator;
import BHEngine.PlayerScript;
import BHEngine.Timer;
import BattlehardFunctions.BattlehardFunctions;


public class Gigarun {
	// static String url = "jdbc:mysql://184.106.205.252:3306/bhdb"; // REAL MODE MYSQL SERVER
		static String url = "jdbc:mysql://localhost:3306/bhdb"; // TEST MODEMYSQL SERVER ADDRESS
	//	static String url = "jdbc:mysql://184.106.231.186:3306/bhdb"; 
		static String pass = "gigawhat";
		static String user = "gigabyte";
		static String domainName = "184.106.231.186:8080";
	//	static String domainName = "localhost:8080"; // test mode is it's own server.
		//static String domainName = "www.aiwars.org"; REAL MODE SERVER ADDRESSS
		static int testSize=10;
		static int sizeLimit=1048576*2;
		static double velocityLimit = 5000;// we do bytes/millisec, so we want 
			// 1MB in a week, so that's gonna be 1048576/(604800000) = .0017
		/*
		public static String bhengsrcdirectory = "/users/arkavon/documents/apache-tomcat-6.0.26/webapps/AIWars/WEB-INF/classes/src/";
		public static String bhengbindirectory = "/users/arkavon/documents/apache-tomcat-6.0.26/webapps/AIWars/WEB-INF/classes/";

		private static String srcdirectory = "/users/arkavon/documents/apache-tomcat-6.0.26/webapps/AIWars/WEB-INF/classes/userscriptsrc/";
		private static String bindirectory = "/users/arkavon/documents/apache-tomcat-6.0.26/webapps/AIWars/WEB-INF/classes/userscriptbin/";
		static String apachedirectory = "/users/arkavon/documents/apache-tomcat-6.0.26/";
	*/
		//-cp lib/servlet-api.jar
		public static String bhengbindirectory = "/usr/share/apache-tomcat-6.0.28/webapps/AIWars/WEB-INF/classes/";
		public static String bhengsrcdirectory = "/usr/share/apache-tomcat-6.0.28/webapps/AIWars/WEB-INF/classes/src/";
		private static String srcdirectory = "/usr/share/apache-tomcat-6.0.28/webapps/AIWars/WEB-INF/classes/userscriptsrc/";
		private static String bindirectory = "/usr/share/apache-tomcat-6.0.28/webapps/AIWars/WEB-INF/classes/userscriptbin/";
		public static String apachedirectory = "/usr/share/apache-tomcat-6.0.28/";
	public static void main(String args[]) throws IOException {
	/*	System.out.println("trying.");
		int pid = Integer.parseInt(args[0]);
		try {
			FileWriter fw = new FileWriter(apachedirectory + "logs/compileLog-" + pid + ".out");
			fw.write(args[0]);
			  String playername=null,playerpass=null;

			 Class.forName("com.mysql.jdbc.Driver");
			
		     fw.write("I am opening " + pid + "'s revelations.\n");
		      Connection con =
		                     DriverManager.getConnection(
		                                 url,user, pass);
    		  statusSwitch(con,pid,1); // means there is currently a bun in the oven - no
    		  // further compiles until complete!

		      GodGenerator God=null; Class clazz; Object primer;
		      Object currRevInstance=null; Constructor newSCons; PlayerScript ps=null;
		  	Object currInstInstance; Timer t=null;
		  	long[] currMem;
		  	
		  	Class<?> currInst;
		      BattlehardFunctions bf;
		      long size[] = new long[testSize];
		      double memoryVelocity[] = new double[testSize];
		      int i = 0;
		     try {
		      while(i<testSize) {
		    	  fw.write("Loading God " + i );
		    	  fw.write("Total memory is " + Runtime.getRuntime().totalMemory());
		       God = new GodGenerator(pid,url,user,pass);
		      // okay once it's up and running...
		    //   String status = God.status;
		      while(!God.loaded) {
		    	
		      }
		      // now we should have a smaller God ready to load our program.
				 bf = new BattlehardFunctions(God,God.godPlayer,"4p5v3sxQ",false);
				 // get that shit before we even load it!
				
				  long startingMemoryUse = getUsedMemoryBef();
		    	  if(i==0) {
		    		  
		    		 //  * So we only load the program once and keep the player script around
		    		  // * afterwards, resetting it to a different copy of the player each go around!
		    		   
				      if(God.godPlayer.getPs().loadProgram()) {
				    	  ps=God.godPlayer.getPs();
				      } else{
				    	  fw.write("God has failed to load the program.\n");
				    	  fw.close();
				    	  statusSwitch(con,pid,-1); // Means failed. 
				    	  God.killGod=true;
				    	  break;
				      }
		    		  playername=God.godPlayer.getUsername();
		    		  playerpass=God.godPlayer.getPassword();
		    		  fw.write("God was able to load the program.\n");

		    		  statusSwitch(con,pid,2); // means compile successful, but now in processing.
		    	  } else {
		    		  // resetting it.
		    		  ps.resetPS(God.godPlayer);
		    	  }
		    	  clazz = God.godPlayer.getPs().currRev;
		      
		   
		    	  try {
		    	  
			        
			        
		      
					if(currRevInstance!=null) ((Thread) currRevInstance).stop(); // to kill the old one off.
					 newSCons = clazz.getConstructor(BattlehardFunctions.class);
					 
					 if(t!=null) {
							
							t.stopTimer();
					 }  
					int millis = (int) Math.round((((double) i)/testSize)*300);
					//if(rand<1) rand = 1;
					currMem = new long[millis];
				//	  long startingMemoryUse = getUsedMemoryBef();
					  // we want to include classize, because if we load many classes,
					  // we run out of room.
					currRevInstance = newSCons.newInstance(bf);
					fw.write("Starting program for God " + i);
					((Thread) currRevInstance).start();
					t = new Timer(millis,100); // doin' it in millis. 300*100=30000 = 5 mins.
				//	  long startingMemoryUse = getUsedMemory();

					
				//	 * Now here, we need a timer that is random between 1 minute and 5 minutes.
					// * If the thread ends before then, then we need 
					 
					fw.write("currMem is of size " + currMem.length);
					fw.write("Thread is now " + ((Thread) currRevInstance).isAlive());
					int lastT = t.getT();
					while(((Thread) currRevInstance).isAlive()) {
						if(t.isDone()) break;
						// we use the timer so we can do velocities.
							try {
								if(t.getT()>=0&&lastT!=t.getT()) {
								lastT=t.getT();
								currMem[t.getT()] = getUsedMemory(); // so we don't get motherfuckin out of bounds
							//	fw.write("Stored: " +currMem[t.getT()]);
							//	gc();

								}
							}catch(ArrayIndexOutOfBoundsException exc) {
								fw.write("Tried to grab a bad array entry.");
								// we do -1, because the size is not the index, the size is the index-1.
							}
					}
					
					 long endingMemoryUse = getUsedMemoryAft();
			         float approxSize = (endingMemoryUse - 
			                             startingMemoryUse);
					if(t.isDone()) {
						((Thread) currRevInstance).stop();
						fw.write("Timer ended first.");
					}
					else fw.write("Program ended first.");
					fw.write("Program for God " + i + " has ended it's testing period.\n");
					
			         int j = 0;
			         double avg=0;
			         while(j<currMem.length-1) {
			        	
			        	 if(currMem[j]!=0) {
			        		 
			        	//	  * How to get the velocity:
			        		//  * You take the velocity at each point between i and i+1.
			        		  
			        		 avg +=  ((double) (currMem[j+1]-currMem[j])/currMem.length);
			        		 fw.write("mem difference: " + (currMem[j]-currMem[j+1]));
			        	 }
		        		 j++;

			         }
			         
			         memoryVelocity[i]=avg;
			         size[i] = Math.round(approxSize);
			         fw.write("memoryVelocity for i is " + memoryVelocity[i]+"\n");
			         fw.write("size for i is " + size[i]+"\n");

					}
					catch(NoSuchMethodException exc) { exc.printStackTrace();break;}
					catch(InvocationTargetException exc) { exc.printStackTrace(); break;}
					catch(IllegalAccessException exc) {exc.printStackTrace();break;} // JESUS SO MANY DAMN EXCEPTION HANDLERS!
					catch(InstantiationException exc) { exc.printStackTrace();  break;}
		        
		     
					
		      // do something with size here
		      
		      
		      
		      // Kill off the God for the next one to exist!
		      God.killGod=true;
		      try {
				God.holdGod.join(100000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			God.holdGod.stop(); // we wait, if not, we kill.
		
				t.stopTimer();
				clazz=null;primer=null;currRevInstance=null;newSCons=null;
				currInstInstance=null;t=null;currMem=null; bf = null; currInst=null;
				ps.makeNull();
		      God=null;
		      gc();
		      i++;
		      }
		     } catch(OutOfMemoryError exc) { 
		    	  
		    	  if(currRevInstance!=null)
				((Thread) currRevInstance).stop();
		    	  if(God.holdGod!=null)
				God.holdGod.stop(); // we wait, if not, we kill.
				   try {
						God.holdGod.join(100000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					gc();
					fw.write("Huge memory leak detected.");
					
					eraseCmdRestart(con,pid,"You have a huge memory leak."); 

					}
		      
		      
		      // okay now we should have a size and memory velocity to work with.
		      fw.write("Now we are doing calculations to see if size and velocity are okay.\n");
		      i = 0;
		      long avgSize=0;
		      double avgV=0;
		      double avgVAggregate=0;
		      while(i<testSize) {
		    	  fw.write("size " + i + " is " + size[i]);
		    	  avgSize+=(long) Math.round(((double) size[i])/size.length);
		    	  if(i>1)
		    	  avgVAggregate+=(size[i]-size[i-1]);
		    	  avgV+=memoryVelocity[i]/memoryVelocity.length; // CAN ALSO GET V BY TAKING TIMES TOO WITH SIZE
		    	  i++;
		      }
		      boolean passed =true;
		      avgVAggregate/=testSize;
		      fw.write("AvgSize is: " + avgSize + " and avgV is " + avgV + " and avgVAggregate is " + avgVAggregate + ".\n");
		      if(avgSize>sizeLimit) {
		    	  eraseCmdRestart(con,pid,"You have exceeded the memory size limit of " + sizeLimit + " bytes." +
		    			  " Try minimizing your use of large objects and making sure all of your unused object instances are released" +
		      		" for garbage collection. See the board's technical section for more on this issue, or contact support.");
		    	  passed=false;
		      }
		      		
		      if(Math.abs(avgV)>velocityLimit) {eraseCmdRestart(con,pid, "You have a memory leak somewhere in your program. You probably" +
		      		" are committing some simple mistakes in memory and object management that are keeping you from passing Gigabyte's" +
		      		" tests. See the board's technical section for more on this issue, or contact support.");
	    	  passed=false;
		      }
		      if(passed) {
		    	  
		    	  statusSwitch(con,pid,0);
		    	 sendCompileReq(playername,playerpass);
		      }

				 fw.write("Quitting.");

				 fw.close();
		      con.close();
		 		  } catch(SQLException exc2) { exc2.printStackTrace();}
			 catch(ClassNotFoundException exc2) { exc2.printStackTrace(); }
			
			 
				System.exit(1);*/
	}
	private static long getUsedMemory() {
		// no fuckin gc.
	     // gc();
	      long totalMemory = Runtime.getRuntime().totalMemory();
	    //  gc();
	      long freeMemory = Runtime.getRuntime().freeMemory();
	      long usedMemory = totalMemory - freeMemory;
	      return usedMemory;
	   }
	private static long getUsedMemoryBef() {
		// we clean right before we go.
	      gc();
	      long totalMemory = Runtime.getRuntime().totalMemory();
	      gc();
	      long freeMemory = Runtime.getRuntime().freeMemory();
	      long usedMemory = totalMemory - freeMemory;
	      return usedMemory;
	   }
	private static long getUsedMemoryAft() {
		// no gc, we want it preserved.
	  //   gc();
	      long totalMemory = Runtime.getRuntime().totalMemory();
	    //  gc();
	      
	      long freeMemory = Runtime.getRuntime().freeMemory();
	      long usedMemory = totalMemory - freeMemory;
	      gc();
	      return usedMemory;
	      
	   }
	   private static void gc() {
	      try {
	         System.gc();
	         Thread.currentThread().sleep(100);
	         System.runFinalization();
	         Thread.currentThread().sleep(100);
	         System.gc();
	         Thread.currentThread().sleep(100);
	         System.runFinalization();
	         Thread.currentThread().sleep(100);
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	   }
	public static void eraseCmdRestart(Connection con, int pid, String error) {
		  try {
			  Statement stmt = con.createStatement();
	      stmt.execute("start transaction;");
	      System.out.println("Trying to send..." + error);
		   //   fw.write("I am telling the player to update.");
		      stmt.executeUpdate("update revelations set error = \"" + error + "\",status=-1  where pid = "+pid+";");

		//      stmt.executeUpdate("update player set outputchannel = 'true' where pid = " + player.ID +";");	
		      // above unneeded as player does this for us by having battlehardfunctions called notifyViewer
		      // when it wants attention. What if this doesn't get a chance to do instructions = null before
		      // player does callSync? Possible to get duplicate commands but highly unlikely.
		      // This must be this way because then programs of the user can also notify the Viewer
		      // of any changes needed!
				stmt.executeUpdate("commit;"); 				
				stmt.close(); 
		  } catch(SQLException exc2) { exc2.printStackTrace();}
				
				 
	}
	public static void statusSwitch(Connection con, int pid, int status) {
		  try {
			  Statement stmt = con.createStatement();
	      stmt.execute("start transaction;");
		   //   fw.write("I am telling the player to update.");
		      stmt.executeUpdate("update revelations set status = " + status + "  where pid = "+pid+";");

		//      stmt.executeUpdate("update player set outputchannel = 'true' where pid = " + player.ID +";");	
		      // above unneeded as player does this for us by having battlehardfunctions called notifyViewer
		      // when it wants attention. What if this doesn't get a chance to do instructions = null before
		      // player does callSync? Possible to get duplicate commands but highly unlikely.
		      // This must be this way because then programs of the user can also notify the Viewer
		      // of any changes needed!
				stmt.executeUpdate("commit;"); 				
				stmt.close(); 
		  } catch(SQLException exc2) { }
				
				 
	}
	public static int sendCompileReq(String username, String password) {
		HttpClient httpClient = new HttpClient(); // may need to be s, or no s, or no 8080
		//http://184.106.231.186:8080/AIWars/GodGenerator?reqtype=compileProgram&UN=triggerhappy&Pass=noah&code=4p5v3sxQ
		GetMethod method = new GetMethod("http://"+domainName + "/AIWars/GodGenerator?reqtype=compileProgram&UN=" + username+"&Pass="+password + "&code=4p5v3sxQ");
	/*	method.addRequestHeader(new Header("reqtype","compileProgram"));
		method.addRequestHeader(new Header("UN", username));
		method.addRequestHeader(new Header("Pass", password));*/

		try {
			int statusCode = httpClient.executeMethod( method );
			return statusCode;
		//	fw.write(statusCode);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
}
