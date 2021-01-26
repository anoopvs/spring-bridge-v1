package org.springbridge.action;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class TestCounts {

	public static void main(String[] args) {
		//testCountsLoop();
		testActionMessages();
	}
	
	public static void testActionMessages() {
		ActionMessages messages= new ActionMessages();
		System.out.println(messages.size());
		messages.add("A", new ActionMessage("APPLE"));
		messages.add("A", new ActionMessage("AXE"));
		messages.add("B", new ActionMessage("BOY"));
		System.out.println(messages.size());
		messages.add("B", new ActionMessage("BOOK"));
		messages.add("C", new ActionMessage("CAT"));
		messages.add("C", new ActionMessage("COW"));
		messages.add("C", new ActionMessage("COMPUTER"));
		System.out.println(messages.size());
		System.out.println(messages);
		ActionMessages messagesOne= new ActionMessages();
		messagesOne.add("A", null);
		messagesOne.add("A", null);
		messages.add(messagesOne);
		System.out.println(messages.size());
		System.out.println(messages);
		
	}
	
	public static void testCountsLoop() {
		// TODO Auto-generated method stub
		Map<String, Collection<String>> messages= new LinkedHashMap<>();
		messages.computeIfAbsent("A", k -> new LinkedHashSet<String>(2)).add("APPLE");
		messages.computeIfAbsent("A", k -> new LinkedHashSet<String>(2)).add("AXE");
		messages.computeIfAbsent("B", k -> new LinkedHashSet<String>(2)).add("BOY");
		messages.computeIfAbsent("B", k -> new LinkedHashSet<String>(2)).add("BOOK");
		messages.computeIfAbsent("C", k -> new LinkedHashSet<String>(2)).add("CAT");
		long startTime=System.nanoTime();
		int count=0;
		for(Collection<String> collection:messages.values()) {
			count+=collection.size();
		}
		System.out.println(System.nanoTime()-startTime);
		System.out.println(count);
		
		 startTime=System.nanoTime();
//		 count=messages.values().stream().flatMap(Collection::stream)
//		.collect(Collectors.reducing(0, e -> 1, Integer::sum));
		 count=(int) messages.values().stream().flatMap(Collection::stream).count();
//		 count=messages.values().stream()
//					.collect(Collectors.reducing(0, Collection::size, Integer::sum));
		System.out.println(System.nanoTime()-startTime);
		System.out.println(count);
	}

}
