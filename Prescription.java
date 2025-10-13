package models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/*
 * This class represents a mediacal prescription created by doctor.
 */
public class Prescription implements Serializable {
    //unique id for prescription
    private final String id=UUID.randomUUID().toString();
    private String residentId;//unique id of the resident
    private String doctorId;//unique doctor id
    private LocalDateTime careatedAt;//the time and date at which the prescription was created
    private List<PrescriptionItem> items=new ArrayList<>(); 

    public Prescription(String residentId, String doctorId, Collection<PrescriptionItem> items){
        this.residentId=residentId;
        this.doctorId=doctorId;
        this.careatedAt=LocalDateTime.now();
        if(items!=null)this.items.addAll(items);
    }

    //getter methods.
    public String getId(){return id;}
    public String getResidentId(){return residentId;}
    public String getDoctorId(){return doctorId;}
    public LocalDateTime getCreateAt(){return careatedAt;}
    public List<PrescriptionItem> getItems(){return Collections.unmodifiableList(items);}

    //this fuction replaces all the existing prescription items with a new collection.
    public void replaceItems(Collection<PrescriptionItem> newItems){
        items.clear();
        if(newItems!=null) items.addAll(newItems);
    }
    
}
