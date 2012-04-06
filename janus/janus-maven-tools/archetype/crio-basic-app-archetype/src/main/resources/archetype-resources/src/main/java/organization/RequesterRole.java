#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* 
 * ${symbol_dollar}Id${symbol_dollar}
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
package ${package}.organization;

import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.message.MessageException;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * @author ${symbol_dollar}Author: srodriguez${symbol_dollar}
 * @version ${symbol_dollar}Name${symbol_dollar} ${symbol_dollar}Revision${symbol_dollar} ${symbol_dollar}Date${symbol_dollar}
 * @mavengroupid ${symbol_dollar}GroupId${symbol_dollar}
 * @mavenartifactid ${symbol_dollar}ArtifactId${symbol_dollar}
 */
public class RequesterRole extends Role {

	private static final int MAX_RESPONSES = 5;
	private int responses = 0;

	/** {@inheritDoc}
	 */
	public Status live() {

		//First send a request
		try {
			if (responses < MAX_RESPONSES) {
				print("Requesting date...");
				//we send a message to any agent playing the DateProviderRole
				//Notice that the content of the message can be anything
				//except from "done"
				sendMessage(DateProviderRole.class, new StringMessage("date?"));

			} else {
				//Inform the Provider that we are done.
				sendMessage(DateProviderRole.class, new StringMessage("done"));
				//Tell the agent to leave the role. The RequesterRole is done with its mission.
				leaveMe();
			}
		}
		catch(AssertionError ae) {
			throw ae;
		}
		catch (MessageException e) {
			e.printStackTrace();
		}
		
		//check for responses
		DateMessage dm = (DateMessage) getMessage();

		if (dm != null) {
			print("Current date is " + dm.getContent().toString());
			responses++;
		}
		return StatusFactory.ok(this);
	}

}
