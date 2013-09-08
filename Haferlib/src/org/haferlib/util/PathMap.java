package org.haferlib.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayDeque;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * An NAry tree implementation that has string keys that look like file system paths.
 * Nodes have their children sorted by key.
 * 
 * @author John Werner
 * 
 */

public class PathMap<V> implements Iterable<V> {

	/**
	 * Iterates over the tree.
	 * 
	 * @author John Werner
	 *
	 */
	private class PathMapIterator implements Iterator<V> {

		private ArrayDeque<Node> deque;

		private PathMapIterator() {
			deque = new ArrayDeque<>();
			deque.push(PathMap.this.rootNode);
		}

		public boolean hasNext() {
			return (deque.size() > 0);
		}

		public V next() {
			Node current = deque.pop();
			for (Node child : current.getChildNodes()) {
				deque.push(child);
			}

			return current.value;
		}

		// Remove is not supported.
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * A node in the tree.
	 * 
	 * @author John Werner
	 *
	 */
	private class Node implements Comparable<Node> {

		private String key; // The identifier of this node
		private V value; // The value of this node
		private TreeMap<String, Node> children; // The sorted children.

		private Node(String key, V value) {
			this.key = key;
			this.value = value;
			children = new TreeMap<String, Node>();
		}

		private void addChild(Node child) {
			children.put(child.key, child);
		}

		public void removeChild(Node child) {
			children.remove(child);
		}

		public boolean equals(Object o) {
			if (o == this || o.equals(key))
				return true;
			return false;
		}

		public int compareTo(Node other) {
			return key.compareTo(other.key);
		}

		public Node getChild(String key) {
			return children.get(key);
		}

		public Collection<Node> getChildNodes() {
			return children.values();
		}

		public int getNumChildren() {
			return children.size();
		}

	}

	// Instance fields.
	private Pattern keySplitter;	// This is used to split keys around backslashes.
	private String rootPath;		// This is removed from the start of keys before put and get operations.
	private Node rootNode;			// The top node of this tree.
	private int numNodes;			// Keeps track of the number of nodes in this tree.
	
	/**
	 * Make an NAryTree. setRootPath must be called before performing any operations on a tree
	 * constructed with this constructor.
	 */
	public PathMap() {
		keySplitter = Pattern.compile("\\\\");
	}

	/**
	 * Make an NAryTree with the given root path.
	 * 
	 * @param rootPath The root path of keys in the tree, which is sent to setRootPath.
	 */
	public PathMap(String rootPath) {
		this();
		setRootPath(rootPath);
	}

	/**
	 * Get the value at a given key.
	 * 
	 * @param key The key to get the value of.
	 * @return The value of the node at the key, or null if no node exists there.
	 */
	public V get(String key) {
		// Get the node for that key.
		Node node = getNode(key);
		
		// If we found a node, return its value.
		if (node != null)
			return node.value;
		// Otherwise, return null.
		return null;
	}
	
	/**
	 * Get the node at a key.
	 * 
	 * @param key The key to look for the node at.
	 * @return The node at the key, or null if no node is at that key.
	 */
	private Node getNode(String key) {
		// First, the key is compared to the root path and dealt with appropriately.
		if (rootPath.equals(key))
			return rootNode;
		if (rootPath.startsWith(key))
			return null;
		if (key.startsWith(rootPath))
			key = key.substring(rootPath.length());

		// Then, the key is broken into its levels, which are separated by backslashes. Much like a filesystem.
		String[] levels = keySplitter.split(key);

		// Then the nodes are looped through to find the appropriate one.
		Node current = rootNode;
		for (int i = 0; i < levels.length; i++) {
			// See if this level has the right key.
			current = current.getChild(levels[i]);
			// If there isn't a node for the level, return null.
			if (current == null)
				return null;
		}
		
		// Return the node we found.
		return current;
	}

	/**
	 * Get the keys of the children of a key.
	 * 
	 * @param key The key of the node to get the child keys of.
	 * @return The keys of the children of the node at key.
	 */
	public String[] getChildKeys(String key) {
		Node node = getNode(key);
		if (node == null)
			return null;
		
		Collection<Node> childNodes = node.getChildNodes();
		String[] childKeys = new String[childNodes.size()];
		int i = 0;
		for (Node n : childNodes) {
			childKeys[i++] = n.key;
		}
		
		return childKeys;
	}
	
	/**
	 * Put a value at a given key, making nodes as needed.
	 * 
	 * @param key Where to place the value.
	 * @param value The value to place at the key.
	 */
	public void put(String key, V value) {
		// First, the key is compared to the root path and dealt with appropriately.
		// If the key is the root, set its value.
		if (key.equals(rootPath)) {
			rootNode.value = value;
			return;
		}
		// If the location should technically be outside of this tree, don't do anything.
		if (rootPath.startsWith(key) || !key.startsWith(rootPath))
			return;
		
		// Make the key relative to the root path.
		key = key.substring(rootPath.length());

		// Then, the key is broken into levels. Then, any missing levels are created. After that, the value is placed in the appropriate level.
		String[] levels = keySplitter.split(key);

		// The nodes are traversed and made as needed.
		Node current = rootNode;
		Node next = null;
		for (int i = 0; i < levels.length; i++) {
			// See if this node has the right next node.
			next = current.getChild(levels[i]);
			// If there isn't a next node, make it.
			if (next == null) {
				next = new Node(levels[i], null);
				current.addChild(next);
				numNodes++;
			}
			// Go to the next node.
			current = next;
		}

		// Once we are here, the nodes have been created and current should hold the node to place the value in
		current.value = value;
	}
	
	/**
	 * Remove the value at the given key. Also removes the node if it has no child nodes.
	 * 
	 * @param key The key of the value to remove.
	 * @return The value that was at the key, or null if there was no value there.
	 */
	public V remove(String key) {
		Node node = getNode(key);
		if (node == null)
			return null;
		
		// Get the old value and clear it from the node.
		V out = node.value;
		node.value = null;
		
		// If the node has no children, find its parent and remove it.
		if (node.getNumChildren() == 0) {
			// Get the key of the parent of this.
			String parentKey;
			if (key.endsWith("\\"))
				parentKey = key.substring(0, key.length() - 1);
			else
				parentKey = key;
			parentKey = parentKey.substring(0, parentKey.lastIndexOf('\\'));
			
			// Remove the node from the parent node.
			Node parentNode = getNode(parentKey);
			if (parentNode != null) {
				parentNode.removeChild(node);
			}
		}
		
		// Return the old value at the key.
		return out;
	}
	
	/**
	 * Get the root path of this tree.
	 * 
	 * @return The root path relative to which all nodes are made.
	 */
	public String getRootPath() {
		return rootPath;
	}

	/**
	 * Clear the contents of this tree.
	 */
	public void clear() {
		rootNode = new Node(null, null);
	}

	/**
	 * Set the root path of this tree, relative to which all nodes are placed.
	 * Clears the tree.
	 * 
	 * @param path The new root path.
	 */
	public void setRootPath(String path) {
		rootPath = path;
		clear();
	}

	/**
	 * Get the number of nodes in this tree.
	 * 
	 * @return The number of nodes in the tree.
	 */
	public int getNumNodes() {
		return numNodes;
	}
		
	/**
	 * Return an iterator over this tree.
	 */
	@Override
	public Iterator<V> iterator() {
		return new PathMapIterator();
	}
}