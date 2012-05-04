package org.janusproject.demos.acl.cnp.agent;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.acl.ACLAgent;
import org.janusproject.acl.protocol.AbstractFipaProtocol;
import org.janusproject.acl.protocol.EnumFipaProtocol;
import org.janusproject.acl.protocol.FipaConversationManager;
import org.janusproject.demos.acl.cnp.organization.ContractNetOrganization;
import org.janusproject.demos.acl.cnp.role.ContractNetRequester;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

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
public class ACLCallForProposalSender extends ACLAgent {

	private static final long serialVersionUID = -7078376398776599011L;

	/**
	 * The protocol's manager
	 */
	protected FipaConversationManager protocolManager;

	@Override
	public Status activate(Object... parameters) {
		
		this.protocolManager = new FipaConversationManager(this);
		AbstractFipaProtocol protocol =  this.protocolManager.createConversation(EnumFipaProtocol.FIPA_CONTRACT_NET, Locale.getString("INITIALIZATION")); //$NON-NLS-1$		
		Logger.getAnonymousLogger().log(Level.INFO, Locale.getString("CREATION")); //$NON-NLS-1$
		
		SizedIterator<AgentAddress> agents = getLocalAgents();	
		
		ArrayList<AgentAddress> participants = new ArrayList<AgentAddress>();		
		while (agents.hasNext()) 
		{
			AgentAddress participant = agents.next();			
			if ( participant != getAddress() ) 
			{
				participants.add(participant);
			}
		}
		
		protocol.initiate(getAddress(), participants);
		
		GroupAddress groupAddress = getOrCreateGroup(ContractNetOrganization.class);	
		
		if (requestRole( ContractNetRequester.class, groupAddress, protocol )==null) {
			return StatusFactory.cancel(this);
		}
		
		return StatusFactory.ok(this);
	}
}