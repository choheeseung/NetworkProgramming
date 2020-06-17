
public class BaseBallModel {
	static String Server ="";
	static int Port = 0;
	
	int answer[] = new int [3];
	int guess[] = new int [3];
	int strike = 0;
	int ball = 0;
	int score[] = new int [2];
	
	BaseBallModel() {
		BaseBallClient baseBallClient = new BaseBallClient(Server, Port);
		for (int i=0;i<3;i++) {
			answer[i] = baseBallClient.answer[i];
			guess[i] = baseBallClient.num[i];
			
			int oneServerNumber = answer[i];
			for (int j=0;j<3;j++) {
				int oneClientNumber = guess[j];
				if (oneServerNumber == oneClientNumber) {
					if(i==j)
						strike++;
					else
						ball++;
				}
			}
		}
		System.out.println(strike+"스트라이크"+ball+"볼");
		score[0] = strike;
		score[1] = ball;
		
	}
}
