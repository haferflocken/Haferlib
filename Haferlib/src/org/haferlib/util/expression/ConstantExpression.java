// An expression that never changes.

package org.haferlib.util.expression;


public class ConstantExpression implements Expression {
	
	private int value;
	
	public ConstantExpression(int value) {
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}
	
	@Override
	public Expression copy() {
		return new ConstantExpression(value);
	}
	
	@Override
	public Expression addWith(Expression other) {
		// If we are adding with another constant, return a constant.
		if (other instanceof ConstantExpression) {
			return new ConstantExpression(value + ((ConstantExpression)other).value);
		}
		// Otherwise, we're adding with a variable expression, and we can let it do the work.
		else
			return other.addWith(this);
	}

}
