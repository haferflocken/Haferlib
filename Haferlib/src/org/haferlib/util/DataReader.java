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

/**
 * Reads data structures from files.
 * 
 * @author John Werner
 *
 */

public class DataReader {

	private static final String TRUE = "true";
	private static final String FALSE = "false";

	private Matcher intMatcher;
	private Matcher floatMatcher;
	private Matcher fieldNameMatcher;
	private Matcher equalsMatcher;

	/**
	 * Create a DataReader.
	 */
	public DataReader() {
		intMatcher = Pattern.compile("-?\\d+").matcher("");
		floatMatcher = Pattern.compile("-?\\d+\\.\\d+").matcher("");
		fieldNameMatcher = Pattern.compile("[a-zA-Z]\\w*").matcher("");
		equalsMatcher = Pattern.compile("\\s*=").matcher("");
	}

	/**
	 * Read the data from a file at a given path.
	 * 
	 * @param pathString A string representing the file path.
	 * @return A map of Strings to Objects that holds the loaded data from the file.
	 * @throws IOException if an I/O error occurs while reading the file.
	 */
	public Map<String, Object> readFile(String pathString) throws IOException {
		return readFile(FileSystems.getDefault().getPath(pathString));
	}

	/**
	 * Read the data from a file at a given path.
	 * 
	 * @param source A path representing the file.
	 * @return A map of Strings to Objects that holds the loaded data from the file.
	 * @throws IOException if an I/O error occurs while reading the file.
	 */
	public Map<String, Object> readFile(Path source) throws IOException {
		// Get a scanner for the file. Make sure it closes if it fails.
		Scanner scan = null;
	
		// Read the entire file in as a string.
		scan = new Scanner(source);
		StringBuilder contents = new StringBuilder();
		while (scan.hasNextLine()) {
			contents.append(scan.nextLine());
		}
		scan.close();

		// Read the string.
		return readString(contents.toString());
	}

	/**
	 * Read the data out of a string.
	 * 
	 * @param rawData A string, most typically the entire contents of a file, that the data is read from.
	 * @return A map of Strings to Objects that holds the loaded data from the string.
	 */
	public Map<String, Object> readString(String rawData) {
		// Get rid of trailing and leading whitespace.
		rawData = rawData.trim();

		// Break the raw data into tokens and trim them.
		// Count the number of non-string semicolons.
		int numTokens = 0;
		for (int i = 0; i < rawData.length(); i++) {
			char c = rawData.charAt(i);
			if (c == '\"') {
				i = indexOfStringClose(rawData, i);
			}
			else if (c == ';') {
				numTokens++;
			}
		}
		
		// Allocate an array for the tokens and make them.
		String[] tokens = new String[numTokens];
		int tokenIndex = 0;
		int startToken = 0;
		for (int i = 0; i < rawData.length(); i++) {
			char c = rawData.charAt(i);
			if (c == '\"') {
				int stringClose = indexOfStringClose(rawData, i);
				i = stringClose;
			}
			else if (c == ';') {
				tokens[tokenIndex] = rawData.substring(startToken, i);
				tokenIndex++;
				startToken = i + 1;
			}
		}

		// Look through the tokens and return the output TreeMap.
		return buildMapFromTokens(tokens);
	}

	/**
	 * Builds a Map of data from an array of tokens.
	 * Tokens must be a field name followed by an = followed by a value. They must be trimmed.
	 * 
	 * @param tokens The tokens to make into a map.
	 * @return A map made from the tokens.
	 */
	private Map<String, Object> buildMapFromTokens(String[] tokens) {
		// Make the output TreeMap.
		TreeMap<String, Object> out = new TreeMap<>();

		// Look through the tokens for syntax errors.
		for (int i = 0; i < tokens.length; i++) {
			// Get the field name and the value.
			fieldNameMatcher.reset(tokens[i]);
			if (!fieldNameMatcher.find() || fieldNameMatcher.start() != 0) {
				Log.getDefaultLog().error("SYNTAX ERROR: Invaid field name in: " + tokens[i]);
				continue;
			}
			String fieldName = fieldNameMatcher.group();

			// Make sure the field name is followed by an =.
			equalsMatcher.reset(tokens[i]);
			if (!equalsMatcher.find() || equalsMatcher.start() != fieldNameMatcher.end()) {
				Log.getDefaultLog().error("SYNTAX ERROR: Failed to find = after field name.");
				continue;
			}

			// Get the value, which is just everything after the =.
			int valueStart = equalsMatcher.end();
			if (valueStart >= tokens[i].length()) {
				Log.getDefaultLog().error("SYNTAX ERROR: Expected value after = in: " + tokens[i]);
				continue;
			}
			String rawValue = tokens[i].substring(valueStart);

			// Parse the value. If it isn't null, add it to the output.
			Object value = parseValue(rawValue);
			if (value != null) {
				out.put(fieldName, value);
			}
		}

		// Return the output.
		return out;
	}

