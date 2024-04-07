package com.harsh.chatm.Fragments

import android.os.Binder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.harsh.chatm.R
import com.harsh.chatm.databinding.FragmentChatBinding


class ChatFragment : Fragment() {
    lateinit var binding : FragmentChatBinding
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            hide()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(layoutInflater)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvChat.setOnClickListener(){
            mAuth.signOut()
         //   Log.e("Sign Ou")
            findNavController().navigate(R.id.action_chatFragment_to_loginFragment)
        }
        binding.fabAddUser.setOnClickListener(){
            findNavController().navigate(R.id.action_chatFragment_to_usersFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            show()
        }
    }


}