package BHEngine;

public class AQ4 extends QuestListener {

	
	public AQ4(int ID, GodGenerator God) {
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
				"<br />You successfully completed a Siege campaign that lasted more than two rounds! Call Me Conqueror achievement unlocked!<br /><br />"+getRewardBlock(10,pid,null),"No Hint"};

		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		// TODO Auto-generated method stub
		return "This is the Call Me Conqueror achievement quest.";
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
					if(r.isGenocide()&&r.isRaidOver()&&r.isAllClear()&&r.getGenoRounds()>=2) {
						reward(pid);
						addAchievement("Call Me Conqueror",pid);
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
		while(i<10){ 
			rewardOneHour(pid);
			i++;
		}
		Player p = findPlayer(pid);
	//	p.setKnowledge(p.getKnowledge()+10);
	}

}