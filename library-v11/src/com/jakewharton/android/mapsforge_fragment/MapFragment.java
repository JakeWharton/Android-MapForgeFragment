package com.jakewharton.android.mapsforge_fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class MapFragment extends Fragment {
	private static final String KEY_LATITUDE_E6 = "MapFragment:LatitudeE6";
	private static final String KEY_LONGITUDE_E6 = "MapFragment:LongitudeE6";
	private static final String KEY_ZOOM_LEVEL = "MapFragment:ZoomLevel";
	private static final String KEY_MAP_FILE = "MapFragment:MapFile";
	
	private final MapViewMode mMapViewMode;
	
	
	public MapFragment() {
		this(MapView.DEFAULT_MAP_VIEW_MODE);
	}
	
	public MapFragment(MapViewMode mapViewMode) {
		mMapViewMode = mapViewMode;
	}


	@Override
	public MapView getView() {
		return (MapView)super.getView();
	}
	
	@Override
	public MapView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MapView mapView = new MapView(getActivity(), mMapViewMode);
		if (savedInstanceState != null) {
			final int latitude = savedInstanceState.getInt(KEY_LATITUDE_E6, Integer.MIN_VALUE);
			final int longitude = savedInstanceState.getInt(KEY_LONGITUDE_E6, Integer.MIN_VALUE);
			final byte zoom = savedInstanceState.getByte(KEY_ZOOM_LEVEL, Byte.MIN_VALUE);
			final String mapFile = savedInstanceState.getString(KEY_MAP_FILE);
			
			if ((mapFile != null) && !mapView.getMapViewMode().requiresInternetConnection()) {
				mapView.setMapFileFromParcel(mapFile);
			}
			
			if ((latitude != Integer.MIN_VALUE) && (longitude != Integer.MIN_VALUE) && (zoom != Byte.MIN_VALUE)) {
				mapView.setCenterAndZoom(new GeoPoint(latitude, longitude), zoom);
			}
		}
		
		return mapView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MapView mapView = getView();
		if (mapView != null) {
			mapView.destroy();
			mapView = null;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		MapView mapView = getView();
		if (mapView.hasValidCenter()) {
			if (!mapView.getMapViewMode().requiresInternetConnection() && (mapView.getMapFile() != null)) {
				outState.putString(KEY_MAP_FILE, mapView.getMapFile());
			}
			
			GeoPoint center = mapView.getMapCenter();
			outState.putInt(KEY_LATITUDE_E6, center.getLatitudeE6());
			outState.putInt(KEY_LONGITUDE_E6, center.getLongitudeE6());
			outState.putByte(KEY_ZOOM_LEVEL, mapView.getZoomLevel());
		}
	}
}
