/* 
 * $Id$
 * 
 * Copyright (c) 2004-10, Janus Core Developers <Sebastian RODRIGUEZ, Nicolas GAUD, Stephane GALLAND>
 * All rights reserved.
 *
 * http://www.janus-project.org
 */
package org.janusproject.extras.ui.eclipse.base.images;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.janusproject.extras.ui.eclipse.base.Activator;

/**
 * @author Sebastian RODRIGUEZ &lt;sebastian@sebastianrodriguez.com.ar&gt;
 * @version $FullVersion$
 * @mavengroupid org.janus-project.kernel
 * @mavenartifactid org.janusproject.extras.ui.eclipse.base
 * 
 */
public class JanusSharedImages {
	private static final String ICONS = "/icons/";
	public static final String AGENTS = ICONS + "agents.gif";
	public static final String AGENT = ICONS + "agent.gif";
	public static final String AGENT_KERNEL = ICONS + "kernelagent.gif";
	public static final String GROUPS = ICONS + "groups.gif";
	public static final String GROUP = ICONS + "group.gif";
	public static final String LIBRARY = ICONS + "library.gif";
	public static final String KERNEL = ICONS + "kernel.gif";
	public static final String MODULES = ICONS + "modules.gif";
	public static final String MODULE = ICONS + "module.gif";
	public static final String MODULE_RUNNING = ICONS + "module-running.gif";
	public static final String RUN = ICONS + "run.gif";
	public static final String LOGO_JANUS_BANNER = ICONS + "logo-janus-banner.png";
	public static final String LOGO_JANUS = ICONS
			+ "logo-janus.png";

	public final static Image getImage(String imageKey) {
		return ImageDescriptor.createFromURL(
				Platform.getBundle(Activator.PLUGIN_ID).getEntry(imageKey)).createImage();
	}
}
