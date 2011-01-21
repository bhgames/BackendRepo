package BHEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TextDemo {
	static final String TIME_FORMAT_NOW = "hh:mm:ss MM/dd";

public static void main(String args[]) {
	try {
		   Class.forName("com.mysql.jdbc.Driver");
		     
		      Connection con =
		                     DriverManager.getConnection(
		                                 GodGenerator.url,GodGenerator.user, GodGenerator.pass);
            Statement stmt = con.createStatement();
            Statement stmt2 = con.createStatement();
            ResultSet t = stmt.executeQuery("select tid from town;");
            stmt2.execute("start transaction;");
            while(t.next()) {
            	/*
            	 * +-------------+------------------+------+-----+---------+----------------+
| Field       | Type             | Null | Key | Default | Extra          |
+-------------+------------------+------+-----+---------+----------------+
| name        | varchar(30)      | NO   |     | NULL    |                |
| slot        | int(11)          | NO   |     | NULL    |                |
| lvl         | int(11)          | NO   |     | NULL    |                |
| lvling      | int(11)          | YES  |     | NULL    |                |
| ppl         | int(11)          | YES  |     | NULL    |                |
| pplbuild    | int(11)          | YES  |     | NULL    |                |
| pplticks    | int(11)          | YES  |     | NULL    |                |
| tid         | int(10) unsigned | NO   | MUL | NULL    |                |
| lvlUp       | int(11)          | NO   |     | NULL    |                |
| deconstruct | tinyint(1)       | NO   |     | NULL    |                |
| pploutside  | int(11)          | YES  |     | -1      |                |
| bunkerMode  | int(11)          | YES  |     | 0       |                |
| bid         | int(10) unsigned | NO   | PRI | NULL    | auto_increment |
+-------------+------------------+------+-----+---------+----------------+
name				slot	lvl		lvling	ppl		pplbuild	pplticks  tid	lvlUp	deconstruct	  pploutside	bnkrMode	bid
| Headquarters      |    7 |   4 |     -1 |    0 |        0 |        0 |    4 |     0 |           0 |         -1 |          0 |  12 |

            	 */
            	
            	stmt2.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp," +
            			"deconstruct,pploutside,bunkerMode) values ('Metal Mine',0,3,-1,0,0,0,"+t.getInt(1)+",0,0,-1,0);");
            	stmt2.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp," +
            			"deconstruct,pploutside,bunkerMode) values ('Timber Field',1,3,-1,0,0,0,"+t.getInt(1)+",0,0,-1,0);");
            	stmt2.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp," +
            			"deconstruct,pploutside,bunkerMode) values ('Manufactured Materials Plant',2,3,-1,0,0,0,"+t.getInt(1)+",0,0,-1,0);");
            	stmt2.execute("insert into bldg (name,slot,lvl,lvling,ppl,pplbuild,pplticks,tid,lvlUp," +
            			"deconstruct,pploutside,bunkerMode) values ('Food Farm',3,3,-1,0,0,0,"+t.getInt(1)+",0,0,-1,0);");
            }
            stmt2.execute("commit;");
            stmt2.close(); t.close(); stmt.close(); con.close();
            
            stmt.close();
} catch(ClassNotFoundException exc) {} catch(SQLException exc) {exc.printStackTrace();}
}
static String nowTime() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_NOW);
    return sdf.format(cal.getTime());

  }



}
