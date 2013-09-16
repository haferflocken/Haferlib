package org.haferlib.test;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import org.haferlib.util.DataReader;
import org.haferlib.util.DataWriter;

public class ReadWriteTest {
	
	public static void main(String[] args) {
		try (Scanner scan = new Scanner(System.in)) {
			// Get a test file.
			System.out.print("Enter a file path: ");
			String filePath = scan.nextLine();
			System.out.println();
			
			// Read the file.
			DataReader reader = new DataReader();
			Map<String, Object> data = reader.readFile(filePath);
			
			// Ask for a destination.
			System.out.print("Enter a destination: ");
			String destPath = scan.nextLine();
			System.out.println();
			
			// Write the file.
			DataWriter writer = new DataWriter();
			writer.setOutputToFile(new File(destPath));
			writer.write(data);
			writer.closeOutput();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
