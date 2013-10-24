/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010, 2012 Janus Core Developers
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
package org.janusproject.demos.simulation.foragerbots.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.foragerbots.agents.GridListener;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.KernelEvent;
import org.janusproject.kernel.KernelListener;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.AgentProbe;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.agent.ProbeManager;

/**
 * Graphic User Interface for the forager bot demo.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class EnvironmentFrame
extends JFrame {
	
	private static final long serialVersionUID = -8971365701634319276L;
	
	private static final int DIALOG_WIDTH = 800;
	private static final int DIALOG_HEIGHT = 600;
	private static final int RIGHT_PANEL_WIDTH = 200;
	private static final int BOTTOM_PANEL_HEIGHT = 200;
	
	private final EventHandler eventHandler = new EventHandler();
	private final EnvironmentPanel panel;
	
	private final DefaultListModel agentList = new DefaultListModel();
	private final JList agentListObj = new JList(this.agentList);
	
	private AgentProbe currentProbe = null;
	private final DefaultListModel probeList = new DefaultListModel();
	
	/**
	 * @param kernel is the current Janus Kernel.
	 */
	public EnvironmentFrame(Kernel kernel) {
		assert(kernel!=null);
		setTitle(Locale.getString(EnvironmentFrame.class, "TITLE")); //$NON-NLS-1$
		getContentPane().setLayout(new BorderLayout());

		addWindowListener(new Closer());
		
		this.panel = new EnvironmentPanel();
		
		JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		JScrollPane gridScrollPane = new JScrollPane(this.panel);
		gridScrollPane.setMinimumSize(new Dimension(DIALOG_WIDTH-RIGHT_PANEL_WIDTH, DIALOG_HEIGHT-BOTTOM_PANEL_HEIGHT));

		this.agentListObj.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.agentListObj.getSelectionModel().addListSelectionListener(this.eventHandler);
		JScrollPane agentListScrollPane = new JScrollPane(this.agentListObj);
		
		JList probeTable = new JList(this.probeList);
		probeTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane probeScrollPane = new JScrollPane(probeTable);

		horizontalSplitPane.setTopComponent(gridScrollPane);
		horizontalSplitPane.setBottomComponent(probeScrollPane);
		verticalSplitPane.setLeftComponent(horizontalSplitPane);
		verticalSplitPane.setRightComponent(agentListScrollPane);
		getContentPane().add(verticalSplitPane, BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
		
		pack();

		kernel.addKernelListener(this.eventHandler);
	}
	
	/** Select the agent with the given address and put a probe on it.
	 */
	void selectAgent() {
		int sel = this.agentListObj.getSelectedIndex();
		if (sel>=0 && sel<this.agentList.getSize()) {
			AgentAddress adr = (AgentAddress)this.agentList.get(sel);
			assert(adr!=null);
			Kernel janusKernel = Kernels.get();
			assert(janusKernel!=null);
			ProbeManager probeManager = janusKernel.getProbeManager();
			assert(probeManager!=null);
			if (this.currentProbe!=null) probeManager.release(this.currentProbe);
			this.currentProbe = probeManager.createProbe(AgentProbe.class, adr);
			refreshProbe();
		}
	}
	
	/** Refresh the probes.
	 */
	protected synchronized void refreshProbe() {
		this.probeList.removeAllElements();
		if (this.currentProbe!=null) {
			Set<String> names = this.currentProbe.getProbedValueNames();
			Object v;
			String label;
			this.probeList.addElement(Locale.getString(EnvironmentFrame.class, "PROBE_LABEL_ENTITY", this.currentProbe.getWatchedObject())); //$NON-NLS-1$);
			for(String name : names) {
				v = this.currentProbe.getProbeValue(name, Object.class);
				if (v==null) {
					label = Locale.getString(EnvironmentFrame.class, "PROBE_LABEL_NULL", name); //$NON-NLS-1$
				}
				else {
					label = Locale.getString(EnvironmentFrame.class, "PROBE_LABEL", name, v); //$NON-NLS-1$
				}
				this.probeList.addElement(label);
			}
		}
	}
	
	/** Replies the grid listener.
	 * 
	 * @return the grid listener.
	 */
	public GridListener getGridListener() {
		return this.panel;
	}
	
	/** Add agent address in the list.
	 * 
	 * @param adr
	 */
	protected synchronized void addAgent(AgentAddress adr) {
		this.agentList.addElement(adr);
	}

	/** Remove agent address from the list.
	 * 
	 * @param adr
	 */
	protected synchronized void removeAgent(AgentAddress adr) {
		this.agentList.removeElement(adr);
		refreshProbe();
	}

	/** Remove all agent addresses from the list.
	 */
	protected synchronized void removeAllAgents() {
		this.agentList.removeAllElements();
		refreshProbe();
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class EventHandler implements KernelListener, ListSelectionListener {

		/**
		 */
		public EventHandler() {
			//
		}
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void agentKilled(KernelEvent event) {
			removeAgent(event.getAgent());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void agentLaunched(KernelEvent event) {
			addAgent(event.getAgent());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean exceptionUncatched(Throwable error) {
			return false; // The exception is not treated by this listener. Assumes that another listener will catch this exception.
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void kernelAgentKilled(KernelEvent event) {
			removeAllAgents();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void kernelAgentLaunched(KernelEvent event) {
			//
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void valueChanged(ListSelectionEvent e) {
			// Selection of agent changed
			if (!e.getValueIsAdjusting()) {
				selectAgent();
			}
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Closer extends WindowAdapter {
		/**
		 */
		public Closer() {
			//
		}
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void windowClosing(WindowEvent event) {
			Kernels.killAll();
			System.exit(0);
		}
	}
		
}
