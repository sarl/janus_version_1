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
package org.janusproject.kernel.message;

/** A message that embbeds an enumeration value.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class EnumMessage extends AbstractContentMessage<Enum<?>> {
	
	private static final long serialVersionUID = 843955055802539867L;
	
	private final Enum<?> content;
	
	/**
	 * @param o the content of the message.
	 */
	public EnumMessage(Enum<?> o) {
		this.content = o;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Enum<?> getContent() {
		return this.content;
	}
	
	/** Replies the boolean value embedded in this message.
	 * 
	 * @param <T> is the type of the enumeration.
	 * @param type is the type of the enumeration.
	 * @return the content of this message, or <code>null</code>.
	 */
	public <T extends Enum<?>> T enumValue(Class<T> type) {
		if (this.content!=null && type.isInstance(this.content)) {
			return type.cast(this.content);
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(super.toString());
		buf.append('=');
		buf.append(this.content==null ? null : this.content.name());
		return buf.toString();
	}

}