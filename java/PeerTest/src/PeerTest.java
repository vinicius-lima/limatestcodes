import java.util.Scanner;

public class PeerTest {

	public static void main(String[] args) {
		String applicationName = "PeerTest";
		Scanner keyboard = new Scanner(System.in);
		int wait = 1;
		
		System.out.println("Creating peer manager...");
		PeerManager peer = new PeerManager();
		System.out.println("Peer manager created!");
		
		peer.registryApplication(applicationName);
		
		while(wait != 0){
			wait = keyboard.nextInt();
			
			if(wait != 0){
				if(peer.discoverPeers(applicationName)){
					String state = "{\"attribute\":\"some value\"}";
					peer.transferState(applicationName, state);
				}
				else
					System.err.println("Could not find any peer!!!");
			}
		}
		
		System.out.println("Shutting down...");
		peer.shutdown();
		keyboard.close();
		System.exit(0);
	}

}
