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
package org.janusproject.demos.acl.cnp.role;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.acl.Performative;
import org.janusproject.acl.protocol.ProtocolResult;
import org.janusproject.acl.protocol.cnp.FipaContractNetProtocol;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * 
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ContractNetBroker extends Role {

	private State state;
	private FipaContractNetProtocol contractNetProtocol;
	private ProtocolResult callForProposal;

	@Override
	public Status live() {
		this.state = HandleRequest();
		return StatusFactory.ok(this);
	}

	@Override
	public Status activate(Object... parameters) {

		for (Object parameter : parameters) {
			if (parameter instanceof FipaContractNetProtocol) {
				this.contractNetProtocol = (FipaContractNetProtocol) parameter;
				this.state = State.WAITING_CALL_FOR_PROPOSAL;
				return StatusFactory.ok(this);
			}
		}

		return StatusFactory.cancel(this);
	}

	private State HandleRequest() {

		if (this.contractNetProtocol.hasFailed()) {
			leaveMe();
		}

		switch (this.state) {
		case WAITING_CALL_FOR_PROPOSAL: {
			this.callForProposal = this.contractNetProtocol.getCallForProposal();

			if (this.callForProposal != null) {
				log(Locale.getString("PROPOSALRECEIVED")); //$NON-NLS-1$
				return State.SENDING_PROPOSAL;

				// contractNetProtocol.notUnderstood("?");
				// return State.DONE;
			}

			return State.WAITING_CALL_FOR_PROPOSAL;
		}
		case SENDING_PROPOSAL: {
			if (this.callForProposal.getContent().toString().equalsIgnoreCase(Locale.getString("TASKDESC"))) { //$NON-NLS-1$
				this.contractNetProtocol.propose(Locale.getString("PROPOSEARG")); //$NON-NLS-1$
				log(Locale.getString("PROPOSALSENT")); //$NON-NLS-1$
				return State.WAITING_PROPOSAL_ANSWER;
			}
			this.contractNetProtocol.refuse(Locale.getString("REFUSE")); //$NON-NLS-1$
			log(Locale.getString("REFUSESENT")); //$NON-NLS-1$
			return State.DONE;
		}
		case WAITING_PROPOSAL_ANSWER: {
			ProtocolResult result = this.contractNetProtocol.getAnswerToCallForProposal();

			if (result != null) {

				if (result.getPerformative().compareTo(Performative.ACCEPT_PROPOSAL) == 0) {
					log(Locale.getString("ACCEPTRECEIVED")); //$NON-NLS-1$
					return State.SENDING_RESULT;
				} else if (result.getPerformative().compareTo(Performative.REJECT_PROPOSAL) == 0) {
					log(Locale.getString("REJECTRECEIVED")); //$NON-NLS-1$
					return State.REJECTED;
				}
			}

			return State.WAITING_PROPOSAL_ANSWER;
		}
		case SENDING_RESULT: {
			// contractNetProtocol.informDone("task completed");
			this.contractNetProtocol.informResult(Locale.getString("END")); //$NON-NLS-1$
			// contractNetProtocol.failure("task failed");

			log(Locale.getString("RESULTSENT")); //$NON-NLS-1$

			return State.DONE;
		}
		case REJECTED: {
			log(Locale.getString("REJECTED")); //$NON-NLS-1$
			leaveMe();
			return this.state;
		}
		case DONE: {
			log(Locale.getString("DONE")); //$NON-NLS-1$
			leaveMe();
			return this.state;
		}
		case CANCELED:
		case CANCELING:
		case NOT_STARTED:
		default:
			return this.state;
		}
	}

	private void log(String str) {
		Logger.getAnonymousLogger().log(Level.INFO, "[" + getPlayer() + "] : " + str); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private enum State {
		NOT_STARTED, 
		WAITING_CALL_FOR_PROPOSAL, 
		SENDING_PROPOSAL, 
		WAITING_PROPOSAL_ANSWER, 
		SENDING_RESULT, 
		CANCELING, 
		CANCELED, 
		REJECTED, 
		DONE;
	}
}
