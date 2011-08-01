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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import android.graphics.Rect;

/**
 * A tile represents a rectangular part of the world map. All tiles can be identified by their X and Y
 * number together with their zoom level. The actual area that a tile covers on a map depends on the
 * underlying map projection.
 */
class Tile implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Amount of bytes per pixel of a map tile.
	 */
	static final byte TILE_BYTES_PER_PIXEL = 2;

	/**
	 * Width and height of a map tile in pixel.
	 */
	static final int TILE_SIZE = 256;

	/**
	 * Size of a single map tile in bytes.
	 */
	static final int TILE_SIZE_IN_BYTES = TILE_SIZE * TILE_SIZE * TILE_BYTES_PER_PIXEL;

	/**
	 * Stores the hash value of this object.
	 */
	private transient int hashCode;

	/**
	 * Pixel X coordinate of the upper left corner of this tile on the world map.
	 */
	transient long pixelX;

	/**
	 * Pixel Y coordinate of the upper left corner of this tile on the world map.
	 */
	transient long pixelY;

	/**
	 * X number of this tile.
	 */
	final long x;

	/**
	 * Y number of this tile.
	 */
	final long y;

	/**
	 * Zoom level of this tile.
	 */
	final byte zoomLevel;

	/**
	 * Constructs an immutable tile with the specified XY number and zoom level.
	 * 
	 * @param x
	 *            the X number of the tile.
	 * @param y
	 *            the Y number of the tile.
	 * @param zoomLevel
	 *            the zoom level of the tile.
	 */
	Tile(long x, long y, byte zoomLevel) {
		this.x = x;
		this.y = y;
		this.zoomLevel = zoomLevel;
		calculateTransientValues();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof Tile)) {
			return false;
		}
		Tile other = (Tile) obj;
		if (this.x != other.x) {
			return false;
		} else if (this.y != other.y) {
			return false;
		} else if (this.zoomLevel != other.zoomLevel) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public String toString() {
		return this.zoomLevel + "/" + this.x + "/" + this.y;
	}

	/**
	 * Calculates the hash value of this object.
	 * 
	 * @return the hash value of this object.
	 */
	private int calculateHashCode() {
		int result = 7;
		result = 31 * result + (int) (this.x ^ (this.x >>> 32));
		result = 31 * result + (int) (this.y ^ (this.y >>> 32));
		result = 31 * result + this.zoomLevel;
		return result;
	}

	/**
	 * Calculates the values of some transient variables.
	 */
	private void calculateTransientValues() {
		this.pixelX = this.x * TILE_SIZE;
		this.pixelY = this.y * TILE_SIZE;
		this.hashCode = calculateHashCode();
	}

	private void readObject(ObjectInputStream objectInputStream) throws IOException,
			ClassNotFoundException {
		objectInputStream.defaultReadObject();
		calculateTransientValues();
	}

	/**
	 * Calculates the bounding box of this tile.
	 * 
	 * @return the bounding box of this tile.
	 */
	Rect getBoundingBox() {
		return new Rect(
				(int) (MercatorProjection.pixelXToLongitude(this.pixelX, this.zoomLevel) * 1000000),
				(int) (MercatorProjection.pixelYToLatitude(this.pixelY, this.zoomLevel) * 1000000),
				(int) (MercatorProjection.pixelXToLongitude(this.pixelX + TILE_SIZE,
						this.zoomLevel) * 1000000), (int) (MercatorProjection.pixelYToLatitude(
						this.pixelY + TILE_SIZE, this.zoomLevel) * 1000000));
	}
}