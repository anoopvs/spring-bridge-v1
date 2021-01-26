package org.springbridge.action;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springbridge.action.ActionMessage;
import org.springbridge.action.ActionMessages;

public class ActionMessagesTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSize() {
		// fail("Not yet implemented");
		ActionMessages am = new ActionMessages();
		am.add("123", new ActionMessage("CDE"));
		System.out.println(am.size());
		System.out.println(am.toString());
	}

}
