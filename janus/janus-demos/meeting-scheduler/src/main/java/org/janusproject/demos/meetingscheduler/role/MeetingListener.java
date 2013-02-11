package org.janusproject.demos.meetingscheduler.role;

import java.util.Map;
import java.util.UUID;

import org.janusproject.demos.meetingscheduler.ontology.Meeting;
import org.janusproject.demos.meetingscheduler.ontology.MeetingTimeSlot;

import com.miginfocom.util.dates.ImmutableDateRange;

/**
 * Meeting Listener.
 * 
 * @author bfeld
 * @author ngrenie
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 *
 */
public interface MeetingListener {
	public void incomingMeetingProposal(Meeting meeting);

	public void chooseMeetingTimeSlot(UUID id, Map<ImmutableDateRange, MeetingTimeSlot> slots);

	void createActivity(ImmutableDateRange immutableDateRange,
			String description, UUID id);
}
