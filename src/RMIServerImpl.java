import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerImpl extends UnicastRemoteObject implements RMIServer{
	BaseBallServer bs = null;
	protected RMIServerImpl(BaseBallServer bs) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		this.bs = bs;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String CheckStatus(String answer, String data) throws RemoteException {
		// TODO Auto-generated method stub
		bs.CheckStatus("hi", "helo");
		return "This is CheckStatus";
	}
	
}
