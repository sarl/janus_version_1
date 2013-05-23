package org.janusproject.extras.ui.eclipse.moduletools.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;
import org.janusproject.extras.ui.eclipse.moduletools.widgets.StartModuleDialog;

public class StartModuleHandler extends AbstractHandler implements IHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		new StartModuleDialog(HandlerUtil.getActiveShell(event)).run();
		return null;
	}



}
