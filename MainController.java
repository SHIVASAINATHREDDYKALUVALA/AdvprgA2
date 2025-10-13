import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javafx.scene.control.ListView;

import Exceptions.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import models.*;

//javaFX controller for our application
public class MainController {
    public enum Gender{M,F}


    @FXML private GridPane gridBeds;
    @FXML private TextArea txtAudit;
    @FXML private Label lblStatus;

    @FXML private ComboBox<String> cbNurse;
    @FXML private ComboBox<String> cbDoctor;
    @FXML private ComboBox<String> cbManager;

    @FXML private TextField tfResidentId,tfResidentName, tfBedCode, tfMoveFrom, tfMoveTo;
    @FXML private ComboBox<Gender> cbGender;

    @FXML private TextField tfRxBedCode, tfRxId, tfMed,tfDose, tfTime;
    @FXML private TextField tfAdminRxId,tfAdminMed,tfAdminDose;

    @FXML private ComboBox<String> cbTargetStaff;
    @FXML private TextField tfNewUser;
    @FXML private PasswordField pfNewPass;

    @FXML private ListView<String> lvRx;

    private CareHome app;

    private final ObservableList<String> nurses=FXCollections.observableArrayList();
    private final ObservableList<String> doctors=FXCollections.observableArrayList();
    private final ObservableList<String> managers=FXCollections.observableArrayList();
    private final ObservableList<String> allStaff=FXCollections.observableArrayList();

    /**
     * called by application after FXML is loaded
     */
    public void init(CareHome app){
        this.app=app;
        try{ this.app=app.loadFromFile("carehome.dat");
        
        }catch(Exception e){//error(e);
            }
        
        nurses.setAll(app.getNurses().stream().map(Nurse::getName).collect(Collectors.toList()));
        doctors.setAll(app.getDoctors().stream().map(Doctor::getName).collect(Collectors.toList()));
        managers.setAll(app.getManagers().stream().map(Manager::getName).collect(Collectors.toList()));
        allStaff.setAll(mergeStaff());

        cbNurse.setItems(nurses);
        cbDoctor.setItems(doctors);
        cbManager.setItems(managers);
        cbTargetStaff.setItems(allStaff);
        cbGender.getItems().setAll(Gender.values());

        drawBedsGrid();
        refreshAudit();
        setStatus("Ready");
        //drawBedsGrid();
    }

    //this method merges all the staffs into single list
    private List<String> mergeStaff(){
        List<String> staff=new ArrayList<>();
        staff.addAll(app.getNurses().stream().map(Nurse::getName).collect(Collectors.toList()));
        staff.addAll(app.getDoctors().stream().map(Doctor::getName).collect(Collectors.toList()));
        staff.addAll(app.getManagers().stream().map(Manager::getName).collect(Collectors.toList()));
        return staff;
    }

    //Draws the ward layout with rooms
    private void drawBedsGrid(){
        gridBeds.getChildren().clear();
        gridBeds.setHgap(10);
        gridBeds.setVgap(10);
        gridBeds.setPadding(new Insets(10));


        int row=0;
        for(int w=1; w<=2; w++){
            for(int r=1;r<=6;r++){
                HBox roomBox=new HBox(8);
                roomBox.setPadding(new Insets(5));
                Label roomLbl=new Label("W"+w+"-R"+r+":");
                roomLbl.getStyleClass().add("room-title");
                roomBox.getChildren().add(roomLbl);

                for(int b=1;b<=4;b++){
                    String code="W"+w+"-R"+r+"-B"+b;
                    Bed bed=findBed(code);
                   // System.out.println("11");
                    if(bed==null) continue;
                   // System.out.println("1");

                    Button bedBtn=new Button(codeShort(code));
                    bedBtn.setPrefWidth(85);
                    bedBtn.setTooltip(new Tooltip(bed.toString()));
                    //System.out.println("color"+bed.getCode());
                    bedBtn.setStyle(colorForBed(bed));
                    bedBtn.setOnAction(e->{
                        tfBedCode.setText(code);
                        tfMoveFrom.setText(code);
                        tfRxBedCode.setText(code);
                        showPrescriptionsForBed(code);
                        setStatus("Selected"+code);
                    });
                    roomBox.getChildren().add(bedBtn);
                }
                gridBeds.add(roomBox,0,row++);
            }
        }
    }

    //returns color based on gender
    private String colorForBed(Bed bed){
        //System.out.println(bed.getCode());
        Resident r=bed.getOccupant();
        if(r==null){
           // System.out.println(bed.getCode());
            return "-fx-background-color:#d9d9d9;";}
        if(r.getGender().equals("M")){
            //System.out.println(bed.getCode()+" M");
            return "-fx-background-color:#87CEFA;";
        }else if(r.getGender().equals("F")){
            //System.out.println(bed.getCode()+ " F");
            return "-fx-background-color:#FFC0CB;";
        }else{
            //System.out.println(bed.getCode()+""+r.getGender());
            return "-fx-background-color:#d9d9d9;";
        }
       
    }

