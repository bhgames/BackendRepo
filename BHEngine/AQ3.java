package BHEngine;

public class AQ3 extends QuestListener {

	
	public AQ3(int ID, GodGenerator God) {
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
				"<br />You successfully built an Airship! High Flying achievement unlocked!<br /><br />"+getRewardBlock(20,pid,null),"No Hint"};

		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		// TODO Auto-generated method stub
		return "This is the High Flying achievement quest.";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		int i = 0;
		Player p = findPlayer(pid);
		Town t; Building b;
		boolean breakOut=false;
		if(p.towns()!=null)
		while(i<p.towns().size()) {
			t = p.towns().get(i);
			if(t.isZeppelin()) {
					reward(pid);
					addAchievement("High Flying",pid);
					p.flicker=getUsername();
					destroy(p);
					break;
			}
			i++;
		}

	}

	@Override
	public void playerConstructor(Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reward(int pid) {
		int i = 0;
		while(i<20){ 
			rewardOneHour(pid);
			i++;
		}
		Player p = findPlayer(pid);
	//	p.setKnowledge(p.getKnowledge()+50);
	}

}