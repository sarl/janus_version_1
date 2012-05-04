package org.janusproject.demos.acl.request.agent;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.acl.ACLAgent;
import org.janusproject.acl.protocol.AbstractFipaProtocol;
import org.janusproject.acl.protocol.EnumFipaProtocol;
import org.janusproject.acl.protocol.FipaConversationManager;
import org.janusproject.demos.acl.request.organization.RequestOrganization;
import org.janusproject.demos.acl.request.role.Answerer;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;


/**
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
public class ACLProtocolReceiver extends ACLAgent {

	private static final long serialVersionUID = -7078376398776599011L;
	
	/**
	 * The protocol's manager
	 */
	protected FipaConversationManager protocolManager;

	@Override
	public Status activate(Object... parameters) {
		this.protocolManager = new FipaConversationManager(this);
		AbstractFipaProtocol protocol = this.protocolManager.createConversation(EnumFipaProtocol.FIPA_REQUEST, Locale.getString("INITIALIZATION")); //$NON-NLS-1$
		protocol.initiateAsParticipant();
		
		Logger.getAnonymousLogger().log(Level.INFO, Locale.getString("PROTOCOLCREATED")); //$NON-NLS-1$
		
		GroupAddress groupAddress = getOrCreateGroup(RequestOrganization.class);	
		
		if (requestRole( Answerer.class, groupAddress, protocol) == null) {
			return StatusFactory.cancel(this);
		}
		
		return StatusFactory.ok(this);
	}	
}