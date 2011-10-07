package BHEngine;
//import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;

public class TradeSchedule {
	public UUID id;
	public boolean threadSafe=true;
	
	TradeSchedule mate;  
	
	private long metal, 
				 timber, 
				 manmat, 
				 food,
				 othermetal, 
				 othertimber, 
				 othermanmat, 
				 otherfood;
	private Town town2,
				 town1;
	private boolean twoway=false,
					agreed=false,
					finished = false,
					stockMarketTrade=false,
					caravan=false;
	private int currTicks = 0,
				timesDone=0,
				timesToDo=1,
				intervaltime=3600; 
	private UUID mateID;
	private UberConnection con; 
	private GodGenerator God; 
	// they happen to be handled at the same time.
	
	public void setTradeScheduleValues(UUID id, Town town1, Town town2, long metal, 
			long timber, long manmat, long food, long othermetal,long othertimber, 
			long othermanmat, long otherfood, int currTicks, int timesDone,boolean twoway,
			boolean agreed, int intervaltime, int timesToDo, UUID mateTradeScheduleID,
			boolean caravan) {
		
		this.mateID = mateTradeScheduleID; // if two way and not agreed, will be null until agreed, or else
		// if not two way is null forever! (mysql autogen id starts at 1)
		
		this.twoway=twoway;
		this.timesDone=timesDone;
		this.agreed=agreed;this.intervaltime=intervaltime;this.timesToDo=timesToDo;
		this.town2=town2;this.town1 = town1;
		if(town2!=null&&town2.townID==town1.townID) stockMarketTrade=true; // so we know it's a sm trade!!!
		this.id = id; this.metal=metal;this.timber=timber;
		this.manmat=manmat;this.food=food;
		this.othermetal=othermetal;this.othertimber=othertimber;
		this.othermanmat=othermanmat;this.otherfood=otherfood;
		this.currTicks=currTicks;
		this.caravan=caravan;
		
	}
	public TradeSchedule(UUID id,GodGenerator God) {
			
		this.id = id; 
		this.con=God.con; this.God=God;
			 
		try {	
			UberPreparedStatement rus = con.createStatement("select * from tradeschedule where id = ?;");
			rus.setString(1,id.toString());
			ResultSet rrs = rus.executeQuery();

			while(rrs.next()) {
				
				
				Town town1 = God.findTown(rrs.getInt(1));
				Town town2Obj = God.findTown(rrs.getInt(2));

				setTradeScheduleValues(id,town1,town2Obj,rrs.getLong(8),
						 rrs.getLong(9),rrs.getLong(10),rrs.getLong(11),rrs.getLong(14), rrs.getLong(15), rrs.getLong(16), rrs.getLong(17), rrs.getInt(13),rrs.getInt(12),rrs.getBoolean(6),rrs.getBoolean(7),rrs.getInt(4),rrs.getInt(5),id,rrs.getBoolean(21)); // this one has no sql addition!
				
		
			}
			rrs.close();rus.close();
					
		} catch(SQLException exc) { exc.printStackTrace(); }
			 
		
	}
	synchronized public void synchronize() {
		 this.con=God.con;
		try {	
		UberPreparedStatement rus = con.createStatement("select * from tradeschedule where tsid = ?;");
		rus.setString(1,id.toString());
		ResultSet rrs = rus.executeQuery();
		while(rrs.next()) {
	
			
			Town town1 = God.findTown(rrs.getInt(1));
			Town town2Obj = God.findTown(rrs.getInt(2));

			setTradeScheduleValues(id,town1,town2Obj,rrs.getLong(8),
					 rrs.getLong(9),rrs.getLong(10),rrs.getLong(11),rrs.getLong(14), rrs.getLong(15), rrs.getLong(16), rrs.getLong(17), rrs.getInt(13),rrs.getInt(12),rrs.getBoolean(6),rrs.getBoolean(7),rrs.getInt(4),rrs.getInt(5),UUID.fromString(rrs.getString(19)),rrs.getBoolean(21)); // this one has no sql addition!
			
	
		}
				rrs.close();rus.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		
	}
	public void deleteMe() {
		// if you want to keep finished trades,
		// remove the deleteMe for later. Depends on what you're tryin' to do.
		setFinished(true);
		save();
		getTown1().tradeSchedules().remove(this);
		
	}
	
	synchronized public void save() {
		try {
		UberPreparedStatement stmt = con.createStatement("update tradeschedule set  m = ?, t = ?, mm = ?, f = ?, tid1 = ?, tid2 = ?, intervaltime = ?, times = ?, twoway = ?, agreed = ?, timesdone = ?, currticks = ?, otherm = ?, othert = ?, othermm = ?, otherf = ?, finished =  ?, mate_tsid = ?,caravan = ? where tsid = ?;");
			int tid2 = 0;
			  if(getTown2()!=null) tid2 = getTown2().townID;

			 stmt.setLong(1,metal);
			 stmt.setLong(2,timber);
			 stmt.setLong(3,manmat);
			 stmt.setLong(4,food);
			 stmt.setInt(5,getTown1().townID);
			 stmt.setInt(6,tid2);
			 stmt.setInt(7,intervaltime);
			 stmt.setInt(8,timesToDo);
			 stmt.setBoolean(9,twoway);
			 stmt.setBoolean(10,agreed);
			 stmt.setInt(11,timesDone);
			 stmt.setInt(12,currTicks);
			 stmt.setLong(13,othermetal);
			 stmt.setLong(14,othertimber);
			 stmt.setLong(15,othermanmat);
			 stmt.setLong(16,otherfood);
			 stmt.setBoolean(17,finished);
			 if(mateID!=null)
			 stmt.setString(18,mateID.toString());
			 else stmt.setString(18,"none");
			 stmt.setBoolean(19,caravan);

			 stmt.setString(20,id.toString());
		  
		  stmt.executeUpdate();
		  stmt.close();
		  
		} catch(SQLException exc) { exc.printStackTrace(); }
		  
		  
	}
	/**
	 * Only way to safely delete a Trade Schedule is to set it's timesDone = timesToDo and returns all trades.
	 * Otherwise you could delete it with trades out.
	 */
	public void deleteMeInterrupt() {
		if(isTwoway()&&!isAgreed()) deleteMe(); // Twoways not agreed can delete no prob.
		
		setTimesToDo(getTimesDone());//don't switch values, won't delete then!
		int i = 0; Trade t; Town t1 = getTown1();
		ArrayList<Trade> tres = t1.tradeServer();
		while(i<tres.size()) {
			t = tres.get(i);
			if(t.getTs().id.equals(id)) 
				t.setTicksToHit(t.getTotalTicks()-t.getTicksToHit());
				t.setTradeOver(true);
			i++;
		}
		if(isTwoway()&&isAgreed()&&getMate().getTimesDone()!=getMate().getTimesToDo()) { 
			// last part is to make sure the first one who deletes, deletes the second, 
			// but then the second doesn't try to delete the first again, causing
			// a stack overflow error.
			getMate().deleteMeInterrupt();
		}
	}
	public TradeSchedule getMate() {
		
		// THIS WILL RETURN NULL IF THIS TS DOES NOT HAVE A MATE!!!
		/*
		 * The reason we only search out the mate once
		 * and never again if we have it is so we can save
		 * processing time. In certain instances when a schedule existed
		 * before a new server load, when it loads up it can't necessarily
		 * find it's mate even with it's id because that mate may not be loaded yet,
		 * so what happens is the first time it looks for it's mate(ie first getMate() call)
		 * it'll find it and save it. 
		 * 
		 * Also getMate serves to distinguish between threadSafe and non threadSafe
		 * two ways, the threadSafe one being the one of the two that calls getMate()
		 * first. The reason we do this is because what if both twoways hit 0 at
		 * the same time - without notifying one another, they each create a pair
		 * of trades. So making it so one is simply like a eunich and can't make trades
		 * but just count down like a dummy, waiting for the other, annihilates this
		 * possibility.
		 * 
		 * The suggestion of just having them both make single trades is impossible
		 * also because subtle differences in processor choices, town sizes, and trade schedule
		 * sizes of the separate player threads will cause a discrepancy
		 * in timers over long swathes of time, and I do not believe in making
		 * a correction function - because you don't know which ts is correct if they're
		 * five second apart due to one being checked every 1.0001 s and the other every 1 s,
		 * so I just have elected to have one make both trades. If they did both make
		 * their own trades like oneway trades, then the time discrepancy would
		 * allow one user to cancel their tradeschedule just after the other had sent
		 * theirs and screw him. There'd be no way for me to check for sure
		 * when I sent the first trade that the second one would STILL be there a few
		 * seconds later - I can't see far into the future, and with players'
		 * ability to script down to the second, I don't see this leak as something
		 * I want to keep around.
		 * 
		 * Also a tradeschedule not being thread safe does not mean it doesn't create
		 * trade objects - it means that it cannot call it's own makeTrade method and
		 * cannot go through the testing process in sendTradeIfPossible,
		 * the other one has to make that call in the sendTradeIfPossible's mate trade areas.(the
		 * areas denoted by (twoway&&agreed).
		 * 
		 */
		if(mate==null) {
		int i = 0; //TradeSchedule ts=null;
		if(isStockMarketTrade()) return null;
		//boolean found = true;
		 if(!isAgreed()||!isTwoway()) return null; // duh there won't be one.
		 
		 Town t2 = getTown2();
		 ArrayList<TradeSchedule> tses = t2.tradeSchedules();
		 while(i<tses.size()) {
			 if(tses.get(i).id.equals(mateID)) {
				 mate = tses.get(i);
				 break;
			 }
			 i++;
		 }
		 if(threadSafe&&mate.threadSafe) mate.threadSafe = false; // auto sets one to be unsafe
		 // if both are set to safe.
		 mate.mate=this;
		 
		} 
		return mate;
		 
	}
	public boolean hasAMate() {
		if(isStockMarketTrade()) return true; // no town for smts.
		int i = 0; TradeSchedule ts;
		Town t2 = getTown2(); Town t1 = getTown1(); Town othert1,othert2;
		ArrayList<TradeSchedule> tses = t2.tradeSchedules();
		while(i<tses.size()) {
			ts = tses.get(i);
			othert1 = ts.getTown1();
			othert2 = ts.getTown2();
		if(othert1.townID==t2.townID&&othert2.townID==t1.townID&&ts.getMetal()==getOthermetal()&&ts.getTimber()==getOthertimber()&&ts.getManmat()==getOthermanmat()&&ts.getFood()==getOtherfood()&&
				ts.getOthermetal()==getMetal()&&ts.getOthertimber()==getTimber()&&ts.getOthermanmat()==getManmat()&&ts.getOtherfood()==getFood()
				&&getIntervaltime()==ts.getIntervaltime()&&getTimesToDo()==ts.getTimesToDo()&&ts.isTwoway()==isTwoway()&&getTimesDone()==ts.getTimesDone()) return true;
		i++;

		}
		
		return false;
	}
	public TradeSchedule(Town town1, Town town2, long m, long t, long mm, long f, long othermetal,long othertimber, long othermanmat, long otherfood, int intervaltime,int timesToDo, boolean twoway,UUID mateTradeScheduleID, boolean caravan) {
		// Can't do an infinite number of arguments here so need to add manually.
		// holds distance and ticksToHit in this object.
		this.con=town1.getPlayer().God.con; 
		this.God=town1.getPlayer().God;
		/*
		boolean smtrade=false;
		if(town2!=null&&town2.townID==town1.townID) {
			smtrade=true;
		}
		 */
		this.twoway=twoway;
		intervaltime=(int) Math.round(intervaltime/GodGenerator.gameClockFactor);
		this.town2=town2;
		this.town1 = town1; currTicks=intervaltime;
		this.caravan=caravan;
		this.timesToDo=timesToDo;
		metal=m;timber=t;manmat=mm;food=f;
		
		if(town2!=null&&town2.townID==town1.townID) {
			stockMarketTrade=true;
		}
		
		this.othermetal=othermetal;
		this.othertimber=othertimber;
		this.intervaltime=intervaltime;
		this.othermanmat=othermanmat;
		this.otherfood=otherfood;

				 
		UberPreparedStatement stmt;
		try {
  
			stmt = con.createStatement("insert into tradeschedule (tid1, tid2,m,t,mm,f,otherm,othert,othermm,otherf,intervaltime,times,twoway,mate_tsid,id,caravan) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");

		      
			// First things first. We update the player table.
			boolean transacted=false;
			while(!transacted) {
				try {

					int tid2 = 0; if(town2!=null) tid2=town2.townID;
					stmt.setInt(1,town1.townID);
					stmt.setInt(2,tid2);
					stmt.setLong(3,m);
					stmt.setLong(4,t);
					stmt.setLong(5,mm);
					stmt.setLong(6,f);
					stmt.setLong(7,othermetal);
					stmt.setLong(8,othertimber);
					stmt.setLong(9,othermanmat);
					stmt.setLong(10,otherfood);
					stmt.setInt(11,intervaltime);
					stmt.setInt(12,timesToDo);
					stmt.setBoolean(13,twoway);
					if(mateID==null)
						stmt.setString(14,"none");
					else
						stmt.setString(14,mateID.toString());
					id = UUID.randomUUID();
					stmt.setString(15,id.toString());
					stmt.setBoolean(16,caravan);
					town1.tradeSchedules().add(this); // before we execute, or else an exc may kill it and this could be a test.
					// let's add this raid and therefore get the rid out of it.
					stmt.executeUpdate();
		    

			  /*
		      ArrayList<TradeSchedule> a  = town1.tradeSchedules();
		      while(a.size()<=0&&timesTried<10) {
			  Thread.currentThread().sleep(10);
		      a= town1.tradeSchedules();
		      timesTried++;
		      
		      }		     
		      tradeScheduleID=a.get(a.size()-1).tradeScheduleID;*/
					stmt.close(); transacted=true;  }
				
				catch(MySQLTransactionRollbackException exc) { } 
			}
		} catch(SQLException exc) { exc.printStackTrace(); }
	}
	
	public void makeTrade(boolean second, int traders) {
		//public Trade(Town town1, Town town2, int m, int t, int mm, int f, TradeSchedule ts) {
		// don't need to worry about threadSafety here due to it being checked in sendTradeIfPossible method in God.
		// Even nonsafe trade schedules can make trades, they just have to have their makeTrade method called by the safe one's
		//getMate() return.
		//Trade t;
		if(!isCaravan()) 
			new Trade(getTown1(),getTown2(),getMetal(),getTimber(),getManmat(),getFood(),this,traders);
		else {
			long theResource = 25+1*getTimesDone();
			if(theResource>300) theResource=300;
			new Trade(getTown1(),getTown2(),theResource,theResource,theResource,0,this,traders);
		}

		// makes this bitch.
		if(!second)
			setCurrTicks(getIntervaltime());
		else setCurrTicks(getIntervaltime()+1); // because in this case,
		// this is the second trade in a two-way and so it gets decreased by one
		// in the same iteration as the first was sent but it gets hijacked before that.
		// essentially means that it got sent at currTicks=1 by it's own clock,
		// and then it gets to start a tick earlier - so when we reset to intervaltime,
		// before this iteration is up, it'll get subtracted by one even though it shouldn't
		// since we processed it with it's mate. So we add one to counter that!
		setTimesDone(getTimesDone() + 1);
		
	}
	public void resetTradeTimers() {
		// This method is called in the event that this schedule cannot complete
		// at this time for some reason.
		
		if(!isTwoway())
		setCurrTicks(getIntervaltime());
		
		if(isTwoway()&&isThreadSafe()) { // if you're the threadsafe trade, you make the changes.
			setCurrTicks(getIntervaltime());
			TradeSchedule other = getMate();
			if(other!=null) { 
				other.setCurrTicks(getIntervaltime()+1);
			}
			
		}
	}
	public void completeTradeSetUp(Town town2){
		/*
		 * The reason we only search out the mate once
		 * and never again if we have it is so we can save
		 * processing time. In certain instances when a schedule existed
		 * before a new server load, when it loads up it can't necessarily
		 * find it's mate even with it's id because that mate may not be laoded yet,
		 * so what happens is the first time it looks for it's mate(ie first getMate() call)
		 * it'll find it and save it. When a trade is set up within a same server session,
		 * the mate is found the second it's made! cool, eh?
		 */
		this.town2=town2;
		
		TradeSchedule ts =
			new TradeSchedule(getTown2(),  getTown1(), getOthermetal(),  getOthertimber(),  getOthermanmat(),  getOtherfood(),
					 getMetal(), getTimber(),  getManmat(),  getFood(),  getIntervaltime(), getTimesToDo(),  true,id,caravan);
		ts.setIntervaltime(getIntervaltime()); // Because when you create a new schedule, it goes by a shorter interval, because it divides
		ts.setCurrTicks(getCurrTicks()); // Because when you create a new schedule, it goes by a shorter interval, because it divides

		// by 10 twice.
		mateID=ts.id;
		ts.mateID=id;
		ts.mate = this;
		mate=ts;
		ts.setAgreed(true);
		ts.setThreadSafe(false); // creating the mate that does not do the trading.
		setAgreed(true);
		ts.save();
		save(); // May as well save.
		
		
	}
	public Town getTown1() {
		if(town1==null)
		town1= God.getTown(getInt("tid1"));
		
		return town1;
	}
	public Town getTown2() {
		if(town2==null)
			town2= God.getTown(getInt("tid2"));
			
			return town2;
	}
	public long getMetal() {
		return metal;
	}
	public void setMetal(long metal) {
		this.metal = metal;
	}
	public long getTimber() {
		return timber;
	}
	public void setTimber(long timber) {
		this.timber = timber;
	}
	public long getManmat() {
		return manmat;
	}
	public void setManmat(long manmat) {
		this.manmat = manmat;
	}
	public long getFood() {
		return food;
	}
	public void setFood(long food) {
		this.food = food;
	}
	public long getOthermetal() {
		return othermetal;
	}
	public void setOthermetal(long othermetal) {
		this.othermetal = othermetal;
	}
	public long getOthertimber() {
		return othertimber;
	}
	public void setOthertimber(long othertimber) {
		this.othertimber = othertimber;
	}
	public long getOthermanmat() {
		return othermanmat;
	}
	public void setOthermanmat(long othermanmat) {
		this.othermanmat = othermanmat;
	}
	public long getOtherfood() {
		return otherfood;
	}
	public void setOtherfood(long otherfood) {
		this.otherfood = otherfood;
	}
	public boolean isTwoway() {
		return twoway;
	}
	public void setTwoway(boolean twoway) {
		this.twoway = twoway;
	}
	public int getCurrTicks() {
		return currTicks;
	}
	public void setCurrTicks(int currTicks) {
		this.currTicks = currTicks;
	}
	public int getTimesDone() {
		return timesDone;
	}
	public void setTimesDone(int timesDone) {
		this.timesDone = timesDone;
	}
	public int getTimesToDo() {
		return timesToDo;
	}
	public void setTimesToDo(int timesToDo) {
		this.timesToDo = timesToDo;
	}
	public UUID getMateID() {
		return mateID;
	}
	public void setMateID(UUID id) {
		this.mateID = id;
	}
	public int getIntervaltime() {
		return intervaltime;
	}
	public void setIntervaltime(int intervaltime) {
		this.intervaltime = intervaltime;
	}
	public boolean isAgreed() {
		return agreed;
	}
	public void setAgreed(boolean agreed) {
		this.agreed = agreed;
	}
	public boolean isFinished() {
		return finished;
	}
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	public boolean isStockMarketTrade() {
		return stockMarketTrade;
	}
	public void setStockMarketTrade(boolean stockMarketTrade) {
		this.stockMarketTrade = stockMarketTrade;
	}
	public void setTown2(Town town2) {
		this.town2 = town2;
	}
	public void setTown1(Town town1) {
		this.town1 = town1;
	}
	public void setMemAgreed(boolean agreed) {
		setBoolean("agreed",agreed);
	}
	public boolean isMemAgreed() {
		return getBoolean("agreed");
	}
	public void setMemCurrTicks(int currTicks) {
		setInt("currticks",currTicks);
	}
	public int getMemCurrTicks() {
		return getInt("currticks");
	}
	public void setMemFinished(boolean finished) {
		setBoolean("finished",finished);
	}
	public boolean isMemFinished() {
		return getBoolean("finished");
	}
	public void setMemFood(long food) {
		setLong("f",food);
	}

	public long getMemFood() {
		return getLong("f");
	}

	public void setMemIntervaltime(int intervaltime) {
		setInt("intervaltime",intervaltime);
	}
	public int getMemIntervaltime() {
		return getInt("intervaltime");
	}
	public void setMemManmat(long manmat) {
		setLong("mm",manmat);

	}

	public long getMemManmat() {
		return getLong("mm");
	}

	public void setMemMetal(long metal) {
		setLong("m",metal);
	}

	public long getMemMetal() {
		return getLong("m");
	}
	public void setMemOtherfood(long otherfood) {
		setLong("otherf",otherfood);
	}
	public long getMemOtherfood() {
		return getLong("otherf");
	}
	public void setMemOthermanmat(long othermanmat) {
		setLong("othermm",othermanmat);
	}
	public long getMemOthermanmat() {
		return getLong("othermm");
	}
	public void setMemOthermetal(long othermetal) {
		setLong("otherm",othermetal);
	}
	public long getMemOthermetal() {
		return getLong("otherm");
	}
	public void setMemOthertimber(long othertimber) {
		setLong("othert",othertimber);
	}
	public long getMemOthertimber() {
		return getLong("othert");
	}
	
	public boolean isMemStockMarketTrade() {
		if(getInt("tid1")==getInt("tid2")) return true;
		else return false;	
		}
	public void setMemTimber(long timber) {
		setLong("t",timber);
	}
	public long getMemTimber() {
		return getLong("t");
	}
	public void setMemTimesDone(int timesDone) {
		setInt("timesdone",timesDone);
	}
	public int getMemTimesDone() {
		return getInt("timesdone");
	}
	public void setMemTimesToDo(int timesToDo) {
		setInt("times",timesToDo);
	}
	public int getMemTimesToDo() {
		return getInt("times");
	}
	public void setMemTwoway(boolean twoway) {
		setBoolean("twoway",twoway);
	}
	public boolean isMemTwoway() {
		return getBoolean("twoway");
	}
	public void setMemMateTradeScheduleID(int mateTradeScheduleID) {
		setInt("mate_tsid",mateTradeScheduleID);
	}
	public int getMemMateTradeScheduleID() {
		return getInt("mate_tsid");
	}
	public void setThreadSafe(boolean threadSafe) {
		this.threadSafe = threadSafe;
	}
	public boolean isThreadSafe() {
		return threadSafe;
	}
	
	public void setInt(String fieldName, int toSet) {
		try {
			UberPreparedStatement stmt = con.createStatement("update tradeschedule set " + fieldName + " = ? where tsid = ?;");
			stmt.setInt(1,toSet);
			stmt.setString(2,id.toString());	
			stmt.execute();

			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setDouble(String fieldName, double toSet) {
		try {
			UberPreparedStatement stmt = con.createStatement("update tradeschedule set " + fieldName + " = ? where tsid = ?;");
			stmt.setDouble(1,toSet);
			stmt.setString(2,id.toString());	
			stmt.execute();

			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setLong(String fieldName, long toSet) {
		try {
			UberPreparedStatement stmt = con.createStatement("update tradeschedule set " + fieldName + " = ? where tsid = ?;");
			stmt.setLong(1,toSet);
			stmt.setString(2,id.toString());	
			stmt.execute();

			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setBoolean(String fieldName, boolean toSet) {
		try {
			UberPreparedStatement stmt = con.createStatement("update tradeschedule set " + fieldName + " = ? where tsid = ?;");
			stmt.setBoolean(1,toSet);
			stmt.setString(2,id.toString());	
			stmt.execute();

			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public void setString(String fieldName, String toSet) {
		try {
			UberPreparedStatement stmt = con.createStatement("update tradeschedule set " + fieldName + " = ? where tsid = ?;");
			stmt.setString(1,toSet);
			stmt.setString(2,id.toString());	
			stmt.execute();

			stmt.close();
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
	}
	public int getInt(String toGet) {
		try {
			UberPreparedStatement stmt = con.createStatement("select " + toGet + " from tradeschedule where tsid = ?;");
			stmt.setString(1,id.toString());	
			ResultSet rs = stmt.executeQuery();
			rs.next();
			int toRet=rs.getInt(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return -1;
	}
	
	
	public double getDouble(String toGet) {
		try {
			UberPreparedStatement stmt = con.createStatement("select " + toGet + " from tradeschedule where tsid = ?;");
			stmt.setString(1,id.toString());	
			ResultSet rs = stmt.executeQuery();
			rs.next();
			double toRet=rs.getDouble(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return -1;
	}
	
	public long getLong(String toGet) {
		try {
			UberPreparedStatement stmt = con.createStatement("select " + toGet + " from tradeschedule where tsid = ?;");
			stmt.setString(1,id.toString());	
			ResultSet rs = stmt.executeQuery();
			rs.next();
			long toRet=rs.getLong(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return -1;
	}
	
	public boolean getBoolean(String toGet) {
		try {
			UberPreparedStatement stmt = con.createStatement("select " + toGet + " from tradeschedule where tsid = ?;");
			stmt.setString(1,id.toString());	
			ResultSet rs = stmt.executeQuery();
			rs.next();
			boolean toRet=rs.getBoolean(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return false;
	}
	public String getString(String toGet) {
		try {
			UberPreparedStatement stmt = con.createStatement("select " + toGet + " from tradeschedule where tsid = ?;");
			stmt.setString(1,id.toString());	
			ResultSet rs = stmt.executeQuery();
			rs.next();
			String toRet=rs.getString(1);
			rs.close();
			stmt.close();
			return toRet;
		}catch(SQLException exc) {
			exc.printStackTrace();
		}
		return null;
	}
	public void setCaravan(boolean caravan) {
		this.caravan = caravan;
	}
	public boolean isCaravan() {
		return caravan;
	}
}
