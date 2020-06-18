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
	int [] fromClientNumber;
	int [] fromServerNumber;

	@Override
	public String CheckStatus(int UserID, String num) throws RemoteException {
		// TODO Auto-generated method stub
		String answer = bs.answer.get(UserID);
		num = num.replaceAll("[^0-9]", "");
		answer = answer.replaceAll("[^0-9]", "");
		
		fromClientNumber = new int[num.length()];
		fromServerNumber = new int[answer.length()];
		for(int i=0; i<num.length(); i++) {
			fromClientNumber[i] = num.charAt(i) - '0';
			fromServerNumber[i] = answer.charAt(i) - '0';	
		}
		int strike = 0;
		int ball = 0;
		for(int i=0;i<num.length();i++) {
			for(int j=0;j<num.length();j++) {
				if(fromClientNumber[j] == fromServerNumber[i]) {
					if(i==j)
						strike++;
					else
						ball++;
				}
			}
		}
		String result = strike +" strike "+ ball +" ball";
		if (strike == answer.length()) {
			bs.SendResult(UserID);
		} 
		else if (strike < 1 && ball < 1)
		{
			result = "Out";
		}
		return result;
	}
	
}
