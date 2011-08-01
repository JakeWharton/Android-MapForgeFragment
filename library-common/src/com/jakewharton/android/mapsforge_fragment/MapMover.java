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
 * A MapMover moves the map horizontally and vertically at a constant speed. It runs in a separate
 * thread to avoid blocking the UI thread.
 */
class MapMover extends Thread {
	private static final int FRAME_LENGTH = 15;
	private static final float MOVE_SPEED = 0.2f;
	private static final String THREAD_NAME = "MapMover";

	private MapView mapView;
	private float moveX;
	private float moveY;
	private boolean pause;
	private boolean ready;
	private long timePrevious;

	@Override
	public void run() {
		setName(THREAD_NAME);

		long timeCurrent;
		long timeElapsed;

		while (!isInterrupted()) {
			synchronized (this) {
				while (!isInterrupted() && ((this.moveX == 0 && this.moveY == 0) || this.pause)) {
					try {
						this.ready = true;
						wait();
					} catch (InterruptedException e) {
						// restore the interrupted status
						interrupt();
					}
				}
				this.ready = false;
			}

			if (isInterrupted()) {
				break;
			}

			// calculate the time difference to previous call
			timeCurrent = SystemClock.uptimeMillis();
			timeElapsed = timeCurrent - this.timePrevious;
			this.timePrevious = timeCurrent;

			// add the movement to the transformation matrices
			this.mapView.matrixPostTranslate(timeElapsed * this.moveX, timeElapsed
					* this.moveY);
			synchronized (this.mapView.overlays) {
				for (Overlay overlay : this.mapView.overlays) {
					overlay.matrixPostTranslate(timeElapsed * this.moveX, timeElapsed
							* this.moveY);
				}
			}

			// move the map and the overlays
			this.mapView.moveMap(timeElapsed * this.moveX, timeElapsed * this.moveY);
			synchronized (this.mapView.overlays) {
				for (Overlay overlay : this.mapView.overlays) {
					overlay.requestRedraw();
				}
			}

			this.mapView.handleTiles();
			synchronized (this) {
				try {
					wait(FRAME_LENGTH);
				} catch (InterruptedException e) {
					// restore the interrupted status
					interrupt();
				}
			}
		}

		// set the pointer to null to avoid memory leaks
		this.mapView = null;
	}

	/**
	 * Returns the status of the MapMover.
	 * 
	 * @return true if the MapMover is not working, false otherwise.
	 */
	synchronized boolean isReady() {
		return this.ready;
	}

	/**
	 * Handles a "move down" event.
	 */
	void moveDown() {
		if (this.moveY > 0) {
			// stop moving the map vertically
			this.moveY = 0;
		} else if (this.moveY == 0) {
			// start moving the map
			this.moveY = -MOVE_SPEED * this.mapView.getMoveSpeed();
			this.timePrevious = SystemClock.uptimeMillis();
			synchronized (this) {
				notify();
			}
		}
	}

	/**
	 * Handles a "move left" event.
	 */
	void moveLeft() {
		if (this.moveX < 0) {
			// stop moving the map horizontally
			this.moveX = 0;
		} else if (this.moveX == 0) {
			// start moving the map
			this.moveX = MOVE_SPEED * this.mapView.getMoveSpeed();
			this.timePrevious = SystemClock.uptimeMillis();
			synchronized (this) {
				notify();
			}
		}
	}

	/**
	 * Handles a "move right" event.
	 */
	void moveRight() {
		if (this.moveX > 0) {
			// stop moving the map horizontally
			this.moveX = 0;
		} else if (this.moveX == 0) {
			// start moving the map
			this.moveX = -MOVE_SPEED * this.mapView.getMoveSpeed();
			this.timePrevious = SystemClock.uptimeMillis();
			synchronized (this) {
				notify();
			}
		}
	}

	/**
	 * Handles a "move up" event.
	 */
	void moveUp() {
		if (this.moveY < 0) {
			// stop moving the map vertically
			this.moveY = 0;
		} else if (this.moveY == 0) {
			// start moving the map
			this.moveY = MOVE_SPEED * this.mapView.getMoveSpeed();
			this.timePrevious = SystemClock.uptimeMillis();
			synchronized (this) {
				notify();
			}
		}
	}

	/**
	 * Requests that the MapMover should stop working.
	 */
	synchronized void pause() {
		this.pause = true;
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
	 * Stops moving the map horizontally.
	 */
	void stopHorizontalMove() {
		this.moveX = 0;
	}

	/**
	 * Stops moving the map in any direction.
	 */
	void stopMove() {
		this.moveX = 0;
		this.moveY = 0;
	}

	/**
	 * Stops moving the map vertically.
	 */
	void stopVerticalMove() {
		this.moveY = 0;
	}

	/**
	 * Requests that the MapMover should continue moving the map.
	 */
	synchronized void unpause() {
		this.pause = false;
		this.timePrevious = SystemClock.uptimeMillis();
		notify();
	}
}