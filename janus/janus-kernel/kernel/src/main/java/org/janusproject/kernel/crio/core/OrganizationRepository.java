/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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
package org.janusproject.kernel.crio.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.crio.organization.OrganizationFactory;
import org.janusproject.kernel.crio.organization.OrganizationInstanciationError;
import org.janusproject.kernel.repository.TreeRepository;
import org.janusproject.kernel.util.comparator.GenericComparator;

/**
 * Repository of organizations.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
final class OrganizationRepository
extends TreeRepository<Class<? extends Organization>, Organization> {

	/** Avoid public construction.
	 */
	OrganizationRepository() {
		super(GenericComparator.SINGLETON);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Class<? extends Organization> key, Organization data) {
		super.add(key, data);
		// Force the new instance of the singleton
		// to consider itself as the registered singleton. 
		data.isSingleton = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Organization remove(Class<? extends Organization> key) {
		Organization o = super.remove(key);
		// Force the previous instance of the singleton
		// to consider not more itself as the registered singleton. 
		if (o!=null) o.isSingleton = false;
		return o;
	}



	/**
	 * Replies the instance for the given organization in the context.
	 * 
	 * @param <O> is the organization that must be instanced.
	 * @param context is the current CRIO execution context.
	 * @param organization is the organization that must be instanced.
	 * @param factory is the organization factory to use, or <code>null</code> to use the
	 * default one.
	 * @param accessContext is the context of access control to use, or <code>null</code> to
	 * use the default one (OrganizationRepository access control).
	 * @return organization instance in the current context.
	 */
	private static <O extends Organization> O organization(
			CRIOContext context,
			Class<O> organization,
			OrganizationFactory<O> factory,
			AccessControlContext accessContext) {
		assert(context!=null);
		assert(organization!=null);
		
		assert(!organization.isAnonymousClass()) : organization.getCanonicalName();
		assert(!organization.isSynthetic()) : organization.getCanonicalName();
		assert(!organization.isMemberClass()
			|| Modifier.isStatic(organization.getModifiers()))
			: organization.getCanonicalName();
		
		OrganizationRepository repository = context.getOrganizationRepository();
		assert(repository!=null);
		
		synchronized(repository) {
			Organization o = repository.get(organization);
			
			if (o==null || !organization.isInstance(o)) {
				try {
					if (o!=null) {
						// Force the previous instance of the singleton
						// to consider not more itself as the registered singleton. 
						o.isSingleton = false;
						Logger.getLogger(organization.getCanonicalName()).warning(
								Locale.getString(
										OrganizationRepository.class,
										"ORGANIZATION_ALREADY_INSIDE", //$NON-NLS-1$
										organization.getCanonicalName()));
					}
					o = AccessController.doPrivileged(
							new OrganizationInstanciator<O>(context, organization, factory),
							accessContext);
					assert(o!=null);
					repository.add(organization, o);
				}
				catch(AssertionError ae) {
					throw ae;
				}				
				catch (PrivilegedActionException e) {
					Throwable t = e;
					while (t.getCause()!=null && t!=e) {
						t = t.getCause();
					}
					throw new OrganizationInstanciationError(t);
				}
			}
			
			return organization.cast(o);
		}
	}

	/**
	 * Replies the instance for the given organization in the context.
	 * 
	 * @param <O> is the organization that must be instanced.
	 * @param context is the current CRIO execution context.
	 * @param factory is the organization factory to use, or <code>null</code> to use the
	 * default one.
	 * @param accessContext is the context of access control to use, or <code>null</code> to
	 * use the default one (OrganizationRepository access control).
	 * @return organization instance in the current context.
	 */
	static <O extends Organization> O organization(
			CRIOContext context,
			OrganizationFactory<O> factory,
			AccessControlContext accessContext) {
		assert(factory!=null);
		Class<O> organization = factory.getOrganizationType();
		return organization(context, organization, factory, accessContext);
	}

	/**
	 * Replies the instance for the given organization in the context.
	 * 
	 * @param <O> is the organization that must be instanced.
	 * @param context is the current CRIO execution context.
	 * @param organization is the organization that must be instanced.
	 * @param accessContext is the context of access control to use, or <code>null</code> to
	 * use the default one (OrganizationRepository access control).
	 * @return organization instance in the current context.
	 */
	static <O extends Organization> O organization(
			CRIOContext context,
			Class<O> organization,
			AccessControlContext accessContext) {
		return organization(context, organization, null, accessContext);
	}

	/**
	 * Priviligied instanciator of organizations.
	 * 
	 * @param <O> is the type of the expected organization.
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class OrganizationInstanciator<O extends Organization>
	implements PrivilegedExceptionAction<O> {
		
		private final Class<O> type;
		private final CRIOContext context;
		private final OrganizationFactory<O> factory;
		
		/**
		 * @param context is the CRIO context in which organization may be created.
		 * @param type is the type of the expected organization.
		 * @param factory is the factory to use to create a new instance of the organization.
		 */
		public OrganizationInstanciator(CRIOContext context, Class<O> type, OrganizationFactory<O> factory) {
			this.context = context;
			this.type = type;
			this.factory = factory;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public O run() throws Exception {
			if (this.factory!=null)
				return this.factory.newInstance(this.context);
			
			Constructor<O> cons = this.type.getConstructor(CRIOContext.class);
			assert(cons!=null);
			if (!Modifier.isPublic(cons.getModifiers())) {
				Logger.getLogger(this.type.getCanonicalName()).severe(
						Locale.getString(
								OrganizationRepository.class,
								"ORGANIZATION_NOT_PUBLIC_CONSTRUCTOR", //$NON-NLS-1$
								this.type.getCanonicalName()));
			}
			return cons.newInstance(this.context);
		}
		
	}

}
