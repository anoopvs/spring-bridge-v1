package org.springbridge.support.propertyeditors;

import static org.junit.Assert.assertEquals;

import java.util.function.IntSupplier;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springbridge.test.common.ValidatorForm;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.ServletWebRequest;

public class StringToNumberEditorTest {
	private WebRequestDataBinder dataBinder;
	private ValidatorForm form;
	private IntSupplier defaultValueGenerator = () -> Integer.valueOf(0);
	
	@Before
	public void setUp() throws Exception {
		form = new ValidatorForm();
		dataBinder= new WebRequestDataBinder(form, "validatorForm");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected=BindException.class)
	public void testFailure() throws BindException {
		dataBinder.bind(new ServletWebRequest(getHttpServletRequest()));
		dataBinder.close();
	}
	@Test
	public void testSuccess() throws BindException  {
		registerCustomEditors(dataBinder);
		dataBinder.bind(new ServletWebRequest(getHttpServletRequest()));
		dataBinder.close();
		System.out.println(form);
		System.out.println(dataBinder.getBindingResult());
		assertEquals("Values are not equal", 123,
				form.getShortValue());
	}
	
	private HttpServletRequest getHttpServletRequest() {
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.addParameter("shortValue", "123");
		httpRequest.addParameter("byteValue", "");
		httpRequest.addParameter("integerValue", "");
		httpRequest.addParameter("floatValue", "");
		httpRequest.addParameter("longValue", "");
		httpRequest.addParameter("doubleValue", "");
		httpRequest.addParameter("intArray", "1");
		httpRequest.addParameter("intArray", "");
		httpRequest.addParameter("intArray", "3");
		return httpRequest;
	}
	
	private void registerCustomEditors(DataBinder dataBinder) {
		dataBinder.registerCustomEditor(byte.class,
				new StringToNumberEditor(Byte.class, getDefaultValueGenerator()));
		dataBinder.registerCustomEditor(short.class,
				new StringToNumberEditor(Short.class, getDefaultValueGenerator()));
		dataBinder.registerCustomEditor(int.class,
				new StringToNumberEditor(Integer.class, getDefaultValueGenerator()));
		dataBinder.registerCustomEditor(long.class,
				new StringToNumberEditor(Long.class, getDefaultValueGenerator()));
		dataBinder.registerCustomEditor(float.class,
				new StringToNumberEditor(Float.class, getDefaultValueGenerator()));
		dataBinder.registerCustomEditor(double.class,
				new StringToNumberEditor(Double.class, getDefaultValueGenerator()));
	}

	private IntSupplier getDefaultValueGenerator() {
		return defaultValueGenerator;
	}
}
