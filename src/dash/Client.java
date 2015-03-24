package dash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client
{
	public static void main(String[] args) throws FileNotFoundException
	{
		System.out.println("Client is starting...");
		try
		{
			Socket socket = new Socket("127.0.0.1", 12000);
			System.out.println("Connected to server");
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			int packetNo = 1;
			while (packetNo <= 5)
			{
				out.println("deneme");
				System.out.println(input.readLine());
				packetNo++;
			}
		} catch (IOException e) {
			System.out.println("Cannot connect to server");
		}
	}
}
