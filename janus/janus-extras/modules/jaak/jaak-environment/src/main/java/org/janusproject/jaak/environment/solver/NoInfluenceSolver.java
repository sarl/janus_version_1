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
import java.util.List;

import org.janusproject.jaak.envinterface.influence.Influence;
import org.janusproject.jaak.envinterface.influence.MotionInfluence;
import org.janusproject.jaak.envinterface.influence.MotionInfluenceStatus;
import org.janusproject.jaak.environment.model.AbstractJaakEnvironmentInfluenceSolver;
import org.janusproject.jaak.environment.model.RealTurtleBody;


/** This class defines an implementation for influence solver which
 * does nothing.
 * This implementation:<ul>
 * <li>generate a motion action corresponding to all the influences;</li>
 * <li>does not validate the other influences.</li>
 * </ul>
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class NoInfluenceSolver extends AbstractJaakEnvironmentInfluenceSolver {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void solve(
			Collection<? extends Influence> endogenousInfluences,
			Collection<RealTurtleBody> bodies,
			ActionApplier actionApplier) {
		if (actionApplier!=null) {
			if (endogenousInfluences!=null) {
				for(Influence influence : endogenousInfluences) {
					applyInfluence(actionApplier, influence, null);
				}
			}
			if (bodies!=null) {
				List<Influence> influences;
				for(RealTurtleBody body : bodies) {
					influences = body.consumeOtherInfluences();
					if (influences!=null) {
						for(Influence influence : influences) {
							applyInfluence(actionApplier, influence, null);
						}
					}
					MotionInfluence influence = body.consumeMotionInfluence();
					if (influence!=null) {
						applyInfluence(actionApplier, influence, MotionInfluenceStatus.COMPLETE_MOTION);
					}
				}
			}
		}
	}
	
}