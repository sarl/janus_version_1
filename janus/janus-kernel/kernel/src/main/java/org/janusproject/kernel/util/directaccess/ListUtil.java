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

import java.util.Comparator;
import java.util.List;

/**
 * Utilities on lists.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.4
 */
public class ListUtil {

	/** Remove the given element from the list using a dichotomic algorithm.
	 * <p>
	 * This function ensure that the comparator is invoked as: <code>comparator(data, dataAlreadyInList)</code>.
	 * 
	 * @param <E> is the type of the elements in the list.
	 * @param list is the list to change.
	 * @param comparator is the comparator of elements.
	 * @param data is the data to remove.
	 * @return <code>true</code> if the data was removed, otherwise <code>false</code>
	 */
	public static <E> boolean dichotomicRemove(List<E> list, Comparator<? super E> comparator, E data) {
		assert(list!=null);
		assert(comparator!=null);
		assert(data!=null);
		int f = 0;
		int l = list.size()-1;
		int c;
		E d;
		int cmpR;
		while (l>=f) {
			c = (f+l)/2;
			d = list.get(c);
			cmpR = comparator.compare(data, d);
			if (cmpR==0) {
				list.remove(c);
				return true;
			}
			else if (cmpR<0) {
				l = c-1;
			}
			else {
				f = c+1;
			}
		}
		return false;
	}

	/** Add the given element in the main list using a dichotomic algorithm.
	 * <p>
	 * This function ensure that the comparator is invoked as: <code>comparator(data, dataAlreadyInList)</code>.
	 * <p>
	 * If the data is al
	 * 
	 * @param <E> is the type of the elements in the list.
	 * @param list is the list to change.
	 * @param comparator is the comparator of elements.
	 * @param data is the data to insert.
	 * @param allowMultipleOccurencesOfSameValue indicates if multiple
	 * occurrences of the same value are allowed in the list.
	 * @return <code>true</code> if the data was added, otherwise <code>false</code>
	 */
	public static <E> boolean dichotomicAdd(List<E> list, Comparator<? super E> comparator, E data, boolean allowMultipleOccurencesOfSameValue) {
		assert(list!=null);
		assert(comparator!=null);
		assert(data!=null);
		int f = 0;
		int l = list.size()-1;
		int c;
		E d;
		int cmpR;
		while (l>=f) {
			c = (f+l)/2;
			d = list.get(c);
			cmpR = comparator.compare(data, d);
			if (cmpR==0 && !allowMultipleOccurencesOfSameValue) return false;
			if (cmpR<0) {
				l = c-1;
			}
			else {
				f = c+1;
			}
		}
		list.add(f, data);
		return true;
	}

	/** Replies if the given element is inside the list, using a dichotomic algorithm.
	 * <p>
	 * This function ensure that the comparator is invoked as: <code>comparator(data, dataAlreadyInList)</code>.
	 * 
	 * @param <E> is the type of the elements in the list.
	 * @param list is the list to explore.
	 * @param comparator is the comparator of elements.
	 * @param data is the data to search for.
	 * @return <code>true</code> if the data is inside the list, otherwise <code>false</code>
	 */
	public static <E> boolean dichotomicContains(List<E> list, Comparator<? super E> comparator, E data) {
		assert(list!=null);
		assert(comparator!=null);
		assert(data!=null);
		int f = 0;
		int l = list.size()-1;
		int c;
		E d;
		int cmpR;
		while (l>=f) {
			c = (f+l)/2;
			d = list.get(c);
			cmpR = comparator.compare(data, d);
			if (cmpR==0) {
				return true;
			}
			else if (cmpR<0) {
				l = c-1;
			}
			else {
				f = c+1;
			}
		}
		return false;
	}

}
