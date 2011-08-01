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

import android.graphics.Rect;

/**
 * Holds all parameters of a map file.
 */
class MapFileParameters {
	/**
	 * Divisor for converting coordinates stored as integers to double values.
	 */
	private static final double COORDINATES_DIVISOR = 1000000.0;

	/**
	 * Stores the hash value of this object.
	 */
	private final int hashCode;

	/**
	 * Base zoom level of the map file, which equals to one block.
	 */
	final byte baseZoomLevel;

	/**
	 * Size of the entries table at the beginning of each block in bytes.
	 */
	final int blockEntriesTableSize;

	/**
	 * Vertical amount of blocks in the grid.
	 */
	final long blocksHeight;

	/**
	 * Horizontal amount of blocks in the grid.
	 */
	final long blocksWidth;

	/**
	 * Y number of the tile at the bottom boundary in the grid.
	 */
	final long boundaryBottomTile;

	/**
	 * X number of the tile at the left boundary in the grid.
	 */
	final long boundaryLeftTile;

	/**
	 * X number of the tile at the right boundary in the grid.
	 */
	final long boundaryRightTile;

	/**
	 * Y number of the tile at the top boundary in the grid.
	 */
	final long boundaryTopTile;

	/**
	 * Absolute start address of the index in the enclosing file.
	 */
	final long indexStartAddress;

	/**
	 * Size of the map file in bytes.
	 */
	final long mapFileSize;

	/**
	 * Total number of blocks in the grid.
	 */
	final long numberOfBlocks;

	/**
	 * Absolute start address of the map file in the enclosing file.
	 */
	final long startAddress;

	/**
	 * Maximum zoom level for which the block entries tables are made.
	 */
	final byte zoomLevelMax;
	/**
	 * Minimum zoom level for which the block entries tables are made.
	 */
	final byte zoomLevelMin;

	/**
	 * Creates a new immutable set of parameters for a MapFileParameters.
	 * 
	 * @param startAddress
	 *            the start address of the map file.
	 * @param indexStartAddress
	 *            the start address of the index.
	 * @param mapFileSize
	 *            the size of the map file.
	 * @param baseZoomLevel
	 *            the base zoom level of the map file.
	 * @param tileZoomLevelMin
	 *            the minimum zoom level of the map file.
	 * @param tileZoomLevelMax
	 *            the maximum zoom level of the map file.
	 * @param mapBoundary
	 *            the boundary of the map file.
	 */
	MapFileParameters(long startAddress, long indexStartAddress, long mapFileSize,
			byte baseZoomLevel, byte tileZoomLevelMin, byte tileZoomLevelMax, Rect mapBoundary) {
		this.startAddress = startAddress;
		this.indexStartAddress = indexStartAddress;
		this.mapFileSize = mapFileSize;
		this.baseZoomLevel = baseZoomLevel;
		this.zoomLevelMin = tileZoomLevelMin;
		this.zoomLevelMax = tileZoomLevelMax;
		this.hashCode = calculateHashCode();

		// calculate the XY numbers of the boundary tiles in this map file
		this.boundaryTopTile = MercatorProjection.latitudeToTileY(mapBoundary.bottom
				/ COORDINATES_DIVISOR, this.baseZoomLevel);
		this.boundaryLeftTile = MercatorProjection.longitudeToTileX(mapBoundary.left
				/ COORDINATES_DIVISOR, this.baseZoomLevel);
		this.boundaryBottomTile = MercatorProjection.latitudeToTileY(mapBoundary.top
				/ COORDINATES_DIVISOR, this.baseZoomLevel);
		this.boundaryRightTile = MercatorProjection.longitudeToTileX(mapBoundary.right
				/ COORDINATES_DIVISOR, this.baseZoomLevel);

		// calculate the horizontal and vertical amount of blocks in this map file
		this.blocksWidth = this.boundaryRightTile - this.boundaryLeftTile + 1;
		this.blocksHeight = this.boundaryBottomTile - this.boundaryTopTile + 1;

		// calculate the total amount of blocks in this map file
		this.numberOfBlocks = this.blocksWidth * this.blocksHeight;

		// calculate the size of the tile entries table
		this.blockEntriesTableSize = 2 * (this.zoomLevelMax - this.zoomLevelMin + 1) * 2;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof MapFileParameters)) {
			return false;
		}
		MapFileParameters other = (MapFileParameters) obj;
		if (this.startAddress != other.startAddress) {
			return false;
		} else if (this.mapFileSize != other.mapFileSize) {
			return false;
		} else if (this.baseZoomLevel != other.baseZoomLevel) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	/**
	 * Calculates the hash value of this object.
	 * 
	 * @return the hash value of this object.
	 */
	private int calculateHashCode() {
		int result = 7;
		result = 31 * result + (int) (this.startAddress ^ (this.startAddress >>> 32));
		result = 31 * result + (int) (this.mapFileSize ^ (this.mapFileSize >>> 32));
		result = 31 * result + this.baseZoomLevel;
		return result;
	}
}