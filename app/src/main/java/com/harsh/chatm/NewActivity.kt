package com.harsh.chatm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.harsh.chatm.databinding.ActivityMainBinding
import com.harsh.chatm.databinding.ActivityNewBinding

class NewActivity : AppCompatActivity() {
    val binding: ActivityNewBinding by lazy {
        ActivityNewBinding.inflate(layoutInflater)
    }
    private val TAG = "NewActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intent?.let {
            var data = it.getStringExtra("data")
            Log.e(TAG, "data $data")
            when(data){
                "1"-> Toast.makeText(this, "Toast showed from notification", Toast.LENGTH_SHORT).show()
                "2"-> Snackbar.make(binding.cl, "Shown from notification", Snackbar.LENGTH_SHORT).show()
                else->{}
            }
        }
    }
}