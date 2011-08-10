package BattlehardFunctions;

import java.util.UUID;
import java.sql.Timestamp;
/**
 * 
 * @author Chris Hall
 *
 */
public class UserDiplo {
	private String	type, 
					p1, 
					p2;
	private Timestamp created;
	private UUID ID;
	private boolean accepted = false;
	
	public UserDiplo(UUID ID, String type, String p1, String p2, Timestamp created, boolean accepted) {
		this.ID 		= ID;
		this.type		= type;
		this.p1			= p1;
		this.p2			= p2;
		this.created	= created;
		this.accepted	= accepted;
	}
	/**
	 * Gets the unique ID of this diplomatic arrangement.
	 * 
	 * @return This diplomatic arrangement's ID
	 */
	public UUID getID() {
		return ID;
	}
	
	/**
	 * Gets the type of arrangement this object refers to.
	 * <br/><br/>
	 * Currently, there are 6 types of diplomatic arrangements.
	 * <ul>
	 * <li>Peace Treaty</li>
	 * <li>Non-Aggression Pact</li>
	 * <li>Alliance</li>
	 * <li>Voluntary Vassalage</li>
	 * <li>Trade Embargo</li>
	 * <li>War</li>
	 * </ul>
	 * @return One of the above values.
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Gets the name of the originating player of the diplomatic arrangement.  This is the player that created this arrangement.
	 * @return The name of the originating player.
	 */
	public String getP1() {
		return p1;
	}
	
	/**
	 * Gets the name of the second player in the diplomatic arrangement.
	 * @return The name of the second player.
	 */
	public String getP2() {
		return p2;
	}
	
	/**
	 * Gets the Timestamp representing when this diplomatic arrangement started.
	 * 
	 * @return A Timestamp that represents when this diplomatic arrangement started.
	 */
	public Timestamp getCreated() {
		return created;
	}
}