/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010, 2012 Janus Core Developers
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
package org.janusproject.demos.simulation.boids.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.arakhne.afc.math.MathUtil;
import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.vmutil.locale.Locale;

/**
 * Graphic User Interface for the Boid demo.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class EnvironmentGUI extends JPanel {
	
	private static final long serialVersionUID = -7122258822721247462L;
	
	private final EnvironmentGuiPanel internalPanel;
	
	/**
	 * 
	 * @param width of the world
	 * @param height of the world
	 * @param provider is providing boid states.
	 */
	public EnvironmentGUI(int width, int height, BodyStateProvider provider) {
		super();
		setLayout(new BorderLayout());
		
		this.internalPanel = new EnvironmentGuiPanel(width, height, provider);
		add(this.internalPanel, BorderLayout.CENTER);

		JPanel populationButtons = new JPanel(new GridLayout(2, 3));
		add(populationButtons, BorderLayout.SOUTH);
		
		ButtonGroup btGroup = new ButtonGroup();
		JRadioButton noneButton = new JRadioButton(Locale.getString(EnvironmentGUI.class, "NONE")); //$NON-NLS-1$
		btGroup.add(noneButton);
		noneButton.addActionListener(new PopulationButtonListener(null));
		populationButtons.add(noneButton);

		for(Population pop : provider.populations()) {
			JRadioButton button = new JRadioButton(""); //$NON-NLS-1$
			btGroup.add(button);
			button.setText(pop.name);
			button.setToolTipText(pop.name);
			button.setForeground(toComplement(pop.color));
			button.setBackground(pop.color);
			button.addActionListener(new PopulationButtonListener(pop));
			populationButtons.add(button);
		}
		
		noneButton.setSelected(true);
		
		JLabel label = new JLabel(Locale.getString(EnvironmentGUI.class,"INTRO")); //$NON-NLS-1$
		add(label, BorderLayout.NORTH);
	}
	
	private static Color toComplement(Color c) {
		return new Color(
				255 - c.getRed(),
				255 - c.getGreen(),
				255 - c.getBlue());
	}

	/** Replies the position of the mouse on the panel.
	 * 
	 * @return the position of the mouse on the panel.
	 */
	public Vector2f getUserPosition() {
		return this.internalPanel.getUserPosition();
	}

	/** Replies the motion of the mouse on the panel.
	 * 
	 * @return the motion of the mouse on the panel.
	 */
	public Vector2f getUserDirection() {
		return this.internalPanel.getUserDirection();
	}

	/** Replies the selected population if the mouse is on the panel 
	 * 
	 * @return selected population if mouse is on panel.
	 */
	public Population getSelectedPopulation() {
		return this.internalPanel.getSelectedPopulation();
	}

	/** Set Selection population.
	 * 
	 * @param population
	 */
	void setSelectedPopulation(Population population) {
		this.internalPanel.setSelectedPopulation(population);
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class PopulationButtonListener implements ActionListener {
		
		private final Population population;
		
		/**
		 * @param population
		 */
		public PopulationButtonListener(Population population) {
			this.population = population;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			EnvironmentGUI.this.setSelectedPopulation(this.population);
		}
		
	}

	/**
	 * Displaying panel for boid demo.
	 * 
	 * @author $Author: ngaud$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class EnvironmentGuiPanel extends JPanel
	implements MouseMotionListener, MouseListener {

		private static final long serialVersionUID = 1147541712721356182L;

		/**
		 * Demi-width of the world.
		 */
		private final int demiWidth;
		/**
		 * Demi-height of the world.
		 */
		private final int demiHeight;

		private final BodyStateProvider bodyProvider;
		
		private final Point lastMousePosition = new Point();
		private Point mousePosition = null;
		private Population selectedPopulation;

		/**
		 * @param width of the world
		 * @param height of the world
		 * @param provider is the provider of the body states.
		 */
		public EnvironmentGuiPanel(int width, int height, BodyStateProvider provider) {
			super();
			this.demiWidth = width / 2;
			this.demiHeight = height / 2;
			this.bodyProvider = provider;
			setBackground(new Color(.6f, .6f, .6f));
			setPreferredSize(new Dimension(width, height));
			addMouseMotionListener(this);
			addMouseListener(this);
		}

		/** Replies the position of the mouse on the panel.
		 * 
		 * @return the position of the mouse on the panel.
		 */
		public synchronized Vector2f getUserPosition() {
			Point m = this.mousePosition;
			Population p = this.selectedPopulation;
			if (m==null || p==null) return null;
			return new Vector2f(
					m.x - this.demiWidth,
					m.y - this.demiHeight);
		}
		
		/** Replies the motion of the mouse on the panel.
		 * 
		 * @return the motion of the mouse on the panel.
		 */
		public synchronized Vector2f getUserDirection() {
			Point lm = new Point(this.lastMousePosition);
			Point m = this.mousePosition;
			Population p = this.selectedPopulation;
			if (m==null || p==null) return null;
			Vector2f v = new Vector2f(m.x, m.y);
			v.sub(new Vector2f(lm.x, lm.y));
			if (v.length()!=0) v.normalize();
			return v;
		}

		/** Replies the selected population if the mouse is on the panel 
		 * 
		 * @return selected population if mouse is on panel.
		 */
		public synchronized Population getSelectedPopulation() {
			Point m = this.mousePosition;
			Population p = this.selectedPopulation;
			if (m==null || p==null) return null;
			return p;
		}

		/** Set Selection population.
		 * 
		 * @param population
		 */
		public synchronized void setSelectedPopulation(Population population) {
			this.selectedPopulation = population;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			
			super.paint(g2d);

			
			for (PerceivedBoidBody boid : this.bodyProvider) {
				paintBoid(g2d, 
						boid.getPosition(),
						boid.getOrientation(),
						boid.getGroup().color);
			}
		}

		/**
		 * Paint a boid.
		 * 
		 * @param g2d is the graphical context to use.
		 * @param position is the position of the boid.
		 * @param direction is the direction of the boid.
		 * @param groupColor is the color of the group.
		 */
		private void paintBoid(Graphics2D g2d, Vector2f position, Vector2f direction, Color groupColor) {
			int posX;
			int posY;
			double cos;
			double sin;

			posX = this.demiWidth + (int)position.getX();
			posY = this.demiHeight + (int)position.getY();
			
			float angle = MathUtil.signedAngle(1, 0, direction.getX(), direction.getY());
			
			cos = Math.cos(angle);
			sin = Math.sin(angle);

			g2d.setColor(groupColor);

			g2d.drawLine(posX + (int) (5 * cos), posY + (int) (5 * sin), posX
					- (int) (2 * cos + 2 * sin), posY - (int) (2 * sin - 2 * cos));
			g2d.drawLine(posX + (int) (5 * cos), posY + (int) (5 * sin), posX
					- (int) (2 * cos - 2 * sin), posY - (int) (2 * sin + 2 * cos));
			g2d.drawLine(posX - (int) (2 * cos + 2 * sin), posY
					- (int) (2 * sin - 2 * cos), posX - (int) (2 * cos - 2 * sin),
					posY - (int) (2 * sin + 2 * cos));
		}

		private synchronized void update(Point newPosition) {
			if (this.mousePosition==null) {
				this.mousePosition = new Point(newPosition);
				this.lastMousePosition.setLocation(newPosition);
			}
			else {
				this.lastMousePosition.setLocation(this.mousePosition);
				this.mousePosition.setLocation(newPosition);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			update(e.getPoint());
		}


		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			update(e.getPoint());
		}


		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			//
		}


		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseEntered(MouseEvent e) {
			update(e.getPoint());
		}


		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			this.mousePosition = null;
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

}
