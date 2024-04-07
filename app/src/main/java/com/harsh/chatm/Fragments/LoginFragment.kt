package com.harsh.chatm.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.harsh.chatm.MainActivity
import com.harsh.chatm.R
import com.harsh.chatm.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    lateinit var mAuth: FirebaseAuth
lateinit var binding : FragmentLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser != null){
            findNavController().navigate(R.id.action_loginFragment_to_chatFragment)
        }
        binding.btnLogin.setOnClickListener(){
            val email = binding.edtMailLogin.text.toString()
            val pass = binding.edtPasswordLogin.text.toString()


            if (pass.isNullOrEmpty()) {
                binding.edtPasswordLogin.setError("Enter Email")
            } else if (email.isNullOrEmpty()){
                binding.edtMailLogin.setError("Enter Password")
            }
                else {
                logIn(email,pass)
            }
        }
        binding.tvSignUp.setOnClickListener(){
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }
    private fun logIn(email: String, pass: String) {
        mAuth.signInWithEmailAndPassword(email,pass)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Signed in Successul", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_chatFragment)
            }.addOnFailureListener{
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


}