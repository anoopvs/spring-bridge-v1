package org.springbridge.action;

import java.util.Collections;
import java.util.Map;

public class TestSingletonMap {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String,String> singletonMap=Collections.singletonMap("KEY","VALUE");
		singletonMap.put("KEY1","VALUE1");
	}

}
