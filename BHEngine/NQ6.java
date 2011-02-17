package BHEngine;

public class NQ6 extends QuestListener {

	public NQ6(int ID, GodGenerator God) {
		super(ID, God);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		// TODO Auto-generated method stub
		if(completedQuest(p,"NQ5")) {
			return true;
		}
		else
		return false;
		}

	@Override
	public String[] getCurrentQuestText(int pid) {
		// TODO Auto-generated method stub
		String additional[]  = { "30 Knowledge Points" };
		Player p = God.getPlayer(pid);
		String goal = "";
		String paragraph ="";
		String paragraph2 = "";
		if(p.getVersion().equals("military")) {
			goal = "<li>Build a Construction Yard.</li><li>Train 5 Engineers.</li>";
		//	paragraph = "Click on a nearby town.  Any town will do, but Id towns will be mostly defenseless.";
		//	paragraph2="Make sure that “Attack” is selected in the mission list.  Then, select some units to send.  You'll notice that the “Selected Army Size” changed when you typed in the number of units to send.  This shows the “Cover Size” of the selected group.  Having a “Cover Size” close to the “Cover Size Limit” (CSL) of the target town improves your combat odds.  The CSL of a town can be gained by scouting the town, or from the “Additional Combat Info” section of a Status Report from that town. If everything is in order, click “Send”.";

		}
		else if(p.getVersion().equals("civilian")){
			goal = "<li>Build an Arms Factory.</li><li>Train 5 Shock Troopers.</li>";
			//paragraph = "The Engineers trained by your Construction Yard reduce the build times of pretty much everything.  Go ahead and build 4 more now..";
		//	paragraph2="Select “Civilian” from the Mission list.  Since you selected an Id town, you should see that the type of mission is an “Archaeological Dig”.  On Digs, you can discover treasures left behind in Id towns.  Everything from resources to full tech upgrades can be found.  Normally, you need 10 scholars to perform a dig, but, for this quest only, we'll allow you to send with less.If everything is in order, click “Send”.";

		}

		String toRet[] = {getRewardBlock(9,pid,additional)+"<br /><br />Goals:  <ul><li>Build a warehouse of each type.</li><li>Upgrade a building(requires a Construction Yard.)</li><li>"+goal+"</li></ul><br /><br />"+
				"Excellent!  The Knowledge Points (KP) used to purchase tech upgrades are produced by Scholars in your towns.  The KP pool and tech upgrades are universal and apply to all your towns.<br /><br />"+
				"We're almost finished.  Your last task is to get this town fully operational.  If you forget how to do anything, you can view your quest logs by clicking the third button from the bottom of the sidebar on the left.<br /><br />"
				
				
			
,"No Hint"};
		return toRet;
		}

	@Override
	public String getQuestDescription(int pid) {
		// TODO Auto-generated method stub
		return "Complete Your Town";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = God.getPlayer(pid);
		Town t = God.getTown(p.getCapitaltid());
		if(t!=null&&t.townID!=0) {
			
			if(p.getPs().b.haveBldg("Metal Warehouse",t.townID)&&
					p.getPs().b.haveBldg("Timber Warehouse",t.townID)
					&&p.getPs().b.haveBldg("Manufactured Materials Warehouse",t.townID)
					&&p.getPs().b.haveBldg("Food Warehouse",t.townID))  {
				int i = 0; boolean foundOne=false; Building b;
				while(i<t.bldg().size()) {
					b = t.bldg().get(i);
					if(!b.getType().equals("Metal Mine")&&!b.getType().equals("Timber Field")&&
							!b.getType().equals("Manufactured Materials Plant")&&!b.getType().equals("Food Farm")&&b.getLvl()>1) {
						foundOne=true;
						break;
					}
					i++;
				}
				
				if(foundOne) { // so we've got a lvl 2+ bldg, all the warehouses...now check specifics.
					
					if(p.getPs().b.haveBldg("Arms Factory",t.townID)&&p.getVersion().equals("civilian")&&t.getAu().get(0).getSize()>=5) {
						// have an AF and have soldiers...
						// REWARD!!!
						reward(pid);
						destroy(p);
					} else 
						if(p.getPs().b.haveBldg("Construction Yard",t.townID)&&p.getVersion().equals("military")&&t.getTotalEngineers()>=5) {
							// have a CY and engineers...
							
							reward(pid);
							destroy(p);
						
						}

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
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);
		rewardOneHour(pid);	
		rewardOneHour(pid);

		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+30);
	}

}
