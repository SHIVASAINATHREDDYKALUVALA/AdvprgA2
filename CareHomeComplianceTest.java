import java.time.DayOfWeek;
import java.time.LocalTime;

import javax.print.Doc;

import Exceptions.RosterException;
import models.Doctor;
import models.Nurse;
import models.ShiftType;

/**
 * this test verifies compilance rules
 * check weather nurse coverage for both shifts daily
 * check nurse shift is less than 8 hours daily
 * 1 hour doctor shift
 */
public class CareHomeComplianceTest {
    @Test
    void compilance_ok_with_full_coverage(){
        CareHome app=new CareHome();
        Nurse n1=app.addNurse("Alice");
        Nurse n2=app.addNurse("Bob");
        Doctor d1=app.addDoctor("Dr.Gray");

        for(DayOfWeek day:DayOfWeek.value()){
            app.assignNurseShift(n1, day, ShiftType.MORNING);
            app.assignNurseShift(n2, day, ShiftType.AFTERNOON);
            app.assignDoctorHour(d1, day, LocalTime.of(9,0));

        }
        assertDoesNotThrow(app::checkCompliance);
    }

    @Test
    void missing_doctor_hour_fails(){
        CareHome app=new CareHome();
        Nurse n1=app.addNurse("Alice");
        Nurse n2=app.addNurse("Bob");
        for(DayOfWeek day: DayOfWeek.values()){
            app.assignNurseShift(n1, day, ShiftType.MORNING);
            app.assignNurseShift(n2, day, ShiftType.AFTERNOON);

        }
        assertThrows(RosterException.class,app::checkCompliance);
    }

    @Test
    void nurse_more_than_8h(){
        CareHome app=new CareHome();
        Nurse n1=app.addNurse("Alice");
        Doctor d1=app.addDoctor("Dr. Gray");
        //violation: we are assingning nurse both shifts on monday
        app.assignNurseShift(n1, DayOfWeek.MONDAY, ShiftType.MORNING);
        app.assignNurseShift(n1, DayOfWeek.MONDAY, ShiftType.AFTERNOON);

        for(DayOfWeek day: DayOfWeek.values()){
            app.assignDoctorHour(d1, day, LocalTime.of(10,0));
            if(day!=DayOfWeek.MONDAY)app.assignDoctorHour(n1, day, ShiftType.MORNING);

        }
        assertThrows(RosterException.class,app::checkCompliance);
    }
}
