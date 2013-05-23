/* 
 * $Id$
 * 
 * Copyright (c) 2004-10, Janus Core Developers <Sebastian RODRIGUEZ, Nicolas GAUD, Stephane GALLAND>
 * All rights reserved.
 *
 * http://www.janus-project.org
 */
package org.janusproject.extras.ui.eclipse.kernelinformation.adapters;

import java.awt.Point;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.janusproject.extras.ui.eclipse.kernelinformation.Activator;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.mmf.KernelService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Sebastian RODRIGUEZ &lt;sebastian@sebastianrodriguez.com.ar&gt;
 * @version $FullVersion$
 * @mavengroupid org.janus-project.kernel
 * @mavenartifactid org.janusproject.extras.ui.eclipse.kernelinformation
 * 
 */
public class AgentAddressPropertySourceAdapter implements IPropertySource {

	private static final String PROPERTY_UUID = "org.janusproject.agentaddress.uuid";
	private static final String PROPERTY_NAME = "org.janusproject.agentaddress.name";
	private static final String PROPERTY_CHANNELINTERACTABLE = "org.janusproject.agentaddress.supportschannels";
	private static final String PROPERTY_CHANNELS = "org.janusproject.agentaddress.channels";

	private static final String CAT_ADDR = "Address";
	private static final String CAT_CHANNELS = "Channels";

	private AgentAddress address = null;
	//private ChannelInteractable channelInteractable = null;

	private IPropertyDescriptor[] propertyDescriptors;

	public AgentAddressPropertySourceAdapter(AgentAddress address) {
		this.address = address;
		//KernelService s = getKernelService();
		//channelInteractable = s.getChannelInteractable(address);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	@Override
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (propertyDescriptors == null) {
			// Create a descriptor and set a category
			PropertyDescriptor uuidDescriptor = new PropertyDescriptor(PROPERTY_UUID, "Address");

			uuidDescriptor.setCategory(CAT_ADDR);

			PropertyDescriptor nameDescriptor = new PropertyDescriptor(PROPERTY_NAME, "Name");

			nameDescriptor.setCategory(CAT_ADDR);

			PropertyDescriptor isIntDescriptor = new PropertyDescriptor(PROPERTY_CHANNELINTERACTABLE, "Supports Channels");

			isIntDescriptor.setCategory(CAT_CHANNELS);

			PropertyDescriptor chDescriptor = new PropertyDescriptor(PROPERTY_CHANNELS, "Supported Channels");

			chDescriptor.setCategory(CAT_CHANNELS);

			propertyDescriptors = new IPropertyDescriptor[] { nameDescriptor, uuidDescriptor, isIntDescriptor, chDescriptor };
		}
		return propertyDescriptors;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java .lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
	//FIXME update this methods to remove all ref to channel
		if (PROPERTY_UUID.equals(id)) {
			return this.address.getUUID();
		} else if (PROPERTY_NAME.equals(id)) {
			return this.address.getName();
		 /*else if (PROPERTY_CHANNELINTERACTABLE.equals(id)) {
			return this.channelInteractable != null;*/
		} else if (PROPERTY_CHANNELS.equals(id)) {
			/*if (this.channelInteractable != null) {
				return this.channelInteractable.getSupportedChannels().toString();
			}*/
			return "None";
		}
		return "Retorno para " + id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang .Object)
	 */
	@Override
	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java .lang.Object)
	 */
	@Override
	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java .lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {

	}

	private KernelService getKernelService() {
		BundleContext context = Platform.getBundle(Activator.PLUGIN_ID).getBundleContext();
		ServiceReference r = context.getServiceReference(KernelService.class.getName());
		if (r != null) {
			return (KernelService) context.getService(r);
		}
		return null;
	}
}
