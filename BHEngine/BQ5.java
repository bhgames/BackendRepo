package BHEngine;

//import java.util.ArrayList;
//import BattlehardFunctions.BattlehardFunctions;

public class BQ5 extends QuestListener {

	public BQ5(int ID, GodGenerator God) {
		super(ID, God);
	}

	@Override
	public boolean checkPlayer(Player p) {
		if(completedQuest(p,"BQ4")) return true;
		else
		return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String 	additional[] = {},
				goals = "<div class='goalBox'>Goals:<ul><li>Research a Technology</li></ul></div>",
				text = "<div class='descBox'>Congratulations! You've liberated some resources. Attacking, or 'Farming', Id towns is a good way to earn resources while you're starting out.<br/><br/>"
						+ "Now that you've learned how to construct buildings, train units, and send missions, it's time to learn how to research new technology. Researching tech benefits your entire empire and each tech has its own unique effect. Some techs are leveled, and increase in effectiveness with each level, or one time purchases, such as blueprints. You can view all the technologies available to you from your Institute.<br/><br/>"
						+ "Once inside your Institute, you'll see all the techs separated by category. Take some time to browse each category and look at the various technology available. If you're not sure what a tech does, simply click its name and a help message will appear giving you more information on what that tech does as well as its maximum level.<br/><br/>"
						+ "Once you've found a tech you like, and can afford, research it.</div>",
				reward = "<div class='rewardBox'>"+getRewardBlock(1,pid,additional)+"</div>",
				script = "";
		return new String[] {goals+text+reward,script};
	}

	@Override
	public void iterateQuest(int times, int pid) {

	}
	
	public void callMeIfResearched(int pid) {
		Player p = God.getPlayer(pid);
		reward(pid);
		destroy(p);
		p.getPs().b.joinQuest(God.getPlayerId("BQ6"));
	}

	@Override
	public void playerConstructor(Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reward(int pid) {
		rewardOneHour(pid);
	}

	public String getQuestDescription(int pid) {
		return "Knowledge is Power";

}
}
