package client;

import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import style.*;        //import the custom style
import tftp.TFTP;

import java.io.File;
import java.util.Optional;

public class ClientGUI extends Application implements EventHandler<ActionEvent> {
	private Scene scene;
	private Stage stage;

	//set the elements
	private Label lbServer = new Label("Server:");
	private TextField tfServer = new TextField("localhost");
	private Button btnConnect = new Button("Connect");

	private Label lbDirectory = new Label("Directory:");
	private TextField tfDirectory = new TextField();
	private Button btnChangeDir = new Button("Change Dir");

	private Button btnUpload = new Button("Upload");
	private Button btnDownload = new Button("Download");

	private TextArea taLog = new TextArea();
	private Button btnClear = new Button("Clear");
	//set the pane
	private FlowPane serverPane = new FlowPane(8, 8);
	private FlowPane directoryPane = new FlowPane(8, 8);
	private FlowPane buttonPane = new FlowPane(8, 8);
	private VBox root = new VBox(8);

	private FontStyle fs;

	/*main*/
	public static void main(String[] args) {
		launch(args);
	}//main

	// Called automatically after launch sets up javaFX
	public void start(Stage _stage) throws Exception {
		stage = _stage;
		stage.setTitle("Client GUI");
		scene = new Scene(root, 550, 300);

		//set the  style
		fs = new MonoSpacedFont();    //get the monospace font style
		tfServer.setFont(fs.getFont());
		tfDirectory.setFont(fs.getFont());
		taLog.setFont(fs.getFont());

		//set the element setting
		tfServer.setPrefColumnCount(18);    //set the width
		tfDirectory.setPrefColumnCount(40);
		tfDirectory.setDisable(true);        //set disable
		serverPane.setAlignment(Pos.CENTER);    //set the position
		directoryPane.setAlignment(Pos.CENTER_LEFT);
		buttonPane.setAlignment(Pos.CENTER_RIGHT);
		btnClear.setAlignment(Pos.CENTER_RIGHT);
		stage.setX(100);
		stage.setY(100);
		taLog.autosize();
		taLog.setPrefHeight(500);

		//pane add children
		serverPane.getChildren().addAll(lbServer, tfServer, btnConnect);
		directoryPane.getChildren().addAll(lbDirectory, tfDirectory, btnChangeDir);
		buttonPane.getChildren().addAll(btnUpload, btnDownload);
		//add all to root
		root.getChildren().addAll(serverPane, directoryPane, buttonPane, taLog, btnClear);

		//set the button action
		btnChangeDir.setOnAction(this);
		btnConnect.setOnAction(this);
		btnDownload.setOnAction(this);
		btnUpload.setOnAction(this);
		btnClear.setOnAction(this);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent windowEvent) {
				System.exit(0);
			}
		});//close request

		stage.setScene(scene);
		stage.show();
	}//start

	public void handle(ActionEvent evt) {
		// Get the button that was clicked
		Button btn = (Button) evt.getSource();

		// Switch on its name
		switch (btn.getText()) {
			case "Connect":
				TFTP tftpConnect = new ClientThread(this);
				tftpConnect.start();
				System.out.println("click coonnects");
				break;
			case "Change Dir":
				changeDir();
				break;
			case "Upload":
				String filenameUpload = dialogTypeFilename();	//get the file name
				if(filenameUpload.equals("")){return;}
				TFTP tftpUpload = new ClientUpload(this,filenameUpload);
				tftpUpload.start();
				System.out.println("click upload");
				break;
			case "Download":
				String filenameDownload = dialogTypeFilename();	//get the file name
				if(filenameDownload.equals("")){return;}
				TFTP tftpDownload = new ClientDownload(this,filenameDownload);
				tftpDownload.start();
				System.out.println("click doownload");
				break;
			case "Clear":
				taLog.setText("");
				break;
		}
	}//handle

	private void changeDir(){
		DirectoryChooser dc = new DirectoryChooser();
		File directory = dc.showDialog(null);
		tfDirectory.setText(directory.toString());
//		System.out.println(directory);
	}//change dir

	private String dialogTypeFilename(){
		TextInputDialog ti = new TextInputDialog("");
		Optional<String> input = ti.showAndWait();
		if(input.isEmpty()){
			return "";
		}
		return input.get();
	}//getUploadFile, use textInputDialog

	//set the setter
	public TextField getTfServer() {
		return this.tfServer;
	}//get tf server

	public TextField getTfDirectory() {
		return this.tfDirectory;
	}//get tf directory

	public TextArea getTaLog() {
		return this.taLog;
	}//get ta log

}//class
