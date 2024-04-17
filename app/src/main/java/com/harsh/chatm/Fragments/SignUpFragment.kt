package com.harsh.chatm.Fragments

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.harsh.chatm.R
import com.harsh.chatm.databinding.FragmentSignUpBinding
import java.util.UUID


class SignUpFragment : Fragment() {
    private val binding by lazy { FragmentSignUpBinding.inflate(layoutInflater) }
    lateinit var mAuth: FirebaseAuth
    val dbFire = Firebase.firestore
    var uri: Uri? = null
    var imageLocation: String? = null
    var langCode: String? = null
    var isImageUploadedtoFirebase = false
    var token : String ?= null
    val filename = UUID.randomUUID().toString()
    val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
    var imagesPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
    }

    var pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        binding.ivImage.setImageURI(it)
        if (it != null){
            uri = it
            binding.pbProgressBar.visibility = View.VISIBLE
            ref.putFile(uri!!).addOnSuccessListener {
                Log.d(
                    "RegisterActivity",
                    "Successfully uploaded ${it.metadata?.path}"
                )
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("File Location", "$it")
                    imageLocation = it.toString()
                    isImageUploadedtoFirebase = true
                    binding.pbProgressBar.visibility = View.GONE

                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Image not uploaded$it", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    var permission = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    } else {
        android.Manifest.permission.READ_MEDIA_IMAGES
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            hide()
        }
        mAuth = FirebaseAuth.getInstance()
        val languageNames = resources.getStringArray(R.array.language_names)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languageNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagesPermission.launch(permission)
        binding.ivImage.setOnClickListener() {
            pickImage.launch("image/*")
        }
        binding.languageSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedLanguageName = parent.getItemAtPosition(position) as String

                    // Retrieve corresponding language code from arrays.xml
                    val languageCodes = resources.getStringArray(R.array.language_codes)
                    val selectedLanguageCode = languageCodes[position]
                    langCode = selectedLanguageCode
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Another interface callback
                }
            }
        binding.btnSignUp.setOnClickListener() {
            val email = binding.edtMailSignUp.text.toString()
            val pass = binding.edtPasswordSignUp.text.toString()
            val name = binding.edtNameSignUp.text.toString()
            val phoneNumber = binding.edtPhoneNumber.text.toString()

            if (email.isNullOrEmpty()) {
                binding.edtMailSignUp.setError("Enter Email")
            } else if (name.isNullOrEmpty()) {
                binding.edtNameSignUp.setError("Enter Name")
            } else if (phoneNumber.isNullOrEmpty()) {
                binding.edtPhoneNumber.setError("Enter Phone Number")
            } else if (pass.isNullOrEmpty()) {
                binding.edtMailSignUp.setError("Enter Email")
            } else if (pass.length < 8) {
                binding.edtPasswordSignUp.setError("Password Length should be greater or equal to 8")
           }
//            else if (uri != null) {
//                binding.pbProgressBar.visibility = View.VISIBLE
//                ref.putFile(uri!!).addOnSuccessListener {
//                    Log.d(
//                        "RegisterActivity",
//                        "Successfully uploaded ${it.metadata?.path}"
//                    )
//                    ref.downloadUrl.addOnSuccessListener {
//                        Log.d("File Location", "$it")
//                        imageLocation = it.toString()
//                        isImageUploadedtoFirebase = true
//                        binding.pbProgressBar.visibility = View.GONE
//                        // signUp(email, pass,name,phoneNumber,imageLocation.toString(),)
//
//                    }
//                }.addOnFailureListener {
//                    Toast.makeText(requireContext(), "Image not uploaded$it", Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//            }
            else if (!isImageUploadedtoFirebase){
                Toast.makeText(requireContext(), "Image is not uploaded", Toast.LENGTH_SHORT).show()
            }
            else{
                signUp(
                    email,
                    pass,
                    name,
                    phoneNumber,
                    imageLocation.toString(),
                    langCode.toString(),
                    token.toString()
                )
            }


        }
        binding.tvLogin.setOnClickListener() {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

    }



    private fun signUp(
        email: String,
        pass: String,
        name: String,
        phoneNumber: String,
        imageURL: String,
        language: String,
        fbToken : String
    ) {
        binding.pbProgressBar.visibility = View.VISIBLE


        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                    token = it
                    Log.e("Token",token.toString())
                }.addOnFailureListener{
                    Toast.makeText(requireContext(), "Token is not Found", Toast.LENGTH_SHORT).show()
                }

                Toast.makeText(requireContext(), "User Created Succesfully", Toast.LENGTH_SHORT)
                    .show()
                val userMap = hashMapOf(
                    "name" to name,
                    "phone" to phoneNumber,
                    "email" to email,
                    "image" to imageURL,
                    "uid" to it.user?.uid,
                    "language" to language,
                    "token" to fbToken
                )
                val uid = it.user!!.uid
                dbFire.collection("user").document(uid).set(userMap)
                    .addOnSuccessListener {
                        binding.pbProgressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Data added to firestore",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                    }


            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error:${it.message}", Toast.LENGTH_SHORT).show()
                binding.pbProgressBar.visibility = View.GONE
            }
    }


}