/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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
package org.janusproject.kernel.agentmemory;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * Implementation of an agent memory using a direct access to
 * the getter/setter of the agent Java class.
 * <p>
 * This class tries to use the following functions for the referenced object:
 * <ul>
 * <li><code>Object getK&lt;name&gt;()</code>, where <code>&lt;name&gt;</code> is the name of the knowledge,</li>
 * <li><code>Object setK&lt;name&gt;(Object value)</code>, where <code>&lt;name&gt;</code> is the name of the knowledge.</li>
 * </ul>
 * <p>
 * <code>JavaReflectionMemory</code> is not synchronized.
 * 
 * @param <OBJ> is the type of the object to access.
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JavaReflectionMemory<OBJ> extends AbstractMemory {

	/** Replies the strin that could be used inside a Java function name
	 * and that corresponds to the given knowledge name.
	 * <p>
	 * This function discards all characters that are not a letter, a number or
	 * the underscore character.
	 * 
	 * @param knowledgeName is the name of the knowledge to format
	 * @return the name of the knowledge without invalid character.
	 */
	public static String formatKnowledgeName(String knowledgeName) {
		return knowledgeName.replaceAll("[^a-zA-Z0-9_]+", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private final WeakReference<OBJ> referee;
	
	/**
	 * @param referent is the object to access to.
	 */
	public JavaReflectionMemory(OBJ referent) {
		this.referee = new WeakReference<OBJ>(referent);
	}
	
	/** Replies the getter function that permits to access to
	 * the field with the given name.
	 * 
	 * @param fieldName is the name of the knowledge for which the getter function must be replied
	 * @return the getter function for the given knowledge.
	 */
	protected Method getGetter(String fieldName) {
		try {
			OBJ referent = this.referee.get();
			if (referent!=null) {
				Class<?> type = referent.getClass();
				return type.getMethod("getK"+formatKnowledgeName(fieldName)); //$NON-NLS-1$
			}
		}
		catch(AssertionError ae) {
			throw ae;
		}
		catch(Exception _) {
			// return null on error
		}
		return null;
	}
	
	/** Replies the setter function that permits to access to
	 * the field with the given name.
	 * 
	 * @param fieldName is the name of the knowledge for which the setter function must be replied
	 * @return the setter function for the given knowledge.
	 */
	protected Method getSetter(String fieldName) {
		try {
			OBJ referent = this.referee.get();
			if (referent!=null) {
				Class<?> type = referent.getClass();
				return type.getMethod("setK"+formatKnowledgeName(fieldName), Object.class); //$NON-NLS-1$
			}
		}
		catch(AssertionError ae) {
			throw ae;
		}
		catch(Exception _) {
			// return null on error
		}
		return null;
	}

	/** Replies the knowledge with the given identifier.
	 * 
	 * @param id is the identifier of the knowledge.
	 * @return the data or <code>null</code>
	 */
	@Override
	public Object getMemorizedData(String id) {
		try {
			OBJ referent = this.referee.get();
			if (referent!=null) {
				Method m = getGetter(id);
				if (m!=null)
					return m.invoke(referent);
			}
		}
		catch(AssertionError e) {
			throw e;
		}
		catch(Throwable _) {
			// return null on error
		}
		return null;
	}
	
	/** Replies if a knowledge with the given identifier is existing in the memory.
	 * 
	 * @param id is the identifier of the knowledge.
	 * @return <code>true</code> if the knowledge is existing, otherwise <code>false</code>
	 */
	@Override
	public boolean hasMemorizedData(String id) {
		OBJ referent = this.referee.get();
		if (referent!=null) {
			Method m = getGetter(id);
			if (m!=null) {
				try {
					return m.invoke(referent) != null;
				}
				catch(AssertionError e) {
					throw e;
				}
				catch (Throwable _) {
					//
				}
			}
		}
		return false;
	}

	/** Put a knowledge in the memory.
	 * 
	 * @param id is the identifier of the knowledge.
	 * @param value is the data to memorize.
	 * @return <code>true</code> if the knowledge was successfully saved, otherwise <code>false</code>
	 */
	@Override
	public boolean putMemorizedData(String id, Object value) {
		try {
			OBJ referent = this.referee.get();
			if (referent!=null) {
				Method m = getSetter(id);
				if (m!=null) {
					Object oldValue = m.invoke(referent, value);
					
					if (value==null)
						fireKnowledgeRemoved(id, oldValue);
					else if (oldValue==null)
						fireKnowledgeAdded(id, value);
					else
						fireKnowledgeUpdate(id, oldValue, value);
					
					return true;
				}
			}
		}
		catch(AssertionError e) {
			throw e;
		}
		catch(Throwable _) {
			// return false on error
		}
		return false;
	}

	/** Remove a knowledge from the memory.
	 * 
	 * @param id is the identifier of the knowledge.
	 */
	@Override
	public void removeMemorizedData(String id) {
		try {
			OBJ referent = this.referee.get();
			if (referent!=null) {
				Method m = getSetter(id);
				if (m!=null) {
					Object oldValue = m.invoke(referent, new Object[]{null});
					fireKnowledgeRemoved(id, oldValue);
				}
			}
		}
		catch(AssertionError e) {
			throw e;
		}
		catch(Throwable _) {
			//
		}
	}

}
