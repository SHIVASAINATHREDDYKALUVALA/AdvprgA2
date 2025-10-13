package models;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

/**
 * base class for all staffs
 * adds roster and helpers.
 */

public class Staff implements Serializable{
    private String id=UUID.randomUUID().toString();
    private String name;
    private String role;
    private String password;
    private Set<Shift> shifts=new HashSet<>();

    public Staff(String name, String role){
        this.name=name;
        this.role=role;
    }
    public Set<Shift> getShifts(){
        return Collections.unmodifiableSet(shifts);
    }
    public String getId(){return id;}
    public String getName(){return name;}
    public String getRole(){
        return role;}
    public void setName(String name){this.name =name;}
    public String getPassword(){return password;}
    public void setPassword(String password){this.password=password;}
    //assinging shift to staff members.
    public void assignShift(Shift s){shifts.add(s);}

    //check weather staff is rostered or not.
    public boolean isRosteredAt(LocalDateTime when){
        return shifts.stream().anyMatch(s->s.covers(when));
    }

    //returns total hours assigned on a given day
    public double hoursOn(DayOfWeek d){
        return shifts.stream().filter(s->s.getDay()==d).mapToDouble(Shift::hours).sum();
    }
}
