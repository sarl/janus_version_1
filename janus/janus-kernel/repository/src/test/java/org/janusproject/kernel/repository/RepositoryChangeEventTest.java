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
package org.janusproject.kernel.repository;

import java.util.logging.Level;

import org.janusproject.kernel.repository.RepositoryChangeEvent.ChangeType;
import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RepositoryChangeEventTest extends TestCase {

	private Repository<?,?> repository;
	private RepositoryChangeEvent[] events;
	private Object changedObject;
	private Object oldValue;
	private Object newValue;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.repository = new HashRepository<Object,Object>();
		this.changedObject = new Object();
		this.oldValue = new Object();
		this.newValue = new Object();
		this.events = new RepositoryChangeEvent[ChangeType.values().length];
		for(int i=0; i<ChangeType.values().length; ++i) {
			ChangeType t = ChangeType.values()[i];
			this.events[i] = new RepositoryChangeEvent(
					this.repository,
					t,
					this.changedObject,
					this.oldValue,
					this.newValue);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.events = null;
		this.repository = null;
		this.changedObject = null;
		this.oldValue = null;
		this.newValue = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetSource() {
		for(int i=0; i<ChangeType.values().length; ++i) {
			assertSame(ChangeType.values()[i].name(), this.repository, this.events[i].getSource());
		}
	}

	/**
	 */
	public void testGetType(){
		for(int i=0; i<ChangeType.values().length; ++i) {
			assertEquals(ChangeType.values()[i].name(), ChangeType.values()[i], this.events[i].getType());
		}
	}

	/**
	 */
	public void testGetChangedObject(){
		for(int i=0; i<ChangeType.values().length; ++i) {
			assertSame(ChangeType.values()[i].name(), this.changedObject, this.events[i].getChangedObject());
		}
	}

	/**
	 */
	public void testGetOldValue(){
		for(int i=0; i<ChangeType.values().length; ++i) {
			assertSame(ChangeType.values()[i].name(), this.oldValue, this.events[i].getOldValue());
		}
	}

	/**
	 */
	public void testGetNewValue(){
		for(int i=0; i<ChangeType.values().length; ++i) {
			assertSame(ChangeType.values()[i].name(), this.newValue, this.events[i].getNewValue());
		}
	}

}
