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

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;

/**
 * A map renderer which uses a Canvas for drawing.
 * 
 * @see <a href="http://developer.android.com/reference/android/graphics/Canvas.html">Canvas</a>
 */
class CanvasRenderer extends DatabaseMapGenerator {
	private static final Paint PAINT_TILE_COORDINATES = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_TILE_COORDINATES_STROKE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_TILE_FRAME = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final String THREAD_NAME = "CanvasRenderer";
	private int arrayListIndex;
	private Paint bitmapFilterPaint;
	private Canvas canvas;
	private float[][] coordinates;
	private Path path;
	private StringBuilder stringBuilder;
	private Matrix symbolMatrix;
	private float[] tileFrame;

	@Override
	void drawNodes(List<PointTextContainer> drawNodes) {
		PointTextContainer pointTextContainer;
		for (this.arrayListIndex = drawNodes.size() - 1; this.arrayListIndex >= 0; --this.arrayListIndex) {
			pointTextContainer = drawNodes.get(this.arrayListIndex);
			if (pointTextContainer.paintBack != null) {
				this.canvas.drawText(pointTextContainer.text, pointTextContainer.x,
						pointTextContainer.y, pointTextContainer.paintBack);
			}
			this.canvas.drawText(pointTextContainer.text, pointTextContainer.x, pointTextContainer.y,
					pointTextContainer.paintFront);
		}
	}

	@Override
	void drawSymbols(List<SymbolContainer> drawSymbols) {
		SymbolContainer symbolContainer;
		for (this.arrayListIndex = drawSymbols.size() - 1; this.arrayListIndex >= 0; --this.arrayListIndex) {
			symbolContainer = drawSymbols.get(this.arrayListIndex);
			// use the matrix for rotation and translation of the symbol
			if (symbolContainer.alignCenter) {
				this.symbolMatrix
						.setRotate(symbolContainer.rotation, symbolContainer.symbol.getWidth() >> 1,
								symbolContainer.symbol.getHeight() >> 1);
				this.symbolMatrix.postTranslate(symbolContainer.x
						- (symbolContainer.symbol.getWidth() >> 1), symbolContainer.y
						- (symbolContainer.symbol.getHeight() >> 1));
			} else {
				this.symbolMatrix.setRotate(symbolContainer.rotation);
				this.symbolMatrix.postTranslate(symbolContainer.x, symbolContainer.y);
			}
			this.canvas.drawBitmap(symbolContainer.symbol, this.symbolMatrix, this.bitmapFilterPaint);
		}
	}

	@Override
	void drawTileCoordinates(Tile tile) {
		this.stringBuilder.setLength(0);
		this.stringBuilder.append("X: ");
		this.stringBuilder.append(tile.x);
		this.canvas.drawText(this.stringBuilder.toString(), 20, 30,
				PAINT_TILE_COORDINATES_STROKE);
		this.canvas.drawText(this.stringBuilder.toString(), 20, 30, PAINT_TILE_COORDINATES);

		this.stringBuilder.setLength(0);
		this.stringBuilder.append("Y: ");
		this.stringBuilder.append(tile.y);
		this.canvas.drawText(this.stringBuilder.toString(), 20, 60,
				PAINT_TILE_COORDINATES_STROKE);
		this.canvas.drawText(this.stringBuilder.toString(), 20, 60, PAINT_TILE_COORDINATES);

		this.stringBuilder.setLength(0);
		this.stringBuilder.append("Z: ");
		this.stringBuilder.append(tile.zoomLevel);
		this.canvas.drawText(this.stringBuilder.toString(), 20, 90,
				PAINT_TILE_COORDINATES_STROKE);
		this.canvas.drawText(this.stringBuilder.toString(), 20, 90, PAINT_TILE_COORDINATES);
	}

	@Override
	void drawTileFrame() {
		this.canvas.drawLines(this.tileFrame, PAINT_TILE_FRAME);
	}

