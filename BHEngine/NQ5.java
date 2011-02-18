package BHEngine;

public class NQ5 extends QuestListener {

	public NQ5(int ID, GodGenerator God) {
		super(ID, God);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		// TODO Auto-generated method stub
		if(completedQuest(p,"NQ4")) {
			return true;
		}
		else
		return false;
		}

	@Override
	public String[] getCurrentQuestText(int pid) {
		// TODO Auto-generated method stub
		String additional[]  = { "25 Knowledge Points" };
		Player p = God.getPlayer(pid);
	
	

		String toRet[] = {getRewardBlock(7,pid,additional)+"<br /><br />Goals:  <ul><li>Purchase a Research.</li></ul><br /><br />"+
				"Great! <br /><br /> You'll be using those interfaces often while playing.  So, it's best that you take some time to familiarize yourself with them.<br /><br />"+
				"Now it's time to learn about Research, so head over to the Institute you built earlier.  You should have a few points to spend.  Take a moment and look over the various techs. You'll want to start planning your development early.  So, if you have any questions, now's the time to ask.  Good places to ask would be the chat (Menu > Chat), our <a href='battlehardalpha.xtreemhost.com' target='_forum'>forum</a>, or via the feedback or support menus.<br /><br />" +
				"Once you've found a research you like, and have the points for, click “Purchase”.<br /><br />"
				
			
,"No Hint"};
		return toRet;
		}

	@Override
	public String getQuestDescription(int pid) {
		return "Research Something";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerConstructor(Player p) {
		// TODO Auto-generated method stub

	}
	public void callMeIfResearched(int pid) {
		reward(pid);
		Player p = God.getPlayer(pid);
		destroy(p);
		p.getPs().b.joinQuest(God.getPlayerId("NQ6"));
		
	}
	@Override
	public void reward(int pid) {
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);

		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+25);
	}

}
