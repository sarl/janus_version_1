/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.jaak.kernel;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.jaak.envinterface.time.JaakTimeManager;
import org.janusproject.jaak.environment.model.JaakEnvironment;
import org.janusproject.jaak.spawner.JaakSpawner;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Kernels;

/** Utility methods to initialize and manage a Jaak kernel.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JaakKernel {

	/** Initialize the Jaak kernel.
	 * 
	 * @return the controller of the Jaak simulation.
	 * @see #initializeKernel(JaakEnvironment, JaakSpawner...)
	 */
	public static JaakKernelController initializeKernel() {
		return initializeKernel((JaakEnvironment)null);
	}

	/** Initialize the Jaak kernel with the given turtle spawner.
	 * 
	 * @param spawner is the turtle spawner to use.
	 * @return the controller of the Jaak simulation.
	 * @see #initializeKernel(JaakEnvironment, JaakSpawner...)
	 */
	public static JaakKernelController initializeKernel(JaakSpawner... spawner) {
		return initializeKernel(null, spawner);
	}

	/** Initialize the Jaak kernel with the given environment and the given turtle spawner.
	 * <p>
	 * If the given <var>environment</var> is <code>null</code> the default implementation
	 * of a situated environment is used. See {@link JaakEnvironment} for more details.
	 * If the given <var>spawner</var> is <code>null</code>, no spawner will be used. New
	 * turtles should be manually launched on the Janus kernel.
	 * 
	 * @param environment is the situated environment to use.
	 * @param spawner is the turtle spawner to use.
	 * @return the controller of the Jaak simulation.
	 */
	public static JaakKernelController initializeKernel(JaakEnvironment environment, JaakSpawner... spawner) {
		DefaultJaakTimeManager timeManager = new DefaultJaakTimeManager();
		environment.setTimeManager(timeManager);
		JaakKernelAgent jaakAgent = new JaakKernelAgent(timeManager, environment, spawner);
		Kernel kernel = Kernels.create(
				false,
				jaakAgent.newTurtleActivator(),
				timeManager,
				null,
				null,
				null);
		AgentAddress kAdr = kernel.launchHeavyAgent(jaakAgent,
				Locale.getString(JaakKernel.class, "JAAK_KERNEL_NAME")); //$NON-NLS-1$
		
		return new Controller(kAdr, timeManager);
	}
	
	/** Utility methods to initialize and manage a Jaak kernel.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Controller implements JaakKernelController {

		private final JaakTimeManager timeManager;
		private final AgentAddress kernelAddress;
		
		/**
		 * @param kernelAddress is the address of the kernel.
		 * @param timeManager is the time manager.
		 */
		public Controller(AgentAddress kernelAddress, JaakTimeManager timeManager) {
			this.timeManager = timeManager;
			this.kernelAddress = kernelAddress;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress getKernelAddress() {
			return this.kernelAddress;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public JaakTimeManager getTimeManager() {
			return this.timeManager;
		}
		
	}
	
}