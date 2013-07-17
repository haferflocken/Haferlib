//An n-ary tree where all nodes have their children sorted by key. All keys are strings.

package org.haferlib.util;

import java.util.Iterator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

public class NAryTree<V> implements Iterable<V> {

	//The iterator class.
	private static class NAryTreeIterator<E> implements Iterator<E> {

		private ArrayDeque<Node<E>> deque;

		private NAryTreeIterator(NAryTree<E> tree) {
			deque = new ArrayDeque<>();
			deque.push(tree.getRoot());
			/*for (int i = 0; i < tree.getRootChildren().size(); i++) {
				deque.push((Node)tree.getRootChildren().get(i));
			}*/
			//System.out.println("Tree root : " + deque.size());
		}

		public boolean hasNext() {
			return (deque.size() > 0);
		}

		public E next() {
			Node<E> current = deque.pop();
			for (int i = 0; i < current.getNumChildren(); i++) {
				deque.push(current.getChildren().get(i));
			}

			return current.getValue();
		}

		//Do nothing with remove.
		public void remove() {
		}
	}

	//The class of nodes in the tree.
	public static class Node<E> implements Comparable<Node<E>> {

		private String key; //The identifier of this node
		private E value; //The value of this node
		private ArrayList<Node<E>> children; //The sorted children.
		private int depth; //How many parents this node has. This isn't used by anything in this file, but may be useful by other classes.

		private Node(String key, E value, ArrayList<Node<E>> children, int depth) {
			this.key = key;
			this.value = value;
			this.children = children;
			Collections.sort(children);
			this.depth = depth;
		}

		private void addChild(Node<E> child) {
			//Place the node in sorted order.
			if (children.size() > 0) {
				for (int i = 0; i < children.size(); i++) {
					if (children.get(i).compareTo(child) >= 0) {
						children.add(i, child);
						return;
					}
				}
				children.add(child);
			}
			else {
				children.add(child);
			}
		}

		public boolean removeChild(Node<E> child) {
			return children.remove(child);
		}

		public boolean equals(Object o) {
			if (o == this || o.equals(key))
				return true;
			return false;
		}

		public int compareTo(Node<E> other) {
			return key.compareTo(other.key);
		}

		//Get a value from this by key. Since it is sorted, we can quicksearch.
		public Node<E> get(String key) {
			//System.out.println("Searching for " + key);

			for (int i = 0; i < children.size(); i++) {
				if (children.get(i).equals(key))
					return children.get(i);
			}
			return null;

			//Failed attempt at quicksearch
			/*Node<V> select;
			int l, r, m;
			l = 0;
			r = children.size();
			while(r - l > 1) {
				m = (l + r) / 2;
				select = children.get(m);
				if (select.equals(key))
					return select;
				else if (key.compareTo(select.key) > 0)
					l = m;
				else //if key < select
					r = m;
			}
			return null;*/
		}

		public String getKey() {
			return key;
		}

		public E getValue() {
			return value;
		}

		public ArrayList<Node<E>> getChildren() {
			return children;
		}

		public int getNumChildren() {
			return children.size();
		}

		public int getDepth() {
			return depth;
		}
	}

	private Pattern keySplitter;	//This is used to split keys around backslashes.
	private String rootPath;		//This is removed from the start of keys before put and get operations.
	private Node<V> rootNode;		//The top node of this tree.

	public NAryTree(String rootPath) {
		keySplitter = Pattern.compile("\\\\");
		this.rootPath = rootPath;
		clear();
	}

	public V get(String key) {
		//Get the node for that key.
		Node<V> node = getNode(key);
		
		//If we found a node, return its value.
		if (node != null)
			return node.value;
		//Otherwise, return null.
		return null;
	}
	
	public Node<V> getNode(String key) {
		//First, the key is compared to the root path and dealt with appropriately.
		if (rootPath.equals(key))
			return rootNode;
		if (rootPath.startsWith(key))
			return null;
		if (key.startsWith(rootPath))
			key = key.substring(rootPath.length());

		//Then, the key is broken into its levels, which are separated by backslashes. Much like a filesystem.
		String[] levels = keySplitter.split(key);

		//Then the nodes are looped through to find the appropriate one.
		Node<V> current = rootNode;
		for (int i = 0; i < levels.length; i++) {
			//See if this level has the right key.
			current = current.get(levels[i]);
			//If there isn't a node for the level, return null.
			if (current == null)
				return null;
		}
		
		//Return the node we found.
		return current;
	}

	public void put(String key, V value) {
		//First, the key is compared to the root path and dealth with appropriately
		if (key.equals(rootPath)) {
			rootNode.value = value;
			return;
		}
		if (rootPath.startsWith(key))
			return;
		if (key.startsWith(rootPath))
			key = key.substring(rootPath.length());

		//Then, the key is broken into levels. Then, any missing levels are created. After that, the value is placed in the appropriate level.
		String[] levels = keySplitter.split(key);

		//The nodes are traversed and made as needed
		Node<V> current = rootNode;
		Node<V> next = null;
		for (int i = 0; i < levels.length; i++) {
			//See if this node has the right next node
			next = current.get(levels[i]);
			//If there isn't a next node, make it
			if (next == null) {
				//System.out.println("Making node " + levels[i]);
				next = new Node<V>(levels[i], null, new ArrayList<Node<V>>(), i);
				current.addChild(next);
			}
			//Go to the next node
			current = next;
		}

		//Once we are here, the nodes have been created and current should hold the node to place the value in
		current.value = value;
	}

	public Node<V> getRoot() {
		return rootNode;
	}

	public ArrayList<Node<V>> getChildren(String key) {
		//Find the node.
		Node<V> node = getNode(key);
		
		//If we found the node, return its children. Otherwise, return null.
		if (node != null)
			return node.children;
		return null;
	}

	public void clear() {
		rootNode = new Node<V>(null, null, new ArrayList<Node<V>>(), 0);
	}

	protected void setRootPath(String path) {
		rootPath = path;
	}

	public Iterator<V> iterator() {
		return new NAryTreeIterator<V>(this);
	}
}