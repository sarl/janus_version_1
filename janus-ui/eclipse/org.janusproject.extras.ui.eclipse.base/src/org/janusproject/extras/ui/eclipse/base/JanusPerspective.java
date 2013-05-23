package org.janusproject.extras.ui.eclipse.base;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.janusproject.extras.ui.eclipse.base.utils.SystemConsole;

public class JanusPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		layout.addView(IPageLayout.ID_PROP_SHEET, IPageLayout.RIGHT, 0.5f, layout.getEditorArea());
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(
			      new IConsole[] { SystemConsole.getSystemConsole()});

		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.BOTTOM, 0.5f, layout.getEditorArea() );
	}

}
