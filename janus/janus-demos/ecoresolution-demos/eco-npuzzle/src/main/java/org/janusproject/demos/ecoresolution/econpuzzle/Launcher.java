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
package org.janusproject.demos.ecoresolution.econpuzzle;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.janusproject.demos.ecoresolution.econpuzzle.agent.EcoBlank;
import org.janusproject.demos.ecoresolution.econpuzzle.agent.EcoPlace;
import org.janusproject.demos.ecoresolution.econpuzzle.agent.EcoTile;
import org.janusproject.demos.ecoresolution.econpuzzle.agent.NPuzzleProblem;
import org.janusproject.demos.ecoresolution.econpuzzle.ui.NPuzzleFrame;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;

/**
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {

	/**
	 * @param argv
	 * @throws Exception
	 */
	public static void main(String[] argv) throws Exception {
		final int gridsize = 4;
		final int placeNumber = gridsize * gridsize;

		// Grid initialization

		// Problem initialization
		NPuzzleProblem problem = new NPuzzleProblem();

		// initialization of places
		EcoPlace[] places = new EcoPlace[placeNumber];
		EcoPlace[][] placesGrid = new EcoPlace[gridsize][gridsize];
		List<Integer> hostingPlaceIds = new LinkedList<Integer>();
		List<Integer> goalPlaceIds = new LinkedList<Integer>();
		{
			int x, y;
			for (int i = 0; i < placeNumber; i++) {
				places[i] = new EcoPlace(Integer.toString(i),i);
				hostingPlaceIds.add(i);
				goalPlaceIds.add(i);
				x = (i % gridsize);
				y = i / gridsize;
				placesGrid[x][y] = places[i];
			}
		}

		// initialization of places neighboring
		for (int x = 0; x < gridsize; x++) {
			for (int y = 0; y < gridsize; y++) {
				if (x>0) {
					problem.addLeftRightRelation(placesGrid[x - 1][y], placesGrid[x][y]);
				} else {
					problem.addRightNothingRelation(placesGrid[x][y]);
				} 
				
				if(x==gridsize-1) {
					problem.addLeftNothingRelation(placesGrid[x][y]);
				}

				if (y>0) {
					problem.addUpDownRelation(placesGrid[x][y - 1], placesGrid[x][y]);
				} else {
					problem.addDownNothingRelation(placesGrid[x][y]);
				} 
				
				if(y==gridsize-1) {
					problem.addUpNothingRelation(placesGrid[x][y]);
				}
			}
		}

		Collections.shuffle(hostingPlaceIds);
		Collections.shuffle(goalPlaceIds);

		// placeNumber-1 = number of normal tiles, the last one is the blank tile
		EcoTile[] tiles = new EcoTile[placeNumber];
		EcoTile[][] tilesGrid = new EcoTile[gridsize][gridsize];

		Integer hostingPlaceId;
		Integer goalPlaceId;

		{// initialization of tiles and defines the corresponding place info
			int x, y;
			for (int i = 0; i < placeNumber - 1; i++) {//the last one is the blank tile
				hostingPlaceId = hostingPlaceIds.remove(0);
				goalPlaceId = goalPlaceIds.remove(0);
				tiles[i] = new EcoTile(Integer.toString(goalPlaceId));
				problem.addHostedHostingRelation(places[hostingPlaceId], tiles[i]);
				problem.addGoalRelation(places[goalPlaceId], tiles[i]);

				x = (hostingPlaceId % gridsize);
				y = hostingPlaceId / gridsize;
				tilesGrid[x][y] = tiles[i];
			}

			// Blank tile initialization
			tiles[placeNumber - 1] = new EcoBlank("Blank"); //$NON-NLS-1$
			hostingPlaceId = hostingPlaceIds.remove(0);
			problem.addHostedHostingRelation(places[hostingPlaceId], tiles[placeNumber - 1]);
			x = (hostingPlaceId % gridsize);			
			y = hostingPlaceId / gridsize;
			tilesGrid[x][y] = tiles[placeNumber - 1];
		}
		
		// initialization of tiles neighboring
		for (int x = 0; x < gridsize; x++) {
			for (int y = 0; y < gridsize; y++) {
				if (x>0) {
					problem.addLeftRightRelation(tilesGrid[x - 1][y], tilesGrid[x][y]);
				} else {
					problem.addRightNothingRelation(tilesGrid[x][y]);
				} 
				
				if (x==gridsize-1) {
					problem.addLeftNothingRelation(tilesGrid[x][y]);
				}

				if (y>0) {
					problem.addUpDownRelation(tilesGrid[x][y - 1], tilesGrid[x][y]);
				} else {
					problem.addDownNothingRelation(tilesGrid[x][y]);
				} 
				
				if (y==gridsize-1) {
					problem.addUpNothingRelation(tilesGrid[x][y]);
				}
			}
		}

		//init blank relations linking all classical tiles with blank tiles
		for (int i = 0; i < placeNumber - 1; i++) {
			problem.addBlankRelation((EcoBlank)tiles[placeNumber - 1], tiles[i]);
			
		}
		
		
		//Creating frame and launching NPuzzle
		Kernel kernel = Kernels.get();
		
		NPuzzleFrame frame = new NPuzzleFrame(kernel,gridsize);
		frame.setVisible(true);
				
		problem.solve(null);
	}

}
