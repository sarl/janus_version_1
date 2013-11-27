/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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

import java.util.Collection;
import java.util.Iterator;

import org.janusproject.kernel.schedule.AbstractActivator;
import org.janusproject.kernel.schedule.ActivationStage;
import org.janusproject.kernel.status.ExceptionStatus;
import org.janusproject.kernel.status.MultipleStatus;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.directaccess.DirectAccessCollection;
import org.janusproject.kernel.util.directaccess.SafeIterator;

/**
 * The base of the scheduling mecanisms.
 * Allows to schedule roles.
 * <p>
 * This activator is empty if there have no more role inside.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RoleActivator
extends AbstractActivator<Role> {

	/** 
	 */
	public RoleActivator() {
		super(Role.class);
	}

	/**
	 * @param scheduledRoles is the list of scheduled roles
	 */
	public RoleActivator(Collection<? extends Role> scheduledRoles) {
		super(Role.class, scheduledRoles);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Status executeInit(Iterator<? extends Role> roles, Object... parameters) {
		// Role are initialized when instanciated
		// See Group and Role.proceedPrivateInitialization() source codes
		return StatusFactory.ok(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Status executeBehaviour(Iterator<? extends Role> roles) {
		MultipleStatus ms = new MultipleStatus();
		Role r;
		while (roles.hasNext()) {
			r = roles.next();
			try {
				if (!r.wakeUpIfSleeping()) {
					ms.addStatus(r.proceedPrivateBehaviour());
				}
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable e) {
				ms.addStatus(new ExceptionStatus(e));
			}
			Thread.yield();
		}
		return ms.pack(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Status executeDestroy(Iterator<? extends Role> roles) {
		MultipleStatus ms = new MultipleStatus();
		Role r;
		while (roles.hasNext()) {
			r = roles.next();
			try {
				ms.addStatus(r.proceedPrivateDestruction());
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable e) {
				ms.addStatus(new ExceptionStatus(e));
			}
		}
		return ms.pack(this);
	}

	/**
	 * Add the role from the activator.
	 * 
	 * @param role is the role to add.
	 * @since 0.5
	 */
	void addRole(Role role) {
		addActivableObject(role);
	}

	/**
	 * Remove the role from the activator.
	 * 
	 * @param role is the role to remove.
	 * @since 0.5
	 */
	void removeRole(Role role) {
		removeActivableObject(role);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SafeIterator<Role> getExecutionPolicy(ActivationStage stage,
			DirectAccessCollection<Role> candidates) {
		return candidates.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Iterator<? extends Role> getExecutionPolicy(
			ActivationStage stage, Collection<? extends Role> candidates) {
		return candidates.iterator();
	}
	
}