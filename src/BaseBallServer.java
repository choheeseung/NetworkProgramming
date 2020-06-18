import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.SocketTimeoutException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class BaseBallServer extends JFrame implements Runnable{
	static Vector<RMIServerImpl> clientList;
	private JPanel contentPane;
	static JTextArea log;
	
	/*Multi Thread*/
	private BaseBallServerRunnable clients[] = new BaseBallServerRunnable[3];
	public int clientCount = 0;
	//private static int Port = -1;
	static String Server ="";
	static int Port = 0000;
	Map<Integer, String> answer;
	
	BaseBallServer(String server, int port) throws RemoteException {
		/*View*/
		super("BASEBALL SERVER");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 430);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		log = new JTextArea(20, 50);
		log.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(log);
		scrollPane.setBounds(400, 50, 368, 178);
		contentPane.add(scrollPane);
		
		setVisible(true);
		
		/**/
		this.Server = server;
		this.Port = port;
		answer = new HashMap<Integer, String>();
	}
	
	BaseBallServer() throws RemoteException {
		super();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		final KeyStore ks;
		final KeyManagerFactory kmf;
		final SSLContext sc;
		
		//final String runRoot = "C:\\Users\\Heeseung\\git\\NetworkProgramming\\bin\\";  // root change : your system root
		final String runRoot = "C:\\Users\\geun\\NP\\NetworkProgramming\\bin\\";  // root change : your system root
		
		SSLServerSocketFactory ssf = null;
		SSLServerSocket s = null;
		String ksName = runRoot +".keystore\\SSLSocketServerKey";
		
		char keyStorePass[] = "123456".toCharArray();
		char keyPass[] = "123456".toCharArray();
		try {
			ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(ksName),keyStorePass);
			
			kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, keyPass);
			
			sc = SSLContext.getInstance("TLS");
			sc.init(kmf.getKeyManagers(), null, null);
			
			/* SSLServerSocket */
			ssf = sc.getServerSocketFactory();
			s = (SSLServerSocket) ssf.createServerSocket(Port);
			log.append ("Server started: socket created on " + Port+"\n");
			printServerSocketInfo(s);
			
			RMIServerImpl server = new RMIServerImpl(this);
			Naming.rebind("rmi://"+Server+"/BaseBall", server);
			log.append("RMI connect");
			
			while (true) {
				addClient(s);
			}
		} catch (BindException b) {
			log.append("Can't bind on: "+Port);
		} catch (SSLException se) {
			log.append("SSL problem, exit~");
			try {
				s.close();
			} catch (IOException i) {
			}
		} catch (Exception e) {
			log.append("What?? exit~");
			try {
				s.close();
			} catch (IOException i) {
			}
		} finally {
			try {
				if (s != null) s.close();
			} catch (IOException i) {
				System.out.println(i);
			}
		}
	}
	public int whoClient(int clientID) {
		for (int i = 0; i < clientCount; i++)
			if (clients[i].getClientID() == clientID)
				return i;
		return -1;
	}
	public void putClient(int clientID, String inputLine) {
		for (int i = 0; i < clientCount; i++)
			if (clients[i].getClientID() == clientID) {
				log.append("writer: "+clientID+"\n");
			} else {
				log.append("write: "+clients[i].getClientID()+"\n");
				clients[i].out.println(inputLine);
				clients[i].out.flush();
			}
		log.setCaretPosition(log.getText().length());
	}
	public void addClient(SSLServerSocket serverSocket) {
		SSLSocket clientSocket = null;
		
		if (clientCount < clients.length) { 
			try {
				clientSocket = (SSLSocket)serverSocket.accept();
				printSocketInfo(clientSocket);
				//clientSocket.setSoTimeout(40000); // 1000/sec
			} catch (IOException i) {
				log.append("Accept() fail: "+i);
			}
			clients[clientCount] = new BaseBallServerRunnable(this, clientSocket);
			new Thread(clients[clientCount]).start();
			clientCount++;
			log.append ("Client connected: " + clientSocket.getPort()+", CurrentClient: " + clientCount + "\n");
			log.setCaretPosition(log.getText().length());
		} else {
			try {
				SSLSocket dummySocket = (SSLSocket)serverSocket.accept();
				BaseBallServerRunnable dummyRunnable = new BaseBallServerRunnable(this, dummySocket);
				new Thread(dummyRunnable);
				dummyRunnable.out.write(dummySocket.getPort() + " < Sorry maximum user connected now");
				log.append("Client refused: maximum connection " + clients.length + " reached.");
				log.setCaretPosition(log.getText().length());
				dummyRunnable.close();
			} catch (IOException i) {
				i.printStackTrace();
			}	
		}
	}
	public synchronized void delClient(int clientID) {
		int pos = whoClient(clientID);
		BaseBallServerRunnable endClient = null;
	      if (pos >= 0) {
	    	   endClient = clients[pos];
	    	  if (pos < clientCount-1)
	    		  for (int i = pos+1; i < clientCount; i++)
	    			  clients[i-1] = clients[i];
	    	  clientCount--;
	    	  log.append("Client removed: " + clientID  + " at clients[" + pos +"], CurrentClient: " + clientCount+"\n");
	    	  log.setCaretPosition(log.getText().length());
	    	  endClient.close();
	      }
	}
	private static void printSocketInfo(SSLSocket s) {
		log.append("Socket class: "+s.getClass()+"\n");
		log.append("   Remote address = "
				+s.getInetAddress().toString()+"\n");
		log.append("   Remote port = "+s.getPort()+"\n");
		log.append("   Local socket address = "
				+s.getLocalSocketAddress().toString()+"\n");
		log.append("   Local address = "
				+s.getLocalAddress().toString()+"\n");
		log.append("   Local port = "+s.getLocalPort()+"\n");
		log.append("   Need client authentication = "+s.getNeedClientAuth()+"\n");
		SSLSession ss = s.getSession();
		log.append("   Cipher suite = "+ss.getCipherSuite()+"\n");
		log.append("   Protocol = "+ss.getProtocol()+"\n");
	}
	private static void printServerSocketInfo(SSLServerSocket s) {
		log.append("Server socket class: "+s.getClass()+"\n");
		log.append("   Server address = "+s.getInetAddress().toString()+"\n");
		log.append("   Server port = "+s.getLocalPort()+"\n");
		log.append("   Need client authentication = "+s.getNeedClientAuth()+"\n");
		log.append("   Want client authentication = "+s.getWantClientAuth()+"\n");
		log.append("   Use client mode = "+s.getUseClientMode()+"\n");
		log.setCaretPosition(log.getText().length());
	}

	public void setAnswer(int clientID, String substring) {
		for (int i = 0; i < clientCount; i++)
		{
			if (clients[i].getClientID() != clientID) {
				answer.put(clients[i].getClientID(), substring);
				System.out.println(clientID+", "+clients[i].getClientID()+", "+substring);
			} 
		}
	}
	public synchronized void SendResult(int clientID) {
		for (int i = 0; i < clientCount; i++)
		{
			if (clients[i].getClientID() == clientID)
			{
				clients[i].out.println("$You are WIN!!");
			} 
			else
			{
				clients[i].out.println("$You are LOSE.");
			}
			clients[i].out.flush();
		}
	}
	public synchronized void NewGame() {
		for (int i = 0; i < clientCount; i++)
		{
			clients[i].out.println("$NEWGAME");
			clients[i].out.flush();
		}
	}
}
class BaseBallServerRunnable implements Runnable {
	
