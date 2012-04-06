/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010, 2012 Janus Core Developers
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
package org.janusproject.demos.network.januschat.organization;

import org.janusproject.kernel.agentsignal.Signal;
import org.janusproject.kernel.crio.core.GroupAddress;

/**
 * This signal is fired when an error has occured in the chatter role.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ErrorSignal extends Signal {

	private static final long serialVersionUID = 5720204198045531323L;
	
	private final Throwable error;
	private final GroupAddress chatroom;
	
	/**
	 * @param source is the emitter of the signal
	 * @param chatroom is the address of the chatroom.
	 * @param error is the error.
	 */
	public ErrorSignal(Object source, GroupAddress chatroom, Throwable error) {
		super(source);
		this.error = error;
		this.chatroom = chatroom;
	}

	/** Replies the address of the chatroom.
	 * 
	 * @return the chatroom.
	 */
	public GroupAddress getChatRoom() {
		return this.chatroom;
	}

	/** Replies the error in the chatroom.
	 * 
	 * @return the error.
	 */
	public Throwable getError() {
		return this.error;
	}

}
