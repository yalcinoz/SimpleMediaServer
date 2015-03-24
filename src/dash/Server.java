package dash;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class Server
{
	static Socket controllerSocket;
	
	public Queue<String> clientReq = new LinkedList<String>();
	
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		Server s = new Server();
		s.connectToController();
		s.openServerSocketForClients();
	}

	private void connectToController() throws UnknownHostException, IOException
	{
		this.controllerSocket = new Socket("127.0.0.1", 12001);
	}

	private void openServerSocketForClients() throws IOException
	{
		ServerSocket serverSocket = new ServerSocket(12000);
		while (true)
		{
			Socket clientSocket = serverSocket.accept();
			new ServerWorker(controllerSocket, clientSocket).start();
		}
	}
}
