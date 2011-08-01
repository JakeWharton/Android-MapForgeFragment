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

import java.util.PriorityQueue;

import android.graphics.Bitmap;

/**
 * A MapGenerator provides map images. This abstract base class handles all thread specific actions and
 * provides the queue for jobs, which need to be processed and scheduled.
 */
abstract class MapGenerator extends Thread {
	private static final GeoPoint DEFAULT_START_POINT = new GeoPoint(51.33, 10.45);
	private static final byte DEFAULT_ZOOM_LEVEL = 5;

	private PriorityQueue<MapGeneratorJob> jobQueue1;
	private PriorityQueue<MapGeneratorJob> jobQueue2;
	private MapView mapView;
	private boolean pause;
	private boolean ready;
	private boolean requestMoreJobs;
	private boolean scheduleNeeded;
	private PriorityQueue<MapGeneratorJob> tempQueue;
	private TileMemoryCardCache tileMemoryCardCache;
	private TileRAMCache tileRAMCache;

	/**
	 * Abstract default constructor that must be called by subclasses.
	 */
	MapGenerator() {
		super();
		this.jobQueue1 = new PriorityQueue<MapGeneratorJob>(64);
		this.jobQueue2 = new PriorityQueue<MapGeneratorJob>(64);
	}

	@Override
	public final void run() {
		setName(getThreadName());
		// create the currentTileBitmap for the tile content
		Bitmap currentTileBitmap = Bitmap.createBitmap(Tile.TILE_SIZE, Tile.TILE_SIZE,
				Bitmap.Config.RGB_565);
		setupMapGenerator(currentTileBitmap);

		MapGeneratorJob currentMapGeneratorJob;
		while (!isInterrupted()) {
			prepareMapGeneration();

			synchronized (this) {
				while (!isInterrupted() && (this.jobQueue1.isEmpty() || this.pause)) {
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

			// get the next tile from the job queue that needs to be processed
			synchronized (this) {
				if (this.scheduleNeeded) {
					schedule();
					this.scheduleNeeded = false;
				}
				currentMapGeneratorJob = this.jobQueue1.poll();
			}

			// check if the current job can be skipped or must be processed
			if (!this.tileRAMCache.containsKey(currentMapGeneratorJob)
					&& !this.tileMemoryCardCache.containsKey(currentMapGeneratorJob)) {
				// check if the tile was generated successfully
				if (executeJob(currentMapGeneratorJob)) {
					if (isInterrupted()) {
						break;
					}

					if (this.mapView != null) {
						// copy the tile to the MapView
						this.mapView.putTileOnBitmap(currentMapGeneratorJob, currentTileBitmap);
						this.mapView.postInvalidate();
					}

					// put the tile image in the cache
					this.tileMemoryCardCache.put(currentMapGeneratorJob, currentTileBitmap);
				}
			}

			// if the job queue is empty, ask the MapView for more jobs
			if (!isInterrupted() && this.jobQueue1.isEmpty() && this.requestMoreJobs
					&& this.mapView != null) {
				this.mapView.requestMoreJobs();
			}
		}

		cleanup();

		// free the currentTileBitmap memory
		if (currentTileBitmap != null) {
			currentTileBitmap.recycle();
			currentTileBitmap = null;
		}

		// set some fields to null to avoid memory leaks
		this.mapView = null;
		this.tileRAMCache = null;
		this.tileMemoryCardCache = null;

		if (this.jobQueue1 != null) {
			this.jobQueue1.clear();
			this.jobQueue1 = null;
		}

		if (this.jobQueue2 != null) {
			this.jobQueue2.clear();
			this.jobQueue2 = null;
		}

		this.tempQueue = null;
	}

	/**
	 * Schedules all jobs in the queue.
	 */
	private void schedule() {
		if (this.mapView != null) {
			while (!this.jobQueue1.isEmpty()) {
				this.jobQueue2.offer(this.mapView.setJobPriority(this.jobQueue1.poll()));
			}
			// swap the two job queues
			this.tempQueue = this.jobQueue1;
			this.jobQueue1 = this.jobQueue2;
			this.jobQueue2 = this.tempQueue;
		}
	}

	/**
	 * Adds the given job to the queue. A call to this method has no effect if the given job is already
	 * in the queue.
	 * 
	 * @param mapGeneratorJob
	 *            the job to be added to the queue.
	 */
	final synchronized void addJob(MapGeneratorJob mapGeneratorJob) {
		if (!this.jobQueue1.contains(mapGeneratorJob)) {
			this.jobQueue1.offer(mapGeneratorJob);
		}
	}

	/**
	 * This method will by called at the end of the run method when the thread was interrupted. It can
	 * be used to clean up objects and to close any open connections.
	 */
	abstract void cleanup();

	/**
	 * Clears the job queue.
	 */
	final synchronized void clearJobs() {
		this.jobQueue1.clear();
	}

	/**
	 * This method will by called when a job needs to be executed.
	 * 
	 * @param mapGeneratorJob
	 *            the job that should be executed.
	 * @return true if the job was executed successfully, false otherwise.
	 */
	abstract boolean executeJob(MapGeneratorJob mapGeneratorJob);

	/**
	 * Returns the default starting point on the map. May be overridden by subclasses.
	 * 
	 * @return the default starting point.
	 */
	GeoPoint getDefaultStartPoint() {
		return DEFAULT_START_POINT;
	}

	/**
	 * Returns the default zoom level of the map. May be overridden by subclasses.
	 * 
	 * @return the default zoom level.
	 */
	byte getDefaultZoomLevel() {
		return DEFAULT_ZOOM_LEVEL;
	}

	/**
	 * Returns the maximum zoom level that the MapGenerator can handle.
	 * 
	 * @return the maximum zoom level.
	 */
	abstract byte getMaxZoomLevel();

	/**
	 * Returns the number of jobs that are already in the queue.
	 * 
	 * @return the number of jobs in the queue.
	 */
	final synchronized int getNumberOfJobs() {
		return this.jobQueue1.size();
	}

	/**
	 * Returns the name of the MapGenerator. It will be used as the name for the thread.
	 * 
	 * @return the name of the MapGenerator.
	 */
	abstract String getThreadName();

	/**
	 * Returns the status of the MapGenerator.
	 * 
	 * @return true if the MapGenerator is not working, false otherwise.
	 */
	final boolean isReady() {
		return this.ready;
	}

	/**
	 * This method is called each time the MapView gets attached to the window. May be overridden by
	 * subclasses to react on this event.
	 * <p>
	 * The default implementation of this method does nothing.
	 */
	void onAttachedToWindow() {
		// do nothing
	}

	/**
	 * This method is called each time the MapView gets detached from the window. May be overridden by
	 * subclasses to react on this event.
	 * <p>
	 * The default implementation of this method does nothing.
	 */
	void onDetachedFromWindow() {
		// do nothing
	}

	/**
	 * Request the MapGenerator to stop working.
	 */
	final synchronized void pause() {
		this.pause = true;
	}

	/**
	 * This method is called each time before a tile needs to be processed. It can be used to clear any
	 * data structures that will be needed when the next map tile will be processed.
	 */
	abstract void prepareMapGeneration();

	/**
	 * Request a scheduling of all jobs that are currently in the queue.
	 * 
	 * @param askForMoreJobs
	 *            true if the MapGenerator may ask for more jobs, false otherwise.
	 */
	final synchronized void requestSchedule(boolean askForMoreJobs) {
		this.scheduleNeeded = true;
		this.requestMoreJobs = askForMoreJobs;
		notify();
	}

	/**
	 * Sets the MapView for this MapGenerator.
	 * 
	 * @param mapView
	 *            the MapView.
	 */
	final void setMapView(MapView mapView) {
		this.mapView = mapView;
	}

	/**
	 * Sets the tile caches that the MapGenerator should use.
	 * 
	 * @param tileRAMCache
	 *            the TileRAMCache.
	 * @param tileMemoryCardCache
	 *            the TileMemoryCardCache.
	 */
	final void setTileCaches(TileRAMCache tileRAMCache, TileMemoryCardCache tileMemoryCardCache) {
		this.tileRAMCache = tileRAMCache;
		this.tileMemoryCardCache = tileMemoryCardCache;
	}

	/**
	 * This method is called only once before any map tile is requested. It can be used to set up data
	 * structures or connections that will be needed.
	 * 
	 * @param bitmap
	 *            the bitmap on which all future tiles need to be copied.
	 */
	abstract void setupMapGenerator(Bitmap bitmap);

	/**
	 * Request the MapGenerator to continue working.
	 */
	final synchronized void unpause() {
		this.pause = false;
		notify();
	}
}