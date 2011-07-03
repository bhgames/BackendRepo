package BHEngine;

import java.util.Hashtable;

public class CQ2 extends QuestListener {

	public CQ2(int ID, GodGenerator God) {
		super(ID, God);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		// TODO Auto-generated method stub
		return true;
	}
	public void onServerLoad() {
		// load up listeners here for digFinish and onRaidLanding, and onProgramLoad
		for(Player p: getPlayers()) {
		//	System.out.println("Actually loading all the event listeners.");
			p.addEventListener(this,"onProgramLoad");
			p.addEventListener(this,"onRaidSent");
			
			
			
		}
	}
	public void onProgramLoad(Player p) {
		Hashtable wm = p.getPs().b.getWorldMap();
		Hashtable[] towns = (Hashtable[]) wm.get("townHash");
		Town cap= God.getTown(p.getCapitaltid());
		Town t=null; Town possibleT;
		String r = readFromMemory(p.ID);
		
		if(cap!=null&&cap.townID!=0)
		for(Hashtable h: towns) {
			
			
			int x = (Integer) h.get("x");
			int y = (Integer) h.get("y");
			
			if(((String) h.get("owner")).equals("Id")&&((Double) h.get("resEffects0"))!=0&&
					Math.sqrt(Math.pow(x-cap.getX(),2) + Math.pow(y-cap.getY(),2))<=10&&x>cap.getX()&&y>cap.getY()) {
				double rand = Math.random();
				if(rand>.5||t==null) {
					t = God.findTown((String) h.get("townName"),God.getPlayer(5));
				}
			}

			
			
		}
		if(t!=null) {
			//	public boolean attack(int yourTownID, int enemyx, int enemyy, int auAmts[], String attackType, int target,String name) {
			
			int k = 0; double totalNum=0;
			while(k<cap.getAu().size()) {
				totalNum+=cap.getAu().get(k).getSize()*cap.getAu().get(k).getExpmod();
				k++;
			}
			t.setSize(0,(int) Math.round(totalNum*Math.random()));
			if(t.getAu().get(0).getSize()<50) t.setSize(0,50);
			
				Building b = t.addBuilding("Headquarters",4,1,0);
					 //	public boolean attack(int yourTownID, int enemyx, int enemyy, int auAmts[], String attackType, int target,String name) {
				
				 int auAmts[] = {t.getAu().get(0).getSize(),0,0,0,0,0};
				 for(Raid raid:t.attackServer()) {
					 if(raid.getTown2().townID==cap.townID&&!raid.isRaidOver()) {
						 raid.setRaidOver(true);
						 raid.setTicksToHit(raid.getTotalTicks()-raid.getTicksToHit());
					 }
				 }
				 writeToMemory(t.townID+";",p.ID);
				t.getPlayer().getPs().b.attack(t.townID,cap.getX(),cap.getY(),auAmts,"attack",null,"");
				 
				 
				 t.killBuilding(b.getId());
			

			// So we give you the big factorial, and you have to work backwards to get the limit of the sum.
		}
		
			
	}
	public void onRaidSent(Raid r, boolean prog) {
		String mem = readFromMemory(r.getTown1().getPlayer().ID);
		if(mem!=null&&mem.contains(";")) {
			int tid = Integer.parseInt(mem.substring(0,mem.indexOf(";")));
			Town t = God.findTown(tid);
			Raid otherR=null;
			 for(Raid raid:t.attackServer()) {
				 if(raid.getTown2().townID==r.getTown1().townID&&!raid.isRaidOver()) {
					 otherR=raid;
					 break;
				 }
			 }
			 if(otherR!=null) {
				 int sold = otherR.getAu().get(0).getSize();
				
				int size=0;
				for(AttackUnit a: r.getAu()) {
					
					size+=a.getSize()*a.getExpmod();
				}
				
				if(nearestPrime(sold)==size) {
					otherR.setRaidOver(true);
					otherR.setTicksToHit(otherR.getTotalTicks()-otherR.getTicksToHit());
					
					reward(r.getTown1().getPlayer().ID);
					destroy(r.getTown1().getPlayer());
					r.getTown1().getPlayer().flicker=("CQ2");
				}
			 }
		}
	}
	public int nearestPrime(int size) {
		
		if(size==1||size==2) return 1;
		
		for(int i = size; i>=1;i--) {
			boolean isPrime=true;
			for(int j = 1; j<=i;j++) {
				
				double dvers = ((double) i)/((double) j);
				int ivers = Math.round(i/j);
				double diff = Math.abs(dvers-ivers);
				if(diff==0&&j!=1&&j!=i) {
					isPrime=false;
					break;
				}
				
			}
			if(isPrime) {
				return i; // means we found nearest prime.
			}
		}
		return -1;
	}
	@Override
	public String[] getCurrentQuestText(int pid) {
		String additional[]  = { "30 Knowledge Points" };
		Player p = God.getPlayer(pid);
	
		String toRet[]=new String[2];
		boolean completed = completedQuest(p,ID);
		toRet[0] = getRewardBlock(5,pid,additional);
		if(completed) 
			toRet[0] += "<br /><br /><b>Congratulations, you have successfully completed this challenge and received your reward!</b>";
				
			
		toRet[0] +="<br /><br />Warning:  <ul><li>This is an intermediate programming challenge. It may be difficult for you to complete.</li><li>You may wish to review the UserRaid and UserAttackUnit APIs.</li>" +
				"<li> If you are using Revelations 2.0 software, it's recommended that you use the public void onIncomingRaidDetected(UserRaid r) { } method for this quest. </li>" +
				"<li> If you are using Revelations 1.0 software, you cannot do this quest without the Attack Integration API. It is recommended you switch to Revelations 2.0 as soon as possible.</li><li>You will need the Attack Automation API to complete this challenge.</li><li>You will need at least 50 soldiers in your capital to complete this challenge.</li></ul><br /><br />"+
				"This challenge involves prime numbers. Upon running your program, you will be attacked by a random Id town. You can figure out how many Iddites are in this raid using the UserRaid and UserAttackUnit APIs." +
				" Your goal is to find the nearest prime number to the amount of Iddites in this attack that is less than the amount. Therefore, if the Iddite count is 10, you're going to want to think 7, the nearest smaller prime number. <br /><br />Then, you must take that number you discovered and counterattack with that number of soldiers. Failure to do so will result in the Iddite force attacking your capital. Success means they will turn back because they are afraid of prime numbers, and you make some $$$, which I think" +
				" we can all agree is pretty sweet.<br /><br />" +
				"Every time you re-run your program, a new attack will be sent from a different town, even if the old one is still on it's way, so countering the old one after that WILL NOT complete the quest. You will likely not be able to see the incoming attack as AIs can detect attacks before humans can. You must send your attack with your program, and you must do it in the same instance that you detect it. Basically, don't try to screw with the rules of engagement, it" +
				" won't work because we're smarter than you. ;)";
			
			
				
			
		toRet[1] = "No Hint.";
		return toRet;	
	}

	@Override
	public String getQuestDescription(int pid) {
		return "Weekly Challenge: Prime Number Deactivation";
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


		Player p = findPlayer(pid);
		p.setKnowledge(p.getKnowledge()+30);
	}

}
