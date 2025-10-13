package models;

import java.io.Serializable;
import java.time.LocalDateTime;

/*
 * This class represents a single log entry for each action performed by the staff member.
 * it records who performed the action and when it occured.
 */
public class ActionLogEntry implements Serializable{
    private LocalDateTime when;//date and time of event
    private String staffId;// unique staff id of the staff who performed the action
    private ActionType type; //type of action
    private String details; //additional information
    

    public ActionLogEntry(LocalDateTime when, String staffId, ActionType type, String details){
        this.when=when;
        this.staffId=staffId;
        this.type=type;
        this.details=details;
    }

    //getter methods for date and time, staffid, type of action and additional details.
    public LocalDateTime getWhen(){return when;}
    public String getStaffId(){return staffId;}
    public ActionType getType(){return type;}
    public String getDetails(){return details;}

    @Override
    public String toString(){
        return String.format("[%s] Staff:%s | Action: %s |Details: %s",when,staffId,type,details);
    }

    
}
