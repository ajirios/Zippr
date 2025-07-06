package com.zippr.zippr

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.zippr.zippr.R

class CountryRecyclerAdapter(private var dataSet: List<Country>, var activity: AppCompatActivity): RecyclerView.Adapter<CountryRecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val countryNameTextView: TextView;
        val phoneCodeTextView: TextView;
        val flagImageView: ImageView;

        init {
            countryNameTextView = view.findViewById(R.id.countryNameTextView);
            phoneCodeTextView = view.findViewById(R.id.phoneCodeTextView);
            flagImageView = view.findViewById(R.id.flagImageView);
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.country_item, viewGroup, false);

        val lp = view.getLayoutParams();
        lp.height = viewGroup.measuredHeight/8;
        view.setLayoutParams(lp);

        return ViewHolder(view);
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val country = dataSet[position]
        viewHolder.countryNameTextView.text = country.countryName
        viewHolder.phoneCodeTextView.text = country.phoneCode

        // Dynamically get the drawable resource ID
        val context = viewHolder.flagImageView.context
        val resId = context.resources.getIdentifier(country.flag, "drawable", context.packageName)

        if (resId != 0) {
            viewHolder.flagImageView.setImageResource(resId);
        } else {
            // Optional: set a placeholder if image not found
            viewHolder.flagImageView.setImageResource(R.drawable.baseline_map_24);
        }

        viewHolder.itemView.setOnClickListener {
            val intent = Intent(activity, LocationActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount() : Int {
        return dataSet.size
    }
}