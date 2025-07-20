package app.toll.toll

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import app.toll.toll.model.Favourites
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FavouritesRecyclerAdapter(private var dataSet: List<app.toll.toll.Place>): RecyclerView.Adapter<FavouritesRecyclerAdapter.ViewHolder>() {

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
            .inflate(R.layout.favourite_item, viewGroup, false);

        // add the 3 lines of code below to show 5 recycler items in the activity at a time
        val lp = view.getLayoutParams();
        lp.height = viewGroup.measuredHeight/9;
        view.setLayoutParams(lp);

        return ViewHolder(view);
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val clickedPlace = dataSet[position];
        viewHolder.streetAddressTextView.text = dataSet[position].streetAddress;
        viewHolder.cityAddressTextView.text = dataSet[position].cityAddress;
        viewHolder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount() : Int {
        return dataSet.size
    }
}