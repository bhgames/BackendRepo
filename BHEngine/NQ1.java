package BHEngine;

import java.util.ArrayList;

public class NQ1 extends QuestListener {

	public NQ1(int ID, GodGenerator God) {
		super(ID, God);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		// TODO Auto-generated method stub
		if(p.getCapitaltid()==-1) return false;
		return true;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		// TODO Auto-generated method stub
				String additional[]  = { "5 Knowledge Points" };

		String toRet[] = {getRewardBlock(1,pid,additional)+"<br /><br />Goals:  <ul><li>Construct a building.<br /><ul><li>If you're here for a fight, build an Arms Factory.</li><li>If you're here to build the next Roman Empire, build a Construction Yard.</li></ul></li></ul><br /><br />"+
				"Welcome to AI Wars.  I am helper AI designation 63-N-3515 'Genesis'.  In order to assist your acclimation to your command role, the following curriculum has been devised.  Note that your choices affect the goals of later quests.  Let's begin with construction."+ 

"<br /><br />Click on an empty lot to open the building list.  Take a moment to browse the list. Clicking the “Build” button will open up an info prompt for that building with more information and allow you to build that building.  For now, however, choose between the Construction Yard and Arms Factory."
,"No Hint"};
		
		
		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		return "Construct a Building";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		PlayerScript ps = p.getPs();
	
		if(ps.b.haveBldg("Arms Factory",p.getCapitaltid())||ps.b.haveBldg("Construction Yard",p.getCapitaltid())) {
			if(ps.b.haveBldg("Arms Factory",p.getCapitaltid()) )p.setVersion("military");
			else if(ps.b.haveBldg("Construction Yard",p.getCapitaltid()) )p.setVersion("civilian");

			reward(pid);
			destroy(p);
			ps.b.joinQuest(God.getPlayerId("NQ2"));
		}
		

	}

	@Override
	public void playerConstructor(Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reward(int pid) {
		rewardOneHour(pid);
		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+5);
	}

}
