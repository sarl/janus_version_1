package org.janusproject.acl;

/**
 * This enumeration describes all available ACL Representations as defined by Fipa.
 * <ul>
 * <li>BIT_EFFICIENT</li>
 * <li>STRING</li>
 * <li>XML</li>
 * </ul>
 * 
 * @see <a href="http://www.fipa.org/repository/aclreps.php3">FIPA ACL Representation Specifications</a>
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public enum ACLRepresentation {
	/**
	 * 
	 */
	BIT_EFFICIENT("fipa.acl.rep.bitefficient.std"), //$NON-NLS-1$
	/**
	 * 
	 */
	STRING("fipa.acl.rep.string.std"), //$NON-NLS-1$
	/**
	 * 
	 */
	XML("fipa.acl.rep.xml.std"); //$NON-NLS-1$
	  
	private final String value;
	
	ACLRepresentation(String value) {
		this.value = value;
	}

	/**
	 * @return the string value associated to an enum case
	 */
	public String getValue() {
		return this.value;
	}
}
