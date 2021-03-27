package server;
import tftp.*;
public class ServerBegin extends Thread implements TFTFConstants{
	private ServerGUI gui;

	public ServerBegin(){} //constructor
	public ServerBegin(ServerGUI _gui){
		this.gui = _gui;
	}//constructor

	@Override
	public void run() {

	}//run
}//class
