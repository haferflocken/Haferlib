// A mathematical expression that can give an integer value.

package org.haferlib.util.expression;

public interface Expression {
	
	// A marker that indicates a class is a token in an expression.
	public static abstract class Token {
	}
	
	// An operator in an expression.
	public static final class OperatorToken extends Token {
		
		private byte type; // Type corresponds to the index of the operator in Expression.OPERATORS
		
		public OperatorToken(byte type) {
			this.type = type;
		}
		
		public float operate(float value1, float value2) {
			// Add
			if (type == 0)
				return value1 + value2;
			// Subtract
			if (type == 1)
				return value1 - value2;
			// Multiply
			if (type == 2)
				return value1 * value2;
			// Divide
			if (type == 3)
				return value1 / value2;
			// Power
			if (type == 4)
				return (float) Math.pow(value1, value2);
			// Modulus
			if (type == 5)
				return value1 % value2;
			// Max
			if (type == 6)
				return Math.max(value1, value2);
			// Min
			if (type == 7)
				return Math.min(value1, value2);
			// Root
			if (type == 8)
				return (float) Math.pow(value1, 1.0 / value2);
			// Unknown
			return 0;
		}
	}
	
	// A number in an expression.
	public static final class NumberToken extends Token {
		
		private float number;
		
		public NumberToken(float number) {
			this.number = number;
		}
		
		public float getNumber() {
			return number;
		}
	}
	
	// A variable in an expression.
	public static final class VariableToken extends Token {
		
		private String variableName;
		
		public VariableToken(String variableName) {
			this.variableName = variableName;
		}
		
		public String getVariableName() {
			return variableName;
		}
	}
	
	// Compares expressions to see which should be evaluated first.
	// TODO Determine if I need this.
	
	public static final String POWER = "^";
	public static final String ROOT = "^1/";
	public static final String MULTIPLY = "*";
	public static final String DIVIDE = "/";
	public static final String MODULUS = "%";
	public static final String ADD = "+";
	public static final String SUBTRACT = "-";
	public static final String MAX = ">";
	public static final String MIN = "<";
	public static final String[] OPERATORS = {
		ADD, SUBTRACT, MULTIPLY, DIVIDE, POWER, MODULUS, MAX, MIN, ROOT,
	};
	public static final String LEFT_PAREN = "(";
	public static final String RIGHT_PAREN = ")";

	int getValue();
	
	Expression copy();
	
	Expression addWith(Expression other);
	
}
