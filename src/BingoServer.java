import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class BingoServer implements Runnable{
	
	
	public static ArrayList <String> Log = new ArrayList<> ();
	private BingoServerRunnable clients[] = new BingoServerRunnable[3];
	public int clientCount = 0;
	BingoServerView bsv = null;
	private int ePort = -1;
	
	public BingoServer(String port) {
		this.ePort = Integer.parseInt(port);
		bsv = new BingoServerView();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(ePort);
			bsv.log.append("Server started: socket created on"  + ePort+"\n");
			bsv.log.setCaretPosition(bsv.log.getText().length());
			while(true) {
				addClient(serverSocket);
			}
		} catch (BindException b) {
			JOptionPane.showMessageDialog(null, "Can't bind on: "+ePort, "ERROR", JOptionPane.WARNING_MESSAGE);
		} catch (IOException i) {
			JOptionPane.showMessageDialog(null, i, "ERROR", JOptionPane.WARNING_MESSAGE);
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
	public void addClient(ServerSocket serverSocket) {
		Socket clientSocket = null;
		
		if (clientCount < clients.length) { 
			try {
				clientSocket = serverSocket.accept();
				clientSocket.setSoTimeout(40000); // 1000/sec
			} catch (IOException i) {
				JOptionPane.showMessageDialog(null, i, "ERROR", JOptionPane.WARNING_MESSAGE);
			}
			clients[clientCount] = new BingoServerRunnable(this, clientSocket);
			new Thread(clients[clientCount]).start();
			clientCount++;
			bsv.log.append("Client connected: " + clientSocket.getPort()+", CurrentClient: " + clientCount+"\n");
			bsv.log.setCaretPosition(bsv.log.getText().length());
		} else {
			try {
				Socket dummySocket = serverSocket.accept();
				BingoServerRunnable dummyRunnable = new BingoServerRunnable(this, dummySocket);
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
		BingoServerRunnable endClient = null;
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
class BingoServerRunnable implements Runnable {
	protected BingoServer chatServer = null;
	protected Socket clientSocket = null;
	protected PrintWriter out = null;
	protected BufferedReader in = null;
	public int clientID = -1;
	
	public BingoServerRunnable (BingoServer server, Socket socket) {
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
class BingoServerView extends JFrame {
	private JPanel contentPane;
	JTextArea log;
	
	BingoServerView(){
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
	}
}
