package app.toll.toll

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.model.Place as GPlace
import kotlinx.coroutines.delay

class RecyclerAdapter(private var dataSet: MutableList<Place>, var activity: AppCompatActivity, private val placesClient: PlacesClient
): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val streetAddressTextView: TextView
        val cityAddressTextView: TextView

        init {
            // Define click listener for the ViewHolder's View.
            streetAddressTextView = view.findViewById(R.id.streetAddressTextView);
            cityAddressTextView = view.findViewById(R.id.cityAddressTextView);
        }
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item, viewGroup, false);

        // add the 3 lines of code below to show 5 recycler items in the activity at a time
        val lp = view.getLayoutParams();
        lp.height = viewGroup.measuredHeight/7;
        view.setLayoutParams(lp);

        return ViewHolder(view);
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val clickedPlace = dataSet[position];
        viewHolder.streetAddressTextView.text = dataSet[position].streetAddress;
        viewHolder.cityAddressTextView.text = dataSet[position].cityAddress;
        viewHolder.itemView.setOnClickListener {

            activity.lifecycleScope.launch {
                val destinationEditText = activity.findViewById<EditText>(R.id.destinationEditText);
                destinationEditText.setText(dataSet[position].streetAddress + ", " + dataSet[position].cityAddress);
                delay(1000);

                // Fetch full place details from Google Places API
                val placeFields = listOf(GPlace.Field.LAT_LNG)
                val request = FetchPlaceRequest.builder(clickedPlace.placeId, placeFields).build()

                placesClient.fetchPlace(request).addOnSuccessListener { response ->
                    val latLng = response.place.latLng
                    if (latLng != null) {
                        // Update the Place object if needed
                        clickedPlace.latitude = latLng.latitude;
                        clickedPlace.longitude = latLng.longitude;

                        StopsActivity.trip.destination = clickedPlace;

                        activity.lifecycleScope.launch {
                            val intent = Intent(activity, MapsActivity::class.java);
                            intent.putExtra("destination", clickedPlace.streetAddress + ", " + clickedPlace.cityAddress);
                            intent.putExtra("latitude", latLng.latitude);
                            intent.putExtra("longitude", latLng.longitude);
                            activity.startActivity(intent);
                        }
                    }
                }.addOnFailureListener {
                    it.printStackTrace()
                    //Toast.makeText(activity, "Failed to get place details", Toast.LENGTH_SHORT).show();
                }
            }
            val imm = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
            imm.hideSoftInputFromWindow(it.windowToken, 0);
        }
    }

    override fun getItemCount() : Int {
        return dataSet.size
    }

}