package com.harsh.chatm.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.harsh.chatm.Adapter.MessageAdapter
import com.harsh.chatm.DataClasses.Message
import com.harsh.chatm.databinding.FragmentMessageBinding


class MessageFragment : Fragment() {
    private  val TAG = "MessageFragment"
    lateinit var binding : FragmentMessageBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    var recieverRoom : String ?= null
    var senderRoom : String ?= null
    private lateinit var mDbRef : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(requireContext(),messageList)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            show()
        }

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
        mDbRef = FirebaseDatabase.getInstance()
        val username = arguments?.getString(("username"))
        val recieverUid = arguments?.getString(("uid"))
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        senderRoom = recieverUid + senderUid
        recieverRoom = senderUid + recieverUid

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = username
        }
        Log.e("username,uid:","$username,$recieverUid")

        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMessages.adapter = messageAdapter
       // binding.rvMessages.scrollToPosition(messageAdapter.itemCount - 1)
        fetchChat()
        binding.btnSend.setOnClickListener(){
            val message = binding.edtMessage.text.toString()
            val messageObject = Message(message, senderUid.toString())
                mDbRef.getReference().child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.getReference().child("chats").child(recieverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
            binding.edtMessage.setText("")

        }



    }

    private fun fetchChat() {
        mDbRef.getReference().child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postsnapshot in snapshot.children){
                        val message = postsnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


}