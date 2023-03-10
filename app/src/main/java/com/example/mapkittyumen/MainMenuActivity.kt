package com.example.mapkittyumen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.Display.Mode
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.*
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError

class MainMenuActivity : AppCompatActivity(), UserLocationObjectListener, Session.SearchListener, CameraListener{

    lateinit var mapView:MapView
    lateinit var probkibut: Button
    lateinit var locationMapKit: UserLocationLayer
    lateinit var userLocationLayer: UserLocationLayer
    lateinit var searchManager: SearchManager
    lateinit var searchEdit: EditText
    lateinit var searchSession: Session

    private fun sumbitQuery(query:String){
        searchSession = searchManager.submit(query, VisibleRegionUtils.toPolygon(mapView.map.visibleRegion), SearchOptions(), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("api key")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main_menu)
        mapView = findViewById(R.id.mapview)
        mapView.map.move(
            CameraPosition(Point(57.152985, 65.541227), 10.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 2.0f), null
        )

        var mapkit:MapKit = MapKitFactory.getInstance()
        requestLocatePermission()
        var probki = mapkit.createTrafficLayer(mapView.mapWindow)
        probki.isTrafficVisible = false
        var locationOnMapkit = mapkit.createUserLocationLayer(mapView.mapWindow)
        locationOnMapkit.isVisible = true
        probkibut = findViewById(R.id.probkiBoutton)
        probkibut.setOnClickListener{
            if (probki.isTrafficVisible == false){
                probki.isTrafficVisible = true
                probkibut.setBackgroundResource(R.drawable.simplegreen)
            }
            else{
                probki.isTrafficVisible = true
                probkibut.setBackgroundResource(R.drawable.butoff)
            }
        }
        locationMapKit = mapkit.createUserLocationLayer(mapView.mapWindow)
        locationMapKit.isVisible = true
        locationMapKit.setObjectListener(this)
        SearchFactory.initialize(this)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        mapView.map.addCameraListener(this)
        searchEdit = findViewById(R.id.search_field)
        searchEdit.setOnEditorActionListener{ v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                sumbitQuery(searchEdit.text.toString())
            }
            false
        }
    }

    private fun requestLocatePermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),0)
            return
        }
    }


    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {

    }

    override fun onObjectRemoved(p0: UserLocationView) {
        TODO("Not yet implemented")
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
        TODO("Not yet implemented")
    }

    override fun onSearchResponse(p0: Response) {
        TODO("Not yet implemented")
    }

    override fun onSearchError(p0: Error) {
        TODO("Not yet implemented")
    }

    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateReason,
        p3: Boolean
    ) {
        TODO("Not yet implemented")
    }

}