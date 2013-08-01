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
package org.janusproject.kernel.crio.interaction;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.janusproject.kernel.configuration.JanusProperties;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.mailbox.BufferedTreeSetMailbox;
import org.janusproject.kernel.mailbox.Mailbox;

/**
 * Utilities on mailboxes.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @mavenartifactid mailbox
 * @since 0.5
 */
public class MailboxUtil {

	/** Create a mailbox from the default configuration.
	 * 
	 * @param callerType is the type of the caller.
	 * @param configuration is the configuration of the caller.
	 * @param logger is the logger to use to log any error.
	 * @return the mailbox.
	 */
	public static Mailbox createDefaultMailbox(Class<?> callerType, JanusProperties configuration, Logger logger) {
		if (configuration!=null) {
			String className = null;
			if (Role.class.isAssignableFrom(callerType)) {
				className = configuration.getProperty(JanusProperty.JANUS_ROLE_MAILBOX_TYPE);
			}
			if (className==null) {
				className = configuration.getProperty(JanusProperty.JANUS_AGENT_MAILBOX_TYPE);
			}
			if (className!=null) {
				try {
					Class<?> type = Class.forName(className);
					return (Mailbox)type.newInstance();
				}
				catch(Throwable e) {
					if (logger!=null)
						logger.log(Level.SEVERE, e.toString(), e);
				}
			}
		}
		return new BufferedTreeSetMailbox();
	}

}
