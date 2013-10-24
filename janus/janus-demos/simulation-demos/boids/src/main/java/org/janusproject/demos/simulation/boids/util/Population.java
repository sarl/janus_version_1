/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010 Janus Core Developers
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
package org.janusproject.demos.simulation.boids.util;

import java.awt.Color;

/**
 * Features and configuration of a population/group of boids.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Population {
	
	/**
	 * Default repulsion force.
	 */
    public static final float DEFAULT_REPULSION_FORCE = 5.0f;

	/**
	 * Default separation force.
	 */
    public static final float DEFAULT_SEPARATION_FORCE = 1.0f;

	/**
	 * Default cohesion force.
	 */
    public static final float DEFAULT_COHESION_FORCE = 0.0001f;

	/**
	 * Default alignment force.
	 */
    public static final float DEFAULT_ALIGNEMENT_FORCE = 1.0f;

	/**
	 * Default repulsion distance.
	 */
    public static final float DEFAULT_REPULSION_DIST = 100.0f;

	/**
	 * Default separation distance.
	 */
    public static final float DEFAULT_SEPARATION_DIST = 10.0f;

    /**
	 * Default cohesion distance.
	 */
    public static final float DEFAULT_COHESION_DIST = 100.0f;

	/**
	 * Default alignment distance.
	 */
    public static final float DEFAULT_ALIGNMENT_DIST = 100.0f;

	/**
	 * Default count of boids.
	 */
    public static final int DEFAULT_BOIDS_NB = 10;

    /** Color associated to the population.
     */
    public final Color color;
    
    /** Max speed for boids in this population.
     */
    public final float maxSpeed;

    /** Max force for boids in this population.
     */
    public final float maxForce;

    /** Field of view of boids in this population.
     */
    public final float visibleAngle;

    /** Mass of boids in this population.
     */
    public final float mass;
    
    /** Norm of the acceleration for boids.
     */
    public final float acceleration;

    /** Indicates if cohesion may be computed in this population.
     */
    public final boolean cohesionOn = true;
    /** Indicates if repulsion may be computed in this population.
     */
    public final boolean repulsionOn = true;
    /** Indicates if alignment may be computed in this population.
     */
    public final boolean alignementOn = true;
    /** Indicates if separation may be computed in this population.
     */
    public final boolean separationOn = true;
    
    /** Separation force for this group.
     */
    public final float separationForce;
    /** Cohesion force for this group.
     */
    public final float cohesionForce;
    /** Alignement force for this group.
     */
    public final float alignmentForce;
    /** Repulsion force for this group.
     */
    public final float repulsionForce;
    /** Separation distance for this group.
     */
    public final float separationDistance;
    /** Cohesion distance for this group.
     */
    public final float cohesionDistance;
    /** Alignement distance for this group.
     */
    public final float alignmentDistance;
    /** Repulsion distance for this group.
     */
    public final float repulsionDistance;
    /** Name of the population.
     */
    public final String name;

    /**
     * @param col is the color of the population.
     * @param name is the name of the population.
     */
    public Population(Color col, String name) {
    	assert(col!=null);
    	this.name = name;
		this.color = col;
		this.maxSpeed = 2;
		this.maxForce = 1.7f;
		this.visibleAngle = (float)Math.toRadians(90.);
		this.mass = 1.0f;
		this.acceleration = 0.85f;
		this.separationForce = DEFAULT_SEPARATION_FORCE;
		this.cohesionForce = DEFAULT_COHESION_FORCE;
		this.alignmentForce = DEFAULT_ALIGNEMENT_FORCE;
		this.repulsionForce = DEFAULT_REPULSION_FORCE;
		this.separationDistance = DEFAULT_SEPARATION_DIST;
		this.cohesionDistance = DEFAULT_COHESION_DIST;
		this.alignmentDistance = DEFAULT_ALIGNMENT_DIST;
		this.repulsionDistance = DEFAULT_REPULSION_DIST;
    }
    
    @Override
    public boolean equals(Object o) {
    	if (o instanceof Population) {
    		Population p = (Population)o;
    		return this.color.equals(p.color);
    	}
    	return false;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return this.color==null ? 0 : this.color.hashCode();
	}
    
}

