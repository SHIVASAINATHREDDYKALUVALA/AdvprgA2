package models;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * Represents single medical log entry
 * Each logs entry has:
 * resident who received , staff member who administred, medicine name and dosage.
 */
public class MedicationLogEntry implements Serializable{
    private String residentId;
    private String staffId;
    private String medicine;
    private String dose;
    private LocalDateTime time;
    private String prescriptionId;

    public MedicationLogEntry(String residentId, String staffId, String medicine, String dose,LocalDateTime time){
        this.residentId=residentId;
        this.staffId=staffId;
        this.medicine=medicine;
        this.dose=dose;
        this.time=time;
    }

    public MedicationLogEntry(String residentId, String staffId, String medicine, String dose,LocalDateTime time,String prescriptionId){
        this.residentId=residentId;
        this.staffId=staffId;
        this.medicine=medicine;
        this.dose=dose;
        this.time=time;
        this.prescriptionId=prescriptionId;
    }

    //getter methods
    public String residentId(){return residentId;}
    public String staffId(){return staffId;}
    public String medicine(){return medicine;}
    public String dose(){return dose;}
    public LocalDateTime time(){return time;}
    public String PrescriptionId(){return prescriptionId;}
}
