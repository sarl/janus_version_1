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
package org.janusproject.demos.ecoresolution.ecocube.agent;

import java.util.HashSet;
import java.util.Set;

import org.janusproject.ecoresolution.identity.AnyIdentity;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.EcoAttack;
import org.janusproject.ecoresolution.relation.EcoRelation;

/**
 * A cube eco-agent.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class CubeAgent extends AbstractCubeProblemAgent {
	
	private static final long serialVersionUID = -7794599449753464077L;
	
	private final EcoIdentity planeAgent;
	
	/**
	 * @param label
	 * @param target
	 * @param planeAgent
	 */
	public CubeAgent(String label, EcoIdentity target, EcoIdentity planeAgent) {
		getAddress().setName(label);
		this.planeAgent = planeAgent;
		setGoal(downwardRelation(target));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EcoIdentity getTableIdentity() {
		return this.planeAgent;
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
	public EcoAttack selectSatisfactionIntruder() {
		EcoRelation goal = getGoal();
		if (goal!=null) {
			EcoIdentity me = getEcoIdentity();
			// Is a box on me? If yes it is an intruder
			EcoRelation onTop = getAcquaintance(upwardRelation(me, AnyIdentity.SINGLETON));
			if (onTop!=null) {
				return attack(onTop.getOtherParticipant(me));
			}			
			// Does the target box is free? If not the box on my target is an intruder.
			onTop = getAcquaintance(upwardRelation(goal.getSlave(), AnyIdentity.SINGLETON));
			if (onTop!=null) {
				return attack(onTop.getOtherParticipant(goal.getSlave()));
			}
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSatisfactionIncreasing() {
		EcoRelation goal = getGoal();
		if (goal!=null) {
			setAcquaintance(goal);
		}
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
		EcoRelation conflictingRelation = upwardRelation(AnyIdentity.SINGLETON);
		EcoRelation onTop = getAcquaintance(conflictingRelation);
		if (onTop!=null) {
			Set<EcoAttack> s = new HashSet<EcoAttack>();
			s.add(attack(onTop.getOtherParticipant(getEcoIdentity())));
			return s;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doEscaping(Set<EcoAttack> attacks) {
		// Move on table
		setAcquaintance(downwardRelation(this.planeAgent));
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
			return AgentType.CUBE;
		}
		
	}
	
}
