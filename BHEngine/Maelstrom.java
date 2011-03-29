package BHEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONStringer;

import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;

public class Maelstrom implements Runnable {
	private ArrayList<Cloud> Clouds = new ArrayList<Cloud>();
	public static int waitTime = (int) (3600/GodGenerator.gameClockFactor);
	Thread t;
	 int cloudNumber;
	 int maxX,maxY;
	 int cloudCounter=waitTime;
	 GodGenerator God;
	 public static int maxCloudSize=GodGenerator.mapTileWidthX;
	 
	public Maelstrom(int numberOfTowns,int maxX, int maxY, GodGenerator God) {
		cloudNumber=(int) Math.round(((double) numberOfTowns)/3.0);
		this.maxX=maxX;
		this.maxY=maxY;
		this.God=God;
		
		try {
			UberStatement stmt = God.con.createStatement();
			ResultSet getLInfo = stmt.executeQuery("select * from cloud");
			while(getLInfo.next()) {
				/*
				 *    ->  cid int unsigned not null auto_increment,
	    ->  size int unsigned not null default 1,
	    ->  incs varchar(30) default '[0,0,0,0,0,0,0,0,0,0,0,0,0]',
	    ->  centerx int not null default 1,
	    ->  centery int not null default 1,
	    ->  ticksToDeath int not null default 5,
	    ->  direction int not null default 0,
	    ->	velocity int not null default 1,
	    ->  primary key(cid)


	public Cloud(int cloudID,int centerx, int centery, double[] incs,
			int size,double velocity,int ticksToDeath, int direction,Maelstrom m) {
				 */
				//System.out.println("Reading in cloud " + getLInfo.getInt(4) + "," + getLInfo.getInt(5));
				getClouds().add(new Cloud(getLInfo.getInt(1),getLInfo.getInt(4),getLInfo.getInt(5),
						PlayerScript.decodeStringIntoDoubleArray(getLInfo.getString(3)), 
						getLInfo.getInt(2),getLInfo.getInt(8),
						getLInfo.getInt(6), getLInfo.getInt(7), this,getLInfo.getBoolean(9)));
			}
			
			
		
			getLInfo.close();stmt.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t = new Thread(this,"Maelstrom");
		t.start();
	}
	public void setCloudNumber(int num) {
		this.cloudNumber=num;
	}
	public void run() {
		Cloud c;
		for(;;) {

		/*
		 * Insert checker to generate directions(weighted based on the cloud's
		 * speed) and call move on them. Then, call adjustCloud to
		 * combine, add, delete any clouds required.
		 */
		checkCloudOverlaps();
		
		if(cloudCounter==waitTime) {
			int i = 0; int moveDir; double moveRand; double chgDirAtAllRand; double moveProb;
			while(i<getClouds().size()) {
				c = getClouds().get(i);
				chgDirAtAllRand = Math.random();
				/*
				 * We want it to be harder to move the heavier you are.
				 * Let's say the max cloud size is ten. Anything beyond that...
				 * is unlikely.
				 */
				moveProb=((double) c.size)/10;
			//	System.out.println("cloud " + i + "bef: " + c.centerx+","+c.centery);

				if(chgDirAtAllRand>moveProb) {
				moveRand = Math.random();
				if(moveRand<=.25) moveDir = 0;
				else if(moveRand<=.5 && moveRand > .25) moveDir = 1;
				else if(moveRand<=.75&& moveRand > .5) moveDir = 2;
				else moveDir = 3;

				c.move(moveDir);
				} else c.move(c.direction);
			//	System.out.println("cloud " + i + "aft: " + c.centerx+","+c.centery);

				i++;
				
			}
			System.out.println("I am cycling.");
			cloudCounter=0;
			adjustClouds();
		}
		cloudCounter++;
		try {
			t.sleep((int) Math.round(GodGenerator.gameClockFactor*1000));
		}catch(InterruptedException exc) { exc.printStackTrace(); }
		}
	}
	
	public void addWeatherEffects(ArrayList<AttackUnit> t1au, ArrayList<AttackUnit> t2au, int x, int y) {
		int i = 0;AttackUnit a;
		Cloud c = getCloud(x,y);
		if(c!=null) {
			c.deleteable=false;// so we can only delete if there are no raids using the cloud!
		while(i<t1au.size()) {
			a = t1au.get(i);
			 double cloudmod=0;
			/*
			 * Combat incs:
			 * 0 conc
			 * 1 armor
			 * 2 cargo
			 * 3 speed
			 * 4 firepower
			 * 5 amm
			 * 6 acc
			 * 
			 */
		/*
			if(c.incs[0]<0)
				a.setConcealment(a.getConcealment()
						+ (int) Math.round((c.incs[0]*(1.0+cloudmod)*((double) a.getConcealment()))));
			else
				a.setConcealment(a.getConcealment()
						+ (int) Math.round((c.incs[0]*(1.0)*((double) a.getConcealment()))));*/
				if(c.incs[1]<0)
				a.setArmor(a.getArmor() +  (int) Math.round((c.incs[1]*(1.0+cloudmod)*((double) a.getArmor()))));
				else
				a.setArmor(a.getArmor() +  (int) Math.round((c.incs[1]*(1.0)*((double) a.getArmor()))));

				if(c.incs[2]<0)
				a.setCargo(a.getCargo() +  (int) Math.round((c.incs[2]*(1.0+cloudmod)*((double) a.getCargo()))));
				else
					a.setCargo(a.getCargo() +  (int) Math.round((c.incs[2]*(1.0)*((double) a.getCargo()))));

				if(c.incs[3]<0)
				a.setSpeed(a.getSpeed() +  (int) Math.round((c.incs[3]*(1.0+cloudmod)*((double) a.getSpeed()))));
				else
					a.setSpeed(a.getSpeed() +  (int) Math.round((c.incs[3]*(1.0)*((double) a.getSpeed()))));

				if(c.incs[4]<0)
				a.setAttackDamage(a.getAttackDamage() +  (int) Math.round((c.incs[4]*(1.0+cloudmod)*((double) a.getAttackDamage()))));
				else
					a.setAttackDamage(a.getAttackDamage() +  (int) Math.round((c.incs[4]*(1.0)*((double) a.getAttackDamage()))));

				/*if(c.incs[5]<0)
				a.setAmmo(a.getAmmo() +  (int) Math.round((c.incs[5]*(1.0+cloudmod)*((double) a.getAmmo()))));
				else
					a.setAmmo(a.getAmmo() +  (int) Math.round((c.incs[5]*(1.0)*((double) a.getAmmo()))));

				if(c.incs[6]<0)
				a.setAccuracy(a.getAccuracy() +  (int) Math.round((c.incs[6]*(1.0+cloudmod)*((double) a.getAccuracy()))));
				else
					a.setAccuracy(a.getAccuracy() +  (int) Math.round((c.incs[6]*(1.0)*((double) a.getAccuracy()))));*/


			i++;
		}
		i=0;
		
		while(i<t2au.size()) {
			a = t2au.get(i);
			 double cloudmod=0;

			/*
			 * Combat incs:
			 * 0 conc
			 * 1 armor
			 * 2 cargo
			 * 3 speed
			 * 4 firepower
			 * 5 amm
			 * 6 acc
			 * 
			 */
			if(a.getType()!=4&&a.getType()!=0)
				
		/*	if(c.incs[0]<0)
				a.setConcealment(a.getConcealment()
						+ (int) Math.round((c.incs[0]*(1.0+cloudmod)*((double) a.getConcealment()))));
			else
				a.setConcealment(a.getConcealment()
						+ (int) Math.round((c.incs[0]*(1.0)*((double) a.getConcealment()))));*/
				if(c.incs[1]<0)
				a.setArmor(a.getArmor() +  (int) Math.round((c.incs[1]*(1.0+cloudmod)*((double) a.getArmor()))));
				else
				a.setArmor(a.getArmor() +  (int) Math.round((c.incs[1]*(1.0)*((double) a.getArmor()))));

				if(c.incs[2]<0)
				a.setCargo(a.getCargo() +  (int) Math.round((c.incs[2]*(1.0+cloudmod)*((double) a.getCargo()))));
				else
					a.setCargo(a.getCargo() +  (int) Math.round((c.incs[2]*(1.0)*((double) a.getCargo()))));

				if(c.incs[3]<0)
				a.setSpeed(a.getSpeed() +  (int) Math.round((c.incs[3]*(1.0+cloudmod)*((double) a.getSpeed()))));
				else
					a.setSpeed(a.getSpeed() +  (int) Math.round((c.incs[3]*(1.0)*((double) a.getSpeed()))));

				if(c.incs[4]<0)
				a.setAttackDamage(a.getAttackDamage() +  (int) Math.round((c.incs[4]*(1.0+cloudmod)*((double) a.getAttackDamage()))));
				else
					a.setAttackDamage(a.getAttackDamage() +  (int) Math.round((c.incs[4]*(1.0)*((double) a.getAttackDamage()))));

			/*	if(c.incs[5]<0)
				a.setAmmo(a.getAmmo() +  (int) Math.round((c.incs[5]*(1.0+cloudmod)*((double) a.getAmmo()))));
				else
					a.setAmmo(a.getAmmo() +  (int) Math.round((c.incs[5]*(1.0)*((double) a.getAmmo()))));

				if(c.incs[6]<0)
				a.setAccuracy(a.getAccuracy() +  (int) Math.round((c.incs[6]*(1.0+cloudmod)*((double) a.getAccuracy()))));
				else
					a.setAccuracy(a.getAccuracy() +  (int) Math.round((c.incs[6]*(1.0)*((double) a.getAccuracy()))));*/

			

			i++;
		}
		
		}
	}
	public void removeWeatherEffects(Raid r) {
		/* THIS METHOD IS NO LONGER USED.
		 * The question is, given one number, and a percentage of another number,
		 * how do I find that other number? So I say 110 is 110% of what number.
		 *  Well I know that percentage*number=110, so I just do number = 110/percentage.
		 *  in this case, the number is 100.
		 * 
		 */
		int i = 0;AttackUnit a;
		Cloud c = getCloud(r.getTown2().getX(),r.getTown2().getY());
		if(c!=null) {
		while(i<r.getAu().size()) {
			a = r.getAu().get(i);
			/*
			 * Combat incs:
			 * 0 conc
			 * 1 armor
			 * 2 cargo
			 * 3 speed
			 * 4 firepower
			 * 5 amm
			 * 6 acc
			 * 
			 */
			
		//	a.setConcealment(Math.round(((double) a.getConcealment() )/ (1.0+c.incs[0])));
			a.setArmor(Math.round(((double) a.getArmor() )/ (1+c.incs[1])));
			a.setCargo(Math.round(((double) a.getCargo() ) / (1+c.incs[2])));
			a.setSpeed(Math.round(((double) a.getSpeed() ) / (1+c.incs[3])));
			a.setAttackDamage(Math.round(((double) a.getAttackDamage() )/ (1+c.incs[4])));
		//	a.setAmmo(Math.round(((double) a.getAmmo() )/ (1+c.incs[5])));
	//		a.setAccuracy(Math.round(((double) a.getAccuracy() ) / (1+c.incs[6])));

			i++;
		}
		i=0;
		while(i<r.getTown2().getAu().size()) {
			a = r.getTown2().getAu().get(i);
			/*
			 * Combat incs:
			 * 0 conc
			 * 1 armor
			 * 2 cargo
			 * 3 speed
			 * 4 firepower
			 * 5 amm
			 * 6 acc
			 * 
			 */
			
		//	a.setConcealment(Math.round(((double) a.getConcealment() )/ (1.0+c.incs[0])));
			a.setArmor(Math.round(((double) a.getArmor() )/ (1+c.incs[1])));
			a.setCargo(Math.round(((double) a.getCargo() ) / (1+c.incs[2])));
			a.setSpeed(Math.round(((double) a.getSpeed() ) / (1+c.incs[3])));
			a.setAttackDamage(Math.round(((double) a.getAttackDamage() )/ (1+c.incs[4])));
		//	a.setAmmo(Math.round(((double) a.getAmmo() )/ (1+c.incs[5])));
	//		a.setAccuracy(Math.round(((double) a.getAccuracy() ) / (1+c.incs[6])));

			i++;
		}
		c.deleteable=true; // so we can only delete if there are no raids using the cloud!
		}
	}
	
	public double getEngineerEffect(int x, int y) {
		Cloud c = getCloud(x,y);
		if(c!=null)
		return c.incs[11];
		else return 0;
	}
	public boolean EMPed(Player p) {
		int i = 0; Cloud c;
		while(i<p.towns().size()) {
			c = getCloud(p.towns().get(i).getX(),p.towns().get(i).getY());
			if(c!=null&&c.emp) return true;
			i++;
		}
		return false;
	}
	public double getTraderEffect(int x, int y) {
		Cloud c = getCloud(x,y);
		if(c!=null)
		return c.incs[12];
		else return 0;
	}
	public double getScholarEffect(Player p) {
	/*	Cloud c = getCloud(x,y);
		if(c!=null)
		return c.incs[13];
		else */
		
		return 0;
	}
	public double[] getResEffects(double resIncs[], int x, int y) {
		
		double effects[] = new double[5];
		int i = 0;
		Cloud c = getCloud(x,y);
		if(c==null) return resIncs;
		while(i<4) {
			effects[i]=resIncs[i]+c.incs[i+7]*resIncs[i];
			effects[i]=resIncs[i]+.05*resIncs[i];
			i++;
		}
		effects[4]=0;
		return effects;

	}
	
	private Cloud getCloud(int x, int y) {
		int i = 0; Cloud c;
		while(i<getClouds().size()) {
			c=getClouds().get(i);
			int boundsxm = c.centerx-(int) (Math.round(((double) c.size)/2));
			int boundsxp = boundsxm+c.size;
			int boundsym = c.centery-(int) (Math.round(((double) c.size)/2));
			int boundsyp = boundsym+c.size;
			
			if(x>=boundsxm&&x<=boundsxp&&y>=boundsym&&y<=boundsyp) {
				return c; // Because any other clouds in the area will soon be merged
				// with c!
			}
			i++;
		}
		
		return null;
	}
	public void checkCloudOverlaps() {
		int i = 0; Cloud b,c;
	/*	while(i<getClouds().size()) {
			
		//	System.out.println("Cloud " + i + " is at " + getClouds().get(i).centerx +"," + getClouds().get(i).centery);
		int j = 0;
			while(j<getClouds().size()) {
				if(i!=j&&getClouds().get(j).centerx==getClouds().get(i).centerx&&getClouds().get(j).centery==getClouds().get(i).centery){
			//		System.out.println("This cloud has a copy at " + j);
				}
				j++;
			}
			i++;
		}*/ 
		i=0;
		while(i<getClouds().size()) {
			c = getClouds().get(i);
		//	System.out.println("Checking vicinity of cloud "+ c.centerx+","+c.centery);
			int j = 0;
			while(j<getClouds().size()) {
				b = getClouds().get(j);
				if(j!=i) {
				int boundsxm = c.centerx-(int) (Math.round(((double) c.size)/2));
				int boundsxp = boundsxm+c.size;
				int boundsym = c.centery-(int) (Math.round(((double) c.size)/2));
				int boundsyp = boundsym+c.size;
				int boundsxmb = b.centerx-(int) (Math.round(((double) b.size)/2));
				int boundsxpb = boundsxmb+b.size;
				int boundsymb = b.centery-(int) (Math.round(((double) b.size)/2));
				int boundsypb = boundsymb+b.size;
				// so if one of my x bounds is within your bounds and if one of my y bounds is within your bounds
				// then merge like a player.
				if((boundsxmb>=boundsxm&&boundsxpb<=boundsxp)&&(boundsymb>=boundsym&&boundsypb<=boundsyp)) {
			//		System.out.println("I've detected " + b.centerx+","+ b.centery + " within cloud " + c.centerx+","+c.centery + " of size " + c.size + ", so I'm merging them.");
					c.merge(b);
					b.delete();
					getClouds().remove(b);
					
					if(i>j) i--; // if it's greater than the current index is gonna be off by one now
					// after that cloud disappeared!
					j--;

					
				}
				}
				j++;
			}
			i++;
		}
	}
	public void adjustClouds() {
		/*
		 * This adds/deletes/combines relevant clouds.
		 */
		
		// First, check for dead clouds.
		
		int i = 0;
		Cloud c;
		while(i<getClouds().size()) {
			c = getClouds().get(i);
			if(c.ticksToDeath<=0) {
				if(c.deleteable) {
					
				c.delete();
				getClouds().remove(c);
				i--;
				}
			} else {
				c.ticksToDeath--;
				c.reset();
			}
			i++;
		}
		// now we merge any clouds that are within one another!
		
		checkCloudOverlaps();

		// and just in case we somehow lost clouds in the maelstrom...we check!
		int totalSize=0;
		i=0;
		while(i<getClouds().size()) {
			totalSize+=Math.pow(getClouds().get(i).size,2); // Size ten cloud is really 100 clouds.
			i++;
		}
		System.out.println("total cloud size is " + totalSize + " and limit is " + cloudNumber);
		while(totalSize<cloudNumber) {
			c= new Cloud(this,0,0,0,false);
			totalSize++;
		}
		
		
		
	}
	
	public void addCloud(double percent, int x, int y, boolean emp) {
		if(percent>.9) percent=.9;
		if(percent<-.9) percent=-.9;
		Cloud c = new Cloud(this,percent,x,y,emp); // Create a radioactive cloud!
		
	}
	public void setClouds(ArrayList<Cloud> clouds) {
		Clouds = clouds;
	}
	public ArrayList<Cloud> getClouds() {
		return Clouds;
	}
	
	public void addToCloudHash(ArrayList<Hashtable> cloudHash, Town t1, int aggregate) {
		
		int i = 0; int t1x = t1.getX(); int t1y = t1.getY();
		Hashtable r; Cloud c;
		while(i<Clouds.size()) {
			c = Clouds.get(i);
			int x = c.centerx; int y = c.centery;
			// If you want the cloud to be visible as soon as it's edge hits the radius, add the size to it,
			// then the radius is autoextended to fit the center IF the edge has touched.
			if((Math.sqrt(Math.pow(x-t1x,2)+Math.pow(y-t1y,2))<=(c.size+(10+aggregate*3*(1))))) {
				boolean foundAny=false,found=false;
				
				// foundAny*!found = don't show town
				// show town = !foundAny + found
				if(!foundAny||found) {
				r = new Hashtable();
				/*
				 * 	double incs[];
						int size;
						int centerx,centery;
						double velocity;
						int cloudID;
						int ticksToDeath;
						int direction;
						static double percentPerCloud=.05;
						static double maxVelocity=5;
						static double maxLifeTime=10;
						boolean deleteable = true;
				 */
				r.put("size",c.size);
				r.put("centerx",c.centerx);
				r.put("centery",c.centery);
				r.put("velocity",c.velocity);
				r.put("cloudID",c.cloudID);
				r.put("ticksToDeath",c.ticksToDeath);
				r.put("direction",c.direction);
				r.put("incs",c.incs);
				
				  int k = 0; boolean add=true;
					 while(k<cloudHash.size()) {
						 if(((Integer) cloudHash.get(k).get("cloudID"))==((Integer) r.get("cloudID"))){
							 add=false;
							 break;
						 }
						 k++;
					 }
					 
					 if(add) {
						
						 cloudHash.add(r);

					 }
			}
			}
			i++;
		}
	}
	
	public boolean cloudExists(int x, int y) {
		int j = 0; Cloud c;
		
			while(j<getClouds().size()) {
				c = getClouds().get(j);
				int boundsxm = c.centerx-(int) (Math.round(((double) c.size)/2));
				int boundsxp = boundsxm+c.size;
				int boundsym = c.centery-(int) (Math.round(((double) c.size)/2));
				int boundsyp = boundsym+c.size;
				if(x>=boundsxm&&x<=boundsxp&&y>=boundsym&&y<=boundsyp) {
					return true;
				
				}
				
				j++;
			}
			return false;
	}
}

class Cloud {
	double incs[];
	int size;
	int centerx,centery;
	double velocity;
	int cloudID;
	public boolean emp;
	int ticksToDeath;
	int direction;
	static double percentPerCloud=.05;
	static double maxVelocity=5;
	static double maxLifeTime=10;
	boolean deleteable = true;
	Maelstrom m;
	/*
	 create table cloud (
	 cid int unsigned not null auto_increment,
	 size int unsigned not null default 1,
	 incs varchar(30) default '[0,0,0,0,0,0,0,0,0,0,0,0,0]',
	 centerx int not null default 1,
	 centery int not null default 1,
	 ticksToDeath int not null default 5,
	 direction int not null default 0,
	 velocity int not null default 1,
	 primary key(cid)
	 ) Engine=InnoDB;

	 
	 */
	/*
	 * Combat incs:
	 * 0 conc
	 * 1 armor
	 * 2 cargo
	 * 3 speed
	 * 4 firepower
	 * 5 amm
	 * 6 acc
	 * 
	 */
	/*
	 * ResIncs:
	 * 7 is metal
	 * 8 is timber
	 * 9 is manmat
	 * 10 is food
	 * 11 is Engineer Effectiveness(Bldg times)
	 * 12 is Trader Effectiveness(Better SM trades)
	 * 13 is Scholar Effectiveness(Increased Knowledge Percentage)[Implement scholars first.]
	 */
	/*
	 * Velocity - speed of 1 indicates how many cloud movements per waitTime. So increasing
	 * the waitTime to 1 hour means 1 cloud movement per hour. Decreasing waitTime to .5 hrs
	 * means 1 per half hour, so the system moves proportional to the basic unit of processing
	 * time for this AI.
	 */
	
