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
package org.janusproject.jaak.environment.solver;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.jaak.bresenham.Bresenham;
import org.janusproject.jaak.envinterface.body.TurtleBody;
import org.janusproject.jaak.envinterface.influence.Influence;
import org.janusproject.jaak.envinterface.influence.MotionInfluence;
import org.janusproject.jaak.envinterface.influence.MotionInfluenceStatus;
import org.janusproject.jaak.envinterface.perception.JaakObject;
import org.janusproject.jaak.environment.GridModel;
import org.janusproject.jaak.environment.ValidationResult;
import org.janusproject.jaak.environment.model.AbstractJaakEnvironmentInfluenceSolver;
import org.janusproject.jaak.environment.model.RealTurtleBody;


/** This class defines a default implementation for influence solver.
 * This implementation:<ul>
 * <li>avoid motion influences to collide on target position, paths are not treated;</li>
 * <li>does not validate the other influences.</li>
 * </ul>
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PathBasedInfluenceSolver extends AbstractJaakEnvironmentInfluenceSolver {
	
	private static final Comparator<Point2i> POINT_COMPARATOR = new Comparator<Point2i>() {
		@Override
		public int compare(Point2i o1, Point2i o2) {
			int cmp = o1.x() - o2.x();
			if (cmp!=0) return cmp;
			return o1.y() - o2.y();
		}
	};
	
	private void detectMotionConflictsAndApplyNonMotionInfluence(
			Collection<Path> paths,
			Map<Point2i, List<PathElement>> conflictingCells,
			Influence influence,
			ActionApplier actionApplier) {
		Point2i position;

		if (influence instanceof MotionInfluence) {
			MotionInfluence mi = (MotionInfluence)influence;
			
			JaakObject movedObject = mi.getMovedObject();
			assert(movedObject!=null);

			position = movedObject.getPosition();
			assert(position!=null);
	
			// Compute target position
			Point2i newPosition = new Point2i(
					Math.round(position.getX() + mi.getLinearMotionX()),
					Math.round(position.getY() + mi.getLinearMotionY()));

			Iterator<Point2i> iterator = Bresenham.line(
								position.x(), position.y(),
								newPosition.x(), newPosition.y());
			Point2i p;
			PathElement pathElement;
			List<PathElement> conflictingElements;
			PathElement previousElement = null;
			Path path = new Path(mi);
			
			paths.add(path);
			
			while (iterator.hasNext()) {
				p = iterator.next();
				
				if (validatePosition(p)==ValidationResult.WRAPPED) {
					// Wrapped, recompute path from the new position
					ValidationResult r = validatePosition(newPosition);
					assert(r==ValidationResult.WRAPPED);
					iterator = Bresenham.line(
							p.x(), p.y(),
							newPosition.x(), newPosition.y());
					// Consume the first point
					assert(iterator!=null && iterator.hasNext());
					p = iterator.next();
				}
				
				pathElement = new PathElement(p, path);
				
				if (previousElement!=null) {
					previousElement.next = pathElement;
				}
				
				conflictingElements = conflictingCells.get(p);
				if (conflictingElements==null) {
					// no conflict, right now
					conflictingElements = new LinkedList<PathElement>();
					conflictingCells.put(p, conflictingElements);
				}
				else {
					if (conflictingElements.size()==1)
						conflictingElements.get(0).inConflict = true;
					pathElement.inConflict = true;
				}
				conflictingElements.add(pathElement);
				
				previousElement = pathElement;
			}
								
		}
		else {
			applyInfluence(actionApplier, influence, null);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void solve(
			Collection<? extends Influence> endogenousInfluences,
			Collection<RealTurtleBody> bodies,
			ActionApplier actionApplier) {
		
		GridModel grid = getGridModel();
		assert(grid!=null);
		
		Collection<Path> paths = new LinkedList<Path>();
		Map<Point2i, List<PathElement>> conflictingCells = new TreeMap<Point2i, List<PathElement>>(POINT_COMPARATOR);
				
		// Appling no-motion influences and localizing the motion influence targets
		// from the endogenous engine
		if (endogenousInfluences!=null) {
			for(Influence influence : endogenousInfluences) {
				detectMotionConflictsAndApplyNonMotionInfluence(paths, conflictingCells, influence, actionApplier);
			}
		}
	
		// Appling no-motion influences and localizing the motion influence targets
		// from the bodies
		if (bodies!=null) {
			MotionInfluence mi;
			List<? extends Influence> influences;
			
			for(RealTurtleBody body : bodies) {
				mi = body.consumeMotionInfluence();
				if (mi==null) {
					mi = new MotionInfluence(body);
				}
				detectMotionConflictsAndApplyNonMotionInfluence(paths, conflictingCells, mi, actionApplier);
				influences = body.consumeOtherInfluences();
				if (influences!=null) {
					for(Influence influence : influences) {
						detectMotionConflictsAndApplyNonMotionInfluence(paths, conflictingCells, influence, actionApplier);
					}
				}
			}
		}
		
		// Fixing motion influences and apply the fixed motion influences
		PathElement pathElement;
		MotionInfluence motionInfluence;
		MotionInfluenceStatus motionInfluenceStatus;
		TurtleBody body;
		Point2i bodyPosition;
		Vector2f motion = new Vector2f();
		for(Path path : paths) {
			motionInfluence = path.influence;
			// search for the last path element
			pathElement = path.getLastTraversableElementInPath(conflictingCells);
			if (pathElement!=null) {
				body = motionInfluence.getEmitter();
				assert(body!=null);
				bodyPosition = body.getPosition();
				motion.set(motionInfluence.getLinearMotion());
				motionInfluence.setLinearMotion(
						pathElement.position.getX()-bodyPosition.getX(),
						pathElement.position.getY()-bodyPosition.getY());
				if (motion.lengthSquared()<=motionInfluence.getLinearMotion().lengthSquared()) {
					motionInfluenceStatus = MotionInfluenceStatus.COMPLETE_MOTION;
				}
				else {
					motionInfluenceStatus = MotionInfluenceStatus.PARTIAL_MOTION;
				}
			}
			else {
				// Apply the rotation even if linear motion was discarted
				motionInfluence.setLinearMotion(0, 0);
				motionInfluenceStatus = MotionInfluenceStatus.NO_MOTION;
			}
			applyInfluence(actionApplier, motionInfluence, motionInfluenceStatus);
		}
	}
	
	/** 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Path {

		/** Influence. */
		final MotionInfluence influence;
		/** First element in path. */
		PathElement firstElement;
		
		/**
		 * @param mi
		 */
		public Path(MotionInfluence mi) {
			this.influence = mi;
		}
		
		private static void unmarkConflicts(Map<Point2i, List<PathElement>> conflictingCells, PathElement element) {
			PathElement current = element;
			List<PathElement> list;
			while (current!=null) {
				if (current.inConflict) {
					list = conflictingCells.get(current.position);
					if (list!=null) {
						if (list.size()<=2) {
							for(PathElement e : list) {
								e.inConflict = false;
							}
						}
						else {
							current.inConflict = false;
							list.remove(current);
						}
					}
				}
				current = current.next;
			}
		}
		
		public PathElement getLastTraversableElementInPath(Map<Point2i, List<PathElement>> conflictingCells) {
			assert(this.firstElement!=null);
			PathElement lastTraversable = null;
			PathElement current = this.firstElement;
			while (current!=null && !current.inConflict) {
				lastTraversable = current;
				current = current.next;
			}
			
			// Unmark conflicts on the following elements
			if (lastTraversable!=null && lastTraversable.next!=null)
				unmarkConflicts(conflictingCells, lastTraversable.next);
			
			if (lastTraversable==null) {
				// conflict on the first cell of the path
				// Search for a new candidate later on the path
				// which will permit to move a little beat.
				current = this.firstElement.next;
				lastTraversable = this.firstElement;
				while (current!=null && !current.inConflict) {
					lastTraversable = current;
					current = current.next;
				}
				if (lastTraversable==this.firstElement) {
					lastTraversable = null;
				}
			}
			
			return lastTraversable;
		}
	}
	
	/** 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class PathElement {

		/** Path. */
		final Point2i position;
		/** Next element in path. */
		PathElement next;
		/** Indicates if this element is not traversable. */
		boolean inConflict = false;
		
		/**
		 * @param position
		 * @param path
		 */
		public PathElement(Point2i position, Path path) {
			this.position = position;
			this.next = null;
			if (path.firstElement==null)
				path.firstElement = this;
		}
		
	}

}