package Exceptions;

//Exception will be raised when the staff member perform action that they are not allowed to do.
public class UnAuthorizedAction extends Exception {
    public UnAuthorizedAction(String message){
        super(message);
    }
}