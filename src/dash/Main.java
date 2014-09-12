package dash;

public class Main
{
	public static void main(String[] args)
	{
		System.out.println("Server and controller are starting...");
		new Thread(new Server()).start();
		new Thread(new Controller()).start();
	}
}
