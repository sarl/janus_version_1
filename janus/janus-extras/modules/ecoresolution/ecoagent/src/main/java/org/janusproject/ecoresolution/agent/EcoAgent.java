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
package org.janusproject.ecoresolution.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.janusproject.ecoresolution.entity.AbstractEcoEntity;
import org.janusproject.ecoresolution.entity.InitializableEcoEntity;
import org.janusproject.ecoresolution.event.EcoEntityListener;
import org.janusproject.ecoresolution.identity.AgentIdentity;
import org.janusproject.ecoresolution.identity.AnyIdentity;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.identity.NoIdentity;
import org.janusproject.ecoresolution.relation.EcoAttack;
import org.janusproject.ecoresolution.relation.EcoRelation;
import org.janusproject.ecoresolution.sm.EcoState;
import org.janusproject.ecoresolution.sm.EcoStateMachine;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.channels.Channel;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;

/** An agent which is using the eco-resolution approach.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(
		fixedParameters={}
)
public abstract class EcoAgent extends Agent implements ChannelInteractable {
	
	private static final long serialVersionUID = -5205688010060112991L;

	private final EcoStateMachine ecoStateMachine;
	private final EcoAgentChannel ecoChannel;
	private final AgentEcoEntity ecoEntity;
	
	/**
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	public EcoAgent(Boolean commitSuicide) {
		super(commitSuicide);
		this.ecoEntity = new AgentEcoEntity();
		this.ecoStateMachine = new EcoStateMachine(this.ecoEntity);
		this.ecoChannel = createEcoChannel();
	}
	
	/**
	 */
	public EcoAgent() {
		this.ecoEntity = new AgentEcoEntity();
		this.ecoStateMachine = new EcoStateMachine(this.ecoEntity);
		this.ecoChannel = createEcoChannel();
	}
	
	/** Invoked to create an eco-channel instance.
	 * 
	 * @return a new instance of eco-channel.
	 */
	protected EcoAgentChannel createEcoChannel() {
		return new EcoAgentChannel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Status live() {
		if (this.ecoStateMachine.run()) {
			this.ecoChannel.fireChannelChange();
		}
		return null;
	}
	
	/** Replies the eco-identity associated to this agent.
	 * @return the eco-identity
	 */
	public EcoIdentity getEcoIdentity() {
		return this.ecoEntity.getIdentity();
	}

	/** Replies the eco-entity associated to this agent.
	 * @return the eco-entity
	 */
	InitializableEcoEntity getEcoEntity() {
		return this.ecoEntity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<? extends Class<? extends Channel>> getSupportedChannels() {
		return Collections.singleton(EcoChannel.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C extends Channel> C getChannel(Class<C> channelClass, Object... params) {
		if (channelClass.isInstance(this.ecoChannel)) {
			return channelClass.cast(this.ecoChannel);
		}
		return null;
	}

	/** Add listener on eco-entity events.
	 * 
	 * @param listener
	 */
	protected final void addEcoEntityListener(EcoEntityListener listener) {
		this.ecoEntity.addEcoEntityListener(listener);
	}

	/** Remove listener on eco-entity events.
	 * 
	 * @param listener
	 */
	protected final void removeEcoEntityListener(EcoEntityListener listener) {
		this.ecoEntity.removeEcoEntityListener(listener);
	}

	/** Replies if the agent is satisfied according to its eco-resolution goals.
	 * 
	 * @return <code>true</code> if the role is satisfied,
	 * otherwise <code>false</code>
	 */
	public final boolean isSatisfied() {
		return this.ecoStateMachine.isTerminalState();
	}

	/** Replies the current state of the eco-agent.
	 * 
	 * @return the current state of the eco-agent.
	 */
	public final EcoState getEcoState() {
		return this.ecoStateMachine.getState();
	}

	/** Select and replies an intruder eco-entity which is
	 * currently avoiding to satisfy the current eco-entity.
	 * 
	 * @return the attack to send to the intruder or <code>null</code> if no intruder.
	 */
	protected abstract EcoAttack selectSatisfactionIntruder();

	/** Select and replies an intruder eco-entity which is
	 * currently avoiding to escape.
	 * 
	 * @param attacks are the attacks on this eco-entity.
	 * @return the collection of the various attacks to send to the intruders or <code>null</code> if no intruder.
	 */
	protected abstract Set<EcoAttack> selectEscapingIntruder(Set<EcoAttack> attacks);

	/** Replies the goal of this eco-entity.
	 * A goal is a relationship to obtain against an
	 * other eco-entity.
	 * 
	 * @return the goal of this eco-entity.
	 */
	protected final EcoRelation getGoal() {
		return this.ecoEntity.getGoal();
	}

	/** Set the goal of this eco-entity.
	 * A goal is a relationship to obtain against an
	 * other eco-entity.
	 * 
	 * @param goal is the goal of this eco-entity.
	 */
	protected final void setGoal(EcoRelation goal) {
		this.ecoEntity.setGoal(goal);
	}

	/** Invoked to escape.
	 * 
	 * @param attacks are the available attacks.
	 */
	protected abstract void doEscaping(Set<EcoAttack> attacks);

	/** Replies the acquaintances of this eco-entity.
	 * An acquaintance in eco-resolution problem solving
	 * is described through a relationship between this
	 * eco-entity (as master) and another eco-entity (as slave). 
	 * 
	 * @return the acquaintances of this eco-entity.
	 */
	protected final Set<EcoRelation> getAcquaintances() {
		return this.ecoEntity.getAcquaintances();
	}
	
	/** Replies the first acquaintance which is matching
	 * the given acquaintance pattern. 
	 * This function assumes that at least one of the participants
	 * to the relation is {@link AnyIdentity} or {@link NoIdentity}.
	 * 
	 * @param pattern
	 * @return the first acquaintance, which is matching the given pattern.
	 */
	protected final EcoRelation getAcquaintance(EcoRelation pattern) {
		assert(pattern!=null);
		for(EcoRelation relation :this.ecoEntity.getAcquaintances()) {
			if (pattern.equals(relation)) {
				return relation;
			}
		}
		return null;
	}
	
	/**
	 * Replies all acquaintances having the specified type.
	 *
	 * @param <T> is the desired EcoRelation type.
	 * @param pattern is the desired EcoRelation type.
	 * @return all acquaintances having the specified type
	 */
	protected final <T extends EcoRelation> Set<T> getAcquaintances(Class<? extends T> pattern) {
		assert(pattern!=null);
		
		Set<T> searchedAcq = new HashSet<T>();
		for(EcoRelation relation :this.ecoEntity.getAcquaintances()) {
			if (pattern.isAssignableFrom(relation.getClass())) {
				searchedAcq.add(pattern.cast(relation));
			}
		}	
		return searchedAcq;
	}
	
	/**
	 * Replies all acquaintances having one of the specified types.
	 * 
	 * @param <T> is the list of searched EcoRelation types
	 * @param patterns is the list of searched EcoRelation types
	 * @return all acquaintances having one of the specified types ordered in a Map
	 */
	protected final <T extends EcoRelation> Map<Class<? extends T>,T> getAcquaintances(Set<Class<? extends T>> patterns) {
		assert(patterns!=null&&patterns.size()>0);
		
		Map<Class<? extends T>,T> searchedAcq = new HashMap<Class<? extends T>,T>();
		for(EcoRelation relation :this.ecoEntity.getAcquaintances()) {
			for (Class<? extends T> pattern : patterns) {
				if (pattern.isAssignableFrom(relation.getClass())) {
					searchedAcq.put(pattern, pattern.cast(relation));
				}
			}

		}	
		return searchedAcq;
	}

	/** Replies the dependencies of this eco-entity.
	 * Dependencies are the eco-entities which depend on
	 * the current eco-entity on there goals. 
	 * 
	 * @return the dependencies of this eco-entity.
	 */
	protected final Set<EcoIdentity> getDependencies() {
		return this.ecoEntity.getDependencies();
	}

	/** Invoked to move to increase the satisfaction.
	 */
	protected abstract void doSatisfactionIncreasing();
	
	/** Invoked when satisfied.
	 */
	protected abstract void doSatisfied();

	/** Force this agent to consider the given relation in its acquaintances.
	 * If the given acquaintance is under conflict with one in the current
	 * knowledge, the knowledge is updated accordingly.
	 * 
	 * @param relation is the new acquaintance to consider.
	 */
	protected final void setAcquaintance(EcoRelation relation) {
		this.ecoEntity.addAcquaintance(relation);
	}
	
	/** Force this agent to consider the given set of relations in its acquaintances.
	 * If one of the given acquaintances is under conflict with another one in the current
	 * knowledge, the knowledge is updated accordingly.
	 * 
	 * @param relations are the new acquaintances to consider.
	 */
	protected final void setAcquaintances(Collection<EcoRelation> relations) {
		for(EcoRelation relation :relations) {
			this.ecoEntity.addAcquaintance(relation);
		}
	}
	
	/** Implementation of an eco-channel.
	 * 
	 * @author $Author: sgalland$
	 * @author $Author: ngaud$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class EcoAgentChannel implements EcoChannel {
		
		private final Collection<EcoChannelListener> channelListeners = new ArrayList<EcoChannelListener>();
		
		/**
		 */
		public EcoAgentChannel() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<EcoRelation> getAcquaintances() {
			return EcoAgent.this.getAcquaintances();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EcoState getEcoState() {
			return EcoAgent.this.getEcoState();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EcoIdentity getEcoEntity() {
			return EcoAgent.this.getEcoIdentity();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Address getChannelOwner() {
			return getAddress();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void addEcoChannelListener(EcoChannelListener listener) {
			this.channelListeners.add(listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void removeEcoChannelListener(EcoChannelListener listener) {
			this.channelListeners.remove(listener);
		}
		
		/**
		 * Notify listeners about eco-agent content changes.
		 */
		public synchronized void fireChannelChange() {
			for(EcoChannelListener listener : this.channelListeners) {
				listener.channelContentChanged();
			}
		}

	} // class EcoAgentChannel
	
	/** Agent implementation of an eco-entity in eco-resolution problem solving.
	 * 
	 * @author $Author: sgalland$
	 * @author $Author: ngaud$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	private class AgentEcoEntity extends AbstractEcoEntity {

		/**
		 */
		public AgentEcoEntity() {
			super(new AgentIdentity(EcoAgent.this.getAddress()));
		}
		
		/** {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		protected void doKill() {
			EcoAgent.this.killMe();
		}
		
		/** {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		protected Iterable<Message> getMessages() {
			return EcoAgent.this.getMessages();
		}

		/** {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		protected void sendMessage(Message message, EcoIdentity receiver) {
			EcoAgent.this.sendMessage(message, ((AgentIdentity)receiver).getAgentAddress());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void doEscaping() {
			EcoAgent.this.doEscaping(getAttacks());
			clearAttacks();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void doSatisfactionIncreasing() {
			EcoAgent.this.doSatisfactionIncreasing();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void doSatisfied() {
			EcoAgent.this.doSatisfied();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<EcoAttack> selectEscapingIntruder(Set<EcoAttack> attacks) {
			return EcoAgent.this.selectEscapingIntruder(attacks);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EcoAttack selectSatisfactionIntruder() {
			return EcoAgent.this.selectSatisfactionIntruder();
		}

	} // class AgentEcoEntity
	
}