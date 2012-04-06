/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011 Janus Core Developers
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
package org.janusproject.kernel.condition;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Iterator;

import org.janusproject.kernel.time.KernelTimeManager;

/** Provide time-based parameters for a Condition. 
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class TimeConditionParameterProvider implements ConditionParameterProvider {
	
	/** Number of parameters for this type of provider.
	 */
	public static final int PARAMETER_COUNT = 2;

	/** Index of the parameter that is the current time simulation
	 * as a single precision floating-point value.
	 */
	public static final int TIME_INDEX = 0;
	
	/** Name of the parameter that is the current time simulation
	 * as a single precision floating-point value.
	 */
	public static final String TIME_NAME = "time"; //$NON-NLS-1$

	/** Index of the parameter that is the current time simulation
	 * as a {@code Date}.
	 */
	public static final int DATE_INDEX = 1;
	
	/** Name of the parameter that is the current time simulation
	 * as a {@code Date}.
	 */
	public static final String DATE_NAME = "date"; //$NON-NLS-1$

	private final WeakReference<KernelTimeManager> timeManager;
	
	/**
	 * @param timeManager is the time manager to use to provide the parameters.
	 */
	public TimeConditionParameterProvider(KernelTimeManager timeManager) {
		this.timeManager = new WeakReference<KernelTimeManager>(timeManager);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getConditionParameterCount() {
		return 2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getConditionParameterAt(int index) {
		switch(index) {
		case TIME_INDEX:
			return this.timeManager.get().getCurrentTime();
		case DATE_INDEX:
			return this.timeManager.get().getCurrentDate();
		}
		throw new IndexOutOfBoundsException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getConditionParameter(String parameterName) {
		if (TIME_NAME.equals(parameterName))
			return this.timeManager.get().getCurrentTime();
		if (DATE_NAME.equals(parameterName))
			return this.timeManager.get().getCurrentDate();
		throw new IllegalArgumentException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getConditionParameterNameAt(int index) {
		switch(index) {
		case TIME_INDEX:
			return TIME_NAME;
		case DATE_INDEX:
			return DATE_NAME;
		}
		throw new IndexOutOfBoundsException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Object> getConditionParameters() {
		KernelTimeManager tm = this.timeManager.get();
		return Arrays.<Object>asList(
				tm.getCurrentTime(),
				tm.getCurrentDate()).iterator();
	}
	
}
