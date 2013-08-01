package org.janusproject.demos.meetingscheduler.ontology;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.miginfocom.util.dates.ImmutableDateRange;

/**
 * The message sent as the response with chosen time slots.
 * Contains meeting id and time slots with preference.
 * 
 * @author bfeld
 * @author ngrenie
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 *
 */
public class MeetingResponse implements Serializable {

	private static final long serialVersionUID = 2190746471098238663L;
	private UUID id;
	private Map<ImmutableDateRange, Integer> slots = new HashMap<ImmutableDateRange, Integer>();

	public MeetingResponse(Meeting meeting) {
		super();
		this.id = meeting.getId();
	}

	public void addResponseDate(ImmutableDateRange date, Integer value) {
		if (value != 0) {
			slots.put(date, value);
		}
	}

	public UUID getId() {
		return this.id;
	}

	public Map<ImmutableDateRange, Integer> getSlots() {
		return slots;
	}

}
