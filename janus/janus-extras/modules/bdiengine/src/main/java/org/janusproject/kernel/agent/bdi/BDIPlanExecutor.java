/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-2012 Janus Core Developers
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
package org.janusproject.kernel.agent.bdi;

import java.lang.ref.WeakReference;
import java.util.List;

import org.janusproject.kernel.agent.bdi.event.BDIEvent;
import org.janusproject.kernel.agentsignal.LastSignalAdapter;

/**
 * Executes a plan.
 * 
 * @author $Author: matthias.brigaud@gmail.com$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public final class BDIPlanExecutor{
	/**
	 * Indicates the agent which has created this plan executor.
	 */
	private final WeakReference<BDIAgent> agent;
	
	/**
	 * Index of the next action to execute.
	 * Increments each time an action is executed.
	 * Put to 0 when a plan is finished.
	 */
	private int currentActionIndex;
	
	/**
	 * The plan executor stop the plan execution if waiting event is not null.
	 * Used to wait a particular event inside a plan.
	 */
	private Class<? extends BDIEvent> waitingEvent = null;
	
	/**
	 * Listener.
	 */
	private final LastSignalAdapter<BDIEvent> eventListener;

	/**
	 * Create a plan executor.
	 * @param agent is the creator
	 */
	public BDIPlanExecutor(BDIAgent agent) {
		this.agent = new WeakReference<BDIAgent>(agent);
		this.eventListener = new LastSignalAdapter<BDIEvent>(BDIEvent.class);
		
		init();
	}
	
	/**
	 * Initialize the action index.
	 */
	public void init() {
		this.currentActionIndex = 0;
	}
	
	/**
	 * Get the current action index.
	 * @return the current action index
	 */
	public int getCurrentActionIndex() {
		return this.currentActionIndex;
	}
	
	/**
	 * Get the plan exxecutor's listener.
	 * @return the planexecutor's listener
	 */
	public LastSignalAdapter<BDIEvent> getEventListener() {
		return this.eventListener;
	}
	
	/**
	 * Update the event the planExecutor is waiting for.
	 * The plan execution is thus temporarly stopped.
	 * @param event 
	 */
	public void setWaitingEvent(Class<? extends BDIEvent> event) {
		this.waitingEvent = event;
	}
	
	/**
	 * Execute the plan if not waiting.
	 * Call the execute method from the current plan
	 * If all actions have been executed, the execution is over.
	 * @param beliefs : agent's beliefs
	 * @return the current plan's status
	 */
	public BDIPlanStatus run(List<BDIBelief> beliefs) {
		if (this.eventListener.getLastReceivedSignal().getClass() == this.waitingEvent)
			this.waitingEvent = null;
		
		if (this.waitingEvent != null)
			return new BDIPlanStatus(BDIPlanStatusType.PAUSED);
		
		BDIPlan plan = this.agent.get().getCurrentPlan();
		List<BDIAction> actions = this.agent.get().getCurrentPlan().getActions();
		
		if (this.currentActionIndex == actions.size()) {
			init();
			return new BDIPlanStatus(BDIPlanStatusType.SUCCESSFUL);
		}
		
		BDIPlanStatus planStatus = plan.execute(this.currentActionIndex, beliefs);

		if (planStatus.getType() != BDIPlanStatusType.IN_PROGRESS)
			return planStatus;
		
		this.currentActionIndex++;

		return planStatus;
	}
}
