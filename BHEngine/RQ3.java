package BHEngine;

import java.util.ArrayList;

public class RQ3 extends QuestListener {

	public RQ3(int ID, GodGenerator God) {
		super(ID, God);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		if(completedQuest(p,"RQ2")) return true;
		else return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		// TODO Auto-generated method stub
		String additional[] = {"20 Knowledge Points"};
		String toRet[] = {/*"Prompt: <br /><br />"+

"‘I am beginning to see how Rev could be useful to me.’<br /><br />"+

"‘Really?’ Genesis asks. ‘Because I had thought you particularly inept at foresight.’<br /><br />"+

"‘One day, I’ll find your datacore,’ you promise him. ‘What next?’<br /><br />"+

"‘Well, we know Revelations can do things, and it can say things. What we need to test now is your ability to use the Revelations AI to actually help you in your every day use.’<br /><br />"+

"‘In what way?’<br /><br />"+

"‘Well, let’s try something a bit different. Let’s turn Revelations into an auto-farmer.’<br /><br />"+

"‘Sounds kind of tough,’ you mutter. ‘I don’t think I’m ready quite yet.’<br /><br />"+

"‘Well, then we’ll start simple. Have Revelations just do one of your normal farming raids for you.’<br /><br />"+

"‘Right, will do.’<br /><br />",*/
"<br />" + getRewardBlock(7,pid,additional) + "<br /><br />It's pretty easy to build combat units or send messages with Eve. But are you smart enough to figure out how to attack with her? She can be your very own hammer of vengeance on those who have the gall to attack you while you're sleeping if you work her right! Let's take a look at how to get started with that. For the duration of the next three quests, you have access to the Attack API, which can be purchased from the Institute at any time. <br /><br />Goal: Attack an Id city using Eve. <br /><br />Tutorial: <br /><br /> Before" +
		" we truly get started with this quest, you're going to need a quick understanding of arrays. Arrays are like shelves for boxes of the same type. In the last quest, you created four different int variables that held the different costs in each of the four resources for a unit. Don't you think that's kind of" +
		" disorganized? Well, you're right(and if you disagreed, shut up.)<br /><br /> It'd have been neater to store them in some sort of more organized way, like with the equivalent of a shelf with actual boxes. In Java this is called an array. What it does is it allows you to store multiple boxes under the same name, sort of like multiple boxes of different broken overpriced Apple products on a shelf labeled 'Crap.' Imagine one of the boxes on the Crap shelf is named 1, and it holds 5 iPads, and another is named 2, and holds" +
		" 4 iPhones that don't run the Android OS(Which is better because guess what? It uses Java, the language you're learning!) Then you could tell someone to either incinerate 'Crap 1' or 'Crap 2' and they'd easily know which box to light up! <br /><br />Now, you could instead take the boxes off the shelf, put them on the ground, and label one 'large crappy iPhones', and the other" +
		" just 'crappy iPhones,' but you'd have to use a lot more syllables to get your order across, and in the time it takes you to destroy these evil contraptions, Steve Jobs might release another iPhone/Pad! In the same way, Java allows you to say something like int[] x = {11,25}; and define a shelf named x, with two boxes inside of it, one with the value 11 and another" +
		" with the value 25. Then you can reference the number 11 by using the label x[0], and the number 25 by using x[1]. If you expected me to use 1 and 2 in the [], not 0 and 1, you need to get used to what is called zero-based indexing. Java loves starting to count from 0. So an array of length 2 has two boxes, and the first box is called the 0th box, or in Java parlance, the 0th element.  This is meant to help you" +
		" keep your program more organized.<br /><br />" +
		"Step A:<br /><br /> Before you send an attack, you need to use an integer array to specify how much of each combat unit you want to send. In A.I. Wars, you can have a max of six different " +
		"combat units, so you must have at the very least six different values, each representing the amount of each of the different unit types that you want to send. The amounts must be ordered in the same way your units are in the Arms Factory." +
		" Now, even though you may not have all six slots unlocked at this point, you still must specify sizes for the locked slots. You just put zero instead of a number. " +
		" Now, create an int array of length six and then fill up it's six indexes with the unit sizes you want to use. <br /><br /> Notice that zero-based indexing plays a part in the setting of the variables, and that the way I define this array is different from the way I explained it before. Here I first declare the shelf and then fill it later on. Notice also that I do not have to tell Eve what type of box each box on the shelf I made is," +
		" becuase she already knows from my declaration of the shelf being an int shelf. There are many ways to mock a hippie - similarly, you can create an array or shelf and fill it instantly on creation or make an empty one and fill it's boxes later." +
"<br /><br /> <img src='../images/quests/RQ3-1.png' />" +
"<br /><br /> Step B: <br /><br /> Set up the rest of the variables you need and make the function call to attack. You need to start using your townID instead of your town name," +
" as your townID is a unique identifier for your town, and every town has one. Also, most of the functions you use in this game require your townID, not it's name. Your main town's townID is " + God.getPlayer(pid).towns().get(0).townID + ". You can access this number again any time by reopening this Quest Window from your finished quest list when you complete this challenge. Also choose the x and y of a nearby Id town as your target x and target y variables" +
		" so the attack knows where it's going. <br /><br /> Once you've set up your variables, use the attack function and make sure to get the ordering of the boxes in the () correctly according to this example. They must be in that order for Eve to read her attacking script correctly! <img src='../images/quests/RQ3-2.png' /> ","No hint."
,""/*"<object width=\"425\" height=\"344\"><param name=\"movie\" value=\"http://www.youtube.com/v/u5xcBsCkJnE?hl=en&fs=1\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/u5xcBsCkJnE?hl=en&fs=1\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"425\" height=\"344\"></embed></object>"*/};
		return toRet;
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		
		String story[] = {"newprogram();","attack();"};
		if(logContains(story,0,p)) {
			reward(pid);
			destroy(p);
			p.getPs().b.joinQuest(God.getPlayerId("RQ4"));

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

		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+20);
	}
	public String getQuestDescription(int pid) {
		Player p =God.getPlayer(pid);
		if(completedQuest(p,"RQ2")) return "Attack an Iddite town with your Revelations A.I.!";
		else return "Locked.";
}

}
