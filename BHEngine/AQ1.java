package BHEngine;

public class AQ1 extends QuestListener {

	
	public AQ1(int ID, GodGenerator God) {
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
		String additional[]  = { "" };
		String toRet[] = {
				"<br />You successfully built a Missile Silo! I Am Become Death achievement unlocked!<br /><br />"+getRewardBlock(10,pid,null),"No Hint"};

		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		// TODO Auto-generated method stub
		return "AQ1";
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
			int j = 0;
			if(t.bldg()!=null)
				while(j<t.bldg().size()) {
					b = t.bldg().get(j);
					if(b.getType().equals("Missile Silo")&&b.getLvl()>=1) {
						reward(pid);
						addAchievement("I Am Become Death",pid);
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
	//p.setKnowledge(p.getKnowledge()+25);
	}

}
