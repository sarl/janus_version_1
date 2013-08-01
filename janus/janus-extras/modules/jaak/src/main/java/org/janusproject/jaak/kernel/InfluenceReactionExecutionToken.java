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
package org.janusproject.jaak.kernel;

/** Token used to control the execution process in an
 * Influence-Reaction process.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class InfluenceReactionExecutionToken implements InfluenceReactionExecutionTokenOwner {

	private InfluenceReactionExecutionTokenOwner owner = null;
	
	/**
	 */
	public InfluenceReactionExecutionToken() {
		//
	}
	
	/** Replies the owner of the token.
	 * 
	 * @return the owner of the token.
	 */
	public synchronized InfluenceReactionExecutionTokenOwner getOwner() {
		return this.owner;
	}
	
	/** Replies if the given object is the owner of the token.
	 * 
	 * @param obj
	 * @return <code>true</code> if <var>obj</var> is the owner
	 * of the token, otherwise <code>false</code>.
	 */
	public synchronized boolean isOwner(InfluenceReactionExecutionTokenOwner obj) {
		return obj!=null && this.owner==obj;
	}
	
	/** Get the token. This function does not block the calling thread.
	 * This function returns immediately and does not change the token owner.
	 * 
	 * @param obj is the new owner of the token.
	 * @return <code>true</code> if the given object is the new object, otherwise
	 * <code>false</code> if the token is not free.
	 */
	public synchronized boolean hasToken(InfluenceReactionExecutionTokenOwner obj) {
		return (obj!=null && this.owner==obj);
	}

	/** Release the token.
	 * 
	 * @param obj is the object which want to release the token.
	 * @param newObj is the new object which want to get the token.
	 * @return <code>true</code> if the token was released, otherwise
	 * <code>false</code> if the token is not owned by <var>obj</var>.
	 */
	public synchronized boolean moveToken(InfluenceReactionExecutionTokenOwner obj, InfluenceReactionExecutionTokenOwner newObj) {
		if (this.owner==obj) {
			this.owner = newObj;
			return true;
		}
		return false;
	}

}