/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2009-2012 Janus Core Developers
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.util.FileUtils;
import org.janus_project.janus_maven_plugin.layouts.Layout;
import org.janus_project.janus_maven_plugin.util.Utils;
import org.janus_project.janus_maven_plugin.util.ZipUtils;

/**
 * Abstract implementation of a maven mojo for the distribution of Janus platform.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractDistributionMojo extends AbstractJanusModuleMojo {

	/**
	 * Name of the launcher file
	 */
	public static final String PROP_USED_LAUNCHER_FILE_NAME = "janus.maven-module-plugin.launcherFileName"; //$NON-NLS-1$

	/**
	 * The platform configuration file name. <br/>
	 * If not defined the default name of the layout will be used.
	 * 
	 * @parameter property="preparedist.platformConfigFileName"
	 */
	protected String platformConfigFileName = null;

	/**
	 * The layout configuration file name. <br/>
	 * If not defined the default name of the layout will be used.
	 * 
	 * @parameter property="preparedist.layoutConfigFileName"
	 */
	protected String layoutConfigFileName = null;

	/**
	 * Resolve and reply the current OSGi layout. This function does not return when a layout is not available.
	 * 
	 * @return the layout, never <code>null</code>.
	 * @throws MojoFailureException
	 * @throws MojoExecutionException
	 */
	protected Layout ensureLayout() throws MojoFailureException, MojoExecutionException {
		Layout layout = resolveLayout();
		if (layout == null) {
			throw new MojoExecutionException("Unknown Layout: "+this.layout); //$NON-NLS-1$
		}
		getLog().info("OSGi layout: "+layout.getName()); //$NON-NLS-1$
		return layout;
	}

	/**
	 * Distribute the OSGi layout bin files.
	 * 
	 * @param outDir
	 *            is the output directory, such as target/janus-dist.
	 * @param layout
	 *            is the current OSGi layout.
	 * @throws MojoExecutionException
	 * @throws IOException
	 */
	protected void distributeBins(File outDir, Layout layout) throws MojoExecutionException, IOException {
		Artifact launcherArtifact = resolveLauncher();

		File binDir = new File(outDir, layout.getBinDir());
		binDir.mkdirs();

		this.mavenProject.getProperties().put(PROP_USED_LAUNCHER_FILE_NAME, launcherArtifact.getFile().getName());

		getLog().info("Copying bin file: " + launcherArtifact.getFile().getName()); //$NON-NLS-1$
		FileUtils.copyFileToDirectory(launcherArtifact.getFile().getAbsolutePath(), binDir.getAbsolutePath());
	}

	/**
	 * Distribute the OSGi layout bundle files.
	 * 
	 * @param outDir
	 *            is the output directory, such as target/janus-dist.
	 * @param layout
	 *            is the current OSGi layout.
	 * @throws MojoExecutionException
	 * @throws ArtifactNotFoundException
	 * @throws ArtifactResolutionException
	 * @throws IOException
	 */
	protected void distributeBundles(File outDir, Layout layout) throws MojoExecutionException, ArtifactResolutionException, ArtifactNotFoundException, IOException {
		Set<File> deps = Utils.getOsgiDependencyFiles(getLog(), this.artifacts, this.excludes);

		resolveArtifact(this.projectArtifact.getGroupId(), this.projectArtifact.getArtifactId(), this.projectArtifact.getVersion());

		Build build = this.mavenProject.getBuild();
		File currentProjectJarFile = new File(this.outputDirectory, build.getFinalName() + ".jar"); //$NON-NLS-1$

		if (!currentProjectJarFile.exists()) {
			throw new ArtifactNotFoundException(
					"Unable to find the output jar file for the project: "+ //$NON-NLS-1$
					currentProjectJarFile.toString(), this.createArtifact(this.projectArtifact.getGroupId(), this.projectArtifact.getArtifactId(), this.projectArtifact.getVersion()));
		}
		deps.add(currentProjectJarFile);

		distributeBundles(outDir, layout, deps);
	}

	@SuppressWarnings("unchecked")
	private void detectEmbeddedArtifacts(Set<File> inputDirs) throws MojoExecutionException {
		MavenProject p = this.getMavenSession().getCurrentProject();

		List<Dependency> odeps = p.getDependencies();
		List<Dependency> deps = new ArrayList<Dependency>(odeps == null ? Collections.<Dependency> emptyList() : odeps);
		File artifactFile;
		Dependency dep;
		Artifact depArtifact;
		Iterator<Dependency> iterator;
		while (!deps.isEmpty()) {
			iterator = deps.iterator();
			dep = iterator.next();
			iterator.remove();
			if (dep != null) {
				depArtifact = resolveArtifact(dep.getGroupId(), dep.getArtifactId(), dep.getVersion());

				artifactFile = depArtifact.getFile();
				if (!Utils.isOSGiExplodedBundle(artifactFile) && !Utils.isOSGiBundle(artifactFile)) {
					// The dependency artifact should be embedded in the top artifact
					inputDirs.add(artifactFile);

					p = this.getMavenSession().getCurrentProject();
					odeps = p.getDependencies();
					if (odeps != null)
						deps.addAll(odeps);
				}
			}
		}
	}

	private File getOSGiBundle(Artifact artifact) throws IOException, MojoExecutionException {
		File depFile = artifact.getFile();
		if (Utils.isOSGiExplodedBundle(depFile)) {
			// Debugeable exploded bundle
			String targetName = Utils.getArtifactTargetName(artifact);
			File workingDir = ensureWorkingDirectory();
			File outputJar = new File(workingDir, targetName);
			FileUtils.forceDeleteOnExit(outputJar);
			if (outputJar.exists())
				FileUtils.forceDelete(outputJar);

			Set<File> inputDirs = new HashSet<File>();
			inputDirs.add(depFile);
			detectEmbeddedArtifacts(inputDirs);

			// Zipping the folder
			ZipUtils.zipDirectories(getLog(), inputDirs, outputJar);

			return outputJar;
		}

		if (Utils.isOSGiBundle(depFile)) {
			// Bundle Jar file
			return depFile;
		}

		return null;
	}

	/**
	 * Distribute the OSGi layout bundle files.
	 * 
	 * @param outDir
	 *            is the output directory, such as target/janus-dist.
	 * @param layout
	 *            is the current OSGi layout.
	 * @throws MojoExecutionException
	 * @throws ArtifactNotFoundException
	 * @throws ArtifactResolutionException
	 * @throws IOException
	 * @throws MojoFailureException
	 * @throws ProjectBuildingException
	 */
	protected void distributeDebugeableBundles(File outDir, Layout layout) throws MojoExecutionException, ArtifactResolutionException, ArtifactNotFoundException, IOException, MojoFailureException, ProjectBuildingException {
		Set<File> deps = new HashSet<File>();
		Iterator<Artifact> i = this.artifacts.iterator();
		Artifact a;
		File depFile;

		if (this.excludes != null && this.excludes.size() > 0) {
			while (i.hasNext()) {
				a = i.next();
				if (!this.excludes.contains(a.getArtifactId())) {//verify if the dependencies is not in the excluded artifiacts
					depFile = getOSGiBundle(a);
					if (depFile != null) {
						getLog().info("Bundle detected for " + a.toString() + ": " + depFile); //$NON-NLS-1$ //$NON-NLS-2$
						deps.add(depFile);
					}
				} else {
					getLog().info("Excluding " + a.toString()); //$NON-NLS-1$ 
				}
			}
		} else {
			while (i.hasNext()) {
				a = i.next();
				depFile = getOSGiBundle(a);
				if (depFile != null) {
					getLog().info("Bundle detected for " + a.toString() + ": " + depFile); //$NON-NLS-1$ //$NON-NLS-2$
					deps.add(depFile);
				}
			}

		}

		resolveArtifact(this.projectArtifact.getGroupId(), this.projectArtifact.getArtifactId(), this.projectArtifact.getVersion());

		depFile = getOSGiBundle(this.projectArtifact);
		if (depFile != null) {
			deps.add(depFile);
		} else {
			throw new MojoFailureException("Unable to find a bundle for artifact:" //$NON-NLS-1$
					+ this.projectArtifact.toString());
		}

		distributeBundles(outDir, layout, deps);
	}

	/**
	 * Distribute the OSGi layout bundle files.
	 * 
	 * @param outDir
	 *            is the output directory, such as target/janus-dist.
	 * @param layout
	 *            is the current OSGi layout
	 * @param dependencies
	 *            are the dependencies to distribute as bundles.
	 * @throws ArtifactNotFoundException
	 * @throws ArtifactResolutionException
	 * @throws IOException
	 */
	private void distributeBundles(File outDir, Layout layout, Set<File> dependencies) throws ArtifactResolutionException, ArtifactNotFoundException, IOException {
		File bundleDir = new File(outDir, layout.getBundleDir());
		bundleDir.mkdirs();

		Iterator<File> iterator = dependencies.iterator();
		while (iterator.hasNext()) {
			File d = iterator.next();
			// conf file
			getLog().info("Copying OSGi bundle: " + d.getAbsolutePath()); //$NON-NLS-1$
			FileUtils.copyFileToDirectory(d.getAbsolutePath(), bundleDir.getAbsolutePath());
		}

	}

	/**
	 * Distribute the OSGi layout configuration files.
	 * 
	 * @param outDir
	 *            is the output directory, such as target/janus-dist.
	 * @param layout
	 *            is the current OSGi layout.
	 * @throws MojoExecutionException
	 * @throws IOException
	 */
	protected void distributeConfigFiles(File outDir, Layout layout) throws MojoExecutionException, IOException {
		File originalConfDir = new File(this.configDir);
		if (!originalConfDir.isAbsolute()) {
			originalConfDir = new File(this.mavenProject.getBasedir(), this.configDir);
		}
		if (!originalConfDir.exists()) {
			StringBuilder buffer = new StringBuilder();
			buffer.append("Configuration File ["); //$NON-NLS-1$
			buffer.append(this.configDir);
			buffer.append("] can not be found. A configuration file must be provided."); //$NON-NLS-1$
			throw new MojoExecutionException(buffer.toString());
		}

		File originalLayoutDir = new File(originalConfDir.getParentFile(), layout.getName());
		getLog().debug("Resolving Absolute Path to layout directory " + originalLayoutDir.getAbsolutePath()); //$NON-NLS-1$
		if (!originalLayoutDir.exists()) {
			throw new MojoExecutionException("Layout Configuration File can not be found. A configuration file must be provided."); //$NON-NLS-1$
		}

		// Creating configuration directory
		File confDir = new File(outDir, layout.getConfigurationDir());
		confDir.mkdirs();

		// Copying the platform configuration file
		if (this.platformConfigFileName == null) {
			this.platformConfigFileName = layout.getDefaultPlatformConfigFileName();
		}

		File platformConfigFile = new File(confDir, this.platformConfigFileName);
		getLog().info("Creating platform configuration file: " + platformConfigFile.getAbsoluteFile()); //$NON-NLS-1$
		Utils.merge(originalConfDir, platformConfigFile, true);

		// Copying the layout configuration file
		if (this.layoutConfigFileName == null) {
			this.layoutConfigFileName = layout.getDefaultLayoutConfigFileName();
		}

		boolean isSameConfigFile = (this.platformConfigFileName.equals(this.launcher));

		File layoutConfigFile = new File(confDir, this.layoutConfigFileName);
		getLog().info("Creating OSGi layout configuration file: " + layoutConfigFile.getAbsoluteFile()); //$NON-NLS-1$
		Utils.merge(originalLayoutDir, layoutConfigFile, !isSameConfigFile);
	}

}
