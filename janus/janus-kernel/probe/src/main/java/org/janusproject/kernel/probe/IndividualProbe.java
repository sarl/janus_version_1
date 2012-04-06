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

import org.janusproject.kernel.address.AgentAddress;

/** A probe permits to obtain values from one single agent.
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
public interface IndividualProbe extends Probe {

	/** Replies the address of the probed agent.
	 * 
	 * @return the address of the probed agent.
	 * @since 0.5
	 */
	public AgentAddress getWatchedObject();
	
	/** Replies a probed value.
	 * 
	 * @param <T> is the type of the watched value to reply.
	 * @param probeName is the name of watched attribute to return.
	 * @param clazz is the type of the value to reply.
	 * @return the value of the watched attribute, or <code>null</code> if the value does not exist. 
	 */
	public <T> T getProbeValue(String probeName, Class<T> clazz);

	/** Replies a probed value.
	 * 
	 * @param probeName is the name of watched attribute to return.
	 * @return the value of the watched attribute, or <code>null</code> if the value does not exist.
	 * @since 0.5 
	 */
	public Object getProbeValue(String probeName);

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param <T> is the type of the value to reply.
	 * @param probeName is the name of the probed value.
	 * @param clazz is the type of the expected value.
	 * @return the value or <code>null</code>
	 */
	public <T> T[] getProbeArray(String probeName, Class<T> clazz);

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public int getProbeInt(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public long getProbeLong(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public float getProbeFloat(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public double getProbeDouble(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public boolean getProbeBool(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public char getProbeChar(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public byte getProbeByte(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public short getProbeShort(String probeName) throws ProbeException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeException when the value of the watched attribute was not set.
	 */
	public String getProbeString(String probeName) throws ProbeException;
	
}