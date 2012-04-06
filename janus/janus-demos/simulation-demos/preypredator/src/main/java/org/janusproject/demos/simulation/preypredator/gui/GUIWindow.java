/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2009 Stephane GALLAND
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

package org.janusproject.demos.simulation.preypredator.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

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
public class GUIWindow extends JFrame implements WindowListener {

	private static final long serialVersionUID = 3939643459108505034L;
	
	/** GUI panel.
	 */
    protected final GUI gui;
	
    /**
     * @param worldState is the world state provider.
     */
	public GUIWindow(final GUIWorldState worldState) {
		this.gui = new GUI(worldState, true);
		
		Container content = getContentPane();
		
		content.setLayout(new BorderLayout());
		
		content.add(BorderLayout.CENTER,this.gui);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				worldState.stopGame();
				GUIWindow.this.gui.stopRefresher();
			}
			@Override
			public void windowClosing(WindowEvent e) {
				worldState.stopGame();
				GUIWindow.this.gui.stopRefresher();
			}
		});
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		pack();
		
		this.gui.launchRefresher();
		
		addWindowListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void windowActivated(WindowEvent e) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void windowClosed(WindowEvent e) {
		this.gui.stopRefresher();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		this.gui.stopRefresher();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void windowDeactivated(WindowEvent e) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void windowDeiconified(WindowEvent e) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void windowIconified(WindowEvent e) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void windowOpened(WindowEvent e) {
		//
	}

}