package com.harsh.chatm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity(){
    var firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({

            var intent = Intent(this@SplashActivity, MainActivity::class.java)
            if(firebaseAuth.currentUser != null) {
                intent = Intent(this@SplashActivity, ChatActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
    }
