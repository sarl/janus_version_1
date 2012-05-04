package org.janusproject.acl.encoding;

/**
 * This enumeration corresponds to the different type of encoding (UTF-8, ISO, ASCII...)
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public enum PayloadEncoding {
	
	/**
	 * UTF8 Enconding, the defualt one
	 */
	UTF8("UTF-8"); //$NON-NLS-1$
	
	private final String value;
	
	PayloadEncoding(String value) {
		this.value = value;
	}

	/**
	 * @return the string value associated to an enum case
	 */
	public String getValue() {
		return this.value;
	}
	
	
}
