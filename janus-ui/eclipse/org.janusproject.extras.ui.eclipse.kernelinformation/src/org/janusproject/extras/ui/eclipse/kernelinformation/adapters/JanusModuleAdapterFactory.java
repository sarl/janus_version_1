/* 
 * $Id$
 * 
 * Copyright (c) 2004-10, Janus Core Developers <Sebastian RODRIGUEZ, Nicolas GAUD, Stephane GALLAND>
 * All rights reserved.
 *
 * http://www.janus-project.org
 */
package org.janusproject.extras.ui.eclipse.kernelinformation.adapters;

import javax.print.attribute.standard.Severity;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.views.properties.IPropertySource;
import org.janusproject.extras.ui.eclipse.kernelinformation.Activator;
import org.janusproject.kernel.address.AgentAddress;
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
public class JanusModuleAdapterFactory implements IAdapterFactory {



	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adaptableObject instanceof ServiceReference){
			Object o = getBundleContext().getService((ServiceReference) adaptableObject);
			if(IPropertySource.class.equals(adapterType) && o instanceof JanusModule){
				return new JanusModulePropertySourceAdapter((ServiceReference) adaptableObject);
			}	
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@Override
	public Class[] getAdapterList() {
		// TODO Auto-generated method stub
		return null;
	}
	private BundleContext getBundleContext() {
		return Platform.getBundle(Activator.PLUGIN_ID).getBundleContext();
	}
}
