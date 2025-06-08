package app.toll.toll

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class MapsFragment : Fragment() {

    private lateinit var mMap: GoogleMap;
    private lateinit var fusedLocationClient: FusedLocationProviderClient;
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001;

    var placeAddress = "";
    var latitude = 0.0;
    var longitude = 0.0;

    var spinnerOptions = mutableListOf<String>();

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        mMap = googleMap;

        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true;
            isZoomGesturesEnabled = true;
            isScrollGesturesEnabled = true;
            isMapToolbarEnabled = true;
        }

        googleMap.isMyLocationEnabled = true;
        googleMap.setMinZoomPreference(9f);
        googleMap.setMaxZoomPreference(18f);
        updateLocation();

        val adapter = ArrayAdapter(requireActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, spinnerOptions);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        val spinner = view?.findViewById<Spinner>(R.id.spinner);
        if (spinner != null) {
            spinner.adapter = adapter
        };
        val editTextText = view?.findViewById<EditText>(R.id.editTextText);
        if (editTextText != null) {
            editTextText.setOnClickListener { view ->
                val address = getAddressFromLatLng(requireActivity(), latitude, longitude);
                placeAddress = "$address";
                val intent = Intent(requireActivity(), StopsActivity::class.java);
                intent.putExtra("origin", placeAddress);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                StopsActivity.trip.origin = Place(placeAddress, "", latitude, longitude, "");
                startActivity(intent);
                false
            }
        }
    }

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback);
        
        spinnerOptions.add("Auto");
        spinnerOptions.add("Motorbike");
        spinnerOptions.add("Tricycle");
        spinnerOptions.add("Truck");
        spinnerOptions.add("Trailer");
        spinnerOptions.add("Bicycle");
        spinnerOptions.add("Tanker");

        @Suppress("DEPRECATION")
        requireActivity().window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun checkLocationPermissionAndEnableMap(map: GoogleMap) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            // Ask for permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            var add = ""
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address: Address = addresses[0]
                    add = address.getAddressLine(0)
                } else {
                    null
                }
            }
            add

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                latitude = location.latitude;
                longitude = location.longitude;
            } else {
                // Location is null (first launch or GPS not ready yet), request a new location
                requestNewLocation()
            }
        }
    }

    private fun requestNewLocation() {
        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1 // We only need one update
            interval = 0
        }

        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                val location = locationResult.lastLocation;
                val userLatLng = location?.let { LatLng(it.latitude, location.longitude) }
                if (userLatLng != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, requireActivity().mainLooper)
        }
    }

}