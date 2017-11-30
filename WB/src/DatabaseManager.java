import java.lang.*;
import java.sql.*;

public class DatabaseManager {
    final String HOST = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/ckoziolDB";
    final String USER;
    final String PWD;
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;

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
	} finally {
			
	    //connection.close();
	}
	try{
	    statement = connection.createStatement();
	}catch (Exception e){
	    System.out.println("CreateStatement() failed");
	    System.exit(1);
	}finally {
		
	    //statement.close();
	}
    }

    /*
      Use when we want something in return
      ex~ SELECT
     */
    public ResultSet queryDB(String query){
	try{
	    resultSet = statement.executeQuery(query);
	}catch (Exception e){
	    System.out.println("executeQuery() failed");
	    System.exit(1);
	}
	return resultSet;
    }
    /*
      Use when we dont need anything back
      ex~ INSERT
     */
    public int updateDB(String update){
	try{
	    statement.executeUpdate(update);
	}catch (Exception e){
	    System.out.println("Error updating, using :");
	    System.out.println(update);
	    System.out.println();
	    return 1;
	}
	return 0;
    }

    public boolean userExists(String username){
	String check = String.format("SELECT name FROM Users U WHERE U.c_username='%s';", username);
	resultSet = queryDB(check);
	if (!resultSet.next()){
	    return false;
	}
	return true;
    }

    public int addMarketAccount(int m_id, String c_username, double balance){
	if (!userExists(c_username)){
	    System.out.println("username does not exist");
	    return 1;
	}
	String s = String.format("INSERT INTO MarketAccount (m_id, c_username, balance) WHERE (%d, %s, %f);", m_id, c_username, balance);
	// add the MA to the DB
	return updateDB(s);
    }

    public int addUser(String name, String state, String pNumber, String email, String taxId, String address, String username, int manager, String password){
	if (state.length() != 2){
	    System.out.println("State must be 2 letters");
	    return 1;
	}
	if (pNumber.length() != 10){
	    System.out.println("Phone number must be 10 digits");
	    return 1;
	}
	// TODO: check if username is in db already and throw error

	String s = String.format("INSERT INTO Users (c_username, name, state, phone, email, tax_id, address, manager, password) VALUES ('%s', '%s','%s','%s','%s','%s','%s', %d, '%s')",username,name,state, pNumber,email, taxId, address, manager, password);

	return updateDB(s);
    }

    /*
      market:0->stockAcct, 1->marketAcct
    */
    public int updateMA(int m_id, String username, double amount) {
	// check if user exists
	if (!userExists(username)){
	    System.out.println("username does not exist");
	    return 1;
	}
	String s = String.format("SELECT MA.username FROM MarketAccount MA WHERE MA.username='%s';", username);
	resultSet = queryDB(s);
	if (!resultSet.next()){
	    // create market account for user and add $1000
	    System.out.println("Market Account does not exist");
	    return 1;
	}
	s = String.Format("UPDATE MarketAccount MA SET MA.amount=%d WHERE MA.username='%s';", amount, username);

	return updateDB(s);
    }
    public int updateSA(int s_id, String, String c_username, double shares, double stock_price, String stock_symbol){
	if (!userExists(c_username)){
	    System.out.println("username does not exist");
	    return 1;
	}
	String s = String.format("SELECT SA.c_username FROM StockAccount SA WHERE SA.c_username='%s';", c_username);
	resultSet = queryDB(s);
	if (!resultSet.next()){
	    System.out.println("Stock Account does not exist");
	    return 1;
	}

	return updateDB(s);
    }
   
    public ResultSet buy(String query) {
	return resultSet;
    }
    public ResultSet sell(String query) {
	return resultSet;
    }
    public ResultSet history(String query) {
	return resultSet;
    }
    public ResultSet intrest_M(String query) {
	return resultSet;
    }
    public ResultSet statement_M(String query) {
	return resultSet;
    }
    public ResultSet activeCustomer_M(String query) {
	return resultSet;
    }
    public ResultSet DTER_M(String query) {
	return resultSet;
    }
    public ResultSet customerReport_M(String query) {
	return resultSet;
    }
    public ResultSet delete_M(String username) {
	
	return resultSet;
    }

    

    public static void main(String []args){
	DatabaseManager db;
	if (args.length != 3){
	    db = new DatabaseManager("ckoziol", "959");
	}else {
	    db = new DatabaseManager(args[1], args[2]);
	}

	//ResultSet rs = db.queryDB("SELECT * FROM Actors;");
	db.openMarketAccount("abc","CA", "6198189328", "test@gmail", "123","123", "abc",0, "abc");
	/*
	  try{
	  while (rs.next()){
	  System.out.println("rs has next");
	  }
	  System.out.println("rs doesnt have next");
	  }catch (Exception e){
	  System.out.println("error in rs.next()");
	  }
	*/
	
	System.out.println("Done.");
    }
}
