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
package org.janusproject.kernel.crio.core;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.capacity.CapacityCaller;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityPrototypeValidator;

/**
 * Execute capacities in a pool of threads.
 * <p>
 * Creates a thread pool that creates new threads as needed, but
 * will reuse previously constructed threads when they are
 * available.  These pools will typically improve the performance
 * of programs that execute many short-lived asynchronous tasks.
 * Calls to <tt>submit</tt> will reuse previously constructed
 * threads if available. If no existing thread is available, a new
 * thread will be created and added to the pool if the maximum number
 * of threads allowed in pool is not reached. Threads that have
 * not been used for sixty seconds are terminated and removed from
 * the cache. Thus, a pool that remains idle for long enough will
 * not consume any resources.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see Capacity
 * @see CapacityImplementation
 */
public final class CapacityExecutor {
	
	/** Maximal count of threads allowed for capacity execution, by default <code>50</code>.
	 */
	public static final int THREAD_POOL_MAX_SIZE = 50;
	
	/** Minimal count of threads for capacity execution, by default <code>0</code>.
	 */
	public static final int THREAD_POOL_MIN_SIZE = 0;

	/** Allowed iddle duration in seconds.
	 */
	public static final int THREAD_IDDLE_DURATION = 60;
	
	private ExecutorService executionService = null;
	
	private final Map<UUID,DifferedCapacityInvocation> results = new TreeMap<UUID,DifferedCapacityInvocation>();
	
	/**
	 */
	CapacityExecutor() {
		//
	}

