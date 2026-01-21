package fgori.ft_hanguots

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class ContactDisplayAdapter (private val contactList: MutableList<Contact>, private val onItemClick :(Contact) -> Unit) : RecyclerView.Adapter<ContactDisplayAdapter.ContactDisplayViewHolder>() {

    class ContactDisplayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactImage: ImageView = itemView.findViewById(R.id.contact_image)
        val contactName: TextView = itemView.findViewById(R.id.ContName)
        val contactNumber: TextView = itemView.findViewById(R.id.ContNumber)
        val contactMail: TextView = itemView.findViewById(R.id.ContMail)
        val contactAddress: TextView = itemView.findViewById(R.id.ContAddress)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactDisplayViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item_view, parent, false)
        return ContactDisplayViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactDisplayViewHolder, position: Int) {
        val currentContact = contactList[position]

        holder.contactName.text =
            currentContact.getValue("name") + " " + currentContact.getValue("surname")
        val imageUri = currentContact.getValue("img")
        if (!imageUri.isNullOrEmpty()) {
            val uri = Uri.parse(imageUri)
            holder.contactImage.setImageURI(uri)
        } else {
            holder.contactImage.setImageResource(R.drawable.ic_launcher_foreground)
        }
        addText(currentContact.getValue("phone"), holder.contactNumber)
        addText(currentContact.getValue("email"), holder.contactMail)
        addText(currentContact.getValue("address"), holder.contactAddress)

        holder.itemView.setOnClickListener {
            onItemClick(currentContact)
        }
        val sharedPreference =
            holder.itemView.context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val colorString = sharedPreference.getString("USER_COLOR", "#2196F3")
        val background = holder.itemView.background.mutate()
        if (background is GradientDrawable) {
            try {
                val color = Color.parseColor(colorString)
                background.colors = intArrayOf(color, Color.TRANSPARENT)
            } catch (e: IllegalArgumentException) {
                val color = Color.parseColor("#2196F3")
                background.colors = intArrayOf(color, Color.TRANSPARENT)

            }
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    fun updateData(filteredList: MutableList<Contact>)
    {
        contactList.clear()
        contactList.addAll(filteredList)
        notifyDataSetChanged()
    }
    fun addText(text: String, holderView : TextView)
    {
        if(text.isEmpty()) {
            holderView.visibility = View.GONE
        }
        else
        {
            holderView.text = text
        }
    }


}