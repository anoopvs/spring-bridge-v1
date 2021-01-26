package org.springbridge.support.propertyeditors;

import org.springbridge.action.Globals;

public class TestPattern {

	public static void main(String[] args) {
		System.out.println(Globals.MESSAGE_FORMAT_PATTERN.matcher("/jsp/Wildcard/{11}.jsp").matches());
		System.out.println(Globals.MESSAGE_FORMAT_PATTERN.matcher("/jsp/Wildcard/avs{1}.jsp").matches());
		System.out.println(Globals.MESSAGE_FORMAT_PATTERN.matcher("/jsp/Wildcard/errorPage.jsp").matches());
		String handlerName = new TestPattern().getClass().getSimpleName() + "('%s') ";
		System.out.println(String.format(handlerName, "124"));
	}

}
