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
import java.net.BindException;
import java.net.InetAddress;
import java.rmi.RemoteException;
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

public class BaseBallClient extends JFrame implements RMIClient, Runnable {
	
	static String eServer = "";
	static int ePort = 0000;
	
	//SSL	
	SSLSocketFactory f = null;
	SSLSocket c = null;

	static InetAddress inetaddr = null;
	
	//View
	private JPanel contentPane;
	
	JTextField MessageField;
	JTextArea MessageArea;
	JTextField NumField;
	JTextArea MyArea;
	JTextField AnswerField;
	
	JButton btnSend;
	JButton btnGo;
	JButton btnReady;

	/**
	 * Create the frame.
	 */
	BaseBallClient(String server, int port) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 802, 481);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel mine = new JLabel("My Board");
		mine.setBounds(312, 7, 169, 50);
		mine.setFont(new Font("Serif", Font.BOLD, 25));
		contentPane.add(mine);
		
		MessageField = new JTextField();
		MessageField.setBounds(509, 387, 188, 56);
		contentPane.add(MessageField);
		MessageField.setColumns(10);
		
		btnSend = new JButton("Send");
		btnSend.setBounds(698, 387, 78, 56);
		contentPane.add(btnSend);
		
		JTextArea YourArea = new JTextArea();
		YourArea.setBounds(12, 64, 239, 315);
		contentPane.add(YourArea);
		
		MessageArea = new JTextArea();
		MessageArea.setBounds(509, 64, 267, 315);
		contentPane.add(MessageArea);
		MessageArea.setEditable(false);
		
		NumField = new JTextField();
		NumField.setBounds(258, 387, 169, 56);
		contentPane.add(NumField);
		NumField.setColumns(10);
		
		MyArea = new JTextArea();
		MyArea.setBounds(258, 64, 239, 315);
		contentPane.add(MyArea);
		
		JLabel Chat = new JLabel("Chatting");
		Chat.setFont(new Font("Serif", Font.BOLD, 25));
		Chat.setBounds(598, 10, 164, 44);
		contentPane.add(Chat);
		
		btnGo = new JButton("GO");
		btnGo.setBounds(432, 385, 65, 58);
		contentPane.add(btnGo);
		
		btnReady = new JButton("Ready");
		btnReady.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AnswerField.setEnabled(false);
				btnReady.setEnabled(false);
			}
		});
		btnReady.setBounds(186, 385, 65, 58);
		contentPane.add(btnReady);
		
		AnswerField = new JTextField();
		AnswerField.setColumns(10);
		AnswerField.setBounds(12, 387, 169, 56);
		contentPane.add(AnswerField);
		
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
			inetaddr = InetAddress.getByName(eServer);
			c = (SSLSocket) f.createSocket(eServer, ePort);
			c.startHandshake();
			
		} catch (BindException b) {
			MessageArea.append("Can't bind on: "+ePort);
			System.exit(1);
		} catch (IOException i) {
			System.out.println(i);
			System.exit(1);
		}
		new Thread(new ClientReceiver(c)).start();
		new Thread(new ClientSender(c)).start();
		//button listener
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		BaseBallStart baseBallstart = new BaseBallStart();
		
		baseBallstart.userName.setText("user1");
		baseBallstart.IP_addr.setText("127.0.0.1");
		baseBallstart.port.setText("8888");

		eServer = baseBallstart.IP_addr.getText();
		ePort = Integer.parseInt(baseBallstart.port.getText());
		
		baseBallstart.connect_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(baseBallstart.userName.getText().equals("")||baseBallstart.IP_addr.getText().equals("")||baseBallstart.port.getText().equals(""))
					System.out.println("plz enter textfield!");
				else
				{
					System.out.println("connect");
					baseBallstart.setVisible(false);
					new Thread(new BaseBallClient(eServer, ePort)).start();
				}
			}
		});
	}

	class ClientSender implements Runnable {
		private SSLSocket chatSocket = null;
		//BufferedWriter out = null;
		PrintWriter out = null;
		ClientSender(SSLSocket socket){
			this.chatSocket = socket;
		}
		
		public void run() {
			
			try {
				//out = new BufferedWriter(new OutputStreamWriter(chatSocket.getOutputStream()));
				out = new PrintWriter(chatSocket.getOutputStream(),true);
				
				btnSend.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						String userInput = MessageField.getText();
						MessageField.setText("");
						MessageArea.append(chatSocket.getLocalPort()+" : " + userInput+"\n");
						out.println(userInput);
						out.flush();
					}
				});
				
			} catch(IOException i) {
				try {
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
						MessageArea.append(readSome+"\n");
						MessageArea.setCaretPosition(MessageArea.getText().length());
					}
					in.close();
					chatSocket.close();
				} catch(IOException i) {
					try {
						if(in != null)in.close();
						if(chatSocket != null) chatSocket.close();
					} catch(IOException e) {
					}
					MessageArea.append("leave.");
					System.exit(1);
				}
			}
		}
	}
	
}
