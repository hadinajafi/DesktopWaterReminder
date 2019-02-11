/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eatdrinkhealthy;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tabPane.tabMinWidthProperty().bind(root.widthProperty().divide(tabPane.getTabs().size()).subtract(20.5));
        statPieChart.setTitle("How much you Drunk water today");
        ObservableList<Data> data = FXCollections.observableArrayList(new PieChart.Data("Water Drunken", 00.75), new PieChart.Data("Didn't Drink yet!", 00.25));
        statPieChart.setData(data);
    }    
    
}
