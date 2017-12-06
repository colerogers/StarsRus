import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Manager extends JFrame {

	private JPanel contentPane;
	private JTextField actionText;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Manager frame = new Manager();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Manager() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		String[] options = {"Add Interest", "Generate Monthly Statement", "List Active Customers", "DTER",
				"Customer Report", "Delete Transaction"};
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
//				getContentPane().removeAll();
//				getContentPane().revalidate();
//				getContentPane().repaint();
			}
		});
		btnSubmit.setBounds(129, 203, 117, 25);
		contentPane.add(btnSubmit);
	}
}
