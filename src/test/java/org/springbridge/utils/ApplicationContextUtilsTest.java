package org.springbridge.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springbridge.utils.ApplicationContextUtils;
import org.springbridge.utilsconfigs.TestConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class ApplicationContextUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertNotNull(ApplicationContextUtils.getApplicationContext());
		assertNotNull(ApplicationContextUtils.getMessageResources());
		assertNotNull(ApplicationContextUtils.getMessage("errors.cancel"));
		assertNotNull(ApplicationContextUtils.getMessageResources().resolveStringValue("AVS"));
		assertEquals("Values are not equal", "AVS",
				ApplicationContextUtils.getMessageResources().resolveStringValue("AVS"));
		assertEquals("Values are not equal", "Operation cancelled.",
				ApplicationContextUtils.getMessage("errors.cancel"));
		System.out.println(ApplicationContextUtils.getMessageResources().resolveStringValue("User home is ${user.home}"));
	}

//	@Test(expected = ExceptionInInitializerError.class)
//	public void testMessageResources() {
//		assertNotNull(ApplicationContextUtils.getMessageResources());
//	}

}
