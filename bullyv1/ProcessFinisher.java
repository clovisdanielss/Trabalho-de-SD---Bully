package bullyv1;

public class ProcessFinisher extends Thread{


	
	public ProcessFinisher() {

	}
	
	@Override
	public void run(){
		try {
			sleep(45000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	

}
