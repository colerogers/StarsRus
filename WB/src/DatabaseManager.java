import java.lang.*;
import java.sql.*;
import java.util.*;

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
	
	// helper print method
	public void p(String s){
		System.out.println("ERROR: "+s);
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
	    System.out.println(query);
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

    public int addMarketAccount(String c_username){
	if (!userExists(c_username)){
	    System.out.println("username does not exist");
	    return 1;
	}

	String s = String.format("INSERT INTO MarketAccounts (c_username, balance) VALUES ('%s', %f);", c_username, 1000.0);

	// add the MA to the DB
	return updateDB(s);
	}
	
	public int addStockAccount(String c_username, double shares, double stock_price, String stock_symbol){
		if (shares < 0){ p("shares below 0"); return 1; }
		if (stock_symbol.length() != 3){ p("stock_symbol not length 3"); return 1; }
		if (!userExists(c_username)){ p("user doesn't exist"); return 1; }
		// check account already exists
		String s = String.format("SELECT SA.c_username FROM StockAccount SA WHERE SA.c_username='%s' AND SA.stock_symbol='%s';", c_username, stock_symbol);
		resultSet = queryDB(s);
		try{
			if (resultSet.next()){
				return 1;
			}
		}catch (Exception e){}

		s = String.format("INSERT INTO StockAccount (c_username, shares, stock_price, stock_symbol) VALUES (%s, %f, %f, %s);", c_username, shares, stock_price, stock_symbol);
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
    public int updateMA(String username, double amount) {
	// check if user exists
	if (!userExists(username)){
	    System.out.println("username does not exist");
	    return 1;
	}
	String s = String.format("SELECT MA.c_username FROM MarketAccounts MA WHERE MA.c_username='%s';", username);
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
		System.out.println("MA Balance will go under 0");
		return 1;
	}

	s = String.format("UPDATE MarketAccounts MA SET MA.balance=MA.balance+%.2f WHERE MA.c_username='%s';", amount, username);
	if (updateDB(s) != 0)
		return 1;
	// TODO: add to transactions
	return addTransaction("", 0, 0, amount, 0, "1", username);
    }
    
    public int updateSA(String c_username, double shares, double stock_price, String stock_symbol){
	if (!userExists(c_username)){
	    System.out.println("username does not exist");
	    return 1;
	}
	// try to add account, if 0 then created account, if 1 already exists
	int code = addStockAccount(c_username, shares, stock_price, stock_symbol);

	if (getShares(c_username, stock_symbol) + shares < 0){
		System.out.println("SA shares will go under 0");
		return 1;
	}

	String s = String.format("UPDATE StockAccount SA SET SA.shares=SA.shares+%.2f WHERE SA.c_username='%s';", shares, c_username);
	
	if (updateDB(s) != 0)
		return 1;

	return addTransaction(stock_symbol, 0, stock_price, 0, shares, "1", c_username);
    }   
	// -1 is the error value
    public double getBalance(String username){
	if (!userExists(username)){
	    return -1;
	}
	String s = String.format("SELECT MA.balance FROM MarketAccounts MA WHERE MA.c_username='%s';", username);
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
	public double getShares(String username, String stock_symbol){
		if (!userExists(username)){ p("user not found"); return -1; }

		String s = String.format("SELECT SA.shares FROM StockAccount SA WHERE SA.c_username='%s' AND SA.stock_symbol='%s'", username, stock_symbol);
		resultSet = queryDB(s);
		try{
			if (!resultSet.next())
				return 0;
			return resultSet.getDouble("shares");
		}catch (Exception e){
			return 0;
		}
	}
	
	public int addTransaction(String stock_symbol, double intrest_accrued, double stock_price, double market_account_amount, double stock_account_amount, String t_date, String username){
		t_date = "1";
		String s;
		if (intrest_accrued != 0){
			// intrest transaction
			s = String.format("INSERT INTO Transactions (t_date, intrest_accrued, c_username) VALUES ('%s', %f, '%s';", t_date, intrest_accrued, username);
			return updateDB(s);
		} else if (market_account_amount != 0){
			// market account transaction
			s = String.format("INSERT INTO Transactions (t_date, market_account_amount, c_username) VALUES ('%s', %f, '%s');", t_date, market_account_amount, username);
			return updateDB(s);
		} else if (stock_account_amount != 0){
			// stock account transaction
			s = String.format("INSERT INTO Transactions (t_date, stock_symbol, stock_price, stock_account_amount, c_username) VALUES ('%s', '%s', %f, %f, '%s');", t_date, stock_symbol, stock_price, stock_account_amount, username);
			return updateDB(s);
		}else{
			p("bad transaction type");
			return 1;
		}
	}

	public String[] getTransactionHistory(String username){
		ArrayList<String> list = new ArrayList<String>();
		if (!userExists(username)){ p("user doesn't exist"); return list.stream().toArray(String[]::new); }
		String query = String.format("SELECT * FROM Transactions T WHERE T.c_username='%s'", username);
		resultSet = queryDB(query);
		try {
			String s, date, stock_sym;
			double intrest, stock_price, market_amt, stock_amt;
			while (resultSet.next()){
				date = resultSet.getString("t_date");
				stock_sym = resultSet.getString("stock_symbol");
				intrest = resultSet.getDouble("intrest_accrued");
				stock_price = resultSet.getDouble("stock_price");
				market_amt = resultSet.getDouble("market_account_amount");
				stock_amt = resultSet.getDouble("stock_account_amount");
				list.add(""+date+stock_sym+intrest+stock_price+market_amt+stock_amt+username);
			}
		}catch (Exception e){
			p("resultSet access in transaction history");
		}

		return list.stream().toArray(String[]::new);
	}

	public String[] listActiveCustomers(){
		ArrayList<String> list = new ArrayList<String>();

		String query = "SELECT Temp.c_username "
					  +"FROM { SELECT COUNT(T.stock_account_amount) AS shares, T.c_username "
							   +"FROM Transactions T "
							   +"GROUP BY T.c_username } AS Temp "
					  +"WHERE Temp.shares >= 1000";
		resultSet = queryDB(query);
		try{
			while (resultSet.next()){
				list.add(resultSet.getString("c_username"));
			}
		}catch (Exception e){
			p("resultSet access in list active customers");
		}

		return list.stream().toArray(String[]::new);
	}

	public int deleteTransactions(){

		return 0;
	}
	
	public String[] generateGovernementReport(){
		ArrayList<String> list = new ArrayList<String>();
		
		return list.stream().toArray(String[]::new);
	}

	public String[] customerReport(){
		ArrayList<String> list = new ArrayList<String>();
		
		return list.stream().toArray(String[]::new);
	}

    

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
