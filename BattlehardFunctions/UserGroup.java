package BattlehardFunctions;

import java.util.ArrayList;

public class UserGroup {
/*
 * +-------------+------------------+------+-----+---------+----------------+
| Field       | Type             | Null | Key | Default | Extra          |
+-------------+------------------+------+-----+---------+----------------+
| usergroupid | int(10) unsigned | NO   | PRI | NULL    | auto_increment |
| name        | varchar(50)      | NO   |     | NULL    |                |
| pid         | int(10) unsigned | NO   | MUL | NULL    |                |
+-------------+------------------+------+-----+---------+----------------+

 */
	int userGroupID;
	String name;
	ArrayList<String> users;
	ArrayList<Integer> pids;
	public UserGroup(String name, int userGroupID, ArrayList<String> users, ArrayList<Integer> pids) {
		this.name = name;
		this.userGroupID = userGroupID;
		this.users = users;
		this.pids=pids;
	}
	public int getUserGroupID() {
		return userGroupID;
	}
	public String getName() {
		return name;
	}
	public ArrayList<String> getUsers() {
		return users;
	}
	public ArrayList<Integer> getPIDs() {
		return pids;
	}
	
	
	

}
