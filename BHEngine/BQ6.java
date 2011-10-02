package BHEngine;

//import java.util.ArrayList;

import BattlehardFunctions.BattlehardFunctions;
//import BattlehardFunctions.UserBuilding;

public class BQ6 extends QuestListener {

	public BQ6(int ID, GodGenerator God) {
		super(ID, God);
	}

	@Override
	public boolean checkPlayer(Player p) {
		if(completedQuest(p,"BQ5")) return true;
		else
		return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String 	additional[] = {"50 Knowledge Points"},
				goals = "<div class='goalBox'>Goals:<ul><li>Construct a Trade Center</li><li>Construct a Metal Warehouse</li><li>Construct a Crystal Repository</li><li>Construct a Lumber Yard</li><li>Construct a Granary</li></ul></div>",
				text = "<div class='descBox'>Very good! As I said, technologies you research affect your entire empire.<br/><br/>"
						+ "For your last task, you need to round out your town with all the things it will need. Namely, you'll need to build one of each of the resource storage buildings (Metal Warehouse, Crystal Repository, Lumber Yard, and Granary) and a Trade Center. You may not meet the requirements to build the Trade Center. If you don't, check the Construction Interface to find out what you need to do to build it.<br/><br/>"
						+ "Once you finish this quest, you're free to continue as you see fit. If you need any additional assistance, you can ask on the Chatbox, via our feedback form, or via our forum. All of these can be accessed in game via the tabs on the left.</div>",
				reward = "<div class='rewardBox'>"+getRewardBlock(4,pid,additional)+"</div>",
				script = "";
		
		return new String[] {goals+text+reward,script};
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		int capitaltid = p.getCapitaltid();
		BattlehardFunctions bf = p.getPs().b;
		if(bf.haveBldg("Trade Center", capitaltid)&&bf.haveBldg("Metal Warehouse", capitaltid)&&bf.haveBldg("Crystal Repository", capitaltid)&&bf.haveBldg("Lumber Yard", capitaltid)&&bf.haveBldg("Granary", capitaltid)) {
			reward(pid);
			destroy(p);
		}
	}

	@Override
	public void playerConstructor(Player p) {

	}

	@Override
	public void reward(int pid) {
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);

		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+50);
	}
	
	@Override
	public String getQuestDescription(int pid) {
		return "Empire Building";

}

}
