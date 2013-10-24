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
package org.janusproject.jaak.spawner;

import java.lang.ref.WeakReference;

import org.arakhne.afc.math.MathConstants;
import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.arakhne.afc.math.discrete.object2d.Rectangle2i;
import org.arakhne.afc.math.discrete.object2d.Shape2i;
import org.janusproject.jaak.envinterface.EnvironmentArea;
import org.janusproject.jaak.envinterface.body.TurtleBody;
import org.janusproject.jaak.turtle.Turtle;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.time.KernelTimeManager;
import org.janusproject.kernel.util.random.RandomNumber;

/** A spawner which is spawning on the overall world surface.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JaakWorldSpawner extends JaakSpawner {
	
	private final WeakReference<EnvironmentArea> environment;
	
	/**
	 * @param environment is the environment in which the spawning may proceed.
	 */
	public JaakWorldSpawner(EnvironmentArea environment) {
		this.environment = new WeakReference<EnvironmentArea>(environment);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Point2i computeCurrentSpawningPosition(Point2i desiredPosition) {
		EnvironmentArea area = this.environment.get();
		assert(area!=null);
		if (desiredPosition!=null
		 && desiredPosition.x()>=area.getX()
		 && desiredPosition.y()>=area.getY()
		 && desiredPosition.x()<=area.getX()+area.getWidth()
		 && desiredPosition.y()<=area.getY()+area.getHeight()) {
			return new Point2i(desiredPosition);
		}
		int dx = RandomNumber.nextInt(area.getWidth());
		int dy = RandomNumber.nextInt(area.getHeight());
		return new Point2i(area.getX() + dx, area.getY() + dy);
	}
		
	/** {@inheritDoc}
	 */
	@Override
	public Point2i getReferenceSpawningPosition() {
		EnvironmentArea area = this.environment.get();
		assert(area!=null);
		return new Point2i(area.getX(), area.getY());
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Shape2i toShape() {
		EnvironmentArea area = this.environment.get();
		assert(area!=null);
		return new Rectangle2i(area.getX(), area.getY(), area.getWidth(), area.getHeight());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected float computeSpawnedTurtleOrientation(KernelTimeManager timeManager) {
		return RandomNumber.nextFloat() * MathConstants.TWO_PI;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Turtle createTurtle(KernelTimeManager timeManager) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSpawnable(KernelTimeManager timeManager) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void turtleSpawned(Turtle turtle, TurtleBody body, KernelTimeManager timeManager) {
		//
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
		return new Object[0];
	}

}