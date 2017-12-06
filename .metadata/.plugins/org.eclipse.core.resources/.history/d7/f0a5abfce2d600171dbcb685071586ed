import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;

public class Trader extends JFrame {

	private JPanel contentPane;
	private JTextField actionText;
	private DatabaseManager DB = new DatabaseManager("ckoziol", "959");
	private JTextField stockField;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					Trader frame = new Trader();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public Trader(String current_user) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);


		String[] trader_options = {"Deposit", "Withdraw", "Buy", "Sell", "Show Balance",
				"Transaction History", "Check Stock/Actor", "List Movie Info",
				"Top Movies in x Years", "Reviews for x Movie"};
		
		String[] manager_options = {"Add Interest", "Generate Monthly Statement", "List Active Customers", "DTER",
				"Customer Report", "Delete Transaction", "Change Date"};
		

		ArrayList<String> list = new ArrayList<String>();
		for(int i =0; i < trader_options.length; i++){
			list.add(trader_options[i]);
		}
		if(DB.isManager(current_user)){
			for(int i =0; i < manager_options.length; i++){
				list.add(manager_options[i]);
			}
		}
		String[] options = list.toArray(new String[0]);
		

		JComboBox actionField = new JComboBox(options);
		actionField.setBounds(149, 54, 196, 43);
		contentPane.add(actionField);
		
		JLabel lblSelectAction = new JLabel("Select Action");
		lblSelectAction.setBounds(29, 58, 147, 34);
		contentPane.add(lblSelectAction);
		
		actionText = new JTextField();
		actionText.setBounds(149, 127, 109, 37);
		contentPane.add(actionText);
		actionText.setColumns(10);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selected = (int)actionField.getSelectedIndex();
				switch(selected){
				case 0://deposit
					try{
						double d_value = Double.parseDouble(actionText.getText());
						DB.updateMA(current_user, d_value);
						double d_balance = DB.getBalance(current_user);
						JOptionPane.showMessageDialog(null, "Deposit complete, new balance: " + Double.toString(d_balance));
					} catch(NumberFormatException en){
						JOptionPane.showMessageDialog(null, "Please enter a valid amount");
					}
					break;
				case 1://withdraw
					try{
						int w_value = -1*Integer.parseInt(actionText.getText());
						DB.updateMA(current_user, w_value);
						double w_balance = DB.getBalance(current_user);
						JOptionPane.showMessageDialog(null, "Withdraw complete, new balance: " + Double.toString(w_balance));
					} catch(NumberFormatException en){
						JOptionPane.showMessageDialog(null, "Please enter a valid amount");
					}
					break;
				case 2://buy
					try{
						int b_shares = Integer.parseInt("0" + actionText.getText());
						String b_stock = stockField.getText();
						double b_stock_price = DB.getCurrentStockPrice(b_stock); // TODO: get cur price
						if(b_shares <= 0 || DB.stockExists(b_stock) != 0){
							JOptionPane.showMessageDialog(null, "Enter valid number of shares or valid stock symbol");
						}
						else if (b_stock_price >= 0){
							if (DB.updateSA(current_user, b_shares, b_stock_price, b_stock) == 0){
								JOptionPane.showMessageDialog(null, "Successfully bought shares");
							}else{
								JOptionPane.showMessageDialog(null, "Error occured");
							}
						}else {
							JOptionPane.showMessageDialog(null, "Error getting stock price");
						}
					} catch (Exception ex){
						JOptionPane.showMessageDialog(null, "Input Error");
					}
					break;
				case 3://sell
					String stock = stockField.getText();
					if(DB.stockExists(stock) == 0){
						try {
							SellStock frame = new SellStock(current_user, stock);
							frame.setVisible(true);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					else{
						JOptionPane.showMessageDialog(null, "Stock does not exist");
					}
//					int s_shares = Integer.parseInt(actionText.getText()) * (-1);
//					String s_stock = stockField.getText();
//					double s_stock_price = DB.getCurrentStockPrice(s_stock); //HERE fix stock price
//					if (s_stock_price >= 0){
//						if(DB.updateSA(current_user, s_shares, s_stock_price, s_stock) == 0){
//							JOptionPane.showMessageDialog(null, "Successfully sold shares");
//						}
//						else{
//							JOptionPane.showMessageDialog(null, "Error occured");
//						}
//					}else 
//						JOptionPane.showMessageDialog(null, "Error getting stock price");
					break;
				case 4://show balance
					double balance = DB.getBalance(current_user);
					JOptionPane.showMessageDialog(null, "You account balance is: " + Double.toString(balance));
					break;
				case 5://transaction history
					JOptionPane.showMessageDialog(null, DB.getTransactionHistory(current_user));
					break;
				case 6://check stock/actor
					String stock_symbol = stockField.getText();
					JOptionPane.showMessageDialog(null, DB.getActorAndStock(stock_symbol));
					break;
				case 7://list movie info
					String movie = actionText.getText();
					JOptionPane.showMessageDialog(null, DB.getMovie(movie));
					break;
				case 8://top movies in x years
					try{
						String t_movie = actionText.getText();
						String[] temp_years = t_movie.split("-");
						int year1 = Integer.parseInt(temp_years[0]);
						int year2 = Integer.parseInt(temp_years[1]);
						JOptionPane.showMessageDialog(null, DB.getTopMovies(year1, year2));
					} catch(NumberFormatException | ArrayIndexOutOfBoundsException ex){
						JOptionPane.showMessageDialog(null, "Please enter valid dates in form YYYY-YYYY");
					}
					break;
				case 9://reviews for x movie
					String r_movie = actionText.getText();
					JOptionPane.showMessageDialog(null, DB.getReviews(r_movie));
					break;
				case 10://Add interest
					break;
				case 11://generate monthly statement
					String user = actionText.getText();
					JOptionPane.showMessageDialog(null, DB.generateMonthlyStatement(user));
					break;
				case 12://list active customers
					JOptionPane.showMessageDialog(null, DB.listActiveCustomers());
					break;
				case 13://DTER
					JOptionPane.showMessageDialog(null, DB.generateGovernementReport());
					break;
				case 14://customer report
					JOptionPane.showMessageDialog(null, DB.customerReport());
					break;
				case 15://delete transactions
					if(1 == DB.deleteTransactions()){
						JOptionPane.showMessageDialog(null, "Error deleting Transactions");
					}
					else{
						JOptionPane.showMessageDialog(null, "Transactions for this month deleted");
					}
					break;
				case 16://change date
					String d = actionText.getText();
					if (DB.changeDay(d) != 0){
						JOptionPane.showMessageDialog(null, "Date change failed");
					}
					else{
						JOptionPane.showMessageDialog(null, "Updated current date to: " + d);
					}
					break;
				}

				
				actionText.setText("");
				stockField.setText("");
//				getContentPane().removeAll();
//				getContentPane().revalidate();
//				getContentPane().repaint();
			}
		});
		btnSubmit.setBounds(306, 214, 117, 25);
		contentPane.add(btnSubmit);
		
		JLabel lblActionAmount = new JLabel("Action Amount");
		lblActionAmount.setBounds(29, 127, 102, 15);
		contentPane.add(lblActionAmount);
		
		JLabel lblifApplicable = new JLabel("(If applicable)");
		lblifApplicable.setBounds(29, 138, 109, 15);
		contentPane.add(lblifApplicable);
		
		stockField = new JTextField();
		stockField.setBounds(149, 181, 109, 29);
		contentPane.add(stockField);
		stockField.setColumns(10);
		
		JLabel lblStockSymbol = new JLabel("Stock symbol");
		lblStockSymbol.setBounds(29, 180, 102, 15);
		contentPane.add(lblStockSymbol);
		
		JLabel lblifAppilcable = new JLabel("(If appilcable)");
		lblifAppilcable.setBounds(29, 195, 102, 15);
		contentPane.add(lblifAppilcable);
	}
}
