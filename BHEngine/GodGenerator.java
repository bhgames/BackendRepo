
package BHEngine;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import BattlehardFunctions.BattlehardFunctions;
import BattlehardFunctions.UserBuilding;
import BattlehardFunctions.UserRaid;
import BattlehardFunctions.UserTPR;
import BattlehardFunctions.UserTrade;
import BattlehardFunctions.UserTradeSchedule;

import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;
import java.security.SecureRandom;


/*
 Encyclopedia of questions:
 
 
 PROCEDURE FOR SUCCESSFUL ACCOUNT SPLIT
 1. MAKE A SYSTEMS BACKUP
 2. update player set password = md5(password);
 3. run convert reqtype.
 
 Splitting accounts:
 
 
 1. On player creation, email, fuid, username and password, registration date are now stored in a separate table that is never deleted. Encrypted passwords now!---CHECK---
 2. When a deleted player logs in(ie can't find login info) but can find account info, a new account is created
 and a mail is sent describing what happened. Also, new players can't create same username of deleted player.---CHECK---
 3. Players who lock in FB accounts lock in on both tables. ---CHECK---
 4. Players with accounts but no player objects are sent periodical emails that match with a current weekly promotion we put out.---CHECK---
 5. You need to convert all old players to the new system.---CHECK---
 6. You need to come up with a way to login as other people using a master password.---CHECK---
 
 Tests:
 
 1. Create an account, make sure everything works.
 	-One server sesh---CHECK---
 	-Multi---CHECK---
 2. Delete the account, see what happens on login.
 	-One server sesh---CHECK---
 	-Multi---CHECK---
 3. Delete the account, see what happens if you try to create a new account with the same username.---CHECK---
 4. Lock in an FB account and see if it updates on both tables.---CAN'T---
 5. Test the periodical sending by deleting an account.---CHECK---
 6. Test the conversion works.---CHECK---
 7. test your autologin works.---CHECK---
 
 
 
	Tracking Logins:
	
	1. On login, mark it down in the player object as last_session_made, and then when the player goes inactive,
	compare it to the last_login, and then make another variable called numLogins that tracks how many last_session_mades there were,
	so you can keep a running average attached to the account object. The running average you just add the difference of the session made
	and last login to the total time played amount and then divide it by number of logins.
	
	Tests:
	1. Make a login, then go "inactive" and come back and see when you went inactive and see if it adds it to the total alright.
	Then do it again.---CHECK---



 Server Stat Page
 
 Number 1: Reporting Metrics
1. Number of Registered Users (Need to split accounts up first.)
2. Active This Week ( Can get)
3. Average/Max/Min Age of Users(active/passive)  (Can get)
4. Registered This Today, Yesterday, Week, Last Week, Month, Last Month, Year, Last Year (Add Registered At Date)
5. Deleted Players' (Compare Registered Users to Players)
6. Conversion Pipeline
	FB Page or Fb Ad or Google Ad-> Goto App -> Home Page ->  Ad Page(A with B without) -> Viewer-> Play 
			1					.03			.5	               .5                  .1
(With Google Analytics)
7. Average Time Spent in the Game (last_session_made - last_login)
8. Server uptime (Search for an apache function or use exact readout)
 
 
 
 
 
 List of changes for Beta 3:
 
 1. Combined bunker modes.---CHECK---
 	a. BP change 1000 for Troop Push---CHECK---
 	b. Loser gets 50% of winner's BP---CHECK---
 	c. Bunker protect adjusts based on #towns you have.
 	d. Check that AF Protect adjust based on #towns you have, and is 3x as much now.
 2. Archaeological digs.
 3. Trade routes that generate wealth.
 4. Resource outcroppings.
 5. Non-towned areas become "territories" when you send settlers. 
 6. BLOCKADE mission type for trades.
 7. Food becomes a drainer, not in prices.
 8. RQ Common Bugs and DEBUG class
 9. BQ redo - Military and Civilian quest path. Military Quests means attacking real players. Make them make a tactical offensive/defensive decision
 that branches them.
 10. Hero
 11. Fake players - evolving their AIs. They kill one another, and replace them with their AIs. Random defects every day.
 12. Alliances between players so he can visit each other.
 13. In Facebook
 14. Redo Revelations to be event-driven AI. Unlockable Events, like onTick
 15. Buildable built in code plugins that run before event code.
 16. Global Server Clock---CHECK---
 
 
 
 
 Archaeological Digs:
 
 
 If an Id town is attacked and there is no defense there, then the Civilians flee. A Dig lasts for 24 hours, at which point,
 the Civilians return. You then have a 24 hour window to log on and send another group to do a Dig at that Id site, to continue
 building the likelihood of finding something cool. A Dig site degrades over time, so you have to send regular digs to keep
 going deeper, is the reasoning behind it. If you go beyond the 24 hour window, the likelihood restarts.
 Digs show up on the world map as markers over Id towns for all to see.
 
 When something has been found, a mail is sent to your character. It tells you what you found ex

"We found some sort of tomb. We can open the door but we'll destroy the passageways beyond - we won't be able to search further
on this dig."

This can happen at any point during the 24 hour period of the dig. Once this mail has been sent out, you have 24 hours to reply
before the team has to return. During this time the probability growth timer has been frozen. If they return, then the timer
begins counting down, and if they do not and they stay and grab the item, the probability timer is reset. If you do not reply at all,
they simply return in 24 hours. 


Prizes offered:
1. Varying degrees of KP (10 KP Centered around 2 days, 20 KP centered around 3, 30 KP centered around 4)
2. In the future, new event handlers for your AI.
3. Blueprints of exotic attack units outside the normal ones. (Will come up with three, new soldier centered around 5 days, tank around 10, jugger 15)
4. Resources of varying degrees. (10% of your mine levels in a day around 1 Day, 20% around 2 days, 30% around 3 days)
5. Occasionally, extra Tech Points. (All the gay techs centered around 4 days, Some of the more useful ones around 5 days, Some of the most useful around 6 days.)
6. Nuclear Missile Silo, fully stocked.
7. Zeppelin.
8. In the future, code blocks written by the Devs for your AI to use. Rarity: Depends on the code.
9. In the future, the ability to control the Id town becomes yours, by finding the central core of the AI there. It'll be AI-controlled,
but will protect you, or attack others.

What you find is based on the mix of civvies you send.

How do we do this?

Well, we've got to rig up support raids. We now simply just include an extra variable called scholars on raids
that is >0, and if it is, then it's a dig, not a support raid. They can only goto Id towns. Id towns now have three things: probability
timer associated with them, a dig count timer, well, all towns do, actually, and a saved point at when a message will be sent in the dig for
that dig. 

So, each prize has a gaussian that has it's center with a sigma around it of 2 days. Each gaussian is centered
at a different probability tick. At any one time, the probability of getting any of the prizes at a certain tick x
is gotten by finding the values of each gaussian at that tick x, summing them and normalizing them to 100%. Even getting
"nothing" is considered a prize.

When a dig begins, the chance of you finding something in the next 24 hours is chosen by randomly choosing a point in the next
24 hours to enact a random search of the gaussian probabilities using probability timer.. It has to be truly random for players to get a chance to find something.

A message is sent out and the timer starts up again. When the timer gets past the chosen time for finding by 24 hours, the scholars 
are sent home, the dig timer and find time are reset, and the probability timer starts to go down. If the prize is accepted,
all the same things happen except the probability timer goes to zero. So, to summarize:

1. Probability timer is the timer that is constantly going up when a dig is on, and drifts down when it is off or when a prize is taken.
2. Dig timer - restarts every dig, helps time how long we stay.
3. Find Time - Randomly selected each dig, is the choice of when to send the message out.
 
 Days:
 1. Center of Nothing( Relative Mag: .5)
 2. Center of 10% daily resources (Relative Mag: 1)
 3. Center of 20% Daily resources and 50 KP (Relative Mag: 1)
 4. Center of 30% Daily Resources, 65 KP (Relative Mag: 1)
 5. Center of 50% Daily Resources, 80 KP (Relative Mag: 1)
 6. Center of random API, of hidden soldier blueprint(Rock), random tech points(Civvie in Grass, Military in Desert) (Relative Mag: .5)
 7. Center of hidden tank blueprint(Grass), and of the four resource increaser technologies(3 in Grass, 1 in Rock/Desert) (Relative Mag: .3)
 8. Center of hidden Juggernaught blueprint(Desert) (Relative Mag: .1)
 9. Center of hidden Missile Silo or Zeppelin(Silo in Rock, Zeppelin in Desert)(Relative Mag: .05)
 
 PEOPLE CANNOT SEND DUPLICATE DIGS TO SITE ALREADY DIGGED BY THEM!
 
 So... to do:
 
 1. Add the three variables to the town table, load them in and save them.---CHECK---
 2. Make a function that returns a takes the probability ticker and returns a prize name.
 3. Add it to "attack" as a possible thing to do, but make it not available unless you have the Dig API.
 4. Make an exception in support logic block that detects if it's a dig, and if it does, it sets the digTimer to 0,
 and chooses a random message send time. If there is another player there, knock them off if they have civvies and you have civvies,
 or fight them off if they are armed and you are armed, or run if you're not.
 5. In iterate, if dig timer is >=0, it goes up, and so does probability. If dig timer is <0, probability goes down towards 0.
 When the random message send time hits, send the message, and then return them manually when the counter goes down.
 6. In Id you must begin doing checks for messages and deleting them and responding to them.
 7. If a player attacks an Id town with a dig, he obviously fights the units there. If all the support units die and the
 dig is on, then the dig counter is reset, as is the find timer, and a status report is sent AND a message saying the stuff was lost.
 
 
 
 
 
 
 
 Fixing the scaling issue:
 
 		// so if the player is older than 4 days, we don't take the risk of letting them lose all that progress
		// if there is an accidental restart, they had logged in and done stuff in the fifteen minute time period since the last save,
		// and not been saved. That'd SUCK! Though if they then refreshed, they'd be back to being updated. No difference.
		// What if there is an unexpected server shutdown? the player loses a heck of a lot more...having not logged on.
 
 The scaling issue has to do with threads. Now we limit the amount of threads to 5 iterators.
 
 The iterators do not need to cycle every town every time, or every player, though they still need every league. We need to set up protocols:
 
 1. If a town has a raid outgoing, it gets cycled until there are no outgoing raids.
 2. If a player logs in, all his towns are cycled and he is cycled until he is up to date,
 then he begins cycling.
 3. If a raid arrives at a town, then that town and all towns connected via the player
 are cycled until up to date.
 4. If a trade arrives, town and player gets cycled.
 5. If a trade is outgoing, town gets cycled.
 6. If an Id town is attacked, it gets cycled, but the other ones do not.
 7. If a league is cycled, then all players that it collects taxes from get updated too. No fix for that yet.
 
 
UPDATE: epoch fix

0. Add notion of saving gameClock. ---CHECK---
1. all the playedTicks + owedticks need to be changed to playedTicks+(serverclock-owedticks)---CHECK--
2. Iterators only set owedTick equal to serverClock when the last_login is greater than half an hour
and owedTick is 0.---CHECK---
4. If an iterator comes across a player with an owedTick that isn't 0, then don't fucking touch it.---CHECK---
5. Update now does by serverTick-owedTick---CHECK---
6. POST RESET: Set gameClock = Id's max played plus owed, then set everybody's owed = Idplayed+Idowed-owedTicks.
ON BOTH TEST AND MAIN

Tests you'll need to do for this update:
1. that update function works as it should
2. That iterators do not touch owedTicks>0
3. That iterators set owedTicks correctly when the time is right.
 
 Implementation:
 
 0. Add an "owed" playedTicks amount to each player - they can each write that to their
 saves without writing anything else. If owed ticks is less than the time since the last server save in ticks,
 then we know that this person hasn't done anything since that save, and doesn't need saving. Well if
 server ticks aren't the same as owedTicks, owedTicks is ticks over all, server ticks is just
 since the server began ticking. but generally, if owedTicks is greater than the ticks since
 the last save, or since server start, then we know not to save.---CHECK---
 
 1. The iterators no longer look for things with lower playedTicks, they look for towns
 and players with sessions, or towns with outgoing raids/trades. Id is not cycled anymore this way.---CHECK---
 
 2. In the attackServerCheck, if a raid of any sort arrives, town 2 is cycled as well
 as the player and all it's other towns, unless it's Id, then it's just that town.---CHECK---
 
 3. Same goes for tradeServerCheck, except for Id.---CHECK---
 
 4. If a player logs in with owed ticks, he gets cycled back to full. This way, players cannot accrue owedTicks
 and get cycled. Like, an iterator should never be cycling a player with owedTicks. owedTicks disappear on login,
 and when the player has timed out, the owedTicks keeps stacking, if they sent a raid it just extends the time they
 have the server focus for. If a program is running, the player of course is kept cycling.---CHECK---

5. Leagues update players before taxing them. But why should they? If they have an owedTicks of greater than say, three days,
they have no incoming resources of any sort other than passive, which hasn't been upgraded.---CHECK---


Tests:

1. Test with players that are inactive that they just accrue owedTicks and do not cycle.---CHECK---
2. Test via savePlayer string updates that if owedTicks goes beyond save ticks, it just doesn't save.---CHECK---
3. Test that when you login, the player gets updated.---CHECK---
4. Test that while logged in, player is continuing to get updated(easy to do by having it print iterator ticks.)---CHECK---
5. Test that if you attack an inactive player, he both iterates AND gets saved.---CHECK---
6. Test that if you trade with an inactive, he both iterates and gets saved.---CHECK---
7. Test across server restarts that players that are inactive still accrue owedTicks.---CHECK---
8. Test that savePlayer updates only those active across a restart.---CHECK---
9. Test that while logged in across a restart, continue to get updated.---CHECK---
10. Attack a player, restart before attack hits, see if the update still happens.---CHECK---
11. Check that attacking an Id town only updates the Id town.---CHECK---






 The Record-Keeper AI:
 
 1. This AI CANNOT use the database. It seems that shit just gets lost that way. I also don't
 want to keep this data, it needs to be transient. It's job is to just fine discrepancies. What I want
 is for it to hold in it's mind all logs of soldiers going out. Then, on attack beginning, I want for
 it to check that the AU sizes are the same as they were. If they aren't, then a message
 is to be sent to me and the sizes are to be restored.
 
 I also want it to watch that raids DO return. If a raid gets counts down to zero,
 then I want record-keeper to watch that it has indeed returned. It needs to be fucking minding
 all raids, making sure they're coming back. If they don't, then it needs to see if there
 is a GOOD REASON it shouldn't be returning. This is complex stuff, this one should send a message.
 
 To do: 
 
 1. The record keeper has a hash of integer arrays of numbers of AUs referenced by rids. A raid submits one
 when it is made, and it looks for it in the combat/debris/scout logic blocks, and if it sees it, it 
 uses it to compare notes. If not, too bad.
 2. The Record Keeper is itself a thread separated from other ones. Every raid it has in it's hash, once they have been
 checked by the raid's combatlogicblock, it then "waits" for the raid to return back home.
 
 
 
 
 The achievements:
 
 1. I am become Death - Build a missile silo successfully.
 2. Destroyer of Worlds - Launch a nuke.
 3. High Flying - Build your first Airship
 4. Call me Conqueror - Commit a successful siege that lasts more than two rounds
 5. Grizzled Veteran - Complete all of the beginner quests.
 6. Scout's Honor - Successfully scout another player.
 7. Veni, Vidi, Vici - Grow to four towns large.
 8. Asimovian - Unlock all A.I. APIs
 9. Noble Savage - Reach an average level of 10 in your capital city.
 10. Pwnblazer - Accrue 1000 BP
 
 
 
 
 
 How Nuking Works:
 
 Each level means an EXTRA building to level down. How are Nukes DIFFERENT
 from bombers and not BETTER? Well Bombers go after individual buildings and
 are generally priced to be the same amount as the building required. Nukes
 need to cost significantly more. So the nuke level translates to the amount
 of buildings it will attempt to knock down a level. It is incredibly destructive.
 It's range is limited though by it's level too.
 
However, it also will COST a shit load. Given that the base price is 4000, as opposed to 400,
it literally costs 10x as much to upgrade this building as opposed to other ones. And the fucked
up thing is, each level it'll hit one extra building and knock it down a level, so only
by hitting level 10 could you possibly hope to hit 10 buildings down a level and ONLY
if they are level 10 or greater each will you get your cost of charging it back. But even then,
you won't really,because you'll be paying for the previous nine levels. So Adding all that up,
it'll probably mean you'll have to get somebody at level 12-15, with 10 buildings, to hit.

All in all, a very INCONVENIENT weapon. But sweet. And the worst part is, if somebody
else has a nuke, yours is subtracted in effectiveness! so you spend all that money on leveling
and it just gets cancelled by somebody else. I'd rather use bombers!

Well it should also kill army too. Each level up should knock out an extra 5% of the army
there. So there should be a nuke incoming thing. We can easily check for this by adding
some sort of checker method for towns that scan through all the towns nearby and look
for a nuke launch. This should be visible on the WM and this is where we can pick it up.

How do we implement:

1. Create the nuke - when it's created, it sets the ticksPerPerson up to the ticks Required,
and then it counts it down! CLEVER... Once it's done, then it cannot be unlaunched. You can only
launch from 5 spaces*Level of Nuke---CHECK---

2. Create a checker method in town that tracks the nuke to it's finish. If the building---CHECK---
is demolished before then, no more nuke! Checker method should basically mimic the bomb logic block...
it should tally civilians killed, buildings killed, and army killed. Then it creates a fallout cloud.

3. Level 1 Silo takes a week to build. The rest of the upgrades are easy. This CANNOT
be diminished by Engineers. Also sends out a message to all in the area.---CHECK---

4. At end of attack, check for level 0-1 Missile silos and destroy them if there are no
defenders left.--CHECK---
 
 To test:
 1. Build a nuke.---CHECK---
 2. Attack the nuke, destroy it. Test out messaging.---CHECK---
 3. Let a nuke complete, and fire one off. Check that it---CHECK---
 	-Kills AUs accordingly---CHECK---
 	-Drops the right amount of buildings.---CHECK---
 	-Creates the correct fallout cloud.---CHECK---
 	-Creates all the relevant SRs for everybody.---CHECK---
 4. Block the nuke partial---CHECK---
 5. Block the nuke wholly---CHECK---
 	
 	
 	
 
 How debris works:
 
 Simply: 
 
 To do:
 Find out the cost to build those units OFF THE TOP to maximize their value, using returnPrice.---CHECK---
 
 Place 10% of that in some extra res[] array on town.---CHECK---
 
 New mission type: debris collects debris. ---CHECK---
 
 Recycling building, if you have it, works like comms center but for 3.3% back of debris before it's added after attacks.---CHECK--
 
 Include info in World Map data---CHECK---
 
 1. To test: Attack somebody and create the debris.---CHECK---
 2. Give someone the recycling center and see if they take the debris that they should.---CHECK---
 3. Try and fetch the debris from the town of the debris and from another town.---CHECK---
 
 
 
 
 
 
 The Unlockable API:
 
 Need to separate it into unlockable groups:
 
 Attacking API Functions - Can be unlocked via doing the RQs, or by Research.
 Adv. Attacking API - Can get UserRaids, other stuff.
 
 Trading API Functions - Can be unlocked by research.
 Advanced - Get UserTrades and TSes
 
 Stock Market API
 
 Building API Functions - Including build civvies, unlocked by research or by doing the RQs.
 Advanced - Get UserBuildings and other stuff.
 
 Research API Functions
 
 The Complete Analytic API - Get UserTowns and UserPlayers. Can only get with the other advancements.
 

 
the codes are attackAPI, tradingAPI, smAPI, researchAPI, buildingAPI, messagingAPI, advancedAttackAPI, advancedTradingAPI, advancedBuildingAPI, zeppelinAPI,completeAnalyticAPI
 nukeAPI
 
 To test:
 1. Try and do an attack without the API. Print the error.---CHECK---
 
 
 
 
 Changing the Two-Way Trade:
 
 To make it SUDDENLY work with nobody on the other end, we need to put in some new handling.
 Right now you set up both towns in advance and it only gets "set off" when you uh accept.
 Can we do this in a different way? We can make two way trades accept no second town, and
 only get accepted when the tsid is sent by somebody else.
 
 Testing the two-way trade:
 1. Finally, post a two way trade.---CHECK---
 2. Accept the trade with another account, see if it plays out correctly.---CHECK---
 3. Redo 1 and 2, cancel the trade schedule.---CHECK---
 
 
 
 
 THE ZEPPELIN RULESET:
 
 1. Can only hold a certain amount of soldiers based on CSL.
 2. Comes with an HQ and four warehouses. The MINES are hidden and do not yield resources.
 3. If all soldiers die in an attack or the blimp is attacked without soldiers, the blimp dies. ALL
 resources goto the victor.
 4. The Zeppelin carries fuel. It carries it by spaces moved. A zeppelin factory building
 generates this fuel based on it's level. A Level 1 Zeppelin Building generates 5
 fuel cartridges a day. A level 2 Zeppelin does 10, Level 3 does 15, etc. It can always
 hold four days' worth.
 5. When a Zeppelin is hovering over a base town of yours, it will slowly refuel at a rate
 of equal to five times what it gains in a day.
 6. If a Zeppelin runs out of fuel, it can autoreturn to base, loses all men and all resources.
 7. To control the Zeppelin, one merely needs to select an x,y to goto. The Zeppelin moves twice as fast
 as the fastest unit, the Collossus. So it moves at a base speed of 600. 
 8. The Zeppelin cannot move until all outgoing raids have returned. No leaving soldiers behind!
 9. If a Zeppelin is over an Id town, it suckles out resources.
 10. If two stationary zeppelins are in the same place, then they combat each other.
 11. Zeppelins cannot station over towns that are owned by others, only by you or Id.
 12. If somebody attacks a Zeppelin at a certain x,y, if the Zeppelin is found there then
 it is sent. If it's over your town, then it joins the fight, if it's over Id towns, it joins the fight on the side of Id.
 13. Zeppelins CANNOT be Genocided.
 14. When two Zeppelins combat one another in the same space and both don't lose all men, the one who lost the fight
 is butted to the side 5 spaces max or aborted.
 
 What needs to be done:
 1. Create the passive functions
 	
 	-Make sure Zepp towns get no resincs.---CHECK---
 	
 	-Make sure zeppelins are listed as towns on WM with endpoint and direction and their velocity.---CHECK---
 	
 	-Zeppelin movement code -if moving, keep moving, if just stopped moving, check
 	and combat other zeppelins in the x,y.---CHECK---
 	
 	-Refuel and fuel build code - Put them together to make things easy.---CHECK---
 	
 	-Suckle code - town must deliver resources to Zepps, not other way around, so res is divided between zepps and it's done correctly.---CHECK---
 	
 2. Create the active functions
 
 	-If support is sent to the Zeppelin, make sure it can't go above it's CSL.---CHECK.---
 	
 	-Creation code: Zeppelin is created with a timer on the building for people, but it is zeppelin, and buildings built.---CHECK---
 	
 	-Alter attack code - if blimpie is attacked and has no soldiers at the end, destroy, destroy, destroy! All res to victor. If
 	other attacker is blimpie, must die too if loses. If Blimpie is town 2, this is easy. If Blimpie is over an Id town, it's units are added as "support" momentarily and returned when done, but Blimpie
 	CAN die in this way. If Blimpie is over your town, it joins and if you lose the fight Blimpie DIES. BLIMPIE ALWAYS DIES! ---CHECK---
 	
 	-moveZeppelin code - subtracts fuel in advance. You cannot move the zeppelin again until it's done moving. This is so we can plot it on the WM.
 	Can't move to towns that are not yours. Can't move if you have raids out. Can't move if you don't have the fuel. You can't place one of your zeppelins over another of your zeppelins.
 	Can't move the Zeppelin if another of yours is on chart for that x,y.---CHECK---
 	
 	-Implement abortZeppelin code - if it runs out of fuel somewhere, you can autoreturn it to capital in exchange for all resources aboard.---CHECK---
 	
 	-Change the attack and resupply functions to TOTALLY hit the town instead if there is one under it. YOU CANNOT GENO/GLASS/STRAFE BLIMPIES.
 	YOU CAN ONLY SUPPORT/OFFSUPPORT/ATTACK BLIMPIES. If you ARE A BLIMPIE and you ATTACK YOUR X,Y and there is another BLIMPIE there,
 	then you hit the BLIMPIE, not the town under it, if there is one. This is because this'll only get called when you're just finished
 	moving, and when you're moving YOU CANNOT ATTACK.---CHECK---
 	
 	-Put an ability to offload resources.---CHECK---
 
	 To test:
	 1. Create a Zeppelin.---CHECK---
	 2. Test that refueling occurs when hovering.---CHECK---
	 3. Try moving it away from the town to an open spot.---CHECK---
	 4. Move it over an Id town, test resource suction.---CHECK---
	 5. Move it over a player town, should fail.---CHECK---
	 6. Move it back to your town with the zeppelin airbase and see if it begins refueling.---CHECK---
	 7. Have two zeppelins overlap in an open space. Try offensive losing, defensive losing, both getting away.---CHECK---
	 8. Have a zeppelin attack another zeppelin, see if it dies if def loses, that off doesn't die if off loses, that if both don't lose, no bad things happen.---CHECK---
	 9. have a town attack a zeppelin over space, see if it can destroy it, and make sure the town isn't destroyed if it loses.---CHECK---
	 10. Have a zeppelin hit a town with a Zeppelin over it and lose, see if nothing happens, and if it wins, see the Zeppelin die. Make sure the town is the one getting hit!---CHECK---
	 11. Test out abortZeppelin---CHECK---
	 12. Test out offLoadResources---CHECK---
	 13. Test to make sure nothing can get built.---CHECK---
	 14. Test out moving troops to a zeppelin - does it return above it's CSL?---CHECK---
	 15. Test out attacking Zeppelin over an Id town - does it attack the zeppelin instead of the Id town?---CHECK---
	 
 
 
 
 
 THE NEW COMBAT UNITS:
 
  To make Units that are Weak to two things, and Strong v 1, and always, have the 1 be different than the other two...

	Then you'd have a FP-AMMACC, AMM-FPACC, ACC-AMMFP - That's three units
	
	Adding three more, let's say you can be strong to one of your weaknesses:
	
	Then you'd have a FP-AMMFP, FP-ACCFP, AMM-AMMFP, AMM-ACCAMM, ACC-ACCFP, ACC-ACCAMM
	
	These six units would all be weak to themselves, which is interesting. And if you did the Strong v 1, Weak v 1?
	
	Then you'd be doing FP-AMM(Punisher), FP-ACC(Wolverine), AMM-FP(Shock Trooper), AMM-ACC(Dreadnaught), ACC-FP(Seeker), ACC-AMM(Collossus)
	
	I like this six better.
	
	Given that you're going to have One Carrier Unit, One Scout Unit, and one Invincible Unit,
	
	This leaves you with SIX possible combinations you require.
	
  (conc,armor,cargo,speed)
  (For completeResearch calls, remove all Spaces, keep cap formatting)
 Soldier Class:
 1. Shock Trooper(75,25,50,50,'3,4,') (holding Plasma Rifle and Arc Thrower) with Destroyer Class Upgrade STRONG AGAINST SPEED, WEAK TO FP, 
 2. Pillager (25,50,200,25,'0,') (holding Pump Action EMP ) with Mayhem Upgrade MEDIUM AGAINST ARMOR, WEAK TO AMM,ACC, 
 3. Vanguard (119,40,1,40,'2,5,') (holding Laser Rifle and Rail Gun) with Defender Upgrade STRONG AGAINST CONCEALMENT, WEAK TO FP,AMM, Counter with: Seeker

 Tank Class:
 1. Wolverine (50,125,100,125,'6,7,') (holding WTF Class Rocket Launcher and Automatic EMP Burster) with Devastator Upgrade STRONG AGAINST ARMOR, WEAK TO ACC, 
 2. Seeker (150,50,100,100,'8,11,') (holding EMP Grenade Launcher and Fully Automated Laser Drone ) with Battlehard Upgrade STRONG AGAINST CONCEALMENT, WEAK TO FP
 3. Damascus (200,200,1,199,'9,') (holding Plasma Minigun) with Stonewall Upgrade MEDIUM AGAINST SPEED, WEAK TO NOTHING, Counter with: Nothing is particularly good against Damascus.

 Juggernaught Class:
 1. Punisher (233,234,233,100,'12,13,') (holding BRTHLE and Singularity Whip) with Impervious Upgrade STRONG AGAINST ARMOR, WEAK TO AMM,
 2. Dreadnaught (100,250,200,250,'15,16,') (holding Quantum Anomaly Enabler and Gauss Minigun with Antigrav) with Conqueror Upgrade STRONG AGAINST SPEED, WEAK TO ACC
 3. Collossus (200,250,250,100, '14,17,') (holding Superstring Accelerator Cannon and EMP Wasp) with Ironside Upgrade STRONG AGAINST CONCEALMENT, WEAK TO AMM

 Bomber Class:
 1. Helios (29,29,30,12,'20,') (holding The Focused Nova Bomb) with Havoc Upgrade STRONG AGAINST NOTHING, WEAK TO AMM
 2. Horizon (12,29,29,30,'19,') (holding The Horizon Machine) with Devastator Upgrade STRONG AGAINST NOTHING, WEAK TO ACC
 3. Hades (30,12,29,29,'18,') (holding The H.I.V.E.) with Conqueror Upgrade STRONG AGAINST NOTHING, WEAK TO FP

 
 Congealing all the Researches:
 
	Building Slot Tech 10 Pts
	Building Lot Tech 20 Pts
	Building Stability Tech 5 Pts
	Comms Center Tech 5 Pts
	Town Tech 50 Pts
	
	The Three Efficiencies 5 Pts
	
	AFTech 5 Pts
	BunkerTech 5 Pts
	StealthTech 10 Pts
	ScoutTech 10 Pts
	SupportTech 5 Pts
	Manufacturing Tech 20 Pts
	
	Each of these techs should start at something like 5 points each. Then the next should be 10. If
	we do this, then people will be able to load on the techs early on and not need any scholars...

	The Unit Techs - 50 Points a Soldier, 100 points a Tank(Need at least two towns), 200 Points a Juggernaught(Three towns), 400 Points a Bomber(four towns)
	
	TroopPush - 50 Points
	
	Well holy shit, you'd really need to keep up with this. Well, what can we do? We can take
	totalScholars and then do
	
	Amount/Day= (totalScholars+1) ?
	
	Price Increase/Level = BasePrice*Level
	
	
 	
 	
 
 
 
 
 
 
 
 Achievement System:
 
 These need to be attached to the player. Each achievement has an entry in the
 db in the Achievements table, and then there is another table, called pa, which is
 pid-aid matches. Then there is a givePerm table, which gives certain questnames permissions...
 since their qids may change across server restarts and all. Then those quests can give
 players the achievements. When a player loads, he loads his achievements in an array
 of AP objects which are local to the player.
 
 
 
 
 
 
 
 
 To find the smallest and largest player in an area:
 
 To reward a town, we're going to need to make sure that that town is not rewarded twice.
 But then in a different 5x5, the middle town of the three may be rewarded. Everybody gets rewarded.
 This is not acceptable, put simply. We need to run in 5x5 blocks all the way out, regardless of Id towns,
 just goto the edge of space. So we do a block and scan, then do a block and scan. So on and so forth.
 
 
 It needs to be a combination of resource output, total player troop size, and actual CSL of the town. We might
 as well only look at troop sizes in the town - then empty towns will not get attacked. We'd prefer armed towns do.
 
 So we measure each town's average mine level, the town troop size, and actual CSL of the town.
 Then we compare: Which player has the most troops? Which player has the highest average mines? The highest CSL?
 
 We give each player a rank in each mode. Then we add all the ranks together. Whichever person has the lowest,
 gets hit, whichever has the highest, gets a troop push in that town. This means they could potentially get TPs in many
 places where their army is not, but each successive troop push becomes weaker due to the time and they may have higher
 mines or higher CSLs or something than the next guy.
 
 
 
 
 
 
 Skin replacement with attribute updates:
 
 We need ten different skin types, and they need to be the same
 across the first three units, though it'd be nice if we could have
 some differentiation.
 
 So for skin improvements, let's look at things we can update/change:
 1. We can change the seven attributes defining a unit.
 2. We can change their BP contribution.
 3. We can change their Alamo effect resistance.
 4. We can change their weather resistance.
 8. Bombers, we could up their bombing powers.
 
So we need to be making skins better and better as they level up.

0: Nada, nobody loves you. ----- Standard Infantry/Tank/Juggernaught symbology ---CHECK---
1: 5% FP, AMM, ACC increase ----- Advanced Armor Upgrade ---CHECK---
2: 5% CONC, ARMOR, CARGO, SPEED increase ----- Advanced Weaponry Upgrade ---CHECK---
3: 5% across the board ----- Nanotech Integration Upgrade ---CHECK---
4: 10% across the board and 5% more BP gained based on percentage of army make up with BP skins. ----- Morale Upgrade ---CHECK---
5: 25% more BP gained. ----- Premium Upgrade ---CHECK---
6: 25% Cover Size Limit deflection -----  Alamo Upgrade ---CHECK---
7: 25% BP 25% CSL deflection 5% across the board - Superiority Upgrade ---CHECK---
8: 50% weather resistance and 10% across the board - Impervious Upgrade ---CHECK---
9: 25% weather resistance 25% CSL deflection and 10% across the board. - Conqueror Upgrade ---CHECK---

Bomber Upgrades:
0: Nada, nobody loves you. ----- Standard
1: 25% increase in People Bombing ----- Genocide Upgrade 
2: 25% increase in Building Bombing ----- Devastation Upgrade
3: 15% people, 15% building bombing ----- Siege Upgrade
4: 25% building 25% people bombing ----- Armageddon Upgrade
 
 
 
 
 
 
 
 
 Test Schedule:

Initial Tests:---FRIDAY---
1. Test server start up and load.---CHECK---
2. Test saveServer.---CHECK---
3. Test SyncPlayer out.---CHECK---
4. Get Viewer to load.---CHECK---

Main Tests:
1. THE BUILDING SYSTEM---FRIDAY,SATURDAY---
	1. Test that a building builds. See that it is saved, that you can save it yourself, and that it persists on startup.
	Test that res get taken.---CHECK---
	2. Test that a building levels. Save it, restart it.---CHECK---
	3. Test that a building gets destroyed. Check it deletes properly.---CHECK---
	4. Test cancelling of building lot queue item. Test that res get taken. Make sure it deletes properly.---CHECK---
	5. Put some units on the queue and let them finish. Test that it saves, restart.---CHECK---
	6. Cancel a queue item.---CHECK---
	7. Destroy an AF with queue items in it.---CHECK---

2. THE COMBAT SYSTEM---SATURDAY,SUNDAY---
	1. Test that an attack goes well - watch it create and destroy, and watch unit numbers and that resources
	are taken accordingly and added in the town on the way back.---CHECK---
	2. Test Genocides.---CHECK---
	3. Test Glassing. Hit some buildings motha fucka.---CHECK---
	4. Test scouting.---CHECK---
	5. Test sending support to another town.---CHECK---
	6. Attack that support, make sure it dies correctly.---CHECK---
	7. Send offsupport, and have them be hitting somebody else when you hit their supportAU at home and kill them all.
	See that it remains the same. Also, make sure that supportAU die correctly, but make sure not all of them die.---CHECK---
	8. Send offsupport, have someone attack someone else where they lose all their units and have no supportAU at home.---CHECK---
	9. Test any kind of raid recall.---CHECK---
	10. Test recall support.---CHECK---
	
3. THE TRADE SYSTEM---MONDAY---
	1. Test One Way creation and follow through, that all timers work properly.---CHECK---
	2. Test SM creation and follow through.---CHECK---
	3. Test cancelling a TS in transit and one that hasn't began yet.---CHECK---
	4. Test that synching with a two way works just fine.---forget twoways.--- 
	
4. THE LEAGUE SYSTEM---MONDAY---
	1. Create a league and test that you can play as it.---CHECK---
		1. Create league. ---CHECK---
		2. Switch to league player. ---CHECK---
	2. Have someone join the league and tax them, have it print out what it's taking each run.---CHECK---
	3. Change the person's permissions to view the main league town and let them work with it.
		* Order of calls on player objects:
				getBuildings()
				getWeapons()
				getUserRaids()
				getMessages()
				getUserGroups()
				getUserTPR() or getUserTPRs() <--- TPRs if league is true.
				getQuests()
				
				getUserSR()
				worldmap
				getUserTownsWithSupportAbroad()
				getUserTrades()
				getUserTradeSchedules()
	4. Add another guy and make sure they can't work with that town.
	5. Remove that first person from the league.
	
5. ATTACKUNIT SYSTEM---MONDAY--- 
	1. Test UTCC creation---CHECK---
	2. UTCC edit---CHECK---
	3. UTCC destroy.---CHECK---
	4. AU creation---CHECK---
	5. AU destruction.---CHECK---
		
6. THE QUEST SYSTEM---TUESDAY---
	1. Create a new player.---CHECK---
	2. Test all quests.
 
 Why averaging the fractions in the combat system is OKAY
 
Well, let's say you had a closed system with just the three couples
and no cargo. Then you could do something like
50/20 + 50/30 + 50/10 = 9.1

and 50/50+50/5+50/5 = 21.

So you can average these together and get different stuff every time.


Achievement Point System - how does it work?

 Can only be done if your achievement rating is >0. 
 So we need to decide what does what - and scale it. So we have the upgrades:
 
 1. Buildings finish instantly - 100 AP. ---CHECK---
 2. Troop Push at current brkthru level - 200 AP. ---CHECK---
 3. Grab one extra research - 1000 AP.---CHECK---
 4. 50% Unit Build times for a week - 100 AP---CHECK---
 5. 25% on any mine for a week - 50 AP----CHECK---
 6. Grab a skin without research - 100 AP.---CHECK---
 7. Ferocity Upgrade. Men get 10% stronger for a week.  500AP.---CHECK---
 8. Instant SM trade - 10 AP.---CHECK---
 9. Use Revelations - Premium Members only.---CHECK---
 
 What causes AP, and by how much?
 
 1. Great battle - formula is (fraction of average CSL)*(lossdiff)*(Exp(1-lossdiff/1)/Exp(1))*100AP (lossdiff is 0-1)
 2. Complete a Genocide on someone else of similar size. Formula is (theirStartingCSL/yourStartingCSL)*100AP.
 3. Destroy an enemy building. (20 AP)
 4. Invade another **ACTIVE** (last 48 hours) player's town. (1000AP)
 5. Successful Defense - Completely wipe out a genociding force. Formula is same as above.
 
 
 
 
Player-PS creation: Create on player creation?

I mean, why not? Most likely, we're going to use the PS a lot when a player
is called - they have access to the battlehard functions. Why not create something of
a framework every time a player is requested? A player is not often requested unless
for a lot of stuff. If we do it that way, then the player retains nearly the same
functionality that he did before - you know, for parsing and what not. It'll change over time,
the point is the disconnect.
 
Player-Program Separation - How does that work?

Well let's think about this - what does the program need? It needs a programmable battlehard functions
object with a player reference. If we disconnect the player, then it needs to generate a new reference.
Then when the player's PS calls the BF loadAndRun, the loadAndRun should generate a thread that is not connected
to the playerscript, but to God. God should hold all the Threads, and we can uh...test if they're alive
by simply iterating through and asking for the playerID of each one running.
 
 
 
 
 
 Statement pooling:
 
 Well we now need a connection called an UberConnection. It has an ArrayList of UberStatements.
 
 It overrides the createStatement() method to give one of these UberStatements out, each which
 has it's own "taken" boolean. If it finds no statements out, it creates one and adds it.
 If it finds that more than 20% of the statements are not taken at the moment, it cuts half
 of those 20% out by closing them.
 
 The UberStatement class has a boolean called taken, and an overridden close method that basically
 turns taken to false, but does not close the method. Instead, a destroy method allows for that to
 happen. When the connection is created, it starts with the minimum Iterator number of UberStatements.
 
 
 So the price problem:
 
 Resource rates go too fast, at 2.7 - exponentially. They should probably
 grow like x^2 to match the unit prices - because units do grow like n^2 and I like
 that idea. If the prices do not grow exponentially...then, unit prices do grow
 the same way that rates do. What has to be changed?
 
 1. Building caps
 2. Bomber numbers
 3. CSL amounts
 
 As for build times...they need to drop like the same way that unit numbers rise.
 You could always just make the unit build times follow a production curve
 according to the CSL which is a measure of growth. But clearly the CSL only really
 represents like uh...well it's like one building, for each unit, times sqrt(2),
 so it's really not that representative, as people differ their military amount.
 
 Well I think we needed to mult it by the city number anyway, because people tend to
 group their numbers in the cities.
 
 So things to do:
 1. Find the old build cap article, rewrite and apply it - and do it with three hours, not one hour!---CHECK---
 2. Change the building prices and caps!---CHECK---
 3. Change the bombing eqns.---CHECK---
 4. Change the CSL both in the Controller code, in the getCSL, and probably in combat somewheres.---CHECK---
 5. Change the building times...units no longer grow exponentially...they grow like (lvl+1)^2.---CHECK---
 6. Change the building times for buildings.---CHECK---
 7. Change civvy build times.---CHECK---
 8. Change research building times.---CHECK---
 9. Change scouting.---CHECK---
 
 Building time change:
 Well generally we want to be able to make them to the point where roughly n=4*sqrt(6)*(lvl+1)*
 then basically I mean unit numbers rise like mine numbers do... so generally we just
 want the rebuild times to change significantly...so uh...let's say we want to get you to the point
 where your price level is like say 3 hours of production in 24 hours. So then how do we do that?
 In each town, we measure the average mine level and then uh... we... then figure out how many
 troops one can buy with the resources from a three hour time period and then say it'll
 take four days to make those so figure the build times by dividing four days by the number.
 Then you can bring in engineers and have the caps from a building of identical level to average
 mine level knock down the price by 1/e.
 
 So we want the building time to be proportional to the global average level of your buildings.
 We take this to determine how many soldiers is the point where time should reach a certain extent
 for the next soldier. Each soldier needs to take more and more time...
 
So it should take four days for you to reach your maximum for a unit type. Do we go by
unit types or by all units? We should go by all units. So we know that the true n we're looking for
is the average CSL of all of your towns. So the formula for unit build times has got to be gradually extending...

How the fuck do we do this? We want the time to start small and work up to a limit. Basically
we have a graph of t v size, and as size increases, t does as well. We want the area under
the line to be equal to 4 days. The line can really be anything - or can it - it's constrained
by the size and the time required. So we solve for it.

Lets say then that TimeForTroop(x) = ax where x is the troop number, then we integrate that
from 0 to CSL, and that should be days. Since we're integrating what is essentially time,
then we set it to days.

So then Integrate[ax,{x,0,CSL}] = Days. Then aCSL^2=Days, and a = Days/CSL^2.

So then t = (4 days)*number/(CSL^2) s = 345600*number/(CSL^2)

Then we want engineers to be able to get it down to like a minimum of 1 day rebuild. We
want this to be the number of engineers you have in one building of the level dictated by that CSL.
But then new cities simply can't compete - well, not yet. But that's okay...or should it go by town, so all can
contribute equally across them? No...generally if you have enough towns that it's an issue for a new one, it should
not take long to grow it using your other resources. If we multiply it by

Exp(1-townEngineers/capForLevelAtCsl)/E(1) Which leads to a 36% decrease when you've got one
CY filled with engineers at that level. Works for me.

Final time fix then:

t = Sigma(Expmod,345600*(number)*Exp(1-townEngineers/capForLevelAtCSL)/(Exp(1)*CSL^2))

This is for soldiers. For each unit type, gotta sort of cycle through how many soldiers would build in that period.

Alright now for the civvy units. I guess we can use the same formula except do we really use CSL?
No, no need really...it's a bit different. Use the building's cap. 

t = 345600*(number)*Exp(1-townEngineers/capForLevelAtBuildingLevel)/(BuildingCap^2)

Building time fix:

Just use the Exp(1-townEngineers/capForLevelAtCSL) thing.

Knowledge fix:

Just use the same Exp thing above, but use average building level cap. I mean this basically means
research scales with your growth - you can be any size nation and still reach where you need to go.
How do reward larger nations? Well, we could make so you always need two levels above your average level
to get it down to 33% take off. So it gets harder and harder to keep up, the larger you get. Which I guess is kind of cool.
So let's just go off of their average level.

 Tanks v Juggers v Soldiers, what do they need to have to fix the imbalance?
 
 Currently DR is right, one Jugger getting 4x less cover is fucking ridiculous. It needs
 to change, perhaps being removed entirely. In the tests that we've found, where
 1 tank = 10 soldiers, does not include the fact that each weapon class is stronger.
 
 So a tank holding two tier 2 weapons is on average gonna be 10% stronger than soldiers
 holding two tier 1 weapons - though soldiers can hold tier 2 weapons. If the soldiers
 did wield a tier 2 weapon, then the tank is even with the soldier, but the soldier gives
 up it's diversity in exchange for this power up, so that cancels. The tank does not have to.
 
 So the tank can be, at max, 10% more powerful than 10 soldiers. It's actually worth 11. And
 it's 2x as fast. The formula for the next weapon is 2(old)+.1*old, so 100+10 = 110.
 
 So for Juggers, we get 2(old)+.1*old = 2(2(soldier)+.1(soldier))+.1(2(soldier) + .1(soldier))
 
 = 4s+.2s + .2s + .01s = 4.41s, when equipped with two tier 3 weapons, as opposed to like
 eight tier 1 weapons or four tier 2 weapons. So they actually are let's see... .41/4, which is
 about 10-11% stronger than 40 soldiers. So in both cases we have two units that are 10% stronger than
 their equivalent in soldiers.
 
 But the Jugger is 4x faster, and the tank 2x faster. Well, if they are 10% stronger, why not
 make them each only get a 10% detriment in defense? Then tanks count as 11, and Juggers as like 44.
 So they hit harder but take more damage. This doesn't account for the speed - though if we delete
 the speed, and we do this, then the balance does not give way to diversity but to sameness.
 
 You NEED soldiers to scout. You NEED bombers to bomb. What do you need tanks and Juggers for?
 Well, we can give the Juggers the speed bonus - them getting such a high speed rating is awesome.
 Tanks are kind of halfway there, 2x the speed, so they need something else. 
 
 If you made all the cover sizes the same as the actual sizes, what you could do, is allow for
 tanks to be worth less in cover. They could only be worth 90% of the CSL. 
 
 That just seems kinda one sided. Clearly, you need to think of something...
 
 1. Soldiers = Scouts
 2. Tanks = CSL Reduction?
 3. Juggers = Moar cargo?
 
 
 
 
 
 Iterators 2.0:
 
 Iterators do not load players, the search for any sql entries with
 timers and decrement them, moving across the playerbase. When a timer hits zero,
 the player is loaded if it is not already, as well as any other players if they are not already,
and a flag goes off to let everybody know in all possible tomcats that this player cannot be accessed.
Then the data is calculated and rewritten..

The only players which would necessarily be retained in memory would be the ones with programs
activated...but even they would no longer be necessary when you separate the programs onto
separate servers!

To do this:
1. Change everythign form players.size to like getPlayers().size and these players will only
be loaded when they are needed, and taken from memory if they are already in it.
2. Switch the iterators over to timer decrementation.
 
 
 Gigabyte 2.0:
 
 Separate servers of 25 players each with a battlehardcall thing in BattlehardFunctions
 that just makes server calls and parses. Have an is=alive on each gigabyte server, and it loads
 and run player threads and isalive returns nothing then restart thes erver, separate the players up
 and run gigabyte tests on them. Spin up a new gigabyte every time you need one or one dies.
 
 Dad's evolution:
 
 Okay so on the UI for HQ, under the Cover meter there will be a slider for 
 quality, and on it will be a notch from 0-200%. This notch represents the average quality of your
  troops relative to their standard blueprint level. When you send an attack you 
  move another slider along there to choose the quality of troops you want to send into combat.
   If send really high quality ones and you lose them all, then the average quality 
   level goes down. Vice versa happens when you send crappy ones. So your population of troopers
    evolves over time based on the quality of the men that are dying, like natural
     selection except governed by you. I know how to do it. I just need your thoughts
      on it and some other people's. Is it needed, would it make the game better?
 
 
 
 
 
 Capital Cities:
 
 How do we implement them?
 
 Well, the easiest way would to just have a town ID associated with each player
 that is their capital city. If it's negative 1, they have not set it yet, or have
 lost the town(you must set this up.) 
 
 If it is negative one, they can do makeCapitalCity(tid) on some place. Then,
 if the town called for building, leveling, etc, well, really just building buildings,
 is using lotNums beyond it's means, it's a-okay.
 
 
 
 
 
 
 
 
 Rewiring the Alamo Effect:
 
 Some goals of this discussion:
 
 1. Max limit on the Alamo effect needs to be a % based on your stealth tech and theirs.
 2. The new Alamo calculation needs to allow for a normal sized army to defend a normal sized town,
 and it needs to be proportional to the building levels in the town. Stealth and concealment
 cannot come into the calculation - concealment is what's altered, and stealth must determine the
 limit at which concealment cannot be altered further, but someone MUST be able to easily
 see what size army they can send.
 
 Okay so question: How large should an army be based on building size? Should it even be based on buildings
 or just on mines? Mines are what determine the army. But really we should just go based on aggregate weighted
 level. 
 
 Just make the town one giant building with the cap on it - it already works for troops as they price like civvies.
 Then multiply it by au slot size  because six different unit types means generally they will afford
 6x as many units as if they had one unit type, and they'll easily over extend. So it'll scale with them!
   
   From this we determine the bottom of the exponential and the top we get just by calculating the normal stuff.
   
   Now, the limit on this exponential should be based on your stealth tech and theirs. How do we compute that?
   
   Your max decrease in attributes
   is 1-.035*stealthTech. So it's good to have a high stealth. This means a farmer can fight Alamo, but not permanently.
   The best they can hope for is 70% limit. At the default 3, they'd get a little over 10% protection from Alamo.
 
 	And then finally one derives concealment from this. Or maybe, we could derive all things?
 	Hell just reduce fucking everything. As long as tanks and juggers contribute 2x and 4x as much to being hard to hide,
 	then the 2x and 4x relation for speed works out.
 	
 	
 	
 	
 	
 	
 	
 Rewiring Exp Adv in combat:
 
 So generally, we want the expmods to be based on hwo much
 each unit is worth. This is still fair as they all do proportionate
 damage. But what we want is for a 5:1 in a town of x pop, where the
 5 gets less concealment, to be the point at which the exp adv
 they gain from having those numbers outdoes that loss.
 
 So, generally, speaking, the defensive side does 5x more damage/person
 in concealment than the offensive side due to their concealment lack,
 actually 10x if they are attacking.
 
 But the 5 side is doing in general 1x damage/person but there are 5x as many,
 so actually the thing is quite even unless they are attacking. In this simple
 system we are just assuming that the...let's just get the eqns.
 
 			double expTerm1 = Math.exp(-(currentExpAdvSizeOffWithDivMods+1)/((sigmaTerm+1)*holdAttack.town1.getStealth()*(holdAttack.town2.getPop()+1)/2));

		okay so assuming everything is the same, we look at Exp(-5/x)/Exp(-1/x) = .Epx(-5/x+1/x)=Exp(-4/x).
	
 Okay so then we can say that is the ratio of C_5/C_1. This is directly
proportional to the ratio of damage they do per soldier to one another, assuming
that they both gun for concealment(even if they don't, when x is a certain number,
it naturally comes out to choose that frac, and this is the situation we're looking for-
not a small army in a big town defending from a big army but small army-small town, big town.

	 But generally...exp advantage is applied whether or not you hit a small town...that's
	 what we want to stop - smaller armies getting creamed to shit. The alamo effect
	 simply does not apply in large towns with small armies that are being rebuilt.
	 How do we protect them?
	 
	 Hrm. Well, if we set the limit for C_5/C_1, we generally want that exp advantage
	 should be equal given same x's. But we can't, there is a second variable, x,
	 that prevents us from isolating when it is.
	 
	The answer is, we need to ask, independent of concealment, how curved do we want
	that advantage line to be? At 5:1 the 5 gets only 30% of damage done to it
	and 1 gets 27% more damage done to it. We already knew 1 was screwed, but should
	the 30% be there? I mean shit, that's like next to nothing on the 5 army died.
	
	At .15 for a constant, we get that the 5 guys take 54% damage, and the defenders
	take 112% damage. Now if they were fighting in a small town where the 5's concealment
	was degraded to like Exp(-5/1)/Exp(-1/1) and you get .018, then they are taking about 50x more
	damage due to concealment. So the 1 generally does .6 soldiers worth *50 of damage on the 5, downgraded
	to .3*50 = 15x by this new formula, and 9x by the old. So they actually do take out
	more soldiers per capita.
	
	I think we need to degrade to .1, not .3.. That means at 2:1
	you experience 90% damage on 2 and 105.1% damage on 1. Not bad, it's a nice little
	reward for outnumbering, but not too large. And at Exp(-2/1)/Exp(-1/1)  you get
	.36, which means the 1 does 3x as much damage roughly as the 2, and the 2 does
	2x as much damage as the 1 would normally do without the conc advantage
	because of having twice the numbers, and after exp adv, the 1 does
	3x*.9 = 2.7x, and the 2 does 2x*1.05=2.1. So in the 2:1 situation, the 1 guys
	actually hit a little harder, and probably keep doing so for some time. When do they not?
	
	At 4:1, it's 74% and 107%, respectively. This is a nice number because the damage
	gained by the defense grows slowly with size, since they're being pummeled anyway,
	while the real reward for the offensive is losing a hell of a lot less.
	
	It's gonna need to stay this way if we're going to make it hard to kill small armies
	in small villages, because this can't have an effect until large size.
		
	Now how do we reduce it further using armor? We want to make having higher armor
	an absolute reduction of the exp-adv - a way to form it off. So it's got
	to be that armor somehow reduces the damage taken
	
 	Well we could use the fraction of armors on the exp...so if you had half the armor
 	and you were attacking, then they'd do 2x more damage on you than normally,
 	
 	So it'd calculate the damage dealt, and  then it'd multiply that by uh...
 	well the difference in average armor could be huge...you'd have to calculate
 	it and it could make a guy who was only receiving 86% due to numbers get a 1.5
 	and get hit even harder...and then when somebody is getting pummeled they'd switch
 	to high armor units that hit concealment and they'd get like 50x damage from concealment
 	and then get a 1.5-2 modifier on the exp adv and just destroy incoming armies.
 	Not sure how fair that'd be. Hell, it'd be kinda cool, because those defending units
 	would be like unable to go anywhere quickly - highly armored behemoths with sniper rifles...
 	
 	So what we want to do is not increase the damage done by the unit but reduce the damage
 	taken. So a max would be you take 0% damage due to armor, and that'd mean if you
 	hit somebody hard in concealment due to low conc-high armor, the high armor could
 	actually offset that loss. That cannot happen, I will not do it.
 
 
 
 
 
 
 
 
 
 Quest System:
 
 How should the quest system work? Well, I want people to be capable of being
 on multiple quests, and I want quests to have a quest line. I want the quests
 to be listener programs that get "iterated" every time the player does to check
 for things. 
 
 So do we want a quest database, and all the quests are loaded in server start up?
 That'd certainly be the easiest way to do it, and then there could be an array
 of Quest objects that are loaded classes with names, and when people choose
 a quest one is instantiated and tacked onto their player. There'd be a second
 table that links players to quest ids. We'll make quests a separate db.
 
 So the quest-player table will be pid,qid,completed, and if completed is -1,
 it hasn't been loaded, if it's 0, it has been loaded but not completed, and if 1,
 has been completed. So we'll have a Quest object that every Quest must implement,
 and there will be certain methods they must have:
 
 1. iterate() method - this method is called every time a player iterates.
 2. Add Player method - if a quest is going to contain multiple players, it needs
 to keep an array list of them, and they need to know of it by having it
 in their array list. This is where you can check whether or not to reject players.
 3. rewardAndDestroy() method - what happens if the iterate finds that the quest is complete
 and it should kill itself by doing the rewardAndDestroy method where it wraps
 everything up, and then does the markDone method.
 4. complete() method - already set up as part of the interface.
 
 Then the player just iterates all their quest listeners every 10s.
 
 Things to do:
 
 1. Create the QuestListener interface. ---CHECK----
 2. Make it so God loads them on startup, and the programs
 are each attached to the players, and that they iterate.---CHECK---
 3. Make a getQuestCompleted(qid) function to player.---CHECK---
 4. Make it so player reload rethinks quests. ---CANT, MANY-TO-MANY, MAY BE VARIABLES
 THAT THE QUEST HOLDS ON TO!---
 
 Tests to do:
 
 1. Write a quest that just tests to see when you get a certain res hit. Have it reward and
 then destroy your player off it.---CHECK---
 2. Write a quest that won't let you join unless your player has a certain quest completed.(Last)---CHECK---
 3. Test out the addTown and killTown methods.---CHECK---
 4. Test out the storage and questlog stuff.---CHECK---
 5. Print out the players and make sure quests only get repped once.---CHECK---
 6. Test that support allows you to remove/add players to quests.---IMPOSSIBLE.---
 
 
 
 
 
 
 
 
Resource advantages in certain towns:

Install resAdvantages that are stored in towns in the inc sections as percentage points.
Now we have a new use for them. Each town starts with a default of zero but when you
do the Id repop it can create some that have different things.
1. Create the array---CHECK---
2. Create readinwriteoutsynchronize procedures---CHECK---
3. Modify the League suck up equations iterate and your iterate---CHECK---
4. Modify the variables in the tres databasaes to be doubles.---CHECK---
5. Include resAdvantage or whatever in this fucking town of yours. JSON style.---CHECK---
6. Modify placement code to autochoose place of 0000 for beginner player.---CHECK---
7. Modify growId to make every fourth town a normal one. Then the placement will
auto work with the new system.---CHECK---







 Memory Cycle:
 
Could turn God's getPlayers() array into an array of like player-object like strings
that have a get method that return a player if there is one so getPlayers().get(i)
returns first an object like PlayerList, and then PlayerList has a get(i) function,
and if the get is called, then a Player object is loaded up and returned. A separate
AI will load and iterate the players. As the players need other ones, those players are loaded
up in a very clever way. Then they can still do the work, without the problems.

See, lets say player x needs player y and player y has support units from z. Then
x calls y who gets loaded up and in turn calls z who gets loaded up. X then does its stuff
and all the players are dumped. Then y gets called up and loads up z, and so on.
The separate AI doing the iterating would be like an iterator, there'd be like 100
of these threads sucking up players and iterating them. They'd of course check current memory -
if the player was loaded for another, and hasn't been dumped yet, it'll on synchronize,
just iterate that one.

Of course, if a player has "canJava" on, then it takes up a slot permanently, if it's program
is currently running. If not, it slips back into the fold.

To do:
1. Make a PlayerList object that holds names of players, and has a get function
that loads up ones that are not currently in memory. You'll need to redo
the way God loads up - it simply doesn't need to anymore, no more threading for God.
2. Create the iterators, which call get periodically on players who's current
tick rating hasn't been updated yet(keep this all in memory in the playerlist) 
and run them(so they'll naturally call ones already loaded or load them if need be).
3. Make it so the iterators do not unload a player once it's been iterated if it's
running a java program.
4. Make God just do a counter iteration, for the GodCounter.
5. MAKE EVERYTHING SYNCHRONIZED TO AN EXTREME DEGREE!
6. Make the iterators always seek the lowest tick and grab it if they can(synchronized).
7. Make the Watcher AI, which watches Iterators and sees if the number of players
unprocessed goes to zero before the game clock factor goes past, and if it doesn't,
then increase the number of iterators.

To just make a thread pool:
1. Create a GameClock that God increments and each player has one
2. Iterators search players that do not match current game clock. Once
they detect no players have a lack of this game clock, they then go and request
a new gameclock from the server.

Valid quasithoughts: You obviously have to choose one player, and then
once it's loaded, try to load it's raidsntrades, and then those'll load
the other ones as required. So then it'd all work. So now, it'll do like synchronize,
it'll load just ONE player, and then ask for that ONE player's raidsntrades,
and each get will be like a little spark calling another player. I mean,
that's how we find these players, right? By loading them up! Now, if a player
is loaded up, it's only gonna remain in memory for as long as it takes the iterator
to find it needs an update, update it, and trash it. So if you've got 100 iterators
and some 10,000 players, chances are you're going to have a few thousand loaded
and the iterators will be dropping them out as fast as they can but more likely than
not, you'll have 9900 loaded and 100 being loaded again. But that would only happen
if the time it takes to iterate all the players through and dump them is greater
than the gameClockFactor - then clearly then the iterators are overloaded, 
and so the players in memory will get dumped only to get lifted again, and dumped,
and so on, because they'll all be lagging behind. Eventually you'd get somet hat
are five ticks behind, and you'll need to go to the lowest tick first, but if you can't
keep up with the counter, you're screwed. You'll need to make a protection for that.

 
 
 JPI Security AI:
 
 Okay so what we need to first cut out any trailing white space between
 periods, and then do a basic contains test that tests for a battery
 of different statements and/or keywords that we can store in a DB.
 Then if we make any hits we test to see if it's in quotations.
 If it fails we send it back. 
 
 
 Okay use the link and code to have a single AI that takes in and measures
 program usage. This AI will exist as a separate program on a separate
 server. It's job will be to create mini-programs that do memory tests.
 So what users will do is when they compile, the program
 first goes through above and is checked, and if it passes that stage,
 it is passed to this AI(by setting a flag) that puts it on a queue to be measured.
 Then once it's been measured and passed, it gets checked off as approved
 and then the program can be loaded by the user.
 
 The AI will have to load player copies(all the way down to other players?) and use those in the class and let
 it get initial memory, and if it's above .025MB, kill it off. Then,
 do a join() and wait for it to finish - start a separate timer thread
with a random number of ticks and then store the reference to that thread.
Come back and check it later and see memory usage then. If the program
isn't dead yet and memory is growing,
you can say there is a memory leak. If it has any memory remains
below the limit, then it's okay. Then the program shuts itself down and updates the database.

Also have a second thread that checks the AI and if it's own memory grows beyond say 10Mb then
kill it off to save the server.

So the different states are:
-1. Latest compile failed.
0. Ready to Submit Compile/Latest Compile Successful
1. Compiling
2. In Gigabyte processing

So at any time they can hit run and if their class file exists, it'll run it,
but if they want to submit a new one they need to go through the process.

The second they hit compile, then it goes to 1. Remember
that the class loaded should not be loaded into here, only made into a class,
to test if it compiles. This should be done on Gigabyte and sent back
as if it did or did not compile. This way, the old class file remains intact!

To do:
1. Create Gigarun.
	-0. Create a Special God Constructor that creates a Ghost God with only all that player
	requires.---CHECK---
	-1. Load up and compile class into folder after mkdiring it. If it doesn't compile,
	set error equal to what ever.---CHECK---
	-2. Run the file and then join().---CHECK---
	-3. Do the time subtraction to figure out whether or not it's growing.---CHECK---
2. Create Gigawatch. ---CHECK---
3. Create Playerscript method that calls it.---CHECK---
4. Create Playerscript method---CHECK---
5. String checker---CHECK---

Tests:
1. Create table checkTable with badstring and id here and online.---CHECK---
2. Set up separate server and change all the relavant IPs(gigaIP), check Gigarun, Gigawatch,
PlayerScript, and God for all this stuff to make sure it's okay.---CHECK---
3. Install Gigabyte on the private server.---CHECK---
4. Create a call to God on your server that allows for him to send out a compile request
for your player.---CHECK---
5. Debug the shit out of it as it goes.
6. Once it runs through once, print out memory usage and stuff on Gigarun and deeply analyze it.
7. Test long term durability of Gigabyte by leaving it on for a few days and making periodic
calls.








 API Upgrade:
 
 Things I want to do:
 
 1. Switch over to tid only. Means overloaded methods.---CHECK---
 2. Each method now has the same argument order.---CHECK---
 3. For each one, different parser code.---CHECK---
 4. UserPlayer object, has UserTown objects.---CHECK---
 
  lotNum, townID, number
  String type, int number, int townID
 
 
 
 
 Theory of different tick lengths:
 
 If the tick lengths were set at once every 10 seconds,
 and all the wait times were multiplied proportionately, then
 you'd have longer processing times with the allowed same effects - but you'd have
 to make it so that no event could happen in between those 10 seconds. So raids
 would always have to have integral multiples of 10, things like that. Resource
 increases would be every ten seconds, and basically raids/trades/tradeschedules/buildings/queues/
 anything with a timer must be modded to ten seconds.
 
 I mean really if you made all the "wait time" changes by ten it'd work as a game
 but then the game would be ten times slower - so you'd probably want to divide
 all the times by ten and multiply all the incs by ten. You could actually set this
 up as a "game clock." You'd divide intervaltime by ten for tradeschedules because people will
 input that in seconds, undoubtedly.
 
 resIncs---
 Raids,trades,tradeschedules,research ---
 Units,people,buildings ----
 Can't think of much else to do, Maelstrom and Trader are separate!
 Send attack in BF.----
 Testhold in various God logic(CTRL F).----
 
 
 
 
 
 Theory of Asynchronous Operation:
 
 The basic idea is there is a timer and we use that timer to make the player do everything
 it would have in that time period. So whenever the user uses BHFunctions, whenever he
 hits any command, then synchronization happens first. Now players are not lone objects.
 When a player loads a world map then all players inside need to synchronize. Whenever one player
 has a raid or a trade transferring to another player, that player needs to be synchronized first, too.
 Basically in this way an expanding web of synchronization happens in webs across the game as
 people play it, as opposed to it all happening at once at a steady cadence. 
 
 So the protocols:
 
 1. Whenever a BF call is made, synchronize.
 2. Whenever a controller call is made, synchronize.
 3. When worldMap is called, sync everybody in that area.
 4. When a Raid reaches it's end, synchronize both parties involved.
 5. When a Trade reaches it's end, synchronize both parties involved.
 6. Add a sync timer so that as you add over cities, if it takes time, then
 the resTimer will have it account for it in the additions as it's adding instead
 of going through everything again, so that if it takes like 15s for Id to do one iteration,
 then it will ad 4 s, then 5s, then 6s, to each resource as it goes through towns,
 and have it cycle through each building, trade, and raid accordingly. May
 have to rewire building server a bit.
 
 
Support System:

Chg = 0 is normal, chg =1 on player means pause,
chg = 2 means take it all in again.

CHECK FOR INCOMING RAIDS BEFORE YOU DO THIS - IF YOU DO DURING A RAID, THE OTHER
GUY WILL LOSE HIS MEN BUT THIS GUY WILL NOT!

DO NOT DELETE BUILDINGS. DO NOT CHANGE THEIR TYPES.
If you have to, make sure there are no outgoing genocides/glassings and
that all queues in that building are deleted as well.

Tests:
1. Test the new loadPlayer for support.---CHECK---
2. Test changing player level attributes(if any)---CHECK---
3. Test changing town level---CHECK---
4. Bldg level---CHECK---
5. Queue level---CHECK---
6. AUTemplates, AU, and supportAU---CHECK---





The Weather System:

Let's think. How do want this to work. Generally we know we want
each "unit" of weather to take from one attribute and add to another.
There are, of course, seven attributes to take from, leading to 7*6
= 42 different weather combos.

We want a random walk quality to this system - we want individual units
to have a randomly generated speed(weighted, of course) and we want
them to combine with other units when they hit each other. When they strike each
other, their new velocities need to be computed as well as their new momentums.
I'd like the new combos to slow down also, and be harder to change direction.
They will of course combine effects.

Every unit will have an average depletion time before it dissipates and combos
will be the weighted average of their times, so that big ones can't gain
too much traction. There needs to be a conservation thing so whenever
one dies, another is created. The number needs to be proportional
the size of the server and entry points need to be random.

Okay so we need to set up some...plans.

1. Create the weather AI.---CHECK---
2. Create the weather unit, which keeps track of combos.---CHECK---
3. Persistence---CHECK---
4. Make the weather AI drift the weather units---CHECK---
5. Allow weather units to combine, make them slower and combination of speeds---CHECK---
6. Make it so the weather AI creates new weather combos at the rate
they deplete.---CHECK---
7. Make a getter method for info on weather.---CHECK---


Tests to Run:

1.Turn it on with 5 towns in the large square and observe what it creates and destroys.---CHECK---
2. Observe clouds merging by making the maxX and maxY a lot smaller and decreasing the wait time significantly.---CHECK---
3. Hardwire in a trade effect and test it.---CHECK---
4. Hardwire in an engineer effect and test it.---CHECK---
5. Hardwire in a resource effect and test it.---CHECK---
6. Hardwire in a combat effect and test it.---CHECK---
7. Test persistence.---CHECK---








The League System:

So, the league system. What are some attributes that I want?
-I want a taxing system
-I want a league bank
-I want the league bank to have traders
-I want the league to be able to assign ranks
-I want the league to be able to have a description page and website link.
-I want the league to be able to have different permission sets.
-I want those with permission to be able to download a grid of their users as a Jason
array and a grid of all enemies they have attacked or have been attacked by in the last ten days. They
can limit by league if they wish.

Alright so let's think of implementation.

So we need to rethink this shit: Okay so I want to work within our system.
I want there to be "league-owned" towns and a league player. The first question is:
Given a league town, if you have a lot of members, your league town will not
exactly have the caps to make having warehouses useful. So you're going
to need a special kind of player, called League. Leagues will have towns, certainly,
and they will have extra permissions too. So leagues will have extra entries with
descriptions, and a table of tprs. The thing is the towns they have will have overloaded
building add methods that do not allow additions of warehouses. The towns will
be able to build traders, men, have units, etc., and will have places on the map.
You'll have to log in through your communications center - the initial player will
have the so called "Master Pass," but others'll have to go through the communications center
login to log in and it'll check TPRs. 

So then what does the communications center do for you? Well, instead of gathering
average trade tech or anything like that from players, we set the resCaps of every town
the league player gets by using the weighted average cap of communications centers.

Also, league players are not allowed to invade. They can only be given towns. So
league towns will often be in areas near other players.

Okay so list of things to do:
1. Create the League that extends player, with all the tprs and everything
you need for data creation, and persistence. You will have to initialize it
like a player, use that constructor(easily done) and then if it has
league affiliation, enact the league constructors. However, I do not think it will
need a league constructor if you do it correctly. Let it do everything a player
does already, just override methods that allow for certain buildings.---CHECK---

2. Expand tprs to include tids (if they are moderators.) Then these moderators can
only have access to so many towns. Admins have access to everything.---CHECK---

3. Utilize the createPlayer through battlehard functions to create leagues off
of player-given towns. Control functionality to remove privileges, add them, etc.---CHECK---

4. Think of a way to easily implement league control functionality into bhf
by moderators/admins without logging in - do they get the league player reference
in their player to access? What? Probably the easiest way is if they
control those towns, to just put references in their towns array. But then they'll
be doing cycle-throughs, so put it in and if it's a league town, don't do anything to it
- it just allows those players to access and modify those towns.---CHECK---

5. Implement tax rates.---CHECK---

6. Implement persistence---CHECK---


Tests to do:

Messaging Leftovers:
1. Test out usergroup creation---CHECK---
2. Test out usergroup deletion---CHECK---
3. Test out usergroup get command---CHECK---
4. Test out user group update command---CHECK---
5. Insert/Update league user group for all league members to use.---CHECK---

League:
1. Create a league.---CHECK---
2. Invite someone to the league.---CHECK---
3. Accept the invite.---CHECK---
4. Set up some different tprs, test how town access works out.---CHECK---
5. Test out some different commands, see how that works out.---CHECK---
6. Take an Id town with League and see if taxes distribute properly.---CHECK---
7. Try to give somebody a new town permission.---CHECK---
8. Persistence. ---CHECK---
9. Delete league member.---CHECK---


 Mines and Warehouses:

 Well, clearly warehouse storage amounts have to grow exponentially. The
 amount they grow should be the sum of their cost (we use this instead
 of a straight number so we have the flexibility to change it). To make
 the resource cap work, we first need to figure out resources.
 
 Okay so all of the mines combine together to contribute to an easily
 referencable inc variable. So we create the mine buildings and
 you need to modify create player to "add" these buildings
 and lotTech to be stored correctly. The mine buildings each
 contribute to the increase rate, so whenever one is increased in level,
 we re-adjust the resource increase rate for that town. Now, these
 rates are per second. Unfortunately, this means they can't be less than one.
 We need to modify the database to except doubles for this job, and
 then create a holder "double" for each resource that isn't recorded,
 and every second, these doubles are added to. When the double >= 1, 
 then that double is emptied till it gets to <1 again.(Because at one point
 they may make more than 1/sec.)
 
 Every turn if the resource to be added is greater than the total warehouse
 amount(easily calculable and kept till a warehouse levels) then we do not keep
 the resource.
 
 To do:
 1. Implement mine buildings.---CHECK---
 2. Store new ones in the db for each and every town with a quick textdemo type program.---CHECK---
 3. Alter create player to have these buildings.---CHECK - ID ALREADY HAS BUILDINGS ON CREATION!---
 4. Create the non-stored double holder variable array and delete the db incs,
 calculate inc on level up of building or server start.---CHECK---
 5. Create the non-stored caps for each building at load up and adjust only when
 one levels up, or on server start.---CHECK---
 6. Create the protocols in player to increment the holders with the incs and to suck out
 all whole numbers whenever >1 to be added to the real resources, and to not add more
 than the caps.---CHECK---
 7. Make mailing system more manageable by allowing hashes of user groups and users
 to be sent in, created, and destroyed. Do this by creating a user group
 table that points to the player, and there is a user table that has entries
 of player names and user groups, so that each entry points to a group. Create
 the commands for battlehardfunctions to get and receive these. Do not have to change
 the message table structure though, just create multiples. Can just add a "subject id"
 so all have that same unique identifier for combinations! Then you can do google like
 email. Also add creation time.---CHECK---

 
 Tests:
 Mines:
 1. Test resource rate gens by printing out small increments over time and setting it low.---CHECK---
 2. Do it again but make sure whole numbers add.---CHECK---
 3. Hit a cap.---CHECK---
 
 Mail:
 4. Send a few single messages, see if number of packs increases correctly.---CHECK---
 5. Send a few more messages, tagged to each other for each one and see if they group correctly.---CHECK--
 6. Delete the first, middle, and last messages, respectively from the pack, see how it works out.---CHECK---
 7. Test that referring to later messages in a tree works (for original_message_id.) ---CHECK---
 
 
             	
 +-------------+------------------+------+-----+---------+----------------+
| Field       | Type             | Null | Key | Default | Extra          |
+-------------+------------------+------+-----+---------+----------------+
| name        | varchar(30)      | NO   |     | NULL    |                |
| slot        | int(11)          | NO   |     | NULL    |                |
| lvl         | int(11)          | NO   |     | NULL    |                |
| lvling      | int(11)          | YES  |     | NULL    |                |
| ppl         | int(11)          | YES  |     | NULL    |                |
| pplbuild    | int(11)          | YES  |     | NULL    |                |
| pplticks    | int(11)          | YES  |     | NULL    |                |
| tid         | int(10) unsigned | NO   | MUL | NULL    |                |
| lvlUp       | int(11)          | NO   |     | NULL    |                |
| deconstruct | tinyint(1)       | NO   |     | NULL    |                |
| pploutside  | int(11)          | YES  |     | -1      |                |
| bunkerMode  | int(11)          | YES  |     | 0       |                |
| bid         | int(10) unsigned | NO   | PRI | NULL    | auto_increment |
+-------------+------------------+------+-----+---------+----------------+
name				slot	lvl		lvling	ppl		pplbuild	pplticks  tid	lvlUp	deconstruct	  pploutside	bnkrMode	bid
| Headquarters      |    7 |   4 |     -1 |    0 |        0 |        0 |    4 |     0 |           0 |         -1 |          0 |  12 |

            	 
 
 Code to generate mine buildings:
 
 package BHEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.DefaultListModel;
import javax.swing.JApplet;

import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

public class TextDemo {
	static final String TIME_FORMAT_NOW = "hh:mm:ss MM/dd";

public static void main(String args[]) {
	try {
		   Class.forName("com.mysql.jdbc.Driver");
		     
		      Connection con =
		                     DriverManager.getConnection(
		                                 GodGenerator.url,GodGenerator.user, GodGenerator.pass);
            Statement stmt = con.createStatement();
            Statement stmt2 = con.createStatement();
            ResultSet t = stmt.executeQuery("select tid from town;");
            stmt2.execute("start transaction;");
            while(t.next()) {

            	
            	stmt2.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp," +
            			"deconstruct,pploutside,bunkerMode) values ('Metal Mine',0,3,-1,0,0,0,"+t.getInt(1)+",0,0,-1,0);");
            	stmt2.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp," +
            			"deconstruct,pploutside,bunkerMode) values ('Timber Field',1,3,-1,0,0,0,"+t.getInt(1)+",0,0,-1,0);");
            	stmt2.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp," +
            			"deconstruct,pploutside,bunkerMode) values ('Manufactured Materials Plant',2,3,-1,0,0,0,"+t.getInt(1)+",0,0,-1,0);");
            	stmt2.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp," +
            			"deconstruct,pploutside,bunkerMode) values ('Food Farm',3,3,-1,0,0,0,"+t.getInt(1)+",0,0,-1,0);");
            }
            stmt2.execute("commit;");
            stmt2.close(); t.close(); stmt.close(); con.close();
            
            stmt.close();
} catch(ClassNotFoundException exc) {} catch(SQLException exc) {exc.printStackTrace();}
}
static String nowTime() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_NOW);
    return sdf.format(cal.getTime());

  }

}
 
 
 
 
 
 
 
 
 Research System:
 
 You need to implement missing techs - like Bomber and Bomber resistance Tech.
 Have you make "discoveries" to research or choose among a pool based on your experiences.
 So if you fight a lot, combat researches come up more often in your discoveries.
 
 	RESEARCHES:
	LotTech, StealthTech(maxed at 20, see scouting block), Unit Techs, Weapon Techs,  HP Increase Tech, Support and Attack conglomeration techs, Next Town tech,
	Each of the civilians can have it's efficiency upgraded, building slot tech, building stability tech, bunker tech
	Engineer, Trader, Scholar Tech, add 10% efficiency to time decrease, carry alotments, everything, for each and every one!
	
	MAKE SURE TECHS ARE COUPLED LIKE BUNKER TECH WITH SOMETHING ELSE COMBATIVE(weapons?).
	
	Stealth is more important for offensive, couple with Bunker Tech, a defensive trait.
	(Approximately twice as useful for offensive as defensive, if you check. This is how
	it's paid for.)
	
	Trade Tech has a max of 10.
	
	You need to edit Maelstrom to include the Research System.
	
	Techs:
	Lot Tech
	Stealth
	attack lot tech
	4 unit techs
	IMPLEMENTATION REQUIRED
	Troop push
	Bunker level up
	IMPLEMENTATION END
	21 weapon techs
	Support tech
	Town tech
	3 Civilian Efficiencies
	Building Slot Tech
	Building Stability Tech(not implemented, I don't think)
	bunker tech
	Bomber Tech
	Trade Tech
	Pic techs[35]
	Assume each has a max of 10 except stealth tech.
	Total: 
	10*(9) + 20 + 21 + unlimited town techs + 20 stealth + 35 = approximately 200 tech upgrades. <--- NEED TO REWIRE THIS FORMULA
	AFTER ADDITIONS
	
	LotTech is for building slots, buildingSlotTech is for how many can be building
	at a time! lotTech needs implementing.
	
	God damnit we need a better system. We clearly cannot use knowledge as a "resource."
	We need to use the people directly. Have an AI That works a little harder
	and a little faster as your numbers increase. So if we have each breakthrough
	becoming "available" at longer and longer time intervals...say we have it grow not
	exponentially but like x^2.5 where x is breakthrough number. We have 200 technology
	upgrades to worry about and we want them to only successfully be able to get 25%
	of them by the end of 12 months with no scholars. We want 4 techs chosen ON AVERAGE
	per breakthrough, which means 4 techs a break through means 200/4 = 50 something
	breakthroughs for everything. We want 15 in 12 months. So what y,
	x^y s (where x is breakthrough) when x is integrated from 0 to 15, gives 12 months?
	
	Let's use mathematica. Using y = 6.099 gets us about a year.
	
	Now we can lower breakthrough times by doing something like
	
	x^y -> divide into two parts  1/5th + (5/6ths+.5(5/6)*towns.size)* Math.exp(-Math.log((totalScholars*(1+t.player.God.Maelstrom.getResearchEffect(t.x,t.y))+1))/(10))
	 
	 
	(just use the building level up code for this)
	
	on average you get 4 PER UPGRADE but don't really get 4 per research - you get 2 for the first ten,
	then 3 the second, then 4 the third, then 5 the fourth, and 6 the fifth - this adds to
	2+3+4+5+6 = 20 * 10 = 200 researches
	
	So there is that base time protection, and every time you add a town, you get 
	an extra fifty percent of the deductible added on, but once you get that town
	up to full status, you get an effective exponential factor decrease that makes it better.
	
	
	Now to group technologies. You get 10 available techs, can only choose 4.
	

	Defensive Tech 
	Stealth - Every round
	Bunker tech - Every round
	Building Stability Tech - Every round
	Defensive Might - If you're winning defensive battles(ie taking out a certain portion more)
	Bunker level up - if you get like 3 defensive techs in a row, you are presented with this option.
	
	Offensive Tech
	Stealth(20) - Same as above
	Bomber Tech - Doing a lot of bombing relative to toher runs.
	Troop Push - If you do three offensive techs in a row.
	
	Military Output Tech
	Support tech - If you support or are supported often.
	Unit techs(4) - Every third breakthrough
	Weapon techs(21) - Randomly thrown in - but only if you have a unit that can
	equip them, or a unit tech is available to research that can!
	Lot Tech - If you max out like 90% of your lots.
	Attack Lot Tech - If you max out your attack unit slots, increase probability.
	
	Economic Output Tech
	Trade Tech - If you uh make a lot of trade schedules, have a high leveled trade center, lots of traders.
	Engineering Tech - If you have high CYs or a lot of Engineers
	Scholar Tech - If you have a higher number of scholars.
	Building Slot Tech - If you level up often
	Town Tech - Every second breakthrough or so. Given that there are a total of 12 in a year
	with no scholars, and maybe as much as 30 if there are, then 15 towns can be gotten.
	
	
	Probably best to do this one piece at a time and test it. How do you test it? Manufacture the situation
	in the db. This system may need user testing before it is fully viable.
	
	
	To do then:
	1. Create the framework and ticker. ---CHECK---
	2. Create the battlehard functions call. ---CHECK---
	3. Implement the new techs. ---CHECK---
	
	Tests to run:
	1. Test that breakthroughs happen on time and persistence.---CHECK---
	2. Test that you cannot grab unit techs or town techs or pic techs unless it's every 3, or whatever
	the rules are.---CHECK---
	3. Make sure breakthrough times are realistic(check each one for a bit.)---CHECK---
	4. Test out the new EngTech and ScholTech if you've got time.---NOPE.---

	
 
 For making new players, scripting:
 
 Need to create a new folder in userscriptsbin and src.
 
 
 
 Quest System Ideas:
 
 Like trade schedules if tid is -1, load the "quest town" for that player to attack.
 -2, load the other...and so on! Or keep them in global pointers, like questTown1. Or something.
 Or attach a copy of each quest town to a quest towns object in each Player to send to that's
 always some distance away from their main town! Cool, eh!?! Then when they send stuff, if
 you can't find it, it must be a QUEST TOWN. CHECK FOR IT! Or rather, have the front end
 allow them to "send" there via choice but then notify by calling an attack with a flag
 set to true telling you you've sent to a QUEST TOWN!
 v.09 list:
 To do for v.09!

Attack stuff:
1. Implement status reports to allow for easy viewing of attacks.---DONE---
1b. Allow for the ability to read in raids from God, no I don't care that they will be shifted around, it's necessary.---DONE---
1c. Allow read ins for the battlehardviewer, but no visual support yet...not necessary.---DONE--- (via statreports)
2. Fix basic attacking equations, with new HP stuff.---DONE---
		-Fix weapons values---DONE---
		-Fix equations---DONE---
3. Fix genocide by making sure civilians can be equipped with a weapon. ---DONE---
4. Create the glassing option, can be used with genocide or without, causes 25% decrease in collected resources, only with bombers.---DONE---
5. Implement veterancy.---DONE---
6. Implement supporting armies, and make sure they can attack if they are flagged to do so.(Also you must be able to support, not just receive.)---DONE---
7. Implement the scout function - can only be used with soldier units, and they must have a high concealment and be nearly unarmed to proceed.---DONE---
8. Implement invasion.(1.5wk)---DONE---
9. Implement any type of raid-recall(.5wk)---DONE---
11. Implement resupplying genocide and glassing runs(.75wk)---DONE---
12. Implement changing the bombing orders of glassing/genocide runs.(.25wk)---DONE---
13. Implement Bunker system. (2wk)---DONE---
14. Implement the pricing in returnPrice and the buildCombatUnit to include troops on raids and in foreign villages.(1day)---DONE---

15. Implement the HQ building, which controls raid slots. Supporting another player counts as a single raid slot. Will
need to rewire a lot of things to recognize this. Easy to find supports, just look for AUs attached to other cities
from your city, count the number of unique tids and there you have it! Can also derive the data this way.(.5wk) ---DONE---

16. Implement Queue system - this means that instead of keeping track of ppl left to build, there
is a separate queue table corresponding to queue objects stored in an arraylist on each building.
So for arms factories, a number is kept, and a slot focus, and so there doesn't need to 
be arrays anymore of numLeftToBuild or the columns in the SQL table, each query is dealt
with as it comes through the building server by decreasing ticks
and numbers left, until it's max units made are built and then the queue
is removed. This does not need to be done with people, you can just
have an x button there and ask for the amount or something.(1wk)---DONE---

Total:1.525 month remaining.

Do a round of Systems stuff here. Pick three major problems and solve them.
1. Memory leak in display software(1wk)---DONE---
2. Make God the primary reader/writer, have all players request updates through him or call his methods. Saves memory.(1wk)---DONE---
	-Can easily fix this by passing God's connection to the Players, this way they can keep using
	the same statement over and over again without a need ot recreate that statement every time
	the method is called. And if they lose the connection, they can call on God
	to re-establish it.
3. Fix the JPI to take multiple classes.(1wk)---DONE---
4. Fix PlayerScript to parse commands from display program.---DONE---


Menu stuff, after attack stuff:
1. Arms Factory unit build menu(1wk)---DONE---
2. Arms Factory unit creation menu(1wk)---DONE---
3. Bunker menu(1wk)---DONE---
4. HQ Menu(1wk)---DONE---
5. Combat simulator(1wk)---POSTPONED---
6. Implement concealment disguising raids from appearing, or that they even appear if one is coming to you.(.5wk)---DONE---


Total till next version: 3.5wk

 
 
 
 
Trading system:

Take into account that traders go up in price...may want to have them group
like soldiers.

Set up trading like raiding system.

1. Trade Routes:
	Alright, some options for trade routes:
	1. Two-Way or One-Way, Two-Way requires a message to be sent.
	2. Has Interval in Days/Hours, number of times can be any number up to 50 or infinity.
	3. If a trade cannot be completed for whatever reason, it waits.

2. Stock Market
	So we need to somehow keep track of exchange rates. We can use the trade raid system
	thing but we have to somehow denote it's a stock market trade and just have it switch
	off resources. We need a separate thread called "Trader" that measures the ratios
	at an instant at decides the exchange rate. Now, if we go by the rate of trades
	happening in a certain instance, then anybody can upset the way of things
	by just having a lot of trades of 1 resource. If we go by volume, then the
	largest players have all the power over trades. But this is how it works in real life
	as well.
	
	The formula for the exchange rate is total_resource1/total_resource2.
	
	It defaults to 1 if there are 0 trades on either end and has a max trade
	ratio of 10:1 to protect from like 1000:1. Nobody'll make that trade
	and it will stay that way.
	
	Also, trader technology increases the time spent doing trades, and the amount
	of resources a trader can carry, by 10% * trade tech. Also, stock market prices
	can be augmented, so you can get a better rate by 10%*trade tech.
	
	How do players make new trade routes? By negotiating directly with nearby players,
	via messaging. Yes, you could offer a market place where players could shop for new
	routes and post them, but then where is the haggling? The communication?
	Talk to Markus more on this.
	
	Also by doing this, by making it non-visible, people can make money by passing
	along info about trade routes to other people, say if guy x knows guy y wants metal,
	and guy z has it, he can take a commission for setting up something. This is so
	much cooler.
	
	When you reach larger playerbase, you'll need to implement randomly choosing 1000 players.
	Or maybe even just 100. It looks like 2^63-100000*exp(30) yields a positive answer,
	so even with 100000 players at high levels, you're fine, and that's unlikely. So you
	CAN worry about this some other time!
	
	Good news is that stock market trades are pre-agreed two-ways so they count by
	Trader's count!
	
	To make a stock market trade, we set up a pre-agreed twoway trade with the tid2 = 
	-1, and so then if when we're reading in this data, we see -1, we just set town2 = null
	and set a stockMarketTrade on the TS object to let us know it's a stock marketer.
	
	Why do we keep agreed when we can just check for a mate? Because an unagreed two way
	just sits there, an agreed twoway that just lost it's mate due to a cancel on the other
	end is most certainly agreed but it doesn't know it has no mate until it checks. Basically,
	if it has no mate, we don't want it to delete itself before the other person can agree
	to the terms, because it has no mate during the time it's hanging in space and the other
	user is thinking about it!

	How much should a trader be able to carry as a base value and what is it's speed?
	
	300 carry and 100 speed, because it costs the same as a soldier and a soldier gets 400 points to spread
	amongst itself that it could put ALL in cargo. But why waste that kind of room when
	you can get as much out of a trader? We don't want players using soldiers for a kind
	of cargo, and we want a fair trade amongst units. If it's 300, then people will spend
	money on cargo whore units and raid instead of trading. Granted, raiding loses
	you more units, but they'll just leave themselves open or something. But then you
	gotta think...well that's  alot of fucking resources...if it were 80 resources each,
	then each trader would pay for itself by itself but you'll give it that factor
	extra of help...but shit, it's 400 resources because that's what a soldier could
	carry for the same price but away from a battlefield. I don't like it because
	I think it'll backfire a bit, but given that people'll grow, I think it'll
	be useful in the long run, to promote heavy free trade!
	
	How much will having TSes take slots, not trades, effect TSes themselves?
	
	Well then it becomes about bulk moving. A ts that does a 100 resource trade
	every ten days clearly is not worth as much as one that does a 1000 resource
	trade every hour. So what you'll see is people trading quickly and powerfully, moving
	as much as possible. Granted, they are limited by the number of traders they possess
	at any one time, as some may be out on another big trade, leaving a dearth, but
	that's alright. People will maximize it.
	
	Are trade records useful for data analysis?
	No, because while they track the amount of traders required, they have 0ed out
	resources at the end since resources can be exchanged at both ends and so we have
	to keep track of that in the db instead of a fixed variable. So what you should
	look at instead are trade schedules - all trades are derived from there and
	so you can use a smaller dataset to get even more data!

	On a stock market trade, town2 = town1. That's how we know it's a sm trade!
		    	
		   		/*
		 * The reason we only search out the mate once
		 * and never again if we have it is so we can save
		 * processing time. In certain instances when a schedule existed
		 * before a new server load, when it loads up it can't necessarily
		 * find it's mate even with it's id because that mate may not be laoded yet,
		 * so what happens is the first time it looks for it's mate(ie first getMate() call)
		 * it'll find it and save it. When a trade is set up within a same server session,
		 * the mate is found the second it's made! cool, eh?
		 * 
	Also look at getMate() for more information on threadSafety. This is how twoways
	keep from interacting and creating duplicates of one another.
		 Tomcat Tests:

1. Simple web prog here---CHECK---
2. God here---CHECK---
3. Simple web prog up there---CHECK---
4. God up there.---CHECK---
5. Return towns correctly here. ---CHECK---
6. Return towns correctly there. ---CHECK---


	Things to do:
	
	0. Set up messaging system with two different message types.
	[So messaging system has subject, body, to and from pids, and a message type. Objects
	are not kept track of by this system except they are checked for if they are trading messages
	and then marked as processed by turning the type up to 2 from 1 from 0 if it's a normal message..]---CHECK---
	1. a. Set up Trade Object with all the fixins. Make sure it has a constructor
	for both loading and new trades somehow. This includes the DB object!!!---CHECK---
	1. b. Set up the TradeSchedule object with all the fixin's, same way.---CHECK---
	2. Set up Player's trade stuff, in it's updates and in God's loading patterns.---CHECK---
		-Trades---CHECK---
		-TradeSchedules---CHECK---
	3. Create the Trader thread that keeps track of some global variables stored in God as new
	columns. May have to change this when you move to multiple Gods, to be global. ---CHECK---
	4. Create the method to setup trade routes. Make sure it has proper validation
	for buildings and trade centers and what not. ---CHECK---
	5. Make the iterator method for it, and make sure it does scheduled tasks properly! Also
	when it has no destination, use the stock market ratios for send back.---CHECK---
	6. Update the invasion code to get rid of all trading routes, et cetera.---NO NEED.---
	
	Tests to Run:
	1. Set up a simple one-way one-time trade.---CHECK---
	2. Set up a simple one-way two-time trade.---CHECK---
	3. Set up a two-way one-time trade.---CHECK---
	4. Two-way three-time trade.---CHECK---
	5. Create a bunch of trades in one direction, and a few in the other, to create
	a predictable 10:1 ratio and then try to do a stock market trade.---CHECK---
	6. Try to do a stock market trade with other stuff clouding it.---CHECK---
	7. Try to make a trade with more than one resource.---CHECK---
	8. Test that these trades are maintained across server restarts.---CHECK---
	9. Make Create Player work!
	
	
	
	
	




 
 
 
 
 How do we make those little popup windows in bhviewer work?
 Well this is kind of complex. SO what we're going to do is we're going
 to only allow one pane to be added at a time, and we're going
 to just take a plain panel and put it in the layer above modal. Forgot
 what that is. Maybe the glass pane. Then we're going to equip it with
 a JTextField. 
 
 How does it activate? By not moving the mouse. If it reads your position as being
 over something, it'll set someHelpChosen=true and the message, and if a timer
 runs out it'll put that box above. When you move the mouse, the box is removed
 if it exists and the timer reset!
 
 
 
 
 
 Seeing attacks:
 This is definitely going to be a viewer phenomenon, though we could store it or something.
 Can't be coupled to Concealment as that's already way too valuable. Couple to
 stealth tech v bunker tech instead. Rename bunker tech "defensive tech."
 
 Formula would be like bunker/stealth. If bunker>stealth, then the attack
 shows up the second it's sent. If not, then it shows up when the time is
 equal to that fraction of the original time described by bunker/stealth.
 
 You're going to need to take into account the fact that Bunkers already
 present such a large advantage to players. Stealth does help determine
 concealment for both parties so stealth has defensive/offensive advantages,
 though it's twice as advantages to offensive players as defensive ones. Bunker
 has only defensive, so it's probably better to use it for seeing attacks.
 
 
 
 
 Finding Memory Leaks:
 What causes the largest leaks are background processes that run so periodically
 that no user could click a button fast enough to match their rate. User induced
 processes in the program do not contribute significantly to the memory leak. Therefore,
 leaving each menu or thing on in the display program is a good test to find leaks,
 because users clicking items will not contribute greatly to it over the common
 duration they run the program for!
 
 
 
 
 
 Stuff done for the Fighting System GUIs:
 
 To do: 
 1. Create the AUtemp table.---CHECK---
 2. Modify the attackunit table to include graphic.---CHECK---
 3. Modify the player table to have a boolean list of all the graphics that have been unlocked and AUTemplates pointer..---CHECK---
 4. Modify God to be able to read in such Data.---CHECK---
 5. Modify Player to be able to write out such Data.---CHECK---
 6. Modify the unit creation function to accept a code for which graphic we're talking about.---CHECK---
 7. Make a template unit creation function that doesn't put holds on the graphic number.---CHECK---
 8. Create the AU building table by having arms factory, then a list of attacked units
 that can be clicked. There should be a template list off to one side that will display
 your templates when one is selected, or an au when one is selected. When you select
 an empty slot you can replace with a template or replace one with no soldiers.
 The picture and textbox for building should be to the right, and there should be a build
 queue on the bottom.---CHECK---
 
 
 
 
 Rewiring the viewer:
 Here's how it's best to be: You basically changed your design around.
 Now, you're lower panel(mapPane) is the old battlehard viewer. When
 update is called on battlehardviewer, the paintComponent method is called on mapPane,
 which does everything, including loading players up and stuff. In a way
 this is just the same as the old AWT code, except now the pane
 does it. It also has a reference to the main layered pane it's a part of,
 so it should be capable of adding new objects to the layered pane on top
 or under it, allowing it to control the flow of execution without change.
 See the following sample code for how it works:
 package BHEngine;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.DefaultListModel;
import javax.swing.JApplet;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

class lowerPanel extends JPanel {
	int j = 0;
	public lowerPanel() {
		super(new BorderLayout());
		
	}
	protected void paintComponent(Graphics g) {
		Image backMap = createImage(200, 200);
		j++;
		Graphics buffg = backMap.getGraphics();
		buffg.setColor(Color.black);
		buffg.fillRect(0, 0, 200, 200);
		buffg.setColor(Color.white);
		buffg.drawString("Hello World" + j,100,100);
		g.drawImage(backMap, 0, 0, this);
		
	}
	
}
class upperPanel extends JPanel {
	public upperPanel() {
		super(new BorderLayout());
		
	}
	protected void paintComponent(Graphics g) {
		
			JTextField smallBox = new JTextField(6);
			smallBox.setBorder(new LineBorder(Color.gray));
			smallBox.setBackground(new Color(0, 11, 35));// 250,368 244,322
															// 236,260 = 11,35
			smallBox.setFont(new Font("Times New Roman", Font.PLAIN, 14));
			smallBox.setForeground(Color.white);
			smallBox.setBounds(100, 100, 60, 60);

			add(smallBox);
			//validate();
		


			DefaultListModel defmod = new DefaultListModel();

			JList statreports = new JList(defmod);
			statreports.setBorder(new LineBorder(Color.gray));
			statreports.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			statreports.setBackground(new Color(0, 11, 35));
			statreports.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			statreports.setForeground(Color.white);

			JScrollPane templateScrollPane = new JScrollPane(statreports);

			templateScrollPane.setBorder(new LineBorder(Color.gray));
			templateScrollPane.setBackground(new Color(0, 11, 35));
			templateScrollPane.setBounds(150, 100, 30, 30);
			templateScrollPane.setPreferredSize(new Dimension(30, 30));

			add(templateScrollPane);
		
	}
	
}

public class TextDemo extends JApplet {
	JLayeredPane main;
	BHViewWatch bhview;
	JTextField smallBox;
	JScrollPane templateScrollPane;
	DefaultListModel defmod;
	JPanel lower;
	JPanel upper;
	JList statreports;
	boolean textBoxesLoaded = false, templateBoxLoaded = false;

	public void createGUI() {
		lower = new lowerPanel();
		main = new JLayeredPane();
		upper = new upperPanel();
		lower.setBounds(0, 0, 200, 200);
		upper.setBounds(0,0,200,200);
		
		JTextField smallBox = new JTextField(6);
		smallBox.setBorder(new LineBorder(Color.gray));
		smallBox.setBackground(new Color(0, 11, 35));// 250,368 244,322
														// 236,260 = 11,35
		smallBox.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		smallBox.setForeground(Color.white);
		smallBox.setBounds(100, 100, 60, 60);

		//validate();
	


		DefaultListModel defmod = new DefaultListModel();

		JList statreports = new JList(defmod);
		statreports.setBorder(new LineBorder(Color.gray));
		statreports.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		statreports.setBackground(new Color(0, 11, 35));
		statreports.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		statreports.setForeground(Color.white);

		JScrollPane templateScrollPane = new JScrollPane(statreports);

		templateScrollPane.setBorder(new LineBorder(Color.gray));
		templateScrollPane.setBackground(new Color(0, 11, 35));
		templateScrollPane.setBounds(150, 100, 30, 30);
		templateScrollPane.setPreferredSize(new Dimension(30, 30));

		
		
		main.add(lower, JLayeredPane.DEFAULT_LAYER);
		main.add(templateScrollPane, JLayeredPane.PALETTE_LAYER);
		main.add(smallBox,JLayeredPane.PALETTE_LAYER);
		
		//lower.setOpaque(true);
		main.setOpaque(true);
		setContentPane(main);

	//	bhview = new BHViewWatch(this);

	}
	public void init() {
		   try {
	            SwingUtilities.invokeAndWait(new Runnable() {
	                public void run() {
	                    createGUI();
	                }
	            });
	        } catch (Exception e) { 
	            System.err.println("createGUI didn't successfully complete");
	        }
	}

}
 
 
 
 
 
 
 
Graphics Codes:

The way it works is the codes for each unit are read in like weapons
and stored in player-global boolean arrays for that index. Simple enough.
Each Attackunit is then loaded with it's proper graphic. The number of the graphic
corresponds to it's name - tank picture 1 is tank1.png.
 
 Why did you switch to string parsing for commands from the display viewer?
 Because it is less work for the processor than compiling a class every time
 and you can't import battlehard functions, which is useful.
 
 The defaults are the first 3 for all units except for bombers, which only gets the first 2.
 If you want to change this, change the new player code, not the db defaults, as the new player
 code instantly writes over that.
 
 
 
 How do you expand the JPI to multiple classes?
 
 By having userscripts under a separate user directory. Don't be a bitch.






 
 How did your memory problem work?
 
 When you do
 
 for(;;) {
 
 building b=new building(); }
 
 Each time it goes through, a new reference is created, and
 the variable that points to that reference is lost, but the reference
 is not collected by garbage collection even after
 you leave the scope. So you take building out
 and put it above the for. You did it for most everything
 but as the program gets used of course you missed stuff and memory
 will build up. You just have to restart every now and then
 and slowly find stuff.
 
 Think of it this way, once the variable b is no longer tethered to the
 old location specified by the older b, the garbage collector can't use it
 to find that older location in memory, and so can't gc it.
 
Also, you need to close all statements, resultsets, and connections when you're
done with them, and you can't just say ResultSet = new ResultSet and expect
the old one to die without being closed. It isn't collected until it's resources
it's using are freed, and that comes from the close() statement!




 How do unit build times work?
 
 Well, you'll notice the standard formula of 
 
 ticksPerUnit= (int) Math.round(300+1800*Math.exp(-Math.log(totalEngineers)/10));

Why do we do this? Well, we want an exponentially decreasing time as you build engineers,
so we make part of the ticks decreasable by having engineers. Now, engineers grow
proportional to the exp(average construction yard level), so if we put any number
over totalEngineers, like

Exp(-totalEngineers/somenumber) then very quickly it'll be outpaced, even if you
switch from somenumer=1800 to somenumber=50000 it only takes a few levels for
engineers to reach that. In order to set the characteristic average building level
where you should get .33 amplitude of the original 1800(e^-1 hits it to do this)
then you need to use the log of total engineers, which is the same thing as
taking the log(Exp(average construction yard level)) so that it's basically

Exp(-averageConstrLevel/10) This means when your average construction yard is
full and at lvl 10, you'll see a decay of 1800 by a factor of .33. So
by taking logs and using the bottom number, we can set the characteristic levels
of buildings where engineers make differences.

Why is this not done for building level up times? Let's take a look at the code 
from the getTicksForLevelingAtLevel(int lvlYouWant) method, as this gives
us samples of tick times for any level we ask about in a certain town:

	double nextLevelBase = (int) Math.round(300*Math.exp(lvlYouWant));
		double base = (int) Math.round(nextLevelBase/6.0);
		double expFactor = base*5;
		return (int) Math.round(base+expFactor*Math.exp(-totalEngineers/nextLevelBase));
		
As you can see, for whatever reason I thought of at the time,
nextLevelBase grows exponentially. totalEngineers does also.
		
Therefore, it's alright. Exp/Exp is a linear thing. That's why we don't change that!

We set basic trooper and citizen to be really low by level 10,
tanks by 15, juggers by 20, and bombers by 25, a nice and easy gradation!
(For once.)
		
		
 
 
 
 How do Queues work?
 
 Queues are for combat units only. The civilian units will be canceled separately.
 The AU-related fields on the building table shall be removed.
 
 Queue system is a new system. Buildings will now have IDs, and these
 will be foreign keys to Queues in the Queue table. A queue will have these fields:
AUtoBuild - AUslot of AU to build next.
AUCurrentTicks - Current ticks on the next au.
AUNumber - Number to Build
QueueID - Unique Queue ID
BID - Building ID

Each building will have an ArrayList of Queue objects that have these attributes.
When the system first loads up, it loads the most recent queue and begins processing it.
When a queue finishes(when AUNumber is 0 or cancelQueueItem is called) it is deleted
from the db and from the queue list. A the previous structure of having an array
of au units to build will be removed as it is now superfluous. This will be
the new medium.

List of things to do:

1. Alter the bldg table.---CHECK---
2. Create the queue table.---CHECK---
3. Create the queue object.---CHECK---
4. Alter Building to be able to add and remove queue objects as well as have different
data structures as per the queue way.---CHECK---
5. Alter the building server check method to work with this new system.---CHECK---
6. Alter God's loading and player's reading to work with the new system, player
does queue removal.. Remember, you only need
to ever update the topmost queue object! Use order by and shit too when getting
them out of God.---CHECK---
7. Change town's add/load bldg stuff.---CHECK---
8. BattlehardViewer fix.---CHECK---
9. BattlehardFunctions fix.---CHECK---

Tests that were done:
1. Try to queue up a single attack unit.---CHECK---
2. Queue up multiple attack units. See if it works as expected.---CHECK---
3. Queue up multiple attack units across multiple arms factories in the same town.---CHECK---
4. Same test but across different towns.---CHECK---
5. Same test but across different players. Can do all these tests by changing tid of buildings.---CHECK---
6. Try cancelling queue items.---CHECK---
7. Let one get pulled off naturally by ending.---CHECK---
8. Test across server restarts.---CHECK---







 
 How does the HQ Building work?
 
 Well, first off, you can only have one per city. It's level is the same
 amount of slots you can have out. A slot is taken if there are any
 outgoing/returning raids, and for any support units stationed elsewhere.
 The amount of unique townIDs for which you have support units stationed
 is the number of slots taken. 
 
 Of course, if an HQ building is bombed, then the player has more slots than allowed,
 but we'll let him. Eventually they'll come back and he'll no longer be able to use them!
 
 It should be noted that support unit recalls do not count towards your overall slot
 score. Why? Because a retreat is easily called, it doesn't require so much.
 
 So things that you need to do:
 1. Set up the if statement to make sure only one building can be made of the HQ variety.---CHECK---
 2. The slotsFree boolean method of the Town object needs to be made. It looks at
 the number of raids and the number of unique towns holding support units as well
 as the level of the HQ building(if present) to
 report back this boolean.---CHECK---
 3. Within the attack function which sends out all raids, and the resupply function,
 which helps out genocides/glassing, there needs to be an update to check slots
 and make sure they aren't greater than the level of the building.---CHECK---
 
 Tests done:
1. Try to send an attack with no HQ building.---CHECK---
2. Try to send more than 1 attack with lvl 1, more than 3 attacks with lvl 3.---CHECK---
3. Switch to player 3 and with a lvl 1 HQ try to send an attack - should be unable
due to support aus.---CHECK---





 
 
 
 How does the Bunker system work?
 
 The Bunker as a combat system is a series of algorithmic modifications
 on the BCE(Battlehard Combat Engine) that alter the flow
 of battle in favor of defense proportional to the amount and
 level of Bunkers present.
 
 This is only one part of the Bunker system, however.
 Bunkers have three different settings:
 1. Defense Mode
 2. VIP Mode
 3. Cache Mode
 
 Defense helps with combat, VIP protects civilians from
 genocide(and bombing) and cache mode protects resources. The amount of raw resources
 protected by a bunker needs to be the same across all modes. Also, bunkers
 need to not be easy to protect all and need to not protect all so people can take
 resources.
 
 Defense:
Bunker tech increases the protection a defensive bunker offers.
So, starting at 5% defensive increase, it can be ramped up to
like 50% over time with researches. This happens no matter
the bunker level. Increasing bunker level increases how many
soldiers are protected by this defensive increase. So providing protection is not
really the same thing as saving them entirely, it provides defensive boosts,
which go by bunker tech, but how many get defensive boosts is basically how many
you could buy with the warehouse. Similarly, with VIP, how many you get to protect
completely go by how many you could buy. So defensive mode is not as good as VIP mode, unless
VIP mode only really protects 5*bunkerTech% of what you could buy with the warehouse. That
means the other ones get slaughtered though, whereas with an army the protection is spread
and those units do not necessarily die. So we make the cap on bunkers the same, they go by
how much you could buy by full warehouse. This means effectively you're providing
protection to more soldiers than you could get with saved resources from the cache,
and preserving more civvies.

How many should be protected? I mean, you could protect
the aggregate cost of the building that you've put in so far,
but generally a bunker is a small, small % of your total economic output spent.
 Maybe what it should do is take your warehouse res for that level and calculate
 how many soldiers you could buy at max cap, and then protect that many. Of course you'll
 have to do n/how many slots are filled to get the right cost.
 

So how specifically does it work then? Each bunker is tagged with a peopleProtected
amount which is actually just whatever I choose rounded so we don't need to save anything,
however bunkers have a mode, which is 0,1, or 2, and 0 is defense mode.
So in combat, then, bunker frac would be whatever the bunkerTech said it was(1-.05*bunkerTech)
and then that fraction should be diminished proportional to the fraction of the total army
size the total bunker protection offers. So like, if the bunker supports 250 people,
then the army size being 500 means that only 1/2 of the army should be protected. 

How do we do this? If it were the whole army, bunker frac should be .95 if bunkerTech is 1,
but since it's half the army, each set of units should only get half of the protection,
so .975 instead. Instead of protecting half of the units and not protecting the other
half, the algorithm spreads the .05% that would goto 250 over 500 so it becomes .025%.

So the formula for bunker frac is


bunkerfrac=(1-.05*bunkerTech*armysizefrac)

where armyfrac is

Sigma(exp(lvl), each bunker)/totalArmyExpmodsize or 1 if armyfrac>1.

Add (lvl-25)*Exp(25) if lvl is above 25 for soft limit on bunkers.

This minimizes damage done. Armysizefrac can be calculated at the beginning
of the raid.

The VIP mode works like so:

In the event of a genocide, the total number protected over the town pop or 1 if this is greater
than 1 is the fraction of
each building that IS NOT let out into genocide.

Similarly, when pplbombing occurs with the bombLogic, the people in a building
can be killed but ONLY down to the fraction that the bunker protects, or 1,
if it's greater.

VIP mode takes the number that you can fit in the cap in the equations
and does n/3, because the cost of the amount of civvies you can hold
sort of gets divided by three since there are three types, and bunker
mode provides protection across all units already so they distribute
properly.

Resource mode works like:

So basically you should only be able to save a percentage of warehouses at the same level.
Let's say it starts at 5% of the warehouse res at the same level. Then you can upgrade
your bunker tech and get 25%, 35%, and so on. So you can turtle up. It is possible to do. 

And then while soldiers get 5% of full warehouse cap res protected max, you get 5%
of full warehouse cap res protected in cache mode, the only uneven is that civvies
get wholly protected all the way up to the cap. How do we fix that? Only protect 5% of them?

That means each level up you get, you get 5*bunkertech% protected by each of the modes
of the warehouse cap at that level. So we max out at 50% - you need two bunkers to get it all for res,
whereas with troops it just expands your max amount protectable, but with civvies literally
does double protection.

To clarify:
1. Leveling up bunker means at the new level's warehouse cap, however many soldiers you could buy,
you must give 5*bunkerTech% to them.
2. Leveling up your bunker means at the level's warehouse cap, however many civilians you could buy,
you must protect 5*bunkerTech% of them.
3. Leveling up your bunker means at the level's warehouse cap, you must protect 5*bunkerTech% of those res.

To implement this new formula:
1. Change combat for civvies and armies, probably going to have to make a battlehard function
that returns hwo many units you can build given 400 and 100 base unit prices.---CHECK---
2. Change the effect strings.---CHECK---
3. Change the res saved in the moveResources formula accordingly.
4. Change the res saved in the moveResources formula according to cargo change below.
make sure THE ZERO CASE WHEN YOU HIT ZERO PLAYERS ALLOWS FOR ALL!
5. CHANGE ARMS FACTORY IMPROVEMENTS TOO!---CHECK---

The formula for units saved is gonna be resourceAmt*exp(lvl+1)/((70/4)n/(unitsallowable))*(n/(unitsallowable)+1)/2
(the 70 comes from summing the total res and then you divide by four, units allowable is how ever many attack
unit slots you have filled or three for civvies!)

Corresponding Cargo change: 
To account for the low protection rating and high cargo capacity of units and with
brushes being generally nonlethal to incoming soldiers, we either make it so you only
take cargo if you win the fight(% wise) or your cargo is drastically reduced.

so if you lose the fight, the percentage you lost by is the cargo you can take,
whereas if you win the fight, you can take your normal cargo. If you make it weighted,
so you get more cargo percentage if you win, then players will try to crush others
before taking cargo - if you make it normal, then they will just try to outnumber the other
guy slightly, to be forced not to get the drastically reduced cargo. 

I have an idea. You first allow them to take as much as possible, but then you multiply that
by (your%lost-their%lost)*afTech*.05, so afTech gets evened out here! The max of course would be
grabbing 100% of your loot, the minimum, 0. And this cannot be won by sending high cargo,
this must be won by straight winning the battle.


Arms factories not equal?

True you get their benefit across all your raids, but generally at home when you get hit
your whole army is contributing to the fight, whereas with 10 raids out only 1/10 of your army
is in each one, so while they'll likely get more protection overall sent out in a little bits
than having your army at home and seeing that the bunker can't hold them, bunkers counter
this by being able to protect other types of stuff. The fact that you can protect roughly as many
res as you can take, due to this fix and the fact that people generally only win by 1% or so in battles,(so
5-10% for an AF of 1), means res mode and AF cargo cancel out, AF protection and bunker mode do not cancel out
but bunkers also protect civvies so there is that, something AF does not do in any capacity.


To-do list to make it happen then is:

1. Add a new field to bldgs called bunker setting.---CHECK---
2. Add a new field to player called bunkerTech.---CHECK---
3. Alter the combat system to detect how many bunkers, how much
they contribute, etc, and make a bunkerfrac.---CHECK---
4. Alter the combat system's genocide protocols to not take out
civilians below the bunker limits decided in the document above.---CHECK---
5. Alter the pplbombing part of bombLogic so that it can't kill
more than the bunker limit.---CHECK---
6. Alter resource taking so that resources cannot be taken below
the limit(1/4th the formula for resources above, so 25 instead of 100
for each.)---CHECK---
7. Implement the changeBunkerMode() method in battlehardfunctions.---CHECK---
8. Test and debug this shit.

Tests done:
1. Set it up so bunkerfrac gets printed for every following instance.
Create one bunker and set it on combat mode lvl x and make there be more
soldiers than that, let's see if bunker frac protects correctly - ie, for 2x
soldiers, there should be a .975 frac since 2.5% is taken off instead of 5%,
and for 1x, should just be 5%. For 4x, should be 5/4%. ---CHECK---
2. Turn on VIP mode and try a genocide. Make sure x, the number of people,
is half the total, so half get taken out of each. Then try the whole numbered
one. And so forth. ---CHECK---
3. Send a strafing with a pplbomber. See if when x is half of the total, only
half get bombed, then when it is the total, etc.---CHECK---
4. Set it to resource mode and try to move resources beyond the resource limit
which should print and see what happens.---CHECK---
5. Try having one vip bunker and one combat bunker and seeing how the two are
affected in a genocide.---CHECK---
6. Try seeing what happens with a glassing.---CHECK---
7. Try choosing an improper mode or with an improper building just to test
the change function out.---CHECK---
8. Make sure that bunkerModes and bunkerTechs are maintained across server restarts.---CHECK---





 How does bomb target order get changed?
 
 Very simple, do changeTarget(int raidID, int newTarget).
 
 Tests:
1. Send out a strafe and try and change the target.---CHECK---
2. Try to change the strafe target post raid.---CHECK---
3. Try to change an attack bomb target.---CHECK---




 
Genocide-Glassing Civilian Support Policies:

Civilians are not supporting units. They will be saved in additional
unused columns for in the bldg entry when they
are out for genocide. They will then be read in again if the size is not -1. This
preserves them across restarts. We put it in bldg because this auto-keeps track
of different engineering attack unit types due to multiple copies of the same
building!---CHECK---

They do not gain veterancy via a new if statement on their lotNums!=-1.---CHECK---

Finally, they will remain outside until all of the genocides that are attacking
that city that have allClear booleans have either been recalled or are over. This will
be done by a special method called checkForGenocides(Town argument) in the player object that
searches for civilian units and, if found, searches for genocides currently on-going.
Normally, genocides put back civvies if they lose, but in the case of a recall,
then this never occurs, so this method will then do that for us. If there
are other incoming genocides that haven't hit yet(and thus have no allClear)
then those civilians still go back inside...that'd allow someone to keep
sending and recalling a genocide to keep citizens outside!---CHECK---

If a genocide is beaten, then it should not remove civilian au unless checkForGenocides
is false.---CHECK---

If a genocide hits and there is already an allClear genocide hitting that city,
then it's allClear variable is automatically turned on. This will be started
before the raid even begins by simply looking to see if it's a genocide,
finding if there are any others with allClear on, and then going with that.---CHECK---

Is it possible to add supportAU, then have them still be there after civilians
are added? No. Civilians should always be for the most part right after
the original attacking au but we'll think on this and prepare.

Is it possible that two attackServer run throughs from separate players that land at the
same tick will add civilian units at the same time, duplicating them? No,
because one will set it to zero before the other.

Tests done:
1. Send an attack out with support au and recall the support au and see
if the raid retains it's data structures and raidSupportAU after
the recall via a print out function and a select * call. This is important
in the assumptions I make. It won't hurt this method but it's a small test
and could eliminate a future issue. I made an adjustment in the support code for this.---CHECK---
2. Test genocide across a server restart and see that civilians are MAINTAINED.---CHECK---
3. Test that when one genocide enters a town when another is there already and allClear is on,
it also puts on allClear and doesn't add anymore civilians.---CHECK---
4. Recall a Genocide raid before it ends and see if the civilians disappear.---CHECK---
5. Change veterancy listings to show which units are getting veterancy so you can see
if civilians get it.---CHECK---
6. Test that scouting runs during genocides do not give info on civilian units.---CHECK---
7. Two genocides, one incoming, while the other does its work and fails
Civilians are returned to their posts.---CHECK---





 How does glassing/genocide resupply work?
 
 Via the resupply(raidID,auAmts[],yourTownName) function. You can only
 resupply from the originating town as supportAU MAY be different.
 
 What will it do? It'll either have resupplyID on raid =-1 for normal attacks
 or it'll be something real for resupplies, which is how we'll tell.
 
 Beauty is don't have to worry about adding new aus during the other's attack,
 they're both on the same attackServer so they're never being processed
 at the same time!
 
 Tests:

1. Send out a genocide raid on a city with citizens and soldiers so that
it hits the first time, followed by a resupply that'll hit right after
and resupply, without support units.---CHECK---
2. Do it with support units.---CHECK---
3. Try to send non-offsupport units.---CHECK---
4. Recall that resupply...---CHECK---
 
 
 
 
 How does raid-recall work?
 
 You type raidRecall(RID), which then searches through your attack servers
 until it finds it and returns it. Couple this a new user object type
 called userRaid, which is in the battlehard functions package and
 contains an integer array of au sizes and an au string array with their names,
 as well as a destination string, x, y, a starting point string and x,y, 
 and a raid ID.
 
 When the user types userRaid holdRaids[] = returnRaids(townName), then it
 returns an array of these objects. Then one can be used to do raidRecall.
 
 Tests for recall:
1. See if you can get data on all of your raids, using (preferably) player 2 after
fucking stalling that invasion raid. Specifically, have it print out a toString
or something.---CHECK---
2. See if you can then do it with player 1 and recall that horrendous 159 raid coming
in.---CHECK---
 
 
 
 
 Six different types of AU all build at the same time...so what does one get for
 specializing in one unit and paying that extra build time(factor of six for same
 number of units)?
 
 Okay, so if you build one unit, you incur price restrictions, in exchange
 you get veterancy.
 
 If you build six units, you build a lot faster than building one unit(by a factor
 of six) but you DO NOT get veterancy AND you do not incur price restrictions AND
 with cargo you get the same improvement as having 6 units with 6 spread.
 
 What we do to fix this is set up a "chooser" in each Arms Factory that lets
 the user choose which units get developed...or change that into a new
 table of queues referencing tids and building slot numbers, followed by
 queue ID which will increase over time. You'd load them onto a stack
 on each building and this would be a lot more data. Better to leave this one.
 
 Besides, you want people to DIVERSIFY their armaments. The best way to do this
 is to promote it. Therefore, people will on average have MANY units, with only
 a few going for veterancy. Veterancy also just changes fight turnouts in a sort
 of luck related fashion, and does reward those who build a lot of a certain type
 of unit.
 
 
 
 
 
 How does createPlayer method work?
 See the documentation there.
 From your journal:
Made a createPlayer method in God...you'd need to set up a separate channel to listen
for requests, but that can be done later, the prototype is there and ready
for testing when you are, when you implement it in the battlehardviewer,
it's just getting the paperwork ready.


 
 
 
 
 Code for creating the Id player:
 
 NOTE THIS CODE NEEDS TO BE UPDATED TO INCLUDE MINE BUILDING CODE!!
 
 insert into player (username,stealth,knowledge,totalscho,totalmess,totalpop,alotTech,soldTech,tankTech,juggerTech,weaponTech,buildingSlotTech,instructions,outputchannel,poutputchannel,civWeap,bomberTech,suppTech,townTech) values ("Id",1,0,0,0,1,0,0,0,0,"1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,",1,"null","null","null",0,1,1,999)
  and then
  insert into revelations (pid,revAI) values (5,'nada');

insert into attackunit (name,pid,slot,conc,armor,cargo,speed,type) values ('locked',5,1,0,0,0,0,5);
 do for each slot
 
 and then use this code to generate the world towns:
  		 Statement stmt;
			try {

		      Class.forName("com.mysql.jdbc.Driver");
		    
		      Connection con =
		                     DriverManager.getConnection(
		                                 GodGenerator.url,GodGenerator.user, GodGenerator.pass);
		      stmt = con.createStatement();
		      
		      // First things first. We update the player table.
		      
		      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.

			
		      int x=4;
		      while(x<GodGenerator.maxXSize) {
		    	  int y = 4;
		    	  while(y<GodGenerator.maxYSize) {
		    		  
		    		  
		       		 double  rand = Math.random();
		       		 int xmod=0;
		       		 int ymod = 0;
		    		  if(rand<.33)
		    		  xmod=1;
		    		  else if(rand>.33&&rand<.66)
		    			  xmod=0;
		    		  else xmod=-1; // so each time we get a chance
		    		  // of a diff x, but x overall never repeats and is
		    		  // never out of sync.
		    		   rand = Math.random();

		    		  if(rand<.33)
			    		  ymod=1;
			    		  else if(rand>.33&&rand<.66)
			    			  ymod=0;
			    		  else ymod=-1;
		    		  stmt.execute("insert into town (pid,townName,x,y,m,t,mm,f,pop,minc,tinc,mminc,finc,kinc,au1,au2,au3,au4,au5,au6) values (5,\"Town" + x + "-" + y + "\","
		    				  +(x+xmod)+","+(y+ymod)+",0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0)");

		    		  y+=4;

		    	  }
		    	  y=4;
	 
	    		  x+=4;
		    	  
		      }
			stmt.execute("commit;");
			con.close();
			} catch(ClassNotFoundException exc) {  exc.printStackTrace(); } catch(SQLException exc) { exc.printStackTrace(); }

		

 
 
 
 
 Possible New God setup for Alpha to Beta:
 
 Alright so I'm thinking...how to fit more players on.
 
 We create an event LIST in the game...all events have
 pids attached, attacker, defenders, support pids, everything,
 and there are different types of events:
 
 Raid Events
 Building Events
 Trading Events
 
 etc.
 
 This table records the timestamp of creation and the number of ticks until a synchronize
 takes place due to this event and this event is removed when it's used
 to create a synchronize...
 
 When a user signs on, he selects all events concerning him that should
 have happened already
 and sends "synchronize" signals to all of the other players
 that may be out there...and a player that is logged on will suddenly resynchronize,
 also any player that logs on auto-synchronizes with logon. This puts
 the processor on the user's computer to work.
 
 The events will have timestamps and seconds left, so that it can be seen that
 an event has passed and synchronization needs to occur. The iterate() function
 will need to be used repeatedly so that if a raid has landed and then another
 5 seconds later, iterate is called 5 times before the second raid is processed.
 
 PlayerScript will become a new subAI, which will have each of the old players
 on it, that's the problem, if they exist, the players need to exist on the
 server and be easily writable, or else we rewire battlehard functions
 to just place events on the server table and use query statements to assemble
 player objects on the go...probably best to assemble a player object, and then
 every time a function is called, the table is checked against time to see if sync
 is needed, then the player object syncs, then the function goes through and
 does stuff.
 
 In this way, almost all of the processing occurs on the user end, my server
 just contains a database. The only processing it needs to do is the programs
 the users write.
 
 How could we make this work? Well, firstly, we get rid of God. We make
 it so that when the user signs on, he loads his player, and all events with
 calculated "negative ticks" (we keep the old data tables but all "ticks" stuff
 is stored on a single event table) we process, starting with the earliest event
 and iterating in between negative tick intervals, doing all the processing
 on the facsimile players - also loading up any other players required to run the
 data. By loading them up, however, we also have to load them up, and in loading them
 up, we may have to load another up...and that guy may need you to load up, but you
 can't without calling the other guy...okay maybe come back to look at this in some
 time.
 
 
 Well there are a limited number of events. I guess what you need to do is
 select all events associated with your pid, and then for every pid associated,
 select all pid associated with them, and so on, until you get a list of pids
 that need loading. Then, you load them all up on the user's computer at once,
 turn them on, and then do processing based on negative ticks backup. Then after
 all events are processed IN ORDER in each player, every one being iterated
 in between events, then the resultant data 
 
 
 
 
 
 
 
 How does Invasion work?
 
 Right, well, basically we shouldn't do it on comparison
 to population as that will be easy to do. What we should do is
 add townTech and if you have room you can take another town.
 
 The restriction should be that the HQ and all Bunkers must be destroyed
 before a town is taken over. Then Invasion can work if the army
 is some percentage of the population. Also, HQ as a building
 needs to be coded in and can only be built once per city.
 
 checklist to make invasion work:
 
 1. Create the HQ building type and make sure it cannot be built if there is one
 already in town. This is the only building like this. Also make HQ
 scoutable by updating that code.---CHECK---
 2. Create the invasion boolean on raid table and in raid object that
 is preserved. Make it so the attack() command can send and understand it.---CHECK---
 3. Make the invasionLogicBlock that checks to see if bunkers/HQs exist
 and if they don't then check to see that the army is at least twice
 the population of the town(expMod should be used instead of popSize,
 as then the larger attack units are better for invasion.) If
 the prospects check out, then the town needs to be removed from one player
 and put on another with the giveTown method.---CHECK---
 4. Create the giveTown method---CHECK---
 5. Implement the invasionLogicBlock into attackServer by placing it in the
 combat logic block, as one of the if-handlers at the end. if(invade) is
 fairly easy enough and no other combat types will have that!!!.---CHECK---
 6. Status Reports need to be created and read.---CHECK---
 7. Make the giveTown method to give towns away, and make
 a player called "Id" that holds all the other towns that aren't
 held by players. Unocc. ---CHECK---
 8. Make it so the viewer switches to home town with a town is lost!
 This hometown will exist no matter what because synchronize will
 only be called once the new town is given if it's the last one...
 
 Invasion Testing:
In all testing, place support units in attack and defending positions...
And make sure there is always something building in multiple buildings,
Make sure a few buildings on building server
Few harmless raids on raid server. ---CHECK---

1. Try to take player2's town2 using player1's forces, with military at home.---CHECK---
2. Try to take town2 with forces that do not meet the invasion reqs. Shouldn't work...---CHECK---
3. Add a bunker to town2, but strip of units, try to invade.---CHECK---
4. Bunker added, but with units too, try to invade.---CHECK---
5. Try to take player3's town, only town, see if it gives the new city correctly!---CHECK---
6. Change all ID's towns to player2, and try to take player3's town, see if it sends
the raid back!---CHECK---
 
 
 
 
 
 How does scouting work?
 
Let's make it simple. We know that stealthTech will only be able to go
up to a certain amount.

What determines what you see?

There are different levels of scouting, later levels include all data in previous levels:
1. You see names and troop numbers in a town.
2. You see resources.
3. You see civilian numbers.
4. You see building amounts and levels.
5. You see attack unit type data of units that have size>0.

You can never see research chosen by the player, or attack units not
present at that city! Can you see support units? Sure can!

How do we know what type of report is shipped? Easy!

If your stealth technology is lower, then you can only receive level 1 reports.
If your stealth technology is higher, then you receive the difference in stealth
technologies as the level of report that you receive. 


IF SUPPORT UNITS ARE SCOUTING, SHOULDN'T THEIR STEALTH TECH BE USED? No.
Then nobody would build tech except for one guy who would loan them units.

Right so now it's time for the new one. Scout tech and concealment v stealth and accuracy.
Accuracy will need to be modified by the units like concealment is because we're using soldiers.

So basically we need to go by the Sa/Sd ratio for what type of report you get. And you need
to be something like

((1+.05*scout)*avgConc)/((1+.05*stealth)*averageAcc)*cos^2(1-(yours)/(cover))/sin^2(theirs/cover). 

So if this is under 1, then we take a random number and if it is say...above
the number and under 1(which it will be) they get caught.

If more than one, we set up the tiered thing we know of.

You can see how the tech would be quite useful. But then we also need
to somehow include that sending in large numbers MAY get you more info but
you have a greater chance of getting caught...

We could use cover in this? Yeah. Let's use cover, it's kind of cool.

Except make it signficantly lower...multiply this by an exponential with their
army size somehow decreasing the chance or something. How about...

sin^2(x)?

so what is this x? It's got to be somehow related to their Cover and troop size
and your troop size.

1-(yours)/(cover)? So if they have far over the limit, harder to hit the sweetspot.

Then multiplied by

1-(theirs/cover). So if they're way below, it really hurts you, and similarly, if they're
way above, it really hurts you.


So the special amount is there. You can sacrifice more to get more info.







Scout Logic Block:

3. Calculate effective concealment as per the combat system(copy/paste.)
5. Calculate the final limit and a random number.
6. If the random number is => limit, send a status report.
7. Status report is level 1 if theirs is greater, yours-theirs if lesser.
8. Status reports are sent by
	1. using offNames for their unit names,
	2. auoffst for their troop numbers, civilians can be included here for level 2.
	3. auofffi can hold their stats and weapons held.
	4. audefnames can hold building names  
	5. audefst can hold their numbers.
	6. m,t,mm, and f fields in a stat report can hold resources.
	
Of course, if the difference is greater than 5 then they see everything!

List of things to do:
1. Modify raid table and data structure to include the scout boolean(loadin,loadout,etc)---CHECK---
2. Modify combat part and statreps to include integer scout variable in statreps
so that if combat comes, then when the raid is reconfigured and goes through as a
combat run, it will show up in stat reports as a scouting run gone awry.---CHECK---
3. Modify stat reports to display bad scouting runs.---CHECK---
4. Modify attack() to be able to send out scouting parties.---CHECK---
5. Create scout logic block and else if statement on attack server check.---CHECK---
6. Create ability to read scout reports.---CHECK---






 Why do we scale the cargo?
 
 We know that the standard soldier carries 50 cargo,
 The standard soldier has a base cost of 80 resources. This means that at a certain army size,
 cargo taken will be nothing compared to unit costs...which will make pillaging kind of
 useless as far as everyone else goes.
 
 So there is that formula number*(number+1)/2.
 ...so that total cargo scales up to the size of the attack! This means
 larger attacks are better if you intend to take a lot because soldiers can work together
 to carry more back.
 
 This number could be size*popSize, because
 Their popSize is 5, 10, and 20. With popSize used instead,
Juggernaughts get 4x cargo generally and a 10x increase to their size number(size*popSize)
leading to a 40x cargo boost relative to soldiers, and they are worth 40 soldiers.
Tanks do 2x the cargo and 5x their size leading to a 10x cargo boost. But this isn't
entirely true, each one of those 10 soldiers adds an extra multiplicative, whereas
the juggernaught is just straight up one thing. However, Juggernaught prices' include
the little addups made.

 If each unit contributes separately
 but then we get into grouping the same unit giving more advantages.
 
 So what we do is sum up all their cargos and all their sizes and then do the formula
 above on the entire cargo loadout.

However, if you have 1 of each of three combat aus, it'll add like they were
all bought as the same au and so had a triple price increase when this didn't happen.
This makes it more lucrative to split up unit types and not use only one. Okay.
I can work with that.
 
 So, to overview:
 
 Prices rise like base 80 * 1, then 80 * 2, etc.
 
 Cargo rises like 50 * 1, then 50*2, etc, roughly. So you can quickly
 reimburse units by raiding.


 
 
 
 
 
 How does concealment work in offensive, vs defensive, situations?
 
it takes twice as much concealment to launch a good offensive attack than a defensive one.
Can be fixed later but would be a good rule of thumb: Twice the concealment for offense.
Not what I'd prefer, but it basically means that the army you can attack with at a certain concealment rating on a town
of similar population is half the army you can defend with and get the same rating. It also means
attacking armies that are large relative to the village size will suffer for it, leading to small armies against
small villages, large armies against large villages, and a need to reconnoiter everything. Cool!
							
							
							
							
							
							

 How do supporting armies work?
 
 6. Implement supporting armies, and make sure they can attack if they are flagged to do so.(Also you must be able to support, not just receive.)

What's the best way to do this?

We need to maintain always the town origin of the armies. So do we send them on the raid server?
sure, why not. We keep track of their slots, sure, but we shouldn't use this to define anything.
We remove them from the raid and add them to the aus of the town they were sent to. Then we
can send raids with them. The attackserver itself doesn't care how many there are...but the database
code does. We need to come up with a way to add this stuff. Maybe a single extra string for extra
aus in every place.

Players can send their own aus to support anywhere. They cannot, however, send other player's aus
to support anywhere. They can send them to attack, though, and return to their original set
position.

CHECKLIST TO MAKE IT HAPPEN:

 
Tests done:
1. Set supportTech = 1, try to transfer 100 soldiers and see what gets sent back. Make sure population of town it's going to
is 100.---CHECK---
2. Same test, transfer 7 soldiers.---CHECK---
3. Set supportTech = 3, try to transfer 100 soldiers, then 30 soldiers.---CHECK---
4. Keeping supportTech = 3, create a three new players, give them each 100 soldiers, and with one town,
and try to send them to player2's one town. See if you get 30% on each.---CHECK---
5. Keeping supportTech = 3, recall one of the soldier units from player2.---CHECK---
6. Try to send two support runs - 15 soldiers and 15 soldiers.---CHECK---
7. Load the player2 up with 2 players' worth of support and then
do an attack on him, view results. Are they within expectations?---CHECK---
8. Knock out a support unit(if you didn't already) and view what happens to the support AU in memory and
elsewhere.---CHECK---
9. Send player2's support units to attack player1, with their own units, and then without. Does it work?---CHECK---
10. Recall all units after all of this and see if we can recreate the peace.---CHECK---
11. Test that support is retained across server restarts.---CHECK---
12. Test that support raids are retained across server restarts.---CHECK---
13. Kill only part of the units and see if it adjusts supportAU table.---CHECK---

(This checklist can also then be modified to explain how supporting armies work.)
View this checklist as an instruction key on HOW to make supporting raids happen,
this is what had to be done, you can find these steps located in the program, because this
is such a diffuse topic, it is not linear and therefore defies a straightforward explanation.
Instead rely on this list of changes to understand where and why.


Foreign AUs are kept in a separate table called "support" with foreign key tid and has columns:---CHECK---
Foreign tid(foreign key)[Can also get player from here.]
tid(foreign key)
real slot num of attackunit in the foreign player's arsenal
 slot number of this player's arsenal
 size of unit

All attackunits will now have a "support" integer, that when 1, means that
it is a support unit, 2 is an offensive support unit, 0 means it is not a
support unit. It then has an holdOriginalSlot number that is used
for the foreign player's original slot. It does not need to remember foreign tids,
the db server can do that..---CHECK---

When they go through a fight, when removeAU is called, it will detect the support boolean
and remove from the other player.---CHECK---

Removing foreign aus is a difficult business, because one must check the raid server
AND the town they came from for zero before removal.---CHECK---

Player will now have "support tech" associated with it that determines how many
players can have supporting armies in a location and support density tech
which determines what percentage of your army size they can have there. Actually
we can combine these to make it easier - each level adds a slot and a percentage of
like 10% of population size. Or maybe allow only 50% max of your army size, and then
they can upgrade slots. But if it's 1 slot 10%, then 2 slots 20% each, 3 slots 30% each,
it's a researchable skill. And it doesn't change the game much.---CHECK---

Raid objects need a support integer attached, meaning they are a support raid. As it
is not commonly used, it doesn't need to be included in the load up constructor.
It will need to be included in the new Raid constructor, though.  Instead,
you can use the makeSupportRun() or makeOffSupportRun() method when loading up from a
dead DB. This includes changes to the DB to hold this integer.---CHECK---

attack() will have to be modified to allow for variable length arrays for sending
these units. Right so to send a raid that is support, there needs to be support codes:
"support" means defense
"offsupport" means defensive and offensive capabilities.---CHECK---

attackServerCheck will do the detecting on incoming support units, any units that
go beyond the limits will be sent back as a separate return raid object. See
procedural overview comments on this in the attackServerCheck code for this piece. This will
also generate a support status report that both parties can view.---CHECK---

Stat reports will now carry the boolean support, which is by default false.
If it is present, then offNames will be names of unit,
auoffst will be units sent, auoffi will be units sent back.
defst and deffi will be left blank and stuff. Stat reports will display the
report similarly.---CHECK---

When the user attacks with
support units, each supporter will receive a copy of the battle report.
-Do this by..well it searches tid1, tid2...so basically, you use supportAU table,
and say, do I have any units that share a tid with me on that table? If so,
what are their ftids? Get them all and then query all stat reports
with that ftid. ---CHECK---

Veterancy will have to be modified by searching out the OTHER player's stuff.
-What, specifically, needs modification? The totalPopulation count.
We can use originalPlayer in an if statement for this. ---CHECK---

recall() which will recall all units from a specific city. This method will eventually work on raids
also. ---CHECK---

Make it so users cannot build or modify units that are in slots > slot 6(or 5 on 0 based indexing.)---CHECK---

God needs to be able to read all of this in.---CHECK---

Player needs to be able to write all of this out, specifically, updating supportAU
table as called..---CHECK---

raidSupportAU needs to be created to store values of sAU on
raids. How this works: When attackunits are added beyond the sixth,
they are added to raidSupportAU table by add method in Raid, and
updated by player for all >6 aus. God reads in support AUs for each
raid from raidSupportAU table after
it makes the raid and puts in au for it, and then adds copies of them
from the town which has them. This does not effect defensive support AUs!
That's the supportAU table, which requires ftids and other stuff
that we can save space on if we do not use these in raidSupportAU.---CHECK---

Make stat reports do a separate defending line and offending line every six units. That'll make it easy to
see.---CHECK---







 Do 4 tanks = 1 Jugger as expected?
 
 Yeah. They do. Because 10 soldiers = 1 tank and 40 soldiers = 1 Juggernaught. Perfect math.
 
 
 
 
 
 
 
 
 How does the Veterancy system work?
 
  Probably best to randomly choose to get protection or a damage boost.
 
 So what can we do to spur on veterancy? What defines it? Should it be time used, amount made, or what?
 I'm not about using amount made, because this number rises exponentially. I can't use the percentage of the
 combat unit population it has, because people get 100% veterancy with only one unit. I can't use time,
 because then the server has got to measure dates and stuff, which is NOT what I want.
 
Veterancy is defined by the formula:

randomdecimal*expModOfAllSuchUnit*.5/totalPopWithExpMod

totalPopWithExpMod, includes combat units. So veterancy really isn't determined by the time that the units have
existed, or by the time the unit type has existed, so it's kind of a misnomer. It's just assumed that a user
wouldn't be able to generate an army of sizeable amount compared to his population very quickly, since
his population was built over time and cost so much to make. So it's an indirect measure of time.

So the problem here is that totalPop uses popSize, not expMod, and expMod is generally> than pop for
units...which means you could get veterancy fracs over 1 which is unacceptable. A fix is to
use the sum of all town populations + the sum of all unit populations in each town*expmod, and
we use this instead via a certain method, findPopWithExpMod or something along those lines.

If the user had genocide committed on his people, then he wouldn't have much in the way of military units, but if
he did have one last protected city, those units there would get the huge boost. I guess they should be fighting like hell.

So once we have the veterancy "limit" as described above, we can make a veterancyfrac that hits both the damage on
the defensive and offensive units that makes the damage increase or decrease, and we need to subtract the offensive
and defensive contributions due to their respective veterancies to get the total veterancy frac.

For instance, if offensive gets an off boost, defensive a def boost, then 1-offboost+defboost = vetfrac for
the defenders. If off gets no off boost, but defensive, then it's just 1-0+defboost. See?

In bombing, the formulae should remain the same. Veterancy shouldn't affect bombers more than other units.

How about popSize*size? We're using this in the number of units thing, right? Ideally, yeah.

So then is it fair? Tanks get like a popSize of 5, but an expMod of 10. So which do we use?
Is it really right to give tanks a factor of five advantage on gaining veterancy to soldiers?

Well given that one tank is equal to ten soldiers, then ten soldiers get twice the veterancy
of a tank using popSize. So we actually need to use expMod. Then ten soldiers are as
powerful as a tank/get the same veterancy.






What is the deal with expMod v popSize?

expMod can be qualified as "How much the unit is worth in combat value terms of soldiers."
It is also how it appears to the outside worth - how much is that unit worth in every
way in terms of combat value?

popSize can be qualified as "How much the unit costs in people to operate."
This is how many people are actually operating the unit - but the tech
the unit has may make this conglomeration of people worth more than say
four soldiers.

expMod really is more useful, popSize is just sort of a bitch move to make stuff work out.
I dunno. I just know that it all adds up for what I want to do with two variables, popSize and expMod,
and expMod seems to work for comparisons of unit sizes.

popSize came about because this is how much population a unit is worth - it helps determine
  the multiplier of the damage done by a unit,
 and the proportion of damage that a unit receives. The reason
I don't get rid of this in favor of expmod is that expmod too large a damage modifier in the damage equations to work out properly.

expMod is for exponential advantages, so that the number of soldiers it takes to kill a tank
do not get any exponential advantage on the tank(expmod is 10 for tank as opposed to 5(it's popSize) to make this happen.)

Expmod can also be used with veterancy to guarantee ten soldiers get the same veterancy as the 1 tank they kill.
It is also used with support calculations - since 1 tank = 10 soldiers, expmod is used to determine
relative sizes of units in comparison with each other to figure out a fair way to interchange between them
with balancing support armies. That is, if popSize is used, 2 tanks = 10 soldiers by a support maxSize's
count, but if expMod is used, 1 tank = 10 soldiers. See?





 How does population get counted?
 
 Many methods change peopleInside and AU sizes. But they don't change the things that keep track of them.
 
 Here is an overview of track variables:
 So there is a total variable for each unit - messengers and scholars are player wide,
 engineers and traders local to cities.
 
 Res[4] in each city is the city's civilian pop.
 
 Total population is all civilians+all combat units(combat units not in size but in popSize.)
 
 removePeople method is great for getting rid of people in buildings, not aus.
 
 removeAU does remove AUs.
 
 As AUs are only kept track of in totalPopulation, we do not ever change it except when they are lost,
 whether or not they are on raids. We remove population at the end of every attack server round according to the unit
 amount lost. Buildingserver is the only thing that adds population, and it does it whenever new things are built.
 When a building is destroyed, it calls removePeople to adjust the population. Similarly, when levelDown is called,
 and the building IS NOT destroyed, it also removes people.
 
 Finally, if the bomb logic detects that people have died, it removes the difference also.
 
 So a quick list of things that change population:
 1. Bldgserver(aus+people)
 2: Attack server(aus)
 3. Bombing logic block(people)
 4. levelDown(people)
 5. killBuilding(people)
 
 
 
 
 
 How does bombing as an option work?
 
 Well you must understand that there are two ways to do this:
 1. As an extension to genocide, require that it only commence bombing after
 all offensive units are destroyed, and it'd keep going until it killed everything or was called back.
 	-In this case, each bomb could have offensive characteristics, each one moving from ppl bomb down
 	to bldg bomb tier 3-tier1.
 2. As an extra option - each attack run results in both normal damage and bombing damage. Then people
 could do a genocide bombing type deal or a single attack or what have you.
 3. As two separate options: Coupled with genocide, it does complete annihilation. Coupled with attack,
 it does a single strafing run.
 	-This would have to work by performing bombing calculations after each attack, and modifying the genocide
 	if statements to include in the number of defensive units left standing the number of bldgs and or people...
 	
 	We could do the final one, and separate into options:
 	1. Attack
 	2. Genocide
 	3. Glass(Genocide+Bombing to the ground)
 		-Would use full bombing ability, but only after a clearing of the area. Uses 25% while under normal conditions.
 	4. Strafing Run(Attack+Bomb.)
 		-Would only use 25% of bombing ability, but can be done in a strafing run.
 		-Just like how you lose 75% of resources in glassing.
 	
 	In this case the codes would be "glass" and "strarun" in the program.
 
 What are the mechanics of bombing? How are they calculated?
 
 Well clearly each bomb should do some percentage of damage to in-building civilians and the buildings themselves.
 
 The Nova Bomb does tier 3 average damage to combat units
 HM does tier 2
 Hive does tier 1
 
 As for their effects on buildings, this'll clearly have to be proportional to the number of bombers.
 Do we want the number of bombersxprice of bombers = price of level to take down? This would give us
 a good estimate on how many bombers it'd take to take down a level. We have the return price method, which
 will return the price of new units, but we could also figure out the price for the current units - though
 some may have been lost in previous battles, diminishing the overall value of the current set.
 Also this would not be an exact number - different numbers of bombers from different players could
 take down the same level building. I guess that they should at least have enough so that if they
 were at the BASE amount of bombers(0) and built up to a certain number x, that number x and price y
 should, multiplied together, equal the price of the building's level.
 
 Then, it doesn't matter when they bought it, we set 0 arbitrarily. This is, after all, the cheapest
 way to do it - it's gonna mean a lot more bombers are required. Let's look at some numbers:
 
 Since we arbitrarily set bomber price at 20x the soldier price, we can use this as a starting point.
 One bomber costs 80*20=1600 resources.
 
 The next bomber costs 3200. And so on...we know that buildings cost their cost array * exp(lvl).
 
 We can sum that up. Building cost is around 400. And rises like Math.pow(lvl+1,2+.03(lvl+1)).
 
 So we've got number of bombers required >= 400*Math.pow(lvl+1,2+.03(lvl+1))/(price of bombers)
 
 
 price of bombers = 1400*y*(y+1)/2. Where y is number of bombers.
 
 1400*y^2*(y+1)/2 = 400*Math.pow(lvl+1,2+.03(lvl+1))
 
 bomberprice*(y^3 + y^2)/2 = bldgprice*Math.pow(lvl+1,2+.03(lvl+1))
 
	(y^3 + y^2) = 2*bldgprice*Math.pow(lvl+1,2+.03(lvl+1))/bomberprice
	
	(y^3 + y^2) - 2*bldgprice*Math.pow(lvl+1,2+.03(lvl+1))/bomberprice=0;
	run:
 NSolve[y^3 + y^2 - 8*(l + 1)^(2 + .03 (l + 1))/14 == 0, y] ==> make l the level.
So we need to solve this cubic. As this equation essentially applies to every lvl, we should
calculate it in advance and list the price table here:

So bldgprice*2/(bomberprice) = 400*2/1400 = 8/14. So let's do a calculation table here then for

	(y^3 + y^2) - 8*Math.pow(lvl+1,2+.03(lvl+1))/14=0;
 using mathematica and print it out. But there is more to this theory.

SEE BNR FOR THE NUMBERS ON THIS EQN

y = (1/3)*(-1 - Math.pow(2,2/3)/Math.pow((4 - 27*Math.exp(lvl) + 3*Math.sqrt(3)*Math.sqrt(-8*Math.exp(lvl) + 27*Math.exp(2*lvl))),(
   1/3)) - Math.pow((4 - 27*Math.exp(lvl) + 3*Math.sqrt(3)*Math.sqrt(-8*Math.exp(lvl) + 27*Math.exp(2*lvl))),(
   1/3))/Math.pow(2,(2/3)));
   
   This generates nonreal numbers. Java cannot handle them and any work arounds are incredibly memory intensive, and also nearly
   impossible to debug. Fucking Java. Possible workarounds:
   
   1. Use a different equation to determine bombers.
   2. Hardwire in the levels.
   
   Thoughts of 1:
   	-No direct price-to-damage correlation between building levels lost and bombers required. Then again,
   	people may not be sending the entire fleet anyway so this is kind of an arbitrary choice of price basis.
   	They may send only the top 10% and those bombers cost a shitton more than if you just built that amount from the
   	start. Price is really not a good indicator. Maybe a straight up exp function would do?
   	We know one bomber is roughly 1/4th of the price of a mine, so exp(bomber) maybe? Well if we choose a general number,
   	then Exp(lvl) = 10^11, something outrageous. So that can't really work. Linear bombers, possibly...
   	One thing we could do is do this numerically...I mean, what we could do is try values that solve the equation starting from one
   	and incrementing in units of five or so until a "correct" answer is found...it'd mean something like 600 calculations per bombing run
   	though.
   	
   	Combination it: Record values up till level 30 and then beyond level 30, extrapolate...make it start at level 30's number
   	and do increases in units of five until it gets close to the number. Agrizzled.
   	
   	
   How about this? Below the set number, the probability that a bombing will knock down a building level is proportional
   to Exp(-setnumber/number)/Exp(-1) for the probability that it breaks down the level. Then, above a certain point,
   the probability is 100% guaranteed to succeed...but I feel like, no matter what, something amazing needs to be able to happen.
   
   but it needs to be amazing. There needs to be room for fantastic survival in this engine. There needs to be a second modifier,
   the Omega modifier, that only activates above
   the 100% limit(setnumber<number) and if it's between 0-Exp[setnumber/numberofbombers]/(100*Exp[1]), we get that the bombing
   fails. So it's 1% at the equality limit, that it'll fail...but this only activating above the limit sort of means that below
   the limit, it better work out: at one below, there is a 1% chance of failure, and one above, 1% chance of failure. So the two
   solutions meet at the center. This is what we want.
   
   So final:
   
   For <setnumber, Exp(-setnumber/number)/Exp(-1)
   For >setnumber, Exp[setnumber/numberofbombers]/(100*Exp[1])
   
   Create random numbers that should be between 0-1 and test against this. How to find which level to bomb?
   
   Check target and find type match, then search for those buildings - find the number, create an array, measure the bombing success on each one,
   then take down the level of the highest level lost.
   
   In all of this we forgot to include the truly magical world of civilian bombing. In this case, we can use the same idea
   as building levels...actually, why not make the ability to kill the civvies proportional to the level of the building?
   No, because then sending bombers without bldg-blasting capability is a high resistance job...we could make it
   so that by lowering the building level, it kills civvies, but no. Since bombers scale exactly like civilians,
   we can just look at the base prices and compare those, since they will both have slope increases of the same rate.
   
   If you have ten bombers, the tenth bomber is going to be ten x the base price, and the tenth civilian, ten x the base
   price, so if you get 1600*10 = 16000 and 100*10 = 1000, then the civilian is still going to be 1/16th the price,
   so that the tenth bomber should kill 16 civilians. But civilians cost 80, actually, so just take bombers
   and do a division of 16/.8 = 20 civilians per bomber, roughly. Then keep this as an accumulator and subtract
   from it as you look at each bldg().
   
 	How about a limit proportional to
   
   Exp[bomberpeoplepointsleft/totalbomberpeoplepoints]/Exp[1]?
   
   and use a random number for each building to see if it gets hit?
   
   Then, in the first building hit, there is a 100% that we can empty the building on people points.
   And then we move on down, as we subtract bomber people points, the chance gets less that the building
   will take a heavy hit...and so buildings with few pop that get killed will not really effect
   the limit. Then there are always chances of less death. Of miracles - happening.
   Because to miss an entire building happens - whether or not it is highly full. And there may
   be not enough bombers to get at it.
   
Price of Bombers?

Note that setting 1600 as the price for the unit effectively is arbitrary. It really only determines the number of them
required to take down a bldg level. Their population should be adjusted for how many soldiers they kill, but they won't
kill many, and so we can set that arbitrarily too. But if they have such low stats, and incredibly crappy concealment, what
do they get? Well they get to do what other units can't, knock down buildings and stuff. That's a HUGE advantage. This also makes
them valuable and there is a need to protect them. As they are a separate unit class, apart from the rest in a way, everyone
will be in the same trouble and will need to protect them accordingly, how much so will evolve over time, and become natural.
Only further experimentation is possible.

Redesigning the bomber?
What do I want?
I want the bomber to be a semi-viable attack unit, with about the defensive and offensive capabilities of a tank.
I want the bomber to only be able to carry a bomb weapon.
I want the bomber to be half as concealable as the tank but in return get 2x the expmod.
	-This means that they can get an easier exponential advantage(bc flying) but less concealment.
The Bomber will be priced at 12x, as opposed to 6x, since it gets an extra mission parameters, namely bombing.

Let's use the numbers we have:

20 popSize means they receive a hell of a lot more damage(20x that of a soldier plus they are half as defensive,
totaling a 40x damage increase). They use a tier 2 weapon on average,
the same as a soldier can hold, so they do 20x the damage that a soldier does. With 4000 HP, they have 40x the HP
so the defensive issue is practically erased. So really they are an ultra powerful combat unit - 
if they do 20x the damage that a soldier does, and it takes roughly 20 soldiers to kill it, then it's
expmod should be around 20, but this should be tested. It on average has 25 for a stat, and soldiers
have roughly 100 for their attacking stat, leading to a factor of 4 x 20 x .5  x100 = 40*100 = 4000 which means in a one
on one, the twenty soldiers kill out the bomber. Which makes it's expmod exactly 20, same as it's popsize.

It will kill approximately 100/50 = a factor of 2 for weapons, so .5 * 20 * 100 * 2 = 2000 or twenty soldiers. 

So ideally, it's got 20x the price, which would normally undergo the concealment and no recon decrease of 37.5%, but this guy
does get another mission type and can bomb so it should be 20x the price straight up. Also, their weapons are not
maneuverable in any way so really the 20x is a loseout, only useful if you want to kill buildings and stuff.

 	
 
 What about price changes with growing prices and 10sold=1tank? 8x of what?
 
 If one tank is = to ten soldiers, let's look at how much ten soldiers cost - 
 
 if tanks, as you say, cost 8x, then say a soldier costs 1. Then a tank costs 8.
 
 But ten soldiers under linear cost raising costs 1+2+3+4+5+6+7+8+9+10 = 55. Well, shit.
 Shouldn't it be 8x55 then? Can this idea of yours be preserved? The answer? Yes.
 Around 100 soldiers, the price is 100+101+102+103...=1055. The tank at this point, at
 100 units, is 100*8 = 800. The price of the 100th soldier is 100x. The price of the
 10th tank is 10*8*x = 80x. The price of the 1000th soldier is 1000x. The price
 of the 100th tank is 800x.  I use 1000 soldiers->100 tank because they are equivalent,
 combatively.
 
 And this is roughly 8x the price. So as numbers increase, this relation DOES indeed hold.
 
 It's just, in the beginning, it really doesn't - so people will rather build tanks
 until they get high enough, then they'll go for men, and at high enough numbers, the two
 will be about evenly where they're supposed to be!
 
 
 
 
 How powerful are civilians?
 
 Well, 80 soldiers kill 160 civilians(12121 hp lost thereabouts) and vice versa does 8100 damage(about 80 soldiers),
 so they are on average half as strong as soldiers with the same gun and distribution of talents, though they carry
 no cargo points.




 How does the new fighting system work?
 Right, so it does the fractions on the three different stats, highest frac wins.
 Damage is doled out on weighted average form - you donate your damage times the weighted fraction
 which is your pop over the rest of the armies pop, all of these are summed for a weighted average. Number is
 multiplied by 100 and .5 (.5 as a factor to moderate it, otherwise evenly matched normal guys kill each other off
 completely) and 100 is the basic unit of health. It's actually multiplied
 by the percentage of the enemy population that that component makes up. 
 
 The exponential expmod thing factor is a measure of exponential advantage. The standards are set by the fact that
 10soldiers = 1 tank, and 40 soldiers = 1 Juggernaught, so that the exp advantage is 1 (no change) when these
 matches are made.
 
 
 
 
 
 When does pop go up?
 
 Only when civilians are made, as this is tallied for concealment and including troops cancels the effect.
 Yeah. Go fuck yourself.
 
 
 
 
 
 
 How does hp work and prices?
 
 Works on the basis that a tank can kill ten men(5x pop x 2x firepower = 10x hp loss as men)
 
Exponential advantage had to be removed for symmetry. We keep population instead because it determines concealment -
without this, concealment on tanks could be much higher than for humans.

 so ten men should be able to kill one tank, hence a tank has about 5x the hp, because it has 2x as much armor,
 so 5x2 = 10, 10x the overall effectiveness. See?
 
 
 Bombers can have 20x hp, since they're valuable, costing about 2 tanks but being weak as
 shit.
  
Tanks put out five times the damage due to population, and in general have twice the firepower, so they accumulate
roughly a factor of ten better than soldiers, so to counteract this, they should cost ten times as much to use.

Juggernaughts have four times firepower and ten times damage due to population, so should cost 40x as much.

Bombers should only cost 20x as much, because they have a single weapons slot and do next to no damage.

But because soldiers get better concealment, make it more 9x or 7x, 34x, 16x, etc, so that yeah they have
more firepower but not as much concealment, and no scouting ability. See the concealment quantifier section to get a good bead on this.

But the reality is the .5 modifier makes so that generally, you put out half as much killing power as
it takes to kill you. So if you've got 10 soldiers, they can kill 5. 1 Juggernaught can kill about 25 people.
1 tank can probably kill about 5 soldiers. But to kill each of these two units, you need 40 people
and 10 people, respectively. Similarly, if you wanted to kill the 40 soldiers, you'd need 2 juggernaughts.

But what does this mean for unit pricing?

 You've got to either price by the damage done, or by how much
damage it takes to kill you. So a Jugger takes 40 soldiers to kill but kills about 20-25. Now, if you took
this .5 modifier off it'd probably got back to 40-1 and stuff like that basically you always kill exactly
what you send and you'll always do a subtraction. Unacceptable.

Well the good thing is - if we go by one or the other, everyone faces that. So let's consider the 40x
price on Juggernaughts. Yeah, you could pay it and kill 25 soldiers - meaning you pay more to kill
those soldiers. Alternatively, though, if you want to kill 25 soldiers with soldiers, you must
buy approximately 40 soldiers because of the .5 difference...that's gonna be something like
.5*100*40 = 2000 = 20 soldiers. So either way you're paying the 40x price tag to get 25.

Now if we go by how much they can kill, then you're paying 25x for a Juggernaught or 40x for
the 40 soldiers to kill the same 25 soldiers - uneven. So the prices are correct




Quantifying the loss due to concealment:
 We're fucked. The equation goes like
 
 			sigmaTermdef+=((HUdef.concealment-au.concealment)*au.size*au.popSize/(HUdef.size*HUdef.popSize));
					
		
				 sigmaTermdef = HUdef.concealment-sigmaTermdef;
			double expTerm2 = Math.exp(-(currentArmySizeDefPop+1)/((sigmaTermdef+1)*(holdAttack.town2.getPop()+1)*Math.exp(holdAttack.town2.getStealth())));
				
 This is where we stand. Okay so tanks take AWAY from concealment half as much as 10 soldiers would.
 They also get 5/2 = 2.something in the expterm whereas the 10/1 doesn't... and by size, it's 1/2,
 which Exp[-10] is much worse than Exp[-.5]. By expmod, it's Exp[-5] vs Exp[-10], either way
 soldiers are gettin screwed. What if we...modded the equations a bit. We don't want concealment
 to hurt having tanks and juggers, so we make it that we use their expmod and halve their
 contribution to concealment if they're a tank and fourth if it it's a jugger.
 
 Then it'd be 10/1 and 10/1. Or 40/1 and 40/1.
 
 Different formula. Say x is soldier concealment. If we did
 
 popSize/x, then tanks get 5/2, jugger 40/4, and bomber 20/.5=10. Bombers interestingly
 are the same. If we do expMod/x, then we get 10/1, 10/2, and 40/1,40/4 and 20/1,20/.5.
 
We want 5x harder to conceal, 10x harder, 20x harder, respectively. Then this reflects
the fact that they detract 5x more, 10x more, and so on from concealment.

So we can get that five up there by using popSize. The problem is 10 soldiers
are 1 tank, so the 10soldiers = 10 pop, the 1 tank is 5. So we switch
to expmod then. The problem is tanks are twice as good, juggers four times, at concealments. What we need are to get rid of these.
So we just do expMod/(concealment/2 or 4 or .5 to cancel out the differences) gets me back to zero.
Then to make tanks 5x harder to conceal, we multiply this equation by 5, or 10, or 20 if jugger or bomber also.

How do we apply this? By first using the expmods, sure, but the concealment is generated by the HU and differences.
So we need a new thing. So we change the sigmaTerm to go by ((moddedhigh-moddedcurrent)*au.size/(total size.)
and we mod them as we grab them. This also means tanks and juggers, which can support more powerful weapons,
will naturally go for anti-concealment, because they're a hell of a lot easier to hit.] Since the
modded concealments already contain information about the population sizes of these units, or rather
the detriments due to these units, we just need to worry about their proportion of size as opposed to
others. So 10 soldiers contribute as much as 10 tanks, because the 10tanks already have their concealments
modded by a factor of 2, to get 10/1 and 10/1, they add equally to the sum.

Instead of 5, 10 or 20 we do 2x, 4x, or .5x on the fucking fraction itself from below:

BUT WAAAAIIIITTT: if we do (samexpmodeitherway)/(sameconcealmenteitherway) * 2, then tanks
get that shit. so tanks get divided by 2 once because they have 2x the concealment,
and then once again because they need to be 2x harder to hide. First we equalize,
then we make it harder for juggers and stuff. How do we implement this? 
by when adding a variable that does it for us called moddedExpMod or something.

so okay if you send x number of tanks you get x resources and you get x power in x time
4:12 PM
 
to get the same concealment
4:12 PM
 
you send 2x soldiers and you get 2x resources and you get 1.8x power in 2x time
Noah J. Prince
4:12 PM
why 1.8
Jiggly Yo Wigly
4:12 PM
accounting for tier 2 weapons being 110% power of tier 1
4:12 PM
 
however you keep in mind that 2x soldiers cost in the long run 2x as much
Noah J. Prince
4:12 PM
so tanks are more powerful
Jiggly Yo Wigly
4:13 PM
so okay if you send x number of tanks you get x resources and you get x power in x time for x resources to build
4:12 PM

to get the same concealment
4:12 PM

you send 2x soldiers and you get 2x resources and you get 1.8x power in 2x time for 2x resources to build
Noah J. Prince
4:13 PM
but i guess it makes up because you can have more of them
Jiggly Yo Wigly
4:13 PM
I modified my previous statement
4:13 PM
 
so I think we can cancel out the 2x power part
4:13 PM
 
because you pay for the power by buying the units
4:13 PM
 
right
Noah J. Prince
4:14 PM
sure
Jiggly Yo Wigly
4:14 PM
so then what's left is you get 2x resources for 2x time
4:14 PM
 
or x resources for x time
4:14 PM
 
that's what's called a balance
Noah J. Prince
4:14 PM
and your whats called a faggot
Jiggly Yo Wigly
4:14 PM
with there of course being a wait of .2x less power in the soldiers due to the fact that you max out their build reduction due to engineers earlier in the game
Noah J. Prince
4:15 PM
program that shit and update it asap
4:15 PM
 
so i can pwn thornes bitch ass
 
 ---erase below---
 For human soldiers, it's easier by a factor of Exp[-1]Exp[5] =  54% to conceal themselves.
 (do Exp[soldier]/Exp[tank] and see the 5 come up to be positive.)
 
 Plus, soldiers can do recon missions. So concealment on a tank is downgraded by 50%. How to make up for this?
 In price. Tank has more points to put there, though...but they have to put five times
 the concealment to achieve the same as a soldier. Five times. They get 10x the health
 and can do 10x the damage, so the price needs to be lowered. concealment is one of four
 attributes, so 50% of it is 50% of 25% which is 12.5%, so a 12.5% reduction is around an 1/8th
 of the 10x ideal price. So that's about 8.75x the price. Similarly, 35x the price for Juggernaughts, and 20x for bombers.
 
 	Ca:double expTerm1 = Math.exp(-currentArmySize/(sigmaTerm*Math.exp(holdAttack.town1.getStealth())*20));

 	Cd:double expTerm2 = Math.exp(-currentArmySizedef/(sigmaTermdef*holdAttack.town2.getPop()*Math.exp(holdAttack.town2.getStealth())));

where sigmaterm and sigmatermdef are concealment, modified.
---erase above
One tank's cargo is like 2x soldiers, but 10x soldiers = tank combat wise. So? Well
tank has 5x population and this gets multiplied by the 2x to = 10x. Similarly,
Jugger has 4x and has 10 people, so it gets 40x. So Cargo is all the same.

But what of the fact that soldiers get less speed? In terms of armor, it does not matter,
they're supposed to be weaker - tanks and juggers lose out on concealment and get
compensated, and in cargo they are equal, but in speed there is an imbalance.
Tanks are on average 2x faster and Juggers are 4x faster. Well, if you make it
so that Juggers get 1/4th as many res as a soldier in cargo, and tanks 1/2 as much,
then a tank can raid 2x faster but only grabs 1/2 as much.

So how do we solve this monstrously unfair equation? So we know that tanks
can make a factor of 2 more raids than soldiers, and juggers a factor of 4.
Should they cost more due to this? Well clearly. 3

 
By 35x, we mean they cost as much as it takes to build 35 soldiers, which is different than 35*price_of_one_soldier.
Then they are on the exact same scaleup of price as the soldier is...
 So I changed prices of units so that the multiplier for a Jugger was not just 35,
which is 35*the base price of a soldier, but actually the price of building 35 soldiers.
i.e. it takes into account the fact that each of the 35 increases in price by making the
multiplier be 35*(35+1)/2 so that when it hits the base price, it ups it to 35 soldiers worth,
then the factor that is determined by how many of those units are in existence is multiplied on this
quantity to give the price of the new Juggernaught, bitch!

Now really it does make sense to have tanks and juggers, they cost exactly what you'd expect, finally.

 Continuing on my track, Do we need to worry about tanks v juggernaughts?
 
 Not really...I suppose they'll have to be worried about, though.
 Consider that to cancel out the exp factors, the tanks with expmods of 10 will need four to cancel out the one juggernaught,
 
 And, as expected, the test yields 4 Tanks = 1 Juggernaught, fucking awesome. However, Juggernaughts give twice the pop that Tanks do,
 but we've got to keep that. But why build Tanks if one Jug takes four out with half the pop-cost?
 This needs to be accounted for...but the price has already been adjusted for soldiers relatively and you can't change
 stats up, so something else needs to be altered.  However Juggernaughts get twice as much "Attention" in multi-component battles as tanks do.
 So yeah, Juggernaughts are half the population(so easier concealment) than four tanks, but they get twice the combative attention
 due to the weighted fraction, so that evens out there. If you want concealment, go for a Jugger, but it'll take a lot more damage,
 even though it has the same HP as the four tanks. WOOH! I think. Best to just field test it.





 How does Genocide work?
 
 What the fuck. Okay, at the beginning of attack server, any new civilians are added if allClear is on.
 Also, if there are no defensive combat units and genocide is on but there is no allClear then civilian
 units are added before the thing runs it route, so the first attack is genocide.(i.e. allClear being the boolean
 used to indicate a battle was fought and now all defensive units are dead and civvies are engaged now, so if it's off with no
 combat units, this is the first run and there is no defense) 
 
 At the end, if no resFlag has been thrown(i.e. no standoffs) and Genocide is on, then it does some tests:
 
 If number of units defending is > 0 and allClear is false, then it sends it back for another raid
 If number of units are = 0 and not all Clear, then civilians fight
 If number of units > 0 and allClear is true, then civilians go for another run around...
 if number of units = 0 and all Clear is true, then civvies are dead, and resources should be collected.
 
 This is the general idea, that you can find strewn in if-else statements at the end. I do not believe it alters much.
 

 
 
 
 
  * Delays, Synchronizer:
 * 
 *There will be a timed delay between the server's player and our own. This needs to be rectified.
 *The time it takes to set up the player object(to "synchronize") needs to be taken into account - if it
 *takes 2s, then the player object, once set up, needs to increment itself by two seconds. Or we can
 *just forget about it.
 *
 *Back to our previous example, if this started at 3:00:00, server time, then this will always be
 *3:00:02 our time, if there is always a lag of some sort. This lag will be different for everyone.
 *
 *What we need then is a clock from the server...no, we need to correct for delays, and during those delays, it'd
 *probably be good of we used a player buffer.
 *
 *
 
 
 
 
 How does the new compile script work?
 1. User presses button, mouse adapter calls the compileScript method on BattlehardViewer,
 and sets up the lookIfCompiled timer. checkCompiler is set equal to true, practically freezing the program.
 2. compileScript loads instructions onto db.
 3. Synchronizer receives output and calls the showCompileOutput method, which shows debug screen
 if it's up with text and sets up the red C symbol and corresponding timer if it did go over the limit.
 If synchronizer never receives output within the 7s, then Synchronizer calls showCompileOutput with an error string thing
 and turns off checkCompiler. This is all contained in a single if statement.
 
 
 
 
 
 How does synchronization work?
 When loadplayerup is called, a buffered player is loaded with info. Then, synchronize on p is set
 to true. When a drawing function is called by the applet, the drawing function checks p's synchronize,
 and if it is true, it moves the values of buffered player over to p, and then continues on. Also, p's
 thread quits incrementation during the synchronize is true phase.
 
 
 
 
 
 Why not make units proportional to weaponry?
 You pay for weaponry with the amount that you can put on - one tier 2 is a little over twice as powerful
 as the equivalent tier 1, but it comes at the cost of 2 slots instead of 1. So a unit can hold 3
 different tier 1s for instance to attack three different attributes or it can be more powerful against
 2 attributes by equipping 1 tier 2 and 1 tier 1, making it stronger against 1. So this is how it pays.
 
 
 
 
 
 How do you make building deconstruct time?
 Idea would be to take as long to deconstruct as to construct, but we know that is bull.
 How about something more interesting. Newer improvements are harder to destroy. Older ones aren't.
 Why don't we just do something equivalent to lvl up but instead lvl down. Set deconstruct = true and
 then "level up" the building, except when it gets to the top tick, it gets killed.
 
 What about resource returns? There shouldn't be much - all the leveling would yield a flood. Perhaps none at all
 for now and we'll see how it goes.
 
 
 
 
 
 How do you make the buttons freeze when you click something that needs to make a server-call?
 
Right so if a button is pressed, then it turns on compilePressed and a timer, then the adapter gets locked up,
and BHViewWatch checks the output channel every second as long as compilePressed is on, or before the timer runs out, whichever
is first.

Then BHViewWatch constantly reads the output channel, and if it sees output:, it calls up the show compile script,
if it's true, it just releases the buttonPressed argument.





 Why does appletviewer crash?
 
 It may be because you mixed Panels with Swing components(J-stuff.)
 
 
 
 
 
 How does the multi-lvling of buildings work?
 
 The user calls for a second level up of a building. Then the cost is adjusted for lvlUps in the exponential, and
 if lvlUps is 0, nothing bad happens there.
 The town's level up is called, it subtracts the resources(Correctly) and if the lvlUp is 0, we
 enact the start timer and add it to the bldgserver, if not, we simply increment levelups.
 When the building server sees a building change level, it decreases lvlUps, and if it's 0, then
 the building is removed. If it does not, then the building's levelUp is called since the price
 has already been paid in advance and the clock goes ticking again.






 How are buildings loaded?
 		// The way buildings work is that they are first made and then put in via the loadBuilding method. This way,
					// all engineers and stuff like that is slowly built up.
					// Then, when a player is made and it calls the add player method on all towns, all towns set up their
					// player's stuff and also reconfigure all buildings' various ticks cycles to account for engineers that
					// may have been found after that particular building has been loaded.
					 * 
					 * 
					 * 
					 * 
					 * 
					 * 
					 * 
					 * 
How do I slow down resource rates?
Keep your integers...make an intermediary double that gets added to from resInc, when it goes over 1, all above 1 in that
buffer is put into the resources, and so it continues.





What are the rates of increase?
Resource Increases should rise like (level+1)^2 on the original rate, which'd be 20.


The idea is that one level up of your mines greatly increases your resource collection rate, and that rate
can sustain the continual purchase of troops over time up until the number of troops is of the order of (lvl+1)^2, then
one needs to become frugal once more. Unit base price should be around 20 resources also so that all the math works out.

Formula is (lvl+1)^(2+.03*(lvl+1))*100.
Run Plot[(l+1)^(2+.03*(l+1))*100/((20*(l+1)^2)), {l,0,30}] to see the characteristic times in mathematica.


    100 is the resource, not another multiplier - so actually is cost!

Building levels work like mines with the exact same formula. This is because
Caps need to be around the size of the number of people that it'll take for the user to want to upgrade their mines - i.e.,
when number^2+n = 60*(minelvl+1)^2 roughly...this is because otherwise we'll have people not being able to build units beyond caps
so they'll hit up their buildings till the buildings become too expensive, then upgrade their mines to make more bldg levels,
but then civvy units will be worth nothing relatively...so the caps need to rise like the number of units it's economical
to build. Why do the rate start at 20? See second paragraph below.

These resources can be seen as one resource, so metal starts at 20, each mine roughly costs 100 100 100 100 but
you must be careful to not go above or below too much - and if you do, that other mines take up the slack with increases.

Specifically, each unit's element price is about 20 per unit, so it's
20*n*(n+1)/2 = 10*n*(n+1) = 60(lvl+1)^2, solving for n gives the cap at a building lvl, we want
bldg lvls to rise like mine lvls, so we say minelvl=bldglvl and use bldglvl in the equation
to find the n at which people will upgrade mines and set it as a building cap so people will
upgrade buildings too. So n^2+n = 6(lvl+1)^2, c = -6(lvl+1)^2

n^2+n-c, n = -1/2 + sqrt(1+24(lvl+1)^2)/2; Best to drop the ones to get
n = sqrt(24*(lvl+1)^2)/2 = sqrt(6)*(lvl+1) = cap.

					
 */
/*
Right-o, you're opening your own personal can of whoopass. This will be the greatest
challenge you ever undertake. You are installing the script reader. The if statement
will encapsulate nested if functionality by shipping the inside code back through
the script generator. I know, it's the shit. The boolean checker now can
check for havemetal() only. And even that will present problems. This whole thing
is just one big clusterfuck of a mind problem. Figure it out and meet deadlines and you will
be rich.

 Send raid back and handle it.



 * 
 * -J. 
 */

/* Journal:


TEST SERVER SIZING ON TEST!

http://www.javabeginner.com/

Common bug list



Okay so to do:
Checked APIs:
Attack Automation
Attack Integration
Trade Automation
Build Automation(check buildcombatunit)





The Schedule:


Fixes to master branch:
1. Make it so you set the height of the images explicitly to fix the scrollbar problem.
2. Make it so genocide is 50% take again






Zong callback URI:
www.aiwars.org/AIWars/GodGenerator?reqtype=upgrade

 To Do:
1. Create league with troops in town, troops don't leave.
2. Send support mission, recall, support disappears.
3. Unable to recall from Id towns?
11. DebugRevelations class
12. Fuckin vaca mode.



Systems To Do:
13. Install APR library(see Java links) on fake God then put it on real God. See if it helps.
14. img1 and img2 subdomains.



-J.


Version control with multiple programs: github.com.
Dropbox.com for common folders.
Response Times for Website: newrelic.com
dig into JAX-RS, it's a Java spec thing for exposing an HTTP interface to objects
PHD - passenger for pushing rails to server
Commission Junction - cj.com
Convert2Media evony

http://www.linuxsecurity.com/content/view/121960/49/
http://www.cisco.com/en/US/tech/tk59/technologies_white_paper09186a0080174a5b.shtml
DDOS attacks
http://shebangme.blogspot.com/2010/03/ddos-attacks-and-how-to-handle-them.html

http://cloudservers.rackspacecloud.com/index.php/Common_iptables_rules_for_CentOS_5.3
For ip tables. If you restart need to save rules.





Ideas for extras once game engine is finished:
1. Weather---CHECK---
2. Different city-types.---CHECK---
3. New and exciting different buildings that have different functionality
4. Civilian troopers of some sort?
5. Floating cities?


	
-J.
 */


/*
Version Info:
v.12
-Research System Implemented
-GameClockFactor implemented to allow for relatively dynamic processing time for player objects.
-Arms Factories now serve like offensive bunkers.
-Troop push and bunker up, two new researches, now implemented.
-Basic BattlehardFunctions API completed.

v.11
-League System implemented
-Mines implemented
-Warehouses implemented
-Draconian Messaging System implemented
-Various improvements and additions to the API and general security

v.10
-Trading System implemented
-GodGenerator turned into a Servlet
-Cloud server setup and God is now serving to JS front end.
-Markus created Town, World Map menus in BHViewer v.2
-Better error handling, playersript now spawns a copy when it encounters an error,
for instance.

v.09
-Battlehard Combat Engine completely implemented
-Menus for Arms Factory, Bunker, Unit Template Creation Menu, HQ Building, Combat Simulator, and Raid Sending implemented!
-Split the viewer and God into separate packages to prevent algorithm theft.
-Hardwired commands into playerscript to reduce lag.
-JPI now can handle multiple classes and doesn't need to add numbers to them, the classes unload.
-God now passes connection to the Players, so multiple connections to the db are not required.
-Theoretical protections in place for out of memory errors
-Memory leaks taken care of in both client and serverside, I think.
-Logon screen now takes user and password stuff.
-Can click towns to goto HQ menu and attack them.
-Incoming/Outgoing listed now

v.08
-Works online now
-There are issues with the construction yard submenu, it is doing something that java editor doesn't
to take up so much memory that it skips ahead every three seconds or so, will look into it in later versions.
-Implemented heuristic programming - player objects in the viewer synchronize occasionally with server in background
-Status reports implemented.

v.07
-Construction Yard submenu implemented
-Deconstructing and multi-lvling buildings implemented
-MySQL Server implemented
-Various minor graphical updates

v.06
-Sweeping changes in scripting: Scripting is now taken care of by the Java compiler, not my interpreter. 50% of the work is now gone.
-Scripting menus have been set up in game(Compile, Run, Stop, Edit)
-Menu bar, finalized map and town menus have been set up, world map and town switch works.
-Enlarged the gaming world to include the entire Alpha universe map.

v.05
-Added scopes
-Added double buffering to take care of image flickering
-Added the building and cityview graphics

v.04
-Tweaked the combat system to be more fair by increasing the cost of units by a factor of ten.
-Added Engineering effects(Reduced building times, etc.)
-Added the levelUp() function to the language.
-Added the canBuy() function to the language.
-Added the ability to interpret String, Double, and Integer expressions.
(Before, ints,doubles,Strings, had to be defined directly, ex. int x=5; or String x="fubar"; but int x=y+6/7*(5+345); was not
recognizable by the language.)

v.03
-Modified the display program to be separate from God and Player programs. It now reads from a txt database every 1s and updates
accordingly.
-Modified God to read off txt database and spin off Player threads which alter their parts of the database. They can only change other
players' data through the use of God's methods.
-Added the ability to support nested conditional statements within conditionals, however, multiple redundant nesting
such as ((x)) or ((x&&y)) is still not supported yet.
-Display program now uses one map shard and the town graphic.

v.02
-Added Genocide option
-Added resource taking during attacks/genocides(50% capacity for Genocides)
-Added all civvy units and all buildings. Civvy and bldgs do not provide functions other than building themselves yet.
-Added the town screen where you can see your buildings.
-Added all the resources, when mouseovering towns, see their resources.
-When mouseovering buildings, see data on buildings.
-Implemented boolean logic(AND, OR, NOT) and several new "have"-type functions, as well as == and =. No >, <, <=, >= yet. 
-Put in int, string, boolean, and double.
-Implemented if then else statements

v. 01
-Supports 2 players, single towns.
-Supports attacking
-Supports metal resource
-Gameclock implemented.

 */
public class GodGenerator extends HttpServlet implements Runnable {
	//BattlehardViewer bh;
	int res1[] = new int[8]; // There is metal and number of bland military units for this alpha software.
	// Metal is 0, timber is 1. Stealthtech is 2. Population is 3.
	// 28.3 for .01, 2.83 for .1, 
	
	public static double gameClockFactor=10; // At 1, 1 tick = 1s.
	public static double sessionLagTime = 2*3600000; // How much time in Date speech till a session logs out.
	// so with timers, you'll want to divide by the gameClockFactor, diminishing it...
	// so divide 10 ticks by 10, then with 1 tick=1s at ticks = 10 is 10s to do,
	// now at 1 tick = 10s, the timer will be 1 tick means 10s to do. Same dealie.
	// Really this just gives the computer time to make changes, and we can
	// expand it as the game grows.
	
	/*  1. Center of Nothing( Relative Mag: .5)
	 2. Center of 10% daily resources (Relative Mag: 1)
	 3. Center of 20% Daily resources and 50 KP (Relative Mag: 1)
	 4. Center of 30% Daily Resources, 65 KP (Relative Mag: 1)
	 5. Center of 50% Daily Resources, 80 KP (Relative Mag: 1)
	 6. Center of random API, of hidden soldier blueprint(Rock), random tech points(Civvie in Grass, Military in Desert) (Relative Mag: .5)
	 7. Center of hidden tank blueprint(Grass), and of the four resource increaser technologies(3 in Grass, 1 in Rock/Desert) (Relative Mag: .3)
	 8. Center of hidden Juggernaught blueprint(Desert) (Relative Mag: .1)
	 9. Center of hidden Missile Silo or Zeppelin(Silo in Rock, Zeppelin in Desert)(Relative Mag: .05)*/
	
	/*

	 * Prize codes:
	 * nothing
	 * daily10
	 * daily20
	 * daily30
	 * daily50
	 * lowkp
	 * medkp
	 * highkp
	 * api
	 * soldier
	 * civvietech
	 * militech
	 * tank
	 * resInc
	 * juggernaught
	 * silo
	 * zeppelin
	 */
	//	public double[] generateGaussianDist(int numOfPoints, double height, double center, double width) {

	public static double nothingCtr = 1;
	public static double nothingWidth = 2; // 2 days homes.
	public static double nothingHeight = 1;
	public static double daily10Ctr = 2;
	public static double daily10Width = 2; // 2 days homes.
	public static double daily10Height = 1;
	public static double daily20Ctr = 3;
	public static double daily20Width = 2; // 2 days homes.
	public static double daily20Height = 1;
	public static double daily30Ctr = 4;
	public static double daily30Width = 2; // 2 days homes.
	public static double daily30Height = 1;
	public static double daily50Ctr = 5;
	public static double daily50Width = 2; // 2 days homes.
	public static double daily50Height = 1;
	public static double lowKPCtr = 3;
	public static double lowKPWidth = 2; // 2 days homes.
	public static double lowKPHeight = 1;
	public static double medKPCtr = 4;
	public static double medKPWidth = 2; // 2 days homes.
	public static double medKPHeight = 1;
	public static double highKPCtr = 5;
	public static double highKPWidth = 2; // 2 days homes.
	public static double highKPHeight = 1;
	public static double apiCtr = 6;
	public static double apiWidth = 2; // 2 days homes.
	public static double apiHeight = .5;
	public static double soldierCtr = 6;
	public static double soldierWidth = 2; // 2 days homes.
	public static double soldierHeight = .5;
	public static double techCtr = 6;
	public static double techWidth = 2; // 2 days homes.
	public static double techHeight = .5;
	public static double tankCtr = 7;
	public static double tankWidth = 2; // 2 days homes.
	public static double tankHeight = .3;
	public static double resIncCtr = 7;
	public static double resIncWidth = 2; // 2 days homes.
	public static double resIncHeight = .3;
	public static double juggernaughtCtr = 8;
	public static double juggernaughtWidth = 2; // 2 days homes.
	public static double juggernaughtHeight = .1;
	public static double siloCtr = 9;
	public static double siloWidth = 2; // 2 days homes.
	public static double siloHeight = .05;
	public static double zeppelinCtr = 9;
	public static double zeppelinWidth = 2; // 2 days homes.
	public static double zeppelinHeight = .05;
	
	public static int buildingSlotTechPrice=10; // how much it costs to buy this research.
	public static int buildingLotTechPrice=20;
	public static int buildingStabilityTechPrice=5;
	public static int commsCenterTechPrice=5;
	public static int townTechPrice=50;
	public static int civEfficiencyPrice=5;
	public static int afTechPrice=5;
	public static int bunkerTechPrice=5;
	public static int stealthTechPrice=10;
	public static int scoutTechPrice=10;
	public static int supportTechPrice=5;
	public static int aLotTechPrice=20;
	public static int soldierTechPrice=50;
	public static int tankTechPrice=100;
	public static int juggerTechPrice=200;
	public static int bomberTechPrice=400;
	public static int troopPushPrice=100;
	public static int zeppelinTechPrice=800;
	public static int missileSiloTechPrice=400;
	public static int recyclingCenterTechPrice=100;
	public static int metalRefTechPrice=200;
	public static int timberRefTechPrice=200;
	public static int manMatRefTechPrice=200;
	public static int foodRefTechPrice=200;
	
	public static int attackAPITechPrice=50;
	public static int advancedAttackAPITechPrice=75;
	
	public static int tradingAPITechPrice=50;
	public static int advancedTradingAPITechPrice=75;
	
	public static int smAPITechPrice=200;
	
	public static int researchAPITechPrice=50;

	public static int buildingAPITechPrice=75;
	public static int advancedBuildingAPITechPrice=125;
	
	public static int messagingAPITechPrice=100;// EXCEPT SENDYOURSELF
	
	public static int zeppelinAPITechPrice=400;
	
	public static int  completeAnalyticAPITechPrice=50;
	
	public static int nukeAPITechPrice=200;
	
	public static int worldMapAPITechPrice = 50;


	
	public static double speedadjust = .001; //down raises timers //283 s for .001 for a trade between adjacent towns

	public static int traderCarryAmount=300;
	public static int traderSpeed = 100; 
	public static int lotTechLimit=18; // how many lots you can have maximum.
	public static int tradeDistance = 20;
	public static int maxMessageLimit=100;
	public static int stockMarketTime=(int) Math.round(tradeDistance*10/(traderSpeed*speedadjust));
	public static int invasionDistance = 20; // This is the limit on how far you can make an invasion from.
	// default stockmarket time
	public ArrayList<Hashtable> mapTileHashes;
	private Hashtable[] achievements;
	UberConnection zongCon;
	
	public Trader Trader;
	public static int leagueLagTime=1; // how many ticks before taxes are done with leagues.
	//public int gpid; // The player this God is based around, if -1, is a normal God.
	//public int lgpid;
	public int gameClock=0;
	public boolean serverLoaded=false;
	public MemoryLeakDetector Gigabyte;
	ArrayList<Iterator> iterators=new ArrayList<Iterator>();
	
	public ArrayList<Hashtable> programs = new ArrayList<Hashtable>();
	public Hashtable accounts = new Hashtable();
	private ArrayList<Player> iteratorPlayers;
	private ArrayList<Town> iteratorTowns;
	public Maelstrom Maelstrom;
	public boolean loaded=false;
	public static int totalUnitPrice=70;
	public static int unarmedAmt = 10; // The unarmed weapon which occurs when soldiers/tanks/juggers/bombers
	// fight unarmed. Currently is 10.
	public static int minIterators=3;
	public static int maxIterators=10;
	public static int printWhenTicks=(int) (100000/gameClockFactor);
	public int printCounter=0;
	public static int ticksTillIncrease=5;
	public static int ticksTillDecrease = 20;
	public static int mapTileWidthX=9;
	public static int mapTileWidthY=9;
	public static int saveWhenTicks = (int) (60*15/gameClockFactor);
	public int saveCounter=0;
	Hashtable entryPointURLs = new Hashtable();
	Controllers Router;
	int res2[] = new int[7]; // For the second player.
	int resInc[] = new int[7]; // for both players, for now.
	public boolean killGod;
	OutOfMemoryError holdE;
	Timer lagTimer;
	Exception holdE2;
	int ticks = 0; // Gameclock.
	boolean testMe = false;
	boolean elseflag = false;
	//ArrayList<Raid> attackServer = new ArrayList<Raid>();
	//public static int maxXSize=60,maxYSize=42; // the size of the world in town-sized chunks.
	//ArrayList<Raid> attackServer = new ArrayList<Raid>();
	// NOTE I USED NONLOCAL ADDRESS FOR MOST RECENT PROGRAM TEST...I THINK IT'LL STILL WORK JUST FINE THOUGH.
	//static String url = "jdbc:mysql://72.167.46.39:3306/bhdb";
	static String url = "jdbc:mysql://localhost:3306/bhdb";
	static String zongurl = "jdbc:mysql://localhost:3306/zongPayments";

	static String accessurl =  "jdbc:mysql://localhost:3306/accessCodeDB";
//	static String pass = "D1einfuk";
	static String user = "root";
//	static String user = "bhdbuser";
	static String pass = "battlehard";
	String specurl,specuser,specpass;
	static boolean server = true;
	static boolean activated = false; // activate for better streamlining.
	public String status="";
	private static String srcdirectory = "/users/arkavon/documents/apache-tomcat-6.0.26/webapps/AIWars/WEB-INF/classes/src/";
	private static String bindirectory = "/users/arkavon/documents/apache-tomcat-6.0.26/webapps/AIWars/WEB-INF/classes/";
//	private static String srcdirectory = "/home/jmp3qa/BattlehardAIWars/src/";
//	private static String bindirectory = "/home/jmp3qa/BattlehardAIWars/bin/";
	boolean godHere = true;
	public UberConnection con;
	public Thread holdGod;

	public void doPost(HttpServletRequest req, HttpServletResponse res) 
	throws IOException, ServletException {

	
	//res.setContentType("text/html");
	res.setContentType("text/html");

	PrintWriter out = res.getWriter();
	
	if(req.getParameter("reqtype").equals("command")) {
		Router.command(req,out);
	} else if(req.getParameter("reqtype").equals("saveProgram")) {
		Router.saveProgram(req,out);
	}else if(req.getParameter("reqtype").equals("serverStatus")) {
		Router.serverStatus(req,out);
	}else if(req.getParameter("reqtype").equals("createNewPlayer")) {
		Router.createNewPlayer(req,out);
	}else if(req.getParameter("reqtype").equals("forgotPass")) {
		Router.forgotPass(req,out);
	}else if(req.getParameter("reqtype").equals("upgrade")) {
		Router.upgrade(req,out);
	}else if(req.getParameter("reqtype").equals("logout")) {
		Router.logout(req,out);
	}else if(req.getParameter("reqtype").equals("username")) {
		Router.username(req,out);
	}else if(req.getParameter("reqtype").equals("noFlick")) {
		Router.noFlick(req,out);
	}else if(req.getParameter("reqtype").equals("support")) {
		Router.support(req,out);
	}else if(req.getParameter("reqtype").equals("flickStatus")) {
		Router.flickStatus(req,out);
	}else if(req.getParameter("reqtype").equals("getTiles")) {
		Router.getTiles(req,out);
	}else if(req.getParameter("reqtype").equals("resetPass")) {
		Router.resetPass(req,out);
	}
	else if(req.getParameter("reqtype").equals("linkFB")) {
		Router.linkFB(req,out);
	} else if(req.getParameter("reqtype").equals("login")) {
		Router.login(req,out);
	} else if(req.getParameter("reqtype").equals("FBBlast")) {
		Router.FBBlast(req,out);
	} else if(req.getParameter("reqtype").equals("makePaypalReq")) {
		Router.makePaypalReq(req,out);
	}else if(req.getParameter("reqtype").equals("sendTestEmail")) {
		Router.sendTestEmail(req,out);
	}
	else {
		out.println(status);
	}
	

	out.close();
}
	public void doGet(HttpServletRequest req, HttpServletResponse res) 
	throws IOException, ServletException {

	
	//res.setContentType("text/html");
	res.setContentType("text/html");

	PrintWriter out = res.getWriter();


	if(req.getParameter("reqtype").equals("world_map")) {
		Router.loadWorldMap(req,out);
	} else if(req.getParameter("reqtype").equals("forgotPass")) {
		Router.forgotPass(req,out);
	}else if(req.getParameter("reqtype").equals("serverStatus")) {
		Router.serverStatus(req,out);
	}else if(req.getParameter("reqtype").equals("convert")) {
		Router.convert(req,out);
	}else if(req.getParameter("reqtype").equals("newsletter")) {
		Router.newsletter(req,out);
	}else if(req.getParameter("reqtype").equals("player")) {
		Router.loadPlayer(req,out,false);
	} else if(req.getParameter("reqtype").equals("league")) {
		Router.loadPlayer(req,out,true);
	}  else if(req.getParameter("reqtype").equals("login")) {
		Router.login(req,out);
	}  else if(req.getParameter("reqtype").equals("makePlayers")) {
		Router.makePlayers(req,out);
	} else if(req.getParameter("reqtype").equals("FBBlast")) {
		Router.FBBlast(req,out);
	}else if(req.getParameter("reqtype").equals("logout")) {
		Router.logout(req,out);
	}else if(req.getParameter("reqtype").equals("noFlick")) {
		Router.noFlick(req,out);
	}else if(req.getParameter("reqtype").equals("flickStatus")) {
		Router.flickStatus(req,out);
	}else if(req.getParameter("reqtype").equals("getTiles")) {
		Router.getTiles(req,out);
	} else if(req.getParameter("reqtype").equals("getZongScreen")) {
		Router.getZongScreen(req,out);
	} else if(req.getParameter("reqtype").equals("upgrade")) {
		Router.upgrade(req,out);
	}else if(req.getParameter("reqtype").equals("pausePlayer")) {
		Router.pausePlayer(req,out);
	} else if(req.getParameter("reqtype").equals("syncPlayer")) {
		Router.syncPlayer(req,out);
	}else if(req.getParameter("reqtype").equals("makePaypalReq")) {
		Router.makePaypalReq(req,out);
	} else if(req.getParameter("reqtype").equals("saveServer")) {
		Router.saveServer(req,out);
	}else if(req.getParameter("reqtype").equals("session")) {
		Router.session(req,out,false);
	} else if(req.getParameter("reqtype").equals("command")) {
		Router.command(req,out);
	} else if(req.getParameter("reqtype").equals("tileset")) {
		Router.growTileset(req,out);
	}else if(req.getParameter("reqtype").equals("username")) {
		Router.username(req,out);
	}else if(req.getParameter("reqtype").equals("growId")) {
		Router.growId(req,out);
	}else if(req.getParameter("reqtype").equals("createNewPlayer")) {
		Router.createNewPlayer(req,out);
	}else if(req.getParameter("reqtype").equals("generateCodes")) {
		Router.generateCodes(req,out);
	} else if(req.getParameter("reqtype").equals("deletePlayer")) {
		Router.deletePlayer(req,out);
	} else if(req.getParameter("reqtype").equals("restartServer")) {
		Router.restartServer(req,out);
	} else if(req.getParameter("reqtype").equals("sendTestEmail")) {
		Router.sendTestEmail(req,out);
	}  else	if(req.getParameter("reqtype").equals("compileProgram")) {
		System.out.println("Compiling now.");
		Router.compileProgram(req,out);
	} 
	else {
		out.println(status);
	}
	

	out.close();
}


	public void init() {
		// called instead of constructor by tomcat, use it also when not being called by tomcat.
		 Router = new Controllers(this);
		
		  
		       con =
		                     new UberConnection(
		                                 url,user, pass,this);
		      zongCon =
                   new UberConnection(
                               zongurl,user, pass,this);
   
	
	holdGod = new Thread(this);
	holdGod.start();
	
	}
	public GodGenerator() {
		if(!server)
		init();
		// turn on init when doing it via java application for testing,
		// but server app goes straight to init(), and runs main. So it calls
		// it twice. We only want to do it once, so we blank out init for server tests.
	}
	public GodGenerator(int gpid, String url, String user, String pass) {
		
		
		this.specurl=url;
		this.specuser=user;
		this.specpass=pass;
		try {
			 con = new UberConnection(url,user,pass,this);
	              
	 }
	 catch(NullPointerException exc) { exc.printStackTrace(); }
	 holdGod = new Thread(this);
		holdGod.start();
	}
	public static void main(String args[]) throws IOException {
		GodGenerator g = new GodGenerator();
		System.out.println("Maybe here?");
	}
	public void run() {
		GodGenerator God = this;
		System.out.println("I am on.");
		
		
	
	// replace with query. 
		try {
			
			UberStatement qstmt = con.createStatement();
			
		
		ResultSet rs = qstmt.executeQuery("select gameClock from God;");
		
		rs.next();
		gameClock = rs.getInt(1);
		rs.close();
		
		
		int i = 0;
		loadAchievements();

		status+="Loading players...\n";
		iteratorPlayers=loadPlayers();// we set em up!
		status+="Loading towns...\n";
		iteratorTowns = loadTowns();
		
		status+="Loading God...\n";
		int maxX=0;
		int maxY=0;

		 i = 0;
		while(i<getTowns().size()) {
			if(!getTowns().get(i).getPlayer().isQuest()&&Math.abs(getTowns().get(i).getX())>maxX) maxX = Math.abs(getTowns().get(i).getX());
			if(!getTowns().get(i).getPlayer().isQuest()&&Math.abs(getTowns().get(i).getY())>maxY) maxY = Math.abs(getTowns().get(i).getY());

			i++;
		}
		
		//Maelstrom = new Maelstrom(iteratorTowns.size(),maxX,maxY,this);
		Maelstrom = new Maelstrom(0,maxX,maxY,this);
		Trader = new Trader(this);
		
		status+="Loading Maelstrom and Trader...\n";
		serverLoaded=true;
		
		i=0;
		// at this point, we want to know whether or not we should load any new quests that
		// do not have players yet. We don't load/create these questplayers  in the test Gigabyte
		// servers because no player has joined them yet, so why waste the memory?
		ResultSet qs = qstmt.executeQuery("select qid,questcode,classname from Quest where qid = 0 and activated=true");
		UberStatement qstmt2 = con.createStatement();
		QuestListener q; Player p;
		while(qs.next()) {
			//	public Player createNewPlayer(String username, String password, int type, int tidToGive, String code) {

			p = createNewPlayer(qs.getString(3),"4p5v3sxQ",2,-1,"0000","nolinkedemail",true,0,0,false,0);
		
			q =  loadQuest(	p.ID,qs.getString(2),qs.getString(3));
			iteratorPlayers.add(q);
	
			qstmt2.executeUpdate("update Quest set qid = " + p.ID + " where classname = \"" + p.getUsername() + "\"");

		}
		qstmt2.close();
		
		qs.close(); 

		
		i=0;
		
	ArrayList<Player> players = God.getPlayers();
		while(i<players.size()) {
			p = players.get(i);
			
			
		
			qs =qstmt.executeQuery("select autorun from player where pid = " + p.ID);
			if(qs.next())
			if(qs.getBoolean(1)) p.getPs().b.runProgram();
			qs.close();
			i++;
		}
		
		Gigabyte = new MemoryLeakDetector(this);

		
		qstmt.close();
		i=0;
		SessionIdentifierGenerator forIt = new SessionIdentifierGenerator();
		String nextRand;

		int numIterators = (int) Math.round(((double) iteratorPlayers.size()+iteratorTowns.size())/10);
		
		while(i<(numIterators)&&i<maxIterators) {
			nextRand=forIt.nextSessionId();
			
			int j =0; boolean found = false;
			while(j<iterators.size()) {
				if(iterators.get(j).iterateID.equals(nextRand)) {
					found=true; break;
				}
				j++;
			}
			if(!found) {
			iterators.add(new Iterator(God,gameClock,nextRand));
			i++;
			}
		}
		i=0;
		loaded=true; Iterator it;	
		int lagCounterInc=0;
		lagTimer = new Timer();
		System.out.println("I am running...");
		for(;;) {
		//	try {
		//	System.out.println("beating."+gameClock);
				if(killGod) {
					 i =0;
					while(i<iterators.size()) {
						iterators.get(i).deleteMe();
						try {
							iterators.get(i).t.join(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						i++;
					}
					i = 0;
				
					
					
					break; }
				try {
				holdGod.sleep((int) Math.round(1000*gameClockFactor));
				} catch(InterruptedException exc) { break; }
				//System.gc();
				// before we increment the gameClock, let's check and see if 
				// the iterators have done their job in the alotted time.
				con.memoryLeakDetector();
				if(!lagTimer.isDone()&&lagCounterInc>=ticksTillIncrease) {
					// this means one of the Iterators didn't finish everything
					// in time to stop the lagTimer itself. So we stop it,
					// and clearly we need to increase the iterator size.
					// now we double the iterators so it scales proportionally.
					if(iterators.size()<getPlayers().size()&&iterators.size()<maxIterators) {
						int size = iterators.size();
						i=0;
						while(i<size) {
							nextRand=forIt.nextSessionId();
							
							int j =0; boolean found = false;
							while(j<iterators.size()) {
								if(iterators.get(j).iterateID.equals(nextRand)) {
									found=true; break;
								}
								j++;
							}
							if(!found) {
								// so we only add the iterator if the new ID is valid,
								// and we don't increment until it is!
							it = new Iterator(God,gameClock,nextRand);
							iterators.add(it);
							if(iterators.size()>getPlayers().size()||iterators.size()>maxIterators) break;
							i++;
							}
						}
					
						System.out.println("Increasing the iterators to "+iterators.size());

					}
					
					lagTimer.stopTimer();
					lagCounterInc=0;
					}else if(!lagTimer.isDone()&&lagCounterInc<ticksTillIncrease){
						// we give it another chance before we increase.
						lagTimer.stopTimer();
						lagCounterInc++; 
						if(printCounter==printWhenTicks)
						System.out.println("Increasing the lagCounter to " + lagCounterInc);

					}else if(lagTimer.isDone()&&lagCounterInc<=(-ticksTillDecrease)) {
						// clearly it's time we shrink this thing.
						// we shrink it by half.
						double size = 9*iterators.size()/10; // we go up by 100%, and down by 10%,
						// so that we'll naturally go up a lot and then find a happy medium.
						// But also the ticksTillDecrease is a hell of a lot higher, it takes
						// a good long run to lower the size by a bit.
						if(iterators.size()>minIterators) 
						while(iterators.size()>(int) Math.round(size)) {
							it = iterators.get(iterators.size()-1);
							iterators.remove(it);
							if(iterators.size()<=minIterators) break;
							it.deleteMe();
						}
						System.out.println("Shrinking the iterators to " + iterators.size());

						lagCounterInc=0; 
					} else if(lagTimer.isDone()&&lagCounterInc>(-ticksTillDecrease)) {
						if(printCounter==printWhenTicks)
						System.out.println("Decreasing the lagCounter to " + lagCounterInc);

						lagCounterInc--;
					}
				
				
					lagTimer = new Timer();
				 i = 0; 
				
			
				if(printCounter==printWhenTicks) printCounter=0;
				if(saveCounter==saveWhenTicks) saveCounter=0;

				saveCounter++;
				gameClock++;
				printCounter++;
			
		}
	} catch(SQLException exc) { exc.printStackTrace(); }
	 catch(OutOfMemoryError exc) { 
		 exc.printStackTrace();
		 killGod=true;
		 holdE=exc;
		 
	 }
		
	 /*
	  *

	  */
	/*
	 * Here is where we deal with the problem of making a new God.
	 */
	 
	 // con and rs are still open...
	 
	 if(killGod) {
		 UberStatement stmt;
		 try {
		  stmt = con.createStatement();
		stmt.executeUpdate("update God set errorlog = errorlog+'" + "';");

		stmt.close();

		con.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		status+=e.toString();
	}

	 }
	System.out.println("Exiting...");
	 
	}
	public void loadMapTiles() {
		try {
			mapTileHashes = new ArrayList<Hashtable>();
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from maptile;");
			Hashtable r;
			while(rs.next()) {
				r = new Hashtable();
				r.put("centerx",rs.getInt(3));
				r.put("centery",rs.getInt(4));
				r.put("mapName",rs.getString(2));
				r.put("mid",rs.getInt(1));
				
				mapTileHashes.add(r);
			}
			rs.close();
			stmt.close();
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public ArrayList<Hashtable> getMapTileHashes() {
		if(mapTileHashes==null) loadMapTiles();
		return mapTileHashes;
	}
	
	public Hashtable[] getAchievements() {
		return achievements;
		
	}
	public void loadAchievements() {
		try {
			UberStatement stmt = con.createStatement();
			UberStatement stmt2 = con.createStatement();
			UberStatement stmt3 = con.createStatement();

			ResultSet rs = stmt.executeQuery("select * from achievements;");
			ResultSet rs2,rs3;
			ArrayList<Hashtable> hold = new ArrayList<Hashtable>();
			Hashtable r;
			ArrayList<Integer> permissions;
			while(rs.next()) {
				r = new Hashtable();
				r.put("aid",rs.getInt(1));
				r.put("aname",rs.getString(2));
				r.put("agraphic",rs.getString(3));
				r.put("adesc",rs.getString(4));
				permissions = new ArrayList<Integer>();
				rs2 = stmt2.executeQuery("select questNum from apPermissions where aid = " + rs.getInt(1));
				while(rs2.next()) {
					rs3 = stmt3.executeQuery("select qid from Quest where questNum = " + rs2.getInt(1));
					if(rs3.next())
					permissions.add(rs3.getInt(1));
					rs3.close();
					
				}
				
				rs2.close();
				r.put("permissions",permissions);
				hold.add(r);
				
			}
			achievements = new Hashtable[hold.size()];
			int i = 0;
			while(i<hold.size()) {
				achievements[i]=hold.get(i);
				i++;
			}
			rs.close();
			stmt.close();
			stmt2.close();
			stmt3.close();
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
		
	}
	public void expandMap() {
			try {
			System.out.println("Expanding map...");
			UberStatement stmt = con.createStatement();
			Hashtable r; 
			// so it's always going to be a square root...
			int radius = (int) Math.sqrt(getMapTileHashes().size());
			// so let's start on the right.
			ArrayList<String> mapTileNames = new ArrayList<String>();
			ResultSet rs = stmt.executeQuery("select mapName from mapnames;");
			while(rs.next()) {
				
				mapTileNames.add(rs.getString(1));
			}
			rs.close();
			
			int centerx = -(radius)*mapTileWidthX;
			String name;
			while(centerx<=(radius)*mapTileWidthX) {
				int centery = (radius)*mapTileWidthY;
				while(centery>=-(radius)*mapTileWidthY) {
					if(Math.abs(centery)>(radius-1)*mapTileWidthY||Math.abs(centerx)>(radius-1)*mapTileWidthX) {
						// now we create...
						double rand = Math.random();
						int toGrab = (int) Math.round(rand*((double) mapTileNames.size()-1.0));
						name = mapTileNames.get(toGrab);
						System.out.println("Totally putting in tiles of name " + name);
						System.out.println("insert into maptile (mapName,centerx,centery) values ('" + name +"'," + centerx + "," + centery + ");");
						stmt.execute("insert into maptile (mapName,centerx,centery) values ('" + name +"'," + centerx + "," + centery + ");");
									//insert into maptile(mapName,centerx,centery) values ('worldmap.png',0,0);
						
					} 
					System.out.println(centerx+","+centery);

					centery-=mapTileWidthY;
				}
				centerx+=mapTileWidthX;
			}
			
			stmt.close();
			
			loadMapTiles();
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
		
	}
	public boolean needMoreIterators() {
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select internalClock from player where internalClock<" + gameClock + ";");
			if(rs.next()) {
					rs.close();
					stmt.close();
					return false;
			}
			rs.close();
			rs = stmt.executeQuery("select internalClock from town where internalClock<" + gameClock + ";");
			if(rs.next()) {
					rs.close();
					stmt.close();
					return false;
			}
			rs.close();
			rs = stmt.executeQuery("select internalClock from league where internalClock<" + (gameClock-leagueLagTime) + ";");
			if(rs.next()) {
					rs.close();
					stmt.close();
					return false;
			}
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return true;
	}
	public QuestListener loadQuest(int ID) {
		 String questcode=null, questname=null;
			try {
				UberStatement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("select questcode,classname from Quest where qid = " + ID);
				if(rs.next()) { questcode=rs.getString(1); questname = rs.getString(2); }
				rs.close();
				stmt.close();
			} catch(SQLException exc) { exc.printStackTrace();}
			if(questcode==null||questname==null) return null;
			else return loadQuest(ID,questcode,questname);
	}
	public QuestListener loadQuest(int ID, String questcode, String questname) {
		
	       	 String toWrite; ResultSet holdRevStuff; String oldRev; FileWriter fw;
	  		String makeItExist[]; Process proc; StreamGobbler outputGobbler,inputGobbler,
	  		errorGobbler; Timer j; BattlehardFunctions bf;
	  		File oldR, oldRJ; String total,holdParcel; 
	  	 boolean transacted=false;
  			
  			
                   toWrite = "";
         			if(!questcode.equals("Classfile")) {
         				// !!!! NOTE THIS COMPILES EVERY FUCKING TIME. YOU NEED TO CHANGE THIS FOR QUEST API FOR DEVELOPERS.
                  try {
  				
  			//	String[] oldRev = GodGenerator.returnStringArrayFromFile("/users/arkavon/documents/programs/workspace/BattlehardAIWars/src/userscripts/" + player.username + "/Revelations.java");
  			
  			 oldRev = questcode;
  			 // make it if it's not already there!
  			 fw = new FileWriter(PlayerScript.bhengsrcdirectory+"BHEngine/" + questname + ".java"); // we use bhengbindirectory
  			 // so they compile and make in same place, easy.

  			 	fw.write(oldRev);
  				fw.close(); } catch(IOException exc) { exc.printStackTrace(); System.out.println("IO Exception occured in loadQuests. Please contact support");
  				return null;}

  				try {
  					String toExec = "javac -cp " + PlayerScript.bhengbindirectory + ":" + PlayerScript.apachedirectory  +  "lib/servlet-api.jar"+
						" -d " + PlayerScript.bhengbindirectory + 
							" " + PlayerScript.bhengsrcdirectory +"BHEngine/"+ questname + ".java";
  					System.out.println("Executing " + toExec);
  				 proc = 	Runtime.getRuntime().exec(toExec);
  	             errorGobbler = new 
                  StreamGobbler(proc.getErrorStream(), "ERROR");     
  	             
  	             inputGobbler = new StreamGobbler(proc.getInputStream(),"INPUT");
              
              // any output?
               outputGobbler = new 
                  StreamGobbler(proc.getInputStream(), "OUTPUT");
              // kick them off
              errorGobbler.start();
              outputGobbler.start();
              inputGobbler.start();
               j= new Timer(7);
              while((!errorGobbler.isDone()||!outputGobbler.isDone())&&!j.isDone()) {
              	// This loop should play over and over until either j is done or outputgobbler and error gobbler are done.
              	// so we know the loop should not play if w = jdone + (outputgob*errorgob) 
              	// there for we should play it while not(w) is true which is !jdone*!(og*eg) = !jdone(!og + !eg)
              }
              
                                      
              // any error???
              int exitVal = proc.waitFor();
              toWrite = errorGobbler.returnRead()+"\n"+outputGobbler.returnRead()+"\n"+inputGobbler.returnRead()+"\nExitValue: "+exitVal;
              System.out.println(toWrite);
  				proc.destroy(); // to kill it off and release resources!
  				}
  					catch(IOException exc) { exc.printStackTrace(); System.out.println("IO Exception occured in Quest Making. Please contact support"); ; return null;}
  					catch(InterruptedException exc) { exc.printStackTrace(); System.out.println("Interrupted Exception occured in Quest Making. Please contact support"); ; return null; }
  				
  				 // after these two statements, no reference to the former currRev to bother us.
  				//		System.gc();
  				
  			//	 bf = new BattlehardFunctions(player.God,player,"4p5v3sxQ");
  			//	System.out.println("Here.");
  				 j = new Timer(7);
  				do {
  					makeItExist = GodGenerator.returnStringArrayFromFile(PlayerScript.bhengbindirectory +"BHEngine/"+ questname +".class");
  			//	makeItExist = GodGenerator.returnStringArrayFromFile( "/users/arkavon/documents/programs/workspace/BattlehardAIWars/bin2/"+"userscripts/"+ player.username+"/Revelations"+timeshit+".class");

  					// so it has time to compile, once it's not load again we know it exists.
  					// should only get out if the timer is 0 or make it exist does not equal load again(is 1)
  					// call load again 0, timer at 0 is 0, then !F = !t + m, so it should go while F, or while(!(!t + m))
  					// or while(t!m) goodie. j.isDone() is true if 0, so j.isDone() is actually !t. Sorry for confusion.
  				} while(!j.isDone()&&makeItExist[0].equals("load again"));
  				boolean toRet=true;
  				if(!makeItExist[0].equals("load again")) System.out.println("loaded " + questname + "'s program."); else {
  					System.out.println("Nope, " +questname + ".");
  					toRet=false;
  					}

         			} else {
         				makeItExist = new String[1]; // to get past the if down there.
         				makeItExist[0] = "loaded";
         			}
         			
  				if(!makeItExist[0].equals("load again"))
  				try {
  					// NOTE WE DO NOT USE URLCLASSLOADER BECAUSE WE ONLY LOAD THESE
  					// CLASSES ONCE, NOT DYNAMICALLY, SO WE CAN USE THE MAIN CLASSLOADER,
  					// ALSO MAIN CLASSLOADER HAS THE REQUIRED SERVLET CLASSES LOADED!!!
  					Class<QuestListener> currRev=null;
  					currRev = (Class<QuestListener>) Class.forName("BHEngine." + questname);
  				
  					Constructor newSCons = currRev.getConstructors()[0];
  							
  						
  					
  					QuestListener currRevInstance = (QuestListener) newSCons.newInstance(ID,this);
  				// oldR = new File(bindirectory +"userscripts/"+ player.username.toLowerCase()+"/Revelations.class");
  	  			//	oldRJ = new File(PlayerScript.bhengbindirectory+"BHEngine/"+questcode+".class");
  			//	oldR = new File(PlayerScript.bhengbindirectory+"BHEngine/"+questcode+".java");
  			//	oldR.delete(); 
  			//	oldRJ.delete();
 				 return currRevInstance;

  			//	timeshit++;
  				} 
  				catch(ClassNotFoundException exc) { exc.printStackTrace(); System.out.println("ClassNotFoundException occured in Gigabyte. Please contact support");  return null; } catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} /*catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/ catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
  			
  				
            
           return null;
	
	}
	public int getGameClock() {
		return gameClock;
	}
	public ArrayList<Player> getIteratorPlayers() {
		return iteratorPlayers;
	}
	public ArrayList<Town> getIteratorTowns() {
		return iteratorTowns;
	}

	public static void loadPlayerScript_Deprecated(Player p) {
		
		// this'll load the playerscript object for the player.
		// This is kind of roundabout and I can't remember why I did it,
		// and we're not using it anymore, but here it is just in case!
		try {
			String holdOriginal[];
			do {
			 holdOriginal = GodGenerator.returnStringArrayFromFile(srcdirectory + "BHEngine/PlayerScript.java");
			} while(holdOriginal[0].equals("load again"));
			 // in case another program is using it..
		FileWriter fw = new FileWriter(srcdirectory + "pshf/PlayerScript" + p.getUsername() + ".java");
		// and so here we have it on the brink of hell where the dark lurker waits...
		
		int jes = 0;
		while(jes<holdOriginal.length) {
	//		System.out.println(holdOriginal[jes]);
			if(holdOriginal[jes].equals("package BHEngine;")) fw.write("package pshf; import BHEngine.*;\n");
			else if(holdOriginal[jes].contains("public class PlayerScript implements Runnable {")) fw.write("public class PlayerScript" + p.getUsername() + " implements Runnable {\n");
			else if(holdOriginal[jes].contains("public PlayerScript(Player player) {")) fw.write("	public PlayerScript" + p.getUsername() + "(Player player) {\n");
			else if(holdOriginal[jes].contains("PlayerScript newOne = new PlayerScript(player);")) fw.write("PlayerScript" + p.getUsername() + " newOne = new PlayerScript" + p.getUsername() + "(player);\n");
			else
					fw.write(holdOriginal[jes]+"\n");
			
			jes++;
		}
		fw.close();
		
		} catch(IOException exc) {
			System.out.println("Idioso.");
		}

		// So we got that shit. Now we need to compile it and run it.
		// How do we do this? Well, we've re-written it. Now we need to compile it.
		try {
		Runtime.getRuntime().exec("javac -cp "+bindirectory +
				" -d "+bindirectory + 
				" " + srcdirectory + "pshf/PlayerScript"+p.getUsername()+".java");
		/*
		 * "javac -cp /users/arkavon/documents/programs/workspace/battlehardaiwars/bin/ -d /users/arkavon/documents/programs/workspace/battlehardaiwars/bin/ /users/arkavon/documents/programs/workspace/BattlehardAIWars/src/BattlehardFunctions/PlayerScript.java"
		 */
	}
		catch(IOException exc) { System.out.println("God just really hates you today!"); }


	try {
		
		String makeItExist[];
		do {
			makeItExist = returnStringArrayFromFile(bindirectory + "pshf/PlayerScript"+p.getUsername()+".class");
			// so it has time to compile, once it's not load again we know it exists. 
			
		} while(makeItExist[0].equals("load again"));
		URL[] ue = new URL[1];
		try{
		ue[0] = new URL("file:"+bindirectory);
		} catch(MalformedURLException exc) { }
		URLClassLoader urlload = new URLClassLoader(ue);
		
		Class<?> newScript = Class.forName("pshf.PlayerScript"+p.getUsername(),false,urlload);
		Constructor newSCons[] = newScript.getConstructors();
		Object pl = newSCons[0].newInstance(p);

		// now to delete because when we don't, the old class file doesn't get replaced. Don't know why, but I make it work.
		File toDel = new File(bindirectory+"pshf/PlayerScript"+p.getUsername()+".class");
		File toDel2 = new File(srcdirectory+"pshf/PlayerScript"+p.getUsername()+".java");
		toDel.delete(); toDel2.delete();
	
		
	} //catch(NoSuchMethodException exc) { System.out.println("No class constructor."); }
	catch(ClassNotFoundException exc) { System.out.println("Class not found."); }
	catch(InvocationTargetException exc) { System.out.println("Bad target."); }
catch(IllegalAccessException exc) { System.out.println("Illegal access.");} 
	catch(InstantiationException exc) { System.out.println("Nobody likes you."); }
	}
	
	
	public static String[] semicolonSeparate(String[] toSep) {
		// What does this method do?
		// Makes a new array with each entry being a separate statements, statements being identified by ;'s.
		
		// First get the number of semicolons and brackets. Make brackets need an escape character too.
		// This will be the first recorded difference.
		
		int semi = 0;
		// Really the only thing that requires an escape character is ".
		
		// To mimic this ability, then, this code needs to take into account whether or not it is inside a "" at the time.
		
		int k = 0;
		String toParse;
		String prevSemi; String testSemi;
		while(k<toSep.length) {
			int j = 0;
			
			 toParse = toSep[k];
			 prevSemi="";
			boolean openP = false;
			while(j<toParse.length()) {
				 testSemi = toParse.substring(j,j+1);
				if(testSemi.equals("\"")&&!prevSemi.equals("\\")&&!openP) openP = true; 
				else if(testSemi.equals("\"")&&!prevSemi.equals("\\")&&openP) openP = false;
				if((testSemi.equals(";")||testSemi.equals("{")||testSemi.equals("}"))&&!openP) semi++; // means these guys aren't in a string.
				prevSemi=testSemi;
				j++;
				
			}
			k++; // what about {}? I believe program can handle them - check and find out.
		}
		if(semi>0) {
	String toReplace[] = new String[semi];
	semi=0;
	 k = 0;
	while(k<toSep.length) {
		int j = 0;
		
		 toParse = toSep[k];
		 prevSemi="";
		boolean openP = false;
	
		while(j<toParse.length()) {
			 testSemi = toParse.substring(j,j+1);
			if(testSemi.equals("\"")&&!prevSemi.equals("\\")&&!openP) openP = true; 
			else if(testSemi.equals("\"")&&!prevSemi.equals("\\")&&openP) openP = false;
			if((testSemi.equals(";")||testSemi.equals("{")||testSemi.equals("}"))&&!openP){
				toReplace[semi] = toParse.substring(0,j+1);
				
				toParse=toParse.substring(j+1,toParse.length());
				j=-1;
				semi++;
			}
			
			prevSemi = testSemi;
			j++;
			
		}
		k++;
	}
	
	return toReplace;
		} else {
			String sendBack[] = new String[1];
			sendBack[0] = "0";
			return sendBack;
		}
	
	
	}

	public int getPlayerSize() {
		int size=0;
		/*
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select count(pid) from player;");
			if(rs.next()) size=rs.getInt(1);
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace();} 
		return size;*/
		return getPlayers().size();
	}
	public static String[] returnStringArrayFromFile(String filename)  {
		try {
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);

		String take;
		int ICounter = 0;
		do {
		take = br.readLine();
		ICounter++;
		} while(take!=null);

		fr.close();
		 fr = new FileReader(filename);
		 if(ICounter>0) {
		 br = new BufferedReader(fr);
		String holdText[] = new String[ICounter-1];
		ICounter = 0;
		do {
			try {
		holdText[ICounter] = br.readLine(); } catch(ArrayIndexOutOfBoundsException exc) {
			// this means something very crappy happened. I'm still not sure what but the best thing to do
			// is to deal with it.
			  holdText = new String[1];
			 holdText[0] = "load again";
			 return holdText;
			
		}
		
		if(holdText[ICounter]==null) {
			  holdText = new String[1];
				 holdText[0] = "load again";
				 return holdText;
				
		}
		//if(holdText[ICounter].equals("") || holdText[ICounter].equals(" ")) ICounter--;
		// Above is safety constraint to make sure no blank spaces get saved. 
		ICounter++;
		} while(ICounter<holdText.length);
		return holdText; }
		 else {
			 String holdText[] = new String[1];
			 holdText[0] = "load again";
		 }
		
		} catch(IOException exc) {
			 String holdText[] = new String[1];
			 holdText[0] = "load again";
			 return holdText;
			//System.out.println("Hello. Error.");
		}
		
		return new String[4];

		}
	
	public int returnNumUniqueTowns(int originalTID) {
		/*
		 * Returns the number of unique towns with support units
		 * from this player. Is non-static in that it references
		 * players held by God.
		 */
		
		int i = 0;
		Town thisT = findTown(originalTID);
		int numUniques=0;
		Player p = thisT.getPlayer();
		ArrayList<Town> towns = getTowns(); Town t; ArrayList<AttackUnit> au; AttackUnit a;
		while(i<towns.size()) {
			t = towns.get(i);
			au = t.getAu();
			int j = 0;
			while(j<au.size()) {
				a = au.get(j);
				if(a.getSupport()>0&&a.getOriginalPlayer().ID==p.ID&&a.getOriginalTID()==originalTID) {
					numUniques++;
					break;
				}
				j++;
			}
			
			i++;
		}
		return numUniques;
		/*
		int numUniques=0;
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select count(distinct tid) from supportAU where ftid = " + originalTID);
			if(rs.next()) numUniques = rs.getInt(1);
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return numUniques;*/
	}

	public  Town findTown(String townName, Player p) {
		int i = 0;
		ArrayList<Town> towns = p.towns();
		Town t;
		while(i<towns.size()) {
			t = towns.get(i);
			if(t.getTownName().equals(townName)) return t;
			i++;
		}
		return new Town(0,this);
/*
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select tid from town where townName = '" + townName + "'");
			int tid = 0;
			if(rs.next()) tid = rs.getInt(1);
			
			rs.close();
			stmt.close();
			return new Town(tid,this);
		} catch(SQLException exc) { 
			exc.printStackTrace();
		}
		
		return new Town(0,this);*/
	}
	public static String secondsToString(long time){
	    int seconds = (int)(time % 60);
	    int minutes = (int)((time/60) % 60);
	    int hours = (int)((time/3600) % 24);
	    int days = (int) (time / 86400);
	    String secondsStr = (seconds<10 ? "0" : "")+ seconds;
	    String minutesStr = (minutes<10 ? "0" : "")+ minutes;
	    String hoursStr = (hours<10 ? "0" : "")+ hours;
	    String daysStr = (days<10 ? "0" : "") + days;
	    return new String(daysStr + ":" + hoursStr + ":" + minutesStr + ":" + secondsStr);
	  }
	public Town findTown(int tid) {
		
		

		int i = 0;
		Town t;
		while(i<getTowns().size()) {
			t=getTowns().get(i);
			if(t.townID==tid) return t;
			i++;
		}
		return null;
	}

public Town findTown(int x, int y) {
		
		// Finds the i for the specific town you're looking for.
		// returns Testtown if the town doesn't exist.
		// Zeppelins will only ever be near each other momentarily...then they are destroyed...
		// Since you can't overlap your own and you battle other people's when you meet them.
		int i = 0; ArrayList<Town> townsThere = new ArrayList<Town>();
		while(i<getTowns().size()) {
			if(getTowns().get(i).getX()==x&&getTowns().get(i).getY()==y) townsThere.add( getTowns().get(i));
			i++;
		}
		
		i = 0;
		while(i<townsThere.size()) {
			if(!townsThere.get(i).isZeppelin()) return townsThere.get(i);
			i++;
		}
		// hmm.. we haven't returned a town yet? Then maybe we did find something, but it's a 
		// Zeppelin, not a town!
		
		if(townsThere.size()>0) return townsThere.get(0);
		// It's not? Return the old dog.
		return new Town(0,this);
		/*
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select tid from town where x = "  + x + " and y = " + y);
			int tid = 0;
			if(rs.next()) tid = rs.getInt(1);
			
			rs.close();
			stmt.close();
			return new Town(tid,this);
		} catch(SQLException exc) { 
			exc.printStackTrace();
		}
		
		return new Town(0,this);*/
	}
public Town findZeppelin(int x, int y) {
	
	// Finds the i for the specific town you're looking for.
	// returns Testtown if the town doesn't exist.
	// Zeppelins will only ever be near each other momentarily...then they are destroyed...
	// Since you can't overlap your own and you battle other people's when you meet them.
	int i = 0; ArrayList<Town> townsThere = new ArrayList<Town>();
	while(i<getTowns().size()) {
		if(getTowns().get(i).getX()==x&&getTowns().get(i).getY()==y) townsThere.add( getTowns().get(i));
		i++;
	}
	
	i = 0;
	while(i<townsThere.size()) {
		if(townsThere.get(i).isZeppelin()) return townsThere.get(i);
		i++;
	}
	
	return new Town(0,this);
	/*
	try {
		UberStatement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select tid from town where x = "  + x + " and y = " + y);
		int tid = 0;
		if(rs.next()) tid = rs.getInt(1);
		
		rs.close();
		stmt.close();
		return new Town(tid,this);
	} catch(SQLException exc) { 
		exc.printStackTrace();
	}
	
	return new Town(0,this);*/
}
public ArrayList<Town> findZeppelins(int x, int y) { // returns all zeppelins at a location.
	
	// Finds the i for the specific town you're looking for.
	// returns Testtown if the town doesn't exist.
	// Zeppelins will only ever be near each other momentarily...then they are destroyed...
	// Since you can't overlap your own and you battle other people's when you meet them.
	int i = 0; ArrayList<Town> townsThere = new ArrayList<Town>();
	while(i<getTowns().size()) {
		if(getTowns().get(i).getX()==x&&getTowns().get(i).getY()==y) townsThere.add( getTowns().get(i));
		i++;
	}
	
	i = 0;
	while(i<townsThere.size()) {
		if(!townsThere.get(i).isZeppelin()){
			townsThere.remove(i);
			i--;
		}
		i++;
	}
	
	return townsThere;

}



	public String getUsername(int pid) {
		int i = 0;
		ArrayList<Player> players = getPlayers();
		Player p;
		while(i<players.size()) {
			p = players.get(i);
			if(p.ID==pid) return p.getUsername();
			i++;
		}
		return null;
	//	return (new Player(pid,this)).getUsername();
	}
	public  static double getAverageLevel(Town t) {
		
		ArrayList<Building> bldg = t.bldg();
		int i = 0;
		double numCounted=0;
		double lvl=0;
		while(i<bldg.size()) {
			if(((t.getPlayer().isLeague()||t.isZeppelin())&&!bldg.get(i).getType().equals("Metal Mine")
					&&!bldg.get(i).getType().equals("Timber Field")&&!bldg.get(i).getType().equals("Manufactured Materials Plant")
					&&!bldg.get(i).getType().equals("Food Farm"))||(!t.getPlayer().isLeague()&&!t.isZeppelin())){
				
				lvl+=((double) bldg.get(i).getLvl());
				numCounted++;
			}
			i++;
		}
		lvl/=numCounted;
		return lvl;
		/*
		double avgLevel=0;
		try {
			UberStatement stmt = t.getPlayer().con.createStatement();
			ResultSet rs = stmt.executeQuery("select avg(lvl) from bldg where tid = " + t.townID);
			if(rs.next()){
				 avgLevel=rs.getInt(1);
				
			}
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return avgLevel;
		*/
	}
	public static double[] returnModifiedConcealments(Raid holdAttack) {
		String combatData = ""; String combatHeader = "";
		int k = 0;
		int currentArmySize = 0;
		int currentExpAdvSizeOff = 0; boolean found = false;
		int currentArmySizePop=0;
		int currentExpAdvSizeOffWithDivMods =0; // So this is the expmod but with
		// factors included for concealment like 2x on tank expmods so tanks
		// are 2x as hard to hide as their 10 soldier equiv(seen as 20 soldiers.)
		// This way they get to account for the fact that they get 2x the speed.
		AttackUnit au;
		do {
			 au = holdAttack.getAu().get(k);
			 switch(au.getPopSize()) {
				case 1:
					currentExpAdvSizeOffWithDivMods+=au.getExpmod()*au.getSize();
					break;
				case 5:
					currentExpAdvSizeOffWithDivMods+=au.getExpmod()*au.getSize()*2;
					break;
				case 10:
					currentExpAdvSizeOffWithDivMods+=au.getExpmod()*au.getSize()*4;
					break;
				case 20:
					currentExpAdvSizeOffWithDivMods+=(int) Math.round(au.getExpmod()*au.getSize()*.5);
					break;
		
				}
			currentArmySize+=au.getSize();
			
			currentArmySizePop+=au.getSize()*au.getPopSize();
			currentExpAdvSizeOff+=au.getSize()*au.getExpmod();
			k++;
		} while(k<holdAttack.getAu().size());
		
		// Found the highest concealment unit.
		
		// Now I need to add up all the other units concealment times their ratios to the highest one.
		//IF YOU CHANGE THIS CONCEALMENT CODE, CHANGE SCOUTS ALSO
		k = 0;
		double sigmaTerm = 0; double moddedhi = 0;
	
		do {
			 au = holdAttack.getAu().get(k);
				double moddedother=0;
				switch(au.getPopSize()) {
				case 1:
					moddedother=au.getConcealment();
					break;
				case 5:
					moddedother=au.getConcealment()/2;
					break;
				case 10:
					moddedother=au.getConcealment()/4;
					break;
				case 20:
					moddedother=au.getConcealment()/.5;
					break;
				}
				if(currentArmySize>0)
				sigmaTerm+=((moddedother)*au.getSize()/(currentArmySize));
			
			k++;
		} while(k<holdAttack.getAu().size());
		/*if(found)
		 sigmaTerm = moddedhi-sigmaTerm;
		else sigmaTerm=0;*/
		 if(sigmaTerm<0) sigmaTerm=0;
		// Now I need the exponential term, which includes the sigma term a little.
		// System.out.println("current population of army incoming is " + currentArmySizePop);
			//double expTerm1 = (sigmaTerm*holdAttack.town1.getStealth()*10)/currentArmySize;
			// it takes twice as much concealment to launch a good offensive attack than a defensive one.
			// Can be fixed later but would be a good rule of thumb: Twice the concealment for offense.
			// Not what I'd prefer, but it basically means that the army you can attack with at a certain concealment rating on a town
			// of similar population is half the army you can defend with and get the same rating. It also means
			// attacking armies that are large relative to the village size will suffer for it, leading to small armies against
			// small villages, large armies against large villages, and a need to reconnoiter everything. Cool!
			// Bunker system alleviates the low concealment problem - there is a defensive advantage to having
			// those.
			
			//IF YOU CHANGE THIS CONCEALMENT CODE, CHANGE SCOUTS ALSO

			 k = 0;
			int currentExpAdvSizeDef = 0; found = false;
			int currentArmySizedef = 0;
			int currentArmySizeDefPop=0;
			int currentExpAdvSizeDefWithDivMods=0;
			do {
				
				 au = holdAttack.getTown2().getAu().get(k);
				 switch(au.getPopSize()) {
					case 1:
						currentExpAdvSizeDefWithDivMods+=au.getExpmod()*au.getSize();
						break;
					case 5:
						currentExpAdvSizeDefWithDivMods+=au.getExpmod()*au.getSize()*2;
						break;
					case 10:
						currentExpAdvSizeDefWithDivMods+=au.getExpmod()*au.getSize()*4;
						break;
					case 20:
						currentExpAdvSizeDefWithDivMods+=(int) Math.round(au.getExpmod()*au.getSize()*.5);
						break;
			
					}
				currentArmySizedef+=au.getSize();
				currentExpAdvSizeDef+=au.getSize()*au.getExpmod();
				currentArmySizeDefPop+=au.getSize()*au.getPopSize();
				k++;
			} while(k<holdAttack.getTown2().getAu().size());
			// at this point we know currentArmySizedef. So we put the if UberStatement here for genocide.
			
	//		System.out.println("currentExpAdvSizeDef is " + currentExpAdvSizeDef + " currentExpAdvSizeOff is " + currentExpAdvSizeOff);
			combatData+=("currentExpAdvSizeDef is " + currentExpAdvSizeDef + " currentExpAdvSizeOff is " + currentExpAdvSizeOff);

			
			
			int numUnitsRemainingD=0;
			boolean resFlag=true; // for standoffs, which are rare under new regime.
			//IF YOU CHANGE THIS CONCEALMENT CODE, CHANGE SCOUTS ALSO

			
		
			// Found the highest concealment unit.
			
			// Now I need to add up all the other units concealment times their ratios to the highest one.
			
			k = 0;
			moddedhi = 0;
		
			double sigmaTermdef = 0;
			do {
				 au = holdAttack.getTown2().getAu().get(k);
					double moddedother=0;
					switch(au.getPopSize()) {
					case 1:
						moddedother=au.getConcealment();
						break;
					case 5:
						moddedother=au.getConcealment()/2;
						break;
					case 10:
						moddedother=au.getConcealment()/4;
						break;
					case 20:
						moddedother=au.getConcealment()/.5;
						break;
					}
					if(currentArmySizedef>0) 
					sigmaTermdef+=((moddedother)*au.getSize()/(currentArmySizedef));
				
				
				k++;
			} while(k<holdAttack.getTown2().getAu().size());
			
			 if(sigmaTermdef<0) sigmaTermdef=0;
//			 System.out.println("sigmaTermDef" + sigmaTermdef);
				//IF YOU CHANGE THIS CONCEALMENT CODE, CHANGE SCOUTS ALSO

			// Now I need the exponential term, which includes the sigma term a little.
		
			// System.out.println("current population of army in town is " + currentArmySizeDefPop);
			 // used to use currentArmySizeDef in expTerm calcs, but this gives bigger units more concealment.
			double expTerm2 = Math.exp(-(currentExpAdvSizeDefWithDivMods+1)/((sigmaTermdef+1)*holdAttack.getTown2().getStealth()));
			double expTerm1 = Math.exp(-(currentExpAdvSizeOffWithDivMods+1)/((sigmaTerm+1)*holdAttack.getTown1().getStealth()*(holdAttack.getTown2().getPop()+1)/2));

			// double expTerm2 = (sigmaTermdef*holdAttack.town2.getStealth()*holdAttack.town2.getPop())/currentArmySizedef;
			//IF YOU CHANGE THIS CONCEALMENT CODE, CHANGE SCOUTS ALSO

		
		// Now put it all together for pre-combat concealment!
		//	System.out.println(sigmaTermdef + " is sigmaTermDef, expTerm2 is " + expTerm2 + " expTerm 1 is" +expTerm1);
			double Ca = (sigmaTerm+1)*expTerm1+1;
			double Cd = (sigmaTermdef+1)*expTerm2+1;
			
			double toret[] = new double[2];
			toret[0]=sigmaTerm+1;
			toret[1]=sigmaTermdef+1;
			return toret;
	}

	public Player createNewPlayer(String username, String password, int type, int tidToGive, String code,String email, boolean skipMe, int chosenTileX,int chosenTileY,boolean okayToMakeNewAccount, long fuid) {
		/*
		 * This method creates a new player by giving them
		 * a player object, kickstarting it, and then giving
		 * them a town from Id.
		 * 
		 * Note that if you want to change starting stats
		 * or what is placed on the player, you need to adjust
		 * both the executeUpdate SQL statements as well as the hardwired
		 * data structure segments down below.
		 * 
		 */
		 
		int i = 0;
		ArrayList<Player> players = getPlayers();
		System.out.println("Playersize " + players.size());
		while(i<players.size()) {
			if(players.get(i).getUsername().equals(username)) {
			//	System.out.println("username already taken.");
				return null; // player username already exists! 
			}
			i++;
		}
		
		if(okayToMakeNewAccount&&accounts.get(username)!=null) return null; // If it's a genuinely new player request,
		// then okayToMakeNewAccount is going to be true. If it is say an old player returning with no account, then it won't be okay
		// to make a new account and we won't even check that it isn't there, he'll just...get a new player.
		
		
		
		if(username.contains(".")) {
		//	System.out.println("No periods, "+ username );
			return null;
		}
		if(username.contains(" ")) {
		//	System.out.println("No spaces.");

			return null;
		}

		try {
			boolean transacted=false;
		/*	Cone   con =
                   DriverManager.getConnection(
                               url,user, pass);*/
			while(!transacted) {
				
			try {
			UberStatement stmt = con.createStatement(); ResultSet rs;
			
			
			/*ResultSet rs = stmt.executeQuery("select pid from player where username = \"" + username + "\";");
			if(rs.next()) {
				int pid = rs.getInt(1);
				rs.close();
				
				stmt.execute("delete from attackunit where pid = " +pid);
				stmt.execute("delete from autemplate where pid = " +pid);
				stmt.execute("update town set pid = 5 where pid = " +pid);
				stmt.execute("delete from player where pid = " +pid);



			} else
			rs.close();*/
			
			stmt.executeUpdate("start transaction;");
			if(okayToMakeNewAccount) {
				
				stmt.execute("insert into users(fuid,username,password,email) values ("+fuid+",\""+username+"\",md5(\""+password+"\"),\""+email+"\");");
				rs = stmt.executeQuery("select uid,password,registration_date from users where username = '"+username+"';");
				rs.next();
				Hashtable r = new Hashtable();
				  r.put("uid",rs.getInt(1));
		    	   r.put("fuid",fuid);
		    	   r.put("username",username);
		    	   r.put("password",rs.getString(2));
		    	   r.put("registration_date",rs.getTimestamp(3));
		    	   r.put("email",email);
		    	 accounts.put(username,r);
		    	 if(fuid!=0) accounts.put(fuid,r);
		    	   rs.close();
				
			}
			int numPlayers = 0;
			rs = stmt.executeQuery("select count(*) from player where username = '" + username+"'");
			if(rs.next()) numPlayers=rs.getInt(1);
			rs.close();
			
			if(numPlayers==0) {
				if(okayToMakeNewAccount)
			stmt.executeUpdate("insert into player (username,password,stealth,knowledge,totalscho,totalmess,totalpop,alotTech,soldTech,tankTech,juggerTech,weaponTech,buildingSlotTech,instructions,outputchannel,poutputchannel,civWeap,bomberTech,suppTech,townTech,bunkerTech,tradeTech,lotTech,accessCode,afTech,email,fuid) " +
					"values (\"" + username + "\",md5(\"" + password + "\"),1,0,0,0,1,1,1,1,1,\"1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,\",2,\"null\",\"null\",\"null\",0,1,1,1,1,1,8,'" + code + "',1,'"+email+"',"+fuid+")");
			//		"values (\"" + username + "\",\"" + password + "\",3,0,0,0,1,5,1,1,1,\"1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,\",3,\"null\",\"null\",\"null\",0,1,3,2,3,1,18,'" + code + "',3)");
			
				else  // the not okay to make new account version got it's password from an already encrypted password in users!
					stmt.executeUpdate("insert into player (username,password,stealth,knowledge,totalscho,totalmess,totalpop,alotTech,soldTech,tankTech,juggerTech,weaponTech,buildingSlotTech,instructions,outputchannel,poutputchannel,civWeap,bomberTech,suppTech,townTech,bunkerTech,tradeTech,lotTech,accessCode,afTech,email,fuid) " +
							"values (\"" + username + "\",\"" + password + "\",1,0,0,0,1,1,1,1,1,\"1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,\",2,\"null\",\"null\",\"null\",0,1,1,1,1,1,8,'" + code + "',1,'"+email+"',"+fuid+")");
			
			}
					// once the player is made, then we move on.
			 rs = stmt.executeQuery("select pid from player where username = \"" + username + "\"");
			rs.next();
			int pid = rs.getInt(1);		//	System.out.println("pid is " + pid);

			stmt.execute(" insert into revelations (pid,revAI) values (" + pid + ",\"\")");
			
			Runtime.getRuntime().exec("mkdir " + PlayerScript.getSrcDirectory()  + "userscripts/" + username);
			Runtime.getRuntime().exec("mkdir " + PlayerScript.getBinDirectory()  + "userscripts/" + username);

			rs.close();
			int numAttackUnits = 0;
			rs = stmt.executeQuery("select count(*) from attackunit where pid = " + pid);
			if(rs.next()) numAttackUnits=rs.getInt(1);
			rs.close();
			
			if(numAttackUnits==0) {
			 i = 1;			
			 
			 stmt.execute("insert into attackunit (name,pid,slot,conc,armor,cargo,speed,type,graphic) values ('empty'," + pid + "," + 0 + ",0,0,0,0,5,0);");

			while(i<6) {
				stmt.execute("insert into attackunit (name,pid,slot,conc,armor,cargo,speed,type,graphic) values ('locked'," + pid + "," + i + ",0,0,0,0,5,0);");

				i++;
			}
			
			}
			Player p =null;
	    	if(type==0){
				 p = new Player(pid,this);
																			//int stabilityTech, int scholTicks, int brkthrus, int brkups, int lotTech, int engTech,int scholTech
			// p = new Player(pid,username,tlist,0,0,1,0,3,this,5,true,true,true,true,weap,3,0,3,2,au,3,AUTemplates,soldierPicTech,tankPicTech,juggerPicTech,bomberPicTech,1,password,false,1,0,0,0,18,1,1,3,gameClock,-1,1);
			 }else if(type==1){
	    		//	public void createLeague(String leagueName,String leagueLetters, Player initial) {
					 p = new League(pid,this);
					 ((League) p).setLeagueInternalClock(gameClock);

	    	}else {
				 p = new Player(pid,this);

	    		// we make, but do not add the player, this is a QuestListener that we're loading later, differently.
	    		// This whole method just creates a player object that is thrown to the wind once we're done if this is a quest!
				 }

			// now load up an AU array to give the false town.
			
			i = 0;
			//Town t = new Town(0,this);
			int weapons[] = new int[0];
			//	t.addUnitType(new AttackUnit("empty",0,0,0,0,i,0,weapons,0));
				p.setAu(new AttackUnit("empty",0,0,0,0,i,0,weapons,0)); 
			i=1;
			while(i<6) {

				//	public AttackUnit(String name, double conc, double armor, double cargo, double speed, int slot, int popSize, int weap[]) {

			
			//	t.addUnitType(new AttackUnit("locked",0,0,0,0,i,0,weapons,0));
				p.setAu(new AttackUnit("locked",0,0,0,0,i,0,weapons,0)); // for the player.
				i++;
			}
			
					boolean weap[] = new boolean[21];
			i = 1;
			while(i<21) {
				weap[i]=false; 
				i++;
			}
			weap[0]=true;
			
		    boolean soldierPicTech[] = new boolean[10];
		    
		    i = 0;
		    while(i<soldierPicTech.length) {
		    	soldierPicTech[i]=true;
		    	i++;
		    }
		    boolean tankPicTech[] = new boolean[10];
		    i = 0;
		    while(i<tankPicTech.length) {
		    	tankPicTech[i]=true;
		    	i++;
		    }
		    boolean juggerPicTech[] = new boolean[10];
		    i = 0;
		    while(i<juggerPicTech.length) {
		    	juggerPicTech[i]=true;
		    	i++;
		    }
		    boolean bomberPicTech[] = new boolean[5];
		    i = 0;
		    while(i<bomberPicTech.length) {
		    	bomberPicTech[i]=true;
		    	i++;
		    }		    
		
	    	
	    	p.setBomberPicTech(bomberPicTech);
	    	p.setTankPicTech(tankPicTech);
	    	p.setSoldierPicTech(soldierPicTech);
	    	p.setJuggerPicTech(juggerPicTech);
	    //	p.setWeap(weap);
			
	    	p.setRevTimer((int) Math.round(( (double) 52*7*24*3600)/((double) GodGenerator.gameClockFactor)));
	    	p.setBp(p.getBp()+100);
	    	p.setKnowledge(p.getKnowledge()+100);
			// now that we've loaded'er up, we can
			// wait to activate the player until after the giveNewCity
			// mabob has been called.
			
			stmt.executeUpdate("commit;");
			if(type==1) {
			if(!giveNewTown(p,tidToGive,type,skipMe,chosenTileX,chosenTileY)) return null; // means no room left for new players.
			// bad because the player still exists but just has no town and hasn't started yet.
			// Need to write a method that gives a town without any trouble if they reach
			// the cap.
			} else {
			if(!giveNewTown(p,-1,type,skipMe,chosenTileX,chosenTileY)) return null; } // means no room left for new players.

			//p.towns.remove(0); // get rid of the original town! Not worth it...
			
		
			if(type==0||type==1) {
				players.add(p);
			}

			
			transacted=true; 
			PlayerScript ps = p.getPs();
			//	public boolean createUnitTemplate(String unitName, int tierNumber, int concealment,int armor, int cargo, int speed, int weaponsArray[], int graphicNum) {
			ps.b.setCapitalCity(p.towns().get(0).townID);
			stmt.executeUpdate("update player set capitaltid = " + p.getCapitaltid() + " where pid = " + p.ID);
			if(type==0||type==1) 		{
				// ANYTHING YOU WANT TO LOAD FOR PLAYERS/LEAGUES, PUT IT HERE...BECAUSE IF IT REQUIRES HAVING A TOWN WITH A PLAYER REFERENCE,
				// AND IT'S A QUEST PLAYER, THEN THAT STATEMENT WILL FAIL WHEN YOU TRY TO MAKE QUESTS!
				int k = 1;
				while(k<=10) {
					p.getPs().b.joinQuest("AQ"+k);
					k++;
				}
				if(fuid==0)
				p.getPs().b.joinQuest("ConnectWithFacebook");
				p.getPs().b.joinQuest("BQ1"); // must be after capitalcity and after cwf
				int weapArrayNew[] = new int[2]; weapArrayNew[0]=0; weapArrayNew[1]=0;
				p.setKnowledge(p.getKnowledge()+soldierTechPrice);
				String toRes[] = {"ShockTrooper"};
				ps.b.completeResearches(toRes); // give them a default template.
				ps.b.createCombatUnit(0,"Shock Trooper");

			}

			p.setInternalClock(gameClock);
			stmt.close(); 
	
		/*	ps.b.sendYourself("Hello, and welcome to the game! <br /> <br /> A.I. Wars is a new kind of strategy game. Our competitors'" +
					" insist on banning any player who uses JavaScript frontend bots to play for them and assist them. We on the other hand encourage it by providing you with a backend" +
					" Java A.I. that is fully programmable in all game-related tasks, called Eve, a Revelations class A.I. You can write programs that drive your game even when you're not online, which is something" +
					" the JavaScript bots could never do.<br /> <br /> You have been given an Autopilot membership for free for one week in order to finish the beginner quests, which teach you how to play and how to program. You cannot use Eve without an Autopilot membership." +
					" <br /><br />We are also the first game to have a player-driven storyline campaign. Choose your side:<br /> <br />" +
					" The Eagle Empire - Strict and strong with a promise of unwavering military protection for it's citizen states, the Eagles carry a heavy tax rate and a big stick.<br /> <br />" +
					" The Crimson Shadows - Made up of independent rebel and mercenary states, this ragtag group of nations is led by a mysterious leader known only as Azel. Sporting next to no tax rate, and no hand-holding, this organization is ruthlessly" +
					" hunted and outlawed by the Eagle Empire since the terrorist bombings on Eagle Prime.<br /> <br />" +
					"Of course, you can create your own league too. Reach a large enough size, and you will become part of the official storyline, like the Eagle Empire and the Crimson Shadows! <br /> <br /> Apply to one of these leagues " +
					" after you have completed the beginner quests. You will need a Communications Center. <br /> <br /> To begin the beginner quests, press the cube icon on the center of the left sidebar, then hit the BQ1 link in the center frame to get started. When you complete Quest Objectives, the Quest " +
					"will not update from in progress to completed unless you use Menu > Refresh or refresh the page, so do both of those before notifying us of a glitch in Quest completion.","Welcome to A.I. Wars Beta!");*/
			if(type==2) {
				p.save(); // Seeing as this player is about to die and be reloaded as a quest, best to save it first!
			}
			 //weapArrayNew = new int[4]; 
			 //weapArrayNew[0]=0; weapArrayNew[1]=0;
			 //weapArrayNew[2]=0; weapArrayNew[3]=0;
			//p.ps.b.createUnitTemplate("Wolfram Tank",2,100,100,100,100,weapArrayNew,0); // give them a default template.
			return p;
			} catch(MySQLTransactionRollbackException exc) { } 
			}
		} catch(SQLException exc) { exc.printStackTrace(); } catch(IOException exc) {
			exc.printStackTrace();
		}
		
		return null;
	}
	public static boolean scoutLogicBlock(Raid holdAttack) {
		/*
		 * We could have two filters? No. Need concealment to help army size.
		 * Stealth tech should also help in some way. Basically, can use
		 * concealment*stealthTech as a quantity S
		 * 
		 * Remember that the town population is included in modified
		 * concealment. Sending an army of the size of the town will yield to an
		 * extremely low S rating.
		 * 
		 * The defending army's S rating also includes town population and so
		 * can diminish too, so really what we need to be doing is comparing S
		 * ratings.
		 * 
		 * S values will be of the order of 100-1000.
		 * 
		 * yourS/theirS - if it's greater than 1, then there should be an
		 * exponentially decreasing chance that you are discovered, that's equal
		 * to like .01 when the S ratio is like 1.01.
		 * 
		 * If yourS/theirS is less than one, then it can serve as a linear thing
		 * - a random should be below it as a limit. So we have the difficulty
		 * of not being discovered decreasing linearly as the limit is raised -
		 * then once it reaches yourS/theirS, for the ratio being equal to 1 and
		 * greater, there is an exponentially small chance of discovery, and all
		 * levels of stat reports are accessible.
		 */
		
		//First we calculate pre-combat concealment, copy paste, bitches.
		

		
		// Now I need to add up all the other units concealment times their ratios to the highest one.
		// made a method to return concealments, same as combatlogicblock, but this makes
		// this code more tidy.
			/*double conc[] = returnModifiedConcealments(holdAttack);
			double Ca = conc[0];
			double Cd = conc[1];
			// right so now we have Ca, and Cd.
			System.out.println("Ca is " + Ca + " Cd is " + Cd + " t1 stealth is  " + holdAttack.town1.getStealth()
					+ " t2 stealth is " + (Cd)*holdAttack.town2.getStealth() + " using Cd+1 which is put in mod concealment method.");
			double Sa = (Ca)*holdAttack.town1.getStealth();
			double Sd = (Cd)*holdAttack.town2.getStealth();*/
			
			//double S = (Sa)/(Sd); // this is called S, the "S ratio."
		int ie = 0;int totalCheckedSize=0;
		while(ie<holdAttack.getAu().size()) {
			totalCheckedSize+=holdAttack.getAu().get(ie).getSize();
			// SuggestTitleVideoId
			ie++;
		}
		if(totalCheckedSize==0) {
			// this means we called getAu() for the first time before the au statements got to update and put
			// the units into the raid!
			holdAttack.setAu(null);
			holdAttack.getAu(); // reset.
		}
		int i = 0;
		double offPop=0; double defPop=0;
		Town t1 = holdAttack.getTown1(); Town t2 = holdAttack.getTown2();
		
		ArrayList<AttackUnit> t1au = holdAttack.getAu();
		ArrayList<AttackUnit> t2au = t2.getAu();
		try {
		t1.getPlayer().God.Maelstrom.addWeatherEffects(t1au,t2au,t2.getX(),t2.getY());
		AttackUnit.addSkinEffects(t1au);
		AttackUnit.addSkinEffects(t2au);
		} catch(Exception exc) { exc.printStackTrace(); System.out.println("Weather or skin effects caused an exception but combat was saved."); }
		
		while(i<t1au.size()) {
			offPop+=t1au.get(i).getSize()*t1au.get(i).getExpmod();
			i++;
		}
		 i = 0;
		while(i<t2au.size()) {
			defPop+=t2au.get(i).getSize()*t2au.get(i).getExpmod();
			i++;
		}
		i=0;
		double avgConc=0;
		AttackUnit au;
		if(offPop>0)
		while(i<t1au.size()) {
			double modded=0;
			au = t1au.get(i);
			 switch(au.getPopSize()) {
				case 1:
					modded=au.getConcealment();
					break;
				case 5:
					modded=au.getConcealment()/2;
					break;
				case 10:
					modded=au.getConcealment()/4;
					break;
				case 20:
					modded=au.getConcealment()/.5;
					break;
		
				}
			 
			 avgConc+=modded*(au.getSize()*au.getExpmod())/offPop;
			i++;
		}
		double avgAcc=0;i=0;
		if(defPop>0)
		while(i<t2au.size()) {
			double modded=0;
			au = t2au.get(i);
			 switch(au.getPopSize()) {
				case 1:
					modded=au.getConcealment();
					break;
				case 5:
					modded=au.getConcealment()/2;
					break;
				case 10:
					modded=au.getConcealment()/4;
					break;
				case 20:
					modded=au.getConcealment()/.5;
					break;
		
				}
			 avgAcc+=modded*(au.getSize()*au.getExpmod())/defPop;
			i++;
		}
		//((1+.05*scout)*avgConc)/((1+.05*stealth)*averageAcc)*cos^2(2pi*((yours)/(cover)))sin^2(2pi*(theirs/cover)). 
		Player t1p = t1.getPlayer(); Player t2p = t2.getPlayer();
		double Sa = (1+.05*t1p.getScoutTech())*avgConc+1;
		double Sd = (1+.05*t2p.getStealth())*avgAcc+1;
		double CSL = t2p.getPs().b.getCSL(t2.townID);
		double SSL = CSL/t2p.towns().size();
		CSL*=(((double) t2p.getStealthTech()+1.0)/20.0);
		if(CSL<=0) CSL=1;
		SSL*=(1-((double) t1p.getScoutTech()+1.0)/20.0);
		if(SSL<=0) SSL=1;
		double offContrib= Math.abs(Math.pow(Math.sin(Math.PI*offPop/(2*SSL+1)),1));
		if(offPop>(2*SSL+1)) offContrib=0; // This is beyond hope, you are beyond the curve.
		double defContrib= Math.abs(Math.pow(Math.sin(Math.PI*defPop/(2*CSL+1)),1));
		if(defPop>(2*CSL+1)) defContrib=0; // This is beyond hope, you are beyond the curve.


		//System.out.println(combatHeader);
		double addon = offContrib-defContrib;
	//	System.out.println("Sa/Sd is " + (Sa/Sd) + " and addon is " + addon + " from " + offContrib + " - " + defContrib);
		if(addon>1) addon = 1;
		if(addon<.1) addon=.1;
		double S = (Sa/Sd)*(addon);
		String combatHeader = "The S value of this scouting raid was " + ( ((double) Math.round(S*10))/10)+". The incoming scouts had a scouting ability of " + ( ((double) Math.round(Sa*10))/10) + " and the defenders had a detection ability of " 
		+ ( ((double) Math.round(Sd*10))/10) + ". The incoming scouts were within " + Math.abs(Math.round((100-100*offContrib))) + "% of their sweet spot."
		+ " The defenders were within " + Math.abs(Math.round((100-100*defContrib))) + "% of their sweet spot. The Detection Soft Limit was " + CSL +" and Scout Size Limit " + SSL+".";
	
		try {
		AttackUnit.removeEffects(t1au,t1p);
		AttackUnit.removeEffects(t2au,t2p);
	
		} catch(Exception exc) { exc.printStackTrace(); System.out.println("Weather or skin effects caused an exception but combat was saved."); }

		//System.out.println("S is " + S + " Sa is " + Sa + " Sd is " + Sd);
			// Now we have our s quotient...going up a stealth doubles
			// your chances for success!
			
			double rand = Math.random();
			//System.out.println("Rand is " + rand);

			String auoffnames="",auoffst="",auofffi="",audefnames="",audefst="";
			// if any field is "", then that means data not gathered.
			// however, m,t,mm,f being -1 means no resources either.
			long m=-1,t=-1,mm=-1,f=-1;
			if(S>=1) {
				// exponentially decreasing chance of going to combat mode.
				// difference in stealth techs yields type of report sent.
				
				double limit = .05*Math.exp(-S)/Math.exp(-1); // will yield .05 for
				// S = 1, meaning a 5% chance, 1 in 20 chance of getting caught at S = 1.
				// By comparison, the linear method at S = 1 has a limit of 1,
				// easily beaten, but at 9/10, it's .9, which means 1 in 10 chance
				// that you'll lose, 19/20, a .05 chance, and so on, but I do
				// not believe that stealth tech will be allowed to progress that far,
				// but yeah, at S =20/21, you have a better chance than at S = 1.
				// As long as stealthTech can't get there.
			//	System.out.println("Limit is " + limit);
				
				if(rand>limit) {
					// send status report
					/*
					 * 
					 * There are different levels of scouting, later levels include all data in previous levels:
						1. You see names and troop numbers in a town.
						2. You see resources.
						3. You see civilian numbers.
						4. You see building amounts and levels.
						5. You see attack unit type data of units that have size>0.
						
						Data allocation:
					1. using offNames for their unit names,
					2. auoffst for their troop numbers, civilians can be included here for level 2.
					3. auofffi can hold their stats and weapons held.
					4. audefnames can hold building names  
					5. audefst can hold their numbers. audeffi can hold their levels.
					6. m,t,mm, and f fields in a stat report can hold resources.
					 */
					int replvl = (int) Math.round(S);
					boolean loadCivies = false; // Case 2 should load civies but we want them
					// at the END of auoffnames so we place them after the switch UberStatement.
					if(replvl>4) replvl = 4; // max we can go!
					else if(replvl<0) replvl=0;
					AttackUnit a; String type[];
					switch(replvl) {
					// notice here, there are no breaks - you get four, you get all the information
					// as it drops through each case!
					case 4:
						 i = 0;
						auofffi=";";
						while(i<t2au.size()) {
							 a = t2au.get(i);
							if(a.getLotNum()==-1)// no civvies.
							if(a.getSize()>0) {auofffi+=","+a.getConcealment()+","+
							a.getArmor()+","+a.getCargo()+","+a.getSpeed();
							int j = 0;
							while(j<a.getWeap().length) {
								auofffi+=","+a.getWeap()[j];
								j++;
							}
							auofffi+=";"; // semicolon separates entries of unit info.
							} else {
								auofffi+=",?;"; // means no info on this guy since none are there.
							}
							
							i++;
						}
					case 3:
						// adding buildings
					/*	 type = new String[8];
						type[0]="Headquarters";
						type[1]="Warehouse";
						type[2]="Arms Factory";
						type[3] = "Construction Yard";
						type[4]="Institute";
						type[5]="Bunker";
						type[6] = "Trade Center";
						type[7]="Communications Center";
						
						 i = 0;
						while(i<type.length) {
							audefnames+=","+type[i];
							int j = 0;
							int numBldg = 0;
							while(j<holdAttack.getTown2().bldg().size()) {
								if(holdAttack.getTown2().bldg().get(j).type.contains(type[i])) numBldg++;
								j++;
							}
							audefst+=","+numBldg;

							i++;
						}*/
						
					

					/*	try {
							UberStatement stmt = t1p.God.con.createStatement();
							ResultSet rs = stmt.executeQuery("select name,lvl from bldg where tid = " + t2.townID);
							while(rs.next()) {
								audefnames+=","+rs.getString(1);
								audefst+=","+rs.getInt(2);
							}
							rs.close();
							stmt.close();
						} catch(SQLException exc) { exc.printStackTrace(); }*/
						
						i = 0;
						ArrayList<Building> bldg = t2.bldg();
						while(i<bldg.size()) {
							audefnames+=","+bldg.get(i).getType();
							audefst+=","+bldg.get(i).getLvl();
							i++;
						}
						
						
						
						
					case 2:
						// give them civvie numbers
						loadCivies=true;
					case 1:
						// give them resource amounts
						long res[] = t2.getRes();
						m=res[0];
						t=res[1];
						mm=res[2];
						f=res[3];
					
					case 0:
						// give them the attack unit amounts.
						 i = 0;
						while(i<t2au.size()) {
							if(t2au.get(i).getLotNum()==-1) {
							auoffnames+=","+t2au.get(i).getName();
							auoffst+=","+t2au.get(i).getSize();
							}
							i++;
						}
					
						
					
					}
					Building b;
					
					if(loadCivies) {
						auoffnames+=",Engineer,Trader,Scholar";
						
						
						auoffst+="," + t2.getTotalEngineers() + ","
						+t2.getTotalTraders() + "," + t2.getTotalScholars();
						if(replvl==4) { // only if replvl = 4 do we send attack unit info,
							// even for civilians!
						int weap[] = new int[1];
						weap[0]=holdAttack.getTown2().getPlayer().getCivWeapChoice();
						 a = new AttackUnit("Civilian",3,weap,"Civilian");
						auofffi+=","+a.getConcealment()+","+
						a.getArmor()+","+a.getCargo()+","+a.getSpeed()+","+a.getWeap()[0]+";";			
						} 
						
					}
					auoffnames+=",Cover Soft Limit";
					auoffst+=","+t2p.getPs().b.getCSL(t2.townID);
					auoffnames+=",Your Cover Size In Originating Town";
					auoffst+=","+t1p.getPs().b.getCS(t1.townID);
					auoffnames+=",Their Cover Size In Target Town";
					auoffst+=","+t2p.getPs().b.getCS(t2.townID);
					
				} else {
					// resend raid as a raid.
					holdAttack.setScout(2);
					combatLogicBlock(holdAttack,combatHeader);
					return false;
				}
				
				
			} else {
				// means a linearly decreasing chance of being found out...
				// as Sa/Sd decreases, that means attackers have less and less
				// stealth, and if a random number falls below this limit,
				// then a report is made, above, combat ensues, as for lower
				// Sa, easier to get above than below!

				double limit = S;
			//	System.out.println("Limit is " + limit);


				if(rand<limit) {
					// send status report
					 i = 0;
						while(i<t2au.size()) {
							auoffnames+=","+t2au.get(i).getName();
							auoffst+=","+t2au.get(i).getSize();
							i++;
						}
						
						auoffnames+=",Cover Soft Limit";
						auoffst+=","+t2p.getPs().b.getCSL(t2.townID);
						auoffnames+=",Your Cover Size In Originating Town";
						auoffst+=","+t1p.getPs().b.getCS(t1.townID);
						auoffnames+=",Their Cover Size In Target Town";
						auoffst+=","+t2p.getPs().b.getCS(t2.townID);
						
				} else {
					// resend raid as a raid.
					holdAttack.setScout(2);
					combatLogicBlock(holdAttack,combatHeader);
					return false;
				}
			}
			int c= 0;
			double lowSpeed = 0;
			AttackUnit g;
			// we want it weighted...so we must divide by total size*expmod...
			// because that gives us the total amount of soldier equivalents.
			int totalsize=0;
			while(c<t1au.size()) {
				 g = t1au.get(c);
			//	if(g.size>0&&g.speed<lowSpeed) lowSpeed=g.speed;
				lowSpeed+=(g.getSize()*g.getExpmod()*g.getSpeed());
				totalsize+=(g.getSize()*g.getExpmod());
				 c++;
			} 
			lowSpeed/=totalsize;
			int testhold = (int) Math.round((Math.sqrt(Math.pow((t2.getX()-t1.getX()),2)+Math.pow((t2.getY()-t1.getY()),2))*10/(lowSpeed*speedadjust))/GodGenerator.gameClockFactor);
			if(testhold==0) testhold=(int) Math.round(((double) 10/(lowSpeed*speedadjust))/GodGenerator.gameClockFactor);
			holdAttack.setRaidOver(true);
			holdAttack.setTicksToHit(testhold);
			
			try {
				
				   
			   UberStatement   stmt = t1p.God.con.createStatement();
			      boolean transacted = false;
			      // First things first.
			      
			  //    System.out.println("I am making a scout report.");
			      while(!transacted) {
			    	  
			      try {
			      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
			     // only the scouting player gets a report, the defensive one does not.
			      // we know scout will be 1 so we set it that way and save a SQL transaction.
			      stmt.execute("insert into statreports (defender,scout,m,t,mm,f,pid,tid1,tid2,auoffst,auofffi,audefst,audeffi,auoffnames,audefnames,combatHeader,offTownName,defTownName) values" +
			      		"(false," + 1 + "," + m + "," + t + "," + mm + "," + f +","
			      		+ t1p.ID + ","+ t1.townID + "," + t2.townID + ",\"" + auoffst + "\",\"" + auofffi + "\",\"" 
			      		+ audefst + "\",\"" + "" +"\",\""+ auoffnames + "\",\"" + audefnames + "\",\"" + combatHeader +  "\",'"+t1.getTownName()+"','"+t2.getTownName()+"');");
			   
			      // send out reports to support units' players on offensive side, we're assuming
			      // we weren't discovered so why should defense get one?
			      int o = 6;
			      ArrayList<Player> holdForP = new ArrayList<Player>();
			      Player curr;
			      while(o<t1au.size()) {
			    	   curr = t1au.get(o).getOriginalPlayer();
			    	   c = 0;
			    	  boolean found = false;
			    	  while(c<holdForP.size()) {
			    		  if(holdForP.get(c).ID==curr.ID) {found=true; break; }
			    		  c++;
			    	  }
			    	  
			    	  if(!found) holdForP.add(curr);
			    	  
			    	  o++;
			      }
			      
			      o = 0;
			      while(o<holdForP.size()) {
			    	  
				      
				      stmt.execute("insert into statreports (defender,scout,m,t,mm,f,pid,tid1,tid2,auoffst,auofffi,audefst,audeffi,auoffnames,audefnames,offTownName,defTownName) values" +
				      		"(false," + 1 + "," + m + "," + t + "," + mm + "," + f +","
				      		+ holdForP.get(o).ID + ","+ t1.townID + "," + t2.townID + ",\"" + auoffst + "\",\"" + auofffi + "\",\"" 
				      		+ audefst + "\",\"" + "" +"\",\""+ auoffnames + "\",\"" + audefnames + "\",'"+ t1.getTownName() + "','"+ t2.getTownName()+"');");
			    	  o++;
			      }
			      
			      stmt.execute("commit;");stmt.close();transacted=true; } catch(MySQLTransactionRollbackException exc) { }
			      }
			 } catch(SQLException exc) { exc.printStackTrace(); }

		return true;
	}
	public static boolean checkForInvasion(Raid holdAttack) {
		
		// see below for comments.
		
		if(holdAttack.getTown2().getPlayer().ID==holdAttack.getTown1().getPlayer().ID){
			// So if by some chance this is being processed while an invasion is being processed,
			// this if UberStatement sends this support raid back...there is a small window though
			// that beingInvaded will be turned on before this block ends but after this if UberStatement.
			// It'll return false if it sends the raid back and true if it doesn't.
			holdAttack.setRaidOver(true);
			int c = 0;
			double lowSpeed = 0;
			AttackUnit g;
			// we want it weighted...so we must divide by total size*expmod...
			// because that gives us the total amount of soldier equivalents.
			int totalsize=0;
			ArrayList<AttackUnit> au = holdAttack.getAu();
			do {
				 g = au.get(c);
			//	if(g.size>0&&g.speed<lowSpeed) lowSpeed=g.speed;
				lowSpeed+=(g.getSize()*g.getExpmod()*g.getSpeed());
				totalsize+=(g.getSize()*g.getExpmod());
				 c++;
			} while(c<au.size());
			lowSpeed/=totalsize;
			Town t1 = holdAttack.getTown1(); Town t2 = holdAttack.getTown2();
			int testhold = (int) Math.round((((double) Math.sqrt(Math.pow((t2.getX()-t1.getX()),2)+Math.pow((t2.getY()-t1.getY()),2))*10/(lowSpeed*speedadjust)))/GodGenerator.gameClockFactor);
			if(testhold==0) testhold=(int) Math.round(((double) 10/(lowSpeed*speedadjust))/GodGenerator.gameClockFactor);

			holdAttack.setTicksToHit(testhold);
			
			// Now raid is being returned!
			return false;
		}
		
		return true;
	}
	public static boolean supportLogicBlock(Raid actattack) {
		// so if the raid isn't over but it is a support run of some sort, we need to offload
		// these troops and send any back according to the support tech rules which are:
		// level x means x slots and x*10 percent of total population of city per slot.
		
		// first let's get max size. Look at population of army in the city.
		// this means bigger cities with bigger armies can support larger
		// hosts.  Granted, what about your own troops? I mean, hell, if you're recieving
		// guys that are NOT support units then these should be added just fine,
		// however if there ARE foreign units in this raid, then those need
		// to follow the rules. This means if you try to move foreign armies
		// to cities that cannot support them, they will be sent back.
		
		// recieved 0-size foreign AU should not be added to the list.
		/*
		 * Procedure here is:
		 * 1. Find the maxSize by adding up all native AUs and subtracting all
		 * foreign ones from the same guy that sent the raid. This is used
		 * to subtract from to keep track of how many spots are left in the city
		 * for this supporter. Without the subtraction, they could fill up as
		 * much as they want as each raid would start it's calculation anew!
		 * 2. Move all natural units in shipment over normally(Full size)
		 * OR move all foreign units in shipment over according to their
		 * proportions(see moveForeignSupportAUs method)
		 * 3. If maxMeter, a running tally of moved foreign support unit size*expmod, is
		 * less than maxSize,
		 * and there are units left, these are new
		 * AU types that need to be added. Add them to the supportAU bhdb table and to memory.
		 * 4. Make a support report in statreports bhdb.
		 * 5. if there are units left, return them. Otherwise, delete the raid.
		 */
		// Also need to produce a new type of status report.
		
		/*
		 * The thing is, what if the user is sending support units in from multiple other users
		 * WITH his units? It'd be easier to just allow support runs to be sent, but only with your
		 * own units. Don't allow moving of other people's units. Indeed, allowing people to move
		 * entire armies around from other people without their permission or knowing is sort of
		 * like being given extra units. They could just "sell" them to people. This in itself
		 * is unacceptable. In the current way of things, users can send their own support units,
		 * but nobody elses.
		 * 
		 * The below handles all units - it could handle it, I believe, if a user somehow managed
		 * to send support units from another player to another town, but it wouldn't calculate it entirely
		 * properly - namely, it'd assume they were all from the same user, not from different ones.
		 * However, the support() function, as defined by battlehardfunctions, does not allow
		 * moving of units that aren't your own as per the support condition, so this is a moot
		 * point.
		 * 
		 * In all cases however, the units will either be entirely foreign to town2 or entirely
		 * from the same user.
		 */
		int ie = 0;int totalCheckedSize=0;
		while(ie<actattack.getAu().size()) {
			totalCheckedSize+=actattack.getAu().get(ie).getSize();
			// SuggestTitleVideoId
			ie++;
		}
		if(totalCheckedSize==0) {
			// this means we called getAu() for the first time before the au statements got to update and put
			// the units into the raid!
			actattack.setAu(null);
			actattack.getAu(); // reset.
		}
		Town t1 = actattack.getTown1(); Town t2 = actattack.getTown2();
		Player t1p = t1.getPlayer(); Player t2p = t2.getPlayer();
	//	int t1x = t1.getX(); int t1y = t1.getY();
		//int t2x = t2.getX(); int t2y = t2.getY();
		ArrayList<AttackUnit> t2au = t2.getAu();
		ArrayList<AttackUnit> t1au = actattack.getAu();
		//	UserRaid holdAttack = t1p.ps.b.getUserRaid(actattack.raidID);
		AttackUnit townVers,incoming,test;// ArrayList<Raid> attackServer;

		//if(!checkForInvasion(actattack)) return false;
		// If you're trying to support your own town and it's being invaded at the moment,
		// you're not going to be doing any supporting, and checkForInvasion returns false
		// if so so you know to return false, if it returns true, keep moving.
		// checkForInvasion also saves us the trouble of sending the raid back, it
		// does it also. Will be placed periodically through the sequence for
		// checks.
		
		int y = 0; int maxSize=0; int maxMeter = 0; // used to measure as we add units to the total size.
		int originalTotalSize = 0; // This is the total size of the sent raid aus. Used for moveForeignSupportAUs()
		int originalTotalExpModPop=0; // Used for the measuring of maximums with Zeppelins - need to know proportions!
		// method.
		y = 0; 

		while(y<t1au.size()) {
			 incoming = t1au.get(y);
			originalTotalExpModPop+=incoming.getSize()*incoming.getExpmod(); // just for later Zeppelin max stuff.

		y++;	
		}
		if(t2p.ID!=t1p.ID) {	
			// This if block sets up the equivalent of "maxCargo" for
			// foreign support units if this is a foreign contingent.
			// Otherwise it doesn't need to be done as all units
			// will not be foreign and the moveForeignSupportAUs method
			// will not be used! (Not doing this for that saves memory.)
		y = 0;

		AttackUnit a;
		while(y<t2au.size()) {
			 a = t2au.get(y);
			//System.out.println(a.name +"'s support is " + a.support);
			if(a.getSupport()==0) // can't have support units adding to the tally, now can we?
				maxSize+=a.getSize()*a.getExpmod(); // so ten soldiers = 1 tank and 1 tank takes as much maxSize as 10 men.
			y++;	
		}
		y = 0;
		Raid r; // we also check raids for AU! Thank You Markus -.-.
		
		/*
		try {
			UberStatement stmt = t2p.God.con.createStatement();
			ResultSet rs = stmt.executeQuery("select sum(au1+au2+au3+au4+au5+au6) from raid where tid1 = " + t2.townID + " and (raidOver = false or ticksToHit >= 0)");
			if(rs.next()) maxSize+= rs.getInt(1);
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		*/
		
		ArrayList<Raid> raids = t2.attackServer();
		ArrayList<AttackUnit> auset;
		while(y<raids.size()) {
			r = raids.get(y);
			auset = r.getAu();
			int k = 0;
			while(k<6) {
				maxSize+=auset.get(k).getSize();
				k++;
			}
			y++;
		}
		
		maxSize*=.1*t2p.getSupportTech(); // so if supportTech = 5, can hold .5*size of pop.
		
		// now maxSize is  almost ready. We'll use it to deduct from when we stock units in the new town,
		// to keep a tally of how much we can do. All that remains
		// is to make sure it's adjusted for what the player may already have on site.
		//System.out.println("Max size before subtraction: " + maxSize);
		y = 0; 

		while(y<t1au.size()) {
			 incoming = t1au.get(y);
			originalTotalSize+=incoming.getSize(); // just for later moveForeignSupportAUs stuff.

		y++;	
		}
			 int k = 0;
			  incoming = t1au.get(0); // Just to use for purpose
			 // of locating other player held units.
			while(k<t2au.size()) {
		
				 townVers =t2au.get(k);

				if(incoming.getSupport()>0&&incoming.getOriginalPlayer().ID!=t2p.ID
						&&townVers.getSupport()>0&&incoming.getOriginalPlayer().ID==townVers.getOriginalPlayer().ID) {
					// so if the incoming au is the from the same FOREIGN player(see explanation in nearly identical if
					// UberStatement in next loop for more on this branch)
					// then subtract this size amount from max size!
					maxSize-=townVers.getSize()*townVers.getExpmod();
				//	System.out.println(townVers.name + " takes " + townVers.size*townVers.expmod);
				}
				
				k++;
			}
		
			if(t2.getPlayer().ID==5) maxSize=9999999; // pretty much can hold any amount if you're Id.
		
		// Now max Size is prepared.
		// Lets make sure there are even slots available by finding out how many
		// foreigners have a spot here.
		
		int numOtherForeignPlayers = 0; // the number of other foreign players cannot exceed supportTech.
		
		 k = 0;
		while(k<t2au.size()) {
			 townVers = t2au.get(k);
			if(townVers.getSupport()>0) {
				int i = 0;
				boolean found = false; // looking for a duplicate foreign player ID in the units already scanned
				// to make sure not to double count players. Ideally, I'd check each AU, and count
				// each foreign one, but more than one foreign type may be from a single player,
				// so this method, ie, double checking for all before units to see if I've already counted
				// this guy once, is a safe bet to insure no double counting.
				while(i<k) {
					// k not inclusive as this grabs the latest unit.
					 test = t2au.get(i);
					if(test.getSupport()>0&&test.getOriginalPlayer().ID==townVers.getOriginalPlayer().ID&&
							test.getOriginalPlayer().ID!=t1p.ID) { found=true; break; }
					// Notice the last part, that the test's original player is not the town
					// that the support is coming from.
					// This means that if the player already has units here, his will not be counted
					// in the total tally of foreigners so that if the total foreigners is already
					// at limit but he is one of them, it'll look to this method as if there is
					// room for one more and his units will be added.
					i++;
				}
				
				
				if(!found) numOtherForeignPlayers++; // so if this is the first au seen with this foreign ID,
				// then we count it as a mark of a foreign player.
			}
			k++;
		}
		
		if(numOtherForeignPlayers>=t2p.getSupportTech()&&t2p.ID!=5) maxSize=0; 
		// numOtherForeignPlayers will never be > than but just in case.
		// setting maxSize = 0 ensures no units moved.
	}
	
		y = 0; 					
		String supportUnitNames="";
		String supportUnitsGained = "";
		String supportUnitsReturned = "";
		
	///	checkForInvasion(actattack); // Right before we start changing values,
		// check again in case that small window has happened and invasion began
		// right after the last guy was called.
		int CSL = t2p.getPs().b.getCSL(t2.townID);
		
		int max = 99999999;

		if(t2.isZeppelin()) { // if it's a Zeppelin, we need to take into account its' limits and reset the max.
			int CS = t2p.getPs().b.getCS(t2.townID);
			max = CSL-CS; 
			// max is based on the amount of space available in soldiers times the fraction of the incoming force you are, so you get the proportional piece of the max required.
			// we set up the math here that we need for later, then we multiply it by the part of the fraction given by the incoming.
			//This means changes for local units, but for foreign ones, we can easily just reset the maxSize, because
			// you can never send foreign AUs on support, only send yours to another person's.
		}
		
		if(maxSize>max) maxSize=max; // We make sure that you CANNOT get beyond the max.
		
		while(y<t1au.size()) {
			 incoming = t1au.get(y);
			int k = 0;
			while(k<t2au.size()) {
				// just a precaution. Ideally I should be able to grab the au at the same
				// index but I don't want to take that risk with a consumer game.
				// What if I change something, or I'm wrong?
				 townVers = t2au.get(k);
				if(incoming.getSupport()==0
						&&townVers.getSlot()==incoming.getSlot()) {  // if this is a non-foreign unit and slot = slot.
				
					// we set toAddInSoldiers equal to the soldier populations. If the toAddIS is greater than the max, it is set
					// set to the max. Then we figure out how many units that actually is in it's expmod, and then go with it.
					int thisMax = (int) Math.round(max*((double) incoming.getSize()*incoming.getExpmod())/((double) originalTotalExpModPop));
					int toAddInSoldiers = incoming.getSize()*incoming.getExpmod(); 
					//System.out.println("thisMax is " + thisMax + " before being tested, and the fraction is " + (((double) incoming.getSize()*incoming.getExpmod())/((double) originalTotalExpModPop)));
					if(toAddInSoldiers>thisMax) toAddInSoldiers= thisMax;
					int toAdd = (int) Math.round(((double) toAddInSoldiers)/((double) incoming.getExpmod()));
				//	System.out.println("thisMax is " + thisMax + "  as a result of max being  "+ max + " and originalTEMP " + originalTotalExpModPop + " with toAddInSoldiers becoming " + 
					//		toAddInSoldiers + " which made toAdd " + toAdd);
					supportUnitNames+=","+incoming.getName();
					supportUnitsGained+=","+toAdd;
					supportUnitsReturned+=","+(incoming.getSize()-toAdd);
					t2.setSize(k,townVers.getSize() + toAdd);
					actattack.setSize(incoming.getSlot(),(incoming.getSize()-toAdd));
					}
				// So if the players are the same, then just add the size, we're moving
				// units around is all.
				else if(incoming.getSupport()>0&&incoming.getOriginalPlayer().ID!=t2p.ID
						&&townVers.getSupport()>0&&incoming.getOriginalPlayer().ID==townVers.getOriginalPlayer().ID
						&&incoming.getOriginalSlot()==townVers.getOriginalSlot()) { 
					//		System.out.println("I am giving foreign units already in place.");
							supportUnitNames+=","+incoming.getName();
							int oldMaxMeter=maxMeter;
							maxMeter+=moveForeignSupportAUs(maxSize,actattack,incoming,townVers,k, originalTotalSize);
							//incoming = t1au.get(y);

							if(incoming.getSupport()!=townVers.getSupport()){
								try {
									UberStatement stmt = t1p.God.con.createStatement();
									stmt.executeUpdate("update supportAU set stype=" + incoming.getSupport() + " where ftid = " + t1.townID + " and tid = " + t2.townID);
									t2au = t2.getAu(); // gotta refresh now!
									stmt.close();
								} catch(SQLException exc) { exc.printStackTrace(); }
							}
							
							// if the sender sends off support of the same type, all become off.
							// and vice versa.
							supportUnitsGained+=","+(maxMeter-oldMaxMeter);
							supportUnitsReturned+=","+incoming.getSize();

					}
					// Explanation of above if UberStatement:
					// If the player isn't the same,
					// then the slot on the incoming is going to be the slot on the other player.
					// We may already have units of his here or we may not, so we need
					// to do extra comparisons:
					//  Is townVers a support unit?
							//If yes, is it from the same player, with the same originalSlot?
							// If so, add to this.
							// if not, move on.
					
					
				
				k++;
			}
			
			
			y++;
		}
		
		y=0; 
		ArrayList<AttackUnit> au;
		try {
			

		   UberStatement   stmt = t1p.God.con.createStatement();
		   boolean transacted=false;
		   while(!transacted) {
			   
		   try {
			   ResultSet rs;
		      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
		    //  System.out.println("maxMeter is " + maxMeter + " maxSize is " + maxSize);
		
		      if(maxMeter<maxSize) { // If maxMeter = maxSize, then we're either
			// outta units to move over or some couldn't be as they haven't had au types added
			// yet. This block adds these types and updates bhdb!
			AttackUnit newGuy;
		while(y<t1au.size()) {
			
			if(t1au.get(y).getSize()>0) {
				//  they were never added because their AU
				// type isn't attached here yet.
				 // The only reason this size>0 and maxMeter is if this AU type not attached
				// to town's AU array yet. Get me?
				// if the size is zero, it's just because it's been forced there
				// by the uniformity of the battlehardfunctions attack() function's
				// au list assembly loop adding every AU on the town's AU list,
				// regardless of whether or not the user indicated it wanted
				// a greater-than-zero number of units on the raid,
				// and it's not actually meant to be
				// added as it's a zero, indicating the user DID NOT TRY TO SEND
				// A SUPPORT AU ON A SUPPORT RUN SOMEWHERE ELSE!
				
				
				
					// New AU to be added to the food chain, and then stocked appropriately, since we still have
					// maxSize points left.
				
				//System.out.println("I am giving a new foreign unit type..");

					 newGuy = t1au.get(y).returnCopy();
					int oldMaxMeter=maxMeter;
					supportUnitNames+=","+newGuy.getName();
					newGuy.setSize(0);
					au = t2au;
					int slot = 6; // default.
					int indexCount = 6;
					int k = 6;
					int maxSlot=6;
					while(k<au.size()) {
						if(au.get(k).getSlot()>maxSlot) {
							maxSlot=au.get(k).getSlot();
						}
						indexCount++;
						k++;
					}
					
					/*
					rs = stmt.executeQuery("select max(slotnum) from supportAU where tid = " + t2.townID);
					if(rs.next()&&rs.getInt(1)>0)
					 slot = rs.getInt(1)+1;
					rs.close();
					
					rs = stmt.executeQuery("select count(*) from supportAU where tid = " + t2.townID);
					if(rs.next())
					 indexCount += rs.getInt(1); // Now if there is one supportAU previously, we're looking to grab the next one, at 7.
					rs.close();
					*/
					
					stmt.execute("insert into supportAU (tid,ftid,fslotnum,slotnum,size,stype) values (" + 
							t2.townID + "," + t1.townID + "," + newGuy.getOriginalSlot() + "," + (maxSlot+1)+ "," +
							newGuy.getSize() + "," +  newGuy.getSupport()+ ");");
					newGuy.setSlot(maxSlot+1);
					t2au.add(newGuy);
					System.out.println("t1au " + y + " size is " + t1au.get(y).getSize());
					maxMeter+=moveForeignSupportAUs(maxSize,actattack,t1au.get(y),newGuy,indexCount,originalTotalSize);
					supportUnitsGained+=","+(maxMeter-oldMaxMeter);
					supportUnitsReturned+=","+(t1au.get(y).getSize());

					//supportUnitsReturned+=","+(t1au.get(y).getSize()-(maxMeter-oldMaxMeter)); // we take how much was added and subtract. DB version.
				//	t1au.get(y).setSize(t1au.get(y).getSize()-(maxMeter-oldMaxMeter)); // just keeping up with the real model in the DB.
					System.out.println("t1au " + y + " size is " + t1au.get(y).getSize());

				//	holdAttack.getTown2().getAu().add(newGuy);
				//	newGuy.setSlot(holdAttack.getTown2().getAu().size()-1); // so it's the most
					// recent addition, makes sense size-1 is it's slot!
				
						// Need to make a new entry in the supportAU table.
					
					

			}
	
			
			y++;
		}
		

		}
		
		
		
		
	

		      
		      // First things first. We update the player table.
		//      System.out.println("I am making a support report.");
		      stmt.execute("insert into statreports (pid,tid1,tid2,auoffst,auofffi,auoffnames,offTownName,defTownName,support) values" +
		      		"(" +t1p.ID + ","+ t1.townID + "," + t2.townID + ",\"" + supportUnitsGained + "\",\"" + supportUnitsReturned + "\",\"" 
		      		+ supportUnitNames + "\",'"+t1.getTownName()+"','"+t2.getTownName()+"',true);");
		      if(t2p.ID!=t1p.ID)
		      stmt.execute("insert into statreports (pid,tid1,tid2,auoffst,auofffi,auoffnames,offTownName,defTownName,support) values" +
			      		"(" +t2p.ID+","+ t1.townID + "," + t2.townID + ",\"" + supportUnitsGained + "\",\"" + supportUnitsReturned + "\",\"" 
			      		+ supportUnitNames + "\",'"+ t1.getTownName() + "','" + t2.getTownName() + "',true);");
		      stmt.execute("commit;");stmt.close(); transacted=true;
		   } catch(MySQLTransactionRollbackException exc) { } 
		   }
		}catch(SQLException exc) { exc.printStackTrace(); }

			// At this point, if there are still units in the raid,
			// Just no more room left, may or may not be AU object for it.
			// Need to return the units.
			int c = 0;
			double lowSpeed = 0;
			AttackUnit g;
			// we want it weighted...so we must divide by total size*expmod...
			// because that gives us the total amount of soldier equivalents.
			int totalsize=0;
			do {
				 g = t1au.get(c);
			//	if(g.size>0&&g.speed<lowSpeed) lowSpeed=g.speed;
				lowSpeed+=(g.getSize()*g.getExpmod()*g.getSpeed());
				totalsize+=(g.getSize()*g.getExpmod());
				 c++;
			} while(c<t1au.size());
			lowSpeed/=totalsize;
			
			if(lowSpeed>0) {
				
			actattack.setRaidOver(true);
			int testhold = (int) Math.round((Math.sqrt(Math.pow((t2.getX()-t1.getX()),2)+Math.pow((t2.getY()-t1.getY()),2))*10/(lowSpeed*speedadjust))/GodGenerator.gameClockFactor);
			if(testhold==0) testhold=(int) Math.round(((double) 10/(lowSpeed*speedadjust))/GodGenerator.gameClockFactor);

			actattack.setTicksToHit(testhold);
			
			} else {
				// in this special case where maxSize=0 and size=0(ie the lowSpeed never changes because there are
				// no units left to contribute to it) then the raid gets deleted.
				actattack.setRaidOver(true);
				actattack.deleteMe();
			}
		
			
			
		return true;
	}
	public void sendNukeMessage(String otherGuy, Town holdT, boolean win) {
		
		int o = 0;
		ArrayList<Player> holdForP = new ArrayList<Player>();
		
		Hashtable wm =holdT.getPlayer().getPs().b.getWorldMap();
		
		ArrayList<Integer> playerIDs = new ArrayList<Integer>();
		Hashtable[] towns = (Hashtable[]) wm.get("townHash");
		Player p = holdT.getPlayer();
		while(o<towns.length) {
			int k = 0; boolean found=false;

			while(k<playerIDs.size()) {
				if(playerIDs.get(k).toString().equals(((Integer) towns[o].get("pid")).toString())) {
					found = true; break;
				}
				k++;
			}
			
			if(!found) playerIDs.add((Integer) towns[o].get("pid"));
			o++;
		}
		

		//	public boolean sendMessage(int pid_to[],String body, String subject, int original_subject_id) {
		int pids[] = new int[playerIDs.size()];
		o = 0;
		while(o<pids.length) {
			pids[o]=playerIDs.get(o);

			o++;
		}
		if(win)
		System.out.println("Sending ... " + p.getPs().b.sendMessage(pids,"<<<<<SIGNAL HIJACKED>>>>> <br /><br /> <<<<STATIC>>>> This is a message from the " + p.getUsername()+" Liberation Front. We are an organization " +
				"devoted to peace and harmony in this Empire. We've just received <<<<STATIC>>>>> that our Emperor has undertaken a nuclear" +
						" weapons program in " +  holdT.getTownName() + " at coordinates " + holdT.getX() + "," + holdT.getY() +". We need your help to stop this " +
								"monstrous leader from taking away our <<<<STATIC>>>> and our humanity! <br /><br /> As of now, our Emperor has one week to finish the Missile Silo. If you attack " + 
								holdT.getTownName() + " before this time and wipe out all of his defenses, the silo will be destroyed. <br /><br /> Please, help us!<br /><br /> <<<<<SIGNAL RETAINED>>>>>", "TRANSMISSION WARNING: HIJACKED SIGNAL",0)+ ",Error is "  + p.getPs().b.getError());
		else
		p.getPs().b.sendMessage(pids,"<<<<<SIGNAL HIJACKED>>>>> <br /><br /> <<<<STATIC>>>> This is a message from the " + p.getUsername()+" Liberation Front. Together with the help of " + otherGuy + ", we thwarted the Emperor's plans and destroyed his missile silos, and he is now weeping in his Palace like a girl. Thank you for all of your help! <<<<<SIGNAL RETAINED>>>>>", "TRANSMISSION WARNING: HIJACKED SIGNAL",0);
		
		
	
	}
	public static int moveForeignSupportAUs(int maxSize, Raid r, AttackUnit incoming,AttackUnit townVers,int townVersIndex, int originalSizeOfIncomingForce) {
		/*
		 * This method subtracts from maxSize, a size holder, the proportional
		 * amount of incoming size that should be taken and placed into the townVersion of the attackUnit.
		 * It's sort of like the way reosurces are done - it looks at other aus, their sizes,
		 * and figures out what proportion this one's size is to the others and then moves
		 * that proportion over so that every foreign unit has a chance to get an equal
		 * representation from max Size.
		 * 
		 * Because this method may be called multiple times, the size of the incoming units may have
		 * changed if this isn't the first calling. Hence, we have the originalSizeOfIncomingForce int
		 * to "assist" with this. 
		 * 
		 * This method uses technology from the moveResources method - in this case, the total is
		 * maxSize, and wer'e figuring how much of ONE resource(ONE au) can be taken out of the many.
		 * 
		 * We know that in the case that this method is called, all units are foreign units.
		 */
		
		// maxSize is totalCargo.
		// originalSizeOfIncomingForce is totalTRes, traditionally size*expmod to be even. popSize only
		// useful in concealment calculations since expmod too large.
		//incoming.size is t.res[i].
		
		// this version does not use longs like moveResources does.
	//	int i = 0;
		//ArrayList<AttackUnit> au = r.getTown2().getAu();
		
		
				int totake = (int) Math.ceil(incoming.getSize()*maxSize/(originalSizeOfIncomingForce+1)); // find what to take
				int taken = incoming.getSize()-totake; // subtract in holder variable
				if(taken<0) totake+=taken; // if it's neg, subtract that from totake, we're not taking more than we can.
				if(taken<0) { r.getTown2().setSize(townVersIndex,townVers.getSize()+incoming.getSize());
				System.out.println("Totake was " + totake + " incoming size was " + incoming.getSize() + " and townverssize was " + townVers.getSize() +  "Added to " + r.getTown2().getAu().get(townVersIndex).getName() + " some " + r.getTown2().getAu().get(townVersIndex).getSize());
				r.setSize(incoming.getSlot(),0); }else {
					r.getTown2().setSize(townVersIndex,townVers.getSize() + totake); r.setSize(incoming.getSlot(),incoming.getSize() - totake); // if < 0, it's 0, if not, subtract it.
				}
			
		
				// totake is what should be subtracted from maxSize...but really we'll keep a secondary
				// variable called maxSizeMeter to be subtracted from.
				
				return totake;
		
	}
	public static boolean invasionLogicBlock(Raid holdAttack) {
	/* 3. Make the invasionLogicBlock that checks to see if bunkers/HQs exist
	 and if they don't then check to see that the army is at least twice
	 the population of the town(expMod should be used instead of popSize,
	 as then the larger attack units are better for invasion.) If
	 the prospects check out, then the town needs to be removed from one player
	 and put on another with the giveTown method.
	 */
		
	if(!holdAttack.isInvade()) return false;
	Town t1 = holdAttack.getTown1(); Town t2 = holdAttack.getTown2();
	ArrayList<AttackUnit> t1au = holdAttack.getAu(); ArrayList<AttackUnit> t2au = t2.getAu();
	Player t1p = t1.getPlayer(); Player t2p = t2.getPlayer();
	
	int townSize = 0;
	int t2TownSize=0;
	int numHQBunkers=0;
	System.out.println("-1");

	if(t2.getPlayer().isQuest()) {
	
		int i = 0;
		QuestListener q = (QuestListener) t2.getPlayer();
		ArrayList<doableBy> invadableBy = q.invadableBy();
		boolean foundAny=false,found=false;
		doableBy d;
		while(i<invadableBy.size()) {
			d = invadableBy.get(i);
			if(d.tid==t2.townID) {
				foundAny=true;
				// now we know for certain that this town is only invadable by some.
			}
			
			if(foundAny&&d.tid==t2.townID&&d.pid==t1.getPlayer().ID) {
				found=true;
			}
			i++;
		}
		
		if(foundAny&&!found) return false; // so if some invadableBys were found but yours wasn't, return false.
	}
	/*try {
		UberStatement stmt = t1p.God.con.createStatement();
		
		ResultSet rs = stmt.executeQuery("select count(*) from invadable where tid =  " +t2.townID);
		if(rs.next()) {
			// means can only be invaded by certain people.
			int amt = 0;
			amt = rs.getInt(1);
			if(amt>0) { // then we know whether or not there is anything there. 
			rs.close();
			rs = stmt.executeQuery("select count(*) from invadable where tid = " + t2.townID + " and pid = " + t1p.ID);
			 amt = 0;
			if(rs.next()) amt=rs.getInt(1);
			rs.close();
			if(amt==0) {
				stmt.close();
				return false;
			}
			}
		}
		rs.close();
//	/*	 rs = stmt.executeQuery("select count(*) from town where pid = " + t1p.ID);
	//	if(rs.next()) townSize = rs.getInt(1);
	//	rs.close();
		
	//	rs = stmt.executeQuery("select count(*) from town where pid = " + t2p.ID);
	//	if(rs.next()) t2TownSize = rs.getInt(1);
//		rs.close();
		
	//	rs = stmt.executeQuery("select count(*) from bldg where tid = " + t2.townID + " and (name = 'Bunker' or name = 'Headquarters');");
		//if(rs.next()) numHQBunkers = rs.getInt(1);
		//rs.close();
	
		
		stmt.close();
	} catch(SQLException exc) { exc.printStackTrace(); }*/
	
	townSize=t1p.towns().size();
	t2TownSize = t2p.towns().size();
	
	ArrayList<Building> bldg = t2.bldg();
	int i = 0;
	while(i<bldg.size()) {
		if(bldg.get(i).getType().equals("Bunker")||bldg.get(i).getType().equals("Headquarters")) numHQBunkers++;
		i++;
	}
	
	System.out.println("0");

	if(t1p.getTownTech()<=townSize) return false;
	System.out.println("1");

	if(numHQBunkers!=0) return false;
	// Not like townTech'll be less than the town size but just in case, we don't
	// want them getting a town without a town tech amount privy to it.
	 i = 0;
	
	
	i = 0;
	
	int aucontrib = 0;
	while(i<t2au.size()) {
		aucontrib+=t2au.get(i).getSize()*t2au.get(i).getExpmod(); // using
		// expmod instead so units get equal representation in terms of worth in an invasion.
		i++;
	}
	
	i = 0;
	int offau = 0;
	AttackUnit a;
	while(i<t1au.size()) {
		 a = t1au.get(i);
		offau+=a.getSize()*a.getExpmod();
		i++;
	}
	
	
	//System.out.println("Attacker has " + offau + " and defender " +(holdAttack.getTown2().res[4]+aucontrib));
	if(offau<5*(t2.getPop()+aucontrib)) return false;
	
	// no hq, no bunkers, more than five times the population in the town...
	// invasion!
	boolean townGot=false;
	// Fucking static methods make me have to reference every goddamn thing.
	System.out.println("2");

	if(t2TownSize==1) {
		// if this is the player's last town, then we need to try to get them
		// a new one.
	townGot= t2p.God.giveNewTown(t2p,-1,0,true,0,0);
// if townGot fails, then we've run out of new spaces and we send the raid
	// back with no invasion. No use taking the town and causing God to go
	// haywire over one player having no cities.
	if(!townGot) {
		
		// if the size of the town is 
		int c = 0;
		double lowSpeed = 0;
		AttackUnit g;
		// we want it weighted...so we must divide by total size*expmod...
		// because that gives us the total amount of soldier equivalents.
		int totalsize=0;
		while(c<t1au.size()) {
			 g = t1au.get(c);
		//	if(g.size>0&&g.speed<lowSpeed) lowSpeed=g.speed;
			lowSpeed+=(g.getSize()*g.getExpmod()*g.getSpeed());
			totalsize+=(g.getSize()*g.getExpmod());
			 c++;
		}
		lowSpeed/=totalsize;
		if(lowSpeed==0) {
			holdAttack.setRaidOver(true); // no need to reset ticksToHit, why return a ghostparty?
			holdAttack.deleteMe(); // no longer needed in memory, release it.
			
		} else {
			int testhold = (int) Math.round((Math.sqrt(Math.pow((holdAttack.getTown2().getX()-holdAttack.getTown1().getX()),2)+Math.pow((holdAttack.getTown2().getY()-holdAttack.getTown1().getY()),2))*10/(lowSpeed*speedadjust))/GodGenerator.gameClockFactor);
			if(testhold==0) testhold=(int) Math.round(((double) 10/(lowSpeed*speedadjust))/GodGenerator.gameClockFactor);

			holdAttack.setRaidOver(true);
			holdAttack.setTicksToHit(testhold); // sending back AUs
		}
	return false;
	} 
		
	}
	System.out.println("3");
	
	// if the code gets here, we can give the town away.
	//System.out.println("Preparing to give the town.");
	t2.giveTown(holdAttack,t1p); // give the town away.
	
	return true;
}
public static boolean debrisLogicBlock(Raid r) {
	
	/*
	 * Moves debris into the hole of the raid. 
	 */
	int ie = 0;int totalCheckedSize=0;
	while(ie<r.getAu().size()) {
		totalCheckedSize+=r.getAu().get(ie).getSize();
		// SuggestTitleVideoId
		ie++;
	}
	if(totalCheckedSize==0) {
		// this means we called getAu() for the first time before the au statements got to update and put
		// the units into the raid!
		r.setAu(null);
		r.getAu(); // reset.
	}
	moveResources(r,r.getTown2(),100,true);
	Player t1p = r.getTown1().getPlayer(); Player t2p = r.getTown2().getPlayer();
	Town t1 = r.getTown1(); Town t2 = r.getTown2();
	try {
		String unitStart=""; String unitNames="";String unitEnd="";
		int i = 0;
		while(i<r.getAu().size()) {
			unitStart+=","+r.getAu().get(i).getSize();
			unitNames+=","+r.getAu().get(i).getName();
			unitEnd+=",0";

			i++;
		}
		r.setRaidOver(true);
		r.setTicksToHit(r.getTotalTicks());
		UberStatement stmt = r.getTown1().getPlayer().God.con.createStatement();
		 stmt.execute("insert into statreports (pid,tid1,tid2,auoffst,auofffi,auoffnames,m,t,mm,f,offTownName,defTownName,debris) values" +
		      		"(" +t1p.ID + ","+ t1.townID + "," + t2.townID + ",\"" + unitStart + "\",\"" + unitEnd + "\",\"" 
		      		+ unitNames + "\","+r.getMetal() +"," + r.getTimber() + ","+r.getManmat() + "," + r.getFood() + ",'"+t1.getTownName()+"','"+t2.getTownName()+"',true);");
		   
		   stmt.close();
		
	}catch(SQLException exc) { exc.printStackTrace(); }
	return true;
}
public static boolean removeCivilianAU(Town t) { // NOBODY USES THIS SHIT NO MO.
	// This means the defenders won. Right? So now I must add them back and remove the units.
	int u = 6; // Beyond this is where civilians are.
	
	try {

	boolean found=false;
	while(u<t.getAu().size()) {
		if(t.getAu().get(u).getLotNum()!=-1){ found=true; break; }
		u++;
	}
	if(found) {
		UberStatement stmt = t.getPlayer().God.con.createStatement();
		boolean transacted=false;
		while(!transacted) {
			
		try {
		stmt.execute("start transaction");
		u=6;
		AttackUnit holdA;
		Building b;
		synchronized(t.getAu()) {
	do {
		int j = 0;
		 holdA = t.getAu().get(u);
	
		if(holdA.getLotNum()!=-1) {// it might be support, after all!
		
		do {
			 b = t.bldg().get(j);
			if(holdA.getLotNum()==b.getLotNum()) {
				b.setPeopleInside(holdA.getSize()); t.getAu().remove(u);

			if(holdA.getCivType().equals("Institute")) {
					t.getPlayer().setTotalScholars(
							t.getPlayer().getTotalScholars() + holdA.getSize());
					t.getPlayer().setTotalPopulation(
							t.getPlayer().getTotalPopulation() + holdA.getSize());
					t.getRes()[4]+=holdA.getSize();
					
				}
				
				
			else	if(holdA.getCivType().equals("Trade Center")) {
					
					t.getPlayer().setTotalPopulation(
							t.getPlayer().getTotalPopulation() + holdA.getSize());
					t.getRes()[4]+=holdA.getSize();
					
				}
			else	if(holdA.getCivType().equals("Construction Yard")) {
					
					t.getPlayer().setTotalPopulation(
							t.getPlayer().getTotalPopulation() + holdA.getSize());
					t.getRes()[4]+=holdA.getSize();
					
				}
			else	if(holdA.getCivType().equals("Communications Center")) {
					
					// May not contribute to pop but they can still be killed > hence no IGMs.
					 
				}
				 break;
			}
			j++;
		} while(j<t.bldg().size());
		// Finally reset the peopleOutside variable on bldg that cares for these numbers.
		stmt.executeUpdate("update bldg set pploutside=-1 where tid = " + t.townID + " and slot = " + holdA.getLotNum());
		}
	} while(u<t.getAu().size());
		}
	
	stmt.executeUpdate("commit;");
	stmt.close(); transacted=true;
		} catch(MySQLTransactionRollbackException exc) {
			
		}
		}

	return true;
	} else return false;
	} catch(SQLException exc) {exc.printStackTrace(); }
	return false;
}
/**
 * Returns how many people given a level.
 * @return
 */
public static int getPeople(int lvl, int unitsallowable, int resourceTypeAmt, int totalRes){
	//resourceAmt*exp(lvl+1)/((70/4)n/(unitsallowable))*(n/(unitsallowable)+1)/2)=1
	/*
	 * So how many resource types? 4? What's the total cost? 70/4 because resourceAmt
	 * is for each res so we have to divide by four so you get more in there.
	 */
	double f = unitsallowable;
	double t = ((double)totalRes)/((double) resourceTypeAmt);
	double rmt = Building.resourceAmt*Math.pow(lvl+2,2);
	
	// now we have rmt/((t/f)*n*(n/f + 1)/2) = 1
	// then we have 1/(2rmt) = 1/((t/f)*n*(n/f+1))
	// then we have 2rmt = (t/f)*n*(n/f+1)
	// then we have (t/f^2)*n^2+(t/f)*n - 2rmt = 0
	// renaming the factors on n^2 a, and n b, and the c the -2rmt, we do
	double a=0,b=0,c=0;
	if(f>0) {
	 a = t/(f*f);
	 b = t/f; }
	 c = -2*rmt;
	
	 double toRound=0;
	 if((b*b-4*a*c)>=0)
	 toRound = (-b+Math.sqrt(b*b-4*a*c))/(2*a);
	 else toRound = 0;
	int n = (int) Math.round(toRound);
//	System.out.println("When I got people, I had an a of " + a + ", a b of " + b + " and a c of " + c + " leading to " + toRound + " " +
	//		" and my lvl was " + lvl + " with unitsallowable " + unitsallowable + " and restypeamt " + resourceTypeAmt + " totalres " + totalRes);
	if(n<1) n=1;
	return n;
}
public boolean checkForGenocides(Town t) {
	// right so we now need to check for any allClear genocides.
	// True if there are incoming genocides.
	int num = 0;
	int i = 0;
	ArrayList<Raid> attackServer = t.attackServer();
	while(i<attackServer.size()) {
		if(attackServer.get(i).isGenocide()&&attackServer.get(i).isAllClear()&&!attackServer.get(i).isRaidOver()) return true;
		i++;
	}
	return false;
	/*
	try {
		UberStatement stmt = t.getPlayer().God.con.createStatement();
		ResultSet rs = stmt.executeQuery("select count(*) from raid where tid2 = " + t.townID + " and genocide = true and allClear = true and raidOver = false;");
		if(rs.next()) num = rs.getInt(1);
		rs.close();
		stmt.close();
	} catch(SQLException exc) { exc.printStackTrace(); }
	
	if(num>0) return true; else return false;*/
}
	public static boolean combatLogicBlock(Raid actattack, String combatHeader) {
	//	System.out.println("Raid ID: " + holdAttack.raidID);
		int ie = 0;int totalCheckedSize=0;
		while(ie<actattack.getAu().size()) {
			totalCheckedSize+=actattack.getAu().get(ie).getSize();
			// SuggestTitleVideoId
			ie++;
		}
		if(totalCheckedSize==0) {
			// this means we called getAu() for the first time before the au statements got to update and put
			// the units into the raid!
			actattack.setAu(null);
			actattack.getAu(); // reset.
		}
		String combatData=""; 
		Town t1 = actattack.getTown1(); Town t2 = actattack.getTown2();
		boolean isZeppAbove=false;
		int oldTownSizeArray[] = null;
		Town possZepp=null;
		if(t2.getPlayer().ID==5) {
			 possZepp = t2.getPlayer().God.findZeppelin(t2.getX(),t2.getY());
			 System.out.println("Looking for zepp with tid of " + possZepp.townID);
			if(possZepp.townID!=0&&possZepp.getPlayer().ID!=t1.getPlayer().ID) {
				// there is a zeppelin overhead of this Id town and shit,
				System.out.println("Detected zepp");
				// it's not yours!
				actattack.setTown2(possZepp);
				t2=possZepp; // now the player is attacking the Zeppelin.
			}
		} else {
			 possZepp = t2.getPlayer().God.findZeppelin(t2.getX(),t2.getY());
			if(possZepp.townID!=0) {
				// there is a zeppelin overhead of this player town, it needs to add troops!
				oldTownSizeArray = new int[6];
				int i = 0;
				isZeppAbove=true;
				i=0;
				while(i<6) {
					oldTownSizeArray[i]=t2.getAu().get(i).getSize();
					t2.setSize(i,t2.getAu().get(i).getSize()+possZepp.getAu().get(i).getSize());
					i++;
				}
			}
		}
		Player t2p = t2.getPlayer();
		Player t1p = t1.getPlayer();
		ArrayList<AttackUnit> t1au = actattack.getAu();
		ArrayList<AttackUnit> t2au = t2.getAu();
		UserRaid holdAttack = t1p.getPs().b.getUserRaid(actattack.raidID);
		String raidType = holdAttack.raidType();
		boolean genocide = false; if(actattack.isGenocide()) genocide=true;
		if(genocide) actattack.setGenoRounds(holdAttack.getGenoRounds() + 1);

		int ges = 0; 
		int totalNum = 0; // Basically just currentArmyDef but we don't want to do all the auxiliary calculations yet.
		while(ges<t2au.size()) {
			totalNum+=t2au.get(ges).getSize();
			ges++;
		}
		if(genocide&&!holdAttack.allClear()&&(t2p.God.checkForGenocides(t2)||totalNum==0))
		{
			actattack.setAllClear(true);
		//	System.out.println("I changed this guy to allClear due to other genocides in the area.");
			// in case there are already genocides on this town.
			holdAttack = t1p.getPs().b.getUserRaid(actattack.raidID);
		}
		
		int t1x = t1.getX(); int t1y = t1.getY(); int t2x = t2.getX(); int t2y = t2.getY();
		try {

		t1.getPlayer().God.Maelstrom.addWeatherEffects(t1au,t2au,t2x,t2y);
		AttackUnit.addSkinEffects(t1au);
		AttackUnit.addSkinEffects(t2au);

		} catch(Exception exc) { exc.printStackTrace(); System.out.println("Weather or skin effects caused an exception but combat was saved."); }

		
		boolean invsucc = false; 

		// in case of invasion, town2's player becomes town1's by the time
		// a report is filed, so we take care of this by recording this
		// in a variable already!
		int j =0;
		double bunkerSize=0; // getting the total number of soldiers the bunkers that are in mode 1 can hold.
		double afSize = 0;
		// recalculate this again later on to get bunkersize ofr mode 0 guys.
		//resourceAmt*exp(lvl+1)/((70/4)n/(unitsallowable))*(n/(unitsallowable)+1)/2 = 1
		//
		

		int t2pid = t2p.ID;
		UserBuilding b;
		UserBuilding t2bldg[] = t2p.getPs().b.getUserBuildings(t2.townID,"all");
		while(j<t2bldg.length) {
			 b = t2bldg[j];
			if(b.getType().equals("Bunker"))  bunkerSize+=Math.round(.33*.05*t2p.getBunkerTech()*getPeople(b.getLvl(),3,4,totalUnitPrice));
		//	else if(b.type.equals("Bunker")&&b.bunkerMode==1&&b.getLvl()>25) bunkerSize+=Math.exp(25)+(b.getLvl()-25)*Math.exp(25);
			
			j++;
		}
	
		j=0;
		long pop = t2.getPop();
		double civvybunkerfrac=((double) bunkerSize)/((double) pop);
	//	System.out.println("bunkerSize is " + bunkerSize + " res[4] is " + holdAttack.getTown2().res[4] + " civvybunkerfrac(before I check for >1 cbfs) is " +  civvybunkerfrac);
	//	System.out.println("AFSize is " + afSize);
		combatData+="bunkerSize is " + bunkerSize + " res[4] is " + pop+ " civvybunkerfrac(before I check for >1 cbfs) is " +  civvybunkerfrac;
		if(civvybunkerfrac>1) civvybunkerfrac=1; // don't want them getting over 1 in protection!
		if(holdAttack.allClear()&&t2bldg.length>0) {
			// This means the civilians are fighting. Need to add any new units.
			/*
			 * In terms of bunkers, I imagine that each time civilians are killed,
			 * the civvybunkerfrac gets even better! So it leads me to believe that..
			 * if at first you have 100 civilians in a building, with a civvyfrac of
			 * .5, then only 50 leave. Those 50 are then removed from the record and next round,
			 * civvyfrac is 1, and the 50 still do not leave the building.
			 * Right. So we are in agreement. Are we? Oh yes, we are. Hey, fuck you.
			 * Don't tell me what to fuck. :)
			 * 
			 * Well, if civvyfrac is .3, then .7 of the civilians should get released,
			 * so the REMOVAL frac is actually 1-civvybunkerfrac.
			 */
			//boolean foundCiv = false;
			//boolean foundNonZeroCiv=false;
			int u = 0;
				AttackUnit Civ; Building actb;
			while(u<t2bldg.length) {
				
			 b = t2bldg[u];
			
			if(b.getPeopleInside()>0) {
				
				int weap[] = new int[1];
				weap[0]=t2p.getCivWeapChoice(); // These are the guys being attacked.
				 Civ = new AttackUnit("Civilian", b.getLotNum(),weap, b.getType());
				Civ.setSize((int) Math.round(((double) b.getPeopleInside())*(1-civvybunkerfrac)));
		//		System.out.println("People being removed are " + ((int) Math.round(((double) b.getPeopleInside())*(1-civvybunkerfrac))));
			//	System.out.println("civvybunker is " + civvybunkerfrac + " and it takes " + (1-civvybunkerfrac));
				if(Civ.getCivType().equals("Institute")) {
					Civ.setName("Scholar");
					
				}
				if(Civ.getCivType().equals("Trade Center")) {
					Civ.setName("Trader");
					
				}
				if(Civ.getCivType().equals("Construction Yard")) {
					Civ.setName("Engineer");
					
				}
				

			//	b.setPeopleInside((int) Math.round(((double) b.getPeopleInside())*civvybunkerfrac));
				//foundCiv = true;
				//if(Civ.getSize()>0) foundNonZeroCiv = true;
			//	holdAttack.getTown2().addUnitType(Civ);
				t2au.add(Civ);
				Civ.setSlot(t2au.size()-1);
				
			}
			
			
			u++;
			}
			
		
		}


	
			// Now we initiate the attack and remove the attack from the queue, posting a status report.
			
			// First we must calculate pre-concealment factor for both.
			// Requires asking for player2's units.
			/*
			 * C = (HU_Concealment-Sigma(i, (#Ui/HU)*Ui_concealment))*Exp(-Curr_Army_Size/((HU_Concealment-Sigma(i, (#Ui/HU)*Ui_concealment))*Exp(StealthTechLvl)*10)) 
			 * 
			 */
			// Looks like we may need to upgrade player infrastructure, so each player
			// has units at base so we can inquire of them easily. 
	
			int k = 0;
			int currentArmySize = 0;
			int currentExpAdvSizeOff = 0; boolean found = false;
			int currentArmySizePop=0;
			int currentExpAdvSizeOffWithDivMods =0; // So this is the expmod but with
			// factors included for concealment like 2x on tank expmods so tanks
			// are 2x as hard to hide as their 10 soldier equiv(seen as 20 soldiers.)
			// This way they get to account for the fact that they get 2x the speed.
			AttackUnit au;
			while(k<t1au.size()) {
				 au = t1au.get(k);
				 
				 if(au.getSize()>0)
				 switch(au.getPopSize()) {
					case 1:
						currentExpAdvSizeOffWithDivMods+=au.getExpmod()*au.getSize();
						break;
					case 5:
						currentExpAdvSizeOffWithDivMods+=au.getExpmod()*au.getSize();
						break;
					case 10:
						currentExpAdvSizeOffWithDivMods+=au.getExpmod()*au.getSize();
						break;
					case 20:
						currentExpAdvSizeOffWithDivMods+=(int) Math.round(au.getExpmod()*au.getSize());
						break;
			
					}
				currentArmySize+=au.getSize();
				
				currentArmySizePop+=au.getSize()*au.getPopSize();
				currentExpAdvSizeOff+=au.getSize()*au.getExpmod();
				k++;
			} 
			
			// Found the highest concealment unit.
			
			// Now I need to add up all the other units concealment times their ratios to the highest one.
			//IF YOU CHANGE THIS CONCEALMENT CODE, CHANGE SCOUTS ALSO
			k = 0;
			double sigmaTerm = 0;
			 while(k<t1au.size()) {
				 au = t1au.get(k);
					double moddedother=0;
					switch(au.getPopSize()) {
					case 1:
						moddedother=au.getConcealment();
						break;
					case 5:
						moddedother=au.getConcealment()/2;
						break;
					case 10:
						moddedother=au.getConcealment()/4;
						break;
					case 20:
						moddedother=au.getConcealment()/.5;
						break;
					}
					if(currentArmySize>0) // gotta do it this way, what if there is one juggernaught?
						// then size+1 makes 1/2 is half.
					sigmaTerm+=((moddedother)*au.getSize()/(currentArmySize));
				
				k++;
			}
		
			 if(sigmaTerm<0) sigmaTerm=0;
			// Now I need the exponential term, which includes the sigma term a little.
			 combatData+=("Inc.AdvSizeWithMod: " + currentExpAdvSizeOffWithDivMods + " Stealth is " + Math.round(t1p.getStealth()) + " townPop: " + (pop+1));
				//double expTerm1 = (sigmaTerm*holdAttack.town1.getStealth()*10)/currentArmySize;
				// it takes twice as much concealment to launch a good offensive attack than a defensive one.
				// Can be fixed later but would be a good rule of thumb: Twice the concealment for offense.
				// Not what I'd prefer, but it basically means that the army you can attack with at a certain concealment rating on a town
				// of similar population is half the army you can defend with and get the same rating. It also means
				// attacking armies that are large relative to the village size will suffer for it, leading to small armies against
				// small villages, large armies against large villages, and a need to reconnoiter everything. Cool!
				// Bunker system alleviates the low concealment problem - there is a defensive advantage to having
				// those.
				
				//IF YOU CHANGE THIS CONCEALMENT CODE, CHANGE SCOUTS ALSO

				 k = 0;
				int currentExpAdvSizeDef = 0; found = false;
				int currentArmySizedef = 0;
				int currentArmySizeDefPop=0;
				int currentExpAdvSizeDefWithDivMods=0;
				while(k<t2au.size()) {
					
					 au = t2au.get(k);
					 switch(au.getPopSize()) {
						case 1:
							currentExpAdvSizeDefWithDivMods+=au.getExpmod()*au.getSize();
							break;
						case 5:
							currentExpAdvSizeDefWithDivMods+=au.getExpmod()*au.getSize();
							break;
						case 10:
							currentExpAdvSizeDefWithDivMods+=au.getExpmod()*au.getSize();
							break;
						case 20:
							currentExpAdvSizeDefWithDivMods+=(int) Math.round(au.getExpmod()*au.getSize());
							break;
				
						}
					currentArmySizedef+=au.getSize();
					currentExpAdvSizeDef+=au.getSize()*au.getExpmod();
					currentArmySizeDefPop+=au.getSize()*au.getPopSize();
					k++;
				} 
				// at this point we know currentArmySizedef. So we put the if UberStatement here for genocide.
				
				combatData+=("ExpAdvSizeDef: " + currentExpAdvSizeDef + " ExpAdvSizeOff:" + currentExpAdvSizeOff);
				
				
				
				int numUnitsRemainingD=0;
				boolean resFlag=true; // for standoffs, which are rare under new regime.
				//IF YOU CHANGE THIS CONCEALMENT CODE, CHANGE SCOUTS ALSO

				
			
				// Found the highest concealment unit.
				
				// Now I need to add up all the other units concealment times their ratios to the highest one.
				
				k = 0;
				
				double sigmaTermdef = 0;
				while(k<t2au.size()) {
					 au = t2au.get(k);
				
						double moddedother=0;
						switch(au.getPopSize()) {
						case 1:
							moddedother=au.getConcealment();
							break;
						case 5:
							moddedother=au.getConcealment()/2;
							break;
						case 10:
							moddedother=au.getConcealment()/4;
							break;
						case 20:
							moddedother=au.getConcealment()/.5;
							break;
						}
						if(currentArmySizedef>0)
						sigmaTermdef+=((moddedother)*au.getSize()/(currentArmySizedef));
					
					
					k++;
				} 
			
				 if(sigmaTermdef<0) sigmaTermdef=0;
	//			 System.out.println("sigmaTermDef" + sigmaTermdef);
					//IF YOU CHANGE THIS CONCEALMENT CODE, CHANGE SCOUTS ALSO

				// Now I need the exponential term, which includes the sigma term a little.
			
				 combatData+=("Def.AdvSizeWithMod: " + currentExpAdvSizeDefWithDivMods + " Stealth is " + Math.round(t2p.getStealth()));
				 j=0; int totalPoppedUnits=0;
				 ArrayList<AttackUnit> t2pau = t2p.getAu();
					while(j<t2pau.size()) {
						if(!t2pau.get(j).getName().equals("empty")&&!t2pau.get(j).getName().equals("locked")) totalPoppedUnits++;
						j++;
					}
					int t2townsize = t2p.towns().size();
				 double bldglvl = getAverageLevel(t2);
				 combatHeader+=" Actual offensive army size equivalent: " + currentExpAdvSizeOff + ", defensive: " + currentExpAdvSizeDef + ". Cover size equivalent for offensive: " + currentExpAdvSizeOffWithDivMods + ", defensive: " + currentExpAdvSizeDefWithDivMods + ".";
				 combatHeader+=" Cover soft limit of " 
				 + Math.round(4*Math.sqrt(6)*(bldglvl+1)*t2townsize*totalPoppedUnits) + ".";
				 // used to use currentArmySizeDef in expTerm calcs, but this gives bigger units more concealment.
				double expTerm2 = Math.exp(-(currentExpAdvSizeDefWithDivMods+1)/(4*Math.sqrt(6)*(bldglvl+1)*t2townsize*totalPoppedUnits*8+1));
				double expTerm1 = Math.exp(-(currentExpAdvSizeOffWithDivMods+1)/(4*Math.sqrt(6)*(bldglvl+1)*t2townsize*totalPoppedUnits*8+1));

				// double expTerm2 = (sigmaTermdef*holdAttack.getTown2().getStealth()*holdAttack.getTown2().getPop())/currentArmySizedef;
				//IF YOU CHANGE THIS CONCEALMENT CODE, CHANGE SCOUTS ALSO

				 combatData+="sigmaTerm: " + Math.round(sigmaTerm)+ ", sigmaTermdef: " + Math.round(sigmaTermdef);
			
			// Now put it all together for pre-combat concealment!
			//	System.out.println(sigmaTermdef + " is sigmaTermDef, expTerm2 is " + expTerm2 + " expTerm 1 is" +expTerm1);
			//	double Ca = (sigmaTerm+1)*expTerm1+1;
			//	double Cd = (sigmaTermdef+1)*expTerm2+1;
				
				combatData+="ExpTerm1: " + expTerm1 + " ExpTerm2: " + expTerm2; //+ " Ca: " + Math.round(Ca) + " Cd: " + Math.round(Cd);
				if(expTerm1<(.035*t1p.getStealthTech())) {
			//	combatHeader+=" Due to the Alamo effect, the offense would be getting a " + (100-Math.round(expTerm1*100)) + "% decrement" +
				//		" to their attributes, but this was above the " + (100-Math.round(100*.035*holdAttack.town1.getPlayer().stealthTech)) + "% limit " +
					//			" dictated by their stealth Tech, so it was set to this limit.";
				expTerm1=.035*t1p.getStealthTech();

				}
				try {
				expTerm1=AttackUnit.getNewCSL(t1au, expTerm1);
				} catch(Exception exc) { exc.printStackTrace(); System.out.println("CSL skin effect had trouble, combat saved though.");}
				combatHeader +=" Accounting for how much cover there was, the offense received a " + (100-Math.round(expTerm1*100)) + "% decrement to their attributes,";
				
				if(expTerm2<(.035*t2p.getStealthTech())) {
			//		combatHeader+=" Due to the Alamo effect, the defense would be getting a " + (100-Math.round(expTerm2*100)) + "% decrement" +
				//			" to their attributes, but this was above the " + (100-Math.round(100*.035*holdAttack.getTown2().getPlayer().stealthTech)) + "% limit " +
					//				" dictated by their stealth Tech, so it was set to this limit.";
				expTerm2=.035*t2p.getStealthTech();
				}
				try {
				expTerm2=AttackUnit.getNewCSL(t2au, expTerm2);
				} catch(Exception exc) { exc.printStackTrace(); System.out.println("CSL skin effect had trouble, combat saved though.");}

				combatHeader +=" and the defense received a " + (100-Math.round(expTerm2*100)) + "% decrement to their attributes.";

				
				
				//IF YOU CHANGE THIS CONCEALMENT CODE, CHANGE SCOUTS ALSO

				// Now we do the actual fight calculations.
				
				 
				 j=0;
				 bunkerSize=0; // getting the total number of soldiers the bunkers that are in mode 0 can hold.
			
				while(j<t2bldg.length) {
					 b = t2bldg[j];
					if(b.getType().equals("Bunker"))  bunkerSize+=Math.round(.33*.05*t2p.getBunkerTech()*getPeople(b.getLvl(),totalPoppedUnits,4,totalUnitPrice/(t2p.towns().size()))); // because unit prices adjusts, remember?
					//else if(b.type.equals("Bunker")&&b.bunkerMode==0&&b.getLvl()>25) bunkerSize+=Math.exp(25)+(b.getLvl()-25)*Math.exp(25);
					
					j++;
				}
				j=0; int totalPoppedUnitsOff=0;
				ArrayList<AttackUnit> t1pau = t1p.getAu();
				while(j<t1pau.size()) {
					if(!t1pau.get(j).getName().equals("empty")&&!t1pau.get(j).getName().equals("locked")) totalPoppedUnitsOff++;
					j++;
				}
				afSize = 0;
				j=0;
				UserBuilding t1bldg[] = t1p.getPs().b.getUserBuildings(t1.townID,"Arms Factory");
				while(j<t1bldg.length) {
					 b = t1bldg[j];
					if(b.getType().equals("Arms Factory"))  afSize+=Math.round(.05*t1p.getAfTech()*getPeople(b.getLvl(),totalPoppedUnitsOff,4,totalUnitPrice/t1p.towns().size())); // add back the lvl<25 if you change!
				//	else if(b.type.equals("Arms Factory")&&b.getLvl()>25) afSize+=Math.exp(25)+(b.getLvl()-25)*Math.exp(25);
					
					j++;
				}
				
				k = 0;
				double totalHPOff=0,totalPopOff=0,totalHPDef=0,totalPopDef=0;
				String offNames = "";
				String offUnitsBefore = "";
				int offsize = 0, defsize = 0;
				AttackUnit holdUnit;
				 while(k<t1au.size()) {
					 holdUnit = t1au.get(k);
					totalHPOff += holdUnit.getHp()*holdUnit.getSize();
					totalPopOff += holdUnit.getSize()*holdUnit.getPopSize(); // Need to change pop settings. 
					if(holdUnit.getSize()>0) offsize++;
					offNames += ","+ holdUnit.getName();
					offUnitsBefore+= "," + holdUnit.getSize();
					k++;
				}
				
				k = 0;
				String defNames = "";
				String defUnitsBefore = "";
				while(k<t2au.size()) {
					 holdUnit = t2au.get(k);
					totalPopDef += holdUnit.getSize()*holdUnit.getPopSize(); // All units have population 1 right now, need to change this.
					totalHPDef += holdUnit.getHp()*holdUnit.getSize();
					defNames += ","+ holdUnit.getName();
					defUnitsBefore += "," + holdUnit.getSize();
					if(holdUnit.getSize()>0) defsize++;

					k++;
				} 
				
				
				double holdHPLostDef[] = new double[t2au.size()]; // you need to make it the size that
				double holdHPLostOff[] = new double[t1au.size()];
				k = 0;  j = 0;
				int counter = 0;
				double defrand = Math.random();
				boolean defensiveDefInc = false; // this is the veterancy decider variable - is it a defensive or offensive increase?
				// if true, the defenders get a defensive increase.
				
				if(defrand>.5) defensiveDefInc = true;
				
				double offrand = Math.random();
				boolean offensiveDefInc = false; // if true, the offensive team gets a defensive increase.
		

				double armysizefrac=((double) bunkerSize)/((double) currentExpAdvSizeDef+1); // used with bunker size of mode 0s.
				
				if(armysizefrac>1)armysizefrac=1; // don't want >1 armysizefracs.
				///bunkerfrac=(1-.05*bunkerTech*armysizefrac)
				double bunkerfrac = (1-.05*((double) t2p.getBunkerTech())*armysizefrac);
				
				combatHeader+="The defenses bunker provided an average protection of " + Math.round(-((bunkerfrac-1)*100)) + "%.";
				combatData+="armysizefrac: " + armysizefrac;
				if(offrand>.5) offensiveDefInc=true;
			//	System.out.println("Total expmod pop for t1 is " + getTotalPopWithExpMod(holdAttack.town1.getPlayer()));
			//	System.out.println("Total expmod pop for t2 is " + getTotalPopWithExpMod(holdAttack.getTown2().getPlayer()));
				double averageVetOff[] = new double[t1au.size()];
				double averageVetDef[] = new double[t2au.size()];
				double averageVetOffHits[] =new double[t1au.size()];
				double averageVetDefHits[] =new double[t2au.size()];

				AttackUnit off,def;
				while(k<t2au.size()) {
					 def = t2au.get(k);
					double differentialfrac = def.getSize()*def.getPopSize()/(totalPopDef+1); // so damage gets distributed to this
					// army component proportional to the size of which it makes up the enemy army, not just divided evenly
					// by six if there are six components, say.

					while(j<t1au.size()) {
						//size*population*(acc/Cd)(fp/armor)(ammo/speed)
						 off = t1au.get(j);
						if(def.getSize()>0&&off.getSize()>0) {// May get divide by zeroes otherwise.
							
						// below is veterancy block.
						// gets offensive's random, defensive's random
						// if offensive is getting an offensive lead and defensive
						// is getting a defensive lead then the two advantages are combined and
						// subtracted from one to get the full extent of the damage on the
						// defending units.
						offrand = Math.random(); defrand = Math.random();
						double offensiveSubtractor = 0; double defensiveIncreaser = 0;
						if(off.getLotNum()==-1)
						if(off.getSupport()==0) {
							if(!offensiveDefInc) offensiveSubtractor=offrand*.5*off.getExpmod()*GodGenerator.getTotalSize(off,t1p)
							/(getTotalPopWithExpMod(t1p)+1); 
							try {
							averageVetOff[j]+=offensiveSubtractor;
							averageVetOffHits[j]++;
							} catch(Exception exc) { exc.printStackTrace(); System.out.println("Combat was saved."); }

							}
						else if(off.getSupport()>0) {
							if(!offensiveDefInc) offensiveSubtractor=offrand*.5*off.getExpmod()*GodGenerator.getTotalSize(off,off.getOriginalPlayer())
							/(getTotalPopWithExpMod(off.getOriginalPlayer())+1); 
							try {
							averageVetOff[j]+=offensiveSubtractor;
							averageVetOffHits[j]++;
							} catch(Exception exc) { exc.printStackTrace(); System.out.println("Combat was saved."); }

							}
						if(def.getLotNum()==-1) // lotnums are for civvies which do no veterancy.
						if(def.getSupport()==0) {
							if(defensiveDefInc) defensiveIncreaser = defrand*.5*def.getExpmod()*GodGenerator.getTotalSize(def,t2p)
							/(getTotalPopWithExpMod(t2p)+1); 		
							try {
							averageVetDef[k]+=defensiveIncreaser;
							averageVetDefHits[k]++;
							} catch(Exception exc) { exc.printStackTrace(); System.out.println("Combat was saved."); }

						}
						else if(def.getSupport()>0) {
							if(defensiveDefInc) defensiveIncreaser = defrand*.5*def.getExpmod()*GodGenerator.getTotalSize(def,def.getOriginalPlayer())
							/(getTotalPopWithExpMod(def.getOriginalPlayer())+1);
							try {
							averageVetDef[k]+=defensiveIncreaser;
							averageVetDefHits[k]++;
							} catch(Exception exc) { exc.printStackTrace(); System.out.println("Combat was saved."); }

						}
						// So we look at original Player's pop if it's an offsupport unit!
						double veterancyfrac = 1-defensiveIncreaser+offensiveSubtractor;
					//	System.out.println("defensiveIncreaser for " + def.name + " is " + defensiveIncreaser);
					//	System.out.println("offensiveSub for " + off.name + " is " + offensiveSubtractor);
						combatData+="defensiveIncreaser for " + def.getName() + " is " + defensiveIncreaser;
						combatData+="offensiveSub for " + off.getName() + " is " + offensiveSubtractor;
						// defending unit ALWAYS subtracts because no matter who is defending, they want the lowest
						// veterancy frac.
						// defensive Increaser removes from veternacyFrac because this hits the HP LOST
						// not HP KEPT so it lowers the hp that the defensive player's units will lose.
						// veterancyfrac is 1 if no def or off.
							
							
							
						double weightedfrac = off.getSize()*off.getPopSize()/(totalPopOff+1); // to give it proper weight.
						double holdfracs[] = new double[3];
					//	System.out.println("fp is " + off.firepower + " armor is " + def.armor);
						combatData+="fp is " + off.getFirepower() + " armor is " + def.getArmor();
						 // basically we make it so factors of 26000 don't occur when Cd is low.
						
						if(off.getAccuracy()>0)
						holdfracs[0] = (off.getAccuracy()/(def.getConcealment()*(expTerm2)+1));
						else holdfracs[0]=(unarmedAmt)/(def.getConcealment()*expTerm2+1);
							 
						if(off.getFirepower()>0)
						holdfracs[1] = (off.getFirepower()/(def.getArmor()*expTerm2+1));
						else holdfracs[1]=(unarmedAmt)/(def.getArmor()*expTerm2+1);
						
						if(off.getAmmo()>0)
						 holdfracs[2] = (off.getAmmo()/(def.getSpeed()*expTerm2+1));
						else 
						 holdfracs[2] = (unarmedAmt/(def.getSpeed()*expTerm2+1));

						 double maxfrac=0;
						 int g = 0;
						 while(g<holdfracs.length) {
							 maxfrac += holdfracs[g]/3.0;
							combatData+="off on def frac " + g + " is " + holdfracs[g];

							 g++;
						 }
						 double fero =1;
						 if(t1p.getFeroTimer()>0) fero=1.1;
							combatData+="For def HP weighted frac is " + weightedfrac + " diff is " + differentialfrac + " size is " + off.getSize()
									+ " popsize is " + off.getPopSize() + " veterancyfrac is " + veterancyfrac + " defensiveInc " +
									defensiveIncreaser + " offSub " + offensiveSubtractor + " bunkerfrac is " + bunkerfrac+ " maxfrac is " + maxfrac + " fero increase is " + fero;
						holdHPLostDef[k] += .6*weightedfrac*100*off.getSize()*off.getPopSize()*maxfrac*differentialfrac*veterancyfrac*bunkerfrac*fero;
						if(holdHPLostDef[k]<0) holdHPLostDef[k]=0;
						// 100 is base unit of health here, so it works like so.
						//.5 is there so that when everything is evened out, only 50% casualties,
						// with 100% occuring against stronger(rel 2 maxfrac) battles.
						}
						j++;
					}
					
				//	holdHPLostDef[k]=holdHPLostDef[k]*Math.exp(.5*totalPopOff/totalPopDef)/Math.exp(1); // apply exponential advantage.
					
					j=0;
					k++;
				}
				// so first we calculate every hplostdef and every hplostoff.
				combatData+=("t2s pop:" + t2p.getTotalPopulation() + " t1s pop:" + t1p.getTotalPopulation());
				k = 0;  j = 0;
				 counter = 0;
				 double armysizeofffrac=((double) afSize)/((double) currentExpAdvSizeOff+1); // used with bunker size of mode 0s.
					
					if(armysizeofffrac>1)armysizeofffrac=1; // don't want >1 armysizefracs.
					///bunkerfrac=(1-.05*bunkerTech*armysizefrac)
					double affrac = (1-.05*((double) t1p.getAfTech())*armysizeofffrac); 
					combatHeader+="The attackers Arms Factories conferred a " + Math.round((-100*(affrac-1))) + "% advantage average on their troops.";
				
					while(k<t1au.size()) {
					 def = t1au.get(k);
					double differentialfrac = def.getSize()*def.getPopSize()/(totalPopOff+1); // so damage gets distributed to this

					while(j<t2au.size()) {
						//size*population*(acc/Cd)(fp/armor)(ammo/speed)
						 off = t2au.get(j);
						if(def.getSize()>0&&off.getSize()>0) {// May get divide by zeroes otherwise.
							
							
							offrand = Math.random(); defrand = Math.random();
							double offensiveIncreaser = 0; double defensiveDecreaser = 0;
							// because now off is def!
							
							// now if offensiveDefInc is on that means the offenders
							// need a defensive increase which happens when an offensiveIncreaser
							// is calculated and subtracted from veterancyfrac since vf is prop. to
							// damage done.
							// defensive is now the offenders here so their units are off, but their
							// boolean, which defDefInc is false if they are getting an offensive increase,
							//will want to increase vetfrac and use the off unit.
							if(offensiveDefInc&&def.getLotNum()==-1)
							if(offensiveDefInc&&def.getSupport()==0) {offensiveIncreaser=defrand*.5*def.getExpmod()*GodGenerator.getTotalSize(def,t1p)
							/(getTotalPopWithExpMod(t1p)+1);
							try {
							averageVetDef[k]+=offensiveIncreaser;
							averageVetDefHits[k]++;
							} catch(Exception exc) { exc.printStackTrace(); System.out.println("Combat was saved."); }
							}
							else if(offensiveDefInc&&def.getSupport()>0) { offensiveIncreaser=defrand*.5*def.getExpmod()*GodGenerator.getTotalSize(def,def.getOriginalPlayer())
							/(getTotalPopWithExpMod(def.getOriginalPlayer())+1);
							try {
							averageVetDef[k]+=offensiveIncreaser;
							averageVetDefHits[k]++;
							} catch(Exception exc) { exc.printStackTrace(); System.out.println("Combat was saved."); }

							}
							if(!defensiveDefInc&&off.getLotNum()==-1)
							if(!defensiveDefInc&&off.getSupport()==0) {defensiveDecreaser = offrand*.5*off.getExpmod()*GodGenerator.getTotalSize(off,t2p)
							/(getTotalPopWithExpMod(t2p)+1);
							try {
							averageVetOff[j]+=defensiveDecreaser;
							averageVetOffHits[j]++;
							} catch(Exception exc) { exc.printStackTrace(); System.out.println("Combat was saved."); }

							}
							else if(!defensiveDefInc&&off.getSupport()>0) {defensiveDecreaser = offrand*.5*off.getExpmod()*GodGenerator.getTotalSize(off,off.getOriginalPlayer())
							/(getTotalPopWithExpMod(off.getOriginalPlayer())+1);
							try {
							averageVetOff[j]+=defensiveDecreaser;
							averageVetOffHits[j]++;
							} catch(Exception exc) { exc.printStackTrace(); System.out.println("Combat was saved."); }

							}
							
						//	System.out.println("For defensiveSub, Total units "+ GodGenerator.getTotalSize(def,holdAttack.getTown2().getPlayer()));
						//	System.out.println(" and total pop is " +getTotalPopWithExpMod(holdAttack.getTown2().getPlayer()) + " defensiveDefInc is " +defensiveDefInc);
							double veterancyfrac = 1+defensiveDecreaser-offensiveIncreaser;

						//	System.out.println("defensiveDecreaser for " + off.name + " is " + defensiveDecreaser);
						//	System.out.println("offensiveInc for " + def.name + " is " + offensiveIncreaser);
							// veterancyfrac is 1 if no def or off. decreaser is added because what we're
							// figuring below is HP LOST not HP KEPT.
							
							
							double weightedfrac = off.getSize()*off.getPopSize()/(totalPopDef+1); // to give it proper weight.
							double holdfracs[] = new double[3];
						//	System.out.println("fp is " + off.firepower + " armor is " + def.armor);
							
							if(off.getAccuracy()>0)
								holdfracs[0] = (off.getAccuracy()/(def.getConcealment()*expTerm1+1));
								else holdfracs[0]=(unarmedAmt)/(def.getConcealment()*expTerm1+1);
							
							// holdfracs[0] = (off.accuracy/(Ca+1)); 
								if(off.getFirepower()>0)
							holdfracs[1] = (off.getFirepower()/(def.getArmor()*expTerm1+1));
								else
							holdfracs[1] = (unarmedAmt/(def.getArmor()*expTerm1+1));

								if(off.getAmmo()>0)
							 holdfracs[2] = (off.getAmmo()/(def.getSpeed()*expTerm1+1));
								else
							 holdfracs[2] = (unarmedAmt/(def.getSpeed()*expTerm1+1));

							 double maxfrac=0;
							 int g = 0;
							 while(g<holdfracs.length) {
								  maxfrac += holdfracs[g]/3.0;
								 combatData+=("def on off frac " + g + " is " + holdfracs[g]);
								 g++;
							 }
							 
							 /*
							  * def on off frac 0 is 5.471698113839964def on off frac 1 is 5.7254901960784315def on off frac 2 is 5.7254901960784315
							  * For off HP weighted frac is 0.9090909090909091 diff is 0.975609756097561 size is 1 popsize is 10 veterancyfrac is 1.0 
							  * defensiveSub 0.0 offInc 0.0 affrac is  0.9721904649370162
							  */
							 double fero =1;
							 if(t2p.getFeroTimer()>0) fero=1.1;
							combatData+="For off HP weighted frac is " + weightedfrac + " diff is " + differentialfrac + " size is " + off.getSize()
									+ " popsize is " + off.getPopSize()+ " veterancyfrac is " + veterancyfrac + " defensiveSub " +
									defensiveDecreaser + " offInc " + offensiveIncreaser + " affrac is  "+ affrac + " maxfrac is " + maxfrac + " fero increase is " + fero;
							holdHPLostOff[k] += .6*weightedfrac*100*off.getSize()*off.getPopSize()*maxfrac*differentialfrac*veterancyfrac*affrac*fero;
							// 100 is base unit of health here, so it works like so. 
							if(holdHPLostOff[k]<0) { holdHPLostOff[k]=0; //System.out.println(k+"th was below 0 in HP, briefly.");
							}

						}
					
						j++;
					}
				//	holdHPLostOff[k]=holdHPLostOff[k]*Math.exp(.5*totalPopDef/totalPopOff)/Math.exp(1);
					j=0;
					k++;
				}
				
	
					// so we know that for each unit, we put in entries for each attacking unit,
			
				try {
				k = 0;
				AttackUnit a=null;
				while(k<averageVetOff.length) {
					if(averageVetOffHits[k]>0)
						averageVetOff[k]/=averageVetOffHits[k];
					
						a = t1au.get(k);
						
					if(a.getSize()>0) // Don't show for units not there.
					combatHeader+=a.getName() + " got a " + Math.round(averageVetOff[k]*100) + " % average veterancy advantage. ";
					k++;
				}
				k = 0;
				while(k<averageVetDef.length) {
					if(averageVetDefHits[k]>0)
						averageVetDef[k]/=averageVetDefHits[k];
					
						a = t2au.get(k);
						
					if(a.getSize()>0)
					combatHeader+=a.getName() + " got a " + Math.round(averageVetDef[k]*100) + " % average veterancy advantage. ";

					
					k++;
				}
				
					
				} catch(Exception exc) { exc.printStackTrace(); System.out.println("Tried to make veterancy header. Combat was saved."); }
				double totalOffLost = 0, totalDefLost = 0;

			
				
				k = 0;
				double offExpAdv=0,defExpAdv=0;
				 offExpAdv =Math.exp(.1*(1-((double) currentExpAdvSizeOff)/((double) (currentExpAdvSizeDef+1))))*100;
				
				 defExpAdv = Math.exp(.1*(1-((double) currentExpAdvSizeDef)/((double) (currentExpAdvSizeOff+1))))*100;
			//currentExpAdvSizeDef is 40 currentExpAdvSizeOff is 30
				combatHeader+="The exponential advantage increase on hp lost on offense was " + Math.round((100-offExpAdv)) + "% and on defense was " + Math.round((100-defExpAdv)) + "%.";
				while(k<holdHPLostOff.length) {
					totalOffLost+=holdHPLostOff[k];
					if(currentExpAdvSizeDef>0)
					combatData+=(k + "th lost " + Math.round(holdHPLostOff[k]) + " bef adv " + Math.round(holdHPLostOff[k]*Math.exp(.1*(1-((double) currentExpAdvSizeOff)/((double) (currentExpAdvSizeDef+1))))) + "hp aft.");
					else 
						combatData+=(k + "th lost " + holdHPLostOff[k] + " before exp advantage and 0 or unknown amount of hp.");
					k++;
				}
				k = 0;
				while(k<holdHPLostDef.length) {
					totalDefLost+=holdHPLostDef[k];
					if(currentExpAdvSizeOff>0)
					combatData+=(k + "th lost " + Math.round(holdHPLostDef[k]) + " bef adv " +  Math.round(holdHPLostDef[k]*Math.exp(.1*(1-((double) currentExpAdvSizeDef)/((double) (currentExpAdvSizeOff+1))))) + "hp aft.");
					else 
						combatData+=(k + "th lost " + holdHPLostDef[k] + " before exp advantage and 0 or unknown amount of hp.");

					k++;
				}

				k=0;
				String offUnitsAfter = "";
				int numUnitsRemainingO=0;
				int totalRemainingBefA=0;
				int expModOffAft = 0;
				 counter = 0;
				long totalCost[] = new long[5];
				long particularCost[] = null;
				 while(k<t1au.size()) {
					 holdUnit = t1au.get(k); // DON'T USE T1AU, IT DOESN'T CHANGE WHEN WE USE THE SETTER METHODS!
					 // THAT'S ONLY USEFUL FOR THE UPPER ATTACK CODE WHERE IT DOES CALCULATIONS!
						double holdHP = holdUnit.getSize()*holdUnit.getHp();
						double holdOld = holdUnit.getSize();
						if(currentExpAdvSizeDef>0) // if it's 0, we're not losing hp anyway, and this is a div/0 error.
						holdHP-=holdHPLostOff[k]*Math.exp(.1*(1-((double) currentExpAdvSizeOff)/((double) (currentExpAdvSizeDef+1)))); 
						if(holdUnit.getHp()>0)
						actattack.setSize(k,(int) Math.round(((double) holdHP)/(holdUnit.getHp())));
						//holdUnit.setSize((int) Math.round(((double) holdHP)/(holdUnit.getHp())));
						
						if(holdUnit.getSize()<0) {
							actattack.setSize(k,0); // we must use the raid's setters
							//holdUnit.setSize(0);
						}
						numUnitsRemainingO+=holdUnit.getSize();
						expModOffAft+=(holdOld-holdUnit.getSize())*holdUnit.getExpmod();
						
						//t1.removeAU(holdUnit.getSlot(),(int) holdOld-holdUnit.getSize()); // This lowers the population of the player accordingly.
						offUnitsAfter+= "," + (int) (holdOld-holdUnit.getSize());
						if(k<6)
						particularCost = t1p.getPs().b.returnPrice(holdUnit.getName(),(int) (holdOld-holdUnit.getSize()),t1.townID);
						else{
							try {
								particularCost = holdUnit.getOriginalPlayer().getPs().b.returnPrice(holdUnit.getName(),(int) (holdOld-holdUnit.getSize()),holdUnit.getOriginalPlayer().towns().get(0).townID);
								}catch(Exception exc) { exc.printStackTrace(); System.out.println("Combat was saved, though."); }
							}

							
						int l = 0;
						
						while(l<totalCost.length-1) {
							totalCost[l]+=particularCost[l];
							l++;
						}
					k++;
				} 
				
				
				k=0;
				String defUnitsAfter = "";
				numUnitsRemainingD=0; int totalRemainingBefD = 0;
				int expModDefAft = 0; 
				ArrayList<Building> bldg;
				Building actb;
				while(k<t2au.size()) {
					 holdUnit = t2au.get(k);
					double holdHP = holdUnit.getSize()*holdUnit.getHp();
					double holdOld = holdUnit.getSize();
					holdHP-=holdHPLostDef[k]*Math.exp(.1*(1-((double) currentExpAdvSizeDef)/((double) (currentExpAdvSizeOff+1))));
					
					if(holdUnit.getLotNum()==-1) {
					if(holdUnit.getHp()>0){
					t2.setSize(k,(int) Math.round(((double) holdHP)/(holdUnit.getHp())));
				//	holdUnit.setSize((int) Math.round(((double) holdHP)/(holdUnit.getHp())));
					
					}
					if(holdUnit.getSize()<0) {
						t2.setSize(k,0);
					//	holdUnit.setSize(0); // in case they are zero.
					}
					} else { // THIS MEANS THIS IS A CIVVY UNIT, SO WE DETRACT FROM HIS BUILDING!
						int i = 0;
						if(holdUnit.getHp()>0){
							holdUnit.setSize((int) Math.round(((double) holdHP)/(holdUnit.getHp())));
							
							}
							if(holdUnit.getSize()<0) {
								holdUnit.setSize(0); // in case they are zero.
							}
							
						while(i<t2bldg.length) {
							b = t2bldg[i];
							actb = t2.findBuilding(b.getBid());
							if(b.getLotNum()==holdUnit.getLotNum()) actb.setPeopleInside((int) Math.round(b.getPeopleInside()-(holdOld-holdUnit.getSize())));
							i++;
						}
						
					
					}
					
					numUnitsRemainingD+=holdUnit.getSize();
					expModDefAft+=(holdOld-holdUnit.getSize())*holdUnit.getExpmod(); // this really means what was LOST. 
					
				//	t2.removeAU(holdUnit.getSlot(),(int) holdOld-holdUnit.getSize()); 
						 
					defUnitsAfter += "," + (int) (holdOld-holdUnit.getSize());
					if(k<6)
						particularCost = t2p.getPs().b.returnPrice(holdUnit.getName(),(int) (holdOld-holdUnit.getSize()),t2.townID);
						else{
							try {
							particularCost = holdUnit.getOriginalPlayer().getPs().b.returnPrice(holdUnit.getName(),(int) (holdOld-holdUnit.getSize()),holdUnit.getOriginalPlayer().towns().get(0).townID);
							}catch(Exception exc) { exc.printStackTrace(); System.out.println("Combat was saved, though."); }
						}
					int l = 0;
					while(l<totalCost.length-1) {
						totalCost[l]+=particularCost[l];
						l++;
					}
					
					// now we add back the Zeppelin units.

					if(k<6&&isZeppAbove) {
						
						// so if I have x units and y of those units were originally town units, then if z is the after battle of x, and z 
						// is less than y, then the units remain in the town. If z > y, the excess returns to the zeppelin.
						int townUnitsBefore = oldTownSizeArray[k];
						int townUnitsAfter = holdUnit.getSize();
						int difference = townUnitsAfter-townUnitsBefore;
						if(difference>0) {
							possZepp.getAu().get(k).setSize(difference);
							holdUnit.setSize(townUnitsBefore); // so we get the difference,
							// tack it back, then set the unit properly.
							
						} else possZepp.getAu().get(k).setSize(0);
						
					}
					if(holdUnit.getLotNum()!=-1) { // we remove the civvie AFTER getting numUnitsRemaining, etc, or else it gets removed before we take data and
						//it's all fucked up.
						t2au.remove(k);
						k--;//remove the civilian unit, no further need for it.
					}
					
					
					
					k++;
				} 
				synchronized(t2.getDebris()) { // ADDING DEBRIS
				int l = 0;
				while(l<totalCost.length-1) {
					totalCost[l]=(long) Math.round(((double) totalCost[l])*.1);
					System.out.println("10% of total cost of all units lost for " + l + " is " + totalCost[l]);
					t2.getDebris()[l]+=totalCost[l];
					l++;
				}
				
				int i = 0;
				double totalpercent=0;
				while(i<t2.bldg().size()) {
					if(t2.bldg().get(i).getType().equals("Recycling Center")) {
						totalpercent+=.033*t2.bldg().get(i).getLvl();
					}
					i++;
				}
				if(totalpercent>1) totalpercent=1;
				if(totalpercent<0) totalpercent=0;
				
				l = 0;
				synchronized(t2.getRes()) {
					
					while(l<t2.getRes().length-1) {
						System.out.println("This player is getting " + totalpercent + " (1 being 100%) % of " + totalCost[l] + " at " + l + " with before debris being " + t2.getDebris()[l]);
						t2.getRes()[l]+=(long) Math.round(((double) totalCost[l])*totalpercent);
						t2.getDebris()[l]-=(long) Math.round(((double) totalCost[l])*totalpercent);
						System.out.println("As a result, the remaining debris is " + t2.getDebris()[l]);
						l++;
					}
				}
				}	
				if(numUnitsRemainingD==0) {
					// fuckin kill missile silos..
					UserBuilding[] bldgs = t2p.getPs().b.getUserBuildings(t2.townID,"Missile Silo");
					System.out.println("Destroying the Missile Silos.");
					int l = 0;
					if(bldgs.length>0) t2p.God.sendNukeMessage(t1p.getUsername(),t2,false);
					while(l<bldgs.length) {
						if(bldgs[l].getLvl()==0) {
							t2.killBuilding(bldgs[l].getBid());
						}
						l++;
					}
					
					
				}
			
				
				double percentlossoff=0,percentlossdef=0;
				double percentlossdiff=100; // we know that generally the beginning off must have at least 1, but the def may
				// indeed have 0, in which case we do not want to do this with a 0/0 thing in there, so if it does,
				// percentlossdif is already 100!
				if(currentExpAdvSizeOff!=0&&currentExpAdvSizeDef!=0) {
					 percentlossoff = 100*(((double) expModOffAft)/((double) currentExpAdvSizeOff));
					 percentlossdef = 100*(((double) expModDefAft)/((double) currentExpAdvSizeDef));

				 percentlossdiff=Math.round(percentlossdef-percentlossoff);
				 
				}
				combatHeader+=" The offense lost " + Math.round(percentlossoff) + "% of their " +
						"forces, and the defense lost " + Math.round(percentlossdef) + "% leading to a percent loss difference of " + percentlossdiff + "%.";
				
				boolean premium=false;
				int bp=0;
				System.out.println(t1p.getUsername() + " premiumTimer is " + t1p.getPremiumTimer());
				if(currentExpAdvSizeDef>0) // only get BP from fights, my man.
				if(percentlossdiff<0) {
					
					
				if(t2p.getPremiumTimer()>0) {
					premium=true;
				}
				ArrayList<Town> towns = t2p.towns();
				int avgLevel=0;
				 k = 0;
				 int highLvl=0;
				while(k<towns.size()) {
					avgLevel+=(int) Math.round(((double) getAverageLevel(towns.get(k)) )/ ((double) towns.size()));
					int x = 0;
					while(x<towns.get(k).bldg().size()) {
						if(towns.get(k).bldg().get(x).getLvl()>highLvl) highLvl=towns.get(k).bldg().get(x).getLvl();
						x++;
					}
					k++;
				}
				double percdifflvl = ((double) (highLvl-avgLevel))/100;
				
				avgLevel = (int) Math.round(((double) (1.0-percdifflvl)*((double) avgLevel) + percdifflvl*((double) highLvl)));
				 k = 0;
				AttackUnit a; int popped = 0;
				ArrayList<AttackUnit> t1modau = t2p.getAu();
				while(k<t1modau.size()) {
					a = t1modau.get(k);
					if(!a.getName().equals("locked")&&!a.getName().equals("empty"))popped++;
					k++;
				}
				k=0;
				int avgCSL = t2p.getPs().b.getCSLAtLevel(avgLevel,popped,t2p.towns().size());
				double fracOfCSL = ((double) currentExpAdvSizeDef)/((double) avgCSL);
				//Plot[350*Exp[-(l - .2)^2/(2*.08^2)]/Exp[1], {l, 0, 1}]
				if(fracOfCSL>1) fracOfCSL=1;
				bp = (int) Math.round( fracOfCSL*100*Math.exp(-Math.pow(-percentlossdiff-20,2)/(2*Math.pow(20,2))));
						
				try {
				bp = AttackUnit.getBP(t2au,bp);
				} catch(Exception exc2) { exc2.printStackTrace(); System.out.println("skin effect bp exception found but combat saved"); }
			//	System.out.println("bp def before, taking into account bp addon change, is " + bp + "part 1: "+ (fracOfCSL*1500) + " part 2: " + (fracOfCSL*1500*((double) -percentlossdiff/100.0)) +
			//			 " part 3: " +(fracOfCSL*1500*((double) -percentlossdiff/100.0)*Math.exp(1-((double) -percentlossdiff)/20.0)) + " part 4: " +
				//		 ( fracOfCSL*1500*((double) -percentlossdiff/100.0)*Math.exp(1-((double) -percentlossdiff)/20.0)/Math.exp(1)) + " part 5: " + 
					//	 Math.round( fracOfCSL*1500*((double) -percentlossdiff/100.0)*Math.exp(1-((double) -percentlossdiff)/20.0)/Math.exp(1)) + " part 6: " + 
						// bp);
				
				if(bp<10||t1p.ID==5||t1p.isQuest()) bp = 0;
				if(genocide) bp=(int) Math.round(((double) bp)/((double) actattack.getGenoRounds()));

				System.out.println("Average CSL is " + avgCSL + " and fracOfCSL is  " +fracOfCSL + " and percentlossdiff is " + percentlossdiff + " and bp is " + bp);
			
				if(premium) {
					t2p.setBp(t2p.getBp()+bp);
					t2p.setTotalBPEarned(t2p.getTotalBPEarned()+bp);
					if(t1p.getPremiumTimer()>0) {
						t1p.setBp(t1p.getBp()+bp/2);
						t1p.setTotalBPEarned(t1p.getTotalBPEarned()+bp/2);
					}

				}
				} else if(percentlossdiff>0){
					if(t1p.getPremiumTimer()>0) {
						premium=true;
					}
					ArrayList<Town> towns = t1p.towns();
					int avgLevel=0;
					 k = 0;
					 int highLvl=0;
					while(k<towns.size()) {
						avgLevel+=(int) Math.round(((double) getAverageLevel(towns.get(k)) )/ ((double) towns.size()));
						int x = 0;
						while(x<towns.get(k).bldg().size()) {
							if(towns.get(k).bldg().get(x).getLvl()>highLvl) highLvl=towns.get(k).bldg().get(x).getLvl();
							x++;
						}
						k++;
					}
					double percdifflvl = ((double) (highLvl-avgLevel))/100;
					System.out.println("avgLevel bef: " +avgLevel + " and high Lvl: "+ highLvl + " and percdifflvl =" + percdifflvl);

					avgLevel = (int) Math.round(((double) (1.0-percdifflvl)*((double) avgLevel) + percdifflvl*((double) highLvl)));
					System.out.println("avgLevel after: " +avgLevel);
					if(avgLevel<=0) avgLevel=1;
					 k = 0;
					AttackUnit a; int popped = 0;
					ArrayList<AttackUnit> t1modau = t1p.getAu();
					while(k<t1modau.size()) {
						a = t1modau.get(k);
						if(!a.getName().equals("locked")&&!a.getName().equals("empty"))popped++;
						k++;
					}
					k=0;
					int avgCSL = t1p.getPs().b.getCSLAtLevel(avgLevel,popped,t1p.towns().size());
					double fracOfCSL = ((double) currentExpAdvSizeOff)/((double) avgCSL);
					
					//Plot[350*Exp[-(l - .2)^2/(2*.08^2)]/Exp[1], {l, 0, 1}]
					if(fracOfCSL>1) fracOfCSL=1;
					bp = (int) Math.round( fracOfCSL*100*Math.exp(-Math.pow(percentlossdiff-20,2)/(2*Math.pow(20,2))));
				
					try {
					bp = AttackUnit.getBP(t1au,bp);
					} catch(Exception exc2) { exc2.printStackTrace(); System.out.println("skin effect bp exception found but combat saved"); }
				//	System.out.println("bp before, taking into account bp addon change, is " + bp + "part 1: "+ (fracOfCSL*1500) + " part 2: " + (fracOfCSL*1500*((double) percentlossdiff/100.0)) +
					//		 " part 3: " +(fracOfCSL*1500*((double) percentlossdiff/100.0)*Math.exp(1-((double) percentlossdiff)/20.0)) + " part 4: " +
							// ( fracOfCSL*1500*((double) percentlossdiff/100.0)*Math.exp(1-((double) percentlossdiff)/20.0)/Math.exp(1)) + " part 5: " + 
						//	 Math.round( fracOfCSL*1500*((double) percentlossdiff/100.0)*Math.exp(1-((double) percentlossdiff)/20.0)/Math.exp(1)) + " part 6: " + 
							// bp);
					
					if(bp<10||t2p.ID==5||t2p.isQuest()) bp = 0;
					if(genocide) bp=(int) Math.round(((double) bp)/((double) actattack.getGenoRounds()));

					System.out.println("Average CSL is " + avgCSL + " and fracOfCSL is  " +fracOfCSL + " and percentlossdiff is " + percentlossdiff + " and bp is " + bp);
				
					if(premium) {
						t1p.setBp(t1p.getBp()+bp);
						t1p.setTotalBPEarned(t1p.getTotalBPEarned()+bp);
						if(t2p.getPremiumTimer()>0) {
						t2p.setBp(t2p.getBp()+bp/2);
						t2p.setTotalBPEarned(t2p.getTotalBPEarned()+bp/2); }
					}
				}
				
				
				if(bp>150) bp=150; // max bp you can get from single attack.

				double togain = (percentlossdiff*.05*t1p.getAfTech()*100);
				if(togain>100) togain=100;
				if(togain<=0) togain = .025*t1p.getAfTech()*100;
				
				if(!genocide)
					combatHeader+=	" This means a " + Math.round(togain) + "% take of max cargo capturable in this raid.";
				else combatHeader+= " Due to this being a campaign, the max cargo has been diminished to half of a full take.";
				resFlag = true;
				if(numUnitsRemainingD==totalRemainingBefD&&numUnitsRemainingO==totalRemainingBefA) {
					// Stand off occurs. No resources are taken.
					resFlag = false;
				}
				
				String bombReturn = bombLogicBlock(actattack,null,null); // bomb logic block. Can be removed as necessary,
				// other wise needs to be used after the attack is complete.
			//	System.out.println("Return value for bombLogic is " + bombReturn);
				int returnNum = Integer.parseInt(bombReturn.substring(0,bombReturn.indexOf(",")));
				//BHAttackViewer newWindow = new BHAttackViewer(holdAttack,defNames,offNames,defUnitsBefore,offUnitsBefore,defUnitsAfter,offUnitsAfter);
			
				String bombResultBldg = bombReturn.substring(bombReturn.indexOf(",")+1,bombReturn.lastIndexOf(","));
				String bombResultPpl = bombReturn.substring(bombReturn.lastIndexOf(",")+1,bombReturn.length());
				//System.out.println("return Num is " + returnNum + " bombBldg is " + bombResultBldg + " bombPpl is " + bombResultPpl);

				// Right so the units are now fixed.
				
				// Now to send the attack back.
				// find slowest unit left alive
				try {
				AttackUnit.removeEffects(t1au,t1p); /// must do before weather effects...or else it'll be wrong.
				AttackUnit.removeEffects(t2au,t2p); /// must do before weather effects...or else it'll be wrong.
				} catch(Exception exc) { exc.printStackTrace(); System.out.println("Weather or skin effects caused an exception but combat was saved."); }

	
			
				int c = 0;
				double lowSpeed = 0;
				AttackUnit g;
				// we want it weighted...so we must divide by total size*expmod...
				// because that gives us the total amount of soldier equivalents.
				int totalsize=0;
				do {
					 g = t1au.get(c);
				//	if(g.size>0&&g.speed<lowSpeed) lowSpeed=g.speed;
					lowSpeed+=(g.getSize()*g.getExpmod()*g.getSpeed());
					totalsize+=(g.getSize()*g.getExpmod());
					 c++;
				} while(c<t1au.size());
				if(lowSpeed==0) {
					actattack.setRaidOver(true); // no need to reset ticksToHit, why return a ghostparty?
					actattack.deleteMe(); // no longer needed in memory, release it.
			/*	if(holdAttack.isGenocide()&&holdAttack.isAllClear()) {
					// defenders win. Get rid of civilian AU.
					//if(!holdAttack.getTown2().getPlayer().God.checkForGenocides(holdAttack.getTown2()))
				//	removeCivilianAU(holdAttack.getTown2());
					// if there are other genocides out there still hitting, leave this
					// civilian au thing out...put raidOver to true before hand so it
					// won't be found.
				}*/
			//	attackServer.remove(i);
			//	i--; // Means no survivors. Gone due to fact that this needs to stay on to be updated.

				
				} else {
					lowSpeed/=totalsize;
					// put this here because if zero, 0/0 = NaN!
					
					
					// below is the logic that controls genocide fighting stages vs. normal ones.
				int testhold = (int) Math.round((Math.sqrt(Math.pow((t2x-t1x),2)+Math.pow((t2y-t1y),2))*10/(lowSpeed*speedadjust))/GodGenerator.gameClockFactor);
				if(testhold==0) testhold=(int) Math.round(((double) 10/(lowSpeed*speedadjust))/GodGenerator.gameClockFactor);

				if(raidType.equals("invasion")) { // so if there is an invasion, no genocide or collecting
					// resources or anything, just INVADE. Or at least try...
				 invsucc=invasionLogicBlock(actattack);
				 
				 if(!invsucc) {
					 // if not success, need to send back the raid!
					 
					 actattack.setTicksToHit(testhold);
						actattack.setTotalTicks(testhold);
						actattack.setRaidOver(true);
						
						if(resFlag) {
							// Means can collect resources.
							
							// just gets total, divides by 1/4th, and takes one of each of those resources.
						
							moveResources(actattack,t2,percentlossdiff,false);
							

						}
				 }
				}
				else if(!genocide||(genocide&&!resFlag)) { // second condition means standoff during genocide.
					actattack.setTicksToHit(testhold);
					actattack.setTotalTicks(testhold);

					actattack.setRaidOver(true);
				
				if(resFlag) {
					// Means can collect resources.
					
					// just gets total, divides by 1/4th, and takes one of each of those resources.
				
					moveResources(actattack,t2,percentlossdiff,false);
					

				}
				} else if(genocide&&numUnitsRemainingD>0&&!holdAttack.allClear()) {
					actattack.setTicksToHit((int) Math.round(((double) testhold)/4));
					actattack.setTotalTicks((int) Math.round(((double) testhold)/4));
				} else if((genocide&&numUnitsRemainingD==0&&!holdAttack.allClear())) {
					// Oo now it's time to attack the civilians!! Kill them all!! :O O O OO O  : OOOO :O :O!!!
					
					// Will drain all of the civilians from their posts into an attack unit, one per building, with lot number
					// saved in the object. Will set a flag "allclear" on the raid on so it knows that when the defensive units
					// remaining are 0 and this flag is on that genocide is complete. 
					// If the attacking units remaining are 0 and this flag is on then well shit it'll remove the
					// raid anyway, but the flag will let us know to put the units back in their cages.
					
					// No defense comes from if the people have no defensive attack units from the getgo!
					actattack.setAllClear(true);
					boolean foundCiv = false;
					boolean foundNonZeroCiv=false;
					int u = 0;
					
					while(u<t2bldg.length) {
						
					 b = t2bldg[u];
					
					if(b.getPeopleInside()>0) {
						
						int size = (int) Math.round(((double) b.getPeopleInside())*(1-civvybunkerfrac));
						
						foundCiv = true;
						if(size>0) foundNonZeroCiv = true;
			
						
					}
					
					
					u++;
					} 
					
					if(!foundCiv&&returnNum!=0) {
						// Means no units and not on a bombing run
						// where a successful bombing just occured, should just simply send raid back with resources.
						actattack.setRaidOver(true);
						actattack.setTicksToHit(testhold);
						actattack.setTotalTicks(testhold);

						moveResources(actattack,t2,percentlossdiff,false);

					} else if(!foundNonZeroCiv&&returnNum!=0) {
						// Civilian units were found but none of them came out due to bunkers.
						// Same as above but we remove civilian units.
						//removeCivilianAU(holdAttack.getTown2());
						actattack.setRaidOver(true);
						actattack.setTicksToHit(testhold);
						actattack.setTotalTicks(testhold);

						moveResources(actattack,t2,percentlossdiff,false);
					}
					else {
					
						actattack.setTicksToHit((int) Math.round(((double) testhold)/4.0));
						actattack.setTotalTicks((int) Math.round(((double) testhold)/4.0));

					}
					
					} else if(holdAttack.allClear()&&(numUnitsRemainingD>0||returnNum==0)) {
						// Ooh this is bad. Means they're still not all dead, or bombing isn't complete.
						// if bombing isn't complete but all dead, this thing will keep catching and resetting
						// until it's done. 
						// if return Numever goes to 1, and the defensive units dwindles to 0, it's caught by the next
						// block.

						actattack.setTicksToHit((int) Math.round(((double) testhold)/4));
						actattack.setTotalTicks((int) Math.round(((double) testhold)/4));

					} else if(holdAttack.allClear()&&numUnitsRemainingD==0&&(returnNum<0||returnNum==1)) {
						// so if allClear, no more units remaining, and there are no bombers or bomb isn't set to on,
						// or there are bombers but there is nothing left to bomb(or possibly 0 bldgbombers for some reason),
						//enact the ending...
						/*
						 * 		 * Return codes:
						 		* -2 means bomb isn't set to on.
						 		* -1 means no bombers
						 		* 0 means successful bombing
						 		* 1 means nothing left to bomb, or there are no bldgbombers left for some reason..
						 		* 
						 		* Cool this also means that if there are no bombers left, and bomb was set to on,
						 		* it doesn't matter...it'll detect this and switch back to normal genociding.
						 */
						// Clearly the town has lost. Need to remove the unit types now. And take resources.
					//	removeCivilianAU(holdAttack.getTown2());
						
						actattack.setRaidOver(true);
						actattack.setTicksToHit(testhold);
						actattack.setTotalTicks(testhold);

						if(resFlag) {
							
							moveResources(actattack,t2,percentlossdiff,false);


						}
						// knock out capital city!
						if(t2p.getCapitaltid()==t2.townID) {
							// EMP burst.
							int i = 0;
							while(i<t2p.God.programs.size()) {
								if(((Integer) t2p.God.programs.get(i).get("pid"))==t2p.ID&&((Thread) t2p.God.programs.get(i).get("Revelations")).isAlive()) {
									((Thread) t2p.God.programs.get(i).get("Revelations")).stop();
									t2p.getPs().b.sendYourself("Your Revelations A.I. was shut off due to a minor EMP caused by" + t1p.getUsername() + " successfully sieging or glassing your Capital! You can reactivate your A.I. at any time.", t1p.getUsername() + " has shut off your A.I.!");
									combatHeader+=" E.V.E., the Revelations A.I., was shut off by the attackers during this final raid.";
								}
								i++;
							}
							
						}
						
					}
				// Right so now the raid has happened and been reset so it is heading back home.
				}
				String zeppText="";
				
				if(numUnitsRemainingD==0&&t2.isZeppelin()){
					// we delete the town but not before adding the sources to the raid.
					
					long toAdd[] = new long[5];
					
					int i = 0;
					while(i<toAdd.length-1) { // first we add it then do changes.
						toAdd[i]=t2.getRes()[i]+t2.getDebris()[i];
						i++;
					}
					if(t1.getPlayer().getLeague()!=null) {
						
					
						double afterTax = (1-t1p.getLeague().getTaxRate(t1p.ID));
						double tax = 1-afterTax;
					//	System.out.println("raid gets "+r.getMetal()*afterTax + " and leagues gets " + r.getMetal()*tax + " of total " + r.getMetal() + " due to tax being " + tax + " and aftertax being " + afterTax);
						 i = 0;
						long secbuff[] = t1p.getLeague().getSecondaryResBuff();
						double tmodifier = 1;
						if(t1p.getLeague().getPremiumTimer()>0) tmodifier=.5;
						synchronized(secbuff) {
						while(i<toAdd.length-1) {
							secbuff[i]+=toAdd[i]*tax*tmodifier;
							toAdd[i]=(long) Math.round(toAdd[i]*afterTax);
							i++;
						}
						
						}
						
											
				} 
				
				
				actattack.setMetal(actattack.getMetal()+toAdd[0]);
				actattack.setTimber(actattack.getTimber()+toAdd[1]);
				actattack.setManmat(actattack.getManmat()+toAdd[2]);
				actattack.setFood(actattack.getFood()+toAdd[3]);

					t2.deleteTown();
					zeppText+=t2.getTownName()+";";
				} else if(numUnitsRemainingD==0&&!t2.isZeppelin()) {
					
					// IN THIS CASE, THE ZEPPELIN IS FLOATING OVER FRIENDLY TERRITORY. WE MUST SEND THE RESOURCES HOME!
					
					Town zeppelin = t2p.God.findZeppelin(t2.getX(),t2.getY());
					
					if(zeppelin.townID!=0&&zeppelin.getPlayer().ID==t2p.ID){ 
						// if it is a zeppelin in need of destroying...the resources go back to the town it's over.
						synchronized(t2.getRes()) {
							int i = 0;
							while(i<t2.getRes().length-1) {
								t2.getRes()[i]+=zeppelin.getRes()[i];
								i++;
							}
						}
						
						zeppelin.deleteTown();
					
					
						zeppText+=zeppelin.getTownName()+";";}
				} 
				
				if(t1.isZeppelin()&&t2.isZeppelin()&&numUnitsRemainingO!=0&&numUnitsRemainingD!=0&&t1.getX()==t2.getX()&&t1.getY()==t2.getY()) {
					// this is the case where both are zeppelins but both survived the battle quite intact.
					// if percentlossdiff is +, then we know the offense won.
					if(percentlossdiff>0) {
						int count = 0; 
						Town t;
						while(count<5) {
							t2.setX(t2.getX()+1);
							t = t2p.God.findTown(t2.getX(),t2.getY());
							if(!t.isZeppelin()) break; // It's okay if you're shoved into another zeppelin, but if you're shoved over a town, no way.
							count++;
						}
						if(count==5) t2p.getPs().b.abortAirship(t2.townID);
						else t2.setDestX(t2.getX());
						
					} else {
						int count = 0; 
						Town t;
						while(count<5) {
							t1.setX(t1.getX()+1);
							t = t1p.God.findTown(t1.getX(),t1.getY());
							if(!t.isZeppelin()) break; // It's okay if you're shoved into another zeppelin, but if you're shoved over a town, no way.
							count++;
						}
						if(count==5) t1p.getPs().b.abortAirship(t1.townID);
						else t1.setDestX(t1.getX()); // so it doesn't try and move again.

					}
				} 
				if(numUnitsRemainingO==0&&t1.isZeppelin()&&t2.isZeppelin()&&t1.getX()==t2.getX()&&t1.getY()==t2.getY()) {
					// The attacker can only be killed if the defender is a ZEPPELIN and their xs and ys are the same at the time of the
					// attack. If this is the case, all res goes to t2. If not, then it can't be killed.
					
						long toAdd[] = new long[5];
						
						int i = 0;
						while(i<toAdd.length-1) { // first we add it then do changes.
							toAdd[i]=t1.getRes()[i]+t1.getDebris()[i];
							i++;
						}
						if(t2.getPlayer().getLeague()!=null) {
							
						
							double afterTax = (1-t2p.getLeague().getTaxRate(t2p.ID));
							double tax = 1-afterTax;
						//	System.out.println("raid gets "+r.getMetal()*afterTax + " and leagues gets " + r.getMetal()*tax + " of total " + r.getMetal() + " due to tax being " + tax + " and aftertax being " + afterTax);
							 i = 0;
							long secbuff[] = t2p.getLeague().getSecondaryResBuff();
							double tmodifier = 1;
							if(t2p.getLeague().getPremiumTimer()>0) tmodifier=.5;
							synchronized(secbuff) {
							while(i<toAdd.length-1) {
								secbuff[i]+=toAdd[i]*tax*tmodifier;
								toAdd[i]=(long) Math.round(toAdd[i]*afterTax);
								i++;
							}
							
							}
							
												
					} 
					
						i = 0;
						synchronized(t2.getRes()) {
							while(i<t2.getRes().length-1) {
								t2.getRes()[i]+=toAdd[i];
								i++;
							}
						}
					t1.deleteTown();
					zeppText+=t1.getTownName()+";";
					
				} 
				

	//	j++;
	//} while(j<holdAttack.getAu().size());
				// this is where statreps are made.
				try {
					
					 
					     
				     
				   UberStatement   stmt = t1p.con.createStatement();
				      boolean transacted=false;
				      // First things first. We update the player table.
				     // System.out.println("I am making a status report.");
				      try {
				      if(combatData.length()>8000) combatData=combatData.substring(0,7999);
				      if(combatHeader.length()>8000) combatHeader=combatHeader.substring(0,7999);

				      while(!transacted) {
				    	 // holdAttack = t1p.getPs().b.getUserRaid(actattack.raidID); // because now they've got res!
				    	  boolean invade = false; int scout = 0;
				    	  if(raidType.equals("invasion")) invade = true;
				    	  if(raidType.equals("scout")) scout = 2; // means failed scouting.
				    	  try {
				      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
				      
				      int offbp=0,defbp=0;
				      if(percentlossdiff<0) { defbp=bp; offbp=bp/2;} else {offbp=bp; defbp=bp/2;} // >-30 means that positive means incoming won,
				      // so we know what to set!
				      
				      if(percentlossdiff>(-30))
				      stmt.execute("insert into statreports (defender,invade,invsucc,scout,m,t,mm,f,pid,tid1,tid2,auoffst,auofffi,audefst,audeffi,auoffnames,audefnames,genocide,bombbldgdata,bombppldata,combatdata,combatheader,bp,premium,ax,ay,dx,dy,offTownName,defTownName,zeppText,debm,debt,debmm,debf) values" +
				      		"(false," + invade + "," + invsucc + "," + scout + "," + actattack.getMetal() + "," + actattack.getTimber() + "," +actattack.getManmat() + "," + actattack.getFood() +","
				      		+ t1p.ID + ","+ t1.townID + "," + t2.townID + ",\"" + offUnitsBefore + "\",\"" + offUnitsAfter + "\",\"" 
				      		+ defUnitsBefore + "\",\"" + defUnitsAfter +"\",\""+ offNames + "\",\"" + defNames + "\"," +genocide + ",\""+bombResultBldg+"\",\""
				      		+ bombResultPpl+ "\",'" + combatData + "','" + combatHeader + "'," + offbp + "," + premium + ","+t1.getX()+","+t1.getY()+","+t2.getX()+","+t2.getY()+",'"+t1.getTownName()+"','"+t2.getTownName()+"','"+zeppText+"',"+totalCost[0]+","+totalCost[1]+","+totalCost[2]+","+totalCost[3]+");");
				      else
				    	  stmt.execute("insert into statreports (defender,invade,invsucc,scout,m,t,mm,f,pid,tid1,tid2,auoffst,auofffi,audefst,audeffi,auoffnames,audefnames,genocide,bombbldgdata,bombppldata,combatdata,combatheader,bp,ax,ay,dx,dy,offTownName,defTownName,zeppText) values" +
						      		"(false," + invade + "," + invsucc + "," + scout + "," + actattack.getMetal() + "," + actattack.getTimber() + "," +actattack.getManmat() + "," + actattack.getFood() +","
						      		+ t1p.ID + ","+ t1.townID + "," + t2.townID + ",\"" + offUnitsBefore + "\",\"" + offUnitsAfter + "\",\"" 
						      		+ ",0,0,0,0,0,0" + "\",\"" + ",0,0,0,0,0,0" +"\",\""+ offNames + "\",\"" + ",???,???,???,???,???,???" + "\"," + genocide + ",\""+bombResultBldg+"\",\""
						      		+ bombResultPpl+ "\",'" + combatData + "','" + "No data is available due to your being pwned.'" +","+offbp+ ","+t1.getX()+","+t1.getY()+","+t2.getX()+","+t2.getY()+",'"+t1.getTownName()+"','"+t2.getTownName()+"','"+zeppText+"'"+");");
				      
				      stmt.execute("insert into statreports (defender,invade,invsucc,scout,m,t,mm,f,pid,tid1,tid2,auoffst,auofffi,audefst,audeffi,auoffnames,audefnames,genocide,bombbldgdata,bombppldata,combatdata,combatheader,bp,premium,ax,ay,dx,dy,offTownName,defTownName,zeppText,debm,debt,debmm,debf) values" +
					      		"(true," + invade + "," + invsucc + ","+ scout + ","+ actattack.getMetal() + "," + actattack.getTimber() + "," +actattack.getManmat() + "," + actattack.getFood()  +","+ t2pid + ","+ t1.townID + "," + t2.townID + ",\"" + offUnitsBefore + "\",\"" + offUnitsAfter + "\",\"" 
					      		+ defUnitsBefore + "\",\"" + defUnitsAfter +"\",\""+ offNames + "\",\"" + defNames + "\"," + genocide + ",\""+bombResultBldg+"\",\""
					      		+ bombResultPpl+ "\",'" + combatData + "','" + combatHeader + "',"+defbp+","+premium+ ","+t1.getX()+","+t1.getY()+","+t2.getX()+","+t2.getY()+",'"+t1.getTownName()+"','"+t2.getTownName()+"','"+zeppText+"',"+totalCost[0]+","+totalCost[1]+","+totalCost[2]+","+totalCost[3]+");");
				      
				      // send out reports to support units' players on offensive and defensive sides.
				      int o = 6;
				      ArrayList<Player> holdForP = new ArrayList<Player>(); 
				      Player curr;
				      ArrayList<AttackUnit> actt1au = t1.getAu();
				      while(o<actt1au.size()) {
				    	   curr = actt1au.get(o).getOriginalPlayer();
				    	   c = 0;
				    	   found = false;
				    	  while(c<holdForP.size()) {
				    		  if(holdForP.get(c).ID==curr.ID) {found=true; break; }
				    		  c++;
				    	  }
				    	  
				    	  if(!found) holdForP.add(curr);
				    	  
				    	  o++;
				      }
				      
				      o = 0;
				      while(o<holdForP.size()) {
				    	 // System.out.println("my percentlossdiff is " + percentlossdiff);
				    	  if(percentlossdiff>-30)
					      stmt.execute("insert into statreports (defender,invade,invsucc,scout,m,t,mm,f,pid,tid1,tid2,auoffst,auofffi,audefst,audeffi,auoffnames,audefnames,genocide,bombbldgdata,bombppldata,combatdata,combatheader,ax,ay,dx,dy,offTownName,defTownName,zeppText,debm,debt,debmm,debf) values" +
						      		"(false,"+ invade + "," + invsucc + "," +scout + ","+ actattack.getMetal() + "," + actattack.getTimber() + "," +actattack.getManmat() + "," + actattack.getFood() +"," + holdForP.get(o).ID + ","+ t1.townID + "," + t2.townID + ",\"" + offUnitsBefore + "\",\"" + offUnitsAfter + "\",\"" 
						      		+ defUnitsBefore + "\",\"" + defUnitsAfter +"\",\""+ offNames + "\",\"" + defNames + "\"," + genocide + ",\""+bombResultBldg+"\",\""
						      		+ bombResultPpl+ "\",'" + combatData + "','" + combatHeader + "'"+ ","+t1.getX()+","+t1.getY()+","+t2.getX()+","+t2.getY()+",'"+t1.getTownName()+"','"+t2.getTownName()+"','"+zeppText+"',"+totalCost[0]+","+totalCost[1]+","+totalCost[2]+","+totalCost[3]+");");
				    	  else
					    	  stmt.execute("insert into statreports (defender,invade,invsucc,scout,m,t,mm,f,pid,tid1,tid2,auoffst,auofffi,audefst,audeffi,auoffnames,audefnames,genocide,bombbldgdata,bombppldata,combatdata,combatheader,ax,ay,dx,dy,offTownName,defTownName,zeppText) values" +
							      		"(false," + invade + "," + invsucc + "," + scout + "," + actattack.getMetal() + "," + actattack.getTimber() + "," +actattack.getManmat() + "," + actattack.getFood() +","
							      		+ holdForP.get(o).ID+ ","+ t1.townID + "," + t2.townID + ",\"" + offUnitsBefore + "\",\"" + offUnitsAfter + "\",\"" 
							      		+ ",0,0,0,0,0,0" + "\",\"" + ",0,0,0,0,0,0" +"\",\""+ offNames + "\",\"" + ",???,???,???,???,???,???" + "\"," + genocide + ",\""+bombResultBldg+"\",\""
							      		+ bombResultPpl+ "\",'" + combatData + "','" + "No data is available due to your being pwned." + "'"+ ","+t1.getX()+","+t1.getY()+","+t2.getX()+","+t2.getY()+",'"+t1.getTownName()+"','"+t2.getTownName()+"','"+zeppText+"'"+");");
				    	  o++;
				      }
				       o = 6;
				       holdForP = new ArrayList<Player>();
				       if(!invade) // if invasion happens, no need to notify supports, they're gone.
				      while(o<t2au.size()) {
				    	  
				    	   curr = t2au.get(o).getOriginalPlayer();
				    	  if(curr!=null) { // could be a civilian unit(only on genocide/glassing defensive
				    		  // side, in which case
				    		  // there is no originalPlayer.
				    	   c = 0;
				    	   found = false;
				    	  while(c<holdForP.size()) {
				    		  if(holdForP.get(c).ID==curr.ID) {found=true; break; }
				    		  c++;
				    	  }
				    	  
				    	  if(!found) holdForP.add(curr);
				    	  }
				    	  o++;
				      }
				      
				      o = 0;
				      while(o<holdForP.size()) {
				    	  
					      stmt.execute("insert into statreports (defender,invade,invsucc,scout,m,t,mm,f,pid,tid1,tid2,auoffst,auofffi,audefst,audeffi,auoffnames,audefnames,genocide,bombbldgdata,bombppldata,combatdata,combatheader,offTownName,defTownName) values" +
						      		"(true,"+ invade + "," + invsucc + "," +scout + ","+  holdAttack.resources()[0] + "," + holdAttack.resources()[1] + "," + holdAttack.resources()[2] + "," + holdAttack.resources()[3] +"," + holdForP.get(o).ID + ","+ t1.townID + "," + t2.townID + ",\"" + offUnitsBefore + "\",\"" + offUnitsAfter + "\",\"" 
						      		+ defUnitsBefore + "\",\"" + defUnitsAfter +"\",\""+ offNames + "\",\"" + defNames + "\"," + genocide + ",\""+bombResultBldg+"\",\""
						      		+ bombResultPpl+ "\",'" + combatData + "','" + combatHeader + "','"+t1.getTownName()+"','"+t2.getTownName()+"');");
				    	  o++;
				      }
				      stmt.execute("commit;");stmt.close();transacted=true; }
				      catch(MySQLTransactionRollbackException exc) { }
				      } } catch(Exception exc) { exc.printStackTrace(); System.out.println("Combat reports lost but combat was saved."); }
				 } catch(SQLException exc) { exc.printStackTrace(); }

			return true;
	}
	public static boolean resupplyLogicBlock(Raid holdAttack) {
		// resupplies the raid in question...
		int ie = 0;int totalCheckedSize=0;
		while(ie<holdAttack.getAu().size()) {
			totalCheckedSize+=holdAttack.getAu().get(ie).getSize();
			// SuggestTitleVideoId
			ie++;
		}
		if(totalCheckedSize==0) {
			// this means we called getAu() for the first time before the au statements got to update and put
			// the units into the raid!
			holdAttack.setAu(null);
			holdAttack.getAu(); // reset.
		}
		Town t1 = holdAttack.getTown1();
		int raidID = holdAttack.getResupplyID();
		int i = 0; boolean found = false; Raid r=null;
		ArrayList<Raid> attackServer=t1.attackServer();
		while(i<attackServer.size()) {
			 r = attackServer.get(i);
			if(r.raidID==raidID) { found = true; break; }
			i++;
		}
		
		if(!found||(found&&!r.isGenocide())) return false;
		// so if the raid doesn't exist OR it does exist AND it's not a genocide(or glass, glass
		// also uses the genocide boolean) run, return false!
		// We do this with bhfunction's resupply too, but double protection
		// means less babies.

		// cool, now we should have the raid...
		
		if(found&&r!=null) {
			// further check.
			// we can assume that the make up of the support units of the town
			// are the same as on the raid. I'm assuming that I made it this
			// way when I coded supply but I cannot remember at this point...
			// I believe so.
			// However, due to the complexity of this argument, I will just
			// make sure that I don't accidentally slip up...I think if the data
			// structure is present on the raid but a recall was previously sent
			// so that the support au size = 0, it is left their while it's
			// home version is removed. So we worry about this here with
			// an inner loop - no indexoutofbounds exceptions can touch us
			// but at the expense of more cpu time per run.
			
			String genocideUnitsBefore="";
			String genocideUnitsAfter="";
			String genocideUnitNames="";
			AttackUnit a;
			 i = 0;
			
			while(i<holdAttack.getAu().size()) {
				int j = 0;
				while(j<r.getAu().size()) {
					 a = r.getAu().get(j);
					if(a.getSlot()==holdAttack.getAu().get(i).getSlot()) {
						genocideUnitNames+=","+a.getName();
						genocideUnitsBefore+=","+a.getSize();
						r.setSize(j,a.getSize() + holdAttack.getAu().get(i).getSize());
						genocideUnitsAfter+=","+r.getAu().get(j).getSize();
						holdAttack.setSize(i,0);
						break;
					}
				j++;
				}
				i++;
			}

			holdAttack.setRaidOver(true);
			holdAttack.deleteMe();
			 
			try {
				
				  
			   UberStatement   stmt =holdAttack.getTown1().getPlayer().con.createStatement();
			      
			      // First things first.
			//      System.out.println("I am making a resupply report.");
			      boolean transacted=false;
			      while(!transacted) {
			    	  
			      try {
			      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
			     // only the scouting player gets a report, the defensive one does not.
			      
			      stmt.execute("insert into statreports (defender,resupplyID,m,t,mm,f,pid,tid1,tid2,auoffst,auofffi,auoffnames) values" +
			      		"(false," + holdAttack.getResupplyID() + "," + -1 + "," + -1 + "," + -1 + "," + -1 +","
			      		+ holdAttack.getTown1().getPlayer().ID + ","+ holdAttack.getTown1().townID + "," + holdAttack.getTown2().townID + ",\"" + genocideUnitsBefore + "\",\"" + genocideUnitsAfter+"\",\""+ genocideUnitNames + "\");");
			   
			      // send out reports to support units' players on offensive side, we're assuming
			      // we weren't discovered so why should defense get one?
			      int o = 6;
			      ArrayList<Player> holdForP = new ArrayList<Player>(); Player curr;
			      while(o<holdAttack.getTown1().getAu().size()) {
			    	   curr = holdAttack.getTown1().getAu().get(o).getOriginalPlayer();
			    	  int c = 0;
			    	   found = false;
			    	  while(c<holdForP.size()) {
			    		  if(holdForP.get(c).ID==curr.ID) {found=true; break; }
			    		  c++;
			    	  }
			    	  
			    	  if(!found) holdForP.add(curr);
			    	  
			    	  o++;
			      }
			      
			      o = 0;
			      while(o<holdForP.size()) {
			    	  
				      
				      stmt.execute("insert into statreports (defender,resupplyID,m,t,mm,f,pid,tid1,tid2,auoffst,auofffi,auoffnames) values" +
				      		"(false," + holdAttack.getResupplyID() + "," + -1+ "," + -1 + "," + -1 + "," + -1+","
				      		+ holdForP.get(o).ID + ","+ holdAttack.getTown1().townID + "," + holdAttack.getTown2().townID + ",\"" + genocideUnitsBefore + "\",\"" + genocideUnitsAfter + "\",\"" 
				      		+ genocideUnitNames +"\");");
			    	  o++;
			      }
			      
			      stmt.execute("commit;"); stmt.close(); transacted=true; }
			      catch(MySQLTransactionRollbackException exc) {
			    	  
			      }
			      }
			 } catch(SQLException exc) { exc.printStackTrace(); }
			return true;
		}
		
		return false; // if it somehow skipped the upper one and made it through
		// the other checks...
		
	}
	public static void attackServerCheck(Town t1, Player p) {
		
		/*
		 * How we Genocide an area:
		 * 
		 * In the holdRaid there should be a separate flag labeled Genocide.
		 * If this flag is on, then when an attack finishes, if any units remain on the attackers side and defenders side,
		 * the raid will happen again but from one fourth the distance. When all offensive units are dead,
		 * the raid is removed, of course. If all defensive units are dead, a separate attack happens where all the
		 * civilian units go up against all the defensive units. We simply assume that all civilian units
		 * have the same basic defensive abilities - which can be upgraded. We'll have to keep that data
		 * somewhere. Only after all this has gone will units take resources, and even then, they will
		 * only take half of the resources they could have taken if they had just attacked.
		 */
		UserRaid holdAttack;
		UserRaid[] attackServer = p.getPs().b.getUserRaids(t1.townID); Raid r;
		AttackUnit au;
		ArrayList<AttackUnit> AU;
		ArrayList<AttackUnit> tAU;
		
		if(attackServer.length>0) {
			int i = 0;
			do {
				 holdAttack = attackServer[i];
				 if(holdAttack.getTID1()==t1.townID) { // because we grab incomings also with userraids.
					r = t1.findRaid(holdAttack.raidID());
					if(holdAttack.eta()<=0&&!holdAttack.raidOver()&&r.getTown2().owedTicks>0) {
						r.getTown2().update();

					}
			//	System.out.println("raidOver is currently " + holdAttack.raidOver);
					// this else UberStatement is for the actual attack server to use, the above is the facsimile treatment.
				if(holdAttack.eta()<=0&&!holdAttack.raidOver()&&!holdAttack.raidType().equals("support")&&!holdAttack.raidType().equals("offsupport")&&!holdAttack.raidType().equals("scout")&&!holdAttack.raidType().equals("resupply")&&!holdAttack.raidType().equals("debris")) { 
					// support = 0 means not supporting run, and scout=0 means it's  not
					// a scouting run.
					combatLogicBlock(r,"");
	
				} else if(holdAttack.eta()<=0&&!holdAttack.raidOver()&&holdAttack.raidType().equals("resupply")) {
					// resupplyID>-1 indicates a resupply run, and is also the raidID of the genocide/glassing
					// raid we are resupplying!
					
					resupplyLogicBlock(r);
				}
				else if(holdAttack.eta()<=0&&!holdAttack.raidOver()&&(holdAttack.raidType().equals("support")||holdAttack.raidType().equals("offsupport"))) {
					// this means it's a supporting run. Scout will always be 0 for this.
					
					supportLogicBlock(r);
					
				}else if(holdAttack.eta()<=0&&!holdAttack.raidOver()&&(holdAttack.raidType().equals("debris"))) {
					// this means it's a supporting run. Scout will always be 0 for this.
				
					debrisLogicBlock(r);
					
				}	else if(holdAttack.eta()<=0&&!holdAttack.raidOver()&&holdAttack.raidType().equals("scout")) {
					
					scoutLogicBlock(r);
					// So scoutLogicBlock returns false if there is discovery,
					// and we go straight into a combat block then!
				}	else if (holdAttack.eta()<=0&&holdAttack.raidOver()) {
					// Now this is a return raid.
				//	System.out.println("Returning...");
					int c = 0;
					
						AU = r.getAu(); tAU = t1.getAu();
					do {
						 au = AU.get(c);
						 int k = 0;
						 while(k<tAU.size()) {
							 if(tAU.get(k).getSlot()==au.getSlot())
									t1.setSize(k,
											tAU.get(k).getSize()
													+ au.getSize());
					//		 System.out.println("Adding unit " + c + " of size " + au.size);
							 k++;
						 }
						c++;
					} while (c<AU.size());
					long res[] =t1.getRes();
					synchronized(res) {
					res[0]+=holdAttack.resources()[0];
					res[1]+=holdAttack.resources()[1];
					res[2]+=holdAttack.resources()[2];
					res[3]+=holdAttack.resources()[3];
					}
					// alright new units added. Need to add resources too when they get added on.
					r.deleteMe(); // Get it out of memory now.
						
					
				}
				else {
					
			//	System.out.println("Counting down " + t1.getTownName() + " tth bef "+ r.getTicksToHit());
					r.setTicksToHit((int) (holdAttack.eta() - 1));
			//	System.out.println("Counting down " + t1.getTownName() +" aft: " + r.getTicksToHit());
				}
				
			}
				i++;
			} while(i<attackServer.length);
		}
		
		
	}
	
	public static void tradeServerCheck(Town t1, Player p) {
		/*
		 * 
		 */
		UserTrade t; UserTradeSchedule ts=null; UserBuilding b; 
; ResultSet rs; Building actb;
		TradeSchedule actts; Trade actt; UserBuilding bldg[]; Town t2;
		int i = 0; UserTrade[] tres = p.getPs().b.getUserTrades(t1.townID); UserTradeSchedule[] tses = p.getPs().b.getUserTradeSchedules(t1.townID);
	//	System.out.println(p.username +" is running this shindig on tid " + tid + " which is owned by " + t1.getPlayer().username);
		UberStatement stmt = null;
		UserTrade[] otherTres;
		try {
			stmt = p.God.con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(i<tses.length) {
			try {
			ts = tses[i];
			actts = t1.findTradeSchedule(ts.getTradeScheduleID());
			if(actts.getTown2()!=null)
			t2 = actts.getTown2();
			else t2=null;
			//System.out.println("town1 is " +actts.getTown1().getTownName() + " and timesToDo is  "  + actts.getTimesToDo() +" and times done is "+ actts.getTimesDone() + " and finished is " + actts.isFinished());
			
			
			if(!ts.isAgreed()&&ts.isTwoway()) {
				// if it's not agreed yet, it remains static.
			/*	try {
					
					 rs = stmt.executeQuery("select * from messages where pid_to = " + p.ID+ " and pid_from = " 
							 + t2.getPlayer().ID + " and msg_type = 2 and tsid = " + ts.getTradeScheduleID());
					if(rs.next()) {
						// this means the other agreed, to we create one for the other player and
						// make them both agree.
						
						actts.completeTradeSetUp();
						 
					}
					
					rs.close();
					 
				} catch(SQLException exc) {
					exc.printStackTrace();

				}*/
			} else {
				if(ts.getTradeScheduleID()==4979||ts.getTradeScheduleID()==4980) System.out.println("I am " + ts.getTradeScheduleID() + " and my timesDone is " + ts.getTimesDone() + " and my todo is " +ts.getTimesToDo());
			if(ts.getTimesDone()>=ts.getTimesToDo()&&ts.getTimesToDo()!=-1) { 
				// do nothing, this trade schedule is simply waiting for it's last trade to expire!
				// if this is an infinity schedule, clearly timesDone is always larger(default at 0)
				// so we say, "if not an infinity schedule and it's greater."
				// Then the infinity schedule always gets thrown into the currTicks section
				// to be decremented.
				// could be a cancelled tradeschedule without any trades out...in this case,
				// if someone did a delete on a ts, the timesToDo is set to timesDone, not the
				// other way around, so suddenly infinity schedules can fall in here,
				// as well as normal trade schedules that you want cancelled!
				if(!ts.isTwoway()) {
					int sum = 0;
					int k = 0;
					while(k<tres.length) {
						if(tres[k].getTradeScheduleID()==ts.getTradeScheduleID()/*&&(!tres[k].isTradeOver()||tres[k].getTicksToHit()>=0)*/) sum++;
						k++;
					}
					/*
					try {
						 rs = stmt.executeQuery("select count(*) from trade where tsid = " + ts.getTradeScheduleID() + " and (tradeOver = false or ticksToHit>=0);");
						if(rs.next()) sum = rs.getInt(1);
						rs.close();
					} catch(SQLException exc) { exc.printStackTrace(); }*/
				if(sum==0) {

					// okay so we can delete.
					actts.deleteMe();
				}
				} else {
					int k = 0;
					int sum = 0;
/*
					try {
						 rs = stmt.executeQuery("select count(*) from trade where (tsid = " + ts.getTradeScheduleID() + " or tsid = " + ts.getMateTradeScheduleID() +") and (tradeOver = false or ticksToHit>=0);");
						if(rs.next()) sum = rs.getInt(1);
						rs.close();
					} catch(SQLException exc) { exc.printStackTrace(); }*/
					while(k<tres.length) {
						if((tres[k].getTradeScheduleID()==ts.getTradeScheduleID())/*&&(!tres[k].isTradeOver()||tres[k].getTicksToHit()>=0)*/) sum++;
						k++;
					}
					otherTres =actts.getTown2().getPlayer().getPs().b.getUserTrades(actts.getTown2().townID);
					
					k = 0;
					while(k<otherTres.length) {
						if((otherTres[k].getTradeScheduleID()==actts.getMate().tradeScheduleID)/*&&(!tres[k].isTradeOver()||tres[k].getTicksToHit()>=0)*/) sum++;
						k++;
					}
					if(sum==0&&actts.isThreadSafe()) {
						// okay so we can delete both of them...or none. We can't delete one and not the other!
						actts.getMate().deleteMe();
						actts.deleteMe();
					}
				}
			}
			
			else {

			if(ts.getCurrTicks()>0) {
				actts.setCurrTicks(ts.getCurrTicks() - 1);
			} else if(ts.getCurrTicks()<=0) {
				/*
				 * Time to create a new trade if possible.
				 */

				if(!sendTradeIfPossible(actts)) actts.resetTradeTimers();
				// 
			}
			}
			}
			} catch(Exception exc) {
				exc.printStackTrace();
				System.out.println("Exception for TS caught and now being dealt with.");
				if(ts!=null){
				System.out.println("TradeScheduleID: "+ ts.getTradeScheduleID());
				try {
					UberStatement stmt2 = p.God.con.createStatement();
					//stmt2.execute("update tradeschedule set finished=true where tsid = " +ts.getTradeScheduleID());
					stmt2.execute("delete from trade where tsid = " + ts.getTradeScheduleID());

					
					stmt2.close();
					p.synchronize();
				} catch(SQLException exc2) {
					exc2.printStackTrace();
				}
				tres =p.getPs().b.getUserTrades(t1.townID); // need to get new user trades, before going on,
				// as old trades with the ts may now be gone.
				}
				p.getPs().b.sendYourself("Your player just encountered a trade schedule related error and one or more trades may have been recalled due to this. Please notify support.", "Error occured.");
			}
			i++;
		}
		
		// NOW DO TRADES
		long res[];
		i = 0;
		Player otherP; t = null;
		while(i<tres.length) {
			try {
			t = tres[i];
			if(t.getTID1()==t1.townID) { // because get user trades fucking grabs incomings too.
			actt = t1.findTrade(t.getTradeID());
			t2 = actt.getTown2();
			otherP = t2.getPlayer();
			
			ts = p.getPs().b.getUserTradeSchedule(t.getTradeScheduleID());
			if(t.getTicksToHit()>0) {
				actt.setTicksToHit(t.getTicksToHit() - 1);
			} else if(t.getTicksToHit()<=0&&!t.isTradeOver()) {
			if(actt.getTown2().owedTicks>0)	actt.getTown2().update();
				
				/*
				 * Time to offload resources!
				 */
				if(!t.isStockMarketTrade()) {
					
					if(!actt.getTs().isTwoway()&&otherP.getLeague()!=null) {
						/*
						 * If this is a two way trade,
						 * then town 2, where the dump is occurring, paid tax
						 * on the resources they shipped already through collecting them
						 * in a raid or by mines. If this is just someone
						 * dumping resources on them, ie, a one way trade, this
						 * trade needs to be taxed!
						 */

							double afterTax = (1-otherP.getLeague().getTaxRate(otherP.ID));
							double tax = 1-afterTax;
							actt.setMetal((long) Math.round(t.getMetal()*afterTax));
							actt.setTimber((long) Math.round(t.getTimber()*afterTax));
							actt.setManmat((long) Math.round(t.getManmat()*afterTax));
							actt.setFood((long) Math.round(t.getFood()*afterTax));
							
							long[] secbuff = otherP.getLeague().getSecondaryResBuff();
							synchronized(secbuff) {
								double modifier=1;
								if(otherP.getLeague().getPremiumTimer()>0) modifier=.5;
								secbuff[0]+=t.getMetal()*tax*modifier;
								secbuff[1]+=t.getTimber()*tax*modifier;
								secbuff[2]+=t.getManmat()*tax*modifier;
								secbuff[3]+=t.getFood()*tax*modifier;
							}

							
					}
					
					
						res = t2.getRes();
						synchronized(res) {
						t = p.getPs().b.getUserTrade(t.getTradeID());
						res[0]+=t.getMetal();
						res[1]+=t.getTimber();
						res[2]+=t.getManmat();
						res[3]+=t.getFood();
						}
						actt.setMetal(0);
						actt.setTimber(0);
						actt.setManmat(0);
						actt.setFood(0);
					
					
				
				} else {
					// if it's a sm trade, then we onload
					// resources at this point after unloading our own!
					// also, you already paid tax on these resources if it is a sm trade.
					actts = actt.getTs();
					actt.setMetal(actts.getOthermetal());
					actt.setTimber(actts.getOthertimber());
					actt.setManmat(actts.getOthermanmat());
					actt.setFood(actts.getOtherfood());

				}
				
				
				actt.setTicksToHit(t.getTotalTicks());
				actt.setTradeOver(true);
				
				

				
				
			} else if(t.getTicksToHit()<=0&&t.isTradeOver()) {
				
				// add traders back...proportional to them that left?
				
				// honestly no way to put them back proportionally...
				
				// find out where they came from.
				int j = 0; int tradeDearth=0;
				bldg = p.getPs().b.getUserBuildings(t1.townID,"Trade Center");
				while(j<bldg.length) {
					b = bldg[j];
					tradeDearth+=(b.getCap()-b.getPeopleInside());
					j++;
				}
				
				j = 0; int popCheck=0;
				while(j<bldg.length) {
					b = bldg[j];
					actb = t1.findBuilding(b.getBid());
						if(tradeDearth>=0) {
							int toAdd=(int) Math.floor(t.getTraders()*((double) (b.getCap()-b.getPeopleInside()))/((double) tradeDearth));
							if(b.getPeopleInside()+toAdd>b.getCap()) toAdd = (int) (b.getCap()-b.getPeopleInside());
							popCheck+=toAdd;
						//	System.out.println("Before: "+ actb.getPeopleInside() + " and adding " + toAdd);

							actb.setPeopleInside(b.getPeopleInside()
									+ toAdd);
					//	System.out.println("After: "+ actb.getPeopleInside());
						}
						else {
							actt.setTraders(0); break; // if somehow the cap goes down, then these traders are lost.
						}
					// so if you have 1/4th of the trade dearth, you get 1/4th of the traders!
					// This is okay, because if you don't have the room for them, you wouldn't
					// have been part of the dearth! You can't build more traders in the slots of the
					// traders gone due to preprogramming in battlehard functions!
					j++;
				}
				if(popCheck<t.getTraders()&&tradeDearth>=0) {
					// clearly we're going to get...rounding errors, where we have one or two homeless traders. So we add them back.
					j = 0; 
					bldg = p.getPs().b.getUserBuildings(t1.townID,"Trade Center");
					while(j<bldg.length) {
						b = bldg[j];
						actb = t1.findBuilding(b.getBid());

							if((b.getCap()-b.getPeopleInside())>=popCheck) {
								
								actb.setPeopleInside(b
										.getPeopleInside()
										+ popCheck);
								popCheck=0;break;
							}
							else if(b.getCap()>b.getPeopleInside()&&(b.getCap()-b.getPeopleInside())<popCheck) {
								popCheck-=(b.getCap()-b.getPeopleInside());
								actb.setPeopleInside((int)( b.getCap()));

							}
							
						if(popCheck<=0) break;
						j++;
					}
					
				}
				
				//actts = actt.getTs();
				//if(ts.getTimesDone()>=ts.getTimesToDo()&&ts.getTimesToDo()!=-1) actts.deleteMe();
				res = t1.getRes();
				synchronized(res) {
				res[0]+=t.getMetal(); 
				res[1]+=t.getTimber();
				res[2]+=t.getManmat();
				res[3]+=t.getFood();
				}
				actt.deleteMe();
				
			}
		}
		} catch(Exception exc) {
			exc.printStackTrace();
			System.out.println("Exception for Trade caught and now being dealt with.");
			if(t!=null){
			System.out.println("TradeID: "+ t.getTradeID());
			if(p.findTradeSchedule(t.getTradeScheduleID())==null) {
				System.out.println("The tradeschedule is null is the reason. Seems to not be a part of the player anymore.");
			}
			if(p.God.findTown(t.getTID1())==null) System.out.println("town 1 is null");
			if(p.God.findTown(t.getTID2())==null) System.out.println("town 2 is null");

			try {
				UberStatement stmt2 = p.God.con.createStatement();
			//	stmt2.execute("update tradeschedule set finished=true where tsid = " +t.getTradeScheduleID());
				stmt2.execute("delete from trade where tsid = " + t.getTradeScheduleID());
				stmt2.close();
				p.synchronize();
			} catch(SQLException exc2) {
				exc2.printStackTrace();
			}
			}
			
			p.getPs().b.sendYourself("Your player just encountered a trade related error and one or more trades may have been recalled due to this. Please notify support.", "Error occured.");

		}
			i++;
		}
		try {
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public static boolean sendTradeIfPossible(TradeSchedule actts) {
		/*
		 * So we don't send if you don't got the stuff.
		 * 
		 * Need to check for the stuff.
		 * 
		 * POSSIBLE ERROR: SENDING A TRADE BEFORE THE OTHER PERSON'S TRADE GOES OUT
		 * DUE TO ITERATIVE NATURE OF PROGRAM CAUSES ONE USER TO SEND A TRADE AND THE OTHER
		 * DOES NOT BECAUSE THEY CANCEL THEIR TS RIGHT AFTER YOU SEND OUT THE FIRST TRADE.
		 * 
		 * WON'T HAPPEN BECAUSE DELETE FUNCTION DOES NOT HAPPEN UNTIL AFTER PROCESSING...BUT IF THEY
		 * DID PRESS IT AND IT DID GET DELETED AND THEN THE NEXT ROUND THE COUNTER ON THE OTHER GOT TO ZERO,
		 * THE OTHER WOULD DELETE ITSELF! BAHA!
		 * 
		 * 
		 * If there is a two way, only one of those twoway ts objects actually makes trades.
		 * The other is just a placeholder, so it's threadSafe is set to false. This is because
		 * if both count down at the same time there are sometimes duplicate tradesets.
		 * So we kick all threadSafe trades and their timers will not reset
		 * unless they are threadSafe, instead, their mate's reset their timers.
		 */
		Town t1 = actts.getTown1(); Town t2 = actts.getTown2();
		t2.update(); // obviously need to update before sending trade!
		UserBuilding[] bldg;
		UserTradeSchedule ts = actts.getTown1().getPlayer().getPs().b.getUserTradeSchedule(actts.tradeScheduleID);
		if(ts.isTwoway()&&!ts.isAgreed()) return false;
		
		// if it's two way, need to make sure the other town's got a trade schedule identical to it.
		// Just in case!
		if(ts.isTwoway()&&ts.isAgreed()&&ts.getMateTradeScheduleID()==0) { actts.deleteMe(); return false; } 
		
		// means somebody cancelled the trade and so none should be sent.


		if(actts.isTwoway()&&ts.isAgreed()&&!actts.isThreadSafe()) return false;

		// so now that we've checked on two ways, that they are either agreed and have a mate
		// or are not agreed to and so they wouldn't have one, basically you can't get
		// through unless you have a one way or a two way agreed trade with a mate!
		
		int i = 0;
		int t1Traders=0; UserBuilding b;
		long res[] = t1.getRes();
		if(res[0]<ts.getMetal()) return false;
		if(res[1]<ts.getTimber()) return false;
		if(res[2]<ts.getManmat()) return false;
		if(res[3]<ts.getFood()) return false;

	
		if(ts.isTwoway()) {
			res=t2.getRes();
			if(res[0]<ts.getOthermetal()) return false;
			if(res[1]<ts.getOthertimber()) return false;
			if(res[2]<ts.getOthermanmat()) return false;
			if(res[3]<ts.getOtherfood()) return false;

		}
		// do you have the traders??

		/*
		try {
			UberStatement stmt = t1.getPlayer().God.con.createStatement();
			ResultSet rs = stmt.executeQuery("select sum(ppl) from bldg where tid = " + t1.townID + " and name = 'Trade Center'");
			if(rs.next()) t1Traders = rs.getInt(1);
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }*/
		
		int k = 0;
		ArrayList<Building> bldgs = t1.bldg();
		while(k<bldgs.size()) {
			if(bldgs.get(k).getType().equals("Trade Center")) t1Traders+=bldgs.get(k).getPeopleInside();
			k++;
		}
	//	System.out.println("TR Factor Before: " + ((double) (1 + .1*(ts.getTown1().getPlayer().tradeTech-1)) + " and after " + (1 + .1*(ts.getTown1().getPlayer().tradeTech-1)+ ts.getTown1().player.God.Maelstrom.getTraderEffect(ts.getTown1().x,ts.getTown1().y))) );
		//System.out.println("Totalresource Before:" + ((long) ((double) (1 + .1*(ts.getTown1().player.tradeTech-1))*traderCarryAmount*t1Traders*(t1Traders+1)/2))
			//	+ " after: " +((long) ( (double) (1 + .1*(ts.getTown1().player.tradeTech-1)+ ts.getTown1().player.God.Maelstrom.getTraderEffect(ts.getTown1().x,ts.getTown1().y))*traderCarryAmount*t1Traders*(t1Traders+1)/2)));

		
		long totalresource =(long)((double)(1 + .05*(t1.getPlayer().getTradeTech()-1)+ t1.getPlayer().God.Maelstrom.getTraderEffect(t1.getX(),t1.getY()))*traderCarryAmount*t1Traders*(t1Traders+1)/2.0);
		//tradeTech starts at 1 so we subtract one...max is ten.


		if(totalresource<ts.getMetal()) return false;
		if(totalresource<ts.getTimber()) return false;
		if(totalresource<ts.getManmat()) return false;
		if(totalresource<ts.getFood()) return false;
		
		
		
		// THIS EQUATION RELATED TO BATTLEHARDFUNCTIONS HOWMANYTRADERS!
		int t1Required = t1.getPlayer().getPs().b.howManyTraders(ts.getMetal()+ts.getTimber()+ts.getManmat()+ts.getFood(),t1.townID);
		if(t1Required>t1Traders) return false;
		
		int t2Traders=0,t2Required=0;
		
		if(ts.isTwoway()&&ts.isAgreed()&&!ts.isStockMarketTrade()) {
			 i = 0;
				 t2Traders=0;
				// do you have the traders??
				/* try {
						UberStatement stmt = t1.getPlayer().God.con.createStatement();
						ResultSet rs = stmt.executeQuery("select sum(ppl) from bldg where tid = " + t2.townID + " and name = 'Trade Center'");
						if(rs.next()) t2Traders = rs.getInt(1);
						rs.close();
						stmt.close();
					} catch(SQLException exc) { exc.printStackTrace(); }*/
				  k = 0;
				bldgs = t1.bldg();
					while(k<bldgs.size()) {
						if(bldgs.get(k).getType().equals("Trade Center")) t2Traders+=bldgs.get(k).getPeopleInside();
						k++;
					}
				
				// need double on this to ensure that we don't lose small degrees of freedom.
				long othertotalresource =(long) ((double)(1 + .05*(t2.getPlayer().getTradeTech()-1)+t2.getPlayer().God.Maelstrom.getTraderEffect(t2.getX(),t2.getY()))*traderCarryAmount*t2Traders*(t2Traders+1)/2.0);
				//tradeTech starts at 1 so we subtract one...max is ten.

				if(othertotalresource<ts.getOthermetal()) return false;
				if(othertotalresource<ts.getOthertimber()) return false;
				if(othertotalresource<ts.getOthermanmat()) return false;
				if(othertotalresource<ts.getOtherfood()) return false;

				 t2Required = t2.getPlayer().getPs().b.howManyTraders(ts.getOthermetal()+ts.getOthertimber()+ts.getOthermanmat()+ts.getOtherfood(),t2.townID);
				 if(t2Required>t2Traders) return false;
		} 

		
		// CHECK IF THE OTHER GUY CAN MAKE THE TRADE, TOO! HELL, MAKE IT RIGHT HERE,
		// AND RESET BOTH'S CURRTICKS SO WHEN THE OTHER COMES AROUND, NO ISSUE!
		//(Well, it get's an extra tick in edgewise, due to being reset to 0 from full amount - 1 instead
		// of going the full amount and then reset to 0 so just set the currTicks at -1.)
		
		
		
		// now you clearly have the resources and the traders. Now you need
		// to subtract from each building's traders proportional to those inside!
		
		
		// looking at previous methods...
	
		/* 
		 * bunkerSize<t.getRes[i]) {
		 * T.RES IS WHAT'S BEING TAKEN. TOTALCARGO IS WHAT IS REQUIRED TO BE TAKEN. TOTALTRES IS TOTAL TRADERS IN THE TOWN!
				long totake = t.getRes[i]*totalCargo/totalTRes; // find what to take
				long taken = (t.getRes[i]-bunkerSize)-totake; // subtract in holder variable
				// so if we have a totake of 12 and 10 resources, -2 is added to toTake to make it only take 10.
				// with bunkerSize subtracting from taken, then if bunkerSize is 2, 10-2-12 = -4 adds to 10 to
				// make toTake = 8, so that bunkerSize keeps some of the resources.
				// Then totake is taken off of t.getRes[i] with no trouble.
				// bunkerSize is zero if no bunkers present, so t.getResi is totally absorbed
				// without bunkers, otherwise, it is absorbed up to the limit.
				// totake is already modified aboe so the second expression in the else UberStatement down
				// there works just fine.
				if(taken<0) totake+=taken; // if it's neg, subtract that from totake, we're not taking more than we can.
				if(taken<0) t.getRes[i] = bunkerSize; else t.getRes[i] = t.getRes[i]-totake; // if < 0, it's 0, if not, subtract it.
		 */
		
		// this is where actual trading begins...this tradeschedule is no longer threadsafe.
		
			
				// between these lines lies the deadzone. If both flows get through here
				//at the same time, they'll both turn eachother's thread
				// safeties off and both send duplicate trades.
	
			i = 0;
			int totalT1TravTraders=0;
			bldg = t1.getPlayer().getPs().b.getUserBuildings(t1.townID,"Trade Center");
			Building actb;
			while(i<bldg.length) {
				 b = bldg[i];
				 actb = t1.findBuilding(b.getBid());
				
				int totake = (int) Math.ceil(((double) b.getPeopleInside())*((double) t1Required)/((double) t1Traders)); // find what to take
				int taken = b.getPeopleInside()-totake; 
				if(taken<0) totake+=taken; // if it's neg, subtract that from totake, we're not taking more than we can.
				if(taken<0) { totalT1TravTraders+=b.getPeopleInside(); actb.setPeopleInside(0); }
				else { 			totalT1TravTraders+=totake;
				actb.setPeopleInside(b.getPeopleInside()-totake); }// if < 0, it's 0, if not, subtract it.
					 
				 
			i++;
			}
			i = 0;
			
			 res = t1.getRes();
			 synchronized(res) {
			res[0]-=ts.getMetal();
			res[1]-=ts.getTimber();
			res[2]-=ts.getManmat();
			res[3]-=ts.getFood();
			 }
			// okay so now we need to create the damn trade.
			
			actts.makeTrade(false,totalT1TravTraders); // it's not the second entity.
			
			if(ts.isTwoway()&&ts.isAgreed()&&!ts.isStockMarketTrade()) {
				// this means we have a partner to send with!
			int totalT2TravTraders=0;
			bldg = t2.getPlayer().getPs().b.getUserBuildings(t2.townID,"Trade Center");
			
			while(i<bldg.length) {
				 b = bldg[i];
				 actb = t2.findBuilding(b.getBid());
					
			int totake = (int) Math.ceil(((double) b.getPeopleInside())*((double) t2Required)/((double) t2Traders)); // find what to take
			int taken = b.getPeopleInside()-totake; 
			if(taken<0) totake+=taken; // if it's neg, subtract that from totake, we're not taking more than we can.
			if(taken<0) { totalT2TravTraders+=b.getPeopleInside(); actb.setPeopleInside(0); }
			else { 			totalT2TravTraders+=totake;
				actb.setPeopleInside(b.getPeopleInside()-totake); }
					 
				 
			
			i++;
			}
			res =t2.getRes();
			synchronized(res) {
			res[0]-=ts.getOthermetal();
			res[1]-=ts.getOthertimber();
			res[2]-=ts.getOthermanmat();
			res[3]-=ts.getOtherfood();
			}
			actts.getMate().makeTrade(true,totalT2TravTraders); // it IS the second entity.

			}
			

	

		return true;
		
				
	}
	public static int getTotalPopWithExpMod(Player p) {
		/*
		 * Because sometimes you don't want totalPop, which uses population,
		 * but total population of civilians and total expmod of all units, you 
		 * can use this method to get that.
		 */
		int i = 0;
		int totalpopwithexpmod = 0; Town t;
		ArrayList<Town> towns = p.towns();
		while(i<towns.size()) {
			 t = towns.get(i);
			
			totalpopwithexpmod+=t.getPop();
			i++;
		}
		
		i=0;
		while(i<6) {
			totalpopwithexpmod+=getTotalSize(p.getAu().get(i),p)*p.getAu().get(i).getExpmod();
			i++;
		}
		return totalpopwithexpmod;
	}public static int getCombatPopWithExpMod(Player p) {
		/*
		 * Because sometimes you don't want totalPop, which uses population,
		 * but total population of civilians and total expmod of all units, you 
		 * can use this method to get that.
		 */
		int i = 0;
		int totalpopwithexpmod = 0; Town t;
		
		i=0;
		while(i<6) {
			totalpopwithexpmod+=getTotalSize(p.getAu().get(i),p)*p.getAu().get(i).getExpmod();
			i++;
		}
		return totalpopwithexpmod;
	}
	public static int getTotalSize(AttackUnit au, Player p) {
		// so this method returns the total size of all units of this type in existence
		// for this player. Due to it only being used for veterancy and due to veterancy
		// always sending us the originating player, we use originalSlot and know that
		// the au are arranged that way according to originalSlot.
		// I do believe that in all cases, the slot number is what we record it to be so
		// we can just retrieve that from memory.
		
		int i = 0; int size = 0; Town t;ArrayList<Raid> attackServer;
		UberStatement stmt; ResultSet rs; UberStatement stmt2; ResultSet rs2;
		ArrayList<AttackUnit> auset;
		
		 ArrayList<Town> towns = p.towns(); AttackUnit a;
			while(i<towns.size()) {
				 t = towns.get(i);

				auset = t.getAu();
				
				if(au.getSupport()>0)
				size+=auset.get(au.getOriginalSlot()).getSize();
				else size+=auset.get(au.getSlot()).getSize();
			
				
					int k = 0;
					
					while(k<t.attackServer().size()) {
						auset = t.attackServer().get(k).getAu();
						int j = 0;
						while(j<auset.size()) {
							if(auset.get(j).getSlot()==au.getSlot()){ size+=auset.get(j).getSize(); break; }
							j++;
						}
						k++;
					}
					
					k=0;
					while(k<p.God.getTowns().size()) {
						auset =p.God.getTowns().get(k).getAu();
						int j = 0;
						while(j<auset.size()) {
							a = auset.get(j);
							if((au.getSupport()>0&&a.getSupport()>0&&a.getOriginalPlayer().ID==au.getOriginalPlayer().ID&&a.getOriginalSlot()==au.getOriginalSlot())
								||(au.getSupport()>0&&a.getSupport()==0&&p.God.getTowns().get(k).getPlayer().ID==au.getOriginalPlayer().ID&&au.getOriginalSlot()==a.getSlot())
								||(au.getSupport()==0&&a.getSupport()>0&&a.getOriginalPlayer().ID==p.ID&&au.getSlot()==a.getOriginalSlot()))
								size+=a.getSize();
								
						
							j++;
						}
						j = 0;
						attackServer = p.God.getTowns().get(k).attackServer();
						while(j<attackServer.size()) {
							auset = attackServer.get(j).getAu();
							int x = 0;
							while(x<auset.size()) {
								a = auset.get(x);
								if((au.getSupport()>0&&a.getSupport()>0&&a.getOriginalPlayer().ID==au.getOriginalPlayer().ID&&a.getOriginalSlot()==au.getOriginalSlot())
										||(au.getSupport()>0&&a.getSupport()==0&&p.God.getTowns().get(k).getPlayer().ID==au.getOriginalPlayer().ID&&au.getOriginalSlot()==a.getSlot())
										||(au.getSupport()==0&&a.getSupport()>0&&a.getOriginalPlayer().ID==p.ID&&au.getSlot()==a.getOriginalSlot())
										)
										size+=a.getSize();
								x++;
							}
							j++;
						}
						k++;
					}
				
					
				
				
				i++;
			}
	/*	try {
		 stmt = p.God.con.createStatement();
		 stmt2 = p.God.con.createStatement();

		 ArrayList<Town> towns = p.towns();
		while(i<towns.size()) {
			 t = towns.get(i);

			auset = t.getAu();

			if(au.getSupport()>0)
			size+=auset.get(au.getOriginalSlot()).getSize();
			else size+=auset.get(au.getSlot()).getSize();
		
				if(au.getSupport()>0)
					 rs = stmt.executeQuery("select * from supportAU where ftid = " + t.townID + " and fslotnum = " + au.getOriginalSlot());
				else 	 rs = stmt.executeQuery("select * from supportAU where ftid = " + t.townID + " and fslotnum = " + au.getSlot());
					while(rs.next()) {
						size+=rs.getInt(5);
						if(rs.getInt(6)==2) {
						rs2 = stmt2.executeQuery("select size from raidSupportAU where tid = " + rs.getInt(1) + " and tidslot = " + rs.getInt(4));
							while(rs2.next()) {
								size+=rs2.getInt(1);
							}
							rs2.close();
						}
					}
					rs.close();
				
			
			if(au.getSupport()>0)
				rs = stmt.executeQuery("select au" + (au.getOriginalSlot()+1) + " from raid where tid1 = " + t.townID + " and (raidOver = false or ticksToHit>=0)");
			else
			rs = stmt.executeQuery("select au" + (au.getSlot()+1) + " from raid where tid1 = " + t.townID + " and (raidOver = false or ticksToHit>=0)");
			
			while(rs.next()) {
				size+=rs.getInt(1);
				
			}
			rs.close();
			
			i++;
		}

		stmt.close();
		stmt2.close();
		}catch(SQLException exc) { exc.printStackTrace(); } 
		

*/

		return size;
	}
	
	static public boolean moveResources(Raid r, Town t, double percentlossdiff, boolean debris) {

		/*
		 * Basically, ideally, the resources are taken
		 * as one fourth, or one eigth, or one sixteenth, of the total cargo,
		 * depending on mission type.
		 * But what if the town doesn't have these resources?
		 * You need an overflow meter.
		 * Specifically, you need to take proportionally - so if they have 10, 10, 10, and 40,
		 * and you have seven cargo, you take 1,1,1,4.
		 * 
		 * The equation we look for is
		 * 
		 * (townResAmt/totalTownResAmt) is the percentage of the resource relative to the others, if you added them up.
		 * You want to take this same proportion in your cargo. So to do this, you do...
		 * 
		 * (townResAmt/totalTownResAmt)*cargoAmt. This is the amount of that resource you WOULD take
		 * if they have more than your cargoAmt. If this amount is greater than what they have,
		 * you take all of what they have, to the bunker limit.
		 * 
		 * Then the left over, you add to overflow...
		 * 
		 * Now, you know, for instance, if they've got 80% metal, and 80% of your cargo is too much
		 * for them, then sure as hell they can't pay the rest. Think about it:
		 * they've got 70,10,10,10, and you've got Cargo for 1000. 70% of cargo is 700. They can't pay it.
		 * No way in hell. 
		 * 
		 * But 70,10,10,10 with 110 cargo is 77, you could overflow that with 7, then you take 11 and 11 and 11 from
		 * the other guys correspondingly, but they still can't pay that shit. So you have to take it all. Every time.
		 * 
		 * 100*(exp(currlvl)+1)(exp(currlvl))/2
	*/
		
		// ANYTHING COMMENTED IS DELETABLE. LEFT HERE FOR SOME POSTERITY.
		int i = 0;
		long bunkerSize=0; Building b;
		while(i<r.getTown2().bldg().size()) {
			 b = r.getTown2().bldg().get(i);
			if(b.getType().equals("Bunker"))  bunkerSize+=(long)Math.round(.33*.05*((double) r.getTown2().getPlayer().getBunkerTech()) *((double) Building.resourceAmt)*Math.pow((b.getLvl()+2),2));

			i++;
		}
		
		i=0;
		long totalCargo = 0; int totalSize=0;
		while(i<r.getAu().size()) {
			int n = r.getAu().get(i).getSize();
			totalSize+=Math.round(((double) n*(n+1)*r.getAu().get(i).getPopSize()*r.getAu().get(i).getCargo())/2);
		
			i++;
		}
		
		
		i = 0; long totalTRes = 0;
		while(i<4) {
			if(!debris)
			totalTRes+=t.getRes()[i];
			else
			totalTRes+=t.getDebris()[i];

			i++;
		}
		if(totalTRes==0) return false; // just to make sure no divide by zero occurs. Why take resources otherwise?
		int dividingfactor=1; // basic attack dividing factor.
		if(r.isGenocide()) {
			//dividingfactor*=r.getGenoRounds(); // this is for the special requirements of foreshortening cargo on genocides.
			// I do timesing so that doing a bombing run knocks it down by an extra factor of four!
			// Shitty, no?
			dividingfactor*=2; //divide by two because of genocide.
			if(dividingfactor==0) dividingfactor=1; // just in case.
		}
		
		if(r.isInvade()) dividingfactor*=4; // this will discourage invasions as a standard
		// method of attack since one only gets one fourth the resources.
		double factor = totalSize;
		// extra factor of 2
		// is a scale to make it 100 total taken instead of 200 for average 50 cargo thing.
		totalCargo = Math.round(factor/dividingfactor); 

		//boolean done[] = new boolean[4]; // default is false. Goodie.
		
		 i = 0; //long overflow = 0;
		
				double modifier = percentlossdiff*.05*r.getTown1().getPlayer().getAfTech();
				if(modifier>1) modifier=1;
				if(modifier<=0) modifier = .025*r.getTown1().getPlayer().getAfTech();
				long res[];
				if(!debris)
				 res = t.getRes();
				else res = t.getDebris();// CLEVER WAY TO TREAT DEBRIS LIKE LOOT!
				if(debris) bunkerSize=0;
		while(i<4) {
			if(bunkerSize<res[i]) {
				long totake = res[i]*totalCargo/totalTRes; // find what to take
				long taken = (res[i]-bunkerSize)-totake; // subtract in holder variable
				// so if we have a totake of 12 and 10 resources, -2 is added to toTake to make it only take 10.
				// with bunkerSize subtracting from taken, then if bunkerSize is 2, 10-2-12 = -4 adds to 10 to
				// make toTake = 8, so that bunkerSize keeps some of the resources.
				// Then totake is taken off of t.res[i] with no trouble.
				// bunkerSize is zero if no bunkers present, so t.resi is totally absorbed
				// without bunkers, otherwise, it is absorbed up to the limit.
				// totake is already modified aboe so the second expression in the else UberStatement down
				// there works just fine.
				if(taken<0) totake+=taken; // if it's neg, subtract that from totake, we're not taking more than we can.
				long totakebef = totake;
				totake=(long) Math.round(((double) totake)*modifier);
				if(taken<0) res[i] = bunkerSize+(totakebef-totake); else res[i] = res[i]-totake; // if < 0, it's 0, if not, subtract it.
				
				
				
				switch(i) {
				case 0:
					r.setMetal(r.getMetal() + totake);
					break;
				case 1:
					r.setTimber(r.getTimber() + totake);

					 break;
				case 2:
					r.setManmat(r.getManmat() + totake);

					break;
				case 3:
					r.setFood(r.getFood() + totake);

					 break;
				}
				
				
			}
			//}
			

			
			i++;
		}
		
		if(r.getTown1().getPlayer().getLeague()!=null) {
			double afterTax = (1-r.getTown1().getPlayer().getLeague().getTaxRate(r.getTown1().getPlayer().ID));
			double tax = 1-afterTax;
			long oldM = r.getMetal();
			long oldT = r.getTimber();
			long oldMM = r.getManmat();
			long oldF = r.getFood();
		//	System.out.println("raid gets "+r.getMetal()*afterTax + " and leagues gets " + r.getMetal()*tax + " of total " + r.getMetal() + " due to tax being " + tax + " and aftertax being " + afterTax);
			r.setMetal((long) Math.round(r.getMetal()*afterTax));
			r.setTimber((long) Math.round(r.getTimber()*afterTax));
			r.setManmat((long) Math.round(r.getManmat()*afterTax));
			r.setFood((long) Math.round(r.getFood()*afterTax));
			
			long secbuff[] = r.getTown1().getPlayer().getLeague().getSecondaryResBuff();
			double tmodifier = 1;
			if(r.getTown1().getPlayer().getLeague().getPremiumTimer()>0) tmodifier=.5;
			synchronized(secbuff){ 
				secbuff[0]+=oldM*tax*tmodifier;
				secbuff[1]+=oldT*tax*tmodifier;
				secbuff[2]+=oldMM*tax*tmodifier;
				secbuff[3]+=oldF*tax*tmodifier;}
			//	r.getTown1().getPlayer().getLeague().setSecondaryResBuff(secbuff);
			

			}
		
		 

		return true;
		
		
		
		
	}
	public static String bombLogicBlock(Raid holdAttack, Town townFromHit, Town townToHit) {
		/*
		 * This is the bomb logic block, it can be inserted and removed where necessary. Essentially it 
		 * does all the logic processing for if and when something should be bombed and stuff..
		 * I keep it separate because shit is already complex in the attack server. I just put this
		 * block where necessary.
		 * 
		 * Return codes:
		 * -2 means bomb isn't set to on.
		 * -1 means no bombers
		 * 0 means successful bombing
		 * 1 means nothing left to bomb, or no bldgbombers left.
		 * 
		 * These return codes are attached to the start of the bomb reports.
		 * 
		 * This does all the thinking pertaining to when and how it shall be used. It can be placed right after
		 * unit losses and stuff are calculated.
		 */
		Town t1,t2;
		if(holdAttack!=null) {
		 t1 = holdAttack.getTown1();  t2 = holdAttack.getTown2();}
		else {
			t1 = townFromHit;
			t2 = townToHit; // if it's a nuke, we've got different problems.
		}
		Player t1p =t1.getPlayer(); Player t2p = t2.getPlayer();
		
		UserBuilding b; UserBuilding bldg[] = t2p.getPs().b.getUserBuildings(t2.townID,"all");
		int j =0;
		double bunkerSize=0; // getting the total number of soldiers the bunkers that are in mode 1 can hold.
		
		while(j<bldg.length) {
			 b = bldg[j];
			if(b.getType().equals("Bunker"))  bunkerSize+=Math.round(.33*.05*t2p.getBunkerTech()*getPeople(b.getLvl(),3,4,totalUnitPrice/t2p.towns().size()));
			j++;
		}
		j=0;
		double civvybunkerfrac=bunkerSize/(t2.getPop());
		if(civvybunkerfrac>1) civvybunkerfrac=1; // don't want them getting over 1 in protection!
		UserRaid r=null;
		if(holdAttack!=null) {
		 r = t1p.getPs().b.getUserRaid(holdAttack.raidID);
		
		if(!r.bomb()) return -2+",null+,null+"; }
		int bnr[] = new int[31]; // used for bomber numbers. hardcoded.
		bnr[0] = 1;
		bnr[1] = 1;
		bnr[2] = 1;
		bnr[3] = 2;
		bnr[4] = 2;
		bnr[5] = 3;
		bnr[6] = 3;
		bnr[7] = 4;
		bnr[8] = 4;
		bnr[9] = 5;
		bnr[10] = 5;
		bnr[11] = 6;
		bnr[12] = 6;
		bnr[13] = 7;
		bnr[14] = 7;
		bnr[15] = 8;
		bnr[16] = 9;
		bnr[17] = 9;
		bnr[18] = 10;
		bnr[19] = 11;
		bnr[20] = 12;
		bnr[21] = 13;
		bnr[22] = 13;
		bnr[23] = 14;
		bnr[24] = 16;
		bnr[25] = 17;
		bnr[26] = 18;
		bnr[27] = 19;
		bnr[28] = 20;
		bnr[29] = 22;
		bnr[30] = 23;
		
		int i = 0; double mult = 1.0;
		
		if(r!=null&&r.bombTarget().equals("Bunker")) mult = 4.0; // if it's a bunker, we make it tougher.
		while(i<bnr.length) {
			bnr[i]+=(int) Math.round(((double) bnr[i])*.05*(((double) t2p.getStabilityTech())-1.0)*mult); // add stability tech.
			i++;
		}
		int strengthdiluter = 999999;
		boolean genocide=false;
		String raidType = "glass"; // default.
		if(r!=null) {
		 raidType = r.raidType();}
		if(raidType.equals("glass")) genocide=true;
		
		if(r!=null&&(!genocide&&r.bomb()||(genocide&&!r.allClear()&&r.bomb()))) {
			strengthdiluter = 4; // 25% of full powerizzle. For strafing.
		} else if(r==null||(genocide&&r.bomb()&&r.allClear())) {
			// If Glassing and all Clear...get the full effect!
			strengthdiluter = 1;
			
		}
		// now, do we have the bombers?
		
		 i = 0;
		boolean hasBombers = false;
		ArrayList<AttackUnit> t1au=null;
		if(r==null) hasBombers=true;
		else {
		t1au = holdAttack.getAu();
		while(i<t1au.size()) {
			if(t1au.get(i).getPopSize()==20&&t1au.get(i).getSize()>0) { hasBombers = true; break; }
			i++;
		} 
		}
		if(!hasBombers) return -1+",null+,null+"; // so we check that Bombers even exist.
		
		double bldgbombers = 0;
		double pplbombers = 0;
		
		// so how does this code-ness work? Each attack unit that a bomber contributes to a queue which is diluted according to it's bldgdilution.
		// same with people dilution.
		if(r!=null) {
		i=0;AttackUnit a;
		while(i<t1au.size()) {
			 a = t1au.get(i);

			if(a.getPopSize()==20) {
			//	System.out.println(a.name + "'s size is " + a.size);

				switch(a.getWeap()[0]) {
				case 18:
					bldgbombers+=a.getSize(); // hive
					break;
				case 19:
					bldgbombers+=a.getSize()/2; // The Horizon Machine
					pplbombers+=(a.getSize()/2)*20; // *20 b/c one bomber = 20 civilians in cost.
					break;
				case 20:
					pplbombers+=a.getSize()*20; // focused nova bomb
					break;
				}
			}
			i++;
		} } else bldgbombers+=bnr[30]*2;
		
		bldgbombers/=strengthdiluter;
		pplbombers/=strengthdiluter;
		if(r!=null)
		try{ 
			bldgbombers = AttackUnit.getBombBldgEffect(holdAttack.getAu(),bldgbombers);
			pplbombers = AttackUnit.getBombPplEffect(holdAttack.getAu(),pplbombers);

		} catch(Exception exc) { exc.printStackTrace(); System.out.println("Bomb effect skin add went wrong. Bombing saved, though!"); }
	
		// so now we've got people bombers and bldg bombers.
		// I do not believe that we should hit citizens in buildings the same way we hit them outside,
		// otherwise what is the difference? It should be significantly easier. But if they go genocide with
		// a full ppl bomber, then what is the point? I guess it should detect for that and hit them with an extra
		// bit here.
	
		   
		//   For <setnumber, Exp(-setnumber/number)/Exp(-1)
		//   For >setnumber, Exp[setnumber/numberofbombers]/(100*Exp[1])
		
		// bomb target codes:
		/*
		 * 0: Bomb all targets(random decision).(This can get bunkers.)
		 * 1: Bomb warehouses
		 * 2: Bomb Arms Factories
		 * 3: Bomb Headquarters
		 * 4: Bomb Trade Centers
		 * 5: Bomb Institutes.
		 * 6: Bomb Communications Centers.
		 * 7: Bomb Construction Yards.
		 * 8: Bomb Bunkers
		 * 
		 */
		/*String targName = "all";
		switch(r.bombTarget()) {
		case 0:
			break;
		case 1:
			targName = "Warehouse";
			break;
		case 2:
			targName = "Arms Factory";
			break;
		case 3:
			targName = "Headquarters";
			break;
		case 4:
			targName = "Trade Center";
			break;
		case 5:
			targName = "Institute";
			break;
		case 6:
			targName = "Communications Center";
			break;
		case 7:
			targName = "Construction Yard";
			break;
		case 8:
			targName="Bunker";
			break;
		}*/
		String targName="all";
		if(r!=null)
		 targName = r.bombTarget();
		
		// Right so now we have the target set up.
		
		i = 0; int numbldgs=0; int numpeopleinbldgs = 0;
	
		while(i<bldg.length) {
			if((bldg[i].getType().contains(targName)||targName.equals("all")||(targName.equals("Refinery")&&(bldg[i].getType().equals("Metal Refinery")
					||bldg[i].getType().equals("Timber Processing Plant")||bldg[i].getType().equals("Materials Research Center")||bldg[i].getType().equals("Hydroponics Lab"))))&&bldg[i].getLvl()>0) { numbldgs++; numpeopleinbldgs+=bldg[i].getPeopleInside(); }
			i++;
		}
		if(numbldgs==0||(bldgbombers==0&&numpeopleinbldgs==0)) return 1+",vic+,vic+"; // so if there are no more targets left, return false,
		// or if there are no bldgbomber types available and the number of people in buildings is zero(hence we're only on a ppl bomber mission and
		// no ppl left to fight with) then we return false. So it detects a success and returns a true if so.
		int bldgindex[] = new int[numbldgs];
		i=0;  j = 0;
		
		while(i<bldg.length) {
			if((bldg[i].getType().contains(targName)||targName.equals("all")||(targName.equals("Refinery")&&(bldg[i].getType().equals("Metal Refinery")
					||bldg[i].getType().equals("Timber Processing Plant")||bldg[i].getType().equals("Materials Research Center")||bldg[i].getType().equals("Hydroponics Lab"))))&&bldg[i].getLvl()>0) {  bldgindex[j]=i; j++; }
			i++;
		}		
		
		i = 0;
		int bestbldgbombindex=-1; int bestbldglvl = -1;
		double pplbombersleft = pplbombers; Building actb;
		String bombResultPpl = "";
	//	System.out.println("Building bombers:" + bldgbombers + " People Bombers: " + pplbombers);
		while(i<bldgindex.length) {
				// for random bombing, bldgindex has every building.
				 b = bldg[bldgindex[i]];

			
				//   For <setnumber, Exp(-setnumber/number)/Exp(-1)
				//   For >setnumber, Exp[setnumber/numberofbombers]/(100*Exp[1])
				//   Exp[bomberpeoplepointsleft/totalbomberpeoplepoints]/Exp[1] for ppl.

				double rand = Math.random();
				double limit;
				
				if(pplbombersleft>0) { // ppl bomber block only activated if there are units left. No use wasting cycles!
				limit = Math.exp(pplbombersleft/(pplbombers+1))/Math.exp(1);
			//	System.out.println("People limit is " + limit);
				if(rand<limit) {
					// people bombing code, if it can bomb the building, it does.
					// if peopleInside-pplbombersleft is -5, then pplbombersleft = 5 now.
					// if peopleInside-pplbombersleft is 5, then pplbombersleft should be zero.
					// so basically if peopleInside is not less than 0, then pplbombersleft = 0,
					// if it is less than 0, then it's negative is what's left of pplbombersleft.
					int peopleBef = b.getPeopleInside();
					actb = new Building(b.getBid(), t1p.God);
					actb
							.setPeopleInside((int) (b.getPeopleInside()
									- pplbombersleft));
				
					if((b.getPeopleInside()
							- pplbombersleft)<(int)Math.round(peopleBef*civvybunkerfrac)) {
						// so if people inside is less than is allowed by bunkerfrac,
						// then remove only up to the bunker frac in people and leave the rest
						// inside. This is the same old 0 and all lost if civvybunkerfrac is 0.
						pplbombersleft-=(int)Math.round(peopleBef*(1-civvybunkerfrac));
						actb.setPeopleInside((int) Math.round(peopleBef*civvybunkerfrac));
					}
					else
						pplbombersleft=0;
					
					if(b.getPeopleInside()!=peopleBef) { bombResultPpl+=+b.getLotNum()+"."+peopleBef+"."+b.getPeopleInside()+ ";"; /*r.getTown2().removePeople(b,peopleBef-b.getPeopleInside());*/ }
					// so if people are indeed lost, the system updates it with remove people.
					
				//	System.out.println("My name is " + b.type + " and I now have " + b.peopleInside);
					
					
				}
				}
				
				// below: if it's less than or equal to, than the rand needs to be within limit to work,
				// if it's greater, then there is a small chance it won't work, and if it's within this,
				// it does not work, so it needs to be greater than limit.
				
				double bnramt = 0;
				try {
					bnramt=bnr[b.getLvl()];
				} catch(IndexOutOfBoundsException exc) {
					// if we get this, we're dealing with a lvl>31. Don't worry, we have an answer...
					bnramt=bnr[30]; // makes it infeasible to have buildings this large.
				}
				if(bldgbombers<=bnramt) {
				//	System.out.println("Ratio is " + bnramt/(bldgbombers+1) + " bnr amt is " + bnramt + " bldgbombers is " + bldgbombers);
					limit = Math.exp(-bnramt/(bldgbombers+1))/Math.exp(-1);
					if(rand<=limit&&b.getLvl()>bestbldglvl) { bestbldglvl = b.getLvl(); bestbldgbombindex=bldgindex[i]; }  
				} else {
					limit = Math.exp(bnramt/(bldgbombers+1))/(100*Math.exp(1));
					if(rand>limit&&b.getLvl()>bestbldglvl) { bestbldglvl = b.getLvl(); bestbldgbombindex=bldgindex[i]; } 
				}
				
				//if(bestbldgbombindex>=0)
			//System.out.println("Rand is: " + rand + " limit is " + limit + " People bombers left: " + pplbombersleft + " Current bestbldglvl: "+ bestbldglvl +
				//	" current bestbldgbombindex: " + bestbldgbombindex + " this relates to " + r.getTown2().bldg().get(bestbldgbombindex).type + " at lot " +
					//r.getTown2().bldg().get(bestbldgbombindex).lotNum);
				//else System.out.println("Rand is: " + rand + " limit is " + limit + " People bombers left: " + pplbombersleft + " and no buildings bombed yet.");
			i++;
		}
		
		String bombResultBldg = "";
		if(bestbldgbombindex!=-1) {
			// this means we did bomb something down.
			
			// return that shit.
			 bombResultBldg = t2.levelDown(bldg[bestbldgbombindex].getBid()); 
			// complex logic deciding how to level down building.
			// best handled at the town level.
			
		} else bombResultBldg = "nobldg";
		
		if(bombResultPpl.equals("")) bombResultPpl="noppl";
		
		bombResultPpl+="+"; bombResultBldg+="+"; // as list separators...then even bombings from raids get separated
		// CORRECTLY.
		
		if(bombResultBldg.startsWith("d")&&bldgindex.length==1) return 1+"," + bombResultBldg + "," + bombResultPpl; 
		// so if this is the last building destroyed we know we're finished.
		if(bldgbombers==0) return 1+"," + bombResultBldg + "," + bombResultPpl; 
		// if bldgbombers is zero, then obviously we can't hit targets anymore, so we know
		// we're finished, as all people would go outside for a glassing and a genocide
		// so pplbombers are irrelevant then, and the ones that do stay in due to bunkers
		// are immune to pplbombers in the same exact way.
		// bombResultBldg looks like d lotnum or l lotnum.oldlvl.pplkilledinblast
		// bomb result people looks like ;lotNum.peopleBef.peopleAfter
		// so bombResultPpl does ;data;data;data <--- no ending ;.
		// using different separators for different parts.
		return 0+"," + bombResultBldg + "," + bombResultPpl; 
	}
	public boolean getGodHere() {
		return godHere;
	}
	
	public boolean giveNewTown(Player p, int tid, int type, boolean skipMe, int chosenTileX,int chosenTileY) {
		/*
		 * Only use type = 2 if it's a quest capital city, so we know to put it far, far away
		 * and not waste a good core town.
		 * 
		 * This gives the player a new city by finding the closest non-ID city
		 * to the 20x20 border and then giving the nearest ID city to that person.
		 */
		int i= 0; double distance = 99999999;

		Player Id = getPlayer(5);
		
		
		 int j = 0;

		 int regTownSize=0;
		 Town t=null;
		 ArrayList<Town> towns = Id.towns();
		 while(j<towns.size()) {
			 t = towns.get(j);
			 int k = 0; boolean foundNone=true;
			 while(k<t.getResEffects().length) { // we only do normal towns, bitches.
				 if(t.getResEffects()[k]!=0) foundNone=false;
				 k++;
			 }
			 if(foundNone) {
				 regTownSize++;
			 }
			 j++;
		 }
			if(regTownSize<=5) growId();
			// this means no more regular spaces in the land.
			
			
			double testDist = 0;
			
		 Town closest=null;
		 if(tid==-1&&type!=2) {
			 
			
					j=0;
					towns = Id.towns();
		 while(j<towns.size()) {
			  t = towns.get(j);
			  if(skipMe)
			  testDist = Math.sqrt(t.getX()*t.getX()+t.getY()*t.getY());
			  else 
				 testDist= Math.sqrt(Math.pow(t.getX()-chosenTileX,2)+Math.pow(t.getY()-chosenTileY,2));
			 if(testDist<=(distance)) {
				 int k = 0; boolean foundNone=true;
				 while(k<t.getResEffects().length) { // we only do normal towns, bitches.
					 if(t.getResEffects()[k]!=0) foundNone=false;
					 k++;
				 }
				 if(foundNone) {
				 distance = testDist;
				 closest = t;
				 }// else System.out.println("tid " + t.townID + " is not clean.");
			 }
			 j++;
		 }
		 
		 t = closest;
		 if(closest==null) {
			 t = towns.get(0);
			// System.out.println("I had to use the zeroth town");
		 }
	//	 Id.towns().remove(t);
		 j = 0;
		 long res[] = t.getRes();
		 synchronized(res) {
		 while(j<res.length-1) {
			 res[j]=1000; // basic resource to start with.
			 j++;
		 }
		 }
		 j=0;
		// System.out.println("I got here. town id is " + t.townName + " and distance is " + distance);
			
		 } else if (tid==-1&&type==2) { 
		
				try {
					UberStatement stmt = con.createStatement();
					
					boolean transacted=false;
					while(!transacted) {
						try {
					stmt.execute("start transaction;");
					ResultSet rs;
				//	stmt.execute("update player set chg = 1 where pid = " + ID);
					int x = 1000000,y=1000000;
					rs = stmt.executeQuery("select count(*) from player");
					int count=0;
					if(rs.next()) count = rs.getInt(1);
					rs.close();
					  stmt.execute("insert into town (pid,townName,x,y,m,t,mm,f,pop,minc,tinc,mminc,finc,kinc,au1,au2,au3,au4,au5,au6) values (" + p.ID  +",'CapitalCity',"
		    				  +(x+count)+","+(y+count)+",0,0,0,0,1," + 0 + "," + 0 + "," + 0+ "," + 0+ "," +0 + ",0,0,0,0,0,0)");
					   rs = stmt.executeQuery("select tid from town where x = " + (x+count) + " and y = " + (y+count) + ";");
		    		  rs.next();
		    		   tid = rs.getInt(1);
		    		  rs.close();
		    		
		    		  stmt.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp,deconstruct,pploutside,bunkerMode) values (" +
		    		  		"'Metal Mine',0,3,-1,0,0,0,"+tid+",0,0,-1,0);");
		    		  stmt.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp,deconstruct,pploutside,bunkerMode) values (" +
			    		  		"'Timber Field',1,3,-1,0,0,0,"+tid+",0,0,-1,0);");
		    		  stmt.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp,deconstruct,pploutside,bunkerMode) values (" +
			    		  		"'Manufactured Materials Plant',2,3,-1,0,0,0,"+tid+",0,0,-1,0);");
		    		  stmt.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp,deconstruct,pploutside,bunkerMode) values (" +
			    		  		"'Food Farm',3,3,-1,0,0,0,"+tid+",0,0,-1,0);");
		    		  System.out.println("I am adding a new town of " + tid);
		    		  t = new Town(tid,this);
		    	
		    		  getIteratorTowns().add(t);
		    		 
		    		 // System.out.println("This town: " + t + " on end of player: "+ towns().get(towns().size()-1));

		    		  stmt.execute("commit;");
		    //		  stmt.execute("update player set chg = 2 where pid = "+ ID);
		    		  rs.close();
		    		  stmt.close();
		    		  transacted=true;
						} catch(MySQLTransactionRollbackException exc) { } 
					}
				
					} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 }
		 else {
			 boolean found = false;
			 i=0; Player pl=null;
				t = findTown(tid);
			 pl = t.getPlayer();
				
			//	pl.towns().remove(t);
		 }
		
		 // Finds the first town with a greater distance than the farthest
		 // city.
		 
		 // Now we create a false invasion raid on this town and pass
		 // it to the town's method, using the constructor that
		 // doesn't put this in a db anywhere, it's a transient piece
		 // of tomfoolery.
		 //public Raid(int raidID, double distance, int ticksToHit, Town town1, Town town2, boolean Genocide,boolean allClear,
		//	int metal, int timber, int manmat, int food,boolean raidOver,ArrayList<AttackUnit> au, boolean Bomb, boolean invade) {
		 
		// interestingly, if the last town has support au on it, those will appropriately be
		 // sent home by the invasion protocol that's causing this guy to lose his last
		 // village. His own units will be sent to this new town automatically,
		 // so for this raid we just kind of need to put in a zeroed out au list
		 // for data processing purposes, and his actual entrenched units will follow shortly.
		 // also we don't need to worry about sending the raid back as per giveTown,
		 // it will see there are no units and just turn on deleteMe and raidOver
		 // which mean nothing in this context as this raid is not on an attackServer
		 // nor in the DB.
		 // But we do need to NOT WORRY ABOUT sending supportAU zeroed models to this town,
		 // as support is stuck in a town whether it's off or def, and the db
		 // reflects that. However, Putting supportAU COPIES from one town in another,
		 // when zeroed, will cause no problems, as they are bypassed
		 // by the supportLogicBlock.
		 /*i = 0; ArrayList<AttackUnit> au = new ArrayList<AttackUnit>(); 
		 while(i<p.towns.get(0).getAu().size()) {
			 au.add(p.towns.get(0).getAu().get(i).returnCopy());
			i++; 
		 }*/
		 
		// System.out.println("From " + p.towns.get(0).townID + " to " + t.townID + " id is " + tid);
		 // Remember we just want zero-size copies because the actual au there
		 // will soon ret
		 System.out.println("Got here to give it.");
		 if(type!=2) // if type is 2, we already give it up there when we make it!
		 t.giveTown(null,p);
		
		 return true;
		

		
	}
	public static void buildingServerCheck(Town holdTown) {
		// this will soon be changed around.
		
		/*
		 * Goes through every player's building server buildings and ticks them
		 * down and goes through every player's building and creates new units in queue.
		 * 
		 * Also needs to adjust the times of everything building for new engineers.
		 * How to do this? It can send messages to each building and each building will modify
		 * the ticks left.
		 */
		ArrayList<Building> bldgserver = holdTown.bldgserver();
		int u = 0; Building b;
		QueueItem q;
			while(u<bldgserver.size()) {
				
				 b = bldgserver.get(u);
			
					 
				 if(b.getTicksToFinishTotal()==0) b.modifyTicksLevel(holdTown.getTotalEngineers(),holdTown.getPlayer().God.Maelstrom.getEngineerEffect(holdTown.getX(),holdTown.getX()),holdTown.getPlayer().getEngTech());
				 // if we just loaded, then ticksToFinishTotal isn't set and must be.
				if(b.getTicksToFinish()<b.getTicksToFinishTotal()&&b.getTicksToFinish()!=-1) {
					b.modifyTicksLevel(holdTown.getTotalEngineers(),holdTown.getPlayer().God.Maelstrom.getEngineerEffect(holdTown.getX(),holdTown.getY()),holdTown.getPlayer().getEngTech()); b.setTicksToFinish(b.getTicksToFinish()+1); 
					} 
				else if(b.getTicksToFinish()>=b.getTicksToFinishTotal()&&b.getLvlUps()>0) {
				b.setLvlUps(b.getLvlUps()-1);
			
				// for mines!
				
				// wherehouses cap out resources, others people.
			//	System.out.println(b.lvlUps + " is the way.");

				if(b.getLvlUps()<=0&&!b.isDeconstruct()) {  b.setLvl(b.getLvl()+1);}
				else if(b.getLvlUps()<=0&&b.isDeconstruct()) { holdTown.killBuilding(b.bid); }
				else if(b.getLvlUps()>0){ 
					b.setLvl(b.getLvl()+1);
					b.levelUp(holdTown.getTotalEngineers(),holdTown.getPlayer().God.Maelstrom.getEngineerEffect(holdTown.getX(),holdTown.getY()),holdTown.getPlayer().getEngTech());
			
				}
				}
				
				if(b.getNumLeftToBuild()>0) {
					 if(b.getTicksPerPerson()==0) b.modifyPeopleTicks(holdTown.getTotalEngineers(),holdTown.getPlayer().God.Maelstrom.getEngineerEffect(holdTown.getX(),holdTown.getY()),holdTown.getPlayer().getEngTech());
					 // if we just loaded, then ticksPerPerson isn't set and must be.
					if(b.getTicksLeft()>=b.getTicksPerPerson()) {
						b.setNumLeftToBuild(b.getNumLeftToBuild()-1);
						b.setPeopleInside(b.getPeopleInside()+1);
						b.setTicksLeft(0);
				
					

					} else {b.modifyPeopleTicks(holdTown.getTotalEngineers(),holdTown.getPlayer().God.Maelstrom.getEngineerEffect(holdTown.getX(),holdTown.getY()),holdTown.getPlayer().getEngTech()); b.setTicksLeft(b.getTicksLeft()+1); }
					
				}
				if(b.getType().equals("Arms Factory")) {
					if(b.Queue().size()>0) {
						int o =0;
						while(o<b.Queue().size()) {
							q = b.Queue().get(o);
							if(q.getTicksPerUnit()==0) {
								int i = 0;
								while(i<holdTown.getAu().size()) {
									if(holdTown.getAu().get(i).getSlot()==q.getAUtoBuild()) {
										q.modifyUnitTicksForItem(holdTown.getAu().get(i).getPopSize(),holdTown); 
										break;
									}
									i++;
								}
							}
							o++;
						}
					 q = b.Queue().get(0);

					if(q.getAUNumber()>0) {
						
						// this sets ticksPerUnit on constructor setup.
						if(q.getCurrTicks()>=q.getTicksPerUnit()) {
					//		System.out.println("I am doing a unit.");
							q.setAUNumber(q.getAUNumber()-1);
							int g = 0; 
							while(g<holdTown.getAu().size()) {
								if(holdTown.getAu().get(g).getSlot()==q.getAUtoBuild()){
									holdTown.getAu().get(g).setSize(holdTown.getAu().get(g).getSize()+1);
									break;
								}
								g++;
							}
							
							q.resetTicks();
							
						
						} else {
							int i = 0;
							while(i<holdTown.getAu().size()) {
								if(holdTown.getAu().get(i).getSlot()==q.getAUtoBuild()) {
									q.modifyUnitTicksForItem(holdTown.getAu().get(i).getPopSize(),holdTown); 
									break;
								}
								i++;
							}
							q.incrTicks(); }
						
					} else {
						// this means that there is a queue item with 0 or less number left to build.
						// it should be removed from the queue.
						q.deleteMe();
						
						// then the next one will be used next time around!
						
					}
					
					}
					
					 }
				u++;
			}
/*
			Town holdTown = holdPlayer.God.findTown(townID,holdPlayer.God); UserBuilding b; UserBuilding holdBldg; UserQueueItem q; UserQueueItem queue[];ArrayList<AttackUnit> au;
			Building actb; QueueItem actq;
			UserBuilding[] bldgserver,bldg;
			int engTech =holdPlayer.getEngTech();

		
				int totalEngineers = holdTown.getTotalEngineers();
				double engEffect = holdPlayer.God.Maelstrom.getEngineerEffect(holdTown.getX(),holdTown.getY());
			
				int u = 0;
				PlayerScript ps = holdPlayer.getPs();
				 bldgserver = ps.b.getUserBuildingServer(holdTown.townID,"all");
				
				while(u<bldgserver.length) {
					
					 holdBldg = bldgserver[u];
					 actb = new Building(holdBldg.getBid(),holdPlayer.God);
					 int ticksToFinish = holdBldg.getTicksToFinish();
				//	 if(holdBldg.getTicksToFinishTotal()==0) holdBldg.modifyTicksLevel(holdTown.getTotalEngineers(),holdTown.getPlayer().God.Maelstrom.getEngineerEffect(holdTown.getX(),holdTown.getY()),holdTown.getPlayer().engTech);
					 // if we just loaded, then ticksToFinishTotal isn't set and must be.
					if(ticksToFinish<holdBldg.getTicksToFinishTotal()&&ticksToFinish!=-1) {
						//holdBldg.modifyTicksLevel(holdTown.getTotalEngineers(),holdTown.getPlayer().God.Maelstrom.getEngineerEffect(holdTown.getX(),holdTown.getY()),holdTown.getPlayer().engTech);
						actb.setTicksToFinish(ticksToFinish + 1); 
						} 
					else if(ticksToFinish>=holdBldg.getTicksToFinishTotal()) {
						
					actb.setLvl(holdBldg.getLvl() + 1); actb.setLvlUps(holdBldg.getLvlUps() - 1);
				
					// wherehouses cap out resources, others people.
				//	System.out.println(holdBldg.lvlUps + " is the way.");
					 // not we use -1 on holdBldg because its now out of date on lvlUps by -1.
					if((holdBldg.getLvlUps()-1)<=0&&!holdBldg.isDeconstruct()) { actb.setTicksToFinish(-1); }
					else if((holdBldg.getLvlUps()-1)<=0&&holdBldg.isDeconstruct()) {  holdTown.killBuilding(holdBldg.getBid()); }
					else if((holdBldg.getLvlUps()-1)>0){ 
						actb.levelUp(totalEngineers,engEffect,engTech);
				
					}
					}
					u++;

				} 
					u=0;
					bldg = ps.b.getUserBuildings(holdTown.townID,"all");
					while(u<bldg.length) {
					 b = bldg[u];
					 actb = new Building(b.getBid(),holdPlayer.God);
					if(b.getNumLeftToBuild()>0) {
						// if(b.getTicksPerPerson()==0) actb.modifyPeopleTicks(totalEngineers,engEffect,engTech);
						 // if we just loaded, then ticksPerPerson isn't set and must be.
						 int ticksLeft = b.getTicksLeft();
						if(ticksLeft>=b.getTicksPerPerson()) {
							actb.setNumLeftToBuild(b
									.getNumLeftToBuild() - 1);
							actb
									.setPeopleInside(b.getPeopleInside() + 1);
							actb.setTicksLeft(0);
							

						} else {
						//	b.modifyPeopleTicks(holdTown.getTotalEngineers(),holdTown.getPlayer().God.Maelstrom.getEngineerEffect(holdTown.getX(),holdTown.getY()),holdTown.getPlayer().engTech); 
							actb.setTicksLeft(b.getTicksLeft() + 1); 
							}
						
					}
					
					if(b.getType().equals("Arms Factory")) {
						queue = b.getQueue();
						if(queue.length>0) {
						 q = queue[0];
						 actq = new QueueItem(q.getQid(), b.getBid(),holdPlayer.God);

						 au = holdTown.getAu();
						if(q.returnNumLeft()>0) {
						//	if(q.returnTicksPerUnit()==0) {
								
							//	q.modifyUnitTicksForItem(au.get(q.returnAUtoBuild()).getPop(),holdTown); 
								//System.out.println("queue item " + q.qid + " is " + q.returnTicksPerUnit());
								
						//	}
							// this sets ticksPerUnit on constructor setup.
							int currTicks = q.returnTicks();
							
							if(currTicks>=q.returnTicksPerUnit()) {
							//	System.out.println("I am doing a unit.");
								actq.setAUNumber(q.returnNumLeft()-1);
								int g = 0; 
								while(g<au.size()) {
									if(au.get(g).getSlot()==q.returnAUtoBuild()){
										holdTown.setSize(g,
												au.get(g).getSize() + 1);
									}
									g++;
								}
								
								if((q.returnNumLeft()-1)==0) actq.deleteMe();
								else
								actq.resetTicks();
								
							
							} else actq.setCurrTicks(currTicks+1);
							
							
							// else {b.modifyUnitTicksForQueue(holdTown.getAu(),holdTown.getTotalEngineers()); q.incrTicks(); }
							
						} else {
							// this means that there is a queue item with 0 or less number left to build.
							// it should be removed from the queue.
							actq.deleteMe();
							
							// then the next one will be used next time around!
							
						}
						}
						 }
					u++;
				} 
					
				*/
	
	}
	/*public void run() {
		// This is where God counts his clocks and ups his resources...
		
		// For this simple version, we will gain 1 metal per clock tick. Clock ticks
		// will happen every 1 second.
		
		for(;;) {
			try {
			Thread.sleep(1000); } catch (InterruptedException exc) {
				System.out.println("Error!");
			}
			ticks++;
			res1[0]+=resInc[0];
			res1[1]+=resInc[1];
			res1[2]+=resInc[2];
			res1[3]+=resInc[3];
			res1[4]+=resInc[4];

			res2[0]+=resInc[0];
			
			
			//Now comes the attack check.

			// Going to just store scripts in string arrays now, as I would normally if read from a file.
			
			String holdPlayer1[] = new String[7];
			String holdPlayer2[] = new String[11];
			holdPlayer1[0] = "if(havemetal(10,Town1)) {";
			holdPlayer1[1] = "attack(Town1,5,6,10,10,10,10,10,10); }";
			holdPlayer1[2] = "";
			holdPlayer1[3] = "";
			holdPlayer1[4] = "";
			holdPlayer1[5] = "";
			holdPlayer1[6] = "";

			
			
			
			holdPlayer2[0] = "if(havemetal(150,Town2)) {";
			holdPlayer2[1] = "}";
			holdPlayer2[2] = "int x;";
			holdPlayer2[3] = "x=5;";
			holdPlayer2[4] = "String y;";
			holdPlayer2[5] = "y=\"hello.\";";
			holdPlayer2[6] = "double z;";
			holdPlayer2[7] = "z=5;";
			holdPlayer2[8] = "boolean k;";
			holdPlayer2[9] = "k=true;";
			holdPlayer2[10] = "boolean k=false;";
			
			// Going to need to make it so you can't attack with other people's towns via exceptions.
			
		
			
			scriptReader(holdPlayer1, players.get(0));
			scriptReader(holdPlayer2, players.get(1));
	//		attackServerCheck(); 
		//	buildingServerCheck();
			
			
			bh.update(bh.getGraphics());
			
			
			
			
		}*/
	
	public ArrayList<Town> getTowns() {
		return getIteratorTowns();
	}
	public ArrayList<Town> loadTowns() {
		ArrayList<Town> players = new ArrayList<Town>();

		try {
			UberStatement stmt = con.createStatement();
		      ResultSet rs= stmt.executeQuery("SELECT tid from town");

		      while(rs.next()) {
		    	  int id = rs.getInt(1);
		    	  players.add(new Town(id,this));
		    	  
		    	  
		      }
		      rs.close();
		      stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return players;
	}
	public ArrayList<Player> getPlayers() {
		return getIteratorPlayers();
	}
	public ArrayList<Player> loadPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();

	try {
		UberStatement lchk = con.createStatement();
		UberStatement stmt = con.createStatement();
		UberStatement qstmt = con.createStatement();

		ResultSet lchkRS,qs;
		League league;
		GodGenerator God = this;
		      // First things first. We update the player table.
		      ResultSet rs= stmt.executeQuery("SELECT * from player");
		      String player;
		      Player p;
		      while(rs.next()) {
			    int ID = rs.getInt(1);
			     player = rs.getString(2);

			lchkRS = lchk.executeQuery("select * from league where pid = " + ID);
			
			if(lchkRS.next()) {
				 league = new League(ID,God);
				players.add(league);
			} else {
				
								// now here we load in quests.
				qs = qstmt.executeQuery("select qid,questcode,classname from Quest where activated = true and qid = " + ID);
			
				if(qs.next()) {
					p = loadQuest(ID,qs.getString(2),qs.getString(3));
				}
				else {
					if(ID==5) p = new Id(ID,God);
					else
					p = new Player(ID,God);
				}
				qs.close();
				
				players.add(p);

			}
			lchkRS.close();
	      
	      } 
		       
		      

	      rs.close();
	      stmt.close();
	      qstmt.close();
	      lchk.close();	
	      
	       rs= stmt.executeQuery("SELECT * from users");

	       while(rs.next()) {
	    	   
	    	   Hashtable r = new Hashtable();
	    	   r.put("uid",rs.getInt(1));
	    	   r.put("fuid",rs.getLong(2));
	    	   r.put("username",rs.getString(3));
	    	   r.put("password",rs.getString(4));
	    	   r.put("registration_date",rs.getTimestamp(5));
	    	   r.put("email",rs.getString(6));

	    	   accounts.put(rs.getString(3),r);
	    	   if(rs.getLong(2)!=0)
	    	   accounts.put(rs.getLong(2),r);

	       }
	       rs.close();
	} catch(SQLException exc) { exc.printStackTrace(); }
	return players;
	}
	public Player getPlayer(int pid) {
		
		int i = 0;
		if(getPlayers()==null) return null;
		while(i<getPlayers().size()) {
			if(getPlayers().get(i).ID==pid) return getPlayers().get(i);
			i++;
		}
		
		return null;
	}
	public Player getPlayerByFuid(long fuid) {
		
		int i = 0;
		if(getPlayers()==null) return null;
		while(i<getPlayers().size()) {
			if(getPlayers().get(i).getFuid()==fuid) return getPlayers().get(i);
			i++;
		}
		
		return null;
	}	public Player getPlayerByEmail(String email) {
		
		int i = 0;
		if(getPlayers()==null) return null;
		while(i<getPlayers().size()) {
			if(getPlayers().get(i).getEmail().equals(email)) return getPlayers().get(i);
			i++;
		}
		
		return null;
	}
	public int getPlayerId(String username) {
		int i = 0;
	//	System.out.println("Got here1.");

		if(getPlayers()==null) return 5;
	//	System.out.println("Got here2.");
		while(i<getPlayers().size()) {
			if(getPlayers().get(i).getUsername().toLowerCase().equals(username.toLowerCase())) return getPlayers().get(i).ID;
			i++;
		}

		return 5;
		/*
		int pid = 5;
		try {
			UberStatement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select pid from player where username = \"" + username +"\";");
			if(rs.next()) pid = rs.getInt(1);
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return pid; // Id!*/
	}
	
	public static double getProbabilityInDist(double height, double center, double width, int x) {
		return height*Math.exp(-Math.pow(x-center,2)/(2*Math.pow(width,2))); // this'll return the probability at that x.
	}
	public static double[] generateGaussianDist(int numOfPoints, double height, double center, double width) {
		double toRet[] = new double[numOfPoints];
		
		// f = height*exp(-(x-center)^2/(2width^2))
		
		// We'll be running from
		// 0 to height. and then back down again.
		// Solving for x...which is what we want, as we plug in for various heights.
		// ln((f/height)) * 2*width^2 = -(x-center)^2
		// +/- sqrt( -ln((f/height)) * 2*width^2 ) + center = x
		// f < height always, as height is max, so ln will be negative, so it will be positive.
		// so are gonna want f to scale up to height and then we switch formulae.
		int i = 0;
		double f = (height/((double) numOfPoints)); double mod = 1; boolean trip=false;
		while(i<toRet.length) {
			if(f>=height&&!trip){
				trip=true;
				mod=-1;
				f=(height/((double) numOfPoints));
			}
			
			toRet[i]=mod*Math.sqrt( -Math.log(f/height)*2*Math.pow(width,2)   )+center;
		//	System.out.println("for f of " + f + " I get  " + toRet[i]);
			f+=(height/((double) numOfPoints));
			i++;
		}
		return toRet;
		
	}
	public void growId() {
		
		// First adds a vertical strip, then a horizontal strip, of Id towns.
		
			try {
			//	growTileset(); // we grow the tileset first.
			   
				int numberOfTowns = getTowns().size();
				int maxX=0; int maxY=0;
				int i = 0;
				while(i<getTowns().size()) {
					if(!getTowns().get(i).getPlayer().isQuest()&&Math.abs(getTowns().get(i).getX())>maxX) maxX = Math.abs(getTowns().get(i).getX());
					if(!getTowns().get(i).getPlayer().isQuest()&&Math.abs(getTowns().get(i).getY())>maxY) maxY = Math.abs(getTowns().get(i).getY());

					i++;
				}
				System.out.println("maxX:" + maxX + " maxY: " + maxY);
				maxX+=4; maxY+=4;
				ResultSet rs;
				ArrayList<Integer> openSpotsX = new ArrayList<Integer>();
				ArrayList<Integer> openSpotsY = new ArrayList<Integer>();

				int x = -maxX;
				Town t;
				 int minDist=2; // There needs to be at least two distance from the nearest town.
				 Town t2;
				while(x<=maxX) {
					int y = -maxY;
					while(y<=maxY) {
						t = findTown(x,y);
						if(t.townID==0) {
							 i = 0;
							 double mindist=999999;
							while(i<openSpotsX.size()) {
									int otherX = openSpotsX.get(i);
									int otherY = openSpotsY.get(i);
									double dist = Math.sqrt(Math.pow(otherX-x,2) + Math.pow(otherY-y,2));
									if(dist<mindist) mindist = dist;
									
								i++;
							}
							
							int checkX = x-minDist;
							
							while(checkX<x+minDist) {
								int checkY = y-minDist;
								while(checkY<y+minDist) {
									t2 = findTown(checkX,checkY);
									if(t2.townID!=0) {
									double dist = Math.sqrt(Math.pow(checkX-x,2) + Math.pow(checkY-y,2));
									
									if(dist<mindist) mindist = dist;
									
									}
									checkY++;
								}
								checkX++;
							}
							if(mindist>=minDist) {
								openSpotsX.add(x);openSpotsY.add(y);
							}
							
							i=0;
							
						}
						
						y++;
					}
					x++;
				}
				
				
				//double numOfPts = (2*(maxX+3)*2*(maxY+3) - 2*maxX*2*maxY)/9;
				double numOfPts = openSpotsX.size();
				// so there is a city on average every 3x3 square, so we div by 9...
				// so the area calculation assumes every square spot is taken,
				// but in reality it's every...3x3. So we divide.
				int numToUse = (int) Math.ceil(4*numOfPts); // 4x for each res.
				
				boolean taken[] = new boolean[numToUse];
				// a,b,c a is height, b is center, c is width.
				// Height honestly doesn't matter here. It's the same damn curve, either way,
				// and the height gets incremented the same either way. Just put 1.
				// now for b - where do you want the average resEffect to be? .3? Okay.
				// now c is the width. .35 looks good.
				
			//	double gauss[] = generateGaussianDist(numToUse,1,-.1,.16);
				double gauss[] = generateGaussianDist(numToUse,1,0,.16);

			   /*    x=-(maxX+3);
		      while(x<=maxX+3) {
		    	  int y = -(maxY+3);
		    	  while(y<=(maxY+3)) {
		    		  if(Math.abs(x)>maxX||Math.abs(y)>maxY)
			    		 makeCity(x,y,gauss,taken);
		    		  y+=3;

		    	  }
	 
	    		  x+=3;
		    	  
		      }
		      */
				
				x = 0;
				while(x<openSpotsX.size()) {
					System.out.println("Adding town " + openSpotsX.get(x) + "," + openSpotsY.get(x));
					makeCity(openSpotsX.get(x),openSpotsY.get(x),gauss,taken);
					x++;
				}
		     
					
						      /*
						      mrs = mus.executeQuery("select COUNT(*) from town;");
								mrs.next();
								mrs.close();
								mrs = mus.executeQuery("select max(x) from town;");
								mrs.next();
								 maxX = mrs.getInt(1);
								mrs.close();
								mrs = mus.executeQuery("select max(y) from town;");
								mrs.next();
								 maxY = mrs.getInt(1);
								mrs.close();*/
								synchronized(Maelstrom) {
								Maelstrom.maxX=maxX+3;
								Maelstrom.maxY=maxY+3;
								}
								
								i = 0; Hashtable r; int maxCenterX=0; int maxCenterY=0;
								while(i<getMapTileHashes().size()) {
									r = getMapTileHashes().get(i);
									int centerx = (Integer) r.get("centerx");
									int centery = (Integer) r.get("centery");
									if(Math.abs(centerx)>maxCenterX) maxCenterX=Math.abs(centerx);
									if(Math.abs(centery)>maxCenterY) maxCenterY=Math.abs(centery);

									i++;
								}
								if(maxX>(maxCenterX+mapTileWidthX/2.0)||maxY>(maxCenterY+mapTileWidthY/2.0)){
									expandMap();
								}
							
		   /*   mus.execute("update player set chg = 2 where username = 'Id';");
		      
			mus.execute("commit;");
			mus.close();*/
			
			} catch(SQLException exc) { exc.printStackTrace(); }

		
	}
	public void growTileset() {
		/*
		 * How the fuck does this thing work? We basically can do each x. We can't randomize...we need to have
		 * objects distributed. 
		 * 
		 * Don't make this complicated: Specify how many features you want, and the average radius size. So say
		 * you want six features, then you randomly choose wood or water, height and width, and irregularity.
		 * Then you first insert the green, then you check to make sure none of the objects intersect one another
		 * or are too close, then you enter their hash of x,ys into the tileset.
		 * 
		 * At 0 irregularity it should be a circle. However, with each increasing dose of irregularity, the chance
		 * of the radius increasing or decreasing by 1 grows. And it keeps track of the irregularity. The irregularity
		 * can be averaged, too.
		 */
		
			try {
		UberStatement stmt = con.createStatement();
	     UberStatement mus = con.createStatement();
		
			ResultSet mrs = mus.executeQuery("select max(x) from tile;");
				mrs.next();
				int maxX = mrs.getInt(1);
				mrs.close();
				mrs = mus.executeQuery("select max(y) from tile;");
				mrs.next();
				int maxY = mrs.getInt(1);
				mrs.close();
				
				int irregavg=3; // use this to determine when to shrink or grow radius.
				double avgrsize=4; // average starting radius size of an object.
				double avgfeatureamt=(int) Math.round((((double) ((maxX+40)*(maxY+40)-maxX*maxY)*.075)/(((double) Math.pow(avgrsize,2))))); // average amount of features in an area.
			// so basically avgfeatureamt will give a density of these features per area...
				// or rather, the area/amt = density. So we find out how many avg features we can fit in with
				//avgrsize^2 and then multiply this by .15 so we only see them 15% of the time.
				System.out.println("avgfeatureamt:"+avgfeatureamt);
				int x=-(maxX+20);
	      
	      while(x<=maxX+20) {
	    	  int y = -(maxY+20);
	    	  while(y<=(maxY+20)) {
	    		  if(Math.abs(x)>maxX||Math.abs(y)>maxY)
		    		 
	    			 stmt.execute("insert into tile(x,y,tilename) values ("+x+","+y+",'g');");
	    		  // put all the greens in.
	    			  
	    		  y+=1;

	    	  }
 
    		  x+=1;
	    	  
	      }
		//	public double[] generateGaussianDist(int numOfPoints, double height, double center, double width) {

		int numOfPts=5;
		int numToUse = (int) Math.ceil(4*numOfPts); // 4x for each res.

		double gauss[] = generateGaussianDist(numToUse,1,avgfeatureamt/2.0,Math.round(((double) avgfeatureamt)/3.0));

		  int grand = (int) Math.round( ((double) gauss.length-1)*Math.random());
		  
		  int actfeat =(int) Math.round(gauss[grand]);
		  
		  int i = 0;
		  Hashtable object[] = new Hashtable[actfeat];
		  
		  while(i<actfeat) {
			  // now make each feature.
			  
			 // gauss = generateGaussianDist(numToUse,1,irregavg,.16);

			//   grand = (int) Math.round((gauss.length-1)*Math.random());
			  
			 // int actirreg =(int) Math.round(gauss[grand]); // irreg not in this version.
			  
			 
			 
			  
			  // now I have everything I kind of need.
			  
			  // so first we need to choose a center. We need to choose an x outside borderx
			  // and a y outside bordery that is on average more distant from the others.
			  
			  
			  object[i] = new Hashtable();
			  
		
			 
			  gauss = generateGaussianDist(numToUse,1,avgrsize/2.0,Math.round(((double) avgrsize)/6.0));

			   grand = (int) Math.round(((double)  gauss.length-1)*Math.random());
			  
			  double actrsize = Math.round(gauss[grand]);
			  System.out.println("actrsize:" +actrsize);
			  
			  
			  
		
			  boolean recalculate=true;
			  int xrand=0,yrand=0;
			  int max = (maxX+40)*(maxY+40)-maxX*maxY;
			  int counter = 0;
			  while(recalculate&&counter<max) {
			   xrand = (int) Math.round((20-actrsize)*Math.random());
			  double minneg =  Math.random();
			  if(minneg<.5) xrand+=maxX;
			  else xrand=-maxX-xrand; // so it goes either to the left or to the right.
			  
			   yrand = (int) Math.round((20-actrsize)*Math.random());
			   minneg = Math.random();
			  if(minneg<.5) yrand+=maxY;
			  else yrand=-maxY-yrand; // so it goes either to the left or to the right.
			  
			  int j = 0; boolean toosmall=false;
			  while(j<i) {
				  // need the distance between the other objects.
				  if(object[j]!=null) {
				  int tx = (Integer) object[j].get("x0");
				  int ty = (Integer) object[j].get("y0");
				  double r = (Double) object[j].get("r");
				//  System.out.println("r was " +r + " and actrsize was " + actrsize);
				//  System.out.println("Trying: " + Math.sqrt(Math.pow(tx-xrand,2) + Math.pow(ty-yrand,2)) +" but " +(Math.sqrt(2*Math.pow(r,2))+Math.sqrt(2*Math.pow(actrsize,2))) );
				  if(Math.sqrt(Math.pow((tx-xrand),2) + Math.pow((ty-yrand),2)) < (Math.sqrt(2*Math.pow(r,2))+Math.sqrt(2*Math.pow(actrsize,2)))) {
					//  System.out.println("I am too small.");
					  /// we're using the max distance in from a center point in a square here - clearly illustrated by using pythag. thoerem.
					  toosmall=true;
					  break;
				  }
				  }
				  j++;
				  
			  }
			  if(!toosmall) recalculate=false;
			  
			  
			  counter++;
			  
			  }
			  
			  if(!recalculate) {
			//  System.out.println("recalculate is " + recalculate);
			  object[i].put("x0",xrand);
			  object[i].put("y0",yrand);
			  object[i].put("r",actrsize);
			  }
			  
			  i++;
		  }
		  
		  
		  // now we have the objects. So now we simply do insertions into the db.
		  i = 0;
		  while(i<object.length) {
			  if(object[i]!=null) {
			   x = (Integer) object[i].get("x0");
			 int  y = (Integer) object[i].get("y0");
			 double  r = (Double) object[i].get("r");

			  // now we just need to figure out where the x and ys of the corners are.
			  double woodwat = Math.random();
			  String filename; String filefill;
			  if(woodwat<.5) {
				  filename="gtwa";
				  filefill="wa";
			  }
			  else {
				  filename="gtw"; // so it goes either to the left or to the right.
				  filefill="w";
			  }
			  

		
			 // sweet now we have the corners. Now we need to connect them!
			 
			 int yc = (int) (y-r), xc = (int) (x+r); // start at one edge and dance with it.
			 
			 int currx = (int) (x-r);
			 while(currx<=(xc)) {
				 int curry = (int) (y+r);
				 // so we go from the upper left corner down in y and then move over in x.
				 while(curry>=(yc)) {
					 if(curry==(y+r)&&currx!=(x-r)&&currx!=(x+r)) {
						 // then we're up top baby
						 stmt.executeUpdate("update tile set tilename = '" + filename + "bs' where x = " + (currx) + " and y = "+ (curry) + ";");

					 } else if(curry==(y-r)&&currx!=(x-r)&&currx!=(x+r)) {
						 // then we're on bottom
						 stmt.executeUpdate("update tile set tilename = '" + filename + "us' where x = " + (currx) + " and y = "+ (curry) + ";");

					 } else if(currx==(x+r)&&curry!=(y-r)&&curry!=(y+r)) {
						 // we're on the right.
						 
						 stmt.executeUpdate("update tile set tilename = '" + filename + "rs' where x = " + (currx) + " and y = "+ (curry) + ";");

					 } else if(currx==(x-r)&&curry!=(y-r)&&curry!=(y+r)) {
						 // on the bottom.
						 stmt.executeUpdate("update tile set tilename = '" + filename + "ls' where x = " + (currx) + " and y = "+ (curry) + ";");

					 } else  {
						 // we're in the middle somewheres.
						 stmt.executeUpdate("update tile set tilename = '" + filefill + "' where x = " + (currx) + " and y = "+ (curry) + ";");

					 } 
					 curry--;
				 }
				 
				 currx++;
			 }
			 
			 stmt.executeUpdate("update tile set tilename = '" + filename + "llc' where x = " + (x-r) + " and y = "+ (y+r) + ";");
			 stmt.executeUpdate("update tile set tilename = '" + filename + "lrc' where x = " + (x+r) + " and y = "+ (y+r) + ";");
			 stmt.executeUpdate("update tile set tilename = '" + filename + "ulc' where x = " + (x-r) + " and y = "+ (y-r) + ";");
			 stmt.executeUpdate("update tile set tilename = '" + filename + "urc' where x = " + (x+r) + " and y = "+ (y-r) + ";");
			  }
			  i++;
		  }
		  stmt.close();
		} catch(SQLException exc) {exc.printStackTrace();}

	}

	public boolean makeCity(int x, int y, double currGaussian[], boolean taken[]) throws SQLException {
		
		int i = 0; boolean found = false;
		while(i<taken.length) {
			if(!taken[i]){
				found = true;
				break;
			}
			i++;
		}
		
		if(!found) return false; // no more gaussian points in the pool.
		 UberStatement stmt;

	      stmt = con.createStatement();
	      ResultSet rs;
	      
	      // First things first. We update the player table.
	      
	      stmt.execute("start transaction;"); // it's logged in, starts transaction so data problems won't happen.
	
		double resEffects[] = new double[5];
		  double randCity = Math.random();
		  if(randCity>.7) {

		  int k = 0;
		  while(k<resEffects.length-1) {
			  // no ppl changing in this bitch.
			  
			  int grand = (int) Math.round((currGaussian.length-1)*Math.random());
		//	  System.out.println("My gaussian length is " + currGaussian.length);
			//  System.out.println("My random is " + grand);
			  while(taken[grand]) {
				  grand = (int) Math.round((currGaussian.length-1)*Math.random());
			  }
			  
			  resEffects[k]=currGaussian[grand];
			  taken[grand]=true;
			  k++;
		  }
		  } 
 		 double  rand = Math.random();
 		 int xmod=0;
 		 int ymod = 0;
		  if(rand<.33)
		  xmod=1;
		  else if(rand>.33&&rand<.66)
			  xmod=0;
		  else xmod=-1; // so each time we get a chance
		  // of a diff x, but x overall never repeats and is
		  // never out of sync.
		   rand = Math.random();

		  if(rand<.33)
  		  ymod=1;
  		  else if(rand>.33&&rand<.66)
  			  ymod=0;
  		  else ymod=-1;
		  if(findTown(x+xmod,y+ymod).townID!=0) {
			  return false; // Means there is a town there.
		  }
		 
		  
		  stmt.execute("insert into town (pid,townName,x,y,m,t,mm,f,pop,minc,tinc,mminc,finc,kinc,au1,au2,au3,au4,au5,au6) values (5,\"Town" + (x+xmod) + "-" + (y+ymod) + "\","
				  +(x+xmod)+","+(y+ymod)+",0,0,0,0,1," + resEffects[0] + "," + resEffects[1] + "," + resEffects[2] + "," + resEffects[3] + "," + resEffects[4] + ",0,0,0,0,0,0)");
		  rs = stmt.executeQuery("select tid from town where x = " + (x+xmod) + " and y = " + (y+ymod) + ";");
		  rs.next();
		  int tid = rs.getInt(1);
		  rs.close();
		 /*
			name        | varchar(50)      | NO   |     | NULL    |                |
			| slot        | int(11)          | NO   |     | NULL    |                |
			| lvl         | int(11)          | NO   |     | NULL    |                |
			| lvling      | int(11)          | YES  |     | NULL    |                |
			| ppl         | int(11)          | YES  |     | NULL    |                |
			| pplbuild    | int(11)          | YES  |     | NULL    |                |
			| pplticks    | int(11)          | YES  |     | NULL    |                |
			| tid         | int(10) unsigned | NO   | MUL | NULL    |                |
			| lvlUp       | int(11)          | NO   |     | NULL    |                |
			| deconstruct | tinyint(1)       | NO   |     | NULL    |                |
			| pploutside  | int(11)          | YES  |     | -1      |                |
			| bunkerMode  | int(11)          | YES  |     | 0       |                |
			| bid         | int(10) unsigned | NO   | PRI | NULL    | auto_increment |
			
			| Metal Mine                   |    0 |   3 |     -1 |    0 |        0 |        0 | 2273 |     0 |           0 |         -1 |          0 | 598 |

		  */
		  stmt.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp,deconstruct,pploutside,bunkerMode) values (" +
		  		"'Metal Mine',0,3,-1,0,0,0,"+tid+",0,0,-1,0);");
		  stmt.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp,deconstruct,pploutside,bunkerMode) values (" +
  		  		"'Timber Field',1,3,-1,0,0,0,"+tid+",0,0,-1,0);");
		  stmt.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp,deconstruct,pploutside,bunkerMode) values (" +
  		  		"'Manufactured Materials Plant',2,3,-1,0,0,0,"+tid+",0,0,-1,0);");
		  stmt.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp,deconstruct,pploutside,bunkerMode) values (" +
  		  		"'Food Farm',3,3,-1,0,0,0,"+tid+",0,0,-1,0);");
		  stmt.execute("commit;");
		  Town t = new Town(tid,this);
		  iteratorTowns.add(t);
		  getPlayer(5).towns().add(t);
		  rs.close();
		  
		  stmt.close();
		  return true;
	}
	
	public void restartServer() {
		int i = 0; Player Id=null;
		Id = getPlayer(5);
		
		// now we have Id, and the other players are off of the Iterators.
		Player p;
		while(i<getPlayers().size()) {
			 p = getPlayers().get(i);

		//	sendMail(p.getEmail(),p.getUsername(),"Email","Apologies for the Building errors!","This is an automated message from AI Wars. We wanted to apologize if you joined the game and were unable to build anything. We were unaware of this bug as we hadn't changed a thing in that section of the code. But we've found the bug and fixed it, and we hope you'll come back and try again!");
			i++;
		}
		System.out.println("Done email sending.");
		i= 0;
		
		try {
			UberStatement stmt = con.createStatement();
			/*
			 *  God             |
| Quest          --- |
| attackunit  ---   |
| autemplate   ---   |
| bldg      ---      |
| bugTable    ---    |
| checkTable  ----    |
| cloud    ----       |
| league   ----       |
| messages     ---   |
| permissions     |
| player    ----      |
| qpc   -----          |
| queue      ----     |
| raid         -----   |
| raidSupportAU  ---- |
| resolution    ----  |
| revelations   ----  |
| statreports   ---  |
| supportAU     -----  |
| town    ---        |
| tpr      ----       |
| trade         ----  |
| tradeschedule ---  |
| usergroupmember ---|
| usergroups     ---- |

			 */
			stmt.execute("delete from attackunit where pid != " +Id.ID);
			stmt.execute("delete from autemplate where pid != " +Id.ID);
			// anything else? quests...they mark as completed, this is incorrect.
			// needs to be gone completely.
			stmt.execute("update Quest set qid = 0");
			stmt.execute("delete from qpc;");
			stmt.execute("delete from ap;");
			stmt.execute("delete from usergroupmember;");
			stmt.execute("delete from usergroups;");
			stmt.execute("delete from messages;");
			
			stmt.execute("delete from supportAU;");
			stmt.execute("delete from raidSupportAU;");
			stmt.execute("delete from raid;");
			stmt.execute("delete from trade;");
			stmt.execute("delete from tradeschedule;");
			stmt.execute("delete from revelations where pid != 5;");
			stmt.execute("delete from cloud;");


			stmt.execute("delete from permissions;");
			stmt.execute("delete from tpr;");
			stmt.execute("delete from league;");

			stmt.execute("delete from revelations where pid != " + Id.ID);
			stmt.execute("delete from statreports;");
			
			stmt.execute("delete from queue;");
			stmt.execute("delete from bldg;");
			
			//stmt.execute("delete from tile;");
			stmt.execute("delete from invadable;");

		//	stmt.execute("insert into tile (x,y,tilename) values (0,0,'g');");

			ResultSet rs = stmt.executeQuery("select min(x^2+y^2),tid from town where pid = " + Id.ID);
			rs.next();
		//	System.out.println(rs.getInt(2));
			int tid = rs.getInt(2);
			rs.close();
			stmt.execute("delete from town where tid!=" + tid + ";");
			
			stmt.execute("delete from player where pid != " + Id.ID);
			stmt.execute("update town set x=0,y=0,au1=0,au2=0,au3=0,au4=0,au5=0,au6=0 where tid = " + tid + ";");
		//	growId();
			killGod=true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void deletePlayer(Player p, boolean playableAgain) {
		p.setBeingDeleted(true);
		/*
		 * This thing has got to delete the player. How does it do that? By
		 * giving all it's towns to itself.
		 * if playableAgain is true, then all towns' resincs get left alone, including
		 * capital, which could be retaken.
		 * 
		 */
		
		int i = 0; Player Id=getPlayer(5);
		ArrayList<Town> towns = p.towns();
		 i = towns.size()-1; // when we cycle through, the town gets stripped
		// and added back on to the end. So we start at the end and move down.
		Player pl; Town t; Trade tr; Town ptown; Raid r; AttackUnit a; ArrayList<Raid> attackServer;
		ArrayList<Trade> tres; Building b;
		 ArrayList<Player> players = getPlayers();
		 UserBuilding bldg[];
		while(p.towns().size()>0) {
			ptown = p.towns().get(0);
			giveNewTown(Id,ptown.townID,0,true,0,0); // we strip the town of support AU
			// and make it ready to go for Id.
			ptown.setTownName("Town"+ptown.getX()+"-"+ptown.getY());
			int x = 0; 

			while(x<players.size()) {
				int j = 0;
				pl = players.get(x);
				towns = pl.towns();
				while(j<towns.size()) {
					int k = 0;
					t = towns.get(j);
					tres = t.tradeServer();
					while(k<tres.size()) {
						tr = tres.get(k);
						if(tr.getTown2().townID == ptown.townID) tr.setTradeOver(true);
						
						k++;
					}
					
					k = 0;
					attackServer = t.attackServer();
					while(k<attackServer.size()) {
						r = attackServer.get(k);
						synchronized(r) {
						if(r.getTown2().townID == ptown.townID) r.setRaidOver(true);
						int z = 0;
						while(z<r.getAu().size()) { // no support units from this player can survive.
							if(r.getAu().get(z).getSupport()>0&&r.getAu().get(z).getOriginalPlayer().ID==p.ID) r.getAu().get(z).setSize(0);
							z++;
						}
						}
						k++;
					}
					k=0;
					while(k<t.getAu().size()) {
						a = t.getAu().get(k);
						if(a.getSupport()>0&&a.getOriginalPlayer().ID==p.ID) {
							a.setSize(0);
						}
						k++;
					}
					
					j++;
				}
				x++;
			}
			
			// now we cancel TSes.
			int y = 0;
			tres = ptown.tradeServer();
			ArrayList<TradeSchedule> tses = ptown.tradeSchedules();
			while(y<tses.size()) {
				tses.get(y).deleteMe();
				y++;
			}
			
			 y = 0;
		
			while(y<tres.size()) {
				tres.get(y).deleteMe();
				y++;
			}
			
			 y = 0;
			 	attackServer = ptown.attackServer();
				synchronized(attackServer) {
				while(y<attackServer.size()) {
					attackServer.get(y).deleteMe();
					y++;
				}
				}
				y=0;
				bldg = ptown.getPlayer().getPs().b.getUserBuildings(ptown.townID,"all");
			while(y<bldg.length) { // demolish all possible buildings.
				if(!bldg[y].getType().equals("Metal Mine")&&
						!bldg[y].getType().equals("Timber Field")&&
						!bldg[y].getType().equals("Manufactured Materials Plant")&&
						!bldg[y].getType().equals("Food Farm")) ptown.killBuilding(bldg[y].getBid());
				else ptown.findBuilding(bldg[y].getBid()).setLvl(3);
				y++;
			//	Id.getPs().b.demolish(ptown.bldg().get(y).getLotNum(),ptown.townID);
			}
			
			if(!playableAgain) {
				// if it's not playable again we need to make it not grabbable by a player!
				y = 0;
				while(y<ptown.getResEffects().length) {
					if(ptown.getResEffects()[y]==0) ptown.getResEffects()[y]=Math.random()*.3;
					y++;
				}
				ptown.setMemResEffects(ptown.getResEffects()); // we don't save them, normally.
			}
			// Now that all TSes have been killed, all incoming raids and trades turned away,
				// all outgoing stuff squashed, can move on.
			 
			
		}
		

		
		// remove them from their league
		
		if(p.getLeague()!=null) { // killing off their league.
			p.getLeague().deleteTPR(p.ID);
		}
		 i = 0; UserTPR[] tpr;
		while(i<getPlayers().size()) {
			if(getPlayers().get(i).isLeague()) {
				int j = 0;
				tpr = (getPlayers().get(i).getPs().b.getUserTPRs());
				while(j<tpr.length) {
					if(tpr[j].getPid()==p.ID)
						((League) getPlayers().get(i)).deleteTPR(p.ID);
					j++;
				}
				
			}
			i++;
		}
		
		
		// remove them from their quests

		i=0;
		ArrayList<QuestListener> activeQuests = p.getActiveQuests();
		while(i<activeQuests.size()) {
			activeQuests.get(i).destroy(p);
			i++;
		}
			
		// now we iterate.
		p.saveAndIterate(1); // This ought to make it so all that stuff above gets
		//deleted! Well, actually, Id will make sure that all of your attacks and stuff
		// get deleted when it iterates. This just runs through any delete mes we might
		// have missed.
		p.save();
		players.remove(p);

		try {
			UberStatement stmt = con.createStatement();
			stmt.execute("delete from attackunit where pid = " +p.ID);
			stmt.execute("delete from autemplate where pid = " +p.ID);
			// anything else? quests...they mark as completed, this is incorrect.
			// needs to be gone completely.
			if(p.isLeague()) {
			
					
					ResultSet rs = stmt.executeQuery("select tprid from tpr where league_pid = " + p.ID);
					UberStatement stmt2 = con.createStatement();
					while(rs.next()) {
						stmt2.execute("delete from permissions where tprid = " + rs.getInt(1));
					}
					stmt2.close();
					rs.close();
			
				stmt.execute("delete from tpr where league_pid = " + p.ID);

				stmt.execute("delete from league where pid = " + p.ID);
				
				int k = 0;
				while(k<players.size()) {
					if(players.get(k).getLeague()!=null&&players.get(k).getLeague().ID==p.ID) {
						synchronized(players.get(k).getLeague()) {
						players.get(k).setLeague(null);
						}
					}
					k++;
				}
			}
			stmt.execute("delete from qpc where pid = " +p.ID);
			stmt.execute("delete from ap where pid = " +p.ID);
			stmt.execute("delete from messages where pid_to = " + p.ID);
			stmt.execute("delete from usergroupmember where pid = " + p.ID);
			stmt.execute("delete from usergroups where pid = " + p.ID);
			stmt.execute("delete from revelations where pid = " + p.ID);
			stmt.execute("delete from statreports where pid = " + p.ID);
			stmt.execute("delete from invadable where pid = " + p.ID);
			
			stmt.execute("delete from player where pid = " + p.ID);

			stmt.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String generateCodes(int num) {
		
		try {
			UberConnection con = new UberConnection(accessurl,user,pass,this);

			UberStatement stmt = con.createStatement();
			stmt.execute("start transaction;");
			ArrayList<String> oldCodes = new ArrayList<String>();
			ResultSet rs = stmt.executeQuery("select * from access;");
			while(rs.next()) {
				oldCodes.add(new String(rs.getString(1)));
			}
			
			rs.close();
			// now we generate a new amount!
			// Since we're only doing up to 1000 players with this, we only need four numbers/letters.
			
			ArrayList<String> newCodes = new ArrayList<String>();
			String newC;
			SessionIdentifierGenerator s = new SessionIdentifierGenerator();
			
			while(newCodes.size()<=num) {
				newC = s.nextSessionId();
				
				int i =0; boolean copy=false;
				while(i<newCodes.size()){
					if(newCodes.get(i).equals(newC)) copy=true;
					i++;
				}
				i=0;
				
				while(i<oldCodes.size()){
					if(oldCodes.get(i).equals(newC)) copy=true;
					i++;
				}
				
				if(!copy) newCodes.add(newC);
			}
			
			int i = 0;
			while(i<newCodes.size()) {
				
				stmt.execute("insert into access(code) values('" + newCodes.get(i) +"')");
				i++;
			}
			stmt.execute("commit;");
			
			 i = 0; String toRet="";
			 while(i<newCodes.size()) {
				 toRet+=newCodes.get(i)+";";
				 i++;
			 }
			 
			stmt.close();
			con.close();
			 return toRet;

		}catch(SQLException exc) { exc.printStackTrace(); }
		return "";
	}
	
	public boolean checkCode(String code) {
		try {
			UberConnection con = new UberConnection(accessurl,user,pass,this);

			if(code.equals("uva")) return true;
			 	UberStatement stmt = con.createStatement();
			
				ResultSet rs = stmt.executeQuery("select * from access where code = '" + code + "';");
				
				if(!rs.next()) {
					rs.close(); stmt.close(); con.close();
					return false;
				}
				
			if(rs.getBoolean(3)) {
				rs.close(); stmt.close(); con.close();
				return false;
			}
			
			rs.close();
			stmt.close();
			con.close();
			
			return true;
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
		
		return false;
	}
	public boolean destroyCode(String code) {
		try {
			UberConnection con = new UberConnection(accessurl,user,pass,this);
			 
				UberStatement stmt = con.createStatement();
			
				ResultSet rs = stmt.executeQuery("select * from access where code = '" + code + "'");
				
				if(!rs.next()) {
					rs.close(); stmt.close(); con.close();
					return false;
				}
				
			
			
			rs.close();
			stmt.execute("update access set used = true where code = '" + code + "'");
			
			stmt.close();
			con.close();
			
			return true;
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
		
		return false;
	}
	public boolean programRunning(int ID) {
		int i = 0;
		while(i<programs.size()) {
			if(((Integer) programs.get(i).get("pid"))==ID&&((Thread) programs.get(i).get("Revelations")).isAlive()) {
				return true;
			}
			i++;
		}
		return false;
	}
	public boolean updateLastLogin(int pid) {
	/*	try {
		UberStatement stmt = con.createStatement();
		stmt.execute("update player set last_login = current_timestamp where pid = "+ pid);
		ResultSet rs = stmt.executeQuery("select last_login from player where pid = " +pid);
		if(rs.next()) getPlayer(pid).last_login=rs.getDate(1);
		rs.close();
		stmt.close();
		return true;

		} catch(SQLException exc) { exc.printStackTrace(); }*/

		Date today = new Date();

		getPlayer(pid).last_login =new Timestamp(today.getTime());
		
		getPlayer(pid).update(); // need to update you if you have any missing owedTicks.

		return true;
	}
	public boolean updateLastSession(int pid) {
		/*	try {
			UberStatement stmt = con.createStatement();
			stmt.execute("update player set last_login = current_timestamp where pid = "+ pid);
			ResultSet rs = stmt.executeQuery("select last_login from player where pid = " +pid);
			if(rs.next()) getPlayer(pid).last_login=rs.getDate(1);
			rs.close();
			stmt.close();
			return true;

			} catch(SQLException exc) { exc.printStackTrace(); }*/

			Date today = new Date();
			getPlayer(pid).numLogins++;
			getPlayer(pid).last_session =new Timestamp(today.getTime());
			

			return true;
		}
		
	public Town getTown(int tid) {
		// JUST GET THE TOWN, DON'T ABSTRACT EVERYTHING JUST YET!
		
		return findTown(tid);
	}
	public ArrayList<QuestListener> getAllActiveQuests() {
		int i = 0;
		ArrayList<QuestListener> activeQuests = new ArrayList<QuestListener>();
		while(i<getPlayers().size()) {
			
			if(getPlayers().get(i).isQuest()) activeQuests.add((QuestListener) getPlayers().get(i));
			i++;
		}
		return activeQuests;
		/*
		ArrayList<QuestListener> aq = new ArrayList<QuestListener>();
		try {
			UberStatement stmt = con.createStatement();
			//				qs = qstmt.executeQuery("select qid,questcode,classname from Quest where activated = true and qid = " + ID);

			ResultSet rs = stmt.executeQuery("select qid,questcode,classname from Quest where activated = true;");
			while(rs.next()) {
				aq.add(loadQuest(rs.getInt(1),rs.getString(2),rs.getString(3)));
			}
			rs.close();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return aq;
	}*/
	}
	
	public String getEntryPointURL(String country) { // returns entryPointURI for this country.
			Hashtable r = (Hashtable) entryPointURLs.get(country);
			
			if(r==null||((Double) r.get("ticksAtUpdate"))+3.0*3600.0/gameClockFactor<gameClock) {
		 try {        

			 String currency = "USD";
			 String customerKey = "bthgmprd";
			/* String toWrite = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
			 
			 "<requestMobilePaymentProcessEntrypoints"+
			 " xmlns=\"http://pay01.zong.com/zongpay\""+
				 " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
					" xsi:schemaLocation=\"http://pay01.zong.com/zongpay/zongpay.xsd\">"+

			 "<customerKey>"+customerKey+"</customerKey>"+
			 "<countryCode>"+country+"</countryCode>"+
			 "<items currency=\"" + currency + "\"/>"+
			"</requestMobilePaymentProcessEntrypoints>";
			 toWrite.replace("<","%3C");
			 toWrite.replace(">","%3E");
			 toWrite.replace("\"","%22");
			 toWrite.replace(" ","%20");*/
			 String toWrite = "https://pay01.zong.com/zongpay/actions/default?method=lookup&request=%3C?xml%20version=%221.0%22%20encoding=%22UTF-8%22?%3E%3CrequestMobilePaymentProcessEntrypoints%20xmlns=%22http://pay01.zong.com/zongpay%22%20xmlns:xsi=%22http://www.w3.org/2001/XMLSchema-instance%22%20xsi:schemaLocation=%22http://pay01.zong.com/zongpay/zongpay.xsd%22%3E%3CcustomerKey%3E"+
			 customerKey+"%3C/customerKey%3E%3CcountryCode%3E"+country+
			 "%3C/countryCode%3E%3Citems%20currency=%22"+currency+"%22/%3E%3C/requestMobilePaymentProcessEntrypoints%3E";
			 
			// URL url = new URL("https://pay01.zong.com/zongpay/actions/default?method=lookup&request="+toWrite);
			 URL url = new URL(toWrite);
		 

		 URLConnection urlc = url.openConnection();

//		 urlc.setRequestProperty("Content-Type","text/xml");

		// urlc.addRequestProperty("request",toWrite);
		 urlc.setDoOutput(true);
		 urlc.setDoInput(true);
		 
		 urlc.connect();
		// PrintWriter pw = new PrintWriter(urlc.getOutputStream());
		
		 System.out.println("Writing: "+ toWrite);
		// pw.write(toWrite);

		// pw.close();
		 System.out.println("Got here.");
		 BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));

		 String inputLine="";
		 String res_line;
		 while ((res_line = in.readLine()) != null) {

		 inputLine+=res_line;
		 
		 }
		 
		 in.close();
		 
		 r = new Hashtable();
		 String entryPointURL=null;
		 if(country.equals("US")||country.equals("GB")) {
		 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"1.0000\""),inputLine.length());
	//	 System.out.println("GB or US: " + entryPointURL);
		 } else if(country.equals("IN")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"50.0000\""),inputLine.length());

		 } else if(country.equals("CA")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"1.2500\""),inputLine.length());

		 }else if(country.equals("IE")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"2.0000\""),inputLine.length());

		 }
		 else if(country.equals("AU")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"2.5500\""),inputLine.length());

		 }
		 else if(country.equals("CH")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"1.0000\""),inputLine.length());

		 }else if(country.equals("ZA")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"10.0000\""),inputLine.length());

		 }
		 else if(country.equals("FR")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"1.5000\""),inputLine.length());

		 }else if(country.equals("BE")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"1.0000\""),inputLine.length());

		 }else if(country.equals("DE")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"1.9900\""),inputLine.length());

		 }else if(country.equals("ES")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"1.4160\""),inputLine.length());

		 }else if(country.equals("PT")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"2.0000\""),inputLine.length());

		 }
		 else if(country.equals("SE")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"7.0000\""),inputLine.length());

		 }else if(country.equals("DK")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"8.0000\""),inputLine.length());

		 }else if(country.equals("NL")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"1.5000\""),inputLine.length());

		 }else if(country.equals("NO")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"8.0000\""),inputLine.length());

		 }else if(country.equals("FI")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"1.0000\""),inputLine.length());

		 }else if(country.equals("CL")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"750.0000\""),inputLine.length());

		 }else if(country.equals("MX")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"15.1300\""),inputLine.length());

		 }else if(country.equals("CO")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"3600.0000\""),inputLine.length());

		 }else if(country.equals("TR")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"2.0000\""),inputLine.length());

		 }else if(country.equals("PL")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"2.4400\""),inputLine.length());

		 }else if(country.equals("HU")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"500.0000\""),inputLine.length());

		 }else if(country.equals("AT")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"2.0000\""),inputLine.length());

		 }else if(country.equals("CZ")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"20.0000\""),inputLine.length());

		 }else if(country.equals("RU")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"34.0000\""),inputLine.length());

		 }else if(country.equals("AR")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"4.8400\""),inputLine.length());

		 }else if(country.equals("VE")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"5.8000\""),inputLine.length());

		 }else if(country.equals("NZ")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"2.5000\""),inputLine.length());

		 }else if(country.equals("BR")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"4.9900\""),inputLine.length());

		 }else if(country.equals("PE")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"5.0000\""),inputLine.length());

		 }else if(country.equals("ID")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"1000.0000\""),inputLine.length());

		 }else if(country.equals("MY")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"3.5000\""),inputLine.length());

		 }else if(country.equals("GR")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"2.0600\""),inputLine.length());

		 }else if(country.equals("TW")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"10.0000\""),inputLine.length());

		 }else if(country.equals("PH")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"10.0000\""),inputLine.length());

		 }else if(country.equals("IT")) {
			 entryPointURL = inputLine.substring(inputLine.indexOf("exactPrice=\"1.0000\""),inputLine.length());

		 }
			 
		 entryPointURL = entryPointURL.substring(entryPointURL.indexOf("<entrypointUrl>")+15, entryPointURL.indexOf("</entrypointUrl>"));

		 System.out.println(entryPointURL);
		// r.put("entryPointURL",);
		 r.put("ticksAtUpdate",gameClockFactor);
		 r.put("entryPointURL",entryPointURL);
		 entryPointURLs.put(country,r);
		 }

		 catch (Exception e) {

		 //Threat the exceptions here
			 e.printStackTrace();

		 }
		 
		}
			
			return (String) r.get("entryPointURL");
	}
	
	public void makePaypalCall() {
		/*
		 * Credential	API Signature
API Username	arkavon_api1.hotmail.com
API Password	SRC3PY9HXQ74LEHL
Signature	
Request Date	Nov 19, 2010 07:59:04 PST

est Account:	jordan_1290192092_biz@gmail.com	Nov. 19, 2010 10:41:43 PST
API Username:	jordan_1290192092_biz_api1.gmail.com
API Password:	1290192103
Signature:	 AVlIy2Pm7vZ1mtvo8bYsVWiDC53rA4yNKXiRqPwn333Hcli5q6kXsLXs	
		 */
		// String toWrite = "https://pay01.zong.com/zongpay/actions/default?method=lookup&request=%3C?xml%20version=%221.0%22%20encoding=%22UTF-8%22?%3E%3CrequestMobilePaymentProcessEntrypoints%20xmlns=%22http://pay01.zong.com/zongpay%22%20xmlns:xsi=%22http://www.w3.org/2001/XMLSchema-instance%22%20xsi:schemaLocation=%22http://pay01.zong.com/zongpay/zongpay.xsd%22%3E%3CcustomerKey%3E"+
		// customerKey+"%3C/customerKey%3E%3CcountryCode%3E"+country+
		 //"%3C/countryCode%3E%3Citems%20currency=%22"+currency+"%22/%3E%3C/requestMobilePaymentProcessEntrypoints%3E";
		 
		// URL url = new URL("https://pay01.zong.com/zongpay/actions/default?method=lookup&request="+toWrite);
		String PWD = "SRC3PY9HXQ74LEHL";
		String USER = "jordan_1290192092_biz@gmail.com";
		String sig = "Ao2cz0vwzCWpPKantE6AVVvinKsRAXpaXbMqjOSmCS3LIGDoSwCMxjWf";
		String version = "52.0";
		String toWrite = "https://api-3t.sandbox.paypal.com/nvp?&METHOD=SetExpressCheckout&VERSION="+version+"&USER="+USER+"&PWD="+PWD+"&SIGNATURE="+sig;
		try {
		URL url = new URL(toWrite);
	 

	 URLConnection urlc = url.openConnection();

	 urlc.setDoOutput(true);
	 urlc.setDoInput(true);
	 
	 urlc.connect();	
	 System.out.println("Writing: "+ toWrite);
	
	 BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));

	 String inputLine="";
	 String res_line;
	 while ((res_line = in.readLine()) != null) {

	 inputLine+=res_line;
	 
	 }
	 System.out.println("Receiving: " + inputLine);
	 in.close();
	} catch(MalformedURLException exc) { exc.printStackTrace();} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	}
	
	public String returnPrizeName(int probTick, int x, int y, boolean test, PrintWriter out) {
		
		/*  1. Center of Nothing( Relative Mag: .5)
		 2. Center of 10% daily resources (Relative Mag: 1)
		 3. Center of 20% Daily resources and 50 KP (Relative Mag: 1)
		 4. Center of 30% Daily Resources, 65 KP (Relative Mag: 1)
		 5. Center of 50% Daily Resources, 80 KP (Relative Mag: 1)
		 6. Center of random API, of hidden soldier blueprint(Rock), random tech points(Civvie in Grass, Military in Desert) (Relative Mag: .5)
		 7. Center of hidden tank blueprint(Grass), and of the four resource increaser technologies(3 in Grass, 1 in Rock/Desert) (Relative Mag: .3)
		 8. Center of hidden Juggernaught blueprint(Desert) (Relative Mag: .1)
		 9. Center of hidden Missile Silo or Zeppelin(Silo in Rock, Zeppelin in Desert)(Relative Mag: .05)*/
		
		/*

		 * Prize codes:
		 * nothing
		 * daily10
		 * daily20
		 * daily30
		 * daily50
		 * lowkp
		 * medkp
		 * highkp
		 * api
		 * soldier
		 * civvietech
		 * militech
		 * tank
		 * resInc
		 * juggernaught
		 * silo
		 * zeppelin
		 */
		int i = 0;Hashtable r; String type = "none";
		while(i<mapTileHashes.size()) {
			r = mapTileHashes.get(i);
			int mapX =(Integer) r.get("centerx");
			int mapY = (Integer) r.get("centery");
			double dist = Math.sqrt(Math.pow(x-mapX,2) + Math.pow(y-mapY,2));
			if(dist<(((double) mapTileWidthX)/2.0)) {
				type = (String) r.get("mapName");
				break;
			}
			i++;
		}
		if(type.equals("none")) return "nothing";
		System.out.println("Found a map type of " + type);
		// So now that we know the types we can have, we just need to add up the probabilities
		// and normalize them.
		//	public static double getProbabilityInDist(double height, double center, double width, int x) {

		int hoursIn = (int) Math.round(probTick*GodGenerator.gameClockFactor/(3600));
		double nothingProb = getProbabilityInDist(nothingHeight,nothingCtr,nothingWidth,hoursIn);
		if(test) out.println("nothingProb is " + nothingProb);
		
		double daily10Prob = getProbabilityInDist(daily10Height,daily10Ctr,daily10Width,hoursIn);
		if(test) out.println("daily10Prob is " + daily10Prob);
		
		double daily20Prob = getProbabilityInDist(daily20Height,daily20Ctr,daily20Width,hoursIn);
		if(test) out.println("daily20Prob is " + daily20Prob);
		
		double daily30Prob = getProbabilityInDist(daily30Height,daily30Ctr,daily30Width,hoursIn);
		if(test) out.println("daily30Prob is " + daily30Prob);
		
		double daily50Prob = getProbabilityInDist(daily50Height,daily50Ctr,daily50Width,hoursIn);
		if(test) out.println("daily50Prob is " + daily50Prob);
		
		double lowKPProb = getProbabilityInDist(lowKPHeight,lowKPCtr,lowKPWidth,hoursIn);
		if(test) out.println("lowKPProb is " + lowKPProb);
		
		double medKPProb = getProbabilityInDist(medKPHeight,medKPCtr,medKPWidth,hoursIn);
		if(test) out.println("medKPProb is " + medKPProb);
		
		double highKPProb = getProbabilityInDist(highKPHeight,highKPCtr,highKPWidth,hoursIn);
		if(test) out.println("highKPProb is " + highKPProb);
		
		double apiProb = getProbabilityInDist(apiHeight,apiCtr,apiWidth,hoursIn);
		if(test) out.println("apiProb is " + apiProb);
		
		double soldierProb = getProbabilityInDist(soldierHeight,soldierCtr,soldierWidth,hoursIn);
		if(!type.equals("rock")) soldierProb=0;
		if(test) out.println("soldierProb is " + soldierProb);
		
		double techProb = getProbabilityInDist(techHeight,techCtr,techWidth,hoursIn);
		if(type.equals("rock")) techProb=0;
		if(test) out.println("techProb is " + techProb);
		
		double tankProb = getProbabilityInDist(tankHeight,tankCtr,tankWidth,hoursIn);
		if(!type.equals("grass")) tankProb=0;
		if(test) out.println("tankProb is " + tankProb);
		
		double resIncProb = getProbabilityInDist(resIncHeight,resIncCtr,resIncWidth,hoursIn);
		if(test) out.println("resIncProb is " + resIncProb);
		
		double juggernaughtProb = getProbabilityInDist(juggernaughtHeight,juggernaughtHeight,juggernaughtHeight,hoursIn);
		if(!type.equals("sand")) juggernaughtProb=0;
		if(test) out.println("juggernaughtProb is " + juggernaughtProb);
		
		double siloProb = getProbabilityInDist(siloHeight,siloCtr,siloWidth,hoursIn);
		if(!type.equals("rock")) siloProb=0;
		if(test) out.println("siloProb is " + siloProb);
		
		double zeppelinProb = getProbabilityInDist(zeppelinHeight,zeppelinCtr,zeppelinWidth,hoursIn);
		if(!type.equals("sand")) zeppelinProb=0;
		if(test) out.println("zeppelinProb is " + zeppelinProb);
		
		// now, to give every one a point between 0 and 100, to give them relative probabilities, we first must normalize
		// the distribution. We can do this by adding them all together, and saying 100 = (sum)*N, where N is the normalization factor,
		// and do 100/(sum) = N. Then we set up a series of points. We say for instance
		// nothingProbPoint = 0+nothingProb;
		// daily10ProbPoint=nothingProbPoint+daily10Prob 
		// Then we have to do if statements...a series of them, to detect which one, which is kinda shitty. But so be it.
		// To add new probabilities: 
		// 1. GOTTA GET NEW PROB FACTOR UP THERE.
		// 2. THEN ADD IT TO THE NORMALIZATION FACTOR CALCULATION
		// 3. SET UP IT'S PROB POINT
		// 4. SET UP IT'S IF STATEMENT
		
		double N = 100/(nothingProb+daily10Prob+daily20Prob+daily30Prob+daily50Prob+lowKPProb+medKPProb+highKPProb+apiProb+soldierProb
				+techProb+tankProb+resIncProb+juggernaughtProb+siloProb+zeppelinProb);
		
		double nothingProbPt = 0+nothingProb*N;
		if(test) out.println("nothingProbPt is " + nothingProbPt);
		
		double daily10ProbPt = nothingProbPt+daily10Prob*N;
		if(test) out.println("daily10ProbPt is " + daily10ProbPt);
		
		double daily20ProbPt = daily10ProbPt+daily20Prob*N;
		if(test) out.println("daily20ProbPt is " + daily20ProbPt);
		
		double daily30ProbPt = daily20ProbPt+daily30Prob*N;
		if(test) out.println("daily30ProbPt is " + daily30ProbPt);
		
		double daily50ProbPt = daily30ProbPt+daily50Prob*N;
		if(test) out.println("daily50ProbPt is " + daily50ProbPt);
		
		double lowKPProbPt = daily50ProbPt+lowKPProb*N;
		if(test) out.println("lowKPProbPt is " + lowKPProbPt);
		
		double medKPProbPt = lowKPProbPt+medKPProb*N;
		if(test) out.println("medKPProbPt is " + medKPProbPt);
		
		double highKPProbPt = medKPProbPt+highKPProb*N;
		if(test) out.println("highKPProbPt is " + highKPProbPt);
		
		double apiProbPt = highKPProbPt+apiProb*N;
		if(test) out.println("apiProbPt is " + apiProbPt);
		
		double soldierProbPt = apiProbPt+soldierProb*N;
		if(test) out.println("soldierProbPt is " + soldierProbPt);

		double techProbPt = soldierProbPt+techProb*N;
		if(test) out.println("techProbPt is " + techProbPt);

		double tankProbPt = techProbPt+tankProb*N;
		if(test) out.println("tankProbPt is " + tankProbPt);

		double resIncProbPt = tankProbPt+resIncProb*N;
		if(test) out.println("resIncProbPt is " + resIncProbPt);

		double juggernaughtProbPt = resIncProbPt+juggernaughtProb*N;
		if(test) out.println("resIncProbPt is " + resIncProbPt);
		
		double siloProbPt = juggernaughtProbPt+siloProb*N;
		if(test) out.println("siloProbPt is " + siloProbPt);
		
		double zeppelinProbPt = siloProbPt+zeppelinProb*N;
		System.out.println("zeppelinProbPt is " + zeppelinProbPt);
		
		
		double rand = Math.random()*100; // rand generated.
		
		if(rand<=nothingProbPt) {
			if(test) out.println("nothing");
			return "nothing";
		} else if(rand>nothingProbPt&&rand<=daily10ProbPt) {
			if(test) out.println("daily10");
			return "daily10";
		}else if(rand>daily10ProbPt&&rand<=daily20ProbPt) {
			if(test) out.println("daily20");
			return "daily20";
		}else if(rand>daily20ProbPt&&rand<=daily30ProbPt) {
			if(test) out.println("daily30");
			return "daily30";
		}else if(rand>daily30ProbPt&&rand<=daily50ProbPt) {
			if(test) out.println("daily50");
			return "daily50";
		}else if(rand>daily50ProbPt&&rand<=lowKPProbPt) {
			if(test) out.println("lowKP");
			return "lowKP";
		}else if(rand>lowKPProbPt&&rand<=medKPProbPt) {
			if(test) out.println("medKP");
			return "medKP";
		}else if(rand>medKPProbPt&&rand<=highKPProbPt) {
			if(test) out.println("highKP");
			return "highKP";
		}else if(rand>highKPProbPt&&rand<=apiProbPt) {
			if(test) out.println("api");
			return "api";
		}else if(rand>apiProbPt&&rand<=soldierProbPt) {
			if(test) out.println("soldier");
			return "soldier";
		}else if(rand>soldierProbPt&&rand<=techProbPt) {
			if(type.equals("grass")) {
				if(test) out.println("civvietech");
				return "civvietech";
			} else if(type.equals("sand")){
				if(test) out.println("militech");
				return "militech";
			} else {
				if(test) out.println("nothing - though this is in error, you shouldn't get techs in non-grass non-sand environments..");
				return "nothing";
			}
				
		}else if(rand>techProbPt&&rand<=tankProbPt) {
			if(test) out.println("tank");
			return "tank";
		}else if(rand>tankProbPt&&rand<=resIncProbPt) {
			if(test) out.println("resInc");
			return "resInc";
		}else if(rand>resIncProbPt&&rand<=juggernaughtProbPt) {
			if(test) out.println("juggernaught");
			return "juggernaught";
		}else if(rand>juggernaughtProbPt&&rand<=siloProbPt) {
			if(test) out.println("silo");
			return "silo";
		}else if(rand>siloProbPt&&rand<=zeppelinProbPt) {
			if(test) out.println("zeppelin");
			return "zeppelin";
		}
		
		if(test) out.println("found nothing by error.");
		return "nothing";
	}
	public  void convertPlayers() {
		// converts players from the old system to the new one.
		// assumes passwords are ALREADY encrypted.
		int i = 0;
		Player p;
		UberStatement stmt;
		try {
			stmt = con.createStatement();
			ResultSet rs;
		while(i<getPlayers().size()) {
			p = getPlayers().get(i);
			if(!p.isLeague()&&!p.isQuest()&&p.ID!=5) {
				String username = p.getUsername();
				
				String password = p.getPassword();
				
				String email = p.getEmail();
				try {
				stmt.execute("insert into users(fuid,username,password,email) values ("+p.getFuid()+",\""+username+"\",\""+password+"\",\""+email+"\");");
				rs = stmt.executeQuery("select uid,password,registration_date from users where username = '"+username+"';");
				rs.next();
				Hashtable r = new Hashtable();
				  r.put("uid",rs.getInt(1));
		    	   r.put("fuid",0);
		    	   r.put("username",username);
		    	   r.put("password",rs.getString(2));
		    	   r.put("registration_date",rs.getTimestamp(3));
		    	   r.put("email",email);
		    	   accounts.put(username,r);
		    	 if(p.getFuid()!=0)
		    		 accounts.put(p.getFuid(),r);
		    	   rs.close();
				// make new accounts.
				} catch(SQLException exc) { exc.printStackTrace(); }
				
				
			}
			i++;
		}
		stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public static int sendMail(String email, String name, String promotion, String subject, String message) {
		HttpClient httpClient = new HttpClient();
		PostMethod method = new PostMethod("https://api.madmimi.com/mailer");
		method.addParameter("username", "jordanmprince@gmail.com");
		method.addParameter("api_key", "4d252c5ee17c5f178e23751ca0f37145");
		method.addParameter("promotion_name", promotion);
		method.addParameter("recipient",name+ " <" + email + ">");
	//	method.addParameter("recipient","Jordan Prince <jordanmprince@gmail.com>");
		method.addParameter("subject",subject);
		method.addParameter("from", " AI Wars <donotreply@aiwars.org>");
		//String plaintext = message;
		method.addParameter("raw_plain_text", message);
		try {
			int statusCode = httpClient.executeMethod( method );
			return statusCode;
		//	System.out.println(statusCode);
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


  class SessionIdentifierGenerator
{

  private SecureRandom random = new SecureRandom();

  public String nextSessionId()
  {
    return new BigInteger(130, random).toString(32);
  }

}


