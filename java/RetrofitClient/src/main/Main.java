/**
 * Dependencs:
 * gson-2.2.4.jar or later
 * converter-gson-2.0.0-beta2.jar or later
 * retrofit-2.0.0-beta2.jar or later
 * okhttp-2.6.0.jar or later
 * okio-1-6.0.jar or later*/

package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import client.EndpointInterface;
import client.Message;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class Main {

	public static void main(String[] args) {
		
		String BASE_URL = "http://localhost:9000";
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		
		EndpointInterface service = retrofit.create(EndpointInterface.class);
		
		Call<Message> call = service.getLast();
		Message msg;
		try {
			msg = call.execute().body();
			System.out.println(msg + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		call = service.sumForm(10, 20);
		try {
			msg = call.execute().body();
			System.out.println(msg + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		msg = new Message(30, 40, 0);
		call = service.sumJson(msg);
		try {
			msg = call.execute().body();
			System.out.println(msg + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			File file = new File("doge.png");
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			
			String response = service.updateFile(file.getName(), in.available()).execute().body();
			int serverPort = Integer.parseInt(response);
			
			byte[] buf = new byte[in.available()];
			in.read(buf);
			in.close();
			
			Socket sock = new Socket("localhost", serverPort);
			DataOutputStream out = new DataOutputStream(sock.getOutputStream());
			out.write(buf);
			sock.close();
			
			System.out.println("File sent!!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
