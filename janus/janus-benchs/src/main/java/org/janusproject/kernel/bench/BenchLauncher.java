/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.janusproject.kernel.bench;

import java.io.File;
import java.lang.reflect.Constructor;

import org.janusproject.kernel.bench.api.Bench;

/** Run the benchs.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class BenchLauncher {

	/**
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		File output = new File(args[0]);
		float progression = Float.parseFloat(args[1]);
		float progressionWindow = Float.parseFloat(args[2]);
		float progressionPerClass = progressionWindow / (args.length - 3);
		for(int i=3; i<args.length; ++i) {
			Class<?> type = Class.forName(args[i]);
			if (Bench.class.isAssignableFrom(type)) {
				Class<? extends Bench<?>> benchType = (Class<? extends Bench<?>>)type; 
				Constructor<? extends Bench<?>> cons = benchType.getConstructor(File.class);
				Bench<?> bench = cons.newInstance(output);
				bench.runBenchs(progression, progressionPerClass);
				bench = null;
				for(int j=0; j<6; ++j) {
					System.gc();
				}
			}
			progression += progressionPerClass;
		}
		System.exit(0);
	}
	
}