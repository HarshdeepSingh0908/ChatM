package com.harsh.chatm.Adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harsh.chatm.DataClasses.User
import com.harsh.chatm.Interfaces.OnUserClick
import com.harsh.chatm.Interfaces.OnMenuDotClick
import com.harsh.chatm.R

class AddNewUserAdapter(val userList: ArrayList<User>, var onUserClick: OnUserClick,var context: Context,var onMenuDotClick: OnMenuDotClick) : RecyclerView.Adapter<AddNewUserAdapterViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddNewUserAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item2, parent, false)
        return AddNewUserAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddNewUserAdapterViewHolder, position: Int) {
        val user = userList[position]
        // holder.bind(user)
        holder.userName.text = user.name
        holder.userEmail.text = user.email
        Glide.with(context)
            .load(if (userList[position].image != null){userList[position].image}
            else {
                R.drawable.signup_image
            }
            )
            .override(140, 140)
            .into(holder.image)
        holder.itemView.setOnClickListener(){
            onUserClick.onClick(user)
        }
        holder.itemView.setOnLongClickListener(){
            onMenuDotClick.onMenuDotClick(user,it)
            return@setOnLongClickListener true
        }

    }



    override fun getItemCount(): Int {
        return userList.size
    }




}

class AddNewUserAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var userName = itemView.findViewById<TextView>(R.id.tvName)
    var userEmail = itemView.findViewById<TextView>(R.id.tvPhoneNumber)
    var image = itemView.findViewById<ImageView>(R.id.userImg)




}

