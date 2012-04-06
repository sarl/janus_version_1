/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011 Janus Core Developers
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
package org.janusproject.demos.ecoresolution.econpuzzle.relation;

import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.EcoRelation;
import org.janusproject.ecoresolution.relation.EcoRelationUtil;

/**
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class NPuzzleRelationUtil {

	
	/**
	 * Replies the hashcode for the given relation.
	 * 
	 * @param relation
	 * @return the hashcode for the given relation.
	 */
	public static int hashCode(EcoRelation relation) {
		EcoIdentity m, s;
		if (relation instanceof Hosting) {
			m = relation.getMaster();
			s = relation.getSlave();
			return EcoRelationUtil.hashCode(Hosting.class, m, s);
		} else if (relation instanceof Hosted) {
			m = relation.getSlave();
			s = relation.getMaster();
			return EcoRelationUtil.hashCode(Hosting.class, m, s);
		} else if (relation instanceof Up) {
			m = relation.getMaster();
			s = relation.getSlave();
			return EcoRelationUtil.hashCode(Up.class, m, s);
		} else if (relation instanceof Down) {
			m = relation.getSlave();
			s = relation.getMaster();
			return EcoRelationUtil.hashCode(Up.class, m, s);
		} else if (relation instanceof Left) {
			m = relation.getMaster();
			s = relation.getSlave();
			return EcoRelationUtil.hashCode(Left.class, m, s);
		} else if (relation instanceof Right) {
			m = relation.getSlave();
			s = relation.getMaster();
			return EcoRelationUtil.hashCode(Left.class, m, s);
		} else if (relation instanceof TileGoal) {
			m = relation.getMaster();
			s = relation.getSlave();
			return EcoRelationUtil.hashCode(TileGoal.class, m, s);
		} else if (relation instanceof PlaceGoal) {
			m = relation.getSlave();
			s = relation.getMaster();
			return EcoRelationUtil.hashCode(TileGoal.class, m, s);
		} else /*if (relation instanceof Blank)*/ {
			assert(relation instanceof Blank);
			m = relation.getMaster();
			s = relation.getSlave();
			return EcoRelationUtil.hashCode(Blank.class, m, s);
		}	
	}
	
	/*********************************************************************************************************/
	
	/**
	 * @param m1
	 * @param s1
	 * @param r2
	 * @return <code>true</code> if equals; <code>false</code> otherwise.
	 */
	public static boolean isHostingEqual(EcoIdentity m1, EcoIdentity s1, EcoRelation r2) {
		EcoIdentity m2, s2;
		if (r2 instanceof Hosting) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
			return isRelationEqual(m1, s1, m2, s2);
		} else if (r2 instanceof Hosted){
			m2 = r2.getSlave();
			s2 = r2.getMaster();
			return isRelationEqual(m1, s1, m2, s2);
		} else {
			return false;
		}
	}
	
	/**
	 * @param m1
	 * @param s1
	 * @param r2
	 * @return <code>true</code> if up; <code>false</code> otherwise.
	 */
	public static boolean isUpEqual(EcoIdentity m1, EcoIdentity s1, EcoRelation r2) {
		EcoIdentity m2, s2;
		if (r2 instanceof Up) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
			return isRelationEqual(m1, s1, m2, s2);
		} else if (r2 instanceof Down) {
			m2 = r2.getSlave();
			s2 = r2.getMaster();
			return isRelationEqual(m1, s1, m2, s2);
		} else {
			return false;
		}
	}
	
	/**
	 * @param m1
	 * @param s1
	 * @param r2
	 * @return <code>true</code> if left; <code>false</code> otherwise.
	 */
	public static boolean isLeftEqual(EcoIdentity m1, EcoIdentity s1, EcoRelation r2) {
		EcoIdentity m2, s2;
		if (r2 instanceof Left) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
			return isRelationEqual(m1, s1, m2, s2);
		} else if (r2 instanceof Right) {
			m2 = r2.getSlave();
			s2 = r2.getMaster();
			return isRelationEqual(m1, s1, m2, s2);
		} else {
			return false;
		}
	}
	
	/**
	 * @param m1
	 * @param s1
	 * @param r2
	 * @return <code>true</code> if equals; <code>false</code> otherwise.
	 */
	public static boolean isTileGoalEqual(EcoIdentity m1, EcoIdentity s1, EcoRelation r2) {
		EcoIdentity m2, s2;
		if (r2 instanceof TileGoal) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
			return isRelationEqual(m1, s1, m2, s2);
		} else if (r2 instanceof PlaceGoal) {
			m2 = r2.getSlave();
			s2 = r2.getMaster();
			return isRelationEqual(m1, s1, m2, s2);
		} else {
			return false;
		}
	}
	
	
	/**
	 * @param m1
	 * @param s1
	 * @param r2
	 * @return <code>true</code> if equals; <code>false</code> otherwise.
	 */
	public static boolean isBlankEqual(EcoIdentity m1, EcoIdentity s1, EcoRelation r2) {
		EcoIdentity m2, s2;
		if (r2 instanceof Blank) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
			return isRelationEqual(m1, s1, m2, s2);
		}
		return false;
	}
	
	private static boolean isRelationEqual(EcoIdentity m1, EcoIdentity s1, EcoIdentity m2, EcoIdentity s2) {
		return m1!=null && s1!=null && m2!=null && s2!=null && m1.equals(m2) && s1.equals(s2);
	}	
	
	
	/*********************************************************************************************************/
	/**
	 * @param r1
	 * @param r2
	 * @return <code>true</code> if conflict; <code>false</code> otherwise.
	 */
	public static boolean isConflict(Hosted r1, EcoRelation r2) {
		assert(r1!=null && r2!=null);
		EcoIdentity m1 = r1.getSlave();
		EcoIdentity s1 = r1.getMaster();
		EcoIdentity m2, s2;
		m2 = s2 = null;
		if (r2 instanceof Hosted) {
			m2 = r2.getSlave();
			s2 = r2.getMaster();
			return isRelationConflict(m1,s1,m2,s2);
		} else if (r2 instanceof Hosting) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
			return isRelationConflict(m1,s1,m2,s2);
		} else {
			return false;
		}
	}
	
	/**
	 * @param r1
	 * @param r2
	 * @return <code>true</code> if conflict; <code>false</code> otherwise.
	 */
	public static boolean isConflict(Hosting r1, EcoRelation r2) {
		assert(r1!=null && r2!=null);
		EcoIdentity m1 = r1.getSlave();
		EcoIdentity s1 = r1.getMaster();
		EcoIdentity m2, s2;
		m2 = s2 = null;
		if (r2 instanceof Hosted) {
			m2 = r2.getSlave();
			s2 = r2.getMaster();
			return isRelationConflict(m1,s1,m2,s2);
		} else if (r2 instanceof Hosting) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
			return isRelationConflict(m1,s1,m2,s2);
		} else {
			return false;
		}
	}
	
	
	/**
	 * @param r1
	 * @param r2
	 * @return <code>true</code> if conflict; <code>false</code> otherwise.
	 */
	public static boolean isConflict(Up r1, EcoRelation r2) {
		assert(r1!=null && r2!=null);
		EcoIdentity m1 = r1.getSlave();
		EcoIdentity s1 = r1.getMaster();
		EcoIdentity m2, s2;
		m2 = s2 = null;
		if (r2 instanceof Down) {
			m2 = r2.getSlave();
			s2 = r2.getMaster();
			return isRelationConflict(m1,s1,m2,s2);
		} else if (r2 instanceof Up) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
			return isRelationConflict(m1,s1,m2,s2);
		} else {
			return false;
		}
	}
	
	/**
	 * @param r1
	 * @param r2
	 * @return <code>true</code> if conflict; <code>false</code> otherwise.
	 */
	public static boolean isConflict(Down r1, EcoRelation r2) {
		assert(r1!=null && r2!=null);
		EcoIdentity m1 = r1.getSlave();
		EcoIdentity s1 = r1.getMaster();
		EcoIdentity m2, s2;
		m2 = s2 = null;
		if (r2 instanceof Down) {
			m2 = r2.getSlave();
			s2 = r2.getMaster();
			return isRelationConflict(m1,s1,m2,s2);
		} else if (r2 instanceof Up) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
			return isRelationConflict(m1,s1,m2,s2);
		} else {
			return false;
		}
	}
	
	
	/**
	 * @param r1
	 * @param r2
	 * @return <code>true</code> if conflict; <code>false</code> otherwise.
	 */
	public static boolean isConflict(Left r1, EcoRelation r2) {
		assert(r1!=null && r2!=null);
		EcoIdentity m1 = r1.getSlave();
		EcoIdentity s1 = r1.getMaster();
		EcoIdentity m2, s2;
		m2 = s2 = null;
		if (r2 instanceof Right) {
			m2 = r2.getSlave();
			s2 = r2.getMaster();
			return isRelationConflict(m1,s1,m2,s2);
		} else if (r2 instanceof Left) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
			return isRelationConflict(m1,s1,m2,s2);
		} else {
			return false;
		}
	}
	
	/**
	 * @param r1
	 * @param r2
	 * @return <code>true</code> if conflict; <code>false</code> otherwise.
	 */
	public static boolean isConflict(Right r1, EcoRelation r2) {
		assert(r1!=null && r2!=null);
		EcoIdentity m1 = r1.getSlave();
		EcoIdentity s1 = r1.getMaster();
		EcoIdentity m2, s2;
		m2 = s2 = null;
		if (r2 instanceof Right) {
			m2 = r2.getSlave();
			s2 = r2.getMaster();
			return isRelationConflict(m1,s1,m2,s2);
		} else if (r2 instanceof Left) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
			return isRelationConflict(m1,s1,m2,s2);
		} else {
			return false;
		}
	}
	
	/**
	 * @param r1
	 * @param r2
	 * @return <code>true</code> if conflict; <code>false</code> otherwise.
	 */
	public static boolean isConflict(TileGoal r1, EcoRelation r2) {
		assert(r1!=null && r2!=null);
		EcoIdentity m1 = r1.getSlave();
		EcoIdentity s1 = r1.getMaster();
		EcoIdentity m2, s2;
		m2 = s2 = null;
		if (r2 instanceof PlaceGoal) {
			m2 = r2.getSlave();
			s2 = r2.getMaster();
			return isRelationConflict(m1,s1,m2,s2);
		} else if (r2 instanceof TileGoal) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
			return isRelationConflict(m1,s1,m2,s2);
		} else {
			return false;
		}
	}

	
	/**
	 * @param r1
	 * @param r2
	 * @return <code>true</code> if conflict; <code>false</code> otherwise.
	 */
	public static boolean isConflict(PlaceGoal r1, EcoRelation r2) {
		assert(r1!=null && r2!=null);
		EcoIdentity m1 = r1.getSlave();
		EcoIdentity s1 = r1.getMaster();
		EcoIdentity m2, s2;
		m2 = s2 = null;
		if (r2 instanceof PlaceGoal) {
			m2 = r2.getSlave();
			s2 = r2.getMaster();
			return isRelationConflict(m1,s1,m2,s2);
		} else if (r2 instanceof TileGoal) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();
			return isRelationConflict(m1,s1,m2,s2);
		} else {
			return false;
		}
	}
	
	
	/**
	 * @param r1
	 * @param r2
	 * @return <code>true</code> if conflict; <code>false</code> otherwise.
	 */
	public static boolean isConflict(Blank r1, EcoRelation r2) {
		assert(r1!=null && r2!=null);
		EcoIdentity m1 = r1.getSlave();
		EcoIdentity s1 = r1.getMaster();
		EcoIdentity m2, s2;
		m2 = s2 = null;
		if (r2 instanceof Blank) {
			m2 = r2.getMaster();
			s2 = r2.getSlave();		
			return isRelationConflict(m1,s1,m2,s2);	
		}
		return false;
	}
	
	private static boolean isRelationConflict(EcoIdentity master1, EcoIdentity slave1, EcoIdentity master2, EcoIdentity slave2) {
		if (slave1==null || slave2==null || master1==null || master2==null) return true;
		boolean sameSlave = slave1.equals(slave2);		
		boolean sameMaster = master1.equals(master2);
		return sameMaster ^ sameSlave;
	}

}
