package BHEngine;

import java.util.Hashtable;

public class CQ1 extends QuestListener {
	
	public CQ1(int ID, GodGenerator God) {
		super(ID, God);
		// TODO Auto-generated constructor stub
	}
	public void onServerLoad() {
		// load up listeners here for digFinish and onRaidLanding, and onProgramLoad
		for(Player p: getPlayers()) {
		//	System.out.println("Actually loading all the event listeners.");
			String r = readFromMemory(p.ID);
			if(r!=null&&!r.contains("done")) {
			p.addEventListener(this,"onProgramLoad");
			p.addEventListener(this,"onRaidSent");
			}
			if(r!=null&&r.contains(";")) {
				 r = r.substring(0,r.indexOf(";"));
				Town t = God.findTown(r,God.getPlayer(5));
				t.addEventListener(this,"digFinish");
			//	System.out.println("Added digFinish to " + t.getTownName());
			}
			
		}
	}
	
	public void onProgramLoad(Player p) {
		String r = readFromMemory(p.ID); 
		String prev = ""; Town old = null;
		if(r!=null&&r.contains(";")) {
			 prev = r.substring(0,r.indexOf(";"));
			 
			  old = God.findTown(prev,God.getPlayer(5));
			 
			  if(old!=null&&old.townID!=0) {
					 dropEventListener(this,"digFinish"); // so it resets...
					 
				 }

		}
		// so if sent doesn't equal true, I should, in general choose another, but I also need to see if the first twn is there.
			
			 
			// now we choose a random id town around their capital.
			
			Hashtable wm = p.getPs().b.getWorldMap();
			Hashtable[] towns = (Hashtable[]) wm.get("townHash");
			Town cap= God.getTown(p.getCapitaltid());
			Town t=null; Town possibleT;
			
			if(cap!=null&&cap.townID!=0)
			for(Hashtable h: towns) {
				
				
				int x = (Integer) h.get("x");
				int y = (Integer) h.get("y");
				
				if(((String) h.get("owner")).equals("Id")&&((Double) h.get("resEffects0"))!=0&&
						Math.sqrt(Math.pow(x-cap.getX(),2) + Math.pow(y-cap.getY(),2))<=7&&x>cap.getX()&&y>cap.getY()) {
					double rand = Math.random();
					if(rand>.5||t==null) {
						t = God.findTown((String) h.get("townName"),God.getPlayer(5));
					}
				}

				
				
			}
			if(t!=null) {
				writeToMemory(t.getTownName()+";",p.ID);
				t.addEventListener(this,"digFinish");
				int pid_to[] = {p.ID};
				//	public boolean sendSystemMessage(int pid_to[],String body, String subject, int original_subject_id) {
				// So to preserve the coordinate transforms...we always choose one with a greater x and y than the player's town.
				// 2 = -7 - -9 
				int sumX = sumFactorial((t.getX()-cap.getX())), sumY = sumFactorial((t.getY()-cap.getY()));
		//		System.out.println("Chose " + t.getTownName() + " and am using the factorials of " +(t.getX()-cap.getX()) + " and "+ (t.getY()-cap.getY()) + " with sums of " + sumX + " and " + sumY);

				getPs().b.sendSystemMessage(pid_to,""+sumX,""+sumY, 0);
				
				// So we give you the big factorial, and you have to work backwards to get the limit of the sum.
			}
			
		

		
		
	}
	
	public void onRaidSent(Raid r, boolean prog) {
		if(prog) {

	//	System.out.println("I WAS CALLED, BIATCCHH");
			String town = readFromMemory(r.getTown1().getPlayer().ID);
			if(town!=null&&town.contains(";")) {
				String townName = town.substring(0,town.indexOf(";"));
				//System.out.println("my town name is " + townName);

				Town t = God.findTown(townName,God.getPlayer(5));
				if(t!=null&&t.townID!=0) {
					
					if(r.getTown2().townID==t.townID&&r.getDigAmt()>0) {
					//	System.out.println("dig found motherfuckers.");
						r.getTown1().getPlayer().dropEventListener(this,"onProgramLoad");
						r.getTown1().getPlayer().dropEventListener(this,"onRaidSent"); // BUT NOT DIG FINISH, THEIR DIG IS STILL GOING!
	
						reward(r.getTown1().getPlayer().ID);
						writeToMemory(town+"done",r.getTown1().getPlayer().ID);
						r.getTown1().getPlayer().flicker=("CQ1");
					}
				}
			}
			
		}
	}
	
