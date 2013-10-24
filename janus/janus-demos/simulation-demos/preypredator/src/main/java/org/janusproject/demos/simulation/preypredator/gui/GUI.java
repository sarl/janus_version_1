/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2009 Stephane GALLAND
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

package org.janusproject.demos.simulation.preypredator.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.Executors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.arakhne.afc.vmutil.Resources;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.preypredator.message.MoveDirection;
import org.janusproject.demos.simulation.preypredator.osgi.PreyPredatorActivator;
import org.janusproject.kernel.address.AgentAddress;

/** 
 * Display world state.
 * <p>
 * Copied from <a href="http://www.arakhne.org/tinymas/index.html">TinyMAS Platform Demos</a>
 * and adapted for Janus platform.
 * <p>
 * Thanks to Julia Nikolaeva, aka. <a href="mailto:flameia@zerobias.com">Flameia</a>, for the icons.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class GUI extends JPanel implements WorldStateChangeListener {

	private static final long serialVersionUID = 3939643459108505034L;
	
	/**
	 * Speed of animation (in seconds).
	 */
	protected static final int ANIMATION_SPEED = 1;
	/**
	 * Number of steps in animat animation.
	 */
	protected static final int ANIMATION_STEPS = 10;
	
	/**
	 * Prey icon.
	 */
	protected static final Icon PREY_ICON;
	/**
	 * Predator icon.
	 */
	protected static final Icon PREDATOR_ICON;	
	/**
	 * Up direction icon.
	 */
	protected static final Icon UP_ICON;
	/**
	 * Down direction icon.
	 */
	protected static final Icon DOWN_ICON;	
	/**
	 * Left direction icon.
	 */
	protected static final Icon LEFT_ICON;
	/**
	 * Right direction icon.
	 */
	protected static final Icon RIGHT_ICON;	
	/**
	 * Width of icons.
	 */
	protected static final int ICON_WIDTH;
	/**
	 * Height of icons.
	 */
	protected static final int ICON_HEIGHT;
	
	private static final String IMG_PATH_PREFIX="org/janusproject/demos/simulation/preypredator/img/";	 //$NON-NLS-1$
	
	/**
	 * Here we get the class which load <code>PreyPredatorActivator</code> class to be sure to obtain the right 
	 * classloader which has the global osgi bundle classpath.
	 */
	static {
		URL url = Resources.getResource(PreyPredatorActivator.class.getClassLoader(),IMG_PATH_PREFIX+"rabbit.png"); //$NON-NLS-1$
		assert(url!=null);
		PREY_ICON = new ImageIcon(url);
		url = Resources.getResource(PreyPredatorActivator.class.getClassLoader(),IMG_PATH_PREFIX+"lion.png"); //$NON-NLS-1$		
		assert(url!=null);
		PREDATOR_ICON = new ImageIcon(url);
		url = Resources.getResource(PreyPredatorActivator.class.getClassLoader(),IMG_PATH_PREFIX+"go-up.png"); //$NON-NLS-1$	
		assert(url!=null);
		UP_ICON = new ImageIcon(url);
		url = Resources.getResource(PreyPredatorActivator.class.getClassLoader(),IMG_PATH_PREFIX+"go-down.png"); //$NON-NLS-1$		
		assert(url!=null);
		DOWN_ICON = new ImageIcon(url);
		url = Resources.getResource(PreyPredatorActivator.class.getClassLoader(),IMG_PATH_PREFIX+"go-previous.png"); //$NON-NLS-1$		
		assert(url!=null);
		LEFT_ICON = new ImageIcon(url);
		url = Resources.getResource(PreyPredatorActivator.class.getClassLoader(),IMG_PATH_PREFIX+"go-next.png"); //$NON-NLS-1$
		assert(url!=null);
		RIGHT_ICON = new ImageIcon(url);
		
		ICON_WIDTH = Math.max(PREY_ICON.getIconWidth(), PREDATOR_ICON.getIconWidth());
		ICON_HEIGHT = Math.max(PREY_ICON.getIconHeight(), PREDATOR_ICON.getIconHeight());
	}

	/** Replies an icons in which the two givens icons are merged.
	 * 
	 * @param top_icon is the foreground picture.
	 * @param bottom_icon is the background picture.
	 * @param x is the position of the foreground picture.
	 * @param y is the position of the foreground picture.
	 * @return the merged icon.
	 */
    public static Image mergeImages(Image top_icon, Image bottom_icon, int x, int y) {
    	int imgWidth = bottom_icon.getWidth(null);
    	int imgHeight = bottom_icon.getHeight(null);
    	BufferedImage img = new BufferedImage(
    			imgWidth, imgHeight,
    			Transparency.BITMASK);
    	Graphics g = img.getGraphics();
    	g.setClip(0,0,imgWidth,imgHeight);
    	g.drawImage(bottom_icon,0,0,null);
    	int rx = x;
    	int ry = y;
    	if (rx<0) rx = imgWidth + rx;
    	if (ry<0) ry = imgHeight + ry;
    	g.drawImage(top_icon,rx,ry,null);
    	return Toolkit.getDefaultToolkit().createImage(img.getSource());
    }

	/** Replies an icons in which the two givens icons are merged.
	 * 
	 * @param top_icon is the foreground picture.
	 * @param bottom_icon is the background picture.
	 * @param x is the position of the foreground picture.
	 * @param y is the position of the foreground picture.
	 * @return the merged icon.
	 */
    public static Icon mergeIcons(Icon top_icon, Icon bottom_icon, int x, int y) {
        if ((top_icon instanceof ImageIcon)&&(bottom_icon instanceof ImageIcon)) {
        	Image new_image = mergeImages(
    				((ImageIcon)top_icon).getImage(),
    				((ImageIcon)bottom_icon).getImage(),
    				x, y);
        	if (new_image!=null)
        		return new ImageIcon(new_image);        	
        }
        return null;
    }                       

    /** World Model.
     */
    protected GUIWorldState worldState;
    
    /** World displayer.
     */
    protected final Grid grid;

    /** Refreshing thread.
     */
	protected final RefreshThread refresher;
	
	/** Buffered positions before moves.
	 */
	protected Map<AgentAddress,WorldState> positions;

	/** Buffered positions after moves.
	 */
	protected Map<AgentAddress,WorldState> nextPositions;

	/**
	 * @param worldState is the world state provider
	 * @param enableQuitButton
	 */
	public GUI(GUIWorldState worldState, boolean enableQuitButton) {
		this.worldState = worldState;
		this.refresher = new RefreshThread();
		this.grid = new Grid();
		
		if (worldState!=null)
			worldState.addWorldStateChangeListener(this);
		
		setLayout(new BorderLayout());
		
		JScrollPane scroll = new JScrollPane(this.grid);
		add(BorderLayout.CENTER,scroll);
		
		if (enableQuitButton) {
			JButton closeBt = new JButton(Locale.getString(GUI.class, "QUIT")); //$NON-NLS-1$
			closeBt.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GUI.this.worldState.stopGame();
				}
			});
			add(BorderLayout.SOUTH, closeBt);
		}
		else {
			JLabel label = new JLabel(Locale.getString(GUI.class, "BLANK_WHEN_FINISH")); //$NON-NLS-1$
			add(BorderLayout.SOUTH, label);
		}
		
		Dimension d;
		if (worldState!=null)
			d = new Dimension(
					(worldState.getWorldWidth()+1) * ICON_WIDTH,
					(worldState.getWorldHeight()+2) * ICON_HEIGHT);
		else
			d = new Dimension(300,350);
		setMinimumSize(d);
		setPreferredSize(d);
	}
	
	/**
	 * Set the world state for GUI.
	 * @param state
	 */
	public void setWorldState(GUIWorldState state) {
		assert(state!=null);
		if (this.worldState!=null)
			this.worldState.removeWorldStateChangeListener(this);
		this.worldState = state;
		if (this.worldState!=null) {
			this.worldState.addWorldStateChangeListener(this);
			setPreferredSize(new Dimension(
						(this.worldState.getWorldWidth()+1) * ICON_WIDTH,
						(this.worldState.getWorldHeight()+2) * ICON_HEIGHT));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stateChanged() {
		this.refresher.addMoves(this.worldState.getLastPositions());
	}

	/** Start refresher.
	 */
	public void launchRefresher() {
		Executors.newSingleThreadExecutor().execute(this.refresher);
	}
	
	/** Stop refresher.
	 */
	public void stopRefresher() {
		this.refresher.stop();
	}

	/**
	 * Update GUI with moves.
	 * @param moves
	 */
	protected void setPositions(Map<AgentAddress,WorldState> moves) {
		this.positions = moves;
		this.grid.setStep(-1);
		this.grid.repaint();
	}

	/**
	 * Update GUI with moves
	 * @param moveStep
	 * @param moves
	 */
	protected void moveTo(int moveStep, Map<AgentAddress,WorldState> moves) {
		this.nextPositions = moves;
		this.grid.setStep(moveStep);
		this.grid.repaint();
	}

	/**
	 * Update GUI.
	 * 
	 * @param moveStep
	 */
	protected void refreshGUI(int moveStep) {
		if (moveStep<0) {
			this.positions = this.nextPositions;
			//debugPositions(getKernel(), this.positions, "NEW POSITION"); //$NON-NLS-1$
		}
		this.grid.setStep(moveStep);
		this.grid.repaint();
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class RefreshThread implements Runnable {

		private final Queue<Map<AgentAddress,WorldState>> queue = new LinkedList<Map<AgentAddress,WorldState>>(); 
		
		private boolean run = true;
		private int moveStep = -2;

		public RefreshThread() {
			//
		}
		
		public void stop() {
			this.run = false;
		}
		
		@Override
		public void run() {
			while (this.run) {
				if (this.moveStep<0) {
					Map<AgentAddress,WorldState> moves = this.queue.poll();
					if (moves!=null) {
						if (this.moveStep==-1) {
							this.moveStep = 0;
							GUI.this.moveTo(this.moveStep, moves);
						}
						else if (this.moveStep==-2) {
							this.moveStep = -1;
							GUI.this.setPositions(moves);
						}
					}
				}
				else {
					try {
						Thread.sleep((1000*ANIMATION_SPEED)/ANIMATION_STEPS);
					}
					catch (InterruptedException e) {
						//
					}
					if (this.moveStep>=ANIMATION_STEPS) {
						this.moveStep = -1;
					}
					else {
						++this.moveStep;
					}
					GUI.this.refreshGUI(this.moveStep);
				}
				Thread.yield();
			}
		}
		
		/** Enqueue moves.
		 * 
		 * @param moves
		 */
		public void addMoves(Map<AgentAddress,WorldState> moves) {
			if (moves!=null) {
				Map<AgentAddress,WorldState> old = this.queue.peek();
				if ((old==null)||(!old.equals(moves))) {
					this.queue.offer(moves);
				}
			}
		}
		
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class Grid extends JPanel {
		
		private static final long serialVersionUID = 4361140442107583935L;
		
		private int moveStep = -1;
		
		public Grid() {
			//
		}
		
		public void setStep(int step) {
			this.moveStep = step;
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2d = (Graphics2D)g;
			
			Dimension currentDim = getPreferredSize();
			Dimension desiredDim = computeDesiredDim();
			
			if ((desiredDim!=null)&&(!currentDim.equals(desiredDim))) {
				setPreferredSize(desiredDim);
				revalidate();
				repaint();
				return;
			}
			
			if (this.moveStep>=0) {
				drawMovingAgents(g2d);
			}
			else {
				drawAgents(g2d);					
			}
			
			drawGrid(g2d, currentDim);
		}
		
		private void drawMovingAgents(Graphics2D g2d) {
			if ((this.moveStep<0)||
				(GUI.this.nextPositions==null)||
				(GUI.this.positions==null)) drawAgents(g2d);
			
			int x, y, deltaX, deltaY;
			WorldState d;
			WorldState nextD;
			AgentAddress id;
			
			AgentAddress prey = GUI.this.worldState.getPrey();
			deltaX = ((this.moveStep * ICON_WIDTH) / ANIMATION_STEPS);
			deltaY = ((this.moveStep * ICON_HEIGHT) / ANIMATION_STEPS);
			
			Icon ic;
			String name;

			for (Entry<AgentAddress, WorldState> entry : GUI.this.positions.entrySet()) {
				id = entry.getKey();
				d = entry.getValue();
				if (GUI.this.nextPositions.containsKey(id)) {
					nextD = GUI.this.nextPositions.get(id);
					x = d.X * ICON_WIDTH;
					y = d.Y * ICON_HEIGHT;
					
					if (nextD.X<d.X) {
						x -= deltaX;
					}
					else if (nextD.X>d.X) {
						x += deltaX;
					}
										
					if (nextD.Y<d.Y) {
						y -= deltaY;
					}
					else if (nextD.Y>d.Y) {
						y += deltaY;
					}
					
					ic = getIcon(id.equals(prey), nextD.DIRECTION);
					assert(ic!=null);
					ic.paintIcon(this, g2d, x, y);

					name = entry.getKey().getName();
					if (name!=null)
						g2d.drawString(name, x, y+ICON_HEIGHT);
				}
			}
		}
		
		private void drawAgents(Graphics2D g2d) {
			if (GUI.this.positions==null) return;
			int x, y;
			WorldState d;
			AgentAddress prey = GUI.this.worldState.getPrey();
			Icon ic;
			String name;
			for (Entry<AgentAddress, WorldState> entry : GUI.this.positions.entrySet()) {
				d = entry.getValue();
				x = d.X * ICON_WIDTH;
				y = d.Y * ICON_HEIGHT;
				
				ic = getIcon(entry.getKey().equals(prey), d.DIRECTION);
				assert(ic!=null);
				ic.paintIcon(this, g2d, x, y);
				
				name = entry.getKey().getName();
				if (name!=null)
					g2d.drawString(name, x, y+ICON_HEIGHT);
			}
		}
		
		private void drawGrid(Graphics2D g2d, Dimension currentDim) {
			int x, y;
			g2d.setColor(Color.BLACK);
			for(x=0; x<=currentDim.width; x+=ICON_WIDTH) {
				g2d.drawLine(x, 0, x, currentDim.height); 
			}
			for(y=0; y<=currentDim.height; y+=ICON_WIDTH) {
				g2d.drawLine(0, y, currentDim.width, y); 
			}
			FontMetrics fm = g2d.getFontMetrics();
			y -= ICON_WIDTH - fm.getHeight();
			
						
			//FIXME Find the way to load the resource from property files with the right bundle classpath
			//Here we get the class which load <code>PreyPredatorActivator</code> class to be sure to obtain the right classloader which has the global osgi bundle classpath.
			//Way 2: ResourceBundle bundle = ResourceBundle.getBundle("org/janusproject/demos/simulation/preypredator/gui/GUI.properties",java.util.Locale.getDefault(),PreyPredatorActivator.class.getClassLoader());			
			
			//Way 1: g2d.drawString(LocalizedString.getString(GUI.class, "COPYRIGHT_1"), 0, y); //$NON-NLS-1$
			//Way 2: g2d.drawString(bundle.getString("COPYRIGHT_1"), 0, y); //$NON-NLS-1$
			g2d.drawString("© 2005-2010 Stéphane Galland - Java code", 0, y); //$NON-NLS-1$
			y += fm.getHeight();
			
			//Way 1:g2d.drawString(LocalizedString.getString(GUI.class, "COPYRIGHT_2"), 0, y); //$NON-NLS-1$
			//Way 2: g2d.drawString(bundle.getString("COPYRIGHT_2"), 0, y); //$NON-NLS-1$
			g2d.drawString("© 2005-2010 Julia Nikolaeva - Icons", 0, y); //$NON-NLS-1$
		}
				
		private Dimension computeDesiredDim() {
			return new Dimension(
					GUI.this.worldState.getWorldWidth()*GUI.ICON_WIDTH,
					GUI.this.worldState.getWorldHeight()*GUI.ICON_HEIGHT);
		}
		
		private Icon getIcon(boolean isPrey, MoveDirection direction) {
			Icon ic;
			if (isPrey) {
				ic = PREY_ICON;
			}
			else {
				ic = PREDATOR_ICON;
			}
			
			switch(direction) {
			case UP:
				ic = mergeIcons(UP_ICON, ic, 
						(ic.getIconWidth()-UP_ICON.getIconWidth())/2, 0);
				break;
			case DOWN:
				ic = mergeIcons(DOWN_ICON, ic, 
						(ic.getIconWidth()-DOWN_ICON.getIconWidth())/2,
						ic.getIconHeight() - DOWN_ICON.getIconHeight());
				break;
			case LEFT:
				ic = mergeIcons(LEFT_ICON, ic,
						0,
						(ic.getIconHeight()-LEFT_ICON.getIconHeight())/2);
				break;
			case RIGHT:
				ic = mergeIcons(RIGHT_ICON, ic, 
						ic.getIconWidth() - RIGHT_ICON.getIconWidth(),
						(ic.getIconHeight()-RIGHT_ICON.getIconHeight())/2);
				break;
			case NONE:
			default:
				break;
			}
			
			return ic;
		}
	}
	
}