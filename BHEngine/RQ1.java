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
	//	if(completedQuest(p,"BQBranchToRQ")) return true;
	//	else
	//	return false;
		return true;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String additional[]={};
		String toRet[] = {"<br />"+getRewardBlock(3,pid,additional)+"<br /><br />You found a Revelations Class A.I. named Eve when you scouted out that distress call. She's been badly damaged and needs reprogramming. Before we can do that, we need to do some tests to make sure that she is still working. Let's start simple and test out her communications capabilities.<br /><br /> Goal: Use the sendYourself Function of the Revelations AI to send a message to yourself.<br /><br />Tutorial:<br /><br />"+
				" Now, let me preface this by explaining that any program reads like the script for a play to Eve, and that writing these scripts can be difficult. If you have trouble, you can normally find somebody willing to help in the Chat(select Menu > Chat). Starting at the top, Eve acts out all the stage instructions and speeches, one after another in sequence, from top to bottom. The language she uses however is not English, but Java. In this play, we want Eve to store a message and subject header to send to us, and then send it. In accordance, we will first store the data we want and then use it to send a message.<br /><br /> Step A:<br /><br />To send a message, you first need to store the message body and subject in two variables. Variables are like the labels on boxes that can hold specific values in keeping for later use in the program. Now, not all boxes are created equal. Some can only hold whole numbers, others decimals, and some can hold words, and you have to tell the program what kind of box it is before you give that box a name and then put a value in it. For instance, int variables can hold whole numbers and Strings can hold words. <br /><br />Let's create the msg variable and assign it a value. This will be a String variable, as it needs to contain words, and we will tell Eve this before we label it as msg. After we tell Eve it's both a String box and it's name is msg, we'll dump our message into it for later use. <br /><br /> <img src='../images/quests/RQ1-1.png' style='height:117px'  />" +
				"<br /><br />Step B: <br /><br /> All messages need a subject, right? Well a subject can be stored in a different variable with a different label, but it will also be a String variable. <br /><br /> <img src='../images/quests/RQ1-2.png' style='height:132px'/> " +
				"<br /><br /> Step C: <br /><br /> Use the sendYourself function to send the message. This command is called a function. It's kind of like shorthand notation for more parts in the play. Instead of listing out a whole bunch of boring soliloquys that Eve must recite to send a message, which would be tedious, we've instead told Eve that we don't want any of that garbage and just to reference a hidden script she has already remembered from a past engagement. <br /><br />In this way, we make our programs shorter and easier to read. Eve is able to know what message to send using this hidden script by being 'passed' the boxes you set up earlier so that she knows what body and subject to send. We do this by placing both of the boxes into a giant one made by the parenthesis and separate the two boxes by commas so that they don't get all mushed together. <br /><br /> <img src='../images/quests/RQ1-3.png' style='height:134px' /> <br /><br />Step D: <br /><br />As with all Programming Quests, now that you've finished your program, hit run to attempt quest completion. You may need to use the Menu > Refresh button to see the message that you sent. Please forgive me if I do not remind you of this factoid every quest. :)","No hint.",""/*"<object width=\"425\" height=\"344\"><param name=\"movie\" value=\"http://www.youtube.com/v/nSeyAQnms-Y?hl=en&fs=1\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/nSeyAQnms-Y?hl=en&fs=1\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"425\" height=\"344\"></embed></object>"*/};
				
		
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
	//	if(completedQuest(p,"BQBranchToRQ")) return "Test out comms with your Revelations A.I.";
	//	else return "Locked.";
		
		return "Test out comms with the Revelations A.I.";
}

}
