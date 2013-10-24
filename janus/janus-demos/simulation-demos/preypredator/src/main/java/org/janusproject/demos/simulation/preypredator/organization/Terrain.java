/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2009 Stephane GALLAND
 * Copyright (C) 2010, 2012 Janus Core Developers
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

package org.janusproject.demos.simulation.preypredator.organization;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.preypredator.capacity.RefreshViewerCapacity;
import org.janusproject.demos.simulation.preypredator.gui.WorldState;
import org.janusproject.demos.simulation.preypredator.message.GameMessage;
import org.janusproject.demos.simulation.preypredator.message.GameMessage.GameMessageType;
import org.janusproject.demos.simulation.preypredator.message.MoveDirection;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agentmemory.Memory;
import org.janusproject.kernel.crio.core.HasAllRequiredCapacitiesCondition;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.ExceptionStatus;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.random.RandomNumber;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/** 
 * Terrain in a world world.
 * <p>
 * Copied from <a href="http://www.arakhne.org/tinymas/index.html">TinyMAS Platform Demos</a>
 * and adapted for Janus platform.
 * <p>
 * Thanks to Julia Nikolaeva, aka. <a href="mailto:flameia@zerobias.com">Flameia</a>, for the icons.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@RoleActivationPrototype(
		fixedParameters={Number.class}
)
public class Terrain extends Role {

	/**
	 * Label of the world state in agent memory.
	 */
	public static final String WORLD_STATE = "WORLD_STATE"; //$NON-NLS-1$
	/**
	 * Label of the world width in agent memory.
	 */
	public static final String WORLD_WIDTH = "WORLD_WIDTH"; //$NON-NLS-1$
	/**
	 * Label of the world height in agent memory.
	 */
	public static final String WORLD_HEIGHT = "WORLD_HEIGHT"; //$NON-NLS-1$
	/**
	 * Label of the prey catching flag in agent memory.
	 */
	public static final String IS_PREY_CATCHED = "IS_PREY_CATCHED"; //$NON-NLS-1$
	
	/**
	 * Label of the positions in agent memory.
	 */
	public static final String POSITIONS = "POSITIONS"; //$NON-NLS-1$

	/**
	 * Label of the prey address in agent memory.
	 */
	public static final String PREY_ID = "PREY_ID"; //$NON-NLS-1$

	private State state;
	private int awaitingAgents = 0;
	private int width;
	private int height;
	private final Map<AgentAddress,MoveDirection> awaitingMessages = new TreeMap<AgentAddress, MoveDirection>();

	/**
	 */
	public Terrain() {
		addObtainCondition(
				new HasAllRequiredCapacitiesCondition(RefreshViewerCapacity.class));		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		AgentAddress rp = getPlayer();
		if (rp!=null) rp.getName();
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		this.state = State.WAIT_AGENTS;
		this.awaitingAgents = ((Number)parameters[0]).intValue();
		Memory memory = getMemory();
		this.width = memory.getMemorizedData(WORLD_WIDTH, Integer.class);
		this.height = memory.getMemorizedData(WORLD_HEIGHT, Integer.class);
		
		return StatusFactory.ok(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Status live() {
		Memory memory = getMemory();

		Map<AgentAddress,WorldState> locations = (Map<AgentAddress,WorldState>)memory.getMemorizedData(POSITIONS);

		if (this.state==State.WAIT_AGENTS) {
			for(Message message : getMailbox()) {
				if (message instanceof GameMessage) {
					GameMessage gmsg = (GameMessage)message;
					switch(gmsg.getType()) {
					case I_AM_PREDATOR:
						addPredator(((RoleAddress)gmsg.getSender()).getPlayer());
						break;
					case I_AM_PREY:
						addPrey(((RoleAddress)gmsg.getSender()).getPlayer());
						break;
					case END_OF_GAME:
						leaveMe();
						break;
					case MOVE:
					case READY_TO_SIMULATE:
					default:
						// ignore message
					}
				}
			}
			if (locations.size()>=this.awaitingAgents) {
				Message msg = new GameMessage(GameMessageType.READY_TO_SIMULATE);
				broadcastMessage(Prey.class, msg);
				broadcastMessage(Predator.class, msg);
				this.state = State.SIMULATE;
				try {
					executeCapacityCall(RefreshViewerCapacity.class);
				}
				catch (Exception e) {
					return new ExceptionStatus(e);
				}
			}
		}
		else {
			for(Message message : getMailbox()) {
				if ((!this.awaitingMessages.containsKey(message.getSender()))&&
					(message instanceof GameMessage)&&
					(((GameMessage)message).getType()==GameMessageType.MOVE)) {
					this.awaitingMessages.put(((RoleAddress)message.getSender()).getPlayer(), ((GameMessage)message).getContent());
				}
				else {
					warning(Locale.getString(Terrain.class, "SKIP_MESSAGE", (((RoleAddress)message.getSender()).getPlayer()).toString())); //$NON-NLS-1$
				}
			}
			
			AgentAddress[][] world = (AgentAddress[][])memory.getMemorizedData(WORLD_STATE);
			
			if (this.awaitingMessages.size()==locations.size()) {
				// All agents have sent messages

				// Compute desired positions
				Map<AgentAddress, WorldState> desiredPositions = new TreeMap<AgentAddress, WorldState>();
				for (Entry<AgentAddress,MoveDirection> entry : this.awaitingMessages.entrySet()) {
					desiredPositions.put(entry.getKey(), 
							predictMove(entry.getKey(), entry.getValue(), locations));
				}
					
				// Detect conflicts
				Map<WorldState, Set<AgentAddress>> conflicts = new HashMap<WorldState, Set<AgentAddress>>();
				for (Entry<AgentAddress,WorldState> entry1 : desiredPositions.entrySet()) {
					for (Entry<AgentAddress,WorldState> entry2 : desiredPositions.entrySet()) {
						if (!entry1.getKey().equals(entry2.getKey())) {
							if (entry1.getValue().equals(entry2.getValue())) {
								// Conflit
								Set<AgentAddress> conflictedAgents = conflicts.get(entry1.getValue());
								if (conflictedAgents==null) {
									conflictedAgents = new HashSet<AgentAddress>();
									conflicts.put(entry1.getValue(), conflictedAgents);
								}
								conflictedAgents.add(entry1.getKey());
								conflictedAgents.add(entry2.getKey());
							}
						}
					}
				}
				
				// Solve conflits:
				// One agent randomly wins the position
				int idx;
				AgentAddress[] tab;
				for (Entry<WorldState,Set<AgentAddress>> entry : conflicts.entrySet()) {
					tab = new AgentAddress[entry.getValue().size()];
					entry.getValue().toArray(tab);
					idx = RandomNumber.nextInt(tab.length);
					moveAgent(tab[idx], this.awaitingMessages.get(tab[idx]), world, locations);
					for (AgentAddress agentId : tab) {
						desiredPositions.remove(agentId);
					}
				}
				
				// Move agents
				for (AgentAddress agentId : desiredPositions.keySet()) {
					moveAgent(agentId,this.awaitingMessages.get(agentId), world, locations);
				}
				
				// Set last movement desires
				for (Entry<AgentAddress,WorldState> entry : locations.entrySet()) {
					WorldState state = entry.getValue();
					MoveDirection direction = this.awaitingMessages.get(entry.getKey());
					if (direction==null) direction = MoveDirection.NONE;
					state.DIRECTION = direction;
				}
							
				// Clear buffers
				desiredPositions.clear();
				conflicts.clear();
				this.awaitingMessages.clear();
				
				//setStateProbe();
				
				//debugPositions(this.width, this.height, this.world);
				
				// Check game state
				if (isPreyCatched(world, locations)) {
					//setProbe("CATCHED", true); //$NON-NLS-1$
					print(Locale.getString(Terrain.class, "PREY_CATCHED")); //$NON-NLS-1$
					memory.putMemorizedData(IS_PREY_CATCHED, Boolean.TRUE);
					//debugPositions(this.width, this.height, this.world);
					leaveMe();
				}

				try {
					executeCapacityCall(RefreshViewerCapacity.class);
				}
				catch (Exception e) {
					return new ExceptionStatus(e);
				}
			}
		}
		
		return StatusFactory.ok(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status end() {
		broadcastMessage(Predator.class, new GameMessage(GameMessageType.END_OF_GAME));
		broadcastMessage(Prey.class, new GameMessage(GameMessageType.END_OF_GAME));
		return StatusFactory.ok(this);
	}

	/** Compute the new cell of an agent.
	 */
	private WorldState predictMove(
			AgentAddress id, 
			MoveDirection direction, 
			Map<AgentAddress,WorldState> locations) {
		WorldState pos = locations.get(id);
		assert(pos!=null);

		int x = pos.X;
		int y = pos.Y;
		int nx = x;
		int ny = y;
		
		switch(direction) {
		case UP:
			--ny;
			break;
		case DOWN:
			++ny;
			break;
		case LEFT:
			--nx;
			break;
		case RIGHT:
			++nx;
			break;
		case NONE:
			break;
		default:
			throw new IllegalStateException();
		}

		if (((nx!=x)||(ny!=y))&&
			(nx>=0)&&(nx<this.width)&&
			(ny>=0)&&(ny<this.height)) {
			x = nx;
			y = ny;
		}
		
		return new WorldState(x,y,direction);
	}
	
	/** Move an agent if possible
	 */
	private void moveAgent(AgentAddress id, 
			MoveDirection direction, 
			AgentAddress[][] world, 
			Map<AgentAddress,WorldState> locations) {
		WorldState pos = locations.get(id);
		if (pos==null) return;

		int x = pos.X;
		int y = pos.Y;
		int nx = x;
		int ny = y;
		
		switch(direction) {
		case UP:
			--ny;
			break;
		case DOWN:
			++ny;
			break;
		case LEFT:
			--nx;
			break;
		case RIGHT:
			++nx;
			break;
		case NONE:
			break;
		default:
			throw new IllegalStateException();
		}

		if (((nx!=x)||(ny!=y))&&
			(nx>=0)&&(nx<this.width)&&
			(ny>=0)&&(ny<this.height)&&
			(world[ny][nx]==null)) {
			world[ny][nx] = id;
			world[y][x] = null;
			pos.X = nx;
			pos.Y = ny;
		}
	}
	
	/**
	 * Check if the prey is dead.
	 */
	private boolean isPreyCatched(AgentAddress[][] world, Map<AgentAddress,WorldState> locations) {
		SizedIterator<AgentAddress> preys = getPlayers(Prey.class);
		if (preys.totalSize()==0) return false;
		
		AgentAddress preyAdr;
		
		while (preys.hasNext()) {
			preyAdr = preys.next();
			
			WorldState pos = locations.get(preyAdr);
			if (pos==null) return false;
			
			int xPrey = pos.X;
			int yPrey = pos.Y;
			
			assert(xPrey>=0 && xPrey<this.width);
			assert(yPrey>=0 && yPrey<this.height);
			
			// Check in four directions
			int catched = 0;
			
			// Up
			if ((yPrey<=0)||(world[yPrey-1][xPrey]!=null))
				++catched;
			// Down
			if ((yPrey>=(this.height-1))||(world[yPrey+1][xPrey]!=null))
				++catched;
			// Left
			if ((xPrey<=0)||(world[yPrey][xPrey-1]!=null))
				++catched;
			// Right
			if ((xPrey>=(this.width-1))||(world[yPrey][xPrey+1]!=null))
				++catched;
			
			if (catched!=4) return false;
		}
		
		return true;
	}
	
	/**
	 * Debug agent positions.
	 * 
	 * @param width is the width of the terrain
	 * @param height is the height of the terrain
	 * @param grid is the cell contents.
	 */
	protected static void debugPositions(int width, int height, AgentAddress[][] grid) {
		int[] size = new int[width];
		Arrays.fill(size, 0);
		
		for (int c=0; c<width; ++c) {
			for (int r=0; r<height; ++r) {
				if ((grid[r][c]!=null)&&(size[c]<grid[r][c].getName().length()))
					size[c] = grid[r][c].getName().length();
			}
		}
		
		for(int r=0; r<height; ++r) {
			System.out.print("| "); //$NON-NLS-1$
			for(int c=0; c<width; ++c) {
				int s = grid[r][c]==null ? 0 : grid[r][c].getName().length();
				if (grid[r][c]!=null) System.out.print(grid[r][c].getName());
				for(int k=s; k<size[c]; ++k)
					System.out.print(" "); //$NON-NLS-1$
				System.out.print(" |"); //$NON-NLS-1$
			}
			System.out.println(""); //$NON-NLS-1$
		}
	}

	@SuppressWarnings("unchecked")
	private void addPrey(AgentAddress prey) {
		Memory memory = getMemory();
		assert(memory!=null);
		if (memory.getMemorizedData(PREY_ID, AgentAddress.class)==null) {
			int x, y;
			AgentAddress[][] world = (AgentAddress[][])memory.getMemorizedData(WORLD_STATE);
			Map<AgentAddress,WorldState> locations = (Map<AgentAddress,WorldState>)memory.getMemorizedData(POSITIONS);
			do {
				x = RandomNumber.nextInt(this.width);
				y = RandomNumber.nextInt(this.height);
			}
			while (world[y][x]!=null);
			
			world[y][x] = prey;
			locations.put(prey, new WorldState(x,y,null));
			memory.putMemorizedData(PREY_ID, prey);
		}
		else {
			warning(Locale.getString(Terrain.class, "PREY_EXISTS", prey.toString())); //$NON-NLS-1$
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addPredator(AgentAddress predator) {
		Memory memory = getMemory();
		assert(memory!=null);
		int x, y;
		AgentAddress[][] world = (AgentAddress[][])memory.getMemorizedData(WORLD_STATE);
		Map<AgentAddress,WorldState> locations = (Map<AgentAddress,WorldState>)memory.getMemorizedData(POSITIONS);
		do {
			x = RandomNumber.nextInt(this.width);
			y = RandomNumber.nextInt(this.height);
		}
		while (world[y][x]!=null);
		
		world[y][x] = predator;
		locations.put(predator, new WorldState(x,y,null));
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private enum State {
		
		WAIT_AGENTS,
		
		SIMULATE;

	}
	
}