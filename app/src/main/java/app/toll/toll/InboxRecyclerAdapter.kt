package app.toll.toll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.toll.toll.model.Contact
import com.bumptech.glide.Glide

class InboxRecyclerAdapter(private var dataSet: List<Contact>) :
    RecyclerView.Adapter<InboxRecyclerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImageView: ImageView = view.findViewById(R.id.profileImageView)
        val recipientNameTextView: TextView = view.findViewById(R.id.recipientNameTextView)
        val lastMessageTextView: TextView = view.findViewById(R.id.lastMessageTextView)
        val lastMessageTimeTextView: TextView = view.findViewById(R.id.lastMessageTimeTextView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.messenger_item, viewGroup, false)

        // Optional: Adjust height for uniform list
        val lp = view.layoutParams
        lp.height = viewGroup.measuredHeight / 8 // Adjust as needed
        view.layoutParams = lp

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val contact = dataSet[position];
        viewHolder.recipientNameTextView.text = contact.contactName ?: "Unknown Contact";
        viewHolder.lastMessageTextView.text = contact.lastMessage ?: "";
        viewHolder.lastMessageTimeTextView.text = contact.lastContactedAt ?: "";

        // Load profile picture (Glide handles placeholder)
        Glide.with(viewHolder.itemView.context)
            .load(contact.profilePictureUrl)
            .placeholder(R.drawable.baseline_person_2_24)
            .circleCrop()
            .into(viewHolder.profileImageView)

        // Item click listener
        viewHolder.itemView.setOnClickListener {
            // TODO: Open chat screen with this contact
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}