package org.janusproject.extras.ui.eclipse.kernelinformation.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.janusproject.extras.ui.eclipse.base.images.JanusSharedImages;
import org.janusproject.extras.ui.eclipse.kernelinformation.Activator;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.KernelEvent;
import org.janusproject.kernel.KernelListener;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.KernelAgent;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.channels.ChannelInteractableListener;
import org.janusproject.kernel.configuration.JanusProperties;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.mmf.JanusModule;
import org.janusproject.kernel.mmf.KernelService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Sebastian RODRIGUEZ &lt;sebastian@sebastianrodriguez.com.ar&gt;
 * @version $FullVersion$
 * @mavengroupid org.janus-project.kernel
 * @mavenartifactid org.janusproject.extras.ui.eclipse.kernelinformation
 * 
 */
public class KernelInformationView extends ViewPart implements
		ChannelInteractableListener, KernelListener,
		ITabbedPropertySheetPageContributor {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.janusproject.extras.ui.eclipse.kernelinformation.views.KernelInformationView";

	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	private Kernel janusKernel = null;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	class TreeObject implements IAdaptable {
		private String name;
		private TreeParent parent;
		private String imageKey;

		/**
		 * @return the imageKey
		 */
		public String getImageKey() {
			return imageKey;
		}

		public TreeObject(String name, String imageKey) {
			this.name = name;
			this.imageKey = imageKey;
		}

		public String getName() {
			return name;
		}

		public void setParent(TreeParent parent) {
			this.parent = parent;
		}

		public TreeParent getParent() {
			return parent;
		}

		public String toString() {
			return getName();
		}

		public Object getAdapter(Class key) {
			return null;
		}
	}

	class TreeParent extends TreeObject {
		private ArrayList children;

		public TreeParent(String name, String imageKey) {
			super(name, imageKey);
			// this.imageKey = imagekey;
			children = new ArrayList();
		}

		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}

		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}

		public Object[] getChildren() {
			return (TreeObject[]) children.toArray(new TreeObject[children
					.size()]);
		}

		public boolean hasChildren() {
			return getChildren().length > 0;
		}
	}

	class SystemPropertiesTreeObject extends TreeParent {
		public SystemPropertiesTreeObject() {
			super("System Properties", JanusSharedImages.LIBRARY);
		}

		@Override
		public TreeObject[] getChildren() {
			JanusProperty[] ps = JanusProperty.values();
			JanusProperties props = getKernelService().getCRIOContext()
					.getProperties();
			List<TreeObject> l = new ArrayList<KernelInformationView.TreeObject>();
			for (int i = 0; i < ps.length; i++) {
				l.add(new TreeObject(ps[i].getPropertyName() + " = "
						+ props.get(ps[i]), null));
			}
			return (TreeObject[]) l.toArray(new TreeObject[] {});
		}
	}

	class AgentsTreeObject extends TreeParent {

		public AgentsTreeObject() {
			super("Agents", JanusSharedImages.AGENTS);

		}

		@Override
		public Object[] getChildren() {
			if (janusKernel == null) {
				return new Object[0];
			}
			//TODO change it by introducing a new attribute containing the collection update with a kernellistener
			Iterator<AgentAddress> agentIter = janusKernel.getAgents();
			Collection<AgentAddress> agents = new HashSet<AgentAddress>();
			while(agentIter.hasNext()) {
				agents.add(agentIter.next());
			}
			
			return agents.toArray();
		}

	}

	class ModulesTreeObject extends TreeParent {
		public ModulesTreeObject() {
			super("Modules", JanusSharedImages.MODULES);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.janusproject.extras.ui.eclipse.kernelinformation.views.
		 * KernelInformationView.TreeParent#getChildren()
		 */
		@Override
		public Object[] getChildren() {
			ServiceReference[] r;
			try {
				r = getBundleContext().getServiceReferences(
						JanusModule.class.getName(), null);
				if (r != null) {
					return r;
				}

			} catch (InvalidSyntaxException e) {
				
			}
			return new Object[0];
		}
	}

	class ViewContentProvider implements IStructuredContentProvider,
			ITreeContentProvider {
		private TreeParent invisibleRoot;

		// KernelInformationChannel channel = null;

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null
					&& newInput instanceof Kernel) {
				janusKernel = (Kernel) newInput;
			}
			v.refresh();
		}

		public void dispose() {
			janusKernel = null;
		}

		public Object[] getElements(Object parent) {
			if (parent instanceof Kernel) {
				if (invisibleRoot == null)
					initialize();
				return getChildren(invisibleRoot);
			}
			if (parent.equals(getViewSite())) {
				if (invisibleRoot == null)
					initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject) child).getParent();
			}
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).getChildren();
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent) parent).hasChildren();
			return false;
		}

		/*
		 * We will set up a dummy model to initialize tree heararchy. In a real
		 * code, you will connect to a real model and expose its hierarchy.
		 */
		private void initialize() {

			TreeParent groups = new TreeParent("Groups",
					JanusSharedImages.GROUPS);

			invisibleRoot = new TreeParent("", null);
			invisibleRoot.addChild(new SystemPropertiesTreeObject());
			invisibleRoot.addChild(new AgentsTreeObject());
			invisibleRoot.addChild(new ModulesTreeObject());
			invisibleRoot.addChild(groups);
		}
	}

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			if (obj instanceof ServiceReference) {
				ServiceReference r = (ServiceReference) obj;
				JanusModule m = (JanusModule) getBundleContext().getService(r);
				return m.getName() + " (Provider = "
						+ r.getBundle().getSymbolicName() + ")";
			}
			return obj.toString();
		}

		public Image getImage(Object obj) {
			if (obj instanceof ServiceReference) {
				ServiceReference r = (ServiceReference) obj;
				JanusModule m = (JanusModule) getBundleContext().getService(r);
				if(m.isRunning()){
					return JanusSharedImages.getImage(JanusSharedImages.MODULE_RUNNING);
				}
				return JanusSharedImages.getImage(JanusSharedImages.MODULE);
			}
			if (obj instanceof AgentAddress) {
				return JanusSharedImages.getImage(JanusSharedImages.AGENT);
			}
			TreeObject to = (TreeObject) obj;
			if (to.getImageKey() == null) {
				return null;
			}
			return JanusSharedImages.getImage(to.getImageKey());

		}
	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public KernelInformationView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.setHelp(viewer.getControl(),
						"org.janusproject.extras.ui.eclipse.kernelinformation.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		getBundleContext().registerService(
				ChannelInteractableListener.class.getName(), this, null);

		getBundleContext().registerService(KernelListener.class.getName(),
				this, null);
		getSite().setSelectionProvider(viewer);

	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				KernelInformationView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Janus Kernel Information View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private BundleContext getBundleContext() {
		return Platform.getBundle(Activator.PLUGIN_ID).getBundleContext();
	}

	private KernelService getKernelService() {
		BundleContext context = getBundleContext();
		ServiceReference r = context.getServiceReference(KernelService.class
				.getName());
		if (r == null) {
			return null;
		}

		return (KernelService) context.getService(r);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.janusproject.kernel.channels.ChannelInteractableListener#
	 * channelIteractableKilled
	 * (org.janusproject.kernel.channels.ChannelInteractable)
	 */
	@Override
	public void channelIteractableKilled(ChannelInteractable agent) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.janusproject.kernel.channels.ChannelInteractableListener#
	 * channelIteractableLaunched
	 * (org.janusproject.kernel.channels.ChannelInteractable)
	 */
	@Override
	public void channelIteractableLaunched(ChannelInteractable agent) {
		
		if (agent instanceof KernelAgent && janusKernel == null) {			
			janusKernel = ((KernelAgent)agent).toKernel();
			viewer.setInput(janusKernel);
		}
		
		/*if (agent.getSupportedChannels().contains(
				KernelInformationChannel.class)
				&& janusKernel == null) {
			janusKernel = agent.getChannel(KernelInformationChannel.class);
			viewer.setInput(janusKernel);
		}*/

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.janusproject.kernel.agent.KernelListener#agentKilled(org.janusproject
	 * .kernel.agent.KernelEvent)
	 */
	@Override
	public void agentKilled(KernelEvent arg0) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				viewer.refresh();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.janusproject.kernel.agent.KernelListener#agentLaunched(org.janusproject
	 * .kernel.agent.KernelEvent)
	 */
	@Override
	public void agentLaunched(KernelEvent arg0) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				viewer.refresh();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.janusproject.kernel.agent.KernelListener#exceptionUncatched(java.
	 * lang.Throwable)
	 */
	@Override
	public boolean exceptionUncatched(Throwable arg0) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.janusproject.kernel.agent.KernelListener#kernelAgentKilled(org.
	 * janusproject.kernel.agent.KernelEvent)
	 */
	@Override
	public void kernelAgentKilled(KernelEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.janusproject.kernel.agent.KernelListener#kernelAgentLaunched(org.
	 * janusproject.kernel.agent.KernelEvent)
	 */
	@Override
	public void kernelAgentLaunched(KernelEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			return new TabbedPropertySheetPage(this);
		}
		return super.getAdapter(adapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor
	 * #getContributorId()
	 */
	public String getContributorId() {
		return getSite().getId();
	}
}