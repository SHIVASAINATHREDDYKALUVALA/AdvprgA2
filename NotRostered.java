package Exceptions;

//Custum Exception which raises when the staff member is attempting to perform an action when they are not scheduled.
public class NotRostered extends Exception {
    public NotRostered(String m){
        super(m);
    }
    
}
