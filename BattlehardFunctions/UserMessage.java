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
	
	UUID id;
	public UUID getId() {
		return id;
	}

	public int[] getPidTo() {
		return pidTo;
	}

	public int getPidFrom() {
		return pidFrom;
	}

	public String getBody() {
		return body;
	}

	public String getSubject() {
		return subject;
	}
/**
 * Not sure on these:
 * 0 is normal
 * 3 is league invite
 * 4 is league accept
 * 5 is system message(meaning only visible to AIs)
 * 6 is vassal invite
 * @return
 */
	public int getMsgType() {
		return msgType;
	}

	public boolean isReaded() {
		return readed;
	}

	public int getTsid() {

		return tsid;
	}

	public UUID getOriginalSubjectID() {
		return originalSubjectID;
	}

	public UUID getSubjectID() {
		return subjectID;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public String getUsernameFrom() {
		return usernameFrom;
	}public String[] getUsernameTo() {
		return usernameTo;
	}
	public boolean getDeleted() { 
		return deleted;
	}
	public void setReaded(boolean read) {
		readed=read;
	}
	public void setDeleted(boolean deleted) {
		this.deleted=deleted;
	}
	

	int pidTo[];
	int pidFrom;
	String body;
	String subject;
	int msgType;
	boolean readed;
	boolean deleted;
	int tsid=-1;
	UUID originalSubjectID;
	String creationDate;
	String usernameFrom,usernameTo[];
	UUID subjectID;
	public UserMessage(UUID messageID,int pidTo[], int pidFrom,String usernameTo[], String usernameFrom, String body, String subject, int msgType, boolean readed, int tsid, UUID originalSubjectID, String creationDate, UUID subjectID,boolean deleted) {
		this.id=messageID;this.pidTo=pidTo;this.pidFrom=pidFrom;this.body=body;this.subject=subject;this.msgType=msgType;
		this.readed=readed;
		
		this.tsid=tsid;
		this.deleted=deleted;
		this.originalSubjectID=originalSubjectID;this.creationDate=creationDate;
		this.subjectID=subjectID;
		this.usernameTo=usernameTo;this.usernameFrom=usernameFrom;
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
