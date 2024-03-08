package io.donado.sfroutes

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.tomtom.sdk.location.GeoPoint
import io.donado.sfroutes.databinding.ActivityMainBinding
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.OnLocationUpdateListener
import com.tomtom.sdk.location.android.AndroidLocationProvider
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import com.tomtom.sdk.map.display.route.Instruction
import com.tomtom.sdk.map.display.route.RouteOptions
import com.tomtom.sdk.map.display.style.StyleMode
import com.tomtom.sdk.map.display.ui.MapFragment
import com.tomtom.sdk.routing.RoutePlanner
import com.tomtom.sdk.routing.RoutePlanningCallback
import com.tomtom.sdk.routing.RoutePlanningResponse
import com.tomtom.sdk.routing.RoutingFailure
import com.tomtom.sdk.routing.online.OnlineRoutePlanner
import com.tomtom.sdk.routing.options.Itinerary
import com.tomtom.sdk.routing.options.RoutePlanningOptions
import com.tomtom.sdk.routing.options.guidance.GuidanceOptions
import com.tomtom.sdk.routing.route.Route
import com.tomtom.sdk.vehicle.Vehicle
import io.donado.sfroutes.screens.PermissionsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding;
    private lateinit var preferences: AppPreferences;

    private lateinit var helper: TomTomMapHelper;


    private val apiKey = BuildConfig.TOMTOM_API_KEY
    private lateinit var mapFragment: MapFragment;
    private lateinit var tomTomMap: TomTomMap;
    private lateinit var locationProvider: LocationProvider;
    private lateinit var onLocationUpdateListener: OnLocationUpdateListener;

    private lateinit var routePlanner: RoutePlanner;
    private lateinit var routePlanningOptions: RoutePlanningOptions;
    private lateinit var route: Route;


    private val foregroundLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.any { it.value }

        if (granted) {
            initLocationProvider()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init preferences class
        preferences = AppPreferences(this)
        helper = TomTomMapHelper();

        binding.logoutButton.setOnClickListener({ onLogoutClicked() })
        binding.permissionsButton.setOnClickListener({ startPermissionsActivity() })

        ensureForegroundLocationPermission()

        initMap()
        initRouting()
    }

    private fun onLogoutClicked() {
        preferences.setLoggedIn(false)
        startLoginActivity()
        finish()
    }

    // Request the permission
    private fun ensureForegroundLocationPermission() {
        foregroundLocationPermissionLauncher.launch(
            arrayOf(
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
            )
        )
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun startPermissionsActivity() {
        val intent = Intent(this, PermissionsActivity::class.java)
        startActivity(intent)
    }

    private fun showUserLocation() {
        locationProvider.enable()
        // zoom to current location at city level
        onLocationUpdateListener = OnLocationUpdateListener { location ->
            tomTomMap.moveCamera(CameraOptions(location.position, zoom = 12.0))
            locationProvider.removeOnLocationUpdateListener(onLocationUpdateListener)
        }
        locationProvider.addOnLocationUpdateListener(onLocationUpdateListener)
        tomTomMap.setLocationProvider(locationProvider)
        val locationMarker = LocationMarkerOptions(type = LocationMarkerOptions.Type.Pointer)
        tomTomMap.enableLocationMarker(locationMarker)
    }

    private fun drawRoute(
        route: Route,
        color: Int = RouteOptions.DEFAULT_COLOR,
        withDepartureMarker: Boolean = true,
        withZoom: Boolean = true
    ) {
        val instructions = route.legs
            .flatMap { routeLeg -> routeLeg.instructions }
            .map {
                Instruction(
                    routeOffset = it.routeOffset
                )
            }
        val routeOptions = RouteOptions(
            geometry = route.geometry,
            destinationMarkerVisible = true,
            departureMarkerVisible = withDepartureMarker,
            instructions = instructions,
            routeOffset = route.routePoints.map { it.routeOffset },
            color = color,
            tag = route.id.toString()
        )
        tomTomMap.addRoute(routeOptions)
        if (withZoom) {
            tomTomMap.zoomToRoutes(ZOOM_TO_ROUTE_PADDING)
        }
    }
    companion object {
        private const val ZOOM_TO_ROUTE_PADDING = 100
    }

    private val routePlanningCallback = object : RoutePlanningCallback {
        override fun onSuccess(result: RoutePlanningResponse) {
            route = result.routes.first()
            route?.let { drawRoute(it) }
        }

        override fun onFailure(failure: RoutingFailure) {
            Toast.makeText(this@MainActivity, failure.message, Toast.LENGTH_SHORT).show()
        }

        override fun onRoutePlanned(route: Route) = Unit
    }

    private fun calculateRouteTo(destination: GeoPoint) {
        val userLocation =
            tomTomMap.currentLocation?.position ?: return
        val itinerary = Itinerary(origin = userLocation, destination = destination)
        routePlanningOptions = RoutePlanningOptions(
            itinerary = itinerary,
            guidanceOptions = GuidanceOptions(),
            vehicle = Vehicle.Bicycle()
        )
        routePlanner.planRoute(routePlanningOptions, routePlanningCallback)
    }

    // map
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initMap() {
        val cameraOptions: CameraOptions = CameraOptions(zoom = 4.00, tilt=50.50)
        val mapOptions = MapOptions(
            mapKey = apiKey,
            cameraOptions = cameraOptions
        )
        mapFragment = MapFragment.newInstance(mapOptions)
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()
        mapFragment.getMapAsync { map ->
            tomTomMap = map
            // Place the code here to show user location and setup map listeners
            // as explained in the following sections.
            tomTomMap.setStyleMode(StyleMode.DARK)
            showUserLocation()
            helper.drawPolygons(tomTomMap);
        }
    }

    private fun initRouting() {
        routePlanner = OnlineRoutePlanner.create(context = this, apiKey = apiKey)
    }

    private fun initLocationProvider() {
        locationProvider = AndroidLocationProvider(context = this)
        locationProvider.enable()
    }
}