package dash;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerWorker extends Thread
{
	public Socket controllerSocket;
	
	private Socket clientSocket;

	public ServerWorker(Socket controllerSocket, Socket clientSocket) throws FileNotFoundException
	{
		this.controllerSocket = controllerSocket;
		this.clientSocket = clientSocket;
	}

	public void run()
	{
		try
		{
			PrintWriter outputToClient = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			while (true)
			{
				String lineFromClient = inputFromClient.readLine();
				if ( !lineFromClient.endsWith("mpd") )
				{
					synchronized (controllerSocket)
					{
						String lineToSend = this.createMessageForController(lineFromClient);
						PrintWriter outputToController = new PrintWriter(controllerSocket.getOutputStream(), true);
						outputToController.println(lineToSend);
						BufferedReader inputFromController = new BufferedReader(new InputStreamReader(controllerSocket.getInputStream()));
						String lineFromController = inputFromController.readLine();
						System.out.println("To Controller: " + lineToSend + " / From Controller: " + lineFromController);
					}
				}
				this.sendOutput(lineFromClient);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void sendOutput(String filepath)
    {
		try
		{
			String absolutePath = System.getProperty("user.dir") + "/dash/data/" + filepath;
			File f = new File(absolutePath);
			int len = (int) f.length();
			byte[] buf = new byte[len];
			DataInputStream din = new DataInputStream(new FileInputStream(f));
			din.readFully(buf);
			din.close();
			
			System.out.println("FILE LENGTH: " + len);
			
			OutputStream output = this.clientSocket.getOutputStream();
			ByteBuffer b = ByteBuffer.allocate(4);
			b.putInt(len);
			byte[] result = b.array();
			System.out.println(result.length);
			System.out.println(Arrays.toString(result));
			output.write(result);
	        output.write(buf);
	        output.flush();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Cannot send output", e);
		}
    }
	
	private String createMessageForController(String line)
	{
		String message = this.clientSocket.getRemoteSocketAddress().toString();
		
		Pattern pattern = Pattern.compile("\\w*?_(\\d+)kbit/\\w*?\\d{1,2}s(\\d{1,3})\\.\\w{3}", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(line);
		while ( matcher.find() )
		{
			message += "," + matcher.group(1);
			message += "," + matcher.group(2);
		}
		return message;
	}
}









