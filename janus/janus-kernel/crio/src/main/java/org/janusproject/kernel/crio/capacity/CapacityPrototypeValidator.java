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
package org.janusproject.kernel.crio.capacity;

import org.janusproject.kernel.util.prototype.PrototypeException;
import org.janusproject.kernel.util.prototype.PrototypeScope;
import org.janusproject.kernel.util.prototype.PrototypeValidator;

/**
 * Validate the prototype of a capacity during
 * its invocation.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CapacityPrototypeValidator
extends PrototypeValidator<Capacity,CapacityPrototype> {

	private static final CapacityPrototypeValidator SINGLETON = new CapacityPrototypeValidator();
	
	private CapacityPrototypeValidator() {
		super(Capacity.class, CapacityPrototype.class);
	}

	/**
	 * Validate the input parameters to respect
	 * the capacity prototype.
	 * 
	 * @param capacityToTest is the type of the capacity to test.
	 * @param parameters are the input parameters.
	 * @return always <code>true</code>
	 * @throws PrototypeException when the prototype
	 * does not match the input parameters.
	 */
	public static boolean validateInputParameters(
			Class<? extends Capacity> capacityToTest,
			Object... parameters) {
		assert(SINGLETON!=null);
		return SINGLETON.validateInputs(
				capacityToTest,
				parameters);
	}
	
	/**
	 * Validate the output parameters to respect
	 * the capacity prototype.
	 * 
	 * @param capacityToTest is the type of the capacity to test.
	 * @param values are the ouput parameters.
	 * @return always <code>true</code>
	 * @throws PrototypeException when the prototype
	 * does not match the ouput values.
	 */
	public static boolean validateOutputParameters(
			Class<? extends Capacity> capacityToTest,
			Object... values) {
		assert(SINGLETON!=null);
		return SINGLETON.validateOutputs(
				capacityToTest,
				values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getFirstOptionalValueIndex(PrototypeScope scope, CapacityPrototype prototype) {
		switch(scope) {
		case INPUT:
			return prototype.optionalParameterAt();
		case OUTPUT:
			return prototype.optionalOutputAt();
		default:
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?>[] getFixedSizeValueList(
			PrototypeScope scope,
			CapacityPrototype prototype) {
		Class<?>[] types = null;
		switch(scope) {
		case INPUT:
			types = prototype.fixedParameters();
			break;
		case OUTPUT:
			types = prototype.fixedOutput();
			break;
		default:
		}
		if (types!=null
			&& types.length==1
			&& types[0] == CapacityPrototype.class) {
			types = null;
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getVariableSizePartValueType(
			PrototypeScope scope,
			CapacityPrototype prototype) {
		Class<?> type = null;
		switch(scope) {
		case INPUT:
			type = prototype.variableParameters();
			break;
		case OUTPUT:
			type = prototype.variableOutput();
			break;
		default:
		}
		if (type!=null
			&& type == CapacityPrototype.class) {
			type = null;
		}
		return type;
	}
	
}
