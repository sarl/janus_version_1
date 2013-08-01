package org.janusproject.demos.meetingscheduler.util;

import com.miginfocom.util.dates.ImmutableDateRange;

/**
 * 
 * 
 * @author bfeld
 * @author ngrenie
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 *
 */
public class DateRangeUtil {
	public static String dateToHumanFriendly(ImmutableDateRange date){
		return "From "+date.getStart().getTime()+" to "+date.getEnd().getTime();
	}
}
