/**
 * File: PictureFilter.java
 * Author: Jon Zhang
 * Date created: August 2013
 * Date last modified: August 10, 2013
 */
import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PictureFilter extends FileFilter {
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		} else if (f.isFile()) {
			if (f.getName().endsWith((".jpg"))
					|| f.getName().endsWith((".jpeg"))
					|| f.getName().endsWith((".gif"))
					|| f.getName().endsWith((".png"))) {
				return true;
			}

		}
		return false;

	}

	@Override
	public String getDescription() {
		return "Images";
	}

}