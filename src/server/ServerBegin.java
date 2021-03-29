package server;
import client.ClientThread;
import tftp.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ServerBegin extends TFTP implements TFTFConstants{
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
		byte[] data = new byte[1024];
		try {
			dp = new DatagramPacket(data,data.length);	//ready the packet
			ds = new DatagramSocket(SERVER_PORT);				//packet ready the receive the port

			while(true) {		//while true to keep receive the data
				ds.receive(dp);                                //listen the port, until get the data
				/*
				when receive the packet, put into the byteArray,
				let dataInputStream to read the byteArray
				 */
				ByteArrayInputStream ab = new ByteArrayInputStream(data);
				DataInputStream dis = new DataInputStream(ab);
				int opcode = dis.readShort();        //get the opcode 1,2,3,4,5

				/*
				for opcode DATA:3
				 */
				int numOfBlock = dis.readShort();	//read the num of block

				String content = "";
				byte b;
				while ((b = dis.readByte()) > 0) {
					content += (char) b;
				}
				log(opcode + "");
				log(numOfBlock + "");
				log(content);

				//send to client
				System.out.println("server send to client");
				byte[] hlder = "hello yoouo connect".getBytes();
				DATAPacket(dp.getAddress(),1,hlder,CLIENT_PORT);
			}//while
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
