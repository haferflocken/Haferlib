package org.haferutil;

import java.util.Iterator;
import java.util.ArrayDeque;
import java.io.File;
import java.io.FileFilter;

/**
 * An NAryTree that is used to load a directory of files. The files can be filtered by extension.
 * This tree is immutable except by setRootPath. Attempts to call put/remove will result in an
 * UnsupportedOperationException being thrown. This is to ensure this tree accurately reflects the
 * file system at the time it was built.
 * 
 * Nodes of this tree represent directories.
 * 
 * This class is not a subtype of NAryTree.
 * 
 * @author John
 *
 */

public class FileTree extends PathMap<File[]> {

	/**
	 * A FileFilter that filters by extension.
	 * 
	 * @author John
	 *
	 */
	private static class FileExtensionFilter implements FileFilter {

		private String[] extensions;

		public FileExtensionFilter(String[] es) {
			extensions = es;
		}

		public boolean accept(File pathname) {
			if (!pathname.isFile())
				return false;
			for (int i = 0; i < extensions.length; i++)
				if (pathname.getName().endsWith(extensions[i]))
					return true;
			return false;
		}

	}

	// Instance fields.
	private FileExtensionFilter fileFilter;	// Determines which files are accepted by this tree.
	private int numFiles;					// The number of files in this tree (not counting directories).

	/**
	 * Create a file tree out of the given file path that contains all the files there.
	 * 
	 * @param filePath The path of the directory to make this tree from.
	 */
	public FileTree(String filePath) {
		this(filePath, "");
	}
	
	/**
	 * Create a file tree out of the given file path that contains all of the files there that
	 * have names ending in filterExtension.
	 * 
	 * @param filePath The path of the directory to make this tree from.
	 * @param filterExtension The extension to filter files by.
	 */
	public FileTree(String filePath, String filterExtension) {
		this(filePath, new String[] { filterExtension });
	}

	/**
	 * Create a file tree out of the given file path that contains all of the files there
	 * that have names ending in one of the elements of filterExtensions.
	 * 
	 * @param filePath The path of the directory to make this tree from.
	 * @param filterExtensions The extensions to filter files by.
	 */
	public FileTree(String filePath, String[] filterExtensions) {
		super();
		fileFilter = new FileExtensionFilter(filterExtensions);
		setRootPath(filePath);
	}
	
	/**
	 * Get the file extensions that are used to filter the tree.
	 * 
	 * @return The file extensions the tree filters by.
	 */
	public String[] getFilterExtensions() {
		return fileFilter.extensions;
	}

	/**
	 * Get the number of files in this file tree.
	 * 
	 * @return The total number of files in all the directories in this tree.
	 */
	public int getNumFiles() {
		return numFiles;
	}

	/**
	 * Set the root path of this file tree and build it.
	 */
	@Override
	public void setRootPath(String filePath) {
		// Throw an InvalidArgumentException if this isn't a directory or doesn't exist.
		File file = new File(filePath);
		if (!file.exists() || !file.isDirectory())
			throw new IllegalArgumentException("The filePath parameter must reference a directory.");
		
		// Set the root path.
		super.setRootPath(filePath);
		
		// Traverse the files and build the tree.
		ArrayDeque<File> todo = new ArrayDeque<File>();
		todo.add(file);
		File current;
		while (todo.size() > 0) {
			current = todo.pop();
			File[] subFiles = current.listFiles();
			for (int i = 0; i < subFiles.length; i++) {
				if (subFiles[i].isDirectory()) {
					todo.add(subFiles[i]);
				}
			}
				
			// Make a key and make sure it ends with a backslash.
			String currentPath = current.getAbsolutePath();
			if (currentPath.charAt(currentPath.length() - 1) != '\\')
				currentPath += '\\';
				
			// Make a value, place it in the tree, and add to the number
			// of files in the tree.
			File[] currentFiles = current.listFiles(fileFilter);
			super.put(currentPath, currentFiles);
			if (currentFiles != null)
				numFiles += currentFiles.length;
		}
	}
		
	/**
	 * Get the files at a given directory path.
	 */
	@Override
	public File[] get(String key) {
		return super.get(key);
	}

	/**
	 * @throws UnsupportedOperationException whenever called.
	 */
	@Override
	public void put(String key, File[] value) {
		throw new UnsupportedOperationException("FileTrees are immutable.");
	}

	/**
	 * @throws UnsupportedOperationException whenever called.
	 */
	@Override
	public File[] remove(String key) {
		throw new UnsupportedOperationException("FileTrees are immutable.");
	}
	
	/**
	 * Get an iterator over this tree. Each element the iterator looks at is the contents of a directory.
	 * 
	 * @return An iterator over this tree.
	 */
	@Override
	public Iterator<File[]> iterator() {
		return super.iterator();
	}

}