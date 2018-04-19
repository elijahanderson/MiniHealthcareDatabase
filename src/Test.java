/*
    Retrieves data from already created database (HealthcareDatabase)
*/

import java.util.Collection;

import dao.DatabaseManager;
import model.Clinic;
import model.Doctor;
import model.Patient;

/**
 * Simple client that inserts sample data then runs a query.
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DatabaseManager dbm = new DatabaseManager();

		dbm.clearTables();

		// department heads are set to null for now; see below
		// Department chem = dbm.insertDepartment(5, "Chemistry", null);
		// Department eng = dbm.insertDepartment(8, "English", null);
		// Department mathcs = dbm.insertDepartment(10, "MathCS", null);

        Clinic hospital1 = dbm.insertClinic(123456789, "Hospital One", "1234 Road St", "555-555-5555");
        Clinic hospital2 = dbm.insertClinic(234567890, "Hospital Two", "2345 Street Rd", "555-867-5309");
        Clinic quickCare = dbm.insertClinic(345678901, "Quick Care", "3456 Avenue Way", "999-123-4567");

        Doctor igor = dbm.insertDoctor(8735, "Dr. Igor Stravinsky", "Dentist", "555-946-7123", "igorstravinsky@hospital1.org", hospital1);
        Doctor owen = dbm.insertDoctor(4679, "Dr. Owen Keefer", "Cardiologist", "555-413-3489", "owenkeefer@hospital1.org", hospital2);
        Doctor susan = dbm.insertDoctor(3799, "Dr. Susan Cruisin", "Gastroentologist", "555-664-1979", "cruisinsusan@hospital1.org", hospital1);

        Patient margaret = dbm.insertPatient(280084699, "Margaret Marco", "margthemonster@gmail.com", "502-718-0803", "1918-05-04", hospital1, igor);
        Patient bill = dbm.insertPatient(123456789, "Bill Bob", "killbill@yahoo.com", "465-159-3578", "1990-11-05", hospital2, owen);
        Patient delbert = dbm.insertPatient(234567890, "Delbert Hillegas", "cantfellthedel@hotmail.com", "369-456-1937", "1958-12-25", hospital1, susan);
        Patient elise = dbm.insertPatient(469155855, "Elise Babees", "elbabees@gmail.com", "169-466-6666", "1996-06-06", hospital1, igor);
        /*
		Faculty john = dbm.insertFaculty(123456789, "John White", "Professor", mathcs);
		Faculty lee = dbm.insertFaculty(234567890, "Lee Williams", "Associate", mathcs);
		Faculty ellen = dbm.insertFaculty(345678901, "Ellen Mitchell", "Associate", chem);
		Faculty alice = dbm.insertFaculty(456789012, "Alice Trupe", "Professor", eng);
		Faculty owen = dbm.insertFaculty(567890123, "Owen Keefer", "Assistant", mathcs);
		dbm.insertFaculty(678901234, "Erich Brumbaugh", "Emeritus", chem);
		*/

		// Have to set department heads after creating faculty,
		// because faculty need to refer to departments (cycle in foreign keys)
        /*
		chem.setHead(ellen);
		eng.setHead(alice);
		mathcs.setHead(lee);

		dbm.insertCourse(mathcs, 131, "Calculus", john);
		dbm.insertCourse(chem, 310, "Physical Chemistry", john);
		dbm.insertCourse(mathcs, 100, "Computer Science I", lee);
		dbm.insertCourse(mathcs, 110, "Computer Science II", lee);
		dbm.insertCourse(mathcs, 381, "Databases", lee);
		dbm.insertCourse(chem, 320, "Organic Chemistry", ellen);
		dbm.insertCourse(chem, 100, "Chemistry I", ellen);
		dbm.insertCourse(eng, 110, "College Writing", alice);
		dbm.insertCourse(mathcs, 285, "Documentation", alice);
		dbm.insertCourse(mathcs, 420, "Syntax", owen);
		dbm.insertCourse(eng, 364, "Syntax", owen);
		*/
		dbm.commit();

		// Now retrieve a table of MathCS faculty and their courses;
		// each course also lists the head of the department offering the course
        /*
		Collection<Faculty> faculty = mathcs.getFaculty();
		for (Faculty fac : faculty) {
			System.out.println(fac);
			Collection<Course> courses = fac.getCourses();
			for (Course c : courses) {
				System.out.println("  " + c + " [Head: " + c.getDept().getHead() + "]");
			}
		}
		*/
        System.out.println(hospital1.toString());
        System.out.println(hospital2.toString());
		System.out.println(quickCare.toString());

		System.out.println(igor.toString());
		System.out.println(margaret.toString());
		System.out.println("\n--------------------------------------------------\n");

		// print patients belonging to hospital1
		System.out.println("Patients of " + hospital1.getName() + ":");
		Collection<Patient> patients = hospital1.getPatients();
		for (Patient p : patients) {
			System.out.println(p.toString());
		}

		System.out.println("\n--------------------------------------------------\n");

		// print doctors and patients of those doctors belonging to hospital1
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

		System.out.println("Done");
	}

}
