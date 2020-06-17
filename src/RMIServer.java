import java.rmi.*;

public interface RMIServer extends Remote {
	public String CheckStatus(String answer, String data) throws RemoteException;
}
