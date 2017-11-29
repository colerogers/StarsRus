import java.lang.*;
import java.sql.*;

public class DatabaseManager {
    final String HOST = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/<database>";
    final String USER;
    final String PWD;
    Connection connection;
    Statement statement;
    ResultSet resultSet;

    //public Connection getConnection() throws

    DatabaseManager(){
	USER = PWD = "";
    }

    DatabaseManager(String user, String password){
	USER = user;
	PWD = password;
	// load the driver 
	try{ Class.forName("com.mysql.jdbc.Driver");
	}catch (Exception e){
	    System.out.println("Error: unable to load class driver");
	    System.exit(1);
	}

	// connect to the database
	try{
	    connection = DriverManager.getConnection(HOST, USER, PWD);
	}catch (Exception e){
	    System.out.println("Error: unable to connect to db");
	    System.exit(1);
	}finally{
	    //connection = DriverManager.
	}

	String QUERY = "";
	try{
	statement = connection.createStatement();
	}catch (Exception e){
	    System.out.println("CreateStatement() failed");
	    System.exit(1);
	}
	try{
	resultSet = statement.executeQuery(QUERY);
	}catch (Exception e){
	    System.out.println("executeQuery() failed");
	    System.exit(1);
	}
    }

    public static void main(String []args){
	DatabaseManager db;
	if (args.length != 3){
	    db = new DatabaseManager("colerogers", "249");
	}else {
	    db = new DatabaseManager(args[1], args[2]);
	}

	System.out.println("working");
	
    }
}
