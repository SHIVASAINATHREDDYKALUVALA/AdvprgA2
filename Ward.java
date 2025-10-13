package models;

import java.util.*;
import java.io.Serializable;

/**
 * Class for a single ward, it will contain multiple rooms
 * constructor will accepts an array describing bed per room.
 */
public class Ward implements Serializable{
    private String name;
    private List<Room> rooms;

    public Ward(String name, int[] bedsPerRoom){
        this.name=name;
        this.rooms=new ArrayList<>();
        for(int i=0;i<bedsPerRoom.length;i++){
            rooms.add(new Room("W"+name.replaceAll("\\D","")+"-R"+(i+1),bedsPerRoom[i]));
        }
    }

    //getter functions which return name and list of the rooms in the ward
    public String getName(){return name;}
    public List<Room> getRooms(){return rooms;}

    //finds the bed in the ward using the bed code
    public Optional<Bed> findBed(String bedCode){
        return rooms.stream()
                .map(r->r.findBed(bedCode))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
    
}
