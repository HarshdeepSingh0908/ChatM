package com.harsh.chatm.Fragments

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.harsh.chatm.R
import com.harsh.chatm.databinding.FragmentSignUpBinding
import java.util.Calendar
import java.util.UUID


class SignUpFragment : Fragment() {
    lateinit var binding: FragmentSignUpBinding
    lateinit var mAuth: FirebaseAuth
    val dbFire = Firebase.firestore
    var uri : Uri? = null
    var imageLocation : String ?= null
    var imagesPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
    }

    var pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){
        binding.ivImage.setImageURI(it)
        uri = it
    }

    var permission = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }else{
        android.Manifest.permission.READ_MEDIA_IMAGES
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagesPermission.launch(permission)
        if (mAuth.currentUser != null) {

        }
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        binding.ivImage.setOnClickListener(){
            pickImage.launch("image/*")


        }
        binding.btnSignUp.setOnClickListener() {
            val email = binding.edtMailSignUp.text.toString()
            val pass = binding.edtPasswordSignUp.text.toString()
            val name = binding.edtNameSignUp.text.toString()
            val phoneNumber = binding.edtPhoneNumber.text.toString()
            if (email.isNullOrEmpty()) {
                binding.edtMailSignUp.setError("Enter Email")
            }else if (name.isNullOrEmpty()){
                binding.edtNameSignUp.setError("Enter Name")
            }
            else if (phoneNumber.isNullOrEmpty()){
                binding.edtPhoneNumber.setError("Enter Phone Number")
            }
            else if (pass.isNullOrEmpty()) {
                binding.edtMailSignUp.setError("Enter Email")
            } else if (pass.length < 8) {
                binding.edtPasswordSignUp.setError("Password Length should be greater or equal to 8")
            }
            else if (imageLocation != null){
                Toast.makeText(requireContext(), "Select Image", Toast.LENGTH_SHORT).show()
            }
            else if (uri != null){

                    ref.putFile(uri!!).addOnSuccessListener {
                        Log.d(
                            "RegisterActivity",
                            "Successfully uploaded ${it.metadata?.path}"
                        )
                        ref.downloadUrl.addOnSuccessListener {
                            Log.d("File Location", "$it")
                            imageLocation = it.toString()
                            signUp(email, pass,name,phoneNumber,imageLocation.toString())

                        }
                    }.addOnFailureListener{
                        Toast.makeText(requireContext(), "Image not uploaded$it", Toast.LENGTH_SHORT).show()
                    }

            }else {
                signUp(email, pass,name,phoneNumber,imageLocation.toString())
            }



        }
        binding.tvLogin.setOnClickListener(){
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

    }

    private fun signUp(email: String, pass: String, name: String, phoneNumber: String, imageURL : String) {
        binding.pbLoading.visibility = View.VISIBLE


        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener {

                Toast.makeText(requireContext(), "User Created Succesfully", Toast.LENGTH_SHORT)
                    .show()
                val userMap = hashMapOf(
                    "name" to name,
                    "phone" to phoneNumber,
                    "email" to email,
                    "image" to imageURL,
                    "uid" to it.user?.uid
                )
                val uid = it.user!!.uid
                dbFire.collection("user").document(uid).set(userMap)
                    .addOnSuccessListener {
                        binding.pbLoading.visibility = View.GONE
                        Toast.makeText(requireContext(), "Data added to firestore", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                    }


            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error:${it.message}", Toast.LENGTH_SHORT).show()
                binding.pbLoading.visibility = View.GONE
            }
    }


}