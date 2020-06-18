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
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

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
import javax.swing.SwingConstants;

public class BaseBallClient extends JFrame implements Runnable {

//	BaseBallModel m = new BaseBallModel();
	RMIServer server = null;

	static String eServer = "";
	static int ePort = 0000;
	String userName = "";
	int userID = 0000;
	

	// SSL
	SSLSocketFactory f = null;
	SSLSocket c = null;

	static InetAddress inetaddr = null;

	// View
	private JPanel contentPane;

	JLabel Answerlabel;
	
	JTextField MessageField;
	JTextArea MessageArea;
	JTextField NumField;
	JTextArea YourArea;
	JTextArea MyArea;
	JTextField AnswerField;

	JButton btnSend;
	JButton btnGo;
	JButton btnReady;
	JButton btnNew;
	/*
	 * public int[] answer = new int[3]; public int[] num = new int[3];
	 */

	/**
	 * Create the frame.
	 */
	BaseBallClient(String userName, String server, int port) {
		super("BASEBALL GAME CLIENT");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 806, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		MessageField = new JTextField();
		MessageField.setBounds(509, 387, 188, 56);
		contentPane.add(MessageField);
		MessageField.setFont(new Font("Serif", Font.PLAIN, 14));
		MessageField.setColumns(10);

		btnSend = new JButton("Send");
		btnSend.setBounds(698, 387, 78, 56);
		btnSend.setFont(new Font("Serif", Font.PLAIN, 14));
		contentPane.add(btnSend);
		
		MyArea = new JTextArea();
		MyArea.setBounds(12, 120, 239, 259);
		MyArea.setFont(new Font("Serif", Font.PLAIN, 14));
		contentPane.add(MyArea);

		MessageArea = new JTextArea();
		MessageArea.setBounds(509, 64, 267, 315);
		contentPane.add(MessageArea);
		MessageArea.setFont(new Font("Serif", Font.PLAIN, 14));
		MessageArea.setEditable(false);
		
		NumField = new JTextField();
		NumField.setBounds(12, 387, 373, 56);
		contentPane.add(NumField);
		NumField.setFont(new Font("Serif", Font.PLAIN, 14));
		NumField.setColumns(10);

		YourArea = new JTextArea();
		YourArea.setBounds(258, 120, 239, 259);
		YourArea.setFont(new Font("Serif", Font.PLAIN, 14));
		contentPane.add(YourArea);

		JLabel Chat = new JLabel("Chatting : "+userName);
		Chat.setFont(new Font("Serif", Font.BOLD, 25));
		Chat.setBounds(509, 10, 267, 44);
		Chat.setHorizontalAlignment(JLabel.CENTER);
		contentPane.add(Chat);

		btnGo = new JButton("GO");
		btnGo.setBounds(392, 385, 105, 58);
		btnGo.setFont(new Font("Serif", Font.PLAIN, 14));
		contentPane.add(btnGo);

		btnReady = new JButton("Ready");
		btnReady.setBounds(258, 37, 115, 47);
		btnReady.setFont(new Font("Serif", Font.PLAIN, 14));
		contentPane.add(btnReady);
		
		btnNew = new JButton("NewGame");
		btnNew.setBounds(382, 37, 115, 47);
		btnNew.setFont(new Font("Serif", Font.PLAIN, 14));
		contentPane.add(btnNew);

		AnswerField = new JTextField();
		AnswerField.setColumns(10);
		AnswerField.setBounds(12, 38, 239, 46);
		contentPane.add(AnswerField);
		AnswerField.setFont(new Font("Serif", Font.PLAIN, 14));

		Answerlabel = new JLabel();
		Answerlabel.setBounds(12, 38, 239, 46);
		contentPane.add(Answerlabel);
		AnswerField.setFont(new Font("Serif", Font.PLAIN, 14));
		Answerlabel.setVisible(false);
		
		JLabel Info = new JLabel("Enter Non-duplicate 3 or 4 digits Numbers and Press READY");
		Info.setBounds(12, 10, 485, 18);
		Info.setFont(new Font("Serif", Font.BOLD, 17));
		contentPane.add(Info);
		
		JLabel lbMy = new JLabel("My Area");
		lbMy.setBounds(12, 94, 239, 15);
		lbMy.setFont(new Font("Serif", Font.PLAIN, 14));
		contentPane.add(lbMy);
		
		JLabel lbYour = new JLabel("Opponent Area");
		lbYour.setBounds(258, 94, 239, 15);
		lbYour.setFont(new Font("Serif", Font.PLAIN, 14));
		contentPane.add(lbYour);

		setVisible(true);
		this.userName = userName;
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

			System.out.println("before");
			server = (RMIServer) Naming.lookup("rmi://" + eServer + "/BaseBall");
			System.out.println("after");
			
		} catch (BindException b) {
			MessageArea.append("Can't bind on: " + ePort);
			System.exit(1);
		} catch (IOException i) {
			System.out.println(i);
			System.exit(1);
		} catch (NotBoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		new Thread(new ClientReceiver(c)).start();
		new Thread(new ClientSender(c)).start();
	}

