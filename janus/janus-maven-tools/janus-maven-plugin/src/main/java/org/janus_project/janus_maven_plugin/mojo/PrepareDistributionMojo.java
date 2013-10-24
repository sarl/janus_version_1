/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2009-2011 Janus Core Developers
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
package org.janus_project.janus_maven_plugin.mojo;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.janus_project.janus_maven_plugin.layouts.Layout;
import org.janus_project.janus_maven_plugin.util.Utils;

/**
 * Prepares a Janus Application distribution.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * 
 * @goal preparedist
 * @execute phase="package"
 * @executionStrategy="always"
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class PrepareDistributionMojo extends AbstractDistributionMojo {
	
	/** {@inheritDoc}
	 */
	@Override
	public void executeMojo() throws MojoExecutionException {
		try {
			File outDir = ensureJanusDistDirectory();
			Layout layout = ensureLayout();
			distributeBins(outDir, layout);
			distributeConfigFiles(outDir, layout);
			distributeBundles(outDir, layout);

			File dbgFile = new File(outDir, DEBUG_VERSION_FILE);
			if (dbgFile.exists()) dbgFile.delete();
			
			getLog().info("NOTICE: " + DIST_DIR + " is in full release mode"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (Exception e) {
			Utils.createMojoException(e,
					"Error while creating release layout [", //$NON-NLS-1$
					this.launcher.get(PROP_GROUPID),
					",", //$NON-NLS-1$
					this.launcher.get(PROP_ARTIFACTID),
					",", //$NON-NLS-1$
					this.launcher.get(PROP_VERSION),
					"]: ", //$NON-NLS-1$
					e.getLocalizedMessage());
		}
	}

}
