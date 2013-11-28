// Makes expressions from strings.

package org.hafermath.expression;

import java.util.ArrayDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hafermath.expression.ConstantExpression;
import org.hafermath.expression.VariableExpression;

import static org.hafermath.expression.Expression.*;

public class ExpressionBuilder {

	public static final String NUMBER_REGEX = "\\-?(\\d*\\.)?\\d+";
	public static final String VARIABLE_REGEX = "[A-Za-z]\\w*";
	public static final String OPERATOR_REGEX = "\\+|\\-|\\*|/|\\^|%|>|<|\\^1/";
	public static final String PARENTHESIS_REGEX = "\\(|\\)";
	public static final String TOKEN_REGEX = NUMBER_REGEX + '|' +
			VARIABLE_REGEX + '|' + OPERATOR_REGEX + '|' + PARENTHESIS_REGEX;

	private Matcher numberMatcher;
	private Matcher tokenMatcher;
	
	// Constructor.
	public ExpressionBuilder() {
		numberMatcher = Pattern.compile(NUMBER_REGEX).matcher("");
		tokenMatcher = Pattern.compile(TOKEN_REGEX).matcher("");
	}
	
	// Make tokens from a string that can be turned into RPN.
	private String[] makeRawTokens(String expString) {
		// Count the number of tokens in the string.
		int numTokens = 0;
		tokenMatcher.reset(expString);
		while (tokenMatcher.find()) {
			numTokens++;
		}
		
		// Get the tokens from the string.
		tokenMatcher.reset();
		String rawTokens[] = new String[numTokens];
		int tI = 0;
		while (tokenMatcher.find()) {
			rawTokens[tI++] = tokenMatcher.group();
		}
		
		// We need to deal with implied multiplication.
		// Count the number of left parenthesis that don't have operators before them, ex. 3(x + 2)
		int numOperatorlessParens = 0;
		for (int i = 1; i < rawTokens.length; i++) {
			if (rawTokens[i].equals(LEFT_PAREN)) {
				if (!rawTokens[i].equals(LEFT_PAREN) && operatorType(rawTokens[i - 1]) == -1) {
					numOperatorlessParens++;
				}
			}
		}

		// Add in MULTIPLY where there wasn't one.
		if (numOperatorlessParens > 0) {
			// Make the new array.
			String[] oldInfixTokens = rawTokens;
			rawTokens = new String[oldInfixTokens.length + numOperatorlessParens];

			// The first token is always the same.
			rawTokens[0] = oldInfixTokens[0];

			// Loop through the rest, adding MULTIPLY.
			for (int offset = 0, i = 1; i < oldInfixTokens.length; i++) {
				if (oldInfixTokens[i].equals(LEFT_PAREN)) {
					if (operatorType(oldInfixTokens[i - 1]) == -1) {
						rawTokens[i + offset] = MULTIPLY;
						offset++;
					}

				}
				rawTokens[i + offset] = oldInfixTokens[i];
			}
		}
		
		// Return the tokens.
		return rawTokens;
	}
	
	// Use a shunting yard to reorder infix tokens into RPN tokens.
	private String[] shuntingYard(String[] infixTokens) {
		// Count the number of parenthesis.
		int numParens = 0;
		for (int i = 0; i < infixTokens.length; i++) {
			if (infixTokens[i].equals(LEFT_PAREN) || infixTokens[i].equals(RIGHT_PAREN))
				numParens++;
		}

		// Make the output array. It has a length of infixTokens.length - numParens.
		String[] rpnTokens = new String[infixTokens.length - numParens];

		// Make the operator stack.
		ArrayDeque<String> operatorStack = new ArrayDeque<String>();

		// Loop through the tokens to shunting yard them.
		int q = 0;
		for (int i = 0; i < infixTokens.length; i++) {
			// Get the operator type.
			byte opType = operatorType(infixTokens[i]);
			
			// If the token is an operator, just look at Wikipedia.
			if (opType != -1) {
				while (operatorStack.size() > 0) {
					String operator = operatorStack.peek();
					if (precedence(infixTokens[i], operator) <= 0) {
						rpnTokens[q] = operatorStack.pop();
						q++;
					}
					else {
						break;
					}
				}
				operatorStack.push(infixTokens[i]);
			}
			// If the token is a left parenthesis, push it onto the operator stack.
			else if (infixTokens[i].equals(LEFT_PAREN)) {
				operatorStack.push(infixTokens[i]);
			}
			// If the token is a right parenthesis, pop tokens off the stack until
			// we reach a left parenthesis.
			else if (infixTokens[i].equals(RIGHT_PAREN)) {
				while (operatorStack.size() > 0) {
					String operator = operatorStack.pop();
					if (operator.equals(LEFT_PAREN)) {
						break;
					}
					else {
						rpnTokens[q] = operator;
						q++;
					}
				}
			}
			// If the token is a number or a variable, add it to rpnTokens.
			else {
				rpnTokens[q] = infixTokens[i];
				q++;
			}
		}

		// Once all tokens are read, pop the stack onto the output array.
		while (operatorStack.size() > 0) {
			rpnTokens[q] = operatorStack.pop();
			q++;
		}
		
		// Return the RPN tokens.
		return rpnTokens;
	}
	
