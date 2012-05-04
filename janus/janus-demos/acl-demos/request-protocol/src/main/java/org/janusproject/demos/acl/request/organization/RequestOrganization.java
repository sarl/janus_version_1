package org.janusproject.demos.acl.request.organization;

import org.janusproject.demos.acl.request.role.Answerer;
import org.janusproject.demos.acl.request.role.Requester;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;

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
public class RequestOrganization extends Organization {

	/**
	 * @param crioContext
	 */
	public RequestOrganization(final CRIOContext crioContext) {
		super(crioContext);
		addRole(Requester.class);
		addRole(Answerer.class);
	}

}
