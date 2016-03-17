package transfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;

import main.BaseViewerFX;

public class TransferManager {
	
	private int localPort;
	private String remoteIP;
	private int remotePort;
	private Gson gson;
	private BaseViewerFX viewer;
	private Thread rcvThread;
	
	public TransferManager(int lPort, String rIP, int rPort, BaseViewerFX viewer) {
		this(lPort, rIP, rPort);
		this.viewer = viewer;
	}
	
	public TransferManager(int lPort, String rIP, int rPort) {
		localPort = lPort;
		remoteIP = rIP;
		remotePort = rPort;
		gson = new Gson();
		
		rcvThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					@SuppressWarnings("resource")
					ServerSocket server = new ServerSocket(localPort);
					while(true){
						// Reads incoming execution state metadata.
						Socket sock = server.accept();
						DataInputStream in = new DataInputStream(sock.getInputStream());
						String json = in.readUTF();
						TransferWrapper wrapper = gson.fromJson(json, TransferWrapper.class);
						
						// Reads actual file content.
						byte[] content = new byte[(int)wrapper.getFileSize()];
						for(int i = 0; i < content.length; i++)
							content[i] = in.readByte();
						saveFileContent(wrapper.getPDFfile(), content);
						
						in.close();
						sock.close();
						
						// Notify UI to update user's view interface.
						viewer.resumeExecution(wrapper);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		rcvThread.start();
	}
	
	public void saveFileContent(String name, byte[] fileContent) {
		try {
			File file = new File(name);
			if(file.exists())
				file.delete();
			
			DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
			out.write(fileContent);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void transfer(TransferWrapper wrapper) {
		try {
			Socket sock = new Socket(remoteIP, remotePort);
			
			// Sends execution state metadata.
			File file = new File(wrapper.getPDFfile());
			wrapper.setPDFfile(file.getName());
			String json = gson.toJson(wrapper);
			
			DataOutputStream out = new DataOutputStream(sock.getOutputStream());
			out.writeUTF(json);
			
			// Sends actual file content.
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			byte[] content = new byte[in.available()];
			in.readFully(content);
			out.write(content);
			
			in.close();
			out.close();
			sock.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
