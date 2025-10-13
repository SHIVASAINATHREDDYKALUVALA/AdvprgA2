import java.io.*;
import java.time.*;



import Exceptions.*;
import models.*;
import java.util.*;
import java.util.stream.Collectors;

public class CareHome implements Serializable{
    //nurse and doctor registries.
    private List<Manager> managers=new ArrayList<>();
    private List<Nurse> nurse=new ArrayList<>();
    private List<Doctor> doctor=new ArrayList<>();

    //two wards with 6 rooms each and each room with 1..4 beds 
    private List<Ward> wards=new ArrayList<>();

    //Medication administration (log)
    private List<MedicationLogEntry> medLog =new ArrayList<>();
    private List<ActionLogEntry> audit=new ArrayList<>();

    
    private Map<String, Prescription> prescriptions=new HashMap<>();
    private Map<String, List<String>> rxByResident= new HashMap<>();

    //building wards with rooms 
    public CareHome(){

        wards.add(new Ward("1",new int[]{1,2,2,3,4,4}));
        wards.add(new Ward("2",new int[]{1,2,2,3,4,4}));
    }

    // Staff management
    //adding nurse to the list
    public Nurse addNurse(String name){
         Nurse n=new Nurse(name);
         nurse.add(n);
         return n;
    }

    //adding doctor to the list
    public Doctor addDoctor(String name){
        Doctor d=new Doctor(name);
        doctor.add(d);
        return d;
    }

    //adding new manager to list
    public Manager addManager(String name, String username, String password){
        Manager m=new Manager(name);
        initCredentials(m,username,password);
        managers.add(m);
        return m;
    }
    //getter methods which returns list of nurses , doctors,managers
    public List<Nurse> getNurses(){return nurse;}
    public List<Doctor> getDoctors(){return doctor;}
    public List<Manager> getManagers(){return Collections.unmodifiableList(managers);}

    //funtion to assign a shift to nurse.
    public void assignNurseShift(Nurse n, DayOfWeek day, ShiftType type){
        n.assignShift(Shift.nurse(day, type));
    }
    
    //assign 1-hour shift to a doctor at the given time in a day
    public void assignDoctorHour(Doctor d, DayOfWeek day, LocalTime start){
        d.assignShift(Shift.doctorOneHour(day, start));
    }
    //initialize credentials for a staff members.
    public void initCredentials(Staff s, String username, String password){
        s.setName(username);
        s.setPassword(password);
    }

    //method is used to update staff members credentials
    public void updateStaffCredentials(Manager byManager, Staff target, String newUsername, String newPassword){
        try {
            requireRole(byManager, "MANAGER", LocalDateTime.now());
        } catch (UnAuthorizedAction | NotRostered e) {
            e.printStackTrace();
        }
        if(newUsername!=null && !newUsername.isBlank()) target.setName(newUsername);
        if(newPassword !=null && !newPassword.isBlank()) target.setPassword(newPassword);
        audit.add(new ActionLogEntry(LocalDateTime.now(), byManager.getId(), ActionType.UPDATE_STAFF_DETAILS, "Updated creds for staff"+target.getId()));
    }

    //Bed and resident management functions
    //Allocate resident to a specific bed using bed code 
    //throws exception if bed occupied or not found.
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

    //Move resident to different bed or between two beds.
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

    //Nurse initiated resident move.
    public void nurseMoveResident(Nurse nurse, String fromBedCode, String toBedCode, LocalDateTime at) throws BedNotFound, BedOccupied, UnAuthorizedAction, NotRostered{
        
            requireRole(nurse, "NURSE",at);
            Bed from = findBedOrThrow(fromBedCode);
            Bed to = findBedOrThrow(toBedCode);
            
        if(from.isVacant()) throw new BedNotFound("No resident in "+ fromBedCode);
        if(!to.isVacant()) throw new BedOccupied("Destination "+toBedCode+" occupied.");
        Resident r=from.getOccupant();
        from.vacate();
        to.assign(r);
        audit.add(new ActionLogEntry(at, nurse.getId(), ActionType.MOVE_RESIDENT, "Resident "+r.getId()+": "+fromBedCode+"->"+toBedCode));
        
        
        
    }

