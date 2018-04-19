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
            Collection<Patient> patients = d.getPatients();
            for (Patient p : patients) {
                System.out.println(p.getPName());
            }
        }

        // Quick Care was converted into an insane asylum
        Clinic insaneAsylum = dbm.findClinicByName("Quick Care");
        insaneAsylum.setName(insaneAsylum.getID(), "Trans-Allegheny Lunatic Asylum");
        Doctor romanikov = dbm.insertDoctor(11366, "Dmitri Romanikov", "Insanology", "555-987-1566", "defnotinsane@allegheny.org", insaneAsylum);
        dbm.insertPatient(333445555, "Hannibal Lecter", "skinwearer@gmail.com", "555-145-5434", "1950-02-14", insaneAsylum, romanikov);
        dbm.insertPatient(164222489, "Jack the Ripper", "knifeguy@yahoo.com", "555-666-1344", "1884-06-06", insaneAsylum, romanikov);

        // retrieve a table of Trans-Allegheny's doctors and their patients
        System.out.println("Doctors and patients of " + insaneAsylum.getName() + ":");
        Collection<Doctor> doctors2 = insaneAsylum.getDoctors();
        for (Doctor d : doctors2) {
            System.out.println("Patients of " + d.getDName() + ":");
            Collection<Patient> patients = d.getPatients();
            for (Patient p : patients) {
                System.out.println(p.getPName());
            }
        }

        dbm.commit();

        dbm.close();

        System.out.println("Done.");
    }
}
