/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.demos.ecoresolution.ecocube.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.arakhne.afc.vmutil.Resources;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.ecoresolution.ecocube.agent.AgentType;
import org.janusproject.demos.ecoresolution.ecocube.agent.CubeEcoChannel;
import org.janusproject.demos.ecoresolution.ecocube.relation.DownwardRelation;
import org.janusproject.ecoresolution.agent.EcoChannel;
import org.janusproject.ecoresolution.agent.EcoChannelListener;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.identity.EcoIdentityComparator;
import org.janusproject.ecoresolution.relation.EcoRelation;
import org.janusproject.ecoresolution.sm.EcoState;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.channels.ChannelInteractableListener;

/** Panel to display the cube world.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class CubeWorldPanel extends JPanel implements ChannelInteractableListener, EcoChannelListener, ActionListener {

	private static final long serialVersionUID = 6237447501041820156L;

	/** Action name to show the first action state.
	 */
	private static final String ACTION_FIRST_STATE = "firstCubeWorldState"; //$NON-NLS-1$

	/** Action name to show the next action state.
	 */
	private static final String ACTION_NEXT_STATE = "nextCubeWorldState"; //$NON-NLS-1$
	
	/** Action name to show the previous action state.
	 */
	private static final String ACTION_PREVIOUS_STATE = "previousCubeWorldState"; //$NON-NLS-1$
	
	/** Action name to show the last action state.
	 */
	private static final String ACTION_LAST_STATE = "lastCubeWorldState"; //$NON-NLS-1$

	private static boolean equalsState(EcoState s1, EcoState s2) {
		if (s1.isInitializationState() && s2.isInitializationState())
			return true;
		return s1.equals(s2);
	}

	private static Icon getIcon(String name) {
		URL iconResource = Resources.getResource(CubeWorldPanel.class,name);
		if (iconResource==null) return null;
		return new ImageIcon(iconResource);
	}

	private final Displayer displayer = new Displayer();
	private final JButton startButton;
	private final JButton previousButton;
	private final JButton nextButton;
	private final JButton endButton;
	private final JLabel stateLabel;
	
	private final Collection<CubeEcoChannel> channels = new ArrayList<CubeEcoChannel>();
	private final int cubeCount;
	private final EcoIdentity planeEntity;
	private final List<State> worldStates = new ArrayList<State>();
	private int currentStateIndex = -1;

	/**
	 * @param k is the kernel to inspect.
	 * @param planeEntity is the entity which is the plane.
	 * @param cubeCount is the number of cubes in the problem.
	 */
	public CubeWorldPanel(Kernel k, EcoIdentity planeEntity, int cubeCount) {
		this.planeEntity = planeEntity;
		this.cubeCount = cubeCount;
		
		setLayout(new BorderLayout());
		add(this.displayer, BorderLayout.CENTER);
		
		JPanel tools = new JPanel();
		tools.setLayout(new BoxLayout(tools, BoxLayout.X_AXIS));
		add(tools, BorderLayout.SOUTH);
		
		this.startButton = new JButton(getIcon("first.png")); //$NON-NLS-1$
		this.startButton.setActionCommand(ACTION_FIRST_STATE);
		this.startButton.setToolTipText(Locale.getString(CubeWorldPanel.class, "FIRST_STATE")); //$NON-NLS-1$
		tools.add(this.startButton);
		this.startButton.addActionListener(this);

		this.previousButton = new JButton(getIcon("previous.png")); //$NON-NLS-1$
		this.previousButton.setActionCommand(ACTION_PREVIOUS_STATE);
		this.previousButton.setToolTipText(Locale.getString(CubeWorldPanel.class, "PREVIOUS_STATE")); //$NON-NLS-1$
		tools.add(this.previousButton);
		this.previousButton.addActionListener(this);
		
		this.nextButton = new JButton(getIcon("next.png")); //$NON-NLS-1$
		this.nextButton.setActionCommand(ACTION_NEXT_STATE);
		this.nextButton.setToolTipText(Locale.getString(CubeWorldPanel.class, "NEXT_STATE")); //$NON-NLS-1$
		tools.add(this.nextButton);
		this.nextButton.addActionListener(this);
		
		this.endButton = new JButton(getIcon("last.png")); //$NON-NLS-1$
		this.endButton.setActionCommand(ACTION_LAST_STATE);
		this.endButton.setToolTipText(Locale.getString(CubeWorldPanel.class, "LAST_STATE")); //$NON-NLS-1$
		tools.add(this.endButton);
		this.endButton.addActionListener(this);

		this.stateLabel = new JLabel();
		this.stateLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		tools.add(this.stateLabel);
		
		updateUIComponents();
		
		k.getChannelManager().addChannelInteractableListener(this);
	}
	
	private void updateUIComponents() {
		boolean prev = this.currentStateIndex>0 && this.currentStateIndex<this.worldStates.size();
		boolean next = this.currentStateIndex>=0 && this.currentStateIndex<(this.worldStates.size()-1);
		this.startButton.setEnabled(prev); 
		this.previousButton.setEnabled(prev); 
		this.nextButton.setEnabled(next);
		this.endButton.setEnabled(next);
		String label;
		if (this.currentStateIndex>=0 && this.currentStateIndex<this.worldStates.size()) {
			State state = getCurrentState();
			assert(state!=null);
			String key = (state.isInitialization) ? "INIT_INFO" : "INFO"; //$NON-NLS-1$ //$NON-NLS-2$
			label = Locale.getString(CubeWorldPanel.class, key, 
					Integer.toString(this.currentStateIndex+1),
					Integer.toString(this.worldStates.size()));
		}
		else {
			label = Locale.getString(CubeWorldPanel.class, "NO_INFO"); //$NON-NLS-1$
		}
		this.stateLabel.setText(label);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_FIRST_STATE.equals(e.getActionCommand())) {
			synchronized(getTreeLock()) {
				int oldState = this.currentStateIndex;
				this.currentStateIndex = 0;
				if (this.currentStateIndex>=this.worldStates.size()) {
					this.currentStateIndex = -1;
				}
				if (oldState!=this.currentStateIndex) {
					repaint();
					updateUIComponents();
				}
			}
		}
		else if (ACTION_PREVIOUS_STATE.equals(e.getActionCommand())) {
			synchronized(getTreeLock()) {
				int oldState = this.currentStateIndex;
				--this.currentStateIndex;
				if (this.currentStateIndex<0 || this.currentStateIndex>=this.worldStates.size()) {
					this.currentStateIndex = -1;
				}
				if (oldState!=this.currentStateIndex) {
					repaint();
					updateUIComponents();
				}
			}
		}
		else if (ACTION_NEXT_STATE.equals(e.getActionCommand())) {
			synchronized(getTreeLock()) {
				int oldState = this.currentStateIndex;
				++this.currentStateIndex;
				if (this.currentStateIndex<0 || this.currentStateIndex>=this.worldStates.size()) {
					this.currentStateIndex = -1;
				}
				if (oldState!=this.currentStateIndex) {
					repaint();
					updateUIComponents();
				}
			}
		}
		else if (ACTION_LAST_STATE.equals(e.getActionCommand())) {
			synchronized(getTreeLock()) {
				int oldState = this.currentStateIndex;
				this.currentStateIndex = this.worldStates.size() - 1;
				if (this.currentStateIndex<0 || this.currentStateIndex>=this.worldStates.size()) {
					this.currentStateIndex = -1;
				}
				if (oldState!=this.currentStateIndex) {
					repaint();
					updateUIComponents();
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void channelIteractableKilled(ChannelInteractable agent) {
		if (agent.getSupportedChannels().contains(CubeEcoChannel.class)) {
			CubeEcoChannel channel = agent.getChannel(CubeEcoChannel.class);
			if (channel!=null) {
				channel.removeEcoChannelListener(this);
				this.channels.remove(channel);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void channelIteractableLaunched(ChannelInteractable agent) {
		if (agent.getSupportedChannels().contains(EcoChannel.class)) {
			CubeEcoChannel channel = agent.getChannel(CubeEcoChannel.class);
			if (channel!=null) {
				this.channels.add(channel);
				channel.addEcoChannelListener(this);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void channelContentChanged() {
		synchronized(getTreeLock()) {
			State state = new State();
			EcoIdentity entity;
			EcoIdentity previousEcoEntity;
			
			for(CubeEcoChannel channel : this.channels) {
				if (channel.getAgentType()==AgentType.CUBE) {
					entity = channel.getEcoEntity();
					assert(entity!=null);
					state.ecoStates.put(entity, channel.getEcoState());
					for(EcoRelation relation : channel.getAcquaintances()) {
						if (relation instanceof DownwardRelation) {
							DownwardRelation dr = (DownwardRelation)relation;
							if (dr.getMaster().equals(entity)) {
								if (this.planeEntity.equals(dr.getSlave())) {
									// on ground
									if (!state.onGround.add(entity)) {
										state.isInconsistent = true;
									}
								}
								else {
									// on other
									previousEcoEntity = state.map.put(dr.getSlave(), entity);
									if (previousEcoEntity!=null && !entity.equals(previousEcoEntity)) {
										state.isInconsistent = true;
									}
								}
							}
						}
					}
					state.isInitialization = state.isInitialization || channel.getEcoState().isInitializationState();
				}
			}
	
			if (!state.onGround.isEmpty()) {
				State lastState = getLastState();
				if (lastState==null || !lastState.equals(state)) {
					this.worldStates.add(state);
					int oldIndex = this.currentStateIndex;
					if (this.currentStateIndex<0) {
						this.currentStateIndex = 0;
					}
					else if (this.currentStateIndex>=this.worldStates.size()) {
						this.currentStateIndex = this.worldStates.size() - 1;
					}
					updateUIComponents();
					if (this.currentStateIndex!=oldIndex) repaint();
				}
			}
		}
	}
	
	/** Replies the current displayed state.
	 * 
	 * @return the current displayed state.
	 */
	private synchronized State getCurrentState() {
		if (this.currentStateIndex>=0 && this.currentStateIndex<this.worldStates.size()) {
			return this.worldStates.get(this.currentStateIndex);
		}
		return null;
	}
	
	/** Replies the last state in history.
	 * 
	 * @return the last state in history.
	 */
	private synchronized State getLastState() {
		if (!this.worldStates.isEmpty()) {
			return this.worldStates.get(this.worldStates.size()-1);
		}
		return null;
	}

	/** Displayer of the cube world.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	private class Displayer extends JPanel {

		private static final long serialVersionUID = 5641156803294849884L;

		/**
		 */
		public Displayer() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public synchronized void paint(Graphics g) {

			Dimension viewport = getSize();
			
			int size = Math.min(viewport.width, viewport.height) - 30;
			
			int columnSize = Math.max((viewport.width - 10) / CubeWorldPanel.this.cubeCount, 10);
			int cubeSize = Math.max(Math.min(columnSize, size / CubeWorldPanel.this.cubeCount) - 5, 5);
			int base = viewport.height - 20;

			State currentState = getCurrentState();

			// Draw warning background
			Color bg = getBackground();
			if (currentState!=null && currentState.isInconsistent) {
				setBackground(Color.ORANGE);
			}
			super.paint(g);
			setBackground(bg);
			
			// Draw table
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, base, viewport.width, 20);
					
			// Draw cubes
			if (currentState!=null) {
				int x = 5 + (columnSize-cubeSize)/2;
				for(EcoIdentity onGround : currentState.onGround) {
					drawColumn(g, currentState, onGround, x, base, cubeSize);
					x += columnSize;
				}
			}
			
		}
		
		private void drawColumn(Graphics g, State state, EcoIdentity onGround, int x, int y, int cubeSize) {
			String label;
			int strWidth;
			EcoState ecoState;

			EcoIdentity entity = onGround;
			int yy = y - cubeSize;
			int strHeight = g.getFontMetrics().getHeight()/2;
			
			Color backColor, frontColor;
			
			while (entity!=null) {

				ecoState = state.ecoStates.get(entity);
				if (ecoState==null) {
					backColor = Color.DARK_GRAY;
					frontColor = Color.WHITE;
				}
				else {
					switch(ecoState) {
					case ESCAPING:
						backColor = Color.RED;
						frontColor = Color.BLACK;
						break;
					case ESCAPED:
						backColor = Color.PINK;
						frontColor = Color.BLACK;
						break;
					case SATISFACTING:
						backColor = Color.WHITE;
						frontColor = Color.BLACK;
						break;
					case SATISFACTED:
						backColor = Color.GREEN;
						frontColor = Color.BLACK;
						break;
					case INITIALIZED:
					case INITIALIZING:
					default:
						backColor = Color.DARK_GRAY;
						frontColor = Color.WHITE;
						break;
					}
				}
				
				g.setColor(backColor);
				g.fillRect(x, yy, cubeSize, cubeSize);
				g.setColor(frontColor);
				g.drawRect(x, yy, cubeSize, cubeSize);
				
				label = entity.toString();
				strWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), label);
				g.drawString(label, x + (cubeSize-strWidth)/2, yy + (cubeSize-strHeight)/2);

				yy -= cubeSize;
				entity = state.map.get(entity);
			}
		}

	} // class Displayer
	
	/** State of the world.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	private class State {

		/** Hierarchy of cubes.
		 */
		public final Map<EcoIdentity,EcoIdentity> map = new TreeMap<EcoIdentity, EcoIdentity>(EcoIdentityComparator.SINGLETON);

		/** Satisfaction statuses.
		 */
		public final Map<EcoIdentity,EcoState> ecoStates = new TreeMap<EcoIdentity, EcoState>(EcoIdentityComparator.SINGLETON);

		/** Cubes on ground.
		 */
		public final SortedSet<EcoIdentity> onGround = new TreeSet<EcoIdentity>(EcoIdentityComparator.SINGLETON);
		
		
		/** Indicates if this state is inconsistent.
		 */
		public boolean isInconsistent = false;
		
		/** Indicates if this state is for a initialization stage.
		 */
		public boolean isInitialization = false;
		
		/**
		 */
		public State() {
			//
		}
		
		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public boolean equals(Object o) {
			if (o instanceof State) {
				State s = (State)o;
				if (this.isInitialization!=s.isInitialization) return false;
				if (!this.onGround.equals(s.onGround)) return false;
				
				EcoState st;
				for(Entry<EcoIdentity,EcoState> entry : this.ecoStates.entrySet()) {
					st = s.ecoStates.get(entry.getKey());
					if (st==null || !equalsState(st, entry.getValue())) return false;
				}
				
				return this.map.equals(s.map);
			}
			return false;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			int h = 1;
			h = h * 31 + Boolean.valueOf(this.isInitialization).hashCode();
			h = h * 31 + this.onGround.hashCode();
			h = h * 31 + this.ecoStates.hashCode();
			h = h * 31 + this.map.hashCode();
			return h;
		}
		
	} // class State
		
}