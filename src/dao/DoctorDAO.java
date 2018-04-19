package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import model.Clinic;
import model.Patient;
import model.Doctor;

/**
 * Data Access Object for the Doctor table.
 * Encapsulates all of the relevant SQL commands.
 * Based on code by bhoward
 *
 * @author Eli Anderson
 */
public class DoctorDAO {
	private Connection conn;
	private DatabaseManager dbm;

	public DoctorDAO(Connection conn, DatabaseManager dbm) {
		this.conn = conn;
		this.dbm = dbm;
	}

	/**
	 * Create the Doctor table via SQL
	 *
	 * @param conn
	 * @throws SQLException
	 */
	static void create(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "create table DOCTOR(\n"
				+ "mln int not null,\n"
				+ "dname varchar(50) not null,\n"
				+ "specialty varchar(30) not null,\n"
				+ "phone varchar(14) not null,\n"
                + "email varchar(50) not null,\n"
                + "dCID int not null,\n"
				+ "primary key(mln)\n"
				+ ")";
		stmt.executeUpdate(s);
	}

	/**
	 * Modify the Doctor table to add foreign key constraints
     * @param conn
     * @throws SQLException
     */
     static void addConstraints(Connection conn) throws SQLException {
         Statement stmt = conn.createStatement();
         String s = "alter table DOCTOR add constraint doctor_fk_clinic \n"
                        + "foreign key(dCID) references CLINIC(clinicID)";
         stmt.executeUpdate(s);
     }

	/**
	 * Retrieve a Doctor object given its key.
	 *
	 * @param mln
	 * @return the Doctor object, or null if not found
	 */
	public Doctor find(int mln) {
		try {
			String qry = "select dname, specialty, phone, email, dCID from DOCTOR where mln = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setInt(1, mln);
			ResultSet rs = pstmt.executeQuery();

			// return null if doctor doesn't exist
			if (!rs.next())
				return null;

			String dname = rs.getString("dname");
			String specialty = rs.getString("specialty");
            String phone = rs.getString("phone");
            String email = rs.getString("email");
            int cid = rs.getInt("dCID");
			rs.close();

			Clinic clinic = dbm.findClinic(cid);
			Doctor doctor = new Doctor(this, mln, dname, specialty, phone, email, clinic);

			return doctor;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error finding doctor", e);
		}
	}

    /**
    * Retrieve a Doctor object by search by dname
    *
    * @param dname
    * @return the Doctor object, or null if not found
    */
    public Doctor findByName(String dname) {
        try {
            String qry = "select mln, specialty, phone, email, dCID from DOCTOR where dname = ?";
            PreparedStatement pstmt = conn.prepareStatement(qry);
            pstmt.setString(1, dname);
            ResultSet rs = pstmt.executeQuery();

            // return null if doctor doesn't exist
            if (!rs.next())
                return null;

            int mln = rs.getInt("mln");
            String specialty = rs.getString("specialty");
            String phone = rs.getString("phone");
            String email = rs.getString("email");
            int cid = rs.getInt("dCID");
            rs.close();

            Clinic clinic = dbm.findClinic(cid);
            Doctor doctor = new Doctor(this, mln, dname, specialty, phone, email, clinic);

            return doctor;
        } catch (SQLException e) {
            dbm.cleanup();
            throw new RuntimeException("error finding doctor by name", e);
        }
    }

	/**
	 * Add a new Doctor with the given attributes.
	 *
	 * @param mln
	 * @param dname
     * @param specialty
	 * @param phone
	 * @param email
     * @param clinic
	 * @return the new Doctor object, or null if key already exists
	 */
	public Doctor insert(int mln, String dname, String specialty, String phone, String email, Clinic clinic) {
		try {
			// make sure that the medical licence number is currently unused
			if (find(mln) != null)
				return null;

			String cmd = "insert into DOCTOR(mln, dname, specialty, phone, email, dCID) "
					+ "values(?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setInt(1, mln);
			pstmt.setString(2, dname);
			pstmt.setString(3, specialty);
			pstmt.setString(4, phone);
            pstmt.setString(5, email);
            pstmt.setInt(6, clinic.getID());
			pstmt.executeUpdate();

			Doctor doctor = new Doctor(this, mln, dname, specialty, phone, email, clinic);

			return doctor;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error inserting new doctor", e);
		}
	}

	/**
	 * Doctor name was changed in the model object, so propagate the change to the
	 * database.
	 *
	 * @param mln
	 * @param dname
	 */

	public void changeName(int mln, String dname) {
		try {
			String cmd = "update DOCTOR set dname = ? where mln = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setString(1, dname);
			pstmt.setInt(2, mln);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing doctor's name", e);
		}
	}

	/**
	*	Doctor specialty was changed in the model object, so propagate the change to the database.
	*
	* @param mln
	* @param specialty
	*/
	public void changeSpecialty(int mln, String specialty) {
		try {
			String cmd = "update DOCTOR set specialty = ? where mln = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setString(1, specialty);
			pstmt.setInt(2, mln);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing doctor's specialty", e);
		}
	}

	/**
	*	Doctor clinicID was changed in the model object, so propagate the change to the database.
	*
	* @param mln
	* @param clinic
	*/
	public void changeClinic(int mln, Clinic clinic) {
		try {
			String cmd = "update DOCTOR set clinicID = ? where mln = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setInt(1, clinic.getID());
			pstmt.setInt(2, mln);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing doctor's clinic", e);
		}
	}

	/**
	*	Doctor's email was changed in the model object, so propagate the change to the database.
	*
	* @param mln
	* @param email
	*/
	public void changeEmail(int mln, String email) {
		try {
			String cmd = "update DOCTOR set email = ? where mln = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setString(1, email);
			pstmt.setInt(2, mln);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing doctor's email", e);
		}

	}

	/**
	 * Retrieve a Collection of all patients in the given clinic.
	 *
	 * @param mln
	 * @return the Collection
	 */
	public Collection<Patient> getPatients(int mln) {
		try {
			Collection<Patient> patients = new ArrayList<Patient>();
			String qry = "select ssn from PATIENT where pDID = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setInt(1, mln);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int ssn = rs.getInt("ssn");
				patients.add(dbm.findPatient(ssn));
			}
			rs.close();
			return patients;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error getting doctor's patients", e);
		}
	}
	/**
	 * Clear all data from the Doctor table.
	 *
	 * @throws SQLException
	 */
	void clear() throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "delete from DOCTOR";
		stmt.executeUpdate(s);
	}
}
