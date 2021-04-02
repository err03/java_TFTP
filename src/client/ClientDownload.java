package client;

import javafx.application.Platform;
import tftp.TFTP;
import java.io.*;
import java.net.*;

public class ClientDownload extends TFTP {
	private ClientGUI gui;
	private DatagramPacket dp;
	private DatagramSocket dsSend;
	private DatagramSocket dsReceive;
	private InetAddress address;
	private boolean keepReceived = true;
	private FileOutputStream fos;
	String filename = "";

	public ClientDownload(){}//constructor
	public ClientDownload(ClientGUI _gui){
		this.gui = _gui;
	}//constructor

	@Override
	public void run() {
		filename = "Lincoln512.txt";
		String mode = "octet";
		int blockNum = 0;	//to send ACK
		try {
			/*
			send RRQ to server
			 */
			address = InetAddress.getByName(gui.getTfServer().getText());	//get the localhost
			dsSend = new DatagramSocket();	//use for send
			dsReceive = new DatagramSocket(CLIENT_PORT);				//packet ready the receive the port
			dsReceive.setSoTimeout(1000);	//set the timeout

			dp = RRQPacket(filename,mode,address,SERVER_PORT);
			dsSend.send(dp);

			log("------------ Sending --- to Server <RRQ>("+RRQ+"):");
			log("-> File:[ " + filename+" ]");
			log("-> Mode:[ " + mode + " ]");
			/*
				receive data from server
			 */
			while(true) {
				blockNum++;
				ReadDataFromServer();	//while true, to keep read data from server

				//when receive the data, send ACK to server
				dp = ACKPacket(address, blockNum, SERVER_PORT);
				dsSend.send(dp);
				log("------------ Sending --- to Server <ACK>(" + ACK + "):");
				log("-> Block # :[ " + blockNum + " ]");
			}//while
		} catch (IOException e) {
//			e.printStackTrace();
			System.out.println("Time out");
			DownloadThreadClose();
		}//try...catch
	}//run

	private void ReadDataFromServer() throws IOException {
		int opcode = 0,blockNum=0;
		byte b;				//use for dis.readByte

		String fileContent = "";		//save the data from server
		String fileData = "";		//receive the file Data
		byte[] data = new byte[1024];

		dp = new DatagramPacket(data,data.length);	//ready the packet
		System.out.println("Client is Receving...");
		dsReceive.receive(dp);					//will stop here until read the data
		System.out.println("Client received packet");
		ByteArrayInputStream ab = new ByteArrayInputStream(data);	// data ↑
		DataInputStream dis = new DataInputStream(ab);

		opcode = dis.readShort();        //get the opcode 1,2,3,4,5
		if (opcode < 1 || opcode > 5) {
			sendERROR(5,"Opcode can't < 1 || > 5");
			return;
		} else if(opcode == 5){
			receiveERROR(opcode,dis);
			throw new IOException();	//throw the exception to end the program
		}//if else if

		blockNum = dis.readShort();
		while ((b = dis.readByte()) > 0) {
			fileData += b + ",";
			fileContent += (char) b;
//			System.out.println(b);
		}//while
		log("------------ Receive --- from Server <DATA>("+opcode+"):");
		log("-> OpCode # :[ " + opcode+" ]");
		log("-> Block # :[ " + blockNum+" ]");
		log(fileData);
		writeFileToLocal(fileContent.getBytes());		//write file to local
//		System.out.println(fileContent);
//		System.out.println(fileData);
	}//read from server

	private void receiveERROR(int opcode,DataInputStream dis) throws IOException {
		log("------------ Receive --- from Server <ERROR>("+opcode+"):");
		int Ecode = dis.readShort();
		switch (Ecode){
			case UNDEF:
				log("-> Ecode # :[ " + UNDEF+" ]");
				log("-> Ecode Message :[ Undefined error ]");
				break;
			case NOTFD:
				log("-> Ecode # :[ " + NOTFD+" ]");
				log("-> Ecode Message :[ File not found ]");
				break;
			case ACCESS:
				log("-> Ecode # :[ " + ACCESS+" ]");
				log("-> Ecode Message :[ Access Violation (Can't open the file) ]");
				break;
			case ILLOP:
				log("-> Ecode # :[ " + ILLOP+" ]");
				log("-> Ecode Message :[ Illegal Opcode ]");
				break;
		}//switch
	}//receive the ERROR from server

	private void writeFileToLocal(byte[] data) throws IOException {
		//C:\Users\error\Desktop\client
		fos = new FileOutputStream(new File("C:\\Users\\error\\Desktop\\client\\"+filename),true);
		fos.write(data);
		fos.close();
	}//write the file to local

	private void sendERROR(int Ecode,String errMsg) {
		try {
			dp = ERRORPacket(Ecode,errMsg.getBytes(),dp.getAddress(),CLIENT_PORT);
			dsSend.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}

		log("------------ Send --- to Client <ERROR>("+ERROR+"):");
		log("-> Message # :[ " + errMsg+" ]");
	}//send ERROR to client

	//--------------------------------------------------------------------------------------
	private void log(String msg){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui.getTaLog().appendText(msg + "\n");
			}
		});//pllatform run
	}//log

	private void DownloadThreadClose(){
		dsSend.close();
		dsReceive.close();
	}//download thread close
}//class
