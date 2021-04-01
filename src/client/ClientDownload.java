package client;

import javafx.application.Platform;
import tftp.TFTP;

import java.awt.desktop.ScreenSleepEvent;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;

public class ClientDownload extends TFTP {
	private ClientGUI gui;
	private DatagramPacket dp;
	private DatagramSocket dsSend;
	private DatagramSocket dsReceive;
	private InetAddress address;
	private boolean keepReceived = true;
	public ClientDownload(){}//constructor
	public ClientDownload(ClientGUI _gui){
		this.gui = _gui;
	}//constructor

	@Override
	public void run() {
		String filename = "Lincoln.txt";
		String mode = "octet";
		int blockNum = 0;	//to send ACK
		int touchTime = 1;
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
		}
	}//run

	private void ReadDataFromServer() throws IOException {
		int opcode = 0,blockNum=0;
		byte b;				//use for dis.readByte
		byte end = 0;
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
		blockNum = dis.readShort();
		while ((b = dis.readByte()) > 0) {
			fileData += b + ",";
			fileContent += (char) b;
//			System.out.println(b);
		}
		log("------------ Receive --- from Server <DATA>("+opcode+"):");
		log("-> OpCode # :[ " + opcode+" ]");
		log("-> Block # :[ " + blockNum+" ]");
		log(fileData);
//		System.out.println(fileContent);
//		System.out.println(fileData);
	}//read from server

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
