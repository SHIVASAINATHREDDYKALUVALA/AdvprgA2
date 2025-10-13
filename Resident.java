package models;

import java.io.Serializable;
import java.util.Optional;

/**
 * class for patient or a resident occupyig a bed
 */
public class Resident implements Serializable {
    
    private String id;
    private String gender;
    private String name;
    public Resident(String id,String name, String gender){
        this.id=id;
        this.gender=gender;
        this.name=name;        
    }
    //returns the id of the resident
    public String getId(){return id;}
    //the full name of the resident will be returned
    public String getName(){return name;}
    //returns the gender of the resident
    public String getGender(){return gender;}

    
    
}
