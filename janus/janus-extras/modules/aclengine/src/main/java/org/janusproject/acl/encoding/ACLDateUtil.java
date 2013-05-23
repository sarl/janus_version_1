/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.janusproject.acl.encoding;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/** Provides utilities to convert dates for ACL.
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ACLDateUtil {
	private final static long year = 365*24*60*60*1000L;
    private final static long month = 30*24*60*60*1000L;
    private final static long day = 24*60*60*1000;
    private final static long hour = 60*60*1000;
    private final static long minute = 60*1000;
    private final static long sec = 1000;
    
    /**
     * Replies the token that is representing a time in ACL format of
     * the specified date.
     * 
     * @param date
     * @return the ACL time token from <var>date</var>.
     */
	public static String toDateTimeToken(Date date) {
		if (date == null) {
			return null;
		}
		
		Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
		utcCal.setTime(date);
        StringBuffer formatedDate = new StringBuffer();

        formatedDate.append(zeroPaddingNumber(utcCal.get(Calendar.YEAR), 4));
        formatedDate.append(zeroPaddingNumber(utcCal.get(Calendar.MONTH) + 1, 2));
        formatedDate.append(zeroPaddingNumber(utcCal.get(Calendar.DATE), 2));
        formatedDate.append("T");  //$NON-NLS-1$
        formatedDate.append(zeroPaddingNumber(utcCal.get(Calendar.HOUR_OF_DAY), 2));
        formatedDate.append(zeroPaddingNumber(utcCal.get(Calendar.MINUTE), 2));
        formatedDate.append(zeroPaddingNumber(utcCal.get(Calendar.SECOND), 2));
        formatedDate.append(zeroPaddingNumber(utcCal.get(Calendar.MILLISECOND), 3));
        formatedDate.append("Z"); //$NON-NLS-1$
        
        return formatedDate.toString();
	}
	
	/**
	 * Parse an ACL time token to produce a Java date.
	 * 
	 * @param dateTimeToken is the ACL time to parse.
	 * @return the Java representation of <var>dataTimeToken</var>
	 */
	public static Date toDate(String dateTimeToken) {
		if ("".equals(dateTimeToken)) { //$NON-NLS-1$
			return null;
		}
		
		char sign = dateTimeToken.charAt(0);
		
		if ((sign == '+') || (sign == '-')) {
			// convert a relative time into an absolute time
			long millisec = Long.parseLong(dateTimeToken.substring(1, 5)) * year +
				Long.parseLong(dateTimeToken.substring(5, 7)) * month +
				Long.parseLong(dateTimeToken.substring(7, 9)) * day +
				Long.parseLong(dateTimeToken.substring(10, 12)) * hour +
				Long.parseLong(dateTimeToken.substring(12, 14)) * minute +
				Long.parseLong(dateTimeToken.substring(14, 16)) * sec;
		
			millisec = System.currentTimeMillis() + (sign == '+' ? millisec : (-millisec));
			return(new Date(millisec));
		} else if( dateTimeToken.endsWith("Z")) { //$NON-NLS-1$
		    // Preferred format is to pass UTC times, indicated by trailing 'Z'
		    return computeCalendar(dateTimeToken, true).getTime();
		} else {
		    // Alternate format is to use local times - no trailing 'Z'
		    return computeCalendar(dateTimeToken, false).getTime();
		}
	}
	
	
	private static Calendar computeCalendar(String dateTimeToken, boolean utc) {
		Calendar cal = null;
		
		if (utc) {
			cal = Calendar.getInstance(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
		} else {
			cal = Calendar.getInstance();
		}
		
		cal.set(Calendar.YEAR, Integer.parseInt(dateTimeToken.substring(0, 4)));
		cal.set(Calendar.MONTH, Integer.parseInt(dateTimeToken.substring(4, 6)) - 1);
		cal.set(Calendar.DATE, Integer.parseInt(dateTimeToken.substring(6, 8)));
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateTimeToken.substring(9, 11)));
		cal.set(Calendar.MINUTE, Integer.parseInt(dateTimeToken.substring(11, 13))); 
		cal.set(Calendar.SECOND, Integer.parseInt(dateTimeToken.substring(13, 15)));
		cal.set(Calendar.MILLISECOND, Integer.parseInt(dateTimeToken.substring(15, 18)));
		
		return cal;
	}
	
	private static String zeroPaddingNumber(long value, int digits) {
		String s = Long.toString(value);
		int n = digits-s.length();
		
		for (int i=0; i<n; i++) {
			s = "0" + s; //$NON-NLS-1$
		}
		
		return s;
	}

	/** Replies if the given string-representation of a date
	 * contains the designator of a type.
	 * <p>
	 * The type designator is an alphabetic character (ie. a
	 * letter) at the end of the string.
	 * 
	 * @param s is the string-representation to parse
	 * @return <code>true</code> if a type designator is inside;
	 * <code>false</code> otherwise.
	 */
	public static boolean containsTypeDesignator(String s) {
        char a = s.charAt(s.length()-1);
        return ((a >= 'a' && a <= 'z') || (a >= 'A' && a <= 'Z'));
    }
}
