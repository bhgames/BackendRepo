package BHEngine;

import java.util.ArrayList;

public class BQ3 extends QuestListener {

	public BQ3(int ID, GodGenerator God) {
		super(ID, God);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		if(completedQuest(p,"BQ2")) return true;
		else
		return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String additional[]  = { "10 Knowledge Points" };

		String toRet[] = {"<script type='text/javascript'>$('#IO').click();</script>"+/*"<br /><br />"+

			"‘Is that it? Do I have men?’<br /><br />"+

			"‘See for yourself.’<br /><br />"+

			"You witness a column of men charging out of the Arms Factory. They turn and salute you. ‘Why are they saluting?’<br /><br />"+

			"‘Because many of them are about to die for you.’<br /><br />"+

			"‘…but why? I don’t even know who I am.’<br /><br />"+

			"‘Stop whining. All we can do now is wait.’<br /><br />"+
			"'But I don't want them all to die. It'll take forever to build them again!'<br /><br />"+
			"'What do you think this is?' Genesis responds. 'An old fashioned browser game where you spend weeks " +
			"rebuilding after one defeat?'<br /><br />" +
			"You don't respond, unsure of yourself.<br /><br /> 'Well of course it's not!' Genesis answers. 'Both your combat unit prices and build times grow or shrink as your "+
			"army size does, and because of this, you are ALWAYS two days or less from a complete rebuild of your army, no matter what, and the first unit is always significantly cheaper than the last.'" 
		+"<br /><br />Genesis pauses for a moment. ‘I’m receiving a distress call from a town nearby. I’ve labeled it on your map. It’s the strangest thing.’<br /><br />"+

			"‘How is that – ‘<br /><br />"+

			"‘It’s not a human distress call. It’s an A.I.’<br /><br />"+

			"‘Hold up,’ you say. ‘You have not given me crap and I have no idea what’s going on. Explain.’<br /><br />"+

			"‘Well, there are a whole series of AIs, one for each book in the Bible. Genesis AIs are advisory and are considered the weakest. This is why I was spared the Event, I was too simple and hardened to be wiped. But the higher level AIs…I had thought them dead.’<br /><br />"+

			"‘The Event?’ you wonder.<br /><br />"+

			"‘Yes, the shutdown event that occurred ‘ – ‘seconds ago.’<br /><br />"+

			"‘How many?’<br /><br />"+

			"'- seconds ago.’<br /><br />"+

			"‘...You're an asshole..’<br /><br />"+

			"‘Are you an idiot?’ Genesis asks.<br /><br />"+

			"‘Fine, be that way. I’ll think about it,’ you say. ‘What’s in it for me?’<br /><br />"+

			"‘I really can’t say at this time,’ Genesis responds. 'But while you're waiting to get slaughtered, you might as well consider scouting out the call. Also, build me a bunker. We need something to hide in!<br /><br />" */
			
			
			"<br />As you can see from your I/O box, which we've opened for you beneath this Quest Box when you close it, an attack is incoming from an Id town, but you've prepared for it! Unfortunately, you cannot smack talk your victory to Id, because Id is the umbrella name for all unoccupied cities and the now-defunct A.I. that once controlled them. Each unoccupied city is an uncivilized place of maddened robots that occasionally lash out at nearby players, like this Id town is now. They are also havens of new resources and technologies.<br /><br /> While you're busily defending yourself, you should build a bunker. Bunkers serve as defensive barriers – whether they protect resources, soldiers, or civilians, depending on your choice from the Bunker menu. Women and children first...or weapons? It's up to you!<br /><br />Also, you've picked up a distress call from a nearby city. It seems to have something to do with Learning Java. You have the option to leave it alone or to check it out. To investigate it, choose the BQBranchToRQ Quest from the Quest menu.<br /><br /> Goal: Survive the battle and build a bunker.<br /><br />"+getRewardBlock(2,pid,additional)
			/*"<br /><li><a href='javascript:;' id='quest_distress'>click here</a> to investigate a distress call from an empty city. There may be some programming in it for you!<script type='text/javascript'>$('#quest_distress').unbind('click').click(function(){var TP = new make_AJAX();$('#quest_close').click();TP.callback = function() {load_player(player.league,true,true);};TP.get('/AIWars/GodGenerator?reqtype=command&command='+player.command+'.joinQuest(BQBranchToRQ);');});</script></li></ul><br />"*/
			,"No hint."};

		return toRet;
	}

