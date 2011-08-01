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
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * A MapGenerator that downloads map tiles from a server. To build an implementation for a certain tile
 * server, extend this class and implement the abstract methods.
 */
abstract class TileDownloadMapGenerator extends MapGenerator {
	private Bitmap decodedBitmap;
	private int[] pixelColors;
	private final StringBuilder stringBuilder;
	private Bitmap tileBitmap;

	/**
	 * Default constructor that must be called by subclasses.
	 */
	TileDownloadMapGenerator() {
		super();
		this.stringBuilder = new StringBuilder(128);
	}

	@Override
	final void cleanup() {
		this.tileBitmap = null;
		if (this.decodedBitmap != null) {
			this.decodedBitmap.recycle();
			this.decodedBitmap = null;
		}
	}

	@Override
	final boolean executeJob(MapGeneratorJob mapGeneratorJob) {
		try {
			getTilePath(mapGeneratorJob.tile, this.stringBuilder);
			// read the data from the tile URL
			InputStream inputStream = new URL(this.stringBuilder.toString()).openStream();
			this.decodedBitmap = BitmapFactory.decodeStream(inputStream);
			inputStream.close();

			// check if the input stream could be decoded into a bitmap
			if (this.decodedBitmap == null) {
				return false;
			}

			// copy all pixels from the decoded bitmap to the color array
			this.decodedBitmap.getPixels(this.pixelColors, 0, Tile.TILE_SIZE, 0, 0,
					Tile.TILE_SIZE, Tile.TILE_SIZE);
			this.decodedBitmap.recycle();

			// copy all pixels from the color array to the tile bitmap
			if (this.tileBitmap != null) {
				this.tileBitmap.setPixels(this.pixelColors, 0, Tile.TILE_SIZE, 0, 0,
						Tile.TILE_SIZE, Tile.TILE_SIZE);
			}
			return true;
		} catch (UnknownHostException e) {
			Logger.debug(e.getMessage());
			return false;
		} catch (IOException e) {
			Logger.exception(e);
			return false;
		}
	}

	/**
	 * Returns the host name of the tile download server.
	 * 
	 * @return the server name.
	 */
	abstract String getServerHostName();

	/**
	 * Stores the absolute path to the requested tile in the given StringBuilder.
	 * 
	 * @param tile
	 *            the tile for which an image is required.
	 * @param imagePath
	 *            the StringBuilder to store the URL to the image.
	 */
	abstract void getTilePath(Tile tile, StringBuilder imagePath);

	@Override
	final void prepareMapGeneration() {
		this.stringBuilder.setLength(0);
	}

	@Override
	final void setupMapGenerator(Bitmap bitmap) {
		this.tileBitmap = bitmap;
		this.pixelColors = new int[Tile.TILE_SIZE * Tile.TILE_SIZE];
	}
}