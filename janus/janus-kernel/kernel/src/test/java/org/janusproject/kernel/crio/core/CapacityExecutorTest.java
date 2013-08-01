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

import java.util.UUID;
import java.util.logging.Level;

import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.util.random.RandomNumber;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CapacityExecutorTest extends TestCase {

	private CRIOContext context;
	private CapacityExecutor executor;
	private Organization organization;
	private GroupAddress address;
	private KernelScopeGroup group;
	private MembershipService membership;
	private boolean distributed, persistent;
	private RolePlayer player;
	
	/**
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.context = new CRIOContext(null);
		this.executor = this.context.getCapacityExecutor();
		assertNotNull(this.executor);
		this.organization = new Organization1Stub(this.context);
		this.address = new GroupAddress(UUID.randomUUID(), this.organization.getClass());
		this.membership = new MembershipServiceStub();
		this.distributed = RandomNumber.nextBoolean();
		this.persistent = RandomNumber.nextBoolean();
		this.group = new KernelScopeGroup(
				this.organization,
				this.address,
				this.distributed,
				this.persistent,
				this.membership);
		this.player = new RolePlayerStub(this.context);
		this.group.requestRole(this.player, RoleStub.class, null, null);
	}

	/**
	 */
	@Override
	protected void tearDown() throws Exception {
		this.executor = null;
		this.player = null;
		this.group = null;
		this.organization = null;
		this.address = null;
		this.membership = null;
		this.context = null;
		super.tearDown();
	}

	/**
	 * Test method for {@link org.janusproject.kernel.crio.core.CapacityExecutor#hasCapacityExecution()}.
	 * @throws Exception
	 */
	public void testHasCapacityExecution() throws Exception {
		assertFalse(this.executor.hasCapacityExecution());
	}	
	
	/**
	 * @throws Exception
	 */
	public void testExecute_success() throws Exception {
		CapacityContext context = CapacityExecutor.executeImmediately(
				CapacityStub.class,
				new CapacityImplementationStub(true, 0),
				this.player,
				this.group,
				this.group.getPlayedRole(this.player.getAddress(), RoleStub.class),
				'a','b','c');
		assertNotNull(context);
		assertNotNull(context.getIdentifier());
		assertFalse(context.isFailed());
		assertTrue(context.isResultAvailable());
		assertEquals(3, context.getOutputValueCount());
		assertNull(context.getOutputValueAt(-1));
		assertEquals(1, context.getOutputValueAt(0));
		assertEquals(2, context.getOutputValueAt(1));
		assertEquals(3, context.getOutputValueAt(2));
		assertNull(context.getOutputValueAt(3));
	}

	/**
	 * @throws Exception
	 */
	public void testExecute_failure() throws Exception {
		CapacityContext context = CapacityExecutor.executeImmediately(
				CapacityStub.class,
				new CapacityImplementationStub(false, 0),
				this.player,
				this.group,
				this.group.getPlayedRole(this.player.getAddress(), RoleStub.class),
				'a','b','c');
		assertNotNull(context);
		assertNotNull(context.getIdentifier());
		assertTrue(context.isFailed());
		assertFalse(context.isResultAvailable());
		assertEquals(0, context.getOutputValueCount());
		assertNull(context.getOutputValueAt(-1));
		assertNull(context.getOutputValueAt(0));
		assertNull(context.getOutputValueAt(1));
		assertNull(context.getOutputValueAt(2));
		assertNull(context.getOutputValueAt(3));
	}

	/**
	 * @throws Exception
	 */
	public void testSubmit_success() throws Exception {
		UUID id = this.executor.submit(
				CapacityStub.class,
				new CapacityImplementationStub(true, 1000),
				this.player,
				this.group,
				this.group.getPlayedRole(this.player.getAddress(), RoleStub.class),
				'a','b','c');
		assertNotNull(id);
		
		while (!this.executor.hasResult(this.player.getAddress(),id)) {
			Thread.sleep(500);
		}

		CapacityContext context = this.executor.waitResult(this.player.getAddress(),id);
		assertNotNull(context);
		assertFalse(context.isFailed());
		assertTrue(context.isResultAvailable());
		assertEquals(3, context.getOutputValueCount());
		assertNull(context.getOutputValueAt(-1));
		assertEquals(1, context.getOutputValueAt(0));
		assertEquals(2, context.getOutputValueAt(1));
		assertEquals(3, context.getOutputValueAt(2));
		assertNull(context.getOutputValueAt(3));
	}

	/**
	 * @throws Exception
	 */
	public void testSubmit_failure() throws Exception {
		UUID id = this.executor.submit(
				CapacityStub.class,
				new CapacityImplementationStub(false, 1000),
				this.player,
				this.group,
				this.group.getPlayedRole(this.player.getAddress(), RoleStub.class),
				'a','b','c');
		assertNotNull(id);
		
		CapacityContext context = this.executor.waitResult(this.player.getAddress(),id);
		assertNull(this.executor.waitResult(this.player.getAddress(),id));
		assertNotNull(context);
		assertTrue(context.isFailed());
		assertFalse(context.isResultAvailable());
		assertEquals(0, context.getOutputValueCount());
		assertNull(context.getOutputValueAt(-1));
		assertNull(context.getOutputValueAt(0));
		assertNull(context.getOutputValueAt(1));
		assertNull(context.getOutputValueAt(2));
		assertNull(context.getOutputValueAt(3));
	}

	/**
	 */
	public void testWaitResultAgentAddressUUIDLongTimeUnit_timeout() {
		UUID id = this.executor.submit(
				CapacityStub.class,
				new CapacityImplementationStub(true, 2000),
				this.player,
				this.group,
				this.group.getPlayedRole(this.player.getAddress(), RoleStub.class),
				'a','b','c');
		assertNotNull(id);

		assertNull(this.executor.waitResult(new AgentAddressStub(), id, 500));
		
		CapacityContext context = this.executor.waitResult(this.player.getAddress(),id, 500);
		assertNull(context);
		
		context = this.executor.waitResult(this.player.getAddress(),id);
		assertNotNull(context);
		assertFalse(context.isFailed());
		assertTrue(context.isResultAvailable());
		assertEquals(3, context.getOutputValueCount());
		assertNull(context.getOutputValueAt(-1));
		assertEquals(1, context.getOutputValueAt(0));
		assertEquals(2, context.getOutputValueAt(1));
		assertEquals(3, context.getOutputValueAt(2));
		assertNull(context.getOutputValueAt(3));
	}

	/**
	 */
	public void testWaitResultAgentAddressUUIDLongTimeUnit_notimeout() {
		UUID id = this.executor.submit(
				CapacityStub.class,
				new CapacityImplementationStub(true, 2000),
				this.player,
				this.group,
				this.group.getPlayedRole(this.player.getAddress(), RoleStub.class),
				'a','b','c');
		assertNotNull(id);

		assertNull(this.executor.waitResult(new AgentAddressStub(), id, 10000));
		
		CapacityContext context = this.executor.waitResult(this.player.getAddress(),id, 10000);
		assertNotNull(context);
		assertFalse(context.isFailed());
		assertTrue(context.isResultAvailable());
		assertEquals(3, context.getOutputValueCount());
		assertNull(context.getOutputValueAt(-1));
		assertEquals(1, context.getOutputValueAt(0));
		assertEquals(2, context.getOutputValueAt(1));
		assertEquals(3, context.getOutputValueAt(2));
		assertNull(context.getOutputValueAt(3));
	}

	/**
	 * @throws Exception
	 */
	public void testHasResultAgentAddressUUID() throws Exception {
		UUID id = this.executor.submit(
				CapacityStub.class,
				new CapacityImplementationStub(true, 2000),
				this.player,
				this.group,
				this.group.getPlayedRole(this.player.getAddress(), RoleStub.class),
				'a','b','c');
		assertNotNull(id);
		
		assertFalse(this.executor.hasResult(new AgentAddressStub(), id));

		assertFalse(this.executor.hasResult(this.player.getAddress(),id));
		while (!this.executor.hasResult(this.player.getAddress(),id)) {
			Thread.yield();
		}
		assertTrue(this.executor.hasResult(this.player.getAddress(),id));
		
		CapacityContext context = this.executor.waitResult(this.player.getAddress(),id);
		assertNotNull(context);
		assertFalse(this.executor.hasResult(this.player.getAddress(),id));
		assertFalse(context.isFailed());
		assertTrue(context.isResultAvailable());
		assertEquals(3, context.getOutputValueCount());
		assertNull(context.getOutputValueAt(-1));
		assertEquals(1, context.getOutputValueAt(0));
		assertEquals(2, context.getOutputValueAt(1));
		assertEquals(3, context.getOutputValueAt(2));
		assertNull(context.getOutputValueAt(3));
	}

	/**
	 * @throws Exception
	 */
	public void testInstantResultAgentAddressUUID() throws Exception {
		UUID id = this.executor.submit(
				CapacityStub.class,
				new CapacityImplementationStub(true, 2000),
				this.player,
				this.group,
				this.group.getPlayedRole(this.player.getAddress(), RoleStub.class),
				'a','b','c');
		assertNotNull(id);

		assertNull(this.executor.instantResult(new AgentAddressStub(), id));
		
		assertNull(this.executor.instantResult(this.player.getAddress(),id));
		while (!this.executor.hasResult(this.player.getAddress(),id)) {
			Thread.yield();
		}
		CapacityContext context = this.executor.instantResult(this.player.getAddress(),id);
		assertNotNull(context);
		assertFalse(this.executor.hasResult(this.player.getAddress(),id));
		assertNull(this.executor.instantResult(this.player.getAddress(),id));
		assertFalse(context.isFailed());
		assertTrue(context.isResultAvailable());
		assertEquals(3, context.getOutputValueCount());
		assertNull(context.getOutputValueAt(-1));
		assertEquals(1, context.getOutputValueAt(0));
		assertEquals(2, context.getOutputValueAt(1));
		assertEquals(3, context.getOutputValueAt(2));
		assertNull(context.getOutputValueAt(3));
	}

	/**
	 */
	public void testDoneAgentAddressUUIDObjectArray() {
		UUID id = this.executor.submit(
				CapacityStub.class,
				new CapacityImplementationStub(true, 100000),
				this.player,
				this.group,
				this.group.getPlayedRole(this.player.getAddress(), RoleStub.class),
				'a','b','c');
		assertNotNull(id);

		assertFalse(this.executor.done(new AgentAddressStub(), id, 'x', 'y', 'z'));
		
		assertTrue(this.executor.done(this.player.getAddress(), id, 'x', 'y', 'z'));
		
		CapacityContext context = this.executor.instantResult(this.player.getAddress(),id);
		assertNotNull(context);
		assertFalse(context.isFailed());
		assertTrue(context.isResultAvailable());
		assertEquals(3, context.getOutputValueCount());
		assertNull(context.getOutputValueAt(-1));
		assertEquals('x', context.getOutputValueAt(0));
		assertEquals('y', context.getOutputValueAt(1));
		assertEquals('z', context.getOutputValueAt(2));
		assertNull(context.getOutputValueAt(3));
	}

	/**
	 */
	public void testCancelAgentAddressUUID() {
		UUID id = this.executor.submit(
				CapacityStub.class,
				new CapacityImplementationStub(true, 10000),
				this.player,
				this.group,
				this.group.getPlayedRole(this.player.getAddress(), RoleStub.class),
				'a','b','c');
		assertNotNull(id);

		assertFalse(this.executor.cancel(new AgentAddressStub(), id));
		
		assertTrue(this.executor.cancel(this.player.getAddress(), id));
		
		CapacityContext context = this.executor.instantResult(this.player.getAddress(),id);
		assertNotNull(context);
		assertTrue(context.isFailed());
		assertNull(context.getFailureException());
		assertFalse(context.isResultAvailable());
		assertEquals(0, context.getOutputValueCount());
	}

	/**
	 */
	public void testCancelAgentAddressUUIDThrowable() {
		Throwable a = new Exception();
		UUID id = this.executor.submit(
				CapacityStub.class,
				new CapacityImplementationStub(true, 10000),
				this.player,
				this.group,
				this.group.getPlayedRole(this.player.getAddress(), RoleStub.class),
				'a','b','c');
		assertNotNull(id);

		assertFalse(this.executor.cancel(new AgentAddressStub(), id, a));
		
		assertTrue(this.executor.cancel(this.player.getAddress(), id, a));
		
		CapacityContext context = this.executor.instantResult(this.player.getAddress(),id);
		assertNotNull(context);
		assertTrue(context.isFailed());
		assertSame(a, context.getFailureException());
		assertFalse(context.isResultAvailable());
		assertEquals(0, context.getOutputValueCount());
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class CapacityImplementationStub extends CapacityImplementation implements CapacityStub {

		private final boolean failed;
		private final int sleep;
		
		/**
		 * @param f
		 * @param s
		 */
		public CapacityImplementationStub(boolean f, int s) {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
			this.failed = f;
			this.sleep = s;
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			assertEquals('a', call.getInputValues()[0]);
			assertEquals('b', call.getInputValues()[1]);
			assertEquals('c', call.getInputValues()[2]);
			
			if (this.sleep>0) {
				Thread.sleep(this.sleep);
			}
			
			call.setOutputValues(1,2,3);
			if (!this.failed) call.fail();
		}
		
	}

}
