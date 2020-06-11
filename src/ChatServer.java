import java.io.*;
import java.net.BindException;
import java.net.SocketTimeoutException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.swing.JOptionPane;

public class ChatServer implements Runnable {

	public static ArrayList <String> Log = new ArrayList<> ();
	private ChatServerRunnable clients[] = new ChatServerRunnable[3];
	public int clientCount = 0;
	BingoServerView bsv = null;
	private int ePort = -1;
	
	private KeyStore ks;
    private KeyManagerFactory kmf;
    private SSLContext sc;
    SSLServerSocketFactory sslServerSocketFactory = null;
    SSLServerSocket sslServerSocket = null;
    SSLSocket sslSocket = null;
	
    final String runRoot = "C:\\Users\\Heeseung\\git\\NetworkProgramming\\bin\\";  // root change : your system root
    
	public ChatServer(String port) {
		this.ePort = Integer.parseInt(port);
		bsv = new BingoServerView();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		SSLServerSocket serverSocket = null;
		String ksName = runRoot + ".keystore\\SSLSocketServerKey";
		
		char keyStorePass[] = "123456".toCharArray();
		char keyPass[] = "123456".toCharArray();
		
		try {
			ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(ksName), keyStorePass);
			
			kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, keyPass);
			
			sc = SSLContext.getInstance("TLS");
			sc.init(kmf.getKeyManagers(), null, null);
			
			sslServerSocketFactory = sc.getServerSocketFactory();
			sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(ePort);
			
			bsv.log.append("Server started: socket created on"  + ePort+"\n");
			bsv.log.setCaretPosition(bsv.log.getText().length());
			while(true) {
				addClient(serverSocket);
			}
		} catch (BindException b) {
			JOptionPane.showMessageDialog(null, "Can't bind on: "+ePort, "ERROR", JOptionPane.WARNING_MESSAGE);
		} catch (IOException i) {
			JOptionPane.showMessageDialog(null, i, "ERROR", JOptionPane.WARNING_MESSAGE);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null) serverSocket.close();
			} catch (IOException i) {
				JOptionPane.showMessageDialog(null, i, "ERROR", JOptionPane.WARNING_MESSAGE);
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
				bsv.log.append("writer: "+clientID + "\n");
			} else {
				bsv.log.append("write: "+clients[i].getClientID()+ "\n");
				clients[i].out.println(inputLine);
			}
	}
	public void addClient(SSLServerSocket serverSocket) {
		SSLSocket clientSocket = null;
		
		if (clientCount < clients.length) { 
			try {
				clientSocket = (SSLSocket) sslServerSocket.accept();
				clientSocket.setSoTimeout(40000); // 1000/sec
			} catch (IOException i) {
				JOptionPane.showMessageDialog(null, i, "ERROR", JOptionPane.WARNING_MESSAGE);
			}
			clients[clientCount] = new ChatServerRunnable(this, clientSocket);
			new Thread(clients[clientCount]).start();
			clientCount++;
			bsv.log.append("Client connected: " + clientSocket.getPort()+", CurrentClient: " + clientCount+"\n");
			bsv.log.setCaretPosition(bsv.log.getText().length());
		} else {
			try {
				SSLSocket dummySocket = (SSLSocket) serverSocket.accept();
				ChatServerRunnable dummyRunnable = new ChatServerRunnable(this, dummySocket);
				new Thread(dummyRunnable);
				dummyRunnable.out.println(dummySocket.getPort()
						+ " < Sorry maximum user connected now");
				bsv.log.append("Client refused: maximum connection "
						+ clients.length + " reached.");
				bsv.log.setCaretPosition(bsv.log.getText().length());
				dummyRunnable.close();
			} catch (IOException i) {
				System.out.println(i);
			}	
		}
	}
	public synchronized void delClient(int clientID) {
		int pos = whoClient(clientID);
		ChatServerRunnable endClient = null;
	      if (pos >= 0) {
	    	   endClient = clients[pos];
	    	  if (pos < clientCount-1)
	    		  for (int i = pos+1; i < clientCount; i++)
	    			  clients[i-1] = clients[i];
	    	  clientCount--;
	    	  bsv.log.append("Client removed: " + clientID
	    			  + " at clients[" + pos +"], CurrentClient: " + clientCount);
	    	  bsv.log.setCaretPosition(bsv.log.getText().length());
	    	  endClient.close();
	      }
	}	
}
class ChatServerRunnable implements Runnable {
	protected ChatServer chatServer = null;
	protected SSLSocket clientSocket = null;
	protected PrintWriter out = null;
	protected BufferedReader in = null;
	public int clientID = -1;
	
	public ChatServerRunnable (ChatServer server, SSLSocket socket) {
		this.chatServer = server;
		this.clientSocket = socket;
		clientID = clientSocket.getPort();
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch(IOException i){
			JOptionPane.showMessageDialog(null, i, "ERROR", JOptionPane.WARNING_MESSAGE);
		}
	}
	public void run() {
		try {
			String inputLine;
			while((inputLine = in.readLine())!= null) {
				chatServer.putClient(getClientID(), getClientID() + " : "+inputLine);
				if (inputLine.equalsIgnoreCase("Bye."))
					break;
			}
			chatServer.delClient(getClientID());
		} catch(SocketTimeoutException ste) {
			System.out.println("Socket timeout Occurred, force close() : "+getClientID());
			chatServer.delClient(getClientID());
		}	catch (IOException e) {
			// TODO Auto-generated catch block
			chatServer.delClient(getClientID());
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