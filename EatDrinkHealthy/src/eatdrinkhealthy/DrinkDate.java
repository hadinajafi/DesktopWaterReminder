/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eatdrinkhealthy;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author hadin
 */
public class DrinkDate {
    private Date date;
    private SimpleDateFormat format;

    public DrinkDate() {
        this.date = new Date();
    }
    
    public String getToday(){
        format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(date);
    }
}
