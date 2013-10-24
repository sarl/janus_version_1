/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-2012 Janus Core Developers
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
package org.janusproject.demo.jruby.market.selective;

import java.net.URL;

import org.arakhne.afc.vmutil.Resources;
import org.janusproject.jrubyengine.RubyExecutionContext;

/**
 * Launcher of JRuby Selective Market Demos
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gui.vinson@gmail.com$
 * @author $Author: renaud.buecher@utbm.fr$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {
	
	/** URL of the launching Ruby script.
	 */
	public static final URL LAUNCHING_SCRIPT = Resources.getResource(Launcher.class, "launcher.rb"); //$NON-NLS-1$
	
	/**
	 * Execute a script which is the Launcher.java
	 * from the simpleMarketSelectiv demo in ruby version
	 * @param args
	 */
	public static void main(String[] args) {
		RubyExecutionContext resc = new RubyExecutionContext();
		resc.runScript(LAUNCHING_SCRIPT);
	}

}
