import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


public class BingoClient extends JFrame implements Runnable{
	
	static String eServer = "";
	static int ePort = 0000;
	
	//SSL
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	PrintWriter out = null;
	
	SSLSocketFactory f = null;
	SSLSocket c = null;
	
	BufferedWriter w = null;
	BufferedReader r = null;
	
	//View
	private JPanel contentPane;
	private JButton[] NumButton=new JButton[25];
	private int[] check=new int[25];
	
	JButton SendButton;
	JButton ReadyButton;
	JButton RanButton;
	
	JPanel panel;
	JTextField textField;
	JTextArea textArea;
	
	BingoClient(String server, int port){
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
		RanButton.setFont(new Font("Serif", Font.BOLD, 17));
		contentPane.add(RanButton);
		RanButton.addActionListener((ActionListener) new RanButtonEvent());
	
		ReadyButton = new JButton("READY");
		ReadyButton.setBounds(200, 410, 170, 70);
		ReadyButton.setFont(new Font("Serif", Font.BOLD, 25));
		contentPane.add(ReadyButton);
		ReadyButton.addActionListener((ActionListener) new ReadyButtonEvent());
		
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
			NumButton[i].setEnabled(false); //enable이 false였다가 Ready누르면 활성화
			NumButton[i].addActionListener((ActionListener) new NumButtonEvent());
		}
		setVisible(true);
		
		this.eServer = server;
		this.ePort = port;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			System.setProperty("javax.net.ssl.trustStore", "trustedcerts");
			System.setProperty("javax.net.ssl.trustStorePassword", "123456");
			
			f = (SSLSocketFactory) SSLSocketFactory.getDefault();
			c = (SSLSocket) f.createSocket(eServer, ePort);
			
			String[] supported = c.getSupportedCipherSuites();
			c.setEnabledCipherSuites(supported);
			c.startHandshake();
			
			new Thread(new ClientReceiver(c)).start();
			
			SendButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String msg = textField.getText();
					textField.setText("");
					textArea.append(c.getLocalPort()+ " : " +msg+"\n");
					Thread t = new Thread() {
						public void run() {
							try { //UTF = 유니코드의 규약(포맷), 한글 깨지지 않게 해줌
								out = new PrintWriter(c.getOutputStream(), true);
								out.println(msg);
								out.flush(); //계속 채팅 위해 close()하면 안됨				
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					};
					t.start();
				}
			});
			
			
		} catch(IOException io) {
			io.printStackTrace();
			try {
				r.close();
				c.close();
			} catch (IOException i) {
			}
		}
		
		//button listener
	}
	class ClientSender implements Runnable {
		private SSLSocket chatSocket = null;
		
		ClientSender(SSLSocket socket){
			this.chatSocket = socket;
		}
		
		public void run() {
			Scanner KeyIn = null;
			PrintWriter out = null;
			try {
				KeyIn = new Scanner(System.in);
				out = new PrintWriter(chatSocket.getOutputStream(),true);
				String userInput = "";
				System.out.println("Your are "+chatSocket.getLocalPort()+", Type Message (\"Bye.\" to leave) \n");
				while((userInput = KeyIn.nextLine()) != null) {
					out.println(userInput);
					out.flush();
					if(userInput.equalsIgnoreCase("Bye."))
						break;
				}
				KeyIn.close();
				out.close();
				chatSocket.close();
			} catch(IOException i) {
				try {
					if(out != null) out.close();
					if(KeyIn != null) KeyIn.close();
					if(chatSocket != null) chatSocket.close();
				} catch (IOException e) {
					
				}
				System.exit(1);
			}
		}
		
	}

	class ClientReceiver implements Runnable{
		private SSLSocket chatSocket = null;
		
		ClientReceiver(SSLSocket socket){
			this.chatSocket = socket;
		}
		
		public void run() {
			while (chatSocket.isConnected()) {
				BufferedReader in = null;
				try {
					in = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
					String readSome = null;
					while((readSome = in.readLine())!= null) {
						textArea.append(readSome+"\n");
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
	class RanButtonEvent implements ActionListener{
		public void actionPerformed(ActionEvent e) {
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
				panel.remove(NumButton[i]);
				panel.revalidate();
				panel.repaint();
				NumButton[i]=new JButton(""+nums[i]);
				NumButton[i].setFont(new Font("Serif", Font.BOLD, 25));
				panel.add(NumButton[i]);
				NumButton[i].setEnabled(false);
				NumButton[i].addActionListener((ActionListener) new NumButtonEvent());
			}
		}
	}
	
	/**Ready버튼 눌렀을 때
	 * 
	 * 빙고판 활성화, 랜덤버튼비활성화
	 *
	 */
	class ReadyButtonEvent implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			for(int i=0;i<25;i++) {
				NumButton[i].setEnabled(true);
			}
			RanButton.setEnabled(false);
			ReadyButton.setEnabled(false);
		}
	}
}
