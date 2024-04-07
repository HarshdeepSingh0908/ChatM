package com.harsh.chatm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.harsh.chatm.databinding.ActivityTranslationBinding


class TranslationActivity : AppCompatActivity() {
    private  val TAG = "TranslationActivity"
    lateinit var binding : ActivityTranslationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnTranslate.setOnClickListener(){
            identifyLanguage()
            prepareTranslateModel()
        }
    }

    private fun identifyLanguage() :String{
        var code : String =""
        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage("Hello")
            .addOnSuccessListener { languageCode ->
                if (languageCode == "und") {
                    Log.e(TAG, "Can't identify language.")
                } else {
                    Log.e(TAG, "Language: $languageCode")
                    code = languageCode
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Could not Identify Language", Toast.LENGTH_SHORT).show()
            }
        return code
    }

    private fun prepareTranslateModel() {
    val options  = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.HINDI)
        .build()
            val englishHindiTranslator = Translation.getClient(options)
        var conditions = DownloadConditions.Builder()
            .build()

        englishHindiTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                Toast.makeText(this, "Downloaded Successfuly", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Download Failed", Toast.LENGTH_SHORT).show()
            }
        val originalText = binding.edtText.text.toString()
        englishHindiTranslator.translate(originalText)
            .addOnSuccessListener { translatedText ->
                binding.tvTranslatedText.text = translatedText
            }
            .addOnFailureListener { exception ->
                Log.e("Error",exception.toString())
            }
    }
}