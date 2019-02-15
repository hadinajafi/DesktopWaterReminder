/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eatdrinkhealthy;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author hadin
 */
public class EatDrinkHealthy extends Application {

    @Override
    public void start(Stage primaryStage) {
        if(SystemTray.isSupported()){
            System.out.println("tray supported");
        }
        else{
            System.err.println("Tray is not supported");
        }
        AnchorPane mainPane;
        try {
            mainPane = FXMLLoader.load(getClass().getResource("MainAnchorPaneLayout.fxml"));
            Scene scene = new Scene(mainPane);
            scene.getStylesheets().add(getClass().getResource("mainanchorpanelayout.css").toExternalForm());
            primaryStage.setTitle("Eat & Drink Healthy");
            primaryStage.setResizable(false);
            primaryStage.getIcons().add(new javafx.scene.image.Image("/icons/water4-16.png"));
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
    public void displayTray(){
        SystemTray tray = SystemTray.getSystemTray();
        Image imageIcon = null;
        try {
            imageIcon = ImageIO.read(getClass().getResource("/icons/water4-16.png"));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        TrayIcon icon = new TrayIcon(imageIcon);
        icon.setImageAutoSize(true);
        icon.setToolTip("Tray Icon demo");
        try {
            tray.add(icon);
        } catch (AWTException ex) {
            System.err.println(ex.getMessage());
        }
        icon.displayMessage("Drink Water Time!", "It's time to Drink Water please", TrayIcon.MessageType.INFO);
    }
}
