import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.util.ArrayList;

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

public class BingoServer extends JFrame implements Runnable{
	
	private JPanel contentPane;
	static JTextArea log;
	
	/*Multi Thread*/
	private BingoServerRunnable clients[] = new BingoServerRunnable[3];
	public int clientCount = 0;
	private int Port = -1;
	
	//SSL
	KeyStore ks;
	KeyManagerFactory kmf;
	SSLContext sc;
	
	final String runRoot = "C:\\Users\\geun\\NP\\NetworkProgramming\\bin\\";  // root change : your system root

	SSLServerSocketFactory ssf = null;
	SSLServerSocket s = null;
	SSLSocket c = null;
	
	ArrayList<String> list = new ArrayList <>();
	
	BufferedWriter w = null;
	BufferedReader r = null;
	
	BingoServer(int port){
		/*View*/
		super("BINGO SERVER");
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
		this.Port = port;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

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
			log.append("Server started: socket created on " + Port+"\n");
			printServerSocketInfo(s);
			
			while(true) {
				addClient(s);
			}
			
		} catch (SSLException se) {
			log.append("SSL problem, exit~");
			try {
				w.close();
				r.close();
				s.close();
				c.close();
			} catch (IOException i) {
			}
		} catch (Exception e) {
			log.append("What?? exit~");
			try {
				w.close();
				r.close();
				s.close();
				c.close();
			} catch (IOException i) {
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
			}
	}
	public void addClient(SSLServerSocket serverSocket) {
		SSLSocket clientSocket = null;
		
		if (clientCount < clients.length) { 
			try {
				clientSocket = (SSLSocket)serverSocket.accept();
				printSocketInfo(clientSocket);
				//clientSocket.setSoTimeout(40000); // 1000/sec
			} catch (IOException i) {
				log.append ("Accept() fail: "+i+"\n");
			}
			clients[clientCount] = new BingoServerRunnable(this, clientSocket);
			new Thread(clients[clientCount]).start();
			clientCount++;
			log.append ("Client connected: " + clientSocket.getPort()
					+", CurrentClient: " + clientCount+"\n");
		} else {
			try {
				SSLSocket dummySocket = (SSLSocket)serverSocket.accept();
				BingoServerRunnable dummyRunnable = new BingoServerRunnable(this, dummySocket);
				new Thread(dummyRunnable);
				dummyRunnable.out.println(dummySocket.getPort()
						+ " < Sorry maximum user connected now"+"\n");
				log.append("Client refused: maximum connection "
						+ clients.length + " reached."+"\n");
				dummyRunnable.close();
			} catch (IOException i) {
				i.printStackTrace();
			}	
		}
	}
	public synchronized void delClient(int clientID) {
		int pos = whoClient(clientID);
		BingoServerRunnable endClient = null;
	      if (pos >= 0) {
	    	   endClient = clients[pos];
	    	  if (pos < clientCount-1)
	    		  for (int i = pos+1; i < clientCount; i++)
	    			  clients[i-1] = clients[i];
	    	  clientCount--;
	    	  log.append("Client removed: " + clientID
	    			  + " at clients[" + pos +"], CurrentClient: " + clientCount+"\n");
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
		System.out.println("   Need client authentication = "+s.getNeedClientAuth()+"\n");
		SSLSession ss = s.getSession();
		System.out.println("   Cipher suite = "+ss.getCipherSuite()+"\n");
		System.out.println("   Protocol = "+ss.getProtocol()+"\n");
	}
	private static void printServerSocketInfo(SSLServerSocket s) {
		log.append("Server socket class: "+s.getClass()+"\n");
		log.append("   Server address = "+s.getInetAddress().toString()+"\n");
		log.append("   Server port = "+s.getLocalPort()+"\n");
		log.append("   Need client authentication = "+s.getNeedClientAuth()+"\n");
		log.append("   Want client authentication = "+s.getWantClientAuth()+"\n");
		log.append("   Use client mode = "+s.getUseClientMode()+"\n");
	}
}
class BingoServerRunnable implements Runnable {
	protected BingoServer bingoServer = null;
	protected SSLSocket clientSocket = null;
	protected PrintWriter out = null;
	protected BufferedReader in = null;
	public int clientID = -1;
	
	public BingoServerRunnable (BingoServer server, SSLSocket socket) {
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
				bingoServer.putClient(getClientID(), getClientID() + " : "+inputLine);
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