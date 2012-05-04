package org.janusproject.demos.acl.cnp.agent;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.acl.ACLAgent;
import org.janusproject.acl.protocol.AbstractFipaProtocol;
import org.janusproject.acl.protocol.EnumFipaProtocol;
import org.janusproject.acl.protocol.FipaConversationManager;
import org.janusproject.demos.acl.cnp.organization.ContractNetOrganization;
import org.janusproject.demos.acl.cnp.role.ContractNetBroker;
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
public class ACLCallForProposalReceiver extends ACLAgent {

	private static final long serialVersionUID = -7078376398776599011L;
	
	/**
	 * The protocol's manager
	 */
	protected FipaConversationManager protocolManager;

	@Override
	public Status activate(Object... parameters) {
		this.protocolManager = new FipaConversationManager(this);
		AbstractFipaProtocol protocol = this.protocolManager.createConversation(EnumFipaProtocol.FIPA_CONTRACT_NET,Locale.getString("INITIALIZATION")); //$NON-NLS-1$
		protocol.initiateAsParticipant();
		
		Logger.getAnonymousLogger().log(Level.INFO, Locale.getString("CREATION")); //$NON-NLS-1$
		
		GroupAddress groupAddress = getOrCreateGroup(ContractNetOrganization.class);	
		
		if (requestRole( ContractNetBroker.class, groupAddress, protocol)==null) {
			return StatusFactory.cancel(this);
		}
		
		return StatusFactory.ok(this);
	}	
}