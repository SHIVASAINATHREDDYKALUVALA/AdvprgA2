import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.*;

import javax.print.Doc;

import Exceptions.BedNotFound;
import Exceptions.BedOccupied;
import Exceptions.RosterException;
import Exceptions.UnAuthorizedAction;
import models.*;
import java.util.*;
import java.util.stream.Collectors;

public class CareHome implements Serializable{
    private List<Nurse> nurse=new ArrayList<>();
    private List<Doctor> doctor=new ArrayList<>();

    private List<Ward> wards=new ArrayList<>();
    private List<MedicationLogEntry> medLog =new ArrayList<>();

    public CareHome(){
        wards.add(new Ward("1",new int[]{1,2,2,3,4,4}));
        wards.add(new Ward("2",new int[]{1,2,2,3,4,4}));
    }

    public Nurse addNurse(String name){
         Nurse n=new Nurse(name);
         nurse.add(n);
         return n;
    }

    public Doctor addDoctor(String name){
        Doctor d=new Doctor(name);
        doctor.add(d);
        return d;
    }

    public List<Nurse> getNurses(){return nurse;}
    public List<Doctor> getDoctors(){return doctor;}

    public void assignNurseShift(Nurse n, DayOfWeek day, ShiftType type){
        n.assignShift(Shift.nurse(day, type));
    }
    
    public void assignDoctorHour(Doctor d, DayOfWeek day, LocalTime start){
        d.assignShift(Shift.doctorOneHour(day, start));
    }


    public void allocateResidentToBed(Resident r, String bedCode) {
        try{
        Bed bed=findBedOrThrow(bedCode);
        if(!bed.isVacant()) throw new BedOccupied("bed" +bedCode +"is alread occupied.");
        bed.assign(r);
        }catch(BedNotFound b){
            System.out.println(b.getMessage());
        }catch(BedOccupied b){
            System.out.println(b.getMessage());
        }
    }

    public void moveResident(String fromBedCode, String toBedCode){
        try{
        Bed from=findBedOrThrow(fromBedCode);
        Bed to=findBedOrThrow(toBedCode);
        if(from.isVacant()) throw new BedNotFound("No resident in "+ fromBedCode);
        if(!to.isVacant()) throw new BedOccupied("Destination "+toBedCode+" occupied");
        Resident r=from.getOccupant();
        from.vacate();
        to.assign(r);
        }catch(BedNotFound b){
            System.out.println(b.getMessage());
        }catch(BedOccupied b){
            System.out.println(b.getMessage());
        }
    }


    private Bed findBedOrThrow(String bedCode) throws BedNotFound{
        return wards.stream()
                .map(w->w.findBed(bedCode))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(()->new BedNotFound("bed "+bedCode+" not found"));
    }



    public void recordMedication(Resident re, Staff by, String medicine, String dose, LocalDateTime at){
        try{
        if(by.getRole()!="NURSE"){
            throw new UnAuthorizedAction("Only nurses can administer medication");
        }
        if(!by.isRosteredAt(at)){
            throw new RosterException("Staff not rostered at"+ at);
        }
        medLog.add(new MedicationLogEntry(re.getId(), by.getId(), medicine, dose, at));
        }catch(UnAuthorizedAction a){
            System.out.println(a.getMessage());
        }catch(RosterException r){
            System.out.println(r.getMessage());
        }
    }

    public List<MedicationLogEntry> getMedicationLog(){
        return Collections.unmodifiableList(medLog);
    }

    public void checkCompliance(){
        try{
        for(DayOfWeek day: DayOfWeek.values()){
            boolean morningCovered=nurse.stream().anyMatch(n->
            n.getShifts().contains(Shift.nurse(day, ShiftType.MORNING)));
            boolean afternoonCovered=nurse.stream().anyMatch(n->
            n.getShifts().contains(Shift.nurse(day, ShiftType.AFTERNOON)));
            if(!morningCovered|| !afternoonCovered){
                throw new RosterException("Nurse coverage missing on "+day+
                "(morning="+morningCovered+", afternoon="+afternoonCovered+")");
            }
        }
        }catch(RosterException r){
            System.out.println(r.getMessage());
        }

        try{
        for(Nurse n: nurse){
            for(DayOfWeek d: DayOfWeek.values()){
                if(n.hoursOn(d)>8.0){
                    throw new RosterException("Nurse "+n.getName());
                }
            }
        }
        }catch(RosterException r){
            System.out.println(r.getMessage());
        }

        try{
        for(DayOfWeek day:DayOfWeek.values()){
            boolean any=doctor.stream().anyMatch(doc->
            doc.getShifts().stream().anyMatch(s->s.getDay()==day && s.getRole()=="DOCTOR"));
            if(!any) throw new RosterException("No doctor hour assigned for" +day);
        }
        }catch(RosterException r){
            System.out.println(r.getMessage());
        }
    }

   

    public void saveToFile(String path)throws IOException{
        try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(path))){
            out.writeObject(this);
        }
    }

    public static CareHome loadFromFile(String p) throws IOException, ClassNotFoundException{
        try(ObjectInputStream in=new ObjectInputStream(new FileInputStream(p))){
            return (CareHome) in.readObject();
        }
    }
    
    public String printBeds(){
        StringBuilder sb=new StringBuilder();
        for(Ward w: wards){
            sb.append("Ward ").append(w.getName()).append("\n");
            w.getRooms().forEach(r->r.getBeds().forEach(b->sb.append(" ").append("\n")));
        }
        return sb.toString();
    }

    public String printRoster(){
        return "Nurses:\n"+nurse.stream()
                .map(n->n+"->"+n.getShifts())
                .collect(Collectors.joining("\n"))+
                "\nDOCTORS:\n"+doctor.stream()
                .map(d->d+"->"+d.getShifts())
                .collect(Collectors.joining("\n"));
    }
}


enum Role{
MANAGER,
NURSE,
DOCTOR
}