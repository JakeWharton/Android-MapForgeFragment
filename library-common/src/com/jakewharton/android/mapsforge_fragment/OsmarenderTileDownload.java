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
 * A MapGenerator that downloads tiles from the TilesAtHome server at OpenStreetMap.
 */
class OsmarenderTileDownload extends TileDownloadMapGenerator {
	private static final String SERVER_HOST_NAME = "tah.openstreetmap.org";
	private static final String THREAD_NAME = "OsmarenderTileDownload";
	private static final String URL_FIRST_PART = "http://" + SERVER_HOST_NAME + "/Tiles/tile/";
	private static final byte ZOOM_MAX = 17;

	@Override
	byte getMaxZoomLevel() {
		return ZOOM_MAX;
	}

	@Override
	String getServerHostName() {
		return SERVER_HOST_NAME;
	}

	@Override
	String getThreadName() {
		return THREAD_NAME;
	}

	@Override
	void getTilePath(Tile tile, StringBuilder imagePath) {
		imagePath.append(URL_FIRST_PART);
		imagePath.append(tile.zoomLevel);
		imagePath.append("/");
		imagePath.append(tile.x);
		imagePath.append("/");
		imagePath.append(tile.y);
		imagePath.append(".png");
	}
}