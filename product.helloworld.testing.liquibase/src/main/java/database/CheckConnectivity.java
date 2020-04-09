package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckConnectivity {

	public static void main(String[] args) {

		
		try {
		
			System.out.println("Start connectivity test ...");
			
			Connection con = DatabaseConnection.initializeDatabase(); 

			/*
			String query = "SELECT * FROM LIQ_TEST1";

			// create the java statement
			Statement st = con.createStatement();

			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(query);


			int j=1;
			while (rs.next()){
				String name = rs.getString("test1_name");
		        String location = rs.getString("test1_location");
		        String company = rs.getString("test1_company");

		        System.out.println("For row " + j + ", the name="+name + ", the location="+location+ ", and the company="+company);
				j++;
			}
			
			st.close(); 
			*/
			con.close(); 
			
			System.exit(0);
			
		}catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
		}
		
		
		
		
	}

}
