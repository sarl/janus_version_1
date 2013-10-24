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
package org.janusproject.demos.jaak.pacman.channel;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.kernel.channels.Channel;

/** Channel to communicate with the pacman.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Player extends Channel {

	/** Move the interactive player on the given direction.
	 * 
	 * @param direction
	 */
	public void movePlayer(PlayerDirection direction);

	/** Replies the time to remain to be a super pacman.
	 * 
	 * @return the time to remain a super pacman in ms.
	 */
	public long getRemainPowerTime();

	/** Replies the position of the pacman.
	 * 
	 * @return the position of the pacman.
	 */
	public Point2i getPosition();

}