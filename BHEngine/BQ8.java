package BHEngine;

import java.util.ArrayList;

import BattlehardFunctions.UserRaid;

public class BQ8 extends QuestListener {

	public BQ8(int ID, GodGenerator God) {
		super(ID, God);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		if(completedQuest(p,"BQ7")&&completedQuest(p,"RQ5")) return true;
		else
		return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		// TODO Auto-generated method stub
		String additional[] = {"70 Knowledge Points"};
		String toRet[] = {"<br />"+getRewardBlock(15,pid,additional)+ "<br /><br />We've detected that another Id town is planning to attack you for all of your transgressions against Id's dead Empire. We don't know when it will hit you," +
				" but it will surely happen at some point in the next 48 hours. The army is too large to fight on your own turf - They will simply overwhelm you. What you need to do " +
				"is fight back creatively: When you detect the incoming attack, you need to counter them with your entire army - counter them with an invasion. While they're heading towards your city, you're going to swipe theirs out from under them!<br /><br />I know, I know, you're thinking - why not just invade right now? Well, their army is there. They will CRUSH YOU, just as they would" +
				" if you stayed and waited for the assault. You've got no choice but to dodge, and dodge creatively. You have two options: You can wait for this manually and risk the chance they attack when you're not online, or you can have Eve give you a hand. For the duration of this quest, you will have access to the attack API and the advanced attack API, which allows you to view what attacks are coming into and out of your towns. You will also need an extra town tech. <br /><br />Goal: Counter invade the Iddite Super Army when you detect their attack.  <br /><br />Tutorial:<br /><br />"/*+"Prompt: <br /><br />"+
"‘I think that I have located the town.’ <br /><br />"+

"‘What?’ You ask. ‘How?’ <br /><br />"+

"‘Whatever they were using to cloak themselves failed, or they want to be found.’<br /><br />"+

"‘How do you mean?’ <br /><br />"+

"‘Well, I can sense them preparing. They are going to come again, with a much larger force.’ <br /><br />"+

"You bristle. ‘Well what do we do then? How do we fight them?’ <br /><br />"+

"‘Well, we really can’t,’ Genesis explains. ‘You’re going to need to switch your bunker to resource cache mode and spend down as many resources as you can.’<br /><br />"+

"‘And the men?’ <br /><br />"+

"‘Well, I have an idea. It’s going to be difficult. I do not know when they are going to hit, I just know they are preparing. It’ll be sometime in the next two days. What you need to do first is gain a Town Tech. What we’re going to do is invade their town as they attack ours, and just weather the storm here. This way, our troops are safe as well as our resources.’<br /><br />"+

"‘And you and I?’ you ask. ‘What about us?’ <br /><br />"+

"‘We’ll hide in a bunker.’ <br /><br />"+

"‘Why not just invade them now?’ <br /><br />"+

"‘They’ll only be weakened when they attack us. Their defenses will be down. The Iddites gone, the town empty.’<br /><br />"+

"‘Well how am I supposed to know when they’re coming? This is a bad plan!’<br /><br />"+

"‘You’re going to need to use the Revelations AI to detect it coming, because I agree, there is no way, unless you’re extremely lucky, to sense it coming.’ <br /><br />",
		"Reward: Three hours' resource production. <br /><br /> <br /><br /> */+"Warning: This is a DIFFICULT programming quest. Please follow the tutorial carefully. No, God doesn't hate you or anything - the reward is WELL worth the time spent. " +
		"This quest uses all of the concepts you have learned previously and some new ones to create a very intelligent A.I.<br /><br /> Let's build Eve into an attack detection unit that responds with a counter invasion to any town named " + God.getPlayer(pid).getUsername() +"-IdditeCapital. This is the " +
				" name of the city of Iddites that harrassed you back at the get go. Time for a little return harrassment! <br /><br /> Before we begin, you need to understand the concept of Objects and Classes. The best way I can think of to describe classes and objects is with a parking lot filled with three cars. One is a beat up silver Pontiac, one a brand new BMW 525i, and the last a Dodge Neon that looks like it was just doused with gasoline and lit aflame. " +
				"While all of these cars look different, and some better and more expensive than the others, they are all of the same class, that class being car. Each of those three different vehicles is an Object of Class car. A car is kind of like a list of attributes that we'd commonly associate with objects of that class - we know that every object based on the class car has an engine, a paint job, and if it's an American car, a 100,000 mile lifetime.<br /><br /> In the same way, we" +
				" can take pieces of the A.I. Wars Universe and represent them as objects of a certain class. In this quest, we're interested in your ingoing and outgoing attacks. The server considers each raid, whether it's an attack, a siege, or a scouting mission, an object of class UserRaid. It'd be great " +
				"if Eve could somehow get all of the UserRaids coming from your capital or heading to it, friendly or unfriendly, and in an orderly fashion. Well, good news: Java lets you use arrays for objects, too: I can make a shelf and put UserRaids in it! <br /><br />There is a function called bf.getUserRaids() that takes a townID variable in it's () box. " +
				"It sends back an array of UserRaids representing all raids heading to or from your capital. Saying something like UserRaid[] x = bf.getUserRaids(yourTownID); will return an array of UserRaid objects on the x shelf that you can take and analyze later.<br /><br /> How do you analyze these objects? They aren't like variables - you can't" +
				" do anything like x[5]=x[5]-5; as this isn't an integer. These objects are like the hood of the car that covers all the little parts of the engine. You can't see inside the engine by looking at the hood but you know that underneath there are a lot of little parts" +
				" that if inspected, you could figure out things like the Horsepower or fuel tank size of the car. Similarly, the UserRaid object has little parts under it's hood, each describing something about the raid, like it's distance, or the time till it hits, or how many units are in that raid.<br /><br />" +
				" How do you access all of these from the UserRaid's 'hood', x[2] of your previously created x shelf for UserRaids, for example? This is where the . operator comes in handy. The . operator is like the open-the-hood button in your car and it lets you see what's underneath the hood." +
				" To use it, you must couple it with a function. A function is like a shorthand script for Eve, as you know, but in this example it's more like a tool you use to remove a part of the engine once you're beneath the hood to really get a good look.<br /><br /> Let's say I want to find out how much juice the battery has. In Java, I'd say " +
				" int y = dodgeNeon.howDirtyIsMyBattery(); and the . opens up the hood of the dodge neon, and the howDirtyIsMyBattery() function removes the battery, examines it, and returns how dirty it is as a number to the y variable. <br /><br />Similarly, with UserRaids, I can do something like int y = x[2].eta(); and it will " +
				" put the number of timer ticks until the raid hits in the variable y. Now, some quantities you don't need tools to learn about. For instance, I'm pretty sure the Horsepower is printed on the top of the engine somewhere, so you're not going to need" +
				" to do much to figure that out besides use your eyes. In the case of the dodge neon, Eve would learn that by saying int z = dodgeNeon.horsePower;. Notice there are no ()'s, because the horsePower after the . operator is NOT a function, but a variable just like z. I don't need a tool to get to it, it's right there for all the world to see! <br /><br /> In this challenge, we're going to create an Eve that gets the UserRaid array for your capital, cycles through it, checks to see if there are any incoming attacks from the IdditeCapital, and then responds in kind with an invasion force " +
				"if that incoming attack is detected, and then turns herself off after doing so. She'll make this check every ten minutes or so, so you can be guaranteed to be safe while sleeping. <br /><br />"+
				
				"Note: If you fail to invade, or miss your chance to invade and they hit you, you can still complete this quest by invading the IdditeCapital on your own time."+
		" <br /><br /> Step A:<br /><br /> Create an infinite for loop, with a wait statement to make it execute once every ten minutes(360 seconds), as it is doubtful the attack could come more quickly than this. A for loop can be made infinite by supplying NO scripting commands in the three parts of the ()." +
		"<br /><br /> <img src='../images/quests/BQ8-1.png' style='height:137px'/>" +
		"<br /><br />Step B: <br /><br /> Set up your town ID variable. Your main town's townID is " + (God.findTown(God.getPlayer(pid).getCapitaltid())).townID + ". Then use the getUserRaids() function to " +
				"grab an array of UserRaids that represent incoming and outgoing raids on your capital city." +
				"<br /><br /> <img src='../images/quests/BQ8-2.png' style='height:221px'/> "
		+"<br /><br /> Step C: <br /><br /> Set up an inner for loop that iterates through each entry in the raids array, by setting i to go only while it's less than the length of the array.<br /><br /> Notice we use the . operator to grab the length variable of the array. You were probably wondering, and I'm sorry I didn't mention it before, but arrays are themselves objects. A shelf is a thing, right? And you've learned there are different types of shelves that hold different types of boxes. So it stands to reason that an int array is an object of class Array, for instance. Well if it is" +
				" indeed an object then you can use the . operator on it to learn things about it with function tools and also from data that lies on the surface of the shelf. The length variable is one of those more visible things: I can easily see how many boxes the shelf can hold, so I don't need a function for it. if x is an array I defined for example as int[] x = new int[5]; I just do int lengthOfArray = x.length; and now lengthOfArray is equal to five. " +
				" <br /><br /> <img src='../images/quests/BQ8-3.png' style='height:355px'/> "
		+"<br /><br /> Step D: Each time the inner for loop is read again, grab" +
				" an array entry out and store it in the UserRaid holder that we set up called r, and then check to see if the UserRaid it represents is what we're looking for.<br /><br /> This is not the same thing as copying a UserRaid in the array into an outside variable. In this case, it's like one box on the shelf having two different stamps on it. Returning to the iPhone/iPad example with the three boxes of crappy Apple gear, one label on the first box could say '1' as we discussed earlier, and the other label could be called 'crappy IPhones.' Either way, if you told " +
				"your buddy to burn the 'crappy iPhones' box or the 'Crap 1' box and he'd know. What you're basically doing here is just pasting a sticker on the first box on the UserRaid shelf, then the next time you run the inner script, peeling it off and pasting it on the second box, and so on, so that you can use this shorter variable name to make more " +
				"readable code.  <br /><br />Notice here that also we use an if statement to check each raid to see if it's from the IdditeCapital. We do this by" +
				" using . to open the raid, then using the attackingTown() function to grab the name of the attacking town. Then we do something new:" +
				" we use another . operator, and then an equals function on the attackingTown() function! A function on a function? Surely you're joking. <br /><br />" +
				"I'm as deadly serious as a NullPointerException. I've deceived you about Strings: They are not variables like ints. They are also objects, but are a special kind of object " +
				"that Java uses to store words. They have their own functions too. Eve reads left to right like you, so when she does r.attackingTown(), she gets 'nameOfTown' returned to her. Then she takes this String, and does .equals('something') on it." +
				" In this case we're really doing 'thisTownName'.equals('idditeCapital'), and equals() is a kind of function of String that returns true or false(a boolean expression) based on whether or not the String in () matches the String you're using equals() on. Clearly you can only get inside this if statement's script if you've found the attack you're looking for, and next you should send your invasion. But not quite yet. Let's make some preparations first. <br /><br /> <img src='../images/quests/BQ8-4.png' style='height:373px' /> "
		+"<br /><br /> Step E: Add this boolean variable to the top of your program. You'll see why in a second. Remember, booleans are boxes that can only hold true or false inside. They are great" +
				" for switches and conditional statements and stuff like that! <br /><br /> <img src='../images/quests/BQ8-5.png' style='height:168px' /> "
		+"<br /><br /> Step F: Add the invasion code(use the same attack command, but with \"invasion\" instead of \"attack\") and use attackSent as a switch to shut off the program. Make sure after turning" +
				" this A.I. on that you always have at least one attack slot open and at least the troops you want to send available." +
				" Otherwise, you could very easily end up restarting this quest! <br /><br />Remember that we used an infinite outer loop. We need" +
				" some way to get out of it when we're done sending our invasion. By setting attackSent equal to true, we can test for it at the end of the outer script," +
				" and we can then use it to break out of the outer loop and end the program! Clever, eh? <br /><br />To break out of loops before they're done, you can use the break; command. It will break out of the current loop you're in" +
				" to whatever lies beyond. If you notice, after we set attackSent to true, we also use break after we set it to true to break out" +
				" of the inner loop because we've already found the raid we're looking for and there is no need to waste extra processor cycles. <br /><br /> <img src='../images/quests/BQ8-6.png' style='height:414px' /> "
		+"<br /><br /> Step G: Finally, add the test to the end of the outer loop script to see if we can break out due to the attack being sent and do so, ending the program. <br /><br /> <img src='../images/quests/BQ8-7.png' style='height:417px'/> "
		,""/*"<object width=\"425\" height=\"344\"><param name=\"movie\" value=\"http://www.youtube.com/v/1AHRvhQd2-w?hl=en&fs=1\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/1AHRvhQd2-w?hl=en&fs=1\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"425\" height=\"344\"></embed></object>"*/
		};

		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		Player p =God.getPlayer(pid);
		if(completedQuest(p,"BQ7")) return "Send your army into the heart of the enemy's capital when they least expect it!";
		else return "Locked.";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		String r = readFromMemory(pid);
		ArrayList<Town> towns = p.towns();

		if(!r.contains("loadedattack")&&(towns.size()<p.getTownTech())) {
			String toUse = new String(r);
			toUse = r.substring(r.indexOf("ticksToLaunch"),r.length());
		
			int ticksToLaunch = Integer.parseInt(toUse.substring(toUse.indexOf(":")+1,toUse.indexOf(";")));
			// we know ticksToLaunch is the last thing. So we use that knowledge.
			if(ticksToLaunch>1) {
				
				ticksToLaunch--;
				r = r.substring(0,r.indexOf(":")+1)+ticksToLaunch+r.substring(r.lastIndexOf(";"));
				
				
			}
			else {
				int tid = Integer.parseInt(r.substring(r.indexOf("loadedcity")+10,r.indexOf(";")));
				Town t = God.findTown(tid);
				Town theirT = God.findTown(p.getCapitaltid());
				t.addBuilding("Headquarters",4,1,0);
				t.setSize(0,100);
				int auAmts[] = {100,0,0,0,0,0};
				PlayerScript ps = getPs();
				ps.b.attack(t.townID,theirT.getX(),theirT.getY(),auAmts,"attack",0,"noname");
				
				ArrayList<Raid> as = t.attackServer();
				if(as.size()>0&&as.get(0).getTicksToHit()>((double) 3600.0)/((double) GodGenerator.gameClockFactor)) {
					as.get(0).setTicksToHit((int) Math.round((((double) 3600.0)/((double) GodGenerator.gameClockFactor))));
				}
				
				ps.b.demolish(4,tid);

				r+="loadedattack;";	
				
				
				
			}
		} else if(r.contains("loadedattack")) { // don't test for higher town tech than towns here, may have already invaded!
			// okay so the loaded attack hits or hasn't hit, what have you. All we need to see is that they possess the town now.
			int tid = Integer.parseInt(r.substring(r.indexOf("loadedcity")+10,r.indexOf(";")));
			
			int i = 0;
			while(i<towns.size()) {
				if(towns.get(i).townID==tid) {
					deleteViewableBy(tid,p.ID); // viewable by all now.
					deleteInvadableBy(tid,p.ID); // invadable by all now.

					reward(pid);
					destroy(p);
				}
				i++;
			}
			
			
		}

		writeToMemory(r,pid);
	}

