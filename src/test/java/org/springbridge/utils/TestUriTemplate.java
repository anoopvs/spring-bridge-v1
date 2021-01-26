package org.springbridge.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.util.UriTemplate;

public class TestUriTemplate {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UriTemplate template = new UriTemplate("https://example.com/hotels/{hotel}/bookings/{booking}");
		Map<String, String> uriVariables = new HashMap<String, String>();
		uriVariables.put("booking", "42");
		uriVariables.put("hotel", "Rest & Relax");
		System.out.println(template.expand(uriVariables));
	}

}
