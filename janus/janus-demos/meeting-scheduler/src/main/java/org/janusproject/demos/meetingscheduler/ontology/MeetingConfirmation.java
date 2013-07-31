package org.janusproject.demos.meetingscheduler.ontology;

import java.io.Serializable;
import java.util.UUID;

import com.miginfocom.util.dates.ImmutableDateRange;

public class MeetingConfirmation implements Serializable {

	private static final long serialVersionUID = -5296586500282612217L;
	private String description;
	private ImmutableDateRange dateRange;
	private UUID id;

	public MeetingConfirmation(ImmutableDateRange immutableDateRange,
			String description, UUID id) {
		super();
		this.dateRange = immutableDateRange;
		this.description = description;
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public ImmutableDateRange getDateRange() {
		return dateRange;
	}

	public UUID getId() {
		return this.id;
	}

}
