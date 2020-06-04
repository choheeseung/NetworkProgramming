import java.io.IOException;
import java.net.BindException;
import java.net.Socket;

/**
 * Bingo의 Control과 관련된 클래스
 * 
 */
public class BingoControl {
	
	BingoModel m;
	int serverPort;
	String userName = "";
	String eServer = "";
	int ePort = 0000;
	Thread th;
	Socket chatSocket = null;
	ClientSender cs;
	
	BingoControl(BingoModel model){
		this.m = model;
	}
	
	public void connect() {
		
	}
	
	public void createServer() {
		
	}
	
	public void Random() {
		System.out.println("Random");
	}
	
	public void Ready() {
		System.out.println("Ready");
	}
	
	public void Send() {
		System.out.println("Send");
	}
}