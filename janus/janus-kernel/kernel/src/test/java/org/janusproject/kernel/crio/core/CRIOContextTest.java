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
package org.janusproject.kernel.crio.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;
import java.util.logging.Level;

import org.janusproject.kernel.time.KernelTimeManager;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.configuration.JanusProperties;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.configuration.PrivilegedJanusPropertySetter;
import org.janusproject.kernel.crio.core.CRIOContext.PrivilegedContext;
import org.janusproject.kernel.crio.interaction.PrivilegedMessageTransportService;
import org.janusproject.kernel.crio.organization.PrivilegedPersistentGroupCleanerService;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.mailbox.BufferedMailbox;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.message.Message;

import junit.framework.TestCase;


/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CRIOContextTest extends TestCase {

	private CRIOContext context;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.context = new CRIOContext(null);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.context.destroy();
		this.context = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetOrganizationRepository() {
		OrganizationRepository r;
		assertNotNull(r = this.context.getOrganizationRepository());
		assertSame(r, this.context.getOrganizationRepository());
	}

	/**
	 */
	public void testGetCapacityExecutor() {
		CapacityExecutor r;
		assertNotNull(r = this.context.getCapacityExecutor());
		assertSame(r, this.context.getCapacityExecutor());
	}

	/**
	 */
	public void testGetGroupRepository() {
		GroupRepository r;
		assertNotNull(r = this.context.getGroupRepository());
		assertSame(r, this.context.getGroupRepository());
	}
	
	/**
	 */
	public void testGetTimeManager() {
		KernelTimeManager tm = this.context.getTimeManager();
		assertNotNull(tm);
		assertSame(tm, this.context.getTimeManager());
	}
	
	/**
	 */
	public void testPrivilegedContextForwardMessage() {
		PrivilegedContextStub pContext = new PrivilegedContextStub();
		CRIOContext crioContext = new CRIOContext(null, null, null, pContext);
		assertNotNull(pContext.mts);
		
		RolePlayer player1 = new RolePlayerStub(crioContext);
		RolePlayer player2 = new RolePlayerStub(crioContext);
		RolePlayer player3 = new RolePlayerStub(crioContext);

		GroupAddress ga = player1.getOrCreateGroup(Organization1Stub.class);
		
		assertNotNull(player1.requestRole(RoleStub.class, ga));
		assertNotNull(player2.requestRole(RoleStub.class, ga));
		assertNotNull(player3.requestRole(Role3Stub.class, ga));

		Mailbox mb1 = player1.getRole(ga, RoleStub.class).getMailbox();
		Mailbox mb2 = player2.getRole(ga, RoleStub.class).getMailbox();
		Mailbox mb3 = player3.getRole(ga, Role3Stub.class).getMailbox();
		
		Message message = new Message();
		
		// Send the message to be sure the message's fields are correctly set
		// and clear all the mail boxes.
		player2.sendMessage(ga, RoleStub.class, RoleStub.class, player1.getAddress(), message);
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		mb1.clear();
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		mb2.clear();
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		mb3.clear();
		
		assertTrue(mb1.isEmpty());
		assertTrue(mb2.isEmpty());
		assertTrue(mb3.isEmpty());
		
		Address adr = pContext.mts.forwardMessage(message);
		assertNotNull(adr);
		assertTrue(adr instanceof RoleAddress);

		assertEquals(player1.getAddress(), ((RoleAddress)adr).getPlayer());
		
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		
		assertFalse(mb1.isEmpty());
		assertTrue(mb2.isEmpty());
		assertTrue(mb3.isEmpty());
		
		assertSame(message, mb1.removeFirst());
	}

	/**
	 */
	public void testPrivilegedContextForwardBroadcastMessage() {
		PrivilegedContextStub pContext = new PrivilegedContextStub();
		CRIOContext crioContext = new CRIOContext(null, null, null, pContext);
		assertNotNull(pContext.mts);
		
		RolePlayer player1 = new RolePlayerStub(crioContext);
		RolePlayer player2 = new RolePlayerStub(crioContext);
		RolePlayer player3 = new RolePlayerStub(crioContext);

		GroupAddress ga = player1.getOrCreateGroup(Organization1Stub.class);
		
		assertNotNull(player1.requestRole(RoleStub.class, ga));
		assertNotNull(player2.requestRole(RoleStub.class, ga));
		assertNotNull(player3.requestRole(Role3Stub.class, ga));

		Mailbox mb1 = player1.getRole(ga, RoleStub.class).getMailbox();
		Mailbox mb2 = player2.getRole(ga, RoleStub.class).getMailbox();
		Mailbox mb3 = player3.getRole(ga, Role3Stub.class).getMailbox();
		
		Message message = new Message();
		
		// Send the message to be sure the message's fields are correctly set
		// and clear all the mail boxes.
		player2.broadcastMessage(ga, RoleStub.class, RoleStub.class, message);
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		mb1.clear();
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		mb2.clear();
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		mb3.clear();
		
		assertTrue(mb1.isEmpty());
		assertTrue(mb2.isEmpty());
		assertTrue(mb3.isEmpty());

		assertTrue(pContext.mts.forwardBroadcastMessage(message));
		
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		
		assertFalse(mb1.isEmpty());
		assertFalse(mb2.isEmpty());
		assertTrue(mb3.isEmpty());
		
		assertSame(message, mb1.removeFirst());
		assertSame(message, mb2.removeFirst());
	}
	
	/**
	 */
	public void testSetPrivilegedProperty() {
		PrivilegedContextStub pContext = new PrivilegedContextStub();
		CRIOContext crioContext = new CRIOContext(null, null, null, pContext);
		assertNotNull(pContext.jps);
		
		for(JanusProperty prop : JanusProperty.values()) {
			String previousValue = crioContext.getProperties().getProperty(prop);
			String nValue = UUID.randomUUID().toString();
			try {
				pContext.jps.setPrivilegedProperty(prop, nValue);
			}
			catch(Throwable e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				try {
					pw.print(prop.getPropertyName());
					pw.print(": "); //$NON-NLS-1$
					pw.println(e.toString());
					e.printStackTrace(pw);
				}
				finally {
					pw.close();
				}
				fail(sw.toString());
			}
			String currentValue = crioContext.getProperties().getProperty(prop);
			if (prop.isReadOnly()) {
				if (JanusProperties.isConstantProperty(prop)) 
					assertEquals(prop.getPropertyName(), previousValue, currentValue);
				else
					assertEquals(prop.getPropertyName(), nValue, currentValue);
			}
			else {
				assertEquals(prop.getPropertyName(), nValue, currentValue);
			}
		}
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class PrivilegedContextStub implements PrivilegedContext {

		/** MTS.
		 */
		public PrivilegedMessageTransportService mts;
		
		/** JPS.
		 */
		public PrivilegedJanusPropertySetter jps;

		/** PA.
		 */
		public PrivilegedPlayerAddressService pa;

		/**
		 */
		public PrivilegedContextStub() {
			//
		}
		
		/**{@inheritDoc}
		 */
		@Override
		public void setPrivilegedMessageTransportService(PrivilegedMessageTransportService mts) {
			this.mts = mts;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setPrivilegedJanusPropertySetter(PrivilegedJanusPropertySetter jps) {
			this.jps = jps;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setPrivilegedPersistentGroupCleanerService(PrivilegedPersistentGroupCleanerService pgc) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PrivilegedMessageTransportService getPrivilegedMessageTransportService() {
			return this.mts;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PrivilegedPersistentGroupCleanerService getPrivilegedPersistentGroupCleanerService() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PrivilegedJanusPropertySetter getPrivilegedJanusPropertySetter() {
			return this.jps;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setPrivilegedPlayerAddressService(PrivilegedPlayerAddressService pa) {
			this.pa = pa;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PrivilegedPlayerAddressService getPrivilegedPlayerAddressService() {
			return this.pa;
		}
		
	}

}
