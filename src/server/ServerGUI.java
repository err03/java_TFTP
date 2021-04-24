package server;

import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;    //for alert
import javafx.scene.layout.*;
import javafx.stage.*;
import style.FontStyle;
import style.MonoSpacedFont;

import java.io.File;

public class ServerGUI extends Application implements EventHandler<ActionEvent> {
	private Scene scene;
	private Stage stage;

	//set the element
	private Label lbDirectory = new Label("Directory:");
	private TextField tfDirectory = new TextField();
	private Button btnChangeDir = new Button("Change Dir");

	private Button btnStart = new Button("Start");
	private TextArea taLog = new TextArea();
	private Button btnClear = new Button("Clear");
	//set the pane
	private FlowPane directoryPane = new FlowPane(8, 8);
	private VBox root = new VBox(8);

	private FontStyle fs;
	//serverBegin
	private ServerBegin sb;

	/*main*/
	public static void main(String[] args) {
		launch(args);
	}//main

	// Called automatically after launch sets up javaFX
	public void start(Stage _stage) throws Exception {
		stage = _stage;
		stage.setTitle("Server GUI");
		scene = new Scene(root, 550, 300);

		//set the  style
		fs = new MonoSpacedFont();    //get the monospace font style
		tfDirectory.setFont(fs.getFont());
		taLog.setFont(fs.getFont());

		//set element setting
		tfDirectory.setPrefColumnCount(25); //set width
		tfDirectory.setDisable(true);        //set disable
		directoryPane.setAlignment(Pos.CENTER);
		stage.setX(700);
		stage.setY(100);
		btnClear.setAlignment(Pos.CENTER_RIGHT);
		taLog.autosize();
		taLog.setPrefHeight(500);

		//set the pane add children
		directoryPane.getChildren().addAll(lbDirectory, tfDirectory, btnChangeDir, btnStart);
		root.getChildren().addAll(directoryPane, taLog, btnClear);

		//set button action
		btnStart.setOnAction(this);
		btnChangeDir.setOnAction(this);
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
			case "Start":
				btnStart.setText("Stop");
				tfDirectory.setEditable(false);
				sb = new ServerBegin(this);
				sb.start();
				btnChangeDir.setDisable(true);
				break;
			case "Stop":
				sb.stopServer();
				tfDirectory.setEditable(true);
				btnStart.setText("Start");
				btnChangeDir.setDisable(false);
				break;
			case "Clear":
				taLog.setText("");
				break;
			case "Change Dir":
				changeDir();
				break;
		}
	}//handle

	private void changeDir(){
		DirectoryChooser dc = new DirectoryChooser();
		File directory = dc.showDialog(null);
		tfDirectory.setText(directory.toString());
//		System.out.println(directory);
	}//change dir

	//set the getter
	public TextField getTfDirectory() {
		return tfDirectory;
	}//get tf directory

	public TextArea getTaLog() {
		return taLog;
	}//get tf ta log
}//class
