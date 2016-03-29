package sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import draw.DrawString;
import draw.MoveString;

public class StateManager implements Runnable{
	private DrawString string;
	private int port;
	private String remoteIP;
	private int remotePort;
	private MoveString moveIt;
	
	public StateManager(DrawString string, int servicePort, String remoteIp, int remotePort){
		this.string = string;
		port = servicePort;
		this.remoteIP = remoteIp;
		this.remotePort = remotePort;
	}

	@Override
	public void run() {
		try {
			@SuppressWarnings("resource")
			ServerSocket sv = new ServerSocket(port);
			while(true){
				Socket ct = sv.accept();
				DataInputStream in = new DataInputStream(ct.getInputStream());
				if(in.readUTF().equals("SEND")){
					byte[] inBuffer = new byte[28];
					in.read(inBuffer);
					string.updateState(inBuffer);
					moveIt.startRun();
				}
				else{ // A GET was received.
					byte[] state = string.getState();
					DataOutputStream out = new DataOutputStream(ct.getOutputStream());
					out.write(state);
					moveIt.stopRun();
				}
				ct.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendState(){
		try {
			Socket ct = new Socket(remoteIP, remotePort);
			DataOutputStream out = new DataOutputStream(ct.getOutputStream());
			out.writeUTF("SEND");
			out.write(string.getState());
			ct.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getState(){
		try {
			Socket ct = new Socket(remoteIP, remotePort);
			DataOutputStream out = new DataOutputStream(ct.getOutputStream());
			out.writeUTF("GET");
			DataInputStream in = new DataInputStream(ct.getInputStream());
			byte[] state = new byte[28];
			in.read(state);
			string.updateState(state);
			ct.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setMover(MoveString moveIt){
		this.moveIt = moveIt;
	}
}
