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
package org.janusproject.kernel.organization.holonic.merging.predefined;

import org.janusproject.kernel.organization.holonic.merging.StandAlone;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * Predefined definition of the Standalone role.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PredefinedStandAlone extends StandAlone {

	private int current = 1; // the current state

	/**
	 */
	public PredefinedStandAlone() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Status live() {
		this.current = Run();
		return StatusFactory.ok(this);
	}

	/**
	 * @return the state of the role.
	 */
	private int Run() {
		switch (this.current) {
		default:
			return 0;
		}
	}
}
