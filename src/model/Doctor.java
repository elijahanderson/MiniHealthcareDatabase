package model;

import java.util.Collection;

import dao.DoctorDAO;

/**
 * Model object for a row in the Doctor table.
 * Accesses the underlying database through a DoctorDAO.
 * Based on code by bhoward
 *
 * @author Eli Anderson
 */
public class Doctor {
	private DoctorDAO dao;
	private int mln;
	private String dname;
	private String specialty;
    private String phone;
    private String email;
	private Clinic clinic;
	private Collection<Patient> patients;

	public Doctor(DoctorDAO dao, int mln, String dname, String specialty, String phone, String email, Clinic clinic) {
		this.dao = dao;
		this.mln = mln;
		this.dname = dname;
		this.specialty = specialty;
        this.phone = phone;
        this.email = email;
		this.clinic = clinic;
	}

	public String toString() {
		return dname + "\n" + specialty + "\nEmployer: " + clinic.getName();
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
		dao.changeSpecialty(mln, specialty);
	}

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
		dao.changeClinic(mln, clinic);
	}

	public int getMLN() {
		return mln;
	}

	public String getDName() {
		return dname;
	}

	public String getPhoneNum() {
        return phone;
    }

	public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        dao.changeEmail(mln, email);
    }

	// retrieve all the patients in a given clinic
	public Collection<Patient> getPatients() {
		if (patients == null)
			patients = dao.getPatients(mln);
		return patients;
	}
}
