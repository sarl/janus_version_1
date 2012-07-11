/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010 Janus Core Developers
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
package org.janusproject.demos.simulation.foragerbots.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.janusproject.demos.simulation.foragerbots.message.MoveMessage;
import org.janusproject.demos.simulation.foragerbots.message.PerceptionMessage;
import org.janusproject.demos.simulation.foragerbots.message.RegistrationAckMessage;
import org.janusproject.demos.simulation.foragerbots.message.RegistrationMessage;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.util.random.RandomNumber;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(
		fixedParameters={}
)
public class Grid extends Agent {

	private static final long serialVersionUID = -9127529082817489267L;

	/** Max amount of resource per cell.
	 */
	public static final int MAX_RESOURCE_AMOUNT = 10;
	
	private final int width;
	private final int height;
	private final Cell[][] grid;
	private final Map<AgentAddress,Cell> agents = new TreeMap<AgentAddress, Grid.Cell>();
	private final int[] bases;

	private final Collection<GridListener> listeners = new LinkedList<GridListener>();


	/**
	 * @param sizeX is the width of the world.
	 * @param sizeY is the height of the world.
	 * @param baseCount is the coun tof bases on the grid.
	 */
	public Grid(int sizeX, int sizeY, int baseCount) {
		this.width = sizeX;
		this.height = sizeY;
		this.grid = new Cell[sizeX][sizeY];
		for(int x=0; x<sizeX; ++x) {
			for(int y=0; y<sizeY; ++y) {
				this.grid[x][y] = new Cell(x,y,RandomNumber.nextBoolean()? RandomNumber.nextInt(MAX_RESOURCE_AMOUNT) : 0);
			}
		}
		this.bases = new int[baseCount*2];
		for(int x=0; x<this.bases.length-1; x+=2) {
			this.bases[x] = RandomNumber.nextInt(this.width);
			this.bases[x+1] = RandomNumber.nextInt(this.height);
			this.grid[this.bases[x]][this.bases[x+1]].setResourceAmount(0);
		}
	}
	
	/** Replies the width of the world.
	 * 
	 * @return the width of the world.
	 */
	public int getWorldWidth() {
		return this.width;
	}
	
	/** Replies the height of the world.
	 * 
	 * @return the height of the world.
	 */
	public int getWorldHeight() {
		return this.height;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		Status s = super.activate(parameters);
		fireGridEvent();
		return s;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		Status s = super.live();
		Message m;
		Mailbox mailbox = getMailbox();
		m = mailbox.removeFirst();
		while (m!=null) {
			if (m instanceof RegistrationMessage) {
				RegistrationMessage reg = (RegistrationMessage)m;
				AgentAddress bot = (AgentAddress)reg.getSender();
				if (!this.agents.containsKey(bot)) {
					int baseIndex = RandomNumber.nextInt(this.bases.length/2);
					int baseX = this.bases[baseIndex*2];
					int baseY = this.bases[baseIndex*2+1];
					
					int x = RandomNumber.nextInt(this.width);
					int y = RandomNumber.nextInt(this.height);
					Cell cell = this.grid[x][y];
					cell.addBot();
					this.agents.put(bot, cell);
					
					RegistrationAckMessage ack = new RegistrationAckMessage(baseX, baseY);
					sendMessage(ack, bot);
					PerceptionMessage perception = new PerceptionMessage(cell);
					sendMessage(perception, bot);
				}
			}
			else if (m instanceof MoveMessage) {
				MoveMessage move = (MoveMessage)m;
				AgentAddress bot = (AgentAddress)move.getSender();
				Cell currentCell = this.agents.get(bot);
				PerceptionMessage ack = null;
				if (currentCell!=null) {
					Direction d = move.getMoveDirection();
					switch(d) {
					case NORTH:
						if (currentCell.y>0) {
							Cell newCell = this.grid[currentCell.x][currentCell.y-1];
							this.agents.put(bot, newCell);
							currentCell.removeBot();
							newCell.addBot();
							ack = new PerceptionMessage(newCell);
						}
						else {
							ack = new PerceptionMessage(currentCell);
						}
						break;
					case SOUTH:
						if (currentCell.y<this.height-1) {
							Cell newCell = this.grid[currentCell.x][currentCell.y+1];
							this.agents.put(bot, newCell);
							currentCell.removeBot();
							newCell.addBot();
							ack = new PerceptionMessage(newCell);
						}
						else {
							ack = new PerceptionMessage(currentCell);
						}
						break;
					case WEST:
						if (currentCell.x>0) {
							Cell newCell = this.grid[currentCell.x-1][currentCell.y];
							this.agents.put(bot, newCell);
							currentCell.removeBot();
							newCell.addBot();
							ack = new PerceptionMessage(newCell);
						}
						else {
							ack = new PerceptionMessage(currentCell);
						}
						break;
					case EAST:
						if (currentCell.x<this.width-1) {
							Cell newCell = this.grid[currentCell.x+1][currentCell.y];
							this.agents.put(bot, newCell);
							currentCell.removeBot();
							newCell.addBot();
							ack = new PerceptionMessage(newCell);
						}
						else {
							ack = new PerceptionMessage(currentCell);
						}
						break;
					case NONE:
					default:
						ack = new PerceptionMessage(currentCell);
						break;
					}
				}
				if (ack!=null) sendMessage(ack, bot);
			}
			m = mailbox.removeFirst();
		}
		
		// Notify GUI about changes
		fireGridEvent();
		return s;
	}
	
