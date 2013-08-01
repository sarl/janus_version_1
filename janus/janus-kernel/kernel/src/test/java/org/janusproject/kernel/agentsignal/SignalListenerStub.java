/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.janusproject.kernel.agentsignal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

/**
 * Stub for signal listener.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class SignalListenerStub implements SignalListener {

	private final List<Signal> signals = new ArrayList<Signal>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSignal(Signal signal) {
		this.signals.add(signal);
	}
	
	/** Assert no signal was received.
	 */
	public void assertNull() {
		if (!this.signals.isEmpty()) {
			Assert.fail("unexpected signals: "+this.signals.toString()); //$NON-NLS-1$
		}
	}
	
	/** Assert that the given signal was received.
	 * 
	 * @param s
	 */
	public void assertSignal(Signal s) {
		Iterator<Signal> iterator = this.signals.iterator();
		while (iterator.hasNext()) {
			Signal sig = iterator.next();
			if (s==sig || sig.equals(s)) {
				iterator.remove();
				return;
			}
		}
		Assert.fail("expecting signal: "+s.toString()); //$NON-NLS-1$
	}

}