	// Make tokens from raw tokens.
	private Token[] makeTokens(String[] rawTokens) {
		Token[] tokens = new Token[rawTokens.length];
		
		for (int i = 0; i < rawTokens.length; i++) {
			// Check if the token is an operator.
			byte opType = operatorType(rawTokens[i]);
			if (opType != -1) {
				tokens[i] = new OperatorToken(opType);
				continue;
			}
			// Check if the token is a number.
			numberMatcher.reset(rawTokens[i]);
			if (numberMatcher.matches()) {
				tokens[i] = new NumberToken(Float.parseFloat(rawTokens[i]));
				continue;
			}
			// If it is neither an operator or a number, assume it is a variable.
			tokens[i] = new VariableToken(rawTokens[i]);
		}
		
		return tokens;
	}
	
	// Get the type of operator a string represents. Returns -1 if it isn't an operator.
	public byte operatorType(String s) {
		for (byte i = 0; i < OPERATORS.length; i++) {
			if (OPERATORS[i].equals(s))
				return i;
		}
		return -1;
	}
	
	// See if there are any variables in an array of raw tokens.
	private boolean hasVariables(Token[] tokens) {
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i] instanceof VariableToken)
				return true;
		}
		return false;
	}
	
	// Make an expression from an infix string.
	public Expression makeExpression(String expString) {
		// Break the string into tokens that can be parsed into RPN.
		String[] rawTokens = makeRawTokens(expString);
		
		// Reorder the infix tokens into RPN tokens.
		rawTokens = shuntingYard(rawTokens);
		
		// Make tokens from the raw tokens.
		Token[] tokens = makeTokens(rawTokens);
		
		// If there are no variables, precalculate the value of the expression
		// and return a constant expression.
		if (!hasVariables(tokens)) {
			VariableExpression calc = new VariableExpression(tokens);
			calc.evaluate(null);
			return new ConstantExpression(calc.getValue());
		}
		// If there are variables, return a new variable expression.
		return new VariableExpression(tokens);
	}
	
	// Compare the precedence of op1 to op2. -1 if op1 is less precedent, 0 if equal, 1 is op1 is more precedent.
	private int precedence(String op1, String op2) {
		// Operators are, in order of precedence: POWER, ROOT, MULTIPLY, DIVIDE, MODULUS, ADD, SUBTRACT, MAX, MIN

		// op1 equals POWER or ROOT
		if (op1.equals(POWER) || op1.equals(ROOT)) {
			if (op2.equals(POWER) || op2.equals(ROOT)) {
				return 0;
			}
			else {
				return 1;
			}
		}
		// op1 equals MULTIPLY, DIVIDE, or MODULUS
		else if (op1.equals(MULTIPLY) || op1.equals(DIVIDE) || op1.equals(MODULUS)) {
			if (op2.equals(POWER) || op2.equals(ROOT)) {
				return -1;
			}
			else if (op2.equals(MULTIPLY) || op2.equals(DIVIDE) || op2.equals(MODULUS)) {
				return 0;
			}
			else {
				return 1;
			}
		}
		// op1 equals ADD or SUBTRACT
		else if (op1.equals(ADD) || op1.equals(SUBTRACT)) {
			if (op2.equals(POWER) || op2.equals(ROOT) || op2.equals(MULTIPLY) || op2.equals(DIVIDE) || op2.equals(MODULUS)) {
				return -1;
			}
			else if (op2.equals(ADD) || op2.equals(SUBTRACT)) {
				return 0;
			}
			else {
				return 1;
			}
		}
		// op1 equals MAX or MIN
		else {
			if (op2.equals(POWER) || op2.equals(ROOT) || op2.equals(MULTIPLY) || op2.equals(DIVIDE) || op2.equals(MODULUS) || op2.equals(ADD) || op2.equals(SUBTRACT)) {
				return -1;
			}
			else if (op2.equals(MAX) || op2.equals(MIN)) {
				return 0;
			}
			else {
				return 1;
			}
		}
	}

}
