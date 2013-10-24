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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.janus_project.janus_maven_plugin.util.Utils;

/**
 * Runs an application form the debug version of the target/janus-dist folder.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * 
 * @goal rundebug
 * @execute goal="preparedebug"
 * @phase package
 */
public class RunDebugApplicationMojo extends AbstractJanusModuleMojo {

	/** {@inheritDoc}
	 */
	@Override
	public void executeMojo() throws MojoExecutionException {
		File runDir = getJanusDistDirectory();
		if (!runDir.isDirectory()) {
			FileNotFoundException e = new FileNotFoundException(runDir.getAbsolutePath());
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		}

		String launcherFileName = resolveLauncher().getFile().getName();
		String binDirPath = resolveLayout().getBinDir();
		File binDir = new File(runDir, binDirPath);
		File binForCmd = new File(binDir, launcherFileName);

		try {
			getLog().info(
					"Running command "+ //$NON-NLS-1$
							cmdsToString("java", "-jar", binForCmd)+ //$NON-NLS-1$ //$NON-NLS-2$
							" on dir "+ //$NON-NLS-1$
							runDir.getAbsolutePath());

			getLog().info("\n\n========================================\n          Start Applicaton\n========================================\n\n\n"); //$NON-NLS-1$

			Commandline cl = new Commandline("java"); //$NON-NLS-1$
			cl.addArguments(new String[] { "-jar", binForCmd.getAbsolutePath() }); //$NON-NLS-1$
			cl.setWorkingDirectory(runDir.getAbsolutePath());
			StreamConsumer output = new CommandLineUtils.StringStreamConsumer() {
				@Override
				public void consumeLine(String line) {
					getLog().info(line);
				}
			};
			StreamConsumer error = new CommandLineUtils.StringStreamConsumer() {
				@Override
				public void consumeLine(String line) {
					getLog().info(line);
				}
			};

			CommandLineUtils.executeCommandLine(cl, output, error);


		}
		catch (CommandLineException e) {
			Utils.createMojoException(e,
					"Error while creating executing Janus application [", //$NON-NLS-1$
					this.launcher.get(PROP_GROUPID),
					",", //$NON-NLS-1$
					this.launcher.get(PROP_ARTIFACTID),
					",", //$NON-NLS-1$
					this.launcher.get(PROP_VERSION),
					"]: ", //$NON-NLS-1$
					e.getLocalizedMessage());
		}

	}

	private static String cmdsToString(Object... cms) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("["); //$NON-NLS-1$
		for(Object c : cms) {
			if (buffer.length()>1) {
				buffer.append(","); //$NON-NLS-1$
			}
			if (c!=null) {
				buffer.append(c.toString());
			}
			else {
				buffer.append("null"); //$NON-NLS-1$
			}
		}
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}

}
