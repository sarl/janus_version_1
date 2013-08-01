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
package org.janusproject.kernel.configuration;

/** This enumeration describes all the read-only properties
 * in Janus configuration.
 * 
 * <h3>How to create a new standard Janus property</h3>
 * <ol>
 * <li>Add a constant in {@link JanusProperty}: you may choose the property name
 * and if this property is read-only or not.</li>
 * <li>If the property has a default value, add a static final constant
 * in {@link JanusProperties}, and with a name prefixed by <code>DEFAULT_</code>.</li>
 * <li>Add a case in the switch of {@link JanusProperties#getProperty(JanusProperty)} in which
 * the default value for the property may be set.</li>
 * <li>Add a case in the switch of {@link JanusProperties#getProperty(JanusProperty, String)} in which
 * the value of the property may be retreived.</li>
 * </ol>
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum JanusProperty {

	/**
	 * Property to find the home folder for an instance of Janus kernel.
	 * The instance of janus kernel depends on the application.
	 * <p>
	 * Read-only: yes.
	 * 
	 * @see JanusProperties#getKernelDirectory()
	 */
	JANUS_KERNEL_HOME("janus.kernel.home", true), //$NON-NLS-1$

	/**
	 * Property to find the home folder for a Janus application.
	 * The folder contains the subfolder dedicated to all
	 * the instances of the kernel running the application.
	 * <p>
	 * Read-only: yes.
	 * 
	 * @see JanusProperties#getApplicationDirectory()
	 */
	JANUS_APPLICATION_HOME("janus.app.home", true), //$NON-NLS-1$

	/**
	 * Property to find the home folder for Janus.
	 * The folder is the top-most folder dedicated to the Janus
	 * platform, including all the applications and the instanced
	 * kernels.
	 * <p>
	 * Read-only: yes.
	 * 
	 * @see JanusProperties#getRootConfigurationDirectory()
	 */
	JANUS_HOME("janus.home", true), //$NON-NLS-1$

	/**
	 * Property to find Janus Application Name.
	 * <p>
	 * Read-only: yes.
	 */
	JANUS_APPLICATION_NAME("janus.app.name", true), //$NON-NLS-1$

	/** Indicates if the groups are persistent.
	 * <p>
	 * Read-only: no.
	 */
	GROUP_PERSISTENCE("janus.groups.persistence", false),  //$NON-NLS-1$

	/** Indicates if the groups are distributed.
	 * <p>
	 * Read-only: no.
	 */
	GROUP_DISTRIBUTION("janus.groups.distribution", false),  //$NON-NLS-1$

	/** Indicates the timeout delay (in milliseconds) to await
	 * before an iddle kernel agent to be killed.
	 * <p>
	 * Read-only: yes.
	 */
	JANUS_KERNEL_KILL_TIMEOUT("janus.kernel.killTimeout", true),  //$NON-NLS-1$

	/** Indicates if new kernels are keeping alive
	 * when they are no more agents to schedule.
	 * Keeping alive is the negation of beeing suicidal.
	 * <p>
	 * Read-only: no.
	 */
	JANUS_KERNEL_KEEP_ALIVE("janus.kernel.keepAlive", false),  //$NON-NLS-1$

	/** Indicates if new agents (excepts for kernels) 
	 * are keeping alive
	 * when they are no more role to play.
	 * Keeping alive is the negation of beeing suicidal.
	 * <p>
	 * Read-only: no.
	 */
	JANUS_AGENT_KEEP_ALIVE("janus.agent.keepAlive", false),  //$NON-NLS-1$

	/** Indicates the default signal management policy 
	 * for role players and roles.
	 * <p>
	 * Read-only: no.
	 */
	JANUS_AGENT_SIGNAL_POLICY("janus.agent.signalPolicy", false), //$NON-NLS-1$

	/** Indicates the default type of mailbox for the agents.
	 * <p>
	 * Read-only: no.
	 */
	JANUS_AGENT_MAILBOX_TYPE("janus.agent.mailbox.type", false), //$NON-NLS-1$

	/** Indicates the default type of mailbox for the roles.
	 * <p>
	 * Read-only: no.
	 */
	JANUS_ROLE_MAILBOX_TYPE("janus.agent.role.mailbox.type", false), //$NON-NLS-1$

	/**
	 * If janus should clean the jxta home, it deletes
	 * all directories.
	 * <p>
	 * Read-only: no.
	 */
	JXTA_CLEAN("net.jxta.clean", false), //$NON-NLS-1$

	/** The identifier of the World-Of-Janus that may be used
	 * to be connectedd to the Janus community thourhg JXTA.
	 * <p>
	 * Read-only: no.
	 */
	JXTA_WOJ_ID("net.jxta.wojId", false), //$NON-NLS-1$

	/**
	 * The Main Applications group PeerGroupID. 
	 * In this JXTA Peer Group is where newly created janus groups are broadcasted to distant kernels.
	 * <p>
	 * Read-only: yes.
	 */
	JXTA_APPLICATION_ID("net.jxta.applicationId", true), //$NON-NLS-1$

	/** Indicates if the JXTA library can log or not.
	 * <p>
	 * Read-only: no.
	 */
	JXTA_LOGGING("net.jxta.logging.Logging", false), //$NON-NLS-1$

	/** The logging level of the JXTA library.
	 * <p>
	 * Read-only: no.
	 */
	JXTA_LEVEL("net.jxta.level", false), //$NON-NLS-1$

	/** The URI that permits to create make a rendez-vous with JXTA.
	 * <p>
	 * Read-only: true.
	 */
	JXTA_SEEDING_URI("net.jxta.seedingUri", true), //$NON-NLS-1$

	/**
	 * The JXTA mode, usually "Edge".
	 * <p>
	 * Read-only: no.
	 */
	JXTA_MODE("net.jxta.mode", false), //$NON-NLS-1$

	/**
	 * JXTA home folder. This folder dependents on an application instance.
	 * <p>
	 * Read-only: true.
	 */
	JXTA_HOME("JXTA_HOME", true), //$NON-NLS-1$

	/**
	 * The inet address of the multicast group to join with ZeroMQ. 
	 * <p>
	 * Read-only: yes.
	 * @since 1.0
	 */
	ZEROMQ_MULICAT_GROUP_ADDRESS("org.zeromq.multicastGroupAddress", true); //$NON-NLS-1$



	private final String propertyName;
	private final boolean isReadOnly;

	private JanusProperty(String name, boolean isReadOnly) {
		this.propertyName = name;
		this.isReadOnly = isReadOnly;
	}

	/** Replies the property name associated to this property.
	 * 
	 * @return the property name, never <code>null</code>
	 */
	public String getPropertyName() {
		return this.propertyName;
	}

	/** Replies if the property is read-only or not.
	 * 
	 * @return <code>true</code> if the property is read only,
	 * otherwise <code>false</code>
	 */
	public boolean isReadOnly() {
		return this.isReadOnly;
	}

	/** Replies the read-only property which is
	 * corresponding to the given property name.
	 * 
	 * @param propName is the property name to match.
	 * @return the read-only property or <code>null</code> if the
	 * given name does not match.
	 */
	public static JanusProperty parse(String propName) {
		assert(propName!=null);
		for(JanusProperty property : JanusProperty.values()) {
			if (property.getPropertyName().equals(propName))
				return property;
		}
		return null;
	}

	/** Replies if the given property name is associated
	 * to a read-only property.
	 * 
	 * @param propName is the property name to match.
	 * @return <code>true</code> if matchs, otherwise
	 * <code>false</code>
	 */
	public static boolean isPropertyName(String propName) {
		return parse(propName)!=null;
	}

	/** Replies if the given property name is associated
	 * to a read-only property.
	 * 
	 * @param propName is the property name to match.
	 * @return <code>true</code> if matchs, otherwise
	 * <code>false</code>
	 */
	public static boolean isReadOnlyPropertyName(String propName) {
		JanusProperty prop = parse(propName);
		return (prop!=null) ? prop.isReadOnly() : false;
	}

}
