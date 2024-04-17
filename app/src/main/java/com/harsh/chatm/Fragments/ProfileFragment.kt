package com.harsh.chatm.Fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.harsh.chatm.DataClasses.User
import com.harsh.chatm.R
import com.harsh.chatm.databinding.FragmentProfileBinding
import java.util.UUID

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    var langCode: String? = null
    val dbFire = Firebase.firestore
    var mAuth = FirebaseAuth.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    private val TAG = "ProfileFragment"
    var image: String? = null
    var uri: Uri? = null
    var nameOfUser: String? = null
    var languageCode: String? = null
    var languageName: String? = null
    val filename = UUID.randomUUID().toString()
    var ref = FirebaseStorage.getInstance().getReference("/images/$filename")

    @SuppressLint("SuspiciousIndentation")
    var pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            binding.imgProfile.setImageURI(it)
            if (it != null) {
                uri = it
                binding.pbProgressBar.visibility = View.VISIBLE
                ref.putFile(uri!!).addOnSuccessListener {

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("File Location", "$it")
                        dbFire.collection("user").document(uid ?: "").update("image", it)
                        binding.pbProgressBar.visibility = View.GONE

                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Image not uploaded$it", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        fetchUserData()
        binding.tvCurrentLanguage.setOnClickListener() {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Warning")
            builder.setCancelable(false)
            builder.setMessage("Previously sent messages will not be effected, Changes will be applied on new Messages  Are you sure you want to change Language?")
            builder.setPositiveButton("Yes") { _, _ ->
                showLanguageDialog()
            }
            builder.setNegativeButton("No") { _, _ ->
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()

        }
        binding.imgProfile.setOnClickListener() {
            getImageAndUpload()
        }
        binding.tvNameProfile.setOnClickListener() {
            val name = binding.tvNameProfile.text.toString()
            showChangeNameDialog(name)
        }
        binding.tvPhoneProfile.setOnClickListener() {
            val number = binding.tvPhoneProfile.text.toString()
            showChangeNumberDialog(number)
        }


        dbFire.collection("user").document(uid ?: "").addSnapshotListener { value, error ->
            if (error != null) {
                //toast
                return@addSnapshotListener
            }

            if (value != null) {
                Log.e(TAG, "${value.data.toString()}")
                var userData = value.toObject(User::class.java)
                val name = userData?.name
                val phone = userData?.phone
                val image = userData?.image
                val languageCode = userData?.language
                val langName = getLanguageNameFromCode(languageCode)
                Log.e(TAG, "${nameOfUser}")
                binding.tvPhoneProfile.setText(phone)
                binding.tvNameProfile.setText(name)
                binding.tvCurrentLanguage.setText("Current Language: " + langName)
                Glide.with(requireContext())
                    .load(
                        image
                    )
                    .override(140, 140)
                    .into(binding.imgProfile)
            }

        }

    }

    private fun showChangeNameDialog(name: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.change_name)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams
        val edtName: EditText = dialog.findViewById(R.id.etChangeName)
        edtName.setText(name)
        val btnChangeName: Button = dialog.findViewById(R.id.btnChangeName)
        btnChangeName.setOnClickListener() {
            val newName = edtName.text.toString()
            if (newName.isNullOrEmpty()) {
                edtName.setError("Enter Name")
            } else {
                dbFire.collection("user").document(uid ?: "").update("name", newName)
                dialog.dismiss()
            }
        }
        dialog.show()


    }

    private fun showChangeNumberDialog(number: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.change_phone_number)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams
        val edtNumber: EditText = dialog.findViewById(R.id.etChangeNumber)
        val btnChangeNumber: Button = dialog.findViewById(R.id.btnChangePhone)
        edtNumber.setText(number)
        btnChangeNumber.setOnClickListener() {
            val newNumber = edtNumber.text.toString()
            if (newNumber.isNullOrEmpty()) {
                edtNumber.setError("Enter Name")
            } else {
                dbFire.collection("user").document(uid ?: "").update("phone", newNumber)
                dialog.dismiss()
            }
        }
        dialog.show()


    }

    private fun showLanguageDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.change_language_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams

        val languageNames = resources.getStringArray(R.array.language_names)
        val svSpinner: Spinner = dialog.findViewById(R.id.spinnerLanguages)
        val btnSelectLanguage: Button = dialog.findViewById(R.id.btnSelectLanguage)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languageNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        svSpinner.adapter = adapter

        svSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedLanguageName = parent.getItemAtPosition(position) as String
                binding.tvCurrentLanguage.setText("Current Language: " + selectedLanguageName)

                // Retrieve corresponding language code from arrays.xml
                val languageCodes = resources.getStringArray(R.array.language_codes)
                val selectedLanguageCode = languageCodes[position]
                langCode = selectedLanguageCode
                btnSelectLanguage.setOnClickListener() {
                    if (langCode.isNullOrEmpty()) {
                        Toast.makeText(requireContext(), "Select Language", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        dbFire.collection("user").document(uid ?: "").update("language", langCode)
                        dialog.dismiss()
                    }
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        btnSelectLanguage.setOnClickListener() {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getImageAndUpload() {
        pickImage.launch("image/*")

    }

    /*private fun fetchUserData() {
        dbFire.collection("user").document(mAuth.currentUser?.uid ?: "").get()
            .addOnSuccessListener {
                if (it != null) {
                    Log.e(TAG, "${it.data.toString()}")
                    var userData = it.toObject(User::class.java)
                    val name = userData?.name
                    val phone = userData?.phone
                    val image = userData?.image
                    val languageCode = userData?.language
                    val langName = getLanguageNameFromCode(languageCode)
                    Log.e(TAG, "${nameOfUser}")
                    binding.tvPhoneProfile.setText(phone)
                    binding.tvNameProfile.setText(name)
                    binding.tvCurrentLanguage.setText("Current Language: " + langName)
                    Glide.with(requireContext())
                        .load(
                            image
                        )
                        .override(140, 140)
                        .into(binding.imgProfile)


                }

            }.addOnFailureListener {

            }
    }
*/
    private fun getLanguageNameFromCode(languageCode: String?): String {
        return when (languageCode) {
            "af" -> "Afrikaans"
            "sq" -> "Albanian"
            "ar" -> "Arabic"
            "be" -> "Belarusian"
            "bg" -> "Bulgarian"
            "bn" -> "Bengali"
            "ca" -> "Catalan"
            "zh" -> "Chinese"
            "hr" -> "Croatian"
            "cs" -> "Czech"
            "da" -> "Danish"
            "nl" -> "Dutch"
            "en" -> "English"
            "eo" -> "Esperanto"
            "et" -> "Estonian"
            "fi" -> "Finnish"
            "fr" -> "French"
            "gl" -> "Galician"
            "ka" -> "Georgian"
            "de" -> "German"
            "el" -> "Greek"
            "gu" -> "Gujarati"
            "ht" -> "Haitian Creole"
            "he" -> "Hebrew"
            "hi" -> "Hindi"
            "hu" -> "Hungarian"
            "is" -> "Icelandic"
            "id" -> "Indonesian"
            "ga" -> "Irish"
            "it" -> "Italian"
            "ja" -> "Japanese"
            "kn" -> "Kannada"
            "ko" -> "Korean"
            "lt" -> "Lithuanian"
            "lv" -> "Latvian"
            "mk" -> "Macedonian"
            "mr" -> "Marathi"
            "ms" -> "Malay"
            "mt" -> "Maltese"
            "no" -> "Norwegian"
            "fa" -> "Persian"
            "pl" -> "Polish"
            "pt" -> "Portuguese"
            "ro" -> "Romanian"
            "ru" -> "Russian"
            "sk" -> "Slovak"
            "sl" -> "Slovenian"
            "es" -> "Spanish"
            "sv" -> "Swedish"
            "sw" -> "Swahili"
            "tl" -> "Tagalog"
            "ta" -> "Tamil"
            "te" -> "Telugu"
            "th" -> "Thai"
            "tr" -> "Turkish"
            "uk" -> "Ukrainian"
            "ur" -> "Urdu"
            "vi" -> "Vietnamese"
            "cy" -> "Welsh"
            else -> "" // Handle the case where the languageCode is not recognized
        }

    }


}