	public Cloud(Maelstrom m, double percent, int x, int y,boolean emp) {
		/*
		 * Derive data here for each point
		 */
		/*
		 * Okay so we need to make a few things here. We need to decide an absolute amount
		 * of percent increase. We do this with a static variable - each cloud must distribute
		 * this advantage across seven areas.
		 * 
		 * Then what we need to do is randomly choose a number 0-6 to add to,
		 * and a random amount between 0-.05 to add to it, and keep doing this
		 * as long as we are below the maximum.
		 * 
		 * Then we choose a random velocity under the max, and a starting direction.
		 * 
		 * Then we choose a random wait time before death, this is set as ticksToDeath.
		 */
		this.m=m;
		this.emp=emp;
		incs = new double[14];
		double total=0; double randAdd; int index; double plusmin;
		while(total<percentPerCloud) {
			randAdd = Math.random()*percentPerCloud;
			total+=randAdd;
			plusmin = Math.random();
			index = (int) Math.round(Math.random()*(incs.length-1));
			if(plusmin>.5)
			incs[index]+=randAdd;
			else {
				if(index==1) incs[index]+=randAdd; // 1 is armor, armor has no negative weather,
				// to counterbalance it as an attribute!
				else incs[index]-=randAdd;
			}

			
		}
		
		
		velocity = (int) Math.round(Math.random()*(maxVelocity));
		if(velocity<1) velocity=1; // can't do 0 velocities!
		ticksToDeath = (int) Math.round(Math.random()*(maxLifeTime));
		int tries=0;
		do {
		plusmin = Math.random();
		int mod=1;
		if(plusmin>.5) mod=-1;
		 centerx =  (int) Math.round(mod*Math.random()*(m.maxX));
		 plusmin = Math.random();
		if(plusmin>.5) mod=-1;
		else mod = 1;
		 centery =  (int) Math.round(mod*Math.random()*(m.maxY));
		 tries++;
		} while(m.cloudExists(centerx,centery)&&tries<30);
		// break out if a cloud doesn't exist there or tries >30. 
		// !c + >=30 notted is c<30.
		 size=1; // default size for beginning clouds.
		String incsStr; JSONStringer je = new JSONStringer();
	
		if(percent!=0) { // means we want a preset radioactivity cloud.
			int i = 0;
			while(i<incs.length) {
				incs[i]=percent;
				i++;
			}
			size=GodGenerator.mapTileWidthX;
			velocity=0;
			ticksToDeath=(int) Math.round( ((double) (24*7))*Math.abs(percent)); // lasts for a week afterwards.

			centerx=x; centery=y;
			
		}
		try {
		je.array();
		int i = 0;
		while(i<incs.length) {
			je.value(incs[i]);
			i++;
		}
		
		je.endArray();
		incsStr=je.toString();

		} catch(JSONException exc) { exc.printStackTrace();
		incsStr=null;} // a null value will make the default cloud have zero characteristics
		// and thus be database-safe!
		
		try {

		   
		      UberStatement stmt = m.God.con.createStatement();

		      
		      // First things first. We update the player table.
		      boolean transacted=false;
		      while(!transacted) {
		    	  try {
		      
		      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
		      
		      // let's add this raid and therefore get the rid out of it.
		     
		      stmt.executeUpdate("insert into cloud (incs,centerx,centery,ticksToDeath,direction,velocity,emp,size) values ('" +
		    		  incsStr + "'," + centerx + "," + centery + "," + ticksToDeath
		    		  + "," + direction+ "," + velocity+","+emp+","+size+");");
		      stmt.execute("commit;");

		      
		      ResultSet ridstuff = stmt.executeQuery("select cid from cloud where centerx = " + centerx + " and centery = " + centery + " and size = " + size +";");
		
		      
		      while(ridstuff.next()) {
		    	  int j = 0;
		    	  
		    	  while(j<m.getClouds().size()) {
		    		  if(m.getClouds().get(j).cloudID==ridstuff.getInt(1)) break;
		    		  j++;
		    	  }
		    	  
		    	  if(j==m.getClouds().size()) break; // means we found no raid accompanying this raidID.
		      }
		      
		      	cloudID = ridstuff.getInt(1);
				m.getClouds().add(this); // Coolio. Now to print a message.

		      ridstuff.close();
		      
		      stmt.close(); transacted=true; }
		    	  catch(MySQLTransactionRollbackException exc) { }
		      }// need connection for attackunit adds!
			 } catch(SQLException exc) { exc.printStackTrace(); }

		
	}
	public Cloud(int cloudID,int centerx, int centery, double[] incs,
			int size,double velocity,int ticksToDeath, int direction,Maelstrom m,boolean emp) {
		this.cloudID=cloudID;
		this.ticksToDeath=ticksToDeath;
		// ticksToDeath proportional to wait time!
		this.velocity=velocity;
		this.centerx = centerx;
		this.centery = centery;
		this.incs = incs;
		this.size = size;
		this.direction=direction;
		this.emp=emp;
		this.m=m;
		
		
	}
	
