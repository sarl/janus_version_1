package org.janusproject.demos.meetingscheduler.ontology;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.miginfocom.util.dates.ImmutableDateRange;

/**
 * The meeting object, sent as the proposal.
 * Contains meeting id, meeting description and available time slots.
 * 
 * @author bfeld
 * @author ngrenie
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 *
 */
public class Meeting implements Serializable {
	
	private String initiator;
	private static final long serialVersionUID = 3733265177969466470L;
	private List<ImmutableDateRange> dates;
	private String description;
	private UUID id;
	
	public Meeting(String initiator, List<ImmutableDateRange> dates, String description) {
		super();
		this.initiator = initiator;
		this.id = UUID.randomUUID();
		this.dates = dates;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public List<ImmutableDateRange> getDates() {
		return dates;
	}

	public String getInitiator() {
		return initiator;
	}

	public UUID getId() {
		return id;
	}

}
