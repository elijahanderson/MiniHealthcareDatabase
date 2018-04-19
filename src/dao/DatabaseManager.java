package dao;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.derby.jdbc.EmbeddedDriver;
import model.Doctor;
import model.Patient;
import model.Clinic;

/**
 DatabaseManager -- mediates access to HealthcareDatabase
 Based on code by bhoward
  @author Eli Anderson
*/
public class DatabaseManager {
	private Driver driver;
	private Connection conn;
	private DoctorDAO doctorDAO;
	private PatientDAO patientDAO;
	private ClinicDAO clinicDAO;

	private final String url = "jdbc:derby:HealthcareDatabase";

	public DatabaseManager() {
		driver = new EmbeddedDriver();

		Properties prop = new Properties();
		prop.put("create", "false");

		// try to connect to an existing database
		try {
			conn = driver.connect(url, prop);
			conn.setAutoCommit(false);
		}
		catch(SQLException e) {
			// database doesn't exist, so try creating it
			try {
				prop.put("create", "true");
				conn = driver.connect(url, prop);
				conn.setAutoCommit(false);
				create(conn);
			}
			catch (SQLException e2) {
				throw new RuntimeException("cannot connect to database", e2);
			}
		}

		doctorDAO = new DoctorDAO(conn, this);
		patientDAO = new PatientDAO(conn, this);
		clinicDAO = new ClinicDAO(conn, this);
	}

	/**
	 * Initialize the tables and their constraints in a newly created database
	 *
	 * @param conn
	 * @throws SQLException
	 */
	private void create(Connection conn) throws SQLException {
		doctorDAO.create(conn);
		patientDAO.create(conn);
		clinicDAO.create(conn);
		doctorDAO.addConstraints(conn);
		patientDAO.addConstraints(conn);
		conn.commit();
	}

	//***************************************************************
	// Data retrieval functions -- find a model object given its key

	public Doctor findDoctor(int mln) {
		return doctorDAO.find(mln);
	}

	public Patient findPatient(int ssn) {
		return patientDAO.find(ssn);
	}

	public Clinic findClinic(int clinicID) {
		return clinicDAO.find(clinicID);
	}

	public Doctor findDoctorByName(String dname) {
		return doctorDAO.findByName(dname);
	}

	public Patient findPatientByName(String pname) {
		return patientDAO.findByName(pname);
	}

    public Clinic findClinicByName(String cname) {
        return clinicDAO.findByName(cname);
    }

	//***************************************************************
	// Data insertion functions -- create new model object from attributes

	public Doctor insertDoctor(int mln, String dname, String specialty, String phone, String email, Clinic c) {
		return doctorDAO.insert(mln, dname, specialty, phone, email, c);
	}

	public Patient insertPatient(int ssn, String pname, String phone, String email, String bDate, Clinic c, Doctor d) {
		return patientDAO.insert(ssn, pname, phone, email, bDate, c, d);
	}

	public Clinic insertClinic(int cid, String cname, String address, String phone) {
		return clinicDAO.insert(cid, cname, address, phone);
	}

	//***************************************************************
	// Utility functions

	/**
	 * Commit changes since last call to commit
	 */
	public void commit() {
		try {
			conn.commit();
		}
		catch(SQLException e) {
			throw new RuntimeException("cannot commit database", e);
		}
	}

	/**
	 * Abort changes since last call to commit, then close connection
	 */
	public void cleanup() {
		try {
			conn.rollback();
			conn.close();
		}
		catch(SQLException e) {
			System.out.println("fatal error: cannot cleanup connection");
		}
	}

	/**
	 * Close connection and shutdown database
	 */
	public void close() {
		try {
			conn.close();
		}
		catch(SQLException e) {
			throw new RuntimeException("cannot close database connection", e);
		}

		// Now shutdown the embedded database system -- this is Derby-specific
		try {
			Properties prop = new Properties();
			prop.put("shutdown", "true");
			conn = driver.connect(url, prop);
		} catch (SQLException e) {
			System.out.println("Derby has shut down successfully");
		}
	}

	/**
	 * Clear out all data from database (but leave empty tables)
	 */
	public void clearTables() {
		try {
			doctorDAO.clear();
			patientDAO.clear();
			clinicDAO.clear();
		} catch (SQLException e) {
			throw new RuntimeException("cannot clear tables", e);
		}
	}
}
