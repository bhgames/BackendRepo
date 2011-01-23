package BHEngine;

import java.util.ArrayList;

public class BQ4 extends QuestListener {

	public BQ4(int ID, GodGenerator God) {
		super(ID, God);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		// TODO Auto-generated method stub
		if(completedQuest(p,"BQ3")) return true;
		else
		return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		// TODO Auto-generated method stub
		String additional[]  = { "10 Knowledge Points" };

		String theThingToSay = 	"‘I’m gonna need one of those, aren’t I?’<br /><br />"+

		"Genesis pauses. ‘You’re going to need a lot of things. This war is just beginning, and we're damned ill-equipped to fight it.’<br /><br />";
		if(God.getPlayer(pid).getPs().b.haveBldg("Headquarters",God.getPlayer(pid).getCapitaltid())) theThingToSay="";
		String toRet[] = {/*"Prompt:<br /><br />"+
			"‘What the hell were those things?’ you shout, pushing yourself off the ground and brushing off bits of broken glass and debris. An Iddite nearly got to your control tower but you managed to hold him off.<br /><br />"+

					"He lies on the floor, in a pool of mercurial blood, twitching and sparking in his last moments of life. You heft your EMP Pump Action in your hand and cock it one more time, blowing a new hole into the writhing form.<br /><br />"+

					"‘So you’re telling me every citizen gets one of these?’<br /><br />"+

					"‘Yes. You can change what you’re equipped with in a Headquarters.’<br /><br />"
					
					+theThingToSay+
					"‘What do we need to do?’ You ask.<br /><br />"+

					"‘Well, we need to build a few different structures to support an economy again, and we need to raid the surrounding dead cities for resources.’<br /><br />"+

					"‘How many dead cities are there?’<br /><br />"+

					"‘More than you can count.’<br /><br />"+

					"‘Is that where those things came from?’<br /><br />"+

					"‘Yes and no. They’re roamers, but they originated from these cities. The city you now stand in was previously dead, until you woke up. You are the only survivor in this city.’<br /><br />"+

					"‘What happened to them all?’<br /><br />"+

					"‘Let’s first concentrate on the task at hand.’<br /><br />"+

					"‘I’m going to keep asking,’ you promise.<br /><br />"+

					"‘I know.’<br /><br />",*/

					"<br />It's time to fight back! As you've been told, Id towns, while home to dangerous zombie-like foes called Iddites, are also some of the largest resource and technology depots in the A.I. Wars Universe!<br /><br />Before you can attack an Id town, you'll need a Headquarters from which to direct your troops. You can build a Headquarters on the lot that sticks out from the top four in your town only. Then you can send attacks to any Id city on the World Map! If you are unsure how to send attacks, you can always press the ? button on the World Map.<br /><br />Goal: Build a Headquarters and successfully attack and pillage one of Id's cities.<br /><br />" + getRewardBlock(2,pid,additional) ,"No hint."};

		return toRet;
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		ArrayList<Raid> as = p.towns().get(0).attackServer();
		int i = 0; boolean found=false; // of course we may not necessarily get the fastest raid...but oh well, who cares, right?
		Town t;
		while(i<as.size()) {
			t = as.get(i).getTown2();
			if(t!=null&&t.getPlayer().ID==5&&as.get(i).isRaidOver()) {
				reward(pid);
				destroy(p);
				p.getPs().b.joinQuest(God.getPlayerId("BQ5"));
			}
			i++;
		}
		/*
		if(r.contains("attacksent")) {
		

			Raid a = p.findRaid(Integer.parseInt(r.substring(r.indexOf("attacksent")+10,r.indexOf(";"))));
			if(a!=null) {
			boolean raidOver = a.isRaidOver(); int ticks = a.getTicksToHit(); 
		//	if(a.raidID==8798)
		//	System.out.println("Raid is " + raidOver+ " and ticks are " + ticks + "and r is  " + r);
			if(!raidOver&&ticks==1) { 
				r+="attackhit;";
				writeToMemory(r,pid);
			} else if(raidOver&&ticks==1&&r.contains("attackhit")){
			//	System.out.println("Attack hit is in it.");
				reward(pid);
				destroy(p);
				p.getPs().b.joinQuest(God.getPlayerId("BQ5"));
			//	p.getPs().b.joinQuest(God.getPlayerId("BQBranchToRQ"));


			} 
			
			}else {
				
				// clearly something happened, we need to reset the entire deal.
				writeToMemory("",pid);
			}
		} else {
			ArrayList<Raid> as = p.towns().get(0).attackServer();
			int i = 0; boolean found=false; // of course we may not necessarily get the fastest raid...but oh well, who cares, right?
			Town t;
			while(i<as.size()) {
				t = as.get(i).getTown2();
				if(t!=null&&t.getPlayer().ID==5&&!as.get(i).isRaidOver()) {
					found = true; break;
				}
				i++;
			}
			if(found) {
			r+="attacksent"+as.get(i).raidID+";";
			writeToMemory(r,pid); }
		}*/

	}

	@Override
	public void playerConstructor(Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reward(int pid) {
		rewardOneHour(pid);
		rewardOneHour(pid);
		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+10);
	}
	public String getQuestDescription(int pid) {
		Player p =God.getPlayer(pid);
		if(completedQuest(p,"BQ3")) return "Fight back: Hit an Id city.";
		else return "Locked.";
}
}
