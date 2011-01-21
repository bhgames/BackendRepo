package BHEngine;

import java.util.ArrayList;

public class BQ2 extends QuestListener {

	public BQ2(int ID, GodGenerator God) {
		super(ID, God);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		//System.out.println("Did he complete it?" + completedQuest(p,"BQ1"));
		if(completedQuest(p,"BQ1")) {
			return true;
		}
		else
		return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String additional[]  = { "5 Knowledge Points" };

		String toRet[] = {/*"<b>Reminder: If you find yourself staring at a constructing building that never finishes or the UI otherwise freezes in some way, " +
				" we find that refreshing the browser or using Menu > Refresh works to remove the freeze. Menu > Refresh does not refresh the browser, just your viewer, so it is preferred. </b><br /> <br />"
				+*//*"Prompt:<br /><br />"+

"‘Good.  My name is Genesis, by the way.’<br /><br />"+
"‘Odd name.’<br /><br />"+

"‘It’s my AI designation. I only have the ability to tell you you're an idiot, not to act like one, so I take the lowest designation.’<br /><br />"+

"‘Right,’ you murmur. The darkness on the horizon has grown by two fold. There is now a palpable darkness oozing out of the south into your field of vision. They aren’t far from your food farm. ‘…What is an Iddite?’<br /><br />"+

"‘The worst monsters known to man.’<br /><br />"+

"‘What, are they some kind of beast?’<br /><br />"+

"‘No, they're men.’<br /><br />"+

"‘But you said they were monsters.’<br /><br />"+

"‘They are.’<br /><br />"+

"‘Well then what do we do next?’<br /><br />"+

"‘We need to initiate a Troop Push. This will allow us to build about a day’s worth of cybernetic troops instantaneously and without cost. An institute building has the requisite technology for this feat. Build one now. That button, there.’<br /><br />"+

"‘Gotcha.’<br /><br />"+*/"<br />It's time to execute a troop push! <br /><br />A troop push instantly creates a lot of troops at no cost and in no time in the event of an emergency need, like this one. A troop push can be executed by buying a Troop Push research from an Institute's Military Units tab, but you must have an Arms Factory to use it. This is why we built one in the first place.<br /><br />"+


"Goal: Execute a Troop Push.<br /><br />" + getRewardBlock(1,pid,additional) ,"No hint."};

		return toRet;
		//<a href='javascript:;' id='quest_troopPush'>click here</a> to execute a Troop Push and create around 20 Shock Troopers instantaneously.<script type='text/javascript'>$('#quest_troopPush').unbind('click').click(function(){var TP = new make_AJAX();$('#quest_close').click();TP.callback = function() {load_player(player.league,true,true);};TP.get('/AIWars/GodGenerator?reqtype=command&command='+player.command+'.completeResearches([troopPush]);'+player.command+'.pingQuest(BQ2);');});</script>
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		int i = 0;
		ArrayList<AttackUnit> au = p.towns().get(0).getAu();
		int totalSize=0;
		while(i<au.size()) {
			totalSize+=au.get(i).getSize();
			i++;
		}
		
		if(totalSize>=20) {
			reward(pid);
			destroy(p);
			p.getPs().b.joinQuest(God.getPlayerId("BQ3"));



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
		p.setKnowledge(p.getKnowledge()+5);
	}
	public String getQuestDescription(int pid) {
			Player p =God.getPlayer(pid);
			if(completedQuest(p,"BQ1")) return "Initiate a troop push of shock troopers to use for the defense of your city.";
			else return "Locked.";
	}
	

}
