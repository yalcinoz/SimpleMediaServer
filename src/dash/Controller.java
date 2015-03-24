package dash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Controller
{
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		try {
			ServerSocket serverSocket = new ServerSocket(12001);
			Socket clientSocket = serverSocket.accept();
			System.out.println("Client connected : " + clientSocket);

			PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			while (true)
			{
				output.println(input.readLine());
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException("ControllerServer\t:\tCannot open server socket ", e);
		}
	}
}
