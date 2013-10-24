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
package org.janusproject.kernel.util.prototype;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;

/**
 * Validate the prototype of an invocation.
 * <p>
 * Prototype is composed of:
 * <ul>
 * <li>a list of types for input values,</li>
 * <li>a list of types for output values.</li>
 * </ul>
 * <p>
 * The following categories of type list are supported:
 * <ul>
 * <li>1.&nbsp;No parameter, eg.: <code>()</code>,</li>
 * <li>2.&nbsp;a arbitrary count of mandatory parameters with associated types, eg.:
 * 			<code>(Class<sub>1</sub>,Class<sub>2</sub>,Class<sub>3</sub>)</code>,</li>
 * <li>3.&nbsp;a finite but unspecified count of parameters of the same type, eg.:
 * 			<code>(Class...)</code>,</li>
 * <li>4.&nbsp;a arbitrary count of mandatory parameters followed by a arbitrary count of
 * optional parameters, eg.:
 * 			<code>(Class<sub>1</sub>,Class<sub>2</sub>[,Class<sub>3</sub>,Class<sub>4</sub>])</code>,</li>
 * <li>4.&nbsp;a arbitrary count of mandatory parameters followed by a arbitrary count of
 * optional parameters, followed in turn by a finite but unspecified number of parameters of the same type, eg.:
 * 			<code>(Class<sub>1</sub>,Class<sub>2</sub>[,Class<sub>3</sub>,Class<sub>4</sub>],Class<sub>5</sub>...)</code>,</li>
 * </ul>
 * All these categories could be used to specified a prototype usable by
 * this validator. The exact syntax depends on the definition of the 
 * prototype annotation itself.
 * <h3>Examples</h3>
 * The following examples use the annotation notation, which is the simplest way to specify the prototypes. 
 * <p>
 * <strong>Example 1: any number of parameters of type Object</strong><br>
 * <code><span>@</span>Prototype</code>
 * <p>
 * <strong>Example 2: three parameters of type Integer, String, Boolean resp.</strong><br>
 * <pre><code><span>@</span>Prototype(
 *   fixedParameters={Integer.class,String.class,Boolean.class}
 *)</pre></code>
 * <p>
 * <strong>Example 3: two mandatory parameters of type Integer, String resp. and one optional Boolean</strong><br>
 * <pre><code><span>@</span>Prototype(
 *   fixedParameters={Integer.class,String.class,Boolean.class},
 *   optionalParameterAt=2
 *)</code></pre>
 * <p>
 * <strong>Example 3: two mandatory parameters of type Integer, String resp., followed by any parameters of type Boolean</strong><br>
 * <pre><code><span>@</span>Prototype(
 *   fixedParameters={Integer.class,String.class},
 *   variableParameters=Boolean.class
 *)</code></pre>
 * <p>
 * <strong>Example 4: two mandatory parameters of type Integer, String resp., followed one optional Boolean, followed by any parameters of type Float</strong><br>
 * <pre><code><span>@</span>Prototype(
 *   fixedParameters={Integer.class,String.class,Boolean.class},
 *   optionalParameterAt=2,
 *   variableParameters=Float.class
 *)</code></pre>
 * <p>
 * <strong>Example 5: no parameter</strong><br>
 * <pre><code><span>@</span>Prototype(
 *   fixedParameters={}
 *)</code></pre>
 * 
 * @param <MT> is the type of the annotated primitive.
 * @param <AT> is the type of the annotation.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class PrototypeValidator<MT, AT extends Annotation> {

	private final Class<MT> annotatedType;
	private final Class<AT> annotationType;
	
	private volatile boolean notAnnotatedWarningDisplayed = false;
	
	/**
	 * @param annotatedType is the type of the annotated primitive.
	 * @param annotationType is the type of the annotation.
	 */
	protected PrototypeValidator(Class<MT> annotatedType, Class<AT> annotationType) {
		assert(annotatedType!=null);
		assert(annotationType!=null);
		this.annotatedType = annotatedType;
		this.annotationType = annotationType;
	}
	
	/** Replies the type of the annotations supported by this validator.
	 * 
	 * @return the type of the annotations supported by this validator.
	 */
	public Class<AT> getAnnotationType() {
		return this.annotationType;
	}
	
	/** Replies the type of the tested/annotated object.
	 * 
	 * @return the type of the tested/annotated object.
	 */
	public Class<MT> getAnnotedObjectType() {
		return this.annotatedType;
	}

	/**
	 * Validate the input parameters to respect
	 * the prototype.
	 * 
	 * @param objectToTest is the type of the object to test.
	 * @param parameters are the input parameters.
	 * @return always <code>true</code>; to allow us to invoke this function in an assert.
	 * @throws PrototypeException when the prototype
	 * does not match the input parameters.
	 */
	public boolean validateInputs(
			Class<? extends MT> objectToTest,
			Object... parameters) {
		AT prototype = searchFirstPrototypeInClassHierarchy(objectToTest);
		if (prototype!=null) {
			int optionalIndex = getFirstOptionalValueIndex(PrototypeScope.INPUT, prototype);
			Class<?> allInput = getVariableSizePartValueType(PrototypeScope.INPUT, prototype);
			Class<?>[] input = getFixedSizeValueList(PrototypeScope.INPUT, prototype);
			check(objectToTest, PrototypeScope.INPUT, input, allInput, optionalIndex, parameters);
		}
		else if (!this.notAnnotatedWarningDisplayed) {
			this.notAnnotatedWarningDisplayed = true;
			getLogger().warning( 
					Locale.getString(
							PrototypeValidator.class,
							"NOT_ANNOTATED_WITH_PROTOTYPE", //$NON-NLS-1$
							objectToTest.getCanonicalName(),
							PrototypeScope.INPUT.toLocalizedString(),
							this.annotationType.getName()));
		}
		return true;
	}
	
	/**
	 * Validate the output parameters to respect
	 * the prototype.
	 * 
	 * @param objectToTest is the type of the object to test.
	 * @param values are the ouput parameters.
	 * @return always <code>true</code>; to allow us to invoke this function in an assert.
	 * @throws PrototypeException when the prototype
	 * does not match the ouput values.
	 */
	public boolean validateOutputs(
			Class<? extends MT> objectToTest,
			Object... values) {
		AT prototype = searchFirstPrototypeInClassHierarchy(objectToTest);
		if (prototype!=null) {
			int optionalIndex = getFirstOptionalValueIndex(PrototypeScope.OUTPUT, prototype);
			Class<?> allOutput = getVariableSizePartValueType(PrototypeScope.OUTPUT, prototype);
			Class<?>[] output = getFixedSizeValueList(PrototypeScope.OUTPUT, prototype);
			check(objectToTest, PrototypeScope.OUTPUT, output, allOutput, optionalIndex, values);
		}
		else if (!this.notAnnotatedWarningDisplayed) {
			this.notAnnotatedWarningDisplayed = true;
			getLogger().warning( 
					Locale.getString(
							PrototypeValidator.class,
							"NOT_ANNOTATED_WITH_PROTOTYPE", //$NON-NLS-1$
							objectToTest.getCanonicalName(),
							PrototypeScope.OUTPUT.toLocalizedString(),
							this.annotationType.getName()));
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private AT searchFirstPrototypeInClassHierarchy(
			Class<? extends MT> objectToTest) {
		AT proto;
		List<Class<? extends MT>> candidates = new LinkedList<Class<? extends MT>>();
		Class<? extends MT> type;
		Class<?> parent;
		Class<?>[] superTypes;
		
		candidates.add(objectToTest);
		
		while (!candidates.isEmpty()) {
			
			type = candidates.remove(0);
			
			proto = type.getAnnotation(this.annotationType);
			if (proto!=null) return proto;
			
			superTypes = type.getInterfaces();
			if (superTypes!=null) {
				for(Class<?> t : superTypes) {
					if (this.annotatedType.isAssignableFrom(t)) {
						candidates.add((Class<? extends MT>)t);
					}
				}
			}
			
			if (!type.isInterface()) {
				parent = type.getSuperclass();
				if (parent!=null && this.annotatedType.isAssignableFrom(parent)) {
					candidates.add((Class<? extends MT>)parent);
				}
			}
		}
		return null;
	}
	
	/**
	 * Replies the logger for this validator.
	 * 
	 * @return the logger for this validator.
	 */
	protected static Logger getLogger() {
		return Logger.getLogger(PrototypeValidator.class.getCanonicalName());
	}
	
	private static <MT> void check(
			Class<? extends MT> objectToTest,
			PrototypeScope scope,
			Class<?>[] specifiedParameters,
			Class<?> restParameters,
			int optionalIndex,
			Object[] values) {
		Object[] internalValues = values;
		if (internalValues==null)
			internalValues = new Object[0];
		
		if (specifiedParameters==null && restParameters==null) {
			//
			// void is required
			//
			if (internalValues.length!=0) {
				String message = Locale.getString(
						PrototypeValidator.class,
						"VOID_INPUT_REQUIRED", //$NON-NLS-1$
						objectToTest.getCanonicalName(),
						scope.toLocalizedString());
				getLogger().severe(message);
				throw new PrototypeException(scope, objectToTest, internalValues, message);
			}
		}
		else if (specifiedParameters==null) {
			//
			// Infinite and unspecified count of parameters of the same type.
			//
			int mandatoryCount = (optionalIndex<0) ? internalValues.length : optionalIndex;

			if (internalValues.length<mandatoryCount) {
				String message = Locale.getString(
						PrototypeValidator.class,
						"NOT_ENOUGH_MANDATORY_PARAMETER", //$NON-NLS-1$
						mandatoryCount,
						internalValues.length,
						scope.toLocalizedString(),
						objectToTest.getCanonicalName());
				getLogger().severe(message);
				throw new PrototypeException(scope, objectToTest, internalValues, message);
			}
			
			for(int i=0; i<internalValues.length; ++i) {
				if (internalValues[i]!=null
					&& !restParameters.isInstance(internalValues[i])) {
					String message = Locale.getString(
							PrototypeValidator.class,
							"INVALID_PARAMETER_TYPE", //$NON-NLS-1$
							objectToTest.getCanonicalName(),
							scope.toLocalizedString(),
							i,
							restParameters.getCanonicalName(),
							internalValues[i].getClass().getCanonicalName());
					getLogger().severe(message);
					throw new PrototypeException(scope, objectToTest, internalValues, message);
				}
			}
		}
		else if (restParameters==null) {
			//
			// Have specified parameter types only
			//

			if (internalValues.length>specifiedParameters.length) {
				String message = Locale.getString(
						PrototypeValidator.class,
						"TOO_MANY_MANDATORY_PARAMETER", //$NON-NLS-1$
						specifiedParameters.length,
						internalValues.length,
						scope.toLocalizedString(),
						objectToTest.getCanonicalName());
				getLogger().severe(message);
				throw new PrototypeException(scope, objectToTest, internalValues, message);
			}

			int mandatoryCount = (optionalIndex<0) ? specifiedParameters.length : optionalIndex;

			if (internalValues.length<mandatoryCount) {
				String message = Locale.getString(
						PrototypeValidator.class,
						"NOT_ENOUGH_MANDATORY_PARAMETER", //$NON-NLS-1$
						mandatoryCount,
						internalValues.length,
						scope.toLocalizedString(),
						objectToTest.getCanonicalName());
				getLogger().severe(message);
				throw new PrototypeException(scope, objectToTest, internalValues, message);
			}
			
			// mandatory parameters
			for(int i=0; i<mandatoryCount; ++i) {
				assert(specifiedParameters[i]!=null);
				if (internalValues[i]!=null
					&& !specifiedParameters[i].isInstance(internalValues[i])) {
						String message = Locale.getString(
								PrototypeValidator.class,
								"INVALID_PARAMETER_TYPE", //$NON-NLS-1$
								objectToTest.getCanonicalName(),
								scope.toLocalizedString(),
								i,
								specifiedParameters[i].getCanonicalName(),
								internalValues[i].getClass().getCanonicalName());
						getLogger().severe(message);
						throw new PrototypeException(scope, objectToTest, internalValues, message);
				}
			}
			
			// optional parameters
			boolean optionalType;
			int j = mandatoryCount;
			for(int i=mandatoryCount; i<internalValues.length; ++i) {
				assert(specifiedParameters[i]!=null);
				optionalType = (internalValues[j]==null
						||specifiedParameters[i].isInstance(internalValues[j]));
				if (optionalType) {
					++j;
				}
			}

			if (j<internalValues.length) {				String message = Locale.getString(
						PrototypeValidator.class,
						"OPTIONAL_PARAMETER_NOT_FOUND", //$NON-NLS-1$
						objectToTest.getCanonicalName(),
						scope.toLocalizedString(),
						j);
				getLogger().severe(message);
				throw new PrototypeException(scope, objectToTest, internalValues, message);
			}

		}
		else {
			//
			// Have both specified parameter types only
			// and infinite and unspecified count of parameters of the same type.
			//
			assert(specifiedParameters!=null);
			assert(restParameters!=null);
			
			int mandatoryCount = (optionalIndex<0) ? specifiedParameters.length : optionalIndex;

			if (internalValues.length<mandatoryCount) {
				String message = Locale.getString(
						PrototypeValidator.class,
						"NOT_ENOUGH_MANDATORY_PARAMETER", //$NON-NLS-1$
						mandatoryCount,
						internalValues.length,
						scope.toLocalizedString(),
						objectToTest.getCanonicalName());
				getLogger().severe(message);
				throw new PrototypeException(scope, objectToTest, internalValues, message);
			}
			
			int idxValue = 0;
			int idxType = 0;
			for(; idxValue<internalValues.length
			   && idxType<specifiedParameters.length;
				++idxValue) {
				assert(specifiedParameters[idxType]!=null);
				if (internalValues[idxValue]!=null
					&& !specifiedParameters[idxType].isInstance(internalValues[idxValue])) {
					
					if (idxType>=mandatoryCount) {
						// Optional is missed
						++idxType;
					}
					else {
						// Mandatory is missed
						String message = Locale.getString(
								PrototypeValidator.class,
								"INVALID_PARAMETER_TYPE", //$NON-NLS-1$
								objectToTest.getCanonicalName(),
								scope.toLocalizedString(),
								idxValue,
								specifiedParameters[idxType].getCanonicalName(),
								internalValues[idxValue].getClass().getCanonicalName());
						getLogger().severe(message);
						throw new PrototypeException(
								scope, 
								objectToTest, 
								internalValues, 
								message);
					}
				}
				else {
					++idxType;
				}
			}
			
			for(; idxValue<internalValues.length; ++idxValue) {
				if (internalValues[idxValue]!=null
					&& !restParameters.isInstance(internalValues[idxValue])) {
						String message = Locale.getString(
								PrototypeValidator.class,
								"INVALID_PARAMETER_TYPE", //$NON-NLS-1$
								objectToTest.getCanonicalName(),
								scope.toLocalizedString(),
								idxValue,
								restParameters.getCanonicalName(),
								internalValues[idxValue].getClass().getCanonicalName());
						getLogger().severe(message);
						throw new PrototypeException(
								scope, 
								objectToTest, 
								internalValues, 
								message);
					}
			}

			if (idxValue<internalValues.length) {
				String message = Locale.getString(
						PrototypeValidator.class,
						"TOO_MANY_PARAMETERS", //$NON-NLS-1$
						objectToTest.getCanonicalName(),
						scope.toLocalizedString(),
						idxValue);
				getLogger().severe(message);
				throw new PrototypeException(
						scope, 
						objectToTest, 
						internalValues, 
						message);
			}
			
		}
	}
	
	/** Replies the fixed-size list of types which composes the prototype.
	 * <p>
	 * The replied value may corresponds to the category 2 of the prototype types,
	 * or to the first half of category 4. If <code>null</code> is replied,
	 * it means that there are no specified parameter for category 2.
	 * 
	 * @param scope is the scope of the prototype checking.
	 * @param prototype is the current prototype annotation which is tested
	 * by the validator.
	 * @return the predefined list of types for the prototype (Category 2),
	 * or <code>null</code> if no parameter was specified for category 2.
	 */
	protected abstract Class<?>[] getFixedSizeValueList(PrototypeScope scope, AT prototype);
	
	/** Replies the type of the parameters which are composing
	 * the variable-size of the prototype.
	 * <p>
	 * The replied value may corresponds to the category 3 of the prototype types,
	 * or to the second half of category 4. If <code>null</code> is replied,
	 * it means that there are no variable size part in prototype.
	 * 
	 * @param scope is the scope of the prototype checking.
	 * @param prototype is the current prototype annotation which is tested
	 * by the validator.
	 * @return the type for the values in the variable size part of the prototype
	 * (Category 3), or <code>null</code> if no parameter was specified for category 3.
	 */
	protected abstract Class<?> getVariableSizePartValueType(PrototypeScope scope, AT prototype);
	
	/** Replies the index of the first value, which is considered to be optionnal
	 * in prototype.
	 * <p>
	 * All values with an index lower or equal to the index replied by this
	 * function are assumed to be mandatory. A negative value means that all
	 * parameters are mandatory.
	 * 
	 * @param scope is the scope of the prototype checking.
	 * @param prototype is the current prototype annotation which is tested
	 * by the validator.
	 * @return the index of the first optional parameter or a negative value
	 * if these are no optional parameter.
	 */
	protected abstract int getFirstOptionalValueIndex(PrototypeScope scope, AT prototype);

}