    //find bed using using bedcode.
    public Bed findBedOrThrow(String bedCode) throws BedNotFound{
        return wards.stream()
                .map(w->w.findBed(bedCode))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(()->new BedNotFound("bed "+bedCode+" not found"));
    }


    //Prescriptions
    public Prescription doctorAddPrescription(Doctor doctor,String bedCode, Collection<PrescriptionItem> items, LocalDateTime at) throws BedNotFound, UnAuthorizedAction, NotRostered{
        
        requireRole(doctor, "DOCTOR",at);        
        
        Bed bed=findBedOrThrow(bedCode);
        Resident res=bed.getOccupant();
        Prescription rx=new Prescription(res.getId(), doctor.getId(), items);
        prescriptions.put(rx.getId(),rx);
        rxByResident.computeIfAbsent(res.getId(), k->new ArrayList<>()).add(rx.getId());
        audit.add(new ActionLogEntry(at, doctor.getId(), ActionType.ADD_PRESCRIPTION, "Rx "+rx.getId()+" for resident "+ res.getId()+" ("+ items.size()+" items)"));
        
        return rx;
    }

    //this methods is used for updating an existing prescription by doctor
    public void doctorUpdatePrescription(Doctor doctor, String prescriptionId, Collection<PrescriptionItem> newItems, LocalDateTime at) throws UnAuthorizedAction{
        try {
            requireRole(doctor, "DOCTOR",at);
        } catch (UnAuthorizedAction | NotRostered e) {
            System.out.println(e.getMessage());
        }
        Prescription rx=getPrescriptionOrThrow(prescriptionId);
        if(!rx.getDoctorId().equals(doctor.getId())){
            throw new UnAuthorizedAction("Only the author doctor can update this prescription.");
        }
        rx.replaceItems(newItems);
        audit.add(new ActionLogEntry(at, doctor.getId(), ActionType.UPDATE_PRESCRIPTION,  "Rx "+prescriptionId+" update with "+(newItems==null?0:newItems.size())+ " items"));
    }

    //this will return all the prescription for a resident id
    public List<Prescription> getPrescriptionsForResident(String residentId){
        var ids=rxByResident.getOrDefault(residentId,List.of());
        List<Prescription> out=new ArrayList<>();
        for(String id:ids) out.add(prescriptions.get(id));
        return Collections.unmodifiableList(out);
    }

    //lookup prescription by id
    private Prescription getPrescriptionOrThrow(String rxid){
        Prescription rx=prescriptions.get(rxid);
        if(rx==null) throw new IllegalArgumentException("Prescription not found"+ rxid);
        return rx;
    }


    public void nurseAdministerDose(Nurse nurse, String prescriptionId, String medicine,String dose, LocalDateTime at) throws IllegalAccessException{
        try {
            requireRole(nurse,"NURSE",at);
        } catch (UnAuthorizedAction | NotRostered e) {
            System.out.println(e.getMessage());
        }
        Prescription rx=getPrescriptionOrThrow(prescriptionId);
        boolean listed=rx.getItems().stream().anyMatch(i->i.getMedicine().equalsIgnoreCase(medicine));
        if(!listed){
            throw new IllegalAccessException("Medication not listed in prescription.");
        }
        medLog.add(new MedicationLogEntry(rx.getResidentId(), nurse.getId(), medicine, dose, at,rx.getId()));
        audit.add(new ActionLogEntry(at, nurse.getId(), ActionType.ADMINISTER_DOSE, "Rx "+rx.getId()+"|"+medicine+" "+dose+" to resident "+rx.getResidentId()));
    }

    //Record a medication adminstration event.
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

