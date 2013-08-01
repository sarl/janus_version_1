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
package org.janusproject.kernel.mailbox;


/**
 * A buffered mailbox provides mean to manage a message distribution 
 * adapted for application based essentially on non-threaded agents
 * i.e. simulation especially simulation based on Influence/Reaction Model.
 * <p>
 * The message is not directly add to the inbox but
 * in a temporary box. And when all non-threaded 
 * agents are scheduled, messages are put back in the inbox. 
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @mavenartifactid mailbox
 */
public interface BufferedMailbox extends Mailbox {

	/**
	 * Replies if the temp buffer is empty or not.
	 * 
	 * @return <code>true</code> if no message is currently buffered,
	 * otherwise <code>false</code>
	 */
	public boolean isBufferEmpty();
			
	/**
	 * Clear the temp buffer.
	 */
	public void clearBuffer();

	/**
	 * Replies the buffer size.
	 * 
	 * @return count of mails in the buffer list.
	 */
	public int getBufferSize();

	/** Synchronize the buffer and the inbox.
	 * <p>
	 * Put back all mails in the buffer into the inbox.
	 */
	public void synchronizeMessages();
	
}
