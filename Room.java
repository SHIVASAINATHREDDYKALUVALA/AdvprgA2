package models;

import java.io.Serializable;
import java.util.*;

/**
 * class for a single room , it contains one or more beds
 */
public class Room implements Serializable {
    private String code;// eg : "W1-R1"
    private List<Bed> beds; //list of the beds in the room

    public Room(String code, int bedcount){
        this.code=code;
        this.beds=new ArrayList<>();
        //generating bed objects
        for(int i=1;i<=bedcount;i++){
            beds.add(new Bed(code +"-B"+i));
        }
    }
    //function return the room code
    public String getCode(){return code;}
    //returns all the beds in the room
    public List<Bed> getBeds(){return beds;}
    
    //will find the first vacant bed.
    public Optional<Bed> findVacantBed(){
        return beds.stream().filter(Bed::isVacant).findFirst();
    }

    //this function will locate the bed. 
    public Optional<Bed> findBed(String bedCode){
        return beds.stream().filter(b->b.getCode().equals(bedCode)).findFirst();
    }
    

}
