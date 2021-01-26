package org.springbridge.test.common;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.springbridge.utils.WildcardPathMatcher;
import org.springframework.util.AntPathMatcher;

public class TestAntPathMatcher {

	public static void main(String[] args) {
		AntPathMatcher pathMatcher = new AntPathMatcher();
		final String uriTemplatePath="https://example.com/hotels/{hotel}/bookings/{booking}";
		System.out.println(pathMatcher.match(uriTemplatePath, "https://example.com/hotels/taj/bookings/101"));
		System.out.println(pathMatcher.match("/login.do", "/login.do"));
		System.out.println(pathMatcher.match("/Prepare*.do", "/PrepareAction.do"));
		System.out.println(pathMatcher.match("/Source*.do", "/SourceAction.do"));
		System.out.println(pathMatcher.match("/Wildcard-Not.do", "/Wildcard-Not.do"));
		System.out.println(pathMatcher.extractUriTemplateVariables("/Prepare{val:\\w+}.do", "/PrepareAction.do"));
		//System.out.println(pathMatcher.extractUriTemplateVariables("path/{first:\\w+}/another/{second:\\W+}/file", "path/to/another/location/of/file"));
		//MessageFormat format = new MessageFormat("Edit{1}Action");
		System.out.println(pathMatcher.extractUriTemplateVariables("/Prepare*.do", "/PrepareAction.do"));
		WildcardPathMatcher wild = new WildcardPathMatcher();
		System.out.println("****************START***********************");
		HashMap<String, String> matchedPatterns = new HashMap<>();
		int[] pattern = wild.compilePattern("/Prepare*.do");
		System.out.println(wild.match(matchedPatterns, "/PrepareAction.do", pattern));
		System.out.println(matchedPatterns);
		matchedPatterns.clear();
		pattern = wild.compilePattern("/**/launchAdmin.do");
		System.out.println(wild.match(matchedPatterns, "/CSR/launchAdmin.do", pattern));
		System.out.println(matchedPatterns);
		matchedPatterns.clear();
		System.out.println(wild.match(matchedPatterns, "/ADMIN/launchAdmin.do", pattern));
		System.out.println(matchedPatterns);
		System.out.println("****************END***********************");
		System.out.println(new MessageFormat("/jsp/Wildcard/{1}.jsp").format(new String[] { "FIRST", "SECOND" }));
		Pattern VARIABLE_PATTERN = Pattern.compile("(.*\\{\\d{1}\\}.*)*");
		System.out.println(VARIABLE_PATTERN.matcher("Edit{1}Action").matches());
		System.out.println(VARIABLE_PATTERN.matcher("Edit{9}Action").matches());
		System.out.println(VARIABLE_PATTERN.matcher("/jsp/{2A}/Source{11G}.jsp").matches());
		System.out.println(VARIABLE_PATTERN.matcher("/Wildcard-Not.do").matches());
		System.out.println(VARIABLE_PATTERN.matcher("{1}/jsp/{2}/Source{1}.jsp").matches());
		System.out.println(new MessageFormat("{1}/jsp/{2}/Source{1}.jsp").format(new String[] { "ZERO", "ONE", "TWO" }));
	}

}
