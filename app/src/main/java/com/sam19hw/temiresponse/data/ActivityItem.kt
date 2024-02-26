package com.sam19hw.temiresponse.data

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sam19hw.temiresponse.R


class ActivityItem(val name: String, val path: String, val isOnline: Boolean) {

    companion object {
        private var lastContactId = 0
        fun createActivitiesList(numContacts: Int): ArrayList<ActivityItem> {
            val activities = ArrayList<ActivityItem>()
            for (i in 1..numContacts) {
                activities.add(ActivityItem("Person " + ++lastContactId, "path", i <= numContacts / 2))
            }
            return activities
        }
        fun createActivity(name: String, path: String, isOnline: Boolean): ActivityItem {
            return ActivityItem(name, path, isOnline)
        }
    }
}

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
class ActivityAdapter (private val mActivities: List<ActivityItem>, val context: Context) : RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val nameTextView = itemView.findViewById<TextView>(R.id.activity_name)
        val messageButton = itemView.findViewById<Button>(R.id.message_button)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.activity_item, parent, false)

        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ActivityAdapter.ViewHolder, position: Int) {
        // Get the data model based on position
        val contact: ActivityItem = mActivities.get(position)
        // Set item views based on your views and data model
        val textView = viewHolder.nameTextView
        textView.setText(contact.name)
        val button = viewHolder.messageButton
        button.text = if (contact.isOnline) "Start" else "Unavailable"
        button.isEnabled = contact.isOnline

        // Sets the button to start the activity located in the path from the given activity context
        button.setOnClickListener {
            //val intent = Intent(context, NavTestActivity::class.java)
            val intent = Intent(context, Class.forName(contact.path))
            context.startActivity(intent)
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mActivities.size
    }


}
