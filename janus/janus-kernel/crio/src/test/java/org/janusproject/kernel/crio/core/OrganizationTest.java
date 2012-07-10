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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class OrganizationTest extends TestCase {

	private CRIOContext context;
	private Organization organization;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.context = new CRIOContext(null);
		this.organization = OrganizationRepository.organization(
				this.context,
				Organization1Stub.class,
				null);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.organization = null;
		this.context = null;
		super.tearDown();
	}
	
	private static void assertEquals(Collection<?> c1, Collection<?> c2) {
		if (c1==c2) return;
		if (c1!=null && c2!=null && c1.size()==c2.size()) {
			try {
				ArrayList<Object> obj = new ArrayList<Object>(c2);
				Iterator<?> iterator = c1.iterator();
				boolean failure = false;
				Object o1;
				while (iterator.hasNext() && !failure) {
					o1 = iterator.next();
					failure = !obj.remove(o1);
				}
				if (!failure && obj.isEmpty()) return;
			}
			catch(AssertionError ae) {
				throw ae;
			}
			catch(Throwable _) {
				//
			}
		}
		fail("collections are not equal. Expected: " //$NON-NLS-1$
				+((c1==null)?null:c1.toString())
				+"; Actual: " //$NON-NLS-1$
				+((c2==null)?null:c2.toString()));
	}
	
	private static void assertEmpty(Collection<?> c1) {
		if (c1!=null) {
			if (c1.isEmpty()) return;
			fail("collections is not empty"); //$NON-NLS-1$
		}
		else {
			fail("collection is null"); //$NON-NLS-1$
		}
	}

	/**
	 */
	public void testContainsClass() {
		assertTrue(this.organization.contains(RoleStub.class));
		assertFalse(this.organization.contains(Role2Stub.class));
		
		this.organization.addRole(Role2Stub.class);

		assertTrue(this.organization.contains(RoleStub.class));
		assertTrue(this.organization.contains(Role2Stub.class));
	}

	/**
	 */
	public void testIterator() {
		Iterator<Class<? extends Role>> iterator;
		
		iterator = this.organization.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(RoleStub.class, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(Role3Stub.class, iterator.next());
		assertFalse(iterator.hasNext());
		
		this.organization.addRole(Role2Stub.class);

		iterator = this.organization.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(RoleStub.class, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(Role3Stub.class, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(Role2Stub.class, iterator.next());
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testGetDefinedRoles() {
		Collection<Class<? extends Role>> collection;
		Iterator<Class<? extends Role>> iterator;
		
		collection = this.organization.getDefinedRoles();
		assertNotNull(collection);
		assertEquals(2, collection.size());
		iterator = collection.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(RoleStub.class, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(Role3Stub.class, iterator.next());
		assertFalse(iterator.hasNext());
		
		
		this.organization.addRole(Role2Stub.class);

		collection = this.organization.getDefinedRoles();
		assertNotNull(collection);
		assertEquals(3, collection.size());
		iterator = collection.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(RoleStub.class, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(Role3Stub.class, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(Role2Stub.class, iterator.next());
		assertFalse(iterator.hasNext());
	}
		
	/**
	 */
	public void testRemoveRoleClass() {
		Iterator<Class<? extends Role>> iterator;
		
		this.organization.removeRole(Role2Stub.class);
		
		iterator = this.organization.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(RoleStub.class, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(Role3Stub.class, iterator.next());
		assertFalse(iterator.hasNext());

		this.organization.removeRole(RoleStub.class);
		
		iterator = this.organization.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(Role3Stub.class, iterator.next());
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testCreateGroupCollectionCollection() {
		Collection<? extends GroupCondition> obtain1 = new ArrayList<GroupCondition>();
		Collection<? extends GroupCondition> leave1 = new ArrayList<GroupCondition>();
		Collection<? extends GroupCondition> obtain2 = new ArrayList<GroupCondition>();
		Collection<? extends GroupCondition> leave2 = new ArrayList<GroupCondition>();
		
		GroupAddress g1 = this.organization.createGroup(obtain1, leave1);
		GroupAddress g2 = this.organization.createGroup(obtain2, leave2);
		
		assertNotNull(g1);
		assertNotNull(g2);
		assertFalse(g1.equals(g2));
		
		KernelScopeGroup gr1 = this.context.getGroupRepository().get(g1);
		KernelScopeGroup gr2 = this.context.getGroupRepository().get(g2);
		
		assertNotNull(gr1);
		assertNotNull(gr2);
		assertNotSame(gr1, gr2);
		
		assertSame(this.organization, gr1.getOrganization());
		assertSame(this.organization, gr2.getOrganization());
		
		assertEquals(obtain1, gr1.getObtainConditions());
		assertEquals(leave1, gr1.getLeaveConditions());

		assertEquals(obtain2, gr2.getObtainConditions());
		assertEquals(leave2, gr2.getLeaveConditions());
	}

	/**
	 */
	public void testCreateGroupCollectionCollectionString() {
		String gName1 = "TheGroup~1"; //$NON-NLS-1$
		String gName2 = "TheGroup~2"; //$NON-NLS-1$
		Collection<? extends GroupCondition> obtain1 = new ArrayList<GroupCondition>();
		Collection<? extends GroupCondition> leave1 = new ArrayList<GroupCondition>();
		Collection<? extends GroupCondition> obtain2 = new ArrayList<GroupCondition>();
		Collection<? extends GroupCondition> leave2 = new ArrayList<GroupCondition>();
		
		GroupAddress g1 = this.organization.createGroup(obtain1, leave1, gName1);
		GroupAddress g2 = this.organization.createGroup(obtain2, leave2, gName2);
		
		assertNotNull(g1);
		assertNotNull(g2);
		assertFalse(g1.equals(g2));
		assertEquals(gName1, g1.getName());
		assertEquals(gName2, g2.getName());
		
		KernelScopeGroup gr1 = this.context.getGroupRepository().get(g1);
		KernelScopeGroup gr2 = this.context.getGroupRepository().get(g2);
		
		assertNotNull(gr1);
		assertNotNull(gr2);
		assertNotSame(gr1, gr2);
		
		assertSame(this.organization, gr1.getOrganization());
		assertSame(this.organization, gr2.getOrganization());
		
		assertEquals(obtain1, gr1.getObtainConditions());
		assertEquals(leave1, gr1.getLeaveConditions());

		assertEquals(obtain2, gr2.getObtainConditions());
		assertEquals(leave2, gr2.getLeaveConditions());
	}

	/**
	 */
	public void testCreateGroupCollectionCollectionMembershipBooleanBooleanString() {
		String gName1 = "TheGroup~1"; //$NON-NLS-1$
		String gName2 = "TheGroup~2"; //$NON-NLS-1$
		MembershipService member1 = new MembershipServiceStub();
		MembershipService member2 = new MembershipServiceStub();
		Collection<? extends GroupCondition> obtain1 = new ArrayList<GroupCondition>();
		Collection<? extends GroupCondition> leave1 = new ArrayList<GroupCondition>();
		Collection<? extends GroupCondition> obtain2 = new ArrayList<GroupCondition>();
		Collection<? extends GroupCondition> leave2 = new ArrayList<GroupCondition>();
		
		GroupAddress g1 = this.organization.createGroup(obtain1, leave1, member1, true, false, gName1);
		GroupAddress g2 = this.organization.createGroup(obtain2, leave2, member2, false, true, gName2);
		
		assertNotNull(g1);
		assertNotNull(g2);
		assertFalse(g1.equals(g2));
		assertEquals(gName1, g1.getName());
		assertEquals(gName2, g2.getName());
		
		KernelScopeGroup gr1 = this.context.getGroupRepository().get(g1);
		KernelScopeGroup gr2 = this.context.getGroupRepository().get(g2);
		
		assertNotNull(gr1);
		assertNotNull(gr2);
		assertNotSame(gr1, gr2);
		
		assertSame(this.organization, gr1.getOrganization());
		assertSame(this.organization, gr2.getOrganization());
		
		assertEquals(obtain1, gr1.getObtainConditions());
		assertEquals(leave1, gr1.getLeaveConditions());

		assertEquals(obtain2, gr2.getObtainConditions());
		assertEquals(leave2, gr2.getLeaveConditions());
		
		assertSame(member1, gr1.getMembership());
		assertSame(member2, gr2.getMembership());
		
		assertFalse(gr1.isDistributed()); // false because the kernel is not nerworking.
		assertFalse(gr2.isDistributed());

		assertFalse(gr1.isPersistent());
		assertTrue(gr2.isPersistent());
	}

	/**
	 */
	public void testCreateGroupUUIDCollectionCollectionMembershipBooleanBooleanString() {
		UUID id1 = UUID.randomUUID();
		UUID id2 = UUID.randomUUID();
		String gName1 = "TheGroup~1"; //$NON-NLS-1$
		String gName2 = "TheGroup~2"; //$NON-NLS-1$
		MembershipService member1 = new MembershipServiceStub();
		MembershipService member2 = new MembershipServiceStub();
		Collection<? extends GroupCondition> obtain1 = new ArrayList<GroupCondition>();
		Collection<? extends GroupCondition> leave1 = new ArrayList<GroupCondition>();
		Collection<? extends GroupCondition> obtain2 = new ArrayList<GroupCondition>();
		Collection<? extends GroupCondition> leave2 = new ArrayList<GroupCondition>();
		
		GroupAddress g1 = this.organization.createGroup(id1, obtain1, leave1, member1, true, false, gName1);
		GroupAddress g2 = this.organization.createGroup(id2, obtain2, leave2, member2, false, true, gName2);
		
		assertNotNull(g1);
		assertNotNull(g2);
		assertFalse(g1.equals(g2));
		assertEquals(id1, g1.getUUID());
		assertEquals(gName1, g1.getName());
		assertEquals(id2, g2.getUUID());
		assertEquals(gName2, g2.getName());
		
		KernelScopeGroup gr1 = this.context.getGroupRepository().get(g1);
		KernelScopeGroup gr2 = this.context.getGroupRepository().get(g2);
		
		assertNotNull(gr1);
		assertNotNull(gr2);
		assertNotSame(gr1, gr2);
		
		assertSame(this.organization, gr1.getOrganization());
		assertSame(this.organization, gr2.getOrganization());
		
		assertEquals(obtain1, gr1.getObtainConditions());
		assertEquals(leave1, gr1.getLeaveConditions());

		assertEquals(obtain2, gr2.getObtainConditions());
		assertEquals(leave2, gr2.getLeaveConditions());
		
		assertSame(member1, gr1.getMembership());
		assertSame(member2, gr2.getMembership());
		
		assertFalse(gr1.isDistributed()); // false because the kernel is not nerworking.
		assertFalse(gr2.isDistributed());

		assertFalse(gr1.isPersistent());
		assertTrue(gr2.isPersistent());
	}

	/**
	 */
	public void testCreateGroup() {
		GroupAddress g1 = this.organization.createGroup();
		GroupAddress g2 = this.organization.createGroup();
		
		assertNotNull(g1);
		assertNotNull(g2);
		assertFalse(g1.equals(g2));
		
		KernelScopeGroup gr1 = this.context.getGroupRepository().get(g1);
		KernelScopeGroup gr2 = this.context.getGroupRepository().get(g2);
		
		assertNotNull(gr1);
		assertNotNull(gr2);
		assertNotSame(gr1, gr2);
		
		assertSame(this.organization, gr1.getOrganization());
		assertSame(this.organization, gr2.getOrganization());
		
		assertEmpty(gr1.getObtainConditions());
		assertEmpty(gr1.getLeaveConditions());

		assertEmpty(gr2.getObtainConditions());
		assertEmpty(gr2.getLeaveConditions());
	}

	/**
	 */
	public void testCreateGroupString() {
		String gName1 = "Group~1"; //$NON-NLS-1$
		String gName2 = "Group~2"; //$NON-NLS-1$
		
		GroupAddress g1 = this.organization.createGroup(gName1);
		GroupAddress g2 = this.organization.createGroup(gName2);
		
		assertNotNull(g1);
		assertNotNull(g2);
		assertFalse(g1.equals(g2));
		assertEquals(gName1, g1.getName());
		assertEquals(gName2, g2.getName());
	}

	/**
	 */
	public void testIsGroupGroupAddress() {
		GroupAddress g1 = this.organization.createGroup();
		GroupAddress g2 = this.organization.createGroup();
		GroupAddress g3 = new GroupAddress(UUID.randomUUID(), this.organization.getClass());

		assertTrue(this.organization.isGroup(g1));
		assertTrue(this.organization.isGroup(g2));
		assertFalse(this.organization.isGroup(g3));
	}

	/**
	 */
	public void testGetGroup() {
		assertNull(this.organization.getGroup());
		
		GroupAddress g1 = this.organization.createGroup();
		
		assertEquals(g1, this.organization.getGroup());
		assertEquals(g1, this.organization.getGroup());
	}

	/**
	 */
	public void testGetGroupUUIDString() {
		UUID rId = UUID.randomUUID();
		String rName = rId.toString();
		
		assertNull(this.organization.getGroup(rId, rName));
		
		GroupAddress g1 = this.organization.createGroup();
		
		assertEquals(g1, this.organization.getGroup(g1.getUUID(), null));
		assertEquals(g1, this.organization.getGroup(g1.getUUID(), rName));
		assertEquals(g1, this.organization.getGroup(g1.getUUID(), g1.getName()));
	}

	/**
	 */
	public void testGroup() {
		GroupAddress g1 = this.organization.group();
		assertEquals(g1, this.organization.group());
		assertEquals(g1, this.organization.group());
	}
	
	/**
	 */
	public void testGroupString() {
		String gName = "TOTO"; //$NON-NLS-1$
		GroupAddress g1 = this.organization.createGroup(gName);
		assertEquals(g1, this.organization.group(gName));
		assertEquals(g1, this.organization.group(gName));
	}

	/**
	 */
	public void testGroupUUIDCollectionCollectionMembershipServiceBooleanBooleanString() {
		String gName = "TheName"; //$NON-NLS-1$

		GroupAddress g1 = this.organization.createGroup();
		assertEquals(g1, this.organization.group(g1.getUUID(), null, null, null, true, true, gName));
		assertNull(g1.getName());
		
		GroupAddress g2 = this.organization.group(UUID.randomUUID(), null, null, null, true, true, gName);
		assertEquals(gName, g2.getName());
	}

	/**
	 */
	public void testHasGroup() {
		assertFalse(this.organization.hasGroup());
		this.organization.group();
		assertTrue(this.organization.hasGroup());
		this.organization.group();
		assertTrue(this.organization.hasGroup());
	}

}