package org.janusproject.kernel.network.zeromq.zeromq;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.configuration.JanusProperties;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.network.jxta.NetworkAdapter;
import org.janusproject.kernel.network.jxta.NetworkListener;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

public class ZeroMQNetworkAdapter implements NetworkAdapter {

	ZeroMQNode node;
	private Logger logger;
	private JanusProperties janusProperties = null;

	public ZeroMQNetworkAdapter(ZeroMQNode node) {
		super();
		this.node = node;
		this.logger = Logger.getLogger("ZeroMQAdapter");
	}

	@Override
	public SizedIterator<AgentAddress> getRemoteKernels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void informLocalRoleTaken(GroupAddress groupAddress,
			Class<? extends Role> role, AgentAddress agentAddress) {
		this.node.logger.info("Agent " + agentAddress + " has take "
				+ role.getName());
	}

	@Override
	public void informLocalRoleReleased(GroupAddress groupAddress,
			Class<? extends Role> role, AgentAddress agentAddress) {
		this.node.sub_socket.unsubscribe(agentAddress.getUUID().toString()
				.getBytes());

	}

	@Override
	public Address sendMessage(Message message) {
		this.logger.info("Send message: " + message);
		this.node.publish(message.getReceiver().getUUID().toString(),
				"message", SerializationUtil.encode(message).getBytes());
		return null;
	}

	@Override
	public void broadcastMessage(Message message) {
		Address adr = message.getSender();
		this.logger.info("Message :" + message.getClass());
		if (adr instanceof RoleAddress) {
			GroupAddress group = ((RoleAddress) adr).getGroup();
			this.logger.info("Broadcast message : " + message + " to "
					+ group.getUUID().toString());
			this.node.publish(group.getUUID().toString(), "broadcast",
					SerializationUtil.encode(message).getBytes());
		}
	}

	@Override
	public void initializeNetwork(AgentAddress kernelAddress,
			JanusProperties properties) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Initialize network");
		this.node.init(kernelAddress);
		this.node.register();
	}

	@Override
	public void shutdownNetwork() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Status informLocalGroupCreated(GroupAddress ga,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			MembershipService membership) {
		this.logger.info("new group " + ga.getUUID() + " : " + ga.getName());
		// Register to group address
		this.node.logger.info("Subscribe to: " + ga.getUUID().toString());
		this.node.sub_socket.subscribe(ga.getUUID().toString().getBytes());

		// Send created group on network
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("organization", ga.getOrganization());
		data.put("groupId", ga.getUUID());
		data.put("groupName", ga.getName());
		data.put("obtainConditions", obtainConditions);
		data.put("leaveConditions", leaveConditions);
		data.put("membership", SerializationUtil.encode(membership));
		data.put("persistent", Boolean.valueOf(this.janusProperties
				.getProperty(JanusProperty.GROUP_PERSISTENCE)));
		this.node.applicationPublish("localGroupCreated", data);
		this.node.sub_socket.subscribe(ga.getUUID().toString().getBytes());
		return StatusFactory.ok("ok");
	}

	@Override
	public Status informLocalGroupRemoved(GroupAddress ga) {
		this.node.sub_socket.unsubscribe(ga.getUUID().toString().getBytes());
		return StatusFactory.ok("ok");
	}

	@Override
	public void setNetworkAdapterListener(NetworkListener listener) {
		System.out.println(listener);
		this.node.setNetworkAdapterListener(listener);
	}

	@Override
	public void setJanusProperties(JanusProperties properties) {
		this.janusProperties = properties;
		this.node.setAppName(properties.getProperty(JanusProperty.JANUS_APPLICATION_NAME));
	}

	// Deprecated functions

	@Override
	public boolean isRemoteAddress(GroupAddress groupAddress,
			AgentAddress address) {
		return false;
	}

	@Override
	public RoleAddress getRemoteAddress(GroupAddress groupAddress) {
		return null;
	}

	@Override
	public void informLocalAgent(AgentAddress agentAdress) {
		// TODO Auto-generated method stub
	}

	@Override
	public void informLocalAgentRemoved(AgentAddress agentAddress) {
		// TODO Auto-generated method stub

	}

}
