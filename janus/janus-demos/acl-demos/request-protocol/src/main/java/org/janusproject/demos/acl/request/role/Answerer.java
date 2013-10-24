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
package org.janusproject.demos.acl.request.role;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.acl.protocol.ProtocolResult;
import org.janusproject.acl.protocol.request.FipaRequestProtocol;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/** Role that is answerer.
 *  
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class Answerer extends Role {

	private State state;
	private FipaRequestProtocol requestProtocol;
	private ProtocolResult request;
	
	@Override
	public Status live() {
		this.state = HandleRequest();
		return StatusFactory.ok(this);
	}
	
	@Override
	public Status activate(Object... parameters) {
		
		for (Object parameter : parameters) {
			if (parameter instanceof FipaRequestProtocol) {
				this.requestProtocol = (FipaRequestProtocol) parameter;
				this.state = State.WAITING_REQUEST;
				return StatusFactory.ok(this);
			}
		}
		
		return StatusFactory.cancel(this);
	}
	
	private State HandleRequest() {
		
		if (this.requestProtocol.hasFailed() ){
			leaveMe();
		}
		
		switch (this.state) 
		{
			case WAITING_REQUEST: 
			{
				this.request = this.requestProtocol.getRequest();
				
				if( this.request != null ){
					log(Locale.getString("REQUESTRECEIVED")); //$NON-NLS-1$
					return State.SENDING_ANSWER;

				// requestProtocol.notUnderstood("?");
				// return State.DONE;
				}
				
				return State.WAITING_REQUEST;
			}
			case SENDING_ANSWER:
			{
				if( this.request.getContent().toString().equalsIgnoreCase(Locale.getString("REQUESTCONTENT"))) { //$NON-NLS-1$
					this.requestProtocol.agree(Locale.getString("AGREE")); //$NON-NLS-1$
					log(Locale.getString("AGREEMENTSENT")); //$NON-NLS-1$
					return State.SENDING_RESULT;
				}
				this.requestProtocol.refuse(Locale.getString("REFUSE")); //$NON-NLS-1$
				log(Locale.getString("REFUSALSENT")); //$NON-NLS-1$
				return State.DONE;
			}
			case SENDING_RESULT:
			{
				this.requestProtocol.informDone(Locale.getString("INFORMDONE")); //$NON-NLS-1$
				log(Locale.getString("RESULTSENT")); //$NON-NLS-1$
				return State.DONE;
			}
			case DONE:
			{
				log(Locale.getString("FINALIZATION")); //$NON-NLS-1$
				this.request = null;
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
		WAITING_REQUEST,
		SENDING_ANSWER,
		SENDING_RESULT,
		DONE;
	}
}
