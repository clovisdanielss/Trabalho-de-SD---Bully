package bullyv1;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ProcessController extends Thread{
	
	private ArrayList<Data> table;
	private Socket client;
	private int pid;
	@SuppressWarnings("unused")
	private int pc;
	private Process myProcess;
	// Quando for fazer eleicao sera necessario esse parametro msg
	
	public ProcessController(Socket client, int pc, Process p) {
		this.client = client;
		this.table = p.getMyTable();
		this.pid = p.getpID();
		myProcess = p;
		this.pc = pc;
	}
	
	@Override
	public void run(){
		
		
		// Fim da escuta, fecha o socket.
		try {

			listen();
			client.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void listen() throws IOException{
		byte[] buffer = new byte[100];
		client.getInputStream().read(buffer);
		
		
		String myBuffer = new String(buffer).trim();
		
		// Vou ter 4 possíveis mensagens recebidas:   
		//(?) Eleicao (!) Keep Alive para o Servidor (O) Resposta de um servidor ao keep alive
		//(Nb) Recebe msg do novo boss.
		
		String msgRcv = "";
		int start = 0;
		int end = 0;
		
		
		for(int i = 0; i < myBuffer.length(); i++){
			if(myBuffer.charAt(i) == '('){
				start = i;
			}
			if(myBuffer.charAt(i) == ')'){
				end = i;
				msgRcv = myBuffer.substring(start + 1, end -1);
				String pID = myBuffer.substring(start + 2, end);
				checkMensage(msgRcv,pID);
			}
		}
	}

	private synchronized void checkMensage(String msgRcv, String pID) {
		if(msgRcv.equals("!")){
			// Retorno Ok para o camarada. Caso "send"			
			Data serverData = Data.myData(Integer.valueOf(pID), table);
			try {
				
				//PROBLEMA CASO HAJA IP'S IGUAIS, DEVO PROCURAR PELO PID
				
				Socket socket = new Socket(serverData.getIp(), Integer.valueOf(serverData.getPort()));
				
				if(myProcess.isBoss())
					socket.getOutputStream().write(("(O"+ String.valueOf(pid) +")").getBytes());
				
				socket.close();
				
			} catch (NumberFormatException e) {
			} catch (UnknownHostException e) {
			} catch (IOException e) {
			}
			
			myProcess.setIsBoss(true);
		}
		if(msgRcv.equals("?")){
			// Retorno Ok para o camarada. Caso "election"
			Data serverData = Data.myData(Integer.valueOf(pID), table);
			try {
				System.out.println(":("+Integer.valueOf(myProcess.getpID())+")> Respondendo Eleicao Para : " + serverData.getIp() + ": ("+ serverData.getId() + ")");
				
				//PROBLEMA CASO HAJA IP'S IGUAIS, DEVO PROCURAR PELO PID
				
				Socket socket = new Socket(serverData.getIp(), Integer.valueOf(serverData.getPort()));
				
				if(!myProcess.isBoss())
					socket.getOutputStream().write(("(N"+ String.valueOf(pid) +")").getBytes());
				else
				{
					socket.getOutputStream().write(("(b"+ String.valueOf(pid) +")").getBytes());
				}
				socket.close();
				
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}	
			
		}
		if(msgRcv.equals("O")){
			try {
				myProcess.setKeepAlive(true);
				client.close();
				
				notifyAll();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		if(msgRcv.equals("N")){
			try {
				Data serverData = Data.myData(Integer.valueOf(pID), table);
				System.out.println(":("+Integer.valueOf(myProcess.getpID())+")> " + serverData.getIp() + ": ("+ serverData.getId() 
					+ ")  Me Avisou que NAO sou o novo boss");
				myProcess.setElectionACK(true);
				if(myProcess.isBoss())
					myProcess.setIsBoss(false);
				
				client.close();
				
				notifyAll();
			} catch (IOException e) {
			}
		}
		if(msgRcv.equals("b")){
			try {
				Data serverData = Data.myData(Integer.valueOf(pID), table);
				System.out.println(":("+Integer.valueOf(myProcess.getpID())+")> O novo chefe : " + serverData.getIp() 
					+ ": ("+ serverData.getId() + ") me bateu!");
				
				if(myProcess.isBoss())
					myProcess.setIsBoss(false);
				
				myProcess.setElectionACK(true);
				
				if(myProcess.isElection())
					myProcess.setElection(false);
				
				myProcess.setBoss(serverData);
				
				client.close();
				
				notifyAll();
				} catch (IOException e) {
			}
		}
		
	}
	
	

}
