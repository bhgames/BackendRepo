package BHEngine;

import java.util.ArrayList;

public class NQ2 extends QuestListener {

	public NQ2(int ID, GodGenerator God) {
		super(ID, God);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		// TODO Auto-generated method stub
		if(completedQuest(p,"NQ1")) {
			return true;
		}
		else
		return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		// TODO Auto-generated method stub
		String additional[]  = { "10 Knowledge Points" };
		Player p = God.getPlayer(pid);
		String goal = "";
		String paragraph ="";
		if(p.getVersion().equals("military")) {
			goal = "Put a Shock Trooper into your Arms Factory Build Queue";
			paragraph = "Enter your Arms Factory's interface by clicking on it.  Inside, you'll find all the necessary tools to train your soldiers and construct your war machines.  First, we'll have to assign your soldier to a slot.  If it's not already selected, select the first empty slot now..  Then, after selecting “Shock Trooper” from the dropdown to the right, click “Assign.”"+
			"<br /><br />Now, it's time to build your first soldier. In the lower part of the screen, you'll see the training interface.  Type 1 into the input box on the left.  After a moment, the values will update showing the cost, build time, and slots needed for the number of units you're trying to build.  If everything is in order, go ahead and queue yourself a shock trooper. While you wait, build an Institute and a Headquarters!";
		}
		else if(p.getVersion().equals("civilian")){
			goal = "Put an Engineer into your Construction Yard Build Queue";
			paragraph = "Enter your Construction Yard's interface by clicking on it.  The Construction Yard shows the ID of your current town as well as the lot numbers of all your buildings. In the lower part of the screen, you'll see the training interface.  Type 1 into the input box on the left. <br /><br /> After a moment, the values will update showing the cost and training time of the number of units your trying to train.  If everything is in order, go ahead and queue yourself an engineer. Then, go back out to Town View and construct an Institute and a Headquarters.";
		}

		String toRet[] = {getRewardBlock(1,pid,additional)+"<br /><br />Goals:  <ul><li>"+goal+"</li><li>Build an Institute and a Headquarters.</li></ul><br /><br />"+
				"Very good!  You picked that up very quickly.<br /><br />"+paragraph+"<br /><br />The Headquarters can only be built on the topmost lot.<br /><br />"
				
			
,"No Hint"};
		return toRet;
			}

	@Override
	public String getQuestDescription(int pid) {
		// TODO Auto-generated method stub
		String toRet = "Begin Your Quest";
		Player p = God.getPlayer(pid);
		if(p.getVersion().equals("military")) toRet = "Prepare Your Conquest";
		else if(p.getVersion().equals("civilian")) toRet = "Raise An Empire";
		return toRet;
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		ArrayList<AttackUnit> au = p.getAu();
		PlayerScript ps = p.getPs();
	
		if(ps.b.haveBldg("Headquarters",p.getCapitaltid())&&ps.b.haveBldg("Institute",p.getCapitaltid())) {
			int k = 0;
			Town t=God.getTown(p.getCapitaltid());
			ArrayList<Building> bldg = t.bldg();
			Building b;
			while(k<bldg.size()) {
				b = bldg.get(k);
				if(b.getType().equals("Arms Factory")&&p.getVersion().equals("military")) {
					
					if(b.Queue().size()>1||t.getAu().get(0).getSize()>=1) {
						reward(pid);
						destroy(p);
						ps.b.joinQuest(God.getPlayerId("NQ3"));
						break;
					}
				} else if(b.getType().equals("Construction Yard")&&p.getVersion().equals("civilian")) {
					
					if(b.getNumLeftToBuild()>=1||b.getPeopleInside()>=1) {
						reward(pid);
						destroy(p);
						ps.b.joinQuest(God.getPlayerId("NQ3"));
						break;
					}
				}

				k++;
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
		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+10);
	}

}
