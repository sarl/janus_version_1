package org.janusproject.kernel.network.zeromq.zeromq;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.network.jxta.NetworkListener;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQException;

public class ZeroMQNode {
	// Node infos
	UUID id;

	// UDP Broadcasting
	InetAddress group;
	MulticastSocket udp_listener;

	// ZMQ Part
	Context context;
	Poller poller;
	Socket pub_socket;
	Integer pub_port;
	Socket sub_socket;
	Socket server_socket;
	Integer server_port;

	Integer sub_pollin_id;
	Integer server_pollin_id;

	// Logging
	Logger logger;

	boolean ready = false;

	private NetworkListener listener;

	private String appName;

	public void init(AgentAddress kernelAddress) {

		// Node infos
		this.id = kernelAddress.getUUID();

		// Logger
		this.logger = Logger.getLogger(this.id.toString());

		// UDP Broadcasting
		try {
			this.group = InetAddress.getByName("237.252.249.227");
			this.udp_listener = new MulticastSocket(1600);
			this.udp_listener.joinGroup(this.group);
			this.udp_listener.setSoTimeout(1);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// ZMQ
		this.context = ZMQ.context(1);

		try {
			this.pub_socket = this.context.socket(ZMQ.PUB);
			this.pub_port = this.pub_socket.bindToRandomPort("tcp://*");
			this.server_socket = this.context.socket(ZMQ.ROUTER);
			this.server_port = this.server_socket.bindToRandomPort("tcp://*");
			this.sub_socket = this.context.socket(ZMQ.SUB);

			// Set sockets identity
			this.pub_socket.setIdentity(this.id.toString().getBytes());
			this.server_socket.setIdentity(this.id.toString().getBytes());
			this.sub_socket.setIdentity(this.id.toString().getBytes());
		} catch (ZMQException e) {
			e.printStackTrace();
			System.exit(1);
		}

		this.poller = context.poller(2);
		this.sub_pollin_id = this.poller.register(this.sub_socket,
				Poller.POLLIN);
		this.server_pollin_id = this.poller.register(this.server_socket,
				Poller.POLLIN);
	}

	private Map<String, Object> register_infos() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id", this.id);
		data.put("pub_port", this.pub_port);
		data.put("server_port", this.server_port);
		return data;
	}

