package tftp;

public interface TFTFConstants {
	public static final int SERVER_PORT = 69;	//for server tftp use
	public static final int CLIENT_PORT = 3560;	//for client tftp use

	public static final int RRQ = 1;	//client read from server(download)
	public static final int WRQ = 2;	//client write to server (upload))
	public static final int DATA = 3;	//data client transfer to server
	public static final int ACK = 4;	//to confirm the data, when read/write the data
	public static final int ERROR = 5;	//error 8 error code

	//error code
	public static final int UNDEF = 0;
	public static final int NOTFD = 1;
	public static final int ACCESS = 2;
	public static final int ILLOP = 4;
}
