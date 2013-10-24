/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-12 Janus Core Developers
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
package org.janusproject.demos.jaak.pacman;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.arakhne.afc.math.discrete.object2d.Tuple2iComparator;
import org.janusproject.demos.jaak.pacman.channel.Player;
import org.janusproject.demos.jaak.pacman.semantic.PillSemantic;
import org.janusproject.demos.jaak.pacman.semantic.WallSemantic;
import org.janusproject.demos.jaak.pacman.turtle.Ghost;
import org.janusproject.demos.jaak.pacman.turtle.PacMan;
import org.janusproject.demos.jaak.pacman.ui.PacManPanel;
import org.janusproject.jaak.envinterface.channel.GridStateChannel;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;
import org.janusproject.jaak.envinterface.perception.Obstacle;
import org.janusproject.jaak.environment.model.JaakEnvironment;
import org.janusproject.jaak.environment.solver.ActionApplier;
import org.janusproject.jaak.kernel.JaakKernel;
import org.janusproject.jaak.kernel.JaakKernelController;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.util.random.RandomNumber;

/** Launcher for the PacMan Demo.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PacManGame {

	/** Duration of the super pacman.
	 */
	public static final int SUPER_PACMAN_DURATION = 10;
	
	/** Length of the vision of the ghosts.
	 */
	public static final int GHOST_VISION_LENGTH = 10;

	private static final long waitingDuration = 300;
	private static final int WIDTH = 30;
	private static final int HEIGHT = 30;
	private static final int PILL_COUNT = 15;
	private static final int GHOST_COUNT = 10;
	
	/** Initialize the kernel.
	 * 
	 * @param environment
	 * @return the controller
	 */
	public static JaakKernelController initializeKernel(JaakEnvironment environment) {
		JaakKernelController controller = JaakKernel.initializeKernel(environment);
		controller.getTimeManager().setWaitingDuration(waitingDuration);
		return controller;
	}
	
	/**
	 * Create an instance of the environment.
	 * 
	 * @return an instance of the environment.
	 */
	public static JaakEnvironment createEnvironment() {
		JaakEnvironment environment = new JaakEnvironment(WIDTH, HEIGHT);
		environment.setWrapped(false);
		ActionApplier actionApplier = environment.getActionApplier();

		Set<Point2i> positions = new TreeSet<Point2i>(new Tuple2iComparator());
		
		generateWalls(actionApplier, positions, 0, WIDTH-1, 0, HEIGHT-1);
		
		generatePills(actionApplier, positions, 0, WIDTH-1, 0, HEIGHT-1);
		
		return environment;
	}
	
	private static void generatePills(ActionApplier actionApplier, Set<Point2i> positions, int minX, int maxX, int minY, int maxY) {
		int x,y;
		Point2i p;
		for(int i=0; i<PILL_COUNT; ++i) {
			x = RandomNumber.nextInt(maxX-minX)+minX;
			y = RandomNumber.nextInt(maxY-minY)+minY;
			p = new Point2i(x,y);
			while (positions.contains(p)) {
				x = RandomNumber.nextInt(maxX-minX)+minX;
				y = RandomNumber.nextInt(maxY-minY)+minY;
				p = new Point2i(x,y);
			}
			actionApplier.putObject(p.x(), p.y(), new EnvironmentalObject(PillSemantic.SEMANTIC));
			positions.remove(p);
		}
	}

	/**
	 * The algorithm is inspired by the <a href="http://en.wikipedia.org/wiki/Maze_generation_algorithm">Prim</a>'s
	 * algorithm.
	 * 
	 * @param actionApplier
	 * @param positions
	 * @param minX
	 * @param maxX
	 * @param minY
	 * @param maxY
	 */
	private static void generateWalls(ActionApplier actionApplier, Set<Point2i> positions, int minX, int maxX, int minY, int maxY) {
		for(int i=minX; i<=maxX; ++i) {
			for(int j=minY; j<=maxY; ++j) {
				actionApplier.putObject(i, j, new Obstacle(WallSemantic.SEMANTIC));
				positions.add(new Point2i(i,j));
			}
		}
		
		int x, y;
		Point2i p;
		Set<Point2i> breakableWalls = new TreeSet<Point2i>(new Tuple2iComparator());
		
		x = RandomNumber.nextInt(maxX-minX+1)+minX;
		y = RandomNumber.nextInt(maxY-minY+1)+minY;
		p = new Point2i(x,y);
		actionApplier.removeObjects(x, y);
		positions.remove(p);
		
		addBreakableWall(positions, breakableWalls, minX, minY, maxX, maxY, x-1, y);
		addBreakableWall(positions, breakableWalls, minX, minY, maxX, maxY, x+1, y);
		addBreakableWall(positions, breakableWalls, minX, minY, maxX, maxY, x,   y-1);
		addBreakableWall(positions, breakableWalls, minX, minY, maxX, maxY, x,   y+1);
		
		while (!breakableWalls.isEmpty()) {
			p = removeRandom(breakableWalls);
			assert(p!=null);
			
			if (isBreakableWall(positions, minX, minY, maxX, maxY, p.x(), p.y())) {
				actionApplier.removeObjects(p.x(), p.y());
				positions.remove(p);

				addBreakableWall(positions, breakableWalls, minX, minY, maxX, maxY, p.x()-1, p.y());
				addBreakableWall(positions, breakableWalls, minX, minY, maxX, maxY, p.x()+1, p.y());
				addBreakableWall(positions, breakableWalls, minX, minY, maxX, maxY, p.x(),   p.y()-1);
				addBreakableWall(positions, breakableWalls, minX, minY, maxX, maxY, p.x(),   p.y()+1);
			}
		}
	}
	
	private static boolean isBreakableWall(
		Set<Point2i> positions,
		int minX, int minY, int maxX, int maxY,
		int x, int y) {
		Point2i wallPosition = new Point2i(x,y);
		if (positions.contains(wallPosition)) { // A wall exist
			boolean freeU  = (y>minY && !positions.contains(new Point2i(x,   y-1)));
			boolean freeD  = (x<maxX && !positions.contains(new Point2i(x,   y+1)));
			boolean freeL  = (x>minX && !positions.contains(new Point2i(x-1, y)));
			boolean freeR  = (x<maxX && !positions.contains(new Point2i(x+1, y)));
			
			boolean freeUL = (x>minX && y>minY && !positions.contains(new Point2i(x-1, y-1)));
			boolean freeUR = (x<maxX && y>minY && !positions.contains(new Point2i(x+1, y-1)));
			boolean freeDL = (x>minX && y<maxY && !positions.contains(new Point2i(x-1, y+1)));
			boolean freeDR = (x<maxX && y<maxY && !positions.contains(new Point2i(x+1, y+1)));
			
			// Invalid cases: O is a wall, ? is the breakable wall, X is a free cell, . is a cell
			// XX. | .XX | ... | ... | .X. | ...
			// X?. | .?X | .?X | X?. | .?. | X?X
			// ... | ... | .XX | XX. | .X. | ...
			//
			// All the other are assumed to be valid: the wall is breakable
			
			if ((freeL && freeUL && freeU)
			||  (freeU && freeUR && freeR)
			||  (freeR && freeDR && freeD)
			||  (freeD && freeDL && freeL)
			||  (freeU && freeD)
			||  (freeL && freeR)) {
				return false;
			}
			
			return true;
		}
		return false;
	}
	
	private static void addBreakableWall(
			Set<Point2i> positions,
			Set<Point2i> walls,
			int minX, int minY, int maxX, int maxY,
			int x, int y) {
		if (x>=minX && x<=maxX && y>=minY && y<=maxY) {
			Point2i wallPosition = new Point2i(x,y);
			if (positions.contains(wallPosition))
				walls.add(wallPosition);
		}
	}
	
	private static Point2i removeRandom(Set<Point2i> theSet) {
		Iterator<Point2i> it = theSet.iterator();
		int r = RandomNumber.nextInt(theSet.size());
		Point2i p = null;
		for(int i=0; i<=r && it.hasNext(); ++i)
			p = it.next();
		if (p!=null) it.remove();
		return p;
	}
	
	/**
	 * Create the ghosts and the interactive pacman. 
	 *
	 * @return an instance of the pacman.
	 */
	public static Player createEntitiesWithInteractivePlayer() {
		Kernel k = Kernels.get();
		
		PacMan pacman = new PacMan();
		k.submitLightAgent(pacman);
		
		for(int i=0; i<GHOST_COUNT; ++i) {
			Ghost ghost = new Ghost(i);
			k.submitLightAgent(ghost);
		}
		
		k.launchDifferedExecutionAgents();
		
		return pacman.getChannel(Player.class);
	}

	/** Create a panel which is able to display the world state.
	 * 
	 * @param jaakKernel is the address agent which is managing the Jaak environment.
	 * @param player is the representation of the player.
	 * @return the created panel.
	 * @throws IllegalStateException if the given jaakKernel does not provide a
	 * {@link GridStateChannel}.
	 */
	public static PacManPanel createPanel(AgentAddress jaakKernel, Player player) {
		GridStateChannel channel = Kernels.get().getChannelManager().getChannel(jaakKernel, GridStateChannel.class);
		if (channel==null) throw new IllegalStateException();
		return new PacManPanel(channel, player);
	}
		
}