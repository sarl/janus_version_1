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
package org.janusproject.demos.jaak.pacman.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.jaak.envinterface.channel.GridStateChannel;
import org.janusproject.jaak.envinterface.channel.GridStateChannelListener;
import org.janusproject.kernel.agent.Kernels;

/**
 * Graphic User Interface for the pacman demo.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PacManFrame extends JFrame implements GridStateChannelListener {
	
	private static final long serialVersionUID = 4288323781090725321L;
	
	private final WeakReference<GridStateChannel> channel;
	
	/**
	 * @param panel is the panel which is able to display the ant colony.
	 */
	public PacManFrame(PacManPanel panel) {
		this.channel = new WeakReference<GridStateChannel>(panel.getChannel());
		
		setTitle(Locale.getString(PacManFrame.class, "TITLE_0")); //$NON-NLS-1$
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		addWindowListener(new Closer());
		
		JScrollPane scrollPane = new JScrollPane(panel);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(
				50+PacManPanel.CELL_WIDTH*(getChannel().getGridWidth()),
				50+PacManPanel.CELL_HEIGHT*(getChannel().getGridHeight())));
		
		pack();
		
		getChannel().addGridStateChannelListener(this);
	}
	
	/**
	 * Replies the channel to the grid.
	 * @return the channel to the grid.
	 */
	protected GridStateChannel getChannel() {
		return this.channel.get();
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void gridStateChanged() {
		GridStateChannel channel = getChannel();
		int count = channel.getTurtleCount();
		String title;
		switch(count) {
		case 0:
			title = Locale.getString(PacManFrame.class, "TITLE_0"); //$NON-NLS-1$
			break;
		case 1:
			title = Locale.getString(PacManFrame.class, "TITLE_1", Integer.valueOf(count)); //$NON-NLS-1$
			break;
		default:
			title = Locale.getString(PacManFrame.class, "TITLE_n", Integer.valueOf(count)); //$NON-NLS-1$
			break;
		}
		setTitle(title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void jaakEnd() {
		getChannel().removeGridStateChannelListener(this);
		setTitle(Locale.getString(PacManFrame.class, "TITLE_0")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void jaakStart() {
		GridStateChannel channel = getChannel();
		int count = channel.getTurtleCount();
		String title;
		switch(count) {
		case 0:
			title = Locale.getString(PacManFrame.class, "TITLE_0"); //$NON-NLS-1$
			break;
		case 1:
			title = Locale.getString(PacManFrame.class, "TITLE_1", Integer.valueOf(count)); //$NON-NLS-1$
			break;
		default:
			title = Locale.getString(PacManFrame.class, "TITLE_n", Integer.valueOf(count)); //$NON-NLS-1$
			break;
		}
		setTitle(title);
	}
	
}
