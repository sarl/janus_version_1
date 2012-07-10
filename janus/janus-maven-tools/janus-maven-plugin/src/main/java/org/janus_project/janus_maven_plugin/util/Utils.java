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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * General utilities.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Utils {
	/**
	 * name of the {@code Bundle-SymbolicName} tag
	 */
	public static final String BUNDLE_SYM_NAME = "Bundle-SymbolicName"; //$NON-NLS-1$

	/**
	 * Config property file name
	 */
	public static final String CONFIG_FILE_EXT = ".japp.properties"; //$NON-NLS-1$

	/**
	 * Replies the dependency files for an OSGi module.
	 * 
	 * @param log
	 *            - logger used to output errors if any
	 * @param artifacts
	 *            - the set of artificats, we wants to determine their dependencies
	 * @param excludedArtifacts  - the list of excluded artifacts
	 * @return a list of file correspodning to the dependencies of the specfied set of artifacts
	 */
	public static Set<File> getOsgiDependencyFiles(Log log, Set<Artifact> artifacts, List<String> excludedArtifacts) {
		Set<File> result = new HashSet<File>();
		Iterator<Artifact> i = artifacts.iterator();
		Artifact a;
		
		if (excludedArtifacts != null && excludedArtifacts.size() > 0) {
			while (i.hasNext()) {
				a = i.next();
				if (isOSGiBundle(a.getFile())) {
					if (!excludedArtifacts.contains(a.getArtifactId())) {// verify if the dependencies is not in the excluded artifiacts
						result.add(a.getFile());
						log.warn("Copying "+ a.toString()); //$NON-NLS-1$ 
					} else {
						log.warn("Excluding "+ a.toString()); //$NON-NLS-1$ 
					}
				} else {
					StringBuilder buffer = new StringBuilder();
					buffer.append("Ignoring Artifact ["); //$NON-NLS-1$
					buffer.append(a.getGroupId());
					buffer.append(":"); //$NON-NLS-1$
					buffer.append(a.getArtifactId());
					buffer.append(":"); //$NON-NLS-1$
					buffer.append(a.getVersion());
					buffer.append("] because it does not contain a Manifest. It can not be an OSGi bundle."); //$NON-NLS-1$
					log.warn(buffer.toString());
				}
			}
		} else {
			while (i.hasNext()) {
				a = i.next();
				if (isOSGiBundle(a.getFile())) {
					result.add(a.getFile());
				} else {
					StringBuilder buffer = new StringBuilder();
					buffer.append("Ignoring Artifact ["); //$NON-NLS-1$
					buffer.append(a.getGroupId());
					buffer.append(":"); //$NON-NLS-1$
					buffer.append(a.getArtifactId());
					buffer.append(":"); //$NON-NLS-1$
					buffer.append(a.getVersion());
					buffer.append("] because it does not contain a Manifest. It can not be an OSGi bundle."); //$NON-NLS-1$
					log.warn(buffer.toString());
				}
			}
		}

		return result;
	}

	/**
	 * Ensure that the given file is a valid OSGi bundle.
	 * 
	 * @param file
	 * @return <code>true</code> if the given file is an OSGi bundle, otherwise <code>false</code>.
	 */
	public static boolean isOSGiBundle(File file) {
		try {
			JarFile jFile = new JarFile(file);
			try {
				// Get the manifest
				Manifest manifest = jFile.getManifest();
	
				Attributes attrs = manifest.getMainAttributes();
	
				String name = attrs.getValue(BUNDLE_SYM_NAME);
	
				if (name != null) {
					return true;
				}
			}
			catch (IOException e) {
				//
			}
			finally {
				jFile.close();
			}
		}
		catch (IOException e) {
			//
		}
		return false;
	}

	/**
	 * Ensure that the given file is a valid OSGi directory, ie a exploded bundle.
	 * 
	 * @param file
	 * @return <code>true</code> if the given file is an OSGi bundle, otherwise <code>false</code>.
	 */
	public static boolean isOSGiExplodedBundle(File file) {
		if (file.isDirectory()) {
			try {
				File manifestFile = new File(new File(file, "META-INF"), "MANIFEST.MF"); //$NON-NLS-1$ //$NON-NLS-2$
				if (manifestFile.canRead()) {
					// Get the manifest
					FileInputStream fis = new FileInputStream(manifestFile);
					try {
						Manifest manifest = new Manifest(fis);

						Attributes attrs = manifest.getMainAttributes();
	
						String name = attrs.getValue(BUNDLE_SYM_NAME);
	
						if (name != null) {
							return true;
						}
					}
					finally {
						fis.close();
					}
				}
			} catch (IOException e) {
				//
			}
		}
		return false;
	}

	/**
	 * Write the content of a directory into a file.
	 * 
	 * @param dir
	 *            - source directory
	 * @param dst
	 *            - output file
	 * @param eraseExistingContent
	 *            indicates if the existing content should be erased. If <code>true</code> the previous content is lost, if <code>false</code> the previous content is put back at the begining of the new file.
	 * @throws IOException
	 */
	public static void merge(File dir, File dst, boolean eraseExistingContent) throws IOException {
		String[] children = dir.list();
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return (name.endsWith(CONFIG_FILE_EXT));
			}
		};

		int len;
		byte[] buf = new byte[1024];

		StringBuilder oldContent = new StringBuilder();
		if (!eraseExistingContent && dst.exists()) {
			FileInputStream in = new FileInputStream(dst);
			try {
				while ((len = in.read(buf)) > 0) {
					oldContent.append(new String(buf, 0, len));
				}
			}
			finally {
				in.close();
			}
			if (oldContent.length() > 0) {
				oldContent.append("\n"); //$NON-NLS-1$
			}
		}

		OutputStream out = new FileOutputStream(dst);
		try {

			children = dir.list(filter);
			Arrays.sort(children);
	
			out.write(oldContent.toString().getBytes());
	
			for (String child : children) {
				FileInputStream in = new FileInputStream(new File(dir, child));
				try {
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
				}
				finally {
					in.close();
				}
			}

		}
		finally {
			out.close();
		}
	}

	/**
	 * Replies the name of the jar file, which is the target file of the given artifact.
	 * 
	 * @param a
	 * @return the target filename.
	 */
	public static String getArtifactTargetName(Artifact a) {
		return getArtifactTargetName(a, null);
	}

	/**
	 * Replies the name of the jar file, which is the target file of the given artifact.
	 * 
	 * @param a
	 * @param tag
	 * @return the target filename.
	 */
	public static String getArtifactTargetName(Artifact a, String tag) {
		StringBuilder b = new StringBuilder();
		b.append(a.getArtifactId());
		b.append("-"); //$NON-NLS-1$
		b.append(a.getVersion());
		/*
		 * if (a.hasClassifier()) { not available in aether b.append("-"); //$NON-NLS-1$ b.append(a.getClassifier()); }
		 */
		if (tag != null && !"".equals(tag)) { //$NON-NLS-1$
			b.append(":"); //$NON-NLS-1$
			b.append(tag);
		}
		b.append(".jar"); //$NON-NLS-1$
		return b.toString();
	}

	/**
	 * Build an mojo exception with the given parameter.
	 * <p>
	 * <b>IMPORTANT: </b> this function never returns.
	 * 
	 * @param e
	 *            is the exception to forward.
	 * @param message
	 *            is the parts of the message.
	 * @throws MojoExecutionException
	 */
	public static void createMojoException(Throwable e, Object... message) throws MojoExecutionException {
		StringBuilder buffer = new StringBuilder();
		for (Object o : message) {
			if (o != null) {
				buffer.append(o.toString());
			}
		}
		if (e != null) {
			fillStackTrace(buffer, e);
		}
		throw new MojoExecutionException(buffer.toString(), e);
	}

	private static void fillStackTrace(StringBuilder buffer, Throwable e) {
		buffer.append("\n"); //$NON-NLS-1$
		buffer.append(e.toString());
		for (StackTraceElement elt : e.getStackTrace()) {
			buffer.append("\n\tat "); //$NON-NLS-1$
			buffer.append(elt.toString());
		}

		Throwable cause = e.getCause();
		if (cause != null) {
			fillStackTrace(buffer, cause);
		}
	}

	/**
	 * Create an empty touch.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void touchFile(File file) throws IOException {
		FileOutputStream s = new FileOutputStream(file);
		try {
			s.write(file.toString().getBytes());
		}
		finally {
			s.close();
		}
	}

}
