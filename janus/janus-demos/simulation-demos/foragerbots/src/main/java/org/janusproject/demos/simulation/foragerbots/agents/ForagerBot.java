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

import java.util.List;

import org.janusproject.demos.simulation.foragerbots.agents.Grid.Cell;
import org.janusproject.demos.simulation.foragerbots.message.MoveMessage;
import org.janusproject.demos.simulation.foragerbots.message.PerceptionMessage;
import org.janusproject.demos.simulation.foragerbots.message.RegistrationAckMessage;
import org.janusproject.demos.simulation.foragerbots.message.RegistrationMessage;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.probe.Watchable;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.random.RandomNumber;

/**
 * A forager bot.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype()
public class ForagerBot extends Agent {
	
	private static final long serialVersionUID = 7487218855639817552L;

	/** Capacity of the bot.
	 */
	@Watchable
	public final int capacity;

	/** Amount of resources.
	 */
	@Watchable
	public int resource;

	private AgentAddress gridAgent = null;
	private int baseX, baseY;
	private State state;
	private int lastResourceX, lastResourceY;
	private boolean nearBase;
	private Direction lastDirection;
	
	/**
	 */
	public ForagerBot() {
		this.capacity = RandomNumber.nextInt(9)+1;
		this.nearBase = RandomNumber.nextBoolean();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		this.state = State.REGISTRATION;
		return StatusFactory.ok(this);
	}
	
	private Direction randomDirection(List<Direction> available) {
		Direction d;
		int i=0;
		d = available.get(RandomNumber.nextInt(available.size()));
		while (i<5 && d==this.lastDirection) {
			d = available.get(RandomNumber.nextInt(available.size()));
			++i;
		}
		
		return i>=5 ? Direction.NONE : d;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		switch(this.state) {
		case REGISTRATION:
			broadcastMessage(new RegistrationMessage());
			this.state = State.WAIT_REGISTRATION;
			break;
		case WAIT_REGISTRATION:
			{
				RegistrationAckMessage ack = getMessage(RegistrationAckMessage.class);
				if (ack!=null) {
					this.gridAgent = (AgentAddress)ack.getSender();
					this.baseX = ack.getBaseX();
					this.baseY = ack.getBaseY();
					this.resource = 0;
					this.lastResourceX = this.baseX;
					this.lastResourceY = this.baseY;
					this.lastDirection = null;
					this.state = State.SEARCH_RESOURCE;
				}
			}
			break;
		case GOTO_RESOURCE:
			{
				PerceptionMessage perception = getMessage(PerceptionMessage.class);
				if (perception!=null) {
					Cell cell = perception.getCurrentCell();
					int dx = this.lastResourceX - cell.x;
					int dy = this.lastResourceY - cell.y;
					if (dx==0 && dy==0) {
						this.lastResourceX = this.baseX;
						this.lastResourceY = this.baseY;
						this.state = State.SEARCH_RESOURCE;
						sendMessage(new MoveMessage(Direction.NONE), this.gridAgent);
					}
					else {
						Direction d;
						if (Math.abs(dx)>=Math.abs(dy)) {
							d = (dx<0) ? Direction.WEST : Direction.EAST;
						}
						else {
							d = (dy<0) ? Direction.NORTH : Direction.SOUTH;
						}
						sendMessage(new MoveMessage(d), this.gridAgent);
						this.lastDirection = d;
					}
				}
			}
			break;
		case SEARCH_RESOURCE:
			{
				PerceptionMessage perception = getMessage(PerceptionMessage.class);
				if (perception!=null) {
					Cell cell = perception.getCurrentCell();
					if (cell.getResourceAmount()>0) {
						this.resource = cell.consumeResource(this.capacity);
						if (this.resource>0) {
							this.lastResourceX = cell.x;
							this.lastResourceY = cell.y;
							this.state = State.RETURN_TO_BASE;
						}
					}
					if (this.resource==0) {
						List<Direction> directions = cell.getNeightbours();
						if (!directions.isEmpty()) {
							Direction dir = randomDirection(directions);
							sendMessage(new MoveMessage(dir), this.gridAgent);
							this.lastDirection = dir;
						}
					}
					else {
						sendMessage(new MoveMessage(Direction.NONE), this.gridAgent);
					}
				}
			}
			break;
		case RETURN_TO_BASE:
			{
				PerceptionMessage perception = getMessage(PerceptionMessage.class);
				if (perception!=null) {
					Cell cell = perception.getCurrentCell();
					int dx = this.baseX - cell.x;
					int dy = this.baseY - cell.y;
					if (dx==0 && dy==0) {
						this.resource = 0;
						if (!this.nearBase
							&& (this.lastResourceX!=this.baseX || this.lastResourceY!=this.baseY)) {
							this.state = State.GOTO_RESOURCE;
						}
						else
							this.state = State.SEARCH_RESOURCE;
						sendMessage(new MoveMessage(Direction.NONE), this.gridAgent);
					}
					else {
						Direction d;
						if (Math.abs(dx)>=Math.abs(dy)) {
							d = (dx<0) ? Direction.WEST : Direction.EAST;
						}
						else {
							d = (dy<0) ? Direction.NORTH : Direction.SOUTH;
						}
						sendMessage(new MoveMessage(d), this.gridAgent);
						this.lastDirection = d;
					}
				}
			}
			break;
		default:
			throw new IllegalStateException();
		}
		return StatusFactory.ok(this);
	}

	/**
	 * A forager bot.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public enum State {
		/** Send registration to the grid.
		 */
		REGISTRATION,
		/** Wait for registration ack from the grid.
		 */
		WAIT_REGISTRATION,
		/** Go to the last known position for resource.
		 */
		GOTO_RESOURCE,
		/** Random Move on the grid to find resource.
		 */
		SEARCH_RESOURCE,
		/** Return back at the base to drop resource.
		 */
		RETURN_TO_BASE;
	}
	
}
