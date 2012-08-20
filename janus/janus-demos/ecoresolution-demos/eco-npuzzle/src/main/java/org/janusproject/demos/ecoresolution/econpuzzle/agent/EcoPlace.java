/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011 Janus Core Developers
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
package org.janusproject.demos.ecoresolution.econpuzzle.agent;

import java.util.Set;

import org.janusproject.demos.ecoresolution.econpuzzle.agent.channel.EcoPlaceNPuzzleChannel;
import org.janusproject.ecoresolution.relation.EcoAttack;

/**
 * 
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class EcoPlace extends AbstractNPuzzleAgent {

	private static final long serialVersionUID = -8553335518051356495L;
	
	private final int placeIndex;

	/**
	 * @param label
	 * @param index
	 */
	public EcoPlace(String label, int index) {
		super();
		this.placeIndex = index;
		getAddress().setName(label);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EcoAgentChannel createEcoChannel() {
		return new EcoPlaceChannel();
	}

	@Override
	protected EcoAttack selectSatisfactionIntruder() {
		// attack celui qui empeche sat
		return null;
	}

	@Override
	protected Set<EcoAttack> selectEscapingIntruder(Set<EcoAttack> attacks) {
		// atack celui qui empeche la fuite

		return null;
	}

	@Override
	protected void doEscaping(Set<EcoAttack> attacks) {
		// free for escape alors escape
	}

	@Override
	protected void doSatisfactionIncreasing() {
		// free for sat
	}

	@Override
	protected void doSatisfied() {
		// sat finale
	}

	/**
	 * 
	 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
	 * 
	 */
	private class EcoPlaceChannel extends EcoAgentChannel implements EcoPlaceNPuzzleChannel {

		/**
		 */
		public EcoPlaceChannel() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentType getAgentType() {
			return AgentType.PLACE;
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public int getIndex() {
			return EcoPlace.this.placeIndex;
		}

	}
}
