package org.springbridge.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDynaActionForm {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SampleDynaActionForm sdf=new SampleDynaActionForm();
		System.out.println(sdf.getStringList());
		sdf.set("stringList",1,"10");
		System.out.println(sdf.getStringList());
		System.out.println(sdf.getIntMap());
		sdf.set("intMap","10",10);
		System.out.println(sdf.getIntMap());
		sdf.remove("intMap","10");
		System.out.println(sdf.getIntMap());
	}

	static class SampleDynaActionForm extends DynaActionForm {
		
		private static final long serialVersionUID = 1L;
		List<Integer> intList = new ArrayList<>();
		List<String> stringList = new ArrayList<>();
		Map<String, Integer> intMap = new HashMap<>();
		Map<String, String> stringMap = new HashMap<>();
		
		public SampleDynaActionForm() {
			intList.add(1);intList.add(21);intList.add(31);
			stringList.add("one");stringList.add("two");stringList.add("three");stringList.add("four");
			intMap.put("1", 1);
			intMap.put("2", 2);
			intMap.put("3", 3);
			intMap.put("10", 4);
		}
		public List<Integer> getIntList() {
			return intList;
		}

		public void setIntList(List<Integer> intList) {
			this.intList = intList;
		}

		public List<String> getStringList() {
			return stringList;
		}

		public void setStringList(List<String> stringList) {
			this.stringList = stringList;
		}

		public Map<String, Integer> getIntMap() {
			return intMap;
		}

		public void setIntMap(Map<String, Integer> intMap) {
			this.intMap = intMap;
		}

		public Map<String, String> getStringMap() {
			return stringMap;
		}

		public void setStringMap(Map<String, String> stringMap) {
			this.stringMap = stringMap;
		}

	}

}
