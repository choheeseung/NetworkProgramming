import java.rmi.*;

public interface RMIServer extends Remote {
	public String CheckStatus(int UserID, String data) throws RemoteException;
}
