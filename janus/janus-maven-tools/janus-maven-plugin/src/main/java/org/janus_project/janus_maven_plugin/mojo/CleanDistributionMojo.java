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
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.janus_project.janus_maven_plugin.util.Utils;

/**
 * Clean the application target/janus-dist folder.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * 
 * @goal clean
 * @phase pre-clean
 */
public class CleanDistributionMojo extends AbstractJanusModuleMojo {

	/**
	 * The name to use for the final file
	 * 
	 * @parameter default-value="${project.groupId}-${project.artifactId}-${project.version}"
	 */
	private String finalFileName;

	/** {@inheritDoc}
	 */
	@Override
	public void executeMojo() throws MojoExecutionException {
		try {
			File workingDir = new File(this.outputDirectory, WORKING_DIR);
			if (workingDir.isDirectory()) {
				try {
					getLog().info("Deleting directory: "+workingDir.getAbsolutePath()); //$NON-NLS-1$
					FileUtils.deleteDirectory(workingDir);
				}
				catch (IOException e) {
					throw new MojoExecutionException(e.getLocalizedMessage(), e);
				}
			}

			File cleanDir = getJanusDistDirectory();
			if (cleanDir.isDirectory()) {
				getLog().info("Deleting directory: "+cleanDir.getAbsolutePath()); //$NON-NLS-1$
				FileUtils.deleteDirectory(cleanDir);
			}

			StringBuilder realFilename = new StringBuilder(this.finalFileName);
			if (!this.finalFileName.endsWith(ZipDistributionMojo.EXT)) {
				realFilename.append(ZipDistributionMojo.EXT);
			}
			File zip = new File(this.outputDirectory, realFilename.toString());

			if (zip.exists()) {
				getLog().info("Deleting file: "+zip.getAbsolutePath()); //$NON-NLS-1$
				FileUtils.forceDelete(zip);
			}
		}
		catch (Exception e) {
			Utils.createMojoException(e,
					"Error while cleaning [", //$NON-NLS-1$
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
