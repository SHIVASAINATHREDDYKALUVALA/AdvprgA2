import Exceptions.BedNotFound;
import Exceptions.BedOccupied;
import models.Resident;

/**
 * this test are to  validates bed allocation and movement rules.
 */
public class BedAllocationTest {
    
    @Test
    void allocate_and_move_resident(){
        CareHome app=new CareHome();
        Resident r=new Resident("R1", "John", "M");
        app.allocateResidentToBed(r, "W1-R1-B1");
        assertThrows(BedOccupied.class,
                    ()->app.allocateResidentToBed(new Resident("R2", "Amy", "F"),"W1-R1-B1"));
        app.moveResident("W1-R1-B1", "W1-R1-B2");
        assertThrows(BedNotFound.class,()->app.moveResident("W1-R1-B1","W1-R1-B3"));
    }
}
