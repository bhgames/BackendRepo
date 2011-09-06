package BHEngine;

//import java.util.ArrayList;

public class BQ2 extends QuestListener {

	public BQ2(int ID, GodGenerator God) {
		super(ID, God);
	}

	@Override
	public boolean checkPlayer(Player p) {
		//System.out.println("Did he complete it?" + completedQuest(p,"BQ1"));
		if(completedQuest(p,"BQ1")) {
			return true;
		}
		else
			return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String additional[]  = { "10 Knowledge Points" };

		String toRet[] = {"<div class='goalBox'>Goals:<ul><li>Build an Institute</li><li>Build an Arms Factory</li></ul></div><div class='descBox'>Excellent! Engineers are also used to recover resources from Resource Outcroppings. So, be sure to keep a lot on hand.<br/><br/>" +
				"Next, we're going to get this town up and running. To do that, we need to build an Institute and an Arms Factory. Institutes house Scholars and allow you to unlock new research for your empire. Scholars generate Research Points (RP) which can be spent on various researches. Arms Factories allow you to produce Soldiers, the staple of any army.<br/><br/>" +
				"Clicking on any open build lot will open the Construction Interface. From here, you can view all the buildings you can currently construct along with the requirements for those you can't. Using the Construction Interface, build an Institute and an Arms Factory now. You can queue both units up at the same time, so you don't have to wait for one to finish before building the second.</div><div class='rewardBox'>" + getRewardBlock(1,pid,additional)+"</div>" ,"No hint."};

		return toRet;
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		PlayerScript ps = p.getPs();
		
		if(ps.b.haveBldg("Arms Factory", p.getCapitaltid())&&ps.b.haveBldg("Institute", p.getCapitaltid())) {
			reward(pid);
			destroy(p);
			p.getPs().b.joinQuest(God.getPlayerId("BQ3"));
		}
	}

	@Override
	public void playerConstructor(Player p) {
		
	}

	@Override
	public void reward(int pid) {
		rewardOneHour(pid);
		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+10);
	}
	public String getQuestDescription(int pid) {
			//Player p =God.getPlayer(pid);
			return "Building Up";
	}
	

}