    //returns unmodified medication log
    public List<MedicationLogEntry> getMedicationLog(){
        return Collections.unmodifiableList(medLog);
    }
    //this returns audits logs
    public List<ActionLogEntry> getAuditLog(){
        return Collections.unmodifiableList(audit);
    }
    //Compliance
    /**
     * 
     * Each nurse must not exceed 8h on any single day, 
     * Each day must have at least one doctor 1hour slot
     * throws exception if any rule is violated.
     */
    public void checkCompliance(){
        //coverage per day
        try{
        for(DayOfWeek day: DayOfWeek.values()){
            boolean morningCovered=false;
            boolean afternoonCovered=false;
            for(Nurse n:nurse){
                for(Shift s:n.getShifts()){
                    if(s.getDay().equals(day) && s.getType().equals(ShiftType.MORNING)){
                        morningCovered=true;
                    }else if(s.getDay().equals(day) && s.getType().equals(ShiftType.AFTERNOON)){
                        afternoonCovered=true;
                    }
                    if(morningCovered && afternoonCovered) break;
                }
                    if(morningCovered && afternoonCovered) break;
            }
            
            if(!morningCovered|| !afternoonCovered){
                throw new RosterException("Nurse coverage missing on "+day+
                "(morning="+morningCovered+", afternoon="+afternoonCovered+")");
            }
        }
        }catch(RosterException r){
            System.out.println(r.getMessage());
        }

        //checking nurse shift
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

        //checking doctor slot, atleast one doctor hour per day
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

   //Serialization
    //Saving data to a file
    public void saveToFile(String path)throws IOException{
        try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(path))){
            out.writeObject(this);
        }
    }

    //Restore application state from the file
    public static CareHome loadFromFile(String p) throws IOException, ClassNotFoundException{
        try(ObjectInputStream in=new ObjectInputStream(new FileInputStream(p))){
            
            return (CareHome) in.readObject();
        }
    }
    
    //prints beds from the rooms
    public String printBeds(){
        StringBuilder sb=new StringBuilder();
        for(Ward w: wards){
            sb.append("Ward ").append(w.getName()).append("\n");
            w.getRooms().forEach(r->r.getBeds().forEach(b->sb.append(b.getCode()).append(" ").append("\n")));
        }
        return sb.toString();
    }

    //prints the shift hours of doctors and nurses
    public String printRoster(){
        return "Nurses:\n"+nurse.stream()
                .map(n->n.getName()+"->"+n.getShifts().stream()
                .map(s->s.getDay()+" "+s.hours())
                .collect(Collectors.joining(","))
                )
                .collect(Collectors.joining("\n"))+
                "\nDOCTORS:\n"+doctor.stream()
                .map(d->d.getName()+"->"+d.getShifts().stream()
                .map(s->s.getDay()+" "+s.hours())
                .collect(Collectors.joining(","))
                )
                .collect(Collectors.joining("\n"));
    }

    
    private void requireRole(Staff actor, String role, LocalDateTime at) throws UnAuthorizedAction, NotRostered{
        if(actor==null || actor.getRole()!=role){
            throw new UnAuthorizedAction("unAuthorized action");
        }

        if(role.equals("DOCTOR")){
            Shift today=actor.getShifts().stream().filter(s->s.getDay()==LocalDate.now().getDayOfWeek())
                            .findFirst()
                            .orElse(null);
            if(today==null){throw new NotRostered(role+"not rosetered at"+ at);}
            if(!at.toLocalTime().isBefore(today.getStart())){
            return;}else{
               throw new NotRostered(role+"not rosetered at"+ at);
            }
        }

        if(role.equals("NURSE")){
            Shift today=actor.getShifts().stream().filter(s->s.getDay()==LocalDate.now().getDayOfWeek())
                            .findFirst()
                            .orElse(null);
            if(today==null){throw new NotRostered(role+"not rosetered at"+ at);}
            if(!at.toLocalTime().isBefore(today.getStart())){
            return;}else{
               throw new NotRostered(role+"not rosetered at"+ at);
            }
        }
        if(role!="MANAGER" && !actor.isRosteredAt(at)){
            throw new NotRostered(role+"not rosetered at"+ at);
        }
    }
}


enum Role{
MANAGER,
NURSE,
DOCTOR
}


