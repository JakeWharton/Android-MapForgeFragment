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

import android.os.SystemClock;

/**
 * A ZoomAnimator handles the zoom-in and zoom-out animations of the corresponding MapView. It runs in a
 * separate thread to avoid blocking the UI thread.
 */
class ZoomAnimator extends Thread {
	private static final int DEFAULT_DURATION = 300;
	private static final int FRAME_LENGTH = 15;
	private static final String THREAD_NAME = "ZoomAnimator";

	private int duration;
	private boolean executeAnimation;
	private MapView mapView;
	private float pivotX;
	private float pivotY;
	private float scaleFactorApplied;
	private long timeStart;
	private float zoomDifference;
	private float zoomEnd;
	private float zoomStart;

	/**
	 * Constructs a new ZoomAnimator with the default duration.
	 */
	ZoomAnimator() {
		super();
		setDuration(DEFAULT_DURATION);
	}

	@Override
	public void run() {
		setName(THREAD_NAME);

		long timeElapsed;
		float timeElapsedPercent;
		float currentZoom;
		float scaleFactor;

		while (!isInterrupted()) {
			synchronized (this) {
				while (!isInterrupted() && !this.executeAnimation) {
					try {
						wait();
					} catch (InterruptedException e) {
						// restore the interrupted status
						interrupt();
					}
				}
			}

			if (isInterrupted()) {
				break;
			}

			// calculate the elapsed time
			timeElapsed = SystemClock.uptimeMillis() - this.timeStart;
			timeElapsedPercent = Math.min(1, timeElapsed / (float) this.duration);

			// calculate the zoom and scale values at the current moment
			currentZoom = this.zoomStart + timeElapsedPercent * this.zoomDifference;
			scaleFactor = currentZoom / this.scaleFactorApplied;
			this.scaleFactorApplied *= scaleFactor;
			this.mapView.matrixPostScale(scaleFactor, scaleFactor, this.pivotX, this.pivotY);

			// check if the animation time is over
			if (timeElapsed >= this.duration) {
				this.executeAnimation = false;
				this.mapView.handleTiles();
			} else {
				this.mapView.postInvalidate();
				synchronized (this) {
					try {
						wait(FRAME_LENGTH);
					} catch (InterruptedException e) {
						// restore the interrupted status
						interrupt();
					}
				}
			}
		}

		// set the pointer to null to avoid memory leaks
		this.mapView = null;
	}

	/**
	 * Sets the duration of the animation in milliseconds.
	 * 
	 * @param duration
	 *            the duration of the animation in milliseconds.
	 * @throws IllegalArgumentException
	 *             if the duration is negative.
	 */
	private void setDuration(int duration) {
		if (duration < 0) {
			throw new IllegalArgumentException();
		}
		this.duration = duration;
	}

	/**
	 * Returns the status of the ZoomAnimator.
	 * 
	 * @return true if the ZoomAnimator is working, false otherwise.
	 */
	boolean isExecuting() {
		return this.executeAnimation;
	}

	/**
	 * Sets the MapView for this MapMover.
	 * 
	 * @param mapView
	 *            the MapView.
	 */
	void setMapView(MapView mapView) {
		this.mapView = mapView;
	}

	/**
	 * Sets the parameters for the zoom animation.
	 * 
	 * @param zoomStart
	 *            the zoom factor at the begin of the animation.
	 * @param zoomEnd
	 *            the zoom factor at the end of the animation.
	 * @param pivotX
	 *            the x coordinate of the animation center.
	 * @param pivotY
	 *            the y coordinate of the animation center.
	 */
	void setParameters(float zoomStart, float zoomEnd, float pivotX, float pivotY) {
		this.zoomStart = zoomStart;
		this.zoomEnd = zoomEnd;
		this.pivotX = pivotX;
		this.pivotY = pivotY;
	}

	/**
	 * Starts a zoom animation with the current parameters.
	 */
	void startAnimation() {
		this.zoomDifference = this.zoomEnd - this.zoomStart;
		this.scaleFactorApplied = this.zoomStart;
		this.executeAnimation = true;
		this.timeStart = SystemClock.uptimeMillis();
		synchronized (this) {
			notify();
		}
	}
}