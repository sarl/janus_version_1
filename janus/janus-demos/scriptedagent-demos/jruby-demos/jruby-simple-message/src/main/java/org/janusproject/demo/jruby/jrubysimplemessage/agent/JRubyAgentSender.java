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
package org.janusproject.demo.jruby.jrubysimplemessage.agent;

import org.janusproject.jrubyengine.JRubyAgent;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.status.Status;

/**
 * Simple agent sending a message
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gui.vinson@gmail.com$
 * @author $Author: renaud.buecher@utbm.fr$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JRubyAgentSender extends JRubyAgent{

	private static final long serialVersionUID = 2508734170719659042L;

	private static final String RubyScriptPath = JRubyAgentReceiver.class.getClassLoader().getResource("ruby/").getPath(); //$NON-NLS-1$
	
	/**
	 * Boolean specifying if the message was sent or not
	 */
	private boolean isMessageSended=false;
	
	/**
	 * the address of the agent to send the message
	 */
	private AgentAddress receiverAddress;

	/**
	 * 
	 * @param address - the address of the agent to send the message
	 */
	public JRubyAgentSender(AgentAddress address){
		super();
		this.receiverAddress = address;
	}

	@Override
	public Status live() {
		Status s = super.live();
		if (s.isSuccess()) {
			this.isMessageSended=(Boolean) runRubyFunction(RubyScriptPath,"sender.rb", "live", this.isMessageSended, this.receiverAddress,this); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return s;
	}

}
