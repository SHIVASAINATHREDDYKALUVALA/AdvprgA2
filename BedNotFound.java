package Exceptions;
//Exception for referenced bed is missing of resident in the bed is missing.
public class BedNotFound extends Exception {
    public BedNotFound(String message){
        super(message);
    }
}


