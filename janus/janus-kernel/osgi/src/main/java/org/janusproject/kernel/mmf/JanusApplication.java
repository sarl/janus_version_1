/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2009-2011 Janus Core Developers
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
package org.janusproject.kernel.mmf;

import org.janusproject.kernel.agent.KernelAgentFactory;
import org.osgi.framework.BundleActivator;

/**
 * A Janus Application is a special kind of that defines the type of kernel to
 * use and the {@link KernelAuthority}. Only ONE Janus applications is allowed
 * per Kernel.
 * <p>
 * 
 * The {@link KernelAgentFactory} that the application returns is used to spwan the {@link KernelService}.
 * Usually you will return <code>null</code> if you want a stand-alone kernel (i.e. no intereactions over 
 * the network) or a {@code NetworkingKernelAgentFactory} if you want the kernel to use the network and 
 * connect with distant kernels.
 * 
 * <p>
 * Additionally it can define where to automatically (see {@link #isAutoStartJanusModules()}) start registered 
 * {@link JanusModule}s or not. 
 * If true all already registered modules and the ones that are discovered at runtime will be started. 
 *
 *<p>  
 * A typical Network-enable application will do something like this:
 *
 *<p>
 <pre>
 public class MyApplication implements JanusApplication {
 
 	private BundleContext context = null;
 
 	public MyApplication(BundleContext context) {
 		this.context = context;
 	}
 
 	public Status start(IKernelService kernel) {
 		kernel.launchHeavyAgent(new MyAgent());
 		return StatusFactory.ok(this);
 	}
 	
 	public KernelAgentFactory getKernelFactory() {
 		return new NetworkingKernelAgentFactory(context);
 	}
 
 	public IKernelAuthority getKernelAuthority() {
 		//Returning null means all Operations will be approved.
 		return null;
 	}
 
 	public boolean isAutoStartJanusModules() {
 		return true;
 	}
 
 }
 </pre>
 * <p>
 *Then in your {@link BundleActivator} use :
 *<p>
 <pre>
 	public void start(BundleContext context) throws Exception {
		context.registerService(JanusApplication.class.getName(), new MyApplication(context), null);
	}
  </pre>
 *<p>
 *<b>IMPORTANT :</b> A JanusApplication must not be registered as {@link JanusModule} as well.
 *
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface JanusApplication extends JanusModule {

	/**
	 * Replies the {@link KernelAgentFactory} to use when starting the {@link KernelService}.
	 * 
	 * @return the kernel factory to use.
	 */
	public KernelAgentFactory getKernelAgentFactory();

	/**
	 * The Kernel authority to validate kernel operations.
	 * 
	 * @return the authority or null if all operations are to be approved.
	 */
	public KernelAuthority getKernelAuthority();

	/**
	 * Replies if all janus modules should be started automatically.
	 * 
	 * @return a boolean specifying if all janus modules should be started automatically.
	 */
	public boolean isAutoStartJanusModules();
	
	/**
	 * Replies if the OSGi framework running janus should be stopped when the kernel stops.
	 * @return a boolean specifying if the OSGi framework running janus should be stopped when the kernel stops.
	 */
	public boolean isStopOsgiFramework();
	
	/**
	 * Replies if the Kernel should be stopped once no more agents are running.
	 * @return a boolean specifying if the Kernel should be stopped once no more agents are running.
	 */
	public boolean isKeepKernelAlive();

}
