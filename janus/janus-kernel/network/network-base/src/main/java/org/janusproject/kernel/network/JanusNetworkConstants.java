/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2013 Janus Core Developers
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.janusproject.kernel.network;



/**
 * A set of contants used to configure the Janus Network Module.
 * 
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface JanusNetworkConstants {

	/**
	 * The unique PeerGroupID for the World of Janus.
	 */
	public static final String PG_WORLD_OF_JANUS_ID = "urn:jxta:uuid-8B33E028B054497B8BF9A446A224B1FF02"; //$NON-NLS-1$

	/** Postfix for the identifier of the Janus features on JXTA.
	 */
	public static final String JANUS_ORG_CLASS_ID_POSTFIX = "0010000505"; //$NON-NLS-1$
	
	/** Parameter name in a network advertisement for the Janus features.
	 */
	public static final String TAG_JANUS_ORG = "JanusOrgClass"; //$NON-NLS-1$

	/** Parameter name in a network advertisement for the Janus group id.
	 */
	public static final String TAG_JANUS_GROUP_ID = "JanusGroupId"; //$NON-NLS-1$

	/** Parameter name in a network advertisement for the Janus group name.
	 */
	public static final String TAG_JANUS_GROUP_NAME = "JanusGroupName"; //$NON-NLS-1$

	/** Parameter name in a network advertisement for the entering conditions in a Janus group.
	 */
	public static final String TAG_JANUS_OBTAIN_CONDITIONS = "obtainConditions"; //$NON-NLS-1$

	/** Parameter name in a network advertisement for the exiting conditions from a Janus group.
	 */
	public static final String TAG_JANUS_LEAVE_CONDITIONS = "leaveConditions"; //$NON-NLS-1$

	/** Parameter name in a network advertisement for the conditions associated to a Janus group.
	 */
	public static final String TAG_JANUS_CONDITION = "condition"; //$NON-NLS-1$

	/** Parameter name in a network advertisement for the membership checker for a Janus group.
	 */
	public static final String TAG_JANUS_MEMBERSHIPSERVICE = "membership"; //$NON-NLS-1$	

}
