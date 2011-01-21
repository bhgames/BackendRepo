package BHEngine;

public class AQ8 extends QuestListener {

	
	public AQ8(int ID, GodGenerator God) {
		super(ID, God);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
			return true;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		// TODO Auto-generated method stub
		String additional[]  = { "500 Knowledge Points" };
		String toRet[] = {
				"<br />You successfully unlocked all of the Revelations APIs! Asimovian achievement unlocked!<br /><br />"+getRewardBlock(50,pid,null),"No Hint"};

		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		// TODO Auto-generated method stub
		return "This is the Asimovian achievement quest.";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		
	
		if(p.isAttackAPI()&&p.isAdvancedAttackAPI()&&p.isTradingAPI()&&
				p.isSmAPI()&&p.isResearchAPI()&&p.isBuildingAPI()&&
				p.isAdvancedBuildingAPI()&&p.isMessagingAPI()&&p.isZeppelinAPI()
				&&p.isCompleteAnalyticAPI()&&p.isNukeAPI()&&p.isWorldMapAPI()) {
			reward(pid);
			addAchievement("Asimovian",pid);
			p.flicker=getUsername();

			destroy(p);
			
		}
	}

	@Override
	public void playerConstructor(Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reward(int pid) {
		int i = 0;
		while(i<50){ 
			rewardOneHour(pid);
			i++;
		}
		Player p = findPlayer(pid);
	//	p.setKnowledge(p.getKnowledge()+500);
	}

}