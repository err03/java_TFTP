package client;

import tftp.TFTP;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ClientDownload extends TFTP {
	private ClientGUI gui;
	public ClientDownload(){}//constructor
	public ClientDownload(ClientGUI _gui){
		this.gui = _gui;
	}//constructor

	@Override
	public void run() {
		String filename = "filename.txt";
		try {
			/*
			send RRQ to server
			 */
			address = InetAddress.getByName(gui.getTfServer().getText());	//get the localhost
			ds = new DatagramSocket();
			dp = RRQPacket(filename,SERVER_PORT);
			ds.send(dp);
			/*
				receive data from server
			 */
			ReadFromServer();

			//when get data, send ACK to server
			ACKPacket(address,1,SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}//try catch
	}//run

	private void ReadFromServer() throws IOException {
		int opcode = 0,blockNum=0;
		byte b;				//use for dis.readByte
		String content = "";		//save the data from server
		byte[] data = new byte[1024];

		dp = new DatagramPacket(data,data.length);	//ready the packet
		ds = new DatagramSocket(CLIENT_PORT);				//packet ready the receive the port
		ds.receive(dp);					//will stop here until read the data
		ByteArrayInputStream ab = new ByteArrayInputStream(data);	// data ↑
		DataInputStream dis = new DataInputStream(ab);

		opcode = dis.readShort();        //get the opcode 1,2,3,4,5
		blockNum = dis.readShort();
		while ((b = dis.readByte()) > 0) {
			content += (char) b;
			System.out.println(b);
		}

		log(opcode + "");
		log(blockNum + "");
		log(content);
	}//read from server

	private void log(String msg){
		gui.getTaLog().appendText(msg + "\n");
	}//log

}//class
