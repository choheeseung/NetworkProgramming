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

	BaseBallModel m = new BaseBallModel();
	RMIServer server = null;

	static String eServer = "";
	static int ePort = 0000;

	// SSL
	SSLSocketFactory f = null;
	SSLSocket c = null;

	static InetAddress inetaddr = null;

	// View
	private JPanel contentPane;

	JTextField MessageField;
	JTextArea MessageArea;
	JTextField NumField;
	JTextArea YourArea;
	JTextArea MyArea;
	JTextField AnswerField;

	JButton btnSend;
	JButton btnGo;
	JButton btnReady;

	public int[] answer = new int[3];
	public int[] num = new int[3];

	/**
	 * Create the frame.
	 */
	BaseBallClient(String server, int port) {
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

		JLabel Chat = new JLabel("Chatting");
		Chat.setFont(new Font("Serif", Font.BOLD, 25));
		Chat.setBounds(598, 10, 164, 44);
		contentPane.add(Chat);

		btnGo = new JButton("GO");
		btnGo.setBounds(392, 385, 105, 58);
		btnGo.setFont(new Font("Serif", Font.PLAIN, 14));
		contentPane.add(btnGo);

		btnReady = new JButton("Ready");
		btnReady.setBounds(392, 37, 105, 47);
		btnReady.setFont(new Font("Serif", Font.PLAIN, 14));
		contentPane.add(btnReady);

		AnswerField = new JTextField();
		AnswerField.setColumns(10);
		AnswerField.setBounds(12, 38, 373, 46);
		contentPane.add(AnswerField);
		AnswerField.setFont(new Font("Serif", Font.PLAIN, 14));

		JLabel Info = new JLabel("Enter 3 Numbers and Press READY Button");
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
		// BufferedWriter out = null;
		PrintWriter out = null;

		ClientSender(SSLSocket socket) {
			this.chatSocket = socket;
		}

		public void run() {

			try {
				// out = new BufferedWriter(new
				// OutputStreamWriter(chatSocket.getOutputStream()));
				out = new PrintWriter(chatSocket.getOutputStream(), true);

				btnSend.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						String userInput = MessageField.getText();
						MessageField.setText("");
						MessageArea.append(chatSocket.getLocalPort() + " : " + userInput + "\n");
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
							for(int i=0; i<userInput.length(); i++) {
								num[i] = userInput.charAt(i) - '0';		
							}
							try {
								result = server.CheckStatus(m.userID, Arrays.toString(num));
							} catch (RemoteException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							MyArea.append(Arrays.toString(num) + " : " + result + "\n");
							MyArea.setCaretPosition(MessageArea.getText().length());
							out.println("#"+Arrays.toString(num) + " : " + result);
							out.flush();
						}
					}
				});
				btnReady.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						if (!AnswerField.getText().equals("")) {
							//m.setAnswer(AnswerField.getText());
							String userAnswer = AnswerField.getText();
							for(int i=0; i<userAnswer.length(); i++) {
								answer[i] = userAnswer.charAt(i) - '0';		
							}
							out.println("@"+Arrays.toString(answer));
							btnReady.setEnabled(false);
						}
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
							if (readSome.charAt(0) == '#')
							{
								YourArea.append(readSome.substring(1) + "\n");
								YourArea.setCaretPosition(MessageArea.getText().length());
							}
							else if(readSome.charAt(0) == '@') //´ä
							{
								m.userID = Integer.parseInt(readSome.substring(1));
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
