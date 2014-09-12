package dash;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

abstract class Base implements Runnable
{
	protected boolean isStopped = false;
	protected ServerSocket serverSocket = null;
	protected ExecutorService threadPool = Executors.newFixedThreadPool(10);
	
	protected abstract int getListeningPort();
	protected abstract String getName();
	
	public void run()
	{
		this.openServerSocket();
		System.out.println(this.getName() + " Started");
		while ( !isStopped )
		{
			Socket clientSocket = null;
			try
			{
				clientSocket = this.serverSocket.accept();
			}
			catch (IOException e)
			{
				if ( this.isStopped )
				{
					System.out.println(this.getName() + " Stopped");
					return;
				}
				throw new RuntimeException("Error accepting client connection", e);
			}
			try
			{
				Class<?> cls = Class.forName("dash." + this.getName() + "Worker");
				Constructor<?> constructor = cls.getConstructor(Socket.class);
				this.threadPool.execute((Runnable) constructor.newInstance(clientSocket));
			}
			catch (Exception e)
			{
				throw new RuntimeException("Error creating a new worker for " + this.getName(), e);
			}
		}
	}
	
	private void openServerSocket()
	{
		try
		{
			this.serverSocket = new ServerSocket(this.getListeningPort());
		}
		catch (IOException e)
		{
			throw new RuntimeException("Cannot open " + this.getName() + " socket on port " + this.getListeningPort(), e);
		}
	}
}
