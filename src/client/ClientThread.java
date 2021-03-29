package client;

import tftp.TFTFConstants;
import tftp.TFTP;

import java.io.*;
import java.net.*;


public class ClientThread extends TFTP {
	private ClientGUI gui;
	private DatagramPacket pkt = null;
	public ClientThread(){}//constructor
	public ClientThread(ClientGUI _gui){
		this.gui = _gui;
	}//constructor

	@Override
	public void run() {
		System.out.println("ClientConnect");
		try {
			address = InetAddress.getByName(gui.getTfServer().getText());	//get the localhost
			byte[] a = "hello".getBytes();
			DATAPacket(address,1,a,SERVER_PORT);


			//ready to read from server
			byte[] data = new byte[1024];
			dp = new DatagramPacket(data,data.length);	//ready the packet
			ds = new DatagramSocket(CLIENT_PORT);				//packet ready the receive the port
			ds.receive(dp);
			ByteArrayInputStream ab = new ByteArrayInputStream(data);
			DataInputStream dis = new DataInputStream(ab);
			String content = "";
			byte b;
			while ((b = dis.readByte()) > 0) {
				content += (char) b;
				System.out.println(b);
			}
			log(content);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//run

	private void log(String msg){
		gui.getTaLog().appendText(msg + "\n");
	}//log
}//class
