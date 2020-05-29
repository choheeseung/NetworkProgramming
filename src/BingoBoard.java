import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class BingoBoard extends JFrame {

	private JPanel contentPane;
	private JButton[] NumButton=new JButton[25];
	private int[] check=new int[25];
 
	private JPanel panel;
	private JTextField textField;
 
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		try {
			Bingo_start bs = new Bingo_start();
			bs.connect_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					bs.connect();
					bs.setVisible(false);
				}
				
			});
			
			bs.exit_btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					System.exit(0);
				}
				
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * Create the frame.
	 */
	public BingoBoard() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 800, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
  
		panel = new JPanel();
		panel.setBounds(20, 50, 350, 350);
		contentPane.add(panel);
		panel.setLayout(new GridLayout(5, 5, 3, 3));
  
		JLabel mine = new JLabel("My BingoBoard");
		mine.setBounds(100, 4, 310, 50);
		mine.setFont(new Font("Serif", Font.BOLD, 25));
		contentPane.add(mine);
		
		JButton RanButton = new JButton("Random Shuffle");
		RanButton.setBounds(20, 410, 170, 70);
		RanButton.setFont(new Font("Serif", Font.BOLD, 20));
		contentPane.add(RanButton);
		
		JButton ReadyButton = new JButton("READY");
		ReadyButton.setBounds(200, 410, 170, 70);
		ReadyButton.setFont(new Font("Serif", Font.BOLD, 25));
		contentPane.add(ReadyButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(400, 50, 368, 178);
		contentPane.add(scrollPane);
		
		textField = new JTextField();
		textField.setBounds(400, 240, 276, 39);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton SendButton = new JButton("Send");
		SendButton.setBounds(690, 240, 78, 39);
		contentPane.add(SendButton);
		
		JPanel yourpanel = new JPanel();
		yourpanel.setBounds(400, 291, 200, 200);
		contentPane.add(yourpanel);
		yourpanel.setLayout(new GridLayout(5, 5, 1, 1));
		for(int i=0;i<25;i++){
			NumButton[i]=new JButton("");
			yourpanel.add(NumButton[i]);
			NumButton[i].setEnabled(false);
		}
		
		initButton();
		this.setVisible(true);
	}
	public void initButton(){
		int cnt;
		int[] nums=new int[25];
		Random rand=new Random();
		for(int j=0;j<25;j++){
			while(true){
				cnt=0;
				nums[j]=rand.nextInt(25)+1;
				//랜덤으로 중복된 숫자 빼기
				for(int k=0;k<j;k++){
					if(nums[j]==nums[k]){
						cnt++;
					}
				}
				if(cnt==0){break;}
			}
		}
  
		for(int i=0;i<25;i++){
			check[i]=0;
			NumButton[i]=new JButton(""+nums[i]);
			NumButton[i].setFont(new Font("Serif", Font.BOLD, 25));
			panel.add(NumButton[i]);
			NumButton[i].addActionListener((ActionListener) new NumButtonEvent());
		}
	}
 
	
	class NumButtonEvent implements ActionListener{ //버튼누르면 enable이 false로 바뀜
		public void actionPerformed(ActionEvent e){
			for(int i=0;i<25;i++){
				if(e.getSource()==NumButton[i]){
					NumButton[i].setEnabled(false);
					NumButton[i].setFont(new Font("Serif", Font.BOLD, 30));
					check[i]=1;
				}
			}
		}
	}
}
class Bingo_start extends JFrame{
	
	private JPanel contentPane;
	JTextField userName;
	JTextField IP_addr;
	JTextField port;
	JButton connect_btn;
	JButton exit_btn;
	
	Bingo_start(){
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
		connect_btn.setBounds(12, 103, 120, 23);
		contentPane.add(connect_btn);
		
		exit_btn = new JButton("Exit");
		exit_btn.setBounds(152, 103, 120, 23);
		contentPane.add(exit_btn);
		
		this.setVisible(true);
	}
	public void connect() {
		try {
			BingoBoard bb = new BingoBoard();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}