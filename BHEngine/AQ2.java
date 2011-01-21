package BHEngine;

public class AQ2 extends QuestListener {

	
	public AQ2(int ID, GodGenerator God) {
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
		String additional[]  = { "25 Knowledge Points" };
		String toRet[] = {
				"<br />You successfully launched a nuke! Destroyer Of Worlds achievement unlocked!<br /><br />"+getRewardBlock(10,pid,null),"No Hint"};

		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		// TODO Auto-generated method stub
		return "This is the Destroyer Of Worlds achievement quest.";
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
					if(b.getType().equals("Missile Silo")&&b.getLvl()>=1&&b.getTicksLeft()>0) {
						reward(pid);
						addAchievement("Destroyer Of Worlds",pid);
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