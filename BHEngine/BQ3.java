package BHEngine;

//import java.util.ArrayList;

public class BQ3 extends QuestListener {

	public BQ3(int ID, GodGenerator God) {
		super(ID, God);
	}

	@Override
	public boolean checkPlayer(Player p) {
		if(completedQuest(p,"BQ2")) return true;
		else
			return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String additional[]  = { "15 Knowledge Points" };

		String toRet[] = {"<div class='goalBox'>Goals:<ul><li>Queue 5 Pillagers</li><li>Train 1 Pillagers</li></ul></div><div class='descBox'>" +
				"Very Good! These buildings are very important to the operation of both your town and empire.<br/><br/>" +
				"Now, we have to concern ourselves with the defense of your new town. Enter your Arms Factory now. From here, you can produce any soldier unit you've unlocked. Right now, you only have access to the 'Pillager' blueprints. Selecting a blueprint will display information about that unit type, such as it's damage type and and attack power as well as how many of that unit you have in your current town.<br/><br/>" +
				"For now, let's train 5 Pillagers. We'll need a soldier for your next assignment, so we'll have to wait until one is finished.<br/>" +
				"While you wait, You'll also want to train some scholars so you can start generating RP.</div><div class='rewardBox'>"+getRewardBlock(1,pid,additional)+"</div>","No hint."};

		return toRet;
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		Town t = p.towns().get(0);
		int built = 0, queued = 0, slot = 0;
		for(AttackUnit a : t.getAu()) {
			if(a.getName().equals("Pillager")) {
				built = a.getSize();
				slot = a.getSlot();
				break;
			}
		}
		for(Building b : t.bldg()) {
			if(b.getType().equals("Arms Factory")) {
				for(QueueItem q : b.Queue()) {
					if(q.getAUtoBuild()==slot)
						queued+=q.getAUNumber();
				}
			}
		}
		
		if(built>1&&built+queued>4) {
			reward(pid);
			destroy(p);
			//auto finish the remaining AU, if any
			for(AttackUnit a : t.getAu()) {
				if(a.getName().equals("Pillager")) {
					a.setSize(a.getSize()+Math.max(5-built, 0));
					break;
				}
			}
			if(built<5) {
				for(Building b : t.bldg()) {
					if(b.getType().equals("Arms Factory")) {
						for(QueueItem q : b.Queue()) {
							if(q.getAUtoBuild()==slot&&built<5) {
								if(q.getAUNumber()<built) {
									built+=q.getAUNumber();
									q.setAUNumber(0);
									q.deleteMe();
								} else {
									q.setAUNumber(q.getAUNumber()-(5-built));
									built=5;
									break;
								}
							}
						}
					}
					if(built>=5) {
						break;
					}
				}
			}
			p.getPs().b.joinQuest(God.getPlayerId("BQ4"));
		}
	}

	@Override
	public void playerConstructor(Player p) {

	}

	@Override
	public void reward(int pid) {
		rewardOneHour(pid);
		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+15);

	}
	public String getQuestDescription(int pid) {
		return "Militarizing";
	}

}
