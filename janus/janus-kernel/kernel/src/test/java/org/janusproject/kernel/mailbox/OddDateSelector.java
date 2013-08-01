/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.kernel.mailbox;

import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.util.selector.Selector;

/**
 * This class selects mails with odd date.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class OddDateSelector
implements Selector<Message> {

	/**
	 */
	public OddDateSelector() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSelected(Object msg) {
		return msg instanceof Message &&
				(((Message)msg).getCreationDate()%2)==1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<Message> getSupportedClass() {
		return Message.class;
	}

}
