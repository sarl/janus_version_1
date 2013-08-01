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

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class ValidatorStub extends PrototypeValidator<Object,PrototypeStub> {

	/**
	 */
	public ValidatorStub() {
		super(Object.class, PrototypeStub.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getFirstOptionalValueIndex(PrototypeScope scope, PrototypeStub prototype) {
		switch(scope) {
		case INPUT:
			return prototype.optionalInputAt();
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
	protected Class<?>[] getFixedSizeValueList(PrototypeScope scope, PrototypeStub prototype) {
		Class<?>[] types = null;
		switch(scope) {
		case INPUT:
			types = prototype.input();
			break;
		case OUTPUT:
			types = prototype.output();
			break;
		default:
		}
		if (types!=null
			&& types.length==1
			&& types[0] == PrototypeStub.class) {
			types = null;
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getVariableSizePartValueType(PrototypeScope scope, PrototypeStub prototype) {
		Class<?> type = null;
		switch(scope) {
		case INPUT:
			type = prototype.allInput();
			break;
		case OUTPUT:
			type = prototype.allOutput();
			break;
		default:
		}
		if (type!=null
			&& type == PrototypeStub.class) {
			type = null;
		}
		return type;
	}
	
}
