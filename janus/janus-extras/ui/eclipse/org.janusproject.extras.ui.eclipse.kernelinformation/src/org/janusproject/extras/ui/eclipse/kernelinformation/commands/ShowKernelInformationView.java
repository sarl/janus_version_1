package org.janusproject.extras.ui.eclipse.kernelinformation.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.janusproject.extras.ui.eclipse.kernelinformation.views.KernelInformationView;

public class ShowKernelInformationView extends AbstractHandler implements
		IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(KernelInformationView.ID);
		} catch (PartInitException e) {
			throw new ExecutionException("Unable to execute command.",e);
		}
		return null;
	}

}
