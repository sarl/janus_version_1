/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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
package org.janusproject.kernel.organization.holonic.merging;

import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;

/**
 * This organization manages the creation of a new super-agent from a set of
 * agent playing the StandAlone Role. The creation of a new super-agent should
 * define who will be head, part ou multi-part and the power attributed to each
 * of them in the future super-agent
 * </p>
 * One roles is defnied on it :
 * <ul>
 * <li> <code>StandAlone</code> : the agent wishing to create a new
 * super-agent.
 * </ul>
 * 
 * <p>
 * Different approaches can be used to define rules that will govern the life of
 * the super-agent; we have identified three main different methods :
 * <ul>
 * <li> Predefined : The agents were conceived so that the rules for the
 * superagent are predefined and known by members in advance. This approach may
 * be usefull when developping closed application. The adaptivity of these types
 * of system will remain constrained to the anticipated cases only, and will
 * probably prove impossible to use in large open environments.
 * 
 * <li> Negotiation : The Merging process foresees a mechanism to negotiate the
 * configuration of the super-agent. This approach allows a wider range of
 * applications, and improved adaptive capabilities. But the negotiation process
 * may induce important overheads. A mix this and the previous approach could
 * help reducing the overhead.
 * 
 * <li> Evolutive : The super-agent is created with a minimum of engagements of
 * the members. The members can then increase their commitment toward the
 * super-agent when they consider it useful. The minimal rules set contains only
 * one rule: Add new rules. Using this rule with a voting mechanism, any new
 * rule or modification of it can be obtained.
 * </ul>
 * </br></br> Each of these method is defined in a appropriate organization in
 * the lower package
 * </p>
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class MergingOrganization extends Organization {
	
	/**
	 * @param context
	 */
	public MergingOrganization(CRIOContext context) {
		super(context);
	}
	
}
