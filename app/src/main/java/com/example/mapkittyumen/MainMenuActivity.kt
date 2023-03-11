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
        MapKitFactory.setApiKey("c18b5cf8-3c97-4207-8a63-6d5d061ba136")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main_menu)
        mapView = findViewById(R.id.mapview)
        mapView.map.move(
            CameraPosition(Point(57.152985, 65.541227), 10.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 2.0f), null
        )

        var mapkit:MapKit = MapKitFactory.getInstance()
        requstLocationPermission()
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

    private fun requstLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 0)
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
        locationMapKit.setAnchor(
            PointF((mapView.width() * 0.5).toFloat(), (mapView.height() * 0.5).toFloat()),
            PointF((mapView.width() * 0.5).toFloat(), (mapView.height() * 0.83).toFloat())
        )
        userLocationView.arrow.setIcon(ImageProvider.fromResource(this, R.drawable.navigator))

        val picIcon = userLocationView.pin.useCompositeIcon()
        picIcon.setIcon("recycling", ImageProvider.fromResource(this, R.drawable.recycling),
            IconStyle().setRotationType(RotationType.ROTATE).setZIndex(0f).setScale(1f)
        )
        picIcon.setIcon("pin", ImageProvider.fromResource(this, R.drawable.nothing),
        IconStyle().setAnchor(PointF(0.5f, 0.5f)).setRotationType(RotationType.ROTATE).setZIndex(1f).setScale(0.5f))
        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(p0: UserLocationView) {
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
    }

    override fun onSearchResponse(response: Response) {
        val mapObjects:MapObjectCollection = mapView.map.mapObjects
        mapObjects.clear()
        for (searchResult in response.collection.children){
            val resultLocation = searchResult.obj!!.geometry[0].point!!
            if (response != null){
                mapObjects.addPlacemark(resultLocation, ImageProvider.fromResource(this, R.drawable.placeholder))
            }
        }
    }

    override fun onSearchError(error: Error) {
        var errorMessage = "Неизвестная ошибка!"
        if (error is RemoteError){
            errorMessage = "Фатальная ошибка!"
        }
        else if (error is NetworkError){
            errorMessage = "Проблемы с интернетом!"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished){
            sumbitQuery(searchEdit.text.toString())
        }
    }

}