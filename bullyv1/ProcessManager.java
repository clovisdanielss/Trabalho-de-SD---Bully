package bullyv1;

import java.io.IOException;
import java.net.Socket;

/**
 * Aceita as conecxoes feitas no servidor do meu processo.
 * */
public class ProcessManager extends Thread{
	
	private Process myProcess;
	private int pc;
	
	public ProcessManager(Process process){
		myProcess = process;
		setPc(0);
	}
	
	
	@Override
	public void run(){
		while(true){
			try {
				//myProcess.checkProcess();
				pc++;
				Socket client = myProcess.getServer().accept();
				new ProcessController(client,pc,myProcess).start();
				if(pc == myProcess.getpID() - 1)
					pc = 0;

			} 
			catch (IOException e) {
			}
			
		}
	}

	public int getPc() {
		return pc;
	}

	public void setPc(int pc) {
		this.pc = pc;
	}
	
	
	
}
