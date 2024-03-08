package io.donado.sfroutes

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.polygon.PolygonOptions

class TomTomMapHelper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun drawPolygons (tomTomMap: TomTomMap) {
        val fillColor = Color.argb(0.1F, 1.0F, 1.0F, 0.0F)
        val polygonOptions = PolygonOptions(
            listOf(
                GeoPoint(latitude = 52.33744437330409, longitude = 4.84036333215833),
                GeoPoint(latitude = 52.3374581784774, longitude = 4.88185047814447),
                GeoPoint(latitude = 52.32935816673911, longitude = 4.910078096170823),
                GeoPoint(latitude = 52.381705486736315, longitude = 4.893630047460435),
                GeoPoint(latitude = 52.385294680380866, longitude = 4.846939597146335)
            ),
            outlineColor = Color.BLUE,
            outlineWidth = 2.0,
            fillColor = fillColor
        )
        val polygon = tomTomMap.addPolygon(polygonOptions)
    }

}