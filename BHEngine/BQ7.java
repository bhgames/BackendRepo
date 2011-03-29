package BHEngine;

import java.util.ArrayList;

public class BQ7 extends QuestListener {

	public BQ7(int ID, GodGenerator God) {
		super(ID, God);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		if(completedQuest(p,"BQ6")) return true;
		else
		return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String additional[] = {"50 Knowledge Points"};
		String toRet[] = {"Now that you've got a real city under your control, it's time to get a real military! It's time you diversified your forces by unlocking a second type of combat unit besides Shock Trooper. You need two things to start building a new unit: A Manufacturing Tech from the Military Infrastructure tab in the Institute, and a new Unit Blueprint, unlocked from the Military Units tab. Once you've got these, you can load the Blueprint you've got into the new Slot you bought in the Arms Factory menu. If you have any trouble with loading a slot with a blue print, click the ? in the Arms Factory menu.<br /><br /> Goal: Gain the ability to build another unit type of your choosing and build a total of 50 soldiers of any type in your capital city.<br /><br />"+getRewardBlock(10,pid,additional)
				/*"<br /><br />Prompt: <br /><br />"+

"‘Alright, you’re doing great,’ Genesis tells you. <br /><br />"+

"‘Yeah, so when do we strike back…and where?’ <br /><br />"+

"‘I am still trying to find the originating town, it seems to have disappeared off the map as soon as the attack failed.’ <br /><br />"+

"‘What, they made a town disappear?’ You ask. <br /><br />"+

"‘My files on the technological advancements of the Id civilization are corrupted. I do not know the extent of the Iddites’ power. One thing is for certain, they can’t be more organized than the per town level.’ <br /><br />"+

"‘Well then what comes next?’ <br /><br />"+

"‘Army building. You need to expand and diversify your forces.’ <br /><br />"*/,"No hint."
		
		};
		return toRet;
	}

	@Override
	public void iterateQuest(int times, int pid) {
		/*Player p = findPlayer(pid); WILL NO LONGER COMPILE.
		int i = 0;
		
		int capitaltid = p.getCapitaltid();
			if(p.getALotTech()>=1) {
				if(p.getAUTemplates().size()>1&&capitaltid!=-1) {
					i = 0;
					Town t = (God.findTown(capitaltid));
					ArrayList<AttackUnit> au = t.getAu();
					ArrayList<Raid> raids = t.attackServer();
					int size=0;
					while(i<6) {
						size+=au.get(i).getSize()*au.get(i).getExpmod();
						i++;
					}
					int k = 0;
					while(k<raids.size()) {
						 if(raids.get(k).getTown1().townID==t.townID) {
							 i = 0;
							 au = raids.get(k).getAu();
							while(i<6) {
								size+=au.get(i).getSize()*au.get(i).getExpmod();
								i++;
							}
						 }
						 k++;
					}
					
					if(size>=50) {
						reward(pid);
						destroy(p);
						p.getPs().b.joinQuest(God.getPlayerId("BQ8"));
					}
				}
			}
		*/

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
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);

		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+50);
	}
	public String getQuestDescription(int pid) {
		return "BQ7";

}

}
