package models;

import java.time.LocalTime;

/**
 * predefined shift templates and variables
 * nurses will have 8 hours shifts and doctors uses flexible 1 hours slots
 */
public enum ShiftType {
    MORNING(LocalTime.of(8,0),LocalTime.of(16,0)),
    AFTERNOON(LocalTime.of(14,0),LocalTime.of(22,0)),
    DOCTOR_HOUR(null, null);

    private LocalTime start;
    private LocalTime end;

    ShiftType(LocalTime start, LocalTime end){
        this.start=start;
        this.end=end;
    }

    public LocalTime start(){return start;}
    public LocalTime end(){return end;}
}
