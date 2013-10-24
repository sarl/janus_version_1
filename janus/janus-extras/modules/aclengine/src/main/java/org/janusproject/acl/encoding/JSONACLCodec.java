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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.arakhne.afc.vmutil.locale.Locale;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.ACLMessageContent;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.AddressUtil;

/**
 * This class encodes an ACLMessageContent in JSON or decodes an
 * ACLMessageContent encoded in bytes to JSON
 * 
 * @author $Author: ngrenie$
 * @author $Author: bfeld$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class JSONACLCodec implements ACLMessageContentEncodingService {

	/**
	 * Encodes a given ACL Message into an array of bytes.
	 */
	@Override
	public byte[] encode(ACLMessage aMsg) {

		Map<String, Object> output = new HashMap<String, Object>();

		// Performative
		output.put(Locale.getString(JSONACLCodec.class, "PERFORMATIVE"), aMsg //$NON-NLS-1$
				.getPerformative().ordinal());

		// Display SENDER
		AgentAddress sender = aMsg.getSender();
		if (sender != null) {
			Map<String, String> sender_infos = new HashMap<String, String>();
			sender_infos.put(Locale.getString(JSONACLCodec.class, "NAME"), //$NON-NLS-1$
					sender.getName());
			sender_infos.put(Locale.getString(JSONACLCodec.class, "ID"), sender //$NON-NLS-1$
					.getUUID().toString());
			output.put(Locale.getString(JSONACLCodec.class, "SENDER"), //$NON-NLS-1$
					sender_infos);
		}

		// Display RECEIVERS
		Collection<AgentAddress> receivers = aMsg.getReceiver();
		Collection<Map<String, String>> receivers_infos = new ArrayList<Map<String, String>>();

		if (receivers != null) {
			for (AgentAddress receiver : receivers) {
				Map<String, String> receiver_info = new HashMap<String, String>();
				receiver_info.put(Locale.getString(JSONACLCodec.class, "NAME"), //$NON-NLS-1$
						receiver.getName());
				receiver_info.put(Locale.getString(JSONACLCodec.class, "ID"), //$NON-NLS-1$
						receiver.getUUID().toString());
				receivers_infos.add(receiver_info);
			}
			output.put(Locale.getString(JSONACLCodec.class, "RECEIVERS"), //$NON-NLS-1$
					receivers_infos);
		}

		// Display CONTENT
		String content = aMsg.getContent().getContent().toString();
		if (content != null && content.length() > 0) {
			output.put(Locale.getString(JSONACLCodec.class, "CONTENT"), //$NON-NLS-1$
					content.trim());
		}

		// Display ENCODING
		output.put(Locale.getString(JSONACLCodec.class, "ENCODING"), //$NON-NLS-1$
				aMsg.getEncoding());

		// Display LANGUAGE
		output.put(Locale.getString(JSONACLCodec.class, "LANGUAGE"), //$NON-NLS-1$
				aMsg.getLanguage());

		// Display ONTOLOGY
		output.put(Locale.getString(JSONACLCodec.class, "ONTOLOGY"), //$NON-NLS-1$
				aMsg.getOntology());

		// Display PROTOCOL
		output.put(Locale.getString(JSONACLCodec.class, "PROTOCOL"), aMsg //$NON-NLS-1$
				.getProtocol().getName());

		// Display CONVERSATION ID
		UUID conversationId = aMsg.getConversationId();
		if (conversationId != null) {
			output.put(Locale.getString(JSONACLCodec.class, "CONVERSATIONID"), //$NON-NLS-1$
					aMsg.getConversationId().toString());
		}

		return fromMap(output);
	}

	/**
	 * Decodes a given array of bytes supposed to correspond to the
	 * ACLMessageContent.
	 */
	@Override
	public ACLMessageContent decode(byte[] byteMsg, Object... parameters) {
		ACLMessage.Content content = new ACLMessage.Content();

		Map<String, Object> json = fromBytes(byteMsg);

		// PERFORMATIVE
		content.setPerformative((Integer) json.get(Locale.getString(
				JSONACLCodec.class, "PERFORMATIVE"))); //$NON-NLS-1$

		// SENDER
		if (json.containsKey(Locale.getString(JSONACLCodec.class, "SENDER"))) { //$NON-NLS-1$
			@SuppressWarnings("unchecked")
			String sender_id = (String) ((Map<String, Object>) json.get(Locale
					.getString(JSONACLCodec.class, "SENDER"))).get(Locale //$NON-NLS-1$
					.getString(JSONACLCodec.class, "ID")); //$NON-NLS-1$
			content.setSender(AddressUtil.createAgentAddress(UUID
					.fromString(sender_id)));
		}

		// RECEIVERS
		if (json.containsKey(Locale.getString(JSONACLCodec.class, "RECEIVERS"))) { //$NON-NLS-1$
			@SuppressWarnings("unchecked")
			ArrayList<Map<String, Object>> json_receivers = (ArrayList<Map<String, Object>>) json
					.get(Locale.getString(JSONACLCodec.class, "RECEIVERS")); //$NON-NLS-1$
			Collection<AgentAddress> receivers = new ArrayList<AgentAddress>();

			for (int i = 0; i < json_receivers.size(); i++) {
				String receiver_info = (String) json_receivers.get(i).get(
						Locale.getString(JSONACLCodec.class, "ID")); //$NON-NLS-1$
				receivers.add(AddressUtil.createAgentAddress(UUID
						.fromString(receiver_info)));
			}
			content.setReceiver(receivers);
		}

		// CONTENT
		if (json.containsKey(Locale.getString(JSONACLCodec.class, "CONTENT"))) { //$NON-NLS-1$
			content.setContent(new StringBuffer((String) json.get(Locale
					.getString(JSONACLCodec.class, "CONTENT")))); //$NON-NLS-1$
		}

		// ENCODING
		content.setEncoding((String) json.get(Locale.getString(
				JSONACLCodec.class, "ENCODING"))); //$NON-NLS-1$

		// LANGUAGE
		if (json.containsKey(Locale.getString(JSONACLCodec.class, "LANGUAGE"))) { //$NON-NLS-1$
			content.setLanguage((String) json.get(Locale.getString(
					JSONACLCodec.class, "LANGUAGE"))); //$NON-NLS-1$
		}

		// ONTOLOGY
		if (json.containsKey(Locale.getString(JSONACLCodec.class, "ONTOLOGY"))) { //$NON-NLS-1$
			content.setOntology((String) json.get(Locale.getString(
					JSONACLCodec.class, "ONTOLOGY"))); //$NON-NLS-1$
		}

		// PROTOCOL
		content.setProtocol((String) json.get(Locale.getString(
				JSONACLCodec.class, "PROTOCOL"))); //$NON-NLS-1$

		// CONVERSATION ID
		if (json.containsKey(Locale.getString(JSONACLCodec.class,
				"CONVERSATIONID")) //$NON-NLS-1$
				&& json.get(Locale.getString(JSONACLCodec.class,
						"CONVERSATIONID")) != null) { //$NON-NLS-1$

			try {
				String uuid = (String) json.get(Locale.getString(
						JSONACLCodec.class, "CONVERSATIONID")); //$NON-NLS-1$
				content.setConversationId(UUID.fromString(uuid));
			} catch (IllegalArgumentException e) {
				content.setConversationId(null);
			}
		}

		return content;
	}

	/**
	 * Return JSON ObjectMapper, used to convert Map to JSON String
	 * @return a new object mapper
	 */
	@SuppressWarnings("static-method")
	protected ObjectMapper getMapper() {
		return new ObjectMapper();
	}

	/**
	 * Convert a Map to byte[] using getMapper
	 */
	private byte[] fromMap(Map<String, Object> m) {
		ObjectMapper mapper = getMapper();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			mapper.writeValue(baos, m);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(new String(baos.toByteArray()));
		return baos.toByteArray();
	}

	/**
	 * Convert a byte[] to Map using getMapper
	 */
	private Map<String, Object> fromBytes(byte[] byteMsg) {
		ObjectMapper mapper = getMapper();
		try {
			return mapper.readValue(byteMsg,
					new TypeReference<Map<String, Object>>() {
						//
					});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
