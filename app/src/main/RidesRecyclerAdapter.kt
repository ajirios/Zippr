package app.toll.toll


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RidesRecyclerAdapter(private var dataSet: List<Order>) :
    RecyclerView.Adapter<RidesRecyclerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val streetAddressTextView: TextView = view.findViewById(R.id.tripStreetAddressTextView)
        val cityAddressTextView: TextView = view.findViewById(R.id.tripCityAddressTextView)
        val dateTimeTextView: TextView = view.findViewById(R.id.tripDateTimeTextView)
        val fareTextView: TextView = view.findViewById(R.id.tripFareTextView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.trip_item, viewGroup, false)

        // Adjust height to show 5 items at a time (like your original adapter)
        val lp = view.layoutParams
        lp.height = viewGroup.measuredHeight / 4
        view.layoutParams = lp

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val order = dataSet[position]

        // Set Street Address (dropoff location)
        viewHolder.streetAddressTextView.text = order.dropoffLocation?.address ?: "Unknown Address"

        // Set City Address (extract from dropoff location address if possible)
        val address = order.dropoffLocation?.address ?: ""
        val city = extractCityFromAddress(address)
        viewHolder.cityAddressTextView.text = city

        // Set Date/Time Placed
        viewHolder.dateTimeTextView.text = order.placedAt ?: "Unknown Date"

        // Set Fare Paid (format as currency)
        val fare = order.fareEstimate ?: 0.0
        val currency = order.currency ?: "$"
        viewHolder.fareTextView.text = "$currency${String.format("%.2f", fare)}"

        // Item Click Listener
        viewHolder.itemView.setOnClickListener {
            // TODO: Handle item click, e.g., navigate to trip details
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    // Helper to extract city from address string
    private fun extractCityFromAddress(address: String): String {
        // Split the address by commas and take second segment as city (if available)
        val parts = address.split(",")
        return if (parts.size >= 2) parts[1].trim() else "Unknown City"
    }
}