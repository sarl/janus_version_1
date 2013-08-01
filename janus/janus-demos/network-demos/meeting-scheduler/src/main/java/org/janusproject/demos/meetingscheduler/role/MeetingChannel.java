package org.janusproject.demos.meetingscheduler.role;

import java.util.List;
import java.util.UUID;

import org.janusproject.demos.meetingscheduler.ontology.Meeting;
import org.janusproject.demos.meetingscheduler.ontology.MeetingResponse;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.channels.Channel;

import com.miginfocom.util.dates.ImmutableDateRange;

/**
 * Used to communicate between agent and UI.
 * 
 * @author bfeld
 * @author ngrenie
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 *
 */
public interface MeetingChannel extends Channel {

	public void addMeetingListener(MeetingListener listener);

	public void removeMeetingListener(MeetingListener listener);

	public void release();

	public void createMeeting(Meeting meeting, List<AgentAddress> participants);

	void responseMeeting(MeetingResponse meetingResponse);

	public void confirmMeeting(UUID id, ImmutableDateRange immutableDateRange);
}
