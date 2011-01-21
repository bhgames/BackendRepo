package BHEngine;

import java.util.ArrayList;

public class RQ4 extends QuestListener {

	public RQ4(int ID, GodGenerator God) {
		super(ID, God);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		if(completedQuest(p,"RQ3")) return true;
		else return false;

	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String additional[] = {"30 Knowledge Points"};
		String toRet[] = {/*"Prompt:<br /><br />"+
				"‘Well that wasn’t so bad,’ you say. ‘I can see how this’d work, but I just can’t see how you could get a super-sentient AI out of this.’<br /><br />"+

"‘Every journey starts with the first step. As you build your Revelations, you’re retracing the steps that other engineers made on their own. As it gets more complex, it will start making as good or better decisions than you. It’s at the point when it begins to communicate with other Revelations and rewrite itself that you get Id,’ Genesis explains. ‘It’s just simple evolution.’<br /><br />"+

"‘So what I’m doing now…’<br /><br />"+

"‘Is on the path to rebuilding Id’s network, yes.’<br /><br />"+

"‘But the bastard attacked me.’<br /><br />"+

"‘Incorrect, Id is dead. His short-circuiting armies are going haywire and attacking you. So if you can bring Id back, then maybe they’ll finally be brought back under control…our control.’<br /><br />"+

"‘I like the way you think,’ you say. ‘Now for the auto-farmer?’.<br /><br />"+

"‘Yes, I think we could manage that now,’ Genesis says.<br /><br />"
,*/
				"<br />"+getRewardBlock(9,pid,additional)+ "<br /><br />Great, Eve is now a glorified Launch Attack button, but that's not as useful as you might at first have thought. You need Eve to be smarter than that - you need her to execute that attack on that town for it's resources periodically, so that you can" +
				" be picking up wealth even while you sleep! Let's turn Eve into what is called an 'Autofarmer.' She'll become a program that runs a loop that attacks an Id town every hour. You could, in time, expand this Autofarmer to hit other players, too. You could even send them a fun little message notifying them of" +
				" their doom before every attack. The sky's the limit in A.I. Wars!(Literally, you can fly Airships if you want...check out Advanced Technologies in the Institute.) <br /><br /> Goal: Have Eve attack one town on the dot every hour for resources! Do this at least three times with Revelations at the appointed time differences to pass this quest.<br /><br />Tutorial:<br /><br />" +
				" Before we begin, you're going to need to learn a little about for loops. For loops are a fun construct in Java that tells Eve to read the same piece of script over and over again. This is meant to save you from" +
				" copy paste hell, because if you wanted to do anything twice, or thrice, like say making an attack, you'd have to write the attack statement three times. To create a for loop, you need to provide three programming statements in it's (), unlike other ()'s we've seen so far, which just take variables. An example would be for(int i = 0; i<10; i=i+1) {}." +
				" <br /><br /> Now at this point, you're probably pretty confused. Why not variables in the parentheses? Well, in this case, the () is sort of like a shelf that holds three different scripts for Eve to read at different times. She reads the first one before the loop starts. This one line is used to set up" +
				" something important for the loop - in this case we set up i, a variable that we can use to count how many times the loop has executed. The second " +
				" piece of script in the () shelf is read by Eve every time she re-reads the inner script of the loop. It's a boolean condition - a question she asks herself. She asks, 'is i less than 10 still?' If it isn't, then she knows to not read the script again and move on to new parts of the program, and if it is," +
				" she goes ahead and reads the script once more. Finally, the third little script is the incrementation script. She reads that right after she checks the second part every time she reads the script. We use it to make i increase itself by one, so" +
				" that we can effectively make sure that the inner script is only read 10 times. <br /><br /> In this challenge, you will create a for loop and have an attack command inside of it. After the attack command, you'll put a new command that lets you pause the program for an hour, called bf.wait(3600);. " +
				" It takes a number in it's () that allows it to wait for that amount of time in seconds. This script will be read three times, allowing you to attack the same town three times over a period of three hours, completely hands off, and without rewriting code! Once you hit run on Eve, she'll do all the work for you!" +
				"<br /><br /> Step A:<br /><br /> Create a for loop that will run the script inside itself three times." +
				"<br /><br /> <img src='../images/quests/RQ4-1.png' />" +
				"<br /><br /> Step B: <br /><br /> Set up the rest of the variables you need and make the function call to attack. You need to start using your townID instead of your town name," +
				" as your townID is a unique identifier for your town, and every town has one. Also, most of the functions you use in this game require your townID, not it's name. Your main town's townID is " + God.getPlayer(pid).towns().get(0).townID + ". Then use the " +
						" wait function, which takes seconds in it's (), to cause Eve to freeze for an hour every time the script is read to create the desired one-hour-hit effect. <br /><br /> <img src='../images/quests/RQ4-2.png' /> ","No hint.",
				""/*"<object width=\"425\" height=\"344\"><param name=\"movie\" value=\"http://www.youtube.com/v/SykkKPHjhNs?hl=en&fs=1\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/SykkKPHjhNs?hl=en&fs=1\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"425\" height=\"344\"></embed></object>"*/};
		return toRet;
	}

	@Override
	public void iterateQuest(int times, int pid) {
	Player p = findPlayer(pid);
		
		String story[] = {"newprogram();","attack();","attack();","wait(3600);" // two at a time because we actually call the diff methods of attack.
				,"attack();","attack();","wait(3600);","attack();","attack();"};
		if(logContains(story,1,p)) {
			reward(pid);
			destroy(p);
			p.getPs().b.joinQuest(God.getPlayerId("RQ5"));

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
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);
		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+30);
	}
	public String getQuestDescription(int pid) {
		Player p =God.getPlayer(pid);
		if(completedQuest(p,"RQ3")) return "Build a one-city autofarmer with your Revelations A.I.!";
		else return "Locked.";
}

}
