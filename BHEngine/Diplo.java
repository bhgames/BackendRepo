package BHEngine;

import java.util.Date;
import java.util.UUID;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * The Diplo Class contains all the information about a diplomatic arrangement between two players.
 * These arrangements are then used everywhere to determine war and peace.
 * <br/><br/>
 * All the setter and updater methods for this class return the current instance of this class to allow for more flexible
 * instantiation of this class.
 * <br/><br/>
 * For example, using the following code I could load an arrangement from the database, modify its created date in
 * one line, and still save it to a variable:
 * 		<ul>Diplo d = new Diplo(ID,Con).setCreated(newTimestamp(new Date().getTime()));</ul>
 * 
 * @author Chris Hall
 *
 */
public class Diplo {
	/*
	 * TODO list
	 * 		Finish up all the methods needed by diplo.  This is mainly a holder class, so it doesn't need a lot of stuff
	 * 		Most of the work is done on the player level via isAllied and calculateDiplo
	 * 		These methods both need to be finished to check for alliances and pull down diplomatic arrangements.
	 * 		Write get and set methods for all player objects
	 */
	
	/**
	 * Contains the names of arrangement types in a way that makes them easily comparable
	 */
	public static enum Type {PeaceTreaty,NAP,Alliance,TradeEmbargo,War,VoluntaryVassalage};
	
	/**
	 * Stores the strength of the diplomatic arrangement.  This is used to compare arrangements to determine when an
	 * arrangement should cancel out another arrangement.
	 * <br/><br/>
	 * The values are as follows:
	 * <ol start='0'>
	 * <li>uninitialized</li>
	 * <li>propagated from an ally</li>
	 * <li>created directly with this player</li>
	 * <li>created by this player's league</li>
	 * <li>created by this player's lord</li>
	 * </ol>
	 * Higher value arrangements cancel out lower value arrangements.
	 */
	private int value = 0;
	private boolean accepted = false;
	private Type type;
	private UUID ID;
		//the two players this diplomatic arrangement is between
	private Player 	p1, //this is the player that created this diplomatic arrangement 
					p2; 
	private UberConnection con;
	private Timestamp created;
							
	/**
	 * Loads a diplomatic arrangement from the database.
	 * 
	 * @param ID	the ID of the diplomatic arrangement
	 * @param God	an instance of God for player lookups
	 */
	public Diplo(UUID ID, GodGenerator God) {
		this.ID = ID;
		con = God.con;
		try {
			/*
			 * col 1 - dipid
			 * col 2 - type
			 * col 3 - p1id
			 * col 4 - p2id
			 * col 5 - created
			 * col 6 - value
			 * col 7 - accepted
			 */
			UberPreparedStatement stmt = con.createStatement("select * from diplo where wher dipid = ?;");
			stmt.setString(1, ID.toString());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				type = Enum.valueOf(Type.class, rs.getString(2));
				p1 = God.getPlayer(rs.getInt(3));
				p2 = God.getPlayer(rs.getInt(4));
				created = rs.getTimestamp(5);
				value = rs.getInt(6);
				accepted = rs.getBoolean(7);
			}
			rs.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
	}
	
	/**
	 * Creates a new diplomatic arrangement and stores it in the database.
	 * 
	 * @param ID	the ID of this diplomatic arrangement
	 * @param type	the Type of arrangement
	 * @param p1	the player creating this arrangement
	 * @param p2	the second player this arrangement references
	 * @param value	the value of this arrangement
	 */
	public Diplo(UUID ID, Type type, Player p1, Player p2, int value) {
		this.ID 		= ID;
		this.type 		= type;
		this.p1			= p1;
		this.p2 		= p2;
		this.value	 	= value;
		created			= new Timestamp(new Date().getTime());
		con 			= p1.getCon();
		//both of these arrangements start out accepted
		if(type==Type.TradeEmbargo||type==Type.War) {
			accepted = true;
		}
		try {
			UberPreparedStatement stmt = con.createStatement("insert into diplo(dipid,type,p1id,p2id,created,value,accepted) values (?,?,?,?,?,?,?)");
			stmt.setString(1, ID.toString());
			stmt.setString(2, type.name());
			stmt.setInt(3,p1.getID());
			stmt.setInt(4,p2.getID());
			stmt.setLong(5, created.getTime());
			stmt.setInt(6, value);
			stmt.setBoolean(7, accepted);
			stmt.execute();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
	}
	/**
	 * Updates this diplomatic arrangement's database entry
	 * 
	 * @return The current diplomatic arrangement
	 */
	public Diplo update() {
		try {
			UberPreparedStatement stmt = con.createStatement("update diplo set type=?,p1id=?,created=?,value=?,accepted=? where dipid=?");
			stmt.setString(1, type.name());
			stmt.setInt(2, p1.getID());
			stmt.setInt(3,p2.getID());
			stmt.setLong(4, created.getTime());
			stmt.setInt(5, value);
			stmt.setBoolean(6, accepted);
			stmt.setString(7, ID.toString());
			stmt.execute();
			stmt.close();
		} catch(SQLException exc) { exc.printStackTrace(); }
		return this;
	}
	
	/**
	 * Cancels this diplomatic arrangement and removes it from the database
	 * 
	 * @return The now canceled diplomatic arrangement 
	 */
	public void cancel() {
		try {
			UberPreparedStatement stmt = con.createStatement("delete from diplo where dipid=?");
			stmt.setString(1, ID.toString());
			p1.getDiplo().remove(this);
			p2.getDiplo().remove(this);
		} catch(SQLException exc) { exc.printStackTrace();}
	}
	
	public Type getType() {
		return type;
	}
	
	public UUID getID() {
		return ID;
	}
	
	public Player getP1() {
		return p1;
	}
	public Player getP2() {
		return p2;
	}
	
	public Timestamp getCreated() {
		return created;
	}

	public int getValue() {
		return value;
	}
	
	public boolean isAccepted() {
		return accepted;
	}
	
	public boolean isSame(Diplo d) {
		return (type==d.getType()&&(p2.getID()==d.getP2().getID()||p1.getID()==d.getP2().getID()));
	}
	
	public Diplo setType(Type newType) {
		type = newType;
		return this;
	}
	
	public Diplo setP1(Player p) {
		p1 = p;
		return this;
	}
	
	public Diplo setP2(Player p) {
		p2 = p;
		return this;
	}
	
	public Diplo setCreated(Timestamp t) {
		created = t;
		return this;
	}
	
	public Diplo setValue(int val) {
		value = val;
		return this;
	}
	
	public Diplo setAccepted() {
		if(!accepted) {
			created = new Timestamp(new Date().getTime());
		}
		accepted = true;
		return this;
	}
}