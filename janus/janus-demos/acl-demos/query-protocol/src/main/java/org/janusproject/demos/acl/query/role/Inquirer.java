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
package org.janusproject.demos.acl.query.role;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.protocol.EnumFipaProtocol;
import org.janusproject.acl.protocol.FipaConversationManager;
import org.janusproject.acl.protocol.ProtocolState;
import org.janusproject.acl.protocol.query.FipaQueryProtocol;
import org.janusproject.acl.protocol.query.QueryProtocolState;
import org.janusproject.demos.acl.query.util.QueryInfo;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/** Role that is inquiring.
 * 
 * @author $Author: flacreus$
 * @author $Author: sroth$
 * @author $Author: cstentz$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class Inquirer extends Role {
	
	private FipaConversationManager conversationManager;
	private FipaQueryProtocol queryConversation;
	
	private QueryInfo queryInfo;
	
	@Override
	public Status activate(Object... parameters) {
		for (Object parameter : parameters) {
			if (parameter instanceof FipaConversationManager) {
				this.conversationManager = (FipaConversationManager) parameter;
			} else if (parameter instanceof  QueryInfo) {
				this.queryInfo = (QueryInfo) parameter;
			}
		}
		
		if (this.conversationManager != null && this.queryInfo != null) {
			return StatusFactory.ok(this);
		}
		return StatusFactory.cancel(this);
	}
	
	@Override
	public Status live() {		
		if (this.queryConversation == null) {
			AgentAddress answerer = getPlayer(Answerer.class);
			if (answerer != null) {
				this.queryConversation = (FipaQueryProtocol) this.conversationManager.createProtocol(EnumFipaProtocol.FIPA_QUERY);
				this.queryConversation.initiate(getPlayer(), getPlayer(Answerer.class));
			}
		} else {
			handleQuery();
		}
		
		return StatusFactory.ok(this);
	}
	
	private ProtocolState handleQuery() {
		switch ((QueryProtocolState) this.queryConversation.getState()) {
		case NOT_STARTED:
			this.queryConversation.query(createACLMessage(this.queryInfo));
			print(Locale.getString(Inquirer.class, "QUERY_SENT")); //$NON-NLS-1$
			break;

		case WAITING_ANSWER:
			ACLMessage answer = this.queryConversation.getAnswer();
			if (answer != null) {
				print(Locale.getString(Inquirer.class, "ANSWER_RECEIVED", answer.getSender().getUUID(), answer.toXML())); //$NON-NLS-1$
			}
			break;
			
		case WAITING_RESULT:
			ACLMessage result = this.queryConversation.getResult();
			if (result != null) {
				print(Locale.getString(Inquirer.class, "RESULT_RECEIVED", result.getSender().getUUID(), result.toXML())); //$NON-NLS-1$
			}
			break;
		
		case DONE:
			leaveMe();
			break;
			
		case CANCELED:
		case CANCELING:
		case REFUSED:
		case SENDING_ANSWER:
		case SENDING_QUERY:
		case SENDING_RESULT:
		case WAITING_QUERY:
		default:
			break;
		}
		
		return this.queryConversation != null ? this.queryConversation.getState() : null;
	}
	
	private static ACLMessage createACLMessage(QueryInfo queryInfo) {
		
		ACLMessage message = new ACLMessage(queryInfo.getQuery(), queryInfo.getPerformative());
		message.setOntology(queryInfo.getOntology());
		message.setLanguage(queryInfo.getLanguage());
		
		return message;
	}
	
	
	private void print(String str){
		super.print("[" + getPlayer() + "] : " + str); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