	public int sumFactorial(int limit) {
		 int prod = 1;
		   long factorial = 1;     // Calculate factorial in this variable
		  int prodsum = 0;
		    // Loop from 1 to the value of limit
		    for(int i = 1; i <= limit; i++)
		    {
		    	//System.out.println("i is " + i);
		      factorial = 1;       // Initialize factorial
		      int j =2;
		      while(j <= i) {
		        factorial *= j++;
		      }
		      //System.out.println("Factorial for that is "+ factorial);
		 
		     
		      prodsum = (int) (prodsum + factorial);
		     // System.out.println("Running sum is " +prodsum);
		 
		    }
		 
		     
		      
		      return prodsum;
	}
	public void digFinish(Town t, Player p) {
		//System.out.println("Dig finish called, prev timer was " + t.getProbTimer());
		t.setProbTimer((int) (t.getProbTimer()+4*24*3600/GodGenerator.gameClockFactor));	
		//System.out.println("Dig finish called, new timer is " + t.getProbTimer());

		t.dropEventListener(this,"digFinish");
		destroy(p);

	}
	
	@Override
	public boolean checkPlayer(Player p) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		String additional[]  = { "50 Knowledge Points" };
		Player p = God.getPlayer(pid);
	
		String toRet[]=new String[2];
		String mem = readFromMemory(p.ID);
		toRet[0] = getRewardBlock(7,pid,additional);
		if(mem!=null&&mem.contains("done")) 
			toRet[0] += "<br /><br /><b>Congratulations, you have successfully completed this challenge and received your reward! This Quest will auto-complete when the dig is finished.</b>";
				
			
		toRet[0] +="<br /><br />Warning:  <ul><li>This is a tough programming challenge. It is not for the faint of heart and the answers are not posted.</li><li>You may wish to review the UserMessage API.</li>" +
				"<li> If you are using Revelations 2.0 software, it's recommended that you use the public void onMessageReceived(UserMessage m) { } method for this quest. </li>" +
				"<li> If you are using Revelations 1.0 software, you cannot do this quest without the Messaging API. It is recommended you switch to Revelations 2.0 as soon as possible.</li><li>You will need the Attack Automation API and the Dig API to complete this challenge.</li></ul><br /><br />"+
			
				"Your mission, since you already accepted it, involves receiving an encoded message. <br /><br />This message will have a single number in the subject, and a single number in the body. Each of these numbers is an encoded number representing a coordinate on the map. The subject has the encoded y, and the body, the encoded x, of a town where a huge dig reward awaits. <br /><br /> " +
				" Every time you run your AI, the coordinates will be reselected and the message resent, and you will be unable to view this message via normal user interface. Only AIs can read this message, as it is a special type called a 'System Message'. You also must send the dig via your AI. Sending it manually will not count. <br /><br />" +
				"The algorithm to decode each number is the same. The formula below gets you through the first decryption, and it's the same for Y as it is for X:<br /><br />" +
				"1!+2!+3!+...decoded_number_X!=encoded_number_X<br /><br />" +
				"Once you have both your x encryption and y encryption decrypted, you're not done yet. These numbers represent relative distances from your capital. You can transform them into actual coordinates via this formula:<br /><br />" +
				"actual_coordinate_X=decoded_number_X+capitalX<br /><br />Once you've decoded your x and y, you must send a dig to that location. On sending the dig to the correct location, you will instantly be rewarded by this quest. However, you must complete the dig normally and be offered the dig prize message and accept it to get the treasure waiting there and actually finish the quest. Good luck!";
				
				
			
		toRet[1] = "No Hint.";
		return toRet;	
		
	}

	@Override
	public String getQuestDescription(int pid) {
		return "Weekly Challenge: Factorials";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerConstructor(Player p) {
		// TODO Auto-generated method stub
		p.addEventListener(this,"onProgramLoad");
		p.addEventListener(this,"onRaidSent");
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
		p.setKnowledge(p.getKnowledge()+50);
	}

}
