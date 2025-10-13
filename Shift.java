package models;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;


/**
 * Represents a scheduled work period for nurses and doctors
 * nurses will have fixed morning and afternoon windows and 
 * Doctors will have one hour windows at any time
 */

public class Shift implements Serializable{
 private DayOfWeek day;
 private ShiftType type;
 private LocalTime start;
 private LocalTime end;
 private Role role;

 //Factory for the nurse shift 
 public static Shift nurse(DayOfWeek day, ShiftType type){
    if(type==ShiftType.DOCTOR_HOUR) throw new IllegalArgumentException("Use doctor factory.");
    return new Shift(day,type, type.start(), type.end(),Role.NURSE);
 }

 //Factory for the doctor one hour slot at the give time.
 public static Shift doctorOneHour(DayOfWeek day, LocalTime startingAt){
    return new Shift(day, ShiftType.DOCTOR_HOUR, startingAt, startingAt.plusHours(1), Role.DOCTOR);
 }

 private Shift(DayOfWeek day, ShiftType type, LocalTime start, LocalTime end, Role role){
    this.day=day;
    this.type=type;
    this.start=start;
    this.end=end;
    this.role=role;
 }

 public DayOfWeek getDay(){return day;}
 
 public String getRole(){
   if (Role.NURSE==role) return "NURSE";
   if (Role.DOCTOR==role) return "DOCTOR";
   if (Role.MANAGER==role) return "MANAGER";
   return null;
 }

 //Inclusive start and end.
 public double hours(){
    return Duration.between(start, end).toHours();
 }

 //Does this shift cover the given timestamp
 public boolean covers(LocalDateTime at){
    return at.getDayOfWeek()==day &&
            !at.toLocalTime().isBefore(start)&&
            at.toLocalTime().isBefore(end);
 }

 public LocalTime getStart(){
   return start;
 }
 public LocalTime getEnd(){return end;}

 public ShiftType getType(){return type;}
}

//predefined roles, which will be used for authorization or business rules.
enum Role{
MANAGER,
NURSE,
DOCTOR
}