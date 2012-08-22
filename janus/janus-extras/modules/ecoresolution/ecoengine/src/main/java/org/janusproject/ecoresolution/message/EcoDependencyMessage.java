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
package org.janusproject.ecoresolution.message;

import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.EcoRelation;
import org.janusproject.kernel.message.AbstractContentMessage;

/** A dependency update query message between two eco-agents or two eco-roles.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public final class EcoDependencyMessage extends AbstractContentMessage<EcoIdentity> {
	
	private static final long serialVersionUID = 2688855532348334607L;
	
	private final EcoIdentity dependency;
	private final EcoRelation feedbackPattern;
	
	/**
	 * Construct a message to add a dependency.
	 * 
	 * @param dependency is the related dependency
	 * @param feedbackPattern is the pattern to use to obtain a feedback from the dependency
	 */
	public EcoDependencyMessage(EcoIdentity dependency, EcoRelation feedbackPattern) {
		this.dependency = dependency;
		this.feedbackPattern = feedbackPattern;
	}

	/**
	 * Construct a message to remove a dependency.
	 * 
	 * @param dependency is the related dependency
	 */
	public EcoDependencyMessage(EcoIdentity dependency) {
		this.dependency = dependency;
		this.feedbackPattern = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EcoIdentity getContent() {
		return this.dependency;
	}
	
	/** Replies if this message is for creating a dependency, or removing a dependency.
	 * 
	 * @return <code>true</code> if the dependency must be created, otherwise <code>false</code>.
	 */
	public boolean isDependencyCreation() {
		return this.feedbackPattern!=null;
	}
	
	/** Replies if the pattern to use to feedback the knowledge of the dependent entity.
	 * 
	 * @return the pattern that should be used by the receiver of this message to send back
	 * its unknowledge.
	 */
	public EcoRelation getFeedBackPattern() {
		return this.feedbackPattern;
	}

}