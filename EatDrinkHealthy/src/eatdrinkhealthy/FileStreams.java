package eatdrinkhealthy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static eatdrinkhealthy.common.Constants.FILE_NAME;
import static eatdrinkhealthy.common.Constants.ROOT;

/**
 * @author Hadi Najafi
 */
public class FileStreams {

    private ObjectMapper mapper;
    private Map<String, Integer> data;
    private File dataFile;

    public FileStreams() {
        data = new HashMap<>();
        mapper = new ObjectMapper();
        creatDirectories();
        dataFile = new File(ROOT + FILE_NAME);
    }

    private void creatDirectories() {
        File file = new File(ROOT);
        if (!file.exists()) {
            file.mkdirs();
        }
        dataFile = new File(ROOT + FILE_NAME);
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException ex) {
                System.err.println("Can't write to the disk\n" + ex.getMessage());
                Logger.getLogger(FileStreams.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void writeData() {
        try {
            //writer = new FileWriter(dataFile);
            mapper.writeValue(dataFile, data);
        } catch (IOException ex) {
            System.err.println("Can't write on the system disk, \n" + ex.getMessage());
            Logger.getLogger(FileStreams.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void readData() {
        try {
            data = mapper.readValue(dataFile, new TypeReference<Map<String, Integer>>() {
            });
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(FileStreams.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setData(Map<String, Integer> data) {
        this.data = data;
        writeData();
    }

    public Map<String, Integer> getData() {
        readData();
        return data;
    }
}
