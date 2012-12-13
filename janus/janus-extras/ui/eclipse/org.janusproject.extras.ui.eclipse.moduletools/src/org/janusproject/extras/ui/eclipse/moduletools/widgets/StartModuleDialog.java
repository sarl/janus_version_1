/* 
 * $Id$
 * 
 * Copyright (c) 2004-10, Janus Core Developers <Sebastian RODRIGUEZ, Nicolas GAUD, Stephane GALLAND>
 * All rights reserved.
 *
 * http://www.janus-project.org
 */
package org.janusproject.extras.ui.eclipse.moduletools.widgets;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.janusproject.extras.ui.eclipse.base.images.JanusSharedImages;
import org.janusproject.extras.ui.eclipse.moduletools.Activator;
import org.janusproject.kernel.mmf.JanusModule;
import org.janusproject.kernel.mmf.KernelService;
import org.janusproject.kernel.status.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Sebastian RODRIGUEZ &lt;sebastian@sebastianrodriguez.com.ar&gt;
 * @version $FullVersion$
 * @mavengroupid org.janus-project.kernel
 * @mavenartifactid org.janusproject.extras.ui.eclipse.moduletools
 * 
 */
public class StartModuleDialog extends TitleAreaDialog {
	private TableViewer viewer;
	private ServiceReference selectedReference = null;

	/**
	 * @param parentShell
	 */
	public StartModuleDialog(Shell parentShell) {
		super(parentShell);
	}

	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		// Set the title
		setTitle("Start Janus Platform Module");

		// Set the message
		if (getKernelService() == null) {
			setMessage("The KernelService is not available.", IMessageProvider.ERROR);
		} else {
			setMessage("Select the Module to start.", IMessageProvider.INFORMATION);
		}
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);

		setTitleImage(JanusSharedImages.getImage(JanusSharedImages.LOGO_JANUS_BANNER));

		return contents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse .swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		createModuleViewer(composite);
		return composite;
	}

	/**
	 * @param parent
	 */
	private void createModuleViewer(Composite parent) {
		this.viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE);
		createColumns(this.viewer);
		this.viewer.setContentProvider(new ModulesContentProvider());
		this.viewer.setLabelProvider(new ModulesLabelProvider());
		this.viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				StartModuleDialog.this.selectedReference = (ServiceReference) selection.getFirstElement();
				StartModuleDialog.this.selectedReference.getClass();
			}
		});
this.viewer.setInput("Start");
	}

	private void createColumns(TableViewer viewer) {

		String[] titles = { "Name", "Description", "ModuleClass", "Bundle", "Bundle Version", "ServiceID" };
		int[] bounds = { 200, 200, 200, 200, 50, 20 };

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
		}
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	class ModulesLabelProvider extends LabelProvider implements ITableLabelProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java .lang.Object, int)
		 */
		@Override
		public Image getColumnImage(Object obj, int idx) {
			if (idx == 0) {
				ServiceReference ref = (ServiceReference) obj;
				JanusModule m = (JanusModule) getBundleContext().getService(ref);
				if (m.isRunning()) {
					return JanusSharedImages.getImage(JanusSharedImages.MODULE_RUNNING);
				}
				return JanusSharedImages.getImage(JanusSharedImages.MODULE);
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java. lang.Object, int)
		 */
		@Override
		public String getColumnText(Object obj, int column) {
			ServiceReference ref = (ServiceReference) obj;
			JanusModule m = (JanusModule) getBundleContext().getService(ref);
			switch (column) {
			case 0:
				return m.getName();
			case 1:
				return m.getDescription();
			case 2:
				return m.getClass().getName();
			case 3:
				return ref.getBundle().getSymbolicName();
			case 4:
				return ref.getBundle().getVersion().toString();
			case 5:
				return ref.getProperty("service.id").toString();

			}
			return "Columna " + column;
		}

	}

	class ModulesContentProvider implements IStructuredContentProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements( java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object parent) {
			try {
				ServiceReference[] refs = getBundleContext().getServiceReferences(JanusModule.class.getName(), null);
				if (refs == null) {
					return new Object[0];
				}
				return refs;
			} catch (InvalidSyntaxException e) {

			}
			return new Object[0];
		}

	}

	public void run() {
		if (getKernelService() == null) {
			MessageDialog.openError(getShell(), "Unavailable Kernel", "The KernelService is not available. Please ensure Janus is running.");
			return;
		}

		int res = open();
		if (res == OK) {
			JanusModule m = getSelectedModule();
			if (m.isRunning()) {
				MessageDialog.openError(getShell(), "Module Running", "The selected Module is already running.");
				return;
			}
			Status status = getKernelService().startJanusModule(m, null);
			if (status.isFailure()) {
				MessageDialog.openError(getShell(), "Error while starting module", status.getMessage());
			}
		}
	}

	public JanusModule getSelectedModule() {
		if (selectedReference != null) {
			return (JanusModule) getBundleContext().getService(selectedReference);
		}
		return null;

	}

	private BundleContext getBundleContext() {
		return Platform.getBundle(Activator.PLUGIN_ID).getBundleContext();
	}

	private KernelService getKernelService() {
		BundleContext context = getBundleContext();
		ServiceReference r = context.getServiceReference(KernelService.class.getName());
		if (r == null) {
			return null;
		}

		return (KernelService) context.getService(r);
	}

}
