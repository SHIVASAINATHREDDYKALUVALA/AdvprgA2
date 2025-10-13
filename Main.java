
import java.time.*;
import java.util.*;

import Exceptions.*;
import models.*;

/*
 * Text menu for phase1 features
 */

public class Main {
    private static Scanner sc=new Scanner(System.in);
    public static void main(String[] args){
        CareHome app=new CareHome();
        seedSampleRoster(app);
        //loop for text based menu
        while(true){
            System.out.println("\n=======RMIT care home=========");
            System.out.println("1. Add Nurse");
            System.out.println("2. Add Doctor");
            System.out.println("3. Assign Nurse Shift");
            System.out.println("4. Assign Doctor Hour");
            System.out.println("5. Allocate Resident to bed");
            System.out.println("6. Nurse Move Resident");
            System.out.println("7. Doctor Add Prescription");
            System.out.println("8. Print Roster");
            System.out.println("9. print Beds");
            System.out.println("10. check compliance");
            System.out.println("11. save");
            System.out.println("12. Load");
            System.out.println("13. Doctor Update prescription");
            System.out.println("14. Nurse Administer Dose (by Rx)");
            System.out.println("15. Show Audit log");
            System.out.println("0. Exit");
            System.out.println("Choose: ");
            //taking user choice as input
            int c=Integer.parseInt(sc.nextLine().trim());
            try{
                switch (c) {
                    case 0:
                        return; //exit's the program
                    case 1:
                        addNurse(app);
                        break;
                    case 2:
                        addDoctor(app);
                        break;
                    case 3:
                        assignNurseShift(app);
                        break;
                    case 4:
                        assignDoctorHour(app);
                        break;
                    case 5:
                        allocateResident(app);
                        break;
                    case 6:
                        nurseMoveResident(app);
                        break;
                    case 7:
                        doctorAddRx(app);
                        break;
                    case 8:
                        //prints entire nurses and doctors day/shift
                        System.out.println(app.printRoster());
                        break;
                    case 9:
                        //prints ward, room,bed layout 
                        System.out.println(app.printBeds());
                        break;
                    case 10:
                        app.checkCompliance();
                        System.out.println("Compliance OK");
                        break;
                    case 11:
                        //save's current state to a file
                        app.saveToFile("carehome.dat");
                        System.out.println("Saved.");
                        break;
                    case 12:
                        //load data from the file
                        app=CareHome.loadFromFile("carehome.dat");
                        System.out.println("Loaded");
                        break;
                    case 13:
                        doctorUpdateRx(app);
                        break;
                    case 14:
                        nurseAdminDose(app);
                        break;
                    case 15:
                        app.getAuditLog().forEach(e->System.out.println(e.getWhen()+"| Staff: "+
                        e.getStaffId()+" |Action :"+e.getType()+"| Details :"+e.getDetails()+"\n"));
                        break;
                    
                    default:
                        System.out.println("Invalid.");
                }
            }catch (Exception e) {
                System.out.println("error: "+ e.getMessage());
            }
        }
       
    }

    //initialize application with two nurses and doctor and assigning them with shifts
    private static void seedSampleRoster(CareHome app){
        Nurse n1=app.addNurse("Rose");
        Nurse n2=app.addNurse("Raze");
        Doctor d1=app.addDoctor("Dr. mike");
        Manager m1=app.addManager("Mike", "mike", "pass@123");
        for(DayOfWeek day: DayOfWeek.values()){
            app.assignNurseShift(n1, day, ShiftType.MORNING);
            app.assignNurseShift(n2, day, ShiftType.AFTERNOON);
            app.assignDoctorHour(d1, day, LocalTime.of(23,0));
        }
    }

    //method to add new nurse
    private static void addNurse(CareHome app){
        System.out.print("Name: ");
        String name=sc.nextLine();
        app.addNurse(name);
        System.out.println("Nurse added");
    }

    //this method add's new doctor
    private static void addDoctor(CareHome app){
        System.out.print("Name: ");
        String name=sc.nextLine();
        app.addDoctor(name);
        System.out.println("Nurse added");
    }

    //assign's nurse to a shift on a specific day
    private static void assignNurseShift(CareHome app){
        var nurses=app.getNurses();
        if(nurses.isEmpty()){
            System.out.println("No nurses");
            return;
        }
        for(int i=0;i<nurses.size();i++){
            System.out.println((i+1)+") "+nurses.get(i).getName());
        }
        System.out.print("Pick nurse #:");
        int idx=Integer.parseInt(sc.nextLine())-1;
        System.out.print("Day (MON..SUN): ");
        DayOfWeek day=DayOfWeek.valueOf(sc.nextLine().trim().toUpperCase());
        System.out.print("Type (MORNING(8am to 4pm)/AFTERNOON(2pm to 10pm)): ");
        ShiftType type=ShiftType.valueOf(sc.nextLine().trim().toUpperCase());
        app.assignNurseShift(nurses.get(idx), day, type);
        System.out.println("Assigned");
    }

    //assigns doctor hour on a specific day
    private static void assignDoctorHour(CareHome app){
        var docs=app.getDoctors();
        if(docs.isEmpty()){
            System.out.println("No doctors.");
            return;
        }
        for(int i=0;i<docs.size();i++){
            System.out.println((i+1)+") "+docs.get(i).getName());
        }
        System.out.print("Pick doctor #:");
        int idx=Integer.parseInt(sc.nextLine())-1;
        System.out.print("Day (MON..SUN): ");
        DayOfWeek day=DayOfWeek.valueOf(sc.nextLine().trim().toUpperCase());
        System.out.print("Start hour (0-23): ");
        int h=Integer.parseInt(sc.nextLine().trim());
        app.assignDoctorHour(docs.get(idx), day, LocalTime.of(h, 0));
        System.out.println("Assigned.");
    }

