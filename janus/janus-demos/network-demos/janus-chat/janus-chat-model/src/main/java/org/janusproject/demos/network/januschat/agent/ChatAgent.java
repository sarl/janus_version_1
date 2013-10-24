/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
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
package org.janusproject.demos.network.januschat.agent;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.network.januschat.organization.ChatOrganization;
import org.janusproject.demos.network.januschat.organization.ChatterRole;
import org.janusproject.demos.network.januschat.organization.ErrorSignal;
import org.janusproject.demos.network.januschat.organization.SendPrivateTextSignal;
import org.janusproject.demos.network.januschat.organization.SendTextSignal;
import org.janusproject.demos.network.januschat.organization.SendToUserCapacity;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.agentsignal.Signal;
import org.janusproject.kernel.agentsignal.SignalListener;
import org.janusproject.kernel.agentsignal.SignalPolicy;
import org.janusproject.kernel.channels.Channel;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.role.RolePlayingEvent;
import org.janusproject.kernel.crio.role.RolePlayingListener;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.status.Status;

/**
 * Personal agent to interact in chatrooms.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(fixedParameters = {})
public class ChatAgent extends Agent implements ChannelInteractable, RolePlayingListener {

	private static final long serialVersionUID = -6588578672342619892L;

	private final ChatChannelImpl channelImpl = new ChatChannelImpl();

	/**
	 * 
	 */
	public ChatAgent() {
		getCapacityContainer().addCapacity(new SendToUserCapacityImpl());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		getSignalManager().setPolicy(SignalPolicy.FIRE_SIGNAL);
		addSignalListener(new SigListener());
		Status s = super.activate(parameters);
		if (s.isSuccess()) {
			String n = getName();
			if (n == null || "".equals(n))n = getAddress().toString(); //$NON-NLS-1$
			createChatRoom(Locale.getString(ChatAgent.class, "MY_PERSONAL_CHAT", n)); //$NON-NLS-1$
		}
		return s;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		Status s = super.live();
		if (s.isSuccess()) {
			if (hasMessage()) {
				IncomingPrivateMessageListener[] pmlist = getEventListeners(IncomingPrivateMessageListener.class);
				for (Message m : getMailbox()) {
					if (m instanceof StringMessage) {
						for (IncomingPrivateMessageListener listener : pmlist) {
							listener.incomingPrivateMessage((AgentAddress)m.getSender(), ((StringMessage) m).getContent());
						}
					}
				}
			}
		}
		return s;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void roleReleased(RolePlayingEvent event) {
		// Notify listeners about the chatter departure.
		IncomingChatListener[] list = getEventListeners(IncomingChatListener.class);
		for (IncomingChatListener listener : list) {
			listener.exitChatroom(event.getGroup().getAddress(), event.getPlayer());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void roleTaken(RolePlayingEvent event) {
		// Notify listeners about the chatter arrival.
		IncomingChatListener[] list = getEventListeners(IncomingChatListener.class);
		for (IncomingChatListener listener : list) {
			listener.joinChatroom(event.getGroup().getAddress(), event.getPlayer());
		}
	}

	/**
	 * Create a new chatroom and join it.
	 * 
	 * @param name
	 *            is the name of the chatroom.
	 * @return the created chat room.
	 */
	protected final GroupAddress createChatRoom(String name) {
		assert (name != null && !"".equals(name)); //$NON-NLS-1$
		GroupAddress ga = createGroup(ChatOrganization.class, name);
		if (ga != null) {
			// Notify listeners about the chatroom creation.
			IncomingChatListener[] list = getEventListeners(IncomingChatListener.class);
			for (IncomingChatListener listener : list) {
				listener.chatroomCreated(ga);
			}

			joinChatRoom(ga);
		}

		return ga;
	}

	/**
	 * Join an existing chatroom.
	 * 
	 * @param chatroom
	 *            is the address of the chatroom.
	 * @return <code>true</code> on success, otherwise <code>false</code>.
	 */
	protected final boolean joinChatRoom(GroupAddress chatroom) {
		assert (chatroom != null);
		addRolePlayingListener(this);
		if (requestRole(ChatterRole.class, chatroom)==null) {
			removeRolePlayingListener(this);
			return false;
		}
		return true;
	}

	/**
	 * Exit from an existing chatroom.
	 * 
	 * @param chatroom
	 *            is the address of the chatroom.
	 * @return <code>true</code> on success, otherwise <code>false</code>.
	 */
	protected final boolean exitChatRoom(GroupAddress chatroom) {
		assert (chatroom != null);
		return leaveRole(ChatterRole.class, chatroom);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C extends Channel> C getChannel(Class<C> channelClass, Object... params) {
		if (channelClass.isAssignableFrom(ChatChannel.class)) {
			return channelClass.cast(this.channelImpl);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<? extends Class<? extends Channel>> getSupportedChannels() {
		return Collections.singleton(ChatChannel.class);
	}

	/**
	 * Implementation of the SendToUserCapacity.
	 * 
	 * @author $Author: srodriguez$
	 * @author $Author: sgalland$
	 * @author $Author: ngaud$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class SendToUserCapacityImpl extends CapacityImplementation implements SendToUserCapacity {

		/**
		 */
		public SendToUserCapacityImpl() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void call(CapacityContext call) throws Exception {
			GroupAddress chatroom = call.getInputValueAt(0, GroupAddress.class);
			RoleAddress emitter = call.getInputValueAt(1, RoleAddress.class);
			String message = call.getInputValueAt(2, String.class);
			onChatRoomMessageReceived(chatroom, emitter, message);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void onChatRoomMessageReceived(GroupAddress chatroom, RoleAddress emitter, String message) {
			// Notify listeners about the join
			IncomingChatListener[] list = ChatAgent.this.getEventListeners(IncomingChatListener.class);
			for (IncomingChatListener listener : list) {
				listener.incomingMessage(chatroom, emitter.getPlayer(), message);
			}
		}

	} // class SendToUserCapacityImpl

	/**
	 * Implementation of the ChatChannel.
	 * 
	 * @author $Author: srodriguez$
	 * @author $Author: sgalland$
	 * @author $Author: ngaud$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class ChatChannelImpl implements ChatChannel {

		/**
		 */
		public ChatChannelImpl() {
			//
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
		@SuppressWarnings("synthetic-access")
		@Override
		public void addIncomingChatListener(IncomingChatListener listener) {
			ChatAgent.this.addEventListener(IncomingChatListener.class, listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void removeIncomingChatListener(IncomingChatListener listener) {
			ChatAgent.this.removeEventListener(IncomingChatListener.class, listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<GroupAddress> getParticipatingChatrooms() {
			return getGroups();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<GroupAddress> getAllChatrooms() {
			return getExistingGroups(ChatOrganization.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<AgentAddress> getChatroomParticipants(GroupAddress chatroom) {
			return getPlayers(ChatterRole.class, chatroom);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void postMessage(GroupAddress chatroom, String message) {
			fireSignal(new SendTextSignal(this, chatroom, message));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void postPrivateMessage(AgentAddress agent, String message) {
			fireSignal(new SendPrivateTextSignal(this, agent, message));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean exitChatroom(GroupAddress chatroom) {
			return ChatAgent.this.exitChatRoom(chatroom);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean joinChatroom(GroupAddress chatroom) {
			return ChatAgent.this.joinChatRoom(chatroom);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void addIncomingPrivateMessageListener(IncomingPrivateMessageListener listener) {
			ChatAgent.this.addEventListener(IncomingPrivateMessageListener.class, listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void removeIncomingPrivateMessageListener(IncomingPrivateMessageListener listener) {
			ChatAgent.this.removeEventListener(IncomingPrivateMessageListener.class, listener);
		}

	} // class ChatChannelImpl

	/**
	 * Implementation of the listener on signals.
	 * 
	 * @author $Author: srodriguez$
	 * @author $Author: sgalland$
	 * @author $Author: ngaud$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class SigListener implements SignalListener {

		/**
		 */
		public SigListener() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void onSignal(Signal signal) {
			if (signal instanceof SendPrivateTextSignal) { // Process incmoing and outgoing Private messages and signals
				SendPrivateTextSignal psig = (SendPrivateTextSignal)signal;
				sendMessage(new StringMessage(psig.getText()), psig.getAgent());
			}
			else if (signal instanceof ErrorSignal) {// Process Error signals
				ErrorSignal esig = (ErrorSignal)signal;
				IncomingChatListener[] list = getEventListeners(IncomingChatListener.class);
				for (IncomingChatListener listener : list) {
					listener.chatroomError(esig.getChatRoom(), esig.getError());
				}
			}

		}
		
	}
	
}
