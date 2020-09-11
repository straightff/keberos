package kb.ToolClass;

import java.sql.*;
import java.util.ArrayList;

public class SqliteTool
{
    private String dataBaseName;
    private Statement stmt;

    //初始化建立数据库连接,db默认在src/DataBase下面
    public SqliteTool(String dbName) throws ClassNotFoundException {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:src/DataBase/"+dbName+".db");
            System.out.println("Opened database successfully");
            this.stmt = c.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

        //插入单项到表
    public boolean UpdateIntoTable(Statement statement, String key, String tableName, String colum) throws SQLException {
        //判断表项是否为空
//        ResultSet rs = stmt.executeQuery("select * from "+tableName+";");
        //sql语句

        return false;
    }
    //插入一列到表
    public boolean insertIntoTable(Statement statement,ArrayList<String> list,String tableName){
        String sql=null;
//        sql = "insert into "+tableName+" ("
        return false;
    }
    //从数据库查询多项值
    public String searchOneFromTable(String key,String tableName,String colum) throws SQLException {
        ResultSet re = stmt.executeQuery("select "+colum+" from "+tableName+" where ID="+key+";");
//        ResultSet re2 = stmt.executeQuery("select * from "+tableName+";");
        return  re.getString("KEY");
//        System.out.println(re.getString("KEY"));
//        System.out.println(re.toString());
    }
    //从表中删除列
    public boolean deleteFromTable(Statement statement,String key,String tableName){

        return false;
    }
    //表中删除单项
    public boolean deleteOneFromTable(Statement statement,String key,String tableName,String colum){

        return false;
    }

    //从数据库查询多项值

    //查询表项
    public ArrayList<String> searchFromTable(Statement statement,String key,String tableName){
        return  null;
    }
}
