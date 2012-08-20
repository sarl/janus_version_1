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
package org.janusproject.acl.encoding;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.janusproject.acl.ACLMessageContent;
import org.janusproject.acl.Performative;

/**
 * Helper used to parse and rebuild the decoded {@link ACLMessageContent}.
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class StringACLCodecHelper {

	private static String PERFORMATIVE_PATTERN = "(\\()(.*)"; //$NON-NLS-1$
	private static int PERFORMATIVE_GROUP = 2;

	private static String SENDER_PATTERN = "(:sender)(\\s+)(\\()(\\s*)(agent-identifier)(\\s+)(:name)(\\s+)([a-zA-Z0-9_-]+)(\\s*)(\\))"; //$NON-NLS-1$
	private static int SENDER_GROUP = 9;
	
	private static String RECEIVER_PATTERN = "(:receiver)(\\s+)(\\()(\\s+)(set)(\\s+)(.*)(\\s*)(\\))"; //$NON-NLS-1$
	//private static int RECEIVER_GROUP = 7;
	
	private static String RECEIVER_ITEM_PATTERN = "(\\()(\\s*)(agent-identifier)(\\s+)(:name)(\\s+)([a-zA-Z0-9_-]+)(\\s*)(\\))"; //$NON-NLS-1$
	private static int RECEIVER_ITEM_GROUP = 7;
	
	private static String CONTENT_PATTERN = "(:content)(\\s+)(\\\")(.*)(\\\")(\\s*)"; //$NON-NLS-1$
	private static int CONTENT_GROUP = 4;
	
	private static String ENCODING_PATTERN = "(:encoding)(\\s+)(.*)(\\s*)"; //$NON-NLS-1$
	private static int ENCODING_GROUP = 3;
	
	private static String LANGUAGE_PATTERN = "(:language)(\\s+)(.*)(\\s*)"; //$NON-NLS-1$
	private static int LANGUAGE_GROUP = 3;
	
	private static String ONTOLOGY_PATTERN = "(:ontology)(\\s+)(.*)(\\s*)"; //$NON-NLS-1$
	private static int ONTOLOGY_GROUP = 3;
	
	private static String PROTOCOL_PATTERN = "(:protocol)(\\s+)(.*)(\\s*)"; //$NON-NLS-1$
	private static int PROTOCOL_GROUP = 3;
	
	private static String CONVERSATION_ID_PATTERN = "(:conversation-id)(\\s+)(.*)(\\s*)"; //$NON-NLS-1$
	private static int CONVERSATION_ID_GROUP = 3;
	
	private static String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"; //$NON-NLS-1$
	
	/**
	 * @param str
	 * @return true if the specified string is a performative, false otherwise
	 */
	public static boolean isPerformative( String str ){
		return doesPatternMatch( str, PERFORMATIVE_PATTERN );
	}
	
	/**
	 * @param str
	 * @return the performative contained in the specified string
	 */
	public static int getPerformative( String str ){
		return Performative.valueOf( getMatch( str, PERFORMATIVE_PATTERN, PERFORMATIVE_GROUP ) ).ordinal();
	}
	
	/**
	 * @param str
	 * @return true if the specified string corresponds to a sender pattern, false otherwise
	 */
	public static boolean isSender( String str ){
		return doesPatternMatch( str, SENDER_PATTERN );
	}
	
	/**
	 * @param str
	 * @return the sender contained in the specified string
	 */
	public static String getSender( String str ){
		return getMatch( str, SENDER_PATTERN, SENDER_GROUP );
	}

	/**
	 * @param str
	 * @return true if the specified string corresponds to a receiver pattern, false otherwise
	 */
	public static boolean isReceiver( String str ){
		return doesPatternMatch( str, RECEIVER_PATTERN );
	}
	
	/**
	 * @param str
	 * @return the receiver list contained in the specified string
	 */
	public static ArrayList<String> getReceiversList( String str ){
		return getMatches(str, RECEIVER_ITEM_PATTERN, RECEIVER_ITEM_GROUP);
	}
	
	/**
	 * @param str
	 * @return  true if the specified string is a message content, false otherwise
	 */
	public static boolean isContent( String str ){
		return doesPatternMatch( str, CONTENT_PATTERN );
	}
	
	/**
	 * @param str
	 * @return the message content contained in the specified string
	 */
	public static String getContent( String str ){
		return getMatch( str, CONTENT_PATTERN, CONTENT_GROUP );
	}

	/**
	 * @param str
	 * @return  true if the specified string is an encosing specification, false otherwise
	 */
	public static boolean isEncoding( String str ){
		return doesPatternMatch( str, ENCODING_PATTERN );
	}
	
	/**
	 * @param str
	 * @return the encoding descriptor contained in the specified string
	 */
	public static String getEncoding( String str ){
		return getMatch( str, ENCODING_PATTERN, ENCODING_GROUP );
	}
	
	/**
	 * @param str
	 * @return  true if the specified string is a language descriptor, false otherwise
	 */
	public static boolean isLanguage( String str ){
		return doesPatternMatch( str, LANGUAGE_PATTERN );
	}
	
	/**
	 * @param str
	 * @return the language descriptor contained in the specified string
	 */
	public static String getLanguage( String str ){
		return getMatch( str, LANGUAGE_PATTERN, LANGUAGE_GROUP );
	}
	
	/**
	 * @param str
	 * @return  true if the specified string is an ontology descriptor, false otherwise
	 */
	public static boolean isOntology( String str ){
		return doesPatternMatch( str, ONTOLOGY_PATTERN );
	}
	
	/**
	 * @param str
	 * @return the ontology descriptor contained in the specified string
	 */
	public static String getOntology( String str ){
		return getMatch( str, ONTOLOGY_PATTERN, ONTOLOGY_GROUP );
	}
	
	/**
	 * @param str
	 * @return  true if the specified string is a protocol descriptor, false otherwise
	 */
	public static boolean isProtocol( String str ){
		return doesPatternMatch( str, PROTOCOL_PATTERN );
	}
	
	/**
	 * @param str
	 * @return the protocol descriptor contained in the specified string
	 */
	public static String getProtocol( String str ){
		return getMatch( str, PROTOCOL_PATTERN, PROTOCOL_GROUP );
	}
	
	/**
	 * @param str
	 * @return  true if the specified string is a conversation id, false otherwise
	 */
	public static boolean isConversationId( String str ){
		return doesPatternMatch( str, CONVERSATION_ID_PATTERN );
	}
	
	/**
	 * @param str
	 * @return the conversation id contained in the specified string
	 */
	public static String getConversationId( String str ){
		return getMatch( str, CONVERSATION_ID_PATTERN, CONVERSATION_ID_GROUP );
	}
	
	/**
	 * @param str
	 * @return  true if the specified string is an UUID, false otherwise
	 */
	public static boolean isUUID( String str ){
		return doesPatternMatch( str, UUID_PATTERN );
	}
	
	// Generic methods
	
	private static boolean doesPatternMatch( String str, String pattern ){
		return Pattern.compile( pattern ).matcher( str ).matches();
	}
	
	private static String getMatch( String str, String pattern, int group ){
		Matcher m = Pattern.compile( pattern ).matcher( str );
		m.find();
		String result = m.group( group );
		
		return (result == "null" ? null : result); //$NON-NLS-1$
	}

	private static ArrayList<String> getMatches( String str, String pattern, int group ){
		Matcher m = Pattern.compile( pattern ).matcher( str );
		ArrayList<String> list = new ArrayList<String>();
		
		while (m.find()) {
		    list.add( m.group( group ) );
		}
		
		return list;
	}
}
