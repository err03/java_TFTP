package client;

import tftp.TFTFConstants;
import tftp.TFTP;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;


public class ClientConnect extends TFTP {
	private ClientGUI gui;

	public ClientConnect(){}//constructor
	public ClientConnect(ClientGUI _gui){
		this.gui = _gui;
	}//constructor

	@Override
	public void run() {
		System.out.println("Connect");
		DATAPacket();
	}//run

	private void log(String msg){
		gui.getTaLog().appendText(msg + "\n");
	}//log
}//class
