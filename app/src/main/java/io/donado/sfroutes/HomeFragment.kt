package io.donado.sfroutes

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.OnLocationUpdateListener
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import com.tomtom.sdk.map.display.style.StyleMode
import com.tomtom.sdk.map.display.ui.MapFragment
import com.tomtom.sdk.routing.RoutePlanner
import com.tomtom.sdk.routing.options.RoutePlanningOptions
import com.tomtom.sdk.routing.route.Route

class HomeFragment : Fragment() {
    private lateinit var preferences: AppPreferences;

    private lateinit var mapHelper: TomTomMapHelper;

    private val apiKey = BuildConfig.TOMTOM_API_KEY
    private lateinit var mapFragment: MapFragment;
    private lateinit var tomTomMap: TomTomMap;
    private lateinit var locationProvider: LocationProvider;
    private lateinit var onLocationUpdateListener: OnLocationUpdateListener;

    private lateinit var routePlanner: RoutePlanner;
    private lateinit var routePlanningOptions: RoutePlanningOptions;
    private lateinit var route: Route;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


}