	/** Shutdown this executor.
	 * <p>
	 * It causes to stop all capacity executions.
	 */
	void shutdown() {
		if (this.executionService!=null) {
			this.executionService.shutdownNow();
		}
		for(DifferedCapacityInvocation task : this.results.values()) {
			try {
				task.cancel(true);

				GroupCapacityContext context = task.get();
				if (context!=null && !context.isResultAvailable()) {
					context.fail(new InterruptedException());
				}
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable e) {
				Logger.getLogger(CapacityExecutor.class.getCanonicalName()).
					log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}
	
	/** Replies if a capacity is running.
	 * 
	 * @return <code>true</code> if at least one capacty is running,
	 * otherwise <code>false</code>
	 */
	public boolean hasCapacityExecution() {
		return this.executionService!=null && !this.executionService.isTerminated();
	}
	
	//------------------------------------------------
	// Synchronious Execution
	//------------------------------------------------
		
	/** Execute immediately the given capacity implementation.
	 * For asynchronous execution, see {@link #submit(Class, CapacityImplementation, CapacityCaller, KernelScopeGroup, Role, Object...)}.
	 * 
	 * @param capacity is the invoked capacity.
	 * @param capacityImplementation is the capacity to run.
	 * @param caller is the capacity caller.
	 * @param group is the group inside which the capacity is invoked.
	 * @param role is the role which has invoked the capacity.
	 * @param parameters are the values to pass to the capacity implementation.
	 * @return the execution context after execution.
	 * @throws Exception 
	 */
	static CapacityContext executeImmediately(
			Class<? extends Capacity> capacity,
			CapacityImplementation capacityImplementation,
			CapacityCaller caller,
			KernelScopeGroup group,
			Role role,
			Object... parameters) throws Exception {
		assert(capacity!=null);
		assert(capacityImplementation!=null);
		assert(capacity.isInstance(capacityImplementation)) : 
			Locale.getString(CapacityExecutor.class,
					"INVALID_IMPLEMENTATION", //$NON-NLS-1$
					capacityImplementation.getClass().getCanonicalName(),
					capacity.getCanonicalName());
		assert(CapacityPrototypeValidator.validateInputParameters(capacity, parameters));
		
		GroupCapacityContext context = new GroupCapacityContext(
				caller, group, role, 
				capacity,
				capacityImplementation.getImplementationType(),
				parameters);
		capacityImplementation.call(context);
		
		assert(context.isFailed() || CapacityPrototypeValidator.validateOutputParameters(capacity, context.getOutputValues()));

		return context;
	}
	
	//------------------------------------------------
	// Asynchronious Execution
	//------------------------------------------------

	/** Put the given capacity implementation inside the execution
	 * queue.
	 * For synchronous execution, see {@link #executeImmediately(Class, CapacityImplementation, CapacityCaller, KernelScopeGroup, Role, Object...)}.
	 * 
	 * @param capacity is the invoked capacity.
	 * @param capacityImplementation is the capacity to run.
	 * @param caller is the capacity caller.
	 * @param group is the group inside which the capacity is invoked.
	 * @param role is the role which has invoked the capacity.
	 * @param parameters are the values to pass to the capacity implementation.
	 * @return the identifier of the task in the queue.
	 */
	UUID submit(
			Class<? extends Capacity> capacity,
			CapacityImplementation capacityImplementation,
			CapacityCaller caller,
			KernelScopeGroup group,
			Role role,
			Object... parameters) {
		assert(capacity!=null);
		assert(capacityImplementation!=null);
		assert(capacity.isInstance(capacityImplementation)) : 
			Locale.getString(CapacityExecutor.class,
					"INVALID_IMPLEMENTATION", //$NON-NLS-1$
					capacityImplementation.getClass().getCanonicalName(),
					capacity.getCanonicalName());
		assert(CapacityPrototypeValidator.validateInputParameters(capacity, parameters));
		
		GroupCapacityContext context = new GroupCapacityContext(
				caller, group, role, 
				capacity,
				capacityImplementation.getImplementationType(),
				parameters);
		
		synchronized(this) {
			if (this.executionService==null) {
				this.executionService = new ThreadPoolExecutor(
						THREAD_POOL_MIN_SIZE,
						THREAD_POOL_MAX_SIZE,
                        THREAD_IDDLE_DURATION,
                        TimeUnit.SECONDS,
                        new SynchronousQueue<Runnable>());
			}
		}
		
		UUID id = context.getIdentifier();
		
		synchronized(this.results) {
			Future<GroupCapacityContext> future = this.executionService.submit(new Task(context, capacityImplementation));
			this.results.put(id, new DifferedCapacityInvocation(context, future, caller.getAddress()));
		}
		
		return id;
	}
	
	/**
	 * Retrieves the result of the call with the specified identifier, waiting
	 * if necessary up to the specified wait time if the result is not available.
	 * <p>
	 * After calling this function and if a context was replied,
	 * the replied task result is no more available from executor. It means
	 * that following invocation of <code>consumeResult()</code> or <code>getResult()</code>
	 * with the same identifier as parameter will always returns <code>null</code>.
	 * <p>
	 * When timeout is reached, the context is not consumed. 
	 * 
	 * @param invoker is the address of the entity which wants the results.
	 * @param taskIdentifier is the identifier of the call, 
	 * given by {@link #submit(Class, CapacityImplementation, CapacityCaller, KernelScopeGroup, Role, Object...)}
	 * @param timeout indicates how long to wait before giving up (in milliseconds).
	 * @return the result of the call, or <tt>null</tt> if the
	 * specified waiting time elapses before the result is available.
	 */
	public final CapacityContext waitResult(AgentAddress invoker, UUID taskIdentifier, long timeout) {
		return waitResult(invoker, taskIdentifier, timeout, TimeUnit.MILLISECONDS);
	}

	/**
	 * Retrieves the result of the call with the specified identifier, waiting
	 * if necessary up to the specified wait time if the result is not available.
	 * <p>
	 * After calling this function and if a context was replied,
	 * the replied task result is no more available from executor. It means
	 * that following invocation of <code>consumeResult()</code> or <code>getResult()</code>
	 * with the same identifier as parameter will always returns <code>null</code>.
	 * <p>
	 * When timeout is reached, the context is not consumed. 
	 * 
	 * @param invoker is the address of the entity which wants the results.
	 * @param taskIdentifier is the identifier of the call, 
	 * given by {@link #submit(Class, CapacityImplementation, CapacityCaller, KernelScopeGroup, Role, Object...)}
	 * @param timeout indicates how long to wait before giving up.
	 * @param unit is the time unit of the given timeout.
	 * @return the result of the call, or <tt>null</tt> if the
	 * specified waiting time elapses before the result is available.
	 */
	public CapacityContext waitResult(AgentAddress invoker, UUID taskIdentifier, long timeout, TimeUnit unit) {
		assert(invoker!=null);
		assert(taskIdentifier!=null);
		
		DifferedCapacityInvocation task;
		synchronized(this.results) {
			task = this.results.remove(taskIdentifier);
		}
		
		if (task==null)
			return null;
		
		if (!invoker.equals(task.getOwner())) {
			synchronized(this.results) {
				this.results.put(taskIdentifier, task);
			}
			return null;
		}
		
		try {
			return task.get(timeout, unit);
		}
		catch(AssertionError ae) {
			throw ae;
		}
		catch(Exception e) {
			synchronized(this.results) {
				this.results.put(taskIdentifier, task);
			}
			return null;
		}
	}

	/**
	 * Replies if the result of the call with the specified identifier is available.
	 * 
	 * @param invoker is the address of the entity which wants the results.
	 * @param taskIdentifier is the identifier of the call, 
	 * given by {@link #submit(Class, CapacityImplementation, CapacityCaller, KernelScopeGroup, Role, Object...)}
	 * @return <code>true</code> if the result is available, otherwhise <code>false</code>
	 */
	public boolean hasResult(AgentAddress invoker, UUID taskIdentifier) {
		assert(invoker!=null);
		assert(taskIdentifier!=null);
		
		DifferedCapacityInvocation task;
		synchronized(this.results) {
			task = this.results.get(taskIdentifier);
		}
		
		return (task!=null
				&& invoker.equals(task.getOwner())
				&& task.isDone());
	}

	/**
	 * Retrieves the result of the call with the specified identifier, do not wait
	 * if the result is not available.
	 * 
	 * @param invoker is the address of the entity which wants the results.
	 * @param taskIdentifier is the identifier of the call, 
	 * given by {@link #submit(Class, CapacityImplementation, CapacityCaller, KernelScopeGroup, Role, Object...)}
	 * @return the result of the call, or <tt>null</tt> if the given identifier is unkwown or
	 * no result is available.
	 */
	public CapacityContext instantResult(AgentAddress invoker, UUID taskIdentifier) {
		assert(invoker!=null);
		assert(taskIdentifier!=null);
		
		DifferedCapacityInvocation task;
		synchronized(this.results) {
			task = this.results.get(taskIdentifier);
		}
		
		if (task==null)
			return null;
		
		if (!invoker.equals(task.getOwner())) {
			synchronized(this.results) {
				this.results.put(taskIdentifier, task);
			}
			return null;
		}
		
		if (task.isDone()) {
			synchronized(this.results) {
				this.results.remove(taskIdentifier);
			}
			try {
				return task.get();
			}
			catch(AssertionError ae) {
				throw ae;
			}
			catch (Exception e) {
				Logger.getLogger(CapacityExecutor.class.getCanonicalName()).
					log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}

		return null;
	}

	/**
	 * Force the result of a capacity invocation.
	 * 
	 * @param terminator is the address of the entity which wants to terminate the capacity execution.
	 * @param taskIdentifier is the identifier of the call, 
	 * given by {@link #submit(Class, CapacityImplementation, CapacityCaller, KernelScopeGroup, Role, Object...)}
	 * @param results are the values to put back as capacity results.
	 * @return <code>true</code> if the result was saved, otherwise <code>false</code>.
	 */
	public boolean done(AgentAddress terminator, UUID taskIdentifier, Object... results) {
		assert(terminator!=null);
		assert(taskIdentifier!=null);
		
		DifferedCapacityInvocation task;
		synchronized(this.results) {
			task = this.results.get(taskIdentifier);
		}
		
		if (task==null ||
			!terminator.equals(task.getOwner()))
			return false;
		
		try {
			task.cancel(true);
			GroupCapacityContext context = task.get();
			if (context!=null) {
				context.success();
				context.setOutputValues(results);
				return true;
			}
		}
		catch(AssertionError e) {
			throw e;
		}
		catch(Throwable e) {
			Logger.getLogger(CapacityExecutor.class.getCanonicalName()).
				log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		
		return false;
	}

	/**
	 * Cancel a capacity invocation.
	 * <p>
	 * This function stop the capacity running
	 * and force it to fail.
	 * 
	 * @param canceler is the address of the netity which wants to cancel the capacity execution.
	 * @param taskIdentifier is the identifier of the call, 
	 * given by {@link #submit(Class, CapacityImplementation, CapacityCaller, KernelScopeGroup, Role, Object...)}
	 * @return <code>true</code> if the capacity was canceled, otherwise <code>false</code>
	 */
	public final boolean cancel(AgentAddress canceler, UUID taskIdentifier) {
		return cancel(canceler, taskIdentifier, null);
	}

	/**
	 * Cancel a capacity invocation.
	 * <p>
	 * This function stop the capacity running
	 * and force it to fail.
	 * 
	 * @param canceler is the address of the netity which wants to cancel the capacity execution.
	 * @param taskIdentifier is the identifier of the call, 
	 * given by {@link #submit(Class, CapacityImplementation, CapacityCaller, KernelScopeGroup, Role, Object...)}
	 * @param exception is the exception which causes cancelation.
	 * @return <code>true</code> if the capacity was canceled, otherwise <code>false</code>
	 */
	public boolean cancel(AgentAddress canceler, UUID taskIdentifier, Throwable exception) {
		assert(canceler!=null);
		assert(taskIdentifier!=null);
		
		DifferedCapacityInvocation task;
		synchronized(this.results) {
			task = this.results.get(taskIdentifier);
		}
		
		if (task==null ||
			!canceler.equals(task.getOwner()))
				return false;
				
		try {
			task.cancel(true);
			GroupCapacityContext context = task.get();
			if (context!=null) {
				if (exception!=null) context.fail(exception);
				else context.fail();
				return true;
			}
		}
		catch(AssertionError e) {
			throw e;
		}
		catch(Throwable e) {
			Logger.getLogger(CapacityExecutor.class.getCanonicalName()).
				log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		
		return false;
	}

	/**
	 * Remove all capacity invocations.
	 * <p>
	 * This function stop the capacity running
	 * and remove it. Capacity context will be
	 * no more available.
	 *
	 * 
	 * @param owner is the address of the entity which owns the results.
	 * @return <code>true</code> if at least on capacity
	 * invocation was removed, otherwise <code>false</code>
	 */
	public boolean clear(AgentAddress owner) {
		boolean changed = false;
		assert(owner!=null);
		synchronized(this.results) {
			Iterator<Entry<UUID,DifferedCapacityInvocation>> iterator;
			Entry<UUID,DifferedCapacityInvocation> entry;
			DifferedCapacityInvocation invocation;
			
			iterator = this.results.entrySet().iterator();
			
			while (iterator.hasNext()) {
				entry = iterator.next();
				assert(entry!=null);
				invocation = entry.getValue();
				assert(invocation!=null);
				if (owner.equals(invocation.getOwner())) {
					if (!invocation.isDone())
						invocation.cancel(true);
					iterator.remove();
					changed = true;
				}
			}
		}
		return changed;
	}

	/**
	 * Retrieves the result of the call with the specified identifier, waiting
	 * if necessary up to the specified wait time if the result is not available.
	 * 
	 * @param invoker is the address of the entity which wants the results.
	 * @param taskIdentifier is the identifier of the call, 
	 * given by {@link #submit(Class, CapacityImplementation, CapacityCaller, KernelScopeGroup, Role, Object...)}
	 * @return the result of the call, or <tt>null</tt> if the given identifier is unkwown.
	 */
	public CapacityContext waitResult(AgentAddress invoker, UUID taskIdentifier) {
		assert(invoker!=null);
		assert(taskIdentifier!=null);
		
		DifferedCapacityInvocation task;
		synchronized(this.results) {
			task = this.results.remove(taskIdentifier);
		}
		
		if (task==null)
			return null;
		
		if (!invoker.equals(task.getOwner())) {
			synchronized(this.results) {
				this.results.put(taskIdentifier, task);
			}
			return null;
		}
		
		try {
			return task.get();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Task implements Callable<GroupCapacityContext> {

		private GroupCapacityContext context;
		private CapacityImplementation capacity;
		
		/**
		 * @param context
		 * @param capacity
		 */
		public Task(GroupCapacityContext context, CapacityImplementation capacity) {
			this.context = context;
			this.capacity = capacity;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupCapacityContext call() {
			GroupCapacityContext currentContext = this.context;
			CapacityImplementation currentCapacity = this.capacity;
			this.context = null;
			this.capacity = null;

			if (currentContext!=null && currentCapacity!=null) {
				try {
					currentCapacity.call(currentContext);
					assert(
							currentContext.isFailed()
							||
							CapacityPrototypeValidator.validateOutputParameters(
								currentContext.getInvokedCapacity(),
								currentContext.getOutputValues()));
				}
				catch(AssertionError ae) {
					throw ae;
				}
				catch (Throwable e) {
					currentContext.fail(e);
				}
			}
			
			return currentContext; 
		}
		
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class DifferedCapacityInvocation implements Future<GroupCapacityContext> {

		private Future<GroupCapacityContext> future;
		private GroupCapacityContext context;
		private final AgentAddress owner;
		
		/**
		 * @param context
		 * @param future
		 * @param owner
		 */
		public DifferedCapacityInvocation(
				GroupCapacityContext context,
				Future<GroupCapacityContext> future,
				AgentAddress owner) {
			this.future = future;
			this.context = context;
			this.owner = owner;
		}
		
		/** Replies the owner of this invocation.
		 * 
		 *  @return the owner of this invocation.
		 */
		public AgentAddress getOwner() {
			return this.owner;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			if (this.future!=null
				&& this.future.cancel(mayInterruptIfRunning)) {
				this.future = null;
				return true;
			}
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupCapacityContext get() throws InterruptedException, ExecutionException {
			if (this.future==null) return this.context;
			return this.future.get();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupCapacityContext get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException,
				TimeoutException {
			if (this.future==null) return this.context;
			return this.future.get(timeout, unit);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isCancelled() {
			return (this.future==null) || this.future.isCancelled();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isDone() {
			return (this.future==null) || this.future.isDone();
		}
		
	}
	
}
