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
package org.janusproject.kernel.crio.role;

import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.util.prototype.PrototypeException;
import org.janusproject.kernel.util.prototype.PrototypeScope;
import org.janusproject.kernel.util.prototype.PrototypeValidator;

/**
 * Validate the prototype of a role activation function during
 * its invocation.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RoleActivationPrototypeValidator
extends PrototypeValidator<Role,RoleActivationPrototype> {

	private static final RoleActivationPrototypeValidator SINGLETON = new RoleActivationPrototypeValidator();
	
	private RoleActivationPrototypeValidator() {
		super(Role.class, RoleActivationPrototype.class);
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
			Class<? extends Role> capacityToTest,
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
			Class<? extends Role> capacityToTest,
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
	protected int getFirstOptionalValueIndex(
			PrototypeScope scope,
			RoleActivationPrototype prototype) {
		switch(scope) {
		case INPUT:
			return prototype.optionalParameterAt();
		case OUTPUT:
			return -1;
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
			RoleActivationPrototype prototype) {
		Class<?>[] types = null;
		switch(scope) {
		case INPUT:
			types = prototype.fixedParameters();
			break;
		case OUTPUT:
			break;
		default:
		}
		if (types!=null
			&& types.length==1
			&& types[0] == RoleActivationPrototype.class) {
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
			RoleActivationPrototype prototype) {
		Class<?> type = null;
		switch(scope) {
		case INPUT:
			type = prototype.variableParameters();
			break;
		case OUTPUT:
			break;
		default:
		}
		if (type!=null
			&& type == RoleActivationPrototype.class) {
			type = null;
		}
		return type;
	}
	
}
