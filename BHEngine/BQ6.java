package BHEngine;

import java.util.ArrayList;

import BattlehardFunctions.BattlehardFunctions;
import BattlehardFunctions.UserBuilding;

public class BQ6 extends QuestListener {

	public BQ6(int ID, GodGenerator God) {
		super(ID, God);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		if(completedQuest(p,"BQ5")) return true;
		else
		return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String additional[] = {"20 Knowledge Points"};
		String toRet[] = {"Let's expand your city's functionalities. As you've probably figured out, the Institute allows you to upgrade your civilization. For example, to gain extra building lots for new buildings, you can purchase Building Lot Tech in the Civilian Infrastructure tab. A Construction Yard would be useful as it allows you to upgrade your other buildings and make them more powerful and resilient to attack. A Trade Center will allow you to trade resources with other players and ship resources to other territories that you may soon accrue. <br /><br />" +
				" There are three types of civilians in this game: Scholars, Traders, and Engineers. Scholars get built in Institutes and each one generates One Knowledge Point Per Day that you can spend on upgrading your civilization or unlocking new units and abilities. Traders are built in the Trade Center and are used when you send resources or execute trade routes with other players. " +
				"Engineers are built in the Construction Yard and help lower the build times of everything in the game, so you don't have to wait as long to conquer other players.<br /><br />Using this newly learned knowledge, expand your Empire to include a true Civilian populace!<br /><br /> Goal 1: Build a Construction Yard and Trade Center. "+
				"<br /><br />Goal 2: Level all of your buildings to level 2 and your resource generation buildings to Level 4.<br /><br />Goal 3: Fill your Construction Yards, Institutes, and Trade Centers with Civilians at the Level 2 capacity limits.<br /><br />"+getRewardBlock(6,pid,additional)
				/*"Prompt: <br /><br />"+
"‘Alright, good. Now you should be able to bring in those resources and establish the rest of your town.’ <br /><br />"+

"‘And then what?’ <br /><br />"+

"‘…and then we take the fight to them.’ <br /><br />"*/
,"No hint."


		};
		return toRet;
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		int capitaltid = p.getCapitaltid();
		if(capitaltid!=-1) {
			BattlehardFunctions bf = p.getPs().b;
			
				UserBuilding bldg[] = bf.getUserBuildings(capitaltid,"all");
				boolean max=true;
				boolean lvl=true;
				int i = 0;
				UserBuilding b;
				if(bldg!=null)
				while(i<bldg.length) {
					b = bldg[i];
					
					if(!b.getType().equals("Metal Mine")&&!b.getType().equals("Timber Field")
							&&!b.getType().equals("Manufactured Materials Plant")&&!b.getType().equals("Food Farm")) {
						if(b.getLvl()<2) {
							if(p.getUsername().equals("Azel")) System.out.println("Azel failed at building " + b.getType() + " with lvl " + b.getLvl());
							lvl=false; // so it's level 2 for normal buildings, 4 for mines.
							break;
						}
					} else {
						if(b.getLvl()<4) {
							if(p.getUsername().equals("Azel")) System.out.println("Azel failed at building " + b.getType() + " with lvl " + b.getLvl());
							lvl=false;
							break;
						}
						
					}
					if(b.getType().equals("Institute")||b.getType().equals("Construction Yard")
							||b.getType().equals("Trade Center")) {
						if(b.getPeopleInside()<Building.getCap(2,false)){

							max = false;
							break;
						}
					} 
					i++;
				}
				
				
				if(max&&lvl) {
					reward(pid);
					destroy(p);
					p.getPs().b.joinQuest(God.getPlayerId("BQ7"));

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
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);

		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+20);
	}
	public String getQuestDescription(int pid) {
		Player p =God.getPlayer(pid);
		if(completedQuest(p,"BQ5")) return "Grow your city in preparation of mounting an offensive!";
		else return "Locked.";
}

}