	@Override
	void drawWayNames(List<WayTextContainer> drawWayNames) {
		WayTextContainer pathTextContainer;
		float[] textCoordinates;
		for (this.arrayListIndex = drawWayNames.size() - 1; this.arrayListIndex >= 0; --this.arrayListIndex) {
			pathTextContainer = drawWayNames.get(this.arrayListIndex);
			this.path.rewind();
			textCoordinates = pathTextContainer.coordinates;
			this.path.moveTo(textCoordinates[0], textCoordinates[1]);
			for (int i = 2; i < textCoordinates.length; i += 2) {
				this.path.lineTo(textCoordinates[i], textCoordinates[i + 1]);
			}
			this.canvas
					.drawTextOnPath(pathTextContainer.text, this.path, 0, 3, pathTextContainer.paint);
		}
	}

	@Override
	void drawWays(List<List<List<ShapePaintContainer>>> drawWays, byte layers,
			byte levelsPerLayer) {
		List<List<ShapePaintContainer>> shapePaintContainers;
		CircleContainer circleContainer;
		WayContainer complexWayContainer;
		ShapePaintContainer shapePaintContainer;
		List<ShapePaintContainer> wayList;
		for (byte currentLayer = 0; currentLayer < layers; ++currentLayer) {
			shapePaintContainers = drawWays.get(currentLayer);
			for (byte currentLevel = 0; currentLevel < levelsPerLayer; ++currentLevel) {
				wayList = shapePaintContainers.get(currentLevel);
				for (this.arrayListIndex = wayList.size() - 1; this.arrayListIndex >= 0; --this.arrayListIndex) {
					shapePaintContainer = wayList.get(this.arrayListIndex);
					this.path.rewind();
					switch (shapePaintContainer.shapeContainer.getShapeType()) {
						case CIRCLE:
							circleContainer = (CircleContainer) shapePaintContainer.shapeContainer;
							this.path.addCircle(circleContainer.x, circleContainer.y,
									circleContainer.radius, Path.Direction.CCW);
							break;
						case WAY:
							complexWayContainer = (WayContainer) shapePaintContainer.shapeContainer;
							this.coordinates = complexWayContainer.coordinates;
							for (int j = 0; j < this.coordinates.length; ++j) {
								// make sure that the coordinates sequence is not empty
								if (this.coordinates[j].length > 2) {
									this.path.moveTo(this.coordinates[j][0],
											this.coordinates[j][1]);
									for (int i = 2; i < this.coordinates[j].length; i += 2) {
										this.path.lineTo(this.coordinates[j][i],
												this.coordinates[j][i + 1]);
									}
								}
							}
							break;
					}
					this.canvas.drawPath(this.path, shapePaintContainer.paint);
				}
			}
		}
	}

	@Override
	void finishMapGeneration() {
		// do nothing
	}

	@Override
	String getThreadName() {
		return THREAD_NAME;
	}

	@Override
	void onAttachedToWindow() {
		// do nothing
	}

	@Override
	void onDetachedFromWindow() {
		// do nothing
	}

	@Override
	void setupRenderer(Bitmap bitmap) {
		this.canvas = new Canvas(bitmap);
		this.symbolMatrix = new Matrix();
		this.bitmapFilterPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
		this.tileFrame = new float[] { 0, 0, 0, Tile.TILE_SIZE, 0, Tile.TILE_SIZE,
				Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE,
				0 };
		this.path = new Path();
		this.path.setFillType(Path.FillType.EVEN_ODD);
		this.stringBuilder = new StringBuilder(16);
		PAINT_TILE_COORDINATES.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_TILE_COORDINATES.setTextSize(20);
		PAINT_TILE_COORDINATES_STROKE.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_TILE_COORDINATES_STROKE.setStyle(Paint.Style.STROKE);
		PAINT_TILE_COORDINATES_STROKE.setStrokeWidth(5);
		PAINT_TILE_COORDINATES_STROKE.setTextSize(20);
		PAINT_TILE_COORDINATES_STROKE.setColor(Color.WHITE);
	}
}