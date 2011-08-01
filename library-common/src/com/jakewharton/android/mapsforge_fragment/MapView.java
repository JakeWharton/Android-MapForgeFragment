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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ZoomControls;

/**
 * A MapView shows a map on the display of the device. It handles all user input and touch gestures to
 * move and zoom the map. This MapView also comes with an integrated scale bar, which can be activated
 * via the {@link #setScaleBar(boolean)} method. The built-in zoom controls can be enabled with the
 * {@link #setBuiltInZoomControls(boolean)} method. The {@link #getController()} method returns a
 * <code>MapController</code> to programmatically modify the position and zoom level of the map.
 * <p>
 * This implementation supports offline map rendering as well as downloading map images (tiles) over an
 * Internet connection. All possible operation modes are listed in the {@link MapViewMode} enumeration.
 * The operation mode of a MapView can be set in the constructor and changed at runtime with the
 * {@link #setMapViewMode(MapViewMode)} method. Some MapView parameters like the maximum possible zoom
 * level or the default starting point depend on the selected operation mode.
 * <p>
 * In offline rendering mode a special database file is required which contains the map data. Such map
 * files can be stored in any readable folder. The current map file for a MapView is set by calling the
 * {@link #setMapFile(String)} method. To retrieve a <code>MapDatabase</code> that returns some metadata
 * about the map file, use the {@link #getMapDatabase()} method.
 * <p>
 * Map tiles are automatically cached in a separate directory on the memory card. The size of this cache
 * may be adjusted via the {@link #setMemoryCardCacheSize(int)} method. The
 * {@link MapView#setMemoryCardCachePersistence(boolean)} method sets the cache persistence.
 * <p>
 * {@link Overlay Overlays} can be used to display geographical data such as points and ways. To draw an
 * overlay on top of the map, add it to the list returned by {@link #getOverlays()}. Overlays may be
 * added or removed from the list at any time.
 * <p>
 * All text fields from the {@link TextField} enumeration can be overridden at runtime via the
 * {@link #setText(TextField, String)} method. The default texts are in English.
 */
public class MapView extends ViewGroup {
	/**
	 * Enumeration of all text fields that can be overridden at runtime via the
	 * {@link MapView#setText(TextField, String)} method.
	 */
	public enum TextField {
		/**
		 * Unit symbol kilometer.
		 */
		KILOMETER,

		/**
		 * Unit symbol meter.
		 */
		METER;
	}

	/**
	 * Implementation for multi-touch capable devices.
	 */
	private static class MultiTouchHandler extends TouchEventHandler {
		private static final int INVALID_POINTER_ID = -1;
		
		private final MapView mapView;
		private int action;
		private int activePointerId;
		private long multiTouchDownTime;
		private long multiTouchTime;
		private int pointerIndex;
		private final ScaleGestureDetector scaleGestureDetector;

		MultiTouchHandler(Activity activity, MapView mapView) {
			super(activity, mapView);
			this.mapView = mapView;
			this.activePointerId = INVALID_POINTER_ID;
			this.scaleGestureDetector = new ScaleGestureDetector(activity,
					new ScaleListener(mapView));
		}

		@Override
		int getAction(MotionEvent event) {
			return event.getAction() & MotionEvent.ACTION_MASK;
		}

		@Override
		boolean handleTouchEvent(MotionEvent event) {
			// round the event coordinates to integers
			event.setLocation((int) event.getX(), (int) event.getY());

			// workaround for a bug in the ScaleGestureDetector, see Android issue #12976
			if (event.getAction() != MotionEvent.ACTION_MOVE || event.getPointerCount() > 1) {
				// let the ScaleGestureDetector inspect the event
				this.scaleGestureDetector.onTouchEvent(event);
			}

			// extract the action from the action code
			this.action = getAction(event);

			if (this.action == MotionEvent.ACTION_DOWN) {
				this.longPressDetector.pressStart();
				this.previousPositionX = event.getX();
				this.previousPositionY = event.getY();
				this.mapMoved = false;
				// save the ID of the pointer
				this.activePointerId = event.getPointerId(0);
				return true;
			} else if (this.action == MotionEvent.ACTION_MOVE) {
				this.pointerIndex = event.findPointerIndex(this.activePointerId);

				if (this.scaleGestureDetector.isInProgress()) {
					return true;
				}

				// calculate the distance between previous and current position
				this.moveX = event.getX(this.pointerIndex) - this.previousPositionX;
				this.moveY = event.getY(this.pointerIndex) - this.previousPositionY;

				if (!this.mapMoved) {
					if (Math.abs(this.moveX) > this.mapMoveDelta
							|| Math.abs(this.moveY) > this.mapMoveDelta) {
						// the map movement delta has been reached
						this.longPressDetector.pressStop();
						this.mapMoved = true;
					} else {
						// do nothing
						return true;
					}
				}

				// save the position of the event
				this.previousPositionX = event.getX(this.pointerIndex);
				this.previousPositionY = event.getY(this.pointerIndex);

				this.mapView.matrixPostTranslate(this.moveX, this.moveY);
				this.mapView.moveMap(this.moveX, this.moveY);
				this.mapView.handleTiles();
				return true;
			} else if (this.action == MotionEvent.ACTION_UP) {
				this.longPressDetector.pressStop();
				this.pointerIndex = event.findPointerIndex(this.activePointerId);
				this.activePointerId = INVALID_POINTER_ID;
				if (this.mapMoved || this.longPressDetector.isEventHandled()) {
					this.previousEventTap = false;
				} else {
					if (this.previousEventTap) {
						// calculate the distance to the previous tap position
						this.tapDiffX = Math.abs(event.getX(this.pointerIndex)
								- this.previousTapX);
						this.tapDiffY = Math.abs(event.getY(this.pointerIndex)
								- this.previousTapY);
						this.tapDiffTime = event.getEventTime() - this.previousTapTime;

						// check if a double-tap event occurred
						if (this.tapDiffX < this.doubleTapDelta
								&& this.tapDiffY < this.doubleTapDelta
								&& this.tapDiffTime < this.doubleTapTimeout) {
							// double-tap event, zoom in
							this.previousEventTap = false;
							this.mapView.setCenter(this.mapView.getProjection().fromPixels((int) event.getX(),
									(int) event.getY()));
							this.mapView.zoom((byte) 1, 1);
							return true;
						}
					} else {
						this.previousEventTap = true;
					}

					// store the position and the time of this tap event
					this.previousTapX = event.getX(this.pointerIndex);
					this.previousTapY = event.getY(this.pointerIndex);
					this.previousTapTime = event.getEventTime();

					this.tapPoint = this.mapView.getProjection().fromPixels(
							(int) event.getX(this.pointerIndex),
							(int) event.getY(this.pointerIndex));
					synchronized (this.mapView.overlays) {
						for (Overlay overlay : this.mapView.overlays) {
							if (overlay.onTap(this.tapPoint, this.mapView)) {
								// the tap event has been handled
								break;
							}
						}
					}
				}
				return true;
			} else if (this.action == MotionEvent.ACTION_CANCEL) {
				this.longPressDetector.pressStop();
				this.activePointerId = INVALID_POINTER_ID;
				return true;
			} else if (this.action == MotionEvent.ACTION_POINTER_DOWN) {
				this.longPressDetector.pressStop();
				// save the time when the pointer has gone down
				this.multiTouchDownTime = event.getEventTime();
			} else if (this.action == MotionEvent.ACTION_POINTER_UP) {
				this.longPressDetector.pressStop();
				// extract the index of the pointer that left the touch sensor
				this.pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				if (event.getPointerId(this.pointerIndex) == this.activePointerId) {
					// the active pointer has gone up, choose a new one
					if (this.pointerIndex == 0) {
						this.pointerIndex = 1;
					} else {
						this.pointerIndex = 0;
					}
					// save the position of the event
					this.previousPositionX = event.getX(this.pointerIndex);
					this.previousPositionY = event.getY(this.pointerIndex);
					this.activePointerId = event.getPointerId(this.pointerIndex);
				}

				// calculate the time difference since the pointer has gone down
				this.multiTouchTime = event.getEventTime() - this.multiTouchDownTime;
				if (this.multiTouchTime < this.doubleTapTimeout) {
					// multi-touch tap event, zoom out
					this.previousEventTap = false;
					this.mapView.zoom((byte) -1, 1);
				}

				return true;
			}
			// the event was not handled
			return false;
		}
	}

	private static class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
		private final MapView mapView;
		private float focusX;
		private float focusY;
		private float scaleFactor;
		private float scaleFactorApplied;

