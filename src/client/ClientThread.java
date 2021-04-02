package client;

import javafx.application.Platform;
import tftp.TFTP;

import java.io.*;
import java.net.*;


public class ClientThread extends TFTP {
	private ClientGUI gui;
	private DatagramPacket dp;
	private DatagramSocket dsSend;
	private DatagramSocket dsReceive;
	private InetAddress address;
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
			dsSend = new DatagramSocket();		//use for send
			dsReceive = new DatagramSocket(CLIENT_PORT);		//packet ready the receive the port
			dsReceive.setSoTimeout(1000);
			address = InetAddress.getByName(gui.getTfServer().getText());	//get the localhost
			/*
			testing, send data to client
			 */
			int blockNum = 1;
			String msg = "Testing Msg: hello";
			byte[] a = msg.getBytes();
			dp = DATAPacket(address,blockNum,a,SERVER_PORT);
			dsSend.send(dp);
			log("------------ Sending --- to Server <DATA>("+DATA+"):");
			log("-> Block # :[ " +blockNum+" ]");
			log(msg);

			receiveACK();	//ready to read from server
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Time Out");
			ClientThreadClose();
		}finally {
			ClientThreadClose();
		}
	}//run

	/*
	receive the ACK from server
	 */
	private boolean receiveACK() throws IOException {
		int opcode = 0,blockNum=0;

		byte[] data = new byte[4];

		dp = new DatagramPacket(data,data.length);	//ready the packet
		dsReceive.receive(dp);					//will stop here until read the data
		ByteArrayInputStream ab = new ByteArrayInputStream(data);	// data ↑
		DataInputStream dis = new DataInputStream(ab);
		opcode = dis.readShort();        //get the opcode 1,2,3,4,5
		blockNum = dis.readShort();

		log("------------ Receive --- from Server <ACK>("+opcode+"):");
		log("-> Block # :[ " + blockNum+" ]");

		return true;
	}//receive ACK from server

	private void log(String msg){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui.getTaLog().appendText(msg + "\n");
			}
		});//pllatform run
	}//log

	private void ClientThreadClose(){
		dsSend.close();
		dsReceive.close();
	}//download thread close
}//class