	protected BaseBallServer bingoServer = null;
	protected SSLSocket clientSocket = null;
	protected PrintWriter out = null;
	protected BufferedReader in = null;
	public int clientID = -1;
	
	public BaseBallServerRunnable (BaseBallServer server, SSLSocket socket) {
		this.bingoServer = server;
		this.clientSocket = socket;
		clientID = clientSocket.getPort();
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch(IOException i){
			
		}
	}
	public void run() {
		try {
			String inputLine;
			while((inputLine = in.readLine())!= null) {
				if (inputLine.charAt(0) == '#')
				{
					//ÃßÃø Number
					bingoServer.putClient(getClientID(), inputLine);
				}
				else if (inputLine.charAt(0) == '@')
				{
					//UserÀÇ answer
					bingoServer.setAnswer(getClientID(), inputLine.substring(1));
					out.println("@"+getClientID());
					out.flush();
				}
				else if (inputLine.equals("$NEWGAME"))
				{
					bingoServer.answer.clear();
					bingoServer.NewGame();
				}
				else
				{
					bingoServer.putClient(getClientID(), inputLine);
				}
				if (inputLine.equalsIgnoreCase("Bye."))
					break;
			}
			bingoServer.delClient(getClientID());
		} catch(SocketTimeoutException ste) {
			System.out.println("Socket timeout Occurred, force close() : "+getClientID());
			bingoServer.delClient(getClientID());
		}	catch (IOException e) {
			// TODO Auto-generated catch block
			bingoServer.delClient(getClientID());
		}
	}
	public int getClientID() {
		return clientID;
	}
	
	public void close() {
		try {
			if(in != null) in.close();
			if(out != null) out.close();
			if(clientSocket != null) clientSocket.close();
		} catch (IOException i) {
			
		}
	}
}