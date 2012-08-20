/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.janusproject.kernel.agent;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.EventListener;
import java.util.Set;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.channels.Channel;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.channels.ChannelInteractableListener;
import org.janusproject.kernel.channels.ChannelInteractableWrapper;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.organization.GroupEvent;
import org.janusproject.kernel.crio.organization.GroupListener;
import org.janusproject.kernel.crio.role.RolePlayingEvent;
import org.janusproject.kernel.crio.role.RolePlayingListener;
import org.janusproject.kernel.util.event.ListenerCollection;

/**
 * A manager of channels.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class ChannelManager {

	private final WeakReference<KernelContext> context;
	private final ListenerCollection<? extends EventListener> listeners = new ListenerCollection<EventListener>();
	
	private Listener groupListener = null;
	
	/**
	 * @param context
	 */
	ChannelManager(KernelContext context) {
		assert(context!=null);
		this.context = new WeakReference<KernelContext>(context);
		Kernel k = context.getKernel();
		if (k!=null) {
			this.groupListener = new Listener();
			k.addGroupListener(this.groupListener);
		}
	}
	
	/** Release any resource owned by this manager.
	 */
	synchronized void release() {
		if (this.groupListener!=null) {
			KernelContext kc = this.context.get();
			if (kc!=null) {
				Kernel k = kc.getKernel();
				if (k!=null) k.removeGroupListener(this.groupListener);
			}
			this.groupListener = null;
		}
	}
	
	/**
	 * Adds a {@link ChannelInteractableListener}
	 * @param listener
	 */
	public void addChannelInteractableListener(ChannelInteractableListener listener) {
		if (listener!=null) {
			this.listeners.add(ChannelInteractableListener.class, listener);
		}
	}
	
	/**
	 * Removes a {@link ChannelInteractableListener}
	 * @param listener
	 */
	public void removeChannelInteractableListener(ChannelInteractableListener listener) {
		if (listener!=null) {
			this.listeners.remove(ChannelInteractableListener.class, listener);
		}
	}
	
	/**
	 * Return the {@link ChannelInteractable} Interface for the agent.
	 * Only local agents can be reached this way.
	 * 
	 * @param address
	 * @return the {@link ChannelInteractable} interface for the agent if the
	 *         agent is local and if it implements the interface, <code>null</code> otherwise.
	 */
	public ChannelInteractable getChannelInteractable(AgentAddress address) {
		KernelContext c = this.context.get();
		if (c!=null) {
			Agent a = c.getAgentRepository().get(address);
			if(a instanceof ChannelInteractable){
				return new ChannelInteractableWrapper((ChannelInteractable) a);
			}
		}
		return null;
	}

	/**
	 * Return the {@link ChannelInteractable} Interface for the role.
	 * Only local roless can be reached this way.
	 * 
	 * @param player is the address of the player.
	 * @param group is the address of the group in which the role is located.
	 * @param role is the role for which a channel should be opened.
	 * @return the {@link ChannelInteractable} interface for the role if the
	 *         role is local and if it implements the interface, <code>null</code> otherwise.
	 */
	public ChannelInteractable getChannelInteractable(AgentAddress player, GroupAddress group, Class<? extends Role> role) {
		KernelContext c = this.context.get();
		if (c!=null) {
			Agent a = c.getAgentRepository().get(player);
			if(a!=null){
				Role r = a.getRoleInstance(group, role);
				if (r instanceof ChannelInteractable) {
					return new ChannelInteractableWrapper((ChannelInteractable) r);
				}
			}
		}
		return null;
	}
	
	/**
	 * Return if the given agent supports channels.
	 * 
	 * @param address
	 * @return <code>true</code> if the agent provides at least one channel;
	 * otherwise <code>false</code>.
	 */
	public boolean isChannelInteractable(AgentAddress address) {
		KernelContext c = this.context.get();
		if (c!=null) {
			Agent a = c.getAgentRepository().get(address);
			return (a instanceof ChannelInteractable);
		}
		return false;
	}

	/**
	 * Return if the given role supports channels.
	 * 
	 * @param player is the address of the player.
	 * @param group is the address of the group in which the role is located.
	 * @param role is the role for which a channel should be opened.
	 * @return <code>true</code> if the role provides at least one channel;
	 * otherwise <code>false</code>.
	 */
	public boolean isChannelInteractable(AgentAddress player, GroupAddress group, Class<? extends Role> role) {
		KernelContext c = this.context.get();
		if (c!=null) {
			Agent a = c.getAgentRepository().get(player);
			if(a!=null){
				Role r = a.getRoleInstance(group, role);
				return (r instanceof ChannelInteractable);
			}
		}
		return false;
	}

	/**
	 * Replies the communication channels supported by this agent.
	 * 
	 * @param address
	 * @return the list of the supported channels
	 */
	public Set<? extends Class<? extends Channel>> getSupportedChannels(AgentAddress address) {
		ChannelInteractable ci = getChannelInteractable(address);
		if (ci!=null) {
			return ci.getSupportedChannels();
		}
		return Collections.emptySet();
	}

	/**
	 * Intanciates a channel of a given class.
	 * <p>
	 * The parameters depend on the requested channel. Please refer to the
	 * channel's documentation for further details.
	 * 
	 * @param <C> is the type of the channel to reply.
	 * @param address
	 * @param channelClass is the type of the channel to reply.
	 * @param params is the parameters to pass to the channel instance.
	 * @return a Channel instance or <code>null</code> if not supported.
	 * @throws IllegalArgumentException
	 *             if the params do not comply to the channel's required
	 *             creations parameters.
	 */
	public <C extends Channel> C getChannel(AgentAddress address, Class<C> channelClass, Object... params) {
		ChannelInteractable ci = getChannelInteractable(address);
		if (ci!=null) {
			return ci.getChannel(channelClass, params);
		}
		return null;
	}
	
	/**
	 * Replies the communication channels supported by this agent.
	 * 
	 * @param player is the address of the player.
	 * @param group is the address of the group in which the role is located.
	 * @param role is the role for which a channel should be opened.
	 * @return the list of the supported channels
	 */
	public Set<? extends Class<? extends Channel>> getSupportedChannels(AgentAddress player, GroupAddress group, Class<? extends Role> role) {
		ChannelInteractable ci = getChannelInteractable(player, group, role);
		if (ci!=null) {
			return ci.getSupportedChannels();
		}
		return Collections.emptySet();
	}

	/**
	 * Intanciates a channel of a given class.
	 * <p>
	 * The parameters depend on the requested channel. Please refer to the
	 * channel's documentation for further details.
	 * 
	 * @param <C> is the type of the channel to reply.
	 * @param player is the address of the player.
	 * @param group is the address of the group in which the role is located.
	 * @param role is the role for which a channel should be opened.
	 * @param channelClass is the type of the channel to reply.
	 * @param params is the parameters to pass to the channel instance.
	 * @return a Channel instance or <code>null</code> if not supported.
	 * @throws IllegalArgumentException
	 *             if the params do not comply to the channel's required
	 *             creations parameters.
	 */
	public <C extends Channel> C getChannel(AgentAddress player, GroupAddress group, Class<? extends Role> role, Class<C> channelClass,
			Object... params) {
		ChannelInteractable ci = getChannelInteractable(player, group, role);
		if (ci!=null) {
			return ci.getChannel(channelClass, params);
		}
		return null;
	}

	/** Notifies the listeners about the destruction of a channel interactable.
	 * 
	 * @param ci
	 */
	void fireChannelInteractableKilled(ChannelInteractable ci) {
		ChannelInteractableWrapper wrapper = new ChannelInteractableWrapper(ci);
		for(ChannelInteractableListener listener : this.listeners.getListeners(ChannelInteractableListener.class)) {
			listener.channelIteractableKilled(wrapper);
		}
	}

	/** Notifies the listeners about the destruction of a channel interactable.
	 * 
	 * @param ci
	 */
	void fireChannelInteractableLaunched(ChannelInteractable ci) {
		ChannelInteractableWrapper wrapper = new ChannelInteractableWrapper(ci);
		for(ChannelInteractableListener listener : this.listeners.getListeners(ChannelInteractableListener.class)) {
			listener.channelIteractableLaunched(wrapper);
		}
	}
	
	/**
	 * @author $Author: srodriguez$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class Listener implements GroupListener, RolePlayingListener {
		
		/**
		 */
		public Listener() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void groupCreated(GroupEvent event) {
			event.getGroup().addRolePlayingListener(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void groupDestroyed(GroupEvent event) {
			event.getGroup().removeRolePlayingListener(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void roleReleased(RolePlayingEvent event) {
			ChannelInteractable ci = event.getChannelInteractable();
			if (ci!=null) fireChannelInteractableKilled(ci);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void roleTaken(RolePlayingEvent event) {
			ChannelInteractable ci = event.getChannelInteractable();
			if (ci!=null) fireChannelInteractableLaunched(ci);
		}

	} // class GroupListener

}
