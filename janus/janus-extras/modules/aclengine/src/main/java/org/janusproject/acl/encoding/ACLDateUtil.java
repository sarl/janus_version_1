package org.janusproject.acl.encoding;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ACLDateUtil {
	private final static long year = 365*24*60*60*1000L;
    private final static long month = 30*24*60*60*1000L;
    private final static long day = 24*60*60*1000;
    private final static long hour = 60*60*1000;
    private final static long minute = 60*1000;
    private final static long sec = 1000;
    
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

	public static boolean containsTypeDesignator(String s) {
        char a = s.charAt(s.length()-1);
        return ((a >= 'a' && a <= 'z') || (a >= 'A' && a <= 'Z'));
    }
}
