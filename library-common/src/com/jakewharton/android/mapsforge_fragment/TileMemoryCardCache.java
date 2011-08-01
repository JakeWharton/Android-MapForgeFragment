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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;

/**
 * A thread-safe cache for image files with a fixed size and LRU policy.
 */
class TileMemoryCardCache {
	/**
	 * Filename extension for cached image files.
	 */
	private static final String IMAGE_FILE_NAME_EXTENSION = ".tile";

	/**
	 * Load factor of the internal HashMap.
	 */
	private static final float LOAD_FACTOR = 0.6f;

	/**
	 * Name of the file used for serialization of the cache map.
	 */
	private static final String SERIALIZATION_FILE_NAME = "cache.ser";

	private static Map<MapGeneratorJob, File> createMap(final int initialCapacity) {
		return new LinkedHashMap<MapGeneratorJob, File>(
				(int) (initialCapacity / LOAD_FACTOR) + 2, LOAD_FACTOR, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<MapGeneratorJob, File> eldest) {
				if (size() > initialCapacity) {
					// remove the entry from the cache and delete the cached file
					this.remove(eldest.getKey());
					if (!eldest.getValue().delete()) {
						eldest.getValue().deleteOnExit();
					}
				}
				return false;
			}
		};
	}

	private final ByteBuffer bitmapBuffer;
	private long cacheId;
	private int capacity;
	private Map<MapGeneratorJob, File> map;
	private File outputFile;
	private final File tempDir;

	/**
	 * Constructs an image file cache with a fixes size and LRU policy.
	 * 
	 * @param tempDir
	 *            the temporary directory to use for cached files.
	 * @param capacity
	 *            the maximum number of entries in the cache.
	 * @throws IllegalArgumentException
	 *             if the capacity is negative.
	 */
	TileMemoryCardCache(String tempDir, int capacity) {
		if (capacity < 0) {
			throw new IllegalArgumentException();
		}

		this.tempDir = new File(tempDir);
		// check if the cache directory exists
		if (!this.tempDir.exists()) {
			// check if the cache directory can be created
			if (this.tempDir.mkdirs()) {
				this.capacity = capacity;
			} else {
				this.capacity = 0;
			}
		} else if (!this.tempDir.isDirectory() || !this.tempDir.canRead()
				|| !this.tempDir.canWrite()) {
			this.capacity = 0;
		} else {
			this.capacity = capacity;
		}

		this.bitmapBuffer = ByteBuffer.allocate(Tile.TILE_SIZE_IN_BYTES);

		// restore the serialized cache map if possible
		if (!deserializeCacheMap()) {
			this.map = createMap(this.capacity);
		}
	}

	/**
	 * Deletes all cached files and the cache directory itself.
	 */
	private void deleteCachedFiles() {
		// delete all files in the cache map
		if (this.map != null) {
			for (File file : this.map.values()) {
				if (!file.delete()) {
					file.deleteOnExit();
				}
			}
			this.map.clear();
			this.map = null;
		}

		if (this.tempDir != null && this.tempDir.isDirectory()) {
			// create a filename filter that matches all cached image files
			FilenameFilter filenameFilter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(IMAGE_FILE_NAME_EXTENSION);
				}
			};

			// delete all other cached image files
			for (File file : this.tempDir.listFiles(filenameFilter)) {
				if (!file.delete()) {
					file.deleteOnExit();
				}
			}

			// delete the cache directory
			if (this.tempDir != null && !this.tempDir.delete()) {
				this.tempDir.deleteOnExit();
			}
		}
	}

	/**
	 * Restores the serialized cache map if possible.
	 * 
	 * @return true if the map was restored successfully, false otherwise.
	 */
	@SuppressWarnings("unchecked")
	private synchronized boolean deserializeCacheMap() {
		try {
			// check if the serialization file exists and is readable
			File file = new File(this.tempDir, SERIALIZATION_FILE_NAME);
			if (!file.exists()) {
				return false;
			} else if (!file.isFile()) {
				return false;
			} else if (!file.canRead()) {
				return false;
			}

			// create the input streams
			FileInputStream inputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

			// restore the serialized cache map (the compiler warning cannot be fixed)
			this.map = (Map<MapGeneratorJob, File>) objectInputStream.readObject();

			// close the input streams
			objectInputStream.close();
			inputStream.close();

			// delete the serialization file
			if (!file.delete()) {
				file.deleteOnExit();
			}

			return true;
		} catch (IOException e) {
			Logger.exception(e);
			return false;
		} catch (ClassNotFoundException e) {
			Logger.exception(e);
			return false;
		}
	}

	/**
	 * Serializes the cache map.
	 * 
	 * @return true if the map was serialized successfully, false otherwise.
	 */
	private synchronized boolean serializeCacheMap() {
		try {
			// check if the serialization file exists and is readable
			File file = new File(this.tempDir, SERIALIZATION_FILE_NAME);

			// try to delete the serialization file if it exists
			if (file.exists() && !file.delete()) {
				return false;
			}

			// check if the cache map exists
			if (this.map == null) {
				return false;
			}

			// create the output streams
			FileOutputStream outputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

			// serialize the cache map
			objectOutputStream.writeObject(this.map);

			// close the output streams
			objectOutputStream.close();
			outputStream.close();

			return true;
		} catch (IOException e) {
			Logger.exception(e);
			return false;
		}
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
	 * 
	 * @param persistence
	 *            true if the cached images should be kept, false otherwise.
	 */
	synchronized void destroy(boolean persistence) {
		if (persistence) {
			// delete all files only if serialization of the cache map fails
			if (!serializeCacheMap()) {
				deleteCachedFiles();
			}
			this.map = null;
		} else {
			deleteCachedFiles();
		}
	}

	/**
	 * @param mapGeneratorJob
	 *            key of the image whose data should be returned.
	 * @param buffer
	 *            the buffer in which the image data should be copied.
	 * @return true if the image data were copied successfully, false otherwise.
	 * @see Map#get(Object)
	 */
	boolean get(MapGeneratorJob mapGeneratorJob, ByteBuffer buffer) {
		try {
			File inputFile;
			synchronized (this) {
				inputFile = this.map.get(mapGeneratorJob);
			}
			FileInputStream fileInputStream = new FileInputStream(inputFile);
			if (fileInputStream.read(buffer.array()) == buffer.array().length) {
				// the complete bitmap has been read successfully
				buffer.rewind();
			}
			fileInputStream.close();
			return true;
		} catch (FileNotFoundException e) {
			synchronized (this) {
				this.map.remove(mapGeneratorJob);
			}
			return false;
		} catch (IOException e) {
			Logger.exception(e);
			return false;
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
			// write the image to a temporary file
			try {
				bitmap.copyPixelsToBuffer(this.bitmapBuffer);
				this.bitmapBuffer.rewind();
				this.outputFile = new File(this.tempDir, ++this.cacheId
						+ IMAGE_FILE_NAME_EXTENSION);
				// check for an existing file with that name
				while (this.outputFile.exists()) {
					// increment the cache ID to avoid overwriting the existing file
					this.outputFile = new File(this.tempDir, ++this.cacheId
							+ IMAGE_FILE_NAME_EXTENSION);
				}
				FileOutputStream fileOutputStream = new FileOutputStream(this.outputFile, false);
				fileOutputStream.write(this.bitmapBuffer.array(), 0, this.bitmapBuffer.array().length);
				fileOutputStream.close();
				synchronized (this) {
					this.map.put(mapGeneratorJob, this.outputFile);
				}
			} catch (IOException e) {
				Logger.exception(e);
			}
		}
	}

	/**
	 * Adjusts the capacity of the cache.
	 * 
	 * @param capacity
	 *            the new capacity of the cache.
	 */
	synchronized void setCapacity(int capacity) {
		this.capacity = capacity;
		// create a new map with the new capacity
		Map<MapGeneratorJob, File> newMap = createMap(this.capacity);

		// put all entries from the old map in the new one.
		for (Map.Entry<MapGeneratorJob, File> entry : this.map.entrySet()) {
			newMap.put(entry.getKey(), entry.getValue());
		}
		this.map = newMap;
	}
}