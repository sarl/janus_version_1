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
package org.janusproject.demos.network.januschat.swing.shell;

import java.util.logging.Level;

import org.janusproject.demos.network.januschat.ChatUtil;
import org.janusproject.demos.network.januschat.ChatterListener;
import org.janusproject.demos.network.januschat.swing.ui.ChatRoomFrame;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.network.jxse.agent.JxtaJxseKernelAgentFactory;

/** 
 * SIMPLE CHAT DEMO.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {
	
	/**
	 * @param argv
	 * @throws Exception
	 */	
	public static void main(String[] argv) throws Exception {
		LoggerUtil.setGlobalLevel(Level.SEVERE);
		Kernels.setPreferredKernelFactory(new JxtaJxseKernelAgentFactory());
		ChatUtil.addChatterListener(new Listener());
		ChatUtil.createChatter();
	}
	
	/** 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Listener implements ChatterListener {
	
		/**
		 */
		public Listener() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onChatterCreated(AgentAddress chatter) {
			ChatRoomFrame frame = new ChatRoomFrame(chatter);
			frame.setVisible(true);
		}
		
	}
	
}