	class ClientSender implements Runnable {
		private SSLSocket chatSocket = null;
		PrintWriter out = null;

		ClientSender(SSLSocket socket) {
			this.chatSocket = socket;
		}

		public void run() {

			try {
				out = new PrintWriter(chatSocket.getOutputStream(), true);

				btnSend.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						String userInput = userName+ " : " +MessageField.getText();
						MessageField.setText("");
						MessageArea.append(userInput + "\n");
						out.println(userInput);
						out.flush();
					}
				});
				btnGo.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						if (!NumField.getText().equals("")) { 
							String userInput = NumField.getText();
							String result = "";
							NumField.setText("");
							try {
								result = server.CheckStatus(userID, userInput);
							} catch (RemoteException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							MyArea.append(userInput + " : " + result + "\n");
							MyArea.setCaretPosition(MessageArea.getText().length());
							out.println("#"+userInput + " : " + result);
							out.flush();
							btnGo.setEnabled(false);
						}
					}
				});
				btnReady.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						if (!AnswerField.getText().equals("")) {
							String userAnswer = AnswerField.getText();
							char [] tmp = userAnswer.toCharArray();
							boolean checkduplicate = false;
							/* duplicate check */
							for(int i = 0 ; i < userAnswer.length(); i++)
								for(int j = userAnswer.length()-1 ; j > i; j--)
									if (tmp[i] == tmp[j])
										checkduplicate = true;
							
							if (checkduplicate) {
								MessageArea.append("Please Input Non-Duplicate Number\n");
							}
							else
							{
								Answerlabel.setText(userAnswer);
								AnswerField.setText("");
								AnswerField.setVisible(false);
								Answerlabel.setVisible(true);
								out.println("@"+userAnswer);
								out.flush();
								btnReady.setEnabled(false);
							}
						}
						else
						{
							MessageArea.append("Please Input Non-Duplicate Number\n");
						}
					}
				});
				btnNew.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						out.println("$NEWGAME");
						out.flush();
					}
					
				});
			} catch (IOException i) {
				try {
					if (chatSocket != null)
						chatSocket.close();
				} catch (IOException e) {

				}
				System.exit(1);
			}
		}
	}

	class ClientReceiver implements Runnable {
		private SSLSocket chatSocket = null;

		ClientReceiver(SSLSocket socket) {
			this.chatSocket = socket;
		}

		public void run() {
			while (chatSocket.isConnected()) {
				BufferedReader in = null;
				if (btnSend.getActionListeners() != null) {
					try {
						in = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
						String readSome = null;
						while ((readSome = in.readLine()) != null) {			
							if (readSome.equals("$NEWGAME"))
							{
								AnswerField.setVisible(true);
								Answerlabel.setText("");
								Answerlabel.setVisible(false);
								YourArea.setText("");
								MyArea.setText("");
								btnReady.setEnabled(true);
								btnGo.setEnabled(true);
							}
							else if (readSome.charAt(0) == '#')
							{
								YourArea.append(readSome.substring(1) + "\n");
								YourArea.setCaretPosition(MessageArea.getText().length());
								btnGo.setEnabled(true);
							}
							else if(readSome.charAt(0) == '@') //´ä
							{
								userID = Integer.parseInt(readSome.substring(1));
							}
							else if(readSome.charAt(0) == '$')
							{
								MessageArea.append(readSome.substring(1) + "\n");
								btnGo.setEnabled(false);
							}
							else 
							{
								MessageArea.append(readSome + "\n");
								MessageArea.setCaretPosition(MessageArea.getText().length());
							}
							
						}
						in.close();
						chatSocket.close();
					} catch (IOException i) {
						try {
							if (in != null)
								in.close();
							if (chatSocket != null)
								chatSocket.close();
						} catch (IOException e) {
						}
						MessageArea.append("leave.");
						System.exit(1);
					}
				}
			}
		}
	}
}
