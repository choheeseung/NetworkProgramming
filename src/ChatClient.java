import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class ClientSender implements Runnable {
	private Socket chatSocket = null;
	
	ClientSender(Socket socket){
		this.chatSocket = socket;
	}
	
	public void run() {
		Scanner KeyIn = null;
		PrintWriter out = null;
		try {
			KeyIn = new Scanner(System.in);
			out = new PrintWriter(chatSocket.getOutputStream(),true);
			String userInput = "";
			ChatServer.Log.add("Your are "+chatSocket.getLocalPort()+", Type Message (\"Bye.\" to leave) \n");
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
	private Socket chatSocket = null;
	
	ClientReceiver(Socket socket){
		this.chatSocket = socket;
	}
	
	public void run() {
		while (chatSocket.isConnected()) {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
				String readSome = null;
				while((readSome = in.readLine())!= null) {
					System.out.println(readSome);
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
