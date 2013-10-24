/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010 Janus Core Developers
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
package org.janusproject.demos.simulation.boids.util;

/**
 * General configuration constants for the Boids simulation.
 *
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Settings {
	
	/**
     * May wall collision be avoid?
     */
	public static final boolean WALL_AVOIDENCE   =  false;
		
	/**
     * Distance under which obstacles are taken into account
     * by boids.
     */
	public  static final float OBSTACLE_DISTANCE  =  20f;
	
	/**
     * Adapation factor for obstacles in boids's force computation.
     */
    public  static final float OBSTACLE_FORCE  =    80.0f;
    
	/**
	 * Demi-width of the environment.
	 */
    public static final int ENVIRONMENT_DEMI_WIDTH = 200;
    
	/**
	 * Demi-height of the environment.
	 */
    public static final int ENVIRONMENT_DEMI_HEIGHT = 100;
    
	/**
	 * Count of shadow entities.
	 */
    public static final int SHADOW_ENTITY_COUNT = 30;

	/**
	 * Radius inside which all members of a shadow group
	 * are located from the center of the group.
	 */
    public static final int SHADOW_GROUP_RADIUS = 50;

}
