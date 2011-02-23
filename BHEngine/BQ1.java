package BHEngine;

import java.util.ArrayList;

public class BQ1 extends QuestListener {

	public BQ1(int ID, GodGenerator God) {
		super(ID, God);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		// No checker, this is the beginner quest!
		if(p.getCapitaltid()==-1||!p.getVersion().equals("original")) return false;
		if(p.getVersion().equals("new")||p.getVersion().equals("military")||p.getVersion().equals("civilian")) return false;
		return true;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		// TODO Auto-generated method stub
		String additional[]  = { "5 Knowledge Points" };
		String toRet[] = {/*"<b>You are 3 Quests(approximately 15 minutes) from the programming quests that teach you Java. If already know some basic Java, click on the icon of the woman on the left sidebar and hit new to get started. You can download an API off our forum to get some of the commands you'll need.<br /><br />Note: If your buildings do not go from red to black within a thirty seconds of building them, hit Menu > Refresh.<br /><br /></b>"+	
			
			"Prompt:<br /><br />" + 

		"You open your eyes to find yourself in your own casket.<br /><br />"+

"You awake in a room filled with LCD monitors jutting out of nearly every available patch of gunmetal wall, all of them blank, dark, and dead. You do not know how you got here, or where you are. You climb out of the casket.<br /><br />" +

"It’s not a casket, it’s some type of cryo-bay. You know this, but not how you knew it. All of the monitors begin flashing violently, throwing bursts of light across the room. You fall back and hide your eyes.<br /><br />"+

"‘Emergency. Iddites in bound.’<br /><br />"+

"You shake your head against the voice and push back into the casket.<br /><br />"+

"‘Get up, damn it. We’ve got a war to fight.’ The voice is gruff and angry, yet without a hint of scratchiness or unevenness of voice. It’s odd, artificial.<br /><br />"+

"‘Who is that?’<br /><br />"+

"‘No time for that now.’<br /><br />"+

"You peek out to see each monitor filled with a single blinking eye now.  All light blue pastel hues, calming yet cold at the same time. Every time the voice speaks, the eyelid blinks repeatedly in rhythm with the consonants.<br /><br />"+

"‘Stop being a slacker and get the hell out of bed.’<br /><br />"+

"You ignore the voice.<br /><br />"+

"You’re shocked by the casket and thrown out onto the floor, naked, splayed, and knocked unawares.<br /><br />"+

"‘Get the hell outside right now.’<br /><br />"+

"You struggle up and find a gunmetal gray door made of hardened steel that is warped in the far corner. The doorknob is square, hard, and unfriendly. You squeeze it hard and yank it open.<br /><br />"+

"You’re faced with a dusty, barren wasteland of dead bodies and destroyed buildings.<br /><br />"+

"There is nobody left alive in this city.<br /><br />"+

"‘They’re coming.’<br /><br />"+

"‘Who?’<br /><br />"+

"‘We need to prepare a defense. Only you can do that. There are some stairs to your left, head up them and inside you will find a control room. You’ll find clothes there.’<br /><br />"+

"Indeed you do. It is a small tower that gives you a birds eye view of the dead city. You can see now that every fallen body glints in the mid-morning sun – as if sprinkles of metal had been scattered like snowflakes during the night. This is the only remaining building, and was definitely not the largest.<br /><br />"+

"There are mesas in the distance, large ones. Beyond, you can see timber fields, manufactured materials plants, steel mills and food farms. Each building winks at you in the sunlight. But beyond that, a darkness is gathering. You can’t tell whether it’s just the horizon, or something on the horizon.<br /><br />"+

"‘Do you see it?’<br /><br />"+

"‘What is it?’<br /><br />"+

"‘It’s Id's forces. See that button there? We need to build an Arms Factory and an Institute to build this city’s defenses.’<br /><br />"+

"‘What's Id?’<br /><br />"+

"'It's what'll happen to you if you go inactive for more than 48 hours in the first week of playtime!'<br /><br />" +

"'What, you mean, like I lose my account?'<br /><br />"+

"‘Yes - Id comes after new inactive players especially. Now, there is no time. Build the Arms Factory and Institute!’<br /><br />"*/

"<br />We've just received a signal that an attacking force of zombie robots is on it's way to pillage your new territories while you're weak! You need to prepare! If you want to survive this attack, you're going to need some soldiers, and you're going to need them quickly. Let's get prepared to raise an army!<br /><br />Note: For new players, one of our number one players has written a guide to help get you started. If you're interested, click <a href = 'http://battlehardalpha.xtreemhost.com/viewtopic.php?f=6&t=651'>here</a><br /><br />Goal: Build an Arms Factory and an Institute.<br /><br />"+getRewardBlock(1,pid,additional),"No Hint"};
		
		
		return toRet;
	}
//<li>To do this: Click on the dark squares in your town view to open the build menu. Select the building you wish to build, and then hit build again on the new window that comes up with information about that building.</li> <br /><li>Note: You can reopen this BQ1 menu from the quest info menu. To get there, try clicking on the cube on the left menu bar.</li><br /><li>If your buildings appear red(as in they are being built) for more than a few seconds, hit Menu > Refresh to see if you've lost connection with the server.</li><br /><li>We'd like to hear your feedback. To take a survey on your gaming experience, please press the link in the upper corner of the page or <a href='http://www.surveymonkey.com/s/HWL86VM'>Click here</a></li>
	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		ArrayList<AttackUnit> au = p.getAu();
		PlayerScript ps = p.getPs();
	
		if(ps.b.haveBldg("Arms Factory",p.getCapitaltid())&&ps.b.haveBldg("Institute",p.getCapitaltid())&&!au.get(0).getName().equals("empty")&&!au.get(0).getName().equals("locked")) {
			reward(pid);
			destroy(p);
			ps.b.joinQuest(God.getPlayerId("BQ2"));
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
		return "BQ1";
	}
	
}
