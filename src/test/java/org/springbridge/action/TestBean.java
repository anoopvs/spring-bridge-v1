package org.springbridge.action;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class TestBean extends ActionForm{

	private static final long serialVersionUID = 1L;

	private String name;

	private List<?> list;

	private Object objRef;
	
	int[] intArray;
	
	public TestBean() {
	}
	
	public TestBean(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}

	public Object getObjRef() {
		return objRef;
	}

	public void setObjRef(Object objRef) {
		this.objRef = objRef;
	}

	public int[] getIntArray() {
		return intArray;
	}

	public void setIntArray(int[] intArray) {
		this.intArray = intArray;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		return super.validate(mapping, request);
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
	}

	@Override
	public String toString() {
		return "TestBean [name=" + name + ", list=" + list + ", objRef=" + objRef + ", intArray="
				+ Arrays.toString(intArray) + "]";
	}
	
}
