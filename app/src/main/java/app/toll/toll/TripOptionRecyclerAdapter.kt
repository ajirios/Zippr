package app.toll.toll

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class TripOptionRecyclerAdapter(private var dataSet: MutableList<TripOption>, var activity: AppCompatActivity): RecyclerView.Adapter<TripOptionRecyclerAdapter.ViewHolder>() {
    private var selectedPosition = 0;

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val optionNameTextView: TextView;
        val waitTimeTextView: TextView;
        val driverCountTextView: TextView;
        val tripFareTextView: TextView;

        init {
            // Define click listener for the ViewHolder's View.
            optionNameTextView = view.findViewById(R.id.tripOptionTextView);
            waitTimeTextView = view.findViewById(R.id.waitTimeTextView);
            driverCountTextView = view.findViewById(R.id.driverCountTextView);
            tripFareTextView = view.findViewById(R.id.fareTextView);
        }
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.class_item, viewGroup, false);

        // add the 3 lines of code below to show 5 recycler items in the activity at a time
        val lp = view.getLayoutParams();
        lp.height = viewGroup.measuredHeight/3;
        view.setLayoutParams(lp);

        return ViewHolder(view);
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.optionNameTextView.text = dataSet[position].optionName;
        viewHolder.waitTimeTextView.text = "${dataSet[position].waitTime.toString()} min";
        viewHolder.driverCountTextView.text = dataSet[position].driverCount.toString();
        viewHolder.tripFareTextView.text = "NGN${dataSet[position].tripFare}";


        viewHolder.itemView.isSelected = selectedPosition == position;


        if (dataSet[position].waitTime != 900) {
            val background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE;
                viewHolder.optionNameTextView.setTextColor(Color.BLACK);
                viewHolder.tripFareTextView.setTextColor(Color.BLACK);
                viewHolder.waitTimeTextView.setTextColor(Color.BLACK);
                cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, Resources.getSystem().displayMetrics);
                setColor(Color.TRANSPARENT);
                if (selectedPosition == position) {
                    setStroke(3, Color.parseColor("#009900"))
                }
                else {
                    setStroke(0, Color.TRANSPARENT);
                }
            }

            viewHolder.itemView.background = background;

            viewHolder.itemView.setOnClickListener {
                if (dataSet[position].waitTime != 900) {
                    val previousPosition = selectedPosition;
                    selectedPosition = position;
                    MapsActivity.selectedTripOption = dataSet[position];
                    notifyItemChanged(previousPosition);
                    notifyItemChanged(selectedPosition);
                    val button3 = activity.findViewById<Button>(R.id.button3);
                    button3.text = "Select ${dataSet[position].optionName}";
                }
            }
        }
        else {
            viewHolder.waitTimeTextView.text = "0 min";
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE;
                viewHolder.optionNameTextView.setTextColor(Color.GRAY);
                viewHolder.tripFareTextView.setTextColor(Color.GRAY);
                viewHolder.waitTimeTextView.setTextColor(Color.GRAY);
                cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, Resources.getSystem().displayMetrics);
                setColor(Color.TRANSPARENT);
                if (selectedPosition == position) {
                    setStroke(3, Color.parseColor("#009900"))
                }
                else {
                    setStroke(0, Color.TRANSPARENT);
                }
            }
            viewHolder.itemView.background = bg;
        }
    }

    override fun getItemCount() : Int {
        return dataSet.size
    }

    public fun updateList(newItems: MutableList<TripOption>) {
        dataSet.clear();
        dataSet.addAll(newItems);
        notifyDataSetChanged(); // Or use DiffUtil for better performance
    }
}