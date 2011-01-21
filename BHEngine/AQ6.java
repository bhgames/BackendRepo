package BHEngine;

public class AQ6 extends QuestListener {

	
	public AQ6(int ID, GodGenerator God) {
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
		String additional[]  = { "10 Knowledge Points" };
		String toRet[] = {
				"<br />You successfully completed a scouting mission against another player! Scouts Honor achievement unlocked!<br /><br />"+getRewardBlock(5,pid,null),"No Hint"};

		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		// TODO Auto-generated method stub
		return "This is the Scouts Honor achievement quest.";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		int i = 0;
		Player p = findPlayer(pid);
		Town t; Raid r;
		
		boolean breakOut=false;
		if(p.towns()!=null)
		while(i<p.towns().size()) {
			t = p.towns().get(i);
			int j = 0;
			if(t.attackServer()!=null) 
				while(j<t.attackServer().size()) {
					r = t.attackServer().get(j);
					if(r.getScout()==1&&r.isRaidOver()&&r.getTown2().getPlayer().ID!=5&&!r.getTown2().getPlayer().isQuest()) {
						reward(pid);
						addAchievement("Scouts Honor",pid);
						p.flicker=getUsername();

						destroy(p);
						breakOut=true;
						break;
					}
					j++;
				}
			if(breakOut) break;
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
		while(i<5){ 
			rewardOneHour(pid);
			i++;
		}
		Player p = findPlayer(pid);
	//	p.setKnowledge(p.getKnowledge()+10);
	}

}