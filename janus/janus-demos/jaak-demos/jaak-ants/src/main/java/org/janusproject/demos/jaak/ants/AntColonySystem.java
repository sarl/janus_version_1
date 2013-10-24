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
package org.janusproject.demos.jaak.ants;

import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.jaak.ants.environment.AntColony;
import org.janusproject.demos.jaak.ants.environment.AntColonySpawner;
import org.janusproject.demos.jaak.ants.environment.Food;
import org.janusproject.demos.jaak.ants.ui.AntPanel;
import org.janusproject.jaak.envinterface.channel.GridStateChannel;
import org.janusproject.jaak.environment.model.JaakEnvironment;
import org.janusproject.jaak.environment.solver.ActionApplier;
import org.janusproject.jaak.spawner.JaakSpawner;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Kernels;

/** Launcher for the Ant Colony Demo.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AntColonySystem {

	private static final boolean isWrappedEnvironment = true;
	private static final int ANT_COLONY_COUNT = 4;
	private static final int ANT_COLONY_PATROLLER_POPULATION = 10;	
	private static final int ANT_COLONY_FORAGER_POPULATION = 50;	
	private static final int FOOD_SOURCE = 30;
	private static final int WIDTH = 200;
	private static final int HEIGHT = 150;
	
	/** Max amount of food per food source.
	 */
	public static final int MAX_FOOD_PER_SOURCE = 1000;

	/** Max pheromone amount.
	 */
	public static final float MAX_PHEROMONE_AMOUNT = 10f;

	private static final Set<Point2i> positions = new TreeSet<Point2i>(new Comparator<Point2i>(){
		@Override
		public int compare(Point2i o1, Point2i o2) {
			if (o1==o2) return 0;
			if (o1==null) return Integer.MIN_VALUE;
			if (o2==null) return Integer.MAX_VALUE;
			int cmp = o1.x() - o2.x();
			if (cmp!=0) return cmp;
			return o1.y() - o2.y();
		}
	});
	
	/**
	 * Create an ant colony and the associated spawner.
	 * 
	 * @param colonyId is the identifier of the colony to create.
	 * @param environment is the environment on which the spawner is created.
	 * @return an instance of the spawner.
	 */
	public static JaakSpawner createColony(int colonyId, JaakEnvironment environment) {
		ActionApplier actionApplier = environment.getActionApplier();
		Point2i position = new Point2i();
		Random rnd = new Random();
		position.set(
				rnd.nextInt(environment.getWidth()),
				rnd.nextInt(environment.getHeight()));
		while (positions.contains(position)) {
			position.set(
					rnd.nextInt(environment.getWidth()),
					rnd.nextInt(environment.getHeight()));
		}
		positions.add(position);
		AntColony antColonyObject = new AntColony(colonyId);
		actionApplier.putObject(position.x(), position.y(), antColonyObject);
		return new AntColonySpawner(ANT_COLONY_PATROLLER_POPULATION, ANT_COLONY_FORAGER_POPULATION, position.x(), position.y());
	}

	/**
	 * Create the ant colonies and the associated spawners.
	 * 
	 * @param environment is the environment on which the spawner is created.
	 * @return an instance of the spawner.
	 */
	public static JaakSpawner[] createColonies(JaakEnvironment environment) {
		StringBuilder buffer = new StringBuilder();
		JaakSpawner[] spawners = new JaakSpawner[ANT_COLONY_COUNT];
		for(int i=0; i<spawners.length; i++) {
			spawners[i] = createColony(i+1, environment);
			buffer.setLength(0);
			buffer.append(Locale.getString(AntColonySystem.class,
					"SPAWNER_INITIALIZATION", //$NON-NLS-1$
					Integer.toString(i+1),
					Integer.toString(spawners.length),
					Integer.toString(spawners[i].getReferenceSpawningPosition().x()),
					Integer.toString(spawners[i].getReferenceSpawningPosition().y())));
			System.out.println(buffer.toString());
		}
		return spawners;
	}

	/**
	 * Create an instance of the environment.
	 * 
	 * @return an instance of the environment.
	 */
	public static JaakEnvironment createEnvironment() {
		JaakEnvironment environment = new JaakEnvironment(WIDTH, HEIGHT);
		environment.setWrapped(isWrappedEnvironment);
		Random rnd = new Random();
		ActionApplier actionApplier = environment.getActionApplier();
		Food food;
		StringBuilder buffer = new StringBuilder();
		
		for(int i=0; i<FOOD_SOURCE; i++) {
			Point2i p = new Point2i(
					rnd.nextInt(environment.getWidth()),
					rnd.nextInt(environment.getHeight()));
			while (positions.contains(p)) {
				p.set(
						rnd.nextInt(environment.getWidth()),
						rnd.nextInt(environment.getHeight()));
			}
			positions.add(p);
			food = new Food(Math.max(10,rnd.nextInt(MAX_FOOD_PER_SOURCE)));
			actionApplier.putObject(p.x(), p.y(), food);
			
			buffer.setLength(0);
			buffer.append(Locale.getString(AntColonySystem.class,
					"FOOD_INITIALIZATION", //$NON-NLS-1$
					Integer.toString(i+1),
					Integer.toString(FOOD_SOURCE),
					Integer.toString(p.x()),
					Integer.toString(p.y()),
					food.getAmount()));
			System.out.println(buffer.toString());
		}
		
		return environment;
	}
	
	/** Create a panel which is able to display the world state.
	 * 
	 * @param jaakKernel is the address agent which is managing the Jaak environment.
	 * @return the created panel.
	 * @throws IllegalStateException if the given jaakKernel does not provide a
	 * {@link GridStateChannel}.
	 */
	public static AntPanel createPanel(AgentAddress jaakKernel) {
		GridStateChannel channel = Kernels.get().getChannelManager().getChannel(jaakKernel, GridStateChannel.class);
		if (channel==null) throw new IllegalStateException();
		return new AntPanel(channel);
	}
		
}