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
package org.janusproject.kernel.probe;

import java.util.Map;

import org.janusproject.kernel.address.AgentAddress;

/** A probe permits to obtain information on a collection of agents.
 * <p>
 * A probe must not be invasive and the agent is
 * able to ignore them (ie. to not provide information).
 * <p>
 * This interface is strongly inspirated by the <code>Probe</code>
 * interface from the
 * <a href="http://www.arakhne.org/tinyMAS/">tinyMAS project</a>.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface CollectiveProbe extends Probe {

	/** Replies the addresses of the probed agents.
	 * 
	 * @return the addresses of the probed agents.
	 * @since 0.5
	 */
	public AgentAddress[] getWatchedObjects();

	/** Replies a probed value.
	 * 
	 * @param <T> is the type of the watched value to reply.
	 * @param probeName is the name of the probed value
	 * @param clazz is the type of the watched value to reply.
	 * @return the value
	 */
	public <T> Map<AgentAddress,T> getProbeValue(String probeName, Class<T> clazz);

	/** Replies a probed value.
	 * 
	 * @param probeName is the name of the probed value
	 * @return the value
	 * @since 0.5
	 */
	public Map<AgentAddress,Object> getProbeValue(String probeName);

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param <T> is the type of the watched value to reply.
	 * @param probeName is the name of the probed value.
	 * @param clazz is the type of the expected value.
	 * @return the value or <code>null</code>
	 */
	public <T> Map<AgentAddress,T[]> getProbeArray(String probeName, Class<T> clazz);

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public Map<AgentAddress,Integer> getProbeInt(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public Map<AgentAddress,Long> getProbeLong(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public Map<AgentAddress,Float> getProbeFloat(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public Map<AgentAddress,Double> getProbeDouble(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public Map<AgentAddress,Boolean> getProbeBool(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public Map<AgentAddress,Character> getProbeChar(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public Map<AgentAddress,Byte> getProbeByte(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public Map<AgentAddress,Short> getProbeShort(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public Map<AgentAddress,String> getProbeString(String probeName) throws ProbeException;
	
}