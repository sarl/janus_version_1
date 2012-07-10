/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2009-2011 Janus Core Developers
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.janus_project.janus_maven_plugin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.maven.plugin.logging.Log;

/**
 * Zip utilities.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ZipUtils {

	private static final String ZIP_SEP = File.separator;
	
	/**
	 * Zip the content of a directory.
	 * 
	 * @param log
	 * @param directory
	 * @param zip
	 * @throws IOException
	 */
	public static final void zipDirectory(Log log, File directory, File zip) throws IOException {
		zipDirectories(log, Collections.singleton(directory), zip, null);
	}

	/**
	 * Zip the content of a directory.
	 * 
	 * @param log
	 * @param directory
	 * @param zip
	 * @param zipBaseDir
	 * @throws IOException
	 */
	public static final void zipDirectory(Log log, File directory, File zip, String zipBaseDir)
			throws IOException {
		zipDirectories(log, Collections.singleton(directory), zip, zipBaseDir);
	}

	/**
	 * Zip the content of directories.
	 * 
	 * @param log
	 * @param directories
	 * @param zip
	 * @throws IOException
	 */
	public static final void zipDirectories(Log log, Set<File> directories, File zip) throws IOException {
		zipDirectories(log, directories, zip, null);
	}

	/**
	 * Zip the content of directories.
	 * 
	 * @param log
	 * @param directories
	 * @param zip
	 * @param zipBaseDir
	 * @throws IOException
	 */
	public static final void zipDirectories(Log log, Set<File> directories, File zip, String zipBaseDir)
			throws IOException {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
		try {
			File d;
			for(File directory : directories) {
				if (directory.isDirectory())
					d = directory;
				else
					d = directory.getParentFile();
				zip(log, d, d.getAbsolutePath(), zos, zipBaseDir);
			}
		}
		finally {
			zos.close();
		}
	}
	
	private static String mergeNames(String... names) {
		StringBuilder buffer = new StringBuilder();
		for(String s : names) {
			if (s!=null && !"".equals(s)) { //$NON-NLS-1$
				buffer.append(ZIP_SEP);
				buffer.append(s);
			}
		}
		String name = buffer.toString();
		return name.replaceAll("["+ZIP_SEP+"]+", ZIP_SEP); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static final void zip(Log log, File directory, String baseDir, ZipOutputStream zos, String innerBaseDir)
			throws IOException {
		File[] files = directory.listFiles();
		if (files!=null) {
			byte[] buffer = new byte[8192];
			int len;
			
			for(File subfile : files) {
				if (subfile!=null) {
					if (subfile.isDirectory()) {
						zip(log, subfile, baseDir, zos, innerBaseDir);
					}
					else {
						String fullPath = subfile.getAbsolutePath().substring(baseDir.length());
						
						String entryName = mergeNames(innerBaseDir, fullPath);
						
						FileInputStream in = new FileInputStream(subfile);
						try {
							ZipEntry entry = new ZipEntry(entryName);
							zos.putNextEntry(entry);
							while ((len = in.read(buffer))>0) {
								zos.write(buffer, 0, len);
							}
						}
						finally {
							in.close();
						}
					}
				}
			}
		}
	}

}
