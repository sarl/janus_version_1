/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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
package org.janusproject.demos.ecoresolution.ecocube.relation;

import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.EcoRelation;
import org.janusproject.ecoresolution.relation.EcoRelationUtil;

/** Utilities for relations.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
class CubeRelationUtil {

	/** Replies if the given relations are under conflict.
	 * 
	 * @param table
	 * @param r1
	 * @param r2
	 * @return <code>true</code> if conflicting, otherwise <code>false</code>.
	 */
	public static boolean isConflict(EcoIdentity table, DownwardRelation r1, EcoRelation r2) {
		assert(r1!=null && r2!=null);
		EcoIdentity m1 = r1.getMaster();
		EcoIdentity s1 = r1.getSlave();
		EcoIdentity m2, s2;
		m2 = s2 = null;
		if (r2 instanceof UpwardRelation) {
			m2 = r2.getSlave();
			s2 = r2.getMaster();
		}
		else if (r2 instanceof DownwardRelation) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
		}
		if (m2!=null && s2!=null)
			return isDownwardConflict(table, m1,s1,m2,s2);
		return false;
	}

	/** Replies if the given relations are under conflict.
	 * 
	 * @param table
	 * @param r1
	 * @param r2
	 * @return <code>true</code> if conflicting, otherwise <code>false</code>.
	 */
	public static boolean isConflict(EcoIdentity table, UpwardRelation r1, EcoRelation r2) {
		assert(r1!=null && r2!=null);
		EcoIdentity m1 = r1.getSlave();
		EcoIdentity s1 = r1.getMaster();
		EcoIdentity m2, s2;
		m2 = s2 = null;
		if (r2 instanceof UpwardRelation) {
			m2 = r2.getSlave();
			s2 = r2.getMaster();
		}
		else if (r2 instanceof DownwardRelation) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
		}
		return isDownwardConflict(table, m1,s1,m2,s2);
	}

	/** Replies if the given downward relations are under conflict.
	 * 
	 * @param table
	 * @param master1
	 * @param slave1
	 * @param master2
	 * @param slave2
	 * @return <code>true</code> if conflicting, otherwise <code>false</code>.
	 */
	public static boolean isDownwardConflict(EcoIdentity table, EcoIdentity master1, EcoIdentity slave1, EcoIdentity master2, EcoIdentity slave2) {
		assert(table!=null);
		if (slave1==null || slave2==null || master1==null || master2==null) return true;
		boolean sameSlave = slave1.equals(slave2);
		if (sameSlave && slave1.equals(table)) return false;
		boolean sameMaster = master1.equals(master2);
		return sameMaster ^ sameSlave;
	}

	/**
	 * Replies the hashcode for the given relation.
	 * 
	 * @param relation
	 * @return the hashcode for the given relation.
	 */
	public static int hashCode(EcoRelation relation) {
		EcoIdentity m, s;
		if (relation instanceof DownwardRelation) {
			m = relation.getMaster();
			s = relation.getSlave();
		}
		else {
			m = relation.getSlave();
			s = relation.getMaster();
		}
		return EcoRelationUtil.hashCode(DownwardRelation.class, m, s);
	}

	/** Replies if this eco-relation is equals to the given eco-relation.
	 * 
	 * @param m1 is the master of a downward relation.
	 * @param s1 is the slave of a downward relation.
	 * @param r2
	 * @return <code>true</code> if master, slave, and relation type are
	 * the same for both this eco-relation and the given eco-relation.
	 * Otherwise replies <code>false</code>.
	 */
	public static boolean isDownwardEqual(EcoIdentity m1, EcoIdentity s1, EcoRelation r2) {
		EcoIdentity m2, s2;
		if (r2 instanceof DownwardRelation) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
		}
		else {
			assert(r2 instanceof UpwardRelation);
			m2 = r2.getSlave();
			s2 = r2.getMaster();
		}
		return isDownwardEqual(m1, s1, m2, s2);
	}

	/** Replies if this eco-relation is equals to the given eco-relation.
	 * 
	 * @param m1
	 * @param s1
	 * @param m2
	 * @param s2
	 * @return <code>true</code> if master, slave, and relation type are
	 * the same for both this eco-relation and the given eco-relation.
	 * Otherwise replies <code>false</code>.
	 */
	public static boolean isDownwardEqual(EcoIdentity m1, EcoIdentity s1, EcoIdentity m2, EcoIdentity s2) {
		return m1!=null && s1!=null && m2!=null && s2!=null &&
			m1.equals(m2) && s1.equals(s2);
	}

}