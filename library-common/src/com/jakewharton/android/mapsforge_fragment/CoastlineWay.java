/*
 * Copyright 2010, 2011 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jakewharton.android.mapsforge_fragment;

/**
 * A CoastlineWay is a special way to outline a sea or an island.
 * 
 * @see <a href="http://wiki.openstreetmap.org/wiki/Tag:natural%3Dcoastline">Tag:natural=coastline</a>
 */
class CoastlineWay {
	private static final byte BOTTOM = 1;
	private static final byte LEFT = 2;
	private static final byte RIGHT = 0;
	private static final byte TOP = 3;

	/**
	 * Calculates the angle for a given coastline point. The angle is defined as zero at the middle of
	 * the right tile side. The angle increases clockwise, therefore the middle of the bottom tile side
	 * has a value of π/2 and the middle of the left tile side a value of π.
	 * 
	 * @param x
	 *            the x coordinate of the coastline point.
	 * @param y
	 *            the y coordinate of the coastline point.
	 * @param tileBoundaries
	 *            the boundaries of the tile.
	 * @param tileSize
	 *            the size of the tile.
	 * @return the angle, always between 0 (inclusively) and 2π (exclusively).
	 */
	private static double calculateAngle(float x, float y, int[] tileBoundaries, int tileSize) {
		double angle = Math.atan2(y - tileBoundaries[1] - (tileSize >> 1), x
				- tileBoundaries[0] - (tileSize >> 1));
		if (angle < 0) {
			return angle + 2 * Math.PI;
		}
		return angle;
	}

	/**
	 * Calculates the side of the tile that corresponds to a given angle.
	 * 
	 * @param angle
	 *            the angle, must be between 0 (inclusively) and 2π (exclusively).
	 * @return the corresponding side of the tile.
	 */
	private static byte calculateSide(double angle) {
		if (angle < Math.PI * 0.25) {
			return RIGHT;
		} else if (angle < Math.PI * 0.75) {
			return BOTTOM;
		} else if (angle < Math.PI * 1.25) {
			return LEFT;
		} else if (angle < Math.PI * 1.75) {
			return TOP;
		} else {
			return RIGHT;
		}
	}

	/**
	 * Calculates the orientation of the given coastline segment by calculating the signed area. As the
	 * origin is in the top left corner, a positive area means clockwise.
	 * 
	 * @param coastline
	 *            the coordinates of the coastline segment.
	 * @return true if the orientation is clockwise, false otherwise.
	 */
	static boolean isClockWise(float[] coastline) {
		double area = 0;
		int nextNode;
		for (int currentNode = 0; currentNode < coastline.length; currentNode += 2) {
			nextNode = (currentNode + 2) % coastline.length;
			area += (coastline[currentNode] + coastline[nextNode])
					* (coastline[nextNode + 1] - coastline[currentNode + 1]);
		}
		return area > 0;
	}

	/**
	 * Checks if the given coastline segment is closed.
	 * 
	 * @param coastline
	 *            the coordinates of the coastline segment.
	 * @return true if the given coastline segment is closed, false otherwise.
	 */
	static boolean isClosed(float[] coastline) {
		return coastline[0] == coastline[coastline.length - 2]
				&& coastline[1] == coastline[coastline.length - 1];
	}

	/**
	 * Checks if the given coastline segment starts and ends outside of the tile.
	 * 
	 * @param coastline
	 *            the coordinates of the coastline segment.
	 * @param tileBoundaries
	 *            the boundaries of the tile.
	 * @return true if first and last point are outside of the tile, false otherwise.
	 */
	static boolean isValid(float[] coastline, int[] tileBoundaries) {
		return (coastline[0] <= tileBoundaries[0] || coastline[0] >= tileBoundaries[2]
				|| coastline[1] <= tileBoundaries[1] || coastline[1] >= tileBoundaries[3])
				&& (coastline[coastline.length - 2] <= tileBoundaries[0]
						|| coastline[coastline.length - 2] >= tileBoundaries[2]
						|| coastline[coastline.length - 1] <= tileBoundaries[1] || coastline[coastline.length - 1] >= tileBoundaries[3]);
	}

	/**
	 * Way node coordinates of the clipped coastline way.
	 */
	final float[] data;

	/**
	 * Angle at which the clipped coastline way enters the tile.
	 */
	final double entryAngle;

	/**
	 * Side on which the clipped coastline way enters the tile.
	 */
	final byte entrySide;

	/**
	 * Angle at which the clipped coastline way leaves the tile.
	 */
	final double exitAngle;

	/**
	 * Side on which the clipped coastline way leaves the tile.
	 */
	final byte exitSide;

	/**
	 * Constructs a new CoastlineWay with the given coordinates.
	 * 
	 * @param coastline
	 *            the coordinates of the coastline segment.
	 * @param tileBoundaries
	 *            the boundaries of the tile.
	 * @param tileSize
	 *            the size of the tile.
	 */
	CoastlineWay(float[] coastline, int[] tileBoundaries, int tileSize) {
		this.data = coastline;
		this.entryAngle = calculateAngle(this.data[0], this.data[1], tileBoundaries, tileSize);
		this.exitAngle = calculateAngle(this.data[this.data.length - 2],
				this.data[this.data.length - 1], tileBoundaries, tileSize);
		this.entrySide = calculateSide(this.entryAngle);
		this.exitSide = calculateSide(this.exitAngle);
	}
}