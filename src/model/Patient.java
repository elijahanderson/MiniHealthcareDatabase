package model;

import java.util.Collection;

import dao.PatientDAO;

/**
 * Model object for a row in the Patient table.
 * Accesses the underlying database through a PatientDAO.
 * Based on code by bhoward
 *
 * @author Eli Anderson
 */
public class Patient {
	private PatientDAO dao;
	private int ssn;
	private String pname;
    private String email;
    private String phone;
	private String birthDate; // in yyyy-mm-dd format
	private Clinic clinic;
	private Doctor doctor;

	public Patient(PatientDAO dao, int ssn, String pname, String email, String phone, String birthDate, Clinic clinic, Doctor doctor) {
		this.dao = dao;
		this.ssn = ssn;
		this.pname = pname;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
		this.clinic = clinic;
		this.doctor = doctor;
	}

	public String toString() {
		return pname + "\n" + phone + "\n" + email;
	}

	public int getSSN() {
		return ssn;
	}

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
		dao.changeClinic(ssn, clinic);
	}

	public Doctor getDoctor() { return doctor; }

	public void setDoctor(Doctor doc) {
		this.doctor = doc;
		dao.changeDoctor(ssn, doc);
	}

	public String getPName() {
		return pname;
	}

	public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        dao.changeEmail(ssn, email);
    }

    public String getPhoneNum() {
        return phone;
    }

    public void setPhoneNum(String phone) {
		this.phone = phone;
		dao.changePhoneNum(ssn, phone);
	}

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String bDate) {
		this.birthDate = bDate;
		dao.changeBirthDate(ssn, birthDate);
	}
}
