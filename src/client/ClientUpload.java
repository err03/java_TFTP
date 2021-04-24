package client;

import javafx.application.Platform;
import tftp.TFTP;

import java.io.*;
import java.net.*;

public class ClientUpload extends TFTP {
	private ClientGUI gui;
	private DatagramPacket dp;
	private DatagramSocket dsSend;
	private DatagramSocket dsReceive;
	private InetAddress address;
	private FileInputStream fis;
	private File f;
	String filename = "";    //get file name here
	String filepath = "";
	public ClientUpload() {
	}//constructor

	public ClientUpload(ClientGUI _gui,String _filename) {
		this.gui = _gui;
		this.filename = _filename;
	}//constructor

	@Override
	public void run() {
		System.out.println("click upload");    //click upload
		filepath = gui.getTfDirectory().getText() + "\\";
//		filename = "a.txt";    //get file name here
		String mode = "octet";
		try {
			address = InetAddress.getByName(gui.getTfServer().getText());    //get the localhost
			dsSend = new DatagramSocket();
			dsReceive = new DatagramSocket(CLIENT_PORT);    //packet ready the receive the port
			dp = WRQPacket(filename, mode, address, SERVER_PORT);
			dsSend.send(dp);
			log("------------ Sending --- to Server <WRQ>(" + WRQ + "):");
			log("-> File:[ " + filename + " ]");
			log("-> Mode:[ " + mode + " ]");

			uploadFileData(filename);
			UploadThreadClose();        //when finish, close the port
		} catch (IOException e) {
			e.printStackTrace();
		}//try catch
	}//run

	private void uploadFileData(String filename) throws IOException {
		int blockNum = 1;    //block number
		int ba = 0;
		byte[] a = new byte[512];        //total 512 length for read from server

		address = InetAddress.getByName(gui.getTfServer().getText());    //get the localhost
		f = new File(filepath + filename);        //use file here,
		if (!f.exists()) {
			log("File : [ " + f.getName() + " ] is not exists");
			return;
		}//if
//			System.out.println(f.getCanonicalFile());
		fis = new FileInputStream(f);

		while ((ba = fis.read(a)) > 0) {
//				System.out.println(ba);		//print out total data how many time that fill the byte[512]
			dp = DATAPacket(address, blockNum, a, SERVER_PORT);    //send data
			dsSend.send(dp);

			log("------------ Sending --- to Server <DATA>(" + DATA + "):");
			log("-> Block # :[ " + blockNum + " ]");
			log(a + "");

			a = new byte[512];        //need to new for next packet
			blockNum++;
			receiveACK();        //if the ACK is true, then it receive the ACK from server
		}//while
	}//upload file to server

	private void receiveACK() throws IOException {
		int opcode = 0, blockNum = 0;

		byte[] data = new byte[4];        //byte length 4

		dp = new DatagramPacket(data, data.length);    //ready the packet

		dsReceive.receive(dp);                    //will stop here until read the data

		ByteArrayInputStream ab = new ByteArrayInputStream(data);    // data ↑
		DataInputStream dis = new DataInputStream(ab);

		opcode = dis.readShort();        //get the opcode 1,2,3,4,5
		if (opcode < 1 || opcode > 5) {
			sendERROR(5, "Opcode can't < 1 || > 5");
			return;
		}
		blockNum = dis.readShort();

		log("------------ Receive --- from Server <ACK>(" + opcode + "):");
		log("-> Block # :[ " + blockNum + " ]");
	}//receive ACK from server

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

	//-------------------------------------------------------------------------------
	private void log(String msg) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui.getTaLog().appendText(msg + "\n");
			}
		});//pllatform run
	}//log

	private void UploadThreadClose() {
		dsSend.close();
		dsReceive.close();
	}//download thread close
}//class
