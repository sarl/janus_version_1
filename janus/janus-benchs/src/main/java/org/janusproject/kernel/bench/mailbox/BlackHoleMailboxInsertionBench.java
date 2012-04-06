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
package org.janusproject.kernel.bench.mailbox;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import org.janusproject.kernel.mailbox.BlackHoleMailbox;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.message.Message;

/** Run the bench on the mailbox API.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class BlackHoleMailboxInsertionBench extends AbstractMailboxInsertionBench {

	/**
	 * @param directory
	 * @throws IOException
	 */
	public BlackHoleMailboxInsertionBench(File directory) throws IOException {
		super(directory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Mailbox createMailbox(Comparator<? super Message> comparator) {
		return new BlackHoleMailbox();
	}
		
}