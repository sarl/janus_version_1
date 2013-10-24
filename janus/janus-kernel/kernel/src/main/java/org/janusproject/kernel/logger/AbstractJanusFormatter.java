/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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
package org.janusproject.kernel.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.time.KernelTimeManager;

/**
 * This class provides a tuned formatter for log records.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractJanusFormatter
extends Formatter {

	private final KernelTimeManager timeManager;
	
	/**
	 * @param timeManager is the kernel time manager.
	 */
	public AbstractJanusFormatter(KernelTimeManager timeManager) {
		assert(timeManager!=null);
		this.timeManager = timeManager;
	}
	
	/** Replies the string which may be used to identify the formatter owner.
	 * 
	 * @return the formatter owner id.
	 */
	public abstract String getFormatterOwner();
	
	/** Format the current time for log message.
	 * 
	 * @return the formated time
	 */
	protected String formatDate() {
		Date dt = this.timeManager.getCurrentDate();
		if (dt==null) {
			float currentDate = this.timeManager.getCurrentTime();
			return Locale.getString(
					AbstractJanusFormatter.class,
					"INTEGER_DATE",  //$NON-NLS-1$
					Float.toString(currentDate));
		}
		return Locale.getString(
				AbstractJanusFormatter.class,
				"HUMAN_DATE", //$NON-NLS-1$
				dt);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String format(LogRecord record) {
		StringBuilder output = new StringBuilder();
		String message = record.getMessage();
		if (message==null || "".equals(message)) { //$NON-NLS-1$
			StringBuilder m = new StringBuilder();
			if (record.getSourceClassName() != null) {	
			    m.append(record.getSourceClassName());
			}
			else {
			    m.append(record.getLoggerName());
			}
			if (record.getSourceMethodName() != null) {	
			    m.append("#"); //$NON-NLS-1$
			    m.append(record.getSourceMethodName());
			}
			message = m.toString();
		}
		output.append(getLocalizedMessage(record, message));
		if (record.getThrown() != null) {
		    try {
		        StringWriter sw = new StringWriter();
		        PrintWriter pw = new PrintWriter(sw);
		        try {
		        	record.getThrown().printStackTrace(pw);
		        }
		        finally {
		        	pw.close();
		        }
				output.append(sw.toString());
		    }
			catch(AssertionError ae) {
				throw ae;
			}
		    catch (Exception ex) {
		    	//
		    }
		}
		output.append("\n"); //$NON-NLS-1$
		return output.toString();
	}
	
	/** Replies the localized message.
	 * 
	 * @param record is the record to log.
	 * @param message is the message to display
	 * @return the localized message.
	 */
	public abstract String getLocalizedMessage(LogRecord record, String message);
	
}
