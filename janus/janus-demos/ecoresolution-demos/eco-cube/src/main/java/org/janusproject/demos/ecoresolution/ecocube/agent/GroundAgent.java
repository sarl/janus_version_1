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
package org.janusproject.demos.ecoresolution.ecocube.agent;

import java.util.Set;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.ecoresolution.agent.EcoAgent;
import org.janusproject.ecoresolution.relation.EcoAttack;

/**
 * A plane eco-agent.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class GroundAgent extends EcoAgent {
	
	private static final long serialVersionUID = 2636612083512327873L;

	/**
	 */
	public GroundAgent() {
		getAddress().setName(Locale.getString(GroundAgent.class, "TEXT")); //$NON-NLS-1$
	}

	/** {@inheritDoc}
	 */
	@Override
	protected EcoAgentChannel createEcoChannel() {
		return new CubeEcoAgentChannel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doEscaping(Set<EcoAttack> attacks) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSatisfactionIncreasing() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSatisfied() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<EcoAttack> selectEscapingIntruder(Set<EcoAttack> attacks) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EcoAttack selectSatisfactionIntruder() {
		return null;
	}

	/** Implementation of an eco-channel.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	private class CubeEcoAgentChannel extends EcoAgentChannel implements CubeEcoChannel {

		/**
		 */
		public CubeEcoAgentChannel() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentType getAgentType() {
			return AgentType.PLANE;
		}
		
	}

}
