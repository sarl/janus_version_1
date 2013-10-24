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
package org.janusproject.demos.jaak.pacman.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.jaak.pacman.channel.Player;
import org.janusproject.demos.jaak.pacman.channel.PlayerDirection;
import org.janusproject.demos.jaak.pacman.semantic.EvadeGhostSemantic;
import org.janusproject.demos.jaak.pacman.semantic.GhostSemantic;
import org.janusproject.demos.jaak.pacman.semantic.PacManSemantic;
import org.janusproject.demos.jaak.pacman.semantic.PillSemantic;
import org.janusproject.demos.jaak.pacman.semantic.PursueGhostSemantic;
import org.janusproject.demos.jaak.pacman.semantic.SuperPacManSemantic;
import org.janusproject.demos.jaak.pacman.semantic.WallSemantic;
import org.janusproject.jaak.envinterface.channel.GridStateChannel;
import org.janusproject.jaak.envinterface.channel.GridStateChannelListener;
import org.janusproject.jaak.envinterface.perception.JaakObject;

/**
 * UI for the pacman game.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PacManPanel extends JPanel implements MouseListener, KeyListener, GridStateChannelListener {

	private static final long serialVersionUID = 8390466904265723095L;

	/** Graphical size of a cell.
	 */
	public static final int CELL_WIDTH = 20;

	/** Graphical size of a cell.
	 */
	public static final int CELL_HEIGHT = 20;

	/** Margin size on the board.
	 */
	public static final int BOARD_MARGIN_SIZE = 5;

	private static final int DEMI_CELL_WIDTH = CELL_WIDTH/2;
	private static final int DEMI_CELL_HEIGHT = CELL_HEIGHT/2;
	
	private static final Color GROUND_COLOR = Color.BLACK;
	private static final Color WALL_COLOR = Color.BLUE;
	private static final Color PACMAN_COLOR = Color.YELLOW;
	private static final Color SUPER_PACMAN_COLOR = Color.RED;
	private static final Color GHOST_COLOR = Color.LIGHT_GRAY;
	private static final Color GHOST_RIDER_COLOR = Color.RED;
	private static final Color GHOST_COW_COLOR = Color.GREEN;
	private static final Color PILL_COLOR = Color.WHITE;
	private static final Color ERROR_COLOR = Color.RED;
	
	private final GridStateChannel environment;
	private final Player player;
	private final AtomicBoolean pacmanMouthOpened = new AtomicBoolean(true);
	
	private final InnerPanel innerPanel = new InnerPanel();
	private final JLabel information = new JLabel();
	
	/**
	 * @param env
	 * @param player
	 */
	public PacManPanel(GridStateChannel env, Player player) {
		this.environment = env;
		this.player = player;

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(
				CELL_WIDTH*this.environment.getGridWidth() + BOARD_MARGIN_SIZE*2,
				CELL_HEIGHT*this.environment.getGridHeight() + BOARD_MARGIN_SIZE*2));
		
		add(BorderLayout.CENTER, this.innerPanel);
		add(BorderLayout.SOUTH, this.information);
		
		env.addGridStateChannelListener(this);

		this.innerPanel.addMouseListener(this);
	
		addKeyListener(this);
		setFocusable(true);
		requestFocusInWindow();
	}
	
	/** Replies the environment channel.
	 * 
	 * @return the environment channel.
	 */
	public GridStateChannel getChannel() {
		return this.environment;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (this.player!=null) {
			int cellx = (e.getX() - BOARD_MARGIN_SIZE) / CELL_WIDTH;
			int celly = (e.getY() - BOARD_MARGIN_SIZE) / CELL_HEIGHT;
			if (cellx>=0 && cellx<this.environment.getGridWidth()
				&&
				celly>=0 && celly<this.environment.getGridHeight()) {
				Point2i t = new Point2i(cellx, celly);
				Point2i p = this.player.getPosition();
				if (p!=null) {
					PlayerDirection direction = PlayerDirection.makeDirection(p, t);
					if (direction!=null) {
						this.player.movePlayer(direction);
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		//
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			this.player.movePlayer(PlayerDirection.WEST);
		}
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			this.player.movePlayer(PlayerDirection.EAST);
		}
		else if (e.getKeyCode() == KeyEvent.VK_UP) {
			this.player.movePlayer(PlayerDirection.NORTH);
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			this.player.movePlayer(PlayerDirection.SOUTH);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		//
	}

	private static PlayerDirection toPlayerDirection(float angle) {
		float urAngle = (float)(Math.PI/4.);
		float ulAngle = (float)(Math.PI - Math.PI/4.);
		float brAngle = (float)(2.*Math.PI + Math.PI/4.);
		float blAngle = (float)(Math.PI + Math.PI/4.);
		
		if (angle>=urAngle && angle<=ulAngle)
			return PlayerDirection.NORTH;
		if (angle>=ulAngle && angle<=blAngle)
			return PlayerDirection.WEST;
		if (angle>=blAngle && angle<=brAngle)
			return PlayerDirection.SOUTH;
		return PlayerDirection.EAST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void gridStateChanged() {
		repaint();
		long superPacMan = this.player.getRemainPowerTime();
		if (superPacMan>0) {
			this.information.setText(Locale.getString(PacManPanel.class, "SUPER_PACMAN_DURATION", superPacMan/1000)); //$NON-NLS-1$
		}
		else {
			this.information.setText(""); //$NON-NLS-1$
		}
		requestFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void jaakEnd() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void jaakStart() {
		//
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class InnerPanel extends JPanel {

		private static final long serialVersionUID = -4524011445065203409L;

		/**
		 */
		public InnerPanel() {
			//
		}
		
		@SuppressWarnings("synthetic-access")
		@Override
		public void paint(Graphics g) {
			super.paint(g);

			Object semantic;
			int px, py;
			Graphics2D g2d = (Graphics2D)g;

			g.setColor(GROUND_COLOR);
			g.fillRect(BOARD_MARGIN_SIZE, BOARD_MARGIN_SIZE, PacManPanel.this.environment.getGridWidth() * CELL_WIDTH, PacManPanel.this.environment.getGridHeight() * CELL_HEIGHT);
			
			for(int x=0; x<PacManPanel.this.environment.getGridWidth(); x++) {
				for(int y=0; y<PacManPanel.this.environment.getGridHeight(); y++) {
					px = CELL_WIDTH * x + BOARD_MARGIN_SIZE;
					py = CELL_HEIGHT * y + BOARD_MARGIN_SIZE;
					for(JaakObject eo : PacManPanel.this.environment.getAllObjects(x, y)) {
						semantic = eo.getSemantic();
						if (semantic instanceof WallSemantic) {
							g2d.setColor(WALL_COLOR);
							g2d.fillRect(px, py, CELL_WIDTH, CELL_HEIGHT);
						}
						else if (semantic instanceof PacManSemantic || semantic instanceof SuperPacManSemantic) {
							if (semantic instanceof SuperPacManSemantic) {
								g2d.setColor(SUPER_PACMAN_COLOR);
							}
							else {
								g2d.setColor(PACMAN_COLOR);
							}
							if (PacManPanel.this.pacmanMouthOpened.get()) {
								g2d.fillArc(
										px+1, py+1, CELL_WIDTH-2, CELL_HEIGHT-2,
										45, 270);
							}
							else {
								g2d.fillArc(
										px+1, py+1, CELL_WIDTH-2, CELL_HEIGHT-2,
										5, 350);
							}
							g2d.setColor(GROUND_COLOR);
							int eyex = px+DEMI_CELL_WIDTH-2;
							int eyey = py+DEMI_CELL_HEIGHT/2-2;
							if (!PacManPanel.this.pacmanMouthOpened.get()) eyex ++;
							g2d.fillOval(eyex, eyey, 4, 4);
						}
						else if (semantic instanceof GhostSemantic) {
							Color color = null;
							if (semantic instanceof EvadeGhostSemantic) {
								color = GHOST_COW_COLOR;
							}
							else if (semantic instanceof PursueGhostSemantic) {
								color = GHOST_RIDER_COLOR;
							}
							else {
								color = GHOST_COLOR;
							}
							assert(color!=null);
							g2d.setColor(color);
							
							g2d.fillArc(
									px+3, py+1, CELL_WIDTH-6, CELL_HEIGHT-2,
									0, 180);
							int up = py+DEMI_CELL_HEIGHT;
							int bottom1 = py+CELL_HEIGHT-2;
							int bottom2 = bottom1-4;
							int lleft = px+3;
							int left = px + DEMI_CELL_WIDTH/2;
							int middle = px + DEMI_CELL_WIDTH;
							int right = px + DEMI_CELL_WIDTH + DEMI_CELL_WIDTH/2;
							int rright = px+CELL_WIDTH-3;
							if (!PacManPanel.this.pacmanMouthOpened.get()) {
								left --;
								middle -= 2;
								bottom2 += 2;
							}
							int[] xx = new int[] {
									lleft,
									lleft,
									left,
									middle,
									right,
									rright,
									rright
							};
							int[] yy = new int[] {
									up,
									bottom1,
									bottom2,
									bottom1,
									bottom2,
									bottom1,
									up
							};
							g2d.fillPolygon(xx,yy,Math.min(xx.length,yy.length));
							
							int eyex, eyey;
							
							int dx = 0;
							int dy = 0;
							
							switch(toPlayerDirection(PacManPanel.this.environment.getOrientation(x,y))) {
							case WEST:
								dx = -2;
								break;
							case EAST:
								break;
							case SOUTH:
								dx = -1;
								dy = -2;
								break;
							case NORTH:
								dx = -1;
								dy = 2;
								break;
							default:
							}
							
							eyex = px+DEMI_CELL_WIDTH - 5 + dx;
							eyey = py+DEMI_CELL_HEIGHT/2 + dy;
							g2d.setColor(GROUND_COLOR);
							g2d.fillOval(eyex, eyey, 5,5);
							g2d.setColor(color);
							
							eyex = px+DEMI_CELL_WIDTH + 2 + dx;
							g2d.setColor(GROUND_COLOR);
							g2d.fillOval(eyex, eyey, 5,5);

						}
						else if (semantic instanceof PillSemantic) {
							g2d.setColor(PILL_COLOR);
							if (PacManPanel.this.pacmanMouthOpened.get()) {
								g2d.fillOval(px+DEMI_CELL_WIDTH-2, py+DEMI_CELL_HEIGHT-2, 4, 4);
							}
							else {
								g2d.fillOval(px+DEMI_CELL_WIDTH-3, py+DEMI_CELL_HEIGHT-3, 6, 6);
							}
						}
						else {
							g2d.setColor(ERROR_COLOR);
							g2d.fillOval(px+2, py+2, CELL_WIDTH-4, CELL_HEIGHT-4);
						}
					}
				}
			}
			
			g.setColor(Color.WHITE);
			g.drawRect(BOARD_MARGIN_SIZE-1, BOARD_MARGIN_SIZE-1, PacManPanel.this.environment.getGridWidth() * CELL_WIDTH + 2, PacManPanel.this.environment.getGridHeight() * CELL_HEIGHT + 2);

			PacManPanel.this.pacmanMouthOpened.set(!PacManPanel.this.pacmanMouthOpened.get());
		}

	}
		
}
