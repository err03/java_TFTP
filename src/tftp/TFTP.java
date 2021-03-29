package tftp;

import java.io.*;
import java.net.*;

public class TFTP extends Thread implements TFTFConstants{
	public InetAddress address;
	public DatagramPacket dp;
	public DatagramSocket ds;
	public FileInputStream fis;
	public File f;

	@Override
	public void run() {
		System.out.println("TFTP");
	}//run

	public void RRQPacket(){

	}//RRQ
	public void WRQPacket(){

	}//WRQ
	public void DATAPacket(InetAddress address,int blockNum,byte[] data,int port){
		System.out.println("DATAPacket");
		byte[] b = new byte[516];		//the max data is 516
		int opcode = DATA;	//3

		try {
			ds = new DatagramSocket();	//no port, use for send packet
			ByteArrayOutputStream ab = new ByteArrayOutputStream(b.length);
			DataOutputStream dos = new DataOutputStream(ab);	//use DataOutputSteam to write data
			dos.writeShort(opcode);		//write the code : 3
			dos.writeShort(blockNum);	//write the block num
			dos.write(data);	//write the data[512]
			dos.close();		//close to flush the data
			System.out.println("tftp prot:"+port);
														//516 //address and port send to client or server
			dp = new DatagramPacket(ab.toByteArray(),ab.toByteArray().length,address,port);
			ds.send(dp);
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}//try catch
	}//DATA
	public void ACKPacket(){

	}//ACK
	public void ERRORPacket(){

	}//ERROR

}//class