	/** Fire grid event.
	 */
	protected void fireGridEvent() {
		synchronized(this.listeners) {
			for(GridListener listener : this.listeners) {
				listener.onGridChanged(this.width, this.height, this.grid, this.bases);
			}
		}
	}

	/** Add listener on grid event.
	 * 
	 * @param listener
	 */
	public void addGridListener(GridListener listener) {
		synchronized(this.listeners) {
			this.listeners.add(listener);
		}
	}
	
	/** Remove listener on grid event.
	 * 
	 * @param listener
	 */
	public void removeGridListener(GridListener listener) {
		synchronized(this.listeners) {
			this.listeners.remove(listener);
		}
	}

	/** A cell in the world grid.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public class Cell {

		/** X coordinate of the cell.
		 */
		public final int x;

		/** Y coordinate of the cell.
		 */
		public final int y;

		private int resource;
		
		private int botCount = 0;
		
		/**
		 * @param x is the x-coordinate of the cell.
		 * @param y is the y-coordinate of the cell.
		 * @param resource is the initial amount of resource in the cell. 
		 */
		public Cell(int x, int y, int resource) {
			this.x = x;
			this.y = y;
			this.resource = resource;
		}

		/** Replies the amount of resource on this cell.
		 * 
		 * @return the amount of resource on this cell.
		 */
		public synchronized int getResourceAmount() {
			return this.resource;
		}

		/** Set the amount of resource on this cell.
		 * 
		 * @param amount is the amount of resource on this cell.
		 */
		public synchronized void setResourceAmount(int amount) {
			this.resource = amount;
		}

		/** Consume a resource.
		 * 
		 * @param capacity is the max amount of resource to reply. 
		 * @return the amount of consumed resource.
		 */
		public synchronized int consumeResource(int capacity) {
			if (this.resource>0) {
				int c = Math.min(this.resource, capacity);
				this.resource -= c;
				return c;
			}
			return 0;
		}

		/** Replies the list of neightbours.
		 * 
		 * @return the list of neightbours.
		 */
		public List<Direction> getNeightbours() {
			List<Direction> set = new ArrayList<Direction>(4);
			if (this.y>0) set.add(Direction.NORTH);
			if (this.y<getWorldHeight()-1) set.add(Direction.SOUTH);
			if (this.x>0) set.add(Direction.WEST);
			if (this.x<getWorldWidth()-1) set.add(Direction.EAST);
			return set;
		}
		
		/** Replies the count of bots on this cell.
		 * 
		 * @return the count of bots on this cell.
		 */
		public int getBotCount() {
			return this.botCount;
		}
		
		/** Remove a bot from this cell.
		 */
		public void removeBot() {
			--this.botCount;
			if (this.botCount<0) this.botCount = 0;
		}

		/** Add a bot into this cell.
		 */
		public void addBot() {
			++this.botCount;
		}

	}

}
