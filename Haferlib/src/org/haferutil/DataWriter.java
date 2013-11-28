package org.haferutil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import org.hafermath.expression.Expression;

public class DataWriter implements AutoCloseable {
	
	private PrintStream printStream;
	private StringBuilder stringBuilder;	// A reusable string builder.
	
	/**
	 * Constructor.
	 */
	public DataWriter() {
		stringBuilder = new StringBuilder();
	}
	
	/**
	 * Make a DataWriter that will write to the given stream.
	 * 
	 * @param stream The stream to write to.
	 */
	public DataWriter(OutputStream stream) {
		this();
		setOutputToStream(stream);
	}
	
	/**
	 * Make a DataWriter that will write to the given file.
	 * 
	 * @param file The file to write to.
	 * @throws FileNotFoundException If the given file is not found.
	 */
	public DataWriter(File file) throws FileNotFoundException {
		this();
		setOutputToFile(file);
	}
	
	/**
	 * Set the output to an OutputStream.
	 * 
	 * @param stream The stream to write to.
	 */
	public void setOutputToStream(OutputStream stream) {
		close();
		printStream = new PrintStream(stream);
	}
	
	/**
	 * Set the output to some file.
	 * 
	 * @param file The file to write to.
	 * @throws FileNotFoundException If the given file is not found.
	 */
	public void setOutputToFile(File file) throws FileNotFoundException {
		close();
		printStream = new PrintStream(file);
	}
	
	/**
	 * Close the current output.
	 */
	public void close() {
		if (printStream != null) {
			printStream.close();
			printStream = null;
		}
	}
	
	/**
	 * Format an object for writing.
	 * 
	 * @param data The object to format.
	 * @return A string representation of the object that will be written.
	 */
	public String format(Object data) {
		// Format arrays between curly braces.
		if (data instanceof Object[]) {
			stringBuilder.append("{ ");
			Object[] array = (Object[])data;
			for (int i = 0; i < array.length; i++) {
				stringBuilder.append(format(array[i]));
				stringBuilder.append(", ");
			}
			stringBuilder.append('}');
			String out = stringBuilder.toString();
			stringBuilder.delete(0, stringBuilder.length());
			return out;
		}
		// Format strings between quotes.
		else if (data instanceof String) {
			stringBuilder.append('\"');
			stringBuilder.append(data.toString());
			stringBuilder.append('\"');
			String out = stringBuilder.toString();
			stringBuilder.delete(0, stringBuilder.length());
			return out;
		}
		// Format maps between < and >.
		else if (data instanceof Map<?, ?>) {
			stringBuilder.append("< ");
			Map<?, ?> map = (Map<?, ?>)data;
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				stringBuilder.append(format(entry.getKey()));
				stringBuilder.append(" : ");
				stringBuilder.append(format(entry.getValue()));
				stringBuilder.append(",\n");
			}
			stringBuilder.append('>');
			String out = stringBuilder.toString();
			stringBuilder.delete(0, stringBuilder.length());
			return out;
		}
		// Format expressions between "#exp and ".
		else if (data instanceof Expression) {
			stringBuilder.append("\"#exp");
			stringBuilder.append(data.toString());
			stringBuilder.append('\"');
			String out = stringBuilder.toString();
			stringBuilder.delete(0, stringBuilder.length());
			return out;
		}
		// Otherwise, just use toString.
		else
			return data.toString();
	}
	
	/**
	 * Writes a map to the output in the order of the map's iterator.
	 * 
	 * @param data The map to write.
	 */
	public void write(Map<String, Object> data) {
		for (Map.Entry<String, Object> entry : data.entrySet()) {
			printStream.append(entry.getKey());
			printStream.append(" = ");
			printStream.append(format(entry.getValue()));
			printStream.append(";\n");
		}
	}

}
