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
package org.janusproject.kernel.mailbox;

import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.util.selector.TypeSelector;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MailTypeSelectorTest extends TestCase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
	}
	
	/**
	 */
	public void testIsSelectedMail() {
		TypeSelector<MessageStub> selector1 = new TypeSelector<>(MessageStub.class);
		MessageStub m1 = new MessageStub(1f, "m1"); //$NON-NLS-1$
		MessageStub m2 = new MessageStub(1f, "m2"); //$NON-NLS-1$
		MessageStub2 m3 = new MessageStub2(1f, "m3"); //$NON-NLS-1$
		
		assertTrue(selector1.isSelected(m1));
		assertTrue(selector1.isSelected(m2));
		assertFalse(selector1.isSelected(m3));
	}

}
