package fgori.ft_hanguots

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter (private val contactList: List<Contact>, private val onItemClick :(Contact) -> Unit) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>()
{
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val contactImage: ImageView = itemView.findViewById(R.id.contact_image)
        val contactName: TextView = itemView.findViewById(R.id.contact_name)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder
    {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentContact = contactList[position]

        holder.contactName.text = currentContact.getValue("name")
        val imageUri = currentContact.getValue("img")
        if (!imageUri.isNullOrEmpty()) {
            val uri = Uri.parse(imageUri)
            holder.contactImage.setImageURI(uri)
        }else{
            holder.contactImage.setImageResource(R.drawable.ic_launcher_foreground)
        }
        holder.itemView.setOnClickListener {
            onItemClick(currentContact)
        }
    }
    override fun getItemCount(): Int {
        return contactList.size
    }
}

