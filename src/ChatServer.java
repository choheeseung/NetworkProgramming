import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class ChatServer implements Runnable {

	public static ArrayList <String> Log = new ArrayList<> ();
	
	private ChatServerRunnable clients[] = new ChatServerRunnable[3];
	public int clientCount = 0;
	
	private int ePort = -1;
	public ChatServer(String port) {
		this.ePort = Integer.parseInt(port);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(ePort);
			Log.add("Server started: socket created on"  + ePort);
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
				System.out.println("writer: "+clientID);
			} else {
				System.out.println("write: "+clients[i].getClientID());
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
			clients[clientCount] = new ChatServerRunnable(this, clientSocket);
			new Thread(clients[clientCount]).start();
			clientCount++;
			Log.add("Client connected: " + clientSocket.getPort()+", CurrentClient: " + clientCount);
		} else {
			try {
				Socket dummySocket = serverSocket.accept();
				ChatServerRunnable dummyRunnable = new ChatServerRunnable(this, dummySocket);
				new Thread(dummyRunnable);
				dummyRunnable.out.println(dummySocket.getPort()
						+ " < Sorry maximum user connected now");
				System.out.println("Client refused: maximum connection "
						+ clients.length + " reached.");
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
	    	  System.out.println("Client removed: " + clientID
	    			  + " at clients[" + pos +"], CurrentClient: " + clientCount);
	    	  endClient.close();
	      }
	}

}
class ChatServerRunnable implements Runnable {
	protected ChatServer chatServer = null;
	protected Socket clientSocket = null;
	protected PrintWriter out = null;
	protected BufferedReader in = null;
	public int clientID = -1;
	
	public ChatServerRunnable (ChatServer server, Socket socket) {
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