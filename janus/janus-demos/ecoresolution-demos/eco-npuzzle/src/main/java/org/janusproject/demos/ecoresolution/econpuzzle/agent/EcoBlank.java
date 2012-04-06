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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.janusproject.demos.ecoresolution.econpuzzle.agent.channel.NPuzzleChannel;
import org.janusproject.demos.ecoresolution.econpuzzle.attack.ManhattanDistanceAttack;
import org.janusproject.demos.ecoresolution.econpuzzle.attack.MoveAttackReturn;
import org.janusproject.demos.ecoresolution.econpuzzle.exception.BadAcquaintancesException;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Down;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Hosted;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Left;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Right;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Up;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.EcoAttack;
import org.janusproject.ecoresolution.relation.EcoRelation;

/**
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class EcoBlank extends EcoTile {

	private static final long serialVersionUID = 5693347888901669240L;

	/**
	 * @param label
	 */
	public EcoBlank(String label) {
		super(label);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EcoAgentChannel createEcoChannel() {
		return new EcoBlankChannel();
	}

	@Override
	protected EcoAttack selectSatisfactionIntruder() {
		// attack celui qui empeche sat
		return null;
	}

	@Override
	protected Set<EcoAttack> selectEscapingIntruder(Set<EcoAttack> attacks) {
		// atack celui qui empeche la fuite
		Set<EcoAttack> attackToForward = new HashSet<EcoAttack>();
		
		Map<Class<? extends EcoAttack>, Set<EcoAttack>> attackClassification = classifyAttack(attacks);

		
		// manage and answer to moveAttack
		Set<EcoAttack> moveAttackReturns = attackClassification.get(MoveAttackReturn.class);

		if ((moveAttackReturns != null) && (moveAttackReturns.size() > 0)) {
			try {
				attackToForward.add(manageIncomingMoveAttack(moveAttackReturns));
			} catch (BadAcquaintancesException e) {
				e.printStackTrace();
			}
		}
				
		// Manhattan attack
		Set<EcoAttack> manhattanAttacks = attackClassification.get(ManhattanDistanceAttack.class);
		if (manhattanAttacks != null && manhattanAttacks.size() > 0) {
			try {
				attackToForward.addAll(manageManhattanAttack(attacks));
			} catch (BadAcquaintancesException e) {
				e.printStackTrace();
			}
		}

		return attackToForward;
	}

	@Override
	protected void doEscaping(Set<EcoAttack> attacks) {
		// free for escape alors escape
		System.out.println("doEscaping ?"); //$NON-NLS-1$
	}

	@Override
	protected void doSatisfactionIncreasing() {
		// free for sat
		System.out.println("doSatisfactionIncreasing ?"); //$NON-NLS-1$
	}

	@Override
	protected void doSatisfied() {// sat finale
		System.out.println("Cool I'M SATISFIED"); //$NON-NLS-1$
	}

	/**
	 * @param attacks
	 *            - a set of MoveAttack
	 * @return the list of my neighbor that haven't attack me, since I can't attack someone who already attack me
	 * @throws BadAcquaintancesException
	 */
	private MoveAttackReturn manageIncomingMoveAttack(Set<EcoAttack> attacks) throws BadAcquaintancesException {
		if (attacks.size() > 1) {// si j'ai plus d'un voisin qui m'attaque j'en choisi au hasard
			System.err.println("I'm attack by various move attack, choose randomly"); //$NON-NLS-1$
		}
		EcoAttack attack = attacks.iterator().next();
		EcoIdentity target = attack.getAssailant();
		EcoIdentity[] neighboors = getNeighboors();
		EcoIdentity me = this.getEcoIdentity();
		EcoRelation defenderUp;
		EcoRelation defenderDown;
		EcoRelation defenderRight;
		EcoRelation defenderLeft;
		EcoRelation defenderHosted;

		if (target.equals(neighboors[0])) {// move to up
			defenderUp = new Down(me, target);
			defenderDown = new Up(target, me);
			defenderRight = new Left(target, neighboors[1]);
			defenderLeft = new Right(target, neighboors[3]);
		} else if (target.equals(neighboors[1])) {// move to right
			defenderUp = new Down(target, neighboors[0]);
			defenderDown = new Up(target, neighboors[2]);
			defenderRight = new Left(me, target);
			defenderLeft = new Right(target, neighboors[3]);
		} else if (target.equals(neighboors[2])) {// move to down
			defenderUp = new Down(me, target);
			defenderDown = new Up(target, neighboors[2]);
			defenderRight = new Left(target, neighboors[1]);
			defenderLeft = new Right(target, neighboors[3]);
		} else if (target.equals(neighboors[3])) {// move to left
			defenderUp = new Down(target, neighboors[0]);
			defenderDown = new Up(target, neighboors[2]);
			defenderRight = new Left(me, target);
			defenderLeft = new Right(target, neighboors[3]);
		} else {
			throw new BadAcquaintancesException();
		}
		defenderHosted = new Hosted(target, getPlace());

		Collection<EcoRelation> newTargetAcquaintances = new LinkedList<EcoRelation>();
		newTargetAcquaintances.add(defenderUp);
		newTargetAcquaintances.add(defenderDown);
		newTargetAcquaintances.add(defenderRight);
		newTargetAcquaintances.add(defenderLeft);
		newTargetAcquaintances.add(defenderHosted);

		MoveAttackReturn ack = new MoveAttackReturn(me, target, newTargetAcquaintances);

		this.setAcquaintances(attack.getConstraints());
		return ack;
	}

	/**
	 * Forward manhattan attacks and manage the ones having as origin the blank tiles or my goals
	 * 
	 * @param attacks
	 *            a set of ManhattanDistanceAttack
	 * @return
	 * @throws BadAcquaintancesException
	 */
	private final Set<ManhattanDistanceAttack> manageManhattanAttack(Set<EcoAttack> attacks) throws BadAcquaintancesException {

		Set<ManhattanDistanceAttack> manhattanAttacks = new HashSet<ManhattanDistanceAttack>();

		// Source of attack, distance from source, list of neighbor having the same distance
		this.sourceDistAndNeighbors = new TreeMap<EcoIdentity, TreeMap<Integer, List<EcoIdentity>>>();

		// Manage the list of my neighbors that haven't sent the attack from the corresponding source, map<attack source, where to forward it>
		Map<EcoIdentity, List<EcoIdentity>> sourcePlaceAndAttackForwarders = new HashMap<EcoIdentity, List<EcoIdentity>>();

		List<EcoIdentity> neighboors = getNeighboorsAsList();

		// Find the minimal Manhattan distance of the various attacks having the same origin;
		// also register which of my neighbors send me this attack to forward it only to this other ones
		{
			EcoIdentity currentOrigin;
			Integer attackDist;
			List<EcoIdentity> attackForwarders;
			EcoIdentity neighboor;

			TreeMap<Integer, List<EcoIdentity>> distAndNeighbors;
			List<EcoIdentity> currentListOfNeighborHavingTheSameDist;

			for (EcoAttack attack : attacks) {
				currentOrigin = ((ManhattanDistanceAttack) attack).getAttackOrigin();
				neighboor = attack.getAssailant();
				attackDist = ((ManhattanDistanceAttack) attack).getManhattanDistance();

				distAndNeighbors = this.sourceDistAndNeighbors.get(currentOrigin);
				if (distAndNeighbors == null) {
					distAndNeighbors = new TreeMap<Integer, List<EcoIdentity>>();
					currentListOfNeighborHavingTheSameDist = new LinkedList<EcoIdentity>();
				} else {
					currentListOfNeighborHavingTheSameDist = distAndNeighbors.get(attackDist);

				}
				currentListOfNeighborHavingTheSameDist.add(neighboor);
				distAndNeighbors.put(attackDist, currentListOfNeighborHavingTheSameDist);
				this.sourceDistAndNeighbors.put(currentOrigin, distAndNeighbors);

				// who send me an attack ? which neighbors ?
				attackForwarders = sourcePlaceAndAttackForwarders.get(currentOrigin);
				if (attackForwarders != null) {
					attackForwarders.remove(neighboor);
					sourcePlaceAndAttackForwarders.put(currentOrigin, attackForwarders);
				} else {
					attackForwarders = new LinkedList<EcoIdentity>(neighboors);
					attackForwarders.remove(neighboor);
					sourcePlaceAndAttackForwarders.put(currentOrigin, attackForwarders);
				}
			}
		}

		// Determine the various attack I have to forward to those of my neighbor whose haven't received it
		// also determine the nearest neighbors from my goal and the blank tile
		{
			List<EcoIdentity> toFowards;
			EcoIdentity attackOrigin;
			Integer currentMinMahanttanDistance;
			Object[] params;

			for (Map.Entry<EcoIdentity, TreeMap<Integer, List<EcoIdentity>>> e : this.sourceDistAndNeighbors.entrySet()) {
				attackOrigin = e.getKey();
				currentMinMahanttanDistance = e.getValue().firstKey();

				//THE ONLY DIFFERENCE WITH ITS PARENT METHOD
				//if (!this.isSatisfied()) { // if I'm satisfied I do not forward the attack
					toFowards = sourcePlaceAndAttackForwarders.get(attackOrigin);
					for (EcoIdentity to : toFowards) {
						params = new Object[2];
						params[0] = currentMinMahanttanDistance;
						params[1] = e.getKey();
						manhattanAttacks.add(new ManhattanDistanceAttack(this.getEcoIdentity(), to, params));
					}
				//}

				// I'm under attack and I forward it to all of my neighbors
				if (attackOrigin.equals(this.getEcoIdentity())) {
					params = new Object[2];
					params[0] = 0;
					params[1] = attackOrigin;
					if (neighboors.get(0) != null)
						manhattanAttacks.add(new ManhattanDistanceAttack(this.getEcoIdentity(), neighboors.get(0), params));
					if (neighboors.get(1) != null)
						manhattanAttacks.add(new ManhattanDistanceAttack(this.getEcoIdentity(), neighboors.get(1), params));
					if (neighboors.get(2) != null)
						manhattanAttacks.add(new ManhattanDistanceAttack(this.getEcoIdentity(), neighboors.get(2), params));
					if (neighboors.get(3) != null)
						manhattanAttacks.add(new ManhattanDistanceAttack(this.getEcoIdentity(), neighboors.get(3), params));
				}
			}
		}

		return manhattanAttacks;
	}

	/**
	 * 
	 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
	 * 
	 */
	private class EcoBlankChannel extends EcoAgentChannel implements NPuzzleChannel {

		/**
		 */
		public EcoBlankChannel() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentType getAgentType() {
			return AgentType.BLANK;
		}

	}

}