    private String codeShort(String full){return full.replace("W","W").replace("-R", "R").replace("-B", "B");
}
    //returns the bed or null if bed is not present
    private Bed findBed(String code){
        try{
            String[] parts=code.split("-");
            int w=Integer.parseInt(parts[0].substring(1));
            int r=Integer.parseInt(parts[1].substring(1));
            int b=Integer.parseInt(parts[2].substring(1));

            int[] bedsPerRoom={1,2,2,3, 4,4};
            if(w<1||w>2 || r<1||r>6 ||b<1|| b>bedsPerRoom[r-1]) return null;


            String bedsDump=app.printBeds();
            
            Bed snapshot=app.findBedOrThrow(code);
            //Bed snapshot=new Bed(code);
            //Optional<Resident> occ=extractResidentFromDumpLine(bedsDump,code);
            //occ.ifPresent(snapshot::assign);
            return snapshot;
        }catch(BedNotFound e){
            error(e);
        }catch(Exception e){
            return null;
        }
        return null;
    }


    private Optional<Resident> extractResidentFromDumpLine(String dump, String code){
        for(String line: dump.split("\\R")){
            line=line.trim();
            if(!line.startsWith(code+" ->")) continue;
            if(line.endsWith("VACANT")) return Optional.empty();

            int open=line.indexOf("->")+2;
            String tail=line.substring(open).trim();

            int nameEnd=tail.indexOf(" [");
            int genderEnd=tail.indexOf("]");
            int idOpen=tail.indexOf("(");
            int idClose=tail.indexOf(")");

            if(nameEnd>0 && genderEnd>0 && idOpen>0 && idClose >idOpen){
                String name=tail.substring(0, nameEnd).trim();
                String g=tail.substring(nameEnd+2,genderEnd);
                String id=tail.substring(idOpen+1, idClose);
                return Optional.of(new Resident(id, name, Gender.valueOf(g).toString()));
            }

        }
        return Optional.empty();
    }

    //add a resident to a chosen bed from UI inputs
    @FXML
    private void onAddResidentToBed(){
        try{
            String id=tfResidentId.getText().trim();
            String name=tfResidentName.getText().trim();
            String g=cbGender.getValue().toString();
            String bedCode=tfBedCode.getText().trim();
            
            if(id.isBlank()|| name.isBlank() ||g==null || bedCode.isBlank())
                throw new IllegalArgumentException("Provide Id, name, Gender,Bedcode.");
            app.allocateResidentToBed(new Resident(id, name, g.toString()), bedCode);
            setStatus("Resident added to "+bedCode);
            redraw();
        }catch(Exception ex){
            error(ex);
        }
    }
    //move a resident between beds(nurse action)
    @FXML
    private void onNurseMoveResident(){
        try{
        String n=cbNurse.getValue();
        Nurse nurse =app.getNurses().stream().filter(nu->nu.getName().equals(n)).findFirst().orElse(null);
        //System.out.println(nurse.getName());
        if(nurse==null) throw new IllegalArgumentException("select a nurse.");
        String from=tfMoveFrom.getText().trim();
        String to=tfMoveTo.getText().trim();
        app.nurseMoveResident(nurse, from, to, LocalDateTime.now());
        setStatus("Resident moved "+from+"->"+to);
        redraw();
        }catch(Exception ex){
            error(ex);
        }
    }

    //Doctor adds a new prescription for the resident
    @FXML
    private void onDoctorAddRx(){
        try{
            String d=cbDoctor.getValue();
            Doctor doc =app.getDoctors().stream().filter(nu->nu.getName().equals(d)).findFirst().orElse(null);

            if(doc==null) throw new IllegalArgumentException("select a Doctor.");
            String bed=tfRxBedCode.getText().trim();
            String med=tfMed.getText().trim();
            String dose=tfDose.getText().trim();
            String t=tfTime.getText().trim();

            if(!bed.isBlank()){showPrescriptionsForBed(bed);}
            List<PrescriptionItem> items=new ArrayList<>();
            if(!med.isBlank() && !dose.isBlank() && !t.isBlank()){
                items.add(new PrescriptionItem(med, dose, LocalTime.parse(t)));
            }
            Prescription rx=app.doctorAddPrescription(doc, bed, items, LocalDateTime.now());
            tfRxId.setText(rx.getId());
            setStatus("Rx added: "+rx.getId());
            refreshAudit();
        }catch(Exception ex){
            error(ex);
        }
    }

