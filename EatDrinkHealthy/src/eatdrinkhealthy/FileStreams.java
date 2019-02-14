/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eatdrinkhealthy;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author hadin
 */
public class FileStreams {
    private FileWriter writer;
    private FileReader reader;
    private Properties data;
    private final String filePath=System.getProperty("user.home") + "/.EatDrinkHealthy/data";

    public FileStreams() {
        data = new Properties();
        
    }
    
    public void updateTimerPeriod(int value){
        data.put("timerPeriod", value);
        try {
            writer = new FileWriter(filePath);
            reader = new FileReader(filePath);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(FileStreams.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeData(){
        try {
            data.store(writer, filePath);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null, "Cannot save data, make sure the application have permission to write on "
                    + "the user home directory.", "Error While Saving!", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(FileStreams.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void readData(){
        data.clear();
        try {
            data.load(reader);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(FileStreams.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setData(Properties data){
        this.data = data;
        writeData();
    }
    
    public Properties getData(){
        readData();
        return data;
    }
}