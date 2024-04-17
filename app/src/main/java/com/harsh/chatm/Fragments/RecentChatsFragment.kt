package com.harsh.chatm.Fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.harsh.chatm.Adapter.UserAdapter
import com.harsh.chatm.DataClasses.User
import com.harsh.chatm.Interfaces.OnUserClick
import com.harsh.chatm.R
import com.harsh.chatm.databinding.FragmentRecentChatsBinding

class RecentChatsFragment : Fragment(), OnUserClick {
    private lateinit var binding: FragmentRecentChatsBinding
    var dbFire = Firebase.firestore
    var mAuth = FirebaseAuth.getInstance()
    var db = Firebase.database
    var senderRoom: String? = null
    private val TAG = "RecentChatsFragment"
    lateinit var userList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecentChatsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setHomeButtonEnabled(false)
            setDisplayHomeAsUpEnabled(false)
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#2294C7")))
            show()
            title = "Chats"
        }
        binding.fabAddChat.setOnClickListener() {
            findNavController().navigate(R.id.action_recentChatsFragment_to_usersFragment)
        }

        binding.rvRecentChats.layoutManager = LinearLayoutManager(requireContext())
        userList = arrayListOf()
        dbFire = FirebaseFirestore.getInstance()
        db.getReference("chats").get()
        dbFire.collection("user").get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val user: User? = data.toObject(User::class.java)
                        senderRoom = user?.uid + mAuth.currentUser?.uid
                        db.getReference("chats").child(senderRoom.toString())
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        if (user != null)
                                            userList.add(user)
                                        Log.e(
                                            TAG,
                                            "${user?.uid.toString()},${user?.name.toString()}"
                                        )
                                        binding.rvRecentChats.adapter?.notifyDataSetChanged()
                                        binding.pbLoading.visibility = View.GONE
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            })

                    }
                    binding.rvRecentChats.adapter = UserAdapter(userList, this, requireContext())
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "${it.toString()}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onClick(user: User) {
        val bundle = Bundle().apply {
            putString("username", user.name)
            putString("uid", user.uid)
        }
        findNavController().navigate(R.id.action_recentChatsFragment_to_messageFragment, bundle)
    }


}