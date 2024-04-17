package com.harsh.chatm

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

object Singleton {
    lateinit var options : TranslatorOptions
    lateinit var translation : Translator
    lateinit var conditions : DownloadConditions
    private val TAG = "Singleton"
    var sourceLang : String ?= null
    init {
        Log.e(TAG, " in init")
        updateLanguages()
    }

    fun updateLanguages(sourceLanguage: String = "en", targetLanguage: String = "en"){
        options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(targetLanguage)
            .build()
        translation = Translation.getClient(options)
        conditions = DownloadConditions.Builder().build()
    }

}