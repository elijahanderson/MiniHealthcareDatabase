package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.ArrayList;

import model.Clinic;
import model.Patient;
import model.Doctor;

/**
 * Data Access Object for the Clinic table.
 * Encapsulates all of the relevant SQL commands.
 * Based on code by bhoward
 *
 * @author Eli Anderson
 */
public class ClinicDAO {
	private Connection conn;
	private DatabaseManager dbm;

	public ClinicDAO(Connection conn, DatabaseManager dbm) {
		this.conn = conn;
		this.dbm = dbm;
	}

	/**
	 * Create the Clinic table via SQL
	 *
	 * @param conn
	 * @throws SQLException
	 */
	static void create(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "create table CLINIC(\n"
				+ "clinicName varchar(30) not null,\n"
				+ "address varchar(50) not null,\n"
				+ "phoneNumber varchar(14) not null,\n"
				+ "clinicID int not null,\n"
				+ "primary key(clinicID)\n"
				+ ")";
		stmt.executeUpdate(s);
	}

	/**
	 * Retrieve a Clinic object given its key.
	 *
	 * @param clinicID
	 * @return the Clinic object, or null if not found
	 */
	public Clinic find(int clinicID) {
		try {
			String qry = "select clinicName, address, phoneNumber from CLINIC where clinicID = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setInt(1, clinicID);
			ResultSet rs = pstmt.executeQuery();

			// return null if clinic doesn't exist
			if (!rs.next())
				return null;

			String cname = rs.getString("clinicName");
			String address = rs.getString("address");
			String phone = rs.getString("phoneNumber");
			rs.close();

			Clinic clinic = new Clinic(this, clinicID, cname, address, phone);

			return clinic;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error finding clinic", e);
		}
	}

	/**
    * Retrieve a Clinic object by search by cname
    *
    * @param cname
    * @return the Clinic object, or null if not found
    */
    public Clinic findByName(String cname) {
		try {
			String qry = "select clinicID, address, phoneNumber from CLINIC where clinicName = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setString(1, cname);
			ResultSet rs = pstmt.executeQuery();

			// return null if clinic doesn't exist
			if (!rs.next())
				return null;

			int cid = rs.getInt("clinicID");
			String address = rs.getString("address");
			String phone = rs.getString("phoneNumber");
			rs.close();

			Clinic clinic = new Clinic(this, cid, cname, address, phone);

			return clinic;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error finding clinic by name", e);
		}

    }

	/**
	 * Add a new Clinic with the given attributes.
	 *
	 * @param cid
	 * @param cname
	 * @param address
	 * @param phone
	 * @return the new Clinic object, or null if key already exists
	 */
	public Clinic insert(int cid, String cname, String address, String phone) {
		try {
			// make sure that the clinicID is currently unused
			if (find(cid) != null)
				return null;

			String cmd = "insert into CLINIC(clinicID, clinicName, address, phoneNumber) "
					+ "values(?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setInt(1, cid);
			pstmt.setString(2, cname);
			pstmt.setString(3, address);
			pstmt.setString(4, phone);
			pstmt.executeUpdate();

			Clinic clinic = new Clinic(this, cid, cname, address, phone);

			return clinic;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error inserting new clinic", e);
		}
	}

	/**
	 * Clinic Name was changed in the model object, so propagate the change to the
	 * database.
	 *
	 * @param cid
	 * @param cname
	 */

	public void changeName(int cid, String cname) {
		try {
			String cmd = "update CLINIC set clinicName = ? where clinicID = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setString(1, cname);
			pstmt.setInt(2, cid);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing Clinic name", e);
		}
	}

	/**
	 * Retrieve a Collection of all patients in the given clinic.
	 *
	 * @param cid
	 * @return the Collection
	 */
	public Collection<Patient> getPatients(int cid) {
		try {
			Collection<Patient> patients = new ArrayList<Patient>();
			String qry = "select ssn from PATIENT where pCID = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setInt(1, cid);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int ssn = rs.getInt("ssn");
				patients.add(dbm.findPatient(ssn));
			}
			rs.close();
			return patients;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error getting clinic's patients", e);
		}
	}

	/**
	 * Retrieve a Collection of all doctors in the given clinic.
	 *
	 * @param cid
	 * @return the Collection
	 */
	public Collection<Doctor> getDoctors(int cid) {
		try {
			Collection<Doctor> doctors = new ArrayList<Doctor>();
			String qry = "select mln from DOCTOR where dCID = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setInt(1, cid);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int mln = rs.getInt("mln");
				doctors.add(dbm.findDoctor(mln));
			}
			rs.close();
			return doctors;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error getting clinic's doctors", e);
		}
	}
	/**
	 * Clear all data from the Clinic table.
	 *
	 * @throws SQLException
	 */
	void clear() throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "delete from CLINIC";
		stmt.executeUpdate(s);
	}
}
