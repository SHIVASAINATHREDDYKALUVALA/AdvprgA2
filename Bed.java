package models;

import java.io.Serializable;
import java.util.Optional;

/**
 * class for single bed
 * bed can be vacant or occupied by the resident
 * code patter example:"W1-R2-B1"
 */
public class Bed implements Serializable{
    //Unique code to identify the bed and Resident class for the resident in the bed
    private String code;
    private Resident occupant;

    public Bed(String code){
        this.code=code;
    }

    //getter methods
    public String getCode(){return code;}
    public Resident getOccupant(){return occupant;}

    //setter methods
    public void assign(Resident r){this.occupant=r;}
    public void vacate(){this.occupant=null;}

    //fuction to check weather the bed is vacant or not
    public boolean isVacant(){return occupant==null;}
}
