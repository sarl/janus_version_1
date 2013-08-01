/* 
 * $Id$
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
package org.janusproject.kernel.util.prototype;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Annotation that permits to mark capacity classes
 * with input and output types.
 * <p>
 * Optional parameters could be defined.
 * The index of the first optional
 * input parameter may be set with {@link #optionalInputAt()}.
 * It is assumed that all subsequent input parameters are
 * also optional; and all parameters with at lower index
 * are mandatory.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Retention(value=RUNTIME)
@Target({ElementType.TYPE})
@interface PrototypeStub {

	/** 
	 * Get the type of all the input values without exception.
	 * <p>
	 * This tag may not be used at same time as
	 * {@link #input()}.
	 */
	Class<?> allInput() default PrototypeStub.class;

	/** 
	 * List of types of the input values.
	 * <p>
	 * An input value may be optional
	 * according to its position in
	 * the value's list. See
	 * {@link #optionalInputAt()}
	 * for more details.
	 * <p>
	 * This tag may not be used at same time as
	 * {@link #allInput()}.
	 */
	Class<?>[] input() default {PrototypeStub.class};
	
	/** 
	 * Index of the first optional value in
	 * input parameters.
	 * <p>
	 * All input values with an index greater or
	 * equal to the given index are assumed to
	 * be optional.  
	 */
	int optionalInputAt() default -1;

	/** 
	 * List of types of the individual output values.
	 * <p>
	 * This tag may not be used at same time as 
	 * {@link #allOutput()}.
	 */
	Class<?>[] output() default {PrototypeStub.class};
	
	/** 
	 * Get the type of all the output values without exception.
	 * <p>
	 * This tag may not be used at same time as
	 * {@link #output()}.
	 */
	Class<?> allOutput() default PrototypeStub.class;

	/** 
	 * Index of the first optional value in
	 * output parameters.
	 * <p>
	 * All output values with an index greater or
	 * equal to the given index are assumed to
	 * be optional.  
	 */
	int optionalOutputAt() default -1;

}