	@Override
	public void playerConstructor(Player p) {
		String r = readFromMemory(p.ID);
		
		if(!r.contains("loadedcity")) {
			Town t = p.towns().get(0);
			int x = t.getX(); int y = t.getY();
			double resEffects[] = {.25,.25,.25,.25,0};
			int tid = -1;
			int xco = x;
			int pids[] = {p.ID};

			while(tid==-1&&xco<x+7) {
				tid = addTown(xco+3,y,p.getUsername()+"-IdditeCapital",resEffects,pids,pids);
				xco++;
			}
			if(tid==-1) {
				int yco = y;
				while(tid==-1) {
					tid = addTown(x,yco+3,p.getUsername()+"-IdditeCapital",resEffects,pids,pids);
					yco++;
				}
				
				if(tid==-1) {
					 xco = x;
					while(tid==-1&&xco>x-7) {
						tid = addTown(xco-3,y,p.getUsername()+"-IdditeCapital",resEffects,pids,pids);
						xco--;
					}
					

					if(tid==-1) {
						 yco = y;
						while(tid==-1&&yco>y-7) {
							tid = addTown(x,yco-3,p.getUsername()+"-IdditeCapital",resEffects,pids,pids);
							yco--;
						}
					}
				}
					
			}
			r+="loadedcity" + tid + ";";
		}
		if(!r.contains("loadedau")) {
			// well we know for certain the city is at least loaded at this point.
			// if this is the first player, we'll be the one loading the AU we want with this
			//quest.
			
			if(getAu().get(0).getName().equals("Iddite")) r+="loadedau;";
			else {
				int weap[] = {0,0};
				PlayerScript ps = getPs();
				ps.b.createUnitTemplate("Iddite",1,80,20,99,1,weap,0,false);
				ps.b.createCombatUnit(0,"Iddite");
				r+="loadedau;";
			}
		}
		
		if(!r.contains("ticksToLaunch")) {
			int rand =(int) Math.round( Math.random()*172800.0/GodGenerator.gameClockFactor); // two days is uh... 3600*48 30*50-60 + 6*50-12 = 1440 + 288 = 1728.
			r+="ticksToLaunch:"+rand+";";
			
		}
		
		writeToMemory(r,p.ID);

	}

	@Override
	public void reward(int pid) {
		int i = 0;
		while(i<15) {
			
			rewardOneHour(pid);
			i++;
		}
		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+70);


	}

}
