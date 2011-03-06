package BHEngine;

public class CQ1 extends QuestListener {

	public CQ1(int ID, GodGenerator God) {
		super(ID, God);
		// TODO Auto-generated constructor stub
	}
	public void onServerLoad() {
		// load up listeners here for digFinish and onRaidLanding, and onProgramLoad
		
	}
	
	public void onProgramLoad(Player p) {
		// Send the message!!
		
		
	}
	public void onDigFinish(Town t) {
		// find player attached to dig town there and increase reward if it is him.
		
	}
	public void onRaidLanding(Raid r) {
		
	}
	@Override
	public boolean checkPlayer(Player p) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuestDescription(int pid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void iterateQuest(int times, int pid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerConstructor(Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reward(int pid) {
		// TODO Auto-generated method stub

	}

}
