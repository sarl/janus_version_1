/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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

import java.util.UUID;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.repository.RepositoryChangeEvent;
import org.janusproject.kernel.repository.RepositoryChangeEvent.ChangeType;
import org.janusproject.kernel.repository.RepositoryChangeListener;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.util.random.RandomNumber;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class GroupRepositoryTest extends TestCase {

	private CRIOContext context;
	private Organization organization;
	private MembershipService membership;
	private boolean distributed, persistent;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.context = new CRIOContext(null);
		this.organization = OrganizationRepository.organization(
				this.context, Organization1Stub.class, null);
		this.membership = new MembershipServiceStub();
		this.distributed = RandomNumber.nextBoolean();
		this.persistent = RandomNumber.nextBoolean();
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.organization = null;
		this.membership = null;
		this.context = null;
		super.tearDown();
	}

	/**
	 */
	public void testNewGroup() {
		GroupAddress grp = this.context.getGroupRepository().newGroup(null, this.organization, null, null, this.membership, this.distributed, this.persistent, null);
		assertNotNull(grp);
		assertSame(this.organization.getClass(), grp.getOrganization());
	}

	/**
	 */
	public void testNewGroupEvent(){
		ListenerStub l = new ListenerStub();
		this.context.getGroupRepository().addRepositoryChangeListener(l);

		GroupAddress grp = this.context.getGroupRepository().newGroup(null, this.organization, null, null, this.membership, this.distributed, this.persistent, null);
		
		assertNotNull(l.event);
		assertEquals(ChangeType.ADD, l.event.getType());
		assertNull(l.event.getOldValue());
		Object newValue = l.event.getNewValue();
		assertNotNull(newValue);
		assertTrue( newValue instanceof KernelScopeGroup);
		KernelScopeGroup kgrp = (KernelScopeGroup)newValue;
		assertEquals(grp, kgrp.getAddress());
		
		this.context.getGroupRepository().remove(grp);
	}
	
	/**
	 */
	public void testGetGroup() {
		GroupAddress adr = new GroupAddress(UUID.randomUUID(), this.organization.getClass());
		KernelScopeGroup grp;
		
		grp = this.context.getGroupRepository().get(adr);
		assertNull(grp);
		
		adr = this.context.getGroupRepository().newGroup(null, this.organization, null, null, this.membership, this.distributed, this.persistent, null);
		
		grp = this.context.getGroupRepository().get(adr);
		assertNotNull(grp);		
		assertSame(this.organization, grp.getOrganization());
		assertSame(adr, grp.getAddress());
	}
	
	/**
	 */
	public void testContainsGroup() {
		GroupAddress adr = new GroupAddress(UUID.randomUUID(), this.organization.getClass());
		KernelScopeGroup grp;
		
		grp = this.context.getGroupRepository().get(adr);
		assertNull(grp);
		
		adr = this.context.getGroupRepository().newGroup(null, this.organization, null, null, this.membership, this.distributed, this.persistent, null);
		
		grp = this.context.getGroupRepository().get(adr);
		assertNotNull(grp);		
		assertSame(this.organization, grp.getOrganization());
		assertSame(adr, grp.getAddress());
		
		assertTrue(this.context.getGroupRepository().containsGroup(adr.getUUID(), adr.getOrganization())!=null);
	}

	/**
	 */
	public void testRemoveGroup() {
		GroupAddress adr = this.context.getGroupRepository().newGroup(null, this.organization, null, null, this.membership, this.distributed, this.persistent, null);

		KernelScopeGroup grp = this.context.getGroupRepository().get(adr);
		assertNotNull(grp);		
		assertSame(this.organization, grp.getOrganization());
		assertSame(adr, grp.getAddress());
		
		this.context.getGroupRepository().removeGroup(adr);

		grp = this.context.getGroupRepository().get(adr);
		assertNull(grp);		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class ListenerStub implements RepositoryChangeListener {

		/** Last fired event.
		 */
		public RepositoryChangeEvent event = null;
		
		/**
		 */
		public ListenerStub() {
			//
		}
		
		/** {@inheritDoc}
		 */
		@Override
		public void repositoryChanged(RepositoryChangeEvent evt) {
			this.event = evt;
		}
		
	}
	
}
