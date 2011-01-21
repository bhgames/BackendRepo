package BHEngine;

import java.util.ArrayList;

import BattlehardFunctions.BattlehardFunctions;

public class BQ5 extends QuestListener {

	public BQ5(int ID, GodGenerator God) {
		super(ID, God);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		if(completedQuest(p,"BQ4")) return true;
		else
		return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String additional[] = {};
		String toRet[] = {"It's time to expand your capabilities and your civilization! Right now, you can only hold 2000 of each resource. Resource warehouses allow you to expand this limit.<br /><br />"+
				"Goal: Build the four different warehouse types.<br /><br />"+getRewardBlock(3,pid,additional)
				/*"Prompt:<br /><br />"+

"‘Well done, you’re now a scavenger.’<br /><br />"+

"‘Thanks,’ you mutter under your breath.<br /><br />"+

"‘Don’t be a namby pamby.’<br /><br />"+

"‘A what?’<br /><br />"+

"‘Shut up,’ Genesis says. ‘In the mean time, you need to expand your warehouse capacity. Right now you’re hitting your resource cap consistently and losing excess. This is about as smart as the Iddite you just killed. We need that excess to rebuild, before they strike again.’<br /><br />"+

"‘Will do,’ you grumble.<br /><br />"+

"‘Oh and, remember, keep raiding the dead cities.’<br /><br />"*/,"No hint."
		};
		return toRet;
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		int capitaltid = p.getCapitaltid();
		if(capitaltid!=-1) {
			BattlehardFunctions b = p.getPs().b;
			if(b.haveBldg("Metal Warehouse",capitaltid)&&b.haveBldg("Timber Warehouse",capitaltid)
					&&b.haveBldg("Manufactured Materials Warehouse",capitaltid)
					&&b.haveBldg("Food Warehouse",capitaltid)) {
				reward(pid);
				destroy(p);
				p.getPs().b.joinQuest(God.getPlayerId("BQ6"));

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
	}

	public String getQuestDescription(int pid) {
		Player p =God.getPlayer(pid);
		if(completedQuest(p,"BQ4")) return "Build warehouses to store more resources.";
		else return "Locked.";
}
}
