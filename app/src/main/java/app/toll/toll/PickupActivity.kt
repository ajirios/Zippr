package com.zippr.zippr

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zippr.zippr.R
import com.zippr.zippr.databinding.ActivityPickupBinding


class PickupActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap;
    private lateinit var pickupBinding: ActivityPickupBinding;
    private lateinit var fusedLocationClient: FusedLocationProviderClient;

    var latitude = 0.0;
    var longitude = 0.0;
    var array = mutableListOf<LatLng>();


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        pickupBinding = ActivityPickupBinding.inflate(layoutInflater);
        setContentView(pickupBinding.root);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.pickupMap) as SupportMapFragment
        mapFragment.getMapAsync(this);

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                // Makes the status bar completely transparent
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                statusBarColor = Color.TRANSPARENT
            }
        }

        val address = StopsActivity.origin?.streetAddress?.split(",");
        if (address != null) {
            pickupBinding.pickupTextView.text = address[0];
        }

        pickupBinding.pickupOptionTextView.text = MapsActivity.selectedTripOption.optionName;
        pickupBinding.pickupFareTextView.text = "NGN${MapsActivity.selectedTripOption.tripFare.toString()}";

        FirebaseApp.initializeApp(this);
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
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

        lifecycleScope.launch {
            delay(500L)
            //val origin = LatLng(6.5244, 3.3792) // Lagos
            //val destination = LatLng(7.3775, 3.9470) // Ibadan
            val originLatitude = StopsActivity.origin?.latitude;
            val originLongitude = StopsActivity.origin?.longitude;
            if (originLatitude != null && originLongitude != null) {
                val origin = LatLng(originLatitude, originLongitude);
                addPolygon();
                mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition(origin, 17F, 5F, 0F)
                    ), 1000, null
                )
            }

        }
    }

    fun updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
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
                    //latitude = location.latitude;
                    //longitude = location.longitude;
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
        }
    }

    fun createCustomMarkerView(context: Context, label: String): BitmapDescriptor {
        val markerView = LayoutInflater.from(context).inflate(R.layout.custom_marker, null)
        val textView = markerView.findViewById<TextView>(R.id.markerLabel)
        textView.text = label

        markerView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)

        val bitmap = Bitmap.createBitmap(
            markerView.measuredWidth, markerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        markerView.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun displayPickupMarker() {
        val originLatitude = StopsActivity.origin?.latitude;
        val originLongitude = StopsActivity.origin?.longitude;
        if (originLatitude != null && originLongitude != null) {
            val origin = LatLng(originLatitude, originLongitude);

            val originMarker = MarkerOptions()
                .position(origin)
                .icon(createCustomMarkerView(this, "Pickup here"))
                .anchor(0.5f, 1f);
            mMap.addMarker(originMarker);
        }
    }

    fun addPolygon(){

        val originLatitude = StopsActivity.origin?.latitude;
        val originLongitude = StopsActivity.origin?.longitude;
        if (originLatitude != null && originLongitude != null) {
            val origin = LatLng(originLatitude, originLongitude);

            val originMarker = MarkerOptions()
                .position(origin)
                .icon(createCustomMarkerView(this, "3 min"))
                .anchor(0.5f, 1f);
            mMap.addMarker(originMarker);

            var latRight = originLatitude + 0.00094;
            var latLeft = originLatitude - 0.00034;
            var lngRight = originLongitude + 0.00084;
            var lngLeft = originLongitude - 0.00034;

            val p0 = LatLng(latRight, lngRight);
            val p1 = LatLng(latRight, lngLeft);
            val p2 = LatLng(latLeft, lngRight);
            val p3 = LatLng(latLeft, lngLeft);
            val polygon = mMap.addPolygon(
                PolygonOptions().apply {
                    add(p0, p1, p3, p2);
                    fillColor(Color.argb(50, 0, 132, 73));
                    strokeColor(Color.parseColor("#008449"));
                    strokeWidth(2f);
                }
            )
        }
    }

    fun addCircle() {
        val lat = StopsActivity.origin?.latitude
        val lng = StopsActivity.origin?.longitude;

        if (lat != null && lng != null) {
            val origin = LatLng(lat, lng);

            val originMarker = MarkerOptions()
                .position(origin)
                .icon(createCustomMarkerView(this, "3 min"))
                .anchor(0.5f, 1f);
            mMap.addMarker(originMarker);

            val circle = mMap.addCircle(
                CircleOptions().apply {
                    center(origin);
                    radius(5000.0);
                    fillColor(Color.GREEN);
                }
            );
        }
    }

    fun writeOrderToUserRealTimeDatabase(userId: String, orderId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users");

        dbRef.child("${userId}/orders/active").setValue(orderId)
            .addOnSuccessListener { Log.d("RTDB", "User order ID saved") }
            .addOnFailureListener { e -> Log.e("RTDB", "Error", e) }
    }

    fun writeOrderToDriverRealTimeDatabase(driverId: String, orderId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("drivers");

        dbRef.child("${driverId}/orders/active").setValue(orderId)
            .addOnSuccessListener { Log.d("RTDB", "Driver order ID saved") }
            .addOnFailureListener { e -> Log.e("RTDB", "Error", e) }
    }
    
    fun onClickConfirmOrderButton(view: View) {
        val originLatitude = StopsActivity.origin?.latitude;
        val originLongitude = StopsActivity.origin?.longitude;
        val originAddress = StopsActivity.origin?.streetAddress ?: " ";
        val destinationLatitude = StopsActivity.destination?.latitude;
        val destinationLongitude = StopsActivity.destination?.longitude;
        val destinationAddress = StopsActivity.destination?.streetAddress ?: " ";
        if (originLatitude != null && originLongitude != null && destinationLatitude != null && destinationLongitude != null) {
            var pickupLocation: com.zippr.zippr.Location = com.zippr.zippr.Location(originLatitude, originLongitude, originAddress);
            var dropoffLocation: com.zippr.zippr.Location = com.zippr.zippr.Location(destinationLatitude, destinationLongitude, destinationAddress);
            var order: Order = Order(null, "Z977082734123", "Z9071234597548301UMG",
                pickupLocation, dropoffLocation, "requested", 33000.00, "NGN", "3:09 PM",
                "cash", 7.9, 20, false, "", null, "Automobile", "Economy");

            val dbRef = FirebaseDatabase.getInstance().getReference("orders");
            val newOrderRef = dbRef.push();
            val orderId = newOrderRef.key ?: " ";
            newOrderRef.setValue(order)
                .addOnSuccessListener {
                    Log.d("RTDB", "Order with ID $orderId saved");
                    order.driverId?.let { it1 -> writeOrderToDriverRealTimeDatabase(it1, orderId) };
                    order.userId?.let { it1 -> writeOrderToUserRealTimeDatabase(it1, orderId) };

                    pickupBinding.pickupModal.visibility = View.GONE;
                    pickupBinding.confirmButtonModal.visibility = View.GONE;
                    pickupBinding.driverStateModal.visibility = View.VISIBLE;
                    mMap.clear();
                    displayPickupMarker();
                }
                .addOnFailureListener { e -> Log.e("RTDB", "Error saving order", e) }
        }

    }

    fun onClickPickupBackButton(view: View) {
        finish();
    }
}