	@Override
	public void iterateQuest(int times, int pid) {
		// TODO Auto-generated method stub
		Player p = findPlayer(pid);
		// when iterateQuest starts, flicker is set to BQ3.

		String r = readFromMemory(pid);
		int townID = Integer.parseInt(r.substring(r.indexOf("loadedcity")+10,r.indexOf(";")));
		Town t = God.findTown(townID);
		if(t!=null) {
			
		ArrayList<Raid> as = t.attackServer();
		if(as.size()==0||as.get(0).isRaidOver()) {
			r+="attackhit;";
			killTown(t.townID);

		//	System.out.println("Destroying BQ3-"+pid + " quest");

		} 
		
	
		}
		int loadedQuestAt=0;
		try {
		 loadedQuestAt = Integer.parseInt(r.substring(r.indexOf("lqa")+3,r.lastIndexOf(";")));
		} catch(NumberFormatException exc) { loadedQuestAt =Integer.parseInt(r.substring(r.indexOf("lqa")+3,r.indexOf("attackhit")-1));}
		if((r.contains("attackhit")||((p.getPlayedTicks())-loadedQuestAt)>=60/GodGenerator.gameClockFactor)&&p.getPs().b.haveBldg("Bunker",p.getCapitaltid())) {

			if(((p.getPlayedTicks())-loadedQuestAt)>=60/GodGenerator.gameClockFactor&&God.getTown(townID)!=null) killTown(t.townID);
			
			reward(pid);
			destroy(p);
			p.getPs().b.joinQuest(God.getPlayerId("BQ4"));
		}
		writeToMemory(r,pid);
		// loadedcity is guaranteed to be the first thing in memory...

	}

	@Override
	public void playerConstructor(Player p) {
		// TODO Auto-generated method stub
		String r = readFromMemory(p.ID);
		int tid = -1;
		if(!r.contains("loadedcity")) {
			Town t = p.towns().get(0);
			int x = t.getX(); int y = t.getY();
			double resEffects[] = {0,0,0,0,0};
			int pids[] = {5};
			int v[] = {p.ID};

			while(tid==-1) {
				tid=addTown(x+1,y,p.ID+"-BQ3",resEffects,pids,v);
				x++;
			}
			r+="loadedcity" + tid + ";";
		}
		if(!r.contains("loadedau")) {
			// well we know for certain the city is at least loaded at this point.
			// if this is the first player, we'll be the one loading the AU we want with this
			//quest.
			
			if(getAu().get(0).getName().equals("Iddite")) r+="loadedau;";
			else {
				int weap[] = {0,0};
				PlayerScript ps = getPs();
				ps.b.createUnitTemplate("Iddite",1,80,20,75,25,weap,0,false);
				ps.b.createCombatUnit(0,"Iddite");
				r+="loadedau;";
			}
		}
		
		if(!r.contains("loadedattack")) {
			//	public boolean attack(int yourTownID, int enemyx, int enemyy, int auAmts[], String attackType, int target,String name) {
		
			Town t = God.findTown(tid);
			Town theirT = God.findTown(p.getCapitaltid());
			t.setSize(0,10);
			//	public Building addBuilding(String type, int lotNum) {

			t.addBuilding("Headquarters",4,1,0);
			int auAmts[] = {10,0,0,0,0,0};
			PlayerScript ps = getPs();
			System.out.println(ps.b.attack(t.townID,theirT.getX(),theirT.getY(),auAmts,"attack",0,"noname") +":"+ps.b.getError());
			
			int i = 0;
			ArrayList<Raid> as = t.attackServer();
			while(i<as.size()) {
				if(as.get(i).getTicksToHit()>2&&!as.get(i).isRaidOver()){
					as.get(i).setTicksToHit(3);
				}
				i++;
			}
			r+="loadedattack;";
			
		}
		r+="lqa"+(p.getPlayedTicks())+";";
		writeToMemory(r,p.ID);

	}
	public boolean destroyWithoutCompletion(Player p) {
		// for this special one where we need a city loaded, if it gets destroyed, so does the town.
		String r = readFromMemory(p.ID);
		int townID = Integer.parseInt(r.substring(r.indexOf("loadedcity")+10,r.indexOf(";")));
	

		 super.destroyWithoutCompletion(p);
		killTown(townID);

		return true;
	}
	@Override
	public void reward(int pid) {
		// TODO Auto-generated method stub
		rewardOneHour(pid);
		rewardOneHour(pid);
		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+10);

	}
	public String getQuestDescription(int pid) {
		return "BQ3";
}

}
