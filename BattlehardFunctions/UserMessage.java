package BattlehardFunctions;

import java.util.UUID;

public class UserMessage {
	/*
	 * +---------------------+------------------+------+-----+-------------------+----------------+
| Field               | Type             | Null | Key | Default           | Extra          |
+---------------------+------------------+------+-----+-------------------+----------------+
| message_id          | int(11)          | NO   | PRI | NULL              | auto_increment |
| pid_to              | int(10) unsigned | NO   | MUL | NULL              |                |
| pid_from            | int(10) unsigned | NO   | MUL | NULL              |                |
| body                | varchar(8000)    | YES  |     |                   |                |
| subject             | varchar(100)     | YES  |     | No subject        |                |
| msg_type            | int(11)          | YES  |     | 0                 |                |
| readed              | tinyint(1)       | YES  |     | 0                 |                |
| deleted             | tinyint(1)       | YES  |     | 0                 |                |
| tsid                | int(10) unsigned | YES  |     | NULL              |                |
| original_message_id | int(10) unsigned | NO   |     | NULL              |                |
| creation_date       | timestamp        | NO   |     | CURRENT_TIMESTAMP |                |
+---------------------+------------------+------+-----+-------------------+----------------+

	 */
	
	UUID id,
		 originalSubjectID,
		 subjectID;
	int pidFrom,
		msgType,
		tsid=-1;
	int[] pidTo;
	String body,
		   subject,
		   creationDate,
		   usernameFrom;
	String[] usernameTo;
	boolean readed,
			deleted;

	public UserMessage(UUID messageID,int pidTo[], int pidFrom,String usernameTo[], String usernameFrom, String body, String subject, int msgType, boolean readed, int tsid, UUID originalSubjectID, String creationDate, UUID subjectID,boolean deleted) {
		this.id=messageID;this.pidTo=pidTo;this.pidFrom=pidFrom;this.body=body;this.subject=subject;this.msgType=msgType;
		this.readed=readed;
		
		this.tsid=tsid;
		this.deleted=deleted;
		this.originalSubjectID=originalSubjectID;this.creationDate=creationDate;
		this.subjectID=subjectID;
		this.usernameTo=usernameTo;this.usernameFrom=usernameFrom;
	}
	
	/**
	 * 
	 * @return This message's UUID
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * 
	 * @return the IDs of the players this message is addressed to
	 */
	public int[] getPidTo() {
		return pidTo;
	}

	/**
	 * 
	 * @return the Id of the player that sent this message
	 */
	public int getPidFrom() {
		return pidFrom;
	}

	/**
	 * 
	 * @return the full body text of this message
	 */
	public String getBody() {
		return body;
	}

	/**
	 * 
	 * @return the subject line of this message
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * Gets the integer representation of this message's type.
	 * <br/>  
	 * Valid types include:
	 * <ol start='0'>
	 * <li>normal</li>
	 * <li>trade invite</li>
	 * <li>trade accept</li>
	 * <li>league invite</li>
	 * <li>league accept</li>
	 * <li>system message (meaning only visible to AIs)</li>
	 * <li>vassal invite</li>
	 * @return the integer representation of this message's type
	 */
	public int getMsgType() {
		return msgType;
	}

	/**
	 * 
	 * @return true, if this message has been read.  False, otherwise.
	 */
	public boolean isReaded() {
		return readed;
	}

	/**
	 * Used with trade messages to keep track of the trade the message is for.
	 * 
	 * @return the ID of the trade this message was sent for
	 */
	public int getTsid() {

		return tsid;
	}

	/**
	 * The OriginalSubjectID is used to group messages together.  
	 * Messages that are not replies to other messages have this set to 0.
	 *  
	 * @return the SubjectID of the message this is in reply to.
	 */
	public UUID getOriginalSubjectID() {
		return originalSubjectID;
	}

	/**
	 * SubjectIDs are used to group messages together.
	 * All messages have a unique SubjectID.
	 * 
	 * @return this message's SubjectID
	 */
	public UUID getSubjectID() {
		return subjectID;
	}
	
	/**
	 * 
	 * @return the string representation of this messages creation date
	 */
	public String getCreationDate() {
		return creationDate;
	}
	
	/**
	 * 
	 * @return the username of the player that sent this message
	 */
	public String getUsernameFrom() {
		return usernameFrom;
	}
	
	/**
	 * 
	 * @return an array of usernames this message is addressed to
	 */
	public String[] getUsernameTo() {
		return usernameTo;
	}
	
	/**
	 * 
	 * @return true, if the message has been deleted.  False, otherwise.
	 */
	public boolean getDeleted() { 
		return deleted;
	}
	
	/**
	 * Sets a message as being read or unread.
	 * 
	 * @param read the state to set this message's read status to
	 */
	public void setReaded(boolean read) {
		readed=read;
	}
	
	/**
	 * 
	 * @param deleted the state to set this message's deleted status to
	 */
	public void setDeleted(boolean deleted) {
		this.deleted=deleted;
	}
	
	/*
	public void addPidTo(int toAdd) {
		int newPid[] = new int[pidTo.length+1];
		int i = 0;
		while(i<pidTo.length) {
			newPid[i]=pidTo[i];
			i++;
		}
		newPid[i]=toAdd;
		pidTo=newPid;
	}
	*/
}
