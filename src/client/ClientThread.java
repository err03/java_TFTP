package client;

import tftp.TFTP;

import java.io.*;
import java.net.*;


public class ClientThread extends TFTP {
	private ClientGUI gui;
	/**
	 * {InetAddress address},{DatagramPacket dp}
	 * and {DatagramSocket ds} from extends TFTP
	 */
	public ClientThread(){}//constructor
	public ClientThread(ClientGUI _gui){
		this.gui = _gui;
	}//constructor

	@Override
	public void run() {
		System.out.println("ClientConnect");
		try {
			/*
			send data to server
			 */
			address = InetAddress.getByName(gui.getTfServer().getText());	//get the localhost
			byte[] a = "hello".getBytes();
			DATAPacket(address,1,a,SERVER_PORT);

			ReadFromServer();	//ready to read from server
		} catch (IOException e) {
			e.printStackTrace();
		}//try..catch
	}//run

	/*
	ready to read from server
	 */
	private void ReadFromServer() throws IOException {
		byte[] data = new byte[1024];
		dp = new DatagramPacket(data,data.length);	//ready the packet
		ds = new DatagramSocket(CLIENT_PORT);				//packet ready the receive the port
		ds.receive(dp);					//will stop here until read the data
		ByteArrayInputStream ab = new ByteArrayInputStream(data);	// data ↑
		DataInputStream dis = new DataInputStream(ab);
		String content = "";
		byte b;
		while ((b = dis.readByte()) > 0) {
			content += (char) b;
			System.out.println(b);
		}
		log(content);
	}//read from server

	private void log(String msg){
		gui.getTaLog().appendText(msg + "\n");
	}//log
}//class
