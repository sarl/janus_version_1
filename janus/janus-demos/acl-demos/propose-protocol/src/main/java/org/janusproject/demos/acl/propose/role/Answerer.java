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
import org.janusproject.acl.protocol.ProtocolResult;
import org.janusproject.acl.protocol.propose.FipaProposeProtocol;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * Answerer.
 * 
 * @author $Author: bfeld$
 * @author $Author: ngrenie$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class Answerer extends Role {

	private State state;
	private FipaProposeProtocol proposeProtocol;
	private ProtocolResult proposal;
	
	@Override
	public Status live() {
		this.state = HandlePropose();
		return StatusFactory.ok(this);
	}
	
	@Override
	public Status activate(Object... parameters) {
		
		for (Object parameter : parameters) {
			if (parameter instanceof FipaProposeProtocol) {
				this.proposeProtocol = (FipaProposeProtocol) parameter;
				this.state = State.WAITING_PROPOSAL;
				return StatusFactory.ok(this);
			}
		}
		
		return StatusFactory.cancel(this);
	}
	
	private State HandlePropose() {
		
		if (this.proposeProtocol.hasFailed() ){
			leaveMe();
		}
		
		switch (this.state) 
		{
			case WAITING_PROPOSAL: 
			{
				this.proposal = this.proposeProtocol.getPropose();
				
				if( this.proposal != null ){
					log(Locale.getString("PROPOSALRECEIVED")); //$NON-NLS-1$
					return State.SENDING_ANSWER;
				}
				
				return State.WAITING_PROPOSAL;
			}
			case SENDING_ANSWER:
			{
				System.out.println(this.proposal.getContent().toString().equalsIgnoreCase(Locale.getString("PROPOSALCONTENT"))); //$NON-NLS-1$
				if( this.proposal.getContent().toString().equalsIgnoreCase(Locale.getString("PROPOSALCONTENT"))) { //$NON-NLS-1$
					this.proposeProtocol.accept(Locale.getString("AGREE")); //$NON-NLS-1$
					log(Locale.getString("AGREEMENTSENT")); //$NON-NLS-1$
					return State.DONE;
				}
				this.proposeProtocol.reject(Locale.getString("REFUSE")); //$NON-NLS-1$
				log(Locale.getString("REFUSALSENT")); //$NON-NLS-1$
				return State.DONE;
			}
			case DONE:
			{
				log(Locale.getString("FINALIZATION")); //$NON-NLS-1$
				this.proposal = null;
				leaveMe();
				return this.state;
			}
		default:
			return this.state;
		}
	}
	
	private void log(String str){
		Logger.getAnonymousLogger().log(Level.INFO, "[" + getPlayer() + "] : " + str); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private enum State{
		WAITING_PROPOSAL,
		SENDING_ANSWER,
		DONE;
	}
}
