package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexion {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.sqlite.JDBC";  
	static final String DB_URL = "jdbc:sqlite:database3.db";

	//  Database credentials
	static final String USER = "root";
	static final String PASS = "talla";

	public static Connection connect() {
		Connection conn = null;
		try{
			//STEP 2: Register JDBC driver
			Class.forName("org.sqlite.JDBC");

			//STEP 3: Open a connection	
			conn = DriverManager.getConnection(DB_URL);//, USER, PASS);
			return conn;
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
			return null;
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
			return null;
		}
	}
}
