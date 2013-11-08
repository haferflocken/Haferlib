package org.haferlib.gdx;

public class IntQueue {

	private int[] array;
	private int start, end;
	private int size;
	
	public IntQueue(int maxCapacity) {
		array = new int[maxCapacity];
		start = 0;
		end = 0;
		size = 0;
	}
	
	public void pushEnd(int element) {
		if (size < array.length) {
			array[end] = element;
			end++;
			end %= array.length;
			size++;
			return;
		}
		throw new IllegalStateException("Cannot pushEnd a full queue.");
	}
	
	public int peekFront() {
		if (size > 0) {
			return array[start];
		}
		throw new IllegalStateException("Cannot peekFront an empty queue.");
	}
	
	public int popFront() {
		if (size > 0) {
			int out = array[start];
			start++;
			start %= array.length;
			size--;
			return out;
		}
		throw new IllegalStateException("Cannot popFront an empty queue.");
	}
	
	public void clear() {
		start = 0;
		end = 0;
		size = 0;
	}
	
	public int size() {
		return array.length;
	}

}
