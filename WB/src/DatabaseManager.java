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
	try{
	    if (!resultSet.next()){
		return false;
	    }
	}catch (Exception e){
	    return false;
	}
	return true;
    }

    public boolean validUser(String username, String password){
	String check = String.format("SELECT U.password FROM Users U WHERE U.c_username='%s';", username);
	resultSet = queryDB(check);
	try{
	    String pw;
	    if (resultSet.next())
		pw = resultSet.getString("password");
	    else
		return false;
	    if (!pw.equals(password))
		return false;
	}catch (Exception e){ return false; }
	return true;
    }

    public boolean isManager(String username){
	String check = String.format("SELECT U.manager FROM Users U WHERE U.c_username='%s';", username);
	resultSet = queryDB(check);
	try{
	    int man=0;
	    if (resultSet.next())
		man = resultSet.getInt("manager");
	    else
		return false;
	    if (man == 0)
		return false;
	}catch (Exception e){ return false; }
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
	try{
	    if (!resultSet.next()){
		// create market account for user and add $1000
		System.out.println("Market Account does not exist");
		return 1;
	    }
	}catch (Exception e){ return 1; }
	// get current balance and check if the amount will take the account below zero
	if (getBalance(username) + amount < 0){
		System.out.println("MA Balance will go under 0")
		return 1;
	}

	s = String.format("UPDATE MarketAccount MA SET MA.amount=MA.amount+%.2f WHERE MA.username='%s';", amount, username);

	return updateDB(s);
    }
    
    public int updateSA(int s_id, String c_username, double shares, double stock_price, String stock_symbol){
	if (!userExists(c_username)){
	    System.out.println("username does not exist");
	    return 1;
	}
	String s = String.format("SELECT SA.c_username FROM StockAccount SA WHERE SA.c_username='%s';", c_username);
	resultSet = queryDB(s);
	try{
	    if (!resultSet.next()){
		System.out.println("Stock Account does not exist");
		return 1;
	    }
	}catch (Exception e){ return 1; }


	s = String.format("UPDATE StockAccount SA SET SA.shares=SA.shares+%.2f WHERE SA.c_username='%s';", shares, c_username);
	
	return updateDB(s);
    }   
	// -1 is the error value
    public double getBalance(String username){
	if (!userExists(username)){
	    return -1;
	}
	String s = String.format("SELECT MA.balance FROM MarketAccount MA WHERE MA.c_username='%s';", username);
	resultSet = queryDB(s);
	try{
	    if (!resultSet.next())
		return -1;
	    return resultSet.getDouble("balance");
	}catch (Exception e){
	    return -1;
	}
	}
	// -1 is the error value;
	public double getShares(String username){
		if (!userExists(username)){
			return -1;
		}
		String s = String.format("SELECT SA.shares FROM StockAccount SA WHERE SA.c_username='%s'", username);
		resultSet = queryDB(s);
		try{
			if (!resultSet.next())
				return -1;
			return resultSet.getDouble("shares");
		}catch (Exception e){
			return -1;
		}
	}
    /*
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
    */

    

    public static void main(String []args){
	DatabaseManager db;
	if (args.length != 3){
	    db = new DatabaseManager("ckoziol", "959");
	}else {
	    db = new DatabaseManager(args[1], args[2]);
	}

	//ResultSet rs = db.queryDB("SELECT * FROM Actors;");
	//db.openMarketAccount("abc","CA", "6198189328", "test@gmail", "123","123", "abc",0, "abc");
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
