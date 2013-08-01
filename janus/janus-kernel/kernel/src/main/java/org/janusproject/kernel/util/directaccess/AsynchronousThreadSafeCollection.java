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
package org.janusproject.kernel.util.directaccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.janusproject.kernel.util.comparator.ComparableComparator;
import org.janusproject.kernel.util.comparator.GenericComparator;
import org.janusproject.kernel.util.event.ListenerCollection;

/**
 * A thread-safe array list which permits to
 * iterator one the collection and to modify the collection
 * at same time.
 * <p>
 * This implementation uses synchronization and
 * ensure that iterators use a internal list which is
 * not modified by additions and removal functions.
 * <p>
 * User of this collection may invoke {@link #applyChanges(boolean)}
 * to be sure that additions and removals are applied.
 * When the function {@linkn #isAutoApplyEnabled()} replies <code>true</code>,
 * the function {@link #applyChanges(boolean)} is automatically invoked as soon
 * as possible. If the replied value is <code>false</code>, this collection
 * never invokes {@link #applyChanges(boolean)} automatically.
 * By default {@link #isAutoApplyEnabled()} replies <code>true</code>.
 * 
 * @param <E> is the type of elements in the collections.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AsynchronousThreadSafeCollection<E>
implements DirectAccessCollection<E> {

	private final Class<E> elementType;
	private final List<E> currentElements;
	private final List<E> addedElements = new ArrayList<E>();
	private final List<E> removedElements = new ArrayList<E>();
	private boolean clearedListFlag = false;
	private boolean autoApply = true;
	private boolean isSet = false;
	
	private final Set<SafeIterator<E>> iterators = new TreeSet<SafeIterator<E>>(GenericComparator.SINGLETON);
	
	private final Comparator<? super E> elementComparator;
	
	private ListenerCollection<AsynchronousThreadSafeCollectionListener<E>> listeners = null;
	
	/**
	 * @param type is the type of elements in the collections.
	 */
	public AsynchronousThreadSafeCollection(Class<E> type) {
		this(type, (Comparator<? super E>)null);
	}
	
	/**
	 * @param type is the type of elements in the collections.
	 * @param comparator permits to main an order in the list, if <code>null</code>
	 * no order is applied.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AsynchronousThreadSafeCollection(Class<E> type, Comparator<? super E> comparator) {
		assert(type!=null);
		this.currentElements = new ArrayList<E>();
		this.elementType = type;
		if (comparator==null) {
			if (Comparable.class.isAssignableFrom(type)) {
				this.elementComparator = new ComparableComparator();
			}
			else {
				this.elementComparator = GenericComparator.SINGLETON;
			}
		}
		else {
			this.elementComparator = comparator;
		}
	}

	/**
	 * @param type is the type of elements in the collections.
	 * @param element
	 */
	public AsynchronousThreadSafeCollection(Class<E> type, E element) {
		this(type, element, null);
	}

	/**
	 * @param type is the type of elements in the collections.
	 * @param element
	 * @param comparator permits to main an order in the list, if <code>null</code>
	 * no order is applied.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AsynchronousThreadSafeCollection(Class<E> type, E element, Comparator<? super E> comparator) {
		this.elementType = type;
		if (element!=null)
			this.currentElements = new ArrayList<E>(Collections.singleton(element));
		else
			this.currentElements = new ArrayList<E>();
		if (comparator==null) {
			if (Comparable.class.isAssignableFrom(type)) {
				this.elementComparator = new ComparableComparator();
			}
			else {
				this.elementComparator = GenericComparator.SINGLETON;
			}
		}
		else {
			this.elementComparator = comparator;
		}
	}

	/**
	 * @param type is the type of elements in the collections.
	 * @param collection
	 */
	public AsynchronousThreadSafeCollection(Class<E> type, Collection<? extends E> collection) {
		this.elementType = type;
		if (collection==null)
			this.currentElements = new ArrayList<E>();
		else
			this.currentElements = new ArrayList<E>(collection);
		this.elementComparator = null;
	}
	
    /** Replies the comparator that is used to sort the elements in this collection.
     * 
     * @return the comparator that is used to sort the elements in this collection, never <code>null</code>.
     * @since 0.5
     */
    public final Comparator<? super E> comparator() {
    	return this.elementComparator;
    }

    /** Add listener on this collection.
	 * 
	 * @param listener
	 */
	public synchronized void addAsynchronousThreadSafeCollectionListener(AsynchronousThreadSafeCollectionListener<E> listener) {
		if (this.listeners==null)
			this.listeners = new ListenerCollection<AsynchronousThreadSafeCollectionListener<E>>();
		this.listeners.add(AsynchronousThreadSafeCollectionListener.class, listener);
	}
	
	/** Remove listener on this collection.
	 * 
	 * @param listener
	 */
	public synchronized void removeAsynchronousThreadSafeCollectionListener(AsynchronousThreadSafeCollectionListener<E> listener) {
		if (this.listeners!=null) {
			this.listeners.remove(AsynchronousThreadSafeCollectionListener.class, listener);
			if (this.listeners.isEmpty()) {
				this.listeners = null;
			}
		}
	}

	/** Fire addition event.
	 * 
	 * @param added are the added elements.
	 */
	@SuppressWarnings("unchecked")
	private void fireAddition(Collection<? extends E> added) {
		assert(added!=null && !added.isEmpty());
		if (this.listeners!=null) {
			AsynchronousThreadSafeCollectionListener<E>[] listeners = 
				this.listeners.getListeners(AsynchronousThreadSafeCollectionListener.class);
			for(AsynchronousThreadSafeCollectionListener<E> l : listeners) {
				l.asynchronouslyAdded(added);
			}
		}
	}

	/** Fire removal event.
	 * 
	 * @param removed are the removed elements.
	 */
	@SuppressWarnings("unchecked")
	private void fireRemoval(Collection<? extends E> removed) {
		assert(removed!=null && !removed.isEmpty());
		if (this.listeners!=null) {
			AsynchronousThreadSafeCollectionListener<E>[] listeners = 
				this.listeners.getListeners(AsynchronousThreadSafeCollectionListener.class);
			for(AsynchronousThreadSafeCollectionListener<E> l : listeners) {
				l.asynchronouslyRemoved(removed);
			}
		}
	}

	/** Fire addition and removal events.
	 * 
	 * @param added are the added elements.
	 * @param removed are the removed elements.
	 */
	@SuppressWarnings("unchecked")
	private void fireAdditionRemoval(Collection<? extends E> added, Collection<? extends E> removed) {
		assert(added!=null && !added.isEmpty());
		assert(removed!=null && !removed.isEmpty());
		if (this.listeners!=null) {
			AsynchronousThreadSafeCollectionListener<E>[] listeners = 
				this.listeners.getListeners(AsynchronousThreadSafeCollectionListener.class);
			for(AsynchronousThreadSafeCollectionListener<E> l : listeners) {
				l.asynchronouslyAdded(added);
				l.asynchronouslyRemoved(removed);
			}
		}
	}

	/** Replies the type of the elements.
	 * 
	 * @return the type of the elements.
	 */
	public Class<E> getElementType() {
		return this.elementType;
	}
	
	/** Replies if at least one iterator is currently
	 * going through this collection.
	 * 
	 * @return <code>true</code> if at least one iterator is iterating
	 * on this collection, otherwise <code>false</code>
	 */
	public synchronized boolean isIterated() {
		return !this.iterators.isEmpty();
	}

    /**
     * Ensures that this collection contains the specified element (optional
     * operation).  Returns <tt>true</tt> if this collection changed as a
     * result of the call.  (Returns <tt>false</tt> if this collection does
     * not permit duplicates and already contains the specified element.)<p>
     *
     * Collections that support this operation may place limitations on what
     * elements may be added to this collection.  In particular, some
     * collections will refuse to add <tt>null</tt> elements, and others will
     * impose restrictions on the type of elements that may be added.
     * Collection classes should clearly specify in their documentation any
     * restrictions on what elements may be added.<p>
     *
     * If a collection refuses to add a particular element for any reason
     * other than that it already contains the element, it <i>must</i> throw
     * an exception (rather than returning <tt>false</tt>).  This preserves
     * the invariant that a collection always contains the specified element
     * after this call returns.
     *
     * @param e element whose presence in this collection is to be ensured
     * @return <tt>true</tt> if this collection changed as a result of the
     *         call
     * @throws UnsupportedOperationException if the <tt>add</tt> operation
     *         is not supported by this collection
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this collection
     * @throws NullPointerException if the specified element is null and this
     *         collection does not permit null elements
     * @throws IllegalArgumentException if some property of the element
     *         prevents it from being added to this collection
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to insertion restrictions
     */
	public boolean add(E e) {
		return innerAdd(e);
	}
	
	private synchronized boolean innerAdd(E e) {
		if (!this.autoApply || isIterated()) {
			ListUtil.dichotomicRemove(this.removedElements, this.elementComparator, e);
			return ListUtil.dichotomicAdd(
					this.addedElements,
					this.elementComparator, e,
					!isSetBehaviorEnabled());
		}
		else if (ListUtil.dichotomicAdd(
				this.currentElements, 
				this.elementComparator,
				e,
				!isSetBehaviorEnabled())) {
			fireAddition(Collections.singleton(e));
			return true;
		}
		return false;
	}

    /**
     * Adds all of the elements in the specified collection to this collection
     * (optional operation).  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified collection is this collection, and this collection is
     * nonempty.)
     *
     * @param c collection containing elements to be added to this collection
     * @return <tt>true</tt> if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>addAll</tt> operation
     *         is not supported by this collection
     * @throws ClassCastException if the class of an element of the specified
     *         collection prevents it from being added to this collection
     * @throws NullPointerException if the specified collection contains a
     *         null element and this collection does not permit null elements,
     *         or if the specified collection is null
     * @throws IllegalArgumentException if some property of an element of the
     *         specified collection prevents it from being added to this
     *         collection
     * @throws IllegalStateException if not all the elements can be added at
     *         this time due to insertion restrictions
     * @see #add(Object)
     */
	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for(E e : c) {
			changed = innerAdd(e) | changed;
		}
		return changed;
	}

    /**
     * Removes all of the elements from this collection (optional operation).
     * The collection will be empty after this method returns.
     *
     * @throws UnsupportedOperationException if the <tt>clear</tt> operation
     *         is not supported by this collection
     */
	public void clear() {
		innerClear();
	}

	private synchronized boolean innerClear() {
		boolean changed = false;
		if (!this.autoApply || isIterated()) {
			if (!this.addedElements.isEmpty()) {
				this.addedElements.clear();
				changed = true;
			}
			if (!this.removedElements.isEmpty()) {
				this.removedElements.clear();
				changed = true;
			}
			this.clearedListFlag = true;
		}
		else if (!this.currentElements.isEmpty()) {
			List<E> removed = new ArrayList<E>(this.currentElements);
			this.currentElements.clear();
			changed = true;
			fireRemoval(removed);
		}
		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized boolean contains(Object o) {
		if (this.elementType.isInstance(o))
			return ListUtil.dichotomicContains(this.currentElements, this.elementComparator, this.elementType.cast(o));
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		for(Object o : c) {
			if (!contains(o)) return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized boolean isEmpty() {
		return this.currentElements.isEmpty();
	}

    /**
     * Returns <tt>true</tt> if this collection at least one pending
     * element for its addition in the collection.
     *
     * @return <tt>true</tt> if this collection has at least one
     * pending element for addition.
     */
    public synchronized boolean hasPendingElement() {
    	return !this.addedElements.isEmpty();
    }

    /**
     * Returns the number of pending elements for there addition
     * in this collection.  If this collection
     * contains more than <tt>Integer.MAX_VALUE</tt> pending
     * elements, returns <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of pending elements for addition in this collection
     */
    public synchronized int getPendingElementCount() {
    	return this.addedElements.size();
    }

    /**
     * Remove all the pending elements in the addition list.
     */
    public synchronized void removePendingElements() {
    	this.addedElements.clear();
    }
    
    /**
     * Removes all of this collection's elements that are also contained in the
     * specified collection (optional operation).  After this call returns,
     * this collection will contain no elements in common with the specified
     * collection.
     *
     * @param c collection containing elements to be removed from this collection
     * @return <tt>true</tt> if this collection changed as a result of the
     *         call
     * @throws UnsupportedOperationException if the <tt>removeAll</tt> method
     *         is not supported by this collection
     * @throws ClassCastException if the types of one or more elements
     *         in this collection are incompatible with the specified
     *         collection (optional)
     * @throws NullPointerException if this collection contains one or more
     *         null elements and the specified collection does not support
     *         null elements (optional), or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for(Object e : c) {
			changed = remove(e) || changed;
		}
		return changed;
	}

    /**
     * Removes a single instance of the specified element from this
     * collection, if it is present (optional operation).  More formally,
     * removes an element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>, if
     * this collection contains one or more such elements.  Returns
     * <tt>true</tt> if this collection contained the specified element (or
     * equivalently, if this collection changed as a result of the call).
     *
     * @param o element to be removed from this collection, if present
     * @return <tt>true</tt> if an element was removed as a result of this call
     * @throws ClassCastException if the type of the specified element
     * 	       is incompatible with this collection (optional)
     * @throws NullPointerException if the specified element is null and this
     *         collection does not permit null elements (optional)
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *         is not supported by this collection
     */
	public synchronized boolean remove(Object o) {
		if (o==null || !this.elementType.isInstance(o))
			return false;
		
		if (!this.autoApply || isIterated()) {
			E elt = this.elementType.cast(o);
			if (ListUtil.dichotomicRemove(this.addedElements, this.elementComparator, elt)) {
				return true;
			}
			
			if (ListUtil.dichotomicAdd(
					this.removedElements, 
					this.elementComparator, 
					this.elementType.cast(o),
					!isSetBehaviorEnabled())) {
				if (this.autoApply) {
					applyChanges(false);
				}
				return true;
			}
		}
		else {
			E elt = this.elementType.cast(o);
			if (ListUtil.dichotomicRemove(
					this.currentElements,
					this.elementComparator,
					elt)) {
				fireRemoval(Collections.singleton(elt));
				return true;
			}
		}
		
		return false;
	}

    /**
     * Removes the element at the specified position in this list (optional
     * operation).  Shifts any subsequent elements to the left (subtracts one
     * from their indices).  Returns the element that was removed from the
     * list.
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *         is not supported by this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
	public synchronized E remove(int index) {
		if (!this.autoApply || isIterated()) {
			E e = this.currentElements.get(index);
			assert(e!=null); 
		
			ListUtil.dichotomicRemove(this.addedElements, this.elementComparator, e);
			ListUtil.dichotomicAdd(
					this.removedElements,
					this.elementComparator,
					e,
					!isSetBehaviorEnabled());
			
			if (this.autoApply) {
				applyChanges(false);
			}
			
			return e;
		}
		
		return this.currentElements.remove(index);
	}

	/**
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation).  In other words, removes from
     * this collection all of its elements that are not contained in the
     * specified collection.
     *
     * @param c collection containing elements to be retained in this collection
     * @return <tt>true</tt> if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>retainAll</tt> operation
     *         is not supported by this collection
     * @throws ClassCastException if the types of one or more elements
     *         in this collection are incompatible with the specified
     *         collection (optional)
     * @throws NullPointerException if this collection contains one or more
     *         null elements and the specified collection does not permit null
     *         elements (optional), or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
	public boolean retainAll(Collection<?> c) {
		boolean changed = innerClear();
		for(Object e : c) {
			if (e!=null && this.elementType.isInstance(e))
				changed = innerAdd(this.elementType.cast(e)) | changed;
		}
		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized int size() {
		return this.currentElements.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized E get(int position) {
		return this.currentElements.get(position);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		SafeIterator<E> iterator = iterator();
		Object[] tab = new Object[iterator.totalSize()];
		try {
			E elt;
			int i=0;
			while (i<tab.length && iterator.hasNext()) {
				elt = iterator.next();
				tab[i] = elt;
				++i;
			}
		}
		finally {
			iterator.release();
		}
		return tab;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		T[] tt = a;
		SafeIterator<E> iterator = iterator();
		try {
			E elt;
			int i=0;
			if (a==null || a.length<iterator.totalSize()) {
				tt = (T[])new Object[iterator.totalSize()];
			}
			while (i<tt.length && iterator.hasNext()) {
				elt = iterator.next();
				tt[i] = (T)elt;
				++i;
			}
		}
		finally {
			iterator.release();
		}
		return tt;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SafeIterator<E> iterator() {
		return new SafeIterator<E>(this, this.currentElements.iterator());
	}
		
	/** Apply registered changes in the current content.
	 * <p>
	 * If the <var>force</var> parameter is <code>true</code>,
	 * this function applies changes and reset any internal
	 * resource related to exiting iterators.
	 * After invoking this function with <code>true</code>
	 * parameter, iterators still running
	 * on the content of this collection may throw a
	 * <code>ConcurrentModificationException</code>.
	 * <p>
	 * If the <var>force</var> parameter is <code>false</code>,
	 * the changes will be applied only if no iterator is
	 * currently running.
	 * 
	 * @param force indicates if changes may be forced or not.
	 * @return <code>true</code> if something changed in the
	 * collection, otherwise <code>false</code>
	 */
	public synchronized boolean applyChanges(boolean force) {
		boolean changed = false;
		
		if (force || !isIterated()) {

			List<E> addition = new ArrayList<E>();
			List<E> removal = new ArrayList<E>();
			
			if (this.clearedListFlag) {
				removal.addAll(this.currentElements);
				this.currentElements.clear();
				this.clearedListFlag = false;
				changed = true;
			}

			if (!this.removedElements.isEmpty()) {
				for(E e : this.removedElements) {
					if (ListUtil.dichotomicRemove(this.currentElements, this.elementComparator, e)) {
						removal.add(e);
						changed = true;
					}
				}
				this.removedElements.clear();
			}
			if (!this.addedElements.isEmpty()) {
				for(E e : this.addedElements) {
					if (ListUtil.dichotomicAdd(
							this.currentElements,
							this.elementComparator,
							e,
							!isSetBehaviorEnabled())) {
						addition.add(e);
						changed = true;
					}
				}
				this.addedElements.clear();
			}
			
			if (!addition.isEmpty()) {
				if (removal.isEmpty()) {
					fireAddition(addition);
				}
				else {
					fireAdditionRemoval(addition, removal);
				}
			}
			else if (!removal.isEmpty()) {
				fireRemoval(removal);
			}
			
			this.iterators.clear();
		}
		
		return changed;
	}
	
	/** Register the given iterator to avoid concurrent modification exceptions.
	 * 
	 * @param iterator is the new iterator.
	 */
	synchronized void allocateIterator(SafeIterator<E> iterator) {
		assert(iterator!=null);
		if (this.autoApply) applyChanges(false);
		this.iterators.add(iterator);
	}
		
	/** Unregister the given iterator.
	 * 
	 * @param iterator is the released iterator.
	 */
	synchronized void releaseIterator(SafeIterator<E> iterator) {
		assert(iterator!=null);
		this.iterators.remove(iterator);
		if (this.autoApply) applyChanges(false);
	}
	
	/** Replies if this collection tries to apply the changes as soon a possible.
	 * <p>
	 * By default the collection does not automatically apply the changes.
	 * 
	 * @return <code>true</code> if the collection tries to automatically apply
	 * the changes, otherwise <code>false</code>.
	 */
	public synchronized boolean isAutoApplyEnabled() {
		return this.autoApply;
	}

	/** Set if this collection tries to apply the changes  as soon a possible.
	 * 
	 * @param autoApply is <code>true</code> if the collection tries to automatically apply
	 * the changes, otherwise <code>false</code>.
	 */
	public synchronized void setAutoApplyEnabled(boolean autoApply) {
		this.autoApply = autoApply;
	}

	/** Replies if this collection is working as a {@code Set}
	 * or as a {@code List}.
	 * <p>
	 * By default the collection is working as a {@code List}.
	 * 
	 * @return <code>true</code> if the collection disables multiple
	 * occurences of the same value in the collection; as it is an
	 * implementation of a {@code Set}. <code>false</code> if the
	 * collection enables multiple occurences of the same value in the
	 * collection; as it is an implementation of a {@code List}.
	 * @since 0.5
	 */
	public synchronized boolean isSetBehaviorEnabled() {
		return this.isSet;
	}

	/** Set if this collection is working as a {@code Set}
	 * or as a {@code List}.
	 * 
	 * @param enable is <code>true</code> if the collection disables multiple
	 * occurences of the same value in the collection; as it is an
	 * implementation of a {@code Set}. <code>false</code> if the
	 * collection enables multiple occurences of the same value in the
	 * collection; as it is an implementation of a {@code List}.
	 * @since 0.5
	 */
	public synchronized void setSetBehaviorEnabled(boolean enable) {
		this.isSet = enable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		SafeIterator<E> iterator = iterator();
		try {
			E elt;
			b.append("["); //$NON-NLS-1$
			while (iterator.hasNext()) {
				elt = iterator.next();
				if (b.length()>1) { // because character '[' is already inside
					b.append(", "); //$NON-NLS-1$
				}
				b.append(elt);
			}
			b.append("]"); //$NON-NLS-1$
		}
		finally {
			iterator.release();
		}
		return b.toString();
	}
	
}
