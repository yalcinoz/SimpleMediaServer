package dash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ControllerWorker implements Runnable
{
	private Socket clientSocket = null;
    private InputStream input = null;
    private OutputStream output = null;

    public ControllerWorker(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    public void run()
    {
    	this.sendResponseToServer();
        this.closeStreams();
        System.out.println("Request processed");
    }
	
    private void sendResponseToServer()
    {
    	try
    	{
    		System.out.println("Controller waiting for server request");
    		this.input = this.clientSocket.getInputStream();
    		BufferedReader reader = new BufferedReader(new InputStreamReader(this.input));
    		String filepath = reader.readLine();
    		System.out.println("Controller read filepath coming from the server: " + filepath);
    		this.output = this.clientSocket.getOutputStream();
    		this.output.write(filepath.getBytes());
    		this.output.flush();
    		System.out.println("Controller sent response");
    		
    	}
    	catch (IOException e)
    	{
    		throw new RuntimeException("Cannot get client input stream", e);
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
