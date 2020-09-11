package kb.SQLiteTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class My_SqLite {
    private static String table1 = "CREATE TABLE User " +     //�û���(�û�ID�����롢��Կ)
            "(ID INT PRIMARY KEY     NOT NULL," +
            "KEY TEXT NOT NULL," +
            "KC TEXT NOT NULL)";
    private static String table2 = "CREATE TABLE Message " +     //��Ϣ��������ID��������ID��ʱ�䡢���ݣ�
            "(SENDER_ID INT NOT NULL," +
            "RECEIVER_ID INT NOT NULL," +
            "TIME TEXT NOT NULL," +
            "TXT TEXT NOT NULL)";
    private static String table3 = "CREATE TABLE Group_table" +    //Ⱥ��Ա��ȺID����ԱID��
            "(GROUP_ID INT PRIMARY KEY NOT NULL," +
            "STAFF TEXT)";


    //�������е���Ϣ�������ݿ����
    public static void Insert_User_table(Statement stmt, List<Object> list) {
        String sql = null;
        try {
            int j = 0;
            for (int i = 0; i < (list.size() / 3); i++) {
                sql = "INSERT INTO User (ID,KEY,KC) " + "VALUES (" + list.get(j) + ",'" + list.get(j + 1) + "','" + list.get(j + 2) + "');";
                System.out.println(sql);
                stmt.executeUpdate(sql);
                j += 3;
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

    }


    //�����ݿ��û����ڵ����ݶ��������
    public static void Qurey_User_table(Statement stmt, List<Object> list) {
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM User;");
            while (rs.next()) {
                int id = rs.getInt("ID");
                String key = rs.getString("KEY");
                String kc = rs.getString("KC");
                list.add(id);
                list.add(key);
                list.add(kc);


            }
            System.out.println(list);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite://d:/keshe.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            stmt.executeUpdate(table1);  //�������ű�
            // stmt.executeUpdate(table2);
            //   stmt.executeUpdate(table3);
            System.out.println("get here!");

            List<Object> list1 = new ArrayList<Object>();
            List<Object> list2 = new ArrayList<Object>();
            list1.add(100);
            list1.add("aaa");
            list1.add("bbb");
            list1.add(200);
            list1.add("ccc");
            list1.add("ddd");
            list1.add(300);
            list1.add("eee");
            list1.add("fff");
            list1.add(400);
            list1.add("ggg");
            list1.add("hhh");
            System.out.println(list1);

	      /*
	      String sql=null;
	      int j=0;
	      for(int i=0;i<(list1.size()/3);i++) {
			    sql="INSERT INTO User (ID,KEY,KC) "+"VALUES ("+list1.get(j)+",'"+list1.get(j+1)+"','"+list1.get(j+2)+"');";
			    System.out.println(sql);
		     	stmt.executeUpdate(sql);
		     	j+=3;
		    }*/

            Insert_User_table(stmt, list1);
            Qurey_User_table(stmt, list2);
            System.out.println(list2);
	   
		    /* 	
		   ResultSet rs = stmt.executeQuery( "SELECT * FROM User;" );
		   while(rs.next()) {
				int id =rs.getInt("ID");
				String key=rs.getString("KEY");
				String kc=rs.getString("KC");
				list2.add(id);
				list2.add(key);
				list2.add(kc);}
				*/


            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

    }

}
