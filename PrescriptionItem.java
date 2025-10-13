package models;

import java.io.Serializable;
import java.time.*;

// Each prescriptionItem object specifies which medicine to administer, at what dosage and time,date .
public class PrescriptionItem implements Serializable  {
    //name of medicine ,it's dose, date and time.
    
    private String medicine;
    private String dose;
    private LocalTime time;

    public PrescriptionItem(String medicine, String dose, LocalTime time){
        this.medicine=medicine;
        this.dose=dose;
        this.time=time;       
    }

    //getter methods which will return medicine name, dose of the medicine and time.
    public String  getMedicine(){return medicine;}
    public String getDose(){return dose;}
    public LocalTime getTime(){return time;}
}
