package BHEngine;

public class NQ3 extends QuestListener {

	public NQ3(int ID, GodGenerator God) {
		super(ID, God);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		// TODO Auto-generated method stub
		if(completedQuest(p,"NQ2")) {
			return true;
		}
		else
		return false;
		}

	@Override
	public String[] getCurrentQuestText(int pid) {

		// TODO Auto-generated method stub
		String additional[]  = { "15 Knowledge Points" };
		Player p = God.getPlayer(pid);
		String goal = "";
		String paragraph ="";
		if(p.getVersion().equals("military")) {
			goal = "<li>Train a total of 5 Shock Troopers</li><li>Train 5 Scholars</li>";
			paragraph = "Head back into your Arms Factory.  One soldier isn't going to be very good in a fight.  So, go ahead and queue up four more.  Don't forget to build soldiers often. Generally speaking, more is better.";
		}
		else if(p.getVersion().equals("civilian")){
			goal = "<li>Train a total of 5 Engineers</li><li>Train 5 Scholars</li>";
			paragraph = "The Engineers trained by your Construction Yard reduce the build times of pretty much everything.  Go ahead and build 4 more now..";
		}

		String toRet[] = {getRewardBlock(3,pid,additional)+"<br /><br />Goals:  <ul>"+goal+"</ul><br /><br />"+
				"Congratulations!  You now know the basics of city building.  All buildings, except the Headquarters can be built multiple times.<br /><br />"+paragraph+"<br /><br />After, head into your new Institute.  Research (covered later) is handled by this building.  Go ahead and build 5 Scholars.  If you haven't noticed, the unit cap for level 1 buildings is 5."
				
			
,"No Hint"};
		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		Player p = God.getPlayer(pid);
		if(p.getVersion().equals("civilian"))
			return "Grow Your Citizenry";
		else if(p.getVersion().equals("military")) {
			return "Build Your Army";
		
		} else return "Populate Your City";

	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = God.getPlayer(pid);
		Town t = God.getTown(p.getCapitaltid());
		if(t!=null) {
			if(p.getVersion().equals("military")) {
				if(t.getAu().get(0).getSize()>=5&&t.getTotalScholars()>=5) {
					reward(pid);
					destroy(p);
					ps.b.joinQuest(God.getPlayerId("NQ4"));
					
				}
			}
			else if(p.getVersion().equals("civilian")) {
				if(t.getTotalScholars()>=5&&t.getTotalEngineers()>=5) {
					reward(pid);
					destroy(p);
					ps.b.joinQuest(God.getPlayerId("NQ4"));
				}
			}
		}

	}

	@Override
	public void playerConstructor(Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reward(int pid) {
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);

		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+15);
	}

}
