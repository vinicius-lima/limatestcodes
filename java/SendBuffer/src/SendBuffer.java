import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;


public class SendBuffer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args[0].equalsIgnoreCase("server")){
			try {
				@SuppressWarnings("resource")
				ServerSocket sv = new ServerSocket(Integer.parseInt(args[1]));
				while(true){
					Socket ct = sv.accept();
					System.out.println("Client connected!!!");
					DataInputStream in = new DataInputStream(ct.getInputStream());
					byte[] buffer = new byte[28];
					in.read(buffer);
					ByteBuffer buf = ByteBuffer.wrap(buffer);
					//buf.put(buffer);
					while(buf.hasRemaining()){
						System.out.println(buf.getInt());
					}
					ct.close();
					System.out.println("-----------------");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		int[] vector = new int[7];
		Random rand = new Random(System.currentTimeMillis());
		while(true){
			for(int i = 0; i < 7; i++)
				vector[i] = rand.nextInt(100);
			
			ByteBuffer buf = ByteBuffer.allocate(28);
			for(int i : vector){
				System.out.println(i);
				buf.putInt(i);
			}
			System.out.println("-----------------");
			
			try {
				Socket ct = new Socket("localhost", Integer.parseInt(args[1]));
				DataOutputStream out = new DataOutputStream(ct.getOutputStream());
				out.write(buf.array());
				ct.close();
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
