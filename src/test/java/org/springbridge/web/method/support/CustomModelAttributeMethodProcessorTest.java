package org.springbridge.web.method.support;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springbridge.action.ActionMapping;
import org.springbridge.action.Globals;
import org.springbridge.action.TestBean;
import org.springbridge.utilsconfigs.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

/**
 * Test fixture for {@link ServletModelAttributeMethodProcessor} specific tests.
 * Also see
 * org.springframework.web.method.annotation.support.ModelAttributeMethodProcessorTests
 *
 * @author Rossen Stoyanchev
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class CustomModelAttributeMethodProcessorTest {
	// Changed ServletModelAttributeMethodProcessor with custom Impl from
	// springbridge
	private CustomModelAttributeMethodProcessor processor;

	private WebDataBinderFactory binderFactory;

	private ModelAndViewContainer mavContainer;

	private MockHttpServletRequest request;

	private NativeWebRequest webRequest;

	private MethodParameter testBeanModelAttr;
	private MethodParameter testBeanWithoutStringConstructorModelAttr;
	private MethodParameter testBeanWithOptionalModelAttr;

	@Autowired
	private ApplicationContext ctx;

	@Before
	public void setup() throws Exception {
		processor = new CustomModelAttributeMethodProcessor(ctx);
		processor.setConvertNullToDefaultValue(true);
		processor.afterPropertiesSet();
		ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
		initializer.setConversionService(new DefaultConversionService());
		binderFactory = new ServletRequestDataBinderFactory(null, initializer);

		mavContainer = new ModelAndViewContainer();
		request = new MockHttpServletRequest();
		request.setRequestURI("/Home.do?tester=Anoop&module=CMAMP");
		webRequest = new ServletWebRequest(request);

		Method method = getClass().getDeclaredMethod("modelAttribute", TestBean.class,
				TestBeanWithoutStringConstructor.class, Optional.class);
		testBeanModelAttr = new MethodParameter(method, 0);
		testBeanWithoutStringConstructorModelAttr = new MethodParameter(method, 1);
		testBeanWithOptionalModelAttr = new MethodParameter(method, 2);
	}

	@SuppressWarnings("unused")
	private void invokeRestTestBean(@ModelAttribute("testBean") TestBean bean) {
	}

	@Test
	public void createAttributeUriTemplateVar() throws Exception {
		Map<String, String> uriTemplateVars = new HashMap<>();
		uriTemplateVars.put("testBean1", "Patty");
		request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVars);

		// Type conversion from "Patty" to TestBean via TestBean(String) constructor
		TestBean testBean = (TestBean) processor.resolveArgument(testBeanModelAttr, mavContainer, webRequest,
				binderFactory);

		assertEquals("Patty", testBean.getName());
	}

	@Test
	public void createAttributeUriTemplateVarCannotConvert() throws Exception {
		Map<String, String> uriTemplateVars = new HashMap<>();
		uriTemplateVars.put("testBean2", "Patty");
		request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVars);

		TestBeanWithoutStringConstructor testBean = (TestBeanWithoutStringConstructor) processor
				.resolveArgument(testBeanWithoutStringConstructorModelAttr, mavContainer, webRequest, binderFactory);

		assertNotNull(testBean);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createAttributeUriTemplateVarWithOptional() throws Exception {
		Map<String, String> uriTemplateVars = new HashMap<>();
		uriTemplateVars.put("testBean3", "Patty");
		request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVars);

		// Type conversion from "Patty" to TestBean via TestBean(String) constructor
		Optional<TestBean> testBean = (Optional<TestBean>) processor.resolveArgument(testBeanWithOptionalModelAttr,
				mavContainer, webRequest, binderFactory);

		assertEquals("Patty", testBean.get().getName());
	}

	@Test
	public void createAttributeRequestParameter() throws Exception {
		request.addParameter("testBean1", "Patty");
		request.addParameter("intArray", "1");
		request.addParameter("intArray", "");
		request.addParameter("intArray", "3");
		// Type conversion from "Patty" to TestBean via TestBean(String) constructor
		TestBean testBean = (TestBean) processor.resolveArgument(testBeanModelAttr, mavContainer, webRequest,
				binderFactory);

		assertEquals("Patty", testBean.getName());
		assertArrayEquals(new int[] { 1, 0, 2 }, new int[] { 1, 0, 2 });
	}

	@Test
	public void testResetMethodInvocation() throws Exception {
		request.addParameter("name", "admin");
		request.addParameter("intArray", "1");
		request.addParameter("intArray", "");
		request.addParameter("intArray", "3");
		request.setAttribute(Globals.MAPPING_KEY, ctx.getBean("homeMapping",ActionMapping.class));
		webRequest = new ServletWebRequest(request);
		Method method = getClass().getDeclaredMethod("invokeRestTestBean", TestBean.class);
		MethodParameter invokeRestTestBeanModelAttr = new MethodParameter(method, 0);
		TestBean testBean = spy(new TestBean());
		mavContainer.addAttribute("testBean", testBean);
		ServletRequestDataBinder dataBinder = new ServletRequestDataBinder(testBean, "testBean");
		testBean = (TestBean) processor.resolveArgument(invokeRestTestBeanModelAttr, mavContainer, webRequest,
				(r, o, n) -> dataBinder);
		assertEquals("admin", testBean.getName());
		// Test reset method is getting invoked before data population.
		verify(testBean, times(1)).reset(any(ActionMapping.class), any(HttpServletRequest.class));
	}

	@Test
	public void createAttributeRequestParameterCannotConvert() throws Exception {
		request.addParameter("testBean2", "Patty");

		TestBeanWithoutStringConstructor testBean = (TestBeanWithoutStringConstructor) processor
				.resolveArgument(testBeanWithoutStringConstructorModelAttr, mavContainer, webRequest, binderFactory);

		assertNotNull(testBean);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createAttributeRequestParameterWithOptional() throws Exception {
		request.addParameter("testBean3", "Patty");

		Optional<TestBean> testBean = (Optional<TestBean>) processor.resolveArgument(testBeanWithOptionalModelAttr,
				mavContainer, webRequest, binderFactory);

		assertEquals("Patty", testBean.get().getName());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void attributesAsNullValues() throws Exception {
		request.addParameter("name", "Patty");

		mavContainer.getModel().put("testBean1", null);
		mavContainer.getModel().put("testBean2", null);
		mavContainer.getModel().put("testBean3", null);

		assertNull(processor.resolveArgument(testBeanModelAttr, mavContainer, webRequest, binderFactory));

		assertNull(processor.resolveArgument(testBeanWithoutStringConstructorModelAttr, mavContainer, webRequest,
				binderFactory));

		Optional<TestBean> testBean = (Optional<TestBean>) processor.resolveArgument(testBeanWithOptionalModelAttr,
				mavContainer, webRequest, binderFactory);
		assertFalse(testBean.isPresent());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void attributesAsOptionalEmpty() throws Exception {
		request.addParameter("name", "Patty");

		mavContainer.getModel().put("testBean1", Optional.empty());
		mavContainer.getModel().put("testBean2", Optional.empty());
		mavContainer.getModel().put("testBean3", Optional.empty());

		assertNull(processor.resolveArgument(testBeanModelAttr, mavContainer, webRequest, binderFactory));

		assertNull(processor.resolveArgument(testBeanWithoutStringConstructorModelAttr, mavContainer, webRequest,
				binderFactory));

		Optional<TestBean> testBean = (Optional<TestBean>) processor.resolveArgument(testBeanWithOptionalModelAttr,
				mavContainer, webRequest, binderFactory);
		assertFalse(testBean.isPresent());
	}

	@SuppressWarnings("unused")
	private void modelAttribute(@ModelAttribute("testBean1") TestBean testBean1,
			@ModelAttribute("testBean2") TestBeanWithoutStringConstructor testBean2,
			@ModelAttribute("testBean3") Optional<TestBean> testBean3) {
	}

	@SuppressWarnings("unused")
	private static class TestBeanWithoutStringConstructor {

		public TestBeanWithoutStringConstructor() {
		}

		public TestBeanWithoutStringConstructor(int i) {
		}
	}

}
