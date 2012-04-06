/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-12 Janus Core Developers
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

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agentsignal.Signal;

/**
 * This signal is fired when a private message may be sent to the chat user.
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SendPrivateTextSignal extends Signal {

	private static final long serialVersionUID = 6435912232698611048L;
	
	private final String text;
	private final AgentAddress agent;
	
	/**
	 * @param source is the emitter of the signal
	 * @param agent is the address of the chatroom.
	 * @param text is the text to sent.
	 */
	public SendPrivateTextSignal(Object source, AgentAddress agent, String text) {
		super(source);
		this.text = text;
		this.agent = agent;
	}

	/** Replies the address of the agent.
	 * 
	 * @return the chatroom.
	 */
	public AgentAddress getAgent() {
		return this.agent;
	}

	/** Replies the text to send.
	 * 
	 * @return the text.
	 */
	public String getText() {
		return this.text;
	}

}
