/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.kernel.network.jxme.jxta.impl;



import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;

import org.janusproject.kernel.network.JanusNetworkConstants;

/** Several utilities dedicated to the JXTA support in Janus.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @author $Author: jeremie.laval@gmail.com$
 * @author $Author: robin.geffroy@gmail.com$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class Utils {

	private static final String SEED = "JanusPlatformDefaultSeed"; //$NON-NLS-1$

	/** Identifier of the Janus features on JXTA.
	 */
	public static final ID JANUS_ORG_CLASS = ID.create(URI.create(PeerGroup.WK_ID_PREFIX + JanusNetworkConstants.JANUS_ORG_CLASS_ID_POSTFIX));

	/**
	 * Given a group name generates a Peer Group ID who's value is chosen based
	 * upon that name.
	 * 
	 * @param parentID
	 *            the ID of the parent peer group.
	 * @param groupName
	 *            group name encoding value
	 * @return The PeerGroupID value
	 */
	public static PeerGroupID createPeerGroupID(PeerGroupID parentID,
			final String groupName) {
		// Use lower case to avoid any locale conversion inconsistencies
		return IDFactory.newPeerGroupID(parentID,
				hash(SEED + groupName.toLowerCase()));
	}

	/**
	 * Returns a SHA1 hash of string.
	 * 
	 * @param expression
	 *            to hash
	 * @return a SHA1 hash of string or {@code null} if the expression could not
	 *         be hashed.
	 */
	private static byte[] hash(String expression) {
		byte[] result;
		MessageDigest digest;
		if (expression == null) {
			throw new IllegalArgumentException("Invalid null expression"); //$NON-NLS-1$
		}
		try {
			digest = MessageDigest.getInstance("SHA1"); //$NON-NLS-1$
		}
		catch(AssertionError ae) {
			throw ae;
		}
		catch (NoSuchAlgorithmException failed) {
			RuntimeException failure = new IllegalStateException(
					"Could not get SHA-1 Message"); //$NON-NLS-1$
			failure.initCause(failed);
			throw failure;
		}
		try {
			byte[] expressionBytes = expression.getBytes("UTF-8"); //$NON-NLS-1$
			result = digest.digest(expressionBytes);
		}
		catch(AssertionError ae) {
			throw ae;
		}
		catch (UnsupportedEncodingException impossible) {
			RuntimeException failure = new IllegalStateException(
					"Could not encode expression as UTF8"); //$NON-NLS-1$
			failure.initCause(impossible);
			throw failure;
		}
		return result;
	}

}
