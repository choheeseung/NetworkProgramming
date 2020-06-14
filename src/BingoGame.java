import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class BingoGame {
	static String Server ="";
	static int Port = 0000;
	public static void main(String[] args) {
		BingoStart bingostart = new BingoStart();
		
		bingostart.userName.setText("user1");
		bingostart.IP_addr.setText("127.0.0.1");
		bingostart.port.setText("8888");
		

		Server = bingostart.IP_addr.getText();
		Port = Integer.parseInt(bingostart.port.getText());
		
		bingostart.connect_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(bingostart.userName.getText().equals("")||bingostart.IP_addr.getText().equals("")||bingostart.port.getText().equals(""))
					System.out.println("plz enter textfield!");
				else
				{
					System.out.println("connect");
					bingostart.setVisible(false);
					new Thread(new BingoClient(Server, Port)).start();
				}
			}
		});
		/**
		 * BingoStart에 create 버튼 리스너
		 */
		bingostart.create_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(bingostart.port.getText().equals(""))
					System.out.println("plz enter port!");
				else
				{
					System.out.println("createServer");
					new Thread(new BingoServer(Port)).start();
					bingostart.create_btn.setEnabled(false);
					
				}
			}
		});
		/**
		 * BingoStart에 exit 버튼 리스너
		 */
		bingostart.exit_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(1);
			}
		});
	}
}
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