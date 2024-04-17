package com.harsh.chatm.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.harsh.chatm.Adapter.MessageAdapter
import com.harsh.chatm.ChatActivity
import com.harsh.chatm.DataClasses.Message
import com.harsh.chatm.DataClasses.User
import com.harsh.chatm.Singleton
import com.harsh.chatm.databinding.FragmentMessageBinding

class MessageFragment : Fragment() {
    private val TAG = "MessageFragment"
    lateinit var binding: FragmentMessageBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    var recieverRoom: String? = null
    var senderRoom: String? = null
    private lateinit var mDbRef: FirebaseDatabase
    var senderUid :String ?= null
    //var language :String ?= null
    var dbFire = Firebase.firestore
    var otherUserLang: String? = null
    var otherUser = User()
    lateinit var mainActivity: ChatActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as ChatActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMessageBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(requireContext(), messageList)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            show()
        }
        mDbRef = FirebaseDatabase.getInstance()
        val username = arguments?.getString(("username"))
        val recieverUid = arguments?.getString(("uid"))
        senderUid = FirebaseAuth.getInstance().currentUser?.uid
        senderRoom = recieverUid + senderUid
        recieverRoom = senderUid + recieverUid

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = username
        }
        Log.e("username,uid:", "$username,$recieverUid")
        dbFire.collection("user")
            .document(recieverUid ?: "")
            .get()
            .addOnSuccessListener {
                otherUser = it.toObject(User::class.java) ?: User()
                otherUserLang = otherUser.language
                Singleton.updateLanguages(otherUserLang ?: "en", mainActivity.user.language ?: "en")
                fetchChat()
            }.addOnFailureListener {
                Log.e(TAG, "Error getting user language: $it")
            }



        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMessages.adapter = messageAdapter

         binding.rvMessages.scrollToPosition(messageAdapter.itemCount - 1)

        binding.btnSend.setOnClickListener() {
            val message = binding.edtMessage.text.toString()
            val messageObject = Message(message, senderUid.toString())
            //checking message language before sending
            var langCodeofMessage = identifyLanguage(message)
            Log.e("OTL",otherUserLang.toString())
            Log.e("CUL",mainActivity.user.language.toString())
            Log.e("langCodeofMessage",langCodeofMessage)
//Testing inside identify language function
//            if (langCodeofMessage == mainActivity.user.language){
//                mDbRef.getReference().child("chats").child(senderRoom!!).child("messages").push()
//                .setValue(messageObject).addOnSuccessListener {
//                    mDbRef.getReference().child("chats").child(recieverRoom!!).child("messages")
//                        .push()
//                        .setValue(messageObject)
//                }
//                binding.edtMessage.setText("")
//            }else{
//                Toast.makeText(requireContext(), "Language is not same", Toast.LENGTH_SHORT).show()
//            }


        }


    }

    private fun fetchChat() {
        var ref = mDbRef.getReference().child("chats").child(senderRoom!!)

        mDbRef.getReference().child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        messageList.clear()
                        if(snapshot.exists())
                            for (postsnapshot in snapshot.children) {
                                if (postsnapshot.exists()){
                                    val message = postsnapshot.getValue(Message::class.java)?:Message()
                                    Singleton.translation.downloadModelIfNeeded(Singleton.conditions)
                                        .addOnSuccessListener {
                                            Log.e(TAG, "Data downloaded Successfully")
                                            startTranslation(message)
                                        }
                                        .addOnFailureListener { exception ->
                                            val modelManager = RemoteModelManager.getInstance()
                                            val model =
                                                TranslateRemoteModel.Builder(mainActivity.user.language ?: "en")
                                                    .build()
                                            modelManager.deleteDownloadedModel(model)
                                                .addOnSuccessListener {
                                                    var conditions = DownloadConditions.Builder()
                                                        .build()
                                                    Singleton.translation.downloadModelIfNeeded(conditions)
                                                        .addOnSuccessListener {
                                                            startTranslation(message)
                                                        }
                                                }
                                                .addOnFailureListener {
                                                    // Error.
                                                }
                                            Log.e(TAG, "Download Failed,$exception")

                                        }
                                }
                                else{
                                    binding.pbProgressBar.visibility = View.GONE
                                }


                            }
                        messageAdapter.notifyDataSetChanged()
                    }
                    else binding.pbProgressBar.visibility = View.GONE

                }

                override fun onCancelled(error: DatabaseError) {
                    //
                }
            })
    }

    fun startTranslation(message: Message){
        Singleton.translation.translate(message?.message.toString())
            .addOnSuccessListener { translatedText ->
                Log.e("transkated text",translatedText)

                val translatedMessage =
                    Message(translatedText, message?.senderId.toString())?: Message()
                messageList.add(translatedMessage)
                messageAdapter.notifyDataSetChanged()
                binding.pbProgressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.e("Error", exception.toString())
            }
    }
 //Code to identify language
    private fun identifyLanguage(message: String): String {
        var code: String = ""
     val messageObject = Message(message, senderUid.toString())
        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(message)
            .addOnSuccessListener { languageCode ->
                if (languageCode == "und") {
                    Log.e(TAG, "Can't identify language.")
                } else {
                    Log.e(TAG, "Language IDENTIFIED: $languageCode")
                    code = languageCode
                    if (languageCode == mainActivity.user.language){
                        mDbRef.getReference().child("chats").child(senderRoom!!).child("messages").push()
                            .setValue(messageObject).addOnSuccessListener {
                                mDbRef.getReference().child("chats").child(recieverRoom!!).child("messages")
                                    .push()
                                    .setValue(messageObject)
                            }
                        binding.edtMessage.setText("")
                    }else{
                        Toast.makeText(requireContext(), "Language is not same", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Could not Identify Language", Toast.LENGTH_SHORT)
                    .show()
            }
        return code
    }


}