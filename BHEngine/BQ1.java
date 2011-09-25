package BHEngine;

//import java.util.ArrayList;

public class BQ1 extends QuestListener {

	public BQ1(int ID, GodGenerator God) {
		super(ID, God);
	}

	@Override
	public boolean checkPlayer(Player p) {
		// No checker, this is the beginner quest!
		if(p.getCapitaltid()==-1) return false;
		return true;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String	additional[]  = { "5 Knowledge Points" },
				goals ="<div class='goalBox'>Goals:<ul><li>Queue 5 Engineers</li></ul></div><br/><br/>",
				text = "<div class='descBox'>Hello and Welcome to Steampunk Wars! If this is your first time playing, I encourage you to watch the Tutorial, if you haven't already. This can be accessed via the Menu if you skipped it.<br/><br/>"
						+ "Let's get you started by building some Engineers. Engineers globally decrease building times for your towns and are trained at the Command Center. So, go ahead and click on your Command Center now to open its menu. The Command Center displays information on your current town, as well as acting as a hub for all your military activities.<br/><br/>"
						+ "On the Engineering tab, look for the Engineer training interface. This interface is the same for all trainable units and displays the cost, in both time and resources, as well as the food consumption of the number of units you're attempting to train. Since your Command Center is level 1, it can only house 5 Engineers at a time. Queue up 5 Engineers now.</div>"
						+ "As you can see, your first town starts with a Command Center already built. Only this town will come with this building pre-built. The Command Center is the most important building in your town. It houses the Engineers that build your buildings and nearly every other building depends on it. In addition, no building can be more than 2 levels higher than your highest leveled Command Center.<br/><br/>",
				reward = "<div class='rewardBox'>"+getRewardBlock(1,pid,additional)+"</div>",
				script = "";
		
		
		return new String[] {goals+text+reward,script};
	}
	
	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		PlayerScript ps = p.getPs();
		Building CC = null;
		for(Building b : p.towns().get(0).bldg()) {
			if(b.getType().equals("Command Center")){
				CC = b;
				break;
			}
		}
		
		if(CC.getPeopleInside()+CC.getNumLeftToBuild()>=5) {
			reward(pid);
			destroy(p);
			ps.b.joinQuest(God.getPlayerId("BQ2"));
		}
		

	}

	@Override
	public void playerConstructor(Player p) {

	}

	@Override
	public void reward(int pid) {
		rewardOneHour(pid);
		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+5);
	}
	
	public String getQuestDescription(int pid) {
		return "Getting Started";
	}
	
}
