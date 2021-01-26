package org.springbridge.action;

import org.springbridge.action.DynaActionForm;

public class StudentDynaActionForm extends DynaActionForm {
	private static final long serialVersionUID = 1L;
	private String firstName;
	private String lastName;
	private int age;
	private char grade;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public char getGrade() {
		return grade;
	}

	public void setGrade(char grade) {
		this.grade = grade;
	}

	@Override
	public String toString() {
		return String.format("StudentDynaActionForm [firstName=%s, lastName=%s, age=%s, grade=%s]", firstName, lastName,
				age, grade);
	}

}
