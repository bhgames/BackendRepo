package BHEngine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QuestDemo extends QuestListener {


	public QuestDemo(int ID, GodGenerator God) {
		super(ID, God);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public boolean checkPlayer(Player p) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		// TODO Auto-generated method stub
		String[] toRet = {"Prompt: The goal of this quest is to create "+
			 "an A.I. that listens every five minutes for an incoming attack " + 
			 " and then responds in kind with a counterattack.", 
			 
			 "Hint:<br />" +
			 "<object width=\"425\" height=\"344\"><param name=\"movie\" value=\"http://www.youtube.com/v/48RhFBvuIu0?hl=en&fs=1\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/48RhFBvuIu0?hl=en&fs=1\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"425\" height=\"344\"></embed></object> " +
			 		" <br />To create an AI capable of detecting attacks," +
			 		" you're going to need a few things. First of all, you're going to need an infinite loop, so use the for loop" +
			 		" we talked about earlier. Inside this loop you're going to need to use the wait command to cause the loop " +
			 		" to run and then stall for five minutes. Once you've done that, put in an if statement that checks for the size of the attack" +
			 		" server in your main town, and if it's greater than 0(meaning an incoming), then the code in the if statement should respond with an attack" +
			 		" aimed at the attacker. For help with the attack parameters, watch the video. So generally, you'll want something like:\n" + 
			 		"for(;;) {\n"+
			 		"bf.wait(350);\n" +
			 		"//if(attack server is greater than 0 in size) {\n" +
			 		"//attack the person\n"+
			 		"//}\n"
			 		+ " We did not complete the challenge for you, of course, you must fill out the commented pseudocode yourself with real code. For more help, you can also visit the Forum."};
			 
		
		return toRet;
	}

	@Override
	public void iterateQuest(int times, int pid) {
		// TODO Auto-generated method stub
		Player p = findPlayer(pid);
		ArrayList<Raid> as =  p.towns().get(0).attackServer();
		if(as.size()==1) {
			reward(pid);
			destroy(p);
		}
	}

	@Override
	public void playerConstructor(Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reward(int pid) {
		// TODO Auto-generated method stub
		rewardOneHour(pid);

	}
	public String getQuestDescription(int pid) {
		Player p =findPlayer(pid);
		 return "Build an attack detection and counterattack A.I. system!";
}


}


