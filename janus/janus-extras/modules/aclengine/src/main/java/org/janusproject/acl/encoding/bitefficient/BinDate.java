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
 * @author $Author: sroth-01$
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
