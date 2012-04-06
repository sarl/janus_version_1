/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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
package org.janusproject.kernel.schedule;

/**
 * Define the action stages used by activator.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum ActivationStage {

	/** Initialization stage.
	 * It is just before live of object. Object may initialize
	 * values and attributes which may be initialize since the first
	 * instant of its life.
	 * Activation function is invoked once time.
	 */
	INITIALIZATION,
	
	/** Live stage.
	 * Object may do something.
	 * Activation function is invoked many times.
	 */
	LIVE,

	/** Destruction stage.
	 * It is just after live of object.
	 * Object may release resources.
	 * Activation function is invoked once time.
	 */
	DESTRUCTION;

}
