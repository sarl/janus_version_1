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
package org.janusproject.kernel.crio.capacity;

import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.util.UUID;
import java.util.logging.Logger;

import org.janusproject.kernel.time.KernelTimeManager;

/**
 * This class stores the informations relative to a call
 * to a capacity implementation.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class CapacityContext {

	/**
	 * The string unambigeously identifying this call.
	 */
	private final UUID identifier;

	/**
	 * The invoked capacity.
	 */
	private final Class<? extends Capacity> invokedCapacity;

	/**
	 * Type of the invoked capacity.
	 */
	private final CapacityImplementationType type;
	
	/**
	 * Indicates if the capacity call has failed.
	 */
	private boolean failure;

	/**
	 * The reference to the object that have called this capacity
	 */
	private final SoftReference<CapacityCaller> caller;

	/**
	 * The List of the value required in input to execute the given capacity
	 */
	private final Object[] inputValues;

	/**
	 * The List of the value provide in output of the execution of the given capacity
	 */
	private Object[] outputValues;

	/**
	 * boolean precising if the result of the call is available, if the
	 * <code>outputValue</code> are affected.
	 */
	private boolean isResultAvailable;
	
	/**
	 * Exception which has cause the failure.
	 */
	private Throwable exception;

	/**
	 * Builds a new call
	 * @param caller is the caller of this capacity
	 * @param invokedCapacity is the invoked capacity in this context.
	 * @param type is the type of the invoked capacity implementation.
	 * @param input are the input data required to execute this capacity
	 */
	public CapacityContext(
			CapacityCaller caller, 
			Class<? extends Capacity> invokedCapacity, 
			CapacityImplementationType type,
			Object... input) {
		this(caller, invokedCapacity, type, UUID.randomUUID(), input);
	}

	/**
	 * Builds a new call.
	 * 
	 * @param caller is the caller of this capacity.
	 * @param invokedCapacity is the invoked capacity in this context.
	 * @param type is the type of the invoked capacity implementation.
	 * @param identifier is the unique identifier of the call.
	 * @param input are the input data required to execute this capacity
	 */
	public CapacityContext(
			CapacityCaller caller, 
			Class<? extends Capacity> invokedCapacity,
			CapacityImplementationType type,
			UUID identifier, 
			Object... input) {
		this.identifier = UUID.randomUUID();
		this.invokedCapacity = invokedCapacity;
		this.type = type;
		this.caller = new SoftReference<CapacityCaller>(caller);
		this.inputValues = input;
		this.outputValues = null;
		this.failure = false;
		this.exception = null;
	}

	/**
	 * Replies the identifier of this capacity call.
	 * 
	 * @return the identifier of this capacity call.
	 */
	public UUID getIdentifier() {
		return this.identifier;
	}

	/**
	 * Replies the invoked capacity which causes to create this context.
	 * 
	 * @return the invoked capacity.
	 */
	public Class<? extends Capacity> getInvokedCapacity() {
		return this.invokedCapacity;
	}

	/**
	 * Replies the type of the invoked capacity which causes 
	 * to create this context.
	 * 
	 * @return the type of the invoked capacity.
	 */
	public CapacityImplementationType getInvokedCapacityType() {
		return this.type;
	}

	/**
	 * Replies the reference to the object which has called this capacity.
	 * 
	 * @return the reference to the object which has called this capacity
	 */
	public CapacityCaller getCaller() {
		return this.caller.get();
	}

	/**
	 * Replies the current time manager.
	 * 
	 * @return the current time manager.
	 */
	public KernelTimeManager getTimeManager() {
		CapacityCaller c = getCaller();
		assert(c!=null);
		return c.getTimeManager();
	}

	/**
	 * Replies the input parameters.
	 * @return input parameters.
	 */
	public Object[] getInputValues() {
		return this.inputValues==null ? new Object[0] : this.inputValues;
	}

	/**
	 * Replies the input parameters.
	 * <p>
	 * This function makes a copy of the array of the input values
	 * to avoid ClassCastException on arrays.
	 * 
	 * @param <T> if the type of the replied objects.
	 * @param type if the type of the replied objects.
	 * @return input parameters.
	 * @since 0.4
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] getInputValues(Class<T> type) {
		assert(type!=null);
		if (this.inputValues==null) {
			return (T[])Array.newInstance(type, 0);
		}
		else if (type.equals(this.inputValues.getClass().getComponentType())) {
			return (T[])this.inputValues;
		}

		T[] tab = (T[])Array.newInstance(type, this.inputValues.length);
		for(int i=0; i<tab.length; ++i) {
			if (this.inputValues[i]==null || type.isAssignableFrom(this.inputValues[i].getClass())) {
				tab[i] = type.cast(this.inputValues[i]);
			}
			else {
				return (T[])Array.newInstance(type, 0);
			}
		}
		return tab;
	}

	/**
	 * Replies the input parameter.
	 * @param index is the index of the input parameter.
	 * @return input parameter.
	 */
	public Object getInputValueAt(int index) {
		return this.inputValues==null || index<0 || index>=this.inputValues.length ? null : this.inputValues[index];
	}

	/**
	 * Replies the input parameter.
	 * 
	 * @param <T> is the type of the element to reply.
	 * @param index is the index of the input parameter.
	 * @param elementType is the type of the element to reply.
	 * @return input parameter.
	 * @throws ClassCastException if the element is not of the given type.
	 */
	public <T> T getInputValueAt(int index, Class<T> elementType) {
		assert(elementType!=null);
		return this.inputValues==null || index<0 || index>=this.inputValues.length ? null : elementType.cast(this.inputValues[index]);
	}
	/**
	 * Replies the count of input parameters.
	 * @return count of input parameters.
	 */
	public int getInputValueCount() {
		return this.inputValues==null ? 0 : this.inputValues.length;
	}

	/**
	 * Mark this capacity call as failed.
	 */
	public void fail() {
		fail(null);
	}

	/**
	 * Mark this capacity call as failed.
	 * 
	 * @param e is the exception which has cause the failure.
	 */
	public void fail(Throwable e) {
		this.failure = true;
		this.exception = e;
		this.outputValues = null;
		this.isResultAvailable = false;
	}
	
	/**
	 * Mark this capacity call as success.
	 * Remove all previously stored output values.
	 */
	public void success() {
		this.failure = false;
		this.exception = null;
		this.outputValues = null;
		this.isResultAvailable = true;
	}

	/** Replies the exception which has caused the failure
	 * if any.
	 * 
	 * @return the exception or <code>null</code> if none.
	 */
	public Throwable getFailureException() {
		return this.exception;
	}

	/**
	 * Replies if this call has failed.
	 * 
	 * @return <code>true</code> if this called has failed, otherwise <code>false</code>
	 */
	public boolean isFailed() {
		return this.failure;
	}

	/**
	 * Replies if call was done and results are available.
	 * 
	 * @return <code>true</code> if the result of the call is available, if the
	 *         <code>outputValue</code> are affected.
	 */
	public boolean isResultAvailable() {
		return this.isResultAvailable;
	}

	/**
	 * Replies all the output values.
	 * 
	 * @return data resulting from the computation of the capacity implementation
	 */
	public Object[] getOutputValues() {
		return (isFailed() || this.outputValues==null) ? new Object[0] : this.outputValues;
	}

	/**
	 * Replies all the output values.
	 * <p>
	 * <strong>Note:</strong> this function makes a copy of the stored results.
	 * It may be expensive in time and in memory. 
	 * 
	 * @param type is the type of the output values.
	 * @return data resulting from the computation of the capacity implementation
	 * @since 0.5
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] getOutputValues(Class<T> type) {
		assert(type!=null);
		if (isFailed() || this.outputValues==null) {
			return (T[])Array.newInstance(type, 0);
		}
		else if (type.equals(this.outputValues.getClass().getComponentType())) {
			return (T[])this.outputValues;
		}

		T[] tab = (T[])Array.newInstance(type, this.outputValues.length);
		for(int i=0; i<tab.length; ++i) {
			if (this.outputValues[i]==null || type.isAssignableFrom(this.outputValues[i].getClass())) {
				tab[i] = type.cast(this.outputValues[i]);
			}
			else {
				return (T[])Array.newInstance(type, 0);
			}
		}
		return tab;
	}

	/**
	 * Replies the first output value.
	 * 
	 * @return first data resulting from the computation of the capacity implementation
	 */
	public Object getOutputValue() {
		return (isFailed() || this.outputValues==null) ? null : this.outputValues[0];
	}

	/**
	 * Replies the output value at given index.
	 * 
	 * @param index
	 * @return data resulting from the computation of the capacity implementation
	 */
	public Object getOutputValueAt(int index) {
		return (isFailed() || this.outputValues==null || index<0 || index>=this.outputValues.length) ? null : this.outputValues[index];
	}

	/**
	 * Replies the output value at given index.
	 * 
	 * @param <T> is the type of the element to reply.
	 * @param index
	 * @param elementType is the type of the element to reply.
	 * @return data resulting from the computation of the capacity implementation
	 * @throws ClassCastException if the element is not of the given type.
	 */
	public <T> T getOutputValueAt(int index, Class<T> elementType) {
		assert(elementType!=null);
		return (isFailed() || this.outputValues==null || index<0 || index>=this.outputValues.length) ? null : elementType.cast(this.outputValues[index]);
	}

	/**
	 * Replies the count of output values.
	 * 
	 * @return count of output values.
	 */
	public int getOutputValueCount() {
		return (isFailed() || this.outputValues==null) ? 0 : this.outputValues.length;
	}

	/**
	 * Set the value
	 * <tt>outputValues</tt> to the output of this call and turn to true the boolean <tt>isResultAvailable</tt>
	 * @param value - the HashMap containing the result of this call
	 */
	public void setOutputValues(Object... value) {
		if (isFailed()) return;
		this.outputValues = value;
		this.isResultAvailable = true;
	}
	
	/** Replies the logger associated to the caller.
	 * 
	 * @return the caller's logger.
	 */
	public Logger getLogger() {
		return getCaller().getLogger();
	}
	
}
