package org.springbridge.action;

import org.springbridge.config.validation.impl.AbstractActionMappingValidator;
import org.springframework.mock.web.MockHttpServletRequest;

public class TestRequestURI {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/Home.do?tester=Anoop&module=CMAMP");
		System.out.println(request.getRequestURI());
		System.out.println(AbstractActionMappingValidator.VALIDATE_MAPPING_SYSTEM_PROPERTY.replace(".", "_"));
	}

}
