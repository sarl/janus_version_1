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
package org.janusproject.demos.jaak.ants.environment;

import org.arakhne.afc.math.MathConstants;
import org.janusproject.demos.jaak.ants.organization.Ant;
import org.janusproject.demos.jaak.ants.organization.Forager;
import org.janusproject.demos.jaak.ants.organization.Patroller;
import org.janusproject.jaak.envinterface.body.TurtleBody;
import org.janusproject.jaak.spawner.JaakPointSpawner;
import org.janusproject.jaak.turtle.Turtle;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.time.KernelTimeManager;
import org.janusproject.kernel.util.random.RandomNumber;

/** Spawner for the Ant Colony Demo.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AntColonySpawner extends JaakPointSpawner {

	private int patrollerBudget;
	private int foragerBudget;

	private boolean isPatrollerSpawned;

	/**
	 * @param patrollerBudget is the maximal count of patroller ants to spawn.
	 * @param foragerBudget is the maximal count of forager ants to spawn.
	 * @param x is the spawning position.
	 * @param y is the spawning position.
	 */
	public AntColonySpawner(int patrollerBudget, int foragerBudget, int x, int y) {
		super(x,y);
		this.patrollerBudget = patrollerBudget;
		this.foragerBudget = foragerBudget;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSpawnable(KernelTimeManager timeManager) {
		return (this.patrollerBudget>0 || this.foragerBudget>0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Turtle createTurtle(KernelTimeManager timeManager) {
		assert(this.patrollerBudget>0 || this.foragerBudget>0);
		return new Ant();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void turtleSpawned(Turtle turtle, TurtleBody body, KernelTimeManager timeManager) {
		if (this.isPatrollerSpawned) {
			--this.patrollerBudget;
		}
		else {
			--this.foragerBudget;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void turtleSpawned(AgentAddress turtle, TurtleBody body, KernelTimeManager timeManager) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object[] getTurtleActivationParameters(Turtle turtle, KernelTimeManager timeManager) {
		assert(this.patrollerBudget>0 || this.foragerBudget>0);
		if (this.patrollerBudget>0 && (this.foragerBudget==0 || RandomNumber.nextBoolean())) {
			this.isPatrollerSpawned = true;
			return new Object[] { Patroller.class };
		}
		if (this.foragerBudget>0) {
			this.isPatrollerSpawned = false;
			return new Object[] { Forager.class };
		}
		return new Object[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected float computeSpawnedTurtleOrientation(KernelTimeManager timeManager) {
		return RandomNumber.nextFloat() * MathConstants.TWO_PI;
	}

}
