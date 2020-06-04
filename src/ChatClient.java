import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

class ClientSender implements Runnable {
	
	private Socket chatSocket = null;
	PrintWriter out = null;
	BingoBoard bb;
	
	DataInputStream dis;
	DataOutputStream dos;	
	
	ClientSender(Socket socket, BingoBoard bb){
		this.chatSocket = socket;
		this.bb = bb;
	}
	
	public void run() {
		try {
			//데이터 전송을 위한 스트림 생성(입추력 모두)
			//보조스트림으로 만들어서 데이터전송 작업을 편하게 ※다른 보조스트림 사용
			out = new PrintWriter(chatSocket.getOutputStream(), true);
			bb.SendButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					sendMessage();
				}
			});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	
	void sendMessage() {
		String msg = bb.textField.getText();
		bb.textField.setText("");
		bb.textArea.append(chatSocket.getLocalPort()+ " : " +msg+"\n");
		
		Thread t = new Thread() {

			@Override

			public void run() {

				try { //UTF = 유니코드의 규약(포맷), 한글 깨지지 않게 해줌
					out = new PrintWriter(chatSocket.getOutputStream(), true);
					out.println(msg);
					out.flush(); //계속 채팅 위해 close()하면 안됨				
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		};

		t.start();			
	}
	
}
class ClientReceiver implements Runnable{
	private Socket chatSocket = null;
	BingoBoard bb;
	
	ClientReceiver(Socket socket, BingoBoard bb){
		this.chatSocket = socket;
		this.bb = bb;
	}
	
	public void run() {
		while (chatSocket.isConnected()) {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
				String readSome = null;
				while((readSome = in.readLine())!= null) {
					bb.textArea.append(readSome + "\n");
				}
				in.close();
				chatSocket.close();
			} catch(IOException i) {
				try {
					if(in != null)in.close();
					if(chatSocket != null) chatSocket.close();
				} catch(IOException e) {
				}
				System.out.println("leave.");
				System.exit(1);
			}
		}
	}
}
class BingoBoard extends JFrame {

	private JPanel contentPane;
	private JButton[] NumButton=new JButton[25];
	private int[] check=new int[25];
	
	JButton SendButton;
	JButton ReadyButton;
	JButton RanButton;
	
	JPanel panel;
	JTextField textField;
	JTextArea textArea;
	
	BingoBoard() {
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
	
		RanButton = new JButton("Random Shuffle");
		RanButton.setBounds(20, 410, 170, 70);
		RanButton.setFont(new Font("Serif", Font.BOLD, 20));
		contentPane.add(RanButton);
	
		ReadyButton = new JButton("READY");
		ReadyButton.setBounds(200, 410, 170, 70);
		ReadyButton.setFont(new Font("Serif", Font.BOLD, 25));
		contentPane.add(ReadyButton);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(400, 50, 368, 178);
		contentPane.add(scrollPane);
		
		textField = new JTextField();
		textField.setBounds(400, 240, 276, 39);
		contentPane.add(textField);
		textField.setColumns(10);
		
		SendButton = new JButton("Send");
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
