//Reads in data in Haferlib Notation.

package org.haferlib.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class DataReader {

	private static final String TRUE = "true";
	private static final String FALSE = "false";

	private Matcher intMatcher;
	private Matcher floatMatcher;
	private Pattern tokenizerPattern;
	private Matcher fieldNameMatcher;
	private Matcher equalsMatcher;

	//The constructor just sets up the regex patterns and matchers.
	public DataReader() {
		intMatcher = Pattern.compile("-?\\d+").matcher("");
		floatMatcher = Pattern.compile("-?\\d+\\.\\d+").matcher("");
		tokenizerPattern = Pattern.compile("\\s*;\\s*");
		fieldNameMatcher = Pattern.compile("[a-zA-Z]\\w*").matcher("");
		equalsMatcher = Pattern.compile("\\s*=").matcher("");
	}

	//Read a file located at pathString.
	public TreeMap<String, Object> readFile(String pathString) throws IOException {
		return readFile(FileSystems.getDefault().getPath(pathString));
	}

	//Read a file located at source.
	public TreeMap<String, Object> readFile(Path source) throws IOException {
		//Get a scanner for the file. Make sure it closes if it fails.
		Scanner scan = null;
		try {
			//Read the entire file in as a string.
			scan = new Scanner(source);
			StringBuilder contents = new StringBuilder();
			while (scan.hasNextLine()) {
				contents.append(scan.nextLine());
			}
			scan.close();

			//Read the string.
			return readString(contents.toString());
		}
		catch (IOException e) {
			throw e;
		}
	}

	//Read a string.
	public TreeMap<String, Object> readString(String rawData) {
		//Get rid of trailing and leading whitespace.
		rawData = rawData.trim();

		//Break the raw data into tokens and trim them.
		//TODO: Redo this without regex so that it ignores escaped semicolons.
		String[] tokens = tokenizerPattern.split(rawData);
		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = tokens[i].trim();
		}

		//Look through the tokens and return the output TreeMap.
		return buildMapFromTokens(tokens);
	}

	//Builds a TreeMap of data from an array of tokens.
	//Tokens must be a field name followed by an = followed by a value. They must be trimmed.
	public TreeMap<String, Object> buildMapFromTokens(String[] tokens) {
		//Make the output TreeMap.
		TreeMap<String, Object> out = new TreeMap<>();

		//Look through the tokens for syntax errors.
		for (int i = 0; i < tokens.length; i++) {
			//Get the field name and the value.
			fieldNameMatcher.reset(tokens[i]);
			if (!fieldNameMatcher.find() || fieldNameMatcher.start() != 0) {
				System.out.println("SYNTAX ERROR: Invaid field name in: " + tokens[i]);
				continue;
			}
			String fieldName = fieldNameMatcher.group();

			//Make sure the field name is followed by an =.
			equalsMatcher.reset(tokens[i]);
			if (!equalsMatcher.find() || equalsMatcher.start() != fieldNameMatcher.end()) {
				System.out.println("SYNTAX ERROR: Failed to find = after field name.");
				continue;
			}

			//Get the value, which is just everything after the =.
			int valueStart = equalsMatcher.end();
			if (valueStart >= tokens[i].length()) {
				System.out.println("SYNTAX ERROR: Expected value after = in: " + tokens[i]);
				continue;
			}
			String rawValue = tokens[i].substring(valueStart);

			//Parse the value. If it isn't null, add it to the output.
			Object value = parseValue(rawValue);
			if (value != null) {
				out.put(fieldName, value);
			}
		}

		//Return the output.
		return out;
	}

	//For finding the last character of a string block. Returns -1 if it doesn't end.
	private int indexOfStringClose(String string, int startIndex) {
		for (int q = startIndex + 1; q < string.length(); q++) {
			if (string.charAt(q) == '\\')
				q++;
			else if (string.charAt(q) == '"')
				return q;
		}
		return -1;
	}

	//For finding the last character of a block defined by an open and close character. Returns -1 if it doesn't end.
	private int indexOfBlockClose(String string, int startIndex, char openBlock, char closeBlock) {
		int numOpen = 1;
		int q;
		for (q = startIndex + 1; q < string.length() && numOpen > 0; q++) {
			if (string.charAt(q) == openBlock)
				numOpen++;
			else if (string.charAt(q) == closeBlock)
				numOpen--;
		}
		if (numOpen == 0)
			return q - 1;
		else
			return -1;
	}

	//Parses a value string into a proper value object. Prints out any syntax errors it finds.
	//Returns null if it encounters a syntax error.
	private Object parseValue(String value) {
		//Trim the value.
		value = value.trim();

		//Is it a boolean?
		if (value.equals(TRUE) || value.equals(FALSE))
			return Boolean.parseBoolean(value);

		//Is it an integer?
		intMatcher.reset(value);
		if (intMatcher.matches())
			return Integer.parseInt(value);

		//Is it a float?
		floatMatcher.reset(value);
		if (floatMatcher.matches())
			return Float.parseFloat(value);

		//Only numbers can have a length less than 2. If the length is less than 2, return true.
		if (value.length() < 2) {
			System.out.println("SYNTAX ERROR: Non-number value too short to be valid: " + value);
			return null;
		}

		//Is it a string?
		if (value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"') {
			int stringEnd = indexOfStringClose(value, 0);
			if (stringEnd != value.length() - 1) {
				System.out.println("SYNTAX ERROR: Unexpected end of string at " + stringEnd + " in " + value);
				return null;
			}
			//Make and return the string if it's a valid string.
			return value.substring(1, value.length() - 1);
		}

		//Is it an array?
		//Arrays start and end with {} and contain values separated by commas.
		if (value.charAt(0) == '{' && value.charAt(value.length() - 1) == '}') {
			//Look through the array, parsing each value as it is seen.
			ArrayList<Object> arrayBuilder = new ArrayList<>();
			int subStart = 1;
			for (int i = 1; i < value.length() - 1; i++) {
				//If we have found the next subvalue to parse, parse it.
				if (value.charAt(i) == ',') {
					String sub = value.substring(subStart, i);
					Object subValue = parseValue(sub);
					//If we fail to parse the value, the array fails to be made as well.
					if (subValue == null)
						return null;
					//Add the value to arrayBuilder.
					arrayBuilder.add(subValue);
					//Get ready to parse the next value.
					subStart = i + 1;
				}
				//We need to make sure we skip over commas inside of string, array and map values.
				else if (value.charAt(i) == '"') {
					int endOfBlock = indexOfStringClose(value, i);
					if (endOfBlock == -1) {
						System.out.println("SYNTAX ERROR: Unclosed string token in: " + value);
						return null;
					}
					i = endOfBlock;
				}
				else if (value.charAt(i) == '{') {
					int endOfBlock = indexOfBlockClose(value, i, '{', '}');
					if (endOfBlock == -1) {
						System.out.println("SYNTAX ERROR: Unclosed array token in: " + value);
						return null;
					}
					i = endOfBlock;
				}
				else if (value.charAt(i) == '<') {
					int endOfBlock = indexOfBlockClose(value, i, '<', '>');
					if (endOfBlock == -1) {
						System.out.println("SYNTAX ERROR: Unclosed map token in: " + value);
						return null;
					}
					i = endOfBlock;
				}
			}
			//Because the last element doesn't necesarily have a comma after it, parse the value
			//of the last element which may not have been grabbed by the loop.
			String lastElementString = value.substring(subStart, value.length() - 1).trim();
			if (lastElementString.length() > 0) {
				Object lastElement = parseValue(lastElementString);
				if (lastElement == null)
					return null;
				arrayBuilder.add(lastElement);
			}
			//If we got through all that without returning, we have a valid array!
			return arrayBuilder.toArray();
		}

		//Is it a map?
		//Maps start and end with <> and contain key-value pairs of the format key : value, .
		if (value.charAt(0) == '<' && value.charAt(value.length() - 1) == '>') {
			//Look through the map, parsing each pair as it is seen.
			Map<Object, Object> map = new LinkedHashMap<>();
			int subStart = 1;
			for (int i = 1; i < value.length() - 1; i++) {
				//If we have found the next pair to parse, parse it.
				if (value.charAt(i) == ',') {
					//Get the pair string
					String pairString = value.substring(subStart, i);
					//Parse the pair.
					Object[] pair = parsePair(pairString);
					if (pair == null)
						return null;
					//Add the pair to the map.
					map.put(pair[0], pair[1]);
					//Get ready to parse the next value.
					subStart = i + 1;
				}
				//We need to make sure we skip over commas inside of string, array and map values.
				else if (value.charAt(i) == '"') {
					int endOfBlock = indexOfStringClose(value, i);
					if (endOfBlock == -1) {
						System.out.println("SYNTAX ERROR: Unclosed string token in: " + value);
						return null;
					}
					i = endOfBlock;
				}
				else if (value.charAt(i) == '{') {
					int endOfBlock = indexOfBlockClose(value, i, '{', '}');
					if (endOfBlock == -1) {
						System.out.println("SYNTAX ERROR: Unclosed array token in: " + value);
						return null;
					}
					i = endOfBlock;
				}
				else if (value.charAt(i) == '<') {
					int endOfBlock = indexOfBlockClose(value, i, '<', '>');
					if (endOfBlock == -1) {
						System.out.println("SYNTAX ERROR: Unclosed map token in: " + value);
						return null;
					}
					i = endOfBlock;
				}
			}
			//Because the last element doesn't necessarily have a comma after it, parse the value
			//of the last element which may not have been grabbed by the loop.
			String lastPairString = value.substring(subStart, value.length() - 1).trim();
			if (lastPairString.length() > 0) {
				//Parse the pair.
				Object[] pair = parsePair(lastPairString);
				if (pair == null)
					return null;

				//Add the pair to the map.
				map.put(pair[0], pair[1]);
			}
			//If we got through all that without returning, we have a valid map!
			return map;
		}

		//If we get here, it isn't anything, so return null.
		System.out.println("SYNTAX ERROR: Unrecognized value: " + value);
		return null;
	}

	//Take a raw pair string and make it into a key and a value.
	//Returns an array with the first element being the key and the second element being the value.
	private Object[] parsePair(String pairString) {
		//First we need to find the : that separates the key and value.
		int colonIndex = -1;
		for (int q = 0; q < pairString.length(); q++) {
			if (pairString.charAt(q) == '\\')
				q++;
			else if (pairString.charAt(q) == ':') {
				colonIndex = q;
				break;
			}
		}
		//If we didn't find a colon, return null.
		if (colonIndex == -1) {
			System.out.println("SYNTAX ERROR: Failed to find ':' in pair: " + pairString);
		}
		//Get the key and value strings.
		String pairKeyString = pairString.substring(0, colonIndex);
		String pairValueString = pairString.substring(colonIndex + 1);
		//Parse the key and value.
		Object pairKey = parseValue(pairKeyString);
		Object pairValue = parseValue(pairValueString);
		//If we fail to parse the key or the value, the map fails to be made as well.
		if (pairKey == null || pairValue == null)
			return null;
		//Return the pair.
		return new Object[] { pairKey, pairValue };
	}

	//Main for testing.
	public static void main(String[] args) throws IOException {
		//Read a file.
		DataReader reader = new DataReader();
		TreeMap<String, Object> data = reader.readFile("C:\\Users\\John\\Google Drive\\markup testing.txt");

		//Print the loaded data.
		for (Map.Entry<String, Object> entry : data.entrySet()) {
			System.out.println(entry.getKey() + ": " + deepToString(entry.getValue()));
		}
	}

	//A more thorough printing method.
	public static String deepToString(Object obj) {
		if (obj instanceof Object[]) {
			StringBuilder out = new StringBuilder();
			out.append('{');
			Object[] array = (Object[])obj;
			for (int i = 0; i < array.length - 1; i++) {
				out.append(deepToString(array[i]));
				out.append(", ");
			}
			if (array.length > 0)
				out.append(deepToString(array[array.length - 1]));
			out.append('}');
			return out.toString();
		}
		else if (obj instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<Object, Object> map = (Map<Object, Object>)obj;
			StringBuilder out = new StringBuilder();
			out.append('<');
			if (map.size() > 0) {
				for (Map.Entry<Object, Object> entry : map.entrySet()) {
					out.append(deepToString(entry.getKey()));
					out.append(": ");
					out.append(deepToString(entry.getValue()));
					out.append(", ");
				}
				out.delete(out.length() - 2, out.length());
			}
			out.append('>');
			return out.toString();
		}
		return obj.toString();
	}
}