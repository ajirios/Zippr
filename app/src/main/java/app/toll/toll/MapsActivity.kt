package app.toll.toll

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import app.toll.toll.databinding.ActivityMapsBinding
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.maps.model.Marker
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import java.io.InputStreamReader
import java.net.HttpURLConnection
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    public lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient;

    var placeAddress = "";
    var latitude = 0.0;
    var longitude = 0.0;
    var currentTime = "00:00";
    var tripDuration: Int = 0  // Duration in minutes
    var tripDistanceKm: Float = 0f  // Optional: Distance in kilometers

    companion object Options {
        var tripOptions: MutableList<TripOption> = mutableListOf<TripOption>();
        var selectedTripOption: TripOption = TripOption("Toll", 6, 4, 3700);
    }

    var updateJob: Job? = null;
    private val API_KEY: String = "API_KEY";

    lateinit var originMarker: Marker;
    lateinit var destinationMarker: Marker;

    private lateinit var recyclerView: RecyclerView;
    private lateinit var viewAdapter: RecyclerView.Adapter<*>;
    private lateinit var viewManager: RecyclerView.LayoutManager;


    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.hailMap) as SupportMapFragment;
        mapFragment.getMapAsync(this);
        //updateLocation();
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

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

        binding.endEditText.setText(intent.getStringExtra("destination"));

        tripOptions.add(TripOption("Toll", 0, 0, 3700));
        tripOptions.add(TripOption("Economy", 0, 0, 8300));
        tripOptions.add(TripOption("Standard", 0, 0, 10200));
        tripOptions.add(TripOption("Premium", 0, 0, 18300));

        selectedTripOption = tripOptions.get(0);
        val button3 = findViewById<Button>(R.id.button3);
        button3.text = "Select ${selectedTripOption.optionName}";

        viewManager = LinearLayoutManager(this);
        viewAdapter = TripOptionRecyclerAdapter(tripOptions, this);
        recyclerView = findViewById<RecyclerView>(R.id.tripOptionRecyclerView).apply {
            setHasFixedSize(true);
            layoutManager = viewManager;
            adapter = viewAdapter;
        }
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

        currentTime = getCurrentTime("America/Toronto");

        /*
        lifecycleScope.launch {
            //delay(4000L);
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(43.88032, -81.830342), 12F));
            delay(4000L);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(LatLng(43.0932, -81.02834), 15F, 5F, 0F)), 4000, null);
        }
        co-routine
        */

        lifecycleScope.launch {
            delay(100L);
            //val origin = LatLng(6.5244, 3.3792) // Lagos
            //val destination = LatLng(7.3775, 3.9470) // Ibadan
            val originLatitude = StopsActivity.origin?.latitude;
            val originLongitude = StopsActivity.origin?.longitude;
            val destinationLatitude = StopsActivity.destination?.latitude;
            val destinationLongitude = StopsActivity.destination?.longitude;
            if (originLatitude != null && originLongitude != null && destinationLatitude != null && destinationLongitude != null) {
                if (latitude == 0.0 || longitude == 0.0) {
                    latitude = originLatitude;
                    longitude = originLongitude;
                }
                val origin = LatLng(originLatitude, originLongitude);
                val destination = LatLng(destinationLatitude, destinationLongitude);
                updateTripOptionsByDriversAndDistances();

                fetchAndDrawRoute(origin, destination)

                mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition(origin, 13F, 5F, 0F)
                    ), 4000, null
                )
            }
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

    fun updateAddress() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude;
                val longitude = location.longitude;
                val address = getAddressFromLatLng(this, latitude, longitude);
                placeAddress = "$address";
                //Toast.makeText(this, "$latitude, $longitude", Toast.LENGTH_SHORT).show();
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

    private fun getDirectionsUrl(origin: LatLng, destination: LatLng): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${destination.latitude},${destination.longitude}"
        val mode = "mode=driving";
        val key = "key=$API_KEY"; // 🔁 Replace with your real API key
        return "https://maps.googleapis.com/maps/api/directions/json?$strOrigin&$strDest&$mode&$key"
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng((lat / 1E5), (lng / 1E5))
            poly.add(p)
        }

        return poly;
    }

    private fun drawRoute(points: List<LatLng>) {

        val polylineOptions = PolylineOptions()
            .addAll(points)
            .width(12f)
            .color(Color.parseColor("#008449")) // Dark green
            .geodesic(true)

        mMap.addPolyline(polylineOptions)
    }

    private fun fetchAndDrawRoute(origin: LatLng, destination: LatLng) {
        var durationComputed = 3 + 1;

        val url = getDirectionsUrl(origin, destination);

        Log.d("fetching directions: ", "directions fetching..");
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = URL(url).readText();
                Log.d("DIRECTIONS_RESPONSE", response);

                val json = JSONObject(response)
                val routes = json.getJSONArray("routes");
                val status = json.getString("status")
                Log.d("DIRECTIONS_STATUS", status)

                if (status == "OK") {
                    val routes = json.getJSONArray("routes");
                    val leg = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);

                    // Extract duration in seconds, then convert to minutes
                    val durationInSeconds = leg.getJSONObject("duration").getInt("value");
                    tripDuration = durationInSeconds / 60;
                    durationComputed = durationComputed + durationInSeconds / 60;

                    // Optional: Extract distance in meters and convert to kilometers
                    val distanceInMeters = leg.getJSONObject("distance").getInt("value");
                    tripDistanceKm = distanceInMeters / 1000f;

                    Log.d("DIRECTIONS_DURATION", "Trip duration: $tripDuration minutes, $durationComputed computed");
                    Log.d("DIRECTIONS_DISTANCE", "Trip distance: $tripDistanceKm km");
                    val overviewPolyline = routes.getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points")
                    val points = decodePolyline(overviewPolyline)

                    withContext(Dispatchers.Main) {
                        drawRoute(points)
                    }

                    if (routes.length() > 0) {
                        val overviewPolyline = routes.getJSONObject(0)
                            .getJSONObject("overview_polyline")
                            .getString("points")

                        val points = decodePolyline(overviewPolyline)

                        withContext(Dispatchers.Main) {
                            drawRoute(points)
                        }
                    }


                    withContext(Dispatchers.Main) {
                        val waitTime = tripOptions.get(0).waitTime;

                        val destinationTime = computeDestinationTime(waitTime + 1 + tripDuration);
                        originMarker = mMap.addMarker(
                            MarkerOptions()
                                .position(origin)
                                .icon(createCustomMarkerView(this@MapsActivity, "$waitTime min"))
                                .anchor(0.5f, 1f)
                        )!!

                        destinationMarker = mMap.addMarker(
                            MarkerOptions()
                                .position(destination)
                                .icon(createCustomMarkerView(this@MapsActivity, "Arrives at $destinationTime"))
                                .anchor(0.5f, 1f)
                        )!!
                    }

                } else {
                    Log.e("DIRECTIONS_API", "API Error: $status")
                }
            } catch (e: Exception) {
                Log.e("MapsActivity", "Error fetching route", e)
            }
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

    private fun getCurrentTime(timeZoneId: String): String {
        return try {
            val timeZone = TimeZone.getTimeZone(timeZoneId)
            if (timeZone.id == "GMT" && timeZoneId != "GMT") {
                // Invalid timezone, fallback to device default
                getCurrentTimeFromTimeZone(TimeZone.getDefault())
            } else {
                getCurrentTimeFromTimeZone(timeZone);
            }
        } catch (e: Exception) {
            // Fallback to device timezone on error
            getCurrentTimeFromTimeZone(TimeZone.getDefault())
        }
    }

    private fun getCurrentTimeFromTimeZone(timeZone: TimeZone): String {
        val calendar = Calendar.getInstance(timeZone)
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        formatter.timeZone = timeZone;
        return formatter.format(calendar.time);
    }

    fun computeDestinationTime(minutes: Int): String {
        var destinationTime = "1";
        var hourComputed = 0;
        var minuteComputed = 0;
        var meridian = "am";

        val array = currentTime.split(":");
        if (array.size > 1 && array[0].isNotEmpty()) {
            hourComputed = Integer.parseInt(array[0]);
            minuteComputed = Integer.parseInt(array[1]);
            minuteComputed = minuteComputed + minutes;
            if (minuteComputed >= 60) {
                val floor = minuteComputed.floorDiv(60);
                hourComputed += floor;
                minuteComputed = minuteComputed.rem(60);
            }
        }
        else {
            destinationTime = "4";
        }

        if (hourComputed in 13..23) {
            hourComputed -= 12;
            meridian = "pm";
            if (minuteComputed < 10) {
                destinationTime = "${hourComputed}:0${minuteComputed}${meridian}";
            }
            else {
                destinationTime = "${hourComputed}:${minuteComputed}${meridian}";
            }
        }
        else if (hourComputed in 1..11) {
            meridian = "am";
            if (minuteComputed < 10) {
                destinationTime = "${hourComputed}:0${minuteComputed}${meridian}";
            }
            else {
                destinationTime = "${hourComputed}:${minuteComputed}${meridian}";
            }
        }
        else if (hourComputed == 0) {
            hourComputed = 12;
            meridian = "am";
            if (minuteComputed < 10) {
                destinationTime = "${hourComputed}:0${minuteComputed}${meridian}";
            }
            else {
                destinationTime = "${hourComputed}:${minuteComputed}${meridian}";
            }
        }
        else if (hourComputed == 12) {
            meridian = "pm";
            if (minuteComputed < 10) {
                destinationTime = "${hourComputed}:0${minuteComputed}${meridian}";
            }
            else {
                destinationTime = "${hourComputed}:${minuteComputed}${meridian}";
            }
        }
        else if (hourComputed == 24) {
            hourComputed = 12;
            meridian = "am";
            if (minuteComputed < 10) {
                destinationTime = "${hourComputed}:0${minuteComputed}${meridian}";
            }
            else {
                destinationTime = "${hourComputed}:${minuteComputed}${meridian}";
            }
        }
        else {
            destinationTime = "3";
        }

        return destinationTime;
    }

    fun initializeFirebaseDriverListeners() {

        FirebaseApp.initializeApp(this);
        //writeToFirebaseRealTimeDatabase();

        val dbRef = FirebaseDatabase.getInstance().getReference("drivers");

        dbRef.addChildEventListener(object : ChildEventListener {
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Check if the changed node contains the "score" field
                if (snapshot.hasChild("dashboard/location/latitude")) {
                    val updatedScore = snapshot.child("dashboard/location/latitude").getValue(Double::class.java);
                    val updatedLongitude = snapshot.child("dashboard/location/longitude").getValue(Double::class.java);
                    val userId = snapshot.key;
                    if (userId == "Z9071234597548301UMG") {
                        var displayable = "";
                        updatedScore?.let {
                            displayable += "$it";
                            Log.d("RTDB", "Score updated: $it");
                        }
                        updatedLongitude?.let {
                            displayable += ", $it";
                        }
                        //mainBinding.scoreTextView.text = displayable;
                    }
                }
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.hasChild("dashboard/location/latitude")) {
                    val updatedScore = snapshot.child("dashboard/location/latitude").getValue(Double::class.java);
                    val updatedLongitude = snapshot.child("dashboard/location/longitude").getValue(Double::class.java);
                    val userId = snapshot.key;
                    if (userId == "Z9071234597548301UMG") {
                        var displayable = "";
                        updatedScore?.let {
                            displayable += "$it";
                            Log.d("RTDB", "Score updated: $it");
                        }
                        updatedLongitude?.let {
                            displayable += ", $it";
                        }
                        //mainBinding.scoreTextView.text = displayable;
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("RTDB", "Listener cancelled", error.toException())
            }
        });
    }

    fun readFromFirebaseRealTimeDatabase() : Int {
        var score: Int? = 0;
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.child("alice123").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                score = snapshot.child("score").getValue(Int::class.java)
                Log.d("RTDB", "Name: $name, Score: $score");
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RTDB", "Error", error.toException())
            }
        });
        return score as Int;
    }

    suspend fun getDriverDistances(
        userLocation: LatLng,  // User's location (latitude and longitude)
        drivers: List<Driver>, // List of drivers
        apiKey: String  // Google Maps API Key
    ): List<Pair<String, Double>> {  // Pair of driver ID and distance in km

        // Construct the origins (user location) and destinations (driver locations) as strings
        val origins = "${userLocation.latitude},${userLocation.longitude}"
        val destinations = drivers.joinToString("|") {
            "${it.dashboard?.location?.latitude},${it.dashboard?.location?.longitude}"
        }

        // Build the URL for the Distance Matrix API request
        val url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=$origins&destinations=$destinations&key=$apiKey"
        Log.d("DistanceMatrix", "Request URL: $url");

        // Create OkHttp client
        val client = OkHttpClient();

        // Create the request
        val request = Request.Builder()
            .url(url)
            .build();
        Log.d("getDriverDistances", "Getting distances started 0...");

        // Initialize the result list
        val result = mutableListOf<Pair<String, Double>>();

        try {
            // Execute the request
            val response: Response = client.newCall(request).execute();
            Log.d("getDriverDistances", "Getting distances started 1...");

            // Check for a successful response code
            if (response.isSuccessful) {
                // Get the response body as a string
                val responseBody = response.body?.string();

                Log.d("DistanceMatrix", "Response: $responseBody")

                // Parse the response as JSON
                val jsonResponse = JSONObject(responseBody);
                val rows = jsonResponse.getJSONArray("rows")
                val elements = rows.getJSONObject(0).getJSONArray("elements")


                tripOptions.get(0).waitTime = 900;
                tripOptions.get(0).driverCount = 0;
                tripOptions.get(1).waitTime = 900;
                tripOptions.get(1).driverCount = 0;
                tripOptions.get(2).waitTime = 900;
                tripOptions.get(2).driverCount = 0;
                tripOptions.get(3).waitTime = 900;
                tripOptions.get(3).driverCount = 0;

                // Iterate over the elements and parse the distances
                for (i in 0 until elements.length()) {
                    val element = elements.getJSONObject(i);
                    val status = element.getString("status");

                    if (status == "OK") {
                        val driver = drivers[i]
                        val distance = element.getJSONObject("distance");
                        val duration = element.getJSONObject("duration");
                        val distanceInMeters = distance.getInt("value") // distance in meters
                        val distanceInKm = distanceInMeters / 1000.0 // Convert to kilometers
                        val durationInMinutes = duration.getString("text");
                        val durationText: String = durationInMinutes.split(" ")[0];
                        val durationInt: Int = durationText.toInt();

                        val assignedOption = driver.profile?.assignedOption;
                        if (assignedOption != null) {
                            if (assignedOption == "Toll") {
                                val driverCount = tripOptions.get(0).driverCount + 1;
                                tripOptions.get(0).driverCount = driverCount;
                                //tripOptions.get(0).waitTime = 900;
                                //tripOptions.get(0).tripFare = calculateTripCost(distanceInKm).toInt();
                                if (durationInt < tripOptions.get(0).waitTime) {
                                    tripOptions.get(0).waitTime = durationInt;
                                }
                            }
                            else if (assignedOption == "Economy") {
                                val driverCount = tripOptions.get(1).driverCount + 1;
                                tripOptions.get(1).driverCount = driverCount;
                                //tripOptions.get(1).waitTime = 900;
                                if (durationInt < tripOptions.get(1).waitTime) {
                                    tripOptions.get(1).waitTime = durationInt;
                                }
                            }
                            else if (assignedOption == "Standard") {
                                val driverCount = tripOptions.get(2).driverCount + 1;
                                tripOptions.get(2).driverCount = driverCount;
                                //tripOptions.get(2).waitTime = 900;
                                if (durationInt < tripOptions.get(2).waitTime) {
                                    tripOptions.get(2).waitTime = durationInt;
                                }
                            }
                            else if (assignedOption == "Premium") {
                                val driverCount = tripOptions.get(3).driverCount + 1;
                                tripOptions.get(3).driverCount = driverCount;
                                if (durationInt < tripOptions.get(3).waitTime) {
                                    tripOptions.get(3).waitTime = durationInt;
                                }
                            }

                            tripOptions.get(0).tripFare = calculateTripCost(distanceInKm, "Toll").toInt();
                            tripOptions.get(1).tripFare = calculateTripCost(distanceInKm, "Economy").toInt();
                            tripOptions.get(2).tripFare = calculateTripCost(distanceInKm, "Standard").toInt();
                            tripOptions.get(3).tripFare = calculateTripCost(distanceInKm, "Premium").toInt();
                        }

                        // Add the driver and their distance to the result list
                        val driverName = driver.profile?.firstName ?: "Unknown Driver"
                        result.add(driverName to distanceInKm);
                    }
                }
                
                Log.d("TripOptions List", "$tripOptions");
            } else {
                // Log the error if the request was not successful
                Log.e("DistanceMatrix", "Request failed with response code: ${response.code}")
            }
        } catch (e: Exception) {
            // Log any exceptions
            Log.e("DistanceMatrix", "Error during request: ${e.message}")
        }

        return result;
    }

    fun updateTripOptionsByDriversAndDistances() {
        Log.d("tripOptions", "Updating trip options started...")

        queryDriversInRealTime(latitude, longitude, 20.0) { drivers ->
            updateJob?.cancel() // cancel any previous update
            updateJob = GlobalScope.launch(Dispatchers.IO) {
                delay(3000) // debounce
                val userLocation = LatLng(latitude, longitude)
                val apiKey = API_KEY;

                val distances = getDriverDistances(userLocation, drivers, apiKey)

                withContext(Dispatchers.Main) {
                    sortTripOptions();
                    viewAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    fun updateTripOptionsByDriversAndDistance() {
        Log.d("tripOptions", "Updating trip options started...");
        queryDrivers(latitude, longitude, 20.0) { drivers: List<Driver> ->

            GlobalScope.launch(Dispatchers.IO) {
                val userLocation = LatLng(latitude, longitude)  // User's location
                //Log.d("Drivers found", "$drivers");
                val apiKey = API_KEY;

                val distances = getDriverDistances(userLocation, drivers, apiKey);
                withContext(Dispatchers.Main) {
                    sortTripOptions();
                    viewAdapter.notifyDataSetChanged();

                    /*
                    // Print the distances
                    distances.forEach {
                        println("Driver ${it.first} is ${it.second} km away.")
                    }*/
                }
            }
        }
    }

    fun queryDrivers(latitude: Double, longitude: Double, radiusKm: Double = 20.0, onDriversFound: (List<Driver>) -> Unit) {
        val database = FirebaseDatabase.getInstance();
        val geoFireRef = database.getReference("drivers-locations");
        val driversRef = database.getReference("drivers");

        val geoFire = GeoFire(geoFireRef);
        val center = GeoLocation(latitude, longitude);
        val geoQuery = geoFire.queryAtLocation(center, radiusKm);
        Log.d("GeoQuery", "geoquery instantiated");

        val foundDrivers = mutableListOf<Driver>()
        val matchingKeys = mutableListOf<String>()
        var completedLookups = 0

        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String, location: GeoLocation) {
                Log.d("GeoQuery", "Key entered: $key")
                matchingKeys.add(key)
            }

            override fun onGeoQueryReady() {
                Log.d("GeoQuery", "GeoQuery ready with ${matchingKeys.size} keys")

                if (matchingKeys.isEmpty()) {
                    onDriversFound(emptyList())
                    return
                }

                for (key in matchingKeys) {
                    driversRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val driver = snapshot.getValue(Driver::class.java)
                            if (driver != null) {
                                Log.d("GeoQuery", "Driver loaded: $driver")
                                foundDrivers.add(driver)
                            } else {
                                Log.w("GeoQuery", "Driver null for key $key")
                            }

                            completedLookups++
                            if (completedLookups == matchingKeys.size) {
                                onDriversFound(foundDrivers)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("GeoQuery", "Driver lookup failed: ${error.message}")
                            completedLookups++
                            if (completedLookups == matchingKeys.size) {
                                onDriversFound(foundDrivers)
                            }
                        }
                    })
                }
            }

            override fun onKeyExited(key: String) {}
            override fun onKeyMoved(key: String, location: GeoLocation) {}
            override fun onGeoQueryError(error: DatabaseError) {
                Log.e("GeoQuery", "Geoquery error: ${error.message}")
            }
        })
    }

    fun queryDriversInRealTime(latitude: Double, longitude: Double, radiusKm: Double = 20.0, onDriversFound: (List<Driver>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val geoFireRef = database.getReference("drivers-locations")
        val driversRef = database.getReference("drivers")

        val geoFire = GeoFire(geoFireRef)
        val center = GeoLocation(latitude, longitude)
        val geoQuery = geoFire.queryAtLocation(center, radiusKm)
        Log.d("GeoQuery", "geoquery instantiated")

        val driverMap = mutableMapOf<String, Driver>()

        fun emitDrivers() {
            onDriversFound(driverMap.values.toList())
        }

        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String, location: GeoLocation) {
                Log.d("GeoQuery", "Driver ENTERED: $key")
                val driverRef = driversRef.child(key)
                driverRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val driver = snapshot.getValue(Driver::class.java)
                        if (driver != null) {
                            driverMap[key] = driver
                            Log.d("GeoQuery", "Updated driver: $driver")
                            emitDrivers()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("GeoQuery", "Error reading driver $key: ${error.message}")
                    }
                })
            }

            override fun onKeyMoved(key: String, location: GeoLocation) {
                Log.d("GeoQuery", "Driver MOVED: $key at $location")
                // GeoQuery does not need to reload driver data here, because `addValueEventListener` above handles real-time updates.
            }

            override fun onKeyExited(key: String) {
                Log.d("GeoQuery", "Driver EXITED: $key")
                driverMap.remove(key)
                emitDrivers()
            }

            override fun onGeoQueryReady() {
                Log.d("GeoQuery", "GeoQuery ready.")
                emitDrivers()
            }

            override fun onGeoQueryError(error: DatabaseError) {
                Log.e("GeoQuery", "GeoQuery error: ${error.message}")
            }
        })
    }

    fun calculateTripCost(distance: Double, fuelEconomyName: String): Double {
        var tripCost = 0.0;

        if (fuelEconomyName == "Toll") {
            tripCost = 1500.0 + (70.0 + 3400.0 + 300.0) * distance;
        }
        else if (fuelEconomyName == "Economy") {
            tripCost = 1500.0 + (93.0 + 2500.0 + 200.0) * distance;
        }
        else if (fuelEconomyName == "Standard") {
            tripCost = 2000.0 + (109.0 + 3900.0 + 400.0) * distance;
        }
        else if (fuelEconomyName == "Premium") {
            tripCost = 4000.0 + (170.0 + 4800.0 + 450.0) * distance;
        }

        return  tripCost;
    }

    fun sortTripOptions() {
        var count = 0;
        var next = 1;
        var option: TripOption = tripOptions[0];

        tripOptions.forEach { tripOption ->
            if (next < tripOptions.size) {
                if (tripOption.driverCount == 0) {
                    option = tripOptions.get(next);
                    tripOptions.set(next, tripOption);
                    tripOptions.set(count, option);
                }
            }
            count++;
            next++;
        }

        selectedTripOption = tripOptions.get(0);
        val button3 = findViewById<Button>(R.id.button3);
        button3.text = "Select ${selectedTripOption.optionName}";
    }

    fun onClickBackArrow(view: View) {
        finish();
    }

    fun onClickDestinationEdit(view: View) {

    }

    fun onClickSelectButton(view: View) {
        val intent = Intent(this, PickupActivity::class.java);
        startActivity(intent);
    }
}
