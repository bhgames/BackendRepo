package BHEngine;

import java.sql.ResultSet;
import java.sql.Statement;

public class Trader implements Runnable {
	
	Thread t; 
	public long rates[][]=new long[4][4];
	// 0 metal 1 timber 2 man mat 3 food
	// 0-1 is how much metal for one timber
	double dilution = 1; public boolean setup = false;
	GodGenerator g;
	public Trader(GodGenerator g) {
		this.g=g; // so now we have access to players' databanks.
		t = new Thread(this, "Trader AI");
		t.start();
	}
	public void run() {
		Player p; Town town; TradeSchedule ts; UberStatement stmt; ResultSet rs; UberStatement stmt2; ResultSet rs2;
		for(;;) {
			try {
		int i = 0;
		int j = 0;
		long newRates[][]=new long[4][4];
					stmt = g.con.createStatement();
					stmt2 = g.con.createStatement();
					// assuming only one-to-one resource trades...
					rs = stmt.executeQuery("select count(*) from trade where made_at > CURRENT_TIMESTAMP-7*24*360000;");
					rs.next();
					if(rs.getInt(1)>100) {
						rs.close();
					rs = stmt.executeQuery("select * from trade where made_at > CURRENT_TIMESTAMP-7*24*360000;");
					while(rs.next()) {
					//	System.out.println("Found trades.");
						rs2 = stmt2.executeQuery("select * from tradeschedule where tsid = " +rs.getInt(13));
						while(rs2.next()) {
						//	System.out.println("Found tradeschedules.");
							boolean agreed = rs2.getBoolean(7);
							boolean twoway = rs2.getBoolean(6);
							boolean stockMarketTrade = false;
							if(rs2.getInt(1)==rs2.getInt(2)) stockMarketTrade=true;
							if((agreed&&twoway)||stockMarketTrade) { // can't be sending trade if not agreed but hey why not double check.
						// now figure out which kind it is...
						int x = 0; long resourcex=1;
						long metal = rs2.getLong(8);
						long timber = rs2.getLong(9);
						long manmat = rs2.getLong(10);
						long food = rs2.getLong(11);
						long othermetal = rs2.getLong(14);
						long othertimber = rs2.getLong(15);
						long othermanmat = rs2.getLong(16);
						long otherfood = rs2.getLong(17);

						if(metal>0)  { x = 0; resourcex = metal; }
						else if(timber>0){ x = 1;resourcex = timber; }
						else if(manmat>0) { x = 2;resourcex = manmat; }
						else if(food>0){ x = 3;resourcex = food; } 
						
						int y = 0;
						long resourcey =1;
						if(othermetal>0) {y = 0; resourcey = othermetal; }
						else if(othertimber>0){ y = 1;resourcey = othertimber; }
						else if(othermanmat>0){ y = 2;resourcey = othermanmat; }
						else if(otherfood>0){ y = 3;resourcey = otherfood; }
						
						
						if(resourcex<resourcey) {
						newRates[x][y]+=((double) resourcex)*(1.1); 
						// basically we want the thing to be uh...constantly healing itself.
						// so if people trade at 1:10, then we want to slowly change it back.
						// This happens this way by slowly pushing it back. So if people trade 
						// at a rate 1:10, then we will record it as 1.1:10, so next time, it'll go up.
						// it will keep creeping up...and equalizing. Vice versa, I guess,
						// if someone is trading at 10:1, then we want it to record as 10:1.1, so it starts
						// to drift down.
						
						// How do we get upsets? Well, let's say you keep shifting the price till it's
						// equal. Then it falls into the category where resource x starts growing,
						// via the else block. so 1.1:1, then 1.2:1...until 10:1...and then people
						// will begin trading the other way and it will get pushed 10:1.1...and so on.
						
						newRates[y][x]+=resourcey; // so we just record the incoming resource in toIndex/FromIndex style,
						// so we're trading resource y as the to index(y) to the from index(x)
						} else {
						
							newRates[x][y]+=resourcex; 
							newRates[y][x]+=((double) resourcey)*(1.1); 

						}
				//		System.out.println(x+","+y+"="+ resourcex);
					//	System.out.println(y+","+x+"="+ resourcey);

						// So we save in the x to y matrix element that resourcex resources
						// were traded in the y resource direction, and in the y to x matrix element,
						// we save that resourcey resourced were traded in the x direction, for later processing.
						
						
					}
						}
					rs2.close();
					}
					} 
					stmt2.close();
					rs.close();
					stmt.close();
			 i = 0;j=0;
			while(i<newRates.length) {
				j = 0;
				while(j<newRates[i].length) {
				//	System.out.println(i + "," + j + " is " + newRates[i][j]);
					j++;
				}
				i++;
			}
		rates = newRates;
		if(!setup) setup=true; // so we know when the trader is done processing!
		System.out.println("I am loaded.");
		} catch(Exception exc) { exc.printStackTrace(); System.out.println("Trader is safe."); }
		try {
			t.sleep(3600000);
		} catch(InterruptedException exc) {
			exc.printStackTrace();
		}
		}
	}
	
