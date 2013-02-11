package org.janusproject.demos.meetingscheduler.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.janusproject.acl.ACLAgent;
import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.Performative;
import org.janusproject.demos.meetingscheduler.ontology.Meeting;
import org.janusproject.demos.meetingscheduler.ontology.MeetingConfirmation;
import org.janusproject.demos.meetingscheduler.ontology.MeetingManager;
import org.janusproject.demos.meetingscheduler.ontology.MeetingResponse;
import org.janusproject.demos.meetingscheduler.role.MeetingChannel;
import org.janusproject.demos.meetingscheduler.role.MeetingListener;
import org.janusproject.demos.meetingscheduler.util.SerializationUtil;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.channels.Channel;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.status.Status;

import com.miginfocom.util.dates.ImmutableDateRange;

/**
 * The main agent class.
 * 
 * @author bfeld
 * @author ngrenie
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 *
 */
public class MeetingAgent extends ACLAgent implements ChannelInteractable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3106607589577058099L;
	private List<MeetingListener> listeners = new ArrayList<MeetingListener>();
	private MeetingManager meetingManager;

	public MeetingAgent() {
		super();
		this.meetingManager = new MeetingManager();
	}

	public Status live() {
		Status s = super.live();

		ACLMessage aMsg = getACLMessage();
		if (aMsg != null) {
			Performative performative = aMsg.getPerformative();
			if (performative == Performative.PROPOSE) {
				Meeting meeting = (Meeting) SerializationUtil.decode(aMsg
						.getContent().getContent().toString());
				meetingManager.saveInitiatorAddress(meeting.getId(),
						aMsg.getSender());
				for (MeetingListener listener : listeners) {
					listener.incomingMeetingProposal(meeting);
				}
			} else if (performative == Performative.ACCEPT_PROPOSAL) {
				MeetingResponse meetingResponse = (MeetingResponse) SerializationUtil
						.decode(aMsg.getContent().getContent().toString());
				meetingManager.process_response(meetingResponse,
						aMsg.getSender());

				if (meetingManager.hasAllResponses(meetingResponse.getId())) {
					for (MeetingListener listener : listeners) {
						listener.chooseMeetingTimeSlot(
								meetingResponse.getId(),
								meetingManager.getSlots(meetingResponse.getId()));
					}
				}
			} else if (performative == Performative.CONFIRM) {
				MeetingConfirmation confirmation = (MeetingConfirmation) SerializationUtil
						.decode(aMsg.getContent().getContent().toString());
				for (MeetingListener listener : listeners) {
					listener.createActivity(confirmation.getDateRange(),
							confirmation.getDescription(), confirmation.getId());
				}
			}
		}
		return s;
	}

	private class MeetingChannelImplementation implements MeetingChannel {

		@Override
		public Address getChannelOwner() {
			// TODO Auto-generated method stub
			return null;
		}

		public synchronized void addMeetingListener(MeetingListener listener) {
			MeetingAgent.this.listeners.add(listener);
		}

		public synchronized void removeMeetingListener(MeetingListener listener) {
			MeetingAgent.this.listeners.remove(listener);
		}

		public synchronized void release() {
			MeetingAgent.this.listeners.clear();
		}

		@Override
		public void createMeeting(Meeting meeting,
				List<AgentAddress> participants) {
			String encoded_meeting = SerializationUtil.encode(meeting);
			ACLMessage message = new ACLMessage(encoded_meeting,
					Performative.PROPOSE);
			MeetingAgent.this.meetingManager.processNewMeeting(meeting,
					participants);
			// Map<AgentAddress, MeetingResponse> meet = new
			// HashMap<AgentAddress, MeetingResponse>();
			// MeetingAgent.this.meetings.put(meeting.getId(), meet);
			for (AgentAddress agentAddress : participants) {
				MeetingAgent.this.sendACLMessage(message, agentAddress);
			}
		}

		@Override
		public void responseMeeting(MeetingResponse meetingResponse) {
			Address address = MeetingAgent.this.meetingManager
					.getInitiatorAddress(meetingResponse.getId());
			MeetingAgent.this.sendACLMessage(
					new ACLMessage(SerializationUtil.encode(meetingResponse),
							Performative.ACCEPT_PROPOSAL),
					(AgentAddress) address);
		}

		@Override
		public void confirmMeeting(UUID id,
				ImmutableDateRange immutableDateRange) {
			String description = MeetingAgent.this.meetingManager
					.getDescription(id);
			for (MeetingListener listener : MeetingAgent.this.listeners) {
				listener.createActivity(immutableDateRange, description, id);
			}
			for (AgentAddress address : MeetingAgent.this.meetingManager
					.getSlots(id).get(immutableDateRange).getParticipants()) {
				MeetingAgent.this.sendACLMessage(
						new ACLMessage(SerializationUtil
								.encode(new MeetingConfirmation(
										immutableDateRange, description, id)),
								Performative.CONFIRM), address);
			}

		}
	}

	public <C extends Channel> C getChannel(Class<C> channelClass,
			Object... params) {
		// Check if the given channel type is supported by the role.
		if (MeetingChannel.class.isAssignableFrom(channelClass)) {

			// Create the instance of the channel.
			MeetingChannel channelInstance = new MeetingChannelImplementation();

			// Reply the channel instance.
			return channelClass.cast(channelInstance);

		}

		// The given channel type is not supported
		throw new IllegalArgumentException("channelClass");
	}

	/**
	 * This function replies the types of the channels that are supported by
	 * this role.
	 */
	public Set<? extends Class<? extends Channel>> getSupportedChannels() {
		return Collections.singleton(MeetingChannel.class);
	}
}
