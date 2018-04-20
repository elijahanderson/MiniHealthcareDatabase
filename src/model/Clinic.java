package model;

import java.util.Collection;
import dao.ClinicDAO;

/**
 * Model object for a row in the Clinic table.
 * Accesses the underlying database through a ClinicDAO.
 * Based on code by bhoward
 *
 * @author Eli Anderson
 */
public class Clinic {
	private ClinicDAO dao;
	private int clinicID;
	private String clinicName;
    private String address;
	private String phoneNumber;
	private Collection<Patient> patients;
	private Collection<Doctor> doctors;

	public Clinic(ClinicDAO dao, int clinicID, String clinicName, String address, String phoneNumber) {
		this.dao = dao;
		this.clinicID = clinicID;
		this.clinicName = clinicName;
		this.address = address;
		this.phoneNumber = phoneNumber;
	}

	public String toString() {
		return clinicName + ":\n" + address + "\n" + phoneNumber;
	}

	public String getName() {
		return clinicName;
	}

    public int getID() {
        return clinicID;
    }

	public void setName(String cname) {
		this.clinicName = cname;
		dao.setName(clinicID, clinicName);
	}

	public String getAddress() {
		return address;
	}

	public Collection<String> getClinicNames() {
		return dao.getClinicNames();
	}

	public String getPhoneNum() {
		return phoneNumber;
	}

	// retrieve all the patients in a given clinic
	public Collection<Patient> getPatients() {
		if (patients == null)
			patients = dao.getPatients(clinicID);
		return patients;
	}

	// retrieve all the doctors in a given clinic
	public Collection<Doctor> getDoctors() {
		if (doctors == null)
			doctors = dao.getDoctors(clinicID);
		return doctors;
	}
}
