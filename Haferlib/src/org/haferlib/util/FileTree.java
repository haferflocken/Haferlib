//An n-ary tree that is used to load a directory of files. The files can be filtered by extension.

package org.haferlib.util;

import java.util.Iterator;
import java.util.ArrayDeque;
import java.io.File;
import java.io.FileFilter;

public class FileTree extends NAryTree<File[]> {

	private class FileExtensionFilter implements FileFilter {

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

	private FileExtensionFilter fileFilter;

	//Constructors.
	public FileTree(String filePath, String filterExtension) {
		this(filePath, new String[] { filterExtension });
	}

	public FileTree(String filePath, String[] filterExtensions) {
		super(filePath);
		fileFilter = new FileExtensionFilter(filterExtensions);
		setFilePath(filePath);
	}

	public void setFilePath(String filePath) {
		//Set super.rootPath
		setRootPath(filePath);
		//Build the tree.
		clear();
		File file = new File(filePath);
		if (file.exists() && file.isDirectory()) {
			//Traverse the files and build the tree.
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
				//When we add a folder to the tree, we need to make sure its path ends with a backslash.
				String currentPath = current.getAbsolutePath();
				if (currentPath.charAt(currentPath.length() - 1) != '\\')
					currentPath += '\\';
				put(currentPath, current.listFiles(fileFilter));
			}
		}
	}

	public String[] getFilterExtensions() {
		return fileFilter.extensions;
	}

	public File[] get(String key) {
		return super.get(key);
	}

	public void put(String key, File[] value) {
		super.put(key, value);
	}

	public Iterator<File[]> iterator() {
		return super.iterator();
	}
}