/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eatdrinkhealthy;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TabPane;

import javafx.scene.layout.AnchorPane;

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
        stopTimer();
        startTimer();
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
        
        timeIntervalTextField.setValueFactory(new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int steps) {
                timeIntervalTextField.getEditor().setText(String.valueOf(timeIntervalTextField.getValue()-1));
            }

            @Override
            public void increment(int steps) {
                timeIntervalTextField.getEditor().setText(String.valueOf(timeIntervalTextField.getValue()+1));
            }
        });
        
        timeIntervalTextField.getEditor().setText(String.valueOf(loadTimer()));//load default or saved value to the spinner
        startTimer();
    }  
    
    private void startTimer(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new EatDrinkHealthy().displayTray();
            }
        }, loadTimer()*60000, calculateLeftHours()+calculateLeftMinute());
    }
    
    private void cancelTimer(){
        timer.cancel();
    }
    
    private void stopTimer(){
        timer.purge();
    }
    
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
     * Calculate the Hours left in the day.
     * for example if current time is 13:00, left hours is 24-13=11. so 11 is left hours till tomorrow.
     * @return 
     */
    private int calculateLeftHours(){
        Date date = new Date();
        SimpleDateFormat hourDateFormat = new SimpleDateFormat("HH");
        System.out.println(hourDateFormat.format(date));
        int hour = 24 - Integer.valueOf(hourDateFormat.format(date));
        hour = hour * 3600 * 1000;
        return hour;
    }
    
    /**
     * Calculate the left minutes till next hour.
     * for example if the time is 13:45, the method return 15. because of 60-45=15.
     * @return 
     */
    private int calculateLeftMinute(){
        Date date = new Date();
        SimpleDateFormat minDateFormat = new SimpleDateFormat("mm");
        int min = 60 - Integer.valueOf(minDateFormat.format(date));
        min = min * 60 * 1000;
        return min;
    }
}
