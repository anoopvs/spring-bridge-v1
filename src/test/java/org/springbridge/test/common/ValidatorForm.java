package org.springbridge.test.common;

import java.util.Arrays;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springbridge.action.DynaValidatorForm;

public class ValidatorForm extends DynaValidatorForm {
	private static final long serialVersionUID = 1L;

	byte byteValue;
	String creditCard;
	String date;
	double doubleValue;
	String email;
	float floatValue;
	int integerValue;
	int[] intArray;
	long longValue;
	String mask;
	int min;
	int max;
	int range;
	String required;
	short shortValue;
	String password;
	String password2;
	public byte getByteValue() {
		return byteValue;
	}
	public void setByteValue(byte byteValue) {
		this.byteValue = byteValue;
	}
	public String getCreditCard() {
		return creditCard;
	}
	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getDoubleValue() {
		return doubleValue;
	}
	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}
	
	@NotEmpty
	@javax.validation.constraints.NotBlank
	@Size(min=12,max=15)
	@Email(regexp=".+@.+\\.in")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public float getFloatValue() {
		return floatValue;
	}
	public void setFloatValue(float floatValue) {
		this.floatValue = floatValue;
	}
	public int getIntegerValue() {
		return integerValue;
	}
	public void setIntegerValue(int integerValue) {
		this.integerValue = integerValue;
	}
	public long getLongValue() {
		return longValue;
	}
	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}
	public String getMask() {
		return mask;
	}
	public void setMask(String mask) {
		this.mask = mask;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	public String getRequired() {
		return required;
	}
	public void setRequired(String required) {
		this.required = required;
	}
	public short getShortValue() {
		return shortValue;
	}
	public void setShortValue(short shortValue) {
		this.shortValue = shortValue;
	}
	@Pattern(regexp=".+@.+\\.[a-z]+")
	@Size(min = 5, max = 20, message="Password size must be between {min} and {max}")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword2() {
		return password2;
	}
	public void setPassword2(String password2) {
		this.password2 = password2;
	}
	
	public int[] getIntArray() {
		return intArray;
	}
	public void setIntArray(int[] intArray) {
		this.intArray = intArray;
	}
	@Override
	public String toString() {
		return "ValidatorForm [byteValue=" + byteValue + ", creditCard=" + creditCard + ", date=" + date
				+ ", doubleValue=" + doubleValue + ", email=" + email + ", floatValue=" + floatValue + ", integerValue="
				+ integerValue + ", intArray=" + Arrays.toString(intArray) + ", longValue=" + longValue + ", mask="
				+ mask + ", min=" + min + ", max=" + max + ", range=" + range + ", required=" + required
				+ ", shortValue=" + shortValue + ", password=" + password + ", password2=" + password2 + "]";
	}
	
}
