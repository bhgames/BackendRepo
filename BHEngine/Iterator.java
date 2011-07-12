package BHEngine;
/*
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;*/
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

public class Iterator implements Runnable {

	GodGenerator God;
	int internalClock = 0;
	Thread t;
	boolean deleteMe=false;
	String iterateID;
	public Iterator(GodGenerator god, int internalClock, String iterateID) {
		God = god;
		this.iterateID=iterateID;
		this.internalClock = internalClock;

		t = new Thread(this,"Iterator"+iterateID);
	//	System.out.println(iterateID + " exists.");

		t.start();
	}
	
	public void deleteMe() {
		//System.out.println(iterateID + " destroyed.");

		deleteMe=true;
	}
	public void checkPlayers() {
		ArrayList<Player> players = God.getIteratorPlayers();
	//		System.out.println(iterateID + "'s internal clock is off.");
		// only need to loop once - think about it, with two iterators,
		// then 0 would be gotten by the first, and the second would hit 1,
		// and then if second lagged at 1, first would skip 1 and get 2 and 3,
		// and so on. With five or ten iterators combing, it's possible to get them all
		// with one iteration.
		// Then at the end of this iteration, we should check.
		int i = 0; 
		Player p;
		Date today = new Date();

		while(i<players.size()) {
			
			p = players.get(i);
			// basically, if one grabs this player,
			// and starts iterating it, the others can't, and will wait
			// to try, but then they'll find it can't be done.
	//		if(p.getUsername().equals("scooter81")) System.out.println("last login was " +p.last_login.getTime() + " and it needs to be > than " +(today.getTime()-GodGenerator.sessionLagTime) + " and if this is negative, it is: " + (p.last_login.getTime()-(today.getTime()-GodGenerator.sessionLagTime)) + " also his internalClock is " + p.getInternalClock() + " and my internalClock is " + internalClock + " and his owedTicks is " + p.owedTicks);
			double dailyLeft = (p.getPlayedTicks())/(24*3600/GodGenerator.gameClockFactor);
			dailyLeft-=Math.round(dailyLeft);
			
			if(p.ID<999999900&&p.getHoldingIteratorID().equals("-1")&&p.getInternalClock()<internalClock&&(p.owedTicks==0||p.ID==5||p.isQuest())) {
	
				synchronized(p){if(p.getHoldingIteratorID().equals("-1")) p.setHoldingIteratorID(iterateID); }
				if(p.getHoldingIteratorID().equals(iterateID)) {
			//		System.out.println(iterateID + " caught " + p.username + "'s focus and is iterating at " + internalClock);
				while(p.getInternalClock()<internalClock) {
					try {
				//	System.out.println(iterateID + " is iterating " + p.username);
						
						if((p.last_login.getTime()>(today.getTime()-GodGenerator.sessionLagTime)||p.stuffOut()||p.ID==5||p.isQuest())){ 
						//	 System.out.println(p.getUsername() + " is active and has " + p.owedTicks + " ticks owed.");
							p.saveAndIterate(internalClock-p.getInternalClock());}
						else {  
						//	System.out.println(p.getUsername() + " is inactive and has " + p.owedTicks + " ticks owed and played for a total of "+ (p.last_login.getTime()-p.last_session.getTime()));
							// we know last_login - last_session is the time they played this time.
							if(p.totalTimePlayed<0) p.totalTimePlayed=0;
							if(p.numLogins<=0) p.numLogins=1;
							p.totalTimePlayed+=(p.last_login.getTime()-p.last_session.getTime());
							p.save(); // save right before we go inactive to get the totalTimePlayed and stuff saved properly.
							p.owedTicks=(God.gameClock); 
	
							p.setInternalClock(internalClock); } 
					} catch(Exception exc) { exc.printStackTrace(); } 
				}
				
				if(God.saveCounter==God.saveWhenTicks) {
				//System.out.println("Saving " + p.getUsername());
					p.save();
				}

	//		System.out.println(iterateID + " 2");

				p.setHoldingIteratorID("-1");
				}
			} 
			
			if(p.ID<999999900&&p.getHoldingIteratorID().equals("-1")&&(dailyLeft==0||p.lastTerritoryClock==0)&&p.ID!=5&&!p.isQuest()) {
				// so basically you get territory even when inactive. However, multiple iterators could
				// find and recalc you in this timeframe, so we also have a last territory clock, which lets them know
				// not to recalc unless you haven't had your territory made yet(ie server restart, lastTerritoryClock is 0),
				// or you did have it recalced awhile ago, but not this very instant. We can't use lastterritoryclock to time
				// daily recalculations, as a server restart MAY have happened 6 hours ago, and you hit your 24 hour mark now,
				// so it'd have made it six hours ago and it would need to make it now.
				synchronized(p){if(p.getHoldingIteratorID().equals("-1")) p.setHoldingIteratorID(iterateID); }
				if(p.getHoldingIteratorID().equals(iterateID)) {
					if(p.lastTerritoryClock!=internalClock) { // recalculation bitch.
						p.territoryCalculator();
						p.saveInfluence();// it'll save twice if somebody just became a lord,
						// otherwise it'll just save once to get towninfluence. NBD.
						p.lastTerritoryClock=internalClock;
						if(p.owedTicks>0) System.out.println(p.getUsername() + " is inactive and I am still doing his territories.");
						else System.out.println(p.getUsername() + " is active and I am doing his territories.");
					}
					p.setHoldingIteratorID("-1");

				}
				
			}
			
			
			i++;
		}
		

		
		
	}
	public void checkTowns() {
		Date today = new Date();

		ArrayList<Town> towns = God.getIteratorTowns();
	//		System.out.println(iterateID + "'s internal clock is off.");
			
		// only need to loop once - think about it, with two iterators,
		// then 0 would be gotten by the first, and the second would hit 1,
		// and then if second lagged at 1, first would skip 1 and get 2 and 3,
		// and so on. With five or ten iterators combing, it's possible to get them all
		// with one iteration.
		// Then at the end of this iteration, we should check.
		int i = 0; 
		Town t;
		while(i<towns.size()) {
			
			t = towns.get(i);
				// basically, if one grabs this player,
			// and starts iterating it, the others can't, and will wait
			// to try, but then they'll find it can't be done.

			if(t.getTownID()<999999900&&t.getHoldingIteratorID().equals("-1")&&t.getInternalClock()<internalClock&&(t.owedTicks==0||t.stuffOut())) { 
	//			if(p.getPlayer().getUsername().contains("testman")) System.out.println("Iterating a "+ p.getPlayer().getUsername());
			//	System.out.println(iterateID + " found " + p.username + " unhooked and in need at " + internalClock);
				// Cool, a double lock. First, if you're iterating through and you catch
				// a player that's held, you don't get in and you just keep going.
				// If it isn't held and you and other iterator get it at the same time,
				// then you both get through the for loop. But then only one of you gets
				// to switch the ID to your number, and once you've done that, the next guy
				// that comes through sees the ID is not one and so does not set the ID to
				// his number, and when he tries to enact the iteration code, you see
				// it's not his number, so he doesn't do it. Only you do.
		//		System.out.println("iterating " + p.username);
				if(t.owedTicks>0) t.update(); // could happen if suddenly a town had owed ticks but wasn't online for the raid to be sent.
				// like a quest could get here. But we save them.
				synchronized(t){if(t.getHoldingIteratorID().equals("-1")) t.setHoldingIteratorID(iterateID); }
				if(t.getHoldingIteratorID().equals(iterateID)) {
			//		System.out.println(iterateID + " caught " + p.username + "'s focus and is iterating at " + internalClock);
			
					try {
				//	System.out.println(iterateID + " is iterating " + p.username);
	
						if(t.getPlayer().last_login.getTime()>(today.getTime()-GodGenerator.sessionLagTime)||t.stuffOut()) {
						// System.out.println(p.getTownName() + ","+ p.getPlayer().getUsername() + " is active and has " + p.owedTicks + " ticks owed.");
							t.iterate(internalClock-t.getInternalClock()); }
						else { 
							// System.out.println(p.getTownName() + ","+ p.getPlayer().getUsername() + " is inactive and has " + p.owedTicks + " ticks owed.");
							t.owedTicks=God.gameClock; t.setInternalClock(internalClock); 
						} 
					} catch(Exception exc) { exc.printStackTrace(); } 
				
					t.setHoldingIteratorID("-1");
				}
			}
			
			
			i++;
		}
		

		
		
	}
	public void checkLords() {
		// GOTTA UPDATE LEAGUES CONSTANTLY, COULD COME ON AND GET TAXES FROM ALL NEW MINES INSTEAD OF OLDER UNUPGRADED ONES!
			ArrayList<Player> players = God.getIteratorPlayers();
	//		System.out.println(iterateID + "'s internal clock is off.");
		// only need to loop once - think about it, with two iterators,
		// then 0 would be gotten by the first, and the second would hit 1,
		// and then if second lagged at 1, first would skip 1 and get 2 and 3,
		// and so on. With five or ten iterators combing, it's possible to get them all
		// with one iteration.
		// Then at the end of this iteration, we should check.
		int i = 0; 
		Player p; 
		while(i<players.size()) {
			p = players.get(i);
			if(p.isLord()) { 
				// basically, if one grabs this player,
			// and starts iterating it, the others can't, and will wait
			// to try, but then they'll find it can't be done.
			if(p.ID<999999900&&p.getHoldingLordIteratorID().equals("-1")&&p.getLordInternalClock()<internalClock-GodGenerator.lordLagTime) {
			
			
				synchronized(p){if(p.getHoldingLordIteratorID().equals("-1")) p.setHoldingLordIteratorID(iterateID); }
				if(p.getHoldingLordIteratorID().equals(iterateID)) {
			//		System.out.println(iterateID + " caught " + p.username + "'s focus and is iterating at " + internalClock);
			
				try {
			//	System.out.println(iterateID + " is iterating " + p.username);
					p.doVassalTaxes(internalClock-p.getLordInternalClock());
				} catch(Exception exc) { exc.printStackTrace(); } 
			
				p.setHoldingLordIteratorID("-1");
				}
			}
			
			}
			i++;
		}
		

		
		
	}
	public void checkLeagues() {
		// GOTTA UPDATE LEAGUES CONSTANTLY, COULD COME ON AND GET TAXES FROM ALL NEW MINES INSTEAD OF OLDER UNUPGRADED ONES!
			ArrayList<Player> players = God.getIteratorPlayers();
	//		System.out.println(iterateID + "'s internal clock is off.");
		// only need to loop once - think about it, with two iterators,
		// then 0 would be gotten by the first, and the second would hit 1,
		// and then if second lagged at 1, first would skip 1 and get 2 and 3,
		// and so on. With five or ten iterators combing, it's possible to get them all
		// with one iteration.
		// Then at the end of this iteration, we should check.
		int i = 0; 
		Player pl; League p;
		while(i<players.size()) {
			pl = players.get(i);
			if(pl.isLeague()) { 
				// basically, if one grabs this player,
			// and starts iterating it, the others can't, and will wait
			// to try, but then they'll find it can't be done.
				 p = (League) pl;
			if(p.ID<999999900&&p.getLeagueHoldingIteratorID().equals("-1")&&p.getLeagueInternalClock()<internalClock-GodGenerator.leagueLagTime) {
			
			//	System.out.println(iterateID + " found " + p.username + " unhooked and in need at " + internalClock);
				// Cool, a double lock. First, if you're iterating through and you catch
				// a player that's held, you don't get in and you just keep going.
				// If it isn't held and you and other iterator get it at the same time,
				// then you both get through the for loop. But then only one of you gets
				// to switch the ID to your number, and once you've done that, the next guy
				// that comes through sees the ID is not one and so does not set the ID to
				// his number, and when he tries to enact the iteration code, you see
				// it's not his number, so he doesn't do it. Only you do.
		//		System.out.println("iterating " + p.username);
				synchronized(p){if(p.getLeagueHoldingIteratorID().equals("-1")) p.setLeagueHoldingIteratorID(iterateID); }
				if(p.getLeagueHoldingIteratorID().equals(iterateID)) {
			//		System.out.println(iterateID + " caught " + p.username + "'s focus and is iterating at " + internalClock);
			
				try {
			//	System.out.println(iterateID + " is iterating " + p.username);
				p.doTaxes(internalClock-p.getLeagueInternalClock());
				} catch(Exception exc) { exc.printStackTrace(); } 
			
				p.setLeagueHoldingIteratorID("-1");
				}
			}
			
			}
			i++;
		}
		

		
		
	}
	public void checkPrograms() {
		// GOTTA UPDATE LEAGUES CONSTANTLY, COULD COME ON AND GET TAXES FROM ALL NEW MINES INSTEAD OF OLDER UNUPGRADED ONES!
			ArrayList<Hashtable> players = God.programs;
	//		System.out.println(iterateID + "'s internal clock is off.");
		// only need to loop once - think about it, with two iterators,
		// then 0 would be gotten by the first, and the second would hit 1,
		// and then if second lagged at 1, first would skip 1 and get 2 and 3,
		// and so on. With five or ten iterators combing, it's possible to get them all
		// with one iteration.
		// Then at the end of this iteration, we should check.
		int i = 0; 
		Hashtable p;
		while(i<players.size()) {
			p = players.get(i);
			int time = internalClock-((Integer) p.get("startAt"));
			double hourlyLeft = (time)/(3600/GodGenerator.gameClockFactor);
			hourlyLeft-=Math.round(hourlyLeft);
			double dailyLeft = (time)/(24*3600/GodGenerator.gameClockFactor);
			dailyLeft-=Math.round(dailyLeft);
			
			if(((String) p.get("holdingIteratorID")).equals("-1")&&(dailyLeft==0||hourlyLeft==0)) {
			
				// IF THIS IS A REV2.0
				synchronized(p){if(((String) p.get("holdingIteratorID")).equals("-1")) p.put("holdingIteratorID",iterateID); }
				if(((String) p.get("holdingIteratorID")).equals(iterateID)) {
			//		System.out.println(iterateID + " caught " + p.username + "'s focus and is iterating at " + internalClock);
				
					
				if(hourlyLeft==0) {
					God.getPlayer((Integer) p.get("pid")).update();
					God.getPlayer((Integer) p.get("pid")).getPs().runMethod("hourlyCatch",null);
				}
				if(dailyLeft==0) {
					God.getPlayer((Integer) p.get("pid")).update();
					God.getPlayer((Integer) p.get("pid")).getPs().runMethod("dailyCatch",null);
				}
					p.put("holdingIteratorID","-1"); 
					p.put("internalClock",internalClock);
				}

			}
			
			
			i++;
		}
		

		
		
	}
	public void run() {

		for(;;) {
			try {
			if(deleteMe) break;
			// Just write the damn thing.
			// Possible issue? What if we're overloaded, and so this iterator doesn't finish
			// getting through players before the others do due to it hitting Id, let's say,
			// and the GCF time passes and the others get a new internalClock and this has the old one?
			// Well then it'll go through the players and find that the player's old clock matches
			// it's old clock and it'll stop the timer even though that's really inaccurate.
			// Therefore, if we set it up so it can only check to see if it can stop the timer
			// if internalClock matches gameClock, only iterators up to date
			// can get away with stopping the clock. However, if one iterator is still
			// chugging away, its likely that none will be able to get away with stopping the clock,
			// as they'll all be incrementing, but they'll all see this ol' incrementer from
			// a few ticks back is still working on some players they can't touch, and they won't
			// turn the next timer off, and as a result, the ticks for an increase will go up.
			int gameClock = God.gameClock;
	
			if(internalClock!=gameClock) {
				
		//	System.out.println(iterateID + "'s internal clock is off.");
			this.internalClock=gameClock;
			// only need to loop once - think about it, with two iterators,
			// then 0 would be gotten by the first, and the second would hit 1,
			// and then if second lagged at 1, first would skip 1 and get 2 and 3,
			// and so on. With five or ten iterators combing, it's possible to get them all
			// with one iteration.
			// Then at the end of this iteration, we should check.
			double rand = Math.random();
			if(rand<.5)
			 checkTowns();
			else if(rand>=.5&&rand<.8)
			 checkPlayers();
			else if(rand>=.8&rand<.85)
		     checkLeagues();
			else if(rand>=.85&&rand<.9)
			 checkLords();
			else
			 checkPrograms();
			
			if(internalClock==God.gameClock) {
				//You can only check if you're not a lagged iterator.
			 int i = 0;boolean tripped1 = false;
			 ArrayList<Town> towns = God.getIteratorTowns();
		//	 System.out.println(iterateID + " checking the lag timer...");
			while(i<towns.size()) {
				if(towns.get(i).getInternalClock()<internalClock&&towns.get(i).owedTicks==0){ 
					
					tripped1=true; break; }
				
				i++;
			}
			i = 0;boolean tripped2 = false;
			 ArrayList<Player> players = God.getIteratorPlayers();
		//	 System.out.println(iterateID + " checking the lag timer...");
			while(i<players.size()) {
				if(players.get(i).getInternalClock()<internalClock&&players.get(i).owedTicks==0){  // don't count those with owedTicks.
					
					tripped2=true; break; }
				
				i++;
			}
			
			i = 0;boolean tripped3 = false;
		//	 System.out.println(iterateID + " checking the lag timer...");
			while(i<players.size()) {
				if(players.get(i).isLeague()&&((League) players.get(i)).getLeagueInternalClock()<internalClock-GodGenerator.leagueLagTime){ 
					
					tripped3=true; break; }
				
				i++;
			}
			i = 0;boolean tripped4 = false;
			ArrayList<Hashtable> programs = God.programs;
			//	 System.out.println(iterateID + " checking the lag timer...");
				while(i<programs.size()) {
					if(((Object) programs.get(i).get("Revelations")).getClass().getSuperclass().getName().equals("Revelations.RevelationsAI2")
							&&((Integer) programs.get(i).get("internalClock"))<internalClock){  //240 is 15 mins, minimum.
						
						tripped4=true; break; }
					
					i++;
				}
			
			
			
			if(!tripped1&&!tripped2&&!tripped3) {
				if(God.printCounter==God.printWhenTicks)
				System.out.println(iterateID + " is stopping the timer because nobody is left.");
				God.lagTimer.stopTimer(); // we shut it off if we finish early.
			} else {
				if(God.printCounter==God.printWhenTicks)
				System.out.println(iterateID + " found players not handled yet.");
			}
			}

			
			}
			} catch(Exception exc) { exc.printStackTrace(); System.out.println(iterateID + " was saved, though.");
		}
	//	System.out.println(iterateID + " is passing out...");
		
	}
	}
	
	
	
}
