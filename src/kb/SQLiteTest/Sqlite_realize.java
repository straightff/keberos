package kb.SQLiteTest;

import kb.ToolClass.SqliteTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class Sqlite_realize {
	private static String table1 = "CREATE TABLE User " +     //�û���(�û�ID�����롢��Կ)
			"(ID INT PRIMARY KEY     NOT NULL," +
			"KEY TEXT NOT NULL,"+
			"KC TEXT NOT NULL)";
	private static String table2 = "CREATE TABLE Message "+     //��Ϣ��������ID��������ID��ʱ�䡢���ݣ�
			"(SENDER_ID INT NOT NULL,"+
			"RECEIVER_ID INT NOT NULL,"+
			"TIME TEXT NOT NULL,"+
			"TXT TEXT NOT NULL)";
	private static String table3="CREATE TABLE Group_table"+    //Ⱥ��Ա��ȺID����ԱID��
			"(GROUP_ID INT PRIMARY KEY NOT NULL,"+
			"STAFF TEXT)";
	

	public void create_table() {   //�������ݿⲢ����
		Connection c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite://d:/keshe.db");
	      System.out.println("Opened database successfully");

	      stmt = c.createStatement();
	      stmt.executeUpdate(table1);  //�������ű�
	      stmt.executeUpdate(table2);
	      stmt.executeUpdate(table3);
	      
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}
	
	//�������е���Ϣ�������ݿ����
	public void Insert_User_table(Statement stmt,List<Object> list) {
		String sql=null;
		try {
		    for(int i=0;i<(list.size()/3);i++) {
			    sql="INSERT INTO User (ID,KEY,KC) "+"VALUES ("+list.get(i)+",'"+list.get(i+1)+"','"+list.get(i+2)+"');";
		     	stmt.executeUpdate(sql);
		    }
		}catch(Exception e){
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		}
		
	}
	
	//���˱��ڵ����ݶ��������
	public void Qurey_User_table(Statement stmt,List<Object> list) {  
		try {
			ResultSet rs = stmt.executeQuery( "SELECT * FROM User;" );
			while(rs.next()) {
				int id =rs.getInt("ID");
				String key=rs.getString("KEY");
				String kc=rs.getString("KC");
				list.add(id);
				list.add(key);
				list.add(kc);
			}
			System.out.println(list);
		}catch(Exception e) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		}

	}

	public static void main(String[] args) {

	}

}
