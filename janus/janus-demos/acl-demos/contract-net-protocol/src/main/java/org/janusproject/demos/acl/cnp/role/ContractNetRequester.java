package org.janusproject.demos.acl.cnp.role;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.acl.protocol.ProtocolResult;
import org.janusproject.acl.protocol.ProtocolState;
import org.janusproject.acl.protocol.cnp.FipaContractNetProtocol;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
*
* This class represents the requester in a CNP
*  
* @author $Author: madeline$
* @author $Author: kleroy$
* @author $Author: ptalagrand$
* @author $Author: ngaud$
* @version $Name$ $Revision$ $Date$
* @mavengroupid $Groupid$
* @mavenartifactid $ArtifactId$
*/
public class ContractNetRequester extends Role {
	
	private FipaContractNetProtocol contractNetProtocol;
	private List<ProtocolResult> proposals;
	private List<AgentAddress> selectedParticipants;
	private List<ProtocolResult> results;
	
	private State state;
	
	@Override
	public Status activate(Object... parameters) {
		
		for (Object parameter : parameters) {
			if (parameter instanceof FipaContractNetProtocol) {
				this.contractNetProtocol = (FipaContractNetProtocol) parameter;
				this.state = State.SENDING_CALL_FOR_PROPOSAL;
				return StatusFactory.ok(this);
			}
		}
		
		return StatusFactory.cancel(this);
	}
	
	@Override
	public Status live() {
		this.state = Request();
		return StatusFactory.ok(this);
	}
	
	private State Request() {
		
		if(this.contractNetProtocol.hasFailed() ){
			leaveMe();
		}
		
		switch (this.state) 
		{
			case SENDING_CALL_FOR_PROPOSAL: 
			{	
				this.contractNetProtocol.callForProposal(Locale.getString("TASKDESC")); //$NON-NLS-1$
				
				//requestProtocol.cancel("@++");
				
				log(Locale.getString("CFPSENT")); //$NON-NLS-1$
				
				return State.WAITING_ALL_PROPOSALS;
			}
			case WAITING_ALL_PROPOSALS:
			{
				this.proposals = this.contractNetProtocol.getAllProposals();
				
				if( this.proposals != null ) {
					log(Locale.getString("PROPOSALRECEIVED")); //$NON-NLS-1$
					
					if (this.proposals.size() == 0) // nobody has accepted the task
						return State.REFUSED;
					
					this.selectedParticipants = new ArrayList<>();
					this.selectedParticipants.add(this.proposals.get(0).getAuthor()); // arbitrary, need to use capacity
					
					this.contractNetProtocol.acceptProposals(this.selectedParticipants, Locale.getString("ACCEPT")); //$NON-NLS-1$
					log(Locale.getString("PROPOSALANSWERED")); //$NON-NLS-1$
					
					return State.WAITING_ALL_RESULTS;
				}
				return this.state;				
			}
			case WAITING_ALL_RESULTS:
			{
				this.results = this.contractNetProtocol.getResults();
				
				if( this.results != null ) {
					log(Locale.getString("RESULTRECEIVED")); //$NON-NLS-1$
					return State.DONE;
				}
				return this.state;
				
			}
			case DONE:
			{
				leaveMe();
				return this.state;
			}
			default: {
				return this.state;
			}
		}
	}	
	
	private void log(String str){
		Logger.getAnonymousLogger().log(Level.INFO, "[" + getPlayer() + "] : " + str); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private enum State implements ProtocolState {
		NOT_STARTED,
		SENDING_CALL_FOR_PROPOSAL,
		WAITING_ALL_PROPOSALS,
		WAITING_ALL_RESULTS,
		CANCELING,
		CANCELED,
		REFUSED,
		DONE;
	}

}
