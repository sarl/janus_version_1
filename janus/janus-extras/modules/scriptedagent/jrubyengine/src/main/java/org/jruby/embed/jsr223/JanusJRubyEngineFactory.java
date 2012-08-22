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
package org.jruby.embed.jsr223;

import javax.script.ScriptEngine;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.util.SystemPropertyCatcher;

/**
 * Factory use to give one context execution by JRubyAgent
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gvinson$
 * @author $Author: rbuecher$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public final class JanusJRubyEngineFactory extends JRubyEngineFactory {

	@Override
	public ScriptEngine getScriptEngine() {
		LocalContextScope scope = SystemPropertyCatcher.getScope(LocalContextScope.THREADSAFE);
		LocalVariableBehavior behavior = SystemPropertyCatcher.getBehavior(LocalVariableBehavior.GLOBAL);
		boolean lazy = SystemPropertyCatcher.isLazy(false);
		ScriptingContainer container = new ScriptingContainer(scope, behavior, lazy);
		SystemPropertyCatcher.setClassLoader(container);
		SystemPropertyCatcher.setConfiguration(container);
		return new JanusJRubyEngine(container, this);
	}

}
