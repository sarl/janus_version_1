package org.janusproject.demos.acl.request.agent;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.acl.ACLAgent;
import org.janusproject.acl.protocol.AbstractFipaProtocol;
import org.janusproject.acl.protocol.EnumFipaProtocol;
import org.janusproject.acl.protocol.FipaConversationManager;
import org.janusproject.demos.acl.request.organization.RequestOrganization;
import org.janusproject.demos.acl.request.role.Requester;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * 
 * 
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ACLProtocolSender extends ACLAgent {

	private static final long serialVersionUID = -7078376398776599011L;

	/**
	 * The protocol's manager
	 */
	protected FipaConversationManager protocolManager;

	@Override
	public Status activate(Object... parameters) {

		this.protocolManager = new FipaConversationManager(this);
		AbstractFipaProtocol protocol = this.protocolManager.createConversation(EnumFipaProtocol.FIPA_REQUEST, Locale.getString("INITIALIZATION")); //$NON-NLS-1$ 		
		Logger.getAnonymousLogger().log(Level.INFO, Locale.getString("PROTOCOLCREATED")); //$NON-NLS-1$ 

		SizedIterator<AgentAddress> agents = getLocalAgents();

		while (agents.hasNext()) {
			AgentAddress participant = agents.next();
			if (participant != getAddress()) {
				protocol.initiate(getAddress(), participant);
			}
		}

		GroupAddress groupAddress = getOrCreateGroup(RequestOrganization.class);

		if (requestRole(Requester.class, groupAddress, protocol) == null) {
			return StatusFactory.cancel(this);
		}

		return StatusFactory.ok(this);
	}
}