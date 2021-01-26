package org.springbridge.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TestStudentSerialization {

	public static void main(String[] args) {
		StudentDynaActionForm sdaf = new StudentDynaActionForm();
		sdaf.set("firstName", "Anoop");
		sdaf.set("lastName", "V S");
		sdaf.set("age", "12");
		sdaf.set("grade", "c");
		System.out.println(sdaf);
		File file = new File("H:/fileDB/sdaf.txt");
		try (FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(fos);) {
			System.out.println("firstName:: " + sdaf.get("firstName"));
			System.out.println("grade:: " + sdaf.get("grade"));
			oos.writeObject(sdaf);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Saved Student to file!!!" + sdaf.hashCode() + "#" + sdaf);
		try (FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis);) {
			sdaf = (StudentDynaActionForm) ois.readObject();
			System.out.println("Saved Student from file!!!" + sdaf.hashCode() + "#" + sdaf);
			System.out.println("firstName:: " + sdaf.get("firstName"));
			System.out.println("grade:: " + sdaf.get("grade"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
