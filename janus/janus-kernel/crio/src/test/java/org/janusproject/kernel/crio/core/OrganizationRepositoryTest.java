/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
import java.security.AccessControlContext;
import java.util.logging.Level;

import org.janusproject.kernel.crio.organization.OrganizationFactory;
import org.janusproject.kernel.crio.organization.OrganizationInstanciationError;
import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class OrganizationRepositoryTest extends TestCase {

	/** Helper to instanciate organizations.
	 * 
	 * @param <O>
	 * @param context
	 * @param organization
	 * @param accessControl
	 * @return an organization
	 */
	public static <O extends Organization> O organization(
			CRIOContext context, 
			Class<O> organization,
			AccessControlContext accessControl) {
		return OrganizationRepository.organization(context, organization, accessControl);
	}
	
	/** Helper to instanciate organizations.
	 * 
	 * @param <O>
	 * @param context
	 * @param factory
	 * @param accessControl
	 * @return an organization
	 */
	public static <O extends Organization> O organization(
			CRIOContext context, 
			OrganizationFactory<O> factory,
			AccessControlContext accessControl) {
		return OrganizationRepository.organization(context, factory, accessControl);
	}

	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * @throws Exception
	 */
	public static void testOrganizationClass_topclass() throws Exception {
		Constructor<TopClassOrganizationStub> cons = TopClassOrganizationStub.class.getConstructor(CRIOContext.class);
		assertNotNull(cons);
		
		CRIOContext context = new CRIOContext(null);
		Organization o = OrganizationRepository.organization(
				context, 
				TopClassOrganizationStub.class,
				null);
		assertNotNull(o);
		assertSame(o, OrganizationRepository.organization(
				context, 
				TopClassOrganizationStub.class, 
				null));
		
		CRIOContext context2 = new CRIOContext(null);
		Organization o2 = OrganizationRepository.organization(
				context2, 
				TopClassOrganizationStub.class,
				null);
		assertNotSame(o, o2);
	}
		
	/**
	 * @throws Exception
	 */
	public static void testOrganizationClass_innerclass() throws Exception {
		Constructor<MyInnerClassOrgaStub> cons = MyInnerClassOrgaStub.class.getConstructor(CRIOContext.class);
		assertNotNull(cons);
		
		CRIOContext context = new CRIOContext(null);
		Organization o = OrganizationRepository.organization(
				context, 
				MyInnerClassOrgaStub.class,
				null);
		assertNotNull(o);
		assertSame(o, OrganizationRepository.organization(
				context, 
				MyInnerClassOrgaStub.class, 
				null));
		
		CRIOContext context2 = new CRIOContext(null);
		Organization o2 = OrganizationRepository.organization(
				context2, 
				MyInnerClassOrgaStub.class, 
				null);
		assertNotSame(o, o2);
	}

	/**
	 * @throws Exception
	 */
	public static void testOrganizationClass_innerclass_protected() throws Exception {
		CRIOContext context = new CRIOContext(null);
		try {
			OrganizationRepository.organization(
					context, 
					MyProtectedInnerClassOrgaStub.class,
					null);
			fail("expecting exception OrganizationInstanciationError"); //$NON-NLS-1$
		}
		catch(OrganizationInstanciationError _) {
			// expected exception
		}
		
		CRIOContext context2 = new CRIOContext(null);
		try {
			OrganizationRepository.organization(
				context2, 
				MyProtectedInnerClassOrgaStub.class, 
				null);
			fail("expecting exception OrganizationInstanciationError"); //$NON-NLS-1$
		}
		catch(OrganizationInstanciationError _) {
			// expected exception
		}
	}

	/**
	 * @throws Exception
	 */
	public static void testOrganizationClass_topclass_protected() throws Exception {
		CRIOContext context = new CRIOContext(null);
		try {
			OrganizationRepository.organization(
					context, 
					Organization3Stub.class,
					null);
			fail("expecting exception OrganizationInstanciationError"); //$NON-NLS-1$
		}
		catch(OrganizationInstanciationError _) {
			// expected exception
		}
		
		CRIOContext context2 = new CRIOContext(null);
		try {
			OrganizationRepository.organization(
				context2, 
				Organization3Stub.class, 
				null);
			fail("expecting exception OrganizationInstanciationError"); //$NON-NLS-1$
		}
		catch(OrganizationInstanciationError _) {
			// expected exception
		}
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class MyInnerClassOrgaStub extends Organization {
		
		/**
		 * @param context
		 */
		public MyInnerClassOrgaStub(CRIOContext context) {
			super(context);
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class MyProtectedInnerClassOrgaStub extends Organization {
		
		/**
		 * @param context
		 */
		protected MyProtectedInnerClassOrgaStub(CRIOContext context) {
			super(context);
		}
		
	}

}
