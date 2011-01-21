package BHEngine;

import java.util.ArrayList;

public class RQ5 extends QuestListener {

	public RQ5(int ID, GodGenerator God) {
		super(ID, God);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		// TODO Auto-generated method stub
		if(completedQuest(p,"RQ4")) return true;
		else return false;
		}

	@Override
	public String[] getCurrentQuestText(int pid) {
		// TODO Auto-generated method stub
		String additional[]={"50 Knowledge Points"};
		String toRet[] = {/*"Prompt: <br /><br />"+
				"‘Holy crap. I’ve got my men doing things without having to tell them to do it any more.’<br /><br />"+

				"‘Who would’ve thought that programming would go such a long way in this kind of world,’ Genesis remarks casually. ‘We need to get back to world domination here. We have enough time to do one last thing before we take it to the Iddites. What do you want to do?’<br /><br />"+

				"‘Well, I’d really like it if I could autofarm more than one town.’<br /><br />"+

				"‘Let’s do it then.’<br /><br />"

				,*/
								"<br />" + getRewardBlock(9,pid,additional) +"<br /><br />This is the last of the programming quests and the last time you'll be able to use the attacking API for free. You will gain enough knowledge points from this final quest to " +
										"purchase the attack API from the Institute's AI Research tab if you wish. If you do not, your Autofarmer will cease to function after quest completion! In this challenge, we're going to make a rather simple upgrade to the Autofarmer you already built by expanding" +
										" it's single target to three different targets that it hits every hour. This triples your hourly raid income!<br /><br />Goal: Attack three towns(they can be the same in each attack wave) at least three times with Eve at the appointed time difference of one hour between waves to pass this quest.<br /><br />Tutorial:" +
								" <br /><br /> Step A:<br /><br /> Create a for loop as you did previously, so it will run the inner script three times." +
								"<br /><br /> <img src='../images/quests/RQ4-1.png' />" +
								"<br /><br /> Step B: <br /><br /> Set up the rest of the variables you need and make the function call to attack. You need to start using your townID instead of your town name," +
								" as your townID is a unique identifier for your town, and every town has one. Also, most of the functions you use in this game require your townID, not it's name. Your main town's townID is " + God.getPlayer(pid).towns().get(0).townID + ". Then use the " +
										" wait function, which takes seconds as it's argument, to cause Eve to freeze for an hour every iteration to create the desired one-hour-hit effect. <br /><br /> <img src='../images/quests/RQ4-2.png' /> "
								+"<br /><br /> Step C: <br /><br /> Replicate your attacking code two more times with different Id town x's and y's so you send out three attacks every loop iteration. <br /><br /> <img src='../images/quests/RQ5-1.png' /> "
								
								,"No hint.",""/*"<object width=\"425\" height=\"344\"><param name=\"movie\" value=\"http://www.youtube.com/v/Ah0CDdy6lMM?hl=en&fs=1\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/Ah0CDdy6lMM?hl=en&fs=1\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"425\" height=\"344\"></embed></object>"*/};
						return toRet;	}

	@Override
	public void iterateQuest(int times, int pid) {
	Player p = findPlayer(pid);
		
		String story[] = {"newprogram();","attack();","attack();","attack();","attack();","attack();","attack();","wait(3600);" // double all attack calls!
				,"attack();","attack();","attack();","attack();","attack();","attack();","wait(3600);","attack();","attack();","attack();","attack();","attack();","attack();"};
		if(logContains(story,1,p)) {
			reward(pid);
			destroy(p);
			p.getPs().b.joinQuest(God.getPlayerId("BQ8"));

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
		p.setKnowledge(p.getKnowledge()+50);
	}
	
	public String getQuestDescription(int pid) {
		Player p =God.getPlayer(pid);
		if(completedQuest(p,"RQ4")) return "Build a three-city autofarmer with your Revelations A.I.!";
		else return "Locked.";
}

}
