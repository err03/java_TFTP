package server;
import tftp.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ServerBegin extends Thread implements TFTFConstants{
	private ServerGUI gui;
	private DatagramSocket ds;
	private DatagramPacket dp;

	public ServerBegin(){} //constructor
	public ServerBegin(ServerGUI _gui){
		this.gui = _gui;
	}//constructor

	@Override
	public void run() {
		log("server start");
		byte[] data = new byte[1500];
		try {
			dp = new DatagramPacket(data,data.length);	//ready the packet
			ds = new DatagramSocket(PORT);				//packet ready the port
			ds.receive(dp);								//listen the port, until get the data
			/*
			when receive the packet, put into the byte array,
			let data input stream to read the byte array
			 */
			ByteArrayInputStream ab = new ByteArrayInputStream(data);
			DataInputStream dis = new DataInputStream(ab);
			int opcode = dis.readShort();		//get the opcode 1,2,3,4,5

			/*
			for opcode DATA:3
			 */
			int numOfBlock = dis.readShort();
			String content = "";
			while(true) {
				byte b = dis.readByte();
				if (b == 0) break;				//break;  still exception, think should be no enough byte[512],need more
				content += (char)b;
				System.out.println((char)b);
			}
			log(opcode + "");
			log(numOfBlock + "");
			log(content);

		} catch (SocketException e) {
			e.printStackTrace();
		}//try catch
		catch (IOException e) {
			e.printStackTrace();
		}
	}//run

	public void log(String msg){
		gui.getTaLog().appendText(msg + "\n");
	}//write the log in server log

	public void stopServer(){
		ds.close();
	}//stop the server
}//class
