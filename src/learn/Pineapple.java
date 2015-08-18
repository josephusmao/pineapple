package learn;

public class Pineapple {
	public static void main(String[] args) {
		try {
			Server server = new Server();
			server.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
