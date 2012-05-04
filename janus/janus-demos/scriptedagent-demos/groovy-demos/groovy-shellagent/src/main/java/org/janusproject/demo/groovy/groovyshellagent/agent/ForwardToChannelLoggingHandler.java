/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011 Janus Core Developers
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
package org.janusproject.demo.groovy.groovyshellagent.agent;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.janusproject.demo.groovy.groovyshellagent.agent.channel.LoggingChannel;

/**
 * Handler for logging API that forward logging message to a Janus channel
 * 
 * @author $Author: lcabasson$
 * @author $Author: cwintz$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ForwardToChannelLoggingHandler extends Handler {
	/**
	 * Janus channel to forward logging record to
	 */
	LoggingChannel logChannel;
	
	/**
	 * Creates a new handler that forward logging message to a janus channel
	 * @param lc Janus Channel to forward logging message to
	 */
	public ForwardToChannelLoggingHandler(LoggingChannel lc)
	{
		this.logChannel = lc;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws SecurityException {
		// nothing to do in particular
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void flush() {
		// nothing to do in particular
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void publish(LogRecord record) {
		this.logChannel.loggingMessageReceived(record);
	}

}
