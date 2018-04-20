package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Patient;
import model.Clinic;
import model.Doctor;

/**
 * Data Access Object for the Patient table.
 * Encapsulates all of the relevant SQL commands.
 * Based on code by bhoward
 *
 * @author Eli Anderson
 */
public class PatientDAO {
	private Connection conn;
	private DatabaseManager dbm;

	public PatientDAO(Connection conn, DatabaseManager dbm) {
		this.conn = conn;
		this.dbm = dbm;
	}

	/**
	 * Create the Patient table via SQL
	 *
	 * @param conn
	 * @throws SQLException
	 */
	static void create(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "create table PATIENT(\n"
				+ "ssn int not null,\n"
				+ "pname varchar(50) not null,\n"
				+ "email varchar(50) not null,\n"
				+ "phone varchar(14) not null,\n"
				+ "birthDate varchar(10) not null,\n" // int yyyy-mm-dd format
				+ "pCID int,\n" // patient can be in records but not currently getting treatment
				+ "pDID int,\n"
				+ "primary key(ssn)\n"
				+ ")";
		stmt.executeUpdate(s);
	}

	/**
	 * Modify the Patient table to add foreign key constraints
	 * @param conn
	 * @throws SQLException
     */
	 static void addConstraints(Connection conn) throws SQLException {
         Statement stmt = conn.createStatement();
         String s1 = "alter table PATIENT add constraint patient_fk_clinic \n"
                        + "foreign key(pCID) references CLINIC(clinicID) on delete set null\n";
         String s2 = "alter table PATIENT add constraint patient_fk_doctor \n"
				 		+ "foreign key(pDID) references DOCTOR(mln) on delete set null";
         stmt.executeUpdate(s1);
         stmt.executeUpdate(s2);
	 }

	/**
	 * Retrieve a Patient object given its key.
	 *
	 * @param ssn
	 * @return the Patient object, or null if not found
	 */
	public Patient find(int ssn) {
		try {
			String qry = "select pname, email, phone, birthDate, pCID, pDID from PATIENT where ssn = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setInt(1, ssn);
			ResultSet rs = pstmt.executeQuery();

			// return null if patient doesn't exist
			if (!rs.next())
				return null;

			String pname = rs.getString("pname");
			String email = rs.getString("email");
			String phone = rs.getString("phone");
			String bDate = rs.getString("birthDate");
			int cid = rs.getInt("pCID");
			int mln = rs.getInt("pDID");
			rs.close();

			Clinic clinic = dbm.findClinic(cid);
			Doctor doctor = dbm.findDoctor(mln);
			Patient patient = new Patient(this, ssn, pname, email, phone, bDate, clinic, doctor);

			return patient;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error finding patient", e);
		}
	}

	/**
    * Retrieve a Patient object by search by pname
    *
    * @param pname
    * @return the Patient object, or null if not found
    */
    public Patient findByName(String pname) {
		try {
			String qry = "select ssn, email, phone, birthDate, pCID, pDID from PATIENT where pname = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setString(1, pname);
			ResultSet rs = pstmt.executeQuery();

			// return null if patient doesn't exist
			if (!rs.next())
				return null;

			int ssn = rs.getInt("ssn");
			String email = rs.getString("email");
			String phone = rs.getString("phone");
			String bDate = rs.getString("birthDate");
			int cid = rs.getInt("pCID");
			int mln = rs.getInt("pDID");
			rs.close();

			Clinic clinic = dbm.findClinic(cid);
			Doctor doctor = dbm.findDoctor(mln);
			Patient patient = new Patient(this, ssn, pname, email, phone, bDate, clinic, doctor);

			return patient;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error finding patient by name", e);
		}
    }

	/**
	 * Add a new Patient with the given attributes.
	 *
	 * @param ssn
	 * @param pname
	 * @param email
	 * @param phone
	 * @param birthDate
	 * @param clinic
	 * @return the new Patient object, or null if key already exists
	 */
	public Patient insert(int ssn, String pname, String email, String phone, String birthDate, Clinic clinic, Doctor doctor) {
		try {
			// make sure that the dept, num pair is currently unused
			if (find(ssn) != null)
				return null;

			String cmd = "insert into PATIENT(ssn, pname, email, phone, birthDate, pCID, pDID) "
					+ "values(?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setInt(1, ssn);
			pstmt.setString(2, pname);
			pstmt.setString(3, email);
			pstmt.setString(4, phone);
			pstmt.setString(5, birthDate);
			pstmt.setInt(6, clinic.getID());
			pstmt.setInt(7, doctor.getMLN());
			pstmt.executeUpdate();

			Patient patient = new Patient(this, ssn, pname, email, phone, birthDate, clinic, doctor);

			return patient;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error inserting new patient", e);
		}
	}

	/**
	 * Patient Name was changed in the model object, so propagate the change to the
	 * database.
	 *
	 * @param ssn
	 * @param pname
	 */

	public void changeName(int ssn, String pname) {
		try {
			String cmd = "update PATIENT set pname = ? where ssn = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setString(1, pname);
			pstmt.setInt(2, ssn);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing patient name", e);
		}
	}

	/**
	*	Patient's clinic was changed in the model object, so propagate the change to the database.
	*
	* @param ssn
	* @param clinic
	*/
	public void changeClinic(int ssn, Clinic clinic) {
		try {
			String cmd = "update PATIENT set pCID = ? where ssn = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setInt(1, clinic.getID());
			pstmt.setInt(2, ssn);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing patient's clinic", e);
		}
	}

	/**
	 *	Patient's doctor was changed in the model object, so propagate the change to the database.
	 *
	 * @param ssn
	 * @param doctor
	 */
	public void changeDoctor(int ssn, Doctor doctor) {
		try {
			String cmd = "update PATIENT set pDID = ? where ssn = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setInt(1, doctor.getMLN());
			pstmt.setInt(2, ssn);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing patient's doctor", e);
		}
	}

	/**
	*	Patient email was changed in the model object, so propagate the change to the database.
	*
	* @param ssn
	* @param email
	*/
	public void changeEmail(int ssn, String email) {
		try {
			String cmd = "update PATIENT set email = ? where ssn = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setString(1, email);
			pstmt.setInt(2, ssn);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing patient's email", e);
		}
	}

	/**
	 *	Patient phone number was changed in the model object, so propagate the change to the database.
	 *
	 * @param ssn
	 * @param phone
	 */
	public void changePhoneNum(int ssn, String phone) {
		try {
			String cmd = "update PATIENT set phone = ? where ssn = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setString(1, phone);
			pstmt.setInt(2, ssn);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing patient's phone number", e);
		}
	}

	/**
	 *	Patient birth date was changed in the model object, so propagate the change to the database.
	 *
	 * @param ssn
	 * @param bDate
	 */
	public void changeBirthDate(int ssn, String bDate) {
		try {
			String cmd = "update PATIENT set birthDate = ? where ssn = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setString(1, bDate);
			pstmt.setInt(2, ssn);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing patient's birth date", e);
		}
	}

	/**
	 * Clear all data from the Patient table.
	 *
	 * @throws SQLException
	 */
	void clear() throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "delete from Patient";
		stmt.executeUpdate(s);
	}
}
