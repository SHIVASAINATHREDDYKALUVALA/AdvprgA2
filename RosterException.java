package Exceptions;

//thrown when business rules like shifts , hours and doctor's are violated.
public class RosterException extends Exception {
    public RosterException(String message){
        super(message);
    }
}