	public long getExchangeResource(int toIndex, int fromIndex, long toResource, int tradeTech,int x, int y) {
		/*
		 * So quoting from up there:
		 * 
						// So we save in the x to y matrix element that resourcex resources
						// were traded in the y resource direction, and in the y to x matrix element,
						// we save that resourcey resourced were traded in the x direction, for later processing.
						 * 
			We know that we take the toIndex fromIndex entry and put it over the fromIndex toIndex entry,
			so that if players are sending 100 timber in the food direction, and they are sending 10 food
			in the timber direction, then the timber(toIndex) to food(fromIndex) ratio is 10,
			or ten to one, and this is achieved by [toIndex][fromIndex]/[fromIndex][toIndex]!
			
			Since we're returning the resource you wanted by the exchange rate,
			then we do rate2/rate1, because 100/10 tofrom/fromto is 10, and 10* 100, timber going in,
			gives 1000 going out, whereas the inverse, 10/100 fromto/tofrom gives 100/10 = 10. Okay see?
		 */
		float rate1 = rates[toIndex][fromIndex];
		float rate2 = rates[fromIndex][toIndex];
		if(toIndex==fromIndex) return toResource;
		//System.out.println("rate 1 is " + rate1 + " and 2 is " + rate2 + " and over each other is " + rate2/rate1);
		// must be .1, since you have 10:1 on other max.
		if(rate1<=0||rate2<=0)  { // in the event of going over the long amount, we go negative,
		// return a 1 as a temporary solution until something better is solved...
			// probably use smaller random sample sizes. Best way to do it! Just grab 1000 randomly.
			double toRes = .05*(tradeTech-1)+g.Maelstrom.getTraderEffect(x,y);
		//	System.out.println("Before:" + (.1*(tradeTech-1)) + " after: " + (1*(tradeTech-1)+g.Maelstrom.getTraderEffect(x,y)));

 		return (long) Math.round(toResource*(1+toRes)); // assuming rate1/rate2=rate2/rate1=1 for default action!
		} 
		// using float we keep reasonable precision.
		//System.out.println("Before:" + (.1*(tradeTech-1)) + " after: " + (1*(tradeTech-1)+g.Maelstrom.getTraderEffect(x,y)));
		
		double toRes = .05*(tradeTech-1)+g.Maelstrom.getTraderEffect(x,y);
		if(rate2/rate1<.1)
		return (long) Math.round(toResource*(.1)*(1+toRes));
		else if(rate2/rate1>10)
		return (long) Math.round(toResource*(10)*(1+toRes));
		else
		return (long) Math.round(toResource*(rate2/rate1)*(1+toRes));

	}
}