    //doctor update an existing prescription
    @FXML
    private void onDoctorUpdateRx(){
        try{
            String d=cbDoctor.getValue();
            Doctor doc =app.getDoctors().stream().filter(nu->nu.getName().equals(d)).findFirst().orElse(null);
            if(doc==null) throw new IllegalArgumentException("select a Doctor");
            String rxId=tfRxId.getText().trim();
            String med=tfMed.getText().trim();
            String dose=tfDose.getText().trim();
            String t=tfTime.getText().trim();

            List<PrescriptionItem> items=new ArrayList<>();
            if(!med.isBlank() && !dose.isBlank() && !t.isBlank()){
                items.add(new PrescriptionItem(med, dose, LocalTime.parse(t)));
            }
            app.doctorUpdatePrescription(doc, rxId, items, LocalDateTime.now());
            setStatus("Rx updated:"+rxId);
            refreshAudit();
        }catch(Exception ex){
            error(ex);
        }
    }

    @FXML
    private void onNurseAdminDose(){
        try{
            String n=cbNurse.getValue();
        Nurse nurse =app.getNurses().stream().filter(nu->nu.getName().equals(n)).findFirst().orElse(null);
        if(nurse==null) throw new IllegalArgumentException("select a nurse.");
            String rxId=tfAdminRxId.getText().trim();
            String med=tfAdminMed.getText().trim();
            String dose=tfAdminDose.getText().trim();
            app.nurseAdministerDose(nurse, rxId, med, dose, LocalDateTime.now());
            setStatus("Dose administered");
            refreshAudit();
        }catch(Exception ex){
            error(ex);
        }
    }
    //methods used by manager to update staff credentials
    @FXML
    private void onManagerUpdateStaffCreds(){
        try{
            String m=cbManager.getValue();
            Manager mgr=app.getManagers().stream().filter(ma->ma.getName().equals(m)).findFirst().orElse(null);
            String s=cbTargetStaff.getValue();
            Staff target=app.getManagers().stream().filter(ma->ma.getName().equals(s)).findFirst().orElse(null);
            if(target==null){target=app.getNurses().stream().filter(nu->nu.getName().equals(s)).findFirst().orElse(null);}
            if(target==null){target=app.getDoctors().stream().filter(nu->nu.getName().equals(s)).findFirst().orElse(null);}
            if(mgr == null || target == null) throw new IllegalArgumentException("Select manager and staff.");
            String user=tfNewUser.getText().trim();
            String pass=pfNewPass.getText();
            app.updateStaffCredentials(mgr, target, user, pass);
            setStatus("Credentials update");
            refreshAudit(); 
        }catch(Exception ex){
            error(ex);
        }
    }
    //runs compliance test
    @FXML
    private void onCheckCompliance(){
        try{
            app.checkCompliance();
            setStatus("Compliance ok");
        }catch(Exception ex){
            error(ex);
        }
    }

    //this method redraws ui
    private void redraw(){
        drawBedsGrid();
        refreshAudit();
    }
    //adds new text to audit text
    private void refreshAudit(){
        String out=app.getAuditLog().stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
        txtAudit.setText(out);
    }
    //sets status label text
    private void setStatus(String msg){lblStatus.setText(msg);}

    //shows erro in status and a blocking aleart
    private void error(Exception ex){
        lblStatus.setText("Error: "+ex.getMessage());
        new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
    }

    //Saves data to the file
    @FXML
    private void onSaveToFile(){
        try{
            app.saveToFile("carehome.dat");
            setStatus("File saved");
        }catch(Exception e){
            error(e);
        }
    }

    private void showPrescriptionsForBed(String bedCode){
        try{
        Bed dump=app.findBedOrThrow(bedCode);
        //Resident maybeResident=extractResidentFromDumpLine(dump, bedCode);
        if(dump.getOccupant()==null){
            lvRx.getItems().setAll("No resident in"+ bedCode);
            return;
        }
        Resident resident=dump.getOccupant();
        var rxs=app.getPrescriptionsForResident(resident.getId());
        if(rxs.isEmpty()){
            lvRx.getItems().setAll("No prescriptions for resident "+resident.getId());
            return;
        }
        List<String> lines=new ArrayList<>();
        for(var rx:rxs){
            lines.add("Rx "+rx.getId());
            for(var item: rx.getItems()){
                lines.add(" ."+item.getMedicine()+" "+item.getDose()+" ");
            }
        }
        lvRx.getItems().setAll(lines);
    }catch(BedNotFound e){
        error(e);
    }
    }
}


/*
 * boolean exists=Arrays.stream(bedsDump.split("\\R"))
                            .anyMatch(line->line.trim().startsWith(code+" ->"));
            if(!exists) return null;
 */