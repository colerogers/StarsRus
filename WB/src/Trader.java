import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Trader extends JFrame {

	private JPanel contentPane;
	private JTextField actionText;
	private DatabaseManager DB = new DatabaseManager("ckoziol", "959");

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


		String[] options = {"Deposit", "Withdraw", "Buy", "Sell", "Show Balance",
				"Transaction History", "Check Stock/Actor", "List Movie Info",
				"Top Movies in x Years", "Reviews for x Movie"};
		JComboBox actionField = new JComboBox(options);
		actionField.setBounds(194, 58, 196, 43);
		contentPane.add(actionField);
		
		JLabel lblSelectAction = new JLabel("Select Action");
		lblSelectAction.setBounds(29, 58, 147, 34);
		contentPane.add(lblSelectAction);
		
		actionText = new JTextField();
		actionText.setBounds(194, 132, 196, 37);
		contentPane.add(actionText);
		actionText.setColumns(10);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selected = (int)e.getSource();
				switch(selected){
				case 0://deposit
					int d_value = Integer.parseInt(actionText.getText());
					DB.updateMA(current_user, d_value);
					break;
				case 1://withdraw
					int w_value = -1*Integer.parseInt(actionText.getText());
					DB.updateMA(current_user, w_value);
					break;
				case 2://buy
					break;
				case 3://sell
					break;
				case 4://show balance
					break;
				case 5://transaction history
					break;
				case 6://check stock/actor
					break;
				case 7://list movie info
					break;
				case 8://top movies in x years
					break;
				case 9://reviews for x movie
					break;
				}
				
				
				
//				getContentPane().removeAll();
//				getContentPane().revalidate();
//				getContentPane().repaint();
			}
		});
		btnSubmit.setBounds(129, 203, 117, 25);
		contentPane.add(btnSubmit);
		
		JLabel lblActionAmount = new JLabel("Action Amount");
		lblActionAmount.setBounds(29, 127, 102, 15);
		contentPane.add(lblActionAmount);
		
		JLabel lblifApplicable = new JLabel("(If applicable)");
		lblifApplicable.setBounds(29, 154, 109, 15);
		contentPane.add(lblifApplicable);
	}
}
