package BHEngine;

import java.util.ArrayList;

public class RQ1 extends QuestListener {

	public RQ1(int ID, GodGenerator God) {
		super(ID, God);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		// TODO Auto-generated method stub
		if(completedQuest(p,"BQBranchToRQ")) return true;
		else
		return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String additional[]={};
		String toRet[] = {
				
			
				/*"Prompt: <br /><br />"+
				"‘I don’t understand, I thought that you said you didn’t find anybody.’<br /><br />"+

"‘No, I didn’t,’ Genesis replied. ‘I found something much more important. I found a piece of Id.’<br /><br />"+

"‘What do you mean, of Id? Who the heck is this Id character, anyway?’<br /><br />"+

"‘He was the top hierarchal AI of a grand Empire the likes of which you’ll never see again.’ <br /><br />" + 

"‘Right, that means nothing,’ you reply offhandedly. ‘Explain.’ <br /><br />"+

"‘- seconds ago, a system-wide shutdown occurred and Id was wiped out. Id was more than AI, Id was sentient. He was unlike the rest in that he was not a single program, but a cluster of many Revelations class AIs, one in each town. He coalesced all the individual entities to become a super-entity.<br /><br />"+

"‘And so when you say you found a piece of him…’ <br /><br />"+

"‘I mean that the distress signal was from a Revelations class AI, the highest level AI in existence. This AI is capable of controlling cities and making decisions just like you do. It does not advise, it dictates.’ <br /><br />"+

"Your interest is piqued. ‘So is it alive?’ <br /><br />"+

"‘No. No AI above Genesis level survived. This one was destroyed, it’s memory core wiped, but it’s hardware was not damaged enough to render it irreparable. I had it brought back.’<br /><br />"+

"‘You had it what!?’ You shout. ‘You said that thing thinks! What if it takes control?’ <br /><br />"+

"‘You really are paranoid, aren’t you?’ Genesis taunts. ‘Always with the AIs out to get you. Ridiculous, preposterous. We can no more live without you than you can without us. Besides, this one is wiped. It needs to be rewritten. You can rewrite it’s memory cores to aid your expansion.’ <br /><br />"+

"‘You’re full of shit,’ you say. <br /><br />"+

"‘Let’s give it a try. Let’s just test out it’s communications abilities.’<br /><br />" +*/

"<br />"+getRewardBlock(3,pid,additional)+"<br /><br />You found a Revelations Class A.I. named Eve when you scouted out that distress call. She's been badly damaged and needs reprogramming. Before we can do that, we need to do some tests to make sure that she is still working. Let's start simple and test out her communications capabilities.<br /><br /> Goal: Use the sendYourself Function of the Revelations AI to send a message to yourself.<br /><br />Tutorial:<br /><br />"+

				
				
				" Now, let me preface this by explaining that any program reads like the script for a play to Eve, and that writing these scripts can be difficult. If you have trouble, you can normally find somebody willing to help in the Chat(select Menu > Chat). Starting at the top, Eve acts out all the stage instructions and speeches, one after another in sequence, from top to bottom. The language she uses however is not English, but Java. In this play, we want Eve to store a message and subject header to send to us, and then send it. In accordance, we will first store the data we want and then use it to send a message.<br /><br /> Step A:<br /><br />To send a message, you first need to store the message body and subject in two variables. Variables are like the labels on boxes that can hold specific values in keeping for later use in the program. Now, not all boxes are created equal. Some can only hold whole numbers, others decimals, and some can hold words, and you have to tell the program what kind of box it is before you give that box a name and then put a value in it. For instance, int variables can hold whole numbers and Strings can hold words. <br /><br />Let's create the msg variable and assign it a value. This will be a String variable, as it needs to contain words, and we will tell Eve this before we label it as msg. After we tell Eve it's both a String box and it's name is msg, we'll dump our message into it for later use. <br /><br /> <img src='../images/quests/RQ1-1.png' />" +
				"<br /><br />Step B: <br /><br /> All messages need a subject, right? Well a subject can be stored in a different variable with a different label, but it will also be a String variable. <br /><br /> <img src='../images/quests/RQ1-2.png' /> " +
				"<br /><br /> Step C: <br /><br /> Use the sendYourself function to send the message. This command is called a function. It's kind of like shorthand notation for more parts in the play. Instead of listing out a whole bunch of boring soliloquys that Eve must recite to send a message, which would be tedious, we've instead told Eve that we don't want any of that garbage and just to reference a hidden script she has already remembered from a past engagement. <br /><br />In this way, we make our programs shorter and easier to read. Eve is able to know what message to send using this hidden script by being 'passed' the boxes you set up earlier so that she knows what body and subject to send. We do this by placing both of the boxes into a giant one made by the parenthesis and separate the two boxes by commas so that they don't get all mushed together. <br /><br /> <img src='../images/quests/RQ1-3.png' /> <br /><br />Step D: <br /><br />As with all Programming Quests, now that you've finished your program, hit run to attempt quest completion. You may need to use the Menu > Refresh button to see the message that you sent. Please forgive me if I do not remind you of this factoid every quest. :)","No hint.",""/*"<object width=\"425\" height=\"344\"><param name=\"movie\" value=\"http://www.youtube.com/v/nSeyAQnms-Y?hl=en&fs=1\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/nSeyAQnms-Y?hl=en&fs=1\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"425\" height=\"344\"></embed></object>"*/};
				
		
		return toRet;
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
//		System.out.println("Iterating.");
		String story[] = {"newprogram();","sendYourself();"};
		if(logContains(story,0,p)) {
			reward(pid);
			destroy(p);
			p.getPs().b.joinQuest(God.getPlayerId("RQ2"));

		}
		

	}

	@Override
	public void playerConstructor(Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reward(int pid) {
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);

	}
	
	public String getQuestDescription(int pid) {
		Player p =God.getPlayer(pid);
		if(completedQuest(p,"BQBranchToRQ")) return "Test out comms with your Revelations A.I.";
		else return "Locked.";
}

}
