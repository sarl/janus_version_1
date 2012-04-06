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
package org.janusproject.kernel.organization.holonic.merging.predefined;

import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.organization.holonic.merging.MergingOrganization;

/**
 * This class defines a specific merging organization with predefined set of rules
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MergingPredefinedOrganization extends MergingOrganization {
	
	/**
	 * @param context
	 */
	private MergingPredefinedOrganization(CRIOContext context) {
		super(context);
		addRole(PredefinedStandAlone.class);
	}
	
}
