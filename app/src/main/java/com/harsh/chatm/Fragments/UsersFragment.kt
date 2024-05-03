package com.harsh.chatm.Fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.harsh.chatm.Adapter.AddNewUserAdapter
import com.harsh.chatm.Adapter.UserAdapter
import com.harsh.chatm.DataClasses.User
import com.harsh.chatm.Interfaces.OnUserClick
import com.harsh.chatm.Interfaces.OnMenuDotClick
import com.harsh.chatm.R

import com.harsh.chatm.databinding.FragmentUsersBinding
import java.util.Locale

class UsersFragment : Fragment(),OnUserClick, OnMenuDotClick {
    lateinit var binding : FragmentUsersBinding
    var dbFire = Firebase.firestore
    var mAuth = FirebaseAuth.getInstance()
    lateinit var  userList : ArrayList<User>
    lateinit var  tempUserList : ArrayList<User>

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
      //  binding.pbProgressBar.visibility = View.VISIBLE
        binding.loadingAnim.visibility = View.VISIBLE

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
           // setHomeButtonEnabled(false)
          //  setDisplayHomeAsUpEnabled(false)
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#2294C7")))
            show()
            title = "Start New Chat"
        }

        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        userList = arrayListOf()
        tempUserList = arrayListOf()
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
                    tempUserList.addAll(userList)

                    binding.rvUsers.adapter = AddNewUserAdapter(tempUserList,this,requireContext(),this)
                    binding.llMain.visibility = View.VISIBLE
                   // binding.pbProgressBar.visibility = View.GONE
                    binding.loadingAnim.visibility = View.GONE
                    binding.svSearchView.clearFocus()
                    binding.svSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                        override fun onQueryTextSubmit(query: String?): Boolean {
                           binding.svSearchView.clearFocus()
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            tempUserList.clear()
                            val searchText = newText!!.toLowerCase(Locale.getDefault())
                            if (searchText.isNotEmpty()) {
                                userList.forEach {
                                    if (it.name!!.toLowerCase(Locale.getDefault()).contains(searchText)) {
                                        tempUserList.add(it)
                                    }
                                }
                            } else {
                                tempUserList.addAll(userList)
                            }
                            binding.rvUsers.adapter?.notifyDataSetChanged()
                            return false
                        }

                    })

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
    fun toggleSearchViewVisibility() {
        binding.svSearchView.apply {
            if (visibility == View.VISIBLE) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
            }
        }
    }

    override fun onMenuDotClick(user: User, holder: View) {

    }


}