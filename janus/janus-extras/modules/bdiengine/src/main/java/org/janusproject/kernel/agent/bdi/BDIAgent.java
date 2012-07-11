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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.bdi.event.BDIBeliefEvent;
import org.janusproject.kernel.agent.bdi.event.BDIEvent;
import org.janusproject.kernel.agent.bdi.event.BDIGoalEvent;
import org.janusproject.kernel.agent.bdi.event.BDIGoalEventType;
import org.janusproject.kernel.agentsignal.QueuedSignalAdapter;
import org.janusproject.kernel.agentsignal.Signal;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * Implements the BDI agent concept of the Janus metamodel. A BDI Agent executes
 * a plan in order to reach a goal. The plan contains actions and represents the
 * "recipe" to execute in order to reach the goal. The agent has beliefs, taken
 * into account during the actions execution. When a goal is reached, the agent
 * select another goal if existing, and therefore select a new plan in order to
 * reach the new goal.
 * 
 * @author $Author: matthias.brigaud@gmail.com$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public abstract class BDIAgent extends Agent implements BDIGoalSelector, BDIPlanSelector {

	private static final long serialVersionUID = 3551372244036534625L;

	/**
	 * Agent's state
	 */
	private BDIAgentState state = BDIAgentState.NO_GOAL_SELECTED;

	/**
	 * Agent's current beliefs
	 */
	protected List<BDIBelief> beliefs;

	/**
	 * Beliefs that have just arrived.
	 */
	protected List<BDIBelief> newBeliefs;

	/**
	 * Plan executor used by this agent.
	 */
	protected final BDIPlanExecutor planExecutor;

	/**
	 * Action factory.
	 */
	protected BDIAbstractActionFactory actionFactory;

	/**
	 * Agent's current goal.
	 */
	private BDIGoal currentGoal = null;

	/**
	 * Agent's current plan.
	 */
	private BDIPlan currentPlan = null;

	/**
	 * Next goals.
	 */
	protected Set<BDIGoal> waitingGoals;

	/**
	 * Agent signal's listener.
	 */
	private QueuedSignalAdapter<Signal> signalListener = new QueuedSignalAdapter<Signal>(Signal.class);

	/**
	 * Create a new BDI Agent.
	 */
	public BDIAgent() {
		super();
		this.beliefs = new ArrayList<BDIBelief>();
		this.newBeliefs = new LinkedList<BDIBelief>();
		this.planExecutor = new BDIPlanExecutor(this);
		this.waitingGoals = new HashSet<BDIGoal>();
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		initActionFactory();
		addSignalListener(this.signalListener);
		addSignalListener(this.planExecutor.getEventListener());
		return null;
	}

	/**
	 * Instanciate the agent factory The developer can use his own factory.
	 */
	protected abstract void initActionFactory();

	/**
	 * Replies the plan executor used by this agent.
	 * 
	 * @return the plan executor
	 */
	public final BDIPlanExecutor getPlanExecutor() {
		return this.planExecutor;
	}

	/**
	 * Get the agent's current goal.
	 * 
	 * @return the agent's current goal
	 */
	public final BDIGoal getCurrentGoal() {
		return this.currentGoal;
	}

	/**
	 * Get the agent's current plan.
	 * 
	 * @return the agent's current plan
	 */
	public final BDIPlan getCurrentPlan() {
		return this.currentPlan;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract BDIGoal selectGoal(Set<BDIGoal> goals, List<BDIBelief> beliefs);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final BDIPlan selectPlan(BDIAgent agent, BDIGoal goal, List<BDIBelief> beliefs) {
		List<Class<? extends BDIPlan>> plans = BDIPlanRepository.getInstance().getPlans(goal);
		List<BDIPlan> planList = getApplicablePlansList(agent, goal, plans, beliefs);
		return selectFromAPL(goal, planList, beliefs);
	}

	/**
	 * Get the APL (applicable plan list). Check is the plan is managed by the requesting agent. Construct a list of relevant plans (those which manage the goal). Construct a list of applicable plans, according to the believes.
	 * 
	 * @param agent
	 *            is the agent calling the method
	 * @param goal
	 *            is the agent's current goal
	 * @param plans
	 *            are the plans managing the goal
	 * @param beliefs
	 *            are the agent's beliefs
	 * @return the Applicable Plan List
	 */
	@SuppressWarnings("unchecked")
	private List<BDIPlan> getApplicablePlansList(BDIAgent agent, BDIGoal goal, List<Class<? extends BDIPlan>> plans, List<BDIBelief> beliefs) {

		List<BDIPlan> handledByAgentPlans = new LinkedList<BDIPlan>();
		List<BDIPlan> relevantPlans = new LinkedList<BDIPlan>();
		List<BDIPlan> applicablePlans = new LinkedList<BDIPlan>();

		for (Class<? extends BDIPlan> p : plans) {
			BDIPlan plan = null;
			Method m = null;

			try {
				m = p.getMethod("getRequiredActions"); //$NON-NLS-1$
			}
			catch (SecurityException e) {
				getLogger().log(Level.WARNING, e.getMessage(), e);
			}
			catch (NoSuchMethodException e) {
				getLogger().log(Level.WARNING, e.getMessage(), e);
			}

			Object o = null;
			if (m != null) {
				try {
					o = m.invoke((Object)null, (Object[])null);
				} catch (IllegalArgumentException e) {
					getLogger().log(Level.WARNING, e.getMessage(), e);
				} catch (IllegalAccessException e) {
					getLogger().log(Level.WARNING, e.getMessage(), e);
				} catch (InvocationTargetException e) {
					getLogger().log(Level.WARNING, e.getMessage(), e);
				}
			}

			List<Class<? extends BDIAction>> actionList = null;
			boolean agentHasRequiredActions = true;
			if (o != null) {
				actionList = (List<Class<? extends BDIAction>>)o;

				assert (actionList != null);

				List<Class<? extends BDIAction>> handledActions = null;

				Method method = null;

				try {
					method = this.getClass().getMethod("getHandledActions"); //$NON-NLS-1$
				} catch (SecurityException e) {
					getLogger().log(Level.WARNING, e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					getLogger().log(Level.WARNING, e.getMessage(), e);
				}

				Object obj = null;
				if (method != null) {
					try {
						obj = method.invoke((Object)null, (Object[])null);
					} catch (IllegalArgumentException e) {
						getLogger().log(Level.WARNING, e.getMessage(), e);
					} catch (IllegalAccessException e) {
						getLogger().log(Level.WARNING, e.getMessage(), e);
					} catch (InvocationTargetException e) {
						getLogger().log(Level.WARNING, e.getMessage(), e);
					}
				}

				if (obj != null) {
					handledActions = List.class.cast(o);
					assert (handledActions != null);
					for (Class<? extends BDIAction> action : actionList) {
						if (!handledActions.contains(action)) {
							agentHasRequiredActions = false;
						}
					}
				}
			}

			if (agentHasRequiredActions) {
				List<BDIAction> actions = new ArrayList<BDIAction>();

				if (actionList!=null) {
					for (Class<? extends BDIAction> action : actionList) {
						actions.add(this.actionFactory.getAction(agent, action));
					}
				}

				Constructor<? extends BDIPlan> c = null;
				try {
					c = p.getConstructor(new Class[] { List.class });
				} catch (SecurityException e) {
					getLogger().log(Level.WARNING, e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					getLogger().log(Level.WARNING, e.getMessage(), e);
				}
				if (c != null) {
					try {
						plan = c.newInstance(actions);
					} catch (IllegalArgumentException e) {
						getLogger().log(Level.WARNING, e.getMessage(), e);
					} catch (InstantiationException e) {
						getLogger().log(Level.WARNING, e.getMessage(), e);
					} catch (IllegalAccessException e) {
						getLogger().log(Level.WARNING, e.getMessage(), e);
					} catch (InvocationTargetException e) {
						getLogger().log(Level.WARNING, e.getMessage(), e);
					}
				}

				handledByAgentPlans.add(plan);
			}
		}

		for (BDIPlan plan : handledByAgentPlans) {
			// TODO check that the plan is obtainable
			if (plan.isRelevant(goal.getClass())) {
				relevantPlans.add(plan);
			}
		}

		for (BDIPlan plan : relevantPlans) {
			if (plan.context(beliefs)) {
				applicablePlans.add(plan);
			}
		}

		return applicablePlans;
	}

	/**
	 * Select a plan from the APL.
	 * 
	 * @param goal
	 *            is the goal to achieve
	 * @param plans
	 *            is the Applicable Plans List
	 * @param beliefs
	 *            are the agent's beliefs
	 * @return the selected plan
	 */
	protected abstract BDIPlan selectFromAPL(BDIGoal goal, List<BDIPlan> plans, List<BDIBelief> beliefs);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {

		changeStateTo(processEvents());

		changeStateTo(run());

		if (this.newBeliefs.size() > 0) {
			updateBeliefs(this.newBeliefs);
			this.newBeliefs.clear();
		}

		return StatusFactory.ok(this);
	}

	/**
	 * Interprets all the received events. Add the BDIBeliefEvent to the new beliefs list. Add the arriving goals to the waiting goals list Change goal and state if a GOAL_CHANGED event is received Change state if a GOAL_SELECTED event is received
	 * 
	 * @return the new agent's state
	 */
	private BDIAgentState processEvents() {
		Signal event = null;

		while ((event = this.signalListener.getFirstAvailableSignal()) != null) {
			if (event instanceof BDIGoalEvent) {
				switch (((BDIGoalEvent) event).getType()) {
				case GOAL_ARRIVED:
					this.waitingGoals.add(((BDIGoalEvent) event).getGoal());
					break;
				case GOAL_CHANGED:
					this.currentGoal = ((BDIGoalEvent) event).getGoal();
					changeStateTo(BDIAgentState.NO_PLAN_SELECTED);
					break;
				case GOAL_SELECTED:
					this.currentGoal = ((BDIGoalEvent) event).getGoal();
					this.waitingGoals.remove(this.currentGoal);
					changeStateTo(BDIAgentState.NO_PLAN_SELECTED);
					break;
				default:
					break;
				}
			} else if (event instanceof BDIBeliefEvent) {
				this.newBeliefs.add(((BDIBeliefEvent) event).getBelief());
			}
		}

		return this.state;
	}

	/**
	 * The BDIAgent's life is represented by a state machine. In each state actions are executed and can lead to a new state.
	 * 
	 * @return the new agent's state
	 */
	private BDIAgentState run() {
		switch (this.state) {
		case NO_GOAL_SELECTED:
			if (this.waitingGoals.size() != 0) {
				BDIGoal goal = selectGoal(this.waitingGoals, this.beliefs);

				if (goal != null) {
					fireSignal(new BDIGoalEvent(goal, BDIGoalEventType.GOAL_SELECTED));
				}
			}

			return BDIAgentState.NO_GOAL_SELECTED;

		case NO_PLAN_SELECTED:
			this.currentPlan = selectPlan(this, this.currentGoal, this.beliefs);

			if (this.currentPlan != null) {
				this.planExecutor.init();
				return BDIAgentState.PLAN_IN_PROGRESS;
			}

			return BDIAgentState.NO_GOAL_SELECTED;

		case PLAN_IN_PROGRESS:
			try {
				BDIPlanStatus planStatus = null;
				planStatus = this.planExecutor.run(this.beliefs);

				switch (planStatus.getType()) {
				case FAILED:
					return BDIAgentState.PLAN_FAILED;
				case IN_PROGRESS:
					return BDIAgentState.PLAN_IN_PROGRESS;
				case PAUSED:
					return BDIAgentState.PLAN_IN_PROGRESS;
				case SUCCESSFUL:
					return BDIAgentState.PLAN_SUCCESSFUL;
				default:
					throw new IllegalStateException();
				}

			} catch (Exception e) {
				getLogger().log(Level.WARNING, e.getMessage(), e);
			}
			return BDIAgentState.PLAN_IN_PROGRESS;

		case PLAN_FAILED:
			return reason();

		case PLAN_SUCCESSFUL:
			this.currentPlan = null;
			this.currentGoal = null;
			return BDIAgentState.NO_GOAL_SELECTED;

		default:
			return this.state;
		}
	}

	/**
	 * Update the believes of the agent.
	 * 
	 * @param newBeliefs
	 *            are the new beliefs
	 */
	protected abstract void updateBeliefs(List<BDIBelief> newBeliefs);

	/**
	 * Reasoning method. Called when a plan has failed to choose the new state.
	 * 
	 * @return the new state
	 */
	protected abstract BDIAgentState reason();

	/**
	 * Modify the agent's state
	 * 
	 * @param state
	 *            is the new state
	 */
	private void changeStateTo(BDIAgentState state) {
		this.state = state;
	}

	/**
	 * Return the actions needed by the plan in order to run. It's mandatory to rewrite this function in the subclass.
	 * 
	 * @return list of actions
	 */
	public static List<Class<? extends BDIAction>> getHandledActions() {
		return getBDIHandledActions();
	}

	/**
	 * Return the basic actions handled by a BDIAgent.
	 * 
	 * @return a list of actions handled by a basic BDIAgent
	 */
	protected static List<Class<? extends BDIAction>> getBDIHandledActions() {
		List<Class<? extends BDIAction>> actions = new ArrayList<Class<? extends BDIAction>>();
		actions.add(SendMessageAction.class);
		actions.add(WaitEventAction.class);
		actions.add(BroadcastMessageAction.class);
		return actions;
	}

	/**
	 * Default method which turn the received messages into signals. The developer should override this method in order to fire his proper signal.
	 */
	protected void processMessages() {
		for (Message msg : getMailbox()) {
			fireSignal(new BDIBeliefEvent(new BDIBelief(msg, BDIBeliefType.MESSAGE)));
		}
	}

	/**
	 * Default action factory. Instanciate an action if it's not contained in the map of actions.
	 * 
	 * @author $Author: matthias.brigaud@gmail.com$
	 * @author $Author: ngaud$
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	protected static class BDIDefaultActionFactory extends BDIAbstractActionFactory {
		/**
		 * 
		 */
		public BDIDefaultActionFactory() {
			// default constructor
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BDIAction createAction(BDIAgent agent, Class<? extends BDIAction> action) {
			try {
				return action.getDeclaredConstructor(new Class[] { agent.getClass() }).newInstance(new Object[] { agent });
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			}

			return null;
		}
	}

	/**
	 * Execute the sendMessage method.
	 * 
	 * @author $Author: matthias.brigaud@gmail.com$
	 * @author $Author: ngaud$
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class SendMessageAction implements BDIAction {
		@Override
		/** {@inheritDoc}
		 */
		public boolean isExecutable(List<BDIBelief> beliefs) {
			return true;
		}

		/**
		 * {@inheritDoc}
		 * Parameter 0 : List<AgentAddress> list of receivers Parameter 1 : Message message
		 */
		@SuppressWarnings({ "unchecked", "synthetic-access" })
		@Override
		public BDIActionStatus execute(Object[] parameters) {
			List<AgentAddress> receivers = null;
			Message msg = null;

			if (parameters.length == 2 && parameters[0] != null && parameters[1] != null) {
				receivers = (List<AgentAddress>) parameters[0];
				msg = (Message) parameters[1];
			}

			if (receivers == null || msg == null) {
				return new BDIActionStatus(BDIActionStatusType.FAILED);
			}

			BDIAgent.this.sendMessage(msg, receivers);

			return new BDIActionStatus(BDIActionStatusType.SUCCESSFUL);
		}
	}

	/**
	 * Update the plan executor's waiting event
	 * 
	 * @author $Author: matthias.brigaud@gmail.com$
	 * @author $Author: ngaud$
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class WaitEventAction implements BDIAction {
		@Override
		/** {@inheritDoc}
		 */
		public boolean isExecutable(List<BDIBelief> beliefs) {
			return true;
		}

		/**
		 * {@inheritDoc} Parameter 0 : Class<? extends BDIEvent> event class
		 */
		@SuppressWarnings("unchecked")
		@Override
		public BDIActionStatus execute(Object[] parameters) {
			Class<? extends BDIEvent> event = null;

			if (parameters.length == 1 && parameters[0] != null)
				event = (Class<? extends BDIEvent>) parameters[0];

			if (event == null)
				return new BDIActionStatus(BDIActionStatusType.FAILED);

			BDIAgent.this.planExecutor.setWaitingEvent(event);

			return new BDIActionStatus(BDIActionStatusType.SUCCESSFUL);
		}
	}

	/**
	 * Execute the broadcastMessage method
	 * 
	 * @author $Author: matthias.brigaud@gmail.com$
	 * @author $Author: ngaud$
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class BroadcastMessageAction implements BDIAction {
		@Override
		/** {@inheritDoc}
		 */
		public boolean isExecutable(List<BDIBelief> beliefs) {
			return true;
		}

		/**
		 * {@inheritDoc} Parameter 0 : List<AgentAddress> list of receivers Parameter 1 : Message message
		 */
		@SuppressWarnings({ "unchecked", "synthetic-access" })
		@Override
		public BDIActionStatus execute(Object[] parameters) {
			List<AgentAddress> receiver = null;
			Message msg = null;

			if (parameters.length == 2 & parameters[0] != null & parameters[1] != null) {
				receiver = (List<AgentAddress>) parameters[0];
				msg = (Message) parameters[1];
			}

			if (receiver == null || msg == null) {
				return new BDIActionStatus(BDIActionStatusType.FAILED);
			}

			BDIAgent.this.broadcastMessage(msg, receiver);

			return new BDIActionStatus(BDIActionStatusType.SUCCESSFUL);
		}
	}
}
