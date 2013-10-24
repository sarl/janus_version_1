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
package org.janusproject.demos.jaak.ants.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.JPanel;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.demos.jaak.ants.AntColonySystem;
import org.janusproject.demos.jaak.ants.environment.Food;
import org.janusproject.demos.jaak.ants.environment.Pheromone;
import org.janusproject.jaak.envinterface.channel.GridStateChannel;
import org.janusproject.jaak.envinterface.channel.GridStateChannelListener;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;

/**
 * Graphic User Interface for the ant demo.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AntPanel extends JPanel implements GridStateChannelListener, MouseListener {

	private static final long serialVersionUID = 4513244867249144515L;
	
	/** Color for mobile ants.
	 */
	public static final Color MOBILE_ANT_COLOR = Color.GREEN;

	/** Color for mobile ants.
	 */
	public static final Color IMMOBILE_ANT_COLOR = Color.YELLOW;

	/** Color for colonies.
	 */
	public static final Color COLONY_COLOR = Color.WHITE;

	/** Size of a cell in pixels.
	 */
	public static final int CELL_SIZE = 4;

	private final GridStateChannel channel;
	private Color[][] grid = null;
	private int[] bases = null;
	private int radarLength = 0;
	private int width = 0;
	private int height = 0;
	
	private int mx = -1;
	private int my = -1;
	private float speed = Float.NaN;
	
	/**
	 * @param channel is the channel from which informations should be retreived.
	 */
	public AntPanel(GridStateChannel channel) {
		setBackground(Color.BLACK);
		this.channel = channel;
		this.channel.addGridStateChannelListener(this);
		addMouseListener(this);
	}
	
	/** Replies the channel to the grid.
	 * 
	 * @return the channel to the grid.
	 */
	public GridStateChannel getChannel() {
		return this.channel;
	}

	/** Replies the color which is corresponding to the given amount
	 * of food.
	 * 
	 * @param amount is the amount of food
	 * @return the color for food
	 */
	public static Color makeFoodColor(int amount) {
		if (amount<=0) return null;
		int n = 55 + (amount * 200) / AntColonySystem.MAX_FOOD_PER_SOURCE;
		return new Color(n, 0, 0);
	}

	/** Replies the color which is corresponding to the given amount
	 * of pheromones.
	 * 
	 * @param pheromoneColor is the color of pheromones
	 * @param foodAmount is the amount of food
	 * @return the color for pheromone
	 */
	public static Color makePheromoneFoodColor(Color pheromoneColor, int foodAmount) {
		int red = 0;
		if (foodAmount>0) {
			red = 55 + (foodAmount * 200) / AntColonySystem.MAX_FOOD_PER_SOURCE;
		}
		
		red += pheromoneColor.getRed();
		if (red<0) red = 0;
		else if (red>255) red = 255;

		return new Color(red, pheromoneColor.getGreen(), pheromoneColor.getRed());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void gridStateChanged() {
		if (this.grid==null) {
			this.width = this.channel.getGridWidth();
			this.height = this.channel.getGridHeight();
			Dimension dim = new Dimension(
					this.width*CELL_SIZE + 10,
					this.height*CELL_SIZE + 10);
			setPreferredSize(dim);
			revalidate();
			this.grid = new Color[this.width][this.height];
		}
		Iterable<EnvironmentalObject> iterable;
		Iterator<EnvironmentalObject> iterator;
		EnvironmentalObject obj;
		Food food;
		Color c;
		float speed;
		for(int x=0; x<this.width; ++x) {
			for(int y=0; y<this.height; ++y) {
				speed = this.channel.getSpeed(x,y);
				if (!Float.isNaN(speed)) {
					if (speed>0f) {
						c = MOBILE_ANT_COLOR;
					}
					else {
						c = IMMOBILE_ANT_COLOR;
					}
				}
				else {
					iterable = this.channel.getEnvironmentalObjects(x, y);
					c = null;
					if (iterable!=null) {
						iterator = iterable.iterator();
						int nbElts = 0;
						Color pc;
						food = null;
						int cr = 0;
						int cg = 0;
						int cb = 0;
						while (iterator.hasNext()) {
							obj = iterator.next();
							if (obj instanceof Pheromone) {
								pc = ((Pheromone)obj).getColor();
								cr = Math.max(pc.getRed(), cr);
								cg = Math.max(pc.getGreen(), cg);
								cb = Math.max(pc.getBlue(), cb);
								++nbElts;
							}
							else if (obj instanceof Food && food==null) {
								food = (Food)obj;
							}
						}
						if (nbElts>0) {
							c = new Color(cr, cg, cb);
						}
						if (c!=null && food!=null) {
							c = makePheromoneFoodColor(c, food.intValue());
						}
						else if (food!=null) {
							c = makeFoodColor(food.intValue());
						}
					}
				}
				if (c==null) c = getBackground();
				this.grid[x][y] = c;
			}
		}
		if (this.bases==null) {
			Point2i[] ptIterator = this.channel.getSpawningPositions();
			this.bases = new int[ptIterator.length*2];
			for(int i=0,j=0; i<this.bases.length; i+=2,j++) {
				this.bases[i] = ptIterator[j].x();
				this.bases[i+1] = ptIterator[j].y();
			}
		}
		repaint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void jaakStart() {
		repaint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void jaakEnd() {
		this.channel.removeGridStateChannelListener(this);
		this.bases = null;
		this.grid = null;
		this.width = this.height = 0;
		repaint();
	}
	
	private static int simu2screen_x(int x) {
		return x * CELL_SIZE + 5;
	}
	
	private static int simu2screen_y(int y) {
		return y * CELL_SIZE + 5;
	}

	private static int screen2simu_x(int x) {
		return ((x - 5) / CELL_SIZE); 
	}
	
	private static int screen2simu_y(int y) {
		return ((y - 5) / CELL_SIZE); 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void paint(Graphics g) {
		super.paint(g);
		
		if (this.grid!=null) {
			for(int x=0; x<this.width; ++x) {
				for(int y=0; y<this.height; ++y) {
					if (this.grid[x][y]!=null) {
						g.setColor(this.grid[x][y]);
						g.fillRect(simu2screen_x(x), simu2screen_y(y), CELL_SIZE, CELL_SIZE);
					}
				}
			}
		}
		if (this.bases!=null) {
			g.setColor(COLONY_COLOR);
			int x, y;
			int s = this.radarLength*2 + 1;
			for(int i=0; i<this.bases.length-1; i+=2) {
				x = this.bases[i];
				y = this.bases[i+1];
				g.fillRect(simu2screen_x(x), simu2screen_y(y), CELL_SIZE, CELL_SIZE);
				if (this.radarLength>0) {
					g.drawOval(
							simu2screen_x(x-this.radarLength),
							simu2screen_y(y-this.radarLength),
							s*CELL_SIZE, s*CELL_SIZE);
				}
			}
			this.radarLength = (this.radarLength + 1) % 15;
		}

		g.setColor(Color.WHITE);
		g.drawRect(simu2screen_x(0), simu2screen_y(0), this.width*CELL_SIZE, this.height*CELL_SIZE);
		
		int mouseX = this.mx;
		int mouseY = this.my;
		float s = this.speed;
		if (!Float.isNaN(s)) {
			String speedTxt = Float.toString(s)+"c/s"; //$NON-NLS-1$
			g.drawString(speedTxt, mouseX, mouseY);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
		int sx = screen2simu_x(this.mx);
		int sy = screen2simu_y(this.my);

		try {
			this.speed = this.channel.getSpeed(sx, sy);
		}
		catch(Throwable _) {
			this.speed = Float.NaN;
		}
		repaint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		this.speed = Float.NaN;
		repaint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		this.speed = Float.NaN;
		repaint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		//
	}

}
