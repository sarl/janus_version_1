/* 
 * $Id$
 * 
 * Copyright (c) 2004-10, Janus Core Developers <Sebastian RODRIGUEZ, Nicolas GAUD, Stephane GALLAND>
 * All rights reserved.
 *
 * http://www.janus-project.org
 */
package org.janusproject.extras.ui.eclipse.kernelinformation.adapters;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.janusproject.extras.ui.eclipse.kernelinformation.Activator;
import org.janusproject.kernel.mmf.JanusModule;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Sebastian RODRIGUEZ &lt;sebastian@sebastianrodriguez.com.ar&gt;
 * @version $FullVersion$
 * @mavengroupid org.janus-project.kernel
 * @mavenartifactid org.janusproject.extras.ui.eclipse.kernelinformation
 * 
 */
public class JanusModulePropertySourceAdapter implements IPropertySource {

	private static final String PROPERTY_BUNDLE_NAME = "bundle.name";
	private static final String PROPERTY_BUNDLE_VERSION = "bundle.version";
	private static final String PROPERTY_MODULE_CLASS = "module.class";
	private static final String PROPERTY_MODULE_RUNNING = "module.running";
	private static final String CAT_BUNDLE = "Provider Bundle";
	private static final String CAT_MODULE = "Module";
	
	private ServiceReference reference = null;
	private JanusModule module = null;

	private IPropertyDescriptor[] propertyDescriptors;

	/**
	 * @param adaptableObject
	 */
	public JanusModulePropertySourceAdapter(ServiceReference adaptableObject) {
		this.reference = adaptableObject;
		Object o = getBundleContext().getService(reference);
		assert o instanceof JanusModule;
		module = (JanusModule) o;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	@Override
	public Object getEditableValue() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (propertyDescriptors == null) {
			PropertyDescriptor bn = new PropertyDescriptor(
					PROPERTY_BUNDLE_NAME, "Symbolic Name");
			bn.setCategory(CAT_BUNDLE);

			PropertyDescriptor bv = new PropertyDescriptor(
					PROPERTY_BUNDLE_VERSION, "Version");
			bv.setCategory(CAT_BUNDLE);

			PropertyDescriptor mc = new PropertyDescriptor(
					PROPERTY_MODULE_CLASS, "Module Class");
			mc.setCategory(CAT_MODULE);
			
			PropertyDescriptor mr = new PropertyDescriptor(
					PROPERTY_MODULE_RUNNING, "Running");
			mr.setCategory(CAT_MODULE);

			propertyDescriptors = new IPropertyDescriptor[] { bn, bv, mc, mr };

		}
		return propertyDescriptors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java
	 * .lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PROPERTY_BUNDLE_NAME.equals(id)) {
			return reference.getBundle().getSymbolicName();
		}
		if (PROPERTY_BUNDLE_VERSION.equals(id)) {
			return reference.getBundle().getVersion();
		}
		if (PROPERTY_MODULE_CLASS.equals(id)) {
			return module.getClass();
		}
		if (PROPERTY_MODULE_RUNNING.equals(id)) {
			return module.isRunning();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang
	 * .Object)
	 */
	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java
	 * .lang.Object)
	 */
	@Override
	public void resetPropertyValue(Object id) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java
	 * .lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
	}

	private BundleContext getBundleContext() {
		return Platform.getBundle(Activator.PLUGIN_ID).getBundleContext();
	}
}
