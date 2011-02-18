package BHEngine;

import java.util.ArrayList;

import BattlehardFunctions.UserRaid;

public class BQBranchToRQ extends QuestListener {

	public BQBranchToRQ(int ID, GodGenerator God) {
		super(ID, God);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		if(completedOrPartOfQuest(p,"BQ3")&&p.getCapitaltid()!=-1) return true;
		else return false;	
		
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String 	username = God.getPlayer(pid).getUsername();
		String additional[] = {};
		String toRet[] = {
			/*	"Prompt: <br />" +
				"'So I need to send a scouting mission to this city?'<br /><br />" + 
				"'Yes,' Genesis responds. 'Then we'll know what we're dealing with. But don't send more than 10, you'll need the rest to fend off the Iddite attack.' <br /><br />"+*/
				"Scout out a distress call. You can do this by sending a scouting mission to " + username + "-DistressCall, which is right next to your city from the World Map. If you do not have a Headquarters yet, you will need to build one to send a scouting mission.<br /><br />Goal: Scout the city next to yours labeled by your radar as " + username+"-DistressCall.<br /><br />" + getRewardBlock(1,pid,additional),"No hint."
				
		};
		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		return "Begin your AI Programming";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		UserRaid[] as = p.getPs().b.getUserRaids(p.getCapitaltid());
		int i = 0;
		UserRaid r; Town t2; String mem;
		while(i<as.length) {
			r = as[i];
			t2 = God.findTown(r.getTID2());
			if(t2.getPlayer().ID==ID) {
				mem = readFromMemory(pid);
				if(r.raidType().equals("scout")&&!r.raidOver()&&r.eta()<=2&&!mem.contains("raidHit;")) {
					// so if you have a scout heading over there and none has hit before...record it.
					if(!mem.contains("raidHit;"))
					writeToMemory(mem+"raidHit;",pid);
					break;
				} else if(r.raidType().equals("scout")&&r.raidOver()&&r.eta()<=2&&mem.contains("raidHit;")) {
					// if a scout is returning from there and at least one has hit before...win it.
					String rd = readFromMemory(pid);
					reward(pid);
					destroy(p);
					killTown(Integer.parseInt(rd.substring(rd.indexOf("loadedcity")+10,rd.indexOf(";"))));
					p.getPs().b.joinQuest(God.getPlayerId("RQ1"));

					
				} 
				
			}
			i++;
		}
	}
	public boolean destroyWithoutCompletion(Player p) {
		// for this special one where we need a city loaded, if it gets destroyed, so does the town.
		String rd = readFromMemory(p.ID);

		 super.destroyWithoutCompletion(p);
		killTown(Integer.parseInt(rd.substring(rd.indexOf("loadedcity")+10,rd.indexOf(";"))));

		return true;
	}
	@Override
	public void playerConstructor(Player p) {
		if(!readFromMemory(p.ID).contains("loadedcity")) {
			
			Town t = new Town(p.getCapitaltid(),God);
			int x = t.getX(); int y = t.getY();
			double resEffects[] = {0,0,0,0,0};
			int tid = -1;
			int pids[] = {5};
			int v[] = {p.ID};
			while(tid==-1) {
				
				tid=addTown(x+1,y,p.getUsername()+"-DistressCall",resEffects,pids,v);
				x++;
			}
			writeToMemory("loadedcity"+tid+";",p.ID);
		}

	}

	@Override
	public void reward(int pid) {
		rewardOneHour(pid);

	}

}
