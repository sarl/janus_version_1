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

import org.janusproject.ecoresolution.relation.EcoRelation;
import org.janusproject.kernel.message.AbstractContentMessage;

/** An acquaintance update query message between two eco-agents or two eco-roles.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public final class EcoAcquaintanceMessage extends AbstractContentMessage<EcoRelation> {
	
	private static final long serialVersionUID = 5031466099781751329L;
	
	private final EcoRelation relation;
	private final boolean knowledgeAddition;
	
	/**
	 * @param relation is the relation to put in this message.
	 * @param isKnowledgeAddition indicates if the given acquaintance should be added to the knowledge. If
	 * <code>false</code> the acquaintance should be removed from the knowledge.
	 */
	public EcoAcquaintanceMessage(EcoRelation relation, boolean isKnowledgeAddition) {
		this.relation = relation;
		this.knowledgeAddition = isKnowledgeAddition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EcoRelation getContent() {
		return this.relation;
	}
	
	/** Replies if the given acquaintance should be added to the knowledge.
	 * 
	 * @return <code>true</code> if the acquaintance should be added to the knowledge.
	 * <code>false</code> if the acquaintance should be removed from the knowledge.
	 */
	public boolean isKnowledgeAddition() {
		return this.knowledgeAddition;
	}
	
	/** Replies if the given acquaintance should be removed from the knowledge.
	 * 
	 * @return <code>false</code> if the acquaintance should be added to the knowledge.
	 * <code>true</code> if the acquaintance should be removed from the knowledge.
	 */
	public boolean isKnowledgeRemoval() {
		return !this.knowledgeAddition;
	}

}