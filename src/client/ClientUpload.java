package client;

import tftp.TFTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;

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
		int blockNum = 0;
		try {
			address = InetAddress.getByName(gui.getTfServer().getText());	//get the localhost

			f = new File("Lincoln.txt");		//use file here,
//			System.out.println(f.getCanonicalFile());
			fis = new FileInputStream(f);
			int ba = 0;
			byte[] a = new byte[512];
			int len = 0;
			while((ba=fis.read(a)) > 0){
//				System.out.println(ba);		//print out total data how many time that fill the byte[512]
				DATAPacket(address,blockNum,a,SERVER_PORT);	//just data, send message
				a = new byte[512];
				blockNum++;
			}//while
		} catch (IOException e) {
			e.printStackTrace();
		}//try..catch
	}//run
}//class
