package app.toll.toll

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import app.toll.toll.databinding.ActivityStopsBinding
import kotlin.math.cos

class StopsActivity : AppCompatActivity() {
    lateinit var stopsBinding: ActivityStopsBinding;

    companion object trip {
        var originPlaces: MutableList<Place> = mutableListOf<Place>();
        var destinationPlaces: MutableList<Place> = mutableListOf<Place>();
        var origin: Place? = null;
        var destination: Place? = null;
    }

    private lateinit var placesClient: PlacesClient
    private  lateinit var originInput: AutoCompleteTextView;
    private lateinit var destinationInput: AutoCompleteTextView
    private lateinit var arrayAdapter: ArrayAdapter<String>

    private lateinit var recyclerView: RecyclerView;
    private lateinit var viewAdapter: RecyclerView.Adapter<*>;
    private lateinit var viewManager: RecyclerView.LayoutManager;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        stopsBinding = ActivityStopsBinding.inflate(layoutInflater);
        setContentView(stopsBinding.root);

        val editText = stopsBinding.destinationEditText;
        editText.requestFocus();

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        @Suppress("DEPRECATION")
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        var origin: String? = intent.getStringExtra("origin");
        if (origin != null) {
            stopsBinding.originEditText.setText(origin);
        }

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "GOOGLE_MAPS_API_KEY");
        }
        placesClient = Places.createClient(this)

        originInput = stopsBinding.originEditText;
        destinationInput = stopsBinding.destinationEditText;
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line)
        destinationInput.setAdapter(arrayAdapter)

        destinationInput.threshold = 2 // Start searching after 1 character

        destinationInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    fetchPredictions(it.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        });



        viewManager = LinearLayoutManager(this);
        viewAdapter = RecyclerAdapter(destinationPlaces, this, placesClient);
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true);
            layoutManager = viewManager;
            adapter = viewAdapter;
        }


        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                // Makes the status bar completely transparent
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                statusBarColor = Color.TRANSPARENT
            }
        }
    }

    fun onClickClearAllButton(view: View) {
        destinationPlaces.clear();
        recyclerView.adapter?.notifyItemRangeChanged(0,0);
        recyclerView.adapter?.notifyDataSetChanged();
    }

    private fun fetchPredictions(query: String) {
        val token = AutocompleteSessionToken.newInstance();
        trip.destinationPlaces.clear();
        recyclerView.adapter?.notifyItemRangeChanged(0,0);
        recyclerView.adapter?.notifyDataSetChanged();

        var center = LatLng(42.9849, -81.2453) // London, ON

        val originLatitude = origin?.latitude;
        val originLongitude = origin?.longitude;
        if (originLatitude != null && originLongitude != null) {
            center = LatLng(originLatitude, originLongitude);
        }
        val bounds = getBounds(center, 100.0);

        @Suppress("DEPRECATION")
        val request = FindAutocompletePredictionsRequest.builder()
            .setLocationBias(RectangularBounds.newInstance(bounds))
            .setSessionToken(token)
            .setQuery(query)
            .setTypeFilter(TypeFilter.ADDRESS) // Only addresses
            .build();

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions;
                val addresses = predictions.map { it.getFullText(null).toString() }
                //arrayAdapter.clear();
                //arrayAdapter.addAll(addresses);
                //arrayAdapter.notifyDataSetChanged()
                val plcs = mutableListOf<Place>();
                var count = 0;
                addresses.forEach { i ->
                    val prediction = predictions[count];
                    if (count < 7) {
                        val arr = i.split(",");
                        var cityAddress = "";
                        for (j in 1..< arr.size) {
                            cityAddress += (arr[j]).trim();
                            if (j < arr.size - 1) {
                                cityAddress += ", ";
                            }
                        }
                        plcs.add(Place(arr[0], cityAddress ?: "London, ON, Canada", 0.0, 0.0, placeId = prediction.placeId));
                    }
                    count++;
                }
                trip.destinationPlaces.addAll(plcs);
                recyclerView.adapter?.notifyItemRangeChanged(0,0);
                recyclerView.adapter?.notifyDataSetChanged();
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace();
            }
    }

    fun getBounds(center: LatLng, radiusKm: Double): LatLngBounds {
        val latOffset = radiusKm / 111.0 // 1 degree latitude ≈ 111 km
        val lngOffset = radiusKm / (111.0 * cos(Math.toRadians(center.latitude)))

        val southwest = LatLng(center.latitude - latOffset, center.longitude - lngOffset)
        val northeast = LatLng(center.latitude + latOffset, center.longitude + lngOffset)
        return LatLngBounds(southwest, northeast)
    }

    fun onClickCancel(view: View) {
        //val intent = Intent(this, MapsActivity::class.java);
        //startActivity(intent);
        finish();
    }
}