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
 * Graphic User Interface for the ant demo.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AntFrame extends JFrame implements GridStateChannelListener {
	
	private static final long serialVersionUID = 530029308607649235L;

	private final WeakReference<GridStateChannel> channel;
	
	/**
	 * @param panel is the panel which is able to display the ant colony.
	 */
	public AntFrame(AntPanel panel) {
		this.channel = new WeakReference<GridStateChannel>(panel.getChannel());
		
		setTitle(Locale.getString(AntFrame.class, "TITLE_0")); //$NON-NLS-1$
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		addWindowListener(new Closer());
		
		JScrollPane scrollPane = new JScrollPane(panel);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
	setPreferredSize(new Dimension(50+AntPanel.CELL_SIZE*(getChannel().getGridWidth()), 50+AntPanel.CELL_SIZE*(getChannel().getGridHeight())));
		
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
			title = Locale.getString(AntFrame.class, "TITLE_0"); //$NON-NLS-1$
			break;
		case 1:
			title = Locale.getString(AntFrame.class, "TITLE_1", Integer.valueOf(count)); //$NON-NLS-1$
			break;
		default:
			title = Locale.getString(AntFrame.class, "TITLE_n", Integer.valueOf(count)); //$NON-NLS-1$
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
		setTitle(Locale.getString(AntFrame.class, "TITLE_0")); //$NON-NLS-1$
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
			title = Locale.getString(AntFrame.class, "TITLE_0"); //$NON-NLS-1$
			break;
		case 1:
			title = Locale.getString(AntFrame.class, "TITLE_1", Integer.valueOf(count)); //$NON-NLS-1$
			break;
		default:
			title = Locale.getString(AntFrame.class, "TITLE_n", Integer.valueOf(count)); //$NON-NLS-1$
			break;
		}
		setTitle(title);
	}
	
}
