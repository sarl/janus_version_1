/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012-2013 Janus Core Developers
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
package org.janusproject.kernel.network.zeromq.zeromq;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.MACNumber;
import org.arakhne.afc.vmutil.locale.Locale;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.network.NetworkListener;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQException;

/** Description of a ZeroMQ node.
 * 
 * @author $Author: bfeld$
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ZeroMQNode {
	// Node infos
	private UUID id = null;

	// UDP Broadcasting
	private InetAddress multicastGroup = null;
	private MulticastSocket udpListener = null;

	// ZMQ Part
	private Context context = null;
	private Poller poller = null;
	private Socket pubSocket = null;
	private Integer pubPort = null;
	private Socket serverSocket = null;
	private Integer serverPort = null;
	private Socket subSocket = null;

	private Integer subPollinId = null;
	private Integer serverPollinId = null;

	// Logging
	private Logger logger = null;

	private final AtomicBoolean ready = new AtomicBoolean(false);

	private NetworkListener listener = null;

	private String applicationName = null;

	/**
	 * Initialize a zeromq node.
	 * 
	 * @param kernelAddress is the address of the Janus kernel that is owning this node.
	 * @param multicastGroupAddress is the address of the multicast group to join.
	 * @throws IOException
	 */
	public void init(AgentAddress kernelAddress, InetAddress multicastGroupAddress) throws IOException {
		// Node infos
		this.id = kernelAddress.getUUID();
		this.multicastGroup = multicastGroupAddress;

		// UDP Broadcasting
		this.udpListener = new MulticastSocket(1600);
		this.udpListener.joinGroup(this.multicastGroup);
		this.udpListener.setSoTimeout(1);

		// Logger
		this.logger = Logger.getLogger(this.id.toString());

		// ZMQ
		this.context = ZMQ.context(1);

		try {
			this.pubSocket = this.context.socket(ZMQ.PUB);
			this.pubPort = this.pubSocket.bindToRandomPort("tcp://*"); //$NON-NLS-1$
			this.serverSocket = this.context.socket(ZMQ.ROUTER);
			this.serverPort = this.serverSocket.bindToRandomPort("tcp://*"); //$NON-NLS-1$
			this.subSocket = this.context.socket(ZMQ.SUB);

			// Set sockets identity
			this.pubSocket.setIdentity(this.id.toString().getBytes());
			this.serverSocket.setIdentity(this.id.toString().getBytes());
			this.subSocket.setIdentity(this.id.toString().getBytes());
		} catch (ZMQException e) {
			e.printStackTrace();
			System.exit(1);
		}

		this.poller = new Poller(2);
		this.subPollinId = this.poller.register(this.subSocket,
				Poller.POLLIN);
		this.serverPollinId = this.poller.register(this.serverSocket,
				Poller.POLLIN);
	}

	/**
	 * Destroy a zeromq node and reset its properties.
	 * 
	 * @throws IOException
	 */
	public void destroy() throws IOException {
		this.subPollinId = null;
		this.serverPollinId = null;
		this.poller.unregister(this.serverSocket);
		this.poller = null;

		this.subSocket.close();
		this.subSocket = null;
		this.serverPort = null;
		this.serverSocket.close();
		this.serverSocket = null;
		this.pubPort = null;
		this.pubSocket.close();
		this.pubSocket = null;
		
		this.context.close();
		this.context = null;

		this.udpListener.leaveGroup(this.multicastGroup);
		this.udpListener.close();
		this.udpListener = null;

		this.logger = null;

		this.multicastGroup = null;
		this.id = null;
	}

	private Map<String, Object> getRegistrationInfos() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id", this.id); //$NON-NLS-1$
		data.put("pub_port", this.pubPort); //$NON-NLS-1$
		data.put("server_port", this.serverPort); //$NON-NLS-1$
		return data;
	}

	/** Register this node other the network.
	 * 
	 * @throws IOException
	 */
	public void register() throws IOException {
		if(this.applicationName != null && !this.applicationName.isEmpty()) {
			this.subSocket.subscribe(this.applicationName.getBytes());
		}

		byte[] buf = null;
		DatagramPacket packet;
		this.logger.info(Locale.getString("SEND", fromMap(getRegistrationInfos()))); //$NON-NLS-1$
		buf = fromMap(getRegistrationInfos()).getBytes();
		packet = new DatagramPacket(buf, buf.length, this.multicastGroup, 1600);

		this.udpListener.send(packet);

		this.ready.set(true);
	}

	/** Unregister this node other the network.
	 * 
	 * @throws IOException
	 */
	public void unregister() throws IOException {
		this.ready.set(false);

		//TODO: send unregistration other the multicast group
		/*byte[] buf = null;
		DatagramPacket packet;
		this.logger.info(Locale.getString("SEND", fromMap(getRegistrationInfos()))); //$NON-NLS-1$
		buf = fromMap(getRegistrationInfos()).getBytes();
		packet = new DatagramPacket(buf, buf.length, this.multicastGroup, 1600);

		this.udpListener.send(packet);*/

		if(this.applicationName != null && !this.applicationName.isEmpty()) {
			this.subSocket.unsubscribe(this.applicationName.getBytes());
		}

	}

	private void processRegistration(Map<String, Object> data, String hostname,
			boolean reply) throws IOException {
		if (((String) data.get("id")).equals(this.id.toString())) { //$NON-NLS-1$
			this.logger.warning(Locale.getString("MYSELF_REGISTRATION_PACK")); //$NON-NLS-1$
			return;
		}
		this.logger.info(Locale.getString("REGISTRATION_PACK", data)); //$NON-NLS-1$

		connectSubSocket(data, hostname);

		if (reply) {
			sendRegistrationTo(data, hostname);
		}

	}

	private void connectSubSocket(Map<String, Object> data, String hostname) {
		String pubAddress = String.format("tcp://%s:%s", hostname, //$NON-NLS-1$
				data.get("pub_port")); //$NON-NLS-1$
		this.logger.info(Locale.getString("CONNECT_SUBSOCKET", pubAddress)); //$NON-NLS-1$
		this.subSocket.connect(pubAddress);
	}

	private static InetAddress getPrimaryIP() throws IOException {
		for(InetAddress adr : MACNumber.getPrimaryAdapterAddresses()) {
			return adr;
		}
		return InetAddress.getLocalHost();
	}

	private void sendRegistrationTo(Map<String, Object> data, String hostname) throws IOException {
		String serverAddress = String.format("tcp://%s:%s", hostname, //$NON-NLS-1$
				data.get("server_port")); //$NON-NLS-1$
		Socket client = this.context.socket(ZMQ.DEALER);
		try {
			this.logger.info(Locale.getString("SEND_REGISTRATION", serverAddress)); //$NON-NLS-1$
			client.connect(serverAddress);

			// Register info
			Map<String, Object> infos = this.getRegistrationInfos();
			infos.put("address", getPrimaryIP()); //$NON-NLS-1$

			// Send packet
			client.send("".getBytes(), ZMQ.SNDMORE); //$NON-NLS-1$
			client.send("register".getBytes(), ZMQ.SNDMORE); //$NON-NLS-1$
			client.send(fromMap(infos).getBytes(), 0);
		}
		finally {
			client.close();
		}
	}

	/** Run the ZeroMQ node behavior.
	 */
	public void run() {

		if (!this.ready.get()) {
			return;
		}

		try {
			receiveUDP();

			byte[] message;
			// Poll ZMQ sockets
			// TODO: Change unit depending on ZEROMQ version
			this.poller.poll(2000);

			// Sub socket
			if (this.poller.pollin(this.subPollinId)) {
				String dest = new String(this.subSocket.recv(0));
				String message_type = new String(this.subSocket.recv(0));
				message = this.subSocket.recv(0);
				this.processSubMessage(dest, message_type, message);
			}
			// Server socket
			if (this.poller.pollin(this.serverPollinId)) {
				message = this.serverSocket.recv(0);
				this.logger.info(Locale.getString("GET_MESSAGE", message)); //$NON-NLS-1$
				if ("".equals(new String(message))) { //$NON-NLS-1$
					String messageType = new String(this.serverSocket.recv(0));
					this.logger.info(Locale.getString("MESSAGE_TYPE", messageType)); //$NON-NLS-1$

					if ("register".equals(messageType)) { //$NON-NLS-1$
						Map<String, Object> payload = fromBytes(this.serverSocket
								.recv(0));
						processRegistration(payload,
								(String) payload.get("address"), false); //$NON-NLS-1$
					}
				}
			}
		}
		catch(IOException e) {
			this.logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}

	}

	private void receiveUDP() throws IOException {
		boolean interupted = false;

		// Receive udp
		byte[] buf = new byte[1000];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		try {
			this.udpListener.receive(packet);
		} catch (InterruptedIOException e) {
			interupted = true;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		if (!interupted) {
			// Process udp packet
			processRegistration(fromBytes(buf), packet.getAddress()
					.getCanonicalHostName(), true);
		}
	}

	@SuppressWarnings("unchecked")
	private void processSubMessage(String dest, String messageType,
			byte[] data) throws IOException {
		this.logger.info(Locale.getString("PROCESS_SUBMESSAGE", dest, messageType, data)); //$NON-NLS-1$
		if ("localGroupCreated".equals(messageType)) { //$NON-NLS-1$
			Map<String, Object> message = fromBytes(data);
			String organizationClass = (String) message.get("organization"); //$NON-NLS-1$
			UUID uuid = UUID.fromString((String) message.get("groupId")); //$NON-NLS-1$
			String groupName = (String) message.get("groupName"); //$NON-NLS-1$
			Collection<? extends GroupCondition> obtainConditions = (Collection<? extends GroupCondition>) message
					.get("obtainConditions"); //$NON-NLS-1$
			Collection<? extends GroupCondition> leaveConditions = (Collection<? extends GroupCondition>) message
					.get("leaveConditions"); //$NON-NLS-1$
			Boolean persistence = (Boolean) message.get("persistent"); //$NON-NLS-1$
			MembershipService membership = (MembershipService) SerializationUtil
					.decode((String) message.get("membership")); //$NON-NLS-1$
			try {
				informDistantGroupDiscovered(organizationClass, uuid,
						groupName, obtainConditions, leaveConditions,
						persistence, membership);
				this.logger.info(Locale.getString("DISTANT_GROUP_DISCOVERED", uuid)); //$NON-NLS-1$
			}
			catch (ClassNotFoundException e) {
				this.logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
		else if ("broadcast".equals(messageType)) { //$NON-NLS-1$
			Message m = (Message) SerializationUtil.decode(new String(data));
			this.logger.info(Locale.getString("RECEIVE_BROADCAST_MESSAGE", m)); //$NON-NLS-1$
			if (m.getReceiver() instanceof RoleAddress) {
				RoleAddress address = m.getReceiver();
				Class<? extends Role> receiverRole = address.getRole();
				this.listener.receiveOrganizationalDistantMessage(
						address.getGroup(), receiverRole, m, true);
			}
		}
	}

	/** Publish the given data on the socket of the given dest.
	 * 
	 * @param dest is the identifier of the dest.
	 * @param messageType
	 * @param data
	 */
	public void publish(UUID dest, String messageType, byte[] data) {
		publish(dest.toString(), messageType, data);
	}

	private void publish(String dest, String messageType, byte[] data) {
		this.logger.info(
				Locale.getString("PUBLISH", messageType, new String(data), dest)); //$NON-NLS-1$
		this.pubSocket.send(dest.toString().getBytes(), ZMQ.SNDMORE);
		this.pubSocket.send(messageType.getBytes(), ZMQ.SNDMORE);
		this.pubSocket.send(data, 0);
	}

	/** Publish the given data on the application socket.
	 * 
	 * @param messageType
	 * @param data
	 * @throws IOException
	 */
	public void publishToApplication(String messageType, Map<String, Object> data) throws IOException {
		publish(this.applicationName, messageType, fromMap(data).getBytes());
	}

	/** Publish the given data to the given dest.
	 * 
	 * @param dest
	 * @param messageType
	 * @param data
	 * @throws IOException
	 */
	public void publish(String dest, String messageType, Map<String, Object> data) throws IOException {
		publish(dest, messageType, fromMap(data).getBytes());
	}

	/** Subscribe to the sub socket associated to the specified agent address.
	 *  
	 * @param agentAddress
	 */
	public void subscribe(UUID agentAddress) {
		this.logger.info(Locale.getString("SUBSCRIBE_TO", agentAddress)); //$NON-NLS-1$
		this.subSocket.subscribe(agentAddress.toString()
				.getBytes());
	}

	/** Unsubscribe to the socket associated to the specified agent address.
	 *  
	 * @param agentAddress
	 */
	public void unsubscribe(UUID agentAddress) {
		this.logger.info(Locale.getString("UNSUBSCRIBE_TO", agentAddress)); //$NON-NLS-1$
		this.subSocket.unsubscribe(agentAddress.toString()
				.getBytes());
	}

	// Encoding/Decoding utils
	private static String fromMap(Map<String, Object> m) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Writer strWriter = new StringWriter();
		try {
			mapper.writeValue(strWriter, m);
		}
		finally {
			strWriter.close();
		}
		return strWriter.toString();
	}

	private static Map<String, Object> fromBytes(byte[] byteMsg) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(byteMsg, new TypeReferenceImpl());
	}

	// Connection with listener

	/** Set the listener to notify when a network event occurs.
	 * 
	 * @param listener
	 */
	public void setNetworkAdapterListener(NetworkListener listener) {
		this.listener = listener;
	}

	/**
	 * Notifies listeners that a distant group was discovered.
	 * 
	 * @param organizationClass
	 *            is the name of the organization, ie. its classname.
	 * @param uuid
	 *            is the identifier of the distant group.
	 * @param groupName
	 *            is the name of the distant group.
	 * @param obtainConditions
	 *            are the conditions to enter in the distant group.
	 * @param leaveConditions
	 *            are the conditions to leave from the distant group.
	 * @param persistence indicates if the group may be persistent. A <code>null</code>
	 * value indicates to use the default configuration.
	 * @param membership
	 *            is the membership checker of the distant group.
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void informDistantGroupDiscovered(String organizationClass,
			UUID uuid, String groupName,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			Boolean persistence, MembershipService membership)
					throws ClassNotFoundException {
		if (this.listener != null) {
			Class<? extends Organization> org = (Class<? extends Organization>) Class
					.forName(organizationClass);
			this.listener.distantGroupDiscovered(org, uuid, obtainConditions,
					leaveConditions, membership, persistence, groupName);
		}
	}

	/** Change the name of the application supported by this node.
	 * 
	 * @param appName
	 */
	public void setApplicationName(String appName) {
		this.applicationName = appName;
	}

	/** Description of a ZeroMQ node.
	 * 
	 * @author $Author: bfeld$
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class TypeReferenceImpl extends TypeReference<Map<String, Object>> {

		/**
		 */
		public TypeReferenceImpl() {
			//
		}

	}

}
