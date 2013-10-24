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
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.janus_project.janus_maven_plugin.layouts.Layout;
import org.janus_project.janus_maven_plugin.util.Utils;
import org.janus_project.janus_maven_plugin.util.ZipUtils;

/**
 * Creates a Zip containing the janus application for distribution.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * 
 * @goal zip
 * @execute goal="preparedist"
 * @phase package
 */
public class ZipDistributionMojo extends AbstractDistributionMojo {

	/**
	 * Extension for the zip file.
	 */
	public final static String EXT = ".zip"; //$NON-NLS-1$

	/**
	 * The name to use for the final file
	 * 
	 * @parameter default-value="${project.groupId}-${project.artifactId}-${project.version}"
	 */
	private String finalFileName;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void executeMojo() throws MojoExecutionException {
		File dirToZip = getJanusDistDirectory();
		if (!dirToZip.exists()) {// failed in the execution of preparedist
			try {
				File outDir = ensureJanusDistDirectory();
				Layout layout = ensureLayout();
				distributeBins(outDir, layout);
				distributeConfigFiles(outDir, layout);
				distributeBundles(outDir, layout);

				File dbgFile = new File(outDir, DEBUG_VERSION_FILE);
				if (dbgFile.exists())
					dbgFile.delete();

				getLog().info("NOTICE: " + DIST_DIR + " is in full release mode"); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (Exception e) {
				Utils.createMojoException(e, "Error while creating release layout [", //$NON-NLS-1$
						this.launcher.get(PROP_GROUPID), ",", //$NON-NLS-1$
						this.launcher.get(PROP_ARTIFACTID), ",", //$NON-NLS-1$
						this.launcher.get(PROP_VERSION), "]: ", //$NON-NLS-1$
						e.getLocalizedMessage());
			}
		}

		if (!dirToZip.isDirectory()) {
			FileNotFoundException e = new FileNotFoundException(dirToZip.getAbsolutePath());
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		}

		Layout layout = resolveLayout();
		if (layout == null) {
			throw new MojoExecutionException("Unknown Layout: "+this.layout); //$NON-NLS-1$
		}

		getLog().info("OSGi layout: " + layout.getName()); //$NON-NLS-1$

		String cacheDirectory = layout.getCacheDir();

		if (cacheDirectory != null && !"".equals(cacheDirectory)) { //$NON-NLS-1$
			File cacheDir = new File(dirToZip, cacheDirectory);
			try {
				if (cacheDir.isDirectory()) {
					FileUtils.deleteDirectory(cacheDir);
				} else if (cacheDir.exists()) {
					FileUtils.forceDelete(cacheDir);
				}
			} catch (IOException e) {
				throw new MojoExecutionException(e.getLocalizedMessage(), e);
			}
		}

		StringBuilder realFilename = new StringBuilder(this.finalFileName);
		if (!this.finalFileName.endsWith(ZipDistributionMojo.EXT)) {
			realFilename.append(ZipDistributionMojo.EXT);
		}
		File zip = new File(this.outputDirectory, realFilename.toString());

		try {
			ZipUtils.zipDirectory(getLog(), dirToZip, zip, this.finalFileName);
		} catch (IOException e) {
			Utils.createMojoException(e, "Error while creating distribution archive [", //$NON-NLS-1$
					this.launcher.get(PROP_GROUPID), ",", //$NON-NLS-1$
					this.launcher.get(PROP_ARTIFACTID), ",", //$NON-NLS-1$
					this.launcher.get(PROP_VERSION), "]: ", //$NON-NLS-1$
					e.getLocalizedMessage());
		}
	}

}
