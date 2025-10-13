import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;

import models.Nurse;
import models.ShiftType;
import models.Manager;
import models.Doctor;

import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class is the main entry point of the application
 */
public class App extends Application{
    
    public static void main(String[] args){launch();}
    
    /*
     * this method will be called automatically with the application starts
     * 
     */
    @Override
    public void start(Stage stage) throws Exception{
            CareHome app=seed();
            FXMLLoader loader=new FXMLLoader(getClass().getResource("MainView.fxml"));
            Scene scene=new Scene(loader.load(),1100,700);
            MainController controller=loader.getController();
            controller.init(app);
            stage.setTitle("RMIT Care Home - JavaFX (Phase 2)");
            stage.setScene(scene);
            stage.show();
    }

    /*
     * this method create manager , two nurses and doctor and 
     * assigns shifts for all days of the week
     */
    private CareHome seed(){
        CareHome app=new CareHome();
        Manager mgr=app.addManager("Eve", "eve", "admin123");
        Nurse n1=app.addNurse("Alice");
        Nurse n2=app.addNurse("Bob");
        Doctor d1=app.addDoctor("Dr.Gray");
        //Assigning shifs and doctor hours for all days of the week
        for(DayOfWeek day:DayOfWeek.values()){
            app.assignNurseShift(n1, day, ShiftType.MORNING);
            app.assignDoctorHour(d1, day, LocalTime.of(10,0));
            app.assignNurseShift(n2, day, ShiftType.AFTERNOON);
        }
        return app;
    }
    
}
