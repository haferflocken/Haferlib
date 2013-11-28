//This class just finds and holds the path to the folder this game is in

package org.haferutil;

import java.io.File;

public class Paths {

	public String gamePath;

	public Paths() {
		gamePath = null;
	}

	public Paths(String folderName) {
		updateGamePath(folderName);
	}

	public void updateGamePath(String folderName) {
		//Find the root
		String path = new File("").getAbsolutePath();
		System.out.println(path);
		path = path.substring(0, path.lastIndexOf(folderName) + folderName.length());
		path += '\\';
		gamePath = path;
		System.out.println(gamePath);
	}
}