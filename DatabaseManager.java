import java.lang.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

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
//        System.exit(1);
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
    
    public int addStockAccount(String c_username, int shares, double stock_price, String stock_symbol){
        if (shares < 0){ p("shares below 0"); return 1; }
        if (stock_symbol.length() != 3){ p("stock_symbol not length 3"); return 1; }
        if (!userExists(c_username)){ p("user doesn't exist"); return 1; }
        // check account already exists
        if (hasStockAccount(c_username, stock_symbol) == 0){
            p("account already exists");
            return 1;
        }

        String s = String.format("INSERT INTO StockAccount (c_username, shares, stock_price, stock_symbol) VALUES ('%s', %d, %f, '%s');", c_username, shares, stock_price, stock_symbol);
        // add the SA to the DB
        return updateDB(s);
    }

    public int hasStockAccount(String username, String stock_symbol){
        if (stock_symbol.length() != 3){ p("stock_symbol not length 3"); return 1; }
        if (!userExists(username)){ p("user doesn't exist"); return 1; }
        if (stockExists(stock_symbol) == 1){
            p("stock does't exist in hasStockAccount");
            return 1;
        }
        // check account already exists
        String s = String.format("SELECT SA.c_username FROM StockAccount SA WHERE SA.c_username='%s' AND SA.stock_symbol='%s';", username, stock_symbol);
        resultSet = queryDB(s);
        try{
            if (resultSet.next()){
                return 0;
            }
        }catch (Exception e){ p("exception in hasStockAccount"); return 1;}
        return 1;
    }

    public int hasStockAccount(String username){
        if (!userExists(username)){ p("user doesn't exist"); return 1; }
        // check account already exists
        String s = String.format("SELECT SA.c_username FROM StockAccount SA WHERE SA.c_username='%s';", username);
        resultSet = queryDB(s);
        try{
            if (resultSet.next()){
                return 0;
            }
        }catch (Exception e){ p("exception in hasStockAccount"); return 1;}
        return 1;
    }

    // return 0 if true, 1 if false
    public int stockExists(String stock_symbol){
        String s = String.format("SELECT S.stock_symbol FROM Stocks S WHERE S.stock_symbol='%s'", stock_symbol);
        resultSet = queryDB(s);
        try{
            if (!resultSet.next()){
                p("stock doesn't exist");
                return 1;
            }
        }catch (Exception e){
            p("exception, stock doesn't exist");
            return 1;
        }
        return 0;
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
	
	return addTransaction("", 0, 0, amount, 0, username);
    }

    // Use if we dont want to add to the transaction log
    public int updateMA(String username, double amount, int i) {
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

    return 0;
    }
    
    
    // buy and sell shares of stocks
    public int updateSA(String c_username, int shares, double stock_price, String stock_symbol){
	if (!userExists(c_username)){
	    System.out.println("username does not exist");
	    return 1;
	}
	double amount = Math.abs(shares * getCurrentStockPrice(stock_symbol)) + 20;
	if (shares > 0){
		// buy stock
		if (getBalance(c_username) + amount < 0){
			p(" balance will go under 0");
			return 1;
		}
		if (hasStockAccount(c_username, stock_symbol) == 1){
			p("customer does not have a stock account ... making one");
			if (addStockAccount(c_username, shares, stock_price, stock_symbol) != 0){
				p("stock account could not be created");
				return 1;
			}
		}
		if (updateMA(c_username, -1*amount, 1) != 0){
			p("error withdrawing market account in updateSA");
			return 1;
		}

		//check to see if user already has stock for that price
		String check = String.format("SELECT SA.* FROM StockAccount SA WHERE c_username='%s' AND stock_price=%.2f", c_username, stock_price);
		resultSet = queryDB(check);
		int already_owned = 1;
		try{
		    if (!resultSet.next()){
		    	already_owned = 0;
		    }
		}catch (Exception e){ return 1; }
		
		if(already_owned == 1){
			String s = String.format("UPDATE StockAccount SA SET SA.shares=SA.shares+%d "
					+ "WHERE SA.c_username='%s' AND SA.stock_symbol='%s' AND SA.stock_price=%f;", shares, c_username, stock_symbol,stock_price);
			
			if (updateDB(s) != 0){
				p("could not update the account");
				return 1;
			}
		}
		else{
			String s = String.format("INSERT INTO StockAccount (c_username, shares, stock_price, stock_symbol) "
					+ "VALUES ('%s', %d, %f, '%s');", c_username, shares, stock_price, stock_symbol);
			
			if (updateDB(s) != 0){
				p("could not update the account");
				return 1;
			}
		}

		return addTransaction(stock_symbol, 0, stock_price, 0, shares, c_username);

	}
	if (shares < 0){
		// sell stock
		if (hasStockAccount(c_username, stock_symbol) == 1){
			p("customer does not have a stock account");
			return 1;
		}
		// check if owned shares of the inputed stock price will go below 0
	        int owned_shares = getShares(c_username, stock_symbol, stock_price);
		if (owned_shares + shares < 0){
			System.out.println("SA shares will go under 0");
			return 1;
		}
		if (updateMA(c_username, amount, 1) != 0){
			p("error depositing market account");
			return 1;
		}
		
		String s = String.format("UPDATE StockAccount SA SET SA.shares=SA.shares+%d WHERE SA.c_username='%s' "
				+ "AND SA.stock_symbol='%s' AND SA.stock_price=%f;", shares, c_username, stock_symbol, stock_price);
 		if (updateDB(s) != 0){
			p("could not update the account");
			return 1;
		}
		if (owned_shares + shares == 0){
		    // delete the row
		    s = String.format("DELETE FROM StockAccount WHERE c_username='%s' AND stock_symbol='%s' AND stock_price=%f", c_username, stock_symbol, stock_price);
		}

		
		return addTransaction(stock_symbol, 0, stock_price, 0, shares, c_username);
	}

	p("tried to buy or sell zero shares");
	return 1;
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
    public String[] getShares(String username, String stock_symbol){
    if (!userExists(username)){ p("user not found"); return null; }
    ArrayList<String> prices = new ArrayList<String>();
    
    String s = String.format("SELECT SA.shares, SA.stock_price FROM StockAccount SA WHERE SA.c_username='%s' AND SA.stock_symbol='%s';", username, stock_symbol);
    resultSet = queryDB(s);
    try{
        while(resultSet.next())
        prices.add(resultSet.getInt("shares") +" "+resultSet.getString("stock_price"));
    }catch (Exception e){
        p("exception in getShares(username, stock_symbol)");
    }
    return prices.stream().toArray(String[]::new);
    }
    // -1 is the error value;
    public int getShares(String username, String stock_symbol, double stock_price){
        if (!userExists(username)){ p("user not found"); return -1; }

        String s = String.format("SELECT SA.shares FROM StockAccount SA WHERE SA.c_username='%s' AND SA.stock_symbol='%s' AND SA.stock_price=%f", username, stock_symbol, stock_price);
        resultSet = queryDB(s);
        try{
            if (!resultSet.next())
                return 0;
            return resultSet.getInt("shares");
        }catch (Exception e){
            return 0;
        }
    }
    
    // -1 is the error value
    public double getCurrentStockPrice(String stock_symbol){
		if (stockExists(stock_symbol) != 0){
            p("stock doesn't exist in getCurrentStockPrice()");
            return -1;
        }
        String s = String.format("SELECT S.current_price FROM Stocks S WHERE S.stock_symbol='%s';", stock_symbol);
        resultSet = queryDB(s);
        try{
            if (resultSet.next()){
                return resultSet.getDouble("current_price");
            }
        }catch (Exception e){ p("exception in getCurrentStockPrice"); }
        return -1;
    }
    
    public String getActorAndStock(String stock_symbol){
        double current_price = getCurrentStockPrice(stock_symbol);
        if(current_price == -1){
            return "Stock symbol/Actor not found";
        }
        
        String s = String.format("SELECT A.* FROM Actors A WHERE A.stock_symbol='%s'", stock_symbol);
        resultSet = queryDB(s);
        String actor_info = "";
        try{
            if (resultSet.next()){
                actor_info += "Name: " + resultSet.getString("name");
                actor_info += "\n Stock Symbol: " + resultSet.getString("stock_symbol");
                actor_info += "\n Stock price: " + current_price;
                actor_info += "\n Birthday: " + resultSet.getString("birthday");
            }
        }catch (Exception e){ p("exception in getting Actor"); }
        
        return actor_info;
    }
    
    

    public int getMovieId(String movie){
        String s = String.format("SELECT M.* FROM moviesDB.Movies M WHERE M.title = '%s'", movie);
        resultSet = queryDB(s);
        String movie_info = "";
        try{
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            int current_id = -1;
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if(rsmd.getColumnName(i).equals("id")){
                        current_id = Integer.parseInt(resultSet.getString(i));
                    }
                    if(rsmd.getColumnName(i).equals("title") && resultSet.getString(i).equals(movie)){
                        return current_id;
                    }
                }
            }
        }catch(Exception e){
            //do nothing rn
        }
        return -1;
    }
    
    public String getReviews(String movie){
        int movie_id = getMovieId(movie);
        String s = String.format("SELECT R.* FROM moviesDB.Reviews R WHERE R.movie_id = %d", movie_id);
        resultSet = queryDB(s);
        String reviews = "";
        try{
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if(rsmd.getColumnName(i).equals("author")){
                        reviews += resultSet.getString(i) + ": ";
                    }
                    else if(rsmd.getColumnName(i).equals("review")){
                        reviews += resultSet.getString(i) + "\n";
                    }
                }
            }
            if(reviews.length() == 0){
                return "No reviews found for this movie";
            }
            return "Reviews for - " + movie + ":\n" + reviews;
        }catch(Exception e){
            //do nothing rn
        }
        return "No Reviews Found for " + movie;
    }
    
    public String getTopMovies(int year1, int year2){
        String s = String.format(
                "SELECT M.title "
                + "FROM moviesDB.Movies M "
                + "WHERE M.rating >= 5 "
                + "AND M.production_year >= %d "
                + "AND M.production_year <= %d", year1, year2 );
        
        resultSet = queryDB(s);
        String top_movies = "";
        try{
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if(rsmd.getColumnName(i).equals("title")){
                        top_movies += resultSet.getString(i) + "\n";
                    }
                }
            }
            if(top_movies.length() == 0){
                return "No movies match this criteria";
            }
            return String.format("Top Movies from %d - %d:\n", year1, year2) + top_movies;
        } catch(Exception e){
            //do nothing rn
        }
        return "No movies match this criteria";
    }
    
    
    public String getMovie(String movie){
        String s = String.format("SELECT M.* FROM moviesDB.Movies M WHERE M.title = '%s'", movie);
        resultSet = queryDB(s);
        String movie_info = "";
        try{
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    String columnValue = resultSet.getString(i);
                    movie_info += rsmd.getColumnName(i) + ": " + columnValue + "\n"; 
//			        System.out.print(columnValue + " " + rsmd.getColumnName(i));
			    }
			}
		}catch(Exception e){
			//do nothing rn
		}
		if(movie_info.length() == 0){
			return "No movie with that title found";
		}
		return movie_info + "\n" + getReviews(movie);
	}

	public int addTransaction(String stock_symbol, double intrest_accrued, double stock_price, double market_account_amount, double stock_account_amount, String username){
		String s;
		String t_date = getCurrentDay();
		
		if (intrest_accrued > (double)0){
			// intrest transaction
		    s = String.format("INSERT INTO Transactions (t_date, interest_accrued, c_username) VALUES ('%s', %f, '%s');", t_date, intrest_accrued, username);
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

	public String getTransactionHistory(String username){
		ArrayList<String> list = new ArrayList<String>();
//		if (!userExists(username)){ p("user doesn't exist"); return list.stream().toArray(String[]::new); }
        if (!userExists(username)){ p("user doesn't exist"); return "User does not exist"; }
        String query = String.format("SELECT * FROM Transactions T WHERE T.c_username='%s'", username);
        resultSet = queryDB(query);
        try {
            String s, date, stock_sym;
            double interest, stock_price, market_amt, stock_amt;
            while (resultSet.next()){
//				date = resultSet.getString("t_date");
                stock_sym = resultSet.getString("stock_symbol");
                interest = resultSet.getDouble("interest_accrued");
                stock_price = resultSet.getDouble("stock_price");
                market_amt = resultSet.getDouble("market_account_amount");
                stock_amt = resultSet.getDouble("stock_account_amount");
                String temp_s = "";
                if(stock_sym != null){
                    temp_s += "Stock Sym: " + stock_sym + " ";
                }
                if(interest != 0){
                    temp_s += "Interest: " + interest + " ";
                }
                if(stock_price != 0){
                    temp_s += "Stock Price: " + stock_price + " ";
                }
                if(stock_amt != 0){
                    temp_s += "Stock Amount: " + stock_amt + " ";
                }
                if(market_amt != 0){
                    temp_s += "Market Amount: " + market_amt + " ";
                }
                list.add(temp_s);
//				list.add(""+date+stock_sym+interest+stock_price+market_amt+stock_amt+username);
            }
        }catch (Exception e){
            p(e.toString());
            p("resultSet access in transaction history");
        }

//		return list.stream().toArray(String[]::new);
		String s = "";
		for(int i = 0; i < list.size(); i++){
			s += list.get(i) + "\n";
		}
		if(s.length() == 0){
			return "No transactions found for this user";
		}
		return s;
	}
	
	public String getCurrentDay(){
		String s = "SELECT * FROM CurrentDay";
		resultSet = queryDB(s);
		try{
			if (resultSet.next()){
				return resultSet.getString("day");
			}
		}catch (Exception e){
			p("Error getting current day");
		}
		return "ERROR GETTING DAY";
	}
	

	public int changeDay(String d){
		String s = String.format("UPDATE CurrentDay SET day='%s'", d);
		if(updateDB(s) != 0){
			p("ERROR UPDATING DATE");
			return -1;
		}
		return 0;
	}
	

	public String listActiveCustomers(){
		ArrayList<String> list = new ArrayList<String>();

		String query = "SELECT Temp.c_username "
					  +"FROM ( SELECT SUM(ABS(T.stock_account_amount)) AS shares, T.c_username "
							   +"FROM Transactions T "
							   +"GROUP BY T.c_username ) AS Temp "
					  +"WHERE Temp.shares >= 1000";
		resultSet = queryDB(query);
		try{
			while (resultSet.next()){
				list.add(resultSet.getString("c_username"));
			}
		}catch (Exception e){
			p("resultSet access in list active customers");
		}

//		return list.stream().toArray(String[]::new);
        String s = "";
        for(int i  = 0; i < list.size(); i++){
            s += list.get(i);
        }
        if(s.length() == 0){
            return "No customers trader more than 1000 shares this month";
        }
        return s;
        
    }

    public int deleteTransactions(){
        String s = "DELETE FROM Transactions;";
	if (updateDB(s) != 0) return 1;
	return 0;
	/*
        int i = 1;
        try{
            i = statement.executeUpdate(s);
        }catch (Exception e){
            System.out.println("executeQuery() failed");
            System.out.println(s);
//		    System.exit(1);
            return 1;
        }
        return i;
	*/
    }

    public int deleteBalance(){
	String s = "DELETE FROM Balance;";
	if (updateDB(s) != 0) return 1;
	return 0;
    }
    
    public String generateGovernementReport(){
    	String query1 = "SELECT T.c_username, SUM(T.interest_accrued) as sum FROM Transactions T WHERE T.interest_accrued IS NOT NULL GROUP BY T.c_username";
    	String query2 = "SELECT T.c_username, SUM(T.stock_price * -1 * T.stock_account_amount - 20) as sum FROM Transactions T WHERE T.stock_account_amount < 0 GROUP BY c_username";
        
        try{
            resultSet = queryDB(query1);
        }catch (Exception e){
            p("executeQuery() failed");
            p(query1);
        }

        HashMap<String, Double> total = new HashMap<String, Double>();
        try{
        	while(resultSet.next()){
        		total.put(resultSet.getString("c_username"), resultSet.getDouble("sum"));
        	}
        } catch(Exception e){
        	p(e.toString());
        	p("Error in gov report");
        }
        
        try{
            resultSet = queryDB(query2);
        }catch (Exception e){
            p("executeQuery() failed");
            p(query2);
        }
        
        try{
        	while(resultSet.next()){
        		if(total.get(resultSet.getString("c_username")) == null){
            		total.put(resultSet.getString("c_username"), resultSet.getDouble("sum"));
        		}
        		else{
        			double value = total.get(resultSet.getString("c_username"));
        			value += resultSet.getDouble("sum");
        			total.put(resultSet.getString("c_username"), value);
        		}
        	}
        } catch(Exception e){
        	p("Error in gov report");
        }
        
        String people = "";
        for (String key : total.keySet()){
        	if(total.get(key) >= 10000){
        		String query3 = String.format("SELECT U.name, U.state FROM Users U WHERE U.c_username='%s'", key);
        		resultSet = queryDB(query3);
        		try{
        			if(resultSet.next()){
        				String state = resultSet.getString("state");
        				String name = resultSet.getString("name");
        				people += String.format("Name: %s, State: %s\n", name, state);
        			}
        		} catch (Exception ex){
        			p(ex.toString());
        		}
        	}
        }
//        System.out.println(people);
        if (people==""){
        	return "No users earned more than $10,000 this month";
        }
        return "Users who have earned more than $10,000 this month:\n" + people;
    }

    // "" is the error condition
    public String getCustomerName(String username){
        if (!userExists(username)){
            p("username doesn't exist getCustomerName");
            p(username);
            return "";
        }
        String s = String.format("SELECT U.name FROM Users U WHERE U.c_username='%s';", username);
        ResultSet rs = queryDB(s);
        String name = "";
        try{
            if (rs.next()){
            //p("in here");
            name = rs.getString("name");
            }
        }catch (Exception e){ p("exception in getCustomerName"); }
        return name;
    }
    
    
    public String generateMonthlyStatement(String username){
    	if(!userExists(username)){
    		return "Error user does no exist";
    	}
    	String query1 = String.format("SELECT U.name, U.email FROM Users U WHERE U.c_username = '%s'", username);
    	String statement = "";
    	//get name and email
    	try{
    		resultSet = queryDB(query1);
    		if(resultSet.next()){
    			String name = resultSet.getString("name");
    			String email = resultSet.getString("email");
    			statement += String.format("Name: %s, Email: %s\n", name, email);
    		}
    	}  catch (Exception ex){
    		p(ex.toString());
    	}
    	
    	//get starting and closing balance
    	
    	//first day of the month so no balances yet
    	double start_month = -1;
    	double end_month = -1;    	
    	if (getCurrentDate().split("-")[2].equals("01")){
    		start_month = getBalance(username);
    		end_month = start_month;
    	} 
    	else{
    	
	    	String max = String.format("(SELECT MAX(B.balance_date) FROM Balance B WHERE B.c_username='%s')", username);
	    	String min = String.format("(SELECT MIN(B.balance_date) FROM Balance B WHERE B.c_username='%s')", username);
	    	String query_begging = String.format("SELECT B.balance FROM Balance B WHERE B.c_username='%s' AND B.balance_date=%s", username, min);
	    	String query_end = String.format("SELECT B.balance FROM Balance B WHERE B.c_username='%s' AND B.balance_date=%s", username, max);
	    	

	    	try{
	    		resultSet = queryDB(query_begging);
	    		if(resultSet.next()){
	    			start_month = resultSet.getDouble("balance");
	    		}
	    	}  catch (Exception ex){
	    		p(ex.toString());
	    	}    	
	    	try{
	    		resultSet = queryDB(query_end);
	    		if(resultSet.next()){
	    			end_month = resultSet.getDouble("balance");
	    		}
	    	}  catch (Exception ex){
	    		p(ex.toString());
	    	}
    	}
    	
    	statement += String.format("Starting Balance: $%.2f\nEndingBalance: $%.2f\n", start_month, end_month);
    	
    	double earnings = end_month - start_month;
    	if(earnings >= 0){
    		statement += String.format("Earnings: $%.2f\n", earnings);
    	}
    	else{
    		statement += String.format("Loss: $%.2f\n", earnings);
    	}
 
    	
    	//total commision paid
    	String commision_query = String.format("SELECT COUNT(*) as count FROM Transactions T WHERE T.c_username = '%s' AND T.stock_symbol IS NOT NULL", username);
    	try{
    		resultSet = queryDB(commision_query);
    		if(resultSet.next()){
    			int total_commision = 20*resultSet.getInt("count");
    			statement += String.format("Total commision paid: $%d", total_commision);
    		}
    	}  catch (Exception ex){
    		p(ex.toString());
    	}
    		
    	
    	return statement;
    }

    // "" is the error condidtion
    // return: "stock_symbol_1:shares_1, stock_symbol_2:shares_2, ..."
    public String getCustomerStockAccountsShares(String username){
        if (!userExists(username)){
            p("username doesn't exist getCustomerStockAccountShares");
            return "";
        }
        String s = String.format("SELECT SA.stock_symbol, SA.shares FROM StockAccount SA WHERE SA.c_username='%s';", username);
        ResultSet rs = queryDB(s);
        StringBuilder sb = new StringBuilder("");
        try{
            while (rs.next()){
                sb.append(rs.getString("stock_symbol")+":"+rs.getInt("shares"));
                if (rs.next())
                    sb.append(", ");
            }
        }catch (Exception e){ p("exception in getCustomerStockAccountsShares"); }
        return sb.toString();
    }

    public String customerReport(){
        StringBuilder sb = new StringBuilder();
        String query = "SELECT * FROM MarketAccounts;";
        ResultSet rs = queryDB(query);

        ArrayList<String> usernames = new ArrayList<String>();
        ArrayList<String> balances = new ArrayList<String>();

        String username;
        double balance;
        try{
            while (rs.next()){
                username = rs.getString("c_username");
                usernames.add(username);
                balance = rs.getDouble("balance");
                balances.add("" + balance);
            }
        }catch (Exception e){
            p("customerReport error");

        }
        for (int i=0; i<usernames.size(); i++){
            sb.append(getCustomerName(usernames.get(i)) + ":");
            sb.append("\n\tMarket Account Balance: " + balances.get(i));
            if (hasStockAccount(usernames.get(i)) == 0){
                  sb.append("\n\tStock Account(s): " + getCustomerStockAccountsShares(usernames.get(i)));
            }
            if (i+1 != usernames.size()){
            sb.append("\n\n");
            }
        }
        
        return sb.toString();
    }

    // dateToSet = "YYYY-MM-DD"
    public int setDate(String dateToSet){
        String[] parts = dateToSet.split("-");
        int yearToSet = Integer.parseInt(parts[0]);
        int monthToSet = Integer.parseInt(parts[1]);
        int dayToSet = Integer.parseInt(parts[2]);
        parts = getCurrentDate().split("-");
        int curYear = Integer.parseInt(parts[0]);
        int curMonth = Integer.parseInt(parts[1]);
        int curDay = Integer.parseInt(parts[2]);
        //if (yearToSet < curYear) { p("can't set a previous year"); return 1; }
        //if (monthToSet < curMonth) { p("can't set a previous month"); return 1; }
        //if (dayToSet < curDay) { p("can't set a previous day"); return 1; }

        String query, s;

        if (yearToSet != curYear){
            // year change
            System.out.println("changing year");
            
            // this loop will get us to correct year
            for (int i=curYear; i<yearToSet; i++){
                for (int j=curMonth; j<12; j++){
                    for (int k=curDay; k<31; k++){
                        if (endOfDay() != 0) { p("setDate: endOfDay inner loop 1");}
                        //if (deleteCurrentDate() != 0) { p("year change for loop in setDate"); }
                        s = String.format("%02d",i) + "-" + String.format("%02d",j) + "-" + String.format("%02d",k+1);
			changeDay(s);
                        //query = String.format("INSERT INTO CurrentDay values ('%s')", s);
                        //if (updateDB(query) != 0) { p("year change for loop in setDate"); }
                    }
                    if (endOfDay() != 0) { p("setDate: endOfDay middle loop 1");}
                    //if (deleteCurrentDate() != 0) { p("year change for loop in setDate"); }
                    s = String.format("%02d",i) + "-" + String.format("%02d",j+1) + "-" + Integer.toString(01);
		    changeDay(s);
                    //query = String.format("INSERT INTO CurrentDay values ('%s')", s);
                    //if (updateDB(query) != 0) { p("year change for loop in setDate"); }
                    if (accrueIntrest() != 0) { p("setDate: accrueIntrest middle loop 1"); }
                    if (deleteTransactions() != 0) { p("setDate deleteTransactions middle loop 1"); }
		    if (deleteBalance() != 0) { p("setDate deleteBalance middle loop 1"); }
                }
                // i-12-1
                for (int k=1; k<31; k++){
                    if (endOfDay() != 0) { p("setDate: endOfDay middle loop 2 @ 833");}
                    //if (deleteCurrentDate() != 0) { p("year change for loop in setDate"); }
                    s = String.format("%02d",i) + "-" + Integer.toString(12) + "-" + String.format("%02d",k + 1);
		    changeDay(s);
                    //query = String.format("INSERT INTO CurrentDay values ('%s')", s);
                    //if (updateDB(query) != 0) { p("year change for loop in setDate"); }
                }
		// i-12-30
                if (endOfDay() != 0) { p("year endOfDay @ 841");}
                //if (deleteCurrentDate() != 0) { p("year change for loop in setDate"); }
                s = String.format("%02d",i+1) + "-" + Integer.toString(01) + "-" + Integer.toString(01);
		changeDay(s);
                //query = String.format("INSERT INTO CurrentDay values ('%s')", s);
                //if (updateDB(query) != 0) { p("year change for loop in setDate"); }
                if (accrueIntrest() != 0) { p("setDate: accrueeInterest @ 847"); }
                if (deleteTransactions() != 0) { p("setDate: deleteTransactions @ 848"); }
		if (deleteBalance() != 0) { p("setDate: deleteBalance @ 849"); }
            }
	    parts = getCurrentDate().split("-");
	    curYear = Integer.parseInt(parts[0]);
	    curMonth = Integer.parseInt(parts[1]);
	    curDay = Integer.parseInt(parts[2]);
            // this loop will get us to first of the month
	    for (int i=curMonth; i<monthToSet; i++){
                // all months have 30 days?
                for (int j=curDay; j<31; j++){
                    if (endOfDay() != 0) { p("setDate @ 859");}
		    s = Integer.toString(curYear) + "-" + String.format("%02d", i) + "-" + String.format("%02d", j+1);
		    changeDay(s);
		    
                    //if (deleteCurrentDate() != 0) { p("month change for loop in setDate"); }
                    //s = Integer.toString(curYear) + "-" + String.format("%02d", i) + "-" + String.format("%02d", j+1);
                    //query = String.format("INSERT INTO CurrentDay values ('%s')", s);
                    //if (updateDB(query) != 0) { p("month change for loop in setDate"); }
                }
		// curYear-i-30
                if (endOfDay() != 0) { p("setDate @ 869");}
		s = Integer.toString(curYear) + "-" + String.format("%02d", i+1) + "-" + Integer.toString(01);
		changeDay(s);
		
                //if (deleteCurrentDate() != 0) { p("month change for loop in setDate"); }
                //s = Integer.toString(curYear) + "-" + String.format("%02d", i+1) + "-" + Integer.toString(01);
                //query = String.format("INSERT INTO CurrentDay values ('%s')", s);
                //if (updateDB(query) != 0) { p("month change for loop in setDate"); }
		
                if (accrueIntrest() != 0) { p("setDate @ 878"); }
                if (deleteTransactions() != 0) { p("setDate @ 879"); }
		if (deleteBalance() != 0) { p("setDate @ 880"); }
            }
	    
	    parts = getCurrentDate().split("-");
	    curMonth = Integer.parseInt(parts[1]);
	    curDay = Integer.parseInt(parts[2]);
            // this loop will get us to correct day
	    for (int i=curDay; i<dayToSet; i++){
                if (endOfDay() != 0) { p("setDate @ 888");}
                //if (deleteCurrentDate() != 0) { p("month change for loop in setDate"); }
                s = Integer.toString(curYear) + "-" + Integer.toString(curMonth) + "-" + String.format("%02d", i+1);
		changeDay(s);
		
                //query = String.format("INSERT INTO CurrentDay values ('%s')", s);
                //if (updateDB(query) != 0) { p("month change for loop in setDate"); }
            }
           

        }else if (monthToSet != curMonth){
            // month change
            System.out.println("changing month");

            // this loop will get us to first of the month
            for (int i=curMonth; i<monthToSet; i++){
                // all months have 30 days?
                for (int j=curDay; j<31; j++){
                    if (endOfDay() != 0) { p("month change; inner endOfDay called in setDate");}
		    s = Integer.toString(curYear) + "-" + String.format("%02d", i) + "-" + String.format("%02d", j+1);
		    changeDay(s);
		    
                    //if (deleteCurrentDate() != 0) { p("month change for loop in setDate"); }
                    //s = Integer.toString(curYear) + "-" + String.format("%02d", i) + "-" + String.format("%02d", j+1);
                    //query = String.format("INSERT INTO CurrentDay values ('%s')", s);
                    //if (updateDB(query) != 0) { p("month change for loop in setDate"); }
                }
		// curYear-i-30
                if (endOfDay() != 0) { p("month change; outer endOfDay called in setDate");}
		s = Integer.toString(curYear) + "-" + String.format("%02d", i+1) + "-" + Integer.toString(01);
		changeDay(s);
		
                //if (deleteCurrentDate() != 0) { p("month change for loop in setDate"); }
                //s = Integer.toString(curYear) + "-" + String.format("%02d", i+1) + "-" + Integer.toString(01);
                //query = String.format("INSERT INTO CurrentDay values ('%s')", s);
                //if (updateDB(query) != 0) { p("month change for loop in setDate"); }
		
                if (accrueIntrest() != 0) { p("month change; accrueIntrest called in setDate"); }
                if (deleteTransactions() != 0) { p("month change; deleteTransactions called in setDate"); }
		if (deleteBalance() != 0) { p("month change; deleteBalance called in setDate"); }
            }
	    // curYear-monthToSet-01
	    parts = getCurrentDate().split("-");
	    curMonth = Integer.parseInt(parts[1]);
	    curDay = Integer.parseInt(parts[2]);
            // this loop will get us to correct day
            for (int i=curDay; i<dayToSet; i++){
                if (endOfDay() != 0) { p("month change; endOfday called in setDate");}
                //if (deleteCurrentDate() != 0) { p("month change for loop in setDate"); }
                s = Integer.toString(curYear) + "-" + Integer.toString(curMonth) + "-" + String.format("%02d", i+1);
		changeDay(s);
		                
            }

        }else{
            // day change
            System.out.println("changing day");

            for (int i=curDay; i<dayToSet; i++){
                if (endOfDay() != 0) { p("day change for loop in setDate");}
		
		s = Integer.toString(curYear) + "-" + Integer.toString(curMonth) + "-" + String.format("%02d", i+1);
		changeDay(s);
	            
            }

        }

        return 0;
    }

    // use when updating date
    public int deleteCurrentDate(){
        String query = "DELETE FROM CurrentDay";
        if (updateDB(query) != 0){
            p("couldn't delete current date");
            return 1;
        }
        return 0;
    }

    // YYYY-MM-DD format
    public String getCurrentDate(){
        String query = "SELECT C.day FROM CurrentDay C";
        ResultSet rs = queryDB(query);
        try{
            if (rs.next())
                return rs.getDate("day").toString();
        } catch (Exception e) { p("exception in getCurrentDate"); }
        return "";
    }

    // required to get the avg daily balance
    public int endOfDay(){
        // for every customer add their balance to the Balance table
        ArrayList<String> usernames = new ArrayList<String>();
        ArrayList<Double> balances = new ArrayList<Double>();
        String query = "SELECT * FROM MarketAccounts;";
        ResultSet rs = queryDB(query);
        try{
            while (rs.next()){
                usernames.add(rs.getString("c_username"));
                balances.add(rs.getDouble("balance"));
            }
        }catch (Exception e){ p("exception in endOfDay"); }
        for (int i=0; i<usernames.size(); i++){
            query = String.format("INSERT INTO Balance VALUES ('%s', %f, '%s')", usernames.get(i), balances.get(i), getCurrentDate());
            if (updateDB(query) != 0)
                p("inserting into balance");
        }
        return 0;
    }

    // intrest = average daily balance * .03
    // use this method when we are currently on the first day of the month
    // so we wouldn't use the balance in the market account today
    public int accrueIntrest(){
        HashMap<Double, Integer> frequency = new HashMap<Double, Integer>();
        ArrayList<String> usernames = new ArrayList<String>();
        String query = "SELECT DISTINCT B.c_username FROM MarketAccounts B;";
        ResultSet rs = queryDB(query);
        try{
            while (rs.next()){
                usernames.add(rs.getString("c_username"));
            }
        }catch (Exception e){ p("exception in accrueIntrest 1st query"); }
        double balance, avgDailyBalance, interest;
        int count;
        Iterator iter;
        Map.Entry pair;

	double total;
	
        for (int i=0; i<usernames.size(); i++){
            count = 0;
            avgDailyBalance = 0;
	    total = 0;
            // get all the balances for a user in the current month
            query = String.format("SELECT B.balance FROM Balance B WHERE B.c_username='%s'", usernames.get(i));
            rs = queryDB(query);
            try{
                while (resultSet.next()){
                    balance = rs.getDouble("balance");
		    total += balance;
		    /*
                    if (frequency.containsKey(balance)){
                        frequency.replace(balance, frequency.get(balance));
                    }else{
                        frequency.put(balance, 0);
                    }
		    */
                    count++;
                }
            }catch (Exception e) { p("exception in accrueIntrest 2nd query"); }
            
            // calculate average daily balance
	    /*
            iter = frequency.entrySet().iterator();
            while (iter.hasNext()){
                pair = (Map.Entry)iter.next();
                avgDailyBalance += ((Double)pair.getKey() * (Integer)pair.getValue());
            }
            avgDailyBalance /= count;
	    */
            //intrest = avgDailyBalance * .03;
	    interest = (total/(double)count) * .03;
            // update MA without posting a transaction
            if (updateMA(usernames.get(i), interest, 1) != 0){
                p("couldn't add intrest when trying to call updateMA");
            }

            // post a intrest transaction
            if (addTransaction("", interest, 0, 0, 0, usernames.get(i)) != 0){
                p("couldn't post add intrest transaction from accrueIntrest");
            }

            frequency = new HashMap<Double, Integer>();
        }
        
        return 0;
    }
    

    public static void main(String []args){
    DatabaseManager db;
    if (args.length != 3){
        db = new DatabaseManager("ckoziol", "959");
    }else {
        db = new DatabaseManager(args[1], args[2]);
    }
    System.out.println("changing date: "+db.setDate("2018-04-03"));

    //System.out.println("currentDate(): "+db.getCurrentDate());
    //System.out.println("endOfDay(): "+Integer.toString(db.endOfDay()));

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