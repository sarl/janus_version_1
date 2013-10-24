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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.repository.RepositorySystem;
import org.arakhne.maven.AbstractArakhneMojo;
import org.codehaus.plexus.util.FileUtils;
import org.janus_project.janus_maven_plugin.layouts.EquinoxLayout;
import org.janus_project.janus_maven_plugin.layouts.FelixLayout;
import org.janus_project.janus_maven_plugin.layouts.Layout;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Abstract implementation of a maven mojo for the Janus platform.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractJanusModuleMojo extends AbstractArakhneMojo {

	/**
	 * Name of the distribution directory
	 */
	public final static String DIST_DIR = "janus-dist"; //$NON-NLS-1$

	/**
	 * Name of the working directory
	 */
	public final static String WORKING_DIR = "janus-tmp"; //$NON-NLS-1$

	/**
	 * Name of the file indicating a debugging version of janus-dist
	 */
	public final static String DEBUG_VERSION_FILE = "debugVersion"; //$NON-NLS-1$

	/**
	 * The layout to use: "equinox" or "felix".
	 * 
	 * @parameter
	 * @required
	 */
	protected String layout;

	/**
	 * Location of the file.
	 * 
	 * @parameter property="project.build.directory"
	 * @required
	 */
	protected File outputDirectory;

	/**
	 * @parameter property="project.basedir"
	 */
	private File baseDirectory;

	/**
	 * @parameter
	 */
	protected List<String> excludes;

	/**
	 * The artifact to use as launcher of you application.
	 * 
	 * @parameter
	 * @required
	 */
	protected Map<String, String> launcher;

//	/**
//	 * The resolver used to get the launcher artifact jar.
//	 * 
//	 * @component
//	 */
//	protected ArtifactResolver resolver;
//
	/**
	 * Directory where the configuration files are stored. <br/>
	 * 
	 * All files with extension ".japp.properties" will be merged into a single file. The name of the final file is defined using the platformConfigFileName parameter
	 * 
	 * @parameter property="preparedist.configDir" default-value="src/main/janus/application/conf"
	 */
	protected String configDir;

	/**
	 * The set of artifacts that the projects depends on.
	 * 
	 * @parameter property="project.artifacts"
	 * @readonly
	 */
	protected Set<Artifact> artifacts;

	/** @parameter property="project"
	 */
	protected MavenProject mavenProject;

	/**
	 * @parameter property="project.artifact"
	 * @readonly
	 */
	protected Artifact projectArtifact;

	/**
	 * Reference to the current session.
	 * 
	 * @parameter property="session"
	 * @required
	 */
	private MavenSession mavenSession;

	/**
	 * The entry point to Aether, i.e. the component doing all the work.
	 * 
	 * @component
	 */
	private RepositorySystem repoSystem;

	/**
	 * The current repository/network configuration of Maven.
	 * 
	 * @parameter default-value="${repositorySystemSession}"
	 * @readonly
	 */
	private Object repoSession;

	/**
	 * The project's remote repositories to use for the resolution of plugins and their dependencies.
	 * 
	 * @parameter default-value="${project.remotePluginRepositories}"
	 * @readonly
	 */
	private List<?> remoteRepos;

	/**
	 * @component
	 */
	private ArtifactHandlerManager artifactHandlerManager;

	/**
	 * @component role="org.apache.maven.project.MavenProjectBuilder"
	 * @required
	 * @readonly
	 */
	private MavenProjectBuilder mavenProjectBuilder;
	
	/** The context of building, compatible with M2E and CLI.
     * @component
     */
    private BuildContext buildContext;

	/**
	 * Replies the current OSGi layout.
	 * 
	 * @return the appropriate layout according to the considred osgi engine (Felix or Equinox)
	 * @throws MojoExecutionException
	 */
	protected Layout resolveLayout() throws MojoExecutionException {
		if (EquinoxLayout.NAME.equals(this.layout)) {
			return new EquinoxLayout();
		}

		if (FelixLayout.NAME.equals(this.layout)) {
			return new FelixLayout();
		}

		throw new MojoExecutionException(
				"Unable to determine the OSGi layout: "+this.layout); //$NON-NLS-1$
	}
	
	 /**
     * 
     * @return the maven artificat
     * @throws MojoExecutionException
     */
    protected Artifact resolveLauncher() throws MojoExecutionException {
            String groupId = this.launcher.get(AbstractArakhneMojo.PROP_GROUPID);
            String artifactId = this.launcher.get(AbstractArakhneMojo.PROP_ARTIFACTID);
            String version = this.launcher.get(AbstractArakhneMojo.PROP_VERSION);

            Artifact launcherArtifact = resolveArtifact(groupId, artifactId, version);

            return launcherArtifact;
    }

	/**
	 * Replies the directory where the janus distribution should be.
	 * 
	 * @return the janus distribution directory.
	 */
	protected File getJanusDistDirectory() {
		return new File(this.outputDirectory, DIST_DIR);
	}

	/**
	 * Create and replies the directory where the janus distribution should be.
	 * 
	 * @return the janus distribution directory.
	 * @throws IOException
	 */
	protected File ensureJanusDistDirectory() throws IOException {
		File f = getJanusDistDirectory();
		if (f.exists()) {
			FileUtils.deleteDirectory(f);
		}
		f.mkdirs();
		return f;
	}

	/**
	 * Create and replies a directory where working data could be written.
	 * 
	 * @return the temporary working directory.
	 * @throws IOException
	 */
	protected File ensureWorkingDirectory() throws IOException {
		File wDir = new File(this.outputDirectory, WORKING_DIR);
		wDir.mkdirs();
		FileUtils.forceDeleteOnExit(wDir);
		return wDir;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getRepositorySystemSession() {
		return this.repoSession;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<?> getRemoteRepositoryList() {
		return this.remoteRepos;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RepositorySystem getRepositorySystem() {
		return this.repoSystem;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MavenSession getMavenSession() {
		return this.mavenSession;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArtifactHandlerManager getArtifactHandlerManager() {
		return this.artifactHandlerManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getOutputDirectory() {
		return this.outputDirectory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getBaseDirectory() {
		return this.baseDirectory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MavenProjectBuilder getMavenProjectBuilder() {
		return this.mavenProjectBuilder;
	}

	/** {@inheritDoc}
	 */
	@Override
	public BuildContext getBuildContext() {
		return this.buildContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkMojoAttributes() throws MojoExecutionException {
		assertNotNull("outputDirectory", this.outputDirectory); //$NON-NLS-1$
		assertNotNull("mavenProject", this.mavenProject); //$NON-NLS-1$
		assertNotNull("mavenSession", this.mavenSession); //$NON-NLS-1$
		assertNotNull("repositorySystem", this.repoSystem); //$NON-NLS-1$
		assertNotNull("repositorySystemSession", this.repoSession); //$NON-NLS-1$
		assertNotNull("remoteRepositoryList", this.remoteRepos); //$NON-NLS-1$
		assertNotNull("artifactHandlerManager", this.artifactHandlerManager); //$NON-NLS-1$
		assertNotNull("mavenProjectBuilder", this.mavenProjectBuilder); //$NON-NLS-1$
	}

}
