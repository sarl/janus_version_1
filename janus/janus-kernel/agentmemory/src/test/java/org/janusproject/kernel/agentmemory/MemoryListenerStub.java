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
package org.janusproject.kernel.agentmemory;

import junit.framework.AssertionFailedError;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class MemoryListenerStub implements MemoryListener {

	/**
	 */
	public String changed = null;
	
	/**
	 */
	public MemoryListenerStub() {
		//
	}
	
	/** Reset the stub.
	 */
	public void reset() {
		this.changed = null;
	}
	
	/** test if the listener has reserved an event for
	 * the given value.
	 * Invoke {@link #reset()} on success.
	 * 
	 * @param expected
	 */
	public void assertEquals(String expected) {
		if (expected==this.changed ||
			(expected!=null && expected.equals(this.changed))) {
			reset();
			return;
		}
		throw new AssertionFailedError(
				"expected change in memory for: " //$NON-NLS-1$
						+expected
						+"; actual: " //$NON-NLS-1$
						+this.changed);
	}

	@Override
	public void onKnownledgeChanged(MemoryEvent event) {
		this.changed = event.getKnowledgeIdentifier();
	}
	
}
