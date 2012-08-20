/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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
package org.janusproject.kernel.crio.coreTest;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.crio.organization.OrganizationFactory;
import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class OrganizationRepositoryTest extends TestCase {

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
	public static void testOrganizationClass_innerPublicClass() throws Exception {
		Constructor<MyInnerPublicClassOrgaStub> cons = MyInnerPublicClassOrgaStub.class.getConstructor(CRIOContext.class);
		assertNotNull(cons);
		
		CRIOContext context = new CRIOContext(null);
		
		Organization o = org.janusproject.kernel.crio.core.OrganizationRepositoryTest.organization(
				context,
				new MyInnerPublicClassOrgaStub.Factory(),
				null);
		assertNotNull(o);
		assertSame(o, org.janusproject.kernel.crio.core.OrganizationRepositoryTest.organization(
				context, 
				new MyInnerPublicClassOrgaStub.Factory(),
				null));
		
		CRIOContext context2 = new CRIOContext(null);
		Organization o2 = org.janusproject.kernel.crio.core.OrganizationRepositoryTest.organization(
				context2, 
				new MyInnerPublicClassOrgaStub.Factory(),
				null);
		assertNotSame(o, o2);
	}

	/**
	 * @throws Exception
	 */
	public static void testOrganizationClass_innerPackageClass() throws Exception {
		Constructor<MyInnerPackageClassOrgaStub> cons = MyInnerPackageClassOrgaStub.class.getConstructor(CRIOContext.class);
		assertNotNull(cons);
		
		CRIOContext context = new CRIOContext(null);
		
		Organization o = org.janusproject.kernel.crio.core.OrganizationRepositoryTest.organization(
				context, 
				new MyInnerPackageClassOrgaStub.Factory(),
				null);
		assertNotNull(o);
		assertSame(o, org.janusproject.kernel.crio.core.OrganizationRepositoryTest.organization(
				context, 
				new MyInnerPackageClassOrgaStub.Factory(),
				null));
		
		CRIOContext context2 = new CRIOContext(null);
		Organization o2 = org.janusproject.kernel.crio.core.OrganizationRepositoryTest.organization(
				context2, 
				new MyInnerPackageClassOrgaStub.Factory(),
				null);
		assertNotSame(o, o2);
	}

	/**
	 * @throws Exception
	 */
	public static void testOrganizationClass_innerPrivateClass() throws Exception {
		Constructor<MyInnerPrivateClassOrgaStub> cons = MyInnerPrivateClassOrgaStub.class.getConstructor(CRIOContext.class);
		assertNotNull(cons);
		
		CRIOContext context = new CRIOContext(null);
		Organization o = org.janusproject.kernel.crio.core.OrganizationRepositoryTest.organization(
				context, 
				new MyInnerPrivateClassOrgaStub.Factory(),
				null);
		assertNotNull(o);
		assertSame(o, org.janusproject.kernel.crio.core.OrganizationRepositoryTest.organization(
				context, 
				new MyInnerPrivateClassOrgaStub.Factory(),
				null));
		
		CRIOContext context2 = new CRIOContext(null);
		Organization o2 = org.janusproject.kernel.crio.core.OrganizationRepositoryTest.organization(
				context2, 
				new MyInnerPrivateClassOrgaStub.Factory(),
				null);
		assertNotSame(o, o2);
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class MyInnerPublicClassOrgaStub extends Organization {
		
		/**
		 * @param context
		 */
		public MyInnerPublicClassOrgaStub(CRIOContext context) {
			super(context);
		}
		
		/**
		 * @author St&eacute;phane GALLAND &lt;stephane.galland@utbm.fr&gt;
		 * @version $FullVersion$
		 * @mavengroupid org.janus-project.kernel
		 * @mavenartifactid crio
		 */
		public static class Factory implements OrganizationFactory<MyInnerPublicClassOrgaStub> {
			
			/**
			 */
			public Factory() {
				//
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public MyInnerPublicClassOrgaStub newInstance(
					CRIOContext context) throws Exception {
				return new MyInnerPublicClassOrgaStub(context);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Class<MyInnerPublicClassOrgaStub> getOrganizationType() {
				return MyInnerPublicClassOrgaStub.class;
			}
			
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	static class MyInnerPackageClassOrgaStub extends Organization {
		
		/**
		 * @param context
		 */
		public MyInnerPackageClassOrgaStub(CRIOContext context) {
			super(context);
		}
		
		/**
		 * @author St&eacute;phane GALLAND &lt;stephane.galland@utbm.fr&gt;
		 * @version $FullVersion$
		 * @mavengroupid org.janus-project.kernel
		 * @mavenartifactid crio
		 */
		public static class Factory implements OrganizationFactory<MyInnerPackageClassOrgaStub> {
			
			/**
			 */
			public Factory() {
				//
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public MyInnerPackageClassOrgaStub newInstance(
					CRIOContext context) throws Exception {
				return new MyInnerPackageClassOrgaStub(context);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Class<MyInnerPackageClassOrgaStub> getOrganizationType() {
				return MyInnerPackageClassOrgaStub.class;
			}

		}
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class MyInnerPrivateClassOrgaStub extends Organization {
		
		/**
		 * @param context
		 */
		public MyInnerPrivateClassOrgaStub(CRIOContext context) {
			super(context);
		}
		
		/**
		 * @author St&eacute;phane GALLAND &lt;stephane.galland@utbm.fr&gt;
		 * @version $FullVersion$
		 * @mavengroupid org.janus-project.kernel
		 * @mavenartifactid crio
		 */
		public static class Factory implements OrganizationFactory<MyInnerPrivateClassOrgaStub> {
			
			/**
			 */
			public Factory() {
				//
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public MyInnerPrivateClassOrgaStub newInstance(
					CRIOContext context) throws Exception {
				return new MyInnerPrivateClassOrgaStub(context);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Class<MyInnerPrivateClassOrgaStub> getOrganizationType() {
				return MyInnerPrivateClassOrgaStub.class;
			}
			
		}

	}

}
