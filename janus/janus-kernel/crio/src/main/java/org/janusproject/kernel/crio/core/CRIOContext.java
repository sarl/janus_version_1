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
package org.janusproject.kernel.crio.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.condition.TimeConditionParameterProvider;
import org.janusproject.kernel.configuration.JanusProperties;
import org.janusproject.kernel.crio.interaction.PrivilegedMessageTransportService;
import org.janusproject.kernel.crio.organization.PrivilegedPersistentGroupCleanerService;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.time.KernelTimeManager;
import org.janusproject.kernel.time.VMKernelTimeManager;

/**
 * This class represents an execution context for CRIO
 * classes.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CRIOContext {

	private final UUID contextId;
	private final OrganizationRepository or = new OrganizationRepository();
	private final CapacityExecutor ce = new CapacityExecutor();
	private final GroupRepository gr = new GroupRepository();
	private final KernelTimeManager tm;
	private final TimeConditionParameterProvider tcpp;
	private final DistantCRIOContextHandler distantPlatformHandler;
	private final JanusProperties properties;
	
	/**
	 * @param tm is the time manager to use in this CRIO context.
	 */
	public CRIOContext(KernelTimeManager tm) {
		this(null, tm, null, null);
	}

	/**
	 * @param id is the identifier of the context, if <code>null</code> an identifier
	 * will be randomly selected.
	 * @param tm is the time manager to use in this CRIO context.
	 * @param distantCRIOPlatformHandler the handler to manage distant CRIO platform operations.
	 * @param privilegedServiceListener is the listener to immediately notify with
	 * the privileged services.
	 */
	protected CRIOContext(UUID id, KernelTimeManager tm, DistantCRIOContextHandler distantCRIOPlatformHandler,
			PrivilegedContext privilegedServiceListener) {
		this.contextId = (id==null) ? UUID.randomUUID() : id;
		this.distantPlatformHandler = distantCRIOPlatformHandler;
		if (tm==null)
			this.tm = new VMKernelTimeManager();
		else
			this.tm = tm;
		this.tcpp = new TimeConditionParameterProvider(this.tm);
		if (privilegedServiceListener!=null) {
			this.properties = new JanusProperties(this.contextId, privilegedServiceListener);
			PrivilegedContextImp imp = new PrivilegedContextImp();
			privilegedServiceListener.setPrivilegedMessageTransportService(imp);
			privilegedServiceListener.setPrivilegedPersistentGroupCleanerService(imp);
			privilegedServiceListener.setPrivilegedPlayerAddressService(imp);
		}
		else {
			this.properties = new JanusProperties(this.contextId);
		}
	}
	
	/** Replies the identifier associated to this context.
	 * 
	 * @return the identifier associated to this context.
	 */
	public UUID getId() {
		return this.contextId;
	}
	
	/** Invoked to destroy this context.
	 */
	protected void destroy() {
		this.ce.shutdown();
		this.or.clear();
		this.gr.clear();
		this.properties.reset();
	}
	
	/** Replies the repository of organizations.
	 * 
	 * @return the repository of organizations.
	 */
	final OrganizationRepository getOrganizationRepository() {
		return this.or;
	}

	/** Replies the executor of capacities.
	 * 
	 * @return the executor of capacities.
	 */
	protected final CapacityExecutor getCapacityExecutor() {
		return this.ce;
	}

	/** Replies the repository of groups.
	 *
	 * @return the group repository.
	 */
	final GroupRepository getGroupRepository() {
		return this.gr;
	}
	
	/** Replies the time manager embedded in this CRIO context.
	 * 
	 * @return the time manager embedded in this CRIO context.
	 */
	public KernelTimeManager getTimeManager() {
		return this.tm;
	}	
	
	/** Replies the provider of the time value for
	 * a time-based condition.
	 * 
	 * @return the provider of the condition parameters.
	 * @since 0.5
	 */
	public TimeConditionParameterProvider getTimeConditionParameterProvider() {
		return this.tcpp;
	}	

	/** Replies the distant CRIO platform handler defined in the current context.
	 * 
	 * @return the distant CRIO platform handler defined in the current context.
	 */
	protected DistantCRIOContextHandler getDistantCRIOContextHandler() {
		return this.distantPlatformHandler ;
	}

	/** Replies the properties available in the current context.
	 * 
	 * @return the properties.
	 */
	public JanusProperties getProperties() {
		return this.properties;
	}
	
	/**
	 * This class provides privilegied access to message transport service
	 * in addition to properties. 
	 * 
	 * @author $Author: srodriguez$
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected interface PrivilegedContext extends org.janusproject.kernel.configuration.JanusProperties.PrivilegedContext {
		
		/** Invoked to provide a privileged message transport service.
		 * 
		 * @param mts is the privileged service.
		 */
		public void setPrivilegedMessageTransportService(PrivilegedMessageTransportService mts);

		/** Replies a privileged message transport service.
		 * 
		 * @return the privileged service.
		 * @since 0.4
		 */
		public PrivilegedMessageTransportService getPrivilegedMessageTransportService();

		/** Invoked to provide a privileged persistent group cleaner service.
		 * 
		 * @param pgc is the privileged service.
		 * @since 0.4
		 */
		public void setPrivilegedPersistentGroupCleanerService(PrivilegedPersistentGroupCleanerService pgc);

		/** Replies a privileged persistent group cleaner service.
		 * 
		 * @return the privileged service.
		 * @since 0.4
		 */
		public PrivilegedPersistentGroupCleanerService getPrivilegedPersistentGroupCleanerService();

		/** Invoked to provide a privileged player-address service.
		 * 
		 * @param pgc is the privileged service.
		 * @since 0.5
		 */
		public void setPrivilegedPlayerAddressService(PrivilegedPlayerAddressService pgc);

		/** Replies a privileged player-address service.
		 * 
		 * @return the privileged service.
		 * @since 0.5
		 */
		public PrivilegedPlayerAddressService getPrivilegedPlayerAddressService();

	}
	
	/**
	 * This class provides privilegied access to message transport service. 
	 * 
	 * @author $Author: srodriguez$
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class PrivilegedContextImp
	implements PrivilegedMessageTransportService, PrivilegedPersistentGroupCleanerService, PrivilegedPlayerAddressService {

		/**
		 */
		public PrivilegedContextImp() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean forwardBroadcastMessage(Message message) {
			Address sender = message.getSender();
			if (sender instanceof RoleAddress) {
				GroupAddress group = ((RoleAddress)sender).getGroup();
				if (group!=null) {
					GroupRepository repo = getGroupRepository();
					assert(repo!=null);
					KernelScopeGroup grpInstance = repo.get(group);
					if (grpInstance!=null) {
						grpInstance.broadcastMessage(message, true);
						return true;
					}
				}
			}
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Address forwardMessage(Message message) {
			Address sender = message.getSender();
			if (sender instanceof RoleAddress) {
				GroupAddress group = ((RoleAddress)sender).getGroup();
				if (group!=null) {
					GroupRepository repo = getGroupRepository();
					assert(repo!=null);
					KernelScopeGroup grpInstance = repo.get(group);
					if (grpInstance!=null) {
						return grpInstance.sendMessage(message, true);
					}
				}
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeInactivePersistentGroups(float delay, TimeUnit unit) {
			GroupRepository gr = getGroupRepository();
			KernelTimeManager tm = getTimeManager();
			Collection<GroupAddress> adrs = new ArrayList<GroupAddress>();
			KernelScopeGroup grp;
			for(GroupAddress adr : gr) {
				grp = gr.get(adr);
				if (grp!=null && grp.isTooOldGroup(tm.getCurrentTime(unit), delay)) {
					adrs.add(adr);
				}
			}
			
			for(GroupAddress g : adrs) {
				gr.removeGroup(g);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void bind(AgentAddress address, RolePlayer player) {
			assert(address!=null);
			assert(player!=null);
			if (address instanceof PlayerAddress) {
				((PlayerAddress)address).bind(player);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void unbind(AgentAddress address) {
			assert(address!=null);
			if (address instanceof PlayerAddress) {
				((PlayerAddress)address).unbind();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public RolePlayer getBindedPlayer(AgentAddress address) {
			assert(address!=null);
			if (address instanceof PlayerAddress) {
				return ((PlayerAddress)address).getRolePlayer();
			}
			return null;
		}

	}

}
