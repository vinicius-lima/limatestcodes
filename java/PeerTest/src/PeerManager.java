import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;

public class PeerManager {
	public static final int DISCOVERY_PORT = 2234;
	public static final int TRANSFERENCE_PORT = 2235;
	public static final int TIMEOUT = 3000;

	private ArrayList<String> registredApps;
	private ArrayList<InetAddress> peers;
	private Thread discoveryThread;
	private Thread transferenceThread;
	
	//private ServiceImpl localServiceImplementation;

	public PeerManager() {
		registredApps = new ArrayList<String>();
		peers = new ArrayList<InetAddress>();
		
		discoveryThread = new Thread(new DiscoveryThread());
		discoveryThread.start();
		System.out.println("Discovery thread started!");
		
		transferenceThread = new Thread(new TransferenceThread());
		transferenceThread.start();
		System.out.println("Transference thread started!");
	}
	
	public void registryApplication(String applicationName) {
		registredApps.add(applicationName);
	}
	
	/*public void addLocalServiceImplementation(ServiceImpl serviceImpl) {
		localServiceImplementation = serviceImpl;
	}*/
	
	public void shutdown() {
		registredApps.clear();
		peers.clear();
		discoveryThread.interrupt();
		transferenceThread.interrupt();
	}

	public boolean discoverPeers(String applicationName) {
		short foundAny = 0;

		// Find any peer using UDP broadcast
		try {
			//Open a random port to send the package
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);

			byte[] sendData = applicationName.getBytes();
			
			//Try the 255.255.255.255 first
			/*try {
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), DISCOVERY_PORT);
				socket.send(sendPacket);
				//System.out.println(">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
			} catch (Exception e) {

			}*/

			// Broadcast the message over all the network interfaces
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements()){
				NetworkInterface networkInterface = interfaces.nextElement();
				
				if (networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue; // Don't want to broadcast to the loopback interface
				}

				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()){
					InetAddress broadcast = interfaceAddress.getBroadcast();
					InetAddress local = interfaceAddress.getAddress();
					System.out.println("Local host: " + local.getHostAddress());
					
					if(broadcast == null){
						continue;
					}

					// Send the broadcast package!
					try{
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, DISCOVERY_PORT);
						socket.send(sendPacket);
					} catch (Exception e) {
						e.printStackTrace();
					}

					System.out.println(">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
				}

			}

			System.out.println(">>> Done looping over all network interfaces. Now waiting for a reply!");
			
			//System.out.println("Local host: " + InetAddress.getLocalHost().getHostAddress());

			//Wait for a response
			byte[] recvBuf = new byte[applicationName.length()];
			DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
			socket.setSoTimeout(TIMEOUT);
			
			while(true){
				try {
					socket.receive(receivePacket);
					
					//We have a response
					System.out.println(">>> Broadcast response from peer: " + receivePacket.getAddress().getHostAddress());
		
					//Check if the message is correct
					String message = new String(receivePacket.getData()).trim();
					System.out.println("message = " + message);
		
					if (message.equals(applicationName)) {
						//DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
						System.out.println("Peer IP: " + receivePacket.getAddress());
						peers.add(receivePacket.getAddress());
						foundAny++;
					}
		
					//Close the port!
					//socket.close();
				} catch (SocketTimeoutException ste) {
					//Close the port!
					System.out.println("SocketTimeoutException archived!!!");
					socket.close();
					break;
				}
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		//foundAny = 1;
		System.out.println("foundAny = " + foundAny);
		return (foundAny != 0);
	}

	private class DiscoveryThread implements Runnable {
		DatagramSocket socket;

		@Override
		public void run() {
			try {
				//Keep a socket open to listen to all the UDP trafic that is destined for this port
				socket = new DatagramSocket(DISCOVERY_PORT, InetAddress.getByName("0.0.0.0"));
				socket.setBroadcast(true);

				while(true){
					//System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");

					//Receive a packet
					byte[] recvBuf = new byte[100];
					DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
					socket.receive(packet);

					//Packet received
					System.out.println(">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
					System.out.println(">>>Packet received; data: " + new String(packet.getData()));

					//See if the packet holds the right command (message)
					String applicationName = new String(packet.getData()).trim();
					if(registredApps.contains(applicationName)){
						byte[] sendData = applicationName.getBytes();

						//Send a response
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
						socket.send(sendPacket);

						//System.out.println(getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	public void transferState(String applicationName, String state) {
		try {
			System.out.println("Transfering to " + peers.get(0));
			//Socket peer = new Socket(peers.get(0), TRANSFERENCE_PORT);
			Socket peer = new Socket(peers.get(0).getHostAddress(), TRANSFERENCE_PORT);
			//Socket peer = new Socket("10.16.1.14", TRANSFERENCE_PORT);
			DataOutputStream out = new DataOutputStream(peer.getOutputStream());
			
			out.write(state.getBytes());
			
			out.close();
			peer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class TransferenceThread implements Runnable {
		ServerSocket socket;

		@Override
		public void run() {
			try {
				socket = new ServerSocket(TRANSFERENCE_PORT);
				
				while(true){
					Socket peer = socket.accept();
					DataInputStream in = new DataInputStream(peer.getInputStream());
					
					byte[] recvBuf = new byte[in.available()];
					in.read(recvBuf);
					String state = new String(recvBuf);
					
					// TODO: difenrenciar de qual aplicacao eh o estado recebido.
					//localServiceImplementation.resumeApplicationState(registredApps.get(0), state);
					System.out.println("State received - " + registredApps.get(0) + ": " + state);
					
					in.close();
					peer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
