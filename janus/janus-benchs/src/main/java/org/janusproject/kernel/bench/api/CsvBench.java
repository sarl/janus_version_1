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
package org.janusproject.kernel.bench.api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.janusproject.kernel.util.sizediterator.SizedIterator;

/** This abstract class describes a bench for the Janus kernel with
 * all the results stored in a CSV file.
 * 
 * @param <R> is the type of the runs
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class CsvBench<R extends BenchRun> extends Bench<R> {

	private final File outputDirectory;
	private BufferedWriter writer;

	/**
	 * @param directory is the directory that shold contains the CSV file.
	 * @throws IOException
	 */
	public CsvBench(File directory) throws IOException {
		this.outputDirectory = directory;
		this.writer = null;
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() throws Exception {
		if (this.writer!=null) {
			this.writer.close();
			this.writer = null;
		}
		super.dispose();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final SizedIterator<R> setUpGroup(String benchFunctionName) throws Exception {
		if (this.writer!=null) {
			this.writer.close();
		}
		StringBuilder filename = new StringBuilder();
		filename.append(getClass().getSimpleName());
		filename.append("#"); //$NON-NLS-1$
		filename.append(benchFunctionName);
		filename.append(".csv"); //$NON-NLS-1$
		this.writer = new BufferedWriter(new FileWriter(new File(this.outputDirectory, filename.toString())));
		return setUpGroupWithCSV(benchFunctionName);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void logError(Logger logger, String benchFunctionName, Throwable e) {
		logger.log(Level.SEVERE, benchFunctionName.toLowerCase()+e.toString(), e);
		StringBuilder filename = new StringBuilder();
		filename.append(getClass().getSimpleName());
		filename.append("#"); //$NON-NLS-1$
		filename.append(benchFunctionName);
		filename.append(".log"); //$NON-NLS-1$
		try {
			PrintWriter s = new PrintWriter(new FileWriter(new File(this.outputDirectory, filename.toString())));
			try {
				s.println(e.toString());
				e.printStackTrace(s);
			}
			catch(Throwable _) {
				//
			}
			finally {
				s.close();
			}
		}
		catch(IOException _) {
			//
		}
	}

	/** Invoked to initialize a group of benchs.
	 * 
	 * @param benchFunctionName is the name of the bench function
	 * @return the iterator on the description of the benchs to run.
	 * @throws Exception
	 */
	protected abstract SizedIterator<R> setUpGroupWithCSV(String benchFunctionName) throws Exception;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDownGroup() throws Exception {
		if (this.writer!=null) {
			this.writer.close();
			this.writer = null;
		}
		super.tearDownGroup();
	}
	
	/** Write a record line in the CSV.
	 * @param columns
	 * @throws IOException
	 */
	protected void writeRecord(Object... columns) throws IOException {
		assert(this.writer!=null);
		for(int i=0; i<columns.length; ++i) {
			if (i>0) this.writer.write("\t"); //$NON-NLS-1$
			if (columns[i]!=null)
				this.writer.write(columns[i].toString());
		}
		this.writer.write("\n"); //$NON-NLS-1$
		this.writer.flush();
	}
	
	/** Write a header line in the CSV.
	 * @param columns
	 * @throws IOException
	 */
	protected void writeHeader(Object... columns) throws IOException {
		assert(this.writer!=null);
		for(int i=0; i<columns.length; ++i) {
			if (i>0) this.writer.write("\t"); //$NON-NLS-1$
			else this.writer.write("#"); //$NON-NLS-1$
			if (columns[i]!=null)
				this.writer.write(columns[i].toString());
		}
		this.writer.write("\n"); //$NON-NLS-1$
		this.writer.flush();
	}

}