		/**
		 * Empty constructor with default visibility to avoid a synthetic method.
		 */
		ScaleListener(MapView mapView) {
			this.mapView = mapView;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			this.scaleFactor = detector.getScaleFactor();
			this.scaleFactorApplied *= this.scaleFactor;
			this.mapView.matrixPostScale(this.scaleFactor, this.scaleFactor, this.focusX, this.focusY);
			this.mapView.invalidateOnUiThread();
			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			// reset the current scale factor
			this.scaleFactor = 1;
			this.scaleFactorApplied = this.scaleFactor;

			this.focusX = this.mapView.getWidth() >> 1;
			this.focusY = this.mapView.getHeight() >> 1;
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			// change the zoom level according to the scale gesture
			this.mapView.zoom((byte) Math.round(Math.log(this.scaleFactorApplied) / Math.log(2)),
					this.scaleFactorApplied);
		}
	}

	/**
	 * Implementation for single-touch capable devices.
	 */
	private static class SingleTouchHandler extends TouchEventHandler {
		private final MapView mapView;
		
		SingleTouchHandler(Activity activity, MapView mapView) {
			super(activity, mapView);
			this.mapView = mapView;
		}

		@Override
		int getAction(MotionEvent event) {
			return event.getAction();
		}

		@Override
		boolean handleTouchEvent(MotionEvent event) {
			// round the event coordinates to integers
			event.setLocation((int) event.getX(), (int) event.getY());

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				this.longPressDetector.pressStart();
				// save the position of the event
				this.previousPositionX = event.getX();
				this.previousPositionY = event.getY();
				this.mapMoved = false;
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				// calculate the distance between previous and current position
				this.moveX = event.getX() - this.previousPositionX;
				this.moveY = event.getY() - this.previousPositionY;

				if (!this.mapMoved) {
					if (Math.abs(this.moveX) > this.mapMoveDelta
							|| Math.abs(this.moveY) > this.mapMoveDelta) {
						// the map movement delta has been reached
						this.longPressDetector.pressStop();
						this.mapMoved = true;
					} else {
						// do nothing
						return true;
					}
				}

				// save the position of the event
				this.previousPositionX = event.getX();
				this.previousPositionY = event.getY();

				this.mapView.matrixPostTranslate(this.moveX, this.moveY);
				this.mapView.moveMap(this.moveX, this.moveY);
				this.mapView.handleTiles();
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				this.longPressDetector.pressStop();
				if (this.mapMoved || this.longPressDetector.isEventHandled()) {
					this.previousEventTap = false;
				} else {
					if (this.previousEventTap) {
						// calculate the distance to the previous tap position
						this.tapDiffX = Math.abs(event.getX() - this.previousTapX);
						this.tapDiffY = Math.abs(event.getY() - this.previousTapY);
						this.tapDiffTime = event.getEventTime() - this.previousTapTime;

						// check if a double-tap event occurred
						if (this.tapDiffX < this.doubleTapDelta
								&& this.tapDiffY < this.doubleTapDelta
								&& this.tapDiffTime < this.doubleTapTimeout) {
							// double-tap event
							this.previousEventTap = false;
							this.mapView.setCenter(this.mapView.getProjection().fromPixels((int) event.getX(),
									(int) event.getY()));
							this.mapView.zoom((byte) 1, 1);
							return true;
						}
					} else {
						this.previousEventTap = true;
					}

					// store the position and the time of this tap event
					this.previousTapX = event.getX();
					this.previousTapY = event.getY();
					this.previousTapTime = event.getEventTime();

					this.tapPoint = this.mapView.getProjection().fromPixels((int) event.getX(),
							(int) event.getY());
					synchronized (this.mapView.overlays) {
						for (Overlay overlay : this.mapView.overlays) {
							if (overlay.onTap(this.tapPoint, this.mapView)) {
								// the tap event has been handled
								break;
							}
						}
					}
				}
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
				this.longPressDetector.pressStop();
				return true;
			}
			// the event was not handled
			return false;
		}
	}

	/**
	 * Abstract base class for the single-touch and the multi-touch handler.
	 */
	private static abstract class TouchEventHandler {
		private class LongPressDetector extends Thread {
			private static final String THREAD_NAME = "LongPressDetector";

			private boolean eventHandled;
			private long pressStart;
			private long timeElapsed;

			LongPressDetector() {
				super();
			}

			@Override
			public void run() {
				setName(THREAD_NAME);

				while (!isInterrupted()) {
					synchronized (this) {
						while (!isInterrupted() && this.pressStart == 0) {
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

					synchronized (this) {
						// calculate the elapsed time since the press has started
						this.timeElapsed = SystemClock.uptimeMillis() - this.pressStart;
						while (!isInterrupted() && this.pressStart > 0
								&& this.timeElapsed < TouchEventHandler.this.longPressTimeout) {
							try {
								// wait for the remaining time of the whole timeout
								wait(TouchEventHandler.this.longPressTimeout - this.timeElapsed);
								this.timeElapsed = SystemClock.uptimeMillis() - this.pressStart;
							} catch (InterruptedException e) {
								// restore the interrupted status
								interrupt();
							}
						}
					}

					if (isInterrupted()) {
						break;
					}

					if (this.pressStart > 0) {
						this.eventHandled = forwardLongPressEvent();
						// stop even if a new long press event has already been started
						pressStop();
					}
				}
			}

			/**
			 * Returns whether a long press event has been handled.
			 * 
			 * @return true if a long press event has been handled, false otherwise.
			 */
			protected boolean isEventHandled() {
				return this.eventHandled;
			}

			/**
			 * Informs the LongTapDetector that a potential long press event has started.
			 */
			protected void pressStart() {
				if (this.pressStart == 0) {
					this.eventHandled = false;
					this.pressStart = SystemClock.uptimeMillis();
					synchronized (this) {
						notify();
					}
				}
			}

			/**
			 * Informs the LongTapDetector that no long press event is happening.
			 */
			protected void pressStop() {
				if (this.pressStart > 0) {
					this.pressStart = 0;
					synchronized (this) {
						notify();
					}
				}
			}
		}

		private final MapView mapView;
		
		/**
		 * Absolute threshold value for a double-tap event.
		 */
		final float doubleTapDelta;

		/**
		 * Maximum time difference in milliseconds for a double-tap event.
		 */
		final int doubleTapTimeout;

		/**
		 * Thread for detecting long press events.
		 */
		final LongPressDetector longPressDetector;

		/**
		 * Stores the coordinates of a long press event.
		 */
		GeoPoint longPressPoint;

		/**
		 * Duration in milliseconds for a long press event.
		 */
		final int longPressTimeout;

		/**
		 * Flag to indicate if the map has been moved.
		 */
		boolean mapMoved;

		/**
		 * Absolute threshold value of a motion event to be interpreted as a move.
		 */
		final float mapMoveDelta;

		/**
		 * Stores the horizontal length of a map move.
		 */
		float moveX;

		/**
		 * Stores the vertical length of a map move.
		 */
		float moveY;

		/**
		 * Flag to store if the previous event was a touch event.
		 */
		boolean previousEventTap;

		/**
		 * Stores the x coordinate of the previous touch event.
		 */
		float previousPositionX;

		/**
		 * Stores the y coordinate of the previous touch event.
		 */
		float previousPositionY;

		/**
		 * Stores the time of the previous tap event.
		 */
		long previousTapTime;

		/**
		 * Stores the X position of the previous tap event.
		 */
		float previousTapX;

		/**
		 * Stores the Y position of the previous tap event.
		 */
		float previousTapY;

		/**
		 * Stores the time difference between the previous and the current tap event.
		 */
		long tapDiffTime;

		/**
		 * Stores the X difference between the previous and the current tap event.
		 */
		float tapDiffX;

		/**
		 * Stores the Y difference between the previous and the current tap event.
		 */
		float tapDiffY;

		/**
		 * Stores the coordinates of a tap event.
		 */
		GeoPoint tapPoint;

		TouchEventHandler(Activity activity, MapView mapView) {
			this.mapView = mapView;
			ViewConfiguration viewConfiguration = ViewConfiguration.get(activity);
			this.mapMoveDelta = viewConfiguration.getScaledTouchSlop();
			this.doubleTapDelta = viewConfiguration.getScaledDoubleTapSlop();
			this.doubleTapTimeout = ViewConfiguration.getDoubleTapTimeout();
			this.longPressTimeout = ViewConfiguration.getLongPressTimeout();
			this.longPressDetector = new LongPressDetector();
			this.longPressDetector.start();
		}

		/**
		 * Forwards a long press event to all overlays until it has been handled.
		 * 
		 * @return true if the long press event has been handled, false otherwise.
		 */
		final boolean forwardLongPressEvent() {
			this.longPressPoint = this.mapView.getProjection().fromPixels((int) previousPositionX,
					(int) previousPositionY);
			if (this.longPressPoint != null) {
				synchronized (this.mapView.overlays) {
					for (Overlay overlay : this.mapView.overlays) {
						if (overlay.onLongPress(this.longPressPoint, this.mapView)) {
							// the long press event has been handled
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * Returns the action from the given event.
		 * 
		 * @param event
		 *            the event to extract the action from.
		 * @return the event, see {@link MotionEvent} for details.
		 */
		abstract int getAction(MotionEvent event);

		/**
		 * Handles a motion event on the touch screen.
		 * 
		 * @param event
		 *            the motion event.
		 * @return true if the event was handled, false otherwise.
		 */
		abstract boolean handleTouchEvent(MotionEvent event);
	}

	/**
	 * Default operation mode of a MapView if no other mode is specified.
	 */
	static final MapViewMode DEFAULT_MAP_VIEW_MODE = MapViewMode.CANVAS_RENDERER;

	/**
	 * Default move speed factor of the map, used for trackball and keyboard events.
	 */
	private static final int DEFAULT_MOVE_SPEED = 10;

	/**
	 * Default value for the kilometer text field.
	 */
	private static final String DEFAULT_TEXT_KILOMETER = " km";

	/**
	 * Default value for the meter text field.
	 */
	private static final String DEFAULT_TEXT_METER = " m";

	/**
	 * Default text scale for the map rendering.
	 */
	private static final float DEFAULT_TEXT_SCALE = 1;

	/**
	 * Default capacity of the memory card cache.
	 */
	private static final int DEFAULT_TILE_MEMORY_CARD_CACHE_SIZE = 100;

	/**
	 * Default minimum zoom level.
	 */
	private static final byte DEFAULT_ZOOM_LEVEL_MIN = 0;

	/**
	 * Names which are used to detect the Android emulator from the SDK.
	 */
	private static final String[] EMULATOR_NAMES = { "google_sdk", "sdk" };

	/**
	 * Path to the caching folder on the external storage.
	 */
	private static final String EXTERNAL_STORAGE_DIRECTORY = "/Android/data/org.mapsforge.android.maps/cache/";

	/**
	 * Default background color of the MapView.
	 */
	private static final int MAP_VIEW_BACKGROUND = Color.rgb(238, 238, 238);

	/**
	 * Message code for the handler to hide the zoom controls.
	 */
	private static final int MSG_ZOOM_CONTROLS_HIDE = 0;

	private static final Paint PAINT_SCALE_BAR = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_SCALE_BAR_STROKE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_SCALE_BAR_TEXT = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_SCALE_BAR_TEXT_WHITE_STROKE = new Paint(
			Paint.ANTI_ALIAS_FLAG);
	private static final int SCALE_BAR_HEIGHT = 35;
	private static final int[] SCALE_BAR_VALUES = { 10000000, 5000000, 2000000, 1000000,
			500000, 200000, 100000, 50000, 20000, 10000, 5000, 2000, 1000, 500, 200, 100, 50,
			20, 10, 5, 2, 1 };
	private static final int SCALE_BAR_WIDTH = 130;

	/**
	 * Capacity of the RAM cache.
	 */
	private static final int TILE_RAM_CACHE_SIZE = 16;

	/**
	 * Constant move speed factor for trackball events.
	 */
	private static final float TRACKBALL_MOVE_SPEED = 40;

	/**
	 * Delay in milliseconds after which the zoom controls disappear.
	 */
	private static final long ZOOM_CONTROLS_TIMEOUT = ViewConfiguration
			.getZoomControlsTimeout();

	/**
	 * Minimum possible zoom level.
	 */
	private static final byte ZOOM_LEVEL_MIN = 0;

	/**
	 * Maximum possible latitude value of the map.
	 */
	static final double LATITUDE_MAX = 85.05113;

	/**
	 * Minimum possible latitude value of the map.
	 */
	static final double LATITUDE_MIN = -85.05113;

	/**
	 * Maximum possible longitude value of the map.
	 */
	static final double LONGITUDE_MAX = 180;

	/**
	 * Minimum possible longitude value of the map.
	 */
	static final double LONGITUDE_MIN = -180;

	/**
	 * Returns the default operation mode of a MapView.
	 * 
	 * @return the default operation mode.
	 */
	public static MapViewMode getDefaultMapViewMode() {
		return DEFAULT_MAP_VIEW_MODE;
	}

	/**
	 * Returns the size of a single map tile in bytes.
	 * 
	 * @return the tile size.
	 */
	public static int getTileSizeInBytes() {
		return Tile.TILE_SIZE_IN_BYTES;
	}

	/**
	 * Detects if the code is currently executed on the emulator from the Android SDK. This method can
	 * be used for code branches to work around known bugs in the Android emulator.
	 * 
	 * @return true if the Android emulator has been detected, false otherwise.
	 */
	private static boolean isAndroidEmulator() {
		for (String name : EMULATOR_NAMES) {
			if (Build.PRODUCT.equals(name)) {
				// we have a match
				return true;
			}
		}
		return false;
	}

	private boolean attachedToWindow;
	private MapGeneratorJob currentJob;
	private Tile currentTile;
	private long currentTime;
	private boolean drawTileCoordinates;
	private boolean drawTileFrames;
	private int fps;
	private Paint fpsPaint;
	private int frameCounter;
	private boolean highlightWaterTiles;
	private double latitude;
	private double longitude;
	private final Activity activity;
	private MapController mapController;
	private MapDatabase mapDatabase;
	private String mapFile;
	private MapGenerator mapGenerator;
	private MapMover mapMover;
	private float mapMoveX;
	private float mapMoveY;
	private Bitmap mapScaleBitmap;
	private Canvas mapScaleCanvas;
	private double mapScalePreviousLatitude;
	private byte mapScalePreviousZoomLevel;
	private Bitmap mapViewBitmap1;
	private Bitmap mapViewBitmap2;
	private Canvas mapViewCanvas;
	private MapViewMode mapViewMode;
	private double mapViewPixelX;
	private double mapViewPixelY;
	private long mapViewTileX1;
	private long mapViewTileX2;
	private long mapViewTileY1;
	private long mapViewTileY2;
	private Matrix matrix;
	private float matrixScaleFactor;
	private float matrixTranslateX;
	private float matrixTranslateY;
	private float moveSpeedFactor;
	private int numberOfTiles;
	private boolean persistence;
	private long previousTime;
	private Projection projection;
	private boolean showFpsCounter;
	private boolean showScaleBar;
	private boolean showZoomControls;
	private String textKilometer;
	private String textMeter;
	private float textScale;
	private Bitmap tileBitmap;
	private ByteBuffer tileBuffer;
	private TileMemoryCardCache tileMemoryCardCache;
	private int tileMemoryCardCacheSize;
	private TileRAMCache tileRAMCache;
	private long tileX;
	private long tileY;
	private TouchEventHandler touchEventHandler;
	private ZoomAnimator zoomAnimator;
	private ZoomControls zoomControls;
	private Handler zoomControlsHideHandler;
	private byte zoomLevel;
	private byte zoomLevelMax;
	private byte zoomLevelMin;

	/**
	 * Thread-safe overlay list. It is necessary to manually synchronize on this list when iterating
	 * over it.
	 */
	List<Overlay> overlays;


	/**
	 * Constructs a new MapView with the given MapViewMode.
	 * 
	 * @param context
	 *            the enclosing MapActivity instance.
	 * @param mapViewMode
	 *            the mode in which the MapView should operate.
	 * @throws IllegalArgumentException
	 *             if the context object is not an instance of {@link MapActivity}.
	 */
	MapView(Context context, MapViewMode mapViewMode) {
		super(context);
		if (!(context instanceof Activity)) {
			throw new IllegalArgumentException("Context is not an instance of Activity");
		}
		this.activity = (Activity)context;
		this.mapViewMode = mapViewMode;
		setupMapView();
	}
	

	/**
	 * Returns the MapController for this MapView.
	 * 
	 * @return the MapController.
	 */
	public MapController getController() {
		return this.mapController;
	}

	/**
	 * Returns the current latitude span in microdegrees (degrees * 10^6).
	 * 
	 * @return the current latitude span from the top to the bottom of the map.
	 * @throws IllegalStateException
	 *             if the MapView dimensions are not valid (width and height > 0).
	 */
	public int getLatitudeSpan() {
		if (getWidth() > 0 && getWidth() > 0) {
			GeoPoint top = this.projection.fromPixels(0, 0);
			GeoPoint bottom = this.projection.fromPixels(0, this.getHeight());
			return Math.abs(top.getLatitudeE6() - bottom.getLatitudeE6());
		}
		throw new IllegalStateException("the MapView has no valid dimensions");
	}

	/**
	 * Returns the current longitude span in microdegrees (degrees * 10^6).
	 * 
	 * @return the current longitude span from the left to the right of the map.
	 * @throws IllegalStateException
	 *             if the MapView dimensions are not valid (width and height > 0).
	 */
	public int getLongitudeSpan() {
		if (getWidth() > 0 && getWidth() > 0) {
			GeoPoint left = this.projection.fromPixels(0, 0);
			GeoPoint right = this.projection.fromPixels(getWidth(), 0);
			return Math.abs(left.getLongitudeE6() - right.getLongitudeE6());
		}
		throw new IllegalStateException("the MapView has no valid dimensions");
	}

	/**
	 * Returns the current center of the map as a GeoPoint.
	 * 
	 * @return the current center of the map.
	 */
	public synchronized GeoPoint getMapCenter() {
		return new GeoPoint(this.latitude, this.longitude);
	}

	/**
	 * Returns the database which is currently used for reading the map file.
	 * 
	 * @return the map database.
	 * @throws UnsupportedOperationException
	 *             if the current MapView mode works with an Internet connection.
	 */
	public MapDatabase getMapDatabase() {
		if (this.mapViewMode.requiresInternetConnection()) {
			throw new UnsupportedOperationException();
		}
		return this.mapDatabase;
	}

	/**
	 * Returns the currently used map file.
	 * 
	 * @return the map file.
	 * @throws UnsupportedOperationException
	 *             if the current MapView mode works with an Internet connection.
	 */
	public String getMapFile() {
		if (this.mapViewMode.requiresInternetConnection()) {
			throw new UnsupportedOperationException();
		}
		return this.mapFile;
	}

	/**
	 * Returns the host name of the tile download server.
	 * 
	 * @return the server name.
	 * @throws UnsupportedOperationException
	 *             if the current MapView mode works with an Internet connection.
	 */
	public String getMapTileDownloadServer() {
		if (!this.mapViewMode.requiresInternetConnection()) {
			throw new UnsupportedOperationException();
		}
		return ((TileDownloadMapGenerator) this.mapGenerator).getServerHostName();
	}

	/**
	 * Returns the current operation mode of the MapView.
	 * 
	 * @return the mode of the MapView.
	 */
	public MapViewMode getMapViewMode() {
		return this.mapViewMode;
	}

	/**
	 * Returns the maximum zoom level which is supported by the currently selected {@link MapViewMode}
	 * of the MapView.
	 * 
	 * @return the maximum possible zoom level.
	 */
	public int getMaxZoomLevel() {
		return this.mapGenerator.getMaxZoomLevel();
	}

	/**
	 * Returns the move speed of the map, used for trackball and keyboard events.
	 * 
	 * @return the factor by which the move speed of the map will be multiplied.
	 */
	public float getMoveSpeed() {
		return this.moveSpeedFactor;
	}

	/**
	 * Returns the thread-safe list of overlays for this MapView. It is necessary to manually
	 * synchronize on this list when iterating over it.
	 * 
	 * @return the overlay list.
	 */
	public final List<Overlay> getOverlays() {
		return this.overlays;
	}

	/**
	 * Returns the projection that is currently in use to convert pixel coordinates to geographical
	 * coordinates on the map.
	 * 
	 * @return The projection of the MapView. Do not keep this object for a longer time.
	 */
	public Projection getProjection() {
		return this.projection;
	}

	/**
	 * Returns the current zoom level of the map.
	 * 
	 * @return the current zoom level.
	 */
	public byte getZoomLevel() {
		return this.zoomLevel;
	}

	/**
	 * Checks for a valid current map file.
	 * 
	 * @return true if the MapView currently has a valid map file, false otherwise.
	 * @throws UnsupportedOperationException
	 *             if the current MapView mode works with an Internet connection.
	 */
	public boolean hasValidMapFile() {
		if (this.mapViewMode.requiresInternetConnection()) {
			throw new UnsupportedOperationException();
		}
		return this.mapFile != null;
	}

	/**
	 * Makes a screenshot of the currently visible map and saves it as compressed image. Zoom buttons,
	 * scale bar, overlays, menus and the title bar are not included in the screenshot.
	 * 
	 * @param fileName
	 *            the name of the image file. If the file exists, it will be overwritten.
	 * @param format
	 *            the file format of the compressed image.
	 * @param quality
	 *            value from 0 (low) to 100 (high). Has no effect on some formats like PNG.
	 * @return true if the image was saved successfully, false otherwise.
	 * @throws IOException
	 *             if an error occurs while writing the file.
	 */
	public boolean makeScreenshot(CompressFormat format, int quality, String fileName)
			throws IOException {
		FileOutputStream outputStream = new FileOutputStream(fileName);
		boolean success;
		synchronized (this.matrix) {
			success = this.mapViewBitmap1.compress(format, quality, outputStream);
		}
		outputStream.close();
		return success;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (!isClickable()) {
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			this.mapMover.moveLeft();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			this.mapMover.moveRight();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			this.mapMover.moveUp();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			this.mapMover.moveDown();
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (!isClickable()) {
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			this.mapMover.stopHorizontalMove();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			this.mapMover.stopVerticalMove();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// show and hide the zoom controls
		switch (this.touchEventHandler.getAction(event)) {
			case MotionEvent.ACTION_DOWN:
				showZoomControls();
				break;
			case MotionEvent.ACTION_CANCEL:
				hideZoomControlsDelayed();
				break;
			case MotionEvent.ACTION_UP:
				hideZoomControlsDelayed();
				break;
			default:
				// do nothing
				break;
		}
		if (!isClickable()) {
			return true;
		}
		return this.touchEventHandler.handleTouchEvent(event);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		if (!isClickable()) {
			return false;
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			// calculate the map move
			this.mapMoveX = event.getX() * (TRACKBALL_MOVE_SPEED * this.moveSpeedFactor);
			this.mapMoveY = event.getY() * (TRACKBALL_MOVE_SPEED * this.moveSpeedFactor);

			matrixPostTranslate(this.mapMoveX, this.mapMoveY);
			moveMap(this.mapMoveX, this.mapMoveY);
			handleTiles();
			return true;
		}
		// the event was not handled
		return false;
	}

	/**
	 * Sets the visibility of the zoom controls.
	 * 
	 * @param showZoomControls
	 *            true if the zoom controls should be visible, false otherwise.
	 */
	public void setBuiltInZoomControls(boolean showZoomControls) {
		this.showZoomControls = showZoomControls;
	}

	/**
	 * Sets the visibility of the frame rate.
	 * <p>
	 * This method is for debugging purposes only.
	 * 
	 * @param showFpsCounter
	 *            true if the map frame rate should be visible, false otherwise.
	 */
	public void setFpsCounter(boolean showFpsCounter) {
		this.showFpsCounter = showFpsCounter;
		invalidateOnUiThread();
	}

	/**
	 * Sets the map file for this MapView.
	 * 
	 * @param newMapFile
	 *            the path to the new map file.
	 * @throws UnsupportedOperationException
	 *             if the current MapView mode works with an Internet connection.
	 */
	public void setMapFile(String newMapFile) {
		if (this.mapViewMode.requiresInternetConnection()) {
			throw new UnsupportedOperationException();
		}
		if (newMapFile == null) {
			// no map file is given
			return;
		} else if (this.mapFile != null && this.mapFile.equals(newMapFile)) {
			// same map file as before
			return;
		} else if (this.mapDatabase == null) {
			// no database exists
			return;
		}

		this.mapMover.pause();
		this.mapGenerator.pause();

		waitForZoomAnimator();
		waitForMapMover();
		waitForMapGenerator();

		this.mapMover.stopMove();
		this.mapGenerator.clearJobs();

		this.mapMover.unpause();
		this.mapGenerator.unpause();

		this.mapDatabase.closeFile();
		if (this.mapDatabase.openFile(newMapFile)) {
			this.mapDatabase.prepareExecution();
			((DatabaseMapGenerator) this.mapGenerator).onMapFileChange();
			this.mapFile = newMapFile;
			clearMapView();
			setCenter(getDefaultStartPoint());
			handleTiles();
		} else {
			this.mapFile = null;
			clearMapView();
			invalidateOnUiThread();
		}
	}

	/**
	 * Sets a new operation mode for the MapView.
	 * 
	 * @param newMapViewMode
	 *            the new mode.
	 */
	public void setMapViewMode(MapViewMode newMapViewMode) {
		// check if the new mode differs from the old one
		if (this.mapViewMode != newMapViewMode) {
			stopMapGeneratorThread();
			this.mapViewMode = newMapViewMode;
			startMapGeneratorThread();
			clearMapView();
			handleTiles();
		}
	}

	/**
	 * Sets the persistence of the memory card cache. If set to true, cached image files will not be
	 * deleted when the MapView gets destroyed. The default value is false.
	 * 
	 * @param persistence
	 *            the new persistence of the memory card cache.
	 */
	public void setMemoryCardCachePersistence(boolean persistence) {
		this.persistence = persistence;
	}

	/**
	 * Sets the new size of the memory card cache. If the cache already contains more items than the new
	 * capacity allows, items are discarded based on the cache policy.
	 * 
	 * @param newCacheSize
	 *            the new capacity of the memory card cache.
	 * @throws IllegalArgumentException
	 *             if the new capacity is negative.
	 */
	public void setMemoryCardCacheSize(int newCacheSize) {
		if (newCacheSize < 0) {
			throw new IllegalArgumentException();
		}
		this.tileMemoryCardCacheSize = newCacheSize;
		this.tileMemoryCardCache.setCapacity(this.tileMemoryCardCacheSize);
	}

	/**
	 * Sets the move speed of the map, used for trackball and keyboard events.
	 * 
	 * @param moveSpeedFactor
	 *            the factor by which the move speed of the map will be multiplied.
	 * @throws IllegalArgumentException
	 *             if the new moveSpeedFactor is negative.
	 */
	public void setMoveSpeed(float moveSpeedFactor) {
		if (moveSpeedFactor < 0) {
			throw new IllegalArgumentException();
		}
		this.moveSpeedFactor = moveSpeedFactor;
	}

	/**
	 * Sets the visibility of the scale bar.
	 * 
	 * @param showScaleBar
	 *            true if the scale bar should be visible, false otherwise.
	 */
	public void setScaleBar(boolean showScaleBar) {
		this.showScaleBar = showScaleBar;
		if (showScaleBar) {
			renderScaleBar();
		}
		invalidateOnUiThread();
	}

	/**
	 * Overrides the specified text field with the given string.
	 * 
	 * @param textField
	 *            the text field to override.
	 * @param value
	 *            the new value of the text field.
	 */
	public void setText(TextField textField, String value) {
		switch (textField) {
			case KILOMETER:
				this.textKilometer = value;
				break;
			case METER:
				this.textMeter = value;
				break;
		}
	}

	/**
	 * Sets the text scale for the map rendering. Has no effect in downloading mode.
	 * 
	 * @param textScale
	 *            the new text scale for the map rendering.
	 */
	public void setTextScale(float textScale) {
		this.textScale = textScale;
		this.mapGenerator.clearJobs();
		clearMapView();
		handleTiles();
	}

	/**
	 * Sets the drawing of tile coordinates for debugging. Has no effect in downloading mode.
	 * <p>
	 * This method is for debugging purposes only.
	 * 
	 * @param drawTileCoordinates
	 *            true if tile coordinates should be drawn, false otherwise.
	 */
	public void setTileCoordinates(boolean drawTileCoordinates) {
		this.drawTileCoordinates = drawTileCoordinates;
		this.mapGenerator.clearJobs();
		clearMapView();
		handleTiles();
	}

	/**
	 * Sets the drawing of tile frames for debugging. Has no effect in downloading mode.
	 * <p>
	 * This method is for debugging purposes only.
	 * 
	 * @param drawTileFrames
	 *            true if tile frames should be drawn, false otherwise.
	 */
	public void setTileFrames(boolean drawTileFrames) {
		this.drawTileFrames = drawTileFrames;
		this.mapGenerator.clearJobs();
		clearMapView();
		handleTiles();
	}

	/**
	 * Sets the highlighting of water tiles. Has no effect in downloading mode.
	 * <p>
	 * This method is for debugging purposes only.
	 * 
	 * @param highlightWaterTiles
	 *            true if water tiles should be highlighted, false otherwise.
	 */
	public void setWaterTiles(boolean highlightWaterTiles) {
		this.highlightWaterTiles = highlightWaterTiles;
		this.mapGenerator.clearJobs();
		clearMapView();
		handleTiles();
	}

	/**
	 * Sets the maximum zoom level of the map to which the user may zoom in.
	 * <p>
	 * The maximum possible zoom level of the MapView depends also on the currently selected
	 * {@link MapViewMode}. For example, downloading map tiles may only be possible up to a certain zoom
	 * level. Setting a higher maximum zoom level has no effect in this case.
	 * 
	 * @param zoomLevelMax
	 *            the maximum zoom level.
	 * @throws IllegalArgumentException
	 *             if the maximum zoom level is smaller than the current minimum zoom level.
	 */
	public void setZoomMax(byte zoomLevelMax) {
		if (zoomLevelMax < this.zoomLevelMin) {
			throw new IllegalArgumentException();
		}
		this.zoomLevelMax = zoomLevelMax;
	}

	/**
	 * Sets the minimum zoom level of the map to which the user may zoom out.
	 * 
	 * @param zoomLevelMin
	 *            the minimum zoom level.
	 * @throws IllegalArgumentException
	 *             if the minimum zoom level is larger than the current maximum zoom level.
	 */
	public void setZoomMin(byte zoomLevelMin) {
		if (zoomLevelMin > this.zoomLevelMax) {
			throw new IllegalArgumentException();
		}
		this.zoomLevelMin = (byte) Math.max(zoomLevelMin, ZOOM_LEVEL_MIN);
	}

	private synchronized void clearMapView() {
		// clear the MapView bitmaps
		if (this.mapViewBitmap1 != null) {
			this.mapViewBitmap1.eraseColor(MAP_VIEW_BACKGROUND);
		}
		if (this.mapViewBitmap2 != null) {
			this.mapViewBitmap2.eraseColor(MAP_VIEW_BACKGROUND);
		}
	}

	/**
	 * Returns the minimum of the maximum zoom level set via {@link #setZoomMax(byte)} and the maximum
	 * zoom level which is supported by the currently selected {@link MapViewMode}.
	 * 
	 * @return the maximum possible zoom level.
	 */
	private byte getMaximumPossibleZoomLevel() {
		return (byte) Math.min(this.zoomLevelMax, this.mapGenerator.getMaxZoomLevel());
	}

	/**
	 * Returns the given zoom level limited to the minimum and maximum possible zoom level.
	 * 
	 * @param zoom
	 *            the zoom level which should be limited.
	 * @return a valid zoom level from the interval [minimum, maximum].
	 */
	private byte getValidZoomLevel(byte zoom) {
		if (zoom < this.zoomLevelMin) {
			return this.zoomLevelMin;
		} else if (zoom > getMaximumPossibleZoomLevel()) {
			return getMaximumPossibleZoomLevel();
		}
		return zoom;
	}

	/**
	 * Displays the zoom controls for a short time.
	 */
	private void hideZoomControlsDelayed() {
		if (this.showZoomControls) {
			this.zoomControlsHideHandler.removeMessages(MSG_ZOOM_CONTROLS_HIDE);
			if (this.zoomControls.getVisibility() != VISIBLE) {
				this.zoomControls.show();
			}
			this.zoomControlsHideHandler.sendEmptyMessageDelayed(MSG_ZOOM_CONTROLS_HIDE,
					ZOOM_CONTROLS_TIMEOUT);
		}
	}

	private void renderScaleBar() {
		double meterPerPixel;
		synchronized (this) {
			// check if recalculating and drawing of the map scale is necessary
			if (this.zoomLevel == this.mapScalePreviousZoomLevel
					&& Math.abs(this.latitude - this.mapScalePreviousLatitude) < 0.2) {
				// no need to refresh the map scale
				return;
			}

			// save the current zoom level and latitude
			this.mapScalePreviousZoomLevel = this.zoomLevel;
			this.mapScalePreviousLatitude = this.latitude;

			// calculate an even value for the map scale
			meterPerPixel = MercatorProjection.calculateGroundResolution(this.latitude,
					this.zoomLevel);
		}

		float mapScaleLength = 0;
		int mapScale = 0;
		for (int i = 0; i < SCALE_BAR_VALUES.length; ++i) {
			mapScale = SCALE_BAR_VALUES[i];
			mapScaleLength = mapScale / (float) meterPerPixel;
			if (mapScaleLength < (SCALE_BAR_WIDTH - 10)) {
				break;
			}
		}

		// fill the bitmap with transparent color
		this.mapScaleBitmap.eraseColor(Color.TRANSPARENT);

		// draw the map scale
		this.mapScaleCanvas.drawLine(7, 20, mapScaleLength + 3, 20, PAINT_SCALE_BAR_STROKE);
		this.mapScaleCanvas.drawLine(5, 10, 5, 30, PAINT_SCALE_BAR_STROKE);
		this.mapScaleCanvas.drawLine(mapScaleLength + 5, 10, mapScaleLength + 5, 30,
				PAINT_SCALE_BAR_STROKE);
		this.mapScaleCanvas.drawLine(7, 20, mapScaleLength + 3, 20, PAINT_SCALE_BAR);
		this.mapScaleCanvas.drawLine(5, 10, 5, 30, PAINT_SCALE_BAR);
		this.mapScaleCanvas.drawLine(mapScaleLength + 5, 10, mapScaleLength + 5, 30, PAINT_SCALE_BAR);

		// draw the scale text
		if (mapScale < 1000) {
			this.mapScaleCanvas.drawText(mapScale + getText(TextField.METER), 10, 15,
					PAINT_SCALE_BAR_TEXT_WHITE_STROKE);
			this.mapScaleCanvas.drawText(mapScale + getText(TextField.METER), 10, 15,
					PAINT_SCALE_BAR_TEXT);
		} else {
			this.mapScaleCanvas.drawText((mapScale / 1000) + getText(TextField.KILOMETER),
					10, 15, PAINT_SCALE_BAR_TEXT_WHITE_STROKE);
			this.mapScaleCanvas.drawText((mapScale / 1000) + getText(TextField.KILOMETER),
					10, 15, PAINT_SCALE_BAR_TEXT);
		}
	}

	private void setupFpsText() {
		// create the paint1 for drawing the FPS text
		this.fpsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.fpsPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		this.fpsPaint.setTextSize(20);
	}

	private void setupMapScale() {
		// create the bitmap for the map scale and the canvas to draw on it
		this.mapScaleBitmap = Bitmap.createBitmap(SCALE_BAR_WIDTH, SCALE_BAR_HEIGHT,
				Bitmap.Config.ARGB_4444);
		this.mapScaleCanvas = new Canvas(this.mapScaleBitmap);

		// set the default text fields for the map scale
		setText(TextField.KILOMETER, DEFAULT_TEXT_KILOMETER);
		setText(TextField.METER, DEFAULT_TEXT_METER);

		// set up the paints to draw the map scale
		PAINT_SCALE_BAR.setStrokeWidth(2);
		PAINT_SCALE_BAR.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_SCALE_BAR.setColor(Color.BLACK);
		PAINT_SCALE_BAR_STROKE.setStrokeWidth(5);
		PAINT_SCALE_BAR_STROKE.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_SCALE_BAR_STROKE.setColor(Color.WHITE);

		PAINT_SCALE_BAR_TEXT.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_SCALE_BAR_TEXT.setTextSize(14);
		PAINT_SCALE_BAR_TEXT.setColor(Color.BLACK);
		PAINT_SCALE_BAR_TEXT_WHITE_STROKE.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_SCALE_BAR_TEXT_WHITE_STROKE.setStyle(Paint.Style.STROKE);
		PAINT_SCALE_BAR_TEXT_WHITE_STROKE.setStrokeWidth(3);
		PAINT_SCALE_BAR_TEXT_WHITE_STROKE.setTextSize(14);
		PAINT_SCALE_BAR_TEXT_WHITE_STROKE.setColor(Color.WHITE);
	}

	private synchronized void setupMapView() {
		// set up the TouchEventHandler depending on the Android version
		if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
			this.touchEventHandler = new SingleTouchHandler(getActivity(), this);
		} else {
			this.touchEventHandler = new MultiTouchHandler(getActivity(), this);
		}

		if (isAndroidEmulator()) {
			// disable the memory card cache to avoid emulator freezes
			this.tileMemoryCardCacheSize = 0;
		} else {
			this.tileMemoryCardCacheSize = DEFAULT_TILE_MEMORY_CARD_CACHE_SIZE;
		}

		this.moveSpeedFactor = DEFAULT_MOVE_SPEED;
		this.textScale = DEFAULT_TEXT_SCALE;

		setBackgroundColor(MAP_VIEW_BACKGROUND);
		setWillNotDraw(false);
		setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

		setupZoomControls();
		setupMapScale();
		setupFpsText();

		// create the projection
		this.projection = new MercatorProjection(this);

		// create the transformation matrix
		this.matrix = new Matrix();

		// create the thread-safe overlay list
		this.overlays = Collections.synchronizedList(new ArrayList<Overlay>(4) {
			private static final long serialVersionUID = 1L;

			@Override
			public void add(int index, Overlay overlay) {
				if (!overlay.isAlive()) {
					overlay.start();
				}
				overlay.setupOverlay(MapView.this);
				super.add(index, overlay);
			}

			@Override
			public boolean add(Overlay overlay) {
				if (!overlay.isAlive()) {
					overlay.start();
				}
				overlay.setupOverlay(MapView.this);
				return super.add(overlay);
			}

			@Override
			public boolean addAll(Collection<? extends Overlay> collection) {
				for (Overlay overlay : collection) {
					if (!overlay.isAlive()) {
						overlay.start();
					}
					overlay.setupOverlay(MapView.this);
				}
				return super.addAll(collection);
			}

			@Override
			public boolean addAll(int index, Collection<? extends Overlay> collection) {
				for (Overlay overlay : collection) {
					if (!overlay.isAlive()) {
						overlay.start();
					}
					overlay.setupOverlay(MapView.this);
				}
				return super.addAll(index, collection);
			}

			@Override
			public void clear() {
				for (int i = size() - 1; i >= 0; --i) {
					get(i).interrupt();
				}
				super.clear();
				invalidateOnUiThread();
			}

			@Override
			public Overlay remove(int index) {
				Overlay removedElement = super.remove(index);
				removedElement.interrupt();
				invalidateOnUiThread();
				return removedElement;
			}

			@Override
			public boolean remove(Object object) {
				boolean listChanged = super.remove(object);
				if (object instanceof Overlay) {
					((Overlay) object).interrupt();
				}
				invalidateOnUiThread();
				return listChanged;
			}

			@Override
			public boolean removeAll(Collection<?> collection) {
				boolean listChanged = super.removeAll(collection);
				for (Object object : collection) {
					if (object instanceof Overlay) {
						((Overlay) object).interrupt();
					}
				}
				invalidateOnUiThread();
				return listChanged;
			}

			@Override
			public Overlay set(int index, Overlay overlay) {
				if (!overlay.isAlive()) {
					overlay.start();
				}
				overlay.setupOverlay(MapView.this);
				Overlay previousElement = super.set(index, overlay);
				previousElement.interrupt();
				invalidateOnUiThread();
				return previousElement;
			}
		});

		// create the tile bitmap and buffer
		this.tileBitmap = Bitmap.createBitmap(Tile.TILE_SIZE, Tile.TILE_SIZE,
				Bitmap.Config.RGB_565);
		this.tileBuffer = ByteBuffer.allocate(Tile.TILE_SIZE_IN_BYTES);

		// create the image bitmap cache
		this.tileRAMCache = new TileRAMCache(TILE_RAM_CACHE_SIZE);

		// create the image file cache with a unique directory
		this.tileMemoryCardCache = new TileMemoryCardCache(Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ EXTERNAL_STORAGE_DIRECTORY + UUID.randomUUID().toString(), this.tileMemoryCardCacheSize);

		// create the MapController for this MapView
		this.mapController = new MapController(this);

		// create the database
		this.mapDatabase = new MapDatabase();

		startMapGeneratorThread();

		// set the default position and zoom level of the map
		GeoPoint defaultStartPoint = this.mapGenerator.getDefaultStartPoint();
		this.latitude = defaultStartPoint.getLatitude();
		this.longitude = defaultStartPoint.getLongitude();
		this.zoomLevel = this.mapGenerator.getDefaultZoomLevel();
		this.zoomLevelMin = DEFAULT_ZOOM_LEVEL_MIN;
		this.zoomLevelMax = Byte.MAX_VALUE;

		// create and start the MapMover thread
		this.mapMover = new MapMover();
		this.mapMover.setMapView(this);
		this.mapMover.start();

		// create and start the ZoomAnimator thread
		this.zoomAnimator = new ZoomAnimator();
		this.zoomAnimator.setMapView(this);
		this.zoomAnimator.start();
	}

	private void setupZoomControls() {
		// create the ZoomControls and set the click listeners
		this.zoomControls = new ZoomControls(this.activity);
		this.zoomControls.setVisibility(View.GONE);

		// set the click listeners for each zoom button
		this.zoomControls.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				zoom((byte) 1, 1);
			}
		});
		this.zoomControls.setOnZoomOutClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				zoom((byte) -1, 1);
			}
		});

		// create the handler for the fade out animation
		this.zoomControlsHideHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				hideZoomZontrols();
			}
		};

		addView(this.zoomControls, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	/**
	 * Displays the zoom controls permanently.
	 */
	private void showZoomControls() {
		if (this.showZoomControls) {
			this.zoomControlsHideHandler.removeMessages(MSG_ZOOM_CONTROLS_HIDE);
			if (this.zoomControls.getVisibility() != VISIBLE) {
				this.zoomControls.show();
			}
		}
	}

	/**
	 * Creates and starts the MapGenerator thread.
	 */
	private void startMapGeneratorThread() {
		switch (this.mapViewMode) {
			case CANVAS_RENDERER:
				this.mapGenerator = new CanvasRenderer();
				((DatabaseMapGenerator) this.mapGenerator).setDatabase(this.mapDatabase);
				break;
			case MAPNIK_TILE_DOWNLOAD:
				this.mapGenerator = new MapnikTileDownload();
				break;
			case OPENCYCLEMAP_TILE_DOWNLOAD:
				this.mapGenerator = new OpenCycleMapTileDownload();
				break;
			case OSMARENDER_TILE_DOWNLOAD:
				this.mapGenerator = new OsmarenderTileDownload();
				break;
		}

		if (this.attachedToWindow) {
			this.mapGenerator.onAttachedToWindow();
		}
		this.mapGenerator.setTileCaches(this.tileRAMCache, this.tileMemoryCardCache);
		this.mapGenerator.setMapView(this);
		this.mapGenerator.start();
	}

	private void stopMapGeneratorThread() {
		// stop the MapGenerator thread
		if (this.mapGenerator != null) {
			this.mapGenerator.interrupt();
			try {
				this.mapGenerator.join();
			} catch (InterruptedException e) {
				// restore the interrupted status
				Thread.currentThread().interrupt();
			}
			this.mapGenerator.onDetachedFromWindow();
			this.mapGenerator = null;
		}
	}

	private void waitForMapGenerator() {
		synchronized (this) {
			while (!this.mapGenerator.isReady()) {
				try {
					wait(50);
				} catch (InterruptedException e) {
					// restore the interrupted status
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	private void waitForMapMover() {
		synchronized (this) {
			while (!this.mapMover.isReady()) {
				try {
					wait(50);
				} catch (InterruptedException e) {
					// restore the interrupted status
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	private void waitForZoomAnimator() {
		synchronized (this) {
			while (this.zoomAnimator.isExecuting()) {
				try {
					wait(50);
				} catch (InterruptedException e) {
					// restore the interrupted status
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	@Override
	protected void onAttachedToWindow() {
		this.attachedToWindow = true;
		if (this.mapGenerator != null) {
			this.mapGenerator.onAttachedToWindow();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		this.attachedToWindow = false;
		if (this.mapGenerator != null) {
			this.mapGenerator.onDetachedFromWindow();
		}
	}

	@Override
	protected final void onDraw(Canvas canvas) {
		if (this.mapViewBitmap1 == null) {
			return;
		}

		// draw the map
		synchronized (this.matrix) {
			canvas.drawBitmap(this.mapViewBitmap1, this.matrix, null);
			// draw the overlays
			synchronized (this.overlays) {
				for (Overlay overlay : this.overlays) {
					overlay.draw(canvas);
				}
			}
		}

		// draw the scale bar
		if (this.showScaleBar) {
			canvas.drawBitmap(this.mapScaleBitmap, 5, getHeight() - SCALE_BAR_HEIGHT - 5, null);
		}

		// draw the FPS counter
		if (this.showFpsCounter) {
			this.currentTime = SystemClock.uptimeMillis();
			if (this.currentTime - this.previousTime > 1000) {
				this.fps = (int) ((this.frameCounter * 1000) / (this.currentTime - this.previousTime));
				this.previousTime = this.currentTime;
				this.frameCounter = 0;
			}
			canvas.drawText(String.valueOf(this.fps), 20, 30, this.fpsPaint);
			++this.frameCounter;
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!changed) {
			// neither size nor position have changed
			return;
		}
		// position the ZoomControls at the bottom right corner
		this.zoomControls.layout(r - this.zoomControls.getMeasuredWidth() - l - 5, b
				- this.zoomControls.getMeasuredHeight() - t, r - l - 5, b - t);
	}

	@Override
	protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// find out how big the ZoomControls should be
		this.zoomControls.measure(MeasureSpec.makeMeasureSpec(MeasureSpec
				.getSize(widthMeasureSpec), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(
				MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST));

		// make sure that MapView is big enough to display the ZoomControls
		setMeasuredDimension(Math.max(MeasureSpec.getSize(widthMeasureSpec), this.zoomControls
				.getMeasuredWidth()), Math.max(MeasureSpec.getSize(heightMeasureSpec),
				this.zoomControls.getMeasuredHeight()));
	}

	@Override
	protected synchronized void onSizeChanged(int w, int h, int oldw, int oldh) {
		// check if the previous MapView bitmaps must be recycled
		if (this.mapViewBitmap1 != null) {
			this.mapViewBitmap1.recycle();
		}
		if (this.mapViewBitmap2 != null) {
			this.mapViewBitmap2.recycle();
		}

		// check if the new dimensions are positive
		if (w > 0 && h > 0) {
			// calculate how many tiles are needed to fill the MapView completely
			this.numberOfTiles = ((w / Tile.TILE_SIZE) + 1) * ((h / Tile.TILE_SIZE) + 1);

			// create the new MapView bitmaps
			this.mapViewBitmap1 = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
			this.mapViewBitmap2 = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

			// create the canvas
			this.mapViewBitmap1.eraseColor(MAP_VIEW_BACKGROUND);
			this.mapViewCanvas = new Canvas(this.mapViewBitmap1);
			handleTiles();

			// set up the overlays
			synchronized (this.overlays) {
				for (Overlay overlay : this.overlays) {
					overlay.onSizeChanged();
				}
			}
		}
	}

	/**
	 * Called by the enclosing {@link MapActivity} when the MapView is no longer needed.
	 */
	void destroy() {
		// stop all overlay threads
		if (this.overlays != null) {
			this.overlays.clear();
		}

		// stop the MapMover thread
		if (this.mapMover != null) {
			this.mapMover.interrupt();
			try {
				this.mapMover.join();
			} catch (InterruptedException e) {
				// restore the interrupted status
				Thread.currentThread().interrupt();
			}
		}

		// stop the ZoomAnimator thread
		if (this.zoomAnimator != null) {
			this.zoomAnimator.interrupt();
			try {
				this.zoomAnimator.join();
			} catch (InterruptedException e) {
				// restore the interrupted status
				Thread.currentThread().interrupt();
			}
		}

		// stop the LongTapDetector thread
		if (this.touchEventHandler.longPressDetector != null) {
			this.touchEventHandler.longPressDetector.interrupt();
		}

		stopMapGeneratorThread();

		// destroy the map controller to avoid memory leaks
		this.mapController = null;

		// free the mapViewBitmap1 memory
		if (this.mapViewBitmap1 != null) {
			this.mapViewBitmap1.recycle();
			this.mapViewBitmap1 = null;
		}

		// free the mapViewBitmap2 memory
		if (this.mapViewBitmap2 != null) {
			this.mapViewBitmap2.recycle();
			this.mapViewBitmap2 = null;
		}

		// free the mapScaleBitmap memory
		if (this.mapScaleBitmap != null) {
			this.mapScaleBitmap.recycle();
			this.mapScaleBitmap = null;
		}

		// free the tileBitmap memory
		if (this.tileBitmap != null) {
			this.tileBitmap.recycle();
			this.tileBitmap = null;
		}

		// destroy the image bitmap cache
		if (this.tileRAMCache != null) {
			this.tileRAMCache.destroy();
			this.tileRAMCache = null;
		}

		// destroy the image file cache
		if (this.tileMemoryCardCache != null) {
			this.tileMemoryCardCache.destroy(this.persistence);
			this.tileMemoryCardCache = null;
		}

		// close the map file
		if (this.mapDatabase != null) {
			this.mapDatabase.closeFile();
			this.mapDatabase = null;
		}
	}

	/**
	 * Returns the default starting point for the map, which depends on the currently selected operation
	 * mode of the MapView.
	 * 
	 * @return the default starting point.
	 */
	GeoPoint getDefaultStartPoint() {
		return this.mapGenerator.getDefaultStartPoint();
	}

	/**
	 * Returns the default zoom level for the map, which depends on the currently selected operation
	 * mode of the MapView.
	 * 
	 * @return the default zoom level.
	 */
	byte getDefaultZoomLevel() {
		return this.mapGenerator.getDefaultZoomLevel();
	}

	/**
	 * Returns the enclosing MapActivity of the MapView.
	 * 
	 * @return the enclosing MapActivity.
	 */
	Activity getActivity() {
		return this.activity;
	}

	/**
	 * Returns the current value of the given text field.
	 * 
	 * @param textField
	 *            the text field whose value should be returned.
	 * @return the current value of the text field (may be null).
	 */
	String getText(TextField textField) {
		switch (textField) {
			case KILOMETER:
				return this.textKilometer;
			case METER:
				return this.textMeter;
			default:
				// all cases are covered, the default case should never occur
				return null;
		}
	}

	/**
	 * Makes sure that the given latitude value is within the possible range.
	 * 
	 * @param lat
	 *            the latitude value that should be checked.
	 * @return a valid latitude value.
	 */
	double getValidLatitude(double lat) {
		if (lat < LATITUDE_MIN) {
			return LATITUDE_MIN;
		} else if (lat > LATITUDE_MAX) {
			return LATITUDE_MAX;
		}
		return lat;
	}

	/**
	 * Returns the ZoomAnimator of this MapView.
	 * 
	 * @return the ZoomAnimator of this MapView.
	 */
	ZoomAnimator getZoomAnimator() {
		return this.zoomAnimator;
	}

	/**
	 * Calculates all necessary tiles and adds jobs accordingly.
	 */
	void handleTiles() {
		if (this.getWidth() == 0) {
			return;
		}

		synchronized (this.overlays) {
			for (Overlay overlay : this.overlays) {
				overlay.requestRedraw();
			}
		}

		if (!this.mapViewMode.requiresInternetConnection() && this.mapFile == null) {
			return;
		}

		synchronized (this) {
			// calculate the XY position of the MapView
			this.mapViewPixelX = MercatorProjection.longitudeToPixelX(this.longitude,
					this.zoomLevel)
					- (getWidth() >> 1);
			this.mapViewPixelY = MercatorProjection.latitudeToPixelY(this.latitude,
					this.zoomLevel)
					- (getHeight() >> 1);

			this.mapViewTileX1 = MercatorProjection.pixelXToTileX(this.mapViewPixelX,
					this.zoomLevel);
			this.mapViewTileY1 = MercatorProjection.pixelYToTileY(this.mapViewPixelY,
					this.zoomLevel);
			this.mapViewTileX2 = MercatorProjection.pixelXToTileX(this.mapViewPixelX
					+ getWidth(), this.zoomLevel);
			this.mapViewTileY2 = MercatorProjection.pixelYToTileY(this.mapViewPixelY
					+ getHeight(), this.zoomLevel);

			// go through all tiles that intersect the screen rectangle
			for (this.tileY = this.mapViewTileY2; this.tileY >= this.mapViewTileY1; --this.tileY) {
				for (this.tileX = this.mapViewTileX2; this.tileX >= this.mapViewTileX1; --this.tileX) {
					this.currentTile = new Tile(this.tileX, this.tileY, this.zoomLevel);
					this.currentJob = new MapGeneratorJob(this.currentTile, this.mapViewMode,
							this.mapFile, this.textScale, this.drawTileFrames,
							this.drawTileCoordinates, this.highlightWaterTiles);
					if (this.tileRAMCache.containsKey(this.currentJob)) {
						// bitmap cache hit
						putTileOnBitmap(this.currentJob, this.tileRAMCache.get(this.currentJob));
					} else if (this.tileMemoryCardCache.containsKey(this.currentJob)) {
						// memory card cache hit
						if (this.tileMemoryCardCache.get(this.currentJob, this.tileBuffer)) {
							this.tileBitmap.copyPixelsFromBuffer(this.tileBuffer);
							putTileOnBitmap(this.currentJob, this.tileBitmap);
							this.tileRAMCache.put(this.currentJob, this.tileBitmap);
						} else {
							// the image data could not be read from the cache
							this.mapGenerator.addJob(this.currentJob);
						}
					} else {
						// cache miss
						this.mapGenerator.addJob(this.currentJob);
					}
				}
			}
		}

		if (this.showScaleBar) {
			renderScaleBar();
		}

		invalidateOnUiThread();

		// notify the MapGenerator to process the job list
		this.mapGenerator.requestSchedule(true);
	}

	/**
	 * Checks if the map currently has a valid center position.
	 * 
	 * @return true if the current center position of the map is valid, false otherwise.
	 */
	synchronized boolean hasValidCenter() {
		if (Double.isNaN(this.latitude) || this.latitude > LATITUDE_MAX
				|| this.latitude < LATITUDE_MIN) {
			return false;
		} else if (Double.isNaN(this.longitude) || this.longitude > LONGITUDE_MAX
				|| this.longitude < LONGITUDE_MIN) {
			return false;
		} else if (!this.mapViewMode.requiresInternetConnection()
				&& (this.mapDatabase == null || this.mapDatabase.getMapBoundary() == null || !this.mapDatabase
						.getMapBoundary().contains(getMapCenter().getLongitudeE6(),
								getMapCenter().getLatitudeE6()))) {
			return false;
		}
		return true;
	}

	/**
	 * Hides the zoom controls immediately.
	 */
	void hideZoomZontrols() {
		this.zoomControls.hide();
	}

	/**
	 * Executes a {@link #invalidate()} call on the UI thread.
	 */
	void invalidateOnUiThread() {
		if (this.activity != null) {
			this.activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					invalidate();
				}
			});
		}
	}

	/**
	 * @return true if the matrix is the identity matrix, false otherwise.
	 */
	boolean matrixIsIdentity() {
		synchronized (this.matrix) {
			return this.matrix.isIdentity();
		}
	}

	/**
	 * Scales the matrix of the MapView and all its overlays.
	 * 
	 * @param sx
	 *            the horizontal scale.
	 * @param sy
	 *            the vertical scale.
	 * @param px
	 *            the horizontal pivot point.
	 * @param py
	 *            the vertical pivot point.
	 */
	void matrixPostScale(float sx, float sy, float px, float py) {
		synchronized (this.matrix) {
			this.matrix.postScale(sx, sy, px, py);
			synchronized (MapView.this.overlays) {
				for (Overlay overlay : MapView.this.overlays) {
					overlay.matrixPostScale(sx, sy, px, py);
				}
			}
		}
	}

	/**
	 * Translates the matrix of the MapView and all its overlays.
	 * 
	 * @param dx
	 *            the horizontal translation.
	 * @param dy
	 *            the vertical translation.
	 */
	void matrixPostTranslate(float dx, float dy) {
		synchronized (this.matrix) {
			this.matrix.postTranslate(dx, dy);
			synchronized (MapView.this.overlays) {
				for (Overlay overlay : MapView.this.overlays) {
					overlay.matrixPostTranslate(dx, dy);
				}
			}
		}
	}

	/**
	 * Moves the map by the given amount of pixels.
	 * 
	 * @param moveHorizontal
	 *            the amount of pixels to move the map horizontally.
	 * @param moveVertical
	 *            the amount of pixels to move the map vertically.
	 */
	synchronized void moveMap(float moveHorizontal, float moveVertical) {
		this.longitude = MercatorProjection.pixelXToLongitude(MercatorProjection
				.longitudeToPixelX(this.longitude, this.zoomLevel)
				- moveHorizontal, this.zoomLevel);
		this.latitude = getValidLatitude(MercatorProjection.pixelYToLatitude(MercatorProjection
				.latitudeToPixelY(this.latitude, this.zoomLevel)
				- moveVertical, this.zoomLevel));
	}

	/**
	 * Called by the enclosing activity when {@link MapActivity#onPause()} is executed.
	 */
	void onPause() {
		// pause the MapMover thread
		if (this.mapMover != null) {
			this.mapMover.pause();
		}

		// pause the MapGenerator thread
		if (this.mapGenerator != null) {
			this.mapGenerator.pause();
		}
	}

	/**
	 * Called by the enclosing activity when {@link MapActivity#onResume()} is executed.
	 */
	void onResume() {
		// unpause the MapMover thread
		if (this.mapMover != null) {
			this.mapMover.unpause();
		}

		// unpause the MapGenerator thread
		if (this.mapGenerator != null) {
			this.mapGenerator.unpause();
		}
	}

	/**
	 * Draws a tile bitmap at the right position on the MapView bitmap.
	 * 
	 * @param mapGeneratorJob
	 *            the job with the tile.
	 * @param bitmap
	 *            the bitmap to be drawn.
	 */
	synchronized void putTileOnBitmap(MapGeneratorJob mapGeneratorJob, Bitmap bitmap) {
		// check if the tile and the current MapView rectangle intersect
		if (this.mapViewPixelX - mapGeneratorJob.tile.pixelX > Tile.TILE_SIZE
				|| this.mapViewPixelX + getWidth() < mapGeneratorJob.tile.pixelX) {
			// no intersection in x direction
			return;
		} else if (this.mapViewPixelY - mapGeneratorJob.tile.pixelY > Tile.TILE_SIZE
				|| this.mapViewPixelY + getHeight() < mapGeneratorJob.tile.pixelY) {
			// no intersection in y direction
			return;
		} else if (mapGeneratorJob.tile.zoomLevel != this.zoomLevel) {
			// the tile doesn't fit to the current zoom level
			return;
		}

		if (this.zoomAnimator.isExecuting()) {
			// do not disturb the ongoing animation
			return;
		}

		if (!matrixIsIdentity()) {
			// change the current MapView bitmap
			this.mapViewBitmap2.eraseColor(MAP_VIEW_BACKGROUND);
			this.mapViewCanvas.setBitmap(this.mapViewBitmap2);

			// draw the previous MapView bitmap on the current MapView bitmap
			synchronized (this.matrix) {
				this.mapViewCanvas.drawBitmap(this.mapViewBitmap1, this.matrix, null);
				this.matrix.reset();
			}

			// swap the two MapView bitmaps
			Bitmap mapViewBitmapSwap = this.mapViewBitmap1;
			this.mapViewBitmap1 = this.mapViewBitmap2;
			this.mapViewBitmap2 = mapViewBitmapSwap;
		}

		// draw the tile bitmap at the correct position
		this.mapViewCanvas.drawBitmap(bitmap,
				(float) (mapGeneratorJob.tile.pixelX - this.mapViewPixelX),
				(float) (mapGeneratorJob.tile.pixelY - this.mapViewPixelY), null);
	}

	/**
	 * This method is called by the MapGenerator when its job queue is empty.
	 */
	void requestMoreJobs() {
		if (!this.mapViewMode.requiresInternetConnection() && this.mapFile == null) {
			return;
		} else if (this.getWidth() == 0) {
			return;
		} else if (this.tileMemoryCardCacheSize < this.numberOfTiles * 3) {
			// the capacity of the file cache is to small, skip preprocessing
			return;
		} else if (this.zoomLevel == 0) {
			// there are no surrounding tiles on zoom level 0
			return;
		}

		synchronized (this) {
			// tiles below and above the visible area
			for (this.tileX = this.mapViewTileX2 + 1; this.tileX >= this.mapViewTileX1 - 1; --this.tileX) {
				this.currentTile = new Tile(this.tileX, this.mapViewTileY2 + 1, this.zoomLevel);
				this.currentJob = new MapGeneratorJob(this.currentTile, this.mapViewMode,
						this.mapFile, this.textScale, this.drawTileFrames,
						this.drawTileCoordinates, this.highlightWaterTiles);
				if (!this.tileMemoryCardCache.containsKey(this.currentJob)) {
					// cache miss
					this.mapGenerator.addJob(this.currentJob);
				}

				this.currentTile = new Tile(this.tileX, this.mapViewTileY1 - 1, this.zoomLevel);
				this.currentJob = new MapGeneratorJob(this.currentTile, this.mapViewMode,
						this.mapFile, this.textScale, this.drawTileFrames,
						this.drawTileCoordinates, this.highlightWaterTiles);
				if (!this.tileMemoryCardCache.containsKey(this.currentJob)) {
					// cache miss
					this.mapGenerator.addJob(this.currentJob);
				}
			}

			// tiles left and right from the visible area
			for (this.tileY = this.mapViewTileY2; this.tileY >= this.mapViewTileY1; --this.tileY) {
				this.currentTile = new Tile(this.mapViewTileX2 + 1, this.tileY, this.zoomLevel);
				this.currentJob = new MapGeneratorJob(this.currentTile, this.mapViewMode,
						this.mapFile, this.textScale, this.drawTileFrames,
						this.drawTileCoordinates, this.highlightWaterTiles);
				if (!this.tileMemoryCardCache.containsKey(this.currentJob)) {
					// cache miss
					this.mapGenerator.addJob(this.currentJob);
				}

				this.currentTile = new Tile(this.mapViewTileX1 - 1, this.tileY, this.zoomLevel);
				this.currentJob = new MapGeneratorJob(this.currentTile, this.mapViewMode,
						this.mapFile, this.textScale, this.drawTileFrames,
						this.drawTileCoordinates, this.highlightWaterTiles);
				if (!this.tileMemoryCardCache.containsKey(this.currentJob)) {
					// cache miss
					this.mapGenerator.addJob(this.currentJob);
				}
			}
		}

		// notify the MapGenerator to process the job list
		this.mapGenerator.requestSchedule(false);
	}

	/**
	 * Sets the center of the MapView and triggers a redraw.
	 * 
	 * @param point
	 *            the new center point of the map.
	 */
	public void setCenter(GeoPoint point) {
		setCenterAndZoom(point, this.zoomLevel);
	}

	/**
	 * Sets the center and zoom level of the MapView and triggers a redraw.
	 * 
	 * @param point
	 *            the new center point of the map.
	 * @param zoom
	 *            the new zoom level. This value will be limited by the maximum and minimum possible
	 *            zoom level.
	 */
	public void setCenterAndZoom(GeoPoint point, byte zoom) {
		if (point == null) {
			// do nothing
			return;
		}

		if (this.mapViewMode.requiresInternetConnection()
				|| (this.mapDatabase != null && this.mapDatabase.getMapBoundary() != null && this.mapDatabase
						.getMapBoundary().contains(point.getLongitudeE6(),
								point.getLatitudeE6()))) {
			if (hasValidCenter()) {
				// calculate the distance between previous and current position
				synchronized (this) {
					this.matrixTranslateX = (float) (MercatorProjection.longitudeToPixelX(
							this.longitude, this.zoomLevel) - MercatorProjection
							.longitudeToPixelX(point.getLongitude(), this.zoomLevel));
					this.matrixTranslateY = (float) (MercatorProjection.latitudeToPixelY(
							this.latitude, this.zoomLevel) - MercatorProjection
							.latitudeToPixelY(point.getLatitude(), this.zoomLevel));
				}
				matrixPostTranslate(this.matrixTranslateX, this.matrixTranslateY);
			}

			// set the new center coordinates and the zoom level
			synchronized (this) {
				this.latitude = getValidLatitude(point.getLatitude());
				this.longitude = point.getLongitude();
				this.zoomLevel = getValidZoomLevel(zoom);
			}

			// enable or disable the zoom buttons if necessary
			this.zoomControls
					.setIsZoomInEnabled(this.zoomLevel < getMaximumPossibleZoomLevel());
			this.zoomControls.setIsZoomOutEnabled(this.zoomLevel > this.zoomLevelMin);
			handleTiles();
		}
	}

	/**
	 * Calculates the priority for the given job based on the current position and zoom level of the
	 * map.
	 * 
	 * @param mapGeneratorJob
	 *            the job for which the priority should be calculated.
	 * @return the MapGeneratorJob with updated priority.
	 */
	MapGeneratorJob setJobPriority(MapGeneratorJob mapGeneratorJob) {
		if (mapGeneratorJob.tile.zoomLevel != this.zoomLevel) {
			mapGeneratorJob.priority = 1000 * Math.abs(mapGeneratorJob.tile.zoomLevel
					- this.zoomLevel);
		} else {
			// calculate the center of the MapView
			double mapViewCenterX = this.mapViewPixelX + (getWidth() >> 1);
			double mapViewCenterY = this.mapViewPixelY + (getHeight() >> 1);

			// calculate the center of the tile
			long tileCenterX = mapGeneratorJob.tile.pixelX + (Tile.TILE_SIZE >> 1);
			long tileCenterY = mapGeneratorJob.tile.pixelY + (Tile.TILE_SIZE >> 1);

			// set tile priority to the distance from the MapView center
			double diffX = mapViewCenterX - tileCenterX;
			double diffY = mapViewCenterY - tileCenterY;
			mapGeneratorJob.priority = (int) Math.sqrt(diffX * diffX + diffY * diffY);
		}
		return mapGeneratorJob;
	}

	/**
	 * Sets the map file for this MapView without displaying it.
	 * 
	 * @param newMapFile
	 *            the path to the new map file.
	 * @throws UnsupportedOperationException
	 *             if the current MapView mode works with an Internet connection.
	 */
	void setMapFileFromParcel(String newMapFile) {
		if (this.mapViewMode.requiresInternetConnection()) {
			throw new UnsupportedOperationException();
		}
		if (newMapFile != null && this.mapDatabase != null
				&& this.mapDatabase.openFile(newMapFile)) {
			this.mapDatabase.prepareExecution();
			((DatabaseMapGenerator) this.mapGenerator).onMapFileChange();
			this.mapFile = newMapFile;
		} else {
			this.mapFile = null;
		}
	}

	/**
	 * Zooms in or out by the given amount of zoom levels.
	 * 
	 * @param zoomLevelDiff
	 *            the difference to the current zoom level.
	 * @param zoomStart
	 *            the zoom factor at the begin of the animation.
	 * @return true if the zoom level was changed, false otherwise.
	 */
	boolean zoom(byte zoomLevelDiff, float zoomStart) {
		if (zoomLevelDiff > 0) {
			// check if zoom in is possible
			if (this.zoomLevel + zoomLevelDiff > getMaximumPossibleZoomLevel()) {
				return false;
			}
			this.matrixScaleFactor = 1 << zoomLevelDiff;
		} else if (zoomLevelDiff < 0) {
			// check if zoom out is possible
			if (this.zoomLevel + zoomLevelDiff < this.zoomLevelMin) {
				return false;
			}
			this.matrixScaleFactor = 1.0f / (1 << -zoomLevelDiff);
		} else {
			// zoom level is unchanged
			this.matrixScaleFactor = 1;
		}

		// change the zoom level
		synchronized (this) {
			this.zoomLevel += zoomLevelDiff;
		}

		// enable or disable the zoom buttons if necessary
		this.zoomControls.setIsZoomInEnabled(this.zoomLevel < getMaximumPossibleZoomLevel());
		this.zoomControls.setIsZoomOutEnabled(this.zoomLevel > this.zoomLevelMin);
		hideZoomControlsDelayed();

		this.zoomAnimator.setParameters(zoomStart, this.matrixScaleFactor, getWidth() >> 1,
				getHeight() >> 1);
		this.zoomAnimator.startAnimation();
		return true;
	}
}