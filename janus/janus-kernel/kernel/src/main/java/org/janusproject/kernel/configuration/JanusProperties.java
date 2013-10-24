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

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.text.DateFormat;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.prefs.Preferences;

import org.arakhne.afc.vmutil.FileSystem;

/** This class provides {@link Properties}
 * with support for read-only properties.
 * <p>
 * The read-only properties are described by
 * {@link JanusProperty}.
 * 
 * <h3>How a property value is retreived?</h3>
 * 
 * <code>JanusProperties</code> replies the fist non-<code>null</code> value
 * retreive from the following steps:
 * <ol>
 * <li>get property from the super <code>Properties</code>, including the default properties
 * passed as constructor parameter.</li>
 * <li>loop on the registered {@link JanusPropertyProvider} instances and replies the
 * first provided value (see below for more details on <code>JanusPropertyProvider</code>).</li>
 * <li>If <code>JanusProperties</code> is not the system's properties, ie. replied by
 * {@link System#getProperties()}, nor the default properties passed in constructor
 * parameter, then retreive the value with a {@link System#getProperty(String)} invocation.</li>
 * <li>Search the property in the operating system environment, ie.
 * {@link System#getenv()}.</li>
 * </ol>
 * 
 * <h3>How a property value is set?</h3>
 * 
 * <code>JanusProperties</code> runs the following steps when setting a property value:
 * <ol>
 * <li>If the property is defined as read-only in {@link JanusProperty}, do not set and return.</li>
 * <li>Loop on registered {@link JanusPropertyProvider} instances, if one instance
 * indicates that the property was defined as read-only, then do not set and return.</li>
 * <li>Store the property value.<li>
 * <li>If {@link #isSystemPropertySynchronized()} is <code>true</code>, then
 * set a system's property with the same name, eg. invoke {@link System#setProperty(String, String)}.</li>
 * </ol>
 * 
 * <h3>What is a <code>JanusPropertyProvider</code>?</h3>
 * 
 * A <code>JanusPropertyProvider</code> instance is an object which is 
 * able to provide property values and may be queried by <code>JanusProperties</code>.
 * <p>
 * By default, three types of <code>JanusPropertyProvider</code> implementations are
 * provided:
 * <ul>
 * <li>{@link PreferencesJanusPropertyProvider} is able to retreive properties from given {@link Preferences}.</li>
 * <li>{@link PropertiesJanusPropertyProvider} is able to retreive properties from given {@link Properties}.</li>
 * <li>{@link ResourceBundleJanusPropertyProvider} is able to retreive properties from a {@link ResourceBundle}.</li>
 * </ul>
 * By default, <code>JanusProperties</code> contains a {@link ResourceBundleJanusPropertyProvider}
 * which tries to read the resource named by the value of {@link #DEFAULT_PROPERTY_FILE}.
 * 
 * <h3>How to create a new standard Janus property</h3>
 * 
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
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JanusProperties extends Properties {

	private static final long serialVersionUID = 2559489157064757737L;

	/** This constant is the default application name for Janus kernels.
	 * <p>
	 * Value: <code>DefaultJanusApplication</code>.
	 * 
	 * @see JanusProperty#JANUS_APPLICATION_NAME
	 */
	public static final String DEFAULT_APPLICATION_NAME = "DefaultJanusApplication"; //$NON-NLS-1$

	/** This constant defines the property file where default configuration values
	 * may be put.
	 * <p>
	 * Value: <code>org/janusproject/kernel/configuration/kernelConfiguration</code>.
	 */
	public static final String DEFAULT_PROPERTY_FILE = "org/janusproject/kernel/configuration/kernelConfiguration"; //$NON-NLS-1$
	
	/** This constant defines the default URI for JXTA seeding.
	 * <p>
	 * Value: <code>http://www.janus-project.org/jxta/rendezvous.php?2</code>.
	 * 
	 * @see JanusProperty#JXTA_SEEDING_URI
	 */
	public static final URI DEFAULT_JXTA_SEEDING_URI;

	/** Indicates if the kernel agents are able to stay alive by default.
	 * <p>
	 * Value: <code>true</code>.
	 * 
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 */
	public static final boolean DEFAULT_KERNEL_KEEP_ALIVE = true;

	/** Indicates if the agents are able to stay alive by default.
	 * <p>
	 * Value: <code>true</code>.
	 * 
	 * @see JanusProperty#JANUS_AGENT_KEEP_ALIVE
	 */
	public static final boolean DEFAULT_AGENT_KEEP_ALIVE = false;

	/** This constants indicates if the JXTA folder may be clean by default.
	 * <p>
	 * Value: <code>true</code>.
	 * 
	 * @see JanusProperty#JXTA_CLEAN
	 */
	public static final boolean DEFAULT_JXTA_CLEAN = true;
	
	/** This constants indicates the default mode for JXTA.
	 * <p>
	 * Value: <code>EDGE</code>.
	 * 
	 * @see JanusProperty#JXTA_MODE
	 */
	public static final String DEFAULT_JXTA_MODE = "EDGE"; //$NON-NLS-1$

	/** This constants indicates the default address of the multicast group
	 * used by the ZeroMQ networking layer.
	 * <p>
	 * Value: <code>237.252.249.227</code>.
	 * 
	 * @see JanusProperty#ZEROMQ_MULICAT_GROUP_ADDRESS
	 * @since 1.0
	 */
	public static final String DEFAULT_ZEROMQ_MULICAT_GROUP_ADDRESS = "237.252.249.227"; //$NON-NLS-1$

	/** Timeout delay (in milliseconds) to await a kernel agent
	 * to be killed.
	 * <p>
	 * Value: <code>10000</code>.
	 * 
	 * @see JanusProperty#JANUS_KERNEL_KILL_TIMEOUT
	 */
	public static final long DEFAULT_KERNEL_AGENT_KILL_TIMEOUT = 10000;	
	
	/**
	 * Indicates if, by default, the groups are distributed.
	 * 
	 * @see JanusProperty#GROUP_DISTRIBUTION
	 */
	public static final boolean DEFAULT_GROUP_DISTRIBUTION_FLAG = true;
	
	/**
	 * Indicates if, by default, the groups are persistent.
	 * 
	 * @see JanusProperty#GROUP_PERSISTENCE
	 */
	public static final boolean DEFAULT_GROUP_PERSISTENCE_FLAG = false;
	
	/** This constants indicates if the logging level.
	 * <p>
	 * Value: <code>SEVERE</code>.
	 */
	public static final String DEFAULT_LOGGING_LEVEL = Level.SEVERE.getName();
	
	/** Indicates the default signal management policy 
	 * for role players and roles.
	 * <p>
	 * Value: <code>FIRE_SIGNAL</code>.
	 * 
	 * @see JanusProperty#JANUS_AGENT_SIGNAL_POLICY
	 */
	public static final String DEFAULT_AGENT_SIGNAL_POLICY = "FIRE_SIGNAL"; //$NON-NLS-1$

	/** Indicates the default type of mailbox for the agents.
	 * <p>
	 * Value: <code>org.janusproject.kernel.mailbox.ThreadSafeMailbox</code>.
	 * 
	 * @see JanusProperty#JANUS_AGENT_MAILBOX_TYPE
	 */
	public static final String DEFAULT_AGENT_MAILBOX_TYPE = "org.janusproject.kernel.mailbox.BufferedTreeSetMailbox"; //$NON-NLS-1$

	/** Indicates the default type of mailbox for the roles.
	 * When the type of the role mailbox is <code>null</code>, the
	 * type of the agent mailbox is used in place.
	 * <p>
	 * Value: <code>null</code>.
	 * 
	 * @see JanusProperty#JANUS_ROLE_MAILBOX_TYPE
	 */
	public static final String DEFAULT_ROLE_MAILBOX_TYPE = null;

	static {
		try {
			DEFAULT_JXTA_SEEDING_URI = new URI("http://www.janus-project.org/JxtaRendezVous"); //$NON-NLS-1$
		}
		catch(AssertionError e) {
			throw e;
		}
		catch (Exception e) {
			throw new Error(e);
		}
	}
	
	
	
	
	
	private final List<JanusPropertyProvider> providers = new ArrayList<JanusPropertyProvider>();

	private final AtomicBoolean systemPropertySynchronization = new AtomicBoolean(true);

	private final UUID contextId;
	
	/**
	 * @param contextId is the identifier of the context in whic hthis list of properties
	 * is used.
	 */
	public JanusProperties(UUID contextId) {
		this(contextId, null, null);
	}

	/**
	 * @param contextId is the identifier of the context in whic hthis list of properties
	 * is used.
	 * @param defaults are the default values.
	 */
	public JanusProperties(UUID contextId, Properties defaults) {
		this(contextId, defaults, null);
	}
	
	/**
	 * @param contextId is the identifier of the context in whic hthis list of properties
	 * is used.
	 * @param privilegedServiceListener is the listener to immediately notify with
	 * the privileged services.
	 */
	public JanusProperties(UUID contextId, PrivilegedContext privilegedServiceListener) {
		this(contextId, null, privilegedServiceListener);
	}

	/**
	 * @param contextId is the identifier of the context in whic hthis list of properties
	 * is used.
	 * @param defaults are the default values.
	 * @param privilegedServiceListener is the listener to immediately notify with
	 * the privileged services.
	 */
	public JanusProperties(UUID contextId, Properties defaults, PrivilegedContext privilegedServiceListener) {
		super(defaults);
		assert(contextId!=null);
		this.contextId = contextId;
		this.providers.add(new ResourceBundleJanusPropertyProvider(DEFAULT_PROPERTY_FILE));
		initializeSystemProperties(false);
		if (privilegedServiceListener!=null) {
			privilegedServiceListener.setPrivilegedJanusPropertySetter(new PrivilegedJanusPropertySetterImp());
		}
	}

	/** Replies the identifier of the context in which this list of properties
	 * is used.
	 * 
	 * @return the context identifier.
	 */
	public UUID getContextId() {
		return this.contextId;
	}

	/** Replies if this property manager set the system's properties
	 * when a property is put inside this manager.
	 * <p>
	 * If <code>true</code>, this <code>JanusProperties</code> will
	 * automatically invoke {@link System#setProperty(String, String)}.
	 * 
	 * @return <code>true</code> if synchronized, otherwise <code>false</code>.
	 */
	public boolean isSystemPropertySynchronized() {
		return this.systemPropertySynchronization.get();
	}
	
	/** Set if this property manager set the system's properties
	 * when a property is put inside this manager.
	 * <p>
	 * If <code>true</code>, this <code>JanusProperties</code> will
	 * automatically invoke {@link System#setProperty(String, String)}.
	 * 
	 * @param sync is <code>true</code> if synchronized, otherwise <code>false</code>.
	 */
	public void setSystemPropertySynchronized(boolean sync) {
		this.systemPropertySynchronization.set(sync);
	}

	/** Force the System properties to be set with the
	 * Janus configuration values.
	 * 
	 * @param forceSetting
	 */
	private void initializeSystemProperties(boolean forceSetting) {
		String value;
		for(JanusProperty prop : JanusProperty.values()) {
			try {
				value = getProperty(prop);
				if (value!=null) {
					put(new UnprotectedPropertyName(prop, forceSetting), value);
				}
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				// Secutiry exception
			}
		}
	}

	/** Force the System properties to be unset with the
	 * Janus configuration values.
	 */
	private void uninitializeSystemProperties() {
		String value;
		for(Object name : keySet()) {
			System.clearProperty(name.toString());
		}
		for(JanusProperty prop : JanusProperty.values()) {
			try {
				value = getProperty(prop);
				if (value!=null) {
					String name = prop.getPropertyName();
					System.clearProperty(name);
				}
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				// Secutiry exception
			}
		}
	}

	/** Add property provider.
	 * 
	 * @param provider
	 */
	public synchronized void addJanusPropertyProvider(JanusPropertyProvider provider) {
		assert(provider!=null);
		this.providers.add(provider);
	}
	
	/** Remove property provider.
	 * 
	 * @param provider
	 */
	public synchronized void removeJanusPropertyProvider(JanusPropertyProvider provider) {
		assert(provider!=null);
		this.providers.remove(provider);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Object put(Object key, Object value) {
		assert(key!=null);
		
		String name = key.toString();
		String strValue = (value!=null) ? value.toString() : null; 
		
		if (key instanceof UnprotectedPropertyName) {
			Object v = super.put(name, strValue);
			if (this.systemPropertySynchronization.get()) {
				Properties sysProps = System.getProperties();
				if (sysProps!=null && sysProps!=this) { // this != System properties
					UnprotectedPropertyName unprotect = (UnprotectedPropertyName)key;
					JanusProperty prop = unprotect.getJanusProperty();
					assert(prop!=null);

					boolean changeValue = unprotect.isForced() || !sysProps.contains(name);
					
					if (changeValue) {
						if (strValue==null)
							sysProps.remove(name);
						else
							sysProps.setProperty(name, strValue);
					}
					
					for(JanusProperty depProp : getDependentProperties(prop)) {
						strValue = getProperty(depProp);
						if (strValue==null || strValue.isEmpty()) {
							sysProps.remove(depProp.getPropertyName());
						}
						else {
							sysProps.setProperty(
									depProp.getPropertyName(),
									strValue);
						}
					}
				}
			}
			return v;
		}
		
		if (isReadOnly(name)) return null;
		
		// Test if one provider has mark it has read-only
		for(JanusPropertyProvider provider : this.providers) {
			if (provider!=null) {
				if (provider.isReadOnlyProperty(name)) return null;
			}
		}
		
		Object v;
		
		if (strValue==null) {
			v = super.remove(name);
		}
		else {
			v = super.put(name, strValue);
		}
		
		if (v!=null && this.systemPropertySynchronization.get()) {
			Properties sysProps = System.getProperties();
			if (sysProps!=null && sysProps!=this) { // this != System properties
				if (strValue==null)
					sysProps.remove(name);
				else
					sysProps.setProperty(name, strValue);
			}
		}
		
		return v;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Object remove(Object key) {
		assert(key!=null);
		if (JanusProperty.isReadOnlyPropertyName(key.toString())) return null;
		Object v = super.remove(key);
		if (this.systemPropertySynchronization.get()) {
			System.clearProperty(key.toString());
		}
		return v;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void clear() {
		super.clear();
		initializeSystemProperties(true);
	}

	/**
	 * Clear the content of this properties and
	 * remove all saved values from the global
	 * properties.
	 */
	public synchronized void reset() {
		uninitializeSystemProperties();
		super.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Entry<Object, Object>> entrySet() {
		return new EntrySet(super.entrySet());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Object> keySet() {
		return new KeySet(super.keySet());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Object> values() {
		Set<Entry<Object, Object>> theSet = entrySet();
		if (theSet instanceof EntrySet) {
			theSet = ((EntrySet)theSet).getOriginalSet();
		}
		return new ValueCollection(theSet);
	}

	/** Replies if the given property is read only even with a privilegied set access.
	 * A constant property is a read-only JanusProperty.
	 * <p>
	 * The read-only flag is a global property of the janus property (see 
	 * {@link JanusProperty#isReadOnly()}).
	 * The constant flag is a property inside the current collection of properties.
	 * <p>
	 * The concept of constant property permits to this <code>JanusProperties</code>
	 * to skip property setting when it is required.
	 * <p>
	 * Basically a property is constant when {@link #getProperty(JanusProperty, String)}
	 * always replies the same value (eg. hard-coded value).
	 * 
	 * @param property
	 * @return <code>true</code> if the property is always read-only, <code>false</code>
	 * if it is not read-only or writtable in privileged context.
	 * @see #isReadOnly(String)
	 */
	public static boolean isConstantProperty(JanusProperty property) {
		assert(property!=null);
		return property==JanusProperty.JANUS_KERNEL_HOME
			|| property==JanusProperty.JANUS_APPLICATION_HOME
			|| property==JanusProperty.JANUS_HOME
			|| property==JanusProperty.JXTA_HOME;
	}
	
	/** Replies the value of the property with the given name.
	 * 
	 * @param property is the property to retreive.
	 * @return is the value of the property, <code>null</code> is replied
	 * if the property was never set before.
	 */
	public String getProperty(JanusProperty property) {
		String defVal = null;
		switch(property) {
		case JXTA_SEEDING_URI:
			defVal = DEFAULT_JXTA_SEEDING_URI.toString();
			break;
		case JXTA_CLEAN:
			defVal = Boolean.toString(DEFAULT_JXTA_CLEAN);
			break;
		case JXTA_MODE:
			defVal = DEFAULT_JXTA_MODE;
			break;
		case JANUS_KERNEL_KEEP_ALIVE:
			defVal = Boolean.toString(DEFAULT_KERNEL_KEEP_ALIVE);
			break;
		case JANUS_AGENT_KEEP_ALIVE:
			defVal = Boolean.toString(DEFAULT_AGENT_KEEP_ALIVE);
			break;
		case JANUS_AGENT_SIGNAL_POLICY:
			defVal = DEFAULT_AGENT_SIGNAL_POLICY;
			break;
		case JANUS_AGENT_MAILBOX_TYPE:
			defVal = DEFAULT_AGENT_MAILBOX_TYPE;
			break;
		case JANUS_ROLE_MAILBOX_TYPE:
			defVal = DEFAULT_ROLE_MAILBOX_TYPE;
			break;
		case JANUS_KERNEL_KILL_TIMEOUT:
			defVal = Long.toString(DEFAULT_KERNEL_AGENT_KILL_TIMEOUT);
			break;
		case GROUP_DISTRIBUTION:
			defVal = Boolean.toString(DEFAULT_GROUP_DISTRIBUTION_FLAG);
			break;
		case GROUP_PERSISTENCE:
			defVal = Boolean.toString(DEFAULT_GROUP_PERSISTENCE_FLAG);
			break;
		case JANUS_APPLICATION_NAME:
			defVal = DEFAULT_APPLICATION_NAME;
			break;
		case JXTA_LOGGING:
		case JXTA_LEVEL:
			defVal = DEFAULT_LOGGING_LEVEL;
			break;
		case ZEROMQ_MULICAT_GROUP_ADDRESS:
			defVal = DEFAULT_ZEROMQ_MULICAT_GROUP_ADDRESS;
			break;
		case JANUS_KERNEL_HOME:
		case JANUS_APPLICATION_HOME:
		case JANUS_HOME:
		case JXTA_HOME:
		case JXTA_WOJ_ID:
		case JXTA_APPLICATION_ID:
			// null default value
			break;
		default:
		}
		return getProperty(property, defVal);
	}

	/** Replies the properties that are dependent on the value of the given property.
	 * 
	 * @param prop
	 * @return the properties which are depending on <var>prop</var>. 
	 */
	protected static JanusProperty[] getDependentProperties(JanusProperty prop) {
		switch(prop) {
		case JANUS_APPLICATION_NAME:
			return new JanusProperty[] {
					JanusProperty.JANUS_KERNEL_HOME,
					JanusProperty.JANUS_APPLICATION_HOME,
					JanusProperty.JXTA_HOME,
				};
		case JANUS_HOME:
			return new JanusProperty[] {
					JanusProperty.JANUS_KERNEL_HOME,
					JanusProperty.JANUS_APPLICATION_HOME,
					JanusProperty.JXTA_HOME,
				};
		case JANUS_APPLICATION_HOME:
			return new JanusProperty[] {
					JanusProperty.JANUS_KERNEL_HOME,
					JanusProperty.JXTA_HOME,
				};
		case JANUS_AGENT_MAILBOX_TYPE:
			return new JanusProperty[] {
					JanusProperty.JANUS_ROLE_MAILBOX_TYPE,
				};
		case GROUP_DISTRIBUTION:
		case GROUP_PERSISTENCE:
		case JANUS_KERNEL_HOME:
		case JANUS_AGENT_KEEP_ALIVE:
		case JANUS_AGENT_SIGNAL_POLICY:
		case JANUS_ROLE_MAILBOX_TYPE:
		case JANUS_KERNEL_KEEP_ALIVE:
		case JANUS_KERNEL_KILL_TIMEOUT:
		case JXTA_HOME:
		case JXTA_APPLICATION_ID:
		case JXTA_CLEAN:
		case JXTA_MODE:
		case JXTA_SEEDING_URI:
		case JXTA_WOJ_ID:
		case JXTA_LEVEL:
		case JXTA_LOGGING:
		case ZEROMQ_MULICAT_GROUP_ADDRESS:
			return new JanusProperty[0];
		default:
		}
		throw new IllegalArgumentException(prop.toString());
	}
	
	/** Replies the value of the property with the given name.
	 * 
	 * @param property is the property to retreive.
	 * @param defaultValue is the value to reply is the property was not set.
	 * @return is the value of the property, <code>null</code> is replied
	 * if the property was never set before.
	 */
	public String getProperty(JanusProperty property, String defaultValue) {
		assert(property!=null);
		switch(property) {
		case JANUS_KERNEL_HOME:
			return getKernelDirectory().getAbsolutePath();
		case JANUS_APPLICATION_HOME:
			return getApplicationDirectory().getAbsolutePath();
		case JANUS_HOME:
			return getRootConfigurationDirectory().getAbsolutePath();
		case JXTA_HOME:
			return FileSystem.join(getKernelDirectory(), "jxta").getAbsolutePath(); //$NON-NLS-1$
		case JANUS_ROLE_MAILBOX_TYPE:
			{
				String propertyValue = getProperty(property.getPropertyName(), defaultValue);
				if (propertyValue==null || propertyValue.isEmpty()) {
					getProperty(JanusProperty.JANUS_AGENT_MAILBOX_TYPE.getPropertyName(), DEFAULT_AGENT_MAILBOX_TYPE);
				}
				return propertyValue;
			}
		case JANUS_AGENT_MAILBOX_TYPE:
		case GROUP_DISTRIBUTION:
		case GROUP_PERSISTENCE:
		case JANUS_APPLICATION_NAME:
		case JANUS_KERNEL_KEEP_ALIVE:
		case JANUS_AGENT_KEEP_ALIVE:
		case JANUS_AGENT_SIGNAL_POLICY:
		case JANUS_KERNEL_KILL_TIMEOUT:
		case JXTA_CLEAN:
		case JXTA_MODE:
		case JXTA_SEEDING_URI:
		case JXTA_WOJ_ID:
		case JXTA_APPLICATION_ID:
		case JXTA_LEVEL:
		case JXTA_LOGGING:
		case ZEROMQ_MULICAT_GROUP_ADDRESS:
			return getProperty(property.getPropertyName(), defaultValue);
		default:
		}
		throw new IllegalArgumentException(property.toString());
	}

	/** Replies if the property with the given name is
	 * read-only.
	 * <p>
	 * This function is <em>not</em> equivalent to
	 * <code>JanusProperty.isReadOnlyPropertyName(name)</code>.
	 * Indeed, this function also returns <code>true</code> if
	 * the property is currently stored inside one of the
	 * known resource bundles.
	 * 
	 * @param name
	 * @return <code>true</code> if the property is read-only,
	 * otherwise <code>false</code>
	 * @see JanusProperty#isReadOnly()
	 * @see #isConstantProperty(JanusProperty)
	 */
	public synchronized boolean isReadOnly(String name) {
		assert(name!=null);
		if (JanusProperty.isReadOnlyPropertyName(name))
			return true;

		// Test if one provider has mark it has read-only
		for(JanusPropertyProvider provider : this.providers) {
			if (provider!=null) {
				if (provider.isReadOnlyProperty(name)) return true;
			}
		}

		return false;
	}

	/**
     * {@inheritDoc}
     */
    @Override
	public synchronized Object get(Object key) {
    	Object value = super.get(key);
		if (value!=null) return value;
		
		String keyName = key.toString();
    	
    	// Search in property providers.
   		for(JanusPropertyProvider provider : this.providers) {
			if (provider!=null) {
				value = provider.getProperty(keyName);
				if (value!=null) return value;
			}
		}
    	
    	// Search in system's properties.
    	Properties systemProps = System.getProperties();
    	if (systemProps!=null && systemProps!=this && systemProps!=this.defaults) {
    		value = systemProps.getProperty(keyName);
			if (value!=null) return value;
    	}
    	
    	// Search in environment variables.
    	return System.getenv(keyName);
    }
    
    /** Replies the boolean value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @return is the value of the property.
	 */
	public boolean getBoolean(JanusProperty property) {
		String value = getProperty(property);
		if (value!=null) {
			try {
				return Boolean.parseBoolean(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return false;
	}

    /** Replies the boolean value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public boolean getBoolean(JanusProperty property, boolean defaultValue) {
		String value = getProperty(property, null);
		if (value!=null) {
			try {
				return Boolean.parseBoolean(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

    /** Replies the byte value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @return is the value of the property.
	 */
	public byte getByte(JanusProperty property) {
		String value = getProperty(property);
		if (value!=null) {
			try {
				return Byte.parseByte(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return (byte)0;
	}

    /** Replies the byte value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public byte getByte(JanusProperty property, byte defaultValue) {
		String value = getProperty(property, null);
		if (value!=null) {
			try {
				return Byte.parseByte(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

    /** Replies the short value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @return is the value of the property.
	 */
	public short getShort(JanusProperty property) {
		String value = getProperty(property);
		if (value!=null) {
			try {
				return Short.parseShort(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return 0;
	}

    /** Replies the short value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public short getShort(JanusProperty property, short defaultValue) {
		String value = getProperty(property, null);
		if (value!=null) {
			try {
				return Short.parseShort(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

    /** Replies the int value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @return is the value of the property.
	 */
	public int getInt(JanusProperty property) {
		String value = getProperty(property);
		if (value!=null) {
			try {
				return Integer.parseInt(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return 0;
	}

    /** Replies the int value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public int getInt(JanusProperty property, int defaultValue) {
		String value = getProperty(property, null);
		if (value!=null) {
			try {
				return Integer.parseInt(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

    /** Replies the long value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @return is the value of the property.
	 */
	public long getLong(JanusProperty property) {
		String value = getProperty(property);
		if (value!=null) {
			try {
				return Long.parseLong(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return 0l;
	}

    /** Replies the long value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public long getLong(JanusProperty property, long defaultValue) {
		String value = getProperty(property, null);
		if (value!=null) {
			try {
				return Long.parseLong(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

    /** Replies the float value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @return is the value of the property.
	 */
	public float getFloat(JanusProperty property) {
		String value = getProperty(property);
		if (value!=null) {
			try {
				return Float.parseFloat(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return 0f;
	}

    /** Replies the float value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public float getFloat(JanusProperty property, float defaultValue) {
		String value = getProperty(property, null);
		if (value!=null) {
			try {
				return Float.parseFloat(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

    /** Replies the double value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @return is the value of the property.
	 */
	public double getDouble(JanusProperty property) {
		String value = getProperty(property);
		if (value!=null) {
			try {
				return Double.parseDouble(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return 0.;
	}

    /** Replies the double value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public double getDouble(JanusProperty property, double defaultValue) {
		String value = getProperty(property, null);
		if (value!=null) {
			try {
				return Double.parseDouble(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

    /** Replies the char value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @return is the value of the property.
	 */
	public char getChar(JanusProperty property) {
		String value = getProperty(property);
		if (value!=null && value.length()>0) {
			return value.charAt(0);
		}
		return '\0';
	}

    /** Replies the char value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public char getChar(JanusProperty property, char defaultValue) {
		String value = getProperty(property, null);
		if (value!=null && value.length()>0) {
			return value.charAt(0);
		}
		return defaultValue;
	}

    /** Replies the byte-array value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @return is the value of the property.
	 */
	public byte[] getByteArray(JanusProperty property) {
		String value = getProperty(property);
		if (value!=null) {
			return value.getBytes();
		}
		return null;
	}

    /** Replies the byte-array value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public byte[] getByteArray(JanusProperty property, byte[] defaultValue) {
		String value = getProperty(property, null);
		if (value!=null) {
			return value.getBytes();
		}
		return defaultValue;
	}

    /** Replies the big decimal value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @return is the value of the property.
	 */
	public BigDecimal getBigDecimal(JanusProperty property) {
		String value = getProperty(property);
		if (value!=null) {
			try {
				return new BigDecimal(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return null;
	}

    /** Replies the big decimal value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public BigDecimal getBigDecimal(JanusProperty property, BigDecimal defaultValue) {
		String value = getProperty(property, null);
		if (value!=null) {
			try {
				return new BigDecimal(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

    /** Replies the big integer value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @return is the value of the property.
	 */
	public BigInteger getBigInteger(JanusProperty property) {
		String value = getProperty(property);
		if (value!=null) {
			try {
				return new BigInteger(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return null;
	}

    /** Replies the big integer value of the property.
	 * 
	 * @param property is the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public BigInteger getBigInteger(JanusProperty property, BigInteger defaultValue) {
		String value = getProperty(property, null);
		if (value!=null) {
			try {
				return new BigInteger(value);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

	/** Replies the boolean value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @return is the value of the property.
	 */
	public boolean getBoolean(String name) {
		return getBoolean(name, false);
	}

	/** Replies the boolean value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public boolean getBoolean(String name, boolean defaultValue) {
		String strValue = getProperty(name, null);
		if (strValue!=null) {
			try {
				return Boolean.parseBoolean(strValue);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

	/** Replies the byte value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @return is the value of the property.
	 */
	public byte getByte(String name) {
		return getByte(name, (byte)0);
	}

	/** Replies the byte value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public byte getByte(String name, byte defaultValue) {
		String strValue = getProperty(name, null);
		if (strValue!=null) {
			try {
				return Byte.parseByte(strValue);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

	/** Replies the short value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @return is the value of the property.
	 */
	public short getShort(String name) {
		return getShort(name, (short)0);
	}

	/** Replies the short value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public short getShort(String name, short defaultValue) {
		String strValue = getProperty(name, null);
		if (strValue!=null) {
			try {
				return Short.parseShort(strValue);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

	/** Replies the int value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @return is the value of the property.
	 */
	public int getInt(String name) {
		return getInt(name, 0);
	}

	/** Replies the int value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public int getInt(String name, int defaultValue) {
		String strValue = getProperty(name, null);
		if (strValue!=null) {
			try {
				return Integer.parseInt(strValue);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

	/** Replies the long value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @return is the value of the property.
	 */
	public long getLong(String name) {
		return getLong(name, 0l);
	}

	/** Replies the long value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public long getLong(String name, long defaultValue) {
		String strValue = getProperty(name, null);
		if (strValue!=null) {
			try {
				return Long.parseLong(strValue);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

	/** Replies the float value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @return is the value of the property.
	 */
	public float getFloat(String name) {
		return getFloat(name, 0f);
	}

	/** Replies the float value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public float getFloat(String name, float defaultValue) {
		String strValue = getProperty(name, null);
		if (strValue!=null) {
			try {
				return Float.parseFloat(strValue);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

	/** Replies the double value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @return is the value of the property.
	 */
	public double getDouble(String name) {
		return getDouble(name, 0.);
	}

	/** Replies the double value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public double getDouble(String name, double defaultValue) {
		String strValue = getProperty(name, null);
		if (strValue!=null) {
			try {
				return Double.parseDouble(strValue);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

	/** Replies the char value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @return is the value of the property.
	 */
	public char getChar(String name) {
		return getChar(name, '\0');
	}

	/** Replies the char value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public char getChar(String name, char defaultValue) {
		String strValue = getProperty(name, null);
		if (strValue!=null && strValue.length()>0) {
			return strValue.charAt(0);
		}
		return defaultValue;
	}

	/** Replies the byte-array value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @return is the value of the property.
	 */
	public byte[] getByteArray(String name) {
		return getByteArray(name, null);
	}

	/** Replies the byte-array value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public byte[] getByteArray(String name, byte[] defaultValue) {
		String strValue = getProperty(name, null);
		if (strValue!=null) {
			return strValue.getBytes();
		}
		return defaultValue;
	}

	/** Replies the big decimal value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @return is the value of the property.
	 */
	public BigDecimal getBigDecimal(String name) {
		return getBigDecimal(name, null);
	}

	/** Replies the boolean value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public BigDecimal getBigDecimal(String name, BigDecimal defaultValue) {
		String strValue = getProperty(name, null);
		if (strValue!=null) {
			try {
				return new BigDecimal(strValue);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

	/** Replies the big integer value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @return is the value of the property.
	 */
	public BigInteger getBigInteger(String name) {
		return getBigInteger(name, null);
	}

	/** Replies the big integer value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public BigInteger getBigInteger(String name, BigInteger defaultValue) {
		String strValue = getProperty(name, null);
		if (strValue!=null) {
			try {
				return new BigInteger(strValue);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

	/** Replies the date value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @return is the value of the property.
	 */
	public Date getDate(String name) {
		return getDate(name, null);
	}

	/** Replies the date value of the property with the given name.
	 * 
	 * @param name is the name of the property to retreive.
	 * @param defaultValue is the default value to reply if the property was not set.
	 * @return is the value of the property.
	 */
	public Date getDate(String name, Date defaultValue) {
		String strValue = getProperty(name, null);
		if (strValue!=null) {
			try {
				DateFormat fmt = DateFormat.getDateTimeInstance();
				return fmt.parse(strValue);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
			try {
				DateFormat fmt = DateFormat.getDateInstance();
				return fmt.parse(strValue);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
		}
		return defaultValue;
	}

	/** Set the value of a property.
	 * 
	 * @param name is the name of the property
	 * @param value is the value of the property.
	 */
	public void setProperty(String name, boolean value) {
		setProperty(name, Boolean.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param name is the name of the property
	 * @param value is the value of the property.
	 */
	public void setProperty(String name, byte value) {
		setProperty(name, Byte.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param name is the name of the property
	 * @param value is the value of the property.
	 */
	public void setProperty(String name, short value) {
		setProperty(name, Short.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param name is the name of the property
	 * @param value is the value of the property.
	 */
	public void setProperty(String name, int value) {
		setProperty(name, Integer.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param name is the name of the property
	 * @param value is the value of the property.
	 */
	public void setProperty(String name, long value) {
		setProperty(name, Long.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param name is the name of the property
	 * @param value is the value of the property.
	 */
	public void setProperty(String name, float value) {
		setProperty(name, Float.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param name is the name of the property
	 * @param value is the value of the property.
	 */
	public void setProperty(String name, double value) {
		setProperty(name, Double.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param name is the name of the property
	 * @param value is the value of the property.
	 */
	public void setProperty(String name, char value) {
		setProperty(name, Character.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param name is the name of the property
	 * @param value is the value of the property.
	 */
	public void setProperty(String name, Number value) {
		setProperty(name, value==null ? null : value.toString());
	}

	/** Set the value of a property.
	 * 
	 * @param name is the name of the property
	 * @param value is the value of the property.
	 */
	public void setProperty(String name, Date value) {
		DateFormat fmt = DateFormat.getDateTimeInstance();
		setProperty(name, value==null ? null : fmt.format(value));
	}

	/** Set the value of a property.
	 * 
	 * @param property is the property
	 * @param value is the value of the property.
	 */
	public void setProperty(JanusProperty property, String value) {
		assert(property!=null);
		setProperty(property.getPropertyName(), value);
	}

	/** Set the value of a property.
	 * 
	 * @param property is the property
	 * @param value is the value of the property.
	 */
	public void setProperty(JanusProperty property, boolean value) {
		assert(property!=null);
		setProperty(property.getPropertyName(), Boolean.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param property is the property
	 * @param value is the value of the property.
	 */
	public void setProperty(JanusProperty property, byte value) {
		assert(property!=null);
		setProperty(property.getPropertyName(), Byte.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param property is the property
	 * @param value is the value of the property.
	 */
	public void setProperty(JanusProperty property, short value) {
		assert(property!=null);
		setProperty(property.getPropertyName(), Short.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param property is the property
	 * @param value is the value of the property.
	 */
	public void setProperty(JanusProperty property, int value) {
		assert(property!=null);
		setProperty(property.getPropertyName(), Integer.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param property is the property
	 * @param value is the value of the property.
	 */
	public void setProperty(JanusProperty property, long value) {
		assert(property!=null);
		setProperty(property.getPropertyName(), Long.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param property is the property
	 * @param value is the value of the property.
	 */
	public void setProperty(JanusProperty property, float value) {
		assert(property!=null);
		setProperty(property.getPropertyName(), Float.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param property is the property
	 * @param value is the value of the property.
	 */
	public void setProperty(JanusProperty property, double value) {
		assert(property!=null);
		setProperty(property.getPropertyName(), Double.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param property is the property
	 * @param value is the value of the property.
	 */
	public void setProperty(JanusProperty property, char value) {
		assert(property!=null);
		setProperty(property.getPropertyName(), Character.toString(value));
	}

	/** Set the value of a property.
	 * 
	 * @param property is the property
	 * @param value is the value of the property.
	 */
	public void setProperty(JanusProperty property, Number value) {
		assert(property!=null);
		setProperty(property.getPropertyName(), value==null ? null : value.toString());
	}

	/** Set the value of a property.
	 * 
	 * @param property is the property
	 * @param value is the value of the property.
	 */
	public void setProperty(JanusProperty property, Date value) {
		DateFormat fmt = DateFormat.getDateTimeInstance();
		setProperty(property.getPropertyName(), value==null ? null : fmt.format(value));
	}

	/** Replies the directory where the configurations of all the Janus applications
	 * could be found.
	 * <p>
	 * The file returned by this function depends on your current
	 * operating system.
	 * On Unix operating systems, the directory is 
	 * {@code $HOME/.config/janus}. On Windows&reg; operating systems, the
	 * directory is
	 * {@code C:<span>\</span>Documents and Settings<span>\</span>USERNAME<span>\</span>Local Settings<span>\</span>Application Data<span>\</span>janus}.
	 * {@code USERNAME} is the login of the current user.
	 * 
	 * @return the configuration directory of all the janus applications.
	 * @see #getApplicationDirectory()
	 * @see #getKernelDirectory()
	 * @since 0.4
	 */
	public static File getRootConfigurationDirectory() {
		return FileSystem.getUserConfigurationDirectoryFor("janus"); //$NON-NLS-1$
	}

	/** Replies the root directory of the current Janus applications.
	 * <p>
	 * The file returned by this function depends on your current
	 * operating system.
	 * On Unix operating systems, the directory is 
	 * {@code $HOME/.config/janus/NAME}. On Windows&reg; operating systems, the
	 * directory is
	 * {@code C:<span>\</span>Documents and Settings<span>\</span>USERNAME<span>\</span>Local Settings<span>\</span>Application Data<span>\</span>janus<span>\</span>NAME}.
	 * {@code USERNAME} is the login of the current user, and {@code NAME} is the name of the
	 * application replied by {@link JanusProperty#JANUS_APPLICATION_NAME}.
	 * <p>
	 * If you want a configuration directory which depends on an application instance,
	 * see {@link #getKernelDirectory()}. 
	 * 
	 * @return the root directory of janus, never <code>null</code>.
	 * @see #getKernelDirectory()
	 * @since 0.4
	 */
	public File getApplicationDirectory() {
		return FileSystem.join(
				getRootConfigurationDirectory(),
				getProperty(JanusProperty.JANUS_APPLICATION_NAME));
	}

	/** Replies the home directory of the current Janus application instance.
	 * <p>
	 * <b>IMPORTANT: </b> the replied directory is automatically deleted when the JRE is exiting.
	 * <p>
	 * The file returned by this function depends on your current
	 * operating system.
	 * On Unix operating systems, the directory is 
	 * {@code $HOME/.config/janus/NAME/ID}. On Windows&reg; operating systems, the
	 * directory is
	 * {@code C:<span>\</span>Documents and Settings<span>\</span>USERNAME<span>\</span>Local Settings<span>\</span>Application Data<span>\</span>janus<span>\</span>NAME<span>\</span>ID}.
	 * {@code USERNAME} is the login of the current user, {@code NAME} is the name of the
	 * application replied by {@link JanusProperty#JANUS_APPLICATION_NAME}, and {@code ID} is the
	 * identifier of the current execution context replied by {@link #getContextId()}. 
	 * <p>
	 * If you want a configuration directory which is independent on an application instance,
	 * see {@link #getApplicationDirectory()}. 
	 * 
	 * @return the home directory of janus, never <code>null</code>.
	 * @see #getApplicationDirectory() 
	 * @since 0.4
	 */
	public File getKernelDirectory() {
		File dir = FileSystem.join(getApplicationDirectory(), getContextId().toString());
		if (dir!=null) dir.deleteOnExit();
		return dir;
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class UnprotectedPropertyName {

		private final JanusProperty janusProperty;
		private final boolean forceSetting;

		/**
		 * @param janusProperty
		 * @param forceSettings
		 */
		public UnprotectedPropertyName(JanusProperty janusProperty, boolean forceSettings) {
			this.janusProperty = janusProperty;
			this.forceSetting = forceSettings;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.janusProperty.getPropertyName();
		}

		/**
		 * Replies the janus property associated to this object.
		 * 
		 * @return the janus property associated to this object.
		 */
		public JanusProperty getJanusProperty() {
			return this.janusProperty;
		}
		
		/** Replies if the value of the associated property
		 * must be forced.
		 * 
		 * @return <code>true</code> if the value should be forced, otherwise <code>false</code>.
		 */
		public boolean isForced() {
			return this.forceSetting;
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class KeySetIterator implements Iterator<Object> {
		
		private Object replied = null;
		private final Iterator<Object> original;
		
		public KeySetIterator(Iterator<Object> original) {
			this.original = original;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.original.hasNext();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object next() {
			this.replied = this.original.next();
			return this.replied;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			if (this.replied==null)
				throw new NoSuchElementException();
			if (!JanusProperty.isReadOnlyPropertyName(this.replied.toString()))
				this.original.remove();
			this.replied = null;
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class KeySet extends AbstractSet<Object> {
		
		private final Set<Object> original;
		
		/**
		 * @param original
		 */
		public KeySet(Set<Object> original) {
			this.original = original;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Object> iterator() {
			return new KeySetIterator(this.original.iterator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return JanusProperties.this.size();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(Object o) {
			return JanusProperties.this.containsKey(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean remove(Object o) {
			return JanusProperties.this.remove(o) != null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			JanusProperties.this.clear();
		}
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class EntrySetIterator implements Iterator<Entry<Object,Object>> {
		
		private Entry<Object,Object> replied = null;
		private final Iterator<Entry<Object,Object>> original;
		
		public EntrySetIterator(Iterator<Entry<Object,Object>> original) {
			this.original = original;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.original.hasNext();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Entry<Object,Object> next() {
			this.replied = this.original.next();
			return this.replied;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			if (this.replied==null)
				throw new NoSuchElementException();
			if (!JanusProperty.isReadOnlyPropertyName(this.replied.getKey().toString()))
				this.original.remove();
			this.replied = null;
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class EntrySet extends AbstractSet<Entry<Object,Object>> {
		
		private final Set<Entry<Object,Object>> original;
		
		/**
		 * @param original
		 */
		public EntrySet(Set<Entry<Object,Object>> original) {
			this.original = original;
		}
		
		public Set<Entry<Object,Object>> getOriginalSet() {
			return this.original;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Entry<Object,Object>> iterator() {
			return new EntrySetIterator(this.original.iterator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return JanusProperties.this.size();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(Object o) {
			if (o instanceof Entry<?,?>) {
				return JanusProperties.this.containsKey(((Entry<?,?>)o).getKey());
			}
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean remove(Object o) {
			if (o instanceof Entry<?,?>) {
				return JanusProperties.this.remove(((Entry<?,?>)o).getKey())!=null;
			}
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			JanusProperties.this.clear();
		}
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class ValueCollectionIterator implements Iterator<Object> {

		private Entry<Object,Object> replied = null;
		private final Iterator<Entry<Object,Object>> original;
		
		/**
		 * @param original
		 */
		public ValueCollectionIterator(Iterator<Entry<Object,Object>> original) {
			this.original = original;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.original.hasNext();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object next() {
			this.replied = this.original.next();
			return this.replied;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			if (this.replied==null)
				throw new NoSuchElementException();
			if (!JanusProperty.isReadOnlyPropertyName(this.replied.getKey().toString()))
				this.original.remove();
			this.replied = null;
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class ValueCollection extends AbstractCollection<Object> {
		
		private final Set<Entry<Object,Object>> original;
		
		/**
		 * @param original
		 */
		public ValueCollection(Set<Entry<Object,Object>> original) {
			this.original = original;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Object> iterator() {
			return new ValueCollectionIterator(this.original.iterator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return JanusProperties.this.size();
		}
		
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
	public interface PrivilegedContext extends EventListener {
		
		/** Invoked to provide a privileged Janus property setter.
		 * 
		 * @param jps is the privileged service.
		 */
		public void setPrivilegedJanusPropertySetter(PrivilegedJanusPropertySetter jps);

		/** Replies the privileged Janus property setter.
		 * 
		 * @return the privileged service.
		 * @since 0.4
		 */
		public PrivilegedJanusPropertySetter getPrivilegedJanusPropertySetter();

	}

	/**
	 * This class provides privilegied access to Janus properties. 
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class PrivilegedJanusPropertySetterImp implements PrivilegedJanusPropertySetter {

		/**
		 */
		public PrivilegedJanusPropertySetterImp() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setPrivilegedProperty(JanusProperty property, String value) {
			JanusProperties.this.put(
					new UnprotectedPropertyName(property, true),
					value);
		}

	}

}
