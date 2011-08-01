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

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;

/**
 * A thread-safe cache for bitmap images with a fixed size and LRU policy.
 */
class TileRAMCache {
	/**
	 * Load factor of the internal HashMap.
	 */
	private static final float LOAD_FACTOR = 0.6f;

	private final ByteBuffer bitmapBuffer;
	private final int capacity;
	private Map<MapGeneratorJob, Bitmap> map;
	private Bitmap tempBitmap;

	/**
	 * List of all Bitmaps which are used for object pooling.
	 */
	final List<Bitmap> bitmapPool;

	/**
	 * Constructs an image bitmap cache with a fixes size and LRU policy.
	 * 
	 * @param capacity
	 *            the maximum number of entries in the cache.
	 * @throws IllegalArgumentException
	 *             if the capacity is negative.
	 */
	TileRAMCache(int capacity) {
		if (capacity < 0) {
			throw new IllegalArgumentException();
		}
		this.capacity = capacity;
		this.map = createMap(this.capacity);
		this.bitmapPool = new LinkedList<Bitmap>();
		// one more bitmap than the cache capacity is needed for put operations
		for (int i = 0; i <= this.capacity; ++i) {
			this.bitmapPool.add(Bitmap.createBitmap(Tile.TILE_SIZE, Tile.TILE_SIZE,
					Bitmap.Config.RGB_565));
		}
		this.bitmapBuffer = ByteBuffer.allocate(Tile.TILE_SIZE_IN_BYTES);
	}

	private Map<MapGeneratorJob, Bitmap> createMap(final int initialCapacity) {
		return new LinkedHashMap<MapGeneratorJob, Bitmap>(
				(int) (initialCapacity / LOAD_FACTOR) + 2, LOAD_FACTOR, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<MapGeneratorJob, Bitmap> eldest) {
				if (size() > initialCapacity) {
					this.remove(eldest.getKey());
					TileRAMCache.this.bitmapPool.add(eldest.getValue());
				}
				return false;
			}
		};
	}

	/**
	 * @param mapGeneratorJob
	 *            key of the image whose presence in the cache should be tested.
	 * @return true if the cache contains an image for the specified key, false otherwise.
	 * @see Map#containsKey(Object)
	 */
	boolean containsKey(MapGeneratorJob mapGeneratorJob) {
		synchronized (this) {
			return this.map.containsKey(mapGeneratorJob);
		}
	}

	/**
	 * Destroy the cache at the end of its lifetime.
	 */
	void destroy() {
		synchronized (this) {
			if (this.map != null) {
				for (Bitmap bitmap : this.map.values()) {
					bitmap.recycle();
				}
				for (Bitmap bitmap : this.bitmapPool) {
					bitmap.recycle();
				}
				this.map.clear();
				this.map = null;
			}
		}
	}

	/**
	 * @param mapGeneratorJob
	 *            key of the image whose data should be returned.
	 * @return the data of the image.
	 * @see Map#get(Object)
	 */
	Bitmap get(MapGeneratorJob mapGeneratorJob) {
		synchronized (this) {
			return this.map.get(mapGeneratorJob);
		}
	}

	/**
	 * @param mapGeneratorJob
	 *            key of the image which should be added to the cache.
	 * @param bitmap
	 *            the data of the image that should be cached.
	 * @see Map#put(Object, Object)
	 */
	void put(MapGeneratorJob mapGeneratorJob, Bitmap bitmap) {
		if (this.capacity > 0) {
			bitmap.copyPixelsToBuffer(this.bitmapBuffer);
			this.bitmapBuffer.rewind();
			this.tempBitmap = this.bitmapPool.remove(0);
			this.tempBitmap.copyPixelsFromBuffer(this.bitmapBuffer);
			synchronized (this) {
				this.map.put(mapGeneratorJob, this.tempBitmap);
			}
		}
	}
}