package com.example.eticket;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {
    private MapView         mMapView;
    private MapController   mMapController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#36363b")));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "About Us" + "</font>"));

        //mapview
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(true);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(15);
        GeoPoint gPt = new GeoPoint(37.67577032550124, 21.43045773975549);
        mMapController.setCenter(gPt);
        Marker startMarker = new Marker(mMapView);
        startMarker.setPosition(gPt);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(startMarker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about_us_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.my_profile:
                //your action
                Intent my_profile = new Intent(AboutUsActivity.this,ProfileActivity.class);
                startActivity(my_profile);
                break;
            case R.id.kleise_thesi:
                //your action
                Intent kleise_thesi = new Intent(AboutUsActivity.this,RouteActivity.class);
                startActivity(kleise_thesi);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}