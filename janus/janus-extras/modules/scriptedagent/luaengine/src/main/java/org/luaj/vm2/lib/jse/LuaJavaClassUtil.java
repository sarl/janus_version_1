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
package org.luaj.vm2.lib.jse;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

/**
 * Utility to use the Lua value for Java classes.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class LuaJavaClassUtil {

	/**
	 * Register a LUA function as a member of a Java class.
	 *  
	 * @param type is the java class to change.
	 * @param functionName is the name of the function to register.
	 * @param function is the LUA closure.
	 */
	@SuppressWarnings("unchecked")
	public static void addAgentFunction(Class<?> type, String functionName, LuaFunction function) {
		if (function!=null) {
			JavaClass javaClass = JavaClass.forClass(type);
			if (javaClass.methods==null)
				loadAgentMembers(javaClass);
			assert(javaClass.methods!=null);
			LuaValue fctName = LuaValue.valueOf(functionName);
			javaClass.methods.put(fctName, function);
		}
	}

	/** Load the members according to the Janus security guidelines for the agents.
	 * 
	 * @param value
	 */
	public static void loadAgentMembers(LuaValue value) {
		if (value instanceof JavaClass) {
			JavaClass javaClass = (JavaClass)value;
			JavaClass previousJavaClass = null;
			for(Class<?> type : getClassHierarchy(((Class<?>)javaClass.m_instance))) {
				javaClass = JavaClass.forClass(type);
				loadFields(javaClass, previousJavaClass);
				loadMethods(javaClass, previousJavaClass);
				previousJavaClass = javaClass;
			}
		}
	}

	private static List<Class<?>> getClassHierarchy(Class<?> type) {
		List<Class<?>> list = new LinkedList<Class<?>>();
		list.add(type);
		Class<?> t = type.getSuperclass();
		while (t!=null && !Object.class.equals(t)) {
			list.add(0, t);
			t = t.getSuperclass();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private static void loadFields(JavaClass javaClass, JavaClass previousJavaClass) {
		if (javaClass.fields==null) {
			Map<LuaValue,Field> m = new HashMap<LuaValue, Field>();
			if (previousJavaClass!=null && previousJavaClass.fields!=null) {
				m.putAll(previousJavaClass.fields);
			}

			for(Field field : ((Class<?>)javaClass.m_instance).getDeclaredFields()) {
				if (Modifier.isPublic(field.getModifiers())
						||Modifier.isProtected(field.getModifiers())) {
					m.put( LuaValue.valueOf(field.getName()), field );
					field.setAccessible(true);
				}
			}
			javaClass.fields = m;
		}
	}

	@SuppressWarnings("unchecked")
	private static void loadMethods(JavaClass javaClass, JavaClass previousJavaClass) {
		if (javaClass.methods==null) {
			Map<LuaValue, Object> map = new HashMap<LuaValue, Object>();
			if (previousJavaClass!=null && previousJavaClass.methods!=null) {
				map.putAll(previousJavaClass.methods);
			}

			Map<String,List<JavaMethod>> namedlists = new HashMap<String, List<JavaMethod>>();
			for(Method method : ((Class<?>)javaClass.m_instance).getDeclaredMethods()) {
				if (Modifier.isPublic(method.getModifiers())
						||Modifier.isProtected(method.getModifiers())) {
					String name = method.getName();
					List<JavaMethod> list = namedlists.get(name);
					if ( list == null )
						namedlists.put(name, list = new ArrayList<JavaMethod>());
					list.add( JavaMethod.forMethod(method) );
					method.setAccessible(true);
				}
			}

			List<JavaConstructor> list = new ArrayList<JavaConstructor>();
			for(Constructor<?> constructor : ((Class<?>)javaClass.m_instance).getDeclaredConstructors()) { 
				if (Modifier.isPublic(constructor.getModifiers())
						||Modifier.isProtected(constructor.getModifiers())) {
					list.add( JavaConstructor.forConstructor(constructor) );
					constructor.setAccessible(true);
				}
			}

			switch ( list.size() ) {
			case 0:
				break;
			case 1: map.put(JavaClass.NEW, list.get(0)); break;
			default:
				JavaConstructor[] cons = new JavaConstructor[list.size()];
				list.toArray(cons);
				map.put(
						JavaClass.NEW,
						JavaConstructor.forConstructors(cons) );
				break;
			}

			Iterator<Entry<String,List<JavaMethod>>> iterator = namedlists.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String,List<JavaMethod>> e = iterator.next();
				List<JavaMethod> methods = e.getValue();
				LuaValue key = LuaValue.valueOf(e.getKey());
				if (methods.size()==1) {
					map.put(key, methods.get(0));
				}
				else {
					JavaMethod[] meths = new JavaMethod[methods.size()];
					methods.toArray(meths);
					map.put(key, JavaMethod.forMethods(meths));
				}
			}

			javaClass.methods = map;
		}
	}

}
