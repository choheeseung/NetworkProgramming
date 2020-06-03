public class BingoControl {
	BingoModel m;
	BingoView v;
	
	BingoControl(BingoModel model, BingoView view){
		this.m = model;
		this.v = view;
	}
	
	public void connect() {
		System.out.println("connect");
		v.bs.setVisible(false);
		v.bb.setVisible(true);
	}
	
	public void createServer() {
		
		System.out.println("createServer");
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