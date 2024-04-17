package com.harsh.chatm.Fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.harsh.chatm.Adapter.UserAdapter
import com.harsh.chatm.DataClasses.User
import com.harsh.chatm.Interfaces.OnUserClick
import com.harsh.chatm.MainActivity
import com.harsh.chatm.R

import com.harsh.chatm.databinding.FragmentUsersBinding

class UsersFragment : Fragment(),OnUserClick{
    lateinit var binding : FragmentUsersBinding
    var dbFire = Firebase.firestore
    var mAuth = FirebaseAuth.getInstance()
    lateinit var  userList : ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.llMain.visibility = View.GONE
        binding.pbProgressBar.visibility = View.VISIBLE

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
           // setHomeButtonEnabled(false)
          //  setDisplayHomeAsUpEnabled(false)
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#2294C7")))
            show()
            title = "Start New Chat"
        }

        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        userList = arrayListOf()
        dbFire = FirebaseFirestore.getInstance()
       /* dbFire.collection("user").addSnapshotListener { value, error ->
            if(error != null){
                //toast
                return@addSnapshotListener
            }

            for(values in value!!.documentChanges){
                var model = values.document.toObject()
                when(values.type){
                    DocumentChange.Type.ADDED->{}
                    DocumentChange.Type.MODIFIED->{}
                    DocumentChange.Type.REMOVED->{}
                }
            }
        }*/


        dbFire.collection("user").get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    for (data in it.documents){
                        val user : User? = data.toObject(User::class.java)
                        if (user != null  && mAuth.currentUser?.uid != user.uid){
                            userList.add(user)
                        }
                    }

                    binding.rvUsers.adapter = UserAdapter(userList,this,requireContext())
                    binding.llMain.visibility = View.VISIBLE
                    binding.pbProgressBar.visibility = View.GONE

                }
            }.addOnFailureListener{
                Toast.makeText(requireContext(), "${it.toString()}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onClick(user: User) {
        val bundle = Bundle().apply{
            putString("username",user.name)
            putString("uid",user.uid)

        }

       findNavController().navigate(R.id.action_usersFragment_to_messageFragment,bundle)
    }





}