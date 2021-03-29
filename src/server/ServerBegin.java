package server;

import tftp.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ServerBegin extends TFTP implements TFTFConstants{
	private ServerGUI gui;
	/**
	 * {DatagramPacket dp} and {DatagramSocket ds} from extends TFTP
	 */
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
			ds = new DatagramSocket(SERVER_PORT);		//packet ready the receive the port

			while(true) {	//while true to keep receive the data
				ds.receive(dp);		//listen the port, until get the data
				/*
				when receive the packet, put into the byteArray,
				let dataInputStream to read the byteArray
				 */
				ByteArrayInputStream ab = new ByteArrayInputStream(data);	//data ↑
				DataInputStream dis = new DataInputStream(ab);
				int opcode = dis.readShort();        //get the opcode 1,2,3,4,5
				log(opcode + "");

				switch (opcode){		//RRQ,WRQ,DATA,ACK,ERROR from implements TFTPConstants
					case RRQ:
						opcode1();
						break;
					case WRQ:
						opcode2();
						break;
					case DATA:
						opcode3(dis);		//might will add return type
						break;
					case ACK:
						opcode4();
						break;
					case ERROR:
						opcode5();
						break;
				}//switch

				WriteToClient("Hello");	//test to write to client
			}//while
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}//try catch
	}//run

	//-------------------------OpCode [1,2,3,4,5] ↓------------------------------------------------------
	private void opcode1(){}//opcode RRQ:1
	private void opcode2(){}//opcode WRQ:2
	private void opcode3(DataInputStream dis) throws IOException {
		/*
		for opcode DATA:3
		 */
		int numOfBlock = dis.readShort();	//read the num of block

		String content = "";
		byte b;
		while ((b = dis.readByte()) > 0) {
			content += (char) b;
		}

		log(numOfBlock + "");
		log(content);
	}//opcode DATA:3
	private void opcode4(){}//opcode ACK:4
	private void opcode5(){}//opcode ERROR:5
//--------------------------------OpCode[1,2,3,4,5] ↑-------------------------------------------------
	private void WriteToClient(String msg){
		//send to client
		System.out.println("server send to client");
		byte[] holder = msg.getBytes();
		DATAPacket(dp.getAddress(),1,holder,CLIENT_PORT);
	}//write to client

	public void log(String msg){
		gui.getTaLog().appendText(msg + "\n");
	}//write the log in server log

	public void stopServer(){
		ds.close();
	}//stop the server
}//class
