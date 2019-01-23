/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eatdrinkhealthy;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author hadin
 */
public class EatDrinkHealthy extends Application {

    @Override
    public void start(Stage primaryStage) {
        AnchorPane mainPane = new AnchorPane();

        Scene scene;
        try {
            scene = FXMLLoader.load(getClass().getResource("MainAnchorPaneLayout.fxml"));
            scene.getStylesheets().add(getClass().getResource("mainanchorpanelayout.css").toExternalForm());
            primaryStage.setTitle("Eat & Drink Healthy");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException ex) {
            Logger.getLogger(EatDrinkHealthy.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
