package org.janusproject.demos.meetingscheduler.ontology;

import java.util.ArrayList;
import java.util.List;

import org.janusproject.kernel.address.AgentAddress;

/**
 * An utility object stored in MeetingManager
 * 
 * @author bfeld
 * @author ngrenie
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 *
 */
public class MeetingTimeSlot {
	private Integer value;
	private List<AgentAddress> participants;
	private Integer participantsSize;

	public MeetingTimeSlot(int size) {
		super();
		this.participantsSize = size;
		this.value = 0;
		this.participants = new ArrayList<AgentAddress>();
	}

	public void processResponse(AgentAddress participant, int value) {
		this.participants.add(participant);
		this.value += value;
	}

	public Integer getValue() {
		return value;
	}

	public Boolean hasAllParticipants() {
		return this.participants.size() == this.participantsSize;
	}

	public List<AgentAddress> getParticipants() {
		return participants;
	}

}
