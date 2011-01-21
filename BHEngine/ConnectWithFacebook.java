package BHEngine;

public class ConnectWithFacebook extends QuestListener {

	public ConnectWithFacebook(int ID, GodGenerator God) {
		super(ID, God);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPlayer(Player p) {
		if(p.getFuid()!=0) return false;
		else
		return true;
	}

	@Override
	public String[] getCurrentQuestText(int pid) {
		// TODO Auto-generated method stub
		String toRet[] = {"Prompt:<br /><br />" + 
					"Link your Facebook account with your A.I. Wars account and get:<br /><br />"
				+	"1. 5 hours of resource production given immediately upon linkage.<br /><br />"+
					"2. The ability to post Status Reports and gain extra resources off those Status Reports!<br /><br />"
				+   "3. The opportunity to gain free days of Autopilot membership!<br /><br />"
				+	"4. The ability to login with your Facebook account!<br /><br />"
				+	"Interested? Link your facebook account in the Account Settings, which can be found in the Menu!",

					"This is too easy for a hint. Grab those resources!"};
		return toRet;
	}

	@Override
	public String getQuestDescription(int pid) {
		
		return "Link your A.I. Wars account with your Facebook account for extra privileges and resources!";
	}

	@Override
	public void iterateQuest(int times, int pid) {
		Player p = findPlayer(pid);
		if(p.getFuid()!=0) {
			reward(pid);
			destroy(p);
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


	}

}
