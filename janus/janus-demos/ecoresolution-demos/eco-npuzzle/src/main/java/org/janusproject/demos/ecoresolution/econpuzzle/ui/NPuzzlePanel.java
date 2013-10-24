/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-12 Janus Core Developers
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
package org.janusproject.demos.ecoresolution.econpuzzle.ui;

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
import java.util.TreeMap;

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
import org.janusproject.demos.ecoresolution.econpuzzle.agent.channel.EcoPlaceNPuzzleChannel;
import org.janusproject.demos.ecoresolution.econpuzzle.agent.channel.NPuzzleChannel;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Hosting;
import org.janusproject.ecoresolution.agent.EcoChannel;
import org.janusproject.ecoresolution.agent.EcoChannelListener;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.identity.EcoIdentityComparator;
import org.janusproject.ecoresolution.relation.EcoRelation;
import org.janusproject.ecoresolution.sm.EcoState;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.channels.ChannelInteractableListener;

/**
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class NPuzzlePanel extends JPanel implements ChannelInteractableListener, EcoChannelListener, ActionListener {

	private static final long serialVersionUID = 8195190416784847807L;

	/**
	 * Action name to show the first action state.
	 */
	private static final String ACTION_FIRST_STATE = "firstNPuzzleState"; //$NON-NLS-1$

	/**
	 * Action name to show the next action state.
	 */
	private static final String ACTION_NEXT_STATE = "nextNPuzzleState"; //$NON-NLS-1$

	/**
	 * Action name to show the previous action state.
	 */
	private static final String ACTION_PREVIOUS_STATE = "previousNPuzzleState"; //$NON-NLS-1$

	/**
	 * Action name to show the last action state.
	 */
	private static final String ACTION_LAST_STATE = "lastNPuzzleState"; //$NON-NLS-1$

	private static Icon getIcon(String name) {
		URL iconResource = Resources.getResource(NPuzzlePanel.class, name);
		if (iconResource == null)
			return null;
		return new ImageIcon(iconResource);
	}

	private static boolean equalsState(EcoState s1, EcoState s2) {
		if (s1.isInitializationState() && s2.isInitializationState())
			return true;
		return s1.equals(s2);
	}

	private final Collection<NPuzzleChannel> channels = new ArrayList<NPuzzleChannel>();

	private final int gridSize;
	private final List<NPuzzleState> worldStates = new ArrayList<NPuzzleState>();
	private int currentStateIndex = -1;

	private final NPuzzleDisplayer displayer = new NPuzzleDisplayer();
	private final JButton startButton;
	private final JButton previousButton;
	private final JButton nextButton;
	private final JButton endButton;
	private final JLabel stateLabel;

	/**
	 * @param k
	 * @param gridSize
	 */
	public NPuzzlePanel(Kernel k, int gridSize) {
		this.gridSize = gridSize;

		setLayout(new BorderLayout());
		add(this.displayer, BorderLayout.CENTER);

		JPanel tools = new JPanel();
		tools.setLayout(new BoxLayout(tools, BoxLayout.X_AXIS));
		add(tools, BorderLayout.SOUTH);

		this.startButton = new JButton(getIcon("first.png")); //$NON-NLS-1$
		this.startButton.setActionCommand(ACTION_FIRST_STATE);
		this.startButton.setToolTipText(Locale.getString(NPuzzlePanel.class, "FIRST_STATE")); //$NON-NLS-1$
		tools.add(this.startButton);
		this.startButton.addActionListener(this);

		this.previousButton = new JButton(getIcon("previous.png")); //$NON-NLS-1$
		this.previousButton.setActionCommand(ACTION_PREVIOUS_STATE);
		this.previousButton.setToolTipText(Locale.getString(NPuzzlePanel.class, "PREVIOUS_STATE")); //$NON-NLS-1$
		tools.add(this.previousButton);
		this.previousButton.addActionListener(this);

		this.nextButton = new JButton(getIcon("next.png")); //$NON-NLS-1$
		this.nextButton.setActionCommand(ACTION_NEXT_STATE);
		this.nextButton.setToolTipText(Locale.getString(NPuzzlePanel.class, "NEXT_STATE")); //$NON-NLS-1$
		tools.add(this.nextButton);
		this.nextButton.addActionListener(this);

		this.endButton = new JButton(getIcon("last.png")); //$NON-NLS-1$
		this.endButton.setActionCommand(ACTION_LAST_STATE);
		this.endButton.setToolTipText(Locale.getString(NPuzzlePanel.class, "LAST_STATE")); //$NON-NLS-1$
		tools.add(this.endButton);
		this.endButton.addActionListener(this);

		this.stateLabel = new JLabel();
		this.stateLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		tools.add(this.stateLabel);

		updateUIComponents();

		k.getChannelManager().addChannelInteractableListener(this);
	}

	private void updateUIComponents() {
		boolean prev = this.currentStateIndex > 0 && this.currentStateIndex < this.worldStates.size();
		//boolean next = this.currentStateIndex >= 0 && this.currentStateIndex < (this.worldStates.size() - 1);
		boolean next = this.currentStateIndex >= 0 && this.currentStateIndex < this.worldStates.size();
		this.startButton.setEnabled(prev);
		this.previousButton.setEnabled(prev);
		this.nextButton.setEnabled(next);
		this.endButton.setEnabled(next);
		String label;
		if (this.currentStateIndex >= 0 && this.currentStateIndex < this.worldStates.size()) {
			NPuzzleState state = getCurrentState();
			assert (state != null);
			String key = (state.isInitialization) ? "INIT_INFO" : "INFO"; //$NON-NLS-1$ //$NON-NLS-2$
			label = Locale.getString(NPuzzlePanel.class, key, Integer.toString(this.currentStateIndex + 1), Integer.toString(this.worldStates.size()));
		} else {
			label = Locale.getString(NPuzzlePanel.class, "NO_INFO"); //$NON-NLS-1$
		}
		this.stateLabel.setText(label);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_FIRST_STATE.equals(e.getActionCommand())) {
			synchronized (this) {
				int oldState = this.currentStateIndex;
				this.currentStateIndex = 0;
				if (this.currentStateIndex >= this.worldStates.size()) {
					this.currentStateIndex = -1;
				}
				if (oldState != this.currentStateIndex) {
					repaint();
					updateUIComponents();
				}
			}
		} else if (ACTION_PREVIOUS_STATE.equals(e.getActionCommand())) {
			synchronized (this) {
				int oldState = this.currentStateIndex;
				--this.currentStateIndex;
				if (this.currentStateIndex < 0 || this.currentStateIndex >= this.worldStates.size()) {
					this.currentStateIndex = -1;
				}
				if (oldState != this.currentStateIndex) {
					repaint();
					updateUIComponents();
				}
			}
		} else if (ACTION_NEXT_STATE.equals(e.getActionCommand())) {
			synchronized (this) {
				int oldState = this.currentStateIndex;
				++this.currentStateIndex;
				if (this.currentStateIndex < 0 || this.currentStateIndex >= this.worldStates.size()) {
					this.currentStateIndex = -1;
				}
				if (oldState != this.currentStateIndex) {
					repaint();
					updateUIComponents();
				}
			}
		} else if (ACTION_LAST_STATE.equals(e.getActionCommand())) {
			synchronized (this) {
				int oldState = this.currentStateIndex;
				this.currentStateIndex = this.worldStates.size() - 1;
				if (this.currentStateIndex < 0 || this.currentStateIndex >= this.worldStates.size()) {
					this.currentStateIndex = -1;
				}
				if (oldState != this.currentStateIndex) {
					repaint();
					updateUIComponents();
				}
			}
		}
	}

	/**
	 * Replies the current displayed state.
	 * 
	 * @return the current displayed state.
	 */
	private synchronized NPuzzleState getCurrentState() {
		if (this.currentStateIndex >= 0 && this.currentStateIndex < this.worldStates.size()) {
			return this.worldStates.get(this.currentStateIndex);
		}
		return null;
	}

	/**
	 * Replies the last state in history.
	 * 
	 * @return the last state in history.
	 */
	private synchronized NPuzzleState getLastState() {
		if (!this.worldStates.isEmpty()) {
			return this.worldStates.get(this.worldStates.size() - 1);
		}
		return null;
	}

	@Override
	public void channelIteractableLaunched(ChannelInteractable agent) {
		if (agent.getSupportedChannels().contains(EcoChannel.class)) {
			NPuzzleChannel channel = agent.getChannel(NPuzzleChannel.class);
			if (channel != null) {
				this.channels.add(channel);
				channel.addEcoChannelListener(this);
			}
		}
	}

	@Override
	public void channelIteractableKilled(ChannelInteractable agent) {
		if (agent.getSupportedChannels().contains(NPuzzleChannel.class)) {
			NPuzzleChannel channel = agent.getChannel(NPuzzleChannel.class);
			if (channel != null) {
				channel.removeEcoChannelListener(this);
				this.channels.remove(channel);
			}
		}
	}

	@Override
	public void channelContentChanged() {
		NPuzzleState lastState = getLastState();

		NPuzzleState state = new NPuzzleState();


		EcoIdentity entity;
		int placeId;
		int x,y;
		for (NPuzzleChannel channel : this.channels) {
			entity = channel.getEcoEntity();
			
			assert (entity != null);
			state.ecoStates.put(entity, channel.getEcoState());
			switch (channel.getAgentType()) {
			case TILE:break;
			case PLACE:
				placeId = ((EcoPlaceNPuzzleChannel)channel).getIndex();
				x = (placeId % NPuzzlePanel.this.gridSize);
				y = placeId / NPuzzlePanel.this.gridSize;
				for (EcoRelation relation : channel.getAcquaintances()) {
					if (relation instanceof Hosting) {						
						state.gridPlaces.put(new Couple(x,y),relation.getMaster());
						state.gridTiles.put(new Couple(x,y),relation.getSlave());
					} /*else if (relation instanceof Left) {

					} else if (relation instanceof Right) {

					} else if (relation instanceof Down) {

					} else if (relation instanceof Up) {

					}*/
				}
				break;

			case BLANK:break;
			default:
				throw new IllegalStateException();
			}
			state.isInitialization = state.isInitialization || channel.getEcoState().isInitializationState();
		}

		if (lastState == null || !lastState.equals(state)) {
			synchronized (this) {
				this.worldStates.add(state);
				int oldIndex = this.currentStateIndex;
				if (this.currentStateIndex < 0) {
					this.currentStateIndex = 0;
				} else if (this.currentStateIndex >= this.worldStates.size()) {
					this.currentStateIndex = this.worldStates.size() - 1;
				}
				updateUIComponents();
				if (this.currentStateIndex != oldIndex)
					repaint();
			}
		}

	}

	/**
	 * Displayer of the NPuzzle Game
	 * 
	 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	private class NPuzzleDisplayer extends JPanel {

		private static final long serialVersionUID = -8472613192229493932L;

		public NPuzzleDisplayer() {
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

			int columnSize = Math.max((viewport.width - 40) / (NPuzzlePanel.this.gridSize * 2), 40);
			int tileSize = Math.max(Math.min(columnSize, size / (NPuzzlePanel.this.gridSize * 2)) - 20, 20);
			

			NPuzzleState currentState = getCurrentState();

			// Draw warning background
			Color bg = getBackground();
			if (currentState != null && currentState.isInconsistent) {
				setBackground(Color.ORANGE);
			}
			super.paint(g);
			setBackground(bg);

			if (currentState != null) {
				// Draw Tiles
				int x0 = 5 + (columnSize - tileSize) / 2;
				int y0 = 5 + ((columnSize - tileSize) / 2) * NPuzzlePanel.this.gridSize;
				int x = x0;
				int y = y0;
				{
					EcoIdentity currentTile;
					for (int i = 0; i < NPuzzlePanel.this.gridSize; i++) {
						for (int j = 0; j < NPuzzlePanel.this.gridSize; j++) {
							currentTile = currentState.gridTiles.get(new Couple(i,j));
							drawTileOrPlace(g, currentTile, currentState, x, y, tileSize);
							y += columnSize;
						}
						y = y0;
						x += columnSize;
					}
				}
				
				g.setColor(Color.BLUE);
				g.drawLine(x, y0-tileSize, x, y0+columnSize*NPuzzlePanel.this.gridSize-tileSize);

				// Draw Places
				x = x0 + columnSize * NPuzzlePanel.this.gridSize + tileSize;
				y = y0;
				{
					EcoIdentity currentPlace;
					for (int i = 0; i < NPuzzlePanel.this.gridSize; i++) {
						for (int j = 0; j < NPuzzlePanel.this.gridSize; j++) {
							currentPlace = currentState.gridPlaces.get(new Couple(i,j));
							drawTileOrPlace(g, currentPlace, currentState, x, y, tileSize);
							y += columnSize;
						}
						y = y0;
						x += columnSize;
					}
				}
			}
		}

		private void drawTileOrPlace(Graphics g, EcoIdentity entity, NPuzzleState state, int x, int y, int cubeSize) {
			assert(entity != null);
			String label;
			int strWidth;
			EcoState ecoState;

			int yy = y - cubeSize;
			int strHeight = g.getFontMetrics().getHeight() / 2;

			Color backColor, frontColor;

			ecoState = state.ecoStates.get(entity);
			if (ecoState == null) {
				backColor = Color.DARK_GRAY;
				frontColor = Color.WHITE;
			} else {
				switch (ecoState) {
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
			g.drawString(label, x + (cubeSize - strWidth) / 2, yy + (cubeSize - strHeight) / 2);

			yy -= cubeSize;
		}
	}// Displayer

	/**
	 * A state of the NPuzzle Game
	 * 
	 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	private class NPuzzleState {

		/**
		 * Tiles of the NPuzzle: classical and blank
		 */
		public final Map<Couple,EcoIdentity> gridTiles = new TreeMap<Couple,EcoIdentity>();

		/**
		 * Places of the NPuzzle: fixed along a game
		 */
		public final Map<Couple,EcoIdentity> gridPlaces = new TreeMap<Couple,EcoIdentity>();

		/**
		 * Satisfaction statuses.
		 */
		public final Map<EcoIdentity, EcoState> ecoStates = new TreeMap<EcoIdentity, EcoState>(EcoIdentityComparator.SINGLETON);

		/**
		 * Indicates if this state is inconsistent.
		 */
		public boolean isInconsistent = false;

		/**
		 * Indicates if this state is for a initialization stage.
		 */
		public boolean isInitialization = false;
		
		public NPuzzleState() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public boolean equals(Object o) {
			if (o instanceof NPuzzleState) {
				NPuzzleState s = (NPuzzleState) o;
				if (this.isInitialization != s.isInitialization) {
					return false;
				}

				EcoState st;
				for (Entry<EcoIdentity, EcoState> entry : this.ecoStates.entrySet()) {
					st = s.ecoStates.get(entry.getKey());
					if (st == null || !equalsState(st, entry.getValue()))
						return false;
				}

				return this.gridTiles.equals(((NPuzzleState) o).gridTiles);
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
			h = h * 31 + this.ecoStates.hashCode();
			h = h * 31 + this.gridPlaces.hashCode();
			h = h * 31 + this.gridTiles.hashCode();
			return h;
		}
		
	}// NPuzzleState

	/**
	 * A couple of integers identifying a place within the NPuzzle Grid
	 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	private class Couple implements Comparable<Couple> {
		
		private int x;
		private int y;
		
		public Couple(int iX,int iY) {
			this.x = iX;
			this.y = iY;
		}

		@Override
		public int compareTo(Couple o) {
			if (this.x < o.x) {
				return -1;
			} else if (this.x > o.x) {
				return 1;
			} else {
				if (this.y < o.y) {
					return -1;
				} else if (this.y > o.y) {
					return 1;
				} else {
					return 0;
				}
			}
		}	
	}
}
