package io.donado.sfroutes

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.android.AndroidLocationProvider
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

    private val foregroundLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.any { it.value }

        if (granted) {
            initLocationProvider()
        }
    }

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