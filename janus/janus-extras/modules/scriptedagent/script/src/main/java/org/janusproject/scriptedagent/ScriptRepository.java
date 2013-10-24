/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.janusproject.scriptedagent;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.arakhne.afc.vmutil.FileSystem;
import org.janusproject.scriptedagent.exception.InvalidDirectoryException;

/**
 * This class allows to check the validity of the folder which should
 * containing Jython scripts, and to get some informations about the folder content.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class ScriptRepository {

	/**
	 * The folder supported by this repository.
	 */
	private final SortedSet<URL> directories = new TreeSet<URL>(new Comparator<URL>() {
		@Override
		public int compare(URL o1, URL o2) {
			if (o1==o2) return 0;
			if (o1==null) return Integer.MIN_VALUE;
			if (o2==null) return Integer.MAX_VALUE;
			return o1.toExternalForm().compareTo(o2.toExternalForm());
		}
	});

	/**
	 * Creates a new directory finder.
	 */
	public ScriptRepository() {
		//
	}

	/**
	 * Creates a new directory finder.
	 * 
	 * @param directory is the path to the directory containing Jython Scripts
	 */
	public ScriptRepository(File directory) {
		addDirectory(directory);
	}

	/**
	 * Creates a new directory finder.
	 * 
	 * @param directory is the path to the directory containing Jython Scripts
	 */
	public ScriptRepository(URL directory) {
		addDirectory(directory);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.directories.toString();
	}
	
	/** Replies if this repository contains a least one directory.
	 * 
	 * @return <code>false</code> if this repository contains a directory;
	 * otherwise <code>true</code>.
	 */
	public boolean isEmpty() {
		return this.directories.isEmpty();
	}
	
	/** Clear the repository.
	 */
	public void clear() {
		this.directories.clear();
	}
	
	/** Replies the number of directories registered in this repository.
	 * 
	 * @return the number of directories registered in this repository.
	 */
	public int size() {
		return this.directories.size();
	}

	/** Add the given directory into the list of the managed directories.
	 * 
	 * @param directory
	 */
	public void addDirectory(File directory) {
		URL u = FileSystem.convertFileToURL(directory);
		if (directory==null || !directory.isDirectory()) {
			throw new InvalidDirectoryException(u);
		}
		addDirectory(u);
	}

	/** Add the given directory into the list of the managed directories.
	 * 
	 * @param directory
	 */
	public void addDirectory(URL directory) {
		if (directory==null) {
			throw new InvalidDirectoryException(directory);
		}
		this.directories.add(directory);
	}

	/** Remove the given directory from the list of the managed directories.
	 * 
	 * @param directory
	 */
	public void removeDirectory(File directory) {
		if (directory!=null) {
			URL u = FileSystem.convertFileToURL(directory);
			removeDirectory(u);
		}
	}

	/** Remove the given directory from the list of the managed directories.
	 * 
	 * @param directory
	 */
	public void removeDirectory(URL directory) {
		if (directory!=null) {
			this.directories.remove(directory);
		}
	}

	/**
	 * Return files in the repository that are matching the given file filter.
	 * Note that the replied files are locale to the operating system, ie. they
	 * are files.
	 * If the repository
	 * is empty, the default directory is replied in the
	 * iterator iff it is matching the specified filter.
	 * 
	 * @param filter is the object that is filtering the names.
	 * @return all the directories in the repository.
	 * @see #getDirectories(ScriptFileFilter)
	 * @see #getDirectories()
	 */
	public Iterator<File> getLocalDirectories(ScriptFileFilter filter) {
		if (isEmpty()) {
			File defaultDirectory = getDefaultLocalDirectory();
			if (defaultDirectory!=null && filter.accept(defaultDirectory)) {
				return Collections.singleton(defaultDirectory).iterator();
			}
			return Collections.<File>emptyList().iterator();
		}
		return new FileIterator(filter, this.directories.iterator());
	}
	
	/**
	 * Return files in the repository.
	 * Note that the replied files are locale to the operating system, ie. they
	 * are files.
	 * If the repository
	 * is empty, the default directory is replied in the
	 * iterator.
	 * 
	 * @return all the directories in the repository.
	 * @see #getDirectories(ScriptFileFilter)
	 * @see #getDirectories()
	 */
	public Iterator<File> getLocalDirectories() {
		if (isEmpty()) {
			return Collections.singleton(getDefaultLocalDirectory()).iterator();
		}		
		return new FileIterator(null, this.directories.iterator());
	}

	/**
	 * Return files in the repository that are matching the given file filter.
	 * If the repository
	 * is empty, the default directory is replied in the
	 * iterator iff it is matching the specified filter.
	 * 
	 * @param filter is the object that is filtering the names.
	 * @return all the directories in the repository.
	 * @see #getLocalDirectories(ScriptFileFilter)
	 * @see #getDirectories()
	 */
	public Iterator<URL> getDirectories(ScriptFileFilter filter) {
		if (isEmpty()) {
			URL defaultDirectory = getDefaultDirectory();
			if (defaultDirectory!=null && filter.accept(defaultDirectory)) {
				return Collections.singleton(defaultDirectory).iterator();
			}
			return Collections.<URL>emptyList().iterator();
		}
		return new URLIterator(filter, this.directories.iterator());
	}
	
	/**
	 * Return the default directory which may be used if thisw repository is empty.
	 * 
	 * @return the default directory.
	 */
	public static File getDefaultLocalDirectory() {
		try {
			return FileSystem.getUserHomeDirectory();
		}
		catch (FileNotFoundException e) {
			throw new Error(e);
		}
	}

	/**
	 * Return the default directory which may be used if thisw repository is empty.
	 * 
	 * @return the default directory.
	 */
	public static URL getDefaultDirectory() {
		File file = getDefaultLocalDirectory();
		return FileSystem.convertFileToURL(file);
	}

	/**
	 * Return files in the repository.
	 * If the repository
	 * is empty, the default directory is replied in the
	 * iterator.
	 * 
	 * @return all the directories in the repository.
	 * @see #getDirectories(ScriptFileFilter)
	 * @see #getLocalDirectories(ScriptFileFilter)
	 */
	public Iterator<URL> getDirectories() {
		if (isEmpty())
			return Collections.singleton(getDefaultDirectory()).iterator();
		return this.directories.iterator();
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 0.5
	 */
	private static class URLIterator implements Iterator<URL> {
		
		private final ScriptFileFilter filter;
		private final Iterator<URL> iterator;
		private URL next;
		
		/**
		 * @param filter
		 * @param iterator
		 */
		public URLIterator(ScriptFileFilter filter, Iterator<URL> iterator) {
			this.filter = filter;
			this.iterator = iterator;
			searchNext();
		}
		
		private void searchNext() {
			URL n;
			this.next = null;
			while (this.next==null && this.iterator.hasNext()) {
				n = this.iterator.next();
				assert(n!=null);
				if (this.filter.accept(n)) {
					this.next = n;
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.next != null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public URL next() {
			URL n = this.next;
			if (n==null) throw new NoSuchElementException();
			searchNext();
			return n;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}	

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 0.5
	 */
	private static class FileIterator implements Iterator<File> {
		
		private final ScriptFileFilter filter;
		private final Iterator<URL> iterator;
		private File next;
		
		/**
		 * @param filter
		 * @param iterator
		 */
		public FileIterator(ScriptFileFilter filter, Iterator<URL> iterator) {
			this.filter = filter;
			this.iterator = iterator;
			searchNext();
		}
		
		private void searchNext() {
			URL n;
			File f;
			this.next = null;
			while (this.next==null && this.iterator.hasNext()) {
				n = this.iterator.next();
				assert(n!=null);
				try {
					f = FileSystem.convertURLToFile(n);
				}
				catch(Throwable _) {
					f = null;
				}
				if (f!=null && (this.filter==null || this.filter.accept(f))) {
					this.next = f;
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.next != null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public File next() {
			File n = this.next;
			if (n==null) throw new NoSuchElementException();
			searchNext();
			return n;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}	

}
