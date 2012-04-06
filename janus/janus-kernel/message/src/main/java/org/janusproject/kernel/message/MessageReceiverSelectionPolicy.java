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

import java.util.List;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.util.directaccess.DirectAccessCollection;
import org.janusproject.kernel.util.random.RandomNumber;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * This interface is used to provide a policy to
 * select a message receiver.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface MessageReceiverSelectionPolicy {

	/** Replies an entity according to the internal selection policy.
	 *
	 * @param sender is the sender of the message.
	 * @param availableEntities are the set of available entities, including the sender.
	 * @return the selected entity.
	 */
	public AgentAddress selectEntity(
			AgentAddress sender,
			List<? extends AgentAddress> availableEntities);

	/** Replies an entity according to the internal selection policy.
	 *
	 * @param sender is the sender of the message.
	 * @param availableEntities are the set of available entities, including the sender.
	 * @return the selected entity.
	 */
	public AgentAddress selectEntity(
			AgentAddress sender,
			DirectAccessCollection<? extends AgentAddress> availableEntities);

	/** Replies an entity according to the internal selection policy.
	 *
	 * @param sender is the sender of the message.
	 * @param availableEntities are the set of available entities, including the sender.
	 * @return the selected entity.
	 */
	public AgentAddress selectEntity(
			AgentAddress sender,
			SizedIterator<? extends AgentAddress> availableEntities);

	/** 
	 * Random selection policy.
	 */
	public static final MessageReceiverSelectionPolicy RANDOM_SELECTION = new MessageReceiverSelectionPolicy() {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress selectEntity(
				AgentAddress sender,
				List<? extends AgentAddress> availableEntities) {
			if (availableEntities!=null && !availableEntities.isEmpty()) {
				AgentAddress adr;
				int n;
				while (true) {
					n = RandomNumber.nextInt(availableEntities.size());
					adr = availableEntities.get(n);
					if (!adr.equals(sender))
						return adr;
					if (availableEntities.size()==1)
						return null;
				}
			}
			return null;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress selectEntity(
				AgentAddress sender,
				DirectAccessCollection<? extends AgentAddress> availableEntities) {
			if (availableEntities!=null && !availableEntities.isEmpty()) {
				AgentAddress adr;
				int n;
				while (true) {
					n = RandomNumber.nextInt(availableEntities.size());
					adr = availableEntities.get(n);
					if (!adr.equals(sender))
						return adr;
					if (availableEntities.size()==1)
						return null;
				}
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress selectEntity(
				AgentAddress sender,
				SizedIterator<? extends AgentAddress> availableEntities) {
			AgentAddress selectedAdr = null;
			if (availableEntities!=null) {
				AgentAddress adr;
				while (availableEntities.hasNext()) {
					adr = availableEntities.next();
					if (!adr.equals(sender)) {
						if (selectedAdr==null || RandomNumber.nextBoolean()) selectedAdr = adr;
						if (RandomNumber.nextBoolean()) {
							return selectedAdr;
						}
					}
				}
			}
			return selectedAdr;
		}

	};
	
}