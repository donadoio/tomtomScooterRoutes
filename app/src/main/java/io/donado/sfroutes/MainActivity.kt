package io.donado.sfroutes

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.OnLocationUpdateListener
import com.tomtom.sdk.location.android.AndroidLocationProvider
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import com.tomtom.sdk.map.display.style.StyleMode
import com.tomtom.sdk.map.display.ui.MapFragment
import io.donado.sfroutes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding;
    private lateinit var preferences: AppPreferences;

    // Drawer menu declarations
    private lateinit var navController: NavController;
    private lateinit var menuToggle: ActionBarDrawerToggle;

    // TomTom declarations
    private lateinit var mapHelper: TomTomMapHelper;
    private val apiKey = BuildConfig.TOMTOM_API_KEY
    private lateinit var locationProvider: LocationProvider;
    private lateinit var mapFragment: MapFragment;
    private lateinit var tomTomMap: TomTomMap;
    private lateinit var onLocationUpdateListener: OnLocationUpdateListener;

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

        preferences = AppPreferences(this)

        // Drawer menu logic
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.navigationView, navController)

        menuToggle = ActionBarDrawerToggle(this, binding.drawerLayout, 0, 0)
        binding.drawerLayout.addDrawerListener(menuToggle)
        menuToggle.syncState()

        // Permissions
        ensureForegroundLocationPermission()

        // TomTom Map initialization logic
        mapHelper = TomTomMapHelper();
        initMap()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initMap() {
        val cameraOptions: CameraOptions = CameraOptions(zoom = 4.00, tilt=50.50)
        val mapOptions = MapOptions(
            mapKey = apiKey,
            cameraOptions = cameraOptions,
        )
        mapFragment = MapFragment.newInstance(mapOptions)
        supportFragmentManager.beginTransaction()
            .replace(binding.navHostFragmentContainer.id, mapFragment)
            .commit()
        mapFragment.getMapAsync { map ->
            tomTomMap = map
            // Place the code here to show user location and setup map listeners
            // as explained in the following sections.
            tomTomMap.setStyleMode(StyleMode.DARK)
            showUserLocation()
            val polygon = mapHelper.drawPolygons(tomTomMap);
            tomTomMap.addMapClickListener { coordinate: GeoPoint ->
                /* YOUR CODE GOES HERE */
                println("STONK")
                println(coordinate);
                return@addMapClickListener true
            }
        }
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (menuToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initLocationProvider() {
        locationProvider = AndroidLocationProvider(context = this)
        locationProvider.enable()
    }

    private fun ensureForegroundLocationPermission() {
        foregroundLocationPermissionLauncher.launch(
            arrayOf(
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
            )
        )
    }
}