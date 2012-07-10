/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010 Janus Core Developers
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
package org.janusproject.demos.market.selective.capacity;

import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.capacity.CapacityPrototype;

/**
 * Identify the best proposal according to the cost parameter among a list of proposal. 
 * Identify the proposal with the lowest cost in a list of proposals.
 * <p>
 * <table>
 * <tr><td>Input</td><td>{@link Proposal}...</td><td>a list of proposals</td><tr>
 * <tr><td>Output</td><td>{@link Proposal}</td>
 *     <td>the proposal with the lowest cost of the previous list</td></tr>
 * </table>
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@CapacityPrototype(
		variableParameters=Proposal.class,
		fixedOutput=Proposal.class
)
public interface FindLowestCostProposalCapacity extends Capacity
{

	//

}
