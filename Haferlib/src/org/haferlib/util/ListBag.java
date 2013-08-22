//An ordered bag is a collection that keeps track of the number of each element in it as a number.
//Inspired by the Bag interface in the Apache Commons Collections framework.
//Backed by a LinkedHashMap for guaranteed iteration order.

package org.haferlib.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class ListBag<E> implements List<E> {
	
	// An element in the bag. Stores a value and a count.
	private static class BagElement<V> {
		
		private V value;
		private int count;
		
		private BagElement(V value, int count) {
			setValue(value);
			setCount(count);
		}
		
		private V getValue() {
			return value;
		}
		
		private int getCount() {
			return count;
		}
		
		private void setValue(V v) {
			value = v;
		}
		
		private void setCount(int c) {
			count = c;
		}
		
		// Equals returns true if the values are equal and does not care about count.
		public boolean equals(Object o) {
			if (!(o instanceof BagElement<?>))
				return false;
			BagElement<?> other = (BagElement<?>)o;
			if ((value == null && other.value != null)
					|| (value != null && other.value == null))
				return false;
			return value.equals(other.value);
		}
		
		public int hashCode() {
			return (value == null ? 0 : value.hashCode());
		}
	}

	// The iterator.
	private class ListBagIterator implements Iterator<E> {
		protected int index, lastIndex;
		
		private ListBagIterator() {
			index = 0;
			lastIndex = -1;
		}

		public boolean hasNext() {
			return index < ListBag.this.size();
		}

		public E next() {
			if (!hasNext())
				throw new NoSuchElementException();
			E out = ListBag.this.get(index);
			lastIndex = index;
			index++;
			return out;
		}

		public void remove() {
			if (lastIndex == -1)
				throw new IllegalStateException();
			ListBag.this.remove(lastIndex);
			lastIndex = -1;
		}
		
	}
	
	// The list iterator.
	private class ListBagListIterator extends ListBagIterator implements ListIterator<E> {
		
		private ListBagListIterator() {
			super();
		}
		
		private ListBagListIterator(int startIndex) {
			this();
			index = startIndex;
		}

		public boolean hasPrevious() {
			return index > 0;
		}

		public E previous() {
			if (!hasPrevious())
				throw new NoSuchElementException();
			E out = ListBag.this.get(index - 1);
			lastIndex = index - 1;
			index--;
			return out;
		}

		public int nextIndex() {
			return index;
		}

		public int previousIndex() {
			return index - 1;
		}

		public void set(E e) {
			if (lastIndex == -1)
				throw new IllegalStateException();
			ListBag.this.set(lastIndex, e);
		}

		public void add(E e) {
			ListBag.this.add(index, e);
			index++;
			lastIndex = -1;
		}
		
	}
	
	// The contents of the bag.
	private List<BagElement<E>> contents; 
	
	// Constructors.
	public ListBag() {
		contents = new ArrayList<>();
	}
	
	private ListBag(List<BagElement<E>> contents) {
		this.contents = contents;
	}
	
	// MODIFIES: this
	// EFFECTS:	 Adds e to this, increasing its count by one. If e isn't already in this it is added to the end. Returns true.
	public boolean add(E e) {
		return add(e, 1);
	}
	
	// MODIFIES: this
	// EFFECTS:  Adds e to this, increasing its count by amount. If e isn't already in this it is added to the end. Returns true;
	public boolean add(E e, int amount) {
		int i = indexOf(e);
		if (i == -1) {
			contents.add(new BagElement<E>(e, amount));
		}
		else {
			BagElement<E> element = contents.get(i);
			element.count += amount;
		}
		return true;
	}
	
	// MODIFIES: this
	// EFFECTS:  Increases the count of e in this by one and ensures the position of e is the given index.
	public void add(int index, E element) {
		add(index, element, 1);
	}
	
	// MODIFIES: this
	// EFFECTS:  Increases the count of e in this by amount and ensures the position of e is the given index.
	public void add(int index, E e, int amount) {
		int i = indexOf(e);
		if (i == -1) {
			contents.add(index, new BagElement<E>(e, amount));
		}
		else if (i == index) {
			contents.get(i).count += amount;
		}
		else {
			BagElement<E> element = contents.remove(i);
			element.count += amount;
			contents.add(index, element);
		}
	}
	
	// MODIFIES: this
	// EFFECTS:  Adds all elements of c to this, increasing the count of each by one. Returns true.
	public boolean addAll(Collection<? extends E> c) {
		for (E e : c) {
			add(e);
		}
		return true;
	}
	
	// MODIFIES: this
	// EFFECTS:  Increases the count in this of the elements in the given collection,
	//			 ensuring they are at the specified position in the list.
	public boolean addAll(int index, Collection<? extends E> c) {
		// TODO
		throw new UnsupportedOperationException();
	}
	
	// MODIFIES: this
	// EFFECTS:  Removes all elements from this.
	public void clear() {
		contents.clear();
	}
	
	// EFFECTS:  Returns true if this contains at least one of the given element.
	public boolean contains(Object o) {
		for (int i = 0; i < contents.size(); i++) {
			BagElement<E> element = contents.get(i);
			if ((o == null && element.getValue() == null) || (element.getValue() != null && element.getValue().equals(o)))
				return true;
		}
		return false;
	}
	
	// EFFECTS:  Returns true if this contains at least one of every element in the collection.
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o))
				return false;
		}
		return true;
	}
	
	// EFFECTS:  Returns true if the given list contains the same elements in the same order.
	public boolean equals(Object o) {
		if (o instanceof ListBag) {
			ListBag<?> other = (ListBag<?>)o;
			if (other.size() != contents.size())
				return false;
			for (int i = 0; i < contents.size(); i++) {
				if (!other.get(i).equals(contents.get(i)))
					return false;
			}
			return true;
		}
		if (o instanceof List) {
			List<?> other = (List<?>)o;
			if (other.size() != contents.size())
				return false;
			int i = 0;
			for (Object e : other) {
				E val = contents.get(i).getValue();
				if ((e == null && val != null)
						|| (e != null && val == null))
					return false;
				if (!e.equals(val))
					return false;
				i++;
			}
			return true;
		}
		return false;
	}
	
	// EFFECTS:  Returns the element at the specified position in this list.
	public E get(int index) {
		return contents.get(index).getValue();
	}
	
	// EFFECTS:  Returns the count of the element at the specified position in this list.
	public int getCount(int index) {
		return contents.get(index).getCount();
	}
	
	// EFFECTS:  Returns the count of the value in this list, returning 0 if none are in this.
	public int getCount(Object o) {
		int index = indexOf(o);
		if (index == -1)
			return 0;
		return contents.get(index).getCount();
	}
	
	// EFFECTS:  Returns the hash code value for this list. See java.util.List.hashCode() for details.
	public int hashCode() {
		int hashCode = 1;
		for (BagElement<E> e : contents)
			hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
		return hashCode;
	}
	
	// EFFECTS:  Returns the index of the first occurrence of the specified element in this list
	//			 or -1 if this does not contain the element.
	public int indexOf(Object o) {
		for (int i = 0; i < contents.size(); i++) {
			BagElement<E> e = contents.get(i);
			if ((o == null && e.getValue() == null) || (e.getValue() != null && e.getValue().equals(o)))
				return i;
		}
		return -1;
	}
	
	// EFFECTS:  Returns true if this contains no elements.
	public boolean isEmpty() {
		return contents.isEmpty();
	}
	
	// EFFECTS:  Returns an iterator over the elements in this list in proper sequence.
	public Iterator<E> iterator() {
		return new ListBagIterator();
	}
	
	// EFFECTS:  Returns the index of the last occurrence of the specified element in this list
	//			 or -1 if this does not contain the element.
	public int lastIndexOf(Object o) {
		for (int i = contents.size() - 1; i > -1; i--) {
			BagElement<E> e = contents.get(i);
			if ((o == null && e.getValue() == null) || (e.getValue() != null && e.getValue().equals(o)))
				return i;
		}
		return -1;
	}
	
	// EFFECTS:  Returns a list iterator over the elements in this list in proper sequence.
	public ListIterator<E> listIterator() {
		return new ListBagListIterator();
	}
	
	// EFFECTS:  Returns a list iterator over the elements in this list in proper sequence
	//			 starting at the specified position in the list.
	public ListIterator<E> listIterator(int index) {
		if (index < 0 || index > contents.size())
			throw new IndexOutOfBoundsException();
		return new ListBagListIterator(index);
	}
	
	// MODIFIES: this
	// EFFECTS:  Removes the element at the given index and sets its count to 0.
	public E remove(int index) {
		BagElement<E> e = contents.remove(index);
		if (e == null)
			return null;
		return e.getValue();
	}
	
	// MODIFIES: this
	// EFFECTS:  Subtracts one from the count of the given element, removing it entirely if its count hits 0.
	public boolean remove(Object o) {
		return remove(o, 1);
	}
	
	// MODIFIES: this
	// EFFECTS:  Subtracts amount from the count of the given element, removing it entirely if its count hits 0.
	public boolean remove(Object o, int amount) {
		int index = indexOf(o);
		if (index == -1)
			return false;
		BagElement<E> e = contents.get(index);
		e.setCount(e.getCount() - amount);
		if (e.getCount() < 1)
			contents.remove(index);
		return true;
	}
	
	// MODIFIES: this
	// EFFECTS:  Remove all occurrences from this of every element that is in the given collection.
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			boolean removed = remove(o);
			if (removed == true)
				changed = true;
		}
		return changed;
	}
	
	// MODIFIES: this
	// EFFECTS:  Remove from this list all elements that aren't in the given collection.
	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		List<BagElement<E>> newContents = new ArrayList<>();
		for (int i = 0; i < contents.size(); i++) {
			BagElement<E> element = contents.get(i);
			if (c.contains(element.getValue()))
				newContents.add(element);
			else
				changed = true;
		}
		contents.clear();
		contents = newContents;
		return changed;
	}
	
	// MODIFIES: this
	// EFFECTS:  If the element isn't already in the list, replaces the element at the specified 
	//			 position in this list with one of the specified element.
	//			 If the element is already at another position in this, removes the element at the given
	//			 position and moves the given element to the given position.
	//			 Returns the element previously at the position.
	public E set(int index, E element) {
		BagElement<E> e = contents.get(index);
		E oldVal = e.getValue();
		int elementIndex = indexOf(element);
		// If the element isn't already in the list, replace the element at index with the given one.
		if (elementIndex == -1) {
			e.setValue(element);
			e.setCount(1);
		}
		// If the element is in the list, move it to the index and remove the old thing that was there.
		else {
			BagElement<E> elementBagElement = contents.remove(elementIndex);
			contents.set(index, elementBagElement);
		}
		return oldVal;
	}
	
	// MODIFIES: this
	// EFFECTS:  Sets the count at the specified index.
	public void setCount(int index, int count) {
		contents.get(index).setCount(count);
	}
	
	// MODIFIES: this
	// EFFECTS:  Sets the count of the given element.
	public void setCount(E element, int count) {
		int index = indexOf(element);
		if (index != -1)
			setCount(index, count);
	}
	
	// EFFECTS:  Returns the number of elements in this.
	public int size() {
		return contents.size();
	}
	
	// EFFECTS:  Returns a view of the portion of this list of the range [fromIndex, toIndex)
	public List<E> subList(int fromIndex, int toIndex) {
		return new ListBag<E>(contents.subList(fromIndex, toIndex));
	}
	
	// EFFECTS:  Returns an array containing all the elements in this list in proper sequence.
	public Object[] toArray() {
		Object[] out = new Object[contents.size()];
		for (int i = 0; i < contents.size(); i++) {
			BagElement<E> e = contents.get(i);
			if (e == null)
				continue;
			out[i] = e.getValue();
		}
		return out;
	}
	
	// EFFECTS:  Returns an array containing all of the elements in this list in proper sequence.
	//			 The runtime type of the returned array is that of the specified array.
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		// If the array is too small, make a new one that is big enough.
		// Thank you, java.util.LinkedList, for showing me how to do this.
		if (a.length < contents.size())
			a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), contents.size());
		
		// Place the elements in the array.
		for (int i = 0; i < contents.size(); i++) {
			a[i] = (T)contents.get(i).getValue();
		}
		
		// If the array is longer than this list, make the element immediately following the contents we just added null.
		if (a.length > contents.size())
			a[contents.size()] = null;
		
		return a; // Return the array.
	}
}
