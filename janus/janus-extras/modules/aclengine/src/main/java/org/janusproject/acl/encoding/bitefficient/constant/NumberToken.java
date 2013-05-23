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

package org.janusproject.acl.encoding.bitefficient.constant;


/** Defines the token from the numbers according to the FIPA specification.
 * 
 * @see <a href="http://www.fipa.org/specs/fipa00069/SC00069G.html">FIPA ACL Message Representation in Bit-Efficient Specification</a> 
 * 
 * @author $Author: flacreus$
 * @author $Author: sroth$
 * @author $Author: cstentz$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */

public enum NumberToken {

	/** Padding.
	 * <p>
	 * Code {@code 0x00}.
	 */
	PADDING("", (byte) 0x00), //$NON-NLS-1$
	/** {@code 0}.
	 * <p>
	 * Code {@code 0x01}.
	 */
	ZERO("0", (byte) 0x01), //$NON-NLS-1$
	/** {@code 1}.
	 * <p>
	 * Code {@code 0x02}.
	 */
	ONE("1", (byte) 0x02), //$NON-NLS-1$
	/** {@code 2}.
	 * <p>
	 * Code {@code 0x03}.
	 */
	TWO("2", (byte) 0x03), //$NON-NLS-1$
	/** {@code 3}.
	 * <p>
	 * Code {@code 0x04}.
	 */
	THREE("3", (byte) 0x04), //$NON-NLS-1$
	/** {@code 4}.
	 * <p>
	 * Code {@code 0x05}.
	 */
	FOUR("4", (byte) 0x05), //$NON-NLS-1$
	/** {@code 5}.
	 * <p>
	 * Code {@code 0x06}.
	 */
	FIVE("5", (byte) 0x06), //$NON-NLS-1$
	/** {@code 6}.
	 * <p>
	 * Code {@code 0x07}.
	 */
	SIX("6", (byte) 0x07), //$NON-NLS-1$
	/** {@code 7}.
	 * <p>
	 * Code {@code 0x08}.
	 */
	SEVEN("7", (byte) 0x08), //$NON-NLS-1$
	/** {@code 8}.
	 * <p>
	 * Code {@code 0x09}.
	 */
	EIGHT("8", (byte) 0x09), //$NON-NLS-1$
	/** {@code 9}.
	 * <p>
	 * Code {@code 0x0a}.
	 */
	NINE("9", (byte) 0x0a), //$NON-NLS-1$
	/** {@code +}.
	 * <p>
	 * Code {@code 0x0b}.
	 */
	PLUS("+", (byte) 0x0b), //$NON-NLS-1$
	/** {@code E}.
	 * <p>
	 * Code {@code 0x0c}.
	 */
	EXPONENT("E", (byte) 0x0c), //$NON-NLS-1$
	/** {@code -}.
	 * <p>
	 * Code {@code 0x0d}.
	 */
	MINUS("-", (byte) 0x0d), //$NON-NLS-1$
	/** {@code .}.
	 * <p>
	 * Code {@code 0x0e}.
	 */
	DOT(".", (byte) 0x0e); //$NON-NLS-1$
	
	private final String token;
	private final byte code;
	
	private NumberToken(String token, byte code){
		this.token = token;
		this.code = code;
	}
	
	/** Replies the code from the FIPA specification.
	 * 
	 * @return the code.
	 */
	public byte getCode() {
		return this.code;
	}
	
	/** Replies the string-representation of the token from the FIPA specification.
	 * 
	 * @return the string-representation of the token.
	 */
	public String getToken() {
		return this.token;
	}
	
	/** Replies the code of the token that is corresponding to the given character.
	 * 
	 * @param token
	 * @return the code, or the code {@link #PADDING} if the character is not
	 * recognized.
	 */
	public static byte getCode(char token) {
		for (NumberToken value : values()) { 
			if (value.getToken().equalsIgnoreCase(Character.toString(token))) { 
				return value.getCode();
			} 
		}
		return NumberToken.PADDING.getCode(); 
	}
	
	/** Replies the string-representation of the given code.
	 * 
	 * @param code
	 * @return the string representation of the given code,
	 * or the string-representation of {@link #PADDING} if the
	 * code is not recognized.
	 */
	public static String getToken(byte code) {
		for (NumberToken value : values()) { 
			if (value.getCode() == code) { 
				return value.getToken();
			} 
		}
		return NumberToken.PADDING.getToken();
	}
}
