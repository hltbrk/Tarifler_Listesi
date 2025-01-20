
package javaapplication2;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DbHelper {
    static String username="root";
    static String password="1234";
    static  String  dburl="jdbc:mysql://localhost:3306/yemek";

    public Connection getConnection() throws SQLException {
        return(Connection) DriverManager.getConnection(dburl,username,password);

    }

    public void ShowError(SQLException exception){
        System.out.println("Error : "+exception.getMessage());
        System.out.println("Error Code : "+exception.getErrorCode());
    }

}
