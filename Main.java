
import java.time.*;
import java.util.*;

import Exceptions.*;
import models.*;


public class Main {
    private static Scanner sc=new Scanner(System.in);
    public static void main(String[] args){
        CareHome app=new CareHome();
        seedSampleRoster(app);
        while(true){
            System.out.println("\n=======RMIT care home=========");
            System.out.println("1. Add Nurse");
            System.out.println("2. Add Doctor");
            System.out.println("3. Assign Nurse Shift");
            System.out.println("4. Assign Doctor Hour");
            System.out.println("5. Allocate Resident to bed");
            System.out.println("6. Move Resident");
            System.out.println("7. Record Medication");
            System.out.println("8. Print Roster");
            System.out.println("9. print Beds");
            System.out.println("10. check compliance");
            System.out.println("11. save");
            System.out.println("12. Load");
            System.out.println("0. Exit");
            System.out.println("Choose: ");
            int c=Integer.parseInt(sc.nextLine().trim());
            try{
                switch (c) {
                    case 0:
                        return;
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
                        moveResident(app);
                        break;
                    case 7:
                        recordMedication(app);
                        break;
                    case 8:
                        System.out.println(app.printRoster());
                        break;
                    case 9:
                        System.out.println(app.printBeds());
                        break;
                    case 10:
                        app.checkCompliance();
                        System.out.println("Compliance OK");
                        break;
                    case 11:
                        app.saveToFile("carehome.dat");
                        System.out.println("Saved.");
                        break;
                    case 12:
                        app=CareHome.loadFromFile("carehome.dat");
                        System.out.println("Loaded");
                        break;
                    default:
                        System.out.println("Invalid.");
                }
            }catch (Exception e) {
                System.out.println("error: "+ e.getMessage());
            }
        }
       
    }

    private static void seedSampleRoster(CareHome app){
        Nurse n1=app.addNurse("Rose");
        Nurse n2=app.addNurse("Raze");
        Doctor d1=app.addDoctor("Dr. mike");

        for(DayOfWeek day: DayOfWeek.values()){
            app.assignNurseShift(n1, day, ShiftType.MORNING);
            app.assignNurseShift(n2, day, ShiftType.AFTERNOON);
            app.assignDoctorHour(d1, day, LocalTime.of(10,0));
        }
    }

    private static void addNurse(CareHome app){
        System.out.print("Name: ");
        String name=sc.nextLine();
        app.addNurse(name);
        System.out.println("Nurse added");
    }

    private static void addDoctor(CareHome app){
        System.out.print("Name: ");
        String name=sc.nextLine();
        app.addDoctor(name);
        System.out.println("Nurse added");
    }

    private static void assignNurseShift(CareHome app){
        var nurses=app.getNurses();
        if(nurses.isEmpty()){
            System.out.println("No nurses");
            return;
        }
        for(int i=0;i<nurses.size();i++){
            System.out.println((i+1)+") "+nurses.get(i));
        }
        System.out.print("Pick nurse #:");
        int idx=Integer.parseInt(sc.nextLine())-1;
        System.out.print("Day (MON..SUN): ");
        DayOfWeek day=DayOfWeek.valueOf(sc.nextLine().trim().toUpperCase());
        System.out.print("Type (MORNING/AFTERNOON): ");
        ShiftType type=ShiftType.valueOf(sc.nextLine().trim().toUpperCase());
        app.assignNurseShift(nurses.get(idx), day, type);
        System.out.println("Assigned");
    }

    private static void assignDoctorHour(CareHome app){
        var docs=app.getDoctors();
        if(docs.isEmpty()){
            System.out.println("No doctors.");
            return;
        }
        for(int i=0;i<docs.size();i++){
            System.out.println((i+1)+") "+docs.get(i));
        }
        System.out.print("Pick doctor #:");
        int idx=Integer.parseInt(sc.nextLine())-1;
        System.out.print("Day (MON..SUN): ");
        DayOfWeek day=DayOfWeek.valueOf(sc.nextLine().trim().toLowerCase());
        System.out.print("Start hour (0-23): ");
        int h=Integer.parseInt(sc.nextLine().trim());
        app.assignDoctorHour(docs.get(idx), day, LocalTime.of(h, 0));
        System.out.println("Assigned.");
    }

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

    private static void moveResident(CareHome app){
        System.out.print("From bed: ");
        String from=sc.nextLine();
        System.out.print("To bed: ");
        String to=sc.nextLine();
        app.moveResident(from, to);
        System.out.println("Moved");
    }

    private static void recordMedication(CareHome app){
        if(app.getNurses().isEmpty()){
            System.out.println("Add nurses first");
            return;
        }
        System.out.print("Resident ID: ");
        String rid=sc.nextLine();
        System.out.println("pick nurse: ");
        var nurses=app.getNurses();
        for(int i=0;i<nurses.size();i++){
            System.out.println((i+1)+") "+nurses.get(i));
        }
        int ni=Integer.parseInt(sc.nextLine())-1;
        System.out.print("Medicine: ");
        String med=sc.nextLine();
        System.out.print("Dose: ");
        String dose=sc.nextLine();
        LocalDateTime now=LocalDateTime.now();
        app.recordMedication(new Resident(rid, "Unknown", "M"), nurses.get(ni), med, dose, now);
        System.out.println("Logged at "+now);
    }


    
}