	public void delete() {
		UberStatement stmt;
		try {

	      
	      stmt = m.God.con.createStatement();
	      
	      // First things first. We update the player table.
	      boolean transacted=false;
	      while(!transacted) {
	    	  try {
	    		
	      
	      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
	      
	      // let's add this raid and therefore get the rid out of it.
	      stmt.executeUpdate("delete from cloud where cid = " + cloudID  + ";");
	      
	   
	     	      
	      stmt.execute("commit;");

	      stmt.close(); transacted=true; }
	    	  catch(MySQLTransactionRollbackException exc) {  }
	      }// need connection for attackunit adds!
		}catch(SQLException exc) { exc.printStackTrace();  }
	}
	
	public boolean merge(Cloud c) {
		//SUBSUME THE CLOUDS!
		//max we can get in a merge is 20*.05 = 100%. then the size'll be reset
		// to 10.
		// we check and take care of this, though, making sure you can never get more than 100% in 
		// any direction.
		int i = 0;
		while(i<c.incs.length) {
			incs[i]+=c.incs[i];
			if(incs[i]>.99) incs[i]=.99;
			else if(incs[i]<-.99) incs[i] = -.99;
			// so we can't have too much in one direction or the other!
			i++;
		}
		// okay now we need to add to the size.
		
		
		velocity=((double).75*(c.velocity*c.size+velocity*size))/(c.size+size);
		// weighted average means that if I'm a bigger cloud, I have more clout.
		// We then take this speed and make it 75% of what it was so that
		// even if a bunch of 10speed clouds hit their aggregate speed is not 10
		// too but something much, much less.
		ticksToDeath=(int) Math.round(((double)(c.ticksToDeath*c.size+ticksToDeath*size))/(c.size+size));

		size+=c.size;
		if(size>Maelstrom.maxCloudSize) size=Maelstrom.maxCloudSize;

		// As ticksToDeath naturally decreases as clouds group due to that taking time for
		// random walks, the ticksToDeath, though replenished by a single cloud merge, will
		// gradually die over time!
		
		reset();
		return true;
	}
	public void reset() {
		/*
		 * Saves new values pertaining to this cloud!
		 */
		
		String incsStr; JSONStringer je = new JSONStringer();
		try {
		je.array();
		int i = 0;
		while(i<incs.length) {
			je.value(incs[i]);
			i++;
		}
		
		je.endArray();
		incsStr=je.toString();

		} catch(JSONException exc) { exc.printStackTrace();
		incsStr=null;} // a null value will make the default cloud have zero characteristics
		// and thus be database-safe!
		
		try {

		      
		     UberStatement stmt = m.God.con.createStatement();
		      
		      // First things first. We update the player table.
		      boolean transacted=false;
		      while(!transacted) {
		    	  try {
		    
		      
		      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
		      
		      // let's add this raid and therefore get the rid out of it.
		      stmt.executeUpdate("update cloud set cid =" + cloudID + ", size = " + size + ", incs = '" + incsStr + "', centerx = " +
		       centerx + ", centery = " + centery + ", ticksToDeath = " + ticksToDeath + ", direction = " + 
		       direction +", velocity = " + velocity +  " where cid = " + cloudID  + ";");

		      

		      stmt.execute("commit;");

		      stmt.close(); transacted=true; }
		    	  catch(MySQLTransactionRollbackException exc) {  }
		      }// need connection for attackunit adds!
			}catch(SQLException exc) { exc.printStackTrace(); }		
		
	}
	public void move(int direction) {
		this.direction=direction;
		/*
		 * 0 up
		 * 1 down
		 * 2 left
		 * 3 right
		 */
		
		switch(direction) {
		case 0:
			centery-=velocity;
			break;
		case 1:
			centery+=velocity;
			break;
		case 2:
			centerx-=velocity;
			break;
		case 3:
			centerx+=velocity;
			break;
		}
		int boundsxm = centerx-(int) (Math.round(((double) size)/2));
		int boundsxp = boundsxm+size;
		int boundsym = centery-(int) (Math.round(((double) size)/2));
		int boundsyp = boundsym+size;
		
		if(boundsxm<-m.maxX)  centerx=(int) Math.round(((double) size)/2);
		
		if(boundsxp>m.maxX)  centerx=m.maxX-((int) Math.round(((double) size)/2));
		if(boundsym<-m.maxY)  centery=(int) Math.round(((double) size)/2);
		
		if(boundsyp>m.maxY)  centery=m.maxY-((int) Math.round(((double) size)/2));
		
		reset();
	}
	
	
	
	
}
