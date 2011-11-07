package BHEngine;

import java.util.ArrayList;

public class RQ2 extends QuestListener {

	public RQ2(int ID, GodGenerator God) {
		super(ID, God);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
	//	if(completedQuest(p,"RQ1")) return true;
	//	else
		return false;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String additional[]={"10 Knowledge Points"};
		String toRet[] = {/*"Prompt: <br /><br />"+

"‘Looks like it can at least talk.’ <br /><br />" + 

"‘It can do much more than talk, you twat,’ Genesis stickily replies. ‘Let’s try something a little more active than words. Let’s make it build a combat unit.’ <br /><br />"+

"‘And how are we going to do that?’ You ask.<br /><br />"+

"‘Through the use of arrays.’ <br /><br />" + 

"‘Right…’ Back to work, you think."*/

"<br />"+ getRewardBlock(5,pid,additional)+"<br /><br />Eve can do many things besides send yourself messages. Each API you unlock in the Institute's AI Research tab allows you to write in new commands for her that do different amazing things for your civilization such as attacking players for you, upgrading your buildings, and even doing smack talking on your behalf! (Talk about potential propaganda wars!) Now that you've wet your feet a little bit using Eve, let's she what she can really do. For the duration of this Quest, you've been given access to a small part of the Build Automation API upgrade in the Institute so that you can test out Eve's building capabilities for yourself. <br /><br />After the five programming quests, you should have earned enough Knowledge Points to unlock this or another basic API, based on your preference of A.I. strategy. <br /><br />Goal: Build a single Shock Trooper with Eve. <br /><br />Tutorial:<br /><br />" +
"Before we begin, I'd like to introduce " +
"two new types of variable boxes called the boolean and the int. The int variable can hold only whole integers and the boolean variable can hold only one of two values: true or false. An example of a boolean "+
"condition would be 'Do I have enough metal?' The result of evaluating this boolean condition in English is the answer 'Yes' or 'No.' In the same way, Eve can ask and store the answer to such a question in a boolean variable as true or false. <br /><br /> Also, Eve can encapsulate a question and answer in a single variable. For instance, I can say boolean x = true; and Eve will remember that x is the answer to some question, and that that answer was true. But what if I want to somehow store the question with x? Wouldn't it be wonderful to just " +
"type boolean x = 'true if I have 50 metal';? Well, you can. You can type boolean x = bf.haveMetal(50,\"someTownName\"); and Eve will first ask the question and then store the answer in x. Notice that I used a word in the second part of the () box for the function without having first put that word in a String variable and naming it something, like String y = \"someCityName\". I'm allowed to do this in Java. Eve just creates a temporary box for that String, just long enough to replace what I typed there in the second part of the () with " +
" that temporary variable's name, then runs the hidden script, and deletes it right after. <br /><br />Even cooler than this question-answer combo, you can always trust the haveMetal function to ask and return an answer. You don't even need to store it. You can just type haveMetal(50,\"someCityName\"); and it'll ask and return an answer that Eve never remembers or cares about.<br /><br /> Now, the reason I digress on all this booleanic nonsense is because if statements LOVE boolean conditions... and we're going to need If statements." +
" If statements encapsulate pieces of the script Eve will only read if a certain boolean condition like 'Do I have 50 metal?'(or, in Eve-speak, bf.haveMetal(50,\"someCityName\");) is asked and returns true. We can use this programming functionality to cause Eve only to read the build-combat-unit part of her script if she finds you have the requisite resources.<br /><br />"+
"In this case, we're going to create a series of If statements, one inside the other. Each if statement is going to ask if Eve has the resource at hand to make the build. First we'll have her check metal, and then if there is enough of that, then she'll go on to detect timber. If there isn't enough metal, she'll never even look at the timber or any of the other resources, much less the build combat unit command in that little piece of the script. In this way we can create the beginnings of an A.I. that can govern your Army Production!"+
" <br /><br /> Step A:<br /><br /> Write down the price of what the next Shock Trooper would cost by checking out it's price for a single one in the Arms Factory, and then create four int variables, one for each resource. Each of these will hold the cost in that resource to make the unit. We can then go ahead and use these later on to test against how much of the resource we actually have." +
" Remember, int variables, unlike String variables, can only hold whole integers! <br /><br /> <img src='../images/quests/RQ2-1.png' style='height:154px' />" +
"<br /><br /> Step B: <br /><br /> Next, we're going to construct an if statement. Remember what I said earlier about those 'shorthand' functions that help you out? In this case, we want the help of the haveMetal, haveTimber, haveManMat, and haveFood functions. Each of these allow Eve to read a hidden script that asks if you have the resource mentioned, and auto-returns a true or false answer for the If statement to know whether or not Eve can read the script inside of it. So haveMetal(), when used, first checks if you have the metal or not, and " +
"then will return true or false if you've got it or not. Let's set up an If statement now. <br /><br /> <img src='../images/quests/RQ2-2.png' style='height:152px'/> " +
"<br /><br /> Step C: <br /><br /> Create a nested if statement set - if a you have the metal, then you know Eve will run the script inside the If statement's surrounding box of {'s. Once inside the metal If statement, you should check the timber, and create a hidden piece of script inside the metal script that Eve can only read if she has the Timber. Then inside the Timber, put the Manufactured Materials. It's like a series of locked doors, or one of those Russian dolls that opens to another smaller doll! So you can run an if statement to see if you have the timber, and then inside that, the manufactured materials, and so on." +
" <br /><br /> <img src='../images/quests/RQ2-3.png' style='height:424px' /> "
+"<br /><br />Step D: <br /><br />Use the buildCombatUnit function to put a combat unit on the queue. The buildCombatUnit function needs a few boxes passed to it, but nothing that you can't already handle or figure out.<br /><br /> " +
" <br /><br /> <img src='../images/quests/RQ2-4.png' style='height:366px'/> ","No hint.",
		
		""/*"<object width=\"425\" height=\"344\"><param name=\"movie\" value=\"http://www.youtube.com/v/yjYdfzBND5E?hl=en&fs=1\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/yjYdfzBND5E?hl=en&fs=1\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"425\" height=\"344\"></embed></object>"*/};
		return toRet;
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		
		String story[] = {"newprogram();","haveMetal();","buildCombatUnit();"};
		if(logContains(story,-1,p)) {
			reward(pid);
			destroy(p);
			p.getPs().b.joinQuest(God.getPlayerId("RQ3"));

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
		p.setKnowledge(p.getKnowledge()+10);
		

	}
	
	public String getQuestDescription(int pid) {
		Player p =God.getPlayer(pid);
		if(completedQuest(p,"RQ1")) return "Build a combat unit with your Revelations A.I.!";
		else return "Locked.";
}
	

}
