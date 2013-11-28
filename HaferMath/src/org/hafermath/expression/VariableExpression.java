// A mathematical expression with variables in it, which means it must be evaluated later.

package org.hafermath.expression;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Map;

public class VariableExpression implements Expression {

	private final Token[] tokens; // An array of the tokens in the expression.
	private final VariableToken[] varTokens; // The variable tokens.
	private ArrayDeque<Float> evaluateStack; // The stack the expression does work with.
	private float lastResult; // The last result of this expression.
	
	// Constructor.
	public VariableExpression(Token[] tokens) {
		this.tokens = tokens; // Set the tokens.
		
		// Count the number of variables.
		int numVars = 0;
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i] instanceof VariableToken)
				numVars++;
		}

		// Initialize varTokens and put the variables in it.
		varTokens = new VariableToken[numVars];
		for (int q = 0, i = 0; i < tokens.length; i++) {
			if (tokens[i] instanceof VariableToken)
				varTokens[q++] = (VariableToken) tokens[i];
		}

		evaluateStack = new ArrayDeque<Float>(); // Make the stack to evaluate with.
	}
	
	// Evaluate the expression.
	public void evaluate(Map<String, Expression> varMap) {
		// Clear the stack.
		evaluateStack.clear();

		// Loop through the tokens in the expression.
		for (int i = 0; i < tokens.length; i++) {
			// If the token is an operator, pop two values off the stack,
			// operate on them, and push the result onto the stack.
			if (tokens[i] instanceof OperatorToken) {
				OperatorToken op = (OperatorToken)tokens[i];
				float value1 = evaluateStack.pop();
				float value2 = evaluateStack.pop();
				float value3 = op.operate(value2, value1);
				evaluateStack.push(value3);
			}
			// If the token is a number or a variable, get its value and push it onto the stack.
			else if (tokens[i] instanceof NumberToken) {
				float value = ((NumberToken)tokens[i]).getNumber();
				evaluateStack.push(value);
			}
			else if (tokens[i] instanceof VariableToken) {
				String varName = ((VariableToken)tokens[i]).getVariableName();
				Expression e = varMap.get(varName);
				if (e != null)
					evaluateStack.push(new Float(e.getValue()));
				else
					evaluateStack.push(new Float(0));
			}
		}

		// The last thing in the stack should be the result.
		lastResult = evaluateStack.pop();
	}

	// Get the variables this expression references.
	public VariableToken[] getVariables() {
		return varTokens;
	}
	
	@Override
	public float getValue() {
		return lastResult;
	}
	
	@Override
	public Expression copy() {
		return new VariableExpression(Arrays.copyOf(tokens, tokens.length));
	}
	
	@Override
	public Expression addWith(Expression other) {
		// Since the tokens are in RPN, we can just append the other's tokens to the end of
		// this and put a plus sign at the end.
		Token[] otherTokens;
		if (other instanceof ConstantExpression)
			otherTokens = new Token[] { new NumberToken(((ConstantExpression)other).getValue()) };
		else
			otherTokens = ((VariableExpression)other).tokens;
		
		Token[] outTokens = Arrays.copyOf(tokens, tokens.length + otherTokens.length + 1);
		for (int i = 0; i < otherTokens.length; i++)
			outTokens[tokens.length + i] = otherTokens[i];
		outTokens[outTokens.length - 1] = new OperatorToken((byte)0);
		
		return new VariableExpression(outTokens);
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		for (Token t : tokens) {
			out.append(t.toString());
			out.append(' ');
		}
		return out.toString();
	}

}
