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
package org.janusproject.demos.jaak.ants.environment;

import java.awt.Color;

import org.janusproject.demos.jaak.ants.AntColonySystem;
import org.janusproject.jaak.envinterface.endogenous.AutonomousEndogenousProcess;
import org.janusproject.jaak.envinterface.influence.Influence;
import org.janusproject.jaak.envinterface.perception.FloatSubstance;
import org.janusproject.jaak.envinterface.perception.Substance;

/** This class defines a pheromon.
 * <p>
 * Ants use a signaling communication system based on the deposition 
 * of pheromone over the path it follows, marking a trail. 
 * Pheromone is a hormone produced by ants that establishes a 
 * sort of indirect communication among them. Basically, an 
 * isolated ant moves at random, but when it finds a pheromone 
 * trail there is a high probability that this ant will 
 * decide to follow the trail.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class Pheromone extends FloatSubstance implements AutonomousEndogenousProcess, Cloneable {
	
	private static final long serialVersionUID = 791462106958634527L;

	private final float evaporationAmount;
	private final Color color;
	
	/**
	 * @param pheromoneIntensity is the initial intensity of the pheromone.
	 * @param evaporationAmount is the amount of pheromone which is evaporating
	 * during one second.
	 * @param color is the color of the pheromone.
	 */
	public Pheromone(float pheromoneIntensity, float evaporationAmount, Color color) {
		this(pheromoneIntensity, evaporationAmount, Pheromone.class, color);
	}
	
	/**
	 * @param pheromoneIntensity is the initial intensity of the pheromone.
	 * @param evaporationAmount is the amount of pheromone which is evaporating
	 * during one second.
	 * @param semantic is the semantic of the pheromone.
	 * @param color is the color of the pheromone.
	 */
	public Pheromone(float pheromoneIntensity, float evaporationAmount, Object semantic, Color color) {
		super(pheromoneIntensity, semantic);
		this.evaporationAmount = evaporationAmount;
		this.color = color;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pheromone clone() {
		try {
			return (Pheromone)super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** Replies the amount of pheromone which is evaporating during one second.
	 * 
	 * @return the amount of pheromone which is evaporating during one second.
	 */
	public final float getEvaporation() {
		return this.evaporationAmount;
	}

	/** Replies the intensity of the pheromone.
	 * 
	 * @return  the intensity of the pheromone.
	 */
	public final float getIntensity() {
		return getAmount().floatValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final Substance decrement(Substance s) {
		if (s instanceof Pheromone) {
			float oldValue = floatValue();
			decrement(s.floatValue());
			Pheromone p = clone();
			p.value = Math.abs(floatValue() - oldValue);
			return p;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final Substance increment(Substance s) {
		if (s instanceof Pheromone) {
			float oldValue = floatValue();
			increment(s.floatValue());
			Pheromone p = clone();
			p.value = Math.abs(floatValue() - oldValue);
			return p;
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void increment(float a) {
		super.increment(a);
		if (this.value>AntColonySystem.MAX_PHEROMONE_AMOUNT)
			this.value = AntColonySystem.MAX_PHEROMONE_AMOUNT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Influence runAutonomousEndogenousProcess(
			float currentTime,
			float simulationStepDuration) {
		decrement(simulationStepDuration, this.evaporationAmount);
		if (floatValue()<=0f) {
			return createRemovalInfluenceForItself();
		}
		return null;
	}
	
	/** Replies the color associated to this pheromone.
	 * @return the color associated to this pheromone.
	 */
	public Color getColor() {
		float d = floatValue() / AntColonySystem.MAX_PHEROMONE_AMOUNT;
		return new Color(
				(int)(this.color.getRed()*d),
				(int)(this.color.getGreen()*d),
				(int)(this.color.getBlue()*d));
	}
	
}