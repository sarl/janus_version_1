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

import java.util.Arrays;
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
import org.janusproject.demos.ecoresolution.econpuzzle.attack.MoveAttack;
import org.janusproject.demos.ecoresolution.econpuzzle.attack.MoveAttackReturn;
import org.janusproject.demos.ecoresolution.econpuzzle.exception.BadAcquaintancesException;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Blank;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Down;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Hosted;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Left;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Right;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Up;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.EcoAttack;
import org.janusproject.ecoresolution.relation.EcoRelation;

/**
 * 
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class EcoTile extends AbstractNPuzzleAgent {

	private static final long serialVersionUID = 3336487729011111956L;

	/**
	 * Double map storing all distance information received through the manhattan attack field process Associate the initial source of an attack, with distance to this source and the list of neighbor located at this distance from the source <Source of attack, distance from source, list of neighbor having the same distance>
	 */
	protected Map<EcoIdentity, TreeMap<Integer, List<EcoIdentity>>> sourceDistAndNeighbors = new TreeMap<EcoIdentity, TreeMap<Integer, List<EcoIdentity>>>();

	private boolean isWaitingMoveAttackReturn = false;

	/**
	 * @param label
	 */
	public EcoTile(String label) {
		assert (label != null);
		getAddress().setName(label);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EcoAgentChannel createEcoChannel() {
		return new EcoTileChannel();
	}

	@Override
	protected EcoAttack selectSatisfactionIntruder() {
		// attack celui qui empeche sat, mes acquaintances n'ont pas changé
		try {
			TreeMap<Integer, List<EcoIdentity>> neighborsFromGoal = this.sourceDistAndNeighbors.get(getGoal());
			if (neighborsFromGoal != null) {

				List<EcoIdentity> nearestNeighborsFromGoal = neighborsFromGoal.get(neighborsFromGoal.firstKey());

				if (nearestNeighborsFromGoal != null) {
					if (nearestNeighborsFromGoal.size() > 1) {
						System.err.println("selectSatisfactionIntruder: Various neighboors having the same distance to my goal, choose the first"); //$NON-NLS-1$
						return moveTo(nearestNeighborsFromGoal.get(0));
					}
					return moveTo(nearestNeighborsFromGoal.get(0));
				}
				System.err.println("selectSatisfactionIntruder: no neighboor is near my goal, launch manhattan attack for my goal"); //$NON-NLS-1$
				return launchManhattanAttack(getGoal().getSlave());

			}
			System.err.println("selectSatisfactionIntruder: nothing in distance map, launch manhattan attack for my goal"); //$NON-NLS-1$
			return launchManhattanAttack(getGoal().getSlave());
		} catch (BadAcquaintancesException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected Set<EcoAttack> selectEscapingIntruder(Set<EcoAttack> attacks) {
		// attack celui qui empeche la fuite, mes acquaintances ont changé

		Set<EcoAttack> attackToForward = new HashSet<EcoAttack>();

		Map<Class<? extends EcoAttack>, Set<EcoAttack>> attackClassification = classifyAttack(attacks);

		Set<EcoAttack> moveAttackReturns = attackClassification.get(MoveAttackReturn.class);
		if (this.isWaitingMoveAttackReturn) {// I was waiting for updating my acquaintances
			if ((moveAttackReturns != null) && (moveAttackReturns.size() > 0)) {
				manageMoveAttackReturn(moveAttackReturns);
			} else {
				System.err.println("selectEscapingIntruder: My Acquaintances have not been updated I can't do anything"); //$NON-NLS-1$
			}
		}

		if (!this.isWaitingMoveAttackReturn) {// if not, it means that my Acquaintances have not been updated so I can't do anything
			// Manhattan attack
			Set<EcoAttack> manhattanAttacks = attackClassification.get(ManhattanDistanceAttack.class);
			if (manhattanAttacks != null && manhattanAttacks.size() > 0) {
				try {
					attackToForward.addAll(manageManhattanAttack(attacks));
				} catch (BadAcquaintancesException e) {
					e.printStackTrace();
				}
			}

			Set<EcoAttack> moveAttacks = attackClassification.get(MoveAttack.class);
			if (moveAttacks != null && moveAttacks.size() > 0) {// really attack, not just manhattan
				List<EcoIdentity> passiveNeighbors = null;
				try {
					passiveNeighbors = manageIncomingMoveAttack(moveAttacks);
				} catch (BadAcquaintancesException e) {
					e.printStackTrace();
				}

				if (passiveNeighbors != null && passiveNeighbors.size() > 0) {
					if (passiveNeighbors.size() > 1) {
						// choose the best passive neighbor
						// flee to the goal
						TreeMap<Integer, List<EcoIdentity>> neighborsFromGoal = this.sourceDistAndNeighbors.get(getGoal());
						boolean done = false;
						if (neighborsFromGoal != null) {

							List<EcoIdentity> nearestNeighborsFromGoal = neighborsFromGoal.get(neighborsFromGoal.firstKey());

							if (nearestNeighborsFromGoal != null) {
								nearestNeighborsFromGoal.retainAll(passiveNeighbors);
								if (nearestNeighborsFromGoal.size() > 0) {
									try {
										attackToForward.add(moveTo(nearestNeighborsFromGoal.get(0)));
										done = true;
									} catch (BadAcquaintancesException e) {
										e.printStackTrace();
									}
								}
							}

						}
						// or flee to the neighbor nearest to blank tile
						if (!done) {
							try {
								TreeMap<Integer, List<EcoIdentity>> neighborsFromBlank = this.sourceDistAndNeighbors.get(getBlank());
								if (neighborsFromBlank != null) {
									List<EcoIdentity> nearestNeighborsFromBlank = neighborsFromBlank.get(neighborsFromBlank.firstKey());

									if (nearestNeighborsFromBlank != null) {
										nearestNeighborsFromBlank.retainAll(passiveNeighbors);
										if (nearestNeighborsFromBlank.size() > 0) {
											try {
												attackToForward.add(moveTo(nearestNeighborsFromBlank.get(0)));
											} catch (BadAcquaintancesException e) {
												e.printStackTrace();
											}
										}
									}

								}
							} catch (BadAcquaintancesException e) {
								e.printStackTrace();
							}
						} else {
							System.err.println("selectEscapingIntruder: Any of neighbor has a computed distance from the goal or blank, launching manhattan process for my goal and blank"); //$NON-NLS-1$
							attackToForward.add(launchManhattanAttack(getGoal().getSlave()));
							try {
								attackToForward.add(launchManhattanAttack(getBlank()));
							} catch (BadAcquaintancesException e) {
								e.printStackTrace();
							}
						}
					} else {// only one possibility, move to the only passive neighbor
						try {
							attackToForward.add(moveTo(passiveNeighbors.get(0)));
						} catch (BadAcquaintancesException e) {
							e.printStackTrace();
						}
					}
				} else {
					// no possibility, all my neighbor attacks me
					// flee to the goal ?
					// flee to the neighboor nearest to blank tile ?
					System.err.println("selectEscapingIntruder: All my neighbors attack me, what shall I do ?"); //$NON-NLS-1$
				}

			} else {// increase my sat
				attackToForward.add(selectSatisfactionIntruder());
			}
		}
		return attackToForward;
	}

	@Override
	protected void doEscaping(Set<EcoAttack> attacks) {
		// free for escape alors escape
		Map<Class<? extends EcoAttack>, Set<EcoAttack>> attackClassification = classifyAttack(attacks);

		Set<EcoAttack> moveAttackReturns = attackClassification.get(MoveAttackReturn.class);
		if (this.isWaitingMoveAttackReturn) {// I was waiting for updating my acquaintances
			if ((moveAttackReturns != null) && (moveAttackReturns.size() > 0)) {
				manageMoveAttackReturn(moveAttackReturns);
			} else {
				System.err.println("doEscaping: My Acquaintances have not been updated I can't do anything"); //$NON-NLS-1$
			}
		}

		// Steph: je retourne pas d'attack dans cette méthode que dois-je faire alors
		System.out.println("doEscaping ?"); //$NON-NLS-1$
		/*
		 * this.nearestNeighborFromBlank = null; this.nearestNeighborFromGoal = null; EcoIdentity myGoal = this.getGoal().getSlave(); EcoIdentity blankTile; try { blankTile = this.getBlank();
		 * 
		 * // check if one of my Neighbors is my goal or the blank tile for (EcoIdentity neighbor : getNeighboorsAsList()) { if (neighbor.equals(blankTile)) { this.nearestNeighborFromBlank = neighbor; }
		 * 
		 * if (neighbor.equals(myGoal)) { this.nearestNeighborFromGoal = neighbor; } }
		 * 
		 * if(this.nearestNeighborFromGoal != null) {
		 * 
		 * } else if (nearestNeighborFromBlank != null) {
		 * 
		 * } else {
		 * 
		 * }
		 * 
		 * } catch (BadAcquaintancesException e) { e.printStackTrace(); }
		 */
	}

	@Override
	protected void doSatisfactionIncreasing() {
		// free for sat

		// Steph: je retourne pas d'attack dans cette méthode que dois-je faire alors
		System.out.println("doSatisfactionIncreasing ?"); //$NON-NLS-1$
	}

	@Override
	protected void doSatisfied() {// sat finale
		System.out.println("Cool I'M SATISFIED"); //$NON-NLS-1$
	}

	/**
	 * @param attacks
	 * @return the attack clasification.
	 */
	protected static Map<Class<? extends EcoAttack>, Set<EcoAttack>> classifyAttack(Set<EcoAttack> attacks) {
		Map<Class<? extends EcoAttack>, Set<EcoAttack>> classif = new TreeMap<Class<? extends EcoAttack>, Set<EcoAttack>>();
		Set<EcoAttack> currentInstances;
		for (EcoAttack attack : attacks) {
			currentInstances = classif.get(attack.getClass());
			if (currentInstances == null) {
				currentInstances = new HashSet<EcoAttack>();
			}
			currentInstances.add(attack);
			classif.put(attack.getClass(), currentInstances);
		}
		return classif;
	}

	/**
	 * @param attacks
	 */
	private void manageMoveAttackReturn(Set<EcoAttack> attacks) {
		if (attacks.size() > 1) {
			System.err.println("I have received more than one MoveAttackReturn, I choose one of them randomly"); //$NON-NLS-1$
		}
		this.setAcquaintances(attacks.iterator().next().getConstraints());

		this.isWaitingMoveAttackReturn = false;
	}

	/**
	 * @param attacks
	 *            - a set of MoveAttack
	 * @return the list of my neighbor that haven't attack me, since I can't attack someone who already attack me
	 * @throws BadAcquaintancesException
	 */
	private List<EcoIdentity> manageIncomingMoveAttack(Set<EcoAttack> attacks) throws BadAcquaintancesException {
		List<EcoIdentity> passiveNeighbors = this.getNeighboorsAsList();
		for (EcoAttack attack : attacks) {
			passiveNeighbors.remove(attack.getAssailant());
		}
		return passiveNeighbors;
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

				if (!this.isSatisfied()) { // if I'm satisfied I do not forward the attack
					toFowards = sourcePlaceAndAttackForwarders.get(attackOrigin);
					for (EcoIdentity to : toFowards) {
						params = new Object[2];
						params[0] = currentMinMahanttanDistance;
						params[1] = e.getKey();
						manhattanAttacks.add(new ManhattanDistanceAttack(this.getEcoIdentity(), to, params));
					}
				}

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

	private final ManhattanDistanceAttack launchManhattanAttack(EcoIdentity target) {
		Object[] params = new Object[2];
		params[0] = 0;
		params[1] = target;
		return new ManhattanDistanceAttack(this.getEcoIdentity(), target, params);
	}

	/**
	 * Launch a MoveAttack, if you use this method you have to manage the waiting and reception of the MoveAttackReturn to update your acquaintances
	 * 
	 * @param target
	 *            - the identity of the tile where I want to move
	 * @return the corresponding MoveAttack to be sent
	 * @throws BadAcquaintancesException
	 */
	private final MoveAttack moveTo(EcoIdentity target) throws BadAcquaintancesException {
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

		if (target.equals(getBlank())) {
			this.isWaitingMoveAttackReturn = true;
		}

		return new MoveAttack(me, target, newTargetAcquaintances);
	}

	/**
	 * @return the identity of the blank tile
	 * @throws BadAcquaintancesException
	 */
	private final EcoIdentity getBlank() throws BadAcquaintancesException {
		Set<Blank> s = this.getAcquaintances(Blank.class);
		assert ((s == null) || (s.size() < 2));// At least and at most one Blank EcoRelation in my acquaintances
		if (s != null) {// the blank tile is always the master of the Blank relation
			return s.iterator().next().getMaster();
		}
		throw new BadAcquaintancesException();
	}

	/**
	 * @return the identity of the place hosting this tile
	 * @throws BadAcquaintancesException
	 */
	protected final EcoIdentity getPlace() throws BadAcquaintancesException {
		Set<Hosted> s = this.getAcquaintances(Hosted.class);
		assert ((s == null) || (s.size() < 2));// At least and at most one Hosted EcoRelation in my acquaintances
		if (s != null) {// the place is always the slave of the Hosted relation
			return s.iterator().next().getSlave();
		}
		throw new BadAcquaintancesException();
	}

	/**
	 * 
	 * @return the list A of neighboor of this tile, A[0] = up, A[1] = right, A[2] = down, A[3] = left
	 * @throws BadAcquaintancesException
	 */
	protected final List<EcoIdentity> getNeighboorsAsList() throws BadAcquaintancesException {
		return Arrays.asList(getNeighboors());
	}

	/**
	 * 
	 * @return the array A of neighboor of this tile, A[0] = up, A[1] = right, A[2] = down, A[3] = left
	 * @throws BadAcquaintancesException
	 */
	protected final EcoIdentity[] getNeighboors() throws BadAcquaintancesException {
		EcoIdentity[] neighboors = { null, null, null, null };

		Set<Class<? extends EcoRelation>> searchAcq = new HashSet<Class<? extends EcoRelation>>();
		searchAcq.add(Left.class);
		searchAcq.add(Right.class);
		searchAcq.add(Up.class);
		searchAcq.add(Down.class);

		Map<Class<? extends EcoRelation>, EcoRelation> m = this.getAcquaintances(searchAcq);

		{
			EcoRelation left = m.get(Left.class);
			if (left != null) {
				if (left.getMaster().equals(this.getEcoIdentity())) {// right
					neighboors[1] = left.getSlave();
				} else if (left.getSlave().equals(this.getEcoIdentity())) {// right
					neighboors[3] = left.getMaster();
				} else {
					throw new BadAcquaintancesException();
				}
			}
		}

		{
			EcoRelation right = m.get(Right.class);
			if (right != null) {
				if (right.getMaster().equals(this.getEcoIdentity())) {// right
					if (neighboors[3] == null) {
						neighboors[3] = right.getSlave();
					} else {
						if (neighboors[3] != right.getSlave()) {
							throw new BadAcquaintancesException();
						}
					}
				} else if (right.getSlave().equals(this.getEcoIdentity())) {// right
					if (neighboors[1] == null) {
						neighboors[1] = right.getMaster();
					} else {
						if (neighboors[1] != right.getMaster()) {
							throw new BadAcquaintancesException();
						}
					}
				} else {
					throw new BadAcquaintancesException();
				}
			}
		}

		{
			EcoRelation up = m.get(Up.class);
			if (up != null) {
				if (up.getMaster().equals(this.getEcoIdentity())) {// right
					neighboors[2] = up.getSlave();
				} else if (up.getSlave().equals(this.getEcoIdentity())) {// right
					neighboors[0] = up.getMaster();
				} else {
					throw new BadAcquaintancesException();
				}
			}
		}

		{
			EcoRelation down = m.get(Down.class);
			if (down != null) {
				if (down.getMaster().equals(this.getEcoIdentity())) {// right
					if (neighboors[0] == null) {
						neighboors[0] = down.getSlave();
					} else {
						if (neighboors[0] != down.getSlave()) {
							throw new BadAcquaintancesException();
						}
					}
				} else if (down.getSlave().equals(this.getEcoIdentity())) {// right
					if (neighboors[2] == null) {
						neighboors[2] = down.getMaster();
					} else {
						if (neighboors[2] != down.getMaster()) {
							throw new BadAcquaintancesException();
						}
					}
				} else {
					throw new BadAcquaintancesException();
				}
			}
		}
		return neighboors;
	}

	/**
	 * 
	 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
	 * 
	 */
	private class EcoTileChannel extends EcoAgentChannel implements NPuzzleChannel {

		/**
		 */
		public EcoTileChannel() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentType getAgentType() {
			return AgentType.TILE;
		}

	}

}
