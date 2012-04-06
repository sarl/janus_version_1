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
package org.janusproject.kernel.locale;

import java.text.MessageFormat;

import org.arakhne.vmutil.locale.Locale;

/**
 * This utility class permits a easier use of localized strings.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @deprecated see {@link Locale}
 */
@Deprecated
public class LocalizedString extends Locale {
	
	/**
     * Replies the text that corresponds to the specified resource.
     * <p>
     * This function does not apply the {@link MessageFormat message formatter}.
     * The string read from the property file is directly replied.
     * 
     * @param resourcePath is the name (path) of the resource file
     * @param key is the name of the resource into the specified file
     * @param defaultValue is the default value to replies if the resource does not contain the specified key. 
     * @return the text that corresponds to the specified resource
     * @deprecated see {@link Locale#getStringWithDefaultFrom(String, String, String, Object...)}
     */
	@Deprecated
    public static String getString(String resourcePath, String key, String defaultValue) {
		return Locale.getStringWithDefaultFrom(resourcePath, key, defaultValue);
    }

	/**
     * Replies the text that corresponds to the specified resource.
     * <p>
     * This function does not apply the {@link MessageFormat message formatter}.
     * The string read from the property file is directly replied.
     * 
     * @param resourcePath is the name (path) of the resource file
     * @param key is the name of the resource into the specified file
     * @return the text that corresponds to the specified resource, or <code>null</code>
     * @deprecated see {@link Locale#getStringWithDefault(String, String, Object...)}
     */
    @Deprecated
    public static String getString(String resourcePath, String key) {
    	return getString(resourcePath, key, null);
    }

	/**
     * Replies the text that corresponds to the specified resource.
     * <p>
     * This function does not apply the {@link MessageFormat message formatter}.
     * The string read from the property file is directly replied.
     * 
     * @param resourceType is the name of the type associated to the resource file
     * @param key is the name of the resource into the specified file
     * @param defaultValue is the default value to replies if the resource does not contain the specified key. 
     * @return the text that corresponds to the specified resource
     * @deprecated see {@link Locale#getStringWithDefault(Class, String, String, Object...)}
     */
    @Deprecated
    public static String getString(Class<?> resourceType, String key, String defaultValue) {
        return Locale.getStringWithDefault(resourceType, key, defaultValue);
    }
    
	/**
     * Replies the text that corresponds to the specified resource.
     * <p>
     * This function does not apply the {@link MessageFormat message formatter}.
     * The string read from the property file is directly replied.
     * 
     * @param resourceType is the name of the type associated to the resource file
     * @param key is the name of the resource into the specified file
     * @return the text that corresponds to the specified resource, or <code>null</code>
     * @deprecated see {@link Locale#getString(Class, String, Object...)}
     */
    @Deprecated
    public static String getString(Class<?> resourceType, String key) {
    	return Locale.getString(resourceType, key);
    }
    
	/**
     * Replies the text that corresponds to the specified resource.
     * <p>
     * This function applies the {@link MessageFormat message formatter}.
     * This function replaces all occurences of <code>#number</code> in
     * the localized string by the string at position <code>number</code> in
     * <var>params</var>.
     * <p>
     * This function is inspirated by the Locale class in &laquo;SeT 
     * Foundation Classes.&raquo;
     * 
     * @param resourcePath is the name (path) of the resource file
     * @param key is the name of the resource into the specified file
     * @param params are the values to put inside the replied string in place of # parameters.
     * @return the text that corresponds to the specified resource
     * @deprecated see {@link Locale#getStringFrom(String, String, Object...)} 
     */
    @Deprecated
    public static String get(String resourcePath, String key, Object... params) {
    	return Locale.getStringFrom(resourcePath, key, params);
    }

	/**
     * Replies the text that corresponds to the specified resource.
     * <p>
     * This function applies the {@link MessageFormat message formatter}.
     * This function replaces all occurences of <code>{number}</code> in
     * the localized string by the string at position <code>number</code> in
     * <var>params</var>.
     * <p>
     * This function is inspirated by the Locale class in &laquo;SeT 
     * Foundation Classes.&raquo;
     * 
     * @param resourceType is the name of the type associated to the resource file
     * @param key is the name of the resource into the specified file
     * @param params are the values to put inside the replied string in place of # parameters.
     * @return the text that corresponds to the specified resource
     * @deprecated see {@link Locale#getString(Class, String, Object...)}
     */
    @Deprecated
    public static String get(Class<?> resourceType, String key, Object... params) {
    	return Locale.getString(resourceType, key, params);
   }

}