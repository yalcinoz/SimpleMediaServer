package dash;


public class Controller extends Base
{
	final private int port = 12001;
	final private String name = "Controller";
	
	protected int getListeningPort()
	{
		return this.port;
	}

	protected String getName()
	{
		return this.name;
	}
}
