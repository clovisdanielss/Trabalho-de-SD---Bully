package bullyv1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Process extends Thread {

	private ArrayList<Data> myTable;
	private ServerSocket server;
	private int pid;
	private Data boss;
	private boolean isBoss, keepAlive, isDown, electionACK, isElection;
	private int t, dR, uR, cDR;

	public Data getBoss() {
		return boss;
	}

	public void setBoss(Data d) {
		boss = d;
	}

	public Process(String pid, ArrayList<Data> table, int t, int dR, int uR, int cDR)
			throws NumberFormatException, IOException, InterruptedException {
		isDown = false;
		isBoss = false;

		this.t = t;
		this.dR = dR;
		this.uR = uR;
		this.cDR = cDR;
		sleep(1000);

		setMyTable(table);
		setpID(Integer.valueOf(pid));

		// Starting own server:
		Data myData = Data.myData(this.pid, myTable);
		server = new ServerSocket(Integer.valueOf(myData.getPort()));

		boss = myTable.get(myTable.size() - 1);

		new ProcessManager(this).start();
		new ProcessBreaker(this).start();
		new ProcessFinisher().start();
	}

	@Override
	public void run() {
		try {
			bully();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void bully() throws InterruptedException {

		if (Integer.valueOf(boss.getId()) == pid) {
			System.out.println("[" + Integer.valueOf(pid) + " Eu sou o chefe!" + "]");
			isBoss = true;
			while (isBoss) {
			}
		} else {
			System.out.println("[O Chefe eh " + boss.getIp() + ":" + boss.getPort() + " com id "
					+ Integer.valueOf(boss.getId()) + "]");
			System.out.println("[Eu sou " + Integer.valueOf(pid) + " ]");
			sleep(2000);
			while (true) {
				if (!send() && !isBoss) {
					election();
				}
			}

		}

		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void waitToDown() throws InterruptedException, IOException {

		wait((long) (1000 * t));

		while (isElection)
			wait();

		if (Math.random() * 100 > uR && isDown == true) {
			System.out.println("[(" + Integer.valueOf(pid) + ") is Going Up]");
			isDown = false;
			// criar server
			Data myData = Data.myData(this.pid, myTable);
			server = new ServerSocket(Integer.valueOf(myData.getPort()));
			if (pid < Integer.valueOf(myTable.get(myTable.size() - 1).getId())) {
				System.out.println("[Verifica se o Chefe eh o conhecido, Se nao for havera eleicao!!]");
				send();
			} else
				election();

		} else if ((Math.random() * 100 > cDR || (isBoss && Math.random() * 100 > dR)) && isDown == false) {
			System.out.println("[(" + Integer.valueOf(pid) + ") is Going Down]");
			isDown = true;
			setIsBoss(false);
			// fechar server;
			server.close();
		}

		notifyAll();
	}

	private synchronized void election() throws InterruptedException {

		setElectionACK(false);

		setElection(true);
		

		if (!isDown) {
			System.out.println(":(" + Integer.valueOf(pid) + ")> " + "Iniciando eleicao...");

			for (Data process : myTable) {
				if (Integer.valueOf(process.getId()) > getpID())
					try {

						Socket socket = new Socket(process.getIp(), Integer.valueOf(process.getPort()));

						System.out.println(":(" + Integer.valueOf(pid) + ")> " + "Mandando Eleicao Para : "
								+ process.getIp() + ":(" + process.getId() + ")");

						socket.getOutputStream().write(("(?" + new Integer(getpID()) + ")").getBytes());

						socket.close();

					} catch (NumberFormatException e) {
					} catch (UnknownHostException e) {
					} catch (IOException e) {
						System.out.println(":(" + Integer.valueOf(pid) + ")> " + "Error ao Mandar Eleicao Para : "
								+ process.getIp() + ":(" + process.getId() + ")");
					}
			}
			try {

				// Espera por resposta de alguem...
				wait(3000);
				if (isElection)
					if (!isElectionACK()) {
						// Avisa que ele é o eleito...
						if (!isBoss)
							setIsBoss(true);
						System.out.println("[Eu sou o novo chefe!]");
						for (Data process : myTable)
							if (Integer.valueOf(process.getId()) < getpID()) {

								try {
									Socket socket = new Socket(process.getIp(), Integer.valueOf(process.getPort()));

									System.out.println(":(" + Integer.valueOf(pid) + ")> " + "Mandando Eleito Para : "
											+ process.getIp() + ":(" + process.getId() + ")");

									socket.getOutputStream().write(("(b" + new Integer(getpID()) + ")").getBytes());

									socket.close();
								} catch (IOException e) {
									System.out.println(
											":(" + Integer.valueOf(pid) + ")> " + "Error ao Mandar Eleito Para : "
													+ process.getIp() + ":(" + process.getId() + ")");
								}
							}

					}
			} catch (InterruptedException e) {
			}

		}
		setElection(false);
		notifyAll();

	}

	private synchronized boolean send() {

		if (!isDown && !isBoss)
			try {

				setKeepAlive(false);

				Socket socket = new Socket(boss.getIp(), Integer.valueOf(boss.getPort()));

				socket.getOutputStream().write(("(!" + new Integer(getpID()) + ")").getBytes());

				socket.close();

				wait(3000);

				if (keepAlive) {
					return true;
				} else {
					System.out.println("[ Detectou falha no chefe" + "(" + Integer.valueOf(boss.getId()) + ") ]");
					return false;
				}

			} catch (NumberFormatException e) {
				return false;
			} catch (UnknownHostException e) {
				System.out.println("[ Detectou falha no chefe" + "(" + Integer.valueOf(boss.getId()) + ") ]");
				return false;
			} catch (IOException e) {
				System.out.println("[ Detectou falha no chefe" + "(" + Integer.valueOf(boss.getId()) + ") ]");
				return false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		return true;
	}

	public int getpID() {
		return pid;
	}

	public void setpID(int pID) {
		this.pid = pID;
	}

	public ArrayList<Data> getMyTable() {
		return myTable;
	}

	public void setMyTable(ArrayList<Data> myTable) {
		this.myTable = myTable;
	}

	public ServerSocket getServer() {
		return server;
	}

	public boolean isBoss() {
		return isBoss;
	}

	public void setIsBoss(boolean fLoop) {
		this.isBoss = fLoop;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public boolean isDown() {
		return isDown;
	}

	public void setDown(boolean isDown) {
		this.isDown = isDown;
	}

	public boolean isElectionACK() {
		return electionACK;
	}

	public void setElectionACK(boolean election) {
		this.electionACK = election;
	}

	public void setOnElection(boolean onElection) {
	}

	public boolean isElection() {
		return isElection;
	}

	public void setElection(boolean isElection) {
		this.isElection = isElection;
	}

}
