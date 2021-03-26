package client;
import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;	//for alert
import javafx.scene.layout.*;
import javafx.stage.*;

import style.*;		//import the custom style

import javax.swing.text.html.parser.Parser;

public class ClientGUI extends Application implements EventHandler<ActionEvent>{
	private Scene scene;
	private Stage stage;

	//set the elements
	private Label lbServer = new Label("Server:");
	private TextField tfServer = new TextField();
	private Button btnConnect = new Button("Connect");

	private Label lbDirectory = new Label("Directory:");
	private TextField tfDirectory = new TextField();
	private Button btnChangeDir = new Button("Change Dir");

	private Button btnUpload = new Button("Upload");
	private Button btnDownload = new Button("Download");

	private TextArea taLog = new TextArea();
	//set the pane
	private FlowPane serverPane = new FlowPane(8,8);
	private FlowPane directoryPane = new FlowPane(8,8);
	private FlowPane buttonPane = new FlowPane(8,8);
	private VBox root = new VBox(8);

	/*main*/
	public static void main(String[] args) {launch(args);}//main

	// Called automatically after launch sets up javaFX
	public void start(Stage _stage) throws Exception {
		stage = _stage;
		stage.setTitle("Client GUI");
		scene = new Scene(root, 550, 300);

		//set the  style
		FontStyle fs = new MonoSpacedFont();	//get the monospace font style
		tfServer.setFont(fs.getFont());
		tfDirectory.setFont(fs.getFont());
		taLog.setFont(fs.getFont());

		//set the element setting
		tfServer.setPrefColumnCount(18);	//set the width
		tfDirectory.setPrefColumnCount(40);
		tfDirectory.setDisable(true);		//set disable
		serverPane.setAlignment(Pos.CENTER);	//set the position
		directoryPane.setAlignment(Pos.CENTER_LEFT);
		buttonPane.setAlignment(Pos.CENTER_RIGHT);
		stage.setX(100);
		stage.setY(100);

		//pane add children
		serverPane.getChildren().addAll(lbServer,tfServer,btnConnect);
		directoryPane.getChildren().addAll(lbDirectory,tfDirectory,btnChangeDir);
		buttonPane.getChildren().addAll(btnUpload,btnDownload);
		//add all to root
		root.getChildren().addAll(serverPane,directoryPane,buttonPane,taLog);

		stage.setScene(scene);
		stage.show();
	}//start

	public void handle(ActionEvent evt) {
		// Get the button that was clicked
		Button btn = (Button)evt.getSource();

		// Switch on its name
		switch(btn.getText()) {}
	}//handle
}
