package BattlehardFunctions;

import java.util.UUID;
import java.sql.Timestamp;
/**
 *  User wrapper for the Diplo Class.
 *  <br/><br/>
 *  Contains all the relevant information on a user's Diplomatic arrangements.  These
 *  arrangements can prevent users from taking certain actions depending on the arrangement
 *  <br/><br/>
 *  Valid types are:
 *  <dl>
 *  <dt>Peace Treaty</dt>
 *  	<dd>Lasts 1 month, or until canceled.<br/>
 *			Cancels War.
 *			<br/><br/>
 *			Players with a Peace Treaty cannot attack each other directly. This only affects the towns of the players with the arrangement. If encountered elsewhere, combat still takes place.
 *		</dd>
 *  <dt>Non-Aggression Pact (NAP)</dt>
 *  	<dd>Lasts until canceled.<br/>
 *			Cancels War.
 *			<br/><br/>
 *			Behaves exactly like a Peace Treaty.
 *		</dd>
 *  <dt>Alliance</dt>
 *  	<dd>Lasts until canceled.<br/>
 *			Cancels War.
 *			<br/><br/>
 *			Players with an Alliance cannot enter into combat with each other. The only 
 *			exception is if one player is supporting a player the other doesn't have an 
 *			Alliance with. If a member of an Alliance declares War or enacts a Trade 
 *			Embargo, all other members of the Alliance do the same. If this arrangement is 
 *			later canceled, all players in the alliance also cancel. These events can 
 *			propagate through multiple alliances.
 *		</dd>
 *  <dt>Voluntary Vassalage</dt>
 *  <dt>Trade Embargo</dt>
 *  <dt>War</dt>
 *  </dl>
 *  
 * @author Chris "Markus" Hall
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
	
	public boolean isAccepted() {
		return accepted;
	}
}