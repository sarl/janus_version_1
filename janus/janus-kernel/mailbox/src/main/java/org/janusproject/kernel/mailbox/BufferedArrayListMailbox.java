/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.util.directaccess.ListUtil;

/**
 * This class provides a {@link ArrayList}-based implementation of a {@link BufferedMailbox}.
 * <p>
 * The list of mails is sorted according to the creation date of the mails.
 * <p>
 * This implementation is thread-safe when it is used from a role or an agent exclusively.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see BufferedTreeSetMailbox
 */
public class BufferedArrayListMailbox extends ArrayListMailbox implements BufferedMailbox {

	private static final long serialVersionUID = 1493582362860477646L;
	
	/** Array list.
	 */
	protected final List<Message> buffer;

	/**
	 * Create mailbox manager.
	 */
	public BufferedArrayListMailbox() {
		this.buffer = new LinkedList<Message>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(Message msg) {
		assert(msg!=null);
		synchronized(this.buffer) {
			return this.buffer.add(msg);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearBuffer() {
		synchronized(this.buffer) {
			this.buffer.clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getBufferSize() {
		synchronized(this.buffer) {
			return this.buffer.size();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isBufferEmpty() {
		synchronized(this.buffer) {
			return this.buffer.isEmpty();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeMessages() {
		synchronized(this.buffer) {
			Comparator<? super Message> cmp = comparator();
			for(Message msg : this.buffer) {
				ListUtil.dichotomicAdd(this.inbox, cmp, msg, true);
			}
			this.buffer.clear();
		}
	}

}
