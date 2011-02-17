package BHEngine;

public class AQ7 extends QuestListener {

	
	public AQ7(int ID, GodGenerator God) {
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
		String additional[]  = { "100 Knowledge Points" };
		String toRet[] = {
				"<br />You successfully expanded your Empire to four towns! Veni, Vidi, Vici achievement unlocked!<br /><br />"+getRewardBlock(40,pid,null),"No Hint"};

		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		// TODO Auto-generated method stub
		return "AQ7";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		if(p.towns()!=null&&p.towns().size()>=4) {
			reward(pid);
			addAchievement("Veni, Vidi, Vici",pid);
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
		while(i<40){ 
			rewardOneHour(pid);
			i++;
		}
		Player p = findPlayer(pid);
		//p.setKnowledge(p.getKnowledge()+100);
	}

}