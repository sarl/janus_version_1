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
package org.janusproject.kernel.bench.agent.message;

import java.util.concurrent.atomic.AtomicInteger;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.bench.api.BenchUtil;
import org.janusproject.kernel.status.Status;

/** Run the bench on the message API.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
class IddleAgent extends Agent {

	private static final long serialVersionUID = 8804935270327139156L;

	private AtomicInteger flag;
	
	/**
	 * @param flag
	 * @param sendingBenchmark indicates if the agent is used for message-sending or
	 * message-reading benchmarks.
	 */
	public IddleAgent(AtomicInteger flag, boolean sendingBenchmark) {
		this.flag = flag;
		if (sendingBenchmark)
			setMailbox(BenchUtil.createMailboxForSendingBenchs());
		else 
			setMailbox(BenchUtil.createMailboxForReadingBenchs());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		if (this.flag==null) {
			//
		}
		else {
			this.flag.incrementAndGet();
			this.flag = null;
		}
		return null;
	}

}
