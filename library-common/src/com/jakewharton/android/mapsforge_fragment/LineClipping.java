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
 * Fast implementation of the Cohen-Sutherland line clipping algorithm.
 */
final class LineClipping {
	private static final byte BOTTOM = 1;
	private static final byte LEFT = 2;
	private static final byte RIGHT = 4;
	private static final byte TOP = 8;

	/**
	 * Computes the region code for a given point and rectangle.
	 * 
	 * @param x
	 *            the x coordinate of the point.
	 * @param y
	 *            the y coordinate of the point.
	 * @param left
	 *            left coordinate of the rectangle.
	 * @param bottom
	 *            bottom coordinate of the rectangle.
	 * @param right
	 *            right coordinate of the rectangle.
	 * @param top
	 *            top coordinate of the rectangle.
	 * @return the region code bitmask.
	 */
	private static byte calculateOutCode(double x, double y, double left, double bottom,
			double right, double top) {
		if (y > top) {
			if (x > right) {
				return TOP | RIGHT;
			} else if (x < left) {
				return TOP | LEFT;
			} else {
				return TOP;
			}
		} else if (y < bottom) {
			if (x > right) {
				return BOTTOM | RIGHT;
			} else if (x < left) {
				return BOTTOM | LEFT;
			} else {
				return BOTTOM;
			}
		} else {
			if (x > right) {
				return RIGHT;
			} else if (x < left) {
				return LEFT;
			} else {
				return 0;
			}
		}
	}

	/**
	 * Clips a line to a rectangle.
	 * 
	 * @param x1
	 *            first x coordinate of the line.
	 * @param y1
	 *            first y coordinate of the line.
	 * @param x2
	 *            second x coordinate of the line.
	 * @param y2
	 *            second y coordinate of the line.
	 * @param left
	 *            left coordinate of the rectangle.
	 * @param bottom
	 *            bottom coordinate of the rectangle.
	 * @param right
	 *            right coordinate of the rectangle.
	 * @param top
	 *            top coordinate of the rectangle.
	 * @return the clipped line or null, in case of no intersection.
	 */
	static double[] clipLineToRectangle(double x1, double y1, double x2, double y2,
			double left, double bottom, double right, double top) {
		double x1new = x1;
		double y1new = y1;
		double x2new = x2;
		double y2new = y2;

		// compute the region codes for both points
		byte outcode1 = calculateOutCode(x1, y1, left, bottom, right, top);
		byte outcode2 = calculateOutCode(x2, y2, left, bottom, right, top);

		while (true) {
			if ((outcode1 | outcode2) == 0) {
				// both points are inside the rectangle
				return new double[] { x1new, y1new, x2new, y2new };
			} else if ((outcode1 & outcode2) > 0) {
				// both points are outside the rectangle in the same region
				return null;
			} else if (outcode1 != 0) {
				// first point is outside the rectangle
				if ((outcode1 & TOP) > 0) {
					x1new = x1new + (x2new - x1new) * (top - y1new) / (y2new - y1new);
					y1new = top;
				} else if ((outcode1 & BOTTOM) > 0) {
					x1new = x1new + (x2new - x1new) * (bottom - y1new) / (y2new - y1new);
					y1new = bottom;
				} else if ((outcode1 & RIGHT) > 0) {
					y1new = y1new + (y2new - y1new) * (right - x1new) / (x2new - x1new);
					x1new = right;
				} else { // must be LEFT
					y1new = y1new + (y2new - y1new) * (left - x1new) / (x2new - x1new);
					x1new = left;
				}
				// recompute the region code for the first point
				outcode1 = calculateOutCode(x1new, y1new, left, bottom, right, top);
			} else {
				// second point is outside the rectangle
				if ((outcode2 & TOP) > 0) {
					x2new = x1new + (x2new - x1new) * (top - y1new) / (y2new - y1new);
					y2new = top;
				} else if ((outcode2 & BOTTOM) > 0) {
					x2new = x1new + (x2new - x1new) * (bottom - y1new) / (y2new - y1new);
					y2new = bottom;
				} else if ((outcode2 & RIGHT) > 0) {
					y2new = y1new + (y2new - y1new) * (right - x1new) / (x2new - x1new);
					x2new = right;
				} else { // must be LEFT
					y2new = y1new + (y2new - y1new) * (left - x1new) / (x2new - x1new);
					x2new = left;
				}
				// recompute the region code for the second point
				outcode2 = calculateOutCode(x2new, y2new, left, bottom, right, top);
			}
		}
	}

	/**
	 * Empty private constructor to prevent object creation.
	 */
	private LineClipping() {
		// do nothing
	}
}