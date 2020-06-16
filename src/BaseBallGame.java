import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
public class BaseBallGame{
	
	static String Server ="";
	static int Port = 0;
	public static void main(String[] args) throws RemoteException {
		
		
		
		BaseBallStart baseballstart = new BaseBallStart();
		
		baseballstart.userName.setText("user1");
		baseballstart.IP_addr.setText("127.0.0.1");
		baseballstart.port.setText("8888");

		Server = baseballstart.IP_addr.getText();
		Port = Integer.parseInt(baseballstart.port.getText());
		
		baseballstart.connect_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(baseballstart.userName.getText().equals("")||baseballstart.IP_addr.getText().equals("")||baseballstart.port.getText().equals(""))
					System.out.println("plz enter textfield!");
				else
				{
					System.out.println("connect");
					baseballstart.setVisible(false);
					new Thread(new BaseBallClient(Server, Port)).start();
				}
			}
			
		});
		/**
		 * BingoStart에 create 버튼 리스너
		 */
		baseballstart.create_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(baseballstart.port.getText().equals(""))
					System.out.println("plz enter port!");
				else
				{
					System.out.println("createServer");
					try {
						new Thread(new BaseBallServer(Port)).start();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		/**
		 * BingoStart에 exit 버튼 리스너
		 */
		baseballstart.exit_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(1);
			}
		});
	}
}
class BaseBallStart extends JFrame{
	
	private JPanel contentPane;
	JTextField userName;
	JTextField IP_addr;
	JTextField port;
	JButton connect_btn;
	JButton create_btn;
	JButton exit_btn;
	
	BaseBallStart(){
		super("BASEBALL GAME");
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