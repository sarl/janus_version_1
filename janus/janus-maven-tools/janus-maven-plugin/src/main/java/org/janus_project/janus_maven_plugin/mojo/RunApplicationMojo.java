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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.janus_project.janus_maven_plugin.util.Utils;

/**
 * Runs an pplication form the target/janus-dist folder.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * 
 * @goal run
 * @execute goal="preparedist"
 * @phase package
 */
public class RunApplicationMojo extends AbstractJanusModuleMojo {

	/** Replies if the given name is a forwardable property.
	 * 
	 * @param name
	 * @return <code>true</code> if the property is forwadable,
	 * otherwise <code>false</code>.
	 */
	protected static boolean isForwardableProperty(String name) {
		if (name!=null) {
			return name.equals("JXTA_HOME") //$NON-NLS-1$
				|| name.startsWith("janus.") //$NON-NLS-1$
				|| name.startsWith("net.jxta.") //$NON-NLS-1$
				|| name.startsWith("felix.") //$NON-NLS-1$
				|| name.startsWith("org.") //$NON-NLS-1$
				|| name.startsWith("http.") //$NON-NLS-1$
				|| name.startsWith("ftp."); //$NON-NLS-1$
		}
		return false;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void executeMojo() throws MojoExecutionException {
		File runDir = getJanusDistDirectory();
		if (!runDir.isDirectory()) {
			FileNotFoundException e = new FileNotFoundException(runDir.getAbsolutePath());
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		}

		Artifact launcherArtifact = resolveLauncher();
		
		String launcherFileName = launcherArtifact.getFile().getName();
		String binDirPath = resolveLayout().getBinDir();
		File binDir = new File(runDir, binDirPath);
		File binForCmd = new File(binDir, launcherFileName);

		try {
			List<String> params = new ArrayList<String>();
			
			Properties props = System.getProperties();
			String propValue;
			for(Object key : props.keySet()) {
				if (key!=null && isForwardableProperty(key.toString())) {
					propValue = props.getProperty(key.toString());
					params.add("-D"+ //$NON-NLS-1$
							key+"="+ //$NON-NLS-1$
							propValue);
				}
			}
			
			params.add("-jar"); //$NON-NLS-1$
			params.add(binForCmd.getAbsolutePath());
			
			getLog().info(
					"Running command java on "+ //$NON-NLS-1$
					params.toString()+
					" in dir "+ //$NON-NLS-1$
					runDir.getAbsolutePath());

			getLog().info("\n\n========================================\n          Start Applicaton\n========================================\n\n\n"); //$NON-NLS-1$

			Commandline cl = new Commandline("java"); //$NON-NLS-1$
			String[] paramTab = new String[params.size()];
			params.toArray(paramTab);
			cl.addArguments(paramTab);
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

}
