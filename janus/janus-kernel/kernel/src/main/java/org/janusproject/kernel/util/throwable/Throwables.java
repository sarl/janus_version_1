/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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
package org.janusproject.kernel.util.throwable;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Utilities on throwables.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.4
 */
public class Throwables {

	/**
	 * Replies a string representation of the given throwable object.
	 * 
	 * @param t is the object to parse.
	 * @return the string representation of <var>t</var>.
	 */
	public static String toString(Throwable t) {
		if (t==null) return ""; //$NON-NLS-1$
		StringWriter swriter = new StringWriter();

		PrintWriter pwriter = new PrintWriter(swriter);
		t.printStackTrace(pwriter);
		
		return swriter.toString();
	}
	
}
