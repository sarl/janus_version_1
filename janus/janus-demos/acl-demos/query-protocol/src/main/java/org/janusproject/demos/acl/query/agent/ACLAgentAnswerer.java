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
package org.janusproject.demos.acl.query.agent;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.acl.ACLAgent;
import org.janusproject.acl.ACLRepresentation;
import org.janusproject.acl.encoding.PayloadEncoding;
import org.janusproject.acl.protocol.FipaConversationManager;
import org.janusproject.demos.acl.query.organization.QueryOrganization;
import org.janusproject.demos.acl.query.role.Answerer;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/** Agent that is answering.
 * 
 * @author $Author: flacreus$
 * @author $Author: sroth$
 * @author $Author: cstentz$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ACLAgentAnswerer extends ACLAgent {

	private static final long serialVersionUID = -7078376398776599011L;
	
	/**
	 * The protocol's manager
	 */
	protected FipaConversationManager protocolManager;

	/**
	 * @param aclRepresentation
	 * @param payloadEncoding
	 */
	public ACLAgentAnswerer(ACLRepresentation aclRepresentation, PayloadEncoding payloadEncoding) {
		super(aclRepresentation, payloadEncoding);
	}
	
	@Override
	public Status activate(Object... parameters) {
		print(Locale.getString(ACLAgentAnswerer.class, "WAITING_ROLE")); //$NON-NLS-1$
		
		GroupAddress providerGA = getOrCreateGroup(QueryOrganization.class);
		if (providerGA!=null) {
			if(requestRole(Answerer.class, providerGA, new FipaConversationManager(this)) != null){
				print(Locale.getString(ACLAgentAnswerer.class, "RUNNING")); //$NON-NLS-1$
			}
			
			return StatusFactory.ok(this);
		}
		
		return killMe();
	}

	
	private void print(String str){
		super.print("[" + getAddress() + "] : " + str); //$NON-NLS-1$  //$NON-NLS-2$
	}
}