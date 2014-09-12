package dash;


public class Server extends Base
{	
	final private int port = 12000;
	final private String name = "Server";
	
	protected int getListeningPort()
	{
		return this.port;
	}
	
	protected String getName()
	{
		return this.name;
	}
}
