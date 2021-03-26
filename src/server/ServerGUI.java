package server;

import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;	//for alert
import javafx.scene.layout.*;
import javafx.stage.*;

public class ServerGUI extends Application implements EventHandler<ActionEvent>{
    private Scene scene;
    private Stage stage;

    //set the element
    private Label lbDirectory = new Label("Directory:");
    private TextField tfDirectory = new TextField();
    private Button btnChangeDir = new Button("Change Dir");

    private Button btnStart = new Button("Start");
    private TextArea taLog = new TextArea();

    //set the pane
    private FlowPane directoryPane = new FlowPane(8,8);
    private VBox root = new VBox(8);

    /*main*/
    public static void main(String[] args) {launch(args);}//main

    // Called automatically after launch sets up javaFX
    public void start(Stage _stage) throws Exception {
        stage = _stage;
        stage.setTitle("Server GUI");
        scene = new Scene(root, 550, 300);

        //set element setting
        tfDirectory.setPrefColumnCount(25); //set width
        directoryPane.setAlignment(Pos.CENTER);
        stage.setX(700);
        stage.setY(100);

        //set the pane add children
        directoryPane.getChildren().addAll(lbDirectory,tfDirectory,btnChangeDir,btnStart);
        root.getChildren().addAll(directoryPane,taLog);

        stage.setScene(scene);
        stage.show();
    }//start

    public void handle(ActionEvent evt) {
        // Get the button that was clicked
        Button btn = (Button)evt.getSource();

        // Switch on its name
        switch(btn.getText()) {}
    }//handle

    //set the getter
    public TextField getTfDirectory() {
        return tfDirectory;
    }//get tf directory

    public TextArea getTaLog() {
        return taLog;
    }//get tf ta log
}//class
