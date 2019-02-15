/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eatdrinkhealthy;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * FXML Controller class
 *
 * @author hadin
 */
public class MainAnchorPaneLayoutController implements Initializable {
    
    @FXML
    private TabPane tabPane;
    @FXML
    private AnchorPane root;
    @FXML
    private Label timecounterLable;
    @FXML
    private Button skipButton;
    @FXML
    private PieChart statPieChart;
    @FXML
    private AnchorPane tabDailyAnchorPane;
    @FXML
    private Spinner<Integer> timeIntervalTextField;
    @FXML
    private Button timeIntervalSaveBtn;
    
    private Map<String, Integer> userData;
    private Timer timer;
    
    @FXML
    void saveApplyBtnAction(ActionEvent event) {
        userData.replace("timerPeriod", Integer.parseInt(timeIntervalTextField.getEditor().getText()));
        FileStreams stream = new FileStreams();
        stream.setData(userData);
        timer.cancel();
        timer.purge();
        createTimer();
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userData = new HashMap<>();
        tabPane.tabMinWidthProperty().bind(root.widthProperty().divide(tabPane.getTabs().size()).subtract(20.5));
        statPieChart.setTitle("How much you Drunk water today");
        ObservableList<Data> data = FXCollections.observableArrayList(new PieChart.Data("Water Drunken", 00.75), new PieChart.Data("Didn't Drink yet!", 00.25));
        statPieChart.setData(data);
        /*
        Setting the value factory for the Spinner, increament and decreament works well and the minimum range is 0.
        */
        timeIntervalTextField.setValueFactory(new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int steps) {
                if(!"0".equals(timeIntervalTextField.getEditor().getText()))
                    timeIntervalTextField.getEditor().setText(String.valueOf(Integer.parseInt(timeIntervalTextField.getEditor().getText())-1));
            }

            @Override
            public void increment(int steps) {
                timeIntervalTextField.getEditor().setText(String.valueOf(Integer.parseInt(timeIntervalTextField.getEditor().getText())+1));
            }
        });
        
        timeIntervalTextField.getEditor().setText(String.valueOf(loadTimer()));//load default or saved value to the spinner
        createTimer();
    }  
    
    /**
     * <b>Create Period Timer</b><br>
     * Start the timer for showing notifications periodically. loadTimer() method used to load the user data.<br>
     * Two integers from the file will multiply into 60,000 to convert the minute to milliseconds.
     */
    private void createTimer(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                displayNotification(); 
            }
        }, loadTimer()*60000, loadTimer()*60000);
    }
    
    /**
     * <b>Load the Timer period from the file</b><br>
     * FileStreams class object, loads the mapped data and return it. the loaded data is integer.
     * @return Integer: timer period
     */
    private int loadTimer(){
        FileStreams streams = new FileStreams();
        userData = streams.getData();
        if(!userData.containsKey("timerPeriod") || userData.isEmpty()){
            userData.put("timerPeriod", 60);
            streams.setData(userData);
            return 60;
        }
        else{
            return (int)userData.get("timerPeriod");
        }
    }
    
    /**
     * <b>Display Notification</b><br>
     * pop up notification to show the message to the user
     * used <i>Platform.runLater()<i> to perform this action on a FX thread. otherwise the timer thread error occurs.
     */
    private void displayNotification(){
        Platform.runLater(() -> {   //creating the FX thread
            Notifications.create().title("Eat & Drink Healthy").text("Time to drink water Please!").graphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/water2.png")))).hideAfter(Duration.seconds(10)).show();
        });
    }
    
    
}
