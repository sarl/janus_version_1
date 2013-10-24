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
package org.janusproject.demos.network.januschat;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.network.januschat.agent.ChatAgent;
import org.janusproject.demos.network.januschat.agent.ChatChannel;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.util.event.ListenerCollection;
import org.janusproject.kernel.util.random.RandomNumber;

/** 
 * Utilities to launch a simple chat agent.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ChatUtil {

	private static final ListenerCollection<ChatterListener> listeners = new ListenerCollection<ChatterListener>(); 
	
	/** Create a chatter.
	 * 
	 * @param chatterName is the name of the chatter.
	 * @return the address of the chatter.
	 */	
	public static AgentAddress createChatter(String chatterName) {
		return createChatter(Kernels.get(), chatterName);
	}

	/** Create a chatter.
	 * @return the address of the chatter.
	 */	
	public static AgentAddress createChatter() {
		return createChatter(Kernels.get());
	}

	/** Create a chatter.
	 * 
	 * @param kernel is the Janus kernel to use.
	 * @param chatterName is the name of the chatter.
	 * @return the address of the chatter.
	 */	
	public static AgentAddress createChatter(Kernel kernel, String chatterName) {
		ChatAgent chat = new ChatAgent();
		AgentAddress chatAgent = kernel.submitLightAgent(chat,chatterName);
		fireChatterCreation(chatAgent);
		kernel.launchDifferedExecutionAgents();
		return chatAgent;
	}

	/** Create a chatter.
	 * @param kernel is the Janus kernel to use.
	 * @return the address of the chatter.
	 */	
	public static AgentAddress createChatter(Kernel kernel) {
		String userName = System.getProperty("user.name"); //$NON-NLS-1$
		int rndIndex = RandomNumber.nextInt(Integer.MAX_VALUE);
		if (userName==null || "".equals(userName)) { //$NON-NLS-1$
			userName = Locale.getString(ChatUtil.class, "ANONYMOUS_USER_NAME", rndIndex); //$NON-NLS-1$
		}
		else {
			userName = Locale.getString(ChatUtil.class, "USER_NAME", userName, rndIndex); //$NON-NLS-1$
		}
		return createChatter(kernel, userName);
	}

	/** Replies the channel for the given chatter.
	 * 
	 * @param chatter
	 * @return the channel, never <code>null</code>.
	 */
	public static ChatChannel getChannelFor(AgentAddress chatter) {
		Kernel kernel = Kernels.get();
		if (kernel!=null) {
			ChatChannel channel = kernel.getChannelManager().getChannel(chatter, ChatChannel.class);
			if (channel!=null) return channel;
		}
		throw new IllegalStateException("no chatter channel"); //$NON-NLS-1$
	}
	
	/** Replies the name of the given room.
	 * 
	 * @param room
	 * @return the name of the room.
	 */
	public static String getRoomName(GroupAddress room) {
		String name = room.getName();
		if (name!=null && !name.isEmpty()) return name;
		name = room.getDescription();
		if (name!=null && !name.isEmpty()) return name;
		return room.getUUID().toString();
	}
	
	/** Replies the name of the given chatter.
	 * 
	 * @param chatter
	 * @return the name of the chatter.
	 */
	public static String getChatterName(AgentAddress chatter) {
		String name = chatter.getName();
		if (name!=null && !name.isEmpty()) return name;
		return chatter.getUUID().toString();
	}

	/** Add listener on chatter creation.
	 * 
	 * @param listener
	 */
	public static void addChatterListener(ChatterListener listener) {
		synchronized(ChatUtil.class) {
			listeners.add(ChatterListener.class, listener);
		}
	}

	/** Remove listener on chatter creation.
	 * 
	 * @param listener
	 */
	public static void removeChatterListener(ChatterListener listener) {
		synchronized(ChatUtil.class) {
			listeners.remove(ChatterListener.class, listener);
		}
	}
	
	private static void fireChatterCreation(AgentAddress chatter) {
		ChatterListener[] list;
		synchronized(ChatUtil.class) {
			list = listeners.getListeners(ChatterListener.class);
		}
		for(ChatterListener listener : list) {
			listener.onChatterCreated(chatter);
		}
	}
	
}
