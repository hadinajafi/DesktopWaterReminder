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
import java.util.function.UnaryOperator;
import javafx.application.Platform;
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
import javafx.scene.control.TextFormatter;
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
    @FXML
    private Spinner<Integer> glassVolumeSpinner;
    @FXML
    private Button glassVolBtn;

    private Map<String, Integer> userData;  //user data such as notification period
    private Timer timer;    //timer to count down to display notification
    private Timer counter; //timer for showing counting down on the Timer label
    private boolean showMessage = false;
    private DrinkDate drinkDate;

    /**
     * 
     * @param event Action event on this button
     * Save user data such as time interval value on the file.
     */
    @FXML
    void saveApplyBtnAction(ActionEvent event) {
        if (timeIntervalTextField.getEditor().getText() == null || timeIntervalTextField.getEditor().getText().equals("") || timeIntervalTextField.getEditor().getText().equals("0")) {
            timeIntervalTextField.getEditor().setText("60");
        }
        userData.replace("timerPeriod", Integer.parseInt(timeIntervalTextField.getEditor().getText()));
        FileStreams stream = new FileStreams();
        stream.setData(userData);
        startNewTimer();
    }

    /**
     * 
     * @param event Action that will execute on this button
     * Reset the timer and adds +1 step to the water progress.
     */
    @FXML
    void skipBtnAction(ActionEvent event) {
        startNewTimer(); //the skip button will just start new timers, nothing else
        increaseWaterDrinkTimes();
        loadingChartData();
    }

    /**
     * 
     * @param event Action that will execute on this button
     * Save glass volume on the file and verify its value, otherwise default value will save.
     * Process ends on writing the value on the file.
     */
    @FXML
    void glassVolSaveBtn(ActionEvent event) {
        if (glassVolumeSpinner.getEditor().getText() == null || glassVolumeSpinner.getEditor().getText().equals("") || glassVolumeSpinner.getEditor().getText().equals("0")) {
            glassVolumeSpinner.getEditor().setText("250");
        }
        userData.replace("glassVol", Integer.parseInt(glassVolumeSpinner.getEditor().getText()));
        new FileStreams().setData(userData);
        loadingChartData();
    }

    /**
     * Cancel and purge all previous timers and create a new timer again.
     * Without showing notification.
     */
    private void startNewTimer() {
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
        drinkDate = new DrinkDate();
        tabPane.tabMinWidthProperty().bind(root.widthProperty().divide(tabPane.getTabs().size()).subtract(25));
        statPieChart.setTitle("How much you Drunk water today");
        //initializing the spinners value factory
        spinnersValueFactory();
        timeIntervalTextField.getEditor().setText(String.valueOf(loadTimer()));//load default or saved value to the spinner
        createTimer(); //start the count down at start up
        loadingChartData(); //load data to the chart
    }

    /**
     * Loading chart Data by 3 values, glass volume, drunk water times in the
     * day and 2 liters water per day.
     */
    private void loadingChartData() {
        userData = new FileStreams().getData(); //getting data from the saved file
        int drinkTimes = 0; //default value in case of data missing
        if (userData.containsKey(drinkDate.getToday())) //checking for handle data missing: null pointer exception happened.
        {
            drinkTimes = userData.get(drinkDate.getToday());
        } else {                                           //add the default (0) value to the userData map and save it.
            userData.put(drinkDate.getToday(), drinkTimes);
            new FileStreams().setData(userData);
        }
        double drunkPerglass = 2 / (userData.get("glassVol") / 1000.0);   //calculate the drunk glass volume by: 2 litrs is default value for every person needs to drink water each day. glass value is CC and divided by 1000 to convert to litrs.
        double drunkWater = drinkTimes / drunkPerglass; //drunk water is drunk times divided by drinks per glass.
        if (drunkWater <= 1.0) {     //if data is not more than 100% percentage. because maybe someone drunk water more than 2 litrs.
            ObservableList<Data> chartData = FXCollections.observableArrayList(new PieChart.Data("Water Drunken", drunkWater), new PieChart.Data("Didn't Drink yet!", 1.00 - drunkWater));
            statPieChart.setData(chartData);
        } else {
            ObservableList<Data> chartData = FXCollections.observableArrayList(new PieChart.Data("Water Drunken", 1.0));
            statPieChart.setData(chartData);
        }
    }

    /**
     * Spinner value factory, defines each spinner value factory in the FXML
     * file and add functionality to the increment and decrement.
     */
    private void spinnersValueFactory() {

        /*
        Adding text formatter with regular expression to accept only numbers for the spinners.
         */
        //filter and regular expression:
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };

        //set the filter to the spinners:
        timeIntervalTextField.getEditor().setTextFormatter(new TextFormatter<>(filter));
        glassVolumeSpinner.getEditor().setTextFormatter(new TextFormatter<>(filter));

        /*
        Setting the value factory for the Spinner, increment and decrement works well and the minimum range is 0.
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

        glassVolumeSpinner.setValueFactory(new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int steps) {
                glassVolumeSpinner.getEditor().setText(String.valueOf(Integer.parseInt(glassVolumeSpinner.getEditor().getText()) - 1));
            }

            @Override
            public void increment(int steps) {
                glassVolumeSpinner.getEditor().setText(String.valueOf(Integer.parseInt(glassVolumeSpinner.getEditor().getText()) + 1));
            }
        });
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

    
    /*
    Example for how to understand the variables:
    
    seconds is 36000
    loadedMinutes = minute = 60
    secondFormatted is 60->59
    minute is 60->59
    hour is 0
    formatted minute is 60->59
    
    >>> second--, second--
    
    seconds is 35998
    second formatted is 58
    hour is 0
    minute is 59
    minute formatted is 59
    
    */
    
    /**
     * Create internal counter to count down from period and show the countdown
     * on the label.
     */
    private void createCounterTimer() {
        final int loadedMinutes = loadTimer();  //loading user data from the file.
        counter = new Timer();
        
        counter.scheduleAtFixedRate(new TimerTask() {
            int seconds = loadedMinutes * 60;   //for calculating whole seconds in the saved file, used for countdown. e.g.: 60 minutes is 3600 seconds, but I don't wanna countdown from 3600.
            int hour = loadedMinutes/60;    //calculated hour value from minutes. used for timer lable too.
            int secondFormatted = 0;    //formatted second, it is from 0 to 59
            int minute = seconds/60;    //minute is calculated minute from seconds. like loadedMinutes. it is from 0 to infinite.
            int minuteFormatted = loadedMinutes - hour * 60;    //formatted minute used in timer label: from 0 to 59
            String secString, minString, hourString;    //formatted strings to show in timer label

            @Override
            public void run() {
                Platform.runLater(() -> {   //running the FX thread
                    if(secondFormatted < 10)    //setting "ss" format, like 00, 01, 02, ..., 09
                        secString = "0" + secondFormatted;
                    else
                        secString = String.valueOf(secondFormatted);
                    if(minuteFormatted < 10)    //setting "mm" format, like 00, 01, 02, ..., 09
                        minString = "0" + minuteFormatted;
                    else
                        minString = String.valueOf(minuteFormatted);
                    if(hour < 10)   //setting "hh" format, like 00, 01, 02, ..., 09
                        hourString = "0" + hour;
                    else
                        hourString = String.valueOf(hour);
                    timecounterLable.setText(hourString + ":" + minString + ":" + secString);
                    
                    seconds--;  //using the whole seconds to countdown.
                    hour = minute / 60;
                    minute = seconds/60;
                    secondFormatted = seconds - minute*60;
                    minuteFormatted = minute - hour*60;
                    if(minuteFormatted < 0){
                        if(hour >= 0)
                            hour--;
                        minuteFormatted = minute - hour*60;
                    }
                    if(seconds < 0)
                        timer.cancel();
                });
            }
        }, 0, 1000);    //timer will start without delay and will count down every second (1000 milisec).
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
        if (!userData.containsKey("timerPeriod") || userData.isEmpty()) {   //if the user have no saved data, we use 60 minutes as default.
            userData.put("timerPeriod", 60);    //putting default 60 minutes in the map variable.
            //logic is if the user haven't any timer period data, so there is no drink times data too.
            userData.put(drinkDate.getToday(), 0);  //putting default 0 times drinks for today.
            //adding glass capacity. user can change it. data is by CC.
            userData.put("glassVol", 250);
            glassVolumeSpinner.getEditor().setText(String.valueOf(userData.get("glassVol")));
            streams.setData(userData);  //updating and save data.
            return 60;
        } else {
            glassVolumeSpinner.getEditor().setText(String.valueOf(userData.get("glassVol")));
            return (int) userData.get("timerPeriod");   //if the saved file and data exists, the saved data will return.
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
            increaseWaterDrinkTimes();
            //updating the chart
            loadingChartData();
        });
    }

    /**
     * increase the water drunken times by one, when notification showed up or
     * skip button pressed. data will save immediately in the user file.
     */
    private void increaseWaterDrinkTimes() {
        int drinkTimes = userData.get(drinkDate.getToday()) + 1;
        if (userData.containsKey(drinkDate.getToday())) //if any notifications showed to the user, so there is data
        {
            userData.replace(drinkDate.getToday(), drinkTimes);
        } else {   //unless if user haven't any data about today drink times.
            userData.put(drinkDate.getToday(), 1);  //putting 1 because it is the first drink water for today.
        }
        new FileStreams().setData(userData);    //saving the data.
    }

}
