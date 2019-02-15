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
    
    private void createTimer(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new EatDrinkHealthy().displayTray(true);
            }
        }, loadTimer()*60000, loadTimer()*60000);
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
}
