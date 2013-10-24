/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.janusproject.demos.acl.propose.role;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.acl.Performative;
import org.janusproject.acl.protocol.ProtocolResult;
import org.janusproject.acl.protocol.ProtocolState;
import org.janusproject.acl.protocol.propose.FipaProposeProtocol;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * Requester.
 * 
 * @author $Author: bfeld$
 * @author $Author: ngrenie$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class Requester extends Role {
	
	private FipaProposeProtocol proposeProtocol;
	private ProtocolResult answer;
	private State state;
	
	@Override
	public Status live() {
		this.state = Propose();
		return StatusFactory.ok(this);
	}
	
	@Override
	public Status activate(Object... parameters) {
		
		for (Object parameter : parameters) {
			if (parameter instanceof FipaProposeProtocol) {
				this.proposeProtocol = (FipaProposeProtocol) parameter;
				this.state = State.SENDING_REQUEST;
				return StatusFactory.ok(this);
			}
		}
		
		return StatusFactory.cancel(this);
	}
	
	private State Propose() {
		
		if (this.proposeProtocol.hasFailed() ){
			leaveMe();
		}
		
		switch (this.state) {
			case SENDING_REQUEST: 
			{	
				this.proposeProtocol.propose(Locale.getString("PROPOSAL")); //$NON-NLS-1$
				
				log(Locale.getString("PROPOSALSENT")); //$NON-NLS-1$
				
				return State.WAITING_ANSWER;
			}
			case WAITING_ANSWER:
			{
				this.answer = this.proposeProtocol.getAnswer();
				
				if( this.answer != null ) {
					log(Locale.getString("ANSWERRECEIVED")); //$NON-NLS-1$
					
					if (this.answer.getPerformative().compareTo(Performative.REFUSE) == 0) {
						return State.REJECTED;
					}
					
					return State.ACCEPTED;
				}
				return this.state;
			}
			case REJECTED:
			{
				log(Locale.getString("PROPOSALREJECTED")); //$NON-NLS-1$
				this.answer = null;
				leaveMe();
				return this.state;	
			}
			case ACCEPTED:
			{
				log(Locale.getString("PROPOSALACCEPTED")); //$NON-NLS-1$
				this.answer = null;
				leaveMe();
				return this.state;	
			}
		case DONE:
		default:
			return this.state;
		}
	}	
	
	private void log(String str){
		Logger.getAnonymousLogger().log(Level.INFO, "[" + getPlayer() + "] : " + str); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private enum State implements ProtocolState {
		SENDING_REQUEST,
		WAITING_ANSWER,
		DONE,
		REJECTED,
		ACCEPTED;
	}

}
