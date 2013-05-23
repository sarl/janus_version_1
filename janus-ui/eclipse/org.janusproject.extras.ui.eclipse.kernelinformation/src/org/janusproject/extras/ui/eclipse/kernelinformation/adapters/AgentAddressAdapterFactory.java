package org.janusproject.extras.ui.eclipse.kernelinformation.adapters;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;
import org.janusproject.kernel.address.AgentAddress;

public class AgentAddressAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(IPropertySource.class.equals(adapterType)){
			return new AgentAddressPropertySourceAdapter((AgentAddress) adaptableObject);
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		// TODO Auto-generated method stub
		return new Class[]{IPropertySource.class};
	}

}
