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
import sun.security.krb5.internal.rcache.AuthList;

/**
 * FXML Controller class
 *
 * @author hadin
 */
public class MainAnchorPaneLayoutController implements Initializable {

    @FXML
    private TabPane tabPane;    //tabbed pane to use a multitab user interface
    @FXML
    private AnchorPane root;    //root and parent of all components
    @FXML
    private Label timecounterLable; //show counting down the current period till next notification. Timer "counter" used for this
    @FXML
    private Button skipButton;  //skip the current period of time and jump to next period
    @FXML
    private PieChart statPieChart;  //showing the stats about how much the user drunk water today
    @FXML
    private AnchorPane tabDailyAnchorPane;  //anchor pane inside the daily tab
    @FXML
    private Spinner<Integer> timeIntervalTextField; //Spinner to choose the notifications period
    @FXML
    private Button timeIntervalSaveBtn; //save the chosen notification period from the spinner and save it to file

    private Map<String, Integer> userData;  //user data such as notification period
    private Timer timer;    //timer to count down to display notification
    private Timer counter; //timer for showing counting down on the Timer label
    private boolean showMessage = false;

    @FXML
    void saveApplyBtnAction(ActionEvent event) {
        userData.replace("timerPeriod", Integer.parseInt(timeIntervalTextField.getEditor().getText()));
        FileStreams stream = new FileStreams();
        stream.setData(userData);
        startNewTimer();
    }
    
    @FXML
    void skipBtnAction(ActionEvent event) {
        startNewTimer(); //skip button will just start new timers, nothing else
    }
    /**
     * Cancel and purge all previous timers and create a new timer again.
     */
    private void startNewTimer(){
        counter.cancel();
        counter.purge();
        timer.cancel();
        timer.purge();
        showMessage = false;
        createTimer();
    }

    /**
     * Initializes the controller class.
     *
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
                if (!"0".equals(timeIntervalTextField.getEditor().getText())) {
                    timeIntervalTextField.getEditor().setText(String.valueOf(Integer.parseInt(timeIntervalTextField.getEditor().getText()) - 1));
                }
            }

            @Override
            public void increment(int steps) {
                timeIntervalTextField.getEditor().setText(String.valueOf(Integer.parseInt(timeIntervalTextField.getEditor().getText()) + 1));
            }
        });

        timeIntervalTextField.getEditor().setText(String.valueOf(loadTimer()));//load default or saved value to the spinner
        createTimer();
    }

    /**
     * <b>Create Period Timer</b><br>
     * Start the timer for showing notifications periodically. loadTimer()
     * method used to load the user data.<br>
     * Two integers from the file will multiply into 60,000 to convert the
     * minute to milliseconds.
     */
    private void createTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (showMessage) {
                    displayNotification();
                    counter.cancel();
                    counter.purge();
                }
                //start of internal timer to count down
                createCounterTimer();
                showMessage = true;
                //end of run method
            }
        }, 0, loadTimer() * 60000);
    }

    /**
     * Create internal counter to count down from period and show the countdown on the label.
     */
    private void createCounterTimer() {
        counter = new Timer();
        counter.scheduleAtFixedRate(new TimerTask() {
            int minutes = loadTimer();
            int seconds = 0;
            int hour = minutes / 60;

            @Override
            public void run() {
                if (hour > 0 && minutes == 0) {
                    hour--;
                    minutes += 59;
                } else if (minutes > 0 && seconds == 0) {
                    minutes--;
                    seconds += 59;
                } else if (seconds > 0) {
                    seconds--;
                } else {
                    counter.cancel();
                }
                Platform.runLater(() -> {
                    String hh, mm, ss;
                    if(hour < 10){
                        hh = "0" + hour;
                    }else{
                        hh = String.valueOf(hour);
                    }
                    if(minutes < 10){
                        mm = "0" + minutes;
                    }else{
                        mm = String.valueOf(minutes);
                    }
                    if(seconds < 10){
                        ss = "0" + seconds;
                    }else{
                        ss = String.valueOf(seconds);
                    }
                    timecounterLable.setText(hh + ":" + mm + ":" + ss);
                });
            }
        }, 0, 1000);
    }

    /**
     * <b>Load the Timer period from the file</b><br>
     * FileStreams class object, loads the mapped data and return it. the loaded
     * data is integer.
     *
     * @return Integer: timer period
     */
    private int loadTimer() {
        FileStreams streams = new FileStreams();
        userData = streams.getData();
        if (!userData.containsKey("timerPeriod") || userData.isEmpty()) {
            userData.put("timerPeriod", 60);
            streams.setData(userData);
            return 60;
        } else {
            return (int) userData.get("timerPeriod");
        }
    }

    /**
     * <b>Display Notification</b><br>
     * pop up notification to show the message to the user.<br>
     * <i>Platform.runLater()</i> used to perform this action on a FX thread.
     * otherwise the timer thread error occurs:<br>
     * <u>The Notification in package controlsfx can't run on a timer thread.
     * needs FX thread.</u>
     */
    private void displayNotification() {
        Platform.runLater(() -> {   //creating the FX thread
            Notifications.create().title("Eat & Drink Healthy").text("It's Time to drink Water!").graphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/water2.png")))).hideAfter(Duration.seconds(10)).show();
        });
    }

}
