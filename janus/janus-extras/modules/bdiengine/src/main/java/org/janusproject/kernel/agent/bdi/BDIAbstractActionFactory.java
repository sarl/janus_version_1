/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-2012 Janus Core Developers
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
package org.janusproject.kernel.agent.bdi;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Abstract action factory.
 * Only one instance of each action is stored in the action factory.
 * 
 * @author $Author: matthias.brigaud@gmail.com$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public abstract class BDIAbstractActionFactory implements BDIActionFactory{
	/**
	 * Store the instanciated actions
	 */
	protected Map<Class<? extends BDIAction>, SoftReference<BDIAction>> actions = 
		new HashMap<Class<? extends BDIAction>, SoftReference<BDIAction>>();

	/** {@inheritDoc}
	 * If the action is in the map, return it.
	 * Otherwise create and add to the map the action.
	 */
	@Override
	public final BDIAction getAction(BDIAgent agent, Class<? extends BDIAction> a) {
		if (!this.actions.containsKey(a) || this.actions.get(a) == null)
			this.actions.put(a, new SoftReference<BDIAction>(createAction(agent, a)));
		
		return this.actions.get(a).get();
	}

	/** {@inheritDoc}
	 * 
	 */
	@Override
	public final Collection<BDIAction> getActions() {
		Collection<SoftReference<BDIAction>> actionReferenceList = this.actions.values();
		Collection<BDIAction> actionList = new LinkedList<BDIAction>();
		
		for (SoftReference<BDIAction> a : actionReferenceList) {
			if (a.get() != null)
				actionList.add(a.get());
		}
		
		return actionList;
	}
	
	/**
	 * Instanciate an action
	 * @param agent is the BDIAgent calling the method
	 * @param action is the action's class we want to create
	 * @return an instanciated action
	 */
	public abstract BDIAction createAction(BDIAgent agent, Class<? extends BDIAction> action);
}
