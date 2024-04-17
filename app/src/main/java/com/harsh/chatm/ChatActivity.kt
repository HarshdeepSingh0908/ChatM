package com.harsh.chatm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.harsh.chatm.DataClasses.User

class ChatActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    var user = User()
    var dbFire = Firebase.firestore
    var firebaseAuth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        navController = supportFragmentManager.findFragmentById(R.id.navcontroller2)!!.findNavController()
        setupActionBarWithNavController(navController)
        dbFire.collection("user").document(firebaseAuth?.currentUser?.uid?:"")
            .get()
            .addOnSuccessListener {
                user = it.toObject(User::class.java)?: User()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_action_logout -> {
               val builder = AlertDialog.Builder(this)
                builder.setTitle("Logout")
                builder.setMessage("Are you sure you want to Logout?")
                builder.setPositiveButton("Yes"){dialog,which->
                    firebaseAuth.signOut()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    dialog.dismiss()
                }
                builder.setNegativeButton("No"){dialog,which->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()

            }
            R.id.menu_action_profile ->
                {
                    val intent = Intent(this,ProfileActivity::class.java)
                    startActivity(intent)
                }


        }
        return super.onOptionsItemSelected(item)
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}