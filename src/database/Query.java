package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Query {

	static Connection conn = null;
	static Statement stmt = null;

	public static boolean insert(String sql) throws SQLException , Exception{

		Statement stmt = null;
		Connection conn = null;
		

			conn = Connexion.connect();

			//STEP 4: Execute a query
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Inserted records into the table...");
			stmt.close();
			conn.close();
			return true;
//		}catch(SQLException se){
//			//Handle errors for JDBC
//			Methodes.showExceptionAlert("Erreur Base de donne", "Erreur lors de l insert dans la base de donne", se);
//			se.printStackTrace();
//			
//		}catch(Exception e){
//			//Handle errors for Class.forName
//			Methodes.showExceptionAlert("Erreur Base de donne", "Erreur lors de l insert dans la base de donne", e);
//			
//			e.printStackTrace();
//		}
//
//		return false;
	}//end main


	public static ResultSet select(String sql) throws SQLException , Exception{
		//try{
			//if(conn== null || stmt == null) {
			conn = Connexion.connect();
			//STEP 4: Execute a query
			stmt = conn.createStatement();
			//}

			ResultSet rs = stmt.executeQuery(sql);
			return rs;
//		}catch(SQLException se){
//			//Handle errors for JDBC
//			Methodes.showExceptionAlert("Erreur Base de donne", "Erreur lors du select dans la base de donne", se);
//			se.printStackTrace();
//			return null;
//		}catch(Exception e){
//			//Handle errors for Class.forName
//			Methodes.showExceptionAlert("Erreur Base de donne", "Erreur lors du select dans la base de donne", e);
//			e.printStackTrace();
//			return null;
//		}

	}

	//sous selection lorsque la connextion et le stmt sont toujours ouvet!!!!!!!!!!!
	public static ResultSet sub_select(String sql) throws SQLException{
		//try{
			if(conn== null || stmt == null) {
				conn = Connexion.connect();
				//STEP 4: Execute a query
				stmt = conn.createStatement();
			}

			ResultSet rs = stmt.executeQuery(sql);
			return rs;
//		}catch(SQLException se){
//			//Handle errors for JDBC
//			Methodes.showExceptionAlert("Erreur Base de donne", "Erreur lors du sub_select dans la base de donne", se);
//			se.printStackTrace();
//			return null;
//		}
	}


	public static void close_connection()throws SQLException{

		//try {
			stmt.close();
			conn.close();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			Methodes.showExceptionAlert("Erreur Base de donne", "Erreur lors du close connection dans la base de donne", e);
//			e.printStackTrace();
//		}
	}
	
	
	public static int getRowCount(String table_name) throws SQLException{
		
		ResultSet rs;
		//try {
			conn = Connexion.connect();
			//STEP 4: Execute a query
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("select count(*) from " + table_name);
			//Moving the cursor to the last row
			rs.next();
			return rs.getInt("count(*)");
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			Methodes.showExceptionAlert("Erreur Base de donne", "Erreur lors du getrowcount  dans la base de donne", e);
//			e.printStackTrace();
//		}
//	      
//	      
//	      return -1;
	}

}
