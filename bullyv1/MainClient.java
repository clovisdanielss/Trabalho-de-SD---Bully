package bullyv1;

import java.io.IOException;

public class MainClient {
	public static void main(String[] args) throws IOException {
		
		
		int nC = Integer.valueOf(args[0]);
		int t = Integer.valueOf(args[1]); // Periodo de tempo para sortear queda do servidor
		int dR = Integer.valueOf(args[2]); //Complemento da Chance do servidor cair
		int uR = Integer.valueOf(args[3]); //Complemento da Chance do servidor voltar
		int cDR = Integer.valueOf(args[4]); //Complemento da Chance do cliente cair 
		String hostTracker = args[5];
		
		SimpleClient client = new SimpleClient(nC,hostTracker);
		
		for(int i = 0; i < client.getTable().size(); i++){
			client.getTable().get(i).setId(new Integer(i + 1).toString());
		}
		
		Data myData = Data.myData(client.getMyIp(),String.valueOf(client.getSocket().getLocalPort() + 1),
				client.getTable());		

		try {
			new Process(myData.getId(), client.getTable(),t,dR,uR,cDR).start();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
}
