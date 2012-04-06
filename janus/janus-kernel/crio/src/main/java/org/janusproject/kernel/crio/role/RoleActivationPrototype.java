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
package org.janusproject.kernel.crio.role;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Annotation that permits to mark role classes
 * with input types for activation stage.
 * <p>
 * Optional parameters could be defined.
 * The index of the first optional
 * input parameter may be set with {@link #optionalParameterAt()}.
 * It is assumed that all subsequent input parameters are
 * also optional; and all parameters with at lower index
 * are mandatory.
 * <p>
 * <strong>Example 1: any number of parameters of type Object</strong><br>
 * <pre><code><span>@</span>RoleActivationPrototype(
 *   variableParameters={Object.class}
 *)</pre></code>
 * <p>
 * <strong>Example 2: three parameters of type Integer, String, Boolean resp.</strong><br>
 * <pre><code><span>@</span>RoleActivationPrototype(
 *   fixedParameters={Integer.class,String.class,Boolean.class}
 *)</pre></code>
 * <p>
 * <strong>Example 3: two mandatory parameters of type Integer, String resp. and one optional Boolean</strong><br>
 * <pre><code><span>@</span>RoleActivationPrototype(
 *   fixedParameters={Integer.class,String.class,Boolean.class},
 *   optionalParameterAt=2
 *)</code></pre>
 * <p>
 * <strong>Example 3: two mandatory parameters of type Integer, String resp., followed by any parameters of type Boolean</strong><br>
 * <pre><code><span>@</span>RoleActivationPrototype(
 *   fixedParameters={Integer.class,String.class},
 *   variableParameters=Boolean.class
 *)</code></pre>
 * <p>
 * <strong>Example 4: two mandatory parameters of type Integer, String resp., followed one optional Boolean, followed by any parameters of type Float</strong><br>
 * <pre><code><span>@</span>RoleActivationPrototype(
 *   fixedParameters={Integer.class,String.class,Boolean.class},
 *   optionalParameterAt=2,
 *   variableParameters=Float.class
 *)</code></pre>
 * <p>
 * <strong>Example 5: no parameter</strong><br>
 * <pre><code><span>@</span>RoleActivationPrototype
 *<span>@</span>RoleActivationPrototype(
 *   fixedParameters={}
 *)</code></pre>
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Retention(value=RUNTIME)
@Target({ElementType.TYPE})
public @interface RoleActivationPrototype {

	/** 
	 * Get the type of all the input values without exception.
	 * <p>
	 * This tag may not be used at same time as
	 * {@link #fixedParameters()}.
	 */
	Class<?> variableParameters() default RoleActivationPrototype.class;

	/** 
	 * List of types of the input values.
	 * <p>
	 * An input value may be optional
	 * according to its position in
	 * the value's list. See
	 * {@link #optionalParameterAt()}
	 * for more details.
	 * <p>
	 * This tag may not be used at same time as
	 * {@link #variableParameters()}.
	 */
	Class<?>[] fixedParameters() default {RoleActivationPrototype.class};
	
	/** 
	 * Index of the first optional value in
	 * input parameters.
	 * <p>
	 * All input values with an index greater or
	 * equal to the given index are assumed to
	 * be optional.  
	 */
	int optionalParameterAt() default -1;

}