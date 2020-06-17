import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerImpl extends UnicastRemoteObject implements RMIServer{
	BaseBallServer bs;
	protected RMIServerImpl(BaseBallServer bs) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		this.bs = bs;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String CheckStatus(int UserID, String data) throws RemoteException {
		// TODO Auto-generated method stub
		
		return bs.answer.get(UserID);
	}
	
}
