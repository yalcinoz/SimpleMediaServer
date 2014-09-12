package dash;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerWorker implements Runnable
{
	private Socket clientSocket = null;
    private Pattern pattern = null;
    private InputStream input = null;
    private OutputStream output = null;
    private String filepath = null;

    public ServerWorker(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
        this.pattern = Pattern.compile("GET /(.+) HTTP/1.1");
    }

    public void run()
    {
        this.parseConnectionString();
        this.getDataFromController();
        this.sendOutput();
        this.closeStreams();
        System.out.println("Request processed");
    }
    
    private void parseConnectionString()
    {
    	String line = null;
    	try
    	{
	    	this.input  = this.clientSocket.getInputStream();
	        BufferedReader in = new BufferedReader(new InputStreamReader(this.input));
	        line = in.readLine();
    	}
    	catch (IOException e)
    	{
    		throw new RuntimeException("Cannot get input stream", e);
    	}
    	Matcher matcher = this.pattern.matcher(line);
    	String str = null;
    	while (matcher.find())
    	{
    		str = matcher.group(1);
    		break;
    	}
    	this.filepath = str;
    }
    
    private void getDataFromController()
    {
    	try
    	{
    		System.out.println("Creating new socket to connect to controller");
    		Socket socket = new Socket("localhost", 12001);
    		PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
    		output.println(this.filepath);
    		
    		System.out.println("Sent output to controller");
    		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    		this.filepath = input.readLine();
    		System.out.println("Read response from controller");
    		
    		output.close();
    		input.close();
    		socket.close();
    	}
    	catch (IOException e)
    	{
    		throw new RuntimeException("Cannot open controller client socket", e);
    	}
    }
    
	private void sendOutput()
    {
		try
		{
			String absolutePath = System.getProperty("user.dir") + "/dash/" + this.filepath;
			File f = new File(absolutePath);
			int len = (int) f.length();
			byte[] buf = new byte[len];
			DataInputStream din = new DataInputStream(new FileInputStream(f));
			din.readFully(buf);
			din.close();
			
			this.output = this.clientSocket.getOutputStream();
	        this.output.write(("HTTP/1.1 200 OK\n").getBytes());
	        this.output.write(("Content-Length: " + len + "\n\n").getBytes());
	        this.output.write(buf);
	        this.output.flush();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Cannot send output", e);
		}
    }
	
	private void closeStreams()
	{
		try
		{
			this.input.close();
			this.output.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Cannot close streams", e);
		}
	}
}
