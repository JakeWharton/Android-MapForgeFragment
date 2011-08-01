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
 * An ImmutablePoint represents an fixed pair of float coordinates.
 */
class ImmutablePoint implements Comparable<ImmutablePoint> {
	/**
	 * Subtracts the x and y coordinates of one point from another point.
	 * 
	 * @param minuend
	 *            the minuend.
	 * @param subtrahend
	 *            the subtrahend.
	 * @return a new Point object.
	 */
	static ImmutablePoint substract(ImmutablePoint minuend, ImmutablePoint subtrahend) {
		return new ImmutablePoint(minuend.x - subtrahend.x, minuend.y - subtrahend.y);
	}

	/**
	 * Stores the hash value of this object.
	 */
	private final int hashCode;

	/**
	 * X coordinate of this point.
	 */
	final float x;

	/**
	 * Y coordinate of this point.
	 */
	final float y;

	/**
	 * Constructs a new ImmutablePoint with the given x and y coordinates.
	 * 
	 * @param x
	 *            the x coordinate of the point.
	 * @param y
	 *            the y coordinate of the point.
	 */
	ImmutablePoint(float x, float y) {
		this.x = x;
		this.y = y;
		this.hashCode = calculateHashCode();
	}

	@Override
	public int compareTo(ImmutablePoint point) {
		if (this.x > point.x) {
			return 1;
		} else if (this.x < point.x) {
			return -1;
		} else if (this.y > point.y) {
			return 1;
		} else if (this.y < point.y) {
			return -1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ImmutablePoint)) {
			return false;
		}
		ImmutablePoint other = (ImmutablePoint) obj;
		if (this.x != other.x) {
			return false;
		} else if (this.y != other.y) {
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
		return this.x + "," + this.y;
	}

	/**
	 * Calculates the hash value of this object.
	 * 
	 * @return the hash value of this object.
	 */
	private int calculateHashCode() {
		int result = 7;
		result = 31 * result + Float.floatToIntBits(this.x);
		result = 31 * result + Float.floatToIntBits(this.y);
		return result;
	}
}