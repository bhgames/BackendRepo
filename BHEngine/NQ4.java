package BHEngine;

public class NQ4 extends QuestListener {

	public NQ4(int ID, GodGenerator God) {
		super(ID, God);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		// TODO Auto-generated method stub
		if(completedQuest(p,"NQ3")) {
			return true;
		}
		else
		return false;	
		
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		String additional[]  = { "20 Knowledge Points" };
		Player p = God.getPlayer(pid);
		String goal = "";
		String paragraph ="";
		String paragraph2 = "";
		if(p.getVersion().equals("military")) {
			goal = "<li>Send an attack to a nearby town.</li>";
			paragraph = "Click on a nearby town.  Any town will do, but Id towns will be mostly defenseless.";
			paragraph2="Make sure that “Attack” is selected in the mission list.  Then, select some units to send.  You'll notice that the “Selected Army Size” changed when you typed in the number of units to send.  This shows the “Cover Size” of the selected group.  Having a “Cover Size” close to the “Cover Size Limit” (CSL) of the target town improves your combat odds.  The CSL of a town can be gained by scouting the town, or from the “Additional Combat Info” section of a Status Report from that town. If everything is in order, click “Send”.<br /><br />";

		}
		else if(p.getVersion().equals("civilian")){
			goal = "<li>Send a dig to a nearby town.</li>";
			paragraph = "The Engineers trained by your Construction Yard reduce the build times of pretty much everything.  Go ahead and build 4 more now..";
			paragraph2="Select “Arch. Dig” from the Mission list.  Since you selected an Id town, you should see that the type of mission is an “Archaeological Dig”.  On Digs, you can discover treasures left behind in Id towns.  Everything from resources to full tech upgrades can be found.  Normally, you need 10 scholars to perform a dig, but, for this quest only, we'll allow you to send with less. If everything is in order, click “Send”.<br /><br />";

		}

		String toRet[] = {getRewardBlock(5,pid,additional)+"<br /><br />Goals:  <ul>"+goal+"</ul><br /><br />"+
				"Excellent!  Now it's time to learn how to really use those units. Open the World Map.  The map contains a great deal of information about the towns around you.  Take a moment and survey your surroundings.  As you'll notice, a number of towns are owned by “Id”.  Id is not a player.  Rather, it's a derelict AI from ages past.<br /><br />"+paragraph+" Click “Send mission” in the menu that appears.  This will take you to your Headquarter's “Send Mission” tab.<br /><br />"+paragraph2

				
			
,"No Hint"};
		return toRet;
		}

	@Override
	public String getQuestDescription(int pid) {
		Player p = God.getPlayer(pid);
		if(p.getVersion().equals("civilian"))
			return "Send a Dig!";
		else if(p.getVersion().equals("military")) {
			return "Send an Attack!";
		
		} else return "Send A Mission!";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		int i = 0;Player p = God.getPlayer(pid);
		Town t = God.getTown(p.getCapitaltid());
		if(t!=null&&t.townID!=0) {
			Raid holdAttack;
			
			while(i<t.attackServer().size()) {
				holdAttack = t.attackServer().get(i);
				if(holdAttack.getTown1().townID==t.townID)
				if(holdAttack.getDigAmt()>0&&p.getVersion().equals("civilian")) {
					
					reward(pid);
					destroy(p);
					p.getPs().b.joinQuest(God.getPlayerId("NQ5"));
					break;
				} else if(holdAttack.getSupport()==0&&holdAttack.getScout()==0&&!holdAttack.isDebris()&&holdAttack.getDigAmt()==0&&p.getVersion().equals("military")) {
					reward(pid);
					destroy(p);
					p.getPs().b.joinQuest(God.getPlayerId("NQ5"));
					break;
				}
				i++;
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


		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+20);
	}

}
