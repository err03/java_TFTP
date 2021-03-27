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
	public void DATAPacket(){
		byte[] b = new byte[516];		//the max data is 516, for output
		System.out.println("data");
		int blockNum = 1;	//1
		int opcode = DATA;	//3

		try {
			address = InetAddress.getByName("localhost");
			ds = new DatagramSocket();
			ByteArrayOutputStream ab = new ByteArrayOutputStream(b.length);
			DataOutputStream dos = new DataOutputStream(ab);
			dos.writeShort(opcode);		//write the data code : 3
			dos.writeShort(blockNum);	//write the block num

			f = new File("Lincoln.txt");		//use file here,
			System.out.println(f.getCanonicalFile());
			fis = new FileInputStream(f);
			int ba = 0;
			String value = "";
			while((ba=fis.read()) > 0){
				value += (char)ba;
			}
//			System.out.println(value);

			dos.writeBytes(value);	//write the data
			dos.writeByte(0);
			dos.close();

			dp = new DatagramPacket(ab.toByteArray(),ab.toByteArray().length,address,PORT);
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
