/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010 Janus Core Developers
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
package org.janusproject.demos.simulation.foragerbots.message;

import org.janusproject.kernel.message.Message;

/**
 * Registration ack message.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RegistrationAckMessage extends Message {
	
	/**
	 */
	private static final long serialVersionUID = 6550124686953998962L;

	private final int baseX;
	private final int baseY;
	
	/**
	 * @param baseX is the x-coordinate of the bot base.
	 * @param baseY is the y-coordinate of the bot base.
	 */
	public RegistrationAckMessage(int baseX, int baseY) {
		this.baseX = baseX;
		this.baseY = baseY;
	}
	
	/** Replies the x-coordinate of the bot base.
	 * 
	 * @return the x-coordinate of the bot base.
	 */
	public int getBaseX() {
		return this.baseX;
	}
	
	/** Replies the y-coordinate of the bot base.
	 * 
	 * @return the y-coordinate of the bot base.
	 */
	public int getBaseY() {
		return this.baseY;
	}

}
