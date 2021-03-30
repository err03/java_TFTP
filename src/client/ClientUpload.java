package client;

import tftp.TFTP;

import java.io.*;
import java.net.*;

public class ClientUpload extends TFTP {
	private ClientGUI gui;
	/**
	 * {InetAddress address},{FileInputStream fis}
	 * and {File f} from extends TFTP
	 */

	public ClientUpload(){}//constructor
	public ClientUpload(ClientGUI _gui){
		this.gui = _gui;
	}//constructor

	@Override
	public void run() {
		System.out.println("click upload");	//click upload
		String filename = "filename.txt";	//get file name here
		try {
			ds = new DatagramSocket();
			dp = WRQPacket(filename,SERVER_PORT);
			ds.send(dp);

			uploadFileData(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}//try catch
	}//run

	private void uploadFileData(String filename) throws IOException {
		int blockNum = 0;	//block number
		int ba = 0;
		byte[] a = new byte[512];		//total 512 length for read from server

		address = InetAddress.getByName(gui.getTfServer().getText());	//get the localhost
		ds = new DatagramSocket();	//no port, use for send packet
		f = new File(filename);		//use file here,
//			System.out.println(f.getCanonicalFile());
		fis = new FileInputStream(f);

		while((ba=fis.read(a)) > 0){
//				System.out.println(ba);		//print out total data how many time that fill the byte[512]
			dp = DATAPacket(address,blockNum,a,SERVER_PORT);	//send data
			ds.send(dp);
			a = new byte[512];		//need to new for next packet
			blockNum++;
			if(receiveACK()){		//if the ACK is true, then it receive the ACK from server
				continue;
			}//if
		}//while
	}//upload file to server

	private boolean receiveACK() throws IOException {
		int opcode = 0,blockNum=0;

		byte[] data = new byte[4];		//byte length 4

		dp = new DatagramPacket(data,data.length);	//ready the packet
		ds = new DatagramSocket(CLIENT_PORT);				//packet ready the receive the port

		ds.receive(dp);					//will stop here until read the data

		ByteArrayInputStream ab = new ByteArrayInputStream(data);	// data ↑
		DataInputStream dis = new DataInputStream(ab);

		opcode = dis.readShort();        //get the opcode 1,2,3,4,5
		blockNum = dis.readShort();

		log(opcode + "");
		log(blockNum + "");
		return true;
	}//receive ACK from server

	private void log(String msg){
		gui.getTaLog().appendText(msg + "\n");
	}//log
}//class
