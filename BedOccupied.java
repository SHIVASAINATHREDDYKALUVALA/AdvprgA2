package Exceptions;
//Exception will be thrown when the resident is placed into occupied bed.
public class BedOccupied extends Exception {
    public BedOccupied(String message){
        super(message);
    }
}
