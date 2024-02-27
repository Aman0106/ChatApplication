package com.example.chatapplictionlikewhastapp.featureHome.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.chatapplictionlikewhastapp.databinding.ContactCardLayoutBinding
import com.example.chatapplictionlikewhastapp.featureHome.pojo.ContactsUserinfo

class ContactsListAdapter: RecyclerView.Adapter<ContactsListAdapter.ContactsListViewHolder>() {

    private var contactsList = ArrayList<ContactsUserinfo>()

    lateinit var onItemClicked: ((ContactsUserinfo) -> Unit)

    fun setContactsList(chatList: ArrayList<ContactsUserinfo>) {
        contactsList = chatList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsListViewHolder {
        return ContactsListViewHolder(ContactCardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ContactsListViewHolder, position: Int) {
        val currentContact = contactsList[position]
        if (currentContact.profileImage != null)
            holder.binding.imgProfile.load(currentContact.profileImage)
        holder.binding.tvContactName.text = currentContact.name
        holder.binding.tvContactPhoneNumber.text = currentContact.phoneNumber

        holder.binding.contactCard.setOnClickListener {
            onItemClicked.invoke(contactsList[position])
        }
    }

    override fun getItemCount() = contactsList.size

    inner class ContactsListViewHolder(val binding:ContactCardLayoutBinding): RecyclerView.ViewHolder(binding.root)
}