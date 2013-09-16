package org.haferlib.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import org.haferlib.util.expression.Expression;

public class DataWriter {
	
	private PrintStream printStream;
	private StringBuilder stringBuilder;	// A reusable string builder.
	
	public DataWriter() {
		stringBuilder = new StringBuilder();
	}
	
	public void setOutputToStream(OutputStream stream) {
		closeOutput();
		printStream = new PrintStream(stream);
	}
	
	public void setOutputToFile(File file) throws FileNotFoundException {
		closeOutput();
		printStream = new PrintStream(file);
	}
	
	public void closeOutput() {
		if (printStream != null) {
			printStream.close();
			printStream = null;
		}
	}
	
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
	
	public void write(Map<String, Object> data) {
		for (Map.Entry<String, Object> entry : data.entrySet()) {
			printStream.append(entry.getKey());
			printStream.append(" = ");
			printStream.append(format(entry.getValue()));
			printStream.append(";\n");
		}
	}

}
