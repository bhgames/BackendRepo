package BHEngine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

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
		//	if(p.getUsername().equals("scooter81")) System.out.println("last login was " +p.last_login.getTime() + " and it needs to be > than " +(today.getTime()-GodGenerator.sessionLagTime) + " and if this is negative, it is: " + (p.last_login.getTime()-(today.getTime()-GodGenerator.sessionLagTime)) + " also his internalClock is " + p.getInternalClock() + " and my internalClock is " + internalClock + " and his owedTicks is " + p.owedTicks);

			if(p.getHoldingIteratorID().equals("-1")&&p.getInternalClock()<internalClock) {
			
	//			System.out.println(iterateID + " 1");
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
				synchronized(p){if(p.getHoldingIteratorID().equals("-1")) p.setHoldingIteratorID(iterateID); }
				if(p.getHoldingIteratorID().equals(iterateID)) {
			//		System.out.println(iterateID + " caught " + p.username + "'s focus and is iterating at " + internalClock);
			while(p.getInternalClock()<internalClock) {
				if(God.printCounter==God.printWhenTicks||p.getUsername().equals("JigglyYoWigly")) 
				System.out.println(iterateID + " is iterating " + p.getUsername() + " at " + internalClock + " when GC is " + God.gameClock + " and p internal clock is " + p.getInternalClock());
				try {
			//	System.out.println(iterateID + " is iterating " + p.username);
					if(p.last_login.getTime()>(today.getTime()-GodGenerator.sessionLagTime)||p.stuffOut()){ 
					//	 System.out.println(p.getUsername() + " is active and has " + p.owedTicks + " ticks owed.");

						p.saveAndIterate(internalClock-p.getInternalClock());}
					else { // System.out.println(p.getUsername() + " is inactive and has " + p.owedTicks + " ticks owed.");
						p.owedTicks+=(internalClock-p.getInternalClock()); p.setInternalClock(internalClock); } 
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
			
			
			i++;
		}
		

		
		
	}
	public void checkTowns() {
		Date today = new Date();

			ArrayList<Town> players = God.getIteratorTowns();
	//		System.out.println(iterateID + "'s internal clock is off.");
			
		// only need to loop once - think about it, with two iterators,
		// then 0 would be gotten by the first, and the second would hit 1,
		// and then if second lagged at 1, first would skip 1 and get 2 and 3,
		// and so on. With five or ten iterators combing, it's possible to get them all
		// with one iteration.
		// Then at the end of this iteration, we should check.
		int i = 0; 
		Town p;
		while(i<players.size()) {
			
			p = players.get(i);
				// basically, if one grabs this player,
			// and starts iterating it, the others can't, and will wait
			// to try, but then they'll find it can't be done.
		
			if(p.getHoldingIteratorID().equals("-1")&&p.getInternalClock()<internalClock) {
			
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
				synchronized(p){if(p.getHoldingIteratorID().equals("-1")) p.setHoldingIteratorID(iterateID); }
				if(p.getHoldingIteratorID().equals(iterateID)) {
			//		System.out.println(iterateID + " caught " + p.username + "'s focus and is iterating at " + internalClock);
			
				try {
			//	System.out.println(iterateID + " is iterating " + p.username);
					if(p.getPlayer().last_login.getTime()>(today.getTime()-GodGenerator.sessionLagTime)||p.stuffOut()) {
						 System.out.println(p.getTownName() + ","+ p.getPlayer().getUsername() + " is active and has " + p.owedTicks + " ticks owed.");

						p.iterate(internalClock-p.getInternalClock()); }
					else { 
						// System.out.println(p.getTownName() + ","+ p.getPlayer().getUsername() + " is inactive and has " + p.owedTicks + " ticks owed.");

						p.owedTicks+=(internalClock-p.getInternalClock()); p.setInternalClock(internalClock); } 
				} catch(Exception exc) { exc.printStackTrace(); } 
			
				p.setHoldingIteratorID("-1");
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
			if(p.getLeagueHoldingIteratorID().equals("-1")&&p.getLeagueInternalClock()<internalClock-GodGenerator.leagueLagTime) {
			
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
			if(rand<.6)
			 checkTowns();
			else if(rand>.6&&rand<.9)
			 checkPlayers();
			else
		     checkLeagues();
			
			if(internalClock==God.gameClock) {
				//You can only check if you're not a lagged iterator.
			 int i = 0;boolean tripped1 = false;
			 ArrayList<Town> towns = God.getIteratorTowns();
		//	 System.out.println(iterateID + " checking the lag timer...");
			while(i<towns.size()) {
				if(towns.get(i).getInternalClock()<internalClock){ 
					
					tripped1=true; break; }
				
				i++;
			}
			i = 0;boolean tripped2 = false;
			 ArrayList<Player> players = God.getIteratorPlayers();
		//	 System.out.println(iterateID + " checking the lag timer...");
			while(i<players.size()) {
				if(players.get(i).getInternalClock()<internalClock){ 
					
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
