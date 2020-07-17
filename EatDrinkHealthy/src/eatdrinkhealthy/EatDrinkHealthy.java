/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eatdrinkhealthy;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;

/**
 *
 * @author hadin
 */
public class EatDrinkHealthy extends Application {

    private Stage primaryStage;
    private boolean firstTray = true;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        displayTray();
        AnchorPane mainPane;
        try {
            mainPane = FXMLLoader.load(getClass().getResource("MainAnchorPaneLayout.fxml"));
            Scene scene = new Scene(mainPane);
            scene.getStylesheets().add(getClass().getResource("mainanchorpanelayout.css").toExternalForm());
            this.primaryStage.setTitle("Eat & Drink Healthy");
            this.primaryStage.setResizable(false);
            this.primaryStage.getIcons().add(new javafx.scene.image.Image("/icons/glass_128.png"));
            this.primaryStage.setScene(scene);
            this.primaryStage.show();
            Platform.setImplicitExit(false);
            primaryStage.setOnCloseRequest((WindowEvent event) -> {
                Platform.runLater(() -> {
                    if (SystemTray.isSupported()) {
                        primaryStage.hide();
                        displayTray();
                    } else {
                        System.exit(0);
                    }
                });
            });
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

    public void displayTray() {
        PopupMenu trayMenu = new PopupMenu("Menu");
        MenuItem exitBtn = new MenuItem("Exit");
        trayMenu.add(exitBtn);
        exitBtn.addActionListener((java.awt.event.ActionEvent e) -> {
            System.exit(0);
        });
        //check if the system supports tray icon
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();

            Image imageIcon = null;
            try {
                imageIcon = ImageIO.read(getClass().getResource("/icons/glass_48.png"));
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                System.out.println("Can't find the image for tray!");
            }
            TrayIcon icon = new TrayIcon(imageIcon, "Drink Water", trayMenu);
            icon.setToolTip("Eat & Drink Healthy");

            MouseAdapter trayIconMouseAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            trayMenu.show(e.getComponent(), e.getX(), e.getY());
                        } else if (e.getButton() == 1) {
                            if (!primaryStage.isShowing()) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        primaryStage.show();
                                    }
                                });
                            }
                        }
                    } catch (IllegalArgumentException | NullPointerException ex) {
                        System.err.println(ex.getMessage());
                    }
                }

            };

            icon.addMouseListener(trayIconMouseAdapter);
            icon.setImageAutoSize(true);
            try {
                if (firstTray) {
                    tray.add(icon);
                    firstTray = false;
                }
            } catch (AWTException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
