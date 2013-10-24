/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-2012 Janus Core Developers
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
package org.janusproject.demo.jruby.simplemessage.agent;

import java.net.URL;

import org.arakhne.afc.vmutil.Resources;
import org.janusproject.jrubyengine.RubyAgent;
import org.janusproject.kernel.status.Status;

/**
 * Simple agent waiting a message and sending an ACK
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gui.vinson@gmail.com$
 * @author $Author: renaud.buecher@utbm.fr$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JRubyAgentReceiver extends RubyAgent {
	private static final long serialVersionUID = 2261480592543737175L;

	/** URL of the Ruby receiver script.
	 */
	public static final URL RECEIVER_SCRIPT = Resources.getResource(JRubyAgentReceiver.class, "receiver.rb"); //$NON-NLS-1$

	@Override
	public Status live() {		
		Status s = super.live();
		if (s.isSuccess()) {
			runFunction(RECEIVER_SCRIPT, "live", this); //$NON-NLS-1$
		}
		return s;
	}
	
}
