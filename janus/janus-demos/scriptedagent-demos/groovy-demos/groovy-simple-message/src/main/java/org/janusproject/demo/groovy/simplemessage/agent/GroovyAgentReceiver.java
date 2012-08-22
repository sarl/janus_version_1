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
package org.janusproject.demo.groovy.simplemessage.agent;

import org.janusproject.demo.groovy.simplemessage.Launcher;
import org.janusproject.groovyengine.GroovyAgent;
import org.janusproject.kernel.status.Status;

/**
 * Simple agent waiting a message and sending an ACK
 * 
 * @author $Author: lcabasson$
 * @author $Author: cwintz$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class GroovyAgentReceiver extends GroovyAgent {
	private static final long serialVersionUID = 2261480592543737175L;

	@Override
	public Status live() {		
		Status s = super.live();
		if (s.isSuccess()) {
			runFunction(Launcher.RECEIVER_SCRIPT, "live", this); //$NON-NLS-1$
		}
		return s;
	}
	
}
