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
package org.janusproject.kernel.agent;

import java.util.Collection;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.GroupAddress;

/**
 * This interface provides the services of an holon.
 * <p>
 * During 80's, Arthur Koestler proposed the word
 * "holon"<sup><a href="#reference_koestler">1</a></sup>.
 * It is a combination from the Greek holos = whole, with 
 * the suffix on which, as in proton or neutron, suggests 
 * a particle or part.
 * <p>
 * Two observations impelled Koestler to propose the word
 * holon. The first comes from Herbert
 * Simon<sup><a href="#reference_simon">2</a></sup>, a Nobel 
 * prize winner, and is based on his 'parable of the two 
 * watchmakers'. From this parable, Simon concludes that 
 * complex systems will evolve from simple systems much 
 * more rapidly if there are stable intermediate forms 
 * than if there are not; the resulting complex systems 
 * in the former case will be hierarchic.
 * <p>
 * The second observation, made by Koestler while analysing 
 * hierarchies and stable intermediate forms in living 
 * organisms and social organisation, is that although it 
 * is easy to identify sub-wholes or parts 'wholes' and 
 * 'parts' in an absolute sense do not exist anywhere. 
 * This made Koestler propose the word holon to describe 
 * the hybrid nature of sub- wholes/parts in real-life 
 * systems; holons simultaneously are self-contained wholes 
 * to their subordinated parts, and dependent parts when 
 * seen from the inverse direction.
 * <p>
 * Koestler also establishes the link between holons and 
 * the watchmakers' parable from professor Simon. He 
 * points out that the sub-wholes/holons are autonomous 
 * self-reliant units, which have a degree of independence 
 * and handle contingencies without asking higher authorities 
 * for instructions. Simultaneously, holons are subject 
 * to control from (multiple) higher authorities. The first 
 * property ensures that holons are stable forms, which 
 * survive disturbances. The latter property signifies that 
 * they are intermediate forms, which provide the proper 
 * functionality for the bigger whole.
 * <p>
 * Finally, Koestler defines a holarchy as a hierarchy of 
 * self-regulating holons which function (a) as autonomous 
 * wholes in supra-ordination to their parts, (b) as 
 * dependent parts in sub- ordination to controls on 
 * higher levels, (c) in coordination with their local 
 * environment.
 * <p>
 * <div id="reference_koestler">[1] A. KOESTLER, The GHOST
 * in the MACHINE, Arkana Books, 1989.</div><br>
 * <div id="reference_simon">[2] H. A. SIMON, The Sciences
 * of the Artificial, 6th ed., MIT Press Cambridge (Mass.),
 * 1990, ISBN 0-26269073-X.</div>
 *  
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Holon {
	
	/**
	 * Replies the address of this agent/holon.
	 * 
	 * @return the address of this agent/holon.
	 */
	public AgentAddress getAddress();

	/**
	 * Replies the CRIO context in which is agent/holon is living.
	 * 
	 * @return the CRIO context in which this this agent/holon is living.
	 */
	public CRIOContext getCRIOContext();

	/**
	 * Replies the Kernel context in which is agent/holon is living.
	 * 
	 * @return the Kernel context in which this this agent/holon is living.
	 */
	public KernelContext getKernelContext();

	/**
	 * Replies if the agent/holon is alive.
	 * 
	 * @return <code>true</code> if the agent/holon is alive,
	 * otherwise <code>false</code>
	 */
	public boolean isAlive();

	/** Replies if this agent/holon is able to commit suicide.
	 * <p>
	 * When an agent/holon is able to commit suicide is will automatically
	 * kill itself when its has no more role to play.
	 * By default an agent/holon is able to commit suicide according to
	 * the value of the variable {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @return <code>true</code> if the agent/holon wants to commit a suicide at least,
	 * otherwise <code>false</code>
	 */
	public boolean canCommitSuicide();

	/**
	 * Replies the state of the agent/holon.
	 * 
	 * @return the state of the agent/holon.
	 */
	public AgentLifeState getState();

	/**
	 * Returns the address of the agent/holon who has created this agent/holon.
	 * 
	 * @return the address of the agent/holon who has created this agent/holon,
	 * or <code>null</code> if this agent/holon is a root in holarchy.
	 */
	public AgentAddress getCreator();

	/**
	 * Returns the date at which the agent was created and put inside
	 * the Janus kernel.
	 * 
	 * @return the date at which the agent was created and put inside
	 * the Janus kernel.
	 */
	public float getCreationDate();

	/**
	 * Returns <tt>true</tt> if this agent/holon is a super-agent/holon : compound of
	 * agents/holons and organizations else <tt>false</tt>
	 * 
	 * @return <tt>true</tt> if this agent/holon is a super-agent/holon : compound of
	 *         agents/holons and organizations
	 */
	public boolean isCompound();

	/**
	 * Returns <tt>true</tt> if this agent/holon is able to recruit new members.<br>
	 * An agent/holon can recruit only if he contains a IntegrationOrganization in
	 * charge of managing recruitment interactions
	 * 
	 * @return <tt>true</tt> if this agent/holon is able to recruit new members.
	 */
	public boolean isRecruitmentAllowed();

	/**
	 * Returns the holonic orgnaization of the agent/holon : <br>
	 * its describes the power distribution and the decision making procedure
	 * into this agent/holon.
	 * 
	 * @return the address of the holonic group or <code>null</code> if none
	 */
	public GroupAddress getHolonicOrganization();

	/**
	 * Returns the list of the address of the various groups (excepted holonic
	 * and merging organization, goals-dependent only) contained in this agent/holon.
	 * 
	 * @return the addresses of the various groups contained in this
	 *         agent/holon.
	 */
	public Collection<GroupAddress> getInternalOrganizations();

	/**
	 * Returns the merging organization in charge of managing recruitment
	 * interactions
	 * 
	 * @return the address of the group managing the recruitment in this agent/holon,
	 * or <code>null</code> if none.
	 */
	public GroupAddress getMergingOrganization();

}
