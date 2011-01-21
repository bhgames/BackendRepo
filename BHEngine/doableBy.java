package BHEngine;

public class doableBy {
	// serves as both invadable by and viewable by.
	int iid;
	public int tid;
	public int pid;
	public int type; //0 is invadable by, 1 is viewable by.
	
	public doableBy(int iid, int pid, int tid, int type) {
		this.iid = iid;
		this.pid = pid;
		this.tid = tid;
		this.type = type;
	}
	
}