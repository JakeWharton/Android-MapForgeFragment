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
 * Implementation of the Sutherland-Hodgman clipping algorithm.
 */
final class SutherlandHodgmanClipping {
	private static float[] clipPolylineToEdge(float[] polyline, int[] edge) {
		if (polyline == null) {
			return null;
		}

		float[] clippedPolyline = new float[polyline.length * 2];
		int clippedPolylineEntries = 0;
		float x1, y1, x2, y2;
		boolean isStartPointInside;
		boolean isEndPointInside;

		for (int i = 0; i < polyline.length - 2; i += 2) {
			x1 = polyline[i];
			y1 = polyline[i + 1];
			x2 = polyline[i + 2];
			y2 = polyline[i + 3];

			isStartPointInside = isInside(x1, y1, edge);
			isEndPointInside = isInside(x2, y2, edge);

			if (isStartPointInside) {
				if (clippedPolylineEntries == 0) {
					clippedPolyline[clippedPolylineEntries++] = x1;
					clippedPolyline[clippedPolylineEntries++] = y1;
				}
				if (isEndPointInside) {
					clippedPolyline[clippedPolylineEntries++] = x2;
					clippedPolyline[clippedPolylineEntries++] = y2;
				} else {
					float[] intersection = computeIntersection(edge, x1, y1, x2, y2);
					clippedPolyline[clippedPolylineEntries++] = intersection[0];
					clippedPolyline[clippedPolylineEntries++] = intersection[1];
				}
			} else if (isEndPointInside) {
				float[] intersection = computeIntersection(edge, x1, y1, x2, y2);
				clippedPolyline[clippedPolylineEntries++] = intersection[0];
				clippedPolyline[clippedPolylineEntries++] = intersection[1];
				clippedPolyline[clippedPolylineEntries++] = x2;
				clippedPolyline[clippedPolylineEntries++] = y2;
			}
		}

		if (clippedPolylineEntries == 0) {
			return null;
		}

		float[] retVal = new float[clippedPolylineEntries];
		System.arraycopy(clippedPolyline, 0, retVal, 0, clippedPolylineEntries);
		return retVal;
	}

	private static float[] computeIntersection(int[] edge, double x1, double y1, double x2,
			double y2) {
		if (edge[1] == edge[3]) {
			// horizontal edge
			return new float[] { (float) (x1 + (edge[1] - y1) * ((x2 - x1) / (y2 - y1))),
					edge[1] };
		}
		// vertical edge
		return new float[] { edge[0], (float) (y1 + (edge[0] - x1) * ((y2 - y1) / (x2 - x1))) };
	}

	private static boolean isInside(double x, double y, int[] edge) {
		if (edge[0] < edge[2]) {
			// bottom edge
			return y >= edge[1];
		} else if (edge[0] > edge[2]) {
			// top edge
			return y <= edge[1];
		} else if (edge[1] < edge[3]) {
			// right edge
			return x <= edge[0];
		} else if (edge[1] > edge[3]) {
			// left edge
			return x >= edge[0];
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Clips a polyline to a rectangular clipping region.
	 * 
	 * @param polyline
	 *            coordinates of the polyline
	 * @param rectangle
	 *            coordinates of the rectangle
	 * @return the clipped polyline or null in case of no intersection
	 */
	static float[] clipPolyline(float[] polyline, int[] rectangle) {
		// bottom edge
		float[] clippedPolyline = clipPolylineToEdge(polyline, new int[] { rectangle[0],
				rectangle[1], rectangle[2], rectangle[1] });
		// right edge
		clippedPolyline = clipPolylineToEdge(clippedPolyline, new int[] { rectangle[2],
				rectangle[1], rectangle[2], rectangle[3] });
		// top edge
		clippedPolyline = clipPolylineToEdge(clippedPolyline, new int[] { rectangle[2],
				rectangle[3], rectangle[0], rectangle[3] });
		// left edge
		return clipPolylineToEdge(clippedPolyline, new int[] { rectangle[0], rectangle[3],
				rectangle[0], rectangle[1] });
	}

	/**
	 * Empty private constructor to prevent object creation.
	 */
	private SutherlandHodgmanClipping() {
		// do nothing
	}
}