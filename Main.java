import java.util.Scanner;

import Exceptions.*;
public class Main {
    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
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
                        
                
                    default:
                        break;
                }
            }catch (Exception e) {
                System.out.println("error: "+ e.getMessage());
            }
        }
        
    }
}
