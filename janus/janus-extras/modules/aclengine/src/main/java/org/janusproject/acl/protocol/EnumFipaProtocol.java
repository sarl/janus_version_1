package org.janusproject.acl.protocol;

/**
 * This enumeration describes all available protocols :
 * <ul>
 * <li>FIPA_REQUEST : Request Protocol</li>
 * <li>FIPA_CONTRACT_NET : CNP Contract Net Protocol</li>
 * </ul>
 * <p>
 * Please refer to the following links to get more information about those protocols :
 * <ul>
 * <li><a href="http://www.fipa.org/specs/fipa00026/SC00026H.html">FIPA Request Interaction Protocol Specification</a></li>
 * <li><a href="http://www.fipa.org/specs/fipa00029/SC00029H.html">FIPA Contract Net Interaction Protocol Specification</a></li>
 * </ul>
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public enum EnumFipaProtocol {
	/**
	 */
	NONE("none"), //$NON-NLS-1$
	/**
	 */
	FIPA_REQUEST("fipa-request"), //$NON-NLS-1$
	/**
	 */
	FIPA_CONTRACT_NET("fipa-contract-net"); //$NON-NLS-1$
	
	private final String name;
	
	EnumFipaProtocol(String name) {
		this.name = name;
	}
	
	/**
	 * @return the name of the protocol
	 */
	public String getName() { 
		return this.name; 
	} 
	
	/**
	 * @param name
	 * @return the field of the protocol enum corresponding to the specified string
	 * @throws IllegalArgumentException
	 */
	public static EnumFipaProtocol valueOfByName(String name) throws IllegalArgumentException { 
		for (EnumFipaProtocol value : values()) { 
			if (value.getName().equalsIgnoreCase(name)) { 
				return value; 
			} 
		} 
		return EnumFipaProtocol.NONE; 
	}
}
