/*
 *  Copyright (c) 2001 Sun Microsystems, Inc.  All rights
 *  reserved.
 *  Copyright (c) 2010-2012 James Todd and Janus Core Developers
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
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
 *  $Id: PeerGroupUtil.java,v 1.7 2007/05/28 22:00:51 nano Exp $
 */
package org.janusproject.kernel.network.jxme.jxta.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.TextElement;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.ModuleClassID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;

import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.network.JanusNetworkConstants;
import org.janusproject.kernel.util.throwable.Throwables;

/**
 * Utilities on peer groups.
 * 
 * @author $Author: gonzo@jxta.org$
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: jeremie.laval@gmail.com$
 * @author $Author: robin.geffroy@gmail.com$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class PeerGroupUtil {

	/** Default membership id.
	 */
	public static final String MEMBERSHIP_ID = "myjxtauser"; //$NON-NLS-1$

	private static final long MILLISECONDS_IN_A_WEEK = 7 * 24 * 60 * 60 * 1000;

	private static final ModuleSpecID passwordMembershipSpecID = (ModuleSpecID)ID.create(URI.create("urn:jxta:uuid-DeadBeefDeafBabaFeedBabe000000050206")); //$NON-NLS-1$

	private static String makePsswd(String source) {

		/**
		 *
		 * A->D  B->Q  C->K  D->W  E->H  F->R  G->T  H->E  I->N  J->O  K->G  L->X  M->C
		 * N->V  O->Y  P->S  Q->F  R->J  S->P  T->I  U->L  V->Z  W->A  X->B  Y->M  Z->U
		 *
		 */

		String xlateTable = "DQKWHRTENOGXCVYSFJPILZABMU"; //$NON-NLS-1$

		StringBuilder work = new StringBuilder(source);

		for (int eachChar = work.length() - 1; eachChar >= 0; eachChar--) {
			char aChar = Character.toUpperCase(work.charAt(eachChar));

			int replaceIdx = xlateTable.indexOf(aChar);

			if (-1 != replaceIdx) {
				work.setCharAt(eachChar, (char) ('A' + replaceIdx));
			}
		}

		return work.toString();
	}

	/**
	 * Create a new PeerGroupAdvertisment from which a new PeerGroup will be
	 * created.
	 * <p/>
	 * See "Create a Secure Peer Group" in <a
	 * href="http://www.jxta.org/docs/JxtaProgGuide_v2.pdf">Jxta Programmers
	 * Guide</a> for how to create a secure group. Most of the code in this
	 * class is taken direcly from that chapter
	 *
	 * @param parentGroup
	 * the parent PeerGroup
	 * @param name
	 * the name of the new PeerGroup
	 * @param description
	 * the description of the new PeerGroup
	 * @param password
	 * the password for the new Peergroup. If it is null or an empty
	 * string this peer group is not password protected
	 * @param expiration
	 * @param id
	 * @return a new PeerGroupAdvertisement
	 * @throws Exception
	 */
	public static PeerGroupAdvertisement createJXTA(PeerGroup parentGroup, String name, String description, String password, long expiration, PeerGroupID id) throws Exception {
		PeerGroupAdvertisement pga;
		ModuleImplAdvertisement mia;
		boolean passProt = (password != null && !password.trim().equals("")); //$NON-NLS-1$

		// create the ModuleImplAdvertisement and publish it
		mia = parentGroup.getAllPurposePeerGroupImplAdvertisement();

		if (passProt) {
			createPasswordModuleImpl(mia);
		}

		parentGroup.getDiscoveryService().publish(mia);
		parentGroup.getDiscoveryService().remotePublish(mia);

		// create the PeerGroupAdvertisment and publish it
		pga = (PeerGroupAdvertisement) AdvertisementFactory.newAdvertisement(PeerGroupAdvertisement.getAdvertisementType());
		pga.setPeerGroupID(id != null ? id : IDFactory.newPeerGroupID());
		pga.setName(name);// unique name
		pga.setDescription(description);
		pga.setModuleSpecID(mia.getModuleSpecID());

		if (passProt) {
			StructuredTextDocument login = (StructuredTextDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, "Param"); //$NON-NLS-1$
			String loginString = MEMBERSHIP_ID + ":" //$NON-NLS-1$
					+ makePsswd(password) + ":"; //$NON-NLS-1$
			TextElement loginElement = login.createElement("login", loginString); //$NON-NLS-1$

			login.appendChild(loginElement);
			pga.putServiceParam(PeerGroup.membershipClassID, login);
		}
		DiscoveryService ds = parentGroup.getDiscoveryService();

		ds.publish(pga, expiration != 0 ? expiration : 2 * MILLISECONDS_IN_A_WEEK, expiration != 0 ? expiration : 2 * MILLISECONDS_IN_A_WEEK);
		ds.remotePublish(pga, expiration != 0 ? expiration : 2 * MILLISECONDS_IN_A_WEEK);

		return pga;
	}

	/** Create a JXTA advertisment for Janus.
	 * 
	 * @param parentGroup
	 * @param description
	 * @param password
	 * @param expiration
	 * @param id
	 * @param janusGroupAddress
	 * @param obtainConditions
	 * @param leaveConditions
	 * @param membership
	 * @return an advertisment.
	 * @throws Exception
	 */
	public static PeerGroupAdvertisement createJanus(PeerGroup parentGroup, String description, String password, long expiration, PeerGroupID id, GroupAddress janusGroupAddress, Collection<? extends GroupCondition> obtainConditions, Collection<? extends GroupCondition> leaveConditions, MembershipService membership) throws Exception {
		PeerGroupAdvertisement pga;
		ModuleImplAdvertisement mia;
		boolean passProt = (password != null && !password.trim().equals("")); //$NON-NLS-1$

		// create the ModuleImplAdvertisement and publish it
		mia = parentGroup.getAllPurposePeerGroupImplAdvertisement();

		if (passProt) {
			createPasswordModuleImpl(mia);
		}

		parentGroup.getDiscoveryService().publish(mia);
		parentGroup.getDiscoveryService().remotePublish(mia);

		// create the PeerGroupAdvertisment and publish it
		pga = (PeerGroupAdvertisement) AdvertisementFactory.newAdvertisement(PeerGroupAdvertisement.getAdvertisementType());
		pga.setPeerGroupID(id != null ? id : IDFactory.newPeerGroupID());
		pga.setName(janusGroupAddress.getUUID().toString());
		pga.setDescription(description);
		pga.setModuleSpecID(mia.getModuleSpecID());

		if (passProt) {
			StructuredTextDocument login = (StructuredTextDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, "Param"); //$NON-NLS-1$
			String loginString = MEMBERSHIP_ID + ":" //$NON-NLS-1$
					+ makePsswd(password) + ":"; //$NON-NLS-1$
			TextElement loginElement = login.createElement("login", loginString); //$NON-NLS-1$

			login.appendChild(loginElement);
			pga.putServiceParam(PeerGroup.membershipClassID, login);
		}

		StructuredTextDocument orgClass = (StructuredTextDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, "Param"); //$NON-NLS-1$
		orgClass.appendChild(orgClass.createElement(JanusNetworkConstants.TAG_JANUS_ORG, janusGroupAddress.getOrganization().getName()));
		orgClass.appendChild(orgClass.createElement(JanusNetworkConstants.TAG_JANUS_GROUP_ID, janusGroupAddress.getUUID().toString()));

		if (janusGroupAddress.getName() != null) {
			orgClass.appendChild(orgClass.createElement(JanusNetworkConstants.TAG_JANUS_GROUP_NAME, janusGroupAddress.getName()));
		}

		ByteArrayOutputStream out;
		ObjectOutputStream oos;

		// obtain conditions serialization
		if (obtainConditions != null && obtainConditions.size() > 0) {
			Element eObtainConditions = orgClass.createElement(JanusNetworkConstants.TAG_JANUS_OBTAIN_CONDITIONS);
			Element obtainCondition;
			for (GroupCondition obtain : obtainConditions) {
				out = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(out);
				oos.writeObject(obtain);
				oos.close();
				obtainCondition = orgClass.createElement(JanusNetworkConstants.TAG_JANUS_CONDITION, new String(out.toByteArray()));
				eObtainConditions.appendChild(obtainCondition);
			}
			orgClass.appendChild(eObtainConditions);
		}

		// leave conditions serialization
		if (leaveConditions != null && leaveConditions.size() > 0) {
			Element eLeaveConditions = orgClass.createElement(JanusNetworkConstants.TAG_JANUS_LEAVE_CONDITIONS);
			Element leaveCondition;
			for (GroupCondition leave : leaveConditions) {
				out = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(out);
				oos.writeObject(leave);
				oos.close();
				leaveCondition = orgClass.createElement(JanusNetworkConstants.TAG_JANUS_CONDITION, new String(out.toByteArray()));
				eLeaveConditions.appendChild(leaveCondition);
			}
			orgClass.appendChild(eLeaveConditions);
		}

		// Membership serialization
		if (membership != null) {
			out = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(out);
			oos.writeObject(membership);
			oos.close();
			orgClass.appendChild(orgClass.createElement(JanusNetworkConstants.TAG_JANUS_MEMBERSHIPSERVICE, new String(out.toByteArray())));
		}
		pga.putServiceParam(Utils.JANUS_ORG_CLASS, orgClass);

		DiscoveryService ds = parentGroup.getDiscoveryService();

		ds.publish(pga, expiration != 0 ? expiration : 2 * MILLISECONDS_IN_A_WEEK, expiration != 0 ? expiration : 2 * MILLISECONDS_IN_A_WEEK);
		ds.remotePublish(pga, expiration != 0 ? expiration : 2 * MILLISECONDS_IN_A_WEEK);

		return pga;
	}

	/**
	 * 
	 * @param pg
	 * @param groupId
	 * @return the list of advertisement associated to the specified JXTA Janus group
	 */
	public static List<PeerGroupAdvertisement> getJanusGroupAdvs(PeerGroup pg, UUID groupId) {
		assert (groupId != null);
		List<PeerGroupAdvertisement> p = new ArrayList<PeerGroupAdvertisement>();

		try {
			for (Enumeration<?> gas = pg.getDiscoveryService().getLocalAdvertisements(DiscoveryService.GROUP, "Name", groupId.toString()); gas //$NON-NLS-1$
					.hasMoreElements();) {
				Object o = gas.nextElement();

				if (o instanceof PeerGroupAdvertisement) {
					p.add((PeerGroupAdvertisement) o);
				}
			}
		} catch (AssertionError ae) {
			throw ae;
		} catch (IOException ioe) {
			// FIXME do something in case of exception
		}

		return p;
	}

	/**
	 * 
	 * @param pg
	 * @param name
	 * @return the list of advertisement associated to the specified JXTA group
	 */
	public static List<PeerGroupAdvertisement> getAdvs(PeerGroup pg, String name) {
		List<PeerGroupAdvertisement> p = new ArrayList<PeerGroupAdvertisement>();

		try {
			for (Enumeration<?> gas = pg.getDiscoveryService().getLocalAdvertisements(DiscoveryService.GROUP, name != null ? "Name" : null, name); gas //$NON-NLS-1$
			.hasMoreElements();) {
				Object o = gas.nextElement();

				if (o instanceof PeerGroupAdvertisement) {
					p.add((PeerGroupAdvertisement) o);
				}
			}
		} catch (AssertionError ae) {
			throw ae;
		}
		catch (IOException ioe) {
			// FIXME do something in case of exception
		}

		return p;
	}

	/**
	 * 
	 * @param pg
	 * @param name
	 * @param listener
	 */
	public static void discoverAdvs(PeerGroup pg, String name, DiscoveryListener listener) {
		DiscoveryService s = pg.getDiscoveryService();

		s.getRemoteAdvertisements(null, DiscoveryService.GROUP, name != null ? "Name" : null, name, 10, listener); //$NON-NLS-1$
	}

	/**
	 * Updates the ModuleImplAdvertisement of the PeerGroupAdvertisement to reflect the fact that we want to use the PasswordService in order to manage the membership in this group
	 * 
	 * @param mia
	 *            the ModuleImplAdvertisement to update
	 */
	private static void createPasswordModuleImpl(ModuleImplAdvertisement mia) {
		StdPeerGroupParamAdv stdPgParams = new StdPeerGroupParamAdv();
		Map<ModuleClassID, Object> params = stdPgParams.getServices();
		boolean found = false;

		// loop until the MembershipService is found
		for (Iterator<ModuleClassID> pi = params.keySet().iterator(); pi.hasNext() && !found;) {
			ModuleClassID serviceID = pi.next();

			if (serviceID.equals(PeerGroup.membershipClassID)) {
				// get the Advertisement for the MembershipService
				ModuleImplAdvertisement memServices = (ModuleImplAdvertisement) params.get(serviceID);

				// create a new Advertisement describing the password service
				ModuleImplAdvertisement newMemServices = createPasswordServiceImpl(memServices);

				// update the services hashtable
				params.remove(serviceID);
				params.put(PeerGroup.membershipClassID, newMemServices);
				found = true;

				// and update the Service parameters list for the
				// ModuleImplAdvertisement
				mia.setParam((Element) stdPgParams.getDocument(MimeMediaType.XMLUTF8));

				// change the ModuleSpecID since this
				if (!mia.getModuleSpecID().equals(PeerGroup.allPurposePeerGroupSpecID)) {
					mia.setModuleSpecID(IDFactory.newModuleSpecID(mia.getModuleSpecID().getBaseClass()));
				} else {
					ID passID = ID.nullID;

					try {
						passID = IDFactory.fromURI(new URI("urn", "jxta:uuid-" //$NON-NLS-1$ //$NON-NLS-2$
								+ "DeadBeefDeafBabaFeedBabe00000001" + "04" //$NON-NLS-1$ //$NON-NLS-2$
								+ "06", null)); //$NON-NLS-1$
					} catch (AssertionError ae) {
						throw ae;
					} catch (URISyntaxException e) {
						Logger.getLogger(PeerGroupUtil.class.getName()).severe(Throwables.toString(e));
					}

					mia.setModuleSpecID((ModuleSpecID) passID);
				}
			}
		}
	}

	/**
	 * Create the ModuleImplAdvertisement that describes the PasswordService that this group is going to use
	 * 
	 * @param template
	 *            the previous ModuleImplAdvertisement that we use as a template
	 * @return the ModuleImplAdvertisement that describes the PasswordService that this group is going to use
	 */
	private static ModuleImplAdvertisement createPasswordServiceImpl(ModuleImplAdvertisement template) {
		ModuleImplAdvertisement passMember = (ModuleImplAdvertisement) AdvertisementFactory.newAdvertisement(ModuleImplAdvertisement.getAdvertisementType());

		passMember.setModuleSpecID(passwordMembershipSpecID);
		passMember.setCode("PasswdMembershipService"); //$NON-NLS-1$
		passMember.setDescription("Membership Services for MyJXTA"); //$NON-NLS-1$
		passMember.setCompat(template.getCompat());
		passMember.setUri(template.getUri());
		passMember.setProvider(template.getProvider());

		return passMember;
	}
}
