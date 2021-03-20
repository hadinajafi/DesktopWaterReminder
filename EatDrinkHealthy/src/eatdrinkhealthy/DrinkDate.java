package eatdrinkhealthy;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Hadi Najafi
 */
public class DrinkDate {
    private Date date;
    private SimpleDateFormat format;

    public DrinkDate() {
        this.date = new Date();
    }
    
    public String getToday(){
        format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(date);
    }
}
