import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BingoGame {

	public static void main(String[] args) {
		BingoModel model = new BingoModel();
		BingoView view = new BingoView(model);
		BingoControl control = new BingoControl(model, view);
		
		/**
		 * BingoStart�� connect ��ư ������
		 */
		view.bs.connect_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(view.bs.userName.getText().equals("")||view.bs.IP_addr.getText().equals("")||view.bs.port.getText().equals(""))
					System.out.println("plz enter textfield!");
				else
					control.connect();
			}
		});
		/**
		 * BingoStart�� create ��ư ������
		 */
		view.bs.create_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(view.bs.port.getText().equals(""))
					System.out.println("plz enter port!");
				else
					control.createServer();
			}
		});
		/**
		 * BingoStart�� exit ��ư ������
		 */
		view.bs.exit_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(1);
			}
		});
		/**
		 * BingoBoard�� Random ��ư ������
		 */
		view.bb.RanButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				control.Random();
			}
		});
		/**
		 * BingoBoard�� Ready ��ư ������
		 */
		view.bb.ReadyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				control.Ready();
			}
		});
		/**
		 * BingoBoard�� Send ��ư ������
		 */
		view.bb.SendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				control.Send();
			}
		});
	}
}
