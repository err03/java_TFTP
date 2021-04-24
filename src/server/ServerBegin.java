package server;

import javafx.application.Platform;
import tftp.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ServerBegin extends TFTP implements TFTFConstants {
	private ServerGUI gui;
	private DatagramPacket dp;
	private DatagramSocket dsSend;
	private DatagramSocket dsReceive;
	public String WRQfilename = "";        //WRQ for write it to local
	private String filepath = "";

	public ServerBegin() {
	} //constructor

	public ServerBegin(ServerGUI _gui) {
		this.gui = _gui;
	}//constructor

	@Override
	public void run() {
		log("------------------ Server Start ------------------");
		filepath = gui.getTfDirectory().getText() + "\\";
		byte[] data = new byte[1024];
		try {
			dsReceive = new DatagramSocket(SERVER_PORT);        //packet ready the receive the port
			dsSend = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}//try catch
//			dsReceive.setSoTimeout(1000);
		while (true) {    //while true to keep receive the data
			try {
				System.out.println("server is waiting packet...");
				dp = new DatagramPacket(data, data.length);    //ready the packet
				dsReceive.receive(dp);        //listen the port, until get the data
				/*
				when receive the packet, put into the byteArray,
				let dataInputStream to read the byteArray
				 */
				System.out.println("server receive the packet");
				ByteArrayInputStream ab = new ByteArrayInputStream(data);    //data ↑
				DataInputStream dis = new DataInputStream(ab);

				int opcode = dis.readShort();        //get the opcode 1,2,3,4,5
				if (opcode < 1 || opcode > 5) {
					sendERROR(5, "Opcode can't < 1 || > 5");
					continue;
				}
				//RRQ,WRQ,DATA,ACK,ERROR from implements TFTPConstants
				switch (opcode) {
					case RRQ:
						opcodeRRQ(dis);
						break;
					case WRQ:
						opcodeWRQ(dis);
						break;
					case DATA:
						opcodeDATA(dis);
						break;        //might will add return type
					case ACK:
						opcodeACK();
						break;
					case ERROR:
						opcodeERRR();
						break;
				}//switch

				data = new byte[1024];        //new a byte
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				sendERROR(1, "File not found");
			} catch (IOException e) {
				e.printStackTrace();
				sendERROR(0, "Undefine Error.. Message:" + e.getMessage());
			}//try catch
		}//while
	}//run

	//-------------------------OpCode [1,2,3,4,5] ↓------------------------------------------------------
	private void opcodeRRQ(DataInputStream dis) throws IOException {	//when receive client's RRQ
		String RRQfilename = "";
		String mode = "";
		while(true){
			byte b = dis.readByte();
			if(b == 0)
				break;
			RRQfilename += (char)b;
		}//while
		log("------------ Receive --- Client is <RRQ>("+RRQ+"):");
		log("-> File:[ " + RRQfilename+" ]");
		while(true){
			byte b = dis.readByte();
			if(b == 0)
				break;
			mode += (char)b;
		}//while
		log("-> Mode:[ " + mode + " ]");

		/*
		read the data from local, then write to client
		 */
		int blockNum = 1;	//block number
		int ba = 0;
		byte[] a = new byte[512];		//total 512 length for read from server

		File f = new File(filepath+RRQfilename);        //use file here,
//		System.out.println(f.getCanonicalFile());

		FileInputStream fis = new FileInputStream(f);

		while((ba= fis.read(a)) > 0){
//			System.out.println(ba);		//print out total data how many time that fill the byte[512]
			WriteToClient(blockNum,a);	//** write to client
			blockNum++;
			a = new byte[512];		//need to new for next packet
			receiveACK();		//until receive the AC, otherwise it will stuck
		}//while
		System.out.println("Server finish sending data");
	}//opcode RRQ:1

	private void opcodeWRQ(DataInputStream dis) throws IOException {
		log("------------Receive --- Client is <WRQ>("+WRQ+"):");
		String filename="";
		String mode = "";
		while(true){
			byte b = dis.readByte();
			if(b == 0)
				break;
			filename += (char)b;
		}//while
		WRQfilename = filename;		//renew the filename,
		log("-> File:[ " + WRQfilename+" ]");
		while(true){
			byte b = dis.readByte();
			if(b == 0)
				break;
			mode += (char)b;
		}//while
		log("-> Mode:[ " + mode + " ]");
	}//opcode WRQ:2

	private void opcodeDATA(DataInputStream dis) throws IOException {	//use for receive the data
		/*	FileOutputStream to write file to local.
		for opcode DATA:3
		and block num#
		 */
		int numOfBlock = dis.readShort();	//read the num of block#

		String fileContent = "";
		String fileData = "";
		byte b;
		while ((b = dis.readByte()) > 0) {
			fileData += b + ",";
			fileContent += (char) b;
		}

		log("------------ Receive --- Client is <DATA>("+DATA+"):");
		log("-> Block # :[ " + numOfBlock+" ]");
		log(fileData);
		/*
		after receive the DATA, send ACK
		 */
		sendACK(numOfBlock);		//send ACK
		if(!WRQfilename.equals("")) {		//will happen error; lat
			writeFileToLocal(fileContent.getBytes());    //write to file
		}//if
//		System.out.println("--- filename:"+WRQfilename);
	}//opcode DATA:3
	private void opcodeACK() throws IOException {	//receive client's ACK, then send data to client
		/**	only for testing, is it receive correct ACK
		 * didn't used, when server receive the data, receiveACK() is in while{} at op1
		 */
		System.out.println("Server ACK--------");
	}//opcode ACK:4
	private void opcodeERRR(){}//opcode ERROR:5

	//--------------------------------Receive and Write-------------------------------------------------
	public void WriteToClient(int blockNum, byte[] holder) throws IOException {
		//send to client
		System.out.println("server send to client");

		dsSend.send(DATAPacket(dp.getAddress(), blockNum, holder, CLIENT_PORT));

		log("------------ Sending --- to Server <DATA>(" + DATA + "):");
		log("-> Block # :[ " + blockNum + " ]");
		log(holder + "");
	}//write to client

	public void receiveACK() throws IOException {
		int opcode, blockNum;

		byte[] data = new byte[4];

		dp = new DatagramPacket(data, data.length);    //ready the packet
		System.out.println("Receving ACK...");
		dsReceive.receive(dp);                    //will stop here until read the data
		System.out.println("received ACK");
		ByteArrayInputStream ab = new ByteArrayInputStream(data);    // data ↑
		DataInputStream dis = new DataInputStream(ab);

		opcode = dis.readShort();        //get the opcode 1,2,3,4,5
		blockNum = dis.readShort();

		log("------------ Receive --- from Server <ACK>(" + opcode + "):");
		log("-> Block # :[ " + blockNum + " ]");
	}//receive ACK from Client

	public void sendACK(int blockNum) throws IOException {
		dp = ACKPacket(dp.getAddress(), blockNum, CLIENT_PORT);
		dsSend.send(dp);
		log("------------ Send --- to Client <ACK>(" + ACK + "):");
		log("-> Block # :[ " + blockNum + " ]");
	}//send ACK to client

	private void sendERROR(int Ecode, String errMsg) {
		try {
			dp = ERRORPacket(Ecode, errMsg.getBytes(), dp.getAddress(), CLIENT_PORT);
			dsSend.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}

		log("------------ Send --- to Client <ERROR>(" + ERROR + "):");
		log("-> Message # :[ " + errMsg + " ]");
	}//send ERROR to client

	//--------------------------------------------------------------------------------
	public void writeFileToLocal(byte[] data) throws IOException {
//		gui.getTfDirectory()
		FileOutputStream fos = new FileOutputStream( filepath + WRQfilename, true);
		fos.write(data);
		fos.close();
	}//write the file to local

	public void log(String msg) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui.getTaLog().appendText(msg + "\n");
			}
		});//pllatform run
	}//log

	public void stopServer() {
		log("------------------ Server Stop ------------------ ");
		dsSend.close();
		dsReceive.close();
	}//stop the server
}//class
