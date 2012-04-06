/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010 Janus Core Developers
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import org.janusproject.demos.simulation.foragerbots.agents.Grid;
import org.janusproject.demos.simulation.foragerbots.agents.GridListener;
import org.janusproject.demos.simulation.foragerbots.agents.Grid.Cell;

/**
 * Graphic User Interface for the forager bot demo.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class EnvironmentPanel
extends JPanel
implements GridListener {
	
	private static final long serialVersionUID = 2936607520245283399L;

	/** Size of a cell in pixels.
	 */
	public static final int CELL_SIZE = 4;
	
	private Color[][] grid = null;
	private int[] bases = null;
	private int radarLength = 0;
	
	/**
	 */
	public EnvironmentPanel() {
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(600, 400));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onGridChanged(int width, int height, Cell[][] cells, int[] baseCoordinates) {
		if (this.grid==null) {
			setPreferredSize(new Dimension(width*CELL_SIZE, height*CELL_SIZE));
			this.grid = new Color[width][height];
		}
		if (this.bases==null) {
			this.bases = baseCoordinates;
		}
		
		
		Cell cell;
		
		for(int x=0; x<cells.length; ++x) {
			for(int y=0; y<cells[x].length; ++y) {
				cell = cells[x][y];
				if (cell.getBotCount()>0) {
					this.grid[x][y] = Color.RED;
				}
				else {
					int r = cell.getResourceAmount();
					int c = (r * 255) / Grid.MAX_RESOURCE_AMOUNT;
					this.grid[x][y] = new Color(c, c, 0);
				}
			}
		}
		repaint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (this.grid!=null) {
			Color cell;
			
			for(int x=0; x<this.grid.length; ++x) {
				for(int y=0; y<this.grid[x].length; ++y) {
					cell = this.grid[x][y];
					g.setColor(cell);
					g.fillRect(x*CELL_SIZE, y*CELL_SIZE, CELL_SIZE, CELL_SIZE);
				}
			}
		}
		if (this.bases!=null) {
			g.setColor(Color.GREEN);
			int x, y;
			int s = this.radarLength*2 + 1;
			for(int i=0; i<this.bases.length-1; i+=2) {
				x = this.bases[i];
				y = this.bases[i+1];
				g.fillRect(x*CELL_SIZE, y*CELL_SIZE, CELL_SIZE, CELL_SIZE);
				if (this.radarLength>0) {
					g.drawOval(
							(x-this.radarLength)*CELL_SIZE,
							(y-this.radarLength)*CELL_SIZE,
							s*CELL_SIZE, s*CELL_SIZE);
				}
			}
			this.radarLength = (this.radarLength + 1) % 15;
		}
	}

}
