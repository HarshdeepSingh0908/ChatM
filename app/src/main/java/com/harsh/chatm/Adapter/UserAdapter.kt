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
import com.harsh.chatm.R

class UserAdapter(val userList: ArrayList<User>, var onUserClick: OnUserClick,var context: Context) : RecyclerView.Adapter<UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
       // holder.bind(user)
        holder.userName.text = user.name
        holder.userPhoneNumber.text = user.phone
        Glide.with(context)
            .load(if (userList[position].image != null){userList[position].image}
            else {
                R.drawable.ic_launcher_background
            }
            )
            .override(140, 140)
            .into(holder.image);
        holder.itemView.setOnClickListener(){
            onUserClick.onClick(user)
        }

    }



}

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var userName = itemView.findViewById<TextView>(R.id.tvName)
    var userPhoneNumber = itemView.findViewById<TextView>(R.id.tvPhoneNumber)
    var image = itemView.findViewById<ImageView>(R.id.userImg)


    }
