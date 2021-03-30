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
				System.out.println("server is waiting packet...");
				ds.receive(dp);		//listen the port, until get the data
				/*
				when receive the packet, put into the byteArray,
				let dataInputStream to read the byteArray
				 */
				System.out.println("server receive the packet");
				ByteArrayInputStream ab = new ByteArrayInputStream(data);	//data ↑
				DataInputStream dis = new DataInputStream(ab);

				int opcode = dis.readShort();        //get the opcode 1,2,3,4,5

				switch (opcode){		//RRQ,WRQ,DATA,ACK,ERROR from implements TFTPConstants
					case RRQ:
						opcode1(dis);
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

//				WriteToClient(DATA,"Hello");	//test to write to client
			}//while
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}//try catch
	}//run

	//-------------------------OpCode [1,2,3,4,5] ↓------------------------------------------------------
	private void opcode1(DataInputStream dis) throws IOException {	//when receive client's RRQ
		log("Client is <RRQ>("+RRQ+")");

		String filename = "";
		String mode = "";
		while(true){
			byte b = dis.readByte();
			if(b == 0)
				break;
			filename += (char)b;
		}
		log("Client is RRQ,file:"+filename);

		while(true){
			byte b = dis.readByte();
			if(b == 0)
				break;
			mode += (char)b;
		}
		log("Client is RRQ,mode:"+mode);

		/*
		read the data from local, then write to client
		 */
		int blockNum = 0;	//block number
		int ba = 0;
		byte[] a = new byte[512];		//total 512 length for read from server

		f = new File("filename.txt");		//use file here,
//		System.out.println(f.getCanonicalFile());
		fis = new FileInputStream(f);
		while((ba=fis.read(a)) > 0){
//			System.out.println(ba);		//print out total data how many time that fill the byte[512]
			WriteToClient(blockNum,a);	//** write to client
			blockNum++;
			a = new byte[512];		//need to new for next packet
		}//while
	}//opcode RRQ:1

	private void opcode2(){
		log("Client is <WRQ>("+WRQ+")");
	}//opcode WRQ:2
	private void opcode3(DataInputStream dis) throws IOException {	//use for receive the data
		/*	FileOutputStream to write file to local.
		for opcode DATA:3
		and block num#
		 */
		int numOfBlock = dis.readShort();	//read the num of block#

		String content = "";
		byte b;
		while ((b = dis.readByte()) > 0) {
			content += (char) b;
		}

		log("<DATA>("+DATA+")Block#:"+numOfBlock);
		log(content);
	}//opcode DATA:3
	private void opcode4() throws IOException {	//receive client's ACK, then send data to client
		if(receiveACK()){

		}//if
	}//opcode ACK:4
	private void opcode5(){}//opcode ERROR:5

//--------------------------------Receive and Write-------------------------------------------------
	private void WriteToClient(int blockNum,byte[] holder) throws IOException {
		//send to client
		System.out.println("server send to client");

		ds = new DatagramSocket();	//no port, use for send packet
		ds.send(DATAPacket(dp.getAddress(),blockNum,holder,CLIENT_PORT));
	}//write to client

	private boolean receiveACK() throws IOException {
		int opcode = 0,blockNum=0;

		byte[] data = new byte[4];

		dp = new DatagramPacket(data,data.length);	//ready the packet
		ds = new DatagramSocket(SERVER_PORT);				//packet ready the receive the port
		ds.receive(dp);					//will stop here until read the data
		ByteArrayInputStream ab = new ByteArrayInputStream(data);	// data ↑
		DataInputStream dis = new DataInputStream(ab);

		opcode = dis.readShort();        //get the opcode 1,2,3,4,5
		blockNum = dis.readShort();

		log(opcode + "");
		log(blockNum + "");
		return true;
	}//receive ACK from Client

	//--------------------------------------------------------------------------------
	public void log(String msg){
		gui.getTaLog().appendText(msg + "\n");
	}//write the log in server log

	public void stopServer(){
		ds.close();
	}//stop the server
}//class
