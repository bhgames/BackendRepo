package BHEngine;

import java.util.ArrayList;

public class BQ4 extends QuestListener {

	public BQ4(int ID, GodGenerator God) {
		super(ID, God);
	}

	@Override
	public boolean checkPlayer(Player p) {
		if(completedQuest(p,"BQ3")) return true;
		else
			return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String 	additional[]  = { "20 Knowledge Points" },
				goals = "<div class='goalBox'>Goals:<ul><li>Attack a nearby town</li></ul></div>",
				text = "<div class='descBox'>Excellent! To speed things up a bit, your remaining 4 queued soldiers have been finished automatically.<br/><br/>"
						+ "Let's put these new soldiers to work! Navigate to the World Map. From here, you can see your towns, nearby towns, any Zeppelins in the area, as well as territorial borders and Resource Outcroppings. For now, we're looking for nearby towns to pilla- I mean, relieve of excess resources. Select a nearby town owned by Id and select 'Send Mission' from the popup.<br/><br/>"
						+ "This will take you to your Command Center. From here, you can enter the number of units you want to send and select the type of mission to send them on. For your current task, you need to send all 5 of your new Pillagers to 'Attack' the town you selected. Id towns aren't owned by a player and usually have no defense whatsoever.<br/><br/>"
						+ "Send the attack now. You'll receive your next task when the attack lands.</div>",
				reward = "<div class='rewardBox'>" + getRewardBlock(0,pid,additional)+"</div>",
				script = "";

		return new String[] {goals+text+reward,script};
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		ArrayList<Raid> as = p.towns().get(0).attackServer();
		int i = 0; //boolean found=false; // of course we may not necessarily get the fastest raid...but oh well, who cares, right?
		Town t;
		while(i<as.size()) {
			t = as.get(i).getTown2();
			if(t!=null&&as.get(i).isRaidOver()) {
				reward(pid);
				destroy(p);
				p.getPs().b.joinQuest(God.getPlayerId("BQ5"));
			}
			i++;
		}

	}

	@Override
	public void playerConstructor(Player p) {

	}

	@Override
	public void reward(int pid) {
		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+20);
	}
	
	public String getQuestDescription(int pid) {
		//Player p =God.getPlayer(pid);
		return "Pillaging";
	}
}
