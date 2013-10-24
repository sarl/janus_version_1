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
import org.janusproject.acl.Performative;
import org.janusproject.acl.protocol.ProtocolResult;
import org.janusproject.acl.protocol.ProtocolState;
import org.janusproject.acl.protocol.request.FipaRequestProtocol;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/** Role that is requesting.
 *  
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class Requester extends Role {
	
	private FipaRequestProtocol requestProtocol;
	private ProtocolResult answer;
	private ProtocolResult result;
	
	private State state;
	
	@Override
	public Status live() {
		this.state = Request();
		return StatusFactory.ok(this);
	}
	
	@Override
	public Status activate(Object... parameters) {
		
		for (Object parameter : parameters) {
			if (parameter instanceof FipaRequestProtocol) {
				this.requestProtocol = (FipaRequestProtocol) parameter;
				this.state = State.SENDING_REQUEST;
				return StatusFactory.ok(this);
			}
		}
		
		return StatusFactory.cancel(this);
	}
	
	private State Request() {
		
		if (this.requestProtocol.hasFailed() ){
			leaveMe();
		}
		
		switch (this.state) {
			case SENDING_REQUEST: 
			{	
				//requestProtocol.request( "evil" );
				this.requestProtocol.request(Locale.getString("REQUEST")); //$NON-NLS-1$
				
				//requestProtocol.cancel("@++");
				
				log(Locale.getString("REQUESTSENT")); //$NON-NLS-1$
				
				return State.WAITING_ANSWER;
			}
			case WAITING_ANSWER:
			{
				this.answer = this.requestProtocol.getAnswer();
				
				if( this.answer != null ) {
					log(Locale.getString("ANSWERRECEIVED")); //$NON-NLS-1$
					
					if (this.answer.getPerformative().compareTo(Performative.REFUSE) == 0)
						return State.REFUSED;
					
					return State.WAITING_RESULT;
				}
				return this.state;
			}
			case WAITING_RESULT:
			{
				this.result = this.requestProtocol.getResult();
				
				if (this.result != null ) {
					log(Locale.getString("RESULTRECEIVED")); //$NON-NLS-1$
					return State.DONE;
				}
				return this.state;

			}
			case DONE:
			{
				this.answer = null;
				this.result = null;
				leaveMe();
				return this.state;
			}
			
		case CANCELED:
		case CANCELING:
		case REFUSED:
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
		WAITING_RESULT,
		DONE,
		CANCELING,
		CANCELED,
		REFUSED;
	}

}
