package BHEngine;

public class AQ5 extends QuestListener {

	
	public AQ5(int ID, GodGenerator God) {
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
		String additional[]  = { "20 Knowledge Points" };
		String toRet[] = {
				"<br />You successfully completed all of the Beginner Quests! Grizzled Veteran achievement unlocked!<br /><br />"+getRewardBlock(15,pid,null),"No Hint"};

		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		// TODO Auto-generated method stub
		return "This is the Grizzled Veteran achievement quest.";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		if(p.completedQuest("BQ8")) {
					reward(pid);
					addAchievement("Grizzled Veteran",pid);
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
		while(i<15){ 
			rewardOneHour(pid);
			i++;
		}
		Player p = findPlayer(pid);
	//	p.setKnowledge(p.getKnowledge()+20);
	}

}