    //this method create a resident and allocates them into a given bed
    private static void allocateResident(CareHome app){
        System.out.print("Resident ID: ");
        String id=sc.nextLine();
        System.out.print("Name: ");
        String name=sc.nextLine();
        System.out.print("Gender (M/F): ");
        String g=sc.nextLine().trim().toUpperCase();
        Resident r=new Resident(id, name, g);
        System.out.print("Bed code (e.g., W1-R1-B1): ");
        String bed=sc.nextLine();
        app.allocateResidentToBed(r, bed);
        System.out.println("Allocated.");
    }

    //nurse initiated resident move from one bed to another
    private static void nurseMoveResident(CareHome app){
        var nurses=app.getNurses();
        if(nurses.isEmpty()){
            System.out.println("No nurses.");
            return;
        }
        for(int i=0;i<nurses.size();i++){
            System.out.println((i+1)+") "+nurses.get(i).getName());
        }
        int ni=Integer.parseInt(sc.nextLine())-1;
        System.out.print("From bed:");
        String from=sc.nextLine();
        System.out.print("To bed:");
        String to=sc.nextLine();
        try {
            app.nurseMoveResident(nurses.get(ni), from, to, LocalDateTime.now());
        } catch (BedNotFound | BedOccupied e) {
            System.out.println(e.getMessage());
        } catch (UnAuthorizedAction |NotRostered e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Moved.");
    }

    //this method is used to create a new prescription by doctor
    private static void doctorAddRx(CareHome app){
        var docs=app.getDoctors();
        if(docs.isEmpty()){
            System.out.println("No doctors.");
            return;
        }
        for(int i=0;i<docs.size();i++){
            System.out.println((i+1)+") "+docs.get(i).getName());
        }
        int di=Integer.parseInt(sc.nextLine())-1;
        System.out.print("Bed code (resident must be present): ");
        String bed=sc.nextLine();
        List<PrescriptionItem> items=new ArrayList<>();
        while (true) {
           System.out.print("Medicine (blank to stop):");
           String med=sc.nextLine().trim();
           if(med.isBlank()) break;
           System.out.print("Dose: ");
           String dose=sc.nextLine().trim();
           System.out.print("Time (HH:mm): ");
           String t=sc.nextLine().trim();
           items.add(new PrescriptionItem(med, dose, LocalTime.parse(t))); 
        }
        Prescription rx;
        try {
            rx = app.doctorAddPrescription(docs.get(di), bed, items, LocalDateTime.now());
            if(rx==null){return;}
            System.out.println("Added prescription "+rx.getId());
        } catch (BedNotFound e) {
           System.out.println(e.getMessage());
        } catch (UnAuthorizedAction e) {
           System.out.println(e.getMessage());
        } catch (NotRostered e) {
           System.out.println(e.getMessage());
        }
        
    }

    //allows doctor to update the prescription of preticular resident
    private static void doctorUpdateRx(CareHome app){
        System.out.print("Rx ID: ");
        String rxId=sc.nextLine().trim();
        var docs=app.getDoctors();
        if(docs.isEmpty()){
            System.out.println("No doctors.");
            return;
        }
        for(int i=0;i<docs.size();i++){
            System.out.println((i+1)+") "+docs.get(i).getName());
        }
        int di=Integer.parseInt(sc.nextLine())-1;
        List<PrescriptionItem> newItems=new ArrayList<>();
        while(true){
            System.out.print("Medicine (black to stop):");
            String med=sc.nextLine().trim();
            if(med.isBlank())
            break;
            System.out.print("Dose: ");
            String dose=sc.nextLine().trim();
            System.out.print("Time (HH:mm): ");
            String t=sc.nextLine().trim();
            newItems.add(new PrescriptionItem(med, dose, LocalTime.parse(t)));
        }
        try {
            app.doctorAddPrescription(docs.get(di), rxId, newItems, LocalDateTime.now());
        } catch (BedNotFound e) {
           System.out.println(e.getMessage());
        } catch (UnAuthorizedAction e) {
           System.out.println(e.getMessage());
        } catch (NotRostered e) {
           System.out.println(e.getMessage());
        }
        System.out.println("Updated Rx "+rxId);
    }

    //nurse logs that a dose for a specific prescription.
    private static void nurseAdminDose(CareHome app){
        var nurses =app.getNurses();
        if(nurses.isEmpty()){
            System.out.println("No nurses");
            return;
        }
        for(int i=0;i<nurses.size();i++){
            System.out.println((i+1)+") "+nurses.get(i).getName());
        }
        int ni=Integer.parseInt(sc.nextLine())-1;
        System.out.print("Rx ID: ");
        String rxId=sc.nextLine().trim();
        System.out.print("Medicine: ");
        String med=sc.nextLine().trim();
        System.out.print("Dose: ");
        String dose=sc.nextLine().trim();
        try {
            app.nurseAdministerDose(nurses.get(ni), rxId, med, dose, LocalDateTime.now());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println("Dose logged");
    }

   
    
    

    
}
