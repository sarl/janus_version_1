/*
 *  Copyright (c) 2001 Sun Microsystems, Inc.  All rights
 *  reserved.
 *  Copyright (c) 2010-2011 James Todd and Janus Core Developers
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following discalimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  3. The end-user documentation included with the redistribution,
 *  if any, must include the following acknowledgment:
 *  "This product includes software developed by the
 *  Sun Microsystems, Inc. for Project JXTA."
 *  Alternately, this acknowledgment may appear in the software itself,
 *  if and wherever such third-party acknowledgments normally appear.
 *
 *  4. The names "Sun", "Sun Microsystems, Inc.", "JXTA" and "Project JXTA"
 *  must not be used to endorse or promote products derived from this
 *  software without prior written permission. For written
 *  permission, please contact Project JXTA at http://www.jxta.org.
 *
 *  5. Products derived from this software may not be called "JXTA",
 *  nor may "JXTA" appear in their name, without prior written
 *  permission of Sun.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of Project JXTA.  For more
 *  information on Project JXTA, please see
 *  <http://www.jxta.org/>.
 *
 *  This license is based on the BSD license adopted by the Apache Foundation.
 *
 *  $Id: PipeUtil.java,v 1.4 2007/05/02 21:39:08 nano Exp $
 */

package org.janusproject.kernel.network.jxse.jxta.impl;

import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.PipeAdvertisement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import org.janusproject.kernel.util.throwable.Throwables;

/**
 * Utitilies around the JXTA pipes.
 * 
 * @author $Author: gonzo@jxta.org$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class PipeUtil {

	/** Replies the JXTA advertisement from the given JXTA pipe
	 * or publish a new one if none was found.
	 * 
	 * @param pg is the JXTA group.
	 * @param name is the name of the pipe.
	 * @param type is the type of the created advertisement.
	 * @param pipeId is the id of the created advertisement.
	 * @return the pipe advertisement or <code>null</code>.
	 */
	public static PipeAdvertisement getAdvertisement(PeerGroup pg, String name, String type, byte[] pipeId) {
		PipeAdvertisement pa = searchLocalAdvertisement(pg, name);

		if (pa == null) {
			pa = createAdvertisement(pg, name, type, pipeId);
			publish(pg, pa);
		}

		return pa;
	}

	/** Replies the JXTA advertisements from the given JXTA pipe.
	 * 
	 * @param pg is the JXTA group.
	 * @param name is the name of the pipe.
	 * @return the pipe advertisement or <code>null</code>.
	 */
	public static List<PipeAdvertisement> getAdvertisements(PeerGroup pg, String name) {
		List<PipeAdvertisement> p = new ArrayList<PipeAdvertisement>();
		Enumeration<Advertisement> pas;
		try {
			pas = pg.getDiscoveryService().getLocalAdvertisements(
					DiscoveryService.ADV, 
					PipeAdvertisement.NameTag, 
					name);
		}
		catch (AssertionError ae) {
			throw ae;
		}
		catch (IOException ioe) {
			return p;
		}

		while (pas.hasMoreElements()) {
			Object o = pas.nextElement();
			if (o instanceof PipeAdvertisement) {
				p.add((PipeAdvertisement) o);
			}
		}

		return p;
	}

	/**
	 * Search and reply the pipe advertisement with the given
	 * name in the given JXTA group.
	 * 
	 * @param pg is the JXTA group.
	 * @param name is the name of the advertisement.
	 * @return the advertisement or <code>null</code>.
	 */
	public static PipeAdvertisement searchLocalAdvertisement(PeerGroup pg, String name) {
		DiscoveryService discoveryService = pg.getDiscoveryService();
		Enumeration<Advertisement> pas;
		try {
			pas = discoveryService.getLocalAdvertisements(
					DiscoveryService.ADV, 
					PipeAdvertisement.NameTag, 
					name);
		}
		catch (AssertionError ae) {
			throw ae;
		}
		catch (IOException e) {
			return null;
		}
		
		PipeAdvertisement pa;
		while (pas.hasMoreElements()) {
			pa = (PipeAdvertisement) pas.nextElement();

			if (pa.getName().equals(name)) {
				return pa;
			}
		}
		return null;
	}

	/**
	 * Discover advertisements from remote peers. This does not normally 
     * provide an exhaustive search. Instead it provides a "best efforts" 
     * search which will provide a selection of advertisements of matching the 
     * search criteria. The selection of advertisements returned may be random 
     * or predictable depending upon the network configuration and no 
     * particular behaviour should be assumed. In general the narrower the
     * query specified the more exhaustive the responses will be.
     * 
	 * @param pg is the JXTA group to discover in.
	 * @param name is the name of the advertisements to discover.
	 * @param listener is the listener to notify about any discovering.
	 */
	public static void discoverAdvertisements(PeerGroup pg, String name, DiscoveryListener listener) {
		DiscoveryService s = pg.getDiscoveryService();
		s.getRemoteAdvertisements(null, 
				DiscoveryService.ADV, name != null ? PipeAdvertisement.NameTag : null, 
				name, 10, listener);
	}

	/**
	 * Constructs a new instance of PipeAdvertisement matching the given type.
     *
	 * @param pg is the JXTA group to create in.
	 * @param name is the name of the advertisement.
	 * @param type is the type of the advertisement.
	 * @return the create advertisement, never <code>null</code>.
	 */
	public static PipeAdvertisement createAdvertisement(PeerGroup pg, String name, String type) {
		return createAdvertisement(pg, name, type, null);
	}

	/**
	 * Constructs a new instance of PipeAdvertisement matching the given type.
     *
	 * @param pg is the JXTA group to create in.
	 * @param name is the name of the advertisement.
	 * @param type is the type of the advertisement.
	 * @param id is the identifier of the advertisement.
	 * @return the create advertisement, never <code>null</code>.
	 */
	public static PipeAdvertisement createAdvertisement(PeerGroup pg, String name, String type, byte[] id) {
		PipeAdvertisement pa = (PipeAdvertisement)AdvertisementFactory.newAdvertisement(
				PipeAdvertisement.getAdvertisementType());

		pa.setPipeID(id != null 
				? IDFactory.newPipeID(pg.getPeerGroupID(), id)
				: IDFactory.newPipeID(pg.getPeerGroupID()));
		pa.setName(name);
		pa.setType(type);

		return pa;
	}

	/**
	 * Publish an Advertisement. The Advertisement will expire automatically 
     * on the local peer after <code>DEFAULT_LIFETIME</code> and will expire on
     * other peers after <code>DEFAULT_EXPIRATION</code>.
     * <p/>
     * When an Advertisement is published, it is stored, and indexed in the 
     * peer's local cache. The Advertisement indexes are also shared with 
     * Rendezvous peers. Advertisement indexes may not be shared with other
     * peers immediately, but may be updated as part of a periodic process. The
     * Discovery Service currently publishes index updates every 30 seconds.
     *
     * @param pg is the JXTA group to publish in.
     * @param pa is the Advertisement to publish.
	 */
	public static void publish(PeerGroup pg, PipeAdvertisement pa) {
		DiscoveryService ds = pg.getDiscoveryService();
		try {
			ds.publish(pa);
		}
		catch (AssertionError ae) {
			throw ae;
		}
		catch (IOException e) {
			Logger.getLogger(PipeUtil.class.getName()).severe(Throwables.toString(e));
		}
	}
	
}
