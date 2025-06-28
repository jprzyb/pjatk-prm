package pl.pjatk.proj_2

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class LocationHelper(private val context: Context) {
    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCityName(onResult: (String) -> Unit) {
        fusedClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val city = address?.firstOrNull()?.locality ?: "Nieznane"
                onResult(city)
            } else {
                onResult("Lokalizacja niedostępna")
            }
        }.addOnFailureListener {
            onResult("Błąd lokalizacji")
        }
    }
}
