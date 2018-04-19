import dao.DatabaseManager;
import model.Doctor;
import model.Patient;
import model.Clinic;

import java.util.Collection;

/**
 * Retrieves data from an already created database.
 * Checks that the same data may be retrieved from the database and not just from the in-memory cache
 */
public class Test2 {
    /**
     * @param args
     */
    public static void main(String[] args) {
        DatabaseManager dbm = new DatabaseManager();
        Clinic hospital1 = dbm.findClinicByName("Hospital One");

        // retrieve a table of Hospital One's doctors and their patients
        System.out.println("Doctors and patients of " + hospital1.getName() + ":");
        Collection<Doctor> doctors = hospital1.getDoctors();
        for (Doctor d : doctors) {
            System.out.println("Patients of " + d.getDName() + ":");
            Collection<Patient> patients2 = d.getPatients();
            for (Patient p : patients2) {
                System.out.println(p.getPName());
            }
        }

        dbm.commit();

        dbm.close();

        System.out.println("Done.");
    }
}
