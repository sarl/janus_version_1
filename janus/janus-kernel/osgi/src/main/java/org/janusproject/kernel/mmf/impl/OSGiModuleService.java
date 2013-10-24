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
package org.janusproject.kernel.mmf.impl;

import java.net.URL;
import java.util.logging.Logger;

import org.apache.felix.bundlerepository.RepositoryAdmin;
import org.apache.felix.bundlerepository.Resolver;
import org.apache.felix.bundlerepository.Resource;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.mmf.JanusModule;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * The module service resolves OSGi bundles, local or on a remote OBR
 * repository.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class OSGiModuleService {

	private final BundleContext context;

	private final RepositoryAdmin repositoryAdmin;
	private final PackageAdmin packageAdmin;

	/**
	 * @param context
	 */
	public OSGiModuleService(BundleContext context) {
		this.context = context;
		RepositoryAdmin ra = null;
		try {
			ra = (RepositoryAdmin) getService(RepositoryAdmin.class.getName(), null);
		}
		catch(AssertionError ae) {
			throw ae;
		}
		catch (InvalidSyntaxException e) {
			//
		}
		this.repositoryAdmin = ra;
		ServiceReference ref2 = context.getServiceReference(PackageAdmin.class
				.getName());
		this.packageAdmin = (PackageAdmin) context.getService(ref2);
	}

	/**
	 * Resolves a bundle using a symbolic name and a Version range. This
	 * includes a search using OBR service. If no version range is provided the
	 * first available version corresponding to the version range is returned.
	 * 
	 * @param symbolicName
	 *            the symbolic name of the bundle.
	 * @param versionRange
	 *            a version range following the standard OSGi filter. If
	 *            <code>null</code> all available versions are retrieved.
	 *            However only the first is returned.
	 * @return a bundle respecting the <code>symbolicName</code> and the
	 *         <code>versionRange</code>
	 * @throws Exception
	 */
	public Bundle resolveBundle(String symbolicName, String versionRange)
			throws Exception {
		// try to resolve it from the local repository
		Bundle bundle = resolveBundle(symbolicName, versionRange, this.packageAdmin);
		if (bundle == null) {
			Logger logger = Logger.getLogger(getClass().getCanonicalName());
			logger.fine(Locale.getString(OSGiModuleService.class,
					"BUNDLE_NOT_FOUND", //$NON-NLS-1$
					symbolicName, versionRange));
			if (installRemoteBundle(symbolicName, versionRange)) {
				bundle = resolveBundle(symbolicName, versionRange, this.packageAdmin);
			}
		}
		return bundle;
	}

	/**
	 * Adds a repository URL to the OBR.
	 * 
	 * @param url
	 * @throws Exception
	 */
	public void addRepository(URL url) throws Exception {
		this.repositoryAdmin.addRepository(url);
	}

	/**
	 * Removes the repository to the OBR.
	 * 
	 * @param url
	 */
	public void removeRepository(URL url) {
		this.repositoryAdmin.removeRepository(url.toString());
	}

	/**
	 * Creates a standard OSGi string filter to find a bundle using its
	 * symbolicname and (optionally) a version range. If no version range is
	 * provided all available versions are retrieved.
	 * 
	 * @param symbolicName
	 * @param versionRange
	 * @return
	 */
	private static String createFilter(String symbolicName, String versionRange) {
		StringBuilder filterStr = new StringBuilder();
		filterStr.append("(|(presentationname=*)(symbolicname="); //$NON-NLS-1$
		filterStr.append(symbolicName);
		filterStr.append(")(version="); //$NON-NLS-1$
		if (versionRange == null) {
			filterStr.append("*"); //$NON-NLS-1$
		} else {
			filterStr.append(versionRange);
		}
		filterStr.append("))"); //$NON-NLS-1$
		return filterStr.toString();
	}

	/**
	 * Requests the OBR service to install a remote bundle.
	 * 
	 * @param symbolicName
	 * @param versionRange
	 * @return
	 * @throws InvalidSyntaxException
	 */
	private boolean installRemoteBundle(String symbolicName, String versionRange)
			throws InvalidSyntaxException {

		Resolver resolver = this.repositoryAdmin.resolver();
		Resource[] resources = this.repositoryAdmin
				.discoverResources(createFilter(symbolicName, versionRange));
		if (resources != null && resources.length > 0) {
			resolver.add(resources[0]);
			if (resolver.resolve()) {
				resolver.deploy(0);
				return true;
			}
		}
		return false;
	}

	/**
	 * Tries to resolve a bundle using the local repository.
	 * 
	 * @param symbolicName
	 *            The symbolicName of the bundle
	 * @param versionRange
	 *            a version range using the
	 * @param packageAdmin
	 * @return A local bundle or null if not found
	 */
	private static Bundle resolveBundle(String symbolicName, String versionRange,
			PackageAdmin packageAdmin) {
		if (packageAdmin != null) {
			Bundle[] bs = packageAdmin.getBundles(symbolicName, versionRange);
			if (bs != null && bs.length > 0) {
				return bs[0];
			}
		}

		return null;
	}

	/** Replies the OSGi bundle of the given type.
	 * 
	 * @param clazz
	 * @return the OSGi bundle of the given type.
	 */
	public Bundle getBundleForClass(Class<?> clazz) {
		return this.packageAdmin.getBundle(clazz);
	}
	
	/** Replies the Janus module of the given type.
	 * 
	 * @param module
	 * @return the Janus module of the given type, or <code>null</code> if
	 * the module was not found.
	 */
	public JanusModule getJanusModule(Class<? extends JanusModule> module) {
		StringBuilder filter = new StringBuilder();
		filter.append("(objectclass="); //$NON-NLS-1$
		filter.append(module.getName());
		filter.append(")"); //$NON-NLS-1$
		try {
			return (JanusModule) getService(JanusModule.class.getName(), filter.toString());
		}
		catch(AssertionError ae) {
			throw ae;
		}
		catch (InvalidSyntaxException _) {
			//
		}

		return null;
	}
	
	private Object getService(String serviceName, String filter) throws InvalidSyntaxException{
		ServiceReference[] ref = this.context.getServiceReferences(serviceName,filter);
		if(ref != null && ref.length>0){
			return this.context.getService(ref[0]);
		}
		
		return null;
	}

}
