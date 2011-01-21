package BHEngine;

public class AQ9 extends QuestListener {

	
	public AQ9(int ID, GodGenerator God) {
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
		String additional[]  = { "150 Knowledge Points" };
		String toRet[] = {
				"<br />You successfully reached an average building level of 10 in your Capital City! Noble Savage achievement unlocked!<br /><br />"+getRewardBlock(50,pid,null),"No Hint"};

		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		// TODO Auto-generated method stub
		return "This is the Noble Savage achievement quest.";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		
		if(p.getCapitaltid()!=-1) {
					Town t = God.getTown(p.getCapitaltid());
					double avg = God.getAverageLevel(t);
				if(avg>=10) {
					reward(pid);
					addAchievement("Noble Savage",pid);
					p.flicker=getUsername();

					destroy(p);
					
				}
		
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
		//p.setKnowledge(p.getKnowledge()+150);
	}

}