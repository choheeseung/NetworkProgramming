import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

class BingoStart extends JFrame{
	
	private JPanel contentPane;
	JTextField userName;
	JTextField IP_addr;
	JTextField port;
	JButton connect_btn;
	JButton create_btn;
	JButton exit_btn;
	
	BingoStart(){
		super("BINGO GAME");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 170);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("User Name :");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(12, 10, 80, 21);
		contentPane.add(lblNewLabel);
		
		userName = new JTextField();
		userName.setBounds(95, 10, 100, 21);
		contentPane.add(userName);
		userName.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("IP Address : ");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1.setBounds(12, 41, 80, 21);
		contentPane.add(lblNewLabel_1);
		
		IP_addr = new JTextField();
		IP_addr.setBounds(95, 41, 100, 21);
		contentPane.add(IP_addr);
		IP_addr.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("PORT : ");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_2.setBounds(12, 72, 80, 21);
		contentPane.add(lblNewLabel_2);
		
		
		port = new JTextField();
		port.setBounds(95, 72, 45, 21);
		contentPane.add(port);
		port.setColumns(10);
		
		connect_btn = new JButton("Connect");
		connect_btn.setBounds(5, 103, 90, 23);
		contentPane.add(connect_btn);
		
		create_btn = new JButton("Create");
		create_btn.setBounds(100, 103, 90, 23);
		contentPane.add(create_btn);
		
		exit_btn = new JButton("Exit");
		exit_btn.setBounds(195, 103, 80, 23);
		contentPane.add(exit_btn);
		
		
		this.setVisible(true);
	}
}