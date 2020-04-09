package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection { 
 
	
	public static Connection initializeDatabase() 
        throws SQLException, ClassNotFoundException 
    { 
		
		System.out.println("Connection set starts ...");

        //String dbDriver = "com.mysql.jdbc.Driver"; 
        String dbURL = "jdbc:mysql://192.168.33.14:3306/"; 
        String dbName = "hellodb"; 
        String dbUsername = "ac"; 
        String dbPassword = "ac"; 
  
        //Class.forName(dbDriver); 
        Connection con = DriverManager.getConnection(dbURL + dbName+"?useSSL=false", 
                                                     dbUsername,  
                                                     dbPassword); 

		System.out.println("Connection set done!");
        
        return con; 
        

    } 
    
} 