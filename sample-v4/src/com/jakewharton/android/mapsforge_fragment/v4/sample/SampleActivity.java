package com.jakewharton.android.mapsforge_fragment.v4.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.jakewharton.android.mapsforge_fragment.GeoPoint;
import com.jakewharton.android.mapsforge_fragment.MapFragment;
import com.jakewharton.android.mapsforge_fragment.MapView;
import com.jakewharton.android.mapsforge_fragment.MapViewMode;

public class SampleActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, new SampleMapFragment()).commit();
    }

    private static class SampleMapFragment extends MapFragment {
    	public SampleMapFragment() {
    		super(MapViewMode.OSMARENDER_TILE_DOWNLOAD);
    	}
    	
		@Override
		public MapView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			MapView view = super.onCreateView(inflater, container, savedInstanceState);
			
			view.setClickable(true);
			view.setBuiltInZoomControls(true);
			view.setCenterAndZoom(new GeoPoint(40.434436, -80.024817), (byte)12);
			
			return view;
		}
    }
}
