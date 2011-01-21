package BHEngine;

public class AQ10 extends QuestListener {

	
	public AQ10(int ID, GodGenerator God) {
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
		String additional[]  = { "50 Knowledge Points" };
		String toRet[] = {
				"<br />You successfully gained 1000BP! Pwnblazer achievement unlocked!<br /><br />"+getRewardBlock(50,pid,null),"No Hint"};

		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		// TODO Auto-generated method stub
		return "This is the Pwnblazer achievement quest.";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		
		if(p.getTotalBPEarned()>=1000) {
			
					reward(pid);
					addAchievement("Pwnblazer",pid);
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
	//	p.setKnowledge(p.getKnowledge()+50);
	}

}