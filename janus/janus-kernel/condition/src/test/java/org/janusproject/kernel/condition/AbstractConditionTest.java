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
package org.janusproject.kernel.condition;

import java.util.Random;

import org.janusproject.kernel.condition.AbstractCondition;
import org.janusproject.kernel.condition.ConditionParameterProvider;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AbstractConditionTest extends TestCase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
	}
	
	/**
	 */
	public void testGetParameterCount() {
		Random rnd = new Random();
		for(int i=0; i<200; ++i) {
			int n = rnd.nextInt();
			AbstractCondition<ConditionParameterProvider> cond = new AbstractConditionStub(n);
			assertEquals(n, cond.getConditionParameterCount());
		}
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class AbstractConditionStub extends AbstractCondition<ConditionParameterProvider> {

		private static final long serialVersionUID = -7336034753198484687L;

		public AbstractConditionStub(int count) {
			super(count);
		}

		@Override
		public boolean evaluate(ConditionParameterProvider object) {
			return false;
		}

		@Override
		public ConditionFailure evaluateFailure(ConditionParameterProvider object) {
			return new FalseCondition();
		}

	}

}