	/**
	 * Find the last character of a block enclosed by quotes in a string.
	 * 
	 * @param string The string to look in.
	 * @param startIndex Where to start looking. Generally the opening quote.
	 * @return The index of the first non-escaped quote after startIndex. -1 if none is found.
	 */
	private int indexOfStringClose(String string, int startIndex) {
		for (int q = startIndex + 1; q < string.length(); q++) {
			if (string.charAt(q) == '\\')
				q++;
			else if (string.charAt(q) == '"')
				return q;
		}
		return -1;
	}

	/**
	 * Find the last character of a block defined by an open and close character.
	 * Sub-blocks are respected: calling indexOfBlockClose("{ {} }", 0, '{', '}') will
	 * return 5, not 3.
	 * 
	 * @param string The string to look through.
	 * @param startIndex The index to begin looking at. Generally the opening character of the block.
	 * @param openBlock The character that opens blocks.
	 * @param closeBlock The character that closes blocks.
	 * @return The index of the closing character of the block. -1 if none is found.
	 */
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

	// Parses a value string into a proper value object. Prints out any syntax errors it finds.
	// Returns null if it encounters a syntax error.
	private Object parseValue(String value) {
		// Trim the value.
		value = value.trim();

		// Is it a boolean?
		if (value.equals(TRUE) || value.equals(FALSE))
			return Boolean.parseBoolean(value);

		// Is it an integer?
		intMatcher.reset(value);
		if (intMatcher.matches())
			return Integer.parseInt(value);

		// Is it a float?
		floatMatcher.reset(value);
		if (floatMatcher.matches())
			return Float.parseFloat(value);

		// Only numbers can have a length less than 2. If the length is less than 2, return true.
		if (value.length() < 2) {
			Log.getDefaultLog().error("SYNTAX ERROR: Non-number value too short to be valid: " + value);
			return null;
		}

		// Is it a string?
		if (value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"') {
			int stringEnd = indexOfStringClose(value, 0);
			if (stringEnd != value.length() - 1) {
				Log.getDefaultLog().error("SYNTAX ERROR: Unexpected end of string at " + stringEnd + " in " + value);
				return null;
			}
			//Make and return the string if it's a valid string.
			return value.substring(1, value.length() - 1);
		}

		// Is it an array?
		// Arrays start and end with {} and contain values separated by commas.
		if (value.charAt(0) == '{' && value.charAt(value.length() - 1) == '}') {
			// Look through the array, parsing each value as it is seen.
			ArrayList<Object> arrayBuilder = new ArrayList<>();
			int subStart = 1;
			for (int i = 1; i < value.length() - 1; i++) {
				// If we have found the next subvalue to parse, parse it.
				if (value.charAt(i) == ',') {
					String sub = value.substring(subStart, i);
					Object subValue = parseValue(sub);
					// If we fail to parse the value, the array fails to be made as well.
					if (subValue == null)
						return null;
					// Add the value to arrayBuilder.
					arrayBuilder.add(subValue);
					// Get ready to parse the next value.
					subStart = i + 1;
				}
				// We need to make sure we skip over commas inside of string, array and map values.
				else if (value.charAt(i) == '"') {
					int endOfBlock = indexOfStringClose(value, i);
					if (endOfBlock == -1) {
						Log.getDefaultLog().error("SYNTAX ERROR: Unclosed string token in: " + value);
						return null;
					}
					i = endOfBlock;
				}
				else if (value.charAt(i) == '{') {
					int endOfBlock = indexOfBlockClose(value, i, '{', '}');
					if (endOfBlock == -1) {
						Log.getDefaultLog().error("SYNTAX ERROR: Unclosed array token in: " + value);
						return null;
					}
					i = endOfBlock;
				}
				else if (value.charAt(i) == '<') {
					int endOfBlock = indexOfBlockClose(value, i, '<', '>');
					if (endOfBlock == -1) {
						Log.getDefaultLog().error("SYNTAX ERROR: Unclosed map token in: " + value);
						return null;
					}
					i = endOfBlock;
				}
			}
			// Because the last element doesn't necesarily have a comma after it, parse the value
			// of the last element which may not have been grabbed by the loop.
			String lastElementString = value.substring(subStart, value.length() - 1).trim();
			if (lastElementString.length() > 0) {
				Object lastElement = parseValue(lastElementString);
				if (lastElement == null)
					return null;
				arrayBuilder.add(lastElement);
			}
			// If we got through all that without returning, we have a valid array!
			return arrayBuilder.toArray();
		}

		// Is it a map?
		// Maps start and end with <> and contain key-value pairs of the format key : value, .
		if (value.charAt(0) == '<' && value.charAt(value.length() - 1) == '>') {
			// Look through the map, parsing each pair as it is seen.
			Map<Object, Object> map = new LinkedHashMap<>();
			int subStart = 1;
			for (int i = 1; i < value.length() - 1; i++) {
				// If we have found the next pair to parse, parse it.
				if (value.charAt(i) == ',') {
					//Get the pair string
					String pairString = value.substring(subStart, i);
					//Parse the pair.
					Object[] pair = parsePair(pairString);
					if (pair == null)
						return null;
					// Add the pair to the map.
					map.put(pair[0], pair[1]);
					// Get ready to parse the next value.
					subStart = i + 1;
				}
				// We need to make sure we skip over commas inside of string, array and map values.
				else if (value.charAt(i) == '"') {
					int endOfBlock = indexOfStringClose(value, i);
					if (endOfBlock == -1) {
						Log.getDefaultLog().error("SYNTAX ERROR: Unclosed string token in: " + value);
						return null;
					}
					i = endOfBlock;
				}
				else if (value.charAt(i) == '{') {
					int endOfBlock = indexOfBlockClose(value, i, '{', '}');
					if (endOfBlock == -1) {
						Log.getDefaultLog().error("SYNTAX ERROR: Unclosed array token in: " + value);
						return null;
					}
					i = endOfBlock;
				}
				else if (value.charAt(i) == '<') {
					int endOfBlock = indexOfBlockClose(value, i, '<', '>');
					if (endOfBlock == -1) {
						Log.getDefaultLog().error("SYNTAX ERROR: Unclosed map token in: " + value);
						return null;
					}
					i = endOfBlock;
				}
			}
			// Because the last element doesn't necessarily have a comma after it, parse the value
			// of the last element which may not have been grabbed by the loop.
			String lastPairString = value.substring(subStart, value.length() - 1).trim();
			if (lastPairString.length() > 0) {
				// Parse the pair.
				Object[] pair = parsePair(lastPairString);
				if (pair == null)
					return null;

				// Add the pair to the map.
				map.put(pair[0], pair[1]);
			}
			// If we got through all that without returning, we have a valid map!
			return map;
		}

		// If we get here, it isn't anything, so return null.
		Log.getDefaultLog().error("SYNTAX ERROR: Unrecognized value: " + value);
		return null;
	}

	// Take a raw pair string and make it into a key and a value.
	// Returns an array with the first element being the key and the second element being the value.
	private Object[] parsePair(String pairString) {
		// First we need to find the : that separates the key and value.
		int colonIndex = -1;
		for (int q = 0; q < pairString.length(); q++) {
			if (pairString.charAt(q) == '\\')
				q++;
			else if (pairString.charAt(q) == ':') {
				colonIndex = q;
				break;
			}
		}
		// If we didn't find a colon, return null.
		if (colonIndex == -1) {
			Log.getDefaultLog().error("SYNTAX ERROR: Failed to find ':' in pair: " + pairString);
		}
		// Get the key and value strings.
		String pairKeyString = pairString.substring(0, colonIndex);
		String pairValueString = pairString.substring(colonIndex + 1);
		// Parse the key and value.
		Object pairKey = parseValue(pairKeyString);
		Object pairValue = parseValue(pairValueString);
		// If we fail to parse the key or the value, the map fails to be made as well.
		if (pairKey == null || pairValue == null)
			return null;
		// Return the pair.
		return new Object[] { pairKey, pairValue };
	}

}