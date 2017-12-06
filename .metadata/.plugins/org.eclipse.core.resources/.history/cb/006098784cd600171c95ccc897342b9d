import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class SellStock extends JFrame {

	private JPanel contentPane;
	private JTextField amountField;
	private DatabaseManager DB = new DatabaseManager("ckoziol", "959");

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					SellStock frame = new SellStock();
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
	public SellStock(String current_user, String current_stock) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		
		
		String[] shares_list = DB.getShares(current_user, current_stock);
		
		JComboBox sharesField = new JComboBox(shares_list);
		sharesField.setBounds(193, 88, 206, 50);
		contentPane.add(sharesField);
		
		JLabel lblStock = new JLabel("Stock");
		lblStock.setBounds(47, 33, 70, 15);
		contentPane.add(lblStock);
		
		JLabel lblSharesPrice = new JLabel("Shares / Price");
		lblSharesPrice.setBounds(33, 98, 119, 31);
		contentPane.add(lblSharesPrice);
		
		JLabel stockField = new JLabel(current_stock);
		stockField.setBounds(193, 22, 134, 36);
		contentPane.add(stockField);
		
		JButton btnNewButton = new JButton("Sell Shares");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//sell stocks
				int selected = (int)sharesField.getSelectedIndex();
				String to_sell = shares_list[selected];
				double stock_price = Double.parseDouble(to_sell.split("\\s+")[1]);
				System.out.println(stock_price);
				int shares = -1*Integer.parseInt(amountField.getText());
				if(DB.updateSA(current_user, shares, stock_price, current_stock) == 0){
					JOptionPane.showMessageDialog(null, "Stock sold");
					dispose();
				}
				else {
					JOptionPane.showMessageDialog(null, "Error selling stock");
				}
			}
		});
		btnNewButton.setBounds(321, 226, 117, 25);
		contentPane.add(btnNewButton);
		
		JLabel lblAmountToSell = new JLabel("Amount to Sell");
		lblAmountToSell.setBounds(33, 185, 105, 15);
		contentPane.add(lblAmountToSell);
		
		amountField = new JTextField();
		amountField.setBounds(215, 183, 159, 19);
		contentPane.add(amountField);
		amountField.setColumns(10);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnClose.setBounds(55, 226, 117, 25);
		contentPane.add(btnClose);
	}
}
