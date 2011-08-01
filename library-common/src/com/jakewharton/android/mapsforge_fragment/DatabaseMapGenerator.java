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

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;

/**
 * A MapGenerator that reads map data from a database and renders them.
 */
abstract class DatabaseMapGenerator extends MapGenerator implements
		CoastlineAlgorithm.ClosedPolygonHandler {
	private static final byte AREA_NAME_BLACK = 0;
	private static final byte AREA_NAME_BLUE = 1;
	private static final byte AREA_NAME_RED = 2;
	private static final int BITMAP_AMENITY = 0x04;
	private static final int BITMAP_BUILDING = 0x20;
	private static final int BITMAP_HIGHWAY = 0x80;
	private static final int BITMAP_LANDUSE = 0x10;
	private static final int BITMAP_LEISURE = 0x08;
	private static final int BITMAP_NATURAL = 0x02;
	private static final int BITMAP_RAILWAY = 0x40;
	private static final int BITMAP_WATERWAY = 0x01;
	private static final byte DEFAULT_LAYER = 5;
	private static final byte DEFAULT_ZOOM_LEVEL = 13;
	private static final byte LAYERS = 11;
	private static final byte MIN_ZOOM_LEVEL_AREA_NAMES = 17;
	private static final byte MIN_ZOOM_LEVEL_WAY_NAMES = 15;

	private static final Paint PAINT_AERIALWAY = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_AEROWAY_AERODROME_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_AEROWAY_AERODROME_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_AEROWAY_APRON_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_AEROWAY_RUNWAY1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_AEROWAY_RUNWAY2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_AEROWAY_TAXIWAY1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_AEROWAY_TAXIWAY2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_AEROWAY_TERMINAL_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_AEROWAY_TERMINAL_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_AMENITY_GRAVE_YARD_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_AMENITY_HOSPITAL_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_AMENITY_PARKING_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_AMENITY_PARKING_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_AMENITY_SCHOOL_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_AMENITY_SCHOOL_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_BARRIER_BOLLARD = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_BARRIER_WALL = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_10 = new Paint(
			Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_2 = new Paint(
			Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_4 = new Paint(
			Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_6 = new Paint(
			Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_8 = new Paint(
			Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_9 = new Paint(
			Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_BOUNDARY_NATIONAL_PARK = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_BUILDING_ROOF_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_BUILDING_YES_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_BUILDING_YES_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_HIGHWAY_BRIDLEWAY1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_BRIDLEWAY2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_BYWAY = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_CONSTRUCTION = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_CYCLEWAY1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_CYCLEWAY2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_FOOTWAY_AREA_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_FOOTWAY_AREA_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_FOOTWAY1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_FOOTWAY2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_LIVING_STREET1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_LIVING_STREET2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_MOTORWAY_LINK1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_MOTORWAY_LINK2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_MOTORWAY1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_MOTORWAY2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_PATH1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_PATH2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_PEDESTRIAN_AREA_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_PEDESTRIAN_AREA_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_PEDESTRIAN1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_PEDESTRIAN2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_PRIMARY_LINK1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_PRIMARY_LINK2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_PRIMARY1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_PRIMARY2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_RESIDENTIAL1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_RESIDENTIAL2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_ROAD1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_ROAD2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_SECONDARY_LINK1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_SECONDARY_LINK2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_SECONDARY1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_SECONDARY2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_SERVICE_AREA_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_SERVICE_AREA_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_SERVICE1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_SERVICE2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_STEPS1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_STEPS2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_TERTIARY1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_TERTIARY2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_TRACK1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_TRACK2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_TRUNK_LINK1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_TRUNK_LINK2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_TRUNK1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_TRUNK2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_TUNNEL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_UNCLASSIFIED1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HIGHWAY_UNCLASSIFIED2 = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_HISTORIC_CIRCLE_INNER = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_HISTORIC_CIRCLE_OUTER = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_LANDUSE_ALLOTMENTS_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_ALLOTMENTS_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_BASIN_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_CEMETERY_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_CEMETERY_PATTERN = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_COMMERCIAL_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_COMMERCIAL_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_CONSTRUCTION_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_FARM_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_FOREST_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_GRASS_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_GRASS_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_INDUSTRIAL_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_MILITARY_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_RESIDENTIAL_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LANDUSE_RETAIL_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_LEISURE_COMMON_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LEISURE_COMMON_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LEISURE_NATURE_RESERVE_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LEISURE_NATURE_RESERVE_PATTERN = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LEISURE_STADIUM_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_LEISURE_STADIUM_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_MAN_MADE_PIER = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_MILITARY_BARRACKS_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_MILITARY_NAVAL_BASE_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_MILITARY_PATTERN = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_NAME_BLACK_HUGE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_BLACK_LARGE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_BLACK_LARGER = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_BLACK_NORMAL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_BLACK_SMALL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_BLACK_TINY = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_BLACK_TINY_CENTER = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_BLUE_TINY = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_PURPLE_TINY = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_RED_NORMAL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_RED_SMALLER = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_RED_TINY = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_WHITE_STROKE_HUGE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_WHITE_STROKE_LARGE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_WHITE_STROKE_LARGER = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_WHITE_STROKE_NORMAL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_WHITE_STROKE_SMALL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_WHITE_STROKE_SMALLER = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_WHITE_STROKE_TINY = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NAME_WHITE_STROKE_TINY_CENTER = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_NATURAL_BEACH_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NATURAL_COASTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NATURAL_COASTLINE_INVALID = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NATURAL_COASTLINE_VALID = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NATURAL_GLACIER_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NATURAL_GLACIER_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NATURAL_HEATH_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NATURAL_LAND_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NATURAL_MARSH_PATTERN = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NATURAL_WATER_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_NATURAL_WOOD_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_PISTE_TYPE_DOWNHILL_ADVANCED = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_PISTE_TYPE_DOWNHILL_EASY = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_PISTE_TYPE_DOWNHILL_EXPERT = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_PISTE_TYPE_DOWNHILL_FREERIDE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_PISTE_TYPE_DOWNHILL_INTERMEDIATE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_PISTE_TYPE_DOWNHILL_NOVICE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_PISTE_TYPE_NORDIC = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_RAILWAY_CIRCLE_INNER = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_RAILWAY_CIRCLE_OUTER = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_RAILWAY_LIGHT_RAIL1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_RAILWAY_LIGHT_RAIL2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_RAILWAY_RAIL_TUNNEL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_RAILWAY_RAIL1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_RAILWAY_RAIL2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_RAILWAY_STATION_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_RAILWAY_STATION_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_RAILWAY_SUBWAY_TUNNEL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_RAILWAY_SUBWAY1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_RAILWAY_SUBWAY2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_RAILWAY_TRAM1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_RAILWAY_TRAM2 = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_ROUTE_FERRY = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_SPORT_SHOOTING_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_SPORT_SHOOTING_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_SPORT_SWIMMING_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_SPORT_SWIMMING_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_SPORT_TENNIS_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_SPORT_TENNIS_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_TOURISM_ATTRACTION_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_TOURISM_ZOO_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_TOURISM_ZOO_OUTLINE = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_WATER_TILE_HIGHTLIGHT = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_WATERWAY_CANAL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_WATERWAY_RIVER = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_WATERWAY_RIVERBANK_FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_WATERWAY_STREAM = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final Paint PAINT_WOOD_CONIFEROUS_PATTERN = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_WOOD_DECIDUOUS_PATTERN = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final Paint PAINT_WOOD_MIXED_PATTERN = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final byte TEXT_SCALE_HUGE = 25;
	private static final byte TEXT_SCALE_LARGE = 15;
	private static final byte TEXT_SCALE_LARGER = 20;
	private static final byte TEXT_SCALE_NORMAL = 13;
	private static final byte TEXT_SCALE_SMALL = 12;
	private static final byte TEXT_SCALE_SMALLER = 11;
	private static final byte TEXT_SCALE_TINY = 10;

	private static final int TILE_BACKGROUND = Color.rgb(248, 248, 248);
	private static final byte ZOOM_MAX = 21;

	/**
	 * Returns a WayContainer for the given coastline segment.
	 * 
	 * @param coastline
	 *            the coordinates of the coastline segment.
	 * @return a WayContainer.
	 */
	private static WayContainer getWayContainer(float[] coastline) {
		float[][] wayCoordinates = new float[1][coastline.length];
		System.arraycopy(coastline, 0, wayCoordinates[0], 0, coastline.length);
		return new WayContainer(wayCoordinates);
	}

	private List<PointTextContainer> areaLabels;
	private float[] areaNamePositions;
	private CoastlineAlgorithm coastlineAlgorithm;
	private float[][] coordinates;
	private MapGeneratorJob currentJob;
	private Tile currentTile;
	private float currentX;
	private float currentY;
	private MapDatabase database;
	private float diffX;
	private float diffY;
	private int innerWayLength;
	private List<List<ShapePaintContainer>> innerWayList;
	private LabelPlacement labelPlacement;
	private float lastTileTextScale;
	private byte lastTileZoomLevel;
	private List<List<ShapePaintContainer>> layer;
	private MapPatterns mapPatterns;
	private MapSymbols mapSymbols;
	private List<PointTextContainer> nodes;
	private List<SymbolContainer> pointSymbols;
	private float previousX;
	private float previousY;
	private double segmentLengthInPixel;
	private ShapeContainer shapeContainer;
	private int skipPixels;
	private SymbolContainer symbolContainer;
	private final TagIDsNodes tagIDsNodes;
	private final TagIDsWays tagIDsWays;
	private Bitmap tileBitmap;
	private Tile tileForCoastlineAlgorithm;
	private float[][] waterTileCoordinates;
	private float[] wayNamePath;
	private List<WayTextContainer> wayNames;
	private List<List<List<ShapePaintContainer>>> ways;
	private List<SymbolContainer> waySymbols;

	/**
	 * Default constructor that must be called by subclasses.
	 */
	DatabaseMapGenerator() {
		super();
		this.tagIDsNodes = new TagIDsNodes();
		this.tagIDsWays = new TagIDsWays();
	}

	@Override
	public void onInvalidCoastlineSegment(float[] coastline) {
		this.ways.get(DEFAULT_LAYER).get(LayerIds.NATURAL$COASTLINE).add(
				new ShapePaintContainer(getWayContainer(coastline), PAINT_NATURAL_COASTLINE_INVALID));
	}

	@Override
	public void onIslandPolygon(float[] coastline) {
		this.ways.get(DEFAULT_LAYER).get(LayerIds.NATURAL$LAND).add(
				new ShapePaintContainer(getWayContainer(coastline), PAINT_NATURAL_LAND_FILL));
		this.ways.get(DEFAULT_LAYER).get(LayerIds.NATURAL$COASTLINE).add(
				new ShapePaintContainer(getWayContainer(coastline), PAINT_NATURAL_COASTLINE));
	}

	@Override
	public void onValidCoastlineSegment(float[] coastline) {
		this.ways.get(DEFAULT_LAYER).get(LayerIds.NATURAL$COASTLINE).add(
				new ShapePaintContainer(getWayContainer(coastline), PAINT_NATURAL_COASTLINE_VALID));
	}

	@Override
	public void onWaterPolygon(float[] coastline) {
		this.ways.get(DEFAULT_LAYER).get(LayerIds.SEA_AREAS).add(
				new ShapePaintContainer(getWayContainer(coastline), PAINT_NATURAL_WATER_FILL));
	}

	@Override
	public void onWaterTile() {
		this.ways.get(DEFAULT_LAYER).get(LayerIds.SEA_AREAS).add(
				new ShapePaintContainer(new WayContainer(this.waterTileCoordinates),
						PAINT_NATURAL_WATER_FILL));
	}

	/**
	 * Draws the name of an area if the zoomLevel level is high enough.
	 * 
	 * @param wayName
	 *            the name of the area.
	 * @param wayLabelPosition
	 *            the position of the area label (may be null).
	 * @param nameColor
	 *            the area name color mode.
	 * @param nameOffset
	 *            the vertical offset from the area center.
	 */
	private void addAreaName(String wayName, int[] wayLabelPosition, byte nameColor, byte nameOffset) {
		if (wayName != null && this.currentTile.zoomLevel >= MIN_ZOOM_LEVEL_AREA_NAMES) {
			if (wayLabelPosition == null) {
				this.areaNamePositions = calculateCenterOfBoundingBox();
			} else {
				this.areaNamePositions = new float[] { scaleLongitude(wayLabelPosition[0]),
						scaleLatitude(wayLabelPosition[1]) };
			}
			// choose the correct text paint
			if (nameColor == AREA_NAME_BLUE) {
				this.areaLabels.add(new PointTextContainer(wayName, this.areaNamePositions[0],
						this.areaNamePositions[1] - nameOffset, PAINT_NAME_BLUE_TINY,
						PAINT_NAME_WHITE_STROKE_TINY));
			} else if (nameColor == AREA_NAME_BLACK) {
				this.areaLabels.add(new PointTextContainer(wayName, this.areaNamePositions[0],
						this.areaNamePositions[1] - nameOffset, PAINT_NAME_BLACK_LARGE));
			} else if (nameColor == AREA_NAME_RED) {
				this.areaLabels.add(new PointTextContainer(wayName, this.areaNamePositions[0],
						this.areaNamePositions[1] - nameOffset, PAINT_NAME_RED_TINY));
			}
		}
	}

	/**
	 * Draws the symbol of an area if the zoomLevel level is high enough.
	 * 
	 * @param symbolBitmap
	 *            the bitmap containing the symbol.
	 * @param zoomLevel
	 *            the minimum zoom level at which the symbol should be rendered.
	 */
	private void addAreaSymbol(Bitmap symbolBitmap, byte zoomLevel) {
		if (symbolBitmap == null || this.currentTile.zoomLevel >= zoomLevel) {
			return;
		}

		this.areaNamePositions = calculateCenterOfBoundingBox();
		this.pointSymbols.add(new SymbolContainer(symbolBitmap, this.areaNamePositions[0]
				- (symbolBitmap.getWidth() >> 1), this.areaNamePositions[1]
				- (symbolBitmap.getHeight() >> 1)));
	}

	private SymbolContainer addPOISymbol(float x, float y, Bitmap symbolBitmap) {
		if (symbolBitmap == null) {
			return null;
		}

		this.symbolContainer = new SymbolContainer(symbolBitmap, x - (symbolBitmap.getWidth() >> 1), y
				- (symbolBitmap.getHeight() >> 1));
		this.pointSymbols.add(this.symbolContainer);
		return this.symbolContainer;
	}

	/**
	 * Renders the name for the current way.
	 * 
	 * @param wayName
	 *            the way name to render.
	 * @param outline
	 *            if specified, the color of this paint will be used for the name outline.
	 */
	private void addWayName(String wayName, Paint outline) {
		// calculate the way name length plus some margin of safety
		float wayNameWidth = PAINT_NAME_BLACK_TINY_CENTER.measureText(wayName) + 20;

		// flag if the current way name has been rendered at least once
		boolean wayNameRendered = false;
		this.skipPixels = 0;

		// get the first way point coordinates
		this.previousX = this.coordinates[0][0];
		this.previousY = this.coordinates[0][1];

		// find way segments long enough to draw the way name on them
		for (int i = 2; i < this.coordinates[0].length; i += 2) {
			// get the current way point coordinates
			this.currentX = this.coordinates[0][i];
			this.currentY = this.coordinates[0][i + 1];

			// calculate the length of the current segment (Euclidian distance)
			this.diffX = this.currentX - this.previousX;
			this.diffY = this.currentY - this.previousY;
			this.segmentLengthInPixel = Math.sqrt(this.diffX * this.diffX + this.diffY * this.diffY);

			if (this.skipPixels > 0) {
				this.skipPixels -= this.segmentLengthInPixel;
			} else if (this.segmentLengthInPixel > wayNameWidth) {
				this.wayNamePath = new float[4];
				// check to prevent inverted way names
				if (this.previousX <= this.currentX) {
					this.wayNamePath[0] = this.previousX;
					this.wayNamePath[1] = this.previousY;
					this.wayNamePath[2] = this.currentX;
					this.wayNamePath[3] = this.currentY;
				} else {
					this.wayNamePath[0] = this.currentX;
					this.wayNamePath[1] = this.currentY;
					this.wayNamePath[2] = this.previousX;
					this.wayNamePath[3] = this.previousY;
				}
				this.wayNames.add(new WayTextContainer(this.wayNamePath, wayName,
						PAINT_NAME_BLACK_TINY_CENTER));

				if (outline != null) {
					// draw the outline of the way name with the correct color
					Paint paintOutline = new Paint(PAINT_NAME_WHITE_STROKE_TINY_CENTER);
					paintOutline.setColor(outline.getColor());
					this.wayNames.add(new WayTextContainer(this.wayNamePath, wayName, paintOutline));
				}

				wayNameRendered = true;

				// set the minimum amount of pixels to skip before repeating the way name
				this.skipPixels = 500;
			}

			// store the previous way point coordinates
			this.previousX = this.currentX;
			this.previousY = this.currentY;
		}

		// if no segment is long enough, check if the name can be drawn on the whole way
		if (!wayNameRendered && getWayLengthInPixel() > wayNameWidth) {
			// check to prevent inverted way names
			if (this.coordinates[0][0] > this.coordinates[0][this.coordinates[0].length - 2]) {
				// reverse the way coordinates
				int offsetLeft = 0;
				int offsetRight = this.coordinates[0].length - 2;
				float exchangeValue;
				while (offsetLeft < offsetRight) {
					// exchange the way x coordinates
					exchangeValue = this.coordinates[0][offsetLeft];
					this.coordinates[0][offsetLeft] = this.coordinates[0][offsetRight];
					this.coordinates[0][offsetRight] = exchangeValue;

					// exchange the way y coordinates
					exchangeValue = this.coordinates[0][offsetLeft + 1];
					this.coordinates[0][offsetLeft + 1] = this.coordinates[0][offsetRight + 1];
					this.coordinates[0][offsetRight + 1] = exchangeValue;

					// move the pointers to the next position;
					offsetLeft += 2;
					offsetRight -= 2;
				}
			}
			this.wayNames.add(new WayTextContainer(this.coordinates[0], wayName,
					PAINT_NAME_BLACK_TINY_CENTER));

			if (outline != null) {
				// draw the outline of the way name with the correct color
				Paint paintOutline = new Paint(PAINT_NAME_WHITE_STROKE_TINY_CENTER);
				paintOutline.setColor(outline.getColor());
				this.wayNames.add(new WayTextContainer(this.coordinates[0], wayName, paintOutline));
			}
		}
	}

	/**
	 * Renders the given symbol along the current way.
	 * 
	 * @param symbolBitmap
	 *            the bitmap containing the symbol.
	 * @param alignCenter
	 *            true if the symbol should be centered, false otherwise.
	 * @param repeatSymbol
	 *            true if the symbol should be repeated, false otherwise.
	 */
	private void addWaySymbol(Bitmap symbolBitmap, boolean alignCenter, boolean repeatSymbol) {
		if (symbolBitmap == null) {
			return;
		}

		/**
		 * Distance in pixels to skip from both ends of a segment.
		 */
		final int segmentSafetyDistance = 30;

		/**
		 * Minimum distance in pixels before the symbol is repeated.
		 */
		final int distanceBetweenSymbols = 200;

		this.skipPixels = segmentSafetyDistance;

		// get the first way point coordinates
		this.previousX = this.coordinates[0][0];
		this.previousY = this.coordinates[0][1];

		// draw the symbol on each way segment
		float segmentLengthRemaining;
		float segmentSkipPercentage;
		float symbolAngle;
		for (int i = 2; i < this.coordinates[0].length; i += 2) {
			// get the current way point coordinates
			this.currentX = this.coordinates[0][i];
			this.currentY = this.coordinates[0][i + 1];

			// calculate the length of the current segment (Euclidian distance)
			this.diffX = this.currentX - this.previousX;
			this.diffY = this.currentY - this.previousY;
			this.segmentLengthInPixel = Math.sqrt(this.diffX * this.diffX + this.diffY * this.diffY);
			segmentLengthRemaining = (float) this.segmentLengthInPixel;

			while (segmentLengthRemaining - this.skipPixels > segmentSafetyDistance) {
				// calculate the percentage of the current segment to skip
				segmentSkipPercentage = this.skipPixels / segmentLengthRemaining;

				// move the previous point forward towards the current point
				this.previousX += this.diffX * segmentSkipPercentage;
				this.previousY += this.diffY * segmentSkipPercentage;
				symbolAngle = (float) Math.toDegrees(Math.atan2(this.currentY - this.previousY,
						this.currentX - this.previousX));

				this.waySymbols.add(new SymbolContainer(symbolBitmap, this.previousX, this.previousY,
						alignCenter, symbolAngle));

				// check if the symbol should only be rendered once
				if (!repeatSymbol) {
					return;
				}

				// recalculate the distances
				this.diffX = this.currentX - this.previousX;
				this.diffY = this.currentY - this.previousY;

				// recalculate the remaining length of the current segment
				segmentLengthRemaining -= this.skipPixels;

				// set the amount of pixels to skip before repeating the symbol
				this.skipPixels = distanceBetweenSymbols;
			}

			this.skipPixels -= segmentLengthRemaining;
			if (this.skipPixels < segmentSafetyDistance) {
				this.skipPixels = segmentSafetyDistance;
			}

			// set the previous way point coordinates for the next loop
			this.previousX = this.currentX;
			this.previousY = this.currentY;
		}
	}

	private float[] calculateCenterOfBoundingBox() {
		// calculate the minimal bounding box
		float bboxLongitude1 = this.coordinates[0][0];
		float bboxLongitude2 = this.coordinates[0][0];
		float bboxLatitude1 = this.coordinates[0][1];
		float bboxLatitude2 = this.coordinates[0][1];
		for (int i = 2; i < this.coordinates[0].length; i += 2) {
			if (this.coordinates[0][i] < bboxLongitude1) {
				bboxLongitude1 = this.coordinates[0][i];
			} else if (this.coordinates[0][i] > bboxLongitude2) {
				bboxLongitude2 = this.coordinates[0][i];
			}
			if (this.coordinates[0][i + 1] > bboxLatitude1) {
				bboxLatitude1 = this.coordinates[0][i + 1];
			} else if (this.coordinates[0][i + 1] < bboxLatitude2) {
				bboxLatitude2 = this.coordinates[0][i + 1];
			}
		}

		// return center coordinates
		return new float[] { (bboxLongitude1 + bboxLongitude2) / 2, (bboxLatitude1 + bboxLatitude2) / 2 };
	}

	/**
	 * Calculate the length in pixel of the current way coordinates using the Euclidean distance for
	 * each way segment.
	 * 
	 * @return the length of the way in pixels.
	 */
	private double getWayLengthInPixel() {
		this.previousX = this.coordinates[0][0];
		this.previousY = this.coordinates[0][1];
		this.segmentLengthInPixel = 0;
		for (int i = 2; i < this.coordinates[0].length; i += 2) {
			this.currentX = this.coordinates[0][i];
			this.currentY = this.coordinates[0][i + 1];
			this.diffX = this.currentX - this.previousX;
			this.diffY = this.currentY - this.previousY;
			this.segmentLengthInPixel += Math.sqrt(this.diffX * this.diffX + this.diffY * this.diffY);
			this.previousX = this.currentX;
			this.previousY = this.currentY;
		}
		return this.segmentLengthInPixel;
	}

	/**
	 * Sets the style, color and stroke parameters for all paints.
	 */
	private void initializePaints() {
		PAINT_AERIALWAY.setStyle(Paint.Style.STROKE);
		PAINT_AERIALWAY.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AERIALWAY.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_AERIALWAY.setColor(Color.rgb(32, 32, 32));

		PAINT_AEROWAY_AERODROME_FILL.setStyle(Paint.Style.FILL);
		PAINT_AEROWAY_AERODROME_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AEROWAY_AERODROME_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_AEROWAY_AERODROME_FILL.setColor(Color.rgb(229, 224, 195));
		PAINT_AEROWAY_AERODROME_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_AEROWAY_AERODROME_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AEROWAY_AERODROME_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_AEROWAY_AERODROME_OUTLINE.setColor(Color.rgb(145, 140, 144));
		PAINT_AEROWAY_APRON_FILL.setStyle(Paint.Style.FILL);
		PAINT_AEROWAY_APRON_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AEROWAY_APRON_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_AEROWAY_APRON_FILL.setColor(Color.rgb(240, 240, 240));
		PAINT_AEROWAY_RUNWAY1.setStyle(Paint.Style.STROKE);
		PAINT_AEROWAY_RUNWAY1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AEROWAY_RUNWAY1.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_AEROWAY_RUNWAY1.setColor(Color.rgb(0, 0, 0));
		PAINT_AEROWAY_RUNWAY2.setStyle(Paint.Style.STROKE);
		PAINT_AEROWAY_RUNWAY2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AEROWAY_RUNWAY2.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_AEROWAY_RUNWAY2.setColor(Color.rgb(212, 220, 189));
		PAINT_AEROWAY_TAXIWAY1.setStyle(Paint.Style.STROKE);
		PAINT_AEROWAY_TAXIWAY1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AEROWAY_TAXIWAY1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_AEROWAY_TAXIWAY1.setColor(Color.rgb(0, 0, 0));
		PAINT_AEROWAY_TAXIWAY2.setStyle(Paint.Style.STROKE);
		PAINT_AEROWAY_TAXIWAY2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AEROWAY_TAXIWAY2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_AEROWAY_TAXIWAY2.setColor(Color.rgb(212, 220, 189));
		PAINT_AEROWAY_TERMINAL_FILL.setStyle(Paint.Style.FILL);
		PAINT_AEROWAY_TERMINAL_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AEROWAY_TERMINAL_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_AEROWAY_TERMINAL_FILL.setColor(Color.rgb(243, 214, 182));
		PAINT_AEROWAY_TERMINAL_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_AEROWAY_TERMINAL_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AEROWAY_TERMINAL_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_AEROWAY_TERMINAL_OUTLINE.setColor(Color.rgb(115, 100, 143));

		PAINT_AMENITY_GRAVE_YARD_FILL.setStyle(Paint.Style.FILL);
		PAINT_AMENITY_GRAVE_YARD_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AMENITY_GRAVE_YARD_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_AMENITY_GRAVE_YARD_FILL.setColor(Color.rgb(189, 227, 203));
		PAINT_AMENITY_HOSPITAL_FILL.setStyle(Paint.Style.FILL);
		PAINT_AMENITY_HOSPITAL_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AMENITY_HOSPITAL_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_AMENITY_HOSPITAL_FILL.setColor(Color.rgb(248, 248, 248));
		PAINT_AMENITY_PARKING_FILL.setStyle(Paint.Style.FILL);
		PAINT_AMENITY_PARKING_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AMENITY_PARKING_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_AMENITY_PARKING_FILL.setColor(Color.rgb(255, 255, 192));
		PAINT_AMENITY_PARKING_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_AMENITY_PARKING_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AMENITY_PARKING_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_AMENITY_PARKING_OUTLINE.setColor(Color.rgb(233, 221, 115));
		PAINT_AMENITY_SCHOOL_FILL.setStyle(Paint.Style.FILL);
		PAINT_AMENITY_SCHOOL_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AMENITY_SCHOOL_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_AMENITY_SCHOOL_FILL.setColor(Color.rgb(205, 171, 222));
		PAINT_AMENITY_SCHOOL_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_AMENITY_SCHOOL_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_AMENITY_SCHOOL_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_AMENITY_SCHOOL_OUTLINE.setColor(Color.rgb(233, 221, 115));

		PAINT_BARRIER_BOLLARD.setStyle(Paint.Style.FILL);
		PAINT_BARRIER_BOLLARD.setColor(Color.rgb(111, 111, 111));
		PAINT_BARRIER_WALL.setStyle(Paint.Style.STROKE);
		PAINT_BARRIER_WALL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_BARRIER_WALL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_BARRIER_WALL.setColor(Color.rgb(0, 0, 0));

		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_2.setStyle(Paint.Style.STROKE);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_2.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_2.setColor(Color.rgb(242, 100, 93));
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_4.setStyle(Paint.Style.STROKE);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_4.setStrokeJoin(Paint.Join.ROUND);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_4.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_4.setColor(Color.rgb(242, 100, 93));
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_6.setStyle(Paint.Style.STROKE);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_6.setStrokeJoin(Paint.Join.ROUND);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_6.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_6.setColor(Color.rgb(242, 100, 93));
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_8.setStyle(Paint.Style.STROKE);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_8.setStrokeJoin(Paint.Join.ROUND);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_8.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_8.setColor(Color.rgb(242, 100, 93));
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_9.setStyle(Paint.Style.STROKE);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_9.setStrokeJoin(Paint.Join.ROUND);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_9.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_9.setColor(Color.rgb(242, 100, 93));
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_10.setStyle(Paint.Style.STROKE);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_10.setStrokeJoin(Paint.Join.ROUND);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_10.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_10.setColor(Color.rgb(242, 100, 93));
		PAINT_BOUNDARY_NATIONAL_PARK.setStyle(Paint.Style.STROKE);
		PAINT_BOUNDARY_NATIONAL_PARK.setStrokeJoin(Paint.Join.ROUND);
		PAINT_BOUNDARY_NATIONAL_PARK.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_BOUNDARY_NATIONAL_PARK.setColor(Color.rgb(79, 248, 76));

		PAINT_BUILDING_ROOF_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_BUILDING_ROOF_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_BUILDING_ROOF_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_BUILDING_ROOF_OUTLINE.setColor(Color.rgb(115, 100, 143));
		PAINT_BUILDING_YES_FILL.setStyle(Paint.Style.FILL);
		PAINT_BUILDING_YES_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_BUILDING_YES_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_BUILDING_YES_FILL.setColor(Color.rgb(243, 214, 182));
		PAINT_BUILDING_YES_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_BUILDING_YES_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_BUILDING_YES_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_BUILDING_YES_OUTLINE.setColor(Color.rgb(115, 100, 143));

		PAINT_HIGHWAY_BRIDLEWAY1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_BRIDLEWAY1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_BRIDLEWAY1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_BRIDLEWAY1.setColor(Color.rgb(175, 212, 175));
		PAINT_HIGHWAY_BRIDLEWAY2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_BRIDLEWAY2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_BRIDLEWAY2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_BRIDLEWAY2.setColor(Color.rgb(112, 185, 113));
		PAINT_HIGHWAY_BYWAY.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_BYWAY.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_BYWAY.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_BYWAY.setColor(Color.rgb(239, 173, 170));
		PAINT_HIGHWAY_CONSTRUCTION.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_CONSTRUCTION.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_CONSTRUCTION.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_CONSTRUCTION.setColor(Color.rgb(208, 208, 209));
		PAINT_HIGHWAY_CYCLEWAY1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_CYCLEWAY1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_CYCLEWAY1.setStrokeCap(Paint.Cap.BUTT);
		PAINT_HIGHWAY_CYCLEWAY1.setColor(Color.rgb(136, 159, 139));
		PAINT_HIGHWAY_CYCLEWAY2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_CYCLEWAY2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_CYCLEWAY2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_CYCLEWAY2.setColor(Color.rgb(209, 234, 209));
		PAINT_HIGHWAY_FOOTWAY_AREA_FILL.setStyle(Paint.Style.FILL);
		PAINT_HIGHWAY_FOOTWAY_AREA_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_FOOTWAY_AREA_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_FOOTWAY_AREA_FILL.setColor(Color.rgb(255, 255, 255));
		PAINT_HIGHWAY_FOOTWAY_AREA_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_FOOTWAY_AREA_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_FOOTWAY_AREA_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_FOOTWAY_AREA_OUTLINE.setColor(Color.rgb(143, 144, 141));
		PAINT_HIGHWAY_FOOTWAY1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_FOOTWAY1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_FOOTWAY1.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_HIGHWAY_FOOTWAY1.setColor(Color.rgb(165, 166, 150));
		PAINT_HIGHWAY_FOOTWAY2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_FOOTWAY2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_FOOTWAY2.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_HIGHWAY_FOOTWAY2.setColor(Color.rgb(229, 224, 194));
		PAINT_HIGHWAY_LIVING_STREET1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_LIVING_STREET1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_LIVING_STREET1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_LIVING_STREET1.setColor(Color.rgb(194, 194, 194));
		PAINT_HIGHWAY_LIVING_STREET2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_LIVING_STREET2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_LIVING_STREET2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_LIVING_STREET2.setColor(Color.rgb(255, 255, 255));
		PAINT_HIGHWAY_MOTORWAY1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_MOTORWAY1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_MOTORWAY1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_MOTORWAY1.setColor(Color.rgb(80, 96, 119));
		PAINT_HIGHWAY_MOTORWAY2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_MOTORWAY2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_MOTORWAY2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_MOTORWAY2.setColor(Color.rgb(128, 155, 192));
		PAINT_HIGHWAY_MOTORWAY_LINK1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_MOTORWAY_LINK1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_MOTORWAY_LINK1.setStrokeCap(Paint.Cap.BUTT);
		PAINT_HIGHWAY_MOTORWAY_LINK1.setColor(Color.rgb(80, 96, 119));
		PAINT_HIGHWAY_MOTORWAY_LINK2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_MOTORWAY_LINK2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_MOTORWAY_LINK2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_MOTORWAY_LINK2.setColor(Color.rgb(128, 155, 192));
		PAINT_HIGHWAY_PATH1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_PATH1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_PATH1.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_HIGHWAY_PATH1.setColor(Color.rgb(128, 128, 128));
		PAINT_HIGHWAY_PATH2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_PATH2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_PATH2.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_HIGHWAY_PATH2.setColor(Color.rgb(208, 208, 208));
		PAINT_HIGHWAY_PEDESTRIAN1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_PEDESTRIAN1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_PEDESTRIAN1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_PEDESTRIAN1.setColor(Color.rgb(128, 128, 128));
		PAINT_HIGHWAY_PEDESTRIAN2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_PEDESTRIAN2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_PEDESTRIAN2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_PEDESTRIAN2.setColor(Color.rgb(237, 237, 237));
		PAINT_HIGHWAY_PEDESTRIAN_AREA_FILL.setStyle(Paint.Style.FILL);
		PAINT_HIGHWAY_PEDESTRIAN_AREA_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_PEDESTRIAN_AREA_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_PEDESTRIAN_AREA_FILL.setColor(Color.rgb(229, 224, 195));
		PAINT_HIGHWAY_PEDESTRIAN_AREA_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_PEDESTRIAN_AREA_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_PEDESTRIAN_AREA_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_PEDESTRIAN_AREA_OUTLINE.setColor(Color.rgb(145, 140, 144));
		PAINT_HIGHWAY_PRIMARY1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_PRIMARY1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_PRIMARY1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_PRIMARY1.setColor(Color.rgb(141, 67, 70));
		PAINT_HIGHWAY_PRIMARY2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_PRIMARY2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_PRIMARY2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_PRIMARY2.setColor(Color.rgb(228, 109, 113));
		PAINT_HIGHWAY_PRIMARY_LINK1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_PRIMARY_LINK1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_PRIMARY_LINK1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_PRIMARY_LINK1.setColor(Color.rgb(141, 67, 70));
		PAINT_HIGHWAY_PRIMARY_LINK2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_PRIMARY_LINK2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_PRIMARY_LINK2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_PRIMARY_LINK2.setColor(Color.rgb(228, 109, 113));
		PAINT_HIGHWAY_RESIDENTIAL1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_RESIDENTIAL1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_RESIDENTIAL1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_RESIDENTIAL1.setColor(Color.rgb(153, 153, 153));
		PAINT_HIGHWAY_RESIDENTIAL2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_RESIDENTIAL2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_RESIDENTIAL2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_RESIDENTIAL2.setColor(Color.rgb(255, 255, 255));
		PAINT_HIGHWAY_ROAD1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_ROAD1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_ROAD1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_ROAD1.setColor(Color.rgb(122, 128, 124));
		PAINT_HIGHWAY_ROAD2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_ROAD2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_ROAD2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_ROAD2.setColor(Color.rgb(208, 208, 208));
		PAINT_HIGHWAY_SECONDARY1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_SECONDARY1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_SECONDARY1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_SECONDARY1.setColor(Color.rgb(163, 123, 72));
		PAINT_HIGHWAY_SECONDARY2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_SECONDARY2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_SECONDARY2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_SECONDARY2.setColor(Color.rgb(253, 191, 111));
		PAINT_HIGHWAY_SECONDARY_LINK1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_SECONDARY_LINK1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_SECONDARY_LINK1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_SECONDARY_LINK1.setColor(Color.rgb(163, 123, 72));
		PAINT_HIGHWAY_SECONDARY_LINK2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_SECONDARY_LINK2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_SECONDARY_LINK2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_SECONDARY_LINK2.setColor(Color.rgb(253, 191, 111));
		PAINT_HIGHWAY_SERVICE_AREA_FILL.setStyle(Paint.Style.FILL);
		PAINT_HIGHWAY_SERVICE_AREA_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_SERVICE_AREA_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_SERVICE_AREA_FILL.setColor(Color.rgb(255, 255, 255));
		PAINT_HIGHWAY_SERVICE_AREA_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_SERVICE_AREA_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_SERVICE_AREA_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_SERVICE_AREA_OUTLINE.setColor(Color.rgb(143, 144, 141));
		PAINT_HIGHWAY_SERVICE1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_SERVICE1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_SERVICE1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_SERVICE1.setColor(Color.rgb(126, 126, 126));
		PAINT_HIGHWAY_SERVICE2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_SERVICE2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_SERVICE2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_SERVICE2.setColor(Color.rgb(255, 255, 255));
		PAINT_HIGHWAY_STEPS1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_STEPS1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_STEPS1.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_HIGHWAY_STEPS1.setColor(Color.rgb(123, 126, 119));
		PAINT_HIGHWAY_STEPS2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_STEPS2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_STEPS2.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_HIGHWAY_STEPS2.setColor(Color.rgb(229, 224, 195));
		PAINT_HIGHWAY_TERTIARY1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_TERTIARY1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_TERTIARY1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_TERTIARY1.setColor(Color.rgb(153, 153, 153));
		PAINT_HIGHWAY_TERTIARY2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_TERTIARY2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_TERTIARY2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_TERTIARY2.setColor(Color.rgb(247, 244, 150));
		PAINT_HIGHWAY_TRACK1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_TRACK1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_TRACK1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_TRACK1.setColor(Color.rgb(177, 188, 126));
		PAINT_HIGHWAY_TRACK2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_TRACK2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_TRACK2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_TRACK2.setColor(Color.rgb(255, 255, 255));
		PAINT_HIGHWAY_TRUNK1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_TRUNK1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_TRUNK1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_TRUNK1.setColor(Color.rgb(71, 113, 71));
		PAINT_HIGHWAY_TRUNK2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_TRUNK2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_TRUNK2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_TRUNK2.setColor(Color.rgb(127, 201, 127));
		PAINT_HIGHWAY_TRUNK_LINK1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_TRUNK_LINK1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_TRUNK_LINK1.setStrokeCap(Paint.Cap.BUTT);
		PAINT_HIGHWAY_TRUNK_LINK1.setColor(Color.rgb(71, 113, 71));
		PAINT_HIGHWAY_TRUNK_LINK2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_TRUNK_LINK2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_TRUNK_LINK2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_TRUNK_LINK2.setColor(Color.rgb(127, 201, 127));
		PAINT_HIGHWAY_TUNNEL.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_TUNNEL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_TUNNEL.setStrokeCap(Paint.Cap.BUTT);
		PAINT_HIGHWAY_TUNNEL.setColor(Color.argb(150, 131, 131, 131));
		PAINT_HIGHWAY_UNCLASSIFIED1.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_UNCLASSIFIED1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_UNCLASSIFIED1.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_UNCLASSIFIED1.setColor(Color.rgb(126, 126, 126));
		PAINT_HIGHWAY_UNCLASSIFIED2.setStyle(Paint.Style.STROKE);
		PAINT_HIGHWAY_UNCLASSIFIED2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_HIGHWAY_UNCLASSIFIED2.setStrokeCap(Paint.Cap.ROUND);
		PAINT_HIGHWAY_UNCLASSIFIED2.setColor(Color.rgb(255, 255, 255));

		PAINT_HISTORIC_CIRCLE_INNER.setStyle(Paint.Style.FILL);
		PAINT_HISTORIC_CIRCLE_INNER.setColor(Color.rgb(64, 64, 254));
		PAINT_HISTORIC_CIRCLE_OUTER.setStyle(Paint.Style.STROKE);
		PAINT_HISTORIC_CIRCLE_OUTER.setColor(Color.rgb(90, 90, 90));
		PAINT_HISTORIC_CIRCLE_OUTER.setStrokeWidth(2);

		PAINT_LANDUSE_ALLOTMENTS_FILL.setStyle(Paint.Style.FILL);
		PAINT_LANDUSE_ALLOTMENTS_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_ALLOTMENTS_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_ALLOTMENTS_FILL.setColor(Color.rgb(189, 227, 203));
		PAINT_LANDUSE_ALLOTMENTS_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_LANDUSE_ALLOTMENTS_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_ALLOTMENTS_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_ALLOTMENTS_OUTLINE.setColor(Color.rgb(112, 194, 63));
		PAINT_LANDUSE_BASIN_FILL.setStyle(Paint.Style.FILL);
		PAINT_LANDUSE_BASIN_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_BASIN_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_BASIN_FILL.setColor(Color.rgb(180, 213, 240));
		PAINT_LANDUSE_CEMETERY_FILL.setStyle(Paint.Style.FILL);
		PAINT_LANDUSE_CEMETERY_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_CEMETERY_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_CEMETERY_FILL.setColor(Color.rgb(189, 227, 203));
		PAINT_LANDUSE_CEMETERY_PATTERN.setShader(this.mapPatterns.cemeteryShader);
		PAINT_LANDUSE_COMMERCIAL_FILL.setStyle(Paint.Style.FILL);
		PAINT_LANDUSE_COMMERCIAL_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_COMMERCIAL_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_COMMERCIAL_FILL.setColor(Color.rgb(255, 254, 192));
		PAINT_LANDUSE_COMMERCIAL_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_LANDUSE_COMMERCIAL_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_COMMERCIAL_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_COMMERCIAL_OUTLINE.setColor(Color.rgb(228, 228, 228));
		PAINT_LANDUSE_CONSTRUCTION_FILL.setStyle(Paint.Style.FILL);
		PAINT_LANDUSE_CONSTRUCTION_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_CONSTRUCTION_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_CONSTRUCTION_FILL.setColor(Color.rgb(164, 124, 65));
		PAINT_LANDUSE_FARM_FILL.setStyle(Paint.Style.FILL);
		PAINT_LANDUSE_FARM_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_FARM_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_FARM_FILL.setColor(Color.rgb(189, 227, 203));
		PAINT_LANDUSE_FOREST_FILL.setStyle(Paint.Style.FILL);
		PAINT_LANDUSE_FOREST_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_FOREST_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_FOREST_FILL.setColor(Color.rgb(114, 191, 129));
		PAINT_LANDUSE_GRASS_FILL.setStyle(Paint.Style.FILL);
		PAINT_LANDUSE_GRASS_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_GRASS_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_GRASS_FILL.setColor(Color.rgb(189, 227, 203));
		PAINT_LANDUSE_GRASS_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_LANDUSE_GRASS_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_GRASS_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_GRASS_OUTLINE.setColor(Color.rgb(112, 193, 62));
		PAINT_LANDUSE_INDUSTRIAL_FILL.setStyle(Paint.Style.FILL);
		PAINT_LANDUSE_INDUSTRIAL_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_INDUSTRIAL_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_INDUSTRIAL_FILL.setColor(Color.rgb(235, 215, 254));
		PAINT_LANDUSE_MILITARY_FILL.setStyle(Paint.Style.FILL);
		PAINT_LANDUSE_MILITARY_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_MILITARY_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_MILITARY_FILL.setColor(Color.rgb(208, 208, 80));
		PAINT_LANDUSE_RESIDENTIAL_FILL.setStyle(Paint.Style.FILL);
		PAINT_LANDUSE_RESIDENTIAL_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_RESIDENTIAL_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_RESIDENTIAL_FILL.setColor(Color.rgb(228, 228, 228));
		PAINT_LANDUSE_RETAIL_FILL.setStyle(Paint.Style.FILL);
		PAINT_LANDUSE_RETAIL_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LANDUSE_RETAIL_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LANDUSE_RETAIL_FILL.setColor(Color.rgb(254, 234, 234));

		PAINT_LEISURE_COMMON_FILL.setStyle(Paint.Style.FILL);
		PAINT_LEISURE_COMMON_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LEISURE_COMMON_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LEISURE_COMMON_FILL.setColor(Color.rgb(199, 241, 163));
		PAINT_LEISURE_COMMON_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_LEISURE_COMMON_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LEISURE_COMMON_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LEISURE_COMMON_OUTLINE.setColor(Color.rgb(123, 200, 145));
		PAINT_LEISURE_NATURE_RESERVE_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_LEISURE_NATURE_RESERVE_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LEISURE_NATURE_RESERVE_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LEISURE_NATURE_RESERVE_OUTLINE.setColor(Color.rgb(112, 193, 142));
		PAINT_LEISURE_NATURE_RESERVE_PATTERN.setShader(this.mapPatterns.natureReserveShader);
		PAINT_LEISURE_STADIUM_FILL.setStyle(Paint.Style.FILL);
		PAINT_LEISURE_STADIUM_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LEISURE_STADIUM_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LEISURE_STADIUM_FILL.setColor(Color.rgb(189, 227, 203));
		PAINT_LEISURE_STADIUM_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_LEISURE_STADIUM_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_LEISURE_STADIUM_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_LEISURE_STADIUM_OUTLINE.setColor(Color.rgb(123, 200, 145));

		PAINT_MAN_MADE_PIER.setStyle(Paint.Style.STROKE);
		PAINT_MAN_MADE_PIER.setStrokeJoin(Paint.Join.ROUND);
		PAINT_MAN_MADE_PIER.setStrokeCap(Paint.Cap.ROUND);
		PAINT_MAN_MADE_PIER.setColor(Color.rgb(228, 228, 228));

		PAINT_MILITARY_BARRACKS_FILL.setStyle(Paint.Style.FILL);
		PAINT_MILITARY_BARRACKS_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_MILITARY_BARRACKS_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_MILITARY_BARRACKS_FILL.setColor(Color.rgb(208, 208, 80));
		PAINT_MILITARY_NAVAL_BASE_FILL.setStyle(Paint.Style.FILL);
		PAINT_MILITARY_NAVAL_BASE_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_MILITARY_NAVAL_BASE_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_MILITARY_NAVAL_BASE_FILL.setColor(Color.rgb(181, 214, 241));
		PAINT_MILITARY_PATTERN.setShader(this.mapPatterns.militaryShader);

		PAINT_NAME_BLACK_TINY.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_BLACK_TINY.setTextAlign(Align.LEFT);
		PAINT_NAME_BLACK_TINY.setColor(Color.rgb(0, 0, 0));
		PAINT_NAME_BLACK_TINY_CENTER.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_BLACK_TINY_CENTER.setTextAlign(Align.CENTER);
		PAINT_NAME_BLACK_TINY_CENTER.setColor(Color.rgb(0, 0, 0));
		PAINT_NAME_BLACK_SMALL.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_BLACK_SMALL.setTextAlign(Align.LEFT);
		PAINT_NAME_BLACK_SMALL.setColor(Color.rgb(0, 0, 0));
		PAINT_NAME_BLACK_NORMAL.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_BLACK_NORMAL.setTextAlign(Align.LEFT);
		PAINT_NAME_BLACK_NORMAL.setColor(Color.rgb(0, 0, 0));
		PAINT_NAME_BLACK_LARGE.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_BLACK_LARGE.setTextAlign(Align.LEFT);
		PAINT_NAME_BLACK_LARGE.setColor(Color.rgb(0, 0, 0));
		PAINT_NAME_BLACK_LARGER.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_BLACK_LARGER.setTextAlign(Align.LEFT);
		PAINT_NAME_BLACK_LARGER.setColor(Color.rgb(0, 0, 0));
		PAINT_NAME_BLACK_HUGE.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_BLACK_HUGE.setTextAlign(Align.LEFT);
		PAINT_NAME_BLACK_HUGE.setColor(Color.rgb(0, 0, 0));
		PAINT_NAME_BLUE_TINY.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_BLUE_TINY.setTextAlign(Align.LEFT);
		PAINT_NAME_BLUE_TINY.setColor(Color.rgb(64, 64, 254));
		PAINT_NAME_PURPLE_TINY.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_PURPLE_TINY.setTextAlign(Align.LEFT);
		PAINT_NAME_PURPLE_TINY.setColor(Color.rgb(255, 4, 255));
		PAINT_NAME_RED_TINY.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_RED_TINY.setTextAlign(Align.LEFT);
		PAINT_NAME_RED_TINY.setColor(Color.rgb(236, 46, 46));
		PAINT_NAME_RED_SMALLER.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_RED_SMALLER.setTextAlign(Align.LEFT);
		PAINT_NAME_RED_SMALLER.setColor(Color.rgb(236, 46, 46));
		PAINT_NAME_RED_NORMAL.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_RED_NORMAL.setTextAlign(Align.LEFT);
		PAINT_NAME_RED_NORMAL.setColor(Color.rgb(236, 46, 46));
		PAINT_NAME_WHITE_STROKE_TINY.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_WHITE_STROKE_TINY.setTextAlign(Align.LEFT);
		PAINT_NAME_WHITE_STROKE_TINY.setStyle(Paint.Style.STROKE);
		PAINT_NAME_WHITE_STROKE_TINY.setStrokeWidth(3);
		PAINT_NAME_WHITE_STROKE_TINY.setColor(Color.rgb(255, 255, 255));
		PAINT_NAME_WHITE_STROKE_TINY_CENTER.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_WHITE_STROKE_TINY_CENTER.setTextAlign(Align.CENTER);
		PAINT_NAME_WHITE_STROKE_TINY_CENTER.setStyle(Paint.Style.STROKE);
		PAINT_NAME_WHITE_STROKE_TINY_CENTER.setStrokeWidth(2);
		PAINT_NAME_WHITE_STROKE_TINY_CENTER.setColor(Color.rgb(255, 255, 255));
		PAINT_NAME_WHITE_STROKE_SMALLER.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_WHITE_STROKE_SMALLER.setTextAlign(Align.LEFT);
		PAINT_NAME_WHITE_STROKE_SMALLER.setStyle(Paint.Style.STROKE);
		PAINT_NAME_WHITE_STROKE_SMALLER.setStrokeWidth(3);
		PAINT_NAME_WHITE_STROKE_SMALLER.setColor(Color.rgb(255, 255, 255));
		PAINT_NAME_WHITE_STROKE_SMALL.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_WHITE_STROKE_SMALL.setTextAlign(Align.LEFT);
		PAINT_NAME_WHITE_STROKE_SMALL.setStyle(Paint.Style.STROKE);
		PAINT_NAME_WHITE_STROKE_SMALL.setStrokeWidth(3);
		PAINT_NAME_WHITE_STROKE_SMALL.setColor(Color.rgb(255, 255, 255));
		PAINT_NAME_WHITE_STROKE_NORMAL.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_WHITE_STROKE_NORMAL.setTextAlign(Align.LEFT);
		PAINT_NAME_WHITE_STROKE_NORMAL.setStyle(Paint.Style.STROKE);
		PAINT_NAME_WHITE_STROKE_NORMAL.setStrokeWidth(3);
		PAINT_NAME_WHITE_STROKE_NORMAL.setColor(Color.rgb(255, 255, 255));
		PAINT_NAME_WHITE_STROKE_LARGE.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_WHITE_STROKE_LARGE.setTextAlign(Align.LEFT);
		PAINT_NAME_WHITE_STROKE_LARGE.setStyle(Paint.Style.STROKE);
		PAINT_NAME_WHITE_STROKE_LARGE.setStrokeWidth(3);
		PAINT_NAME_WHITE_STROKE_LARGE.setColor(Color.rgb(255, 255, 255));
		PAINT_NAME_WHITE_STROKE_LARGER.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_WHITE_STROKE_LARGER.setTextAlign(Align.LEFT);
		PAINT_NAME_WHITE_STROKE_LARGER.setStyle(Paint.Style.STROKE);
		PAINT_NAME_WHITE_STROKE_LARGER.setStrokeWidth(3);
		PAINT_NAME_WHITE_STROKE_LARGER.setColor(Color.rgb(255, 255, 255));
		PAINT_NAME_WHITE_STROKE_HUGE.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		PAINT_NAME_WHITE_STROKE_HUGE.setTextAlign(Align.LEFT);
		PAINT_NAME_WHITE_STROKE_HUGE.setStyle(Paint.Style.STROKE);
		PAINT_NAME_WHITE_STROKE_HUGE.setStrokeWidth(3);
		PAINT_NAME_WHITE_STROKE_HUGE.setColor(Color.rgb(255, 255, 255));

		PAINT_NATURAL_BEACH_FILL.setStyle(Paint.Style.FILL);
		PAINT_NATURAL_BEACH_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_NATURAL_BEACH_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_NATURAL_BEACH_FILL.setColor(Color.rgb(238, 204, 85));
		PAINT_NATURAL_COASTLINE.setStyle(Paint.Style.STROKE);
		PAINT_NATURAL_COASTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_NATURAL_COASTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_NATURAL_COASTLINE.setColor(Color.rgb(181, 214, 241));
		PAINT_NATURAL_COASTLINE_INVALID.setStyle(Paint.Style.STROKE);
		PAINT_NATURAL_COASTLINE_INVALID.setStrokeJoin(Paint.Join.ROUND);
		PAINT_NATURAL_COASTLINE_INVALID.setStrokeCap(Paint.Cap.ROUND);
		PAINT_NATURAL_COASTLINE_INVALID.setColor(Color.rgb(112, 133, 153));
		PAINT_NATURAL_COASTLINE_VALID.setStyle(Paint.Style.STROKE);
		PAINT_NATURAL_COASTLINE_VALID.setStrokeJoin(Paint.Join.ROUND);
		PAINT_NATURAL_COASTLINE_VALID.setStrokeCap(Paint.Cap.ROUND);
		PAINT_NATURAL_COASTLINE_VALID.setColor(Color.rgb(181, 214, 241));
		PAINT_NATURAL_GLACIER_FILL.setStyle(Paint.Style.FILL);
		PAINT_NATURAL_GLACIER_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_NATURAL_GLACIER_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_NATURAL_GLACIER_FILL.setColor(Color.rgb(250, 250, 255));
		PAINT_NATURAL_GLACIER_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_NATURAL_GLACIER_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_NATURAL_GLACIER_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_NATURAL_GLACIER_OUTLINE.setColor(Color.rgb(182, 220, 233));
		PAINT_NATURAL_HEATH_FILL.setStyle(Paint.Style.FILL);
		PAINT_NATURAL_HEATH_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_NATURAL_HEATH_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_NATURAL_HEATH_FILL.setColor(Color.rgb(255, 255, 192));
		PAINT_NATURAL_LAND_FILL.setStyle(Paint.Style.FILL);
		PAINT_NATURAL_LAND_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_NATURAL_LAND_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_NATURAL_LAND_FILL.setColor(Color.rgb(248, 248, 248));
		PAINT_NATURAL_MARSH_PATTERN.setShader(this.mapPatterns.marshShader);
		PAINT_NATURAL_WATER_FILL.setStyle(Paint.Style.FILL);
		PAINT_NATURAL_WATER_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_NATURAL_WATER_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_NATURAL_WATER_FILL.setColor(Color.rgb(181, 214, 241));
		PAINT_NATURAL_WOOD_FILL.setStyle(Paint.Style.FILL);
		PAINT_NATURAL_WOOD_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_NATURAL_WOOD_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_NATURAL_WOOD_FILL.setColor(Color.rgb(114, 191, 129));

		PAINT_PISTE_TYPE_DOWNHILL_ADVANCED.setStyle(Paint.Style.STROKE);
		PAINT_PISTE_TYPE_DOWNHILL_ADVANCED.setStrokeJoin(Paint.Join.ROUND);
		PAINT_PISTE_TYPE_DOWNHILL_ADVANCED.setStrokeCap(Paint.Cap.ROUND);
		PAINT_PISTE_TYPE_DOWNHILL_ADVANCED.setColor(Color.argb(136, 0, 0, 0));
		PAINT_PISTE_TYPE_DOWNHILL_EASY.setStyle(Paint.Style.STROKE);
		PAINT_PISTE_TYPE_DOWNHILL_EASY.setStrokeJoin(Paint.Join.ROUND);
		PAINT_PISTE_TYPE_DOWNHILL_EASY.setStrokeCap(Paint.Cap.ROUND);
		PAINT_PISTE_TYPE_DOWNHILL_EASY.setColor(Color.argb(136, 64, 64, 255));
		PAINT_PISTE_TYPE_DOWNHILL_EXPERT.setStyle(Paint.Style.STROKE);
		PAINT_PISTE_TYPE_DOWNHILL_EXPERT.setStrokeJoin(Paint.Join.ROUND);
		PAINT_PISTE_TYPE_DOWNHILL_EXPERT.setStrokeCap(Paint.Cap.ROUND);
		PAINT_PISTE_TYPE_DOWNHILL_EXPERT.setColor(Color.argb(136, 246, 128, 10));
		PAINT_PISTE_TYPE_DOWNHILL_FREERIDE.setStyle(Paint.Style.STROKE);
		PAINT_PISTE_TYPE_DOWNHILL_FREERIDE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_PISTE_TYPE_DOWNHILL_FREERIDE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_PISTE_TYPE_DOWNHILL_FREERIDE.setColor(Color.argb(136, 246, 221, 10));
		PAINT_PISTE_TYPE_DOWNHILL_INTERMEDIATE.setStyle(Paint.Style.STROKE);
		PAINT_PISTE_TYPE_DOWNHILL_INTERMEDIATE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_PISTE_TYPE_DOWNHILL_INTERMEDIATE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_PISTE_TYPE_DOWNHILL_INTERMEDIATE.setColor(Color.argb(136, 255, 64, 64));
		PAINT_PISTE_TYPE_DOWNHILL_NOVICE.setStyle(Paint.Style.STROKE);
		PAINT_PISTE_TYPE_DOWNHILL_NOVICE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_PISTE_TYPE_DOWNHILL_NOVICE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_PISTE_TYPE_DOWNHILL_NOVICE.setColor(Color.argb(136, 64, 255, 64));
		PAINT_PISTE_TYPE_NORDIC.setStyle(Paint.Style.STROKE);
		PAINT_PISTE_TYPE_NORDIC.setStrokeJoin(Paint.Join.ROUND);
		PAINT_PISTE_TYPE_NORDIC.setStrokeCap(Paint.Cap.BUTT);
		PAINT_PISTE_TYPE_NORDIC.setColor(Color.rgb(192, 0, 0));

		PAINT_RAILWAY_CIRCLE_INNER.setStyle(Paint.Style.FILL);
		PAINT_RAILWAY_CIRCLE_INNER.setColor(Color.rgb(236, 46, 46));
		PAINT_RAILWAY_CIRCLE_OUTER.setStyle(Paint.Style.STROKE);
		PAINT_RAILWAY_CIRCLE_OUTER.setColor(Color.rgb(90, 90, 90));
		PAINT_RAILWAY_CIRCLE_OUTER.setStrokeWidth(2);
		PAINT_RAILWAY_LIGHT_RAIL1.setStyle(Paint.Style.STROKE);
		PAINT_RAILWAY_LIGHT_RAIL1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_RAILWAY_LIGHT_RAIL1.setStrokeCap(Paint.Cap.BUTT);
		PAINT_RAILWAY_LIGHT_RAIL1.setColor(Color.rgb(181, 228, 227));
		PAINT_RAILWAY_LIGHT_RAIL2.setStyle(Paint.Style.STROKE);
		PAINT_RAILWAY_LIGHT_RAIL2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_RAILWAY_LIGHT_RAIL2.setStrokeCap(Paint.Cap.BUTT);
		PAINT_RAILWAY_LIGHT_RAIL2.setColor(Color.rgb(16, 77, 17));
		PAINT_RAILWAY_RAIL_TUNNEL.setStyle(Paint.Style.STROKE);
		PAINT_RAILWAY_RAIL_TUNNEL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_RAILWAY_RAIL_TUNNEL.setStrokeCap(Paint.Cap.BUTT);
		PAINT_RAILWAY_RAIL_TUNNEL.setColor(Color.argb(150, 153, 156, 153));
		PAINT_RAILWAY_RAIL1.setStyle(Paint.Style.STROKE);
		PAINT_RAILWAY_RAIL1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_RAILWAY_RAIL1.setStrokeCap(Paint.Cap.BUTT);
		PAINT_RAILWAY_RAIL1.setColor(Color.rgb(230, 230, 231));
		PAINT_RAILWAY_RAIL2.setStyle(Paint.Style.STROKE);
		PAINT_RAILWAY_RAIL2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_RAILWAY_RAIL2.setStrokeCap(Paint.Cap.BUTT);
		PAINT_RAILWAY_RAIL2.setColor(Color.rgb(52, 50, 50));
		PAINT_RAILWAY_STATION_FILL.setStyle(Paint.Style.FILL);
		PAINT_RAILWAY_STATION_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_RAILWAY_STATION_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_RAILWAY_STATION_FILL.setColor(Color.rgb(243, 214, 182));
		PAINT_RAILWAY_STATION_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_RAILWAY_STATION_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_RAILWAY_STATION_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_RAILWAY_STATION_OUTLINE.setColor(Color.rgb(115, 100, 143));
		PAINT_RAILWAY_SUBWAY1.setStyle(Paint.Style.STROKE);
		PAINT_RAILWAY_SUBWAY1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_RAILWAY_SUBWAY1.setStrokeCap(Paint.Cap.BUTT);
		PAINT_RAILWAY_SUBWAY1.setColor(Color.rgb(183, 183, 229));
		PAINT_RAILWAY_SUBWAY2.setStyle(Paint.Style.STROKE);
		PAINT_RAILWAY_SUBWAY2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_RAILWAY_SUBWAY2.setStrokeCap(Paint.Cap.BUTT);
		PAINT_RAILWAY_SUBWAY2.setColor(Color.rgb(25, 24, 91));
		PAINT_RAILWAY_SUBWAY_TUNNEL.setStyle(Paint.Style.STROKE);
		PAINT_RAILWAY_SUBWAY_TUNNEL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_RAILWAY_SUBWAY_TUNNEL.setStrokeCap(Paint.Cap.BUTT);
		PAINT_RAILWAY_SUBWAY_TUNNEL.setColor(Color.argb(150, 165, 162, 184));
		PAINT_RAILWAY_TRAM1.setStyle(Paint.Style.STROKE);
		PAINT_RAILWAY_TRAM1.setStrokeJoin(Paint.Join.ROUND);
		PAINT_RAILWAY_TRAM1.setStrokeCap(Paint.Cap.BUTT);
		PAINT_RAILWAY_TRAM1.setColor(Color.rgb(229, 183, 229));
		PAINT_RAILWAY_TRAM2.setStyle(Paint.Style.STROKE);
		PAINT_RAILWAY_TRAM2.setStrokeJoin(Paint.Join.ROUND);
		PAINT_RAILWAY_TRAM2.setStrokeCap(Paint.Cap.BUTT);
		PAINT_RAILWAY_TRAM2.setColor(Color.rgb(77, 16, 76));

		PAINT_ROUTE_FERRY.setStyle(Paint.Style.STROKE);
		PAINT_ROUTE_FERRY.setStrokeJoin(Paint.Join.ROUND);
		PAINT_ROUTE_FERRY.setStrokeCap(Paint.Cap.SQUARE);
		PAINT_ROUTE_FERRY.setColor(Color.rgb(113, 113, 113));

		PAINT_SPORT_SHOOTING_FILL.setStyle(Paint.Style.FILL);
		PAINT_SPORT_SHOOTING_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_SPORT_SHOOTING_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_SPORT_SHOOTING_FILL.setColor(Color.rgb(189, 227, 203));
		PAINT_SPORT_SHOOTING_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_SPORT_SHOOTING_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_SPORT_SHOOTING_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_SPORT_SHOOTING_OUTLINE.setColor(Color.rgb(112, 193, 143));
		PAINT_SPORT_SWIMMING_FILL.setStyle(Paint.Style.FILL);
		PAINT_SPORT_SWIMMING_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_SPORT_SWIMMING_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_SPORT_SWIMMING_FILL.setColor(Color.rgb(181, 214, 241));
		PAINT_SPORT_SWIMMING_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_SPORT_SWIMMING_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_SPORT_SWIMMING_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_SPORT_SWIMMING_OUTLINE.setColor(Color.rgb(97, 97, 255));
		PAINT_SPORT_TENNIS_FILL.setStyle(Paint.Style.FILL);
		PAINT_SPORT_TENNIS_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_SPORT_TENNIS_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_SPORT_TENNIS_FILL.setColor(Color.rgb(209, 138, 106));
		PAINT_SPORT_TENNIS_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_SPORT_TENNIS_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_SPORT_TENNIS_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_SPORT_TENNIS_OUTLINE.setColor(Color.rgb(178, 108, 77));

		PAINT_TOURISM_ATTRACTION_FILL.setStyle(Paint.Style.FILL);
		PAINT_TOURISM_ATTRACTION_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_TOURISM_ATTRACTION_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_TOURISM_ATTRACTION_FILL.setColor(Color.rgb(242, 202, 234));
		PAINT_TOURISM_ZOO_FILL.setStyle(Paint.Style.FILL);
		PAINT_TOURISM_ZOO_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_TOURISM_ZOO_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_TOURISM_ZOO_FILL.setColor(Color.rgb(199, 241, 163));
		PAINT_TOURISM_ZOO_OUTLINE.setStyle(Paint.Style.STROKE);
		PAINT_TOURISM_ZOO_OUTLINE.setStrokeJoin(Paint.Join.ROUND);
		PAINT_TOURISM_ZOO_OUTLINE.setStrokeCap(Paint.Cap.ROUND);
		PAINT_TOURISM_ZOO_OUTLINE.setColor(Color.rgb(123, 200, 145));

		PAINT_WATER_TILE_HIGHTLIGHT.setStyle(Paint.Style.FILL);
		PAINT_WATER_TILE_HIGHTLIGHT.setStrokeJoin(Paint.Join.ROUND);
		PAINT_WATER_TILE_HIGHTLIGHT.setStrokeCap(Paint.Cap.ROUND);
		PAINT_WATER_TILE_HIGHTLIGHT.setColor(Color.CYAN);

		PAINT_WATERWAY_CANAL.setStyle(Paint.Style.STROKE);
		PAINT_WATERWAY_CANAL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_WATERWAY_CANAL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_WATERWAY_CANAL.setColor(Color.rgb(179, 213, 241));
		PAINT_WATERWAY_RIVER.setStyle(Paint.Style.STROKE);
		PAINT_WATERWAY_RIVER.setStrokeJoin(Paint.Join.ROUND);
		PAINT_WATERWAY_RIVER.setStrokeCap(Paint.Cap.ROUND);
		PAINT_WATERWAY_RIVER.setColor(Color.rgb(179, 213, 241));
		PAINT_WATERWAY_RIVERBANK_FILL.setStyle(Paint.Style.FILL);
		PAINT_WATERWAY_RIVERBANK_FILL.setStrokeJoin(Paint.Join.ROUND);
		PAINT_WATERWAY_RIVERBANK_FILL.setStrokeCap(Paint.Cap.ROUND);
		PAINT_WATERWAY_RIVERBANK_FILL.setColor(Color.rgb(179, 213, 241));
		PAINT_WATERWAY_STREAM.setStyle(Paint.Style.STROKE);
		PAINT_WATERWAY_STREAM.setStrokeJoin(Paint.Join.ROUND);
		PAINT_WATERWAY_STREAM.setStrokeCap(Paint.Cap.ROUND);
		PAINT_WATERWAY_STREAM.setColor(Color.rgb(179, 213, 241));

		PAINT_WOOD_CONIFEROUS_PATTERN.setShader(this.mapPatterns.woodConiferousShader);
		PAINT_WOOD_DECIDUOUS_PATTERN.setShader(this.mapPatterns.woodDeciduousShader);
		PAINT_WOOD_MIXED_PATTERN.setShader(this.mapPatterns.woodMixedShader);
	}

	/**
	 * Converts a latitude value into an Y coordinate on the current tile.
	 * 
	 * @param latitude
	 *            the latitude value.
	 * @return the Y coordinate on the current tile.
	 */
	private float scaleLatitude(int latitude) {
		return (float) (MercatorProjection.latitudeToPixelY(latitude / (double) 1000000,
				this.currentTile.zoomLevel) - this.currentTile.pixelY);
	}

	/**
	 * Converts a longitude value into an X coordinate on the current tile.
	 * 
	 * @param longitude
	 *            the longitude value.
	 * @return the X coordinate on the current tile.
	 */
	private float scaleLongitude(int longitude) {
		return (float) (MercatorProjection.longitudeToPixelX(longitude / (double) 1000000,
				this.currentTile.zoomLevel) - this.currentTile.pixelX);
	}

	/**
	 * Sets the font size of the paints according to the given text scale.
	 * 
	 * @param textScale
	 *            the text scale for map rendering.
	 */
	private void setPaintTextSize(float textScale) {
		PAINT_NAME_BLACK_TINY.setTextSize(TEXT_SCALE_TINY * textScale);
		PAINT_NAME_BLACK_TINY_CENTER.setTextSize(TEXT_SCALE_TINY * textScale);
		PAINT_NAME_BLACK_SMALL.setTextSize(TEXT_SCALE_SMALL * textScale);
		PAINT_NAME_BLACK_NORMAL.setTextSize(TEXT_SCALE_NORMAL * textScale);
		PAINT_NAME_BLACK_LARGE.setTextSize(TEXT_SCALE_LARGE * textScale);
		PAINT_NAME_BLACK_LARGER.setTextSize(TEXT_SCALE_LARGER * textScale);
		PAINT_NAME_BLACK_HUGE.setTextSize(TEXT_SCALE_HUGE * textScale);

		PAINT_NAME_BLUE_TINY.setTextSize(TEXT_SCALE_TINY * textScale);

		PAINT_NAME_PURPLE_TINY.setTextSize(TEXT_SCALE_TINY * textScale);

		PAINT_NAME_RED_TINY.setTextSize(TEXT_SCALE_TINY * textScale);
		PAINT_NAME_RED_SMALLER.setTextSize(TEXT_SCALE_SMALLER * textScale);
		PAINT_NAME_RED_NORMAL.setTextSize(TEXT_SCALE_NORMAL * textScale);

		PAINT_NAME_WHITE_STROKE_TINY.setTextSize(TEXT_SCALE_TINY * textScale);
		PAINT_NAME_WHITE_STROKE_TINY_CENTER.setTextSize(TEXT_SCALE_TINY * textScale);
		PAINT_NAME_WHITE_STROKE_SMALLER.setTextSize(TEXT_SCALE_SMALLER * textScale);
		PAINT_NAME_WHITE_STROKE_SMALL.setTextSize(TEXT_SCALE_SMALL * textScale);
		PAINT_NAME_WHITE_STROKE_NORMAL.setTextSize(TEXT_SCALE_NORMAL * textScale);
		PAINT_NAME_WHITE_STROKE_LARGE.setTextSize(TEXT_SCALE_LARGE * textScale);
		PAINT_NAME_WHITE_STROKE_LARGER.setTextSize(TEXT_SCALE_LARGER * textScale);
		PAINT_NAME_WHITE_STROKE_HUGE.setTextSize(TEXT_SCALE_HUGE * textScale);
	}

	/**
	 * Sets the stroke width and dash effects of the paints according to the given zoom level.
	 * 
	 * @param zoomLevel
	 *            the zoom level for which the properties should be set.
	 */
	private void setPaintZoomLevel(byte zoomLevel) {
		float paintScaleFactor;
		switch (zoomLevel) {
			case 22:
				paintScaleFactor = 54;
				break;
			case 21:
				paintScaleFactor = 42;
				break;
			case 20:
				paintScaleFactor = 30;
				break;
			case 19:
				paintScaleFactor = 20;
				break;
			case 18:
				paintScaleFactor = 12;
				break;
			case 17:
				paintScaleFactor = 8;
				break;
			case 16:
				paintScaleFactor = 6;
				break;
			case 15:
				paintScaleFactor = 4;
				break;
			case 14:
				paintScaleFactor = 2;
				break;
			case 13:
				paintScaleFactor = 1.5f;
				break;
			default:
				paintScaleFactor = 1;
				break;
		}

		PAINT_AERIALWAY.setStrokeWidth(0.5f * paintScaleFactor);

		PAINT_AEROWAY_AERODROME_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);
		PAINT_AEROWAY_RUNWAY1.setStrokeWidth(7.5f * paintScaleFactor);
		PAINT_AEROWAY_RUNWAY2.setStrokeWidth(5 * paintScaleFactor);
		PAINT_AEROWAY_TAXIWAY1.setStrokeWidth(4 * paintScaleFactor);
		PAINT_AEROWAY_TAXIWAY2.setStrokeWidth(3 * paintScaleFactor);
		PAINT_AEROWAY_TERMINAL_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);

		PAINT_AMENITY_SCHOOL_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);
		PAINT_AMENITY_PARKING_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);

		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_2.setStrokeWidth(1.5f * paintScaleFactor);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_2.setPathEffect(new DashPathEffect(new float[] {
				3 * paintScaleFactor, 2 * paintScaleFactor }, 0));
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_4.setStrokeWidth(1 * paintScaleFactor);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_4.setPathEffect(new DashPathEffect(
				new float[] { 3 * paintScaleFactor, 2 * paintScaleFactor, 1 * paintScaleFactor,
						2 * paintScaleFactor }, 0));
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_6.setStrokeWidth(0.3f * paintScaleFactor);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_6.setPathEffect(new DashPathEffect(new float[] {
				1 * paintScaleFactor, 4 * paintScaleFactor }, 0));
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_8.setStrokeWidth(0.3f * paintScaleFactor);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_8.setPathEffect(new DashPathEffect(
				new float[] { 3 * paintScaleFactor, 2 * paintScaleFactor, 1 * paintScaleFactor,
						2 * paintScaleFactor }, 0));
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_9.setStrokeWidth(0.3f * paintScaleFactor);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_9.setPathEffect(new DashPathEffect(new float[] {
				3 * paintScaleFactor, 2 * paintScaleFactor }, 0));
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_10.setStrokeWidth(0.3f * paintScaleFactor);
		PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_10.setPathEffect(new DashPathEffect(new float[] {
				1 * paintScaleFactor, 4 * paintScaleFactor }, 0));
		PAINT_BOUNDARY_NATIONAL_PARK.setStrokeWidth(1.5f * paintScaleFactor);
		PAINT_BOUNDARY_NATIONAL_PARK.setPathEffect(new DashPathEffect(new float[] {
				1 * paintScaleFactor, 4 * paintScaleFactor }, 0));

		PAINT_BUILDING_ROOF_OUTLINE.setStrokeWidth(0.1f * paintScaleFactor);
		PAINT_BUILDING_YES_OUTLINE.setStrokeWidth(0.2f * paintScaleFactor);

		PAINT_HIGHWAY_BRIDLEWAY1.setStrokeWidth(1.8f * paintScaleFactor);
		PAINT_HIGHWAY_BRIDLEWAY2.setStrokeWidth(1.5f * paintScaleFactor);
		PAINT_HIGHWAY_BYWAY.setStrokeWidth(1.2f * paintScaleFactor);
		PAINT_HIGHWAY_CONSTRUCTION.setStrokeWidth(1.3f * paintScaleFactor);
		PAINT_HIGHWAY_CYCLEWAY1.setStrokeWidth(1.1f * paintScaleFactor);
		PAINT_HIGHWAY_CYCLEWAY2.setStrokeWidth(0.8f * paintScaleFactor);
		PAINT_HIGHWAY_FOOTWAY1.setPathEffect(new DashPathEffect(new float[] { 1 * paintScaleFactor,
				1 * paintScaleFactor }, 0));
		PAINT_HIGHWAY_FOOTWAY1.setStrokeWidth(0.8f * paintScaleFactor);
		PAINT_HIGHWAY_FOOTWAY2.setStrokeWidth(0.5f * paintScaleFactor);
		PAINT_HIGHWAY_LIVING_STREET1.setStrokeWidth(1.8f * paintScaleFactor);
		PAINT_HIGHWAY_LIVING_STREET2.setStrokeWidth(1.5f * paintScaleFactor);
		PAINT_HIGHWAY_MOTORWAY_LINK1.setStrokeWidth(2.4f * paintScaleFactor);
		PAINT_HIGHWAY_MOTORWAY_LINK2.setStrokeWidth(2.1f * paintScaleFactor);
		PAINT_HIGHWAY_MOTORWAY1.setStrokeWidth(2.9f * paintScaleFactor);
		PAINT_HIGHWAY_MOTORWAY2.setStrokeWidth(2.6f * paintScaleFactor);
		PAINT_HIGHWAY_PATH1.setPathEffect(new DashPathEffect(new float[] { 1 * paintScaleFactor,
				1 * paintScaleFactor }, 0));
		PAINT_HIGHWAY_PATH1.setStrokeWidth(0.8f * paintScaleFactor);
		PAINT_HIGHWAY_PATH2.setStrokeWidth(0.5f * paintScaleFactor);
		PAINT_HIGHWAY_PEDESTRIAN_AREA_OUTLINE.setStrokeWidth(0.1f * paintScaleFactor);
		PAINT_HIGHWAY_PEDESTRIAN1.setStrokeWidth(1.8f * paintScaleFactor);
		PAINT_HIGHWAY_PEDESTRIAN2.setStrokeWidth(1.5f * paintScaleFactor);
		PAINT_HIGHWAY_PRIMARY_LINK1.setStrokeWidth(2.1f * paintScaleFactor);
		PAINT_HIGHWAY_PRIMARY_LINK2.setStrokeWidth(1.8f * paintScaleFactor);
		PAINT_HIGHWAY_PRIMARY1.setStrokeWidth(2.1f * paintScaleFactor);
		PAINT_HIGHWAY_PRIMARY2.setStrokeWidth(1.8f * paintScaleFactor);
		PAINT_HIGHWAY_RESIDENTIAL1.setStrokeWidth(1.8f * paintScaleFactor);
		PAINT_HIGHWAY_RESIDENTIAL2.setStrokeWidth(1.5f * paintScaleFactor);
		PAINT_HIGHWAY_ROAD1.setStrokeWidth(1.8f * paintScaleFactor);
		PAINT_HIGHWAY_ROAD2.setStrokeWidth(1.5f * paintScaleFactor);
		PAINT_HIGHWAY_SECONDARY_LINK1.setStrokeWidth(2 * paintScaleFactor);
		PAINT_HIGHWAY_SECONDARY_LINK2.setStrokeWidth(1.7f * paintScaleFactor);
		PAINT_HIGHWAY_SECONDARY1.setStrokeWidth(2 * paintScaleFactor);
		PAINT_HIGHWAY_SECONDARY2.setStrokeWidth(1.7f * paintScaleFactor);
		PAINT_HIGHWAY_SERVICE_AREA_OUTLINE.setStrokeWidth(0.1f * paintScaleFactor);
		PAINT_HIGHWAY_SERVICE1.setStrokeWidth(1.3f * paintScaleFactor);
		PAINT_HIGHWAY_SERVICE2.setStrokeWidth(1 * paintScaleFactor);
		PAINT_HIGHWAY_STEPS1.setPathEffect(new DashPathEffect(new float[] { 1 * paintScaleFactor,
				1 * paintScaleFactor }, 0));
		PAINT_HIGHWAY_STEPS1.setStrokeWidth(0.8f * paintScaleFactor);
		PAINT_HIGHWAY_STEPS2.setPathEffect(new DashPathEffect(new float[] { 1 * paintScaleFactor,
				1 * paintScaleFactor }, 3));
		PAINT_HIGHWAY_STEPS2.setStrokeWidth(0.5f * paintScaleFactor);
		PAINT_HIGHWAY_TERTIARY1.setStrokeWidth(1.8f * paintScaleFactor);
		PAINT_HIGHWAY_TERTIARY2.setStrokeWidth(1.5f * paintScaleFactor);
		PAINT_HIGHWAY_TRACK1.setStrokeWidth(1.8f * paintScaleFactor);
		PAINT_HIGHWAY_TRACK2.setStrokeWidth(1.5f * paintScaleFactor);
		PAINT_HIGHWAY_TRUNK_LINK1.setStrokeWidth(2.4f * paintScaleFactor);
		PAINT_HIGHWAY_TRUNK_LINK2.setStrokeWidth(2.1f * paintScaleFactor);
		PAINT_HIGHWAY_TRUNK1.setStrokeWidth(2.6f * paintScaleFactor);
		PAINT_HIGHWAY_TRUNK2.setStrokeWidth(2.3f * paintScaleFactor);
		PAINT_HIGHWAY_TUNNEL.setPathEffect(new DashPathEffect(new float[] { 1.5f * paintScaleFactor,
				1.5f * paintScaleFactor }, 0));
		PAINT_HIGHWAY_TUNNEL.setStrokeWidth(0.8f * paintScaleFactor);
		PAINT_HIGHWAY_UNCLASSIFIED1.setStrokeWidth(1.8f * paintScaleFactor);
		PAINT_HIGHWAY_UNCLASSIFIED2.setStrokeWidth(1.5f * paintScaleFactor);

		PAINT_LANDUSE_ALLOTMENTS_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);
		PAINT_LANDUSE_GRASS_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);

		PAINT_LEISURE_COMMON_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);
		PAINT_LEISURE_NATURE_RESERVE_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);
		PAINT_LEISURE_STADIUM_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);

		PAINT_MAN_MADE_PIER.setStrokeWidth(0.8f * paintScaleFactor);

		PAINT_NATURAL_COASTLINE.setStrokeWidth(1 * paintScaleFactor);
		PAINT_NATURAL_COASTLINE_INVALID.setStrokeWidth(2 * paintScaleFactor);
		PAINT_NATURAL_COASTLINE_VALID.setStrokeWidth(2 * paintScaleFactor);
		PAINT_NATURAL_GLACIER_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);

		PAINT_PISTE_TYPE_DOWNHILL_ADVANCED.setStrokeWidth(2.4f * paintScaleFactor);
		PAINT_PISTE_TYPE_DOWNHILL_EASY.setStrokeWidth(2.4f * paintScaleFactor);
		PAINT_PISTE_TYPE_DOWNHILL_EXPERT.setStrokeWidth(2.4f * paintScaleFactor);
		PAINT_PISTE_TYPE_DOWNHILL_FREERIDE.setStrokeWidth(2.4f * paintScaleFactor);
		PAINT_PISTE_TYPE_DOWNHILL_INTERMEDIATE.setStrokeWidth(2.4f * paintScaleFactor);
		PAINT_PISTE_TYPE_DOWNHILL_NOVICE.setStrokeWidth(2.4f * paintScaleFactor);
		PAINT_PISTE_TYPE_NORDIC.setPathEffect(new DashPathEffect(new float[] { 3 * paintScaleFactor,
				2 * paintScaleFactor }, 0));
		PAINT_PISTE_TYPE_NORDIC.setStrokeWidth(1.2f * paintScaleFactor);

		PAINT_RAILWAY_RAIL_TUNNEL.setPathEffect(new DashPathEffect(new float[] {
				1.5f * paintScaleFactor, 1.5f * paintScaleFactor }, 0));
		PAINT_RAILWAY_RAIL_TUNNEL.setStrokeWidth(0.5f * paintScaleFactor);
		PAINT_RAILWAY_RAIL1.setPathEffect(new DashPathEffect(new float[] { 2 * paintScaleFactor,
				2 * paintScaleFactor }, 0));
		PAINT_RAILWAY_RAIL1.setStrokeWidth(0.5f * paintScaleFactor);
		PAINT_RAILWAY_RAIL2.setStrokeWidth(0.6f * paintScaleFactor);
		PAINT_RAILWAY_TRAM1.setStrokeWidth(0.4f * paintScaleFactor);
		PAINT_RAILWAY_TRAM1.setPathEffect(new DashPathEffect(new float[] { 2 * paintScaleFactor,
				2 * paintScaleFactor }, 0));
		PAINT_RAILWAY_TRAM2.setStrokeWidth(0.5f * paintScaleFactor);
		PAINT_RAILWAY_LIGHT_RAIL1.setStrokeWidth(0.4f * paintScaleFactor);
		PAINT_RAILWAY_LIGHT_RAIL1.setPathEffect(new DashPathEffect(new float[] { 2 * paintScaleFactor,
				2 * paintScaleFactor }, 0));
		PAINT_RAILWAY_LIGHT_RAIL2.setStrokeWidth(0.5f * paintScaleFactor);
		PAINT_RAILWAY_SUBWAY1.setPathEffect(new DashPathEffect(new float[] { 2 * paintScaleFactor,
				2 * paintScaleFactor }, 0));
		PAINT_RAILWAY_SUBWAY1.setStrokeWidth(0.4f * paintScaleFactor);
		PAINT_RAILWAY_SUBWAY2.setStrokeWidth(0.5f * paintScaleFactor);
		PAINT_RAILWAY_SUBWAY_TUNNEL.setPathEffect(new DashPathEffect(new float[] {
				1 * paintScaleFactor, 1 * paintScaleFactor }, 0));
		PAINT_RAILWAY_SUBWAY_TUNNEL.setStrokeWidth(0.4f * paintScaleFactor);
		PAINT_RAILWAY_STATION_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);

		PAINT_ROUTE_FERRY.setPathEffect(new DashPathEffect(new float[] { 3 * paintScaleFactor,
				3 * paintScaleFactor }, 0));
		PAINT_ROUTE_FERRY.setStrokeWidth(1 * paintScaleFactor);

		PAINT_SPORT_SHOOTING_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);
		PAINT_SPORT_SWIMMING_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);
		PAINT_SPORT_TENNIS_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);

		PAINT_TOURISM_ZOO_OUTLINE.setStrokeWidth(0.3f * paintScaleFactor);

		PAINT_WATERWAY_CANAL.setStrokeWidth(1.5f * paintScaleFactor);
		PAINT_WATERWAY_RIVER.setStrokeWidth(1 * paintScaleFactor);
		PAINT_WATERWAY_STREAM.setStrokeWidth(0.7f * paintScaleFactor);
	}

	@Override
	final void cleanup() {
		// free the memory of the map symbols
		if (this.mapSymbols != null) {
			this.mapSymbols.recycle();
			this.mapSymbols = null;
		}

		// free the memory memory of the map patterns
		if (this.mapPatterns != null) {
			this.mapPatterns.recycle();
			this.mapPatterns = null;
		}

		this.currentTile = null;
		this.tileBitmap = null;
		this.database = null;
	}

	/**
	 * This method is called when the nodes should be rendered.
	 * 
	 * @param drawNodes
	 *            the nodes to be rendered.
	 */
	abstract void drawNodes(List<PointTextContainer> drawNodes);

	/**
	 * This method is called when the map symbols should be rendered.
	 * 
	 * @param drawSymbols
	 *            the symbols to be rendered.
	 */
	abstract void drawSymbols(List<SymbolContainer> drawSymbols);

	/**
	 * This method is called when the tile coordinates should be rendered.
	 * 
	 * @param tile
	 *            the tile whose coordinates should be rendered.
	 */
	abstract void drawTileCoordinates(Tile tile);

	/**
	 * This method is called when the tile frame should be rendered.
	 */
	abstract void drawTileFrame();

	/**
	 * This method is called when the way names should be rendered.
	 * 
	 * @param drawWayNames
	 *            the way names to be rendered.
	 */
	abstract void drawWayNames(List<WayTextContainer> drawWayNames);

	/**
	 * This method is called when the ways should be rendered.
	 * 
	 * @param drawWays
	 *            the ways to be rendered.
	 * @param layers
	 *            the number of layers.
	 * @param levelsPerLayer
	 *            the amount of levels per layer.
	 */
	abstract void drawWays(List<List<List<ShapePaintContainer>>> drawWays, byte layers,
			byte levelsPerLayer);

	@Override
	final boolean executeJob(MapGeneratorJob mapGeneratorJob) {
		this.currentJob = mapGeneratorJob;
		this.currentTile = mapGeneratorJob.tile;

		// check if the zoom level has changed
		if (this.currentTile.zoomLevel != this.lastTileZoomLevel) {
			setPaintZoomLevel(this.currentTile.zoomLevel);
			this.lastTileZoomLevel = this.currentTile.zoomLevel;
		}

		// check if the text scale has changed
		if (this.currentJob.textScale != this.lastTileTextScale) {
			setPaintTextSize(this.currentJob.textScale);
			this.lastTileTextScale = this.currentJob.textScale;
		}

		this.database.executeQuery(this.currentTile,
				this.currentTile.zoomLevel >= MIN_ZOOM_LEVEL_WAY_NAMES, this);

		if (isInterrupted()) {
			return false;
		}

		// start the coastline algorithm for generating closed polygons
		this.coastlineAlgorithm.setTiles(this.tileForCoastlineAlgorithm, this.currentTile);
		this.coastlineAlgorithm.generateClosedPolygons(this);

		// erase the tileBitmap with the default color
		this.tileBitmap.eraseColor(TILE_BACKGROUND);

		// draw all map objects
		drawWays(this.ways, LAYERS, LayerIds.LEVELS_PER_LAYER);
		if (isInterrupted()) {
			return false;
		}

		drawSymbols(this.waySymbols);
		this.nodes = this.labelPlacement.placeLabels(this.nodes, this.pointSymbols, this.areaLabels,
				this.currentTile);
		drawSymbols(this.pointSymbols);
		if (isInterrupted()) {
			return false;
		}

		drawWayNames(this.wayNames);
		if (isInterrupted()) {
			return false;
		}

		drawNodes(this.nodes);
		drawNodes(this.areaLabels);

		if (mapGeneratorJob.drawTileFrames) {
			drawTileFrame();
		}

		if (mapGeneratorJob.drawTileCoordinates) {
			drawTileCoordinates(this.currentTile);
		}

		finishMapGeneration();
		return true;
	}

	/**
	 * This method is called after all map objects have been rendered.
	 */
	abstract void finishMapGeneration();

	@Override
	final GeoPoint getDefaultStartPoint() {
		if (this.database != null) {
			if (this.database.getStartPosition() != null) {
				return this.database.getStartPosition();
			} else if (this.database.getMapCenter() != null) {
				return this.database.getMapCenter();
			}
		}
		return super.getDefaultStartPoint();
	}

	@Override
	final byte getDefaultZoomLevel() {
		return DEFAULT_ZOOM_LEVEL;
	}

	@Override
	final byte getMaxZoomLevel() {
		return ZOOM_MAX;
	}

	/**
	 * This method must be called each time a new map file is set for the database.
	 */
	final void onMapFileChange() {
		this.tagIDsNodes.update(this.database.getNodeTags());
		this.tagIDsWays.update(this.database.getWayTags());
	}

	@Override
	final void prepareMapGeneration() {
		// clear all data structures for the map objects
		for (byte i = LAYERS - 1; i >= 0; --i) {
			this.innerWayList = this.ways.get(i);
			for (byte j = LayerIds.LEVELS_PER_LAYER - 1; j >= 0; --j) {
				this.innerWayList.get(j).clear();
			}
		}
		this.wayNames.clear();
		this.nodes.clear();
		this.areaLabels.clear();
		this.waySymbols.clear();
		this.pointSymbols.clear();
		this.coastlineAlgorithm.clearCoastlineSegments();
	}

	final void renderCoastlineTile(Tile tile) {
		this.tileForCoastlineAlgorithm = tile;
	}

	/**
	 * Renders a single POI.
	 * 
	 * @param nodeLayer
	 *            the layer of the node.
	 * @param latitude
	 *            the latitude of the node.
	 * @param longitude
	 *            the longitude of the node.
	 * @param nodeName
	 *            the name of the node (may be null).
	 * @param houseNumber
	 *            the house number of the node (may be null).
	 * @param nodeElevation
	 *            the elevation of the node (may be null).
	 * @param nodeTagIds
	 *            the tag id array of the node.
	 */
	final void renderPointOfInterest(byte nodeLayer, int latitude, int longitude, String nodeName,
			String houseNumber, String nodeElevation, boolean[] nodeTagIds) {
		float nodeX = scaleLongitude(longitude);
		float nodeY = scaleLatitude(latitude);

		// check for a valid layer value
		if (nodeLayer < 0) {
			this.layer = this.ways.get(0);
		} else if (nodeLayer >= LAYERS) {
			this.layer = this.ways.get(LAYERS - 1);
		} else {
			this.layer = this.ways.get(nodeLayer);
		}

		/* houseNumber */
		if (houseNumber != null && this.currentTile.zoomLevel >= 17) {
			this.nodes.add(new PointTextContainer(houseNumber, nodeX, nodeY,
					PAINT_NAME_BLACK_TINY, PAINT_NAME_WHITE_STROKE_TINY));
		}

		/* aeroway */
		if (this.tagIDsNodes.aeroway$aerodrome != null
				&& nodeTagIds[this.tagIDsNodes.aeroway$aerodrome.intValue()]
				&& this.currentTile.zoomLevel <= 14) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.airport);
		} else if (this.tagIDsNodes.aeroway$helipad != null
				&& nodeTagIds[this.tagIDsNodes.aeroway$helipad.intValue()]) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.helipad);
		}

		/* amenity */
		else if (this.tagIDsNodes.amenity$pub != null
				&& nodeTagIds[this.tagIDsNodes.amenity$pub.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.pub);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_RED_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$cinema != null
				&& nodeTagIds[this.tagIDsNodes.amenity$cinema.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.cinema);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$theatre != null
				&& nodeTagIds[this.tagIDsNodes.amenity$theatre.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.theatre);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$fire_station != null
				&& nodeTagIds[this.tagIDsNodes.amenity$fire_station.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.firebrigade);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$shelter != null
				&& nodeTagIds[this.tagIDsNodes.amenity$shelter.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.shelter);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$school != null
				&& nodeTagIds[this.tagIDsNodes.amenity$school.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.school);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$university != null
				&& nodeTagIds[this.tagIDsNodes.amenity$university.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.university);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$place_of_worship != null
				&& nodeTagIds[this.tagIDsNodes.amenity$place_of_worship.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.church);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$atm != null
				&& nodeTagIds[this.tagIDsNodes.amenity$atm.intValue()]
				&& this.currentTile.zoomLevel >= 17) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.atm);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$library != null
				&& nodeTagIds[this.tagIDsNodes.amenity$library.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.library);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$fast_food != null
				&& nodeTagIds[this.tagIDsNodes.amenity$fast_food.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.fastfood);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$parking != null
				&& nodeTagIds[this.tagIDsNodes.amenity$parking.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.parking);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$hospital != null
				&& nodeTagIds[this.tagIDsNodes.amenity$hospital.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.hospital);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$restaurant != null
				&& nodeTagIds[this.tagIDsNodes.amenity$restaurant.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.restaurant);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$bank != null
				&& nodeTagIds[this.tagIDsNodes.amenity$bank.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.bank);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$cafe != null
				&& nodeTagIds[this.tagIDsNodes.amenity$cafe.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.cafe);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$fuel != null
				&& nodeTagIds[this.tagIDsNodes.amenity$fuel.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.petrolStation);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$bus_station != null
				&& nodeTagIds[this.tagIDsNodes.amenity$bus_station.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.bus_sta);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.amenity$post_box != null
				&& nodeTagIds[this.tagIDsNodes.amenity$post_box.intValue()]) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.postbox);
		} else if (this.tagIDsNodes.amenity$post_office != null
				&& nodeTagIds[this.tagIDsNodes.amenity$post_office.intValue()]) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.postoffice);
		} else if (this.tagIDsNodes.amenity$pharmacy != null
				&& nodeTagIds[this.tagIDsNodes.amenity$pharmacy.intValue()]) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.pharmacy);
		} else if (this.tagIDsNodes.amenity$fountain != null
				&& nodeTagIds[this.tagIDsNodes.amenity$fountain.intValue()]) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.fountain);
		} else if (this.tagIDsNodes.amenity$recycling != null
				&& nodeTagIds[this.tagIDsNodes.amenity$recycling.intValue()]) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.recycling);
		} else if (this.tagIDsNodes.amenity$telephone != null
				&& nodeTagIds[this.tagIDsNodes.amenity$telephone.intValue()]) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.telephone);
		} else if (this.tagIDsNodes.amenity$toilets != null
				&& nodeTagIds[this.tagIDsNodes.amenity$toilets.intValue()]) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.toilets);
		} else if (this.tagIDsNodes.amenity$bicycle_rental != null
				&& nodeTagIds[this.tagIDsNodes.amenity$bicycle_rental.intValue()]) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.bicycle_rental);
		}

		/* barrier */
		else if (this.tagIDsNodes.barrier$bollard != null
				&& nodeTagIds[this.tagIDsNodes.barrier$bollard.intValue()]) {
			this.layer.get(LayerIds.POI_CIRCLE_SYMBOL).add(
					new ShapePaintContainer(new CircleContainer(nodeX, nodeY,
							1.5f), PAINT_BARRIER_BOLLARD));
		}

		/* highway */
		else if (this.tagIDsNodes.highway$bus_stop != null
				&& nodeTagIds[this.tagIDsNodes.highway$bus_stop.intValue()]) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.bus);
		} else if (this.tagIDsNodes.highway$traffic_signals != null
				&& nodeTagIds[this.tagIDsNodes.highway$traffic_signals.intValue()]
				&& this.currentTile.zoomLevel >= 17) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.traffic_signal);
		}

		/* historic */
		else if ((this.tagIDsNodes.historic$memorial != null && nodeTagIds[this.tagIDsNodes.historic$memorial
				.intValue()])
				|| (this.tagIDsNodes.historic$monument != null && nodeTagIds[this.tagIDsNodes.historic$monument
						.intValue()])) {
			this.shapeContainer = new CircleContainer(nodeX, nodeY, 3);
			this.layer.get(LayerIds.POI_CIRCLE_SYMBOL).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_HISTORIC_CIRCLE_INNER));
			this.layer.get(LayerIds.POI_CIRCLE_SYMBOL).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_HISTORIC_CIRCLE_OUTER));
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX,
						nodeY - 8, PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
			}
		}

		/* leisure */
		else if (this.tagIDsNodes.leisure$playground != null
				&& nodeTagIds[this.tagIDsNodes.leisure$playground.intValue()]) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.playground);
		}

		/* man_made */
		else if (this.tagIDsNodes.man_made$windmill != null
				&& nodeTagIds[this.tagIDsNodes.man_made$windmill.intValue()]) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.windmill);
		}

		/* natural */
		else if (this.tagIDsNodes.natural$cave_entrance != null
				&& nodeTagIds[this.tagIDsNodes.natural$cave_entrance.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.cave_entrance);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLACK_SMALL, PAINT_NAME_WHITE_STROKE_SMALL));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.natural$peak != null
				&& nodeTagIds[this.tagIDsNodes.natural$peak.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.peak);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLACK_SMALL, PAINT_NAME_WHITE_STROKE_SMALL));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
			if (nodeElevation != null && this.currentTile.zoomLevel >= 17) {
				this.nodes.add(new PointTextContainer(nodeElevation, nodeX,
						nodeY + 18, PAINT_NAME_BLACK_TINY, PAINT_NAME_WHITE_STROKE_TINY));
			}
		} else if (this.tagIDsNodes.natural$volcano != null
				&& nodeTagIds[this.tagIDsNodes.natural$volcano.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.vulcan);
			if (nodeName != null && this.currentTile.zoomLevel >= 14) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLACK_TINY, PAINT_NAME_WHITE_STROKE_SMALLER));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
			if (nodeElevation != null && this.currentTile.zoomLevel >= 17) {
				this.nodes.add(new PointTextContainer(nodeElevation, nodeX,
						nodeY + 18, PAINT_NAME_BLACK_TINY, PAINT_NAME_WHITE_STROKE_TINY));
			}
		}

		/* place */
		else if (this.tagIDsNodes.place$city != null
				&& nodeTagIds[this.tagIDsNodes.place$city.intValue()]) {
			if (nodeName != null && this.currentTile.zoomLevel <= 14) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLACK_HUGE, PAINT_NAME_WHITE_STROKE_HUGE));
			}
		} else if (this.tagIDsNodes.place$country != null
				&& nodeTagIds[this.tagIDsNodes.place$country.intValue()]) {
			if (nodeName != null && this.currentTile.zoomLevel <= 6) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLACK_HUGE, PAINT_NAME_WHITE_STROKE_HUGE));
			}
		} else if (this.tagIDsNodes.place$island != null
				&& nodeTagIds[this.tagIDsNodes.place$island.intValue()]) {
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLACK_LARGER, PAINT_NAME_WHITE_STROKE_LARGER));
			}
		} else if ((this.tagIDsNodes.place$suburb != null && nodeTagIds[this.tagIDsNodes.place$suburb
				.intValue()])
				|| (this.tagIDsNodes.place$town != null && nodeTagIds[this.tagIDsNodes.place$town
						.intValue()])
				|| (this.tagIDsNodes.place$village != null && nodeTagIds[this.tagIDsNodes.place$village
						.intValue()])) {
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLACK_LARGE, PAINT_NAME_WHITE_STROKE_LARGE));
			}
		}

		/* railway */
		else if (this.tagIDsNodes.railway$level_crossing != null
				&& nodeTagIds[this.tagIDsNodes.railway$level_crossing.intValue()]) {
			/* crossing */
			addPOISymbol(nodeX, nodeY, this.mapSymbols.railway_crossing);
		} else if (this.tagIDsNodes.railway$station != null
				&& nodeTagIds[this.tagIDsNodes.railway$station.intValue()]) {
			/* station */
			if ((this.tagIDsNodes.station$light_rail != null && nodeTagIds[this.tagIDsNodes.station$light_rail
					.intValue()])
					|| (this.tagIDsNodes.station$subway != null && nodeTagIds[this.tagIDsNodes.station$subway
							.intValue()])) {
				this.shapeContainer = new CircleContainer(nodeX, nodeY, 4);
				this.layer.get(LayerIds.POI_CIRCLE_SYMBOL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_CIRCLE_INNER));
				this.layer.get(LayerIds.POI_CIRCLE_SYMBOL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_CIRCLE_OUTER));
				if (nodeName != null) {
					this.nodes.add(new PointTextContainer(nodeName, nodeX,
							nodeY - 10, PAINT_NAME_RED_SMALLER,
							PAINT_NAME_WHITE_STROKE_SMALLER));
				}
			} else {
				this.shapeContainer = new CircleContainer(nodeX, nodeY, 6);
				this.layer.get(LayerIds.POI_CIRCLE_SYMBOL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_CIRCLE_INNER));
				this.layer.get(LayerIds.POI_CIRCLE_SYMBOL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_CIRCLE_OUTER));
				if (nodeName != null) {
					this.nodes.add(new PointTextContainer(nodeName, nodeX,
							nodeY - 15, PAINT_NAME_RED_NORMAL,
							PAINT_NAME_WHITE_STROKE_NORMAL));
				}
			}
		} else if ((this.tagIDsNodes.railway$halt != null && nodeTagIds[this.tagIDsNodes.railway$halt
				.intValue()])
				|| (this.tagIDsNodes.railway$tram_stop != null && nodeTagIds[this.tagIDsNodes.railway$tram_stop
						.intValue()])) {
			/* halt or tram_stop */
			this.shapeContainer = new CircleContainer(nodeX, nodeY, 4);
			this.layer.get(LayerIds.POI_CIRCLE_SYMBOL).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_CIRCLE_INNER));
			this.layer.get(LayerIds.POI_CIRCLE_SYMBOL).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_CIRCLE_OUTER));
			if (nodeName != null) {
				this.nodes
						.add(new PointTextContainer(nodeName, nodeX,
								nodeY - 10, PAINT_NAME_RED_SMALLER,
								PAINT_NAME_WHITE_STROKE_SMALLER));
			}
		}

		/* shop */
		else if (this.tagIDsNodes.shop$bakery != null
				&& nodeTagIds[this.tagIDsNodes.shop$bakery.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.bakery);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.shop$organic != null
				&& nodeTagIds[this.tagIDsNodes.shop$organic.intValue()]) {
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
			}
		} else if (this.tagIDsNodes.shop$supermarket != null
				&& nodeTagIds[this.tagIDsNodes.shop$supermarket.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.supermarket);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		}

		/* tourism */
		else if (this.tagIDsNodes.tourism$information != null
				&& nodeTagIds[this.tagIDsNodes.tourism$information.intValue()]) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.information);
		} else if (this.tagIDsNodes.tourism$museum != null
				&& nodeTagIds[this.tagIDsNodes.tourism$museum.intValue()]) {
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
			}
		} else if (this.tagIDsNodes.tourism$hostel != null
				&& nodeTagIds[this.tagIDsNodes.tourism$hostel.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.hostel);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.tourism$hotel != null
				&& nodeTagIds[this.tagIDsNodes.tourism$hotel.intValue()]) {
			this.symbolContainer = addPOISymbol(nodeX, nodeY,
					this.mapSymbols.hotel);
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_BLUE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
				this.nodes.get(this.nodes.size() - 1).symbol = this.symbolContainer;
			}
		} else if (this.tagIDsNodes.tourism$attraction != null
				&& nodeTagIds[this.tagIDsNodes.tourism$attraction.intValue()]) {
			if (nodeName != null) {
				this.nodes.add(new PointTextContainer(nodeName, nodeX, nodeY,
						PAINT_NAME_PURPLE_TINY, PAINT_NAME_WHITE_STROKE_TINY));
			}
		} else if (this.tagIDsNodes.tourism$viewpoint != null
				&& nodeTagIds[this.tagIDsNodes.tourism$viewpoint.intValue()]) {
			addPOISymbol(nodeX, nodeY, this.mapSymbols.viewpoint);
		}

		/* unknown node */
		else {
			return;
		}
	}

	/**
	 * Renders water background for the current tile.
	 */
	final void renderWaterBackground() {
		if (this.currentJob.highlightWater) {
			this.ways.get(DEFAULT_LAYER).get(LayerIds.SEA_AREAS).add(
					new ShapePaintContainer(new WayContainer(this.waterTileCoordinates),
							PAINT_WATER_TILE_HIGHTLIGHT));
		} else {
			this.ways.get(DEFAULT_LAYER).get(LayerIds.SEA_AREAS).add(
					new ShapePaintContainer(new WayContainer(this.waterTileCoordinates),
							PAINT_NATURAL_WATER_FILL));
		}
	}

	/**
	 * Renders a single way or area. An area is a special case of a way where the first and last way
	 * node have the same coordinates.
	 * 
	 * @param wayLayer
	 *            the layer of the way.
	 * @param wayNumberOfRealTags
	 *            the number of real tags.
	 * @param wayName
	 *            the name of the way (may be null).
	 * @param wayRef
	 *            the reference of the way (may be null).
	 * @param wayLabelPosition
	 *            the position of the area label (may be null).
	 * @param wayTagIds
	 *            the tag id array of the way.
	 * @param wayTagBitmap
	 *            the way tag tileBitmap.
	 * @param wayNodesSequenceLength
	 *            the number of node positions.
	 * @param wayNodesSequence
	 *            the node positions.
	 * @param innerWays
	 *            the inner nodes if this way is a multipolygon.
	 */
	final void renderWay(byte wayLayer, byte wayNumberOfRealTags, String wayName, String wayRef,
			int[] wayLabelPosition, boolean[] wayTagIds, byte wayTagBitmap, int wayNodesSequenceLength,
			int[] wayNodesSequence, int[][] innerWays) {
		byte remainingTags = wayNumberOfRealTags;
		if (innerWays == null) {
			this.coordinates = new float[1][];
		} else {
			this.coordinates = new float[1 + innerWays.length][];
		}
		this.coordinates[0] = new float[wayNodesSequenceLength];
		for (int i = 0; i < wayNodesSequenceLength; i += 2) {
			this.coordinates[0][i] = scaleLongitude(wayNodesSequence[i]);
			this.coordinates[0][i + 1] = scaleLatitude(wayNodesSequence[i + 1]);
		}

		if (innerWays != null) {
			for (int j = 1; j <= innerWays.length; ++j) {
				int[] innerWay = innerWays[j - 1];
				this.innerWayLength = innerWay.length;
				this.coordinates[j] = new float[this.innerWayLength];

				for (int i = 0; i < this.innerWayLength; i += 2) {
					this.coordinates[j][i] = scaleLongitude(innerWay[i]);
					this.coordinates[j][i + 1] = scaleLatitude(innerWay[i + 1]);
				}
			}
		}
		this.shapeContainer = new WayContainer(this.coordinates);

		// check for a valid layer value
		if (wayLayer < 0) {
			this.layer = this.ways.get(0);
		} else if (wayLayer >= LAYERS) {
			this.layer = this.ways.get(LAYERS - 1);
		} else {
			this.layer = this.ways.get(wayLayer);
		}

		/* highway */
		if ((wayTagBitmap & BITMAP_HIGHWAY) != 0) {
			if (this.tagIDsWays.tunnel$yes != null && wayTagIds[this.tagIDsWays.tunnel$yes.intValue()]) {
				this.layer.get(LayerIds.HIGHWAY_TUNNEL$YES).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_TUNNEL));
			} else if (this.tagIDsWays.highway$motorway != null
					&& wayTagIds[this.tagIDsWays.highway$motorway.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_MOTORWAY1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$MOTORWAY1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$MOTORWAY2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_MOTORWAY2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_MOTORWAY2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$MOTORWAY1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_MOTORWAY1));
					this.layer.get(LayerIds.HIGHWAY$MOTORWAY2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_MOTORWAY2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_MOTORWAY2);
					}
				}
			} else if (this.tagIDsWays.highway$motorway_link != null
					&& wayTagIds[this.tagIDsWays.highway$motorway_link.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_MOTORWAY_LINK1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$MOTORWAY_LINK1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$MOTORWAY_LINK2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_MOTORWAY_LINK2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_MOTORWAY_LINK2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$MOTORWAY_LINK1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_MOTORWAY_LINK1));
					this.layer.get(LayerIds.HIGHWAY$MOTORWAY_LINK2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_MOTORWAY_LINK2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_MOTORWAY_LINK2);
					}
				}
			} else if (this.tagIDsWays.highway$trunk != null
					&& wayTagIds[this.tagIDsWays.highway$trunk.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_TRUNK1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$TRUNK1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$TRUNK2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_TRUNK2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_TRUNK2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$TRUNK1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_TRUNK1));
					this.layer.get(LayerIds.HIGHWAY$TRUNK2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_TRUNK2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_TRUNK2);
					}
				}
			} else if (this.tagIDsWays.highway$trunk_link != null
					&& wayTagIds[this.tagIDsWays.highway$trunk_link.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_TRUNK_LINK1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$TRUNK_LINK1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$TRUNK_LINK2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_TRUNK_LINK2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_TRUNK_LINK2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$TRUNK_LINK1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_TRUNK_LINK1));
					this.layer.get(LayerIds.HIGHWAY$TRUNK_LINK2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_TRUNK_LINK2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_TRUNK_LINK2);
					}
				}
			} else if (this.tagIDsWays.highway$primary != null
					&& wayTagIds[this.tagIDsWays.highway$primary.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_PRIMARY1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$PRIMARY1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$PRIMARY2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_PRIMARY2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_PRIMARY2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$PRIMARY1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_PRIMARY1));
					this.layer.get(LayerIds.HIGHWAY$PRIMARY2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_PRIMARY2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_PRIMARY2);
					}
				}
			} else if (this.tagIDsWays.highway$primary_link != null
					&& wayTagIds[this.tagIDsWays.highway$primary_link.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_PRIMARY_LINK1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$PRIMARY_LINK1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$PRIMARY_LINK2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_PRIMARY_LINK2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_PRIMARY_LINK2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$PRIMARY_LINK1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_PRIMARY_LINK1));
					this.layer.get(LayerIds.HIGHWAY$PRIMARY_LINK2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_PRIMARY_LINK2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_PRIMARY_LINK2);
					}
				}
			} else if (this.tagIDsWays.highway$secondary != null
					&& wayTagIds[this.tagIDsWays.highway$secondary.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_SECONDARY1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$SECONDARY1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$SECONDARY2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_SECONDARY2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_SECONDARY2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$SECONDARY1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_SECONDARY1));
					this.layer.get(LayerIds.HIGHWAY$SECONDARY2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_SECONDARY2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_SECONDARY2);
					}
				}
			} else if (this.tagIDsWays.highway$secondary_link != null
					&& wayTagIds[this.tagIDsWays.highway$secondary_link.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_SECONDARY_LINK1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$SECONDARY_LINK1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$SECONDARY_LINK2)
							.add(
									new ShapePaintContainer(this.shapeContainer,
											PAINT_HIGHWAY_SECONDARY_LINK2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_SECONDARY_LINK2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$SECONDARY_LINK1)
							.add(
									new ShapePaintContainer(this.shapeContainer,
											PAINT_HIGHWAY_SECONDARY_LINK1));
					this.layer.get(LayerIds.HIGHWAY$SECONDARY_LINK2)
							.add(
									new ShapePaintContainer(this.shapeContainer,
											PAINT_HIGHWAY_SECONDARY_LINK2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_SECONDARY_LINK2);
					}
				}
			} else if (this.tagIDsWays.highway$tertiary != null
					&& wayTagIds[this.tagIDsWays.highway$tertiary.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_TERTIARY1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$TERTIARY1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$TERTIARY2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_TERTIARY2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_TERTIARY2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$TERTIARY1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_TERTIARY1));
					this.layer.get(LayerIds.HIGHWAY$TERTIARY2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_TERTIARY2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_TERTIARY2);
					}
				}
			} else if (this.tagIDsWays.highway$unclassified != null
					&& wayTagIds[this.tagIDsWays.highway$unclassified.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_UNCLASSIFIED1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$UNCLASSIFIED1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$UNCLASSIFIED2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_UNCLASSIFIED2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_UNCLASSIFIED2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$UNCLASSIFIED1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_UNCLASSIFIED1));
					this.layer.get(LayerIds.HIGHWAY$UNCLASSIFIED2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_UNCLASSIFIED2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_UNCLASSIFIED2);
					}
				}
			} else if (this.tagIDsWays.highway$road != null
					&& wayTagIds[this.tagIDsWays.highway$road.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_ROAD1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$ROAD1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$ROAD2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_ROAD2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_ROAD2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$ROAD1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_ROAD1));
					this.layer.get(LayerIds.HIGHWAY$ROAD2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_ROAD2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_ROAD2);
					}
				}
			} else if (this.tagIDsWays.highway$residential != null
					&& wayTagIds[this.tagIDsWays.highway$residential.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_RESIDENTIAL1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$RESIDENTIAL1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$RESIDENTIAL2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_RESIDENTIAL2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_RESIDENTIAL2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$RESIDENTIAL1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_RESIDENTIAL1));
					this.layer.get(LayerIds.HIGHWAY$RESIDENTIAL2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_RESIDENTIAL2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_RESIDENTIAL2);
					}
				}
			} else if (this.tagIDsWays.highway$living_street != null
					&& wayTagIds[this.tagIDsWays.highway$living_street.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_LIVING_STREET1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$LIVING_STREET1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$LIVING_STREET2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_LIVING_STREET2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_LIVING_STREET2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$LIVING_STREET1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_LIVING_STREET1));
					this.layer.get(LayerIds.HIGHWAY$LIVING_STREET2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_LIVING_STREET2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_LIVING_STREET2);
					}
				}
			} else if (this.tagIDsWays.highway$service != null
					&& wayTagIds[this.tagIDsWays.highway$service.intValue()]) {
				if (this.tagIDsWays.area$yes != null && wayTagIds[this.tagIDsWays.area$yes.intValue()]) {
					this.layer.get(LayerIds.HIGHWAY$SERVICE_AREA$YES).add(
							new ShapePaintContainer(this.shapeContainer,
									PAINT_HIGHWAY_SERVICE_AREA_OUTLINE));
					this.layer.get(LayerIds.HIGHWAY$SERVICE_AREA$YES).add(
							new ShapePaintContainer(this.shapeContainer,
									PAINT_HIGHWAY_SERVICE_AREA_FILL));
					addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				} else if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_SERVICE1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$SERVICE1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$SERVICE2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_SERVICE2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_SERVICE2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$SERVICE1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_SERVICE1));
					this.layer.get(LayerIds.HIGHWAY$SERVICE2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_SERVICE2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_SERVICE2);
					}
				}
			} else if (this.tagIDsWays.highway$track != null
					&& wayTagIds[this.tagIDsWays.highway$track.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_TRACK1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$TRACK1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$TRACK2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_TRACK2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_TRACK2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$TRACK1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_TRACK1));
					this.layer.get(LayerIds.HIGHWAY$TRACK2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_TRACK2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_TRACK2);
					}
				}
			} else if (this.tagIDsWays.highway$pedestrian != null
					&& wayTagIds[this.tagIDsWays.highway$pedestrian.intValue()]) {
				if (this.tagIDsWays.area$yes != null && wayTagIds[this.tagIDsWays.area$yes.intValue()]) {
					this.layer.get(LayerIds.HIGHWAY$PEDESTRIAN_AREA$YES).add(
							new ShapePaintContainer(this.shapeContainer,
									PAINT_HIGHWAY_PEDESTRIAN_AREA_OUTLINE));
					this.layer.get(LayerIds.HIGHWAY$PEDESTRIAN_AREA$YES).add(
							new ShapePaintContainer(this.shapeContainer,
									PAINT_HIGHWAY_PEDESTRIAN_AREA_FILL));
					addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				} else if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_PEDESTRIAN1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$PEDESTRIAN1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$PEDESTRIAN2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_PEDESTRIAN2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_PEDESTRIAN2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$PEDESTRIAN1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_PEDESTRIAN1));
					this.layer.get(LayerIds.HIGHWAY$PEDESTRIAN2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_PEDESTRIAN2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_PEDESTRIAN2);
					}
				}
			} else if (this.tagIDsWays.highway$path != null
					&& wayTagIds[this.tagIDsWays.highway$path.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_PATH1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$PATH1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$PATH2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_PATH2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_PATH2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$PATH1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_PATH1));
					this.layer.get(LayerIds.HIGHWAY$PATH2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_PATH2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_PATH2);
					}
				}
			} else if (this.tagIDsWays.highway$cycleway != null
					&& wayTagIds[this.tagIDsWays.highway$cycleway.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_CYCLEWAY1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$CYCLEWAY1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$CYCLEWAY2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_CYCLEWAY2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_CYCLEWAY2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$CYCLEWAY1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_CYCLEWAY1));
					this.layer.get(LayerIds.HIGHWAY$CYCLEWAY2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_CYCLEWAY2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_CYCLEWAY2);
					}
				}
			} else if (this.tagIDsWays.highway$footway != null
					&& wayTagIds[this.tagIDsWays.highway$footway.intValue()]) {
				if (this.tagIDsWays.area$yes != null && wayTagIds[this.tagIDsWays.area$yes.intValue()]) {
					this.layer.get(LayerIds.HIGHWAY$FOOTWAY_AREA$YES).add(
							new ShapePaintContainer(this.shapeContainer,
									PAINT_HIGHWAY_FOOTWAY_AREA_OUTLINE));
					this.layer.get(LayerIds.HIGHWAY$FOOTWAY_AREA$YES).add(
							new ShapePaintContainer(this.shapeContainer,
									PAINT_HIGHWAY_FOOTWAY_AREA_FILL));
					addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				} else if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_FOOTWAY1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					paint1Bridge.setPathEffect(null);
					this.layer.get(LayerIds.HIGHWAY$FOOTWAY1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$FOOTWAY2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_FOOTWAY2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_FOOTWAY2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$FOOTWAY1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_FOOTWAY1));
					this.layer.get(LayerIds.HIGHWAY$FOOTWAY2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_FOOTWAY2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_FOOTWAY2);
					}
				}
			} else if (this.tagIDsWays.highway$byway != null
					&& wayTagIds[this.tagIDsWays.highway$byway.intValue()]) {
				this.layer.get(LayerIds.HIGHWAY$BYWAY).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_BYWAY));
				if (wayName != null && this.currentTile.zoomLevel > 15) {
					addWayName(wayName, PAINT_HIGHWAY_BYWAY);
				}
			} else if (this.tagIDsWays.highway$bridleway != null
					&& wayTagIds[this.tagIDsWays.highway$bridleway.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_BRIDLEWAY1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$BRIDLEWAY1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$BRIDLEWAY2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_BRIDLEWAY2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_BRIDLEWAY2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$BRIDLEWAY1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_BRIDLEWAY1));
					this.layer.get(LayerIds.HIGHWAY$BRIDLEWAY2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_BRIDLEWAY2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_BRIDLEWAY2);
					}
				}
			} else if (this.tagIDsWays.highway$steps != null
					&& wayTagIds[this.tagIDsWays.highway$steps.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_STEPS1);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					paint1Bridge.setColor(Color.BLACK);
					paint1Bridge.setStrokeWidth(paint1Bridge.getStrokeWidth() * 1.05f);
					this.layer.get(LayerIds.HIGHWAY$STEPS1).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					this.layer.get(LayerIds.HIGHWAY$STEPS2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_STEPS2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_STEPS2);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$STEPS1).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_STEPS1));
					this.layer.get(LayerIds.HIGHWAY$STEPS2).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_STEPS2));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_STEPS2);
					}
				}
			} else if (this.tagIDsWays.highway$construction != null
					&& wayTagIds[this.tagIDsWays.highway$construction.intValue()]) {
				if (this.tagIDsWays.bridge$yes != null
						&& wayTagIds[this.tagIDsWays.bridge$yes.intValue()]) {
					Paint paint1Bridge = new Paint(PAINT_HIGHWAY_CONSTRUCTION);
					paint1Bridge.setStrokeCap(Paint.Cap.BUTT);
					this.layer.get(LayerIds.HIGHWAY$CONSTRUCTION).add(
							new ShapePaintContainer(this.shapeContainer, paint1Bridge));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, paint1Bridge);
					}
				} else {
					this.layer.get(LayerIds.HIGHWAY$CONSTRUCTION).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_HIGHWAY_CONSTRUCTION));
					if (this.tagIDsWays.oneway$yes != null
							&& wayTagIds[this.tagIDsWays.oneway$yes.intValue()]
							&& this.currentTile.zoomLevel > 15) {
						addWaySymbol(this.mapSymbols.oneway, true, true);
					}
					if (wayName != null && this.currentTile.zoomLevel > 15) {
						addWayName(wayName, PAINT_HIGHWAY_CONSTRUCTION);
					}
				}
			}
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* building */
		if ((wayTagBitmap & BITMAP_BUILDING) != 0) {
			if (this.tagIDsWays.building$roof != null
					&& wayTagIds[this.tagIDsWays.building$roof.intValue()]) {
				this.layer.get(LayerIds.BUILDING$ROOF).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_BUILDING_ROOF_OUTLINE));
			} else if ((this.tagIDsWays.building$apartments != null && wayTagIds[this.tagIDsWays.building$apartments
					.intValue()])
					|| (this.tagIDsWays.building$embassy != null && wayTagIds[this.tagIDsWays.building$embassy
							.intValue()])
					|| (this.tagIDsWays.building$government != null && wayTagIds[this.tagIDsWays.building$government
							.intValue()])
					|| (this.tagIDsWays.building$gym != null && wayTagIds[this.tagIDsWays.building$gym
							.intValue()])
					|| (this.tagIDsWays.building$ruins != null && wayTagIds[this.tagIDsWays.building$ruins
							.intValue()])
					|| (this.tagIDsWays.building$sports != null && wayTagIds[this.tagIDsWays.building$sports
							.intValue()])
					|| (this.tagIDsWays.building$train_station != null && wayTagIds[this.tagIDsWays.building$train_station
							.intValue()])
					|| (this.tagIDsWays.building$university != null && wayTagIds[this.tagIDsWays.building$university
							.intValue()])
					|| (this.tagIDsWays.building$yes != null && wayTagIds[this.tagIDsWays.building$yes
							.intValue()])) {
				addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				this.layer.get(LayerIds.BUILDING$YES).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_BUILDING_YES_OUTLINE));
				this.layer.get(LayerIds.BUILDING$YES).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_BUILDING_YES_FILL));
			}
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* railway */
		if ((wayTagBitmap & BITMAP_RAILWAY) != 0) {
			if (this.tagIDsWays.railway$rail != null
					&& wayTagIds[this.tagIDsWays.railway$rail.intValue()]) {
				/* railway=rail */
				if (this.tagIDsWays.tunnel$yes != null
						&& wayTagIds[this.tagIDsWays.tunnel$yes.intValue()]) {
					this.layer.get(LayerIds.RAILWAY$RAIL_TUNNEL$YES).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_RAIL_TUNNEL));
				} else {
					this.layer.get(LayerIds.RAILWAY$RAIL).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_RAIL1));
					this.layer.get(LayerIds.RAILWAY$RAIL).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_RAIL2));
				}
			} else if (this.tagIDsWays.railway$tram != null
					&& wayTagIds[this.tagIDsWays.railway$tram.intValue()]) {
				/* railway=tram */
				this.layer.get(LayerIds.RAILWAY$TRAM).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_TRAM1));
				this.layer.get(LayerIds.RAILWAY$TRAM).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_TRAM2));
			} else if ((this.tagIDsWays.railway$light_rail != null && wayTagIds[this.tagIDsWays.railway$light_rail
					.intValue()])
					|| (this.tagIDsWays.railway$narrow_gauge != null && wayTagIds[this.tagIDsWays.railway$narrow_gauge
							.intValue()])) {
				/* railway=light_rail */
				this.layer.get(LayerIds.RAILWAY$LIGHT_RAIL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_LIGHT_RAIL1));
				this.layer.get(LayerIds.RAILWAY$LIGHT_RAIL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_LIGHT_RAIL2));
			} else if (this.tagIDsWays.railway$subway != null
					&& wayTagIds[this.tagIDsWays.railway$subway.intValue()]) {
				/* railway=subway */
				if (this.tagIDsWays.tunnel$yes != null
						&& wayTagIds[this.tagIDsWays.tunnel$yes.intValue()]) {
					/* tunnel=yes */
					this.layer.get(LayerIds.RAILWAY$SUBWAY_TUNNEL).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_SUBWAY_TUNNEL));
				} else {
					this.layer.get(LayerIds.RAILWAY$SUBWAY).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_SUBWAY1));
					this.layer.get(LayerIds.RAILWAY$SUBWAY).add(
							new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_SUBWAY2));
				}
			} else if (this.tagIDsWays.railway$station != null
					&& wayTagIds[this.tagIDsWays.railway$station.intValue()]) {
				this.layer.get(LayerIds.RAILWAY$STATION).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_STATION_OUTLINE));
				this.layer.get(LayerIds.RAILWAY$STATION).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_RAILWAY_STATION_FILL));
			}
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* landuse */
		if ((wayTagBitmap & BITMAP_LANDUSE) != 0) {
			if (this.tagIDsWays.landuse$allotments != null
					&& wayTagIds[this.tagIDsWays.landuse$allotments.intValue()]) {
				addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				this.layer.get(LayerIds.LANDUSE$ALLOTMENTS).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_ALLOTMENTS_FILL));
				this.layer.get(LayerIds.LANDUSE$ALLOTMENTS).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_ALLOTMENTS_OUTLINE));
			} else if (this.tagIDsWays.landuse$cemetery != null
					&& wayTagIds[this.tagIDsWays.landuse$cemetery.intValue()]) {
				addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				if (this.currentTile.zoomLevel >= 16) {
					this.layer.get(LayerIds.LANDUSE$CEMETERY)
							.add(
									new ShapePaintContainer(this.shapeContainer,
											PAINT_LANDUSE_CEMETERY_PATTERN));
				}
				this.layer.get(LayerIds.LANDUSE$CEMETERY).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_CEMETERY_FILL));
			} else if ((this.tagIDsWays.landuse$farm != null && wayTagIds[this.tagIDsWays.landuse$farm
					.intValue()])
					|| (this.tagIDsWays.landuse$recreation_ground != null && wayTagIds[this.tagIDsWays.landuse$recreation_ground
							.intValue()])) {
				addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				this.layer.get(LayerIds.LANDUSE$FARM).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_FARM_FILL));
			} else if ((this.tagIDsWays.landuse$basin != null && wayTagIds[this.tagIDsWays.landuse$basin
					.intValue()])
					|| (this.tagIDsWays.landuse$reservoir != null && wayTagIds[this.tagIDsWays.landuse$reservoir
							.intValue()])) {
				this.layer.get(LayerIds.LANDUSE$BASIN).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_BASIN_FILL));
			} else if ((this.tagIDsWays.landuse$brownfield != null && wayTagIds[this.tagIDsWays.landuse$brownfield
					.intValue()])
					|| (this.tagIDsWays.landuse$industrial != null && wayTagIds[this.tagIDsWays.landuse$industrial
							.intValue()])) {
				addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				this.layer.get(LayerIds.LANDUSE$INDUSTRIAL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_INDUSTRIAL_FILL));
			} else if (this.tagIDsWays.landuse$commercial != null
					&& wayTagIds[this.tagIDsWays.landuse$commercial.intValue()]) {
				addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				this.layer.get(LayerIds.LANDUSE$COMMERCIAL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_COMMERCIAL_FILL));
				this.layer.get(LayerIds.LANDUSE$COMMERCIAL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_COMMERCIAL_OUTLINE));
			} else if ((this.tagIDsWays.landuse$construction != null && wayTagIds[this.tagIDsWays.landuse$construction
					.intValue()])
					|| (this.tagIDsWays.landuse$greenfield != null && wayTagIds[this.tagIDsWays.landuse$greenfield
							.intValue()])) {
				this.layer.get(LayerIds.LANDUSE$CONSTRUCTION).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_CONSTRUCTION_FILL));
			} else if ((this.tagIDsWays.landuse$forest != null && wayTagIds[this.tagIDsWays.landuse$forest
					.intValue()])
					|| (this.tagIDsWays.landuse$wood != null && wayTagIds[this.tagIDsWays.landuse$wood
							.intValue()])) {
				if (this.currentTile.zoomLevel >= 14) {
					if (this.tagIDsWays.wood$coniferous != null
							&& wayTagIds[this.tagIDsWays.wood$coniferous.intValue()]) {
						this.layer.get(LayerIds.LANDUSE$FOREST).add(
								new ShapePaintContainer(this.shapeContainer,
										PAINT_WOOD_CONIFEROUS_PATTERN));
					} else if (this.tagIDsWays.wood$deciduous != null
							&& wayTagIds[this.tagIDsWays.wood$deciduous.intValue()]) {
						this.layer.get(LayerIds.LANDUSE$FOREST).add(
								new ShapePaintContainer(this.shapeContainer,
										PAINT_WOOD_DECIDUOUS_PATTERN));
					} else if (this.tagIDsWays.wood$mixed != null
							&& wayTagIds[this.tagIDsWays.wood$mixed.intValue()]) {
						this.layer.get(LayerIds.LANDUSE$FOREST).add(
								new ShapePaintContainer(this.shapeContainer, PAINT_WOOD_MIXED_PATTERN));
					}
				}
				this.layer.get(LayerIds.LANDUSE$FOREST).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_FOREST_FILL));
			} else if ((this.tagIDsWays.landuse$farmland != null && wayTagIds[this.tagIDsWays.landuse$farmland
					.intValue()])
					|| (this.tagIDsWays.landuse$grass != null && wayTagIds[this.tagIDsWays.landuse$grass
							.intValue()])
					|| (this.tagIDsWays.landuse$village_green != null && wayTagIds[this.tagIDsWays.landuse$village_green
							.intValue()])) {
				this.layer.get(LayerIds.LANDUSE$GRASS).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_GRASS_FILL));
				this.layer.get(LayerIds.LANDUSE$GRASS).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_GRASS_OUTLINE));
			} else if (this.tagIDsWays.landuse$military != null
					&& wayTagIds[this.tagIDsWays.landuse$military.intValue()]) {
				this.layer.get(LayerIds.LANDUSE$MILITARY).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_MILITARY_FILL));
			} else if (this.tagIDsWays.landuse$residential != null
					&& wayTagIds[this.tagIDsWays.landuse$residential.intValue()]) {
				// always draw on the lowest layer
				this.ways.get(0).get(LayerIds.LANDUSE$RESIDENTIAL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_RESIDENTIAL_FILL));
			} else if (this.tagIDsWays.landuse$retail != null
					&& wayTagIds[this.tagIDsWays.landuse$retail.intValue()]) {
				this.layer.get(LayerIds.LANDUSE$RETAIL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LANDUSE_RETAIL_FILL));
			}
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* leisure */
		if ((wayTagBitmap & BITMAP_LEISURE) != 0) {
			if ((this.tagIDsWays.leisure$common != null && wayTagIds[this.tagIDsWays.leisure$common
					.intValue()])
					|| (this.tagIDsWays.leisure$garden != null && wayTagIds[this.tagIDsWays.leisure$garden
							.intValue()])
					|| (this.tagIDsWays.leisure$golf_course != null && wayTagIds[this.tagIDsWays.leisure$golf_course
							.intValue()])
					|| (this.tagIDsWays.leisure$park != null && wayTagIds[this.tagIDsWays.leisure$park
							.intValue()])
					|| (this.tagIDsWays.leisure$pitch != null && wayTagIds[this.tagIDsWays.leisure$pitch
							.intValue()])
					|| (this.tagIDsWays.leisure$playground != null && wayTagIds[this.tagIDsWays.leisure$playground
							.intValue()])) {
				addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				this.layer.get(LayerIds.LEISURE$COMMON).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LEISURE_COMMON_FILL));
				this.layer.get(LayerIds.LEISURE$COMMON).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LEISURE_COMMON_OUTLINE));
			} else if (this.tagIDsWays.leisure$nature_reserve != null
					&& wayTagIds[this.tagIDsWays.leisure$nature_reserve.intValue()]) {
				addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				this.layer.get(LayerIds.LEISURE$NATURE_RESERVE).add(
						new ShapePaintContainer(this.shapeContainer,
								PAINT_LEISURE_NATURE_RESERVE_PATTERN));
				this.layer.get(LayerIds.LEISURE$NATURE_RESERVE).add(
						new ShapePaintContainer(this.shapeContainer,
								PAINT_LEISURE_NATURE_RESERVE_OUTLINE));
			} else if ((this.tagIDsWays.leisure$sports_centre != null && wayTagIds[this.tagIDsWays.leisure$sports_centre
					.intValue()])
					|| (this.tagIDsWays.leisure$stadium != null && wayTagIds[this.tagIDsWays.leisure$stadium
							.intValue()])
					|| (this.tagIDsWays.leisure$track != null && wayTagIds[this.tagIDsWays.leisure$track
							.intValue()])
					|| (this.tagIDsWays.leisure$water_park != null && wayTagIds[this.tagIDsWays.leisure$park
							.intValue()])) {
				addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				this.layer.get(LayerIds.LEISURE$STADIUM).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LEISURE_STADIUM_FILL));
				this.layer.get(LayerIds.LEISURE$STADIUM).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_LEISURE_STADIUM_OUTLINE));
			}
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* amenity */
		if ((wayTagBitmap & BITMAP_AMENITY) != 0) {
			if ((this.tagIDsWays.amenity$college != null && wayTagIds[this.tagIDsWays.amenity$college
					.intValue()])
					|| (this.tagIDsWays.amenity$school != null && wayTagIds[this.tagIDsWays.amenity$school
							.intValue()])
					|| (this.tagIDsWays.amenity$university != null && wayTagIds[this.tagIDsWays.amenity$university
							.intValue()])) {
				addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				this.layer.get(LayerIds.AMENITY$SCHOOL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_AMENITY_SCHOOL_FILL));
				this.layer.get(LayerIds.AMENITY$SCHOOL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_AMENITY_SCHOOL_OUTLINE));
			} else if (this.tagIDsWays.amenity$grave_yard != null
					&& wayTagIds[this.tagIDsWays.amenity$grave_yard.intValue()]) {
				this.layer.get(LayerIds.AMENITY$GRAVE_YARD).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_AMENITY_GRAVE_YARD_FILL));
			} else if (this.tagIDsWays.amenity$hospital != null
					&& wayTagIds[this.tagIDsWays.amenity$hospital.intValue()]) {
				/* amenity=hospital */
				if (this.currentTile.zoomLevel >= 15) {
					addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 18);
					addAreaSymbol(this.mapSymbols.hospital, (byte) 16);
				}
				this.layer.get(LayerIds.AMENITY$HOSPITAL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_AMENITY_HOSPITAL_FILL));
			} else if (this.tagIDsWays.amenity$parking != null
					&& wayTagIds[this.tagIDsWays.amenity$parking.intValue()]) {
				/* amenity=parking */
				if (this.currentTile.zoomLevel >= 17) {
					addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 18);
					addAreaSymbol(this.mapSymbols.parking, (byte) 17);
				}
				this.layer.get(LayerIds.AMENITY$PARKING).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_AMENITY_PARKING_FILL));
				this.layer.get(LayerIds.AMENITY$PARKING).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_AMENITY_PARKING_OUTLINE));
			} else if (this.tagIDsWays.amenity$fountain != null
					&& wayTagIds[this.tagIDsWays.amenity$fountain.intValue()]) {
				/* amenity=fountain */
				if (this.currentTile.zoomLevel >= 16) {
					addAreaSymbol(this.mapSymbols.fountain, (byte) 16);
				}
			}
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* natural */
		if ((wayTagBitmap & BITMAP_NATURAL) != 0) {
			if (this.tagIDsWays.natural$beach != null
					&& wayTagIds[this.tagIDsWays.natural$beach.intValue()]) {
				addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				this.layer.get(LayerIds.NATURAL$BEACH).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_NATURAL_BEACH_FILL));
			} else if (this.tagIDsWays.natural$heath != null
					&& wayTagIds[this.tagIDsWays.natural$heath.intValue()]) {
				this.layer.get(LayerIds.NATURAL$HEATH).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_NATURAL_HEATH_FILL));
			} else if (this.tagIDsWays.natural$land != null
					&& wayTagIds[this.tagIDsWays.natural$land.intValue()]) {
				this.layer.get(LayerIds.NATURAL$LAND).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_NATURAL_LAND_FILL));
			} else if ((this.tagIDsWays.natural$scrub != null && wayTagIds[this.tagIDsWays.natural$scrub
					.intValue()])
					|| (this.tagIDsWays.natural$wood != null && wayTagIds[this.tagIDsWays.natural$wood
							.intValue()])) {
				addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				this.layer.get(LayerIds.NATURAL$WOOD).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_NATURAL_WOOD_FILL));
			} else if (this.tagIDsWays.natural$water != null
					&& wayTagIds[this.tagIDsWays.natural$water.intValue()]) {
				addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
				this.layer.get(LayerIds.NATURAL$WATER).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_NATURAL_WATER_FILL));
			} else if ((this.tagIDsWays.natural$marsh != null && wayTagIds[this.tagIDsWays.natural$marsh
					.intValue()])
					|| (this.tagIDsWays.natural$wetland != null && wayTagIds[this.tagIDsWays.natural$wetland
							.intValue()])) {
				this.layer.get(LayerIds.NATURAL$MARSH).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_NATURAL_MARSH_PATTERN));
			} else if (this.tagIDsWays.natural$coastline != null
					&& wayTagIds[this.tagIDsWays.natural$coastline.intValue()]) {
				this.coastlineAlgorithm.addCoastlineSegment(this.coordinates[0]);
			} else if (this.tagIDsWays.natural$glacier != null
					&& wayTagIds[this.tagIDsWays.natural$glacier.intValue()]) {
				this.layer.get(LayerIds.NATURAL$GLACIER).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_NATURAL_GLACIER_FILL));
				this.layer.get(LayerIds.NATURAL$GLACIER).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_NATURAL_GLACIER_OUTLINE));
			}
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* man_made */
		if (this.tagIDsWays.man_made$pier != null
				&& wayTagIds[this.tagIDsWays.man_made$pier.intValue()]) {
			this.layer.get(LayerIds.MAN_MADE$PIER).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_MAN_MADE_PIER));
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* waterway */
		if ((wayTagBitmap & BITMAP_WATERWAY) != 0) {
			if ((this.tagIDsWays.waterway$canal != null && wayTagIds[this.tagIDsWays.waterway$canal
					.intValue()])
					|| (this.tagIDsWays.waterway$drain != null && wayTagIds[this.tagIDsWays.waterway$drain
							.intValue()])) {
				this.layer.get(LayerIds.WATERWAY$CANAL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_WATERWAY_CANAL));
			} else if (this.tagIDsWays.waterway$river != null
					&& wayTagIds[this.tagIDsWays.waterway$river.intValue()]) {
				this.layer.get(LayerIds.WATERWAY$RIVER).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_WATERWAY_RIVER));
			} else if (this.tagIDsWays.waterway$riverbank != null
					&& wayTagIds[this.tagIDsWays.waterway$riverbank.intValue()]) {
				this.layer.get(LayerIds.WATERWAY$RIVERBANK).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_WATERWAY_RIVERBANK_FILL));
			} else if (this.tagIDsWays.waterway$stream != null
					&& wayTagIds[this.tagIDsWays.waterway$stream.intValue()]) {
				this.layer.get(LayerIds.WATERWAY$STREAM).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_WATERWAY_STREAM));
			}
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* barrier */
		if ((this.tagIDsWays.barrier$fence != null && wayTagIds[this.tagIDsWays.barrier$fence
				.intValue()])
				|| (this.tagIDsWays.barrier$wall != null && wayTagIds[this.tagIDsWays.barrier$wall
						.intValue()])) {
			if (this.currentTile.zoomLevel > 15) {
				this.layer.get(LayerIds.BARRIER$WALL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_BARRIER_WALL));
			}
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* boundary */
		if (this.tagIDsWays.boundary$administrative != null
				&& wayTagIds[this.tagIDsWays.boundary$administrative.intValue()]) {
			if (this.tagIDsWays.admin_level$2 != null
					&& wayTagIds[this.tagIDsWays.admin_level$2.intValue()]) {
				this.layer.get(LayerIds.ADMIN_LEVEL$2).add(
						new ShapePaintContainer(this.shapeContainer,
								PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_2));
			} else if (this.tagIDsWays.admin_level$4 != null
					&& wayTagIds[this.tagIDsWays.admin_level$4.intValue()]) {
				this.layer.get(LayerIds.ADMIN_LEVEL$4).add(
						new ShapePaintContainer(this.shapeContainer,
								PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_4));
			} else if (this.tagIDsWays.admin_level$6 != null
					&& wayTagIds[this.tagIDsWays.admin_level$6.intValue()]) {
				this.layer.get(LayerIds.ADMIN_LEVEL$6).add(
						new ShapePaintContainer(this.shapeContainer,
								PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_6));
			} else if (this.tagIDsWays.admin_level$8 != null
					&& wayTagIds[this.tagIDsWays.admin_level$8.intValue()]) {
				this.layer.get(LayerIds.ADMIN_LEVEL$8).add(
						new ShapePaintContainer(this.shapeContainer,
								PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_8));
			} else if (this.tagIDsWays.admin_level$9 != null
					&& wayTagIds[this.tagIDsWays.admin_level$9.intValue()]) {
				this.layer.get(LayerIds.ADMIN_LEVEL$9).add(
						new ShapePaintContainer(this.shapeContainer,
								PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_9));
			} else if (this.tagIDsWays.admin_level$10 != null
					&& wayTagIds[this.tagIDsWays.admin_level$10.intValue()]) {
				this.layer.get(LayerIds.ADMIN_LEVEL$10).add(
						new ShapePaintContainer(this.shapeContainer,
								PAINT_BOUNDARY_ADMINISTRATIVE_ADMIN_LEVEL_10));
			}
			if (--remainingTags <= 0) {
				return;
			}
		} else if (this.tagIDsWays.boundary$national_park != null
				&& wayTagIds[this.tagIDsWays.boundary$national_park.intValue()]) {
			this.layer.get(LayerIds.BOUNDARY$NATIONAL_PARK).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_BOUNDARY_NATIONAL_PARK));
		}

		/* sport */
		if (this.tagIDsWays.sport$shooting != null
				&& wayTagIds[this.tagIDsWays.sport$shooting.intValue()]) {
			this.layer.get(LayerIds.SPORT$SHOOTING).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_SPORT_SHOOTING_FILL));
			this.layer.get(LayerIds.SPORT$SHOOTING).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_SPORT_SHOOTING_OUTLINE));
			if (--remainingTags <= 0) {
				return;
			}
		} else if (this.tagIDsWays.sport$swimming != null
				&& wayTagIds[this.tagIDsWays.sport$swimming.intValue()]) {
			this.layer.get(LayerIds.SPORT$SWIMMING).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_SPORT_SWIMMING_FILL));
			this.layer.get(LayerIds.SPORT$SWIMMING).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_SPORT_SWIMMING_OUTLINE));
			addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
			if (--remainingTags <= 0) {
				return;
			}
		} else if (this.tagIDsWays.sport$tennis != null
				&& wayTagIds[this.tagIDsWays.sport$tennis.intValue()]) {
			this.layer.get(LayerIds.SPORT$TENNIS).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_SPORT_TENNIS_FILL));
			this.layer.get(LayerIds.SPORT$TENNIS).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_SPORT_TENNIS_OUTLINE));
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* aeroway */
		if (this.tagIDsWays.aeroway$aerodrome != null
				&& wayTagIds[this.tagIDsWays.aeroway$aerodrome.intValue()]) {
			this.layer.get(LayerIds.AEROWAY$AERODROME).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_AEROWAY_AERODROME_OUTLINE));
			this.layer.get(LayerIds.AEROWAY$AERODROME).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_AEROWAY_AERODROME_FILL));
			if (--remainingTags <= 0) {
				return;
			}
		} else if (this.tagIDsWays.aeroway$apron != null
				&& wayTagIds[this.tagIDsWays.aeroway$apron.intValue()]) {
			this.layer.get(LayerIds.AEROWAY$APRON).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_AEROWAY_APRON_FILL));
			if (--remainingTags <= 0) {
				return;
			}
		} else if (this.tagIDsWays.aeroway$runway != null
				&& wayTagIds[this.tagIDsWays.aeroway$runway.intValue()]) {
			this.layer.get(LayerIds.AEROWAY$RUNWAY1).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_AEROWAY_RUNWAY1));
			this.layer.get(LayerIds.AEROWAY$RUNWAY2).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_AEROWAY_RUNWAY2));
			if (--remainingTags <= 0) {
				return;
			}
		} else if (this.tagIDsWays.aeroway$taxiway != null
				&& wayTagIds[this.tagIDsWays.aeroway$taxiway.intValue()]) {
			this.layer.get(LayerIds.AEROWAY$TAXIWAY1).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_AEROWAY_TAXIWAY1));
			this.layer.get(LayerIds.AEROWAY$TAXIWAY2).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_AEROWAY_TAXIWAY2));
			if (--remainingTags <= 0) {
				return;
			}
		} else if (this.tagIDsWays.aeroway$terminal != null
				&& wayTagIds[this.tagIDsWays.aeroway$terminal.intValue()]) {
			this.layer.get(LayerIds.AEROWAY$TERMINAL).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_AEROWAY_TERMINAL_OUTLINE));
			this.layer.get(LayerIds.AEROWAY$TERMINAL).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_AEROWAY_TERMINAL_FILL));
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* tourism */
		if (this.tagIDsWays.tourism$attraction != null
				&& wayTagIds[this.tagIDsWays.tourism$attraction.intValue()]) {
			addAreaName(wayName, wayLabelPosition, AREA_NAME_RED, (byte) 0);
			this.layer.get(LayerIds.TOURISM$ATTRACTION).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_TOURISM_ATTRACTION_FILL));
			if (--remainingTags <= 0) {
				return;
			}
		} else if (this.tagIDsWays.tourism$zoo != null
				&& wayTagIds[this.tagIDsWays.tourism$zoo.intValue()]) {
			this.layer.get(LayerIds.TOURISM$ZOO).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_TOURISM_ZOO_FILL));
			this.layer.get(LayerIds.TOURISM$ZOO).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_TOURISM_ZOO_OUTLINE));
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* aerial ways */
		if ((this.tagIDsWays.aerialway$cable_car != null && wayTagIds[this.tagIDsWays.aerialway$cable_car
				.intValue()])
				|| (this.tagIDsWays.aerialway$drag_lift != null && wayTagIds[this.tagIDsWays.aerialway$drag_lift
						.intValue()])
				|| (this.tagIDsWays.aerialway$gondola != null && wayTagIds[this.tagIDsWays.aerialway$gondola
						.intValue()])
				|| (this.tagIDsWays.aerialway$magic_carpet != null && wayTagIds[this.tagIDsWays.aerialway$magic_carpet
						.intValue()])
				|| (this.tagIDsWays.aerialway$mixed_lift != null && wayTagIds[this.tagIDsWays.aerialway$mixed_lift
						.intValue()])
				|| (this.tagIDsWays.aerialway$rope_tow != null && wayTagIds[this.tagIDsWays.aerialway$rope_tow
						.intValue()])) {
			this.layer.get(LayerIds.AERIALWAY$CABLE_CAR).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_AERIALWAY));
			if (wayName != null && this.currentTile.zoomLevel >= 15) {
				addWayName(wayName, PAINT_NAME_WHITE_STROKE_TINY);
			}
			if (--remainingTags <= 0) {
				return;
			}
		} else if (this.tagIDsWays.aerialway$chair_lift != null
				&& wayTagIds[this.tagIDsWays.aerialway$chair_lift.intValue()]) {
			this.layer.get(LayerIds.AERIALWAY$CABLE_CAR).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_AERIALWAY));
			if (wayName != null && this.currentTile.zoomLevel >= 15) {
				addWayName(wayName, PAINT_NAME_WHITE_STROKE_TINY);
				addWaySymbol(this.mapSymbols.chair_lift_2, false, false);
			}
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* pistes */
		if (this.tagIDsWays.piste$type$downhill != null
				&& wayTagIds[this.tagIDsWays.piste$type$downhill.intValue()]) {
			if (this.tagIDsWays.piste$difficulty$novice != null
					&& wayTagIds[this.tagIDsWays.piste$difficulty$novice.intValue()]) {
				this.layer.get(LayerIds.PISTE$TYPE$DOWNHILL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_PISTE_TYPE_DOWNHILL_NOVICE));
				if (wayName != null && this.currentTile.zoomLevel > 15) {
					addWayName(wayName, PAINT_NAME_WHITE_STROKE_TINY);
				}
			} else if (this.tagIDsWays.piste$difficulty$easy != null
					&& wayTagIds[this.tagIDsWays.piste$difficulty$easy.intValue()]) {
				this.layer.get(LayerIds.PISTE$TYPE$DOWNHILL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_PISTE_TYPE_DOWNHILL_EASY));
				if (wayName != null && this.currentTile.zoomLevel > 15) {
					addWayName(wayName, PAINT_NAME_WHITE_STROKE_TINY);
				}
			} else if (this.tagIDsWays.piste$difficulty$intermediate != null
					&& wayTagIds[this.tagIDsWays.piste$difficulty$intermediate.intValue()]) {
				this.layer.get(LayerIds.PISTE$TYPE$DOWNHILL).add(
						new ShapePaintContainer(this.shapeContainer,
								PAINT_PISTE_TYPE_DOWNHILL_INTERMEDIATE));
				if (wayName != null && this.currentTile.zoomLevel > 15) {
					addWayName(wayName, PAINT_NAME_WHITE_STROKE_TINY);
				}
			} else if (this.tagIDsWays.piste$difficulty$advanced != null
					&& wayTagIds[this.tagIDsWays.piste$difficulty$advanced.intValue()]) {
				this.layer.get(LayerIds.PISTE$TYPE$DOWNHILL)
						.add(
								new ShapePaintContainer(this.shapeContainer,
										PAINT_PISTE_TYPE_DOWNHILL_ADVANCED));
				if (wayName != null && this.currentTile.zoomLevel > 15) {
					addWayName(wayName, PAINT_NAME_WHITE_STROKE_TINY);
				}
			} else if (this.tagIDsWays.piste$difficulty$expert != null
					&& wayTagIds[this.tagIDsWays.piste$difficulty$expert.intValue()]) {
				this.layer.get(LayerIds.PISTE$TYPE$DOWNHILL).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_PISTE_TYPE_DOWNHILL_EXPERT));
				if (wayName != null && this.currentTile.zoomLevel > 15) {
					addWayName(wayName, PAINT_NAME_WHITE_STROKE_TINY);
				}
			} else if (this.tagIDsWays.piste$difficulty$freeride != null
					&& wayTagIds[this.tagIDsWays.piste$difficulty$freeride.intValue()]) {
				this.layer.get(LayerIds.PISTE$TYPE$DOWNHILL)
						.add(
								new ShapePaintContainer(this.shapeContainer,
										PAINT_PISTE_TYPE_DOWNHILL_FREERIDE));
				if (wayName != null && this.currentTile.zoomLevel > 15) {
					addWayName(wayName, PAINT_NAME_WHITE_STROKE_TINY);
				}
			}
			if (--remainingTags <= 0) {
				return;
			}
		} else if (this.tagIDsWays.piste$type$nordic != null
				&& wayTagIds[this.tagIDsWays.piste$type$nordic.intValue()]) {
			this.layer.get(LayerIds.PISTE$TYPE$NORDIC).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_PISTE_TYPE_NORDIC));
			if (wayName != null && this.currentTile.zoomLevel > 15) {
				addWayName(wayName, PAINT_NAME_WHITE_STROKE_TINY);
			}
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* route */
		if (this.tagIDsWays.route$ferry != null && wayTagIds[this.tagIDsWays.route$ferry.intValue()]) {
			this.layer.get(LayerIds.ROUTE$FERRY).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_ROUTE_FERRY));
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* military */
		if ((this.tagIDsWays.military$airfield != null && wayTagIds[this.tagIDsWays.military$airfield
				.intValue()])
				|| (this.tagIDsWays.military$barracks != null && wayTagIds[this.tagIDsWays.military$barracks
						.intValue()])) {
			if (this.currentTile.zoomLevel >= 14) {
				this.layer.get(LayerIds.MILITARY$BARRACKS).add(
						new ShapePaintContainer(this.shapeContainer, PAINT_MILITARY_PATTERN));
			}
			this.layer.get(LayerIds.MILITARY$BARRACKS).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_MILITARY_BARRACKS_FILL));
			if (--remainingTags <= 0) {
				return;
			}
		} else if (this.tagIDsWays.military$naval_base != null
				&& wayTagIds[this.tagIDsWays.military$naval_base.intValue()]) {
			this.layer.get(LayerIds.MILITARY$NAVAL_BASE).add(
					new ShapePaintContainer(this.shapeContainer, PAINT_MILITARY_NAVAL_BASE_FILL));
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* historic */
		if (this.tagIDsWays.historic$ruins != null
				&& wayTagIds[this.tagIDsWays.historic$ruins.intValue()]) {
			addAreaName(wayName, wayLabelPosition, AREA_NAME_BLUE, (byte) 0);
			if (--remainingTags <= 0) {
				return;
			}
		}

		/* place */
		if (this.tagIDsWays.place$locality != null
				&& wayTagIds[this.tagIDsWays.place$locality.intValue()]) {
			addAreaName(wayName, wayLabelPosition, AREA_NAME_BLACK, (byte) 0);
			if (--remainingTags <= 0) {
				return;
			}
		}
	}

	/**
	 * Sets the database that should be used.
	 * 
	 * @param database
	 *            the database.
	 */
	final void setDatabase(MapDatabase database) {
		this.database = database;
		if (this.database.hasOpenFile()) {
			onMapFileChange();
		}
	}

	@Override
	final void setupMapGenerator(Bitmap bitmap) {
		this.tileBitmap = bitmap;

		this.coastlineAlgorithm = new CoastlineAlgorithm();
		this.labelPlacement = new LabelPlacement();
		this.mapPatterns = new MapPatterns();
		this.mapSymbols = new MapSymbols();

		initializePaints();

		// set up all data structures for the map objects
		this.ways = new ArrayList<List<List<ShapePaintContainer>>>(LAYERS);
		for (byte i = LAYERS - 1; i >= 0; --i) {
			this.innerWayList = new ArrayList<List<ShapePaintContainer>>(LayerIds.LEVELS_PER_LAYER);
			for (byte j = LayerIds.LEVELS_PER_LAYER - 1; j >= 0; --j) {
				this.innerWayList.add(new ArrayList<ShapePaintContainer>(0));
			}
			this.ways.add(this.innerWayList);
		}
		this.wayNames = new ArrayList<WayTextContainer>(64);
		this.nodes = new ArrayList<PointTextContainer>(64);
		this.areaLabels = new ArrayList<PointTextContainer>(64);
		this.waySymbols = new ArrayList<SymbolContainer>(64);
		this.pointSymbols = new ArrayList<SymbolContainer>(64);

		// create the coordinates array for water tiles
		this.waterTileCoordinates = new float[][] { { 0, 0, Tile.TILE_SIZE, 0, Tile.TILE_SIZE,
				Tile.TILE_SIZE, 0, Tile.TILE_SIZE, 0, 0 } };

		setupRenderer(this.tileBitmap);
	}

	/**
	 * This method is called once during the setup process. It can be used to set up internal data
	 * structures that the renderer needs.
	 * 
	 * @param bitmap
	 *            the bitmap on which all future tiles need to be copied.
	 */
	abstract void setupRenderer(Bitmap bitmap);
}