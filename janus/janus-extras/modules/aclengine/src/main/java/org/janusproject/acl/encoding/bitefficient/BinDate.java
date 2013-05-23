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
package org.janusproject.acl.encoding.bitefficient;

import java.util.ArrayList;
import java.util.List;

import org.janusproject.acl.encoding.ACLDateUtil;
import org.janusproject.acl.encoding.bitefficient.constant.BinDateTimeToken;
import org.janusproject.acl.encoding.bitefficient.constant.NumberToken;

/**
 * This class encodes provides methods to transform an String ISO8601 date to bytes and decode it.
 * 
 * @author $Author: flacreus$
 * @author $Author: sroth$
 * @author $Author: cstentz$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class BinDate {
	/**
	 * Encode an ISO8601 date
	 * 
	 * @param s String representing an ISO8601 date
	 * @return the encoded date for bit efficient
	 */
	public static List<Byte> toBin(String s) {
        List<Byte> encodedDate = new ArrayList<Byte>();
        byte b;
        int startPos = 0;
        
        // should never happen for now because dates stored in a ACLMessage are absolute date
        if (s.charAt(0) == '+' || s.charAt(0) == '-') {
        	startPos = 1;
        }
        
        // YY YY MM DD
        for (int i = startPos ; i < 8+startPos ; i+=2) {
        	b = (byte)(NumberToken.getCode(s.charAt(i)) << 4);
        	b |= (NumberToken.getCode(s.charAt(i+1)) & 0x0f);
        	encodedDate.add(b);
        }

        // HH MM SS SS
        for (int i = 9+startPos ; i < 17+startPos ; i+=2) {
            b = (byte)(NumberToken.getCode(s.charAt(i)) << 4);
            b |= (NumberToken.getCode(s.charAt(i+1)) & 0x0f);
            encodedDate.add(b);
        }
        
        // S0
        b = (byte)(NumberToken.getCode(s.charAt(17+startPos)) << 4);
        encodedDate.add(b);
        
        return encodedDate;
    }
	
	/**
	 * decode an encoded ISO8601 date
	 * 
	 * @param bytesRead an ISO8601 encoded date
	 * @return the corresponding String
	 */
	public static String toString(List<Byte> bytesRead) {
		String s = ""; //$NON-NLS-1$
		
		for (int i = 0 ; i < 9 ; ++i) {
			if (s.length() == 8) {	
				s += 'T';
			}
			s += NumberToken.getToken((byte) ((bytesRead.get(i)>>4) & 0x0f));
			s += NumberToken.getToken((byte) (bytesRead.get(i) & 0x0f));
        }
		
		return s;
	}
	
	/**
	 * Determine the date time type (asbolute or relative time, with or without type designator, ..)
	 * 
	 * @param dateTimeToken String representing an ISO8601 date
	 * @return the date time token (byte)
	 */
	public static byte getDateTimeType(String dateTimeToken) {
		int tmp = 0;
        
        if (dateTimeToken.charAt(0) == '+') {
        	tmp = 1;
        } else if (dateTimeToken.charAt(0) == '-') {
        	tmp = 2;
        }
        
        if (ACLDateUtil.containsTypeDesignator(dateTimeToken)) {
        	tmp += 4;
        }

        return (byte) (BinDateTimeToken.ABS_TIME.getCode() + tmp);
	}
}
