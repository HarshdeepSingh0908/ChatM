package com.harsh.chatm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.harsh.chatm.databinding.ActivityMainBinding
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.harsh.chatm.DataClasses.User

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.show()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var singleton = Singleton
        navController = supportFragmentManager.findFragmentById(R.id.navcontroller)!!.findNavController()
        setupActionBarWithNavController(navController)

        Firebase.messaging.subscribeToTopic("Test").addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this, "Subscribed to the topic", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(this, "Sorry! cannot subscribe to the topic", Toast.LENGTH_SHORT).show()
            }
        }


    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}