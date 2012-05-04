package org.janusproject.demos.acl.cnp.organization;

import org.janusproject.demos.acl.cnp.role.ContractNetBroker;
import org.janusproject.demos.acl.cnp.role.ContractNetRequester;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;


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
public class ContractNetOrganization extends Organization {

	/**
	 * @param crioContext
	 */
	public ContractNetOrganization(CRIOContext crioContext) {
		super(crioContext);
		addRole(ContractNetRequester.class);
		addRole(ContractNetBroker.class);
		addRole(ContractNetBroker.class);
	}

}
