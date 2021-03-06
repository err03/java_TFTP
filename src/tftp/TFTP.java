package tftp;

import java.io.*;
import java.net.*;

public class TFTP extends Thread implements TFTFConstants {

	@Override
	public void run() {
		System.out.println("run(): TFTP");
	}//run

	public DatagramPacket RRQPacket(String filename, String mode, InetAddress address, int port) {
		System.out.println("RRQPacket");
		int opcode = RRQ;                //1
		ByteArrayOutputStream ab = null;
		DataOutputStream dos;
		try {
			ab = new ByteArrayOutputStream(opcode + filename.length() + 1 + mode.length() + 1);
			dos = new DataOutputStream(ab);    //use DataOutputSteam to write data

			dos.writeShort(opcode);        //write the code : 1
			dos.writeBytes(filename);    //write filename
			dos.writeByte(0);    //write 0
			dos.writeBytes(mode);    //write mode
			dos.writeByte(0);    //write 0
			dos.close();        //close to flush the data

//			System.out.println("tftp prot:"+port);
		} catch (IOException e) {
			e.printStackTrace();
		}//try catch
		//depend
		return new DatagramPacket(ab.toByteArray(), ab.toByteArray().length, address, port);
	}//RRQ

	public DatagramPacket WRQPacket(String filename, String mode, InetAddress address, int port) {
		System.out.println("WRQPacket");
		int opcode = WRQ;                //2
		ByteArrayOutputStream ab = null;
		DataOutputStream dos;
		try {
			ab = new ByteArrayOutputStream(opcode + filename.length() + 1 + mode.length() + 1);
			dos = new DataOutputStream(ab);    //use DataOutputSteam to write data

			dos.writeShort(opcode);        //write the code : 2
			dos.writeBytes(filename);    //write filename
			dos.writeByte(0);    //write 0
			dos.writeBytes(mode);    //write mode
			dos.writeByte(0);    //write 0
			dos.close();        //close to flush the data

//			System.out.println("tftp prot:"+port);
		} catch (IOException e) {
			e.printStackTrace();
		}//try catch
		//depend
		return new DatagramPacket(ab.toByteArray(), ab.toByteArray().length, address, port);
	}//WRQ

	public DatagramPacket DATAPacket(InetAddress address, int blockNum, byte[] data, int port) {
		System.out.println("DATAPacket");
		byte[] b = new byte[516];        //the max data is 516
		int opcode = DATA;                //3
		ByteArrayOutputStream ab = null;
		DataOutputStream dos;
		try {
			ab = new ByteArrayOutputStream(b.length);
			dos = new DataOutputStream(ab);    //use DataOutputSteam to write data

			dos.writeShort(opcode);        //write the code : 3
			dos.writeShort(blockNum);    //write the block num
			dos.write(data);    //write the data[512]
			dos.close();        //close to flush the data

//			System.out.println("tftp prot:"+port);
		} catch (IOException e) {
			e.printStackTrace();
		}//try catch
		//516 //address and port send to client or server
		return new DatagramPacket(ab.toByteArray(), ab.toByteArray().length, address, port);
	}//DATA

	public DatagramPacket ACKPacket(InetAddress address, int blockNum, int port) {
		System.out.println("ACKPacket");
		byte[] b = new byte[4];        //the max data is 4
		int opcode = ACK;                //4
		ByteArrayOutputStream ab = null;
		DataOutputStream dos;
		try {
			ab = new ByteArrayOutputStream(b.length);
			dos = new DataOutputStream(ab);    //use DataOutputSteam to write data

			dos.writeShort(opcode);        //write the code : 4
			dos.writeShort(blockNum);    //write the block num
			dos.close();        //close to flush the data
//			System.out.println("tftp prot:"+port);
		} catch (IOException e) {
			e.printStackTrace();
		}//try catch
		//516 //address and port send to client or server
		return new DatagramPacket(ab.toByteArray(), ab.toByteArray().length, address, port);
	}//ACK

	public DatagramPacket ERRORPacket(int Ecode, byte[] data, InetAddress address, int port) {
		System.out.println("ERROR packet");
		byte[] b = new byte[126];        //the max data is 126
		int opcode = ERROR;
		ByteArrayOutputStream bas = null;
		DataOutputStream dos;
		try {
			bas = new ByteArrayOutputStream(b.length);
			dos = new DataOutputStream(bas);

			dos.writeShort(opcode);
			dos.writeShort(Ecode);
			dos.write(data);
			dos.write(0);
			dos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return new DatagramPacket(bas.toByteArray(), bas.toByteArray().length, address, port);
	}//ERROR
	//-----------------------------------------------------------------

}//class