	public void register() {
		System.out.println("Register");
		if(this.appName != null) {
			this.sub_socket.subscribe(this.appName.getBytes());
		}
		byte[] buf = null;
		DatagramPacket packet;
		logger.info("Send " + fromMap(register_infos()));
		buf = fromMap(register_infos()).getBytes();
		packet = new DatagramPacket(buf, buf.length, this.group, 1600);

		try {
			udp_listener.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		ready = true;
	}

	private void process_register(Map<String, Object> data, String hostname,
			boolean reply) {
		if (((String) data.get("id")).equals(this.id.toString())) {
			logger.info("Get our own register packet");
			return;
		}
		logger.info("Get register packet " + data.toString());

		this.connect_sub_socket(data, hostname);

		if (reply) {
			this.send_register_to(data, hostname);
		}

	}

	private void connect_sub_socket(Map<String, Object> data, String hostname) {
		String pub_address = String.format("tcp://%s:%s", hostname,
				(Integer) data.get("pub_port"));
		logger.info("Connect sub socket to " + pub_address);
		this.sub_socket.connect(pub_address);
	}

	private void send_register_to(Map<String, Object> data, String hostname) {
		String server_address = String.format("tcp://%s:%s", hostname,
				(Integer) data.get("server_port"));
		Socket client = this.context.socket(ZMQ.DEALER);
		logger.info("Send register info to " + server_address);
		client.connect(server_address);

		// Register info
		Map<String, Object> infos = this.register_infos();
		// TODO: Find correct local address
		infos.put("address", "127.0.0.1");

		// Send packet
		client.send("".getBytes(), ZMQ.SNDMORE);
		client.send("register".getBytes(), ZMQ.SNDMORE);
		client.send(fromMap(infos).getBytes(), 0);
	}

	public void run() {

		if (!ready) {
			return;
		}

		receive_udp();

		byte[] message;
		// Poll ZMQ sockets
		// TODO: Change unit depending on ZEROMQ version
		this.poller.poll(2000);

		// Sub socket
		if (this.poller.pollin(this.sub_pollin_id)) {
			String dest = new String(this.sub_socket.recv(0));
			String message_type = new String(this.sub_socket.recv(0));
			message = this.sub_socket.recv(0);
			this.process_sub_message(dest, message_type, message);
		}
		// Server socket
		if (this.poller.pollin(this.server_pollin_id)) {
			message = this.server_socket.recv(0);
			this.logger.info("Get message from server " + message);
			if (new String(message).equals("")) {
				this.logger.info("Null message");
				String message_type = new String(this.server_socket.recv(0));

				this.logger.info("Message type " + message_type);

				if (message_type.equals("register")) {
					Map<String, Object> payload = fromBytes(this.server_socket
							.recv(0));
					this.process_register(payload,
							(String) payload.get("address"), false);
				}
			}
		}

	}

	private void receive_udp() {
		boolean interupted = false;

		// Receive udp
		byte[] buf = new byte[1000];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		try {
			this.udp_listener.receive(packet);
		} catch (InterruptedIOException e) {
			interupted = true;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		if (!interupted) {
			// Process udp packet
			this.process_register(fromBytes(buf), packet.getAddress()
					.getCanonicalHostName(), true);
		}
	}

	@SuppressWarnings("unchecked")
	private void process_sub_message(String dest, String message_type,
			byte[] data) {
		this.logger.info("Process sub message to  " + dest + " " + message_type
				+ "/" + data);
		if (message_type.equals("localGroupCreated")) {
			Map<String, Object> message = fromBytes(data);
			String organizationClass = (String) message.get("organization");
			UUID uuid = UUID.fromString((String) message.get("groupId"));
			String groupName = (String) message.get("groupName");
			Collection<? extends GroupCondition> obtainConditions = (Collection<? extends GroupCondition>) message
					.get("obtainConditions");
			Collection<? extends GroupCondition> leaveConditions = (Collection<? extends GroupCondition>) message
					.get("leaveConditions");
			Boolean persistence = (Boolean) message.get("persistent");
			MembershipService membership = (MembershipService) SerializationUtil
					.decode((String) message.get("membership"));
			try {
				this.informDistantGroupDiscovered(organizationClass, uuid,
						groupName, obtainConditions, leaveConditions,
						persistence, membership);
				this.logger.info("Inform distant group discovered ok");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (message_type.equals("broadcast")) {
			Message m = (Message) SerializationUtil.decode(new String(data));
			this.logger.info("Get broadcast message : " + m);
			if (m.getReceiver() instanceof RoleAddress) {
				RoleAddress address = m.getReceiver();
				Class<? extends Role> receiverRole = address.getRole();
				this.listener.receiveOrganizationalDistantMessage(
						address.getGroup(), receiverRole, m, true);
			}
		}
	}

	public void publish(String dest, String message_type, byte[] data) {
		this.logger.info(String.format("Publish %s with data %s to %s",
				message_type, new String(data), dest));
		this.pub_socket.send(dest.getBytes(), ZMQ.SNDMORE);
		this.pub_socket.send(message_type.getBytes(), ZMQ.SNDMORE);
		this.pub_socket.send(data, 0);
	}
	
	public void applicationPublish(String message_type, Map<String, Object> data) {
		this.publish(this.appName, message_type, data);
	}

	public void publish(String dest, String message_type,
			Map<String, Object> data) {
		this.publish(dest, message_type, fromMap(data).getBytes());
	}

	// Encoding/Decoding utils
	private String fromMap(Map<String, Object> m) {
		ObjectMapper mapper = new ObjectMapper();
		Writer strWriter = new StringWriter();
		try {
			mapper.writeValue(strWriter, m);
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
		return strWriter.toString();
	}

	private Map<String, Object> fromBytes(byte[] byteMsg) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(byteMsg,
					new TypeReference<Map<String, Object>>() {
					});
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// Connection with listener

	public void setNetworkAdapterListener(NetworkListener listener) {
		System.out.println("Set listener " + listener);
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
	 * @param membership
	 *            is the membership checker of the distant group.
	 * @param advertisement
	 *            is the JXTA advertisement.
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

	public void setAppName(String appName) {
		this.appName = appName;
	}
}
